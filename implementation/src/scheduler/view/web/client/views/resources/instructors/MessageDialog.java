package scheduler.view.web.client.views.resources.instructors;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
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
	private MessageDialogClicked clicked;
	
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
        
        this.clicked = MessageDialogClicked.NONE;
        
        final MessageDialog box = this; // hopefully this doesn't cause recursion
        
        
        // ------ Buttons ---------------
        
        final Button buttonClose = new Button("Close",new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
            	box.setClickedButton(MessageDialogClicked.CLOSE);
                box.hide();
            }
        });
        
        final Button buttonOk = new Button("Ok",new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
            	box.setClickedButton(MessageDialogClicked.OK);
                box.hide();
            }
        });
        
        final Button buttonYes = new Button("Yes",new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
            	box.setClickedButton(MessageDialogClicked.YES);
                box.hide();
            }
        });
        
        final Button buttonNo = new Button("No",new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
            	box.setClickedButton(MessageDialogClicked.NO);
                box.hide();
            }
        });
        
        final Button buttonCancel = new Button("Cancel",new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
            	box.setClickedButton(MessageDialogClicked.CANCEL);
                box.hide();
            }
        });
        
        HorizontalPanel buttons = new HorizontalPanel();
        
        switch(type)
        {
        case CLOSE:
        	buttons.add(buttonClose);
        	break;
        case OK:
        	buttons.add(buttonOk);
        	break;
        case OK_CANCEL:
        	buttons.add(buttonOk);
        	buttons.add(new Label("      "));
        	buttons.add(buttonCancel);
        	break;
        case YES_NO:
        	buttons.add(buttonYes);
        	buttons.add(new Label("      "));
        	buttons.add(buttonNo);
        	break;
        case YES_NO_CANCEL:
        	buttons.add(buttonYes);
        	buttons.add(new Label("      "));
        	buttons.add(buttonNo);
        	buttons.add(new Label("      "));
        	buttons.add(buttonCancel);
        	break;
        }
        this.panel.setCellHorizontalAlignment(buttons, HasAlignment.ALIGN_RIGHT);
        this.panel.add(buttons);
        this.add(this.panel);
	}
	
	/**
	 * Set the info, which button was clicked
	 * @param clicked
	 */
	public void setClickedButton(MessageDialogClicked clicked)
	{
		this.clicked = clicked;
	}
	
	/**
	 * @return the button type which has been clicked (either YES, NO, CLOSE, OK or CANCEL)
	 */
	public MessageDialogClicked getClickedButton()
	{
		return this.clicked;
	}
}
