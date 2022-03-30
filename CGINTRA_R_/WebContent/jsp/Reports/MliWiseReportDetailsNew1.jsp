<!-- modified by sudeep.dhiman@pathinfotech.com-->

<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic" %>
<%@ include file="/jsp/SetMenuInfo.jsp" %>
<%@page import ="java.text.DecimalFormat"%>
<%DecimalFormat decimalFormat = new DecimalFormat("##########0.00");%>
<% session.setAttribute("CurrentPage","mliWiseReportDetailsNew.do?method=mliWiseReportDetailsNew");%> 
<%@page import = "org.apache.struts.action.DynaActionForm"%>
<% DynaActionForm dynaForm = (DynaActionForm)session.getAttribute("rsForm");
   String value = (String)dynaForm.get("checkValue");%>

<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form action="mliWiseReportDetailsNew.do?method=mliWiseReportDetailsNew" method="POST" enctype="multipart/form-data">
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
               <tr>
                  <td colspan="6" class="Heading1" align="center"><u><bean:message key="reportHeader"/></u>
                  </td>
                </tr>
                <tr>
                  <td colspan="6">&nbsp;</td>
                </tr>
								<TR>
									<TD colspan="4"> 
										<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
											<TR>
												<TD width="17%" class="Heading"><bean:message key="mliWiseHeader" /></TD>
                        <td class="Heading" width="50%"><bean:write name="radioValue"/>&nbsp;<bean:message key="from"/><bean:write  name="rsForm" property="dateOfTheDocument12"/>&nbsp;<bean:message key="to"/> <bean:write  name="rsForm" property="dateOfTheDocument13"/>
                        </td>
												<TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
											</TR>
											<TR>
												<TD colspan="3" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
											</TR>
											</TABLE>
									</TD>
								</TR>

								<TR align="left" valign="top">
                  <td align="left" valign="top" class="ColumnBackground"><bean:message key="sNo"/></td>
                  <TD align="left" valign="top" class="ColumnBackground">
										&nbsp;<bean:message key="mli"/>
									</TD>
									<TD align="left" class="ColumnBackground"> 
									<bean:message key="proposals"/>
									</TD>
  								<% if (value.equals("yes")){%>	
										<TD align="left" class="ColumnBackground"> 
										<bean:message key="amountApprovedLakhs"/>
										</TD>
                    
									<%}	else{%>
										<TD align="left" class="ColumnBackground"> 
										<bean:message key="amountIssuedLakhs"/>
										</TD>
									<%}
                    int count       = 0;
										int totalCount  = 0;
										double sum      = 0.0;
										double totalSum = 0.0;
									%>
									</TR>
									
									<tr>
									<logic:iterate id="object" name="rsForm" property="mliWiseReportNew" indexId="id">
									<% com.cgtsi.reports.GeneralReport dReport =  (com.cgtsi.reports.GeneralReport)object;%>

									<TR align="left" valign="top">
                  <td align="left" valign="top" class="ColumnBackground1"><%=Integer.parseInt(id+"")+1%></td>
                  <TD align="left" valign="top" class="ColumnBackground1">
									<% String bank = dReport.getType();
									String url = "zoneWiseReportDetails.do?method=zoneWiseReportDetails&Zone="+bank;
									int i = bank.indexOf("&");
									if(i == -1)
									{	%>
									<html:link href="<%=url%>"><%= bank%></html:link>
									<%}
                  else{
									String newZone = bank.replace('&','$');
									String newUrl = "zoneWiseReportDetails.do?method=zoneWiseReportDetails&Zone="+newZone;
									%>
									<html:link href="<%=newUrl%>"><%= bank%></html:link>
									<%}%>
									|							
									<% String bank1 = dReport.getType();
									String url1 = "sectorWiseReportDetails1.do?method=sectorWiseReportDetails1&Sector="+bank1;
									int j = bank1.indexOf("&");
									if(j == -1)
									{	%>
                  <html:link href="<%=url1%>">SectorWise</html:link>
									<%}
									else{
									String newZone1 = bank1.replace('&','$');
									String newUrl1 = "sectorWiseReportDetails1.do?method=sectorWiseReportDetails1&Sector="+newZone1;
									%>
									<html:link href="<%=newUrl1%>">SectorWise</html:link>
									<%}%>
									|
									<% String bank2 = dReport.getType();
									String url2 = "statesWiseReport.do?method=statesWiseReport&state="+bank2;
									int k = bank2.indexOf("&");
									if(k == -1)
									{%>
									<html:link href="<%=url2%>">StateWise</html:link>
									<%}
									else{
									String newZone2 = bank2.replace('&','$');
									String newUrl2 = "statesWiseReport.do?method=statesWiseReport&state="+newZone2;
									%>
									<html:link href="<%=newUrl2%>">StateWise</html:link>
									<%}%>
									</TD>
									<TD align="left" class="ColumnBackground1"> 
									<div align="right">
									<%count=dReport.getProposals();%>
									<%totalCount = count + totalCount;%>
									<%=dReport.getProposals()%>
									</div>
									</TD>
									<TD align="left" class="ColumnBackground1"> 
									<div align="right">
									<%double amount=dReport.getAmount();
									sum = amount/100000;%>
									<% totalSum = totalSum+Double.parseDouble(decimalFormat.format(sum));%>
									<%=decimalFormat.format(sum)%>
									</div>
									</TD>
									</TR> 
								</logic:iterate>

								<tr>
									<TD align="left" valign="top" colspan="2" class="ColumnBackground">
									Total
									</TD>
									<TD align="left" class="ColumnBackground"> 
									<div align="right">
									<%=totalCount%>
									</div>
									</TD>
									<TD align="left" class="ColumnBackground"> 
									<div align="right">
									<%=decimalFormat.format(totalSum)%>
									</div>
									</TD>
								</tr>

								</TABLE>
						</TD>
					</TR>
					<TR >
						<TD height="20" >
							&nbsp;
						</TD>
					</TR>
					<TR>
						<TD align="center" valign="baseline">
							<DIV align="center">
							<A href="javascript:submitForm('mliWiseReportNew.do?method=mliWiseReportNew')">
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

