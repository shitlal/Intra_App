<%@ page language="java"%>
<%@ page import="com.cgtsi.claim.ClaimConstants"%>
<%@page import ="java.text.SimpleDateFormat"%>
<%@page import ="java.text.DecimalFormat"%>
<%@ page import="com.cgtsi.claim.ClaimDetail"%>
<%@ page import = "com.cgtsi.claim.ClaimSummaryDtls"%>
<%@ page import = "com.cgtsi.claim.RecoveryDetails"%>
<%@ page import="com.cgtsi.actionform.ClaimActionForm"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic"%>
<%@ include file="/jsp/SetMenuInfo.jsp" %>
<%SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");%>
<%DecimalFormat decimalFormat = new DecimalFormat("##########.##");%>
<% session.setAttribute("CurrentPage","displayClaimRefNumberDtls.do?method=displayClaimRefNumberDtls");%>
<script>
function calltotalServiceFee()
{
alert("calltotalServiceFee entered");
var asfDeductableforTC=document.getElementById('asfDeductableforTC').innerHTML;;
alert(asfDeductableforTC);
var asfDeductableforWC=document.getElementById('asfDeductableforWC').innerHTML;;
alert(asfDeductableforWC);
alert("calltotalServiceFee exited");
}
function calculatePsTotal()
{
	var tempservicefee=0;
  var asfDeductableforTC=0;
  var asfDeductableforWC=0;
	asfDeductableforTC=findObj("asfDeductableforTC");	
  
	if(asfDeductableforTC!=null && asfDeductableforTC!="")
	{
	var tcsFee=asfDeductableforTC.value;
 // alert(tcsFee);
	}
	if (!(isNaN(tcsFee)) && tcsFee!="")
	{
		tempservicefee+=parseFloat(tcsFee);	
	}

	asfDeductableforWC=findObj("asfDeductableforWC");	
	if(asfDeductableforWC!=null && asfDeductableforWC!="")
	{
	var wcSfee=asfDeductableforWC.value;
 // alert(wcSfee);
	}
	if (!(isNaN(wcSfee)) && wcSfee!="")
	{
		tempservicefee+=parseFloat(wcSfee);	
	}
  var servicefee=document.getElementById('servicefee');
	servicefee.innerHTML=tempservicefee; 
  //alert(servicefee.value);
}
</script>
<%
String bid = null;
ClaimDetail clmDtl = null;
String type = null;
double tcIssued = 0.0;
double wcIssued = 0.0;
double tcRecoveryMade = 0.0;
double wcRecoveryMade = 0.0;
double totalNetOutstanding = 0.0;
double tcClaimApplied = 0.0;
double wcClaimApplied = 0.0;
double tcNetOutstanding = 0.0;
double wcNetOutstanding = 0.0;
double tcServiceFee = 0.0;
double wcServiceFee = 0.0;
double totalServiceFee = 0.0;
%>

<html:errors />
<body onload ="javascript:calculateAmountPayable()">
<html:form action="displayClaimApprovalNew.do?method=displayClaimApprovalNew" method="POST" enctype="multipart/form-data">
  <table width="100%" border="0" cellspacing="0" cellpadding="0">    
    <tr>
      <td class="FontStyle">&nbsp;</td>
    </tr>
  </table>
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr> 
    <td align="right" valign="bottom"><img src="images/TableLeftTop.gif" height="31"></td>
      <td background="images/TableBackground1.gif"><img src="images/ClaimsProcessingHeading.gif" width="131" height="25"></td>
    <td align="right" valign="top" background="images/TableBackground1.gif"> 
      <div align="right"></div></td>
    <td align="left" valign="bottom"><img src="images/TableRightTop.gif" height="31"></td>
  </tr>
  <tr> 
    <td background="images/TableVerticalLeftBG.gif">&nbsp;</td>
    <td colspan="2"><table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td>
		<table width="100%" border="0" cellspacing="1" cellpadding="0">
                                                                
                <tr> 
                  <td colspan="8"><table width="100%" border="0" cellspacing="0" cellpadding="0">
                      <tr> 
                        <td height="20" class="Heading">&nbsp;<bean:message key="claimdetails"/></td>
                        <td align="left" valign="bottom"><img src="images/TriangleSubhead.gif" width="19" height="19"></td>
                      </tr>
                      <tr> 
                        <td colspan="4" class="Heading"><img src="images/Clear.gif" width="5" height="5"></td>
                      </tr>
                    </table></td>
                </tr>
		<%
		       ClaimActionForm claimForm = (ClaimActionForm)session.getAttribute("cpTcDetailsForm") ;
		       clmDtl = claimForm.getClaimdetail();
		       bid = clmDtl.getBorrowerId();
		       // System.out.println("Borrower Id of the Claim Application :" +bid);
		       // System.out.println("Claim Ref Number of the Claim Application :" +clmDtl.getClaimRefNum());
			   String bankName = clmDtl.getMliName();
			   //out.println("bankName:"+bankName);
		%>                
                <tr> 
					<td class="ColumnBackground" colspan="1"> <div align="left">&nbsp; <bean:message key="cpmliname"/></div></td> 		
					<td class="TableData" colspan="8"> <div align="left"> &nbsp;<bean:write property="claimdetail.mliName" name="cpTcDetailsForm"/></div></td>
                </tr>
                <tr> 
                  <td class="ColumnBackground" colspan="1"> <div align="left">&nbsp; <bean:message key="memberId"/></div></td> 
                  <td class="TableData" colspan="8"> <div align="left"> &nbsp;<bean:write property="claimdetail.mliId" name="cpTcDetailsForm"/></div></td>
                </tr>              
                <tr> 
					<td class="ColumnBackground" colspan="1"> <div align="left">&nbsp; <bean:message key="cpssiunitname"/></div></td>        
					<td class="TableData" colspan="8"> <div align="left"> &nbsp;<bean:write property="claimdetail.ssiUnitName" name="cpTcDetailsForm"/></div></td>
                </tr>
            <!--  <tr>
               <td class="ColumnBackground" colspan="1">&nbsp;<bean:message key="whetherrecprocconcluded"/></td>
	       	    <td class="TableData" colspan="8">Yes</td>
		         </tr> -->
			<!--  <tr>
		     <td class="ColumnBackground" colspan="1">&nbsp;<bean:message key="haslegalproceedingsbeeninitiated"/></td>
		     <td class="TableData" colspan="8">Yes</td>
			</tr> -->
				
                
            <!--   <tr> 
                  <td class="ColumnBackground" colspan="1"> <div align="left">&nbsp;<bean:message key="dateofissueofrecallnotice"/></div></td>
                  <td class="TableData" colspan="8"> <div align="left"><bean:write property="claimdetail.dateOfIssueOfRecallNoticeStr" name="cpTcDetailsForm"/></div></td>
            </tr> -->
		
		<tr>
			<td><br></tr>
		</tr>								
		
                <tr> 
                  <td colspan="4" class="SubHeading">&nbsp;<bean:message key="cgpandetails"/></td>
                </tr>
				<tr>
                  <td colspan="8" class="SubHeading">
                  <table width="100%" border="0" cellspacing="1" cellpadding="0">
                      <tr>
                        <td class="ColumnBackground" rowspan="2"><div align="center">&nbsp;<bean:message key="cgpan"/>
                        </td>
                        <td class="ColumnBackground" colspan="2"><div align="center">&nbsp;<bean:message key="creditsanctionedbymli"/>
                        </td>
                        <td class="ColumnBackground" colspan="2"><div align="center">&nbsp;<bean:message key="cpguaranteeissued"/>
                        </td>
                        <td class="ColumnBackground" rowspan="2"><div align="center" > &nbsp;<bean:message key="cpamntdisbursedfortc"/>
                        </td>
                        <td class="ColumnBackground" rowspan="2"><div align="center">&nbsp;<bean:message key="dtoflastdsbrsmntfortc"/>
                        </td>
                        <td class="ColumnBackground" rowspan="2"><div align="center">&nbsp;<bean:message key="dtofreleaseofwc"/>
			</td>
                        <td class="ColumnBackground" rowspan="2"><div align="center">&nbsp;<bean:message key="cpguaranteestartdate"/></div></td>
                        <td class="ColumnBackground" rowspan="2"><div align="center">&nbsp;<bean:message key="cplockinperiodendson"/></div></td>
			
		<!--	<td class="ColumnBackground" rowspan="2"><div align="center">&nbsp;<bean:message key="cpservicefee"/></td>
			<td class="ColumnBackground" rowspan="2"><div align="center">&nbsp;<bean:message key="cpdtofpaymentofservicefee"/></td> -->
			</tr>			
			<tr class="ColumnBackground">
			<td div align="center">&nbsp;<bean:message key="cptermcredit"/></div></td>
			<td div align="center">&nbsp;<bean:message key="cpworkingcapital"/></div></td>			
			<td div align="center">&nbsp;<bean:message key="cptermcredit"/></div></td>
			<td div align="center">&nbsp;<bean:message key="cpworkingcapital"/></div></td>			
			</tr>			
			<logic:iterate property="claimdetail.cgpanDetails" name="cpTcDetailsForm" id="object">
			<%
			java.util.HashMap hashmap = (java.util.HashMap)object;
			String cgpan = (String)hashmap.get(ClaimConstants.CLM_CGPAN);
			double tcSanctionedAmnt = ((Double)hashmap.get(ClaimConstants.CLM_TC_SANCTIONED_AMNT)).doubleValue();
			double wcFBSanctionedAmnt = ((Double)hashmap.get(ClaimConstants.CLM_WC_FB_SANCTIONED_AMNT)).doubleValue();
			double wcNFBSanctionedAmnt = ((Double)hashmap.get(ClaimConstants.CLM_WC_NFB_SANCTIONED_AMNT)).doubleValue();						
			double totalWCSanctionedAmnt = wcFBSanctionedAmnt + wcNFBSanctionedAmnt;
			double tcApprovedAmnt = 0.0;
			double wcApprovedAmnt = 0.0;
			java.util.Date lastDisbursementDt = null;
			String lastDisbursementDtStr = null;
			double totalDisbursedAmount = 0.0;
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
			String loanType = (String)hashmap.get(ClaimConstants.CGPAN_LOAN_TYPE);
			type = loanType;
			if(loanType.equals(ClaimConstants.CGPAN_TC_LOAN_TYPE))
			{
			    tcApprovedAmnt = ((Double)hashmap.get(ClaimConstants.CLM_APPLICATION_APPRVD_AMNT)).doubleValue(); 
			    tcIssued = tcApprovedAmnt;
				totalDisbursedAmount = ((Double)hashmap.get(ClaimConstants.CLM_TOTAL_DISBURSEMENT_AMNT)).doubleValue();
			    lastDisbursementDt = (java.util.Date)hashmap.get(ClaimConstants.CLM_LAST_DISBURSEMENT_DT);
			    // lastDisbursementDtStr = lastDisbursementDt.toString();
			    if(lastDisbursementDt != null)
			    {
			    	lastDisbursementDtStr = sdf.format(lastDisbursementDt);
			    }
			}
			else if(loanType.equals(ClaimConstants.CGPAN_WC_LOAN_TYPE))
			{
			    wcApprovedAmnt = ((Double)hashmap.get(ClaimConstants.CLM_APPLICATION_APPRVD_AMNT)).doubleValue();
			    wcIssued = wcApprovedAmnt;
				totalDisbursedAmount = 0.0;
			    lastDisbursementDtStr = "-";
			}
			java.util.Date guaranteeStartDt = (java.util.Date)hashmap.get(ClaimConstants.CLM_GUARANTEE_START_DT);
			String guaranteeStartDtStr = null;
			if(guaranteeStartDt != null)
			{
			    // guaranteeStartDtStr = guaranteeStartDt.toString();
			    guaranteeStartDtStr = sdf.format(guaranteeStartDt);
			}
			else
			{
			    guaranteeStartDtStr = "-";
			}
			java.util.Date dtOfReleaseOfWC = (java.util.Date)hashmap.get(ClaimConstants.CLM_DT_OF_RELEASE_OF_WC);
			String dtOfReleaseOfWCStr = null;
			if(dtOfReleaseOfWC != null)
			{
			    // dtOfReleaseOfWCStr = dtOfReleaseOfWC.toString();
			    dtOfReleaseOfWCStr = sdf.format(dtOfReleaseOfWC);
			}
			else
			{
			    dtOfReleaseOfWCStr = "-";
			}
			Double serviceFee = (Double)hashmap.get(ClaimConstants.CLM_RP_AMOUNT_RAISED);
			String serviceFeeStr = null;
			if(serviceFee != null)
			{
			    serviceFeeStr = serviceFee.toString();
			}
			else
			{
			    serviceFeeStr = "";
			}
			
			java.util.Date lastServiceFeeDate = (java.util.Date)hashmap.get(ClaimConstants.CLM_RP_APPROPRIATION_DT);
			String lastServiceFeeDateStr = null;
			if(lastServiceFeeDate != null)
			{
			    // lastServiceFeeDateStr =  lastServiceFeeDate.toString();
			    lastServiceFeeDateStr = sdf.format(lastServiceFeeDate);
			}
			else
			{
			     lastServiceFeeDateStr = "";
			}
			java.util.Date lockInPeriodEndDate = (java.util.Date)hashmap.get(ClaimConstants.CLM_LOCK_IN_PERIOD_END_DATE);
			String lockInPeriodEndDateStr = null;
			if(lockInPeriodEndDate != null)
			{
			   // lockInPeriodEndDateStr =  lockInPeriodEndDate.toString();
			   lockInPeriodEndDateStr = sdf.format(lockInPeriodEndDate);
			}
			else
			{
			   lockInPeriodEndDateStr = "-";
			}
			%>
			<tr class="TableData">
			<td><%=cgpan%></td>
			<td><div align="center"><%=tcSanctionedAmnt%></div></td>
			<td><div align="center"><%=totalWCSanctionedAmnt%></div></td>
			<td><div align="center"><%=tcApprovedAmnt%></div></td>
			<td><div align="center"><%=wcApprovedAmnt%></div></td>
			<td><div align="center"><%=totalDisbursedAmount%></div></td>

			<td><div align="center"><%=lastDisbursementDtStr%></div></td>
			<td><div align="center"><%=dtOfReleaseOfWCStr%></div></td>
			<td><div align="center"><%=guaranteeStartDtStr%></div></td>
			<td><div align="center"><%=lockInPeriodEndDateStr%></div></td>			
		<!--	<td><div align="center"><%=serviceFeeStr%></div></td>
			<td><div align="center"><%=lastServiceFeeDateStr%></div></td> -->
			</tr>
      
			</logic:iterate>
			</table></td>
                </tr>
                
                <tr>
			<td><br></tr>
		</tr>
    <tr> 
		   <td colspan="7" class="SubHeading">&nbsp;Dan Summary Report Details </td>
    </tr>
    
      <TR>
       <TD align="left" class="ColumnBackground"><bean:message key="dan"/></TD>
                  <TD width = "15%" align="left" class="ColumnBackground"> 
                  &nbsp;CGPAN</TD>
                  <TD  align="right" class="ColumnBackground"> 
								  &nbsp;DAN Amt</TD>
                  <TD width = "20%" align="left" class="ColumnBackground"> 
								&nbsp;<bean:message key="payId"/></TD>
                <TD width = "10%" align="left" class="ColumnBackground"> 
								&nbsp;Realised Date</TD>
                <TD width = "10%" align="left" class="ColumnBackground"> 
								&nbsp;Appr Date</TD>
                <TD align="left" class="ColumnBackground"> 
									&nbsp;DD No.</TD>
                 <!--  <TD align="left" class="ColumnBackground"> 
									&nbsp;Expiry Dt.</TD> -->
       <!--       <TD width = "10%" align="left" class="ColumnBackground"> 
								&nbsp;Expiry Date
									</TD>
                    <TD align="left" class="ColumnBackground"> 
									&nbsp;<bean:message key="Closure"/>
									</TD> -->
              		</TR>
                  <tr>
                <logic:iterate property="claimdetail.cgpanDetails" name="cpTcDetailsForm" id="object">
                  <%
                  java.util.HashMap hashmap = (java.util.HashMap)object;
			            String cgpan = (String)hashmap.get(ClaimConstants.CLM_CGPAN);
                  System.out.println("CGPAN:"+cgpan);
                  java.util.ArrayList danSummary = (java.util.ArrayList)claimForm.getDanSummaryReportDetails(cgpan);
                  int danSummarysize=danSummary.size();
                  com.cgtsi.reports.ApplicationReport dReport =  null;
                  
                  for (int j=0; j<danSummarysize;j++)
									{
										dReport=(com.cgtsi.reports.ApplicationReport)danSummary.get(j);
									 String danId = dReport.getDan();
                   String cgpanNew = dReport.getCgpan();
                   String payId = dReport.getPayId();
                   String danRaised = dReport.getAppGuaranteeFee();
                   java.util.Date utilDate3=dReport.getAppGuarStartDateTime();
													String formatedDate3 = null;
													if(utilDate3 != null)
													{
														 formatedDate3=dateFormat.format(utilDate3);
													}
													else
													{
														 formatedDate3 = "Not Appr";
													}
                          java.util.Date utilDate5=dReport.getStartDate();
													String formatedDate5 = null;
													if(utilDate5 != null)
													{
														 formatedDate5=dateFormat.format(utilDate5);
													}
													else
													{
														 formatedDate5 = "";
													}
                           java.util.Date utilDate6=dReport.getExpiryDate();
													String formatedDate6 = null;
													if(utilDate6 != null)
													{
														 formatedDate6=dateFormat.format(utilDate6);
													}
													else
													{
														 formatedDate6 = "";
													}
                      String ddNum = dReport.getDdNum();
                   // System.out.println(danId);
                   %>
                  <tr>
                  <TD  align="left" valign="top" class="ColumnBackground1"> <%=danId%></td>
                  <TD  align="left" valign="top" class="ColumnBackground1"> <%=cgpanNew%></td>
                  <TD  align="right" valign="top" class="ColumnBackground1"> <%=danRaised%></td>
                  <TD  align="left" valign="top" class="ColumnBackground1"> <%=payId%></td>
                  <TD  align="left" valign="top" class="ColumnBackground1"> <%=formatedDate3%></td> 
                  <TD  align="left" valign="top" class="ColumnBackground1"> <%=formatedDate5%></td> 
                  <TD  align="left" valign="top" class="ColumnBackground1"> <%=ddNum%></td>
               <!--   <TD  align="left" valign="top" class="ColumnBackground1"> <%=formatedDate6%></td> -->
                  </tr>
                 
                  <%
                	 }
                  %>
                  </logic:iterate>
                </tr> 
                
                <tr> 
		   <td colspan="4" class="SubHeading">&nbsp;NPA Details</td>
                </tr>
                
                <tr> 
		  <td class="ColumnBackground" colspan="1"> <div align="left">&nbsp;<bean:message key="dateofnpa"/></div></td>
		  <td class="TableData" colspan="8"> <div align="left"> &nbsp;<bean:write property="claimdetail.npaDateStr" name="cpTcDetailsForm"/></div></td>
		</tr>
		<tr> 
		  <td class="ColumnBackground" colspan="1"> <div align="left">&nbsp;<bean:message key="dateofreporting"/></div></td>
		  <td class="TableData" colspan="8"> <div align="left"> &nbsp;<bean:write property="claimdetail.dtOfNPAReportedToCGTSIStr" name="cpTcDetailsForm"/></div></td>
		</tr>
		<tr> 
		  <td class="ColumnBackground" colspan="1"> <div align="left">&nbsp;<bean:message key="reasonfornpa"/></div></td>
		  <td class="TableData" colspan="8"> <div align="left"> &nbsp;<bean:write property="claimdetail.reasonForTurningNPA" name="cpTcDetailsForm"/></div></td>
    </tr>
    <tr> 
    <td class="ColumnBackground" colspan="1"> <div align="left">&nbsp;<bean:message key="dateofissueofrecallnotice"/></div></td>
    <td class="TableData" colspan="8"> <div align="left"><bean:write property="claimdetail.dateOfIssueOfRecallNoticeStr" name="cpTcDetailsForm"/></div></td>
   </tr>
   <tr>
		<td class="ColumnBackground" colspan="1">&nbsp;<bean:message key="haslegalproceedingsbeeninitiated"/></td>
		<td class="TableData" colspan="8">Yes</td>
		</tr> 
    <tr>
    <td class="ColumnBackground" colspan="1">&nbsp;<bean:message key="whetherrecprocconcluded"/></td>
	  <td class="TableData" colspan="8"><bean:write property="claimapplication.legalProceedingsDetails.isRecoveryProceedingsConcluded" name="cpTcDetailsForm"/></td>
		</tr>
    <tr>
    <td class="ColumnBackground" colspan="1">&nbsp;Date of filing of suit/ legal proceedings like DRT,Revenue recovery,SERFASI etc</td>
	  <td class="TableData" colspan="8"><div align="left"><% java.util.Date legalDate = clmDtl.getLegalFilingDate();
                          String legalSuitNo = clmDtl.getLegalSuitNumber();
                          String legalForum = clmDtl.getLegalForum();
                          String legalForumName = clmDtl.getLegalForumName();
                          String legalLocation = clmDtl.getLegalLocation();
                          String formatedDate8 = null;
													if(legalDate != null)
													{
														 formatedDate8=dateFormat.format(legalDate);
													}
													else
													{
														 formatedDate8 = "";
													}
    %>
Legal Forum Name:&nbsp;<%=legalForumName%> at <%=legalLocation%>&nbsp;Legal Suit Number <%=legalSuitNo%>&nbsp;Dated &nbsp;<%=formatedDate8%> </div></td>
		</tr>
   <!--  <tr>
    <td class="ColumnBackground">&nbsp;Date of Receipt of complete information: </td>
    <td class="ColumnBackground" colspan="7">&nbsp;<html:text property="dateofReceipt" size="15" alt="dateofReceipt" name="cpTcDetailsForm" maxlength="10"/>
										<IMG src="images/CalendarIcon.gif" width="20" onClick="showCalendar('cpTcDetailsForm.dateofReceipt')" align="center">
	  </td>
	  </tr> -->
		<tr>
		<td><br></tr>
		</tr>
                
                <tr> 
		           <td colspan="3" class="SubHeading">&nbsp;<!--<bean:message key="cpnpasummry"/>-->Claim Settlement Details</td>
                </tr>
                <tr>
                <td class="ColumnBackground" colspan="2">&nbsp;</td>
                <td class="ColumnBackground" colspan="2">&nbsp;Term Loan</td>
                <td class="ColumnBackground" colspan="2">&nbsp;Working Capital</td>
                <td class="ColumnBackground" colspan="2"><div align="center">Total</div></td>
                </tr>
                <tr><td class="ColumnBackground" colspan="2">&nbsp;Guarantee Issued</td>
                <td class="ColumnBackground" colspan="2">&nbsp;<%=tcIssued%></td>
                <td class="ColumnBackground" colspan="2">&nbsp;<%=wcIssued%></td>
                <td class="ColumnBackground" colspan="2">&nbsp;<%=(tcIssued+wcIssued)%></td>
                </tr>
                <tr><td class="ColumnBackground" colspan="2">&nbsp;Amount Outstanding as on date of NPA</td>
                <td class="ColumnBackground" colspan="2">&nbsp;<bean:write property="claimdetail.totalTCOSAmountAsOnNPA" name="cpTcDetailsForm" /></td>
                <td class="ColumnBackground" colspan="2">&nbsp;<bean:write property="claimdetail.totalWCOSAmountAsOnNPA" name="cpTcDetailsForm" /></td>
                 <% double tcOutstandingasonNPA = clmDtl.getTotalTCOSAmountAsOnNPA();
                    double wcOutstandingasonNPA =  clmDtl.getTotalWCOSAmountAsOnNPA();
                    double totalAmntAsOnNPA = tcOutstandingasonNPA+wcOutstandingasonNPA;
                  %>
                <td class="ColumnBackground" colspan="2">&nbsp;<%=totalAmntAsOnNPA%><!--<html:text property="totalAmntAsOnNPA" size="25" alt="totalAmntAsOnNPA" name="cpTcDetailsForm" maxlength="10" />--></td>
                </tr>
            	<logic:iterate property="claimapplication.recoveryDetails" id="object" name="cpTcDetailsForm">
				      <%
  					    RecoveryDetails recDtl = (RecoveryDetails)object; 
                double	  tcInterestAndOtherCharges = recDtl.getTcInterestAndOtherCharges();
					  // System.out.println("TC Interest Charges :" + tcInterestAndOtherCharges);
                double  tcPrincipal = recDtl.getTcPrincipal();
                tcRecoveryMade = tcInterestAndOtherCharges + tcPrincipal;
					    System.out.println("tcRecoveryMade :" + tcRecoveryMade);
			          double		  wcAmount = recDtl.getWcAmount();
					  // System.out.println("WC Amount :" + wcAmount);
					      double  wcOtherCharges = recDtl.getWcOtherCharges();
                wcRecoveryMade = wcAmount + wcOtherCharges; 
                System.out.println("wcRecoveryMade:"+wcRecoveryMade);
                 %>
              </logic:iterate>
                 <tr><td class="ColumnBackground" colspan="2">&nbsp;Amount Recovered after NPA</td>
                <td class="ColumnBackground" colspan="2">&nbsp;<%=tcRecoveryMade%></td>
                <td class="ColumnBackground" colspan="2">&nbsp;<%=wcRecoveryMade%></td>
                <td class="ColumnBackground" colspan="2">&nbsp;<%=(tcRecoveryMade+wcRecoveryMade)%></td>
               </tr>
               <%
                tcNetOutstanding = Math.min(tcIssued,tcOutstandingasonNPA)-(tcRecoveryMade);
                wcNetOutstanding = Math.min(wcIssued,wcOutstandingasonNPA)-(wcRecoveryMade);
                totalNetOutstanding = tcNetOutstanding+wcNetOutstanding;
               %>
                <tr><td class="ColumnBackground" colspan="2">&nbsp;Net Outstanding (Amount in default admissible by CGTMSE) </td>
                <td class="ColumnBackground" colspan="2">&nbsp;<%=tcNetOutstanding%></td>
                <td class="ColumnBackground" colspan="2">&nbsp;<%=wcNetOutstanding%></td>
                <td class="ColumnBackground" colspan="2">&nbsp;<%=totalNetOutstanding%></td>
               </tr>
              <%int m=1;%>
           	<logic:iterate property="claimapplication.claimSummaryDtls" name="cpTcDetailsForm" id="object">
  						<%
  						ClaimSummaryDtls clmSummaryDtl = (ClaimSummaryDtls)object;             
  						String loanApprvd = clmSummaryDtl.getLimitCoveredUnderCGFSI();
              String cgpan = clmSummaryDtl.getCgpan();
              String panType = cgpan.substring(11,13);
  					//	clmAppliedAmnt = clmSummaryDtl.getAmount();
            if(panType.equals(ClaimConstants.CGPAN_TC_LOAN_TYPE)){
                 tcClaimApplied = clmSummaryDtl.getAmount();
              //  System.out.println("wcClaimApplied:"+wcClaimApplied);
             }
              if(panType.equals(ClaimConstants.CGPAN_WC_LOAN_TYPE)){
                  wcClaimApplied = clmSummaryDtl.getAmount();
                //   System.out.println("wcClaimApplied:"+wcClaimApplied);
                }
  						%>
            </logic:iterate>
            <%m++;%>
			
			<%
						String microFlag="N";
						String womenOperated = clmDtl.getWomenOperated();
                        String nerFlag = clmDtl.getNerFlag();
                        String stateName = clmDtl.getStateName();
                        String typeofActivity = clmDtl.getTypeofActivity();
                        String schemeName = clmDtl.getSchemeName();
                          if(typeofActivity==null ||typeofActivity.equals("")){
                            typeofActivity  = "";
                          }
                           if(schemeName==null ||schemeName.equals("")){
                            schemeName  = "";
                          }
				//out.println("womenOperated:"+womenOperated+"nerFlag:"+nerFlag+"stateName:"+stateName+"typeofActivity:"+typeofActivity+"schemeName:"+schemeName);
			%>
			<logic:equal property="claimapplication.microCategory" value="Y" name="cpTcDetailsForm">	
                 <%
                    microFlag="Y";
                    //out.println("Micro Flag:"+microFlag);
                  %>
            </logic:equal>
			<%out.println("Micro Flag:"+microFlag);%>
                <tr>
					<td class="ColumnBackground" colspan="2">&nbsp;Amount Claimed by MLI</td>
					<td class="ColumnBackground" colspan="2">&nbsp;<%=tcClaimApplied%></td>
					<td class="ColumnBackground" colspan="2">&nbsp;<%=wcClaimApplied%></td>
					<td class="ColumnBackground" colspan="2">&nbsp;<%=(tcClaimApplied+wcClaimApplied)%></td>
               </tr>
              <tr><td class="ColumnBackground" colspan="2">&nbsp;<bean:message key="clmeligibilityamnt"/></td>
               <%
                           double tcClaimEligible = 0.0;
                           double wcClaimEligible = 0.0;
                           double tcFirstInstallment = 0.0;
                           double wcFirstInstallment = 0.0;
                           double totalFirstInstalment = 0.0;
						   //out.println("tcIssued:"+tcIssued);
						   //out.println("wcIssued:"+wcIssued);
						   double rate = 0.0;
						   double r1 = ((tcIssued+wcIssued)-5000000)/(tcIssued+wcIssued);
						   double r2 = 5000000/(tcIssued+wcIssued);
				 if("RSF".equals(schemeName)){
					rate = 0.50;
					tcClaimEligible =  Math.round(tcNetOutstanding * rate);
					wcClaimEligible =  Math.round(wcNetOutstanding * rate); 
				}else if("CGFSI".equals(schemeName)){
					out.println("schemeName:"+schemeName);
					if((tcIssued+wcIssued)<=500000 && "Y".equals(microFlag)){
						// if("Y".equals(casesforsafe)){
							// rate = 0.85;
						// }else{
							// rate = 0.80;
						// }
						rate = 0.85;
						tcClaimEligible =  Math.round(tcNetOutstanding * rate);
						wcClaimEligible =  Math.round(wcNetOutstanding * rate);
					}else if((tcIssued+wcIssued)<=500000 && "N".equals(microFlag)){
						rate = 0.75;
						tcClaimEligible =  Math.round(tcNetOutstanding * rate);
						wcClaimEligible =  Math.round(wcNetOutstanding * rate); 
					}else if((tcIssued+wcIssued)>5000000 && "Y".equals(nerFlag)){
						tcClaimEligible =  Math.round(tcNetOutstanding * (0.50) * r1) + Math.round(tcNetOutstanding * (0.80) * r2);
						wcClaimEligible =  Math.round(wcNetOutstanding * (0.50) * r1) + Math.round(wcNetOutstanding * (0.80) * r2); 	
					}else if((tcIssued+wcIssued)>5000000 && "N".equals(nerFlag) && "F".equals(womenOperated)){
						tcClaimEligible =  Math.round(tcNetOutstanding * (0.50) * r1) + Math.round(tcNetOutstanding * (0.80) * r2);
						wcClaimEligible =  Math.round(wcNetOutstanding * (0.50) * r1) + Math.round(wcNetOutstanding * (0.80) * r2);
					}else if((tcIssued+wcIssued)>5000000 && "N".equals(nerFlag) && "M".equals(womenOperated)){
						tcClaimEligible =  Math.round(tcNetOutstanding * (0.50) * r1) + Math.round(tcNetOutstanding * (0.75) * r2);
						wcClaimEligible =  Math.round(wcNetOutstanding * (0.50) * r1) + Math.round(wcNetOutstanding * (0.75) * r2);
					} else {
						rate = 0.75;
						tcClaimEligible =  Math.round(tcNetOutstanding * rate);
						wcClaimEligible =  Math.round(wcNetOutstanding * rate);
					}
				}else {
					rate = 0.75;
					tcClaimEligible =  Math.round(tcNetOutstanding * rate);
					wcClaimEligible =  Math.round(wcNetOutstanding * rate);
				} 
			   
			   
                /* if((tcIssued+wcIssued)<=500000){
                 tcClaimEligible =  Math.round(tcNetOutstanding * 0.80);
                 wcClaimEligible =  Math.round(wcNetOutstanding * 0.80);                 
                }else if((tcIssued+wcIssued)>500000){
                 tcClaimEligible =  Math.round(tcNetOutstanding * 0.75);
                 wcClaimEligible =  Math.round(wcNetOutstanding * 0.75);                 
                }  */
               claimForm.setTcClaimEligibleAmt(tcClaimEligible);
               claimForm.setWcClaimEligibleAmt(wcClaimEligible);
               
               tcFirstInstallment = Math.round(tcClaimEligible  * 0.75);
               wcFirstInstallment  = Math.round(wcClaimEligible * 0.75);
               totalFirstInstalment = tcFirstInstallment + wcFirstInstallment;
               claimForm.setTcFirstInstallment(tcFirstInstallment);
               claimForm.setWcFirstInstallment(wcFirstInstallment);
               %>
               
                <td class="ColumnBackground" colspan="2">&nbsp;<bean:write property="tcClaimEligibleAmt" name="cpTcDetailsForm"/></td>
                <td class="ColumnBackground" colspan="2">&nbsp;<bean:write property="wcClaimEligibleAmt" name="cpTcDetailsForm"/></td>
                <td class="ColumnBackground" colspan="2">&nbsp;<%=(tcClaimEligible+wcClaimEligible)%></td>
               </tr>
              <tr><td class="ColumnBackground" colspan="2">&nbsp;Amount Payable in First Installment</td>
                  <td class="ColumnBackground" colspan="2">&nbsp;<bean:write property="tcFirstInstallment" name="cpTcDetailsForm"/></td>
                <td class="ColumnBackground" colspan="2">&nbsp;<bean:write property="wcFirstInstallment" name="cpTcDetailsForm"/></td>
                <% clmDtl.setEligibleClaimAmt(tcFirstInstallment+wcFirstInstallment); %>
                <td class="ColumnBackground" colspan="2">&nbsp;<bean:write property="claimdetail.eligibleClaimAmt" name="cpTcDetailsForm"/></td>
             </tr>
      <logic:iterate property="claimdetail.cgpanDetails" name="cpTcDetailsForm" id="object">
			<%
			java.util.HashMap hashmap = (java.util.HashMap)object;
		    String cgpan = (String)hashmap.get(ClaimConstants.CLM_CGPAN);
			//String panType = cgpan.substring(11,13);
			String panType = cgpan.substring(cgpan.length()-2,cgpan.length());
			//out.println("panType:"+panType);
  					//	clmAppliedAmnt = clmSummaryDtl.getAmount();
            if(panType.equals(ClaimConstants.CGPAN_TC_LOAN_TYPE)||panType.equals(ClaimConstants.CGPAN_CC_LOAN_TYPE))
            {
                tcServiceFee = ((Double)hashmap.get(ClaimConstants.CLM_TOTAL_SERVICE_FEE)).doubleValue();
				tcServiceFee = tcServiceFee *12.36;
                totalServiceFee = totalServiceFee+tcServiceFee;
              //  System.out.println("wcClaimApplied:"+wcClaimApplied);
            }
            if(panType.equals(ClaimConstants.CGPAN_WC_LOAN_TYPE)){
                wcServiceFee = ((Double)hashmap.get(ClaimConstants.CLM_TOTAL_SERVICE_FEE)).doubleValue();
				wcServiceFee = wcServiceFee *12.36;
                //   System.out.println("wcClaimApplied:"+wcClaimApplied);
                totalServiceFee = totalServiceFee+wcServiceFee;
            }
      %>
       </logic:iterate>
		<tr>
			<td class="ColumnBackground" colspan="2">&nbsp;Amount Deductable from MLI, if any hello</td>
			<td class="ColumnBackground" colspan="2">&nbsp;<%=tcServiceFee%></td>
			<td class="ColumnBackground" colspan="2">&nbsp;<%=wcServiceFee%></td>
			<td class="ColumnBackground" colspan="2" id="servicefee">&nbsp;<%=totalServiceFee%></td>
		</tr>
      
             <%--   <tr>
                <td class="SubHeading" colspan="8">	
                <table width="100%" border="0" cellspacing="1" cellpadding="0">
                <tr>
                <td class="ColumnBackground" rowspan="2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div></td>
                <td class="ColumnBackground" colspan="2" div align="center">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="cpoutstandingamount"/></div></td>
                <td class="ColumnBackground" colspan="2" div align="center">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="cpoutstandingamount"/></div></td>                
                <td class="ColumnBackground" rowspan="2" div align="center">Total</div></td>
                </tr>
                <tr>
                <td class="ColumnBackground" div align="center">&nbsp;<bean:message key="cptcprincipal"/></div></td>
                <td class="ColumnBackground" div align="center">&nbsp;<bean:message key="cptcintesrtchrges"/></div></td>
                <td class="ColumnBackground" div align="center">&nbsp;<bean:message key="cpwcincludinginterest"/></div></td>
                <td class="ColumnBackground" div align="center">&nbsp;<bean:message key="cpothercharges"/></div></td>
                </tr>
                <%                
                String tcInterestChargeForThisBorrower = "tcInterestCharge("+bid+com.cgtsi.claim.ClaimConstants.CLM_AS_ON_NPA+")";
                %>
                <tr class="TableData">
                <td>&nbsp;<bean:message key="cpasonnpa"/></td>
                <td id="tcOutstandingAmtNPA"><bean:write property="claimdetail.totalTCOSAmountAsOnNPA" name="cpTcDetailsForm" /></td>
                <td><html:text property="tcInterestChargeForThisBorrower" name="cpTcDetailsForm" onblur= "javascript:calculateAmountPayable()" maxlength="16" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)"/></td>
                <td id="wcPrincipalNPA"><bean:write property="claimdetail.totalWCOSAmountAsOnNPA" name="cpTcDetailsForm" /></td>
                <td><html:text property="wcOtherChargesAsOnNPA" name="cpTcDetailsForm" onblur= "javascript:calculateAmountPayable()" maxlength="16" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)"/></td>
                <td id="totalAmntAsOnNPA"><bean:write property="totalAmntAsOnNPA" name="cpTcDetailsForm"/></td>
                </tr>
                --%>
                <%
                String tcprncplrecafternpa = "tcprincipal("+bid+com.cgtsi.claim.ClaimConstants.CLM_RECOVERIES_AFTER_NPA+")";
                String tcintrstchrgsrecafternpa = "tcInterestCharge("+bid+com.cgtsi.claim.ClaimConstants.CLM_RECOVERIES_AFTER_NPA+")";
                %>
       
		<tr>
		<td><br></tr>
		</tr>

                <tr> 
		   <td colspan="10" class="SubHeading">&nbsp;<bean:message key="cpclmsummary"/></td>
                </tr>
                <%
                String clmAppliedStr = claimForm.getAmountclaimed();
                %>
<%--		<tr > 
		  <td class="ColumnBackground" colspan="1"> <div align="left">&nbsp;<bean:message key="cpamntclaimed"/></div></td>
		  <td class="TableData" colspan="8"> <div align="left"> &nbsp;<%=clmAppliedStr%></div></td>
		</tr>
		<tr > 
		  <td class="ColumnBackground" colspan="1"> <div align="left">&nbsp;<bean:message key="clmeligibilityamnt"/></div></td>
		  <td class="TableData" colspan="8"> <div align="left"> &nbsp;<bean:write property="claimdetail.eligibleClaimAmt" name="cpTcDetailsForm" /></div></td>
		</tr>	 --%>	
		<html:hidden property="clmRefDtlSet" name="cpTcDetailsForm" value="<%=ClaimConstants.DISBRSMNT_YES_FLAG%>"/>
		<tr> 
		  <td class="ColumnBackground" colspan="1"> <div align="left">&nbsp;<bean:message key="cpamntpayablenow"/></div></td>
		  <td class="TableData" colspan="8"> <div align="left"> &nbsp;<%=(totalFirstInstalment-totalServiceFee)%></div></td>
		</tr>		
  <!--  <tr><td class="TableData">Comments</td>
   <td align="left" valign="top" class="TableData" colspan="7">
    <html:text property="claimdetail.comments"  size="100" alt="comments" name="cpTcDetailsForm"/>
	 </td>
    </tr> -->
              </table>
           </td>
        </tr>
      </table></td>
    <td background="images/TableVerticalRightBG.gif">&nbsp;</td>
  </tr>
  <%
	String url="displayClaimApprovalNewForNewCases.do?method=displayClaimApprovalNewForNewCases&Link="+bankName;
	//out.println("url:"+url);
  %>
  <tr> 
      <td align="right" valign="bottom"><img src="images/TableLeftBottom.gif" width="20" height="51"></td>
      <td colspan="2" valign="bottom" background="images/TableBackground3.gif"> 
        <div>
          <div align="center">          
        <!--  <A href="javascript:submitForm('displayClaimApprovalNew.do?method=displayClaimApprovalNew')"> -->
		<A href="javascript:submitForm('<%=url%>')">
		  <img src="images/Back.gif" alt="Save" width="49" height="37" border="0"></a>
          </div>
      </div></td>
      <td align="right" valign="bottom"><img src="images/TableRightBottom.gif" width="23" height="51"></td>
  </tr>
</table>
</html:form>
</body>
</html>
