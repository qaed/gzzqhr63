package nc.ui.ta.psndoc.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import nc.bs.logging.Logger;
import nc.hr.utils.PubEnv;
import nc.pub.tools.HiSQLHelper;
import nc.ui.bd.ref.IRefConst;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.querytemplate.querytree.FromWhereSQLImpl;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.ta.psndoc.model.TbmPsndocAppModelDataManager;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.ta.psndoc.TBMPsndocSqlPiecer;
import nc.vo.util.SqlWhereUtil;

@SuppressWarnings("restriction")
public class QueryAction extends nc.ui.hr.uif2.action.QueryAction {

	private static final long serialVersionUID = 1L;

	@Override
	protected boolean isActionEnable() {
		if (getModel().getContext().getPk_org() == null) {
			return false;
		}
		return super.isActionEnable();
	}

	@Override
	protected void executeQuery(IQueryScheme queryScheme) {
		//tsy 添加部门权限控制
		FromWhereSQL fromWhereSQL = queryScheme.getTableJoinFromWhereSQL();
		if (fromWhereSQL.getFrom() == null) {//空查询条件，自己构造一个fromwheresql
			fromWhereSQL = new FromWhereSQLImpl();
			((FromWhereSQLImpl) fromWhereSQL).setFrom("tbm_psndoc tbm_psndoc left outer join hi_psnjob T1 ON T1.pk_psnjob = tbm_psndoc.pk_psnjob");
			Map<String, String> aliasMap = new HashMap<String, String>();
			aliasMap.put(".", "tbm_psndoc");
			aliasMap.put("pk_psnjob", "T1");
			((FromWhereSQLImpl) fromWhereSQL).setAttrpath_alias_map(aliasMap);
		}
		SqlWhereUtil sqlWhereUtil = new SqlWhereUtil(fromWhereSQL.getWhere());
		String tableAlias = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob");
		// 部门权限
		String permission =
				HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_DEPT, IRefConst.DATAPOWEROPERATION_CODE, tableAlias);
		if (StringUtils.isNotBlank(permission)) {
			sqlWhereUtil.and(permission);
		}
		Logger.error("添加权限后sql:" + sqlWhereUtil.getSQLWhere());
		fromWhereSQL = new nc.ui.hr.pub.FromWhereSQL(fromWhereSQL, sqlWhereUtil.getSQLWhere());

		((TbmPsndocAppModelDataManager) getDataManager()).initModelBySqlWhere(fromWhereSQL);
		queryExcuted = true;
	}
}
