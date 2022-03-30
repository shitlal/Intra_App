<%@ page language="java"%>
<%@ page import="java.util.ArrayList"%>
<%@page import ="java.text.SimpleDateFormat"%>
<%@ page import="com.cgtsi.claim.ClaimConstants"%>
<%@ page import="com.cgtsi.actionform.ClaimActionForm"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic" %>
<%@ include file="/jsp/SetMenuInfo.jsp" %>
<%SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");%>
<% 
  session.setAttribute("CurrentPage","displayListOfClaimRefNumbers.do?method=displayListOfClaimRefNumbers");
%>
  
<%
String statusFlag = null;
ClaimActionForm claimForm = (ClaimActionForm)session.getAttribute("cpTcDetailsForm") ;
statusFlag = (String)claimForm.getStatusFlag();
// System.out.println("Printing Status Flag :" + statusFlag);
%>

<TABLE width="925" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form action="applicationStatusWiseReportDetails.do?method=applicationStatusWiseReportDetails" method="POST" enctype="multipart/form-data">
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
									<TD colspan="12"> 
										<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
											<tr>
                          <td colspan="8" class="Heading1" align="center"><u><bean:message key="reportHeader"/></u></td>
                      </tr>
                      <tr> <td colspan="8">&nbsp;</td></tr>
                      <TR>
												<TD width="18%" class="Heading"><bean:message key="statusWiseHeader" /></TD>
												<td class="Heading" width="40%">(<bean:write name="radioValue"/>)&nbsp;<bean:message key="from"/> <bean:write  name="cpTcDetailsForm" property="fromDate"/>&nbsp;<bean:message key="to"/> <bean:write  name="cpTcDetailsForm" property="toDate"/></td>
                        <TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
											</TR>
											<TR>
												<TD colspan="3" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
											</TR>

										</TABLE>
									</TD>

									<TR>
                  <TD width="3%" align="left" valign="top" class="ColumnBackground"><bean:message key="sNo"/></TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<bean:message key="bank"/>
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<bean:message key="memberId"/>
									</TD>
                  <TD width="10%" align="left" valign="top" class="ColumnBackground">
										<bean:message key="unitName"/>
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<bean:message key="clmrefnumber"/>
									</TD>
									<logic:equal property="statusFlag" value="AP" name="cpTcDetailsForm">
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										CGCLAN
									</TD>
                  <TD width="10%" align="left" valign="top" class="ColumnBackground">
										Claim Approved Amt.
									</TD>
                  <TD width="10%" align="left" valign="top" class="ColumnBackground">
										Claim Approved Dt
									</TD>
                  </logic:equal>
                  <logic:equal property="statusFlag" value="FW" name="cpTcDetailsForm">
                  	<TD width="10%" align="left" valign="top" class="ColumnBackground">
										Claim Forwarded Dt
									</TD>
                  </logic:equal>
                   <logic:equal property="statusFlag" value="RE" name="cpTcDetailsForm">
                  	<TD width="10%" align="left" valign="top" class="ColumnBackground">
										Claim Rejected Dt
									</TD>
                  </logic:equal>
                   <logic:equal property="statusFlag" value="NE" name="cpTcDetailsForm">
                  	<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<bean:message key="applicationDate"/>
									</TD>
                  </logic:equal>
									<logic:equal property="statusFlag" value="HO" name="cpTcDetailsForm">
                  	<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<bean:message key="applicationDate"/>
									</TD>
                  </logic:equal>
                  <logic:equal property="statusFlag" value="TC" name="cpTcDetailsForm">
                  	<TD width="10%" align="left" valign="top" class="ColumnBackground">
										Temporary Closure Dt
									</TD>
                  </logic:equal>
                  <logic:equal property="statusFlag" value="TR" name="cpTcDetailsForm">
                  	<TD width="10%" align="left" valign="top" class="ColumnBackground">
										Temporary Reject Dt
									</TD>                  
                  </logic:equal>
                  <logic:equal property="statusFlag" value="WD" name="cpTcDetailsForm">
                  	<TD width="10%" align="left" valign="top" class="ColumnBackground">
										Claim Withdrawn Dt.
									</TD>                  
                  </logic:equal>
				  <logic:equal property="statusFlag" value="RR" name="cpTcDetailsForm">
                                    <TD width="10%" align="left" valign="top" class="ColumnBackground">
										Claim Reply Received Dt.
									</TD>                  
                                </logic:equal>
                                <logic:equal property="statusFlag" value="RT" name="cpTcDetailsForm">
                                    <TD width="10%" align="left" valign="top" class="ColumnBackground">
										Claim Returned Dt.
									</TD>  
                                                                        <TD width="15%" align="left" valign="top" class="ColumnBackground">
										Claim Returned Remark
									</TD>
                                </logic:equal>
				</TR>	
					
											
                                                                <logic:iterate name="cpTcDetailsForm" id="object" property="listOfClmRefNumbers" indexId="index">
											<%
											com.cgtsi.claim.ClaimDetail details = (com.cgtsi.claim.ClaimDetail)object;
											%>
													
											<TR>
                                                                                        <TD width="3%" align="left" valign="top" class="ColumnBackground1">
                                                                                                <%=Integer.parseInt(index+"")+1%>
                                                                                        </TD>
												<TD width="10%" align="left" valign="top" class="ColumnBackground1">	
												<%=details.getMliName()%>				
												</TD>
												<TD width="10%" align="left" valign="top" class="ColumnBackground1">					
												<%=details.getMliId()%>				
												</TD>
                                                                                                <TD width="10%" align="left" valign="top" class="ColumnBackground1">					
												<%=details.getSsiUnitName()%>				
												</TD>
																	
												<%String reference=details.getClaimRefNum(); %>
												<% if("RRR".equals(statusFlag) || "WDD".equals(statusFlag)){%>
												<TD width="10%" align="left" valign="top" class="ColumnBackground1">
													<%=reference%>
												</TD> 
												<%}else{%>
												<TD width="10%" align="left" valign="top" class="ColumnBackground1">					
												<%
												String url = "displayClmRefNumberDtl.do?method=displayClmRefNumberDtl&"+ ClaimConstants.CLM_CLAIM_REF_NUMBER+"=" + reference; 	
												
												%>				
												<html:link href="<%=url%>"><%=reference%></html:link>
												</TD>
												<%}%>
												
												
												<%
												String cgclan = (String)details.getCGCLAN();
                                                                                                double claimApproved = (double)details.getEligibleClaimAmt();	%>
												<logic:equal property="statusFlag" value="AP" name="cpTcDetailsForm">
												<TD width="10%" align="left" valign="top" class="ColumnBackground1">	
												<%=cgclan%>				
												</TD>
                                                                                                <TD width="10%" align="left" valign="top" class="ColumnBackground1">	
												<%=claimApproved%>				
												</TD>
                                                                                                
                                                                                                
                                                                                                
                                                                                               
                                                                                               
                                                                                            </logic:equal>
                                                                                    <TD width="10%" align="left" valign="top" class="ColumnBackground1">						
											<%   java.util.Date utilDate=details.getClmSubmittedDt();
													String formatedDate = null;
													if(utilDate != null)
													{
														 formatedDate=dateFormat.format(utilDate);
													}
													else
													{
														 formatedDate = "";
													}
											%>
											<%=formatedDate%>
											</TD>
                                                                                        <logic:equal property="statusFlag" value="RT" name="cpTcDetailsForm">
											<TD width="15%" align="left" valign="top" class="ColumnBackground1">
                                                                                                <%=(String)details.getReturnRemarks()%>	
                                                                                                </td>
                                                                                                </logic:equal>
											</TR>
                                                                                        
											</logic:iterate>
   
							</TABLE>
						</TD>
					</TR>
					<TR >
						<TD height="20" >
							&nbsp;
						</TD>
					</TR>
					<TR >
						<TD align="center" valign="baseline">
							<DIV align="center">
							<A href="javascript:submitForm('showFilterForClaimDetails.do?method=showFilterForClaimDetails')">
									<IMG src="images/Back.gif" alt="OK" width="49" height="37" border="0"></A>
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

