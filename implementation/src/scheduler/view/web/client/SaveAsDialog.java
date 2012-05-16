package scheduler.view.web.client;

import scheduler.view.web.shared.DocumentGWT;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

public class SaveAsDialog {

	public static void afterSaveAsPressed(final CachedService service, final CachedOpenWorkingCopyDocument document, final UpdateHeaderStrategy updateHeaderStrategy, final UnsavedDocumentStrategy unsavedDocumentStrategy) {
		final com.smartgwt.client.widgets.Window window = new com.smartgwt.client.widgets.Window();
		window.setAutoSize(true);
		window.setTitle("Name Schedule");
		window.setCanDragReposition(true);
		window.setCanDragResize(true);
		
      final ListBox saveAsListBox = new ListBox();
      final TextBox tb = new TextBox();
      FlowPanel fp = new FlowPanel();
      final Button saveButton = new Button("Save", new ClickHandler() {
         public void onClick(ClickEvent event) {
         	window.hide();
         	
            final String newDocumentName = tb.getText();
            
            if (newDocumentName.isEmpty()) {
            	Window.alert("Please enter a name for your new document.");
            }
            else {
            	afterNewNameSupplied(service, document, updateHeaderStrategy, newDocumentName, unsavedDocumentStrategy);
            }
         }
      });

      final Button cancelButton = new Button("Cancel", new ClickHandler() {
         @Override
         public void onClick(ClickEvent event) {
         	window.hide();
         }
      });

      tb.addKeyPressHandler(new KeyPressHandler() {
         @Override
         public void onKeyPress(KeyPressEvent event) {
            if (event.getCharCode() == KeyCodes.KEY_ENTER) saveButton.click();
         }
      });

      fp.add(new HTML("<center>Specify a name to save the schedule as...</center>"));
      saveAsListBox.addClickHandler(new ClickHandler() {
         @Override
         public void onClick(ClickEvent event) {
            tb.setText(saveAsListBox.getValue(saveAsListBox.getSelectedIndex()));
         }
      });
      
      saveAsListBox.setVisibleItemCount(5);
      fp.add(saveAsListBox);
      fp.add(tb);
      fp.add(saveButton);
      fp.add(cancelButton);

		window.addItem(fp);

		window.centerInPage();
		window.show();
		
      for (DocumentGWT doc : service.originalDocuments.getAll())
         saveAsListBox.addItem(doc.getName());
	}
	
	private static void afterNewNameSupplied(CachedService service, CachedOpenWorkingCopyDocument document, final UpdateHeaderStrategy updateHeaderStrategy, final String newDocumentName, final UnsavedDocumentStrategy unsavedDocumentStrategy) {

      DocumentGWT existingOriginalDocumentByThatName = null;
      for (DocumentGWT existingDocument : service.originalDocuments.getAll())
      	if (existingDocument.getName().equals(newDocumentName))
      		existingOriginalDocumentByThatName = existingDocument;
      
      if (existingOriginalDocumentByThatName != null) {
      	String confirmMessage = "The document \"" + existingOriginalDocumentByThatName.getName() + "\" already exists.  Are you sure you want to replace it?";
      	
         if (Window.confirm(confirmMessage)) {
         	document.copyToAndAssociateWithDifferentOriginalDocument(
         			existingOriginalDocumentByThatName,
         			new AsyncCallback<Void>() {
               		@Override
               		public void onFailure(Throwable caught) {
               			Window.alert("Failed to save!" + caught.getMessage());
               		}
               		
               		@Override
         				public void onSuccess(Void result) {
               			updateHeaderStrategy.onDocumentNameChanged(newDocumentName);
               			unsavedDocumentStrategy.setDocumentChanged(false);
         					Window.alert("Successfully saved!");
         				}
               	});
         }
      }
      else {
      	document.copyToAndAssociateWithNewOriginalDocument(
      			newDocumentName,
      			new AsyncCallback<Void>() {
            		@Override
            		public void onFailure(Throwable caught) {
            			Window.alert("Failed to save! " + caught.getMessage());
            		}
            		
            		@Override
      				public void onSuccess(Void result) {
            			updateHeaderStrategy.onDocumentNameChanged(newDocumentName);
            			unsavedDocumentStrategy.setDocumentChanged(false);
      					Window.alert("Successfully saved!");
      				}
            	});
      }
	}
}
