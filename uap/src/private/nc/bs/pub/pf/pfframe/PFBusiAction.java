 package nc.bs.pub.pf.pfframe;
 
 import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.integration.common.logging.IntegrationLogger;
import nc.bs.integration.workitem.util.SyncWorkitemUtil;
import nc.bs.logging.Logger;
import nc.bs.pf.pub.BillTypeCacheKey;
import nc.bs.pf.pub.PFRequestDataCacheProxy;
import nc.bs.pf.pub.PfDataCache;
import nc.bs.pf.pub.cache.CondStringKey;
import nc.bs.pf.pub.cache.CondVOKey;
import nc.bs.pf.pub.cache.ICacheDataQueryCallback;
import nc.bs.pf.pub.cache.IRequestDataCacheKey;
import nc.bs.pub.compiler.IWorkFlowRet;
import nc.bs.pub.compiler.IWorkflowBatch;
import nc.bs.pub.pf.IBusiBillStatusCallBack;
import nc.bs.pub.pf.IPfPersonFilter2;
import nc.bs.pub.pf.JumpStatusCallbackContext;
import nc.bs.pub.pf.PfMessageUtil;
import nc.bs.pub.pf.PfUtilDMO;
import nc.bs.pub.pf.PfUtilTools;
import nc.bs.pub.pf.bservice.IPFActionConstrict;
import nc.bs.pub.wfengine.impl.WorkflowMachineImpl;
import nc.bs.pub.workflownote.WorknoteManager;
import nc.bs.wfengine.engine.WfInstancePool;
import nc.impl.uap.pf.PFMessageImpl;
import nc.itf.uap.pf.IPFBusiAction;
import nc.itf.uap.pf.IPFConfig;
import nc.itf.uap.pf.IPFExptLog;
import nc.itf.uap.pf.IPfExchangeService;
import nc.itf.uap.pf.IWorkflowMachine;
import nc.itf.uap.pf.metadata.IFlowBizItf;
import nc.jdbc.framework.exception.DbException;
import nc.message.util.MessageCenter;
import nc.message.vo.AttachmentVO;
import nc.message.vo.MessageVO;
import nc.message.vo.NCMessage;
import nc.uap.pf.metadata.PfMetadataTools;
import nc.vo.integration.workitem.WorkitemVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pf.change.IActionDriveChecker;
import nc.vo.pf.change.IChangeVOCheck;
import nc.vo.pf.change.PfUtilBaseTools;
import nc.vo.pf.exptlog.PfExptLogVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.pub.billtype2.Billtype2VO;
import nc.vo.pub.billtype2.ExtendedClassEnum;
import nc.vo.pub.change.PublicHeadVO;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.msg.MessageinfoVO;
import nc.vo.pub.pf.IPFSourceBillFinder;
import nc.vo.pub.pf.PfUtilActionVO;
import nc.vo.pub.pf.Pfi18nTools;
import nc.vo.pub.pf.SourceBillInfo;
import nc.vo.pub.pfflow01.BillbusinessVO;
import nc.vo.pub.pfflow04.BackmsgVO;
import nc.vo.pub.workflownote.WorkflownoteAttVO;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.uap.pf.PFBatchExceptionInfo;
import nc.vo.uap.pf.PFBusinessException;
import nc.vo.wfengine.definition.WorkflowTypeEnum;
import nc.vo.wfengine.pub.WfTaskOrInstanceStatus;
import nc.vo.wfengine.pub.WfTaskType;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class PFBusiAction
   implements IPFBusiAction
 {
   private PfParameterVO lastParamVO = null;
   
 
 
 
 
 
 
 
 
   public PFBusiAction() {}
   
 
 
 
 
 
 
 
 
   private Object[] startApproveFlowAfterAction(String billtype, AggregatedValueObject billVO, Object userObj, Object retObj, HashMap eParam, Hashtable hashBilltypeToParavo, Hashtable hashMethodReturn, String src_billtypePK)
     throws BusinessException
   {
     Logger.debug("************尝试 启动审批流*************");
     boolean isNeedStart = true;
     boolean bStarted = false;
     Object noApprove = eParam == null ? null : eParam.get("nosendmessage");
     if (noApprove != null) {
       Logger.debug(">>PARAM_NOFLOW");
       isNeedStart = false;
       
 
       PfParameterVO paraVo = (PfParameterVO)hashBilltypeToParavo.get(billtype + src_billtypePK);
       if ((paraVo.m_billVersionPK != null) && (paraVo.m_billNo != null)) {
         new WorkflowMachineImpl().deleteWhenStartExceptPassOrGoing(paraVo, WorkflowTypeEnum.Approveflow.getIntValue());
       }
     }
     
     String startupTrace = "************审批流不可启动*************";
     if (isNeedStart) {
       PfParameterVO paraVo = (PfParameterVO)hashBilltypeToParavo.get(billtype + src_billtypePK);
       if (paraVo == null)
       {
         paraVo = (PfParameterVO)hashBilltypeToParavo.get(billtype);
       }
       PfUtilBaseTools.fetchBillId(paraVo, billVO, retObj);
       
       if ((paraVo.m_billVersionPK != null) && (paraVo.m_billNo != null))
       {
 
         boolean[] wfRet = ((IWorkflowMachine)NCLocator.getInstance().lookup(IWorkflowMachine.class)).sendWorkFlowOnSaveWithFinishJudge(paraVo, hashMethodReturn, eParam);
         bStarted = wfRet[0];
         
 
         if (wfRet[1])
         {
 
 
           paraVo.m_autoApproveAfterCommit = true;
           
 
           retObj = actionOnStep("APPROVE", paraVo);
           
           actiondrive(billVO, userObj, hashBilltypeToParavo, hashMethodReturn, paraVo, eParam);
           bStarted = true;
         }
       }
       
 
 
 
 
 
       if (bStarted)
       {
         PfMessageUtil.setHandled(paraVo.m_workFlow);
         startupTrace = "************审批流成功启动*************";
       } else {
         startupTrace = "************单据id或单据号为空,不能启动审批流*************";
       }
     }
     Logger.warn(startupTrace);
     return new Object[] { Boolean.valueOf(bStarted), retObj };
   }
   
 
 
   private boolean startWorkflowAfterAction(String billtype, AggregatedValueObject billVO, Object userObj, Object retObj, HashMap eParam, Hashtable hashBilltypeToParavo, Hashtable hashMethodReturn, String src_billtypePK)
     throws BusinessException
   {
     Logger.debug("************尝试 启动工作流*************");
     boolean isNeedStart = true;
     boolean bStarted = false;
     Object noWorkflow = eParam == null ? null : eParam.get("nosendmessage");
     if (noWorkflow != null) {
       Logger.debug(">>PARAM_NOFLOW");
       isNeedStart = false;
     }
     
     String startupTrace = "************工作流不可启动*************";
     if (isNeedStart) {
       PfParameterVO paraVo = (PfParameterVO)hashBilltypeToParavo.get(billtype + src_billtypePK);
       if (paraVo == null)
       {
         paraVo = (PfParameterVO)hashBilltypeToParavo.get(billtype);
       }
       
       PfUtilBaseTools.fetchBillId(paraVo, billVO, retObj);
       
       if ((paraVo.m_billVersionPK != null) && (paraVo.m_billNo != null))
       {
         boolean[] wfRet = ((IWorkflowMachine)NCLocator.getInstance().lookup(IWorkflowMachine.class)).sendWorkFlowOnSaveWithFinishJudge(paraVo, hashMethodReturn, eParam);
         bStarted = wfRet[0];
         
         if (bStarted)
           startupTrace = "************工作流成功启动*************";
       } else {
         startupTrace = "************单据id或单据号为空,不能启动工作流*************";
       }
     }
     Logger.warn(startupTrace);
     return bStarted;
   }
   
 
 
   private void sendMessageWhenStartWorkflow(PfParameterVO paraVo, int iworkflowtype)
     throws BusinessException
   {
     WorknoteManager manager = new WorknoteManager();
     String processId = manager.getProcessId(paraVo, iworkflowtype);
     if (StringUtil.isEmptyWithTrim(processId)) {
       return;
     }
     manager.sendMsgWhenWFstateChanged(paraVo, processId, WfTaskOrInstanceStatus.Started.getIntValue(), iworkflowtype);
   }
   
 
 
   private void deleteWorkFlow(PfParameterVO paraVo)
     throws BusinessException
   {
     new WorkflowMachineImpl().deleteCheckFlow(paraVo.m_billType, paraVo.m_billVersionPK, paraVo.m_preValueVo, paraVo.m_operator);
   }
   
   private boolean isDriveAction(String pkBilltype, String actionName) throws PFBusinessException {
     IRequestDataCacheKey key = new CondStringKey("pfbusiaction_isdriveaction", new String[] { actionName });
     
 
 
 
     boolean isDrive = false;
     Object cachedObj = PFRequestDataCacheProxy.get(key);
     
     if ((cachedObj != null) && ((cachedObj instanceof Boolean))) {
       isDrive = ((Boolean)cachedObj).booleanValue();
     } else {
       isDrive = isDriveAction_Db(pkBilltype, actionName);
       PFRequestDataCacheProxy.put(key, Boolean.valueOf(isDrive));
     }
     
     return isDrive;
   }
   
 
 
 
 
 
 
 
   private boolean isDriveAction_Db(String pkBilltype, String actionName)
     throws PFBusinessException
   {
     boolean retflag = true;
     Logger.debug("****判断动作" + actionName + "是否结束动作isDriveAction开始****");
     try {
       PfUtilDMO dmo = new PfUtilDMO();
       String realBilltype = PfUtilBaseTools.getRealBilltype(pkBilltype);
       retflag = dmo.queryLastStep(realBilltype, actionName);
       Logger.debug("==" + (retflag ? "是" : "不是") + "结束动作==");
     } catch (DbException e) {
       Logger.error(e.getMessage(), e);
       throw new PFBusinessException(e.getMessage());
     }
     Logger.debug("****判断动作" + actionName + "是否结束动作isDriveAction结束****");
     return retflag;
   }
   
 
 
 
 
 
 
 
   private Object actionOnStep(String actionName, PfParameterVO paraVo)
     throws BusinessException
   {
     Logger.debug(">>>PFBusiAction.actionOnStep(" + actionName + "," + paraVo.m_billType + ") 开始<<<");
     long begin = System.currentTimeMillis();
     
 
     Object actionReturnObj = null;
     
 
     if ((paraVo.m_billVersionPK == null) && (paraVo.m_preValueVo != null)) {
       paraVo.m_billVersionPK = paraVo.m_preValueVo.getParentVO().getPrimaryKey();
       Logger.debug("*********单据驱动保存、审核(获得驱动时单据主键)*****");
     }
     actionReturnObj = new PFRunClass().runComBusi(paraVo, UFBoolean.FALSE, actionName);
     
     long end = System.currentTimeMillis();
     Logger.info(">>>PFBusiAction.actionOnStep(" + actionName + "," + paraVo.m_billType + ") 结束,耗时=" + (end - begin) + "ms<<<");
     return actionReturnObj;
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
   private boolean actiondrive(AggregatedValueObject srcBillVO, Object userObj, Hashtable hashBilltypeToParavo, Hashtable hashMethodReturn, PfParameterVO srcParaVo, HashMap eParam)
     throws BusinessException
   {
     Logger.debug("*********执行动作驱动actiondrive开始********");
     
     PfUtilActionVO[] drivedActions = queryActionDriveVOs(srcParaVo);
     String srcBilltype = srcParaVo.m_billType;
     
     if ((drivedActions == null) || (drivedActions.length == 0)) {
       Logger.debug("该动作=" + srcParaVo.m_actionName + "没有可驱动的动作");
       return false;
     }
     
 
     LinkedHashSet<String> hsFinishedDriveAction = new LinkedHashSet();
     
     LinkedHashSet<String> hsFlowStartedBilltypes = new LinkedHashSet();
     
 
 
     AggregatedValueObject[] destVos = null;
     
     String srcOperator = srcParaVo.m_operator;
     
 
 
     for (int i = 0; i < drivedActions.length; i++)
     {
 
       boolean isLastDriveAction = true;
       
 
 
 
       AggregatedValueObject srcBillCloneVO = srcBillVO;
       
 
       String destBillType = drivedActions[i].getBillType();
       
       String beDrivedActionName = drivedActions[i].getActionName();
       
       String currentExecDrive = destBillType + ":" + beDrivedActionName;
       Logger.debug("执行驱动" + currentExecDrive + "开始");
       if (hsFinishedDriveAction.contains(currentExecDrive)) {
         Logger.debug("被驱动动作:" + currentExecDrive + "已执行,继续循环操作.");
 
 
       }
       else if (hsFlowStartedBilltypes.contains(destBillType))
       {
         Logger.debug("被驱动单据类型启动了审批流（或工作流），不执行驱动" + currentExecDrive);
       }
       else
       {
         PFActionConstrict aConstrict = new PFActionConstrict();
         boolean isPermit = aConstrict.actionConstrictBeforeDrive(drivedActions[i].getPkMessageDrive(), destBillType, beDrivedActionName, srcParaVo);
         if (!isPermit) {
           Logger.debug("当前驱动" + currentExecDrive + "的约束 不满足，继续下个驱动");
         }
         else {
           hsFinishedDriveAction.add(currentExecDrive);
           
 
           Object checkClzInstance = PfUtilTools.getBizRuleImpl(srcBilltype);
           if ((checkClzInstance != null) && ((checkClzInstance instanceof IActionDriveChecker))) {
             boolean isPermited = ((IActionDriveChecker)checkClzInstance).isEnableDrive(srcBilltype, srcBillCloneVO, srcParaVo.m_actionName, destBillType, beDrivedActionName);
             if (!isPermited) {
               Logger.debug("当前驱动" + currentExecDrive + "的校验不满足，继续下个驱动");
               continue;
             }
           }
           
 
           PfParameterVO destParaVo = null;
           
           String src_billtypePK = StringUtil.isEmptyWithTrim(srcBillVO.getParentVO().getPrimaryKey()) ? "" : srcBillVO.getParentVO().getPrimaryKey();
           if ((hashBilltypeToParavo.containsKey(destBillType + src_billtypePK)) || (PfUtilBaseTools.getRealBilltype(srcBilltype).equals(PfUtilBaseTools.getRealBilltype(destBillType))))
           {
             Logger.debug("驱动所需的paraVo已存在，无需VO交换");
             destParaVo = (PfParameterVO)hashBilltypeToParavo.get(destBillType + src_billtypePK);
             if ((destParaVo == null) && (PfUtilBaseTools.getRealBilltype(srcBilltype).equals(PfUtilBaseTools.getRealBilltype(destBillType)))) {
               destParaVo = srcParaVo.clone();
               hashBilltypeToParavo.put(destBillType + src_billtypePK, destParaVo);
             }
             if (destParaVo.m_preValueVos == null) {
               destVos = new AggregatedValueObject[] { destParaVo.m_preValueVo };
             } else
               destVos = destParaVo.m_preValueVos;
             destParaVo.m_splitValueVos = destVos;
           } else {
             Logger.debug("不存在被驱动单据VO,则进行以源单据为准的VO数据转换");
             
             if ((checkClzInstance instanceof IChangeVOCheck)) {
               boolean bValid = ((IChangeVOCheck)checkClzInstance).checkValidOrNeed(srcBillCloneVO, srcParaVo.m_actionName, destBillType, beDrivedActionName);
               if (!bValid) {
                 Logger.debug("源单据VO不允许数据转换，则继续下个驱动");
                 continue;
               }
             }
             
 
             IPfExchangeService exchangeService = (IPfExchangeService)NCLocator.getInstance().lookup(IPfExchangeService.class);
             destVos = exchangeService.runChangeDataAry(srcBilltype, destBillType, new AggregatedValueObject[] { srcBillCloneVO }, srcParaVo);
             Logger.debug("获得单据:" + destBillType + "的数据交换VO完成");
             
             if ((destVos == null) || (destVos.length == 0))
             {
               Logger.warn(">交换到的单据VO为空，则继续下个驱动");
               continue;
             }
             
 
 
 
 
             Object[] driveObjs = null;
             if (userObj != null) {
               driveObjs = (Object[])Array.newInstance(userObj.getClass(), 1);
               
 
               driveObjs[0] = userObj;
             }
             Logger.debug("进行单据:" + destBillType + "的数据数组VO[0]完成");
             
             destParaVo = PfUtilBaseTools.getVariableValue(destBillType, beDrivedActionName, null, destVos, userObj, driveObjs, null, eParam, hashBilltypeToParavo, src_billtypePK);
             destParaVo.m_splitValueVos = destVos;
             
 
             if (destParaVo.m_operator == null) {
               destParaVo.m_operator = srcOperator;
             }
           }
           
 
           aConstrict.actionConstrictBefore(destParaVo);
           
 
           Object tmpObj = actionOnStep(beDrivedActionName, destParaVo);
           
 
           aConstrict.actionConstrictAfter(destParaVo);
           
 
           backMsg(destParaVo, tmpObj);
           
 
           if (isDriveAction(destBillType, beDrivedActionName)) {
             if (destParaVo.m_splitValueVos != null)
             {
 
               for (int j = 0; j < destParaVo.m_splitValueVos.length; j++)
               {
                 boolean isSucessed = actiondrive(destParaVo.m_splitValueVos[j], userObj, hashBilltypeToParavo, hashMethodReturn, destParaVo, eParam);
                 
                 if (isSucessed) {
                   isLastDriveAction = false;
                 }
               }
             }
           }
           else {
             boolean bAfStarted = false;
             if ((beDrivedActionName.toUpperCase().endsWith("SAVE")) || (beDrivedActionName.toUpperCase().endsWith("EDIT")))
             {
               bAfStarted = ((Boolean)startApproveFlowAfterAction(destParaVo.m_billType, destVos[0], userObj, tmpObj, null, hashBilltypeToParavo, hashMethodReturn, src_billtypePK)[0]).booleanValue();
               if (bAfStarted) {
                 hsFlowStartedBilltypes.add(destBillType);
                 sendMessageWhenStartWorkflow(destParaVo, WorkflowTypeEnum.Approveflow.getIntValue());
               }
             }
             else if (beDrivedActionName.toUpperCase().endsWith("START"))
             {
               boolean bWfStarted = startWorkflowAfterAction(destParaVo.m_billType, destVos[0], userObj, tmpObj, null, hashBilltypeToParavo, hashMethodReturn, src_billtypePK);
               if (bWfStarted) {
                 hsFlowStartedBilltypes.add(destBillType);
                 sendMessageWhenStartWorkflow(destParaVo, WorkflowTypeEnum.Workflow.getIntValue());
               }
             }
             
 
 
             if ((!bAfStarted) && (!srcBilltype.equals(destBillType)))
               insertPushWorkitems(destParaVo, srcBilltype, destBillType, tmpObj, srcParaVo.m_billVersionPK);
             if (isLastDriveAction)
               this.lastParamVO = destParaVo;
             Logger.debug("***执行驱动单据动作:" + currentExecDrive + "结束***");
           }
         } } }
     Logger.debug("*********执行动作驱动actiondrive结束********");
     return true;
   }
   
 
 
 
 
 
 
 
 
 
 
   private Object deepClone(Object oIn)
   {
     Object value = null;
     try {
       ByteArrayOutputStream buf = new ByteArrayOutputStream();
       ObjectOutputStream o = new ObjectOutputStream(buf);
       o.writeObject(oIn);
       ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buf.toByteArray()));
       value = in.readObject();
     } catch (IOException ex) {
       Logger.error(ex.getMessage());
     } catch (ClassNotFoundException ex) {
       Logger.error(ex.getMessage());
     }
     return value;
   }
   
 
 
 
   private void jumpBusitype(PfParameterVO paravo)
     throws BusinessException
   {
     if (paravo == null)
       return;
     String pk_busitype = paravo.m_businessType;
     
     String operator = InvocationInfoProxy.getInstance().getUserId();
     
     String destBillType = paravo.m_billType;
     BilltypeVO billvo = PfDataCache.getBillType(destBillType);
     if (billvo == null)
       return;
     BaseDAO dao = new BaseDAO();
     BillbusinessVO condVO = new BillbusinessVO();
     condVO.setPk_businesstype(pk_busitype);
     condVO.setJumpflag(UFBoolean.TRUE);
     if ((billvo.getIstransaction() != null) && (billvo.getIstransaction().booleanValue())) {
       condVO.setPk_billtype(billvo.getParentbilltype());
       condVO.setTranstype(billvo.getPk_billtypecode());
     } else {
       condVO.setPk_billtype(billvo.getPk_billtypecode());
     }
     try {
       Collection co = dao.retrieve(condVO, true);
       
       if ((co.size() > 0) && (paravo.m_preValueVos != null)) {
         HashMap<String, String> busitypeMaps = new HashMap();
         for (AggregatedValueObject vo : paravo.m_preValueVos) {
           IFlowBizItf fbi = (IFlowBizItf)PfMetadataTools.getBizItfImpl(vo, IFlowBizItf.class);
           
           if (fbi != null)
           {
             String destBusitypePk = null;
             fbi.getBusitype();
             
             String billtype = fbi.getBilltype();
             if (!StringUtil.isEmptyWithTrim(billtype))
             {
               billtype = PfUtilBaseTools.getRealBilltype(billtype);
             } else {
               BilltypeVO billtypevo = PfDataCache.getBillTypeInfo(fbi.getTranstype());
               billtype = billtypevo == null ? null : billtypevo.getParentbilltype();
             }
             if (StringUtil.isEmptyWithTrim(billtype)) {
               billtype = paravo.m_billType;
             }
             if (!StringUtil.isEmptyWithTrim(billtype))
             {
 
               String key = fbi.getPkorg() + operator;
               
               if (busitypeMaps.containsKey(key)) {
                 destBusitypePk = (String)busitypeMaps.get(key);
               } else {
                 destBusitypePk = ((IPFConfig)NCLocator.getInstance().lookup(IPFConfig.class)).retBusitypeCanStart(billtype, fbi.getTranstype(), fbi.getPkorg(), operator);
                 
                 busitypeMaps.put(key, destBusitypePk);
               }
               
               if (!StringUtil.isEmptyWithTrim(destBusitypePk))
               {
 
                 fbi.setBusitype(destBusitypePk);
                 JumpStatusCallbackContext context = new JumpStatusCallbackContext();
                 context.setBillVo(vo);
                 context.setBusitype(destBusitypePk);
                 context.setBilltypeOrTranstype(StringUtil.isEmptyWithTrim(fbi.getTranstype()) ? billtype : fbi.getTranstype());
                 callbackBillStatus(context);
               }
             }
           }
         } } } catch (DAOException e) { Logger.error(e.getMessage(), e);
     }
   }
   
 
 
 
   private void callbackBillStatus(JumpStatusCallbackContext context)
   {
     ArrayList<Billtype2VO> vos = PfDataCache.getBillType2Info(context.getBilltypeOrTranstype(), ExtendedClassEnum.BUSI_CALLBACK.getIntValue());
     for (Billtype2VO bt2VO : vos) {
       String checkClsName = bt2VO.getClassname();
       if (!StringUtil.isEmptyWithTrim(checkClsName)) {
         try
         {
           Object objImpl = Class.forName(checkClsName).newInstance();
           if ((objImpl instanceof IBusiBillStatusCallBack)) {
             ((IBusiBillStatusCallBack)objImpl).callCheckStatus(context);
           }
         } catch (Exception e) {
           Logger.error(e.getMessage(), e);
         }
       }
     }
   }
   
 
 
 
 
 
 
 
 
 
   private void insertPushWorkitems(PfParameterVO paravo, String srcBillType, String destBillType, Object retObj, String srcBillId)
   {
     Logger.debug(">>发送推式消息=" + destBillType + "开始");
     String pkGroup = paravo.m_pkGroup;
     String pk_busitype = paravo.m_businessType;
     String senderman = paravo.m_operator;
     
 
     boolean isNeed = false;
     BillbusinessVO condVO = new BillbusinessVO();
     condVO.setPk_group(pkGroup);
     condVO.setPk_businesstype(pk_busitype);
     
     if (PfUtilBaseTools.isTranstype(srcBillType)) {
       BilltypeVO transtypeVO = PfDataCache.getBillTypeInfo(srcBillType);
       condVO.setPk_billtype(transtypeVO.getParentbilltype());
       condVO.setTranstype(srcBillType);
     } else {
       condVO.setPk_billtype(srcBillType);
     }
     
     BaseDAO dao = new BaseDAO();
     try {
       Collection co = dao.retrieve(condVO, true);
       if (co.size() > 0) {
         BillbusinessVO vo = (BillbusinessVO)co.iterator().next();
         UFBoolean isMsg = vo.getForwardmsgflag();
         if ((isMsg != null) && (isMsg.booleanValue()))
           isNeed = true;
       }
     } catch (DAOException ex) {
       Logger.error(ex.getMessage(), ex);
       return;
     }
     
     if (!isNeed) {
       Logger.debug(">>源单据" + srcBillType + "不可发送下游消息，返回");
       return;
     }
     
 
     if ((paravo.m_splitValueVos == null) || (paravo.m_splitValueVos.length == 0)) {
       return;
     }
     Logger.debug(">>推式消息，分单数=" + paravo.m_splitValueVos.length);
     
     IPfPersonFilter2 filter = null;
     try {
       Object checkClzInstance = PfUtilTools.getBizRuleImpl(paravo.m_billType);
       if ((checkClzInstance instanceof IPfPersonFilter2)) {
         filter = (IPfPersonFilter2)checkClzInstance;
       }
     } catch (BusinessException ex) {
       Logger.error("流程平台：查找业务流下游消息人员过滤IPfPersonFilter2接口异常：" + ex.getMessage(), ex);
     }
     
     for (int k = 0; k < paravo.m_splitValueVos.length; k++)
     {
       AggregatedValueObject billvo = paravo.m_splitValueVos[k];
       
       PfUtilBaseTools.fetchBillId(paravo, billvo, retObj);
       IPFConfig pfcfg = (IPFConfig)NCLocator.getInstance().lookup(IPFConfig.class.getName());
       try
       {
         String[] hsUserPKs = pfcfg.queryForwardMessageUser(srcBillType, destBillType, pk_busitype, billvo, filter);
         if ((hsUserPKs == null) || (hsUserPKs.length == 0)) {
           Logger.warn(">>无法发送推式消息，因为接收用户为空");
           return;
         }
         
 
 
 
 
         List<NCMessage> msgList = new ArrayList();
         
         for (String userId : hsUserPKs)
         {
           MessageVO msgvo = new MessageVO();
           
           msgvo.setMsgsourcetype("notice");
           msgvo.setPriority(Integer.valueOf(0));
           msgvo.setSendtime(new UFDateTime());
           msgvo.setReceiver(userId);
           msgvo.setSender(senderman);
           msgvo.setPk_group(pkGroup);
           
           String originLang = InvocationInfoProxy.getInstance().getLangCode();
           
           String userLang = Pfi18nTools.getLangcodeOfUser(userId);
           InvocationInfoProxy.getInstance().setLangCode(userLang);
           msgvo.setSubject(Pfi18nTools.i18nBilltypeName(srcBillType) + NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "PFBusiAction-0000") + Pfi18nTools.i18nBilltypeName(destBillType) + paravo.m_billNo + NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "PFBusiAction-0001"));
           InvocationInfoProxy.getInstance().setLangCode(originLang);
           
 
 
 
           msgvo.setContenttype("BIZ_PUSH");
           msgvo.setIsdelete(UFBoolean.FALSE);
           msgvo.setIshandled(UFBoolean.FALSE);
           msgvo.setSendstate(UFBoolean.TRUE);
           
           msgvo.setPk_detail(paravo.m_billVersionPK);
           msgvo.setDetail(srcBillType + "@" + destBillType + "@" + paravo.m_billNo);
           
           msgvo.setMsgtype("nc");
           
           msgvo.setDomainflag(PfMessageUtil.getModuleOfBilltype(destBillType));
           
           NCMessage msg = new NCMessage();
           msg.setMessage(msgvo);
           
           msgList.add(msg);
         }
         
 
 
 
 
 
 
 
 
 
 
 
 
 
         if (msgList.size() > 0) {
           PfMessageUtil.deletePullMessage(srcBillId);
           MessageCenter.sendMessage((NCMessage[])msgList.toArray(new NCMessage[0]));
           Logger.debug(">>发送推式消息数=" + msgList.size());
 
         }
         
 
 
       }
       catch (Exception e)
       {
 
 
         Logger.error(e.getMessage(), e);
       }
     }
     Logger.debug(">>发送推式消息=" + destBillType + "结束");
   }
   
 
 
 
 
 
   private PfUtilActionVO[] queryActionDriveVOs(PfParameterVO paraVo)
     throws PFBusinessException
   {
     PfUtilActionVO[] driveActions = null;
     try {
       PfUtilDMO dmo = new PfUtilDMO();
       driveActions = dmo.queryDriveAction(paraVo.m_billType, paraVo.m_businessType, paraVo.m_pkOrg, paraVo.m_actionName, paraVo.m_operator);
       
 
       if (((driveActions == null) || (driveActions.length == 0)) && 
         (PfUtilBaseTools.isTranstype(paraVo.m_billType))) {
         Logger.debug("找不到交易类型的动作驱动，继续找单据类型的下游驱动");
         String strRealBilltype = PfUtilBaseTools.getRealBilltype(paraVo.m_billType);
         driveActions = dmo.queryDriveAction(strRealBilltype, paraVo.m_businessType, paraVo.m_pkOrg, paraVo.m_actionName, paraVo.m_operator);
       }
     }
     catch (DbException e) {
       Logger.error(e.getMessage(), e);
       throw new PFBusinessException(e.getMessage());
     }
     return driveActions;
   }
   
   private void resetApproveStatus(AggregatedValueObject vo, int status) {
     IFlowBizItf fbi = (IFlowBizItf)PfMetadataTools.getBizItfImpl(vo, IFlowBizItf.class);
     if (fbi != null) {
       fbi.setApproveStatus(Integer.valueOf(status));
     }
   }
   
	/**
	 * 审批流动作执行前调用，返回单据所有未完成的待办
	 * @param billid
	 * @return
	 */
	protected WorkitemVO[] beforeProcessAction(String billid, boolean isDeleteAction) {
		if (billid == null) {
			return null;
		} else {
			if (isDeleteAction)
				return SyncWorkitemUtil.queryAllWorkitems(billid);
			else
				return SyncWorkitemUtil.queryUnfinshedWorkitems(billid);
		}
	}
   
   
	/**
	 *  SKQ 2016-12-01 修改，加入待办推送至外部系统处理逻辑，重命名原方法为doProcessAction
	 *  
	 */
	@SuppressWarnings("rawtypes")
	public Object processAction(String actionName, String billOrTranstype, WorkflownoteVO workflowVo, AggregatedValueObject billvo, Object userObj, HashMap eParam)
			throws BusinessException {
		// 此单据类型是否进行待办消息推送
		boolean flag = SyncWorkitemUtil.isSyncEnable(billOrTranstype);
		// 是否删除操作
		boolean isDelelteAction = actionName.startsWith("DELETE");
		
		String billid = null;
		WorkitemVO[] workitems = null;
		
		if (flag) {
			try {
				billid = billvo.getParentVO().getPrimaryKey();
				// 保存动作前单据的billid为NULL，需要在后面重新取billid
				if (billid != null)
					workitems = beforeProcessAction(billid, isDelelteAction);
			} catch(Throwable t) {
				// 捕获推送待办过程所有可能发生的异常，避免影响正常的审批流动作执行
				IntegrationLogger.error(t.getMessage(), t);
			}
		}

		Object result = doProcessAction(actionName, billOrTranstype, workflowVo, billvo, userObj, eParam);
		
		if (flag) {
			try {
				afterProcessAction(billid, billOrTranstype, billvo, workitems, isDelelteAction);
			} catch(Throwable t) {
				// 捕获推送待办过程所有可能发生的异常，避免影响正常的审批流动作执行
				IntegrationLogger.error(t.getMessage(), t);
			}
		}
		
		return result;
	}
   
	/**
	 * 审批流动作执行完成后调用，向OA系统推送待办
	 * @param billid
	 * @param billType
	 * @param billvo
	 * @param workitems  审批之前的代办
	 * @param isDeleteAction
	 * @throws BusinessException
	 */
	protected void afterProcessAction(String billid, String billType, AggregatedValueObject billvo, WorkitemVO[] workitems, boolean isDeleteAction) throws BusinessException {
		if (billid == null)
			billid = billvo.getParentVO().getPrimaryKey();
		
		// 删除操作直接删除所有待办
		if (isDeleteAction && workitems != null) {
			for (WorkitemVO workitem : workitems)
				SyncWorkitemUtil.deleteExternalWorkitem(workitem);
			return;
		}
		
		//查询审批过后的所有代办
		WorkitemVO[] newWorkitems = SyncWorkitemUtil.queryUnfinshedWorkitems(billid);
		//包含在workitems里的已完成的待办，即本次审批处理了的代办
		WorkitemVO[] finshedWorkitems = SyncWorkitemUtil.queryFinshedWorkitemsWithin(billid, workitems);
		
		// 处理需要删除的待办，
		//		逻辑：已本次操作（审批）为界限，分前后两个部分。
		//		1、审批前的一条代办，如果在审批后还存在，说明这是条未处理的代办，不需要删除。
		//		2、审批前的一条代办，如果在审批之后变成了已办，说明这边本次处理的代办，不删除。
		//		3、审批前的一条代办，只有即不在未处理代办，也不存在已处理代办，说明这条代办不存在的应该被删除。
		if (workitems != null) {
			for (int i = 0; i < workitems.length; i++) {
				WorkitemVO wt = workitems[i];
				
				if (newWorkitems != null) {
					for (int j = 0; j < newWorkitems.length; j++) {
						//两者相等说明：本次操作之后，这条代办已经存在，没有被处理，所以不需要进一步操作，所以设置为null
						if (newWorkitems[j] != null && wt.getWorkitemId().equals(newWorkitems[j].getWorkitemId())) {
							workitems[i] = null;
							newWorkitems[j] = null;
						}
					}
				}
				
				if (finshedWorkitems != null) {
					for (int k = 0; k < finshedWorkitems.length; k++) {
						//两者相等说明：已经在本次审批中处理了，不进行删除，因为后面会标识为已完成代办
						if (wt.getWorkitemId().equals(finshedWorkitems[k].getWorkitemId()))
							workitems[i] = null;
					}
				}
				
				//删除代办
				SyncWorkitemUtil.deleteExternalWorkitem(workitems[i]);
			}
		}
		
		// 处理已完成的待办
		if (finshedWorkitems != null) {
			for (WorkitemVO workitem : finshedWorkitems)
				SyncWorkitemUtil.endExternalWorkitem(workitem);
		}
		
		// 处理新增的待办
		if (newWorkitems != null) {
			PublicHeadVO standHeadVO = SyncWorkitemUtil.getStandHeadVo(billType, billvo);
			for (WorkitemVO workitem : newWorkitems)
			{
				SyncWorkitemUtil.beginExternalWorkitem(standHeadVO, workitem);
			}
		}
	}
	
   public Object doProcessAction(String actionName, String billOrTranstype, WorkflownoteVO workflowVo, AggregatedValueObject billvo, Object userObj, HashMap eParam) throws BusinessException {
     Logger.init("workflow");
     billvo.getParentVO().getPrimaryKey();
     Logger.debug("*后台单据动作处理PFBusiAction.processAction开始");
     debugParams(actionName, billOrTranstype, workflowVo, billvo, userObj, eParam);
     long start = System.currentTimeMillis();
     
     Hashtable hashBilltypeToParavo = new Hashtable();
     Hashtable hashMethodReturn = new Hashtable();
     
     if (eParam == null) {
       eParam = new HashMap();
     }
     
 
     if ((eParam != null) && (eParam.get("forcestart") != null)) {
       resetApproveStatus(billvo, -1);
     }
     
 
     processWorknote(workflowVo);
     
 
     AggregatedValueObject[] inVos = null;
     if (billvo != null) {
       inVos = PfUtilBaseTools.pfInitVos(billvo.getClass().getName(), 1);
       inVos[0] = billvo;
     }
     PfParameterVO paraVoOfBilltype = PfUtilBaseTools.getVariableValue(billOrTranstype, actionName, billvo, inVos, userObj, null, workflowVo, eParam, hashBilltypeToParavo);
     
     try
     {
       if (PfUtilBaseTools.isDeleteAction(paraVoOfBilltype.m_actionName, paraVoOfBilltype.m_billType)) {
         deleteWorkFlow(paraVoOfBilltype);
       }
       
       IPFActionConstrict aConstrict = new PFActionConstrict();
       aConstrict.actionConstrictBefore(paraVoOfBilltype);
       
 
       Object retObj = actionOnStep(paraVoOfBilltype.m_actionName, paraVoOfBilltype);
       
 
       aConstrict.actionConstrictAfter(paraVoOfBilltype);
       
 
       if ((retObj instanceof IWorkFlowRet))
       {
         return ((IWorkFlowRet)retObj).m_inVo;
       }
       try
       {
         Object[] tmpObj = (Object[])retObj;
         Hashtable hasNoProc = null;
         
         if ((tmpObj != null) && (tmpObj.length > 0) && ((tmpObj[0] instanceof IWorkflowBatch))) {
           IWorkflowBatch wfBatch = (IWorkflowBatch)tmpObj[0];
           hasNoProc = wfBatch.getNoPassAndGoing();
           Object[] userObjs = (Object[])wfBatch.getUserObj();
           retObj = userObjs[0];
         }
         if ((hasNoProc != null) && (hasNoProc.containsKey("0"))) {
           return null;
         }
       }
       catch (Exception e) {}
       
 
       backMsg(paraVoOfBilltype, retObj);
       
 
       if ((isDriveAction(paraVoOfBilltype.m_billType, paraVoOfBilltype.m_actionName)) && (isNeedDriveAction(retObj))) {
         actiondrive(paraVoOfBilltype.m_preValueVo, userObj, hashBilltypeToParavo, hashMethodReturn, paraVoOfBilltype, eParam);
       }
       
       String src_billtypePK = paraVoOfBilltype.m_preValueVo != null ? paraVoOfBilltype.m_preValueVo.getParentVO().getPrimaryKey() : StringUtil.isEmptyWithTrim(paraVoOfBilltype.m_preValueVo.getParentVO().getPrimaryKey()) ? "" : "";
       
 
       if (PfUtilBaseTools.isSaveAction(paraVoOfBilltype.m_actionName, paraVoOfBilltype.m_billType)) {
         retObj = startApproveFlowAfterAction(paraVoOfBilltype.m_billType, paraVoOfBilltype.m_preValueVo, userObj, retObj, eParam, hashBilltypeToParavo, hashMethodReturn, src_billtypePK)[1];
         sendMessageWhenStartWorkflow(paraVoOfBilltype, WorkflowTypeEnum.Approveflow.getIntValue());
       }
       else if (PfUtilBaseTools.isStartAction(paraVoOfBilltype.m_actionName, paraVoOfBilltype.m_billType)) {
         startWorkflowAfterAction(paraVoOfBilltype.m_billType, paraVoOfBilltype.m_preValueVo, userObj, retObj, eParam, hashBilltypeToParavo, hashMethodReturn, src_billtypePK);
         sendMessageWhenStartWorkflow(paraVoOfBilltype, WorkflowTypeEnum.Workflow.getIntValue());
       }
       
       jumpBusitype(this.lastParamVO);
       
 
       WfInstancePool.getInstance().clear();
       
       Logger.debug("*后台单据动作处理PFBusiAction.processAction结束，耗时" + (System.currentTimeMillis() - start) + "ms");
       
       return retObj;
     } catch (BusinessException ex) {
       logWorkflowExptInfo(ex, paraVoOfBilltype);
       throw ex;
     }
   }
   
   private void processWorknote(WorkflownoteVO worknoteVO)
     throws BusinessException
   {
     if ((worknoteVO == null) || (worknoteVO.getAttachmentSetting() == null)) {
       return;
     }
     List<WorkflownoteAttVO> noteAttList = new ArrayList();
     List<AttachmentVO> attList = worknoteVO.getAttachmentSetting();
     
     String pk_wf_task = worknoteVO.getPk_wf_task();
     String pk_checkflow = worknoteVO.getPk_checkflow();
     
     for (AttachmentVO attVO : attList) {
       WorkflownoteAttVO noteAtt = new WorkflownoteAttVO();
       
       noteAtt.setPk_wf_task(pk_wf_task);
       noteAtt.setPk_checkflow(pk_checkflow);
       noteAtt.setPk_file(attVO.getPk_file());
       noteAtt.setFilename(attVO.getFilename());
       noteAtt.setFilesize(attVO.getFilesize());
       
       noteAttList.add(noteAtt);
     }
     
     new BaseDAO().insertVOList(noteAttList);
   }
   
 
 
 
 
 
   private boolean isNeedDriveAction(Object retObj)
   {
     if ((retObj == null) || (!(retObj instanceof AggregatedValueObject)))
       return true;
     AggregatedValueObject aggObj = (AggregatedValueObject)retObj;
     CircularlyAccessibleValueObject parentvo = aggObj.getParentVO();
     if (parentvo == null)
       return true;
     Object driveFlag = parentvo.getAttributeValue("driveaction");
     if (driveFlag == null)
       return true;
     return driveFlag.toString().equalsIgnoreCase("Y");
   }
   
 
 
   private void logWorkflowExptInfo(BusinessException ex, PfParameterVO paramVO)
   {
     if ((paramVO == null) || (paramVO.m_workFlow == null))
       return;
     IPFExptLog itf = (IPFExptLog)NCLocator.getInstance().lookup(IPFExptLog.class);
     PfExptLogVO logVO = new PfExptLogVO();
     logVO.setPk_org(paramVO.m_pkOrg);
     logVO.setBillno(paramVO.m_billNo);
     logVO.setBilltype(paramVO.m_billType);
     logVO.setMsghint(ex.getMessage());
     logVO.setContent(ex.getMessage());
     
     logVO.setFlowtype(paramVO.m_workFlow.getWorkflow_type() == null ? WorkflowTypeEnum.Approveflow.getIntValue() : paramVO.m_workFlow.getWorkflow_type().intValue());
     try {
       if ((paramVO.m_workFlow.getTaskInfo() != null) && (paramVO.m_workFlow.getTaskInfo().getTask() != null) && (paramVO.m_workFlow.getTaskInfo().getTask().getWfProcessDefPK() != null)) {
         itf.insertLog_RequiresNew(paramVO.m_workFlow.getTaskInfo().getTask().getWfProcessDefPK(), logVO);
       }
     } catch (Exception e) {
       Logger.error("记录流程异常日志出错！");
       Logger.error(e.getMessage(), e);
     }
   }
   
 
 
   private void debugParams(String actionName, String billOrTranstype, WorkflownoteVO worknoteVO, Object billEntity, Object userObj, HashMap eParam)
   {
     Logger.debug("*********************************************");
     Logger.debug("* actionName=" + actionName);
     Logger.debug("* billType=" + billOrTranstype);
     Logger.debug("* worknoteVO=" + worknoteVO);
     Logger.debug("* billEntity=" + billEntity);
     Logger.debug("* userObj=" + userObj);
     Logger.debug("* eParam=" + eParam);
     Logger.debug("*********************************************");
   }
   
 
 
 
 
   private void backMsg(PfParameterVO paravo, Object retObj)
   {
     Logger.debug(">>上游消息处理=" + paravo.m_billType + "开始");
     
 
     final BaseDAO dao = new BaseDAO();
     try {
       BillbusinessVO condVO = new BillbusinessVO();
       condVO.setPk_group(paravo.m_pkOrg);
       condVO.setPk_businesstype(paravo.m_businessType);
       
       if (PfUtilBaseTools.isTranstype(paravo.m_billType)) {
         BilltypeVO transtypeVO = PfDataCache.getBillTypeInfo(paravo.m_billType);
         condVO.setPk_billtype(transtypeVO.getParentbilltype());
         condVO.setTranstype(paravo.m_billType);
       } else {
         condVO.setPk_billtype(paravo.m_billType);
       }
       Collection co = dao.retrieve(condVO, true);
       if (co.size() > 0) {
         BillbusinessVO vo = (BillbusinessVO)co.iterator().next();
         UFBoolean isMsg = vo.getBackmsgflag();
         if ((isMsg == null) || (!isMsg.booleanValue())) {
           Logger.debug(">>单据" + paravo.m_billType + "不可发送上游消息，返回");
           return;
         }
       }
     } catch (DAOException ex) {
       Logger.error(ex.getMessage(), ex);
       return;
     }
     
     try
     {
       final BackmsgVO condVO = new BackmsgVO();
       condVO.setPk_group(paravo.m_pkGroup);
       condVO.setPk_busitype(paravo.m_businessType);
       condVO.setPk_srcbilltype(paravo.m_billType);
       condVO.setActiontype(paravo.m_actionName);
       condVO.setIsapprover(null);
       condVO.setIsbillmaker(null);
       
       IRequestDataCacheKey key = new CondVOKey("pfbusiaction_backmsg", condVO);
       ICacheDataQueryCallback<Collection> callback = new ICacheDataQueryCallback()
       {
         public Collection queryData() throws BusinessException
         {
           return dao.retrieve(condVO, true);
         }
         
       };
       Collection coBackmsg = (Collection)PFRequestDataCacheProxy.get(key, callback);
       
       if ((coBackmsg == null) || (coBackmsg.size() == 0)) {
         Logger.debug(">>单据" + paravo.m_billType + "没有进行上游消息配置，返回");
         return;
       }
       
 
       BilltypeVO billVo = PfDataCache.getBillTypeInfo(new BillTypeCacheKey().buildBilltype(paravo.m_billType).buildPkGroup(paravo.m_pkGroup));
       if ((billVo.getCheckclassname() != null) && (billVo.getCheckclassname().trim().length() != 0)) {
         Object obj = PfUtilTools.instantizeObject(billVo.getPk_billtypecode(), billVo.getCheckclassname().trim());
         IPFSourceBillFinder srcFinder; Iterator iter; if ((obj instanceof IPFSourceBillFinder)) {
           srcFinder = (IPFSourceBillFinder)obj;
           
           PfUtilBaseTools.fetchBillId(paravo, paravo.m_preValueVo, retObj);
           
           for (iter = coBackmsg.iterator(); iter.hasNext();)
           {
             executeBackmsgs(srcFinder, (BackmsgVO)iter.next(), paravo); }
         } else {
           Logger.debug(">>单据" + paravo.m_billType + "的审批流检查类没有实现接口IPFSourceBillFinder，返回");
           return;
         }
       } else {
         Logger.debug(">>单据" + paravo.m_billType + "没有注册审批流检查类，返回");
         return;
       }
     } catch (Exception ex) {
       Logger.error(ex.getMessage(), ex);
     }
   }
   
 
 
 
 
 
 
   private void executeBackmsgs(IPFSourceBillFinder srcFinder, BackmsgVO backmsgVO, PfParameterVO paravo)
     throws BusinessException
   {
     Logger.debug(">>给上游单据" + backmsgVO.getPk_destbilltype() + "发送上游消息 开始");
     
 
     SourceBillInfo[] infos = srcFinder.findSourceBill(backmsgVO.getPk_destbilltype(), paravo.m_preValueVo);
     HashSet<String> hsBillmakers = new HashSet();
     HashSet<String> hsApprovers = new HashSet();
     for (int i = 0; i < (infos == null ? 0 : infos.length); i++) {
       hsBillmakers.add(infos[i].getBillmaker());
       hsApprovers.add(infos[i].getApprover());
     }
     
 
 
     ArrayList alActions = PfDataCache.getBillactionVOs(paravo.m_billType);
     String strDefaultName = Pfi18nTools.findActionName(paravo.m_actionName, alActions);
     String msgContent = NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "PFBusiAction-0002") + Pfi18nTools.i18nBilltypeName(paravo.m_billType) + " " + paravo.m_billNo + NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "PFBusiAction-0003") + Pfi18nTools.i18nActionName(paravo.m_billType, paravo.m_actionName, strDefaultName) + "(" + paravo.m_actionName + ")" + NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "PFBusiAction-0004");
     
     ArrayList<MessageinfoVO> alItems = new ArrayList();
     if ((backmsgVO.getIsbillmaker().booleanValue()) && (backmsgVO.getIsapprover().booleanValue())) {
       hsBillmakers.addAll(hsApprovers);
       constructBackmsgs(paravo, hsBillmakers, msgContent, alItems);
     } else if (backmsgVO.getIsbillmaker().booleanValue()) {
       constructBackmsgs(paravo, hsBillmakers, msgContent, alItems);
     } else if (backmsgVO.getIsapprover().booleanValue()) {
       constructBackmsgs(paravo, hsApprovers, msgContent, alItems);
     }
     
 
 
     new PFMessageImpl().insertPushOrPullMsgs((MessageinfoVO[])alItems.toArray(new MessageinfoVO[alItems.size()]), 6);
     
 
 
     Logger.debug(">>给上游单据" + backmsgVO.getPk_destbilltype() + "发送上游消息 结束");
   }
   
 
 
 
 
 
 
 
   private void constructBackmsgs(PfParameterVO paravo, HashSet<String> hsReceivers, String msgContent, ArrayList<MessageinfoVO> alItems)
   {
     for (String receiver : hsReceivers) {
       MessageinfoVO wi = new MessageinfoVO();
       wi.setPk_billtype(paravo.m_billType);
       
       wi.setBillid(paravo.m_billVersionPK);
       wi.setBillno(paravo.m_billNo);
       wi.setCheckman(receiver);
       wi.setTitle(msgContent);
       
       wi.setPk_corp(paravo.m_pkOrg);
       wi.setSenderman(paravo.m_operator);
       alItems.add(wi);
     }
   }
   
   public Object[] processBatch_MultiThread(String actionName, String billOrTranstype, AggregatedValueObject[] billvos, Object[] userObjAry, WorkflownoteVO worknoteVO, HashMap eParam) throws BusinessException
   {
     ExecutorService exe = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
     CompletionService service = new ExecutorCompletionService(exe);
     Object[] ret = null;
     for (int i = 0; i < billvos.length; i++) {
       HashMap map = eParam == null ? null : (HashMap)eParam.clone();
       Object obj = (userObjAry == null) || (userObjAry.length == 0) ? null : userObjAry[i];
       SingleBillFlowTask task = new SingleBillFlowTask(actionName, billOrTranstype, billvos[i], obj, worknoteVO, map);
       service.submit(task);
     }
     for (int i = 0; i < billvos.length; i++) {
       try
       {
         Future f = service.take();
         Object obj = f.get();
         if (ret == null)
           ret = (Object[])Array.newInstance(obj.getClass(), billvos.length);
         ret[i] = obj;
       }
       catch (Exception e) {
         Logger.error(e);
       }
     }
     
     return ret;
   }
   
 
 
 
 
 
 
 
 
 
   private WorkflownoteVO[] prepareWorkflownotesForBatch(String actionName, String billOrTranstype, AggregatedValueObject[] billvos, Object[] userObjAry, WorkflownoteVO worknoteVO, HashMap hmParam, PFBatchExceptionInfo exceptionInfo)
     throws BusinessException
   {
     WorkflownoteVO[] noteArray = new WorkflownoteVO[billvos.length];
     for (int i = 0; i < billvos.length; i++) {
       if ((!PfUtilBaseTools.isStartFlowAction(actionName, billOrTranstype)) && (!PfUtilBaseTools.isSignalFlowAction(actionName, billOrTranstype))) {
         noteArray[i] = new WorkflownoteVO();
       }
       else {
         try
         {
           if (hmParam.get("nosendmessage") != null) {
             noteArray[i] = null;
           } else {
             noteArray[i] = ((IWorkflowMachine)NCLocator.getInstance().lookup(IWorkflowMachine.class)).checkWorkFlow(actionName, billOrTranstype, billvos[i], null);
           }
           
 
           if (noteArray[i] == null)
           {
             noteArray[i] = new WorkflownoteVO();
             noteArray[i].setAnyoneCanApprove(true);
             noteArray[i].setApproveresult("Y");
           } else if (!"R".equals(worknoteVO.getApproveresult())) {
             if ((noteArray[i].getTaskInfo().getAssignableInfos() != null) && (noteArray[i].getTaskInfo().getAssignableInfos().size() > 0))
             {
               noteArray[i] = null;
               throw new PFBusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "PFBusiAction-0005"));
             }
             if ((noteArray[i].getTaskInfo().getTransitionSelectableInfos() != null) && (noteArray[i].getTaskInfo().getTransitionSelectableInfos().size() > 0)) {
               throw new PFBusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "PFBusiAction-0006"));
             }
           }
           if ((noteArray[i] != null) && (worknoteVO != null)) {
             noteArray[i].setIscheck(worknoteVO.getIscheck());
             noteArray[i].setApproveresult(worknoteVO.getApproveresult());
             noteArray[i].setChecknote(worknoteVO.getChecknote());
             if (("R".equals(worknoteVO.getApproveresult())) && (!noteArray[i].isAnyoneCanApprove())) {
               noteArray[i].getTaskInfo().getTask().setBackToFirstActivity(true);
               noteArray[i].getTaskInfo().getTask().setTaskType(WfTaskType.Backward.getIntValue());
             }
           }
         } catch (BusinessException e) {
           Logger.error(e.getMessage(), e);
           exceptionInfo.putErrorMessage(i, billvos[i], e.getMessage());
         }
       }
     }
     
     return noteArray;
   }
   
 
 
 
 
 
 
   public Object[] processBatch(String actionName, String billOrTranstype, AggregatedValueObject[] billvos, Object[] userObjAry, WorkflownoteVO worknoteVO, HashMap eParam, PFBatchExceptionInfo exceptionInfo)
     throws BusinessException
   {
     WorkflownoteVO[] noteArray = prepareWorkflownotesForBatch(actionName, billOrTranstype, billvos, userObjAry, worknoteVO, eParam, exceptionInfo);
     Object[] ret = null;
     for (int i = 0; i < billvos.length; i++)
       if (noteArray[i] != null)
       {
         HashMap map = eParam == null ? null : (HashMap)eParam.clone();
         Object obj = (userObjAry == null) || (userObjAry.length == 0) ? null : userObjAry[i];
         Object singleVal = null;
         try {
           singleVal = ((IWorkflowMachine)NCLocator.getInstance().lookup(IWorkflowMachine.class)).processSingleBillFlow_RequiresNew(actionName, billOrTranstype, noteArray[i], billvos[i], obj, map);
         }
         catch (Exception e) {
           Logger.error(e.getMessage(), e);
           exceptionInfo.putErrorMessage(i, billvos[i], e.getMessage());
         }
         ret = PfUtilBaseTools.composeResultAry(singleVal, billvos.length, i, ret);
       }
     return ret;
   }
   
   public Object[] processBatch(String actionName, String billOrTranstype, AggregatedValueObject[] billvos, Object[] userObjAry, WorkflownoteVO worknoteVO, HashMap eParam)
     throws BusinessException
   {
     Logger.init("workflow");
     Logger.debug("*后台单据动作批处理PFBusiAction.processBatch开始");
     debugParams(actionName, billOrTranstype, worknoteVO, billvos, userObjAry, null);
     long start = System.currentTimeMillis();
     
     if (billvos == null) {
       return null;
     }
     
     IPFActionConstrict aConstrict = null;
     
 
     Hashtable<String, Object> hashBilltypeToParavo = new Hashtable();
     Hashtable hashMethodReturn = new Hashtable();
     PfParameterVO paravoOfLastSrcBill = null;
     for (int i = 0; i < billvos.length; i++)
     {
       aConstrict = new PFActionConstrict();
       PfParameterVO paraOfThisBill = null;
       
       if ((userObjAry != null) && (userObjAry.length >= 1))
       {
         paraOfThisBill = PfUtilBaseTools.getVariableValue(billOrTranstype, actionName, billvos[i], billvos, userObjAry[i], userObjAry, worknoteVO, eParam, hashBilltypeToParavo);
       }
       else {
         paraOfThisBill = PfUtilBaseTools.getVariableValue(billOrTranstype, actionName, billvos[i], billvos, null, null, worknoteVO, eParam, hashBilltypeToParavo);
       }
       
       if (PfUtilBaseTools.isDeleteAction(paraOfThisBill.m_actionName, paraOfThisBill.m_billType)) {
         deleteWorkFlow(paraOfThisBill);
       }
       
       aConstrict.actionConstrictBefore(paraOfThisBill);
       
       if (i == billvos.length - 1) {
         paravoOfLastSrcBill = paraOfThisBill;
       }
     }
     
     paravoOfLastSrcBill.m_preValueVo = null;
     
     Object[] retObjs = (Object[])actionOnStep(paravoOfLastSrcBill.m_actionName, paravoOfLastSrcBill);
     
 
     Hashtable hasNoProc = null;
     
     if ((retObjs != null) && (retObjs.length > 0) && ((retObjs[0] instanceof IWorkflowBatch))) {
       IWorkflowBatch wfBatch = (IWorkflowBatch)retObjs[0];
       hasNoProc = wfBatch.getNoPassAndGoing();
       retObjs = (Object[])wfBatch.getUserObj();
     }
     
     if (hasNoProc == null) {
       hasNoProc = new Hashtable();
     }
     
 
 
     AggregatedValueObject[] beforeVos = paravoOfLastSrcBill.m_preValueVos;
     for (int i = 0; (beforeVos != null) && (i < beforeVos.length); i++)
     {
 
       if (i == 1) {
         worknoteVO = null;
       }
       
       if (beforeVos[i] != null)
       {
 
 
 
         if (!hasNoProc.containsKey(String.valueOf(i)))
         {
 
 
           Object tmpActionObj = null;
           
           userObjAry = paravoOfLastSrcBill.m_userObjs;
           if ((userObjAry != null) && (userObjAry.length != 0)) {
             tmpActionObj = userObjAry[i];
           }
           
           PfParameterVO currParaVo = null;
           if ((userObjAry != null) && (userObjAry.length >= 1))
           {
             currParaVo = PfUtilBaseTools.getVariableValue(billOrTranstype, actionName, beforeVos[i], beforeVos, tmpActionObj, userObjAry, worknoteVO, eParam, hashBilltypeToParavo);
           }
           else {
             currParaVo = PfUtilBaseTools.getVariableValue(billOrTranstype, actionName, beforeVos[i], beforeVos, null, null, worknoteVO, eParam, hashBilltypeToParavo);
           }
           aConstrict = new PFActionConstrict();
           
           aConstrict.actionConstrictAfter(currParaVo);
           
 
           backMsg(currParaVo, retObjs == null ? null : retObjs[i]);
           
 
           String strActionNameOfPara = currParaVo.m_actionName;
           
           String src_billtypePK = currParaVo.m_preValueVo != null ? currParaVo.m_preValueVo.getParentVO().getPrimaryKey() : StringUtil.isEmptyWithTrim(currParaVo.m_preValueVo.getParentVO().getPrimaryKey()) ? "" : "";
           
           if (isDriveAction(currParaVo.m_billType, strActionNameOfPara))
           {
             hashBilltypeToParavo = new Hashtable();
             hashBilltypeToParavo.put(currParaVo.m_billType + src_billtypePK, currParaVo);
             actiondrive(currParaVo.m_preValueVo, tmpActionObj, hashBilltypeToParavo, hashMethodReturn, currParaVo, new HashMap());
           }
           
 
 
 
           if (PfUtilBaseTools.isSaveAction(strActionNameOfPara, currParaVo.m_billType)) {
             startApproveFlowAfterAction(currParaVo.m_billType, beforeVos[i], tmpActionObj, retObjs == null ? null : retObjs[i], eParam, hashBilltypeToParavo, hashMethodReturn, src_billtypePK);
             sendMessageWhenStartWorkflow(currParaVo, WorkflowTypeEnum.Approveflow.getIntValue());
           }
           else if (PfUtilBaseTools.isStartAction(strActionNameOfPara, currParaVo.m_billType)) {
             startWorkflowAfterAction(currParaVo.m_billType, beforeVos[i], tmpActionObj, retObjs == null ? null : retObjs[i], eParam, hashBilltypeToParavo, hashMethodReturn, src_billtypePK);
             sendMessageWhenStartWorkflow(currParaVo, WorkflowTypeEnum.Workflow.getIntValue());
           }
         }
       }
     }
     
 
     WfInstancePool.getInstance().clear();
     
     Logger.debug("*后台单据动作批处理PFBusiAction.processBatch结束，耗时" + (System.currentTimeMillis() - start) + "ms");
     
     return retObjs;
   }
 }