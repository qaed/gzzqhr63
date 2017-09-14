package nc.ui.hi.ref;

import java.util.Hashtable;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.itf.om.IAOSQueryService;
import nc.itf.org.IOrgConst;
import nc.pub.tools.HiSQLHelper;
import nc.pub.tools.KeyPsnGroupSqlUtils;
import nc.ui.bd.ref.AbstractRefGridTreeModel;
import nc.ui.bd.ref.IRefConst;
import nc.vo.bd.pub.IPubEnumConst;
import nc.vo.hi.psndoc.KeyPsnGrpVO;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.om.aos.AOSSQLHelper;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;

public class PsndocRefTreeModel extends AbstractRefGridTreeModel {
	String wherePart = " and hi_psnjob.pk_org in (select pk_adminorg from org_admin_enable) ";
	/**
	 * �������ⲿ��
	 */
	private boolean includeDummyDept = false;
	/**
	 * �����ؼ���Ա��
	 */
	private boolean includeKeyPsnGrp = true;

	public PsndocRefTreeModel() {
		reset();
	}

	protected String getEnvWherePart() {
		String envWherePart = null;
		String pk_org = getPk_org();
		String pk_group = getPk_group();
		// ����Ա������ܿ�ģʽ����ʾ����
		if ((IOrgConst.GLOBEORG.equals(pk_org)) || (StringUtil.isEmpty(pk_group))) {
			// pk_orgΪȫ�ֻ�ǰ����Ϊ��ʱ����ʾȫ����Ա����
			envWherePart = null;
		} else if (pk_org.equals(pk_group)) {
			// pk_orgΪ����ʱ����δ���þ��幫˾ʱ������ʾ���ŵ���Ա�������ݣ����ڼ�����������ְ����Ա�����ݣ�
			envWherePart =
					" bd_psndoc.pk_psndoc in (select hi_psnjob.pk_psndoc from hi_psnjob  inner join hi_psnorg on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg where hi_psnorg.indocflag = 'Y' and hi_psnjob.pk_group = '" + pk_group + "') ";

		} else {
			// ������ʾ��˾�����ݣ��ù�˾����ְ����Ա�����ݣ�
			envWherePart =
					" bd_psndoc.pk_psndoc in (select hi_psnjob.pk_psndoc from hi_psnjob  inner join hi_psnorg on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg where hi_psnorg.indocflag = 'Y' and hi_psnjob.pk_org in (" + AOSSQLHelper.getChildrenBUInSQLByHROrgPK(getPkhrorg(getPk_org())) + ") )";
		}

		return envWherePart;
	}

	private String getPkhrorg(String pkOrg) {
		try {
			OrgVO hrorgVO = ((IAOSQueryService) NCLocator.getInstance().lookup(IAOSQueryService.class)).queryHROrgByOrgPK(getPk_org());
			return hrorgVO == null ? pkOrg : hrorgVO.getPk_org();
		} catch (BusinessException e) {
			Logger.error(e.getMessage());
		}
		return pkOrg;
	}

	public void reset() {
		setRefTitle(ResHelper.getString("common", "UC000-0000129"));/* @res "��Ա" */
		setRootName(ResHelper.getString("6007psn", "06007psn0302"));/* @res "��֯����" */
		setClassFieldCode(new String[] { "code", "name", "pk_orgdept", "pk_children", "pk_father", "display_order", "pk_group" });
		setClassFatherField("pk_father");
		setClassChildField("pk_children");
		setClassJoinField("pk_orgdept");
		StringBuffer classTableName = new StringBuffer();
		classTableName.append(" ( select code, name,name2,name3,name4,name5,name6, org_adminorg.pk_adminorg pk_org, org_adminorg.pk_adminorg pk_orgdept,org_adminorg.pk_group pk_group, ").append(" org_adminorg.pk_adminorg pk_children, org_adminorg.pk_fatherorg pk_father, 2 display_order ").append(" from org_adminorg where enablestate=" + IPubEnumConst.ENABLESTATE_ENABLE).append(" and exists (select 1 from org_admin_enable where org_admin_enable.pk_adminorg = org_adminorg.pk_adminorg) ").append(" union ").append(" select org_dept.code,org_dept.name,org_dept.name2,org_dept.name3,org_dept.name4,org_dept.name5,org_dept.name6, org_dept.pk_org pk_org,org_dept.pk_dept pk_orgdept, org_adminorg.pk_group, ").append(" org_dept.pk_dept pk_children, case when " + SQLHelper.getNullSql("org_dept.pk_fatherorg") + " then org_adminorg.pk_adminorg ").append(" else org_dept.pk_fatherorg end pk_father,1 display_order ").append(" from org_dept , org_adminorg  where org_dept.pk_org = org_adminorg.pk_adminorg  ").append(" and exists (select 1 from org_admin_enable where org_admin_enable.pk_adminorg = org_dept.pk_org)");

		if (!isIncludeDummyDept()) {// ������������ⲿ��
			classTableName.append(" and org_dept.depttype = 0 ");
		}
		if (isIncludeKeyPsnGrp()) {//������֯��Ϊ�� ���Ҳ���ȫ�ֻ��� ,��Ա��ֻ��ʾ��ǰ��֯��
			String keypsn = ResHelper.getString("6007psn", "06007psn0357");/* @res "�ؼ���Ա��" */
			classTableName.append(" union ").append(" select '','" + keypsn + "','" + keypsn + "','" + keypsn + "','" + keypsn + "','" + keypsn + "','" + keypsn + "' ,pk_group, '" + HICommonValue.PK_KRYPSNGRP + "', ").append(" pk_group, '" + HICommonValue.PK_KRYPSNGRP + "', '~', 3 from org_group where pk_group = '" + getPk_group() + "'  ").append(" union ").append(" select group_code code ,group_name name,group_name2 name2,group_name3 name3,group_name4 name4,group_name5 name5,group_name6 name6,pk_org ,'" + HICommonValue.PRE_KRYPSNGRP + "'||pk_keypsn_group pk_orgdept,pk_group,pk_keypsn_group pk_children,'" + HICommonValue.PK_KRYPSNGRP + "' pk_father, 4 display_order  from hi_keypsn_group where enablestate = " + IPubEnumConst.ENABLESTATE_ENABLE + " and " + KeyPsnGroupSqlUtils.getKeyPsnGroupPowerSql(KeyPsnGrpVO.getDefaultTableName()));
		}

		if ((getPk_org() != null) && (!getPk_org().equals(PubEnv.getPk_group())) && (!getPk_org().equals(IOrgConst.GLOBEORG))) {
			//������֯��Ϊ�� ���Ҳ���ȫ�ֻ��� ,��Ա��ֻ��ʾ��ǰ��֯��
			classTableName.append(" and pk_org = '" + getPk_org() + "' ");
		}
		classTableName.append(" ) orgdept ");
		setClassTableName(classTableName.toString());
		setClassDefaultFieldCount(getClassDefaultFieldCount());
		setClassWherePart(" pk_group ='" + getPk_group() + "' ");
		setClassOrderPart("display_order,code");
		// 20170907 tsy �����Ա���
		setFieldCode(new String[] { "bd_psndoc.code", HiSQLHelper.getLangNameColume("bd_psndoc.name"), HiSQLHelper.getLangNameColume("org_orgs.name"), HiSQLHelper.getLangNameColume("org_dept.name"), HiSQLHelper.getLangNameColume("om_post.postname"), HiSQLHelper.getLangNameColume("bd_psncl.name") });

		setFieldName(new String[] { ResHelper.getString("common", "UC000-0000147")/* @res "��Ա����" */, ResHelper.getString("common", "UC000-0001403")/* @res "����" */, ResHelper.getString("6007psn", "06007psn0074")/* @res "��֯" */, ResHelper.getString("common", "UC000-0004064")/* @res "����" */, ResHelper.getString("common", "UC000-0001653")/* @res "��λ" */, "��Ա���" });
		// 20170907 end
		setHiddenFieldCode(new String[] { "hi_psnjob.pk_dept", "hi_psnjob.pk_psnjob", "bd_psndoc.pk_psndoc", "hi_psnjob.pk_org", "hi_psnjob.pk_post", "hi_psnjob.pk_job", "hi_psnjob.pk_psncl", "idtype", "id" });

		setTableName(" bd_psndoc inner join hi_psnorg on hi_psnorg.pk_psndoc = bd_psndoc.pk_psndoc  " + " inner join (select max( orgrelaid) as orgrelaid,pk_psndoc from hi_psnorg " + "where indocflag='Y' group by pk_psndoc  ) tmp  on hi_psnorg.pk_psndoc = tmp.pk_psndoc " + "and hi_psnorg.orgrelaid = tmp.orgrelaid " + "inner join hi_psnjob on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg " + "and hi_psnjob.lastflag = 'Y' and hi_psnjob.ismainjob = 'Y'  " + "left outer join org_orgs on org_orgs.pk_org = hi_psnjob.pk_org  " + "left outer join org_dept on org_dept.pk_dept = hi_psnjob.pk_dept  " + "left outer join om_post on om_post.pk_post = hi_psnjob.pk_post " + "left outer join bd_psncl on bd_psncl.pk_psncl = hi_psnjob.pk_psncl");

		setPkFieldCode("bd_psndoc.pk_psndoc");
		setDocJoinField("hi_psnjob.pk_org");
		setExactOn(false);

		setWherePart(" hi_psnorg.indocflag = 'Y' ");
		resetFieldName();
		Hashtable content = new Hashtable();
		content.put("0", ResHelper.getString("6001ref", "06001ref0004"));/* @res "���֤" */
		content.put("1", ResHelper.getString("6001ref", "06001ref0005"));/* @res "����֤" */
		content.put("2", ResHelper.getString("6001ref", "06001ref0006"));/* @res "����" */
		content.put("3", ResHelper.getString("10140psn", "2psndoc-000026"));/* @res "������֤" */
		content.put("4", ResHelper.getString("10140psn", "2psndoc-000027"));/* @res "����֤" */
		content.put("5", ResHelper.getString("10140psn", "2psndoc-000028"));/* @res "̨�����֤ " */
		content.put("6", ResHelper.getString("10140psn", "2psndoc-000029"));/* @res "�������֤" */
		content.put("7", ResHelper.getString("10140psn", "2psndoc-000030"));/* @res "̨��֤ " */
		content.put("8", ResHelper.getString("10140psn", "2psndoc-000031"));/* @res "��������þ���֤" */
		Hashtable convert = new Hashtable();
		convert.put("idtype", content);
		setDispConvertor(convert);
		setMutilLangNameRef(false);
	}

	protected void addJoinCondition(StringBuffer sqlBuffer) {
		// �������---���ǲ�����WherePart
		if ((getClassJoinValue() != null) && (!getClassJoinValue().equals(IRefConst.QUERY))) {
			if (HICommonValue.PK_KRYPSNGRP.equals(getClassJoinValue())) {
				// ѡ��ؼ���Ա��ڵ�
				sqlBuffer.append(" and ( hi_psnjob.pk_psnjob in ('') ) ");
			} else if (getClassJoinValue().startsWith(HICommonValue.PRE_KRYPSNGRP)) {

				//ѡ���˾������
				String pk_keypsngrp = getClassJoinValue().substring(5);
				sqlBuffer.append(" and ( hi_psnjob.pk_psnorg in ( select pk_psnorg from hi_psndoc_keypsn where pk_keypsn_grp = '" + pk_keypsngrp + "' and ( endflag <> 'Y' ) ) )");

			} else if (isExactOn()) {
				sqlBuffer.append(" and ( " + getDocJoinField() + " = '" + getClassJoinValue() + "' )");
			} else {
				sqlBuffer.append(" and ( " + getDocJoinField() + " = '" + getClassJoinValue() + "' or hi_psnjob.pk_dept = '" + getClassJoinValue() + "' )");
			}
		}

		sqlBuffer.append(this.wherePart + " and hi_psnjob.pk_psnjob not in ( " + KeyPsnGroupSqlUtils.getKeyPsnPowerSql() + " )");
	}

	public void setIncludeDummyDept(boolean includeDummyDept) {
		this.includeDummyDept = includeDummyDept;
	}

	public boolean isIncludeDummyDept() {
		return this.includeDummyDept;
	}

	public void setIncludeKeyPsnGrp(boolean includeKeyPsnGrp) {
		this.includeKeyPsnGrp = includeKeyPsnGrp;
		reset();
	}

	public boolean isIncludeKeyPsnGrp() {
		return this.includeKeyPsnGrp;
	}
}