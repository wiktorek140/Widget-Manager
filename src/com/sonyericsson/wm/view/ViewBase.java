package com.sonyericsson.wm.view;

import com.sonyericsson.homescreen.Homescreen;
import com.sonyericsson.wm.log.Log;
import com.sonyericsson.wm.model.Model;
import com.sonyericsson.wm.model.ModelListener;
import com.sonyericsson.wm.util.Helper;
import java.io.IOException;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public abstract class ViewBase implements Paintable
{
   private static final int ARC_WIDTH = 8;
   private static final int ARROW_OFFSET = 7;
   private static final int ARROW_SIZE = 12;
   private final Display display;
   private final ViewManager manager;
   private final Model model;
   private Image navLeft;
   private Image navRight;
   private Image navUp;
   private Image navDown;
   private final ModelListener modelListener;
   private final Helper utility;

   public ViewBase(Display display, Homescreen homescreen, final ViewManager manager, Model model)
   {
      this.modelListener = new ModelListener()
      {
      
         public void modelUpdated()
         {
            manager.repaint();
         }
         
      };
      this.utility = Helper.getInstance();
      this.display = display;
      this.manager = manager;
      this.model = model;
      try
      {
         this.navLeft = Image.createImage("/nav_left.png");
         this.navRight = Image.createImage("/nav_right.png");
         this.navUp = Image.createImage("/nav_up.png");
         this.navDown = Image.createImage("/nav_down.png");
      }
      catch (IOException e)
      {
         Log.log("Exception occured while loading navigation images");
      }
      manager.setFullScreenMode(false);
      model.addModelListener(this.modelListener);
   }
   
   public final void activate()
   {
      this.manager.setView(this);
      this.manager.repaint();
      this.manager.serviceRepaints();
   }
   
   public final Display getDisplay()
   {
      return this.display;
   }
   
   public final Helper getUtility()
   {
      return this.utility;
   }
   
   public final ViewManager getViewManager()
   {
      return this.manager;
   }
   
   public abstract void paint(Graphics graphics_1);
   
   protected final void drawArrowDown(Graphics g)
   {
      int x = 120;
      int y = 208;
      g.drawImage(this.navDown, 120, 208, 17);
      this.drawPointer(g, 112, 209, 128, 209, 120, 217);
   }
   
   protected final void drawArrowLeft(Graphics g)
   {
      int x = 8;
      int y = 127;
      g.drawImage(this.navLeft, 0, 114, 20);
      this.drawPointer(g, 8, 118, Math.max(-4, 0), 126, 8, 134);
   }
   
   protected final void drawArrowRight(Graphics g)
   {
      int x = 232;
      int y = 127;
      g.drawImage(this.navRight, 240, 114, 24);
      this.drawPointer(g, 232, 118, Math.min(244, 240), 126, 232, 134);
   }
   
   protected final void drawArrowUp(Graphics g)
   {
      ViewManager viewManager = this.getViewManager();
      int x = viewManager.getWidth() / 2;
      int y = viewManager.getHeight() - 3;
      g.drawImage(this.navUp, x, y, 33);
      this.drawPointer(g, x - 12 + 4, y - 1, x + 12 - 4, y - 1, x, y - 12);
   }
   
   protected final void drawCursor(Graphics g, int x, int y, int w, int h)
   {
      this.setHighlightedBackgroundColor(g);
      this.getUtility().setAlpha(g, this.model.getCursorAlphaChannel());
      this.drawRectangle(g, x, y, Math.max(w - 1, 0), Math.max(h - 1, 0));
   }
   
   protected final Model getModel()
   {
      return this.model;
   }
   
   protected final void paintCursor(Graphics g)
   {
      int x = this.model.getCursorX();
      int y = this.model.getCursorY();
      int w = this.model.getCursorW();
      int h = this.model.getCursorH();
      if (w > 0 && h > 0)
      {
         this.drawCursor(g, x, y, w, h);
      }
   }
   
   protected final void setDefaultBackgroundColor(Graphics g)
   {
      g.setColor(this.getDisplay().getColor(0));
   }
   
   protected final void setDefaultForegroundColor(Graphics g)
   {
      g.setColor(this.getDisplay().getColor(1));
   }
   
   protected final void setHighlightedBackgroundColor(Graphics g)
   {
      g.setColor(this.getDisplay().getColor(2));
   }
   
   protected final void setHighlightedForegroundColor(Graphics g)
   {
      g.setColor(this.getDisplay().getColor(3));
   }
   
   private void drawPointer(Graphics g, int x1, int y1, int x2, int y2, int x3, int y3)
   {
      this.setHighlightedBackgroundColor(g);
      this.getUtility().setAlpha(g, this.getModel().getCursorAlphaChannel());
      g.fillTriangle(x1, y1, x2, y2, x3, y3);
   }
   
   private void drawRectangle(Graphics g, int cX, int cY, int cW, int cH)
   {
      int color = g.getColor();
      g.drawRoundRect(cX - 3, cY - 3, cW + 6, cH + 6, 17, 17);
      g.setColor(color | 0xaaa0a0a0);
      g.drawRoundRect(cX - 2, cY - 2, cW + 4, cH + 4, 14, 14);
      g.setColor(color | 0xccc0c0c0);
      g.drawRoundRect(cX - 1, cY - 1, cW + 2, cH + 2, 11, 11);
      g.setColor(color | 0xaaa0a0a0);
      g.drawRoundRect(cX, cY, cW, cH, 7, 7);
      g.setColor(color);
      g.drawRoundRect(cX + 1, cY + 1, cW - 1, cH - 1, 5, 5);
   }
   
}