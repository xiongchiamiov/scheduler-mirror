package scheduler.view.web.client;

import java.util.ArrayList;
import java.util.Collection;

import scheduler.view.web.shared.DocumentGWT;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

public class SaveAsDialog {

	public static void afterSaveAsPressed(final GreetingServiceAsync service, final DocumentGWT document) {

	      final ListBox saveAsListBox = new ListBox();
	      final ArrayList<String> schedNames = new ArrayList<String>();
	      final TextBox tb = new TextBox();
	      final DialogBox db = new DialogBox();
	      FlowPanel fp = new FlowPanel();
	      final Button saveButton = new Button("Save", new ClickHandler() {
	         public void onClick(ClickEvent event)
	         {
	            db.hide();

	            final String scheduleName = tb.getText();
	            if (scheduleName.isEmpty()) return;

	            boolean allowOverwrite = false;
	            if (schedNames.contains(scheduleName))
	            {
	               if (Window.confirm("The schedule \"" + scheduleName
	                     + "\" already exists.  Are you sure you want to replace it?"))
	                  allowOverwrite = true;
	               else return;
	            }

	            service.moveWorkingCopyToNewOriginalDocument(document.getID(), scheduleName, allowOverwrite, new AsyncCallback<Void>() {
	            	@Override
	            	public void onFailure(Throwable caught) {
	            		// TODO Auto-generated method stub
	            		
	            	}
	            	@Override
	            	public void onSuccess(Void v) {
	            		Window.alert("Successfully saved.");
	            	}
	            });
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
	            if (event.getCharCode() == KeyCodes.KEY_ENTER) saveButton.click();
	         }
	      });

	      service.getAllOriginalDocuments(new AsyncCallback<Collection<DocumentGWT>>() {
			
			@Override
			public void onSuccess(Collection<DocumentGWT> result) {
	            for (DocumentGWT doc : result)
	            {
	               saveAsListBox.addItem(doc.getName());
	               schedNames.add(doc.getName());
	            }
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to get existing document names.");
			}
		});

	      db.setText("Name Schedule");
	      fp.add(new HTML("<center>Specify a name to save the schedule as...</center>"));
	      saveAsListBox.addClickHandler(new ClickHandler()
	      {
	         @Override
	         public void onClick(ClickEvent event)
	         {
	            tb.setText(saveAsListBox.getValue(saveAsListBox.getSelectedIndex()));
	         }
	      });
	      saveAsListBox.setVisibleItemCount(5);
	      fp.add(saveAsListBox);
	      fp.add(tb);
	      fp.add(saveButton);
	      fp.add(cancelButton);

	      db.setWidget(fp);
	      db.center();
	      db.show();
	}

}
