package scheduler.view.web.client.views;

import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.client.IViewContents;
import scheduler.view.web.client.ViewFrame;
import scheduler.view.web.client.views.resources.courses.CoursesView;
import scheduler.view.web.client.views.resources.instructors.InstructorsView;
import scheduler.view.web.client.views.resources.locations.LocationsView;
import scheduler.view.web.shared.DocumentGWT;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.types.TabBarControls;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.MenuItemSeparator;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.TabDeselectedEvent;
import com.smartgwt.client.widgets.tab.events.TabDeselectedHandler;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripMenuButton;

public class AdminScheduleNavView extends VerticalPanel implements IViewContents {
	final GreetingServiceAsync service;
	final String username;
	final DocumentGWT document;
	
	SimplePanel viewFrameContainer;
	ViewFrame currentViewFrame;
	
	// final MenuBar menuBar;
	
	// MenuBar fileMenu, settingsMenu;
	// MenuItem instructorsMenuItem, locationsMenuItem, coursesMenuItem,
	// scheduleMenuItem;
	
	public AdminScheduleNavView(GreetingServiceAsync service, String username, DocumentGWT document) {
		this.service = service;
		this.username = username;
		this.document = document;
	}
	
	@Override
	public Widget getContents() {
		return this;
	}
	
	@Override
	public void afterPush(final ViewFrame viewFrame) {
		// makeFileMenu(viewFrame);
		// makeSettingsMenu();
		// makeResourcesAndScheduleViewsMenuItems(viewFrame);
		
		addMenus();
		
		// coursesMenuItem.getCommand().execute();
	}
	
	@Override
	public boolean canPop() {
		return true;
	}
	
	@Override
	public void beforePop() {
		
	}
	
	@Override
	public void beforeViewPushedAboveMe() {}
	
	@Override
	public void afterViewPoppedFromAboveMe() {}
	
	
	
	// private void makeFileMenu(final ViewFrame viewFrame) {
	// fileMenu = new MenuBar(true);
	// DOM.setElementAttribute(fileMenu.getElement(), "id", "fileMenu");
	//
	// MenuItem newItem = new MenuItem("New", true, new Command() {
	// public void execute() {
	// NewScheduleCreator.createNewSchedule(service, username);
	// }
	// });
	// DOM.setElementAttribute(newItem.getElement(), "id", "newItem");
	// fileMenu.addItem(newItem);
	//
	// MenuItem openItem = new MenuItem("Open", true, new Command() {
	// public void execute() {
	// String baseHref = Window.Location.getHref().substring(0,
	// Window.Location.getHref().lastIndexOf('?'));
	// Window.open(baseHref + "?userid=" + username, "_new", null);
	// }
	// });
	// DOM.setElementAttribute(openItem.getElement(), "id", "openItem");
	// fileMenu.addItem(openItem);
	//
	// fileMenu.addSeparator();
	//
	// MenuItem closeItem = new MenuItem("Close", true, new Command() {
	// public void execute() {
	// assert(false);
	// }
	// });
	//
	// DOM.setElementAttribute(closeItem.getElement(), "id", "closeItem");
	// fileMenu.addItem(closeItem);
	//
	// MenuItem saveItem = new MenuItem("Save", true, new Command() {
	// public void execute() {
	// service.saveWorkingCopyToOriginalDocument(document.getID(), new
	// AsyncCallback<Void>() {
	// public void onSuccess(Void result) {
	// Window.alert("Successfully saved!");
	// }
	// public void onFailure(Throwable caught) {
	// Window.alert("Failed to save! " + caught.getMessage());
	// }
	// });
	// }
	// });
	//
	// DOM.setElementAttribute(saveItem.getElement(), "id", "saveItem");
	// fileMenu.addItem(saveItem);
	//
	// MenuItem saveAsItem = new MenuItem("Save As", true, new Command() {
	// public void execute() {
	// SaveAsDialog.afterSaveAsPressed(service, document);
	// }
	// });
	//
	// DOM.setElementAttribute(saveAsItem.getElement(), "id", "saveAsItem");
	// fileMenu.addItem(saveAsItem);
	//
	// fileMenu.addSeparator();
	//
	// MenuItem importItem = new MenuItem("Import", true, new Command() {
	// public void execute() {
	// Import.showImport();
	// }
	// });
	//
	// DOM.setElementAttribute(importItem.getElement(), "id", "importItem");
	// fileMenu.addItem(importItem);
	//
	// MenuItem exportItem = new MenuItem("Export", true, new Command() {
	// public void execute() {
	// ExportDialog.displayExportPopup();
	// }
	//
	// });
	//
	// DOM.setElementAttribute(exportItem.getElement(), "id", "exportItem");
	// fileMenu.addItem(exportItem);
	//
	// fileMenu.addSeparator();
	//
	// MenuItem printItem = new MenuItem("Print", true, new Command() {
	// public void execute() {
	// Window.alert("Not yet implemented");
	// }
	// });
	//
	// DOM.setElementAttribute(printItem.getElement(), "id", "printItem");
	// fileMenu.addItem(printItem);
	//
	//
	// MenuItem mergeItem = new MenuItem("Merge", true, new Command() {
	// public void execute() {
	// MergeDialog.fileMergePressed(service);
	// }
	// });
	//
	// DOM.setElementAttribute(mergeItem.getElement(), "id", "mergeItem");
	// fileMenu.addItem(mergeItem);
	//
	// // fileMenuItem = new MenuItem("File v", true, fileMenu);
	// // DOM.setElementAttribute(fileMenuItem.getElement(), "id", "FileVIitem");
	// }
	//
	// private void makeSettingsMenu() {
	// settingsMenu = new MenuBar(true);
	// DOM.setElementAttribute(settingsMenu.getElement(), "id", "settingsMenu");
	//
	// MenuItem timesItem = new MenuItem("Times", true, new Command() {
	// public void execute() {
	// Window.alert("Unimplemented");
	// }
	// });
	// DOM.setElementAttribute(timesItem.getElement(), "id", "timesItem");
	// settingsMenu.addItem(timesItem);
	//
	// MenuItem preferencesItem = new MenuItem("Preferences", true, new Command()
	// {
	// public void execute() {
	// Window.alert("Unimplemented");
	// }
	// });
	// DOM.setElementAttribute(preferencesItem.getElement(), "id",
	// "preferencesItem");
	// settingsMenu.addItem(preferencesItem);
	//
	// MenuItem permissionsItem = new MenuItem("Permissions/Roles", true, new
	// Command() {
	// public void execute() {
	// Window.alert("Unimplemented");
	// }
	// });
	// DOM.setElementAttribute(permissionsItem.getElement(), "id", "timesItem");
	// settingsMenu.addItem(permissionsItem);
	// }
	//
	// private void makeResourcesAndScheduleViewsMenuItems(final ViewFrame
	// viewFrame) {
	// menuBar.addItem(instructorsMenuItem = new MenuItem("Instructors", true,
	// new Command() {
	// public void execute() {
	// if (viewFrame.canPopViewsAboveMe()) {
	// viewFrame.popFramesAboveMe();
	// viewFrame.frameViewAndPushAboveMe(new InstructorsView(service, document));
	// }
	// }
	// }));
	//
	// menuBar.addItem(locationsMenuItem = new MenuItem("Locations", true,
	// new Command() {
	// public void execute() {
	// if (viewFrame.canPopViewsAboveMe()) {
	// viewFrame.popFramesAboveMe();
	// viewFrame.frameViewAndPushAboveMe(new LocationsView(service, document));
	// }
	// }
	// }));
	//
	// menuBar.addItem(coursesMenuItem = new MenuItem("Courses", true,
	// new Command() {
	// public void execute() {
	// if (viewFrame.canPopViewsAboveMe()) {
	// viewFrame.popFramesAboveMe();
	// viewFrame.frameViewAndPushAboveMe(new CoursesView(service, document));
	// }
	// }
	// }));
	//
	// menuBar.addItem(scheduleMenuItem = new MenuItem("Schedule", true,
	// new Command() {
	// public void execute() {
	// if (viewFrame.canPopViewsAboveMe()) {
	// viewFrame.popFramesAboveMe();
	// viewFrame.frameViewAndPushAboveMe(new CalendarView(service, document));
	// }
	// }
	// }));
	// }
	
	private ToolStripMenuButton makeFileMenuAndButton() {
		Menu menu = new Menu();
		menu.setShowShadow(true);
		menu.setShadowDepth(3);
		
		MenuItem newItem = new MenuItem("New", "icons/16/document_plain_new.png", "Ctrl+N");
		MenuItem openItem = new MenuItem("Open", "icons/16/folder_out.png", "Ctrl+O");
		MenuItem saveItem = new MenuItem("Save", "icons/16/disk_blue.png", "Ctrl+S");
		MenuItem saveAsItem = new MenuItem("Save As", "icons/16/save_as.png");
		
		MenuItem recentDocItem = new MenuItem("Recent Documents", "icons/16/folder_document.png");
		
		Menu recentDocSubMenu = new Menu();
		MenuItem dataSM = new MenuItem("data.xml");
		dataSM.setChecked(true);
		MenuItem componentSM = new MenuItem("Component Guide.doc");
		MenuItem ajaxSM = new MenuItem("AJAX.doc");
		recentDocSubMenu.setItems(dataSM, componentSM, ajaxSM);
		
		recentDocItem.setSubmenu(recentDocSubMenu);
		
		MenuItem exportItem = new MenuItem("Export as...", "icons/16/export1.png");
		Menu exportSM = new Menu();
		exportSM.setItems(
				new MenuItem("XML"),
				new MenuItem("CSV"),
				new MenuItem("Plain text"));
		exportItem.setSubmenu(exportSM);
		
		MenuItem printItem = new MenuItem("Print", "icons/16/printer3.png", "Ctrl+P");
		printItem.setEnabled(false);
		
		MenuItemSeparator separator = new MenuItemSeparator();
		
		menu.setItems(newItem, openItem, separator, saveItem, saveAsItem,
				separator, recentDocItem, separator, exportItem, separator, printItem);
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
		
		MenuItem newItem = new MenuItem("Times", "icons/16/document_plain_new.png", "Ctrl+N");
		MenuItem openItem = new MenuItem("Preferences", "icons/16/folder_out.png", "Ctrl+O");
		MenuItem saveItem = new MenuItem("Permissions/Roles", "icons/16/disk_blue.png", "Ctrl+S");
		
		menu.setItems(newItem, openItem, saveItem);
		menu.setZIndex(1100000);
		menu.addStyleName("menumenu");
		
		ToolStripMenuButton menuButton = new ToolStripMenuButton("Settings", menu);
		menuButton.setWidth(100);
		menuButton.setZIndex(1200000);
		return menuButton;
	}
	
	
	private void addMenus() {
		Panel panel = new FlowPanel();
		panel.addStyleName("menubarcontainer");
		
		Panel container = new SimplePanel();
		container.addStyleName("menubar");
		panel.add(container);
		
		{
			ToolStrip toolStrip = new ToolStrip();
			toolStrip.setOverflow(Overflow.VISIBLE);
			toolStrip.setZIndex(1000000);
			toolStrip.setAutoWidth();
			toolStrip.setHeight(25);
			
			toolStrip.addSpacer(5);
			
			toolStrip.addMenuButton(makeFileMenuAndButton());
			
			toolStrip.addSpacer(5);
			
			toolStrip.addMenuButton(makeSettingsMenuAndButton());
			
			// I have no idea why we need two of them here, but it's the only way
			// it seems to work... -eo
			toolStrip.addSpacer(5);
			toolStrip.addSpacer(5);
			
			container.add(toolStrip);
		}
		
		
		TabSet tabSet = new TabSet();
		tabSet.setTabBarPosition(Side.TOP);
		
		tabSet.setTabBarThickness(25);
		tabSet.setTabBarAlign(Side.LEFT);
		tabSet.setWidth100();
		tabSet.setHeight(25);
		tabSet.setOverflow(Overflow.VISIBLE);
		tabSet.setPaneContainerOverflow(Overflow.VISIBLE);
//		tabSet.setPaneCon

		final Tab coursesTab = new Tab("Courses");
		final Tab instructorsTab = new Tab("Instructors");	
		final Tab locationsTab = new Tab("Locations");	
		final Tab scheduleTab = new Tab("Schedule");
		
		tabSet.addTabSelectedHandler(new TabSelectedHandler() {
			public void onTabSelected(TabSelectedEvent event) {
//				Window.alert(event.getTab().getTitle());
				if (event.getTab() == coursesTab) {
					currentViewFrame = new ViewFrame(new CoursesView(service, document));
					viewFrameContainer.add(currentViewFrame);
					currentViewFrame.afterPush();
				}
				else if (event.getTab() == instructorsTab) {
					currentViewFrame = new ViewFrame(new InstructorsView(service, document));
					viewFrameContainer.add(currentViewFrame);
					currentViewFrame.afterPush();
				}
				else if (event.getTab() == locationsTab) {
					currentViewFrame = new ViewFrame(new LocationsView(service, document));
					viewFrameContainer.add(currentViewFrame);
					currentViewFrame.afterPush();
				}
				else if (event.getTab() == scheduleTab) {
					currentViewFrame = new ViewFrame(new CalendarView(service, document));
					viewFrameContainer.add(currentViewFrame);
					currentViewFrame.afterPush();
				}
				else
					assert(false);
			}
		});

		tabSet.addTabDeselectedHandler(new TabDeselectedHandler() {
			public void onTabDeselected(TabDeselectedEvent event) {
//				currentViewFrame.beforePop();
				viewFrameContainer.clear();
			}
		});
		tabSet.addTab(coursesTab);

		tabSet.addTab(instructorsTab);

		tabSet.addTab(locationsTab);

		tabSet.addTab(scheduleTab);
			
		tabSet.setTabBarControls(TabBarControls.TAB_SCROLLER, TabBarControls.TAB_PICKER);
		
		SimplePanel tabsetcontainer = new SimplePanel();
//		tabSet.draw();
		tabsetcontainer.addStyleName("menutabset");
		panel.add(tabsetcontainer);

		viewFrameContainer = new SimplePanel();

		
		this.add(panel);
		this.add(viewFrameContainer);

		tabsetcontainer.add(tabSet);
		
	}
}
