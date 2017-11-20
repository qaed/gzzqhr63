package nc.impl.hrwa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.dao.BaseDAO;
import nc.hr.utils.PubEnv;
import nc.hr.utils.SQLHelper;
import nc.impl.pub.ace.AceWaBaUnitPubServiceImpl;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.ui.bd.ref.IRefConst;
import nc.ui.hrwa.wa_ba_unit.ace.view.FromDeptGenDialog;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.bm.data.BmDataVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.om.hrdept.AggHRDeptVO;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.org.DeptVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ISuperVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.uif2.LoginContext;
import nc.vo.wa.wa_ba.unit.AggWaBaUnitHVO;
import nc.vo.wa.wa_ba.unit.WaBaUnitBVO;
import nc.vo.wa.wa_ba.unit.WaBaUnitHVO;

import org.apache.commons.lang.StringUtils;

public class WaBaUnitMaintainImpl extends AceWaBaUnitPubServiceImpl implements nc.itf.hrwa.IWaBaUnitMaintain {
	/*
	 * �޸����нӿ��Լ�ʵ�֡������еľ������ VO �����޸�Ϊ�ۺ� VO ����
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
		//��Ҫ���������
		//List<AggWaBaUnitHVO> insert_list = new ArrayList<AggWaBaUnitHVO>();
		//�������в���
		Iterator<Entry<HRDeptVO, String>> iterator = paramHashMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<HRDeptVO, String> entry = iterator.next();
			AggWaBaUnitHVO aggvo = new AggWaBaUnitHVO();
			WaBaUnitHVO hvo = new WaBaUnitHVO();
			hvo.setCreationtime(new UFDateTime());//����ʱ��
			hvo.setCreator(loginContext.getPk_loginUser());//������
			hvo.setPk_group(loginContext.getPk_group());//����
			hvo.setPk_org(loginContext.getPk_org());//��֯
			hvo.setCode(entry.getKey().getCode());//����
			hvo.setName(entry.getKey().getName());//����
			hvo.setBa_unit_type("����������");
			hvo.setSrc_obj_pk(entry.getKey().getPk_dept());//���沿��pk
			aggvo.setParent(hvo);
			List<WaBaUnitBVO> bvos = new ArrayList<WaBaUnitBVO>();
			PsnJobVO[] psnjobVOs = queryPsnJobVO(entry.getValue());
			//���������µ���Ա
			if (psnjobVOs == null || psnjobVOs.length == 0) {
				continue;
			}
			for (int i = 0; i < psnjobVOs.length; i++) {
				PsnJobVO psnJobVO = psnjobVOs[i];
				WaBaUnitBVO bvo = new WaBaUnitBVO();
				bvo.setPk_psndoc(psnJobVO.getPk_psndoc());
				bvo.setPk_psnjob(psnJobVO.getPk_psnjob());
				bvo.setDr(0);
				bvos.add(bvo);
			}
			aggvo.setChildrenVO(bvos.toArray(new WaBaUnitBVO[0]));
			//��������
			this.insert(aggvo);
		}

	}

	/**
	 * ����������ȡpsnjobVO
	 * 
	 * @param extCond
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	private PsnJobVO[] queryPsnJobVO(String extCond) throws BusinessException {
		StringBuffer sql =
				new StringBuffer("").append(" select hi_psnjob.* ").append(" from hi_psnjob ").append(" inner join bd_psndoc on bd_psndoc.pk_psndoc = hi_psnjob.pk_psndoc  ").append(" inner join hi_psnorg on bd_psndoc.pk_psndoc = hi_psnorg.pk_psndoc ").append(" left join hi_psndoc_trial on hi_psndoc_trial.pk_psndoc = hi_psnjob.pk_psndoc ");

		String condition = "(1=1) ";
		condition += filterPsnjobCondition();
		if (!StringUtils.isEmpty(extCond)) {
			condition = condition + " and " + extCond + " ";
		}
		// ��ԱȨ�޹���
		String psnPowerSql =
				SQLHelper.getPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_6007PSNJOB, IRefConst.DATAPOWEROPERATION_CODE, "hi_psnjob");
		if (!StringUtils.isBlank(psnPowerSql)) {
			condition = condition + " and " + psnPowerSql;
		}
		sql.append(" where " + condition);

		sql.append("  order by  hi_psnjob.ismainjob desc");
		List<PsnJobVO> psnjobVOs = (List<PsnJobVO>) new BaseDAO().executeQuery(sql.toString(), new BeanListProcessor(PsnJobVO.class));
		if (psnjobVOs == null) {
			return null;
		}
		return psnjobVOs.toArray(new PsnJobVO[0]);
	}

	/**
	 * ��Ա��ְ��¼�Ĺ̶���������
	 * 
	 * @return
	 */
	private String filterPsnjobCondition() {
		StringBuffer whereBuf = new StringBuffer();
		whereBuf.append(" and  (");
		//��ְ��Ա
		whereBuf.append("  (hi_psnorg.indocflag = 'Y' ");// �Ѽ�����Ա����
		whereBuf.append("   and hi_psnorg.endflag = 'N' ");// δ����
		whereBuf.append("   and hi_psnorg.lastflag = 'Y' "); // ���¼�¼
		whereBuf.append("   and hi_psnjob.endflag = 'N' ");// δ����
		whereBuf.append("   and hi_psnjob.psntype = 0 "); // ��Ա����Ϊ����Ա
		whereBuf.append("   and hi_psnjob.lastflag = 'Y' "); // ���¼�¼
		whereBuf.append("   and hi_psnjob.ismainjob = 'Y' ");//��ְ
		whereBuf.append("   and isnull(hi_psnjob.jobglbdef7,'N') <> 'Y' ");//��һ�����Ÿ�����
		whereBuf.append("   and hi_psnjob.pk_psncl='1001A410000000002HSB' ");//�ر�Ա��
		whereBuf.append("   and (isnull(hi_psndoc_trial.endflag,'Y')='Y' and isnull(hi_psndoc_trial.lastflag,'Y')='Y'))");//������
		//��ְ��Ա
		whereBuf.append("  or (hi_psnorg.indocflag = 'Y'  ");//��ְ
		whereBuf.append("  and hi_psnorg.endflag = 'Y' ");//��ְ
		whereBuf.append("  and hi_psnorg.lastflag='Y' ");
		whereBuf.append("  and to_char(sysdate-30)<hi_psnorg.enddate ");
		whereBuf.append("  and hi_psnjob.psntype = 0 ");
		whereBuf.append("  and hi_psnjob.lastflag = 'Y' ");
		whereBuf.append("  and hi_psnjob.ismainjob='Y' ");//��ְ
		whereBuf.append("  and isnull(hi_psnjob.jobglbdef7,'N') <> 'Y' ");//��һ�����Ÿ�����
		whereBuf.append("  and hi_psnjob.pk_psncl='1001A410000000003FXX' ");//��ְ�ر�Ա��
		whereBuf.append("   and (isnull(hi_psndoc_trial.endflag,'Y')='Y' and isnull(hi_psndoc_trial.lastflag,'Y')='Y'))");

		whereBuf.append(" ) ");//��ְ
		return whereBuf.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	/**
	 * ��ѯ��Ա
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

	@Override
	public void fastUpdate(LoginContext loginContext, String condition) throws BusinessException {
		StringBuilder sqlWhere = new StringBuilder();
		sqlWhere.append(" pk_dept in (select src_obj_pk from wa_ba_unit where pk_org='" + loginContext.getPk_org() + "') ");
		//������䵥Ԫ�У���ǰ��֯�£��ɹ���Ĳ��ţ����ɼ����ţ�
		AggHRDeptVO[] aggdepts = FromDeptGenDialog.getDeptQryService().queryByCondition(loginContext, sqlWhere.toString());
		//��ǰӦ������Ա
		sqlWhere.delete(0, sqlWhere.length());
		sqlWhere.append(" ( hi_psnjob.pk_dept in (select pk_dept from org_dept where  1=1 and (");
		for (AggHRDeptVO aggdept : aggdepts) {
			String pk_dept = (String) aggdept.getParentVO().getAttributeValue(DeptVO.PK_DEPT);
			sqlWhere.append(" org_dept.pk_dept='" + pk_dept + "' or org_dept.pk_fatherorg='" + pk_dept + "' or ");
		}
		sqlWhere.delete(sqlWhere.length() - 3, sqlWhere.length());
		sqlWhere.append("))) ");
		Map<String, PsnJobVO> psnjobMap = arrayvoToMap(PsndocVO.PK_PSNDOC, queryPsnJobVO(sqlWhere.toString()));

		//��ǰ������֯�µ�����Ϊ�����������ɡ��ķ��䵥Ԫ
		Object[] aggunit = (Object[]) query(" pk_org='" + loginContext.getPk_org() + "'");
		/**
		 * key����֯ value:��Ӧ�Ľ���Ԫ
		 */
		Map<String, String> hashOrgUnit = new HashMap<String, String>();
		/**
		 * key:psndoc vaue:��Ӧ��unitbvo
		 */
		Map<String, ISuperVO> UnitBVOMap = new HashMap<String, ISuperVO>();
		for (Object aggvo : aggunit) {
			AggWaBaUnitHVO aggWaBaUnitHVO = (AggWaBaUnitHVO) aggvo;
			if (aggWaBaUnitHVO.getParentVO().getSrc_obj_pk() == null) {
				continue;
			}
			hashOrgUnit.put(aggWaBaUnitHVO.getParentVO().getSrc_obj_pk(), aggWaBaUnitHVO.getParentVO().getPk_wa_ba_unit());
			UnitBVOMap.putAll(arrayvoToMap(PsndocVO.PK_PSNDOC, aggWaBaUnitHVO.getChildren(WaBaUnitBVO.class)));
		}
		super.syncUnitBVO(hashOrgUnit, UnitBVOMap, psnjobMap);
	}

}
