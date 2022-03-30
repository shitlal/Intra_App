<%@ page language="java"%>
<%@ page import="java.util.ArrayList"%>
<%@page import ="java.text.SimpleDateFormat"%>

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
									<TD colspan="14"> 
										<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
											<tr>
                          <td colspan="6" class="Heading1" align="center"><u>Credit Guarantee Fund Trust for Micro and Small Enterprises&nbsp;</u></td>
                      </tr>
                      <tr> <td colspan="6">&nbsp;</td></tr>
                      
                      <TR>
												<TD width="18%" class="Heading">MLI CHECKER ID LIST</TD>
												<td class="Heading" width="40%">&nbsp;<bean:message key="from"/> </td>
                        <TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
											</TR>
											<TR>
												<TD colspan="3" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
											</TR>
        			</TABLE>
									</TD>

									<TR>
                  <td width="3%" align="left" valign="top" class="ColumnBackground">
                  <bean:message key="sNo"/></td>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<bean:message key="bankName" />
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
									Member Id
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										 <bean:message key="zoneName" />
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
								  checker First Name
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
									checker Middle Name
									</TD>
									<TD width="15%" align="left" valign="top" class="ColumnBackground">
										checker Last Name
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
										Checker Emp Id
									</TD>
									<TD width="15%" align="left" valign="top" class="ColumnBackground">
									Checker Designation									</TD>
                  <TD width="20%" align="left" valign="top" class="ColumnBackground">
									Checker Phone number		
									</TD>
								<!--	<TD width="10%" align="left" valign="top" class="ColumnBackground">
										<bean:message key="dateofreporting" />
									</TD> -->
									<TD width="10%" align="right" valign="top" class="ColumnBackground">
										Checker Email Id
									</TD>
                  
                  	                 <TD width="10%" align="right" valign="top" class="ColumnBackground">
										Checker Id
									</TD>
									
										<TD width="10%" align="right" valign="top" class="ColumnBackground">
										Date Modified
									</TD>
									</TR>	
	
								<%
										double amount = 0;
										double totalAmount = 0;
									%>

									<tr>
								<logic:iterate name="rsForm" id="object" property="danRaised" indexId="index">
               	<%
									     com.cgtsi.reports.GeneralReport danReport = (com.cgtsi.reports.GeneralReport)object;
							  %>
										<TR>
                      <td align="left" valign="top" class="ColumnBackground1"><%=Integer.parseInt(index+"")+1%></td>
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">						
											<%=danReport.getBankName()%> 
											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">						
											<%   String memId =  danReport.getMemberId();
                     	if((memId == null)||(memId.equals("")))
                      {
                       %>
                       <%=""%>
                       <% } else
											{
											%>
											<%=memId%>
											<%
											}
                      %> 
											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">						
											
                      <%   String zoneName =  danReport.getZoneName();
                     	if((zoneName == null)||(zoneName.equals("")))
                      {
                       %>
                       <%=""%>
                       <% } else
											{
											%>
											<%=zoneName%>
											<%
											}
                      %> 
											</TD>
                      
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">						
												<%= danReport.getEmpFName() %>
               			</TD>
               			
               			
               			
               			
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">	
											<%String midName= danReport.getEmpMName();
											if((midName == null)||(midName.equals("")))
											{
											%>
											<%=""%>
											<%
											}
											else
											{
											%>
											<%=midName%>
											<%
											}
											%>
											</TD>
							
                     <TD width="10%" align="left" valign="top" class="ColumnBackground1">	
											<%String lname= danReport.getEmpLName();
											if((lname == null)||(lname.equals("")))
											{
											%>
											<%=""%>
											<%
											}
											else
											{
											%>
											<%=lname%>
											<%
											}
											%>
											</TD>
                 
			            		
                     <TD width="10%" align="left" valign="top" class="ColumnBackground1">	
											<%String eid= danReport.getEmpId();
											if((eid == null)||(eid.equals("")))
											{
											%>
											<%=""%>
											<%
											}
											else
											{
											%>
											<%=eid%>
											<%
											}
											%>
											</TD>
											
											
													
                     <TD width="10%" align="left" valign="top" class="ColumnBackground1">	
											<%String designation= danReport.getDesignation();
											if((designation == null)||(designation.equals("")))
											{
											%>
											<%=""%>
											<%
											}
											else
											{
											%>
											<%=designation%>
											<%
											}
											%>
											</TD>
											
											 <TD width="10%" align="left" valign="top" class="ColumnBackground1">	
											<%String phono= danReport.getPhoneNo();
											if((phono == null)||(phono.equals("")))
											{
											%>
											<%=""%>
											<%
											}
											else
											{
											%>
											<%=phono%>
											<%
											}
											%>
											</TD>
											
											
											 <TD width="10%" align="left" valign="top" class="ColumnBackground1">	
											<%String emailid= danReport.getEmailId();
											if((emailid == null)||(emailid.equals("")))
											{
											%>
											<%=""%>
											<%
											}
											else
											{
											%>
											<%=emailid%>
											<%
											}
											%>
											</TD>
                <%--   <TD width="10%" align="left" valign="top" class="ColumnBackground1">						
											 <% java.util.Date reportingDate=danReport.getDateOfTheDocument10();
													String reportingDt = null;
													if(reportingDate != null)
													{
														 reportingDt=dateFormat.format(reportingDate);
													}
													else
													{
														 reportingDt = "";
													}
											%>
											<%=reportingDt%>
			             </TD> --%>
                   
                      <TD width="20%" align="left" valign="top" class="ColumnBackground1">						
											<%String userid= danReport.getUserId();
											if((userid == null)||(userid.equals("")))
											{
											%>
											<%=""%>
											<%
											}
											else
											{
											%>
											<%=userid%>
											<%
											}
											%>
                 			</TD>
                 			
                 			        			
                 		
                 			
                 			 <TD width="20%" align="left" valign="top" class="ColumnBackground1">						
											<%String EmployeeId= danReport.getEmployeeId();
											if((EmployeeId == null)||(EmployeeId.equals("")))
											{
											%>
											<%=""%>
											<%
											}
											else
											{
											%>
											<%=EmployeeId%>
											<%
											}
											%>
                 			</TD>
                 			
                 			  <TD width="20%" align="left" valign="top" class="ColumnBackground1">						
											<%String checkerid= danReport.getCheckerId();
											if((checkerid == null)||(checkerid.equals("")))
											{
											%>
											<%=""%>
											<%
											}
											else
											{
											%>
											<%=checkerid%>
											<%
											}
											%>
                 			</TD>
                 			
                 			 <TD width="20%" align="left" valign="top" class="ColumnBackground1">						
											<%String datemodify= danReport.getDateModify();
											if((datemodify == null)||(datemodify.equals("")))
											{
											%>
											<%=""%>
											<%
											}
											else
											{
											%>
											<%=datemodify%>
											<%
											}
											%>
                 			</TD>
                    
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
						<TD align="center" valign="baseline" >
							<DIV align="center">
								 <A href="home.do?method=getMainMenu&menuIcon=RS&mainMenu=List of MLIs">
                                    <IMG src="images/OK.gif" alt="OK" width="49" height="37" border="0"></A>
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

