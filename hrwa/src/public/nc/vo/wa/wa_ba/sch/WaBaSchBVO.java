package nc.vo.wa.wa_ba.sch;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

public class WaBaSchBVO extends SuperVO {
	/**
	 * ������为����
	 */
	public String ba_mng_psnpk;
	/**
	 * ����������
	 */
	public String ba_mng_psnpk_showname;
	/**
	 * ����Ԫ����
	 */
	public String ba_unit_code;
	/**
	 * ����Ԫ����
	 */
	public String ba_unit_name;
	/**
	 * ����Ԫ����
	 */
	public String ba_unit_type;
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
	 * ѡ��
	 */
	public UFBoolean isapprove;
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
	 * �ϲ㵥������
	 */
	public String pk_ba_sch_h;
	/**
	 * ����
	 */
	public String pk_ba_sch_unit;
	/**
	 * ��˾
	 */
	public String pk_corp;
	/**
	 * ����Ԫ����
	 */
	public String pk_wa_ba_unit;
	/**
	 * �ۼƿɷ����ܶ�
	 */
	public String plan_totalmoney;
	/**
	 * ��Ŀ�ƻ������1
	 */
	public String ptmny1;
	/**
	 * ��Ŀ�ƻ������2
	 */
	public String ptmny2;
	/**
	 * ��Ŀ�ƻ������3
	 */
	public String ptmny3;
	/**
	 * ��Ŀ�ƻ������4
	 */
	public String ptmny4;
	/**
	 * ��Ŀ�ƻ������5
	 */
	public String ptmny5;
	/**
	 * ʱ���
	 */
	public UFDateTime ts;
	/**
	 * �Զ�����1
	 */
	public String vdef1;
	/**
	 * �Զ�����10
	 */
	public String vdef10;
	/**
	 * �Զ�����11
	 */
	public String vdef11;
	/**
	 * �Զ�����12
	 */
	public String vdef12;
	/**
	 * �Զ�����13
	 */
	public String vdef13;
	/**
	 * �Զ�����14
	 */
	public String vdef14;
	/**
	 * �Զ�����15
	 */
	public String vdef15;
	/**
	 * �Զ�����16
	 */
	public String vdef16;
	/**
	 * �Զ�����17
	 */
	public String vdef17;
	/**
	 * �Զ�����18
	 */
	public String vdef18;
	/**
	 * �Զ�����19
	 */
	public String vdef19;
	/**
	 * �Զ�����2
	 */
	public String vdef2;
	/**
	 * �Զ�����20
	 */
	public String vdef20;
	/**
	 * �Զ�����3
	 */
	public String vdef3;
	/**
	 * �Զ�����4
	 */
	public String vdef4;
	/**
	 * �Զ�����5
	 */
	public String vdef5;
	/**
	 * �Զ�����6
	 */
	public String vdef6;
	/**
	 * �Զ�����7
	 */
	public String vdef7;
	/**
	 * �Զ�����8
	 */
	public String vdef8;
	/**
	 * �Զ�����9
	 */
	public String vdef9;
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
	 * ��ʵ��
	 */
	public WaBaSchTVO[] pk_s = null;

	/**
	 * ��ȡ������为����
	 * 
	 * @return ������为����
	 */
	public String getBa_mng_psnpk() {
		return this.ba_mng_psnpk;
	}

	/**
	 * ���ý�����为����
	 * 
	 * @param ba_mng_psnpk
	 *            ������为����
	 */
	public void setBa_mng_psnpk(String ba_mng_psnpk) {
		this.ba_mng_psnpk = ba_mng_psnpk;
	}

	/**
	 * ��ȡ����������
	 * 
	 * @return ����������
	 */
	public String getBa_mng_psnpk_showname() {
		return this.ba_mng_psnpk_showname;
	}

	/**
	 * ���÷���������
	 * 
	 * @param ba_mng_psnpk_showname
	 *            ����������
	 */
	public void setBa_mng_psnpk_showname(String ba_mng_psnpk_showname) {
		this.ba_mng_psnpk_showname = ba_mng_psnpk_showname;
	}

	/**
	 * ��ȡ����Ԫ����
	 * 
	 * @return ����Ԫ����
	 */
	public String getBa_unit_code() {
		return this.ba_unit_code;
	}

	/**
	 * ���ý���Ԫ����
	 * 
	 * @param ba_unit_code
	 *            ����Ԫ����
	 */
	public void setBa_unit_code(String ba_unit_code) {
		this.ba_unit_code = ba_unit_code;
	}

	/**
	 * ��ȡ����Ԫ����
	 * 
	 * @return ����Ԫ����
	 */
	public String getBa_unit_name() {
		return this.ba_unit_name;
	}

	/**
	 * ���ý���Ԫ����
	 * 
	 * @param ba_unit_name
	 *            ����Ԫ����
	 */
	public void setBa_unit_name(String ba_unit_name) {
		this.ba_unit_name = ba_unit_name;
	}

	/**
	 * ��ȡ����Ԫ����
	 * 
	 * @return ����Ԫ����
	 */
	public String getBa_unit_type() {
		return this.ba_unit_type;
	}

	/**
	 * ���ý���Ԫ����
	 * 
	 * @param ba_unit_type
	 *            ����Ԫ����
	 */
	public void setBa_unit_type(String ba_unit_type) {
		this.ba_unit_type = ba_unit_type;
	}

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
	 * @param class1
	 *            class1
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
	 * @param class2
	 *            class2
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
	 * @param class3
	 *            class3
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
	 * @param class4
	 *            class4
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
	 * @param class5
	 *            class5
	 */
	public void setClass5(String class5) {
		this.class5 = class5;
	}

	/**
	 * ��ȡѡ��
	 * 
	 * @return ѡ��
	 */
	public UFBoolean getIsapprove() {
		return this.isapprove;
	}

	/**
	 * ����ѡ��
	 * 
	 * @param isapprove
	 *            ѡ��
	 */
	public void setIsapprove(UFBoolean isapprove) {
		this.isapprove = isapprove;
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
	 * @param item1
	 *            item1
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
	 * @param item2
	 *            item2
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
	 * @param item3
	 *            item3
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
	 * @param item4
	 *            item4
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
	 * @param item5
	 *            item5
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
	 * @param memo1
	 *            ��ע1
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
	 * @param memo2
	 *            ��ע2
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
	 * @param memo3
	 *            ��ע3
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
	 * @param memo4
	 *            ��ע4
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
	 * @param memo5
	 *            ��ע5
	 */
	public void setMemo5(String memo5) {
		this.memo5 = memo5;
	}

	/**
	 * ��ȡ�ϲ㵥������
	 * 
	 * @return �ϲ㵥������
	 */
	public String getPk_ba_sch_h() {
		return this.pk_ba_sch_h;
	}

	/**
	 * �����ϲ㵥������
	 * 
	 * @param pk_ba_sch_h
	 *            �ϲ㵥������
	 */
	public void setPk_ba_sch_h(String pk_ba_sch_h) {
		this.pk_ba_sch_h = pk_ba_sch_h;
	}

	/**
	 * ��ȡ����
	 * 
	 * @return ����
	 */
	public String getPk_ba_sch_unit() {
		return this.pk_ba_sch_unit;
	}

	/**
	 * ��������
	 * 
	 * @param pk_ba_sch_unit
	 *            ����
	 */
	public void setPk_ba_sch_unit(String pk_ba_sch_unit) {
		this.pk_ba_sch_unit = pk_ba_sch_unit;
	}

	/**
	 * ��ȡ��˾
	 * 
	 * @return ��˾
	 */
	public String getPk_corp() {
		return this.pk_corp;
	}

	/**
	 * ���ù�˾
	 * 
	 * @param pk_corp
	 *            ��˾
	 */
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	/**
	 * ��ȡ����Ԫ����
	 * 
	 * @return ����Ԫ����
	 */
	public String getPk_wa_ba_unit() {
		return this.pk_wa_ba_unit;
	}

	/**
	 * ���ý���Ԫ����
	 * 
	 * @param pk_wa_ba_unit
	 *            ����Ԫ����
	 */
	public void setPk_wa_ba_unit(String pk_wa_ba_unit) {
		this.pk_wa_ba_unit = pk_wa_ba_unit;
	}

	/**
	 * ��ȡ�ۼƿɷ����ܶ�
	 * 
	 * @return �ۼƿɷ����ܶ�
	 */
	public String getPlan_totalmoney() {
		return this.plan_totalmoney;
	}

	/**
	 * �����ۼƿɷ����ܶ�
	 * 
	 * @param plan_totalmoney
	 *            �ۼƿɷ����ܶ�
	 */
	public void setPlan_totalmoney(String plan_totalmoney) {
		this.plan_totalmoney = plan_totalmoney;
	}

	/**
	 * ��ȡ��Ŀ�ƻ������1
	 * 
	 * @return ��Ŀ�ƻ������1
	 */
	public String getPtmny1() {
		return this.ptmny1;
	}

	/**
	 * ������Ŀ�ƻ������1
	 * 
	 * @param ptmny1
	 *            ��Ŀ�ƻ������1
	 */
	public void setPtmny1(String ptmny1) {
		this.ptmny1 = ptmny1;
	}

	/**
	 * ��ȡ��Ŀ�ƻ������2
	 * 
	 * @return ��Ŀ�ƻ������2
	 */
	public String getPtmny2() {
		return this.ptmny2;
	}

	/**
	 * ������Ŀ�ƻ������2
	 * 
	 * @param ptmny2
	 *            ��Ŀ�ƻ������2
	 */
	public void setPtmny2(String ptmny2) {
		this.ptmny2 = ptmny2;
	}

	/**
	 * ��ȡ��Ŀ�ƻ������3
	 * 
	 * @return ��Ŀ�ƻ������3
	 */
	public String getPtmny3() {
		return this.ptmny3;
	}

	/**
	 * ������Ŀ�ƻ������3
	 * 
	 * @param ptmny3
	 *            ��Ŀ�ƻ������3
	 */
	public void setPtmny3(String ptmny3) {
		this.ptmny3 = ptmny3;
	}

	/**
	 * ��ȡ��Ŀ�ƻ������4
	 * 
	 * @return ��Ŀ�ƻ������4
	 */
	public String getPtmny4() {
		return this.ptmny4;
	}

	/**
	 * ������Ŀ�ƻ������4
	 * 
	 * @param ptmny4
	 *            ��Ŀ�ƻ������4
	 */
	public void setPtmny4(String ptmny4) {
		this.ptmny4 = ptmny4;
	}

	/**
	 * ��ȡ��Ŀ�ƻ������5
	 * 
	 * @return ��Ŀ�ƻ������5
	 */
	public String getPtmny5() {
		return this.ptmny5;
	}

	/**
	 * ������Ŀ�ƻ������5
	 * 
	 * @param ptmny5
	 *            ��Ŀ�ƻ������5
	 */
	public void setPtmny5(String ptmny5) {
		this.ptmny5 = ptmny5;
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
	 * @param ts
	 *            ʱ���
	 */
	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	/**
	 * ��ȡ�Զ�����1
	 * 
	 * @return �Զ�����1
	 */
	public String getVdef1() {
		return this.vdef1;
	}

	/**
	 * �����Զ�����1
	 * 
	 * @param vdef1
	 *            �Զ�����1
	 */
	public void setVdef1(String vdef1) {
		this.vdef1 = vdef1;
	}

	/**
	 * ��ȡ�Զ�����10
	 * 
	 * @return �Զ�����10
	 */
	public String getVdef10() {
		return this.vdef10;
	}

	/**
	 * �����Զ�����10
	 * 
	 * @param vdef10
	 *            �Զ�����10
	 */
	public void setVdef10(String vdef10) {
		this.vdef10 = vdef10;
	}

	/**
	 * ��ȡ�Զ�����11
	 * 
	 * @return �Զ�����11
	 */
	public String getVdef11() {
		return this.vdef11;
	}

	/**
	 * �����Զ�����11
	 * 
	 * @param vdef11
	 *            �Զ�����11
	 */
	public void setVdef11(String vdef11) {
		this.vdef11 = vdef11;
	}

	/**
	 * ��ȡ�Զ�����12
	 * 
	 * @return �Զ�����12
	 */
	public String getVdef12() {
		return this.vdef12;
	}

	/**
	 * �����Զ�����12
	 * 
	 * @param vdef12
	 *            �Զ�����12
	 */
	public void setVdef12(String vdef12) {
		this.vdef12 = vdef12;
	}

	/**
	 * ��ȡ�Զ�����13
	 * 
	 * @return �Զ�����13
	 */
	public String getVdef13() {
		return this.vdef13;
	}

	/**
	 * �����Զ�����13
	 * 
	 * @param vdef13
	 *            �Զ�����13
	 */
	public void setVdef13(String vdef13) {
		this.vdef13 = vdef13;
	}

	/**
	 * ��ȡ�Զ�����14
	 * 
	 * @return �Զ�����14
	 */
	public String getVdef14() {
		return this.vdef14;
	}

	/**
	 * �����Զ�����14
	 * 
	 * @param vdef14
	 *            �Զ�����14
	 */
	public void setVdef14(String vdef14) {
		this.vdef14 = vdef14;
	}

	/**
	 * ��ȡ�Զ�����15
	 * 
	 * @return �Զ�����15
	 */
	public String getVdef15() {
		return this.vdef15;
	}

	/**
	 * �����Զ�����15
	 * 
	 * @param vdef15
	 *            �Զ�����15
	 */
	public void setVdef15(String vdef15) {
		this.vdef15 = vdef15;
	}

	/**
	 * ��ȡ�Զ�����16
	 * 
	 * @return �Զ�����16
	 */
	public String getVdef16() {
		return this.vdef16;
	}

	/**
	 * �����Զ�����16
	 * 
	 * @param vdef16
	 *            �Զ�����16
	 */
	public void setVdef16(String vdef16) {
		this.vdef16 = vdef16;
	}

	/**
	 * ��ȡ�Զ�����17
	 * 
	 * @return �Զ�����17
	 */
	public String getVdef17() {
		return this.vdef17;
	}

	/**
	 * �����Զ�����17
	 * 
	 * @param vdef17
	 *            �Զ�����17
	 */
	public void setVdef17(String vdef17) {
		this.vdef17 = vdef17;
	}

	/**
	 * ��ȡ�Զ�����18
	 * 
	 * @return �Զ�����18
	 */
	public String getVdef18() {
		return this.vdef18;
	}

	/**
	 * �����Զ�����18
	 * 
	 * @param vdef18
	 *            �Զ�����18
	 */
	public void setVdef18(String vdef18) {
		this.vdef18 = vdef18;
	}

	/**
	 * ��ȡ�Զ�����19
	 * 
	 * @return �Զ�����19
	 */
	public String getVdef19() {
		return this.vdef19;
	}

	/**
	 * �����Զ�����19
	 * 
	 * @param vdef19
	 *            �Զ�����19
	 */
	public void setVdef19(String vdef19) {
		this.vdef19 = vdef19;
	}

	/**
	 * ��ȡ�Զ�����2
	 * 
	 * @return �Զ�����2
	 */
	public String getVdef2() {
		return this.vdef2;
	}

	/**
	 * �����Զ�����2
	 * 
	 * @param vdef2
	 *            �Զ�����2
	 */
	public void setVdef2(String vdef2) {
		this.vdef2 = vdef2;
	}

	/**
	 * ��ȡ�Զ�����20
	 * 
	 * @return �Զ�����20
	 */
	public String getVdef20() {
		return this.vdef20;
	}

	/**
	 * �����Զ�����20
	 * 
	 * @param vdef20
	 *            �Զ�����20
	 */
	public void setVdef20(String vdef20) {
		this.vdef20 = vdef20;
	}

	/**
	 * ��ȡ�Զ�����3
	 * 
	 * @return �Զ�����3
	 */
	public String getVdef3() {
		return this.vdef3;
	}

	/**
	 * �����Զ�����3
	 * 
	 * @param vdef3
	 *            �Զ�����3
	 */
	public void setVdef3(String vdef3) {
		this.vdef3 = vdef3;
	}

	/**
	 * ��ȡ�Զ�����4
	 * 
	 * @return �Զ�����4
	 */
	public String getVdef4() {
		return this.vdef4;
	}

	/**
	 * �����Զ�����4
	 * 
	 * @param vdef4
	 *            �Զ�����4
	 */
	public void setVdef4(String vdef4) {
		this.vdef4 = vdef4;
	}

	/**
	 * ��ȡ�Զ�����5
	 * 
	 * @return �Զ�����5
	 */
	public String getVdef5() {
		return this.vdef5;
	}

	/**
	 * �����Զ�����5
	 * 
	 * @param vdef5
	 *            �Զ�����5
	 */
	public void setVdef5(String vdef5) {
		this.vdef5 = vdef5;
	}

	/**
	 * ��ȡ�Զ�����6
	 * 
	 * @return �Զ�����6
	 */
	public String getVdef6() {
		return this.vdef6;
	}

	/**
	 * �����Զ�����6
	 * 
	 * @param vdef6
	 *            �Զ�����6
	 */
	public void setVdef6(String vdef6) {
		this.vdef6 = vdef6;
	}

	/**
	 * ��ȡ�Զ�����7
	 * 
	 * @return �Զ�����7
	 */
	public String getVdef7() {
		return this.vdef7;
	}

	/**
	 * �����Զ�����7
	 * 
	 * @param vdef7
	 *            �Զ�����7
	 */
	public void setVdef7(String vdef7) {
		this.vdef7 = vdef7;
	}

	/**
	 * ��ȡ�Զ�����8
	 * 
	 * @return �Զ�����8
	 */
	public String getVdef8() {
		return this.vdef8;
	}

	/**
	 * �����Զ�����8
	 * 
	 * @param vdef8
	 *            �Զ�����8
	 */
	public void setVdef8(String vdef8) {
		this.vdef8 = vdef8;
	}

	/**
	 * ��ȡ�Զ�����9
	 * 
	 * @return �Զ�����9
	 */
	public String getVdef9() {
		return this.vdef9;
	}

	/**
	 * �����Զ�����9
	 * 
	 * @param vdef9
	 *            �Զ�����9
	 */
	public void setVdef9(String vdef9) {
		this.vdef9 = vdef9;
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
	 * @param waitem1
	 *            ������Ŀ1
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
	 * @param waitem2
	 *            ������Ŀ2
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
	 * @param waitem3
	 *            ������Ŀ3
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
	 * @param waitem4
	 *            ������Ŀ4
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
	 * @param waitem5
	 *            ������Ŀ5
	 */
	public void setWaitem5(String waitem5) {
		this.waitem5 = waitem5;
	}

	@Override
	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("hrwa.WaBaSchBVO");
	}

	public WaBaSchTVO[] getPk_s() {
		return pk_s;
	}

	public void setPk_s(WaBaSchTVO[] pk_s) {
		this.pk_s = pk_s;
	}


}