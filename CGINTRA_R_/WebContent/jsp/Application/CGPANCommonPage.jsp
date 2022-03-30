<%@ page language="java"%>
<%@ page import="com.cgtsi.util.SessionConstants"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic" %>
<%@ page import="java.util.ArrayList"%>



<%@ include file="/jsp/SetMenuInfo.jsp" %>

<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form action="afterCGPANPage.do?method=showCgpanList" method="POST">
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
							<table width="661" border="0" cellspacing="1" cellpadding="1">
								
								<TR align="left">
									<TD  width="50%">							
										<table border="0" cellspacing="1" cellpadding="1" width="100%">		
										<tr>
											<td class="Heading"><div align="center">
													<bean:message key="listOfCgpans"/>
													</div>
											</td>
										</tr>
											<logic:iterate id="aCgpan" name="appForm" property="allCgpans"> 	
												<%
												String linkCgpan=(String)aCgpan;
												
												%>
												<tr>
													<td class="ColumnBackground" height="24">
														<div align="center">
														<a href="afterCGPANPage.do?method=showCgpanList&cgpan=<%=linkCgpan%>&flag=0"><%=linkCgpan%>
														</a>
														
														
													
														</div>	
													</td>
												</tr>											
											</logic:iterate>	
										</table>
									</TD>
										<%
										if(session.getAttribute(SessionConstants.APPLICATION_TYPE).equals("MA"))
										{
									%>
									<TD >
										<table border="0" cellspacing="1" cellpadding="1" width="100%">
											<tr>
												<td class="Heading"><div align="center">
													<bean:message key="listOfAppRefNos"/></div>
												</td>
											</tr>
												<logic:iterate id="aAppRefNo" name="appForm" property="allAppRefNos">
												<%
												String linkAppRefNo=aAppRefNo.toString();
												
												%>
													<tr>
														<td class="ColumnBackground">						
															<div align="center">
															<a href="afterCGPANPage.do?method=showCgpanList&appRef=<%=linkAppRefNo%>&flag=1"><%=linkAppRefNo%>
															</a>
															
															
													
															</div>
														</td>
													</tr>
															
												</logic:iterate>	
										</table>										
									</TD>
									<%
									}%>
								</TR>									
							</table>			
							
						</td>
					</tr>			
										
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







					