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
*项目编码
*/
public String code;
/**
*创建时间
*/
public UFDateTime creationtime;
/**
*创建人
*/
public String creator;
/**
*取数类型
*/
public Integer datatype;
/**
*修改时间
*/
public UFDateTime modifiedtime;
/**
*修改人
*/
public String modifier;
/**
*项目名称
*/
public String name;
/**
*项目主键
*/
public String pk_ba_item;
/**
*集团
*/
public String pk_group;
/**
*组织
*/
public String pk_org;
/**
*组织多版本
*/
public String pk_org_v;
/**
*时间戳
*/
public UFDateTime ts;
/**
*手动输入
*/
public UFDouble value;
/**
*自定义项1
*/
public String vdef1;
/**
*自定义项10
*/
public String vdef10;
/**
*自定义项11
*/
public String vdef11;
/**
*自定义项12
*/
public String vdef12;
/**
*自定义项13
*/
public String vdef13;
/**
*自定义项14
*/
public String vdef14;
/**
*自定义项15
*/
public String vdef15;
/**
*自定义项16
*/
public String vdef16;
/**
*自定义项17
*/
public String vdef17;
/**
*自定义项18
*/
public String vdef18;
/**
*自定义项19
*/
public String vdef19;
/**
*自定义项2
*/
public String vdef2;
/**
*自定义项20
*/
public String vdef20;
/**
*自定义项3
*/
public String vdef3;
/**
*自定义项4
*/
public String vdef4;
/**
*自定义项5
*/
public String vdef5;
/**
*自定义项6
*/
public String vdef6;
/**
*自定义项7
*/
public String vdef7;
/**
*自定义项8
*/
public String vdef8;
/**
*自定义项9
*/
public String vdef9;
/**
*计算公式
*/
public String vformula;
/**
*外部数据源
*/
public String vformulastr;


private Integer iitemtype;
private Integer ifromflag;

/** 
* 获取项目编码
*
* @return 项目编码
*/
public String getCode () {
return this.code;
 } 

/** 
* 设置项目编码
*
* @param code 项目编码
*/
public void setCode ( String code) {
this.code=code;
 } 

/** 
* 获取创建时间
*
* @return 创建时间
*/
public UFDateTime getCreationtime () {
return this.creationtime;
 } 

/** 
* 设置创建时间
*
* @param creationtime 创建时间
*/
public void setCreationtime ( UFDateTime creationtime) {
this.creationtime=creationtime;
 } 

/** 
* 获取创建人
*
* @return 创建人
*/
public String getCreator () {
return this.creator;
 } 

/** 
* 设置创建人
*
* @param creator 创建人
*/
public void setCreator ( String creator) {
this.creator=creator;
 } 

/** 
* 获取取数类型
*
* @return 取数类型
* @see String
*/
public Integer getDatatype () {
return this.datatype;
 } 

/** 
* 设置取数类型
*
* @param datatype 取数类型
* @see String
*/
public void setDatatype ( Integer datatype) {
this.datatype=datatype;
 } 

/** 
* 获取修改时间
*
* @return 修改时间
*/
public UFDateTime getModifiedtime () {
return this.modifiedtime;
 } 

/** 
* 设置修改时间
*
* @param modifiedtime 修改时间
*/
public void setModifiedtime ( UFDateTime modifiedtime) {
this.modifiedtime=modifiedtime;
 } 

/** 
* 获取修改人
*
* @return 修改人
*/
public String getModifier () {
return this.modifier;
 } 

/** 
* 设置修改人
*
* @param modifier 修改人
*/
public void setModifier ( String modifier) {
this.modifier=modifier;
 } 

/** 
* 获取项目名称
*
* @return 项目名称
*/
public String getName () {
return this.name;
 } 

/** 
* 设置项目名称
*
* @param name 项目名称
*/
public void setName ( String name) {
this.name=name;
 } 

/** 
* 获取项目主键
*
* @return 项目主键
*/
public String getPk_ba_item () {
return this.pk_ba_item;
 } 

/** 
* 设置项目主键
*
* @param pk_ba_item 项目主键
*/
public void setPk_ba_item ( String pk_ba_item) {
this.pk_ba_item=pk_ba_item;
 } 

/** 
* 获取集团
*
* @return 集团
*/
public String getPk_group () {
return this.pk_group;
 } 

/** 
* 设置集团
*
* @param pk_group 集团
*/
public void setPk_group ( String pk_group) {
this.pk_group=pk_group;
 } 

/** 
* 获取组织
*
* @return 组织
*/
public String getPk_org () {
return this.pk_org;
 } 

/** 
* 设置组织
*
* @param pk_org 组织
*/
public void setPk_org ( String pk_org) {
this.pk_org=pk_org;
 } 

/** 
* 获取组织多版本
*
* @return 组织多版本
*/
public String getPk_org_v () {
return this.pk_org_v;
 } 

/** 
* 设置组织多版本
*
* @param pk_org_v 组织多版本
*/
public void setPk_org_v ( String pk_org_v) {
this.pk_org_v=pk_org_v;
 } 

/** 
* 获取时间戳
*
* @return 时间戳
*/
public UFDateTime getTs () {
return this.ts;
 } 

/** 
* 设置时间戳
*
* @param ts 时间戳
*/
public void setTs ( UFDateTime ts) {
this.ts=ts;
 } 

/** 
* 获取手动输入
*
* @return 手动输入
*/
public UFDouble getValue () {
return this.value;
 } 

/** 
* 设置手动输入
*
* @param value 手动输入
*/
public void setValue ( UFDouble value) {
this.value=value;
 } 

/** 
* 获取自定义项1
*
* @return 自定义项1
*/
public String getVdef1 () {
return this.vdef1;
 } 

/** 
* 设置自定义项1
*
* @param vdef1 自定义项1
*/
public void setVdef1 ( String vdef1) {
this.vdef1=vdef1;
 } 

/** 
* 获取自定义项10
*
* @return 自定义项10
*/
public String getVdef10 () {
return this.vdef10;
 } 

/** 
* 设置自定义项10
*
* @param vdef10 自定义项10
*/
public void setVdef10 ( String vdef10) {
this.vdef10=vdef10;
 } 

/** 
* 获取自定义项11
*
* @return 自定义项11
*/
public String getVdef11 () {
return this.vdef11;
 } 

/** 
* 设置自定义项11
*
* @param vdef11 自定义项11
*/
public void setVdef11 ( String vdef11) {
this.vdef11=vdef11;
 } 

/** 
* 获取自定义项12
*
* @return 自定义项12
*/
public String getVdef12 () {
return this.vdef12;
 } 

/** 
* 设置自定义项12
*
* @param vdef12 自定义项12
*/
public void setVdef12 ( String vdef12) {
this.vdef12=vdef12;
 } 

/** 
* 获取自定义项13
*
* @return 自定义项13
*/
public String getVdef13 () {
return this.vdef13;
 } 

/** 
* 设置自定义项13
*
* @param vdef13 自定义项13
*/
public void setVdef13 ( String vdef13) {
this.vdef13=vdef13;
 } 

/** 
* 获取自定义项14
*
* @return 自定义项14
*/
public String getVdef14 () {
return this.vdef14;
 } 

/** 
* 设置自定义项14
*
* @param vdef14 自定义项14
*/
public void setVdef14 ( String vdef14) {
this.vdef14=vdef14;
 } 

/** 
* 获取自定义项15
*
* @return 自定义项15
*/
public String getVdef15 () {
return this.vdef15;
 } 

/** 
* 设置自定义项15
*
* @param vdef15 自定义项15
*/
public void setVdef15 ( String vdef15) {
this.vdef15=vdef15;
 } 

/** 
* 获取自定义项16
*
* @return 自定义项16
*/
public String getVdef16 () {
return this.vdef16;
 } 

/** 
* 设置自定义项16
*
* @param vdef16 自定义项16
*/
public void setVdef16 ( String vdef16) {
this.vdef16=vdef16;
 } 

/** 
* 获取自定义项17
*
* @return 自定义项17
*/
public String getVdef17 () {
return this.vdef17;
 } 

/** 
* 设置自定义项17
*
* @param vdef17 自定义项17
*/
public void setVdef17 ( String vdef17) {
this.vdef17=vdef17;
 } 

/** 
* 获取自定义项18
*
* @return 自定义项18
*/
public String getVdef18 () {
return this.vdef18;
 } 

/** 
* 设置自定义项18
*
* @param vdef18 自定义项18
*/
public void setVdef18 ( String vdef18) {
this.vdef18=vdef18;
 } 

/** 
* 获取自定义项19
*
* @return 自定义项19
*/
public String getVdef19 () {
return this.vdef19;
 } 

/** 
* 设置自定义项19
*
* @param vdef19 自定义项19
*/
public void setVdef19 ( String vdef19) {
this.vdef19=vdef19;
 } 

/** 
* 获取自定义项2
*
* @return 自定义项2
*/
public String getVdef2 () {
return this.vdef2;
 } 

/** 
* 设置自定义项2
*
* @param vdef2 自定义项2
*/
public void setVdef2 ( String vdef2) {
this.vdef2=vdef2;
 } 

/** 
* 获取自定义项20
*
* @return 自定义项20
*/
public String getVdef20 () {
return this.vdef20;
 } 

/** 
* 设置自定义项20
*
* @param vdef20 自定义项20
*/
public void setVdef20 ( String vdef20) {
this.vdef20=vdef20;
 } 

/** 
* 获取自定义项3
*
* @return 自定义项3
*/
public String getVdef3 () {
return this.vdef3;
 } 

/** 
* 设置自定义项3
*
* @param vdef3 自定义项3
*/
public void setVdef3 ( String vdef3) {
this.vdef3=vdef3;
 } 

/** 
* 获取自定义项4
*
* @return 自定义项4
*/
public String getVdef4 () {
return this.vdef4;
 } 

/** 
* 设置自定义项4
*
* @param vdef4 自定义项4
*/
public void setVdef4 ( String vdef4) {
this.vdef4=vdef4;
 } 

/** 
* 获取自定义项5
*
* @return 自定义项5
*/
public String getVdef5 () {
return this.vdef5;
 } 

/** 
* 设置自定义项5
*
* @param vdef5 自定义项5
*/
public void setVdef5 ( String vdef5) {
this.vdef5=vdef5;
 } 

/** 
* 获取自定义项6
*
* @return 自定义项6
*/
public String getVdef6 () {
return this.vdef6;
 } 

/** 
* 设置自定义项6
*
* @param vdef6 自定义项6
*/
public void setVdef6 ( String vdef6) {
this.vdef6=vdef6;
 } 

/** 
* 获取自定义项7
*
* @return 自定义项7
*/
public String getVdef7 () {
return this.vdef7;
 } 

/** 
* 设置自定义项7
*
* @param vdef7 自定义项7
*/
public void setVdef7 ( String vdef7) {
this.vdef7=vdef7;
 } 

/** 
* 获取自定义项8
*
* @return 自定义项8
*/
public String getVdef8 () {
return this.vdef8;
 } 

/** 
* 设置自定义项8
*
* @param vdef8 自定义项8
*/
public void setVdef8 ( String vdef8) {
this.vdef8=vdef8;
 } 

/** 
* 获取自定义项9
*
* @return 自定义项9
*/
public String getVdef9 () {
return this.vdef9;
 } 

/** 
* 设置自定义项9
*
* @param vdef9 自定义项9
*/
public void setVdef9 ( String vdef9) {
this.vdef9=vdef9;
 } 

/** 
* 获取计算公式
*
* @return 计算公式
*/
public String getVformula () {
return this.vformula;
 } 

/** 
* 设置计算公式
*
* @param vformula 计算公式
*/
public void setVformula ( String vformula) {
this.vformula=vformula;
 } 

/** 
* 获取外部数据源
*
* @return 外部数据源
*/
public String getVformulastr () {
return this.vformulastr;
 } 

/** 
* 设置外部数据源
*
* @param vformulastr 外部数据源
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
 * @param iitemtype 要设置的 iitemtype
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
 * @param ifromflag 要设置的 ifromflag
 */
public void setIfromflag(Integer ifromflag) {
	this.ifromflag = ifromflag;
}


@Override
public Object getAttributeValue(String key) {
	// TODO 自动生成的方法存根
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
	// TODO 自动生成的方法存根
	
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