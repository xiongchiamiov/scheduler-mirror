package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.HTMLUtilities;
import edu.calpoly.csc.scheduler.view.web.client.table.ResizeableHeader.ResizeCallback;

public class OsmTable<ObjectType extends Comparable<ObjectType>> extends FocusPanel {
	public interface SaveHandler<ObjectType extends Comparable<ObjectType>> {
		void saveButtonClicked();
	}
	
	public static abstract class Column<ObjectType extends Comparable<ObjectType>> {
		private OsmTable<ObjectType> table;
		final public String name;
		final public String width;
		public final Comparator<ObjectType> sortComparator;
		public Widget headerContents;
		public Element headerTDElement;

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
		
		Column(String name, String width, Comparator<ObjectType> sortComparator) {
			this.name = name;
			this.width = width;
			this.sortComparator = sortComparator;
		}
	}
	
	protected class Row {
		public final ObjectType object;
		public final Element trElement;
		public final Widget[] widgetsInCells;
		public Row(ObjectType object, Element trElement, Widget[] widgetsInCells) {
			this.object = object;
			this.trElement = trElement;
			this.widgetsInCells = widgetsInCells;
		}
	}
	
	private class CssClassedSet implements Iterable<Row> {
		final String cssClassName;
		HashSet<Row> set = new HashSet<Row>();
		
		CssClassedSet(String cssClassName) {
			this.cssClassName = cssClassName;
		}
		
		public void add(Row row) {
			addClass(row);
			set.add(row);
		}
		
		public void remove(Row row) {
			removeClass(row);
			set.remove(row);
		}
		
		public void clear() {
			for (Row row : set)
				removeClass(row);
			set.clear();
		}
		
		public void refresh() {
			for (Row row : set)
				addClass(row);
		}
		
		public boolean contains(Row row) { return set.contains(row); }
		public Iterator<Row> iterator() { return set.iterator(); }

		private void addClass(Row row) { row.trElement.addClassName(cssClassName); }
		private void removeClass(Row row) { row.trElement.removeClassName(cssClassName); }
	}
	
	protected Factory<ObjectType> factory;
	protected FlexTable table;
	protected ArrayList<Column<ObjectType>> columns = new ArrayList<Column<ObjectType>>();
	protected LinkedHashMap<ObjectType, Row> rows = new LinkedHashMap<ObjectType, Row>();
	protected CssClassedSet rowsToRemove = new CssClassedSet("removed");
	protected CssClassedSet editedRows = new CssClassedSet("edited");
	protected CssClassedSet addedRows = new CssClassedSet("added");
	protected LinkedHashMap<ObjectType, ObjectType> historyByObject = new LinkedHashMap<ObjectType, ObjectType>();
	
	public OsmTable(Factory<ObjectType> factory, final SaveHandler<ObjectType> saveHandler) {
		VerticalPanel vp = new VerticalPanel();
		add(vp);
		
		this.factory = factory;
		
		FlowPanel controlBar = new FlowPanel();
		controlBar.addStyleName("controlBar");
		
		controlBar.add(new Button("Save All", new ClickHandler() {
			public void onClick(ClickEvent event) {
				saveHandler.saveButtonClicked();
			}
		}));

		controlBar.add(new HTML(
				"<div class=\"addedLegend\"><div>Added</div></div>" +
				"<div class=\"editedLegend\"><div>Modified</div></div>" +
				"<div class=\"removedLegend\"><div>Removed</div></div>"));
		
		vp.add(controlBar);
		
		table = new FlexTable();
		table.addStyleName("osmtable");
		table.setCellPadding(0);
		table.setCellSpacing(0);
		vp.add(table);
		
		FocusPanel newObjectPanel = new FocusPanel();
		newObjectPanel.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) {
				addNewRow();
			}
		});
		vp.add(newObjectPanel);
		
		vp.add(new Button("New", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				addNewRow();
			}
		}));
	}
	
	public void addDeleteColumn() {
		addColumn(new ButtonColumn<ObjectType>("Delete", "4em",
				new ButtonColumn.ClickCallback<ObjectType>() {
					public void buttonClickedForObject(ObjectType object,
							Button button) {
						Row row = rows.get(object);
						if (addedRows.contains(row))
							deleteRow(row);
						toggleRowRemoved(row);
					}

					@Override
					public String initialLabel(ObjectType object) {
						return "X";
					}
				}));
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
	
	public void clear() {
		for (int i = 0; i < rows.size(); i++)
			table.removeRow(1);
		
		rowsToRemove.clear();
		editedRows.clear();
		addedRows.clear();
		historyByObject.clear();
		rows.clear();
	}
	
	private void toggleRowRemoved(Row row) {
		if (rowsToRemove.contains(row))
			rowsToRemove.remove(row);
		else
			rowsToRemove.add(row);
	}

	public final void addNewRow() {
		Row newRow = addRow(factory.create());
		addedRows.add(newRow);

		for (Widget widget : newRow.widgetsInCells) {
			if (widget instanceof Focusable) {
				((Focusable)widget).setFocus(true);
				break;
			}
		}
	}
	
	void setColumnWidth(Column<ObjectType> column, int widthPixels) {
		if (widthPixels < 0)
			return;
		
		column.headerTDElement.setAttribute("style", "");
		
		column.headerContents.setWidth(widthPixels + "px");
		
		int columnIndex = columns.indexOf(column);
		
		for (Row row : rows.values())
			row.widgetsInCells[columnIndex].setWidth(widthPixels + "px");
	}
	
	public void addColumn(final Column<ObjectType> column) {
		assert(rows.size() == 0);
		
		column.attachedToTable(this);

		int newColumnIndex = columns.size();
		
		column.headerContents = new HTML(column.name);
		column.headerContents.addStyleName("headerContents");
		
		FocusPanel contents = new ResizeableHeader(this, column.headerContents, new ResizeCallback() {
			public int getWidth() { return column.headerContents.getOffsetWidth(); }
			public void setWidth(int newWidthPixels) { setColumnWidth(column, newWidthPixels); }
		});
		contents.addStyleName("header");
		table.setWidget(0, newColumnIndex, contents);
		
		column.headerTDElement = HTMLUtilities.getClosestContainingElementOfType(contents.getElement(), "td");
		if (column.width != null)
			column.headerTDElement.setAttribute("style", "width: " + column.width);
		
		contents.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				sortByColumn(column);
			}
		});
		
		columns.add(column);
	}
	
	void sortByColumn(Column<ObjectType> column) {
		ArrayList<ObjectType> sortedList = new ArrayList<ObjectType>(this.rows.keySet());
		Collections.sort(sortedList, column.sortComparator);

		for (Row row : this.rows.values())
			row.trElement.removeFromParent();

		Element tableElement = table.getElement();
		assert(tableElement.getNodeName().equalsIgnoreCase("table"));
		Element tbodyElement = tableElement.getElementsByTagName("tbody").getItem(0);
		assert(tbodyElement.getNodeName().equalsIgnoreCase("tbody"));
		assert(tbodyElement.getChildCount() == 1);
		
		for (int i = 0; i < sortedList.size(); i++) {
			ObjectType object = sortedList.get(i);
			Row row = rows.get(object);
			tbodyElement.appendChild(row.trElement);
		}
	}

	public final Row addRow(ObjectType object) {
		int newObjectIndex = rows.size();
		int rowIndex = newObjectIndex + 1;

		Widget[] widgets = new Widget[columns.size()];
		for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
			Column<ObjectType> column = columns.get(colIndex);
			Widget widget = column.createCellWidget(object);
			widgets[colIndex] = widget;
			table.setWidget(rowIndex, colIndex, widget);

			Element td = HTMLUtilities.getClosestContainingElementOfType(widget.getElement(), "td");
			if (column.width != null)
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
	
	public final void addRows(Collection<ObjectType> objects) {
		for (ObjectType object : objects)
			addRow(object);
	}

	protected void objectChanged(ObjectType object) {
		Row row = rows.get(object);
		assert(row != null);
		
		ObjectType history = historyByObject.get(object);
		assert(history != null);
		
		if (object.compareTo(history) == 0)
			editedRows.remove(row);
		else
			editedRows.add(row);
	}
	
	private void deleteRow(Row row) {
		addedRows.remove(row);
		editedRows.remove(row);
		rowsToRemove.remove(row);
		
		row.trElement.removeFromParent();
	}
}
