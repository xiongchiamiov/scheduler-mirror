package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;


public class AddRemoveTable<ObjectType extends Comparable<ObjectType>> extends OsmTable<ObjectType> {
	Collection<Row> rowsToRemove = new LinkedList<Row>();
	Collection<Row> editedRows = new LinkedList<Row>();
	LinkedHashMap<ObjectType, ObjectType> historyByObject = new LinkedHashMap<ObjectType, ObjectType>();
	
	public AddRemoveTable(Factory<ObjectType> factory) {
		super(factory);

		addColumn(new ButtonColumn<ObjectType>("Delete", "4em", "X",
				new ButtonColumn.ClickCallback<ObjectType>() {
					public void buttonClickedForObject(ObjectType object) {
						toggleObjectMarkedForRemoval(object);
					}
				}));
	}
	
	void toggleObjectMarkedForRemoval(ObjectType object) {
		Row row = rows.get(object);
		
		if (rowsToRemove.contains(row))
			rowsToRemove.remove(row);
		else
			rowsToRemove.add(row);
		
		colorRows();
	}
	
	@Override
	public Row addRowAndDontColor(ObjectType object) {
		Row row = super.addRowAndDontColor(object);
		historyByObject.put(object, factory.createHistoryFor(object));
		assert(object.compareTo(historyByObject.get(object)) == 0);
		return row;
	}
	
	@Override
	protected void objectChanged(ObjectType object) {
		super.objectChanged(object);
		
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
	
	@Override
	protected void colorRows() {
		super.colorRows();
		
		for (Row row : rows.values()) {
			row.row.removeClassName("edited");
			row.row.removeClassName("markedForRemoval");
		}
		
		for (Row row : editedRows)
			row.row.addClassName("edited");
		
		for (Row row : rowsToRemove)
			row.row.addClassName("markedForRemoval");
	}
}
