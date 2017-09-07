package nc.vo.wa.wa_ba.item;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

public class ItemsVO extends SuperVO {
/**
*��Ŀ����
*/
public String code;
/**
*����ʱ��
*/
public UFDateTime creationtime;
/**
*������
*/
public String creator;
/**
*ȡ������
*/
public Integer datatype;
/**
*�޸�ʱ��
*/
public UFDateTime modifiedtime;
/**
*�޸���
*/
public String modifier;
/**
*��Ŀ����
*/
public String name;
/**
*��Ŀ����
*/
public String pk_ba_item;
/**
*����
*/
public String pk_group;
/**
*��֯
*/
public String pk_org;
/**
*��֯��汾
*/
public String pk_org_v;
/**
*ʱ���
*/
public UFDateTime ts;
/**
*�ֶ�����
*/
public UFDouble value;
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
*���㹫ʽ
*/
public String vformula;
/**
*�ⲿ����Դ
*/
public String vformulastr;


private Integer iitemtype;
private Integer ifromflag;

/** 
* ��ȡ��Ŀ����
*
* @return ��Ŀ����
*/
public String getCode () {
return this.code;
 } 

/** 
* ������Ŀ����
*
* @param code ��Ŀ����
*/
public void setCode ( String code) {
this.code=code;
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
* ��ȡȡ������
*
* @return ȡ������
* @see String
*/
public Integer getDatatype () {
return this.datatype;
 } 

/** 
* ����ȡ������
*
* @param datatype ȡ������
* @see String
*/
public void setDatatype ( Integer datatype) {
this.datatype=datatype;
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
* ��ȡ��Ŀ����
*
* @return ��Ŀ����
*/
public String getName () {
return this.name;
 } 

/** 
* ������Ŀ����
*
* @param name ��Ŀ����
*/
public void setName ( String name) {
this.name=name;
 } 

/** 
* ��ȡ��Ŀ����
*
* @return ��Ŀ����
*/
public String getPk_ba_item () {
return this.pk_ba_item;
 } 

/** 
* ������Ŀ����
*
* @param pk_ba_item ��Ŀ����
*/
public void setPk_ba_item ( String pk_ba_item) {
this.pk_ba_item=pk_ba_item;
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
* ��ȡ��֯��汾
*
* @return ��֯��汾
*/
public String getPk_org_v () {
return this.pk_org_v;
 } 

/** 
* ������֯��汾
*
* @param pk_org_v ��֯��汾
*/
public void setPk_org_v ( String pk_org_v) {
this.pk_org_v=pk_org_v;
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
* ��ȡ�ֶ�����
*
* @return �ֶ�����
*/
public UFDouble getValue () {
return this.value;
 } 

/** 
* �����ֶ�����
*
* @param value �ֶ�����
*/
public void setValue ( UFDouble value) {
this.value=value;
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

/** 
* ��ȡ���㹫ʽ
*
* @return ���㹫ʽ
*/
public String getVformula () {
return this.vformula;
 } 

/** 
* ���ü��㹫ʽ
*
* @param vformula ���㹫ʽ
*/
public void setVformula ( String vformula) {
this.vformula=vformula;
 } 

/** 
* ��ȡ�ⲿ����Դ
*
* @return �ⲿ����Դ
*/
public String getVformulastr () {
return this.vformulastr;
 } 

/** 
* �����ⲿ����Դ
*
* @param vformulastr �ⲿ����Դ
*/
public void setVformulastr ( String vformulastr) {
	this.vformulastr=vformulastr;
 } 


  @Override
  public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta("hrwa.wa_ba_item");
  }

/**
 * @return iitemtype
 */
public Integer getIitemtype() {
	return this.datatype;
}

/**
 * @param iitemtype Ҫ���õ� iitemtype
 */
public void setIitemtype(Integer iitemtype) {
	this.iitemtype = iitemtype;
}

/**
 * @return ifromflag
 */
public Integer getIfromflag() {
	return this.datatype;
}

/**
 * @param ifromflag Ҫ���õ� ifromflag
 */
public void setIfromflag(Integer ifromflag) {
	this.ifromflag = ifromflag;
}


@Override
public Object getAttributeValue(String key) {
	// TODO �Զ����ɵķ������
	if(key.equals("iitemtype")||key.equals("ifromflag")){
		if(super.getAttributeValue("datatype")!=null){
			return super.getAttributeValue("datatype");
		}else{
			return 0;
		}
		
	}	
	return super.getAttributeValue(key);
}


@Override
public void setAttributeValue(String name, Object value) {
	// TODO �Զ����ɵķ������
	
	if(name.equals("datatype")){
		
		if(value instanceof ItemsVO){
			
		}else{
			super.setAttributeValue(name, value);
		}
		
		System.out.println(value);
		
	}else{
		super.setAttributeValue(name, value);

	}
	
	
}
}