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
 * �������ʹ�����
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
	 * ���ʹ���
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
			//�������췢������
			NotifyTodoSendContext context = new NotifyTodoSendContext();
//			String 		appName			������Դ
//			String 		createTime		����ʱ��			��ʽΪ:yyyy-MM-dd HH:mm:ss
//			String 		docCreator		���촴���ߣ���Ϊ�գ�
//			String 		extendContent	��Ϣ������չ����Ϊ�գ�
//			String 		key				�ؼ��֣���Ϊ�գ�
//			Integer 	level			�������ȼ�����Ϊ�գ�
//			String 		link			����
//			String 		modelId			����Ψһ��ʶ
//			String 		modelName		ģ����
//			String 		others			��չ��������Ϊ�գ�
//			String 		param1			����1����Ϊ�գ�
//			String 		param2			����2����Ϊ�գ�
//			String 		subject			����
//			Json 		targets			������������
//			Integer 	type			��������			1.������ 2.֪ͨ��
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
			//��¼������Ϣ
			rsMap.put("sendxml", context.toString());
			NotifyTodoAppResult rs = getUnityTodoService().sendTodo(context);// ������xml���͵�OA�������ؽ����
			Logger.error("�������ͷ��ؽ����" + rs);
			//����״̬
			int returnState = rs.getReturnState();
			boolean isSucc = returnState == 2 ? true : false;
			if(returnState == 2){
				returnxml = "���ͳɹ�";
			}else if(returnState == 0){
				returnxml = "δ����";
			}else if(returnState == 1){
				returnxml = "����ʧ�ܣ�������Ϣ��" + rs.getMessage();
			}else{
				returnxml = "OAϵͳ������״̬��δ֪״̬";
			}
			//��¼������Ϣ
			rsMap.put("returnxml", returnxml);
			rsMap.put("successful", isSucc);
			return rsMap;
			
		} catch (RemoteException e) {
			e.printStackTrace();
			returnxml += "\n�쳣��־��" + e.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
			returnxml += "\n�쳣��־��" + e.getMessage();
		}
		rsMap.put("returnxml", returnxml);
		rsMap.put("successful", false);
		return rsMap;
	}

	/**
	 * ɾ������
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
//				String 		appName			������Դ
//				String 		key				�ؼ��֣���Ϊ�գ�
//				String 		modelId			����Ψһ��ʶ
//				String 		modelName		ģ����
//				String 		param1			����1����Ϊ�գ�
//				String 		param2			����2����Ϊ�գ�
//				Json 		targets			������������
//				Integer 	type			�������ͣ���Ϊ�գ�	1.���� 2.���� 3.�ݹ�
//				Integer 	optType			��������			1.ɾ������ 2.ɾ��ָ������������
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
				Logger.error("ɾ����������" + rs);
				//����״̬
				int returnState = rs.getReturnState();
				boolean isSucc = returnState == 2 ? true : false;
				if(returnState == 2){
					returnxml = "ɾ���ɹ�";
				}else if(returnState == 0){
					returnxml = "δɾ��";
				}else if(returnState == 1){
					returnxml = "ɾ��ʧ�ܣ�������Ϣ��" + rs.getMessage();
				}else{
					returnxml = "OAϵͳ������״̬��δ֪״̬";
				}
				//��¼������Ϣ
				rsMap.put("type", "delete");
				rsMap.put("returnxml", returnxml);
				rsMap.put("successful", isSucc);
				return rsMap;
				
			} catch (Exception e) {
				Logger.error("��OA����ɾ��������Ϣ��" + e.getMessage());
				returnxml += "\n�쳣��־��" + e.getMessage();
			}
		rsMap.put("returnxml", returnxml);
		rsMap.put("successful", false);
		return rsMap;
	}
	
	
	/**
	 * ��������Ϊ�Ѱ�
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
//				String 		appName			������Դ
//				String 		key				�ؼ��֣���Ϊ�գ�
//				String 		modelId			����Ψһ��ʶ
//				String 		modelName		ģ����
//				String 		param1			����1����Ϊ�գ�
//				String 		param2			����2����Ϊ�գ�
//				Json 		targets			������������
//				Integer 	type			�������ͣ���Ϊ�գ�	1.���� 2.���� 3.�ݹ�
//				Integer 	optType			��������			1.ɾ������ 2.ɾ��ָ������������
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
				//��ȡ���ؽ��
				NotifyTodoAppResult rs = getUnityTodoService().setTodoDone(context);
				rsMap.put("sendxml", context.toString());
				Logger.error("�����Ѱ�����" + rs);
				int returnState = rs.getReturnState();
				boolean isSucc = returnState == 2 ? true : false;
				if(returnState == 2){
					returnxml = "������Ϊ�Ѱ�";
				}else if(returnState == 0){
					returnxml = "δ����Ϊ�Ѱ�";
				}else if(returnState == 1){
					returnxml = "�����Ѱ�ʧ�ܣ�������Ϣ��" + rs.getMessage();
				}else{
					returnxml = "OAϵͳ������״̬��δ֪״̬";
				}
				//��¼������Ϣ
				rsMap.put("type", "done");
				rsMap.put("returnxml", returnxml);
				rsMap.put("successful", isSucc);
				return rsMap;
			} catch (Exception e) {
				Logger.error("��OA������Ϊ�Ѱ������Ϣ��" + e.getMessage());
				returnxml += "\n�쳣��־��" + e.getMessage();
			}
		rsMap.put("returnxml", returnxml);
		rsMap.put("successful", false);
		return rsMap;
	}
	
	/**
	 * ��ȡ���ʹ������ӿ�
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
	 * ���÷������ɿͻ��˵ķ������
	 * 
	 * @param address WebService��URL
	 * @param serviceClass ����ӿ�ȫ��
	 * @return ����������
	 * @throws Exception
	 */
//	public static Object callService(String address, Class serviceClass)
//			throws Exception {
//
//		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
//		
//		// ��¼��վ��Ϣ
//		factory.getInInterceptors().add(new LoggingInInterceptor());
//		
//		// ��¼��վ��Ϣ
//		factory.getOutInterceptors().add(new LoggingOutInterceptor());
//		
//		// �����Ϣͷ��֤��Ϣ����������Ҫ����֤�û����룬�����˶δ���
//		factory.getOutInterceptors().add(new AddSoapHeader());
//
//		factory.setServiceClass(serviceClass);
//		factory.setAddress(address);
//		
//		// ʹ��MTOM���봦����Ϣ�������Ҫ����Ϣ�д����ĵ������ȶ��������ݣ������˶δ���
//		// Map<String, Object> props = new HashMap<String, Object>();
//		// props.put("mtom-enabled", Boolean.TRUE);
//		// factory.setProperties(props);		
//        
//        // ���������������
//		return factory.create();
//	}
}
