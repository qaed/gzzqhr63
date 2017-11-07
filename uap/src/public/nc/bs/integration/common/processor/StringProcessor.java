/**
 * 
 */
package nc.bs.integration.common.processor;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.jdbc.framework.processor.BaseProcessor;

/**
 * ����������࣬����һ��String���͵ĵ�ֵ
 * @author rime
 *
 */
public class StringProcessor extends BaseProcessor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5323696816665148038L;

	/* (non-Javadoc)
	 * @see nc.jdbc.framework.processor.BaseProcessor#processResultSet(java.sql.ResultSet)
	 */
	@Override
	public Object processResultSet(ResultSet rs) throws SQLException {
		if (rs.next()) {
			return rs.getString(1);
		} else {
			return null;
		}
	}

}
