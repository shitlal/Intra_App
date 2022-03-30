<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic"%>

<% session.setAttribute("CurrentPage","getUnitForClosureRequest.do?method=getUnitForClosureRequest");%>
<%@ include file="/jsp/SetMenuInfo.jsp" %>
<%
String focusField="";
%>
<logic:equal property="bankIdForClosure" value="0000" name="gmClosureForm">
<%focusField = "memberIdForClosure";%>
</logic:equal>

<HTML>
<head>
 <script type="text/JavaScript">
        function submitForm22(action)
        {
        var str1 = document.getElementById("lastDateOfPayment").value;
         var reviseOfTenure = document.getElementById("reviseOfTenure").value;
         var modificationOfRemarks = document.getElementById("modificationOfRemarks").value;
         if(reviseOfTenure=='')
         {
         alert("Revise Tenure is required");
          }
         else if(str1=='')
         {
         alert("LastDate Of Payment is required");
         }
        else if(modificationOfRemarks=='')
         {
         alert("Modification Remarks is required");
          }
         else
         {
       
var validDateFormat =validate(str1);

if(validDateFormat)
{
      var sysDate=new Date();
      var dt2 = sysDate.getDate();
      var mon2 = (sysDate.getMonth());
      var yr2 = sysDate.getFullYear();
    var dt1  = parseInt(str1.substring(0,2),10); 
    var mon1 = parseInt(str1.substring(3,5),10)-1;
    var yr1  = parseInt(str1.substring(6,10),10); 
    var date1 = new Date(yr1, mon1, dt1); 
          
    var date2 = new Date(yr2, mon2, dt2); 

   if(date2 > date1)
    {
        alert("LastDate of Payment cannot be less than System Date");
        
    } 
   else 
    { 
      // document.forms[0].action=action;
	document.forms[0].target="_self";
	document.forms[0].method="POST";
	document.forms[0].submit();
    } 
   }
else
{
alert("Date Format should be in dd/mm/yyyy");

}
        
        }
        
              
    
}

    function isValidDate2(sText) {
       // var reDate = /(?:0[1-9]|[12][0-9]|3[01])\/(?:0[1-9]|1[0-2])\/(?:19|20\d{2})/;
        
         var reDate = /(?:0[1-9]|[12][0-9]|3[01])\/(?:0[1-9]|1[0-2])\/(?:19|20\d{2})/;
        return reDate.test(sText);
    }
    function validate(oInput1) {
    
    
      //  var oInput1 = document.getElementById("txt1");
        if (isValidDate2(oInput1)) {
            return true;
            
        } else {
           return false;
            
        }

    }

        </script>
</head>
	<BODY>
<SCRIPT language="JavaScript" type="text/JavaScript" src="<%=request.getContextPath()%>/js/CGTSI.js">
	</SCRIPT>
     <SCRIPT language="JavaScript" type="text/JavaScript" src="<%=request.getContextPath()%>/js/selectdate.js">
	</SCRIPT> 
<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form action="getUnitForTenureRequest.do?method=getUnitForTenureRequest" method="POST"  >
		<TR> 
			<TD width="20" align="right" valign="bottom"><IMG src="images/TableLeftTop.gif" width="20" height="31"></TD>
			<TD background="images/TableBackground1.gif"><IMG src="images/GuaranteeMaintenanceHeading.gif"></TD>
			<TD width="20" align="left" valign="bottom"><IMG src="images/TableRightTop.gif" width="23" height="31"></TD>
		</TR>
		<TR>
			<TD width="20" background="images/TableVerticalLeftBG.gif">&nbsp;</TD>
			<TD>
			<DIV align="right">			
	<!--			<A HREF="javascript:submitForm('helpClosureDetailsFilter.do?method=helpClosureDetailsFilter')">
			    HELP</A> -->
			</DIV>
				<TABLE width="100%" border="0" align="left" cellpadding="0" cellspacing="0">
					<TR>
						<TD>
							<TABLE width="100%" border="0" cellspacing="1" cellpadding="1">
								<TR>
									<TD colspan="4"> 
										<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
											<TR>
												<TD width="31%" class="Heading"><bean:message key="modifyBorrowerHeader"/></TD>
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
						<TR align="left" valign="top">
									<TD align="left" valign="top" class="ColumnBackground"><font color="#FF0000" size="2"></font><bean:message key="bankName"/>
									</TD>
									<TD align="left" valign="top" class="TableData">
										 <bean:write property="bankName" name="gmClosureForm"/>
									</TD>
                  <TD align="left" valign="top" class="ColumnBackground"><font color="#FF0000" size="2"></font><bean:message key="zoneName"/>
									</TD>
									<TD align="left" valign="top" class="TableData">
									  <bean:write property="zoneName" name="gmClosureForm"/>
									</TD>
								</TR>
            
             <logic:equal property="bankIdForClosure" value="0000" name="gmClosureForm">		
								<TR align="left" valign="top">
									<TD align="left" valign="top" class="ColumnBackground"><font color="#FF0000" size="2"></font><bean:message key="MemberID"/>
									</TD>
									<TD align="left" valign="top" class="TableData">
										<bean:write property="memberIdForClosure" name="gmClosureForm"/>
									</TD>
                  <TD align="left" valign="top" class="ColumnBackground"><font color="#FF0000" size="2"></font><bean:message key="cgpan"/>
									</TD>
									<TD align="left" valign="top" class="TableData">
									 <bean:write property="cgpanForClosure" name="gmClosureForm"/>
									</TD>
								</TR>
                                                                <TR align="left" valign="top">
									<TD align="left" valign="top" class="ColumnBackground"><font color="#FF0000" size="2"></font><bean:message key="branchName"/>
									</TD>
									<TD align="left" valign="top" class="TableData">
										<bean:write property="applicationReport.bid" name="gmClosureForm"/>
                                                                                <bean:write property="branchName" name="gmClosureForm"/>
                                                                               
									</TD>
                  <TD align="left" valign="top" class="ColumnBackground"><font color="#FF0000" size="2"></font><bean:message key="tenure"/>
									</TD>
									<TD align="left" valign="top" class="TableData">
									 <bean:write property="tenure" name="gmClosureForm"/>
									</TD>
								</TR>
                                                                <TR align="left" valign="top">
									<TD align="left" valign="top" class="ColumnBackground"><font color="#FF0000" size="2"></font>Expiry Date
									</TD>
									<TD align="left" valign="top" class="TableData">
										 <bean:write property="expiryDate" name="gmClosureForm"/>
									</TD>
                  <TD align="left" valign="top" class="ColumnBackground"><font color="#FF0000" size="2"></font>
									</TD>
									<TD align="left" valign="top" class="TableData">
									 
									</TD>
								</TR>
						</logic:equal>
						 <logic:notEqual property="bankIdForClosure" value="0000" name="gmClosureForm">		
						 		<TR align="left" valign="top">
									<TD align="left" valign="top" class="ColumnBackground"><bean:message key="MemberID"/>
									</TD>
									<TD align="left" valign="top" class="TableData">
										<bean:write property="memberIdForClosure" name="gmClosureForm"/>
									</TD>
                  <TD align="left" valign="top" class="ColumnBackground"><font color="#FF0000" size="2"></font>
									</TD>
									<TD align="left" valign="top" class="TableData">
									
									</TD>
								</TR>
						</logic:notEqual>
              <TR>                								
								  <TD align="left" valign="top" class="ColumnBackground"><font color="#FF0000" size="2"></font><bean:message key="ssiName"/>
									</TD>
									<TD align="left" valign="top" class="TableData" colspan="4"><bean:write property="unitName" name="gmClosureForm"/>
										
                  </TD>
								</TR>
<TR align="left" valign="top">
									<TD align="left" valign="top"   class="ColumnBackground"><font color="#FF0000" size="2">*</font>Revise Of Tenure
									</TD>
									<TD align="left" valign="top" class="TableData" colspan="4">
             
								<html:text property="reviseOfTenure"  size="20" alt="closure Remarks" name="gmClosureForm"  onkeypress="return numbersOnly(this, event)"   />
						      </TD>
								</TR>  	
								<TR>                								
								  <TD align="left" valign="top" class="ColumnBackground"><font color="#FF0000" size="2">*</font>LastDate of  Payment
									</TD>
									<TD align="left" valign="top" class="TableData" colspan="4">
										<html:text property="lastDateOfPayment" size="20" name="gmClosureForm" maxlength ="10"  />
										  <IMG src="images/CalendarIcon.gif" width="20" onClick="showCalendar('gmClosureForm.lastDateOfPayment')" align="center">
									</TD>
								</TR>
               
								<TR align="left" valign="top">
									<TD align="left" valign="top"   class="ColumnBackground"><font color="#FF0000" size="2">*</font>Modification Of Remarks
									</TD>
									<TD align="left" valign="top" class="TableData" colspan="4">
          
								<html:text property="modificationOfRemarks"  size="100" alt="closure Remarks" name="gmClosureForm"/>
						      </TD>
								</TR>  			


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
								
									<A href="javascript:submitForm('submitSSIDetailForTenure.do?method=submitSSIDetailForTenure')">
									<IMG src="images/OK.gif" alt="OK" width="49" height="37" border="0"></A>
									<A href="javascript:document.gmClosureForm.reset()">
									<IMG src="images/Reset.gif" alt="Cancel" width="49" height="37" border="0"></A>

								<A href="home.do?method=getMainMenu&menuIcon=<%=session.getAttribute("menuIcon")%>&mainMenu=<%=session.getAttribute("mainMenu")%>">
								<IMG src="images/Cancel.gif" alt="Cancel" width="49" height="37" border="0"></A>
                                                                
                                                                <A href="javascript:submitForm22('submitSSIDetailForTenure.do?method=submitSSIDetailForTenure')">
									<IMG src="images/OK.gif" alt="OK" width="49" height="37" border="0"></A>

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
	</BODY>
</HTML>