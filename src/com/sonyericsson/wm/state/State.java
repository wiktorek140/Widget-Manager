package com.sonyericsson.wm.state;

import com.sonyericsson.homescreen.Homescreen;
import com.sonyericsson.homescreen.KeyListener;
import com.sonyericsson.homescreen.idleitem.IdleItemProxy;
import com.sonyericsson.wm.log.Log;
import com.sonyericsson.wm.model.IdleItemModel;
import com.sonyericsson.wm.model.Model;
import com.sonyericsson.wm.tween.Animation;
import com.sonyericsson.wm.tween.AnimationModel;
import com.sonyericsson.wm.tween.Animator;
import com.sonyericsson.wm.tween.CursorAnimation;
import com.sonyericsson.wm.view.ViewBase;

public abstract class State implements KeyListener
{
   protected final CursorAnimation cursor;
   private StateController controller;
   private final Homescreen homescreen;
   private final IdleItemModel itemModel;
   private final Model model;
   private final ViewBase view;

   protected State(Homescreen homescreen, Model model, ViewBase view)
   {
      if (homescreen == null || model == null || view == null)
      {
         throw new IllegalArgumentException();
      }
      this.cursor = new CursorAnimation(model);
      this.homescreen = homescreen;
      this.model = model;
      this.itemModel = model.getIdleItemModel();
      this.view = view;
   }
   
   public abstract void handleCurrentItemRemoved();
   
   public void idleItemAdded(IdleItemProxy proxy)
   {
      Log.log(this.getModel().getProxyName(proxy) + ")");
      proxy.resize(210, 148);
      this.itemModel.add(proxy);
      if (this.itemModel.getCurrent() == null)
      {
         proxy.move(15, 53);
         this.itemModel.setCurrent(proxy);
         proxy.show();
         this.view.getViewManager().repaint();
      }
   }
   
   public void setController(StateController controller)
   {
      this.controller = controller;
   }
   
   public abstract void showNotify();
   
   public void updateArrowShown()
   {
   }
   
   protected final Animation createItemAnimation(final IdleItemProxy proxy, int x, int y, int w, int h, int xFunc, int yFunc, int wFunc, int hFunc, int duration)
   {
      AnimationModel animationModel = new AnimationModel()
      {
      
         public int getH()
         {
            return proxy.getHeight();
         }
         
         public int getW()
         {
            return proxy.getWidth();
         }
         
         public int getX()
         {
            return proxy.getX();
         }
         
         public int getY()
         {
            return proxy.getY();
         }
         
         public void set(int x, int y, int w, int h)
         {
            proxy.move(x, y);
            proxy.resize(w, h);
         }
         
      };
      return new Animation(animationModel, x, y, w, h, xFunc, yFunc, wFunc, hFunc, duration);
   }
   
   protected final Animation createMoveItemInAnimation()
   {
      IdleItemProxy current = this.itemModel.getCurrent();
      if (current != null && current.getY() == -148)
      {
         current.show();
         int x = 15;
         int y = 53;
         int w = current.getWidth();
         int h = current.getHeight();
         int xFunc = 0;
         int yFunc = 0;
         int wFunc = 0;
         int hFunc = 0;
         int duration = 300;
         Animation itemAnimation = this.createItemAnimation(current, 15, 53, w, h, 0, 0, 0, 0, 300);
         return itemAnimation;
      }
      return null;
   }
   
   protected abstract void enter(Animator animator_1);
   
   protected final StateController getController()
   {
      return this.controller;
   }
   
   protected final Homescreen getHomescreen()
   {
      return this.homescreen;
   }
   
   protected final Model getModel()
   {
      return this.model;
   }
   
   protected final ViewBase getView()
   {
      return this.view;
   }
   
   protected abstract void leave(Animator animator_1);

    void keyPressed(int key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
   
}