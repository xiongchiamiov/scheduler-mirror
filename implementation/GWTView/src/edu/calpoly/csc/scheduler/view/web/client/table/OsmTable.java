package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ScrollEvent;
import com.google.gwt.user.client.Window.ScrollHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.HTMLUtilities;
import edu.calpoly.csc.scheduler.view.web.client.table.ResizeableHeader.ResizeCallback;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.ButtonColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.EditSaveColumn;
import edu.calpoly.csc.scheduler.view.web.shared.Identified;

public class OsmTable<ObjectType extends Identified> extends FocusPanel {
	public interface ModifyHandler<ObjectType> {
		void objectsModified(List<ObjectType> added, List<ObjectType> edited, List<ObjectType> removed, AsyncCallback<Void> callback);
	}
	
	public interface Cell {
		Widget getCellWidget();
	}

	public interface ReadingCell extends Cell {
		Widget getCellWidget();
		void enterReadingMode();
	}
	
	public interface EditingCell extends ReadingCell {
		void enterEditingMode();
		void focus();
	}

	public static class SimpleCell implements OsmTable.Cell {
		Widget widget;
		public SimpleCell(Widget widget) { this.widget = widget; }
		public Widget getCellWidget() { return widget; }
	}
	
	private class Column {
		final public String width;
		public Widget headerContents;
		public Element headerTDElement;
		IColumn<ObjectType> userColumn;
		public Comparator<? super ObjectType> comparator;
		
		public Column(String width, Widget headerContents,
				Element headerTDElement, Comparator<? super ObjectType> comparator, IColumn<ObjectType> userColumn) {
			this.width = width;
			this.headerContents = headerContents;
			this.headerTDElement = headerTDElement;
			this.userColumn = userColumn;
		}
	}
	
	public interface IColumn<ObjectType extends Identified> {
		public abstract Cell createCell(IRowForColumn<ObjectType> object);
	}
	
	public interface IReadingColumn<ObjectType extends Identified> extends IColumn<ObjectType> {
		abstract public void updateFromObject(IRowForColumn<ObjectType> row, ReadingCell cell);
	}
	
	public interface IEditingColumn<ObjectType extends Identified> extends IReadingColumn<ObjectType> {
		abstract public void commitToObject(IRowForColumn<ObjectType> row, EditingCell cell);
	}
	
	public interface IRowForCell {
		void enterEditingMode(EditingCell focusedCell);
		void enterReadingMode();
//		void objectChanged(ObjectType object);
	}
	
	public interface IRowForColumn<ObjectType> extends IRowForCell {
		ObjectType getObject();
	}
	
	protected class Row implements IRowForColumn<ObjectType> {
		public final ObjectType object;
		public final Element trElement;
		public final Cell[] cells;
		public boolean adding; // Whether or not this row was just added and has not yet been given to the handler
		public boolean inEditingMode;
		public Row(ObjectType object, Element trElement, Cell[] cells) {
			this.object = object;
			this.trElement = trElement;
			this.cells = cells;
		}
		
		@Override
		public ObjectType getObject() { return object; }
		
		@Override
		public void enterEditingMode(EditingCell focusedCell) {
			OsmTable.this.enterRowEditingMode(this, focusedCell);
		}
		
		@Override
		public void enterReadingMode() {
			OsmTable.this.exitRowEditingMode(this);
		}
	}
	
	protected final IFactory<ObjectType> factory;
	protected final FlexTable table;
	protected final ArrayList<Column> columns = new ArrayList<Column>();
	protected final Map<Integer, Row> rowsByObjectID = new HashMap<Integer, Row>();
	protected final ModifyHandler<ObjectType> saveHandler;
	protected final Element headerTRElement;
	protected final Element fakeHeaderTRElement;
	protected boolean headerFloating;
	
	public OsmTable(IFactory<ObjectType> factory, final ModifyHandler<ObjectType> saveHandler) {
		this.saveHandler = saveHandler;
		
		VerticalPanel vp = new VerticalPanel();
		add(vp);
		
		this.factory = factory;
		
		FlowPanel controlBar = new FlowPanel();
		controlBar.addStyleName("controlBar");

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
			public void onClick(ClickEvent event) {
				addNewRow();
			}
		}));

		HTML placeholder = new HTML("Placeholder");
		table.setWidget(0, 0, placeholder);
		headerTRElement = HTMLUtilities.getClosestContainingElementOfType(placeholder.getElement(), "tr");
		headerTRElement.addClassName("headerRow");
		
		placeholder = new HTML("Placeholder");
		table.setWidget(1, 0, placeholder);
		fakeHeaderTRElement = HTMLUtilities.getClosestContainingElementOfType(placeholder.getElement(), "tr");
		fakeHeaderTRElement.addClassName("fakeHeaderRow");
		fakeHeaderTRElement.addClassName("hidden");
		
		headerFloating = false;
		
		Window.addWindowScrollHandler(new ScrollHandler() {
			public void onWindowScroll(ScrollEvent event) {
				if (!headerFloating) {
					if (event.getScrollTop() > table.getAbsoluteTop()) {
						headerFloating = true;
						headerTRElement.addClassName("floating");
						fakeHeaderTRElement.removeClassName("hidden");
					}
				}
				else {
					if (event.getScrollTop() < table.getAbsoluteTop()) {
						headerFloating = false;
						headerTRElement.removeClassName("floating");
						fakeHeaderTRElement.addClassName("hidden");
					}
				}
			}
		});
	}
	
	public void addDeleteColumn() {
		addColumn("Delete", "4em", null, new ButtonColumn<ObjectType>("X",
				new ButtonColumn.ClickCallback<ObjectType>() {
					public void buttonClickedForObject(ObjectType object, Button button) {
						final Row row = rowsByObjectID.get(object.getID());
						row.trElement.addClassName("sending");
						
						if (!Window.confirm("Are you sure you want to delete " + object.toString() + "?")) {
							row.trElement.removeClassName("sending");
							return;
						}
						
						LinkedList<ObjectType> list = new LinkedList<ObjectType>();
						list.add(object);
						saveHandler.objectsModified(
								new LinkedList<ObjectType>(),
								new LinkedList<ObjectType>(),
								list,
								new AsyncCallback<Void>() {
									public void onSuccess(Void result) {
										row.trElement.removeClassName("sending");
										deleteRow(row);
									}
									public void onFailure(Throwable caught) {
										row.trElement.removeClassName("sending");
										Window.alert("Failed to delete row: " + caught.getMessage());
									}
								});
					}
				}));
	}

	private void deleteRow(Row row) {
		rowsByObjectID.remove(row.object.getID());
		row.trElement.removeFromParent();
	}
	
	public void addEditSaveColumn() {
		addColumn("Edit", "4em", null, new EditSaveColumn<ObjectType>("Edit", "Save",
				new EditSaveColumn.ClickCallback<ObjectType>() {
					public void enteredMode(ObjectType object) { enterRowEditingMode(rowsByObjectID.get(object.getID()), null); }
					public void exitedMode(ObjectType object) { exitRowEditingMode(rowsByObjectID.get(object.getID())); }
				}));
	}

	protected void enterRowEditingMode(Row row, EditingCell focusedCell) {
		assert(!row.inEditingMode);
		row.inEditingMode = true;
		
		for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
			IColumn<ObjectType> rawColumn = columns.get(colIndex).userColumn;
			if (rawColumn instanceof IEditingColumn) {
				IEditingColumn<ObjectType> column = (IEditingColumn<ObjectType>)rawColumn;
				EditingCell cell = (EditingCell)row.cells[colIndex];
				column.updateFromObject(row, cell);
				cell.enterEditingMode();
			}
		}
		
		if (focusedCell != null) {
			focusedCell.focus();
		}
		else {
			for (Cell cell : row.cells) {
				if (cell instanceof EditingCell) { 
					((EditingCell) cell).focus();
					break;
				}
			}
		}
	}

	protected void enterRowReadingMode(final Row row) {
		for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
			IColumn<ObjectType> rawColumn = columns.get(colIndex).userColumn;
			if (rawColumn instanceof IReadingColumn) {
				IReadingColumn<ObjectType> column = (IReadingColumn<ObjectType>)rawColumn;
				EditingCell cell = (EditingCell)row.cells[colIndex];
				cell.enterReadingMode();
				column.updateFromObject(row, cell);
			}
		}
	}
	
	protected void exitRowEditingMode(final Row row) {
		assert(row.inEditingMode);
		row.inEditingMode = false;

		for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
			IColumn<ObjectType> rawColumn = columns.get(colIndex).userColumn;
			if (rawColumn instanceof IEditingColumn) {
				IEditingColumn<ObjectType> column = (IEditingColumn<ObjectType>)rawColumn;
				EditingCell cell = (EditingCell)row.cells[colIndex];
				column.commitToObject(row, cell);
			}
		}
		
		row.trElement.addClassName("sending");

		LinkedList<ObjectType> addedList = new LinkedList<ObjectType>();
		LinkedList<ObjectType> editedList = new LinkedList<ObjectType>();
		
		if (row.adding)
			addedList.add(row.getObject());
		else
			editedList.add(row.getObject());
		
		saveHandler.objectsModified(
				addedList,
				editedList,
				new LinkedList<ObjectType>(),
				new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						row.trElement.removeClassName("sending");
						enterRowEditingMode(row, null);
						Window.alert("Failed to send updates to server: " + caught.getMessage());
					}
					
					@Override
					public void onSuccess(Void result) {
						row.trElement.removeClassName("sending");
					}
				});
		
		enterRowReadingMode(row);
	}
	
	public void clear() {
		for (int i = 0; i < rowsByObjectID.size(); i++)
			table.removeRow(2);
		rowsByObjectID.clear();
	}

	public final void addNewRow() {
		ObjectType newObject = factory.create();
		Row newRow = addRow(newObject);
		newRow.adding = true;

		enterRowEditingMode(newRow, null);
	}
	
	void setColumnWidth(Column column, int widthPixels) {
		if (widthPixels < 0)
			return;
		
		column.headerTDElement.setAttribute("style", "");
		
		column.headerContents.setWidth(widthPixels + "px");
		
		int columnIndex = columns.indexOf(column);
		
		for (Row row : rowsByObjectID.values())
			row.cells[columnIndex].getCellWidget().setWidth(widthPixels + "px");
	}
	
	public void addColumn(String name, String width, Comparator<? super ObjectType> comparator, final IColumn<ObjectType> userColumn) {		
		assert(rowsByObjectID.size() == 0);
		
		final int newColumnIndex = columns.size();
		
		final Widget headerContents = new HTML(name);
		headerContents.addStyleName("headerContents");
		
		table.setWidget(0, newColumnIndex, headerContents);
		Element headerTDElement = HTMLUtilities.getClosestContainingElementOfType(headerContents.getElement(), "td");
		
		FocusPanel contents = new ResizeableHeader(this, headerContents, new ResizeCallback() {
			public int getWidth() { return headerContents.getOffsetWidth(); }
			public void setWidth(int newWidthPixels) { setColumnWidth(columns.get(newColumnIndex), newWidthPixels); }
		});
		contents.addStyleName("header");
		table.setWidget(0, newColumnIndex, contents);
		 
		if (width != null)
			headerTDElement.setAttribute("style", "width: " + width);
		
		final Column column = new Column(width, headerContents, headerTDElement, comparator, userColumn);
		
		contents.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				sortByColumn(column);
			}
		});

		SimplePanel fakeHeader = new SimplePanel();
		SimplePanel fakeHeaderContents = new SimplePanel();
		fakeHeader.add(fakeHeaderContents);
		fakeHeaderContents.add(new HTML(name));
		fakeHeader.addStyleName("header");
		table.setWidget(1, newColumnIndex, fakeHeader);
		
		columns.add(column);
	}
	
	void sortByColumn(Column column) {
		if (column.comparator != null) {
			ArrayList<ObjectType> sortedList = new ArrayList<ObjectType>();
			for (Row row : rowsByObjectID.values())
				sortedList.add(row.object);
			Collections.sort(sortedList, column.comparator);
	
			for (Row row : this.rowsByObjectID.values())
				row.trElement.removeFromParent();
	
			Element tableElement = table.getElement();
			assert(tableElement.getNodeName().equalsIgnoreCase("table"));
			Element tbodyElement = tableElement.getElementsByTagName("tbody").getItem(0);
			assert(tbodyElement.getNodeName().equalsIgnoreCase("tbody"));
			assert(tbodyElement.getChildCount() == 2);
			
			for (int i = 0; i < sortedList.size(); i++) {
				ObjectType object = sortedList.get(i);
				Row row = rowsByObjectID.get(object.getID());
				tbodyElement.appendChild(row.trElement);
			}
		}
	}

	public Collection<ObjectType> getObjects() {
		ArrayList<ObjectType> result = new ArrayList<ObjectType>();
		for (Row row : rowsByObjectID.values())
			result.add(row.object);
		return result;
	}
	
	public final Row addRow(ObjectType object) {
		int newObjectIndex = rowsByObjectID.size();
		int rowIndex = newObjectIndex + 2;

		HTML placeholder = new HTML();
		table.setWidget(rowIndex, 0, placeholder);
		Element rowElement = HTMLUtilities.getClosestContainingElementOfType(placeholder.getElement(), "tr");

		Cell[] cells = new Cell[columns.size()];
		
		Row newRow = new Row(object, rowElement, cells);
		rowsByObjectID.put(object.getID(), newRow);
		
		for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
			Column column = columns.get(colIndex);
			Cell cell = column.userColumn.createCell(newRow);
			cells[colIndex] = cell;
		}
		
		enterRowReadingMode(newRow);
		
		for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
			Column column = columns.get(colIndex);
			Cell cell = cells[colIndex];
			
			assert(cell.getCellWidget() == cell.getCellWidget()); // make sure it returns the same instance every time
			table.setWidget(rowIndex, colIndex, cell.getCellWidget());

			Element td = HTMLUtilities.getClosestContainingElementOfType(cell.getCellWidget().getElement(), "td");
			if (column.width != null)
				td.setAttribute("style", td.getAttribute("style") + "; width: " + column.width);
		}

		return newRow;
	}
	
	public final void addRows(Collection<ObjectType> objects) {
		for (ObjectType object : objects)
			addRow(object);
	}	
	
	/** 
	 * @return true if the table has been saved, false if there are unsaved changes in the table
	 */
	public boolean isSaved(){
		for (Row row : rowsByObjectID.values())
			if (row.inEditingMode)
				return false;	
		return true;
	}
}
