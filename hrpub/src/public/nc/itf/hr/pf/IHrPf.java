package nc.itf.hr.pf;

import java.util.ArrayList;
import java.util.HashMap;

import nc.vo.hr.pf.PFAggVO;
import nc.vo.hr.pf.PFQueryParams;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.uap.pf.PfProcessBatchRetObject;
import nc.vo.uif2.LoginContext;

/*************************************************************************************************************
 * <br>
 * Created on 2007-6-5 11:27:59<br>
 * 
 * @author Rocex Wang
 ************************************************************************************************************/
public interface IHrPf {
	/** ������������ʽ�Ͳ�����Ӧ��ϵ */
	public static final HashMap<String, String> hashBillTypePara = new HashMap<String, String>() {
		{
			put("6101", "HI0006"); // ��ְ����������ʽ
			put("6111", "TRN0001"); // ת������������ʽ
			put("6113", "TRN0002"); // ���䵥��������ʽ
			put("6115", "TRN0003"); // ��ְ����������ʽ
			put("6117", "TRN0004"); // ��ְ����������ʽ

			put("6301", "HRWA006"); // ����������������ʽ
			put("6302", "HRWA005"); // н�ʷ���������ʽ

			put("6401", "TBM0001"); // ���൥��������ʽ
			put("6402", "TBM0002"); // ǩ������������ʽ
			put("6403", "HRBT0001"); // �����������ʽ
			put("6404", "HRLM0001"); // �ݼٵ���������ʽ
			put("6405", "HROM0001"); // �Ӱ൥��������ʽ

			put("6802", "HRRM0003"); // ��Ƹ����������ʽ
			put("6801", "HRRM0004"); // ¼�õ���������ʽ
			// ����������ע�͵���
			// put("6909", "TRM0001"); // ��ѵ�ƻ�����������ʽ
			// put("6911", "TRM0002"); // ��ѵ�����������ʽ
			// ��Щ����ǰ����������
			put("6B01", "HRC003"); // �˲��ݶӵ���������ʽ
			put("6406", "HRLO0001"); // ���ٵ���������ʽ
			put("6905", "HRTRM0005"); // ��ѵ����������ʽ
			put("6902", "HRTRM0009"); // ������ѵ����������ʽ
			put("6901", "HRTRM0007"); // Ա����ѵ����������ʽ
			put("6904", "HRTRM0003"); // ��ѵ�������ʽ
			put("6903", "HRTRM0001"); // ��ѵ�ƻ�������ʽ

			// tsy ����
			put("6407", "HRTA0001"); // �±�����������ʽ
		}
	};

	/*********************************************************************************************************
	 * ��������У��
	 * 
	 * @param resourceCode
	 * @param mdOperationCode
	 * @param operationCode
	 * @param pfAggVOs
	 * @return PfProcessBatchRetObject
	 * @throws BusinessException
	 ************************************************************************************************************/
	PfProcessBatchRetObject approveValidation(String operationCode, String mdOperationCode, String resourceCode, AggregatedValueObject... pfAggVOs)
			throws BusinessException;

	/*****************************************************************************************
	 * �����ύ����������
	 * 
	 * @param pfAggVOs
	 * @return PfProcessBatchRetObject
	 * @throws BusinessException
	 ************************************************************************************************************/
	PfProcessBatchRetObject batchCommitBill(AggregatedValueObject[] pfAggVOs) throws BusinessException;

	/*****************************************************************************************
	 * ��������У��
	 * 
	 * @param aggvos
	 * @param blCheckPassIsEnd
	 * @param resourceCode
	 * @param mdOperationCode
	 * @param operationCode
	 * @return PfProcessBatchRetObject
	 * @throws BusinessException
	 ************************************************************************************************************/
	PfProcessBatchRetObject batchUnApproveValidation(boolean blCheckPassIsEnd, String operationCode, String mdOperationCode, String resourceCode, AggregatedValueObject... aggvos)
			throws BusinessException;

	/*****************************************************************************************
	 * ���������ջصĵ��������ջز���,��������
	 * 
	 * @param aggVO
	 * @param blCheckPassIsEnd
	 * @param resourceCode
	 * @param mdOperationCode
	 * @param operationCode
	 * @return AggregatedValueObject
	 * @throws BusinessException
	 ************************************************************************************************************/
	AggregatedValueObject callback_RequiresNew(AggregatedValueObject aggVO, boolean blCheckPassIsEnd, String operationCode, String mdOperationCode, String resourceCode)
			throws BusinessException;

	/************************************************************************************************************************************************************************************************
	 * �������������ջص���ʱ��Ӧ�Ķ������ö����л���µ��ݵ�״̬Ϊ����д�С���ͬʱɾ��������ʵ��<br>
	 * Created on 2007-10-11 11:25:08<br>
	 * 
	 * @author Rocex Wang
	 * @param pfAggVO
	 * @return Object
	 * @throws BusinessException
	 ********************************************************************************************************/
	Object callbackBill(AggregatedValueObject pfAggVO) throws BusinessException;

	/*****************************************************************************************
	 * �����ջص���
	 * 
	 * @param blCheckPassIsEnd
	 * @param operationCode
	 * @param mdOperationCode
	 * @param resourceCode
	 * @param pfAggVOs
	 * @return PfProcessBatchRetObject
	 * @throws BusinessException
	 ************************************************************************************************************/
	PfProcessBatchRetObject callbackBill(boolean blCheckPassIsEnd, String operationCode, String mdOperationCode, String resourceCode, AggregatedValueObject... pfAggVOs)
			throws BusinessException;

	/**
	 * �ջ�У��
	 * 
	 * @param operateCode
	 * @param mdOperateCode
	 * @param resourceCode
	 * @param isCheckPassIsEnd
	 * @param aggVOs
	 * @return PfProcessBatchRetObject
	 * @throws BusinessException
	 */
	PfProcessBatchRetObject callbackValidate(String operateCode, String mdOperateCode, String resourceCode, boolean isCheckPassIsEnd, AggregatedValueObject... aggVOs)
			throws BusinessException;

	/*****************************************************************************************
	 * ����У���Ƶ����Ƿ��е������������Ͷ�Ӧ��������
	 * 
	 * @param aggvos
	 * @return PfProcessBatchRetObject
	 * @throws BusinessException
	 ************************************************************************************************************/
	PfProcessBatchRetObject checkExistWorkflowDefinition(AggregatedValueObject[] aggvos) throws BusinessException;

	/*****************************************************************************************
	 * ���������ύ��ͨ���ĵ��������ύ����,��������
	 * 
	 * @param aggVO
	 * @return AggregatedValueObject
	 * @throws BusinessException
	 ************************************************************************************************************/
	AggregatedValueObject commitBill_RequiresNew(AggregatedValueObject aggVO) throws BusinessException;

	/*****************************************************************************************
	 * ɾ��һ�Ż���ŵ���
	 * 
	 * @param objVOs ��ɾ���ĵ���
	 * @throws BusinessException
	 ************************************************************************************************************/
	void deleteMultiObjects(Object objVOs) throws BusinessException;

	/**
	 * ɾ��У��
	 * 
	 * @param operateCode
	 * @param mdOperateCode
	 * @param resourceCode
	 * @param aggVOs
	 * @return PfProcessBatchRetObject
	 * @throws BusinessException
	 */
	PfProcessBatchRetObject deleteValidation(String operateCode, String mdOperateCode, String resourceCode, AggregatedValueObject... aggVOs)
			throws BusinessException;

	/**
	 * ɾ�����ݵ��������
	 * 
	 * @param pfAggVO
	 * @throws BusinessException
	 */
	void deleteWorkflowNote(AggregatedValueObject pfAggVO) throws BusinessException;

	void deleteOldWorkflowNote(String paramString, String[] paramArrayOfString) throws BusinessException;

	/*****************************************************************************************
	 * ����ֱ����Ĭ��ʵ��,������󲿷���������Ҫ��
	 * 
	 * @param billvos
	 * @param pk_user
	 * @param serverTime
	 * @param approveNote
	 * @param directApproveResult
	 * @return PfProcessBatchRetObject
	 * @throws BusinessException
	 ************************************************************************************************************/
	PfProcessBatchRetObject directApprove(AggregatedValueObject[] billvos, String pk_user, UFDateTime serverTime, String approveNote, int directApproveResult)
			throws BusinessException;

	/*****************************************************************************************
	 * ����ֱ����Ĭ��ʵ��,������󲿷���������Ҫ�󡣣�н��ҵ����չ������ֱ������ͬһ�����н���ҵ�����ص����ơ�
	 * 
	 * @param billvos
	 * @param pk_user
	 * @param serverTime
	 * @param approveNote
	 * @param directApproveResult
	 * @param classname
	 * @param methodname
	 * @param parametertype
	 * @return PfProcessBatchRetObject
	 * @throws BusinessException
	 ************************************************************************************************************/
	PfProcessBatchRetObject directApprove(AggregatedValueObject[] billvos, String pk_user, UFDateTime serverTime, String approveNote, int directApproveResult, String classname, String methodname, String parametertype)
			throws BusinessException;

	/*****************************************************************************************
	 * �������󵥾�,������󲿷���������Ҫ��
	 * 
	 * @param bills
	 * @return PfProcessBatchRetObject
	 * @throws BusinessException
	 ************************************************************************************************************/
	PfProcessBatchRetObject directUnApprove(AggregatedValueObject[] bills) throws BusinessException;

	/*****************************************************************************************
	 * �õ�����������sqlƬ��
	 * 
	 * @param iBillStatus
	 * @param billType
	 * @return String ��ʽ 'xxxxxxxxxxxxxxxxx','xxxxxxxxxxxxxxx','xxxxxxxxxxxxxxxxx'
	 * @throws BusinessException
	 ************************************************************************************************************/
	String getBillIdSql(int iBillStatus, String billType) throws BusinessException;

	/***********************************************************************************************************
	 * ���������ڵ��ʱ����һЩ��ʼ��ȡֵ�Ĳ������ϲ�����һ��������ִ��<br>
	 * Created on 2009-8-21 14:44:55<br>
	 * 
	 * @param strBillTypeCode ��������
	 * @param strPk_corp ��ǰ��˾����
	 * @param strAutoBillCodeParaCode ���������С��Ƿ��Զ����ɵ��ݱ��롱�Ĳ�������
	 * @return Object[] ���û��ֵ����Ԫ���з�null����ʽ��{(Boolean)�Ƿ��Զ����ɵ��ݱ���,(Boolean)���ݱ����Ƿ���Ψһ}
	 * @throws BusinessException
	 * @author Rocex Wang
	 ***************************************************************************/
	Object[] initPF(String strBillTypeCode, String strPk_corp, String strAutoBillCodeParaCode) throws BusinessException;

	/***************************************************************************
	 * ��ѯ���������ĵ���<br>
	 * Created on 2011-6-2 9:27:42<br>
	 * 
	 * @param loginContext
	 * @param clsAggVO
	 * @param strWhere
	 * @param bLazyLoad
	 * @param queryParams
	 * @param strOrderBySQL
	 * @return AggregatedValueObject[]
	 * @throws BusinessException
	 * @author Rocex Wang
	 ***************************************************************************/
	AggregatedValueObject[] queryBill(LoginContext loginContext, Class<? extends AggregatedValueObject> clsAggVO, String strWhere, boolean bLazyLoad, PFQueryParams queryParams, String strOrderBySQL)
			throws BusinessException;

	/******************************************************************************************************
	 * ��ѯ�õ��������иպ����ֵ��ض��˿��������ĵ��ݣ����������������Բ�ѯ��ֱ���е����е��� <br>
	 * Created on 2007-6-5 11:29:12<br>
	 * 
	 * @author Rocex Wang
	 * @param clazz
	 * @param strPkCorp
	 * @param strPkUser
	 * @param strWhere
	 * @return SuperVO[]
	 * @throws BusinessException
	 ********************************************************************************************************/
	SuperVO[] queryWorkingBill(Class clazz, String strPkCorp, String strPkUser, String strWhere) throws BusinessException;

	/*********************************************************************************************************
	 * �ֵ�����������������<br>
	 * Created on 2007-6-5 14:40:12<br>
	 * 
	 * @author Rocex Wang
	 * @param strUserPK
	 * @return WorkflownoteVO[]
	 * @throws BusinessException
	 ********************************************************************************************************/
	WorkflownoteVO[] queryWorkingPF(String strUserPK) throws BusinessException;

	/*********************************************************************************************************
	 * �ֵ������������������еĵ�������<br>
	 * Created on 2007-6-5 17:37:14<br>
	 * 
	 * @author Rocex Wang
	 * @param strUserPK
	 * @return String[]
	 * @throws BusinessException
	 ********************************************************************************************************/
	String[] queryWorkingPFBillPk(String strUserPK) throws BusinessException;

	/***************************************************************************
	 * �ύ��ʱ��Ĭ��ѡ�����б����е��Ƿ�����ͨ���ֶΣ����ջص�ʱ��Ĭ��ȡ�����е�ѡ��<br>
	 * Created on 2009-8-24 14:01:33<br>
	 * 
	 * @param strBillPk ��������
	 * @param blSelected �Ƿ�ѡ��
	 * @param strWhere ��ѯ�ӱ�ĸ�������
	 * @param headClass ����vo��
	 * @param bodyClasses �ӱ�vo��
	 * @param strBodyTabCodes �ӱ�tabcodes
	 * @param strBodyFks �������ӱ��е����
	 * @param strPrintSubNotAllFieldCode �ӱ����Ƿ�����ͨ�����ֶ���
	 * @author Rocex Wang
	 * @throws BusinessException
	 ***************************************************************************/
	void selectAllBodyData(Class headClass, Class bodyClasses[], String strBodyTabCodes[], String strBodyFks[], String strBillPk, String strWhere, String strPrintSubNotAllFieldCode, boolean blSelected)
			throws BusinessException;

	/*****************************************************************************
	 * �����ݷ�Ϊֱ������������
	 * 
	 * @param aggvos
	 * @return HashMap< String , ArrayList< AggregatedValueObject > >
	 * @throws BusinessException
	 ************************************************************************************************************/
	HashMap<String, ArrayList<AggregatedValueObject>> separateBill(AggregatedValueObject[] aggvos) throws BusinessException;

	/*************************************************************************************************************
	 * ����ֱ�� �������� ��directApproveһ���
	 * 
	 * @param aggregatedValueObject
	 * @param pk_user
	 * @param approveTime
	 * @param approveNote
	 * @param directApproveResult
	 * @return AggregatedValueObject
	 * @throws BusinessException
	 ************************************************************************************************************/
	AggregatedValueObject singleDirectApprove_RequiresNew(AggregatedValueObject aggregatedValueObject, String pk_user, UFDateTime approveTime, String approveNote, int directApproveResult)
			throws BusinessException;

	/*************************************************************************************************************
	 * ����ֱ�� �������� ��directApproveһ��ġ���н��ҵ����չ������ֱ������ͬһ�����н���ҵ�����ص����ơ�
	 * 
	 * @param aggregatedValueObject
	 * @param pk_user
	 * @param approveTime
	 * @param approveNote
	 * @param directApproveResult
	 * @param classname
	 * @param methodname
	 * @param parametertype
	 * @return AggregatedValueObject
	 * @throws BusinessException
	 ************************************************************************************************************/
	Object singleDirectApprove_RequiresNew(AggregatedValueObject aggregatedValueObject, String pk_user, UFDateTime approveTime, String approveNote, int directApproveResult, String classname, String methodname, String parametertype)
			throws BusinessException;

	/*********************************************************************************************************
	 * ����ֱ������ �������� ��directUnApproveһ���
	 * 
	 * @param aggregatedValueObject
	 * @return AggregatedValueObject
	 * @throws BusinessException
	 ************************************************************************************************************/
	AggregatedValueObject singleDirectUnApprove_RequiresNew(AggregatedValueObject aggregatedValueObject) throws BusinessException;

	/*********************************************************************************************************
	 * �������������ύʱ��Ӧ�Ķ������ö����л���µ��ݵ�״̬Ϊ�����ύ��<br>
	 * Created on 2007-10-11 11:36:34<br>
	 * 
	 * @author Rocex Wang
	 * @param pfAggVO
	 * @return Object
	 * @throws BusinessException
	 ********************************************************************************************************/
	Object submitBill(PFAggVO pfAggVO) throws BusinessException;

	/*****************************************************************************************
	 * �����ύУ��,��Ҫ�ǵ���״̬��У��
	 * 
	 * @param operationCode
	 * @param mdOperationCode
	 * @param resourceCode
	 * @param iApproveType
	 * @param pfAggVOs
	 * @return PfProcessBatchRetObject
	 * @throws BusinessException
	 ************************************************************************************************************/
	PfProcessBatchRetObject submitValidation(String operationCode, String mdOperationCode, String resourceCode, int iApproveType, AggregatedValueObject... pfAggVOs)
			throws BusinessException;

	/***************************************************************************
	 * ����ʱ��Ĳ���<br>
	 * Created on 2009-9-10 16:13:37<br>
	 * 
	 * @param pfAggVO �������ĵ���VO
	 * @param newWorknoteVO ����ʱ����ӵ��������
	 * @return WorkflownoteVO �����������������
	 * @throws BusinessException
	 * @author Rocex Wang
	 ***************************************************************************/
	WorkflownoteVO unApproveDirectBill(AggregatedValueObject pfAggVO, WorkflownoteVO newWorknoteVO) throws BusinessException;

	/**
	 * ��鵱ǰ�����ĵ������Ƿ�ͬʱ��������������ʽ�ĵ���
	 * 
	 * @param bills
	 * @throws BusinessException
	 */
	void validateApproveType(AggregatedValueObject... bills) throws BusinessException;
}
