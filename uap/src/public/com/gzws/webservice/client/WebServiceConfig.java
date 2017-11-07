package com.gzws.webservice.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import nc.bs.framework.common.RuntimeEnv;

/**
 * ���������ļ�
 * 
 */
public class WebServiceConfig {
	private static WebServiceConfig cfg = new WebServiceConfig();

	// Web�����URL
	private String address;
	
	// Web����ӿ�
	private Class serviceClass;
	
	// Web�����ʶ
	private String serviceBean;
	
	// �û�
	private String user;
	
	// ����
	private String password;

	private WebServiceConfig() {
		loadCfg();
	}

	public static WebServiceConfig getInstance() {
		return cfg;
	}

	/**
	 * ���������ļ�
	 */
	private void loadCfg() {
		Properties prop = new Properties();
//		InputStream confStream = this.getClass().getResourceAsStream(
//				"client.properties");
		String filePath = RuntimeEnv.getInstance().getNCHome() + "/ierp/integration/client.properties";
		FileInputStream confStream = null;
		try {
			confStream = new FileInputStream(filePath);
			prop.load(confStream);
			this.address = prop.getProperty("address");
			String serviceClassName = prop.getProperty("service_class");
			this.serviceClass = Class.forName(serviceClassName);
			this.serviceBean = prop.getProperty("service_bean");
			this.user = prop.getProperty("user");
			this.password = prop.getProperty("password");

		} catch (Exception e) {
			// TODO �Զ����ɵ�catch��
			e.printStackTrace();
		}finally{
			try {
				confStream.close();
			} catch (IOException e) {
				// TODO �Զ����ɵ�catch��
				e.printStackTrace();
			}
		}
//		finally {
//			try {
//				confStream.close();
//			} catch (IOException e) {
//				// TODO �Զ����ɵ�catch��
//				e.printStackTrace();
//			}
//		}

	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Class getServiceClass() {
		return serviceClass;
	}

	public void setServiceClass(Class serviceClass) {
		this.serviceClass = serviceClass;
	}

	public String getServiceBean() {
		return serviceBean;
	}

	public void setServiceBean(String serviceBean) {
		this.serviceBean = serviceBean;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
