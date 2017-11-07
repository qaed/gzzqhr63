package nc.impl.hr.comp.trn;

import nc.bs.framework.common.NCLocator;
import nc.hr.frame.persistence.BaseDAOManager;
import nc.hr.utils.SQLHelper;
import nc.hr.utils.StringPiecer;
import nc.itf.om.IAOSQueryService;
import nc.jdbc.framework.SQLParameter;
import nc.vo.hr.comp.trn.PsnTrnVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;

public class HrTrnDAO extends BaseDAOManager {
	public HrTrnDAO() {
	}

	public PsnTrnVO[] queryTRNPsnInf(String pk_org, UFLiteralDate beginDate, UFLiteralDate endDate, int trnType, String strAddWhere)
			throws BusinessException {
		return queryTRNPsnInf0(pk_org, beginDate, endDate, trnType, strAddWhere, false);
	}

	protected PsnTrnVO[] queryTRNPsnInf0(String pk_hrorg, UFLiteralDate beginDate, UFLiteralDate endDate, int trnType, String strAddWhere, boolean filterByOrg)
			throws BusinessException {
		StringBuffer sqlBuf =
				new StringBuffer("select psnjob.pk_psnjob,psndoc.code as psncode,psnjob.clerkcode,psndoc.name as psnname,psnjob.clerkcode ,bd_psncl.name as psnclassname,org_orgs.name as orgname,dept.name as deptname,job.jobname as jobname,post.postname,dept.pk_dept, psndoc.id as psnid,post.pk_post as pk_post, bd_psncl.pk_psncl, psndoc.pk_psndoc,psnjob.pk_psnorg,");

		sqlBuf.append("psnjob.ismainjob, psnjob.assgid, ");
		sqlBuf.append("dept.pk_dept as workdept, ");
		sqlBuf.append("dept.pk_vid as workdeptvid, ");
		sqlBuf.append("org_orgs.pk_org as workorg, ");
		sqlBuf.append("org_orgs.pk_vid as workorgvid ");

		String tableSql =
				" from hi_psnjob psnjob inner join bd_psndoc psndoc on psnjob.pk_psndoc = psndoc.pk_psndoc inner join hi_psnorg psnorg on psnjob.pk_psnorg = psnorg.pk_psnorg inner join bd_psncl on psnjob.pk_psncl = bd_psncl.pk_psncl left outer join org_dept dept on dept.pk_dept = psnjob.pk_dept left outer join om_job job on job.pk_job = psnjob.pk_job left outer join om_post post on post.pk_post = psnjob.pk_post left outer join org_orgs on org_orgs.pk_org = psnjob.pk_org where  psnjob.lastflag = 'Y' and psnorg.indocflag = 'Y' and psnorg.psntype = 0 ";

		String beginDateWhere =
				"and ((psnjob.begindate >= ? and psnjob.begindate <= ? and (psnjob.begindate > psnorg.startpaydate or " + SQLHelper.getNullSql("psnorg.startpaydate") + ")) " + "or (psnorg.startpaydate >= ? and psnorg.startpaydate <= ? " + "and psnorg.startpaydate >= psnjob.begindate ))";

		String begin2DateWhere =
				"and ((job2.begindate >= ? and job2.begindate <= ? and (job2.begindate > psnorg.startpaydate or " + SQLHelper.getNullSql("psnorg.startpaydate") + ")) " + "or (psnorg.startpaydate >= ? and psnorg.startpaydate <= ? " + "and psnorg.startpaydate >= job2.begindate ))";

		String begin3DateWhere =
				"and ((" + SQLHelper.getNullSql("psnorg.startpaydate") + ") " + "or (psnorg.startpaydate >= ? and psnorg.startpaydate <= ? ))";

		String beginStopWhere =
				"and ((psnjob.begindate >= ? and psnjob.begindate <= ? and (psnjob.begindate < psnorg.stoppaydate or " + SQLHelper.getNullSql("psnorg.stoppaydate") + ")) " + "or (psnorg.stoppaydate >= ? and psnorg.stoppaydate <= ? " + "and psnorg.stoppaydate <= psnjob.begindate ))";

		String endDateWhere =
				"and ((psnjob.enddate >= ? and psnjob.enddate <= ? and (psnjob.enddate < psnorg.stoppaydate or " + SQLHelper.getNullSql("psnorg.stoppaydate") + ")) " + "or (psnorg.stoppaydate >= ? and psnorg.stoppaydate <= ? " + "and psnorg.stoppaydate <= psnjob.enddate ))";

		switch (trnType) {

			case 0:
				// 20171106 tsy 添加 试用情况表
				//				tableSql =
				//						" from hi_psnjob psnjob inner join bd_psndoc psndoc on psnjob.pk_psndoc = psndoc.pk_psndoc inner join hi_psnorg psnorg on psnjob.pk_psnorg = psnorg.pk_psnorg inner join bd_psncl on psnjob.pk_psncl = bd_psncl.pk_psncl left outer join org_dept dept on dept.pk_dept = psnjob.pk_dept left outer join om_job job on job.pk_job = psnjob.pk_job left outer join om_post post on post.pk_post = psnjob.pk_post left outer join org_orgs on org_orgs.pk_org = psnjob.pk_org inner join hi_psndoc_psnchg hipschg on  psnjob.pk_psndoc = hipschg.pk_psndoc and hipschg.pk_corp = org_orgs.pk_corp  where   psnorg.indocflag = 'Y' and psnorg.psntype = 0 ";
				tableSql =
						" from hi_psnjob psnjob inner join bd_psndoc psndoc on psnjob.pk_psndoc = psndoc.pk_psndoc inner join hi_psnorg psnorg on psnjob.pk_psnorg = psnorg.pk_psnorg inner join bd_psncl on psnjob.pk_psncl = bd_psncl.pk_psncl left outer join org_dept dept on dept.pk_dept = psnjob.pk_dept left outer join om_job job on job.pk_job = psnjob.pk_job left outer join om_post post on post.pk_post = psnjob.pk_post left outer join org_orgs on org_orgs.pk_org = psnjob.pk_org inner join hi_psndoc_psnchg hipschg on  psnjob.pk_psndoc = hipschg.pk_psndoc and hipschg.pk_corp = org_orgs.pk_corp left join hi_psndoc_trial trial on trial.pk_psndoc=psnjob.pk_psndoc  where   psnorg.indocflag = 'Y' and psnorg.psntype = 0 ";
				// 20171106 tsy end
				sqlBuf.append(",hipschg.begindate as trndate ");
				sqlBuf.append(tableSql);
				sqlBuf.append("and psnjob.ismainjob = 'Y' ");

				sqlBuf.append(begin3DateWhere);

				sqlBuf.append(" and psnjob.begindate = (select max(psnjob2.begindate) from hi_psnjob psnjob2 where hipschg.pk_psndoc = psnjob2.pk_psndoc and hipschg.pk_psnorg=psnjob2.pk_psnorg and psnjob2.begindate<= '" + endDate.toStdString() + "' and (psnjob2.enddate>= '" + beginDate.toStdString() + "' or psnjob2.enddate is null) and psnjob2.ismainjob='Y' )");

				sqlBuf.append(" and hipschg.begindate<= '" + endDate.toStdString() + "' and hipschg.begindate>= '" + beginDate.toStdString() + "'");

				break;

			case 1:
				tableSql =
						" from hi_psnjob psnjob  inner join hi_psnjob psnjob_old on psnjob_old.pk_psnorg = psnjob.pk_psnorg inner join bd_psndoc psndoc on psnjob.pk_psndoc = psndoc.pk_psndoc inner join hi_psnorg psnorg on psnjob.pk_psnorg = psnorg.pk_psnorg inner join bd_psncl on psnjob.pk_psncl = bd_psncl.pk_psncl left outer join org_dept dept on dept.pk_dept = psnjob_old.pk_dept left outer join om_job job on job.pk_job = psnjob.pk_job left outer join om_post post on post.pk_post = psnjob_old.pk_post left outer join org_orgs on org_orgs.pk_org = psnjob.pk_org where   psnorg.indocflag = 'Y' and psnorg.psntype = 0 ";

				sqlBuf.append(",psnjob.begindate as trndate ");
				sqlBuf.append(tableSql);
				sqlBuf.append("and psnjob.ismainjob = 'Y' ");
				sqlBuf.append("and psnjob.endflag = 'Y' ");
				sqlBuf.append("and psnjob.trnsevent = 4 ");
				sqlBuf.append("and psnjob_old.recordnum = psnjob.recordnum + 1 ");
				sqlBuf.append(beginStopWhere);

				break;

			case 2:
				sqlBuf =
						new StringBuffer("select distinct psndoc.pk_psndoc,psndoc.code as psncode,psnjob.clerkcode,psndoc.name as psnname,bd_psncl.name as psnclassname,dept.name as deptname,post.postname,  postseries.postseriesname as postrank, psndoc.id as psnid,dept2.name as dept2name,post2.postname as post2name, postseries2.postseriesname as postrank2, psnjob.pk_psnjob,psnjob.pk_psnorg,job2.begindate as trndate,psnjob.ismainjob,psnjob.assgid ");

				sqlBuf.append("from hi_psnjob psnjob inner join hi_psnjob job2 on job2.pk_psndoc = psnjob.pk_psndoc and job2.pk_psnorg = psnjob.pk_psnorg and job2.recordnum = psnjob.recordnum-1 and psnjob.assgid = job2.assgid  and ((psnjob.pk_post <> job2.pk_post) or ( " + SQLHelper.getEqualsWaveSql("psnjob.pk_post") + " and job2.pk_post is not null) or " + "(psnjob.pk_post is not null and " + SQLHelper.getEqualsWaveSql("job2.pk_post") + "))");

				sqlBuf.append("inner join hi_psnorg psnorg on psnjob.pk_psnorg = psnorg.pk_psnorg ");
				sqlBuf.append("inner join bd_psndoc psndoc on psnjob.pk_psndoc = psndoc.pk_psndoc ");
				sqlBuf.append("inner join bd_psncl on psnjob.pk_psncl = bd_psncl.pk_psncl ");
				sqlBuf.append("left join org_dept dept2 on dept2.pk_dept = job2.pk_dept ");
				sqlBuf.append("left join om_post post2 on post2.pk_post = job2.pk_post ");
				sqlBuf.append("left join om_postseries  postseries on postseries.pk_postseries = psnjob.pk_postseries ");
				sqlBuf.append("left join org_dept dept on dept.pk_dept = psnjob.pk_dept ");
				sqlBuf.append("left join om_post post on post.pk_post = psnjob.pk_post ");
				sqlBuf.append("left join om_postseries  postseries2 on postseries2.pk_postseries = job2.pk_postseries ");
				sqlBuf.append("where ");
				sqlBuf.append(" psnorg.endflag = 'N' ");
				sqlBuf.append("and psnjob.endflag = 'Y' ");
				sqlBuf.append("and psnjob.lastflag = 'N' ");
				sqlBuf.append("and job2.lastflag = 'Y' ");
				sqlBuf.append(begin2DateWhere);

				break;

			case 3:
				sqlBuf =
						new StringBuffer("select psndoc.pk_psndoc,psndoc.code as psncode,psnjob.clerkcode,psndoc.name as psnname,bd_psncl.name as psnclassname, dept.name as deptname,post.postname,psndoc.id as psnid, psnjob.pk_dept, psnjob.pk_post,psnjob.pk_psnorg, psnjob.pk_psnjob,psnjob.begindate as trndate,psnjob.enddate, psnjob.ismainjob,psnjob.assgid, ");

				sqlBuf.append("dept.pk_dept as workdept, ");
				sqlBuf.append("dept.pk_vid as workdeptvid, ");
				sqlBuf.append("org_orgs.pk_org as workorg, ");
				sqlBuf.append("org_orgs.pk_vid as workorgvid, ");
				sqlBuf.append("org_orgs.name as orgname ");
				sqlBuf.append(tableSql);
				sqlBuf.append("and psnjob.ismainjob = 'N' ");

				sqlBuf.append(beginDateWhere);
				break;

			case 4:
				sqlBuf =
						new StringBuffer("select psndoc.pk_psndoc,psndoc.code as psncode,psnjob.clerkcode,psndoc.name as psnname,bd_psncl.name as psnclassname, dept.name as deptname,post.postname,psndoc.id as psnid, psnjob.pk_dept, psnjob.pk_post,psnjob.pk_psnorg, psnjob.pk_psnjob,psnjob.begindate,psnjob.enddate as trndate,psnjob.ismainjob,psnjob.assgid ");

				sqlBuf.append(tableSql);
				sqlBuf.append("and psnjob.ismainjob = 'N' ");
				sqlBuf.append("and psnjob.endflag = 'Y' ");
				sqlBuf.append(endDateWhere);
				break;

			case 5:
				sqlBuf.append(",psnorg.psntype,psnjob.begindate as trndate,psnorg.startpaydate as startpaydate ");

				sqlBuf.append(tableSql);

				sqlBuf.append(beginDateWhere);
		}

		if (filterByOrg) {
			IAOSQueryService aosService = (IAOSQueryService) NCLocator.getInstance().lookup(IAOSQueryService.class);
			OrgVO[] orgVOs = aosService.queryAOSMembersByHROrgPK(pk_hrorg, false, false);
			String inSQL = StringPiecer.getDefaultPiecesTogether(orgVOs, "pk_org");
			sqlBuf.append(" and psnjob.pk_org in(").append(inSQL).append(") ");
		}

		sqlBuf.append(" and psnjob.pk_org in (select pk_adminorg from org_admin_enable) ");

		if ((strAddWhere != null) && (strAddWhere.length() > 0)) {
			sqlBuf.append(strAddWhere);
		}

		SQLParameter param = new SQLParameter();

		param.addParam(beginDate.toStdString());
		param.addParam(endDate.toStdString());
		param.addParam(beginDate.toStdString());
		param.addParam(endDate.toStdString());
		String sql = sqlBuf.toString();

		if (0 == trnType) {
			sql = sql.replaceAll(" and hi_psnjob.lastflag = 'Y' ", " ");
			param = new SQLParameter();

			param.addParam(beginDate.toStdString());
			param.addParam(endDate.toStdString());
		}

		return (PsnTrnVO[]) executeQueryVOs(sql, param, PsnTrnVO.class);
	}

	public PsnTrnVO[] queryTRNPsnInf4TA(String pk_hrorg, UFLiteralDate beginDate, UFLiteralDate endDate, int trnType, String strAddWhere)
			throws BusinessException {
		return queryTRNPsnInf0(pk_hrorg, beginDate, endDate, trnType, strAddWhere, true);
	}
}