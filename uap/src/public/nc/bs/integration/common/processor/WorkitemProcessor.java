/**
 * 
 */
package nc.bs.integration.common.processor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import nc.jdbc.framework.processor.BaseProcessor;
import nc.vo.integration.workitem.WorkitemVO;

/**
 * 待办消息数据集处理类，返回WorkitemVO[]
 * @author rime
 *
 */
public class WorkitemProcessor extends BaseProcessor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3779181344573991509L;

	/* (non-Javadoc)
	 * @see nc.jdbc.framework.processor.BaseProcessor#processResultSet(java.sql.ResultSet)
	 */
	@Override
	public Object processResultSet(ResultSet rs) throws SQLException {
		ArrayList<WorkitemVO> list = new ArrayList<WorkitemVO>();
		while(rs.next()) {
			WorkitemVO item = new WorkitemVO();
			item.setSendDate(rs.getString(1));
			item.setRecipientId(rs.getString(2));
			item.setRecipient(rs.getString(3));
			item.setSenderId(rs.getString(4));
			item.setSender(rs.getString(5));
			item.setBillId(rs.getString(6));
			item.setBillNo(rs.getString(7));
			item.setBillType(rs.getString(8));
			item.setBillTypeName(rs.getString(9));
			item.setMessageNote(rs.getString(10));
			item.setActionType(rs.getString(11));
			item.setWorkitemId(rs.getString(12));
			item.setSystemCode(rs.getString(13));
			item.setSystemName(rs.getString(14));
			item.setNodeCode(rs.getString(15));
			item.setWebNodeCode(rs.getString(16));
			item.setClassName(rs.getString(17));
			item.setCorpId(rs.getString(18));
			item.setCorpCode(rs.getString(19));
			item.setCorpName(rs.getString(20));
			list.add(item);
		}
		
		return list.size() > 0 ? list.toArray(new WorkitemVO[0]) : null;
	}

}
