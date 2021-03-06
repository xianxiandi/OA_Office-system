package com.web.oa.shiro;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class CustomFormAuthenticationFilter extends FormAuthenticationFilter {

//	@Override
//	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
//		HttpServletRequest req = (HttpServletRequest) request;
//		HttpSession session = req.getSession();
//		String validateCode = req.getParameter("validateCode");
//		String randNum = (String) session.getAttribute("vrifyCode");
//		
//		if (validateCode != null && randNum != null && !validateCode.equals(randNum)) {
//			request.setAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME, "InValidateCode");
//			return true;
//		}
//		
//		return super.onAccessDenied(request, response);
//	}
	
	@Override
	protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
			ServletResponse response) throws Exception {
		WebUtils.getAndClearSavedRequest(request);
		WebUtils.redirectToSavedRequest(request, response, "/main");
		return false;
	}
}
