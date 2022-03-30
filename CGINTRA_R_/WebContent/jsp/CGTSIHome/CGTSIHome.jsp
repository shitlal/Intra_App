<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic" %>
<% session.setAttribute("CurrentPage","showCGTSIHome.do?method=showCGTSIHome");%>
<%@ include file="/jsp/SetMenuInfo.jsp" %>


<HTML>
<SCRIPT language="JavaScript" type="text/JavaScript" src="<%=request.getContextPath()%>/js/CGTSI.js">
</SCRIPT>
<LINK href="<%=request.getContextPath()%>/css/StyleSheet.css" rel="stylesheet" type="text/css">
	<BODY onLoad="setToDefault()">
		<TABLE width="710" border="2">
		<form name="home">
		   <TR>
			<TD> 
      		<TABLE  width="710" border="0" >
        
				<TR>
				  <TD> 
					<TABLE  width="710" border="0" cellspacing="0" cellpadding="5">
					  <TR>
						<TD class="CGTSIInfo">
						  <DIV align="center">Credit Guarantee Fund Trust 
											  <BR>
											  For Small Industries
											  <BR>
											  <BR>
											  Set Up
											  <BR>
											  <BR>
											  By
											  <BR>
											  <BR>
											  <DIV class="GOIInfo" align="center">
											  Ministry of SSI, GoI
											  <BR>
											  &amp;
											  <BR>
											  Small Industries Development Bank Of India
											  </DIV>
						  </DIV>
					   </TD>
					   <TD align="center" valign="middle" class="TableData">
						   <TABLE border="0" width="250">
							 <TR>
							   <TD class="TableData">
								<DIV align="center">
										<font size="2">
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											<strong>Broadcast Message<strong>
								</DIV>
							   </TD>
							 </TR>
							 <TR>
								<TD class="TableData">
									<DIV align="center">
										&nbsp;&nbsp;&nbsp;
										<bean:write property="message" name="login" />
									</DIV>
								</TD>
							</TR>
						   </TABLE>
						<DIV align="center">
						</DIV>
				   </TD>
				  </TR>
				 </TABLE>
				</TD>
			   </TR>
			  </TABLE>
			 </TD>
		   </TR>
		</form>
		</TABLE>
		
		<%
			String alertStr="";
		%>
			
		<logic:iterate id="alert" property="alerts" name="login">

			<%
				String alertMessage=(String)alert;
				alertStr+=alertMessage;
				alertStr+="\\n";
			%>

		</logic:iterate>
		
		<SCRIPT>

		<% if(alertStr!=null && !alertStr.equals("")){%>
			
			alert("<%=alertStr%>");
			
		<%}%>
		</script>		
	</BODY>
</HTML>