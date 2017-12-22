package nc.itf.ta;

import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.pub.BusinessException;
import nc.vo.ta.monthstat.DeptMonthStatVO;
import nc.vo.ta.monthstat.MonthStatVO;
import nc.vo.ta.monthstat.MonthWorkVO;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.uif2.LoginContext;

public interface IMonthStatQueryMaintain {

	/**
	 * �����������ڼ��ѯ�����±�
	 * @param context
	 * @param condition
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	MonthStatVO[] queryByCondition(LoginContext context, FromWhereSQL fromWhereSQL, String year, String month, boolean showNoDataRecord)
			throws BusinessException;

	/**
	 * �����������ڼ��ѯ�����±�
	 * @param context
	 * @param condition
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	MonthStatVO[] queryByCondition(LoginContext context, String[] pk_psndocs, String year, String month, boolean showNoDataRecord)
			throws BusinessException;

	/**
	 * �����������ڼ��ѯ�����±�
	 * @param context
	 * @param pk_depts 
	 * @param condition
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	DeptMonthStatVO[] queryDMSVOByCondition(LoginContext context, String[] pk_depts, FromWhereSQL fromWhereSQL, String year, String month, boolean showNoDataRecord)
			throws BusinessException;

	/**
	 * ���ݲ��š��ڼ��ѯ�����±�
	 * �����±���ϸ��ѯ����ѯ�ڱ����ű��ڼ��ڵ�������Ա
	 * @param context
	 * @param pk_dept
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	MonthStatVO[] queryByDeptPeriod(LoginContext context, String pk_dept, String year, String month) throws BusinessException;

	/**
	 * �����������ڼ��ѯ�����±�
	 * @param context
	 * @param condition
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	MonthStatVO[] queryByConditionAndDept(String pk_dept, boolean containsSubDepts, FromWhereSQL fromWhereSQL, String year, String month, boolean showNoDataRecord)
			throws BusinessException;

	/**
	 * �����������ڼ��ѯ�±�δ������Ա
	 * �߼����ձ���΢��һ��
	 * �ձ�ͳ��δ���ɵ��߼���������
	 * ��Ϊÿ����Ĭ�϶���һ���±���¼������ڼ��ʱ���ÿ���˶�������һ�ڵ��±���¼������˲��������������ж��Ƿ�δ����
	 * Ҫ����iseffective�ֶ����жϣ�������ڼ��ʱ���±����ݱ�insert��iseffectiveδN,��ʾ������Ȼ�����ˣ�������Ȼ����
	 * δ���ɣ�ֻ���ڼ�����ߵ���󣬲Ż���Y
	 * @param context
	 * @param fromWhereSQL
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	TBMPsndocVO[] queryUnGenerateByCondition(LoginContext context, FromWhereSQL fromWhereSQL, String year, String month)
			throws BusinessException;

	/**
	 * ����������ѯ���ţ��Ƿ�����¼��ɲ������ƣ���δ���ɿ����±���Ա
	 * @param pk_dept
	 * @param containsSubDept
	 * @param fromWhereSQL
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	TBMPsndocVO[] queryUnGenerateByConditionAndDept(String pk_dept, boolean containsSubDepts, FromWhereSQL fromWhereSQL, String year, String month)
			throws BusinessException;

	/**
	 * ��ѯĳԱ��ĳ�ڼ���±�
	 * @param pk_psndoc
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	MonthStatVO queryByPsn(String pk_psndoc, String year, String month) throws BusinessException;

	/**
	 * ��ѯĳԱ��ĳ��Ŀ����±�
	 * @param pk_psndoc
	 * @param year
	 * @return
	 * @throws BusinessException
	 */
	MonthStatVO[] queryByPsn(String pk_psndoc, String year) throws BusinessException;

	/**
	 * ��ѯָ����Ա�ӿ�ʼ��Ȼ���µ�������Ȼ����֮������п����±�
	 * ����ʼ=����������ʾ�ڴ���Ȼ���ڣ����µ��±���¼�����±���Ӧ�Ŀ����ڼ������Ȼ���н��������Ǵ��������µĿ����±�����
	 * ÿ��HR��֯������������������Ȼ���ڣ��ڶ��HR��֯�����±���¼���򷵻ض��HR��֯�ڵ��±���¼
	 * ����ʼ<��������ӿ�ʼ�¿�ʼ��һֱ�������£�ÿ���¶���������߼���ѯһ�Σ�Ȼ��HR��֯���飬������֮������
	 * ���±���year+month��������м��пյ��ڼ䣬��Ҫ���루��ֹ�еĿ����ڼ�����Ȼ�µ��м䣬����Ȼ������ȥ�飬��Զ���鲻����
	 * @param pk_psndoc
	 * @param beginYear
	 * @param beginMonth
	 * @param endYear
	 * @param endMonth
	 * @return
	 * @throws BusinessException
	 */
	MonthStatVO[] queryByPsnAndNatualYearMonth(String pk_psndoc, String beginYear, String beginMonth, String endYear, String endMonth)
			throws BusinessException;

	/**
	 * ��ѯĳԱ���ĳ����������������
	 * @param pk_org
	 * @param pk_psndoc
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	MonthWorkVO[] queryMonthWorkVOsByPsn(String pk_org, String pk_psndoc, String year, String month) throws BusinessException;

	/**
	 * ����pkֵ��ѯ���±����ŵ����м�¼�����ڵ�����죬�Զ��������и��·ݵ���
	 * @param pk_org
	 * @param pk_monthstat
	 * @return
	 * @throws BusinessException
	 */
	MonthStatVO[] queryCurrentMonthDeptBypk(String pk_org, String pk_monthstat) throws BusinessException;

	/**
	 * ͨ��srcid��ѯ�±���portal�����ڵ�
	 * @param srcid
	 * @return
	 * @throws BusinessException
	 */
	MonthStatVO[] queryBySrc(String srcid) throws BusinessException;
}
