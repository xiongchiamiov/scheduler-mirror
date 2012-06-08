package scheduler.view.web.client.views;

import scheduler.view.web.client.CachedOpenWorkingCopyDocument;
import scheduler.view.web.client.CachedService;
import scheduler.view.web.client.ExportDialog;
import scheduler.view.web.client.HTMLUtilities;
import scheduler.view.web.client.Import;
import scheduler.view.web.client.MergeDialog;
import scheduler.view.web.client.NewScheduleCreator;
import scheduler.view.web.client.NewScheduleCreator.OpenDocumentCallback;
import scheduler.view.web.client.SaveAsDialog;
import scheduler.view.web.client.UnsavedDocumentStrategy;
import scheduler.view.web.client.UpdateHeaderStrategy;
import scheduler.view.web.client.views.home.CloseStrategy;
import scheduler.view.web.client.views.home.OpenDocumentStrategy;
import scheduler.view.web.client.views.resources.courses.CoursesView;
import scheduler.view.web.client.views.resources.instructors.InstructorsView;
import scheduler.view.web.client.views.resources.locations.LocationsView;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.types.TabBarControls;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.MenuItemSeparator;
import com.smartgwt.client.widgets.menu.events.ClickHandler;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.TabDeselectedEvent;
import com.smartgwt.client.widgets.tab.events.TabDeselectedHandler;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripMenuButton;

public class AdminScheduleNavView extends VerticalPanel {
	final CachedService service;
	final CachedOpenWorkingCopyDocument document;
	boolean documentChanged;
	
	SimplePanel calendarViewContainer;
	UpdateHeaderStrategy updateHeaderStrategy;
	final CloseStrategy closeStrategy;
	final OpenDocumentStrategy openDocumentStrategy;
	
	CoursesView coursesView;
	InstructorsView instructorsView;
	LocationsView locationsView;
	CalendarView calendarView;
	
	// final MenuBar menuBar;
	
	// MenuBar fileMenu, settingsMenu;
	// MenuItem instructorsMenuItem, locationsMenuItem, coursesMenuItem,
	// scheduleMenuItem;
	
	public AdminScheduleNavView(CachedService service, final UpdateHeaderStrategy updateHeaderStrategy, CloseStrategy closeStrategy, OpenDocumentStrategy openDocumentStrategy, CachedOpenWorkingCopyDocument document) {
		this.service = service;
		this.document = document;
		this.documentChanged = false;
		this.updateHeaderStrategy = updateHeaderStrategy;
		this.openDocumentStrategy = openDocumentStrategy;
		this.closeStrategy = closeStrategy;
		
		Window.addWindowClosingHandler(new Window.ClosingHandler() {
			public void onWindowClosing(Window.ClosingEvent closingEvent) {
				if (documentChanged)
					closingEvent.setMessage("You have unsaved data! Are you sure you want to close?");
			}
		});
		
		document.addObserver(new CachedOpenWorkingCopyDocument.Observer() {
			@Override
			public void onAnyLocalChange() {
				documentChanged = true;
				updateHeaderStrategy.setDocumentChanged(true);
			}
		});

		addMenus();
	}
	
	private ToolStripMenuButton makeFileMenuAndButton(final UpdateHeaderStrategy updateHeaderStrategy) {
		Menu menu = new Menu();
		menu.setShowShadow(true);
		menu.setShadowDepth(3);
		
		MenuItem newItem = new MenuItem("New", "icons/16/document_plain_new.png");
		newItem.addClickHandler(new ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				NewScheduleCreator.createNewSchedule(service, new OpenDocumentCallback() {
					@Override
					public void openDocument(int documentID) {
						openDocumentStrategy.openDocument(documentID, false);
					}
				});
			}
		});
		
		MenuItem openItem = new MenuItem("Open", "icons/16/folder_out.png");
		openItem.addClickHandler(new ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				closeStrategy.closeDocument();
			}
		});
		
		MenuItem closeItem = new MenuItem("Close", "icons/16/folder_out.png");
		closeItem.addClickHandler(new ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				closeStrategy.closeDocument();
			}
		});
		
		MenuItem saveItem = new MenuItem("Save", "icons/16/disk_blue.png");
		saveItem.addClickHandler(new ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				document.copyIntoAssociatedOriginalDocument(new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						documentChanged = false;
						updateHeaderStrategy.setDocumentChanged(false);
						Window.alert("Successfully saved!");
					}
					
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Failed to save! " + caught.getMessage());
					}
				});
			}
		});
		
		MenuItem saveAsItem = new MenuItem("Save As", "icons/16/save_as.png");
		saveAsItem.addClickHandler(new ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				SaveAsDialog.afterSaveAsPressed(service, document, updateHeaderStrategy, new UnsavedDocumentStrategy() {
					@Override
					public void setDocumentChanged(boolean documentChanged) {
						AdminScheduleNavView.this.documentChanged = documentChanged;
						updateHeaderStrategy.setDocumentChanged(documentChanged);
					}
				});
			}
		});
		
//		MenuItem mergeItem = new MenuItem("Merge", "icons/16/folder_out.png");
//		mergeItem.addClickHandler(new ClickHandler() {
//			public void onClick(MenuItemClickEvent event) {
//				MergeDialog.fileMergePressed(service);
//			}
//		});
		
		MenuItem importItem = new MenuItem("Import", "icons/16/folder_out.png");
		importItem.addClickHandler(new ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				Import.showImport(service);
			}
		});
		
		MenuItem exportItem = new MenuItem("Export as...", "icons/16/export1.png");
		exportItem.addClickHandler(new ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ExportDialog.displayExportPopup(service, document);
			}
		});
		
//		MenuItem printItem = new MenuItem("Print", "icons/16/printer3.png");
//		printItem.setEnabled(false);
		
		MenuItemSeparator separator = new MenuItemSeparator();
		
		menu.setItems(newItem, openItem, separator, saveItem, saveAsItem, closeItem,
				separator, importItem, exportItem);
		menu.setZIndex(1100000);
		menu.addStyleName("menumenu");
		
		ToolStripMenuButton menuButton = new ToolStripMenuButton("File", menu);
		menuButton.setWidth(100);
		menuButton.setZIndex(1200000);
		return menuButton;
	}
	
	private ToolStripMenuButton makeSettingsMenuAndButton() {
		Menu menu = new Menu();
		menu.setShowShadow(true);
		menu.setShadowDepth(3);
		
		MenuItem newItem = new MenuItem("Times", "icons/16/document_plain_new.png");
		MenuItem openItem = new MenuItem("Preferences", "icons/16/folder_out.png");
		MenuItem saveItem = new MenuItem("Permissions/Roles", "icons/16/disk_blue.png");
		
		menu.setItems(newItem, openItem, saveItem);
		menu.setZIndex(1100000);
		menu.addStyleName("menumenu");
		
		ToolStripMenuButton menuButton = new ToolStripMenuButton("Settings", menu);
		menuButton.setWidth(100);
		menuButton.setZIndex(1200000);
		return menuButton;
	}
	
	private ToolStrip makeToolStrip(UpdateHeaderStrategy updateHeaderStrategy) {
		ToolStrip toolStrip = new ToolStrip();
		toolStrip.setOverflow(Overflow.VISIBLE);
		toolStrip.setZIndex(1000000);
		toolStrip.setAutoWidth();
		toolStrip.setHeight(25);
		
		toolStrip.addSpacer(5);
		
		toolStrip.addMenuButton(makeFileMenuAndButton(updateHeaderStrategy));
		
		// add these back in when we support them
//		toolStrip.addSpacer(5);
//		
//		toolStrip.addMenuButton(makeSettingsMenuAndButton());
		
		// I have no idea why we need two of them here, but it's the only way
		// it seems to work... -eo
		toolStrip.addSpacer(5);
		toolStrip.addSpacer(5);
		
		return toolStrip;
	}
	
	private TabSet makeTabs() {
		
		final TabSet tabSet = new TabSet();
		tabSet.setTabBarPosition(Side.TOP);
		
		tabSet.setTabBarThickness(25);
		tabSet.setTabBarAlign(Side.LEFT);
		tabSet.setWidth100();
		tabSet.setHeight(25);
		tabSet.setOverflow(Overflow.VISIBLE);
		tabSet.setPaneContainerOverflow(Overflow.VISIBLE);
		// tabSet.setPaneCon

		final Tab coursesTab = new Tab("Courses");
		coursesView = new CoursesView(document);
		coursesTab.setPane(coursesView);
//		coursesTab.setID("s_coursesTab");
		tabSet.addTab(coursesTab);
		
		final Tab instructorsTab = new Tab("Instructors");
		instructorsView = new InstructorsView(document);
		instructorsTab.setPane(instructorsView);
//		instructorsTab.setID("s_instructorsTab");
		tabSet.addTab(instructorsTab);
		
		final Tab locationsTab = new Tab("Locations");
		locationsView = new LocationsView(document);
		locationsTab.setPane(locationsView);
//		locationsTab.setID("s_locationsTab");
		tabSet.addTab(locationsTab);
		
		final Tab scheduleTab = new Tab("Schedule");
		Canvas emptyCanvas = new VLayout();
		emptyCanvas.setWidth(0);
		emptyCanvas.setHeight(1); // SmartGWT complains when we have 0 area.
		scheduleTab.setPane(emptyCanvas);
//		scheduleTab.setID("s_scheduleTab");
		tabSet.addTab(scheduleTab);
		
		tabSet.setTabBarControls(TabBarControls.TAB_SCROLLER, TabBarControls.TAB_PICKER);
		
		tabSet.addTabSelectedHandler(new TabSelectedHandler() {
			public void onTabSelected(TabSelectedEvent event) {
				if (event.getTab() == scheduleTab) {
					tabSet.setPaneContainerOverflow(Overflow.HIDDEN);
					calendarView = new CalendarView(document);
					calendarViewContainer.add(calendarView);
				}
				else {
					tabSet.setPaneContainerOverflow(Overflow.VISIBLE);
				}
			}
		});
		
		tabSet.addTabDeselectedHandler(new TabDeselectedHandler() {
			public void onTabDeselected(TabDeselectedEvent event) {
				// currentViewFrame.beforePop();
				calendarView = null;
				calendarViewContainer.clear();
			}
		});
		
		return tabSet;
	}
	
	
	private void addMenus() {
		VLayout navBar = new VLayout();
		navBar.setWidth100();
//		navBar.setHeight(40);
		navBar.setAutoHeight();
		navBar.setOverflow(Overflow.VISIBLE);
		add(navBar);
		
		calendarViewContainer = new SimplePanel();
		add(calendarViewContainer);

		navBar.addMember(makeToolStrip(updateHeaderStrategy));
		
		HTMLUtilities.addSpace(navBar, 10);
		
		navBar.addMember(makeTabs());
	}

	public boolean canClose() {
		if (calendarView != null && !calendarView.canClose())
			return false;
		if (!coursesView.canClose())
			return false;
		if (!instructorsView.canClose())
			return false;
		if (!locationsView.canClose())
			return false;
		if (documentChanged)
			return Window.confirm("There are unsaved changes to this document. Are you sure you want to navigate away?");
		return true;
	}

	public void close() {
		if (calendarView != null)
			calendarView.close();
		coursesView.close();
		instructorsView.close();
		locationsView.close();
		
		updateHeaderStrategy.setDocumentChanged(false);
		updateHeaderStrategy.onClosedDocument();
		
		this.clear();
	}
}
