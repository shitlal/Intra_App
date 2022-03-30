<%@ page import="com.cgtsi.util.SessionConstants"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean"%>
	<!--start of WC-->		
									
							<table width="100%" border="0" cellspacing="1" cellpadding="0" colspan="5">
								 <TR>
									<TD class="SubHeading" width="843"><bean:message key="workingCapital" />
									</TD>						
								</TR>				
							
										
								<tr align="left"> 
									<td class="ColumnBackground" height="28">&nbsp;
										<bean:message key="interestType" />
									</td>
									<td class="TableData" colspan="2">													
									<%
									String appWCFlag=session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).toString();

										if((appWCFlag.equals("13"))||(appWCFlag.equals("12")))		

										{
										%>
										<html:radio name="appForm" value="T" property="wcInterestType" disabled="true">
										</html:radio>
									
										<bean:message key="fixedInterest" />&nbsp;
									
										<html:radio name="appForm" value="F" property="wcInterestType" disabled="true" ></html:radio>	
									
										<bean:message key="floatingInterest" />	
										<%}
										else
										{ %>
									
										<html:radio name="appForm" value="T" property="wcInterestType">
										</html:radio>
									
										<bean:message key="fixedInterest" />&nbsp;
									
										<html:radio name="appForm" value="F" property="wcInterestType" ></html:radio>	
									
										<bean:message key="floatingInterest" />	
									<%}%>
									
										&nbsp;<!--<html:text property="interestRate" size="5" alt="interestRate" name="appForm" maxlength="4" onkeypress="return decimalOnly(this, event)" onkeyup="isValidDecimal(this)"/>
										<bean:message key="inPer" />-->
									</td>
									<!--<td class="ColumnBackground" height="28" >&nbsp;
										<bean:message key="benchMarkPLR" />
									</td>
									<td class="TableData" height="28" width="160">&nbsp;
										
										<html:text property="benchMarkPLR" size="5" alt="benchMarkPLR" name="appForm" maxlength="3" onkeypress="return numbersOnly(this, event)" onkeyup="isValidNumber(this)"/>&nbsp;
										
										<bean:message key="inPer" />
									</td>
									<td class="ColumnBackground" height="28" >&nbsp;
										<bean:message key="benchMarkPLR" />
									</td>-->
									<td class="ColumnBackground" height="28" >&nbsp;
										<font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="typeOfPLR" />
									</td>
									<td class="TableData" height="28" colspan="8">&nbsp;
									<%
									if((appWCFlag.equals("13"))||(appWCFlag.equals("12")))		

										{
										%>
										<bean:write name="appForm" property="wcTypeOfPLR"/>
										<%}
										else
										{ %>
										
										<html:text property="wcTypeOfPLR" size="30" alt="wcTypeOfPLR" name="appForm" maxlength="50" />&nbsp;						
									<%}%>
									</td>
								</tr>
								<tr align="left">
									
									<td width="25%" class="ColumnBackground"> <div align="left">&nbsp;
										<font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="plr" /></div>
									</td>
									<td class="TableData" colspan="11">
										<div align="left"> 
										<%
										if((appWCFlag.equals("13"))||(appWCFlag.equals("12")))		

										{
										%>
										<bean:write name="appForm" property="wcPlr"/>
										<%}
										else
										{ %>
										
											<html:text property="wcPlr" size="5" alt="wcPlr" name="appForm" maxlength="6" onkeypress="return decimalOnly(this, event,3)" onkeyup="isValidDecimal(this)"/>&nbsp;
										<%}%>
											<bean:message key="inPa" />
										
										</div>
									</td>

								</tr>
								<tr align="left">
									<td class="ColumnBackground" rowspan="1" width="25%">
										<bean:message key="limitSanctioned" />
										&nbsp;&nbsp;&nbsp;
									</td>
									<td class="TableData" width="15%">
										&nbsp;<bean:message key="fundBasedLimitSanctioned" />
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									</TD>
									<TD class="TableData" id="fundBasedLimitSanctioned" width="5%">
										<bean:write name="appForm" property="wcFundBasedSanctioned"/>	
										&nbsp;&nbsp;
									</TD>
									<!--<TD class="TableData" >
										<bean:message key="limitFundBasedInterest"/>		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										
										<html:text property="limitFundBasedInterest" size="7" alt="limitFundBasedInterest" name="appForm" maxlength="5" onkeypress="return decimalOnly(this, event)" onkeyup="isValidDecimal(this)"/>
										
										&nbsp;&nbsp;&nbsp;&nbsp;
									</TD>-->
									<td  class="TableData"> <div align="left">
										<bean:message key="limitFundBasedInterest" /></div>
										<%
										if((appWCFlag.equals("13"))||(appWCFlag.equals("12")))		

										{
										%>
										<bean:write name="appForm" property="limitFundBasedInterest"/>
										<%}
										else
										{ %>
										
											<html:text property="limitFundBasedInterest" size="5" alt="limitFundBasedInterest" name="appForm" maxlength="5" onkeypress="return decimalOnly(this, event,2)" onkeyup="isValidDecimal(this)"/>&nbsp;
										<%}%>
											<bean:message key="inPa" />
																			
									</td>
										

									<TD class="TableData" colspan="8">
										<bean:message key="limitFundbasedSanctionedDate" />	&nbsp;
										<%
										if((appWCFlag.equals("13"))||(appWCFlag.equals("12")))		

										{
										%>
										<bean:write name="appForm" property="limitFundBasedSanctionedDate"/>
										<%}
										else
										{ %>
										
										<html:text property="limitFundBasedSanctionedDate" size="7" alt="limitFundbasedSanctionedDate" name="appForm" maxlength="10"/>
										<IMG src="images/CalendarIcon.gif" width="20" onClick="showCalendar('appForm.limitFundBasedSanctionedDate')" align="center">
										<%}%>
										
									</td>
								</tr>
								<tr>
								<td class="TableData" width="15%">
								</td>
								<td class="TableData" width="15%">
										<bean:message key="nonFundBasedLimitSantioned" />	
									</TD>
								<TD class="TableData" id="nonFundBasedLimitSantioned" width="10%">
									<bean:write name="appForm" property="wcNonFundBasedSanctioned"/>&nbsp;
										
									</TD>
									<TD class="TableData">		
										
										<bean:message key="limitNonFundBasedCommission" />		&nbsp;&nbsp

										<%
										if((appWCFlag.equals("13"))||(appWCFlag.equals("12")))		

										{
										%>
										<bean:write name="appForm" property="limitNonFundBasedCommission"/>
										<%}
										else
										{ %>
										
											<html:text property="limitNonFundBasedCommission" size="5" alt="limitFundBasedInterest" name="appForm" maxlength="5" onkeypress="return decimalOnly(this, event,2)" onkeyup="isValidDecimal(this)"/>&nbsp;
										<%}%>

										
									</TD>
									<TD class="TableData" colspan="8">
										<bean:message key="limitNonFundBasedSanctionedDate" />	&nbsp;
										
										<%
										if((appWCFlag.equals("13"))||(appWCFlag.equals("12")))		

										{
										%>
										<bean:write name="appForm" property="limitNonFundBasedSanctionedDate"/>
										<%}
										else
										{ %>
										
										<html:text property="limitNonFundBasedSanctionedDate" size="7" alt="limitFundbasedSanctionedDate" name="appForm" maxlength="10"/>
										<IMG src="images/CalendarIcon.gif" width="20" onClick="showCalendar('appForm.limitNonFundBasedSanctionedDate')" align="center">
										<%}%>

										
									</td>
								</tr>
								<tr align="left">
									<td class="ColumnBackground" rowspan="1">
										<bean:message key="creditguarateed" />
										&nbsp;&nbsp;&nbsp;
									</td>
									<td class="TableData" colspan="11">
										<bean:message key="creditFundBased" />						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										<%
										if((appWCFlag.equals("13"))||(appWCFlag.equals("12")))		

										{
										%>
										<bean:write name="appForm" property="creditFundBased"/>
										<%}
										else
										{ %>
										
										<html:text property="creditFundBased" size="10" alt="creditFundBased" name="appForm" maxlength="16" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)"/>&nbsp;
										<%}%>
										<bean:message key="inRs" />	
										
									</td>
								</tr>


								<tr align="left">
									<td class="TableData">
									</td>
									<td class="TableData" colspan="11">
										<bean:message key="creditNonFundBased" />
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

										<%
										if((appWCFlag.equals("13"))||(appWCFlag.equals("12")))		

										{
										%>
										<bean:write name="appForm" property="creditNonFundBased"/>
										<%}
										else
										{ %>
										
										<html:text property="creditNonFundBased" size="10" alt="creditFundBased" name="appForm" maxlength="16" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)"/>&nbsp;
										<%}%>

										<bean:message key="inRs" />	
										
									</td>
								</tr>

								 <!-- added by sukumar@path for capturing WC details -->
                   <%--           <tr> 
                                                <td class="ColumnBackground" height="28" width="25%">&nbsp;
                                                <bean:message key="amtDisbursed" />		
                                                </td>
                                                <td class="TableData" height="28" width="25%">
                                                <%									
                                                if((appWCFlag.equals("13"))||(appWCFlag.equals("12")))										
                                                {
                                                %><bean:write name="appForm" property="amtDisbursed"/>										
                                                <%}
                                                 else {
                                                 %><html:text property="amtDisbursed" size="20" alt="amtDisbursed" name="appForm" maxlength="16" onkeypress="return 
                                                decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)"/>									
                                                <%}%><bean:message key="inRs" />	
                                                </td>
                                                        <td class="ColumnBackground" height="28" colspan="11">										
                                                        <table border="0" cellpadding="0" 
                                                        cellspacing="0"  width="100%">
                                                        <tr align="left"><td class="ColumnBackground" height="28" width="269">&nbsp;							
                                                        <bean:message key="firstDisbursementDate"/>											
                                                        </td><td class="TableData" height="28" width="160" colspan="5">									
                                                        <%
                                                                                                                                                        
                                                        if((appWCFlag.equals("13"))||(appWCFlag.equals("12"))){
                                                        %><bean:write name="appForm" property="firstDisbursementDate"/><%}								
                                                        else { %>
                                                        <html:text property="firstDisbursementDate" size="10" alt="firstDisbursementDate" name="appForm" maxlength="10"/>
                                                        <IMG src="images/CalendarIcon.gif" width="20" onClick="showCalendar('appForm.firstDisbursementDate')" align="center">
                                                        <%}%>
                                                        </td>
                            </tr>
                                    <tr align="left"><td class="ColumnBackground" height="28" width="269" >&nbsp;							
                                    <%												
                                    if((appWCFlag.equals("13"))||(appWCFlag.equals("12")))										
                                    { %><bean:message key="lastDisbursementDate"/>											
                                    <% } else {%><bean:message key="finalDisbursementDate"/>									
                                    <%}%> </td> <td class="TableData" height="28" width="160">									
                                    <%	
                                    if((appWCFlag.equals("13"))||(appWCFlag.equals("12")))										
                                    {
                                    %><bean:write name="appForm" property="finalDisbursementDate"/>	<%}								
                                    else { %><html:text property="finalDisbursementDate" size="10" alt="finalDisbursementDate" name="appForm" maxlength="10"/>
                                    <IMG src="images/CalendarIcon.gif" width="20" onClick="showCalendar('appForm.finalDisbursementDate')" align="center">
                                    <%}%></td></tr></table></td></tr>
                                    --%>

								
								<!-- end part here for capturing disbursement details for WC 
-->

								
								<tr align="left">
									<td class="ColumnBackground" width="25%" colspan="12" height="25">&nbsp;
										<bean:message key="osFundBased" />
									</td>
								</tr>
								<tr align="left">
									<td class="TableData" colspan="12">
									  <div align="center">
										&nbsp;<bean:message key="osFundBasedPpl" />&nbsp;
										<%
										if((appWCFlag.equals("13"))||(appWCFlag.equals("12")) || (appWCFlag.equals("4")) || (appWCFlag.equals("6")))		

										{
										%>
										<bean:write name="appForm" property="osFundBasedPpl"/>
										<%}
										else
										{ %>
										
										<html:text property="osFundBasedPpl" size="5" alt="osFundBasedPpl" name="appForm" maxlength="16" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)"/>
										<%}%>
										<bean:message key="inRs" />	
										&nbsp;&nbsp;&nbsp;&nbsp;
										<!--<bean:message key="osFundBasedInterestAmt" />&nbsp;
									
										<html:text property="osFundBasedInterestAmt" size="5" alt="osFundBasedInterestAmt" name="appForm" maxlength="13" onkeypress="return numbersOnly(this, event)" onkeyup="isValidNumber(this)"/>
										<bean:message key="inRs" />-->
										
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										<bean:message key="osFundBasedAsOnDate" />&nbsp;&nbsp;&nbsp;
										<%
										if((appWCFlag.equals("13"))||(appWCFlag.equals("12")) || (appWCFlag.equals("4")) || (appWCFlag.equals("6")))		

										{
										%>
									<html:text property="osFundBasedAsOnDate" size="10" alt="osFundBasedAsOnDate" name="appForm" maxlength="10"/>
										<IMG src="images/CalendarIcon.gif" width="20" onClick="showCalendar('appForm.osFundBasedAsOnDate')" align="center">
										<%}
										else
										{ %>
										
										<html:text property="osFundBasedAsOnDate" size="10" alt="osFundBasedAsOnDate" name="appForm" maxlength="10"/>
										<IMG src="images/CalendarIcon.gif" width="20" onClick="showCalendar('appForm.osFundBasedAsOnDate')" align="center">
										<%}%>
										
										</div>
									</td>
								</tr>
								<tr align="left">
									<td class="ColumnBackground" width="25%" colspan="12" height="25">&nbsp;
										<bean:message key="osNonFundBased" />
									</td>
								</tr>
								<tr align="left">
									<td class="TableData" colspan="12">
									  <div align="center">
										<bean:message key="osNonFundBasedPpl" />&nbsp;

										<%
										if((appWCFlag.equals("13"))||(appWCFlag.equals("12")) || (appWCFlag.equals("4")) || (appWCFlag.equals("6")))		

										{
										%>
										<bean:write name="appForm" property="osNonFundBasedPpl"/>
										<%}
										else
										{ %>
										
										<html:text property="osNonFundBasedPpl" size="5" alt="osFundBasedPpl" name="appForm" maxlength="16" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)"/>
										<%}%>

										<bean:message key="inRs" />	
										
										&nbsp;&nbsp;&nbsp;&nbsp;
										<!--<bean:message key="osNonFundBasedCommissionAmt" />&nbsp;
										
										<html:text property="osNonFundBasedCommissionAmt" size="5" alt="osNonFundBasedCommissionAmt" name="appForm" maxlength="13" onkeypress="return numbersOnly(this, event)" onkeyup="isValidNumber(this)"/>-->

										<bean:message key="inRs" />
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										<bean:message key="osNonFundBasedAsOnDate" />&nbsp;&nbsp;&nbsp;

										<%
										if((appWCFlag.equals("13"))||(appWCFlag.equals("12")) || (appWCFlag.equals("4")) || (appWCFlag.equals("6")))		

										{
										%>
										<bean:write name="appForm" property="osNonFundBasedAsOnDate"/>
										<%}
										else
										{ %>
										
										<html:text property="osNonFundBasedAsOnDate" size="10" alt="osFundBasedAsOnDate" name="appForm" maxlength="10"/>
										<IMG src="images/CalendarIcon.gif" width="20" onClick="showCalendar('appForm.osNonFundBasedAsOnDate')" align="center">
										<%}%>

										
										</div>
									</td>
								</tr>
                                <!-- Added by Dkr for financial record -->                            
							  <!-- 	=============== FINANCIAL RECORDS =========================== -->
							  <%							    
								//if((loanType_d.equals("WC") || loanType_d.equals("BO")) && !loanType_d.equals("WCE") && !loanType_d.equals("WCR") && !loanType_d.equals("TCE"))		
                            //    {
						      %> 
					       		<TR>				       		
								  <TD colspan="8">
									<TABLE width="100%" id="financialOtherDtlLblId" >	
								   	<thead> <tr><th width="31%" class="Heading">Financial Record 1Cr-2Cr.</th>
								   	<th style="padding: 0px; width: 0px !important; float: left;"><img width="19" height="19" src="images/TriangleSubhead.gif"></th></tr></thead> 
								<tr align="left">
						          <TD align="left" valign="top" class="ColumnBackground"  colspan="3"><font color="#FF0000" size="2">*</font>
						          <bean:message	key="inCrilcCibilRbi" /></TD>
								<TD align="left" valign="top" class="tableData">
								<html:radio	name="appForm" value="Y" property="promDirDefaltFlg" styleId="promDirDefaltFlg_Y" />
										 <bean:message key="yes" />&nbsp;&nbsp;
										  <html:radio name="appForm" value="N" property="promDirDefaltFlg" styleId="promDirDefaltFlg_N" />
									<bean:message key="no" /></TD>	
						       </tr>     
					             <tr align="left">
										<td class="ColumnBackground" width="25%"><font color="#FF0000" size="2">*</font>
										<bean:message key="credBureName1" /></td>
										<td class="TableData" width="20%" align="left" valign="top">
											<% 	if (appWCFlag.equals("12") || appWCFlag.equals("5")
													|| appWCFlag.equals("13")) {
																	%> <bean:write property="credBureName1" name="appForm" /> <%
																		} else {
																	%> <html:text property="credBureName1" size="20"
												alt="credBureName1" name="appForm"/>
												 <%	} %> 
										</td>
										<td class="ColumnBackground" width="25%">&nbsp;<font color="#FF0000" size="2">*</font> <bean:message
												key="credBureScorKeyProm" />
										</td>
										<td class="TableData" width="20%" align="left" valign="top">
											<%
											if (appWCFlag.equals("12") || appWCFlag.equals("5")
													|| appWCFlag.equals("13")) {
																	%> <bean:write property="credBureKeyPromScor" name="appForm" />
																	 <%
																		} else {
																	%> <html:text property="credBureKeyPromScor" size="20"
												alt="credBureKeyPromScor" name="appForm" maxlength="3" onkeyup="isValidDecimal(this)" /> <bean:message key="3to9" /><%
																		}
																	%> 
										</td>
									</tr>						       						
									<tr align="left">
										<td class="ColumnBackground" width="25%"><font color="#FF0000" size="2"></font><bean:message
												key="credBureName2" /></td>
										<td class="TableData" width="20%" align="left" valign="top">
											<%
											if (appWCFlag.equals("12") || appWCFlag.equals("5")
													|| appWCFlag.equals("13")) {
																	%> <bean:write property="credBureName2"
												name="appForm" /> <%
																		} else {
																	%> <html:text property="credBureName2" size="20"
												alt="credBureName2" name="appForm" /> <%
																		}
																	%> 
										</td>
										<td class="ColumnBackground" width="25%">&nbsp;<font color="#FF0000" size="2"></font> <bean:message
												key="credBureScoreProm2" />
										</td>
										<td class="TableData" width="20%" align="left" valign="top">
											<%
											if (appWCFlag.equals("12") || appWCFlag.equals("5")
													|| appWCFlag.equals("13")) {
																	%> <bean:write property="credBurePromScor2"
												name="appForm" /> <%
																		} else {
																	%> <html:text property="credBurePromScor2" size="20"
												alt="credBurePromScor2" name="appForm" maxlength="3"
												onblur="javascript:calProjectOutlay()"
												onkeypress="return decimalOnly(this, event,13)"
												onkeyup="isValidDecimal(this)" /> <%
																		}
																	%> <bean:message key="3to9" />
										</td>
									</tr>								
									<tr align="left">
										<td class="ColumnBackground" width="25%"><font color="#FF0000" size="2"></font><bean:message
												key="credBureName3" /></td>
										<td class="TableData" width="20%" align="left" valign="top">
											<%	if (appWCFlag.equals("12") || appWCFlag.equals("5")
													|| appWCFlag.equals("13")) {
																	%> <bean:write property="credBureName3"
												name="appForm" /> <%
																		} else {
																	%> <html:text property="credBureName3" size="20"
												alt="credBureName3" name="appForm" /> <%
																		}
																	%> 
										</td>
										<td class="ColumnBackground" width="25%">&nbsp;<font color="#FF0000" size="2"></font> <bean:message
												key="credBureScoreProm3" />
										</td>
										<td class="TableData" width="20%" align="left" valign="top">
											<%
											if (appWCFlag.equals("12") || appWCFlag.equals("5")
													|| appWCFlag.equals("13")) {
											%> <bean:write property="credBurePromScor3"
												name="appForm" />
												<%
													} else {
												%> <html:text property="credBurePromScor3" size="20"
												alt="credBurePromScor3" name="appForm" maxlength="3"
												onblur="javascript:calProjectOutlay()"
												onkeypress="return decimalOnly(this, event,13)"
												onkeyup="isValidDecimal(this)" />
												 <%
													}
												 %> 
												 <bean:message key="3to9" />
										</td>
									</tr>							
									
									<tr align="left">
										<td class="ColumnBackground" width="25%"><font color="#FF0000" size="2"></font><bean:message
												key="credBureName4" /></td>
										<td class="TableData" width="20%" align="left" valign="top">
											<%
											if (appWCFlag.equals("12") || appWCFlag.equals("5")
													|| appWCFlag.equals("13")) {
																	%> <bean:write property="credBureName4"
												name="appForm" /> <%
																		} else {
																	%> <html:text property="credBureName4" size="20"
												alt="credBureName4" name="appForm" /> <%
																		}
																	%> 
										</td>
										<td class="ColumnBackground" width="25%">&nbsp;<font color="#FF0000" size="2"></font> <bean:message
												key="credBureScoreProm4" />
										</td>
										<td class="TableData" width="20%" align="left" valign="top">
											<%
											if (appWCFlag.equals("12") || appWCFlag.equals("5")
													|| appWCFlag.equals("13")) {
																	%> <bean:write property="credBurePromScor4"
												name="appForm" /> <%
																		} else {
																	%> <html:text property="credBurePromScor4" size="20"
												alt="credBurePromScor4" name="appForm" maxlength="3"
												onblur="javascript:calProjectOutlay()"
												onkeypress="return decimalOnly(this, event,13)"
												onkeyup="isValidDecimal(this)" /> <%
																		}
																	%> <bean:message key="3to9" />
										</td>
									</tr>									
									<tr align="left">
										<td class="ColumnBackground" width="25%"><font color="#FF0000" size="2"></font><bean:message
												key="credBureName5" /></td>
										<td class="TableData" width="20%" align="left" valign="top">
											<%
											if (appWCFlag.equals("12") || appWCFlag.equals("5")
													|| appWCFlag.equals("13")) {
																	%> <bean:write property="credBureName5"
												name="appForm" /> <%
																		} else {
																	%> <html:text property="credBureName5" size="20"
												alt="credBureName5" name="appForm"  /> <%
																		}
																	%> 
										</td>
										<td class="ColumnBackground" width="25%">&nbsp;<font color="#FF0000" size="2"></font> <bean:message
												key="credBureScoreProm5" />
										</td>
										<td class="TableData" width="20%" align="left" valign="top">
											<%
											if (appWCFlag.equals("12") || appWCFlag.equals("5")
													|| appWCFlag.equals("13")) {
																	%> <bean:write property="credBurePromScor5"
												name="appForm" /> <%
																		} else {
																	%> <html:text property="credBurePromScor5" size="20"
												alt="credBurePromScor5" name="appForm" maxlength="3"
												onblur="javascript:calProjectOutlay()"
												onkeypress="return decimalOnly(this, event,13)"
												onkeyup="isValidDecimal(this)" /> <%
																		}
												%> <bean:message key="3to9" />
										</td>
									</tr>
									<tr align="left">
										<td class="ColumnBackground" width="25%"><font color="#FF0000" size="2">*</font><bean:message
												key="cibMSMERankFirm" /></td>
										<td class="TableData" width="20%" align="left" valign="top">
											<%
											if (appWCFlag.equals("12") || appWCFlag.equals("5")
													|| appWCFlag.equals("13")) {
																	%> <bean:write property="cibilFirmMsmeRank"
												name="appForm" /> <%
																		} else {
																	%> <html:text property="cibilFirmMsmeRank" size="20"
												alt="credBureName5" name="appForm" maxlength="2"
												onblur="javascript:calProjectOutlay()"
												onkeypress="return decimalOnly(this, event,13)"
												onkeyup="isValidDecimal(this)" /> value between(1-10) <%
																		}
																	%> 
										</td>
										<td class="ColumnBackground" width="25%">&nbsp;<font color="#FF0000" size="2">*</font> <bean:message
												key="expCommercialScore" />
										</td>
										<td class="TableData" width="20%" align="left" valign="top">
											<%
											if (appWCFlag.equals("12") || appWCFlag.equals("5")
													|| appWCFlag.equals("13")) {
																	%> <bean:write property="expCommerScor"
												name="appForm" /> <%
																		} else {
																	%> <html:text property="expCommerScor" size="20"
												alt="credBurePromScor5" name="appForm" maxlength="3"
												onblur="javascript:calProjectOutlay()"
												onkeypress="return decimalOnly(this, event,13)"
												onkeyup="isValidDecimal(this)" /> <%
																		}
												%> <bean:message key="3to9" />
										</td>
									</tr>
									<tr align="left">
										<td class="ColumnBackground" width="25%"><font color="#FF0000" size="2">*</font><bean:message
												key="promNetworth" /></td>
										<td class="TableData" width="20%" align="left" valign="top">
											<%
											if (appWCFlag.equals("12") || appWCFlag.equals("5")
													|| appWCFlag.equals("13")) {
																	%> <bean:write property="promBorrNetWorth"
												name="appForm" /> <%
																		} else {
																	%> <html:text property="promBorrNetWorth" size="20"
												alt="promBorrNetWorth" name="appForm" 
												onblur="javascript:calProjectOutlay()"
												onkeypress="return decimalOnly(this, event,13)"
												onkeyup="isValidDecimal(this)" /> (in Rs. Lacs)
												 <%
													}
												%> 
										</td>
										<td class="ColumnBackground" width="25%">&nbsp;<font color="#FF0000" size="2">*</font> <bean:message
												key="contributPromEntity" />
										</td>
										<td class="TableData" width="20%" align="left" valign="top">
											<%
											if (appWCFlag.equals("12") || appWCFlag.equals("5")
													|| appWCFlag.equals("13")) {
																	%> <bean:write property="promContribution"
												name="appForm" /> <%
																		} else {
																	%> <html:text property="promContribution" size="20"
												alt="promContribution" name="appForm" maxlength="3"
												onblur="javascript:calProjectOutlay()"
												onkeypress="return decimalOnly(this, event,13)"
												onkeyup="isValidDecimal(this)" />(Between 0-100%)
											 <%
												}
												%> 
										</td>
									</tr>
								   <tr align="left">
										<td class="ColumnBackground" width="25%"><font color="#FF0000" size="2">*</font><bean:message
												key="promNpainPast" /></td>
										<td class="TableData" width="20%" align="left" valign="top">
											<%
											if (appWCFlag.equals("12") || appWCFlag.equals("5")
													|| appWCFlag.equals("13")) {
											%> <bean:write property="promGAssoNPA1YrFlg" name="appForm" /> <%
												} else {
											%>
											 <html:radio name="appForm" value="Y" property="promGAssoNPA1YrFlg"/>
											 <bean:message key="yes" />&nbsp;&nbsp;
											  <html:radio name="appForm" value="N" property="promGAssoNPA1YrFlg" />
										      <bean:message key="no" />
										 <%
											}
										%> 
										</td>
										<td class="ColumnBackground" width="25%">&nbsp;<font color="#FF0000" size="2">*</font> <bean:message
												key="promExpRelBusiness" />
										</td>
										<td class="TableData" width="20%" align="left" valign="top">
											<%
											if (appWCFlag.equals("12") || appWCFlag.equals("5")
													|| appWCFlag.equals("13")) {
																	%> <bean:write property="promBussExpYr"
												name="appForm" /> <%
																		} else {
																	%> <html:text property="promBussExpYr" size="20"
												alt="promContribution" name="appForm" maxlength="3"
												onblur="javascript:calProjectOutlay()"
												onkeypress="return decimalOnly(this, event,13)"
												onkeyup="isValidDecimal(this)" />(Between 0-100)
											 <%
												}
												%> 
										</td>
									</tr>									
									<tr align="left">
										<td class="ColumnBackground" width="25%"><font color="#FF0000" size="2">*</font><bean:message
												key="salesRevenue" /></td>
										<td class="TableData" width="20%" align="left" valign="top">
											<%	if (appWCFlag.equals("12") || appWCFlag.equals("5")
													|| appWCFlag.equals("13")) {
																	%> <bean:write property="salesRevenue"
												name="appForm" /> <%
																		} else {
																	%> <html:text property="salesRevenue" size="20"
												alt="salesRevenue" name="appForm" onblur="javascript:calProjectOutlay()"
												onkeypress="return decimalOnly(this, event,13)"
												onkeyup="isValidDecimal(this)" /> (in Rs. Lacs)
												 <%
													}
												%> 
										</td>
										<td class="ColumnBackground" width="25%">&nbsp;<font color="#FF0000" size="2">*</font> <bean:message
												key="taxPBIT" />
										</td>
										<td class="TableData" width="20%" align="left" valign="top">
											<%
											if (appWCFlag.equals("12") || appWCFlag.equals("5")
													|| appWCFlag.equals("13")) {
																	%> <bean:write property="taxPBIT"
												name="appForm" /> <%
																		} else {
																   %> <html:text property="taxPBIT" size="20"
												alt="taxPBIT" name="appForm" onblur="javascript:calProjectOutlay()"
												onkeypress="return decimalOnly(this, event,13)"
												onkeyup="isValidDecimal(this)" />(in Rs. Lacs)
											    <%
												}
												%> 
										</td>
									</tr>
									
									<tr align="left">
										<td class="ColumnBackground" width="25%"><font color="#FF0000" size="2">*</font><bean:message
												key="interestPayment" /></td>
										<td class="TableData" width="20%" align="left" valign="top">
											 <%	if (appWCFlag.equals("12") || appWCFlag.equals("5")
														|| appWCFlag.equals("13")) {
											 %> <bean:write property="interestPayment"
												name="appForm" /> 
												<%
													} else {
												%> <html:text property="interestPayment" size="20"
												alt="interestPayment" name="appForm" onblur="javascript:calProjectOutlay()"
												onkeypress="return decimalOnly(this, event,13)"
												onkeyup="isValidDecimal(this)" /> (in Rs. Lacs)
												 <%
													}
												%> 
										</td>
										<td class="ColumnBackground" width="25%">&nbsp;<font color="#FF0000" size="2">*</font> <bean:message
												key="taxCurrentProvisionAmt" />
										</td>
										<td class="TableData" width="20%" align="left" valign="top">
											<%
											if (appWCFlag.equals("12") || appWCFlag.equals("5")
													|| appWCFlag.equals("13")) {
																	%> <bean:write property="taxCurrentProvisionAmt"
												name="appForm" /> <%
																		} else {
																	%> <html:text property="taxCurrentProvisionAmt" size="20"
												alt="taxCurrentProvisionAmt" name="appForm" onblur="javascript:calProjectOutlay()"
												onkeypress="return decimalOnly(this, event,13)"
												onkeyup="isValidDecimal(this)" />(in Rs. Lacs)
											 <%
												}
												%> 
										</td>
									</tr>
									
									
									<tr align="left">
										<td class="ColumnBackground" width="25%"><font color="#FF0000" size="2">*</font><bean:message
												key="totCurrentAssets" /></td>
										<td class="TableData" width="20%" align="left" valign="top">
											<%
											if (appWCFlag.equals("12") || appWCFlag.equals("5")
													|| appWCFlag.equals("13")) {
											%> <bean:write property="totCurrentAssets" name="appForm" /> <%
																		} else {
																	%> <html:text property="totCurrentAssets" size="20"
												alt="totCurrentAssets" name="appForm" onblur="javascript:calProjectOutlay()"
												onkeypress="return decimalOnly(this, event,13)"
												onkeyup="isValidDecimal(this)" /> (in Rs. Lacs)
												 <%
													}
												%> 
										</td>
										<td class="ColumnBackground" width="25%">&nbsp;<font color="#FF0000" size="2">*</font> <bean:message
												key="totCurrentLiability" />
										</td>
										<td class="TableData" width="20%" align="left" valign="top">
											<%
											if (appWCFlag.equals("12") || appWCFlag.equals("5")
													|| appWCFlag.equals("13")) {
																	%> <bean:write property="totCurrentLiability"
												name="appForm" /> <%
																		} else {
																	%> <html:text property="totCurrentLiability" size="20"
												alt="totCurrentLiability" name="appForm" 
												onkeypress="return decimalOnly(this, event,13)"
												onkeyup="isValidDecimal(this)" />(in Rs. Lacs)
											 <%
												}
												%> 
										</td>
									</tr>
									
									
									<tr align="left">
										<td class="ColumnBackground" width="25%"><font color="#FF0000" size="2">*</font><bean:message
												key="totTermLiability" /></td>
										<td class="TableData" width="20%" align="left" valign="top">
											<%
											if (appWCFlag.equals("12") || appWCFlag.equals("5")
													|| appWCFlag.equals("13")) {
																	%> <bean:write property="totTermLiability"
												name="appForm" /> <%
																		} else {
																	%> <html:text property="totTermLiability" size="20"
												alt="totTermLiability" name="appForm" 
												onkeypress="return decimalOnly(this, event,13)"
												onkeyup="isValidDecimal(this)" /> (in Rs. Lacs)
												 <%
													}
												%> 
										</td>
										<td class="ColumnBackground" width="25%">&nbsp;<font color="#FF0000" size="2">*</font> <bean:message
												key="exuityCapital" />
										</td>
										<td class="TableData" width="20%" align="left" valign="top">
											<%
											if (appWCFlag.equals("12") || appWCFlag.equals("5")
													|| appWCFlag.equals("13")) {
																	%> <bean:write property="exuityCapital"
												name="appForm" /> <%
																		} else {
																	%> <html:text property="exuityCapital" size="20"
												alt="exuityCapital" name="appForm" 
												onkeypress="return decimalOnly(this, event,13)"
												onkeyup="isValidDecimal(this)" />(in Rs. Lacs)
											 <%
												}
												%> 
										</td>
									</tr>
									
									<tr align="left">
										<td class="ColumnBackground" width="25%"><font color="#FF0000" size="2">*</font><bean:message
												key="preferenceCapital" /></td>
										<td class="TableData" width="20%" align="left" valign="top">
											<%
											if (appWCFlag.equals("12") || appWCFlag.equals("5")
													|| appWCFlag.equals("13")) {
																	%> <bean:write property="preferenceCapital"
												name="appForm" /> <%
																		} else {
																	%> <html:text property="preferenceCapital" size="20"
												alt="preferenceCapital" name="appForm" 
												onkeypress="return decimalOnly(this, event,13)"
												onkeyup="isValidDecimal(this)" /> (in Rs. Lacs)
												 <%
													}
												%> 
										</td>
										<td class="ColumnBackground" width="25%">&nbsp;<font color="#FF0000" size="2">*</font> <bean:message
												key="reservesSurplus" />
										</td>
										<td class="TableData" width="20%" align="left" valign="top">
											<%
											if (appWCFlag.equals("12") || appWCFlag.equals("5")
													|| appWCFlag.equals("13")) {
																	%> <bean:write property="reservesSurplus"
												name="appForm" /> <%
																		} else {
																	%> <html:text property="reservesSurplus" size="20"
												alt="exuityCapital" name="appForm" 
												onkeypress="return decimalOnly(this, event,13)"
												onkeyup="isValidDecimal(this)" />(in Rs. Lacs)
											 <%
												}
												%> 
										</td>
									</tr>
									
									<tr align="left">
										<td class="ColumnBackground" width="25%"><font color="#FF0000" size="2">*</font><bean:message
												key="repaymentDueNyrAmt" /></td>
										<td class="TableData" width="20%" align="left" valign="top">
											<%
											if (appWCFlag.equals("12") || appWCFlag.equals("5")
													|| appWCFlag.equals("13")) {
																	%> <bean:write property="repaymentDueNyrAmt"
												name="appForm" /> <%
																		} else {
																	%> <html:text property="repaymentDueNyrAmt" size="20"
												alt="repaymentDueNyrAmt" name="appForm" 
												onkeypress="return decimalOnly(this, event,13)"
												onkeyup="isValidDecimal(this)" /> (in Rs. Lacs)
												 <%
													}
												%> 
										</td>
										<td class="ColumnBackground" width="25%">&nbsp;<!--  <bean:message
												key="reservesSurplus" /> -->
										</td>
										<td class="TableData" width="20%" align="left" valign="top">
											<%
											if (appWCFlag.equals("12") || appWCFlag.equals("5")
													|| appWCFlag.equals("13")) {
																	%> <!-- <bean:write property="reservesSurplus"
												name="appForm" /> --> <%
																		} else {
																	%> <!-- <html:text property="reservesSurplus" size="20"
												alt="exuityCapital" name="appForm" maxlength="16"
												onblur="javascript:calProjectOutlay()"
												onkeypress="return decimalOnly(this, event,13)"
												onkeyup="isValidDecimal(this)" />(in Rs. Lacs) -->
											 <%
												}
											 %> 
										</td>
									</tr>
					            	</table>
		                          </TD>
								</TR>
                                <!-- END -->
								<%//}%>
							</table>			<!--end of WC-->
						