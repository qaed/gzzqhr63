/**
 * 
 */
package nc.bs.integration.common.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import nc.bs.framework.common.RuntimeEnv;
import nc.bs.integration.common.logging.IntegrationLogger;

/**
 * @author rime
 *
 */
public final class IntegrationConfig {

	private static IntegrationConfig instance;
	
	public static IntegrationConfig getInstance() {
		if (instance == null)
			instance = new IntegrationConfig();
		return instance;
	}
	
	private String filePath;
	
	private Properties prop;
	
	private IntegrationConfig() {
		filePath = RuntimeEnv.getInstance().getNCHome() + "/ierp/integration/config.properties";
		prop = new Properties();
		
		try {
			prop.load(new FileInputStream(filePath));
		} catch (Exception e) {
			IntegrationLogger.error(e.getMessage(), e);
		}
	}
	
	public String get(String key) {
		String value = prop.getProperty(key);
		if (value != null)
			try {
				value = new String(value.getBytes("ISO8859-1"),"GBK");
			} catch (UnsupportedEncodingException e) {
				IntegrationLogger.error(e.getMessage(), e);
			}
		return value;
	}
	
	public void set(String key, String value) {
		prop.setProperty(key, value);
	}
	
	public void store() {
		try {
			prop.store(new FileOutputStream(filePath), "");
		} catch (FileNotFoundException e) {
			IntegrationLogger.error(e.getMessage(), e);
		} catch (IOException e) {
			IntegrationLogger.error(e.getMessage(), e);
		}
	}
}
