package nc.vo.pubapp.pattern.pub;

import nc.md.model.impl.MDEnum;
import nc.md.model.type.IType;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

/**
 * sql��乹����������ƴдsql�����ٲ���Ҫ�Ĵ���
 * 
 * @since 6.0
 * @version 2006-6-20 18:50:23
 * @author ����
 */
public class SqlBuilder {

	/**
	 * ���ƴдsql��StringBuffer
	 */
	private StringBuffer buffer = new StringBuffer();

	private void append(MDEnum flag) {
		int type = flag.getReturnType();
		if (type == IType.TYPE_Integer) {
			this.buffer.append(flag.value());
		} else {
			this.buffer.append("'");
			this.buffer.append(flag.value());
			this.buffer.append("'");
		}
	}

	/**
	 * ��һ���ǿյĶ���ת��ΪStringƴд��sql���
	 * 
	 * @param obj �ǿյĶ���
	 */
	public void append(Object obj) {
		this.buffer.append(obj.toString());
	}

	/**
	 * ��һ���ַ���ƴд��sql���
	 * 
	 * @param str �ַ���
	 */
	public void append(String str) {
		this.buffer.append(str);
	}

	/**
	 * ��������ֵ���조���ڡ�����
	 * 
	 * @param name sql�ֶ���
	 * @param value intֵ
	 */
	public void append(String name, int value) {
		this.buffer.append(name);
		this.buffer.append("=");
		this.buffer.append(value);
		this.buffer.append(" ");
	}

	/**
	 * ����int����ֵ����in����
	 * 
	 * @param name sql�ֶ���
	 * @param values int����ֵ
	 */
	public void append(String name, int[] values) {
		this.buffer.append(name);
		this.buffer.append(" in (");
		int length = values.length;
		for (int i = 0; i < length; i++) {
			this.buffer.append(values[i]);
			this.buffer.append(",");
		}
		length = this.buffer.length();
		this.buffer.deleteCharAt(length - 1);
		this.buffer.append(") ");
	}

	/**
	 * ����Integerֵ���조���ڡ�����
	 * 
	 * @param name sql�ֶ���
	 * @param value Integerֵ
	 */
	public void append(String name, Integer value) {
		this.buffer.append(name);
		this.buffer.append("=");
		this.buffer.append(value);
		this.buffer.append(" ");
	}

	/**
	 * ����Integer����ֵ����in����
	 * 
	 * @param name sql�ֶ���
	 * @param values Integer����ֵ
	 */
	public void append(String name, Integer[] values) {
		this.buffer.append(name);
		this.buffer.append(" in (");
		int length = values.length;
		for (int i = 0; i < length; i++) {
			this.buffer.append(values[i]);
			this.buffer.append(",");
		}
		length = this.buffer.length();
		this.buffer.deleteCharAt(length - 1);
		this.buffer.append(") ");
	}

	/**
	 * ����ö��ֵ���조���ڡ�����
	 * 
	 * @param name sql�ֶ���
	 * @param flag ö��
	 */
	public void append(String name, MDEnum flag) {
		this.buffer.append(name);
		this.buffer.append("=");
		this.append(flag);
		this.buffer.append(" ");
	}

	/**
	 * ����ö������ֵ����in����
	 * 
	 * @param name sql�ֶ���
	 * @param flags ö������
	 */
	public void append(String name, MDEnum[] flags) {
		this.buffer.append(name);
		this.buffer.append(" in (");
		int length = flags.length;
		for (int i = 0; i < length; i++) {
			this.append(flags[i]);
			this.buffer.append(",");
		}
		length = this.buffer.length();
		this.buffer.deleteCharAt(length - 1);
		this.buffer.append(") ");
	}

	/**
	 * �����ַ���ֵ���조���ڡ�����
	 * 
	 * @param name sql�ֶ���
	 * @param value Stringֵ ����Ϊ�գ��������쳣����Ϊ��֪���Ƿ�Ҫ���~
	 */
	public void append(String name, String value) {
		if (value != null) {
			this.buffer.append(name);
			this.buffer.append("='");
			this.buffer.append(value);
			this.buffer.append("' ");
		} else {
			ExceptionUtils.unSupported();
		}
	}

	/**
	 * ��������ֵ����operator��ָ��������
	 * 
	 * @param name sql�ֶ���
	 * @param operator sql������
	 * @param value intֵ
	 */
	public void append(String name, String operator, int value) {
		this.buffer.append(name);
		this.buffer.append(operator);
		this.buffer.append(value);
		this.buffer.append(" ");
	}

	/**
	 * ����Integerֵ����operator��ָ��������
	 * 
	 * @param name sql�ֶ���
	 * @param operator sql������
	 * @param value Integerֵ
	 */
	public void append(String name, String operator, Integer value) {
		this.buffer.append(name);
		this.buffer.append(operator);
		this.buffer.append(value);
		this.buffer.append(" ");
	}

	/**
	 * �����ַ���ֵ����operator��ָ��������
	 * 
	 * @param name sql�ֶ���
	 * @param operator sql������
	 * @param value Stringֵ
	 */
	public void append(String name, String operator, String value) {
		this.buffer.append(name);
		this.buffer.append(operator);
		this.buffer.append(" '");
		this.buffer.append(value);
		this.buffer.append("' ");
	}

	/**
	 * ����UFDoubleֵ����operator��ָ��������
	 * 
	 * @param name sql�ֶ���
	 * @param operator sql������
	 * @param value UFDoubleֵ
	 */
	public void append(String name, String operator, UFDouble value) {
		this.buffer.append(name);
		this.buffer.append(operator);
		this.buffer.append(value);
		this.buffer.append(" ");
	}

	/**
	 * ����String����ֵ����in����
	 * 
	 * @param name sql�ֶ���
	 * @param values String����ֵ
	 */
	public void append(String name, String[] values) {
		int length = values.length;
		if (length == 1) {
			// 20171206 tsy ���ױ�����������
			//			this.append(name, values[0]);
			this.buffer.append(name);
			this.buffer.append("='");
			this.buffer.append(values[0]);
			this.buffer.append("' ");
			// 20171206 end
			return;
		}
		this.buffer.append(name);
		this.buffer.append(" in (");
		for (int i = 0; i < length; i++) {
			this.buffer.append("'");
			this.buffer.append(values[i]);
			this.buffer.append("'");
			this.buffer.append(",");
		}
		length = this.buffer.length();
		this.buffer.deleteCharAt(length - 1);
		this.buffer.append(") ");
	}

	/**
	 * ����UFBooleanֵ���조���ڡ�����
	 * 
	 * @param name sql�ֶ���
	 * @param value UFBooleanֵ
	 */
	public void append(String name, UFBoolean value) {
		this.buffer.append(name);
		this.buffer.append("='");
		this.buffer.append(value);
		this.buffer.append("' ");
	}

	/**
	 * ����UFDoubleֵ���조���ڡ�����
	 * 
	 * @param name sql�ֶ���
	 * @param value UFDoubleֵ
	 */
	public void append(String name, UFDouble value) {
		this.buffer.append(name);
		this.buffer.append("=");
		this.buffer.append(value);
		this.buffer.append(" ");
	}

	/**
	 * ����UFDouble����ֵ����in����
	 * 
	 * @param name sql�ֶ���
	 * @param values UFDouble����ֵ
	 */
	public void append(String name, UFDouble[] values) {
		this.buffer.append(name);
		this.buffer.append(" in (");
		int length = values.length;
		for (int i = 0; i < length; i++) {
			this.buffer.append(values[i]);
			this.buffer.append(",");
		}
		length = this.buffer.length();
		this.buffer.deleteCharAt(length - 1);
		this.buffer.append(") ");
	}

	/**
	 * ����case when sql���
	 * 
	 * @param condition �������
	 * @param trueExpression Ϊ��ʱ�����
	 * @param falseExpression Ϊ��ʱ�����
	 */
	public void appendCaseWhen(String condition, String trueExpression, String falseExpression) {
		this.buffer.append(" case when ");
		this.buffer.append(condition);
		this.buffer.append(" then ");
		this.buffer.append(trueExpression);
		this.buffer.append(" else ");
		this.buffer.append(falseExpression);
		this.buffer.append(" end ");
	}

	/**
	 * ����ID�ֶΣ����磺varchar(20)��varchar(36)��varchar(101)����is not null���ʽ��дΪ=~
	 * 
	 * @param name �ֶ�����
	 */
	public void appendIDIsNotNull(String name) {
		this.buffer.append(name);
		this.buffer.append("<>'~' ");
	}

	/**
	 * ����ID�ֶΣ����磺varchar(20)��varchar(36)��varchar(101)����is null���ʽ��дΪ=~
	 * 
	 * @param name �ֶ�����
	 */
	public void appendIDIsNull(String name) {
		this.buffer.append(name);
		this.buffer.append("='~' ");
	}

	/**
	 * ����int����ֵ����not in����
	 * 
	 * @param name sql�ֶ���
	 * @param values int����ֵ
	 */
	public void appendNot(String name, int[] values) {
		this.buffer.append(name);
		this.buffer.append(" not in (");
		int length = values.length;
		for (int i = 0; i < length; i++) {
			this.buffer.append(values[i]);
			this.buffer.append(",");
		}
		length = this.buffer.length();
		this.buffer.deleteCharAt(length - 1);
		this.buffer.append(") ");
	}

	/**
	 * ����Integer����ֵ����not in����
	 * 
	 * @param name sql�ֶ���
	 * @param values Integer����ֵ
	 */
	public void appendNot(String name, Integer[] values) {
		this.buffer.append(name);
		this.buffer.append(" not in (");
		int length = values.length;
		for (int i = 0; i < length; i++) {
			this.buffer.append(values[i]);
			this.buffer.append(",");
		}
		length = this.buffer.length();
		this.buffer.deleteCharAt(length - 1);
		this.buffer.append(") ");
	}

	/**
	 * ����ö������ֵ����not in����
	 * 
	 * @param name sql�ֶ���
	 * @param flags ö������ֵ
	 */
	public void appendNot(String name, MDEnum[] flags) {
		this.buffer.append(name);
		this.buffer.append(" not in (");
		int length = flags.length;
		for (int i = 0; i < length; i++) {
			this.append(flags[i]);
			this.buffer.append(",");
		}
		length = this.buffer.length();
		this.buffer.deleteCharAt(length - 1);
		this.buffer.append(") ");
	}

	/**
	 * ����String����ֵ����not in����
	 * 
	 * @param name sql�ֶ���
	 * @param values String����ֵ
	 */
	public void appendNot(String name, String[] values) {
		this.buffer.append(name);
		this.buffer.append(" not in (");
		int length = values.length;
		for (int i = 0; i < length; i++) {
			this.buffer.append("'");
			this.buffer.append(values[i]);
			this.buffer.append("'");
			this.buffer.append(",");
		}
		length = this.buffer.length();
		this.buffer.deleteCharAt(length - 1);
		this.buffer.append(") ");
	}

	/**
	 * ����UFDouble����ֵ����not in����
	 * 
	 * @param name sql�ֶ���
	 * @param values UFDouble[]����ֵ
	 */
	public void appendNot(String name, UFDouble[] values) {
		this.buffer.append(name);
		this.buffer.append(" not in (");
		int length = values.length;
		for (int i = 0; i < length; i++) {
			this.buffer.append(values[i]);
			this.buffer.append(",");
		}
		length = this.buffer.length();
		this.buffer.deleteCharAt(length - 1);
		this.buffer.append(") ");
	}

	/**
	 * �����������͵��ֶ���Ҫ�ж� is null��ʱ�򣬵��ô˷���
	 * 
	 * @param name �ֶ�����
	 */
	public void appendNumberIsNull(String name) {
		this.buffer.append(name);
		// ��ʱ��˴���
		this.buffer.append(" is null ");
	}

	/**
	 * ƴд��ֵȡ���ȵ�sql
	 * 
	 * @param expression ��ֵ���ʽ
	 * @param precision ����
	 */
	public void appendRound(String expression, int precision) {
		this.buffer.append(" round ( ");
		this.buffer.append(expression);
		this.buffer.append(",");
		this.buffer.append(precision);
		this.buffer.append(" ) ");
	}

	/**
	 * ƴд��ֵȡ���ȵ�sql
	 * 
	 * @param expression ��ֵ���ʽ
	 * @param precision ����
	 */
	public void appendRound(String expression, Integer precision) {
		this.buffer.append(" round ( ");
		this.buffer.append(expression);
		this.buffer.append(",");
		this.buffer.append(precision);
		this.buffer.append(" ) ");
	}

	/**
	 * ɾ�����һ���ַ�
	 */
	public void deleteLastChar() {
		this.buffer.deleteCharAt(this.buffer.length() - 1);
	}

	/**
	 * ƴд����
	 */
	public void endParentheses() {
		this.buffer.append(" ) ");
	}

	/**
	 * ��������sql����ǰ��ƴд��sql���
	 */
	public void reset() {
		this.buffer.setLength(0);
	}

	/**
	 * ƴд(��
	 */
	public void startParentheses() {
		this.buffer.append(" ( ");
	}

	@Override
	public String toString() {
		return this.buffer.toString();
	}
}
