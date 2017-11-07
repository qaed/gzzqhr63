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
	 * ���ʹ���
	 * @param appName		������Դ
	 * @param createTime	����ʱ��				��ʽΪ:yyyy-MM-dd HH:mm:ss
	 * @param docCreator	������
	 * @param extendContent	��Ϣ������չ����Ϊ�գ�
	 * @param key			�ؼ��֣���Ϊ�գ�
	 * @param level			���ȼ�����Ϊ�գ�		1.���� 2.�� 3.һ��
	 * @param link			����
	 * @param modelId		����Ψһ��ʶ
	 * @param modelName		ģ����
	 * @param others		��չ��������Ϊ�գ�
	 * @param param1		����1����Ϊ�գ�			���츽�ӱ�ʶ������ͬ"�ؼ���"���������ֲ�ͬ���͵Ĵ���
	 * @param param2		����2����Ϊ�գ�			���츽�ӱ�ʶ������ͬ"�ؼ���"���������ֲ�ͬ���͵Ĵ���
	 * @param subject		����
	 * @param targets		������
	 * @param type			��������				1.������ 2.֪ͨ��
	 * @return
	 */
	public Map<String, Object> beginWorkitem(String appName, String createTime, String docCreator, String extendContent, String key,
			 Integer level, String link, String modelId, String modelName, String others, String param1, String param2,String subject, String targets, Integer type);
	
	/**
	 * ��Ϊ�Ѱ�
	 * @param appName		������Դ
	 * @param key			�ؼ��֣���Ϊ�գ�
	 * @param modelId		����Ψһ��ʶ
	 * @param modelName		ģ����
	 * @param param1		����1����Ϊ�գ�		���츽�ӱ�ʶ������ͬ"�ؼ���"���������ֲ�ͬ���͵Ĵ���
	 * @param param2		����2����Ϊ�գ�		���츽�ӱ�ʶ������ͬ"�ؼ���"���������ֲ�ͬ���͵Ĵ���
	 * @param targets		������
	 * @param type			�������ͣ���Ϊ�գ�	1.���� 2.����.3�ݹ�
	 * @param optType		��������			
	 * @return
	 */
	public Map<String, Object> endWorkitem(String appName, String key, String modelId,String modelName,
			String param1,String param2 ,String targets, Integer type,Integer optType);
	
	/**
	 * ɾ������
	 * @param appName		������Դ
	 * @param key			�ؼ��֣���Ϊ�գ�
	 * @param modelId		����Ψһ��ʶ
	 * @param modelName		ģ����
	 * @param param1		����1����Ϊ�գ�		���츽�ӱ�ʶ������ͬ"�ؼ���"���������ֲ�ͬ���͵Ĵ���
	 * @param param2		����2����Ϊ�գ�		���츽�ӱ�ʶ������ͬ"�ؼ���"���������ֲ�ͬ���͵Ĵ���
	 * @param targets		������
	 * @param type			�������ͣ���Ϊ�գ�
	 * @param optType		�������� 			1:��ʾɾ��������� 2:��ʾɾ��ָ�����������˲���
	 * @return
	 */
	public Map<String, Object> deleteWorkitem(String appName,String key,String modelId,String modelName,String param1,String param2
			,String targets, Integer type, Integer optType);
}
