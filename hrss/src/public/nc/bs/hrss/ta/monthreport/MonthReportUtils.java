package nc.bs.hrss.ta.monthreport;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.ta.utils.TBMPsndocUtil;
import nc.bs.hrss.ta.utils.TaAppContextUtil;
import nc.itf.ta.IMonthStatQueryMaintain;
import nc.itf.ta.IViewOrderQueryService;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.comp.GridColumn;
import nc.uap.lfw.core.comp.GridComp;
import nc.uap.lfw.core.comp.IGridColumn;
import nc.uap.lfw.core.comp.LabelComp;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Field;
import nc.uap.lfw.core.exception.LfwRuntimeException;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.ta.monthstat.MonthStatVO;
import nc.vo.ta.timerule.TimeRuleVO;
import nc.vo.ta.vieworder.ViewOrderVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class MonthReportUtils {
	private static final String CLOUM_PREFIX = "col_";
	private static final int GRID_COLUMN_WIDTH = 100;

	public MonthReportUtils() {
	}

	public static MonthStatVO[] queryByPsnAndNatualYearMonth(String pk_psndoc, String beginYear, String beginMonth, String endYear, String endMonth) {
		MonthStatVO[] monthStatVOs = null;
		try {
			IMonthStatQueryMaintain service = (IMonthStatQueryMaintain) ServiceLocator.lookup(IMonthStatQueryMaintain.class);
			monthStatVOs = service.queryByPsnAndNatualYearMonth(pk_psndoc, beginYear, beginMonth, endYear, endMonth);
		} catch (BusinessException e) {
			new HrssException(e).deal();
		} catch (HrssException e) {
			e.alert();
		}

		return monthStatVOs;
	}

	public static ViewOrderVO[] queryViewOrder(String pk_org, int fun_type, int report_type, boolean containsDisable) {
		ViewOrderVO[] viewOrderVOs = null;
		try {
			IViewOrderQueryService viewOrder = (IViewOrderQueryService) ServiceLocator.lookup(IViewOrderQueryService.class);
			viewOrderVOs = viewOrder.queryViewOrder(pk_org, fun_type, report_type, true);
		} catch (BusinessException e) {
			new HrssException(e).deal();
		} catch (HrssException e) {
			e.alert();
		}
		return viewOrderVOs;
	}

	public static void removeCol(LfwView widget) {
		GridComp grid = (GridComp) widget.getViewComponents().getComponent("gridMonthReport");
		List<IGridColumn> groupList = new ArrayList();
		IGridColumn col = null;

		String colStr = null;
		for (Iterator<IGridColumn> it = grid.getColumnList().iterator(); it.hasNext();) {
			col = (GridColumn) it.next();
			colStr = col.getId();
			if ((StringUtils.isEmpty(colStr)) || (colStr.startsWith("col_"))) {
				groupList.add(col);
			}
		}
		grid.removeColumns(groupList, true);

		Dataset ds = widget.getViewModels().getDataset("dsMonthReport");

		Field[] fields = ds.getFieldSet().getFields();
		String filedId = null;
		for (Field field : fields) {
			filedId = field.getId();
			if ((StringUtils.isEmpty(filedId)) || (filedId.startsWith("col_"))) {
				ds.getFieldSet().removeField(field);
			}
		}
	}

	public static void buildDsAndGrid(LfwView widget, String pk_org) throws LfwRuntimeException {
		UFBoolean dateChangeFlag = (UFBoolean) getApplicationContext().getAppAttribute("isDateChange");

		getApplicationContext().addAppAttribute("isDateChange", UFBoolean.FALSE);
		if ((dateChangeFlag != null) && (!dateChangeFlag.booleanValue())) {
			return;
		}

		removeCol(widget);

		GridComp grid = (GridComp) widget.getViewComponents().getComponent("gridMonthReport");

		Dataset ds = widget.getViewModels().getDataset("dsMonthReport");

		ViewOrderVO[] viewOrderVOs = queryViewOrder(pk_org, 1, 0, true);

		if ((viewOrderVOs == null) || (viewOrderVOs.length == 0)) {
			return;
		}

		needapprove(pk_org, grid);
		TimeRuleVO timeRuleVO = TaAppContextUtil.getTimeRuleVO();
		Integer timedecimal = timeRuleVO.getTimedecimal();

		for (ViewOrderVO vo : viewOrderVOs) {
			Field field = buildField(vo);

			if (field.getDataType().equals("Decimal")) {

				field.setPrecision(String.valueOf(timedecimal));
			}

			ds.getFieldSet().addField(field);
			GridColumn column = buildColumn(field);

			column.setSumCol(isSumCol(field.getDataType()));
			column.setShowCheckBox(false);
			grid.addColumn(column, true);
		}
		grid.setShowSumRow(true);
	}

	public static void buildDetailGrid(String beginYear, String beginMonth, String endYear, String endMonth) {
		String pk_psndoc = SessionUtil.getPk_psndoc();
		MonthStatVO[] monthStatVOs = queryByPsnAndNatualYearMonth(pk_psndoc, beginYear, beginMonth, endYear, endMonth);

		if (!ArrayUtils.isEmpty(monthStatVOs)) {

			StringBuffer monthReportJson = new StringBuffer("");

			String title = "";
			String itemName = "";
			String itemValue = "";
			String pk_hrorg = "";
			DecimalFormat frmt = null;

			for (int i = 0; i < monthStatVOs.length; i++) {
				pk_hrorg = monthStatVOs[i].getPk_org();

				ViewOrderVO[] viewOrderVOs = queryViewOrder(pk_hrorg, 1, 0, true);

				TimeRuleVO timeRuleVO = TBMPsndocUtil.getTimeRuleVOByOrg(pk_hrorg);

				Integer timedecimal = timeRuleVO.getTimedecimal();
				String strDcmlFrmt = "#0";
				for (int j = 0; j < timedecimal.intValue(); j++) {
					if (strDcmlFrmt == "#0") {
						strDcmlFrmt = strDcmlFrmt + ".";
					}
					strDcmlFrmt = strDcmlFrmt + "0";
				}
				DecimalFormat dcmlFrmt = new DecimalFormat(strDcmlFrmt);

				StringBuffer titleJson = new StringBuffer("");
				StringBuffer itemsJson = new StringBuffer("");

				title =
						NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0097") + monthStatVOs[i].getTbmyear() + "-" + monthStatVOs[i].getTbmmonth() + " " + monthStatVOs[i].getOrgName() + " " + monthStatVOs[i].getDeptName();

				titleJson.append(title);

				itemsJson.append(getItemJsonString(0, NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0001403"), monthStatVOs[i].getPsnName()));

				if (!ArrayUtils.isEmpty(viewOrderVOs)) {
					for (int j = 0; j < viewOrderVOs.length; j++) {
						itemName = getDefaultshowname(viewOrderVOs[j]);
						frmt = dcmlFrmt;

						Object value = monthStatVOs[i].getAttributeValue(viewOrderVOs[j].getField_name());

						if ((value instanceof BigDecimal)) {
							itemValue = frmt.format(value);
						} else if ((value instanceof UFDouble)) {
							itemValue = frmt.format(((UFDouble) value).toBigDecimal());
						} else if ((value instanceof UFBoolean)) {
							UFBoolean booleanValue = (UFBoolean) value;
							if (booleanValue.booleanValue()) {
								itemValue = NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0027");
							} else {
								itemValue = NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0028");
							}
						} else {
							itemValue = String.valueOf(value);
						}
						if ((viewOrderVOs[j].getTimeitem_type() != null) && ((viewOrderVOs[j].getTimeitem_type().intValue() == 0) || (viewOrderVOs[j].getTimeitem_type().intValue() == 1))) {

							itemName =
									itemName + "," + viewOrderVOs[j].getPk_timeitem() + "-" + viewOrderVOs[j].getTimeitem_type() + "," + monthStatVOs[i].getPk_org() + "," + monthStatVOs[i].getTbmyear() + "-" + monthStatVOs[i].getTbmmonth();
						}

						itemsJson.append(getItemJsonString(j + 1, itemName, itemValue));
					}
				}
				monthReportJson.append(getMthReportJsonString(i, titleJson.toString(), itemsJson.toString()));
			}

			String direction = "H_8";
			getApplicationContext().addExecScript("layoutSlip('" + monthReportJson.toString() + "','" + direction + "','" + "Emp" + "');");
		} else {
			LfwView widget = LfwRuntimeEnvironment.getWebContext().getPageMeta().getView("main");
			LabelComp lblContent = (LabelComp) widget.getViewComponents().getComponent("lbl_content");
			lblContent.setVisible(false);
		}
	}

	private static String getItemJsonString(int index, String name, String value) {
		StringBuffer itemStr = new StringBuffer("");
		if (index > 0) {
			itemStr.append(",");
		}
		itemStr.append("item" + index + ": {");
		itemStr.append("name:\"" + name + "\"");
		itemStr.append(",");
		itemStr.append("value:\"" + value + "\"");
		itemStr.append("}");
		return itemStr.toString();
	}

	private static String getMthReportJsonString(int index, String context, String detail) {
		StringBuffer lipJson = new StringBuffer("");
		if (index > 0) {
			lipJson.append(",");
		}
		lipJson.append("{");
		lipJson.append("tital:\"" + context + "\"");
		lipJson.append(", detail:{" + detail + "}");
		lipJson.append("}");
		return lipJson.toString();
	}

	public static void resetData(Dataset ds, String pk_dept, FromWhereSQL fromWhereSQL, String year, String month, boolean containsSubDepts, boolean showNoDataRecord) {
		MonthStatVO[] monthStatVOs = null;

		IMonthStatQueryMaintain service = (IMonthStatQueryMaintain) NCLocator.getInstance().lookup(IMonthStatQueryMaintain.class);
		try {
			monthStatVOs = service.queryByConditionAndDept(pk_dept, containsSubDepts, fromWhereSQL, year, month, showNoDataRecord);
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		SuperVO[] vos = DatasetUtil.paginationMethod(ds, monthStatVOs);
		new SuperVO2DatasetSerializer().serialize(vos, ds, 0);
	}

	public static Field buildField(ViewOrderVO vo) {
		Field field = new Field();

		field.setId("col_" + vo.getField_name());

		field.setText(getDefaultshowname(vo));

		field.setDataType(translatorDataType(vo.getData_type().intValue()));

		field.setField(vo.getField_name());
		return field;
	}

	private static String getDefaultshowname(ViewOrderVO viewOrderVO) {
		String name = viewOrderVO.getMultilangName();
		if (name == null) {
			name = viewOrderVO.getName();
		}
		if (viewOrderVO.getItem_type().intValue() == 1) {
			name =
					name + "(" + (viewOrderVO.getUnit().intValue() == 1 ? NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0078") : NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0079")) + ")";
		}

		return name;
	}

	private static String translatorDataType(int dataType) {
		switch (dataType) {
			case 0:
				return "Integer";
			case 1:
				return "Decimal";
			case 2:
				return "String";
			case 3:
				return "UFBoolean";
			case 4:
				return "Date";
		}
		return "String";
	}

	private static GridColumn buildColumn(Field field) {
		GridColumn column = new GridColumn();

		column.setId(field.getId());

		column.setField(field.getId());

		column.setWidth(100);

		column.setText(field.getText());

		column.setEditorType(getEditType(field.getDataType()));

		column.setRenderType(getRendarType(field.getDataType()));

		column.setDataType(field.getDataType());

		column.setSumCol(isSumCol(field.getDataType()));
		return column;
	}

	public static String getEditType(String dataType) {
		if ("int".equals(dataType))
			return "IntegerText";
		if ("Decimal".equals(dataType))
			return "DecimalText";
		if ("String".equals(dataType))
			return "StringText";
		if ("UFBoolean".equals(dataType))
			return "CheckBox";
		if ("Date".equals(dataType)) {
			return "DateText";
		}
		return "StringText";
	}

	public static String getRendarType(String dataType) {
		if ("int".equals(dataType))
			return "IntegerRender";
		if ("Decimal".equals(dataType))
			return "DecimalRender";
		if ("String".equals(dataType))
			return "DefaultRender";
		if ("UFBoolean".equals(dataType))
			return "BooleanRender";
		if ("Date".equals(dataType)) {
			return "DateRender";
		}
		return "DefaultRender";
	}

	private static boolean isSumCol(String editorType) {
		if (("Integer".equals(editorType)) || ("Decimal".equals(editorType)) || ("Double".equals(editorType)) || ("Float".equals(editorType)) || ("Long".equals(editorType)) || ("UFDouble".equals(editorType))) {

			return true;
		}
		return false;
	}

	private static void needapprove(String pk_org, GridComp grid) {
		TimeRuleVO timeRuleVO = TBMPsndocUtil.getTimeRuleVOByOrg(pk_org);
		if ((timeRuleVO == null) || (timeRuleVO.getMreportapproveflag() == null) || (!timeRuleVO.getMreportapproveflag().booleanValue())) {
			GridColumn col = (GridColumn) grid.getColumnById("isapprove");
			if (col != null) {
				col.setVisible(false);
			}
			GridColumn approvestatuscol = (GridColumn) grid.getColumnById("approvestatus");
			if (approvestatuscol != null) {
				approvestatuscol.setVisible(false);
			}
		}
	}

	public static ApplicationContext getApplicationContext() {
		return AppLifeCycleContext.current().getApplicationContext();
	}
}