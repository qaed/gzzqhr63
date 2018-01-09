package nc.ui.hrwa.wa_ba_sch.ace.handler;

import java.util.HashMap;
import java.util.Map;

import nc.ui.pubapp.uif2app.query2.model.ModelDataManager;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.querytemplate.querytree.FromWhereSQLImpl;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.querytemplate.querytree.QueryScheme;
import nc.vo.util.SqlWhereUtil;

@SuppressWarnings("restriction")
public class SchModelDataManager extends ModelDataManager {

	/* （非 Javadoc）
	 * @see nc.ui.pubapp.uif2app.query2.model.ModelDataManager#initModelByQueryScheme(nc.ui.querytemplate.querytree.IQueryScheme)
	 */
	@Override
	public void initModelByQueryScheme(IQueryScheme qryScheme) {
		FromWhereSQL fromWhereSQL = qryScheme.getTableJoinFromWhereSQL();
		Map<String, String> aliasMap = ((FromWhereSQLImpl)fromWhereSQL).getAttrpath_alias_map();
		if (fromWhereSQL.getFrom() == null) {//空查询条件，自己构造一个fromwheresql
			String from = "wa_ba_sch_h wa_ba_sch_h";
			fromWhereSQL = new FromWhereSQLImpl(from, null);
		}
		SqlWhereUtil sqlWhereUtil = new SqlWhereUtil(fromWhereSQL.getWhere());
		sqlWhereUtil.and(" wa_ba_sch_h.pk_org='" + getModel().getContext().getPk_org() + "'");
		sqlWhereUtil.and(" isnull(wa_ba_sch_h.dr,0)=0 ");
		fromWhereSQL = new FromWhereSQLImpl(fromWhereSQL.getFrom(), sqlWhereUtil.getSQLWhere());
		((FromWhereSQLImpl) fromWhereSQL).setAttrpath_alias_map(aliasMap);
		((QueryScheme) qryScheme).putTableJoinFromWhereSQL(fromWhereSQL);
		super.initModelByQueryScheme(qryScheme);
	}

}
