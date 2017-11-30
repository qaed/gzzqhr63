package nc.ui.trn.transmgt.pub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.PubEnv;
import nc.hr.utils.SQLHelper;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.trn.IItemSetAdapter;
import nc.itf.trn.transmng.ITransmngQueryService;
import nc.pub.tools.HiSQLHelper;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.IRefConst;
import nc.ui.hr.uif2.view.HrBillFormEditor;
import nc.ui.om.ref.JobGradeRefModel2;
import nc.ui.om.ref.JobRankRefModel;
import nc.ui.om.ref.PostRefModel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardBeforeEditListener;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillItemEvent;
import nc.ui.trn.utils.RefUtils;
import nc.ui.uif2.DefaultExceptionHanler;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.uif2.UIState;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.hi.trnstype.TrnstypeFlowVO;
import nc.vo.om.job.JobGradeVO;
import nc.vo.om.job.JobVO;
import nc.vo.om.joblevelsys.FilterTypeEnum;
import nc.vo.om.joblevelsys.JobLevelVO;
import nc.vo.om.post.PostVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.uif2.LoginContext;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class TrnBillFormEditor extends HrBillFormEditor implements BillCardBeforeEditListener {
	private static final long serialVersionUID = 1L;
	private IExceptionHandler defaultExceptionHandler = null;

	private SimpleDocServiceTemplate service = null;
	private String[] psnjobPKs;

	public void afterEdit(BillEditEvent e) {
		try {
			if ("pk_psnjob".equals(e.getKey())) {
				setPersonInfo();
			} else if ("newpk_org".equals(e.getKey())) {
				setItemValueAndEnable("newpk_dept", null, true);
				setItemValueAndEnable("newpk_post", null, true);
				setItemValueAndEnable("newpk_job", null, true);
				setItemValueAndEnable("newpk_jobgrade", null, false);
				setItemValueAndEnable("newpk_jobrank", null, true);
				BillItem item = getBillCardPanel().getHeadItem("stapply_mode");
				if ((item != null) && (((Integer) item.getValueObject()).intValue() == 2) && (!getModel().isApproveSite())) {
					setItemValueAndEnable("pk_hi_org", null, true);
				}
			} else if ("newpk_dept".equals(e.getKey())) {
				setItemValueAndEnable("newpk_post", null, true);
				setItemValueAndEnable("newpk_job", null, true);
				setItemValueAndEnable("newpk_jobgrade", null, false);
				setItemValueAndEnable("newpk_jobrank", null, true);
				BillItem item = getBillCardPanel().getHeadItem("stapply_mode");
				if ((item != null) && (((Integer) item.getValueObject()).intValue() == 2) && (!getModel().isApproveSite())) {
					String pk_dept = (String) getBillCardPanel().getHeadItem("newpk_dept").getValueObject();
					String pk_org = HiSQLHelper.getHrorgBydept(pk_dept);
					pk_org = getModel().getContext().getPk_org().equals(pk_org) ? null : pk_org;
					setItemValueAndEnable("pk_hi_org", pk_org, true);
				}

			} else if ("newpk_post".equals(e.getKey())) {
				String pk_post = (String) getBillCardPanel().getHeadItem("newpk_post").getValueObject();
				PostVO post = pk_post == null ? null : (PostVO) getService().queryByPk(PostVO.class, pk_post, true);
				if (post != null) {
					setItemValueAndEnable("newpk_postseries", post.getPk_postseries(), false);
					setItemValueAndEnable("newpk_job", post.getPk_job(), true);
					JobVO jobVO = post.getPk_job() == null ? null : (JobVO) getService().queryByPk(JobVO.class, post.getPk_job());
					if (jobVO != null) {
						setItemValueAndEnable("newseries", jobVO.getPk_jobtype(), true);
					}
					if (post.getEmployment() != null) {
						setItemValueAndEnable("newoccupation", post.getEmployment(), true);
					}
					if (post.getWorktype() != null) {
						setItemValueAndEnable("newworktype", post.getWorktype(), true);
					}

					String defaultlevel = "";
					String defaultrank = "";
					Map resultMap =
							((IPsndocQryService) NCLocator.getInstance().lookup(IPsndocQryService.class)).getDefaultLevelRank(null, null, null, pk_post, null);

					if (!resultMap.isEmpty()) {
						defaultlevel = (String) resultMap.get("defaultlevel");
						defaultrank = (String) resultMap.get("defaultrank");
					}

					setItemValueAndEnable("newseries", null, false);
					setItemValueAndEnable("newpk_jobgrade", defaultlevel, true);
					setItemValueAndEnable("newpk_jobrank", defaultrank, true);
				} else {
					setItemValueAndEnable("newseries", null, true);
					setItemValueAndEnable("newpk_postseries", null, true);
					setItemValueAndEnable("newpk_jobgrade", null, true);
					setItemValueAndEnable("newpk_jobrank", null, true);
					setItemValueAndEnable("newpk_postseries", null, true);
					setItemValueAndEnable("newpk_job", null, true);
				}
				if (post == null) {
					afterEdit(new BillEditEvent(getBillCardPanel().getHeadItem("newpk_job").getComponent(), null, "newpk_job"));
				}

			} else if ("newpk_job".equals(e.getKey())) {
				String pk_job = (String) getBillCardPanel().getHeadItem("newpk_job").getValueObject();
				String pk_post = (String) getBillCardPanel().getHeadItem("newpk_post").getValueObject();
				JobVO job = pk_job == null ? null : (JobVO) getService().queryByPk(JobVO.class, pk_job, true);
				if (job != null) {
					String defaultlevel = "";
					String defaultrank = "";
					Map resultMap =
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
				String pk_jobgrage = (String) getBillCardPanel().getHeadItem("newpk_jobgrade").getValueObject();
				JobLevelVO jobgrade = pk_jobgrage == null ? null : (JobLevelVO) getService().queryByPk(JobLevelVO.class, pk_jobgrage, true);
				if (jobgrade != null) {
					String pk_postseries = (String) getBillCardPanel().getHeadItem("newpk_postseries").getValueObject();
					String pk_job = (String) getBillCardPanel().getHeadItem("newpk_job").getValueObject();
					String pk_post = (String) getBillCardPanel().getHeadItem("newpk_post").getValueObject();
					String series = (String) getBillCardPanel().getHeadItem("newseries").getValueObject();
					String defaultrank = "";
					Map resultMap =
							((IPsndocQryService) NCLocator.getInstance().lookup(IPsndocQryService.class)).getDefaultLevelRank(series, pk_job, pk_postseries, pk_post, pk_jobgrage);

					if (!resultMap.isEmpty()) {
						defaultrank = (String) resultMap.get("defaultrank");
					}

					setItemValueAndEnable("newpk_jobrank", defaultrank, true);
				} else {
					setItemValueAndEnable("newpk_jobrank", null, true);
				}
			} else if ("newseries".equals(e.getKey())) {
				String series = (String) getBillCardPanel().getHeadItem("newseries").getValueObject();
				String pk_job = (String) getBillCardPanel().getHeadItem("newpk_job").getValueObject();
				String pk_post = (String) getBillCardPanel().getHeadItem("newpk_post").getValueObject();
				if ((StringUtils.isBlank(pk_job)) && (StringUtils.isNotBlank(series))) {
					String defaultlevel = "";
					String defaultrank = "";
					Map resultMap =
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
				String pk_job = (String) getBillCardPanel().getHeadItem("newpk_job").getValueObject();
				String pk_post = (String) getBillCardPanel().getHeadItem("newpk_post").getValueObject();
				String series = (String) getBillCardPanel().getHeadItem("newseries").getValueObject();
				if ((StringUtils.isBlank(pk_job)) && (StringUtils.isBlank(series)) && (StringUtils.isBlank(pk_post)) && (StringUtils.isNotBlank(pk_postseries))) {
					String defaultlevel = "";
					String defaultrank = "";
					Map resultMap =
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
			} else if ("transtypeid".equals(e.getKey())) {
				UIRefPane ref = (UIRefPane) getBillCardPanel().getHeadItem("transtypeid").getComponent();
				setItemValueAndEnable("transtype", ref.getRefCode(), false);
			}
		} catch (BusinessException ex) {
			Logger.error(ex.getMessage(), ex);
		}
	}

	public boolean beforeEdit(BillItemEvent e) {
		if ("transtypeid".equals(e.getItem().getKey())) {
			((UIRefPane) e.getItem().getComponent()).getRefModel().setUseDataPower(false);
			String where =
					new StringBuilder().append(" and ( parentbilltype = '").append(getModel().getBillType()).append("' and pk_group = '").append(getModel().getContext().getPk_group()).append("' )").toString();

			String powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), "transtype", "default", "bd_billtype");

			if (!StringUtils.isBlank(powerSql)) {
				where = new StringBuilder().append(where).append(" and ").append(powerSql).toString();
			}
			((UIRefPane) e.getItem().getComponent()).getRefModel().addWherePart(where);
		} else if ("business_type".equals(e.getItem().getKey())) {
			String where =
					new StringBuilder().append(" and busiprop = 6  and ( isnull(pk_org,'~') = '~' or pk_org = '").append(getModel().getContext().getPk_org()).append("') and validity =1 and primarybilltype like '").append(getModel().getBillType()).append("%'").toString();

			((UIRefPane) e.getItem().getComponent()).getRefModel().addWherePart(where);
		} else if ("pk_psnjob".equals(e.getItem().getKey())) {
			String inJobSql = new StringBuilder().append(" and hi_psnjob.pk_psnjob in ").append(HiSQLHelper.getInJobSQL(true)).toString();
			UIRefPane psnRef = (UIRefPane) e.getItem().getComponent();
			String condition = "";
			String powerSql = null;
			// 20171130 1/2 tsy 添加部门信息权限控制
			String newPowerSql =
					HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_DEPT, IRefConst.DATAPOWEROPERATION_CODE, "org_dept");
			if (!StringUtils.isBlank(newPowerSql)) {
				inJobSql = inJobSql + " and " + newPowerSql;
			}
			// 20171130 1/2 end 
			if (getModel().getStapply_mode().intValue() == 3) {
				psnRef.getRefModel().setUseDataPower(Boolean.FALSE.booleanValue());

				powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), "6007psnjob", "tansdefault", "hi_psnjob");

				condition =
						new StringBuilder().append(" and hi_psnjob.pk_group = '").append(getModel().getContext().getPk_group()).append("' and hi_psnjob.pk_hrorg <> '").append(getModel().getContext().getPk_org()).append("' and hi_psnjob.lastflag = 'Y' and hi_psnjob.ismainjob = 'Y' ").append(inJobSql).append(StringUtils.isBlank(powerSql) ? "" : new StringBuilder().append(" and ").append(powerSql).toString()).toString();
			} else {
				powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), "6007psnjob", "default", "hi_psnjob");

				condition =
						new StringBuilder().append(inJobSql).append(StringUtils.isBlank(powerSql) ? "" : new StringBuilder().append(" and ").append(powerSql).toString()).toString();
			}

			try {
				if (!((IPsndocQryService) NCLocator.getInstance().lookup(IPsndocQryService.class)).getValueOFParaDeployCadre(getModel().getContext()).booleanValue()) {
					condition =
							new StringBuilder().append(condition).append(" and (bd_psndoc.iscadre = 'N' or bd_psndoc.iscadre is null) ").toString();
				}
			} catch (BusinessException ex) {
				Logger.error(ex.getMessage(), ex);
			}
			psnRef.getRefModel().addWherePart(condition);
		} else if ("newpk_org".equals(e.getItem().getKey())) {
			UIRefPane orgRef = (UIRefPane) e.getItem().getComponent();

			orgRef.getRefModel().setUseDataPower(Boolean.FALSE.booleanValue());
			String powerSql = "";
			if ((getModel().getStapply_mode().intValue() == 2) && ("6113".equals(getModel().getBillType()))) {
				powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), "60050orginfo", "tansdefault", "org_orgs");
			} else {
				powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), "60050orginfo", "default", "org_orgs");
			}

			if (!StringUtils.isBlank(powerSql)) {
				orgRef.getRefModel().addWherePart(new StringBuilder().append(" and pk_adminorg in ( select pk_org from org_orgs where ").append(powerSql).append(") and org_adminorg.pk_adminorg in (select pk_adminorg from org_admin_enable)").toString());
			} else {
				orgRef.getRefModel().addWherePart(" and org_adminorg.pk_adminorg in (select pk_adminorg from org_admin_enable)");
			}
		} else if ("newpk_dept".equals(e.getItem().getKey())) {
			UIRefPane deptRef = (UIRefPane) e.getItem().getComponent();

			deptRef.getRefModel().setUseDataPower(Boolean.FALSE.booleanValue());
			String pk_org = (String) getBillCardPanel().getHeadItem("newpk_org").getValueObject();
			deptRef.setPk_org(pk_org);
			String cond = " and hrcanceled = 'N' and depttype <> 1 ";
			// 20171130 2/2 tsy 去掉转正部门的数据权限
			//			String powerSql = "";
			//			if ((getModel().getStapply_mode().intValue() == 2) && ("6113".equals(getModel().getBillType()))) {
			//				powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), "60050deptinfo", "tansdefault", "org_dept");
			//			} else {
			//				powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), "60050deptinfo", "default", "org_dept");
			//			}
			//
			//			if (!StringUtils.isBlank(powerSql)) {
			//				cond = new StringBuilder().append(cond).append(" and ").append(powerSql).toString();
			//			}
			// 20171130 2/2 tsy 去掉转正部门的数据权限
			deptRef.getRefModel().addWherePart(cond);
		} else if ("newpk_psncl".equals(e.getItem().getKey())) {
			UIRefPane psnclRef = (UIRefPane) e.getItem().getComponent();
			String powerSql = "";
			if ((getModel().getStapply_mode().intValue() == 2) && ("6113".equals(getModel().getBillType()))) {
				psnclRef.getRefModel().setDataPowerOperation_code("tansdefault");
				powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), "psncl", "tansdefault", "bd_psncl");
			} else {
				psnclRef.getRefModel().setDataPowerOperation_code("default");
				powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), "psncl", "default", "bd_psncl");
			}

			if (!StringUtils.isBlank(powerSql)) {
				psnclRef.getRefModel().addWherePart(new StringBuilder().append(" and ").append(powerSql).toString());
			}
		} else if ("newpk_post".equals(e.getItem().getKey())) {
			UIRefPane postRef = (UIRefPane) e.getItem().getComponent();
			String pk_org = (String) getBillCardPanel().getHeadItem("newpk_org").getValueObject();
			String pk_dept = (String) getBillCardPanel().getHeadItem("newpk_dept").getValueObject();
			PostRefModel postModel = (PostRefModel) postRef.getRefModel();
			postModel.setPk_org(pk_org);
			postModel.setPkdept(pk_dept);
			String cond =
					new StringBuilder().append(" and ( ").append(SQLHelper.getNullSql("om_post.hrcanceled")).append(" or ").append("om_post").append(".hrcanceled = 'N' ) ").toString();

			String powerSql = "";
			if ((getModel().getStapply_mode().intValue() == 2) && ("6113".equals(getModel().getBillType()))) {
				powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), "60050deptinfo", "tansdefault", "org_dept");
			} else {
				powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), "60050deptinfo", "default", "org_dept");
			}

			if (!StringUtils.isBlank(powerSql)) {
				cond =
						new StringBuilder().append(cond).append(" and om_post.pk_dept in ( select pk_dept from org_dept where  ").append(powerSql).append(" ) ").toString();
			}
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
			String pk_postseries = (String) getBillCardPanel().getHeadItem("newpk_postseries").getValueObject();
			String pk_job = (String) getBillCardPanel().getHeadItem("newpk_job").getValueObject();
			String pk_post = (String) getBillCardPanel().getHeadItem("newpk_post").getValueObject();
			String series = (String) getBillCardPanel().getHeadItem("newseries").getValueObject();
			BillItem item = (BillItem) e.getSource();
			if (item != null) {
				FilterTypeEnum filterType = null;
				String gradeSource = "";
				Map resultMap = null;
				try {
					resultMap =
							((IPsndocQryService) NCLocator.getInstance().lookup(IPsndocQryService.class)).getLevelRankCondition(series, pk_job, pk_postseries, pk_post);
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
			BillItem item = e.getItem();
			String pk_jobgrade = (String) getBillCardPanel().getHeadItem("newpk_jobgrade").getValueObject();
			String pk_postseries = (String) getBillCardPanel().getHeadItem("newpk_postseries").getValueObject();
			String pk_job = (String) getBillCardPanel().getHeadItem("newpk_job").getValueObject();
			String pk_post = (String) getBillCardPanel().getHeadItem("newpk_post").getValueObject();
			String series = (String) getBillCardPanel().getHeadItem("newseries").getValueObject();
			if (item != null) {
				FilterTypeEnum filterType = null;
				String gradeSource = "";
				Map resultMap = null;
				try {
					resultMap =
							((IPsndocQryService) NCLocator.getInstance().lookup(IPsndocQryService.class)).getLevelRankCondition(series, pk_job, pk_postseries, pk_post);
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
		} else if ("pk_hi_org".equals(e.getItem().getKey())) {
			UIRefPane ref = (UIRefPane) e.getItem().getComponent();
			ref.getRefModel().addWherePart(new StringBuilder().append(" and pk_hrorg <> '").append(getModel().getContext().getPk_org()).append("' and pk_hrorg in (select pk_adminorg from org_admin_enable) ").toString());
		} else {
			JComponent ref = e.getItem().getComponent();
			if (e.getItem().getTableCode().equals("newinfo")) {
				if ((ref instanceof UIRefPane)) {
					int transMode = -1;
					BillItem item = getBillCardPanel().getHeadItem("stapply_mode");
					if (item != null) {
						transMode = ((Integer) item.getValueObject()).intValue();
					}
					String pk_org = "";
					item = getBillCardPanel().getHeadItem("pk_hi_org");
					pk_org = item.getValueObject() == null ? getModel().getContext().getPk_org() : (String) item.getValueObject();
					if ((!getModel().isApproveSite()) && (2 == transMode)) {
						if (((UIRefPane) ref).getRefModel() != null) {
							((UIRefPane) ref).setPk_org(getModel().getContext().getPk_group());
						}

					} else if (((UIRefPane) ref).getRefModel() != null) {
						((UIRefPane) ref).setPk_org(pk_org);
					}
				}
			}
		}

		return true;
	}

	public void bodyRowChange(BillEditEvent e) {
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

	public IExceptionHandler getDefaultExceptionHandler() {
		if (this.defaultExceptionHandler == null) {
			this.defaultExceptionHandler = new DefaultExceptionHanler(getModel().getContext().getEntranceUI());
		}
		return this.defaultExceptionHandler;
	}

	private SimpleDocServiceTemplate getService() {
		if (this.service == null) {
			this.service = new SimpleDocServiceTemplate("TrnBillFormEditor");
		}
		return this.service;
	}

	public AbstractTrnPFAppModel getModel() {
		return (AbstractTrnPFAppModel) super.getModel();
	}

	public void initUI() {
		super.initUI();

		getBillCardPanel().setBillBeforeEditListenerHeadTail(this);
	}

	protected void onAdd() {
		super.onAdd();
		showAllSetItems(true);
		UIRefPane psnRef = (UIRefPane) getBillCardPanel().getHeadItem("pk_psnjob").getComponent();
		if (getModel().getStapply_mode().intValue() == 3) {
			psnRef.setRefNodeName("人员工作记录(行政树)");
		} else {
			psnRef.setRefNodeName("人员工作记录(左树不含下级HR)");
			psnRef.setPk_org(getModel().getContext().getPk_org());
		}

		getBillCardPanel().getHeadItem("newpk_postseries").setEnabled(true);
		String trnstype = (String) getBillCardPanel().getHeadItem("pk_trnstype").getValueObject();
		try {
			TrnstypeFlowVO[] flow =
					(TrnstypeFlowVO[]) (TrnstypeFlowVO[]) ((IPersistenceRetrieve) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)).retrieveByClause(null, TrnstypeFlowVO.class, new StringBuilder().append(" pk_group = '").append(PubEnv.getPk_group()).append("' and pk_trnstype = '").append(trnstype).append("'").toString());

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

	private void resetBillData(BillData billdata) {
		getBillCardPanel().setBillData(billdata);
		getBillCardPanel().updateUI();
	}

	protected void setDefaultValue() {
		super.setDefaultValue();
		getBillCardPanel().setHeadItem("pk_trnstype", getModel().getTrnstype());
		Integer tranMode = getModel().getStapply_mode();
		if ((tranMode != null) && (2 == tranMode.intValue())) {
			getBillCardPanel().getHeadItem("pk_hi_org").setEnabled(true);
			getBillCardPanel().setHeadItem("pk_hi_org", null);
		} else {
			getBillCardPanel().getHeadItem("pk_hi_org").setEnabled(false);
		}
	}

	public void setEditable(boolean editable) {
		super.setEditable(editable);
		if (getModel().getUiState() == UIState.ADD) {
			setHeadItemEnable("bill_code", getModel().isBillCodeEditable());
		}
		if (getModel().getUiState() == UIState.EDIT) {
			setHeadItemEnable("pk_trnstype", false);
			setHeadItemEnable("pk_psnjob", false);
			setHeadItemEnable("pk_hi_org", false);
			if (getModel().isApproveSite()) {
				setHeadItemEnable("transtypeid", false);
				setHeadItemEnable("business_type", false);
			} else {
				setHeadItemEnable("bill_code", getModel().isBillCodeEditable());
			}
		}

		if (getModel().isApproveSite()) {
			setHeadItemEnable("bill_code", false);
			setHeadItemEnable("business_type", false);
			setHeadItemEnable("transtypeid", false);
			setHeadItemEnable("apply_date", false);
		}

		if (getModel().getApproveType().intValue() != 1) {
			setHeadItemEnable("transtypeid", false);
		}
	}

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
		bi = getBillCardPanel().getHeadItem("newpk_jobgrade");
		if ((bi != null) && (bi.getValueObject() != null)) {
			setHeadItemEnable("newpk_jobrank", false);
		} else {
			setHeadItemEnable("newpk_jobrank", true);
		}
	}

	private void setItemValueAndEnable(String itemKey, Object value, boolean isEnable) {
		BillItem item = getBillCardPanel().getHeadItem(itemKey);
		if (item != null) {
			getBillCardPanel().setHeadItem(itemKey, value);
			item.setEnabled(isEnable);
		}
	}

	public void setModel(AbstractTrnPFAppModel model) {
		super.setModel(model);
	}

	public void setPersonInfo() throws BusinessException {
		BillItem[] olditems = getBillCardPanel().getBillData().getHeadItems("oldinfo");
		BillItem[] newitems = getBillCardPanel().getBillData().getHeadItems("newinfo");
		BillItem[] allItems = (BillItem[]) (BillItem[]) ArrayUtils.addAll(olditems, newitems);
		UIRefPane psnref = RefUtils.getRefPaneOfItem(getBillCardPanel().getHeadItem("pk_psnjob"));
		String pk_psnjob = psnref.getRefPK();
		if (pk_psnjob == null) {
			for (BillItem i : allItems) {
				i.setValue(null);
			}
			return;
		}

		for (BillItem i : newitems) {
			i.setValue(null);
		}
		PsnJobVO psn = (PsnJobVO) getService().queryByPk(PsnJobVO.class, pk_psnjob, true);
		Object trnstype = getBillData().getHeadItem("pk_trnstype").getValueObject();

		IItemSetAdapter[] itemvos = (IItemSetAdapter[]) getModel().getHashItemSets().get(trnstype);
		if (ArrayUtils.isEmpty(itemvos)) {
			return;
		}
		List defaultls = new ArrayList();
		UFBoolean isde = null;
		String key = null;
		for (IItemSetAdapter vo : itemvos) {
			key = vo.getItemkey();
			if (key.startsWith("old")) {
				continue;
			}
			isde = vo.getIsdefault();
			if ((isde == null) || (!isde.booleanValue()))
				continue;
			defaultls.add(key);
		}

		for (BillItem i : allItems) {
			key = i.getKey();
			if ((!key.startsWith("old")) && (!defaultls.contains(key)))
				continue;
			i.setValue(psn.getAttributeValue(i.getKey().substring(3)));
		}

		HashMap hm =
				((ITransmngQueryService) NCLocator.getInstance().lookup(ITransmngQueryService.class)).getPowerItem(pk_psnjob, (getModel().getStapply_mode().intValue() == 2) && ("6113".equals(getModel().getBillType())));

		if (defaultls.contains("newpk_psncl")) {
			getBillCardPanel().getHeadItem("newpk_psncl").setValue(hm.get("pk_psncl"));
		}
		if (defaultls.contains("newpk_dept")) {
			getBillCardPanel().getHeadItem("newpk_org").setValue(hm.get("pk_org"));
		}
		if (defaultls.contains("newpk_dept")) {
			getBillCardPanel().getHeadItem("newpk_dept").setValue(hm.get("pk_org") == null ? null : (String) hm.get("pk_dept"));
		}

		BillItem jobItem = getBillCardPanel().getHeadItem("newpk_job");
		if ((jobItem == null) || (jobItem.getValueObject() == null)) {
			getBillCardPanel().getHeadItem("newseries").setValue(null);
			getBillCardPanel().getHeadItem("newpk_jobgrade").setValue(null);
		} else {
			String pk_job = jobItem.getValueObject().toString();
			setJobInfo(pk_job, defaultls);
		}

		BillItem postItem = getBillCardPanel().getHeadItem("newpk_post");
		if ((!defaultls.contains("newpk_post")) || (postItem == null) || (postItem.getValueObject() == null)) {
			getBillCardPanel().getHeadItem("newpk_postseries").setValue(null);
		} else {
			String pk_post = postItem.getValueObject().toString();
			PostVO post = pk_post == null ? null : (PostVO) getService().queryByPk(PostVO.class, pk_post, true);
			if (post == null) {
				getBillCardPanel().getHeadItem("newpk_postseries").setValue(null);
			} else {
				getBillCardPanel().getHeadItem("newpk_postseries").setValue(post.getPk_postseries());
				if (!defaultls.contains("newpk_job")) {
					setJobInfo(post.getPk_job(), defaultls);
				}
			}

		}

		BillItem bi = getBillCardPanel().getHeadItem("newpoststat");
		if ((!defaultls.contains("newpoststat")) && (bi != null) && (!bi.isShow())) {
			bi.setValue(UFBoolean.TRUE);
		}

		getBillCardPanel().getHeadItem("pk_psndoc").setValue(psn.getPk_psndoc());
		getBillCardPanel().getHeadItem("pk_psnorg").setValue(psn.getPk_psnorg());
		getBillCardPanel().getHeadItem("assgid").setValue(psn.getAssgid());
		Integer transMode = null;
		if (getBillCardPanel().getHeadItem("stapply_mode") != null) {
			transMode = (Integer) getBillCardPanel().getHeadItem("stapply_mode").getValueObject();
		}
		if ((transMode == null) || (1 == transMode.intValue()) || (2 == transMode.intValue())) {
			getBillCardPanel().getHeadItem("pk_old_hi_org").setValue(getModel().getContext().getPk_org());
		} else {
			getBillCardPanel().getHeadItem("pk_old_hi_org").setValue(HiSQLHelper.getHrorg(psn.getPk_psnorg(), psn.getAssgid()));
		}

		if ((transMode == null) || (1 == transMode.intValue()) || (3 == transMode.intValue())) {
			getBillCardPanel().getHeadItem("pk_hi_org").setValue(getModel().getContext().getPk_org());
		}
	}

	private void setJobInfo(String pk_job, List<String> defaultls) throws BusinessException {
		JobVO job = pk_job == null ? null : (JobVO) getService().queryByPk(JobVO.class, pk_job, true);
		if (job == null) {
			getBillCardPanel().getHeadItem("newpk_jobgrade").setValue(null);
			getBillCardPanel().getHeadItem("newseries").setValue(null);
		} else {
			getBillCardPanel().getHeadItem("newseries").setValue(job.getPk_jobtype());

			String pk_jobgrade = (String) getHeadItemValue("newpk_jobgrade");
			if ((defaultls.contains("newpk_jobgrade")) && (pk_jobgrade != null)) {
				JobGradeVO grade = (JobGradeVO) getService().queryByPk(JobGradeVO.class, pk_jobgrade, true);
				getBillCardPanel().getHeadItem("newpk_jobrank").setValue(grade.getPk_jobrank());
			}
		}
	}

	public void showAllSetItems(boolean editstate) {
		BillData billdata = getBillCardPanel().getBillData();
		BillTabVO oldtab = billdata.getTabVO(0, "oldinfo");
		BillTabVO newtab = billdata.getTabVO(0, "newinfo");
		if ((oldtab == null) || (newtab == null)) {
			return;
		}
		BillItem[] olditems = billdata.getHeadItems("oldinfo");
		BillItem[] newitems = billdata.getHeadItems("newinfo");
		BillItem[] allItems = (BillItem[]) (BillItem[]) ArrayUtils.addAll(olditems, newitems);
		String trnstype = (String) getBillCardPanel().getHeadItem("pk_trnstype").getValueObject();
		IItemSetAdapter[] itemvos = null;
		if (!StringUtils.isBlank(trnstype)) {
			itemvos = (IItemSetAdapter[]) getModel().getHashItemSets().get(trnstype);
			if ((itemvos == null) || (itemvos.length == 0)) {
				getModel().queryTrnsItems(trnstype);
				itemvos = (IItemSetAdapter[]) getModel().getHashItemSets().get(trnstype);
			}
		} else {
			itemvos = new IItemSetAdapter[0];
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
			item.setShow(false);
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
			if (item == null)
				continue;
			if (key.startsWith("old")) {
				item.setShow(true);
			} else {
				item.setShow(isedit);
			}
			if (("newpk_org".equals(item.getKey())) || ("newpk_dept".equals(item.getKey())) || ("newpk_psncl".equals(item.getKey()))) {
				item.setNull(true);
				item.setEdit(editstate);
				item.setEnabled(editstate);
			} else {
				item.setNull((key.startsWith("new")) && (isedit) && (notnull));
				item.setEdit((key.startsWith("new")) && (isedit) && (editstate));
				item.setEnabled((key.startsWith("new")) && (isedit) && (editstate));
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

	public void setPsnjobPKs(String[] psnjobPKs) {
		this.psnjobPKs = psnjobPKs;
	}

	public String[] getPsnjobPKs() {
		return this.psnjobPKs;
	}
}