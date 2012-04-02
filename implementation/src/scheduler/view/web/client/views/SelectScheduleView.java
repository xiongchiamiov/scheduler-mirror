package scheduler.view.web.client.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import scheduler.view.web.client.DocumentTabOpener;
import scheduler.view.web.client.views.resources.instructors.InstructorsHomeView;

import scheduler.view.web.client.DocumentTabOpener;
import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.client.HTMLUtilities;
import scheduler.view.web.client.IViewContents;
import scheduler.view.web.client.NewScheduleCreator;
import scheduler.view.web.client.ViewFrame;
import scheduler.view.web.shared.DocumentGWT;

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

public class SelectScheduleView extends VerticalPanel implements IViewContents
{
   protected final GreetingServiceAsync      service;

   protected final String                      username;
   private ArrayList<String>                 scheduleNames;

   private VerticalPanel                     vdocholder;
   protected ViewFrame                         myFrame;

   HashMap<Integer, DocumentGWT>                   allAvailableOriginalDocumentsByID;
   private ArrayList<DocumentGWT>            checkedDocuments;
   private HashMap<Integer, HorizontalPanel> documentPanels;
   private boolean                           colorNextRow = false;
   String currentDocName;
   
   public SelectScheduleView(final GreetingServiceAsync service, final String username)
   {
      this.service = service;
      this.username = username;
      this.scheduleNames = new ArrayList<String>();
      this.addStyleName("iViewPadding");
      this.checkedDocuments = new ArrayList<DocumentGWT>();
      this.documentPanels = new HashMap<Integer, HorizontalPanel>();

      Button homeTab = new Button("Home", new ClickHandler() {
			public void onClick(ClickEvent event) {
            if (myFrame.canPopViewsAboveMe())
            {
               myFrame.popFramesAboveMe();
               myFrame.frameViewAndPushAboveMe(new SelectScheduleView(service, username));
            }
			}
		});
      
      // Put tabs in menu bar
      
      DOM.setElementAttribute(homeTab.getElement(), "id", "hometab");
      this.add(homeTab);

      Button trashTab = new Button("Trash", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
            if (myFrame.canPopViewsAboveMe())
            {
               myFrame.popFramesAboveMe();
               myFrame.frameViewAndPushAboveMe(new ScheduleTrashView(service, username));
            }
			}
		});
      
      DOM.setElementAttribute(trashTab.getElement(), "id", "trashtab");
      this.add(trashTab);

      // Home panel
      this.addStyleName("homeView");

      this.setWidth("100%");
      HorizontalPanel toprow = new HorizontalPanel();
      toprow.setWidth("100%");
      toprow.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
      toprow.add(new HTMLPanel("<h2>My Scheduling Documents:</h2>"));

      // Buttons to top right
      toprow.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
      Button newSchedButton = new Button("Create New Schedule", new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            createNewSchedule();
         }
      });
      DOM.setElementAttribute(newSchedButton.getElement(), "id", "newScheduleButton");

      Button importButton = new Button("Import", new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            Window.alert("This feature is not yet implemented");
         }
      });
      DOM.setElementAttribute(importButton.getElement(), "id", "importButton");
      HorizontalPanel topright = new HorizontalPanel();
      topright.setHorizontalAlignment(ALIGN_CENTER);
      topright.setSpacing(5);
      topright.setStyleName("buttonPadding");
      topright.add(newSchedButton);
      topright.add(importButton);
      toprow.add(topright);
      // FlowPanel flow = new FlowPanel();
      // flow.add(newSchedButton);
      // flow.add(importButton);
      // toprow.add(flow);
      this.add(toprow);

      // Document selector
      this.setHorizontalAlignment(ALIGN_LEFT);
      ScrollPanel scroller = new ScrollPanel();
      this.add(scroller);
      vdocholder = new VerticalPanel();
      vdocholder.setWidth("100%");
      vdocholder.setStyleName("docTable");
      scroller.add(vdocholder);

      // Trash button
      this.setHorizontalAlignment(ALIGN_CENTER);
      Button trashButton = new Button("Delete Selected Documents", new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            for (DocumentGWT doc : checkedDocuments)
            {
               // Set trashed
               doc.setTrashed(true);
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
      DOM.setElementAttribute(trashButton.getElement(), "id", "trashButton");

      // Merge button
      this.setHorizontalAlignment(ALIGN_CENTER);
      Button mergeButton = new Button("Merge Selected Documents", new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            Window.alert("This feature is not yet implemented");
         }
      });
      
      Button tempButton = new Button("Temporary Instructors' Home View", new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
        	 com.smartgwt.client.widgets.Window instructorWindow = new com.smartgwt.client.widgets.Window();
        	 
        	 InstructorsHomeView homeView = new InstructorsHomeView();
        	 
        	 instructorWindow.addItem(homeView);
        	 
        	 homeView.setHorizontalAlignment(ALIGN_CENTER);
        	 
        	 instructorWindow.setSize("500px", "500px");
        	 
        	 instructorWindow.show();
         }
      });
      DOM.setElementAttribute(mergeButton.getElement(), "id", "mergeButton");
      DOM.setElementAttribute(tempButton.getElement(), "id", "tempButton");
      HorizontalPanel botflow = new HorizontalPanel();
      botflow.setHorizontalAlignment(ALIGN_CENTER);
      botflow.setSpacing(5);
      botflow.setStyleName("buttonPadding");
      botflow.add(trashButton);
      botflow.add(mergeButton);
      botflow.add(tempButton);
      this.add(botflow);
   }

   @Override
   public void afterPush(ViewFrame frame)
   {
      this.myFrame = frame;

      service.getAllOriginalDocuments(new AsyncCallback<Collection<DocumentGWT>>()
      {
         @Override
         public void onFailure(Throwable caught)
         {
            Window.alert("There was an error getting the schedules: " + caught.getMessage());
         }

         @Override
         public void onSuccess(Collection<DocumentGWT> result)
         {
        	 allAvailableOriginalDocumentsByID = new HashMap<Integer, DocumentGWT>();

            vdocholder.clear();
            for (DocumentGWT doc : result)
            {
            	assert(doc.getID() != null);
            	
            		allAvailableOriginalDocumentsByID.put(doc.getID(), doc);
            	
               if (!doc.isTrashed())
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
      if(colorNextRow)
      {
         doc.setStyleName("alternatingRowColor");
         colorNextRow = false;
      }
      else
      {
         colorNextRow = true;
      }
      doc.setWidth("100%");
      doc.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
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
         	TabOpener.openDocInNewTab(username, document);
         }
      }));
      DOM.setElementAttribute(docname.getElement(), "id", "openDocument" + document.getName());
      HorizontalPanel flow = new HorizontalPanel();
      flow.setVerticalAlignment(ALIGN_MIDDLE);
      cb.setStyleName("docPadding");
      docname.setStyleName("docPadding");
      flow.add(cb);
      flow.add(docname);
      doc.add(flow);
      vdocholder.add(doc);
      documentPanels.put(document.getID(), doc);
   }

   @Override
   public void beforePop()
   {
   }

   interface NameScheduleCallback
   {
      void namedSchedule(String name);
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

   private void createNewSchedule()
   {
   	NewScheduleCreator.createNewSchedule(service, username);
   }
}
