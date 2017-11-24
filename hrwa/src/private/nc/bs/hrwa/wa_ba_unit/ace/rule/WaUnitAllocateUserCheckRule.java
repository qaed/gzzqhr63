package nc.bs.hrwa.wa_ba_unit.ace.rule;

import java.util.ArrayList;
import java.util.List;

import nc.bs.logging.Logger;
import nc.hr.utils.SQLHelper;
import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.wa.wa_ba.unit.AggWaBaUnitHVO;
import nc.vo.wa.wa_ba.unit.WaBaUnitHVO;

/**
 * ������䵥Ԫ���������û�����У��
 * 
 * @author tsheay
 */
public class WaUnitAllocateUserCheckRule implements IRule<AggWaBaUnitHVO> {
	@Override
	public void process(AggWaBaUnitHVO[] aggvo) {
		Logger.debug("У�齱��Ԫ�������û�����У��:" + this.getClass().getName());
		if (aggvo == null || aggvo.length == 0) {
			return;
		}
		WaBaUnitHVO hvo = aggvo[0].getParentVO();
		check(hvo);
	}

	/**
	 * У��Ψһ��
	 * 
	 * @param hvo
	 */
	private void check(WaBaUnitHVO hvo) {
		if (hvo == null) {
			return;
		}
		IRowSet rowSet = new DataAccessUtils().query(this.getCheckSql(hvo));
		if (rowSet.size() > 0) {
			StringBuilder returnMsg = new StringBuilder();
			returnMsg.append("����Ա���û������ڣ��޷���Ϊ�����ˣ����飡\t\r\n");

			while (rowSet.next()) {
				returnMsg.append(rowSet.getString(0) + " ");
				returnMsg.append(rowSet.getString(1) + "\t");
			}
			ExceptionUtils.wrappBusinessException(returnMsg.toString());
		}
	}

	/**
	 * ƴ��Ψһ��У���sql
	 * 
	 * @param hvo 
	 * @return
	 */
	private String getCheckSql(WaBaUnitHVO hvo) {
		List<String> mngpks = new ArrayList<String>();
		mngpks.add(hvo.getBa_mng_psnpk());
		mngpks.add(hvo.getBa_mng_psnpk2());
		mngpks.add(hvo.getBa_mng_psnpk3());//����ֱ�Ӽӿ��ܻ���nullֵ,������insql��ʱ���ȥ��
		String insql = SQLHelper.joinToInSql(mngpks.toArray(new String[0]), -1);

		StringBuffer sql = new StringBuffer();
		sql.append("select code,name from bd_psndoc ");
		sql.append(" where not exists (select 1 from sm_user where bd_psndoc.pk_psndoc = sm_user.pk_psndoc) ");
		sql.append("   and pk_psndoc in(" + insql + ") ");
		return sql.toString();
	}
}
