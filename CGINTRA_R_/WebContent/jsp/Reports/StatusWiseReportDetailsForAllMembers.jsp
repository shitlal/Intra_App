<%@ page language="java"%>
<%@ page import="java.util.ArrayList"%>

<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic" %>
<%@ include file="/jsp/SetMenuInfo.jsp" %>
<% session.setAttribute("CurrentPage","statusWiseReportDetails.do?method=statusWiseReportDetails");%>


<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form action="applicationStatusWiseReportDetails.do?method=applicationStatusWiseReportDetails" method="POST" enctype="multipart/form-data">
		<TR> 
			<TD width="20" align="right" valign="bottom"><IMG src="images/TableLeftTop.gif" width="20" height="31"></TD>
			<TD background="images/TableBackground1.gif"><IMG src="images/ReportsHeading.gif" width="121" height="25"></TD>
			<TD width="20" align="left" valign="bottom"><IMG src="images/TableRightTop.gif" width="23" height="31"></TD>
		</TR>
		<TR>
			<TD width="20" background="images/TableVerticalLeftBG.gif">&nbsp;</TD>
			<TD>
				<TABLE width="100%" border="0" align="left" cellpadding="0" cellspacing="0">
					<TR>
						<TD>
							<TABLE width="100%" border="0" cellspacing="1" cellpadding="1">
								<TR>
									<TD colspan="12"> 
										<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
											<TR>
												<TD width="28%" class="Heading"><bean:message key="statusWiseHeader" /></TD>
												<TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
											</TR>
											<TR>
												<TD colspan="3" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
											</TR>

										</TABLE>
									</TD>

									<TR>
                  <TD  width="3%" align="left" valign="top" class="ColumnBackground">
                        <bean:message key="sNo"/>
                      </TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<bean:message key="bank"/>
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<bean:message key="memberId"/>
									</TD>
									<!-- Added By Sukumar on 09012008 for displaying the Application Ref No -->
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<bean:message key="applicationRefNo"/>
									</TD>
								
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<bean:message key="cgpan"/>
									</TD>
										</TR>	
					
											<tr>
											<logic:iterate name="rsForm" id="object" property="mliWiseReport" indexId="index">
											<%
											com.cgtsi.reports.ProgressReport  details = (com.cgtsi.reports.ProgressReport)object;
											%>
													
											<TR>
                      <TD align="left" valign="top" class="ColumnBackground1"><%=Integer.parseInt(index+"")+1%>
                      </TD>
												<TD width="10%" align="left" valign="top" class="ColumnBackground1">	
												<%=details.getBankName()%>				
												</TD>
												<TD width="10%" align="left" valign="top" class="ColumnBackground1">					
												<%=details.getMemberId()%>				
												</TD>
												<!-- Added by sukumar for displaying the app ref no -->
												<TD width="10%" align="left" valign="top" class="ColumnBackground1">
												<%=details.getApplicationRefNo()%>
												</TD>
												<!-- end here -->
												<TD width="10%" align="left" valign="top" class="ColumnBackground1">					
												<%String reference=details.getAppRefNo();
												String url = "applicationWiseReportDetailsForCgpan.do?method=applicationWiseReportDetailsForCgpan&number="+reference; 	%>				
												<html:link href="<%=url%>"><%=reference%></html:link>
												</TD>
												

											</TR>
											</logic:iterate>
   
							</TABLE>
						</TD>
					</TR>
					<TR >
						<TD height="20" >
							&nbsp;
						</TD>
					</TR>
					<TR >
						<TD align="center" valign="baseline">
							<DIV align="center">
							<A href="javascript:submitForm('statusWiseReport.do?method=statusWiseReport')">
									<IMG src="images/Back.gif" alt="Back" width="49" height="37" border="0"></A>

									
									<A href="javascript:printpage()">
									<IMG src="images/Print.gif" alt="Print" width="49" height="37" border="0"></A>
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

