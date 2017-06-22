package nc.itf.hrwa;

import nc.vo.pub.BusinessException;

public interface IWaBaUnitMaintain {
	// 所有方法的参数以及返回值修改为Object
	// 同时修改接口以及实现、把其中的具体的主 VO 类型修改为聚合 VO 类型
	public void delete(Object vo) throws BusinessException;

	public Object insert(Object vo) throws BusinessException;

	public Object update(Object vo) throws BusinessException;

	public Object[] query(String whereSql) throws BusinessException;
}
