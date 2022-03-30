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
	session.setAttribute("CurrentPage",
			"showRevivalRequests.do?method=showRevivalRequests");
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
							<TD width="25%" class="Heading">Revival Request Details</TD>
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
							<TD align="left" class="ColumnBackground">DAN FY</TD>
							<TD align="left" class="ColumnBackground">Base Amount</TD>
							<TD align="left" class="ColumnBackground">Decision</TD>
							<TD align="left" class="ColumnBackground">Remarks On Dan Generation</TD>
							<TD align="left" class="ColumnBackground">Created By</TD>
							<TD align="left" class="ColumnBackground">Remarks On Revival</TD>
						</TR>
						<% ArrayList revivals = (ArrayList)request.getAttribute("REVIVALS");
							for(int i=0;i<revivals.size();i++){
							String[] revival = (String[])revivals.get(i);
							String cgpan = revival[0];
							String danfy = revival[1];
							String amount = revival[2];
							String remark = revival[3];
							String createdBy = revival[4];
						%>
						<TR>
							<TD align="left" class="TableData"><a
								href="#"><%=cgpan %></a>
							<html:hidden name="gmForm" property="cgpans" value="<%=cgpan %>"/>	
							</TD>
							<TD align="left" class="TableData"><%=danfy %>
							<html:hidden name="gmForm" property="danfys" value="<%=danfy %>"/>
							</TD>
							<TD align="left" class="TableData"><html:text name="gmForm"
								property="baseAmts" value="<%=amount %>" onkeypress="return numbersOnly(this, event)"
						onkeyup="isValidNumber(this)" /></TD>
								<TD align="left" class="TableData">
								<html:select name="gmForm" property="decisions" >
									<html:option value="">Select</html:option>
									<html:option value="APPROVE">Approve</html:option>
								</html:select></TD>
							<TD align="left" class="TableData"><%=remark %></TD>
							<TD align="left" class="TableData"><%=createdBy %></TD>
							<TD align="left" class="TableData"><html:textarea name="gmForm" property="remarks"/></TD>
						</TR>
						<%} %>
					</TABLE>
					</TD>
				</TR>
				<TR>
					<TD height="20">&nbsp;</TD>
				</TR>
				<TR>
					<TD align="center" valign="baseline"><A href="#"
						onclick="javascript:submitForm('approveBatchRevivalRequests.do?method=approveBatchRevivalRequests');"> <IMG src="images/Save.gif"
						alt="Save" width="49" height="37" border="0"></A> <!-- <a href="javascript:submitForm('submitRequestForRevival.do?method=submitRequestForRevival');">
						<IMG src="images/OK.gif" alt="OK" width="49" height="37" border="0"></a>  -->
					<A href="javascript:printpage()"> <IMG src="images/Print.gif"
						alt="Print" width="49" height="37" border="0"></A>
					</DIV>
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
