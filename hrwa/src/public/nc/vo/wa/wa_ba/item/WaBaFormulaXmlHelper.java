package nc.vo.wa.wa_ba.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import nc.vo.hr.func.FunctionVO;
import nc.vo.wa.formula.HrWaXmlReader;

public class WaBaFormulaXmlHelper {
	public static Map<String, FunctionVO> getFormulaParser() {
		return HrWaXmlReader.getInstance().getFormulaParser();
	}
	public static FunctionVO getFunctionVO(String key) {
		return  getFormulaParser().get(key);
	}
	/**
	 * 按照分组来获得FunctionVO
	 * 
	 * @author xuanlt on 2010-5-25
	 * @param group
	 * @return
	 * @return FunctionVO[]
	 */
	public static FunctionVO[] getFunctionVOByGroup(String group) {
		FunctionVO[] vos = new FunctionVO[0];
		if (getFormulaParser() == null) {
			return vos;
		}

		ArrayList<FunctionVO> list = new ArrayList<FunctionVO>();

		Collection<FunctionVO> collection = getFormulaParser().values();
		for (Iterator<FunctionVO> iterator = collection.iterator(); iterator.hasNext();) {
			FunctionVO functionVO = iterator.next();
			if (functionVO.getGroup().equals(group)) {
				list.add(functionVO);
			}

		}

		return list.toArray(vos);
	}

}
