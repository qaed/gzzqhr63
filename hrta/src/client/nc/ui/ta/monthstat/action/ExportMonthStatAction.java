package nc.ui.ta.monthstat.action;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.ui.ta.monthstat.model.PsnMonthStatAppModel;
import nc.ui.ta.monthstat.view.PsnMonthStatPanel;
import nc.ui.ta.pub.ExportTBM;
import nc.ui.ta.pub.action.TAExportAction;
import nc.vo.pub.BusinessException;
import nc.vo.ta.monthstat.MonthStatVO;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

@SuppressWarnings("restriction")
public class ExportMonthStatAction extends TAExportAction {
	private static final long serialVersionUID = 1093320678814697776L;
	private PsnMonthStatPanel datapanel;

	public ExportMonthStatAction() {
	}

	public void doExport() throws Exception {
		MonthStatVO[] objs = (MonthStatVO[]) ((PsnMonthStatAppModel) getModel()).getData();
		int colnums = getDatapanel().getStatPanel().getHeadTable().getColumnCount();
		int rows = getDatapanel().getStatPanel().getHeadTable().getRowCount();
		List<String> colNameList = new ArrayList<String>();
		List<List<String>> dataValueList = new ArrayList<List<String>>();
		for (int i = 0; i < colnums; i++) {
			if (!ResHelper.getString("6017dayandmonthstat", "06017dayandmonthstat0036").equals(getDatapanel().getStatPanel().getHeadTable().getColumnName(i)) && !"ÉóÅú×´Ì¬".equals(getDatapanel().getStatPanel().getHeadTable().getColumnName(i))) {

				colNameList.add(getDatapanel().getStatPanel().getHeadTable().getColumnName(i));
				if (i == 4) {
					colNameList.add(ResHelper.getString("common", "UC000-0001802"));

					colNameList.add(ResHelper.getString("common", "UC000-0002560"));
				}
			}
		}

		for (int i = 0; i < rows; i++) {
			List<String> temp = new ArrayList<String>();
			for (int j = 0; j < colnums; j++)
				if (!ResHelper.getString("6017dayandmonthstat", "06017dayandmonthstat0036").equals(getDatapanel().getStatPanel().getHeadTable().getColumnName(j)) && !"ÉóÅú×´Ì¬".equals(getDatapanel().getStatPanel().getHeadTable().getColumnName(j))) {

					String v =
							getDatapanel().getStatPanel().getHeadTable().getValueAt(i, j) == null ? "" : getDatapanel().getStatPanel().getHeadTable().getValueAt(i, j).toString();

					if (ResHelper.getString("6017dayandmonthstat", "06017dayandmonthstat0037").equals(getDatapanel().getStatPanel().getHeadTable().getColumnName(j))) {
						if ("true".equalsIgnoreCase(v.toString())) {
							v = ResHelper.getString("6017dayandmonthstat", "06017dayandmonthstat0094");
						} else {
							v = ResHelper.getString("6017dayandmonthstat", "06017dayandmonthstat0095");
						}
					}

					temp.add(v);
					if (j == 4) {
						temp.add(objs[0].getTbmyear());
						temp.add(objs[0].getTbmmonth());
					}
				}
			dataValueList.add(temp);
		}
		exportToFile(this.strFileName, colNameList, dataValueList, true);
	}

	protected boolean isActionEnable() {
		return (StringUtils.isNotEmpty(getModel().getContext().getPk_org())) && (!ArrayUtils.isEmpty(((PsnMonthStatAppModel) getModel()).getData()));
	}

	public PsnMonthStatPanel getDatapanel() {
		return this.datapanel;
	}

	public void setDatapanel(PsnMonthStatPanel datapanel) {
		this.datapanel = datapanel;
	}

	protected void exportToFile(String file, List<String> cols, List<List<String>> dataList, boolean outputLine) throws BusinessException {
		int rownums = dataList.size();
		int colnums = cols.size();
		String[][] datas = new String[rownums][colnums];
		for (int i = 0; i < rownums; i++) {
			for (int j = 0; j < colnums; j++) {
				datas[i][j] = ((String) dataList.get(i).get(j));
			}
		}
		BufferedWriter out = null;
		try {
			if (file.toLowerCase().endsWith(".xls")) {
				new ExportTBM().exportDayMonthExcelFile(file, ResHelper.getString("6017dayandmonthstat", "06017dayandmonthstat0040"), cols.toArray(new String[0]), datas, outputLine);
			} else {
				File outfile = new File(file);
				if (outfile.exists())
					outfile.delete();
				outfile = new File(file);
				out = new BufferedWriter(new FileWriter(outfile, true));
				StringBuilder sb = new StringBuilder();
				if (outputLine)
					sb.append(ResHelper.getString("common", "UC000-0003389")).append(",");
				for (String colName : cols) {
					sb.append(colName + ",");
				}
				sb = sb.deleteCharAt(sb.length() - 1);
				out.write(sb.toString());
				out.newLine();
				for (int i = 0; i < datas.length; i++) {
					sb.delete(0, sb.length());
					if (outputLine)
						sb.append(i + 1 + ",");
					for (int j = 0; j < datas[i].length; j++) {
						sb.append(datas[i][j] + ",");
					}
					sb = sb.deleteCharAt(sb.length() - 1);
					out.write(sb.toString());
					out.newLine();
				}

				out.close();
				sb = null;
				outfile = null;
			}
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}
}