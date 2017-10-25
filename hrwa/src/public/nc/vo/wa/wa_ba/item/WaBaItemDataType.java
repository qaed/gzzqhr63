package nc.vo.wa.wa_ba.item;

public enum WaBaItemDataType {
	FORMULA("公式", 0), HRWA("薪资规则表", 1), INPUT("手工输入 ", 2), FIXEDVALUE("固定值", 3), OTHERSOURCE("其他来源", 5);
	private String name;
	private int value;

	private WaBaItemDataType(String _name, int _value) {
		this.name = _name;
		this.value = _value;
	}

	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name 要设置的 name
	 */
	@SuppressWarnings("unused")
	private void setName(String name) {
		this.name = name;
	}

	/**
	 * @return value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @param value 要设置的 value
	 */
	public void setValue(int value) {
		this.value = value;
	}

}
