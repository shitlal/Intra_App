<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic" %>
<%@ include file="/jsp/SetMenuInfo.jsp" %>
<%@ page import="com.cgtsi.util.SessionConstants"%>
<% 
if(session.getAttribute(SessionConstants.APPLICATION_TYPE).equals("TCE"))
{
session.setAttribute("CurrentPage","enterApp.do?method=getAddtlTCInfo");

}else if (session.getAttribute(SessionConstants.APPLICATION_TYPE).equals("WCE"))
{
	session.setAttribute("CurrentPage","enterApp.do?method=getEnhanceWCInfo");

}else if (session.getAttribute(SessionConstants.APPLICATION_TYPE).equals("WCR"))
{
	session.setAttribute("CurrentPage","enterApp.do?method=getRenewWCInfo");
}
;%>

<%String focusField ="";%>

<logic:equal property="bankId" value="0000" name="appForm">
<%focusField = "selectMember";%>
</logic:equal>

<logic:notEqual property="bankId" value="0000" name="appForm">
<%focusField = "cgpan";%>
</logic:notEqual>
<script>
function funcValidateExtendedDate(){
    var xmlhtp; 
    var dMsg="";
    var selectMember = document.getElementById("selectMember").value; 		   		
    var cgpan = document.getElementById("cgpan").value;	
    var cgbid = document.getElementById("cgbid").value;
   
if (window.XMLHttpRequest){
	xmlhtp = new XMLHttpRequest(); //for IE7+, Firefox, Chrome, Opera, Safari
} else {
	xmlhtp = new ActiveXObject("Microsoft.XMLHTTP"); //for IE6, IE5
}  
if(selectMember && cgpan){
	xmlhtp.open("POST", "checkRenewPeriodAvailability.do?method=checkRenewPeriodAvailability&selectMember="+selectMember+"&cgpan="+cgpan, true);       		      
	}else if(selectMember && cgbid){
		xmlhtp.open("POST", "checkRenewPeriodAvailability.do?method=checkRenewPeriodAvailability&selectMember="+selectMember+"&cgbid="+cgbid, true);  
	} 
xmlhtp.onreadystatechange = function() { 
	if (xmlhtp.readyState == 4) 
	{			    		
		if (xmlhtp.status == 200)
		{	
            dMsg = xmlhtp.responseText.trim();    			         	
    	   	if( dMsg=='VALID' ){
    	   		submitForm('afterEnterApp.do?method=showCgpanList&flag=2');
    		}else{
   	   		 document.getElementById("dmessage").innerHTML = dMsg;	        	   	     
   	   		}        	   	            
	    } else {
        	document.getElementById("dmessage").innerHTML = dMsg;  //'Something is wrong ! , Please contact CGTMSE support[support@cgtmse.in] team .'; 
        	
        }
    }
};
xmlhtp.send(null);
}

</script>
<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form action="afterEnterApp.do?method=showCgpanList" method="POST" enctype="multipart/form-data" focus="<%=focusField%>">
		<TR> 
			<TD width="20" align="right" valign="bottom"><IMG src="images/TableLeftTop.gif" width="20" height="31"></TD>
			<TD background="images/TableBackground1.gif"></TD>
			<TD width="20" align="left" valign="bottom"><IMG src="images/TableRightTop.gif" width="23" height="31"></TD>
		</TR>
		<TR>
			<TD width="20" background="images/TableVerticalLeftBG.gif">&nbsp;</TD>
			<TD>
				<DIV align="right">			
					<A HREF="javascript:submitForm('mliInfoHelp.do?method=mliInfoHelp')">
					HELP</A>
				</DIV>

				<TABLE width="100%" border="0" align="left" cellpadding="0" cellspacing="0">
					<TR>
						<TD>
							<table width="100%" border="0" cellspacing="1" cellpadding="0">
							<tr> 
							  <td colspan="4">
								<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
									<TR>
										<TD width="31%" class="Heading"><bean:message key="enterApplicationDetails" /></TD>
										<TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
									</TR>
									<TR>
										<TD colspan="6" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
									</TR>
								</TABLE>
							  </td>
							</tr>
							<tr align="left"> 
								<td class="ColumnBackground"> 
									&nbsp;
									<font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="mliID"/>			
								</td>
								<td class="tableData" colspan="3"> 
								<logic:equal property="bankId" value="0000" name="appForm">

										<html:text property="selectMember" size="20" alt="cgbid" name="appForm" maxlength="12"/> 								
								</logic:equal>
								<logic:notEqual property="bankId" value="0000" name="appForm">

									<bean:write property="selectMember" name="appForm"/>				
								</logic:notEqual>
								</td>
							 </tr>
							 <tr align="left"> 
							   <td width="25%" class="ColumnBackground"> 
									&nbsp; 
									<bean:message key="cgpan"/>			
								</td>
								<td width="25%" class="TableData">
									 <html:text property="cgpan" size="15" alt="cgpan" name="appForm" maxlength="15"/>           &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										  &nbsp;&nbsp; 
									 <bean:message key="or"/>	
								</td>
								<td width="25%" class="ColumnBackground"> &nbsp;
									<bean:message key="cgbid"/>			
								</td>
								<td width="25%" class="TableData"> 
									<html:text property="cgbid" size="20" alt="cgbid" name="appForm" maxlength="9"/>           
								</td>
							 </tr>
							 <tr align="left"> 
								<td colspan="4">
									<img src="../images/clear.gif" width="5" height="15">
								</td>
							 </tr>
						  </table>									
						</td>
					</tr>

					<!-- Added by DKR -->
                    <TR>
 					<td align="left"><div id="dmessage" class="errorsMessage"></div>  </td>
						<TD height="20" >
							&nbsp;
						</TD> 
					</TR>
				<!-- 	END -->
					<TR >
						<TD align="center" valign="baseline" colspan="6">
							<DIV align="center">							
                           <A href="javascript:funcValidateExtendedDate();"><IMG src="images/OK.gif" alt="OK" width="49" height="37" border="0"></A>		
					<!--<TR >
						<TD align="center" valign="baseline" colspan="6">
							<DIV align="center">
								<A href="javascript:submitForm('afterEnterApp.do?method=showCgpanList&flag=2')"><IMG src="images/OK.gif" alt="OK" width="49" height="37" border="0"></A>		
								--><A href="javascript:document.appForm.reset()">
									<IMG src="images/Reset.gif" alt="Reset" width="49" height="37" border="0"></A>
								<A href="subHome.do?method=getSubMenu&menuIcon=<%=session.getAttribute("menuIcon")%>&mainMenu=<%=session.getAttribute("mainMenu")%>">
								<IMG src="images/Cancel.gif" alt="Cancel" width="49" height="37" border="0"></A>
							</DIV>
						</TD>
					</TR>					
				</TABLE>
			</TD>
			<TD width="20" background="images/TableVerticalRightBG.gif">
				&nbsp;
			</TD>
		</TR>
		<TR>
			<TD width="20" align="right" valign="top">
				<IMG src="images/TableLeftBottom1.gif" width="20" height="15">
			</TD>
			<TD background="images/TableBackground2.gif">
				&nbsp;
			</TD>
			<TD width="20" align="left" valign="top">
				<IMG src="images/TableRightBottom1.gif" width="23" height="15">
			</TD>
		</TR>
	</html:form>
</TABLE>







					