package nc.vo.pubapp.pattern.pub;

import nc.md.model.impl.MDEnum;
import nc.md.model.type.IType;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

/**
 * sql语句构造器。方便拼写sql，减少不比要的错误
 * 
 * @since 6.0
 * @version 2006-6-20 18:50:23
 * @author 钟鸣
 */
public class SqlBuilder {

	/**
	 * 存放拼写sql的StringBuffer
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
	 * 将一个非空的对象转化为String拼写入sql语句
	 * 
	 * @param obj 非空的对象
	 */
	public void append(Object obj) {
		this.buffer.append(obj.toString());
	}

	/**
	 * 将一个字符串拼写入sql语句
	 * 
	 * @param str 字符串
	 */
	public void append(String str) {
		this.buffer.append(str);
	}

	/**
	 * 对于整数值构造“等于”条件
	 * 
	 * @param name sql字段名
	 * @param value int值
	 */
	public void append(String name, int value) {
		this.buffer.append(name);
		this.buffer.append("=");
		this.buffer.append(value);
		this.buffer.append(" ");
	}

	/**
	 * 对于int数组值构造in条件
	 * 
	 * @param name sql字段名
	 * @param values int数组值
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
	 * 对于Integer值构造“等于”条件
	 * 
	 * @param name sql字段名
	 * @param value Integer值
	 */
	public void append(String name, Integer value) {
		this.buffer.append(name);
		this.buffer.append("=");
		this.buffer.append(value);
		this.buffer.append(" ");
	}

	/**
	 * 对于Integer数组值构造in条件
	 * 
	 * @param name sql字段名
	 * @param values Integer数组值
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
	 * 对于枚举值构造“等于”条件
	 * 
	 * @param name sql字段名
	 * @param flag 枚举
	 */
	public void append(String name, MDEnum flag) {
		this.buffer.append(name);
		this.buffer.append("=");
		this.append(flag);
		this.buffer.append(" ");
	}

	/**
	 * 对于枚举数组值构造in条件
	 * 
	 * @param name sql字段名
	 * @param flags 枚举数组
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
	 * 对于字符串值构造“等于”条件
	 * 
	 * @param name sql字段名
	 * @param value String值 不能为空，否则抛异常。因为不知道是否要添加~
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
	 * 对于整数值构造operator所指定的条件
	 * 
	 * @param name sql字段名
	 * @param operator sql操作符
	 * @param value int值
	 */
	public void append(String name, String operator, int value) {
		this.buffer.append(name);
		this.buffer.append(operator);
		this.buffer.append(value);
		this.buffer.append(" ");
	}

	/**
	 * 对于Integer值构造operator所指定的条件
	 * 
	 * @param name sql字段名
	 * @param operator sql操作符
	 * @param value Integer值
	 */
	public void append(String name, String operator, Integer value) {
		this.buffer.append(name);
		this.buffer.append(operator);
		this.buffer.append(value);
		this.buffer.append(" ");
	}

	/**
	 * 对于字符串值构造operator所指定的条件
	 * 
	 * @param name sql字段名
	 * @param operator sql操作符
	 * @param value String值
	 */
	public void append(String name, String operator, String value) {
		this.buffer.append(name);
		this.buffer.append(operator);
		this.buffer.append(" '");
		this.buffer.append(value);
		this.buffer.append("' ");
	}

	/**
	 * 对于UFDouble值构造operator所指定的条件
	 * 
	 * @param name sql字段名
	 * @param operator sql操作符
	 * @param value UFDouble值
	 */
	public void append(String name, String operator, UFDouble value) {
		this.buffer.append(name);
		this.buffer.append(operator);
		this.buffer.append(value);
		this.buffer.append(" ");
	}

	/**
	 * 对于String数组值构造in条件
	 * 
	 * @param name sql字段名
	 * @param values String数组值
	 */
	public void append(String name, String[] values) {
		int length = values.length;
		if (length == 1) {
			// 20171206 tsy 容易报错，不检验了
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
	 * 对于UFBoolean值构造“等于”条件
	 * 
	 * @param name sql字段名
	 * @param value UFBoolean值
	 */
	public void append(String name, UFBoolean value) {
		this.buffer.append(name);
		this.buffer.append("='");
		this.buffer.append(value);
		this.buffer.append("' ");
	}

	/**
	 * 对于UFDouble值构造“等于”条件
	 * 
	 * @param name sql字段名
	 * @param value UFDouble值
	 */
	public void append(String name, UFDouble value) {
		this.buffer.append(name);
		this.buffer.append("=");
		this.buffer.append(value);
		this.buffer.append(" ");
	}

	/**
	 * 对于UFDouble数组值构造in条件
	 * 
	 * @param name sql字段名
	 * @param values UFDouble数组值
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
	 * 构造case when sql语句
	 * 
	 * @param condition 条件语句
	 * @param trueExpression 为真时的语句
	 * @param falseExpression 为假时的语句
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
	 * 对于ID字段，例如：varchar(20)、varchar(36)、varchar(101)，将is not null表达式拚写为=~
	 * 
	 * @param name 字段名称
	 */
	public void appendIDIsNotNull(String name) {
		this.buffer.append(name);
		this.buffer.append("<>'~' ");
	}

	/**
	 * 对于ID字段，例如：varchar(20)、varchar(36)、varchar(101)，将is null表达式拚写为=~
	 * 
	 * @param name 字段名称
	 */
	public void appendIDIsNull(String name) {
		this.buffer.append(name);
		this.buffer.append("='~' ");
	}

	/**
	 * 对于int数组值构造not in条件
	 * 
	 * @param name sql字段名
	 * @param values int数组值
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
	 * 对于Integer数组值构造not in条件
	 * 
	 * @param name sql字段名
	 * @param values Integer数组值
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
	 * 对于枚举数组值构造not in条件
	 * 
	 * @param name sql字段名
	 * @param flags 枚举数组值
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
	 * 对于String数组值构造not in条件
	 * 
	 * @param name sql字段名
	 * @param values String数组值
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
	 * 对于UFDouble数组值构造not in条件
	 * 
	 * @param name sql字段名
	 * @param values UFDouble[]数组值
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
	 * 对于数字类型的字段需要判断 is null的时候，调用此方法
	 * 
	 * @param name 字段名称
	 */
	public void appendNumberIsNull(String name) {
		this.buffer.append(name);
		// 暂时如此处理
		this.buffer.append(" is null ");
	}

	/**
	 * 拼写数值取精度的sql
	 * 
	 * @param expression 数值表达式
	 * @param precision 精度
	 */
	public void appendRound(String expression, int precision) {
		this.buffer.append(" round ( ");
		this.buffer.append(expression);
		this.buffer.append(",");
		this.buffer.append(precision);
		this.buffer.append(" ) ");
	}

	/**
	 * 拼写数值取精度的sql
	 * 
	 * @param expression 数值表达式
	 * @param precision 精度
	 */
	public void appendRound(String expression, Integer precision) {
		this.buffer.append(" round ( ");
		this.buffer.append(expression);
		this.buffer.append(",");
		this.buffer.append(precision);
		this.buffer.append(" ) ");
	}

	/**
	 * 删除最后一个字符
	 */
	public void deleteLastChar() {
		this.buffer.deleteCharAt(this.buffer.length() - 1);
	}

	/**
	 * 拼写）号
	 */
	public void endParentheses() {
		this.buffer.append(" ) ");
	}

	/**
	 * 重新设置sql，将前面拼写的sql清空
	 */
	public void reset() {
		this.buffer.setLength(0);
	}

	/**
	 * 拼写(号
	 */
	public void startParentheses() {
		this.buffer.append(" ( ");
	}

	@Override
	public String toString() {
		return this.buffer.toString();
	}
}
