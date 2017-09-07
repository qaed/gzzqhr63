 package nc.uap.portal.task.ctrl;
 
 import java.util.Collections;
 import java.util.List;
 import nc.uap.lfw.core.comp.text.TextComp;
 import nc.uap.lfw.core.ctrl.IController;
 import nc.uap.lfw.core.data.Dataset;
 import nc.uap.lfw.core.data.Row;
 import nc.uap.lfw.core.event.DataLoadEvent;
 import nc.uap.lfw.core.event.DatasetEvent;
 import nc.uap.lfw.core.event.TextEvent;
 import nc.uap.portal.plugins.PluginManager;
 import nc.uap.portal.plugins.model.PtExtension;
 import nc.uap.portal.util.ToolKit;
 import org.apache.commons.lang.StringUtils;
 import uap.web.bd.pub.BDLanguageHelper;
 
 public class SystemCtl implements IController
 {
   public SystemCtl() {}
   
   public void onDataLoad_sysds(DataLoadEvent dataLoadEvent)
   {
     Dataset ds = (Dataset)dataLoadEvent.getSource();
     List<PtExtension> extensions = PluginManager.newIns().getExtensions("TASK_QRY_PLUGIN");
     
     Row row = ds.getEmptyRow();
     
 
 
     boolean isdef = false;
     String title; if (ToolKit.notNull(extensions)) {
       Collections.sort(extensions, new java.util.Comparator()
       {
    	 @Override
         public int compare(Object ex1, Object ex2)
         {
           if(ex1 instanceof PtExtension && ex2 instanceof PtExtension){
        	   return ((PtExtension)ex2).getId().compareTo(((PtExtension)ex1).getId());
           }
         	return 0;
         }
       });
       title = "";
       for (PtExtension ex : extensions) {
         title = BDLanguageHelper.getStrOnCurLangCode(ex, "title");
         if (!StringUtils.isEmpty(title))
         {
           row = ds.getEmptyRow();
           row.setValue(ds.nameToIndex("id"), ex.getId());
           row.setValue(ds.nameToIndex("title"), title);
           ds.addRow(row);
           
           //20170630 ljw 默认选择HR流程
           //if (ex.getId().equals("wfmtaskqry")) {
           if (ex.getId().equals("hrsswfmqry")) {
             ds.setRowSelectIndex(Integer.valueOf(ds.getRowIndex(row)));
             isdef = true;
           }
         }
       } }
     if (!isdef)
       ds.setRowSelectIndex(Integer.valueOf(0));
   }
   
   public void onAfterNavSelect(DatasetEvent datasetEvent) {
     Dataset ds = (Dataset)datasetEvent.getSource();
     Row currentRow = ds.getSelectedRow();
     String id = currentRow.getString(ds.nameToIndex("id"));
     changeSystem(id);
   }
   
   public void valueChanged(TextEvent textEvent) {
     String system = ((TextComp)textEvent.getSource()).getValue();
     changeSystem(system);
   }
   
   private void changeSystem(String system) { TaskQryParam tp = new TaskQryParam();
     
 
 
     tp.setSystem(system);
     if ("all".equals(system))
       tp.setSystem(null);
     MainViewController main = new MainViewController();
     
 
 
     main.setQryParamIdAndStatus(tp);
     
 
 
     main.setMenuVisable(tp.getStatus());
     
 
 
     main.fillDs(tp, false);
     
     main.clearQuryPlanCach();
   }
 }