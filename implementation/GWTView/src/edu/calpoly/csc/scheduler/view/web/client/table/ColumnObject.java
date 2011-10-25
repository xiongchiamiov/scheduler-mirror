package edu.calpoly.csc.scheduler.view.web.client.table;

import com.google.gwt.user.cellview.client.Column;

public class ColumnObject<T> {

	private Column<T, ?> column;
	private String label;
	
	public ColumnObject(Column<T, ?> column, String label) {
		super();
		this.column = column;
		this.label = label;
	}
	public Column<T, ?> getColumn() {
		return column;
	}
	public void setColumn(Column<T, ?> column) {
		this.column = column;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
}
