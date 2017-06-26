package nc.vo.wa.wa_ba.sch;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

public class WaBaSchHVO extends SuperVO {
/**
*����ģʽ
*/
public String allocmodel;
/**
*allow_moidifyfactor
*/
public UFBoolean allow_moidifyfactor;
/**
*����ʱ��
*/
public UFDateTime approvedate;
/**
*��������
*/
public String approvenote;
/**
*������
*/
public String approver;
/**
*����״̬
*/
public Integer approvestatus;
/**
*������Ŀ����
*/
public String baitem_count;
/**
*�ύ����
*/
public UFDate bill_submit_date;
/**
*�ύ��
*/
public String bill_submitter;
/**
*��������
*/
public UFDate billdate;
/**
*��������
*/
public String billtype;
/**
*�������
*/
public UFDouble calcu_base;
/**
*���䷽ʽ
*/
public String calcu_method;
/**
*calcu_totalfactor
*/
public UFDouble calcu_totalfactor;
/**
*calcu_totalmoney
*/
public UFDouble calcu_totalmoney;
/**
*�ڼ�
*/
public String cperiod;
/**
*����ʱ��
*/
public UFDateTime creationtime;
/**
*������
*/
public String creator;
/**
*������
*/
public String ctrl_options;
/**
*���
*/
public String cyear;
/**
*�Ƿ����н�ʷ���
*/
public UFBoolean is_write2wa;
/**
*��ע
*/
public String memo;
/**
*�޸�ʱ��
*/
public UFDateTime modifiedtime;
/**
*�޸���
*/
public String modifier;
/**
*��������
*/
public String pk_ba_sch_h;
/**
*ҵ������
*/
public String pk_busitype;
/**
*����ϵ������
*/
public String pk_factor_sch;
/**
*����
*/
public String pk_group;
/**
*��֯
*/
public String pk_org;
/**
*��֯�汾
*/
public String pk_org_v;
/**
*�ƻ��ܶ�
*/
public UFDouble plan_total_money;
/**
*��������
*/
public String sch_code;
/**
*��������
*/
public String sch_name;
/**
*�ܶ���Ʒ���
*/
public String total_ctrl_method;
/**
*ʱ���
*/
public UFDateTime ts;
/**
*�Զ�����1
*/
public String vdef1;
/**
*�Զ�����10
*/
public String vdef10;
/**
*�Զ�����11
*/
public String vdef11;
/**
*�Զ�����12
*/
public String vdef12;
/**
*�Զ�����13
*/
public String vdef13;
/**
*�Զ�����14
*/
public String vdef14;
/**
*�Զ�����15
*/
public String vdef15;
/**
*�Զ�����16
*/
public String vdef16;
/**
*�Զ�����17
*/
public String vdef17;
/**
*�Զ�����18
*/
public String vdef18;
/**
*�Զ�����19
*/
public String vdef19;
/**
*�Զ�����2
*/
public String vdef2;
/**
*�Զ�����20
*/
public String vdef20;
/**
*�Զ�����3
*/
public String vdef3;
/**
*�Զ�����4
*/
public String vdef4;
/**
*�Զ�����5
*/
public String vdef5;
/**
*�Զ�����6
*/
public String vdef6;
/**
*�Զ�����7
*/
public String vdef7;
/**
*�Զ�����8
*/
public String vdef8;
/**
*�Զ�����9
*/
public String vdef9;
/** 
* ��ȡ����ģʽ
*
* @return ����ģʽ
*/
public String getAllocmodel () {
return this.allocmodel;
 } 

/** 
* ���÷���ģʽ
*
* @param allocmodel ����ģʽ
*/
public void setAllocmodel ( String allocmodel) {
this.allocmodel=allocmodel;
 } 

/** 
* ��ȡallow_moidifyfactor
*
* @return allow_moidifyfactor
*/
public UFBoolean getAllow_moidifyfactor () {
return this.allow_moidifyfactor;
 } 

/** 
* ����allow_moidifyfactor
*
* @param allow_moidifyfactor allow_moidifyfactor
*/
public void setAllow_moidifyfactor ( UFBoolean allow_moidifyfactor) {
this.allow_moidifyfactor=allow_moidifyfactor;
 } 

/** 
* ��ȡ����ʱ��
*
* @return ����ʱ��
*/
public UFDateTime getApprovedate () {
return this.approvedate;
 } 

/** 
* ��������ʱ��
*
* @param approvedate ����ʱ��
*/
public void setApprovedate ( UFDateTime approvedate) {
this.approvedate=approvedate;
 } 

/** 
* ��ȡ��������
*
* @return ��������
*/
public String getApprovenote () {
return this.approvenote;
 } 

/** 
* ������������
*
* @param approvenote ��������
*/
public void setApprovenote ( String approvenote) {
this.approvenote=approvenote;
 } 

/** 
* ��ȡ������
*
* @return ������
*/
public String getApprover () {
return this.approver;
 } 

/** 
* ����������
*
* @param approver ������
*/
public void setApprover ( String approver) {
this.approver=approver;
 } 

/** 
* ��ȡ����״̬
*
* @return ����״̬
* @see String
*/
public Integer getApprovestatus () {
return this.approvestatus;
 } 

/** 
* ��������״̬
*
* @param approvestatus ����״̬
* @see String
*/
public void setApprovestatus ( Integer approvestatus) {
this.approvestatus=approvestatus;
 } 

/** 
* ��ȡ������Ŀ����
*
* @return ������Ŀ����
*/
public String getBaitem_count () {
return this.baitem_count;
 } 

/** 
* ���ý�����Ŀ����
*
* @param baitem_count ������Ŀ����
*/
public void setBaitem_count ( String baitem_count) {
this.baitem_count=baitem_count;
 } 

/** 
* ��ȡ�ύ����
*
* @return �ύ����
*/
public UFDate getBill_submit_date () {
return this.bill_submit_date;
 } 

/** 
* �����ύ����
*
* @param bill_submit_date �ύ����
*/
public void setBill_submit_date ( UFDate bill_submit_date) {
this.bill_submit_date=bill_submit_date;
 } 

/** 
* ��ȡ�ύ��
*
* @return �ύ��
*/
public String getBill_submitter () {
return this.bill_submitter;
 } 

/** 
* �����ύ��
*
* @param bill_submitter �ύ��
*/
public void setBill_submitter ( String bill_submitter) {
this.bill_submitter=bill_submitter;
 } 

/** 
* ��ȡ��������
*
* @return ��������
*/
public UFDate getBilldate () {
return this.billdate;
 } 

/** 
* ���õ�������
*
* @param billdate ��������
*/
public void setBilldate ( UFDate billdate) {
this.billdate=billdate;
 } 

/** 
* ��ȡ��������
*
* @return ��������
*/
public String getBilltype () {
return this.billtype;
 } 

/** 
* ���õ�������
*
* @param billtype ��������
*/
public void setBilltype ( String billtype) {
this.billtype=billtype;
 } 

/** 
* ��ȡ�������
*
* @return �������
*/
public UFDouble getCalcu_base () {
return this.calcu_base;
 } 

/** 
* ���÷������
*
* @param calcu_base �������
*/
public void setCalcu_base ( UFDouble calcu_base) {
this.calcu_base=calcu_base;
 } 

/** 
* ��ȡ���䷽ʽ
*
* @return ���䷽ʽ
*/
public String getCalcu_method () {
return this.calcu_method;
 } 

/** 
* ���÷��䷽ʽ
*
* @param calcu_method ���䷽ʽ
*/
public void setCalcu_method ( String calcu_method) {
this.calcu_method=calcu_method;
 } 

/** 
* ��ȡcalcu_totalfactor
*
* @return calcu_totalfactor
*/
public UFDouble getCalcu_totalfactor () {
return this.calcu_totalfactor;
 } 

/** 
* ����calcu_totalfactor
*
* @param calcu_totalfactor calcu_totalfactor
*/
public void setCalcu_totalfactor ( UFDouble calcu_totalfactor) {
this.calcu_totalfactor=calcu_totalfactor;
 } 

/** 
* ��ȡcalcu_totalmoney
*
* @return calcu_totalmoney
*/
public UFDouble getCalcu_totalmoney () {
return this.calcu_totalmoney;
 } 

/** 
* ����calcu_totalmoney
*
* @param calcu_totalmoney calcu_totalmoney
*/
public void setCalcu_totalmoney ( UFDouble calcu_totalmoney) {
this.calcu_totalmoney=calcu_totalmoney;
 } 

/** 
* ��ȡ�ڼ�
*
* @return �ڼ�
*/
public String getCperiod () {
return this.cperiod;
 } 

/** 
* �����ڼ�
*
* @param cperiod �ڼ�
*/
public void setCperiod ( String cperiod) {
this.cperiod=cperiod;
 } 

/** 
* ��ȡ����ʱ��
*
* @return ����ʱ��
*/
public UFDateTime getCreationtime () {
return this.creationtime;
 } 

/** 
* ���ô���ʱ��
*
* @param creationtime ����ʱ��
*/
public void setCreationtime ( UFDateTime creationtime) {
this.creationtime=creationtime;
 } 

/** 
* ��ȡ������
*
* @return ������
*/
public String getCreator () {
return this.creator;
 } 

/** 
* ���ô�����
*
* @param creator ������
*/
public void setCreator ( String creator) {
this.creator=creator;
 } 

/** 
* ��ȡ������
*
* @return ������
*/
public String getCtrl_options () {
return this.ctrl_options;
 } 

/** 
* ���ÿ�����
*
* @param ctrl_options ������
*/
public void setCtrl_options ( String ctrl_options) {
this.ctrl_options=ctrl_options;
 } 

/** 
* ��ȡ���
*
* @return ���
*/
public String getCyear () {
return this.cyear;
 } 

/** 
* �������
*
* @param cyear ���
*/
public void setCyear ( String cyear) {
this.cyear=cyear;
 } 

/** 
* ��ȡ�Ƿ����н�ʷ���
*
* @return �Ƿ����н�ʷ���
*/
public UFBoolean getIs_write2wa () {
return this.is_write2wa;
 } 

/** 
* �����Ƿ����н�ʷ���
*
* @param is_write2wa �Ƿ����н�ʷ���
*/
public void setIs_write2wa ( UFBoolean is_write2wa) {
this.is_write2wa=is_write2wa;
 } 

/** 
* ��ȡ��ע
*
* @return ��ע
*/
public String getMemo () {
return this.memo;
 } 

/** 
* ���ñ�ע
*
* @param memo ��ע
*/
public void setMemo ( String memo) {
this.memo=memo;
 } 

/** 
* ��ȡ�޸�ʱ��
*
* @return �޸�ʱ��
*/
public UFDateTime getModifiedtime () {
return this.modifiedtime;
 } 

/** 
* �����޸�ʱ��
*
* @param modifiedtime �޸�ʱ��
*/
public void setModifiedtime ( UFDateTime modifiedtime) {
this.modifiedtime=modifiedtime;
 } 

/** 
* ��ȡ�޸���
*
* @return �޸���
*/
public String getModifier () {
return this.modifier;
 } 

/** 
* �����޸���
*
* @param modifier �޸���
*/
public void setModifier ( String modifier) {
this.modifier=modifier;
 } 

/** 
* ��ȡ��������
*
* @return ��������
*/
public String getPk_ba_sch_h () {
return this.pk_ba_sch_h;
 } 

/** 
* ������������
*
* @param pk_ba_sch_h ��������
*/
public void setPk_ba_sch_h ( String pk_ba_sch_h) {
this.pk_ba_sch_h=pk_ba_sch_h;
 } 

/** 
* ��ȡҵ������
*
* @return ҵ������
*/
public String getPk_busitype () {
return this.pk_busitype;
 } 

/** 
* ����ҵ������
*
* @param pk_busitype ҵ������
*/
public void setPk_busitype ( String pk_busitype) {
this.pk_busitype=pk_busitype;
 } 

/** 
* ��ȡ����ϵ������
*
* @return ����ϵ������
*/
public String getPk_factor_sch () {
return this.pk_factor_sch;
 } 

/** 
* ���÷���ϵ������
*
* @param pk_factor_sch ����ϵ������
*/
public void setPk_factor_sch ( String pk_factor_sch) {
this.pk_factor_sch=pk_factor_sch;
 } 

/** 
* ��ȡ����
*
* @return ����
*/
public String getPk_group () {
return this.pk_group;
 } 

/** 
* ���ü���
*
* @param pk_group ����
*/
public void setPk_group ( String pk_group) {
this.pk_group=pk_group;
 } 

/** 
* ��ȡ��֯
*
* @return ��֯
*/
public String getPk_org () {
return this.pk_org;
 } 

/** 
* ������֯
*
* @param pk_org ��֯
*/
public void setPk_org ( String pk_org) {
this.pk_org=pk_org;
 } 

/** 
* ��ȡ��֯�汾
*
* @return ��֯�汾
*/
public String getPk_org_v () {
return this.pk_org_v;
 } 

/** 
* ������֯�汾
*
* @param pk_org_v ��֯�汾
*/
public void setPk_org_v ( String pk_org_v) {
this.pk_org_v=pk_org_v;
 } 

/** 
* ��ȡ�ƻ��ܶ�
*
* @return �ƻ��ܶ�
*/
public UFDouble getPlan_total_money () {
return this.plan_total_money;
 } 

/** 
* ���üƻ��ܶ�
*
* @param plan_total_money �ƻ��ܶ�
*/
public void setPlan_total_money ( UFDouble plan_total_money) {
this.plan_total_money=plan_total_money;
 } 

/** 
* ��ȡ��������
*
* @return ��������
*/
public String getSch_code () {
return this.sch_code;
 } 

/** 
* ���÷�������
*
* @param sch_code ��������
*/
public void setSch_code ( String sch_code) {
this.sch_code=sch_code;
 } 

/** 
* ��ȡ��������
*
* @return ��������
*/
public String getSch_name () {
return this.sch_name;
 } 

/** 
* ���÷�������
*
* @param sch_name ��������
*/
public void setSch_name ( String sch_name) {
this.sch_name=sch_name;
 } 

/** 
* ��ȡ�ܶ���Ʒ���
*
* @return �ܶ���Ʒ���
*/
public String getTotal_ctrl_method () {
return this.total_ctrl_method;
 } 

/** 
* �����ܶ���Ʒ���
*
* @param total_ctrl_method �ܶ���Ʒ���
*/
public void setTotal_ctrl_method ( String total_ctrl_method) {
this.total_ctrl_method=total_ctrl_method;
 } 

/** 
* ��ȡʱ���
*
* @return ʱ���
*/
public UFDateTime getTs () {
return this.ts;
 } 

/** 
* ����ʱ���
*
* @param ts ʱ���
*/
public void setTs ( UFDateTime ts) {
this.ts=ts;
 } 

/** 
* ��ȡ�Զ�����1
*
* @return �Զ�����1
*/
public String getVdef1 () {
return this.vdef1;
 } 

/** 
* �����Զ�����1
*
* @param vdef1 �Զ�����1
*/
public void setVdef1 ( String vdef1) {
this.vdef1=vdef1;
 } 

/** 
* ��ȡ�Զ�����10
*
* @return �Զ�����10
*/
public String getVdef10 () {
return this.vdef10;
 } 

/** 
* �����Զ�����10
*
* @param vdef10 �Զ�����10
*/
public void setVdef10 ( String vdef10) {
this.vdef10=vdef10;
 } 

/** 
* ��ȡ�Զ�����11
*
* @return �Զ�����11
*/
public String getVdef11 () {
return this.vdef11;
 } 

/** 
* �����Զ�����11
*
* @param vdef11 �Զ�����11
*/
public void setVdef11 ( String vdef11) {
this.vdef11=vdef11;
 } 

/** 
* ��ȡ�Զ�����12
*
* @return �Զ�����12
*/
public String getVdef12 () {
return this.vdef12;
 } 

/** 
* �����Զ�����12
*
* @param vdef12 �Զ�����12
*/
public void setVdef12 ( String vdef12) {
this.vdef12=vdef12;
 } 

/** 
* ��ȡ�Զ�����13
*
* @return �Զ�����13
*/
public String getVdef13 () {
return this.vdef13;
 } 

/** 
* �����Զ�����13
*
* @param vdef13 �Զ�����13
*/
public void setVdef13 ( String vdef13) {
this.vdef13=vdef13;
 } 

/** 
* ��ȡ�Զ�����14
*
* @return �Զ�����14
*/
public String getVdef14 () {
return this.vdef14;
 } 

/** 
* �����Զ�����14
*
* @param vdef14 �Զ�����14
*/
public void setVdef14 ( String vdef14) {
this.vdef14=vdef14;
 } 

/** 
* ��ȡ�Զ�����15
*
* @return �Զ�����15
*/
public String getVdef15 () {
return this.vdef15;
 } 

/** 
* �����Զ�����15
*
* @param vdef15 �Զ�����15
*/
public void setVdef15 ( String vdef15) {
this.vdef15=vdef15;
 } 

/** 
* ��ȡ�Զ�����16
*
* @return �Զ�����16
*/
public String getVdef16 () {
return this.vdef16;
 } 

/** 
* �����Զ�����16
*
* @param vdef16 �Զ�����16
*/
public void setVdef16 ( String vdef16) {
this.vdef16=vdef16;
 } 

/** 
* ��ȡ�Զ�����17
*
* @return �Զ�����17
*/
public String getVdef17 () {
return this.vdef17;
 } 

/** 
* �����Զ�����17
*
* @param vdef17 �Զ�����17
*/
public void setVdef17 ( String vdef17) {
this.vdef17=vdef17;
 } 

/** 
* ��ȡ�Զ�����18
*
* @return �Զ�����18
*/
public String getVdef18 () {
return this.vdef18;
 } 

/** 
* �����Զ�����18
*
* @param vdef18 �Զ�����18
*/
public void setVdef18 ( String vdef18) {
this.vdef18=vdef18;
 } 

/** 
* ��ȡ�Զ�����19
*
* @return �Զ�����19
*/
public String getVdef19 () {
return this.vdef19;
 } 

/** 
* �����Զ�����19
*
* @param vdef19 �Զ�����19
*/
public void setVdef19 ( String vdef19) {
this.vdef19=vdef19;
 } 

/** 
* ��ȡ�Զ�����2
*
* @return �Զ�����2
*/
public String getVdef2 () {
return this.vdef2;
 } 

/** 
* �����Զ�����2
*
* @param vdef2 �Զ�����2
*/
public void setVdef2 ( String vdef2) {
this.vdef2=vdef2;
 } 

/** 
* ��ȡ�Զ�����20
*
* @return �Զ�����20
*/
public String getVdef20 () {
return this.vdef20;
 } 

/** 
* �����Զ�����20
*
* @param vdef20 �Զ�����20
*/
public void setVdef20 ( String vdef20) {
this.vdef20=vdef20;
 } 

/** 
* ��ȡ�Զ�����3
*
* @return �Զ�����3
*/
public String getVdef3 () {
return this.vdef3;
 } 

/** 
* �����Զ�����3
*
* @param vdef3 �Զ�����3
*/
public void setVdef3 ( String vdef3) {
this.vdef3=vdef3;
 } 

/** 
* ��ȡ�Զ�����4
*
* @return �Զ�����4
*/
public String getVdef4 () {
return this.vdef4;
 } 

/** 
* �����Զ�����4
*
* @param vdef4 �Զ�����4
*/
public void setVdef4 ( String vdef4) {
this.vdef4=vdef4;
 } 

/** 
* ��ȡ�Զ�����5
*
* @return �Զ�����5
*/
public String getVdef5 () {
return this.vdef5;
 } 

/** 
* �����Զ�����5
*
* @param vdef5 �Զ�����5
*/
public void setVdef5 ( String vdef5) {
this.vdef5=vdef5;
 } 

/** 
* ��ȡ�Զ�����6
*
* @return �Զ�����6
*/
public String getVdef6 () {
return this.vdef6;
 } 

/** 
* �����Զ�����6
*
* @param vdef6 �Զ�����6
*/
public void setVdef6 ( String vdef6) {
this.vdef6=vdef6;
 } 

/** 
* ��ȡ�Զ�����7
*
* @return �Զ�����7
*/
public String getVdef7 () {
return this.vdef7;
 } 

/** 
* �����Զ�����7
*
* @param vdef7 �Զ�����7
*/
public void setVdef7 ( String vdef7) {
this.vdef7=vdef7;
 } 

/** 
* ��ȡ�Զ�����8
*
* @return �Զ�����8
*/
public String getVdef8 () {
return this.vdef8;
 } 

/** 
* �����Զ�����8
*
* @param vdef8 �Զ�����8
*/
public void setVdef8 ( String vdef8) {
this.vdef8=vdef8;
 } 

/** 
* ��ȡ�Զ�����9
*
* @return �Զ�����9
*/
public String getVdef9 () {
return this.vdef9;
 } 

/** 
* �����Զ�����9
*
* @param vdef9 �Զ�����9
*/
public void setVdef9 ( String vdef9) {
this.vdef9=vdef9;
 } 


  @Override
  public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta("hrwa.WaBaSchHVO");
  }
}