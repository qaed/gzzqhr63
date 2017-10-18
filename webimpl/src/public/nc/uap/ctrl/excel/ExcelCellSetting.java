package nc.uap.ctrl.excel;

public class ExcelCellSetting {
	private short color;
	private String field;
	
	public ExcelCellSetting(String field,short color){
		this.color=color;
		this.field=field;
	}
	
	public short getColor() {
		return color;
	}
	public void setColor(short color) {
		this.color = color;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	
	
}
