package com.sonyericsson.wm.tween;

import java.util.Vector;

public final class Animator
{
   private final Vector animations;

   public Animator()
   {
      this.animations = new Vector();
   }
   
   public void addAnimation(Animation animation)
   {
      if (animation != null)
      {
         this.animations.addElement(animation);
      }
   }
   
   public void start()
   {
      int size = this.animations.size();

      for (int i=0; i < size; i++) 
      {
         this.getAnimation(i).start();
      }
      boolean running = true;
      while (running)
      {
         running = false;
         
         for (int i=0; i < size; i++) 
         {
            running = !!this.getAnimation(i).tick() && !running;
         }
         try
         {
            Thread.sleep(15);
         }
         catch (Exception exception_1)
         {
         }
      }
   }
   
   private Animation getAnimation(int index)
   {
      return (Animation) this.animations.elementAt(index);
   }
   
}