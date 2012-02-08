package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.ArrayList;
import java.util.Map;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.IViewContents;
import edu.calpoly.csc.scheduler.view.web.client.ViewFrame;
import edu.calpoly.csc.scheduler.view.web.shared.UserDataGWT;

public class ScheduleTrashView extends VerticalPanel implements IViewContents {
   private final GreetingServiceAsync service;
   
   private final MenuBar menuBar;
   
   private final String username;
   private String newDocName;
   private ArrayList<String> scheduleNames;
   private ListBox listBox;
   
   private VerticalPanel vdocholder;
   
   private ViewFrame myFrame;

   Map<String, UserDataGWT> availableSchedulesByName;
   
   public ScheduleTrashView(final GreetingServiceAsync service, final MenuBar menuBar, final String username) {
      this.service = service;
      this.menuBar = menuBar;
      this.username = username;
      this.newDocName = "Untitled";
      this.scheduleNames = new ArrayList<String>();
      
      //Home panel
      this.addStyleName("homeView");
      
      this.setWidth("100%");
      HorizontalPanel toprow = new HorizontalPanel();
      toprow.setWidth("100%");
      toprow.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
      toprow.add(new HTMLPanel("<h3>Not yet implemented</h3>"));
      this.add(toprow);
   }
   
   @Override
   public void afterPush(ViewFrame frame) {
      this.myFrame = frame;
   }
   @Override
   public void beforePop() {
   }
   
   @Override
   public boolean canPop() { return true; }
   
   @Override
   public void beforeViewPushedAboveMe() { }
   
   @Override
   public void afterViewPoppedFromAboveMe() { }
   
   @Override
   public Widget getContents() { return this; }
}
