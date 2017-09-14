package nc.bs.hrwa.wa_ba_sch.ace.rule;

import nc.bs.hrss.pub.Logger;
import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;
import nc.vo.wa.wa_ba.sch.WaBaSchHVO;

/**
 * ������䷽������Ψһ����
 * 
 * @author tsheay
 */
public class WaSchDataUniqueCheckRule implements IRule<AggWaBaSchHVO> {
	@Override
	public void process(AggWaBaSchHVO[] aggvo) {
		Logger.debug("У�齱�𷽰����롢�����Ƿ�Ψһ:" + this.getClass().getName());
		if (aggvo == null || aggvo.length == 0) {
			return;
		}
		WaBaSchHVO hvo = aggvo[0].getParentVO();
		checkDBUnique(hvo);
	}

	/**
	 * У��Ψһ��
	 * 
	 * @param hvo
	 */
	private void checkDBUnique(WaBaSchHVO hvo) {
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
	private String getCheckSql(WaBaSchHVO vo) {
		StringBuffer sql = new StringBuffer();
		sql.append("select sch_code,sch_name ");
		sql.append("	from wa_ba_sch_h");
		sql.append("	where isnull(dr,0)=0 ");
		sql.append("	and ");
		sql.append("	(sch_code ='" + vo.getSch_code() + "'");
		sql.append("	or sch_name='" + vo.getSch_name() + "')");
		sql.append("	and pk_ba_sch_h<>'" + vo.getPk_ba_sch_h() + "';");
		return sql.toString();
	}
}
