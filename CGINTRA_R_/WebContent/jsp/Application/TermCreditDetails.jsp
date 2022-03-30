<%@ page import="com.cgtsi.util.SessionConstants"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean"%>
<script type="text/javascript">
	function enableDisbursement(flag){
		if(flag == "Y"){
		document.forms[0].amtDisbursed.disabled = false;
		document.forms[0].firstDisbursementDate.disabled = false;
		document.forms[0].firstInstallmentDueDate.disabled = false;
		document.forms[0].periodicity.disabled = false;
		document.forms[0].noOfInstallments.disabled = false;
		}
		if(flag == "N"){
		document.forms[0].amtDisbursed.disabled = true;
		document.forms[0].amtDisbursed.value = '';
		document.forms[0].firstDisbursementDate.disabled = true;
		document.forms[0].firstDisbursementDate.value = '';
		document.forms[0].firstInstallmentDueDate.disabled = true;
		document.forms[0].firstInstallmentDueDate.value = '';
		document.forms[0].periodicity.disabled = true;
		document.forms[0].periodicity.value = '';
		document.forms[0].noOfInstallments.disabled = true;
		document.forms[0].noOfInstallments.value = '';
		}
	}
</script>
<table width="100%" border="0" cellspacing="1" cellpadding="0">
								<TR>
									<TD colspan="10">
										<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
											<TR>
												<TD width="31%" class="Heading"><bean:message key="facilityDetails" /></TD>
												<TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
											</TR>
											<TR>
												<TD colspan="9" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
											</TR>
										</TABLE>
									</TD>
								</TR>
								<%
								String appTCFlag=session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).toString();
                                String appCommonFlag1=session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).toString();
								if (appTCFlag.equals("0") || appTCFlag.equals("1") || appTCFlag.equals("2"))
								{

								%>
								<tr align="left">
									<td class="ColumnBackground">
										<bean:message key="facilityRehabilitation"/>
									</td>
									<td class="TableData" colspan="9">
									
											<html:radio name="appForm" value="Y" property="rehabilitation" ></html:radio>
											
											<bean:message key="yes"/>

											
											<html:radio name="appForm" value="N" property="rehabilitation" ></html:radio>
											
											<bean:message key="no"/>
									</td>
								</tr>
								<%}%>
								<tr>
									<td class="ColumnBackground">&nbsp;<bean:message key="scheme"/>
									</td>
									<td class="TableData" colspan="10">&nbsp;
									<%
									if (session.getAttribute(SessionConstants.MCGF_FLAG).equals("M"))
									{ %>
									<bean:write property="scheme" name="appForm"/>
									<% }
									else
									{ %>
									<bean:write property="scheme" name="appForm"/>
									<% }%>
									</td>               
								</tr>
								<tr>
									<td class="ColumnBackground">&nbsp;<bean:message key="loanType" />
									</td>
									<td class="TableData" colspan="10"  id="loanType">&nbsp;<bean:write property="loanType" name="appForm"/>
									<html:hidden property="loanType" name="appForm"/>
									
									</td> 
								</tr>
								<tr>
									<td class="ColumnBackground">&nbsp;<bean:message key="compositeLoan"/>
									</td>
									<td class="TableData" colspan="10">&nbsp;<bean:write property="compositeLoan" name="appForm"/>
									</td> 
								</tr>
								
								<tr align="left"> 
									<td colspan="10" class="SubHeading" height="28" width="843"><br> &nbsp;
										<bean:message key="termCreditDetails"/>
									</td>
								</tr>
								
								<tr align="left"> 
									<td class="ColumnBackground" align="left">&nbsp;
										<bean:message key="amountSanctioned" />
									</td>
									<td align="right" class="TableData" colspan="10">
									<table>
									<tr>
										<td class="ColumnBackground" width="15%">Term Loan</td>
										<td width="35%" class="TableData" id="amountsanctioned">
										<%				
											if((appTCFlag.equals("0"))||(appTCFlag.equals("3"))||(appTCFlag.equals("5"))||(appTCFlag.equals("6") || (appTCFlag.equals("11"))||(appTCFlag.equals("13"))))
											{										
											%>
										<!--	<html:text name="appForm" property="amountSanctioned"/> -->
												<bean:write property="amountSanctioned" name="appForm"/>
											<% }
											else
											{ 											
											%>
											
											<%
											}%>																						
										</td>
									</tr>
									<tr>
											<td class="ColumnBackground" width="15%">WC fund based</td>
											<td class="TableData" align="left" valign="top" id="fundbasedlimitsanctioned">

															<% if(appTCFlag.equals("0")||appTCFlag.equals("3")||appTCFlag.equals("5")||appTCFlag.equals("6") ||appTCFlag.equals("11") || appTCFlag.equals("12") || appTCFlag.equals("13"))
															 {															
															%>
															<bean:write property="wcFundBasedSanctioned" name="appForm"/>
															<%}
															else {														
															%>																
															<%}%>															
											</td>
									</tr>
									<tr>
											<td class="ColumnBackground" width="15%">WC Non fund based</td>
											<td class="TableData" align="left" valign="top" id="nonfundbasedlimitsantioned" >

															<% if((appTCFlag.equals("0"))||(appTCFlag.equals("3"))||(appTCFlag.equals("5"))||(appTCFlag.equals("6")) ||appTCFlag.equals("11") || appTCFlag.equals("12") || appTCFlag.equals("13"))
															 {															
															%>
															<bean:write property="wcNonFundBasedSanctioned" name="appForm"/>
															<%}
															else {															
															%>																															
															<% } %>														
											</td>
									</tr>
								</table>
								</td>	
									
								</tr>
							<TR>
									<td width="15%" class="ColumnBackground" height="28">&nbsp;
										<font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="amountSanctionedDate" />
									</td>
									<td width="20%" class="TableData" height="28" colspan="8">
									<%				
										if((appCommonFlag1.equals("11"))||(appCommonFlag1.equals("13")))
										
										
										{
										%>
										<bean:write name="appForm" property="amountSanctionedDate"/>
										<%}
										else
										{ %>
									
											<html:text property="amountSanctionedDate" size="20" alt="amountSanctionedDate" name="appForm" maxlength="10"/>
											<IMG src="images/CalendarIcon.gif" width="20" onClick="showCalendar('appForm.amountSanctionedDate')" align="center">
										<%}%>
											
									</td>
								 </tr>
								 <tr align="left">
									<td width="25%" class="ColumnBackground">&nbsp;
										<font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="creditGuaranteed" />						
									</td>
									<td colspan="10" class="TableData">
									<%				
										if((appCommonFlag1.equals("11"))||(appCommonFlag1.equals("13")))
										

										{
										%>
										<bean:write name="appForm" property="creditGuaranteed"/>
										<%}
										else
										{ %>
									
											<html:text property="creditGuaranteed" size="20" alt="creditGuaranteed" name="appForm" maxlength="16" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)"/>&nbsp;<bean:message key="inRs" />
										<%}%>
										
									</td>
								</tr>
								<tr align="left">
									<td width="25%" class="ColumnBackground">&nbsp;
										<font color="#FF0000" size="2">*</font>&nbsp;Whether partial or full disbursement has been made ? :						
									</td>
									<td colspan="10" class="TableData">
										
									</td>
								</tr>
								<tr> 
									<td class="ColumnBackground" height="28" width="25%">&nbsp;
										<bean:message key="amtDisbursed" />		
									</td>
									<td class="TableData" height="28" width="25%">
									<%
									
										if(appCommonFlag1.equals("11")||(appCommonFlag1.equals("13")) || appTCFlag.equals("3") || appTCFlag.equals("6"))
										{
										
										%>
										<bean:write name="appForm" property="amtDisbursed"/>
										<%}
										else
										{ %>
										<html:text property="amtDisbursed" size="20" alt="amtDisbursed" name="appForm" maxlength="16" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)" disabled="true"/>
										<%}%>
										<bean:message key="inRs" />	
									</td>
									<td class="ColumnBackground" height="28" colspan="9">
										<table border="0" cellpadding="0" cellspacing="0"  width="100%">
											<tr align="left">
												<td class="ColumnBackground" height="28" width="269">&nbsp;
													<bean:message key="firstDisbursementDate"/>	
												</td>
												<td class="TableData" height="28" width="160" colspan="5">
												<%
												if(appCommonFlag1.equals("11")||(appCommonFlag1.equals("13")) || appTCFlag.equals("3") || appTCFlag.equals("6"))
												{
												%>
												<bean:write name="appForm" property="firstDisbursementDate"/>
												<%}
												else
												{ %>
													<html:text property="firstDisbursementDate" size="10" alt="firstDisbursementDate" name="appForm" maxlength="10" disabled="true"/>
													<IMG src="images/CalendarIcon.gif" width="20" onClick="showCalendar('appForm.firstDisbursementDate')" align="center">
												<%}%>
												</td>
											</tr>
											<tr align="left">
												<td class="ColumnBackground" height="28" width="269" >&nbsp;
												<%
												if(appCommonFlag1.equals("11")||(appCommonFlag1.equals("13")) || appTCFlag.equals("3") || appTCFlag.equals("6"))
												{
												%>
													<bean:message key="lastDisbursementDate"/>	
													<%
												}
												else
												{%>
												<bean:message key="finalDisbursementDate"/>
												<%}%>
												</td>
												<td class="TableData" height="28" width="160">
												<%
												if(appCommonFlag1.equals("11")||(appCommonFlag1.equals("13")) || appTCFlag.equals("3") || appTCFlag.equals("6"))
												{
												%>
												<bean:write name="appForm" property="finalDisbursementDate"/>
												<%}
												else
												{ %>
													<html:text property="finalDisbursementDate" size="10" alt="finalDisbursementDate" name="appForm" maxlength="10"/>
													<IMG src="images/CalendarIcon.gif" width="20" onClick="showCalendar('appForm.finalDisbursementDate')" align="center">
												<%}%>
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr align="left"> 
									<td class="ColumnBackground" height="28" >&nbsp;
										<font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="tenure" />&nbsp;
									</td>
									<td class="TableData" height="28" colspan="10">	
									<%				
										if((appCommonFlag1.equals("11"))||(appCommonFlag1.equals("13")))
										

										{
										%>
										<bean:write name="appForm" property="tenure"/>
										<%}
										else
										{ %>
									
										<html:text property="tenure" size="20" alt="tenure" name="appForm" maxlength="3" onkeypress="return numbersOnly(this, event)" onkeyup="isValidNumber(this)"/><bean:message key="inMonths" />
									<%}%>
									
									</td>
								</tr>
								<tr align="left"> 
									<td class="ColumnBackground" height="28">&nbsp;
										<bean:message key="interestType" />
									</td>
									<td class="TableData" >
									<%				
										if((appCommonFlag1.equals("11"))||(appCommonFlag1.equals("13")))
										

										{
										%>
										<html:radio name="appForm" value="T" property="interestType" disabled="true">
										</html:radio>
									
										<bean:message key="fixedInterest"/>&nbsp;
									
										<html:radio name="appForm" value="F" property="interestType" disabled="true"></html:radio>	
									
										<bean:message key="floatingInterest"/>	

<!--										<bean:write name="appForm" property="interestType"/>-->
										<%}
										else
										{ %>

										<html:radio name="appForm" value="T" property="interestType" >
										</html:radio>
									
										<bean:message key="fixedInterest"/>&nbsp;
									
										<html:radio name="appForm" value="F" property="interestType" ></html:radio>	
									
										<bean:message key="floatingInterest"/>	
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
										if((appCommonFlag1.equals("11"))||(appCommonFlag1.equals("13")))
										

										{
										%>
										<bean:write name="appForm" property="typeOfPLR"/>
										<%}
										else
										{ %>
										
										<html:text property="typeOfPLR" size="20" alt="typeOfPLR" name="appForm" maxlength="50" />&nbsp;						
									<%}%>
									</td>
								</tr>
								<tr align="left">
									
									<td width="25%" class="ColumnBackground"> <div align="left">&nbsp;
										<font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="plr" /></div>
									</td>
									<td class="TableData" >
										<div align="left"> 
										<%				
										if((appCommonFlag1.equals("11"))||(appCommonFlag1.equals("13")))
										

										{
										%>
										<bean:write name="appForm" property="plr"/>
										<%}
										else
										{ %>
											<html:text property="plr" size="5" alt="plr" name="appForm" maxlength="6" onkeypress="return decimalOnly(this, event,3)" onkeyup="isValidDecimal(this)"/>&nbsp;
										<%}%>
											<bean:message key="inPa" />
										
										</div>
									</td>
									<td width="25%" class="ColumnBackground"> <div align="left">&nbsp;
										<font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="interestRate" /></div>
									</td>
									<td class="TableData" colspan="8">
										<div align="left"> 
										<%				
										if((appCommonFlag1.equals("11"))||(appCommonFlag1.equals("13")))
										

										{
										%>
										<bean:write name="appForm" property="interestRate"/>
										<%}
										else
										{ %>
										
											<html:text property="interestRate" size="5" alt="interestRate" name="appForm" maxlength="5" onkeypress="return decimalOnly(this, event,2)" onkeyup="isValidDecimal(this)"/>&nbsp;
										<%}%>
											<bean:message key="inPa" />
										
										</div>
									</td>

								</tr>
								<tr align="left"> 
									<td class="SubHeading" colspan="12" height="24" width="843">
										<bean:message key="repaymentSchedule" />
									</td>
								</tr>
								<TR>
									<td class="ColumnBackground">
										<font color="#FF0000" size="2">*</font>&nbsp;Moratorium Principal
									</td>
									<td class="TableData">
										<span style="font-size: 9pt; font-weight: 700">
													<%				
													if((appCommonFlag1.equals("11"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("3")) || (appCommonFlag1.equals("6")))										

													{
													%>
													
													<%}
													else
													{ %>
													
													<%}%></span>
													<span style="font-size: 9pt"> 
												
													<bean:message key="inMonths" />
													</span>
									</td>
									<td class="ColumnBackground">
										<font color="#FF0000" size="2">*</font>&nbsp;Moratorium Interest
									</td>
									<td class="TableData">
									<%				
													if((appCommonFlag1.equals("11"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("3")) || (appCommonFlag1.equals("6")))										

													{
													%>
													
													<%}
													else
													{ %>
													
													<%}%></span>
													<span style="font-size: 9pt"> 
												
													<bean:message key="inMonths" />
													</span>
									</td>
								</TR>
								<tr align="left"> 
									<td class="ColumnBackground" colspan="12" height="24" width="843">
										<table border="0" cellpadding="0" cellspacing="1"  width="100%" height="71">
											<tr>
												<!--	<td width="7%" height="18">
												</td> -->
												<td width="29%" height="18"><span style="font-size: 9pt; font-weight: 700">
													<font color="#FF0000" size="2">*</font>&nbsp;Repayment&nbsp;<bean:message key="repaymentMoratorium" /></span>
												</td>
												<td width="64%" height="18" colspan="3">
												
													<span style="font-size: 9pt; font-weight: 700">
													<%				
													if((appCommonFlag1.equals("11"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("3")) || (appCommonFlag1.equals("6")))										

													{
													%>
													<bean:write name="appForm" property="repaymentMoratorium"/>
													<%}
													else
													{ %>
													<html:text property="repaymentMoratorium" size="5" alt="repaymentMoratorium" name="appForm" maxlength="3" onkeypress="return numbersOnly(this, event)" onkeyup="isValidNumber(this)"/>
													<%}%></span>
													<span style="font-size: 9pt"> 
												
													<bean:message key="inMonths" />
													</span>
												</td>
											</tr>
											<tr>
												<!--	<td width="7%" height="18">
												</td> -->
												<td width="29%" height="18">
													<span style="font-size: 9pt; font-weight: 700">
														<font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="firstInstallmentDueDate" />
													</span>
												</td>
												<td width="64%" height="18">
												
													<span style="font-size: 9pt; font-weight: 700">
													<%				
													if((appCommonFlag1.equals("11"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("3")) || (appCommonFlag1.equals("6")))																			
													{
													%>
													<bean:write name="appForm" property="firstInstallmentDueDate"/>
													<%}
													else
													{ %>
														<html:text property="firstInstallmentDueDate" size="20" alt="firstInstallmentDueDate" name="appForm" maxlength="10" disabled="true"/>
														<IMG src="images/CalendarIcon.gif" width="20" onClick="showCalendar('appForm.firstInstallmentDueDate')" align="center">
														<%}%>
														</span>
													
												</td>
											</tr>
											<tr>
												<!--	<td width="7%" height="18">
												</td> -->
												<td width="29%" height="18">
													<span style="font-size: 9pt; font-weight: 700">
														<font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="periodicity" />
													</span>
												</td>
												<td width="64%" height="18">
												
													<span style="font-size: 9pt; font-weight: 700">
													<%				
													if((appCommonFlag1.equals("11"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("3")) || (appCommonFlag1.equals("6")))						
													{
													%>
													<html:select property="periodicity" name="appForm" disabled="true">
														<html:option value="">Select</html:option>
														<html:option value="1">Monthly</html:option>
														<html:option value="2">Quarterly</html:option>
														<html:option value="3">Half-Yearly</html:option>
													</html:select>

													<%}
													else
													{ %>
													<html:select property="periodicity" name="appForm" disabled="true">
														<html:option value="">Select</html:option>
														<html:option value="1">Monthly</html:option>
														<html:option value="2">Quarterly</html:option>
														<html:option value="3">Half-Yearly</html:option>
													</html:select>
													<%}%>
													</span>
												
												 </td>
											</tr>
											<tr>
											<!--	<td width="7%" height="18">
												</td> -->
												<td width="29%" height="18">
													<span style="font-size: 9pt; font-weight: 700">
													<font color="#FF0000" size="2">*</font><bean:message key="noOfInstallments" />
													</span>
												</td>
												<td width="64%" height="18">
											
													<span style="font-size: 9pt; font-weight: 700">
													<%				
													if((appCommonFlag1.equals("11"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("3")) || (appCommonFlag1.equals("6")))						
													{
													%>
													<bean:write name="appForm" property="noOfInstallments"/>
													<%}
													else
													{ %>
													<html:text property="noOfInstallments" size="5" alt="noOfInstallments" name="appForm" maxlength="3" onkeypress="return numbersOnly(this, event)" onkeyup="isValidNumber(this)" disabled="true"/>
													<%}%>
													</span>
												
												</td>
											</tr>
										 </table>
									</td>
								</tr>
								<tr> 
									<td class="ColumnBackground" height="28" width="252">&nbsp;
										<bean:message key="pplOS" />
									</td>
									<td colspan="10" class="TableData" height="28" width="590">
									<%				
										if((appCommonFlag1.equals("11"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("3")) || (appCommonFlag1.equals("6")))			

										{
										%>
										<bean:write name="appForm" property="pplOS"/>
										<%}
										else
										{ %>
									
										<html:text property="pplOS" size="5" alt="pplOS" name="appForm" maxlength="16" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)"/>
									<%}%>
										
										<bean:message key="inRs" />&nbsp;
										<bean:message key="pplOsAsOnDate" />

										<%				
										if((appCommonFlag1.equals("11"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("3")) || (appCommonFlag1.equals("6")))

										{
										%>
										<bean:write name="appForm" property="pplOsAsOnDate"/>
										<%}
										else
										{ %>
										
										<html:text property="pplOsAsOnDate" size="20" alt="pplOsAsOnDate" name="appForm" maxlength="10"/>
										<IMG src="images/CalendarIcon.gif" width="20" onClick="showCalendar('appForm.pplOsAsOnDate')" align="center">
										<%}%>
										
									</td>
								</tr>		
                                <!-- Added by DKR for Financial Recore -->                               
  <%
							    // String loanTypk = session.getAttribute("APPLICATION_LOAN_TYPE").toString();
								//if((loanTypk.equals("TC") && !loanTypk.equals("BO") && !loanTypk.equals("TCE")))		
                               // {
						      %> 
								<!-- <tr align="left">
						          <TD align="left" valign="top" class="ColumnBackground"  colspan="2"><font color="#FF0000" size="2">*</font><bean:message
										key="inCrilcCibilRbi" /></TD>
								<TD align="left" valign="top" class="tableData" colspan="2">
								<html:radio	name="appForm" value="Y" property="promDirDefaltFlg" styleId="promDirDefaltFlg_Y" onclick="enableOtherFinancialDtl('TC');" />
										 <bean:message key="yes" />&nbsp;&nbsp;
										  <html:radio name="appForm" value="N" property="promDirDefaltFlg" styleId="promDirDefaltFlg_N" onclick="enableOtherFinancialDtl('TC');" />
									<bean:message key="no" /></TD>	
						       </tr> -->	
					       		<!-- 	========================================== -->
					       		<TR>
								  <TD colspan="8">
									<TABLE width="100%" id="financialOtherDtlLblId" >
										<!-- Need to add financial condition to display on behalf of amount condition -->
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
											<%if (appTCFlag.equals("11") 
													|| appTCFlag.equals("13")) {
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
											if (appTCFlag.equals("11") 
													|| appTCFlag.equals("13")){
																	%> <bean:write property="credBureKeyPromScor" name="appForm" />
																	 <%
																		} else {
																	%> <html:text property="credBureKeyPromScor" size="20"
												alt="credBureKeyPromScor" name="appForm" maxlength="3" />
												<!-- 	alt="credBureKeyPromScor" name="appForm" maxlength="3" onkeyup="isValidDecimal(this)" />  -->
												 <bean:message key="3to9" /><%
												
																		}
																	%> 
										</td>
									</tr>						       						
									<tr align="left">
										<td class="ColumnBackground" width="25%"><font color="#FF0000" size="2"></font><bean:message
												key="credBureName2" /></td>
										<td class="TableData" width="20%" align="left" valign="top">
											<%
																		if (appTCFlag.equals("11") 
																				|| appTCFlag.equals("13")) {
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
																		if (appTCFlag.equals("11") 
																				|| appTCFlag.equals("13")) {
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
											<%
																		if (appTCFlag.equals("11") 
																				|| appTCFlag.equals("13")) {
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
											if (appTCFlag.equals("11") 
													|| appTCFlag.equals("13")) {
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
																		if(appTCFlag.equals("11") 
																				|| appTCFlag.equals("13")) {
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
																		if (appTCFlag.equals("11") 
																				|| appTCFlag.equals("13")) {
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
																		if(appTCFlag.equals("11") 
																				|| appTCFlag.equals("13")) {
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
																		if (appTCFlag.equals("11") 
																				|| appTCFlag.equals("13")) {
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
																		if (appTCFlag.equals("11") 
																				|| appTCFlag.equals("13")) {
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
																		if (appTCFlag.equals("11") 
																				|| appTCFlag.equals("13")){
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
																		if(appTCFlag.equals("11") 
																				|| appTCFlag.equals("13")) {
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
																		if (appTCFlag.equals("11") 
																				|| appTCFlag.equals("13")) {
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
											if (appTCFlag.equals("11") 
													|| appTCFlag.equals("13")){
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
																		if(appTCFlag.equals("11") 
																				|| appTCFlag.equals("13")) {
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
											<%
																		if(appTCFlag.equals("11") 
																				|| appTCFlag.equals("13")) {
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
																		if(appTCFlag.equals("11") 
																				|| appTCFlag.equals("13")) {
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
											 <%
													if (appTCFlag.equals("11") || appTCFlag.equals("13")){
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
																		if (appTCFlag.equals("11") 
																				|| appTCFlag.equals("13")) {
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
																		if (appTCFlag.equals("11") 
																				|| appTCFlag.equals("13")) {
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
																		if (appTCFlag.equals("11") 
																				|| appTCFlag.equals("13")) {
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
																		if(appTCFlag.equals("11") 
																				|| appTCFlag.equals("13")) {
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
																		if(appTCFlag.equals("11") 
																				|| appTCFlag.equals("13")) {
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
																		if (appTCFlag.equals("11") 
																				|| appTCFlag.equals("13")) {
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
																		if (appTCFlag.equals("11") 
																				|| appTCFlag.equals("13")) {
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
																		if (appTCFlag.equals("11") 
																				|| appTCFlag.equals("13")) {
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
																		if (appTCFlag.equals("11") 
																				|| appTCFlag.equals("13")) {
																	%> <!-- <bean:write property="reservesSurplus"
												name="appForm" /> --> <%
																		} else {
																	%> <!-- <html:text property="reservesSurplus" size="20"
												alt="exuityCapital" name="appForm" maxlength="16"
												onblur="javascript:calProjectOutlay()"
												onkeypress="return decimalOnly(this, event,13)"
												onkeyup="isValidDecimal(this)" />(in Rs. Lacs) -->
											 <%
												  } // if condition open then it'll working
											 %> 
										</td>
									</tr>
					            	</table>
		                          </TD>
								</TR>
								 <!--CR 158 End -->	
									
							     								
						<%//}%>
		                 
							    <!-- End -->		           
							</table>			<!--end of term -->
						