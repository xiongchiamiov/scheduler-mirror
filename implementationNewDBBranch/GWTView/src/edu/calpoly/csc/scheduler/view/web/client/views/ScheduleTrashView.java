package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.HTMLUtilities;
import edu.calpoly.csc.scheduler.view.web.client.IViewContents;
import edu.calpoly.csc.scheduler.view.web.client.ViewFrame;
import edu.calpoly.csc.scheduler.view.web.client.views.AdminScheduleNavView.OtherFilesStrategy;
import edu.calpoly.csc.scheduler.view.web.shared.DocumentGWT;
import edu.calpoly.csc.scheduler.view.web.shared.UserDataGWT;

public class ScheduleTrashView extends VerticalPanel implements IViewContents
{
   protected final GreetingServiceAsync      service;
   public static final String                TRASH_PREFIX = "~trashed~";

   private final MenuBar                     menuBar;

   private final String                      username;
   private ArrayList<String>                 scheduleNames;
   private OtherFilesStrategy                filesStrategy;

   private VerticalPanel                     vdocholder;

   private ViewFrame                         myFrame;

   Collection<DocumentGWT>                   availableDocuments;
   private ArrayList<DocumentGWT>            checkedDocuments;
   private HashMap<Integer, HorizontalPanel> documentPanels;
   private boolean                           colorNextRow = false;

   public ScheduleTrashView(final GreetingServiceAsync service, final SimplePanel scheduleNameContainer, final MenuBar menuBar, final String username,
         final OtherFilesStrategy filesStrategy)
   {
      this.filesStrategy = filesStrategy;
      this.service = service;
      this.menuBar = menuBar;
      this.username = username;
      this.scheduleNames = new ArrayList<String>();
      this.addStyleName("iViewPadding");
      this.checkedDocuments = new ArrayList<DocumentGWT>();
      this.documentPanels = new HashMap<Integer, HorizontalPanel>();

      menuBar.clearItems();
      // Put tabs in menu bar
      MenuItem homeTab = new MenuItem("Home", true, new Command()
      {
         @Override
         public void execute()
         {
            if (myFrame.canPopViewsAboveMe())
            {
               myFrame.popFramesAboveMe();
               myFrame.frameViewAndPushAboveMe(new SelectScheduleView(service, scheduleNameContainer, menuBar, username));
            }
         }
      });

      DOM.setElementAttribute(homeTab.getElement(), "id", "hometab");
      menuBar.addItem(homeTab);

      MenuItem trashTab = new MenuItem("Trash", true, new Command()
      {
         public void execute()
         {
            if (myFrame.canPopViewsAboveMe())
            {
               myFrame.popFramesAboveMe();
               myFrame.frameViewAndPushAboveMe(new ScheduleTrashView(service, scheduleNameContainer, menuBar, username, filesStrategy));
            }
         }
      });

      DOM.setElementAttribute(trashTab.getElement(), "id", "trashtab");
      menuBar.addItem(trashTab);

      // Home panel
      this.addStyleName("homeView");

      this.setWidth("100%");
      HorizontalPanel toprow = new HorizontalPanel();
      toprow.setWidth("100%");
      toprow.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
      toprow.add(new HTMLPanel("<h2>Trashed Documents:</h2>"));
      this.add(toprow);

      // Document selector
      this.setHorizontalAlignment(ALIGN_LEFT);
      ScrollPanel scroller = new ScrollPanel();
      this.add(scroller);
      vdocholder = new VerticalPanel();
      vdocholder.setWidth("100%");
      vdocholder.setStyleName("docTable");
      scroller.add(vdocholder);

      // UnTrash button
      this.setHorizontalAlignment(ALIGN_CENTER);
      Button untrashButton = new Button("Restore Selected Documents", new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            for (DocumentGWT doc : checkedDocuments)
            {
               // Set not trashed
               doc.setTrashed(false);
               service.updateDocument(doc, new AsyncCallback<Void>()
               {
                  @Override
                  public void onFailure(Throwable caught)
                  {
                  }

                  @Override
                  public void onSuccess(Void result)
                  {
                  }
               });
               vdocholder.remove(documentPanels.get(doc.getID()));
            }
         }
      });
      DOM.setElementAttribute(untrashButton.getElement(), "id", "untrashButton");
      this.add(untrashButton);
   }

   @Override
   public void afterPush(ViewFrame frame)
   {
      this.myFrame = frame;

      service.getAllDocuments(new AsyncCallback<Collection<DocumentGWT>>()
      {
         @Override
         public void onFailure(Throwable caught)
         {
            Window.alert("There was an error getting the schedules: " + caught.getMessage());
         }

         @Override
         public void onSuccess(Collection<DocumentGWT> result)
         {
            availableDocuments = result;

            vdocholder.clear();
            for (DocumentGWT doc : availableDocuments)
            {
               if (doc.isTrashed())
               {
                  addNewDocument(doc);
                  scheduleNames.add(doc.getName());
               }
            }

            doneAddingDocuments();
         }
      });
   }

   // For subclasses
   protected void doneAddingDocuments()
   {
   }

   private void addNewDocument(final DocumentGWT document)
   {
      HorizontalPanel doc = new HorizontalPanel();
      if (colorNextRow)
      {
         doc.setStyleName("quarterViewMenu");
         colorNextRow = false;
      }
      else
      {
         colorNextRow = true;
      }
      doc.setWidth("100%");
      doc.setHorizontalAlignment(ALIGN_LEFT);
      final CheckBox cb = new CheckBox();
      cb.addValueChangeHandler(new ValueChangeHandler<Boolean>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<Boolean> event)
         {
            if (cb.getValue())
            {
               // Enabled
               checkedDocuments.add(document);
            }
            else
            {
               // Disabled
               if (checkedDocuments.contains(document))
               {
                  checkedDocuments.remove(document);
               }
            }
         }
      });
      FocusPanel docname = new FocusPanel();
      docname.add(HTMLUtilities.createLink(document.getName(), "docLink", new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            String baseHref = Window.Location.getHref();
            if (Window.Location.getHref().contains("?userid="))
            {
               baseHref = Window.Location.getHref().substring(0, Window.Location.getHref().lastIndexOf('?'));
            }
            Window.open(baseHref + "?scheduleid=" + document.getScheduleID() + "&schedulename=" + document.getName()
                  + "&userid=" + username, "_new", null);
         }
      }));
      HorizontalPanel flow = new HorizontalPanel();
      flow.setVerticalAlignment(ALIGN_MIDDLE);
      cb.setStyleName("docPadding");
      docname.setStyleName("docPadding");
      flow.add(cb);
      flow.add(docname);
      doc.add(flow);
      vdocholder.add(doc);
      documentPanels.put(document.getID(), doc);
      vdocholder.add(doc);
   }

   @Override
   public void beforePop()
   {
   }

   @Override
   public boolean canPop()
   {
      return true;
   }

   @Override
   public void beforeViewPushedAboveMe()
   {
   }

   @Override
   public void afterViewPoppedFromAboveMe()
   {
   }

   @Override
   public Widget getContents()
   {
      return this;
   }

   protected void openLoadedSchedule(DocumentGWT doc)
   {
      System.out.println("openloadedschedule?");

      if (myFrame.canPopViewsAboveMe())
      {
         myFrame.popFramesAboveMe();
         myFrame.frameViewAndPushAboveMe(new AdminScheduleNavView(service, filesStrategy, menuBar, username, doc));

      }
   }

}
