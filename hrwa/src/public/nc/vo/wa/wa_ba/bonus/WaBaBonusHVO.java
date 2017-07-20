package nc.vo.wa.wa_ba.bonus;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

public class WaBaBonusHVO extends SuperVO {
	/**
	 * ����ʱ��
	 */
	public UFDateTime approvedate;
	/**
	 * ��������
	 */
	public String approvenote;
	/**
	 * ������
	 */
	public String approver;
	/**
	 * ����״̬
	 */
	public Integer approvestatus;
	/**
	 * ���ݱ���
	 */
	public String bill_code;
	/**
	 * ��������
	 */
	public String bill_name;
	/**
	 * �ύ����
	 */
	public UFDateTime bill_submit_date;
	/**
	 * �ύ��
	 */
	public String bill_submitter;
	/**
	 * ��������
	 */
	public UFDate billdate;
	/**
	 * ����ʱ��
	 */
	public UFDateTime creationtime;
	/**
	 * ������
	 */
	public String creator;
	/**
	 * �޸�ʱ��
	 */
	public UFDateTime modifiedtime;
	/**
	 * �޸���
	 */
	public String modifier;
	/**
	 * �¶�
	 */
	public String period;
	/**
	 * ���𷽰�����
	 */
	public String pk_ba_sch_h;
	/**
	 * ����
	 */
	public String pk_bonus;
	/**
	 * ��������
	 */
	public String pk_busitype;
	/**
	 * ��������
	 */
	public String pk_deptdoc;
	/**
	 * ����
	 */
	public String pk_group;
	/**
	 * ������λ
	 */
	public String pk_om_job;
	/**
	 * ��֯
	 */
	public String pk_org;
	/**
	 * ��֯�汾
	 */
	public String pk_org_v;
	/**
	 * pk_psnbasdoc
	 */
	public String pk_psnbasdoc;
	/**
	 * ��Ա
	 */
	public String pk_psndoc;
	/**
	 * �����ܶ�
	 */
	public UFDouble totalamount;
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
	 * ��ע
	 */
	public String vmemo;
	/**
	 * ���
	 */
	public String year;

	/**
	 * dr
	 */
	public Integer dr;

	/**
	 * ��ȡ����ʱ��
	 * 
	 * @return ����ʱ��
	 */
	public UFDateTime getApprovedate() {
		return this.approvedate;
	}

	/**
	 * ��������ʱ��
	 * 
	 * @param approvedate
	 *            ����ʱ��
	 */
	public void setApprovedate(UFDateTime approvedate) {
		this.approvedate = approvedate;
	}

	/**
	 * ��ȡ��������
	 * 
	 * @return ��������
	 */
	public String getApprovenote() {
		return this.approvenote;
	}

	/**
	 * ������������
	 * 
	 * @param approvenote
	 *            ��������
	 */
	public void setApprovenote(String approvenote) {
		this.approvenote = approvenote;
	}

	/**
	 * ��ȡ������
	 * 
	 * @return ������
	 */
	public String getApprover() {
		return this.approver;
	}

	/**
	 * ����������
	 * 
	 * @param approver
	 *            ������
	 */
	public void setApprover(String approver) {
		this.approver = approver;
	}

	/**
	 * ��ȡ����״̬
	 * 
	 * @return ����״̬
	 * @see String
	 */
	public Integer getApprovestatus() {
		return this.approvestatus;
	}

	/**
	 * ��������״̬
	 * 
	 * @param approvestatus
	 *            ����״̬
	 * @see String
	 */
	public void setApprovestatus(Integer approvestatus) {
		this.approvestatus = approvestatus;
	}

	/**
	 * ��ȡ���ݱ���
	 * 
	 * @return ���ݱ���
	 */
	public String getBill_code() {
		return this.bill_code;
	}

	/**
	 * ���õ��ݱ���
	 * 
	 * @param bill_code
	 *            ���ݱ���
	 */
	public void setBill_code(String bill_code) {
		this.bill_code = bill_code;
	}

	/**
	 * ��ȡ��������
	 * 
	 * @return ��������
	 */
	public String getBill_name() {
		return this.bill_name;
	}

	/**
	 * ���õ�������
	 * 
	 * @param bill_name
	 *            ��������
	 */
	public void setBill_name(String bill_name) {
		this.bill_name = bill_name;
	}

	/**
	 * ��ȡ�ύ����
	 * 
	 * @return �ύ����
	 */
	public UFDateTime getBill_submit_date() {
		return this.bill_submit_date;
	}

	/**
	 * �����ύ����
	 * 
	 * @param bill_submit_date
	 *            �ύ����
	 */
	public void setBill_submit_date(UFDateTime bill_submit_date) {
		this.bill_submit_date = bill_submit_date;
	}

	/**
	 * ��ȡ�ύ��
	 * 
	 * @return �ύ��
	 */
	public String getBill_submitter() {
		return this.bill_submitter;
	}

	/**
	 * �����ύ��
	 * 
	 * @param bill_submitter
	 *            �ύ��
	 */
	public void setBill_submitter(String bill_submitter) {
		this.bill_submitter = bill_submitter;
	}

	/**
	 * ��ȡ��������
	 * 
	 * @return ��������
	 */
	public UFDate getBilldate() {
		return this.billdate;
	}

	/**
	 * ���õ�������
	 * 
	 * @param billdate
	 *            ��������
	 */
	public void setBilldate(UFDate billdate) {
		this.billdate = billdate;
	}

	/**
	 * ��ȡ����ʱ��
	 * 
	 * @return ����ʱ��
	 */
	public UFDateTime getCreationtime() {
		return this.creationtime;
	}

	/**
	 * ���ô���ʱ��
	 * 
	 * @param creationtime
	 *            ����ʱ��
	 */
	public void setCreationtime(UFDateTime creationtime) {
		this.creationtime = creationtime;
	}

	/**
	 * ��ȡ������
	 * 
	 * @return ������
	 */
	public String getCreator() {
		return this.creator;
	}

	/**
	 * ���ô�����
	 * 
	 * @param creator
	 *            ������
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * ��ȡ�޸�ʱ��
	 * 
	 * @return �޸�ʱ��
	 */
	public UFDateTime getModifiedtime() {
		return this.modifiedtime;
	}

	/**
	 * �����޸�ʱ��
	 * 
	 * @param modifiedtime
	 *            �޸�ʱ��
	 */
	public void setModifiedtime(UFDateTime modifiedtime) {
		this.modifiedtime = modifiedtime;
	}

	/**
	 * ��ȡ�޸���
	 * 
	 * @return �޸���
	 */
	public String getModifier() {
		return this.modifier;
	}

	/**
	 * �����޸���
	 * 
	 * @param modifier
	 *            �޸���
	 */
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	/**
	 * ��ȡ�¶�
	 * 
	 * @return �¶�
	 */
	public String getPeriod() {
		return this.period;
	}

	/**
	 * �����¶�
	 * 
	 * @param period
	 *            �¶�
	 */
	public void setPeriod(String period) {
		this.period = period;
	}

	/**
	 * ��ȡ���𷽰�����
	 * 
	 * @return ���𷽰�����
	 */
	public String getPk_ba_sch_h() {
		return this.pk_ba_sch_h;
	}

	/**
	 * ���ý��𷽰�����
	 * 
	 * @param pk_ba_sch_h
	 *            ���𷽰�����
	 */
	public void setPk_ba_sch_h(String pk_ba_sch_h) {
		this.pk_ba_sch_h = pk_ba_sch_h;
	}

	/**
	 * ��ȡ����
	 * 
	 * @return ����
	 */
	public String getPk_bonus() {
		return this.pk_bonus;
	}

	/**
	 * ��������
	 * 
	 * @param pk_bonus
	 *            ����
	 */
	public void setPk_bonus(String pk_bonus) {
		this.pk_bonus = pk_bonus;
	}

	/**
	 * ��ȡ��������
	 * 
	 * @return ��������
	 */
	public String getPk_busitype() {
		return this.pk_busitype;
	}

	/**
	 * ������������
	 * 
	 * @param pk_busitype
	 *            ��������
	 */
	public void setPk_busitype(String pk_busitype) {
		this.pk_busitype = pk_busitype;
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
	 * @param pk_deptdoc
	 *            ��������
	 */
	public void setPk_deptdoc(String pk_deptdoc) {
		this.pk_deptdoc = pk_deptdoc;
	}

	/**
	 * ��ȡ����
	 * 
	 * @return ����
	 */
	public String getPk_group() {
		return this.pk_group;
	}

	/**
	 * ���ü���
	 * 
	 * @param pk_group
	 *            ����
	 */
	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}

	/**
	 * ��ȡ������λ
	 * 
	 * @return ������λ
	 */
	public String getPk_om_job() {
		return this.pk_om_job;
	}

	/**
	 * ����������λ
	 * 
	 * @param pk_om_job
	 *            ������λ
	 */
	public void setPk_om_job(String pk_om_job) {
		this.pk_om_job = pk_om_job;
	}

	/**
	 * ��ȡ��֯
	 * 
	 * @return ��֯
	 */
	public String getPk_org() {
		return this.pk_org;
	}

	/**
	 * ������֯
	 * 
	 * @param pk_org
	 *            ��֯
	 */
	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	/**
	 * ��ȡ��֯�汾
	 * 
	 * @return ��֯�汾
	 */
	public String getPk_org_v() {
		return this.pk_org_v;
	}

	/**
	 * ������֯�汾
	 * 
	 * @param pk_org_v
	 *            ��֯�汾
	 */
	public void setPk_org_v(String pk_org_v) {
		this.pk_org_v = pk_org_v;
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
	 * @param pk_psnbasdoc
	 *            pk_psnbasdoc
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
	 * @param pk_psndoc
	 *            ��Ա
	 */
	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	/**
	 * ��ȡ�����ܶ�
	 * 
	 * @return �����ܶ�
	 */
	public UFDouble getTotalamount() {
		return this.totalamount;
	}

	/**
	 * ���÷����ܶ�
	 * 
	 * @param totalamount
	 *            �����ܶ�
	 */
	public void setTotalamount(UFDouble totalamount) {
		this.totalamount = totalamount;
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
	 * ��ȡ��ע
	 * 
	 * @return ��ע
	 */
	public String getVmemo() {
		return this.vmemo;
	}

	/**
	 * ���ñ�ע
	 * 
	 * @param vmemo
	 *            ��ע
	 */
	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
	}

	/**
	 * ��ȡ���
	 * 
	 * @return ���
	 */
	public String getYear() {
		return this.year;
	}

	/**
	 * �������
	 * 
	 * @param year
	 *            ���
	 */
	public void setYear(String year) {
		this.year = year;
	}

	@Override
	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("hrwa.WaBaBonusHVO");
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}
}