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
		void add(ObjectType toAdd, AsyncCallback<ObjectType> callback);
		void edit(ObjectType toEdit, AsyncCallback<Void> callback);
		void remove(ObjectType toRemove, AsyncCallback<Void> callback);
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
		final Comparator<? super ObjectType> comparator;
		public ColumnMetadata(
				IColumn<ObjectType> column,
				ResizeableWidget header,
				Comparator<? super ObjectType> comparator) {
			this.column = column;
			this.header = header;
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
		public ObjectType object;
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
	protected final FlexTable table;
	protected ColumnMetadata currentSortedColumn;
	protected Element hiddenRowThatMaintainsWidths;
	
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
							col.header.updateWidth();
					}
				}
				else {
					if (event.getScrollTop() < substituteHeaders.getAbsoluteTop()) {
						headerFloating = false;
						headers.removeStyleName("floating");
						substituteHeaders.setHeight("");
						for (ColumnMetadata col : columnMetadatas)
							col.header.updateWidth();
					}
				}
			}
		});
	}

	public void addColumn(String name, String initialWidth, boolean resizable, Comparator<? super ObjectType> comparator, final IColumn<ObjectType> column) {
		assert(rowsByObjectID.size() == 0);
		final int newColumnIndex = columnMetadatas.size();
		
		HTML hiddenCellThatMaintainsWidthsContents = new HTML(name);
		hiddenCellThatMaintainsWidthsContents.addStyleName("headerContents");
		table.setWidget(0, newColumnIndex, hiddenCellThatMaintainsWidthsContents);
		final Element hiddenCellThatMaintainsWidths = HTMLUtilities.getClosestContainingElementOfType(hiddenCellThatMaintainsWidthsContents.getElement(), "td");
		
		if (hiddenRowThatMaintainsWidths == null) {
			hiddenRowThatMaintainsWidths = HTMLUtilities.getClosestContainingElementOfType(hiddenCellThatMaintainsWidthsContents.getElement(), "tr");
			hiddenRowThatMaintainsWidths.addClassName("hiddenRow");
		}
		
		final HTML headerContents = new HTML(name);
		headerContents.addStyleName("headerContents");
		
		ResizeableWidget resizeableHeader = new ResizeableWidget(this, initialWidth, resizable, headerContents, new ResizeCallback() {
			public void trySettingWidth(int newWidthPixels) {
				hiddenCellThatMaintainsWidths.setAttribute("style", "width: " + newWidthPixels + "px");
				if (hiddenCellThatMaintainsWidths.getOffsetWidth() != newWidthPixels) {
					newWidthPixels = hiddenCellThatMaintainsWidths.getOffsetWidth();
					hiddenCellThatMaintainsWidths.setAttribute("style", "width: " + newWidthPixels + "px");
					assert(hiddenCellThatMaintainsWidths.getOffsetWidth() == newWidthPixels);
				}
			}
			public int getWidth() {
				return hiddenCellThatMaintainsWidths.getOffsetWidth();
			}
		});
		headers.add(resizeableHeader);
		
		final ColumnMetadata columnMetadata = new ColumnMetadata(column, resizeableHeader, comparator);

		headerContents.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				toggleSortingForColumn(columnMetadata);
			}
		});

		columnMetadatas.add(columnMetadata);
	}
	
	public void addDeleteColumn() {
		addColumn("", null, false, null, new DeleteColumn<ObjectType>());
	}

	private void deleteRow(final Row row) {
		row.trElement.addClassName("sending");
		
		if (!Window.confirm("Are you sure you want to delete " + row.object.toString() + "?")) {
			row.trElement.removeClassName("sending");
			return;
		}
		
		saveHandler.remove(row.object, new AsyncCallback<Void>() {
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
		addColumn("", null, false, null, new EditModeColumn<ObjectType>());
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

		updateHeaderWidths();
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
		
		updateHeaderWidths();
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
		
		final int oldObjectID = row.getObject().getID();

		if (row.adding) {
			saveHandler.add(row.getObject(), new AsyncCallback<ObjectType>() {
				@Override
				public void onSuccess(ObjectType newObject) {
					row.trElement.removeClassName("sending");
					row.adding = false;
					
					System.out.println("Changing " + oldObjectID + " to " + newObject.getID());

					rowsByObjectID.remove(row.object.getID());
					row.object = newObject;
					rowsByObjectID.put(row.object.getID(), row);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					row.trElement.removeClassName("sending");
					enterRowEditingMode(row, null);
					Window.alert("Failed to send updates to server: " + caught.getMessage());
				}
			});
		}
		else {
			saveHandler.edit(row.getObject(), new AsyncCallback<Void>() {
				
				@Override
				public void onSuccess(Void result) {
					row.trElement.removeClassName("sending");
				}
				
				@Override
				public void onFailure(Throwable caught) {
					row.trElement.removeClassName("sending");
					enterRowEditingMode(row, null);
					Window.alert("Failed to send updates to server: " + caught.getMessage());
				}
			});
			
		}
		
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
			assert(tbodyElement.getChildCount() == 1);
			
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
	
	private Row addAndReturnRowWithoutRefreshingWidths(ObjectType object) {
		int newObjectIndex = rowsByObjectID.size();
		int rowIndex = 1 + newObjectIndex;

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
		updateHeaderWidths();
		return result;
	}
	
	public final void addRow(ObjectType object) {
		addAndReturnRowWithoutRefreshingWidths(object);
		updateHeaderWidths();
	}
	
	private void updateHeaderWidths() {
		for (ColumnMetadata col : columnMetadatas)
			col.header.updateWidth();
	}
	
	public final void addRows(Collection<ObjectType> objects) {
		for (ObjectType object : objects)
			addAndReturnRowWithoutRefreshingWidths(object);
		updateHeaderWidths();
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
