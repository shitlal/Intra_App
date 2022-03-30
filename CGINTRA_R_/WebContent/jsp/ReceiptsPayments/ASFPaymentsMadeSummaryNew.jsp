<%@ page language="java"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");%>
<%@ page import="com.cgtsi.receiptspayments.PaymentList"%>
<%@ page import="com.cgtsi.actionform.RPActionForm"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic" %>
<%@ include file="/jsp/SetMenuInfo.jsp" %>
<% session.setAttribute("CurrentPage","submitASFPaymentsnew.do?method=submitASFPaymentsnew");%>
<%
String name="";
%>

<head>
<script type="text/javascript">

function copyAll()
		{

			var textboxes = document.getElementsByTagName('textarea'),
			box4 = document.getElementById('ta1'),i;
			var check_value = document.getElementById('chID');
			for (i = 0; i < textboxes.length; i++)
			{
				
						if(check_value.checked == true)
						{						

						
								textboxes[i].value = box4.value;
								
						
							
						}
						else
						{

							textboxes[i].value = "";
						}
						if(i==99)
						{
break;
							}
				
			}
		}		

function selectAll(chk)
{
	//var allocatedAmt="";
	//var receivedAmt="";
	//alert('1st outside loop');
	
	//alert(
	for(i=0;chk.length;i++)
	{
		//chk[i].checked=true;
		//alert('inside loop'+chk.length);
			chk[i].checked=true;
			if(i==99)
			{
break;
				}
	}
}




	</script>
</head>
<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form action="submitASFPaymentsnew.do" method="POST" enctype="multipart/form-data">
		<html:hidden property="paymentId" alt="Payment Id" name="rpAllocationForm" />
		<html:hidden property="memberId" alt="Member Id" name="rpAllocationForm" />
		<TR> 
			<TD width="20" align="right" valign="bottom"><IMG src="images/TableLeftTop.gif" width="20" height="31"></TD>
			<TD background="images/TableBackground1.gif"><IMG src="images/ReceiptsPaymentsHeading.gif" width="121" height="25"></TD>
			<TD width="20" align="left" valign="bottom"><IMG src="images/TableRightTop.gif" width="23" height="31"></TD>
		</TR>
		<TR>
			<TD width="20" background="images/TableVerticalLeftBG.gif">&nbsp;</TD>
			<TD>
							<TABLE width="100%" border="0" cellspacing="1" cellpadding="1">
								<TR>
									<TD colspan="7"> 
										<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
											<TR>
												<TD width="20%" class="Heading"><bean:message key="paymentDetails"/></TD>
												<TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
												<TD>&nbsp;</TD>
											</TR>
											<TR>
												<TD colspan="7" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
											</TR>

										</TABLE>
									</TD>
									</TR>
									<TR>
									<TD align="left" valign="top"    class="ColumnBackground">
										SNO
									</TD>
									<TD align="left" valign="top" class="ColumnBackground">
										Member Name
									</TD>
									<TD align="left" valign="top"    class="ColumnBackground">
										
                                                                                PAY ID  (PaymentIdNumber)
									</TD>
									<%	int sum = 0;
										int totalSum = 0;
										int count = 0;
										int totalCount = 0;
										double totalst = 0;
										double totalecess = 0;
										double totalhecess = 0;
									%>
                  <TD align="left" valign="top" class="ColumnBackground">
										Allocated Amount
									</TD>                                                    
                  <!--Two columns paymentId, instrumentNo added by sukant PathInfotech on 21/01/2007-->
                  <TD align="left" valign="top" class="ColumnBackground">
										<!-- <bean:message key="instrumentNo" /> --> Instrument Number
									</TD>
                   <TD align="left" valign="top" class="ColumnBackground">
										<bean:message key="instrumentAmount" />
									</TD>
									<TD align="left" valign="top" class="ColumnBackground">
										<!-- <bean:message key="instrumentDt" /> --> Instrument Date
									</TD>
                                                                        
                                                                      
                                                                        <TD align="left" valign="top" class="ColumnBackground">
										<!-- <bean:message key="instrumentDt" /> --> Received Amt
									</TD>
                                                                        <TD align="left" valign="top" class="ColumnBackground">
										 Realization Date
									</TD>
                                                                        <TD align="left" valign="top" class="ColumnBackground">
										<!-- <bean:message key="instrumentDt" /> --> Short/Excess Amt
									</TD>
                                                                       <TD align="left" valign="top" class="ColumnBackground"><!-- <bean:message key="instrumentDt" /> -->
					Remarks
					<div align="right">Copy/Clear All 
                     <input type="checkbox" name="ch1" id="chID" onClick="copyAll()"/><br/>
					
					<%-- <input type="checkbox" id="<%=paymentId%>" onclick="copyAll(this.id);"/>--%>
					</div>
					</TD>
					
                                    <!--   <TD align="left" valign="top" class="ColumnBackground">   bhu-->
										<!-- <bean:message key="instrumentDt" /> Appropriate  </TD>--> 
									
                                             
                                             
                                             
                                             
                   <!--<TD align="left" valign="top" class="ColumnBackground"> <bean:message key="instrumentDt" /> 
					Appropriate
					<input type="checkbox" name="appropriateFlag" id="chID" onClick="selectAll(document.rpAllocationForm.allFlag)"/>
					<input type="checkbox" id="selectall"/>	
					</TD>                           
                                                                        
                    bhu                                                    
									-->
									
									
									<TD align="left" valign="top" class="ColumnBackground"><!-- <bean:message key="instrumentDt" /> -->
					Appropriate
					<input type="checkbox" name="appropriateFlag" id="chID" onClick="selectAll(document.rpAllocationForm.allFlag)"/>
					<!--<input type="checkbox" id="selectall"/>	-->
					</TD>
									
									
									</TR>	
									<%RPActionForm rpActionForm = (RPActionForm)session.getAttribute("rpAllocationForm") ;

									ArrayList paymentSummary = rpActionForm.getPaymentDetails() ;
									if(paymentSummary==null) {%>
										<TR>
											<TD colspan="5" align="center" valign="top" class="ColumnBackground" >
													<bean:message key="noPaymentsReceived" />
											</TD>
										</TR>
									<%} else {
                                                                          int index=0;
                                                                          String name1="";%>
                                         
                                                                      
										<logic:iterate id="object" name="rpAllocationForm" property="paymentDetails"  indexId="id">
										<%
										com.cgtsi.receiptspayments.PaymentList paymentList =(com.cgtsi.receiptspayments.PaymentList)object;%>
											<TR>
                                                                                        
												<TD align="left" valign="top"   class="tableData">
                                                                                                
                                                                                                
                                                                                                
													<%=id+1%>
													
												</TD>
                                                                                                 
                                                                                                
												<TD align="left" valign="top"  class="tableData">
													<%=paymentList.getMemberName()%>
												</TD>
												
                                                                                                
                                                                                                <%name="payIds(key-"+(id-1)+")";%>
													
                                                                                             
                                                                                            
													<html:hidden property="<%=name%>"   name="rpAllocationForm" value="<%=paymentList.getPaymentId()%>" />
                                                                                                        
                                                                                                        
													 <TD align="left" valign="top"     class="tableData">
													<%=paymentList.getPaymentId()%>
												</td> 
												
												<%--<TD align="left" valign="top" class="tableData">
													<a href="javascript:setHiddenFieldValue('paymentId', '<%=paymentList.getPaymentId()%>', 'appropriatePayments.do?method=getPaymentDetails');">
														<%=paymentList.getPaymentId()%>
													</a>
												</TD>
												
												 --%>
												
												
												
                                                                                                                
                                                                                                                
                                                                                                                
													
												
                                                                       <TD align="left" valign="top" class="tableData">
													<%=paymentList.getAllocatedAmt()%>
												</td>                                                                        
                                                                         <TD align="left" valign="top" class="tableData">
													<%=paymentList.getInstrumentNo()%>    
												</TD>
                                                                                                <TD align="right" valign="top" class="tableData">
													<%=paymentList.getPayAmount()%>
												</TD>
												<TD align="left" valign="top" class="tableData">
														<%
                            String date1=null;
                            java.util.Date instrumetDt = paymentList.getInstrumentDt();
                            if(instrumetDt != null)
                            {
                            date1 =dateFormat.format(instrumetDt);
                            }
                            else
                            {
                              date1="";
                            }                            
                            %>
                          <%=date1%>
                          <% String  amtName=String.valueOf(paymentList.getAllocatedAmt());%>
												</TD>
                                                                                                
                                                                                                
                                                                                                
                                                                                                
                                                                                                <TD align="left" valign="top" class="tableData">
													<%name="receivedAmounts(key-"+(id-1)+")";%>
													<html:text property="<%=name%>"   onkeypress="return numbersOnly(this, event)"  size="10"  name="rpAllocationForm" value="<%=amtName%>"/>
												</TD>
                                                                                                
                                                                                            
                                                                                             
                                                                                                
                                                                                                      <TD class="tableData">
                                                                                                      <%name="realizationDates(key-"+(id-1)+")";%>
														<html:text property="<%=name%>"  name="rpAllocationForm" size="10"   onkeypress="return numbersOnly2(this, event)"  value="<%=date1%>" maxlength="10"/><font color='red'>(DD/MM/YYYY)</font>     
														
													</TD>
                                                                                                 <TD align="right" valign="top" class="tableData">
													<%=paymentList.getShortOrExcessAmount()%>
												</TD>
                                                                                               
                                                                                                  
                                                   <!--   <TD align="left" valign="top" class="tableData">  changed by bhu start -->
													<%-- bhu<%name="remarks(key-"+(id-1)+")";--%> 
													<%--<html:textarea property="<%=name%>" rows="4" cols="40" name="rpAllocationForm" value=""/> </TD> changed by bhu end --%>
													
													
													<TD align="left" valign="top" class="tableData">
													<%name="remarks(key-"+(id-1)+")";%>
													<html:textarea property="<%=name%>" rows="4" cols="40" name="rpAllocationForm" value="" styleId="ta1"/>
													
						
													
													
												    </TD>
                                                                                         <%--         <%name="allocatedFlags(key-"+(id-1)+")";%>  
                                                                                         
                                                                                         <TD align="right" valign="top" class="tableData">
                                                                                           bhu
                                                                                            <html:checkbox name="rpAllocationForm" property="<%=name%>" value="Y" disabled="false"/>
												</TD>
                                                                                           --%>
                                                                                           <%name="allocatedFlags(key-"+(id-1)+")";%>
												<TD align="right" valign="top" class="tableData"><html:checkbox
												name="rpAllocationForm" property="<%=name%>" value="Y"
												disabled="false" styleId="allFlag" styleClass="rpAllocationForm"/></TD>
                                                                                                 
                                                                                               
                                                                                                
                                                                                              
                                                                                               
                                                                                               </TR>
						
                                                                                               
                                                                                               
                                                <%count=paymentList.getAllocatedAmt();
											   totalCount = totalCount + count;%>   
											           
											   
											   
											  
											   
											   <%sum=paymentList.getPayAmount();
											   totalSum = totalSum + sum;%>   
											     
											                                     
                                                                                                
									</logic:iterate>	 											
									
								<%
                                                               index++;
                                                                }
                                                               
                                                                 %>
                                                                 <TR>
                                                                 
                                                                 
									<TD width="10%"  align="left" valign="top" class="ColumnBackground">
									Total
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
									
									</TD>

									<TD width="10%" align="left" valign="top" class="ColumnBackground">
								
									</TD>
									

									<TD width="10%" align="left" valign="top" class="ColumnBackground">
									 <%=totalCount%>  
									
									</TD>
											 									
										
											 									
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
								
									</TD>
                                                                         <TD width="10%" align="left" valign="top" class="ColumnBackground">
									
									<%=totalSum%>  
									
									</TD>
 
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
									<div align="right">
								
									</div>
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
									<div align="right">
									</div>
									</TD>
                                    <TD width="10%" align="left" valign="top" class="ColumnBackground">
									<div align="right">
									</div>
									</TD>
                                    <TD width="10%" align="left" valign="top" class="ColumnBackground">
									<div align="right">
									</div>
									</TD>
                                    <TD width="10%" align="left" valign="top" class="ColumnBackground">
									<div align="right">
									
									</div>
									</TD>
									<TD width="10%" align="left" valign="top" class="ColumnBackground">
									<div align="right">
									
									</div>
									</TD>
									
									</TR>	
                                                                 
                                                                
				</TABLE>
                                
                                
                                  
									
                                
                                
                                
                                
			</TD>
			<TD width="20" background="images/TableVerticalRightBG.gif">
				&nbsp;
			</TD>
		</TR>
                
                
                <TR>
					<TD align="center" valign="baseline" colspan="10">
					<DIV align="center">
				 	<A href="javascript:submitForm('submitASFPaymentsnew.do?method=submitASFPaymentsnew')">
							<IMG src="images/Save.gif" alt="Save" width="49" height="37" border="0"></A> 
          <!--    <A onclick ="" href="javascript:if(checkbox_checker()) submitForm('expiredasfallocatePayments.do?method=submitExpiredASFDANPayments')">
          		  <IMG src="images/Save.gif" alt="Save" width="49" height="37" border="0"></A> -->
					
						<A href="javascript:document.rpAllocationForm.reset()">
							<IMG src="images/Reset.gif" alt="Cancel" width="49" height="37" border="0"></A>						
								<A href="home.do?method=getMainMenu&menuIcon=<%=session.getAttribute("menuIcon")%>">
								<IMG src="images/Cancel.gif" alt="Cancel" width="49" height="37" border="0"></A>
					
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