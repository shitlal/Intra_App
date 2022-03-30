<%@ page language="java"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import ="java.text.SimpleDateFormat"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic" %>
<%@ include file="/jsp/SetMenuInfo.jsp" %>
<% session.setAttribute("CurrentPage","npaReport.do?method=npaReport");%>
<%SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");%>
<%@page import ="java.text.DecimalFormat"%>
<%DecimalFormat decimalFormat = new DecimalFormat("#########0.0##");%>

<TABLE width="800" border="0" cellpadding="0" cellspacing="0">
<html:errors />
<html:form action="npaReport.do?method=npaReport" method="POST" enctype="multipart/form-data">
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
                                <TD colspan="14"> 
                                    <TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
                                    <tr>
										<td colspan="6" class="Heading1" align="center">
											<u>Credit Guarantee Fund Trust for Micro and Small Enterprises&nbsp;</u>
										</td>
									</tr>
									<tr><td colspan="6">&nbsp;</td></tr>
									<TR>
                                        <TD width="18%" class="Heading">NPA Report Details</TD>
                                        <td class="Heading" width="40%">&nbsp;<bean:message key="from"/> 
													<bean:write  name="rsForm" property="dateOfTheDocument26"/>&nbsp;<bean:message key="to"/> 
													<bean:write  name="rsForm" property="dateOfTheDocument27"/>
										</td>
										<TD><IMG src="images/TriangleSubhead.gif"  alt="" width="19" height="19"></TD>
                                    </TR>
                                    <TR>
                                        <TD colspan="3" class="Heading"><IMG src="images/Clear.gif" alt="" width="5" height="5"></TD>
                                    </TR>
									</TABLE>
                                </TD>
				</tr>
				<TR>
				<td width="3%" align="left" valign="top" class="ColumnBackground"><bean:message key="sNo"/></td>
                                <TD width="10%" align="left" valign="top" class="ColumnBackground">
                                    <bean:message key="bankName" />
                                </TD>
                                <TD width="10%" align="left" valign="top" class="ColumnBackground">
                                    <bean:message key="zoneName" />
                                </TD>
                                <TD width="10%" align="left" valign="top" class="ColumnBackground">
                                    <bean:message key="CBbranchName" />
                                </TD>
                                <TD width="10%" align="left" valign="top" class="ColumnBackground">
                                    <bean:message key="mliID" />
                                </TD>
                                <TD width="10%" align="left" valign="top" class="ColumnBackground">
                                                                 <bean:message key="unitNameExisting" />
                                </TD>
                                <TD width="15%" align="left" valign="top" class="ColumnBackground">
                                                                        <bean:message key="cgpanNumber" />
                                </TD>
                                <TD width="10%" align="left" valign="top" class="ColumnBackground">
                                                                        <bean:message key="status" />
                                </TD>
                                <TD width="15%" align="left" valign="top" class="ColumnBackground">
                                                                 Expiry Date
                                </TD>
								<TD width="20%" align="left" valign="top" class="ColumnBackground">
                                                                 <bean:message key="dateofnpa" />
                                </TD>
                                <TD width="10%" align="left" valign="top" class="ColumnBackground">
                                                                        <bean:message key="dateofreporting" />
                                </TD>
                                <TD width="10%" align="right" valign="top" class="ColumnBackground">
                                                                        <bean:message key="reasonfornpa" />
                                </TD>
								<TD width="10%" align="right" valign="top" class="ColumnBackground">
                                                                        <bean:message key="pplOS" />
                                </TD>
								<TD width="10%" align="right" valign="top" class="ColumnBackground">
                                                                        <bean:message key="approvedAmount" />
                                </TD>
                            </TR>	
                                                                <%
                                                                        double amount = 0.0;
                                                                        double totalAmount = 0.0;
                                                                        String expiryDateStr = "";
                                                                        String npaDateStr = "";
                                                                        String reportingDateStr = "";
                                                                        String bankName = "";
                                                                        String zoneName = "";
                                                                        String name = "";
                                                                        String memberId = "";
                                                                        String ssiName = "";
                                                                        String cgpan = "";
                                                                        String status = "";
                                                                        String subject = "";                                                                       
                                                                %>
                            <logic:iterate name="rsForm" id="object" property="danRaised" indexId="index">
				<%
                                       com.cgtsi.reports.GeneralReport danReport = (com.cgtsi.reports.GeneralReport)object;
                                       bankName = danReport.getBankName();
                                       zoneName = danReport.getZoneName();
                                       name = danReport.getName();
                                       memberId = danReport.getMemberId();
                                       ssiName = danReport.getSsiName();
                                       cgpan = danReport.getCgpan();
                                       status = danReport.getStatus();
                                       subject = danReport.getSubject();
                                       amount = danReport.getAmount();
                                       totalAmount = danReport.getCumAmount();
				%>
                            <TR  class="ColumnBackground1">
				<td align="left" valign="top"><%=Integer.parseInt(index+"")+1%></td>
                                <TD width="10%" align="left" valign="top"><%=bankName%></TD>
				<TD width="10%" align="left" valign="top"><%=zoneName%></TD>
                                <TD width="10%" align="left" valign="top"><%=name%></TD>
                                <TD width="10%" align="left" valign="top"><%=memberId%></TD>
                                <TD width="10%" align="left" valign="top"><%=ssiName%></TD>
                                <TD width="15%" align="left" valign="top"><%=cgpan%></TD>
                                <TD width="15%" align="left" valign="top"><%=status%></TD>
										<% java.util.Date expiryDate=danReport.getDateOfTheDocument();
											expiryDateStr = "";
											if(expiryDate != null)
											{
												expiryDateStr=dateFormat.format(expiryDate);
											}
											java.util.Date npaDate=danReport.getDateOfTheDocument1();
                                            npaDateStr = "";
                                            if(npaDate != null)
                                            {
                                                npaDateStr=dateFormat.format(npaDate);
                                            }
                                            java.util.Date reportingDate=danReport.getDateOfTheDocument10();
                                            reportingDateStr = "";
                                            if(reportingDate != null)
                                            {
                                                reportingDateStr=dateFormat.format(reportingDate);
                                            }
										%>
								<TD width="10%" align="left" valign="top"><%=expiryDateStr%></TD>
								<TD width="10%" align="left" valign="top"><%=npaDateStr%></TD>
								<TD width="10%" align="left" valign="top"><%=reportingDateStr%></TD>
								<TD width="20%" align="left" valign="top"><%=subject%></TD>
								<TD width="10%" align="left" valign="top"><%=amount%></TD>
								<TD width="10%" align="left" valign="top"><%=totalAmount%></TD>
                                </TR>
                            </logic:iterate>
                            </TABLE>
                            </TD>
                    </TR>
                    <TR>
                                        <TD height="20" >
                                                &nbsp;
                                        </TD>
                    </TR>
                    <TR>
                                        <TD align="center" valign="baseline">
                                                <DIV align="center">
                                                        <A href="javascript:submitForm('npaReportInput.do?method=npaReportInput')">
                                                            <IMG src="images/Back.gif" alt="Back" width="49" height="37" border="0">
														</A>	
														<A href="javascript:printpage()">
															<IMG src="images/Print.gif" alt="Print" width="49" height="37" border="0">
                                                        </A>             
													<!-- <IMG src="images/Print.gif" alt="Print" width="49" height="37" border="0"></A> -->
                                                        
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
                <TD width="20" align="right" valign="top"><IMG src="images/TableLeftBottom1.gif"  alt="" width="20" height="15"></TD>
                <TD background="images/TableBackground2.gif">&nbsp;</TD>
                <TD width="20" align="left" valign="top"><IMG src="images/TableRightBottom1.gif" alt="" width="23" height="15"></TD>
        </TR>
</html:form>
</TABLE>
