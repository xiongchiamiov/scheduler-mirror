package edu.calpoly.csc.scheduler.view.web.client.table.columns;

import com.google.gwt.user.client.Window;

import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.Cell;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.IRowForColumn;
import edu.calpoly.csc.scheduler.view.web.shared.Identified;

public class DeleteColumn<ObjectType extends Identified> implements OsmTable.IColumn<ObjectType> {
	public interface DeleteObserver<ObjectType extends Identified> {
		void afterDelete(ObjectType object);
	}
	
	DeleteObserver<ObjectType> observer;
	public DeleteColumn(DeleteObserver<ObjectType> observer) {
		this.observer = observer;
	}
	
	public Cell createCell(final IRowForColumn<ObjectType> row) {
		return new DeleteCell(new DeleteCell.DeleteStrategy() {
			public void delete() {
				row.delete();
				observer.afterDelete(row.getObject());
			}
			@Override
			public boolean confirmHandler() {
				return Window.confirm("Are you sure you want to delete " + row.getObject().toString() + "?");
			}
		});
	}
}
