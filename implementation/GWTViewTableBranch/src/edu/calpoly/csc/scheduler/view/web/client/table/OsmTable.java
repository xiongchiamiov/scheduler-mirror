package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.HTMLUtilities;

public class OsmTable<ObjectType> extends VerticalPanel {
	public static abstract class Column<ObjectType> {
		private OsmTable<ObjectType> table;
		final public String name;
		final public String width;

		public void attachedToTable(OsmTable<ObjectType> table) {
			assert(this.table == null);
			this.table = table;
		}

		// Subclass must call this when the value is changed.
		protected void objectChanged(ObjectType object) {
			assert(table != null);
			table.objectChanged(object);
		}
		
		public abstract Widget createCellWidget(ObjectType object);
		
		Column(String name, String width) {
			this.name = name;
			this.width = width;
		}
	}
	
	public static abstract class ValueColumn<ObjectType, ValType> extends Column<ObjectType> {
		protected StaticGetter<ObjectType, ValType> getter;
		protected StaticSetter<ObjectType, ValType> setter;
		protected Comparator<ValType> sorter;
		
		public ValueColumn(String name, String width, StaticGetter<ObjectType, ValType> getter, StaticSetter<ObjectType, ValType> setter, Comparator<ValType> sorter) {
			super(name, width);
			this.sorter = sorter;
			this.getter = getter;
			this.setter = setter;
		}
	}
	
	protected class Row {
		public Element row;
		public Widget[] widgets;
	}
	
	protected Factory<ObjectType> factory;
	private FlexTable table;
	private ArrayList<Column<ObjectType>> columns = new ArrayList<Column<ObjectType>>();
	protected LinkedHashMap<Object, Row> rows = new LinkedHashMap<Object, Row>();
	
	public OsmTable(Factory<ObjectType> factory) {
		table = new FlexTable();
		table.addStyleName("osmtable");
		table.setCellPadding(0);
		table.setCellSpacing(0);
		add(table);
		
		FocusPanel newObjectPanel = new FocusPanel();
		newObjectPanel.addStyleName("newObjectPanel");
		newObjectPanel.add(new HTML("New"));
		newObjectPanel.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) {
				addNewRow();
			}
		});
		newObjectPanel.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addNewRow();
			}
		});
		add(newObjectPanel);
		
		this.factory = factory;
	}
	
	public OsmTable(Factory<ObjectType> factory, Column<ObjectType> newColumns[]) {
		this(factory);
		for (Column<ObjectType> column : newColumns)
			addColumn(column);
	}

	// Subclass may override this if he wants to know when objects have been changed.
	protected void objectChanged(ObjectType object) { }
	
	public final void addNewRow() {
		Row newRow = addRowAndDontColor(factory.create());
		colorRows();
		
		for (Widget widget : newRow.widgets) {
			if (widget instanceof FocusPanel) {
				((FocusPanel)widget).setFocus(true);
				break;
			}
		}
	}
	
	public void addColumn(Column<ObjectType> column) {
		assert(rows.size() == 0);
		
		column.attachedToTable(this);
		
		int newColumnIndex = columns.size();
		Widget contents = new HTML(column.name);
		contents.addStyleName("header");
		table.setWidget(0, newColumnIndex, contents);
		
		Element td = HTMLUtilities.getClosestContainingElementOfType(contents.getElement(), "td");
		td.setAttribute("style", td.getAttribute("style") + "; width: " + column.width);
		
		columns.add(column);
	}
	
	protected Row addRowAndDontColor(ObjectType object) {
		int newObjectIndex = rows.size();
		int rowIndex = newObjectIndex + 1;

		Row newRow = new Row();
		newRow.widgets = new Widget[columns.size()];
		for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
			Column<ObjectType> column = columns.get(colIndex);
			Widget widget = column.createCellWidget(object);
			newRow.widgets[colIndex] = widget;
			table.setWidget(rowIndex, colIndex, widget);

			Element td = HTMLUtilities.getClosestContainingElementOfType(widget.getElement(), "td");
			td.setAttribute("style", td.getAttribute("style") + "; width: " + column.width);
		}
		
		// Get containing tr
		newRow.row = newRow.widgets[0].getElement();
		while (newRow.row.getNodeName().compareToIgnoreCase("tr") != 0)
			newRow.row = newRow.row.getParentElement();
		
		rows.put(object, newRow);
		return newRow;
	}
	
	public final void addRow(ObjectType object) {
		addRowAndDontColor(object);
		colorRows();
	}
	
	public final void addRows(Iterable<ObjectType> objects) {
		for (ObjectType object : objects)
			addRowAndDontColor(object);
		colorRows();
	}
	
	protected void colorRows() {
		Element tableElement = table.getElement();
		
		NodeList<Element> elements = tableElement.getElementsByTagName("tr");
		for (int index = 1; index < elements.getLength(); index++) {
			if (index % 2 == 0)
				elements.getItem(index).addClassName("evenRow");
			else
				elements.getItem(index).removeClassName("evenRow");
		}
	}
}
