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
	 * 奖金分配负责人
	 */
	public String ba_mng_psnpk;
	/**
	 * 分配人名称
	 */
	public String ba_mng_psnpk_showname;
	/**
	 * 奖金单元编码
	 */
	public String ba_unit_code;
	/**
	 * 奖金单元名称
	 */
	public String ba_unit_name;
	/**
	 * 奖金单元类型
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
	 * 选择
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
	 * 上层单据主键
	 */
	public String pk_ba_sch_h;
	/**
	 * 主键
	 */
	public String pk_ba_sch_unit;
	/**
	 * 公司
	 */
	public String pk_corp;
	/**
	 * 奖金单元主键
	 */
	public String pk_wa_ba_unit;
	/**
	 * 累计可分配总额
	 */
	public String plan_totalmoney;
	/**
	 * 项目计划分配额1
	 */
	public String ptmny1;
	/**
	 * 项目计划分配额2
	 */
	public String ptmny2;
	/**
	 * 项目计划分配额3
	 */
	public String ptmny3;
	/**
	 * 项目计划分配额4
	 */
	public String ptmny4;
	/**
	 * 项目计划分配额5
	 */
	public String ptmny5;
	/**
	 * 时间戳
	 */
	public UFDateTime ts;
	/**
	 * 自定义项1
	 */
	public String vdef1;
	/**
	 * 自定义项10
	 */
	public String vdef10;
	/**
	 * 自定义项11
	 */
	public String vdef11;
	/**
	 * 自定义项12
	 */
	public String vdef12;
	/**
	 * 自定义项13
	 */
	public String vdef13;
	/**
	 * 自定义项14
	 */
	public String vdef14;
	/**
	 * 自定义项15
	 */
	public String vdef15;
	/**
	 * 自定义项16
	 */
	public String vdef16;
	/**
	 * 自定义项17
	 */
	public String vdef17;
	/**
	 * 自定义项18
	 */
	public String vdef18;
	/**
	 * 自定义项19
	 */
	public String vdef19;
	/**
	 * 自定义项2
	 */
	public String vdef2;
	/**
	 * 自定义项20
	 */
	public String vdef20;
	/**
	 * 自定义项3
	 */
	public String vdef3;
	/**
	 * 自定义项4
	 */
	public String vdef4;
	/**
	 * 自定义项5
	 */
	public String vdef5;
	/**
	 * 自定义项6
	 */
	public String vdef6;
	/**
	 * 自定义项7
	 */
	public String vdef7;
	/**
	 * 自定义项8
	 */
	public String vdef8;
	/**
	 * 自定义项9
	 */
	public String vdef9;
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
	 * 孙实体
	 */
	public WaBaSchTVO[] pk_s = null;

	/**
	 * 获取奖金分配负责人
	 * 
	 * @return 奖金分配负责人
	 */
	public String getBa_mng_psnpk() {
		return this.ba_mng_psnpk;
	}

	/**
	 * 设置奖金分配负责人
	 * 
	 * @param ba_mng_psnpk
	 *            奖金分配负责人
	 */
	public void setBa_mng_psnpk(String ba_mng_psnpk) {
		this.ba_mng_psnpk = ba_mng_psnpk;
	}

	/**
	 * 获取分配人名称
	 * 
	 * @return 分配人名称
	 */
	public String getBa_mng_psnpk_showname() {
		return this.ba_mng_psnpk_showname;
	}

	/**
	 * 设置分配人名称
	 * 
	 * @param ba_mng_psnpk_showname
	 *            分配人名称
	 */
	public void setBa_mng_psnpk_showname(String ba_mng_psnpk_showname) {
		this.ba_mng_psnpk_showname = ba_mng_psnpk_showname;
	}

	/**
	 * 获取奖金单元编码
	 * 
	 * @return 奖金单元编码
	 */
	public String getBa_unit_code() {
		return this.ba_unit_code;
	}

	/**
	 * 设置奖金单元编码
	 * 
	 * @param ba_unit_code
	 *            奖金单元编码
	 */
	public void setBa_unit_code(String ba_unit_code) {
		this.ba_unit_code = ba_unit_code;
	}

	/**
	 * 获取奖金单元名称
	 * 
	 * @return 奖金单元名称
	 */
	public String getBa_unit_name() {
		return this.ba_unit_name;
	}

	/**
	 * 设置奖金单元名称
	 * 
	 * @param ba_unit_name
	 *            奖金单元名称
	 */
	public void setBa_unit_name(String ba_unit_name) {
		this.ba_unit_name = ba_unit_name;
	}

	/**
	 * 获取奖金单元类型
	 * 
	 * @return 奖金单元类型
	 */
	public String getBa_unit_type() {
		return this.ba_unit_type;
	}

	/**
	 * 设置奖金单元类型
	 * 
	 * @param ba_unit_type
	 *            奖金单元类型
	 */
	public void setBa_unit_type(String ba_unit_type) {
		this.ba_unit_type = ba_unit_type;
	}

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
	 * @param class1
	 *            class1
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
	 * @param class2
	 *            class2
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
	 * @param class3
	 *            class3
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
	 * @param class4
	 *            class4
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
	 * @param class5
	 *            class5
	 */
	public void setClass5(String class5) {
		this.class5 = class5;
	}

	/**
	 * 获取选择
	 * 
	 * @return 选择
	 */
	public UFBoolean getIsapprove() {
		return this.isapprove;
	}

	/**
	 * 设置选择
	 * 
	 * @param isapprove
	 *            选择
	 */
	public void setIsapprove(UFBoolean isapprove) {
		this.isapprove = isapprove;
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
	 * @param item1
	 *            item1
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
	 * @param item2
	 *            item2
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
	 * @param item3
	 *            item3
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
	 * @param item4
	 *            item4
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
	 * @param item5
	 *            item5
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
	 * @param memo1
	 *            备注1
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
	 * @param memo2
	 *            备注2
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
	 * @param memo3
	 *            备注3
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
	 * @param memo4
	 *            备注4
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
	 * @param memo5
	 *            备注5
	 */
	public void setMemo5(String memo5) {
		this.memo5 = memo5;
	}

	/**
	 * 获取上层单据主键
	 * 
	 * @return 上层单据主键
	 */
	public String getPk_ba_sch_h() {
		return this.pk_ba_sch_h;
	}

	/**
	 * 设置上层单据主键
	 * 
	 * @param pk_ba_sch_h
	 *            上层单据主键
	 */
	public void setPk_ba_sch_h(String pk_ba_sch_h) {
		this.pk_ba_sch_h = pk_ba_sch_h;
	}

	/**
	 * 获取主键
	 * 
	 * @return 主键
	 */
	public String getPk_ba_sch_unit() {
		return this.pk_ba_sch_unit;
	}

	/**
	 * 设置主键
	 * 
	 * @param pk_ba_sch_unit
	 *            主键
	 */
	public void setPk_ba_sch_unit(String pk_ba_sch_unit) {
		this.pk_ba_sch_unit = pk_ba_sch_unit;
	}

	/**
	 * 获取公司
	 * 
	 * @return 公司
	 */
	public String getPk_corp() {
		return this.pk_corp;
	}

	/**
	 * 设置公司
	 * 
	 * @param pk_corp
	 *            公司
	 */
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	/**
	 * 获取奖金单元主键
	 * 
	 * @return 奖金单元主键
	 */
	public String getPk_wa_ba_unit() {
		return this.pk_wa_ba_unit;
	}

	/**
	 * 设置奖金单元主键
	 * 
	 * @param pk_wa_ba_unit
	 *            奖金单元主键
	 */
	public void setPk_wa_ba_unit(String pk_wa_ba_unit) {
		this.pk_wa_ba_unit = pk_wa_ba_unit;
	}

	/**
	 * 获取累计可分配总额
	 * 
	 * @return 累计可分配总额
	 */
	public String getPlan_totalmoney() {
		return this.plan_totalmoney;
	}

	/**
	 * 设置累计可分配总额
	 * 
	 * @param plan_totalmoney
	 *            累计可分配总额
	 */
	public void setPlan_totalmoney(String plan_totalmoney) {
		this.plan_totalmoney = plan_totalmoney;
	}

	/**
	 * 获取项目计划分配额1
	 * 
	 * @return 项目计划分配额1
	 */
	public String getPtmny1() {
		return this.ptmny1;
	}

	/**
	 * 设置项目计划分配额1
	 * 
	 * @param ptmny1
	 *            项目计划分配额1
	 */
	public void setPtmny1(String ptmny1) {
		this.ptmny1 = ptmny1;
	}

	/**
	 * 获取项目计划分配额2
	 * 
	 * @return 项目计划分配额2
	 */
	public String getPtmny2() {
		return this.ptmny2;
	}

	/**
	 * 设置项目计划分配额2
	 * 
	 * @param ptmny2
	 *            项目计划分配额2
	 */
	public void setPtmny2(String ptmny2) {
		this.ptmny2 = ptmny2;
	}

	/**
	 * 获取项目计划分配额3
	 * 
	 * @return 项目计划分配额3
	 */
	public String getPtmny3() {
		return this.ptmny3;
	}

	/**
	 * 设置项目计划分配额3
	 * 
	 * @param ptmny3
	 *            项目计划分配额3
	 */
	public void setPtmny3(String ptmny3) {
		this.ptmny3 = ptmny3;
	}

	/**
	 * 获取项目计划分配额4
	 * 
	 * @return 项目计划分配额4
	 */
	public String getPtmny4() {
		return this.ptmny4;
	}

	/**
	 * 设置项目计划分配额4
	 * 
	 * @param ptmny4
	 *            项目计划分配额4
	 */
	public void setPtmny4(String ptmny4) {
		this.ptmny4 = ptmny4;
	}

	/**
	 * 获取项目计划分配额5
	 * 
	 * @return 项目计划分配额5
	 */
	public String getPtmny5() {
		return this.ptmny5;
	}

	/**
	 * 设置项目计划分配额5
	 * 
	 * @param ptmny5
	 *            项目计划分配额5
	 */
	public void setPtmny5(String ptmny5) {
		this.ptmny5 = ptmny5;
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
	 * @param ts
	 *            时间戳
	 */
	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	/**
	 * 获取自定义项1
	 * 
	 * @return 自定义项1
	 */
	public String getVdef1() {
		return this.vdef1;
	}

	/**
	 * 设置自定义项1
	 * 
	 * @param vdef1
	 *            自定义项1
	 */
	public void setVdef1(String vdef1) {
		this.vdef1 = vdef1;
	}

	/**
	 * 获取自定义项10
	 * 
	 * @return 自定义项10
	 */
	public String getVdef10() {
		return this.vdef10;
	}

	/**
	 * 设置自定义项10
	 * 
	 * @param vdef10
	 *            自定义项10
	 */
	public void setVdef10(String vdef10) {
		this.vdef10 = vdef10;
	}

	/**
	 * 获取自定义项11
	 * 
	 * @return 自定义项11
	 */
	public String getVdef11() {
		return this.vdef11;
	}

	/**
	 * 设置自定义项11
	 * 
	 * @param vdef11
	 *            自定义项11
	 */
	public void setVdef11(String vdef11) {
		this.vdef11 = vdef11;
	}

	/**
	 * 获取自定义项12
	 * 
	 * @return 自定义项12
	 */
	public String getVdef12() {
		return this.vdef12;
	}

	/**
	 * 设置自定义项12
	 * 
	 * @param vdef12
	 *            自定义项12
	 */
	public void setVdef12(String vdef12) {
		this.vdef12 = vdef12;
	}

	/**
	 * 获取自定义项13
	 * 
	 * @return 自定义项13
	 */
	public String getVdef13() {
		return this.vdef13;
	}

	/**
	 * 设置自定义项13
	 * 
	 * @param vdef13
	 *            自定义项13
	 */
	public void setVdef13(String vdef13) {
		this.vdef13 = vdef13;
	}

	/**
	 * 获取自定义项14
	 * 
	 * @return 自定义项14
	 */
	public String getVdef14() {
		return this.vdef14;
	}

	/**
	 * 设置自定义项14
	 * 
	 * @param vdef14
	 *            自定义项14
	 */
	public void setVdef14(String vdef14) {
		this.vdef14 = vdef14;
	}

	/**
	 * 获取自定义项15
	 * 
	 * @return 自定义项15
	 */
	public String getVdef15() {
		return this.vdef15;
	}

	/**
	 * 设置自定义项15
	 * 
	 * @param vdef15
	 *            自定义项15
	 */
	public void setVdef15(String vdef15) {
		this.vdef15 = vdef15;
	}

	/**
	 * 获取自定义项16
	 * 
	 * @return 自定义项16
	 */
	public String getVdef16() {
		return this.vdef16;
	}

	/**
	 * 设置自定义项16
	 * 
	 * @param vdef16
	 *            自定义项16
	 */
	public void setVdef16(String vdef16) {
		this.vdef16 = vdef16;
	}

	/**
	 * 获取自定义项17
	 * 
	 * @return 自定义项17
	 */
	public String getVdef17() {
		return this.vdef17;
	}

	/**
	 * 设置自定义项17
	 * 
	 * @param vdef17
	 *            自定义项17
	 */
	public void setVdef17(String vdef17) {
		this.vdef17 = vdef17;
	}

	/**
	 * 获取自定义项18
	 * 
	 * @return 自定义项18
	 */
	public String getVdef18() {
		return this.vdef18;
	}

	/**
	 * 设置自定义项18
	 * 
	 * @param vdef18
	 *            自定义项18
	 */
	public void setVdef18(String vdef18) {
		this.vdef18 = vdef18;
	}

	/**
	 * 获取自定义项19
	 * 
	 * @return 自定义项19
	 */
	public String getVdef19() {
		return this.vdef19;
	}

	/**
	 * 设置自定义项19
	 * 
	 * @param vdef19
	 *            自定义项19
	 */
	public void setVdef19(String vdef19) {
		this.vdef19 = vdef19;
	}

	/**
	 * 获取自定义项2
	 * 
	 * @return 自定义项2
	 */
	public String getVdef2() {
		return this.vdef2;
	}

	/**
	 * 设置自定义项2
	 * 
	 * @param vdef2
	 *            自定义项2
	 */
	public void setVdef2(String vdef2) {
		this.vdef2 = vdef2;
	}

	/**
	 * 获取自定义项20
	 * 
	 * @return 自定义项20
	 */
	public String getVdef20() {
		return this.vdef20;
	}

	/**
	 * 设置自定义项20
	 * 
	 * @param vdef20
	 *            自定义项20
	 */
	public void setVdef20(String vdef20) {
		this.vdef20 = vdef20;
	}

	/**
	 * 获取自定义项3
	 * 
	 * @return 自定义项3
	 */
	public String getVdef3() {
		return this.vdef3;
	}

	/**
	 * 设置自定义项3
	 * 
	 * @param vdef3
	 *            自定义项3
	 */
	public void setVdef3(String vdef3) {
		this.vdef3 = vdef3;
	}

	/**
	 * 获取自定义项4
	 * 
	 * @return 自定义项4
	 */
	public String getVdef4() {
		return this.vdef4;
	}

	/**
	 * 设置自定义项4
	 * 
	 * @param vdef4
	 *            自定义项4
	 */
	public void setVdef4(String vdef4) {
		this.vdef4 = vdef4;
	}

	/**
	 * 获取自定义项5
	 * 
	 * @return 自定义项5
	 */
	public String getVdef5() {
		return this.vdef5;
	}

	/**
	 * 设置自定义项5
	 * 
	 * @param vdef5
	 *            自定义项5
	 */
	public void setVdef5(String vdef5) {
		this.vdef5 = vdef5;
	}

	/**
	 * 获取自定义项6
	 * 
	 * @return 自定义项6
	 */
	public String getVdef6() {
		return this.vdef6;
	}

	/**
	 * 设置自定义项6
	 * 
	 * @param vdef6
	 *            自定义项6
	 */
	public void setVdef6(String vdef6) {
		this.vdef6 = vdef6;
	}

	/**
	 * 获取自定义项7
	 * 
	 * @return 自定义项7
	 */
	public String getVdef7() {
		return this.vdef7;
	}

	/**
	 * 设置自定义项7
	 * 
	 * @param vdef7
	 *            自定义项7
	 */
	public void setVdef7(String vdef7) {
		this.vdef7 = vdef7;
	}

	/**
	 * 获取自定义项8
	 * 
	 * @return 自定义项8
	 */
	public String getVdef8() {
		return this.vdef8;
	}

	/**
	 * 设置自定义项8
	 * 
	 * @param vdef8
	 *            自定义项8
	 */
	public void setVdef8(String vdef8) {
		this.vdef8 = vdef8;
	}

	/**
	 * 获取自定义项9
	 * 
	 * @return 自定义项9
	 */
	public String getVdef9() {
		return this.vdef9;
	}

	/**
	 * 设置自定义项9
	 * 
	 * @param vdef9
	 *            自定义项9
	 */
	public void setVdef9(String vdef9) {
		this.vdef9 = vdef9;
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
	 * @param waitem1
	 *            奖金项目1
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
	 * @param waitem2
	 *            奖金项目2
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
	 * @param waitem3
	 *            奖金项目3
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
	 * @param waitem4
	 *            奖金项目4
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
	 * @param waitem5
	 *            奖金项目5
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