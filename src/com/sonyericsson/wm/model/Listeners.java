package com.sonyericsson.wm.model;

import java.util.Vector;

final class Listeners
{
   private final Vector listeners;

   public static Listeners getInstance()
   {
      return Listeners.Holder.INSTANCE;
   }
   
   private Listeners()
   {
      this.listeners = new Vector(4);
   }
   
   public void add(ModelListener listener)
   {
      synchronized (this.listeners)
      {
         if (!this.listeners.contains(listener))
         {
            this.listeners.addElement(listener);
         }
      }
   }
   
   void notifyListeners()
   {
      synchronized (this.listeners)
      {
         int i = 0;
         for (; i < this.listeners.size(); i++) 
         {
            ((ModelListener) this.listeners.elementAt(i)).modelUpdated();
         }
      }
   }
   
   static final class Holder
   {
      private static final Listeners INSTANCE;
   
      static
      {
         INSTANCE = new Listeners();
      }
      
      private Holder()
      {
      }
      
   }

}