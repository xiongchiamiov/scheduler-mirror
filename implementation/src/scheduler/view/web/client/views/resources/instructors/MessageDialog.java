package scheduler.view.web.client.views.resources.instructors;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;


public class MessageDialog extends DialogBox {
	private VerticalPanel panel;
	private MessageDialogClicked clicked;
	
	public MessageDialog(final String header, final String message, MessageDialogType type) {
		this.setText(header);
		this.setGlassEnabled(true);
        
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
        
//        buttonOk.setStyleAttribute("float", "left");
//        buttonCancel.setStyleAttribute("float", "left");
//        buttonClose.setStyleAttribute("float", "left");
//        buttonYes.setStyleAttribute("float", "left");
//        buttonNo.setStyleAttribute("float", "left");
        
        final Label emptyLabel = new Label("");
    	emptyLabel.setSize("auto","25px");
        
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
        	buttons.add(emptyLabel);
        	buttons.add(buttonCancel);
        	break;
        case YES_NO:
        	buttons.add(buttonYes);
        	buttons.add(emptyLabel);
        	buttons.add(buttonNo);
        	break;
        case YES_NO_CANCEL:
        	buttons.add(buttonYes);
          	buttons.add(emptyLabel);
        	buttons.add(buttonNo);
        	buttons.add(emptyLabel);
        	buttons.add(buttonCancel);
        	break;
        }
//        this.panel.setCellHorizontalAlignment(buttonOk, HasAlignment.ALIGN_RIGHT);
        this.panel.setCellHorizontalAlignment(buttons, HasAlignment.ALIGN_RIGHT);
        this.panel.add(buttons);
        this.add(this.panel);
        
//        // few empty labels to make widget larger
//        final Label emptyLabel = new Label("");
//        emptyLabel.setSize("auto","25px");
//        panel.add(emptyLabel);
//        panel.add(emptyLabel);
//        buttonOk.setWidth("90px");
//        panel.add(buttonOk);
//        panel.setCellHorizontalAlignment(buttonOk, HasAlignment.ALIGN_RIGHT);
//        box.add(panel);
//        return box;
	}
	
	public void setClickedButton(MessageDialogClicked clicked)
	{
		this.clicked = clicked;
	}
	
	public MessageDialogClicked getClickedButton()
	{
		return this.clicked;
	}
}
