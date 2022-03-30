<%@ page language="java"%>
<%@ page import="com.cgtsi.claim.ClaimConstants"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.text.ParsePosition"%>
<%@ page import="com.cgtsi.admin.User"%>
<%@ page import="com.cgtsi.action.BaseAction"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.cgtsi.actionform.ClaimActionForm"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic"%>
<%@ include file="/jsp/SetMenuInfo.jsp" %>
<% session.setAttribute("CurrentPage","saveClaimProcessDetails.do?method=saveClaimProcessDetails");%>
<% 
ClaimActionForm claimActionForm = (ClaimActionForm)session.getAttribute("cpTcDetailsForm") ;
String loggedUserId = (String)claimActionForm.getUserId();
%>
<%
String mlidecision = "";
String thiskey = "";
String thisseckey = "";
String claimAmount = "";
String mlicomments = "";
String memId = "";
String claimrefnumber = "";
double approvedamount = 0.0;
java.util.Date npadate = null;
String npaDtStr = "";
double osAmntAsOnNPA = 0.0;
double appliedClaimAmnt = 0.0;
double eligibleAmnt = 0.0;
String url = "";
String cgclan = "";
java.text.SimpleDateFormat sdf = null;
String approvedClmAmount = null;
String forwardedUserId = "";
String comments ="";
String unitName = "";
%>
<script type="text/javascript">
	function clearComments(){
	//var total = document.getElementById('').value;
	var total1 = document.forms[0].firstClmDtlIndexValue.value;
	//alert('total1:'+total1);
	
		for(var i=0;i<total1;i++){
			var k = i;
			var key1 = document.getElementById(k).value;
			//var key2 = document.getElementById(53).value;
			//alert('key1:'+key1);
			//alert('key2:'+key2);
			
			findObj("remarks("+key1+")").value = '';
			//findObj("remarks("+key2+")").value = '';
		}
	/* var total2 = document.forms[0].secondClmDtlIndexValue.value;
		alert('total2:'+total2);
		for(var i=0;i<2;i++){
			var comment = findObj("remarks("+i+")");
			alert('comment2'+comment);
		} */	
	}
</script>
<body onload="processForwardedToFirst(),processForwardedToSecond()">

<html:errors/>
<html:form action="saveClaimProcessDetails.do?method=saveClaimProcessDetails" method="POST" enctype="multipart/form-data">
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
      <DIV align="right">			
      		<A HREF="javascript:submitForm('helpProcessClaims.do')">
      	        HELP</A>
      </DIV>            
    <table width="100%" border="0" cellspacing="1" cellpadding="0">
          <tr>
            <td colspan="4"><table width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td width="35%" class="Heading">&nbsp;Authorize Claim Applications</td>
                  <td colspan="3"><img src="images/TriangleSubhead.gif" width="19" height="19"></td>
                </tr>
                <tr>
                  <td colspan="4" class="Heading"><img src="images/Clear.gif" width="5" height="5"></td>
                </tr>
              </table></td>
          </tr>
          
          <logic:notEqual property="firstCounter" value="0" name="cpTcDetailsForm">
          <tr> 
	      <td colspan="4" class="SubHeading">&nbsp;<bean:message key="firstclaim"/></td>
          </tr>
          
          <tr>
            <td colspan="4"> <table width="100%" border="0" cellspacing="1" cellpadding="0">
                <tr class="HeadingBg">
                  <td valign="middle" class="HeadingBg"> <div align="center"><strong>&nbsp;Sr.<br>
                      No</strong></div></td>
                  <td class="HeadingBg"><div align="center">Member<br>
                      ID </div></td>
                  <td class="HeadingBg"><div align="center">Borrower<br>
                      Name </div></td>
                  <td class="HeadingBg"><div align="center">
                      Claim <br>
                      Ref <br>
                      Number</div></td>
                  <td valign="top" class="HeadingBg"><div align="center">&nbsp;&nbsp;
                      <strong>Total Approved Application Amount</strong><br><bean:message key="inRs" /></div></td>
                  <td class="HeadingBg"> <div align="center">NPA Date&nbsp;</div></td>
                  <td class="HeadingBg"><div align="center">Total Outstanding Amount<br>
                      as on NPA <br>
                      Date&nbsp;<br><bean:message key="inRs" /></div></td>
                  <td class="HeadingBg"><div align="center"><strong>Applied
                      Claim Amount</strong><br><bean:message key="inRs" /></div></td>
                  <td align="center" valign="top"> <div align="center">
                      <p class="HeadingBg"><strong>&nbsp;Decision</strong></p>
                    </div></td>		  
					  <td class="HeadingBg"><div align="center">First Installment<br><bean:message key="inRs"/></div></td>
					  <td class="HeadingBg"><div align="center"><strong>Forwarded To</strong></div></td>                    
					  <td class="HeadingBg"><div align="center">Approved Claim Amount<br><bean:message key="inRs"/></div></td>
					<td class="HeadingBg"><div align="center">
					<table>
						<tr>Comments</tr>
						<tr><td class="HeadingBg"><div align="center">Clear All
							<input type="checkbox" name="allclear" onclick="if(checked){clearComments();}"/>
						</td>
						</tr>
					</table>
					</div></td>
					
                </tr>
                <%
                int i=1;
                int j=0;
                %>
                <logic:iterate property="firstInstallmentClaims" name="cpTcDetailsForm" id="object">
                <%
                com.cgtsi.claim.ClaimDetail claimdetail = (com.cgtsi.claim.ClaimDetail)object;
                memId = claimdetail.getMliId();
                unitName = claimdetail.getSsiUnitName();
                claimrefnumber = claimdetail.getClaimRefNum();
                approvedamount = claimdetail.getApplicationApprovedAmount();
                npadate = claimdetail.getNpaDate();
                if(npadate != null)
                {
                    sdf = new SimpleDateFormat("dd/MM/yyyy");
                    npaDtStr = sdf.format(npadate);
                }                
                osAmntAsOnNPA = claimdetail.getOutstandingAmntAsOnNPADate();
                appliedClaimAmnt = claimdetail.getAppliedClaimAmt();                
                eligibleAmnt = claimdetail.getEligibleClaimAmt();
              /*  url = "displayClaimRefNumberDtls.do?method=displayClaimRefNumberDtls&"+ClaimConstants.CLM_CLAIM_REF_NUMBER+"="+claimrefnumber;        */         
                url = "displayClmApplicationDtlNewRev.do?method=displayClmApplicationDtlNewRev&"+ClaimConstants.CLM_CLAIM_REF_NUMBER+"="+claimrefnumber;           
                String id1 = "MEMBERID#" + j;
                String id2 = "CLMREFNUMBERID#" + j;                
                approvedClmAmount = claimdetail.getTotalAmtPayNow();
                comments = claimdetail.getComments();
              //  System.out.println("Comments:"+comments);
                %>
                <tr class="TableData">
                  <td height="40"> <div align="center"><%=i%></div></td>
                  <td id="<%=id1%>"><%=memId%></td>
                  <td><div align="right"><%=unitName%></div></td>
                  <td id="<%=id2%>"><a href="<%=url%>"><%=claimrefnumber%></a></td>
		  <%
		      String clmRefNumIndex = "CLMREFNUM##" + j;
		  %>
		  <html:hidden property = "<%=clmRefNumIndex%>" name="cpTcDetailsForm" value="<%=claimrefnumber%>"/>
                  <td><div align="right"><%=approvedamount%></div></td>
                  <td><div align="center"><%=npaDtStr%></div></td>
                  <td><div align="right"><%=osAmntAsOnNPA%></div></td>
                  <td><div align="right"><%=appliedClaimAmnt%></div></td>
				  <td>
				  <%
				  thiskey=ClaimConstants.FIRST_INSTALLMENT+ClaimConstants.CLM_DELIMITER1+memId+ClaimConstants.CLM_DELIMITER1+claimrefnumber;
				  mlidecision = "decision("+thiskey+")";
				  				  
				  %>
				<html:select property="<%=mlidecision%>" name="cpTcDetailsForm"  onchange ="javascript:processForwardedToFirst(this)">
					<html:option value="">Select</html:option>
          <html:option value="<%=ClaimConstants.CLM_TEMPORARY_CLOSE%>">Temporary Closed</html:option>
        	<% if(!(loggedUserId.equals("SANPOOJ0001") || loggedUserId.equals("DEERAO0001")|| loggedUserId.equals("MAYGAIK0001")|| loggedUserId.equals("RICHACH0001"))) { %>	
          <html:option value="<%=ClaimConstants.CLM_TEMPORARY_REJECT%>">Temporary Reject</html:option>
          <html:option value="<%=ClaimConstants.CLM_FORWARD_STATUS%>">Forward</html:option>
          <html:option value="<%=ClaimConstants.CLM_WITHDRAWN%>">Claim Withdrawn</html:option>
					<html:option value="<%=ClaimConstants.CLM_APPROVAL_STATUS%>">Approve</html:option>
          <html:option value="<%=ClaimConstants.CLM_REJECT_STATUS%>">Reject</html:option>
					<!-- <html:option value="<%=ClaimConstants.CLM_HOLD_STATUS%>">Hold</html:option> -->          
					<% } %>
          
         
				</html:select>
				  </td>
				  <td align="center">
					<div align="right"><%=eligibleAmnt%></div>
				  </td>
				  <%
				  thiskey=ClaimConstants.FIRST_INSTALLMENT+ClaimConstants.CLM_DELIMITER1+memId+ClaimConstants.CLM_DELIMITER1+claimrefnumber;
				  forwardedUserId = "forwardedToIds("+thiskey+")";				  
				  %>
				  <td>
				  <html:select property="<%=forwardedUserId%>" name="cpTcDetailsForm">
					<html:option value="">Select</html:option>
					<html:options property="userIds" name="cpTcDetailsForm"/>
				  </html:select>				  
				  </td>
				  <%
				  claimAmount = "approvedClaimAmount("+thiskey+")";
				  %>
				  <td align="center">				  
				  <html:text property="<%=claimAmount%>" name="cpTcDetailsForm" onkeypress="return decimalOnly(this, event,13)" maxlength="16" onkeyup="isValidDecimal(this)"/>					
				  </td>
				  <%
				  mlicomments = "remarks("+thiskey+")";
				  %>
                  		<td align="center">
                        <html:textarea property="<%=mlicomments%>" name="cpTcDetailsForm" value="<%=comments%>" />
						<input type="hidden" id="<%=String.valueOf(j)%>" value="<%=thiskey%>" />						
                  </td>
                </tr>
                <%i++;%>
                <%j++;%>
                </logic:iterate>
                <html:hidden property = "firstClmDtlIndexValue" name="cpTcDetailsForm" value="<%=String.valueOf(j)%>"/>
				
              </table></td>
          </tr>
          </logic:notEqual>
     <tr>
	<td><br></td>
     </tr>                       
     <logic:notEqual property="secondCounter" value="0" name="cpTcDetailsForm">
    <tr> 
      <td colspan="4" class="SubHeading">&nbsp;<bean:message key="secondclaim"/></td>
    </tr>    
    <tr>
      <td colspan="4"> <table width="100%" border="0" cellspacing="1" cellpadding="0">
	  <tr class="HeadingBg">
	    <td valign="middle" class="HeadingBg"> <div align="center"><strong>&nbsp;Sr.<br>
		No</strong></div></td>
	    <td class="HeadingBg"><div align="center">Member<br>
		ID </div></td>
	    <td class="HeadingBg"><div align="center">
		Claim <br>
		Ref <br>
		Number</div></td>
	    <td valign="top" class="HeadingBg"><div align="center">&nbsp;&nbsp;
		<strong>Total Approved Application Amount<br><bean:message key="inRs"/></strong><strong></strong></div></td>
	    <td class="HeadingBg"> <div align="center">NPA Date&nbsp;</div></td>
	    <td class="HeadingBg"><div align="center">Total Outstanding Amount<br>
		as on NPA <br>
		Date&nbsp;<br><bean:message key="inRs"/></div></td>
	    <td class="HeadingBg"><div align="center"><strong>Applied
		Claim Amount</strong><br><bean:message key="inRs" /></div></td>
	    <td align="center" valign="top"> <div align="center">
		<p class="HeadingBg"><strong>&nbsp;Decision</strong></p>
	      </div></td>
			  <td class="HeadingBg"><div align="center">Eligible Claim Amount<br><bean:message key="inRs" /></div></td>
			  <td class="HeadingBg"><div align="center"><strong>Forwarded To</strong></div></td>
			  <td class="HeadingBg"><div align="center">Approved Claim Amount<br><bean:message key="inRs" /></div></td>
	    <td class="HeadingBg"><div align="center">Comments</div></td>
	  </tr>
	  <%int k=0;%>
	  <%int m=1;%>
	  <logic:iterate property="secondInstallmentClaims" name="cpTcDetailsForm" id="object">
	  <%
	  com.cgtsi.claim.ClaimDetail secondClaimdetail = (com.cgtsi.claim.ClaimDetail)object;
	  memId = secondClaimdetail.getMliId();
   
	  claimrefnumber = secondClaimdetail.getClaimRefNum();
	  approvedamount = secondClaimdetail.getApplicationApprovedAmount();
	  npadate = secondClaimdetail.getNpaDate();
	  if(npadate != null)
	  {
	      sdf = new SimpleDateFormat("dd/MM/yyyy");
	      npaDtStr = sdf.format(npadate);
	  }	  
	  osAmntAsOnNPA = secondClaimdetail.getOutstandingAmntAsOnNPADate();
	  appliedClaimAmnt = secondClaimdetail.getAppliedClaimAmt();                
	  eligibleAmnt = secondClaimdetail.getEligibleClaimAmt();
	  cgclan = secondClaimdetail.getCGCLAN();
	  url = "displayClaimRefNumberDtls.do?method=displayClaimRefNumberDtls&"+ClaimConstants.CLM_CLAIM_REF_NUMBER+"="+claimrefnumber;
	  String id3 = "MEMBERID##" + k;
	  String id4 = "CLMREFNUMBERID##" + k;	 
	  approvedClmAmount = secondClaimdetail.getTotalAmtPayNow();
	  thisseckey = ClaimConstants.SECOND_INSTALLMENT+ClaimConstants.CLM_DELIMITER1+memId+ClaimConstants.CLM_DELIMITER1+claimrefnumber+ClaimConstants.CLM_DELIMITER1+cgclan;
	  %>

	  <tr class="TableData">
	    <td height="40"> <div align="center"><%=m%></div></td>
	    <td id="<%=id3%>"><%=memId%></td>
	    <td id="<%=id4%>"><a href="<%=url%>"><%=claimrefnumber%></a></td>
	    <!--
                  <td>
                  <table width="100%" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                  <td id="<%=id4%>"><%=claimrefnumber%></td>
                  </tr>
                  <tr>
                  <td><a href="<%=url%>">View Details</a></td>
                  </tr>
                  </table>
                  </td>
             -->
	    <td><div align="right">&nbsp;<%=approvedamount%></div></td>
	    <td><div align="center"><%=npaDtStr%></div></td>
	    <td><div align="right"><%=osAmntAsOnNPA%></div></td>
	    <td><div align="right">&nbsp;<%=appliedClaimAmnt%></div></td>
			  <td><div align="center">
			  <%			    
			    mlidecision = "decision("+thisseckey+")";			    
			    String val = "CGCLAN";			    
			  %>
			  <html:hidden property = "CGCLAN" name="cpTcDetailsForm" value="<%=cgclan%>"/>
			  <%
			      String cgclanindex = "CGCLAN##" + k;
			  %>
			  <html:hidden property = "<%=cgclanindex%>" name="cpTcDetailsForm" value="<%=cgclan%>"/>
			  <%
			      String clmRefNumbIndex = "CLMREFNUM###" + k;
			  %>
			  <html:hidden property = "<%=clmRefNumbIndex%>" name="cpTcDetailsForm" value="<%=claimrefnumber%>"/>			  
			  <html:select property="<%=mlidecision%>" name="cpTcDetailsForm" onchange ="javascript:processForwardedToSecond(this)">
				<html:option value="">Select</html:option>
				<html:option value="<%=ClaimConstants.CLM_HOLD_STATUS%>">Hold</html:option>
				<html:option value="<%=ClaimConstants.CLM_APPROVAL_STATUS%>">Approve</html:option>
				<html:option value="<%=ClaimConstants.CLM_REJECT_STATUS%>">Reject</html:option>
				<html:option value="<%=ClaimConstants.CLM_FORWARD_STATUS%>">Forward</html:option>
			  </html:select>
			  </div></td>
			  <td align="center">
				<div align="right">&nbsp;<%=eligibleAmnt%></div>
			  </td>
			  <%			  
			  forwardedUserId = "forwardedToIds("+thisseckey+")";				  
			  %>			  
				  <td>
				  <html:select property="<%=forwardedUserId%>" name="cpTcDetailsForm">
					<html:option value="">Select</html:option>
					<html:options property="userIds" name="cpTcDetailsForm"/>
				  </html:select>				  
				  </td>			  
			  <td align="center">				  
			  <%
			     claimAmount = "approvedClaimAmount("+thisseckey+")";
			  %>
			  <html:text property="<%=claimAmount%>" name="cpTcDetailsForm" value="<%=approvedClmAmount%>" onkeypress="return decimalOnly(this, event,13)" maxlength="16" onkeyup="isValidDecimal(this)"/>					
			  </td>
			<td align="center">
			<%
			  mlicomments = "remarks("+thisseckey+")";
			%>
			     <html:textarea property="<%=mlicomments%>" name="cpTcDetailsForm"/>					
	    </td>
	  </tr>
	  <%k++;%>
	  <%m++;%>
	  </logic:iterate>
	  <html:hidden property = "secondClmDtlIndexValue" name="cpTcDetailsForm" value="<%=String.valueOf(k)%>"/>
	</table></td>
    </tr> 
    </logic:notEqual>
          <tr align="center" valign="baseline">
            <td colspan="4"> 
            <div align="center">            
            <A href="javascript:submitForm('saveClaimProcessDetails.do?method=saveClaimProcessDetails')"><img src="images/Save.gif" alt="Save" width="49" height="37" border="0"></a>
            <a href="javascript:document.cpTcDetailsForm.reset()"><img src="images/Reset.gif" alt="Reset" width="49" height="37" border="0"></a>
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
