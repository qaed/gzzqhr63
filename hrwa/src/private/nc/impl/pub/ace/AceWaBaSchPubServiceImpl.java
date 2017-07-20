package nc.impl.pub.ace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrwa.wa_ba_sch.ace.bp.AceWaBaSchApproveBP;
import nc.bs.hrwa.wa_ba_sch.ace.bp.AceWaBaSchDeleteBP;
import nc.bs.hrwa.wa_ba_sch.ace.bp.AceWaBaSchSendApproveBP;
import nc.bs.hrwa.wa_ba_sch.ace.bp.AceWaBaSchUnApproveBP;
import nc.bs.hrwa.wa_ba_sch.ace.bp.AceWaBaSchUnSendApproveBP;
import nc.bs.hrwa.wa_ba_sch.ace.bp.AceWaBaSchUpdateBP;
import nc.bs.hrwa.wa_ba_sch.ace.rule.GenWaBonusRule;
import nc.bs.hrwa.wa_ba_unit.ace.rule.WaUnitDataIsNotUsedRule;
import nc.impl.pubapp.pattern.data.bill.BillInsert;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.BillQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.impl.pubapp.pattern.data.vo.VODelete;
import nc.impl.pubapp.pattern.data.vo.VOInsert;
import nc.impl.pubapp.pattern.data.vo.VOUpdate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.md.model.MetaDataException;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.IMDPersistenceService;
import nc.ui.pubapp.uif2app.query2.action.QueryExecutor;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.ISuperVO;
import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;
import nc.vo.wa.wa_ba.sch.WaBaSchBVO;
import nc.vo.wa.wa_ba.sch.WaBaSchTVO;
import nc.vo.wa.wa_ba.unit.AggWaBaUnitHVO;

public abstract class AceWaBaSchPubServiceImpl {
	IMDPersistenceService persist = NCLocator.getInstance().lookup(IMDPersistenceService.class);
	IMDPersistenceQueryService query = NCLocator.getInstance().lookup(IMDPersistenceQueryService.class);

	// ����
	public AggWaBaSchHVO[] pubinsertBills(IBill[] vos) throws BusinessException {
		// TODO ����߼��Բ���
		// try {
		// // ���ݿ������ݺ�ǰ̨���ݹ����Ĳ���VO�ϲ���Ľ��
		// BillTransferTool<AggWaBaSchHVO>
		// transferTool = new
		// BillTransferTool<AggWaBaSchHVO>(clientFullVOs);
		// // ����BP
		// AceWaBaSchInsertBP action =
		// new AceWaBaSchInsertBP();
		// AggWaBaSchHVO[] retvos =
		// action.insert(clientFullVOs);
		// // ���췵������
		// return
		// transferTool.getBillForToClient(retvos);
		// } catch (Exception e) {
		// ExceptionUtils.marsh(e);
		// }
		BillInsert<AggWaBaSchHVO> billinsert = new BillInsert<AggWaBaSchHVO>();
		AggWaBaSchHVO[] aggvo = (AggWaBaSchHVO[]) vos;
		// �������ݲ���
		AggWaBaSchHVO[] aftervo = billinsert.insert(aggvo);
		String[] bodyTableCodes = aftervo[0].getTableCodes();
		for (String bodyTabCode : bodyTableCodes) {
			// ��ǰҳǩ�µĶ���������
			CircularlyAccessibleValueObject[] afterChildVOS = (aftervo[0]).getTableVO(bodyTabCode);
			for (CircularlyAccessibleValueObject childVO : afterChildVOS) {
				// ����һ��������
				if (bodyTabCode.equals("pk_b")) {
					WaBaSchTVO[] grandvos = (WaBaSchTVO[]) ((WaBaSchBVO) childVO).getPk_s();
					for (int i = 0; grandvos != null && i < grandvos.length; i++) {
						((WaBaSchTVO) grandvos[i]).setPk_ba_sch_unit(childVO.getPrimaryKey());
						((WaBaSchTVO) grandvos[i]).setPk_ba_sch_unit(((WaBaSchBVO) childVO).getPk_ba_sch_h());
						persist.saveBill(grandvos[i]);
					}
				}
			}
		}
		return aftervo;
	}

	// ɾ��
	public void pubdeleteBills(IBill[] vos) throws BusinessException {
		try {
			BillTransferTool<AggWaBaSchHVO> transferTool = new BillTransferTool<AggWaBaSchHVO>((AggWaBaSchHVO[]) vos);
			AggWaBaSchHVO[] fullBills = transferTool.getClientFullInfoBill();
			String[] tableCodes = fullBills[0].getTableCodes();
			for (String tableCode : tableCodes) {
				SuperVO[] originChildrens = (SuperVO[]) fullBills[0].getTableVO(tableCode);
				// ע����ӶԶ���,map��Ҫ����
				Map<String, SuperVO[]> originGrandMap = new HashMap<String, SuperVO[]>();
				for (SuperVO childVO : originChildrens) {
					// ����ǰҳǩ�µĵ�ǰ�ӵ������ﶼ��ѯ������,����ֵ��originBills�е��
					if (tableCode.equals("pk_b")) {
						String originChildPK = ((WaBaSchBVO) childVO).getPrimaryKey();
						Collection originGVOs =
								query.queryBillOfVOByCond(WaBaSchTVO.class, "pk_ba_sch_unit = '" + originChildPK + "'", false);
						WaBaSchTVO[] originGrandvos = (WaBaSchTVO[]) originGVOs.toArray(new WaBaSchTVO[originGVOs.size()]);
						((WaBaSchBVO) childVO).setPk_s(originGrandvos);
						for (int i = 0; originGrandvos != null && i < originGrandvos.length; i++) {
							persist.deleteBill(originGrandvos[i]);
							// persist.deleteBillFromDB(originGrandvos[i]);
						}
					}
				}
			}
			AceWaBaSchDeleteBP deleteBP = new AceWaBaSchDeleteBP();
			deleteBP.delete(fullBills);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// �޸�
	// �ο� BillUpdate.persistent����
	// ��Ҫ����һ��������fullgrandvos����origingrandvos
	public AggWaBaSchHVO[] pubupdateBills(IBill[] vos) throws BusinessException {
		// try {
		// // ���� + ���ts
		// BillTransferTool<AggWaBaSchHVO>
		// transferTool = new
		// BillTransferTool<AggWaBaSchHVO>(clientFullVOs);
		// AceWaBaSchUpdateBP bp = new
		// AceWaBaSchUpdateBP();
		// AggWaBaSchHVO[] retvos =
		// bp.update(clientFullVOs,
		// originBills);
		// // ���췵������
		// return
		// transferTool.getBillForToClient(retvos);
		// } catch (Exception e) {
		// ExceptionUtils.marsh(e);
		// }
		try {
			BillTransferTool<AggWaBaSchHVO> transTool = new BillTransferTool<AggWaBaSchHVO>((AggWaBaSchHVO[]) vos);
			AggWaBaSchHVO[] fullBills = transTool.getClientFullInfoBill();
			AggWaBaSchHVO[] originBills = transTool.getOriginBills();
			// ��VO���޸�
			// nc.impl.pubapp.pattern.data.vo.template.UpdateBPTemplate
			AggWaBaSchHVO[] aggvos = (AggWaBaSchHVO[]) vos;
			String[] tableCodes = originBills[0].getTableCodes();
			Map<IVOMeta, List<ISuperVO>> fullGrandVOs = new HashMap<IVOMeta, List<ISuperVO>>();
			Map<IVOMeta, List<ISuperVO>> originGrandVOs = new HashMap<IVOMeta, List<ISuperVO>>();
			for (String tableCode : tableCodes) {
				SuperVO[] originChildrens = (SuperVO[]) originBills[0].getTableVO(tableCode);
				for (SuperVO childVO : originChildrens) {
					// ����ǰҳǩ�µĵ�ǰ�ӵ������ﶼ��ѯ������,����ֵ��originBills�е��
					if (tableCode.equals("pk_b")) {
						String originChildPK = ((WaBaSchBVO) childVO).getPrimaryKey();
						Collection originGVOs =
								query.queryBillOfVOByCond(WaBaSchTVO.class, "pk_ba_sch_unit = '" + originChildPK + "'", false);
						if (originGVOs != null && originGVOs.size() != 0) {
							WaBaSchTVO[] originGrandvos = (WaBaSchTVO[]) originGVOs.toArray(new WaBaSchTVO[originGVOs.size()]);
							((WaBaSchBVO) childVO).setPk_s(originGrandvos);
							IVOMeta meta = ((SuperVO) (originGVOs.iterator().next())).getMetaData();
							if (originGrandVOs.get(meta) == null) {
								originGrandVOs.put(meta, (List<ISuperVO>) originGVOs);
							} else {
								originGrandVOs.get(meta).addAll(originGVOs);
							}
						}
					}
				}
				SuperVO[] currentChildrens = (SuperVO[]) aggvos[0].getTableVO(tableCode);
				for (SuperVO childVO : currentChildrens) {
					if (tableCode.equals("pk_b")) {
						ISuperVO[] currentGrandvos = (WaBaSchTVO[]) ((WaBaSchBVO) childVO).getPk_s();
						for (int i = 0; currentGrandvos != null && i < currentGrandvos.length; i++) {
							((WaBaSchTVO) currentGrandvos[i]).setPk_ba_sch_unit(childVO.getPrimaryKey());
						}
						if (currentGrandvos != null && currentGrandvos.length != 0) {
							IVOMeta meta = ((SuperVO) (currentGrandvos[0])).getMetaData();
							List arrayList = new ArrayList(Arrays.asList(currentGrandvos));
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
			AceWaBaSchUpdateBP bp = new AceWaBaSchUpdateBP();
			AggWaBaSchHVO[] retBills = bp.update(fullBills, originBills);
			return aggvos;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// �ο�BillIndex���������vo������Ԫ���ݿ��е�vo��pk�ڽ��洫����ֵ�в���
	private Map<IVOMeta, List<ISuperVO>> getFullGrandVOs(Map<IVOMeta, List<ISuperVO>> fullGrandVOs, Map<IVOMeta, List<ISuperVO>> originGrandVOs) {
		if (originGrandVOs == null || originGrandVOs.size() == 0)
			return fullGrandVOs;
		//
		// Ӧ����λ�ȡmeta��
		// ���ܻ�������
		//
		for (Iterator itmeta = originGrandVOs.keySet().iterator(); itmeta.hasNext();) {
			IVOMeta meta = (IVOMeta) itmeta.next();
			List<ISuperVO> originvos = originGrandVOs.get(meta);
			if (originvos == null || originvos.size() == 0)
				continue;
			for (Iterator itvo = originvos.iterator(); itvo.hasNext();) {
				ISuperVO originvo = (ISuperVO) itvo.next();
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

	// �ο� BillUpdate.persistent����
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
			// ���ݵ�ǰvo��ȡԭʼvo
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
	 * ������ʵ��Ԫ���ݡ���ʵ��������ȡʵ��
	 * 
	 * @param voMeta ��ʵ��Ԫ����
	 * @param key ��ʵ������
	 * @return ��ʵ��
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
		Iterator it = originGrandVOs.iterator();
		while (it.hasNext()) {
			SuperVO grandvo = (SuperVO) it.next();
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
			bills = query.query(queryScheme, null);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return bills;
	}

	/**
	 * �����������
	 * 
	 * @param bills
	 * @throws BusinessException
	 */
	private void loadGrandData(AggWaBaSchHVO[] bills) throws BusinessException {
		// TODO �Զ����ɵķ������
		if (bills != null && bills.length > 0) {
			for (AggWaBaSchHVO aggvo : bills) {
				WaBaSchBVO[] bvos = (WaBaSchBVO[]) aggvo.getChildren(WaBaSchBVO.class);
				if (bvos != null && bvos.length > 0) {
					for (WaBaSchBVO bvo : bvos) {
						Collection gvos =
								query.queryBillOfVOByCond(WaBaSchTVO.class, "pk_ba_sch_unit = '" + bvo.getPk_ba_sch_unit() + "'", false);
						WaBaSchTVO[] tvos = (WaBaSchTVO[]) gvos.toArray(new WaBaSchTVO[gvos.size()]);
						bvo.setPk_s(tvos);
					}
				}
			}
		}
	}

	/**
	 * ������ʵ�֣���ѯ֮ǰ��queryScheme���мӹ���
	 * �����Լ����߼�
	 * 
	 * @param queryScheme
	 */
	protected void preQuery(IQueryScheme queryScheme) {
		// ��ѯ֮ǰ��queryScheme���мӹ��������Լ����߼�
	}

	// �ύ
	public AggWaBaSchHVO[] pubsendapprovebills(AggWaBaSchHVO[] clientFullVOs, AggWaBaSchHVO[] originBills) throws BusinessException {
		AceWaBaSchSendApproveBP bp = new AceWaBaSchSendApproveBP();
		AggWaBaSchHVO[] retvos = bp.sendApprove(clientFullVOs, originBills);
		return retvos;
	}

	// �ջ�
	public AggWaBaSchHVO[] pubunsendapprovebills(AggWaBaSchHVO[] clientFullVOs, AggWaBaSchHVO[] originBills) throws BusinessException {
		AceWaBaSchUnSendApproveBP bp = new AceWaBaSchUnSendApproveBP();
		AggWaBaSchHVO[] retvos = bp.unSend(clientFullVOs, originBills);
		return retvos;
	};

	// ����
	public AggWaBaSchHVO[] pubapprovebills(AggWaBaSchHVO[] clientFullVOs, AggWaBaSchHVO[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AceWaBaSchApproveBP bp = new AceWaBaSchApproveBP();
		AggWaBaSchHVO[] retvos = bp.approve(clientFullVOs, originBills);
		return retvos;
	}

	// ����
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
					new AceWaBaItemDataPubServiceImpl((WaBaSchBVO[]) aggvo[0].getChildren(WaBaSchBVO.class));
			dataServer.doCaculate();

		}
		return aggvo;
	}
}