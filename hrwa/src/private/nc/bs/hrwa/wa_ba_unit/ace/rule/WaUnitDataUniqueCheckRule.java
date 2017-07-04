package nc.bs.hrwa.wa_ba_unit.ace.rule;

import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.wa.wa_ba.unit.AggWaBaUnitHVO;
import nc.vo.wa.wa_ba.unit.WaBaUnitHVO;

public class WaUnitDataUniqueCheckRule implements IRule<AggWaBaUnitHVO> {
	@Override
	public void process(AggWaBaUnitHVO[] aggvo) {
		if (aggvo == null || aggvo.length == 0) {
			return;
		}
		WaBaUnitHVO hvo = aggvo[0].getParentVO();
		checkDBUnique(hvo);
	}

	/**
	 * У��Ψһ��
	 * 
	 * @param hvo
	 */
	private void checkDBUnique(WaBaUnitHVO hvo) {
		if (hvo == null) {
			return;
		}
		IRowSet rowSet = new DataAccessUtils().query(this.getCheckSql(hvo));
		if (rowSet.size() > 0) {
			ExceptionUtils.wrappBusinessException("����ʧ�ܣ���ǰ���������޸ĵ���Ϣ�ڸü����Ѿ����ڱ����������ͬ�ļ�¼��");
		}
	}

	/**
	 * ƴ��Ψһ��У���sql
	 * 
	 * @param bill
	 * @return
	 */
	private String getCheckSql(WaBaUnitHVO vo) {
		StringBuffer sql = new StringBuffer();
		sql.append("select code,name ");
		sql.append("  from wa_ba_unit");
		sql.append(" where isnull(dr,0)=0 ");
		sql.append(" and ");
		sql.append(" (code ='");
		sql.append(vo.getCode());
		sql.append("' ");
		sql.append(" or ");
		sql.append(" name='");
		sql.append(vo.getName());
		sql.append("' ");
		sql.append(");");
		return sql.toString();
	}
}
