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

/**
 * This dialog is a message box
 * @author Carsten Pfeffer
 */
public class MessageDialog extends DialogBox {
	private VerticalPanel panel;
	
	private Button buttonClose;
	private Button buttonOk;
	private Button buttonYes;
	private Button buttonNo;
	private Button buttonCancel;
	
	/**
	 * Creates a message box with the buttons defined by 'type'
	 * @param header: headline message
	 * @param message: message shown in the dialog
	 * @param type: determines which buttons are shown
	 */
	public MessageDialog(final String header, final String message, MessageDialogType type) {
		this.setText(header);
		this.setGlassEnabled(true);
		this.setModal(true);
        
        // the content panel of the dlg
        this.panel = new VerticalPanel();
        this.panel.add(new Label(message));
        
        // ------ Buttons ---------------
        
        this.buttonClose = new Button("Close");
        this.buttonOk = new Button("Ok");
        this.buttonYes = new Button("Yes");
        this.buttonNo = new Button("No");
        this.buttonCancel = new Button("Cancel");
        
        HorizontalPanel buttons = new HorizontalPanel();
        
        switch(type)
        {
        case CLOSE:
        	buttons.add(this.buttonClose);
        	break;
        case OK:
        	buttons.add(this.buttonOk);
        	break;
        case OK_CANCEL:
        	buttons.add(this.buttonOk);
        	buttons.add(new Label("      "));
        	buttons.add(this.buttonCancel);
        	break;
        case YES_NO:
        	buttons.add(this.buttonYes);
        	buttons.add(new Label("      "));
        	buttons.add(this.buttonNo);
        	break;
        case YES_NO_CANCEL:
        	buttons.add(this.buttonYes);
        	buttons.add(new Label("      "));
        	buttons.add(this.buttonNo);
        	buttons.add(new Label("      "));
        	buttons.add(this.buttonCancel);
        	break;
        }
        this.panel.setCellHorizontalAlignment(buttons, HasAlignment.ALIGN_RIGHT);
        this.panel.add(buttons);
        this.add(this.panel);
        this.setModal(true);
	}
	
	public void addClickHandler(ClickHandler handler)
	{
		this.buttonClose.addClickHandler(handler);
        this.buttonOk.addClickHandler(handler);
        this.buttonYes.addClickHandler(handler);
        this.buttonNo.addClickHandler(handler);
        this.buttonCancel.addClickHandler(handler);
	}
}
