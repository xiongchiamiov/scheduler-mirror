package scheduler.view.web.client.views.resources.instructors;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.smartgwt.client.widgets.Window;

/**
 * This dialog is a message box
 * @author Carsten Pfeffer
 */
public class NoCourseDialog extends Window {
	private VerticalPanel panel;

	private Button buttonYes;
	private Button buttonNo;
	
	/**
	 * Creates a message box with the buttons defined by 'type'
	 * @param header: headline message
	 * @param message: message shown in the dialog
	 * @param type: determines which buttons are shown
	 */
	public NoCourseDialog(final String header, final String message) {
		this.setAutoSize(true);
		this.setTitle(header);
		
        // the content panel of the dlg
        this.panel = new VerticalPanel();
        this.panel.add(new Label(message));
        
        // ------ Buttons ---------------
        this.buttonYes = new Button("Yes");
        this.buttonNo = new Button("No");

        HorizontalPanel buttons = new HorizontalPanel();
        
    	buttons.add(this.buttonYes);
    	Label empty = new Label();
    	empty.setWidth("25px");
    	buttons.add(empty);
    	buttons.add(this.buttonNo);
        
    	buttons.setCellHorizontalAlignment(this.buttonYes, HasAlignment.ALIGN_RIGHT);
    	buttons.setCellHorizontalAlignment(this.buttonNo, HasAlignment.ALIGN_RIGHT);
    	
        this.panel.setCellHorizontalAlignment(buttons, HasAlignment.ALIGN_RIGHT);
        this.panel.add(buttons);
        this.addItem(this.panel);
	}
	
	public void addClickYesHandler(ClickHandler handler)
	{
        this.buttonYes.addClickHandler(handler);
	}
	
	public void addClickNoHandler(ClickHandler handler)
	{
        this.buttonNo.addClickHandler(handler);
	}
}
