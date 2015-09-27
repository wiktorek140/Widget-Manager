package com.sonyericsson.wm.view;

import com.sonyericsson.homescreen.Homescreen;
import com.sonyericsson.wm.model.Model;
import com.sonyericsson.wm.model.WidgetModel;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public final class ManagementView extends ViewBase
{
   private static final int BG_COLOR_TEXT = 0;
   private static final int BG_HEIGHT = 30;
   private static final int BG_MARGIN = 25;
   private static final int BG_WIDTH = 190;
   private static final int BG_X = 25;
   private static final int BG_Y = 85;
   private final WidgetModel widgetModel;

   public ManagementView(Display display, Homescreen homescreen, ViewManager manager, Model model)
   {
      super(display, homescreen, manager, model);
      this.widgetModel = this.getModel().getWidgetModel();
   }
   
   public void paint(Graphics g)
   {
      this.paintAppTextGradientBackground(g);
      if (!this.widgetModel.isEmpty())
      {
         int status = this.widgetModel.getStatusForWidgetAtCursor();
         int cmds = 1;
         if (status == 3)
         {
            cmds |= 2;
         }
         else if (status == 2)
         {
            cmds |= 0x10;
         }
         this.getViewManager().setCommands(cmds);
         this.paintApplicationText(g);
         this.paintCursor(g);
         int i = 0;
         int max = Math.min(5, this.widgetModel.size());
         for (; i < max; i++) 
         {
            this.paintWidgetIcon(g, i);
         }
      }
   }
   
   private void paintApplicationText(Graphics g)
   {
      Font font = g.getFont();
      String name = this.widgetModel.getNameAtCursor();
      if (font.stringWidth(name) > 190)
      {
         String ellipsis = "...";
         int ellipsisWidth = font.stringWidth("...");
         while (font.stringWidth(name) + ellipsisWidth > 190)
         {
            name = name.substring(0, name.length() - 1);
            if (!(name.length() > 1))
            {
               break;
            }
         }
         name = new StringBuffer(String.valueOf(name)).append("...").toString();
      }
      g.setColor(0);
      this.getUtility().setAlpha(g, 255);
      int x = 25 + (190 - font.stringWidth(name)) / 2;
      int y = 85 + (30 - font.getHeight()) / 2;
      g.drawString(name, x, y, 20);
   }
   
   private void paintAppTextGradientBackground(Graphics g)
   {
      float alpha = 255.0f;
      float deltaA = alpha / 30.0f;
      g.setColor(10066329);
      int i = 0;
      for (; i < 30; i++) 
      {
         int x = 25;
         int x2 = 215;
         if (i < 3)
         {
            x += 3 - i;
            x2 -= 3 - i;
         }
         g.drawLine(x, 85 + i, x2, 85 + i);
         alpha -= deltaA / 2.0f;
         this.getUtility().setAlpha(g, (int) alpha);
      }
   }
   
   private void paintWidgetIcon(Graphics g, int i)
   {
      int widgetX = this.widgetModel.getXAtPos(i);
      int widgetY = this.widgetModel.getYAtPos(i);
      Image widgetIcon = this.widgetModel.getIconAtPos(i);
      int x = widgetX + (42 - widgetIcon.getWidth()) / 2;
      int y = widgetY + (42 - widgetIcon.getHeight()) / 2;
      g.drawImage(widgetIcon, x, y, 20);
   }
   
}