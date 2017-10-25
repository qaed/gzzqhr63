package nc.bs.hrwa.wa_ba_unit.ace.rule;

import nc.bs.logging.Logger;
import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.wa.wa_ba.unit.AggWaBaUnitHVO;
import nc.vo.wa.wa_ba.unit.WaBaUnitBVO;

/**
 * ������䵥Ԫ��Աȫ��Ψһ����
 * 
 * @author tsheay
 */
public class WaUnitPsnUniqueCheckRule implements IRule<AggWaBaUnitHVO> {
	@Override
	public void process(AggWaBaUnitHVO[] aggvo) {
		Logger.debug("У�齱��Ԫ��Ա��ȫ���Ƿ�Ψһ:" + this.getClass().getName());
		if (aggvo == null || aggvo.length == 0) {
			return;
		}
		WaBaUnitBVO[] bvos = (WaBaUnitBVO[]) aggvo[0].getChildren(WaBaUnitBVO.class);
		checkDBUnique(bvos);
	}

	/**
	 * У��Ψһ��
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
			returnMsg.append("����Ա���Ѿ��������������䵥Ԫ�У����飡\t\r\n");

			while (rowSet.next()) {
				returnMsg.append(rowSet.getString(0) + " ");
				returnMsg.append(rowSet.getString(1) + " ");
				returnMsg.append(rowSet.getString(2) + "\t");
			}
			ExceptionUtils.wrappBusinessException(returnMsg.toString());
		}
	}

	/**
	 * ƴ��Ψһ��У���sql
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
