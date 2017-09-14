package nc.impl.hrwa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.hrwa.wa_ba_unit.ace.rule.WaUnitDataUniqueCheckRule;
import nc.hr.utils.PubEnv;
import nc.hr.utils.SQLHelper;
import nc.impl.pub.ace.AceWaBaUnitPubServiceImpl;
import nc.impl.pubapp.pattern.data.bill.BillInsert;
import nc.impl.pubapp.pattern.data.bill.BillQuery;
import nc.itf.hrwa.IWaBaUnitMaintain;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.IMDPersistenceService;
import nc.ui.bd.ref.IRefConst;
import nc.vo.bd.meta.AggVOBDObject;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.bm.data.BmDataVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pubapp.AppContext;
import nc.vo.uif2.LoginContext;
import nc.vo.wa.wa_ba.unit.AggWaBaUnitHVO;
import nc.vo.wa.wa_ba.unit.WaBaUnitBVO;
import nc.vo.wa.wa_ba.unit.WaBaUnitHVO;

public class WaBaUnitMaintainImpl extends AceWaBaUnitPubServiceImpl implements nc.itf.hrwa.IWaBaUnitMaintain {
	/*
	 * 修改所有接口以及实现、把其中的具体的主 VO 类型修改为聚合 VO 类型
	 */
	//	IMDPersistenceService persist = NCLocator.getInstance().lookup(IMDPersistenceService.class);
	//	IMDPersistenceQueryService query = NCLocator.getInstance().lookup(IMDPersistenceQueryService.class);

	@Override
	public void delete(Object vos) throws BusinessException {
		super.deletetreeinfo(vos);
	}

	@Override
	public Object insert(Object vos) throws BusinessException {
		return super.inserttreeinfo(vos);
	}

	@Override
	public Object update(Object vos) throws BusinessException {
		return super.updatetreeinfo(vos);
	}

	@Override
	public Object[] query(String whereSql) throws BusinessException {
		return super.querytreeinfo(whereSql);

	}

	@Override
	public void creatByDept(LoginContext loginContext, HashMap<HRDeptVO, String> paramHashMap) throws BusinessException {
		//需要插入的数据
		//List<AggWaBaUnitHVO> insert_list = new ArrayList<AggWaBaUnitHVO>();
		//遍历所有部门
		Iterator<Entry<HRDeptVO, String>> iterator = paramHashMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<HRDeptVO, String> entry = iterator.next();
			AggWaBaUnitHVO aggvo = new AggWaBaUnitHVO();
			WaBaUnitHVO hvo = new WaBaUnitHVO();
			hvo.setCreationtime(new UFDateTime());//创建时间
			hvo.setCreator(loginContext.getPk_loginUser());//创建人
			hvo.setPk_group(loginContext.getPk_group());//集团
			hvo.setPk_org(loginContext.getPk_org());//组织
			hvo.setCode(entry.getKey().getCode());//编码
			hvo.setName(entry.getKey().getName());//名字
			hvo.setBa_unit_type("按部门生成");
			aggvo.setParent(hvo);
			List<WaBaUnitBVO> bvos = new ArrayList<WaBaUnitBVO>();
			PsnJobVO[] psnjobVOs = queryPsnJobVO(entry.getValue());
			//遍历部门下的人员
			if (psnjobVOs == null || psnjobVOs.length == 0) {
				continue;
			}
			for (int i = 0; i < psnjobVOs.length; i++) {
				PsnJobVO psnJobVO = psnjobVOs[i];
				WaBaUnitBVO bvo = new WaBaUnitBVO();
				bvo.setPk_psndoc(psnJobVO.getPk_psndoc());
				bvos.add(bvo);
			}
			aggvo.setChildrenVO(bvos.toArray(new WaBaUnitBVO[0]));
			//保存数据
			this.insert(aggvo);
		}

	}

	/**
	 * 根据条件获取psnjobVO
	 * 
	 * @param extCond
	 * @return
	 * @throws BusinessException
	 */
	private PsnJobVO[] queryPsnJobVO(String extCond) throws BusinessException {
		StringBuffer sql =
				new StringBuffer("").append(" select hi_psnjob.* ").append(" from hi_psnjob ").append(" inner join bd_psndoc on bd_psndoc.pk_psndoc = hi_psnjob.pk_psndoc  ").append(" inner join hi_psnorg on bd_psndoc.pk_psndoc = hi_psnorg.pk_psndoc ");

		String condition = "(1=1) ";
		condition += filterPsnjobCondition();
		if (!StringUtils.isEmpty(extCond)) {
			condition = condition + " and " + extCond + " ";
		}
		// 人员权限过滤
		String psnPowerSql =
				SQLHelper.getPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_6007PSNJOB, IRefConst.DATAPOWEROPERATION_CODE, "hi_psnjob");
		if (!StringUtils.isBlank(psnPowerSql)) {
			condition = condition + " and " + psnPowerSql;
		}
		sql.append(" where " + condition);

		sql.append("  order by  hi_psnjob.ismainjob desc");
		BaseDAO dao = new BaseDAO();
		List<PsnJobVO> psnjobVOs = (List<PsnJobVO>) new BaseDAO().executeQuery(sql.toString(), new BeanListProcessor(PsnJobVO.class));
		if (psnjobVOs == null) {
			return null;
		}
		return psnjobVOs.toArray(new PsnJobVO[0]);
	}

	/**
	 * 人员任职记录的固定过滤条件
	 * 
	 * @return
	 */
	private String filterPsnjobCondition() {
		StringBuffer whereBuf = new StringBuffer();
		whereBuf.append(" and hi_psnjob.endflag = 'N' ");// 未结束

		whereBuf.append("and hi_psnorg.indocflag = 'Y' "); // 已加入人员档案
		whereBuf.append(" and hi_psnorg.endflag = 'N' ");// 未结束
		// whereBuf.append("and hi_psnorg.lastflag = 'Y' "); // 最新记录
		whereBuf.append("and hi_psnjob.psntype = 0 "); // 人员类型为：人员
		whereBuf.append("and hi_psnjob.lastflag = 'Y' "); // 最新记录
		// 需要包含兼职人员whereBuf.append("and hi_psnjob.ismainjob = 'Y' ");
		return whereBuf.toString();
	}

	@Override
	/**
	 * 查询人员
	 * 
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public BmDataVO[] queryPsnForAdd(String condition) throws BusinessException {
		StringBuilder sql = new StringBuilder();
		sql.append("select distinct hi_psnjob.pk_psnjob, ");
		sql.append("       hi_psnjob.pk_psnorg, ");
		sql.append("       hi_psnjob.assgid, ");
		sql.append("       hi_psnjob.pk_psndoc, ");
		sql.append("       hi_psnjob.clerkcode, ");
		sql.append("       " + SQLHelper.getMultiLangNameColumn("bd_psndoc.name") + "  psnname, ");
		sql.append("       " + SQLHelper.getMultiLangNameColumn("org_dept.name") + "  deptname, ");
		sql.append("       " + SQLHelper.getMultiLangNameColumn("om_post.postname") + "  postname, ");
		sql.append("       " + SQLHelper.getMultiLangNameColumn("om_job.jobname") + "  jobname, ");
		sql.append("       " + SQLHelper.getMultiLangNameColumn("bd_psncl.name") + "  psnclname, ");
		sql.append("       " + SQLHelper.getMultiLangNameColumn("org_orgs.name") + "  orgname, ");
		sql.append("       org_orgs.pk_org as workorg, ");
		sql.append("       org_orgs.pk_vid as workorgvid, ");
		sql.append("       org_dept.pk_dept as workdept, ");
		sql.append("       org_dept.pk_vid as workdeptvid ");
		sql.append("  from hi_psnjob inner join bd_psndoc on bd_psndoc.pk_psndoc = hi_psnjob.pk_psndoc ");
		sql.append(" inner join hi_psnorg on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg ");
		sql.append(" left outer join om_job on om_job.pk_job = hi_psnjob.pk_job ");
		sql.append(" left outer join om_post on om_post.pk_post = hi_psnjob.pk_post ");
		sql.append(" left outer join bd_psncl on bd_psncl.pk_psncl = hi_psnjob.pk_psncl ");
		sql.append(" left outer join org_dept on org_dept.pk_dept = hi_psnjob.pk_dept ");
		sql.append(" left outer join org_orgs on org_orgs.pk_org = hi_psnjob.pk_org ");
		sql.append(" where hi_psnjob.ismainjob = 'Y' ");
		sql.append(" and hi_psnorg.psntype = 0  ");
		sql.append(" and hi_psnorg.indocflag = 'Y'  ");
		sql.append(" and hi_psnorg.lastflag = 'Y' ");
		if (!StringUtil.isEmptyWithTrim(condition)) {
			sql.append(" and hi_psnjob.pk_psnjob in (select pk_psnjob from hi_psnjob where " + condition + ")");
		}
		sql.append(" order by hi_psnjob.pk_psndoc ");
		BaseDAO dao = new BaseDAO();
		List<BmDataVO> vos = (List<BmDataVO>) dao.executeQuery(sql.toString(), new BeanListProcessor(BmDataVO.class));
		return vos.toArray(new BmDataVO[0]);

	}
}
