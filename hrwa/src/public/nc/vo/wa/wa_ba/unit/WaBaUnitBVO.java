package nc.vo.wa.wa_ba.unit;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

public class WaBaUnitBVO extends SuperVO {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1305084669154804919L;
	/**
	 * ������֯
	 */
	public String pk_corp;
	/**
	 * ��������
	 */
	public String pk_deptdoc;
	/**
	 * ��Ա����
	 */
	public String pk_psndoc;

	/**
	 * ��Ա������¼
	 */
	private String pk_psnjob;

	/**
	 * �ϲ㵥������
	 */
	public String pk_wa_ba_unit;
	/**
	 * �ӱ�����
	 */
	public String pk_wa_ba_unit_b;
	/**
	 * ��Ա����
	 */
	public String psnname;
	/**
	 * �Ա�
	 */
	public Integer sex;
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
	 * drֵ
	 */
	public Integer dr;

	/**
	 * ��ȡ������֯
	 * 
	 * @return ������֯
	 */
	public String getPk_corp() {
		return this.pk_corp;
	}

	/**
	 * ����������֯
	 * 
	 * @param pk_corp ������֯
	 */
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	/**
	 * ��ȡ��������
	 * 
	 * @return ��������
	 */
	public String getPk_deptdoc() {
		return this.pk_deptdoc;
	}

	/**
	 * ������������
	 * 
	 * @param pk_deptdoc ��������
	 */
	public void setPk_deptdoc(String pk_deptdoc) {
		this.pk_deptdoc = pk_deptdoc;
	}

	/**
	 * ��ȡ��Ա����
	 * 
	 * @return ��Ա����
	 */
	public String getPk_psndoc() {
		return this.pk_psndoc;
	}

	/**
	 * ������Ա����
	 * 
	 * @param pk_psndoc ��Ա����
	 */
	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	/**
	 * ��ȡ�ϲ㵥������
	 * 
	 * @return �ϲ㵥������
	 */
	public String getPk_wa_ba_unit() {
		return this.pk_wa_ba_unit;
	}

	/**
	 * �����ϲ㵥������
	 * 
	 * @param pk_wa_ba_unit �ϲ㵥������
	 */
	public void setPk_wa_ba_unit(String pk_wa_ba_unit) {
		this.pk_wa_ba_unit = pk_wa_ba_unit;
	}

	/**
	 * ��ȡ�ӱ�����
	 * 
	 * @return �ӱ�����
	 */
	public String getPk_wa_ba_unit_b() {
		return this.pk_wa_ba_unit_b;
	}

	/**
	 * �����ӱ�����
	 * 
	 * @param pk_wa_ba_unit_b �ӱ�����
	 */
	public void setPk_wa_ba_unit_b(String pk_wa_ba_unit_b) {
		this.pk_wa_ba_unit_b = pk_wa_ba_unit_b;
	}

	/**
	 * ��ȡ��Ա����
	 * 
	 * @return ��Ա����
	 */
	public String getPsnname() {
		return this.psnname;
	}

	/**
	 * ������Ա����
	 * 
	 * @param psnname ��Ա����
	 */
	public void setPsnname(String psnname) {
		this.psnname = psnname;
	}

	/**
	 * ��ȡ�Ա�
	 * 
	 * @return �Ա�
	 * @see String
	 */
	public Integer getSex() {
		return this.sex;
	}

	/**
	 * �����Ա�
	 * 
	 * @param sex �Ա�
	 * @see String
	 */
	public void setSex(Integer sex) {
		this.sex = sex;
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
	 * @param vdef1 �Զ�����1
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
	 * @param vdef10 �Զ�����10
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
	 * @param vdef11 �Զ�����11
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
	 * @param vdef12 �Զ�����12
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
	 * @param vdef13 �Զ�����13
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
	 * @param vdef14 �Զ�����14
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
	 * @param vdef15 �Զ�����15
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
	 * @param vdef16 �Զ�����16
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
	 * @param vdef17 �Զ�����17
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
	 * @param vdef18 �Զ�����18
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
	 * @param vdef19 �Զ�����19
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
	 * @param vdef2 �Զ�����2
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
	 * @param vdef20 �Զ�����20
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
	 * @param vdef3 �Զ�����3
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
	 * @param vdef4 �Զ�����4
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
	 * @param vdef5 �Զ�����5
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
	 * @param vdef6 �Զ�����6
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
	 * @param vdef7 �Զ�����7
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
	 * @param vdef8 �Զ�����8
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
	 * @param vdef9 �Զ�����9
	 */
	public void setVdef9(String vdef9) {
		this.vdef9 = vdef9;
	}

	@Override
	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("hrwa.WaBaUnitBVO");
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
}