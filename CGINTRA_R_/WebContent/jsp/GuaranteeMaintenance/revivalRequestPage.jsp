<%@page import="java.util.Calendar"%>
<%@ page language="java"%>
<%@ page import="java.util.ArrayList"%>

<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DecimalFormat"%>

<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic"%>
<%@ include file="/jsp/SetMenuInfo.jsp"%>
<%
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
%>
<%
	DecimalFormat decimalFormat = new DecimalFormat("##########.##");
%>
<%
	session.setAttribute("CurrentPage",
			"requestForRevivalInput.do");
%>

<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form
		action="submitRequestForRevival.do?method=submitRequestForRevival"
		method="POST" enctype="multipart/form-data">
		<TR>
			<TD width="20" align="right" valign="bottom"><IMG
				src="images/TableLeftTop.gif" width="20" height="31"></TD>
			<TD background="images/TableBackground1.gif"><IMG
				src="images/ReportsHeading.gif" width="121" height="25"></TD>
			<TD width="20" align="left" valign="bottom"><IMG
				src="images/TableRightTop.gif" width="23" height="31"></TD>
		</TR>
		<TR>
			<TD width="20" background="images/TableVerticalLeftBG.gif">&nbsp;</TD>
			<TD>
			<TABLE width="100%" border="0" align="left" cellpadding="0"
				cellspacing="0">
				<TR>
					<TD>
					<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td colspan="6" class="Heading1" align="center"><u>Credit
							Guarantee Fund Trust for Micro and Small Enterprises </u></td>
						</tr>
						<TR>
							<TD height="20">&nbsp;</TD>
						</TR>
					</table>
					<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
					<TR>
							<TD width="25%" class="Heading">CGPAN Details</TD>
							<TD><IMG src="images/TriangleSubhead.gif" width="19"
								height="19"></TD>
						</TR>
						<TR>
							<TD width="5%" colspan="12" class="Heading"><IMG
								src="images/Clear.gif" width="5" height="5"></TD>
						</TR>
						</TABLE>
					<TABLE width="100%" border="0" cellpadding="0" cellspacing="1">
						
						<TR>
							<TD align="left" class="ColumnBackground">CGPAN</TD>
							<TD align="left" class="TableData"><bean:write
								name="gmForm" property="cgpan" /></TD>
							<TD align="left" class="ColumnBackground">UNIT NAME</TD>
							<TD align="left" class="TableData"><bean:write
								name="gmForm" property="CgpanDetail.borrowerName" /></TD>
						</TR>
					</TABLE>
					<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
					<TR>
							<TD width="25%" class="Heading">Dan Details</TD>
							<TD><IMG src="images/TriangleSubhead.gif" width="19"
								height="19"></TD>
						</TR>
						<TR>
							<TD width="5%" colspan="12" class="Heading"><IMG
								src="images/Clear.gif" width="5" height="5"></TD>
						</TR>
						</TABLE>
					<TABLE width="100%" border="0" cellpadding="0" cellspacing="1">
						
						<TR align="left" valign="top">
							<TD align="left" class="ColumnBackground"><bean:message
								key="dan" /></TD>

							<TD align="left" class="ColumnBackground"><bean:message
								key="approvedAmount" /></TD>
							<TD align="left" class="ColumnBackground">DAN Amt</TD>
							<TD width="25%" align="left" class="ColumnBackground">
							<bean:message key="payId" /></TD>
							<TD width="10%" align="left" class="ColumnBackground">
							Realised Date</TD>
							<TD width="10%" align="left" class="ColumnBackground">
							Appr Date</TD>
							<TD width="10%" align="left" class="ColumnBackground">
							Expiry Date</TD>
							<TD align="left" class="ColumnBackground"><bean:message
								key="appropriation" /></TD>
							<TD align="left" class="ColumnBackground">DD No.</TD>
							<TD align="left" class="ColumnBackground"><bean:message
								key="Closure" /></TD>
						</TR>
						<tr>
							<logic:iterate id="object" name="gmForm"
								property="cgpanHistoryReport" indexId="id">
								<%
									com.cgtsi.reports.ApplicationReport dReport = (com.cgtsi.reports.ApplicationReport) object;
								%>
								<TR align="left" valign="top">
									<TD align="left" valign="top" class="TableData"><%=dReport.getDan()%>&nbsp;</TD>
									<TD align="left" valign="top" class="TableData">
									&nbsp;<%=dReport.getAppApprovedAmount()%></TD>
									<TD align="left" valign="top" class="TableData">
									&nbsp;<%=dReport.getAppGuaranteeFee()%></TD>
									<TD width="25%" align="left" valign="top"
										class="TableData">
									<%
											String payId = dReport.getPayId();
											if (payId == null) {
												payId = "Not Appropriated";
											}
									%> 
									&nbsp;<%=payId%></TD>
									<TD width="10%" align="left" valign="top"
										class="TableData">
									<%
												java.util.Date utilDate3 = dReport
																.getAppGuarStartDateTime();
														String formatedDate3 = null;
														if (utilDate3 != null) {
															formatedDate3 = dateFormat.format(utilDate3);
														} else {
															formatedDate3 = "";
														}
											%> &nbsp;&nbsp;<%=formatedDate3%></TD>

									<TD width="10%" align="left" valign="top"
										class="TableData">
									<%
												java.util.Date utilDate5 = dReport.getStartDate();
														String formatedDate5 = null;
														if (utilDate5 != null) {
															formatedDate5 = dateFormat.format(utilDate5);
														} else {
															formatedDate5 = "";
														}
									%> 
									&nbsp;&nbsp;<%=formatedDate5%></TD>
									<TD width="10%" align="left" valign="top"
										class="TableData">
									<%
												java.util.Date utilDate6 = dReport.getExpiryDate();
														String formatedDate6 = null;
														if (utilDate6 != null) {
															formatedDate6 = dateFormat.format(utilDate6);
														} else {
															formatedDate6 = "";
														}
									%> 
									&nbsp;&nbsp;<%=formatedDate6%></TD>
									<TD width="10%" align="left" valign="top"
										class="TableData">&nbsp;&nbsp;<%
												String userId = dReport.getUsrId();
														if (userId == null || userId.equals("null")) {
															userId = "";
														}
											%><%=dReport.getUsrId()%></TD>
									<TD width="10%" align="left" valign="top"
										class="TableData">
										<%
												String ddNum = dReport.getDdNum();
														if (ddNum == null || ddNum.equals("null")) {
															ddNum = "";
														}
										%>
										<%=dReport.getDdNum()%></TD>
									<TD width="10%" align="left" valign="top"
										class="TableData">
										<%
												java.util.Date utilDate7 = dReport.getClosureDate();
														String formatedDate7 = null;
														if (utilDate7 != null) {
															formatedDate7 = dateFormat.format(utilDate7);
														} else {
															formatedDate7 = "";
														}
										%> 
										&nbsp;&nbsp;<%=formatedDate7%></TD>
								</tr>
							</logic:iterate>
					</TABLE>					
								
					<%
						Calendar cal = Calendar.getInstance();
						int year = cal.get(Calendar.YEAR);
						year = year%100;
						int pyear1 = year - 1;
						int pyear2 = year - 2;
						String thisyear = "FY-"+String.valueOf(year);
						String preyear1 = "FY-"+String.valueOf(pyear1);
						String preyear2 = "FY-"+String.valueOf(pyear2);
					%>
					<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
						
					</TABLE>
					<TABLE width="100%" border="0" cellpadding="0" cellspacing="1"
						id="danTable">
						<TR>
							<TD width="1%" class="Heading"></TD>
						 	<!--<TD width="1%" class="Heading"><div align="center">No.</div></TD>  
							--><TD class="Heading"><div align="left">CGPAN</div></TD>
							<TD class="Heading"><div align="left">Dan FY</div></TD>
							<TD class="Heading"><div align="left">Base Amount</div></TD>
							<TD class="Heading"><div align="left">Remarks</div></TD>
						</TR>												
						<TR>
							<TD width="1%"><html:checkbox
								name="gmForm" property="chks" value="Yes"/></TD>
							 <!--<TD width="1%"><label id="index">1</label></TD> 
							--><TD><bean:write
								name="gmForm" property="cgpan" /></TD>	
							<TD>
							<html:select property="danfys" name="gmForm">
								<html:option value="<%=thisyear %>"><%=thisyear %></html:option>
								<html:option value="<%=preyear1 %>"><%=preyear1 %></html:option>
								<html:option value="<%=preyear2 %>"><%=preyear2 %></html:option>
							</html:select></TD>
							<TD><html:text
								name="gmForm" property="baseAmts" onkeypress="return numbersOnly(this, event)"
						onkeyup="isValidNumber(this)" /></TD>
							<TD><html:text
								name="gmForm" property="remarks" /></TD>
						</TR>
					</TABLE>					
					</TD>
				</TR>
				<TR>
					<TD height="20">&nbsp;<!-- <input type="hidden" name="rowcount" value="" /> --></TD>
				</TR>
				<TR>
					<TD align="center" valign="baseline">
					<DIV align="center">
					<A
						href="#" onclick="addRow('danTable');">
					<IMG src="images/AddRow2.gif" alt="Add Row" width="49" height="37" border="0"></A>
					<A
						href="#" onclick="deleteRow('danTable');">
					<IMG src="images/Delete.gif" alt="Delete Row" width="49" height="37" border="0"></A>
					 <A
						href="#" onclick="javascript:saveForm();">
					<IMG src="images/Save.gif" alt="Save" width="49" height="37" border="0"></A> 
					<!-- <a href="javascript:submitForm('submitRequestForRevival.do?method=submitRequestForRevival');">
						<IMG src="images/OK.gif" alt="OK" width="49" height="37" border="0"></a>  -->
					<A href="javascript:printpage()"> <IMG src="images/Print.gif"
						alt="Print" width="49" height="37" border="0"></A></DIV>
					</TD>
				</TR>
			</TABLE>
			</TD>
			<TD width="20" background="images/TableVerticalRightBG.gif">
			&nbsp;</TD>
		</TR>
		<TR>
			<TD width="20" align="right" valign="top"><IMG
				src="images/TableLeftBottom1.gif" width="20" height="15"></TD>
			<TD background="images/TableBackground2.gif">&nbsp;</TD>
			<TD width="20" align="left" valign="top"><IMG
				src="images/TableRightBottom1.gif" width="23" height="15"></TD>
		</TR>
	</html:form>
</TABLE>
<script type="text/javascript">
function saveForm(){
	//alert('saving');
	var table = document.getElementById('danTable');   
    var rowCount = table.rows.length;  
     for (var i=0;i<rowCount;i++)
     { 
         var row = table.rows[i]; 
         var fi1 = row.cells[2].childNodes[0].value;
         var fi2 = row.cells[3].childNodes[0].value;
         var fi3 = row.cells[4].childNodes[0].value;

         if(fi1 === ''){
 			alert('Please select Dan-FY.');
 			return false;
         }  
         
         if(fi2 == '0.0' || fi2 == '0'){
			alert('Please enter base amount.');
			return false;
         } 
         if(fi3 == ''){
 			alert('Please enter base amount.');
 			return false;
          }        

         if (fi1=="" && fi2=="" && fi3==""){ 
            alert ("please remove empty rows.");
            return false;
         }
     }
         
    
	//alert(rowCount);
	var rows = rowCount - 1; 
	//alert(rows);
	document.gmForm.target ="_self";
    document.gmForm.method="POST";
    document.gmForm.action="submitRequestForRevival.do?method=submitRequestForRevival&rowcount="+rows;
    document.gmForm.submit();
}

function addRow(tableID) { 
 	var table = document.getElementById(tableID);  
     for(var k=0;k<1;k++)
     {
    	 var rowCount = table.rows.length;  
    	 if(rowCount == 4){
			alert('Maximum 3 row can be added.');
			return false;
        }
         var row = table.insertRow(rowCount);  
         //var colCount = table.rows[0].cells.length; 
         var colCount = table.rows[1].cells.length; 
         for(var i=0; i<colCount; i++) { 
                    var newcell = row.insertCell(i);                      
						//newcell.innerHTML = table.rows[0].cells[i].innerHTML;  
						newcell.innerHTML = table.rows[1].cells[i].innerHTML;
             			switch(newcell.childNodes[0].type) {  
	                 		case "text":  
	                         	newcell.childNodes[0].value = "";  
	                         	break;  
	                 		case "checkbox":  
	                        	newcell.childNodes[0].checked = false;  
	                         	break;  
	                 		case "select-one":  
	                        	newcell.childNodes[0].value = "";  
	                         	break;         
                      	} //swtich close                           
     	} // Inner for close 
     } //Outer for close
 }

function deleteRow(tableID) {
         try {  
          var table = document.getElementById(tableID);  
          var rowCount = table.rows.length;  
          for(var i=0; i<rowCount; i++) {           
              var row = table.rows[i];  
              var chkbox = row.cells[0].childNodes[0]; 
                if(null != chkbox && true == chkbox.checked) {
                  if(rowCount == 2) {  
                      alert("Cannot delete all the rows.");  
                      break;  
                  }  
                  table.deleteRow(i);  
                  rowCount--;  
                  i--;  
           }  
        }  
     }catch(e) {  
        alert(e);  
    }            
 } 
</script>
