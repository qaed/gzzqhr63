package nc.ui.bd.ref.model;

import nc.itf.org.IOrgConst;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.AbstractRefTreeModel;
import nc.ui.bd.ref.IRefDocEdit;
import nc.ui.bd.ref.IRefMaintenanceHandler;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.vo.bd.region.RegionVO;

import org.apache.commons.lang.StringUtils;

/**
 * 行政区划属性参照
 * 
 * @since 6.1
 * @version 2012-2-13 上午10:23:17
 * @author 王志强
 */
public class RegionDefaultRefTreeModel extends AbstractRefTreeModel {

	private String pk_country;

	public void reset() {
		setRefTitle(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("10140reg", "210140reg-00002")/* @res "行政区划" */);
		setRootName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("10140reg", "210140reg-00002")/* @res "行政区划" */);
		setFieldCode(new String[] { RegionVO.CODE, RegionVO.NAME, RegionVO.MEMCODE });
		setFieldName(new String[] { nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("10140reg", "210140reg-00000")/* @res "行政区划编码" */, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("10140reg", "210140reg-00004")/* @res "行政区划名称" */, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "2UC000-000172") /* @res "助记码" */
		});
		setHiddenFieldCode(new String[] { RegionVO.PK_REGION, RegionVO.PK_FATHER });
		setPkFieldCode(RegionVO.PK_REGION);
		setRefCodeField(RegionVO.CODE);
		setRefNameField(RegionVO.NAME);
		setTableName(RegionVO.getDefaultTableName());
		setFatherField(RegionVO.PK_FATHER);
		setChildField(RegionVO.PK_REGION);
		setMnecode(new String[] { RegionVO.MEMCODE, RegionVO.NAME });
		// 使用启动条件
		setAddEnableStateWherePart(true);
		setFilterRefNodeName(new String[] { "国家地区" });/* -=notranslate=- */
//		AbstractRefModel  model1 = getFilterRefModel("国家地区");
		// TODO 数据权限????
		// setResourceID(IBDResourceIDConst.REGION);
		
		// 维护
		setRefMaintenanceHandler(new IRefMaintenanceHandler() {

			@Override
			public IRefDocEdit getRefDocEdit() {
				return null;
			}

			@Override
			public String[] getFucCodes() {
				return new String[] { "10140REG" };
			}
		});
		resetFieldName();
		if (StringUtils.isEmpty(getPk_country())) {
			setPk_country(IOrgConst.DEFAULTCOUNTRYZONE);
		}
	}

	@Override
	protected String getEnvWherePart() {
		if (StringUtils.isEmpty(getPk_country())) {
			return "1=2";
		} else {
			return "pk_country='" + getPk_country() + "'";
		}
	}

	public void setPk_country(String pk_country) {
		this.pk_country = pk_country;
		setPk_org(IOrgConst.GLOBEORG);
	}

	public String getPk_country() {
		return pk_country;
	}

	@Override
	public void filterValueChanged(ValueChangedEvent changedValue) {
		super.filterValueChanged(changedValue);
		String[] selectedPKs = (String[]) changedValue.getNewValue();
		if (selectedPKs != null && selectedPKs.length > 0)
			setPk_country(selectedPKs[0]);
	}

}
