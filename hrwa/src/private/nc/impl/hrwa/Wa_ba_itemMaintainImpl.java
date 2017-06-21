package nc.impl.hrwa;

import nc.impl.pub.ace.AceWa_ba_itemPubServiceImpl;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.vo.wa.wa_ba.item.ItemsVO;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.query2.sql.process.QuerySchemeProcessor;
import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.pubapp.bill.pagination.PaginationTransferObject;
import nc.vo.pubapp.bill.pagination.util.PaginationUtils;
import nc.impl.pubapp.pattern.data.bill.BillQuery;

public class Wa_ba_itemMaintainImpl extends AceWa_ba_itemPubServiceImpl implements nc.itf.hrwa.IWa_ba_itemMaintain {

      @Override
    public void delete(ItemsVO vos) throws BusinessException {
        super.deletetreeinfo(vos);
    }
  
      @Override
    public ItemsVO insert(ItemsVO vos) throws BusinessException {
        return super.inserttreeinfo(vos);
    }
    
      @Override
    public ItemsVO update(ItemsVO vos) throws BusinessException {
        return super.updatetreeinfo(vos);    
    }
  
      @Override
    public ItemsVO[] query(String whereSql)
        throws BusinessException {
        return super.querytreeinfo(whereSql);
    }

  
}
