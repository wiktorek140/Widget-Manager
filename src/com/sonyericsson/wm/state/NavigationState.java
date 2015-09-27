package com.sonyericsson.wm.state;

import com.sonyericsson.homescreen.Homescreen;
import com.sonyericsson.homescreen.idleitem.IdleItemProxy;
import com.sonyericsson.wm.log.Log;
import com.sonyericsson.wm.model.IdleItemModel;
import com.sonyericsson.wm.model.Model;
import com.sonyericsson.wm.tween.Animation;
import com.sonyericsson.wm.tween.AnimationModel;
import com.sonyericsson.wm.tween.Animator;
import com.sonyericsson.wm.view.ViewBase;
import com.sonyericsson.wm.view.ViewManager;

public final class NavigationState extends State
{
   final AnimationModel cursorModel;
   final Model model;
   private final IdleItemModel itemModel;

   public NavigationState(Homescreen homescreen, Model model, ViewBase view)
   {
      super(homescreen, model, view);
      this.cursorModel = new AnimationModel()
      {
      
         public int getH()
         {
            return 0;
         }
         
         public int getW()
         {
            return 0;
         }
         
         public int getX()
         {
            return 0;
         }
         
         public int getY()
         {
            return 0;
         }
         
         public void set(int x, int y, int w, int h)
         {
            NavigationState.this.getModel().setCursorAlphaChannel(x);
         }
         
      };
      this.model = model;
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
         this.getView().getViewManager().repaint();
      }
   }
   
   public void keyPressed(int key)
   {
      switch (key) 
      {
         case 9:
            this.bounceCursor(9, 10);
            break;
         case 13:
            this.getController().changeState(key);
            break;
         case 15:
            this.navigate(key);
            break;
         case 11:
            this.navigate(key);
            break;
         case 71:
            this.getController().changeState(key);
            break;
         case 2:
            this.getController().changeState(key);
            break;
         case 3:
            this.getController().changeState(key);
            break;
      }
   }
   
   public void showNotify()
   {
      this.getHomescreen().leaveStandby();
   }
   
   protected void enter(Animator animator)
   {
      Log.log("NavigationState.enter()");
      ViewBase view = this.getView();
      view.activate();
      view.getViewManager().setCommands(13);
      this.getHomescreen().leaveStandby();
      animator.addAnimation(this.createMoveItemInAnimation());
      animator.addAnimation(this.createCursorAnimation());
      animator.start();
      this.model.setDrawNavigationArrows(true);
   }
   
   protected void leave(Animator animator)
   {
      Log.log("NavigationState.leave()");
      this.model.setDrawNavigationArrows(false);
   }
   
   private void bounceCursor(int navigationalKey, int delta)
   {
       int bounce;
      switch (navigationalKey) 
      {
         case 9:
            bounce = delta;
            break;
         case 13:
            bounce = -delta;
            break;
         default:
            throw new IllegalArgumentException();
      }
      this.cursor.bounceVertical(bounce);
   }
   
   private Animation createCursorAnimation()
   {
      int x = 13;
      int y = 51;
      int w = 214;
      int h = 152;
      int xFunc = 1;
      int yFunc = 4;
      int wFunc = 1;
      int hFunc = 4;
      return this.cursor.createAnimation(13, 51, 214, 152, 1, 4, 1, 4);
   }
   
   private Animation createCursorColorFading()
   {
      int x = 166;
      int y = 0;
      int w = 0;
      int h = 0;
      int xFunc = 1;
      int yFunc = 0;
      int wFunc = 0;
      int hFunc = 0;
      int duration = 300;
      return new Animation(this.cursorModel, 166, 0, 0, 0, 1, 0, 0, 0, 300);
   }
   
   private void navigate(int key)
   {
      if (this.itemModel.size() > 1)
      {
         IdleItemProxy current = this.itemModel.getCurrent();
         int w = 210;
         int destinationCurrent;
         IdleItemProxy nextCurrent;
         ViewManager viewManager = this.getView().getViewManager();
         if (key == 15)
         {
            nextCurrent = this.itemModel.getPrevious();
            nextCurrent.move(-210, 53);
            destinationCurrent = viewManager.getWidth();
         }
         else
         {
            nextCurrent = this.itemModel.getNext();
            nextCurrent.move(viewManager.getWidth(), 53);
            destinationCurrent = -210;
         }
         nextCurrent.show();
         int x1 = destinationCurrent;
         int x2 = 15;
         int y = 53;
         int h = 148;
         int xFunc = 4;
         int yFunc = 0;
         int wFunc = 0;
         int hFunc = 0;
         int duration = 300;
         Animator animator = new Animator();
         animator.addAnimation(this.createItemAnimation(current, x1, 53, 210, 148, 4, 0, 0, 0, 300));
         animator.addAnimation(this.createItemAnimation(nextCurrent, 15, 53, 210, 148, 4, 0, 0, 0, 300));
         animator.addAnimation(this.createCursorColorFading());
         animator.start();
         current.hide();
         this.itemModel.setCurrent(nextCurrent);
         viewManager.repaint();
      }
   }
   
}