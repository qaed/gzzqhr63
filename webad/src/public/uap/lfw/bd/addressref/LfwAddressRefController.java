package uap.lfw.bd.addressref;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.itf.org.IOrgConst;
import nc.uap.cpb.log.CpLogger;
import nc.uap.cpb.persist.dao.PtBaseDAO;
import nc.uap.ctrl.tpl.IAddressService;
import nc.uap.ctrl.tpl.exp.TplBusinessException;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.cmd.UifPlugoutCmd;
import nc.uap.lfw.core.cmd.UifSaveCmd;
import nc.uap.lfw.core.constants.ReferenceConstants;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ViewContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DialogEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.event.ScriptEvent;
import nc.uap.lfw.core.exception.LfwRuntimeException;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.refnode.IRefNode;
import nc.uap.lfw.core.refnode.LfwRefNode;
import nc.uap.lfw.core.refnode.RefNode;
import nc.uap.lfw.core.vo.LfwExAggVO;
import nc.uap.lfw.format.LfwFormater;
import nc.vo.bd.address.AddressFormatVO;
import nc.vo.bd.address.AddressVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.format.FormatResult;
import nc.vo.pub.format.exception.FormatException;

import org.apache.commons.lang.StringUtils;

import uap.lfw.ref.ctrl.LfwSqlReferenceController;
import uap.lfw.ref.sqlvo.ILfwRefSqlVO;
import uap.lfw.ref.util.LfwReferenceUtil;

public class LfwAddressRefController extends LfwSqlReferenceController {

	@Override
	public void getExtendsParam(ScriptEvent e) {
	}

	@Override
	protected ILfwRefSqlVO getMainRefSqlVO() {
		return null;
	}

	@Override
	protected String getMainDatasetId() {
		return null;
	}

	public void editBeforeShow(DialogEvent dialogEvent) {
		LfwView view = dialogEvent.getSource();
		if (view != null) {
			Dataset ds = view.getViewModels().getDataset(
					AddressRefModelConstants.AddressRefModel_MAIN_DSID);
			if (ds != null) {
				String pk_address = null;
				String pk = LfwRuntimeEnvironment.getWebContext().getWebSession().getOriginalParameter("pk");
				if(StringUtils.isNotBlank(pk)){
					pk_address = pk;
				}
				AddressVO vo = null;
				if(pk_address!=null){
					try{
						vo = NCLocator.getInstance().lookup(IAddressService.class).getAddressVOByPk(pk_address);
					}catch(TplBusinessException e){
						CpLogger.error(e);
						throw new LfwRuntimeException(e.getMessage());
					}
				}
				ds.clear();
				Row row = ds.getEmptyRow();
				if(vo!=null){
					row.setValue(ds.nameToIndex("country"), vo.getCountry());
					row.setValue(ds.nameToIndex("province"), vo.getProvince());
					row.setValue(ds.nameToIndex("city"), vo.getCity());
					row.setValue(ds.nameToIndex("vsection"), vo.getVsection());
					row.setValue(ds.nameToIndex("detailinfo"), vo.getDetailinfo());
					row.setValue(ds.nameToIndex("pk_address"), vo.getPk_address());
					row.setValue(ds.nameToIndex("postcode"), vo.getPostcode());
				}else {
					//tsy 添加默认值
					row.setValue(ds.nameToIndex("country"), IOrgConst.DEFAULTCOUNTRYZONE);//中国
				}
				ds.addRow(row);
				ds.setRowSelectIndex(0);
				ds.setEnabled(true);
			}
		}
	}

	public void matchRefPk(ScriptEvent scriptEvent) {
		super.matchRefPk(scriptEvent);
	}

	/**
	 * 自定义
	 * 
	 * @param refNode
	 * @param matchValue
	 * @return
	 */
	protected List<List<Object>> matchRefPk(RefNode refNode, String matchValue) {
		return null;
	}

	/**
	 * 点确定的时候执行方法
	 * 
	 * @param e
	 */
	@Override
	public void refOkDelegator(MouseEvent<?> mouseEvent) {
		AppLifeCycleContext ctx = AppLifeCycleContext.current();
		ViewContext viewCtx = ctx.getApplicationContext()
				.getCurrentWindowContext().getCurrentViewContext();
		Dataset ds = viewCtx.getView().getViewModels()
				.getDataset(AddressRefModelConstants.AddressRefModel_MAIN_DSID);
		Row currentRow = ds.getSelectedRow();
		Map<String, String> valueMap = new HashMap<String, String>();
		String pk_address = (String) currentRow.getValue(ds.nameToIndex("pk_address"));
		String countryName = (String) currentRow.getValue(ds.nameToIndex("country_name"));
		String provinceName = (String) currentRow.getValue(ds.nameToIndex("province_name"));
		String cityName = (String) currentRow.getValue(ds.nameToIndex("city_name"));
		String vsectionName = (String) currentRow.getValue(ds.nameToIndex("vsection_name"));
		String detailInfo = (String) currentRow.getValue(ds.nameToIndex("detailinfo"));
		String postcode = (String) currentRow.getValue(ds.nameToIndex("postcode"));
		StringBuffer buf = new StringBuffer();
		UifSaveCmd cmd = new UifSaveCmd(AddressRefModelConstants.AddressRefModel_MAIN_DSID, null,LfwExAggVO.class.getName(),false){
			protected void onVoSave(AggregatedValueObject aggvo) {
				IAddressService service = NCLocator.getInstance()
						.lookup(IAddressService.class);
				try {
					AddressVO vo = (AddressVO) aggvo
							.getParentVO();
					if (vo.getPk_address() == null
							|| vo.getPk_address().length() == 0) {
						service.initAddress(vo);
					} else {
						service.updateAddress(vo);
					}
				} catch (TplBusinessException e) {
					CpLogger.error(e.getMessage(), e);
					throw new LfwRuntimeException(e.getMessage());
				}
			}
		};
		cmd.execute();
		pk_address = cmd.getBillPk();
		AddressVO addr;
		try {
			addr = (AddressVO)new PtBaseDAO().retrieveByPK(AddressVO.class, pk_address);
			AddressFormatVO formatVo = new AddressFormatVO(addr);
			formatVo.setCountry(countryName);
			formatVo.setState(provinceName);
			formatVo.setCity(cityName);
			formatVo.setSection(vsectionName);
			formatVo.setRoad(detailInfo);
			formatVo.setPostcode(postcode);
			FormatResult result = LfwFormater.formatAddress(formatVo);
			if(result != null){
				buf.append(result.getValue());
			}
		} catch (DAOException e) {
			CpLogger.error(e);
		} catch (FormatException e) {
			CpLogger.error(e);
		}
		String writeField = LfwRuntimeEnvironment.getWebContext().getWebSession().getOriginalParameter(AddressRefModelConstants.WRITEFIELD);
		String writeDs = LfwRuntimeEnvironment.getWebContext().getWebSession().getOriginalParameter(AddressRefModelConstants.WRITE_DS);
		if(StringUtils.isBlank(writeField)){
			RefNode refNode = null;
			IRefNode iRefNode = LfwReferenceUtil.getRefNodeFromParentWindow(null);
			if(iRefNode instanceof RefNode){
				refNode = (RefNode)iRefNode;
				writeField = refNode.getWriteFields();
				if(StringUtils.isBlank(writeDs)){
					writeDs = refNode.getWriteDs();
				}
			}else if(iRefNode instanceof LfwRefNode){
				refNode = (LfwRefNode)iRefNode;
				writeField = refNode.getWriteFields();
				if(StringUtils.isBlank(writeDs)){
					writeDs = refNode.getWriteDs();
				}
			}
		}
		String[] fileds = writeField.split(",");
		if(fileds.length==2){
			valueMap.put(fileds[0], pk_address);
			valueMap.put(fileds[1], buf.toString());
		}
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("type", "Dataset");
		paramMap.put("id", writeDs);
		paramMap.put("writeFields", valueMap);
		CmdInvoker.invoke(new UifPlugoutCmd(AddressRefModelConstants.MAIN, ReferenceConstants.PLUGOUT_ID,paramMap));
	}
}
