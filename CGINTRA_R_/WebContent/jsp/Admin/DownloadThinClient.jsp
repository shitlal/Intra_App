<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<% session.setAttribute("CurrentPage","downloadThinClient.do?method=downloadThinClient");%>
<%@ include file="/jsp/SetMenuInfo.jsp" %>

<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form action="downloadThinClient.do?method=downloadThinClient" method="POST" >
		<TR> 
			<TD width="20" align="right" valign="bottom"><IMG src="images/TableLeftTop.gif" width="20" height="31"></TD>
			<TD background="images/TableBackground1.gif"></TD>
			<TD width="20" align="left" valign="bottom"><IMG src="images/TableRightTop.gif" width="23" height="31"></TD>
		</TR>
		<TR>
			<TD width="20" background="images/TableVerticalLeftBG.gif">&nbsp;</TD>
			<TD>
			<!-- <DIV align="right">			
				<A HREF="javascript:submitForm('helpDeactivateMember.do?method=helpDeactivateMember')">
			    HELP</A>
			</DIV> -->
				<TABLE width="100%" border="0" align="left" cellpadding="0" cellspacing="0">
					<TR>
						<TD>
							<TABLE width="100%" border="0" cellspacing="1" cellpadding="1">
								<TR>
									<TD colspan="4"> 
										<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
											<TR>
												<TD width="31%" class="Heading"><bean:message key="downloadThinClient" /></TD>
												<TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
											</TR>
											<TR>
												<TD colspan="3" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
											</TR>

										</TABLE>
									</TD>
								</TR>

								<!-- <TR align="left">
									<TD align="left"colspan="4" valign="top" class="ColumnBackground">
										&nbsp;<bean:message key="memberId" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										<html:text property="memberId" size="20" maxlength="12" alt="Bank" name="adminForm"onkeypress="return numbersOnly(this, event)" onkeyup="isValidNumber(this)"/>
									</TD>
								</TR> -->
								<TR align="left">
									<TD align="left" colspan="4" valign="top" class="ColumnBackground">
										&nbsp;
										<% String path=request.getContextPath();%>
										<html:link href="<%=path+\"/Download/ThinClient.exe\"%>"><bean:message key="clickToDownload"/>  CGFSI Scheme 
										</html:link>		
									</TD>
								</TR>
								<TR align="left">
									<TD align="left" colspan="4" valign="top" class="ColumnBackground">
										&nbsp;										
										<html:link href="<%=path+\"/Download/ThinClientMCGF.exe\"%>"><bean:message key="clickToDownload"/>  MCGF Scheme 
										</html:link>		
									</TD>
								</TR>	

							</TABLE>
						</TD>
					</TR>
					<TR >
						<TD height="20" >
							&nbsp;
						</TD>
					</TR>
					<!-- <TR >
						<TD align="center" valign="baseline" >
							<DIV align="center">								
								<A href="javascript:submitForm('deactivateMem.do?method=deactivateMem')"><IMG src="images/OK.gif" alt="OK" width="49" height="37" border="0"></A>								
							</DIV>
						</TD>
					</TR> -->
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





						
