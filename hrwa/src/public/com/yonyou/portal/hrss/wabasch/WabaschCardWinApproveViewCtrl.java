package com.yonyou.portal.hrss.wabasch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.md.model.MetaDataException;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.uap.cpb.persist.dao.PtBaseDAO;
import nc.uap.ctrl.excel.CpExcelModel;
import nc.uap.ctrl.excel.ICpExcelOpeService;
import nc.uap.ctrl.excel.UifExcelExportCmd;
import nc.uap.ctrl.excel.UifExcelImportCmd;
import nc.uap.ctrl.tpl.print.ICpPrintTemplateService;
import nc.uap.ctrl.tpl.print.init.DefaultPrintService;
import nc.uap.lfw.core.AppInteractionUtil;
import nc.uap.lfw.core.ContextResourceUtil;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.cmd.UifAddCmd;
import nc.uap.lfw.core.cmd.UifCopyCmd;
import nc.uap.lfw.core.cmd.UifDatasetAfterSelectCmd;
import nc.uap.lfw.core.cmd.UifDatasetLoadCmd;
import nc.uap.lfw.core.cmd.UifDelCmdRV;
import nc.uap.lfw.core.cmd.UifLineDelCmd;
import nc.uap.lfw.core.cmd.UifPlugoutCmd;
import nc.uap.lfw.core.cmd.UifUpdateUIDataCmdRV;
import nc.uap.lfw.core.cmd.base.CommandStatus;
import nc.uap.lfw.core.comp.ButtonComp;
import nc.uap.lfw.core.comp.GridComp;
import nc.uap.lfw.core.comp.LabelComp;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.comp.ReferenceComp;
import nc.uap.lfw.core.comp.WebElement;
import nc.uap.lfw.core.comp.text.TextComp;
import nc.uap.lfw.core.constants.AppConsts;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.ctx.OpenProperties;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.DatasetRelation;
import nc.uap.lfw.core.data.Field;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DatasetEvent;
import nc.uap.lfw.core.event.DialogEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.event.ScriptEvent;
import nc.uap.lfw.core.exception.LfwBusinessException;
import nc.uap.lfw.core.exception.LfwRuntimeException;
import nc.uap.lfw.core.file.FillFileInfoHelper;
import nc.uap.lfw.core.log.LfwLogger;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.Datasets2RichVOSerializer;
import nc.uap.lfw.core.uif.delegator.DefaultDataValidator;
import nc.uap.lfw.core.uimodel.ViewConfig;
import nc.uap.lfw.file.LfwFileConstants;
import nc.uap.wfm.constant.WfmConstants;
import nc.uap.wfm.exe.WfmCmd;
import nc.uap.wfm.exetask.ApproveExeTaskMainCtrl;
import nc.uap.wfm.pubview.ExecuteTaskWidgetProvider;
import nc.uap.wfm.utils.WfmCPUtilFacade;
import nc.uap.wfm.utils.WfmProDefUtil;
import nc.uap.wfm.utils.WfmPublicViewUtil;
import nc.uap.wfm.utils.WfmTaskUtil;
import nc.uap.wfm.utils.WfmUtilFacade;
import nc.uap.wfm.vo.WfmFormInfoCtx;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;
import nc.vo.wa.wa_ba.sch.WaBaSchTVO;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import uap.lfw.core.itf.ctrl.AbstractMasterSlaveViewController;
import uap.lfw.core.locator.ServiceLocator;
import uap.lfw.core.ml.LfwResBundle;
import uap.web.bd.pub.AppUtil;

import com.yonyou.portal.hrss.wabasch.wfm.WfmFlwFormVO;

/**
 * 卡片窗口默认逻辑
 */
public class WabaschCardWinApproveViewCtrl extends ApproveExeTaskMainCtrl {
	private static final String PLUGOUT_ID = "afterSavePlugout";
	public static final String OPEN_BILL_ID = "openBillId";

	/**
	 * 提交
	 * 
	 * @param mouseEvent
	 */
	public void simpleBtnok_click(MouseEvent<MenuItem> mouseEvent) {
		//		okClick(ExecuteTaskWidgetProvider.EXE_AGREE);
		okClick(null);
	}

	@Override
	public void beforeShow(DialogEvent e) {
		List<Map<String, String>> list = AppLifeCycleContext.current().getGroupParamMapList();
		boolean containFatherPage = false;
		if (list != null)
			for (Map<String, String> map : list) {
				if (ApproveExeTaskMainCtrl.class.getName().equals((String) map.get("clc"))) {
					containFatherPage = true;
				}
			}
		if (!containFatherPage)
			super.beforeShow(e);
	}

	private void okClick(String exeAction) {
		String taskPk = WfmTaskUtil.getTaskPkFromSession();
		Object task = WfmTaskUtil.getTaskFromSessionCache(taskPk);

		Object human = null;
		if (task == null) {
			//没有任务则制单时，取开始活动
			Object prodef = WfmUtilFacade.featchProdefFromSession();
			human = WfmProDefUtil.getStartHumanAct(prodef);
		} else {
			human = WfmTaskUtil.getHumActByTask(task);
		}
		String pk_user = LfwRuntimeEnvironment.getLfwSessionBean().getPk_user();
		getMasterDs().setValue("approvestatus", "3");
		getMasterDs().setValue("bill_submit_date", new UFDate().toString());
		getMasterDs().setValue("bill_submitter", pk_user);

		Boolean isAllowEdit = WfmProDefUtil.allowOpinionEditByHumAct(human);
		Boolean needOpinion = WfmProDefUtil.isOpinionNeedByHumAct(human);
		//制单态必输以及审批环节可编辑意见
		if ((task == null && needOpinion) || (task != null && isAllowEdit)) {
			Object hasInputOpinion = (Object) AppUtil.getAppAttr(WfmConstants.WfmAppAttr_HasInputOpinion);
			if (hasInputOpinion == null) {
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("model", "nc.uap.wfm.pubview.SimpleWfmOpionionPageModel");
				paramMap.put("action", exeAction);
				paramMap.put("needOpinion", needOpinion.booleanValue() ? "Y" : "N");
				OpenProperties props = new OpenProperties();
				props.setWidth("520");
				props.setHeight("300");
				props.setOpenId("opinion");
				props.setTitle(NCLangRes4VoTransl.getNCLangRes().getStrByID("wfm", "DispStrategy-000088")/*意见*/);
				props.setParamMap(paramMap);
				AppLifeCycleContext.current().getViewContext().navgateTo(props);
			} else {
				AppUtil.addAppAttr(WfmConstants.WfmAppAttr_HasInputOpinion, null);
				AppUtil.addAppAttr(WfmConstants.WfmAppAttr_ExeAction, exeAction);
				super.btnok_click(null);
			}
		} else {
			AppUtil.addAppAttr(WfmConstants.WfmAppAttr_ExeAction, exeAction);
			super.btnok_click(null);
		}
	}

	/**
	 * 暂存
	 * 
	 * @param mouseEvent
	 */
	public void simpleBtnsave_click(MouseEvent<MenuItem> mouseEvent) {
		getMasterDs().setValue("approvestatus", "-1");
		super.btnsave_click(null);
	}

	//	/**
	//	 * 关闭时提交链接、指派时调用
	//	 * @param e
	//	 */
	//	public void titleok_click(ScriptEvent e) {
	//		super.titleok_click(null);
	//	}

	/**
	 * 附件
	 * 
	 * @param linkEvent
	 */
	public void simpleAttachClick(MouseEvent<MenuItem> mouseEvent) {
		super.attachClick(null);
	}

	/**
	 * 流程进度
	 * 
	 * @param mouseEvent
	 */
	public void simpleFlowImageClick(MouseEvent<MenuItem> mouseEvent) {
		super.flowImgClick(null);
	}

	/**
	 * 收回
	 * 
	 * @param mouseEvent
	 */
	public void simplerecallClick(MouseEvent<MenuItem> mouseEvent) {
		super.btnrecall_click(null);
	}

	/**
	 * 阅毕
	 * 
	 * @param mouseEvent
	 */
	public void simpleReadEndClick(MouseEvent<MenuItem> mouseEvent) {
		super.btnreadend_click(null);
	}

	/**
	 * 驳回
	 * 
	 * @param mouseEvent
	 */
	public void simpleRejectClick(MouseEvent<MenuItem> mouseEvent) {
		okClick(ExecuteTaskWidgetProvider.EXE_REJECT);
	}

	protected String getMasterDsId() {
		return "WaBaSchHVO";
	}

	protected String[] getDetailDsIds() {
		LfwView view = this.getCurrentView();
		if (view.getViewModels().getDsrelations() != null) {
			DatasetRelation[] rels = view.getViewModels().getDsrelations().getDsRelations(this.getMasterDsId());
			int len = rels != null ? rels.length : 0;
			if (len > 0) {
				String[] detailDsIds = new String[len];
				for (int i = 0; i < len; i++) {
					detailDsIds[i] = rels[i].getDetailDataset();
				}
				return detailDsIds;
			}
		}
		return new String[0];
	}

	protected Dataset[] getDetailDs(String[] detailDsIds) {
		int len = detailDsIds != null ? detailDsIds.length : 0;
		if (len > 0) {
			LfwView view = this.getCurrentView();
			List<Dataset> detailDs = new ArrayList<Dataset>(len);
			for (int i = 0; i < len; i++) {
				Dataset ds = view.getViewModels().getDataset(detailDsIds[i]);
				if (ds != null) {
					detailDs.add(ds);
				}
			}
			return detailDs.toArray(new Dataset[0]);
		}
		return new Dataset[0];
	}

	/**
	 * 获取当前view
	 * 
	 * @return
	 */
	protected LfwView getCurrentView() {
		return AppUtil.getView("main");
	}

	/**
	 * 获取当前view master数据集
	 * 
	 * @return
	 */
	protected Dataset getMasterDs() {
		return this.getCurrentView().getViewModels().getDataset(this.getMasterDsId());
	}

	/**
	 * 获得当前操作状态，新增、编辑或者浏览
	 * 
	 * @return
	 */
	protected String getOperator() {
		String oper = LfwRuntimeEnvironment.getWebContext().getOriginalParameter(AppConsts.OPE_SIGN);
		if (oper == null) {
			oper = (String) AppLifeCycleContext.current().getApplicationContext().getAppAttribute(AppConsts.OPE_SIGN);
		}
		return oper;
	}

	/**
	 * 获取数据集序列化为RichVO实现对象
	 * 
	 * @return
	 */
	protected Datasets2RichVOSerializer getDs2RichVOSerializer() {
		return new Datasets2RichVOSerializer();
	}

	/**
	 * 获取当前ApplicationContext
	 * 
	 * @return
	 */
	protected ApplicationContext getCurrentAppCtx() {
		return AppLifeCycleContext.current().getApplicationContext();
	}

	/**
	 * 生成PK值
	 * 
	 * @return
	 */
	protected String generatePk() {
		String datasource = LfwRuntimeEnvironment.getDatasource();
		return PtBaseDAO.generatePK(datasource);
	}

}
