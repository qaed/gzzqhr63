package nc.ui.hrwa.wa_ba_unit.ref;

import nc.ui.bd.ref.AbstractRefModel;

public class WaUnitRefModel extends AbstractRefModel {
	public WaUnitRefModel() {
		reset();
	}

	public void reset() {
		setRefNodeName("奖金分配单元");
		setFieldCode(new String[] { "code", "name"});
		setFieldName(new String[] { "奖金分配单元编码", "奖金分配单元名称" });
		setHiddenFieldCode(new String[] { "pk_wa_ba_unit","ba_mng_psnpk","ba_unit_type" });
		setTableName("wa_ba_unit");
		setPkFieldCode("pk_wa_ba_unit");
		setDefaultFieldCount(2);
		setRefTitle("奖金分配单元");
		setWherePart(" isnull(dr,0)=0 and pk_org='" + this.getPk_org() + "'");
	}
}
