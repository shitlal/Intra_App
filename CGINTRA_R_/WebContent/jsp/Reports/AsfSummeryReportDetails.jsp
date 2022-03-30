<%@ page language="java"%>
<%@ page import="java.util.ArrayList"%>

<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic" %>
<%@ include file="/jsp/SetMenuInfo.jsp" %>
<%@page import ="java.text.DecimalFormat"%>
<%DecimalFormat decimalFormat = new DecimalFormat("##########.##");%>
<% session.setAttribute("CurrentPage","asfSummeryReport.do?method=asfSummeryReportDetails");%>
  AAAAAAAAAAAAA=========
<TABLE width="725" border="0" cellpadding="0" cellspacing="0">

	<html:errors />
	<html:form action="asfSummeryReport1.do?method=asfSummeryReportDetails" method="POST" enctype="multipart/form-data">
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
									<TD colspan="16"> 
										<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
											<tr>
                        <td colspan="6" class="Heading1" align="center"><u><!-- <bean:message key="reportHeader"/> -->Credit Guarantee Fund Trust 
For Micro and Small Enterprises </u>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="6">&nbsp;</td>
                      </tr>
                      
                      <TR>
												<TD class="Heading" width="42%">ASF Summary Report Details</TD>
                        <TD class="Heading" width="50%">&nbsp;<bean:message key="from"/><bean:write name="rsForm" property="dateOfTheDocument36"/>&nbsp;<bean:message key="to"/><bean:write name="rsForm" property="dateOfTheDocument37"/></TD>
												<TD><IMG src="images/TriangleSubhead.gif"  height="19"></TD>
											</TR>
											<TR>
												<TD colspan="3" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
											</TR>

										</TABLE>
									</TD>

									<TR>
              <% 
    ArrayList arraylist = null;
    String AsfStringArray[]=null;
    String size=(String)request.getAttribute("asfSummeryDetailsArray_size");
   int totalSum=0;
   int totalasfAmountDue=0;
   int totalNoOfASFAppropriated=0;
   int totalNoOfASFNotAppropriated=0;
   int totalAsfAmtNotAppropriated=0;
   int totalReceivedApp=0;
   
   double totalasfAmountDueNew=0.0;
   double totalReceivedAppNew=0.0;
   double totalAsfAmtNotAppropriatedNew=0.0;
  
    if(size=="0")
    {
    out.println("<tr><td class=\"Heading\" colspan=\"11\"><center>No Data Found</center</td></tr>");
    }
    if(size!=null && size!="0")
    {
    arraylist=(ArrayList)request.getAttribute("asfSummeryDetailsArray");
    %> 
	<%
		DecimalFormat decimalFormatNew = new DecimalFormat("##########");
	%>
                        <TR class="tableData">
						<th class="ColumnBackground">S. No.</th>
                         <th class="ColumnBackground">Bank Name</th>
                         <th class="ColumnBackground">Zone Name</th>
                        <th class="ColumnBackground">MLI ID</th>
                        <th class="ColumnBackground">No Of ASF Due</th>
                        <th class="ColumnBackground">ASF Amt due in Rs.</th>
                        <th class="ColumnBackground">No Of ASF Appropriated</th>
                        <th class="ColumnBackground">Received App in Rs.</th>
                        <th class="ColumnBackground">No Of ASF Not Appropriated</th>
                        <th class="ColumnBackground">ASF Amt Not appropriated in Rs.</th>
                        
                      </tr>
                      <%   for(int count=0;count<arraylist.size();count++)
    {
    
     int sum = 0;
      
      
      int asfAmountDue=0;
	 
	  
      int NoOfASFAppropriated=0;
      
      int AsfAmtNotAppropriated=0;
	  
     
      int ReceivedApp=0;
	  
      
      int NoOfASFNotAppropriated=0;
	  
	   double asfAmountDueNew=0.0;
	   double ReceivedAppNew=0.0;
	   double AsfAmtNotAppropriatedNew=0.0;
      
      
      AsfStringArray=new String[10];
      AsfStringArray=(String[])arraylist.get(count);
 	%>
                <tr>
                   <td class="ColumnBackground1">&nbsp;<%=count+1%></td>
                     <td class="ColumnBackground1" size="30">&nbsp;
                  <div align="left">
                    <%=AsfStringArray[7]%>
                    </div>
                  </td>  
                  <td class="ColumnBackground1" size="15">&nbsp;
                  <div align="left">
                   <% if(AsfStringArray[8]==null){
                         AsfStringArray[8]="";
                  }
                  %>
                    <%=AsfStringArray[8]%>
                    </div>
                  </td>
                  <td class="ColumnBackground1">
                    <% String reference=AsfStringArray[0];
                    String url = "asfSummeryReport1.do?method=asfSummeryMliwiseDetailsNew&num="+reference;	%>				
												<html:link href="<%=url%>"><%=reference%></html:link>
                  </td>
                  <td class="ColumnBackground1">
                  <div align="right">
                    <%sum= Integer.parseInt(AsfStringArray[1]);
                    totalSum = totalSum + sum;%>
                    <%=sum%>
                    </div>
                  </td>
                  <td class="ColumnBackground1">
                    <div align="right">
                    <%asfAmountDueNew= Double.parseDouble(AsfStringArray[2]);
                    totalasfAmountDueNew = totalasfAmountDueNew + asfAmountDueNew;%>
                    <%=asfAmountDueNew%>
                    </div>
                  </td>
                  <td class="ColumnBackground1">
                    <div align="right">
                    <%NoOfASFAppropriated= Integer.parseInt(AsfStringArray[3]);
                    totalNoOfASFAppropriated = totalNoOfASFAppropriated + NoOfASFAppropriated;%>
                    <%=NoOfASFAppropriated%>
                    </div>
                  </td>
                  <td class="ColumnBackground1">
                    <div align="right">
                    
					<%ReceivedAppNew= Double.parseDouble(AsfStringArray[4]);
                    totalReceivedAppNew = totalReceivedAppNew + ReceivedAppNew;%>
                    <%=ReceivedAppNew%>
                    </div>
                  </td>
                    <td class="ColumnBackground1">
                    <div align="right">
                    <%NoOfASFNotAppropriated= Integer.parseInt(AsfStringArray[6]);
                    totalNoOfASFNotAppropriated = totalNoOfASFNotAppropriated + NoOfASFNotAppropriated;%>
                    <%=NoOfASFNotAppropriated%>
                    </div>
                  </td>
                
                  <td class="ColumnBackground1">
                  <div align="right">
                    <%AsfAmtNotAppropriatedNew= Double.parseDouble(AsfStringArray[5]);
                    totalAsfAmtNotAppropriatedNew = totalAsfAmtNotAppropriatedNew + AsfAmtNotAppropriatedNew;%>
                    <%=AsfAmtNotAppropriatedNew%>
					
					
                    </div>
                  </tr>
                <%  } %>
                <tr>
					<TD width="10%" align="left"  colspan="1" valign="top" class="ColumnBackground">&nbsp;Total</TD>
                                 	<TD width="10%" align="left"  colspan="1" valign="top" class="ColumnBackground">&nbsp;</td>
										
                     <TD width="10%" align="left"  valign="top" class="ColumnBackground">
                     &nbsp;
                      </TD>
                      <TD width="10%" align="left"  valign="top" class="ColumnBackground">
                     &nbsp;
                      </TD>
                      <TD width="10%" align="left"  valign="top" class="ColumnBackground">
                      <div align="right">
											  <%=totalSum%>
											  </div>
                      </TD>
                      <TD width="10%" align="left" valign="top" class="ColumnBackground">	
											<div align="right">
											  <%=decimalFormatNew.format(totalasfAmountDueNew)%>
											  </div>
											</TD>
                      <TD width="10%" align="left"  valign="top" class="ColumnBackground">
                      <div align="right">
                      <%=totalNoOfASFAppropriated%>
                      </div>
                      </TD>
                      <TD width="10%" align="left"  valign="top" class="ColumnBackground">
                      <div align="right">
                      <%=decimalFormatNew.format(totalReceivedAppNew)%>
                      </div>
                      </TD>
                       <TD width="10%" align="left"  valign="top" class="ColumnBackground">
                      <div align="right">
                      <%=totalNoOfASFNotAppropriated%>
                      </div>
                      </TD>
                      <TD width="10%" align="left"  valign="top" class="ColumnBackground">
                      <div align="right">
                      <%=decimalFormatNew.format(totalAsfAmtNotAppropriatedNew)%>
                      </div>
                      </TD>
                      </TR>
         <% }%>
          
          </TR>
							</TABLE>
						</TD>
					</TR>
					<TR >
						<TD height="20" >
							&nbsp;
						</TD>
					</TR>
					<tr><td colspan="3" align="left" width="700"><font size="2" color="red">Report Generated On : 
					<% java.util.Date loggedInTime=new java.util.Date();
			          java.text.SimpleDateFormat dateFormat1=new java.text.SimpleDateFormat("dd MMMMM yyyy ':' HH.mm");
			          String date1=dateFormat1.format(loggedInTime);
					  out.println(date1);
					  %> hrs.</font></td></tr>
					<TR >
						<TD align="center" valign="baseline" >
							<DIV align="center">
						
								<A href="javascript:history.back()">
									<IMG src="images/Back.gif" alt="Back" width="49" height="37" border="0"></A>
									
									<A href="javascript:printpage()">
									<IMG src="images/Print.gif" alt="Print" width="49" height="37" border="0"></A>
								
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