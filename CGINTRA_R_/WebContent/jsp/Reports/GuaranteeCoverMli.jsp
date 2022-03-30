<%@ page language="java"%>
<%@ page import="java.util.ArrayList"%>

<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic" %>
<%@ include file="/jsp/SetMenuInfo.jsp" %>
<% session.setAttribute("CurrentPage","guaranteeCoverReport.do?method=guaranteeCoverReport");%>

<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form action="danRaisedReport.do?method=danRaisedReport" method="POST" enctype="multipart/form-data">
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
                       <tr>
                          <td colspan="6" class="Heading1" align="center"><u><bean:message key="reportHeader"/></u></td>
                      </tr>
                      <tr> <td colspan="6">&nbsp;</td></tr>
											<TR>
												<TD width="36%" class="Heading"><bean:message key="cpmliname" /></TD>
												<td class="Heading" width="50%">&nbsp;<bean:write name="radioValue"/>&nbsp;<bean:message key="from"/> <bean:write  name="rsForm" property="dateOfTheDocument"/>&nbsp;<bean:message key="to"/> <bean:write  name="rsForm" property="dateOfTheDocument1"/></td>
                        
                        <TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
											</TR>
											<TR>
												<TD colspan="3" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
											</TR>

										</TABLE>
									</TD>

					            <tr>
                          <td width="3%" align="left" valign="top" class="ColumnBackground"><bean:message key="sNo"/></td>
                          <td align="left" valign="top" class="ColumnBackground"><bean:message key="mliName"/></td>
                      </tr>
											<tr>
											<logic:iterate name="rsForm" id="object" property="guaranteeCoverSsi" indexId="index">
											<%
											com.cgtsi.reports.GuaranteeCoverIssued danReport = (com.cgtsi.reports.GuaranteeCoverIssued)object;
											%>

											<TR>
											<td align="left" valign="top" class="ColumnBackground1"><%=Integer.parseInt(index+"")+1%></td>
                        
                      <TD align="center" valign="top" class="ColumnBackground1">	
											<% String mli = danReport.getMemberShortName(); 
											String memberId = danReport.getMemberId(); 
											String url = "guaranteeCoverReportDetails.do?method=guaranteeCoverReportDetails&bank="+memberId;
											%>
											<html:link href="<%=url%>"><%=mli%></html:link>

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
						<TD align="center" valign="baseline" >
							<DIV align="center">
							<A href="javascript:submitForm('guaranteeCover.do?method=guaranteeCover')">
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

