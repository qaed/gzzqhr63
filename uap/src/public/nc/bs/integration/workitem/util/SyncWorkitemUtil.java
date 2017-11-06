package nc.bs.integration.workitem.util;

import java.io.IOException;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;

import nc.bs.hrss.pub.pf.PFUtil;
import nc.bs.hrss.pub.pf.cnfg.ConfigMeta;
import nc.bs.hrss.pub.pf.cnfg.ConfigMetaParser;
import nc.bs.hrss.pub.pf.cnfg.ConfigMetas;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.integration.common.config.IntegrationConfig;
import nc.bs.integration.common.logging.IntegrationLogger;
import nc.bs.integration.common.processor.StringProcessor;
import nc.bs.integration.common.processor.WorkitemProcessor;
import nc.bs.logging.Logger;
import nc.impl.integration.ext.CommonExternalWorkitemManager;
import nc.itf.integration.ext.IExternalWorkitemManager;
import nc.itf.uap.pf.metadata.IFlowBizItf;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.uap.cpb.org.vos.CpAppsNodeVO;
import nc.uap.pf.metadata.PfMetadataTools;
import nc.vo.integration.workitem.WorkitemVO;
import nc.vo.jcom.util.ClassUtil;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.change.PublicHeadVO;
import nc.vo.pub.msg.MessageVO;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.uap.pf.PFRuntimeException;


/**
 * 通过接口发送待办消息至外部系统的工具类
 * @author rime
 *
 */
public final class SyncWorkitemUtil {
	
	private static IExternalWorkitemManager externalWorkitemManager = null;
	//private static String URL = "/portal?returnUrl=/portal/pt/home/view?pageModule=pint%26pageName=task%26if_src=app/taskcenterApp?nodecode=11110101";
	private static String APPNAME = "NC63HR";//来源系统
	/**
	 * 根据PK查询待办事项
	 * @param pk_checkflow
	 * @return
	 */
	public static WorkitemVO queryWokritemByPk(String pk_checkflow) {
		String sql = "select * from uapii_view_workitem where pk_checkflow = '" + pk_checkflow + "'";
		WorkitemVO[] workitems = null;
		
		try {
			workitems = (WorkitemVO[]) new BaseDAO().executeQuery(sql, new WorkitemProcessor());
		} catch (Exception e) {
			IntegrationLogger.error(e.getMessage(), e);
		}
		
		return workitems == null ? null : workitems[0];
	}
	
	/**
	 * 根据<code>billid</code>查询单据所有未完成待办事项
	 * @param billid
	 * @return
	 */
	public static WorkitemVO[] queryUnfinshedWorkitems(String billid) {
		String sql = "select * from uapii_view_workitem where approvestatus = 0 and billid = '" + billid + "'";
		WorkitemVO[] workitems = null;
		
		try {
			workitems = (WorkitemVO[]) new BaseDAO().executeQuery(sql, new WorkitemProcessor());
		} catch (Exception e) {
			IntegrationLogger.error(e.getMessage(), e);
		}
		
		return workitems;
	}
	
	/**
	 * 根据<code>billid</code>查询单据所有不包含在<cdoe>wts</code>内的未完成待办事项
	 * @param billid
	 * @return
	 */
	public static WorkitemVO[] queryUnfinshedWorkitemsWithout(String billid, WorkitemVO[] wts) {
		String sql = "select * from uapii_view_workitem where approvestatus = 0 and billid = '" + billid + "'";
		String condition = buildCondition(wts);
		if (condition != null)
			sql += " and pk_checkflow not in " +  condition;
		WorkitemVO[] workitems = null;
		
		try {
			workitems = (WorkitemVO[]) new BaseDAO().executeQuery(sql, new WorkitemProcessor());
		} catch (Exception e) {
			IntegrationLogger.error(e.getMessage(), e);
		}
		
		return workitems;
	}
	
	/**
	 * 根据<code>billid</code>单据ID,查询当前单据包含在<cdoe>worktiems</code>内的完成待办事项
	 * @param billid
	 * @param billmaker
	 * @return
	 */
	public static WorkitemVO[] queryFinshedWorkitemsWithin(String billid, WorkitemVO[] wts) {
		String sql = "select * from uapii_view_workitem where approvestatus in(1,4) and billid = '" + billid + "'";
		String condition = buildCondition(wts);
		if (condition != null)
			sql += " and pk_checkflow in " +  condition;
		WorkitemVO[] workitems = null;
		
		try {
			workitems = (WorkitemVO[]) new BaseDAO().executeQuery(sql, new WorkitemProcessor());
		} catch (Exception e) {
			IntegrationLogger.error(e.getMessage(), e);
		}
		
		return workitems;
	}
	
	/**
	 * 加签时，根据WorkflownoteVO查询当前审批流下其他审批人的待办事项 hezy 20170904
	 * @param worknoteVO
	 * @return
	 */
	public static WorkitemVO[] queryAddSignWokritemByNote(WorkflownoteVO worknoteVO) {
		String sql = 
			" SELECT * FROM UAPII_VIEW_WORKITEM WHERE ACTIONTYPE<>'BIZ' AND BILLNO='"+ worknoteVO.getBillno() +"'\n" +
			"       AND PK_WF_TASK='"+ worknoteVO.getPk_wf_task() +"' AND NVL(ISCHECK, 'N') = 'N' AND ACTIONTYPE NOT LIKE '%_A'" + 
			"		AND CHECKMAN<>'"+ worknoteVO.getCheckman() +"' ";

		WorkitemVO[] workitems = null;
		
		try {
			workitems = (WorkitemVO[]) new BaseDAO().executeQuery(sql, new WorkitemProcessor());
		} catch (Exception e) {
			IntegrationLogger.error(e.getMessage(), e);
		}
		
		return workitems;
	}
	
	/**
	 * 根据<code>billid</code>查询单据所有待办事项
	 * @param billid
	 * @return
	 */
	public static WorkitemVO[] queryAllWorkitems(String billid) {
		String sql = "select * from uapii_view_workitem where billid = '" + billid + "'";		
		WorkitemVO[] workitems = null;
		
		try {
			workitems = (WorkitemVO[]) new BaseDAO().executeQuery(sql, new WorkitemProcessor());
		} catch (Exception e) {
			IntegrationLogger.error(e.getMessage(), e);
		}
		
		return workitems;
	}
	
	
	/**
	 * 获取配置文件对应的单据表体
	 * @param standHeadVO
	 * @param wt
	 * @return
	 */
	public static String getWorkitemTitle(PublicHeadVO standHeadVO, WorkitemVO wt) {
		String msgNote = MessageVO.getMessageNoteAfterI18N(wt.getMessageNote());
		if ("MAKEBILL".equals(wt.getActionType()))
			return msgNote;
		
		String titlePattern = IntegrationConfig.getInstance().get("oa." + wt.getSystemCode() + ".title");
		if (titlePattern == null)
			return msgNote;
		
		if(standHeadVO != null){
			titlePattern = titlePattern.replaceAll("\\{billmaker\\}", getUserName(standHeadVO.operatorId));
			titlePattern = titlePattern.replaceAll("\\{billtype\\}", wt.getBillTypeName());
			titlePattern = titlePattern.replaceAll("\\{billno\\}", wt.getBillNo());
			titlePattern = titlePattern.replaceAll("\\{msgnote\\}", msgNote);
			return titlePattern;
		}
		
		return msgNote;
	}
	
	/**20170713 ljw
	 * 获取模块名称
	 * @param wt
	 * @return
	 */
	public static String getSystemname(WorkitemVO wt){
		String systemname = IntegrationConfig.getInstance().get("oa." + wt.getSystemCode());
		if (systemname == null || "".equals(systemname))
			systemname = wt.getSystemName();
		return systemname;
	}
	
	/**20170713 ljw
	 * 发送待办事项至外部系统
	 * @param wt
	 */
	public static void beginExternalWorkitem(PublicHeadVO standHeadVO, WorkitemVO wt) {
		if (wt != null) {
			beginExternalWorkitem(
					APPNAME,							//来源系统
					wt.getSendDate(),					//发送时间
					wt.getSender(),						//发送人
					null,								//消息内容扩展
					null,								//关键字
					null,								//待办优先级
					getWorkitemURL(wt),					//链接
					wt.getWorkitemId(), 				//待办ID
					getSystemname(wt),					//模块名
					null,								//扩展参数
					null,								//参数1
					null,								//参数2
					getWorkitemTitle(standHeadVO, wt),	//标题
					wt.getRecipient(), 					//待办接收人编码
					new Integer(1),						//待审状态
					wt.getBillNo()						//单据ID
			);
		}
	}
	
	/**
	 * 获取需要发送的待办URL
	 * @param wt
	 * @return
	 */
	public static String getWorkitemURL(WorkitemVO wt) {
		StringBuffer billurl = new StringBuffer();
		try {
			String billType = wt.getBillType().trim().split("-")[0];
			ConfigMeta configMeta = getModelByBillType(billType);
			if(null != configMeta){
				List<CpAppsNodeVO> list = (List<CpAppsNodeVO>) new BaseDAO().executeQuery("select * from cp_appsnode e where e.id='"+configMeta.getNodeCode()+"'", new BeanListProcessor(CpAppsNodeVO.class));
				if(list!=null && list.size()>0){
					CpAppsNodeVO nodeVo=list.get(0);
					//String className = wt.getClassName().replaceAll("\\&", "\\%26");
					billurl.append("/portal?returnUrl=");
					billurl.append("/portal/");
					billurl.append(nodeVo.getUrl());
					billurl.append("?nodecode=").append(configMeta.getNodeCode());
					billurl.append("%26model=").append(configMeta.getPageModelClazz());
					billurl.append("%26NC=Y%26pf_bill_editable=Y");
					billurl.append("%26billType=").append(wt.getBillType());
					billurl.append("%26billTypeCode=").append(configMeta.getBillTypeCode());
					billurl.append("%26openBillId=").append(wt.getBillId());
					billurl.append("%26state=State_Run");
					//billurl.append("%26taskPk=ncerwfmtaskqry%26state=State_Run%26sourcePage=workflow%26NC=Y");
				}else{
					billurl.append("/portal?returnUrl=/portal/pt/home/view?pageModule=pint%26pageName=task%26if_src=app/taskcenterApp?nodecode=11110101");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return billurl.toString();
	}
	
	private static ConfigMeta getModelByBillType(String billType) throws IOException{
	    ConfigMetas configMetas = (ConfigMetas)CommonUtil.getCacheValue("pfconfig_cache_oa");
	    if(configMetas == null){
	    	String baseParserFilePath = "/hr/hrss/pfconfig.xml";
	    	URL url = PFUtil.class.getResource(baseParserFilePath);
	    	if (url != null) {
	            InputStream input = PFUtil.class.getResource(baseParserFilePath).openStream();
	            configMetas = ConfigMetaParser.parse(input);
	            CommonUtil.setCacheValue("pfconfig_cache_oa", configMetas);
	         }
	    }
	    return configMetas.getConfigMeta(billType);
	}
	
	/**20170713 ljw
	 * 发送待办
	 */
	public static void beginExternalWorkitem(String appName, String sendDate, String sender, String extendContent, String key,
			 Integer level, String link, String workitemId, String modelName, String others, String param1, String param2,String title, String recipient, Integer type, String billNo) {
		Logger.init("workflow");
		Logger.debug("*******ljwtest:getExternalWorkitemManager begin********");
		IExternalWorkitemManager mgr = getExternalWorkitemManager();
		Logger.debug("*******ljwtest:getExternalWorkitemManager end********"+mgr.toString());
		if (mgr != null) {
			//拼接json，登录名作为唯一标识
			String targets = "{\"LoginName\":\""+recipient+"\"}";
			//20170518 hezy 增加发送、返回报文记录
			Map<String, Object> rsMap = mgr.beginWorkitem(appName, sendDate, null, extendContent, key,
					  level, link, workitemId, modelName, others, param1, param2 ,title, targets, type);
			boolean successful = null == rsMap || !rsMap.containsKey("successful") ? false : (Boolean) rsMap.get("successful");
			
			String sendxml = null == rsMap || !rsMap.containsKey("sendxml") ? "" : (String) rsMap.get("sendxml");
			String returnxml = null == rsMap || !rsMap.containsKey("returnxml") ? "" : (String) rsMap.get("returnxml");
			try {
				if (successful) {
//					IntegrationLogger.info("待办推送成功:workitemId=" + workitemId + ", recipient=" + recipient + ", sender=" + sender + ", billNo=" + billNo + ", title=" + title + ",link=" + link+ ", systemName=" + systemName);
					// 操作成功的处理
					new BaseDAO().executeUpdate("insert into success_Workitem(action,workitemid,recipient,sender,billno,title,link,systemname,senddate,sendxml,returnxml,reason,dr)" + "values('begin','"
							+ workitemId + "','" + recipient + "','" + sender + "','" + billNo + "','" + title + "','" + link + "','" + modelName + "','"
							+ sendDate + "','" + sendxml  + "','" + returnxml + "','成功','0')");
				} else {
					// 操作失败的处理
					new BaseDAO().executeUpdate("insert into Fail_Workitem(action,workitemid,recipient,sender,billno,title,link,systemname,senddate,sendxml,returnxml,reason,dr)" + "values('begin','"
							+ workitemId + "','" + recipient + "','" + sender + "','" + billNo + "','" + title + "','" + link + "','" + modelName + "','"
							+ sendDate + "','" + sendxml  + "','" + returnxml  + "','失败','0')");
				}
			} catch (DAOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**20170713 ljw
	 * 标记待办状态为已完成
	 * @param wt
	 */
	public static void endExternalWorkitem(WorkitemVO wt) {
		if (wt != null)
			endExternalWorkitem(
					APPNAME,							//来源系统
					null,								//关键字
					wt.getWorkitemId(), 				//待办ID
					getSystemname(wt),					//模块名
					null,								//参数1
					null,								//参数2
					wt.getRecipient(), 					//待办接收人编码
					new Integer(1),						//待审状态
					new Integer(1),						//待办为已办操作
					wt.getBillNo()						//单据ID
					);
	}
	
	/**20170713 ljw
	 * 标记待办状态为已办
	 */
	public static void endExternalWorkitem(String appName, String key, String workitemId,String modelName,
			String param1,String param2 ,String recipient, Integer type,Integer optType,String billNo) {
		IExternalWorkitemManager mgr = getExternalWorkitemManager();
		if (mgr != null) {
			String targets = "{\"LoginName\":\""+recipient+"\"}";
			//20170518 hezy 增加发送、返回报文记录
			Map<String, Object> rsMap = mgr.endWorkitem(appName, key, workitemId, modelName,
					 param1, param2 , targets, type, optType);
			boolean successful = null == rsMap || !rsMap.containsKey("successful") ? false : (Boolean) rsMap.get("successful");
			String sendxml = null == rsMap || !rsMap.containsKey("sendxml") ? "" : (String) rsMap.get("sendxml");
			String returnxml = null == rsMap || !rsMap.containsKey("returnxml") ? "" : (String) rsMap.get("returnxml");
			try {
				if (successful) {
					// 操作成功的处理
//					IntegrationLogger.info("待办结束成功：workitemId=" + workitemId + ", recipient=" + recipient);
					new BaseDAO().executeUpdate("insert into success_Workitem(action,workitemid,recipient,billno,sendxml,returnxml,reason,dr)" +
							"values('done','"+workitemId+"','"+recipient+"','" + billNo +"','" + sendxml +"','" + returnxml + "','成功','0')");
				} else {// 操作失败的处理
					//20170518 hezy success_Workitem 改成 Fail_Workitem
					new BaseDAO().executeUpdate("insert into Fail_Workitem(action,workitemid,recipient,billno,sendxml,returnxml,reason,dr)" +
							"values('done','"+workitemId+"','"+recipient+"','" + billNo +"','" + sendxml +"','" + returnxml  +"','失败','0')");
				}
			} catch (DAOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
	}
	
	/**20170713 ljw
	 * 删除待办
	 * @param wt
	 */
	public static void deleteExternalWorkitem(WorkitemVO wt) {
		if (wt != null)
			deleteExteranlWorkitem(
					APPNAME,							//来源系统
					null,								//关键字
					wt.getWorkitemId(), 				//待办ID
					getSystemname(wt),					//模块名
					null,								//参数1
					null,								//参数2
					wt.getRecipient(), 					//待办接收人编码
					null,								//待审状态
					new Integer(1),						//待办删除操作
					wt.getBillNo()						//单据ID
					);
	}
	
	/**20170713 ljw
	 * 删除待办
	 */
	public static void deleteExteranlWorkitem(String appName,String key,String workitemId,String modelName,String param1,String param2
			,String recipient, Integer type, Integer optType,String billNo) {
		IExternalWorkitemManager mgr = getExternalWorkitemManager();
		if (mgr != null) {
			String targets = "{\"LoginName\":\""+recipient+"\"}";
			Map<String, Object> rsMap = mgr.deleteWorkitem(appName, key, workitemId, modelName, param1, param2
					, targets, type, optType);
			boolean successful = null == rsMap || !rsMap.containsKey("successful") ? false : (Boolean) rsMap.get("successful");
			String sendxml = null == rsMap || !rsMap.containsKey("sendxml") ? "" : (String) rsMap.get("sendxml");
			String returnxml = null == rsMap || !rsMap.containsKey("returnxml") ? "" : (String) rsMap.get("returnxml");
			try {
				if (successful) {
					// 操作成功的处理
//					IntegrationLogger.info("待办删除成功：workitemId=" + workitemId + ", recipient=" + recipient);
					new BaseDAO().executeUpdate("insert into success_Workitem(action,workitemid,recipient,billno,sendxml,returnxml,reason,dr)" +
							"values('delete','"+workitemId+"','"+recipient+"','" + billNo +"','" + sendxml +"','" + returnxml + "','成功','0')");
				} else {// 操作失败的处理
					//20170518 hezy success_Workitem 改成 Fail_Workitem
					new BaseDAO().executeUpdate("insert into Fail_Workitem(action,workitemid,recipient,billno,sendxml,returnxml,reason,dr)" +
							"values('delete','"+workitemId+"','"+recipient+"','" + billNo  +"','" + sendxml +"','" + returnxml + "','失败','0')");
				}
			} catch (DAOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
	}
	
	public static PublicHeadVO getStandHeadVo(String billType, AggregatedValueObject singleVO) {
		// 获取主表数据
		PublicHeadVO standHeadVo = new PublicHeadVO();
		
		try{
			//根据元数据模型来获取
			PfMetadataTools.checkBilltypeRelatedMeta(billType);
			getHeadInfoByMeta(standHeadVo, singleVO, billType);
			
		}catch (PFRuntimeException e) {
			//根据VO对照信息来获取
			e.printStackTrace();
		}
		
		return standHeadVo;
	}
	
	/**
	 * 从单据实体中获取平台相关信息
	 * <li>根据单据实体的元数据模型
	 * @param headvo  
	 * @param singleBillEntity 单据实体对象，非数组
	 * @param billType
	 * @since 5.5
	 */
	public static void getHeadInfoByMeta(PublicHeadVO headvo, Object singleBillEntity, String billType) {
		headvo.billType = billType;

		IFlowBizItf fbi = PfMetadataTools.getBizItfImpl(singleBillEntity, IFlowBizItf.class);
		if (fbi == null)
			throw new PFRuntimeException("单据实体没有提供业务接口IFlowBizInf的实现类");

		headvo.approveId = fbi.getApprover();
		headvo.billNo = fbi.getBillNo();
		headvo.businessType = fbi.getBusitype();
		headvo.pkOrg = fbi.getPkorg();
		headvo.operatorId = fbi.getBillMaker();
		headvo.pkBillId = fbi.getBillId();
	}
	
	
	
	
	public static String getUserName(String userid) {
		String username = "";
		String sql = "select user_name from sm_user where cuserid = '" + userid + "'";
		try {
			username = (String) new BaseDAO().executeQuery(sql, new StringProcessor());
		} catch (DAOException e) {
			IntegrationLogger.warn(e.getMessage(), e);
		}
		return username;
	}
	
	public static IExternalWorkitemManager getExternalWorkitemManager() {
		if (externalWorkitemManager == null){
			String className = IntegrationConfig.getInstance().get("oa.workitem.manager");
			if (className == null || "".equals(className))
				externalWorkitemManager = new CommonExternalWorkitemManager();
			else
				try {
					externalWorkitemManager = (IExternalWorkitemManager) ClassUtil.loadClass(className).newInstance();
				} catch (Exception e) {
					IntegrationLogger.debug(e.getMessage(), e);
					externalWorkitemManager = new CommonExternalWorkitemManager();
				}
		}		
		return externalWorkitemManager;
	}
	
	public static boolean isSyncEnable(String billtype) {
		String sql = "select systemcode from bd_billtype where pk_billtypecode = '" + billtype + "'";
		try {
			String systemcode = (String) new BaseDAO().executeQuery(sql, new StringProcessor());
			if (systemcode == null)
				return false;
			
			String ncModules = IntegrationConfig.getInstance().get("nc.modules");
			if (ncModules == null || "".equals(ncModules)) {
				return false;
			}
			
			String[] modules = ncModules.split(",");
			for (String module : modules) {
				if (module.equalsIgnoreCase(systemcode))
					return true;
			}
			
			return false;
		} catch (DAOException e) {
			IntegrationLogger.warn(e.getMessage(), e);
			return false;
		}
	}
	
	
	private static String buildCondition(WorkitemVO[] workitems) {
		if (workitems == null || workitems.length == 0) {
			return null;
		} else {
			String condition = "(";
			for (int i = 0; i < workitems.length; i++) {
				if (i != 0) {
					condition += ",";
				}
				
				condition += "'" + workitems[i].getWorkitemId() + "'";
			}
			condition += ")";
			return condition;
		}
	}
}
