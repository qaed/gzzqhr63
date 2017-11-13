package nc.impl.pub.ace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.hrwa.wa_ba_sch.ace.bp.AceWaBaSchApproveBP;
import nc.bs.hrwa.wa_ba_sch.ace.bp.AceWaBaSchDeleteBP;
import nc.bs.hrwa.wa_ba_sch.ace.bp.AceWaBaSchSendApproveBP;
import nc.bs.hrwa.wa_ba_sch.ace.bp.AceWaBaSchUnApproveBP;
import nc.bs.hrwa.wa_ba_sch.ace.bp.AceWaBaSchUnSendApproveBP;
import nc.bs.hrwa.wa_ba_sch.ace.rule.WaSchDataUniqueCheckRule;
import nc.bs.integration.workitem.util.SyncWorkitemUtil;
import nc.bs.logging.Logger;
import nc.impl.pubapp.pattern.data.bill.BillInsert;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.impl.pubapp.pattern.data.vo.VODelete;
import nc.impl.pubapp.pattern.data.vo.VOInsert;
import nc.impl.pubapp.pattern.data.vo.VOUpdate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.itf.hrwa.IWaBaUnitMaintain;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapProcessor;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.IMDPersistenceService;
import nc.message.util.MessageCenter;
import nc.message.vo.MessageVO;
import nc.message.vo.NCMessage;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.ISuperVO;
import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;
import nc.vo.wa.wa_ba.sch.WaBaSchBVO;
import nc.vo.wa.wa_ba.sch.WaBaSchTVO;
import nc.vo.wa.wa_ba.unit.AggWaBaUnitHVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public abstract class AceWaBaSchPubServiceImpl {
	private IMDPersistenceService persist = NCLocator.getInstance().lookup(IMDPersistenceService.class);
	private IMDPersistenceQueryService query = NCLocator.getInstance().lookup(IMDPersistenceQueryService.class);
	private BaseDAO dao;

	// 新增
	public AggWaBaSchHVO[] pubinsertBills(IBill[] vos) throws BusinessException {

		/*
		try {
			// 数据库中数据和前台传递过来的差异VO合并后的结果
			BillTransferTool<AggWaBaSchHVO> transferTool = new BillTransferTool<AggWaBaSchHVO>(clientFullVOs);
			// 调用BP
			AceWaBaSchInsertBP action = new AceWaBaSchInsertBP();
			AggWaBaSchHVO[] retvos = action.insert(clientFullVOs);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}*/

		BillInsert<AggWaBaSchHVO> billinsert = new BillInsert<AggWaBaSchHVO>();
		AggWaBaSchHVO[] aggvo = (AggWaBaSchHVO[]) vos;

		// 添加BP规则
		AroundProcesser<AggWaBaSchHVO> processer = new AroundProcesser<AggWaBaSchHVO>(null);
		processer.addBeforeRule(new WaSchDataUniqueCheckRule());
		processer.before(aggvo);

		// 子表的上期结余默认为0，后计算进行覆盖
		ISuperVO[] childvos = aggvo[0].getChildren(WaBaSchBVO.class);
		for (int i = 0; i < childvos.length; i++) {
			WaBaSchBVO bvo = (WaBaSchBVO) childvos[i];
			bvo.setClass1(UFDouble.ZERO_DBL);
		}
		// 主子数据插入
		AggWaBaSchHVO[] aftervo = billinsert.insert(aggvo);
		String[] bodyTableCodes = aftervo[0].getTableCodes();
		for (String bodyTabCode : bodyTableCodes) {
			// 当前页签下的多条子数据
			CircularlyAccessibleValueObject[] afterChildVOS = (aftervo[0]).getTableVO(bodyTabCode);
			for (CircularlyAccessibleValueObject childVO : afterChildVOS) {
				// 分析一条子数据
				if (bodyTabCode.equals("pk_b")) {
					WaBaSchTVO[] grandvos = (WaBaSchTVO[]) ((WaBaSchBVO) childVO).getPk_s();
					for (int i = 0; grandvos != null && i < grandvos.length; i++) {
						((WaBaSchTVO) grandvos[i]).setPk_ba_sch_unit(childVO.getPrimaryKey());
						((WaBaSchTVO) grandvos[i]).setPk_ba_sch_h(((WaBaSchBVO) childVO).getPk_ba_sch_h());
						((WaBaSchTVO) grandvos[i]).setPk_wa_ba_unit(((WaBaSchBVO) childVO).getPk_ba_sch_unit());

						persist.saveBill(grandvos[i]);
					}
				}
			}
		}
		return aftervo;
	}

	// 删除
	public void pubdeleteBills(IBill[] vos) throws BusinessException {
		try {
			BillTransferTool<AggWaBaSchHVO> transferTool = new BillTransferTool<AggWaBaSchHVO>((AggWaBaSchHVO[]) vos);
			// obtain AggVO
			AggWaBaSchHVO[] fullBills = transferTool.getClientFullInfoBill();
			// obtain ChildVO
			ISuperVO[] originChildrens = (ISuperVO[]) fullBills[0].getChildren(WaBaSchBVO.class);
			for (ISuperVO childVO : originChildrens) {
				String originChildPK = ((WaBaSchBVO) childVO).getPrimaryKey();
				// obtain GrandVO
				Collection<?> originGVOs = query.queryBillOfVOByCond(WaBaSchTVO.class, "pk_ba_sch_unit = '" + originChildPK + "'", false);
				WaBaSchTVO[] originGrandvos = originGVOs.toArray(new WaBaSchTVO[originGVOs.size()]);
				// put GrandVO to ChildVO
				((WaBaSchBVO) childVO).setPk_s(originGrandvos);
			}
			// delete ParentVO and ChildVO
			AceWaBaSchDeleteBP deleteBP = new AceWaBaSchDeleteBP();
			deleteBP.delete(fullBills);
			// delete GrandVO
			for (ISuperVO childVO : originChildrens) {
				WaBaSchTVO[] originGrandvos = ((WaBaSchBVO) childVO).getPk_s();
				for (int i = 0; originGrandvos != null && i < originGrandvos.length; i++) {
					// persist.deleteBill(originGrandvos[i]);//
					persist.deleteBillFromDB(originGrandvos[i]);
				}
			}
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// 修改
	// 参考 BillUpdate.persistent方法
	// 需要构造一个完整的fullgrandvos，和origingrandvos
	public AggWaBaSchHVO[] pubupdateBills(IBill[] vos) throws BusinessException {
		// try {
		// // 加锁 + 检查ts
		// BillTransferTool<AggWaBaSchHVO>
		// transferTool = new
		// BillTransferTool<AggWaBaSchHVO>(clientFullVOs);
		// AceWaBaSchUpdateBP bp = new
		// AceWaBaSchUpdateBP();
		// AggWaBaSchHVO[] retvos =
		// bp.update(clientFullVOs,
		// originBills);
		// // 构造返回数据
		// return
		// transferTool.getBillForToClient(retvos);
		// } catch (Exception e) {
		// ExceptionUtils.marsh(e);
		// }
		try {
			BillTransferTool<AggWaBaSchHVO> transTool = new BillTransferTool<AggWaBaSchHVO>((AggWaBaSchHVO[]) vos);
			AggWaBaSchHVO[] fullBills = transTool.getClientFullInfoBill();
			AggWaBaSchHVO[] originBills = transTool.getOriginBills();
			// 孙VO的修改
			// nc.impl.pubapp.pattern.data.vo.template.UpdateBPTemplate
			AggWaBaSchHVO[] aggvos = (AggWaBaSchHVO[]) vos;
			// 添加BP规则
			AroundProcesser<AggWaBaSchHVO> processer = new AroundProcesser<AggWaBaSchHVO>(null);
			processer.addBeforeRule(new WaSchDataUniqueCheckRule());
			processer.before(aggvos);
			//
			String[] tableCodes = originBills[0].getTableCodes();

			//start
			//fullBills和originBills的数据一样，只是数据的状态不同，更新时根据状态判断是否新增，修改，删除
			Map<String, ISuperVO> map_client = getAggvoBodyMap(fullBills[0]);//用于方便获取子表主键对应的VO
			Map<String, ISuperVO> map_aggvo = getAggvoBodyMap(aggvos[0]);//用于方便获取子表主键对应的VO

			for (Iterator<String> it = map_client.keySet().iterator(); it.hasNext();) {
				String bpk = it.next();
				ISuperVO superVO_aggvo = map_aggvo.get(bpk);
				ISuperVO superVO_client = map_client.get(bpk);
				//对旧的vo和前台传过来的vo，如果没有了就是删除了
				if (superVO_aggvo == null) {
					superVO_client.setStatus(VOStatus.DELETED);
				} else {
					//其他设为和前台vo一样的状态
					superVO_client.setStatus(superVO_aggvo.getStatus());
				}

			}
			//更新子表
			BillUpdate<AggWaBaSchHVO> billupdate = new BillUpdate<AggWaBaSchHVO>();
			fullBills = billupdate.update(fullBills, originBills);

			map_client = getAggvoBodyMap(fullBills[0]);//重新生成一下，因为新增的保存后才有pk

			for (Iterator<String> it = map_client.keySet().iterator(); it.hasNext();) {
				String bpk = it.next();
				WaBaSchBVO superVO_aggvo = (WaBaSchBVO) map_aggvo.get(bpk);
				WaBaSchBVO superVO_client = (WaBaSchBVO) map_client.get(bpk);
				if (superVO_aggvo != null) {
					//Tool生成的fullBills没有孙表，所以把前台的孙表赋值进去
					superVO_client.setPk_s(superVO_aggvo.getPk_s());
				}
			}
			//end

			Map<IVOMeta, List<ISuperVO>> fullGrandVOs = new HashMap<IVOMeta, List<ISuperVO>>();
			Map<IVOMeta, List<ISuperVO>> originGrandVOs = new HashMap<IVOMeta, List<ISuperVO>>();
			for (String tableCode : tableCodes) {
				ISuperVO[] originChildrens = (ISuperVO[]) originBills[0].getTableVO(tableCode);
				for (ISuperVO childVO : originChildrens) {
					// 将当前页签下的当前子的所有孙都查询出来了,并赋值给originBills中的孙。
					if (tableCode.equals("pk_b")) {
						String originChildPK = ((WaBaSchBVO) childVO).getPrimaryKey();
						Collection<WaBaSchTVO> originGVOs =
						//								query.queryBillOfVOByCond(WaBaSchTVO.class, "pk_ba_sch_unit = '" + originChildPK + "'", false);
								query.queryBusiVOByCond(WaBaSchTVO.class, new String[] { "wa_ba_sch_psns" }, "pk_ba_sch_unit = '" + originChildPK + "'", false, false, null);
						if (originGVOs != null && originGVOs.size() != 0) {
							WaBaSchTVO[] originGrandvos = (WaBaSchTVO[]) originGVOs.toArray(new WaBaSchTVO[originGVOs.size()]);
							((WaBaSchBVO) childVO).setPk_s(originGrandvos);
							IVOMeta meta = ((SuperVO) (originGVOs.iterator().next())).getMetaData();
							if (originGrandVOs.get(meta) == null) {
								originGrandVOs.put(meta, new ArrayList<ISuperVO>(originGVOs));
							} else {
								originGrandVOs.get(meta).addAll(originGVOs);
							}
						}
					}
				}

				ISuperVO[] currentChildrens = (ISuperVO[]) fullBills[0].getTableVO(tableCode);
				for (ISuperVO childVO : currentChildrens) {
					if (tableCode.equals("pk_b")) {

						ISuperVO[] currentGrandvos = ((WaBaSchBVO) map_client.get(childVO.getPrimaryKey())).getPk_s();
						for (int i = 0; currentGrandvos != null && i < currentGrandvos.length; i++) {
							((WaBaSchTVO) currentGrandvos[i]).setPk_ba_sch_unit(childVO.getPrimaryKey());
							((WaBaSchTVO) currentGrandvos[i]).setPk_ba_sch_h(((WaBaSchBVO) childVO).getPk_ba_sch_h());
							((WaBaSchTVO) currentGrandvos[i]).setPk_wa_ba_unit(((WaBaSchBVO) childVO).getPk_ba_sch_unit());
						}
						if (currentGrandvos != null && currentGrandvos.length != 0) {
							IVOMeta meta = ((ISuperVO) (currentGrandvos[0])).getMetaData();
							List<ISuperVO> arrayList = new ArrayList<ISuperVO>(Arrays.asList(currentGrandvos));
							if (fullGrandVOs.get(meta) == null) {
								fullGrandVOs.put(meta, arrayList);
							} else {
								fullGrandVOs.get(meta).addAll(arrayList);
							}
						}
					}
				}
			}

			fullGrandVOs = this.getFullGrandVOs(fullGrandVOs, originGrandVOs);
			this.persistent(fullGrandVOs, originGrandVOs);
			//			AceWaBaSchUpdateBP bp = new AceWaBaSchUpdateBP();
			//			AggWaBaSchHVO[] retBills = bp.update(fullBills, originBills);

			//return fullBills;
			return aggvos;
			//end
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// 参考BillIndex，结合两个vo，利用元数据库中的vo的pk在界面传来的值中查找
	private Map<IVOMeta, List<ISuperVO>> getFullGrandVOs(Map<IVOMeta, List<ISuperVO>> fullGrandVOs, Map<IVOMeta, List<ISuperVO>> originGrandVOs) {
		if (originGrandVOs == null || originGrandVOs.size() == 0)
			return fullGrandVOs;
		//
		// 应该如何获取meta？
		// 可能会有问题
		//
		for (Iterator<IVOMeta> itmeta = originGrandVOs.keySet().iterator(); itmeta.hasNext();) {
			IVOMeta meta = itmeta.next();
			List<ISuperVO> originvos = originGrandVOs.get(meta);
			if (originvos == null || originvos.size() == 0)
				continue;
			for (Iterator<ISuperVO> itvo = originvos.iterator(); itvo.hasNext();) {
				ISuperVO originvo = itvo.next();
				String pk = originvo.getPrimaryKey();
				if (pk != null) {
					ISuperVO vo = findGrandVOByPk(fullGrandVOs.get(meta), pk);
					if (vo == null) {
						originvo.setStatus(VOStatus.DELETED);
						if (fullGrandVOs.get(meta) == null || fullGrandVOs.get(meta).size() == 0) {
							List<ISuperVO> list = new ArrayList<ISuperVO>();
							list.add(originvo);
							fullGrandVOs.put(meta, list);
						} else
							fullGrandVOs.get(meta).add(originvo);
					}
				}
			}
		}
		return fullGrandVOs;
	}

	/**
	 * 用于方便获取子表主键对应的VO
	 */
	private Map<String, ISuperVO> getAggvoBodyMap(AggWaBaSchHVO aggvo) {
		CircularlyAccessibleValueObject[] aggvoChild = aggvo.getChildrenVO();
		Map<String, ISuperVO> map_aggvo = new HashMap<String, ISuperVO>();
		for (CircularlyAccessibleValueObject tmpVO : aggvoChild) {
			ISuperVO superVO = (ISuperVO) tmpVO;
			map_aggvo.put(superVO.getPrimaryKey(), superVO);
		}
		return map_aggvo;
	}

	// 参考 BillUpdate.persistent方法
	private void persistent(Map<IVOMeta, List<ISuperVO>> fullGrandVOs, Map<IVOMeta, List<ISuperVO>> originGrandVOs) {
		Map<IVOMeta, List<ISuperVO>> originIndex = new HashMap<IVOMeta, List<ISuperVO>>();
		Map<IVOMeta, List<ISuperVO>> deleteIndex = new HashMap<IVOMeta, List<ISuperVO>>();
		Map<IVOMeta, List<ISuperVO>> newIndex = new HashMap<IVOMeta, List<ISuperVO>>();
		Map<IVOMeta, List<ISuperVO>> updateIndex = new HashMap<IVOMeta, List<ISuperVO>>();
		for (List<ISuperVO> list : fullGrandVOs.values()) {
			this.process(list, originGrandVOs, originIndex, deleteIndex, newIndex, updateIndex);
		}
		this.persistent(originIndex, deleteIndex, newIndex, updateIndex);
	}

	private void process(List<ISuperVO> list, Map<IVOMeta, List<ISuperVO>> originGrandVOs, Map<IVOMeta, List<ISuperVO>> originIndex, Map<IVOMeta, List<ISuperVO>> deleteIndex, Map<IVOMeta, List<ISuperVO>> newIndex, Map<IVOMeta, List<ISuperVO>> updateIndex) {
		for (ISuperVO vo : list) {
			this.process(vo, originGrandVOs, originIndex, deleteIndex, newIndex, updateIndex);
		}
	}

	private void process(ISuperVO vo, Map<IVOMeta, List<ISuperVO>> originGrandVOs, Map<IVOMeta, List<ISuperVO>> originIndex, Map<IVOMeta, List<ISuperVO>> deleteIndex, Map<IVOMeta, List<ISuperVO>> newIndex, Map<IVOMeta, List<ISuperVO>> updateIndex) {
		IVOMeta voMeta = vo.getMetaData();
		int status = vo.getStatus();
		if (status == VOStatus.NEW) {
			List<ISuperVO> list = this.get(voMeta, newIndex);
			list.add(vo);
		} else if (status == VOStatus.UPDATED) {
			List<ISuperVO> updateList = this.get(voMeta, updateIndex);
			updateList.add(vo);
			// 根据当前vo获取原始vo
			ISuperVO originVO = this.get(originGrandVOs, vo.getMetaData(), vo.getPrimaryKey());
			List<ISuperVO> originList = this.get(voMeta, originIndex);
			originList.add(originVO);
		} else if (status == VOStatus.DELETED) {
			List<ISuperVO> list = this.get(voMeta, deleteIndex);
			list.add(vo);
		}
	}

	private void persistent(Map<IVOMeta, List<ISuperVO>> originIndex, Map<IVOMeta, List<ISuperVO>> deleteIndex, Map<IVOMeta, List<ISuperVO>> newIndex, Map<IVOMeta, List<ISuperVO>> updateIndex) {
		for (List<ISuperVO> list : deleteIndex.values()) {
			this.deleteVO(list);
		}
		for (List<ISuperVO> list : newIndex.values()) {
			this.insertVO(list);
		}
		for (Entry<IVOMeta, List<ISuperVO>> entry : updateIndex.entrySet()) {
			List<ISuperVO> list = entry.getValue();
			List<ISuperVO> originList = originIndex.get(entry.getKey());
			this.updateVO(list, originList);
		}
	}

	/**
	 * 根据孙实体元数据、孙实体主键获取实体
	 * 
	 * @param voMeta 孙实体元数据
	 * @param key 孙实体主键
	 * @return 孙实体
	 */
	public ISuperVO get(Map<IVOMeta, List<ISuperVO>> originGrandVOs, IVOMeta voMeta, String key) {
		if (originGrandVOs.containsKey(voMeta)) {
			return findGrandVOByPk(originGrandVOs.get(voMeta), key);
		}
		return null;
	}

	private ISuperVO findGrandVOByPk(List<ISuperVO> originGrandVOs, String key) {
		if (originGrandVOs == null || originGrandVOs.size() == 0)
			return null;
		Iterator<ISuperVO> it = originGrandVOs.iterator();
		while (it.hasNext()) {
			ISuperVO grandvo = it.next();
			if (grandvo.getPrimaryKey() != null && grandvo.getPrimaryKey().equals(key)) {
				return grandvo;
			}
		}
		return null;
	}

	private List<ISuperVO> get(IVOMeta voMeta, Map<IVOMeta, List<ISuperVO>> index) {
		if (index.containsKey(voMeta)) {
			return index.get(voMeta);
		}
		List<ISuperVO> list = new ArrayList<ISuperVO>();
		index.put(voMeta, list);
		return list;
	}

	private void updateVO(List<ISuperVO> list, List<ISuperVO> originList) {
		VOUpdate<ISuperVO> bo = new VOUpdate<ISuperVO>();
		int length = list.size();
		if (length > 0) {
			ISuperVO[] vos = new ISuperVO[length];
			vos = list.toArray(vos);
			ISuperVO[] originVOs = new ISuperVO[length];
			originVOs = originList.toArray(originVOs);
			bo.update(vos, originVOs);
		}
	}

	private void deleteVO(List<ISuperVO> list) {
		VODelete<ISuperVO> bo = new VODelete<ISuperVO>();
		int length = list.size();
		if (length > 0) {
			ISuperVO[] vos = new ISuperVO[length];
			vos = list.toArray(vos);
			bo.delete(vos);
		}
	}

	private void insertVO(List<ISuperVO> list) {
		VOInsert<ISuperVO> bo = new VOInsert<ISuperVO>();
		int length = list.size();
		if (length > 0) {
			ISuperVO[] vos = new ISuperVO[length];
			vos = list.toArray(vos);
			bo.insert(vos);
		}
	}

	public AggWaBaSchHVO[] pubquerybills(IQueryScheme queryScheme) throws BusinessException {
		AggWaBaSchHVO[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<AggWaBaSchHVO> query = new BillLazyQuery<AggWaBaSchHVO>(AggWaBaSchHVO.class);
			//query.setOrderAttribute(WaBaSchBHVO.class, new String[] { "cyear", "cperiod" });//这个是loadChild时调用的，需要排序子表时用
			bills = query.query(queryScheme, "order by cyear,cperiod asc");
			loadGrandData(bills);
		} catch (Exception e) {
			Logger.error(e);
			ExceptionUtils.marsh(e);
		}
		return bills;
	}

	/**
	 * 加载孙表数据
	 * 
	 * @param bills
	 * @throws BusinessException
	 */
	private void loadGrandData(AggWaBaSchHVO[] bills) throws BusinessException {
		if (bills != null && bills.length > 0) {
			for (AggWaBaSchHVO aggvo : bills) {
				WaBaSchBVO[] bvos = (WaBaSchBVO[]) aggvo.getChildren(WaBaSchBVO.class);
				if (bvos != null && bvos.length > 0) {
					for (WaBaSchBVO bvo : bvos) {
						Collection<?> gvos =
								query.queryBillOfVOByCond(WaBaSchTVO.class, "pk_ba_sch_unit = '" + bvo.getPk_ba_sch_unit() + "'", false);
						WaBaSchTVO[] tvos = gvos.toArray(new WaBaSchTVO[gvos.size()]);
						bvo.setPk_s(tvos);
					}
				}
				break;//查第一个就好了
			}
		}
	}

	/**
	 * 由子类实现，查询之前对queryScheme进行加工， 加入自己的逻辑
	 * 
	 * @param queryScheme
	 */
	protected void preQuery(IQueryScheme queryScheme) {
		// 查询之前对queryScheme进行加工，加入自己的逻辑
	}

	// 提交
	@SuppressWarnings("unchecked")
	public AggWaBaSchHVO[] pubsendapprovebills(AggWaBaSchHVO[] clientFullVOs, AggWaBaSchHVO[] originBills) throws BusinessException {
		AceWaBaSchSendApproveBP bp = new AceWaBaSchSendApproveBP();
		AggWaBaSchHVO[] retvos = bp.sendApprove(clientFullVOs, originBills);
		List<String> pkList = new ArrayList<String>();
		//添加消息推送
		sendWorkitem(retvos);
		for (AggWaBaSchHVO aggWaBaSchHVO : retvos) {
			getDao().executeUpdate("delete from wa_ba_sch_unit where dr=1 and pk_ba_sch_h='" + aggWaBaSchHVO.getParentVO().getPk_ba_sch_h() + "'");
			getDao().executeUpdate("delete from wa_ba_sch_psns where dr=1 and pk_ba_sch_h='" + aggWaBaSchHVO.getParentVO().getPk_ba_sch_h() + "'");
			pkList.add(aggWaBaSchHVO.getPrimaryKey());
		}
		return (AggWaBaSchHVO[]) (query.queryBillOfVOByPKs(AggWaBaSchHVO.class, pkList.toArray(new String[0]), false)).toArray(new AggWaBaSchHVO[0]);
	}

	// 收回
	public AggWaBaSchHVO[] pubunsendapprovebills(AggWaBaSchHVO[] clientFullVOs, AggWaBaSchHVO[] originBills) throws BusinessException {
		AceWaBaSchUnSendApproveBP bp = new AceWaBaSchUnSendApproveBP();
		AggWaBaSchHVO[] retvos = bp.unSend(clientFullVOs, originBills);
		//		StringBuilder sql = new StringBuilder();
		deleteWorkitem(clientFullVOs);//删除代办
		for (AggWaBaSchHVO aggWaBaSchHVO : clientFullVOs) {
			for (ISuperVO bodyvo : aggWaBaSchHVO.getChildren(WaBaSchBVO.class)) {
				WaBaSchBVO bvo = (WaBaSchBVO) bodyvo;
				if (StringUtils.isEmpty(bvo.getVdef1())) {//没有当前分配人，没有代办
					continue;
				}
				//清空当前分配人pk
				getDao().executeUpdate("update wa_ba_sch_unit set vdef1=null where pk_ba_sch_unit='" + bvo.getPk_ba_sch_unit() + "'");
			}
		}
		return retvos;
	};

	// 审批
	public AggWaBaSchHVO[] pubapprovebills(AggWaBaSchHVO[] clientFullVOs, AggWaBaSchHVO[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AceWaBaSchApproveBP bp = new AceWaBaSchApproveBP();
		AggWaBaSchHVO[] retvos = bp.approve(clientFullVOs, originBills);
		return retvos;
	}

	// 弃审
	public AggWaBaSchHVO[] pubunapprovebills(AggWaBaSchHVO[] clientFullVOs, AggWaBaSchHVO[] originBills) throws BusinessException {

		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AceWaBaSchUnApproveBP bp = new AceWaBaSchUnApproveBP();
		AggWaBaSchHVO[] retvos = bp.unApprove(clientFullVOs, originBills);
		return retvos;
	}

	public AggWaBaSchHVO[] caculate(IBill[] vos) throws BusinessException {
		AggWaBaSchHVO[] aggvo = (AggWaBaSchHVO[]) vos;
		if (aggvo != null && aggvo.length > 0) {
			AceWaBaItemDataPubServiceImpl dataServer =
					new AceWaBaItemDataPubServiceImpl(aggvo[0].getChildren(WaBaSchBVO.class), aggvo[0].getParentVO().getCyear(), aggvo[0].getParentVO().getCperiod());
			dataServer.doCaculate();

		}
		return aggvo;
	}

	/**
	 * @return dao
	 */
	private BaseDAO getDao() {
		if (this.dao == null) {
			this.dao = new BaseDAO();
		}
		return this.dao;
	}

	/**
	 * 强制分配
	 * 
	 * @param aggvo
	 * @return
	 * @throws BusinessException
	 */
	public AggWaBaSchHVO forceComplete(AggWaBaSchHVO aggvo) throws BusinessException {
		deleteWorkitem(aggvo);
		//		VOUpdate<WaBaSchBVO> voupdate = new VOUpdate<WaBaSchBVO>();
		WaBaSchBVO[] bvos = (WaBaSchBVO[]) aggvo.getChildren(WaBaSchBVO.class);

		for (WaBaSchBVO bvo : bvos) {
			if (StringUtils.isEmpty(bvo.getVdef1())) {//没有分配人，说明分配完成
				continue;
			}

			if (bvo.getClass3() == null) {
				bvo.setClass3(bvo.getClass2());//当期计划分配金额
				bvo.setClass4(bvo.getClass1());//上月的结余转到本月结余
			}
			bvo.setVdef1(null);
			bvo.setDr(0);//必须重新写，否则会被置为null
			//			voupdate.update(bvo, new String[] { "class3","class4","vdef1" });//更新特定字段
			getDao().updateVO(bvo, new String[] { "class3", "class4", "vdef1" });
		}
		return query.queryBillOfVOByPK(AggWaBaSchHVO.class, aggvo.getPrimaryKey(), false);
	}

	/**
	 * 删除OA代办及NC代办
	 * 
	 * @param aggvos
	 * @throws BusinessException
	 */
	private void deleteWorkitem(AggWaBaSchHVO[] aggvos) throws BusinessException {
		if (!ArrayUtils.isEmpty(aggvos)) {
			for (AggWaBaSchHVO aggvo : aggvos) {
				deleteWorkitem(aggvo);
			}
		}
	}

	/**
	 * 删除OA代办及NC代办
	 * 
	 * @param aggvo
	 * @throws BusinessException
	 */
	public void deleteWorkitem(AggWaBaSchHVO aggvo) throws BusinessException {
		if (aggvo != null) {
			WaBaSchBVO[] bvos = (WaBaSchBVO[]) aggvo.getChildren(WaBaSchBVO.class);
			if (!ArrayUtils.isEmpty(bvos)) {
				deleteWorkitem(bvos);
			}
		}
	}

	/**
	 * 删除OA代办及NC代办
	 * 
	 * @param bvos
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	private void deleteWorkitem(WaBaSchBVO[] bvos) throws BusinessException {
		StringBuilder sql = new StringBuilder();
		if (!ArrayUtils.isEmpty(bvos)) {
			for (WaBaSchBVO bvo : bvos) {
				if (StringUtils.isEmpty(bvo.getVdef1())) {
					//没有当前分配人，说明已分配完成，没有代办
					continue;
				}
				sql.delete(0, sql.length());
				//获取当前分配人的id及code
				sql.append("select cuserid,user_code from sm_user where pk_psndoc='" + bvo.getVdef1() + "'");
				Map<String, String> userMap = (Map<String, String>) getDao().executeQuery(sql.toString(), new MapProcessor());
				if (userMap != null) {
					//删除OA代办
					SyncWorkitemUtil.getExternalWorkitemManager().deleteWorkitem("NC63HR", null, bvo.getPk_ba_sch_unit(), "HRWA", null, null, "{\"LoginName\":\"" + userMap.get("user_code") + "\"}", null, Integer.valueOf(1));
					//查询对应的奖金单元
					sql.delete(0, sql.length());
					sql.append("select name from wa_ba_unit where pk_wa_ba_unit='" + bvo.getBa_unit_code() + "'");
					String unitName = (String) getDao().executeQuery(sql.toString(), new ColumnProcessor());
					//删除NC代办
					sql.delete(0, sql.length());
					sql.append("delete from sm_msg_content where  detail like '" + bvo.getPk_ba_sch_h() + "@BAAL%' and receiver='" + userMap.get("cuserid") + "' and subject like '%" + unitName + "%'");
					getDao().executeUpdate(sql.toString());
				}

			}
		}

	}

	/**
	 * 发送OA及NC代办
	 * 
	 * @param aggvos
	 * @throws BusinessException
	 */
	private void sendWorkitem(AggWaBaSchHVO[] aggvos) throws BusinessException {
		if (!ArrayUtils.isEmpty(aggvos)) {
			for (AggWaBaSchHVO aggvo : aggvos) {
				sendWorkitem(aggvo);
			}
		}
	}

	/**
	 * 发送OA及NC代办
	 * 
	 * @param aggvo
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	private void sendWorkitem(AggWaBaSchHVO aggvo) throws BusinessException {
		IWaBaUnitMaintain UnitMaintain = NCLocator.getInstance().lookup(IWaBaUnitMaintain.class);

		ISuperVO[] vos = aggvo.getChildren(WaBaSchBVO.class);
		for (int i = 0; i < vos.length; i++) {
			WaBaSchBVO bvo = (WaBaSchBVO) vos[i];
			Object[] aggunitvos = UnitMaintain.query("pk_wa_ba_unit='" + bvo.getBa_unit_code() + "'");
			if (aggunitvos.length == 0 || aggunitvos[0] == null) {
				continue;
			}
			//插入代办
			//				VOInsert<WorkflownoteVO> voinsert = new VOInsert<WorkflownoteVO>();
			WorkflownoteVO workflownoteVO = new WorkflownoteVO();
			workflownoteVO.setBillid(aggvo.getParentVO().getPk_ba_sch_h());
			workflownoteVO.setBillno(aggvo.getParentVO().getSch_code());
			workflownoteVO.setUserobject(null);
			workflownoteVO.setWorkflow_type(2);
			getDao().insertVO(workflownoteVO);
			//构造消息
			NCMessage[] ncmsg = new NCMessage[1];
			MessageVO msg = new MessageVO();
			//				IUAPQueryBS queryBS = NCLocator.getInstance().lookup(IUAPQueryBS.class);
			//				List<UserVO> uservolist = (List<UserVO>) queryBS.retrieveByClause(UserVO.class, "pk_psndoc='"+((AggWaBaUnitHVO)aggunitvos[0]).getParentVO().getBa_mng_psnpk()+"'");
			//查接收人
			SQLParameter parameter = new SQLParameter();
			parameter.clearParams();
			parameter.addParam(((AggWaBaUnitHVO) aggunitvos[0]).getParentVO().getBa_mng_psnpk());
			Map<String, String> receiverMap =
					(Map<String, String>) getDao().executeQuery("select sm_user.cuserid,sm_user.user_code from sm_user where pk_psndoc=?", parameter, new MapProcessor());
			if (receiverMap == null) {
				throw new BusinessException("当前分配人用户不存在，请检查。");
			}
			//查创建人姓名
			parameter.clearParams();
			parameter.addParam(aggvo.getParentVO().getCreator());
			Map<String, String> creatorNameMap =
					(Map<String, String>) getDao().executeQuery("select user_name,user_code from sm_user where sm_user.cuserid=?", parameter, new MapProcessor());
			//构造消息
			msg.setSender(aggvo.getParentVO().getCreator());//发送人
			msg.setReceiver(receiverMap.get("cuserid"));//接受人
			msg.setMsgsourcetype("worklist");//消息来源类型
			msg.setPriority(5);//优先级
			msg.setSendtime(new UFDateTime());//发送信息时间
			String title =
					"请审批 " + creatorNameMap.get("user_name") + " 发起的 " + ((AggWaBaUnitHVO) aggunitvos[0]).getParentVO().getName() + "_" + aggvo.getParentVO().getSch_name();
			msg.setSubject(title);//标题
			msg.setPk_group(aggvo.getParentVO().getPk_group());
			msg.setPk_detail(workflownoteVO.getPrimaryKey());
			msg.setPk_org(aggvo.getParentVO().getPk_org());
			msg.setDestination("inbox");
			msg.setDetail(aggvo.getParentVO().getPk_ba_sch_h() + "@" + aggvo.getParentVO().getBilltype() + "@" + aggvo.getParentVO().getSch_code());//详细信息
			msg.setContenttype("~");
			//msg.setDomainflag("AUM");
			ncmsg[0] = new NCMessage();
			ncmsg[0].setMessage(msg);
			//保存当前分配人pk,同时将当前已分配，本期结余置为空
			getDao().executeUpdate("update wa_ba_sch_unit set vdef1='" + ((AggWaBaUnitHVO) aggunitvos[0]).getParentVO().getBa_mng_psnpk() + "',class3=null,class4=null where pk_ba_sch_unit='" + bvo.getPk_ba_sch_unit() + "'");
			//OA打开的链接
			StringBuilder link = new StringBuilder();
			link.append("/portal?returnUrl=");
			link.append("/portal/");
			link.append("app/hrss_wabasch");
			link.append("?nodecode=").append("E60135010");
			link.append("%26model=").append("nc.bs.hrss.pub.pf.WebBillApprovePageMode");
			link.append("%26NC=Y%26pf_bill_editable=Y");
			link.append("%26billType=").append("BAAL");
			link.append("%26billTypeCode=").append("BAAL");
			link.append("%26openBillId=").append(aggvo.getPrimaryKey());
			link.append("%26state=State_Run");
			//发送OA代办
			SyncWorkitemUtil.getExternalWorkitemManager().beginWorkitem("NC63HR", new UFDateTime().toStdString(), "{\"LoginName\":\"" + creatorNameMap.get("user_code") + "\"}", null, null, null, link.toString(), bvo.getPk_ba_sch_unit(), "HRWA", null, null, null, title, "{\"LoginName\":\"" + receiverMap.get("user_code") + "\"}", Integer.valueOf(1));
			try {
				MessageCenter.sendMessage(ncmsg);
			} catch (Exception e) {
				throw new BusinessException(e);
			}
		}
	}

}