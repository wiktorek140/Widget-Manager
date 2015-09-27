package com.sonyericsson.wm.view;

import com.sonyericsson.homescreen.Homescreen;
import com.sonyericsson.wm.model.Model;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;

public final class FocusedView extends ViewBase
{

   public FocusedView(Display display, Homescreen homescreen, ViewManager manager, Model model)
   {
      super(display, homescreen, manager, model);
   }
   
   public void paint(Graphics g)
   {
      this.paintCursor(g);
   }
   
}