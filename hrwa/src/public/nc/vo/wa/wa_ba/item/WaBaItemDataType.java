package nc.vo.wa.wa_ba.item;

public enum WaBaItemDataType {
	FORMULA("��ʽ", 0), HRWA("н�ʹ����", 1), INPUT("�ֹ����� ", 2), FIXEDVALUE("�̶�ֵ", 3), OTHERSOURCE("������Դ", 5);
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
	 * @param name Ҫ���õ� name
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
	 * @param value Ҫ���õ� value
	 */
	public void setValue(int value) {
		this.value = value;
	}

}
