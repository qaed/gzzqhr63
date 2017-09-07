/** 
 * <p>功能描述:</p>
 * @version v63
 * <p>文件名： RegisterFilter.java</p>
 * <p>创建人及时间： hzy 2016-7-28下午6:21:55</p>
 *
 * <p>修改人：</p>
 * <p>修改时间：</p>
 * <p>修改描述：</p>
 **/
package nc.sm.login.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.util.AssertionHolder;

import com.landray.sso.client.EKPSSOUserData;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.sso.bs.ISSOService;
import nc.sso.vo.SSORegInfo;
import nc.vo.pub.BusinessException;

/** 
 * <p>功能说明:</p>
 * <p>创建人及时间： hzy 2016-7-28下午6:21:55</p>
 *
 * <p>修改人：</p>
 * <p>修改时间：</p>
 * <p>修改描述：</p>
 **/
public class RegisterFilter implements Filter {

	/**
	 * <p>功能描述：</p>
	 * <p>创建人及时间： hzy 2016-7-28下午6:21:55</p>
	 * (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		// TODO 自动生成的方法存根

	}

	/**
	 * <p>功能描述：</p>
	 * <p>创建人及时间： hzy 2016-7-28下午6:21:56</p>
	 * (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@SuppressWarnings("restriction")
	public void doFilter(ServletRequest servletrequest,
			ServletResponse servletresponse, FilterChain filterchain)
			throws IOException, ServletException {
		if ((!(servletrequest instanceof HttpServletRequest))
				|| (!(servletresponse instanceof HttpServletResponse))) {
			throw new ServletException("统一认证登录只支持HTTP资源请求");
		}
		HttpServletRequest request = (HttpServletRequest) servletrequest;
		HttpServletResponse response = (HttpServletResponse) servletresponse;
		HttpSession session = ((HttpServletRequest)request).getSession();
		session.getValueNames();
		session.getAttributeNames();
		session.getAttribute("_const_cas_assertion_");
		String scheme = request.getScheme(); // 传输协议
		String serverName = request.getServerName(); // NC主机地址
		int serverPort = request.getServerPort(); // NC主机端口
		
//		String userCode = null != request.getRemoteUser() ? request.getRemoteUser() : 
//			null != AssertionHolder.getAssertion() ? AssertionHolder.getAssertion().getPrincipal().getName() : null;
		EKPSSOUserData userData = EKPSSOUserData.getInstance();
		String userCode = userData.getCurrentUsername();
		String ticket = request.getParameter("ticket");
			
		
		// 拼装NC单点登录URL
		String url = null;
		
		SSORegInfo regInfo = new SSORegInfo();
	    regInfo.setSsoKey(userCode);
	    regInfo.setUserCode(userCode);
	    //regInfo.setBusiCenterCode("NC63");
	    //regInfo.setGroupCode("0001B1100000000009A5");
	    ISSOService service = (ISSOService)NCLocator.getInstance().lookup(ISSOService.class);
	    try {
	      String urlString = request.getRequestURI();
	      if(null != urlString && urlString.contains("/index.jsp")){
	    	  service.registerSSOInfo(regInfo);
//	    	  String regStr = scheme + "://" + serverName + ":" + serverPort 
//			  + "/service/ssoRegServlet?ssoKey="+userCode + "&" + "userCode=" + userCode;
//	 			 response.sendRedirect(regStr);
		      url = scheme + "://" + serverName + ":" + serverPort
					    +"/login.jsp?ssoKey=" + userCode;
		      // 重定向到指定的地址
	    	  response.sendRedirect(url);
	      }else{
	    	  filterchain.doFilter(request, response);
	      }
	      return;
	    }
	    catch (BusinessException e) {
	      Logger.info("用户信息注册失败");
	      e.printStackTrace();
	    }
	    catch (Exception e) {
	      Logger.info("用户信息注册失败");
	      e.printStackTrace();
	    }
	}

	/**
	 * <p>功能描述：</p>
	 * <p>创建人及时间： hzy 2016-7-28下午6:21:56</p>
	 * (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig filterconfig) throws ServletException {
		// TODO 自动生成的方法存根
		filterconfig.getFilterName();
		filterconfig.getInitParameterNames();
	}

}
