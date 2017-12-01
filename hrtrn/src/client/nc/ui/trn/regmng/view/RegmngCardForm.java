package nc.ui.trn.regmng.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.PubEnv;
import nc.hr.utils.SQLHelper;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.trn.IItemSetAdapter;
import nc.itf.trn.transmng.ITransmngQueryService;
import nc.pub.tools.HiSQLHelper;
import nc.ui.bd.ref.IRefConst;
import nc.ui.hi.pub.HiAppEventConst;
import nc.ui.hr.uif2.view.HrBillFormEditor;
import nc.ui.om.ref.JobGradeRefModel2;
import nc.ui.om.ref.JobRankRefModel;
import nc.ui.om.ref.PostRefModel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardBeforeEditListener;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillItemEvent;
import nc.ui.trn.regmng.model.RegmngAppModel;
import nc.ui.trn.utils.RefUtils;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.UIState;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.TrialVO;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.hi.trnstype.TrnstypeFlowVO;
import nc.vo.om.job.JobVO;
import nc.vo.om.joblevelsys.FilterTypeEnum;
import nc.vo.om.post.PostVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.trn.regmng.RegapplyVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

@SuppressWarnings("restriction")
public class RegmngCardForm extends HrBillFormEditor implements BillCardBeforeEditListener {
	private static final long serialVersionUID = 2249145712203296380L;

	public RegmngCardForm() {
	}

	private void addListeners() {
		getBillCardPanel().addEditListener(this);
		getBillCardPanel().setBillBeforeEditListenerHeadTail(this);
		getModel().addAppEventListener(this);
	}

	public void afterEdit(BillEditEvent e) {
		try {
			if (RegapplyVO.PK_PSNJOB.equals(e.getKey())) {
				setPersonInfo();
			} else if (RegapplyVO.NEWPK_ORG.equals(e.getKey())) {

				setItemValueAndEnable(RegapplyVO.NEWPK_DEPT, null, true);// 部门
				setItemValueAndEnable(RegapplyVO.NEWPK_POST, null, true);// 岗位
				setItemValueAndEnable(RegapplyVO.NEWPK_POSTSERIES, null, true);// 岗位序列

				setItemValueAndEnable(RegapplyVO.NEWPK_JOB, null, true);// 职务
				setItemValueAndEnable(RegapplyVO.NEWPK_JOBGRADE, null, false);// 职级
				setItemValueAndEnable(RegapplyVO.NEWPK_JOBRANK, null, true);// 职等
				setItemValueAndEnable(RegapplyVO.NEWSERIES, null, true);// 职务类别
			} else if (RegapplyVO.NEWPK_DEPT.equals(e.getKey())) {
				setItemValueAndEnable(RegapplyVO.NEWPK_POST, null, true);// 岗位
				setItemValueAndEnable(RegapplyVO.NEWPK_POSTSERIES, null, true);// 岗位序列
				setItemValueAndEnable(RegapplyVO.NEWPK_JOB, null, true);// 职务
				setItemValueAndEnable(RegapplyVO.NEWPK_JOBGRADE, null, false);// 职级
				setItemValueAndEnable(RegapplyVO.NEWPK_JOBRANK, null, true);// 职等
				setItemValueAndEnable(RegapplyVO.NEWSERIES, null, true);// 职务类别
			} else if ("newseries".equals(e.getKey())) {

				String series = (String) getBillCardPanel().getHeadItem("newseries").getValueObject();
				String pk_job = (String) getBillCardPanel().getHeadItem("newpk_job").getValueObject();
				String pk_post = (String) getBillCardPanel().getHeadItem("newpk_post").getValueObject();
				if ((StringUtils.isBlank(pk_job)) && (StringUtils.isNotBlank(series))) {
					String defaultlevel = "";
					String defaultrank = "";
					Map<String, String> resultMap =
							((IPsndocQryService) NCLocator.getInstance().lookup(IPsndocQryService.class)).getDefaultLevelRank(series, pk_job, null, pk_post, null);

					if (!resultMap.isEmpty()) {
						defaultlevel = (String) resultMap.get("defaultlevel");
						defaultrank = (String) resultMap.get("defaultrank");
					}

					setItemValueAndEnable("newpk_jobgrade", defaultlevel, true);
					setItemValueAndEnable("newpk_jobrank", defaultrank, true);
				} else if ((StringUtils.isBlank(pk_job)) && (StringUtils.isBlank(series))) {
					setItemValueAndEnable("newpk_jobgrade", null, true);
					setItemValueAndEnable("newpk_jobrank", null, true);
				}
			} else if ("newpk_postseries".equals(e.getKey())) {

				String pk_postseries = (String) getBillCardPanel().getHeadItem("newpk_postseries").getValueObject();
				String series = (String) getBillCardPanel().getHeadItem("newseries").getValueObject();
				String pk_job = (String) getBillCardPanel().getHeadItem("newpk_job").getValueObject();
				String pk_post = (String) getBillCardPanel().getHeadItem("newpk_post").getValueObject();
				if ((StringUtils.isBlank(pk_job)) && (StringUtils.isBlank(series)) && (StringUtils.isBlank(pk_post)) && (StringUtils.isNotBlank(pk_postseries))) {

					String defaultlevel = "";
					String defaultrank = "";
					Map<String, String> resultMap =
							((IPsndocQryService) NCLocator.getInstance().lookup(IPsndocQryService.class)).getDefaultLevelRank(series, pk_job, pk_postseries, pk_post, null);

					if (!resultMap.isEmpty()) {
						defaultlevel = (String) resultMap.get("defaultlevel");
						defaultrank = (String) resultMap.get("defaultrank");
					}

					setItemValueAndEnable("newpk_jobgrade", defaultlevel, true);
					setItemValueAndEnable("newpk_jobrank", defaultrank, true);
				} else if ((StringUtils.isBlank(pk_job)) && (StringUtils.isBlank(series)) && (StringUtils.isBlank(pk_post)) && (StringUtils.isBlank(pk_postseries))) {

					setItemValueAndEnable("newpk_jobgrade", null, true);
					setItemValueAndEnable("newpk_jobrank", null, true);
				}
			} else if ("newpk_post".equals(e.getKey())) {

				String pk_post = (String) getBillCardPanel().getHeadItem("newpk_post").getValueObject();
				PostVO post = pk_post == null ? null : (PostVO) getService().retrieveByPk(null, PostVO.class, pk_post);
				if (post != null) {
					setItemValueAndEnable("newpk_postseries", post.getPk_postseries(), false);

					setItemValueAndEnable("newpk_job", post.getPk_job(), true);
					JobVO jobVO = post.getPk_job() == null ? null : (JobVO) getService().retrieveByPk(null, JobVO.class, post.getPk_job());
					if (jobVO != null) {
						setItemValueAndEnable("newseries", jobVO.getPk_jobtype(), false);
					}
					if (post.getEmployment() != null) {
						setItemValueAndEnable("newoccupation", post.getEmployment(), true);
					}
					if (post.getWorktype() != null) {
						setItemValueAndEnable("newworktype", post.getWorktype(), true);
					}

					String defaultlevel = "";
					String defaultrank = "";
					Map<String, String> resultMap =
							((IPsndocQryService) NCLocator.getInstance().lookup(IPsndocQryService.class)).getDefaultLevelRank(null, null, null, pk_post, null);

					if (!resultMap.isEmpty()) {
						defaultlevel = (String) resultMap.get("defaultlevel");
						defaultrank = (String) resultMap.get("defaultrank");
					}

					setItemValueAndEnable("newpk_jobgrade", defaultlevel, true);
					setItemValueAndEnable("newpk_jobrank", defaultrank, true);
				} else {

					setItemValueAndEnable("newseries", null, true);
					setItemValueAndEnable("newpk_postseries", null, true);
					setItemValueAndEnable("newpk_job", null, true);
					setItemValueAndEnable("newpk_jobgrade", null, true);
					setItemValueAndEnable("newpk_jobrank", null, true);
				}

				if (post == null) {
					afterEdit(new BillEditEvent(getBillCardPanel().getHeadItem("newpk_job").getComponent(), null, "newpk_job"));
				}

			} else if ("newpk_job".equals(e.getKey())) {

				String pk_job = (String) getBillCardPanel().getHeadItem("newpk_job").getValueObject();
				String pk_post = (String) getBillCardPanel().getHeadItem("newpk_post").getValueObject();
				JobVO job = pk_job == null ? null : (JobVO) getService().retrieveByPk(null, JobVO.class, pk_job);
				if (job != null) {

					String defaultlevel = "";
					String defaultrank = "";
					Map<String, String> resultMap =
							((IPsndocQryService) NCLocator.getInstance().lookup(IPsndocQryService.class)).getDefaultLevelRank(null, pk_job, null, pk_post, null);

					if (!resultMap.isEmpty()) {
						defaultlevel = (String) resultMap.get("defaultlevel");
						defaultrank = (String) resultMap.get("defaultrank");
					}

					setItemValueAndEnable("newseries", job.getPk_jobtype(), false);
					setItemValueAndEnable("newpk_jobgrade", defaultlevel, true);
					setItemValueAndEnable("newpk_jobrank", defaultrank, true);
				} else {
					setItemValueAndEnable("newseries", null, true);
					setItemValueAndEnable("newpk_jobgrade", null, true);

					setItemValueAndEnable("newpk_jobrank", null, true);
				}

			} else if ("newpk_jobgrade".equals(e.getKey())) {

				String pk_joblevel = (String) getBillCardPanel().getHeadItem("newpk_jobgrade").getValueObject();

				if (StringUtils.isNotBlank(pk_joblevel)) {
					String pk_jobType = (String) getBillCardPanel().getHeadItem("newseries").getValueObject();
					String pk_job = (String) getBillCardPanel().getHeadItem("newpk_job").getValueObject();
					String pk_postSeries = (String) getBillCardPanel().getHeadItem("newpk_postseries").getValueObject();
					String pk_post = (String) getBillCardPanel().getHeadItem("newpk_post").getValueObject();
					String defaultrank = "";
					Map<String, String> resultMap =
							((IPsndocQryService) NCLocator.getInstance().lookup(IPsndocQryService.class)).getDefaultLevelRank(pk_jobType, pk_job, pk_postSeries, pk_post, pk_joblevel);

					if (!resultMap.isEmpty()) {
						defaultrank = (String) resultMap.get("defaultrank");
					}
					setItemValueAndEnable("newpk_jobrank", defaultrank, true);

				} else {

					setItemValueAndEnable("newpk_jobrank", null, true);

				}
			} else if ("trialresult".equals(e.getKey())) {

				Integer trialresult = (Integer) getBillCardPanel().getHeadItem("trialresult").getValueObject();
				if ((trialresult != null) && (2 == trialresult.intValue())) {

					setItemValueAndEnable("trialdelaydate", null, true);
					setItemNull("trialdelaydate", true);

				} else {
					setItemValueAndEnable("trialdelaydate", null, false);
					setItemNull("trialdelaydate", false);
				}
			} else if ("transtypeid".equals(e.getKey())) {

				UIRefPane ref = (UIRefPane) getBillCardPanel().getHeadItem("transtypeid").getComponent();
				setItemValueAndEnable("transtype", ref.getRefCode(), false);
			}

			String pk_jobType = (String) getBillCardPanel().getHeadItem("newseries").getValueObject();
			String pk_job = (String) getBillCardPanel().getHeadItem("newpk_job").getValueObject();
			String pk_postSeries = (String) getBillCardPanel().getHeadItem("newpk_postseries").getValueObject();
			String pk_post = (String) getBillCardPanel().getHeadItem("newpk_post").getValueObject();
			if ((StringUtils.isBlank(pk_jobType)) && (StringUtils.isBlank(pk_postSeries)) && (StringUtils.isBlank(pk_job)) && (StringUtils.isBlank(pk_post))) {
				setItemValueAndEnable("newpk_jobgrade", null, false);
			} else {
				getBillCardPanel().getHeadItem("newpk_jobgrade").setEnabled(true);
			}
		} catch (BusinessException ex) {
			Logger.error(ex.getMessage(), ex);
		}
	}

	public boolean beforeEdit(BillItemEvent e) {

		if ("transtypeid".equals(e.getItem().getKey())) {
			((UIRefPane) e.getItem().getComponent()).getRefModel().setUseDataPower(false);

			String where =
					" and ( parentbilltype = '" + getModel().getBillType() + "' and pk_group = '" + getModel().getContext().getPk_group() + "' )";

			String powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), "transtype", "default", "bd_billtype");

			if (!StringUtils.isBlank(powerSql)) {
				where = where + " and " + powerSql;
			}
			((UIRefPane) e.getItem().getComponent()).getRefModel().addWherePart(where);

		} else if ("business_type".equals(e.getItem().getKey())) {

			String where =
					" and busiprop = 6  and ( isnull(pk_org,'~') = '~' or pk_org = '" + getModel().getContext().getPk_org() + "') and validity =1 and primarybilltype like '" + "6111" + "%'";

			((UIRefPane) e.getItem().getComponent()).getRefModel().addWherePart(where);
		} else if (RegapplyVO.PK_PSNJOB.equals(e.getItem().getKey())) {

			String inJobSql =
					" and hi_psnjob.pk_psnjob in " + HiSQLHelper.getInJobSQL(true) + " and hi_psnjob.ismainjob = 'Y' and hi_psnjob.trial_flag = 'Y' and hi_psnjob.trial_type = " + getModel().getProbationType().toString();

			String powerSql =
					HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_6007PSNJOB, IRefConst.DATAPOWEROPERATION_CODE, "hi_psnjob");
			// 20171130 1/3 tsy 添加部门信息权限控制
			String newPowerSql =
					HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_DEPT, IRefConst.DATAPOWEROPERATION_CODE, "org_dept");
			// 20171130 1/3 end 

			try {
				if (!((IPsndocQryService) NCLocator.getInstance().lookup(IPsndocQryService.class)).getValueOFParaDeployCadre(getModel().getContext()).booleanValue()) {
					inJobSql = inJobSql + " and bd_psndoc.iscadre != 'Y'";
				}
			} catch (BusinessException ex) {
				Logger.error(ex.getMessage(), ex);
			}
			if (!StringUtils.isBlank(powerSql)) {
				inJobSql = inJobSql + " and " + powerSql;
			}
			// 20171130 2/3 tsy 添加部门信息权限控制
			if (!StringUtils.isBlank(newPowerSql)) {
				inJobSql = inJobSql + " and " + newPowerSql;
			}
			// 20171130 2/3 end 
			UIRefPane psnRef = (UIRefPane) e.getItem().getComponent();

			psnRef.getRefModel().addWherePart(inJobSql);

		} else if ("pk_tutor".equals(e.getItem().getKey())) {

			String wherePart = " and hi_psnorg.psntype = 0  and hi_psnorg.endflag = 'N'";
			UIRefPane psnRef = (UIRefPane) e.getItem().getComponent();

			psnRef.getRefModel().addWherePart(wherePart);

		} else if ("newpk_org".equals(e.getItem().getKey())) {

			UIRefPane orgRef = (UIRefPane) e.getItem().getComponent();

			orgRef.getRefModel().setUseDataPower(Boolean.FALSE.booleanValue());
			String powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), "60050orginfo", "default", "org_orgs");

			if (!StringUtils.isBlank(powerSql)) {
				orgRef.getRefModel().addWherePart(" and pk_adminorg in ( select pk_org from org_orgs where " + powerSql + ")and org_adminorg.pk_adminorg in (select pk_adminorg from org_admin_enable)");

			} else {

				orgRef.getRefModel().addWherePart(" and org_adminorg.pk_adminorg in (select pk_adminorg from org_admin_enable)");
			}
		} else if ("newpk_dept".equals(e.getItem().getKey())) {

			UIRefPane deptRef = (UIRefPane) e.getItem().getComponent();
			String pk_org = (String) getBillCardPanel().getHeadItem("newpk_org").getValueObject();
			deptRef.setPk_org(pk_org);
			// 20171130 3/3 tsy 去掉转正部门的数据权限
			// String powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), "60050deptinfo", "default", "org_dept");

			String cond = " and hrcanceled = 'N' and depttype <> 1 ";
			//			if (!StringUtils.isBlank(powerSql)) {
			//				cond = cond + " and " + powerSql;
			//			}
			// 20171130 3/3 end
			deptRef.getRefModel().addWherePart(cond);
		} else if ("newpk_psncl".equals(e.getItem().getKey())) {

			UIRefPane psnclRef = (UIRefPane) e.getItem().getComponent();
			String powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), "psncl", "default", "bd_psncl");

			if (!StringUtils.isBlank(powerSql)) {
				psnclRef.getRefModel().addWherePart(" and " + powerSql);
			}
		} else if ("newpk_post".equals(e.getItem().getKey())) {

			UIRefPane postRef = (UIRefPane) e.getItem().getComponent();
			String pk_org = (String) getBillCardPanel().getHeadItem("newpk_org").getValueObject();
			String pk_dept = (String) getBillCardPanel().getHeadItem("newpk_dept").getValueObject();
			PostRefModel postModel = (PostRefModel) postRef.getRefModel();
			postModel.setPk_org(pk_org);
			postModel.setPkdept(pk_dept);
			String cond = " and ( " + SQLHelper.getNullSql("om_post.hrcanceled") + " or " + "om_post" + ".hrcanceled = 'N' ) ";
			// 20171201 1/1 tsy 取消岗位的权限控制
			/*
			String powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), "60050deptinfo", "default", "org_dept");

			if (!StringUtils.isBlank(powerSql)) {
				cond = cond + " and om_post.pk_dept in ( select pk_dept from org_dept where  " + powerSql + " ) ";
			}
			*/
			// 20171201 1/1 end
			postModel.addWherePart(cond);
		} else if ("newpk_job".equals(e.getItem().getKey())) {

			String pk_org = (String) getBillCardPanel().getHeadItem("newpk_org").getValueObject();
			UIRefPane jobRef = (UIRefPane) e.getItem().getComponent();
			if (pk_org != null) {
				jobRef.setPk_org(pk_org);
			} else {
				jobRef.setPk_org(getModel().getContext().getPk_group());
			}
		} else if ("newpk_jobgrade".equals(e.getItem().getKey())) {

			String pk_job = (String) getBillCardPanel().getHeadItem("newpk_job").getValueObject();
			String pk_post = (String) getBillCardPanel().getHeadItem("newpk_post").getValueObject();

			String pk_jobtype = (String) getBillCardPanel().getHeadItem("newseries").getValueObject();
			String pk_postseries = (String) getBillCardPanel().getHeadItem("newpk_postseries").getValueObject();

			BillItem item = (BillItem) e.getSource();
			if (item != null) {
				FilterTypeEnum filterType = null;
				String gradeSource = "";
				Map<String, Object> resultMap = null;
				try {
					resultMap =
							((IPsndocQryService) NCLocator.getInstance().lookup(IPsndocQryService.class)).getLevelRankCondition(pk_jobtype, pk_job, pk_postseries, pk_post);
				} catch (BusinessException e1) {
					Logger.error(e1.getMessage(), e1);
				}

				if (!resultMap.isEmpty()) {
					filterType = (FilterTypeEnum) resultMap.get("filterType");
					gradeSource = (String) resultMap.get("gradeSource");
				}

				((JobGradeRefModel2) ((UIRefPane) item.getComponent()).getRefModel()).setPk_filtertype(gradeSource, filterType);
			}
		} else if ("newpk_jobrank".equals(e.getItem().getKey())) {

			String pk_jobgrade = (String) getBillCardPanel().getHeadItem("newpk_jobgrade").getValueObject();
			BillItem item = e.getItem();

			String pk_job = (String) getBillCardPanel().getHeadItem("newpk_job").getValueObject();
			String pk_post = (String) getBillCardPanel().getHeadItem("newpk_post").getValueObject();
			String pk_jobtype = (String) getBillCardPanel().getHeadItem("newseries").getValueObject();
			String pk_postseries = (String) getBillCardPanel().getHeadItem("newpk_postseries").getValueObject();
			if (item != null) {
				FilterTypeEnum filterType = null;
				String gradeSource = "";
				Map<String, Object> resultMap = null;
				try {
					resultMap =
							((IPsndocQryService) NCLocator.getInstance().lookup(IPsndocQryService.class)).getLevelRankCondition(pk_jobtype, pk_job, pk_postseries, pk_post);
				} catch (BusinessException e1) {
					Logger.error(e1.getMessage(), e1);
				}

				if (!resultMap.isEmpty()) {
					filterType = (FilterTypeEnum) resultMap.get("filterType");
					gradeSource = (String) resultMap.get("gradeSource");
				}
				((JobRankRefModel) ((UIRefPane) item.getComponent()).getRefModel()).setPk_joblevel(pk_jobgrade);
				((JobRankRefModel) ((UIRefPane) item.getComponent()).getRefModel()).setPk_filtertype(gradeSource, filterType);
			}
		} else {
			JComponent ref = e.getItem().getComponent();
			if (e.getItem().getTableCode().equals("newinfo")) {
				if ((ref instanceof UIRefPane)) {

					String pk_org = "";
					BillItem item = getBillCardPanel().getHeadItem("pk_org");
					pk_org = item.getValueObject() == null ? getModel().getContext().getPk_org() : (String) item.getValueObject();
					if (((UIRefPane) ref).getRefModel() != null) {
						((UIRefPane) ref).setPk_org(pk_org);
					}
				}
			}
		}
		return true;
	}

	public boolean canBeHidden() {
		if ((getModel().getUiState() == UIState.ADD) || (getModel().getUiState() == UIState.EDIT)) {
			return false;

		}

		return super.canBeHidden();
	}

	private BillData getBillData() {

		return getBillCardPanel().getBillData();
	}

	public RegmngAppModel getModel() {
		return (RegmngAppModel) super.getModel();
	}

	private IPersistenceRetrieve getService() {
		return (IPersistenceRetrieve) NCLocator.getInstance().lookup(IPersistenceRetrieve.class);
	}

	public void handleEvent(AppEvent event) {
		super.handleEvent(event);
		if (HiAppEventConst.ORG_CHANGED.equals(event.getType())) {
			// 切换组织后 清空转正项目设置信息

			getModel().getHashItemSets().clear();

			showAllSetItems(false);
		}
	}

	public void initUI() {
		super.initUI();
		getBillCardPanel().getHeadItem("begin_date").setEdit(false);
		addListeners();

	}

	protected void onAdd() {
		super.onAdd();
		showAllSetItems(true);

		String trnstype = "1002Z710000000008GSF";
		try {
			TrnstypeFlowVO[] flow =
					(TrnstypeFlowVO[]) ((IPersistenceRetrieve) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)).retrieveByClause(null, TrnstypeFlowVO.class, " pk_group = '" + PubEnv.getPk_group() + "' and pk_trnstype = '" + trnstype + "'");

			if ((flow != null) && (flow.length > 0)) {
				BillItem transtype = getBillCardPanel().getHeadItem("transtypeid");
				BillItem transtypecode = getBillCardPanel().getHeadItem("transtype");
				if ((transtype != null) && (transtypecode != null) && (getModel().getApproveType().intValue() == 1)) {

					transtype.setValue(flow[0].getPk_transtype());
					transtypecode.setValue(null);
					if (flow[0].getPk_transtype() != null) {
						BilltypeVO billtype =
								(BilltypeVO) ((IPersistenceRetrieve) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)).retrieveByPk(null, BilltypeVO.class, flow[0].getPk_transtype());

						transtypecode.setValue(billtype.getPk_billtypecode());
					}

				}

				BillItem busitype = getBillCardPanel().getHeadItem("business_type");
				if ((busitype != null) && (busitype.isShow())) {
					busitype.setValue(flow[0].getPk_businesstype());
				}
			}
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}

	}

	protected void onEdit() {
		super.onEdit();
		showAllSetItems(true);

	}

	/**
	 * 重置billdata,重画单据界面
	 * 
	 * @param billdata
	 */
	private void resetBillData(BillData billdata) {

		getBillCardPanel().setBillData(billdata);
		getBillCardPanel().updateUI();
	}

	public void setEditable(boolean editable) {
		super.setEditable(editable);
		if (getModel().getUiState() == UIState.EDIT) {
			setHeadItemEnable("pk_psnjob", false);
		}

		Integer trialresult = (Integer) getBillCardPanel().getHeadItem("trialresult").getValueObject();
		if ((getModel().getUiState() == UIState.EDIT) && (trialresult != null) && (2 == trialresult.intValue())) {

			setHeadItemEnable("trialdelaydate", editable);
			setItemNull("trialdelaydate", editable);

		} else {

			setHeadItemEnable("trialdelaydate", false);
			setItemNull("trialdelaydate", false);
		}

		setHeadItemEnable("trialresult", editable);
		setItemNull("trialresult", editable);

		if (getModel().isApproveSite()) {
			setHeadItemEnable("bill_code", false);
			setHeadItemEnable("business_type", false);
			setHeadItemEnable("transtypeid", false);
			setHeadItemEnable("apply_date", false);
		} else {
			if (getModel().getUiState() == UIState.ADD) {
				setHeadItemEnable("bill_code", getModel().isBillCodeEditable());
			} else if (getModel().getUiState() == UIState.EDIT) {
				setHeadItemEnable("bill_code", getModel().isBillCodeEditable());
			} else {
				setHeadItemEnable("bill_code", false);
			}
			setHeadItemEnable("business_type", editable);
			setHeadItemEnable("transtypeid", editable);
		}

		if (getModel().getApproveType().intValue() != 1) {
			setHeadItemEnable("transtypeid", false);
		}
	}

	protected void setItemEnabled(String strKey, boolean enabled) {
		if (getBillData().getHeadItem(strKey) != null)
			getBillData().getHeadItem(strKey).setEnabled(enabled);
	}

	protected void setItemNull(String strKey, boolean newNull) {
		if (getBillData().getHeadItem(strKey) != null) {
			getBillData().getHeadItem(strKey).setNull(newNull);
		}
	}

	/**
	 * 当为编辑态时,设置调配项目的可用性
	 */
	public void setItemEnabledOnEdit() {
		BillItem bi = getBillCardPanel().getHeadItem("newpk_post");
		if ((bi != null) && (bi.getValueObject() != null)) {

			setHeadItemEnable("newpk_job", false);

		} else {
			setHeadItemEnable("newpk_job", true);

		}

		setHeadItemEnable("newpk_postseries", false);
		bi = getBillCardPanel().getHeadItem("newpk_job");
		if ((bi != null) && (bi.getValueObject() != null)) {

			setHeadItemEnable("newpk_jobgrade", true);

		} else {

			setHeadItemEnable("newpk_jobgrade", false);
		}
		setHeadItemEnable("newseries", false);
		setHeadItemEnable("newseries", false);
		bi = getBillCardPanel().getHeadItem("newpk_jobgrade");
		if ((bi != null) && (bi.getValueObject() != null)) {

			setHeadItemEnable("newpk_jobrank", false);
		} else {
			setHeadItemEnable("newpk_jobrank", true);
		}
	}

	/**
	 * 设置项目的值与是否可用
	 * 
	 * @param itemKey
	 * @param value
	 * @param isEnable
	 */
	private void setItemValueAndEnable(String itemKey, Object value, boolean isEnable) {
		BillItem item = getBillCardPanel().getHeadItem(itemKey);
		if (item != null) {
			getBillCardPanel().setHeadItem(itemKey, value);
			item.setEnabled(isEnable);
		}
	}

	public void setModel(RegmngAppModel model) {
		super.setModel(model);
	}

	public void setPersonInfo() throws BusinessException {
		BillItem[] olditems = getBillCardPanel().getBillData().getHeadItems("oldinfo");
		BillItem[] newitems = getBillCardPanel().getBillData().getHeadItems("newinfo");
		BillItem[] allItems = (BillItem[]) ArrayUtils.addAll(olditems, newitems);
		UIRefPane psnref = RefUtils.getRefPaneOfItem(getBillCardPanel().getHeadItem("pk_psnjob"));
		String pk_psnjob = psnref.getRefPK();
		if (pk_psnjob == null) {

			for (BillItem i : allItems) {
				i.setValue(null);
			}
			return;
		}
		PsnJobVO psn =
				(PsnJobVO) ((IPersistenceRetrieve) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)).retrieveByPk(null, PsnJobVO.class, pk_psnjob);

		IItemSetAdapter[] itemvos = (IItemSetAdapter[]) getModel().getHashItemSets().get(getModel().getProbationType());
		if (ArrayUtils.isEmpty(itemvos)) {
			return;
		}
		List<String> defaultls = new ArrayList();
		UFBoolean isde = null;
		String key = null;
		for (IItemSetAdapter vo : itemvos) {
			key = vo.getItemkey();
			if (!key.startsWith("old")) {

				isde = vo.getIsdefault();
				if ((isde != null) && (isde.booleanValue())) {
					defaultls.add(key);
				}
			}
		}

		for (BillItem i : allItems) {
			key = i.getKey();
			if ((key.startsWith("old")) || (defaultls.contains(key))) {
				i.setValue(psn.getAttributeValue(key.substring(3)));
			}
		}

		HashMap<String, String> hm =
				((ITransmngQueryService) NCLocator.getInstance().lookup(ITransmngQueryService.class)).getPowerItem(pk_psnjob, false);

		getBillCardPanel().getHeadItem("newpk_psncl").setValue(hm.get("pk_psncl"));

		getBillCardPanel().getHeadItem("newpk_org").setValue(hm.get("pk_org"));

		getBillCardPanel().getHeadItem("newpk_dept").setValue(hm.get("pk_org") == null ? null : (String) hm.get("pk_dept"));

		BillItem jobItem = getBillCardPanel().getHeadItem("newpk_job");
		if ((!defaultls.contains("newpk_job")) || (jobItem == null) || (jobItem.getValueObject() == null)) {
			getBillCardPanel().getHeadItem("newpk_jobgrade").setValue(null);
			getBillCardPanel().getHeadItem("newseries").setValue(null);

		} else {

			String pk_job = jobItem.getValueObject().toString();
			setJobInfo(pk_job, defaultls);
		}

		BillItem postItem = getBillCardPanel().getHeadItem("newpk_post");
		if ((!defaultls.contains("newpk_post")) || (postItem == null) || (postItem.getValueObject() == null)) {
			getBillCardPanel().getHeadItem("newpk_postseries").setValue(null);
		} else {
			String pk_post = postItem.getValueObject().toString();
			PostVO post = pk_post == null ? null : (PostVO) getService().retrieveByPk(null, PostVO.class, pk_post);
			if (post == null) {
				getBillCardPanel().getHeadItem("newpk_postseries").setValue(null);
			} else {
				getBillCardPanel().getHeadItem("newpk_postseries").setValue(post.getPk_postseries());
				if (!defaultls.contains("newpk_job")) {
					setJobInfo(post.getPk_job(), defaultls);
				}
			}
		}

		getBillCardPanel().getHeadItem("pk_psndoc").setValue(psn.getPk_psndoc());
		getBillCardPanel().getHeadItem("pk_psnorg").setValue(psn.getPk_psnorg());
		getBillCardPanel().getHeadItem("assgid").setValue(psn.getAssgid());

		String where = " pk_psnorg = '" + psn.getPk_psnorg() + "' and endflag = 'N' ";
		TrialVO[] trialVOs =
				(TrialVO[]) ((IPersistenceRetrieve) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)).retrieveByClause(null, TrialVO.class, where);

		if ((trialVOs != null) && (trialVOs.length > 0)) {
			getBillCardPanel().getHeadItem("begin_date").setValue(trialVOs[0].getBegindate());
			getBillCardPanel().getHeadItem("end_date").setValue(trialVOs[0].getEnddate());
			getBillCardPanel().getHeadItem("regulardate").setValue(trialVOs[0].getRegulardate());
		} else {
			getBillCardPanel().getHeadItem("begin_date").setValue(null);
			getBillCardPanel().getHeadItem("end_date").setValue(null);
			getBillCardPanel().getHeadItem("regulardate").setValue(null);
		}

	}

	private void setJobInfo(String pk_job, List<String> defaultls) throws BusinessException {

		JobVO job = pk_job == null ? null : (JobVO) getService().retrieveByPk(null, JobVO.class, pk_job);
		if (job == null) {
			getBillCardPanel().getHeadItem("newpk_jobgrade").setValue(null);
			getBillCardPanel().getHeadItem("newseries").setValue(null);
		} else {
			getBillCardPanel().getHeadItem("newseries").setValue(job.getPk_jobtype());
			String pk_jobgrade = (String) getHeadItemValue("newpk_jobgrade");
			if ((defaultls.contains("newpk_jobgrade")) && (pk_jobgrade != null)) {
				String pk_jobType = (String) getBillCardPanel().getHeadItem("newseries").getValueObject();
				String pk_postSeries = (String) getBillCardPanel().getHeadItem("newpk_postseries").getValueObject();
				String pk_post = (String) getBillCardPanel().getHeadItem("newpk_post").getValueObject();
				String defaultrank = "";
				Map<String, String> resultMap =
						((IPsndocQryService) NCLocator.getInstance().lookup(IPsndocQryService.class)).getDefaultLevelRank(pk_jobType, pk_job, pk_postSeries, pk_post, pk_jobgrade);

				if (!resultMap.isEmpty()) {
					defaultrank = (String) resultMap.get("defaultrank");
				}
				setItemValueAndEnable("newpk_jobrank", defaultrank, true);
			}
		}
	}

	/**
	 * 根据项目设置重画单据项目设置分组
	 */
	public void showAllSetItems(boolean editstate) {

		BillData billdata = getBillCardPanel().getBillData();
		BillTabVO oldtab = billdata.getTabVO(0, "oldinfo");
		BillTabVO newtab = billdata.getTabVO(0, "newinfo");
		if ((oldtab == null) || (newtab == null)) {
			return;
		}
		BillItem[] olditems = billdata.getHeadItems("oldinfo");
		BillItem[] newitems = billdata.getHeadItems("newinfo");
		BillItem[] allItems = (BillItem[]) ArrayUtils.addAll(olditems, newitems);

		Object probationType = getBillCardPanel().getHeadItem("probation_type").getValueObject();

		probationType = probationType == null ? getModel().getProbationType() : probationType;
		IItemSetAdapter[] itemvos = (IItemSetAdapter[]) getModel().getHashItemSets().get(probationType);
		if ((itemvos == null) || (itemvos.length == 0)) {

			getModel().queryRegItems(probationType);
			itemvos = (IItemSetAdapter[]) getModel().getHashItemSets().get(probationType);
		}

		if (ArrayUtils.isEmpty(itemvos)) {
			for (BillItem item : allItems) {
				item.setShow(false);
			}
			oldtab.setMixindex(Integer.valueOf(2));
			newtab.setMixindex(Integer.valueOf(2));
			resetBillData(billdata);
			return;
		}

		for (BillItem item : allItems) {
			item.setNull(false);
			item.setShow(false);
			if (("oldpk_org".equals(item.getKey())) || ("oldpk_dept".equals(item.getKey()))) {

				item.setShow(true);
				item.setNull(false);
				item.setEdit(false);
				item.setEnabled(false);

			} else if (("newpk_org".equals(item.getKey())) || ("newpk_dept".equals(item.getKey()))) {

				item.setShow(true);
				item.setNull(true);
				item.setEdit(editstate);
				item.setEnabled(editstate);

			}
		}

		boolean isedit = false;
		boolean notnull = false;
		String key = null;
		BillItem item = null;
		for (IItemSetAdapter itemvo : itemvos) {
			key = itemvo.getItemkey();
			isedit = itemvo.getIsedit().booleanValue();
			notnull = itemvo.getIsnotnull().booleanValue();
			item = billdata.getHeadItem(key);
			if (item != null) {
				if (("newpk_org".equals(item.getKey())) || ("newpk_psncl".equals(item.getKey())) || ("newpk_dept".equals(item.getKey()))) {

					item.setShow(true);
					item.setNull(true);
					item.setEdit(editstate);
					item.setEnabled(editstate);

				} else {

					item.setShow((key.startsWith("old")) || ((key.startsWith("new")) && (isedit)));
					item.setNull((key.startsWith("new")) && (notnull));
					item.setEdit((key.startsWith("new")) && (isedit) && (editstate));
					item.setEnabled((key.startsWith("new")) && (isedit) && (editstate));
				}
			}
		}
		oldtab.setMixindex(Integer.valueOf(1));
		newtab.setMixindex(Integer.valueOf(1));
		resetBillData(billdata);
	}

	protected void synchronizeDataFromModel() {
		super.synchronizeDataFromModel();
		if (getModel().getSelectedData() != null) {
			showAllSetItems(false);
		}
	}
}
