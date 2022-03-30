<%@ page language="java"%>
<%@ page import = "com.cgtsi.claim.ClaimConstants"%>
<%@ page import="com.cgtsi.actionform.ClaimActionForm"%>
<%@ page import = "com.cgtsi.claim.TermLoanCapitalLoanDetail"%>
<%@ page import = "com.cgtsi.claim.WorkingCapitalDetail"%>
<%@ page import = "com.cgtsi.claim.RecoveryDetails"%>
<%@ page import = "com.cgtsi.claim.ClaimSummaryDtls"%>
<%@ page import = "com.cgtsi.claim.ClaimApplication"%>
<%@ page import = "com.cgtsi.claim.SecurityAndPersonalGuaranteeDtls"%>
<%@ page import = "com.cgtsi.claim.DtlsAsOnDateOfSanction"%>
<%@ page import = "com.cgtsi.claim.DtlsAsOnDateOfNPA"%>
<%@ page import = "com.cgtsi.claim.DtlsAsOnLogdementOfClaim"%>
<%@ page import="java.text.SimpleDateFormat"%>

<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic"%>
<%@page import ="java.text.DecimalFormat"%>

<%@ include file="/jsp/SetMenuInfo.jsp" %>


<%@ page import="java.util.ArrayList"%>
<%SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");%>
<%DecimalFormat decimalFormat = new DecimalFormat("##########.##");%>
<% session.setAttribute("CurrentPage","applicationReportDetails1.do?method=applicationReportDetails1");%>



<% session.setAttribute("CurrentPage","proceedFromRecoveryFilterPage.do?method=proceedFromRecoveryFilterPage");
//ClaimActionForm claimForm = (ClaimActionForm)session.getAttribute("cpTcDetailsForm") ;
%>
<%
String npaClassifiedDt = null;
String npaReportingDt = null;
String reasonForTurningNPA = null;
String cgpan = null;
String hiddencgpan = null;
String dsbrsmntdt = null;
String totalDisbAmnt = null;
String principal = null;
String interestCharges = null;
String claimreferencenumber = "";
String osAsOnNpa = null;
String osAsStatedinCivilSuit = null;
String osAsOnLodgementOfClm = null;
String wccgpan = null;
String hidencgpan = null;
String wcAsOnNPA = null;
String wcOsAsOnInCivilSuit = null;
String wcOsAtLdgmntClm = null;
String landAsOnDtOfSnctnDtl = null;
String netwrthAsOnDtOfSnctn = null;
String reasonReductionDtSnctn = null;
String bldgAsOnDtOfSnctn = null;
String machinecAsOnDtOfSnctn = null;
String otherAssetsAsOnDtOfSnctn = null;
String currAssetsAsOnDtOfSnctn = null;
String otherValAsOnDtOfSnctn = null;
String landAsOnDtOfNPA = null;
String netwrthAsOnDtOfNPA = null;
String rsnRdctnDtSnctnAsOnNPA = null;
String bldgAsOnDtOfNPA = null;
String machinecAsOnDtOfNPA = null;
String otherAssetsAsOnDtOfNPA = null;
String currAssetsAsOnDtOfNPA = null;
String otherValAsOnDtOfNPA = null;
String landAsOnLodgemnt = null;
String netwrthAsOnLodgemnt = null;
String rsnRdctnDtSnctnAsOnLodgemnt = null;
String bldgAsOnDtOfLodgemnt = null;
String machinecAsOnLodgemnt = null;
String otherAssetsAsOnLodgemnt = null;
String currAssetsAsOnLodgemnt = null;
String otherValAsOnLodgemnt = null;
String cgpantodisplay = null;
String tcPrincipal1 = null;
String tcInterestCharges1 = null;
String wcAmount1 = null;
String wcOtherCharges1 = null;
String recMode = null;
String cgpn = null;
String amountClaimed = null;
java.util.HashMap hashmap = null;
java.util.Date lastDsbrsmntDt = null;
double disbAmnt = 0.0;
double principalRepayment = 0.0;
double interestAndOtherCharges = 0.0;
double outstandingAsOnDateOfNPA = 0.0;
double outstandingAsOnDateOfLodgement = 0.0;
double outstandingStatedInCivilSuit = 0.0;
String modeOfRecovery = null;
double tcPrincipal = 0.0;
double tcInterestAndOtherCharges = 0.0;
double wcAmount = 0.0;
double wcOtherCharges = 0.0;
double clmAppliedAmnt = 0.0;

String lastDsbrsmntDtStr;
SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
claimreferencenumber = request.getParameter("ClaimRefNumber");
//System.out.println("Claim Reference Number"+claimreferencenumber);
//System.out.println("First claim");

%>

<html:errors />

<% 
    java.util.ArrayList tcqry=(java.util.ArrayList)request.getAttribute("cgpanList");
	 //System.out.println("tcqry claim"+tcqry.size());
	 
for(int j=0;j<tcqry.size();)
{
    String str[]=new String[45];
    str=(String [])tcqry.get(j);
%>

 <!--<table>
<tr><td><b><font color='red'>CGPAN:</font></</b></td><td><%=str[0]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td></td></tr><tr><td><b><font color='red'>MEMBER ID:</font></b><td><%=str[1]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>APP_SUBMITTED_DT:</font></b><td><%=str[2]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>SSI_UNIT_NAME:</b><td><font color='black'><%=str[3]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>SSI_ADDRESS:</font></b><td><%=str[4]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>SSI_DISTRICT_NAME:</font></b><td><%=str[5]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>SSI_STATE_NAME:</font></b><td><%=str[6]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>SSI_CONSTITUTION:</font></b><td><%=str[7]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>app_loan_type:</font></b><td><%=str[8]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>app_bank_app_Ref_no:</font></b><td><%=str[9]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>app_expiry_dt:</font></b><td><%=str[10]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>PMR_CHF_NAME:</font></b><td><%=str[11]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>pmr_chief_it_pan</font></b><td><%=str[12]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>pmr_chief_gender:</font></b><td><%=str[13]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>pmr_name:</font></b><td><font color='red'><%=str[14]%></font></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>SSI_REGN_NUMBER:</b><td><%=str[15]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>SSI_COMMENCEMENT_DT:</font></b><td><%=str[16]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>ssi_type_of_activity:</font></b><td><%=str[17]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>SSI_INDUSTRY_NATURE:</font></b><td><%=str[18]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>SSI_NO_OF_EMPLOYEES:</font></b><td><%=str[19]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>SSI_PROJECTED_SALES_TURNOVER:</font></b><td><%=str[20]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<tr><td><b><font color='red'>SSI_PROJECTED_EXPORTS:</font></b><td><%=str[21]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>app_remarks:</font></b><td><%=str[22]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>SSI_EXISTING_OUTSTANDING_AMT:</font></b><td><%=str[23]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>app_status:</font></b><td><%=str[24]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>ssi_industry_sector:</font></b><td><%=str[25]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>APP_DC_HANDICRAFT_ACC_FLAG:</font></b><td><%=str[26]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>APP_DC_HANDICRAFT_REIMB:</font></b><td><%=str[27]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>APP_DC_ICARD_NO:</font></b><td><%=str[28]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>APP_DC_ICARD_ISSUE_DATE:</font></b><td><%=str[29]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>bid :</font></b><td><%=str[30]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>app_project_outlay:</font></b><td><%=str[31]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>TRM_AMOUNT_SANCTIONED:</font></b><td><%=str[32]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>TRM_AMOUNT_SANCTIONED_DT:</font></b><td><%=str[33]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>TRM_INTEREST_RATE:</font></b><td><%=str[34]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>TRM_PLR:</font></b><td><%=str[35]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>TRM_PROMOTER_CONTRIBUTION:</font></b><td><%=str[36]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>TRM_SUBSIDY_EQUITY_SUPPORT:</font></b><td><%=str[37]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>TRM_OTHERS:</font></b><td><%=str[38]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>TRM_REPAYMENT_MORATORIUM:</font></b><td><%=str[39]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>TRM_REPAYMENT_PERIODICITY :</font></b><td><%=str[40]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>TRM_FIRST_INSTALLMENT_DUE_DT:</font></b><td><%=str[41]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>TRM_NO_OF_INSTALLMENTS:</font></b><td><%=str[42]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>dbr_amount:</font></b><td><%=str[43]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<tr><td><b><font color='red'>WCP_FB_LIMIT_SANCTIONED:</font></b><td><%=str[44]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>WCP_FB_LIMIT_SANCTIONED_DT:</font></b><td><%=str[45]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>WCP_NFB_LIMIT_SANCTIONED:</font></b><td><%=str[46]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<td><b><font color='red'>WCP_NFB_LIMIT_SANCTIONED_DT:</font></b><td><%=str[47]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<td><b><font color='red'>FBOSAMT:</font></b><td><%=str[48]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<td><b><font color='red'>NFBOSAMT:</font></b><td><%=str[49]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<td><b><font color='red'>WCP_INTEREST:</font></b><td><%=str[50]%></td></tr><tr><td><b><font color='red'>WCP_PLR:</font></b><td><%=str[51]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>WCP_PROMOTERS_CONTRIBUTION:</font></b><td><%=str[52]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>WCP_SUBSIDY_EQUITY_SUPPORT:</font></b><td><%=str[53]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>WCP_OTHERS:</font></b><td><%=str[54]%></td></tr>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tr><td><b><font color='red'>WCP_TENURE:</font></b><td><%=str[55]%></td></tr>


 </table>-->



	
	 
  
   
<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form action="applicationReportDetails.do?method=applicationReportDetails" method="POST" enctype="multipart/form-data">
		<TR> 
			<TD width="20" align="right" valign="bottom"><IMG src="images/TableLeftTop.gif" alt="" width="20" height="31"></TD>
			<TD background="images/TableBackground1.gif"><IMG src="images/ReportsHeading.gif" alt="" width="121" height="25"></TD>
			<TD width="20" align="left" valign="bottom"><IMG src="images/TableRightTop.gif" alt="" width="23" height="31"></TD>
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
											<TR>
												<TD width="20%" class="Heading"><bean:message key="applicationHeader" /></TD>
												<TD><IMG src="images/TriangleSubhead.gif" alt="" width="19" height="19"></TD>
											</TR>
											<TR>
											<TD colspan="3" class="Heading"><IMG src="images/Clear.gif" alt="" width="5" height="5"></TD>
										</TR>

									</TABLE>
								</TD>

				<tr> 
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="cgpanForApplication" /></b><%=str[0]%></div></td>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="memberIdForApplication" /></b><%=str[1]%></div></td>
				</tr>
				<tr>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="asDate" /></b> <bean:define id="applicationDate" name="rsForm" property="applicationReport" type="com.cgtsi.reports.ApplicationReport"/>
				  <%String date1 = null;
				  java.util.Date newApplicationDate = applicationDate.getApplicationDate();
				  if(newApplicationDate != null)
				  {
	  				   date1 = dateFormat.format(newApplicationDate);
				  }
				  else
				  {
					   date1 = ""; 
				  }
				  %>
				  <%=str[2]%></div></td>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="nameOfSsi" /></b><%=str[3]%></div></td>
                </tr>
				<tr>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="addresses" /></b><%=str[4]%>
				  <br>&nbsp;
				  </br></div></td>
                  <td class="tableData"> <div align="left">&nbsp; <b><%=str[5]%></b></div></td>
                </tr>
				<tr>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="state" /></b><%=str[6]%></div></td>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="unitType" /></b>	<bean:define id="unitType" name="rsForm" property="applicationReport" type="com.cgtsi.reports.ApplicationReport"/>
				  <%String type = unitType.getUnitType();
				  if(type != null)
				  {
	  				   type = type.toUpperCase();
					   //System.out.println("type:"+type);
				  }
				  else
				  {
					   type = ""; 
					   //System.out.println("type:"+type);
				  }
				  %>
				  <%=str[7]%></div></td>
                </tr>
                <tr>
                   <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="CreditFacilitySanctioned" /></b> <%=str[8]%></div></td>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="bankReference" /></b><%=str[9]%></div></td>
                </tr>
                <tr>
           <td class="tableData"> <div align="left">&nbsp; <b>Application Expiry Date :</b>
				  <bean:define id="expiryDate" name="rsForm" property="applicationReport" type="com.cgtsi.reports.ApplicationReport"/>
				  <%String date10 = null;
				  java.util.Date newexpiredOn = expiryDate.getExpiryDate();
                                    //System.out.println(newexpiredOn);
				  if(newexpiredOn != null)
				  {
	  				   date10 = dateFormat.format(newexpiredOn);
				  }
				  else
				  {
					   date10 = ""; 
				  }
				  %>
				  <%=str[10]%>
       
				  </div></td>
                </tr>
				<tr>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="ChiefPromoter" /></b><%=str[11]%></div></td>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="itpanNo" /></b><%=str[12]%></div></td>
                </tr>
				<tr>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="Gender" /></b><%=str[13]%></div></td>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="OtherPromoters" /></b><%=str[14]%>
				 
	  			   
				 
				</div></td>
               </tr>
				<tr>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="SSIRegnNo" /></b> <%=str[15]%></div></td>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="DateofInc" /></b>
				  <bean:define id="startDate" name="rsForm" property="applicationReport" type="com.cgtsi.reports.ApplicationReport"/>
				  <%String date2 = null;
				  java.util.Date newStartDate = startDate.getStartDate();
				  if(newStartDate != null)
				  {
	  				   date2 = dateFormat.format(newStartDate);
				  }
				  else
				  {
					 date2 = "";
				  }
				  %>
				  <%=str[16]%></div></td>
				</tr>
        <tr>
        <td class="tableData"><div align="left"><b>&nbsp;<bean:message key="activitytype" /></b>:&nbsp;&nbsp;<%=str[17]%></div></td>
        <td class="tableData">&nbsp;</td>
        </tr>
        
				<tr>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="IndustryType" /></b><%=str[18]%></div></td>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="employees" /></b><%=str[19]%></div></td>
                </tr>
				<tr>
                  <td class="tableData"> <div align="left">&nbsp; <b>
				  <bean:message key="Turnover" /></b>
				  <bean:define id="turnover" property="applicationReport" name="rsForm" type="com.cgtsi.reports.ApplicationReport"/>
				  <%double turnoverValue = turnover.getTurnover();%>
				  <%=str[20]%>
				 </div></td>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="Exports" /></b><%=str[21]%></div></td>
                </tr>
				<tr>
				 <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="Remarks" /></b>	 
				  <bean:define id="remarks" name="rsForm" property="applicationReport" type="com.cgtsi.reports.ApplicationReport"/>
				  <% String remarksFinal = remarks.getRemarks();
				  if((remarksFinal != null) && (!remarksFinal.equals("")))
				  {
                                    //System.out.println("88888888888888888888888888888888888888888888");
				  %>
	  			   <%=str[22]%>
				   <%
				  }
				  else
				  {
				  %>
	  			   <!--<%=""%>-->
				   <%=str[22]%>
				   <%
				  }
				  %>
				</div></td>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="Outstanding" /></b> <%=str[23]%></div></td>
                </tr>
				<tr>
				<td class="tableData"> <div align="left">&nbsp; <b><bean:message key="Status" /></b> <%=str[24]%></div></td>
				<td class="tableData"> <div align="left">&nbsp; <b><bean:message key="industrySector" /></b> <%=str[25]%></div></td>
				</tr>
                                 <tr>
				<td class="tableData"> <div align="left">&nbsp; <b>HandiCrafts : </b> <%=str[26]%></div></td>
				<td class="tableData"> <div align="left">&nbsp; <b>DC Handicrafts : </b> <%=str[27]%></div></td>
				</tr>
                                 <tr>
				<td class="tableData"> <div align="left">&nbsp; <b> I Card Number : </b> <%=str[28]%></div></td>
				<td class="tableData"> <div align="left">&nbsp; <b> I Card Issue date : </b>
                                 <bean:define id="appDcIcardIssueDate" name="rsForm" property="applicationReport" type="com.cgtsi.reports.ApplicationReport"/>
				  <%String IcardIssueDate = null;
				  java.util.Date newIcardIssueDate = startDate.getAppDcIcardIssueDate();
				  if(newIcardIssueDate != null)
				  {
	  				   IcardIssueDate = dateFormat.format(newIcardIssueDate);
				  }
				  else
				  {
					 IcardIssueDate = "";
				  }
				  %>
				   <%=str[29]%></div>
                                
                                
                                
                               </td>
				</tr>
				<tr>
				<td class="tableData"> <div align="left">&nbsp; <b><bean:message key="borrowerId" /></b> <%=str[30]%></div></td>
				<td class="tableData"> <div align="left">&nbsp; <b><bean:message key="projectOutlayRs" /></b>
				  <bean:define id="projectOutlay" property="applicationReport" name="rsForm" type="com.cgtsi.reports.ApplicationReport"/>
				  <%double projectCost = projectOutlay.getProjectOutlay();%>
				  <%=str[31]%>
				  </div></td>
				</tr>
				<tr>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="TCSanctionedAmount" /></b>
				  <bean:define id="tcSanctioned" property="applicationReport" name="rsForm" type="com.cgtsi.reports.ApplicationReport"/>
				  <%double tcAmount = tcSanctioned.getTcSanctioned();%>
				  <%=str[32]%>
				  </div></td>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="TCSanctionedDate" /></b>
				  <bean:define id="tcSanctionedOn" name="rsForm" property="applicationReport" type="com.cgtsi.reports.ApplicationReport"/>
				  <%String date3 = null;
				  java.util.Date newTcSanctionedOn = tcSanctionedOn.getTcSanctionedOn();
				  if(newTcSanctionedOn != null)
				  {
	  				   date3 = dateFormat.format(newTcSanctionedOn);
				  }
				  else
				  {
					   date3 = ""; 
				  }
				  %>
				  <%=str[33]%>
				 </div></td></tr>
				<tr>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="TCPlr" /></b><bean:define id="tcPlr" name="rsForm" property="applicationReport" type="com.cgtsi.reports.ApplicationReport"/>
				  <%double newTcPlr =  tcPlr.getTcPlr();
				  String newTcPlr1 = newTcPlr+"% p.a.";
				  %>
				  <%=str[34]%></div></td>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="TCRate" /></b><bean:define id="tcRate" name="rsForm" property="applicationReport" type="com.cgtsi.reports.ApplicationReport"/>
				  <%double newTcRate =  tcRate.getTcRate();
				  String newTcRate1 = newTcRate+"% p.a.";
				  %>
				  <%=str[35]%></div></td>
                </tr>
				<tr>
                <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="TCPromoterContribution" /></b><%=str[36]%></div></td>
				<td class="tableData"> <div align="left">&nbsp; <b><bean:message key="TCSubsidy" /></b><%=str[37]%></div></td>
				</TR>
				<tr>
                <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="TCEquity" /></b><%=str[38]%></div></td>
				    <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="TCOutstanding" /></b><%=str[39]%></div></td>
				</TR>
					<tr>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="moratoriumPeriod" /></b><%=str[40]%></div></td>
               	  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="Periodicity" /></b><%=str[41]%></div></td>
                  </tr>
				<tr>
				<td class="tableData"> <div align="left">&nbsp; <b><bean:message key="FirstInstallmentDate"/></b>
				  <bean:define id="firstInstallmentDueDate" name="rsForm" property="applicationReport" type="com.cgtsi.reports.ApplicationReport"/>
				  <%String date4 = null;
				  java.util.Date newDueDate = firstInstallmentDueDate.getFirstInstallmentDueDate();
				  if(newDueDate != null)
				  {
	  				   date4 = dateFormat.format(newDueDate);
				  }
				  else
				  {
					   date4 = ""; 
				  }
				  %>
				  <%=str[42]%>
				</div></td>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="NoOfInstallments" /></b><%=str[43]%></div></td>
						  <tr>

						  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="DisbursementAmount" /></b> <%=str[44]%></div></td>
						  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="disbursementDate" /></b>
						  <bean:define id="disbursementDate" name="rsForm" property="applicationReport" type="com.cgtsi.reports.ApplicationReport"/>
						  <%String date5 = null;
						  java.util.Date newDisbursementDate = disbursementDate.getDisbursementDate();
						  if(newDisbursementDate != null)
						  {
							   date5 = dateFormat.format(newDisbursementDate);
						  }
						  else
						  {
							   date5 = ""; 
						  }
						  %>
						  <%=str[45]%>
						  </div></td>
						</tr>
						<tr>

						  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="FinalDisbursement" /></b><%=str[46]%></div></td>
						  <td class="tableData"><div align="left">&nbsp;<b><bean:message key="tcTenure" /></b>:&nbsp;<%=str[47]%></div></td>
				  
						</tr> 
						<tr>
						  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="WCFBLimitSanctioned" /></b>
						  <bean:define id="wcFbSanctioned" property="applicationReport" name="rsForm" type="com.cgtsi.reports.ApplicationReport"/>
						  <%double wcFbAmount = wcFbSanctioned.getWcFbSanctioned();%>
						 <%=str[48]%>
						 </div></td>
						  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="WCFBLimitSanctionedOn" /></b>
						  <bean:define id="wcFbSanctionedOn" name="rsForm" property="applicationReport" type="com.cgtsi.reports.ApplicationReport"/>
						  <%String date6 = null;
						  java.util.Date newWcFbSanctionedOn = wcFbSanctionedOn.getWcFbSanctionedOn();
						  if(newWcFbSanctionedOn != null)
						  {
							   date6 = dateFormat.format(newWcFbSanctionedOn);
						  }
						  else
						  {
							   date6 = ""; 
						  }
						  %>
						  <%=str[49]%>
                                                  </div></td>
						</tr>

						<tr>
						  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="WCNFBLimitSanctioned" /></b><%=str[50]%></div></td>
						  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="WCNFBLimitSanctionedOn" /></b>
						  <bean:define id="wcNfbSanctionedOn" name="rsForm" property="applicationReport" type="com.cgtsi.reports.ApplicationReport"/>
						  <%String date7 = null;
						  java.util.Date newWcNfbSanctionedOn = wcNfbSanctionedOn.getWcNfbSanctionedOn();
						  if(newWcNfbSanctionedOn != null)
						  {
							   date7 = dateFormat.format(newWcNfbSanctionedOn);
						  }
						  else
						  {
							   date7 = ""; 
						  }
						  %>
						 <%=str[51]%>
                                                 </div></td>
						</tr>
							<tr>
						  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="WCFBPrincipalOutstanding" /></b><%=str[52]%></div></td>
							<td class="tableData"> <div align="left">&nbsp; <b><bean:message key="WCNFBPrincipalOutstanding" /></b><%=str[53]%></div></td>
						</tr>
						<tr>
						 <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="WCPlr" /></b><bean:define id="wcPlr" name="rsForm" property="applicationReport" type="com.cgtsi.reports.ApplicationReport"/>
						  <%double newWcPlr =  wcPlr.getWcPlr();
						  String newWcPlr1 = newWcPlr+"% p.a.";
						  %>
						  <%=str[54]%></div></td>
						 <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="WCInterest" /></b><bean:define id="wcInterest" name="rsForm" property="applicationReport" type="com.cgtsi.reports.ApplicationReport"/>
						  <%double newWcInterest =  wcInterest.getWcInterest();
						  String newWcInterest1 = newWcInterest+"% p.a.";
						  %>
						  <%=str[55]%></div></td>
						</tr>
						<tr>
						  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="WCPromoterContribution" /></b><%=str[56]%></div></td>
							<td class="tableData"> <div align="left">&nbsp; <b><bean:message key="WCSubsidy" /></b><%=str[57]%></div></td>
						</tr>
						<tr>
						  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="WCEquity" /></b><%=str[58]%></div></td>
						  <td class="tableData"> <div align="left">&nbsp;<b><bean:message key="workingCapitalTenure" /></b>:&nbsp;&nbsp;<%=str[59]%></div></td>
						</tr>

   				
					</TABLE>

						</TD>
					</TR>
					<TR >
						<TD height="20" >
							&nbsp;
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
				<IMG src="images/TableLeftBottom1.gif" alt="" width="20" height="15">
			</TD>
			<TD background="images/TableBackground2.gif">
				&nbsp;
			</TD>
			<TD width="20" align="left" valign="top">
				<IMG src="images/TableRightBottom1.gif" alt="" width="23" height="15">
			</TD>
		</TR>
	</html:form>
</TABLE>

	 
	 <%  j++ ;} %>
	   
        
      <BR>
		

	<TABLE width="500" border="2" cellpadding="2" cellspacing="2">
		<html:errors />
		<html:form action="newMonthlyProgressReport.do?method=newMonthlyProgressReport" method="POST" enctype="multipart/form-data">
		
		
			<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
											<TR>
												<TD width="20%" class="Heading">Payment History</TD>
												<TD><IMG src="images/TriangleSubhead.gif" alt="" width="19" height="19"></TD>
											</TR>
											<TR>
											<TD colspan="3" class="Heading"><IMG src="images/Clear.gif" alt="" width="5" height="5"></TD>
										</TR>

			</TABLE>
			
									
                          <% 
							ArrayList arraylist = null;
							String MonthlyStringArray[]=null;
							
							arraylist=(ArrayList)request.getAttribute("payDetailList");
							%> 
	
			
			<table width="100%" border="0" cellpadding="0" cellspacing="1">
                  <TR align="left" valign="top">
						<TD width = "10%" align="left" class="ColumnBackground"> 
									CGPAN&nbsp;
									</TD>
                
						<TD width = "13%" align="left" class="ColumnBackground"> 
									DAN&nbsp;&nbsp;
									</TD>
						<TD width = "8%" align="left" class="ColumnBackground"> 
								  Approved Amt
									</TD>
						
						<TD width = "8%" align="left" class="ColumnBackground"> 
								  Dan Amount
									</TD>
						<TD width = "15%" align="left" class="ColumnBackground"> 
								<bean:message key="payId"/>
									</TD>
						<TD width = "8%" align="left" class="ColumnBackground"> 
								Realised Date
									</TD>
						<TD width = "8%" align="left" class="ColumnBackground"> 
								Appr Date
									</TD>
						<TD width = "8%" align="left" class="ColumnBackground"> 
								Expiry Date
									</TD>
						<TD width = "8%" align="left" class="ColumnBackground"> 
									<bean:message key="appropriation"/>
									</TD>
						<TD width = "8%" align="left" class="ColumnBackground"> 
									DD No.
									</TD>
						<TD width = "15%" align="left" class="ColumnBackground"> 
									<bean:message key="Closure"/>
									</TD>
									
						<TD width = "8%" align="left" class="ColumnBackground"> 
									STATUS
									</TD>
              		</TR>
				
                <%
							
			 for(int count=0;count<arraylist.size();)
			{
			  MonthlyStringArray=new String[13];
			 
			  MonthlyStringArray=(String[])arraylist.get(count);
			  
			  %>
	  
				
					<TR align="left" valign="top">
						<TD align="left" class="TableData"><%=MonthlyStringArray[0]%></td>
						<TD align="left" class="TableData"><%=MonthlyStringArray[1]%></td>
						<TD align="left" class="TableData"><%=MonthlyStringArray[2]%></td>
						<TD align="left" class="TableData"><%=MonthlyStringArray[3]%></td>
						<TD align="left" class="TableData"><%=MonthlyStringArray[4]%></td>
						<TD align="left" class="TableData"><%=MonthlyStringArray[5]%></td>
					    <TD align="left" class="TableData"><%=MonthlyStringArray[6]%></td>
						<TD align="left" class="TableData"><%=MonthlyStringArray[7]%></td>
						<TD align="left" class="TableData"><%=MonthlyStringArray[8]%></td>
					    <TD align="left" class="TableData"><%=MonthlyStringArray[9]%></td>
						<TD align="left" class="TableData"><%=MonthlyStringArray[10]%></td>
						<TD align="left" class="TableData"><%=MonthlyStringArray[11]%></td>
					</TR>
			
				<%
				count++;
				
				}
				%>  
			</TABLE>				
		</html:form>
	</TABLE>






<html:form action="addFirstClaimsPageDetails.do?method=addFirstClaimsPageDetails" method="POST" enctype="multipart/form-data">
  <table width="100%" border="0" cellspacing="0" cellpadding="0">    


      <tr>
        <td class="FontStyle">&nbsp;</td>
      </tr>
    </table>
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr> 
      <td width="20" align="right" valign="bottom"><img src="images/TableLeftTop.gif" alt="" width="20" height="31"></td>
        <td width="248" background="images/TableBackground1.gif"><img src="images/ClaimsProcessingHeading.gif" alt="" width="131" height="25"></td>
      <td align="right" valign="top" background="images/TableBackground1.gif"> 
        <div align="right"></div></td>
      <td width="23" align="left" valign="bottom"><img src="images/TableRightTop.gif" alt="" width="23" height="31"></td>
    </tr>
    <tr> 
      <td width="20" background="images/TableVerticalLeftBG.gif">&nbsp;</td>
      <td colspan="2"><table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td>
  		<table width="100%" border="0" cellspacing="1" cellpadding="0">
                  
                  <tr> 
                    <td colspan="8"><table width="100%" border="0" cellspacing="0" cellpadding="0">
                        <tr> 
                          <td width="35%" height="20" class="Heading">&nbsp;<bean:message key="claimtitle"/> details for <%=claimreferencenumber%></td>
                          <td align="left" valign="bottom"><img src="images/TriangleSubhead.gif" alt="" width="19" height="19"></td>
                        </tr>
                        <tr> 
                          <td colspan="4" class="Heading"><img src="images/Clear.gif" alt="" width="5" height="5"></td>
                        </tr>
                      </table></td>
                  </tr>
                  <tr> 
                    <td class="SubHeading" colspan="4">&nbsp;<bean:message key="dtlsOfOperatingOfficeAndLendingBranch"/></td>
                  </tr>
                  <tr> 
                    <td class="ColumnBackground"> <div align="left">&nbsp; <bean:message key="memberId"/></div></td> 
  
                    <td class="TableData" colspan="3"> <div align="left"> &nbsp;<bean:write property="claimapplication.memberDetails.memberId" name="cpTcDetailsForm"/></div></td>
                  </tr>
                  <tr> 
                    <td class="ColumnBackground"> <div align="left">&nbsp; <bean:message key="lendingbankname"/></div></td>
                    <td class="TableData"> <div align="left"> &nbsp;<bean:write property="claimapplication.memberDetails.memberBankName" name="cpTcDetailsForm"/></div></td>
                    <td class="ColumnBackground"> <div align="left">&nbsp;<bean:message key="city"/></div></td>
                    <td class="TableData"> <div align="left"> &nbsp;<bean:write property="claimapplication.memberDetails.city" name="cpTcDetailsForm"/></div></td>
                  </tr>
                  <tr> 
                    <td class="ColumnBackground"> <div align="left">&nbsp;<bean:message key="district"/></div></td>
                    <td class="TableData"> <div align="left"> &nbsp;<bean:write property="claimapplication.memberDetails.district" name="cpTcDetailsForm"/></div></td>
                    <td class="ColumnBackground"> <div align="left">&nbsp;<bean:message key="state"/></div></td>
                    <td class="TableData"> <div align="left"> &nbsp;<bean:write property="claimapplication.memberDetails.state" name="cpTcDetailsForm"/></div></td>
                  </tr>
                  <tr> 
                    <td class="ColumnBackground"> <div align="left">&nbsp;<bean:message key="telephone"/></div></td>
                    <td class="TableData" colspan="3"> <div align="left"> &nbsp;<bean:write property="claimapplication.memberDetails.telephone" name="cpTcDetailsForm"/></div></td>
                  </tr>
                  <tr> 
                    <td class="ColumnBackground"> <div align="left">&nbsp;<bean:message key="eMail"/></div></td>
                    <td class="TableData" colspan="3"> <div align="left"> &nbsp; <bean:write property="claimapplication.memberDetails.email" name="cpTcDetailsForm"/></div></td>
                  </tr>
  				<tr> 
                    <td class="SubHeading" colspan="4"> &nbsp;<bean:message key="borrower/unitdetails"/></td>
                  </tr>
                  <tr> 
                    <td class="ColumnBackground"> <div align="left">&nbsp;<bean:message key="name"/></div></td>
                    <td class="TableData" colspan="3"> <div align="left"> &nbsp;<bean:write property="claimapplication.borrowerDetails.borrowerName" name="cpTcDetailsForm"/></div></td>
                  </tr>
                  <tr> 
                    <td class="ColumnBackground"> <div align="left">&nbsp;<bean:message key="completeaddress"/></div></td>
                    <td class="TableData" colspan="3"> <div align="left"> &nbsp;<bean:write property="claimapplication.borrowerDetails.address" name="cpTcDetailsForm"/></div></td>
                  </tr>
                  <tr> 
                    <td class="ColumnBackground"> <div align="left">&nbsp;<bean:message key="district"/></div></td>
                    <td class="TableData"> <div align="left"> &nbsp;<bean:write property="claimapplication.borrowerDetails.district" name="cpTcDetailsForm"/></div></td>
                    <td class="ColumnBackground"> <div align="left">&nbsp;<bean:message key="state"/></div></td>
                    <td class="TableData"> <div align="left"> &nbsp;<bean:write property="claimapplication.borrowerDetails.state" name="cpTcDetailsForm"/></div></td>
                    <tr> 
                    <td class="ColumnBackground"> <div align="left">&nbsp;<bean:message key="pin"/></div></td>
                    <td class="TableData" colspan="3"> <div align="left"> &nbsp;<bean:write property="claimapplication.borrowerDetails.pinCode" name="cpTcDetailsForm"/></div></td>
                  </tr>
                  <tr>
					      	<td class="ColumnBackground"> <div align="left">&nbsp;Whether the Unit Assisted is an MICRO as per the MSMED Act 2006 definition of MSE:</div></td>
						    <td class="TableData" colspan="3"> <div align="left"> &nbsp;<bean:write property="claimapplication.microCategory" name="cpTcDetailsForm"/></div></td>
                 </tr>
<tr>
					      	<td class="ColumnBackground"> <div align="left">TYPE OF ACTIVITY</div></td>
						    <td class="TableData" colspan="3"> <div align="left"> &nbsp;<bean:write property="claimapplication.borrowerDetails.activity" name="cpTcDetailsForm"/></div></td>
                 </tr>

                  </tr>                
  				<tr> 
                    <td class="SubHeading" colspan="4"> &nbsp;<bean:message key="statusofaccounts"/></td>
                  </tr>                  
                  <tr> 
                  
                    <td class="ColumnBackground"> <div align="left">&nbsp;<bean:message key="dateofnpa"/></div></td>                    
                    <%                    
                    npaClassifiedDt = "claimapplication.npaDetails.".trim() +com.cgtsi.claim.ClaimConstants.NPA_CLASSIFIED_DT;
                    %>
                    <td class="TableData" colspan="3"> <div align="left"> &nbsp;<bean:write property="claimapplication.dateOnWhichAccountClassifiedNPAStr" name="cpTcDetailsForm"/> </div></td>
                  </tr>
                  <%
                  npaReportingDt = "npaDetails.".trim() + com.cgtsi.claim.ClaimConstants.NPA_REPORTING_DT;
                  %>
                  <tr> 
                    <td class="ColumnBackground"> <div align="left">&nbsp;<bean:message key="dateofreporting"/></div></td>
                    <td class="TableData" colspan="3"> <div align="left"> &nbsp; <bean:write property="claimapplication.dateOfReportingNpaToCgtsiStr" name="cpTcDetailsForm"/></div></td>
                  </tr>
                  <%
                  reasonForTurningNPA = "npaDetails.".trim() + com.cgtsi.claim.ClaimConstants.REASONS_FOR_TURNING_NPA;
                  %>
                  <tr> 
                    <td class="ColumnBackground"> <div align="left">&nbsp;<bean:message key="reasonfornpa"/></div></td>
                    <td class="TableData" colspan="3"> <div align="left"> &nbsp; <bean:write property="claimapplication.reasonsForAccountTurningNPA" name="cpTcDetailsForm"/></div></td>
                  </tr>
                  
                  
                  <tr> 
		                      <td class="ColumnBackground"> <div align="left">&nbsp;<bean:message key="dateofissueofrecallnotice"/></div></td>
		                      <td class="TableData" colspan="3">
		    		  	<bean:write property="claimapplication.dateOfIssueOfRecallNoticeStr" name="cpTcDetailsForm"/>
		    		  	<%
		    		  	if(request.getAttribute("recallNoticeAttachment")!=null)
		    		  	{
		    		  	%>
		    		  	<a href="<%=request.getAttribute("recallNoticeAttachment")%>"> 
		    		  	View Attachment 
		    		  	</a>
		    		  	<%
		    		  	}
		    		  	%>
		    		  </td>
		    		  
                  </tr>
                  
                  
  				
						<tr>
				            <td colspan="4" class="SubHeading">&nbsp;<bean:message key="detailsoflegalproceedings"/></td>
				        </tr>
				        <tr>
				            <td colspan="4" class="SubHeading">
								<table width="100%" border="0" cellspacing="1" cellpadding="0">
				  					<tr>
				  						<td class="ColumnBackground" colspan="1">						
				  						&nbsp;<bean:message key="forumthruwhichlegalproceedingsinitiated"/></td>						
				  						<td class="TableData">
				  						<bean:write property="claimapplication.legalProceedingsDetails.forumRecoveryProceedingsInitiated" name="cpTcDetailsForm"/>				  								
				  						</td>						
				  						<td class="ColumnBackground" colspan="1">&nbsp;<bean:message key="otherforum"/></td>
				  						<td class="TableData"></td>
				  					</tr>
				  					<tr>
				                          <td class="ColumnBackground" colspan="1">&nbsp;<bean:message key="suit/caseregnumber"/></td>
				  						<td class="TableData">						
				  						<bean:write property="claimapplication.legalProceedingsDetails.suitCaseRegNumber" name="cpTcDetailsForm"/></td>
				  						
				  						<td class="ColumnBackground" colspan="1">&nbsp;<bean:message key="filingdate"/></td>
				  						<td class="TableData"><bean:write property="claimapplication.legalProceedingsDetails.filingDateStr" name="cpTcDetailsForm"/></td>
				  					</tr>
				  					<tr>
				                          <td class="ColumnBackground" colspan="1">&nbsp;<bean:message key="nameoftheforum"/></td>
				  						<td class="TableData">
				  							<bean:write property="claimapplication.legalProceedingsDetails.nameOfForum" name="cpTcDetailsForm"/>
				  						</td>
				                          <td class="ColumnBackground" colspan="1">&nbsp;<bean:message key="location"/></td>
				  						<td class="TableData">
				  						<bean:write property="claimapplication.legalProceedingsDetails.location" name="cpTcDetailsForm"/>
				  						</td>
				  					</tr>
				  					<tr>
				                        <td class="ColumnBackground" colspan="1">&nbsp;<bean:message key="amntClaimedInTheSuit"/></td>
				  						<td class="TableData">
				  							
												<bean:write property="claimapplication.legalProceedingsDetails.amountClaimed" name="cpTcDetailsForm"/>
											
				  						</td>
										<td class="ColumnBackground" colspan="1">&nbsp;Legal Attachment</td>
										<td class="TableData">
											<%
												if(request.getAttribute("legalDetailsAttachment")!=null)
												{
											%>
												<a href="<%=request.getAttribute("legalDetailsAttachment")%>"> 
												View Attachment 
												</a>
											<%
												}
											%>
										</td>
				  					</tr>
				  					<tr>
				                          <td class="ColumnBackground" colspan="1">&nbsp;<bean:message key="currentStatus"/></td>
				  						<td class="TableData" colspan="3">
				  							<bean:write property="claimapplication.legalProceedingsDetails.currentStatusRemarks" name="cpTcDetailsForm"/>
				  						</td>
				  					</tr>
				  					<tr>
				                          <td class="ColumnBackground" colspan="1">&nbsp;<bean:message key="recoveryProceedingsConcluded"/></td>
				  						<td class="TableData" colspan="3">
				  							<bean:write property="claimapplication.legalProceedingsDetails.isRecoveryProceedingsConcluded" name="cpTcDetailsForm"/>
				  						</td>
				  					</tr>
								</table>
							</td>
						</tr>
				  
						<tr> 
							<td  class="SubHeading">&nbsp;Subsidy Details</td>
						</tr>
									<tr>
				                          <td class="ColumnBackground">&nbsp;Does the project covered under CGTMSE guarantee,involve any subsidy?</td>
				  						<td class="TableData"  colspan="3">
				  							<bean:write property="claimapplication.subsidyFlag" name="cpTcDetailsForm"/>
				  						</td>
				  					</tr>
									<tr>
				                          <td class="ColumnBackground">&nbsp;Has the subsidy been received after NPA?</td>
				  						<td class="TableData" colspan="3">
				  							<bean:write property="claimapplication.isSubsidyRcvdAfterNpa" name="cpTcDetailsForm"/>
				  						</td>
				  					</tr>
									<tr>
				                          <td class="ColumnBackground">&nbsp;Has the subsidy been adjusted against principal/interest overdues?</td>
				  						<td class="TableData" colspan="3">
				  							<bean:write property="claimapplication.isSubsidyAdjustedOnDues" name="cpTcDetailsForm"/>
				  						</td>
				  					</tr>
									<tr>
				                          <td class="ColumnBackground">&nbsp;Subsidy Credit Date</td>
				  						<td class="TableData" colspan="3">
				  							<bean:write property="claimapplication.subsidyDate" name="cpTcDetailsForm"/>
				  						</td>
				  					</tr>
									<tr>
				                          <td class="ColumnBackground">&nbsp;Subsidy Amount</td>
				  						<td class="TableData" colspan="3">
				  							<bean:write property="claimapplication.subsidyAmt" name="cpTcDetailsForm"/>
				  						</td>
				  					</tr>
  				
  				

  	            <tr> 
                    <td colspan="4" class="SubHeading">&nbsp;<bean:message key="termloancompositeloandtls"/></td>
                  </tr>
  				<tr>
                    <td colspan="8" class="SubHeading"><table width="100%" border="0" cellspacing="1" cellpadding="0">
                        <tr>
                          <td class="ColumnBackground" rowspan="2"><div align="center">&nbsp;<bean:message key="sNo"/></div></td>
                          <td class="ColumnBackground" rowspan="2" width="15%"><div align="center">&nbsp;<bean:message key="cgpan"/></div></td>
                          <td class="ColumnBackground" rowspan="2"><div align="center">&nbsp;<bean:message key="dateoflastdisbursement"/></div></td>
						  <td class="ColumnBackground" rowspan="2"><div align="center">&nbsp;Total Amnt Disbursed(Rs.)</div></td>
                          <td class="ColumnBackground" colspan="2"><div align="center" > &nbsp;<bean:message key="repayments"/></div></td>
                          <td class="ColumnBackground" rowspan="2"><div align="center">&nbsp;<bean:message key="outstandingasondateofnpa"/></div></td>
  						<td class="ColumnBackground" rowspan="2"><div align="center">&nbsp;<bean:message key="outstandingstatedinthecivilsuit"/></div></td>
  						<td class="ColumnBackground" rowspan="2"><div align="center">&nbsp;<bean:message key="osAsOnLodgementOfClaim"/></div></td>
  					  </tr>
  					  <tr>
  						<td class="ColumnBackground" width="15%"><div align="center"><bean:message key="principal"/></div></td>
  						<td class="ColumnBackground" width="15%"><div align="center"><bean:message key="interestandothercharges"/></div></td>
  					  </tr>
  					  <% int i=1;%>
  					  <logic:iterate property="claimapplication.termCapitalDtls" id="object" name="cpTcDetailsForm" scope="session">			
  					  <%
  					     TermLoanCapitalLoanDetail tlcldtl = (TermLoanCapitalLoanDetail)object;   					     
  					     cgpan = tlcldtl.getCgpan();
  					     lastDsbrsmntDt = tlcldtl.getLastDisbursementDate();
					     if(lastDsbrsmntDt != null)
					     {
						 lastDsbrsmntDtStr = sdf.format(lastDsbrsmntDt);
					     }
					     else
					     {  					     
						 lastDsbrsmntDtStr = "";
					     }
						 disbAmnt = tlcldtl.getTotaDisbAmnt();
  					     principalRepayment = tlcldtl.getPrincipalRepayment();
  					     interestAndOtherCharges = tlcldtl.getInterestAndOtherCharges();
  					     outstandingAsOnDateOfNPA = tlcldtl.getOutstandingAsOnDateOfNPA();
  					     outstandingStatedInCivilSuit = tlcldtl.getOutstandingStatedInCivilSuit();
  					     outstandingAsOnDateOfLodgement = tlcldtl.getOutstandingAsOnDateOfLodgement();
  					  %>  					          
  					  <tr>					  
  						  <td class="TableData">&nbsp;<%=i%></td>
  						  <td class="TableData">&nbsp;<%= cgpan %></td>
  						  <td class="TableData">  						  
  						  <%=lastDsbrsmntDtStr%>
  						  </td>
						  <td class="TableData">
  							<div align="right"><%=disbAmnt%></div>
  						  </td>
  						  <%
  						     principal="tcprincipal(key-"+i+")";						  
  						  %>
  						  <td class="TableData">
  							<div align="right"><%=principalRepayment%></div>
  						  </td>
  						  <%
  						    interestCharges="tcInterestCharge(key-"+i+")";
  						  %>
  						  <td class="TableData">
  							<div align="right"><%=interestAndOtherCharges%></div>
  						  </td>
  						  <%
  						   osAsOnNpa="tcOsAsOnDateOfNPA(key-"+i+")";
  						  %>
  						  <td class="TableData">
  							<div align="right"><%=decimalFormat.format(outstandingAsOnDateOfNPA)%></div>
  						  </td>
  						  <%
  						   osAsStatedinCivilSuit="tcOsAsStatedInCivilSuit(key-"+i+")";
  						  %>
  						  <td class="TableData">
  							<div align="right"><%=decimalFormat.format(outstandingStatedInCivilSuit)%></div>
  						  </td>  						  
  						  <td class="TableData">
  							<div align="right"><%=decimalFormat.format(outstandingAsOnDateOfLodgement)%></div>
    </td>
  					  </tr>
  					  <%i++;%>
  					  </logic:iterate>
  					  </table></td>
                  </tr>
  				<tr>
  					<td colspan="4" class="SubHeading">
  						&nbsp;<bean:message key="mentiononlyprincipaloutstanding"/></td>
  				</tr>
  				<tr>
  				<td><br></td>
  				</tr>
  	            <tr> 
                    <td colspan="4" class="SubHeading">&nbsp;<bean:message key="workingcapitaldetails"/></td>
                  </tr>
  				<tr>
                    <td colspan="5" class="SubHeading"><table width="100%" border="0" cellspacing="1" cellpadding="0">
                        <tr>
                          <td class="ColumnBackground"><div align="center">&nbsp;<bean:message key="sNo"/></div></td>
                          <td class="ColumnBackground" width="15%"><div align="center">&nbsp;<bean:message key="cgpan"/></div></td>
                          <td class="ColumnBackground" ><div align="center">&nbsp;<bean:message key="oswcasonthedateofnpa"/></div></td>
  						<td class="ColumnBackground" ><div align="center">&nbsp;<bean:message key="oswcstatedinthecivilsuit"/></div></td>
  						<td class="ColumnBackground" ><div align="center">&nbsp;<bean:message key="oswcasonthedateoflodgementofclaim"/></div></td>
  					  </tr>
  					  <% int j = 1; %>
  					  <logic:iterate property="claimapplication.workingCapitalDtls" id="object" name="cpTcDetailsForm" scope="session">
  					  <% 
  					     WorkingCapitalDetail wcdtl = (WorkingCapitalDetail)object; 
  					     cgpan = wcdtl.getCgpan();
  					     outstandingAsOnDateOfNPA = wcdtl.getOutstandingAsOnDateOfNPA();
  					     outstandingStatedInCivilSuit = wcdtl.getOutstandingStatedInCivilSuit();
  					     outstandingAsOnDateOfLodgement = wcdtl.getOutstandingAsOnDateOfLodgement();  					     				  					  
  					  %>  					  
  					  <tr>
  						  <td class="TableData">&nbsp;<%=j%></td>
  						  <td class="TableData">&nbsp;<%=cgpan%></td>
  						  <td class="TableData">
  						  <%
  						    wcAsOnNPA = "wcOsAsOnDateOfNPA(key-"+j+")";
  						  %>
  						   <div align="right"><%=decimalFormat.format(outstandingAsOnDateOfNPA)%></div>
  						  </td>
  						  <td class="TableData">
  						  <%
  						    wcOsAsOnInCivilSuit = "wcOsAsStatedInCivilSuit(key-"+j+")";
  						  %>
  						  <div align="right"><%=decimalFormat.format(outstandingStatedInCivilSuit)%></div>
  						  </td>
  						  <td class="TableData">
  						  <%
  						    wcOsAtLdgmntClm= "wcOsAsOnLodgementOfClaim(key-"+j+")";
  						  %>
  						  <div align="right"><%=decimalFormat.format(outstandingAsOnDateOfLodgement)%>
  						  </div>
  						  </td>
  					  </tr>
  					  <%j++;%>
  					  </logic:iterate>
  					  </table></td>
                  </tr>
  				<tr>
  					<td colspan="4" class="SubHeading">
  						&nbsp;<bean:message key="mentionamountincludinginterest"/>
  					</td>
  				</tr>
  				<tr>
  					<td class="ColumnBackground">&nbsp;<bean:message key="dateofreleaseofwc"/></td>
  					<td class="TableData" colspan="3">
  						<bean:write property="claimapplication.dateOfReleaseOfWCStr" name="cpTcDetailsForm"/>
  					</td>
  				</tr>
  				
                  <tr> 
                    <td colspan="4" class="SubHeading">&nbsp;<bean:message key="securityandpersonalguaranteedtls"/></td>
                  </tr>
                  <tr> 
                    <td colspan="5"><table width="100%" border="0" cellspacing="1">
  					<tr>
  						<td class="ColumnBackground" rowspan="2"><div align="center"><bean:message key="particulars"/></div></td>
  						<td class="ColumnBackground" colspan="2"><div align="center"><bean:message key="security"/></div></td>
  						<td class="ColumnBackground" rowspan="2"><div align="center"><bean:message key="netwothofguarantors"/></div></td>
  						<td class="ColumnBackground" rowspan="2"><div align="center"><bean:message key="reasonsforreductioninsecurity"/></div></td>
  					</tr>
  					<tr>
  						<td class="ColumnBackground"><div align="center"><bean:message key="nature"/></div></td>
  						<td class="ColumnBackground"><div align="center"><bean:message key="value"/></div></td>
  					</tr>
  					<tr>
  						<td class="TableData" rowspan="6"><div align="center"><bean:message key="asondateofsanctionofcredit"/></div></td>
  						<td class="TableData"><div align="center"><bean:message key="land"/></div></td>
  						
  						<td class="TableData"><div align="center">
  						<bean:write property="claimapplication.securityAndPersonalGuaranteeDtls.detailsAsOnDateOfSanction.valueOfLand" name="cpTcDetailsForm"/>							
  						</div></td>
  						
  						<td class="TableData" rowspan="6"><div align="right">
  						<bean:write property="claimapplication.securityAndPersonalGuaranteeDtls.detailsAsOnDateOfSanction.networthOfGuarantors" name="cpTcDetailsForm"/>
  						</div></td>
  						
  						<td class="TableData" rowspan="6"><div align="center">
  						<bean:write property="claimapplication.securityAndPersonalGuaranteeDtls.detailsAsOnDateOfSanction.reasonsForReduction" name="cpTcDetailsForm"/>
  						</div></td>
  					</tr>
  					<tr>
  				        <td class="TableData"><div align="center"><bean:message key="bldg"/></div></td>
  						
  						<td class="TableData"><div align="center">
  						<bean:write property="claimapplication.securityAndPersonalGuaranteeDtls.detailsAsOnDateOfSanction.valueOfBuilding" name="cpTcDetailsForm"/>
  						</div></td>
  					</tr>
  					<tr>
  						<td class="TableData"><div align="center"><bean:message key="machine"/></div></td>
  						
  						<td class="TableData"><div align="center">
  						<bean:write property="claimapplication.securityAndPersonalGuaranteeDtls.detailsAsOnDateOfSanction.valueOfMachine" name="cpTcDetailsForm"/>
  						</div></td>
  					</tr>
  					<tr>
  						<td class="TableData"><div align="center"><bean:message key="assets"/></div></td>
  						
  						<td class="TableData"><div align="center">
  						<bean:write property="claimapplication.securityAndPersonalGuaranteeDtls.detailsAsOnDateOfSanction.valueOfOtherFixedMovableAssets" name="cpTcDetailsForm"/>
  						</div></td>
  					</tr>
  					<tr>
  						<td class="TableData"><div align="center"><bean:message key="currentAssets"/></div></td>
  						
  						<td class="TableData"><div align="center">
  						<bean:write property="claimapplication.securityAndPersonalGuaranteeDtls.detailsAsOnDateOfSanction.valueOfCurrentAssets" name="cpTcDetailsForm"/>
  						</div></td>
  					</tr>
  					<tr>
  						<td class="TableData"><div align="center"><bean:message key="psOthers"/></div></td>
  						
  						<td class="TableData"><div align="center">
  						<bean:write property="claimapplication.securityAndPersonalGuaranteeDtls.detailsAsOnDateOfSanction.valueOfOthers" name="cpTcDetailsForm"/>
  							
  						</div></td>
  					</tr>
  					<tr>
  						<td class="TableData" rowspan="6"><div align="center"><bean:message key="asonthedateofnpa"/></div></td>
  						<td class="TableData"><div align="center"><bean:message key="land"/></div></td>
  						
  						<td class="TableData"><div align="center">
  						<bean:write property="claimapplication.securityAndPersonalGuaranteeDtls.detailsAsOnDateOfNPA.valueOfLand" name="cpTcDetailsForm"/>
  						</div></td>
  						
  						<td class="TableData" rowspan="6"><div align="right">
  						<bean:write property="claimapplication.securityAndPersonalGuaranteeDtls.detailsAsOnDateOfNPA.networthOfGuarantors" name="cpTcDetailsForm"/>						
  						</div></td>
  						
  						<td class="TableData" rowspan="6"><div align="center">
  						<bean:write property="claimapplication.securityAndPersonalGuaranteeDtls.detailsAsOnDateOfNPA.reasonsForReduction" name="cpTcDetailsForm"/>
  						</div></td>
  					</tr>
  					<tr>
  						<td class="TableData"><div align="center"><bean:message key="bldg"/></div></td>
  						
  						<td class="TableData"><div align="center">
  						<bean:write property="claimapplication.securityAndPersonalGuaranteeDtls.detailsAsOnDateOfNPA.valueOfBuilding" name="cpTcDetailsForm"/>						
  						</div></td>
  					</tr>
  					<tr>
  						<td class="TableData"><div align="center"><bean:message key="machine"/></div></td>
  						
  						<td class="TableData"><div align="center">
  						<bean:write property="claimapplication.securityAndPersonalGuaranteeDtls.detailsAsOnDateOfNPA.valueOfMachine" name="cpTcDetailsForm"/>
  						</div></td>
  					</tr>
  					<tr>
  						<td class="TableData"><div align="center"><bean:message key="assets"/></div></td>
  						
  						<td class="TableData"><div align="center">
  						<bean:write property="claimapplication.securityAndPersonalGuaranteeDtls.detailsAsOnDateOfNPA.valueOfOtherFixedMovableAssets" name="cpTcDetailsForm"/>
  						</div></td>
  					</tr>
  					<tr>
  						<td class="TableData"><div align="center"><bean:message key="currentAssets"/></div></td>
  						
  						<td class="TableData"><div align="center">
  						<bean:write property="claimapplication.securityAndPersonalGuaranteeDtls.detailsAsOnDateOfNPA.valueOfCurrentAssets" name="cpTcDetailsForm"/>
  						</div></td>
  					</tr>
  					<tr>
  						<td class="TableData"><div align="center"><bean:message key="psOthers"/></div></td>
  						
  						<td class="TableData"><div align="center">
  						<bean:write property="claimapplication.securityAndPersonalGuaranteeDtls.detailsAsOnDateOfNPA.valueOfOthers" name="cpTcDetailsForm"/>
  						</div></td>
  					</tr>
  					<tr>
  						<td class="TableData" rowspan="6"><div align="center"><bean:message key="asondateoflodgementofcredit"/></div></td>
  						<td class="TableData"><div align="center"><bean:message key="land"/></div></td>
  						
  						<td class="TableData"><div align="center">
  						<bean:write property="claimapplication.securityAndPersonalGuaranteeDtls.detailsAsOnDateOfLodgementOfClaim.valueOfLand" name="cpTcDetailsForm"/>
  						</div></td>
  						
  						<td class="TableData" rowspan="6"><div align="right">
  						<bean:write property="claimapplication.securityAndPersonalGuaranteeDtls.detailsAsOnDateOfLodgementOfClaim.networthOfGuarantors" name="cpTcDetailsForm"/>
  						</div></td>
  						
  						<td class="TableData" rowspan="6"><div align="center">
  						<bean:write property="claimapplication.securityAndPersonalGuaranteeDtls.detailsAsOnDateOfLodgementOfClaim.reasonsForReduction" name="cpTcDetailsForm"/>
  						</div></td>
  					</tr>
  					<tr>
  						<td class="TableData"><div align="center"><bean:message key="bldg"/></div></td>
  						
  						<td class="TableData"><div align="center">
  						<bean:write property="claimapplication.securityAndPersonalGuaranteeDtls.detailsAsOnDateOfLodgementOfClaim.valueOfBuilding" name="cpTcDetailsForm"/>
  						</div></td>
  					</tr>
  					<tr>
  						<td class="TableData"><div align="center"><bean:message key="machine"/></div></td>
  						
  						<td class="TableData"><div align="center">
  						<bean:write property="claimapplication.securityAndPersonalGuaranteeDtls.detailsAsOnDateOfLodgementOfClaim.valueOfMachine" name="cpTcDetailsForm"/>
  						</div></td>
  					</tr>
  					<tr>
  						<td class="TableData"><div align="center"><bean:message key="assets"/></div></td>
  						
  						<td class="TableData"><div align="center">
  						<bean:write property="claimapplication.securityAndPersonalGuaranteeDtls.detailsAsOnDateOfLodgementOfClaim.valueOfOtherFixedMovableAssets" name="cpTcDetailsForm"/>						
  						</div></td>
  					</tr>
  					<tr>
  						<td class="TableData"><div align="center"><bean:message key="currentAssets"/></div></td>
  						
  						<td class="TableData"><div align="center">
  						<bean:write property="claimapplication.securityAndPersonalGuaranteeDtls.detailsAsOnDateOfLodgementOfClaim.valueOfCurrentAssets" name="cpTcDetailsForm"/>
  						</div></td>
  					</tr>
  					<tr>
  						<td class="TableData"><div align="center"><bean:message key="psOthers"/></div></td>
  						
  						<td class="TableData"><div align="center">
  						<bean:write property="claimapplication.securityAndPersonalGuaranteeDtls.detailsAsOnDateOfLodgementOfClaim.valueOfOthers" name="cpTcDetailsForm"/>
  						</div></td>
  					</tr>
                    </table></td>
                  </tr>
  				<tr>
  				<td><br></td>
  				</tr>
  	            
  	       <tr> 
		 <td colspan="4" class="SubHeading">&nbsp;<bean:message key="recoverymadefromborrower"/></td>
	      </tr>
	      			<tr> 
	      				<td colspan="7"><table width="100%" border="0" cellspacing="1" cellpadding="0">
	      				<tr class="ColumnBackground">
	      					<td rowspan="2"><div align="center"><bean:message key="SerialNumber"/></div></td>
	      					<td rowspan="2" width="15%"><div align="center"><bean:message key="cgpan"/></div></td>
	      					<td colspan="2"><div align="center"><bean:message key="termloancompositloaninrs"/></div></td>
	      					<td colspan="2"><div align="center"><bean:message key="workingcapitalinrs"/></div></td>
	      					<td rowspan="2"><div align="center"><bean:message key="modeofrecovery"/></div></td>
	      				</tr>
	      				<tr class="ColumnBackground">
						<td><div align="center"><bean:message key="principal"/></div></td>
						<td><div align="center"><bean:message key="interestandothercharges"/></div></td>
						<td><div align="center"><bean:message key="amountincludinginterest"/></div></td>
						<td><div align="center"><bean:message key="othercharges"/></div></td>
					</tr>
					
					
					<%
					  int k = 1; 
					%>
					<logic:iterate property="claimapplication.recoveryDetails" id="object" name="cpTcDetailsForm">
					<% 
					  RecoveryDetails recDtl = (RecoveryDetails)object; 
					  cgpan = recDtl.getCgpan();
					  // System.out.println("CGPAN :" + cgpan);
					  modeOfRecovery = recDtl.getModeOfRecovery(); 
					  // System.out.println("Mode of Recovery :" + modeOfRecovery);
					  tcPrincipal = recDtl.getTcPrincipal();
					  // System.out.println("TCPrincipal :" + tcPrincipal);
					  tcInterestAndOtherCharges = recDtl.getTcInterestAndOtherCharges();
					  // System.out.println("TC Interest Charges :" + tcInterestAndOtherCharges);
					  wcAmount = recDtl.getWcAmount();
					  // System.out.println("WC Amount :" + wcAmount);
					  wcOtherCharges = recDtl.getWcOtherCharges();
					  // System.out.println("WC Other Charges :" + wcOtherCharges);
					%>  					
					  <tr>
						<td class="TableData"><div align="center">&nbsp;<%=k%></div></td>
						<td class="TableData"><div align="center">&nbsp;<%=cgpan%></div></td>

						<td class="TableData"><div align="right">
						<%=tcPrincipal%>
						</div></td>

						<td class="TableData"><div align="right">
						<%=tcInterestAndOtherCharges%>
						</div></td>

						<td class="TableData"><div align="right">
						<%=wcAmount%>
						</div></td>

						<td class="TableData"><div align="right">
						<%=wcOtherCharges%>
						</div></td>

						<td class="TableData"><div align="center"><%=modeOfRecovery%>
						</div></td>
					  </tr>
					  <%k++;%>
					  </logic:iterate>
					
					
	      				</table></td>
  				<tr>
  					<td class="ColumnBackground">&nbsp;<bean:message key="ifrecoverydonethruots"/></td>
  					<td class="TableData" colspan="3">
  						<bean:write property="claimapplication.dateOfSeekingOTSStr" name="cpTcDetailsForm"/>
  					</td>
  				</tr>											
  				<tr>
  				<td><br></tr>
  				</tr>
  	            <tr> 
                    <td colspan="4" class="SubHeading">&nbsp;<bean:message key="totalamountforwhichguaranteeispreferred"/></td>
                  </tr>
  				<tr> 
  					<td colspan="4"><table width="100%" border="0" cellspacing="1" cellpadding="0">
  						<tr class="ColumnBackground">
  							<td><div align="center"><bean:message key="SerialNumber"/></div></td>
  							<td><div align="center"><bean:message key="cgpan"/></div></td>
  							<td><div align="center"><bean:message key="loanlimitcoveredundercgfsi"/></div></td>
  							<td><div align="center"><bean:message key="amountclaimedinrs"/></div></td>
  						</tr>
  						<%int m=1;%>
  						<logic:iterate property="claimapplication.claimSummaryDtls" name="cpTcDetailsForm" id="object">
  						<%
  						ClaimSummaryDtls clmSummaryDtl = (ClaimSummaryDtls)object;
             
  						cgpan = clmSummaryDtl.getCgpan();
  						String loanApprvd = clmSummaryDtl.getLimitCoveredUnderCGFSI();
  						clmAppliedAmnt = clmSummaryDtl.getAmount();
  						%>						
  						<tr>
  							<td class="TableData"><div align="center">&nbsp;<%=m%></div></td>
  							<td class="TableData"><div align="center">&nbsp;<%=cgpan%></div></td>
  							<td class="TableData"><div align="center">&nbsp;<%=loanApprvd%></div></td>
  							
  							<td class="TableData"><div align="right"><%=decimalFormat.format(clmAppliedAmnt)%></div></td>													
  						</tr>
  						<%m++;%>
             
  						</logic:iterate>
  					</table></td>
                  </tr>
                  <tr>
		  <td><br></td>
		  </tr>
		  <tr>
			<td class="ColumnBackground">&nbsp;<bean:message key="nameofofficial"/></td>
			<td colspan="3" class="TableData"><bean:write property="claimapplication.nameOfOfficial" name="cpTcDetailsForm"/></td>
		  </tr>                
		<tr>
		<td class="ColumnBackground">&nbsp;<bean:message key="designationOfOfficial"/></td>
		<td colspan="3" class="TableData"><bean:write property="claimapplication.designationOfOfficial" name="cpTcDetailsForm"/></td>
		</tr>
    
		<tr>
		<td class="ColumnBackground">&nbsp;<bean:message key="dateofclaimfiling"/></td>
		<td colspan="3" class="TableData" align="center"><bean:write property="claimapplication.claimSubmittedDateStr" name="cpTcDetailsForm"/></td>
		</tr>
		<tr>
		<td class="ColumnBackground">&nbsp;<bean:message key="place"/></td>
		<td colspan="3" class="TableData"><bean:write property="claimapplication.place"  name="cpTcDetailsForm"/></td>
		</tr>
    
    
    <tr>
    <td colspan="4"><div align="center"> 
        <%  String url = "displayClaimRefNumberDtlsMod.do?method=displayClaimRefNumberDtlsMod&"+ClaimConstants.CLM_CLAIM_REF_NUMBER+"="+claimreferencenumber+"&isClaimProceedings="+"Y";%>            
            <A href="<%=url%>">
      <!--    <a href="javascript:submitForm('displayClaimRefNumberDtlsMod.do?method=displayClaimRefNumberDtlsMod&ClaimRefNumber="<%=claimreferencenumber%>"')">   -->
          <IMG src="images/Accept2.gif" alt="Accept2" width="49" height="37" border="0"></A>
                    <%  String url1 = "displayClaimRefNumberDtlsMod.do?method=displayClaimRefNumberDtlsMod&"+ClaimConstants.CLM_CLAIM_REF_NUMBER+"="+claimreferencenumber+"&isClaimProceedings="+"N"; 
           %>
           <A href="<%=url1%>">&nbsp;&nbsp;&nbsp;<IMG src="images/Reject.gif" alt="Reject" width="49" height="37" border="0"></A>
         <A href="javascript:printpage()">&nbsp;&nbsp;&nbsp;<IMG src="images/Print2.gif" alt="Print2" width="49" height="40" border="0"></A> 
            </div>
            </td>          
          </tr>
   	</table></td>
          </tr>
        </table>
        </td>        
      <td width="23" background="images/TableVerticalRightBG.gif">&nbsp;</td>
    </tr>
   <tr><td width="20" align="right" valign="bottom"><img src="images/TableLeftBottom.gif" alt="" width="20" height="51"></td>
        <td colspan="2" valign="bottom" background="images/TableBackground3.gif"> 
        <td width="23" align="right" valign="bottom"><img src="images/TableRightBottom.gif" alt="" width="23" height="51"></td>
 </tr> 
</table>
</html:form>
</body>
</html>


