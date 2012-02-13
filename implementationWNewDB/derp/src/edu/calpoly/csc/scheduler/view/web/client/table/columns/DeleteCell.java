package edu.calpoly.csc.scheduler.view.web.client.table.columns;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;

class DeleteCell extends OsmTable.Cell {
	public interface DeleteStrategy {
		void delete();
		boolean confirmHandler();
	}
	
	DeleteCell(final DeleteStrategy handler) {
		add(new Button("Delete", new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (handler.confirmHandler()) {
					handler.delete();
				}
				event.stopPropagation();
			}
		}));
	}
}