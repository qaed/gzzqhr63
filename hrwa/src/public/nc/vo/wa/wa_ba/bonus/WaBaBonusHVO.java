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
	 * 审批时间
	 */
	public UFDateTime approvedate;
	/**
	 * 审批批语
	 */
	public String approvenote;
	/**
	 * 审批人
	 */
	public String approver;
	/**
	 * 审批状态
	 */
	public Integer approvestatus;
	/**
	 * 单据编码
	 */
	public String bill_code;
	/**
	 * 单据名称
	 */
	public String bill_name;
	/**
	 * 提交日期
	 */
	public UFDateTime bill_submit_date;
	/**
	 * 提交人
	 */
	public String bill_submitter;
	/**
	 * 单据日期
	 */
	public UFDate billdate;
	/**
	 * 创建时间
	 */
	public UFDateTime creationtime;
	/**
	 * 创建人
	 */
	public String creator;
	/**
	 * 修改时间
	 */
	public UFDateTime modifiedtime;
	/**
	 * 修改人
	 */
	public String modifier;
	/**
	 * 月度
	 */
	public String period;
	/**
	 * 奖金方案主键
	 */
	public String pk_ba_sch_h;
	/**
	 * 主键
	 */
	public String pk_bonus;
	/**
	 * 审批流程
	 */
	public String pk_busitype;
	/**
	 * 所属部门
	 */
	public String pk_deptdoc;
	/**
	 * 集团
	 */
	public String pk_group;
	/**
	 * 所属岗位
	 */
	public String pk_om_job;
	/**
	 * 组织
	 */
	public String pk_org;
	/**
	 * 组织版本
	 */
	public String pk_org_v;
	/**
	 * pk_psnbasdoc
	 */
	public String pk_psnbasdoc;
	/**
	 * 人员
	 */
	public String pk_psndoc;
	/**
	 * 分配总额
	 */
	public UFDouble totalamount;
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
	 * 备注
	 */
	public String vmemo;
	/**
	 * 年度
	 */
	public String year;

	/**
	 * dr
	 */
	public Integer dr;

	/**
	 * 获取审批时间
	 * 
	 * @return 审批时间
	 */
	public UFDateTime getApprovedate() {
		return this.approvedate;
	}

	/**
	 * 设置审批时间
	 * 
	 * @param approvedate
	 *            审批时间
	 */
	public void setApprovedate(UFDateTime approvedate) {
		this.approvedate = approvedate;
	}

	/**
	 * 获取审批批语
	 * 
	 * @return 审批批语
	 */
	public String getApprovenote() {
		return this.approvenote;
	}

	/**
	 * 设置审批批语
	 * 
	 * @param approvenote
	 *            审批批语
	 */
	public void setApprovenote(String approvenote) {
		this.approvenote = approvenote;
	}

	/**
	 * 获取审批人
	 * 
	 * @return 审批人
	 */
	public String getApprover() {
		return this.approver;
	}

	/**
	 * 设置审批人
	 * 
	 * @param approver
	 *            审批人
	 */
	public void setApprover(String approver) {
		this.approver = approver;
	}

	/**
	 * 获取审批状态
	 * 
	 * @return 审批状态
	 * @see String
	 */
	public Integer getApprovestatus() {
		return this.approvestatus;
	}

	/**
	 * 设置审批状态
	 * 
	 * @param approvestatus
	 *            审批状态
	 * @see String
	 */
	public void setApprovestatus(Integer approvestatus) {
		this.approvestatus = approvestatus;
	}

	/**
	 * 获取单据编码
	 * 
	 * @return 单据编码
	 */
	public String getBill_code() {
		return this.bill_code;
	}

	/**
	 * 设置单据编码
	 * 
	 * @param bill_code
	 *            单据编码
	 */
	public void setBill_code(String bill_code) {
		this.bill_code = bill_code;
	}

	/**
	 * 获取单据名称
	 * 
	 * @return 单据名称
	 */
	public String getBill_name() {
		return this.bill_name;
	}

	/**
	 * 设置单据名称
	 * 
	 * @param bill_name
	 *            单据名称
	 */
	public void setBill_name(String bill_name) {
		this.bill_name = bill_name;
	}

	/**
	 * 获取提交日期
	 * 
	 * @return 提交日期
	 */
	public UFDateTime getBill_submit_date() {
		return this.bill_submit_date;
	}

	/**
	 * 设置提交日期
	 * 
	 * @param bill_submit_date
	 *            提交日期
	 */
	public void setBill_submit_date(UFDateTime bill_submit_date) {
		this.bill_submit_date = bill_submit_date;
	}

	/**
	 * 获取提交人
	 * 
	 * @return 提交人
	 */
	public String getBill_submitter() {
		return this.bill_submitter;
	}

	/**
	 * 设置提交人
	 * 
	 * @param bill_submitter
	 *            提交人
	 */
	public void setBill_submitter(String bill_submitter) {
		this.bill_submitter = bill_submitter;
	}

	/**
	 * 获取单据日期
	 * 
	 * @return 单据日期
	 */
	public UFDate getBilldate() {
		return this.billdate;
	}

	/**
	 * 设置单据日期
	 * 
	 * @param billdate
	 *            单据日期
	 */
	public void setBilldate(UFDate billdate) {
		this.billdate = billdate;
	}

	/**
	 * 获取创建时间
	 * 
	 * @return 创建时间
	 */
	public UFDateTime getCreationtime() {
		return this.creationtime;
	}

	/**
	 * 设置创建时间
	 * 
	 * @param creationtime
	 *            创建时间
	 */
	public void setCreationtime(UFDateTime creationtime) {
		this.creationtime = creationtime;
	}

	/**
	 * 获取创建人
	 * 
	 * @return 创建人
	 */
	public String getCreator() {
		return this.creator;
	}

	/**
	 * 设置创建人
	 * 
	 * @param creator
	 *            创建人
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * 获取修改时间
	 * 
	 * @return 修改时间
	 */
	public UFDateTime getModifiedtime() {
		return this.modifiedtime;
	}

	/**
	 * 设置修改时间
	 * 
	 * @param modifiedtime
	 *            修改时间
	 */
	public void setModifiedtime(UFDateTime modifiedtime) {
		this.modifiedtime = modifiedtime;
	}

	/**
	 * 获取修改人
	 * 
	 * @return 修改人
	 */
	public String getModifier() {
		return this.modifier;
	}

	/**
	 * 设置修改人
	 * 
	 * @param modifier
	 *            修改人
	 */
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	/**
	 * 获取月度
	 * 
	 * @return 月度
	 */
	public String getPeriod() {
		return this.period;
	}

	/**
	 * 设置月度
	 * 
	 * @param period
	 *            月度
	 */
	public void setPeriod(String period) {
		this.period = period;
	}

	/**
	 * 获取奖金方案主键
	 * 
	 * @return 奖金方案主键
	 */
	public String getPk_ba_sch_h() {
		return this.pk_ba_sch_h;
	}

	/**
	 * 设置奖金方案主键
	 * 
	 * @param pk_ba_sch_h
	 *            奖金方案主键
	 */
	public void setPk_ba_sch_h(String pk_ba_sch_h) {
		this.pk_ba_sch_h = pk_ba_sch_h;
	}

	/**
	 * 获取主键
	 * 
	 * @return 主键
	 */
	public String getPk_bonus() {
		return this.pk_bonus;
	}

	/**
	 * 设置主键
	 * 
	 * @param pk_bonus
	 *            主键
	 */
	public void setPk_bonus(String pk_bonus) {
		this.pk_bonus = pk_bonus;
	}

	/**
	 * 获取审批流程
	 * 
	 * @return 审批流程
	 */
	public String getPk_busitype() {
		return this.pk_busitype;
	}

	/**
	 * 设置审批流程
	 * 
	 * @param pk_busitype
	 *            审批流程
	 */
	public void setPk_busitype(String pk_busitype) {
		this.pk_busitype = pk_busitype;
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
	 * @param pk_deptdoc
	 *            所属部门
	 */
	public void setPk_deptdoc(String pk_deptdoc) {
		this.pk_deptdoc = pk_deptdoc;
	}

	/**
	 * 获取集团
	 * 
	 * @return 集团
	 */
	public String getPk_group() {
		return this.pk_group;
	}

	/**
	 * 设置集团
	 * 
	 * @param pk_group
	 *            集团
	 */
	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}

	/**
	 * 获取所属岗位
	 * 
	 * @return 所属岗位
	 */
	public String getPk_om_job() {
		return this.pk_om_job;
	}

	/**
	 * 设置所属岗位
	 * 
	 * @param pk_om_job
	 *            所属岗位
	 */
	public void setPk_om_job(String pk_om_job) {
		this.pk_om_job = pk_om_job;
	}

	/**
	 * 获取组织
	 * 
	 * @return 组织
	 */
	public String getPk_org() {
		return this.pk_org;
	}

	/**
	 * 设置组织
	 * 
	 * @param pk_org
	 *            组织
	 */
	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	/**
	 * 获取组织版本
	 * 
	 * @return 组织版本
	 */
	public String getPk_org_v() {
		return this.pk_org_v;
	}

	/**
	 * 设置组织版本
	 * 
	 * @param pk_org_v
	 *            组织版本
	 */
	public void setPk_org_v(String pk_org_v) {
		this.pk_org_v = pk_org_v;
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
	 * @param pk_psnbasdoc
	 *            pk_psnbasdoc
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
	 * @param pk_psndoc
	 *            人员
	 */
	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	/**
	 * 获取分配总额
	 * 
	 * @return 分配总额
	 */
	public UFDouble getTotalamount() {
		return this.totalamount;
	}

	/**
	 * 设置分配总额
	 * 
	 * @param totalamount
	 *            分配总额
	 */
	public void setTotalamount(UFDouble totalamount) {
		this.totalamount = totalamount;
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
	 * 获取备注
	 * 
	 * @return 备注
	 */
	public String getVmemo() {
		return this.vmemo;
	}

	/**
	 * 设置备注
	 * 
	 * @param vmemo
	 *            备注
	 */
	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
	}

	/**
	 * 获取年度
	 * 
	 * @return 年度
	 */
	public String getYear() {
		return this.year;
	}

	/**
	 * 设置年度
	 * 
	 * @param year
	 *            年度
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