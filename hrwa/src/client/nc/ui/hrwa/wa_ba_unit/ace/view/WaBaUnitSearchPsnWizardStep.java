package nc.ui.hrwa.wa_ba_unit.ace.view;

import java.awt.BorderLayout;
import java.awt.Dimension;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.itf.bd.psn.psndoc.IPsndocService;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.hi.ref.PsnjobRefModel;
import nc.ui.om.ref.AOSRefModel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.wizard.IWizardStepListener;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.pub.beans.wizard.WizardStepEvent;
import nc.ui.pubapp.uif2app.model.HierachicalDataAppModel;
import nc.ui.querytemplate.CriteriaChangedEvent;
import nc.ui.querytemplate.ICriteriaChangedListener;
import nc.ui.querytemplate.QueryConditionEditor;
import nc.ui.querytemplate.filtereditor.FilterEditorWrapper;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.pub.BusinessException;
import nc.vo.querytemplate.TemplateInfo;

@SuppressWarnings("restriction")
public class WaBaUnitSearchPsnWizardStep extends WizardStep implements IWizardStepListener {
	private QueryConditionEditor queryEditor = null;

	private UIPanel panel = null;

	private HierachicalDataAppModel billModel = null;

	private IPsndocService service = null;

	public HierachicalDataAppModel getBillModel() {
		return this.billModel;
	}

	public void setBillModel(HierachicalDataAppModel billModel) {
		this.billModel = billModel;
	}

	public WaBaUnitSearchPsnWizardStep(String title, AbstractUIAppModel model) {
		setTitle(title);
		this.billModel = ((HierachicalDataAppModel) model);
		//步骤描述
		setDescription(ResHelper.getString("60150bmfile", "060150bmfile0032"));//查询人员

		setComp(getUICompont());
		addListener(this);
	}

	private UIPanel getUICompont() {
		if (this.panel == null) {
			this.panel = new UIPanel();
			this.panel.setLayout(new BorderLayout());
			this.panel.add(getQueryEditor(), "Center");
		}
		return this.panel;
	}

	public QueryConditionEditor getQueryEditor() {
		if (this.queryEditor == null) {
			TemplateInfo tempInfo = new TemplateInfo();
			tempInfo.setFunNode("60070psninfo");//在这个表中查
			tempInfo.setNodekey("psnForUnit");//模板编码
			//			if (this.title.equals(ResHelper.getString("60150bmfile", "060150bmfile0000"))) {//批量新增
			//				tempInfo.setNodekey("bmfileba");
			//			}
			tempInfo.setUserid(WorkbenchEnvironment.getInstance().getLoginUser().getCuserid());
			tempInfo.setCurrentCorpPk(PubEnv.getPk_group());
			tempInfo.setPk_Org(PubEnv.getPk_group());
			this.queryEditor = new QueryConditionEditor(tempInfo);
			this.queryEditor.setPreferredSize(new Dimension(600, 400));
			this.queryEditor.registerCriteriaEditorListener(new ICriteriaChangedListener() {
				public void criteriaChanged(CriteriaChangedEvent event) {
					if (4 == event.getEventtype()) {
						try {
							WaBaUnitSearchPsnWizardStep.this.initCondition(event);
						} catch (BusinessException e) {
							Logger.debug(e.getMessage());
						}
					}
				}
			});
			this.queryEditor.showPanel();
		}
		return this.queryEditor;
	}

	protected void initCondition(CriteriaChangedEvent event) throws BusinessException {
		FilterEditorWrapper wrapper = new FilterEditorWrapper(event.getFiltereditor());

		if (!(wrapper.getFieldValueElemEditorComponent() instanceof UIRefPane)) {
			return;
		}
		UIRefPane ref = (UIRefPane) wrapper.getFieldValueElemEditorComponent();
		if (ref.getRefModel() != null) {
			ref.setMultiCorpRef(true);

			AbstractRefModel refModel = ref.getRefModel();
			if (refModel != null) {
				if (((refModel instanceof AOSRefModel)) && (getPsndocService().isHREnabled(getBillModel().getContext().getPk_org()))) {

					wrapper.setRefPK(getBillModel().getContext().getPk_org());
				}
				if ((refModel instanceof PsnjobRefModel)) {
					refModel.addWherePart("  and hi_psnjob.ismainjob = 'Y'");
				}

				//				if ((refModel instanceof BmClassRefModel)) {
				//					BmLoginContext context = (BmLoginContext) getBmfileModel().getContext();
				//					if ((StringUtils.isNotEmpty(context.getCyear())) && (StringUtils.isNotEmpty(context.getCperiod()))) {
				//						((BmClassRefModel) refModel).setCyear(context.getCyear());
				//						((BmClassRefModel) refModel).setCperiod(context.getCperiod());
				//					}
				//
				//					refModel.setPk_org(getBmfileModel().getContext().getPk_org());
				//
				//					if (getTitle().equals(ResHelper.getString("60150bmfile", "060150bmfile0006"))) {
				//						((BmClassRefModel) refModel).setOtherEnvWhere(" pk_bm_class in(select classid from bm_assigncls where PK_ORG='" + getBmfileModel().getContext().getPk_org() + "')");
				//					}
				//				}
			}
		}
	}

	public void stepActived(WizardStepEvent event) {
	}

	public void stepDisactived(WizardStepEvent event) {
		String sql = getQueryEditor().getWhereSql();

		getQueryEditor().validateConditions(false);

		//		if ((!this.title.equals(ResHelper.getString("60150bmfile", "060150bmfile0000"))) && (StringUtils.isNotEmpty(sql))) {
		//			sql = " bm_data.PK_BM_DATA in (select pk_bm_data from bm_data where " + sql + ")";
		//		}

		getModel().putAttr("whereSql", sql);
	}

	private IPsndocService getPsndocService() {
		if (this.service == null) {
			this.service = ((IPsndocService) NCLocator.getInstance().lookup(IPsndocService.class));
		}
		return this.service;
	}
}
