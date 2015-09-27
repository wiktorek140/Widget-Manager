package com.sonyericsson.wm.util;

import javax.microedition.global.ResourceManager;

public final class LabelProvider
{
   public static final int BACK_LABEL = 3;
   public static final int HIDE_LABEL = 4;
   public static final int MANAGE_LABEL = 5;
   public static final int SELECT_LABEL = 2;
   public static final int SHOW_LABEL = 1;
   public static final int SHOW_WIDGETS_HLP_LABEL = 6;
   private ResourceManager resourceManager;

   public static LabelProvider getInstance()
   {
      return LabelProvider.Holder.INSTANCE;
   }
   
   private LabelProvider()
   {
      this.reset();
   }
   
   public String getLabel(int label)
   {
      return this.resourceManager.getString(label);
   }
   
   public String getLocale()
   {
      return this.resourceManager.getLocale();
   }
   
   public void reset()
   {
      this.resourceManager = ResourceManager.getManager("HSMANAGER");
   }
   
   static final class Holder
   {
      private static final LabelProvider INSTANCE;
   
      static
      {
         INSTANCE = new LabelProvider();
      }
      
      private Holder()
      {
      }
      
   }

}