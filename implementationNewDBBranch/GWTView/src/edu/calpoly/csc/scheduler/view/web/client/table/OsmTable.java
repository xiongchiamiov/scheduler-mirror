package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ScrollEvent;
import com.google.gwt.user.client.Window.ScrollHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.calpoly.csc.scheduler.view.web.client.HTMLUtilities;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticValidator.InputInvalid;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticValidator.InputValid;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticValidator.InputWarning;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticValidator.ValidateResult;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.EditingCell.ExitedEditingModeHandler;
import edu.calpoly.csc.scheduler.view.web.client.table.ResizeableWidget.ResizeCallback;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.DeleteColumn;
import edu.calpoly.csc.scheduler.view.web.shared.Identified;

public class OsmTable<ObjectType extends Identified> extends VerticalPanel implements ClickHandler, DoubleClickHandler {
	enum ColumnSortMode { NOT_SORTING, ASCENDING, DESCENDING }
	
	public interface ObjectChangedObserver<ObjectType extends Identified> {
		void objectChanged(ObjectType object);
	}
	
	public static class Cell extends FocusPanel {
		public interface DeleteCallback {
			void deleteRow();
		}
	}
	
	public static class ReadingCell extends Cell { }

	public static abstract class EditingCell extends ReadingCell {
		public interface ValueChangedObserver {
			void valueChanged();
		}
		
		private ValueChangedObserver valueChangedObserver;
		void setValueChangedObserver(ValueChangedObserver handler) {
			assert(this.valueChangedObserver == null);
			this.valueChangedObserver = handler;
		}
		protected void notifyValueChanged() {
			if (valueChangedObserver != null) {
				System.out.println("calling value changed observer!");
				valueChangedObserver.valueChanged();
			}
		}

		
		
		
		public interface ExitedEditingModeHandler {
			void exitedEditingMode();
		}
		
		private ExitedEditingModeHandler exitedEditingModeHandler;
		void setExitedEditingModeHandler(ExitedEditingModeHandler handler) {
			assert(this.exitedEditingModeHandler == null);
			this.exitedEditingModeHandler = handler;
		}

		private boolean editing;
		public boolean isInEditingMode() { return editing; }
		public void setInEditingMode(boolean editing) {
			System.out.println("Setting " + getClass().getName() + " editing which was " + this.editing + " to " + editing);
			if (this.editing == editing)
				return;
			
			if (editing) {
				this.editing = true;
				enteredEditingMode();
			}
			else {
				exitedEditingMode();
				this.editing = false;
				exitedEditingModeHandler.exitedEditingMode();
			}
		}
		
		protected abstract void enteredEditingMode();
		protected abstract void exitedEditingMode();
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
		public abstract Cell createCell(IRowForColumn<ObjectType> row);
	}
	
	public interface IReadingColumn<ObjectType extends Identified> extends IColumn<ObjectType> {
		abstract public void updateFromObject(IRowForColumn<ObjectType> row, ReadingCell cell);
	}
	
	public interface IEditingColumn<ObjectType extends Identified> extends IReadingColumn<ObjectType> {
		public ValidateResult validate(IRowForColumn<ObjectType> row, EditingCell cell);

		public void commitToObject(IRowForColumn<ObjectType> row, EditingCell cell);
	}
	
	// TODO: change these to callbacks
	public interface IRowForColumn<ObjectType extends Identified> {
		ObjectType getObject();
		void delete();
	}

	protected class Row implements IRowForColumn<ObjectType> {
		public int index;
		public ObjectType object;
		public final Element trElement;
		public final ArrayList<CellContainer> cellContainers;
		public Row(int index, ObjectType object, Element trElement, ArrayList<CellContainer> cellContainers) {
			this.index = index;
			this.object = object;
			this.trElement = trElement;
			this.cellContainers = cellContainers;
		}
		
		@Override
		public ObjectType getObject() { return object; }
		
		public void delete() {
			OsmTable.this.deleteRow(this);
		}
	}

	private class CellContainer {
		final Row row;
		final int colIndex;
		final Cell cell;
		public CellContainer(Row row, int colIndex, Cell cell) {
			this.row = row;
			this.colIndex = colIndex;
			this.cell = cell;
		}
	}

	protected ObjectChangedObserver<ObjectType> objectChangedObserver;
	protected final IFactory<ObjectType> factory;
	protected final ArrayList<ColumnMetadata> columnMetadatas = new ArrayList<ColumnMetadata>();
	protected final Map<Integer, Row> rowsByObjectID = new HashMap<Integer, Row>();
	protected final List<Row> rowsInDisplayedOrder = new ArrayList<Row>();
	protected HorizontalPanel headers;
	protected boolean headerFloating;
	protected final FlexTable table;
	protected ColumnMetadata currentSortedColumn;
	protected Element hiddenRowThatMaintainsWidths;
	protected CellContainer selectedCellContainer;
	
	public OsmTable(IFactory<ObjectType> factory) {
		this.factory = factory;

		addStyleName("osmtableContainer");
		
		addNewObjectButton();
		
		createHeaders();
		
		table = new FlexTable();
		table.addStyleName("osmtable");
		table.setCellPadding(0);
		table.setCellSpacing(0);
		add(table);

		table.addClickHandler(this);
		table.addDoubleClickHandler(this);
		
		createNewObjectTabThingAfterTable();

		addNewObjectButton();
		
		add(new Button("updateheaderwidths", new ClickHandler() {
			public void onClick(ClickEvent event) {
				updateHeaderWidths();
			}
		}));
		
		
		
		
		
		
		final FlexTable target = table;
//		add(new Button("fire tab event1", new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				System.out.println("Firing native tab key down event");
//				NativeEvent tabEvent = Document.get().createKeyDownEvent(false, false, false, false, 9);
//				DomEvent.fireNativeEvent(tabEvent, target);
//			}
//		}));
		
		add(new Button("fire tab event?", new ClickHandler() {
			public void onClick(ClickEvent event) {
				runMacro();
			}
		}));
	}
	
	/**
	 * Used to register callback methods for access via handwritten javascript
	 */
	private native void runMacro() /*-{
		var fiveVar = 5;
    }-*/;
	
	
	
	
	
	
	
	
	public void setObjectChangedObserver(ObjectChangedObserver<ObjectType> obs) {
		assert(objectChangedObserver == null);
		objectChangedObserver = obs;
	}
	
	private void addNewObjectButton() {
		add(new Button("New", new ClickHandler() {
			public void onClick(ClickEvent event) { addNewRow(); }
		}));
	}
	
	// Adds a focus panel after the table, so if you tab past everything else, it creates a new row
	private void createNewObjectTabThingAfterTable() {
		FocusPanel newObjectPanel = new FocusPanel();
		newObjectPanel.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) { addNewRow(); }
		});
		add(newObjectPanel);
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

	public void addColumn(String name, String minimumWidth, boolean resizable, Comparator<? super ObjectType> comparator, final IColumn<ObjectType> column) {
		assert(rowsByObjectID.size() == 0);
		final int newColumnIndex = columnMetadatas.size();
		
		HTML hiddenCellThatMaintainsWidthsContents = new HTML(name);
		hiddenCellThatMaintainsWidthsContents.addStyleName("headerContents");
		table.setWidget(0, newColumnIndex, hiddenCellThatMaintainsWidthsContents);
		final Element hiddenCellThatMaintainsWidths = HTMLUtilities.getClosestContainingElementOfType(hiddenCellThatMaintainsWidthsContents.getElement(), "td");
		if (minimumWidth != null)
			hiddenCellThatMaintainsWidths.setAttribute("style", "min-width: " + minimumWidth);
		
		if (hiddenRowThatMaintainsWidths == null) {
			hiddenRowThatMaintainsWidths = HTMLUtilities.getClosestContainingElementOfType(hiddenCellThatMaintainsWidthsContents.getElement(), "tr");
			hiddenRowThatMaintainsWidths.addClassName("hiddenRow");
		}
		
		final HTML headerContents = new HTML(name);
		headerContents.addStyleName("headerContents");
		
		ResizeableWidget resizeableHeader = new ResizeableWidget(this, resizable, headerContents, new ResizeCallback() {
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
	
	public void addDeleteColumn(DeleteColumn.DeleteObserver<ObjectType> handler) {
		addColumn("", null, false, null, new DeleteColumn<ObjectType>(handler));
	}

	private void deleteRow(final Row row) {
		rowsByObjectID.remove(row.object.getID());
		row.trElement.removeFromParent();
	}
	
	private Row addAndReturnRowWithoutUpdatingHeaderWidths(final ObjectType object) {
		int newObjectIndex = rowsByObjectID.size();
		int rowIndex = 1 + newObjectIndex;

		HTML placeholder = new HTML();
		table.setWidget(rowIndex, 0, placeholder);
		Element rowElement = HTMLUtilities.getClosestContainingElementOfType(placeholder.getElement(), "tr");

		ArrayList<CellContainer> cellContainers = new ArrayList<CellContainer>();
//		CellContainer<ObjectType>[] cellContainers = new CellContainer[columnMetadatas.size()];
		
		final Row newRow = new Row(newObjectIndex, object, rowElement, cellContainers);
		rowsByObjectID.put(object.getID(), newRow);
		
		rowsInDisplayedOrder.add(newRow);
		
		// Create cells for this row
		for (int colIndex = 0; colIndex < columnMetadatas.size(); colIndex++) {
			final Cell cell = columnMetadatas.get(colIndex).column.createCell(newRow);
			table.setWidget(rowIndex, colIndex, cell);
			final CellContainer cellContainer = new CellContainer(newRow, colIndex, cell);
			cellContainers.add(cellContainer);
			
			cell.addKeyDownHandler(new KeyDownHandler() {
				public void onKeyDown(KeyDownEvent event) {
					switch (event.getNativeKeyCode()) {
						case 37: { // left arrow
							CellContainer newSelectedCellContainer = cellContainerAt(cellContainer.row.index, cellContainer.colIndex - 1);
							if (newSelectedCellContainer != null)
								select(newSelectedCellContainer);
						} break;
						
						case 38: { // up arrow
							CellContainer newSelectedCellContainer = cellContainerAt(cellContainer.row.index - 1, cellContainer.colIndex);
							if (newSelectedCellContainer != null)
								select(newSelectedCellContainer);
						} break;
						
						case 39: { // right arrow
							CellContainer newSelectedCellContainer = cellContainerAt(cellContainer.row.index, cellContainer.colIndex + 1);
							if (newSelectedCellContainer != null)
								select(newSelectedCellContainer);
						} break;
						
						case 40: { // down arrow
							CellContainer newSelectedCellContainer = cellContainerAt(cellContainer.row.index + 1, cellContainer.colIndex);
							if (newSelectedCellContainer != null)
								select(newSelectedCellContainer);
						} break;
						
						case 9: { // tab
							if (event.isShiftKeyDown() == false) {
								CellContainer newSelectedCellContainer = cellContainerAt(cellContainer.row.index, cellContainer.colIndex + 1);
								if (newSelectedCellContainer == null)
									newSelectedCellContainer = cellContainerAt(cellContainer.row.index + 1, 0); // first on next line
								if (newSelectedCellContainer != null)
									select(newSelectedCellContainer);
								if (newSelectedCellContainer == null) // if there is no next line, then make one
									addNewRow();
							}
							else {
								CellContainer newSelectedCellContainer = cellContainerAt(cellContainer.row.index, cellContainer.colIndex - 1);
								if (newSelectedCellContainer == null)
									newSelectedCellContainer = cellContainerAt(cellContainer.row.index - 1, columnMetadatas.size() - 1); // last on previous line
								if (newSelectedCellContainer != null)
									select(newSelectedCellContainer);
							}
						} break;
						
						case 13: { // enter
							if (selectedCellContainer != null && selectedCellContainer.cell instanceof EditingCell) {
								EditingCell editingCell = (EditingCell)selectedCellContainer.cell;
								if (!editingCell.isInEditingMode()) {
									CellContainer newSelectedCellContainer = cellContainerAt(cellContainer.row.index + 1, cellContainer.colIndex);
									if (newSelectedCellContainer == null) {
										addNewRow();
										newSelectedCellContainer = cellContainerAt(cellContainer.row.index + 1, cellContainer.colIndex);
									}
									if (newSelectedCellContainer != null)
										select(newSelectedCellContainer);
								}
							}
						} break;
					}
				}
			});

			
//			if (cell instanceof ReadingCell) {
//				final ReadingCell readingCell = (ReadingCell)cell;
//				readingCell.setSizeChangedObserver(new ReadingCell.SizeChangedObserver() {
//					public void sizeChanged() {
//						updateHeaderWidths();
//					}
//				});
//			}

			IColumn<ObjectType> column = columnMetadatas.get(colIndex).column;
			assert((cell instanceof EditingCell) == (column instanceof IEditingColumn));
			if (cell instanceof EditingCell) {
				final EditingCell editingCell = (EditingCell)cell;
				final IEditingColumn<ObjectType> editingColumn = (IEditingColumn<ObjectType>)column;
				
				editingCell.setExitedEditingModeHandler(new ExitedEditingModeHandler() {
					public void exitedEditingMode() {
						ValidateResult result = editingColumn.validate(newRow, editingCell);

						if (result instanceof InputValid || result instanceof InputWarning) {
							editingColumn.commitToObject(newRow, editingCell);
							editingColumn.updateFromObject(newRow, editingCell);
							objectChangedObserver.objectChanged(object);
						}
						else if (result instanceof InputInvalid) {
							editingColumn.updateFromObject(newRow, editingCell);
						}
						else
							assert(false);

						updateHeaderWidths();

						colorCell(cellContainer, result);
					}
				});
				
				editingCell.setValueChangedObserver(new EditingCell.ValueChangedObserver() {
					public void valueChanged() {
						System.out.println("calling colorcell!");
						colorCell(cellContainer, null);
					}
				});
			}
		}
		
		// Have all reading cells get updated from the object
		for (int colIndex = 0; colIndex < columnMetadatas.size(); colIndex++) {
			CellContainer cellContainer = newRow.cellContainers.get(colIndex);
			Cell rawCell = cellContainer.cell;
			IColumn<ObjectType> rawColumn = columnMetadatas.get(colIndex).column;

			assert((rawCell instanceof ReadingCell) == (rawColumn instanceof IReadingColumn));
			if (rawCell instanceof ReadingCell && rawColumn instanceof IReadingColumn)
				((IReadingColumn<ObjectType>)rawColumn).updateFromObject(newRow, (ReadingCell)rawCell);
			
			colorCell(cellContainer, null);
		}
		
		return newRow;
	}
	
	private void colorCell(CellContainer container, ValidateResult validationResult) {
		if (container.cell instanceof EditingCell) {
			
			if (validationResult == null) {
				IColumn<ObjectType> column = columnMetadatas.get(container.colIndex).column;
				assert(column instanceof IEditingColumn);
				IEditingColumn<ObjectType> editingColumn = (IEditingColumn<ObjectType>)column;
				
				EditingCell editingCell = (EditingCell)container.cell;
				
				validationResult = editingColumn.validate(container.row, editingCell);
			}
			

			Element td = HTMLUtilities.getClosestContainingElementOfType(container.cell.getElement(), "td");
			
			if (validationResult instanceof InputValid) {
				td.removeClassName("invalid");
			}
			else if (validationResult instanceof InputWarning) {
				InputWarning inputWarning = (InputWarning)validationResult;
				td.setAttribute("title", inputWarning.reason);
				td.addClassName("invalid");
			}
			else if (validationResult instanceof InputInvalid) {
				
			}
			else
				assert(false);
		}
	}
	
	private CellContainer cellContainerAt(int row, int col) {
		if (row < 0 || row >= rowsByObjectID.size())
			return null;
		if (col < 0 || col >= columnMetadatas.size())
			return null;
		return rowsInDisplayedOrder.get(row).cellContainers.get(col);
	}
	
	private Row addAndReturnRow(ObjectType object) {
		Row result = addAndReturnRowWithoutUpdatingHeaderWidths(object);
		updateHeaderWidths();
		return result;
	}
	
	public final void addRow(ObjectType object) {
		addAndReturnRowWithoutUpdatingHeaderWidths(object);
		updateHeaderWidths();
	}

	public final void addNewRow() {
		ObjectType newObject = factory.create();
		Row newRow = addAndReturnRow(newObject);

		select(newRow.cellContainers.get(0));
	}

	public void clear() {
		for (int i = 0; i < rowsByObjectID.size(); i++)
			table.removeRow(1);
		rowsByObjectID.clear();
	}
	
	private void toggleSortingForColumn(ColumnMetadata column) {
		if (column.sortMode == ColumnSortMode.ASCENDING)
			sortByColumn(column, false);
		else
			sortByColumn(column, true);
	}
	
	void sortByColumn(final ColumnMetadata column, boolean ascending) {
		assert(column != null);
		
//		System.out.println("in sortbycolumn!");
		
		if (currentSortedColumn != null)
			currentSortedColumn.sortMode = ColumnSortMode.NOT_SORTING;
		currentSortedColumn = column;
		currentSortedColumn.sortMode = ascending ? ColumnSortMode.ASCENDING : ColumnSortMode.DESCENDING;
		
//		System.out.println(column.comparator != null);
		
		if (column.comparator != null) {
//			System.out.println("flerp");
			
			Comparator<Row> comparator = new Comparator<Row>() {
				public int compare(Row a, Row b) {
					return column.comparator.compare(a.object, b.object);
				};
			};
			
			Collections.sort(rowsInDisplayedOrder, comparator);
					
			if (!ascending)
				Collections.reverse(rowsInDisplayedOrder);
	
			for (Row row : this.rowsByObjectID.values())
				row.trElement.removeFromParent();
	
			Element tableElement = table.getElement();
			assert(tableElement.getNodeName().equalsIgnoreCase("table"));
			Element tbodyElement = tableElement.getElementsByTagName("tbody").getItem(0);
			assert(tbodyElement.getNodeName().equalsIgnoreCase("tbody"));
			assert(tbodyElement.getChildCount() == 1);
			
			for (int i = 0; i < rowsInDisplayedOrder.size(); i++) {
				Row row = rowsInDisplayedOrder.get(i);
				row.index = i;
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
	
	private void updateHeaderWidths() {
		for (ColumnMetadata col : columnMetadatas)
			col.header.updateWidth();
	}
	
	public final void addRows(Collection<ObjectType> objects) {
		for (ObjectType object : objects)
			addAndReturnRowWithoutUpdatingHeaderWidths(object);
		updateHeaderWidths();
	}	
	
	/** 
	 * @return true if the table has been saved, false if there are unsaved changes in the table
	 */
	public boolean isSaved(){
		assert(false);
//		for (Row row : rowsByObjectID.values())
//			if (row.inEditingMode)
//				return false;	
		return true;
	}
	
	@Override
	public void onClick(ClickEvent event) {
		System.out.println("got click event");
		
        int cellIndex = table.getCellForEvent(event).getCellIndex();
        int rowIndex = table.getCellForEvent(event).getRowIndex();
        
        if (rowIndex == 0) // row 0 is the hidden row
        	return;

        select(cellContainerAt(rowIndex - 1, cellIndex));

        event.stopPropagation();
	}

	private void select(CellContainer newSelectedCellContainer) {
		if (newSelectedCellContainer == selectedCellContainer)
			return;
		
		if (selectedCellContainer != null) {
	    	Element containingTD = HTMLUtilities.getClosestContainingElementOfType(selectedCellContainer.cell.getElement(), "td");
	        containingTD.removeClassName("selected");
	        
	        if (selectedCellContainer.cell instanceof EditingCell) {
	        	EditingCell editingCell = (EditingCell)selectedCellContainer.cell;
	        	editingCell.setInEditingMode(false);
	        }
		}
		
		selectedCellContainer = newSelectedCellContainer;
		
		if (selectedCellContainer != null) {
	    	Element containingTD = HTMLUtilities.getClosestContainingElementOfType(selectedCellContainer.cell.getElement(), "td");
	        containingTD.addClassName("selected");
			selectedCellContainer.cell.setFocus(true);
		}
	}

	@Override
	public void onDoubleClick(DoubleClickEvent event) {
		System.out.println("got doubleclick event");
		
		assert(selectedCellContainer != null);
		
        if (selectedCellContainer.cell instanceof EditingCell) {
        	System.out.println("Setting to editing mode");
        	EditingCell editingCell = (EditingCell)selectedCellContainer.cell;
        	editingCell.setInEditingMode(true);
        }
        
        event.stopPropagation();
	}
	
}
