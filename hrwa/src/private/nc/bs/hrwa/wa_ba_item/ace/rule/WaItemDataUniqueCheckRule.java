package nc.bs.hrwa.wa_ba_item.ace.rule;

import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.wa.wa_ba.item.ItemsVO;

public class WaItemDataUniqueCheckRule implements IRule<ItemsVO> {
	@Override
	public void process(ItemsVO[] vo) {
		if (vo == null || vo.length == 0) {
			return;
		}
		
		checkDBUnique(vo[0]);
	}

	/**
	 * У��Ψһ��
	 * 
	 * @param hvo
	 */
	private void checkDBUnique(ItemsVO vo) {
		if (vo == null) {
			return;
		}
		IRowSet rowSet = new DataAccessUtils().query(this.getCheckSql(vo));
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
	private String getCheckSql(ItemsVO vo) {
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
