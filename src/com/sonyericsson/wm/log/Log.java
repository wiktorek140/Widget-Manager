package com.sonyericsson.wm.log;

public final class Log
{
   private static boolean enabled;

   static
   {
      enabled = true;
   }
   
   public static void log(String log)
   {
      if (enabled)
      {
         System.out.print("[WidgetManager] ");
         System.out.println(log);
      }
   }
   
   public static void setEnabled(boolean enabled)
   {
      enabled = enabled;
   }
   
   private Log()
   {
   }
   
}