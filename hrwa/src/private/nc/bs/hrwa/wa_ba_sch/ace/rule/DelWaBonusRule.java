package nc.bs.hrwa.wa_ba_sch.ace.rule;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.frame.persistence.PersistenceDAO;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.itf.hr.frame.PersistenceDbException;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pubapp.AppContext;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.trade.pub.IBillStatus;
import nc.vo.wa.wa_ba.bonus.WaBaBonusHVO;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;
import nc.vo.wa.wa_ba.sch.WaBaSchBVO;
import nc.vo.wa.wa_ba.sch.WaBaSchHVO;

public class DelWaBonusRule implements IRule<AggWaBaSchHVO> {
	IMDPersistenceQueryService query = NCLocator.getInstance().lookup(IMDPersistenceQueryService.class);

	@Override
	public void process(AggWaBaSchHVO[] aggvo) {
		Logger.init(DelWaBonusRule.class);
		Logger.debug("删除汇总单据nc.bs.hrwa.wa_ba_sch.ace.rule.GenWaBonusRule");
		if (aggvo == null || aggvo.length == 0) {
			return;
		}
		delWaBaBonusByBaSch(aggvo[0]);
		// ExceptionUtils.wrappBusinessException("测试");
	}

	public void delWaBaBonusByBaSch(AggWaBaSchHVO aggvo) {
		WaBaSchHVO schHVO = (WaBaSchHVO) aggvo.getParentVO();

		PersistenceDAO pDAO = new PersistenceDAO();
		WaBaBonusHVO hvo = new WaBaBonusHVO();
		hvo.setPk_bonus(schHVO.getPk_ba_sch_h());
		try {
			pDAO.delete(hvo);
		} catch (PersistenceDbException e) {
			ExceptionUtils.wrappBusinessException("删除奖金汇总单据时发生异常!");
		}
	}
}
