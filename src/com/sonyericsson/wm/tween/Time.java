package com.sonyericsson.wm.tween;

public final class Time
{
   public static final int MAX_TIME = 2147483647;
   private static long start;
   private long current;
   private int fromStart;

   static
   {
      start = 0;
   }
   
   public Time()
   {
   }
   
   private Time(long current, int fromStart)
   {
      this.current = current;
      this.fromStart = fromStart;
   }
   
   public Object createClone()
   {
      synchronized (this)
      {
         return new Time(this.current, this.fromStart);
      }
   }
   
   public int delta(Time ref)
   {
      synchronized (this)
      {
         return this.getFromStart() - ref.getFromStart();
      }
   }
   
   public long getCurrent()
   {
      synchronized (this)
      {
         return this.current;
      }
   }
   
   public int getFromStart()
   {
      synchronized (this)
      {
         return this.fromStart;
      }
   }
   
   public void set(long newCurrent)
   {
      synchronized (this)
      {
         if (start == 0)
         {
            start = newCurrent;
         }
         this.current = newCurrent;
         this.fromStart = (int) (this.current - start);
      }
   }
   
}