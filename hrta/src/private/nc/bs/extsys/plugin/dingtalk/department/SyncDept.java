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
		alert.setMsgTitle("����ͬ������ִ�������HR-->������");
		StringBuilder returnmsg = new StringBuilder();
		returnmsg.append("---------��ʼͬ�����żܹ�-------------------\n");
		StringBuilder sql = new StringBuilder();
		//��ʼ������
		String salesDepartment;//Ӫҵ��id
		String branchOffice;//�ֹ�˾id
		String VentureOffice;//��Ͷid
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
		//---------------------------------��ʼͬ������������---------------------------
		// �������͵Ĳ���
		sql.delete(0, sql.length());
		//		sql.append("select * from org_dept where isnull(dr,0)=0 and hrcanceled = 'N' order by def1 desc,innercode asc");//�Ȱ�û��ͬ���ķŵ�ǰ�棬���������ţ��ٰ����ż������򣬸�������ǰ�棨���������Ƚ������������£�
		sql.append("select b.name,b.pk_dept,b.def1,b.pk_fatherorg,null parentid,c.timecardid principal from org_dept b left join org_corp a on a.pk_corp = b.pk_org left join tbm_psndoc c on c.pk_psndoc = b.principal where a.name in( '����֤ȯ�ɷ����޹�˾','��֤����Ͷ�����޹�˾') and nvl(b.dr,0)=0 and b.hrcanceled = 'N' order by b.def1 desc,b.innercode asc,b.pk_fatherorg desc");
		List<Map<String, String>> depts = (List<Map<String, String>>) getDao().executeQuery(sql.toString(), new MapListProcessor());
		// Ӫҵ�����ڡ�����ҵ����ҵ�����£�Ҫ�ֶ�����
		sql.delete(0, sql.length());
		sql.append("select b.name,b.pk_dept,b.def1,case when b.pk_fatherorg='~' then null else b.pk_fatherorg end pk_fatherorg,'" + salesDepartment + "' parentid,c.timecardid principal from org_dept b left join org_corp a on a.pk_corp = b.pk_org left join tbm_psndoc c on c.pk_psndoc = b.principal where a.pk_fatherorg =(select pk_corp from org_corp where name = 'Ӫҵ��') and nvl(b.dr,0)=0 and b.hrcanceled = 'N'");
		List<Map<String, String>> depts2 = (List<Map<String, String>>) getDao().executeQuery(sql.toString(), new MapListProcessor());
		depts.addAll(depts2);
		// �ֹ�˾���ڡ��ͻ�����������ܲ�����Ҫ�ֶ�����
		sql.delete(0, sql.length());
		sql.append("select b.name,b.pk_dept,b.def1,case when b.pk_fatherorg='~' then null else b.pk_fatherorg end pk_fatherorg,'" + branchOffice + "' parentid,c.timecardid principal from org_dept b left join org_corp a on a.pk_corp = b.pk_org left join tbm_psndoc c on c.pk_psndoc = b.principal where a.pk_fatherorg =(select pk_corp from org_corp where name = '�ֹ�˾') and nvl(b.dr,0)=0 and b.hrcanceled = 'N'");
		List<Map<String, String>> depts3 = (List<Map<String, String>>) getDao().executeQuery(sql.toString(), new MapListProcessor());
		depts.addAll(depts3);
		// �����ʱ�
		//		sql.delete(0, sql.length());
		//		sql.append("select b.name,b.pk_dept,b.def1,case when b.pk_fatherorg='~' then null else b.pk_fatherorg end pk_fatherorg,'50838372' parentid from org_dept b left join org_corp a on a.pk_corp = b.pk_org where a.pk_corp =(select pk_corp from org_corp where name = '��֤����Ͷ�����޹�˾') and nvl(b.dr,0)=0 and b.hrcanceled = 'N'");
		//		List<Map<String, String>> depts4 = (List<Map<String, String>>) getDao().executeQuery(sql.toString(), new MapListProcessor());
		//		depts.addAll(depts4);
		// ����֤ȯ����Ͷ�ʹ������޹�˾��һ�����ţ�Ҫ�ֶ�����
		sql.delete(0, sql.length());
		sql.append("select b.name,b.pk_dept,b.def1,case when b.pk_fatherorg='~' then null else b.pk_fatherorg end pk_fatherorg,'" + VentureOffice + "' parentid,c.timecardid principal from org_dept b left join org_corp a on a.pk_corp = b.pk_org left join tbm_psndoc c on c.pk_psndoc = b.principal where a.pk_corp =(select pk_corp from org_corp where name = '����֤ȯ����Ͷ�ʹ������޹�˾') and nvl(b.dr,0)=0 and b.hrcanceled = 'N'");
		List<Map<String, String>> depts5 = (List<Map<String, String>>) getDao().executeQuery(sql.toString(), new MapListProcessor());
		depts.addAll(depts5);
		returnmsg.append("����ͬ�����������ܼ�:" + depts.size());
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
				//��δ���涤������id,����Ϊ�������ţ�Ҳ�����ϴ�ͬ��û���浽id
				//				returnmsg.append("���������š�:" + dept.get("name"));
				creatDept(dept.get("pk_dept"), dept.get("name"), parentId);
			} else {//������Ϣ
				updateDept(dept.get("pk_dept"), dept.get("def1"), dept.get("name"), parentId, dept.get("principal"));
			}

		}
		//---------------------------------��ʼͬ��ɾ��---------------------------
		sql.delete(0, sql.length());
		sql.append("select def1 id from org_dept  where hrcanceled = 'Y' order by innercode desc ");//  nvl(dr,0)=0 and nvl(def1,0)<>0 and
		//��ͬ��HR�ѳ������������ϻ��еĲ���
		List<String> deleteIds = (List<String>) getDao().executeQuery(sql.toString(), new ColumnListProcessor());

		if (deleteIds != null && deleteIds.size() > 0) {
			//ɾ��������Ӧ�Ĳ���
			for (int i = 0; i < deleteIds.size(); i++) {
				try {
					DepartmentHelper.deleteDepartment(getToken(), Long.parseLong(deleteIds.get(i)));
					returnmsg.append("����ɾ������__����id��" + deleteIds.get(i) + "\n");
				} catch (Exception e) {
					if (e instanceof NumberFormatException) {
						if ("null".equals(e.getMessage())) {
							//û�ж�����idֵ�����ö�
						} else {
							//����def1�������ڲ���id����ֵ�������ˣ�һ�㲻���������
							Logger.error("��ɾ�����š�ʧ�ܣ�����id(����):" + deleteIds.get(i) + ",id�����תΪLong���ͣ�������Ϣ:" + e.getMessage(), e);
						}
					} else if (e.getMessage().contains("60003")) {
						//���Ų����ڣ�˵���Ѿ���ɾ���ˣ�Ҳ���ö�
					} else if (e.getMessage().contains("60005")) {
						//��������Ա��������Ա��û��ͬ��
						Logger.error("��ɾ�����š�ʧ�ܣ�����id(����):" + deleteIds.get(i) + ",������Ϣ:" + e.getMessage(), e);
						returnmsg.append("��ɾ�����š�ʧ�ܣ�����id(����):" + deleteIds.get(i) + ",������Ϣ:" + e.getMessage() + ",�����´�ִ������ʱɾ�������Ժ�ɾ��\n");
					} else {
						Logger.error("��ɾ�����š�ʧ�ܣ�����id(����):" + deleteIds.get(i) + ",������Ϣ:" + e.getMessage(), e);
						returnmsg.append("��ɾ�����š�ʧ�ܣ�����id(����):" + deleteIds.get(i) + ",������Ϣ:" + e.getMessage() + "\n");
						//throw new BusinessException("ͬ��ɾ��ʧ��", e);
					}
				}
			}
			//���������ݿ⣺����def1�����涤������id���ÿգ��ᵼ���µ�һ��ͬ���������󣨻᳢��ɾ��ʱ��def1=null���޷�תΪLong���ͣ�
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
		//---------------------------------ɾ�����---------------------------
		//---------------------------------------------------------------------
		returnmsg.append("-----------����ͬ�����-------------\n");
		returnmsg.append("����ͬ���ڵ� " + (syncDeptErrorTimes + 1) + " ��ͬ��ʱ�ɹ�\n");
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
	 * ��ȡ���еĲ�����Ϣ
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
	 * �������ţ���ȷ�ϸ��������� ���� �Ӳ�������
	 * <p>
	 * ���ܳ��ֵ������
	 * </p>
	 * <p>
	 * 1.�����Ѵ���</br>����ʽ:���ݿ��б��涤���е�id </br>
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
			if (e.getMessage().contains("60008")) {//�������Ѵ��ڣ�HR��δ����ò��ŵ�id��def1
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
	 * ���²���
	 * <p>
	 * ���ܳ��ֵ������
	 * </p>
	 * <p>
	 * 1.���Ų�����</br>����ʽ:�������� </br>
	 * </p>
	 * <p>
	 * 2.�����Ų�����</br>����ʽ:����id�ÿ�(def1=null) </br>
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
			if (e.getMessage().contains("60003")) {//���Ų�����,��������
				creatDept(pk_dept, deptName, parentId);
			} else if (e.getMessage().contains("60004")) {//�����Ų�����,def1�ÿ�
				StringBuilder sql = new StringBuilder();
				sql.append("update org_dept set def1=null where def1 ='" + parentId + "'");
				getDao().executeUpdate(sql.toString());
			} else if (e.getMessage().contains("40031")) {//���Ϸ���UserID�б�:ֻ��ѡ�񱾲��ŵ�Ա�������ɿ粿��ѡ���Լ��Ӳ����е�Ա��
				//��Ա��û�й�����������Ա�д����Բ����������ȡ�
				Logger.error(e + "���Ը���ʧ�ܣ���������Ϊ�������������š���ֻ��ѡ�񱾲��ŵ�Ա�������ɿ粿��ѡ���Լ��Ӳ����е�Ա��");
				updateDept(pk_dept, dingDeptId, deptName, parentId, null);
			} else {
				Logger.error(e);
				throw new BusinessException(e);
			}
		}
	}
}
