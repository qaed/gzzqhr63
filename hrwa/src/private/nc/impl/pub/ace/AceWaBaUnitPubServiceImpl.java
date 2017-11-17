package nc.impl.pub.ace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.hrwa.wa_ba_unit.ace.rule.WaUnitDataIsNotUsedDelRule;
import nc.bs.hrwa.wa_ba_unit.ace.rule.WaUnitDataIsNotUsedRule;
import nc.bs.hrwa.wa_ba_unit.ace.rule.WaUnitDataUniqueCheckRule;
import nc.bs.hrwa.wa_ba_unit.ace.rule.WaUnitPsnUniqueCheckRule;
import nc.hr.utils.SQLHelper;
import nc.impl.pubapp.pattern.data.bill.BillInsert;
import nc.impl.pubapp.pattern.data.bill.BillQuery;
import nc.impl.pubapp.pattern.data.vo.VOQuery;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.IMDPersistenceService;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ISuperVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pubapp.AppContext;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.wa.wa_ba.unit.AggWaBaUnitHVO;
import nc.vo.wa.wa_ba.unit.WaBaUnitBVO;
import nc.vo.wa.wa_ba.unit.WaBaUnitHVO;

import org.apache.commons.lang.StringUtils;

/**
 * @see nc.impl.hrwa.WaBaUnitMaintainImpl ��д�˷���������VO��Ϊ�ۺ�VO
 * @author tsheay
 */
public abstract class AceWaBaUnitPubServiceImpl {
	IMDPersistenceService persist = NCLocator.getInstance().lookup(IMDPersistenceService.class);
	IMDPersistenceQueryService query = NCLocator.getInstance().lookup(IMDPersistenceQueryService.class);
	private BaseDAO dao = null;

	// ���ӷ���
	public Object inserttreeinfo(Object vo) throws BusinessException {
		try {
			AggWaBaUnitHVO[] aggvo = new AggWaBaUnitHVO[1];
			aggvo[0] = (AggWaBaUnitHVO) vo;
			// ���BP����
			AroundProcesser<AggWaBaUnitHVO> processer = new AroundProcesser<AggWaBaUnitHVO>(null);
			processer.addBeforeRule(new WaUnitDataUniqueCheckRule());
			processer.addBeforeRule(new WaUnitPsnUniqueCheckRule());
			processer.before(aggvo);

			BillInsert<AggWaBaUnitHVO> billinsert = new BillInsert<AggWaBaUnitHVO>();
			return billinsert.insert(aggvo)[0];

		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// ɾ������
	public void deletetreeinfo(Object vo) throws BusinessException {
		try {
			AggWaBaUnitHVO[] aggvo = new AggWaBaUnitHVO[1];
			aggvo[0] = (AggWaBaUnitHVO) vo;
			// ���BP����
			AroundProcesser<AggWaBaUnitHVO> processer = new AroundProcesser<AggWaBaUnitHVO>(null);
			processer.addBeforeRule(new WaUnitDataIsNotUsedDelRule());
			processer.before(aggvo);

			persist.deleteBillFromDB(aggvo[0]);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// �޸ķ���
	public Object updatetreeinfo(Object vo) throws BusinessException {
		try {

			AggWaBaUnitHVO[] aggvo = new AggWaBaUnitHVO[1];
			aggvo[0] = (AggWaBaUnitHVO) vo;
			WaBaUnitHVO hvo = aggvo[0].getParentVO();
			// �����޸��˺�ʱ��
			hvo.setModifier(AppContext.getInstance().getPkUser());
			hvo.setModifiedtime(new UFDateTime());
			// ���BP����
			AroundProcesser<AggWaBaUnitHVO> processer = new AroundProcesser<AggWaBaUnitHVO>(null);
			WaBaUnitHVO[] originVOs = this.getTreeCardVOs(new WaBaUnitHVO[] { hvo });
			if (originVOs != null && originVOs[0] != null) {

				if (!originVOs[0].getCode().equals(hvo.getCode())) {
					//�޸ı��ʱҪУ�飬�±�Ų�������
					processer.addBeforeRule(new WaUnitDataUniqueCheckRule());
				}

				if (!StringUtils.equals(originVOs[0].getBa_mng_psnpk(), hvo.getBa_mng_psnpk()) || !StringUtils.equals(originVOs[0].getBa_mng_psnpk2(), hvo.getBa_mng_psnpk2()) || !StringUtils.equals(originVOs[0].getBa_mng_psnpk3(), hvo.getBa_mng_psnpk3()) || aggvo[0].getChildrenVO().length != 0) {
					//�޸��˷����˻����ʱ����ʹ�õĵ�Ԫ�������޸�
					processer.addBeforeRule(new WaUnitDataIsNotUsedRule());
				}
				processer.addBeforeRule(new WaUnitPsnUniqueCheckRule());
			}
			processer.before(aggvo);
			/*
			VOUpdate<WaBaUnitHVO> upd = new VOUpdate<WaBaUnitHVO>();
			WaBaUnitHVO[] superVOs = upd.update(new WaBaUnitHVO[] { hvo }, originVOs);
			*/
			String pk = persist.saveBillWithRealDelete(vo);
			BillQuery<AggWaBaUnitHVO> querybill = new BillQuery<AggWaBaUnitHVO>(AggWaBaUnitHVO.class);
			//			return aggvo;
			return querybill.query(new String[] { pk });
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	private WaBaUnitHVO[] getTreeCardVOs(WaBaUnitHVO[] vos) {
		String[] ids = this.getIDS(vos);
		VOQuery<WaBaUnitHVO> query = new VOQuery<WaBaUnitHVO>(WaBaUnitHVO.class);
		return query.query(ids);
	}

	private String[] getIDS(WaBaUnitHVO[] vos) {
		int size = vos.length;
		String[] ids = new String[size];
		for (int i = 0; i < size; i++) {
			ids[i] = vos[i].getPrimaryKey();
		}
		return ids;
	}

	// ��ѯ����
	public Object[] querytreeinfo(String whereSql) throws BusinessException {
		//		VOQuery<WaBaUnitHVO> query = new VOQuery<WaBaUnitHVO>(WaBaUnitHVO.class);
		//		return query.query(whereSql, null);
		String sql = " isnull(dr,0)=0 and " + whereSql;
		return query.queryBillOfVOByCond(AggWaBaUnitHVO.class, sql, false).toArray();
	}

	/**
	 * ����תMap
	 * 
	 * @param key
	 * @param vos
	 * @return
	 */
	public <T extends ISuperVO> Map<String, T> arrayvoToMap(String key, T... vos) {
		if (vos == null || vos.length == 0) {
			return null;
		}
		Map<String, T> returnMap = new HashMap<String, T>();
		for (T vo : vos) {
			returnMap.put((String) vo.getAttributeValue(key), vo);
		}
		return returnMap;
	}

	/**
	 * ͬ��BVO����ԱΪ��ȷ��λ�ü�psnjob
	 * 
	 * @param hashOrgUnit key��pk_dept value:pk_wa_ba_unit
	 * @param unitBVOMap key:psndoc vaue:��Ӧ��unitbvo
	 * @param psnjobMap key:psndoc vaue:psnjobvo
	 * @throws DAOException
	 */
	public void syncUnitBVO(Map<String, String> hashOrgUnit, Map<String, ISuperVO> unitBVOMap, Map<String, PsnJobVO> psnjobMap)
			throws BusinessException {
		// ����Ӧ�������Ա
		// 1.�������Ա�Ѵ����ڵ�ǰ��֯��bvo�У�����Ӧ����֯�Ƿ���ȷ��pk_psnjob�Ƿ���ȷ
		// 2.�������Ա�����ڵ�ǰ��֯��������,����ȷ���Ƿ��ڷǵ�ǰ�������У�����bvo��ɾ������Ա���ٲ�����Ա
		Iterator<Map.Entry<String, PsnJobVO>> it = psnjobMap.entrySet().iterator();
		List<WaBaUnitBVO> updateList = new ArrayList<WaBaUnitBVO>();
		List<String> deletePsnPks = new ArrayList<String>();
		List<WaBaUnitBVO> insertList = new ArrayList<WaBaUnitBVO>();
		while (it.hasNext()) {
			Map.Entry<String, PsnJobVO> entry = (Map.Entry<String, PsnJobVO>) it.next();
			PsnJobVO psnJobVO = entry.getValue();
			if (unitBVOMap.containsKey(entry.getKey())) {//��Աһһ��Ӧ
				WaBaUnitBVO bvo = (WaBaUnitBVO) unitBVOMap.get(entry.getKey());
				if (!StringUtils.equals(hashOrgUnit.get(psnJobVO.getPk_dept()), bvo.getPk_wa_ba_unit()) || !StringUtils.equals(psnJobVO.getPk_psnjob(), bvo.getPk_psnjob())) {
					bvo.setPk_wa_ba_unit(hashOrgUnit.get(psnJobVO.getPk_dept()));
					bvo.setPk_psnjob(psnJobVO.getPk_psnjob());
					updateList.add(bvo);
				}
				unitBVOMap.remove(entry.getKey());

			} else {
				//��ǰӦ������˲���bvo����
				deletePsnPks.add(entry.getKey());
				WaBaUnitBVO insertvo = new WaBaUnitBVO();
				insertvo.setPk_psndoc(entry.getKey());
				insertvo.setPk_psnjob(psnJobVO.getPk_psnjob());
				insertvo.setPk_wa_ba_unit(hashOrgUnit.get(psnJobVO.getPk_dept()));
				insertList.add(insertvo);
			}
			it.remove();
		}
		deletePsnPks.addAll(unitBVOMap.keySet());
		String insql = SQLHelper.joinToInSql(deletePsnPks.toArray(new String[0]), -1);
		getDao().deleteByClause(WaBaUnitBVO.class, "pk_psndoc in (" + insql + ")");
		getDao().updateVOList(updateList);
		getDao().insertVOList(insertList);
	}

	/**
	 * @return dao
	 */
	public BaseDAO getDao() {
		if (this.dao == null) {
			this.dao = new BaseDAO();
		}
		return this.dao;
	}
}