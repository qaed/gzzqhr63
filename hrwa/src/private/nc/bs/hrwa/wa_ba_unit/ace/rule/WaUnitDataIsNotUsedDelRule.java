package nc.bs.hrwa.wa_ba_unit.ace.rule;

import java.util.Calendar;

import org.apache.commons.httpclient.methods.GetMethod;

import nc.bs.hrss.pub.Logger;
import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.wa.wa_ba.unit.AggWaBaUnitHVO;
import nc.vo.wa.wa_ba.unit.WaBaUnitHVO;

/**
 * 是否被使用校验规则
 * 
 * @author tsheay
 */
public class WaUnitDataIsNotUsedDelRule implements IRule<AggWaBaUnitHVO> {
	@Override
	public void process(AggWaBaUnitHVO[] aggvo) {
		Logger.debug("校验奖金单元是否已被使用:" + this.getClass().getName());
		if (aggvo == null || aggvo.length == 0) {
			return;
		}
		WaBaUnitHVO hvo = aggvo[0].getParentVO();
		checkIsNotUsed(hvo);
	}

	/**
	 * 校验唯一性
	 * 
	 * @param hvo
	 */
	private void checkIsNotUsed(WaBaUnitHVO hvo) {
		if (hvo == null) {
			return;
		}
		IRowSet rowSet = new DataAccessUtils().query(this.getCheckSql(hvo));
		if (rowSet.size() > 0) {
			ExceptionUtils.wrappBusinessException("操作失败，当前信息已被使用。请先删除下游信息");
		}
	}

	/**
	 * 拼接唯一性校验的sql
	 * 
	 * @param bill
	 * @return
	 */
	private String getCheckSql(WaBaUnitHVO vo) {
		StringBuffer sql = new StringBuffer();

		sql.append("select b.code,b.name ");
		sql.append("  from wa_ba_sch_unit a");
		sql.append("  left join wa_ba_unit b on a.ba_unit_code=b.pk_wa_ba_unit");
		sql.append("  where isnull(a.dr,0)=0 ");
		sql.append("  and (a.ba_unit_code ='" + vo.getPk_wa_ba_unit() + "'); ");
		return sql.toString();
	}
}
