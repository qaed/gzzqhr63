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
	 * 考核系数
	 */
	public UFDouble comput_factor;
	/**
	 * 计划绩效工资总额
	 */
	public UFDouble comput_totalmoney;
	/**
	 * 期间1
	 */
	public String cperiod1;
	/**
	 * 期间2
	 */
	public String cperiod2;
	/**
	 * 期间3
	 */
	public String cperiod3;
	/**
	 * 期间4
	 */
	public String cperiod4;
	/**
	 * 期间5
	 */
	public String cperiod5;
	/**
	 * 年度1
	 */
	public String cyear1;
	/**
	 * 年度2
	 */
	public String cyear2;
	/**
	 * 年度3
	 */
	public String cyear3;
	/**
	 * 年度4
	 */
	public String cyear4;
	/**
	 * 年度5
	 */
	public String cyear5;
	/**
	 * 绩效系数
	 */
	public UFDouble f_1;
	/**
	 * 标准绩效工资
	 */
	public UFDouble f_10;
	/**
	 * 标准职位薪酬
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
	 * 备注1
	 */
	public String memo1;
	/**
	 * 备注2
	 */
	public String memo2;
	/**
	 * 备注3
	 */
	public String memo3;
	/**
	 * 备注4
	 */
	public String memo4;
	/**
	 * 备注5
	 */
	public String memo5;
	/**
	 * 奖金分配单据pk
	 */
	public String pk_ba_allocbill;
	/**
	 * 主表主键
	 */
	public String pk_ba_sch_h;
	/**
	 * 主键
	 */
	public String pk_ba_sch_psn;
	/**
	 * 上层单据主键
	 */
	public String pk_ba_sch_unit;
	/**
	 * 部门
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
	 * 职务
	 */
	public String pk_om_duty;
	/**
	 * 岗位
	 */
	public String pk_om_job;
	/**
	 * pk_psnbasdoc
	 */
	public String pk_psnbasdoc;
	/**
	 * 人员
	 */
	public String pk_psndoc;
	/**
	 * 分配单元子表PK
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
	 * 计划金额1
	 */
	public UFDouble pvalue1;
	/**
	 * 计划金额2
	 */
	public UFDouble pvalue2;
	/**
	 * 计划金额3
	 */
	public UFDouble pvalue3;
	/**
	 * 计划金额4
	 */
	public UFDouble pvalue4;
	/**
	 * 计划金额5
	 */
	public UFDouble pvalue5;
	/**
	 * 修订后考核系数
	 */
	public UFDouble revise_factor;
	/**
	 * 修订后绩效工资总额
	 */
	public UFDouble revise_totalmoney;
	/**
	 * 时间戳
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
	 * 奖金项目1
	 */
	public String waitem1;
	/**
	 * 奖金项目2
	 */
	public String waitem2;
	/**
	 * 奖金项目3
	 */
	public String waitem3;
	/**
	 * 奖金项目4
	 */
	public String waitem4;
	/**
	 * 奖金项目5
	 */
	public String waitem5;
	/**
	 * 人员工作记录
	 */
	public String pk_psnjob;
	/**
	 * dr值
	 */
	public Integer dr;

	/**
	 * 获取class1
	 * 
	 * @return class1
	 */
	public String getClass1() {
		return this.class1;
	}

	/**
	 * 设置class1
	 * 
	 * @param class1 class1
	 */
	public void setClass1(String class1) {
		this.class1 = class1;
	}

	/**
	 * 获取class2
	 * 
	 * @return class2
	 */
	public String getClass2() {
		return this.class2;
	}

	/**
	 * 设置class2
	 * 
	 * @param class2 class2
	 */
	public void setClass2(String class2) {
		this.class2 = class2;
	}

	/**
	 * 获取class3
	 * 
	 * @return class3
	 */
	public String getClass3() {
		return this.class3;
	}

	/**
	 * 设置class3
	 * 
	 * @param class3 class3
	 */
	public void setClass3(String class3) {
		this.class3 = class3;
	}

	/**
	 * 获取class4
	 * 
	 * @return class4
	 */
	public String getClass4() {
		return this.class4;
	}

	/**
	 * 设置class4
	 * 
	 * @param class4 class4
	 */
	public void setClass4(String class4) {
		this.class4 = class4;
	}

	/**
	 * 获取class5
	 * 
	 * @return class5
	 */
	public String getClass5() {
		return this.class5;
	}

	/**
	 * 设置class5
	 * 
	 * @param class5 class5
	 */
	public void setClass5(String class5) {
		this.class5 = class5;
	}

	/**
	 * 获取考核系数
	 * 
	 * @return 考核系数
	 */
	public UFDouble getComput_factor() {
		return this.comput_factor;
	}

	/**
	 * 设置考核系数
	 * 
	 * @param comput_factor 考核系数
	 */
	public void setComput_factor(UFDouble comput_factor) {
		this.comput_factor = comput_factor;
	}

	/**
	 * 获取计划绩效工资总额
	 * 
	 * @return 计划绩效工资总额
	 */
	public UFDouble getComput_totalmoney() {
		return this.comput_totalmoney;
	}

	/**
	 * 设置计划绩效工资总额
	 * 
	 * @param comput_totalmoney 计划绩效工资总额
	 */
	public void setComput_totalmoney(UFDouble comput_totalmoney) {
		this.comput_totalmoney = comput_totalmoney;
	}

	/**
	 * 获取期间1
	 * 
	 * @return 期间1
	 */
	public String getCperiod1() {
		return this.cperiod1;
	}

	/**
	 * 设置期间1
	 * 
	 * @param cperiod1 期间1
	 */
	public void setCperiod1(String cperiod1) {
		this.cperiod1 = cperiod1;
	}

	/**
	 * 获取期间2
	 * 
	 * @return 期间2
	 */
	public String getCperiod2() {
		return this.cperiod2;
	}

	/**
	 * 设置期间2
	 * 
	 * @param cperiod2 期间2
	 */
	public void setCperiod2(String cperiod2) {
		this.cperiod2 = cperiod2;
	}

	/**
	 * 获取期间3
	 * 
	 * @return 期间3
	 */
	public String getCperiod3() {
		return this.cperiod3;
	}

	/**
	 * 设置期间3
	 * 
	 * @param cperiod3 期间3
	 */
	public void setCperiod3(String cperiod3) {
		this.cperiod3 = cperiod3;
	}

	/**
	 * 获取期间4
	 * 
	 * @return 期间4
	 */
	public String getCperiod4() {
		return this.cperiod4;
	}

	/**
	 * 设置期间4
	 * 
	 * @param cperiod4 期间4
	 */
	public void setCperiod4(String cperiod4) {
		this.cperiod4 = cperiod4;
	}

	/**
	 * 获取期间5
	 * 
	 * @return 期间5
	 */
	public String getCperiod5() {
		return this.cperiod5;
	}

	/**
	 * 设置期间5
	 * 
	 * @param cperiod5 期间5
	 */
	public void setCperiod5(String cperiod5) {
		this.cperiod5 = cperiod5;
	}

	/**
	 * 获取年度1
	 * 
	 * @return 年度1
	 */
	public String getCyear1() {
		return this.cyear1;
	}

	/**
	 * 设置年度1
	 * 
	 * @param cyear1 年度1
	 */
	public void setCyear1(String cyear1) {
		this.cyear1 = cyear1;
	}

	/**
	 * 获取年度2
	 * 
	 * @return 年度2
	 */
	public String getCyear2() {
		return this.cyear2;
	}

	/**
	 * 设置年度2
	 * 
	 * @param cyear2 年度2
	 */
	public void setCyear2(String cyear2) {
		this.cyear2 = cyear2;
	}

	/**
	 * 获取年度3
	 * 
	 * @return 年度3
	 */
	public String getCyear3() {
		return this.cyear3;
	}

	/**
	 * 设置年度3
	 * 
	 * @param cyear3 年度3
	 */
	public void setCyear3(String cyear3) {
		this.cyear3 = cyear3;
	}

	/**
	 * 获取年度4
	 * 
	 * @return 年度4
	 */
	public String getCyear4() {
		return this.cyear4;
	}

	/**
	 * 设置年度4
	 * 
	 * @param cyear4 年度4
	 */
	public void setCyear4(String cyear4) {
		this.cyear4 = cyear4;
	}

	/**
	 * 获取年度5
	 * 
	 * @return 年度5
	 */
	public String getCyear5() {
		return this.cyear5;
	}

	/**
	 * 设置年度5
	 * 
	 * @param cyear5 年度5
	 */
	public void setCyear5(String cyear5) {
		this.cyear5 = cyear5;
	}

	/**
	 * 获取绩效系数
	 * 
	 * @return 绩效系数
	 */
	public UFDouble getF_1() {
		return this.f_1;
	}

	/**
	 * 设置绩效系数
	 * 
	 * @param f_1 绩效系数
	 */
	public void setF_1(UFDouble f_1) {
		this.f_1 = f_1;
	}

	/**
	 * 获取标准绩效工资
	 * 
	 * @return 标准绩效工资
	 */
	public UFDouble getF_10() {
		return this.f_10;
	}

	/**
	 * 设置标准绩效工资
	 * 
	 * @param f_10 标准绩效工资
	 */
	public void setF_10(UFDouble f_10) {
		this.f_10 = f_10;
	}

	/**
	 * 获取标准职位薪酬
	 * 
	 * @return 标准职位薪酬
	 */
	public UFDouble getF_2() {
		return this.f_2;
	}

	/**
	 * 设置标准职位薪酬
	 * 
	 * @param f_2 标准职位薪酬
	 */
	public void setF_2(UFDouble f_2) {
		this.f_2 = f_2;
	}

	/**
	 * 获取f_3
	 * 
	 * @return f_3
	 */
	public UFDouble getF_3() {
		return this.f_3;
	}

	/**
	 * 设置f_3
	 * 
	 * @param f_3 f_3
	 */
	public void setF_3(UFDouble f_3) {
		this.f_3 = f_3;
	}

	/**
	 * 获取f_4
	 * 
	 * @return f_4
	 */
	public UFDouble getF_4() {
		return this.f_4;
	}

	/**
	 * 设置f_4
	 * 
	 * @param f_4 f_4
	 */
	public void setF_4(UFDouble f_4) {
		this.f_4 = f_4;
	}

	/**
	 * 获取f_5
	 * 
	 * @return f_5
	 */
	public UFDouble getF_5() {
		return this.f_5;
	}

	/**
	 * 设置f_5
	 * 
	 * @param f_5 f_5
	 */
	public void setF_5(UFDouble f_5) {
		this.f_5 = f_5;
	}

	/**
	 * 获取f_6
	 * 
	 * @return f_6
	 */
	public UFDouble getF_6() {
		return this.f_6;
	}

	/**
	 * 设置f_6
	 * 
	 * @param f_6 f_6
	 */
	public void setF_6(UFDouble f_6) {
		this.f_6 = f_6;
	}

	/**
	 * 获取f_7
	 * 
	 * @return f_7
	 */
	public UFDouble getF_7() {
		return this.f_7;
	}

	/**
	 * 设置f_7
	 * 
	 * @param f_7 f_7
	 */
	public void setF_7(UFDouble f_7) {
		this.f_7 = f_7;
	}

	/**
	 * 获取f_8
	 * 
	 * @return f_8
	 */
	public UFDouble getF_8() {
		return this.f_8;
	}

	/**
	 * 设置f_8
	 * 
	 * @param f_8 f_8
	 */
	public void setF_8(UFDouble f_8) {
		this.f_8 = f_8;
	}

	/**
	 * 获取f_9
	 * 
	 * @return f_9
	 */
	public UFDouble getF_9() {
		return this.f_9;
	}

	/**
	 * 设置f_9
	 * 
	 * @param f_9 f_9
	 */
	public void setF_9(UFDouble f_9) {
		this.f_9 = f_9;
	}

	/**
	 * 获取item1
	 * 
	 * @return item1
	 */
	public String getItem1() {
		return this.item1;
	}

	/**
	 * 设置item1
	 * 
	 * @param item1 item1
	 */
	public void setItem1(String item1) {
		this.item1 = item1;
	}

	/**
	 * 获取item2
	 * 
	 * @return item2
	 */
	public String getItem2() {
		return this.item2;
	}

	/**
	 * 设置item2
	 * 
	 * @param item2 item2
	 */
	public void setItem2(String item2) {
		this.item2 = item2;
	}

	/**
	 * 获取item3
	 * 
	 * @return item3
	 */
	public String getItem3() {
		return this.item3;
	}

	/**
	 * 设置item3
	 * 
	 * @param item3 item3
	 */
	public void setItem3(String item3) {
		this.item3 = item3;
	}

	/**
	 * 获取item4
	 * 
	 * @return item4
	 */
	public String getItem4() {
		return this.item4;
	}

	/**
	 * 设置item4
	 * 
	 * @param item4 item4
	 */
	public void setItem4(String item4) {
		this.item4 = item4;
	}

	/**
	 * 获取item5
	 * 
	 * @return item5
	 */
	public String getItem5() {
		return this.item5;
	}

	/**
	 * 设置item5
	 * 
	 * @param item5 item5
	 */
	public void setItem5(String item5) {
		this.item5 = item5;
	}

	/**
	 * 获取备注1
	 * 
	 * @return 备注1
	 */
	public String getMemo1() {
		return this.memo1;
	}

	/**
	 * 设置备注1
	 * 
	 * @param memo1 备注1
	 */
	public void setMemo1(String memo1) {
		this.memo1 = memo1;
	}

	/**
	 * 获取备注2
	 * 
	 * @return 备注2
	 */
	public String getMemo2() {
		return this.memo2;
	}

	/**
	 * 设置备注2
	 * 
	 * @param memo2 备注2
	 */
	public void setMemo2(String memo2) {
		this.memo2 = memo2;
	}

	/**
	 * 获取备注3
	 * 
	 * @return 备注3
	 */
	public String getMemo3() {
		return this.memo3;
	}

	/**
	 * 设置备注3
	 * 
	 * @param memo3 备注3
	 */
	public void setMemo3(String memo3) {
		this.memo3 = memo3;
	}

	/**
	 * 获取备注4
	 * 
	 * @return 备注4
	 */
	public String getMemo4() {
		return this.memo4;
	}

	/**
	 * 设置备注4
	 * 
	 * @param memo4 备注4
	 */
	public void setMemo4(String memo4) {
		this.memo4 = memo4;
	}

	/**
	 * 获取备注5
	 * 
	 * @return 备注5
	 */
	public String getMemo5() {
		return this.memo5;
	}

	/**
	 * 设置备注5
	 * 
	 * @param memo5 备注5
	 */
	public void setMemo5(String memo5) {
		this.memo5 = memo5;
	}

	/**
	 * 获取奖金分配单据pk
	 * 
	 * @return 奖金分配单据pk
	 */
	public String getPk_ba_allocbill() {
		return this.pk_ba_allocbill;
	}

	/**
	 * 设置奖金分配单据pk
	 * 
	 * @param pk_ba_allocbill 奖金分配单据pk
	 */
	public void setPk_ba_allocbill(String pk_ba_allocbill) {
		this.pk_ba_allocbill = pk_ba_allocbill;
	}

	/**
	 * 获取主表主键
	 * 
	 * @return 主表主键
	 */
	public String getPk_ba_sch_h() {
		return this.pk_ba_sch_h;
	}

	/**
	 * 设置主表主键
	 * 
	 * @param pk_ba_sch_h 主表主键
	 */
	public void setPk_ba_sch_h(String pk_ba_sch_h) {
		this.pk_ba_sch_h = pk_ba_sch_h;
	}

	/**
	 * 获取主键
	 * 
	 * @return 主键
	 */
	public String getPk_ba_sch_psn() {
		return this.pk_ba_sch_psn;
	}

	/**
	 * 设置主键
	 * 
	 * @param pk_ba_sch_psn 主键
	 */
	public void setPk_ba_sch_psn(String pk_ba_sch_psn) {
		this.pk_ba_sch_psn = pk_ba_sch_psn;
	}

	/**
	 * 获取上层单据主键
	 * 
	 * @return 上层单据主键
	 */
	public String getPk_ba_sch_unit() {
		return this.pk_ba_sch_unit;
	}

	/**
	 * 设置上层单据主键
	 * 
	 * @param pk_ba_sch_unit 上层单据主键
	 */
	public void setPk_ba_sch_unit(String pk_ba_sch_unit) {
		this.pk_ba_sch_unit = pk_ba_sch_unit;
	}

	/**
	 * 获取部门
	 * 
	 * @return 部门
	 */
	public String getPk_deptdoc() {
		return this.pk_deptdoc;
	}

	/**
	 * 设置部门
	 * 
	 * @param pk_deptdoc 部门
	 */
	public void setPk_deptdoc(String pk_deptdoc) {
		this.pk_deptdoc = pk_deptdoc;
	}

	/**
	 * 获取pk_dutyrank
	 * 
	 * @return pk_dutyrank
	 */
	public String getPk_dutyrank() {
		return this.pk_dutyrank;
	}

	/**
	 * 设置pk_dutyrank
	 * 
	 * @param pk_dutyrank pk_dutyrank
	 */
	public void setPk_dutyrank(String pk_dutyrank) {
		this.pk_dutyrank = pk_dutyrank;
	}

	/**
	 * 获取pk_jobrank
	 * 
	 * @return pk_jobrank
	 */
	public String getPk_jobrank() {
		return this.pk_jobrank;
	}

	/**
	 * 设置pk_jobrank
	 * 
	 * @param pk_jobrank pk_jobrank
	 */
	public void setPk_jobrank(String pk_jobrank) {
		this.pk_jobrank = pk_jobrank;
	}

	/**
	 * 获取pk_jobserial
	 * 
	 * @return pk_jobserial
	 */
	public String getPk_jobserial() {
		return this.pk_jobserial;
	}

	/**
	 * 设置pk_jobserial
	 * 
	 * @param pk_jobserial pk_jobserial
	 */
	public void setPk_jobserial(String pk_jobserial) {
		this.pk_jobserial = pk_jobserial;
	}

	/**
	 * 获取职务
	 * 
	 * @return 职务
	 */
	public String getPk_om_duty() {
		return this.pk_om_duty;
	}

	/**
	 * 设置职务
	 * 
	 * @param pk_om_duty 职务
	 */
	public void setPk_om_duty(String pk_om_duty) {
		this.pk_om_duty = pk_om_duty;
	}

	/**
	 * 获取岗位
	 * 
	 * @return 岗位
	 */
	public String getPk_om_job() {
		return this.pk_om_job;
	}

	/**
	 * 设置岗位
	 * 
	 * @param pk_om_job 岗位
	 */
	public void setPk_om_job(String pk_om_job) {
		this.pk_om_job = pk_om_job;
	}

	/**
	 * 获取pk_psnbasdoc
	 * 
	 * @return pk_psnbasdoc
	 */
	public String getPk_psnbasdoc() {
		return this.pk_psnbasdoc;
	}

	/**
	 * 设置pk_psnbasdoc
	 * 
	 * @param pk_psnbasdoc pk_psnbasdoc
	 */
	public void setPk_psnbasdoc(String pk_psnbasdoc) {
		this.pk_psnbasdoc = pk_psnbasdoc;
	}

	/**
	 * 获取人员
	 * 
	 * @return 人员
	 */
	public String getPk_psndoc() {
		return this.pk_psndoc;
	}

	/**
	 * 设置人员
	 * 
	 * @param pk_psndoc 人员
	 */
	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	/**
	 * 获取分配单元子表PK
	 * 
	 * @return 分配单元子表PK
	 */
	public String getPk_wa_ba_unit() {
		return this.pk_wa_ba_unit;
	}

	/**
	 * 设置分配单元子表PK
	 * 
	 * @param pk_wa_ba_unit 分配单元子表PK
	 */
	public void setPk_wa_ba_unit(String pk_wa_ba_unit) {
		this.pk_wa_ba_unit = pk_wa_ba_unit;
	}

	/**
	 * 获取psn_attkey
	 * 
	 * @return psn_attkey
	 */
	public String getPsn_attkey() {
		return this.psn_attkey;
	}

	/**
	 * 设置psn_attkey
	 * 
	 * @param psn_attkey psn_attkey
	 */
	public void setPsn_attkey(String psn_attkey) {
		this.psn_attkey = psn_attkey;
	}

	/**
	 * 获取psn_attval
	 * 
	 * @return psn_attval
	 */
	public String getPsn_attval() {
		return this.psn_attval;
	}

	/**
	 * 设置psn_attval
	 * 
	 * @param psn_attval psn_attval
	 */
	public void setPsn_attval(String psn_attval) {
		this.psn_attval = psn_attval;
	}

	/**
	 * 获取计划金额1
	 * 
	 * @return 计划金额1
	 */
	public UFDouble getPvalue1() {
		return this.pvalue1;
	}

	/**
	 * 设置计划金额1
	 * 
	 * @param pvalue1 计划金额1
	 */
	public void setPvalue1(UFDouble pvalue1) {
		this.pvalue1 = pvalue1;
	}

	/**
	 * 获取计划金额2
	 * 
	 * @return 计划金额2
	 */
	public UFDouble getPvalue2() {
		return this.pvalue2;
	}

	/**
	 * 设置计划金额2
	 * 
	 * @param pvalue2 计划金额2
	 */
	public void setPvalue2(UFDouble pvalue2) {
		this.pvalue2 = pvalue2;
	}

	/**
	 * 获取计划金额3
	 * 
	 * @return 计划金额3
	 */
	public UFDouble getPvalue3() {
		return this.pvalue3;
	}

	/**
	 * 设置计划金额3
	 * 
	 * @param pvalue3 计划金额3
	 */
	public void setPvalue3(UFDouble pvalue3) {
		this.pvalue3 = pvalue3;
	}

	/**
	 * 获取计划金额4
	 * 
	 * @return 计划金额4
	 */
	public UFDouble getPvalue4() {
		return this.pvalue4;
	}

	/**
	 * 设置计划金额4
	 * 
	 * @param pvalue4 计划金额4
	 */
	public void setPvalue4(UFDouble pvalue4) {
		this.pvalue4 = pvalue4;
	}

	/**
	 * 获取计划金额5
	 * 
	 * @return 计划金额5
	 */
	public UFDouble getPvalue5() {
		return this.pvalue5;
	}

	/**
	 * 设置计划金额5
	 * 
	 * @param pvalue5 计划金额5
	 */
	public void setPvalue5(UFDouble pvalue5) {
		this.pvalue5 = pvalue5;
	}

	/**
	 * 获取修订后考核系数
	 * 
	 * @return 修订后考核系数
	 */
	public UFDouble getRevise_factor() {
		return this.revise_factor;
	}

	/**
	 * 设置修订后考核系数
	 * 
	 * @param revise_factor 修订后考核系数
	 */
	public void setRevise_factor(UFDouble revise_factor) {
		this.revise_factor = revise_factor;
	}

	/**
	 * 获取修订后绩效工资总额
	 * 
	 * @return 修订后绩效工资总额
	 */
	public UFDouble getRevise_totalmoney() {
		return this.revise_totalmoney;
	}

	/**
	 * 设置修订后绩效工资总额
	 * 
	 * @param revise_totalmoney 修订后绩效工资总额
	 */
	public void setRevise_totalmoney(UFDouble revise_totalmoney) {
		this.revise_totalmoney = revise_totalmoney;
	}

	/**
	 * 获取时间戳
	 * 
	 * @return 时间戳
	 */
	public UFDateTime getTs() {
		return this.ts;
	}

	/**
	 * 设置时间戳
	 * 
	 * @param ts 时间戳
	 */
	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	/**
	 * 获取value1
	 * 
	 * @return value1
	 */
	public UFDouble getValue1() {
		return this.value1;
	}

	/**
	 * 设置value1
	 * 
	 * @param value1 value1
	 */
	public void setValue1(UFDouble value1) {
		this.value1 = value1;
	}

	/**
	 * 获取value2
	 * 
	 * @return value2
	 */
	public UFDouble getValue2() {
		return this.value2;
	}

	/**
	 * 设置value2
	 * 
	 * @param value2 value2
	 */
	public void setValue2(UFDouble value2) {
		this.value2 = value2;
	}

	/**
	 * 获取value3
	 * 
	 * @return value3
	 */
	public UFDouble getValue3() {
		return this.value3;
	}

	/**
	 * 设置value3
	 * 
	 * @param value3 value3
	 */
	public void setValue3(UFDouble value3) {
		this.value3 = value3;
	}

	/**
	 * 获取value4
	 * 
	 * @return value4
	 */
	public UFDouble getValue4() {
		return this.value4;
	}

	/**
	 * 设置value4
	 * 
	 * @param value4 value4
	 */
	public void setValue4(UFDouble value4) {
		this.value4 = value4;
	}

	/**
	 * 获取value5
	 * 
	 * @return value5
	 */
	public UFDouble getValue5() {
		return this.value5;
	}

	/**
	 * 设置value5
	 * 
	 * @param value5 value5
	 */
	public void setValue5(UFDouble value5) {
		this.value5 = value5;
	}

	/**
	 * 获取奖金项目1
	 * 
	 * @return 奖金项目1
	 */
	public String getWaitem1() {
		return this.waitem1;
	}

	/**
	 * 设置奖金项目1
	 * 
	 * @param waitem1 奖金项目1
	 */
	public void setWaitem1(String waitem1) {
		this.waitem1 = waitem1;
	}

	/**
	 * 获取奖金项目2
	 * 
	 * @return 奖金项目2
	 */
	public String getWaitem2() {
		return this.waitem2;
	}

	/**
	 * 设置奖金项目2
	 * 
	 * @param waitem2 奖金项目2
	 */
	public void setWaitem2(String waitem2) {
		this.waitem2 = waitem2;
	}

	/**
	 * 获取奖金项目3
	 * 
	 * @return 奖金项目3
	 */
	public String getWaitem3() {
		return this.waitem3;
	}

	/**
	 * 设置奖金项目3
	 * 
	 * @param waitem3 奖金项目3
	 */
	public void setWaitem3(String waitem3) {
		this.waitem3 = waitem3;
	}

	/**
	 * 获取奖金项目4
	 * 
	 * @return 奖金项目4
	 */
	public String getWaitem4() {
		return this.waitem4;
	}

	/**
	 * 设置奖金项目4
	 * 
	 * @param waitem4 奖金项目4
	 */
	public void setWaitem4(String waitem4) {
		this.waitem4 = waitem4;
	}

	/**
	 * 获取奖金项目5
	 * 
	 * @return 奖金项目5
	 */
	public String getWaitem5() {
		return this.waitem5;
	}

	/**
	 * 设置奖金项目5
	 * 
	 * @param waitem5 奖金项目5
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
	 * @param pk_psnjob 要设置的 pk_psnjob
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