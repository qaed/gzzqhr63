package nc.itf.hrwa;

import java.util.HashMap;

import nc.vo.bm.data.BmDataVO;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.pe.PELoginContext;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;

public interface IWaBaUnitMaintain {
	// 所有方法的参数以及返回值修改为Object
	// 同时修改接口以及实现、把其中的具体的主 VO 类型修改为聚合 VO 类型
	public void delete(Object vo) throws BusinessException;

	public Object insert(Object vo) throws BusinessException;

	public Object update(Object vo) throws BusinessException;

	public Object[] query(String whereSql) throws BusinessException;

	/**
	 * 通过部门生成
	 * 
	 * @param loginContext
	 * @param paramHashMap
	 * @throws BusinessException
	 */
	public void creatByDept(LoginContext loginContext, HashMap<HRDeptVO, String> paramHashMap) throws BusinessException;

	public BmDataVO[] queryPsnForAdd(String condition) throws BusinessException;
}
