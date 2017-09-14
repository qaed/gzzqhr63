package nc.itf.hrwa;

import java.util.HashMap;

import nc.vo.bm.data.BmDataVO;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.pe.PELoginContext;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;

public interface IWaBaUnitMaintain {
	// ���з����Ĳ����Լ�����ֵ�޸�ΪObject
	// ͬʱ�޸Ľӿ��Լ�ʵ�֡������еľ������ VO �����޸�Ϊ�ۺ� VO ����
	public void delete(Object vo) throws BusinessException;

	public Object insert(Object vo) throws BusinessException;

	public Object update(Object vo) throws BusinessException;

	public Object[] query(String whereSql) throws BusinessException;

	/**
	 * ͨ����������
	 * 
	 * @param loginContext
	 * @param paramHashMap
	 * @throws BusinessException
	 */
	public void creatByDept(LoginContext loginContext, HashMap<HRDeptVO, String> paramHashMap) throws BusinessException;

	public BmDataVO[] queryPsnForAdd(String condition) throws BusinessException;
}
