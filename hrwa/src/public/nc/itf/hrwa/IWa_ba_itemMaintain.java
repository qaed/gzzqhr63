package nc.itf.hrwa;

import nc.vo.wa.wa_ba.item.ItemsVO;
import nc.vo.pub.BusinessException;

public interface IWa_ba_itemMaintain {

	public void delete(ItemsVO vo) throws BusinessException;

	public ItemsVO insert(ItemsVO vo) throws BusinessException;

	public ItemsVO update(ItemsVO vo) throws BusinessException;

	public ItemsVO[] query(String whereSql) throws BusinessException;
}
