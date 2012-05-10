package scheduler.view.web.client;

import java.util.ArrayList;
import java.util.Collection;

import scheduler.view.web.shared.OriginalDocumentGWT;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MergeDialog {

   public static void fileMergePressed(CachedService service)
   {


		final com.smartgwt.client.widgets.Window window = new com.smartgwt.client.widgets.Window();
		window.setAutoSize(true);
		window.setTitle("Merge");
		window.setCanDragReposition(true);
		window.setCanDragResize(true);
		
   	
      final ArrayList<CheckBox> checkBoxList = new ArrayList<CheckBox>();
      final VerticalPanel vp = new VerticalPanel();
      final VerticalPanel checkBoxPanel = new VerticalPanel();
      FlowPanel fp = new FlowPanel();

      final Button mergeButton = new Button("Merge", new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            int checkCount = 0;

            for (CheckBox cb : checkBoxList)
            {
               if (cb.getValue()) checkCount++;
            }

            if (checkCount >= 2)
            {
               // TODO - Add merge call here when functionality is implemented
               window.destroy();
            }
            else
            {
               Window.alert("Please select 2 or more schedules to merge.");
            }
         }
      });

      final Button cancelButton = new Button("Cancel", new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            window.destroy();
         }
      });

      fp.add(mergeButton);
      fp.add(cancelButton);

      vp.add(checkBoxPanel);
      vp.add(fp);

		window.addItem(vp);

		window.centerInPage();
		window.show();
		
      Collection<OriginalDocumentGWT> result = service.originalDocuments.getAll();
      
      for (OriginalDocumentGWT doc : result)
      {
         CheckBox checkBox = new CheckBox(doc.getName());
         checkBoxList.add(checkBox);
         checkBoxPanel.add(checkBox);
      }

   }
}
