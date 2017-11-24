package nc.impl.pub.ace;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrwa.wa_ba_item.ace.rule.WaItemDataUniqueCheckRule;
import nc.bs.hrwa.wa_ba_item.ace.rule.WaItemDeleteCheckRule;
import nc.impl.pubapp.pattern.data.vo.VOInsert;
import nc.impl.pubapp.pattern.data.vo.VOQuery;
import nc.impl.pubapp.pattern.data.vo.VOUpdate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.IMDPersistenceService;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pubapp.AppContext;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.wa.wa_ba.item.ItemsVO;

public abstract class AceWa_ba_itemPubServiceImpl {
	private IMDPersistenceService persist = NCLocator.getInstance().lookup(IMDPersistenceService.class);
	IMDPersistenceQueryService query = NCLocator.getInstance().lookup(IMDPersistenceQueryService.class);

	// 增加方法
	public ItemsVO inserttreeinfo(ItemsVO vo) throws BusinessException {
		try {
			// 添加BP规则
			AroundProcesser<ItemsVO> processer = new AroundProcesser<ItemsVO>(null);
			processer.addBeforeRule(new WaItemDataUniqueCheckRule());
			processer.before(new ItemsVO[] { vo });
			VOInsert<ItemsVO> ins = new VOInsert<ItemsVO>();
			ItemsVO[] superVOs = ins.insert(new ItemsVO[] { vo });
			return superVOs[0];
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// 删除方法
	public void deletetreeinfo(ItemsVO vo) throws BusinessException {
		try {
			// 添加BP规则
			AroundProcesser<ItemsVO> processer = new AroundProcesser<ItemsVO>(null);
			processer.addBeforeRule(new WaItemDeleteCheckRule());
			processer.before(new ItemsVO[] { vo });
			persist.deleteBillFromDB(vo);
			//			VODelete<ItemsVO> voDel = new VODelete<ItemsVO>();
			//			voDel.delete(new ItemsVO[] { vo });
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}

	}

	// 修改方法
	public ItemsVO updatetreeinfo(ItemsVO vo) throws BusinessException {
		try {
			// 添加BP规则
			AroundProcesser<ItemsVO> processer = new AroundProcesser<ItemsVO>(null);
			ItemsVO[] originVOs = this.getTreeCardVOs(new ItemsVO[] { vo });
			processer.before(new ItemsVO[] { vo });
			// 设置修改人和时间
			vo.setModifier(AppContext.getInstance().getPkUser());
			vo.setModifiedtime(new UFDateTime());
			VOUpdate<ItemsVO> upd = new VOUpdate<ItemsVO>();
			ItemsVO[] superVOs = upd.update(new ItemsVO[] { vo }, originVOs);
			return superVOs[0];
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	private ItemsVO[] getTreeCardVOs(ItemsVO[] vos) {
		String[] ids = this.getIDS(vos);
		VOQuery<ItemsVO> query = new VOQuery<ItemsVO>(ItemsVO.class);
		return query.query(ids);
	}

	private String[] getIDS(ItemsVO[] vos) {
		int size = vos.length;
		String[] ids = new String[size];
		for (int i = 0; i < size; i++) {
			ids[i] = vos[i].getPrimaryKey();
		}
		return ids;
	}

	// 查询方法
	public ItemsVO[] querytreeinfo(String whereSql) throws BusinessException {
		VOQuery<ItemsVO> query = new VOQuery<ItemsVO>(ItemsVO.class);
		return query.query(whereSql, null);
	}
}