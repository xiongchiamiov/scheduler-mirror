package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.HTMLUtilities;
import edu.calpoly.csc.scheduler.view.web.client.IViewContents;
import edu.calpoly.csc.scheduler.view.web.client.Import;
import edu.calpoly.csc.scheduler.view.web.client.ViewFrame;
import edu.calpoly.csc.scheduler.view.web.client.views.AdminScheduleNavView.OtherFilesStrategy;
import edu.calpoly.csc.scheduler.view.web.shared.DocumentGWT;

public class SelectScheduleView extends VerticalPanel implements IViewContents, OtherFilesStrategy
{
   protected final GreetingServiceAsync      service;

   private final MenuBar                     menuBar;

   private final String                      username;
   private String                            newDocName;
   private ArrayList<String>                 scheduleNames;

   private VerticalPanel                     vdocholder;

   private ViewFrame                         myFrame;

   Collection<DocumentGWT>                   availableDocuments;
   private ArrayList<DocumentGWT>            checkedDocuments;
   private HashMap<Integer, HorizontalPanel> documentPanels;
   private boolean                           colorNextRow = false;

   public SelectScheduleView(final GreetingServiceAsync service, final MenuBar menuBar, final String username)
   {
      this.service = service;
      this.menuBar = menuBar;
      this.username = username;
      this.newDocName = "Untitled";
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
               myFrame.frameViewAndPushAboveMe(new SelectScheduleView(service, menuBar, username));
            }
         }
      });

      DOM.setElementAttribute(homeTab.getElement(), "id", "hometab");
      menuBar.addItem(homeTab);

      final OtherFilesStrategy filesStrategy = this;
      MenuItem trashTab = new MenuItem("Trash", true, new Command()
      {
         public void execute()
         {
            if (myFrame.canPopViewsAboveMe())
            {
               myFrame.popFramesAboveMe();
               myFrame.frameViewAndPushAboveMe(new ScheduleTrashView(service, menuBar, username, filesStrategy));
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
      vdocholder.setBorderWidth(1);
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
      DOM.setElementAttribute(mergeButton.getElement(), "id", "mergeButton");
      HorizontalPanel botflow = new HorizontalPanel();
      botflow.setHorizontalAlignment(ALIGN_CENTER);
      botflow.setSpacing(5);
      botflow.setStyleName("buttonPadding");
      botflow.add(trashButton);
      botflow.add(mergeButton);
      this.add(botflow);
   }

   @Override
   public void afterPush(ViewFrame frame)
   {
      this.myFrame = frame;

      service.getAllOriginalDocumentsByID(new AsyncCallback<Collection<DocumentGWT>>()
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
         doc.setStyleName("quarterViewMenu");
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
      docname.add(HTMLUtilities.createLink(document.getName(), "inAppLink", new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            openDocInNewTab(document.getName(), document.getScheduleID());
         }
      }));
      HorizontalPanel flow = new HorizontalPanel();
      flow.add(cb);
      flow.add(docname);
      doc.add(flow);
      vdocholder.add(doc);
      documentPanels.put(document.getID(), doc);
   }

   private void openDocInNewTab(String name, Integer scheduleid)
   {
      String baseHref = Window.Location.getHref();
      if (Window.Location.getHref().contains("?userid="))
      {
         baseHref = Window.Location.getHref().substring(0, Window.Location.getHref().lastIndexOf('?'));
      }
      Window.open(baseHref + "?scheduleid=" + scheduleid + "&schedulename=" + name + "&userid=" + username, "_new",
            null);
      // openInNewWindow(Window.Location.getHref(), scheduleid);
      // selectSchedule(Integer.parseInt(scheduleid), name);
   }

   @Override
   public void beforePop()
   {
   }

   /**
    * Private method for selecting a previously saved schedule from the database
    * given the schedule ID and the schedule name.
    * 
    * @param scheduleID
    *           ID of the schedule to open.
    * @param scheduleName
    *           Name of the schedule to open.
    */
   private void selectSchedule(DocumentGWT document)
   {
      final LoadingPopup popup = new LoadingPopup();
      popup.show();

      // System.out.println("SelectScheduleView.selectSchedule(" + scheduleID +
      // ", " + scheduleName + ")");

      openDocument(document);
   }

   interface NameScheduleCallback
   {
      void namedSchedule(String name);
   }

   /**
    * Displays a popup for specifying a new schedule.
    * 
    * @param buttonLabel
    * @param callback
    */
   private void displayNewSchedPopup(String buttonLabel, final NameScheduleCallback callback)
   {
      final TextBox tb = new TextBox();
      final DialogBox db = new DialogBox(false);
      FlowPanel fp = new FlowPanel();
      final Button butt = new Button(buttonLabel, new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            db.hide();

            final String scheduleName = tb.getText();

            callback.namedSchedule(scheduleName);
         }
      });
      final Button cancelButton = new Button("Cancel", new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            db.hide();
         }
      });

      tb.addKeyPressHandler(new KeyPressHandler()
      {
         @Override
         public void onKeyPress(KeyPressEvent event)
         {
            if (event.getCharCode() == KeyCodes.KEY_ENTER) butt.click();
         }
      });

      db.setText("Name Schedule");
      fp.add(new HTML("<center>Specify a new schedule name.</center>"));
      fp.add(tb);
      fp.add(butt);
      fp.add(cancelButton);

      db.setWidget(fp);
      db.center();
   }

   /**
    * Displays a popup for selecting and opening a previously saved schedule.
    */
   private void displayOpenPopup()
   {
      final DialogBox db = new DialogBox();
      FlowPanel fp = new FlowPanel();
      final ListBox listBox = new ListBox();
      final Collection<DocumentGWT> schedules = new LinkedList<DocumentGWT>();

      service.getAllOriginalDocumentsByID(new AsyncCallback<Collection<DocumentGWT>>()
      {

         @Override
         public void onSuccess(Collection<DocumentGWT> result)
         {
            schedules.addAll(result);
            for (DocumentGWT doc : result)
               listBox.addItem(doc.getName(), doc.getID().toString());
         }

         @Override
         public void onFailure(Throwable caught)
         {
            Window.alert("Failed to open schedule in: " + caught.getMessage());

         }
      });

      listBox.setVisibleItemCount(5);
      fp.add(listBox);

      fp.add(new HTML("<br />"));

      fp.add(new Button("Open", new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            int index = listBox.getSelectedIndex();

            DocumentGWT document = null;
            for (DocumentGWT doc : availableDocuments)
               if (doc.getID().toString().equals(listBox.getValue(index))) document = doc;
            assert (document != null);
            selectSchedule(document);
            db.hide();
         }
      }));

      fp.add(new Button("Cancel", new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            db.hide();
         }
      }));

      db.add(fp);

      db.setText("Open a Schedule");
      db.center();
      db.show();
   }

   // /**
   // * Displays a popup to save the schedule under a different name.
   // */
   // private void displaySaveAsPopup()
   // {
   // final ListBox saveAsListBox = new ListBox();
   // final ArrayList<String> schedNames = new ArrayList<String>();
   // final TextBox tb = new TextBox();
   // final DialogBox db = new DialogBox();
   // FlowPanel fp = new FlowPanel();
   //
   // final Button saveButton = new Button("Save", new ClickHandler()
   // {
   // @Override
   // public void onClick(ClickEvent event)
   // {
   // db.hide();
   //
   // final String scheduleName = tb.getText();
   // if (scheduleName.isEmpty()) return;
   //
   // boolean allowOverwrite = false;
   // if (schedNames.contains(scheduleName))
   // {
   // if (Window.confirm("The schedule \"" + scheduleName
   // + "\" already exists.  Are you sure you want to replace it?"))
   // allowOverwrite = true;
   // else return;
   // }
   //
   // service.saveWorkingCopyToNewOriginalDocument(document.getID(),
   // scheduleName, new AsyncCallback<Integer>()
   // {
   // public void onFailure(Throwable caught)
   // {
   // Window.alert("Failed to save schedule: " + caught.getMessage());
   // }
   //
   // public void onSuccess(Integer newScheduleID)
   // {
   // openLoadedSchedule(newScheduleID, scheduleName);
   // }
   // });
   // }
   // });
   //
   // final Button cancelButton = new Button("Cancel", new ClickHandler()
   // {
   // @Override
   // public void onClick(ClickEvent event)
   // {
   // db.hide();
   // }
   // });
   //
   // tb.addKeyPressHandler(new KeyPressHandler()
   // {
   // @Override
   // public void onKeyPress(KeyPressEvent event)
   // {
   // if (event.getCharCode() == KeyCodes.KEY_ENTER) saveButton.click();
   // }
   // });
   //
   // service.getScheduleNames(new AsyncCallback<Map<String, UserDataGWT>>()
   // {
   // @Override
   // public void onFailure(Throwable caught)
   // {
   // Window.alert("Failed to open schedule in: " + caught.getMessage());
   // }
   //
   // @Override
   // public void onSuccess(Map<String, UserDataGWT> result)
   // {
   // for (String name : result.keySet())
   // {
   // saveAsListBox.addItem(name);
   // schedNames.add(name);
   // }
   // }
   // });
   //
   // db.setText("Name Schedule");
   // fp.add(new
   // HTML("<center>Specify a name to save the schedule as...</center>"));
   // saveAsListBox.addClickHandler(new ClickHandler()
   // {
   // @Override
   // public void onClick(ClickEvent event)
   // {
   // tb.setText(saveAsListBox.getValue(saveAsListBox.getSelectedIndex()));
   // }
   // });
   // saveAsListBox.setVisibleItemCount(5);
   // fp.add(saveAsListBox);
   // fp.add(tb);
   // fp.add(saveButton);
   // fp.add(cancelButton);
   //
   // db.setWidget(fp);
   // db.center();
   // db.show();
   // }

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

   @Override
   public void fileNewPressed()
   {
      createNewSchedule();
   }

   @Override
   public void fileOpenPressed()
   {
      String baseHref = Window.Location.getHref().substring(0, Window.Location.getHref().lastIndexOf('?'));
      Window.open(baseHref + "?userid=" + username, "_new", null);
   }

   @Override
   public void fileImportPressed()
   {
      Import.showImport();
   }

   @Override
   public void fileMergePressed()
   {

      final ArrayList<CheckBox> checkBoxList = new ArrayList<CheckBox>();
      final DialogBox db = new DialogBox();
      final VerticalPanel vp = new VerticalPanel();
      final VerticalPanel checkBoxPanel = new VerticalPanel();
      FlowPanel fp = new FlowPanel();

      final Button mergeButton = new Button("Merge", new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            int checkCount = 0;

            for (CheckBox cb : checkBoxList)
            {
               if (cb.getValue()) checkCount++;
            }

            if (checkCount >= 2)
            {
               // TODO - Add merge call here when functionality is implemented
               db.hide();
            }
            else
            {
               Window.alert("Please select 2 or more schedules to merge.");
            }
         }
      });

      final Button cancelButton = new Button("Cancel", new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            db.hide();
         }
      });

      service.getAllOriginalDocumentsByID(new AsyncCallback<Collection<DocumentGWT>>()
      {

         @Override
         public void onSuccess(Collection<DocumentGWT> result)
         {
            for (DocumentGWT doc : result)
            {
               CheckBox checkBox = new CheckBox(doc.getName());
               checkBoxList.add(checkBox);
               checkBoxPanel.add(checkBox);
            }
         }

         @Override
         public void onFailure(Throwable caught)
         {
            Window.alert("Failed to retrieve documents.");
         }
      });

      fp.add(mergeButton);
      fp.add(cancelButton);

      vp.add(checkBoxPanel);
      vp.add(fp);

      db.setText("Merge Schedules");
      db.setWidget(vp);
      db.center();
      db.show();
   }

   private void createNewSchedule()
   {

      displayNewSchedPopup("Create", new NameScheduleCallback()
      {
         @Override
         public void namedSchedule(final String name)
         {
            if (!scheduleNames.contains(name))
            {
               newDocName = name;
               final LoadingPopup popup = new LoadingPopup();
               popup.show();

               DOM.setElementAttribute(popup.getElement(), "id", "failSchedPopup");

               service.createDocumentAndGetWorkingCopy(newDocName, new AsyncCallback<DocumentGWT>()
               {

                  @Override
                  public void onSuccess(DocumentGWT result)
                  {
                     popup.hide();
                     openDocument(result);
                     // openDocInNewTab(name, newSchedID);
                  }

                  @Override
                  public void onFailure(Throwable caught)
                  {
                     popup.hide();
                     Window.alert("Failed to open new schedule in" + ": " + caught.getMessage());
                  }
               });
            }
            else
            {
               Window.alert("Error: Schedule named " + name + " already exists. Please enter a different name.");
            }
         }
      });
   }

   protected void openDocument(DocumentGWT doc)
   {
      if (myFrame.canPopViewsAboveMe())
      {
         myFrame.popFramesAboveMe();
         myFrame.frameViewAndPushAboveMe(new AdminScheduleNavView(service, this, menuBar, username, doc));
      }
   }

}
