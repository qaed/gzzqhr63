package com.yonyou.portal.hrss.wabasch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.ml.NCLangResOnserver;
import nc.itf.hrwa.IWaBaSchMaintain;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapProcessor;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.message.util.MessageCenter;
import nc.message.vo.MessageVO;
import nc.message.vo.NCMessage;
import nc.uap.ctrl.excel.ExcelCellSetting;
import nc.uap.ctrl.excel.ICpExcelOpeWithSettingService;
import nc.uap.ctrl.excel.UifExcelImportCmd;
import nc.uap.ctrl.tpl.print.ICpPrintTemplateService;
import nc.uap.ctrl.tpl.print.init.DefaultPrintService;
import nc.uap.lfw.core.AppInteractionUtil;
import nc.uap.lfw.core.ContextResourceUtil;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.cmd.UifAddCmd;
import nc.uap.lfw.core.cmd.UifCopyCmd;
import nc.uap.lfw.core.cmd.UifDatasetLoadCmd;
import nc.uap.lfw.core.cmd.UifDelCmdRV;
import nc.uap.lfw.core.cmd.UifLineDelCmd;
import nc.uap.lfw.core.cmd.UifSaveCmdRV;
import nc.uap.lfw.core.cmd.UifUpdateUIDataCmdRV;
import nc.uap.lfw.core.cmd.base.CommandStatus;
import nc.uap.lfw.core.combodata.ComboData;
import nc.uap.lfw.core.comp.GridColumn;
import nc.uap.lfw.core.comp.GridColumnGroup;
import nc.uap.lfw.core.comp.GridComp;
import nc.uap.lfw.core.comp.IGridColumn;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.comp.MenubarComp;
import nc.uap.lfw.core.comp.WebComponent;
import nc.uap.lfw.core.comp.WebElement;
import nc.uap.lfw.core.constants.AppConsts;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Field;
import nc.uap.lfw.core.data.FieldSet;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.uap.lfw.core.event.DatasetEvent;
import nc.uap.lfw.core.event.DialogEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.event.ScriptEvent;
import nc.uap.lfw.core.exception.LfwBusinessException;
import nc.uap.lfw.core.exception.LfwRuntimeException;
import nc.uap.lfw.core.file.FillFileInfoHelper;
import nc.uap.lfw.core.log.LfwLogger;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.uif.delegator.DefaultDataValidator;
import nc.uap.lfw.file.LfwFileConstants;
import nc.uap.lfw.util.LanguageUtil;
import nc.uap.wfm.constant.WfmConstants;
import nc.uap.wfm.exe.WfmCmd;
import nc.uap.wfm.utils.WfmCPUtilFacade;
import nc.uap.wfm.utils.WfmTaskUtil;
import nc.uap.wfm.vo.WfmFormInfoCtx;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.wa.wa_ba.item.VisiableFieldVO;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;
import nc.vo.wa.wa_ba.sch.WaBaSchHVO;
import nc.vo.wa.wa_ba.sch.WaBaSchTVO;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import uap.lfw.core.itf.ctrl.AbstractMasterSlaveViewController;
import uap.lfw.core.locator.ServiceLocator;

import com.yonyou.portal.hrss.wabasch.wfm.WfmFlwFormVO;

/**
 * 卡片窗口默认逻辑
 */
public class WabaschCardWinMainViewCtrl<T extends WebElement> extends AbstractMasterSlaveViewController {
	private static final String PLUGOUT_ID = "afterSavePlugout";
	public static final String OPEN_BILL_ID = "openBillId";
	private IMDPersistenceQueryService queryservice = NCLocator.getInstance().lookup(IMDPersistenceQueryService.class);
	private BaseDAO dao = null;

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
		GridComp gridComp = (GridComp) this.getCurrentView().getViewComponents().getComponent("WaBaSchTVO_grid");
		menucomp.getItem("approve").setEnabled(false);
		menucomp.getItem("save").setEnabled(false);
		gridComp.getMenuBar().getItem("WaBaSchTVO_grid$HeaderBtn_Save").setEnabled(false);
		gridComp.getMenuBar().getItem("WaBaSchTVO_grid$HeaderBtn_Edit").setEnabled(true);
		//可以不改数据，直接完成分配
		//		menucomp.getItem("allocated").setEnabled(false);
		menucomp.getItem("add").setEnabled(false);
		menucomp.getItem("copy").setEnabled(false);
		menucomp.getItem("del").setEnabled(false);

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

		Dataset Bodyds = this.getCurrentView().getViewModels().getDataset("WaBaSchBVO");
		Row[] rows = Bodyds.getAllRow();
		StringBuilder sql = new StringBuilder();
		try {
			//显示字段的值
			for (Row row : rows) {
				sql.delete(0, sql.length());
				sql.append("select doc1.name vdef1,doc2.name name1,doc3.name name2,doc4.name name3 from wa_ba_sch_unit sch");
				sql.append(" left join wa_ba_unit unit on sch.ba_unit_code=unit.pk_wa_ba_unit");
				sql.append(" left join bd_psndoc doc1 on doc1.pk_psndoc=sch.vdef1");
				sql.append(" left join bd_psndoc doc2 on doc2.pk_psndoc=unit.ba_mng_psnpk");
				sql.append(" left join bd_psndoc doc3 on doc3.pk_psndoc=unit.ba_mng_psnpk2");
				sql.append(" left join bd_psndoc doc4 on doc4.pk_psndoc=unit.ba_mng_psnpk3");
				sql.append(" where sch.pk_ba_sch_unit='" + row.getString(Bodyds.nameToIndex("pk_ba_sch_unit")) + "'");
				Map<String, String> nameMap = (Map<String, String>) getDao().executeQuery(sql.toString(), new MapProcessor());
				row.setValue(Bodyds.nameToIndex("vdef1_name"), nameMap.get("vdef1"));//当期分配人
				row.setValue(Bodyds.nameToIndex("ba_mng_name1"), nameMap.get("name1"));//分配人1
				row.setValue(Bodyds.nameToIndex("ba_mng_name2"), nameMap.get("name2"));//分配人2
				row.setValue(Bodyds.nameToIndex("ba_mng_name3"), nameMap.get("name3"));//分配人3
			}
		} catch (Exception e) {
			throw new LfwRuntimeException(e);
		}
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

		WaBaSchHVO vo = (WaBaSchHVO) formCtx;

		int row = 0;
		try {
			row = getDao().updateVO(vo);
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
		MenubarComp menucomp = this.getCurrentView().getViewMenus().getMenuBar("menubar");
		GridComp gridComp = (GridComp) this.getCurrentView().getViewComponents().getComponent("WaBaSchTVO_grid");
		menucomp.getItem("allocated").setEnabled(false);
		menucomp.getItem("save").setEnabled(true);
		gridComp.getMenuBar().getItem("WaBaSchTVO_grid$HeaderBtn_Save").setEnabled(true);
		gridComp.getMenuBar().getItem("WaBaSchTVO_grid$HeaderBtn_Edit").setEnabled(false);
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

		ExcelCellSetting set = new ExcelCellSetting("revise_totalmoney", HSSFColor.RED.index);

		ICpExcelOpeWithSettingService excelOpe =
				(ICpExcelOpeWithSettingService) NCLocator.getInstance().lookup(ICpExcelOpeWithSettingService.class);
		AppLifeCycleContext ctx = AppLifeCycleContext.current();
		nc.uap.lfw.core.page.LfwView widget = ctx.getViewContext().getView();
		String excelStr = excelOpe.exportExcelWithSetting(widget, dsid, compid, "1.xls", set);
		try {
			ctx.getWindowContext().addExecScript("sysDownloadFile('" + nc.uap.lfw.core.LfwRuntimeEnvironment.getRootPath() + "/" + excelStr + "')");
		} catch (Exception e) {
			nc.uap.cpb.log.CpLogger.error(e);
		}

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
		UifExcelImportCmd cmd = new UifExcelImportCmd("com.yonyou.portal.hrss.wabasch.WabaschCardWinMainViewCtrl", PLUGOUT_ID);
		cmd.execute();
		//TVO设为可编辑，否则无法正常保存数据
		this.getCurrentView().getViewModels().getDataset("WaBaSchTVO").setEnabled(true);
		//配合TVO的可编辑状态
		MenubarComp menuBar = this.getCurrentView().getViewMenus().getMenuBar("menubar");
		GridComp gridComp = (GridComp) this.getCurrentView().getViewComponents().getComponent("WaBaSchTVO_grid");
		menuBar.getItem("save").setEnabled(true);
		gridComp.getMenuBar().getItem("WaBaSchTVO_grid$HeaderBtn_Save").setEnabled(true);
		gridComp.getMenuBar().getItem("WaBaSchTVO_grid$HeaderBtn_Edit").setEnabled(false);
		menuBar.getItem("allocated").setEnabled(false);

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
		AppInteractionUtil.showMessageDialog("导入成功", true);
	}

	private void doImport(String fullPath) throws FileNotFoundException, IOException, LfwBusinessException {
		String id = "WaBaSchTVO";
		//add by ljw
		String compid = "WaBaSchTVO_grid";

		AppLifeCycleContext ctx = AppLifeCycleContext.current();
		LfwView widget = ctx.getViewContext().getView();
		Dataset ds = widget.getViewModels().getDataset(id);

		WebComponent comp = widget.getViewComponents().getComponent(compid);
		if (ds == null) {
			throw new LfwRuntimeException(NCLangResOnserver.getInstance().getStrByID("imp", "CpExcelOpeServiceImpl-000000") + id);
		}
		if (comp == null) {
			throw new LfwRuntimeException(NCLangResOnserver.getInstance().getStrByID("imp", "CpExcelOpeServiceImpl-000001") + compid);
		}
		List<VisiableFieldVO> exportFields = getExportedFields(widget, ds, comp);
		//end
		Field[] fields = ds.getFieldSet().getFields();

		List<SuperVO> excelDatas = getExcelDatas(fullPath, exportFields, ds.getCurrentRowCount());

		Row[] rows = ds.getAllRow();

		for (int i = 0; i < rows.length; i++) {
			Row row = rows[i];
			//for (int j = 0; j < fields.length; j++) {
			for (int j = 0; j < exportFields.size(); j++) {
				String key = exportFields.get(j).getId();
				//只能修改修订后绩效系数和修订后绩效工资总额
				if ("revise_totalmoney".equals(key)) {
					for (int k = 0; k < fields.length; k++) {
						if (fields[k].getId().equals(key)) {

							row.setValue(k, excelDatas.get(i).getAttributeValue(key));
							break;
						}
					}
				}
			}
		}
		ds.setRowSelectIndex(0);

	}

	/**
	 * add by ljw
	 */
	private List<VisiableFieldVO> getExportedFields(LfwView widget, Dataset ds, WebComponent comp) {
		GridComp grid = (GridComp) comp;
		List<VisiableFieldVO> list = new ArrayList();
		List<IGridColumn> colList = grid.getColumnList();
		Iterator<IGridColumn> it = colList.iterator();

		while (it.hasNext()) {
			IGridColumn f = (IGridColumn) it.next();
			convertCol(f, list, ds, widget);
		}
		return list;
	}

	/**
	 * add by ljw
	 */
	private void convertCol(IGridColumn column, List<VisiableFieldVO> list, Dataset ds, LfwView widget) {
		if ((column instanceof GridColumn)) {
			GridColumn col = (GridColumn) column;
			if (!col.isVisible())
				return;
			VisiableFieldVO field = new VisiableFieldVO();
			String dsField = col.getField();
			if ((dsField == null) || (dsField.equals("")))
				return;
			String refComboId = col.getRefComboData();
			if ((refComboId != null) && (!refComboId.equals(""))) {
				ComboData comboData = widget.getViewModels().getComboData(refComboId);
				field.setComboData(comboData);
			}
			field.setId(dsField);
			String langDir = col.getLangDir();
			String i18nName = col.getI18nName();
			if (langDir == null)
				langDir = LfwRuntimeEnvironment.getLangDir();
			if ((i18nName == null) || (langDir == null)) {
				field.setName(col.getText());
			} else {
				field.setName(LanguageUtil.getWithDefaultByProductCode(langDir, col.getText(), i18nName));
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

	private List<SuperVO> getExcelDatas(String fullPath, List<VisiableFieldVO> exportFields, int dsRowCount) {
		XSSFWorkbook wb = null;
		try {
			wb = new XSSFWorkbook(new FileInputStream(fullPath));
		} catch (Exception e) {
			throw new LfwRuntimeException("Excel表格式不正确！请使用导出模板导入");
		}
		XSSFSheet sheet = wb.getSheetAt(0);
		checkOutput(sheet, exportFields, dsRowCount);

		List<SuperVO> excelDatas = new ArrayList<SuperVO>(dsRowCount);
		for (int i = 1; i <= dsRowCount; i++) {
			WaBaSchTVO vo = new WaBaSchTVO();
			XSSFRow row = sheet.getRow(i);
			//for (int j = 0; j < fields.length; j++) {
			for (int j = 0; j < exportFields.size(); j++) {
				//String key = fields[j].getId();
				String key = exportFields.get(j).getId();
				vo.setAttributeValue(key, row.getCell(j));
			}
			excelDatas.add(vo);
		}
		return excelDatas;
	}

	private void checkOutput(XSSFSheet sheet, List<VisiableFieldVO> fields, int dsRowCount) {
		XSSFRow row = sheet.getRow(0);
		for (int i = 0; i < fields.size(); i++) {
			if (row.getCell(i) != null && !row.getCell(i).getRichStringCellValue().getString().equals(fields.get(i).getName())) {

				throw new LfwRuntimeException("当前导入模板不是正常导出模板，请使用导出模板导入");
			}
		}

		int rowCount = sheet.getLastRowNum();
		if (rowCount < 1) {
			throw new LfwRuntimeException("Excel中没有数据！");
		}

		if (rowCount != dsRowCount) {
			throw new LfwRuntimeException("导入的人员数与单据人员数不一致，请使用导出模板导入");
		}
	}

	public void onSave(MouseEvent mouseEvent) {
		String id = "WaBaSchTVO";
		Dataset ds = this.getCurrentView().getViewModels().getDataset(id);
		try {
			//检查是否会使得BVO的本期结余为负数
			StringBuilder sql = new StringBuilder();
			sql.delete(0, sql.length());
			sql.append("select plan_totalmoney from wa_ba_sch_unit where pk_ba_sch_unit='" + ds.getValue("pk_ba_sch_unit").toString() + "'");
			//当期可分配总额
			UFDouble planTotalmoney = new UFDouble((BigDecimal) getDao().executeQuery(sql.toString(), new ColumnProcessor()));
			//已分配
			UFDouble currTotalmoney = UFDouble.ZERO_DBL;
			Row[] rows = ds.getAllRow();
			for (Row row : rows) {
				//调整后金额
				UFDouble reviseMoney = row.getUFDobule(ds.nameToIndex("revise_totalmoney"));
				if (reviseMoney == null) {
					currTotalmoney = currTotalmoney.add(row.getUFDobule(ds.nameToIndex("f_10")));
				} else {
					currTotalmoney = currTotalmoney.add(reviseMoney);
				}
			}
			if (planTotalmoney.compareTo(currTotalmoney) < 0) {
				throw new LfwRuntimeException("当期可分配金额不足，请修改后重试！当前已分配金额：" + currTotalmoney.setScale(2, BigDecimal.ROUND_HALF_UP));
			}
			UifSaveCmdRV rv = new UifSaveCmdRV("WaBaSchBVO", new String[] { "WaBaSchTVO" });
			rv.execute();
			onAfterSave(this.getMasterDs(), ds);
			//更新BVO奖金分配单元的"当期已分配金额"
			sql.delete(0, sql.length());
			sql.append("update wa_ba_sch_unit set class3= ");
			sql.append(" (select sum(case when revise_totalmoney is not null then revise_totalmoney else f_10 end)  ");
			sql.append("   from wa_ba_sch_psns where wa_ba_sch_psns.pk_ba_sch_unit= wa_ba_sch_unit.pk_ba_sch_unit)  ");
			sql.append(" where pk_ba_sch_unit='" + ds.getValue("pk_ba_sch_unit").toString() + "'");
			getDao().executeUpdate(sql.toString());
			//更新BVO奖金分配单元的"本期结余"
			sql.delete(0, sql.length());
			sql.append("update wa_ba_sch_unit set class4= ");
			sql.append("  (case when plan_totalmoney-class3<0 then 0 else plan_totalmoney-class3 end)  ");
			sql.append(" where pk_ba_sch_unit='" + ds.getValue("pk_ba_sch_unit").toString() + "'");
			getDao().executeUpdate(sql.toString());

			AppInteractionUtil.showShortMessage(NCLangRes4VoTransl.getNCLangRes().getStrByID("pa", "PaPropertySaveListener-000000"));/*保存成功！*/
			// 重新加载数据
			onAfterRowSelect(new DatasetEvent(getMasterDs()));

		} catch (Exception e) {
			throw new LfwRuntimeException(e);
		}
	}

	protected void onAfterSave(Dataset masterDs, Dataset detailDss) {
		masterDs.setEnabled(false);
		detailDss.setEnabled(false);
		MenubarComp comp = this.getCurrentView().getViewMenus().getMenuBar("menubar");
		GridComp gridComp = (GridComp) this.getCurrentView().getViewComponents().getComponent("WaBaSchTVO_grid");

		comp.getItem("add").setEnabled(false);
		comp.getItem("copy").setEnabled(false);
		comp.getItem("del").setEnabled(false);
		comp.getItem("save").setEnabled(false);
		gridComp.getMenuBar().getItem("WaBaSchTVO_grid$HeaderBtn_Save").setEnabled(false);
		gridComp.getMenuBar().getItem("WaBaSchTVO_grid$HeaderBtn_Edit").setEnabled(true);
		comp.getItem("approve").setEnabled(false);
		comp.getItem("allocated").setEnabled(true);//可以点击“完成分配”
	}

	public void onApprove(MouseEvent mouseEvent) {
		Dataset ds = getMasterDs();
		/*
		ds.setValue("approvestatus", "1");
		//		ds.setValue("cyear", "2018");

		String id = "WaBaSchTVO";
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < this.getDetailDsIds().length; i++) {
			String ids = this.getDetailDsIds()[i];
			list.add(ids);
		}
		list.add(id);

		//		UifSaveCmdRV rv = new UifSaveCmdRV(getMasterDsId(), list.toArray(new String[0]));
		UifSaveCmdRV rv = new UifSaveCmdRV(getMasterDsId(), new String[] { "WaBaSchBVO", "WaBaSchTVO" });
		rv.execute();
		*/
		AggWaBaSchHVO[] bills = null;
		IWaBaSchMaintain operator = NCLocator.getInstance().lookup(IWaBaSchMaintain.class);

		AggWaBaSchHVO[] fullvos = new AggWaBaSchHVO[1];
		try {
			fullvos[0] = queryservice.queryBillOfVOByPK(AggWaBaSchHVO.class, getMasterDs().getValue("pk_ba_sch_h").toString(), false);
			bills = operator.approve(fullvos, fullvos);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		onAfterSave(this.getMasterDs(), ds);

	}

	public void onAfterDataChange(DatasetCellEvent datasetCellEvent) {
		//		LfwSysOutWrapper.println("孙表编辑后事件");
		datasetCellEvent.getNewValue();
		Dataset ds = datasetCellEvent.getSource();
		FieldSet fs = ds.getFieldSet();
		if (fs.nameToIndex("revise_factor") == datasetCellEvent.getColIndex()) {//修改"修订后考核系数"
			//修订后考核系数
			UFDouble revise_factor = ds.getSelectedRow().getUFDobule(fs.nameToIndex("revise_factor"));
			if (revise_factor == null) {//删完"修订后考核系数"的值
				ds.setValue("revise_totalmoney", null);
			} else {//修改为正常的值
				//标准职位薪酬
				UFDouble f_2 = ds.getSelectedRow().getUFDobule(fs.nameToIndex("f_2"));
				//修订后绩效工资总额=标准职位薪酬*修订后考核系数
				ds.setValue("revise_totalmoney", revise_factor.multiply(f_2).toString());
			}
		}
	}

	public void onAllocate(MouseEvent mouseEvent) {
		if (!AppInteractionUtil.showConfirmDialog("请确认", "是否确认本次分配")) {
			return;
		}
		Dataset ds = getCurrentView().getViewModels().getDataset("WaBaSchBVO");
		try {
			//查询该分配单元的所有分配人
			StringBuilder sql = new StringBuilder();
			sql.append("select ba_mng_psnpk||','||ba_mng_psnpk2||','||ba_mng_psnpk3 from wa_ba_unit where pk_wa_ba_unit='" + ds.getValue("ba_unit_code").toString() + "'");
			String psnpks = (String) getDao().executeQuery(sql.toString(), new ColumnProcessor());
			//转为list
			List<String> pkList = Arrays.asList(StringUtils.split(psnpks, ","));
			int index = pkList.indexOf(SessionUtil.getPk_psndoc());
			if (index != 2 && !"~".equals(pkList.get(++index))) {//size=3
				//存在下一个分配人，传递给下一个分配人,并推送消息
				sendnextmsg(pkList.get(index));//推送消息
				//保存当前分配人pk
				getDao().executeUpdate("update wa_ba_sch_unit set vdef1='" + pkList.get(index) + "' where pk_ba_sch_unit='" + (String) getCurrentView().getViewModels().getDataset("WaBaSchBVO").getValue("pk_ba_sch_unit") + "'");
			} else {
				//不存在下一个分配人,可以等待审批了，不能进行审批，因为可能还有其他的分配单元没分配完成
				//把分配人置为空
				getDao().executeUpdate("update wa_ba_sch_unit set vdef1=null where pk_ba_sch_unit='" + (String) getCurrentView().getViewModels().getDataset("WaBaSchBVO").getValue("pk_ba_sch_unit") + "'");
			}
			this.getCurrentAppCtx();
			//主表SCHHVO主键
			String pk_h = getMasterDs().getValue(this.getMasterDs().getPrimaryKeyField()).toString();
			//分配单元UnitHVO主键
			String pk_unit_h = getCurrentView().getViewModels().getDataset("WaBaSchBVO").getValue("ba_unit_code").toString();
			sql.delete(0, sql.length());
			sql.append("select name from wa_ba_unit where pk_wa_ba_unit='" + pk_unit_h + "'");
			//分配单元的名字
			String unitName = (String) getDao().executeQuery(sql.toString(), new ColumnProcessor());
			//当期分配人pk
			String currentMngpsnpk = getCurrentView().getViewModels().getDataset("WaBaSchBVO").getValue("vdef1").toString();
			sql.delete(0, sql.length());
			sql.append("select cuserid from sm_user where pk_psndoc='" + currentMngpsnpk + "'");
			//当期分配人的cuserid
			String userid = (String) getDao().executeQuery(sql.toString(), new ColumnProcessor());
			sql.delete(0, sql.length());
			sql.append("update sm_msg_content set isread='Y',ishandled='Y' where  detail like '" + pk_h + "@BAAL%' and receiver='" + userid + "' and subject like '%" + unitName + "%' ");
			getDao().executeUpdate(sql.toString());
			AppInteractionUtil.showShortMessage("分配成功");
			this.getCurrentAppCtx().closeWinDialog();
		} catch (Exception e) {
			//保持异常链
			throw new LfwRuntimeException(e);
		}

	}

	/**
	 * @return dao
	 */
	private BaseDAO getDao() {
		if (this.dao == null) {
			this.dao = new BaseDAO();
		}
		return this.dao;
	}

	/**
	 * 发送消息给下一个分配人
	 * 
	 * @param receiverpsnpk 下一个分配人
	 * @throws Exception
	 * @throws DAOException
	 */
	private void sendnextmsg(String receiverpsnpk) throws Exception {
		WorkflownoteVO workflownoteVO = new WorkflownoteVO();
		workflownoteVO.setBillid((String) getMasterDs().getValue("pk_ba_sch_h"));
		workflownoteVO.setBillno((String) getMasterDs().getValue("sch_code"));
		workflownoteVO.setUserobject(null);
		workflownoteVO.setWorkflow_type(2);
		getDao().insertVO(workflownoteVO);
		//构造消息
		NCMessage[] ncmsg = new NCMessage[1];
		MessageVO msg = new MessageVO();
		//查接收人
		SQLParameter parameter = new SQLParameter();
		parameter.clearParams();
		parameter.addParam(receiverpsnpk);
		String receiver =
				(String) getDao().executeQuery("select sm_user.cuserid from sm_user where pk_psndoc=?", parameter, new ColumnProcessor());
		//查创建人姓名
		parameter.clearParams();
		parameter.addParam((String) getMasterDs().getValue("creator"));
		String creatorName =
				(String) getDao().executeQuery("select user_name from sm_user where sm_user.cuserid=?", parameter, new ColumnProcessor());
		//查奖金分配单元的名字
		parameter.clearParams();
		parameter.addParam((String) getCurrentView().getViewModels().getDataset("WaBaSchBVO").getValue("pk_ba_sch_unit"));
		String unitName =
				(String) getDao().executeQuery("select name from wa_ba_unit left join wa_ba_sch_unit on wa_ba_unit.pk_wa_ba_unit=wa_ba_sch_unit.ba_unit_code where pk_ba_sch_unit=?", parameter, new ColumnProcessor());
		//构造消息
		msg.setSender((String) getMasterDs().getValue("creator"));//发送人
		msg.setReceiver(receiver);//接受人
		msg.setMsgsourcetype("worklist");//消息来源类型
		msg.setPriority(5);//优先级
		msg.setSendtime(new UFDateTime());//发送信息时间
		msg.setSubject("请审批 " + creatorName + " 发起的 " + unitName + "_" + getMasterDs().getValue("sch_name").toString());//标题
		msg.setPk_group((String) getMasterDs().getValue("pk_group"));
		msg.setPk_detail(workflownoteVO.getPrimaryKey());
		msg.setPk_org((String) getMasterDs().getValue("pk_org"));
		msg.setDestination("inbox");
		msg.setDetail((String) getMasterDs().getValue("pk_ba_sch_h") + "@" + (String) getMasterDs().getValue("billtype") + "@" + (String) getMasterDs().getValue("sch_code"));//详细信息
		msg.setContenttype("~");
		//msg.setDomainflag("AUM");
		ncmsg[0] = new NCMessage();
		ncmsg[0].setMessage(msg);

		MessageCenter.sendMessage(ncmsg);
	}

	/**
	 * 更新表体的当前已分配总额
	 * 
	 * @throws DAOException
	 */
	private void updateBvoSum() throws DAOException {
		Dataset bodyds = getCurrentView().getViewModels().getDataset("WaBaSchBVO");

		StringBuilder sql = new StringBuilder();
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(bodyds.getValue("pk_ba_sch_unit"));
		sql.append("update wa_ba_sch_unit set class3=(select sum(f_10) from wa_ba_sch_psns where wa_ba_sch_psns.pk_ba_sch_unit=wa_ba_sch_unit.pk_ba_sch_unit ) where pk_ba_sch_unit=?");
		getDao().executeUpdate("");
	}
}
