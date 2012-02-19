package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;

public class DocumentGWT implements Serializable {
	private static final long serialVersionUID = 1L;
	
	int id;
	String name;
	
	public DocumentGWT() { }
	
	public DocumentGWT(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public DocumentGWT(DocumentGWT that) {
		this(that.id, that.name);
	}

	public Integer getID() { return id; }
	public void setID(int id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
}
