package nc.bs.hrss.pub.cmd;

import java.util.HashMap;
import java.util.Map;

import nc.bs.hrss.pub.Logger;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.hr.utils.ResHelper;
import nc.itf.hr.pf.IHrPf;
import nc.itf.hrss.pub.cmd.prcss.ICommitProcessor;
import nc.itf.hrss.pub.cmd.prcss.ISaveProcessor;
import nc.itf.uap.pf.metadata.IFlowBizItf;
import nc.uap.lfw.core.AppInteractionUtil;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.cmd.UifPlugoutCmd;
import nc.uap.lfw.core.cmd.UifSaveCmd;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.ctx.OpenProperties;
import nc.uap.lfw.core.ctx.ViewContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.Datasets2AggVOSerializer;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;

import nc.uap.wfm.ncworkflow.cmd.LfwPfUtil;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;

import nc.vo.sm.UserVO;
import org.apache.commons.lang.StringUtils;
import uap.web.bd.pub.AppUtil;

public class PFSaveCommitCmd extends SaveCmd {
	private Class<? extends ICommitProcessor> commitProcessorClass = null;

	public PFSaveCommitCmd(String dsID, Class<? extends ISaveProcessor> saveprocessorClass, Class<? extends ICommitProcessor> commitProcessorClass, Class<? extends AggregatedValueObject> aggVOClass) {
		super(dsID, saveprocessorClass, aggVOClass);
		this.commitProcessorClass = commitProcessorClass;
	}

	public void run() {
		if (AppUtil.getAppAttr("$$$$$has_assign") == null) {
			firstSaveAndCommit();
		} else {
			AppUtil.addAppAttr("$$$$$has_assign", null);
			afterAssignUserPorcess();
		}
	}

	private void firstSaveAndCommit() {
		LfwView viewMain = ViewUtil.getCurrentView();
		Dataset ds = ViewUtil.getDataset(viewMain, getDsID());
		Dataset[] detailDss = getDetailDss();

		Datasets2AggVOSerializer ser = new Datasets2AggVOSerializer();
		doValidate(ds, detailDss);
		AggregatedValueObject aggVo = ser.serialize(ds, detailDss, getAggVOClass().getName());
		if (aggVo == null) {
			return;
		}
		// 20170620 tsy 修复系统标准BUG，休假单，直接点“保存提交时”无法报错信息
		SuperVO parentVO = (SuperVO) aggVo.getParentVO();
		if ((parentVO.getStatus() == 0) && (!org.apache.commons.lang.ArrayUtils.isEmpty(aggVo.getChildrenVO()))) {
			for (CircularlyAccessibleValueObject vo : aggVo.getChildrenVO()) {
				if (vo.getStatus() == 1) {
					parentVO.setStatus(1);
					break;
				}
			}
		}
		// 20170620 end
		String[] removeSessCacheKeys =
				new UifSaveCmd(getDsID(), null, getAggVOClass().getName(), false).fillCachedDeletedVO(aggVo, detailDss);

		try {
			onBeforeVOSave(aggVo);

			if (!checkBeforeVOSave(aggVo)) {
				return;
			}

			AggregatedValueObject newAggVO = onVOSave(aggVo);

			AggregatedValueObject[] newAggVOs = (AggregatedValueObject[]) AppUtil.getAppAttr("splitResult");

			removeSessCacheKeys(removeSessCacheKeys);
			onAfterSave(newAggVO);

			IFlowBizItf flowItf = getFlowBizImplByMdComp(ds, newAggVO);
			if (flowItf == null) {
				CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res", "0c_pub-res0172"), ResHelper.getString("c_pub-res", "0c_pub-res0016"));
			}

			if (-1 != flowItf.getApproveStatus().intValue()) {
				CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res", "0c_pub-res0172"), ResHelper.getString("c_pub-res", "0c_pub-res0017"));
			}

			String pk_group = (String) newAggVO.getParentVO().getAttributeValue("pk_group");
			if ((!StringUtils.isEmpty(pk_group)) && (!pk_group.equals(SessionUtil.getPk_group()))) {
				CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res", "0c_pub-res0173"), ResHelper.getString("c_pub-res", "0c_pub-res0199"));
			}

			String billTypeCode = flowItf.getBilltype();
			String pkHROrg = flowItf.getPkorg();
			Integer iApproveType = getApproveType(pkHROrg, billTypeCode);
			if (iApproveType.intValue() == 2) {
				doCommitApprove(billTypeCode, newAggVO);
				showSucessMessage();

				AppUtil.getCntAppCtx().addExecScript(";try{getTrueParent().reloadData();}catch(e){}");

				CmdInvoker.invoke(new CloseWindowCmd());
			} else {
				if (iApproveType.intValue() == 1) {
					isExistedUserPfFlow(flowItf);
				}

				UserVO[] users = null;
				if (iApproveType.intValue() == 1) {
					users = LfwPfUtil.getAssignUsers("SAVE", billTypeCode, newAggVO, null, null);
				}
				if ((users != null) && (users.length > 0)) {
					AppUtil.addAppAttr("App_newAggVO_PrimaryKey", newAggVO.getParentVO().getPrimaryKey());

					AppUtil.getCntAppCtx().addAppAttribute("NC", UFBoolean.TRUE.toString());

					AppUtil.getCntAppCtx().addAppAttribute("ncAssginUsers", users);
					Map<String, String> paramMap = new HashMap();
					paramMap.put("model", "nc.bs.hrss.pub.assignuser.HrssWfmAssignUserPageModel");

					OpenProperties openWindowProperties = new OpenProperties();
					openWindowProperties.setOpenId("ncassignuser");
					openWindowProperties.setTitle(NCLangRes4VoTransl.getNCLangRes().getStrByID("c_pub-res", "0c_pub-res0020"));

					openWindowProperties.setWidth("800");
					openWindowProperties.setHeight("700");
					openWindowProperties.setParamMap(paramMap);
					openWindowProperties.setType("TYPE_DIALOG");
					openWindowProperties.setPopclose(false);

					AppLifeCycleContext.current().getViewContext().navgateTo(openWindowProperties);

				} else {

					LfwPfUtil.commit(newAggVO, billTypeCode, null, getEParam(iApproveType), null);
					SuperVO returnVO = DatasetUtil.encapsulateVO(ds, (SuperVO) newAggVO.getParentVO());
					new SuperVO2DatasetSerializer().update(new SuperVO[] { returnVO }, ds);
					showSucessMessage();

					AppUtil.getCntAppCtx().addExecScript(";try{getTrueParent().reloadData();}catch(e){}");

					CmdInvoker.invoke(new CloseWindowCmd());
				}
			}

			CmdInvoker.invoke(new UifPlugoutCmd("main", "closewindow"));
		} catch (Exception e) {
			dealWithException(e);
		}
	}

	protected void onAfterSave(AggregatedValueObject newAggVO) {
	}

	protected void afterAssignUserPorcess() {
		Dataset ds = getDataset();
		Row selRow = ds.getSelectedRow();
		String primaryKey = getPrimaryKey(ds, selRow);
		if (StringUtils.isEmpty(primaryKey)) {
			primaryKey = (String) AppUtil.getAppAttr("App_newAggVO_PrimaryKey");
		}
		AggregatedValueObject aggVO = (AggregatedValueObject) getBillVOByPk(primaryKey);

		IFlowBizItf flowItf = getFlowBizImplByMdComp(ds, aggVO);
		String billTypeCode = flowItf.getBilltype();
		String pkHROrg = flowItf.getPkorg();
		Integer iApproveType = getApproveType(pkHROrg, billTypeCode);

		String[] assignPks = (String[]) AppUtil.getAppAttr("ncAssginUsers");
		try {
			LfwPfUtil.commit(aggVO, billTypeCode, null, getEParam(iApproveType), assignPks);
		} catch (Exception e) {
			dealWithException(e);
		}
		showSucessMessage();

		AppUtil.getCntAppCtx().addExecScript(";try{getTrueParent().reloadData();}catch(e){}");

		CmdInvoker.invoke(new CloseWindowCmd());
	}

	protected void showSucessMessage() {
		AppInteractionUtil.showShortMessage(NCLangRes4VoTransl.getNCLangRes().getStrByID("c_pub-res", "0c_pub-res0111"));
	}

	protected boolean checkBeforeCommit(AggregatedValueObject aggVo) throws Exception {
		ICommitProcessor p = getCommitProcessorClass();
		if (p != null) {
			return p.checkBeforeCommit(aggVo);
		}
		return true;
	}

	protected void doCommitApprove(String billTypeCode, AggregatedValueObject aggVO) {
		Dataset ds = getDataset();
		try {
			aggVO = ((IHrPf) ServiceLocator.lookup(IHrPf.class)).commitBill_RequiresNew(aggVO);
			SuperVO returnVO = DatasetUtil.encapsulateVO(ds, (SuperVO) aggVO.getParentVO());
			new SuperVO2DatasetSerializer().update(new SuperVO[] { returnVO }, ds);
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		} catch (HrssException e) {
			Logger.error(e.getMessage(), e);
		}
	}

	public ICommitProcessor getCommitProcessorClass() {
		if (null == this.commitProcessorClass)
			return null;
		try {
			return (ICommitProcessor) this.commitProcessorClass.newInstance();
		} catch (Exception e) {
			new HrssException(e).deal();
		}
		return null;
	}

	public void setCommitProcessorClass(Class<? extends ICommitProcessor> commitProcessorClass) {
		this.commitProcessorClass = commitProcessorClass;
	}
}
