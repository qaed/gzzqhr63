/**
 * 
 */
package nc.impl.integration.ext.cgws.oa;

import java.net.URL;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import nc.bs.framework.server.ServerConfiguration;
import nc.bs.logging.Logger;
import nc.itf.integration.ext.IExternalWorkitemManager;
import nc.vo.pub.BusinessException;


import com.gzws.webservice.client.WebServiceConfig;
import com.landray.kmss.sys.notify.webservice.ISysNotifyTodoWebService;
import com.landray.kmss.sys.notify.webservice.ISysNotifyTodoWebServiceServiceLocator;
import com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult;
import com.landray.kmss.sys.notify.webservice.NotifyTodoRemoveContext;
import com.landray.kmss.sys.notify.webservice.NotifyTodoSendContext;

/**
 * @author ljw
 * 待办推送处理类
 *
 */
public class OAWorkitemManager implements IExternalWorkitemManager {

	ISysNotifyTodoWebService service = null;

	/* (non-Javadoc)
	 * @see nc.itf.integration.ext.IExternalWorkitemManager#beginWorkitem(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public Map<String, Object> beginWorkitem(String appName, String createTime, String docCreator, String extendContent, String key,
			 Integer level, String link, String modelId, String modelName, String others, String param1, String param2,String subject, String targets, Integer type) {
		try {
			return sendWorkitem(appName, createTime, docCreator, extendContent, key,
					  level, link, modelId, modelName, others, param1, param2, subject, targets, type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new HashMap<String, Object>();
	}

	/* (non-Javadoc)
	 * @see nc.itf.integration.ext.IExternalWorkitemManager#endWorkitem(java.lang.String, java.lang.String)
	 */
	public Map<String, Object> endWorkitem(String appName, String key, String modelId,String modelName,
			String param1,String param2 ,String targets, Integer type,Integer optType) {
		try {
			return doneUnityTodo(appName, key, modelId, modelName, param1, param2, targets, type, optType);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		return new HashMap<String, Object>();
	}

	/* (non-Javadoc)
	 * @see nc.itf.integration.ext.IExternalWorkitemManager#deleteWorkitem(java.lang.String, java.lang.String)
	 */
	public Map<String, Object> deleteWorkitem(String appName,String key,String modelId,String modelName,String param1,String param2
			,String targets, Integer type, Integer optType) {
		try {
			return deleteUnityTodo(appName, key, modelId, modelName, param1, param2, targets, type, optType);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		return new HashMap<String, Object>();
	}

	/**
	 * 发送待办
	 * @param appName
	 * @param createTime
	 * @param docCreator
	 * @param extendContent
	 * @param key
	 * @param level
	 * @param link
	 * @param modelId
	 * @param modelName
	 * @param others
	 * @param param1
	 * @param param2
	 * @param subject
	 * @param targets
	 * @param type
	 * @return
	 */
	public Map<String, Object> sendWorkitem(String appName, String createTime, String docCreator, String extendContent, String key,
			 Integer level, String link, String modelId, String modelName, String others, String param1, String param2,String subject, String targets, Integer type) {
		Map<String, Object> rsMap = new HashMap<String, Object>();
		String returnxml = "";
		try {
			//创建待办发送内容
			NotifyTodoSendContext context = new NotifyTodoSendContext();
//			String 		appName			代办来源
//			String 		createTime		创建时间			格式为:yyyy-MM-dd HH:mm:ss
//			String 		docCreator		待办创建者（可为空）
//			String 		extendContent	消息内容扩展（可为空）
//			String 		key				关键字（可为空）
//			Integer 	level			待办优先级（可为空）
//			String 		link			链接
//			String 		modelId			待办唯一标识
//			String 		modelName		模块名
//			String 		others			扩展参数（可为空）
//			String 		param1			参数1（可为空）
//			String 		param2			参数2（可为空）
//			String 		subject			标题
//			Json 		targets			待办所属对象
//			Integer 	type			待办类型			1.审批类 2.通知类
			context.setAppName(appName);
			context.setCreateTime(createTime);
			context.setDocCreator(docCreator);
			context.setExtendContent(extendContent);
			context.setKey(key);
			context.setLevel(level);
			String webUrl = ServerConfiguration.getServerConfiguration().getDefWebServerURL();
			context.setLink(webUrl+link);
			context.setModelId(modelId);
			context.setModelName(modelName);
			context.setOthers(others);
			context.setParam1(param1);
			context.setParam2(param2);
			context.setSubject(subject);
			context.setTargets(targets);
			context.setType(type);
			//记录发送信息
			rsMap.put("sendxml", context.toString());
			NotifyTodoAppResult rs = getUnityTodoService().sendTodo(context);// 将待办xml发送到OA，并返回结果。
			Logger.error("待办推送返回结果：" + rs);
			//返回状态
			int returnState = rs.getReturnState();
			boolean isSucc = returnState == 2 ? true : false;
			if(returnState == 2){
				returnxml = "发送成功";
			}else if(returnState == 0){
				returnxml = "未发送";
			}else if(returnState == 1){
				returnxml = "发送失败，错误信息：" + rs.getMessage();
			}else{
				returnxml = "OA系统新增了状态，未知状态";
			}
			//记录返回信息
			rsMap.put("returnxml", returnxml);
			rsMap.put("successful", isSucc);
			return rsMap;
			
		} catch (RemoteException e) {
			e.printStackTrace();
			returnxml += "\n异常日志：" + e.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
			returnxml += "\n异常日志：" + e.getMessage();
		}
		rsMap.put("returnxml", returnxml);
		rsMap.put("successful", false);
		return rsMap;
	}

	/**
	 * 删除待办
	 * @param appName
	 * @param key
	 * @param modelId
	 * @param modelName
	 * @param param1
	 * @param param2
	 * @param targets
	 * @param type
	 * @param optType
	 * @return
	 * @throws BusinessException
	 */
	public Map<String, Object> deleteUnityTodo(String appName,String key,String modelId,String modelName,String param1,String param2
			,String targets, Integer type, Integer optType) throws BusinessException {
		Map<String, Object> rsMap = new HashMap<String, Object>();
		String returnxml = "";
			try {
//				String 		appName			代办来源
//				String 		key				关键字（可为空）
//				String 		modelId			待办唯一标识
//				String 		modelName		模块名
//				String 		param1			参数1（可为空）
//				String 		param2			参数2（可为空）
//				Json 		targets			待办所属对象
//				Integer 	type			待办类型（可为空）	1.待审 2.待阅 3.暂挂
//				Integer 	optType			操作类型			1.删除待办 2.删除指定待办所属人
				NotifyTodoRemoveContext context = new NotifyTodoRemoveContext();
				context.setAppName(appName);
				context.setKey(key);
				context.setModelId(modelId);
				context.setModelName(modelName);
				context.setParam1(param1);
				context.setParam2(param2);
				context.setTargets(targets);
//				context.setType(type);
				context.setOptType(optType);
				NotifyTodoAppResult rs = getUnityTodoService().deleteTodo(context);
				rsMap.put("sendxml", context.toString());
				Logger.error("删除待办结果：" + rs);
				//返回状态
				int returnState = rs.getReturnState();
				boolean isSucc = returnState == 2 ? true : false;
				if(returnState == 2){
					returnxml = "删除成功";
				}else if(returnState == 0){
					returnxml = "未删除";
				}else if(returnState == 1){
					returnxml = "删除失败，错误信息：" + rs.getMessage();
				}else{
					returnxml = "OA系统新增了状态，未知状态";
				}
				//记录返回信息
				rsMap.put("type", "delete");
				rsMap.put("returnxml", returnxml);
				rsMap.put("successful", isSucc);
				return rsMap;
				
			} catch (Exception e) {
				Logger.error("将OA待办删除错误信息：" + e.getMessage());
				returnxml += "\n异常日志：" + e.getMessage();
			}
		rsMap.put("returnxml", returnxml);
		rsMap.put("successful", false);
		return rsMap;
	}
	
	
	/**
	 * 将待办置为已办
	 * @param appName
	 * @param key
	 * @param modelId
	 * @param modelName
	 * @param param1
	 * @param param2
	 * @param targets
	 * @param type
	 * @param optType
	 * @return
	 * @throws BusinessException
	 */
	public Map<String, Object> doneUnityTodo(String appName, String key, String modelId,String modelName,
			String param1,String param2 ,String targets, Integer type,Integer optType) throws BusinessException {
		Map<String, Object> rsMap = new HashMap<String, Object>();
		String returnxml = "";
			try {
//				String 		appName			代办来源
//				String 		key				关键字（可为空）
//				String 		modelId			待办唯一标识
//				String 		modelName		模块名
//				String 		param1			参数1（可为空）
//				String 		param2			参数2（可为空）
//				Json 		targets			待办所属对象
//				Integer 	type			待办类型（可为空）	1.待审 2.待阅 3.暂挂
//				Integer 	optType			操作类型			1.删除待办 2.删除指定待办所属人
				NotifyTodoRemoveContext context = new NotifyTodoRemoveContext();
				context.setAppName(appName);
				context.setKey(key);
				context.setModelId(modelId);
				context.setModelName(modelName);
				context.setParam1(param1);
				context.setParam2(param2);
				context.setTargets(targets);
				context.setType(type);
				context.setOptType(optType);
				//获取返回结果
				NotifyTodoAppResult rs = getUnityTodoService().setTodoDone(context);
				rsMap.put("sendxml", context.toString());
				Logger.error("设置已办结果：" + rs);
				int returnState = rs.getReturnState();
				boolean isSucc = returnState == 2 ? true : false;
				if(returnState == 2){
					returnxml = "已设置为已办";
				}else if(returnState == 0){
					returnxml = "未设置为已办";
				}else if(returnState == 1){
					returnxml = "设置已办失败，错误信息：" + rs.getMessage();
				}else{
					returnxml = "OA系统新增了状态，未知状态";
				}
				//记录返回信息
				rsMap.put("type", "done");
				rsMap.put("returnxml", returnxml);
				rsMap.put("successful", isSucc);
				return rsMap;
			} catch (Exception e) {
				Logger.error("将OA待办置为已办错误信息：" + e.getMessage());
				returnxml += "\n异常日志：" + e.getMessage();
			}
		rsMap.put("returnxml", returnxml);
		rsMap.put("successful", false);
		return rsMap;
	}
	
	/**
	 * 获取推送待办服务接口
	 * @return
	 * @throws Exception
	 */
//	public ISysNotifyTodoWebService getUnityTodoService() throws Exception {
//		if(null == service){
//			WebServiceConfig cfg = WebServiceConfig.getInstance();
//			service = (ISysNotifyTodoWebService)callService(cfg.getAddress(), cfg.getServiceClass());
//		}
//		return service;
//	}
	
	public ISysNotifyTodoWebService getUnityTodoService() throws Exception {
		WebServiceConfig cfg = WebServiceConfig.getInstance();
		if(null == service){
			service = new ISysNotifyTodoWebServiceServiceLocator().getISysNotifyTodoWebServicePort(new URL(cfg.getAddress()));
		}
		return service;
	}
	
	/**
	 * 调用服务，生成客户端的服务代理
	 * 
	 * @param address WebService的URL
	 * @param serviceClass 服务接口全名
	 * @return 服务代理对象
	 * @throws Exception
	 */
//	public static Object callService(String address, Class serviceClass)
//			throws Exception {
//
//		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
//		
//		// 记录入站消息
//		factory.getInInterceptors().add(new LoggingInInterceptor());
//		
//		// 记录出站消息
//		factory.getOutInterceptors().add(new LoggingOutInterceptor());
//		
//		// 添加消息头验证信息。如果服务端要求验证用户密码，请加入此段代码
//		factory.getOutInterceptors().add(new AddSoapHeader());
//
//		factory.setServiceClass(serviceClass);
//		factory.setAddress(address);
//		
//		// 使用MTOM编码处理消息。如果需要在消息中传输文档附件等二进制内容，请加入此段代码
//		// Map<String, Object> props = new HashMap<String, Object>();
//		// props.put("mtom-enabled", Boolean.TRUE);
//		// factory.setProperties(props);		
//        
//        // 创建服务代理并返回
//		return factory.create();
//	}
}
