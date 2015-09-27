package com.sonyericsson.wm.model;

import com.sonyericsson.homescreen.idleitem.IdleItemProxy;
import com.sonyericsson.wm.log.Log;
import java.io.IOException;
import java.util.Vector;

public final class Model
{
   private int cursorAlphaChannel;
   private int cursorH;
   private int cursorW;
   private int cursorX;
   private int cursorY;
   private boolean drawNavigationArrows;
   private final IdleItemModel idleItemModel;
   private final Listeners listeners;
   private final Vector seenApps;
   private final WidgetModel widgetModel;

   public Model()
   {
      this.drawNavigationArrows = false;
      this.listeners = Listeners.getInstance();
      this.seenApps = new Vector(20);
      this.idleItemModel = new IdleItemModel();
      this.widgetModel = new WidgetModel(this);
      this.cursorAlphaChannel = 166;
   }
   
   public void addModelListener(ModelListener listener)
   {
      this.listeners.add(listener);
   }
   
   public int getCursorAlphaChannel()
   {
      return this.cursorAlphaChannel;
   }
   
   public int getCursorH()
   {
      return this.cursorH;
   }
   
   public int getCursorW()
   {
      return this.cursorW;
   }
   
   public int getCursorX()
   {
      return this.cursorX;
   }
   
   public int getCursorY()
   {
      return this.cursorY;
   }
   
   public boolean getDrawNavigationArrows()
   {
      return this.drawNavigationArrows;
   }
   
   public IdleItemModel getIdleItemModel()
   {
      return this.idleItemModel;
   }
   
   public String getProxyName(IdleItemProxy proxy)
   {
      try
      {
         return proxy.getMIDletIdentity().getName();
      }
      catch (Exception e)
      {
      }
      return null;
   }
   
   public Vector getSeenApps()
   {
      return this.seenApps;
   }
   
   public WidgetModel getWidgetModel()
   {
      return this.widgetModel;
   }
   
   public void restoreRunningState() throws InterruptedException
   {
      Vector runningWidgets = new Vector();
      try
      {
         DataStorage.getInstance().loadRunningWidgetsState(runningWidgets);
         this.widgetModel.restoreRunningState(runningWidgets);
      }
      catch (IOException ioexception_1)
      {
      }
   }
   
   public void saveRunningWidgetsState()
   {
      try
      {
         DataStorage.getInstance().saveRunningWidgetsState(this);
      }
      catch (IOException e)
      {
         Log.log(new StringBuffer("Error when saving model: ").append(e).toString());
      }
   }
   
   public void setCursor(int x, int y, int w, int h, boolean notifyListeners)
   {
      this.cursorX = x;
      this.cursorY = y;
      this.cursorW = w;
      this.cursorH = h;
      if (notifyListeners)
      {
         this.listeners.notifyListeners();
      }
   }
   
   public void setCursorAlphaChannel(int cursorAlphaChannel)
   {
      this.cursorAlphaChannel = cursorAlphaChannel;
   }
   
   public void setDrawNavigationArrows(boolean drawNavigationArrows)
   {
      this.drawNavigationArrows = drawNavigationArrows;
   }
   
   public void stopAllWidgets()
   {
      this.widgetModel.stopAllWidgets();
   }
   
   public int widgetCount()
   {
      return this.widgetModel.size();
   }
   
}