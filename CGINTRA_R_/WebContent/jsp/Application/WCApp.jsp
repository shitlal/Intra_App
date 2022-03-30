<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean"%>
<%@ page import="com.cgtsi.util.SessionConstants"%>
<%--<%! String focusField="";%>  --%>

<%String focusField=""; 
if(session.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE)!=null)
{
	if(request.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG)!=null && request.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("15") && session.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE).equals("WC"))
	{
		session.setAttribute("CurrentPage","tcMli.do?method=getWCMliInfo");
		focusField="district";
	}
	else if(request.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG)!=null && request.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("16") && session.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE).equals("WC"))
	{
		session.setAttribute("CurrentPage","tcMli.do?method=getWCMliInfo");
		focusField="industrySector";
	}
	else if(session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG)!=null && session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("17"))
	{
		session.setAttribute("CurrentPage","afterTcMli.do?method=getBorrowerDetails");
		focusField="guarantorsName1";

	}

	else if(session.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE).equals("WC"))
	{
		session.setAttribute("CurrentPage","tcMli.do?method=getWCMliInfo");
		focusField="mliRefNo";
	}
}else if(session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("4") /*|| session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("12")*/)
{
session.setAttribute("CurrentPage","afterModifyApp.do?method=showCgpanList");
focusField="mliRefNo";
}

if(focusField.equals(""))
{
	focusField=null;
}

org.apache.struts.action.ActionErrors errors = (org.apache.struts.action.ActionErrors)request.getAttribute(org.apache.struts.Globals.ERROR_KEY);
if (errors!=null && !errors.isEmpty())
{
	focusField="test";
}

%>

<%@ include file="/jsp/SetMenuInfo.jsp" %>

<body onLoad="enableAssistance(),enableNone(),setConstEnabled(),enableDistrictOthers(),enableOtherLegalId(),enableSubsidyName(),calProjectCost(),calProjectOutlay(),enableGender()">


<html:form  action="addWcApp.do?method=submitApp" method="POST" focus="<%=focusField%>">
<html:hidden name="appForm" property="test"/>

<html:errors/>

	<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
		<TR> 
			<TD width="20" align="right" valign="bottom"><IMG src="images/TableLeftTop.gif" width="20" height="31"/></TD>
			<TD background="images/TableBackground1.gif"><IMG src="images/ApplicationProcessingHeading.gif" width="91" height="31"/></TD>
			<TD width="20" align="left" valign="bottom"><IMG src="images/TableRightTop.gif" width="23" height="31"/></TD>
		</TR>
		<TR>
			<TD width="20" background="images/TableVerticalLeftBG.gif">&nbsp;</TD>
			<TD>
				<DIV align="right">			
					<A HREF="javascript:submitForm('workingCapitalHelp.do?method=workingCapitalHelp')">
					HELP</A>
				</DIV>

							<%@ include file="CommonAppDetails.jsp"%>
						<%--	<%@ include file="FacilityDetails.jsp"%> 
							<%@ include file="WCDetails.jsp"%>
							<%@ include file="SecuritizationDetails.jsp"%>	--%>
                                                        
                                                        <jsp:include page="FacilityDetails.jsp" />
                                                        <jsp:include page="WCDetails.jsp" />
                                                        <jsp:include page="SecuritizationDetails.jsp" />
                                                        
							<%
							if (session.getAttribute(SessionConstants.MCGF_FLAG).equals("M"))
							{%>
                                                        <jsp:include page="MCGFDetails.jsp" />
								<%-- <%@include file="MCGFDetails.jsp"%>  --%>
							<%}
							%>
				<TABLE width="100%" border="0" align="left" cellpadding="0" cellspacing="0">
					<%
					String appFlag=session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).toString();
					if(appFlag.equals("4"))
						 {
							%>
					<tr align="left">
						<td class="ColumnBackground" height="28">&nbsp;
							<bean:message key="existingRemarks"/>
						</td>
						<td class="TableData" height="28" colspan="5">
							<bean:write property="existingRemarks" name="appForm"/>
						</td>
					</tr>	
					<%}%>
						
					<tr align="left">
						<td class="ColumnBackground" height="28" colspan="2">&nbsp;
							<bean:message key="remarks"/>
						</td>
						<td class="TableData" height="28" colspan="4">
						<%		
						if(appFlag.equals("12") || appFlag.equals("13"))
						 {
							%>
							<bean:write property="remarks" name="appForm"/>
							<%} else {
							%>
				
							<html:textarea property="remarks" cols="75" alt="address" name="appForm" rows="4"/>	
							<%}%>
							
						</td>
					</tr>	
					
					<TR >
						<TD align="center" valign="baseline" colspan="8">
							<DIV align="center">
							<%	
						if (request.getParameter("detail")!=null)
							{%>
									<A href="javascript:window.close()">
									<IMG src="images/Close.gif" alt="Close" width="49" height="37" border="0"></A>

							<%}
							else if(appFlag.equals("12") || appFlag.equals("13"))
							 {
							%>
							<A href="javascript:history.back()">
									<IMG src="images/Back.gif" alt="Back" width="49" height="37" border="0"></A>
							<%}
							else if(appFlag.equals("4")){%>
								<A href="javascript:submitForm('addWcApp.do?method=submitApp')"><IMG src="images/Save.gif" alt="Save" width="49" height="37" border="0"></A>	
								<A href="javascript:document.appForm.reset()">
									<IMG src="images/Reset.gif" alt="Cancel" width="49" height="37" border="0"></A>
								<A href="home.do?method=getMainMenu&menuIcon=<%=session.getAttribute("menuIcon")%>&mainMenu=<%=session.getAttribute("mainMenu")%>">
								<IMG src="images/Cancel.gif" alt="Cancel" width="49" height="37" border="0"></A>
							<%}
							else{%>
								<A href="javascript:submitForm('addWcApp.do?method=submitApp')"><IMG src="images/Save.gif" alt="Save" width="49" height="37" border="0"></A>	
								<A href="javascript:document.appForm.reset()">
									<IMG src="images/Reset.gif" alt="Cancel" width="49" height="37" border="0"></A>
								<A href="subHome.do?method=getSubMenu&menuIcon=<%=session.getAttribute("menuIcon")%>&mainMenu=<%=session.getAttribute("mainMenu")%>">
								<IMG src="images/Cancel.gif" alt="Cancel" width="49" height="37" border="0"></A>
							<%}%>								
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
		</TABLE>
	</html:form>
</body>