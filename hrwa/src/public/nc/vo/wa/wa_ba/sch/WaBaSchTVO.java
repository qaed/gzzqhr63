package nc.vo.wa.wa_ba.sch;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

public class WaBaSchTVO extends SuperVO {
	private static final long serialVersionUID = 4993065203561925513L;
	/**
	 * class1
	 */
	public String class1;
	/**
	 * class2
	 */
	public String class2;
	/**
	 * class3
	 */
	public String class3;
	/**
	 * class4
	 */
	public String class4;
	/**
	 * class5
	 */
	public String class5;
	/**
	 * ����ϵ��
	 */
	public UFDouble comput_factor;
	/**
	 * �ƻ���Ч�����ܶ�
	 */
	public UFDouble comput_totalmoney;
	/**
	 * �ڼ�1
	 */
	public String cperiod1;
	/**
	 * �ڼ�2
	 */
	public String cperiod2;
	/**
	 * �ڼ�3
	 */
	public String cperiod3;
	/**
	 * �ڼ�4
	 */
	public String cperiod4;
	/**
	 * �ڼ�5
	 */
	public String cperiod5;
	/**
	 * ���1
	 */
	public String cyear1;
	/**
	 * ���2
	 */
	public String cyear2;
	/**
	 * ���3
	 */
	public String cyear3;
	/**
	 * ���4
	 */
	public String cyear4;
	/**
	 * ���5
	 */
	public String cyear5;
	/**
	 * ��Чϵ��
	 */
	public UFDouble f_1;
	/**
	 * ��׼��Ч����
	 */
	public UFDouble f_10;
	/**
	 * ��׼ְλн��
	 */
	public UFDouble f_2;
	/**
	 * f_3
	 */
	public UFDouble f_3;
	/**
	 * f_4
	 */
	public UFDouble f_4;
	/**
	 * f_5
	 */
	public UFDouble f_5;
	/**
	 * f_6
	 */
	public UFDouble f_6;
	/**
	 * f_7
	 */
	public UFDouble f_7;
	/**
	 * f_8
	 */
	public UFDouble f_8;
	/**
	 * f_9
	 */
	public UFDouble f_9;
	/**
	 * item1
	 */
	public String item1;
	/**
	 * item2
	 */
	public String item2;
	/**
	 * item3
	 */
	public String item3;
	/**
	 * item4
	 */
	public String item4;
	/**
	 * item5
	 */
	public String item5;
	/**
	 * ��ע1
	 */
	public String memo1;
	/**
	 * ��ע2
	 */
	public String memo2;
	/**
	 * ��ע3
	 */
	public String memo3;
	/**
	 * ��ע4
	 */
	public String memo4;
	/**
	 * ��ע5
	 */
	public String memo5;
	/**
	 * ������䵥��pk
	 */
	public String pk_ba_allocbill;
	/**
	 * ��������
	 */
	public String pk_ba_sch_h;
	/**
	 * ����
	 */
	public String pk_ba_sch_psn;
	/**
	 * �ϲ㵥������
	 */
	public String pk_ba_sch_unit;
	/**
	 * ����
	 */
	public String pk_deptdoc;
	/**
	 * pk_dutyrank
	 */
	public String pk_dutyrank;
	/**
	 * pk_jobrank
	 */
	public String pk_jobrank;
	/**
	 * pk_jobserial
	 */
	public String pk_jobserial;
	/**
	 * ְ��
	 */
	public String pk_om_duty;
	/**
	 * ��λ
	 */
	public String pk_om_job;
	/**
	 * pk_psnbasdoc
	 */
	public String pk_psnbasdoc;
	/**
	 * ��Ա
	 */
	public String pk_psndoc;
	/**
	 * ���䵥Ԫ�ӱ�PK
	 */
	public String pk_wa_ba_unit;
	/**
	 * psn_attkey
	 */
	public String psn_attkey;
	/**
	 * psn_attval
	 */
	public String psn_attval;
	/**
	 * �ƻ����1
	 */
	public UFDouble pvalue1;
	/**
	 * �ƻ����2
	 */
	public UFDouble pvalue2;
	/**
	 * �ƻ����3
	 */
	public UFDouble pvalue3;
	/**
	 * �ƻ����4
	 */
	public UFDouble pvalue4;
	/**
	 * �ƻ����5
	 */
	public UFDouble pvalue5;
	/**
	 * �޶��󿼺�ϵ��
	 */
	public UFDouble revise_factor;
	/**
	 * �޶���Ч�����ܶ�
	 */
	public UFDouble revise_totalmoney;
	/**
	 * ʱ���
	 */
	public UFDateTime ts;
	/**
	 * value1
	 */
	public UFDouble value1;
	/**
	 * value2
	 */
	public UFDouble value2;
	/**
	 * value3
	 */
	public UFDouble value3;
	/**
	 * value4
	 */
	public UFDouble value4;
	/**
	 * value5
	 */
	public UFDouble value5;
	/**
	 * ������Ŀ1
	 */
	public String waitem1;
	/**
	 * ������Ŀ2
	 */
	public String waitem2;
	/**
	 * ������Ŀ3
	 */
	public String waitem3;
	/**
	 * ������Ŀ4
	 */
	public String waitem4;
	/**
	 * ������Ŀ5
	 */
	public String waitem5;
	/**
	 * ��Ա������¼
	 */
	public String pk_psnjob;
	/**
	 * drֵ
	 */
	public Integer dr;

	/**
	 * ��ȡclass1
	 * 
	 * @return class1
	 */
	public String getClass1() {
		return this.class1;
	}

	/**
	 * ����class1
	 * 
	 * @param class1 class1
	 */
	public void setClass1(String class1) {
		this.class1 = class1;
	}

	/**
	 * ��ȡclass2
	 * 
	 * @return class2
	 */
	public String getClass2() {
		return this.class2;
	}

	/**
	 * ����class2
	 * 
	 * @param class2 class2
	 */
	public void setClass2(String class2) {
		this.class2 = class2;
	}

	/**
	 * ��ȡclass3
	 * 
	 * @return class3
	 */
	public String getClass3() {
		return this.class3;
	}

	/**
	 * ����class3
	 * 
	 * @param class3 class3
	 */
	public void setClass3(String class3) {
		this.class3 = class3;
	}

	/**
	 * ��ȡclass4
	 * 
	 * @return class4
	 */
	public String getClass4() {
		return this.class4;
	}

	/**
	 * ����class4
	 * 
	 * @param class4 class4
	 */
	public void setClass4(String class4) {
		this.class4 = class4;
	}

	/**
	 * ��ȡclass5
	 * 
	 * @return class5
	 */
	public String getClass5() {
		return this.class5;
	}

	/**
	 * ����class5
	 * 
	 * @param class5 class5
	 */
	public void setClass5(String class5) {
		this.class5 = class5;
	}

	/**
	 * ��ȡ����ϵ��
	 * 
	 * @return ����ϵ��
	 */
	public UFDouble getComput_factor() {
		return this.comput_factor;
	}

	/**
	 * ���ÿ���ϵ��
	 * 
	 * @param comput_factor ����ϵ��
	 */
	public void setComput_factor(UFDouble comput_factor) {
		this.comput_factor = comput_factor;
	}

	/**
	 * ��ȡ�ƻ���Ч�����ܶ�
	 * 
	 * @return �ƻ���Ч�����ܶ�
	 */
	public UFDouble getComput_totalmoney() {
		return this.comput_totalmoney;
	}

	/**
	 * ���üƻ���Ч�����ܶ�
	 * 
	 * @param comput_totalmoney �ƻ���Ч�����ܶ�
	 */
	public void setComput_totalmoney(UFDouble comput_totalmoney) {
		this.comput_totalmoney = comput_totalmoney;
	}

	/**
	 * ��ȡ�ڼ�1
	 * 
	 * @return �ڼ�1
	 */
	public String getCperiod1() {
		return this.cperiod1;
	}

	/**
	 * �����ڼ�1
	 * 
	 * @param cperiod1 �ڼ�1
	 */
	public void setCperiod1(String cperiod1) {
		this.cperiod1 = cperiod1;
	}

	/**
	 * ��ȡ�ڼ�2
	 * 
	 * @return �ڼ�2
	 */
	public String getCperiod2() {
		return this.cperiod2;
	}

	/**
	 * �����ڼ�2
	 * 
	 * @param cperiod2 �ڼ�2
	 */
	public void setCperiod2(String cperiod2) {
		this.cperiod2 = cperiod2;
	}

	/**
	 * ��ȡ�ڼ�3
	 * 
	 * @return �ڼ�3
	 */
	public String getCperiod3() {
		return this.cperiod3;
	}

	/**
	 * �����ڼ�3
	 * 
	 * @param cperiod3 �ڼ�3
	 */
	public void setCperiod3(String cperiod3) {
		this.cperiod3 = cperiod3;
	}

	/**
	 * ��ȡ�ڼ�4
	 * 
	 * @return �ڼ�4
	 */
	public String getCperiod4() {
		return this.cperiod4;
	}

	/**
	 * �����ڼ�4
	 * 
	 * @param cperiod4 �ڼ�4
	 */
	public void setCperiod4(String cperiod4) {
		this.cperiod4 = cperiod4;
	}

	/**
	 * ��ȡ�ڼ�5
	 * 
	 * @return �ڼ�5
	 */
	public String getCperiod5() {
		return this.cperiod5;
	}

	/**
	 * �����ڼ�5
	 * 
	 * @param cperiod5 �ڼ�5
	 */
	public void setCperiod5(String cperiod5) {
		this.cperiod5 = cperiod5;
	}

	/**
	 * ��ȡ���1
	 * 
	 * @return ���1
	 */
	public String getCyear1() {
		return this.cyear1;
	}

	/**
	 * �������1
	 * 
	 * @param cyear1 ���1
	 */
	public void setCyear1(String cyear1) {
		this.cyear1 = cyear1;
	}

	/**
	 * ��ȡ���2
	 * 
	 * @return ���2
	 */
	public String getCyear2() {
		return this.cyear2;
	}

	/**
	 * �������2
	 * 
	 * @param cyear2 ���2
	 */
	public void setCyear2(String cyear2) {
		this.cyear2 = cyear2;
	}

	/**
	 * ��ȡ���3
	 * 
	 * @return ���3
	 */
	public String getCyear3() {
		return this.cyear3;
	}

	/**
	 * �������3
	 * 
	 * @param cyear3 ���3
	 */
	public void setCyear3(String cyear3) {
		this.cyear3 = cyear3;
	}

	/**
	 * ��ȡ���4
	 * 
	 * @return ���4
	 */
	public String getCyear4() {
		return this.cyear4;
	}

	/**
	 * �������4
	 * 
	 * @param cyear4 ���4
	 */
	public void setCyear4(String cyear4) {
		this.cyear4 = cyear4;
	}

	/**
	 * ��ȡ���5
	 * 
	 * @return ���5
	 */
	public String getCyear5() {
		return this.cyear5;
	}

	/**
	 * �������5
	 * 
	 * @param cyear5 ���5
	 */
	public void setCyear5(String cyear5) {
		this.cyear5 = cyear5;
	}

	/**
	 * ��ȡ��Чϵ��
	 * 
	 * @return ��Чϵ��
	 */
	public UFDouble getF_1() {
		return this.f_1;
	}

	/**
	 * ���ü�Чϵ��
	 * 
	 * @param f_1 ��Чϵ��
	 */
	public void setF_1(UFDouble f_1) {
		this.f_1 = f_1;
	}

	/**
	 * ��ȡ��׼��Ч����
	 * 
	 * @return ��׼��Ч����
	 */
	public UFDouble getF_10() {
		return this.f_10;
	}

	/**
	 * ���ñ�׼��Ч����
	 * 
	 * @param f_10 ��׼��Ч����
	 */
	public void setF_10(UFDouble f_10) {
		this.f_10 = f_10;
	}

	/**
	 * ��ȡ��׼ְλн��
	 * 
	 * @return ��׼ְλн��
	 */
	public UFDouble getF_2() {
		return this.f_2;
	}

	/**
	 * ���ñ�׼ְλн��
	 * 
	 * @param f_2 ��׼ְλн��
	 */
	public void setF_2(UFDouble f_2) {
		this.f_2 = f_2;
	}

	/**
	 * ��ȡf_3
	 * 
	 * @return f_3
	 */
	public UFDouble getF_3() {
		return this.f_3;
	}

	/**
	 * ����f_3
	 * 
	 * @param f_3 f_3
	 */
	public void setF_3(UFDouble f_3) {
		this.f_3 = f_3;
	}

	/**
	 * ��ȡf_4
	 * 
	 * @return f_4
	 */
	public UFDouble getF_4() {
		return this.f_4;
	}

	/**
	 * ����f_4
	 * 
	 * @param f_4 f_4
	 */
	public void setF_4(UFDouble f_4) {
		this.f_4 = f_4;
	}

	/**
	 * ��ȡf_5
	 * 
	 * @return f_5
	 */
	public UFDouble getF_5() {
		return this.f_5;
	}

	/**
	 * ����f_5
	 * 
	 * @param f_5 f_5
	 */
	public void setF_5(UFDouble f_5) {
		this.f_5 = f_5;
	}

	/**
	 * ��ȡf_6
	 * 
	 * @return f_6
	 */
	public UFDouble getF_6() {
		return this.f_6;
	}

	/**
	 * ����f_6
	 * 
	 * @param f_6 f_6
	 */
	public void setF_6(UFDouble f_6) {
		this.f_6 = f_6;
	}

	/**
	 * ��ȡf_7
	 * 
	 * @return f_7
	 */
	public UFDouble getF_7() {
		return this.f_7;
	}

	/**
	 * ����f_7
	 * 
	 * @param f_7 f_7
	 */
	public void setF_7(UFDouble f_7) {
		this.f_7 = f_7;
	}

	/**
	 * ��ȡf_8
	 * 
	 * @return f_8
	 */
	public UFDouble getF_8() {
		return this.f_8;
	}

	/**
	 * ����f_8
	 * 
	 * @param f_8 f_8
	 */
	public void setF_8(UFDouble f_8) {
		this.f_8 = f_8;
	}

	/**
	 * ��ȡf_9
	 * 
	 * @return f_9
	 */
	public UFDouble getF_9() {
		return this.f_9;
	}

	/**
	 * ����f_9
	 * 
	 * @param f_9 f_9
	 */
	public void setF_9(UFDouble f_9) {
		this.f_9 = f_9;
	}

	/**
	 * ��ȡitem1
	 * 
	 * @return item1
	 */
	public String getItem1() {
		return this.item1;
	}

	/**
	 * ����item1
	 * 
	 * @param item1 item1
	 */
	public void setItem1(String item1) {
		this.item1 = item1;
	}

	/**
	 * ��ȡitem2
	 * 
	 * @return item2
	 */
	public String getItem2() {
		return this.item2;
	}

	/**
	 * ����item2
	 * 
	 * @param item2 item2
	 */
	public void setItem2(String item2) {
		this.item2 = item2;
	}

	/**
	 * ��ȡitem3
	 * 
	 * @return item3
	 */
	public String getItem3() {
		return this.item3;
	}

	/**
	 * ����item3
	 * 
	 * @param item3 item3
	 */
	public void setItem3(String item3) {
		this.item3 = item3;
	}

	/**
	 * ��ȡitem4
	 * 
	 * @return item4
	 */
	public String getItem4() {
		return this.item4;
	}

	/**
	 * ����item4
	 * 
	 * @param item4 item4
	 */
	public void setItem4(String item4) {
		this.item4 = item4;
	}

	/**
	 * ��ȡitem5
	 * 
	 * @return item5
	 */
	public String getItem5() {
		return this.item5;
	}

	/**
	 * ����item5
	 * 
	 * @param item5 item5
	 */
	public void setItem5(String item5) {
		this.item5 = item5;
	}

	/**
	 * ��ȡ��ע1
	 * 
	 * @return ��ע1
	 */
	public String getMemo1() {
		return this.memo1;
	}

	/**
	 * ���ñ�ע1
	 * 
	 * @param memo1 ��ע1
	 */
	public void setMemo1(String memo1) {
		this.memo1 = memo1;
	}

	/**
	 * ��ȡ��ע2
	 * 
	 * @return ��ע2
	 */
	public String getMemo2() {
		return this.memo2;
	}

	/**
	 * ���ñ�ע2
	 * 
	 * @param memo2 ��ע2
	 */
	public void setMemo2(String memo2) {
		this.memo2 = memo2;
	}

	/**
	 * ��ȡ��ע3
	 * 
	 * @return ��ע3
	 */
	public String getMemo3() {
		return this.memo3;
	}

	/**
	 * ���ñ�ע3
	 * 
	 * @param memo3 ��ע3
	 */
	public void setMemo3(String memo3) {
		this.memo3 = memo3;
	}

	/**
	 * ��ȡ��ע4
	 * 
	 * @return ��ע4
	 */
	public String getMemo4() {
		return this.memo4;
	}

	/**
	 * ���ñ�ע4
	 * 
	 * @param memo4 ��ע4
	 */
	public void setMemo4(String memo4) {
		this.memo4 = memo4;
	}

	/**
	 * ��ȡ��ע5
	 * 
	 * @return ��ע5
	 */
	public String getMemo5() {
		return this.memo5;
	}

	/**
	 * ���ñ�ע5
	 * 
	 * @param memo5 ��ע5
	 */
	public void setMemo5(String memo5) {
		this.memo5 = memo5;
	}

	/**
	 * ��ȡ������䵥��pk
	 * 
	 * @return ������䵥��pk
	 */
	public String getPk_ba_allocbill() {
		return this.pk_ba_allocbill;
	}

	/**
	 * ���ý�����䵥��pk
	 * 
	 * @param pk_ba_allocbill ������䵥��pk
	 */
	public void setPk_ba_allocbill(String pk_ba_allocbill) {
		this.pk_ba_allocbill = pk_ba_allocbill;
	}

	/**
	 * ��ȡ��������
	 * 
	 * @return ��������
	 */
	public String getPk_ba_sch_h() {
		return this.pk_ba_sch_h;
	}

	/**
	 * ������������
	 * 
	 * @param pk_ba_sch_h ��������
	 */
	public void setPk_ba_sch_h(String pk_ba_sch_h) {
		this.pk_ba_sch_h = pk_ba_sch_h;
	}

	/**
	 * ��ȡ����
	 * 
	 * @return ����
	 */
	public String getPk_ba_sch_psn() {
		return this.pk_ba_sch_psn;
	}

	/**
	 * ��������
	 * 
	 * @param pk_ba_sch_psn ����
	 */
	public void setPk_ba_sch_psn(String pk_ba_sch_psn) {
		this.pk_ba_sch_psn = pk_ba_sch_psn;
	}

	/**
	 * ��ȡ�ϲ㵥������
	 * 
	 * @return �ϲ㵥������
	 */
	public String getPk_ba_sch_unit() {
		return this.pk_ba_sch_unit;
	}

	/**
	 * �����ϲ㵥������
	 * 
	 * @param pk_ba_sch_unit �ϲ㵥������
	 */
	public void setPk_ba_sch_unit(String pk_ba_sch_unit) {
		this.pk_ba_sch_unit = pk_ba_sch_unit;
	}

	/**
	 * ��ȡ����
	 * 
	 * @return ����
	 */
	public String getPk_deptdoc() {
		return this.pk_deptdoc;
	}

	/**
	 * ���ò���
	 * 
	 * @param pk_deptdoc ����
	 */
	public void setPk_deptdoc(String pk_deptdoc) {
		this.pk_deptdoc = pk_deptdoc;
	}

	/**
	 * ��ȡpk_dutyrank
	 * 
	 * @return pk_dutyrank
	 */
	public String getPk_dutyrank() {
		return this.pk_dutyrank;
	}

	/**
	 * ����pk_dutyrank
	 * 
	 * @param pk_dutyrank pk_dutyrank
	 */
	public void setPk_dutyrank(String pk_dutyrank) {
		this.pk_dutyrank = pk_dutyrank;
	}

	/**
	 * ��ȡpk_jobrank
	 * 
	 * @return pk_jobrank
	 */
	public String getPk_jobrank() {
		return this.pk_jobrank;
	}

	/**
	 * ����pk_jobrank
	 * 
	 * @param pk_jobrank pk_jobrank
	 */
	public void setPk_jobrank(String pk_jobrank) {
		this.pk_jobrank = pk_jobrank;
	}

	/**
	 * ��ȡpk_jobserial
	 * 
	 * @return pk_jobserial
	 */
	public String getPk_jobserial() {
		return this.pk_jobserial;
	}

	/**
	 * ����pk_jobserial
	 * 
	 * @param pk_jobserial pk_jobserial
	 */
	public void setPk_jobserial(String pk_jobserial) {
		this.pk_jobserial = pk_jobserial;
	}

	/**
	 * ��ȡְ��
	 * 
	 * @return ְ��
	 */
	public String getPk_om_duty() {
		return this.pk_om_duty;
	}

	/**
	 * ����ְ��
	 * 
	 * @param pk_om_duty ְ��
	 */
	public void setPk_om_duty(String pk_om_duty) {
		this.pk_om_duty = pk_om_duty;
	}

	/**
	 * ��ȡ��λ
	 * 
	 * @return ��λ
	 */
	public String getPk_om_job() {
		return this.pk_om_job;
	}

	/**
	 * ���ø�λ
	 * 
	 * @param pk_om_job ��λ
	 */
	public void setPk_om_job(String pk_om_job) {
		this.pk_om_job = pk_om_job;
	}

	/**
	 * ��ȡpk_psnbasdoc
	 * 
	 * @return pk_psnbasdoc
	 */
	public String getPk_psnbasdoc() {
		return this.pk_psnbasdoc;
	}

	/**
	 * ����pk_psnbasdoc
	 * 
	 * @param pk_psnbasdoc pk_psnbasdoc
	 */
	public void setPk_psnbasdoc(String pk_psnbasdoc) {
		this.pk_psnbasdoc = pk_psnbasdoc;
	}

	/**
	 * ��ȡ��Ա
	 * 
	 * @return ��Ա
	 */
	public String getPk_psndoc() {
		return this.pk_psndoc;
	}

	/**
	 * ������Ա
	 * 
	 * @param pk_psndoc ��Ա
	 */
	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	/**
	 * ��ȡ���䵥Ԫ�ӱ�PK
	 * 
	 * @return ���䵥Ԫ�ӱ�PK
	 */
	public String getPk_wa_ba_unit() {
		return this.pk_wa_ba_unit;
	}

	/**
	 * ���÷��䵥Ԫ�ӱ�PK
	 * 
	 * @param pk_wa_ba_unit ���䵥Ԫ�ӱ�PK
	 */
	public void setPk_wa_ba_unit(String pk_wa_ba_unit) {
		this.pk_wa_ba_unit = pk_wa_ba_unit;
	}

	/**
	 * ��ȡpsn_attkey
	 * 
	 * @return psn_attkey
	 */
	public String getPsn_attkey() {
		return this.psn_attkey;
	}

	/**
	 * ����psn_attkey
	 * 
	 * @param psn_attkey psn_attkey
	 */
	public void setPsn_attkey(String psn_attkey) {
		this.psn_attkey = psn_attkey;
	}

	/**
	 * ��ȡpsn_attval
	 * 
	 * @return psn_attval
	 */
	public String getPsn_attval() {
		return this.psn_attval;
	}

	/**
	 * ����psn_attval
	 * 
	 * @param psn_attval psn_attval
	 */
	public void setPsn_attval(String psn_attval) {
		this.psn_attval = psn_attval;
	}

	/**
	 * ��ȡ�ƻ����1
	 * 
	 * @return �ƻ����1
	 */
	public UFDouble getPvalue1() {
		return this.pvalue1;
	}

	/**
	 * ���üƻ����1
	 * 
	 * @param pvalue1 �ƻ����1
	 */
	public void setPvalue1(UFDouble pvalue1) {
		this.pvalue1 = pvalue1;
	}

	/**
	 * ��ȡ�ƻ����2
	 * 
	 * @return �ƻ����2
	 */
	public UFDouble getPvalue2() {
		return this.pvalue2;
	}

	/**
	 * ���üƻ����2
	 * 
	 * @param pvalue2 �ƻ����2
	 */
	public void setPvalue2(UFDouble pvalue2) {
		this.pvalue2 = pvalue2;
	}

	/**
	 * ��ȡ�ƻ����3
	 * 
	 * @return �ƻ����3
	 */
	public UFDouble getPvalue3() {
		return this.pvalue3;
	}

	/**
	 * ���üƻ����3
	 * 
	 * @param pvalue3 �ƻ����3
	 */
	public void setPvalue3(UFDouble pvalue3) {
		this.pvalue3 = pvalue3;
	}

	/**
	 * ��ȡ�ƻ����4
	 * 
	 * @return �ƻ����4
	 */
	public UFDouble getPvalue4() {
		return this.pvalue4;
	}

	/**
	 * ���üƻ����4
	 * 
	 * @param pvalue4 �ƻ����4
	 */
	public void setPvalue4(UFDouble pvalue4) {
		this.pvalue4 = pvalue4;
	}

	/**
	 * ��ȡ�ƻ����5
	 * 
	 * @return �ƻ����5
	 */
	public UFDouble getPvalue5() {
		return this.pvalue5;
	}

	/**
	 * ���üƻ����5
	 * 
	 * @param pvalue5 �ƻ����5
	 */
	public void setPvalue5(UFDouble pvalue5) {
		this.pvalue5 = pvalue5;
	}

	/**
	 * ��ȡ�޶��󿼺�ϵ��
	 * 
	 * @return �޶��󿼺�ϵ��
	 */
	public UFDouble getRevise_factor() {
		return this.revise_factor;
	}

	/**
	 * �����޶��󿼺�ϵ��
	 * 
	 * @param revise_factor �޶��󿼺�ϵ��
	 */
	public void setRevise_factor(UFDouble revise_factor) {
		this.revise_factor = revise_factor;
	}

	/**
	 * ��ȡ�޶���Ч�����ܶ�
	 * 
	 * @return �޶���Ч�����ܶ�
	 */
	public UFDouble getRevise_totalmoney() {
		return this.revise_totalmoney;
	}

	/**
	 * �����޶���Ч�����ܶ�
	 * 
	 * @param revise_totalmoney �޶���Ч�����ܶ�
	 */
	public void setRevise_totalmoney(UFDouble revise_totalmoney) {
		this.revise_totalmoney = revise_totalmoney;
	}

	/**
	 * ��ȡʱ���
	 * 
	 * @return ʱ���
	 */
	public UFDateTime getTs() {
		return this.ts;
	}

	/**
	 * ����ʱ���
	 * 
	 * @param ts ʱ���
	 */
	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	/**
	 * ��ȡvalue1
	 * 
	 * @return value1
	 */
	public UFDouble getValue1() {
		return this.value1;
	}

	/**
	 * ����value1
	 * 
	 * @param value1 value1
	 */
	public void setValue1(UFDouble value1) {
		this.value1 = value1;
	}

	/**
	 * ��ȡvalue2
	 * 
	 * @return value2
	 */
	public UFDouble getValue2() {
		return this.value2;
	}

	/**
	 * ����value2
	 * 
	 * @param value2 value2
	 */
	public void setValue2(UFDouble value2) {
		this.value2 = value2;
	}

	/**
	 * ��ȡvalue3
	 * 
	 * @return value3
	 */
	public UFDouble getValue3() {
		return this.value3;
	}

	/**
	 * ����value3
	 * 
	 * @param value3 value3
	 */
	public void setValue3(UFDouble value3) {
		this.value3 = value3;
	}

	/**
	 * ��ȡvalue4
	 * 
	 * @return value4
	 */
	public UFDouble getValue4() {
		return this.value4;
	}

	/**
	 * ����value4
	 * 
	 * @param value4 value4
	 */
	public void setValue4(UFDouble value4) {
		this.value4 = value4;
	}

	/**
	 * ��ȡvalue5
	 * 
	 * @return value5
	 */
	public UFDouble getValue5() {
		return this.value5;
	}

	/**
	 * ����value5
	 * 
	 * @param value5 value5
	 */
	public void setValue5(UFDouble value5) {
		this.value5 = value5;
	}

	/**
	 * ��ȡ������Ŀ1
	 * 
	 * @return ������Ŀ1
	 */
	public String getWaitem1() {
		return this.waitem1;
	}

	/**
	 * ���ý�����Ŀ1
	 * 
	 * @param waitem1 ������Ŀ1
	 */
	public void setWaitem1(String waitem1) {
		this.waitem1 = waitem1;
	}

	/**
	 * ��ȡ������Ŀ2
	 * 
	 * @return ������Ŀ2
	 */
	public String getWaitem2() {
		return this.waitem2;
	}

	/**
	 * ���ý�����Ŀ2
	 * 
	 * @param waitem2 ������Ŀ2
	 */
	public void setWaitem2(String waitem2) {
		this.waitem2 = waitem2;
	}

	/**
	 * ��ȡ������Ŀ3
	 * 
	 * @return ������Ŀ3
	 */
	public String getWaitem3() {
		return this.waitem3;
	}

	/**
	 * ���ý�����Ŀ3
	 * 
	 * @param waitem3 ������Ŀ3
	 */
	public void setWaitem3(String waitem3) {
		this.waitem3 = waitem3;
	}

	/**
	 * ��ȡ������Ŀ4
	 * 
	 * @return ������Ŀ4
	 */
	public String getWaitem4() {
		return this.waitem4;
	}

	/**
	 * ���ý�����Ŀ4
	 * 
	 * @param waitem4 ������Ŀ4
	 */
	public void setWaitem4(String waitem4) {
		this.waitem4 = waitem4;
	}

	/**
	 * ��ȡ������Ŀ5
	 * 
	 * @return ������Ŀ5
	 */
	public String getWaitem5() {
		return this.waitem5;
	}

	/**
	 * ���ý�����Ŀ5
	 * 
	 * @param waitem5 ������Ŀ5
	 */
	public void setWaitem5(String waitem5) {
		this.waitem5 = waitem5;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	/**
	 * @return pk_psnjob
	 */
	public String getPk_psnjob() {
		return pk_psnjob;
	}

	/**
	 * @param pk_psnjob Ҫ���õ� pk_psnjob
	 */
	public void setPk_psnjob(String pk_psnjob) {
		this.pk_psnjob = pk_psnjob;
	}

	@Override
	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("hrwa.WaBaSchTVO");
	}

	@Override
	public String getParentPKFieldName() {
		return "pk_ba_sch_unit";
	}

}