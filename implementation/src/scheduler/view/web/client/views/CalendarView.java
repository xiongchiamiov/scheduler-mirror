package scheduler.view.web.client.views;

import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.client.UnsavedDocumentStrategy;
import scheduler.view.web.client.calendar.ScheduleEditWidget;
import scheduler.view.web.shared.DocumentGWT;

import com.google.gwt.user.client.ui.VerticalPanel;

public class CalendarView extends VerticalPanel {
	public CalendarView(GreetingServiceAsync service, DocumentGWT document, UnsavedDocumentStrategy unsavedDocumentStrategy) {
		this.setWidth("100%");
		this.setHeight("100%");
		ScheduleEditWidget scheduleEditWidget = new ScheduleEditWidget(service, document);
		this.add(scheduleEditWidget.getWidget());
	}
}
