package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.ArrayList;

import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;

public interface TableBuilder<T> {

	public ArrayList<ColumnObject<T>> getColumns(ListHandler<T> sortHandler);
	
	public String getLabel(T object);
	
	public T newObject();
	
	public void save(ArrayList<T> list);
}
