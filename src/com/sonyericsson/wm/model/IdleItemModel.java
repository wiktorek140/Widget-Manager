package com.sonyericsson.wm.model;

import com.sonyericsson.homescreen.idleitem.IdleItemProxy;
import java.util.Vector;

public final class IdleItemModel
{
   private final Vector items;
   private IdleItemProxy current;
   private boolean proxyInFocus;

   public IdleItemModel()
   {
      this.items = new Vector();
   }
   
   public boolean isEmpty()
   {
      return this.items.isEmpty();
   }
   
   public int size()
   {
      return this.items.size();
   }
   
   public IdleItemProxy getCurrent()
   {
      return this.current;
   }
   
   public IdleItemProxy getNext()
   {
      if (this.size() > 1)
      {
         int currentIndex = this.items.indexOf(this.current);
         int nextIndex = (currentIndex + 1) % this.size();
         return this.get(nextIndex);
      }
      return null;
   }
   
   public IdleItemProxy getPrevious()
   {
      if (this.size() > 1)
      {
         int currentIndex = this.items.indexOf(this.current);
         int previousIndex = (currentIndex + this.size() - 1) % this.size();
         return this.get(previousIndex);
      }
      return null;
   }
   
   public void setCurrent(IdleItemProxy proxy)
   {
      this.current = proxy;
   }
   
   public IdleItemProxy get(int index)
   {
      return (IdleItemProxy) this.items.elementAt(index);
   }
   
   public void add(IdleItemProxy proxy)
   {
      if (!this.items.contains(proxy))
      {
         this.items.addElement(proxy);
         Listeners.getInstance().notifyListeners();
      }
   }
   
   public void remove(IdleItemProxy proxy)
   {
      this.items.removeElement(proxy);
   }
   
   public synchronized boolean proxyHasFocus()
   {
      return this.proxyInFocus;
   }
   
   public synchronized void setProxyInFocus(boolean proxyInFocus)
   {
      this.proxyInFocus = proxyInFocus;
   }
   
}