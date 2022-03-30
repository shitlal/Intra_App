<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic" %>

<%@ include file="/jsp/SetMenuInfo.jsp" %>
<% session.setAttribute("CurrentPage","showClaimMliListPathForNewCases.do?method=showClaimMliListPathForNewCases");   %>

<TABLE  width="800" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	
<% 
	int total=0; 	
    String[] totcnt = new String[3];     
	    
	%>
	
	<html:form action="showClaimMliListPathForNewCases.do?method=showClaimMliListPathForNewCases" method="POST" enctype="multipart/form-data">
	
	 
	
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
									<TD colspan="4"> 
										<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
											<TR>
												<TD width="59%" class="Heading">MLI Wise Claim Applications</TD>
												<TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
											</TR>
											<TR>
												<TD colspan="3" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
											</TR>

										</TABLE>
									</TD>
								</TR>

							<TR align="left" valign="top">
									<TD align="left" valign="top" class="ColumnBackground">
										&nbsp;S.No
									</TD>
									<TD align="left" valign="top" class="ColumnBackground">
										&nbsp;MLI Name
									</TD>
										<TD align="left" class="ColumnBackground"> 
									&nbsp;No Of Claims Upto 10 Lakhs
									</TD>
									<TD align="left" class="ColumnBackground"> 
									&nbsp;No Of Claims Above 10 Lakhs
									</TD>
									<TD align="left" class="ColumnBackground"> 
									&nbsp;No Of Claims Above 25Lakhs
									</TD>
									<TD align="left" class="ColumnBackground"> 
									&nbsp;Total Claims
									</TD>
									</TR>

									<%int i = 0;%>
									<tr>
									<logic:iterate id="object" name="rsForm" property="mli">
									<%
									     com.cgtsi.reports.GeneralReport list = (com.cgtsi.reports.GeneralReport)object;
									  
									    int bankCnt = list.getProposals();
									    total = total+bankCnt;
									%>
									
									<TR align="left" valign="top">
									<TD align="left" valign="top" class="ColumnBackground1">
										&nbsp;<%= ++i%>				
									</TD>
									<TD align="left" valign="top" class="ColumnBackground1">
										&nbsp;<%= list.getType()%>				
										<%String bank = list.getType();%>		
                    <%
                    
                     bank = bank.replaceAll("&","PATH");
                 
                    
                    %>
									</TD>
									
									
									<TD align="left" class="TableData"> 
									<div align="left">
									&nbsp;<%int office1 =  list.getProposals1();
									String url ="displayClaimApprovalNewForNewCases.do?method=displayClaimApprovalNewForNewCases&fileType=LTE10L&Link="+bank;%>
								<%if( 
								    // (bank.equals("ALLAHABAD BANK"))
              						//			 || 
									// (bank.equals("KERALA FINANCIAL CORPORATION"))
										//		 ||  (bank.equals("LAKSHMI VILAS BANK"))
									 (bank.equals("VILAS BANK"))									 
									 )
								{%>
									<%= office1%>
								<%}else{%>
									<html:link href="<%=url%>"><%= office1%></html:link>
								<%}%>
									</div>
									</TD>
									
								
									<TD align="left" class="TableData"> 
									<div align="left">
									&nbsp;<%int office =  list.getProposals();
									String url1 ="displayClaimApprovalNewForNewCases.do?method=displayClaimApprovalNewForNewCases&fileType=GTE10LTo25L&Link="+bank;%>
									
								<%if( 
								    // (bank.equals("ALLAHABAD BANK")) 
									 //       || 
									(bank.equals("KERALA FINANCIAL CORPORATION"))
									        ||
									 (bank.equals("LAKSHMI VILAS BANK"))
									){%>
									<%= office%>
								<%}else{%>
									<html:link href="<%=url1%>"><%= office%></html:link>
								<%}%>
									
									
									</div>
									</TD>
								
									<TD align="left" class="TableData"> 
									<div align="left">
									&nbsp;<%int office2 =  list.getProposals2();
									String url2 ="displayClaimApprovalNewForNewCases.do?method=displayClaimApprovalNewForNewCases&fileType=GTE25L&Link="+bank;%>
									
								<%if( 
								   // (bank.equals("ALLAHABAD BANK")) 
									//      ||
								    (bank.equals("KERALA FINANCIAL CORPORATION"))
									      ||
									 (bank.equals("LAKSHMI VILAS BANK"))
									){%>
									<%= office2%>
								<%}else{%>
									<html:link href="<%=url2%>"><%= office2%></html:link>
								<%}%>
									
									
									</div>
									</TD>
									<TD align="left" class="TableData">
									<div align="left">
									&nbsp;<%int office3 =  list.getProposals3();%>								
								    &nbsp;<b><%=office3%></b>
									</div>
									</TD>
									</TR>
									
									</logic:iterate>
								
									<TR>
									    	<TD colspan=2 align="right" valign="top" class="TableData">
											<%int j = 0;%>
											<logic:iterate id="object" name="rsForm" property="totcnt"><%										
										     totcnt[j] = (String)object;
										     j++;%>										
									        </logic:iterate>									        
											<font face="arial" size=2 ><b>Total Count of Claim's </b></font></TD>
											
									
											
											
											<TD class="TableData" ><font face="arial" size=2 ><b><%=totcnt[0]%></b></font></TD>
											<TD class="TableData" ><font face="arial" size=2 ><b><%=totcnt[1]%></b></font></TD>
											<TD class="TableData" ><font face="arial" size=2 ><b><%=totcnt[2]%></b></font></TD>
									
											
											
											<TD class="TableData" ><font face="arial" size=2 ><b><%=Integer.parseInt(totcnt[0])+Integer.parseInt(totcnt[1])+Integer.parseInt(totcnt[2])%></b></font></TD>
									</TR>
									
							</TABLE>
						</TD>
					</TR>
					<TR >
						<!--<TD height="20" >
							&nbsp;
						</TD>
					--></TR>
													
					<TR>					
						<TD align="center" valign="baseline">							
							<DIV align="center">
								<A href="home.do?method=getMainMenu&menuIcon=<%=session.getAttribute("menuIcon")%>&mainMenu=<%=session.getAttribute("mainMenu")%>">
									<IMG src="images/OK.gif" alt="OK" width="49" height="37" border="0"></A>
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


