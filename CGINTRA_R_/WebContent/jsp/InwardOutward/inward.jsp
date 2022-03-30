 <%@ page language="java"%>
<%@ page import="java.util.ArrayList"%>
<%@page import ="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="org.apache.struts.validator.DynaValidatorActionForm"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic" %>
<%@ include file="/jsp/SetMenuInfo.jsp" %>
<% 
  session.setAttribute("CurrentPage","Inward.do?method=inwardNew");
  SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
   Date systemDate  = new Date();
   String sysDate = dateFormat.format(systemDate);
  DynaValidatorActionForm dynaForm = (DynaValidatorActionForm)session.getAttribute("ioForm") ;
  dynaForm.set("inwardDts",sysDate);
%>
<HTML>
<HEAD>
	<TITLE> Add/Remove dynamic rows in HTML table </TITLE>
	 <SCRIPT language="javascript">  
   
   var req; 
      
  	/* 	 
  	* Get the second options by calling a Struts action 	 
  	*/	 
        
  	function retrieveSecondOptions(){ 
      
        section = document.getElementById('section'); 
  	//Nothing selected 	 
  	if(section.selectedIndex==0){ 	 
  	return; 	 
        } 	 
  
	selectedOption = section.options[section.selectedIndex].value; 	 
  	//get the (form based) params to push up as part of the get request 	 
        //afterTcMli.do?method=getDistricts
  	url="workshopDetails.do?method=getUserNamesForSection&section="+section.value; 
      
  	//Do the Ajax call 	 
  	if (window.XMLHttpRequest){ // Non-IE browsers 	 
  	req = new XMLHttpRequest(); 	 
  	//A call-back function is define so the browser knows which function to call after the server gives a reponse back 	 
  	req.onreadystatechange = populateSecondBox; 	 
        try { 	 
  	req.open("GET", url, true); //was get 	 
  	} catch (e) { 	 
  	alert("Cannot connect to server"); 	 
  	} 	 
 	req.send(section); 	 
 	} else if (window.ActiveXObject) { // IE 	 
 	req = new ActiveXObject("Microsoft.XMLHTTP"); 	 
 	if (req) { 	 
	req.onreadystatechange = populateSecondBox; 	 
	req.open("GET", url, true); 	 
	
        req.send(); 	 
	} 	 
 	} 	   
	} 	 

	function populateSecondBox(){ 	  
	document.getElementById('assignedTo').options.length = 0; 	 
  	if (req.readyState == 4) { // Complete 	 
  	if (req.status == 200) { // OK response 	 
  	textToSplit = req.responseText; 
        //alert ("the data: "+textToSplit);
                            if(textToSplit == '803')
                              { 	 
                                alert("No select option available on the server") 	 
                               } 	 //if close

	var returnElements=textToSplit.split("||"); 
  
	for ( var i=0; i<returnElements.length; i++ ){ 	 
               valueLabelPair = returnElements[i].split(";"); 	 
              // alert("the first value is :-=-->"+valueLabelPair[0] );
               document.getElementById('assignedTo').options[i] = new Option(valueLabelPair[0], valueLabelPair[1]); 	 
       	} 	 //for close
  	} //ok close	 
            else { 	 
        	         alert("Bad response by the server"); 	 
        	       } 
  } //complet close          	 
  
	} 	 
   
   
   
   
  
   
  function isInteger(s){
	var i;
    for (i = 0; i < s.length; i++){   
        // Check that current character is number.
        var c = s.charAt(i);
        if (((c < "0") || (c > "9"))) return false;
    }
    // All characters are numbers.
    return true;
}

function stripCharsInBag(s, bag){
	var i;
    var returnString = "";
    // Search through string's characters one by one.
    // If character is not in bag, append to returnString.
    for (i = 0; i < s.length; i++){   
        var c = s.charAt(i);
        if (bag.indexOf(c) == -1) returnString += c;
    }
    return returnString;
}

function daysInFebruary (year){
	// February has 29 days in any year evenly divisible by four,
    // EXCEPT for centurial years which are not also divisible by 400.
    return (((year % 4 == 0) && ( (!(year % 100 == 0)) || (year % 400 == 0))) ? 29 : 28 );
}
function DaysArray(n) {
	for (var i = 1; i <= n; i++) {
		this[i] = 31
		if (i==4 || i==6 || i==9 || i==11) {this[i] = 30}
		if (i==2) {this[i] = 29}
   } 
   return this
}

  function isDate(){
  var dtCh= "/";
  var minYear=1900;
  var maxYear=2050;
  var dtStr=findObj("ltrDts").value;
 // alert("dtStr:"+dtStr);
//  alert("dtStrLen:"+dtStr.length);
    if (dtStr.length >= 11 || dtStr.length < 10) 
    {
          alert("Please correct Letter Date, date format should be : dd/MM/yyyy");
          return false;
     }
	var daysInMonth = DaysArray(12)
	var pos1=dtStr.indexOf(dtCh);
 // alert("pos1:"+pos1);
	var pos2=dtStr.indexOf(dtCh,pos1+1);
//  alert("pos2:"+pos2);
	var strDay=dtStr.substring(0,pos1);
 // alert("strDay:"+strDay);
	var strMonth=dtStr.substring(pos1+1,pos2);
 // alert("strMonth:"+strMonth);
	var strYear=dtStr.substring(pos2+1);
	    strYr=strYear;
//   alert("strYear:"+strYear);    
	if (strDay.charAt(0)=="0" && strDay.length>1) strDay=strDay.substring(1)
	if (strMonth.charAt(0)=="0" && strMonth.length>1) strMonth=strMonth.substring(1)
	for (var i = 1; i <= 3; i++) {
		if (strYr.charAt(0)=="0" && strYr.length>1) strYr=strYr.substring(1)
	}
	month=parseInt(strMonth)
	day=parseInt(strDay)
	year=parseInt(strYr)
	if (pos1==-1 || pos2==-1){
		alert("Please correct Letter Date, date format should be : dd/MM/yyyy")
		return false
	}
	if (strMonth.length<1 || month<1 || month>12){
		alert("Please enter a valid month for Letter Date")
		return false
	}
	if (strDay.length<1 || day<1 || day>31 || (month==2 && day>daysInFebruary(year)) || day > daysInMonth[month]){
		alert("Please enter a valid day for Letter Date")
		return false
	}
	if (strYear.length != 4 || year==0 || year<minYear || year>maxYear){
		alert("Please enter a valid 4 digit year between "+minYear+" and "+maxYear)
		return false
	}
	if (dtStr.indexOf(dtCh,pos2+1)!=-1 || isInteger(stripCharsInBag(dtStr, dtCh))==false){
		alert("Please enter a valid date")
		return false
	}
return true
}

function chkdate(objName) {
   
                              var dtCh= "/";
                              var minYear=1900;
                              var maxYear=2050;
                              var strDate;
                              var strDay;
                              var strMonth;
                              var strYear;
                              var strYr;
                              var intday;
                              var intMonth;
                              var intYear;                           
                              var datefield = objName;                              
                              var intElementNr;                          
                             
                             var dtStr = datefield;
                           //   alert("strDate.length:"+dtStr.length);
                             if (dtStr.length >= 11 || dtStr.length < 10) {
                              alert("Please correct Instrument Date, date format should be : dd/MM/yyyy")
                              return false;
                              }
                                   
                    var daysInMonth = DaysArray(12)
                    var pos1=dtStr.indexOf(dtCh);
                   //  alert("pos1:"+pos1);
                    var pos2=dtStr.indexOf(dtCh,pos1+1);
                  //    alert("pos2:"+pos2);
                    var strDay=dtStr.substring(0,pos1);
                   //  alert("strDay:"+strDay);
                    var strMonth=dtStr.substring(pos1+1,pos2);
                  //  alert("strMonth:"+strMonth);
                    var strYear=dtStr.substring(pos2+1);
                    strYr=strYear;
                  //  alert("strYear:"+strYear);    
                  month=parseInt(strMonth.value);
                  day=parseInt(strDay.value);
                  year=parseInt(strYr);
                //  alert("day:"+day+"month:"+month+"year:"+year);
                  if (pos1==-1 || pos2==-1){
                      alert("Please correct Instrument Date, date format should be : dd/MM/yyyy")
                      return false
                    }
                	if (strMonth.length<1 || month<1 || month>12){
                  	alert("Please enter a valid month for Intrument Date")
                  	return false
                  }
                  if (strDay.length<1 || day<1 || day>31 || (month==2 && day>daysInFebruary(year)) || day > daysInMonth[month]){
                  	alert("Please enter a valid day for Instrument Date")
                    return false
                }
                  if (strYear.length != 4 || year==0 || year<minYear || year>maxYear){
                  alert("Please enter a valid 4 digit year between "+minYear+" and "+maxYear)
                	return false
                  }
                 if (dtStr.indexOf(dtCh,pos2+1)!=-1 || isInteger(stripCharsInBag(dtStr, dtCh))==false){
                    alert("Please enter a valid date")
                  	return false
                   } 
                    return true;     
                  }                         
         
    function calSum(tableID)
    {
   // alert("calSum entered");
     var table = document.getElementById(tableID);  
     var rowCount = table.rows.length; 
	 
     var sum=0;
      for (var i=1;i<rowCount;i++)
      {
       var row = table.rows[i]; 
       var fi5 = row.cells[6].childNodes[0].value;
  //     alert("fi6:"+fi6);
       for (var k = 0; k < fi5.length; k++) {
			var ch = fi5.substring(k, k+1);

				if ((ch < "0" || ch > "9")  && ch != "-" && ch != '.')
			  {
				alert('the value Should be number at fi5 Lopcation :--'+fi5);
                          return false;
			  }
		  }
        if (!(isNaN(parseFloat(fi5))) && fi5!="")
        {
			sum+=parseFloat(fi5);
        
		}
     
      }  
    //alert("Sum"+sum);
   // document.adddata.instrumentTotal.value=sum;
		instrumentTotal.innerHTML=sum; 
    }
         
         
         
         function addRow(tableID) { 
         var table = document.getElementById(tableID);  
         for(var k=0;k<5;k++)
         {
             var rowCount = (table.rows.length);
			//alert(rowCount);
             var row = table.insertRow(rowCount);  
             var colCount = table.rows[0].cells.length;  
             for(var i=0; i<colCount; i++) { 
                        var newcell = row.insertCell(i);  
                        if (i==1)
                        {
                       var cell2 = row.insertCell(1);
			                 cell2.innerHTML = rowCount;
                        }
                     else{

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
                    //  alert("newcell.childNodes[0].type:"+newcell.childNodes[0].type);  
                  
                           }         //else close 
               
         } // Inner for close 
         } //Outer for close
         }
         
        function deleteRow(tableID) {  
       // alert("delete Row"+tableID);
            try {  

                var table = document.getElementById(tableID);  
    
                 var rowCount = table.rows.length;  
  
            //   alert(rowCount);

             for(var i=0; i<rowCount; i++) {  
             
                 var row = table.rows[i];  
                 var chkbox = row.cells[0].childNodes[0];  
                     
               // alert("the value of chek is :-->"+chkbox.checked);
                   if(null != chkbox && true == chkbox.checked) {  
                // alert("inside selected"+chkbox.checked);

                     if(rowCount <= 1) {  

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
         
//         function save(tableID)
//              {
//               var table = document.getElementById(tableID);  
//    
//       
//             var rowCount = table.rows.length;  
//       
//             //   alert ("Inside save method..............!"+rowCount);
//                document.ioForm.target ="_self";
//                 document.ioForm.method="POST";
//                 document.ioForm.action="/cgtsi-ViewController-context-root/Inward.do?method=inwardInput&rowcount="+rowCount;
//                // alert("action submit");
//                 document.ioForm.submit();
//              }

            function save(tableID)
              {
	    
               var table = document.getElementById(tableID);  
                var rowCount = table.rows.length;  
		//alert('rowCount:'+rowCount);
                 for (var i=1;i<rowCount;i++)
                { 
                     var row = table.rows[i]; 
                     var fi1 = row.cells[2].childNodes[0].value;
                     var fi2 = row.cells[3].childNodes[0].value;
                     var fi3 = row.cells[4].childNodes[0].value;
                     var fi4 = row.cells[5].childNodes[0].value;
                     var fi5 = row.cells[6].childNodes[0].value;
                     var fi6 = row.cells[7].childNodes[0].value;
                     var fi7 = row.cells[8].childNodes[0].value;
                     var fi8 = row.cells[9].childNodes[0].value;
                 //    var fi9 = row.cells[10].childNodes[0].value;
 //                 if (fi1!="")
//                {
//                alert ("fi1 is not null");
//                }
//                if (fi2!="")
//                {
//                alert ("fi2 is not null");
//                }
//                if (fi3!="")
//                {
//                alert ("fi3 is not null");
//                }
       //    alert("fi4:"+fi4);
            if(fi4!=""){
            if (chkdate(fi4) == false) {
            alert("That Instrument date is " +fi4+ "  invalid. date Format should be dd/MM/yyyy  Please try again.");
             return false;
            }
            }
//              if (fi5!="")
//                {
//                 var datefield = fi5;
//              // alert("fi5"+fi5);
//             if (chkdate(fi5) == false) {
//                  //datefield.select();
//                  alert("That Instrument date is invalid. date Format should be dd/MM/yyyy  Please try again.");
//                   //datefield.focus();
//                    return false;
//                  }
//              }

             if (fi5!="")
                {
                //alert ("fi8 is not null");
                	for (var k = 0; k < fi5.length; k++) {
			var ch = fi5.substring(k, k+1);

			if ((ch < "0" || ch > "9")  && ch != "-" && ch != '.')
			  {
			 alert("the value Should be number at fi5 Location :-->"+fi5);
                          return false;
			  }
		}
                }
//                 if (fi2=="" )
//                   { 
//                      alert ("please remove empty rows from the table ");
//                      return false;
//                   }

               if (fi2=="" && fi3=="" && fi4=="" && fi5=="" && fi6=="")
                   { 
                      alert ("please remove empty rows from the table ");
                      return false;
                   }
				   
				if(fi8==""){
                 alert('please select Trancate System.');
                 
                  return false;
				}
                 //  alert ("AT the End of loop .......!");
               }
                  ///////////////////////////////////////////////////////////////
          
             
			 
			 var ltrdt = document.forms[0].ltrDts.value;
			 if(ltrdt == ""){
				alert('Please enter letter date');
				return false;
			 }
			 var bankName = document.forms[0].bankNames.value;
			 if(bankName == ""){
				alert('Please select bank name');
				return false;
			 }
				var ro = rowCount -1;
                 document.ioForm.target ="_self";
                 document.ioForm.method="POST";
                 document.ioForm.action="InwardNew.do?method=inwardInput&rowcount="+ro;
                 document.ioForm.submit();
             
              }

     </SCRIPT>  
</HEAD>
<BODY>
  <html:errors/>
	<html:form name="ioForm" type="org.apache.struts.validator.DynaValidatorActionForm"  action="Inward.do?method=inwardNew" method="POST">
    
    <TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
									<TR>
										<TD width="30%" class="Heading">Insert Inward Details&nbsp;</TD>
										<TD width="70%"><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
									</TR>
									<TR>
										<TD colspan="4" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
									</TR>
            		</TABLE>
 
    <TABLE width="100%" border="0" cellpadding="0" cellspacing="1">
  	  <TR>
            <td width="30%" class="ColumnBackground "><div align="right">Bank Name&nbsp;</div></td>
             <TD width="100" class="TableData"> <html:select property="bankNames" name="ioForm">
             <html:option value="">Select</html:option>
             <html:options name="ioForm" property="getAllMembers"/>
             </html:select></TD>
             <td colspan="2" width="17%" class="ColumnBackground "><div align="right">Place&nbsp;</div></td> 
             <TD width="100" class="TableData"> <html:text property="places" name="ioForm" size="20" value=""/> </TD>  
             </TR>
             <TR><td width="30%" class="ColumnBackground "><div align="right">Ltr Ref No&nbsp;</div></td>
             <TD width="100" class="TableData"> <html:text property="referenceIds" name="ioForm" size="110" value=""/> </TD> 
             <td colspan="2" width="17%" class="ColumnBackground "><div align="right">Ltr Date&nbsp;</div></td>
		         <TD width="100" class="TableData"> <html:text property="ltrDts" name="ioForm" size="20" value="" onblur= "javascript:isDate()"/></TD>
          </TR>
            <TR><td width="30%" class="ColumnBackground "><div align="right"><bean:message key="subject"/>	&nbsp;</div></td>
          	  	<TD width="100" class="TableData"> <html:text property="subjects" name="ioForm" size="110" value=""/> </TD>
		        <TD colspan="3" width="30%" class="ColumnBackground ">&nbsp;</TD> 
									
            </TR>
    </TABLE>
	
    <TABLE width="100%" id="dataTable"  border="0" cellpadding="0" cellspacing="1">
					<tr> 
						<td width="2%" class="ColumnBackground"><div align="center">&nbsp;</div></td>
						<td width="2%" align="center" valign="middle" class="ColumnBackground "><div align="center">Sr.<br>No.</div></td>
						<td width="10%" class="ColumnBackground"><div align="center">Inward Dt&nbsp;</div></td>
						 
						<td width="10%" class="ColumnBackground"><div align="center">Drawn on Bank&nbsp;</div></td>
						
						<td width="10%" class="ColumnBackground"><div align="center">Instrument No&nbsp;</div></td>
						<td width="10%" class="ColumnBackground"><div align="center">Instrument Date&nbsp;</div></td>
						<td width="10%" class="ColumnBackground"><div align="center">Instrument Amount&nbsp;</div></td>
						<td width="10%" class="ColumnBackground"><div align="center">Section&nbsp;</div></td>
						<td width="15%"class="ColumnBackground"><div align="center"><bean:message key="assignedTo"/>&nbsp;</div></td>
						<td width="12%" class="ColumnBackground"><div align="center">Choose Trancate System&nbsp;</div></td>
					</tr>
				<%for (int i=1;i<=1;i++){%>			
				<TR>
					<TD width="2%"><INPUT type="checkbox" name="chk" value="ON"/></TD>
					<TD width="2%"> <label  id="index"><%=i%> </label> </TD>
					<TD width="10%"><bean:write property="inwardDts" name="ioForm"/>&nbsp;</TD>
					<TD width="10%"> <html:text property="drawnonBank" name="ioForm" size="30" value=""/> </TD>
					<TD width="10%"> <html:text property="sourceIds" name="ioForm" size="15" maxlength="6" value="" onkeypress="return numbersOnly(this, event)" onkeyup="isValidNumber(this)"/> </TD>
					<TD width="10%"> <html:text property="instrumentDts" name="ioForm" size="15" maxlength="10" value=""/> </TD>
					<TD width="10%"> <html:text property="instrumentAmt" name="ioForm" size="15" value="" onblur="calSum('dataTable')"/> </TD>
					<TD width="10%"> <html:select property="section" name="ioForm">
						  <html:option value="">Select</html:option> 
						  <html:option value="GF">GF</html:option>
						  <html:option value="ASF">ASF</html:option>
						   <html:option value="CLAIM">CLAIM</html:option>
						   <html:option value="OTHERS">OTHERS</html:option>
						   <html:option value="RTI">RTI</html:option>
						  </html:select>
					</TD>
					<TD width="15%"> 
						<html:select property="assignedTo" name="ioForm">
							<html:option value="">Select</html:option>
							<html:options property="getUserNames" name="ioForm"/>			
						</html:select>
					</TD>
					<TD width="12%"> <html:select property="txnType" name="ioForm">
						  <html:option value="">Select</html:option>
						  <html:option value="CTS">CTS</html:option>
						  <html:option value="NONCTS">NONCTS</html:option>
					  </html:select>
					</TD>
				</TR>
			 <%}%>
			 
		
	</TABLE> 
    
	<TABLE width="100%">
		<TR>
	<!--	<TD> 
			<TABLE width="100%">
				<TR> -->
					<TD style="width:46%;" class="ColumnBackground" colspan=""><div align="right">Total Instrument Amount</DIV></TD>
					<TD  class="TableData" id="instrumentTotal"><div align="left"></DIV></TD>
			<!--	</TR>
			</TABLE> 
		</TD> -->
		</TR>
	</TABLE>
	<TABLE width="100%">
  <TR>
	<TD colspan="20"> 
		<TABLE width="100%">
		<TR><TD colspan="18" align="center">
		
  
	
    <INPUT type="button" style="width: 80px;height:25px;text-align:center;font-size:9px;" value="SAVE" onclick="save('dataTable')" class="ColumnBackground" /> 
	<INPUT type="button" style="width: 80px;height:25px;text-align:center;font-size:9px;" value="Add Row" onclick="addRow('dataTable')" class="ColumnBackground" />
	<INPUT type="button" style="width: 80px;height:25px;text-align:center;font-size:9px;" value="Delete Row" onclick="deleteRow('dataTable')" class="ColumnBackground" /> 
	

	</TD>
	</TR></TABLE></TD></TR>
       <!--  <TD colspan="2" align="right">
         <INPUT type="button" value="SAVE" onclick="save('dataTable')" /> 
         </TD> -->
       
  </tr>
  
        </form>
</html:form>     
</BODY>
</HTML>