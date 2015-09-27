package com.sonyericsson.wm.state;

import com.sonyericsson.homescreen.Homescreen;
import com.sonyericsson.homescreen.idleitem.IdleItemProxy;
import com.sonyericsson.wm.log.Log;
import com.sonyericsson.wm.model.IdleItemModel;
import com.sonyericsson.wm.model.Model;
import com.sonyericsson.wm.tween.Animation;
import com.sonyericsson.wm.tween.Animator;
import com.sonyericsson.wm.view.ViewBase;
import com.sonyericsson.wm.view.ViewManager;

public final class StandbyState extends State
{
   private String[] suiteVendors;

   public StandbyState(Homescreen homescreen, Model model, ViewBase view)
   {
      super(homescreen, model, view);
      this.suiteVendors = new String[]{"WorldClockWidget", "Sony Ericsson", "WorldClockWidget", "Sony Ericsson Mobile Communications AB"};
   }
   
   public void handleCurrentItemRemoved()
   {
      IdleItemModel itemModel = this.getModel().getIdleItemModel();
      IdleItemProxy next = itemModel.getNext();
      if (next != null)
      {
         next.move(15, 53);
         next.show();
         this.getView().getViewManager().repaint();
      }
      itemModel.setCurrent(next);
   }
   
   public void keyPressed(int key)
   {
      if (key == 9)
      {
         this.getController().changeState(key);
      }
   }
   
   public void showNotify()
   {
      this.getHomescreen().enterStandby();
   }
   
   public void updateArrowShown()
   {
      Homescreen homescreen = this.getHomescreen();
      if (this.getModel().getWidgetModel().isEmpty())
      {
         homescreen.resetKey(9);
      }
      else
      {
         homescreen.setKey(9);
      }
   }
   
   protected void enter(Animator animator)
   {
      Log.log("StandbyState.enter()");
      this.getView().activate();
      this.updateArrowShown();
      animator.addAnimation(this.createMoveItemInAnimation());
      animator.addAnimation(this.createCursorAnimation());
      animator.start();
      Homescreen homescreen = this.getHomescreen();
      homescreen.setStandbyInformation(true);
      this.showDateTimeInfo();
      homescreen.enterStandby();
   }
   
   protected void leave(Animator animator)
   {
      Log.log("StandbyState.leave()");
      Homescreen homescreen = this.getHomescreen();
      homescreen.setStandbyInformation(false);
      homescreen.leaveStandby();
   }
   
   private Animation createCursorAnimation()
   {
      ViewManager viewManager = this.getView().getViewManager();
      int x = viewManager.getWidth() / 2;
      int y = viewManager.getHeight() - 20;
      int w = 0;
      int h = 0;
      int xFunc = 2;
      int yFunc = 4;
      int wFunc = 2;
      int hFunc = 4;
      return this.cursor.createAnimation(x, y, 0, 0, 2, 4, 2, 4);
   }
   
   private boolean isClockWidget(String name, String vendor)
   {
      boolean bClockWidget = false;
      
      for (int i = 0; i < this.suiteVendors.length / 2; i++) 
      {
         if (this.suiteVendors[i * 2].equals(name) && this.suiteVendors[i * 2 + 1].equals(vendor))
         {
            bClockWidget = true;
            break;
         }
      }
      return bClockWidget;
   }
   
   public void showDateTimeInfo()
   {
      String name = "";
      String vendor = "";
      if (this.getModel().getIdleItemModel().getCurrent() != null)
      {
         name = this.getModel().getIdleItemModel().getCurrent().getMIDletIdentity().getName();
         vendor = this.getModel().getIdleItemModel().getCurrent().getMIDletIdentity().getVendor();
      }
      try
      {
         this.getHomescreen().setDateTimeInformation(!this.isClockWidget(name, vendor));
      }
      catch (Exception e)
      {
         Log.log(e.getClass().getName() + " " + e.getMessage());
      }
   }
   
}