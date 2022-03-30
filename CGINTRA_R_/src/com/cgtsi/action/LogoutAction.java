/*
 * Created on Oct 30, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cgtsi.action;

import com.cgtsi.common.DatabaseException;
import com.cgtsi.registration.Registration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.cgtsi.admin.User;
import com.cgtsi.common.Log;
import com.cgtsi.util.SessionConstants;

import java.net.InetAddress;

/**
 * @author VT8150
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class LogoutAction extends BaseAction {
	
	public ActionForward logout(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)  throws DatabaseException
	{
		Log.log(Log.INFO,"LogoutAction","logout","Entered");
		HttpSession session=request.getSession(false);
		//String userId=(String)session.getAttribute("userId");
		//session.removeAttribute("userId");
		User user=(User)session.getAttribute(SessionConstants.USER);
	//	System.out.println("Logout Session Id:"+session.getId().concat(user.getUserId()));
		String userName=user.getFirstName();
	//	System.out.println("User Name:"+userName);
  //  System.out.println("Session Id:"+session.getId());
      String bankId = (user.getBankId()).trim();
	    String zoneId = (user.getZoneId()).trim();
	    String branchId = (user.getBranchId()).trim();
	    String ipAddress = "";
      String hostName = "";
      String proxyName = "";
      String sessionId = "";
      
         
         try {
           java.net.InetAddress i = java.net.InetAddress.getLocalHost();
         // System.out.println("IP Address: "+i);                  // name and IP address
           ipAddress = request.getRemoteAddr();
           hostName = request.getRemoteHost();
           proxyName = request.getHeader("VIA");
           sessionId = session.getId();           
           }
           catch(Exception e)
           {
              e.printStackTrace();
           } 
   
		session.removeAttribute(SessionConstants.USER);
    Registration registration=new Registration();
    System.out.println("Session Id:"+sessionId);
		registration.updateLogoutInformation(bankId,zoneId,branchId,ipAddress,hostName,proxyName,sessionId,user.getUserId());
  
		session.invalidate();
//	System.out.println("Last Session Id:"+session.getId());
		request.setAttribute("userId",userName);
//		System.out.println("Last User Name:"+userName);
		Log.log(Log.INFO,"LogoutAction","logout","Exited");
		return mapping.findForward("success");
	}
}
