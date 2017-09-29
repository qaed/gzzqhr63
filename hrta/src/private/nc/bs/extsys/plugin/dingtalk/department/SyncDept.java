package nc.bs.extsys.plugin.dingtalk.department;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.extsys.plugin.dingtalk.OApiException;
import nc.bs.extsys.plugin.dingtalk.auth.AuthHelper;
import nc.bs.extsys.plugin.dingtalk.workflow.SyncWorkFlow;
import nc.bs.logging.Logger;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.pa.PreAlertReturnType;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.vo.pub.BusinessException;

import com.dingtalk.open.client.api.model.corp.Department;

public class SyncDept implements IBackgroundWorkPlugin {

	private List<Department> alldepts = new ArrayList<Department>();
	private int getAllDeptErrorTimes = 0;
	private int syncDeptErrorTimes = 0;
	private int syncDeptErrorMaxTimes = 30;

	public SyncDept() {

	}

	public SyncDept(BaseDAO dao) {
		this.dao = dao;
	}

	private BaseDAO dao;

	@Override
	public PreAlertObject executeTask(BgWorkingContext arg0) throws BusinessException {
		PreAlertObject alert = null;

		try {
			alert = syncTask(arg0);
		} catch (Exception e) {
			if (syncDeptErrorTimes++ < syncDeptErrorMaxTimes && (e.getMessage().contains("-1") || e.getMessage().contains("-2"))) {
				try {
					Thread.sleep(3000L);
				} catch (InterruptedException e1) {
					Logger.error(e1);
				}
				alert = syncTask(arg0);
			} else {
				throw new BusinessException(e);
			}
		}
		return alert;
	}

	private PreAlertObject syncTask(BgWorkingContext arg0) throws BusinessException {
		PreAlertObject alert = new PreAlertObject();
		alert.setReturnType(PreAlertReturnType.RETURNMESSAGE);
		alert.setMsgTitle("部门同步任务执行情况（HR-->钉钉）");
		StringBuilder returnmsg = new StringBuilder();
		returnmsg.append("---------开始同步部门架构-------------------\n");
		StringBuilder sql = new StringBuilder();
		//初始化参数
		String salesDepartment;//营业部id
		String branchOffice;//分公司id
		String VentureOffice;//创投id
		try {
			Properties pro = new Properties();
			InputStream in = SyncWorkFlow.class.getClassLoader().getResourceAsStream("dingtalk.properties");
			pro.load(in);
			salesDepartment = pro.getProperty("salesDepartment");
			branchOffice = pro.getProperty("branchOffice");
			VentureOffice = pro.getProperty("VentureOffice");
			in.close();
		} catch (Exception e) {
			Logger.error(e);
			throw new BusinessException(e);
		}
		//---------------------------------开始同步新增、更新---------------------------
		// 正常推送的部门
		sql.delete(0, sql.length());
		//		sql.append("select * from org_dept where isnull(dr,0)=0 and hrcanceled = 'N' order by def1 desc,innercode asc");//先把没有同步的放到前面，先新增部门，再按部门级别排序，父部门在前面（父部门优先进行新增、更新）
		sql.append("select b.name,b.pk_dept,b.def1,b.pk_fatherorg,null parentid,c.timecardid principal from org_dept b left join org_corp a on a.pk_corp = b.pk_org left join tbm_psndoc c on c.pk_psndoc = b.principal where a.name in( '广州证券股份有限公司','广证领秀投资有限公司') and nvl(b.dr,0)=0 and b.hrcanceled = 'N' order by b.def1 desc,b.innercode asc,b.pk_fatherorg desc");
		List<Map<String, String>> depts = (List<Map<String, String>>) getDao().executeQuery(sql.toString(), new MapListProcessor());
		// 营业部，在“经纪业务事业部”下，要手动创建
		sql.delete(0, sql.length());
		sql.append("select b.name,b.pk_dept,b.def1,case when b.pk_fatherorg='~' then null else b.pk_fatherorg end pk_fatherorg,'" + salesDepartment + "' parentid,c.timecardid principal from org_dept b left join org_corp a on a.pk_corp = b.pk_org left join tbm_psndoc c on c.pk_psndoc = b.principal where a.pk_fatherorg =(select pk_corp from org_corp where name = '营业部') and nvl(b.dr,0)=0 and b.hrcanceled = 'N'");
		List<Map<String, String>> depts2 = (List<Map<String, String>>) getDao().executeQuery(sql.toString(), new MapListProcessor());
		depts.addAll(depts2);
		// 分公司，在“客户与机构管理总部”，要手动创建
		sql.delete(0, sql.length());
		sql.append("select b.name,b.pk_dept,b.def1,case when b.pk_fatherorg='~' then null else b.pk_fatherorg end pk_fatherorg,'" + branchOffice + "' parentid,c.timecardid principal from org_dept b left join org_corp a on a.pk_corp = b.pk_org left join tbm_psndoc c on c.pk_psndoc = b.principal where a.pk_fatherorg =(select pk_corp from org_corp where name = '分公司') and nvl(b.dr,0)=0 and b.hrcanceled = 'N'");
		List<Map<String, String>> depts3 = (List<Map<String, String>>) getDao().executeQuery(sql.toString(), new MapListProcessor());
		depts.addAll(depts3);
		// 领袖资本
		//		sql.delete(0, sql.length());
		//		sql.append("select b.name,b.pk_dept,b.def1,case when b.pk_fatherorg='~' then null else b.pk_fatherorg end pk_fatherorg,'50838372' parentid from org_dept b left join org_corp a on a.pk_corp = b.pk_org where a.pk_corp =(select pk_corp from org_corp where name = '广证领秀投资有限公司') and nvl(b.dr,0)=0 and b.hrcanceled = 'N'");
		//		List<Map<String, String>> depts4 = (List<Map<String, String>>) getDao().executeQuery(sql.toString(), new MapListProcessor());
		//		depts.addAll(depts4);
		// 广州证券创新投资管理有限公司，一级部门，要手动创建
		sql.delete(0, sql.length());
		sql.append("select b.name,b.pk_dept,b.def1,case when b.pk_fatherorg='~' then null else b.pk_fatherorg end pk_fatherorg,'" + VentureOffice + "' parentid,c.timecardid principal from org_dept b left join org_corp a on a.pk_corp = b.pk_org left join tbm_psndoc c on c.pk_psndoc = b.principal where a.pk_corp =(select pk_corp from org_corp where name = '广州证券创新投资管理有限公司') and nvl(b.dr,0)=0 and b.hrcanceled = 'N'");
		List<Map<String, String>> depts5 = (List<Map<String, String>>) getDao().executeQuery(sql.toString(), new MapListProcessor());
		depts.addAll(depts5);
		returnmsg.append("本次同步部门数量总计:" + depts.size());
		for (Map<String, String> dept : depts) {
			String parentId = null;
			if (dept.get("pk_fatherorg") == null) {
				parentId = dept.get("parentid");
			} else {
				sql.delete(0, sql.length());
				sql.append("select def1 from org_dept  where pk_dept='" + dept.get("pk_fatherorg") + "'");
				parentId = (String) getDao().executeQuery(sql.toString(), new ColumnProcessor());
			}
			if (dept.get("def1") == null || "".equals(dept.get("def1").trim())) {
				//尚未保存钉钉部门id,可能为新增部门，也可能上次同步没保存到id
				//				returnmsg.append("「新增部门」:" + dept.get("name"));
				creatDept(dept.get("pk_dept"), dept.get("name"), parentId);
			} else {//更新信息
				updateDept(dept.get("pk_dept"), dept.get("def1"), dept.get("name"), parentId, dept.get("principal"));
			}

		}
		//---------------------------------开始同步删除---------------------------
		sql.delete(0, sql.length());
		sql.append("select def1 id from org_dept  where hrcanceled = 'Y' order by innercode desc ");//  nvl(dr,0)=0 and nvl(def1,0)<>0 and
		//先同步HR已撤销，但钉钉上还有的部门
		List<String> deleteIds = (List<String>) getDao().executeQuery(sql.toString(), new ColumnListProcessor());

		if (deleteIds != null && deleteIds.size() > 0) {
			//删除钉钉对应的部门
			for (int i = 0; i < deleteIds.size(); i++) {
				try {
					DepartmentHelper.deleteDepartment(getToken(), Long.parseLong(deleteIds.get(i)));
					returnmsg.append("本次删除部门__部门id：" + deleteIds.get(i) + "\n");
				} catch (Exception e) {
					if (e instanceof NumberFormatException) {
						if ("null".equals(e.getMessage())) {
							//没有钉钉的id值，不用动
						} else {
							//可能def1（钉钉内部门id）的值有问题了，一般不会出现这种
							Logger.error("「删除部门」失败，部门id(钉钉):" + deleteIds.get(i) + ",id必须可转为Long类型，错误信息:" + e.getMessage(), e);
						}
					} else if (e.getMessage().contains("60003")) {
						//部门不存在，说明已经被删除了，也不用动
					} else if (e.getMessage().contains("60005")) {
						//部门有人员，可能人员还没有同步
						Logger.error("「删除部门」失败，部门id(钉钉):" + deleteIds.get(i) + ",错误信息:" + e.getMessage(), e);
						returnmsg.append("「删除部门」失败，部门id(钉钉):" + deleteIds.get(i) + ",错误信息:" + e.getMessage() + ",将于下次执行任务时删除，或稍后删除\n");
					} else {
						Logger.error("「删除部门」失败，部门id(钉钉):" + deleteIds.get(i) + ",错误信息:" + e.getMessage(), e);
						returnmsg.append("「删除部门」失败，部门id(钉钉):" + deleteIds.get(i) + ",错误信息:" + e.getMessage() + "\n");
						//throw new BusinessException("同步删除失败", e);
					}
				}
			}
			//不更新数据库：不把def1（保存钉钉部门id）置空，会导致新的一次同步发生错误（会尝试删除时，def1=null，无法转为Long类型）
			/*
			if (deleteIds.size() > 0) {
				sql.delete(0, sql.length());
				sql.append("update org_dept set def1=null where def1 in (");
				for (int i = 0; i < deleteIds.size(); i++) {
					if (i > 0) {
						sql.append(",");
					}
					sql.append("'");
					sql.append(deleteIds.get(0));
					sql.append("'");
				}
				sql.append(")");
				getDao().executeUpdate(sql.toString());
			}
			*/

		}
		//---------------------------------删除完成---------------------------
		//---------------------------------------------------------------------
		returnmsg.append("-----------部门同步完成-------------\n");
		returnmsg.append("本次同步在第 " + (syncDeptErrorTimes + 1) + " 次同步时成功\n");
		alert.setReturnObj(returnmsg.toString());
		return alert;
	}

	private String getToken() throws BusinessException {
		try {
			return AuthHelper.getAccessToken();
		} catch (OApiException e) {
			Logger.error(e);
			throw new BusinessException(e);
		}
	}

	public BaseDAO getDao() {
		if (dao == null) {
			this.dao = new BaseDAO();
		}
		return dao;
	}

	public void setDao(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 获取所有的部门信息
	 * 
	 * @return
	 * @throws BusinessException
	 */
	private List<Department> getAllDept() throws BusinessException {
		if (this.alldepts == null || this.alldepts.size() == 0) {
			try {
				this.alldepts = DepartmentHelper.listDepartments(getToken(), "1");
			} catch (Exception e) {
				this.getAllDeptErrorTimes++;
				if (this.getAllDeptErrorTimes < 10) {
					this.alldepts.clear();
					return getAllDept();
				} else {
					throw new BusinessException(e);
				}
			}
		}
		return this.alldepts;
	}

	/**
	 * 新增部门，先确认父部门新增 先于 子部门新增
	 * <p>
	 * 可能出现的情况：
	 * </p>
	 * <p>
	 * 1.部门已存在</br>处理方式:数据库中保存钉钉中的id </br>
	 * </p>
	 * 
	 * @param pk_dept
	 * @param deptName
	 * @param parentId
	 * @throws DAOException
	 * @throws BusinessException
	 */
	private void creatDept(String pk_dept, String deptName, String parentId) throws DAOException, BusinessException {
		try {
			String id = DepartmentHelper.createDepartment(getToken(), deptName, parentId, null, null, null, null, null, null, null, null);
			SQLParameter parameter = new SQLParameter();
			parameter.addParam(id);
			parameter.addParam(pk_dept);
			getDao().executeUpdate("update org_dept set def1=? where pk_dept=?", parameter);
			Department dept = new Department();
			dept.setId(Long.parseLong(id));
			dept.setName(deptName);
			dept.setParentid(Long.parseLong(parentId));
			getAllDept().add(dept);
		} catch (Exception e) {
			if (e.getMessage().contains("60008")) {//钉钉中已存在，HR尚未保存该部门的id到def1
				for (Department department : getAllDept()) {
					if (deptName.equals(department.getName())) {
						SQLParameter parameter = new SQLParameter();
						parameter.addParam(department.getId().toString());
						parameter.addParam(pk_dept);
						getDao().executeUpdate("update org_dept set def1=? where pk_dept=?", parameter);
						break;
					}
				}
			} else {
				Logger.error(e);
				throw new BusinessException(e);
			}
		}
	}

	/**
	 * 更新部门
	 * <p>
	 * 可能出现的情况：
	 * </p>
	 * <p>
	 * 1.部门不存在</br>处理方式:新增部门 </br>
	 * </p>
	 * <p>
	 * 2.父部门不存在</br>处理方式:钉钉id置空(def1=null) </br>
	 * </p>
	 * 
	 * @param pk_dept
	 * @param dingDeptId
	 * @param deptName
	 * @param parentId
	 * @throws BusinessException
	 */
	private void updateDept(String pk_dept, String dingDeptId, String deptName, String parentId, String deptManagerUseridList)
			throws BusinessException {
		try {
			DepartmentHelper.updateDepartment(getToken(), Long.parseLong(dingDeptId), deptName, parentId, null, null, null, deptManagerUseridList, null, null, null, null, null, null, null);
		} catch (Exception e) {
			if (e.getMessage().contains("60003")) {//部门不存在,新增部门
				creatDept(pk_dept, deptName, parentId);
			} else if (e.getMessage().contains("60004")) {//父部门不存在,def1置空
				StringBuilder sql = new StringBuilder();
				sql.append("update org_dept set def1=null where def1 ='" + parentId + "'");
				getDao().executeUpdate(sql.toString());
			} else if (e.getMessage().contains("40031")) {//不合法的UserID列表:只能选择本部门的员工，不可跨部门选择以及子部门中的员工
				//人员还没有过来，或者人员有错，所以不更新主管先。
				Logger.error(e + "尝试更新失败，部门名称为「测试新增部门」，只能选择本部门的员工，不可跨部门选择以及子部门中的员工");
				updateDept(pk_dept, dingDeptId, deptName, parentId, null);
			} else {
				Logger.error(e);
				throw new BusinessException(e);
			}
		}
	}
}
