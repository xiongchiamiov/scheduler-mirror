package scheduler.view.web.client.views;

import scheduler.view.web.client.ExportDialog;
import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.client.HTMLUtilities;
import scheduler.view.web.client.Import;
import scheduler.view.web.client.MergeDialog;
import scheduler.view.web.client.NewScheduleCreator;
import scheduler.view.web.client.NewScheduleCreator.CreatedScheduleCallback;
import scheduler.view.web.client.SaveAsDialog;
import scheduler.view.web.client.TabOpener;
import scheduler.view.web.client.UnsavedDocumentStrategy;
import scheduler.view.web.client.UpdateHeaderStrategy;
import scheduler.view.web.client.views.resources.courses.CoursesView;
import scheduler.view.web.client.views.resources.courses.DocumentCoursesCache;
import scheduler.view.web.client.views.resources.instructors.InstructorsView;
import scheduler.view.web.client.views.resources.locations.LocationsView;
import scheduler.view.web.shared.DocumentGWT;

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

public class AdminScheduleNavView extends VerticalPanel implements UnsavedDocumentStrategy {
	final GreetingServiceAsync service;
	final String username;
	final DocumentGWT document;
	boolean documentChanged;
	
	SimplePanel viewFrameContainer;
	UpdateHeaderStrategy updateHeaderStrategy;
	
	// final MenuBar menuBar;
	
	// MenuBar fileMenu, settingsMenu;
	// MenuItem instructorsMenuItem, locationsMenuItem, coursesMenuItem,
	// scheduleMenuItem;
	
	public AdminScheduleNavView(GreetingServiceAsync service, final UpdateHeaderStrategy updateHeaderStrategy, String username, DocumentGWT document) {
		this.service = service;
		this.username = username;
		this.document = document;
		this.documentChanged = false;
		this.updateHeaderStrategy = updateHeaderStrategy;
		
		Window.addWindowClosingHandler(new Window.ClosingHandler() {
			public void onWindowClosing(Window.ClosingEvent closingEvent) {
				if (documentChanged)
					closingEvent.setMessage("You have unsaved data! Are you sure you want to close?");
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
				NewScheduleCreator.createNewSchedule(service, username, new CreatedScheduleCallback() {
					public void createdSchedule() { }
				});
			}
		});
		
		MenuItem openItem = new MenuItem("Open", "icons/16/folder_out.png");
		openItem.addClickHandler(new ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				TabOpener.openHomeInNewTab(username);
			}
		});
		
		MenuItem closeItem = new MenuItem("Close", "icons/16/folder_out.png");
		closeItem.addClickHandler(new ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				TabOpener.openHomeInThisTab(username);
			}
		});
		
		MenuItem saveItem = new MenuItem("Save", "icons/16/disk_blue.png");
		saveItem.addClickHandler(new ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				service.saveWorkingCopyToOriginalDocument(document.getID(), new
						AsyncCallback<Void>() {
							public void onSuccess(Void result) {
								documentChanged = false;
								updateHeaderStrategy.setDocumentChanged(false);
								Window.alert("Successfully saved!");
							}
							public void onFailure(Throwable caught) {
								Window.alert("Failed to save! " + caught.getMessage());
							}
						});
			}
		});
		
		MenuItem saveAsItem = new MenuItem("Save As", "icons/16/save_as.png");
		saveAsItem.addClickHandler(new ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				SaveAsDialog.afterSaveAsPressed(service, document, updateHeaderStrategy, (UnsavedDocumentStrategy)AdminScheduleNavView.this);
			}
		});
		
		MenuItem mergeItem = new MenuItem("Merge", "icons/16/folder_out.png");
		mergeItem.addClickHandler(new ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				MergeDialog.fileMergePressed(service);
			}
		});
		
		// MenuItem recentDocItem = new MenuItem("Recent Documents",
		// "icons/16/folder_document.png");
		
		// Menu recentDocSubMenu = new Menu();
		// MenuItem dataSM = new MenuItem("data.xml");
		// dataSM.setChecked(true);
		// MenuItem componentSM = new MenuItem("Component Guide.doc");
		// MenuItem ajaxSM = new MenuItem("AJAX.doc");
		// recentDocSubMenu.setItems(dataSM, componentSM, ajaxSM);
		
		// recentDocItem.setSubmenu(recentDocSubMenu);
		
		MenuItem importItem = new MenuItem("Import", "icons/16/folder_out.png");
		importItem.addClickHandler(new ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				Import.showImport();
			}
		});
		
		MenuItem exportItem = new MenuItem("Export as...", "icons/16/export1.png");
		exportItem.addClickHandler(new ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ExportDialog.displayExportPopup();
			}
		});
		
		MenuItem printItem = new MenuItem("Print", "icons/16/printer3.png");
		printItem.setEnabled(false);
		
		MenuItemSeparator separator = new MenuItemSeparator();
		
		menu.setItems(newItem, openItem, separator, saveItem, saveAsItem, closeItem,
				separator, mergeItem, separator, importItem, exportItem, separator, printItem);
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
		
		toolStrip.addSpacer(5);
		
		toolStrip.addMenuButton(makeSettingsMenuAndButton());
		
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
		
		DocumentCoursesCache documentCoursesCache = new DocumentCoursesCache(service, document.getID());
		documentCoursesCache.addObserver(new DocumentCoursesCache.Observer() {
			public void onPopulate() { }
			public void onModify() {
				documentChanged = true;
				updateHeaderStrategy.setDocumentChanged(true);
			}
		});
		documentCoursesCache.populateFromServer();
		
		coursesTab.setPane(new CoursesView(documentCoursesCache));
		coursesTab.setID("s_coursesTab");
		tabSet.addTab(coursesTab);
		
		final Tab instructorsTab = new Tab("Instructors");
		instructorsTab.setPane(new InstructorsView(service, document, (UnsavedDocumentStrategy)AdminScheduleNavView.this));
		instructorsTab.setID("s_instructorsTab");
		tabSet.addTab(instructorsTab);
		
		final Tab locationsTab = new Tab("Locations");
		locationsTab.setPane(new LocationsView(service, document, (UnsavedDocumentStrategy)AdminScheduleNavView.this));
		locationsTab.setID("s_locationsTab");
		tabSet.addTab(locationsTab);
		
		final Tab scheduleTab = new Tab("Schedule");
		Canvas emptyCanvas = new VLayout();
		emptyCanvas.setWidth(0);
		emptyCanvas.setHeight(1); // SmartGWT complains when we have 0 area.
		scheduleTab.setPane(emptyCanvas);
		scheduleTab.setID("s_scheduleTab");
		tabSet.addTab(scheduleTab);
		
		tabSet.setTabBarControls(TabBarControls.TAB_SCROLLER, TabBarControls.TAB_PICKER);
		
		tabSet.addTabSelectedHandler(new TabSelectedHandler() {
			public void onTabSelected(TabSelectedEvent event) {
				if (event.getTab() == scheduleTab) {
					tabSet.setPaneContainerOverflow(Overflow.HIDDEN);
					viewFrameContainer.add(new CalendarView(service, document, (UnsavedDocumentStrategy)AdminScheduleNavView.this));
				}
				else {
					tabSet.setPaneContainerOverflow(Overflow.VISIBLE);
				}
			}
		});
		
		tabSet.addTabDeselectedHandler(new TabDeselectedHandler() {
			public void onTabDeselected(TabDeselectedEvent event) {
				// currentViewFrame.beforePop();
				viewFrameContainer.clear();
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
		
		viewFrameContainer = new SimplePanel();
		add(viewFrameContainer);

		navBar.addMember(makeToolStrip(updateHeaderStrategy));
		
		HTMLUtilities.addSpace(navBar, 10);
		
		navBar.addMember(makeTabs());
	}

	@Override
	public void setDocumentChanged(boolean documentChanged) {
		// TODO Auto-generated method stub
//		System.out.println("implement me");
	}
}
