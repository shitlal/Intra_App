<%@ page language="java"%>
<%@ page import="java.util.ArrayList"%>

<%@page import ="java.text.SimpleDateFormat"%>
<%@page import ="java.text.DecimalFormat"%>

<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic" %>
<%@ include file="/jsp/SetMenuInfo.jsp" %>
<%SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");%>
<%DecimalFormat decimalFormat = new DecimalFormat("##########.##");%>
<% session.setAttribute("CurrentPage","applicationHistoryReportDetails.do?method=cgpanReductionReportDetails");%>

<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form action="applicationHistoryReportDetails.do?method=cgpanReductionReportDetails" method="POST" enctype="multipart/form-data">
	
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
									<TD colspan="10"> 
                  <TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
                      <tr>
                          <td colspan="6" class="Heading1" align="center"><u>Credit Guarantee Fund Trust for Micro and Small Enterprises </u></td>
                      </tr>
                      <tr> <td colspan="6">&nbsp;</td></tr>
                      	<tr> 
                  <td class="DanReport"> <div align="left">&nbsp; <b><bean:message key="memberIdForApplication" /></b><bean:write property="applicationReport.memberId" name="rsForm"/></div></td>
		          <td class="DanReport"> <div align="left">&nbsp; <b><bean:message key="cgpanForApplication" /></b><bean:write property="applicationReport.cgpan" name="rsForm"/></div></td>
                  </tr>
                   <tr> <td colspan="6">&nbsp;</td></tr>
                  	<tr> 
                  <td> <div align="left">&nbsp; <b><bean:message key="approveAmt"/> : </b><bean:write property="applicationReport.appApprovedAmount" name="rsForm"/></div></td>

                  </tr>
                  <tr> <td colspan="6">&nbsp;</td></tr>
											<TR>
                      </table>
										<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
										<td colspan="1">
											<TR>
												<TD width="25%" class="Heading"><bean:message key="cgpanreductiondetail" /> </TD>
												<TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
											</TR>
											<TR>
										<TD  width="5%" colspan="12" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
												</TR>
												
                  <TR align="left" valign="top">
							   	<TD  align="left" class="ColumnBackground">
									<bean:message key="oldfbsanctionAmount"/>
									</TD>
                <!--  <TD  align="left" class="ColumnBackground"> 
                  &nbsp;CGPAN
									
									</TD>
                  -->
                  <TD  align="left" class="ColumnBackground"> 
									<bean:message key="newfbsanctionAmount"/>
									</TD>
									
					<TD  align="left" class="ColumnBackground"> 
									<bean:message key="oldnfbsanctionAmount"/>
									</TD>				
									
                    <TD  align="left" class="ColumnBackground"> 
								<bean:message key="newnfbsanctionAmount"/>
            			</TD>
                  <TD width = "25%" align="left" class="ColumnBackground"> 
								&nbsp;<bean:message key="reductiondate"/>
									</TD>
                
              		</TR>
                  <tr>
                  	<logic:iterate id="object" name="rsForm" property="cgpanReductionHistoryReport" indexId="id">

									<% com.cgtsi.reports.ApplicationReport dReport =  (com.cgtsi.reports.ApplicationReport)object;%>

									<TR align="left" valign="top">
							     <TD  align="left" valign="top" class="ColumnBackground1">
                   <%=dReport.getOLD_FB_SANCTIONED_AMOUNT() %>&nbsp;
                   </TD>
                   <TD  align="left" valign="top" class="ColumnBackground1">
                   <%=dReport.getREDUCTION_FB_SANCTIONED_AMOUNT()%>
                   </TD> 
                    <TD  align="left" valign="top" class="ColumnBackground1">
                   &nbsp;<%=dReport.getOLD_NFB_SANCTIONED_AMOUNT()%>
                   </TD>
                   
                    <TD  align="left" valign="top" class="ColumnBackground1">
                   &nbsp;<%=dReport.getREDUCTION_NFB_SANCTIONED_AMOUNT()%>
                   </TD>
                   
                    <TD  align="left" valign="top" class="ColumnBackground1">
                   &nbsp;<%=dReport.getREDUCTION_DATE()%>
                   </TD>
                 
                 
                  </tr>
                 </logic:iterate>
                 </td>
                 <td colspan="0">
											<TR>
												<TD width="25%" class="Heading"><bean:message key="cgpanenhancedetail" /> </TD>
												<TD><IMG src="images/TriangleSubhead.gif" width="15" height="19"></TD>
											</TR>
											<TR>
										<TD  width="5%" colspan="10" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
												</TR>
												
           
                  <TD  align="left" class="ColumnBackground"> 
									&nbsp;<bean:message key="newfbenhanceAmount"/>
									</TD>
									
							
                    <TD  align="left" class="ColumnBackground"> 
							&nbsp;<bean:message key="newnfbenhanceAmount"/>
            			</TD>
                  <TD width = "25%" align="left" class="ColumnBackground"> 
								&nbsp;<bean:message key="enhancedate"/>
									</TD>
                
              		</TR>
                  <tr>
                  	<logic:iterate id="object" name="rsForm" property="cgpanEnhanceHistoryReport" indexId="id">

									<% com.cgtsi.reports.ApplicationReport dReport1 =  (com.cgtsi.reports.ApplicationReport)object;%>

									<TR align="left" valign="top">
				
                   <TD  align="left" valign="top" class="ColumnBackground1">
                   <%=dReport1.getENHANCE_FB_SANCTIONED_AMOUNT()%>
                   </TD> 
               
                    <TD  align="left" valign="top" class="ColumnBackground1">
                   &nbsp;<%=dReport1.getENHANCE_NFB_SANCTIONED_AMOUNT()%>
                   </TD>
                   
                    <TD  align="left" valign="top" class="ColumnBackground1">
                   &nbsp;<%=dReport1.getENHANCE_DATE()%>
                   </TD>
                 
                 
                  </tr>
                 </logic:iterate>
                 </td>
									</TABLE>
								</TD>

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
									<A href="javascript:submitForm('applicationHistoryReport.do?method=cgpanReductionEnhanceReport')">
									<IMG src="images/OK.gif" alt="OK" width="49" height="37" border="0"></A>

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
		
		<td></td>
	</html:form>
</TABLE>

