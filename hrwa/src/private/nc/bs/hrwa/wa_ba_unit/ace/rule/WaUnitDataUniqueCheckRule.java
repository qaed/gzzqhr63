package nc.bs.hrwa.wa_ba_unit.ace.rule;

import freemarker.debug.Debugger;
import nc.bs.hrss.pub.Logger;
import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.logging.Debug;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pattern.log.Log;
import nc.vo.wa.wa_ba.unit.AggWaBaUnitHVO;
import nc.vo.wa.wa_ba.unit.WaBaUnitHVO;

/**
 * 奖金分配单元编码唯一规则
 * 
 * @author tsheay
 */
public class WaUnitDataUniqueCheckRule implements IRule<AggWaBaUnitHVO> {
	@Override
	public void process(AggWaBaUnitHVO[] aggvo) {
		Logger.debug("校验奖金单元编码是否唯一:" + this.getClass().getName());
		if (aggvo == null || aggvo.length == 0) {
			return;
		}
		WaBaUnitHVO hvo = aggvo[0].getParentVO();
		checkDBUnique(hvo);
	}

	/**
	 * 校验唯一性
	 * 
	 * @param hvo
	 */
	private void checkDBUnique(WaBaUnitHVO hvo) {
		if (hvo == null) {
			return;
		}
		IRowSet rowSet = new DataAccessUtils().query(this.getCheckSql(hvo));
		if (rowSet.size() > 0) {
			ExceptionUtils.wrappBusinessException("保存失败，当前所新增或修改的信息在该集团已经存在编码相同的记录。");
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
		sql.append("select code,name ");
		sql.append("  from wa_ba_unit");
		sql.append(" where isnull(dr,0)=0 ");
		sql.append(" and ");
		sql.append(" (code ='");
		sql.append(vo.getCode());
		sql.append("' ");
		// sql.append(" or ");
		// sql.append(" name='");
		// sql.append(vo.getName());
		// sql.append("' ");
		sql.append(");");
		return sql.toString();
	}
}
