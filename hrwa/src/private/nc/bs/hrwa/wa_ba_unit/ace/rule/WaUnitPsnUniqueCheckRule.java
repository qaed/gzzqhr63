package nc.bs.hrwa.wa_ba_unit.ace.rule;

import nc.bs.logging.Logger;
import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.wa.wa_ba.unit.AggWaBaUnitHVO;
import nc.vo.wa.wa_ba.unit.WaBaUnitBVO;

/**
 * 奖金分配单元人员全局唯一规则
 * 
 * @author tsheay
 */
public class WaUnitPsnUniqueCheckRule implements IRule<AggWaBaUnitHVO> {
	@Override
	public void process(AggWaBaUnitHVO[] aggvo) {
		Logger.debug("校验奖金单元人员在全局是否唯一:" + this.getClass().getName());
		if (aggvo == null || aggvo.length == 0) {
			return;
		}
		WaBaUnitBVO[] bvos = (WaBaUnitBVO[]) aggvo[0].getChildren(WaBaUnitBVO.class);
		checkDBUnique(bvos);
	}

	/**
	 * 校验唯一性
	 * 
	 * @param hvo
	 */
	private void checkDBUnique(WaBaUnitBVO bvos[]) {
		if (bvos == null || bvos.length == 0) {
			return;
		}
		IRowSet rowSet = new DataAccessUtils().query(this.getCheckSql(bvos));
		if (rowSet.size() > 0) {
			StringBuilder returnMsg = new StringBuilder();
			returnMsg.append("以下员工已经存在于其他分配单元中，请检查！\t\r\n");

			while (rowSet.next()) {
				returnMsg.append(rowSet.getString(0) + " ");
				returnMsg.append(rowSet.getString(1) + " ");
				returnMsg.append(rowSet.getString(2) + "\t");
			}
			ExceptionUtils.wrappBusinessException(returnMsg.toString());
		}
	}

	/**
	 * 拼接唯一性校验的sql
	 * 
	 * @param bill
	 * @return
	 */
	private String getCheckSql(WaBaUnitBVO[] bvos) {
		StringBuffer sql = new StringBuffer();
		sql.append("select psndoc.code psncode, psndoc.name psnname, unit.name unitname");
		sql.append(" from wa_ba_unit_b b");
		sql.append(" left join wa_ba_unit unit on b.pk_wa_ba_unit = unit.pk_wa_ba_unit");
		sql.append(" left join bd_psndoc psndoc on psndoc.pk_psndoc = b.pk_psndoc");
		sql.append(" where b.pk_psndoc in (");
		for (WaBaUnitBVO bvo : bvos) {
			sql.append("'" + bvo.getPk_psndoc() + "',");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(" )");
		sql.append(" and b.pk_wa_ba_unit <>'" + bvos[0].getPk_wa_ba_unit() + "'");
		return sql.toString();
	}
}
