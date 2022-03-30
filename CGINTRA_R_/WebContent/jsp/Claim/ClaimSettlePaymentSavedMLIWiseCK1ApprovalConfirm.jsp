<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic"%>
<%@ include file="/jsp/SetMenuInfo.jsp"%>
<script>

</script>
<br><br><br><br><br>
<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<tr>
		<TD colspan=3><hr></TD>
     </tr>
     <tr>
     	<td> 
			<html:form action="/ClaimSettlePaymentSavedMLIWiseCK1Input.do?method=getClaimSettlePaymentSavedMLIWiseCK1Input" method="POST"  >
			
			 <% String actionType=session.getAttribute("actionType").toString();
		        //out.println(actionType);  
		        if(actionType.equals("Reject")){%>
		        
		        <c:if test='${ requestScope["org.apache.struts.action.ERROR"] ne null }'>
		        
							Return of Payment data Saved Successfully. 
				</c:if>
		        
		        <%}else if (actionType.equals("Approve")){  %>
		        
		        <c:if test='${ requestScope["org.apache.struts.action.ERROR"] ne null }'>
		        
				Payment data approved Successfully And 
				Sent for Approval at Final Checker Level.		
				
				</c:if>  
		        <%} %>
		</td>	
		<td></td>
	  <tr>
			 <td colspan=3><DIV align="center"><A
								href="javascript:submitForm('ClaimSettlePaymentSavedMLIWiseCK1Input.do?method=getClaimSettlePaymentSavedMLIWiseCK1Input')">
							<IMG src="images/OK.gif" alt="OK" width="49" height="37" border="0"></A>
		      </td>
      </tr>
       <tr>
            <TD colspan=3><hr></TD>
       </tr>
</TABLE>
	
	
	
	
	
	
	
	</html:form>
		
</TABLE>