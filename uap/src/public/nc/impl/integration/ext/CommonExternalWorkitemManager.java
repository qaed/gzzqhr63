/**
 * 
 */
package nc.impl.integration.ext;

import java.util.HashMap;
import java.util.Map;

import nc.itf.integration.ext.IExternalWorkitemManager;

/**
 * @author ljw
 *
 */
public class CommonExternalWorkitemManager implements IExternalWorkitemManager {


	public Map<String, Object> beginWorkitem(String appName, String createTime,
			String docCreator, String extendContent, String key, Integer level,
			String link, String modelId, String modelName, String others,
			String param1, String param2, String subject, String targets,
			Integer type) {
		return new HashMap<String, Object>();
	}

	public Map<String, Object> endWorkitem(String appName, String key,
			String modelId, String modelName, String param1, String param2,
			String targets, Integer type, Integer optType) {
		return new HashMap<String, Object>();
	}
	
	public Map<String, Object> deleteWorkitem(String appName, String key,
			String modelId, String modelName, String param1, String param2,
			String targets, Integer type, Integer optType) {
		return new HashMap<String, Object>();
	}

}
