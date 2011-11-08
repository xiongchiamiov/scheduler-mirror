package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.HTMLUtilities;

public class OsmTable<ObjectType extends Comparable<ObjectType>> extends VerticalPanel {
	public interface SaveHandler<ObjectType extends Comparable<ObjectType>> {
		void saveButtonClicked();
	}
	
	public static abstract class Column<ObjectType extends Comparable<ObjectType>> {
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
	
	public static abstract class ValueColumn<ObjectType extends Comparable<ObjectType>, ValType> extends Column<ObjectType> {
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
	
	public Collection<ObjectType> getAddedObjects() {
		Collection<ObjectType> added = new ArrayList<ObjectType>();
		for (Row row : addedRows)
			added.add(row.object);
		return added;
	}
	
	public Collection<ObjectType> getEditedObjects() {
		Collection<ObjectType> edited = new ArrayList<ObjectType>();
		for (Row row : editedRows)
			edited.add(row.object);
		return edited;
	}
	
	public Collection<ObjectType> getRemovedObjects() {
		Collection<ObjectType> removed = new ArrayList<ObjectType>();
		for (Row row : rowsToRemove)
			removed.add(row.object);
		return removed;
	}
	
	public Collection<ObjectType> getAddedUntouchedAndEditedObjects() {
		return new ArrayList<ObjectType>(rows.keySet());
	}
	
	protected class Row {
		public final ObjectType object;
		public final Element row;
		public final Widget[] widgets;
		public Row(ObjectType object, Element row, Widget[] widgets) {
			this.object = object;
			this.row = row;
			this.widgets = widgets;
		}
	}
	
	protected Factory<ObjectType> factory;
	private FlexTable table;
	private ArrayList<Column<ObjectType>> columns = new ArrayList<Column<ObjectType>>();
	protected LinkedHashMap<ObjectType, Row> rows = new LinkedHashMap<ObjectType, Row>();
	Collection<Row> rowsToRemove = new HashSet<Row>();
	Collection<Row> editedRows = new HashSet<Row>();
	Collection<Row> addedRows = new HashSet<Row>();
	private LinkedHashMap<ObjectType, ObjectType> historyByObject = new LinkedHashMap<ObjectType, ObjectType>();

	public void clear() {
		ArrayList<Column<ObjectType>> savedColumns = this.columns;
		
		columns.clear();
		rows.clear();
		rowsToRemove.clear();
		editedRows.clear();
		addedRows.clear();
		historyByObject.clear();
		table.clear();
		
		for (Column<ObjectType> column : savedColumns)
			addColumn(column);
	}
	
	void toggleRowRemoved(Row row) {
		if (rowsToRemove.contains(row))
			rowsToRemove.remove(row);
		else
			rowsToRemove.add(row);
		
		colorRows();
	}
	
	public OsmTable(Factory<ObjectType> factory, final SaveHandler<ObjectType> saveHandler) {
		this.factory = factory;
		
		FlowPanel controlBar = new FlowPanel();
		controlBar.addStyleName("controlBar");
		
		controlBar.add(new Button("Save All", new ClickHandler() {
			public void onClick(ClickEvent event) {
				saveHandler.saveButtonClicked();
			}
		}));

		controlBar.add(new HTML(
				"<div class=\"addedLegend\"><div>To Be Added</div></div>" +
				"<div class=\"editedLegend\"><div>To Be Modified</div></div>" +
				"<div class=\"removedLegend\"><div>To Be Removed</div></div>"));
		
		add(controlBar);
		
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

		addColumn(new ButtonColumn<ObjectType>("Delete", "4em", "X",
				new ButtonColumn.ClickCallback<ObjectType>() {
					public void buttonClickedForObject(ObjectType object) {
						Row row = rows.get(object);
						if (addedRows.contains(row))
							deleteRow(row);
						toggleRowRemoved(row);
					}
				}));
	}
	
	public OsmTable(Factory<ObjectType> factory, SaveHandler<ObjectType> saveHandler, Column<ObjectType> newColumns[]) {
		this(factory, saveHandler);
		for (Column<ObjectType> column : newColumns)
			addColumn(column);
	}

	// Subclass may override this if he wants to know when objects have been changed.
	protected void objectChanged(ObjectType object) {
		Row row = rows.get(object);
		assert(row != null);
		
		ObjectType history = historyByObject.get(object);
		assert(history != null);
		
		if (object.compareTo(history) == 0)
			editedRows.remove(row);
		else
			editedRows.add(row);
		
		colorRows();
	}
	
	private void deleteRow(Row row) {
		addedRows.remove(row);
		editedRows.remove(row);
		rowsToRemove.remove(row);
		
		row.row.removeFromParent();
	}
	
	public final void addNewRow() {
		Row newRow = addRowAndDontColor(factory.create());
		addedRows.add(newRow);
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

		Widget[] widgets = new Widget[columns.size()];
		for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
			Column<ObjectType> column = columns.get(colIndex);
			Widget widget = column.createCellWidget(object);
			widgets[colIndex] = widget;
			table.setWidget(rowIndex, colIndex, widget);

			Element td = HTMLUtilities.getClosestContainingElementOfType(widget.getElement(), "td");
			td.setAttribute("style", td.getAttribute("style") + "; width: " + column.width);
		}
		
		// Get containing tr
		Element rowElement = widgets[0].getElement();
		while (rowElement.getNodeName().compareToIgnoreCase("tr") != 0)
			rowElement = rowElement.getParentElement();

		Row newRow = new Row(object, rowElement, widgets);
		rows.put(object, newRow);

		historyByObject.put(object, factory.createHistoryFor(object));
		assert(object.compareTo(historyByObject.get(object)) == 0);
		
		return newRow;
	}
	
	public final void addRow(ObjectType object) {
		addRowAndDontColor(object);
		colorRows();
	}
	
	public final void addRows(Collection<ObjectType> objects) {
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

		for (Row row : rows.values()) {
			row.row.removeClassName("edited");
			row.row.removeClassName("added");
			row.row.removeClassName("removed");
		}

		for (Row row : addedRows)
			row.row.addClassName("added");
		
		for (Row row : editedRows)
			row.row.addClassName("edited");

		for (Row row : rowsToRemove)
			row.row.addClassName("removed");
	}

	public void commitToHistory() {
		for (ObjectType object : new LinkedList<ObjectType>(historyByObject.keySet()))
			historyByObject.put(object, factory.createHistoryFor(object));
		colorRows();
	}
}
