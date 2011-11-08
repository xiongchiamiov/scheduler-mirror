package edu.calpoly.csc.scheduler.view.web.client.table;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.view.client.ListDataProvider;

public interface NewObjPanel<T> {

	public Grid getGrid();
	
	public T getObject(ListDataProvider<T> dataProvider);
	
	public String getError();
	
	public void focus();
}
