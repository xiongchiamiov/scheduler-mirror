package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.ArrayList;

import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.view.client.ListDataProvider;

public interface TableBuilder<T> {

	public ArrayList<ColumnObject<T>> getColumns(
			ListDataProvider<T> dataProvider, ListHandler<T> sortHandler);

	public String getLabel(T object);

	public T newObject();

	public void save(ArrayList<T> list);
}
