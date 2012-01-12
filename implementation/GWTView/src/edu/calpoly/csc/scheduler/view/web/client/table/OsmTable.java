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
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ScrollEvent;
import com.google.gwt.user.client.Window.ScrollHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.HTMLUtilities;
import edu.calpoly.csc.scheduler.view.web.client.table.ResizeableWidget.ResizeCallback;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.DeleteColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.EditModeColumn;
import edu.calpoly.csc.scheduler.view.web.shared.Identified;

public class OsmTable<ObjectType extends Identified> extends VerticalPanel {
	enum ColumnSortMode { NOT_SORTING, ASCENDING, DESCENDING }
	
	public interface ModifyHandler<ObjectType> {
		void objectsModified(List<ObjectType> added, List<ObjectType> edited, List<ObjectType> removed, AsyncCallback<Void> callback);
	}
	
	public interface Cell {
		Widget getCellWidget();
	}
	
	public interface ReadingCell { }

	public interface ReadingModeAwareCell {
		void enterReadingMode();
	}
	
	public interface EditingModeAwareCell {
		void enterEditingMode();
	}
	
	public interface EditingCell {
		void focus();
	}

	public static class SimpleCell implements OsmTable.Cell {
		Widget widget;
		public SimpleCell(Widget widget) { this.widget = widget; }
		public Widget getCellWidget() { return widget; }
	}
	
	// Miscellaneous information about the column such as initial width, header, etc.
	// For OsmTable's use only.
	private class ColumnMetadata {
		ColumnSortMode sortMode = ColumnSortMode.NOT_SORTING;
		final IColumn<ObjectType> column;
		final ResizeableWidget header;
		final boolean stretchWidthToAccommodateNewRows;
		final Comparator<? super ObjectType> comparator;
		public ColumnMetadata(
				IColumn<ObjectType> column,
				ResizeableWidget header,
				boolean stretchWidthToAccommodateNewRows,
				Comparator<? super ObjectType> comparator) {
			this.column = column;
			this.header = header;
			this.stretchWidthToAccommodateNewRows = stretchWidthToAccommodateNewRows;
			this.comparator = comparator;
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
		void delete();
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
			assert(this != null);
			OsmTable.this.exitRowEditingMode(this);
		}
		
		public void delete() {
			OsmTable.this.deleteRow(this);
		}
	}
	
	protected final IFactory<ObjectType> factory;
	protected final ModifyHandler<ObjectType> saveHandler;
	protected final ArrayList<ColumnMetadata> columnMetadatas = new ArrayList<ColumnMetadata>();
	protected final Map<Integer, Row> rowsByObjectID = new HashMap<Integer, Row>();
	protected HorizontalPanel headers;
	protected boolean headerFloating;
	protected Element colgroupElement;
	protected final FlexTable table;
	protected ColumnMetadata currentSortedColumn;
	
	public OsmTable(IFactory<ObjectType> factory, final ModifyHandler<ObjectType> saveHandler) {
		this.saveHandler = saveHandler;
		this.factory = factory;

		addStyleName("osmtableContainer");
		
		createNewObjectButtons();
		
		createHeaders();
		
		table = new FlexTable();
		table.addStyleName("osmtable");
		table.setCellPadding(0);
		table.setCellSpacing(0);
		add(table);
		
		colgroupElement = DOM.createColGroup();
		table.getElement().insertFirst(colgroupElement);
	}
	
	private void createNewObjectButtons() {
		FocusPanel newObjectPanel = new FocusPanel();
		newObjectPanel.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) { addNewRow(); }
		});
		add(newObjectPanel);
		
		add(new Button("New", new ClickHandler() {
			public void onClick(ClickEvent event) { addNewRow(); }
		}));
	}
	
	private void createHeaders() {
		final SimplePanel substituteHeaders = new SimplePanel();
		add(substituteHeaders);
		
		headers = new HorizontalPanel();
		headers.addStyleName("headers");
		add(headers);
		
		headerFloating = false;
		
		Window.addWindowScrollHandler(new ScrollHandler() {
			public void onWindowScroll(ScrollEvent event) {
				if (!headerFloating) {
					if (event.getScrollTop() > substituteHeaders.getAbsoluteTop()) {
						headerFloating = true;
						substituteHeaders.setHeight(headers.getOffsetHeight() + "px");
						headers.addStyleName("floating");
						for (ColumnMetadata col : columnMetadatas)
							col.header.synchronize();
					}
				}
				else {
					if (event.getScrollTop() < substituteHeaders.getAbsoluteTop()) {
						headerFloating = false;
						headers.removeStyleName("floating");
						substituteHeaders.setHeight("");
						for (ColumnMetadata col : columnMetadatas)
							col.header.synchronize();
					}
				}
			}
		});
	}

	public void addColumn(String name, String initialWidth, boolean resizable, boolean stretchWidthToAccommodateNewRows, Comparator<? super ObjectType> comparator, final IColumn<ObjectType> column) {
		assert(rowsByObjectID.size() == 0);
		final int newColumnIndex = columnMetadatas.size();
		
		final Element colElement = DOM.createCol();
		colElement.setId("col" + newColumnIndex);
		colgroupElement.appendChild(colElement);
		if (initialWidth != null)
			colElement.setAttribute("style", "width: " + initialWidth);
		
		final HTML headerContents = new HTML(name);
		headerContents.addStyleName("headerContents");
		
		ResizeableWidget resizeableHeader = new ResizeableWidget(this, resizable, headerContents, new ResizeCallback() {
			public void setWidth(int newWidthPixels) {
				if (rowsByObjectID.size() == 0)
					return;
				HTMLUtilities.getClosestContainingElementOfType(table.getWidget(0, newColumnIndex).getElement(), "td").setAttribute("style", "width: " + newWidthPixels + "px");
			}
			public int getWidth() {
				if (rowsByObjectID.size() == 0)
					return 0;
				return HTMLUtilities.getClosestContainingElementOfType(table.getWidget(0, newColumnIndex).getElement(), "td").getOffsetWidth();
			}
		});
		headers.add(resizeableHeader);
		
		final ColumnMetadata columnMetadata = new ColumnMetadata(column, resizeableHeader, stretchWidthToAccommodateNewRows, comparator);

		headerContents.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				toggleSortingForColumn(columnMetadata);
			}
		});
		
//		header.addClickHandler(new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				sortByColumn(column);
//			}
//		});
//		
//		sorter.setSortCallback(new SortCallback() {
//			public void sort(boolean ascending) {
//				sortByColumn(column, ascending);
//			}
//		});

		columnMetadatas.add(columnMetadata);
	}
	
	public void addDeleteColumn() {
		addColumn("", null, false, true, null, new DeleteColumn<ObjectType>());
	}

	private void deleteRow(final Row row) {
		row.trElement.addClassName("sending");
		
		if (!Window.confirm("Are you sure you want to delete " + row.object.toString() + "?")) {
			row.trElement.removeClassName("sending");
			return;
		}
		
		LinkedList<ObjectType> list = new LinkedList<ObjectType>();
		list.add(row.object);
		saveHandler.objectsModified(
				new LinkedList<ObjectType>(),
				new LinkedList<ObjectType>(),
				list,
				new AsyncCallback<Void>() {
					public void onSuccess(Void result) {
						row.trElement.removeClassName("sending");
						rowsByObjectID.remove(row.object.getID());
						row.trElement.removeFromParent();
					}
					public void onFailure(Throwable caught) {
						row.trElement.removeClassName("sending");
						Window.alert("Failed to delete row: " + caught.getMessage());
					}
				});
	}
	
	public void addEditSaveColumn() {
		addColumn("", null, false, true, null, new EditModeColumn<ObjectType>());
	}

	protected void enterRowEditingMode(Row row, EditingCell focusedCell) {
		assert(!row.inEditingMode);
		row.inEditingMode = true;
		
		for (int colIndex = 0; colIndex < columnMetadatas.size(); colIndex++) {
			Cell rawCell = row.cells[colIndex];
			
			if (rawCell instanceof ReadingCell) {
				ReadingCell cell = (ReadingCell)rawCell;
				IReadingColumn<ObjectType> column = (IReadingColumn<ObjectType>)columnMetadatas.get(colIndex).column;
				column.updateFromObject(row, cell);
			}
			
			if (rawCell instanceof EditingModeAwareCell) {
				EditingModeAwareCell cell = (EditingModeAwareCell)rawCell;
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
		for (int colIndex = 0; colIndex < columnMetadatas.size(); colIndex++) {
			Cell rawCell = row.cells[colIndex];
			
			if (rawCell instanceof ReadingCell) {
				ReadingCell cell = (ReadingCell)rawCell;
				IReadingColumn<ObjectType> column = (IReadingColumn<ObjectType>)columnMetadatas.get(colIndex).column;
				column.updateFromObject(row, cell);
			}

			if (rawCell instanceof ReadingModeAwareCell) {
				ReadingModeAwareCell cell = (ReadingModeAwareCell)rawCell;
				cell.enterReadingMode();
			}
		}
	}
	
	protected void exitRowEditingMode(final Row row) {
		assert(row != null);
		assert(row.inEditingMode);
		row.inEditingMode = false;

		for (int colIndex = 0; colIndex < columnMetadatas.size(); colIndex++) {
			Cell rawCell = row.cells[colIndex];
			
			if (rawCell instanceof EditingCell) {
				EditingCell cell = (EditingCell)rawCell;
				IEditingColumn<ObjectType> column = (IEditingColumn<ObjectType>)columnMetadatas.get(colIndex).column;
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
						row.adding = false;
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
		Row newRow = addAndReturnRow(newObject);
		newRow.adding = true;

		enterRowEditingMode(newRow, null);
	}
	
	private void toggleSortingForColumn(ColumnMetadata column) {
		if (column.sortMode == ColumnSortMode.ASCENDING)
			sortByColumn(column, false);
		else
			sortByColumn(column, true);
	}
	
	void sortByColumn(ColumnMetadata column, boolean ascending) {
		assert(column != null);
		
//		System.out.println("in sortbycolumn!");
		
		if (currentSortedColumn != null)
			currentSortedColumn.sortMode = ColumnSortMode.NOT_SORTING;
		currentSortedColumn = column;
		currentSortedColumn.sortMode = ascending ? ColumnSortMode.ASCENDING : ColumnSortMode.DESCENDING;
		
//		System.out.println(column.comparator != null);
		
		if (column.comparator != null) {
//			System.out.println("flerp");
					
			ArrayList<ObjectType> sortedList = new ArrayList<ObjectType>();
			for (Row row : rowsByObjectID.values())
				sortedList.add(row.object);
			Collections.sort(sortedList, column.comparator);
			
//			System.out.println("kerp");
					
			if (!ascending)
				Collections.reverse(sortedList);
	
			for (Row row : this.rowsByObjectID.values())
				row.trElement.removeFromParent();
	
			Element tableElement = table.getElement();
			assert(tableElement.getNodeName().equalsIgnoreCase("table"));
			Element tbodyElement = tableElement.getElementsByTagName("tbody").getItem(0);
			assert(tbodyElement.getNodeName().equalsIgnoreCase("tbody"));
			assert(tbodyElement.getChildCount() == 0);
			
			for (int i = 0; i < sortedList.size(); i++) {
//				System.out.println("derp " + i);
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
	
	private Row addAndReturnRowWithoutRefreshingWidths(ObjectType object) {
//		System.out.println("adding " + object.getID());
		
		int newObjectIndex = rowsByObjectID.size();
		int rowIndex = newObjectIndex;

		HTML placeholder = new HTML();
		table.setWidget(rowIndex, 0, placeholder);
		Element rowElement = HTMLUtilities.getClosestContainingElementOfType(placeholder.getElement(), "tr");

		Cell[] cells = new Cell[columnMetadatas.size()];
		
		Row newRow = new Row(object, rowElement, cells);
		rowsByObjectID.put(object.getID(), newRow);
		
		for (int colIndex = 0; colIndex < columnMetadatas.size(); colIndex++) {
			ColumnMetadata column = columnMetadatas.get(colIndex);
			Cell cell = column.column.createCell(newRow);
			cells[colIndex] = cell;
		}
		
		enterRowReadingMode(newRow);
		
		for (int colIndex = 0; colIndex < columnMetadatas.size(); colIndex++) {
			Cell cell = cells[colIndex];
			assert(cell.getCellWidget() == cell.getCellWidget()); // make sure it returns the same instance every time
			table.setWidget(rowIndex, colIndex, cell.getCellWidget());
		}

		return newRow;
	}

	private Row addAndReturnRow(ObjectType object) {
		Row result = addAndReturnRowWithoutRefreshingWidths(object);
		refreshWidths();
		return result;
	}
	
	public final void addRow(ObjectType object) {
		addAndReturnRowWithoutRefreshingWidths(object);
		refreshWidths();
	}
	
	private void refreshWidths() {
		for (ColumnMetadata col : columnMetadatas) {
			if (col.stretchWidthToAccommodateNewRows)
				col.header.synchronizeToMaximumOfBoth();
			else
				col.header.synchronize();
		}
	}
	
	public final void addRows(Collection<ObjectType> objects) {
		for (ObjectType object : objects)
			addAndReturnRowWithoutRefreshingWidths(object);
		refreshWidths();
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
