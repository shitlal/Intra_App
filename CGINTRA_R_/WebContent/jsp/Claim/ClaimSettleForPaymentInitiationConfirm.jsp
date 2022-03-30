<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic"%>
<%@ include file="/jsp/SetMenuInfo.jsp"%>
<% session.setAttribute("CurrentPage","ClaimSettleForPaymentInput.do?method=getClaimSettleForPaymentInput");%>
<html:errors />
<br><br><br><br><br>
<html:form action="/ClaimSettleForPaymentInput.do?method=getClaimSettleForPaymentInput" method="POST">
<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
<TR>
			<TD colspan=3><hr></TD>
</TR>
<tr>
<td></td>	
     <td> 
     <% String actionType=session.getAttribute("actionType").toString();
        //out.println(actionType);  
        if(actionType.equals("Reject")){%>
        
        <c:if test='${ requestScope["org.apache.struts.action.ERROR"] ne null }'>
        
					Return of Initiate Payment data Saved Successfully. 
		</c:if>
        
        <%}else if (actionType.equals("Initiate")){  %>
        
        <c:if test='${ requestScope["org.apache.struts.action.ERROR"] ne null }'>
        
		Initiate Payment data Saved Successfully And 
		Sent for Approval at Reviewer Level.		
		</c:if>  
        <%} %>
     	
	</td>
	
<td></td>
<tr>
<td colspan=3><DIV align="center"><A
						href="javascript:submitForm('ClaimSettleForPaymentInput.do?method=getClaimSettleForPaymentInput')">
					<IMG src="images/OK.gif" alt="OK" width="49" height="37" border="0"></A>
</td>
</tr>
<tr><TD colspan=3><hr></TD></tr>
</TABLE>
</html:form>