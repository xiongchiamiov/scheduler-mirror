package edu.calpoly.csc.scheduler.view.web.client.table;

public class AttributeInfo {

	public static final short INT = 1;
	
	public static final short BOOL = 2;
	
	public static final short STR = 3;
	
	private String attr;
	
	private short type;

	public AttributeInfo(String attr, short type) {
		super();
		this.attr = attr;
		this.type = type;
	}

	public String getAttr() {
		return attr;
	}

	public void setAttr(String attr) {
		this.attr = attr;
	}

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}
}
