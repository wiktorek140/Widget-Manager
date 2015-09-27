package com.sonyericsson.wm.view;

import com.sonyericsson.wm.util.LabelProvider;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

class ViewCommands implements CommandListener
{
   private Command cmdBack;
   private Command cmdHide;
   private Command cmdManage;
   private Command cmdSelect;
   private Command cmdShow;
   private int currentCmds;
   private String locale;
   private final ViewManager viewManager;

   public ViewCommands(ViewManager viewManager)
   {
      this.currentCmds = 0;
      this.viewManager = viewManager;
      this.locale = LabelProvider.getInstance().getLocale();
      this.createCommands();
   }
   
   public synchronized void commandAction(Command cmd, Displayable displayable)
   {
      if (cmd == this.cmdHide)
      {
         this.viewManager.handleKeyPress(71);
      }
      else if (cmd == this.cmdShow)
      {
         this.viewManager.handleKeyPress(71);
      }
      else if (cmd == this.cmdManage)
      {
         this.viewManager.handleKeyPress(2);
      }
      else if (cmd == this.cmdSelect)
      {
         this.viewManager.handleKeyPress(71);
      }
      else if (cmd == this.cmdBack)
      {
         this.viewManager.handleKeyPress(3);
      }
   }
   
   public synchronized void set(int cmds)
   {
      this.removeFromCanvas(this.currentCmds & (cmds ^ 0xffffffff));
      this.addToCanvas((this.currentCmds ^ 0xffffffff) & cmds);
      this.currentCmds = cmds;
   }
   
   public synchronized void update()
   {
      LabelProvider.getInstance().reset();
      String currentLocale = LabelProvider.getInstance().getLocale();
      if (!currentLocale.equals(this.locale))
      {
         this.locale = currentLocale;
         this.removeFromCanvas(this.currentCmds);
         this.createCommands();
         this.addToCanvas(this.currentCmds);
      }
   }
   
   private void addToCanvas(int cmds)
   {
      if ((cmds & 1) == 1)
      {
         this.viewManager.addCommand(this.cmdBack);
      }
      if ((cmds & 2) == 2)
      {
         this.viewManager.addCommand(this.cmdHide);
      }
      if ((cmds & 4) == 4)
      {
         this.viewManager.addCommand(this.cmdManage);
      }
      if ((cmds & 8) == 8)
      {
         this.viewManager.addCommand(this.cmdSelect);
      }
      if ((cmds & 0x10) == 16)
      {
         this.viewManager.addCommand(this.cmdShow);
      }
   }
   
   private void createCommands()
   {
      LabelProvider labelProvider = LabelProvider.getInstance();
      this.cmdBack = new Command(labelProvider.getLabel(3), 2, 0);
      this.cmdHide = new Command(labelProvider.getLabel(4), 1, 0);
      this.cmdManage = new Command(labelProvider.getLabel(5), 1, 1);
      this.cmdSelect = new Command(labelProvider.getLabel(2), 1, 0);
      this.cmdShow = new Command(labelProvider.getLabel(1), 1, 0);
   }
   
   private void removeFromCanvas(int cmds)
   {
      if ((cmds & 1) == 1)
      {
         this.viewManager.removeCommand(this.cmdBack);
      }
      if ((cmds & 2) == 2)
      {
         this.viewManager.removeCommand(this.cmdHide);
      }
      if ((cmds & 4) == 4)
      {
         this.viewManager.removeCommand(this.cmdManage);
      }
      if ((cmds & 8) == 8)
      {
         this.viewManager.removeCommand(this.cmdSelect);
      }
      if ((cmds & 0x10) == 16)
      {
         this.viewManager.removeCommand(this.cmdShow);
      }
   }
   
}