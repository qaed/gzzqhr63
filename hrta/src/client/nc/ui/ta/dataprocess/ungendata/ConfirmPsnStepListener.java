package nc.ui.ta.dataprocess.ungendata;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.PubEnv;
import nc.itf.ta.ITimeDataQueryMaintain;
import nc.pub.tools.HiSQLHelper;
import nc.ui.bd.ref.IRefConst;
import nc.ui.hr.uif2.model.HRWizardModel;
import nc.ui.pub.beans.wizard.IWizardStepListener;
import nc.ui.pub.beans.wizard.WizardStepEvent;
import nc.ui.pub.beans.wizard.WizardStepException;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.querytemplate.querytree.FromWhereSQLImpl;
import nc.ui.ta.pub.selpsn.ConditionSelPsnDateScopePanel;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.logging.Debug;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.util.SqlWhereUtil;

/**
 * 
 * @author YuCheng
 * @version 2012-8-23 ����02:43:37
 */
public class ConfirmPsnStepListener implements IWizardStepListener {

	@Override
	public void stepActived(WizardStepEvent event) throws WizardStepException {
		Debug.debug("multiselect step will be actived");
		ConfirmPsnStep currStep = (ConfirmPsnStep)event.getStep();
		HRWizardModel model = (HRWizardModel)currStep.getModel();
		if(!(model.getStepWhenAction() instanceof SelPsnStep))
			return;
		SelPsnStep selPsnStep = (SelPsnStep)model.getSteps().get(0);
		ConditionSelPsnDateScopePanel selPsnPanel = selPsnStep.getSelPsnPanel();
		FromWhereSQL fromWhereSQL =  selPsnPanel.getQuerySQL();
		
		//tsy ��������δ����ͳ����Ӳ���Ȩ�޿���
		if (fromWhereSQL.getFrom() == null) {//�ղ�ѯ�������Լ�����һ��fromwheresql
			fromWhereSQL = new FromWhereSQLImpl();
			((FromWhereSQLImpl) fromWhereSQL).setFrom("tbm_psndoc tbm_psndoc left outer join hi_psnjob T1 ON T1.pk_psnjob = tbm_psndoc.pk_psnjob");
			Map<String, String> aliasMap = new HashMap<String, String>();
			aliasMap.put(".", "tbm_psndoc");
			aliasMap.put("pk_psnjob", "T1");
			((FromWhereSQLImpl) fromWhereSQL).setAttrpath_alias_map(aliasMap);
		}
		SqlWhereUtil sqlWhereUtil = new SqlWhereUtil(fromWhereSQL.getWhere());
		String tableAlias = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob");
		// ����Ȩ��
		String permission =
				HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_DEPT, IRefConst.DATAPOWEROPERATION_CODE, tableAlias);
		if (StringUtils.isNotBlank(permission)) {
			sqlWhereUtil.and(permission);
		}
		Logger.error("���Ȩ�޺�sql:" + sqlWhereUtil.getSQLWhere());
		fromWhereSQL = new nc.ui.hr.pub.FromWhereSQL(fromWhereSQL, sqlWhereUtil.getSQLWhere());
		
		
		
		UFLiteralDate beginDate = selPsnPanel.getBeginDate();
		UFLiteralDate endDate = selPsnPanel.getEndDate();
		ITimeDataQueryMaintain queryMaintain = NCLocator.getInstance().lookup(ITimeDataQueryMaintain.class);
		TBMPsndocVO[] vos = null;
		try {
			vos = queryMaintain.queryUnGenerateByCondition(selPsnStep.getAppModel().getContext(), fromWhereSQL, beginDate, endDate);
		} catch (BusinessException e) {
			Debug.error(e.getMessage(), e);
			throw new BusinessRuntimeException(e.getMessage(), e);
		}
		currStep.getConfirmPsnPanel().setFormVOs(vos);
	}

	@Override
	public void stepDisactived(WizardStepEvent event) throws WizardStepException {}

}
