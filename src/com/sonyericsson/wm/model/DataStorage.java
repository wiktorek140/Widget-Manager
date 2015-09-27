package com.sonyericsson.wm.model;

import com.sonyericsson.homescreen.idleitem.IdleItemProxy;
import com.sonyericsson.wm.log.Log;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.rms.RecordStore;

public final class DataStorage
{
   private static final String FIELD_DELIMITER = ",";
   private static final String LINE_DELIMITER = ";";
   private static final String REC_STORE = "hsman_db";
   private static final int RECORD_ID = 1;
   private RecordStore rs_RecordStore;

   public static DataStorage getInstance()
   {
      return DataStorage.Holder.INSTANCE;
   }
   
   private DataStorage()
   {
   }
   
   synchronized void loadRunningWidgetsState(Vector runningWidgets) throws IOException
   {
      Log.log("load running state from storage");
      try
      {
         this.openRecordStore();
         int numberOfRecords = 0;
         numberOfRecords = this.rs_RecordStore.getNumRecords();
         if (numberOfRecords > 0)
         {
            byte[] dataBytes = this.rs_RecordStore.getRecord(1);
            String data = new String(dataBytes);
            this.parseDataAndUpdateModel(data, runningWidgets);
         }
      }
      catch (Exception e)
      {
         Log.log("Error when loading model!");
         throw new IOException(e.getClass().getName());
      }
   }
   
   public synchronized void saveRunningWidgetsState(Model model) throws IOException
   {
      Log.log("Saving running state");
      try
      {
         this.openRecordStore();
         int numberOfRecords = 0;
         numberOfRecords = this.rs_RecordStore.getNumRecords();
         byte[] dataBytes = this.createData(model).getBytes();
         if (numberOfRecords == 0)
         {
            this.rs_RecordStore.addRecord(dataBytes, 0, dataBytes.length);
         }
         this.rs_RecordStore.setRecord(1, dataBytes, 0, dataBytes.length);
      }
      catch (Exception e)
      {
         Log.log("Error when saving model!");
         throw new IOException(e.getClass().getName());
      }
   }
   
   private String createData(Model model)
   {
      IdleItemModel itemModel = model.getIdleItemModel();
      StringBuffer data = new StringBuffer();
      int i = 0;
      for (; i < itemModel.size(); i++) 
      {
         IdleItemProxy proxy = itemModel.get(i);
         String name = proxy.getMIDletIdentity().getName();
         Log.log(new StringBuffer("Saving ").append(name).toString());
         data.append(name);
         data.append(",");
      }
      data.append(";");
      IdleItemProxy current = itemModel.getCurrent();
      if (current != null)
      {
         data.append(current.getMIDletIdentity().getName());
      }
      return data.toString();
   }
   
   private void openRecordStore() throws Exception
   {
      this.rs_RecordStore = RecordStore.openRecordStore("hsman_db", true);
   }
   
   private void parseDataAndUpdateModel(String data, Vector runningWidgets)
   {
      int lineIndex = data.indexOf(";");
      if (lineIndex > 0)
      {
         String running = data.substring(0, lineIndex);
         int lastIndex = 0;
         int index = running.indexOf(",");
         while (index > 0)
         {
            String name = running.substring(lastIndex, index);
            Log.log(new StringBuffer("loading ").append(name).toString());
            runningWidgets.addElement(name);
            lastIndex = index + 1;
            index = running.indexOf(",", lastIndex);
         }
      }
   }
   
   static final class Holder
   {
      private static final DataStorage INSTANCE;
   
      static
      {
         INSTANCE = new DataStorage();
      }
      
      private Holder()
      {
      }
      
   }

}