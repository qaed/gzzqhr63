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
 * 奖金分配单元，分配人用户存在校验
 * 
 * @author tsheay
 */
public class WaUnitAllocateUserCheckRule implements IRule<AggWaBaUnitHVO> {
	@Override
	public void process(AggWaBaUnitHVO[] aggvo) {
		Logger.debug("校验奖金单元分配人用户存在校验:" + this.getClass().getName());
		if (aggvo == null || aggvo.length == 0) {
			return;
		}
		WaBaUnitHVO hvo = aggvo[0].getParentVO();
		check(hvo);
	}

	/**
	 * 校验唯一性
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
			returnMsg.append("以下员工用户不存在，无法作为分配人，请检查！\t\r\n");

			while (rowSet.next()) {
				returnMsg.append(rowSet.getString(0) + " ");
				returnMsg.append(rowSet.getString(1) + "\t");
			}
			ExceptionUtils.wrappBusinessException(returnMsg.toString());
		}
	}

	/**
	 * 拼接唯一性校验的sql
	 * 
	 * @param hvo 
	 * @return
	 */
	private String getCheckSql(WaBaUnitHVO hvo) {
		List<String> mngpks = new ArrayList<String>();
		mngpks.add(hvo.getBa_mng_psnpk());
		mngpks.add(hvo.getBa_mng_psnpk2());
		mngpks.add(hvo.getBa_mng_psnpk3());//这里直接加可能会有null值,但构造insql的时候会去掉
		String insql = SQLHelper.joinToInSql(mngpks.toArray(new String[0]), -1);

		StringBuffer sql = new StringBuffer();
		sql.append("select code,name from bd_psndoc ");
		sql.append(" where not exists (select 1 from sm_user where bd_psndoc.pk_psndoc = sm_user.pk_psndoc) ");
		sql.append("   and pk_psndoc in(" + insql + ") ");
		return sql.toString();
	}
}
