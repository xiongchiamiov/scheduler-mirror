package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.ArrayList;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable;
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
	
	Element table;
	Element tableBody;
	Element headerRow;
	IColumn<ObjectType> columns[];
	ArrayList<ObjectType> objects;
	
	public OsmTable(IColumn<ObjectType> columns[]) {
		table = DOM.createTable();
		table.appendChild(tableBody = DOM.createTBody());
		
		this.columns = columns;
		
		headerRow = DOM.createTR();
		for (int colIndex = 0; colIndex < columns.length; colIndex++) {
			Element cell = DOM.createTH();
			cell.setInnerHTML(columns[colIndex].getName());
			headerRow.appendChild(cell);
		}
		tableBody.appendChild(headerRow);
		add(HTML.wrap(table));
		
		objects = new ArrayList<ObjectType>();
	}
	
	public void addRow(ObjectType object) {
		objects.add(object);

		Element newRow = DOM.createTR();
		
		for (int colIndex = 0; colIndex < columns.length; colIndex++) {
			Element cell = DOM.createTD();
			cell.appendChild(columns[colIndex].createCellWidget(object).getElement());
			newRow.appendChild(cell);
		}
		
		tableBody.appendChild(newRow);
	}
	
	public void addRows(Iterable<ObjectType> objects) {
		for (ObjectType object : objects)
			addRow(object);
	}
}
