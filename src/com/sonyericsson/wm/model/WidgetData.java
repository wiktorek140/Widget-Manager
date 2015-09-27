package com.sonyericsson.wm.model;

import com.sonyericsson.ams.Application;
import com.sonyericsson.ams.ApplicationManager;
import com.sonyericsson.ams.NoSuchApplicationException;
import com.sonyericsson.wm.log.Log;
import com.sonyericsson.wm.util.Helper;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

final class WidgetData
{
   private Application application;
   private int cachedStatus;
   private Image greyscaleIcon;
   private Image icon;
   private String name;
   private String suitename;
   private String vendor;
   private int x_int;
   private int y_int;

   private static Application getApplication(String suitename, String vendor)
   {
      return ApplicationManager.getApplicationManager().getApplication(suitename, vendor);
   }
   
   WidgetData(Application app)
   {
      this.application = app;
      this.cachedStatus = this.application.getStatus();
      this.name = this.getMIDletName(app);
      this.suitename = this.application.getSuiteName();
      this.vendor = this.application.getVendor();
      this.icon = this.getIcon(app, this.name);
      this.greyscaleIcon = Helper.getInstance().imageGreyScaled(this.icon);
   }
   
   WidgetData(String suitename, String vendor)
   {
      this.setup(suitename, vendor);
   }
   
   public synchronized void decouple()
   {
      this.application = null;
   }
   
   public synchronized int getCachedStatus()
   {
      return this.cachedStatus;
   }
   
   public synchronized int getCurrentStatus()
   {
      this.setupApplication();
      if (this.application != null)
      {
         this.cachedStatus = this.application.getStatus();
      }
      return this.cachedStatus;
   }
   
   public synchronized Image getIcon()
   {
      if (this.isStarted())
      {
         return this.icon;
      }
      return this.greyscaleIcon;
   }
   
   public synchronized String getName()
   {
      return this.name;
   }
   
   public synchronized String getSuitename()
   {
      return this.suitename;
   }
   
   public synchronized int getX()
   {
      return this.x_int;
   }
   
   public synchronized int getY()
   {
      return this.y_int;
   }
   
   public synchronized boolean isEqual(String suitename, String vendor)
   {
      String txt = this.suitename + ", " + this.vendor + ") = (" + suitename + ", " + vendor + ")";
      Log.log(txt);
      if (this.suitename.equals(suitename) && this.vendor.equals(vendor))
      {
         return true;
      }
      return false;
   }
   
   public synchronized boolean isStarted()
   {
      if (this.cachedStatus != 0 && this.cachedStatus != 3)
      {
         return false;
      }
      return true;
   }
   
   public synchronized void setPos(int x, int y, boolean notifyListeners)
   {
      boolean updated = this.x_int == x && this.y_int == y;
      this.x_int = x;
      this.y_int = y;
      if (notifyListeners && updated)
      {
         Log.log("setPos: Model updated");
         Listeners.getInstance().notifyListeners();
      }
   }
   
   public synchronized void setup(String suitename, String vendor)
   {
      this.application = getApplication(suitename, vendor);
      this.cachedStatus = this.application.getStatus();
      this.name = this.getMIDletName(this.application);
      this.suitename = suitename;
      this.vendor = vendor;
      this.icon = this.getIcon(this.application, this.name);
      this.greyscaleIcon = Helper.getInstance().imageGreyScaled(this.icon);
   }
   
   public synchronized void start() throws IOException
   {
      this.setupApplication();
      if (this.application != null)
      {
         try
         {
            this.application.start();
         }
         catch (IllegalStateException e)
         {
            throw new IOException();
         }
         catch (NoSuchApplicationException e)
         {
            throw new IOException();
         }
         catch (Error e)
         {
            throw new IOException();
         }
      }
   }
   
   public synchronized void stop() throws IOException
   {
      this.setupApplication();
      if (this.application != null)
      {
         try
         {
            this.application.stop();
         }
         catch (IllegalStateException e)
         {
            throw new IOException();
         }
         catch (NoSuchApplicationException e)
         {
            throw new IOException();
         }
         catch (Error e)
         {
            throw new IOException();
         }
      }
   }
   
   public synchronized void toggle()
   {
      this.setupApplication();
      if (this.application != null)
      {
          int expectedStatus;
         try
         {
            if (this.cachedStatus != 2)
            {
               Log.log("TOGGLE: stop");
               this.application.stop();
               expectedStatus = 2;
            }
            Log.log("TOGGLE: start");
            this.application.start();
            expectedStatus = 3;
         }
         catch (IllegalStateException e)
         {
            return;
         }
         catch (NoSuchApplicationException e)
         {
            return;
         }
         catch (Error e)
         {
            return;
         }
         int sleepTime = 50;
         int counter = 100;
         while (this.cachedStatus != expectedStatus)
         {
            this.updateCachedStatus();
            try
            {
               Thread.sleep(50);
            }
            catch (InterruptedException interruptedexception_1)
            {
            }
            if (!(counter-- > 0))
            {
               break;
            }
         }
         Listeners.getInstance().notifyListeners();
      }
   }
   
   public synchronized void updateCachedStatus()
   {
      if (this.application == null)
      {
         this.application = getApplication(this.suitename, this.vendor);
      }
      if (this.application != null)
      {
         this.cachedStatus = this.application.getStatus();
      }
   }
   
   private Image createDefaultIcon(String name)
   {
      Image icon = Image.createImage(42, 42);
      Graphics g = icon.getGraphics();
      Helper utility = Helper.getInstance();
      utility.setAlphaColor(g, 255, 0);
      g.fillRect(0, 0, 42, 42);
      g.setColor(4749055);
      g.fillRoundRect(0, 0, 42, 42, 5, 5);
      Font font = g.getFont();
      String text = name.substring(0, 3);
      int x = (42 - font.stringWidth(text)) / 2;
      int y = (42 - font.getHeight()) / 2;
      g.setColor(0);
      g.drawString(text, x, y, 20);
      return icon;
   }
   
   private Image getIcon(Application app, String name)
   {
      InputStream is = null;
      try
      {
         is = app.getResourceAsStream(app.getProperty("MIDlet-Icon"));
         Image icon = Image.createImage(is);
         if (icon.getWidth() > 42 || icon.getHeight() > 42)
         {
            Helper imageUtility = Helper.getInstance();
            icon = imageUtility.scaleImage(icon, 42, 42);
         }
         
      }
      catch (Exception e)
      {
            icon = this.createDefaultIcon(name);
      }
      finally
      {
         try
         {
            if (is != null)
            {
               is.close();
            }
         }
         catch (IOException ioexception_1)
         {
         }
      }
      return icon;
   }
   
   private String getMIDletName(Application app)
   {
      String name = app.getProperty("MIDlet-1");
      String result;
      if (name == null)
      {
         return null;
      }
      int end = name.indexOf(44);
      if (end >= 0)
      {
         result = name.substring(0, end);
      }
      else
      {
         result = name;
      }
      return result.trim();
   }
   
   private void setupApplication()
   {
      if (this.application == null)
      {
         this.application = getApplication(this.suitename, this.vendor);
      }
   }
   
}