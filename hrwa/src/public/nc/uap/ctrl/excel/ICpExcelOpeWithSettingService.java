package nc.uap.ctrl.excel;

import nc.uap.lfw.core.page.LfwView;

public interface ICpExcelOpeWithSettingService{

	public abstract String exportExcelWithSetting(LfwView paramLfwView, String paramString1, String paramString2, String paramString3,ExcelCellSetting set);
}
