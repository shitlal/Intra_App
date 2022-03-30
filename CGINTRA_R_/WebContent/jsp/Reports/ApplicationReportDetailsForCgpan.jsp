<%@ page language="java"%>
<%@ page import="java.util.ArrayList"%>
<%@page import ="java.text.SimpleDateFormat"%>
<%@page import ="java.text.DecimalFormat"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic" %>
<%@ include file="/jsp/SetMenuInfo.jsp" %>
<%SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");%>
<%DecimalFormat decimalFormat = new DecimalFormat("##########.##");%>
<% session.setAttribute("CurrentPage","applicationReportDetails1.do?method=applicationReportDetails1");%>


<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form action="applicationReportDetails.do?method=applicationReportDetails" method="POST" enctype="multipart/form-data">
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
											<TR>
												<TD width="26%" class="Heading"><bean:message key="applicationHeader" /></TD>
												<TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
											</TR>
											<TR>
												<TD colspan="3" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
											</TR>

										</TABLE>
									</TD>

				<tr> 
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="cgpanForApplication" /></b><bean:write property="applicationReport.cgpan" name="rsForm"/></div></td>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="memberIdForApplication" /></b><bean:write property="applicationReport.memberId" name="rsForm"/></div></td>
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
				  <%=date1%></div></td>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="nameOfSsi" /></b><bean:write property="applicationReport.ssiName" name="rsForm"/></div></td>
                </tr>
				<tr>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="addresses" /></b><bean:write property="applicationReport.address" name="rsForm"/>
				  <br>&nbsp;<bean:write property="applicationReport.city" name="rsForm"/> 
				  </br></div></td>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="district" /></b><bean:write property="applicationReport.district" name="rsForm"/></div></td>
                </tr>
				<tr>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="state" /></b><bean:write property="applicationReport.state" name="rsForm"/></div></td>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="unitType" /></b>	<bean:define id="unitType" name="rsForm" property="applicationReport" type="com.cgtsi.reports.ApplicationReport"/>
				  <%String type = unitType.getUnitType();
				  if(type != null)
				  {
	  				   type = type.toUpperCase();
				  }
				  else
				  {
					   type = ""; 
				  }
				  %>
				  <%=type%></div></td>
                </tr>
                <tr>
                   <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="CreditFacilitySanctioned" /></b> <bean:write property="applicationReport.loanType" name="rsForm"/></div></td>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="bankReference" /></b><bean:write property="applicationReport.referenceNumber" name="rsForm"/></div></td>
                </tr>
                <tr>
           <td class="tableData"> <div align="left">&nbsp; <b>Application Expiry Date :</b>
				  <bean:define id="expiryDate" name="rsForm" property="applicationReport" type="com.cgtsi.reports.ApplicationReport"/>
				  <%String date10 = null;
				  java.util.Date newexpiredOn = expiryDate.getExpiryDate();
          System.out.println(newexpiredOn);
				  if(newexpiredOn != null)
				  {
	  				   date10 = dateFormat.format(newexpiredOn);
				  }
				  else
				  {
					   date10 = ""; 
				  }
				  %>
				  <%=date10%>
       
				  <td class="tableData"> <div align="left">&nbsp;<b></div></td>
                </tr>
				<tr>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="ChiefPromoter" /></b><bean:write property="applicationReport.chiefPromoter" name="rsForm"/></div></td>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="itpanNo" /></b><bean:write property="applicationReport.itpan" name="rsForm"/></div></td>
                </tr>
				<tr>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="Gender" /></b><bean:write property="applicationReport.gender" name="rsForm"/></div></td>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="OtherPromoters" /></b><bean:define id="others" name="rsForm" property="applicationReport" type="com.cgtsi.reports.ApplicationReport"/>
				  <% String othersFinal = others.getOthers();
				  if(othersFinal.equals(",,"))
				  {
				  %>
	  			   <%=""%>
				   <%
				  }
				  else
				  {
				  %>
	  			   <%=othersFinal%>
				   <%
				  }
				  %>
				</div></td>
               </tr>
				<tr>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="SSIRegnNo" /></b> <bean:write property="applicationReport.registrationNumber" name="rsForm"/></div></td>
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
				  <%=date2%></div></td>
				</tr>
        <tr>
        <td class="tableData"><b>&nbsp;<bean:message key="activitytype" /></b>:&nbsp;&nbsp;<bean:write property="applicationReport.typeOfActivity" name="rsForm"/></div></td>
        <td class="tableData">&nbsp;</td>
        </tr>
				<tr>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="IndustryType" /></b><bean:write property="applicationReport.industryType" name="rsForm"/></div></td>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="employees" /></b><bean:write property="applicationReport.employees" name="rsForm"/></div></td>
                </tr>
				<tr>
                  <td class="tableData"> <div align="left">&nbsp; <b>
				  <bean:message key="Turnover" /></b>
				  <bean:define id="turnover" property="applicationReport" name="rsForm" type="com.cgtsi.reports.ApplicationReport"/>
				  <%double turnoverValue = turnover.getTurnover();%>
				  <%=decimalFormat.format(turnoverValue)%>
				 </div></td>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="Exports" /></b><bean:write property="applicationReport.export" name="rsForm"/></div></td>
                </tr>
				<tr>
				 <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="Remarks" /></b>	 
				  <bean:define id="remarks" name="rsForm" property="statusDetails" type="com.cgtsi.reports.ApplicationReport"/>
				  <% String remarksFinal = remarks.getRemarks();
				  if((remarksFinal != null) && (!remarksFinal.equals("")))
				  {
				  %>
	  			   <%=remarksFinal%>
				   <%
				  }
				  else
				  {
				  %>
	  			   <%=""%>
				   <%
				  }
				  %>
				</div></td>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="Outstanding" /></b><bean:write property="applicationReport.outstanding" name="rsForm"/></div></td>
                </tr>
				<tr>
				<td class="tableData"> <div align="left">&nbsp; <b><bean:message key="Status" /></b><bean:write property="applicationReport.status" name="rsForm"/></div></td>
				<td class="tableData"> <div align="left">&nbsp;  <b><bean:message key="industrySector" /></b><bean:write property="applicationReport.industrySector" name="rsForm"/></div></td>
				</tr>
                                 <tr>
				<td class="tableData"> <div align="left">&nbsp; <b>HandiCrafts : </b><bean:write property="applicationReport.handiCraftsAccFlag" name="rsForm"/></div></td>
				<td class="tableData"> <div align="left">&nbsp; <b>DC Handicrafts : </b><bean:write property="applicationReport.dcHandicraftsReimb" name="rsForm"/></div></td>
				</tr>
                                 <tr>
				<td class="tableData"> <div align="left">&nbsp; <b> I Card Number : </b><bean:write property="applicationReport.appDcIcardNo" name="rsForm"/></div></td>
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
				  <%=IcardIssueDate%></div>
                                
                                
                                
                               </td>
				</tr>
				<tr>
				<td class="tableData"> <div align="left">&nbsp; <b><bean:message key="borrowerId" /></b><bean:write property="applicationReport.bid" name="rsForm"/></div></td>
				<td class="tableData"> <div align="left">&nbsp; <b><bean:message key="projectOutlayRs" /></b>
				  <bean:define id="projectOutlay" property="applicationReport" name="rsForm" type="com.cgtsi.reports.ApplicationReport"/>
				  <%double projectCost = projectOutlay.getProjectOutlay();%>
				  <%=decimalFormat.format(projectCost)%>
				  </div></td>
				</tr>
				<tr>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="TCSanctionedAmount" /></b>
				  <bean:define id="tcSanctioned" property="applicationReport" name="rsForm" type="com.cgtsi.reports.ApplicationReport"/>
				  <%double tcAmount = tcSanctioned.getTcSanctioned();%>
				  <%=decimalFormat.format(tcAmount)%>
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
				  <%=date3%>
				 </tr>
				<tr>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="TCPlr" /></b><bean:define id="tcPlr" name="rsForm" property="applicationReport" type="com.cgtsi.reports.ApplicationReport"/>
				  <%double newTcPlr =  tcPlr.getTcPlr();
				  String newTcPlr1 = newTcPlr+"% p.a.";
				  %>
				  <%=newTcPlr1%></div></td>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="TCRate" /></b><bean:define id="tcRate" name="rsForm" property="applicationReport" type="com.cgtsi.reports.ApplicationReport"/>
				  <%double newTcRate =  tcRate.getTcRate();
				  String newTcRate1 = newTcRate+"% p.a.";
				  %>
				  <%=newTcRate1%></div></td>
                </tr>
				<tr>
                <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="TCPromoterContribution" /></b><bean:write property="applicationReport.tcPromoterContribution" name="rsForm"/></div></td>
				<td class="tableData"> <div align="left">&nbsp; <b><bean:message key="TCSubsidy" /></b><bean:write 	property="applicationReport.tcSubsidy" name="rsForm"/></div></td>
				</TR>
				<tr>
                <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="TCEquity" /></b><bean:write property="applicationReport.tcEquity" name="rsForm"/></div></td>
				    <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="TCOutstanding" /></b><bean:write property="applicationReport.tcOutstanding" name="rsForm"/></div></td>
				</TR>
					<tr>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="moratoriumPeriod" /></b><bean:write property="applicationReport.repaymentMoratorium" name="rsForm"/></div></td>
               	  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="Periodicity" /></b><bean:write property="applicationReport.repaymentPeriodicity" name="rsForm"/></div></td>
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
				  <%=date4%>
				</div></td>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="NoOfInstallments" /></b><bean:write property="applicationReport.numberOfInstallments" name="rsForm"/></div></td>
				  <tr>

                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="DisbursementAmount" /></b> <bean:write property="applicationReport.disbursementAmount" name="rsForm"/></div></td>
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
				  <%=date5%>
				  </div></td>
                </tr>
				<tr>

                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="FinalDisbursement" /></b><bean:write property="applicationReport.finalDisbursement" name="rsForm"/></div></td>
				  <td class="tableData"><div align="left">&nbsp;<b><bean:message key="tcTenure" /></b>:&nbsp;&nbsp<bean:write property="applicationReport.tcTenure" name="rsForm"/></div></td>
          
                </tr> 
				<tr>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="WCFBLimitSanctioned" /></b>
				  <bean:define id="wcFbSanctioned" property="applicationReport" name="rsForm" type="com.cgtsi.reports.ApplicationReport"/>
				  <%double wcFbAmount = wcFbSanctioned.getWcFbSanctioned();%>
				  <%=decimalFormat.format(wcFbAmount)%>
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
				  <%=date6%>
				</tr>
				<tr>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="WCFBPrincipalOutstanding" /></b><bean:write property="applicationReport.wcFbPrincipalOutstanding" name="rsForm"/></div></td>
                    <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="WCNFBPrincipalOutstanding" /></b><bean:write property="applicationReport.wcNfbPrincipalOutstanding" name="rsForm"/></div></td>
                </tr>
				<tr>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="WCNFBLimitSanctioned" /></b><bean:write property="applicationReport.wcNfbSanctioned" name="rsForm"/></div></td>
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
				  <%=date7%>
				</tr>

				<tr>
				 <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="WCPlr" /></b><bean:define id="wcPlr" name="rsForm" property="applicationReport" type="com.cgtsi.reports.ApplicationReport"/>
				  <%double newWcPlr =  wcPlr.getWcPlr();
				  String newWcPlr1 = newWcPlr+"% p.a.";
				  %>
				  <%=newWcPlr1%></div></td>
                 <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="WCInterest" /></b><bean:define id="wcInterest" name="rsForm" property="applicationReport" type="com.cgtsi.reports.ApplicationReport"/>
				  <%double newWcInterest =  wcInterest.getWcInterest();
				  String newWcInterest1 = newWcInterest+"% p.a.";
				  %>
				  <%=newWcInterest1%></div></td>
                </tr>
				<tr>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="WCPromoterContribution" /></b><bean:write property="applicationReport.wcPromoterContribution" name="rsForm"/></div></td>
                    <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="WCSubsidy" /></b><bean:write property="applicationReport.wcSubsidy" name="rsForm"/></div></td>
                </tr>
				<tr>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="WCEquity" /></b><bean:write property="applicationReport.wcEquity" name="rsForm"/></div></td>
			  				   <td class="tableData"> <div align="left">&nbsp;<b><bean:message key="workingCapitalTenure" /></b>:&nbsp;&nbsp;<bean:write property="applicationReport.wcTenure" name="rsForm"/></div></td>
         
                </tr>
                <tr>
                  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="revivalStatus" /></b>&nbsp;&nbsp;<bean:write property="applicationReport.revivalStatus" name="rsForm"/></div></td>
				  <td class="tableData"> <div align="left">&nbsp; <b><bean:message key="revivalDate" /></b><bean:define id ="revivaleDate" property="applicationReport" type="com.cgtsi.reports.ApplicationReport" name="rsForm"/>
				  <%String revivalDate=null; 
				 java.util.Date newRevivalDate = revivaleDate.getRevivalDate();
				  if(newRevivalDate != null)
				  {
	  				   revivalDate = dateFormat.format(newRevivalDate);
				  }
				  else
				  {
					 revivalDate = "";
				  }
				  %>
				  <%=revivalDate%></div>
				  
				  
				  </td>
                </tr>

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
									<A href="javascript:submitForm('applicationReportDetails.do?method=applicationReportDetails')">
									<IMG src="images/Back.gif" alt="Back" width="49" height="37" border="0"></A>
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

