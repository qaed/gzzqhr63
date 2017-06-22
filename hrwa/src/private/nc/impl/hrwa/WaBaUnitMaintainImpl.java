package nc.impl.hrwa;

import java.util.ArrayList;

import nc.bs.framework.common.NCLocator;
import nc.impl.pub.ace.AceWaBaUnitPubServiceImpl;
import nc.impl.pubapp.pattern.data.bill.BillInsert;
import nc.impl.pubapp.pattern.data.bill.BillQuery;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.IMDPersistenceService;
import nc.vo.pub.BusinessException;
import nc.vo.wa.wa_ba_unit.AggWaBaUnitHVO;
import nc.vo.wa.wa_ba_unit.WaBaUnitHVO;

public class WaBaUnitMaintainImpl extends AceWaBaUnitPubServiceImpl implements nc.itf.hrwa.IWaBaUnitMaintain {
	/*
	 * 修改所有接口以及实现、把其中的具体的主 VO 类型修改为聚合 VO 类型
	 */
	IMDPersistenceService persist = NCLocator.getInstance().lookup(IMDPersistenceService.class);
	IMDPersistenceQueryService query = NCLocator.getInstance().lookup(IMDPersistenceQueryService.class);

	@Override
	public void delete(Object vos) throws BusinessException {
		// super.deletetreeinfo(vos);
		persist.deleteBill(vos);
	}

	@Override
	public Object insert(Object vos) throws BusinessException {
		// return super.inserttreeinfo(vos);
		BillInsert<AggWaBaUnitHVO> billinsert = new BillInsert<AggWaBaUnitHVO>();
		AggWaBaUnitHVO[] aggvo = new AggWaBaUnitHVO[1];
		aggvo[0] = (AggWaBaUnitHVO) vos;
		return billinsert.insert(aggvo)[0];
	}

	@Override
	public Object update(Object vos) throws BusinessException {
		// return super.updatetreeinfo(vos);
		String pk = persist.saveBill(vos);
		ArrayList<String> pks = new ArrayList<String>();
		pks.add(pk);
		BillQuery<AggWaBaUnitHVO> query = new BillQuery<AggWaBaUnitHVO>(AggWaBaUnitHVO.class);
		return query.query(pks.toArray(new String[0]));
	}

	@Override
	public Object[] query(String whereSql) throws BusinessException {
//		return super.querytreeinfo(whereSql);
		String sql = " isnull(dr,0)=0 " + whereSql;
		return query.queryBillOfVOByCond(AggWaBaUnitHVO.class, sql,false).toArray();
	}
}
