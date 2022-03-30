<!-- Modified by sudeep.dhiman@pathinfotech.com 24-10-06 -->
<%@ page language="java"%>
<%@ page import="java.util.ArrayList"%>

<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic" %>
<%@ include file="/jsp/SetMenuInfo.jsp" %>
<% session.setAttribute("CurrentPage","applicationWiseReport.do?method=applicationWiseReport");%>


<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form action="applicationWiseReportDetails.do?method=applicationWiseReportDetails" method="POST" enctype="multipart/form-data">
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
<!--start-->    <tr>
                  <td colspan="6" class="Heading1" align="center"><u><bean:message key="reportHeader"/></u>
                  </td>
                </tr>
                <tr>
                  <td colspan="6">&nbsp;</td>
                </tr>
<!--end-->
								<TR>
									<TD colspan="12"> 
										<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
											<TR>
												<TD width="19%" class="Heading"><bean:message key="statusWiseHeader" />
                        </TD>
                       <!-- <td width="13%" class="Heading"><bean:write name="rsForm" property="applicationStatus1"/></td>-->
	<!--start-->					<td class="Heading" width="50%"><bean:write name="radioValue"/>&nbsp;<bean:message key="from"/> <bean:write  name="rsForm" property="dateOfTheDocument24"/>&nbsp;<bean:message key="to"/> <bean:write  name="rsForm" property="dateOfTheDocument25"/>
                        </td>
  <!--end-->
                        <TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
											</TR>
											<TR>
												<TD colspan="3" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
											</TR>

										</TABLE>
									</TD>

									<TR>
									<TD width="3%" align="left" valign="top" class="ColumnBackground" ><bean:message key="sNo"/>
                  </TD>
                  <TD width="9%" align="left" valign="top" class="ColumnBackground">
										<bean:message key="bank"/>
									</TD>
									<TD width="9%" align="left" valign="top" class="ColumnBackground">
										<bean:message key="memberId"/>
									</TD>
									<TD width="9%" align="left" valign="top" class="ColumnBackground">
										<bean:message key="cgpan"/>
									</TD>
                  <TD width="9%" align="right" valign="top" class="ColumnBackground">
										<bean:message key="approvedAmount"/>
									</TD>
                  
									</TR>	
					            <%double total = 0.0;%>
											<tr>
											<logic:iterate name="rsForm" id="object" property="danRaised" indexId="id">
											<%
											com.cgtsi.reports.ProgressReport  details = (com.cgtsi.reports.ProgressReport)object;
											%>
													
											<TR>
		<!--start-->				<td width="3%" cellpadding="3" align="left" valign="top" class="ColumnBackground1"><%=Integer.parseInt(id+"")+1%>
                        </td><!--end-->
                        <TD width="9%" align="left" valign="top" class="ColumnBackground1">	
<!-- scriptlet removed -->
                        <bean:write name="object" property="bankName"/>
                        <!--<%=details.getBankName()%>				-->
												</TD>
												<TD width="9%" align="left" valign="top" class="ColumnBackground1">					
                        <bean:write name="object" property="memberId"/>
                      	<!--<%=details.getMemberId()%>				-->
												</TD>
												<TD width="9%" align="left" valign="top" class="ColumnBackground1">					
												<%String reference=details.getAppRefNo();
												String url = "applicationWiseReportDetails.do?method=applicationWiseReportDetails&number="+reference; 	%>				
												<html:link href="<%=url%>"><%=reference%></html:link>
												</TD>
                        <TD width="9%" align="center" valign="top" class="ColumnBackground1">					
                        <bean:write name="object" property="totalSanctionedAmount"/>
                      	<!--<%=details.getMemberId()%>				-->
												</TD>
                        <%total = total+details.getTotalSanctionedAmount();%>
											</TR>
											</logic:iterate>
                 <!--    <tr>
                       <td valign="top" class="ColumnBackground1" align="right" >Total
                       </td>
                        <td colspan="4" valign="top" class="ColumnBackground1" align="right" >
                         <%=total%>
                        </td>
											</TR>-->
											
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
							<A href="javascript:submitForm('applicationWiseReportInput.do?method=applicationWiseReportInput')">
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

