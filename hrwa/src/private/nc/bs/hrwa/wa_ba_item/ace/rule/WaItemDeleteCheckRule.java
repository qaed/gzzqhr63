package nc.bs.hrwa.wa_ba_item.ace.rule;

import nc.bs.logging.Logger;
import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.wa.wa_ba.item.ItemsVO;

public class WaItemDeleteCheckRule implements IRule<ItemsVO> {
	@Override
	public void process(ItemsVO[] vo) {
		if (vo == null || vo.length == 0) {
			return;
		}
		Logger.error("nc.bs.hrwa.wa_ba_item.ace.rule.WaItemDeleteCheckRule__校验是否被使用");
		//		checkUsed(vo[0]);
	}

	/**
	 * 校验是否已经被使用其他节点使用
	 * 
	 * @param hvo
	 */
	private void checkUsed(ItemsVO vo) {
		if (vo == null) {
			return;
		}
		IRowSet rowSet = new DataAccessUtils().query(this.getSql(vo));
		if (rowSet.size() > 0) {
			ExceptionUtils.wrappBusinessException("删除失败，当前条目已经被其他节点引用，请先删除引用");
		}
	}

	/**
	 * 拼接sql
	 * 
	 * @param vo
	 * @return sql
	 */
	private String getSql(ItemsVO vo) {
		StringBuffer sql = new StringBuffer();
		sql.append("select code,name ");
		sql.append("  from wa_ba_item");
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
