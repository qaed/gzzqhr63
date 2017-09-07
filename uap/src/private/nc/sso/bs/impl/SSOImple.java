package nc.sso.bs.impl;

import java.util.List;
import nc.bs.logging.Logger;
import nc.sso.bs.ISSOService;
import nc.sso.vo.SSOAuthenConfVO;
import nc.sso.vo.SSOConfigVO;
import nc.sso.vo.SSORegInfo;
import nc.vo.pub.BusinessException;

public class SSOImple
  implements ISSOService
{
  public SSORegInfo fetchSSORegInfo(String ssoKey)
    throws BusinessException
  {
    try
    {
      return SSORegisterCenter.getInstance().fetchSSORegInfo(ssoKey);
    } catch (Exception e) {
      Logger.error(e.getMessage(), e);
      throw new BusinessException(e.getMessage(), e);
    }
  }

  public void registerSSOInfo(SSORegInfo regInfo) throws BusinessException
  {
    try
    {
      SSORegisterCenter.getInstance().getSSOConfigVO();
      SSORegisterCenter.getInstance().registerSSORegInfo(regInfo);
    } catch (Exception e) {
      Logger.error(e.getMessage(), e);
      throw new BusinessException(e.getMessage(), e);
    }
  }

  public List<SSOAuthenConfVO> getAuthenConfList() throws BusinessException
  {
    try
    {
      return SSORegisterCenter.getInstance().getSSOConfigVO().getAuthenConfVOList();
    } catch (Exception e) {
      Logger.error(e.getMessage(), e);
      throw new BusinessException(e.getMessage(), e);
    }
  }
}