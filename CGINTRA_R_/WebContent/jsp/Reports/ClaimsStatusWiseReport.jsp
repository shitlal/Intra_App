<%@ page language="java"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.cgtsi.claim.ClaimConstants"%>
<%@ page import="com.cgtsi.actionform.ClaimActionForm"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>

<%@ include file="/jsp/SetMenuInfo.jsp" %>
<% session.setAttribute("CurrentPage","showFilterForClaimDetailsNew.do?method=showFilterForClaimDetailsNew");%>

<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form action="displayListOfClaimRefNumbersNew.do?method=displayListOfClaimRefNumbersNew" method="POST" enctype="multipart/form-data">
		<TR> 
			<TD width="22" align="right" valign="bottom"><IMG src="images/TableLeftTop.gif" alt="" width="20" height="31"></TD>
			 <TD background="images/TableBackground1.gif">
                            &nbsp;&nbsp;&nbsp;&nbsp;
                        </TD> 
			<TD width="22" align="left" valign="bottom"><IMG src="images/TableRightTop.gif" alt="" width="23" height="31"></TD>
		</TR>
		<TR>
			<TD width="22" background="images/TableVerticalLeftBG.gif">&nbsp;</TD>
			<TD width="927">
			      <DIV align="right">			
			      		<A HREF="javascript:submitForm('helpClaimStatusWiseReport.do')">
			      	        HELP</A>
			      </DIV>        
				<TABLE width="100%" border="0" align="left" cellpadding="0" cellspacing="0">
					<TR>
						<TD>
							<TABLE width="100%" border="0" cellspacing="1" cellpadding="1">
								<TR>
									<TD colspan="10"> 
										<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
											<TR>
												<TD width="27%" class="Heading"><bean:message key="danReportHeader" /></TD>
												<TD><IMG src="images/TriangleSubhead.gif" alt="" width="19" height="19"></TD>
											</TR>
											<TR>
												<TD colspan="3" class="Heading"><IMG src="images/Clear.gif" alt="" width="5" height="5"></TD>
											</TR>

										</TABLE>
									</TD>

									<TR>
									<TD align="left" valign="top" class="ColumnBackground">
									<DIV align="center">
										  &nbsp;<bean:message key="fromdate" /> 
									</DIV>
									</TD>
									   <TD  align="left" valign="center" class="TableData">
										  <DIV align="left">
										  <html:text property="fromDate" size="20" maxlength="10" name="cpTcDetailsForm"/>
										  <IMG src="images/CalendarIcon.gif" alt="" width="20" onClick="showCalendar('cpTcDetailsForm.fromDate')" align="center">
										  </div>
									  </TD>
									
									  <TD align="left" valign="top" class="ColumnBackground">
									<DIV align="center">
										  &nbsp;<font color="#FF0000" size="2">*</font><bean:message key="toDate"/> 
										  </DIV>
									</TD>

									    <TD  align="left" valign="center" class="TableData">
										  <DIV align="left">
										  <html:text property="toDate" size="20" maxlength="10"  name="cpTcDetailsForm"/>
										  <IMG src="images/CalendarIcon.gif" alt="" width="20" onClick="showCalendar('cpTcDetailsForm.toDate')" align="center">
										  </DIV>
									  </TD>
									  </TR>

									<TR>
									<TD colspan="4" align="left" valign="top" class="ColumnBackground">
									<DIV align="center">
									<html:radio name="cpTcDetailsForm" value="<%=ClaimConstants.CLM_APPROVAL_STATUS%>" property="clmApplicationStatus" ><bean:message key="approved" /></html:radio>
									<html:radio name="cpTcDetailsForm" value="<%=ClaimConstants.CLM_PENDING_STATUS%>" property="clmApplicationStatus" ><bean:message key="newDetails1" /></html:radio>&nbsp;&nbsp;
									<html:radio name="cpTcDetailsForm" value="<%=ClaimConstants.CLM_REJECT_STATUS%>" property="clmApplicationStatus" ><bean:message key="rejected" /></html:radio>&nbsp;&nbsp;
									<html:radio name="cpTcDetailsForm" value="<%=ClaimConstants.CLM_HOLD_STATUS%>" property="clmApplicationStatus" ><bean:message key="hold" /></html:radio>&nbsp;&nbsp;
									<html:radio name="cpTcDetailsForm" value="<%=ClaimConstants.CLM_FORWARD_STATUS%>" property="clmApplicationStatus" ><bean:message key="forward" /></html:radio>&nbsp;&nbsp;
									
									<html:radio name="cpTcDetailsForm" value="<%=ClaimConstants.CLM_REPLY_RECEIVED%>" property="clmApplicationStatus" >Reply Received </html:radio>&nbsp;&nbsp;
                                                                        
                                                                        </DIV>
									</TD>
									</TR>
                                                                        <TR>
									<TD colspan="4" align="left" valign="top" class="ColumnBackground">
									<DIV align="center">
									
									
									<html:radio name="cpTcDetailsForm" value="<%=ClaimConstants.CLM_TEMPORARY_CLOSE%>" property="clmApplicationStatus" >Temporary Closed</html:radio>&nbsp;&nbsp;
									<html:radio name="cpTcDetailsForm" value="<%=ClaimConstants.CLM_TEMPORARY_REJECT%>" property="clmApplicationStatus" >Temporary Rejected</html:radio>&nbsp;&nbsp;
									<html:radio name="cpTcDetailsForm" value="<%=ClaimConstants.CLM_WITHDRAWN%>" property="clmApplicationStatus" >Claim Withdrawn </html:radio>&nbsp;&nbsp;
                                                        
                                                                        <html:radio name="cpTcDetailsForm" value="RT" property="clmApplicationStatus" >Returned</html:radio>
                                                                        </DIV>
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
									<A href="javascript:submitForm('displayListOfClaimRefNumbersNew.do?method=displayListOfClaimRefNumbersNew')">
									<IMG src="images/OK.gif" alt="OK" width="49" height="37" border="0"></A>
								<A href="javascript:document.cpTcDetailsForm.reset()">
									<IMG src="images/Reset.gif" alt="Reset" width="49" height="37" border="0"></A>									
									<a href="subHome.do?method=getSubMenu&menuIcon=<%=session.getAttribute("menuIcon")%>&mainMenu=<%=session.getAttribute("mainMenu")%>">
									<IMG src="images/Cancel.gif" alt="Cancel" width="49" height="37" border="0"></A>
							</DIV>
						</TD>
					</TR>
				</TABLE>
			</TD>
			<TD width="22" background="images/TableVerticalRightBG.gif">
				&nbsp;
			</TD>
		</TR>
		<TR>
			<TD width="22" align="right" valign="top">
				<IMG src="images/TableLeftBottom1.gif" alt="" width="20" height="15">
			</TD>
			<TD background="images/TableBackground2.gif">
				&nbsp;
			</TD>
			<TD width="22" align="left" valign="top">
				<IMG src="images/TableRightBottom1.gif" alt="" width="23" height="15">
			</TD>
		</TR>
	</html:form>
</TABLE>

