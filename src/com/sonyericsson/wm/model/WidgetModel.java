package com.sonyericsson.wm.model;

import com.sonyericsson.ams.Application;
import com.sonyericsson.wm.log.Log;
import com.sonyericsson.wm.tween.AnimationModel;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.lcdui.Image;

public final class WidgetModel
{
   public static final int INSTALLED = 2;
   public static final int STARTED = 3;
   public static final int UNDETERMINED = 0;
   private int cursorPosition;
   private final Model model;
   private int pos0Index;
   private Vector widgets;

   public static void addWidgetToVector(Vector widgets, Application app)
   {
      addWidgetToVector(widgets, new WidgetData(app));
   }
   
   public static int getXForPos(int pos)
   {
      return 5 + 47 * pos;
   }
   
   private static void addWidgetToVector(Vector widgets, WidgetData widget)
   {
      widget.setPos(getXForPos(widgets.size()), 125, false);
      widgets.addElement(widget);
   }
   
   public WidgetModel(Model model)
   {
      this.cursorPosition = 0;
      this.pos0Index = 0;
      this.model = model;
   }
   
   public synchronized void decouple()
   {
      int i = 0;
      for (; i < this.size(); i++) 
      {
         this.getWidgetAtIndex(i).decouple();
      }
   }
   
   public synchronized AnimationModel getAnimationModelForWidgetAtPos(int pos)
   {
      final WidgetData widget = this.getWidgetAtPos(pos);
      return new AnimationModel()
      {
      
         public int getH()
         {
            return 42;
         }
         
         public int getW()
         {
            return 42;
         }
         
         public int getX()
         {
            return widget.getX();
         }
         
         public int getY()
         {
            return widget.getY();
         }
         
         public void set(int x, int y, int width, int height)
         {
            widget.setPos(x, y, true);
         }
         
      };
   }
   
   public synchronized int getCursorIndex()
   {
      if (this.widgets.isEmpty())
      {
         throw new IllegalStateException();
      }
      return this.mod(this.pos0Index + this.cursorPosition, this.size());
   }
   
   public synchronized int getCursorPosition()
   {
      return this.cursorPosition;
   }
   
   public synchronized Image getIconAtPos(int pos)
   {
      return this.getWidgetAtPos(pos).getIcon();
   }
   
   public synchronized String getNameAtCursor()
   {
      return this.getWidgetAtCursor().getName();
   }
   
   public synchronized int getStatusForWidgetAtCursor()
   {
      if (this.widgets.isEmpty())
      {
         return 0;
      }
      return this.getWidgetAtCursor().getCurrentStatus();
   }
   
   public synchronized int getXAtPos(int pos)
   {
      return this.getWidgetAtPos(pos).getX();
   }
   
   public synchronized int getYAtPos(int pos)
   {
      return this.getWidgetAtPos(pos).getY();
   }
   
   public synchronized boolean isEmpty()
   {
      if (this.widgets != null && !this.widgets.isEmpty())
      {
         return false;
      }
      return true;
   }
   
   public synchronized void moveWidgets(int delta)
   {
      this.pos0Index = this.mod(this.pos0Index - delta, this.size());
   }
   
   public synchronized void restoreRunningState(Vector runningWidgets) throws InterruptedException
   {
      if (runningWidgets == null)
      {
         return;
      }
      Log.log("restoring");
      int i = 0;
      for (; i < this.size(); i++) 
      {
         WidgetData widget = this.getWidgetAtIndex(i);
         String name = widget.getSuitename();
         Log.log(new StringBuffer("checking ").append(name).toString());
         if (runningWidgets.contains(name))
         {
            Log.log(new StringBuffer("starting ").append(name).toString());
            long time = System.currentTimeMillis();
            try
            {
               widget.start();
            }
            catch (IOException j)
            {
               Log.log(new StringBuffer("Failed to start ").append(name).toString());
               continue;
            }
            int j = 0;
            for (; j < 25; j++) 
            {
               Thread.sleep(400);
               if (widget.getCurrentStatus() == 3)
               {
                  break;
               }
            }
            Log.log(" started in " + (System.currentTimeMillis() - time) + " ms");
         }
      }
      Log.log("restoring done");
   }
   
   public synchronized void setCursorPosition(int pos)
   {
      int newPos = this.mod(pos, this.size());
      if (newPos != this.cursorPosition)
      {
         this.cursorPosition = newPos;
         Listeners.getInstance().notifyListeners();
      }
   }
   
   public synchronized void setWidgets(Vector newWidgets)
   {
      this.widgets = newWidgets;
      int size = newWidgets.size();
      while (this.pos0Index >= size)
      {
         this.pos0Index--;
      }
      if (this.cursorPosition >= size)
      {
         while (this.cursorPosition >= size)
         {
            this.cursorPosition--;
         }
         int y = 122;
         int x = getXForPos(this.cursorPosition) - 3;
         int w = 47;
         int h = 47;
         this.model.setCursor(x, 122, 47, 47, false);
      }
      Listeners.getInstance().notifyListeners();
   }
   
   public synchronized void setWidgetXForPos(int pos)
   {
      this.getWidgetAtPos(pos).setPos(getXForPos(pos), 125, true);
   }
   
   public synchronized int size()
   {
      if (this.widgets == null)
      {
         return 0;
      }
      return this.widgets.size();
   }
   
   public synchronized void stopAllWidgets()
   {
      int i = 0;
      for (; i < this.size(); i++) 
      {
         WidgetData widget = this.getWidgetAtIndex(i);
         if (widget.isStarted())
         {
            try
            {
               widget.stop();
               continue;
            }
            catch (IOException e)
            {
               Log.log(new StringBuffer("Failed to stop ").append(widget.getName()).toString());
            }
         }
      }
   }
   
   public synchronized void toggleWidgetAtCursor()
   {
      this.getWidgetAtCursor().toggle();
   }
   
   public synchronized void updateWidgetStatus()
   {
      int pos = 0;
      int end = Math.min(this.size(), 5);
      for (; pos < end; pos++) 
      {
         this.getWidgetAtPos(pos).updateCachedStatus();
      }
      Listeners.getInstance().notifyListeners();
   }
   
   public synchronized void widgetDeleted(String[] suiteAndVendor)
   {
      if (suiteAndVendor.length != 2)
      {
         throw new IllegalArgumentException();
      }
      String suite = suiteAndVendor[0];
      String vendor = suiteAndVendor[1];
      int i = 0;
      for (; i < this.widgets.size(); i++) 
      {
         WidgetData w = (WidgetData) this.widgets.elementAt(i);
         if (w.isEqual(suite, vendor))
         {
            this.widgets.removeElementAt(i);
            break;
         }
      }
      int size = this.widgets.size();
      while (this.pos0Index >= size)
      {
         this.pos0Index--;
      }
      if (this.cursorPosition >= size)
      {
         while (this.cursorPosition >= size)
         {
            this.cursorPosition--;
         }
         int y = 122;
         int x = getXForPos(this.cursorPosition) - 3;
         int w = 47;
         int h = 47;
         this.model.setCursor(x, 122, 47, 47, false);
      }
      this.updateWidgetPositions();
   }
   
   public synchronized void widgetInstalled(String[] suiteAndVendor)
   {
      if (suiteAndVendor.length != 2)
      {
         throw new IllegalArgumentException();
      }
      String suitename = suiteAndVendor[0];
      String vendor = suiteAndVendor[1];
      boolean updated = false;
      int i = 0;
      for (; i < this.widgets.size(); i++) 
      {
         WidgetData w = (WidgetData) this.widgets.elementAt(i);
         if (w.isEqual(suitename, vendor))
         {
            w.setup(suitename, vendor);
            updated = true;
            break;
         }
      }
      if (!updated)
      {
         this.widgets.addElement(new WidgetData(suitename, vendor));
      }
      this.updateWidgetPositions();
   }
   
   private int getIndexAtPos(int pos)
   {
      return this.mod(this.pos0Index + Math.max(Math.min(pos, 5), -1), this.size());
   }
   
   private WidgetData getWidgetAtCursor()
   {
      return (WidgetData) this.widgets.elementAt(this.getCursorIndex());
   }
   
   private WidgetData getWidgetAtIndex(int index)
   {
      return (WidgetData) this.widgets.elementAt(index);
   }
   
   private WidgetData getWidgetAtPos(int pos)
   {
      return (WidgetData) this.widgets.elementAt(this.getIndexAtPos(pos));
   }
   
   private int mod(int a, int b)
   {
      return (a % b + b) % b;
   }
   
   private void updateWidgetPositions()
   {
      int i = 0;
      for (; i < Math.min(5, this.widgets.size()); i++) 
      {
         WidgetData w = this.getWidgetAtPos(i);
         w.setPos(getXForPos(i), 125, false);
      }
      Listeners.getInstance().notifyListeners();
   }
   
}