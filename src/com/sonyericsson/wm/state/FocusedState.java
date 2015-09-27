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

public final class FocusedState extends State
{
   private IdleItemModel itemModel;

   public FocusedState(Homescreen homescreen, Model model, ViewBase view)
   {
      super(homescreen, model, view);
      this.itemModel = model.getIdleItemModel();
   }
   
   public void handleCurrentItemRemoved()
   {
      IdleItemProxy next = this.itemModel.getNext();
      if (next == null)
      {
         this.itemModel.setCurrent(null);
         this.getController().changeStateToStandby();
      }
      else
      {
         next.move(15, 53);
         next.show();
         this.itemModel.setCurrent(next);
         this.getController().changeStateToNavigation();
         this.getView().getViewManager().repaint();
      }
   }
   
   public void keyPressed(int keyCode)
   {
      if (keyCode == 3)
      {
         this.getController().changeState(keyCode);
      }
   }
   
   public void showNotify()
   {
      this.getHomescreen().leaveStandby();
   }
   
   protected final Animation createUnfocusAnimation()
   {
      IdleItemProxy proxy = this.itemModel.getCurrent();
      if (proxy != null)
      {
         int x = 15;
         int y = 53;
         int w = 210;
         int h = 148;
         int xFunc = 1;
         int yFunc = 4;
         int wFunc = 1;
         int hFunc = 4;
         int duration = 300;
         return this.createItemAnimation(proxy, 15, 53, 210, 148, 1, 4, 1, 4, 300);
      }
      return null;
   }
   
   protected void enter(Animator animator)
   {
      Log.log("FocusedState.enter()");
      ViewBase view = this.getView();
      view.activate();
      view.getViewManager().setCommands(1);
      this.getHomescreen().leaveStandby();
      IdleItemProxy proxy = this.itemModel.getCurrent();
      if (proxy != null)
      {
         ViewManager viewManager = view.getViewManager();
         animator.addAnimation(this.createCursorAnimation(-2, -2, viewManager.getWidth() + 4, viewManager.getHeight() + 4));
         animator.addAnimation(this.createItemAnimation(proxy));
         animator.start();
      }
   }
   
   protected void leave(Animator animator)
   {
      Log.log("FocusedState.leave()");
      if (this.itemModel.proxyHasFocus())
      {
         IdleItemProxy proxy = this.itemModel.getCurrent();
         if (proxy != null)
         {
            proxy.unfocus();
            this.itemModel.setProxyInFocus(false);
            animator.addAnimation(this.createUnfocusAnimation());
         }
      }
      this.getView().getViewManager().setCommands(13);
   }
   
   private Animation createCursorAnimation(int x, int y, int w, int h)
   {
      int xFunc = 1;
      int yFunc = 4;
      int wFunc = 1;
      int hFunc = 4;
      return this.cursor.createAnimation(x, y, w, h, 1, 4, 1, 4);
   }
   
   private Animation createItemAnimation(IdleItemProxy proxy)
   {
      int x = 15;
      int y = 40;
      int w = 210;
      int h = 200;
      int xFunc = 1;
      int yFunc = 4;
      int wFunc = 1;
      int hFunc = 4;
      int duration = 300;
      Animation itemAnimation = this.createItemAnimation(proxy, 15, 40, 210, 200, 1, 4, 1, 4, 300);
      return itemAnimation;
   }
   
}