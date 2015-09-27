package com.sonyericsson.wm.app;

import com.sonyericsson.homescreen.Homescreen;
import com.sonyericsson.homescreen.KeyListener;
import com.sonyericsson.homescreen.idleitem.IdleItemEventData;
import com.sonyericsson.homescreen.idleitem.IdleItemListener;
import com.sonyericsson.homescreen.idleitem.IdleItemManager;
import com.sonyericsson.homescreen.idleitem.IdleItemProxy;
import com.sonyericsson.wm.log.Log;
import com.sonyericsson.wm.model.IdleItemModel;
import com.sonyericsson.wm.model.Model;
import com.sonyericsson.wm.state.State;
import com.sonyericsson.wm.state.StateController;
import com.sonyericsson.wm.view.ViewManager;
import com.sonyericsson.wm.view.VisibilityListener;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.midlet.MIDletStateChangeException;

public final class Controller implements KeyListener, IdleItemListener, VisibilityListener
{
   private Homescreen homescreen;
   boolean exiting;
   Object exitingLock;
   private final ViewManager manager;
   private final Model model;
   private final StateController stateController;
   private final Timer timer;

   public Controller(Homescreen homescreen, Model model, ViewManager manager, StateController stateController)
   {
      this.exiting = false;
      this.exitingLock = new Object();
      this.timer = new Timer();
      this.homescreen = homescreen;
      this.model = model;
      this.manager = manager;
      this.stateController = stateController;
      IdleItemManager idleItemManager = IdleItemManager.getInstance();
      idleItemManager.addIdleItemListener(this);
      homescreen.addKeyListener(this);
      homescreen.setTransparent(true);
   }
   
   public void destroyApp(boolean unconditional) throws MIDletStateChangeException
   {
      if (!unconditional)
      {
         this.timer.schedule(new TimerTask()
      {
      
         public void run()
         {
            stateController.changeState(7);
            manager.repaint();
         }
         
      }, 0);
         throw new MIDletStateChangeException();
      }
      synchronized (this.exitingLock)
      {
         this.exiting = true;
         this.homescreen.unregister();
         this.model.stopAllWidgets();
      }
   }
   
   public void idleItemEvent(final IdleItemEventData data)
   {
      synchronized (this.exitingLock)
      {
         if (this.exiting)
         {
         }
         State currentState = stateController.getCurrentState();
         final IdleItemProxy proxy = data.getIdleItemProxy();
         final IdleItemModel idleItemModel = this.model.getIdleItemModel();
         switch (data.getEventType()) 
         {
            case 0:
               Log.log(model.getProxyName(proxy) + ")");
               currentState.idleItemAdded(proxy);
               break;
            case 1:
               Log.log("IdleItem REMOVED");
               if (proxy == idleItemModel.getCurrent())
               {
                  idleItemModel.setProxyInFocus(false);
                  currentState.handleCurrentItemRemoved();
               }
               idleItemModel.remove(proxy);
               break;
            case 2:
               Log.log(this.model.getProxyName(proxy) + ")");
               break;
            case 4:
               Log.log(this.model.getProxyName(proxy) + ")");
               this.stateController.changeState(7);
               break;
            case 3:
               Log.log(this.model.getProxyName(proxy)+ ")");
               //proxy =null;
               this.timer.schedule(new TimerTask()
      {
      
         public void run()
         {
            stateController.traverseOut(proxy, ((Integer) data.getData()).intValue());
         }
         
      }, 0);
               break;
            case 5:
               Log.log(this.model.getProxyName(proxy) + ")");
               final Integer v = (Integer) data.getData();
               this.timer.schedule(new TimerTask()
      {
      
         public void run()
         {
            if (v.intValue() == 0)
            {
               stateController.traverseAborted();
            }
            else if (v.intValue() == 1)
            {
               idleItemModel.setProxyInFocus(true);
               stateController.changeStateToFocused();
            }
         }
         
      }, 0);
               break;
            case 6:
               Log.log("WIDGET INSTALLED");
               this.timer.schedule(new TimerTask()
      {
      
         public void run()
         {
            model.getWidgetModel().widgetInstalled((String[]) data.getData());
         }
         
      }, 0);
               break;
            case 7:
               Log.log("WIDGET UNINSTALLED");
               this.timer.schedule(new TimerTask()
      {
      
         public void run()
         {
            model.getWidgetModel().widgetDeleted((String[]) data.getData());
         }
         
      }, 0);
               break;
         }
      }
   }
   
   public void keyPressed(final int key)
   {
      synchronized (this.exitingLock)
      {
         if (this.exiting)
         {
         }
         timer.schedule(new TimerTask()
      {
      
         public void run()
         {
            if (key == 7)
            {
               stateController.changeState(key);
            }
            else
            {
               stateController.keyPressed(key);
            }
         }
         
      }, 0);
      }
   }
   
   public void visibilityChanged(boolean becameVisible)
   {
      synchronized (this.exitingLock)
      {
         if (this.exiting)
         {
         }
         if (0 != 0)
         {
            this.stateController.showNotify();
         }
         else
         {
            this.stateController.hideNotify();
         }
      }
   }
   
}