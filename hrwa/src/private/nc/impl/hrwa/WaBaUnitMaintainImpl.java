package nc.impl.hrwa;

import java.util.ArrayList;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrwa.wa_ba_unit.ace.rule.WaUnitDataUniqueCheckRule;
import nc.impl.pub.ace.AceWaBaUnitPubServiceImpl;
import nc.impl.pubapp.pattern.data.bill.BillInsert;
import nc.impl.pubapp.pattern.data.bill.BillQuery;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.IMDPersistenceService;
import nc.vo.bd.meta.AggVOBDObject;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pubapp.AppContext;
import nc.vo.wa.wa_ba.unit.AggWaBaUnitHVO;
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
}
