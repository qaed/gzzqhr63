package nc.uap.ctrl.excel;

import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import nc.bs.ml.NCLangResOnserver;
import nc.uap.cpb.log.CpLogger;
import nc.uap.lfw.core.ContextResourceUtil;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.combodata.ComboData;
import nc.uap.lfw.core.comp.GridColumn;
import nc.uap.lfw.core.comp.GridColumnGroup;
import nc.uap.lfw.core.comp.GridComp;
import nc.uap.lfw.core.comp.IDataBinding;
import nc.uap.lfw.core.comp.IGridColumn;
import nc.uap.lfw.core.comp.WebComponent;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.exception.LfwRuntimeException;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.util.LanguageUtil;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CpExcelOpeWithSettingServiceImpl implements
		ICpExcelOpeWithSettingService {

	private static final String EXPORTFILES = "exportfiles";

	public CpExcelOpeWithSettingServiceImpl() {
	}

	public String exportExcelWithSetting(LfwView widget, String dsId,
			String compId, String fileName, ExcelCellSetting set) {
		XSSFWorkbook workBook = getWorkBook(widget, dsId, compId, set);
		FileOutputStream out = null;
		try {
			String path = ContextResourceUtil.getCurrentAppPath() + "/"
					+ "exportfiles";
			File dir = new File(path);
			if (!dir.exists())
				dir.mkdirs();
			if (fileName == null)
				fileName = UUID.randomUUID().toString();
			fileName = fileName + ".xlsx";
			out = new FileOutputStream(path + "/" + fileName);
			workBook.write(out);
			return "exportfiles/" + fileName;
		} catch (IOException e) {
			CpLogger.error(e);
			throw new LfwRuntimeException(NCLangResOnserver.getInstance()
					.getStrByID("imp", "CpExcelOpeServiceImpl-000003"));
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					CpLogger.error(e);
				}
			}
		}
	}

	public XSSFWorkbook getWorkBook(LfwView widget, String dsId, String compId,
			ExcelCellSetting set) {
		Dataset ds = widget.getViewModels().getDataset(dsId);
		if (ds == null) {
			throw new LfwRuntimeException(NCLangResOnserver.getInstance()
					.getStrByID("imp", "CpExcelOpeServiceImpl-000000") + dsId);
		}
		WebComponent comp = widget.getViewComponents().getComponent(compId);
		if (comp == null) {
			throw new LfwRuntimeException(NCLangResOnserver.getInstance()
					.getStrByID("imp", "CpExcelOpeServiceImpl-000001") + compId);
		}
		if (!(comp instanceof IDataBinding)) {
			throw new LfwRuntimeException(NCLangResOnserver.getInstance()
					.getStrByID("imp", "CpExcelOpeServiceImpl-000002"));
		}

		List<FieldDesc> list = getExportedFields(widget, ds, comp);
		XSSFWorkbook workBook = doExcelExportWithConfig(ds, list, set);
		return workBook;
	}

	private List<FieldDesc> getExportedFields(LfwView widget, Dataset ds,
			WebComponent comp) {
		GridComp grid = (GridComp) comp;
		List<FieldDesc> list = new ArrayList();
		List<IGridColumn> colList = grid.getColumnList();
		Iterator<IGridColumn> it = colList.iterator();

		while (it.hasNext()) {
			IGridColumn f = (IGridColumn) it.next();
			convertCol(f, list, ds, widget);
		}
		return list;
	}

	private void convertCol(IGridColumn column, List<FieldDesc> list,
			Dataset ds, LfwView widget) {
		if ((column instanceof GridColumn)) {
			GridColumn col = (GridColumn) column;
			if (!col.isVisible())
				return;
			FieldDesc field = new FieldDesc();
			String dsField = col.getField();
			if ((dsField == null) || (dsField.equals("")))
				return;
			String refComboId = col.getRefComboData();
			if ((refComboId != null) && (!refComboId.equals(""))) {
				ComboData comboData = widget.getViewModels().getComboData(
						refComboId);
				field.comboData = comboData;
			}
			field.id = dsField;
			String langDir = col.getLangDir();
			String i18nName = col.getI18nName();
			if (langDir == null)
				langDir = LfwRuntimeEnvironment.getLangDir();
			if ((i18nName == null) || (langDir == null)) {
				field.name = col.getText();
			} else {
				field.name = LanguageUtil.getWithDefaultByProductCode(langDir,
						col.getText(), i18nName);
			}

			list.add(field);
		} else {
			GridColumnGroup colg = (GridColumnGroup) column;
			List<IGridColumn> cList = colg.getChildColumnList();
			if (cList == null)
				return;
			Iterator<IGridColumn> it = cList.iterator();
			while (it.hasNext()) {
				convertCol((IGridColumn) it.next(), list, ds, widget);
			}
		}
	}

	private static XSSFWorkbook doExcelExportWithConfig(Dataset ds,
			List<FieldDesc> eleList, ExcelCellSetting set) {
		XSSFWorkbook workbook = null;
		workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("export");
		setSheetColumnWidth(eleList.size(), sheet);
		XSSFCellStyle titleStyle = createTitleStyle(workbook);
		//add by ljw
		XSSFCellStyle cellStype = createColorStyle(workbook,set.getColor());
		
		int index = -1;
		for (int i = 0; i < eleList.size(); i++) {
			if(((FieldDesc) eleList.get(i)).id.equals(set.getField())){
				index = i;
			}
		}
		//end
		XSSFRow row = sheet.createRow(0);
		for (int i = 0; i < eleList.size(); i++) {
			createCell(row, i, titleStyle, 1, ((FieldDesc) eleList.get(i)).name);
		}

		Row[] rows = ds.getCurrentRowData().getRows();
		for (int j = 0; j < rows.length; j++) {
			XSSFRow newRow = sheet.createRow(j + 1);
			for (int k = 0; k < eleList.size(); k++) {
				FieldDesc fieldDesc = (FieldDesc) eleList.get(k);
				String value = getShowValue(fieldDesc, rows[j], ds);
				//add by ljw 
				if(index != -1 && k == index){
					createCell(newRow, k, cellStype, 1, value);
					//end
				}else{
					createCell(newRow, k, null, 1, value);
				}
			}
		}
		return workbook;
	}

	private static String getShowValue(FieldDesc fieldDesc, Row row, Dataset ds) {
		Object rawValue = row.getValue(ds.nameToIndex(fieldDesc.id));
		String showValue = null;
		if (rawValue != null) {
			showValue = rawValue.toString();
		}
		if (fieldDesc.comboData != null) {
			showValue = fieldDesc.comboData.matchText(showValue);
		}
		return showValue;
	}

	private static void createCell(XSSFRow row, int column,
			XSSFCellStyle style, int cellType, Object value) {
		XSSFCell cell = row.createCell(column);
		if (value == null)
			value = "";
		if (style != null) {
			cell.setCellStyle(style);
		}
		switch (cellType) {
		case 3:
			break;
		case 1:
			cell.setCellValue(value.toString() + "");

			break;
		case 0:
			cell.setCellType(0);
			cell.setCellValue(Double.parseDouble(value.toString()));

			break;
		}

	}

	private static XSSFCellStyle createTitleStyle(XSSFWorkbook wb) {
		XSSFFont boldFont = wb.createFont();
		boldFont.setFontHeight((short) 200);
		XSSFCellStyle style = wb.createCellStyle();
		style.setFont(boldFont);
		return style;
	}
	
	/**
	 * add by ljw
	 * @param wb
	 * @param color
	 * @return
	 */
	private static XSSFCellStyle createColorStyle(XSSFWorkbook wb,short color){
		XSSFCellStyle style = wb.createCellStyle();
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setFillForegroundColor(color);
		return style;
	}

	private static void setSheetColumnWidth(int count, XSSFSheet sheet) {
		for (int i = 0; i < count; i++) {
			sheet.setColumnWidth(i, 4000);
		}
	}

	public CpExcelModel importExcel(InputStream input) {
		CpExcelModel model = new CpExcelModel();
		try {
			XSSFWorkbook wb = new XSSFWorkbook(input);
			XSSFSheet sheet = wb.getSheetAt(0);
			int rowCount = sheet.getLastRowNum() + 1;

			int colCountMax = 0;
			for (int j = 0; j < rowCount; j++) {
				XSSFRow row = sheet.getRow(j);
				if (null != row) {
					int colCount = row.getLastCellNum();
					if (colCountMax < colCount) {
						colCountMax = colCount;
					}
				}
			}
			for (int j = 0; j < rowCount; j++) {
				XSSFRow row = sheet.getRow(j);
				if (null != row) {
					for (int k = 0; k < colCountMax; k++) {
						ensureCols(model, colCountMax, j);
						CpExcelCol col = model.getCol(k);
						XSSFCell cell = row.getCell(k);
						if (null != cell) {
							switch (cell.getCellType()) {
							case 0:
								if (DateUtil.isCellDateFormatted(cell)) {
									col.addValue(cell.getDateCellValue(), j);
								} else
									col.addValue(Double.valueOf(cell
											.getNumericCellValue()), j);
								break;
							case 1:
								col.addValue(cell.getStringCellValue(), j);
								break;
							case 4:
								col.addValue(Boolean.valueOf(cell
										.getBooleanCellValue()), j);
								break;
							case 2:
								col.addValue(cell.getCellFormula(), j);
								break;
							case 3:
								col.addValue("", j);
								break;
							case 5:
								col.addValue("", j);
								break;
							default:
								col.addValue("", j);
								break;
							}

						} else {
							col.addValue(null, j);
						}
					}
				}
			}
		} catch (IOException e) {
			CpLogger.error(e);
			throw new LfwRuntimeException(NCLangResOnserver.getInstance()
					.getStrByID("imp", "CpExcelOpeServiceImpl-000004"));
		}
		return model;
	}

	private void ensureCols(CpExcelModel model, int colCount, int rowCount) {
		int count = model.getColCount();
		if (count < colCount) {
			for (int i = count; i < colCount; i++) {
				CpExcelCol col = new CpExcelCol();
				col.resize(rowCount);
				model.addCol(col);
			}
		}
	}

	private class FieldDesc {
		public ComboData comboData;
		protected String name;
		protected String id;

		private FieldDesc() {
		}
	}
	
}
