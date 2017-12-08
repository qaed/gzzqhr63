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
	/** 审批流审批方式和参数对应关系 */
	public static final HashMap<String, String> hashBillTypePara = new HashMap<String, String>() {
		{
			put("6101", "HI0006"); // 入职单据审批方式
			put("6111", "TRN0001"); // 转正单据审批方式
			put("6113", "TRN0002"); // 调配单据审批方式
			put("6115", "TRN0003"); // 离职单据审批方式
			put("6117", "TRN0004"); // 兼职单据审批方式

			put("6301", "HRWA006"); // 定调资申请审批方式
			put("6302", "HRWA005"); // 薪资发放审批方式

			put("6401", "TBM0001"); // 调班单据审批方式
			put("6402", "TBM0002"); // 签卡单据审批方式
			put("6403", "HRBT0001"); // 出差单据审批方式
			put("6404", "HRLM0001"); // 休假单据审批方式
			put("6405", "HROM0001"); // 加班单据审批方式

			put("6802", "HRRM0003"); // 内聘单据审批方式
			put("6801", "HRRM0004"); // 录用单据审批方式
			// 这两个好像被注释掉了
			// put("6909", "TRM0001"); // 培训计划单据审批方式
			// put("6911", "TRM0002"); // 培训活动单据审批方式
			// 这些是以前的人新增的
			put("6B01", "HRC003"); // 人才梯队单据审批方式
			put("6406", "HRLO0001"); // 销假单据审批方式
			put("6905", "HRTRM0005"); // 培训报名审批方式
			put("6902", "HRTRM0009"); // 部门培训需求审批方式
			put("6901", "HRTRM0007"); // 员工培训需求审批方式
			put("6904", "HRTRM0003"); // 培训活动审批方式
			put("6903", "HRTRM0001"); // 培训计划审批方式

			// tsy 新增
			put("6407", "HRTA0001"); // 月报单据审批方式
		}
	};

	/*********************************************************************************************************
	 * 批量审批校验
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
	 * 批量提交即审批单据
	 * 
	 * @param pfAggVOs
	 * @return PfProcessBatchRetObject
	 * @throws BusinessException
	 ************************************************************************************************************/
	PfProcessBatchRetObject batchCommitBill(AggregatedValueObject[] pfAggVOs) throws BusinessException;

	/*****************************************************************************************
	 * 批量弃审校验
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
	 * 用于批量收回的单个单据收回操作,另起事务
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
	 * 用于审批流中收回单据时响应的动作，该动作中会更新单据的状态为“编写中”，同时删除审批流实例<br>
	 * Created on 2007-10-11 11:25:08<br>
	 * 
	 * @author Rocex Wang
	 * @param pfAggVO
	 * @return Object
	 * @throws BusinessException
	 ********************************************************************************************************/
	Object callbackBill(AggregatedValueObject pfAggVO) throws BusinessException;

	/*****************************************************************************************
	 * 批量收回单据
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
	 * 收回校验
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
	 * 批量校验制单人是否有单据中流程类型对应的审批流
	 * 
	 * @param aggvos
	 * @return PfProcessBatchRetObject
	 * @throws BusinessException
	 ************************************************************************************************************/
	PfProcessBatchRetObject checkExistWorkflowDefinition(AggregatedValueObject[] aggvos) throws BusinessException;

	/*****************************************************************************************
	 * 用于批量提交即通过的单个单据提交操作,另起事务
	 * 
	 * @param aggVO
	 * @return AggregatedValueObject
	 * @throws BusinessException
	 ************************************************************************************************************/
	AggregatedValueObject commitBill_RequiresNew(AggregatedValueObject aggVO) throws BusinessException;

	/*****************************************************************************************
	 * 删除一张或多张单据
	 * 
	 * @param objVOs 待删除的单据
	 * @throws BusinessException
	 ************************************************************************************************************/
	void deleteMultiObjects(Object objVOs) throws BusinessException;

	/**
	 * 删除校验
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
	 * 删除单据的审批意见
	 * 
	 * @param pfAggVO
	 * @throws BusinessException
	 */
	void deleteWorkflowNote(AggregatedValueObject pfAggVO) throws BusinessException;

	void deleteOldWorkflowNote(String paramString, String[] paramArrayOfString) throws BusinessException;

	/*****************************************************************************************
	 * 批量直批的默认实现,能满足大部分审批流的要求
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
	 * 批量直批的默认实现,能满足大部分审批流的要求。（薪资业务）拓展，用于直批后在同一事务中进行业务处理，回调机制。
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
	 * 批量弃审单据,能满足大部分审批流的要求
	 * 
	 * @param bills
	 * @return PfProcessBatchRetObject
	 * @throws BusinessException
	 ************************************************************************************************************/
	PfProcessBatchRetObject directUnApprove(AggregatedValueObject[] bills) throws BusinessException;

	/*****************************************************************************************
	 * 得到单据主键的sql片段
	 * 
	 * @param iBillStatus
	 * @param billType
	 * @return String 格式 'xxxxxxxxxxxxxxxxx','xxxxxxxxxxxxxxx','xxxxxxxxxxxxxxxxx'
	 * @throws BusinessException
	 ************************************************************************************************************/
	String getBillIdSql(int iBillStatus, String billType) throws BusinessException;

	/***********************************************************************************************************
	 * 打开审批流节点的时候做一些初始化取值的操作，合并到这一个方法中执行<br>
	 * Created on 2009-8-21 14:44:55<br>
	 * 
	 * @param strBillTypeCode 单据类型
	 * @param strPk_corp 当前公司主键
	 * @param strAutoBillCodeParaCode 参数设置中“是否自动生成单据编码”的参数编码
	 * @return Object[] 如果没有值，该元素中放null；格式：{(Boolean)是否自动生成单据编码,(Boolean)单据编码是否集团唯一}
	 * @throws BusinessException
	 * @author Rocex Wang
	 ***************************************************************************/
	Object[] initPF(String strBillTypeCode, String strPk_corp, String strAutoBillCodeParaCode) throws BusinessException;

	/***************************************************************************
	 * 查询符合条件的单据<br>
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
	 * 查询得到审批流中刚好是轮到特定人可以审批的单据；根据条件，还可以查询到直批中的所有单据 <br>
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
	 * 轮到审批的所有审批流<br>
	 * Created on 2007-6-5 14:40:12<br>
	 * 
	 * @author Rocex Wang
	 * @param strUserPK
	 * @return WorkflownoteVO[]
	 * @throws BusinessException
	 ********************************************************************************************************/
	WorkflownoteVO[] queryWorkingPF(String strUserPK) throws BusinessException;

	/*********************************************************************************************************
	 * 轮到审批的所有审批流中的单据主键<br>
	 * Created on 2007-6-5 17:37:14<br>
	 * 
	 * @author Rocex Wang
	 * @param strUserPK
	 * @return String[]
	 * @throws BusinessException
	 ********************************************************************************************************/
	String[] queryWorkingPFBillPk(String strUserPK) throws BusinessException;

	/***************************************************************************
	 * 提交的时候默认选中所有表体中的是否审批通过字段；当收回的时候默认取消所有的选中<br>
	 * Created on 2009-8-24 14:01:33<br>
	 * 
	 * @param strBillPk 单据主键
	 * @param blSelected 是否选中
	 * @param strWhere 查询子表的附加条件
	 * @param headClass 主表vo类
	 * @param bodyClasses 子表vo类
	 * @param strBodyTabCodes 子表tabcodes
	 * @param strBodyFks 主表在子表中的外键
	 * @param strPrintSubNotAllFieldCode 子表中是否审批通过的字段名
	 * @author Rocex Wang
	 * @throws BusinessException
	 ***************************************************************************/
	void selectAllBodyData(Class headClass, Class bodyClasses[], String strBodyTabCodes[], String strBodyFks[], String strBillPk, String strWhere, String strPrintSubNotAllFieldCode, boolean blSelected)
			throws BusinessException;

	/*****************************************************************************
	 * 将单据分为直批单与审批单
	 * 
	 * @param aggvos
	 * @return HashMap< String , ArrayList< AggregatedValueObject > >
	 * @throws BusinessException
	 ************************************************************************************************************/
	HashMap<String, ArrayList<AggregatedValueObject>> separateBill(AggregatedValueObject[] aggvos) throws BusinessException;

	/*************************************************************************************************************
	 * 单个直批 另起事务 与directApprove一起的
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
	 * 单个直批 另起事务 与directApprove一起的。（薪资业务）拓展，用于直批后在同一事务中进行业务处理，回调机制。
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
	 * 单个直批弃审 另起事务 与directUnApprove一起的
	 * 
	 * @param aggregatedValueObject
	 * @return AggregatedValueObject
	 * @throws BusinessException
	 ************************************************************************************************************/
	AggregatedValueObject singleDirectUnApprove_RequiresNew(AggregatedValueObject aggregatedValueObject) throws BusinessException;

	/*********************************************************************************************************
	 * 用于审批流中提交时响应的动作，该动作中会更新单据的状态为“已提交”<br>
	 * Created on 2007-10-11 11:36:34<br>
	 * 
	 * @author Rocex Wang
	 * @param pfAggVO
	 * @return Object
	 * @throws BusinessException
	 ********************************************************************************************************/
	Object submitBill(PFAggVO pfAggVO) throws BusinessException;

	/*****************************************************************************************
	 * 批量提交校验,主要是单据状态的校验
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
	 * 弃审时候的操作<br>
	 * Created on 2009-9-10 16:13:37<br>
	 * 
	 * @param pfAggVO 待审批的单据VO
	 * @param newWorknoteVO 弃审时候添加的审批意见
	 * @return WorkflownoteVO 返回新增的审批意见
	 * @throws BusinessException
	 * @author Rocex Wang
	 ***************************************************************************/
	WorkflownoteVO unApproveDirectBill(AggregatedValueObject pfAggVO, WorkflownoteVO newWorknoteVO) throws BusinessException;

	/**
	 * 检查当前审批的单据中是否同时存在两种审批方式的单据
	 * 
	 * @param bills
	 * @throws BusinessException
	 */
	void validateApproveType(AggregatedValueObject... bills) throws BusinessException;
}
