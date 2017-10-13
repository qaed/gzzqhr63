package com.yonyou.portal.hrss.wabasch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.logging.Logger;
import nc.uap.lfw.core.cmd.UifDatasetAfterSelectCmd;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.DatasetRelation;
import nc.uap.lfw.core.data.DatasetRelations;
import nc.uap.lfw.core.data.PaginationInfo;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.data.RowSet;
import nc.uap.lfw.core.exception.LfwBusinessException;
import nc.uap.lfw.core.log.LfwSysOutWrapper;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.util.LfwClassUtil;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.SuperVO;
import nc.vo.wa.wa_ba.sch.WaBaSchBVO;

public class WabaschUifDatasetAfterSelectCmd extends UifDatasetAfterSelectCmd {
	private String datasetId;
	private Dataset ds;
	private LfwView widget;

	private String sqlin;

	public String getDatasetId() {
		return datasetId;
	}

	public void setDatasetId(String datasetId) {
		this.datasetId = datasetId;
	}

	public Dataset getDs() {
		return ds;
	}

	public void setDs(Dataset ds) {
		this.ds = ds;
	}

	public LfwView getWidget() {
		return widget;
	}

	public void setWidget(LfwView widget) {
		this.widget = widget;
	}

	public WabaschUifDatasetAfterSelectCmd(String dsId) {
		super(dsId);
		datasetId = dsId;
	}

	@Override
	/**
	 * ��ѡ�з������������ӹ�ϵ��ͨ���߼�
	 */
	public void execute() {
		sqlin = "";
		Dataset masterDs;
		LfwView widget;
		if (ds == null) {
			widget = getLifeCycleContext().getViewContext().getView();
			masterDs = widget.getViewModels().getDataset(datasetId);
		} else {
			masterDs = ds;
			widget = this.widget;
		}
		//��ȡ���ݼ����ӹ�ϵ���������ӹ�ϵ���������ݼ�����
		DatasetRelations dsRels = widget.getViewModels().getDsrelations();
		if (dsRels != null) {
			DatasetRelation[] masterRels = dsRels.getDsRelations(/*masterDs.getId()*/);
			//��ȡ������
			Row masterSelecteRow = getMasterRow(masterDs);
			if (masterSelecteRow == null) {
				//��ȡ�Ӷ�Ӧ�����ֵ�������õ�VO������
				for (int i = 0; i < masterRels.length; i++) {
					DatasetRelation dr = masterRels[i];
					Dataset detailDs = widget.getViewModels().getDataset(dr.getDetailDataset());
					detailDs.clear();
				}
				//				updateButtons();
				return;
			}
			for (int i = 0; i < masterRels.length; i++) {
				//��ȡ�Ӷ�Ӧ�����ֵ�������õ�VO������
				DatasetRelation dr = masterRels[i];
				Dataset detailDs = widget.getViewModels().getDataset(dr.getDetailDataset());
				//��ȡ��ǰPageIndex
				int curPageIndex = getPageIndex(detailDs);
				//				detailDs.clear();
				String masterKey = dr.getMasterKeyField();
				String detailFk = dr.getDetailForeignKey();

				String keyValue = null;
				try {
					int keyValueIndex = masterDs.getFieldSet().fieldToIndex(masterKey);
					if ((keyValueIndex >= 0) && (keyValueIndex < masterSelecteRow.getContent().length)) {
						keyValue = (String) masterSelecteRow.getValue(keyValueIndex);
					}
				} catch (Exception e) {
					Logger.warn("����Խ���쳣", e);
				}

				//��ʶ�Ƿ���������
				boolean isNewMaster = false;
				if (keyValue == null) {
					isNewMaster = true;
					keyValue = masterSelecteRow.getRowId();
				}
				//�����ӱ�ǰ���ݿ飨�����Ӧֵ)
				String detailFkValue = keyValue;
				//��sql�����ݷ����仯  ����дchangeCurrentKey����    �ı�Currentkey�����¼���
				chanCurrentKey(keyValue, detailDs);
				RowSet rowset = detailDs.getRowSet(detailDs.getCurrentKey(), true);

				PaginationInfo pinfo = rowset.getPaginationInfo();
				pinfo.setPageIndex(curPageIndex);
				String clazz = detailDs.getVoMeta();
				SuperVO vo = (SuperVO) LfwClassUtil.newInstance(clazz);

				if (!isNewMaster) {
					vo.setAttributeValue(detailFk, detailFkValue);//�����������������ڲ�ѯ
				}
				//��һ�����в�ѯ��������
				String wherePart = postProcessRowSelectVO(vo, detailDs);
				if ("nc.vo.wa.wa_ba.sch.WaBaSchTVO".equals(clazz)) {
					if (sqlin != null && !"".equals(sqlin)) {
						wherePart = " 1 = 1 and pk_ba_sch_unit in ( " + sqlin.substring(0, sqlin.length() - 1) + ")";
						LfwSysOutWrapper.println("��ѯ�������where��" + wherePart);

					} else {
						//û����Ҫ���������
						return;
					}
				}
				//����Ԫ����Tabcode����֧�ֵ���������
				processTabCode(widget, detailDs, vo);
				//������ds��orderby
				String orderPart = postOrderPart(detailDs);
				//				(String) detailDs.getExtendAttributeValue("ORDER_PART");
				try {
					isNewMaster = false;

					SuperVO[] vos = queryChildVOs(pinfo, vo, wherePart, isNewMaster, orderPart);//�������������������Ը��������в�ѯ
					if ("nc.vo.wa.wa_ba.sch.WaBaSchBVO".equals(clazz)) {
						/*
						 * ��Ƭ��BVOֻ���һ���ͺ�,��һ��ֻ����һ����Ԫ������
						 * ��Ϊ�ڿ�Ƭҳ�棬�޸�-�������ʱ����savecmd��Ҫ����BVOdataset��masterDataset����TVOdataset��detailDataset��
						 * ����ԭʼ��ƣ���һ������masterDataset��Ĭ��ֻ�õ�һ����������ܵ���TVO��BVO����Ӧ
						 */
						LfwSysOutWrapper.println("����ʾ��ǰ��¼�˿ɷ�������ݣ���ǰ��¼��pk��" + SessionUtil.getPk_psndoc());
						List<SuperVO> bvos = new ArrayList<SuperVO>(vos.length);
						Collections.addAll(bvos, vos);
						boolean hasConstructedSQL = false;//�Ƿ��ѹ���sql���
						Iterator<SuperVO> it = bvos.iterator();
						while (it.hasNext()) {
							WaBaSchBVO bvo = (WaBaSchBVO) it.next();
							//����ʾ��ǰ��¼�˿ɷ��������
							if (bvo.getVdef1() == null || !SessionUtil.getPk_psndoc().equals(bvo.getVdef1()) || hasConstructedSQL) {
								it.remove();
							} else if (!hasConstructedSQL) {//��δ����sql���
								sqlin += "'" + bvo.getPk_ba_sch_unit() + "',";
								hasConstructedSQL = true;
							}
						}
						//��ʱbvosӦ��ֻʣ��1��
						vos = bvos.toArray(vos);
					}

					modifyVos(vos);
					getSerializer().serialize(vos, detailDs, 0);
					postProcessChildRowSelect(detailDs);

					detailDs.setEnabled(false);
				} catch (LfwBusinessException exp) {
					Logger.error(exp.getMessage(), exp);
					throw new nc.uap.lfw.core.exception.LfwRuntimeException(NCLangRes4VoTransl.getNCLangRes().getStrByID("pub", "UifDatasetAfterSelectCmd-000000") /*��ѯ�������,*/
							+ exp.getMessage() + ",ds id:" + detailDs.getId(), NCLangRes4VoTransl.getNCLangRes().getStrByID("pub", "UifDatasetAfterSelectCmd-000001"));/*��ѯ���̳��ִ���*/
				}
			}
		}
		//		updateButtons();

	}
}
