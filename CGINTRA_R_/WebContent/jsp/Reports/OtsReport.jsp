<%@ page language="java"%>
<%@ page import="java.util.ArrayList"%>
<%@page import ="java.text.SimpleDateFormat"%>
<%@page import ="java.text.DecimalFormat"%>

<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic" %>
<%@ include file="/jsp/SetMenuInfo.jsp" %>
<% session.setAttribute("CurrentPage","otsReport.do?method=otsReport");%>
<%SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");%>
<%DecimalFormat decimalFormat = new DecimalFormat("##########.##");%>

<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form action="otsReport.do?method=otsReport" method="POST" enctype="multipart/form-data">
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
									<TD colspan="8"> 
										<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
											<tr>
                          <td colspan="6" class="Heading1" align="center"><u><bean:message key="reportHeader"/></u></td>
                      </tr>
                      <tr> <td colspan="6">&nbsp;</td></tr>
                      <TR>
												<TD width="30%" class="Heading"><bean:message key="otsheader" /></TD>
												<td class="Heading" width="40%">&nbsp;<bean:message key="from"/><bean:write  name="rsForm" property="dateOfTheDocument32"/>&nbsp;<bean:message key="to"/> <bean:write  name="rsForm" property="dateOfTheDocument33" /></td>
                        <TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
											</TR>
											<TR>
												<TD colspan="3" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
											</TR>

										</TABLE>
									</TD>

									<TR>
                  <TD width="3%" align="left" valign="top" class="ColumnBackground"><bean:message key="sNo"/></TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<bean:message key="cgbid" />
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<bean:message key="borrowerName" />
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<bean:message key="otsReason" />
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<bean:message key="otsDecision" />
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<bean:message key="otsRemarks"/>
									</TD>
									</TR>	

									<tr>
									<logic:iterate id="object" name="rsForm" property="danRaised" indexId="index">
									<%
									     com.cgtsi.reports.OtsDetails dReport =  (com.cgtsi.reports.OtsDetails)object;
										 
									%>
							
											<TR>
                      <TD align="left" valign="top" class="ColumnBackground1"><%=Integer.parseInt(index+"")+1%>
                      </TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">
											<%String bid=dReport.getBid();
											String url = "otsReportDetails.do?method=otsReportDetails&danValue="+bid;%>
											<html:link href="<%=url%>"><%=bid%></html:link>
											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">						
											<%=dReport.getBorrowerName()%>
											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">						
											<%=dReport.getOtsReason()%>
											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">
											<div align="right">
											<%=dReport.getOtsDecision()%>
											    </div>
											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">		
											<div align="right">
											<%String remarks=dReport.getOtsRemarks();
											if((remarks == null) || (remarks.equals("")))
											{
											%>
											<%=""%>
											<%
											}
											else
											{
											%>
											<%=remarks%>
											<%
											}
											%>
											   </div>
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
								<A href="javascript:submitForm('otsReportInput.do?method=otsReportInput')">
									<IMG src="images/Back.gif" alt="Print" width="49" height="37" border="0"></A>

									
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

