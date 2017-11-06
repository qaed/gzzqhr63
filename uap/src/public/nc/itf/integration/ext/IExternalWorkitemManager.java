/**
 * 
 */
package nc.itf.integration.ext;

import java.util.Map;

/**
 * @author ljw
 * 
 */
public interface IExternalWorkitemManager {

	/**
	 * 发送待办
	 * @param appName		待办来源
	 * @param createTime	创建时间				格式为:yyyy-MM-dd HH:mm:ss
	 * @param docCreator	创建者
	 * @param extendContent	消息内容扩展（可为空）
	 * @param key			关键字（可为空）
	 * @param level			优先级（可为空）		1.紧急 2.急 3.一般
	 * @param link			链接
	 * @param modelId		待办唯一标识
	 * @param modelName		模块名
	 * @param others		扩展参数（可为空）
	 * @param param1		参数1（可为空）			待办附加标识。功能同"关键字"，辅助区分不同类型的待办
	 * @param param2		参数2（可为空）			待办附加标识。功能同"关键字"，辅助区分不同类型的待办
	 * @param subject		标题
	 * @param targets		接收人
	 * @param type			待办类型				1.审批类 2.通知类
	 * @return
	 */
	public Map<String, Object> beginWorkitem(String appName, String createTime, String docCreator, String extendContent, String key,
			 Integer level, String link, String modelId, String modelName, String others, String param1, String param2,String subject, String targets, Integer type);
	
	/**
	 * 置为已办
	 * @param appName		待办来源
	 * @param key			关键字（可为空）
	 * @param modelId		待办唯一标识
	 * @param modelName		模块名
	 * @param param1		参数1（可为空）		待办附加标识。功能同"关键字"，辅助区分不同类型的待办
	 * @param param2		参数2（可为空）		待办附加标识。功能同"关键字"，辅助区分不同类型的待办
	 * @param targets		接收人
	 * @param type			待办类型（可为空）	1.待审 2.待阅.3暂挂
	 * @param optType		操作类型			
	 * @return
	 */
	public Map<String, Object> endWorkitem(String appName, String key, String modelId,String modelName,
			String param1,String param2 ,String targets, Integer type,Integer optType);
	
	/**
	 * 删除待办
	 * @param appName		待办来源
	 * @param key			关键字（可为空）
	 * @param modelId		待办唯一标识
	 * @param modelName		模块名
	 * @param param1		参数1（可为空）		待办附加标识。功能同"关键字"，辅助区分不同类型的待办
	 * @param param2		参数2（可为空）		待办附加标识。功能同"关键字"，辅助区分不同类型的待办
	 * @param targets		接收人
	 * @param type			待办类型（可为空）
	 * @param optType		操作类型 			1:表示删除待办操作 2:表示删除指定待办所属人操作
	 * @return
	 */
	public Map<String, Object> deleteWorkitem(String appName,String key,String modelId,String modelName,String param1,String param2
			,String targets, Integer type, Integer optType);
}
