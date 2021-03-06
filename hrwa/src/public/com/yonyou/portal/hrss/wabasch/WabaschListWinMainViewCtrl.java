package com.yonyou.portal.hrss.wabasch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.jdbc.framework.processor.MapProcessor;
import nc.uap.cpb.org.querycmd.QueryCmd;
import nc.uap.ctrl.tpl.print.ICpPrintTemplateService;
import nc.uap.ctrl.tpl.print.init.DefaultPrintService;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.cmd.UifAttachCmd;
import nc.uap.lfw.core.cmd.UifDatasetAfterSelectCmd;
import nc.uap.lfw.core.cmd.UifDatasetLoadCmd;
import nc.uap.lfw.core.cmd.UifDelCmdRV;
import nc.uap.lfw.core.cmd.UifEnableCmd;
import nc.uap.lfw.core.cmd.base.FromWhereSQL;
import nc.uap.lfw.core.comp.MenubarComp;
import nc.uap.lfw.core.comp.WebElement;
import nc.uap.lfw.core.constants.AppConsts;
import nc.uap.lfw.core.ctx.OpenProperties;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.exception.LfwRuntimeException;
import nc.uap.lfw.core.log.LfwLogger;
import nc.uap.lfw.core.model.plug.TranslatedRow;
import nc.uap.lfw.file.LfwFileConstants;
import nc.uap.lfw.file.UploadFileHelper;
import nc.uap.wfm.constant.WfmConstants;
import nc.uap.wfm.itf.IWfmTaskQry;
import nc.uap.wfm.utils.WfmCPUtilFacade;
import nc.uap.wfm.utils.WfmUtilFacade;
import nc.uap.wfm.vo.WfmTaskVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import uap.lfw.core.itf.ctrl.AbstractMasterSlaveViewController;
import uap.lfw.core.locator.ServiceLocator;
import uap.lfw.dbl.uiengine.CommonObjectConstants;
import uap.web.bd.pub.AppUtil;

/**
 * 信息列表默认逻辑
 */
public class WabaschListWinMainViewCtrl<T extends WebElement> extends AbstractMasterSlaveViewController {
	private static final String MAIN_VIEW_ID = "main";
	private static final String CARD_WIN_ID = "com.yonyou.portal.hrss.WabaschComps.wabasch_cardwin";
	private static final String CARD_WIN_TITLE = "编辑";
	//当前选择的表体key，进入卡片界面时，只看到这个子表的孙表数据,同包下可见
	static final String SELECTED_SCHUNIT_KEY = "selectedSchUnitKey";
	private BaseDAO dao = null;

	/**
	 * 主数据加载
	 * 
	 * @param dataLoadEvent
	 */
	public void onDataLoad(DataLoadEvent dataLoadEvent) {
		Dataset ds = dataLoadEvent.getSource();
		CmdInvoker.invoke(new UifDatasetLoadCmd(ds.getId()));
	}

	/**
	 * 主数据改变时加载
	 * 
	 * @param datasetEvent
	 */
	public void onAfterRowSelect(DatasetEvent datasetEvent) {
		Dataset ds = datasetEvent.getSource();
		//		CmdInvoker.invoke(new UifDatasetAfterSelectCmd(ds.getId());
		CmdInvoker.invoke(new UifDatasetAfterSelectCmd(ds.getId()) {
			@Override
			protected String postProcessRowSelectVO(SuperVO vo, Dataset ds) {
				//				vo.setAttributeValue("vdef1", SessionUtil.getPk_psndoc());//只能看到需要分配的子表.这里设值，可以直接作为查询条件
				String wherePart = super.postProcessRowSelectVO(vo, ds);
				String wheresql =
						" pk_ba_sch_h='" + vo.getAttributeValue("pk_ba_sch_h") + "' and ba_unit_code in (select pk_wa_ba_unit from wa_ba_unit where ba_mng_psnpk||ba_mng_psnpk2||ba_mng_psnpk3 like '%" + SessionUtil.getPk_psndoc() + "%') ";
				if (wherePart != null) {
					wheresql = wherePart + " and " + wheresql;
				}
				return wheresql;
			}
		});
		MenubarComp comp = this.getCurrentView().getViewMenus().getMenuBar("menubar");
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
		comp.getItem("add").setEnabled(false);
		comp.getItem("del").setEnabled(false);
		//每次进入列表界面都会清空这个值，点编辑onEdit的时候再赋值
		this.getCurrentAppCtx().removeAppAttribute(SELECTED_SCHUNIT_KEY);
		//只有一条子表的时候，加载到列表界面后，直接进入卡片编辑界面
		if (rows.length == 1) {
			onEdit(null);
		}

	}

	/**
	 * 外部触发刷新
	 * 
	 * @param keys
	 */
	public void doRefresh(Map<?, ?> keys) {
		TranslatedRow selRow = (TranslatedRow) keys.get(OPERATE_ROW);
		if (selRow != null) {
			Dataset ds = this.getMasterDs();
			String sign = this.getOperator();
			if (AppConsts.OPE_EDIT.equals(sign)) {
				Row savedRow = ds.getSelectedRow();
				copyTranslateRow2Row(selRow, savedRow, ds);
				// }
			} else if (AppConsts.OPE_ADD.equals(sign)) {
				Row savedRow = ds.getEmptyRow();
				savedRow = copyTranslateRow2Row(selRow, savedRow, ds);
				ds.addRow(savedRow);
			}
		}
	}

	private Row copyTranslateRow2Row(TranslatedRow translatedRow, Row row, Dataset ds) {
		String[] rowKeyStrings = translatedRow.getKeys();
		for (int i = 0; i < rowKeyStrings.length; i++) {
			String rowKeyString = rowKeyStrings[i];
			int colIndex = ds.nameToIndex(rowKeyString);
			if (colIndex != -1)
				row.setValue(colIndex, translatedRow.getValue(rowKeyString));
		}
		return row;
	}

	/**
	 * 新增
	 * 
	 * @param mouseEvent
	 */
	public void onAdd(MouseEvent<?> mouseEvent) {
		this.onAdd_wfm();
		OpenProperties props = new OpenProperties(CARD_WIN_ID, CARD_WIN_TITLE);
		props.setButtonZone(false);
		Map<String, String> paramMap = new HashMap<String, String>(2);
		paramMap.put(AppConsts.OPE_SIGN, AppConsts.OPE_ADD);
		props.setParamMap(paramMap);
		this.getCurrentAppCtx().navgateTo(props);
	}

	/**
	 * 新增-流程业务
	 */
	private void onAdd_wfm() {
		this.getCurrentAppCtx().addAppAttribute(WfmConstants.WfmAppAttr_BillID, null);
		this.resetWfmParameter();
		this.getCurrentAppCtx().addAppAttribute(WfmConstants.WfmAppAttr_FolwTypePk, this.getFlwTypePk());
	}

	/**
	 * 打开页面与当前页面属于同属app范围，每次需要清除app中的变量
	 */
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
	 * 获得单据类型PK
	 * 
	 * @return
	 */
	private String getFlwTypePk() {
		return "0001ZZ10000000009QBD";
	}

	/**
	 * 编辑
	 * 
	 * @param scriptEvent
	 */
	public void onEdit(MouseEvent mouseEvent) {
		Dataset ds = this.getMasterDs();
		if (ds.getSelectedIndex() < 0) {
			throw new LfwRuntimeException("请选中待编辑数据");
		}

		Row row = ds.getSelectedRow();
		String pkValue = (String) row.getValue(ds.nameToIndex(ds.getPrimaryKeyField()));
		OpenProperties props = new OpenProperties(CARD_WIN_ID, CARD_WIN_TITLE);
		props.setButtonZone(false);
		Map<String, String> paramMap = new HashMap<String, String>(2);
		paramMap.put(AppConsts.OPE_SIGN, AppConsts.OPE_EDIT);
		paramMap.put(AppConsts.OPEN_BILL_ID, pkValue);
		paramMap.put(WfmConstants.WfmUrlConst_billID, pkValue);
		//保存编辑表体
		paramMap.put("selectedBodyRow", this.getDetailDs(getDetailDsIds())[0].getSelectedIndex() + "");
		props.setParamMap(paramMap);

		this.onEdit_wfm(pkValue);

		this.getCurrentAppCtx().navgateTo(props);
		Dataset Bodyds = this.getCurrentView().getViewModels().getDataset("WaBaSchBVO");
		this.getCurrentAppCtx().getAppSession().setAttribute(SELECTED_SCHUNIT_KEY, Bodyds.getSelectedRow().getString((Bodyds.nameToIndex("pk_ba_sch_unit"))));
	}

	/**
	 * 刷新
	 * 
	 * @param scriptEvent
	 */
	public void onRefresh(MouseEvent mouseEvent) {
		this.getCurrentAppCtx().refreshWindow();

	}

	/**
	 * 编辑-流程业务
	 * 
	 * @param pkValue
	 */
	private void onEdit_wfm(String pkValue) {
		this.resetWfmParameter();
		try {
			String pk_user = LfwRuntimeEnvironment.getLfwSessionBean().getPk_user();
			IWfmTaskQry taskQry = ServiceLocator.getService(IWfmTaskQry.class);
			WfmTaskVO task = taskQry.getLastTaskVOByFormPkAndUserPk(pkValue, pk_user);
			if (task != null) {
				this.getCurrentAppCtx().addAppAttribute(WfmConstants.WfmAppAttr_TaskPk, task.getPk_task());
			}
		} catch (Exception e) {
			LfwLogger.error(e);
			throw new LfwRuntimeException(e.getMessage());
		}
	}

	/**
	 * 删除
	 * 
	 * @param scriptEvent
	 */
	public void onDelete(MouseEvent mouseEvent) {
		Dataset ds = this.getMasterDs();
		if (ds.getSelectedIndex() < 0) {
			throw new LfwRuntimeException("请选中待删除数据");
		}

		Row row = ds.getSelectedRow();
		String pk_form = (String) row.getValue(ds.nameToIndex(ds.getPrimaryKeyField()));
		if (pk_form != null && !pk_form.equals("")) {
			boolean isCanDel = WfmCPUtilFacade.isCanDelBill(pk_form);
			if (isCanDel) {
				WfmCPUtilFacade.delWfmInfo(pk_form);
				CmdInvoker.invoke(new UifDelCmdRV(this.getMasterDsId()));
			} else {
				throw new LfwRuntimeException("流程已启动，无法删除单据");
			}

		} else {
			throw new LfwRuntimeException("未获取到流程单据主键!");
		}
	}

	/**
	 * 流程进度
	 * 
	 * @param mouseEvent
	 */
	public void onFlow(MouseEvent<?> mouseEvent) {
		Dataset masterDs = this.getMasterDs();
		Row row = masterDs.getSelectedRow();
		String pk_form = (String) row.getValue(masterDs.nameToIndex(masterDs.getPrimaryKeyField()));
		this.getCurrentAppCtx().addAppAttribute(WfmConstants.WfmAppAttr_BillID, pk_form);
		WfmUtilFacade.openFlowImage();
	}

	/**
	 * 启用
	 * 
	 * @param mouseEvent
	 * @throws BusinessException
	 */
	public void onStart(MouseEvent<?> mouseEvent) throws BusinessException {
		CmdInvoker.invoke(new UifEnableCmd(getMasterDsId(), true));
	}

	/**
	 * 停用
	 * 
	 * @param mouseEvent
	 * @throws BusinessException
	 */
	public void onStop(MouseEvent<?> mouseEvent) throws BusinessException {
		CmdInvoker.invoke(new UifEnableCmd(getMasterDsId(), false));
	}

	/**
	 * 附件
	 * 
	 * @param mouseEvent
	 * @throws BusinessException
	 */
	public void onAttchFile(MouseEvent<?> mouseEvent) {
		/*
		Dataset ds = this.getMasterDs();
		Row row = ds.getSelectedRow();
		if (row == null) {
			throw new LfwRuntimeException("请选中一行！");
		}
		uap.web.bd.pub.AppUtil.addAppAttr(LfwFileConstants.SYSID, LfwFileConstants.SYSID_BAFILE);
		String primaryKey = ds.getPrimaryKeyField();
		if (primaryKey == null) {
			throw new LfwRuntimeException("当前Dataset没有设置主键!");
		}
		String primaryKeyValue = (String) row.getValue(ds.nameToIndex(primaryKey));

		String taskPk = WfmUtilFacade.getTaskPkPkByPkFormIns(primaryKeyValue);
		// 如果当前人不是当前单据的参与人，此时取不到任何附件只能查询，设为浏览态
		if (taskPk == null || taskPk.equals("")) {
			this.getCurrentAppCtx().addAppAttribute(WfmConstants.BILLSTATE, WfmConstants.BILLSTATE_BROWSE);
		}

		// 流程附件参数
		Map<String, String> wfmParam = WfmUtilFacade.getFileMgrParamsByTask(taskPk);

		// 附件参数
		Map<String, String> param = UploadFileHelper.BuildDefaultPamater("", primaryKeyValue, CommonObjectConstants.AttachFileType, "");
		param.put("usescanable", "true");
		param.put("state", String.valueOf(31));

		String title = "附件";
		if (wfmParam != null && !wfmParam.isEmpty()) {
			param.putAll(wfmParam);
		}
		CmdInvoker.invoke(new UifAttachCmd(title, param));
		*/
		CommonUtil.Attachment(this.getMasterDs(), Boolean.valueOf(true));
	}

	/**
	 * 打印
	 */
	public void onPrint(MouseEvent<?> mouseEvent) {
		Dataset ds = this.getMasterDs();
		Row row = ds.getSelectedRow();
		if (row == null) {
			throw new LfwRuntimeException("请选中一行！");
		}
		try {
			List<Dataset> list = new ArrayList<Dataset>(1);
			list.add(ds);
			DefaultPrintService printService = new DefaultPrintService();
			printService.setDatasetList(list);
			ICpPrintTemplateService service = ServiceLocator.getService(ICpPrintTemplateService.class);
			service.print(printService);
		} catch (Exception e) {
			LfwLogger.error(e);
			throw new LfwRuntimeException(e.getMessage());
		}
	}

	/**
	 * 主组织变化
	 */
	public void doOrgChange(Map keys) {
		CmdInvoker.invoke(new UifDatasetLoadCmd(getMasterDsId()) {
			protected String postProcessQueryVO(SuperVO vo, Dataset ds) {
				String where = AppUtil.getGroupOrgFielterSqlForDesign(false, "pk_org");
				ds.setLastCondition(where);
				return where;
			}

			// 避免翻页时重启
			protected void changeCurrentKey() {
				setChangeCurrentKey(true);
			}
		});
		clearDetailDs();
	}

	private void clearDetailDs() {
		Dataset[] detailDss = this.getDetailDs(this.getDetailDsIds());
		int len = detailDss != null ? detailDss.length : 0;
		for (int i = 0; i < len; i++) {
			if (detailDss[i] != null) {
				detailDss[i].clear();
			}
		}
	}

	/**
	 * 查询pluguin
	 * 
	 * @param keys
	 */
	public void doQueryChange(Map keys) {
		FromWhereSQL whereSql = (FromWhereSQL) keys.get(FromWhereSQL.WHERE_SQL_CONST);
		CmdInvoker.invoke(new QueryCmd(MAIN_VIEW_ID, getMasterDsId(), whereSql.getWhere()));
	}

	@Override
	protected String getMasterDsId() {
		return "WaBaSchHVO";
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
}
