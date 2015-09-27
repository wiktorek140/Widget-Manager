package com.sonyericsson.wm.tween;

import com.sonyericsson.wm.model.Model;

public final class CursorAnimation
{
   private final AnimationModel animationModel;
   private final Model model;

   public CursorAnimation(final Model model)
   {
      this.animationModel = new AnimationModel()
      {
      
         public int getH()
         {
            return model.getCursorH();
         }
         
         public int getW()
         {
            return model.getCursorW();
         }
         
         public int getX()
         {
            return model.getCursorX();
         }
         
         public int getY()
         {
            return model.getCursorY();
         }
         
         public void set(int x, int y, int w, int h)
         {
            model.setCursor(x, y, w, h, true);
         }
         
      };
      this.model = model;
   }
   
   public void bounceHorisontal(int bounce)
   {
      int x = this.model.getCursorX();
      int y = this.model.getCursorY();
      int w = 47;
      int h = 47;
      int xFunc = 3;
      int yFunc = 0;
      int wFunc = 0;
      int hFunc = 0;
      int duration = 100;
      this.animate(this.animationModel, x + bounce, y, 47, 47, 3, 0, 0, 0, 100);
      this.animate(this.animationModel, x, y, 47, 47, 3, 0, 0, 0, 100);
   }
   
   public void bounceVertical(int bounce)
   {
      int x = this.model.getCursorX();
      int y1 = this.model.getCursorY() - bounce;
      int y2 = this.model.getCursorY();
      int w = this.model.getCursorW();
      int h = this.model.getCursorH();
      int xFunc = 0;
      int yFunc = 3;
      int wFunc = 0;
      int hFunc = 0;
      int duration = 100;
      this.animate(this.animationModel, x, y1, w, h, 0, 3, 0, 0, 100);
      this.animate(this.animationModel, x, y2, w, h, 0, 3, 0, 0, 100);
   }
   
   public Animation createAnimation(int x, int y, int w, int h, int xFunc, int yFunc, int wFunc, int hFunc)
   {
      int duration = 300;
      return new Animation(this.animationModel, x, y, w, h, xFunc, yFunc, wFunc, hFunc, 300);
   }
   
   public Animation createQuickAnimation(int x, int y, int w, int h, int xFunc, int yFunc, int wFunc, int hFunc)
   {
      int duration = 200;
      return new Animation(this.animationModel, x, y, w, h, xFunc, yFunc, wFunc, hFunc, 200);
   }
   
   private final void animate(AnimationModel model, int x, int y, int w, int h, int xFunc, int yFunc, int wFunc, int hFunc, int duration)
   {
      Animator animator = new Animator();
      animator.addAnimation(new Animation(model, x, y, w, h, xFunc, yFunc, wFunc, hFunc, duration));
      animator.start();
   }
   
}