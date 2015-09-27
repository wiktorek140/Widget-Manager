package com.sonyericsson.wm.tween;

public final class Tween
{
   public static final int BOUNCY = 3;
   public static final int EASE_IN = 1;
   public static final int EASE_IN_OUT = 4;
   public static final int EASE_OUT = 2;
   public static final int EXT_FUNC = -1;
   public static final int LINEAR = 0;
   public static final int RELATIVE_FALSE = 0;
   public static final int RELATIVE_PERCENTAGE = 2;
   public static final int RELATIVE_TRUE = 1;
   protected static final int STATE_SLEEPING = 0;
   protected static final int STATE_STARTING = 2;
   protected static final int STATE_TWEENING = 1;
   protected static final int STATE_WAITING = 3;
   private static final int UNSET = 2147483647;
   private int absTo;
   private boolean continuous;
   private int delay;
   private int diff;
   private int duration;
   private int from;
   private int function;
   private int original;
   private byte relative;
   private int startedAt;
   private int state;
   private int to_int;

   private static int bouncy(int t)
   {
      int threshold = 45875;
      int max = 72089;
      if (t < threshold)
      {
         long ret = (long) max;
         ret = ret * (long) t / (long) threshold;
         return (int) ret;
      }
      long ret = (long) (max - 65536);
      ret = 65536 + ret * (long) (65536 - t) / (long) (65536 - threshold);
      return (int) ret;
   }
   
   private static int easeIn(int t)
   {
      return Fx.easeIn(t);
   }
   
   private static int easeOut(int t)
   {
      return Fx.easeOut(t);
   }
   
   Tween()
   {
      this.delay = 0;
      this.from = 2147483647;
      this.state = 0;
   }
   
   public void abort()
   {
      if (this.state == 0)
      {
         return;
      }
      this.setState(0);
   }
   
   public int easeInOut(int input)
   {
      return 65536 + Fx.ksin((input >> 7) + 768) >> 1;
   }
   
   public int getDelay()
   {
      return this.delay;
   }
   
   public int getDuration()
   {
      return this.duration;
   }
   
   public int getFrom()
   {
      return this.from;
   }
   
   public byte getRelative()
   {
      return this.relative;
   }
   
   public boolean isContinuous()
   {
      return this.continuous;
   }
   
   public void setContinuous(boolean value)
   {
      this.continuous = value;
   }
   
   public void setDelay(int value)
   {
      this.delay = value;
   }
   
   public void setDuration(int duration)
   {
      this.duration = duration;
   }
   
   public void setFrom(int v)
   {
      this.from = v;
   }
   
   public void setFunction(int function)
   {
      if (this.function == function)
      {
         return;
      }
      this.function = function;
   }
   
   public void setRelative(byte relative)
   {
      this.relative = relative;
   }
   
   public void setTo(int v)
   {
      this.absTo = v;
      this.to_int = v;
   }
   
   public void start()
   {
      if (this.state != 0)
      {
         return;
      }
      this.setState(2);
   }
   
   public void stop()
   {
      if (this.state == 0)
      {
         return;
      }
      this.setState(0);
   }
   
   public int tick(Time time, Tween.Value value)
   {
      if (this.state == 2)
      {
         this.setState(3);
         this.startedAt = time.getFromStart();
      }
      int runtime = time.getFromStart() - this.startedAt;
      if (this.state == 3 && runtime >= this.delay)
      {
         if (this.from == 2147483647)
         {
            if (this.relative == 1)
            {
               this.absTo = this.original + this.to_int;
            }
         }
         else
         {
            this.original = this.from;
            if (this.relative == 1)
            {
               this.absTo = this.from + this.to_int;
            }
         }
         if (this.relative == 2)
         {
            this.absTo = this.original * this.to_int / 100;
         }
         this.diff = this.absTo - this.original;
         this.setState(1);
         this.startedAt = time.getFromStart();
         runtime = time.getFromStart() - this.startedAt;
      }
      if (this.state == 1 && this.continuous)
      {
         while (runtime >= this.duration)
         {
            runtime -= this.duration;
            this.startedAt += this.duration;
         }
      }
      if (this.state == 1)
      {
         if (runtime < this.duration)
         {
            int tx = (runtime << 16) / this.duration;
            switch (this.function) 
            {
               case 0:
                  break;
               case 1:
                  tx = easeIn(tx);
                  break;
               case 2:
                  tx = easeOut(tx);
                  break;
               case 3:
                  tx = bouncy(tx);
                  break;
               case 4:
                  tx = this.easeInOut(tx);
                  break;
            }
            int cTx = this.calculate(tx);
            value.setValue(cTx);
         }
         else
         {
            this.stop();
         }
      }
      if (this.state == 0)
      {
         return 2147483647;
      }
      return 0;
   }
   
   int getTo()
   {
      return this.absTo;
   }
   
   private int calculate(int tx)
   {
      return Fx.mul(this.diff, tx) + this.original;
   }
   
   private void setState(int newState)
   {
      if (newState == this.state)
      {
         return;
      }
      this.state = newState;
      if (newState == 1 && this.getDuration() == 0)
      {
         this.stop();
      }
   }
   
   public static final class Value
   {
      private int value;
   
      public Value()
      {
      }
      
      public int getValue()
      {
         return this.value;
      }
      
      public void setValue(int value)
      {
         this.value = value;
      }
      
   }

}