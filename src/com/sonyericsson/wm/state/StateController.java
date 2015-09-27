package com.sonyericsson.wm.state;

import com.sonyericsson.homescreen.idleitem.IdleItemProxy;
import com.sonyericsson.wm.log.Log;
import com.sonyericsson.wm.model.Model;
import com.sonyericsson.wm.tween.Animator;

public final class StateController
{
   private State currentState;
   private final State focusedState;
   private final State managementState;
   private final Model model;
   private final State navigationState;
   private final State standbyState;
   private boolean transitionOngoing;

   public StateController(Model model, State focusedState, State managementState, State navigationState, State standbyState)
   {
      this.model = model;
      this.focusedState = focusedState;
      this.managementState = managementState;
      this.navigationState = navigationState;
      this.standbyState = standbyState;
      focusedState.setController(this);
      managementState.setController(this);
      navigationState.setController(this);
      standbyState.setController(this);
      this.changeState(standbyState);
   }
   
   public void keyPressed(int key)
   {
      if (this.transitionOngoing)
      {
         return;
      }
      this.currentState.keyPressed(key);
   }
   
   public void changeState(int homescreenKey)
   {
      switch (homescreenKey) 
      {
         case 7:
            this.handleRedKey();
            break;
         case 9:
            this.handleNavigationalUp();
            break;
         case 13:
            this.handleNavigationalDown();
            break;
         case 71:
            this.handleSoftkeyMiddle();
            break;
         case 3:
            this.handleBackKey();
            break;
         case 2:
            this.handleSoftkeyLeft();
            break;
      }
   }
   
   public void changeStateToFocused()
   {
      this.changeState(this.focusedState);
   }
   
   public void changeStateToNavigation()
   {
      this.changeState(this.navigationState);
   }
   
   public void changeStateToStandby()
   {
      this.changeState(this.standbyState);
   }
   
   public State getCurrentState()
   {
      return this.currentState;
   }
   
   public void hideNotify()
   {
      if (this.currentState != this.focusedState)
      {
         this.changeState(7);
      }
   }
   
   public void showNotify()
   {
      this.currentState.showNotify();
   }
   
   public void traverseOut(IdleItemProxy proxy, int dir)
   {
      if (this.currentState == this.focusedState)
      {
         this.changeState(this.navigationState);
      }
   }
   
   public void traverseAborted()
   {
      this.transitionOngoing = false;
   }
   
   private void changeState(State newState)
   {
      Log.log(this.getStateName(this.currentState) + " -> " + this.getStateName(newState) + ")");
      try
      {
         if (this.currentState != newState)
         {
            Animator animator = new Animator();
            if (this.currentState != null)
            {
               this.currentState.leave(animator);
            }
            this.currentState = newState;
            this.currentState.enter(animator);
            animator.start();
            this.transitionOngoing = false;
         }
      }
      catch (Throwable t)
      {
         Log.log("Exception in changeState!");
      }
      Log.log("StateController.changestate() - DONE");
   }
   
   private String getStateName(State state)
   {
      if (state == null)
      {
         return "null";
      }
      if (state == this.navigationState)
      {
         return "navigationState";
      }
      if (state == this.focusedState)
      {
         return "focusedState";
      }
      if (state == this.standbyState)
      {
         return "standbyState";
      }
      if (state == this.managementState)
      {
         return "managementState";
      }
      return "unknown";
   }
   
   private void handleBackKey()
   {
      if (this.currentState == this.navigationState)
      {
         this.changeState(this.standbyState);
      }
      else if (this.currentState == this.managementState)
      {
         IdleItemProxy current = this.model.getIdleItemModel().getCurrent();
         if (current != null)
         {
            this.changeState(this.navigationState);
         }
         else
         {
            this.changeState(this.standbyState);
         }
      }
      else if (this.currentState == this.focusedState)
      {
         this.changeState(this.navigationState);
      }
   }
   
   private void handleNavigationalDown()
   {
      if (this.currentState == this.navigationState)
      {
         this.changeState(this.standbyState);
      }
   }
   
   private void handleNavigationalUp()
   {
      if (this.currentState == this.standbyState)
      {
         if (this.model.getIdleItemModel().isEmpty())
         {
            this.changeState(this.managementState);
         }
         else
         {
            this.changeState(this.navigationState);
         }
      }
   }
   
   private void handleRedKey()
   {
      this.changeState(this.standbyState);
   }
   
   private void handleSoftkeyLeft()
   {
      if (this.currentState == this.navigationState)
      {
         this.changeState(this.managementState);
      }
   }
   
   private void handleSoftkeyMiddle()
   {
      if (this.currentState == this.navigationState)
      {
         this.transitionOngoing = true;
         this.model.getIdleItemModel().getCurrent().focus(0);
      }
   }
   
}