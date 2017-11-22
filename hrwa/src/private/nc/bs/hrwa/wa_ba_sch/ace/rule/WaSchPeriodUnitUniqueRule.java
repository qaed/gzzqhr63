package nc.bs.hrwa.wa_ba_sch.ace.rule;

import java.util.ArrayList;
import java.util.List;

import nc.bs.hrss.pub.Logger;
import nc.hr.utils.SQLHelper;
import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.pub.ISuperVO;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;
import nc.vo.wa.wa_ba.sch.WaBaSchBVO;
import nc.vo.wa.wa_ba.sch.WaBaSchHVO;

/**
 * ������䷽���У�ÿ�µķ��䵥ԪΨһ
 * 
 * @author tsheay
 */
public class WaSchPeriodUnitUniqueRule implements IRule<AggWaBaSchHVO> {
	@Override
	public void process(AggWaBaSchHVO[] aggvo) {
		Logger.debug("У�齱�𷽰�������¶����õ�ԪΨһ:" + this.getClass().getName());
		if (aggvo == null || aggvo.length == 0) {
			return;
		}
		checkDBUnique(aggvo[0]);
	}

	/**
	 * У��Ψһ��
	 * 
	 * @param aggvo
	 */
	private void checkDBUnique(AggWaBaSchHVO aggvo) {
		if (aggvo == null) {
			return;
		}
		IRowSet rowSet = new DataAccessUtils().query(this.getCheckSql(aggvo));
		if (rowSet.size() > 0) {
			StringBuilder msg = new StringBuilder();
			while(rowSet.next()){
				msg.append(rowSet.getString(0) + " " +rowSet.getString(1) +"\t");
			}
			ExceptionUtils.wrappBusinessException("����ʧ��,���µ�Ԫ��ǰ�ڼ������������뵥\n "+msg.toString());
		}
	}

	/**
	 * ƴ��Ψһ��У���sql
	 * 
	 * @param bill
	 * @return
	 */
	private String getCheckSql(AggWaBaSchHVO aggvo) {
		ISuperVO[] spuerbvos = aggvo.getChildren(WaBaSchBVO.class);
		List<String> unitpks = new ArrayList<String>();
		for (int i = 0; i < spuerbvos.length; i++) {
			WaBaSchBVO waBaSchBVO = (WaBaSchBVO)spuerbvos[i];
			unitpks.add(waBaSchBVO.getBa_unit_code());
		}
		String insql = SQLHelper.joinToInSql(unitpks.toArray(new String[0]), -1);
		WaBaSchHVO hvo = aggvo.getParentVO();
		StringBuffer sql = new StringBuffer();
		sql.append("select waunit.code,waunit.name from wa_ba_sch_unit schunit ");
		sql.append("    left join wa_ba_sch_h h on schunit.pk_ba_sch_h=h.pk_ba_sch_h ");
		sql.append("    left join wa_ba_unit waunit on waunit.pk_wa_ba_unit=schunit.ba_unit_code ");
		sql.append("    where schunit.ba_unit_code in (" + insql + ") ");
		sql.append("    and h.cyear='"+hvo.getCyear()+"' and h.cperiod='"+hvo.getCperiod()+"' ");
		sql.append("    and isnull(schunit.dr,0)=0 ");
		sql.append("	and h.pk_ba_sch_h<>'" + hvo.getPk_ba_sch_h() + "';");
		return sql.toString();
	}
}
