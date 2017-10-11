package com.yonyou.portal.hrss.wabasch;

import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.uap.lfw.core.bm.ButtonStateManager;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.comp.MenubarComp;
import nc.uap.lfw.core.cmd.UifAddCmd;
import nc.uap.lfw.core.cmd.UifSaveCmdRV;
import nc.uap.lfw.core.cmd.UifUpdateUIDataCmdRV;
import nc.uap.lfw.core.comp.text.TextComp;
import nc.uap.wfm.utils.WfmPublicViewUtil;
import nc.uap.lfw.core.event.ScriptEvent;
import nc.uap.lfw.core.cmd.UifDatasetAfterSelectCmd;
import nc.bs.dao.DAOException;
import nc.uap.lfw.core.cmd.UifLineDelCmd;
import nc.uap.lfw.core.ctx.OpenProperties;
import nc.uap.lfw.core.ContextResourceUtil;
import nc.uap.lfw.core.comp.WebElement;
import nc.md.model.MetaDataException;
import nc.uap.ctrl.excel.UifExcelExportCmd;
import nc.uap.lfw.core.cmd.UifDatasetLoadCmd;
import nc.uap.ctrl.excel.CpExcelModel;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.file.LfwFileConstants;
import nc.uap.lfw.core.comp.LabelComp;

import java.util.List;

import nc.uap.lfw.core.comp.ReferenceComp;
import nc.uap.lfw.core.log.LfwLogger;
import nc.uap.lfw.core.file.FillFileInfoHelper;
import nc.uap.lfw.core.comp.GridComp;
import nc.uap.lfw.core.exception.LfwRuntimeException;
import nc.uap.lfw.core.uimodel.ViewConfig;
import nc.vo.pub.lang.UFDate;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.lang.StringUtils;

import nc.uap.wfm.exe.WfmCmd;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;
import nc.uap.wfm.pubview.ExecuteTaskWidgetProvider;
import nc.uap.lfw.core.uif.delegator.DefaultDataValidator;
import nc.uap.lfw.core.cmd.CmdInvoker;

import java.util.Map;

import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.vo.wa.wa_ba.sch.WaBaSchTVO;
import nc.uap.lfw.core.cmd.UifCopyCmd;
import nc.uap.ctrl.excel.ICpExcelOpeService;
import nc.bs.dao.BaseDAO;

import java.io.IOException;
import java.util.HashMap;

import nc.uap.lfw.core.page.LfwView;

import com.yonyou.portal.hrss.wabasch.wfm.WfmFlwFormVO;

import nc.uap.lfw.core.cmd.UifPlugoutCmd;
import uap.lfw.core.itf.ctrl.AbstractMasterSlaveViewController;
import nc.uap.wfm.utils.WfmCPUtilFacade;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import nc.uap.lfw.core.data.MdDataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.exception.LfwBusinessException;

import java.io.FileInputStream;
import java.util.ArrayList;

import nc.uap.ctrl.tpl.print.init.DefaultPrintService;
import nc.vo.wa.wa_ba.sch.WaBaSchHVO;
import nc.uap.lfw.core.cmd.base.CommandStatus;
import nc.bs.framework.common.NCLocator;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import nc.uap.lfw.core.cmd.UifDelCmdRV;
import nc.uap.wfm.utils.WfmUtilFacade;
import nc.vo.pub.SuperVO;
import nc.uap.ctrl.excel.UifExcelImportCmd;
import nc.uap.lfw.core.AppInteractionUtil;
import nc.uap.wfm.utils.WfmProDefUtil;
import nc.uap.ctrl.tpl.print.ICpPrintTemplateService;
import nc.uap.lfw.core.data.Field;
import nc.vo.pub.BusinessException;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.uap.lfw.core.event.DialogEvent;
import nc.uap.lfw.core.comp.ButtonComp;
import nc.uap.wfm.utils.WfmTaskUtil;
import nc.uap.wfm.constant.WfmConstants;
import nc.uap.lfw.core.constants.AppConsts;

import org.apache.poi.xssf.usermodel.XSSFRow;

import uap.web.bd.pub.AppUtil;
import nc.uap.lfw.core.event.DatasetEvent;
import uap.lfw.core.locator.ServiceLocator;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.wfm.vo.WfmFormInfoCtx;

/**
 * 卡片窗口默认逻辑
 */
public class WabaschCardWinMainViewCtrl<T extends WebElement> extends AbstractMasterSlaveViewController {
	private static final String PLUGOUT_ID = "afterSavePlugout";
	public static final String OPEN_BILL_ID = "openBillId";

	/**
	 * 页面显示事件
	 * 
	 * @param dialogEvent
	 */
	public void beforeShow(DialogEvent dialogEvent) {

		Dataset masterDs = this.getMasterDs();
		masterDs.clear();
		String oper = this.getOperator();

		GridComp comp = (GridComp) this.getCurrentView().getViewComponents().getComponent("WaBaSchTVO_grid");
		List<MenuItem> list = new ArrayList<MenuItem>();

		//		comp.getMenuBar().getItem("WaBaSchTVO_grid$HeaderBtn_Edit").setEnabled(false);

		list = comp.getMenuBar().getMenuList();
		for (int i = 0; i < list.size(); i++) {
			MenuItem item = list.get(i);

			item.setEnabled(false);
		}
		comp.getMenuBar().getItem("WaBaSchTVO_grid$HeaderBtn_Edit").setEnabled(true);

		MenubarComp menucomp = this.getCurrentView().getViewMenus().getMenuBar("menubar");
		menucomp.getItem("approve").setEnabled(false);

		uap.web.bd.pub.AppUtil.addAppAttr(LfwFileConstants.SYSID, LfwFileConstants.SYSID_BAFILE);

		if (AppConsts.OPE_ADD.equals(oper)) {
			CmdInvoker.invoke(new UifAddCmd(this.getMasterDsId()) {
				@Override
				protected void onBeforeRowAdd(Row row) {
					setAutoFillValue(row);

					Dataset masterDs = getMasterDs();
					masterDs.setEnabled(false);
					String pk_primarykey = generatePk();
					row.setValue(masterDs.nameToIndex(masterDs.getPrimaryKeyField()), pk_primarykey);
					row.setValue(masterDs.nameToIndex("attach"), pk_primarykey);
					FillFileInfoHelper.resetItem(pk_primarykey);
					FillFileInfoHelper.fillFileInfo(masterDs, row);
				}
			});
		} else if (AppConsts.OPE_EDIT.equals(oper)) {
			String currentValue = LfwRuntimeEnvironment.getWebContext().getWebSession().getOriginalParameter("openBillId");
			if (currentValue == null) {
				String value = (String) LfwRuntimeEnvironment.getWebContext().getWebSession().getOriginalParameter("${forminspk}");
				LfwRuntimeEnvironment.getWebContext().getWebSession().addOriginalParameter("openBillId", value);
			}
			CmdInvoker.invoke(new UifDatasetLoadCmd(masterDs) {
				@Override
				protected void onAfterDatasetLoad() {
					setDSEnabledByTask(this.getDs());

					String primaryKey = this.getDs().getPrimaryKeyField();
					this.getDs().setEnabled(false);
					if (primaryKey == null) {
						throw new LfwRuntimeException("当前Dataset没有设置主键!");
					}
					String primaryKeyValue = (String) this.getDs().getSelectedRow().getValue(this.getDs().nameToIndex(primaryKey));
					FillFileInfoHelper.resetItem(primaryKeyValue);
				}
			});
		}
	}

	/**
	 * 获取任务PK
	 * 
	 * @return String
	 */
	private String getPkTask() {
		String pk = LfwRuntimeEnvironment.getWebContext().getOriginalParameter(WfmConstants.WfmUrlConst_TaskPk);
		if (pk == null) {
			pk = (String) this.getCurrentAppCtx().getAppAttribute(WfmConstants.WfmUrlConst_TaskPk);
		}
		return pk;
	}

	/**
	 * 根据流程任务设置数据集使用状态
	 * 
	 * @param ds
	 */
	private void setDSEnabledByTask(Dataset ds) {
		if (ds != null) {
			Object task = WfmTaskUtil.getTaskFromSessionCache(this.getPkTask());
			if (task != null) {
				if (WfmTaskUtil.isEndState(task) || WfmTaskUtil.isFinishState(task) || WfmTaskUtil.isSuspendedState(task) || WfmTaskUtil.isCanceledState(task)) {
					ds.setEnabled(false);
				} else {
					ds.setEnabled(true);
				}
			} else {
				ds.setEnabled(true);
			}
		}
	}

	/**
	 * 设置PK_ORG字段
	 * 
	 * @param row
	 */
	private void setAutoFillValue(Row row) {
		if (row != null) {
			Dataset ds = this.getCurrentView().getViewModels().getDataset(this.getMasterDsId());

			String pkOrg = this.getCurrentAppCtx().getAppEnvironment().getPk_org();
			if (pkOrg != null) {
				int pkOrgIndex = ds.nameToIndex("pk_org");
				if (pkOrgIndex >= 0) {
					row.setValue(pkOrgIndex, pkOrg);
				}
			}
			String pkGroup = this.getCurrentAppCtx().getAppEnvironment().getPk_group();
			if (pkGroup != null) {
				int pkGroupIndex = ds.nameToIndex(PK_GROUP);
				if (pkGroupIndex >= 0) {
					row.setValue(pkGroupIndex, pkGroup);
				}
			}
		}
	}

	/**
	 * 主数据选中后
	 * 
	 * @param datasetEvent
	 */
	public void onAfterRowSelect(DatasetEvent dsEvent) {
		Dataset ds = dsEvent.getSource();
		/*
		CmdInvoker.invoke(new UifDatasetAfterSelectCmd(ds.getId()) {
			protected void updateButtons() {

			}
		});
		*/
		CmdInvoker.invoke(new WabaschUifDatasetAfterSelectCmd(ds.getId()));
	}

	/**
	 * 新增
	 */
	public void onAdd(MouseEvent<?> mouseEvent) throws BusinessException {
		this.getCurrentAppCtx().addAppAttribute(WfmConstants.WfmAppAttr_BillID, null);
		this.resetWfmParameter();
		this.getCurrentAppCtx().addAppAttribute(WfmConstants.WfmAppAttr_FolwTypePk, this.getFlwTypePk());

		CmdInvoker.invoke(new UifAddCmd(this.getMasterDsId()) {
			protected void onBeforeRowAdd(Row row) {
				setAutoFillValue(row);
			}
		});
	}

	/**
	 * 打印
	 * 
	 * @param mouseEvent
	 * @throws BusinessException
	 */
	public void onPrint(MouseEvent<?> mouseEvent) throws BusinessException {
		Dataset masterDs = this.getMasterDs();
		Row row = masterDs.getSelectedRow();
		if (row == null) {
			throw new LfwRuntimeException("请选中数据！");
		}
		try {
			List<Dataset> list = new ArrayList<Dataset>(1);
			list.add(masterDs);
			DefaultPrintService printService = new DefaultPrintService();
			printService.setDatasetList(list);
			ICpPrintTemplateService service = ServiceLocator.getService(ICpPrintTemplateService.class);
			service.print(printService, null, this.getNodeCode());
		} catch (Exception e) {
			LfwLogger.error(e);
			throw new LfwRuntimeException(e.getMessage());
		}
	}

	private String getNodeCode() {
		return "配置了打印模板的功能节点的nodecode";
	}

	private String getFlwTypePk() {
		return "0001ZZ10000000009QBD";
	}

	private void resetWfmParameter() {
		this.getCurrentAppCtx().addAppAttribute(WfmConstants.WfmAppAttr_TaskPk, null);
		this.getCurrentAppCtx().addAppAttribute(WfmConstants.WfmAppAttr_ScratchPad, null);
		this.getCurrentAppCtx().addAppAttribute(WfmConstants.BILLSTATE, null);
		this.getCurrentAppCtx().addAppAttribute(WfmConstants.AttachFileList_Temp_Billitem, null);
		this.getCurrentAppCtx().addAppAttribute(WfmConstants.WfmAppAttr_ProInsPk, null);
		this.getCurrentAppCtx().addAppAttribute(WfmConstants.RETURN_PK_TASK, null);
		this.getCurrentAppCtx().addAppAttribute(WfmConstants.WfmAppAttr_FormInFoCtx_Billitem, null);
	}

	/**
	 * 删除
	 */
	public void onDelete(MouseEvent<?> mouseEvent) throws BusinessException {
		String pk_form = LfwRuntimeEnvironment.getWebContext().getWebSession().getOriginalParameter("openBillId");
		if (pk_form == null) {
			pk_form = (String) LfwRuntimeEnvironment.getWebContext().getWebSession().getOriginalParameter("${forminspk}");
			LfwRuntimeEnvironment.getWebContext().getWebSession().addOriginalParameter("openBillId", pk_form);
		}
		if (pk_form != null && !pk_form.equals("")) {
			boolean isCanDel = WfmCPUtilFacade.isCanDelBill(pk_form);
			if (isCanDel) {
				WfmCPUtilFacade.delWfmInfo(pk_form);
				CmdInvoker.invoke(new UifDelCmdRV(this.getMasterDsId()));
			} else {
				throw new LfwRuntimeException("流程已启动，无法删除单据");
			}
		} else {
			throw new LfwRuntimeException("未获取到流程单据主键");
		}
	}

	/**
	 * 返回
	 */
	public void onBack(MouseEvent<?> mouseEvent) throws BusinessException {
		this.getCurrentAppCtx().closeWinDialog();
	}

	/**
	 * 复制
	 */
	public void onCopy(MouseEvent<?> mouseEvent) throws BusinessException {
		this.getCurrentAppCtx().addAppAttribute(WfmConstants.WfmAppAttr_BillID, null);
		this.resetWfmParameter();
		CmdInvoker.invoke(new UifCopyCmd(this.getMasterDsId()));
	}

	public void doTaskExecute(Map keys) {
		// 平台默认校验
		new DefaultDataValidator().validate(this.getMasterDs(), this.getCurrentView());

		WfmFormInfoCtx formCtx = this.getWfmFormInfoCtx();

		Dataset ds = getMasterDs();

		BaseDAO dao = new BaseDAO();
		WaBaSchHVO vo = (WaBaSchHVO) formCtx;

		int row = 0;
		try {
			row = dao.updateVO(vo);
		} catch (DAOException e) {
			// TODO 自动生成
		}

		// 设置流程form
		this.getCurrentAppCtx().addAppAttribute(WfmConstants.WfmAppAttr_FormInFoCtx, formCtx);
		// 设置流程类型pk
		this.getCurrentAppCtx().addAppAttribute(WfmConstants.WfmAppAttr_FolwTypePk, this.getFlwTypePk());
		// 设置任务pk
		this.getCurrentAppCtx().addAppAttribute(WfmConstants.WfmAppAttr_TaskPk, this.getPkTask());
		// 调用流程
		CmdInvoker.invoke(new WfmCmd());
		if (CommandStatus.SUCCESS.equals(CommandStatus.getCommandStatus())) {
			CmdInvoker.invoke(new UifUpdateUIDataCmdRV((SuperVO) formCtx, getMasterDsId()));
			this.getCurrentAppCtx().closeWinDialog();
		}
	}

	/**
	 * 子表新增
	 */
	public void onGridAddClick(MouseEvent<?> mouseEvent) {
		GridComp grid = (GridComp) mouseEvent.getSource();
		String dsId = grid.getDataset();
		Dataset ds = this.getCurrentView().getViewModels().getDataset(dsId);
		Row emptyRow = ds.getEmptyRow();
		ds.addRow(emptyRow);
		ds.setRowSelectIndex(ds.getRowIndex(emptyRow));
		ds.setEnabled(true);
	}

	/**
	 * 子表编辑
	 */
	public void onGridEditClick(MouseEvent<?> mouseEvent) {
		GridComp grid = (GridComp) mouseEvent.getSource();
		String dsId = grid.getDataset();
		Dataset ds = this.getCurrentView().getViewModels().getDataset(dsId);
		ds.setEnabled(true);
	}

	/**
	 * 子表删除
	 */
	public void onGridDeleteClick(MouseEvent<?> mouseEvent) {
		GridComp grid = (GridComp) mouseEvent.getSource();
		String dsId = grid.getDataset();
		CmdInvoker.invoke(new UifLineDelCmd(dsId));
	}

	@Override
	protected String getMasterDsId() {
		return "WaBaSchHVO";
	}

	protected WfmFormInfoCtx getWfmFormInfoCtx() {
		Dataset masterDs = this.getMasterDs();
		Dataset[] detailDss = this.getDetailDs(this.getDetailDsIds());
		SuperVO richVO = this.getDs2RichVOSerializer().serialize(masterDs, detailDss, this.getRichVoClazz());
		return (WfmFormInfoCtx) richVO;
	}

	protected String getRichVoClazz() {
		return WfmFlwFormVO.class.getName();
	}

	/**
	 * 导出模板
	 * 
	 * @param mouseEvent
	 */
	public void onexport(MouseEvent mouseEvent) {
		String dsid = "WaBaSchTVO";
		String compid = "WaBaSchTVO_grid";
		UifExcelExportCmd cmd = new UifExcelExportCmd(dsid, compid, "1.xls");
		cmd.execute();
	}

	/**
	 * 导入
	 * 
	 * @param mouseEvent
	 */
	public void onimport(MouseEvent mouseEvent) {
		if (getMasterDs() == null) {
			AppInteractionUtil.showMessageDialog("当前数据为空");
		}

		System.out.println("aaaaaa");
		UifExcelImportCmd cmd = new UifExcelImportCmd("com.yonyou.portal.hrss.wabasch.WabaschCardWinMainViewCtrl", PLUGOUT_ID);
		cmd.execute();
	}

	public void onUploadedExcelFile(ScriptEvent e) throws FileNotFoundException, IOException, LfwBusinessException {
		boolean flag = AppInteractionUtil.showConfirmDialog("确定导入?", "确定导入?");

		AppLifeCycleContext ctx = AppLifeCycleContext.current();
		String relativePath = ctx.getParameter("excel_imp_path");
		String appPath = ContextResourceUtil.getCurrentAppPath();
		String fullPath = appPath + "/" + relativePath;

		if (!flag) {
			deletefile(fullPath);
			return;
		}

		// 解析文件
		doImport(fullPath);

		// 增加导入成功提示信息
		AppInteractionUtil.showMessageDialog("导入成功?", true);
	}

	private void doImport(String fullPath) throws FileNotFoundException, IOException, LfwBusinessException {
		String id = "WaBaSchTVO";
		Dataset ds = this.getCurrentView().getViewModels().getDataset(id);

		Field[] fields = ds.getFieldSet().getFields();

		List<SuperVO> excelDatas = getExcelDatas(fullPath, fields);

		Row[] rows = ds.getAllRow();

		for (int i = 0; i < rows.length; i++) {
			Row row = rows[i];
			for (int j = 0; j < fields.length; j++) {
				String key = fields[j].getId();
				row.setValue(j, excelDatas.get(i).getAttributeValue(key));
			}
		}
		ds.setRowSelectIndex(0);
		ds.setEnabled(true);
	}

	private boolean deletefile(String fullPath) {
		File file = new File(fullPath);
		if (!file.exists()) {
			System.out.println("删除文件失败:" + fullPath + "不存在！");
			return false;
		} else {
			if (file.delete()) {
				System.out.println("删除单个文件" + fullPath + "成功!");
				return true;
			}
			return false;
		}
	}

	/**
	 * 提供上下文
	 * 
	 * @return
	 */
	private ApplicationContext getAppContext() {
		return AppLifeCycleContext.current().getApplicationContext();
	}

	private List<SuperVO> getExcelDatas(String fullPath, Field[] fields) {
		XSSFWorkbook wb = null;
		try {
			wb = new XSSFWorkbook(new FileInputStream(fullPath));
		} catch (Exception e) {
			throw new LfwRuntimeException("Excel表格式不正确！请使用导出模板导入");
		}
		XSSFSheet sheet = wb.getSheetAt(0);
		checkOutput(sheet, fields);
		int rowCount = sheet.getLastRowNum();
		if (rowCount < 1) {
			throw new LfwRuntimeException("Excel中没有数据！");
		}
		List<SuperVO> excelDatas = new ArrayList<SuperVO>(rowCount);
		for (int i = 1; i <= rowCount; i++) {
			WaBaSchTVO vo = new WaBaSchTVO();
			XSSFRow row = sheet.getRow(i);
			for (int j = 0; j < fields.length; j++) {
				String key = fields[j].getId();
				vo.setAttributeValue(key, row.getCell(j));
			}
			excelDatas.add(vo);
		}
		return excelDatas;
	}

	private void checkOutput(XSSFSheet sheet, Field[] fields) {
		XSSFRow row = sheet.getRow(0);
		for (int i = 0; i < fields.length; i++) {
			if (row.getCell(i) == null) {
				throw new LfwRuntimeException("当前导入模板不是正常导出模板，请使用导出模板导入");
			}
		}
	}

	public void onSave(MouseEvent mouseEvent) {
		System.out.println("11111111111");

		String id = "WaBaSchTVO";
		Dataset ds = this.getCurrentView().getViewModels().getDataset(id);

		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < this.getDetailDsIds().length; i++) {
			String ids = this.getDetailDsIds()[i];
			list.add(ids);
		}
		list.add(id);

		UifSaveCmdRV rv = new UifSaveCmdRV(getMasterDsId(), list.toArray(new String[0]));
		rv.execute();
		onAfterSave(this.getMasterDs(), ds);
	}

	protected void onAfterSave(Dataset masterDs, Dataset detailDss) {
		masterDs.setEnabled(false);
		detailDss.setEnabled(false);
		MenubarComp comp = this.getCurrentView().getViewMenus().getMenuBar("menubar");
		comp.getItem("approve").setEnabled(true);
	}

	public void onApprove(MouseEvent mouseEvent) {

		Dataset ds = getMasterDs();
		ds.setValue("approvestatus", "1");
		ds.setValue("cyear", "2018");

		String id = "WaBaSchTVO";
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < this.getDetailDsIds().length; i++) {
			String ids = this.getDetailDsIds()[i];
			list.add(ids);
		}
		list.add(id);

		UifSaveCmdRV rv = new UifSaveCmdRV(getMasterDsId(), list.toArray(new String[0]));
		rv.execute();
		onAfterSave(this.getMasterDs(), ds);

	}
}
