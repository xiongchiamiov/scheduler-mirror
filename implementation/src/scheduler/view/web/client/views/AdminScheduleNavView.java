package scheduler.view.web.client.views;

import scheduler.view.web.client.ExportDialog;
import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.client.IViewContents;
import scheduler.view.web.client.Import;
import scheduler.view.web.client.MergeDialog;
import scheduler.view.web.client.NewScheduleCreator;
import scheduler.view.web.client.SaveAsDialog;
import scheduler.view.web.client.TabOpener;
import scheduler.view.web.client.ViewFrame;
import scheduler.view.web.client.views.resources.courses.CoursesView;
import scheduler.view.web.client.views.resources.instructors.InstructorsView;
import scheduler.view.web.client.views.resources.locations.LocationsView;
import scheduler.view.web.shared.DocumentGWT;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
	
	
	
	private ToolStripMenuButton makeFileMenuAndButton() {
		Menu menu = new Menu();
		menu.setShowShadow(true);
		menu.setShadowDepth(3);
		
		MenuItem newItem = new MenuItem("New", "icons/16/document_plain_new.png", "Ctrl+N");
		newItem.addClickHandler(new ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				NewScheduleCreator.createNewSchedule(service, username);
			}
		});
		
		MenuItem openItem = new MenuItem("Open", "icons/16/folder_out.png", "Ctrl+O");
		openItem.addClickHandler(new ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				TabOpener.openHomeInNewTab(username);
			}
		});
		
		MenuItem closeItem = new MenuItem("Close", "icons/16/folder_out.png", "Ctrl+W");
		
		MenuItem saveItem = new MenuItem("Save", "icons/16/disk_blue.png", "Ctrl+S");
		saveItem.addClickHandler(new ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				service.saveWorkingCopyToOriginalDocument(document.getID(), new
						AsyncCallback<Void>() {
							public void onSuccess(Void result) {
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
				SaveAsDialog.afterSaveAsPressed(service, document);
			}
		});
		
		MenuItem mergeItem = new MenuItem("Merge", "icons/16/folder_out.png", "Ctrl+M");
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
		
		MenuItem importItem = new MenuItem("Import", "icons/16/folder_out.png", "Ctrl+I");
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
		
		MenuItem printItem = new MenuItem("Print", "icons/16/printer3.png", "Ctrl+P");
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
	
	private ToolStrip makeToolStrip() {
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
		
		return toolStrip;
	}
	
	private TabSet makeTabs() {
		
		TabSet tabSet = new TabSet();
		tabSet.setTabBarPosition(Side.TOP);
		
		tabSet.setTabBarThickness(25);
		tabSet.setTabBarAlign(Side.LEFT);
		tabSet.setWidth100();
		tabSet.setHeight(25);
		tabSet.setOverflow(Overflow.VISIBLE);
		tabSet.setPaneContainerOverflow(Overflow.VISIBLE);
		// tabSet.setPaneCon
		
		final Tab coursesTab = new Tab("Courses");
		tabSet.addTab(coursesTab);
		
		final Tab instructorsTab = new Tab("Instructors");
		tabSet.addTab(instructorsTab);
		
		final Tab locationsTab = new Tab("Locations");
		tabSet.addTab(locationsTab);
		
		final Tab scheduleTab = new Tab("Schedule");
		tabSet.addTab(scheduleTab);
		
		tabSet.setTabBarControls(TabBarControls.TAB_SCROLLER, TabBarControls.TAB_PICKER);
		
		tabSet.addTabSelectedHandler(new TabSelectedHandler() {
			public void onTabSelected(TabSelectedEvent event) {
				// Window.alert(event.getTab().getTitle());
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
					assert (false);
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
		Panel panel = new FlowPanel();
		panel.addStyleName("menubarcontainer");
		Panel container = new SimplePanel();
		container.addStyleName("menubar");
		container.add(makeToolStrip());
		panel.add(container);
		add(panel);
		
		viewFrameContainer = new SimplePanel();
		add(viewFrameContainer);
		
		panel.add(makeTabs());
	}
}
