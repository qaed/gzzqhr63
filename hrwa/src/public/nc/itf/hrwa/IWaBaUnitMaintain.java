package nc.itf.hrwa;

import nc.vo.pub.BusinessException;

public interface IWaBaUnitMaintain {
	// ���з����Ĳ����Լ�����ֵ�޸�ΪObject
	// ͬʱ�޸Ľӿ��Լ�ʵ�֡������еľ������ VO �����޸�Ϊ�ۺ� VO ����
	public void delete(Object vo) throws BusinessException;

	public Object insert(Object vo) throws BusinessException;

	public Object update(Object vo) throws BusinessException;

	public Object[] query(String whereSql) throws BusinessException;
}
