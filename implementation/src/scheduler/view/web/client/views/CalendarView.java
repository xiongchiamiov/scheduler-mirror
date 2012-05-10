package scheduler.view.web.client.views;

import scheduler.view.web.client.CachedOpenWorkingCopyDocument;
import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.client.UnsavedDocumentStrategy;
import scheduler.view.web.client.calendar.ScheduleEditWidget;
import scheduler.view.web.shared.DocumentGWT;

import com.google.gwt.user.client.ui.VerticalPanel;

public class CalendarView extends VerticalPanel {
	public CalendarView(CachedOpenWorkingCopyDocument workingCopyDocument) {
		this.setWidth("100%");
		this.setHeight("100%");
		ScheduleEditWidget scheduleEditWidget = new ScheduleEditWidget(workingCopyDocument);
		this.add(scheduleEditWidget.getWidget());
	}
}
