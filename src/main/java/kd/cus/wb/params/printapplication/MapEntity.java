package kd.cus.wb.params.printapplication;

public class MapEntity {
	private String field;
	private String oldvalue;
	private String newvalue;
	private String note;
	public MapEntity(){}
	public MapEntity(String field,String oldvalue,String newvalue){
		this.field = field;
		this.oldvalue = oldvalue;
		this.newvalue = newvalue;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getOldvalue() {
		return oldvalue;
	}
	public void setOldvalue(String oldvalue) {
		this.oldvalue = oldvalue;
	}
	public String getNewvalue() {
		return newvalue;
	}
	public void setNewvalue(String newvalue) {
		this.newvalue = newvalue;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
}
