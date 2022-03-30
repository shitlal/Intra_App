<%@ page language="java"%>
<%@ page import="java.util.ArrayList"%>
<%@page import ="java.text.SimpleDateFormat"%>
<%@page import ="java.text.DecimalFormat"%>

<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic" %>
<%@ include file="/jsp/SetMenuInfo.jsp" %>
<% session.setAttribute("CurrentPage","excessReport.do?method=excessReport");%>
<%SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");%>
<%DecimalFormat decimalFormat = new DecimalFormat("##########.#####");%>

<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form action="excessReport.do?method=excessReport" method="POST" enctype="multipart/form-data">
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
									<TD colspan="8"> 
										<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
											<tr>
                          <td colspan="6" class="Heading1" align="center"><u><bean:message key="reportHeader"/></u></td>
                      </tr>
                      <tr> <td colspan="6">&nbsp;</td></tr>
                      <TR>
												<TD width="18%" class="Heading"><bean:message key="excessInfoReport" /></TD>
												<td class="Heading" width="40%">&nbsp;<bean:message key="from"/> <bean:write  name="rsForm" property="dateOfTheDocument28"/>&nbsp;<bean:message key="to"/> <bean:write  name="rsForm" property="dateOfTheDocument29"/></td>
                        <TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
											</TR>
											<TR>
												<TD colspan="3" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
											</TR>

										</TABLE>
									</TD>

									<TR>
                  <td width="3%" align="left" valign="top" class="ColumnBackground"><bean:message key="sNo"/></td>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<bean:message key="memberId" />
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<bean:message key="excessDate" />
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<bean:message key="excessAmount" />
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<bean:message key="isVoucher" />
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<bean:message key="paymentId"/>
									</TD>

									<%	double sum = 0;
										double totalSum = 0;
									%>
									</TR>	

									<tr>
									<logic:iterate id="object" name="rsForm" property="danRaised" indexId="index">
									<%
									     com.cgtsi.reports.ExcessInfo dReport =  (com.cgtsi.reports.ExcessInfo)object;
										 
									%>
							
											<TR>
                      <TD align="left" valign="top" class="ColumnBackground1"><%=Integer.parseInt(index+"")+1%></TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">
											<%=dReport.getMemberId()%>
											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">						
											<%   java.util.Date utilDate=dReport.getExcessDate();
													String formatedDate = null;
													if(utilDate != null)
													{
														 formatedDate=dateFormat.format(utilDate);
													 //System.out.println("formatedDate:"+formatedDate);
													}
													else
													{
														 formatedDate = "";
													}
											%>
											<%=formatedDate%>
											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">		
											<div align="right">
											   <% sum=dReport.getAmount();
											   totalSum = totalSum + Double.parseDouble(decimalFormat.format(sum));
											   //System.out.println("totalSum:"+totalSum);%>
											   <%=decimalFormat.format(sum)%>
											   </div>
											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">
											<div align="center">
											<%String voucherId=dReport.getVoucher();
											//Fix Bug 07092004 - 18
											if((voucherId == null)||(voucherId.equals("")))
											{
											%>
											<%="N"%>
											<%
											}
											else
											{
											%>
											<%="Y"%>
											<%
											}
											//Fix Completed
											%>

											 </div>
											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">						
											<%String paymentId=dReport.getPaymentId();
											String url = "excessReportDetails.do?method=excessReportDetails&danValue="+paymentId;
											if((paymentId == null) || (paymentId.equals("")))
											{%>
											<%=""%>											
											<%}
											else
											{%>
											<html:link href="<%=url%>"><%=paymentId%></html:link>
											<%}
											%>
											</TD>

											</TR>
											</logic:iterate>

									<TR>
									<TD width="10%" align="left" colspan="2" valign="top" class="ColumnBackground">
									Total
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
									
									</TD>
									<TD width="10%" align="right" valign="top" class="ColumnBackground">
									<div align="right">
									<%=decimalFormat.format(totalSum)%>
									</div>
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
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
					<TR >
						<TD align="center" valign="baseline" >
							<DIV align="center">
								<A href="javascript:submitForm('excessReportInput.do?method=excessReportInput')">
									<IMG src="images/Back.gif" alt="Print" width="49" height="37" border="0"></A>
						
									<A href="javascript:printpage()">
									<IMG src="images/Print.gif" alt="Print" width="49" height="37" border="0"></A>
								
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

