package com.sonyericsson.wm.app;

import com.sonyericsson.ams.Application;
import com.sonyericsson.ams.ApplicationManager;
import com.sonyericsson.ams.NoSuchApplicationException;
import com.sonyericsson.homescreen.Homescreen;
import com.sonyericsson.homescreen.HomescreenMIDlet;
import com.sonyericsson.wm.log.Log;
import com.sonyericsson.wm.model.Model;
import com.sonyericsson.wm.model.WidgetModel;
import com.sonyericsson.wm.state.FocusedState;
import com.sonyericsson.wm.state.ManagementState;
import com.sonyericsson.wm.state.NavigationState;
import com.sonyericsson.wm.state.StandbyState;
import com.sonyericsson.wm.state.StateController;
import com.sonyericsson.wm.view.FocusedView;
import com.sonyericsson.wm.view.ManagementView;
import com.sonyericsson.wm.view.NavigationView;
import com.sonyericsson.wm.view.StandbyView;
import com.sonyericsson.wm.view.ViewBase;
import com.sonyericsson.wm.view.ViewManager;
import java.util.Vector;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDletStateChangeException;

public final class App extends HomescreenMIDlet
{
   private final Controller controller;
   private final Thread widgetStartupThread;

   public App()
   {
      try
      {
         String version = this.getAppProperty("MIDlet-Version");
         if (version == null)
         {
            version = "unknown";
         }
         Log.log(version + " delivery");
         String log = this.getAppProperty("SEMC-WidgetManager-LogEnabled");
         Log.setEnabled("Y".equalsIgnoreCase(log));
         Homescreen homescreen = this.getHomescreen();
         final Model model = new Model();
         Display display = Display.getDisplay(this);
         final ViewManager manager = new ViewManager(display);
         ViewBase standbyView = new StandbyView(display, homescreen, manager, model);
         final StandbyState standbyState = new StandbyState(homescreen, model, standbyView);
         ViewBase navigationView = new NavigationView(display, homescreen, manager, model);
         NavigationState navigationState = new NavigationState(homescreen, model, navigationView);
         ViewBase focusedView = new FocusedView(display, homescreen, manager, model);
         FocusedState focusedState = new FocusedState(homescreen, model, focusedView);
         ViewBase managementView = new ManagementView(display, homescreen, manager, model);
         ManagementState managementState = new ManagementState(homescreen, model, managementView);
         StateController stateController = new StateController(model, focusedState, managementState, navigationState, standbyState);
         this.controller = new Controller(homescreen, model, manager, stateController);
         this.widgetStartupThread = new Thread()
      {
      
         public void run()
         {
            Log.log("Initial loading of widgets begin...");
            try
            {
               model.getWidgetModel().setWidgets(scanForWidgets());
               model.restoreRunningState();
               synchronized (controller.exitingLock)
               {
                  if (!controller.exiting)
                  {
                     standbyState.updateArrowShown();
                     standbyState.showDateTimeInfo();
                  }
               }
               manager.setController(controller);
               Log.log("Initial loading of widgets done...");
            }
            catch (InterruptedException e)
            {
               Log.log("Initial loading of widgets interrupted");
            }
         }
         
      };
         this.widgetStartupThread.start();
      }
      catch (Throwable e)
      {
         Log.log(" " + e.getMessage());
         throw new RuntimeException();
      }
   }
   
   private Vector scanForWidgets()
   {
      Vector widgets = new Vector(16);
      try
      {
         ApplicationManager applicationManager = ApplicationManager.getApplicationManager();
         Application[] apps = applicationManager.getApplications();
         int i = 0;
         for (; i < apps.length; i++) 
         {
            Application app = apps[i];
            if (!this.isWidget(app))
            {
            }
            else
            {
               if (app.getStatus() != 2)
               {
                  try
                  {
                     Log.log(new StringBuffer("Stopping ").append(app.getSuiteName()).toString());
                     app.stop();
                  }
                  catch (IllegalStateException illegalstateexception_1)
                  {
                  }
                  catch (NoSuchApplicationException nosuchapplicationexception_1)
                  {
                  }
                  catch (Error error_1)
                  {
                  }
               }
               WidgetModel.addWidgetToVector(widgets, app);
            }
         }
      }
      catch (Exception e)
      {
         Log.log(new StringBuffer("WidgetLoader.load():").append(e.getClass().getName()).toString());
      }
      return widgets;
   }
   
   private boolean isWidget(Application app)
   {
      return "idlescreen".equalsIgnoreCase(app.getProperty("MIDlet-Category-1"));
   }
   
   protected void destroyApp(boolean unconditional) throws MIDletStateChangeException
   {
      if (this.widgetStartupThread.isAlive())
      {
         this.widgetStartupThread.interrupt();
      }
      this.controller.destroyApp(unconditional);
   }
   
   protected void pauseApp()
   {
   }
   
   protected void startApp() throws MIDletStateChangeException
   {
   }

   
}