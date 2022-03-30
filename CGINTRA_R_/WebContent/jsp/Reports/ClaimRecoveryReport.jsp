<%@ page language="java"%>
<%@ page import="java.util.ArrayList"%>
<%@page import ="java.text.DecimalFormat"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic" %>
<%@ include file="/jsp/SetMenuInfo.jsp" %>
<% session.setAttribute("CurrentPage","claimRecoveryReport.do?method=claimRecoveryReport");%>
<%DecimalFormat decimalFormat = new DecimalFormat("##########.##");%>
	
<script language="javascript" type="text/javascript"> 
		var allocatePayment=0;
		function calcAllocatePayment(amount,name){
		if(document.forms[0].elements[name].checked==true)
			{
			allocatePayment=Number(allocatePayment)+Number(amount);
			}else{
			allocatePayment=Number(allocatePayment)-Number(amount);
			}
			//alert('hi='+allocatePayment);
			document.getElementById("tAmount").innerHTML = allocatePayment;	
		}
		</script>
<TABLE width="800" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form action="claimRecoveryReport.do?method=claimRecoveryReport" method="POST" enctype="multipart/form-data">
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
							<TABLE width="70%" border="0" cellspacing="1" cellpadding="1">
								<TR>
									<TD colspan="14"> 
										<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
											<TR>
												<TD width="40%" class="Heading"><bean:message key="recoveryReportHeader" /></TD>
												<TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
											</TR>
											<TR>
												<TD colspan="14" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
											</TR>

										</TABLE>
									</TD>
	<TR>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<bean:message key="srNo" />
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<bean:message key="mliId" />
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										MLI Name
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<bean:message key="zoneName" />
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<bean:message key="unitName" />
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<bean:message key="cgpanNumber" />
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<bean:message key="guaranteedAmt" />
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										1st Installment Amount
									</TD>
										<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<bean:message key="recoveryType"/>
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										Date of Recovery Received by MLI
									</TD>
								<%-- 	<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<bean:message key="penalIntrestLiv"/>
									</TD> --%>
																
								
									<%-- <TD width="10%" align="left" valign="top" class="ColumnBackground">
										<bean:message key="ddUtrNo"/>
									</TD> --%>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										Payment Date
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
									Virtual account Number
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
									Recovery Received Amount
									</TD>
										
									<!-- <TD width="10%" align="left" valign="top" class="ColumnBackground">
										Approperate All<br> <input type="checkbox" name="selectAll" onclick="selectDeselect(this,1)" />
									</TD> -->
									</TR>
									<%
										double amount = 0;
										double totalAmount = 0;
										double amountDisbursed = 0;
										double amountDisbursedTemp = 0;
										double totalAmountDisbursed = 0;
										String cgpanTemp = "";
										double amountTemp = 0;										
										String srNo="1";			
										String mliIdd="";						
										String zoneNamed="";			
										String unitNamed="";				
										String cgpanNumberd="";					
										double guaranteedAmtd=0.0d;					
										double firstInstalAmtd=0.0d;						
										java.util.Date dateRecovRecvMlid=null;						
										double penalIntrestLivd=0.0d;						
										double recoRecvAmtd=0.0d;					
										String recoveryTyped="";					
										long ddUtrNod=0;			String name = "";			
										java.util.Date ddDatde=null;
										boolean appropFlag = false;
										String dateofclaim = null;
										String claimrefnumber = "";
										String branchName = "";
										String unitName = "";
										double guaranteeapprovedamount = 0.0;
										String viewDu = "";									
										String name1 = "";
										String thiskey = "";
										String mlicomments = "";
										String memId = "";
										String checkboxKey = null;													
										int j=0;
		                                int k=0;		                              
		                                String strTotalAmount="";
									%>
									<tr>
									<logic:iterate name="rsForm"  property="recoveryDisbursement" id="object" indexId="srlIndex" >
									<%
									      com.cgtsi.reports.RecoveryReport dReport =  (com.cgtsi.reports.RecoveryReport)object;
									      String paymentId=dReport.getPaymentId();
									%>
							
									    <tr>
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">	
											<%=srlIndex+1%><input type="hidden" name="paymentId" value="<%=paymentId%>"/>
											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">	
											<%= dReport.getMliId()%>
											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">	
											<%= dReport.getBankName()%>
											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">	
											<%= dReport.getZoneName()%>
											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">	
											<%= dReport.getUnitName()%>
											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">
											<% String cgpan=dReport.getCgpanNumber();
											//System.out.println("cgpan:"+cgpan);
											if((cgpan != null) && (!cgpan.equals("")))
											{
												//System.out.println("cgpan1:"+cgpan);
												if(!cgpan.equals(cgpanTemp))
												{        
												cgpanTemp=cgpan;
												%>
												<%=cgpan%>											
												<%
												}
												else
												{
												%>
												<%=""%>											
												<%
												}
											}
											%>
											</TD>											
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">	
											<%=dReport.getGuaranteedAmt()%>
											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">	
											<%= dReport.getRecoRecvAmt() %>
											</TD>
												<TD width="10%" align="left" valign="top" class="ColumnBackground1">	
											<%= dReport.getRecoveryType()%>
											</TD>
											<TD width="14%" align="left" valign="top" class="ColumnBackground1">	
											<%= new java.text.SimpleDateFormat("dd/MM/yyyy").format(dReport.getDateRecovRecvMli())%>
											</TD>
											<%-- <TD width="10%" align="left" valign="top" class="ColumnBackground1">	
											<%= dReport.getPenalIntrestLiv()%>
											</TD>
											 --%>
											
										
											<%-- <TD width="10%" align="left" valign="top" class="ColumnBackground1">	
											<%= dReport.getDdUtrNo()%>
											</TD> --%>
											<TD width="14%" align="left" valign="top" class="ColumnBackground1">	
											<%= new java.text.SimpleDateFormat("dd/MM/yyyy").format(dReport.getDdDate())%>
											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">	
											<%= dReport.getDdUtrNo()%>
											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">	
											<%=dReport.getRecoRecvAmt()%>
											</TD>
											
											<%-- <TD width="10%" align="left" valign="top" class="ColumnBackground1">	
											<% 					name = "approperiatePaymentYes(" + paymentId + ")";
																		Double amountt = dReport.getRecoRecvAmt();
																		int amt = (int) dReport.getRecoRecvAmt();
																		String completeStr = paymentId + "@" + amountt;
																		String completeStr1 = paymentId.concat("@").concat(Integer.toString(amt));
																		name = "approperiatePaymentYes(" + completeStr1 + ")";
																		System.out.println("completeStr approperiatePaymentYes " + completeStr);
																		String jsMethodDef = "calcAllocatePayment1(" + amount + "," + (j + 1) + ")";
															%>
										    <input type="checkbox"  name="<%=name%>" onclick="<%=jsMethodDef%>" value="<%=name%>" /> 											 
											</TD> --%>
										</TR>
                                           <%  amount = dReport.getRecoRecvAmt();
											   totalAmount = totalAmount + amount;
											   j++;
											   k++;
											%>
											</logic:iterate>
										<TR>
											<TD width="10%" colspan="14" class="ColumnBackground">	
											&nbsp;
											</TD>
										</TR>
										<TR>
											<TD width="10%" align="left" valign="top" class="ColumnBackground">	
											&nbsp;
											</TD>
											<TD width="10%" colspan="8" align="left" valign="top" class="ColumnBackground">	
											Total Recovery Amount :-
											</TD>
											<TD width="10%" colspan="4" valign="top" class="ColumnBackground">												
											<p align="right"><%=decimalFormat.format(totalAmount)%></p>
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
									<A href="javascript:submitForm('claimRecoveryReportInput.do?method=claimRecoveryReportInput')">
									<IMG src="images/Back.gif" alt="Back" width="49" height="37" border="0"></A>
                                    
                                    <!-- <a
										href="javascript:submitForm('displayallocateRecoveryPaymentApproperation.do?method=displayallocateRecoveryPaymentApproperation')"><IMG
										src="images/Submit.gif" alt="Save" width="49" height="37"
										border="0"></a> <a
										href="javascript:document.rsForm.reset();"><img
										src="images/Reset.gif" alt="Reset" width="49" height="37"
										border="0"></a> -->
									
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

