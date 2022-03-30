<%@ page language="java"%>
<%@ page import="com.cgtsi.claim.ClaimConstants"%>
<%@ page import="java.util.*"%>
<%@ page import="com.cgtsi.actionform.ClaimActionForm"%>

<%@ page import="java.text.SimpleDateFormat"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic"%>
<%@ include file="/jsp/SetMenuInfo.jsp"%>
<%
	session.setAttribute("CurrentPage",
			"proceedFromRecoveryFilterPage.do?method=proceedFromRecoveryFilterPage");
%>
<%
	ClaimActionForm claimForm = (ClaimActionForm) session.getAttribute("cpTcDetailsForm");
%>

<html:errors />
<html:form
	action="addFirstClaimsPageDetails.do?method=addFirstClaimsPageDetails"
	method="POST" enctype="multipart/form-data">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">


		<tr>
			<td class="FontStyle">&nbsp;</td>
		</tr>
	</table>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td width="20" align="right" valign="bottom"><img
				src="images/TableLeftTop.gif" alt="" width="20" height="31"></td>
			<td width="248" background="images/TableBackground1.gif"><img
				src="images/ClaimsProcessingHeading.gif" alt="" width="131"
				height="25"></td>
			<td align="right" valign="top"
				background="images/TableBackground1.gif">
			<div align="right"></div>
			</td>
			<td width="23" align="left" valign="bottom"><img
				src="images/TableRightTop.gif" width="23" alt="" height="31"></td>
		</tr>
		<tr>
			<td width="20" background="images/TableVerticalLeftBG.gif">&nbsp;</td>
			<td colspan="2">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td>
					<table width="100%" border="0" cellspacing="1" cellpadding="0">

						<!-- Date of recall notice -->
						
						
						
						
					</table>
					</td>
				</tr>
				
				
				<!-- claim files -->
				<%
						java.util.ArrayList singlefiles = (java.util.ArrayList)request.getAttribute("SINGLECLAIMFILES"); 
						java.util.Map map = (java.util.Map)singlefiles.get(0);
						
				%>
				
				<tr>
					<TD>
					<!-- new table -->
					<table width="100%">
						<tr>
							<td class="TableData" width="2%"><b>1.</b></td>
							<th class="TableData">View Files<font
								color="red">*</font></th>
							<td class="HeadingBg">
							
								<%if(map != null){ %>
								<a href="<%=map.get("FILEPATH") %>"><%=map.get("FILENAME") %></a>
							<%}else{ %>
								No File
							<%} %>
							
							</td>
						</tr>
				
						</tr>
					</table>
					<!-- new table -->
					
					</td>
				</tr>
				
			</table>
			</td>
			<td width="23" background="images/TableVerticalRightBG.gif">&nbsp;</td>
		</tr>


		<tr>
			<td width="20" align="right" valign="bottom"><img
				src="images/TableLeftBottom.gif" alt="" width="20" height="51"></td>
			<td colspan="2" valign="bottom"
				background="images/TableBackground3.gif">

			<div align="center">
			<A href="javascript:printpage()"> <IMG src="images/Print.gif"
				alt="Print" width="49" height="37" border="0"></A></div>
			</td>
			<td width="23" align="right" valign="bottom"><img
				src="images/TableRightBottom.gif" alt="" width="23" height="51"></td>
		</tr>
	</table>
</html:form>

