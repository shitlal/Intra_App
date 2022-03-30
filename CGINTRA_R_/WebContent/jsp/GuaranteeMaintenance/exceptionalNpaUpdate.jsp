<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic"%>

<% session.setAttribute("CurrentPage","exceptionalNpaUpdate.do?method=exceptionalNpaUpdate");%>
<%@ include file="/jsp/SetMenuInfo.jsp" %>
<%
String focusField="";
%>
<logic:equal property="bankId" value="0000" name="gmPeriodicInfoForm">
<%focusField = "memberId";%>
</logic:equal>

<HTML>
	<BODY>

<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form action="exceptionalNpaUpdate.do?method=exceptionalNpaUpdate" method="POST" enctype="multipart/form-data">
		<TR> 
			<TD width="20" align="right" valign="bottom"><IMG src="images/TableLeftTop.gif" alt="" width="20" height="31"></TD>
			<TD background="images/TableBackground1.gif"><IMG src="images/GuaranteeMaintenanceHeading.gif"  alt=""></TD>
			<TD width="20" align="left" valign="bottom"><IMG src="images/TableRightTop.gif"  alt="" width="23" height="31"></TD>
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
												<TD><IMG src="images/TriangleSubhead.gif" alt="" width="19" height="19"></TD>
											</TR>
											<TR>
												<TD colspan="3" class="Heading">
                                                                                                <IMG src="images/Clear.gif" alt="" width="5" height="5"></TD>
											</TR>

										</TABLE>
									</TD>
								</TR>
                                                <tr>
                                                        <TD  align="left" colspan="4"><b><font size="2">
                                                                Fields marked as </font><font color="#FF0000" size="2">*</font><font size="2"> are mandatory</font></b>
                                                        </td>
                                                </tr>
						<TR align="left" valign="top">
									<TD align="left" valign="top" class="ColumnBackground"><bean:message key="bankName"/>
									</TD>
									<TD align="left" valign="top" class="TableData">
										<bean:write property="applicationReport.bankName" name="gmPeriodicInfoForm"/>
									</TD>
                                                                        <TD align="left" valign="top" class="ColumnBackground"><bean:message key="zoneName"/>
									</TD>
									<TD align="left" valign="top" class="TableData">
									 <bean:write property="applicationReport.zoneName" name="gmPeriodicInfoForm"/>
									</TD>
								</TR>
            
                                                <logic:equal property="bankId" value="0000" name="gmPeriodicInfoForm">		
								<TR align="left" valign="top">
									<TD align="left" valign="top" class="ColumnBackground"><bean:message key="MemberID"/>
									</TD>
									<TD align="left" valign="top" class="TableData">
										<bean:write property="memberId" name="gmPeriodicInfoForm"/>
									</TD>
                                                                        <TD align="left" valign="top" class="ColumnBackground"><bean:message key="cgpan"/>
									</TD>
									<TD align="left" valign="top" class="TableData">
									 <bean:write property="cgpan" name="gmPeriodicInfoForm"/>
									</TD>
								</TR>
						</logic:equal>
						 <logic:notEqual property="bankId" value="0000" name="gmPeriodicInfoForm">		
						 		<TR align="left" valign="top">
									<TD align="left" valign="top" class="ColumnBackground"><bean:message key="MemberID"/>
									</TD>
									<TD align="left" valign="top" class="TableData">
										<bean:write property="memberId" name="gmPeriodicInfoForm"/>
									</TD>
                                                                        <TD align="left" valign="top" class="ColumnBackground"><bean:message key="cgpan"/>
									</TD>
									<TD align="left" valign="top" class="TableData">
									 <bean:write property="cgpan" name="gmPeriodicInfoForm"/>
									</TD>
								</TR>
						</logic:notEqual>
                                                                <TR>                								
								  <TD align="left" valign="top" class="ColumnBackground"><bean:message key="ssiName"/>
									</TD>
									<TD align="left" valign="top" class="TableData" colspan="4">
										<bean:write property="applicationReport.ssiName" name="gmPeriodicInfoForm"/>
                                                                    </TD>
								</TR>
                                                                
                                                                <TR align="left" valign="top">
									<TD align="left" valign="top" class="ColumnBackground">
                                                                        Application Status
									</TD>
									<TD align="left" valign="top" class="TableData">
										<bean:write property="applicationReport.status" name="gmPeriodicInfoForm"/>
									</TD>
                                                                        <TD align="left" valign="top" class="ColumnBackground">
                                                                        Application Expiry Date
									</TD>
									<TD align="left" valign="top" class="TableData">
									 <bean:write property="expiryDate" name="gmPeriodicInfoForm"/>
									</TD>
								</TR>
							         
								<TR align="left" valign="top">
									<TD align="left" valign="top"   class="ColumnBackground">
                                                                        <font color="#FF0000" size="2">*</font>Reasons(Npa Exception)
									</TD>
									<TD align="left" valign="top" class="TableData" colspan="4">
              <!--    <html:textarea cols="20" rows="2" property="closureRemarks"   name="gmPeriodicInfoForm" >  </html:textarea> -->
								<html:text property="remarks"  size="100" name="gmPeriodicInfoForm"/>
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
								
									<A href="javascript:submitForm('submitexceptionalNpaUpdaterequest.do?method=submitexceptionalNpaUpdaterequest')">
									<IMG src="images/OK.gif" alt="OK" width="49" height="37" border="0"></A>
									<A href="javascript:document.gmPeriodicInfoForm.reset()">
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
				<IMG src="images/TableLeftBottom1.gif" alt="" width="20" height="15">
			</TD>
			<TD background="images/TableBackground2.gif">
				&nbsp;
			</TD>
			<TD width="20" align="left" valign="top">
				<IMG src="images/TableRightBottom1.gif"  alt="" width="23" height="15">
			</TD>
		</TR>
	</html:form>
</TABLE>
	</BODY>
</HTML>