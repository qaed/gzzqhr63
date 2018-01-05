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
 * �����������Բ���
 * 
 * @since 6.1
 * @version 2012-2-13 ����10:23:17
 * @author ��־ǿ
 */
public class RegionDefaultRefTreeModel extends AbstractRefTreeModel {

	private String pk_country;

	public void reset() {
		setRefTitle(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("10140reg", "210140reg-00002")/* @res "��������" */);
		setRootName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("10140reg", "210140reg-00002")/* @res "��������" */);
		setFieldCode(new String[] { RegionVO.CODE, RegionVO.NAME, RegionVO.MEMCODE });
		setFieldName(new String[] { nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("10140reg", "210140reg-00000")/* @res "������������" */, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("10140reg", "210140reg-00004")/* @res "������������" */, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "2UC000-000172") /* @res "������" */
		});
		setHiddenFieldCode(new String[] { RegionVO.PK_REGION, RegionVO.PK_FATHER });
		setPkFieldCode(RegionVO.PK_REGION);
		setRefCodeField(RegionVO.CODE);
		setRefNameField(RegionVO.NAME);
		setTableName(RegionVO.getDefaultTableName());
		setFatherField(RegionVO.PK_FATHER);
		setChildField(RegionVO.PK_REGION);
		setMnecode(new String[] { RegionVO.MEMCODE, RegionVO.NAME });
		// ʹ����������
		setAddEnableStateWherePart(true);
		setFilterRefNodeName(new String[] { "���ҵ���" });/* -=notranslate=- */
//		AbstractRefModel  model1 = getFilterRefModel("���ҵ���");
		// TODO ����Ȩ��????
		// setResourceID(IBDResourceIDConst.REGION);
		
		// ά��
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
