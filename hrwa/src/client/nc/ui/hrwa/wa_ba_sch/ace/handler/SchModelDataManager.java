package nc.ui.hrwa.wa_ba_sch.ace.handler;

import nc.ui.pubapp.uif2app.query2.model.ModelDataManager;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.querytemplate.querytree.FromWhereSQLImpl;
import nc.ui.querytemplate.querytree.IQueryScheme;

@SuppressWarnings("restriction")
public class SchModelDataManager extends ModelDataManager {

	/* £¨·Ç Javadoc£©
	 * @see nc.ui.pubapp.uif2app.query2.model.ModelDataManager#initModelByQueryScheme(nc.ui.querytemplate.querytree.IQueryScheme)
	 */
	@Override
	public void initModelByQueryScheme(IQueryScheme qryScheme) {
		FromWhereSQLImpl fromW = (FromWhereSQLImpl) qryScheme.getTableJoinFromWhereSQL();
		String wheresql = fromW.getWhere();
		//		FromWhereSQL fromv = qryScheme.getTableListFromWhereSQL();
		String pk_org = getModel().getContext().getPk_org();
		if (wheresql != null && !"".equals(wheresql.trim())) {
			wheresql = wheresql + " and (pk_org='" + pk_org + "')";
		} else {
		}
		fromW.setWhere(wheresql);
		qryScheme.put(IQueryScheme.KEY_SQL_TABLE_JOIN, fromW);
		super.initModelByQueryScheme(qryScheme);
	}

}
