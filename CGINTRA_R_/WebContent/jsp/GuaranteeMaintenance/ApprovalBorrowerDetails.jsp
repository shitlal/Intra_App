<%@ page language="java"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.cgtsi.application.BorrowerDetails"%>
<%@ page import="com.cgtsi.application.SSIDetails"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="java.util.Date"%>
<%@ include file="/jsp/SetMenuInfo.jsp" %>

<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic" %>


<%
session.setAttribute("CurrentPage","showBorrowerDetailsForApproval.do?method=showBorrowerDetailsForApproval");
%>

<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form action="showBorrowerDetailsForApproval.do?method=showBorrowerDetailsForApproval" >
		<TR> 
			<TD width="20" align="right" valign="bottom"><IMG src="images/TableLeftTop.gif" width="20" height="31"></TD>
			<TD background="images/TableBackground1.gif"><IMG src="images/GuaranteeMaintenanceHeading.gif"></TD>
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
									<TD colspan="6"> 
										<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
											<TR>
												<TD width="40%" class="Heading"><bean:message key="borrowerHeader"/> for <bean:write name="gmApprovalForm" property="borrowerId"/></TD>
												<TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
											</TR>
								  </table>
							  </td>
							</tr>

							<tr>
								<td align="center" valign="middle" class="HeadingBg"
								align="center"><center><bean:message key="field"/></td>
								<td align="center" valign="middle" class="HeadingBg"
								align="center"><center><bean:message key="oldBorrowerDetails"/></td>
								<td align="center" valign="middle" class="HeadingBg"
								align="center"><center><bean:message key="newBorrowerDetails"/></td>
							</tr>

							<%
								BorrowerDetails oldBorrower = new BorrowerDetails();
								BorrowerDetails newBorrower = new BorrowerDetails();
								SSIDetails oldSSI = new SSIDetails();
								SSIDetails newSSI = new SSIDetails();
								int i=0;
							%>

							<logic:iterate name="gmApprovalForm" property="borrowerDetails" id="object">
							<%
							if (i==0)
							{
								oldBorrower = (BorrowerDetails) object;
								oldSSI = oldBorrower.getSsiDetails();
							}
							else if (i==1)
							{
								newBorrower = (BorrowerDetails) object;
								newSSI = newBorrower.getSsiDetails();
							}
							i++;
							%>
							</logic:iterate>
							<%
								SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
								DecimalFormat df = new DecimalFormat("#############.##");
								df.setDecimalSeparatorAlwaysShown(false);

								String oldSSIRef = ""+oldSSI.getBorrowerRefNo();
								String oldBid  = oldSSI.getCgbid();
								String oldAssisted = oldBorrower.getAssistedByBank();
								String oldStrOsAmt="";
								if(oldBorrower.getOsAmt()!=0)
								{
									double oldOsAmt = oldBorrower.getOsAmt();
									oldStrOsAmt = df.format(oldOsAmt);
								}
								else{
									double oldOsAmt=0;
								}								
								String oldNpa = oldBorrower.getNpa();
								String oldCovered = oldBorrower.getPreviouslyCovered();
								String oldConst="";
								if(oldSSI.getConstitution()!=null)
								{
									oldConst = oldSSI.getConstitution();
								}
								String oldSSIType="";
								if(oldSSI.getSsiType()!=null)
								{
									oldSSIType = oldSSI.getSsiType();
								}
								String oldSSIName ="";
								if(oldSSI.getSsiName()!=null)
								{
									oldSSIName = oldSSI.getSsiName();								
								}
								String oldRegNo ="";
								if(oldSSI.getRegNo()!=null)
								{
									oldRegNo = oldSSI.getRegNo();
								}
								String oldSSIItpan ="";
								if(oldSSI.getSsiITPan()!=null)
								{
									oldSSIItpan = oldSSI.getSsiITPan();									
								}
								String oldIndNature ="";
								if(oldSSI.getIndustryNature()!=null)
								{
									oldIndNature = oldSSI.getIndustryNature();
								}
								String oldIndSector ="";
								if(oldSSI.getIndustrySector()!=null)
								{
									oldIndSector = oldSSI.getIndustrySector();
								}
								String oldActType ="";
								if(oldSSI.getActivityType()!=null)
								{
									oldActType = oldSSI.getActivityType();
								}								
								String oldNoOfEmp ="";
								if(oldSSI.getEmployeeNos()!=0)
								{
									oldNoOfEmp = ""+oldSSI.getEmployeeNos();
								}
								String oldStrTurnover ="";
								if(oldSSI.getProjectedSalesTurnover()!=0)
								{
									double oldTurnover = oldSSI.getProjectedSalesTurnover();
									oldStrTurnover = df.format(oldTurnover);
								}
								String oldStrExports ="";
								if(oldSSI.getProjectedExports()!=0)
								{
									double oldExports = oldSSI.getProjectedExports();
									oldStrExports = df.format(oldExports);
								}
								String oldAddress ="";
								if(oldSSI.getAddress()!=null)
								{
									oldAddress = oldSSI.getAddress();
								}
								String oldState ="";
								if(oldSSI.getState()!=null)
								{
									oldState = oldSSI.getState();
								}
								String oldDistrict ="";
								if(oldSSI.getDistrict()!=null)
								{
									oldDistrict = oldSSI.getDistrict();
								}
								String oldCity ="";
								if(oldSSI.getCity()!=null)
								{
									oldCity = oldSSI.getCity();
								}
								String oldPincode ="";
								if(oldSSI.getPincode()!=null)
								{
									oldPincode = oldSSI.getPincode();
								}
								String oldCpTitle ="";
								if(oldSSI.getCpTitle()!=null)
								{
									oldCpTitle = oldSSI.getCpTitle();
								}
								String oldCpFirstName ="";
								if(oldSSI.getCpFirstName()!=null)
								{
									oldCpFirstName = oldSSI.getCpFirstName();
								}
								String oldCpMiddleName ="";
								if(oldSSI.getCpMiddleName()!=null)
								{
									oldCpMiddleName = oldSSI.getCpMiddleName();
								}
								String oldCpLastName ="";
								if(oldSSI.getCpLastName()!=null)
								{
									oldCpLastName = oldSSI.getCpLastName();
								}
								String oldCpGender ="";
								if(oldSSI.getCpGender()!=null)
								{
									oldCpGender = oldSSI.getCpGender();
								}
								String oldCpItpan ="";
								if(oldSSI.getCpITPAN()!=null)
								{
									oldCpItpan = oldSSI.getCpITPAN();
								}
								Date oldCpDob = oldSSI.getCpDOB();
								String oldStrDob = "";
								if (oldCpDob != null)
								{
									oldStrDob = dateFormat.format(oldCpDob);
								}
								String oldSocialCat = oldSSI.getSocialCategory();
								if(oldSocialCat==null)
								{
									oldSocialCat="";
								}
								String oldLegalId = oldSSI.getCpLegalID();
								if(oldLegalId==null)
								{
									oldLegalId="";
								}
								String oldLegalValue = oldSSI.getCpLegalIdValue();
								if(oldLegalValue==null)
								{
									oldLegalValue="";
								}
								String oldOtherName1 = oldSSI.getFirstName();
								if(oldOtherName1==null)
								{
									oldOtherName1="";
								}
								String oldOtherItpan1 = oldSSI.getFirstItpan();
								if(oldOtherItpan1==null)
								{
									oldOtherItpan1="";
								}
								Date oldOtherDob1 = oldSSI.getFirstDOB();
								String oldStrOtherDob1 = "";
								if (oldOtherDob1 != null)
								{
									oldStrOtherDob1 = dateFormat.format(oldOtherDob1);
								}
								String oldOtherName2 = oldSSI.getSecondName();
								if(oldOtherName2==null)
								{
									oldOtherName2="";
								}
								String oldOtherItpan2 = oldSSI.getSecondItpan();
								if(oldOtherItpan2==null)
								{
									oldOtherItpan2="";
								}						
								Date oldOtherDob2 = oldSSI.getSecondDOB();
								String oldStrOtherDob2 = "";
								if (oldOtherDob2 != null)
								{
									oldStrOtherDob2 = dateFormat.format(oldOtherDob2);
								}
								String oldOtherName3 = oldSSI.getThirdName();
								if(oldOtherName3==null)
								{
									oldOtherName3="";
								}
								String oldOtherItpan3 = oldSSI.getThirdItpan();
								if(oldOtherItpan3==null)
								{
									oldOtherItpan3="";
								}
								Date oldOtherDob3 = oldSSI.getThirdDOB();
								String oldStrOtherDob3 = "";
								if (oldOtherDob3 != null)
								{
									oldStrOtherDob3 = dateFormat.format(oldOtherDob3);
								}
								String newSSIRef = "";
								String newBid  = "";
								String newAssisted = ""; 
								String newStrOsAmt = "";
								String newNpa = "";
								String newCovered = "";
								String newConst = "";
								String newSSIType = "";
								String newSSIName = "";
								String newRegNo = "";
								String newSSIItpan = "";
								String newIndNature = "";
								String newIndSector = "";
								String newActType = "";
								String newNoOfEmp = "";
								String newStrTurnover = "";
								String newStrExports = "";
								String newAddress = "";
								String newState = "";
								String newDistrict = "";
								String newCity = "";
								String newPincode = "";
								String newCpTitle = "";
								String newCpFirstName = "";
								String newCpMiddleName = "";
								String newCpLastName = "";
								String newCpGender = "";
								String newCpItpan = "";
								String newStrDob = "";
								String newSocialCat = "";
								String newLegalId = "";
								String newLegalValue = "";
								String newOtherName1 = "";
								String newOtherItpan1 = "";
								String newStrOtherDob1 = "";
								String newOtherName2 = "";
								String newOtherItpan2 = "";
								String newStrOtherDob2 = "";
								String newOtherName3 = "";
								String newOtherItpan3 = "";
								String newStrOtherDob3 = "";
								if (newBorrower != null)
								{
									newAssisted = newBorrower.getAssistedByBank();
									double newOsAmt = newBorrower.getOsAmt();
									if (newSSI != null)
									{
									newSSIRef = ""+newSSI.getBorrowerRefNo();
									newBid  = newSSI.getCgbid();
									newStrOsAmt = df.format(newOsAmt);
									newNpa = newBorrower.getNpa();
									newCovered = newBorrower.getPreviouslyCovered();
									newConst = newSSI.getConstitution();
									newSSIType = newSSI.getSsiType();
									newSSIName = newSSI.getSsiName();
									newRegNo = newSSI.getRegNo();
									newSSIItpan = newSSI.getSsiITPan();
									newIndNature = newSSI.getIndustryNature();
									newIndSector = newSSI.getIndustrySector();
									newActType = newSSI.getActivityType();
									newNoOfEmp = ""+newSSI.getEmployeeNos();
									double newTurnover = newSSI.getProjectedSalesTurnover();
									newStrTurnover = df.format(newTurnover);
									double newExports = newSSI.getProjectedExports();
									newStrExports = df.format(newExports);
									newAddress = newSSI.getAddress();
									newState = newSSI.getState();
									newDistrict = newSSI.getDistrict();
									newCity = newSSI.getCity();
									newPincode = newSSI.getPincode();
									newCpTitle = newSSI.getCpTitle();
									newCpFirstName = newSSI.getCpFirstName();
									newCpMiddleName = newSSI.getCpMiddleName();
									newCpLastName = newSSI.getCpLastName();
									newCpGender = newSSI.getCpGender();
									newCpItpan = newSSI.getCpITPAN();
									Date newCpDob = newSSI.getCpDOB();
									if (newCpDob != null)
									{
										newStrDob = dateFormat.format(newCpDob);
									}
									if(newSSI.getSocialCategory()!=null)
									{
										newSocialCat = newSSI.getSocialCategory();
									}
									if(newSSI.getCpLegalID()!=null)
									{
										newLegalId = newSSI.getCpLegalID();
									}
									if(newSSI.getCpLegalIdValue()!=null)
									{
										newLegalValue = newSSI.getCpLegalIdValue();
									}
									if(newSSI.getFirstName()!=null)
									{
										newOtherName1 = newSSI.getFirstName();
									}
									if(newSSI.getFirstItpan()!=null)
									{
										newOtherItpan1 = newSSI.getFirstItpan();
									}
									Date newOtherDob1 = newSSI.getFirstDOB();
									if (newOtherDob1 != null)
									{
										newStrOtherDob1 = dateFormat.format(newOtherDob1);
									}
									if(newSSI.getSecondName()!=null)
									{
										newOtherName2 = newSSI.getSecondName();
									}
									if(newSSI.getSecondItpan()!=null)
									{
										newOtherItpan2 = newSSI.getSecondItpan();
									}
									Date newOtherDob2 = newSSI.getSecondDOB();
									if (newOtherDob2 != null)
									{
										newStrOtherDob2 = dateFormat.format(newOtherDob2);
									}
									if(newSSI.getThirdName()!=null)
									{
										newOtherName3 = newSSI.getThirdName();
									}
									if(newSSI.getThirdItpan()!=null)
									{
										newOtherItpan3 = newSSI.getThirdItpan();
									}
									Date newOtherDob3 = newSSI.getThirdDOB();
									if (newOtherDob3 != null)
									{
										newStrOtherDob3 = dateFormat.format(newOtherDob3);
									}
									}
								}
							%>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><bean:message key="bankAssistance"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldAssisted%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newAssisted%>
								</TD>
							</tr>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><bean:message key="osAmt"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldStrOsAmt%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newStrOsAmt%>
								</TD>
							</tr>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><bean:message key="npa"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldNpa%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newNpa%>
								</TD>
							</tr>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><bean:message key="coveredByCGTSI"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldCovered%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newCovered%>
								</TD>
							</tr>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><bean:message key="constitution"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldConst%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newConst%>
								</TD>
							</tr>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><bean:message key="unitName"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldSSIType%>&nbsp;<%=oldSSIName%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newSSIType%>&nbsp;<%=newSSIName%>
								</TD>
							</tr>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><bean:message key="ssiRegNo"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldRegNo%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldRegNo%>
								</TD>
							</tr>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><bean:message key="firmItpan"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldSSIItpan%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newSSIItpan%>
								</TD>
							</tr>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><bean:message key="industryNature"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldIndNature%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newIndNature%>
								</TD>
							</tr>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><bean:message key="industrySector"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldIndSector%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newIndSector%>
								</TD>
							</tr>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><bean:message key="activitytype"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldActType%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newActType%>
								</TD>
							</tr>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><bean:message key="noOfEmployees"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldNoOfEmp%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newNoOfEmp%>
								</TD>
							</tr>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><bean:message key="turnover"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldStrTurnover%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newStrTurnover%>
								</TD>
							</tr>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><bean:message key="exports"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldStrExports%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newStrExports%>
								</TD>
							</tr>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><bean:message key="address"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldAddress%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newAddress%>
								</TD>
							</tr>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><bean:message key="state"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldState%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newState%>
								</TD>
							</tr>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><bean:message key="district"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldDistrict%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newDistrict%>
								</TD>
							</tr>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><bean:message key="city"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldCity%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newCity%>
								</TD>
							</tr>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><bean:message key="pinCode"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldPincode%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newPincode%>
								</TD>
							</tr>
							<TR align="left">
								<TD align="left" valign="top" class="ColumnBackground" colspan="3">
								<bean:message key="chiefInfo"/>
								</TD>
							</TR>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><bean:message key="title"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldCpTitle%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newCpTitle%>
								</TD>
							</tr>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><bean:message key="firstName"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldCpFirstName%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newCpFirstName%>
								</TD>
							</tr>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><bean:message key="middleName"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldCpMiddleName%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newCpMiddleName%>
								</TD>
							</tr>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><bean:message key="lastName"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldCpLastName%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newCpLastName%>
								</TD>
							</tr>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><bean:message key="gender"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldCpGender%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newCpGender%>
								</TD>
							</tr>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><bean:message key="chiefItpan"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldCpItpan%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newCpItpan%>
								</TD>
							</tr>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><bean:message key="dob"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldStrDob%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newStrDob%>
								</TD>
							</tr>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><bean:message key="socialCategory"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldSocialCat%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newSocialCat%>
								</TD>
							</tr>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><bean:message key="legalid"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldLegalId%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newLegalId%>
								</TD>
							</tr>
							<TR align="left">
								<TD align="left" valign="top" class="ColumnBackground" colspan="3"><bean:message key="otherPromoters" />	
								</TD>
							</TR>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center>1.<bean:message key="promoterName"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldOtherName1%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newOtherName1%>
								</TD>
							</tr>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><bean:message key="promoterItpan"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldOtherItpan1%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newOtherItpan1%>
								</TD>
							</tr>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><bean:message key="promoterDob"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldStrOtherDob1%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newStrOtherDob1%>
								</TD>
							</tr>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center>2.<bean:message key="promoterName"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldOtherName2%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newOtherName2%>
								</TD>
							</tr>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><bean:message key="promoterItpan"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldOtherItpan2%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newOtherItpan2%>
								</TD>
							</tr>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><bean:message key="promoterDob"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldStrOtherDob2%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newStrOtherDob2%>
								</TD>
							</tr>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center>3.<bean:message key="promoterName"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldOtherName3%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newOtherName3%>
								</TD>
							</tr>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><bean:message key="promoterItpan"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldOtherItpan3%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newOtherItpan3%>
								</TD>
							</tr>
							<tr>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><bean:message key="promoterDob"/>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=oldStrOtherDob3%>
								</TD>
								<TD align="center" valign="middle" class="TableData"
								  align="center">
								  <center><%=newStrOtherDob3%>
								</TD>
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
													<A href="javascript:window.history.back()">
													<IMG src="images/Back.gif" alt="Cancel" width="49" height="37" border="0"></A>
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
			</body>
		</TABLE>
