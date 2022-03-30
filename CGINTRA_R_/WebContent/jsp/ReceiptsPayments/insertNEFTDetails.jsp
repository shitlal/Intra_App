<%@ page language="java"%>
<%@page import ="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar" %>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic"%>

<% session.setAttribute("CurrentPage","neftAllocatePayments.do?method=neftAllocatePayments");%>
<%@ include file="/jsp/SetMenuInfo.jsp" %>
<%--<%
String focusField="";
%>
<logic:equal property="bankId" value="0000" name="rpAllocationForm">
<%focusField = "memberId";%>
</logic:equal> --%>

<HTML>
	<BODY>

<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form action="neftAllocatePayments.do?method=neftAllocatePayments" method="POST" enctype="multipart/form-data" >
		<TR> 
			<TD width="20" align="right" valign="bottom"><IMG src="images/TableLeftTop.gif" width="20" height="31"></TD>
			<TD background="images/TableBackground1.gif"></TD>
			<TD width="20" align="left" valign="bottom"><IMG src="images/TableRightTop.gif" width="23" height="31"></TD>
		</TR>
		<TR>
			<TD width="20" background="images/TableVerticalLeftBG.gif">&nbsp;</TD>
			<TD>
			<DIV align="right">			
	
			</DIV>
				<TABLE width="100%" border="0" align="left" cellpadding="0" cellspacing="0">
					<TR>
						<TD>
							<TABLE width="100%" border="0" cellspacing="1" cellpadding="1">
								<TR>
									<TD colspan="4"> 
										<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
											<TR>
												<TD width="31%" class="Heading">Map Payment Id with NEFT Dtls</TD>
												<TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
											</TR>
											<TR>
												<TD colspan="3" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
											</TR>

										</TABLE>
									</TD>
								</TR>
                <tr>
			<TD  align="left" colspan="4"><font size="2"><bold>
				Fields marked as </font><font color="#FF0000" size="2">*</font><font size="2"> are mandatory</bold></font>
			</td>
		</tr>
						
                                <%
                                                java.util.Calendar cal = java.util.Calendar.getInstance();
						int day = cal.get(Calendar.DATE);
						int month = cal.get(Calendar.MONTH)+1;
						int year = cal.get(Calendar.YEAR);
						
						String currentDate = day+"/"+month+"/"+year;
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						java.util.Date cdate = sdf.parse(currentDate);
						currentDate = sdf.format(cdate);
				%>
				<input type="hidden" name="currDate" id="currentDate" value="<%=currentDate%>" />
             <logic:equal property="bankId" value="0000" name="rpAllocationForm">		
								<TR align="left" valign="top">
									<TD align="left" valign="top" class="ColumnBackground"><font color="#FF0000" size="2">*</font><bean:message key="MemberID"/>
									</TD>
									<TD align="left" valign="top" class="TableData">
                  	  <html:text property="memberId" size="25" alt="memberId" name="rpAllocationForm" maxlength="12" />
									
									
									</TD>
       					</TR>
						</logic:equal>
						 <logic:notEqual property="bankId" value="0000" name="rpAllocationForm">		
						 		<TR align="left" valign="top">
									<TD align="left" valign="top" class="ColumnBackground"><bean:message key="MemberID"/>
									</TD>
                  <TD align="left" valign="top" class="ColumnBackground">
										  
                      	<bean:write property="memberId" name="rpAllocationForm"/>
										 </td>
											</TR>
						</logic:notEqual>
            <TR align="left" valign="top">
									<TD align="left" valign="top" class="ColumnBackground"><bean:message key="paymentId"/>
									</TD>
                  <TD align="left" valign="top" class="ColumnBackground">
									     	<bean:write property="paymentId" name="rpAllocationForm"/>
										 </td>
				</TR>
            
            <TR align="left" valign="top">
									<TD align="left" valign="top" class="ColumnBackground"><bean:message key="allocatedAmt"/>
									</TD>
                  <TD align="left" valign="top" class="ColumnBackground">
									     	<bean:write property="allocatedAmt" name="rpAllocationForm"/>
								
                		 </td>
				</TR>
         <TR align="left" valign="top">
                                    <TD align="left" valign="top" class="ColumnBackground">&nbsp;<font color="#FF0000" size="2">*</font><bean:message key="neftCode"/>
									/<bean:message key="ifscCode"/>/UTR No.</TD>
                  <TD align="left" valign="top" class="ColumnBackground">
									         <html:text property="neftCode" size="25" alt="neftCode" name="rpAllocationForm" maxlength="16" />
									
								
                		 </td>
				</TR>
        
        
        <TR align="left" valign="top">
                                <TD align="left" valign="top" class="ColumnBackground">&nbsp;<font color="#FF0000" size="2">*</font><bean:message key="bankName"/>
									</TD>
                  <TD align="left" valign="top" class="ColumnBackground">
                                       <html:text property="bankName" size="25" alt="bankName" name="rpAllocationForm" maxlength="100" />  
									
                		 </td>
				</TR>
        <TR align="left" valign="top">
                                    <TD align="left" valign="top" class="ColumnBackground">&nbsp;<font color="#FF0000" size="2">*</font><bean:message key="zoneName"/>
									</TD>
                  <TD align="left" valign="top" class="ColumnBackground">
                                            <html:text property="zoneName" size="25" alt="zoneName" name="rpAllocationForm" maxlength="100" />  
								
                		 </td>
				</TR>
        <TR align="left" valign="top">
                                <TD align="left" valign="top" class="ColumnBackground">&nbsp;<font color="#FF0000" size="2">*</font><bean:message key="CBbranchName"/>
									</TD>
                  <TD align="left" valign="top" class="ColumnBackground">
						<html:text property="branchName" size="25" alt="branchName" name="rpAllocationForm" maxlength="100" /> 
								
                		 </td>
				</TR>
       
    <!--     <TR align="left" valign="top">
									<TD align="left" valign="top" class="ColumnBackground"><bean:message key="ifscCode"/>
									</TD>
                  <TD align="left" valign="top" class="ColumnBackground">
									     <html:text property="ifscCode" size="25" alt="ifscCode" name="rpAllocationForm" maxlength="100" />
								
                		 </td>
				</TR>  -->
        
           <tr>
              <TD align="left" valign="top" class="ColumnBackground">&nbsp;<font color="#FF0000" size="2">*</font><bean:message key="paymentDate"/></td>
              <td class="tableData"><table cellpadding="0" cellspacing="0">
                <tr>
                  <td><html:text property="paymentDate" size="16" alt="Payment Date" name="rpAllocationForm" maxlength="10"/></td>
                  <td><img src="images/CalendarIcon.gif" width="20" align="center"
                  onClick="showCalendar('rpAllocationForm.paymentDate')"></td>
                </tr>       	
          </table>
              </td>
            </tr>
				</TABLE>
						</TD>
					</TR>
					<TR >
						<TD height="20" >
							&nbsp;
						</TD>
					</TR>
					<TR >
						<TD align="center" valign="baseline" >
							<DIV align="center">
								
								<%--	<A href="javascript:submitForm('neftAllocatePayments.do?method=SubmitMappingRPandNEFT')"> --%>
                                                                <A href="#" onClick="saveNTFSForm();">
									<IMG src="images/OK.gif" alt="OK" width="49" height="37" border="0"></A>
									<A href="javascript:document.rpAllocationForm.reset()">
									<IMG src="images/Reset.gif" alt="Cancel" width="49" height="37" border="0"></A>

								<A href="home.do?method=getMainMenu&menuIcon=<%=session.getAttribute("menuIcon")%>&mainMenu=<%=session.getAttribute("mainMenu")%>">
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

<script type="text/javascript">
    function saveNTFSForm(){
     //alert("aa gaya");
        
        if(document.forms[0].neftCode.value == null || document.forms[0].neftCode.value == ""){
            alert("Please enter transaction/urt/neft number");
            return false;
        } 
        if(document.forms[0].bankName.value == null || document.forms[0].bankName.value == ""){
             alert("Please enter bank name");
             return false;
        }
        if(document.forms[0].zoneName.value == null || document.forms[0].zoneName.value == ""){
             alert("Please enter zone name");
             return false;
        } 
        if(document.forms[0].branchName.value == null || document.forms[0].branchName.value == ""){
             alert("Please enter branch name");
             return false;
        } 
        if(document.forms[0].paymentDate.value == null || document.forms[0].paymentDate.value == ""){
             alert("Please enter payment date");
             return false;
        }
        
        
            document.forms[0].target ="_self";
            document.forms[0].method="POST";
            document.forms[0].action="neftAllocatePayments.do?method=SubmitMappingRPandNEFT";
            document.forms[0].submit();
    }

function chkDate(){
		var paymentDate = document.forms[0].paymentDate.value;
		var currentdate = document.getElementById("currentDate").value;
		var pymtDayStr;
			  var pymtMonStr;
			  var pymtYearStr;
		var currDayStr;
		var currMonStr;
		var currYearStr;		
		var elem1 = paymentDate.split('/');  
		var elem2 = currentdate.split('/');		 
				
				pymtDayStr = elem1[0];  
				pymtMonStr = elem1[1];  
				pymtYearStr = elem1[2];
				currDayStr = elem2[0]; 
				currMonStr = elem2[1]; 
				currYearStr = elem2[2]; 
				
				/* if(memberid.isNaN()){
					alert("");
				} */
				
			if((pymtYearStr > currYearStr) || ((pymtYearStr == currYearStr) && (pymtMonStr > currMonStr)) || ((pymtYearStr == currYearStr) && (pymtMonStr == currMonStr) && (pymtDayStr > currDayStr))){

							alert("payment date can not be greater than current date");
							document.forms[0].paymentDate.value = '';
							//document.forms[0].paymentDate.style.backgroundColor = 'yellow';
							//document.forms[0].paymentDate.focus();
							return false;
				
			}
}


</script>
	</BODY>
</HTML>