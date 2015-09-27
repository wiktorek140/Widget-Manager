package com.sonyericsson.wm.tween;

public final class Animation
{
   private final AnimationModel model;
   private final Tween xTween;
   private final Tween yTween;
   private final Tween wTween;
   private final Tween hTween;
   private final Time time;
   private int state;
   private boolean finished;

   public Animation(AnimationModel model, int destX, int destY, int destW, int destH, int xFunc, int yFunc, int wFunc, int hFunc, int duration)
   {
      this.state = 0;
      this.finished = false;
      this.model = model;
      this.time = new Time();
      this.xTween = this.createTween(model.getX(), destX, duration, xFunc);
      this.yTween = this.createTween(model.getY(), destY, duration, yFunc);
      this.wTween = this.createTween(model.getW(), destW, duration, wFunc);
      this.hTween = this.createTween(model.getH(), destH, duration, hFunc);
   }
   
   public void start()
   {
      this.xTween.start();
      this.yTween.start();
      this.wTween.start();
      this.hTween.start();
   }
   
   public boolean tick()
   {
      if (this.finished)
      {
         return false;
      }
      Tween.Value xValue = new Tween.Value();
      Tween.Value yValue = new Tween.Value();
      Tween.Value wValue = new Tween.Value();
      Tween.Value hValue = new Tween.Value();
      this.time.set(System.currentTimeMillis());
      this.state = this.yTween.tick(this.time, yValue);
      this.state &= this.xTween.tick(this.time, xValue);
      this.state &= this.wTween.tick(this.time, wValue);
      this.state &= this.hTween.tick(this.time, hValue);
      if (this.state == 0)
      {
         this.model.set(xValue.getValue(), yValue.getValue(), wValue.getValue(), hValue.getValue());
      }
      else
      {
         this.model.set(this.xTween.getTo(), this.yTween.getTo(), this.wTween.getTo(), this.hTween.getTo());
         this.finished = true;
      }
      if (this.state == 0)
      {
         return true;
      }
      return false;
   }
   
   public void animate(int destX, int destY, int destW, int destH, int xFunc, int yFunc, int wFunc, int hFunc, int duration)
   {
      Tween xTween = this.createTween(this.model.getX(), destX, duration, xFunc);
      Tween yTween = this.createTween(this.model.getY(), destY, duration, yFunc);
      Tween wTween = this.createTween(this.model.getW(), destW, duration, wFunc);
      Tween hTween = this.createTween(this.model.getH(), destH, duration, hFunc);
      xTween.start();
      yTween.start();
      wTween.start();
      hTween.start();
      Time time = new Time();
      time.set(System.currentTimeMillis());
      int state = 0;
      Tween.Value xValue = new Tween.Value();
      Tween.Value yValue = new Tween.Value();
      Tween.Value wValue = new Tween.Value();
      Tween.Value hValue = new Tween.Value();
      while (state == 0)
      {
         try
         {
            time.set(System.currentTimeMillis());
            state = yTween.tick(time, yValue);
            state &= xTween.tick(time, xValue);
            state &= wTween.tick(time, wValue);
            state &= hTween.tick(time, hValue);
            this.model.set(xValue.getValue(), yValue.getValue(), wValue.getValue(), hValue.getValue());
            if (state == 0)
            {
               Thread.sleep(15);
               continue;
            }
         }
         catch (Exception exception_1)
         {
         }
      }
      this.model.set(destX, destY, destW, destH);
   }
   
   private Tween createTween(int from, int to, int duration, int function)
   {
      Tween tween = new Tween();
      tween.setFrom(from);
      tween.setTo(to);
      tween.setDuration(duration);
      tween.setFunction(function);
      return tween;
   }
   
}