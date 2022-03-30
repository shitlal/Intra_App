<%@ page language="java"%>
<%@ page import="java.util.ArrayList"%>
<%@page import="java.text.DecimalFormat"%>
<%
	DecimalFormat decimalFormat = new DecimalFormat("##########0.00");
%>

<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic"%>
<%@ include file="/jsp/SetMenuInfo.jsp"%>
<%
	session.setAttribute(
			"CurrentPage",
			"npaAccountCountFinancialYrWiseDetails.do?method=npaAccountCountFinancialYrWiseDetails");
%>

<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form
		action="npaAccountCountFinancialYrWiseDetails.do?method=npaAccountCountFinancialYrWiseDetails"
		method="POST" enctype="multipart/form-data">
		<TR>
			<TD width="20" align="right" valign="bottom"><IMG
				src="images/TableLeftTop.gif" width="20" height="31"></TD>
			<TD background="images/TableBackground1.gif"><IMG
				src="images/ReportsHeading.gif" width="121" height="25"></TD>
			<TD width="20" align="left" valign="bottom"><IMG
				src="images/TableRightTop.gif" width="23" height="31"></TD>
		</TR>
		<TR>
			<TD width="20" background="images/TableVerticalLeftBG.gif">&nbsp;</TD>
			<TD>
			<TABLE width="100%" border="0" align="left" cellpadding="0"
				cellspacing="0">
				<TR>
					<TD>
					<TABLE width="100%" border="0" cellspacing="1" cellpadding="1">
						<TR>
							<TD colspan="16">
							<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td colspan="6" class="Heading1" align="center"><u><bean:message
										key="reportHeader" /></u></td>
								</tr>
								<tr>
									<td colspan="6">&nbsp;</td>
								</tr>

								<TR>
									<TD class="Heading" width="60%">Financial Year Wise
									Guarantee Vs NPA</TD>
								</TR>

								<tr>
									<td class="Heading" width="40%">&nbsp;<bean:message
										key="from" /> <bean:write name="rsForm"
										property="dateOfTheDocument12" />&nbsp;<bean:message key="to" />
									<bean:write name="rsForm" property="dateOfTheDocument13" /></td>
									<TD><IMG src="images/TriangleSubhead.gif" height="19"></TD>

								</TR>
								<TR>
									<TD colspan="3" class="Heading"><IMG
										src="images/Clear.gif" width="5" height="5"></TD>
								</TR>

							</TABLE>
							</TD>
</TR>
							
								<%
									ArrayList arraylist = null;
										String MonthlyStringArray[] = null;
										double totGarN = 0;//a total
										double totGarAmtCount = 0.0;//b total
										double totNpaNo = 0;//x total
										double totNpaAmt = 0.0;//y total
										double totNpaNoPercent = 0.0;//sum(a/x)*100
										double totNpaAmtGuarAmtPercent = 0.0;//sum(b/y)*100
										int totGarNInt = 0;
										int totNpaInt = 0;
										arraylist = (ArrayList) session
												.getAttribute("mliWiseGuarNpaNoAmtList");
										if (arraylist.size() == 0) {
											out.println("<tr><td class=\"Heading\" colspan=\"11\"><center>No Data Found</center</td></tr>");
										}
										if (arraylist.size() > 0) {
											arraylist = (ArrayList) session
													.getAttribute("mliWiseGuarNpaNoAmtList");
								%>
								<TR class="tableData">

									<th class="ColumnBackground" colspan="1"
										style="text-align: center;">FinancialYearWise</th>
									<th class="ColumnBackground" colspan="2"
										style="text-align: center;">Guarantee</th>
									<th class="ColumnBackground" colspan="2"
										style="text-align: center;">NPA</th>
									<th class="ColumnBackground" colspan="2"
										style="text-align: center;">NPA % vs Guar %</th>
								</TR>
								<TR class="tableData">
									<th align="center" class="ColumnBackground"></th>
									<th class="ColumnBackground" style="text-align: center;">No</th>
									<th class="ColumnBackground" style="text-align: center;">Amt(Lakhs)</th>
									<th class="ColumnBackground" style="text-align: center;">No</th>
									<th class="ColumnBackground" style="text-align: center;">Amt(Lakhs)</th>
									<th class="ColumnBackground" style="text-align: center;">No</th>
									<th class="ColumnBackground" style="text-align: center;">Amt</th>
								</TR>
								<%
									for (int count = 0; count < arraylist.size(); count++) {
												MonthlyStringArray = new String[7];
												MonthlyStringArray = (String[]) arraylist.get(count);
								%>
								<tr>

									<td class="ColumnBackground1">
									<div align="left">
									<%
										String colname = MonthlyStringArray[0];
													out.println(colname);
									%>
									</div>
									</td>
									<td class="ColumnBackground1">
									<div align="right">
									<%
										String colname1 = MonthlyStringArray[1];
													totGarN = totGarN + Double.parseDouble(colname1);
													out.println(colname1);
													//System.out.println(totGarN);
													totGarNInt = new Double(totGarN).intValue();
													//System.out.println(totGarNInt);
									%>
									</div>
									</td>
									<td class="ColumnBackground1">
									<div align="right">
									<%
										String colname2 = MonthlyStringArray[2];

													totGarAmtCount = totGarAmtCount
															+ Double.parseDouble(colname2);

													out.println(colname2);
									%>
									</div>
									</td>
									<td class="ColumnBackground1">
									<div align="right">
									<%
										String colname3 = MonthlyStringArray[3];
													totNpaNo = totNpaNo + Double.parseDouble(colname3);
													totNpaInt = new Double(totNpaNo).intValue();
													out.println(new Double(colname3).intValue());
									%>
									</div>
									</td>


									<td class="ColumnBackground1">
									<div align="right">
									<%
										String colname4 = MonthlyStringArray[4];
													totNpaAmt = totNpaAmt + Double.parseDouble(colname4);

													out.println(colname4);
									%>
									</div>
									</td>
									<td class="ColumnBackground1">
									<div align="right">
									<%
										String colname5 = MonthlyStringArray[5];
													out.println(colname5);
									%>
									</div>
									</td>

									<td class="ColumnBackground1">
									<div align="right">
									<%
										String colname6 = MonthlyStringArray[6];
													out.println(colname6);
									%>
									</div>
									</td>


								</TR>

								<%
									}

										}
								%>
							</TR>


							<tr>
								<TD align="right" valign="top" colspan="1"
									class="ColumnBackground">Total</TD>

								<td align="right" class="ColumnBackground">
								<div align="right"><%=String.valueOf(totGarNInt)%></div>
								</TD>

								<TD align="right" class="ColumnBackground">
								<div align="right"><%=decimalFormat.format(totGarAmtCount)%>
								</div>
								</TD>
								<TD align="right" class="ColumnBackground">
								<div align="right"><%=String.valueOf(totNpaInt)%></div>
								</TD>
								<TD align="left" class="ColumnBackground">
								<div align="right"><%=decimalFormat.format(totNpaAmt)%></div>
								</TD>


								<TD align="right" class="ColumnBackground">
								<div align="right">
								<%
									totNpaNoPercent = (totNpaNo / totGarN) * 100;
								%> <%=decimalFormat.format(totNpaNoPercent)%></div>
								</TD>
								<TD align="left" class="ColumnBackground">
								<div align="right">
								<%
									totNpaAmtGuarAmtPercent = (totNpaAmt / totGarAmtCount) * 100;
								%> <%=decimalFormat.format(totNpaAmtGuarAmtPercent)%></div>
								</TD>

							</tr>
					</TABLE>
					</TD>
				</TR>

				<TR>
					<TD height="20">&nbsp;</TD>
				</TR>
				<tr>
					<td colspan="3" align="left" width="700"><font size="2"
						color="red">Report Generated On : <%
						java.util.Date loggedInTime = new java.util.Date();
							java.text.SimpleDateFormat dateFormat1 = new java.text.SimpleDateFormat(
									"dd MMMMM yyyy ':' HH.mm");
							String date1 = dateFormat1.format(loggedInTime);
							out.println(date1);
					%> hrs.</font></td>
				</tr>
				<TR>
					<TD align="center" valign="baseline">
					<DIV align="center"><A
						href="javascript:submitForm('npaAccountCountFinancialWiseInput.do?method=npaAccountCountFinancialWiseInput')">
					<IMG src="images/Back.gif" alt="Back" width="49" height="37"
						border="0"></A> <A href="javascript:printpage()"> <IMG
						src="images/Print.gif" alt="Print" width="49" height="37"
						border="0"></A></DIV>
					</TD>
				</TR>

			</TABLE>
			</TD>
			<TD width="20" background="images/TableVerticalRightBG.gif">
			&nbsp;</TD>
		</TR>
		<TR>
			<TD width="20" align="right" valign="top"><IMG
				src="images/TableLeftBottom1.gif" width="20" height="15"></TD>
			<TD background="images/TableBackground2.gif">&nbsp;</TD>
			<TD width="20" align="left" valign="top"><IMG
				src="images/TableRightBottom1.gif" width="23" height="15"></TD>
		</TR>

	</html:form>
</TABLE>
