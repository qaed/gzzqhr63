package nc.ui.hrwa.wa_ba_sch.ace.serviceproxy;

import nc.bs.bank_cvp.compile.registry.BussinessMethods;
import nc.ui.pubapp.pub.task.ISingleBillService;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;

/**
 * 示例单据的操作代理
 * 
 * @since 6.0
 * @version 2011-7-6 上午08:31:09
 * @author duy
 */
public class AceWaBaSchDeleteProxy implements ISingleBillService<AggWaBaSchHVO> {

	@Override
	public AggWaBaSchHVO operateBill(AggWaBaSchHVO aggvo) throws Exception {
		// TODO 删除操作
		throw new Exception("AceWaBaSchDeleteProxy添加删除操作");
//		return null;
	}
	
}
