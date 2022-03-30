<%@ page language="java"%>
<%@ page import="java.util.ArrayList"%>

<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic" %>
<%@ include file="/jsp/SetMenuInfo.jsp" %>
<% session.setAttribute("CurrentPage","minoritystateprogressReport.do?method=minoritystateprogressReport");%>
<%@page import ="java.text.DecimalFormat"%>
<%DecimalFormat decimalFormat = new DecimalFormat("##########.#####");%>


<%

int grandTotal=0;


double grandTotalAmount=0.0;


%>

<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form action="minoritystateprogressReport.do?method=minoritystateprogressReport" method="POST" enctype="multipart/form-data">
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
									<TD colspan="10"> 
										<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
											<tr>
                          <td colspan="6" class="Heading1" align="center"><u><bean:message key="reportHeader"/></u></td>
                      </tr>
                      <tr> <td colspan="6">&nbsp;</td></tr>
                      <TR>
												<TD width="16%" class="Heading"> State Wise Category Report </TD>
												<td class="Heading" width="66%">&nbsp;<bean:message key="from"/> <bean:write  name="rsForm" property="dateOfTheDocument"/>&nbsp;<bean:message key="to"/> <bean:write  name="rsForm" property="dateOfTheDocument1"/></td>
                        <TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
                          <td width="20%" class="ColumnBackground "><div align="center"> </div></td>
											</TR>
											<TR>
												<TD colspan="3" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
											</TR>
                    </TABLE>
									</TD>




<tr> 
				<td colspan="12">
            <table width="100%" border="0" cellspacing="1" cellpadding="0">
             <tr> 
                  <td width="3%"  align="center" valign="middle" class="ColumnBackground "><div align="center">State Name</div></td>
                  <td width="10%" class="ColumnBackground "><div align="center">Category </div></td>
                  <td width="5%" class="ColumnBackground "><div align="center">Number </div></td>
                  <td class="ColumnBackground"  width="5%"> <div align="center">Approved Amount(LAKHS) </div></td>
                    <td width="20%" class="ColumnBackground "><div align="center"> </div></td>
            </tr> 
            
       </td>
        </tr>                       
        <tr>
        <td>






	
				<%
				int serial = 0;
        double total = 0;
				%>
        </td>
       </tr>
				<logic:iterate id="object" name="rsForm" property="categorystateprogressReport" indexId="index">
				<%
				com.cgtsi.reports.GeneralReport pReport = (com.cgtsi.reports.GeneralReport)object;
                                
                                
                                
                                
                                
                                 grandTotal=pReport.getTotalCount();
           
            grandTotalAmount=pReport.getTotalAmount();
        
        
				%>
        <tr class="tableData">
         
				 
				  <td><%= pReport.getStateName()%></td>
           <td><%= pReport.getCategory()%></td> 
           
            <td><%= pReport.getNumber()%></td> 
            
            
              <td><%= pReport.getAmount()%></td> 
              
              <td><div align="left">&nbsp;</div></td> 
           
           
           
         



          
          </div>
    	</logic:iterate>
         				
				</TABLE>
                                
                                
                                <table><tr><td class="ColumnBackground " colspan="6">TOTAL(in Lakhs)</td><td class="ColumnBackground " colspan="85"></td><td colspan="0"></td><td class="ColumnBackground " colspan="33"><%=grandTotal%></td>
          <td class="ColumnBackground " colspan="35"><%=grandTotalAmount%></td><td class="ColumnBackground " colspan="150"></td></tr></table>
				
	
        	</TD>
					</TR>
        
					<TR >
        </TABLE>
						<TD height="20">
							&nbsp;
						</TD>
					</TR>
					<TR >
						<TD align="center" valign="baseline">
							<DIV align="center">
								<A href="javascript:submitForm('minorityStateReport.do?method=minorityStateReport')">
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


