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
	 * 行选中方法，处理主子关系等通用逻辑
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
		//获取数据集主子关系，根据主子关系进行子数据集加载
		DatasetRelations dsRels = widget.getViewModels().getDsrelations();
		if (dsRels != null) {
			DatasetRelation[] masterRels = dsRels.getDsRelations(/*masterDs.getId()*/);
			//获取触发行
			Row masterSelecteRow = getMasterRow(masterDs);
			if (masterSelecteRow == null) {
				//获取子对应的外键值，并设置到VO条件中
				for (int i = 0; i < masterRels.length; i++) {
					DatasetRelation dr = masterRels[i];
					Dataset detailDs = widget.getViewModels().getDataset(dr.getDetailDataset());
					detailDs.clear();
				}
				//				updateButtons();
				return;
			}
			for (int i = 0; i < masterRels.length; i++) {
				//获取子对应的外键值，并设置到VO条件中
				DatasetRelation dr = masterRels[i];
				Dataset detailDs = widget.getViewModels().getDataset(dr.getDetailDataset());
				//获取当前PageIndex
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
					Logger.warn("数组越界异常", e);
				}

				//标识是否是新主行
				boolean isNewMaster = false;
				if (keyValue == null) {
					isNewMaster = true;
					keyValue = masterSelecteRow.getRowId();
				}
				//设置子表当前数据块（外键对应值)
				String detailFkValue = keyValue;
				//如sql或数据发生变化  可重写changeCurrentKey方法    改变Currentkey以重新加载
				chanCurrentKey(keyValue, detailDs);
				RowSet rowset = detailDs.getRowSet(detailDs.getCurrentKey(), true);

				PaginationInfo pinfo = rowset.getPaginationInfo();
				pinfo.setPageIndex(curPageIndex);
				String clazz = detailDs.getVoMeta();
				SuperVO vo = (SuperVO) LfwClassUtil.newInstance(clazz);

				if (!isNewMaster) {
					vo.setAttributeValue(detailFk, detailFkValue);//设置主表主键，用于查询
				}
				//进一步进行查询条件处理
				String wherePart = postProcessRowSelectVO(vo, detailDs);
				if ("nc.vo.wa.wa_ba.sch.WaBaSchTVO".equals(clazz)) {
					if (sqlin != null && !"".equals(sqlin)) {
						wherePart = " 1 = 1 and pk_ba_sch_unit in ( " + sqlin.substring(0, sqlin.length() - 1) + ")";
						LfwSysOutWrapper.println("查询孙表数据where：" + wherePart);

					} else {
						//没有需要分配的数据
						return;
					}
				}
				//进行元数据Tabcode特性支持的条件处理
				processTabCode(widget, detailDs, vo);
				//处理子ds的orderby
				String orderPart = postOrderPart(detailDs);
				//				(String) detailDs.getExtendAttributeValue("ORDER_PART");
				try {
					isNewMaster = false;

					SuperVO[] vos = queryChildVOs(pinfo, vo, wherePart, isNewMaster, orderPart);//设置了主表主键，所以根据它进行查询
					if ("nc.vo.wa.wa_ba.sch.WaBaSchBVO".equals(clazz)) {
						/*
						 * 卡片的BVO只查出一个就好,即一次只分配一个单元的数据
						 * 因为在卡片页面，修改-保存操作时调用savecmd需要传入BVOdataset「masterDataset」和TVOdataset「detailDataset」
						 * 由于原始设计，第一个参数masterDataset会默认只拿第一个，如果可能导致TVO和BVO不对应
						 */
						LfwSysOutWrapper.println("仅显示当前登录人可分配的数据，当前登录人pk：" + SessionUtil.getPk_psndoc());
						List<SuperVO> bvos = new ArrayList<SuperVO>(vos.length);
						Collections.addAll(bvos, vos);
						boolean hasConstructedSQL = false;//是否已构造sql语句
						Iterator<SuperVO> it = bvos.iterator();
						while (it.hasNext()) {
							WaBaSchBVO bvo = (WaBaSchBVO) it.next();
							//仅显示当前登录人可分配的数据
							if (bvo.getVdef1() == null || !SessionUtil.getPk_psndoc().equals(bvo.getVdef1()) || hasConstructedSQL) {
								it.remove();
							} else if (!hasConstructedSQL) {//还未构造sql语句
								sqlin += "'" + bvo.getPk_ba_sch_unit() + "',";
								hasConstructedSQL = true;
							}
						}
						//此时bvos应该只剩下1个
						vos = bvos.toArray(vos);
					}

					modifyVos(vos);
					getSerializer().serialize(vos, detailDs, 0);
					postProcessChildRowSelect(detailDs);

					detailDs.setEnabled(false);
				} catch (LfwBusinessException exp) {
					Logger.error(exp.getMessage(), exp);
					throw new nc.uap.lfw.core.exception.LfwRuntimeException(NCLangRes4VoTransl.getNCLangRes().getStrByID("pub", "UifDatasetAfterSelectCmd-000000") /*查询对象出错,*/
							+ exp.getMessage() + ",ds id:" + detailDs.getId(), NCLangRes4VoTransl.getNCLangRes().getStrByID("pub", "UifDatasetAfterSelectCmd-000001"));/*查询过程出现错误*/
				}
			}
		}
		//		updateButtons();

	}
}
