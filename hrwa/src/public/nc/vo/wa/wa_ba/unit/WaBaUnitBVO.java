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
	 * 所属组织
	 */
	public String pk_corp;
	/**
	 * 所属部门
	 */
	public String pk_deptdoc;
	/**
	 * 人员编码
	 */
	public String pk_psndoc;

	/**
	 * 人员工作记录
	 */
	private String pk_psnjob;

	/**
	 * 上层单据主键
	 */
	public String pk_wa_ba_unit;
	/**
	 * 子表主键
	 */
	public String pk_wa_ba_unit_b;
	/**
	 * 人员名称
	 */
	public String psnname;
	/**
	 * 性别
	 */
	public Integer sex;
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
	 * dr值
	 */
	public Integer dr;

	/**
	 * 获取所属组织
	 * 
	 * @return 所属组织
	 */
	public String getPk_corp() {
		return this.pk_corp;
	}

	/**
	 * 设置所属组织
	 * 
	 * @param pk_corp 所属组织
	 */
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	/**
	 * 获取所属部门
	 * 
	 * @return 所属部门
	 */
	public String getPk_deptdoc() {
		return this.pk_deptdoc;
	}

	/**
	 * 设置所属部门
	 * 
	 * @param pk_deptdoc 所属部门
	 */
	public void setPk_deptdoc(String pk_deptdoc) {
		this.pk_deptdoc = pk_deptdoc;
	}

	/**
	 * 获取人员编码
	 * 
	 * @return 人员编码
	 */
	public String getPk_psndoc() {
		return this.pk_psndoc;
	}

	/**
	 * 设置人员编码
	 * 
	 * @param pk_psndoc 人员编码
	 */
	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	/**
	 * 获取上层单据主键
	 * 
	 * @return 上层单据主键
	 */
	public String getPk_wa_ba_unit() {
		return this.pk_wa_ba_unit;
	}

	/**
	 * 设置上层单据主键
	 * 
	 * @param pk_wa_ba_unit 上层单据主键
	 */
	public void setPk_wa_ba_unit(String pk_wa_ba_unit) {
		this.pk_wa_ba_unit = pk_wa_ba_unit;
	}

	/**
	 * 获取子表主键
	 * 
	 * @return 子表主键
	 */
	public String getPk_wa_ba_unit_b() {
		return this.pk_wa_ba_unit_b;
	}

	/**
	 * 设置子表主键
	 * 
	 * @param pk_wa_ba_unit_b 子表主键
	 */
	public void setPk_wa_ba_unit_b(String pk_wa_ba_unit_b) {
		this.pk_wa_ba_unit_b = pk_wa_ba_unit_b;
	}

	/**
	 * 获取人员名称
	 * 
	 * @return 人员名称
	 */
	public String getPsnname() {
		return this.psnname;
	}

	/**
	 * 设置人员名称
	 * 
	 * @param psnname 人员名称
	 */
	public void setPsnname(String psnname) {
		this.psnname = psnname;
	}

	/**
	 * 获取性别
	 * 
	 * @return 性别
	 * @see String
	 */
	public Integer getSex() {
		return this.sex;
	}

	/**
	 * 设置性别
	 * 
	 * @param sex 性别
	 * @see String
	 */
	public void setSex(Integer sex) {
		this.sex = sex;
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
	 * @param vdef1 自定义项1
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
	 * @param vdef10 自定义项10
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
	 * @param vdef11 自定义项11
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
	 * @param vdef12 自定义项12
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
	 * @param vdef13 自定义项13
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
	 * @param vdef14 自定义项14
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
	 * @param vdef15 自定义项15
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
	 * @param vdef16 自定义项16
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
	 * @param vdef17 自定义项17
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
	 * @param vdef18 自定义项18
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
	 * @param vdef19 自定义项19
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
	 * @param vdef2 自定义项2
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
	 * @param vdef20 自定义项20
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
	 * @param vdef3 自定义项3
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
	 * @param vdef4 自定义项4
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
	 * @param vdef5 自定义项5
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
	 * @param vdef6 自定义项6
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
	 * @param vdef7 自定义项7
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
	 * @param vdef8 自定义项8
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
	 * @param vdef9 自定义项9
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
	 * @param pk_psnjob 要设置的 pk_psnjob
	 */
	public void setPk_psnjob(String pk_psnjob) {
		this.pk_psnjob = pk_psnjob;
	}
}