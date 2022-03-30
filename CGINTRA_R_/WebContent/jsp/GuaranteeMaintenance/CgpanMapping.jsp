<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic" %>
<%@ include file="/jsp/SetMenuInfo.jsp" %>
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.cgtsi.util.SessionConstants"%>
<%
session.setAttribute("CurrentPage","viewCgpanMapping.do?method=viewCgpanMapping");

%>

<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form action="viewCgpanMapping.do?method=viewCgpanMapping" method="POST">
		<TR> 
			<TD width="20" align="right" valign="bottom"><IMG src="images/TableLeftTop.gif" width="20" height="31"></TD>
			<TD background="images/TableBackground1.gif"></TD>
			<TD width="20" align="left" valign="bottom"><IMG src="images/TableRightTop.gif" width="23" height="31"></TD>
		</TR>
		<TR>
			<TD width="20" background="images/TableVerticalLeftBG.gif">&nbsp;</TD>
			<TD>
				<TABLE width="100%" border="0" align="left" cellpadding="0" cellspacing="0">
					<TR>
						<TD>
							<table width="661" border="0" cellspacing="1" cellpadding="0">
							 <TR>
								<TD colspan="7">
									<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
										<TR>
											<TD width="31%" class="Heading"><bean:message key="viewCgpanMapping" /></TD>
											<TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
										</TR>
										<TR>
											<TD colspan="6" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
										</TR>
									</TABLE>
								</TD>
							</TR>
							</table>
							
							<table width="661" border="0" cellspacing="1" cellpadding="1">
								<tr align="left">
									<td class="ColumnBackground" width="207">
										<div align="left">
										  &nbsp;<bean:message key="oldCgpan" />
										</div>
									 </td>
									<td class="ColumnBackground" width="207">
										<div align="left">
										  &nbsp;<bean:message key="newCgpan" />
										</div>
									 </td>
								</tr>
								<logic:iterate name="gmApprovalForm" property="cgpanMapping" id="object">
								<%
									ArrayList mapping = (ArrayList) object;
									String oldCgpan = (String)mapping.get(0);
									String newCgpan = (String)mapping.get(1);
									mapping=null;
								%>
								<tr align="left">
									 <td class="TableData" width="207">
										<div align="left">
										<%=oldCgpan%>
										</div>
									 </td>
									 <td class="TableData" width="207">
										<div align="left">
										<%=newCgpan%>
										</div>
									 </td>
								</tr>
								</logic:iterate>
							 </table>									
						</td>
					</tr>

					
					<TR >
						<TD align="center" valign="baseline" >
							<DIV align="center">								
								<A href="home.do?method=getMainMenu&menuIcon=<%=session.getAttribute("menuIcon")%>&mainMenu=<%=session.getAttribute("mainMenu")%>">
								<IMG src="images/OK.gif" alt="OK" width="49" height="37" border="0"></A>
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