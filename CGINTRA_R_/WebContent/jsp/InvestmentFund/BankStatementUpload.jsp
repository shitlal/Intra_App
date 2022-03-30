<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<% session.setAttribute("CurrentPage","bankStatementUpload.do");%>
<%@ include file="/jsp/SetMenuInfo.jsp" %>
<HTML>
	<BODY>
		<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
			<html:errors />
			<html:form action="bankStatementUploadResult.do?method=bankStatementUploadResult" method="POST" enctype="multipart/form-data">
				<TR> 
					<TD width="20" align="right" valign="bottom"><IMG src="images/TableLeftTop.gif" width="20" height="31"></TD>
					<TD background="images/TableBackground1.gif">
					<TD width="20" align="left" valign="bottom"><IMG src="images/TableRightTop.gif" width="23" height="31"></TD>
				</TR>
				<TR>
					<TD width="20" background="images/TableVerticalLeftBG.gif">&nbsp;</TD>
					<TD>
						<TABLE width="100%" border="0" align="left" cellpadding="0" cellspacing="0">
							<TR>
								<TD>
								<DIV align="right">			
								<A HREF="bankStatementUploadHelp.do?method=bankStatementUploadHelp">
									HELP</A>
									<TABLE width="100%" border="0" cellspacing="1" cellpadding="1">
										<TR>
											<TD colspan="4"> 
												<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
													<TR>
														<TD width="31%" class="Heading"><bean:message key="FileUpload"/></TD>
														<TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
													</TR>
													<TR>
														<TD colspan="3" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
													</TR>

												</TABLE>
											</TD>
										</TR>

				  <TR> 
					<TD class="ColumnBackground">&nbsp;&nbsp;<bean:message key ="ChooseFile"/></TD>
					<TD class="TableData"> 
					<div align="left"> &nbsp;&nbsp; 
					  
					  <html:file property="bankStatementUploadFile"/>

					</div></TD>
				  </TR>

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
									<A href="javascript:submitForm('bankStatementUploadResult.do?method=bankStatementUploadResult')">
											<IMG src="images/Upload.gif" alt="OK" width="49" height="37" border="0"></A>
										<A href="javascript:document.ifForm.reset()">
											<IMG src="images/Reset.gif" alt="Cancel" width="49" height="37" border="0"></A>
											
									<A href="home.do?method=getMainMenu&menuIcon=<%=session.getAttribute("menuIcon")%>&mainMenu=<%=session.getAttribute("mainMenu")%>">
									<IMG src="images/Cancel.gif" alt="Cancel" width="49" height="37" border="0"></A>

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
	</BODY>
</HTML>		
