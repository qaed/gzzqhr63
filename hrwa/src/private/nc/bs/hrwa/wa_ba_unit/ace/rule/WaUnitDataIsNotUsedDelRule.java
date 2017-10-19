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
 * �Ƿ�ʹ��У�����
 * 
 * @author tsheay
 */
public class WaUnitDataIsNotUsedDelRule implements IRule<AggWaBaUnitHVO> {
	@Override
	public void process(AggWaBaUnitHVO[] aggvo) {
		Logger.debug("У�齱��Ԫ�Ƿ��ѱ�ʹ��:" + this.getClass().getName());
		if (aggvo == null || aggvo.length == 0) {
			return;
		}
		WaBaUnitHVO hvo = aggvo[0].getParentVO();
		checkIsNotUsed(hvo);
	}

	/**
	 * У��Ψһ��
	 * 
	 * @param hvo
	 */
	private void checkIsNotUsed(WaBaUnitHVO hvo) {
		if (hvo == null) {
			return;
		}
		IRowSet rowSet = new DataAccessUtils().query(this.getCheckSql(hvo));
		if (rowSet.size() > 0) {
			ExceptionUtils.wrappBusinessException("����ʧ�ܣ���ǰ��Ϣ�ѱ�ʹ�á�����ɾ��������Ϣ");
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

		sql.append("select b.code,b.name ");
		sql.append("  from wa_ba_sch_unit a");
		sql.append("  left join wa_ba_unit b on a.ba_unit_code=b.pk_wa_ba_unit");
		sql.append("  where isnull(a.dr,0)=0 ");
		sql.append("  and (a.ba_unit_code ='" + vo.getPk_wa_ba_unit() + "'); ");
		return sql.toString();
	}
}
