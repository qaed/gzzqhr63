package nc.impl.pub.ace;

import java.util.ArrayList;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrwa.wa_ba_unit.ace.rule.WaUnitDataIsNotUsedRule;
import nc.bs.hrwa.wa_ba_unit.ace.rule.WaUnitDataUniqueCheckRule;
import nc.impl.pubapp.pattern.data.bill.BillInsert;
import nc.impl.pubapp.pattern.data.bill.BillQuery;
import nc.impl.pubapp.pattern.data.vo.VOQuery;
import nc.impl.pubapp.pattern.data.vo.VOUpdate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.IMDPersistenceService;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pubapp.AppContext;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.wa.wa_ba.unit.AggWaBaUnitHVO;
import nc.vo.wa.wa_ba.unit.WaBaUnitHVO;

/**
 * @see nc.impl.hrwa.WaBaUnitMaintainImpl 重写了方法，把主VO改为聚合VO
 * @author tsheay
 */
public abstract class AceWaBaUnitPubServiceImpl {
	IMDPersistenceService persist = NCLocator.getInstance().lookup(IMDPersistenceService.class);
	IMDPersistenceQueryService query = NCLocator.getInstance().lookup(IMDPersistenceQueryService.class);

	// 增加方法
	public Object inserttreeinfo(Object vo) throws BusinessException {
		try {
			AggWaBaUnitHVO[] aggvo = new AggWaBaUnitHVO[1];
			aggvo[0] = (AggWaBaUnitHVO) vo;
			// 添加BP规则
			AroundProcesser<AggWaBaUnitHVO> processer = new AroundProcesser<AggWaBaUnitHVO>(null);
			processer.addBeforeRule(new WaUnitDataUniqueCheckRule());
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
			processer.addBeforeRule(new WaUnitDataIsNotUsedRule());
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
			if (originVOs != null && originVOs[0] != null && !originVOs[0].getCode().equals(hvo.getCode())) {
				processer.addBeforeRule(new WaUnitDataUniqueCheckRule());
				processer.addBeforeRule(new WaUnitDataIsNotUsedRule());
			}
			processer.before(aggvo);
			/*
			VOUpdate<WaBaUnitHVO> upd = new VOUpdate<WaBaUnitHVO>();
			WaBaUnitHVO[] superVOs = upd.update(new WaBaUnitHVO[] { hvo }, originVOs);
			*/
			String pk = persist.saveBill(vo);
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
}