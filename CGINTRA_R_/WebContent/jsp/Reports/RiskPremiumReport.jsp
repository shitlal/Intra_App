<%@ page language="java"%>
<%@ page import="java.util.ArrayList"%>
<%@page import ="java.text.SimpleDateFormat"%>

<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic" %>
<%@ include file="/jsp/SetMenuInfo.jsp" %>
<% session.setAttribute("CurrentPage","riskPremiumReport.do?method=riskPremiumReport");%>
<%SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");%>
<%@page import ="java.text.DecimalFormat"%>
<%DecimalFormat decimalFormat = new DecimalFormat("##########.##");%>

<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form action="riskPremiumReport.do?method=riskPremiumReport" method="POST" enctype="multipart/form-data">
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
									<TD colspan="10"> 
										<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
											<TR>
												<TD width="20%" class="Heading">Risk Premium Report</TD>
												<TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
											</TR>
											<TR>
												<TD colspan="3" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
											</TR>

										</TABLE>
									</TD>

									<TR>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<%-- <bean:message key="memberId" /> --%>Financial Year
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<%-- <bean:message key="instrumentNumber" /> --%>MLI Name
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<%-- <bean:message key="paidDate" /> --%>NPA Percentage
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<%-- <bean:message key="instrumentDate"/> --%>NPA Risk Premium (% of Standard Rate)
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<%-- <bean:message key="amountPaidRs"/> --%>Claim Payout Ratio
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<%-- <bean:message key="instrumentType"/> --%>Claim Payout Risk Premium (% of Standard Rate)
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<%-- <bean:message key="modeOfPayment"/> --%>Total Risk Premium (% of Standard Rate)
									</TD>
									</TR>	
			

									<tr>
									<logic:iterate name="rsForm"  property="riskPremium"  id="object">
									<%
									      com.cgtsi.reports.RiskPremium rpReport =(com.cgtsi.reports.RiskPremium)object;
									%>
											<TR>
											
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">						
											<%=rpReport.getFinalcialYear()%>
											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">						
											<%=rpReport.getMemBankName()%>
											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">						
											<%=rpReport.getNpaRatio()%>
											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">						
											<%=rpReport.getNpaRiskPremPercentage()
											%>
											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">						
											<%=rpReport.getClaimPayOutRatio()
											%>
											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">			
											<%=rpReport.getClaimPayoutRiskPremPercentage() %>
											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">						
											<%=rpReport.getTotal()									
											%>
											</TD>
											
											<%-- <TD width="10%" align="left" valign="top" class="ColumnBackground1">						
											<%String bank=pReport.getPayeeBank();
											if((bank == null)||(bank.equals("")))
											{
											%>
											<%=""%>
											<%
											}
											else
											{
											%>
											<%=bank%>
											<%
											}
											%>
											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">						
											<%=pReport.getPayableAt()%>
											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">						
											<%   java.util.Date utilDate2=pReport.getRealisationDate();
													String formatedDate2 = null;
													if(utilDate2 != null)
													{
														 formatedDate2=dateFormat.format(utilDate2);
													}
													else
													{
														 formatedDate2 = "";
													}
											%>
											<%=formatedDate2%>
											</TD>
											<!--Added By Rajuk--> 
											 <TD width="10%" align="left" valign="top" class="ColumnBackground1">		
											<div align="right">
												<%
													double inclhSBecess = pReport.getSwatchBharatTax();
												totalSBhecess=totalSBhecess+inclhSBecess;
												%>
											    <%=decimalFormat.format(inclhSBecess)%>
											   </div>
                                               </TD>
                                            <!--Ended By Rajuk--> 
                                            
                                              <TD width="10%" align="left" valign="top" class="ColumnBackground1">		
											<div align="right">
												<%
												 
												     double KrishiKalCess = pReport.getKrishiKalCess();
											    	totalkrishiKalCess=totalkrishiKalCess+KrishiKalCess;
												%>
											    <%=decimalFormat.format(KrishiKalCess)%>
											   </div>
                                               </TD>    --%> 
                                            
											</TR>
											</logic:iterate>
											
											
											

											<TR>
											<%-- <TD width="10%" align="left" valign="top" class="ColumnBackground">						
											Total
											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground">						

											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground">						

											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground">						

											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground">			
											<div align="right">
											<%=decimalFormat.format(totalAmount)%>
											</div>
											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground">						

											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground">						

											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground">						

											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground">						

											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground">						
											</TD>
											<!--Added By Rajuk-->
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
									<div align="right">
									<%=decimalFormat.format(totalSBhecess)%>
									</div>
									</TD>
                                     <!--Ended By Rajuk--> 
                                     
                                     
                                     
                                     <TD width="10%" align="left" valign="top" class="ColumnBackground">			
											<div align="right">
											<%=decimalFormat.format(totalkrishiKalCess)%>
											</div>
											</TD> --%>
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
								<A href="javascript:submitForm('riskPremiumReportInput.do?method=riskPremiumReportInput')">
									<IMG src="images/Back.gif" alt="Back" width="49" height="37" border="0"></A>

									
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

