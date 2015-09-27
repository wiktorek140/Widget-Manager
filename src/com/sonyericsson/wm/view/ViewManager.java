package com.sonyericsson.wm.view;

import com.sonyericsson.wm.app.Controller;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;

public final class ViewManager extends Canvas
{
   public static final int BACK = 1;
   public static final int HIDE = 2;
   public static final int MANAGE = 4;
   public static final int NONE = 0;
   public static final int SELECT = 8;
   public static final int SHOW = 16;
   private final ViewCommands commands;
   private Controller controller;
   private Paintable currentView;
   private boolean isVisible;
   private final Display display;

   public ViewManager(Display display)
   {
      this.commands = new ViewCommands(this);
      this.setCommandListener(this.commands);
      this.display = display;
   }
   
   public void handleKeyPress(int key)
   {
      if (this.controller != null)
      {
         this.controller.keyPressed(key);
      }
   }
   
   public void paint(Graphics g)
   {
      this.currentView.paint(g);
   }
   
   public void setCommands(int cmds)
   {
      this.commands.set(cmds);
   }
   
   public void setController(Controller controller)
   {
      this.controller = controller;
      this.display.setCurrent(this);
   }
   
   public void setView(Paintable view)
   {
      if (view == null)
      {
         throw new IllegalArgumentException();
      }
      this.currentView = view;
   }
   
   protected void hideNotify()
   {
      if (this.isVisible && this.controller != null)
      {
         this.controller.visibilityChanged(false);
      }
      this.isVisible = false;
   }
   
   protected void keyPressed(int key)
   {
      switch (this.getGameAction(key)) 
      {
         case 2:
            this.handleKeyPress(15);
            break;
         case 5:
            this.handleKeyPress(11);
            break;
         case 1:
            this.handleKeyPress(9);
            break;
         case 6:
            this.handleKeyPress(13);
            break;
         case 8:
            this.handleKeyPress(71);
            break;
      }
   }
   
   protected void showNotify()
   {
      if (!this.isVisible)
      {
         this.commands.update();
         this.repaint();
         if (this.controller != null)
         {
            this.controller.visibilityChanged(true);
         }
      }
      this.isVisible = true;
   }
   
}