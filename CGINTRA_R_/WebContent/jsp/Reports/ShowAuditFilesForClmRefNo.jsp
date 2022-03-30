<%@ page language="java"%>
<%@ page import="com.cgtsi.claim.ClaimConstants"%>
<%@ page import="java.util.*"%>
<%@ page import="com.cgtsi.actionform.ClaimActionForm"%>

<%@ page import="java.text.SimpleDateFormat"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic"%>
<%@ include file="/jsp/SetMenuInfo.jsp"%>
<%
	session.setAttribute("CurrentPage","proceedFromRecoveryFilterPage.do?method=proceedFromRecoveryFilterPage");
%>
<%
	ClaimActionForm claimForm = (ClaimActionForm) session.getAttribute("cpTcDetailsForm");
	String npaClassifiedDt = null;
	String npaReportingDt = null;
	String reasonForTurningNPA = null;
	String cgpan = null;
	String hiddencgpan = null;
	String dsbrsmntdt = null;
	String principal = null;
	String interestCharges = null;
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
	java.util.Date subsidyDt = null;
	double subsidyAmt = 0.0;
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
	String tcClaimFlag;
	String wcClaimFlag;
	double totDisbAmt = 0.0;	
%>

<html:errors />
 
<html:form action="addFirstClaimsPageDetails.do?method=addFirstClaimsPageDetails" method="POST" enctype="multipart/form-data">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr><td class="FontStyle">&nbsp;</td></tr>
	</table>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td width="20" align="right" valign="bottom"><img src="images/TableLeftTop.gif" alt="" width="20" height="31"></td>
			<td width="248" background="images/TableBackground1.gif"><img src="images/ClaimsProcessingHeading.gif" alt="" width="131" height="25"></td>
			<td align="right" valign="top" background="images/TableBackground1.gif">
				<div align="right"></div>
			</td>
			<td width="23" align="left" valign="bottom"><img src="images/TableRightTop.gif" width="23" alt="" height="31"></td>
		</tr>
		<tr>
			<td width="20" background="images/TableVerticalLeftBG.gif">&nbsp;</td>
			<td colspan="2">
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td>
							<table width="100%" border="0" cellspacing="1" cellpadding="0">
							</table>
						</td>
					</tr>
					<%					
						java.util.Map map = null;
						java.util.HashMap[] maparr = new java.util.HashMap[5];
						double totalAppAmt = 0.0;
						int tc_cgpans = claimForm.getTcCounter();
						int wc_cgpans = claimForm.getWcCounter();
						
						int total_cgpans = tc_cgpans + wc_cgpans;
						java.util.Vector cgpans = (java.util.Vector) claimForm.getCgpansVector();
						String appAmtStr = claimForm.getAppAmount();
						if (appAmtStr != null)
						{
							totalAppAmt = Double.parseDouble(appAmtStr);
						}
						java.util.ArrayList singlefiles = (java.util.ArrayList)request.getAttribute("SINGLECLAIMFILES"); 
						java.util.ArrayList multiplefiles = (java.util.ArrayList)request.getAttribute("MULTIPLECLAIMFILES");
						java.util.ArrayList legalFiles = (java.util.ArrayList)request.getAttribute("LEAGEFILE");//V						
						//System.out.println("JAP singlefiles : "+singlefiles.size() +"\t multiplefiles : "+multiplefiles.size());
						ArrayList tcwcFiles = (ArrayList)request.getAttribute("TCWCFILES");
						ArrayList tcwcamounts = (ArrayList)request.getAttribute("TCWCAMOUNTS");
						//System.out.println("JSP tcwcFiles : "+tcwcFiles.size() +"\t tcwcamounts : "+tcwcamounts.size());						
						
						if(singlefiles != null)//v
						{
							if(singlefiles.size() != 0)
							{
								map = (java.util.Map)singlefiles.get(0);								
								//map = (java.util.Map)singlefiles.get(1);
								//map = (java.util.Map)singlefiles.get(2);
								//map = (java.util.Map)singlefiles.get(3);
								//map = (java.util.Map)singlefiles.get(4);
							}
							else
							{
								map = null;
							}
						}//v
						//java.util.Map map = (java.util.Map)singlefiles.get(0);
						int total_cgpans2 = 0;
						if(tcwcFiles !=null)//v
						{
							if(tcwcFiles.size() != 0)
							{
								total_cgpans2 = tcwcFiles.size();
								//out.println(total_cgpans2);
							}
							else
							{
								total_cgpans2 = 0;
							}
						}
						
						/*if(multiplefiles != null)//v
						{
							if(multiplefiles.size() != 0)
							{
								maparr = (java.util.HashMap[])multiplefiles.get(0);								
								maparr = (java.util.HashMap[])multiplefiles.get(1);
								maparr = (java.util.HashMap[])multiplefiles.get(2);
								maparr = (java.util.HashMap[])multiplefiles.get(3);
								maparr = (java.util.HashMap[])multiplefiles.get(4);
							}
							else
							{
								maparr = null;								
							}
						}*/ //v						
					%>
					<tr>
					<TD><!-- new table -->
					<table width="100%">
						<tr>
							<td class="TableData" width="2%"><b>1.</b></td>
							<th class="TableData">Screen shot of NPA date<font
								color="red">*</font></th>
							<td class="HeadingBg">
							<%if(map != null){ %> <a href="<%=map.get("FILEPATH") %>"><%=map.get("FILENAME") %></a>
							<%}else{ %> No File <%} %>
							</td>
						</tr>
						<% 
							if(multiplefiles != null)
							{
								if(multiplefiles.size() != 0)
								{
									maparr = (java.util.HashMap[])multiplefiles.get(0);
								}
								else
								{
									maparr = null;
								}
							}
							//java.util.HashMap[] maparr = (java.util.HashMap[])multiplefiles.get(0); 
						%>
						<tr>
							<td class="TableData"><b>2.</b></td>
							<th class="TableData">Due diligence report at the time of
							appraisal explaining antecedents of the borrower<font color="red">*</font></th>
							<td class="HeadingBg">
							<div align="left">
							<%
								if(maparr != null)
								{
									if(maparr.length != 0)
									{
							%> 
										<a href="<%=maparr[0].get("FILEPATH") %>"><%=maparr[0].get("FILENAME") %></a>
									<%
									}
									else
									{ 
									%> 
									No File 
									<%
									}
								}
									%>
							</div>

							<div align="left" id="dili1">
							<%
								if(maparr != null)
								{
									if(maparr.length != 0)
									{
										if(maparr[1] != null)
										{
							%> 
											<a href="<%=maparr[1].get("FILEPATH") %>"><%=maparr[1].get("FILENAME") %></a>
										<%}
										else
										{ 
										%> 
										No File 
										<%} 
									}
								}%>
							</div>
							<div align="left" id="dili2">
							<%
								if(maparr != null)
								{
									if(maparr.length != 0)
									{
										if(maparr[2] != null)
										{
							%>
											<a href="<%=maparr[2].get("FILEPATH") %>"><%=maparr[2].get("FILENAME") %></a>
										<%}
										else
										{ 
										%> 
										No File 
										<%} 
									}
								}%>
							</div>

							</td>
						</tr>
						<% 
							if(multiplefiles != null)
							{
								if(multiplefiles.size() != 0)
								{
									maparr = (java.util.HashMap[])multiplefiles.get(1);
								}
								else
								{
									maparr = null;
								}
							}
						//maparr = (java.util.HashMap[])multiplefiles.get(1); 
						%>
						<tr>
							<td class="TableData"><b>3.</b></td>
							<th class="TableData">Post disbursement visit/ inspection
							report <font color="red">(Scanned Copies to be furnished)*</font></th>
							<td class="HeadingBg">
							<div align="left">
							<%
								if(maparr != null)
								{
									if(maparr.length != 0)
									{
										if(maparr[0] != null)
										{
							%>		
											<a href="<%=maparr[0].get("FILEPATH") %>"><%=maparr[0].get("FILENAME") %></a>
										<%}
										else
										{ 
										%>
										No File 
										<%} 
									}
								}%>
							</div>

							<div align="left" id="dili1">
							<%
								if(maparr != null)
								{
									if(maparr.length != 0)
									{
										if(maparr[1] != null)
										{
							%>
											<a href="<%=maparr[1].get("FILEPATH") %>"><%=maparr[1].get("FILENAME") %></a>
										<%}
										else
										{ 
										%> 
										No File 
										<%} 
									}
								}%>
							</div>
							<div align="left" id="dili2">
							<%
								if(maparr != null)
								{
									if(maparr.length != 0)
									{
										if(maparr[2] != null)
										{
							%>
											<a href="<%=maparr[2].get("FILEPATH") %>"><%=maparr[2].get("FILENAME") %></a>
										<%}
										else
										{ 
										%> 
										No File 
										<%} 
									}
								}%>
							</div>
							</td>
						</tr>
						<% 
							if(multiplefiles != null)
							{
								if(multiplefiles.size() != 0)
								{
									maparr = (java.util.HashMap[])multiplefiles.get(2);
								}
								else
								{
									maparr = null;
								}
							}
							//maparr = (java.util.HashMap[])multiplefiles.get(2); 
						%>
						<tr>
							<td class="TableData"><b>4.</b></td>
							<th class="TableData">Post NPA visit / inspection reports <font
								color="red">(Scanned Copies to be furnished)*</font></th>
							<td class="HeadingBg">
							<div align="left">
							<%
								if(maparr != null)
								{
									if(maparr.length != 0)
									{
										if(maparr[0] != null)
										{
							%>
											<a href="<%=maparr[0].get("FILEPATH") %>"><%=maparr[0].get("FILENAME") %></a>
										<%}
										else
										{ 
										%>
										No File 
										<%} 
									}
								}%>
							</div>

							<div align="left" id="dili1">
							<%
								if(maparr != null)
								{
									if(maparr.length != 0)
									{
										if(maparr[1] != null)
										{
							%>
											<a href="<%=maparr[1].get("FILEPATH") %>"><%=maparr[1].get("FILENAME") %></a>
										<%}
										else
										{ 
										%>
										No File 
										<%} 
									}
								}%>
							</div>
							<div align="left" id="dili2">
							<%
								if(maparr != null)
								{
									if(maparr.length != 0)
									{
										if(maparr[2] != null)
										{
							%>
											<a href="<%=maparr[2].get("FILEPATH") %>"><%=maparr[2].get("FILENAME") %></a>
										<%}
										else
										{ 
										%>
										No File
										<%} 
									}
								}%>
							</div>

							</td>
						</tr>
						<%
							if(singlefiles != null)//v
							{
								if(singlefiles.size() != 0)
								{															
									map = (java.util.Map)singlefiles.get(1);								
								}
								else
								{
									map = null;
								}
							}
							//map = (java.util.Map)singlefiles.get(1);
						  %>
						<tr>
							<td class="TableData"><b>5.</b></td>
							<th class="TableData">Copy of suit filed <font color="red">(details
							indicating parties, suit no, amount claimed in the suit)*</font></th>
							<td class="HeadingBg">
							<%
								if(map != null)
								{
							%>
									<a href="<%=map.get("FILEPATH") %>"><%=map.get("FILENAME") %></a>
								<%}
								else
								{ 
								%>
								No File
								<%}	%>
							</td>
						</tr>
						<%
							if(singlefiles != null)
							{
								if(singlefiles.size() != 0)
								{
									map = (java.util.Map)singlefiles.get(2);
								}
								else
								{
									map = null;
								}
							}
						
							//map = (java.util.Map)singlefiles.get(2);
						 %>
						<tr>
							<td class="TableData"><b>6.</b></td>
							<th class="TableData">Copy of the final verdict of the suit
							filed cases, if any</th>
							<td class="HeadingBg">
							<%
								if(map != null)
								{ 
							%> 
										<a href="<%=map.get("FILEPATH") %>"><%=map.get("FILENAME") %></a>
								<%}
								else
								{ 
								%> 
								No File 
								<%} %>
							</td>
						</tr>
						<%
							if(multiplefiles != null)
							{
								if(multiplefiles.size() != 0)
								{
									maparr = (java.util.HashMap[])multiplefiles.get(3);
								}
								else
								{
									maparr = null;
								}
							}
							//maparr = (java.util.HashMap[])multiplefiles.get(3); 
						%>
						<tr>
							<td class="TableData"><b>7.</b></td>
							<th class="TableData">IT PAN, Voter ID copy of promoter /
							Proprietor (or any other KYC details)<font color="red">*</font></th>
							<td class="HeadingBg">
							<div align="left">
							<%
								if(maparr != null)
								{
									if(maparr.length != 0)
									{
										if(maparr[0] != null)
										{
							%>
											<a href="<%=maparr[0].get("FILEPATH") %>"><%=maparr[0].get("FILENAME") %></a>
										<%}
										else
										{ 
										%>
										No File
										<%} 
									}
								}%>
							</div>

							<div align="left" id="dili1">
							<%
								if(maparr != null)
								{
									if(maparr.length != 0)
									{
										if(maparr[1] != null)
										{
							%>
											<a href="<%=maparr[1].get("FILEPATH") %>"><%=maparr[1].get("FILENAME") %></a>
										<%}
										else
										{ 
										%>
										No File 
										<%} 
									}
								}%>
							</div>
							<div align="left" id="dili2">
							<%
								if(maparr != null)
								{
									if(maparr.length != 0)
									{
										if(maparr[2] != null)
										{
							%>
											<a href="<%=maparr[2].get("FILEPATH") %>"><%=maparr[2].get("FILENAME") %></a>
										<%}
										else
										{ 
										%>
										No File 
										<%} 
									}
								}%>
							</div>

							</td>
						</tr>
						<% 
							if(multiplefiles != null)
							{
								if(multiplefiles.size() != 0)
								{
									maparr = (java.util.HashMap[])multiplefiles.get(4);
								}
								else
								{
									maparr = null;
								}
							}
							//maparr = (java.util.HashMap[])multiplefiles.get(4); 
						%>
						<tr>
							<td class="TableData"><b>8.</b></td>
							<th class="TableData">Any other documents that can have
							bearing on the authenticity of the claim (i.e the projected
							balance sheet, P&L A/c, the income tax returns of the previous
							years)<font color="red">*</font></th>
							<td class="HeadingBg">
							<div align="left">
							<%
								if(maparr != null)
								{
									if(maparr.length != 0)
									{
										if(maparr[0] != null)
										{
							%>
											<a href="<%=maparr[0].get("FILEPATH") %>"><%=maparr[0].get("FILENAME") %></a>
										<%}
										else
										{ 
										%>
										No File
										<%} 
									}
								}%>
							</div>

							<div align="left" id="dili1">
							<%
								if(maparr != null)
								{
									if(maparr.length != 0)
									{
										if(maparr[1] != null)
										{
							%>
											<a href="<%=maparr[1].get("FILEPATH") %>"><%=maparr[1].get("FILENAME") %></a>
										<%}
										else
										{ 
										%>
										No File
										<%} 
									}
								}%>
							</div>
							<div align="left" id="dili2">
							<%
								if(maparr != null)
								{
									if(maparr.length != 0)
									{
										if(maparr[2] != null)
										{
							%>
											<a href="<%=maparr[2].get("FILEPATH") %>"><%=maparr[2].get("FILENAME") %></a>
										<%}
										else
										{ 
										%>
										No Fil
										<%} 
									}
								}%>
							</div>

							</td>
						</tr>
						<%
							if(singlefiles != null)
							{
								if(singlefiles.size() != 0)
								{
									map = (java.util.Map)singlefiles.get(3);
								}
								else
								{
									map = null;
								}
							}
							//map = (java.util.Map)singlefiles.get(3);
						%>
						<tr>
							<td class="TableData"><b>9.</b></td>
							<th class="TableData">Staff accountability report (in case
							of quick mortality case where NPA date is within one year from
							date of sanction/ renewal date)<font color="red">*</font></th>
							<td class="HeadingBg">
							<%
								if(map != null)
								{
							%> 
									<a href="<%=map.get("FILEPATH") %>"><%=map.get("FILENAME") %></a>
								<%}
								else
								{ 
								%>
								No File
								<%} %>
							</td>
						</tr>
					</table>
					<!-- new table -->
					<table width="100%">
						<tr>
							<th class="Heading"></th>
							<th class="Heading">
							<div align="center">CGPAN</div>
							</th>
							<% 
								Map[] maparr1 = null;
								for (int z = 0; z < total_cgpans2; z++) 
								{
									ArrayList list = (ArrayList)tcwcFiles.get(z);	
									maparr1 = (java.util.Map[])list.get(0);
									Map m = maparr1[0];
									String cg = (String)m.get("CGPAN");
									//out.println(maparr1.length);
							%>
							<td class="Heading">&nbsp;<%=cg %></td>
							<%
								}
							%>
						</tr>
						<TR>
							<td class="TableData"><b>10.</b></td>
							<th class="TableData">Statement of account of borrower unit
							since beginning (from date of disbursement) till date<font
								color="red">*</font></th>

							<%
								for (int z = 0; z < total_cgpans2; z++)
								{
									ArrayList list = (ArrayList)tcwcFiles.get(z);	
									maparr1 = (java.util.Map[])list.get(0);											
							%>
							<td class="HeadingBg">
							<div align="left">
							<%
								if(maparr1[1] != null)
								{ 
							%>
									<a href="<%=maparr1[1].get("FILEPATH") %>"><%=maparr1[1].get("FILENAME") %></a>
								<%}
								else
								{ 
								%>
								No File
								<%} %>
							</div>

							<div align="left" id="dili1">
							<%
								if(maparr1[2] != null)
								{ 
							%>
									<a href="<%=maparr1[2].get("FILEPATH") %>"><%=maparr1[2].get("FILENAME") %></a>
								<%}
								else
								{ 
								%>
								No File 
								<%} %>
							</div>
							<div align="left" id="dili2">
							<%
								if(maparr1[3] != null)
								{ 
							%>
									<a href="<%=maparr1[3].get("FILEPATH") %>"><%=maparr1[3].get("FILENAME") %></a>
								<%}
								else
								{ 
								%>
								No File 
								<%} %>
							</div>

							</td>
							<%
							}
							%>
						</tr>
						<TR>
							<td class="TableData"><b>11.</b></td>
							<th class="TableData">Copy of appraisal report prior to
							sanction<font color="red">*</font></th>

							<%
								for (int z = 0; z < total_cgpans2; z++)
								{
									ArrayList list = (ArrayList)tcwcFiles.get(z);	
									maparr1 = (java.util.Map[])list.get(1);											
							%>
							<td class="HeadingBg">
							<div align="left">
							<%
								if(maparr1[1] != null)
								{ 
							%>
									<a href="<%=maparr1[1].get("FILEPATH") %>"><%=maparr1[1].get("FILENAME") %></a>
								<%}
								else
								{ 
								%> 
								No File
								<%} %>
							</div>

							<div align="left" id="dili1">
							<%
								if(maparr1[2] != null)
								{ 
							%>
									<a href="<%=maparr1[2].get("FILEPATH") %>"><%=maparr1[2].get("FILENAME") %></a>
								<%}
								else
								{ 
								%>
								No File
								<%} %>
							</div>
							<div align="left" id="dili2">
							<%
								if(maparr1[3] != null)
								{ 
							%>
									<a href="<%=maparr1[3].get("FILEPATH") %>"><%=maparr1[3].get("FILENAME") %></a>
								<%}
								else
								{ 
								%> 
								No File 
								<%} %>
							</div>

							</td>
							<%
							}
							%>
						</tr>
						<TR>
							<td class="TableData"><b>12.</b></td>
							<th class="TableData">Copies of all sanction letters & all
							the amendments to the sanction letter<font color="red">*</font></th>

							<% 
								for (int z = 0; z < total_cgpans2; z++)
								{
									ArrayList list = (ArrayList)tcwcFiles.get(z);	
									maparr1 = (java.util.Map[])list.get(2);											
							%>
							<td class="HeadingBg">
							<div align="left">
							<%
								if(maparr1[1] != null)
								{ 
							%>
									<a href="<%=maparr1[1].get("FILEPATH") %>"><%=maparr1[1].get("FILENAME") %></a>
								<%}
								else
								{ 
								%>
								No File 
								<%} %>
							</div>

							<div align="left" id="dili1">
							<%
								if(maparr1[2] != null)
								{
							%>
									<a href="<%=maparr1[2].get("FILEPATH") %>"><%=maparr1[2].get("FILENAME") %></a>
								<%}
								else
								{
								%>
								No File
								<%} %>
							</div>
							<div align="left" id="dili2">
							<%
								if(maparr1[3] != null)
								{ 
							%>
									<a href="<%=maparr1[3].get("FILEPATH") %>"><%=maparr1[3].get("FILENAME") %></a>
								<%}
								else
								{ 
								%> 
								No File 
								<%} %>
							</div>

							</td>
							<%
							}
							%>
						</tr>
						<TR>
							<td class="TableData"><b>13.</b></td>
							<th class="TableData">Compliance report on all sanction
							terms & conditions<font color="red">*</font></th>

							<% 
								for (int z = 0; z < total_cgpans2; z++)
								{
									ArrayList list = (ArrayList)tcwcFiles.get(z);	
									maparr1 = (java.util.Map[])list.get(3);											
							%>
							<td class="HeadingBg">
							<div align="left">
							<%
								if(maparr1[1] != null)
								{ 
							%>
									<a href="<%=maparr1[1].get("FILEPATH") %>"><%=maparr1[1].get("FILENAME") %></a>
								<%}
								else
								{ 
								%> 
								No File 
								<%} %>
							</div>

							<div align="left" id="dili1">
							<%
								if(maparr1[2] != null)
								{ 
							%> 
									<a href="<%=maparr1[2].get("FILEPATH") %>"><%=maparr1[2].get("FILENAME") %></a>
								<%}
								else
								{ 
								%> 
								No File 
								<%} %>
							</div>
							<div align="left" id="dili2">
							<%
								if(maparr1[3] != null)
								{ 
							%> 
									<a href="<%=maparr1[3].get("FILEPATH") %>"><%=maparr1[3].get("FILENAME") %></a>
								<%}
								else
								{ 
								%> 
								No File 
								<%} %>
							</div>

							</td>
							<%
							}
							%>
						</tr>
						<TR>
							<td class="TableData"><b>14.</b></td>
							<th class="TableData" width="50%">Pre disbursement
							visit/inspection report <font color="red"><b>(Scanned
							Copies to be furnished)*</b></font></th>
							<% 
								for (int z = 0; z < total_cgpans2; z++)
								{
									ArrayList list = (ArrayList)tcwcFiles.get(z);	
									maparr1 = (java.util.Map[])list.get(4);											
							%>
							<td class="HeadingBg">
							<div align="left">
							<%
								if(maparr1[1] != null)
								{ 
							%>
									<a href="<%=maparr1[1].get("FILEPATH") %>"><%=maparr1[1].get("FILENAME") %></a>
								<%}
								else
								{ 
								%> 
								No File 
								<%} %>
							</div>

							<div align="left" id="dili1">
							<%
								if(maparr1[2] != null)
								{ 
							%> 
									<a href="<%=maparr1[2].get("FILEPATH") %>"><%=maparr1[2].get("FILENAME") %></a>
								<%}
								else
								{ 
								%> 
								No File 
								<%} %>
							</div>
							<div align="left" id="dili2">
							<%
								if(maparr1[3] != null)
								{ 
							%> 
									<a href="<%=maparr1[3].get("FILEPATH") %>"><%=maparr1[3].get("FILENAME") %></a>
								<%}
								else
								{ 
								%> 
								No File 
								<%} %>
							</div>

							</td>
							<%
							}
							%>
						</tr>
						<%
						ArrayList tcwclist = (ArrayList)request.getAttribute("TCWCAMOUNTS");
						
						%>
						<tr>
							<th class="Heading"></th>
							<th class="Heading">
							<div align="center">CGPAN</div>
							</th>
							<% 
								for (int z = 0; z < total_cgpans2; z++)
								{
									HashMap tcwcmap = (HashMap) tcwclist.get(z);
									String cg = "";
									cg = (String)tcwcmap.get("CGPAN");
							%>
							<td class="Heading">&nbsp;<%=cg %></td>
							<%
								}
							%>
						</tr>

						<tr>
							<th class="TableData" width="2%">15.</th>
							<th class="TableData" width="50%">Amount of repayment before
							NPA date<font color="red">*</font></th>

							<% for (int z = 0; z < total_cgpans2; z++)
							{
								HashMap tcwcmap = (HashMap) tcwclist.get(z);
								String cg = "";
								cg = (String)tcwcmap.get("CGPAN");
								String prepayamt = String.valueOf(tcwcmap.get("PREPAYAMT"));
								String irepayamt = String.valueOf(tcwcmap.get("IREPAYAMT"));								
							%>


							<td class="HeadingBg">&nbsp;
							<table cellspacing="1" border="0">
								<tr>
									<th class="TableData" style="text-align: center;">Principal</th>
									<th class="TableData" style="text-align: center;">Interest</th>
								</tr>
								<tr>

									<% 
										if(cg.endsWith("TC"))
										{ 
									%>
									<td class="HeadingBg"><%=prepayamt %></td>
									<td class="HeadingBg"><%=irepayamt %></td>
									<%}else { %>
									<td class="HeadingBg">NA</td>
									<td class="HeadingBg">NA</td>
									<%} %>

								</tr>
							</table>
							</td>
							<%
							}
							%>
						</tr>

						<tr>
							<th class="TableData" width="2%">16.</th>
							<th class="TableData" width="50%">Amount of recovery after
							NPA date<font color="red">*</font></th>

							<% 
								for (int z = 0; z < total_cgpans2; z++)
								{
									HashMap tcwcmap = (HashMap) tcwclist.get(z);
									String precoamt = String.valueOf(tcwcmap.get("PRECOAMT"));
									String irecoamt = String.valueOf(tcwcmap.get("IRECOAMT"));
							%>
							<td class="HeadingBg">&nbsp;
							<table cellspacing="1" border="0">
								<tr>
									<th class="TableData" style="text-align: center;">Principal</th>
									<th class="TableData" style="text-align: center;">Interest</th>
								</tr>
								<tr>
									<td class="HeadingBg"><%=precoamt %></td>
									<td class="HeadingBg"><%=irecoamt %></td>
								</tr>
							</table>
							</td>

							<%
							}
							%>
						</tr>
						<tr>
							<th class="TableData" width="2%">17.</th>
							<th class="TableData" width="50%">Rate of Interest charged
							during the currency/tenure of loan(in %)<font color="red">*</font></th>

							<% 
								for (int z = 0; z < total_cgpans2; z++)
								{	
									HashMap tcwcmap = (HashMap) tcwclist.get(z);
									String interest = String.valueOf(tcwcmap.get("INTEREST"));
							%>

							<td class="HeadingBg">&nbsp;<%=interest %></td>
							<%
							}
							%>
						</tr>
					</table>


					<!-- new table -->
					<table width="100%">
						<TR>
							<th class="TableData" width="1%">18.</th>
							<th class="TableData" width="60%">Indicate whichever is
							applicable <font color="red">*</font></th>
							<td class="HeadingBg" width="39%">&nbsp; <logic:equal
								value="P" name="cpTcDetailsForm" property="bankRateType">
								<bean:write name="cpTcDetailsForm" property="plr" />&nbsp;(%)
							</logic:equal> <logic:notEqual value="P" name="cpTcDetailsForm"
								property="bankRateType">
								<bean:write name="cpTcDetailsForm" property="rate" />&nbsp;(%)
							</logic:notEqual></td>
						</TR>
						<TR>
							<th class="TableData" width="1%">19.</th>
							<th class="TableData" width="60%">Insurance copy of primary
							assets, if available<font color="red">*</font></th>
							<td class="HeadingBg" width="39%">&nbsp;<bean:write
								name="cpTcDetailsForm" property="insuranceFileFlag" /></td>
						</TR>
					</table>
					<!-- new table -->
					<table width="100%">
						<tr>
							<th class="Heading"></th>
							<th class="Heading">
							<div align="center">CGPAN</div>
							</th>
							<% 
								for (int z = 0; z < total_cgpans; z++)
								{
									String cg2 = (String) cgpans.get(z);
							%>
							<td class="Heading">&nbsp;<%=cg2 %></td>
							<%
								}
							%>
						</tr>
						<tr>
							<td class="TableData" width="2%"><b>19.1.</b></td>
							<th class="TableData" width="50%">Please attach the
							insurance copy<font color="red">*</font></th>
							<% 
								for (int z = 0; z < total_cgpans2; z++)
								{
									ArrayList list = (ArrayList)tcwcFiles.get(z);	
									maparr1 = (java.util.Map[])list.get(5);											
							%>
							<td class="HeadingBg">
							<div align="left">
							<%
								if(maparr1[1] != null)
								{ 
							%>
									<a href="<%=maparr1[1].get("FILEPATH") %>"><%=maparr1[1].get("FILENAME") %></a>
								<%}
								else
								{ 
								%> 
								No File 
								<%} %>
							</div>

							<div align="left" id="dili1">
							<%
								if(maparr1[2] != null)
								{ 
							%> 
									<a href="<%=maparr1[2].get("FILEPATH") %>"><%=maparr1[2].get("FILENAME") %></a>
								<%}
								else
								{ 
								%> 
								No File 
								<%} %>
							</div>
							<div align="left" id="dili2">
							<%
								if(maparr1[3] != null)
								{ 
							%> 
									<a href="<%=maparr1[3].get("FILEPATH") %>"><%=maparr1[3].get("FILENAME") %></a>
								<%}
								else
								{ 
								%> 
								No File 
								<%} %>
							</div>

							</td>
							<%
							}
							%>
						</tr>
					</table>
					<!-- new table -->
					<table width="100%">
						<tr>
							<th class="TableData" width="3%">19.2.</th>
							<th class="TableData" width="58%">Provide reason for non
							availability of insurance copy<font color="red">*</font></th>
							<td class="HeadingBg" width="39%">&nbsp;<bean:write
								name="cpTcDetailsForm" property="insuranceReason" /></td>
						</tr>
						<tr>
							<th class="TableData" width="3%">20.</th>
							<th class="TableData" width="58%">Status of security with
							detailed explanation ( whether it is in the custody of bank or
							sold etc)<font color="red">*</font></th>
							<td class="HeadingBg" width="39%">&nbsp;<bean:write
								name="cpTcDetailsForm" property="securityRemarks" /></td>
						</tr>
						<tr>
							<th class="TableData" width="3%">21.</th>
							<th class="TableData" width="58%">Recovery efforts made by
							bank after NPA - brief note<font color="red">*</font></th>
							<td class="HeadingBg" width="39%">&nbsp;<bean:write
								name="cpTcDetailsForm" property="recoveryEffortsTaken" /></td>
						</tr>
						<tr>
							<th class="TableData" width="3%">22.</th>
							<th class="TableData" width="58%">Specify Internal rating
							assigned (if any) to the case (mandatory for cases above 50 lakh)<font
								color="red">*</font></th>
							<td class="HeadingBg" width="39%">&nbsp;<bean:write
								name="cpTcDetailsForm" property="rating" /></td>
						</tr>
						
						<%
							if(singlefiles != null)
							{
								if(singlefiles.size() != 0)
								{
									map = (java.util.Map)singlefiles.get(4);
								}
								else
								{
									map = null;
								}
							}
							//map = (java.util.Map)singlefiles.get(4);
						 %>
						<tr>
							<th class="TableData" width="3%">23.</th>
							<th class="TableData" width="58%">Attachment for Internal
							rating assigned (if any) to the case (mandatory for cases above
							50 lakh)<font color="red">*</font></th>
							<td class="HeadingBg" width="39%">
							<%
								if(map != null)
								{ 
							%> 
									<a href="<%=map.get("FILEPATH") %>"><%=map.get("FILENAME") %></a>
								<%}
								else
								{ 
								%> 
								No File 
								<%} %>
							</td>
						</tr>
						<tr>
							<th class="TableData" width="3%">24.</th>
							<th class="TableData" width="58%">Branch Address<font
								color="red">*</font></th>
							<td class="HeadingBg" width="39%">&nbsp;<bean:write
								name="cpTcDetailsForm" property="branchAddress" /></td>
						</tr>
						<tr>
							<th class="TableData" width="3%">25.</th>
							<th class="TableData" width="58%">Whether the above internal
							rating is of investment grade<font color="red">*</font></th>
							<td class="HeadingBg" width="39%">&nbsp; <bean:write
								name="cpTcDetailsForm" property="investmentGradeFlag" /></td>
						</tr>
						<tr>
							<th class="TableData" width="3%">26.</th>
							<th class="TableData" width="58%">Legal Doc</th>
							<td class="HeadingBg" width="39%">&nbsp;
								<%
									if(legalFiles != null)
									{
										if(legalFiles.size() != 0)
										{
											System.out.println("JSP legalFile.size : "+legalFiles.size());
											map = (java.util.Map)legalFiles.get(0); 
											System.out.println("JSP legalFile map :"+map.get("LEGFILEPATH"));
										}
										else
										{
											map = null;
										}
									}									
								%>	
								<%
									if(map != null)
									{
								%>
								<a href="<%=map.get("LEGFILEPATH") %>"><%=map.get("LEGFILENAME") %></a>
								<%  }else {%>
								No File.
								<%} %>
													
							</td>
						</tr>
					</table>

					</td>
				</tr>

			</table>
			</td>
			<td width="23" background="images/TableVerticalRightBG.gif">&nbsp;</td>
		</tr>


		<tr>
			<td width="20" align="right" valign="bottom"><img
				src="images/TableLeftBottom.gif" alt="" width="20" height="51"></td>
			<td colspan="2" valign="bottom"
				background="images/TableBackground3.gif">

			<div align="center"><A href="javascript:printpage()"> <IMG
				src="images/Print.gif" alt="Print" width="49" height="37" border="0"></A></div>
			</td>
			<td width="23" align="right" valign="bottom"><img
				src="images/TableRightBottom.gif" alt="" width="23" height="51"></td>
		</tr>
	</table>
</html:form>

