package com.sonyericsson.wm.state;

import com.sonyericsson.homescreen.Homescreen;
import com.sonyericsson.homescreen.idleitem.IdleItemProxy;
import com.sonyericsson.wm.log.Log;
import com.sonyericsson.wm.model.IdleItemModel;
import com.sonyericsson.wm.model.Model;
import com.sonyericsson.wm.model.WidgetModel;
import com.sonyericsson.wm.tween.Animation;
import com.sonyericsson.wm.tween.AnimationModel;
import com.sonyericsson.wm.tween.Animator;
import com.sonyericsson.wm.view.ViewBase;

public final class ManagementState extends State
{
   private final IdleItemModel itemModel;
   private final WidgetModel widgetModel;

   public ManagementState(Homescreen homescreen, Model model, ViewBase view)
   {
      super(homescreen, model, view);
      this.widgetModel = model.getWidgetModel();
      this.itemModel = model.getIdleItemModel();
   }
   
   public void handleCurrentItemRemoved()
   {
      IdleItemProxy next = this.itemModel.getNext();
      if (next != null)
      {
         next.move(15, -next.getHeight());
         next.show();
      }
      this.itemModel.setCurrent(next);
   }
   
   public void idleItemAdded(IdleItemProxy proxy)
   {
      Log.log(this.getModel().getProxyName(proxy) + ")");
      proxy.resize(210, 148);
      if (this.itemModel.isEmpty())
      {
         proxy.move(15, -proxy.getHeight());
         proxy.show();
         this.itemModel.setCurrent(proxy);
      }
      this.itemModel.add(proxy);
      this.getView().getViewManager().repaint();
   }
   
   public void keyPressed(int key)
   {
      switch (key) 
      {
         case 2:
            this.getController().changeState(key);
            break;
         case 71:
            this.widgetSelected();
            break;
         case 3:
            this.getController().changeState(key);
            break;
         case 15:
            this.moveCursorLeft();
            break;
         case 11:
            this.moveCursorRight();
            break;
         case 13:
            this.getController().changeState(key);
            break;
         case 7:
            this.getController().changeState(key);
            break;
      }
   }
   
   private void moveCursorRight()
   {
      Animator animator = new Animator();
      this.moveCursor(animator, 1);
      animator.start();
   }
   
   private void moveCursorLeft()
   {
      Animator animator = new Animator();
      this.moveCursor(animator, -1);
      animator.start();
   }
   
   public void showNotify()
   {
      this.getHomescreen().leaveStandby();
   }
   
   protected void enter(Animator animator)
   {
      Log.log("ManagementState.enter()");
      ViewBase view = this.getView();
      view.activate();
      view.getViewManager().setCommands(1);
      this.getHomescreen().leaveStandby();
      this.widgetModel.updateWidgetStatus();
      IdleItemProxy current = this.itemModel.getCurrent();
      animator.addAnimation(this.createItemAnimation(current));
      animator.addAnimation(this.createCursorAnimation(this.widgetModel.getCursorPosition()));
      animator.start();
      if (current != null)
      {
         current.hide();
      }
      view.getViewManager().repaint();
      view.getViewManager().serviceRepaints();
   }
   
   protected void leave(Animator animator)
   {
      Log.log("ManagementState.leave()");
      this.getModel().saveRunningWidgetsState();
      IdleItemProxy item = this.itemModel.getCurrent();
      if (item != null)
      {
         item.show();
      }
   }
   
   private Animation createItemAnimation(IdleItemProxy current)
   {
      if (current == null)
      {
         return null;
      }
      int x = 15;
      int y = -148;
      int w = current.getWidth();
      int h = current.getHeight();
      int xFunc = 0;
      int yFunc = 0;
      int wFunc = 0;
      int hFunc = 0;
      int duration = 300;
      return this.createItemAnimation(current, 15, -148, w, h, 0, 0, 0, 0, 300);
   }
   
   private Animation createCursorAnimation(int pos)
   {
      int y = 122;
      int x = WidgetModel.getXForPos(pos) - 3;
      int w = 47;
      int h = 47;
      int xFunc = 2;
      int yFunc = 2;
      int wFunc = 2;
      int hFunc = 2;
      return this.cursor.createQuickAnimation(x, 122, 47, 47, 2, 2, 2, 2);
   }
   
   private void addMoveWidgetAnimations(Animator animator, int delta)
   {
      this.widgetModel.moveWidgets(-delta);
      this.widgetModel.setWidgetXForPos(delta < 0 ? -1 : 5);
      if (delta < 0)
      {
         int pos = -1;
         for (; pos <= 4; pos++) 
         {
            animator.addAnimation(this.getMoveWidgetToPosAnimation(pos));
         }
      }
      else
      {
         int pos = 0;
         for (; pos <= 5; pos++) 
         {
            animator.addAnimation(this.getMoveWidgetToPosAnimation(pos));
         }
      }
   }
   
   private Animation getMoveWidgetToPosAnimation(int pos)
   {
      int x = WidgetModel.getXForPos(pos);
      int y = 125;
      int w = 42;
      int h = 42;
      int xTween = 2;
      int yTween = 2;
      int wTween = 0;
      int hTween = 0;
      AnimationModel animationModel = this.widgetModel.getAnimationModelForWidgetAtPos(pos);
      return new Animation(animationModel, x, 125, 42, 42, 2, 2, 0, 0, 250);
   }
   
   private void moveCursor(Animator animator, int delta)
   {
      if (this.widgetModel.isEmpty())
      {
         return;
      }
      int newCursorPos = this.widgetModel.getCursorPosition() + delta;
      int size = this.widgetModel.size();
      if (newCursorPos < 0 || newCursorPos >= Math.min(5, size))
      {
         if (size <= 5)
         {
            this.cursor.bounceHorisontal(10 * (delta / Math.abs(delta)));
         }
         else
         {
            this.addMoveWidgetAnimations(animator, delta);
            this.widgetModel.updateWidgetStatus();
         }
      }
      else
      {
         animator.addAnimation(this.createCursorAnimation(newCursorPos));
         this.widgetModel.setCursorPosition(newCursorPos);
      }
   }
   
   private void widgetSelected()
   {
      this.widgetModel.toggleWidgetAtCursor();
   }
   
}