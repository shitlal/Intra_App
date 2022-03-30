<%@ page language="java"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.cgtsi.actionform.*"%>
<%@page import="java.util.HashSet"%>
<%@page import ="java.text.SimpleDateFormat"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic" %>
<%@ include file="/jsp/SetMenuInfo.jsp" %>
<%@page import ="java.text.DecimalFormat"%>
<%@ page import="com.cgtsi.claim.ClaimConstants"%>
<% session.setAttribute("CurrentPage","cgpanForReductionprocess.do?method=cgpanForReductionprocess");%>
	

	<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
    <html:errors />
	<html:form action="cgpanForReductionprocess.do?method=cgpanForReductionprocess" method="POST" enctype="multipart/form-data">
	<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
	   <TR> 
			<TD width="20" align="right" valign="bottom"><IMG src="images/TableLeftTop.gif" width="20" height="31"></TD>
			<TD background="images/TableBackground1.gif"></TD>
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
									
                        
                      </TR>
           		 </TABLE>
            
	
			<tr>
		
                  
                  
                  <td valign="top"  class="HeadingBg"><div align="center">&nbsp;&nbsp;<strong>CGPAN</strong></div></td>
                  <td valign="top" class="HeadingBg"><div align="center">&nbsp;&nbsp;<strong>WCP FB LIMIT SANCTIONED</strong></div></td>
                      
                       <td valign="top" class="HeadingBg"><div align="center">&nbsp;&nbsp;
                      <strong>WCP NFB LIMIT SANCTIONED</strong><br></div></td>
                   
                     <td valign="top" class="HeadingBg"><div align="center">&nbsp;&nbsp;<strong>WCP FB CREDIT TO GUARANTEE<br>
                      </strong><br></div></td>
                      
                      <td valign="top" class="HeadingBg"><div align="center">&nbsp;&nbsp;<strong>WCP NFB CREDIT TO GUARANTEE
                      </strong><br></div></td>
                      
                  
		        </tr>
		        

	
								
								
							 <TR>
							  <%
								int j=0;
                                int k=0;
                                
								%>
							   
			<logic:iterate id="object" name="rsForm" property="cgpanHistoryReport" indexId="id">

				<% com.cgtsi.reports.ApplicationReport dReport =  (com.cgtsi.reports.ApplicationReport)object;%>
				

									<TR align="left" valign="top">
							     <TD  align="left" valign="top" class="ColumnBackground1">
                   <%=dReport.getCGPAN() %>&nbsp;
                   </TD>
                 
                    <TD  align="left" valign="top" class="ColumnBackground1">
                   &nbsp;<%=dReport.getWCP_FB_LIMIT_SANCTIONED()%>
                   </TD>
                    <TD  align="left" valign="top" class="ColumnBackground1">
                   &nbsp;<%=dReport.getWCP_NFB_LIMIT_SANCTIONED()%>
                   </TD>
                   
                    <TD  align="left" valign="top" class="ColumnBackground1">
                   &nbsp;<%=dReport.getWCP_FB_CREDIT_TO_GUARANTEE()%>
                   </TD>
                    <TD  align="left" valign="top" class="ColumnBackground1">
                   &nbsp;<%=dReport.getWCP_NFB_CREDIT_TO_GUARANTEE()%>
                   </TD>
                   
                  
                   
                  </tr>
                <%j++; %>
               <%k++; %>
                 </logic:iterate>
				   
				
	           	    
           
              

     

                                             <TD width="10%" align="center" valign="top" class="ColumnBackground1">						
											</TD>
											
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">						

											</TD>
											
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">						

											</TD>
											
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">			
											
											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">			
											
											</TD>
											
										
											
												        
          </tr>
		 </TABLE>	
		    &nbsp;     &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;    &nbsp;   &nbsp;   &nbsp;
		       &nbsp;     &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;    &nbsp;   &nbsp;   &nbsp;
		          &nbsp;     &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;   &nbsp;    &nbsp;   &nbsp;   &nbsp;
		 
		 <TABLE width="725" border="0" cellpadding="0" cellspacing="0">
			<tr>
		
                  
                  
                  <td valign="top"  class="HeadingBg"><div align="center">&nbsp;&nbsp;<strong>CGPAN</strong></div></td>
                  <td valign="top" class="HeadingBg"><div align="center">&nbsp;&nbsp;<strong>WCP FB LIMIT SANCTIONED</strong></div></td>
                      
                       <td valign="top" class="HeadingBg"><div align="center">&nbsp;&nbsp;<strong>WCP NFB LIMIT SANCTIONED</strong><br></div></td>
                   
                     <td valign="top" class="HeadingBg"><div align="center">&nbsp;&nbsp;<strong>WCP FB CREDIT TO GUARANTEE<br>
                      </strong><br></div></td>
                      
                      <td valign="top" class="HeadingBg"><div align="center">&nbsp;&nbsp;<strong>WCP NFB CREDIT TO GUARANTEE
                      </strong><br></div></td>
                      
                         <td valign="top"  class="HeadingBg"><div align="center">&nbsp;&nbsp;<strong>APP REMARKS<br>
                      </strong></div></td>	  
                      
                  
		        </tr>
	
		         <TR>
						 <%
								int l=0;
                                int m=0;
                                
								%>	   
			<logic:iterate id="object" name="rsForm" property="cgpanHistoryReport" indexId="id">

				<% com.cgtsi.reports.ApplicationReport dReport =  (com.cgtsi.reports.ApplicationReport)object;%>
				
				
				 <%
				               String wcpfblimitsanctioned=null;
				               String  wcpnfblimitsanctioned=null;
				               String wcpfbcredittoguarantee=null;
				               String wcpnfbcredittoguarantee=null;
				                wcpfblimitsanctioned=dReport.getWCP_FB_LIMIT_SANCTIONED();
				                 System.out.println("wcpfblimitsanctioned is==="+wcpfblimitsanctioned);
				                 
				                 wcpnfblimitsanctioned=dReport.getWCP_NFB_LIMIT_SANCTIONED();
				                 
				                 System.out.println("wcpnfblimitsanctioned is==="+wcpnfblimitsanctioned);
				                 
				                 
				                 wcpfbcredittoguarantee= dReport.getWCP_FB_CREDIT_TO_GUARANTEE();
				                 System.out.println("wcpfbcredittoguarantee is==="+wcpfbcredittoguarantee);
				                 
				                 wcpnfbcredittoguarantee=dReport.getWCP_NFB_CREDIT_TO_GUARANTEE();
				                 System.out.println("wcpnfbcredittoguarantee is==="+wcpnfbcredittoguarantee);
                                
								%>

									<TR align="left" valign="top">
							     <TD  align="left" valign="top" class="ColumnBackground1">
							             <html:hidden property="cgpanr"  value="<%=dReport.getCGPAN() %>" name="rsForm"/>
							     
                   <%=dReport.getCGPAN() %>&nbsp;
                   </TD>
                  
                 
                    <TD  align="left" valign="top" class="ColumnBackground1">
                     <html:text property="wcpfb" size="20" onkeypress="return numbersOnly(this, event)" onkeyup="isValidNumber(this)" value="<%=wcpfblimitsanctioned%>" name="rsForm"/>	
                   
                   </TD>
                    <TD  align="left" valign="top" class="ColumnBackground1">
                     <html:text property="wcpnfb" size="20" onkeypress="return numbersOnly(this, event)" onkeyup="isValidNumber(this)"  value="<%=wcpnfblimitsanctioned%>" name="rsForm"/>	
                   &nbsp;
                   </TD>
                   
                    <TD  align="left" valign="top" class="ColumnBackground1">
                     <html:text property="wcpfbc" size="20" onkeypress="return numbersOnly(this, event)" onkeyup="isValidNumber(this)" value="<%=wcpfbcredittoguarantee%>" name="rsForm"/>	
                   &nbsp;
                   </TD>
                    <TD  align="left" valign="top" class="ColumnBackground1">
                     <html:text property="wcpnfbc" size="20" onkeypress="return numbersOnly(this, event)" onkeyup="isValidNumber(this)" value="<%=wcpnfbcredittoguarantee%>" name="rsForm" />	
                   &nbsp;
                   </TD>
                                          
                  
	           	 <TD  align="left" valign="top" class="ColumnBackground1">						
	              <html:textarea property="appremarks" name="rsForm" value=" " />	
	           </TD>
	           	    
                   
                  </tr>
               <%l++; %>
               <%m++; %>
                 </logic:iterate>
                 
                 
                   <TD width="10%" align="center" valign="top" class="ColumnBackground1">						
											</TD>
											
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">						

											</TD>
											
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">						

											</TD>
											
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">			
											
											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">			
											
											</TD>
											<TD width="10%" align="left" valign="top" class="ColumnBackground1">			
											
											</TD>
											
										
		     </TABLE>	
              <tr align="center" valign="baseline">
           		 <td colspan="4"> 
           		 <div align="center"> 
		         
              <A href="javascript:submitForm('saveCgpanReductiondetails.do?method=saveCgpanReductiondetails')"><img src="images/Save.gif" alt="Accept" width="49" height="37" border="0"></a>
		      </div>
		      </td>
		     </tr>
		  	</table>     
		

	<TD width="20" background="images/TableVerticalRightBG.gif">
				&nbsp;
			</TD>
	     </tr>
		
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
 
</TABLE>

	</html:form>
	</TABLE>