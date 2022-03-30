<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic" %>

<%@ include file="/jsp/SetMenuInfo.jsp" %>
<% session.setAttribute("CurrentPage","showMliListPath.do?method=showMliListPath");%>

<TABLE  width="800" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form action="showMliListPath.do?method=showMliListPath" method="POST" enctype="multipart/form-data">
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
									<TD colspan="4"> 
										<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
											<TR>
												<TD width="31%" class="Heading">MLI Wise Pending Applications</TD>
												<TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
											</TR>
											<TR>
												<TD colspan="3" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
											</TR>

										</TABLE>
									</TD>
								</TR>

								<TR align="left" valign="top">
									<TD align="left" valign="top" class="ColumnBackground">
										&nbsp;SNo
									</TD>
									<TD align="left" valign="top" class="ColumnBackground">
										&nbsp;MLI Names
									</TD>
									<TD align="left" class="ColumnBackground"> 
									&nbsp;No Of Enhanced Applications
									</TD>
									</TR>

									<%int i = 0;%>
									<tr>
									<logic:iterate id="object" name="rsForm" property="mli">
									<%
									     com.cgtsi.reports.GeneralReport list = (com.cgtsi.reports.GeneralReport)object;
									%>
									
									<TR align="left" valign="top">
									<TD align="left" valign="top" class="ColumnBackground1">
										&nbsp;<%= ++i%>				
									</TD>
									<TD align="left" valign="top" class="ColumnBackground1">
										&nbsp;<%= list.getType()%>				
										<%String bank = list.getType();%>		
                    <%
                     //Start Code by Path to replace & with Path on 9Oct06
                     bank = bank.replaceAll("&","PATH");
                     //End Code by Path to replace & with Path on 9Oct06
                    %>
									</TD>
									<TD align="left" class="TableData"> 
									<div align="left">
									&nbsp;<%int office =  list.getProposals();
									String url ="showAppsForApprovalForMLIWise.do?method=showAppsForApprovalForMLIWise&&Link="+bank;%>
									<html:link href="<%=url%>"><%= office%></html:link>
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
					<TR>
						<TD align="center" valign="baseline">
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

