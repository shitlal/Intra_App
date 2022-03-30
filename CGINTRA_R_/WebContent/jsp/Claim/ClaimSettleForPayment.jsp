<%@ page language="java"%>
<%@ page import="java.util.ArrayList"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DecimalFormat"%>

<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic"%>
<%@ include file="/jsp/SetMenuInfo.jsp"%>
 


<%SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");%>
<%DecimalFormat decimalFormat = new DecimalFormat("#########0.00");%>


<script>
 
function checkAll(ele) {
 	
    var ElemItem = document.getElementsByTagName('input');
    var NoOFEle = ElemItem.length;
    var NoOFChkBox =0;
    var chkboxcnt=0;
    var netamtZero=0.00;
    
	
    var AlreadyCheckedFlag=false;
    if (ele.checked) {
        
        for (var i = 1; i < ElemItem.length; i++) {

            if(i==1){
                //alert("ElemItem[NoOFEle-1].value :"+ElemItem[NoOFEle-2].value);
            	ElemItem[NoOFEle-2].value=netamtZero.toFixed(2);
             }
            
            if (ElemItem[i].type == 'checkbox') {            
            	//alert(" before ElemItem[i].checked :"+ElemItem[i].checked);            	
                if(ElemItem[i].checked==true){
                	AlreadyCheckedFlag=true;
                }
                ElemItem[i].checked = true;
                 ++NoOFChkBox;
            }
            if (ElemItem[i].type == 'hidden') {
                            	
                //alert("ElemItem[NoOFEle-1] :"+ElemItem[NoOFEle-1].value)
               // alert("AlreadyCheckedFlag :"+AlreadyCheckedFlag)
               var netTotTemp=0.0;
                if(i!=NoOFEle && i!=(NoOFEle-1) && i!=(NoOFEle-2)&&i!=(NoOFEle-3)){
                    //Setting Total Amount Selected 
                    netTotTemp=parseFloat(ElemItem[NoOFEle-2].value)+parseFloat(ElemItem[i].value);
                    ElemItem[NoOFEle-2].value=netTotTemp.toFixed(2);
                }               
            }

			if(ElemItem[i].type == 'text'){
				ElemItem[NoOFEle-3].value=NoOFChkBox;
			}
        }
    } else {
        for (var i = 0; i < ElemItem.length; i++) {
            console.log(i)
            if (ElemItem[i].type == 'checkbox') {
                ElemItem[i].checked = false;
            }
        }
                
        ElemItem[NoOFEle-2].value=netamtZero.toFixed(2);/*for amt*/
        ElemItem[NoOFEle-3].value=0;/*for cnt*/
    }
}

function CalculateTotSelectedCntAndAmt(netAmt,chkbxObj){
       //alert("netAmt :"+netAmt+" chkbxObj :"+chkbxObj.checked);
	   if(chkbxObj.checked){
		   
		   var SelTotCnt=parseInt(document.getElementById("SelTotCnt").value);		   
		   var SelTotNtAmt=parseFloat(document.getElementById("SelTotNtAmt").value);
		   
		   SelTotCnt=SelTotCnt+1;
		   //Adding selected value into Total 
		   SelTotNtAmt=SelTotNtAmt+parseFloat(netAmt);
		   //Setting two decimal
		   SelTotNtAmt=SelTotNtAmt.toFixed(2);
		   
		   document.getElementById("SelTotCnt").value=SelTotCnt;
		   document.getElementById("SelTotNtAmt").value=SelTotNtAmt;
		   
	   }else{
		   
		   var SelTotCnt=parseInt(document.getElementById("SelTotCnt").value);
		   var SelTotNtAmt=parseFloat(document.getElementById("SelTotNtAmt").value);
		   
           if(SelTotCnt>=0){
        	   //Subtracting  selected checkbox into Total 
		       SelTotCnt=SelTotCnt-1;
           }
		   
		   if(SelTotNtAmt>=0){
			 //Subtracting  selected amount into Total
		       SelTotNtAmt=SelTotNtAmt-parseFloat(netAmt);
		       SelTotNtAmt=SelTotNtAmt.toFixed(2);
		   }
		   document.getElementById("SelTotCnt").value=SelTotCnt;
		   document.getElementById("SelTotNtAmt").value=SelTotNtAmt;
	   }
}


function Saving(){
        var totNetAmt = document.getElementById("SelTotNtAmt").value;
        var totSelectCnt = document.getElementById("SelTotCnt").value;
        
		if(confirm(" Total Cgpan Selected : "+totSelectCnt+" \n Total Net Outstanding Amount : "+totNetAmt+" \n\n !! Are you Sure for Save for Initiate Payment !! " )){
         
			   submitForm("ClaimSettleForPaymentProcess.do?method=ClaimSettleForPaymentProcess");
               return true;
		}else{
		   return false;
		} 
}


</script>
<bean:define id="AcctAvlType" name="claimSettleForPaymentForm" property="status" type="java.lang.String"/> 
<%
String AcctAvlFlag=AcctAvlType;
out.println("AcctAvlFlag :"+AcctAvlFlag);

%>
<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form
		action="/ClaimSettleForPaymentInput.do?method=getClaimSettleForPaymentInput"
		method="POST"  >
		<TR>
			<TD width="20" align="right" valign="bottom"><IMG
				src="images/TableLeftTop.gif" width="20" height="31" alt=""></TD>
			<TD background="images/TableBackground1.gif"><IMG
				src="images/ReportsHeading.gif" width="121" height="25" alt=""></TD>
			<TD width="20" align="left" valign="bottom"><IMG
				src="images/TableRightTop.gif" width="23" height="31" alt=""></TD>
		</TR>
		<TR>
			<TD width="20" background="images/TableVerticalLeftBG.gif">&nbsp;</TD>
			<TD>
			<TABLE width="100%" border="0" align="left" cellpadding="0"		cellspacing="0">
				<TR>
					<TD>
					<TABLE width="100%" border="0" cellspacing="1" cellpadding="1">
						<TR>
							<TD colspan="23">
							<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td colspan="6" class="Heading1" align="center"><u><bean:message
										key="reportHeader" /></u></td>
								</tr>
								<tr>
									<td colspan="6">&nbsp;</td>
								</tr>
								<TR>
									<TD class="Heading">Claim Settled for Payment</TD>
									<td class="Heading" >&nbsp;
									<bean:message	key="from" /> 
									<bean:write name="claimSettleForPaymentForm" property="startDt" />&nbsp;
									
									
									<bean:message key="to" />
									<bean:write name="claimSettleForPaymentForm" property="endDt" /> </td>
									<TD><IMG src="images/TriangleSubhead.gif" width="19"
										height="19" alt=""></TD>
								</TR>
								<TR>
									<TD colspan="3" class="Heading"><IMG
										src="images/Clear.gif" width="5" height="5" alt=""></TD>
								</TR>

							</TABLE>
							</TD>
						</TR>						
								<%		int columnCount=0;	%>	
						<TR>
						<td width="3%" align="left" valign="top" class="ColumnBackground">Sr.no.</td>
						<TD width="10%" align="center" valign="top"	class="ColumnBackground1">
								         Select All<br>
								         <INPUT type="checkbox" onchange="checkAll(this)" name="chk[]" />
						</TD>
						<%
						  int position_cgpan=0;
						  int position_netamt=0;
						  %>
						<logic:iterate id="object" name="claimSettleForPaymentForm" property="colletralCoulmnName"	indexId="index">
							<%	
										String str=(String)object;
										columnCount++;										
							%>		
								<TD width="10%" align="left" valign="top"	class="ColumnBackground1"><%=str %>	</TD>
								
									<%if(str.equals("CGPAN")){/* 3 */
										  position_cgpan=columnCount-1;
								      }
								
								     if(str.equals("NET PAID AMOUNT")){/*8*/
							    	       position_netamt=columnCount-1;
							         }
								    
								   %>
						</logic:iterate>
								
							</TR>
						<% int row_counter=0; %>
						<logic:iterate id="object1" name="claimSettleForPaymentForm" property="colletralCoulmnValue"	indexId="index1">
							<%
							ArrayList value =  (ArrayList)object1;
							
							String bgcolor="#FFFF99";
							if(value.get(0).equals("Grand Total")){
								bgcolor="orange";
							}
							
							if(value.get(0).equals("Total") || value.get(0).equals("Grand Total")){
							%>
							<TR bgcolor="<%=bgcolor%>" >						
								<td align="left" valign="top" ></td>
								<td align="left" valign="top" ></td>
							<%	
							for(int i=0;i<value.size();i++){
							%>
								<TD width="10%" align="left" valign="top" bgcolor="<%=bgcolor%>"  >
								       <%=value.get(i)==null?"--":value.get(i)%>
								</TD>
							<% } %></TR>
							
							<%}else{%>
							<TR>								
								        <td align="left" valign="top" class="ColumnBackground1"><%=++row_counter%></td>
								        <TD width="10%" align="left" valign="top"
											class="ColumnBackground1">
											
											<input type="hidden" id="netAmt"+<%=value.get(position_cgpan)%> value="<%=value.get(position_netamt)%>"  >  
											
											
											<% String id = "checkboxfield("+value.get(position_cgpan)+")";
											   String param="--";
												if(value.get(position_netamt)!=null){
													  param=value.get(position_netamt).toString();
												}
											   String jsfuncName="CalculateTotSelectedCntAndAmt('"+param+"',this);";
											   
											   
											%>
											    <html:checkbox name="claimSettleForPaymentForm"  
											    property="<%=id%>"    value="true" onclick="<%=jsfuncName%>" ></html:checkbox>
									    </TD>
									<%	
									      for(int i=0;i<value.size();i++){
									%>
										<TD width="10%" align="left" valign="top"
											class="ColumnBackground1">
											   <%=value.get(i)==null?"--":value.get(i)%>
									    </TD>
									    <% } %>
									    
							</TR>
							<% }%>
						</logic:iterate>
					</TABLE>
					</TD>
				</TR>
				<TR>
				    <TD height="20">&nbsp;
				       <table cellpadding=10 cellspacing=10 >
				            <tr>
							<TD height="20">Total Selected</TD>
							<TD height="20"><div id="SelectedTotCnt"><input type="text" id="SelTotCnt" value="0" readonly=true  ></div></TD>
							<TD height="20">Total Net Outstanding Amount</TD>
							<TD height="20"><div id="SelectedTotNetAmt"><input type="text" id="SelTotNtAmt" value="0.00" readonly=true ></div></TD>
							</tr>
						</table>
					</TD>
				</TR>
				<TR>
					<TD height="20">&nbsp;</TD>
				</TR>
				<TR>
					<TD align="center" valign="baseline">	 
					<div>
					<table cellspacing=5 cellpadding=5>
						<tr>
						
							<td>
								<a href="ClaimSettleForPayment.do?method=ExportToFile&&fileType=CSVType&FlowLevel=MK">
								<IMG src="images/csv_icon.png" width="50" height="35" alt="Export to Excel">
								</a>
							</td>
							
							<td>
							    <% if(AcctAvlFlag.equals("A")){%>
									<input type="button" value=" Save for Initiate Payment " onClick="Saving();">
								<%}else{ %>
									<input type="hidden" value=" Use to maintain count of form element for javascript method ">
                                                                <%}%>
							</td> 
							<td>
								<a href="ClaimSettleForPayment.do?method=ExportToFile&&fileType=PDFType&FlowLevel=MK">
								  <IMG src="images/pdf_icon.png" width="50" height="35" alt="Export to PDF">
							</a>
							</td>
						</tr>
						</table>
					<DIV align="center"><A	href="javascript:submitForm('ClaimSettleForPaymentInput.do?method=getClaimSettleForPaymentInput')">
					<IMG src="images/Back.gif" alt="Print" width="49" height="37"	border="0"></A> <A href="javascript:printpage()"> 
					<IMG src="images/Print.gif" alt="Print" width="49" height="37"	border="0"></A></DIV>
					
					</div> 
					</TD>
					 
				</TR>
			</TABLE>
			</TD>
			<TD width="20" background="images/TableVerticalRightBG.gif">
			&nbsp;</TD>
		</TR>
		<TR>
			<TD width="20" align="right" valign="top">									
			    <IMG src="images/TableLeftBottom1.gif" width="20" height="15" alt="">
			</TD>
			<TD background="images/TableBackground2.gif">&nbsp;</TD>
			<TD width="20" align="left" valign="top"><IMG
				src="images/TableRightBottom1.gif" width="23" height="15" alt="">
			</TD>
		</TR>
	</html:form>	
</TABLE>