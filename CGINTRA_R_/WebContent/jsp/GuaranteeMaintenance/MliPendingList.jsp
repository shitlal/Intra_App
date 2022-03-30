<%@ page language="java"%>
<%@page import="com.cgtsi.actionform.GMActionForm"%>
<%@ page import="com.cgtsi.action.GMActionNew" %>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic" %>

<%@ include file="/jsp/SetMenuInfo.jsp" %>
<% session.setAttribute("CurrentPage","mliPendingList.do?method=mliPendingList");%>
<%
	String appFormName = (String)session.getAttribute("APPFORMNAME");
	//System.out.println("JSP appFormName : "+appFormName);
%>

<TABLE  width="800" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form action="afterNpaUpdationApproval.do?method=afterNpaUpdationApproval" method="POST" enctype="multipart/form-data">
		<TR> 
			<TD width="20" align="right" valign="bottom"><IMG src="images/TableLeftTop.gif" width="20" height="31"></TD>
			<TD background="images/TableBackground1.gif"><IMG src="images/GuaranteeMaintenanceHeading.gif" width="121" height="25"></TD>
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
									<TD align="left" valign="top" class="ColumnBackground">&nbsp;SNo</TD>
									<TD align="left" valign="top" class="ColumnBackground">&nbsp;MLI Names</TD>
									<TD align="left" class="ColumnBackground">&nbsp;No Of Applications</TD>
								</TR>
								<%int i = 0;%>
								<logic:iterate id="object" name="gmApproveForm" property="memberList">
									<%
										GMActionForm gm = (GMActionForm)object;
									%>
									<TR align="left" valign="top">
										<TD align="left" valign="top" class="ColumnBackground1"><%= ++i%></TD>
										<TD align="left" valign="top" class="ColumnBackground1">
											<%=gm.getBankName()%>
											<%
												String bank = gm.getBankName();
												//System.out.println("JSP bank1 : "+bank);
											%>
											
											<%
												bank = bank.replaceAll("&","PATH");
												//System.out.println("JSP bank2 : "+bank);
											%>
										</TD>
										<%
											String url = "afterNpaUpdationApproval.do?method=afterNpaUpdationApproval&&Link="+bank+"&formName="+appFormName;
											//System.out.println("JSP url : "+url);
											int noOfApp = gm.getNoOfActions();
											//System.out.println("JSP noOfApp : "+noOfApp);
										%>
										<TD align="left" class="TableData">
											<html:link href="<%=url%>"><%=noOfApp%></html:link>
										</TD>
									</TR>
								</logic:iterate>
								
							</TABLE>
						</TD>
					</TR>
					<TR><TD height="20">&nbsp;</TD></TR>
					<TR>
						<TD align="center" valign="baseline">
							<DIV align="center">
								<A href="home.do?method=getMainMenu&menuIcon=<%=session.getAttribute("menuIcon")%>&mainMenu=<%=session.getAttribute("mainMenu")%>">
									<IMG src="images/OK.gif" alt="OK" width="49" height="37" border="0">
								</A>								
							</DIV>
						</TD>
					</TR>
				</TABLE>
			</TD>
			<TD width="20" background="images/TableVerticalRightBG.gif">&nbsp;</TD>
		</TR>
		<TR>
			<TD width="20" align="right" valign="top">
				<IMG src="images/TableLeftBottom1.gif" width="20" height="15">
			</TD>
			<TD background="images/TableBackground2.gif">&nbsp;</TD>
			<TD width="20" align="left" valign="top">
				<IMG src="images/TableRightBottom1.gif" width="23" height="15">
			</TD>
		</TR>
	</html:form>
</TABLE>