package kd.cus.wb.params.printapplication;

public class BodySubentryEntity {
	private String xml;
	private String type;
	private String field;
	private String note;
	
	public BodySubentryEntity() {}
	
	public BodySubentryEntity(String xml, String type, String field) {
		this.xml = xml;
		this.type = type;
		this.field = field;
	}
	
	public String getXml() {
		return xml;
	}
	public void setXml(String xml) {
		this.xml = xml;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
}
