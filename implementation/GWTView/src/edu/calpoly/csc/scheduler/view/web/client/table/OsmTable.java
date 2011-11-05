package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class OsmTable<ObjectType> extends VerticalPanel {
	public interface IColumn<ObjectType> {
		String getName();
		Widget createCellWidget(ObjectType object);
		int compare(ObjectType a, ObjectType b);
	}
	
	public interface Column<ObjectType, ValType> extends IColumn<ObjectType> {
		ValType getValue(ObjectType object);
		void setValue(ObjectType object, ValType newValue);
	}
	
	FlexTable table;
	IColumn<ObjectType> columns[];
	ArrayList<ObjectType> objects;
	
	public OsmTable(IColumn<ObjectType> columns[]) {
		table = new FlexTable();
		this.columns = columns;
		
		for (int colIndex = 0; colIndex < columns.length; colIndex++) {
			table.setWidget(0, colIndex, new HTML(columns[colIndex].getName()));
		}
		
		add(table);
		
		objects = new ArrayList<ObjectType>();
	}
	
	public void addRow(ObjectType object) {
		int newObjectIndex = objects.size();
		int newRowIndex = newObjectIndex + 1;
		objects.add(object);
		for (int colIndex = 0; colIndex < columns.length; colIndex++) {
			table.setWidget(newRowIndex, colIndex, columns[colIndex].createCellWidget(object));
		}
	}
	
	public void addRows(Iterable<ObjectType> objects) {
		for (ObjectType object : objects)
			addRow(object);
	}
}
