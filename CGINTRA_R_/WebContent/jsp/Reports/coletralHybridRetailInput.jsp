<%@ page language="java"%>
<%@ page import="java.util.ArrayList"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic"%>
<%@ include file="/jsp/SetMenuInfo.jsp"%>
<%
	session.setAttribute("CurrentPage",
			"coletralHybridRetailReportInput.do?method=coletralHybridRetailReportInput");
%>


<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form
		action="coletralHybridRetailReport.do?method=coletralHybridRetailReport"
		method="POST" enctype="multipart/form-data">
		<TR>
			<TD width="20" align="right" valign="bottom"><IMG
				src="images/TableLeftTop.gif" width="20" height="31" alt=""></TD>
			<TD background="images/TableBackground1.gif"><IMG
				src="images/ReportsHeading.gif" width="121" height="25" alt=""></TD>
			<TD width="20" align="left" valign="bottom"><IMG
				src="images/TableRightTop.gif" width="23" height="31" alt=""></TD>
		</TR>
		<TR>
			<TD width="20" background="images/TableVerticalLeftBG.gif">&nbsp;</TD>
			<TD>
			<DIV align="right"><A
				HREF=""> HELP</A>
			<TABLE width="100%" border="0" align="left" cellpadding="0"
				cellspacing="0">
				<tr>
					<TD align="left" colspan="4"><b><font size="2">
					Fields marked as </font><font color="#FF0000" size="2">*</font><font
						size="2"> are mandatory</font></b></TD>
				</tr>
				<TR>
					<TD>
					<TABLE width="100%" border="0" cellspacing="1" cellpadding="1">
						<TR>
							<TD colspan="10">
							<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
								<TR>
									<TD width="27%" class="Heading"><bean:message
										key="danReportHeader" /></TD>
									<TD><IMG src="images/TriangleSubhead.gif" width="19"
										height="19" alt=""></TD>
								</TR>
								<TR>
									<TD colspan="3" class="Heading"><IMG
										src="images/Clear.gif" width="5" height="5" alt=""></TD>
								</TR>

							</TABLE>
							</TD>
						</TR>
							<TR>
								<TD align="left" valign="top" class="ColumnBackground">
									<DIV align="center">&nbsp;<bean:message key="fromdate" /></DIV>
								</TD>
								<TD align="left" valign="middle" class="TableData">
									<DIV align="left">
										<html:text property="dateOfTheDocument20" size="10" maxlength="10" alt="Reference" name="reportForm"/>
										<IMG src="images/CalendarIcon.gif" width="20" onClick="showCalendar('reportForm.dateOfTheDocument20')" align="middle" alt="">
										<DIV align="left"></DIV>
									</DIV>
								</TD>

								<TD align="left" valign="top" class="ColumnBackground">
									<font color="#FF0000" size="2">*</font> &nbsp;<bean:message key="toDate" />
								</TD>

								<TD align="left" valign="middle" class="TableData">
									<DIV align="left">
										<html:text property="dateOfTheDocument21" size="10" maxlength="10" alt="Reference" name="reportForm" />
										<IMG src="images/CalendarIcon.gif" width="20" onClick="showCalendar('reportForm.dateOfTheDocument21')" align="middle" alt="">
										<DIV align="left"></DIV>
									</DIV>
								</TD>
							</TR>
							<TR>
								<TD align="left" valign="top" class="ColumnBackground">
								<DIV align="center">&nbsp;Report Type
								</DIV>
								</TD>
								<TD align="left" valign="middle" class="TableData" colspan="3">
									<html:select property="reportType">
									<html:option value="" >Select</html:option>
										<html:options name="reportForm" property="reportTypeList"></html:options>	
									</html:select><!--
									<html:radio property="reportType" value="WITHSTF">With ST Component & Flag</html:radio>&nbsp;
									<html:radio property="reportType" value="WITHOUTSTF">WithOut ST Component & Flag</html:radio>
								--></TD>
							</TR>
							
					</TABLE>
					</TD>
				</TR>
				<TR>
					<TD height="20">&nbsp;</TD>
				</TR>
				<TR>
					<TD align="center" valign="baseline">
					<DIV align="center"><A
						href="javascript:submitForm('coletralHybridRetailReport.do?method=coletralHybridRetailReport')">
					<IMG src="images/OK.gif" alt="OK" width="49" height="37" border="0"></A>
					<A href="javascript:document.reportForm.reset()"> <IMG
						src="images/Reset.gif" alt="Reset" width="49" height="37"
						border="0"></A> <a
						href='subHome.do?method=getSubMenu&amp;menuIcon=<%=session.getAttribute("menuIcon")%>&amp;mainMenu=<%=session.getAttribute("mainMenu")%>'>
					<IMG src="images/Cancel.gif" alt="Cancel" width="49" height="37"
						border="0"></a></DIV>
					</TD>
				</TR>
			</TABLE>
			</DIV>
			</TD>
			<TD width="20" background="images/TableVerticalRightBG.gif">
			&nbsp;</TD>
		</TR>
		<TR>
			<TD width="20" align="right" valign="top"><IMG
				src="images/TableLeftBottom1.gif" width="20" height="15" alt="">
			</TD>
			<TD background="images/TableBackground2.gif">&nbsp;</TD>
			<TD width="20" align="left" valign="top"><IMG
				src="images/TableRightBottom1.gif" width="23" height="15" alt="">
			</TD>
		</TR>
	</html:form>
</TABLE>

