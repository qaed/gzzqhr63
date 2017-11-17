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
 * @see nc.impl.hrwa.WaBaUnitMaintainImpl 重写了方法，把主VO改为聚合VO
 * @author tsheay
 */
public abstract class AceWaBaUnitPubServiceImpl {
	IMDPersistenceService persist = NCLocator.getInstance().lookup(IMDPersistenceService.class);
	IMDPersistenceQueryService query = NCLocator.getInstance().lookup(IMDPersistenceQueryService.class);
	private BaseDAO dao = null;

	// 增加方法
	public Object inserttreeinfo(Object vo) throws BusinessException {
		try {
			AggWaBaUnitHVO[] aggvo = new AggWaBaUnitHVO[1];
			aggvo[0] = (AggWaBaUnitHVO) vo;
			// 添加BP规则
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

	// 删除方法
	public void deletetreeinfo(Object vo) throws BusinessException {
		try {
			AggWaBaUnitHVO[] aggvo = new AggWaBaUnitHVO[1];
			aggvo[0] = (AggWaBaUnitHVO) vo;
			// 添加BP规则
			AroundProcesser<AggWaBaUnitHVO> processer = new AroundProcesser<AggWaBaUnitHVO>(null);
			processer.addBeforeRule(new WaUnitDataIsNotUsedDelRule());
			processer.before(aggvo);

			persist.deleteBillFromDB(aggvo[0]);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// 修改方法
	public Object updatetreeinfo(Object vo) throws BusinessException {
		try {

			AggWaBaUnitHVO[] aggvo = new AggWaBaUnitHVO[1];
			aggvo[0] = (AggWaBaUnitHVO) vo;
			WaBaUnitHVO hvo = aggvo[0].getParentVO();
			// 设置修改人和时间
			hvo.setModifier(AppContext.getInstance().getPkUser());
			hvo.setModifiedtime(new UFDateTime());
			// 添加BP规则
			AroundProcesser<AggWaBaUnitHVO> processer = new AroundProcesser<AggWaBaUnitHVO>(null);
			WaBaUnitHVO[] originVOs = this.getTreeCardVOs(new WaBaUnitHVO[] { hvo });
			if (originVOs != null && originVOs[0] != null) {

				if (!originVOs[0].getCode().equals(hvo.getCode())) {
					//修改编号时要校验，新编号不能重名
					processer.addBeforeRule(new WaUnitDataUniqueCheckRule());
				}

				if (!StringUtils.equals(originVOs[0].getBa_mng_psnpk(), hvo.getBa_mng_psnpk()) || !StringUtils.equals(originVOs[0].getBa_mng_psnpk2(), hvo.getBa_mng_psnpk2()) || !StringUtils.equals(originVOs[0].getBa_mng_psnpk3(), hvo.getBa_mng_psnpk3()) || aggvo[0].getChildrenVO().length != 0) {
					//修改了分配人或表体时，已使用的单元不允许修改
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

	// 查询方法
	public Object[] querytreeinfo(String whereSql) throws BusinessException {
		//		VOQuery<WaBaUnitHVO> query = new VOQuery<WaBaUnitHVO>(WaBaUnitHVO.class);
		//		return query.query(whereSql, null);
		String sql = " isnull(dr,0)=0 and " + whereSql;
		return query.queryBillOfVOByCond(AggWaBaUnitHVO.class, sql, false).toArray();
	}

	/**
	 * 数组转Map
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
	 * 同步BVO的人员为正确的位置及psnjob
	 * 
	 * @param hashOrgUnit key：pk_dept value:pk_wa_ba_unit
	 * @param unitBVOMap key:psndoc vaue:对应的unitbvo
	 * @param psnjobMap key:psndoc vaue:psnjobvo
	 * @throws DAOException
	 */
	public void syncUnitBVO(Map<String, String> hashOrgUnit, Map<String, ISuperVO> unitBVOMap, Map<String, PsnJobVO> psnjobMap)
			throws BusinessException {
		// 遍历应管理的人员
		// 1.如果该人员已存在在当前组织的bvo中：检查对应的组织是否正确，pk_psnjob是否正确
		// 2.如果该人员不存在当前组织管理部门中,但不确定是否在非当前管理部门中：先在bvo中删除该人员，再插入人员
		Iterator<Map.Entry<String, PsnJobVO>> it = psnjobMap.entrySet().iterator();
		List<WaBaUnitBVO> updateList = new ArrayList<WaBaUnitBVO>();
		List<String> deletePsnPks = new ArrayList<String>();
		List<WaBaUnitBVO> insertList = new ArrayList<WaBaUnitBVO>();
		while (it.hasNext()) {
			Map.Entry<String, PsnJobVO> entry = (Map.Entry<String, PsnJobVO>) it.next();
			PsnJobVO psnJobVO = entry.getValue();
			if (unitBVOMap.containsKey(entry.getKey())) {//人员一一对应
				WaBaUnitBVO bvo = (WaBaUnitBVO) unitBVOMap.get(entry.getKey());
				if (!StringUtils.equals(hashOrgUnit.get(psnJobVO.getPk_dept()), bvo.getPk_wa_ba_unit()) || !StringUtils.equals(psnJobVO.getPk_psnjob(), bvo.getPk_psnjob())) {
					bvo.setPk_wa_ba_unit(hashOrgUnit.get(psnJobVO.getPk_dept()));
					bvo.setPk_psnjob(psnJobVO.getPk_psnjob());
					updateList.add(bvo);
				}
				unitBVOMap.remove(entry.getKey());

			} else {
				//当前应管理的人不在bvo里面
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