package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

import edu.calpoly.csc.scheduler.view.web.client.schedule.EditScheduleItemWidget;
import edu.calpoly.csc.scheduler.view.web.client.schedule.ScheduleEditWidget;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

/**
 * This class encapsulates both the view and model for the schedule table that allows 
 * drag and drop editing. It also contains the logic to layout all of the schedule
 * items on the table. Not the best design, but it was relatively easy and quick to implement
 * 
 * @author Matt Schirle
 */
public class ScheduleEditTable extends SimplePanel {
	
	/**
	 * Models one row in the table
	 */
	private static class ScheduleEditTableRowModel extends Vector<ScheduleItemGWT> {
		
		@Override
		public ScheduleItemGWT get(int index) {
			if (index >= size())
				return null;
			return super.get(index);
		}
	}
	
	/**
	 * Models one day in the table. Each day has a ScheduleEditTableRowModel for each available time.
	 */
	private static class ScheduleEditTableDayModel extends Vector<ScheduleEditTableRowModel> {
		
		public ScheduleEditTableDayModel() {
			mWidth = 1;
			mOffset = 0;
			
			for(int rowNdx = 0; rowNdx < TIMES.length; rowNdx++)
				add(new ScheduleEditTableRowModel());
		}
		
		@Override 
		public void clear() {
			mWidth = 1;
			mOffset = 0;
			
			// Don't delete the rows, just clear each one
			for (int i = 0; i < size(); i++)
				get(i).clear();
		}
		
		/**
		 * @return The number of columns on this day 
		 */
		public int getWidth() { return mWidth; }
		
		/**
		 * @return The column this day starts on
		 */
		public int getOffset() { return mOffset; }
		
		public void setWidth(int width) { mWidth = width; }
		public void setOffset(int offset) { mOffset = offset; }
		
		private int mWidth;
		private int mOffset;
	}
	
	private static class ScheduleEditTableModel {
		
		public ScheduleEditTableModel() {
			mDays = new ScheduleEditTableDayModel[5];
			for (int dayNum = 0; dayNum < mDays.length; dayNum++)
				mDays[dayNum] = new ScheduleEditTableDayModel();
		}
		
		public void clear() {
			for (int dayNum = 0; dayNum < DAYS.length; dayNum++) {
				mDays[dayNum].clear();
			}
		}
		
		public int getDayNum(int colNum) {
			int dayNum = 0;
			for (; dayNum < DAYS.length; dayNum++) {
				ScheduleEditTableDayModel day = get(dayNum);
				if (colNum >= day.getOffset() && colNum < day.getOffset() + day.getWidth())
					return dayNum;
			}
			return -1;
		}
		
		public ScheduleEditTableDayModel get(int dayNum) {
			return mDays[dayNum];
		}
		
		private final ScheduleEditTableDayModel mDays[];
	}
	
	private Map<String, ScheduleItemGWT> mScheduleItems;
	private Map<String, ScheduleItemGWT> mFilteredScheduleItems;
	private ScheduleEditTableModel mModel;
	
	private final ScheduleEditWidget mScheduleController;
	
	private static final String TIMES[] = { "7:00am", "7:30am", "8:00am", "8:30am",
		"9:00am", "9:30am", "10:00am", "10:30am", "11:00am", "11:30am",
		"12:00pm", "12:30pm", "1:00pm", "1:30pm", "2:00pm", "2:30pm",
		"3:00pm", "3:30pm", "4:00pm", "4:30pm", "5:00pm", "5:30pm",
		"6:00pm", "6:30pm", "7:00pm", "7:30pm", "8:00pm", "8:30pm",
		"9:00pm", "9:30pm" };
	
	private static final String DAYS[] = { "Monday", "Tuesday", "Wednesday", 
		"Thursday", "Friday" };
	
	public ScheduleEditTable(ScheduleEditWidget scheduleController) {
		mModel = new ScheduleEditTableModel();
		mScheduleController = scheduleController;
	}
	
	/**
	 * Takes the table model and draws a raw HTML table
	 */
	public void drawTable() {
		clear();
		
		mModel = buildTableModel();
		defineTableCallbacks();
		
		System.out.println("rendering");
		
		final StringBuilder builder = new StringBuilder();
		builder.append("<style type=\"text/css\">"+
			"#editTableContainer {position:absolute;top:237px;left:200px;right:0px;bottom:0px;overflow:auto;border:1px solid #000000;background-color:#FFFFFF;}"+
			"#editTable {border-spacing:0px;cellspacing:0px;}"+
			"#editTable td {padding:4px;border-bottom:1px solid #DDDDDD;}"+
			"#editTable td.item {background-color:#DFF0CF;text-align:center;border:1px solid #FFFFFF;}"+
			"#editTable td.dayHeader {border-left:1px solid #000000;border-bottom:1px solid #000000;font-weight:bold;text-align:center;}"+
			"#editTable td#topCorner {border-bottom:1px solid #000000;}"+
			"#editTable td.daySpacer {border-left:1px solid #000000;padding:0px;margin:0px;width:0px;}"+
			"</style>");
		builder.append("<div id=\"editTableContainer\">");
		builder.append("<table id=\"editTable\"><tr><td id=\"topCorner\"></td>");
		
		// Add day headers
		for (int dayNum = 0; dayNum < DAYS.length; dayNum++) {
			final int colspan = mModel.get(dayNum).getWidth() + 1; // +1 for day spacer cells (see loop below)
			builder.append("<td colspan="+colspan+" class=\"dayHeader\">"+DAYS[dayNum]+"</td>");
		}
		builder.append("</tr>");
		
		// Fill in table
		for (int rowNum = 0; rowNum < TIMES.length; rowNum++) {
			builder.append("<tr><td><b>"+TIMES[rowNum]+"</b></td>");
			
			for (int dayNum = 0; dayNum < DAYS.length; dayNum++) {
				builder.append("<td class=\"daySpacer\"></td>");
				
				final ScheduleEditTableDayModel day = mModel.get(dayNum);
				
				for (int colNum = 0; colNum < day.getWidth(); colNum++) {
					final ScheduleItemGWT item = day.get(rowNum).get(colNum);
					
					if (item == null) {
						builder.append("<td></td>");
					}
					else if (rowNum == getStartRow(item)) {
						final int rowspan = getEndRow(item) - getStartRow(item) + 1; 
						builder.append("<td rowspan="+rowspan+" class=\"item\" "+
								"ondblclick=\"doubleClick("+rowNum+","+(colNum + day.getOffset())+")\">"+
								item.getCourseString()+"</td>");
					}
				}
			}
			
			builder.append("</tr>");
		}
		builder.append("</table>");
		builder.append("</div>");
		
		//registerNatives();
		
		add(new HTML(builder.toString()));
	}
	
	/**
	 * Lays out a table model from the list of filtered scheduled items
	 */
	private ScheduleEditTableModel buildTableModel() {
		// TODO link schedule item between its' days in model so you can 
		// highlight them item on all of it's days when it's selected on the UI
		
		ScheduleEditTableModel model = new ScheduleEditTableModel();
		
		for (ScheduleItemGWT item : mFilteredScheduleItems.values()) {
			for (Integer dayNum : item.getDayNums()) {
				ScheduleEditTableDayModel day = model.get(dayNum);
				
				// Find the leftmost column that is open on all rows that this item needs to occupy
				int colNdx = 0;
				for (; colNdx < day.getWidth(); colNdx++) {
					boolean occupied = false;
					
					for (int rowNdx = getStartRow(item); rowNdx <= getEndRow(item); rowNdx++) {
						ScheduleEditTableRowModel row = day.get(rowNdx);
						
						if (row.get(colNdx) != null) {
							occupied = true;
							break;
						}
					}
					
					if (!occupied)
						break;
				}
				
				// Check it we need to increase the width of this day
				if (colNdx >= day.getWidth()) {
					day.setWidth(colNdx + 1);
				}
				
				int startRow = getStartRow(item);
				int endRow = getEndRow(item);
				if (startRow > endRow)
					throw new RuntimeException("lol");
				
				// Add the item to the column we picked on all of the item's rows
				for (int rowNdx = startRow; rowNdx <= endRow; rowNdx++) {
					ScheduleEditTableRowModel row = day.get(rowNdx);
					
					if (colNdx >= row.size()) {
						row.setSize(colNdx + 1);
					}
					row.set(colNdx, item);
				}
			}
		}
		
		// Calculate all of the day offsets (the column each day starts on)
		for (int dayNum = 1; dayNum < DAYS.length; dayNum++) {
			int prevOffset = model.get(dayNum - 1).getOffset();
			int prevWidth = model.get(dayNum - 1).getWidth();
			model.get(dayNum).setOffset(prevOffset + prevWidth);
		}
		
		return model;
	}
	
	/**
	 * Called when the user double clicks an item in the table
	 */
	public void doubleClick(int row, int col) {
		System.out.println("row " + row + " col " + col + " double clicked");
		
		final int dayNum = mModel.getDayNum(col);
		final ScheduleItemGWT item = mModel.get(dayNum).get(row).get(col);   
		
		final EditScheduleItemWidget editDlg = new EditScheduleItemWidget(item);
		editDlg.center();
	}
	
	/**
	 * Used to register callback methods for access via handwritten javascript
	 */
	public native void defineTableCallbacks() /*-{
		var scheduleTable = this;
		$wnd.doubleClick = function(row, col) {
			return scheduleTable.@edu.calpoly.csc.scheduler.view.web.client.views.ScheduleEditTable::doubleClick(II)(row, col);
		}
    }-*/;
	
	private void applyFilters() {
		// TODO implement filtering
		mFilteredScheduleItems = mScheduleItems;
	}
	
	private int getStartRow(ScheduleItemGWT item) {
		return item.getStartTimeHour() * 2 + (item.getStartTimeMin() < 30 ? 0 : 1);
	}
	
	private int getEndRow(ScheduleItemGWT item) {
		return item.getEndTimeHour() * 2 + (item.getEndTimeMin() < 30 ? 0 : 1) - 1;
	}
	
	public Map<String, ScheduleItemGWT> getScheduleItems() {
		return null;
	}
	
	public void setScheduleItems(Map<String, ScheduleItemGWT> items) {
		mScheduleItems = items;
		applyFilters();
	}
	
	public void addScheduleItem(ScheduleItemGWT item) {
		
	}
	
	public void setDayFilter(List<Integer> days) {
		
	}
	
	public void setTimeFilter(List<Integer> times) {
		
	}
	
	public void setStringFilter(String str) {
		
	}
	
	public void setRoomFilter(List<String> rooms) {
		
	}
	
	public void setInstructorFilter(List<String> instructors) {
		
	}
	
	public void setCourseFilter(List<String> courses) {
		
	}
}
