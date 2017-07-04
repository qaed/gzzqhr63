package nc.impl.pub.ace;

import nc.vo.wa.wa_ba.unit.WaBaUnitHVO;
import nc.impl.pubapp.pattern.data.vo.VODelete;
import nc.impl.pubapp.pattern.data.vo.VOInsert;
import nc.impl.pubapp.pattern.data.vo.VOQuery;
import nc.impl.pubapp.pattern.data.vo.VOUpdate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

/**
 * ��������� @see nc.impl.hrwa.WaBaUnitMaintainImpl,�����˷���
 * 
 * @author tsheay
 * 
 */
public abstract class AceWaBaUnitPubServiceImpl {
	// ���ӷ���
	public WaBaUnitHVO inserttreeinfo(WaBaUnitHVO vo) throws BusinessException {
		try {
			// ���BP����
			AroundProcesser<WaBaUnitHVO> processer = new AroundProcesser<WaBaUnitHVO>(null);
			processer.before(new WaBaUnitHVO[] { vo });
			VOInsert<WaBaUnitHVO> ins = new VOInsert<WaBaUnitHVO>();
			WaBaUnitHVO[] superVOs = ins.insert(new WaBaUnitHVO[] { vo });
			return superVOs[0];
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// ɾ������
	public void deletetreeinfo(WaBaUnitHVO vo) throws BusinessException {
		try {
			// ���BP����
			AroundProcesser<WaBaUnitHVO> processer = new AroundProcesser<WaBaUnitHVO>(null);
			processer.before(new WaBaUnitHVO[] { vo });
			VODelete<WaBaUnitHVO> voDel = new VODelete<WaBaUnitHVO>();
			voDel.delete(new WaBaUnitHVO[] { vo });
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// �޸ķ���
	public WaBaUnitHVO updatetreeinfo(WaBaUnitHVO vo) throws BusinessException {
		try {
			// ���BP����
			AroundProcesser<WaBaUnitHVO> processer = new AroundProcesser<WaBaUnitHVO>(null);
			WaBaUnitHVO[] originVOs = this.getTreeCardVOs(new WaBaUnitHVO[] { vo });
			processer.before(new WaBaUnitHVO[] { vo });
			VOUpdate<WaBaUnitHVO> upd = new VOUpdate<WaBaUnitHVO>();
			WaBaUnitHVO[] superVOs = upd.update(new WaBaUnitHVO[] { vo }, originVOs);
			return superVOs[0];
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	private WaBaUnitHVO[] getTreeCardVOs(WaBaUnitHVO[] vos) {
		String[] ids = this.getIDS(vos);
		VOQuery<WaBaUnitHVO> query = new VOQuery<WaBaUnitHVO>(WaBaUnitHVO.class);
		return query.query(ids);
	}

	private String[] getIDS(WaBaUnitHVO[] vos) {
		int size = vos.length;
		String[] ids = new String[size];
		for (int i = 0; i < size; i++) {
			ids[i] = vos[i].getPrimaryKey();
		}
		return ids;
	}

	// ��ѯ����
	public WaBaUnitHVO[] querytreeinfo(String whereSql) throws BusinessException {
		VOQuery<WaBaUnitHVO> query = new VOQuery<WaBaUnitHVO>(WaBaUnitHVO.class);
		return query.query(whereSql, null);
	}
}