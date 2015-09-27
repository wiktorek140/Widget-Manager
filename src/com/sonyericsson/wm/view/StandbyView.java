package com.sonyericsson.wm.view;

import com.sonyericsson.homescreen.Homescreen;
import com.sonyericsson.wm.model.Model;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;

public final class StandbyView extends ViewBase
{

   public StandbyView(Display display, Homescreen homescreen, ViewManager manager, Model model)
   {
      super(display, homescreen, manager, model);
   }
   
   public void paint(Graphics g)
   {
      Model model = this.getModel();
      if (model.getWidgetModel().size() > 0 || model.getIdleItemModel().size() > 0)
      {
         this.drawArrowUp(g);
      }
   }
   
}