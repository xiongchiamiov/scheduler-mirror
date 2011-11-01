package edu.calpoly.csc.scheduler.view.web.client.schedule;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;

/**
 * DropController for {@link DualListExample}.
 */
class ListBoxDropController extends AbstractDropController {

  private MouseListBox mouseListBox;

  ListBoxDropController(MouseListBox mouseListBox) {
    super(mouseListBox);
    this.mouseListBox = mouseListBox;
  }

  @Override
  public void onDrop(DragContext context) {
    MouseListBox from = (MouseListBox) context.draggable.getParent().getParent();
    for (Widget widget : context.selectedWidgets) {
      if (widget.getParent().getParent() == from) {
        HTML htmlClone = new HTML(DOM.getInnerHTML(widget.getElement()));
        mouseListBox.add(htmlClone);
      }
    }
    super.onDrop(context);
  }

  @Override
  public void onPreviewDrop(DragContext context) throws VetoDragException {
    MouseListBox from = (MouseListBox) context.draggable.getParent().getParent();
    if (from == mouseListBox) {
      throw new VetoDragException();
    }
    super.onPreviewDrop(context);
  }
}

