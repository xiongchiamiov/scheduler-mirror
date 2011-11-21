package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import edu.calpoly.csc.scheduler.view.web.shared.Identified;

public class OsmTable<ObjectType extends Identified> extends FocusPanel {
	public interface SaveHandler<ObjectType> {
		void saveButtonClicked();
	}
	
	public static abstract class Column<ObjectType extends Identified> {
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
		
		public boolean contains(Row row) { return set.contains(row); }
		public Iterator<Row> iterator() { return set.iterator(); }

		private void addClass(Row row) { row.trElement.addClassName(cssClassName); }
		private void removeClass(Row row) { row.trElement.removeClassName(cssClassName); }
		public boolean isEmpty(){
			return set.isEmpty();
		}
	}
	
	protected Factory<ObjectType> factory;
	protected FlexTable table;
	protected ArrayList<Column<ObjectType>> columns = new ArrayList<Column<ObjectType>>();
	protected Map<Integer, Row> rowsByObjectID = new HashMap<Integer, Row>();
	protected CssClassedSet rowsToRemove = new CssClassedSet("removed");
	protected CssClassedSet editedRows = new CssClassedSet("edited");
	protected CssClassedSet addedRows = new CssClassedSet("added");
	protected Map<Integer, ObjectType> objectsByID = new HashMap<Integer, ObjectType>();
	protected Map<Integer, ObjectType> historyByObjectID = new HashMap<Integer, ObjectType>();
	
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
					public void buttonClickedForObject(ObjectType object, Button button) {
						Row row = rowsByObjectID.get(object.getID());
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

	public List<ObjectType> getAddedObjects() {
		List<ObjectType> added = new ArrayList<ObjectType>();
		for (Row row : addedRows)
			added.add(row.object);
		return added;
	}
	
	public List<ObjectType> getEditedObjects() {
		List<ObjectType> edited = new ArrayList<ObjectType>();
		for (Row row : editedRows)
			edited.add(row.object);
		return edited;
	}
	
	public List<ObjectType> getRemovedObjects() {
		List<ObjectType> removed = new ArrayList<ObjectType>();
		for (Row row : rowsToRemove)
			removed.add(row.object);
		return removed;
	}
	
	public void clear() {
		for (int i = 0; i < rowsByObjectID.size(); i++)
			table.removeRow(1);
		
		rowsToRemove.clear();
		editedRows.clear();
		addedRows.clear();
		historyByObjectID.clear();
		rowsByObjectID.clear();
		objectsByID.clear();
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
		
		for (Row row : rowsByObjectID.values())
			row.widgetsInCells[columnIndex].setWidth(widthPixels + "px");
	}
	
	public void addColumn(final Column<ObjectType> column) {
		assert(rowsByObjectID.size() == 0);
		
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
		ArrayList<ObjectType> sortedList = new ArrayList<ObjectType>(this.objectsByID.values());
		Collections.sort(sortedList, column.sortComparator);

		for (Row row : this.rowsByObjectID.values())
			row.trElement.removeFromParent();

		Element tableElement = table.getElement();
		assert(tableElement.getNodeName().equalsIgnoreCase("table"));
		Element tbodyElement = tableElement.getElementsByTagName("tbody").getItem(0);
		assert(tbodyElement.getNodeName().equalsIgnoreCase("tbody"));
		assert(tbodyElement.getChildCount() == 1);
		
		for (int i = 0; i < sortedList.size(); i++) {
			ObjectType object = sortedList.get(i);
			Row row = rowsByObjectID.get(object.getID());
			tbodyElement.appendChild(row.trElement);
		}
	}

	public Collection<ObjectType> getAddedUntouchedAndEditedObjects() {
		ArrayList<ObjectType> result = new ArrayList<ObjectType>();
		for (Integer id : rowsByObjectID.keySet())
			result.add(objectsByID.get(id));
		return result;
	}
	
	public final Row addRow(ObjectType object) {
		int newObjectIndex = rowsByObjectID.size();
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
		
		Element rowElement = HTMLUtilities.getClosestContainingElementOfType(widgets[0].getElement(), "tr");

		Row newRow = new Row(object, rowElement, widgets);
		assert(!rowsByObjectID.containsKey(object.getID()));
		rowsByObjectID.put(object.getID(), newRow);

		historyByObjectID.put(object.getID(), factory.createCopy(object));
		assert(object.equals(historyByObjectID.get(object.getID())));
		
		return newRow;
	}
	
	public final void addRows(Collection<ObjectType> objects) {
		for (ObjectType object : objects)
			addRow(object);
	}

	protected void objectChanged(ObjectType object) {
		Row row = rowsByObjectID.get(object.getID());
		assert(row != null);
		
		ObjectType history = historyByObjectID.get(object.getID());
		assert(history != null);
		
		if (object.equals(history))
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
	
	
	/**
	 * 
	 * @return true if the table has been saved, false if there are unsaved changes in the table
	 */
	public boolean isSaved(){
		
		if(!rowsToRemove.isEmpty()){ return false; }
		if(!editedRows.isEmpty()){ return false; }
		if(!addedRows.isEmpty()){ return false; }
			
		return true;
	}
}
