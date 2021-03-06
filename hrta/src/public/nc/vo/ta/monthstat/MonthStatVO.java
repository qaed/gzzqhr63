/***************************************************************\
 *     The skeleton of this class is generated by an automatic *
 * code generator for NC product. It is based on Velocity.     *
\***************************************************************/
package nc.vo.ta.monthstat;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.annotation.IDColumn;
import nc.vo.ta.annotation.Table;
import nc.vo.ta.annotation.UniqueColumns;
import nc.vo.ta.formula.GeneralMaps;
import nc.vo.ta.item.ItemVO;
import nc.vo.ta.period.IYearMonthData;
import nc.vo.ta.psndoc.ITBMPsndocVO;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.pub.CommonMethods;
import nc.vo.ta.statistic.IVOWithDynamicAttributes;
import nc.vo.ta.statistic.annotation.InsertColumnsWithoutPK;
import nc.vo.ta.statistic.annotation.ItemClass;
import nc.vo.ta.statistic.annotation.StatbVOClassName;
import nc.vo.ta.vieworder.ViewOrderVO;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * <b> 在此处简要描述此类的功能 </b>
 * <p>
 * 在此处添加此类的描述信息
 * </p>
 * 创建日期:2010-09-30 14:24:14
 * 
 * @author
 * @version NCPrj ??
 */

@Table(tableName = "tbm_monthstat")
@IDColumn(idColumn = "pk_monthstat")
@UniqueColumns(uniqueColumns = "pk_org,pk_psndoc,tbmyear,tbmmonth")
@InsertColumnsWithoutPK(columns = "pk_org,pk_group,pk_psndoc,tbmyear,tbmmonth,iseffective,isapprove,isuseful,approvestatus,billtype,mngpsndoc,mngdept")
@StatbVOClassName(className = "nc.vo.ta.monthstat.MonthStatbVO")
@ItemClass(itemClass = "1")
public class MonthStatVO extends SuperVO implements IVOWithDynamicAttributes, ITBMPsndocVO, IYearMonthData {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8746864583649021969L;
	private java.lang.String pk_monthstat;
	private java.lang.String pk_group;
	private java.lang.String pk_org;
	private java.lang.String pk_psndoc;
	private String pk_psnjob;//虚字段，数据库中没有
	private String pk_tbm_psndoc;//虚字段，数据库中没有
	private java.lang.Integer datastatus;
	private java.lang.String tbmyear;
	private java.lang.String tbmmonth;
	//是否有效。专门用来处理“未生成”统计。考勤月报的数据在考勤期间初始化下期的时候生成，生成后此字段默认为N,在计算或者导入后变为Y。统计未生成的时候，为N的就代表未生成
	private UFBoolean iseffective = UFBoolean.FALSE;
	//是否已审核，默认为N。对于不需要审核的情形，则直接处理为N

	private UFBoolean isapprove = UFBoolean.FALSE;
	//是否可以使用。逻辑是：不需要审核的场景下，默认为Y；需要审核的场景下，默认为N，审核通过后为Y，弃审后为N
	//考勤规则由需要审改为不需要审，此字段update为Y。考勤规则由不需要审改为需要审，则update为isapprove

	private UFBoolean isuseful;
	//子表

	private MonthStatbVO[] monthstatbVOs;

	private MonthWorkVO[] monthworkVOs;
	private java.lang.Integer dr = 0;
	private nc.vo.pub.lang.UFDateTime ts;

	String pk_dept;//虚字段，数据库中没有，用于将人员日报数据按部门分组
	private java.lang.String pk_org_v;//虚字段，人员组织在期间的组织版本
	private java.lang.String pk_dept_v;//虚字段，人员部门在期间的版本

	//存储日报项目以及休假出差加班停工待料值的map
	//对于日报项目，key是f_i_1这种，对于类别，key是pk_timeitem
	protected Map<String, Object> valueMap = new HashMap<String, Object>();
	//在日报界面上修改过的日报项目数据
	protected Map<String, Object> modifiedItemValueMap = new HashMap<String, Object>();
	//在日报界面上修改过的出差加班休假停工待料数据
	protected Map<String, Object> modifiedTimeItemValueMap = new HashMap<String, Object>();

	private String psnName;//人员姓名。用于自助显示，业务节点不使用
	private String orgName;//组织名称。用于自助显示，业务节点不使用
	private String deptName;//部门名称。用于自助显示，业务节点不使用
	private ViewOrderVO[] viewOrderVOs;//显示项目。用于自助显示，业务节点不使用

	public static final String TABLE_NAME = "tbm_monthstat";

	//20171109 tsy 新增关于审批的字段 
	private String billno;
	private String busitype;
	private String billmaker;
	private String approver;
	private Integer approvestatus;
	private String approvenote;
	private UFDateTime approvedate;
	private String transtype;
	private String billtype;
	private String transtypepk;
	private String srcid;
	/**
	 * 部门主管
	 */
	private String mngpsndoc;
	/**
	 * 一级部门
	 */
	private String mngdept;
	//20171109 end

	public static final String PK_MONTHSTAT = "pk_monthstat";
	public static final String PK_GROUP = "pk_group";
	public static final String PK_ORG = "pk_org";
	public static final String PK_PSNDOC = "pk_psndoc";
	public static final String PK_PSNJOB = "pk_psnjob";
	public static final String DATASTATUS = "datastatus";
	public static final String TBMYEAR = "tbmyear";
	public static final String TBMMONTH = "tbmmonth";
	public static final String ISEFFECTIVE = "iseffective";
	public static final String ISAPPROVE = "isapprove";
	public static final String ISUSEFUL = "isuseful";

	public static final String ACTUALWORKDAYS = "actualworkdays";
	public static final String WORKDAYS = "workdays";
	public static final String ACTUALWORKHOURS = "actualworkhours";
	public static final String WORKHOURS = "workhours";

	@Override
	public void setAttributeValue(String name, Object value) {
		String lowerNname = name.toLowerCase();
		boolean isDayItem = GeneralMaps.MONTHITEM_FIELD_TYPE_MAP.containsKey(lowerNname) || lowerNname.startsWith("f_");
		//如果是布尔或者日期型，平台有可能传入字符串，而不是UFBoolean和UFLiteralDate，此时需要自己处理

		if (isDayItem) {
			int type = GeneralMaps.getFieldTypeByName(lowerNname);
			if (type == ItemVO.DATA_TYPE_BOOL && value instanceof String) {
				valueMap.put(name, UFBoolean.valueOf((String) value));
				return;
			}
			if (type == ItemVO.DATA_TYPE_DATE && value instanceof String) {
				valueMap.put(name, UFLiteralDate.getDate((String) value));
				return;
			}
		}
		if (isDayItem || CommonMethods.isPrimaryKey(name)) {
			valueMap.put(name, value);
			return;
		}
		super.setAttributeValue(name, value);
	}

	@Override
	public Object getAttributeValue(String name) {
		String lowerNname = name.toLowerCase();
		if (GeneralMaps.MONTHITEM_FIELD_TYPE_MAP.containsKey(lowerNname) || lowerNname.startsWith("f_") || CommonMethods.isPrimaryKey(name)) {
			return valueMap.get(name);
		}
		return super.getAttributeValue(name);
	}

	/**
	 * 属性pk_monthstat的Getter方法. 创建日期:2010-09-30 14:24:14
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_monthstat() {
		return pk_monthstat;
	}

	/**
	 * 属性pk_monthstat的Setter方法. 创建日期:2010-09-30 14:24:14
	 * 
	 * @param newPk_monthstat java.lang.String
	 */
	public void setPk_monthstat(java.lang.String newPk_monthstat) {
		this.pk_monthstat = newPk_monthstat;
	}

	/**
	 * 属性pk_group的Getter方法. 创建日期:2010-09-30 14:24:14
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_group() {
		return pk_group;
	}

	/**
	 * 属性pk_group的Setter方法. 创建日期:2010-09-30 14:24:14
	 * 
	 * @param newPk_group java.lang.String
	 */
	public void setPk_group(java.lang.String newPk_group) {
		this.pk_group = newPk_group;
	}

	/**
	 * 属性pk_org的Getter方法. 创建日期:2010-09-30 14:24:14
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_org() {
		return pk_org;
	}

	/**
	 * 属性pk_org的Setter方法. 创建日期:2010-09-30 14:24:14
	 * 
	 * @param newPk_org java.lang.String
	 */
	public void setPk_org(java.lang.String newPk_org) {
		this.pk_org = newPk_org;
	}

	/**
	 * 属性pk_psndoc的Getter方法. 创建日期:2010-09-30 14:24:14
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_psndoc() {
		return pk_psndoc;
	}

	/**
	 * 属性pk_psndoc的Setter方法. 创建日期:2010-09-30 14:24:14
	 * 
	 * @param newPk_psndoc java.lang.String
	 */
	public void setPk_psndoc(java.lang.String newPk_psndoc) {
		this.pk_psndoc = newPk_psndoc;
	}

	/**
	 * 属性datastatus的Getter方法. 创建日期:2010-09-30 14:24:14
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getDatastatus() {
		return datastatus;
	}

	/**
	 * 属性datastatus的Setter方法. 创建日期:2010-09-30 14:24:14
	 * 
	 * @param newDatastatus java.lang.Integer
	 */
	public void setDatastatus(java.lang.Integer newDatastatus) {
		this.datastatus = newDatastatus;
	}

	/**
	 * 属性tbmyear的Getter方法. 创建日期:2010-09-30 14:24:14
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getTbmyear() {
		return tbmyear;
	}

	/**
	 * 属性tbmyear的Setter方法. 创建日期:2010-09-30 14:24:14
	 * 
	 * @param newTbmyear java.lang.String
	 */
	public void setTbmyear(java.lang.String newTbmyear) {
		this.tbmyear = newTbmyear;
	}

	/**
	 * 属性tbmmonth的Getter方法. 创建日期:2010-09-30 14:24:14
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getTbmmonth() {
		return tbmmonth;
	}

	/**
	 * 属性tbmmonth的Setter方法. 创建日期:2010-09-30 14:24:14
	 * 
	 * @param newTbmmonth java.lang.String
	 */
	public void setTbmmonth(java.lang.String newTbmmonth) {
		this.tbmmonth = newTbmmonth;
	}

	/**
	 * 属性dr的Getter方法. 创建日期:2010-09-30 14:24:14
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getDr() {
		return dr;
	}

	/**
	 * 属性dr的Setter方法. 创建日期:2010-09-30 14:24:14
	 * 
	 * @param newDr java.lang.Integer
	 */
	public void setDr(java.lang.Integer newDr) {
		this.dr = newDr;
	}

	/**
	 * 属性ts的Getter方法. 创建日期:2010-09-30 14:24:14
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getTs() {
		return ts;
	}

	/**
	 * 属性ts的Setter方法. 创建日期:2010-09-30 14:24:14
	 * 
	 * @param newTs nc.vo.pub.lang.UFDateTime
	 */
	public void setTs(nc.vo.pub.lang.UFDateTime newTs) {
		this.ts = newTs;
	}

	/**
	 * <p>
	 * 取得父VO主键字段.
	 * <p>
	 * 创建日期:2010-09-30 14:24:14
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getParentPKFieldName() {
		return null;
	}

	/**
	 * <p>
	 * 取得表主键.
	 * <p>
	 * 创建日期:2010-09-30 14:24:14
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPKFieldName() {
		return "pk_monthstat";
	}

	/**
	 * <p>
	 * 返回表名称.
	 * <p>
	 * 创建日期:2010-09-30 14:24:14
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "tbm_monthstat";
	}

	/**
	 * <p>
	 * 返回表名称.
	 * <p>
	 * 创建日期:2010-09-30 14:24:14
	 * 
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "tbm_monthstat";
	}

	/**
	 * 按照默认方式创建构造子. 创建日期:2010-09-30 14:24:14
	 */
	public MonthStatVO() {
		super();
	}

	public nc.vo.pub.lang.UFBoolean getIseffective() {
		return iseffective;
	}

	public void setIseffective(nc.vo.pub.lang.UFBoolean iseffective) {
		this.iseffective = iseffective;
	}

	public MonthStatbVO[] getMonthstatbVOs() {
		return monthstatbVOs;
	}

	public void setMonthstatbVOs(MonthStatbVO[] monthstatbVOs) {
		this.monthstatbVOs = monthstatbVOs;
	}

	public MonthWorkVO[] getMonthworkVOs() {
		return monthworkVOs;
	}

	public void setMonthworkVOs(MonthWorkVO[] monthworkVOs) {
		this.monthworkVOs = monthworkVOs;
	}

	public String getPk_psnjob() {
		return pk_psnjob;
	}

	public void setPk_psnjob(String pk_psnjob) {
		this.pk_psnjob = pk_psnjob;
	}

	@Override
	public void clearModifiedValue() {
		modifiedItemValueMap.clear();
		modifiedTimeItemValueMap.clear();
		MonthWorkVO[] workVOs = getMonthworkVOs();
		if (!ArrayUtils.isEmpty(workVOs))
			for (MonthWorkVO mwvo : workVOs) {
				mwvo.clearModifiedValues();
			}

	}

	@Override
	public String[] getDynamicAttributeNames() {
		if (MapUtils.isEmpty(valueMap))
			return null;
		return valueMap.keySet().toArray(new String[0]);
	}

	@Override
	public Object getDynamicAttributeValue(String attName) {
		if (MapUtils.isEmpty(valueMap))
			return null;
		return valueMap.get(attName);
	}

	@Override
	public String[] getInsertValuesWithoutPK() {
		return new String[] { getPk_org(), getPk_group(), getPk_psndoc(), getTbmyear(), getTbmmonth(), "Y", "N", isuseful == null ? "N" : isuseful.toString(),getApprovestatus().toString(),getBilltype(),getMngpsndoc(),getMngdept() };
	}

	@Override
	public Map<String, Object> getModifiedItemValueMap() {
		return modifiedItemValueMap;
	}

	@Override
	public Map<String, Object> getModifiedTimeItemValueMap() {
		return modifiedTimeItemValueMap;
	}

	@Override
	public String getUniqueKey() {
		return getPk_org() + getPk_psndoc() + getTbmyear() + getTbmmonth();
	}

	@Override
	public String[] getUniqueKeys() {
		return new String[] { getPk_org(), getPk_psndoc(), getTbmyear(), getTbmmonth() };
	}

	@Override
	public boolean isModifiedEmpty() {
		boolean b = MapUtils.isEmpty(modifiedItemValueMap) && MapUtils.isEmpty(modifiedTimeItemValueMap);
		if (!b || org.apache.commons.lang.ArrayUtils.isEmpty(monthworkVOs))
			return b;
		for (MonthWorkVO workVO : monthworkVOs) {
			if (!workVO.isModifiedEmpty())
				return false;
		}
		return b;
	}

	@Override
	public void removeModifiedValue(String key) {
		if (CommonMethods.isPrimaryKey(key)) {
			modifiedTimeItemValueMap.remove(key);
			return;
		}
		modifiedItemValueMap.remove(key);
	}

	@Override
	public void setModifiedValue(String key, Object value) {
		if (CommonMethods.isPrimaryKey(key)) {
			modifiedTimeItemValueMap.put(key, value);
			return;
		}
		modifiedItemValueMap.put(key, value);
	}

	@Override
	public void syncModified2Value() {
		valueMap.putAll(modifiedItemValueMap);
		valueMap.putAll(modifiedTimeItemValueMap);
		MonthWorkVO[] workVOs = getMonthworkVOs();
		if (!ArrayUtils.isEmpty(workVOs))
			for (MonthWorkVO mwvo : workVOs) {
				mwvo.syncModified2Value();
			}

	}

	@Override
	public String[] getModifiedDynamicAttributeNames() {
		if (MapUtils.isEmpty(modifiedItemValueMap)) {
			if (MapUtils.isEmpty(modifiedTimeItemValueMap))
				return null;
			return modifiedTimeItemValueMap.keySet().toArray(new String[0]);
		}
		if (MapUtils.isEmpty(modifiedTimeItemValueMap))
			return modifiedItemValueMap.keySet().toArray(new String[0]);
		return (String[]) ArrayUtils.addAll(modifiedItemValueMap.keySet().toArray(new String[0]), modifiedTimeItemValueMap.keySet().toArray(new String[0]));
	}

	/**
	 * 判断是否是“无数据记录”,判断依据是：valueMap里面的value，数值、整型的都为空或者0，布尔型的都为空或者N，date和字符型的都是空
	 * 
	 * @return
	 */
	public boolean isNoDataRecord() {
		if (MapUtils.isEmpty(valueMap))
			return true;
		for (Object value : valueMap.values()) {
			if (value == null)
				continue;
			if (value instanceof UFDouble) {
				UFDouble dbl = (UFDouble) value;
				if (dbl.doubleValue() != 0)
					return false;
				continue;
			}

			if (value instanceof BigDecimal) {
				BigDecimal dbl = (BigDecimal) value;
				if (dbl.doubleValue() != 0)
					return false;
				continue;
			}

			if (value instanceof Integer) {
				Integer intg = (Integer) value;
				if (intg.intValue() != 0)
					return false;
				continue;
			}

			if (value instanceof UFBoolean) {
				UFBoolean b = (UFBoolean) value;
				if (b.booleanValue())

					return false;
				continue;

			}
			if (!StringUtils.isBlank(value.toString()))
				return false;
		}
		return true;
	}

	public UFBoolean getIsapprove() {
		return isapprove;
	}

	public void setIsapprove(UFBoolean isapprove) {
		this.isapprove = isapprove;
	}

	public UFBoolean getIsuseful() {
		return isuseful;
	}

	public void setIsuseful(UFBoolean isuseful) {
		this.isuseful = isuseful;
	}

	@Override
	public String getPk_tbm_psndoc() {
		return pk_tbm_psndoc;
	}

	@Override
	public void setPk_tbm_psndoc(String pkTbmPsndoc) {
		pk_tbm_psndoc = pkTbmPsndoc;
	}

	@Override
	public TBMPsndocVO toTBMPsndocVO() {
		TBMPsndocVO vo = new TBMPsndocVO();
		vo.setPrimaryKey(pk_tbm_psndoc);
		vo.setPk_psndoc(pk_psndoc);
		vo.setPk_psnjob(pk_psnjob);
		vo.setPk_group(pk_group);
		vo.setPk_org(pk_org);
		return vo;
	}

	public java.lang.String getPk_org_v() {
		return pk_org_v;
	}

	public void setPk_org_v(java.lang.String pkOrgV) {
		pk_org_v = pkOrgV;
	}

	public java.lang.String getPk_dept_v() {
		return pk_dept_v;
	}

	public void setPk_dept_v(java.lang.String pkDeptV) {
		pk_dept_v = pkDeptV;
	}

	public String getPsnName() {
		return psnName;
	}

	public void setPsnName(String psnName) {
		this.psnName = psnName;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public ViewOrderVO[] getViewOrderVOs() {
		return viewOrderVOs;
	}

	public void setViewOrderVOs(ViewOrderVO[] viewOrderVOs) {
		this.viewOrderVOs = viewOrderVOs;
	}

	@Override
	public String getMonth() {
		return getTbmmonth();
	}

	@Override
	public String getYear() {
		return getTbmyear();
	}

	public String getPk_dept() {
		return pk_dept;
	}

	public void setPk_dept(String pkDept) {
		pk_dept = pkDept;
	}

	//20171109 tsy 新增关于审批的字段 
	/**
	 * @return billno
	 */
	public String getBillno() {
		return billno;
	}

	/**
	 * @param billno 要设置的 billno
	 */
	public void setBillno(String billno) {
		this.billno = billno;
	}

	/**
	 * @return busitype
	 */
	public String getBusitype() {
		return busitype;
	}

	/**
	 * @param busitype 要设置的 busitype
	 */
	public void setBusitype(String busitype) {
		this.busitype = busitype;
	}

	/**
	 * @return billmaker
	 */
	public String getBillmaker() {
		return billmaker;
	}

	/**
	 * @param billmaker 要设置的 billmaker
	 */
	public void setBillmaker(String billmaker) {
		this.billmaker = billmaker;
	}

	/**
	 * @return approver
	 */
	public String getApprover() {
		return approver;
	}

	/**
	 * @param approver 要设置的 approver
	 */
	public void setApprover(String approver) {
		this.approver = approver;
	}

	/**
	 * @return approvestatus
	 */
	public Integer getApprovestatus() {
		return approvestatus;
	}

	/**
	 * @param approvestatus 要设置的 approvestatus
	 */
	public void setApprovestatus(Integer approvestatus) {
		this.approvestatus = approvestatus;
	}

	/**
	 * @return approvenote
	 */
	public String getApprovenote() {
		return approvenote;
	}

	/**
	 * @param approvenote 要设置的 approvenote
	 */
	public void setApprovenote(String approvenote) {
		this.approvenote = approvenote;
	}

	/**
	 * @return approvedate
	 */
	public UFDateTime getApprovedate() {
		return approvedate;
	}

	/**
	 * @param approvedate 要设置的 approvedate
	 */
	public void setApprovedate(UFDateTime approvedate) {
		this.approvedate = approvedate;
	}

	/**
	 * @return transtype
	 */
	public String getTranstype() {
		return transtype;
	}

	/**
	 * @param transtype 要设置的 transtype
	 */
	public void setTranstype(String transtype) {
		this.transtype = transtype;
	}

	/**
	 * @return billtype
	 */
	public String getBilltype() {
		return billtype;
	}

	/**
	 * @param billtype 要设置的 billtype
	 */
	public void setBilltype(String billtype) {
		this.billtype = billtype;
	}

	/**
	 * @return transtypepk
	 */
	public String getTranstypepk() {
		return transtypepk;
	}

	/**
	 * @param transtypepk 要设置的 transtypepk
	 */
	public void setTranstypepk(String transtypepk) {
		this.transtypepk = transtypepk;
	}

	/**
	 * @return srcid
	 */
	public String getSrcid() {
		return srcid;
	}

	/**
	 * @param srcid 要设置的 srcid
	 */
	public void setSrcid(String srcid) {
		this.srcid = srcid;
	}

	//20171109 end

	/**
	 * @return mngpsndoc
	 */
	public String getMngpsndoc() {
		return mngpsndoc;
	}

	/**
	 * @param mngpsndoc 要设置的 mngpsndoc
	 */
	public void setMngpsndoc(String mngpsndoc) {
		this.mngpsndoc = mngpsndoc;
	}

	/**
	 * @return mngdept
	 */
	public String getMngdept() {
		return mngdept;
	}

	/**
	 * @param mngdept 要设置的 mngdept
	 */
	public void setMngdept(String mngdept) {
		this.mngdept = mngdept;
	}
}
