<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<% session.setAttribute("CurrentPage","updateAccount.do?method=updateAccount");%>
<%@ include file="/jsp/SetMenuInfo.jsp" %>

<HTML>
<head>
	<script type="text/javascript">
	function isNumber(evt) 
	{
    	var iKeyCode = (evt.which) ? evt.which : evt.keyCode;	        	
    	if (iKeyCode != 46 && iKeyCode > 31 && (iKeyCode < 48 || iKeyCode > 57))
        	return false;
    	return true;
	}					
	</script>
</head>
	<BODY>
		<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
			<html:form action="updateAccDetail.do?method=updateAccDetail" method="POST">			
				<html:errors />
				<TR> 
					<TD width="20" align="right" valign="bottom"><IMG src="images/TableLeftTop.gif" width="20" height="31"></TD>
					<TD background="images/TableBackground1.gif"></TD>
					<TD width="20" align="left" valign="bottom"><IMG src="images/TableRightTop.gif" width="23" height="31"></TD>
				</TR>
				<TR>
					<TD width="20" background="images/TableVerticalLeftBG.gif">&nbsp;</TD>
					<TD>
						<TABLE width="100%" border="0" align="left" cellpadding="0" cellspacing="0">
							<TR>
								<TD>
									<DIV align="right">			
										<A HREF="#">HELP</A>
									</DIV>
									<TABLE width="100%" border="0" cellspacing="1" cellpadding="1">
										<tr>
											<TD  align="left" colspan="4"><font size="2"><b>Fields marked as</b></font>
												<font color="#FF0000" size="2">*</font><font size="2"><b>are mandatory</b></font>
											</td>										
										</tr>
										<TR>
											<TD colspan="4"> 
												<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
													<TR>
														<TD width="31%" class="Heading">Update Account Details</TD>
														<TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
													</TR>
													<TR>
														<TD colspan="3" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
													</TR>
												</TABLE>
											</TD>
										</TR>
										<TR align="left">
											<TD align="left" valign="top" class="ColumnBackground">&nbsp;
												<font color="#FF0000" size="2">*</font><bean:message key="mliName" />
											</TD>
											<TD align="left" class="TableData"> 
												<html:text property="member" name="regForm" />
											</TD>
										</TR>
										<TR align="left">
											<TD align="left" valign="top" class="ColumnBackground">&nbsp;
												<font color="#FF0000" size="2">*</font><bean:message key="mliID" />
											</TD>
											<TD align="left" class="TableData"> 
												<!--<html:text property="memberId" name="regForm" maxlength="12" size="12" onkeypress="javascript:return isNumber (event)"/>-->
												<bean:write property="memberId" name="regForm"/>
											</TD>
										</TR>		
										<TR align="left">
											<TD align="left" valign="top" class="ColumnBackground">&nbsp;
												<font color="#FF0000" size="2">*</font><bean:message key="contNo"/>
											</TD>
											<TD align="left" class="TableData"> 
												<html:text property="phoneNo" name="regForm" maxlength="12" size="20"/>
											</TD>
										</TR>
										<TR align="left">
											<TD align="left" valign="top" class="ColumnBackground">&nbsp;
												<font color="#FF0000" size="2">*</font><bean:message key="mobNo"/>
											</TD>
											<TD align="left" class="TableData"> 
												<html:text property="phone" name="regForm" maxlength="10" size="20"/>
											</TD>
										</TR>
										<TR align="left">
											<TD align="left" valign="top" class="ColumnBackground">&nbsp;
												<font color="#FF0000" size="2">*</font><bean:message key="emailId" />
											</TD>
											<TD align="left" class="TableData"> 
												<html:text property="emailId" name="regForm" />
											</TD>
										</TR>
										<TR align="left">
											<TD align="left" valign="top" class="ColumnBackground">&nbsp;
												<font color="#FF0000" size="2">*</font><bean:message key="beneficiary"/>
											</TD>
											<TD align="left" class="TableData"> 
												<html:text property="beneficiary" name="regForm" />
											</TD>
										</TR>
										<TR align="left">
											<TD align="left" valign="top" class="ColumnBackground">&nbsp;
												<font color="#FF0000" size="2">*</font><bean:message key="accountType" />
											</TD>
											<TD align="left" class="TableData"> 
												<html:text property="accountType" name="regForm" />
											</TD>
										</TR>	
										<TR align="left">
											<TD align="left" valign="top" class="ColumnBackground">&nbsp;
												<font color="#FF0000" size="2">*</font><bean:message key="brnCode"/>
											</TD>
											<TD align="left" class="TableData"> 
												<html:text property="branchId" name="regForm" />
											</TD>
										</TR>
										<TR align="left">
											<TD align="left" valign="top" class="ColumnBackground">&nbsp;
												<font color="#FF0000" size="2">*</font><bean:message key="micrCode"/>
											</TD>
											<TD align="left" class="TableData"> 
												<html:text property="micrCode" name="regForm" />
											</TD>
										</TR>								
										<TR align="left">
											<TD align="left" valign="top" class="ColumnBackground">&nbsp;
												<font color="#FF0000" size="2">*</font><bean:message key="accountNo"/>
											</TD>
											<TD align="left" class="TableData">
												<html:text property="accNo" name="regForm" />
											</TD>
										</tr>
										<TR align="left">
											<TD align="left" valign="top" class="ColumnBackground">&nbsp;
												<font color="#FF0000" size="2">*</font><bean:message key="rtgsNo"/>
											</TD>
											<TD align="left" valign="top" class="TableData">
												<html:text property="rtgsNO" name="regForm" />	
											</TD>
										</TR>
										<TR align="left">
											<TD align="left" valign="top" class="ColumnBackground">&nbsp;
												<font color="#FF0000" size="2">*</font><bean:message key="neftNo"/>
											</TD>
											<TD align="left" valign="top" class="TableData">
												<html:text property="neftNO" name="regForm" />
											</TD>
										</TR>
									</TABLE>
								</TD>
							</TR>
							<TR ><TD height="20" >&nbsp;</TD></TR>
							<TR >
								<TD align="center" valign="baseline" >
									<DIV align="center">
										<A href="javascript:submitForm('updateAccDetail.do?method=updateAccDetail')">
											<IMG src="images/Save.gif" alt="Save" width="49" height="37" border="0">
										</A>
										<A href="javascript:document.regForm.reset()">
											<IMG src="images/Reset.gif" alt="Cancel" width="49" height="37" border="0">
										</A>
                                        <A href="subHome.do?method=getSubMenu&menuIcon=<%=session.getAttribute("menuIcon")%>&mainMenu=<%=session.getAttribute("mainMenu")%>">
											<IMG src="images/Cancel.gif" alt="Cancel" width="49" height="37" border="0">
										</A>																
									</DIV>
								</TD>
							</TR>
						</TABLE>
					</TD>
					<TD width="20" background="images/TableVerticalRightBG.gif">&nbsp;</TD>
				</TR>
				<TR>
					<TD width="20" align="right" valign="top">
						<IMG src="images/TableLeftBottom1.gif" width="20" height="15">
					</TD>
					<TD background="images/TableBackground2.gif">&nbsp;</TD>
					<TD width="20" align="left" valign="top">
						<IMG src="images/TableRightBottom1.gif" width="23" height="15">
					</TD>
				</TR>
			</html:form>			
		</TABLE>		
	</BODY>
</HTML>