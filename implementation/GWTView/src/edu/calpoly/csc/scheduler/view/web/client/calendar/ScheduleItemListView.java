package edu.calpoly.csc.scheduler.view.web.client.calendar;

import java.util.List;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

public class ScheduleItemListView extends SimplePanel {

	private List<ScheduleItemGWT> mModel;
	
	ScheduleItemListView(List<ScheduleItemGWT> items) {
		mModel = items;
	}
	
	public List<ScheduleItemGWT> getItems() { return mModel; }
	
	public void setItems(List<ScheduleItemGWT> items) { mModel = items; }
	
	public void toggle(boolean hidden) {
		Element container = DOM.getElementById("ScheduleListContainer");
		if (hidden)
			DOM.setStyleAttribute(container, "display", "none");
		else
			DOM.setStyleAttribute(container, "display", "block");
	}
	
	public void drawList() {
		StringBuilder builder = new StringBuilder();
		builder.append("<style type=\"text/css\">"+
				"#ScheduleListContainer {position:absolute;top:116px;bottom:33px;left:0px;width:200px;border-right:1px solid #000000;overflow:auto;}"+
				"#ScheduleList {background-color:#000000;border:none;cellspacing:0px;border-spacing:0px;width:100%;}"+
				"#ScheduleList tr {height:20px;}"+
				"#ScheduleList td {background-color:#FFFFFF;border-top:1px solid #d1dfdf;text-align:center;padding:0px;margin:0px;}"+
				"#ScheduleList td#ScheduleListHeader {position:fixed;top:116px;width:200px;height:20px;background-color:#edf2f2;font-weight:bold;border-bottom:1px solid #000000;padding:4px;}"+
				"#ScheduleList td .ScheduleListItem {background-color:#DFF0CF;margin:1px;text-align:center;padding-top:4px;padding-bottom:4px;height:100%;width:100%;border:none;}"+
				"</style>");
		builder.append("<div id=\"ScheduleListContainer\"><table id=\"ScheduleList\">");
		
		builder.append("<tr><td id=\"ScheduleListHeader\">Available Courses</td></tr>");
		
		for (ScheduleItemGWT item : new DummySchedule()) {
			builder.append("<tr>");
			builder.append("<td><div class=\"ScheduleListItem\">"+item.getCourseString()+"</div></td>");
		}
		
		builder.append("</table></div>");
		
		setHTML(builder.toString());
	}
	
	private void setHTML(String html) {
		clear();
		add(new HTML(html));
	}
}
