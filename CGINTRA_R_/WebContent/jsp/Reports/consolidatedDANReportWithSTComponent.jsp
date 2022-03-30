<%@ page language="java"%>
<%@ page import="java.util.ArrayList"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DecimalFormat"%>

<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic"%>
<%@ include file="/jsp/SetMenuInfo.jsp"%>
<% session.setAttribute("CurrentPage","consolidatedDanReport.do?method=consolidatedDanReport");%>
<%SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");%>
<%DecimalFormat decimalFormat = new DecimalFormat("#########0.00");%>

<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form
		action="consolidatedDanReportInput.do?method=consolidatedDanReportInput"
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
			<TABLE width="100%" border="0" align="left" cellpadding="0"
				cellspacing="0">
				<TR>
					<TD>
					<TABLE width="100%" border="0" cellspacing="1" cellpadding="1">
						<TR>
							<TD colspan="23">
							<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td colspan="6" class="Heading1" align="center"><u><bean:message
										key="reportHeader" /></u></td>
								</tr>
								<tr>
									<td colspan="6">&nbsp;</td>
								</tr>
								<TR>
									<TD width="15%" class="Heading"><bean:message
										key="danReport" /></TD>
									<td class="Heading" width="30%">&nbsp;<bean:message
										key="from" /> <bean:write name="reportForm"
										property="dateOfTheDocument20" />&nbsp;<bean:message key="to" />
									<bean:write name="reportForm" property="dateOfTheDocument21" /></td>
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
							<td width="3%" align="left" valign="top" class="ColumnBackground"><bean:message
								key="sNo" /></td>
								<TD width="10%" align="left" valign="top"
								class="ColumnBackground">ST Flag</TD>
							<TD width="10%" align="left" valign="top"
								class="ColumnBackground">Dan Type</TD>
							<TD width="10%" align="left" valign="top"
								class="ColumnBackground">Last Date</TD>

							<TD width="10%" align="left" valign="top"
								class="ColumnBackground">State Class</TD>
							<TD width="10%" align="left" valign="top"
								class="ColumnBackground">Total Dan Generated</TD>
							<TD width="10%" align="left" valign="top"
								class="ColumnBackground">Total Dan Amount</TD>
							<TD width="10%" align="left" valign="top"
								class="ColumnBackground">Not Paid Cases</TD>
							<TD width="10%" align="left" valign="top"
								class="ColumnBackground">Not Paid Amount</TD>
							<TD width="10%" align="left" valign="top"
								class="ColumnBackground">Paid Cases</TD>
							<TD width="10%" align="left" valign="top"
								class="ColumnBackground">Paid Amount</TD>
							<TD width="10%" align="left" valign="top"
								class="ColumnBackground">Total Service Tax(Rs.)</TD>
							<TD width="10%" align="left" valign="top"
								class="ColumnBackground">Not Paid Service Tax(Rs.)</TD>
							<TD width="10%" align="left" valign="top"
								class="ColumnBackground">Paid Service Tax(Rs.)</TD>

							<TD width="10%" align="left" valign="top"
								class="ColumnBackground">Total ECESS Tax(Rs.)</TD>
							<TD width="10%" align="left" valign="top"
								class="ColumnBackground">Not Paid ECESS Tax(Rs.)</TD>
							<TD width="10%" align="left" valign="top"
								class="ColumnBackground">Paid ECESS Tax(Rs.)</TD>

							<TD width="10%" align="left" valign="top"
								class="ColumnBackground">Total HECESS Tax(Rs.)</TD>
							<TD width="10%" align="left" valign="top"
								class="ColumnBackground">Not Paid HECESS Tax(Rs.)</TD>
							<TD width="10%" align="left" valign="top"
								class="ColumnBackground">Paid HECESS Tax(Rs.)</TD>
							
							<!-- added by vinod@path 27-nov-15 -->
							<TD width="10%" align="left" valign="top"
								class="ColumnBackground">Total SBCESS Tax(Rs.)(Rs.)</TD>
							<TD width="10%" align="left" valign="top"
								class="ColumnBackground">Not Paid SBCESS Tax(Rs.)</TD>
							<TD width="10%" align="left" valign="top"
								class="ColumnBackground">Paid SBCESS Tax(Rs.)</TD>
								<!-- added by kuldeep@ 27-may-16 -->
								<TD width="10%" align="left" valign="top"
								class="ColumnBackground">Total Krishi Kalyan Cess Tax(Rs.)(Rs.)</TD>
							<TD width="10%" align="left" valign="top"
								class="ColumnBackground">Not Paid Krishi Kalyan Cess Tax(Rs.)</TD>
							<TD width="10%" align="left" valign="top"
								class="ColumnBackground">Paid Krishi Kalyan Cess Tax(Rs.)</TD>
								
						</TR>
									<%		
								     	long totalcnt = 0;
										double totalamt = 0.0;
										long notpaidcnt = 0;
										double notpaidamt = 0.0;
										long paidcnt = 0;
										double paidamt = 0.0;
										double totalstamt = 0.0;
										double notpaidstamt = 0.0;
										double paidstamt = 0.0;
										double totalecamt = 0.0;
										double notpaidecamt = 0.0;
										double paidecamt = 0.0;
										double totalhecamt = 0.0;
										double notpaidhecamt = 0.0;
										double paidhecamt = 0.0;
										
										double totalsbamt = 0.0;
										double notpaidsbamt = 0.0;
										double paidsbamt = 0.0;
										
										double totalkrishikamt=0.0;
										double notpaidkrishikamt=0.0;
										double paidkrishikamt=0.0;
										//SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
									%>

						<logic:iterate id="object" name="reportForm" property="danDetails"
							indexId="index">
							<%
										com.cgtsi.reports.DanReport dReport =  (com.cgtsi.reports.DanReport)object;
									%>

							<TR>
								<td align="left" valign="top" class="ColumnBackground1"><%=Integer.parseInt(index+"")+1%></td>
								<TD width="10%" align="left" valign="top"
									class="ColumnBackground1"><%=dReport.getStFlag() %></TD>
								<TD width="10%" align="left" valign="top"
									class="ColumnBackground1"><%=dReport.getDanType() %></TD>
								<%
												//java.util.Date date = null;
											String strdate = null;
												if(dReport.getLastDate() != null){
													 strdate = dateFormat.format(dReport.getLastDate());
												}
											%>
								<td align="left" valign="top" class="ColumnBackground1"><%=strdate %></td>
								<TD width="10%" align="left" valign="top"
									class="ColumnBackground1"><%=dReport.getStclass() %></TD>
								<TD width="10%" align="right" valign="top"
									class="ColumnBackground1"><%=dReport.getTotalcnt() %></TD>
								<TD width="10%" align="right" valign="top"
									class="ColumnBackground1"><%=decimalFormat.format(dReport.getTotalamt()) %></TD>
								<TD width="10%" align="right" valign="top"
									class="ColumnBackground1"><%=dReport.getNotpaidcnt() %></TD>
								<TD width="10%" align="right" valign="top"
									class="ColumnBackground1"><%=decimalFormat.format(dReport.getNotpaidamt()) %></TD>
								<TD width="10%" align="right" valign="top"
									class="ColumnBackground1"><%=dReport.getPaidcnt() %></TD>
								<TD width="10%" align="right" valign="top"
									class="ColumnBackground1"><%=decimalFormat.format(dReport.getPaidamt()) %></TD>
								<TD width="10%" align="right" valign="top"
									class="ColumnBackground1"><%=decimalFormat.format(dReport.getTotalstamt()) %></TD>
								<TD width="10%" align="right" valign="top"
									class="ColumnBackground1"><%=decimalFormat.format(dReport.getNotpaidstamt()) %></TD>
								<TD width="10%" align="right" valign="top"
									class="ColumnBackground1"><%=decimalFormat.format(dReport.getPaidstamt()) %></TD>
								<TD width="10%" align="right" valign="top"
									class="ColumnBackground1"><%=decimalFormat.format(dReport.getTotalecamt()) %></TD>
								<TD width="10%" align="right" valign="top"
									class="ColumnBackground1"><%=decimalFormat.format(dReport.getNotpaidecamt()) %></TD>
								<TD width="10%" align="right" valign="top"
									class="ColumnBackground1"><%=decimalFormat.format(dReport.getPaidecamt()) %></TD>
								<TD width="10%" align="right" valign="top"
									class="ColumnBackground1"><%=decimalFormat.format(dReport.getTotalhecamt()) %></TD>
								<TD width="10%" align="right" valign="top"
									class="ColumnBackground1"><%=decimalFormat.format(dReport.getNotpaidhecamt()) %></TD>
								<TD width="10%" align="right" valign="top"
									class="ColumnBackground1"><%=decimalFormat.format(dReport.getPaidhecamt()) %></TD>
								
								<!-- added by vinod@path 27-nov-15 -->
								<TD width="10%" align="right" valign="top"
									class="ColumnBackground1"><%=decimalFormat.format(dReport.getTotalsbamt()) %></TD>
								<TD width="10%" align="right" valign="top"
									class="ColumnBackground1"><%=decimalFormat.format(dReport.getNotpaidsbamt()) %></TD>
								<TD width="10%" align="right" valign="top"
									class="ColumnBackground1"><%=decimalFormat.format(dReport.getPaidsbamt()) %></TD>
										<!-- added by kuldeep@ 27-may-16 -->
											<TD width="10%" align="right" valign="top"
									class="ColumnBackground1"><%=decimalFormat.format(dReport.getTotalkrishikamt()) %></TD>
								<TD width="10%" align="right" valign="top"
									class="ColumnBackground1"><%=decimalFormat.format(dReport.getNotpaidkrishikamt()) %></TD>
								<TD width="10%" align="right" valign="top"
									class="ColumnBackground1"><%=decimalFormat.format(dReport.getPaidkrishikamt()) %></TD>
										
									
							</TR>
							<%
								totalcnt = totalcnt + dReport.getTotalcnt();
								totalamt = totalamt + dReport.getTotalamt();
								notpaidcnt = notpaidcnt + dReport.getNotpaidcnt();
								notpaidamt = notpaidamt + dReport.getNotpaidamt();
								paidcnt = paidcnt + dReport.getPaidcnt();
								paidamt = paidamt + dReport.getPaidamt();
								totalstamt = totalstamt + dReport.getTotalstamt();
								notpaidstamt = notpaidstamt + dReport.getNotpaidstamt();
								paidstamt = paidstamt + dReport.getPaidstamt();
								totalecamt = totalecamt + dReport.getTotalecamt();
								notpaidecamt = notpaidecamt + dReport.getNotpaidecamt();
								paidecamt = paidecamt + dReport.getPaidecamt();
								totalhecamt = totalhecamt + dReport.getTotalhecamt();
								notpaidhecamt = notpaidhecamt + dReport.getNotpaidhecamt();
								paidhecamt = paidhecamt + dReport.getPaidhecamt();
								
								totalsbamt = totalsbamt + dReport.getTotalsbamt();
								notpaidsbamt = notpaidsbamt +  dReport.getNotpaidsbamt();
								paidsbamt = paidsbamt +  dReport.getPaidsbamt();
															
								totalkrishikamt =totalkrishikamt + dReport.getTotalkrishikamt();
								notpaidkrishikamt=notpaidkrishikamt + dReport.getNotpaidkrishikamt();
								paidkrishikamt =paidkrishikamt + dReport.getPaidkrishikamt();
								
							%>
						</logic:iterate>
						
						<TR>
							<TD width="10%" colspan="2" align="right" valign="top"
								class="ColumnBackground">Total</TD>
							<TD width="10%" align="right" valign="top"
								class="ColumnBackground"></TD>
							<TD width="10%" align="right" valign="top"
								class="ColumnBackground"></TD>
							<TD width="10%" align="right" valign="top"
								class="ColumnBackground"></TD>

							<TD width="10%" align="right" valign="top"
								class="ColumnBackground"><%=totalcnt %></TD>
							<TD width="10%" align="right" valign="top"
								class="ColumnBackground"><%=decimalFormat.format(totalamt) %></TD>
							<TD width="10%" align="right" valign="top"
								class="ColumnBackground"><%=notpaidcnt %></TD>

							<TD width="10%" align="right" valign="top"
								class="ColumnBackground">
							<div align="right"><%=decimalFormat.format(notpaidamt) %></div>
							</TD>
							<TD width="10%" align="right" valign="top"
								class="ColumnBackground">
							<div align="right"><%=paidcnt%></div>
							</TD>
							<TD width="10%" align="right" valign="top"
								class="ColumnBackground">
							<div align="right"><%=decimalFormat.format(paidamt)%></div>
							</TD>
							<TD width="10%" align="right" valign="top"
								class="ColumnBackground">
							<div align="right"><%=decimalFormat.format(totalstamt)%></div>
							</TD>
							<TD width="10%" align="right" valign="top"
								class="ColumnBackground">
							<div align="right"><%=decimalFormat.format(notpaidstamt)%></div>
							</TD>
							<TD width="10%" align="right" valign="top"
								class="ColumnBackground">
							<div align="right"><%=decimalFormat.format(paidstamt)%></div>
							</TD>
							<TD width="10%" align="right" valign="top"
								class="ColumnBackground">
							<div align="right"><%=decimalFormat.format(totalecamt)%></div>
							</TD>
							<TD width="10%" align="right" valign="top"
								class="ColumnBackground">
							<div align="right"><%=decimalFormat.format(notpaidecamt)%></div>
							</TD>
							<TD width="10%" align="right" valign="top"
								class="ColumnBackground">
							<div align="right"><%=decimalFormat.format(paidecamt)%></div>
							</TD>
							
							<TD width="10%" align="right" valign="top"
								class="ColumnBackground">
							<div align="right"><%=decimalFormat.format(totalhecamt)%></div>
							</TD>
							<TD width="10%" align="right" valign="top"
								class="ColumnBackground">
							<div align="right"><%=decimalFormat.format(notpaidhecamt)%></div>
							</TD>
							<TD width="10%" align="right" valign="top"
								class="ColumnBackground">
							<div align="right"><%=decimalFormat.format(paidhecamt)%></div>
							</TD>
							
							<!-- added by vinod@path 27-nov-15 -->
							<TD width="10%" align="right" valign="top"
								class="ColumnBackground">
							<div align="right"><%=decimalFormat.format(totalsbamt)%></div>
							</TD>
							<TD width="10%" align="right" valign="top"
								class="ColumnBackground">
							<div align="right"><%=decimalFormat.format(notpaidsbamt)%></div>
							</TD>
							<TD width="10%" align="right" valign="top"
								class="ColumnBackground">
							<div align="right"><%=decimalFormat.format(paidsbamt)%></div>
							</TD>
							<!-- added by kuldeep@27-5-16 -->
							<TD width="10%" align="right" valign="top"
								class="ColumnBackground">
							<div align="right"><%=decimalFormat.format(totalkrishikamt)%></div>
							</TD>
							<TD width="10%" align="right" valign="top"
								class="ColumnBackground">
							<div align="right"><%=decimalFormat.format(notpaidkrishikamt)%></div>
							</TD>
							<TD width="10%" align="right" valign="top"
								class="ColumnBackground">
							<div align="right"><%=decimalFormat.format(paidkrishikamt)%></div>
							</TD>
						</TR>
					</TABLE>
					</TD>
				</TR>

				<tr>
					<td>
					<table width="100%" border="0" cellspacing="1" cellpadding="0">
						<TR>
							<TD align="left" colspan="4" valign="top" class="SubHeading">
							Service Category : Other Taxable Services - Other Than The 119
							LISTED</TD>
						</TR>
						<TR>

							<td align="left" valign="top" class="ColumnBackground">
							<div align="center">&nbsp;Service Tax Number:</div>
							</td>

							<TD align="left" valign="top" class="ColumnBackground">
							<div align="left">&nbsp;AAATC2613DSD0001</div>
							</TD>

							<td align="left" valign="top" class="ColumnBackground">
							<div align="center">&nbsp;PAN Number:</div>
							</td>

							<td align="left" valign="top" class="ColumnBackground">
							<div align="left">&nbsp;AAATC2613D</div>
							</td>

						</TR>
					</table>
					</td>
				</tr>



				<TR>
					<TD height="20">&nbsp;</TD>
				</TR>
				<TR>
					<TD align="center" valign="baseline">
					<DIV align="center"><A
						href="javascript:submitForm('consolidatedDanReportInput.do?method=consolidatedDanReportInput')">
					<IMG src="images/Back.gif" alt="Print" width="49" height="37"
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
				src="images/TableLeftBottom1.gif" width="20" height="15" alt="">
			</TD>
			<TD background="images/TableBackground2.gif">&nbsp;</TD>
			<TD width="20" align="left" valign="top"><IMG
				src="images/TableRightBottom1.gif" width="23" height="15" alt="">
			</TD>
		</TR>
	</html:form>
</TABLE>

