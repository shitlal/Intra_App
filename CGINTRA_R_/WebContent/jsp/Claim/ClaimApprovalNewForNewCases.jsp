<%@ page language="java"%>  
<%@ page import = "com.cgtsi.actionform.ClaimActionForm"%>
<%@ page import="com.cgtsi.claim.ClaimConstants"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.text.ParsePosition"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic"%>
<%@ include file="/jsp/SetMenuInfo.jsp" %>
<%@ page import ="java.text.DecimalFormat"%>

<% DecimalFormat decimalFormat = new DecimalFormat("##########.##");%>
<% session.setAttribute("CurrentPage","displayClaimApprovalNewForNewCase.do?method=displayClaimApprovalNewForNewCase");%>
<%
String tempname="";
String mlidecision = "";
String thiskey = "";
String claimAmount = "";
String mlicomments = "";
String memId = "";
String claimRetRemarks="";
String claimRetdT="";
String claimrefnumber = "";
double approvedamount = 0.0;
double osAmntAsOnNPA = 0.0;
double appliedClaimAmnt = 0.0;
double eligibleAmnt = 0.0;
String url = "";
String cgclan = "";
java.text.SimpleDateFormat sdf = null;
String approvedClmAmount = null;
String forwardedUserId = "";
String comments ="";
double tcApprovedAmt = 0.0;
double wcApprovedAmt = 0.0;
double tcOutstanding = 0.0;
double wcOutstanding = 0.0;
double tcrecovery = 0.0;
double wcrecovery = 0.0;
double tcEligibleAmt = 0.0;
double wcEligibleAmt = 0.0;
double tcDeduction = 0.0;
double wcDeduction = 0.0;
double tcFirstInstallment = 0.0;
double wcFirstInstallment = 0.0;
String unitName = ""; 
String bid = "";
double claimApprovedAmt = 0.0;

double asfRefundableForTC;
         double asfRefundableForWC;
         String refundFlag;
         String StateCode;
         String  GstNo;

  
         double   asfServTaxForTC=0;
         double asfServTaxForwC=0;
         
         double tcSwbhaCessDed=0;
         double wcSwbhaCessDed=0;

  
         double tckkCessDed=0.0;
         double wckkCessDed=0.0;

%>
<html>
<head>
<LINK href="<%=request.getContextPath()%>/css/StyleSheet.css" rel="stylesheet" type="text/css">
</head>
<body>

<!--<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.5.0/jquery.min.js"></script>-->
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.7.1.min.js"></script>
	<script type="text/javascript">
		function openRecBox(field){
			//alert(field);
			var id = field;
			var da = findObj("recommendationData("+id+")");
			var data = da.value;
			//regex to set content line by line in popup window.
			//alert(da.value);
			var splitBy = '.';
			var str = data.split(splitBy);
			//alert(str[0]+"---"+str.length+"----"+(str.length-1));
			var start = '<html><body><table style="text-align:center;" border="1" cellpadding="" cellspacing=""><tbody><tr><th></th><th>STATUS</th><th>CGPAN</th><th>CONDITION</th></tr>';
			var middle='';
		 	for(var i=0;i<str.length-1;i++){
				var d = str[i];
				var substr = d.split('&');				
			    middle = middle+'<tr><td>'+(i+1)+'</td><td>'+substr[0]+'</td><td>'+substr[1]+'</td><td>'+substr[2]+'</td></tr>';
			} 
			var end = '</tbody></table></body></html>';
			var total = start+middle+end;
			var left = (screen.width/2)-(500/2);
			var top = (screen.height/2)-(300/2);
			//alert(start+middle+end);
			//window.open('','_blank','width=335,height=330,resizable=1').document.write(data);
			//window.open('about:blank').document.body.innerHTML = data;
			var myWin = window.open('','_blank','width=500,height=300,resizable=1,scrollbars=1,location=no,top='+top+',left='+left);
			$(myWin.document.body).ready(function(){
				$(myWin.document.body).append(total);
			});
			setTimeout(function () { myWin.close();}, 60000);
		}
		
		function openReasBox(field){
			var id = field;
			//alert('id:'+id);
			var target = findObj("reasonData("+id+")");
			var data = target.value;
			if(data != ''){
				//regex to set checkbox(s) for selected reasons.
			}
			var left = (screen.width/2)-(800/2);
			var top = (screen.height/2)-(400/2);
			//Here child window handler to get selected values.
			var myWin = window.open('jsp/reasonList.html','_blank','width=800,height=400,resizable=1,scrollbars=1,location=no,top='+top+'left='+left);			
			//pass id to child window.
			setInterval(function(){myTimer();},1000);
			function myTimer() {
    				
    				myWin.document.getElementById('test').value = id;
			}

			
				
		}
		
		function enableRemarks(field){			
			var fname = field.name;
			//alert(field.name);
			var val = field.value;
			//alert(field.value);
			var temp  = fname.split('#');
			var tid = temp[2];
			//alert(tid.length);
			tid = tid.substring(0,(tid.length)-1);
			//alert(tid);
			//var i = tid[2];
			var id = 'F#'+tid;
			//alert(id);
			
			//var field = findObj("decision("+id+")");
			 if(val == "RT"){
				document.getElementById(id).disabled = false;
				//enable reasBox();
			}else{
				document.getElementById(id).disabled = true;
			} 
		}

	function submitFormClaimApproval(action)
		{		
			document.forms[0].action=action;
			document.forms[0].target="_blank";
			document.forms[0].method="POST";	
			document.forms[0].submit();
		}
</script>
<html:errors/>
<html:form name="cpTcDetailsForm" type="com.cgtsi.action.ClaimAction" action="saveClaimApprovalForNewCases.do?method=saveClaimApproval" method="POST">
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <font color="#FF0000" size="2">The number of Claim Application(s) beyond your Approving Limit are : <bean:write property="limit" name="cpTcDetailsForm"/></font>
    <tr>
      <td class="FontStyle">&nbsp;</td>
    </tr>
  </table>
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td align="right" valign="bottom"><img src="images/TableLeftTop.gif" width="20" height="31"></td>
      <td background="images/TableBackground1.gif"><img src="images/ClaimsProcessingHeading.gif" width="131" height="25"></td>
    <td width="20" align="left" valign="bottom"><img src="images/TableRightTop.gif" width="23" height="31"></td>
  </tr>
  <tr>
    <td background="images/TableVerticalLeftBG.gif">&nbsp;</td>
    <td>
                
    <table width="100%" border="0" cellspacing="1" cellpadding="0">
          <tr>
            <td colspan="4"><table width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td width="35%" class="Heading">&nbsp;Approve Claim Applications</td>
                  <td colspan="3"><img src="images/TriangleSubhead.gif" width="19" height="19"></td>
                </tr>
                <tr>
                  <td colspan="21" class="Heading"><img src="images/Clear.gif" width="5" height="5"></td>
                </tr>
              </table></td>
          </tr>
          
          <tr> 
	      <td colspan="4" class="SubHeading">&nbsp;<bean:message key="firstclaim"/></td>
          </tr>
          
          <tr>
            <td colspan="20"> <table width="100%" border="0" cellspacing="1" cellpadding="0">
                <tr class="HeadingBg">
                  <td valign="middle" class="HeadingBg"> <div align="center"><strong>&nbsp;Sr.<br>
                      No</strong></div></td>
                  <td class="HeadingBg"><div align="center">Member<br>
                      ID </div></td>
                  <td class="HeadingBg"><div align="center">
                      Claim <br>
                      Ref <br>
                      Number</div></td>
                      <td class="HeadingBg"><div align="center">
                      Claim <br>
                      Status</div></td>
                <td class="HeadingBg"><div align="center">
                      Unit <br>
                      Name</div></td>
					  <td class="HeadingBg"><div align="center">
                         Application<br>Remarks
                      </div>
                      </td> 					  
					   <td class="HeadingBg"><div align="center">
                         Return<br>Remark
                      </div>
                      </td>
                      <td class="HeadingBg"><div align="center">Return<br>Remark<br>Date</div>
                      </td>
               <td class="HeadingBg"><div align="center">TC Approved Amt<br><bean:message key="inRs"/></div></td>
               <td class="HeadingBg"><div align="center">WC Approved Amt<br><bean:message key="inRs"/></div></td>
               <td class="HeadingBg"><div align="center">TC O/S Amt as on NPA <br><bean:message key="inRs"/></div></td>
               <td class="HeadingBg"><div align="center">WC O/S Amt as on NPA<br><bean:message key="inRs"/></div></td>
               <td class="HeadingBg"><div align="center">TC Recovery as on NPA<br><bean:message key="inRs"/></div></td>
               <td class="HeadingBg"><div align="center">WC Recovery as on NPA<br><bean:message key="inRs"/></div></td>
               <td class="HeadingBg"><div align="center">TC Eligible Amt<br><bean:message key="inRs"/></div></td>
               <td class="HeadingBg"><div align="center">WC Eligible Amt<br><bean:message key="inRs"/></div></td>
               <td class="HeadingBg"><div align="center">TC First Inst<br><bean:message key="inRs"/></div></td>
               <td class="HeadingBg"><div align="center">WC First Inst<br><bean:message key="inRs"/></div></td>
               <td class="HeadingBg"><div align="center">TC Ded<br><bean:message key="inRs"/></div></td>
       <td class="HeadingBg"><div align="center">WC Ded<br><bean:message key="inRs"/></div></td>

      <td class="HeadingBg"><div align="center">TC Refund<br><bean:message key="inRs"/></div></td>
               <td class="HeadingBg"><div align="center">WC Refund<br><bean:message key="inRs"/></div></td>

                <td class="HeadingBg"><div align="center">S.Tax on TC<br><bean:message key="inRs"/></div></td>
                 <td class="HeadingBg"><div align="center">SbhCess on TC<br><bean:message key="inRs"/></div></td>
<td class="HeadingBg"><div align="center">KKCess on TC<br><bean:message key="inRs"/></div></td>
 

               
               <td class="HeadingBg"><div align="center">S.Tax on WC<br><bean:message key="inRs"/></div></td> 
                 <td class="HeadingBg"><div align="center">SbhCess on WC<br><bean:message key="inRs"/></div></td>

 
 <td class="HeadingBg"><div align="center">KKCess on WC<br><bean:message key="inRs"/></div></td>
  <td class="HeadingBg"><div align="center">State_Code<br></div></td>
 <td class="HeadingBg"><div align="center">Gst_No<br></div></td>
             
               <td class="HeadingBg"><div align="center">ASF Received or Not<br></div></td>
             <%--  <td class="HeadingBg"><div align="center">Existing Remarks</div></td> --%>
               <td class="HeadingBg"><div align="center">Net Payable as First Installment<br><bean:message key="inRs"/></div></td>
               <td align="center" valign="top"> <div align="center">
                      <p class="HeadingBg"><strong>&nbsp;Decision</strong></p>
                    </div></td>		
               <td class="HeadingBg"><div align="center">Approved Claim Amount<br><bean:message key="inRs"/></div></td>
			   <td class="HeadingBg"><div align="center">DU Updated<br>@<br></div></td>
               <td class="HeadingBg"><div align="center">Comments</div></td>
			   <td class="HeadingBg"><div align="center">Recommondation(s)</div></td>
			   <td class="HeadingBg"><div align="center">Return Reason(s)</div></td>
                </tr>
                <%
                    int j=0;
                %>
              <logic:iterate id="object" name="cpTcDetailsForm"  property="firstInstallmentClaims"   indexId="index">
		         <%
                com.cgtsi.claim.ClaimDetail claimdetail = (com.cgtsi.claim.ClaimDetail)object;
                memId = claimdetail.getMliId();
                claimrefnumber = claimdetail.getClaimRefNum();
                       
                url = "displayClmApplicationDtlModified.do?method=displayClmApplicationDtlModified&"+ClaimConstants.CLM_CLAIM_REF_NUMBER+"="+claimrefnumber;           
                //url = "getClaimRefNumberDtl.do?method=getClaimRefNumberDtl&"++ClaimConstants.CLM_CLAIM_REF_NUMBER+"="+claimrefnumber; 
                unitName = claimdetail.getSsiUnitName();
				  String appRemarks=claimdetail.getAppRemarks();
	    	//--Added on 21-June-2018
                claimRetRemarks=claimdetail.getClaimRetRemarks();
                claimRetdT=claimdetail.getClaimRetdT();
              //--Added on 21-June-2018
				
				
                bid = claimdetail.getBorrowerId();
                tcApprovedAmt = claimdetail.getTcApprovedAmt();
                wcApprovedAmt = claimdetail.getWcApprovedAmt();
                tcOutstanding = claimdetail.getTcOutstanding();
                wcOutstanding = claimdetail.getWcOutstanding();
                tcrecovery = claimdetail.getTcrecovery();
                wcrecovery = claimdetail.getWcrecovery();
                tcEligibleAmt = claimdetail.getTcClaimEligibleAmt();
                wcEligibleAmt = claimdetail.getWcClaimEligibleAmt();
                tcDeduction = claimdetail.getAsfDeductableforTC();
                wcDeduction = claimdetail.getAsfDeductableforWC();
                tcFirstInstallment = claimdetail.getTcFirstInstallment();
                wcFirstInstallment = claimdetail.getWcFirstInstallment();
                approvedamount = claimdetail.getApplicationApprovedAmount();
                String id1 = "MEMBERID#" + j;
                String id2 = "CLMREFNUMBERID#" + j;                
                approvedClmAmount = claimdetail.getTotalAmtPayNow();
                comments = claimdetail.getComments();
              
			   asfRefundableForTC = claimdetail.getAsfRefundableForTC();
               asfRefundableForWC = claimdetail.getAsfRefundableForWC();
               refundFlag = claimdetail.getRefundFlag();
			   String recommendation = claimdetail.getRecommendation();
			   String data = claimdetail.getRecommendationData();
			   String clmStatus = claimdetail.getClmStatus();
                  asfServTaxForTC =claimdetail.getTcSerTaxDed();		 
			  asfServTaxForwC =claimdetail.getWcSerTaxDed();			  
			  tcSwbhaCessDed =claimdetail.getTcSwbhaCessDed();		 
			  wcSwbhaCessDed =claimdetail.getWcSwbhaCessDed();

              tckkCessDed =claimdetail.getTckkCessDed();		 
			  wckkCessDed =claimdetail.getWckkCessDed();
			  StateCode =claimdetail.getStateCode();		 
			  GstNo =claimdetail.getGstNo();
           %>
		   
		   <% if(wckkCessDed==9999.0){ %>
           <tr class="TableDataR">
		   <%}else{%>
		   <tr class="TableData">
		   <%}%>
                <td align="left" valign="top" class="tableData" width="98"><div align="center">&nbsp;<%= Integer.parseInt(index+"")+1%></div></td>
                <td id="<%=id1%>"><%=memId%></td>
                <td id="<%=id2%>"><%--<%tempname="approveClaims("+claimrefnumber+")";%> --%> <a href="<%=url%>" target="_blank"><%=claimrefnumber%></a>
				  <%
					  String clmRefNumIndex = "CLMREFNUM##" + j;
				  %>
				  <html:hidden property = "<%=clmRefNumIndex%>" name="cpTcDetailsForm" value="<%=claimrefnumber%>"/>
		        </td>
		   <td><div align="right"><%=clmStatus%></div></td>
           <td><div align="right"><%=unitName%></div></td>
		   <td width="10%" ><%=appRemarks%></td>
		   <td width="10%"><%=claimRetRemarks%></td>
		   <td><div align="right"><%=claimRetdT%></div></td>
           <td><div align="right"><%=decimalFormat.format(tcApprovedAmt)%></div></td>
           <td><div align="right"><%=decimalFormat.format(wcApprovedAmt)%></div></td>
           <td><div align="right"><%=decimalFormat.format(tcOutstanding)%></div></td>
           <td><div align="right"><%=decimalFormat.format(wcOutstanding)%></div></td>
           <td><div align="right"><%=decimalFormat.format(tcrecovery)%></div></td>
           <td><div align="right"><%=decimalFormat.format(wcrecovery)%></div></td>
           <td><div align="right"><%=decimalFormat.format(tcEligibleAmt)%></div></td>
           <td><div align="right"><%=decimalFormat.format(wcEligibleAmt)%></div></td>
           <td><div align="right"><%=decimalFormat.format(tcFirstInstallment)%></div></td>
           <td><div align="right"><%=decimalFormat.format(wcFirstInstallment)%></div></td>
           <td><div align="right"><%=tcDeduction%></div></td>
       <td><div align="right"><%=wcDeduction%></div></td>
         <td><div align="right"><%=asfRefundableForTC%></div></td>
           <td><div align="right"><%=asfRefundableForWC%></div></td>

           <td><div align="right"><%=asfServTaxForTC%></div></td>
            <td><div align="right"><%=tcSwbhaCessDed%></div></td>
  
<td><div align="right"><%=tckkCessDed%></div></td>
           
            <td><div align="right"><%=asfServTaxForwC%></div></td>
            <td><div align="right"><%=wcSwbhaCessDed%></div></td>
 <td><div align="right"><%=wckkCessDed%></div></td>
 <td><div align="center"><%=StateCode%></div></td>
 <td><div align="center"><%=GstNo%></div></td>
	
           
           <td><div align="right"><%=refundFlag%></div></td>
		   <% 
			double netPayable = 0.0;
			   if("Y".equals(refundFlag)){
					netPayable = (tcFirstInstallment+wcFirstInstallment)+(asfRefundableForTC+asfRefundableForWC+asfServTaxForTC+asfServTaxForwC+tcSwbhaCessDed+wcSwbhaCessDed); 
					
			   }else{
					netPayable = (tcFirstInstallment+wcFirstInstallment)-(tcDeduction+wcDeduction+asfServTaxForTC+asfServTaxForwC+tcSwbhaCessDed+wcSwbhaCessDed+tckkCessDed+wckkCessDed);
					
					}
		   %>
             <td><div align="right"> <%=Math.round(netPayable)%></div></td>
		   
		   <% 
			  // if("Y".equals(refundFlag)){
				//	claimApprovedAmt = (tcFirstInstallment+wcFirstInstallment)+(asfRefundableForTC+asfRefundableForWC); 					
			  // }else{
				//	claimApprovedAmt = (tcFirstInstallment+wcFirstInstallment)-(tcDeduction+wcDeduction);					
				//	}
				claimApprovedAmt = (tcFirstInstallment+wcFirstInstallment);
		   %>
           
		   
           
           
         	<td>
				  <%
				  thiskey=ClaimConstants.FIRST_INSTALLMENT+ClaimConstants.CLM_DELIMITER1+memId+ClaimConstants.CLM_DELIMITER1+claimrefnumber;
				  mlidecision = "decision("+thiskey+")";
				  				  
				  %>
        <%tempname="approveClaims("+claimrefnumber+")";%>
     <%--    <html:select property="<%=tempname%>" name="cpTcDetailsForm">
														<html:option value="">Select</html:option>
                            <html:option value="AP">Accept</html:option>		
                            <html:option value="RE">Reject</html:option>
													</html:select> --%>
													
					<%	
					 // out.println("REFLAG" +refundFlag);
					if("Y".equals(refundFlag)){%>
						<html:select property="<%=mlidecision%>"    name="cpTcDetailsForm" onchange="enableRemarks(this);">
						
							<html:option value="">Select</html:option>
							<html:option value="AP">Accept</html:option>
							<html:option value="RT">Return</html:option>
						  <%--  <html:option value="TC">Temporary Closed</html:option>
							<html:option value="RE">Reject</html:option>
							<html:option value="NE">Revert Application</html:option> --%>
						 </html:select>  
						 <%}else{%> 
						     	<html:select property="<%=mlidecision%>"    name="cpTcDetailsForm" onchange="enableRemarks(this);">
						
							<html:option value="">Select</html:option>
							
							
							</html:select>
						  <%}%> 
				  </td>
				  <%
				  claimAmount = "approvedClaimAmount("+thiskey+")";
				  %>
				  <td align="center">	
     		 
				<%--  <html:text property="<%=claimAmount%>" name="cpTcDetailsForm"  onkeypress="return decimalOnly(this, event,13)" maxlength="16" onkeyup="isValidDecimal(this)" />			 --%>	
						<input type="text" name="<%=claimAmount%>" value="<%=claimApprovedAmt%>" onkeypress="return decimalOnly(this, event,13)" maxlength="16" onkeyup="isValidDecimal(this)" />
				  </td>
				  
				   <TD width="10%" align="left" valign="top" class="ColumnBackground1">	
							
							        <A href="javascript:submitFormClaimApproval('claimDeclarationView.do?method=claimDeclarationView&clmrefnum=<%=claimrefnumber%>')">
									View</A>
	                  
					</TD>
					
				  <%
				  mlicomments = "remarks("+thiskey+")";
				  %>
                  <td align="center">
                        <html:textarea property="<%=mlicomments%>" name="cpTcDetailsForm"/>					
                  </td>
				  <%
					String recData = "recommendationData("+thiskey+")";
				  %>
				  <td align="center"><table width="50%">
					<tr>
														<td width="25%" style="font:10px;"><%=recommendation%></td>
														<td width="25%"><input type="button" id="<%=thiskey%>" onclick="openRecBox(this.id);" value="View"/>
														<html:hidden property="<%=recData%>" name="cpTcDetailsForm" value="<%=data%>" /></td> 
					</tr></table>
</td>
				  <%
					String thiskey2=ClaimConstants.FIRST_INSTALLMENT+ClaimConstants.CLM_DELIMITER1+claimrefnumber;
					String reasData = "reasonData("+thiskey2+")";
					%>
				<td align="center">
					<table width="50%">
						<tr>
							<!-- This element will open a html page to select reasons of rejecting claim application -->
							<td width="25%">
								<input type="button" id="<%=thiskey2%>" onclick="openReasBox(this.id);" value="View"/>
								<html:hidden property="<%=reasData%>" name="cpTcDetailsForm"/>
							</td>
						</tr>
					</table>
                  </td>
                </tr>
             
                <%j++;%>
                </logic:iterate>
				
             <%--   <html:hidden property = "firstClmDtlIndexValue" name="cpTcDetailsForm" value="<%=String.valueOf(j)%>"/> --%>
              </table></td>
          </tr>
         
     <tr>
	<td><br></td>
     </tr>                       
     <tr>
      <td colspan="4"> <table width="100%" border="0" cellspacing="1" cellpadding="0">
	</table></td>
    </tr> 
  
          <tr align="center" valign="baseline">
            <td colspan="4"> 
            <div align="center">  
      <%--    <% url = "saveClaimApproval.do?method=saveClaimApproval";
         //System.out.println("url:"+url);
         %>
        
         <A href="<%=url%>"><img src="images/OK.gif" alt="Apply" width="49" height="37" border="0"></A> --%>
         <A href="javascript:submitForm('saveClaimApprovalForNewCases.do?method=saveClaimApproval')">
							<IMG src="images/Save.gif" alt="Save" width="49" height="37" border="0"></A> 
         <a href="home.do?method=getMainMenu&menuIcon=<%=session.getAttribute("menuIcon")%>"><img src="images/Cancel.gif" alt="Cancel" width="49" height="37" border="0"></a></div></td>
          </tr>
        </table></td>
    <td background="images/TableVerticalRightBG.gif">&nbsp;</td>
  </tr>
  <tr>
    <td width="20" align="right" valign="top"><img src="images/TableLeftBottom1.gif" width="20" height="15"></td>
    <td background="images/TableBackground2.gif"><div align="center"></div></td>
    <td align="left" valign="top"><img src="images/TableRightBottom1.gif" width="23" height="15"></td>
  </tr>
</table>
</html:form>
</body>
</html>
