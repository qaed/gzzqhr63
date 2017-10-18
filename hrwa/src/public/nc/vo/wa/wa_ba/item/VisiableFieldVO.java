package nc.vo.wa.wa_ba.item;

import nc.uap.lfw.core.combodata.ComboData;

public class VisiableFieldVO {
	private ComboData comboData;
	private String name;
	private String id;
	
	public VisiableFieldVO() {}

	public ComboData getComboData() {
		return comboData;
	}

	public void setComboData(ComboData comboData) {
		this.comboData = comboData;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
}
