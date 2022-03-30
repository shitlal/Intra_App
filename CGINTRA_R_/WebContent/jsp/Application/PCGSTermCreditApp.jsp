<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic" %>
<%@ page import="org.apache.struts.action.DynaActionForm"%>
<%@ page import="com.cgtsi.util.SessionConstants"%>
<%@ page import="java.util.ArrayList"%>
<% session.setAttribute("CurrentPage","pcgsTcMli.do?method=getPCGSMliInfo");%>

  <script language="javascript" type="text/javascript"> 	 
  	var req; 
      
  	/* 	 
  	* Get the second options by calling a Struts action 	 
  	*/	 
        
  	function retrieveSecondOptions(){ 
        alert ("inside retrieveSecondOptions() method");
  	//firstBox = document.getElementById('firstBox'); 
        state = document.getElementById('state'); 
  	//Nothing selected 	 
  	if(state.selectedIndex==0){ 	 
  	return; 	 
        } 	 
  
	selectedOption = state.options[state.selectedIndex].value; 	 
  	//get the (form based) params to push up as part of the get request 	 
        //afterTcMli.do?method=getDistricts
  	url="afterTcMli.do?method=getDistricts&state="+state.value; 
      //  url="afterTcMli.do?method=getDistricts"+selectedOption; 	 
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
 	req.send(state); 	 
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
	document.getElementById('district').options.length = 0; 	 
  	if (req.readyState == 4) { // Complete 	 
  	if (req.status == 200) { // OK response 	 
  	textToSplit = req.responseText 	 
       // alert ("the data: "+textToSplit);
                            if(textToSplit == '803')
                              { 	 
                                alert("No select option available on the server") 	 
                               } 	 //if close

	var returnElements=textToSplit.split("||") 	   
	for ( var i=0; i<returnElements.length; i++ ){ 	 
               valueLabelPair = returnElements[i].split(";") 	 
               document.getElementById('district').options[i] = new Option(valueLabelPair[0], valueLabelPair[1]); 	 
       	} 	 //for close
  	} //ok close	 
            else { 	 
        	         alert("Bad response by the server"); 	 
        	       } 
  } //complet close          	 
  
	} 	   
  
  
  function retrieveCityNames(myfiled){ 
  alert("My Field:"+myfiled);
  alert ("inside retrieveCityNames() method");  	
  city = document.getElementById('city');
  // alert("city:"+city.value);
  	//Nothing selected 	 
  	if(city.selectedIndex==0)
    { 	 
  	   return; 	 
     } 	
   //  selectedOption = city.options[city.selectedIndex].value; 	 
  	//get the (form based) params to push up as part of the get request 	 
        //afterTcMli.do?method=getDistricts
  	url="afterTcMli.do?method=getCityNames&city="+myfiled; 
  if (window.XMLHttpRequest){ // Non-IE browsers 	 
  	req = new XMLHttpRequest(); 	 
  	//A call-back function is define so the browser knows which function to call after the server gives a reponse back 	 
  	req.onreadystatechange = populateCityBox; 	 
        try { 	 
  	req.open("GET", url, true); //was get 	 
  	} catch (e) { 	 
  	alert("Cannot connect to server"); 	 
  	} 	 
 	req.send(city); 	 
 	} else if (window.ActiveXObject) { // IE 	 
 	req = new ActiveXObject("Microsoft.XMLHTTP"); 	 
 	if (req) { 	 
	req.onreadystatechange = populateCityBox; 	 
	req.open("GET", url, true); 		
        req.send(); 	 
	} 	 
 	} 	   
	} 
  
  function populateCityBox(){ 	  
	document.getElementById('city').value.length=0; 	 
  	if (req.readyState == 4) { // Complete 	 
  	if (req.status == 200) { // OK response 	 
  	textToSplit = req.responseText 	 
        alert ("the data: "+textToSplit);
                            if(textToSplit == '803')
                              { 	 
                                alert("No select option available on the server") 	 
                               } 	 //if close

	var returnElements=textToSplit.split("||"); 	   
	for ( var i=0; i<returnElements.length; i++ ){ 	 
               valueLabelPair = returnElements[i];	 
               //alert ("the values is :--->"+valueLabelPair[0]);
               //document.getElementById('city').value = new Option(valueLabelPair[0], valueLabelPair[1]); 	 
               document.getElementById('city').value = valueLabelPair;
              //  document.getElementById('city').options[i] = new Option(i++, valueLabelPair);
       	} 	 //for closecity
  	} //ok close	 
            else { 	 
        	         alert("Bad response by the server"); 	 
        	       } 
  } //complet close          	 
  
	} 
  
	</script>	



<%String focusField ="";%>
<% if(session.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE)!=null)
{
	System.out.println(session.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE));
	if(request.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG)!=null && request.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("15") && session.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE).equals("TC"))
	{
	session.setAttribute("CurrentPage","tcMli.do?method=getTCMliInfo");
	focusField="district";
	}
	else if(request.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG)!=null && request.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("16") && session.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE).equals("TC"))
	{
	session.setAttribute("CurrentPage","tcMli.do?method=getTCMliInfo");
	focusField="industrySector";
	}
	else if(session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG)!=null && session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("17"))
	{
		System.out.println(request.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG));
		session.setAttribute("CurrentPage","afterTcMli.do?method=getBorrowerDetails");
		focusField="guarantorsName1";

	}
	else if(session.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE).equals("TC"))
	{
	session.setAttribute("CurrentPage","tcMli.do?method=getTCMliInfo");
	focusField="mliRefNo";
	}
}
else if(session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("3") /*|| session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("11")*/)
{
	session.setAttribute("CurrentPage","afterModifyApp.do?method=showCgpanList");
	focusField="mliRefNo";

}else if(session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("14"))
{
	session.setAttribute("CurrentPage","afterSsiRefPage.do?method=afterSsiRefPage");
	focusField="mliRefNo";
}
else if(session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("15"))
{
	session.setAttribute("CurrentPage","afterSsiRefPage.do?method=afterSsiRefPage");
	focusField="mliRefNo";
}
if(focusField.equals(""))
{
	focusField=null;
}
org.apache.struts.action.ActionErrors errors = (org.apache.struts.action.ActionErrors)request.getAttribute(org.apache.struts.Globals.ERROR_KEY);
if (errors!=null && !errors.isEmpty())
{
	focusField="test";
}

;%>

    
<%@ include file="/jsp/SetMenuInfo.jsp" %>
<!-- <body onLoad="enableNone(),setConstEnabled(),calProjectOutlay(),enableGender(),enableHandiCrafts()"> -->
<logic:equal property="bankId" value="0000" name="appForm">
<%focusField = "mliRefNo";%>
</logic:equal>
<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form action="addPCGSTermCreditApp.do?method=submitPCGSApp" method="POST" focus="<%=focusField%>">
		<TR> 
			<TD width="20" align="right" valign="bottom"><IMG src="images/TableLeftTop.gif" width="20" height="31"></TD>
			<TD background="images/TableBackground1.gif"></TD>
			<TD width="20" align="left" valign="bottom"><IMG src="images/TableRightTop.gif" width="23" height="31"></TD>
		</TR>
		<TR>
			<TD width="20" background="images/TableVerticalLeftBG.gif">&nbsp;</TD>
			<TD>
				<DIV align="right">			
					<A HREF="javascript:submitForm('termCreditHelp.do?method=termCreditHelp')">
					HELP</A>
				</DIV>

				<TABLE width="100%" border="0" align="left" cellpadding="0" cellspacing="0">
					<TR>
						<TD>
							<table width="100%" border="0" cellspacing="1" cellpadding="0">
                <tr>
                <TD  align="center" colspan="4"><font size="4"><bold><u>
                PCGS Application form for Term Loan 
                  </u> </bold></font>
                </td>
              </tr>						
              <tr>
                <TD  align="left" colspan="4"><font size="2"><bold>
                    Fields marked as </font><font color="#FF0000" size="2">*</font><font size="2"> are mandatory</bold></font>
                </td>
            </tr>
							<tr> 
							  <td colspan="8">
								<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
									<TR>
										<TD width="31%" class="Heading"><bean:message key="enterApplicationDetails" /></TD>
										<TD width="69%"><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
									</TR>
									<TR>
										<TD colspan="4" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
									</TR>
								</TABLE>
							  </td>
							</tr>
           	   <% String appCommonFlag1=(String)session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG);			%>							
						    
                 <TR align="left">
									<TD align="left" valign="top" class="ColumnBackground" width="3%"><font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="branchName"/></td>								
									<TD align="left" valign="top" class="tableData" width="3%">
									<%
									if((appCommonFlag1.equals("1"))||(appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")))
									{
									%>
									<bean:write name="appForm" property="mliBranchName"/>
                  
									<%	}
										else
										{ %><html:text property="mliBranchName" size="20" alt="branch Name" name="appForm" maxlength="100"/>
										<%}%>
									</TD>
                  	<TD align="left" valign="top" class="ColumnBackground" width="5%"><font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="branchCode"/></td>								
									<TD align="left" valign="top" class="tableData" width="5%">
									<%
										if((appCommonFlag1.equals("1"))||(appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")))
										{
									%>
										<bean:write name="appForm" property="mliBranchCode"/>
									<%	}
										else
										{ %>
										<html:text property="mliBranchCode" size="20" alt="Branch Code" name="appForm" maxlength="10"/>
										<%}%>
									</TD>
								 </TR>
                 <!--table starting --><TABLE width="100%" border="0" cellspacing="1" cellpadding="1">
								<TR>
									<TD colspan="7">
										<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
											<TR>
												<TD width="31%" class="Heading"><bean:message key="borrowerHeader"/></TD>
												<TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
											</TR>
											<TR>
												<TD colspan="6" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
											</TR>
										</TABLE>
                    <TR align="left">
												<TD align="left" valign="top" class="ColumnBackground" width="12%"><font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="unitName"/>
												</TD>												
												<TD align="left" valign="top" class="TableData" colspan="2">

												<%	if((appCommonFlag1.equals("0"))||(appCommonFlag1.equals("1"))||(appCommonFlag1.equals("2")) ||(appCommonFlag1.equals("3"))
												|| (appCommonFlag1.equals("4")) || (appCommonFlag1.equals("5")) || (appCommonFlag1.equals("6")) || (appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("14"))||(appCommonFlag1.equals("17"))||(appCommonFlag1.equals("18"))||(appCommonFlag1.equals("19")))
												{	
												%>
												<bean:write property="ssiType" name="appForm"/>
												<%}
												else{
													%>
												
													<html:select property="ssiType" name="appForm" >
														<html:option value="">Select</html:option>
														<html:option value="M/s">M/s</html:option>
														<html:option value="Shri">Shri</html:option>
														<html:option value="Smt">Smt</html:option>
														<html:option value="Ku">Ku</html:option>
													</html:select>&nbsp;&nbsp;&nbsp;&nbsp;
												<%}%>
													

													<%	if((appCommonFlag1.equals("0"))||(appCommonFlag1.equals("1"))||(appCommonFlag1.equals("2")) ||(appCommonFlag1.equals("3"))
													|| (appCommonFlag1.equals("4")) || (appCommonFlag1.equals("5")) || (appCommonFlag1.equals("6")) || (appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("14"))||(appCommonFlag1.equals("17"))||(appCommonFlag1.equals("18"))||(appCommonFlag1.equals("19")))

													{
													%>
													<bean:write name="appForm" property="ssiName"/>
													<%}
													else
													{ %>
													<html:text property="ssiName" size="30" alt="unitName" name="appForm" maxlength="100"/>	
													<%}%>
												 </td>
                       <TD align="left" valign="top" class="ColumnBackground" width="15%"><font color="#FF0000" size="2">*</font>&nbsp;<!-- <bean:message key="bankRefNo"/> --> Loan Account Number</td>								
							         <TD align="left" valign="top" class="ColumnBackground" width="10%" colspan="3">
                       <html:text property="mliRefNo" size="25" alt="Bank Reference No" name="appForm" maxlength="10"/></TD>
									</tr>
                     
                <TR align="left">
									<TD align="left" valign="top" class="ColumnBackground" colspan="3">
									<!--	<bean:message key="coveredByCGTSI"/> --><font color="#FF0000" size="2">*</font>&nbsp;Whether Borrower Covered by CGTMSE previously 
									</TD>
									<TD align="left" class="TableData" colspan="4">

									 <% if((appCommonFlag1.equals("0"))||(appCommonFlag1.equals("1"))||(appCommonFlag1.equals("2")) ||(appCommonFlag1.equals("3"))
											|| (appCommonFlag1.equals("4")) || (appCommonFlag1.equals("5")) || (appCommonFlag1.equals("6")) || (appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("14"))||(appCommonFlag1.equals("17"))||(appCommonFlag1.equals("18"))
									||(appCommonFlag1.equals("19")))
									 {
									%>
									<html:radio name="appForm" value="Y" property="previouslyCovered" disabled="true"></html:radio>
											
											<bean:message key="yes"/>&nbsp;&nbsp;
											
											<html:radio name="appForm" value="N" property="previouslyCovered"  disabled="true"></html:radio>
											
											<bean:message key="no"/>
									<%}
									else {
									%>
									
											<html:radio name="appForm" value="Y" property="previouslyCovered" onclick="enableNone()"></html:radio>
											
											<bean:message key="yes"/>&nbsp;&nbsp;
											
											<html:radio name="appForm" value="N" property="previouslyCovered"  onclick="enableNone()"></html:radio>
											
											<bean:message key="no"/>
										<%}%>
										
									</TD>								
								</TR>
                               
               <TR>
									<TD colspan="8">
										<TABLE width="100%" border="0" cellpadding="1" cellspacing="1">			
											 <TR align="left">
											  <TD align="left" valign="top" class="ColumnBackground" width="13%"><html:radio name="appForm" value="none" property="none" disabled="true"><bean:message key="none" /></html:radio>
												</TD>
										    <TD align="left" valign="top" class="ColumnBackground" width="17%"><html:radio name="appForm" value="cgpan" property="none" disabled="true">
												<bean:message key="cgpan" /></html:radio>																								
												</TD>			
                        <TD align="left" valign="top" class="ColumnBackground" width="42%">	
											  <script>
												 booleanVal=false;
											   </script>
 					              <input type="text" name="unitValue" maxlength="13" size="15" value="" alt="Value">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
												<a href="javascript:submitForm('afterPCGSMliPage.do?method=getBorrowerDetails')">View</a>
 					           </TD>
                  </TABLE> 
              </TD>											
						  </TR>
               <TR align="left">
									<TD align="left" valign="top" class="ColumnBackground" width="30%"><font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="constitution"/>
									</TD>
									<TD align="left" valign="top" class="TableData" colspan="2">
											<%	if((appCommonFlag1.equals("0"))||(appCommonFlag1.equals("1"))||(appCommonFlag1.equals("2")) ||(appCommonFlag1.equals("3"))
												|| (appCommonFlag1.equals("4")) || (appCommonFlag1.equals("5")) || (appCommonFlag1.equals("6")) || (appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("14"))||(appCommonFlag1.equals("17"))||(appCommonFlag1.equals("18"))||(appCommonFlag1.equals("19")))
												{	
											%>
		                        <html:select property="constitution" name="appForm" disabled="true">
														<html:option value="">Select</html:option>
														<html:option value="proprietary"><bean:message key="proprietary" /></html:option>
														<html:option value="partnership"><bean:message key="partnership" /></html:option>
														<html:option value="private"><bean:message key="private" /></html:option>
	                          <html:option value="public"><bean:message key="public" /></html:option>
                            <html:option value="HUF">HUF</html:option>
                            <html:option value="Trust">Trust</html:option>
                            <html:option value="Society/Co op">Society/Co op Society</html:option>
	                     <!-- <html:option value="Others"><bean:message key="others" /></html:option> -->
													</html:select>
	               <!--				<bean:write property="constitution" name="appForm"/>-->
										<%}
												else{
													%>
												
													<html:select property="constitution" name="appForm" onchange="setConstEnabled()">
														<html:option value="">Select</html:option>
														<html:option value="proprietary/ individual"><bean:message key="proprietary" />/ Individual</html:option>
														<html:option value="partnership/ Limited Liability Partnership"><bean:message key="partnership" />/ Limited Liability Partnership</html:option>
														<html:option value="private"><bean:message key="private" /></html:option>
                            <html:option value="public ltd."><bean:message key="public" /> Ltd.</html:option>
                            <html:option value="HUF">HUF</html:option>
                            <html:option value="Trust">Trust</html:option>
                            <html:option value="Society/Co op">Society/Co op Society</html:option>
	                     <!-- <html:option value="Others"><bean:message key="others" /></html:option> -->
													</html:select>&nbsp;&nbsp;&nbsp;&nbsp;		
												<%}%>
													</TD>
                          	<TD align="left" valign="top" class="ColumnBackground" width="15%"><font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="firmItpan"/>
												</td>
												<TD align="left" valign="top" class="TableData" width="15%" colspan="3" >
												<%
													if((appCommonFlag1.equals("0"))||(appCommonFlag1.equals("1"))||(appCommonFlag1.equals("2")) ||(appCommonFlag1.equals("3"))
													|| (appCommonFlag1.equals("4")) || (appCommonFlag1.equals("5")) || (appCommonFlag1.equals("6")) || (appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("14"))||(appCommonFlag1.equals("17"))||(appCommonFlag1.equals("18"))||(appCommonFlag1.equals("19")))

													{
												%>
													<bean:write name="appForm" property="ssiITPan"/>

												<%	}
													else
													{ %><html:text property="ssiITPan" size="20" alt="ITPAN" name="appForm" maxlength="10"/>
													<%}%>
												</td>
											 </TR>
                       <!--  add part here -->
                       <TR>
                       <TD align="left" valign="top" class="ColumnBackground" width="2%"><font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="unitAddress"/>
												</td>
												<TD align="left" valign="top" class="TableData" width="10%" colspan="2">
												<%
													if((appCommonFlag1.equals("0"))||(appCommonFlag1.equals("1"))||(appCommonFlag1.equals("2")) ||(appCommonFlag1.equals("3"))
													|| (appCommonFlag1.equals("4")) || (appCommonFlag1.equals("5")) || (appCommonFlag1.equals("6")) || (appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("14"))||(appCommonFlag1.equals("17"))||(appCommonFlag1.equals("18"))||(appCommonFlag1.equals("19")))

													{
												%>
													<bean:write name="appForm" property="address"/>

												<%	}
													else
													{ %>
													<html:textarea property="address" cols="30" alt="address" name="appForm" rows="3"/>	
													<%}%>
												</td>

                       <TD align="left" valign="top" class="ColumnBackground" width="25%"><font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="state"/>
											 </TD>	
                        	<TD align="left" valign="top" class="TableData" width="15%" colspan="3">
                          <%
													if((appCommonFlag1.equals("0"))||(appCommonFlag1.equals("1"))||(appCommonFlag1.equals("2")) ||(appCommonFlag1.equals("3"))
													|| (appCommonFlag1.equals("4")) || (appCommonFlag1.equals("5")) || (appCommonFlag1.equals("6")) || (appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("14"))||(appCommonFlag1.equals("17"))||(appCommonFlag1.equals("18"))||(appCommonFlag1.equals("19")))

													{
													%>
													<bean:write name="appForm" property="state"/>

													<%	}
													else
													{ %>
                           <%-- <html:select property="state" name="appForm" onchange="javascript:submitForm('afterPCGSMli.do?method=getDistricts')"> --%>
                           
                            <html:select property="state" name="appForm" onchange="javascript:retrieveSecondOptions()" styleId="state" styleClass="mandatory" > 
														<html:option value="">Select</html:option>
													 <html:options name="appForm" property="statesList"/>
              						  </html:select>	
                          
                          <%}%>
                          </TD>
                        </TR>
                        <TR align="left">
												<TD align="left" valign="top" class="ColumnBackground" width="15%"><font color="#FF0000" size="2">*</font><bean:message key="district"/>
												</TD>												
												
                        <TD align="left" valign="top" class="TableData" width="15%">
												<%
													if((appCommonFlag1.equals("0"))||(appCommonFlag1.equals("1"))||(appCommonFlag1.equals("2")) ||(appCommonFlag1.equals("3"))
													|| (appCommonFlag1.equals("4")) || (appCommonFlag1.equals("5")) || (appCommonFlag1.equals("6")) || (appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("14"))||(appCommonFlag1.equals("17"))||(appCommonFlag1.equals("18"))||(appCommonFlag1.equals("19")))

													{
													%>
													<bean:write name="appForm" property="district"/>

													<%	}
													else
													{ %>
                      
											
												 <html:select  property="district" name="appForm"  styleId="district" styleClass="mandatory">
														<html:option value="">Select</html:option>
													<html:options  name="appForm"  property="districtList"/>
													</html:select>
                        
                        </TD>
                        
												<TD align="left" valign="top" class="TableData">
												 </td>                         
												 <%}%>
												<TD align="left" valign="top" class="ColumnBackground" width="25%"><font color="#FF0000" size="2">*</font><bean:message key="city"/>
												</td>                        

												<TD align="left" valign="top" class="TableData" width="15%">
												<%
													if((appCommonFlag1.equals("0"))||(appCommonFlag1.equals("1"))||(appCommonFlag1.equals("2")) ||(appCommonFlag1.equals("3"))
													|| (appCommonFlag1.equals("4")) || (appCommonFlag1.equals("5")) || (appCommonFlag1.equals("6")) || (appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("14"))||(appCommonFlag1.equals("17"))||(appCommonFlag1.equals("18"))||(appCommonFlag1.equals("19")))

													{
													%>
													<bean:write name="appForm" property="city"/>

													<%	}
													else
													{ %>
													<html:text property="city" size="20" alt="city" styleId="city"  onchange="javascript:retrieveCityNames(this.value)" name="appForm" maxlength="100"/>	
													<%}%>
												</td>
                        <TD align="left" valign="top" class="ColumnBackground" width="30%"><font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="pinCode"/>
												</td>
												<TD align="left" valign="top" class="TableData" width="70%" colspan="2">
												<%
													if((appCommonFlag1.equals("0"))||(appCommonFlag1.equals("1"))||(appCommonFlag1.equals("2")) ||(appCommonFlag1.equals("3"))
													|| (appCommonFlag1.equals("4")) || (appCommonFlag1.equals("5")) || (appCommonFlag1.equals("6")) || (appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("14"))||(appCommonFlag1.equals("17"))||(appCommonFlag1.equals("18"))||(appCommonFlag1.equals("19")))

													{
													%>
													<bean:write name="appForm" property="pincode"/>

													<%	}
													else
													{ %>
													<html:text property="pincode" size="20" alt="pinCode" name="appForm" maxlength="6" onkeypress="return numbersOnly(this, event)" onkeyup="isValidNumber(this)"/>
													<%}%>
												</td>	
											</tr>
                     <TR align="left">
                   <TD align="left" valign="top" class="ColumnBackground">
								<!--	<bean:message key="socialCategory"/> --> <font color="#FF0000" size="2">*</font>&nbsp;MSME Category 
									</TD>
									<TD align="left" valign="top" class="TableData" width="20%" colspan="2">

									<% if((appCommonFlag1.equals("0"))||(appCommonFlag1.equals("1"))||(appCommonFlag1.equals("2")) ||(appCommonFlag1.equals("3"))
									|| (appCommonFlag1.equals("4")) || (appCommonFlag1.equals("5")) || (appCommonFlag1.equals("6")) || (appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("14"))||(appCommonFlag1.equals("17"))||(appCommonFlag1.equals("18"))||(appCommonFlag1.equals("19")))
											{										

									%>
									<bean:write property="mseCategory" name="appForm"/>
									<%}
									else {
									%>	
										<html:select property="mseCategory" name="appForm">
											<html:option value="">Select</html:option>
											<html:options name="appForm" property="socialCategoryList"/>
										</html:select>
   						<%}%>							
									</TD> 
                  <TD align="left" valign="top" class="ColumnBackground" width="25%"><font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="industrySector"/>
						    	</td>
                  <TD align="left" valign="top" class="TableData">
												<%
													if((appCommonFlag1.equals("0"))||(appCommonFlag1.equals("1"))||(appCommonFlag1.equals("2")) ||(appCommonFlag1.equals("3"))
													|| (appCommonFlag1.equals("4")) || (appCommonFlag1.equals("5")) || (appCommonFlag1.equals("6")) || (appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("14"))||(appCommonFlag1.equals("17"))||(appCommonFlag1.equals("18"))||(appCommonFlag1.equals("19")))
                 				{
													%>
													<bean:write name="appForm" property="industrySector"/>

													<%	}
													else
													{ %>										
													<html:select property="industrySector" name="appForm" >
														<html:option value="">Select</html:option>
														<html:options name="appForm" property="industrySectorList"/>
													</html:select>
													<%}%>
												 </td>
                         <TD align="left" valign="top" class="ColumnBackground" width="30%">&nbsp;<font color="#FF0000" size="2">*</font><bean:message key="activitytype"/>
												</TD>												
												<TD align="left" valign="top" class="TableData">
												<%
													if((appCommonFlag1.equals("0"))||(appCommonFlag1.equals("1"))||(appCommonFlag1.equals("2")) ||(appCommonFlag1.equals("3"))
													|| (appCommonFlag1.equals("4")) || (appCommonFlag1.equals("5")) || (appCommonFlag1.equals("6")) || (appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("14"))||(appCommonFlag1.equals("17"))||(appCommonFlag1.equals("18"))||(appCommonFlag1.equals("19")))
                  				{
												%>
													<bean:write name="appForm" property="activityType"/>

												<%	}
													else
													{ %>
													<html:text property="activityType" size="20" alt="Activity Type" name="appForm" maxlength="50"/>
													<%}%>
												 </td>

                 </TR>
                 <TR align="left">
												<TD align="left" valign="top" class="ColumnBackground" width="20%" colspan="2"><font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="turnover"/>
												</TD>												
												<TD align="left" valign="top" class="TableData">
												<%
													if((appCommonFlag1.equals("0"))||(appCommonFlag1.equals("1"))||(appCommonFlag1.equals("2")) ||(appCommonFlag1.equals("3"))
													|| (appCommonFlag1.equals("4")) || (appCommonFlag1.equals("5")) || (appCommonFlag1.equals("6")) || (appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("14"))||(appCommonFlag1.equals("17"))||(appCommonFlag1.equals("18"))||(appCommonFlag1.equals("19")))
													{
												%>
													<bean:write name="appForm" property="projectedSalesTurnover"/>

												<%	}
													else
													{ %>
													<html:text property="projectedSalesTurnover" size="20" alt="turnover" name="appForm" maxlength="16" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)"/>
													<%}%>
													<bean:message key="inRs"/>
												 </td>
												<TD align="left" valign="top" class="ColumnBackground" width="25%"><font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="exports"/>
												</td>
												<TD align="left" valign="top" class="TableData" width="15%">
												<%
													if((appCommonFlag1.equals("0"))||(appCommonFlag1.equals("1"))||(appCommonFlag1.equals("2")) ||(appCommonFlag1.equals("3"))
													|| (appCommonFlag1.equals("4")) || (appCommonFlag1.equals("5")) || (appCommonFlag1.equals("6")) || (appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("14"))||(appCommonFlag1.equals("17"))||(appCommonFlag1.equals("18"))||(appCommonFlag1.equals("19")))

													{
												%>
													<bean:write name="appForm" property="projectedExports"/>

												<%	}
													else
													{ %>
													<html:text property="projectedExports" size="10" alt="exports" name="appForm" maxlength="16" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)"/>
													<%}%>
													<bean:message key="inRs"/>
												</td>
                        <TD align="left" valign="top" class="ColumnBackground" width="25%"><font color="#FF0000" size="2">*</font>&nbsp;&nbsp;<bean:message key="noOfEmployees"/>
												</td>
												<TD align="left" valign="top" class="TableData" width="15%">
												<%
													if((appCommonFlag1.equals("0"))||(appCommonFlag1.equals("1"))||(appCommonFlag1.equals("2")) ||(appCommonFlag1.equals("3"))
													|| (appCommonFlag1.equals("4")) || (appCommonFlag1.equals("5")) || (appCommonFlag1.equals("6")) || (appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("14"))||(appCommonFlag1.equals("17"))||(appCommonFlag1.equals("18"))||(appCommonFlag1.equals("19")))
                  			{
												%>
													<bean:write name="appForm" property="employeeNos"/>

												<%	}
													else
													{ %><html:text property="employeeNos" size="10" alt="No Of employees" name="appForm" onkeypress="return numbersOnly(this, event)" onkeyup="isValidNumber(this)"/>	
													<%}%>
												</td>

											</tr>
                      <tr> 
									   <td colspan="8">
										 <table width="100%" border="0" cellspacing="0" cellpadding="0">
											 <tr> 
												<TD width="31%" class="Heading"><bean:message key="projectHeader" /></TD>
												<TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>                 
												 <td>&nbsp;</td>
												 <td>&nbsp;</td>
											 </tr>											
											 <TR>
												<TD colspan="4" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
											</TR>                   
										</table>
									</td>
								</tr>
              <TR>
               <TD align="left" valign="top" class="ColumnBackground" colspan="4" ><font color="#FF0000" size="2">*</font>&nbsp;Whether Unit assisted is a new unit:</TD>
              <TD align="left" valign="top" class="ColumnBackground" colspan="4" >
              <input type="radio" name="unitAssisted" value="Y">Yes<input type="radio" name="unitAssisted" value="N" checked="checked">No</TD>
              </TR>

		      	<TR>
						<TD align="left" valign="top" class="ColumnBackground" colspan="4" ><font color="#FF0000" size="2">*</font>&nbsp;Whether the Unit Assisted is Women Operated and/or Women Owned:</TD>
						<TD align="left" valign="top" class="ColumnBackground" colspan="4" >
             <input type="radio" name="womenOperated" value="Y">Yes<input type="radio" name="womenOperated" value="N" checked="checked">No</TD>
            </TR>	
            <TR>
                  <TD align="left" valign="top" class="ColumnBackground" colspan="2" ><font color="#FF0000" size="2">*</font>&nbsp;Whether the credit is sanctioned under Joint Finance scheme:</TD>
                  <TD align="left" valign="top" class="ColumnBackground" colspan="7" >             
	                <html:radio name="appForm" value="Y" property="jointFinance">Yes</html:radio>
                  <html:radio name="appForm" value="N" property="jointFinance">No</html:radio>
                  </TD>      
				     	</TR>
             <!-- Handicrafts -->
             <tr align="left"> 
									<td colspan="8" align="left" class="ColumnBackground"><b><font color="#FF0000" size="4">Handicrafts</font></b></td>
						 </tr>
                  <TR>
                   <TD align="left" valign="top" class="ColumnBackground" colspan="4" >
                   <font color="#FF0000" size="2">*</font>&nbsp;Whether the credit is sanctioned under Artisan Credit Card (ACC) scheme for Handicraft Artisans operated by DC(Handicrafts), Govt.of India:</TD>
                   <TD align="left" valign="top" class="ColumnBackground" colspan="4" >
                   <input type="radio" name="handiCrafts" value="Y" onclick="javascript:enableHandiCrafts()">Yes<input type="radio" name="handiCrafts" value="N" checked="checked" onclick="javascript:enableHandiCrafts()">N
									 </TD>
           	     </TR>
                 <TR>
                  <TD align="left" valign="top" class="ColumnBackground" colspan="4" ><font color="#FF0000" size="2">*</font>&nbsp;Whether GF/ASF is re-imbursable from O/o DC(Handicrafts) Govt.of India:</TD>
                  <TD align="left" valign="top" class="ColumnBackground" colspan="4" >
                  <input type="radio" name="dcHandicrafts" value="Y">Yes<input type="radio" name="dcHandicrafts" value="N" checked="checked">N
									</TD>
                	</TR>

              <tr align="left"> 
					     <td colspan="8" align="left" class="ColumnBackground"><font color="#FF0000" size="1">Details of Identity Card Issued by DC(Handicraft),GOI. (Compulsory when reimbursement is required ) </font></td>
					    </tr>
               <tr>
                    <TD align="left" valign="top" class="ColumnBackground" colspan="2"><font color="#FF0000" size="2">*</font>&nbsp;I Card Number:</td>
                    <TD align="left" valign="top" class="ColumnBackground" colspan="2">&nbsp;
                    <html:text property="iCardNo" size="25" alt="iCard No" name="appForm" maxlength="11" />
                    </td>
                    <TD align="left" valign="top" class="ColumnBackground" colspan="2">&nbsp;</td>
                    <TD align="left" valign="top" class="ColumnBackground" colspan="2">&nbsp;
                    </td> 
              </tr>
          	   <tr> 
									   <td colspan="8">
										 <table width="100%" border="0" cellspacing="0" cellpadding="0">
											 <tr> 
												<TD width="31%" class="Heading"><bean:message key="promoterHeader" /></TD>
												<TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>                 
												 <td>&nbsp;</td>
												 <td>&nbsp;</td>
											 </tr>											
											 <TR>
												<TD colspan="4" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
											</TR>                   
										</table>
									</td>
								</tr>
								<TR align="left">
									<TD align="left" valign="top" class="ColumnBackground" colspan="7">
										<bean:message key="chiefInfo"/>
									</TD>
								</TR>

							  <TR align="left">
								  <TD align="left" valign="top" class="ColumnBackground" width="15%">
								  </TD>												 
								  <TD align="left" valign="top" class="ColumnBackground" width="10%">
										<font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="title"/>
								   </TD>
								   <TD align="left" valign="top" class="ColumnBackground" width="25%">
										<font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="firstName"/>
								   </TD>
									<TD align="left" valign="top" class="ColumnBackground" width="25%">
										<bean:message key="middleName"/>	
								   </TD>
									<TD align="left" valign="top" class="ColumnBackground" width="25%" colspan="3">
										<font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="lastName"/>
								   </TD>
								</TR>

								<TR align="left">
								  <TD align="left" valign="top" class="ColumnBackground"  width="15%"><bean:message key="name"/>
								  </TD>		
								  <TD align="left" valign="top" class="TableData">

								  <%if((appCommonFlag1.equals("0"))||(appCommonFlag1.equals("1"))||(appCommonFlag1.equals("2")) ||(appCommonFlag1.equals("3"))
									|| (appCommonFlag1.equals("4")) || (appCommonFlag1.equals("5")) || (appCommonFlag1.equals("6")) || (appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("14"))||(appCommonFlag1.equals("17"))||(appCommonFlag1.equals("18"))||(appCommonFlag1.equals("19")))
        					{										
          				%>
									<bean:write property="cpTitle" name="appForm"/>
									<%}
									else {
									%>
									<html:select property="cpTitle" name="appForm" onchange="enableGender()">
										<html:option value="">Select</html:option>
										<html:option value="Mr.">Mr</html:option>
										<html:option value="Smt">Smt</html:option>
										<html:option value="Ku">Ku</html:option>
									</html:select>
									<%}%>
									
								  </TD>
								  <TD align="left" valign="top" class="TableData">

								  <% if((appCommonFlag1.equals("0"))||(appCommonFlag1.equals("1"))||(appCommonFlag1.equals("2")) ||(appCommonFlag1.equals("3"))
									|| (appCommonFlag1.equals("4")) || (appCommonFlag1.equals("5")) || (appCommonFlag1.equals("6")) || (appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("14"))||(appCommonFlag1.equals("17"))||(appCommonFlag1.equals("18"))||(appCommonFlag1.equals("19")))
											{										

									%>
									<bean:write property="cpFirstName" name="appForm"/>
									<%}
									else {
									%>
								  
									<html:text property="cpFirstName" size="20" alt="firstName" name="appForm" maxlength="20"/>
									<%}%>
									
								  </TD>
								   <TD align="left" valign="top" class="TableData">

								   <%if((appCommonFlag1.equals("0"))||(appCommonFlag1.equals("1"))||(appCommonFlag1.equals("2")) ||(appCommonFlag1.equals("3"))
									|| (appCommonFlag1.equals("4")) || (appCommonFlag1.equals("5")) || (appCommonFlag1.equals("6")) || (appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("14"))||(appCommonFlag1.equals("17"))||(appCommonFlag1.equals("18"))||(appCommonFlag1.equals("19")))
				    		{										
				   	 	%>
									<bean:write property="cpMiddleName" name="appForm"/>
									<%}
									else {
									%>								  
									<html:text property="cpMiddleName" size="20" alt="middleName" name="appForm" maxlength="20"/>

									<%}%>
									
								  </TD>
								   <TD align="left" valign="top" class="TableData" colspan="3">

								   <%if((appCommonFlag1.equals("0"))||(appCommonFlag1.equals("1"))||(appCommonFlag1.equals("2")) ||(appCommonFlag1.equals("3"))
									|| (appCommonFlag1.equals("4")) || (appCommonFlag1.equals("5")) || (appCommonFlag1.equals("6")) || (appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("14"))||(appCommonFlag1.equals("17"))||(appCommonFlag1.equals("18"))||(appCommonFlag1.equals("19")))
											{										
            			%>
									<bean:write property="cpLastName" name="appForm"/>
									<%}
									else {
									%>	
									<html:text property="cpLastName" size="20" alt="lastName" name="appForm" maxlength="20"/>
        			<%}%>									
								  </TD>
								</TR>

              <TR align="left">
									<TD align="left" valign="top" class="ColumnBackground" colspan="2"><font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="gender"/>
									</TD>
									<TD align="left" valign="top" class="TableData" width="20%">

									<% if((appCommonFlag1.equals("0"))||(appCommonFlag1.equals("1"))||(appCommonFlag1.equals("2")) ||(appCommonFlag1.equals("3"))
									|| (appCommonFlag1.equals("4")) || (appCommonFlag1.equals("5")) || (appCommonFlag1.equals("6")) || (appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("14"))||(appCommonFlag1.equals("17"))||(appCommonFlag1.equals("18"))||(appCommonFlag1.equals("19")))
											{										

									%>
							   <bean:write property="cpGender" name="appForm"/>
           			<%}
									else {
									%>	
										<html:radio name="appForm" value="M" property="cpGender" >
										</html:radio>
										<bean:message key="male" />&nbsp;&nbsp;&nbsp;
          					<html:radio name="appForm" value="F" property="cpGender" ></html:radio>
										<bean:message key="female" />
									<%}%>
									</TD>
									<TD align="left" valign="top" class="ColumnBackground">
										<font color="#FF0000" size="2">*</font>&nbsp;&nbsp;<bean:message key="chiefItpan" />
									</TD>
									<TD align="left" valign="top" class="TableData" colspan="3" width="20%">

									<% if((appCommonFlag1.equals("0"))||(appCommonFlag1.equals("1"))||(appCommonFlag1.equals("2")) ||(appCommonFlag1.equals("3"))
									|| (appCommonFlag1.equals("4")) || (appCommonFlag1.equals("5")) || (appCommonFlag1.equals("6")) || (appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("14"))||(appCommonFlag1.equals("17"))||(appCommonFlag1.equals("18"))||(appCommonFlag1.equals("19")))
											{										
              		%>
									<bean:write property="cpITPAN" name="appForm"/>
									<%}
									else {
									%>	
										<html:text property="cpITPAN" size="15" alt="chiefItpan" name="appForm" maxlength="10"/>

									<%}%>
									</TD>													
								</TR>
                 <TR>
                  <TD align="left" valign="top" class="ColumnBackground" colspan="3" >Whether the Chief Promoter is Physically Handicapped:</TD>
                  <TD align="left" valign="top" class="ColumnBackground" colspan="7" ><html:radio name="appForm" value="Y" property="physicallyHandicapped" >Yes
									</html:radio><html:radio name="appForm" value="N" property="physicallyHandicapped">No</html:radio>
																								
												</TD></TR>

                <TR align="left">
									<TD align="left" valign="top" class="ColumnBackground" colspan="2">
										<bean:message key="dob" />					
									</TD>
									<TD align="left" valign="top" class="TableData" >

									<% if((appCommonFlag1.equals("0"))||(appCommonFlag1.equals("1"))||(appCommonFlag1.equals("2")) ||(appCommonFlag1.equals("3"))
									|| (appCommonFlag1.equals("4")) || (appCommonFlag1.equals("5")) || (appCommonFlag1.equals("6")) || (appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("14"))||(appCommonFlag1.equals("17"))||(appCommonFlag1.equals("18"))||(appCommonFlag1.equals("19")))
											{										

									%>
									<bean:write property="cpDOB" name="appForm"/>
									<%}
									else {
									%>	
										<html:text property="cpDOB" size="20" alt="dob" name="appForm" maxlength="10"/>
										<IMG src="images/CalendarIcon.gif" width="20" onClick="showCalendar('appForm.cpDOB')" align="center">
									<%}%>
									<TD align="left" valign="top" class="ColumnBackground">
									<bean:message key="socialCategory"/>
									</TD>
									<TD align="left" valign="top" class="TableData" width="20%" colspan="3">

									<% if((appCommonFlag1.equals("0"))||(appCommonFlag1.equals("1"))||(appCommonFlag1.equals("2")) ||(appCommonFlag1.equals("3"))
									|| (appCommonFlag1.equals("4")) || (appCommonFlag1.equals("5")) || (appCommonFlag1.equals("6")) || (appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("14"))||(appCommonFlag1.equals("17"))||(appCommonFlag1.equals("18"))||(appCommonFlag1.equals("19")))
											{										
              		%>
									<bean:write property="socialCategory" name="appForm"/>
									<%}
									else {
									%>	
										<html:select property="socialCategory" name="appForm">
											<html:option value="">Select</html:option>
											<html:options name="appForm" property="socialCategoryList"/>
											</html:select>

									<%}%>									
										
									</TD>
				       </TD>
								</TR>

                <TR align="left">
									<TD align="left" valign="top" class="ColumnBackground" colspan="7"><bean:message key="otherPromoters" />	
									</TD>
								</TR>
								 <tr> 
									 <td class="ColumnBackground" colspan="8">
										 <table border="0" cellpadding="1" cellspacing="0" width="100%">
											<tr>
												<td class="ColumnBackground" width="10%"><b>
													<span style="font-size: 9pt">1. <bean:message key="promoterName" />	</span></b>
												</td>
												<td width="10%" class="TableData">
												
													<b><span style="font-size: 9pt" >	

													<% if((appCommonFlag1.equals("0"))||(appCommonFlag1.equals("1"))||(appCommonFlag1.equals("2")) ||(appCommonFlag1.equals("3"))
													|| (appCommonFlag1.equals("4")) || (appCommonFlag1.equals("5")) || (appCommonFlag1.equals("6")) || (appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")) 
                          || (appCommonFlag1.equals("14"))||(appCommonFlag1.equals("17"))||(appCommonFlag1.equals("18"))||(appCommonFlag1.equals("19")))
															{										
          							%>
													<bean:write property="firstName" name="appForm"/>
													<%}
													else {
													%>							
									
													<html:text property="firstName" size="20" alt="firstName" name="appForm" maxlength="50"/>
													<%}%>
													</span></b>
													
												</td>
												<td width="10%" class="ColumnBackground" >
													<b><span style="font-size: 9pt"> <bean:message key="promoterItpan" /></span></b>
												</td>
												<td width="10%" class="TableData">												
													<b><span style="font-size: 9pt"> 

													<% if((appCommonFlag1.equals("0"))||(appCommonFlag1.equals("1"))||(appCommonFlag1.equals("2")) ||(appCommonFlag1.equals("3"))
													|| (appCommonFlag1.equals("4")) || (appCommonFlag1.equals("5")) || (appCommonFlag1.equals("6")) || (appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("14"))||(appCommonFlag1.equals("17"))||(appCommonFlag1.equals("18"))||(appCommonFlag1.equals("19")))
													{										

													%>
													<bean:write property="firstItpan" name="appForm"/>
													<%}
													else {
													%>		
													<html:text property="firstItpan" size="20" alt="firstItpan" name="appForm" maxlength="10"/>
													<%}%>
													</span></b>
													
												</td>
												<td width="10%" class="ColumnBackground" >
													<b><span style="font-size: 9pt"><bean:message key="promoterDob" /></span></b>
												</td>
												<td width="10%" class="TableData">
												
													<b><span style="font-size: 9pt"> 
													
													<% if((appCommonFlag1.equals("0"))||(appCommonFlag1.equals("1"))||(appCommonFlag1.equals("2")) ||(appCommonFlag1.equals("3"))
													|| (appCommonFlag1.equals("4")) || (appCommonFlag1.equals("5")) || (appCommonFlag1.equals("6")) || (appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("14"))||(appCommonFlag1.equals("17"))||(appCommonFlag1.equals("18"))||(appCommonFlag1.equals("19")))
													{										

													%>
													<bean:write property="firstDOB" name="appForm"/>
													<%}
													else {
													%>												
													 <html:text property="firstDOB" size="10" alt="firstDOB" name="appForm" maxlength="10"/>
													<IMG src="images/CalendarIcon.gif" width="20" onClick="showCalendar('appForm.firstDOB')" align="center">
													<%}%>
													</span></b>
													
												</td>
											</tr>
											 <tr>
												<td class="ColumnBackground" width="17%"><b>
													<span style="font-size: 9pt">2. <bean:message key="promoterName" />	</span></b>
												</td>
												<td width="10%" class="TableData">
												
													<b><span style="font-size: 9pt"> 

													<% if((appCommonFlag1.equals("0"))||(appCommonFlag1.equals("1"))||(appCommonFlag1.equals("2")) ||(appCommonFlag1.equals("3"))
													|| (appCommonFlag1.equals("4")) || (appCommonFlag1.equals("5")) || (appCommonFlag1.equals("6")) || (appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("14"))||(appCommonFlag1.equals("17"))||(appCommonFlag1.equals("18"))||(appCommonFlag1.equals("19")))
													{										

													%>
													<bean:write property="secondName" name="appForm"/>
													<%}
													else {
													%>		
													<html:text property="secondName" size="20" alt="secondName" name="appForm" maxlength="50"/>
													<%}%>
													</span></b>
													
												</td>
												<td width="10%" class="ColumnBackground" >
													<b><span style="font-size: 9pt"> <bean:message key="promoterItpan" /></span></b>
												</td>
												<td width="10%" class="TableData">
												
													<b><span style="font-size: 9pt"> 

													<% if((appCommonFlag1.equals("0"))||(appCommonFlag1.equals("1"))||(appCommonFlag1.equals("2")) ||(appCommonFlag1.equals("3"))
													|| (appCommonFlag1.equals("4")) || (appCommonFlag1.equals("5")) || (appCommonFlag1.equals("6")) || (appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("14"))||(appCommonFlag1.equals("17"))||(appCommonFlag1.equals("18"))||(appCommonFlag1.equals("19")))
													{										

													%>
													<bean:write property="secondItpan" name="appForm"/>
													<%}
													else {
													%>		
													<html:text property="secondItpan" size="20" alt="secondItpan" name="appForm" maxlength="10"/>
													<%}%>
													</span></b>
													
												</td>
												<td width="10%" class="ColumnBackground" >
													<b><span style="font-size: 9pt"><bean:message key="promoterDob" /></span></b>
												</td>
												<td width="10%" class="TableData">
												
													<b><span style="font-size: 9pt"> 

													<% if((appCommonFlag1.equals("0"))||(appCommonFlag1.equals("1"))||(appCommonFlag1.equals("2")) ||(appCommonFlag1.equals("3"))
													|| (appCommonFlag1.equals("4")) || (appCommonFlag1.equals("5")) || (appCommonFlag1.equals("6")) || (appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("14"))||(appCommonFlag1.equals("17"))||(appCommonFlag1.equals("18"))||(appCommonFlag1.equals("19")))
													{										

													%>
													<bean:write property="secondDOB" name="appForm"/>
													<%}
													else {
													%>	
													 <html:text property="secondDOB" size="10" alt="secondDOB" name="appForm" maxlength="10"/>
													<IMG src="images/CalendarIcon.gif" width="20" onClick="showCalendar('appForm.secondDOB')" align="center">
													<%}%>
													</span></b>
													
												</td>
											</tr>

											 <tr>
												<td class="ColumnBackground" width="10%"><b>
													<span style="font-size: 9pt">3. <bean:message key="promoterName" />	</span></b>
												</td>
												<td width="10%" class="TableData">
												
													<b><span style="font-size: 9pt"> 

													<% if((appCommonFlag1.equals("0"))||(appCommonFlag1.equals("1"))||(appCommonFlag1.equals("2")) ||(appCommonFlag1.equals("3"))
													|| (appCommonFlag1.equals("4")) || (appCommonFlag1.equals("5")) || (appCommonFlag1.equals("6")) || (appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("14"))||(appCommonFlag1.equals("17"))||(appCommonFlag1.equals("18"))||(appCommonFlag1.equals("19")))
												{										

													%>
													<bean:write property="thirdName" name="appForm"/>
													<%}
													else {
													%>	
													<html:text property="thirdName" size="20" alt="thirdName" name="appForm" maxlength="50"/>
													<%}%>
													</span></b>
													
												</td>
												<td width="10%" class="ColumnBackground" >
													<b><span style="font-size: 9pt"> <bean:message key="promoterItpan" /></span></b>
												</td>
												<td width="10%" class="TableData">
												
													<b><span style="font-size: 9pt"> 
													<% if((appCommonFlag1.equals("0"))||(appCommonFlag1.equals("1"))||(appCommonFlag1.equals("2")) ||(appCommonFlag1.equals("3"))
													|| (appCommonFlag1.equals("4")) || (appCommonFlag1.equals("5")) || (appCommonFlag1.equals("6")) || (appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("14"))||(appCommonFlag1.equals("17"))||(appCommonFlag1.equals("18"))||(appCommonFlag1.equals("19")))
													{										

													%>
													<bean:write property="thirdItpan" name="appForm"/>
													<%}
													else {
													%>	
													<html:text property="thirdItpan" size="20" alt="thirdItpan" name="appForm" maxlength="10"/>
													<%}%>
													</span></b>
													
												</td>
												<td width="10%" class="ColumnBackground" >
													<b><span style="font-size: 9pt"><bean:message key="promoterDob" /></span></b>
												</td>
												<td width="10%" class="TableData">
												
													<b><span style="font-size: 9pt"> 

													<% if((appCommonFlag1.equals("0"))||(appCommonFlag1.equals("1"))||(appCommonFlag1.equals("2")) ||(appCommonFlag1.equals("3"))
													|| (appCommonFlag1.equals("4")) || (appCommonFlag1.equals("5")) || (appCommonFlag1.equals("6")) || (appCommonFlag1.equals("11"))||(appCommonFlag1.equals("12"))||(appCommonFlag1.equals("13")) || (appCommonFlag1.equals("14"))||(appCommonFlag1.equals("17"))||(appCommonFlag1.equals("18"))||(appCommonFlag1.equals("19")))
													{										

													%>
													<bean:write property="thirdDOB" name="appForm"/>
													<%}
													else {
													%>	
													 <html:text property="thirdDOB" size="10" alt="thirdDOB" name="appForm" maxlength="10"/>
													<IMG src="images/CalendarIcon.gif" width="20" onClick="showCalendar('appForm.thirdDOB')" align="center">
													<%}%>
													</span></b>
													
												</td>
											</tr>
										 </table>
									</td>
								 </tr>

               <tr> 
									   <td colspan="8">
										 <table width="100%" border="0" cellspacing="0" cellpadding="0">
											 <tr> 
												<TD width="31%" class="Heading"><!--<bean:message key="promoterHeader" /> -->Facility Details</TD>
												<TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>                 
												 <td>&nbsp;</td>
												 <td>&nbsp;</td>
											 </tr>											
											 <TR>
												<TD colspan="4" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
											</TR>                   
										</table>
									</td>
								</tr>
                 <tr valign="top"> 
									<td colspan="4">
										<table width="100%" border="0" align="right" cellpadding="0" cellspacing="1">
											<tr> 
												<td class="ColumnBackground" width="25%" align="left" valign="top" >
												<% if(appCommonFlag1.equals("7")|| appCommonFlag1.equals("8")|| appCommonFlag1.equals("10")|| appCommonFlag1.equals("0")|| appCommonFlag1.equals("3")|| appCommonFlag1.equals("5")|| appCommonFlag1.equals("11")|| appCommonFlag1.equals("13")||appCommonFlag1.equals("14")||appCommonFlag1.equals("19"))
												{
												%>
												<font color="#FF0000" size="2">*</font>
													<bean:message key="projectCost" />&nbsp;(TL)
												<%
												}
												else
												{
												%>
												<bean:message key="projectCost" />
												<%}%>
												</td>
												<td class="TableData" width="20%" align="left" valign="top" id="projectCost">

												<% if(appCommonFlag1.equals("11") || appCommonFlag1.equals("12") || appCommonFlag1.equals("13"))
												 {
												%>
												<bean:write property="projectCost" name="appForm"/>
												<%}
												else {
												%>									
													<html:text property="projectCost" size="20" alt="projectCost" name="appForm" maxlength="16" onchange="calltotalAmt()" onblur="javascript:calProjectOutlay()" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)"/>
												<%}%>
													<bean:message key="inRs" />
												</td>
                        </tr>
										 </table>
                     </td>
                     
                     <td colspan="4">
										<table width="100%" border="0" align="right" cellpadding="0" cellspacing="1">
											<tr> 
												<td class="ColumnBackground" width="25%" align="left" valign="top" >
											    &nbsp;
												</td>
												<td class="TableData" width="20%" align="left" valign="top" id="projectCost">
                      		&nbsp;
												</td>
                        </tr>
										 </table>
                     </td>
                     </tr>
                <tr valign="top"> 
									<td colspan="4">
										<table width="100%" border="0" align="right" cellpadding="0" cellspacing="1">
											<tr> 
												<td class="ColumnBackground" width="25%" align="left" valign="top" >
												<% if(appCommonFlag1.equals("7")|| appCommonFlag1.equals("8")|| appCommonFlag1.equals("10")|| appCommonFlag1.equals("0")|| appCommonFlag1.equals("3")|| appCommonFlag1.equals("5")|| appCommonFlag1.equals("11")|| appCommonFlag1.equals("13")||appCommonFlag1.equals("14")||appCommonFlag1.equals("19"))
												{
												%>
												<font color="#FF0000" size="2">*</font>
													<bean:message key="tcSanctioned" />
												<%
												}
												else
												{
												%>
												<bean:message key="tcSanctioned" />
												<%}%>
												</td>
												<td class="TableData" width="20%" align="left" valign="top" id="tcSanctioned">

												<% if(appCommonFlag1.equals("11") || appCommonFlag1.equals("12") || appCommonFlag1.equals("13"))
												 {
												%>
												<bean:write property="termCreditSanctioned" name="appForm"/>
												<%}
												else {
												%>									
													<html:text property="termCreditSanctioned" size="20" alt="tcSanctioned" name="appForm" maxlength="16" onchange="calltotalAmt()" onblur="javascript:calProjectOutlay()" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)"/>
												<%}%>
													<bean:message key="inRs" />
												</td>
                        </tr>
                        <tr>
                         <td colspan="4">
										    <table width="100%" border="0" align="right" cellpadding="0" cellspacing="1">
										  	<tr> 
												<td class="ColumnBackground" width="25%" align="left" valign="top" >
											    &nbsp;
												</td>
												<td class="TableData" width="20%" align="left" valign="top" id="tcSanctioned">
                      		&nbsp;
												</td>
                        </tr>
                		 </table>
                     </td>
										 </table>
                     </td>
                     <td colspan="4">
										 <table width="100%" border="0" align="right" cellpadding="0" cellspacing="1">
												<tr align="left">
															<td class="ColumnBackground" colspan="2" align="center">
															<% if(appCommonFlag1.equals("9")|| appCommonFlag1.equals("8")|| appCommonFlag1.equals("10")|| appCommonFlag1.equals("2")|| appCommonFlag1.equals("4")|| appCommonFlag1.equals("6")|| appCommonFlag1.equals("12")|| appCommonFlag1.equals("13")||appCommonFlag1.equals("18")||appCommonFlag1.equals("19"))
															{
															%>
															<font color="#FF0000" size="2">*</font>
																<bean:message key="wcLimitSanctioned"/>
															<%}
															else if(appCommonFlag1.equals("1"))
															{%>
															<font color="#FF0000" size="2">*</font>
																<bean:message key="wcEnhanceLimitSanctioned"/>
															<%}
															else
															{%>
															<bean:message key="wcLimitSanctioned"/>
															<%}%>
															</td>
														</tr>		
                            <tr align="left">
															<td class="ColumnBackground" align="left" valign="top" >
																<bean:message key="wcFundBased"/>
															</td>
															<td class="TableData" align="left" valign="top" id="wcFBsanctioned">

															<% if(appCommonFlag1.equals("11") || appCommonFlag1.equals("12") || appCommonFlag1.equals("13"))
															 {
															%>
															<bean:write property="wcFundBasedSanctioned" name="appForm"/>
															<%}
															else {
															%>		
															
																<html:text property="wcFundBasedSanctioned" size="20" alt="wcFundBased" name="appForm" maxlength="16" onchange="calltotalAmt()" onblur="javascript:calProjectOutlay()" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)"/>
															<%}%>
															
														
																<bean:message key="inRs"/>
															</td>						
														</tr>
														
													
										 </table>
                     </td>
                     </tr>
                     
                     <tr valign="top"> 
									  <td colspan="4">
										<table width="100%" border="0" align="right" cellpadding="0" cellspacing="1">
											<tr> 
												<td class="ColumnBackground" width="25%" align="left" valign="top" >
												<% if(appCommonFlag1.equals("7")|| appCommonFlag1.equals("8")|| appCommonFlag1.equals("10")|| appCommonFlag1.equals("0")|| appCommonFlag1.equals("3")|| appCommonFlag1.equals("5")|| appCommonFlag1.equals("11")|| appCommonFlag1.equals("13")||appCommonFlag1.equals("14")||appCommonFlag1.equals("19"))
												{
												%>
												<font color="#FF0000" size="2">*</font>
													<bean:message key="tcSubsidyOrEquity" />
												<%
												}
												else
												{
												%>
												<bean:message key="tcSubsidyOrEquity" />
												<%}%>
												</td>
												<td class="TableData" width="20%" align="left" valign="top" id="tcSanctioned">

												<% if(appCommonFlag1.equals("11") || appCommonFlag1.equals("12") || appCommonFlag1.equals("13"))
												 {
												%>
												<%}
												else {
												%>									
													<html:text property="tcSubsidyOrEquity" size="20" alt="tcSubsidyOrEquity" name="appForm" maxlength="16" onblur="javascript:calProjectOutlay()" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)"/>
												<%}%>
													<bean:message key="inRs" />
												</td>
                        </tr>
										 </table>
                     </td>
                     <td colspan="4">
										<table width="100%" border="0" align="right" cellpadding="0" cellspacing="1">
											<tr> 
												<td class="ColumnBackground" width="25%" align="left" valign="top" >
												<% if(appCommonFlag1.equals("7")|| appCommonFlag1.equals("8")|| appCommonFlag1.equals("10")|| appCommonFlag1.equals("0")|| appCommonFlag1.equals("3")|| appCommonFlag1.equals("5")|| appCommonFlag1.equals("11")|| appCommonFlag1.equals("13")||appCommonFlag1.equals("14")||appCommonFlag1.equals("19"))
												{
												%>
												<font color="#FF0000" size="2">*</font>
													<bean:message key="wcSubsidyOrEquity" />
												<%
												}
												else
												{
												%>
												<bean:message key="wcSubsidyOrEquity" />
												<%}%>
												</td>
												<td class="TableData" width="20%" align="left" valign="top" id="tcSanctioned">

												<% if(appCommonFlag1.equals("11") || appCommonFlag1.equals("12") || appCommonFlag1.equals("13"))
												 {
												%>
												<%}
												else {
												%>									
													<html:text property="wcSubsidyOrEquity" size="20" alt="wcSubsidyOrEquity" name="appForm" maxlength="16" onblur="javascript:calProjectOutlay()" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)"/>
												<%}%>
													<bean:message key="inRs" />
												</td>
                        </tr>
										 </table>
                     </td>
                 </tr>
                 <tr valign="top"> 
									  <td colspan="4">
										<table width="100%" border="0" align="right" cellpadding="0" cellspacing="1">
											<tr> 
												<td class="ColumnBackground" width="25%" align="left" valign="top" >
												<% if(appCommonFlag1.equals("7")|| appCommonFlag1.equals("8")|| appCommonFlag1.equals("10")|| appCommonFlag1.equals("0")|| appCommonFlag1.equals("3")|| appCommonFlag1.equals("5")|| appCommonFlag1.equals("11")|| appCommonFlag1.equals("13")||appCommonFlag1.equals("14")||appCommonFlag1.equals("19"))
												{
												%>
												<font color="#FF0000" size="2">*</font>
													<bean:message key="amountSanctionedDate" />
												<%
												}
												else
												{
												%>
												<bean:message key="amountSanctionedDate" />
												<%}%>
												</td>
												<td class="TableData" width="20%" align="left" valign="top" id="amountSanctionedDate">

												<% 	if((appCommonFlag1.equals("11"))||(appCommonFlag1.equals("13")))
												 {
												%>
												<%}
												else {
												%>	
                     	<html:text property="amountSanctionedDate" size="20" alt="amountSanctionedDate" name="appForm" maxlength="10"/>
														<IMG src="images/CalendarIcon.gif" width="20" onClick="showCalendar('appForm.amountSanctionedDate')" align="center">
							
												<%}%>
												</td>
                        </tr>
										 </table>
                     </td>
                    <td colspan="4">
										<table width="100%" border="0" align="right" cellpadding="0" cellspacing="1">
											<tr> 
												<td class="ColumnBackground" width="25%" align="left" valign="top" >
												<% if(appCommonFlag1.equals("7")|| appCommonFlag1.equals("8")|| appCommonFlag1.equals("10")|| appCommonFlag1.equals("0")|| appCommonFlag1.equals("3")|| appCommonFlag1.equals("5")|| appCommonFlag1.equals("11")|| appCommonFlag1.equals("13")||appCommonFlag1.equals("14")||appCommonFlag1.equals("19"))
												{
												%>
												<font color="#FF0000" size="2">*</font>
													<bean:message key="amountSanctionedDate" />
												<%
												}
												else
												{
												%>
												<bean:message key="amountSanctionedDate" />
												<%}%>
												</td>
												<td class="TableData" width="20%" align="left" valign="top" id="limitFundBasedSanctionedDate">

												<% 	if((appCommonFlag1.equals("11"))||(appCommonFlag1.equals("13")))
												 {
												%>
												<%}
												else {
												%>	
                   	<html:text property="limitFundBasedSanctionedDate" size="20" alt="limitFundbasedSanctionedDate" name="appForm" maxlength="10"/>
										<IMG src="images/CalendarIcon.gif" width="20" onClick="showCalendar('appForm.limitFundBasedSanctionedDate')" align="center">
									
												<%}%>
												</td>
                        </tr>
										 </table>
                     </td>
                    </TR>
                    <tr valign="top"> 
									  <td colspan="4">
										<table width="100%" border="0" align="right" cellpadding="0" cellspacing="1">
											<tr> 
												<td class="ColumnBackground" width="25%" align="left" valign="top" >
												<% if(appCommonFlag1.equals("7")|| appCommonFlag1.equals("8")|| appCommonFlag1.equals("10")|| appCommonFlag1.equals("0")|| appCommonFlag1.equals("3")|| appCommonFlag1.equals("5")|| appCommonFlag1.equals("11")|| appCommonFlag1.equals("13")||appCommonFlag1.equals("14")||appCommonFlag1.equals("19"))
												{
												%>
												<font color="#FF0000" size="2">*</font>
													<bean:message key="creditGuaranteed" />
												<%
												}
												else
												{
												%>
												<bean:message key="creditGuaranteed" />
												<%}%>
												</td>
												<td class="TableData" width="20%" align="left" valign="top" id="creditGuaranteed">

												<% 	if((appCommonFlag1.equals("11"))||(appCommonFlag1.equals("13")))
												 {
												%>
												<%}
												else {
												%>	
                 			<html:text property="creditGuaranteed" size="20" alt="creditGuaranteed" name="appForm" maxlength="16" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)"/>&nbsp;<bean:message key="inRs" />
												<%}%>
												</td>
                        </tr>
										 </table>
                     </td>
                     <td colspan="4">
										<table width="100%" border="0" align="right" cellpadding="0" cellspacing="1">
											<tr> 
												<td class="ColumnBackground" width="25%" align="left" valign="top" >
												<% if(appCommonFlag1.equals("7")|| appCommonFlag1.equals("8")|| appCommonFlag1.equals("10")|| appCommonFlag1.equals("0")|| appCommonFlag1.equals("3")|| appCommonFlag1.equals("5")|| appCommonFlag1.equals("11")|| appCommonFlag1.equals("13")||appCommonFlag1.equals("14")||appCommonFlag1.equals("19"))
												{
												%>
												<font color="#FF0000" size="2">*</font>
												<bean:message key="creditGuaranteed" />
												<%
												}
												else
												{
												%>
												<bean:message key="creditGuaranteed" />		
												<%}%>
												</td>
												<td class="TableData" width="20%" align="left" valign="top" id="creditFundBased">

												<% 	if((appCommonFlag1.equals("11"))||(appCommonFlag1.equals("13")))
												 {
												%>
												<%}
												else {
												%>	
                 		<html:text property="creditFundBased" size="20" alt="creditFundBased" name="appForm" maxlength="16" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)"/>&nbsp;
										<%}%>
										<bean:message key="inRs" />	
												</td>
                        </tr>
										 </table>
                     </td>
                    </tr>
                    
                   <tr valign="top"> 
									  <td colspan="4">
										<table width="100%" border="0" align="right" cellpadding="0" cellspacing="1">
											<tr> 
												<td class="ColumnBackground" width="25%" align="left" valign="top" >
												<% if(appCommonFlag1.equals("7")|| appCommonFlag1.equals("8")|| appCommonFlag1.equals("10")|| appCommonFlag1.equals("0")|| appCommonFlag1.equals("3")|| appCommonFlag1.equals("5")|| appCommonFlag1.equals("11")|| appCommonFlag1.equals("13")||appCommonFlag1.equals("14")||appCommonFlag1.equals("19"))
												{
												%>
												<font color="#FF0000" size="2">*</font>
											    <bean:message key="expiryDate" /> 
												<%
												}
												else
												{
												%>
													<bean:message key="expiryDate" /> 
                          <%}%>
												</td>
												<td class="TableData" width="20%" align="left" valign="top" id="expiryDate">

												<% 	if((appCommonFlag1.equals("11"))||(appCommonFlag1.equals("13")))
												 {
												%>
												<%}
												else {
												%>	
                     	<html:text property="expiryDate" size="20" alt="expiryDate" name="appForm" maxlength="10"/>
											<IMG src="images/CalendarIcon.gif" width="20" onClick="showCalendar('appForm.expiryDate')" align="center">
							
												<%}%>
												</td>
                        </tr>
										 </table>
                     </td>
                     <TD align="left" valign="top" class="ColumnBackground" colspan="4"></td>		
                    </TR>
                  <tr>	
                    <td class="ColumnBackground" colspan="7">									
										<font color="#FF0000" size="2">#&nbsp;</font> <font color="#008600" size="2"><b>Credit facilities above Rs. 50 lakh and upto Rs.100 lakh will have to be rated internally by the MLI and should be of investment grade.For loan facility upto 50 lakhs MLIs may indicate ‘N.A’ if rating is not available.</b></font><br>
								    </td>
                 </tr>
                 <TR align="left">
									<TD align="left" valign="top" class="ColumnBackground" colspan="2"><font color="#FF0000" size="2">*</font>&nbsp;Internal Rating</td>								
									<TD align="left" valign="top" class="tableData" width="84%" colspan="2">
									 <% if(appCommonFlag1.equals("11") || appCommonFlag1.equals("12") || appCommonFlag1.equals("13"))
										 {
										%>
									Internal Rating
									<%	}
										else
										{ %><html:text property="internalRating" size="20" alt="internal Rating" name="appForm" maxlength="5"/>
										<%}%>
									</TD>
                   <TD align="left" valign="top" class="ColumnBackground" colspan="4"></td>			
									</TR>
                 <!-- end part here -->
              <!--   <tr>
							   <td class="ColumnBackground"> 
									<font color="#FF0000" size="2">*</font>	&nbsp;
									<bean:message key="cgpan"/>			
								</td>
								<td  class="TableData">
									 	<bean:write property="cgpan" name="appForm"/>					      
								</td>
                </tr> -->
                <TR>
                 <TD align="left" valign="top" class="ColumnBackground" colspan="7" ><font color="#FF0000" size="2">*</font>&nbsp;
              <html:checkbox property="agree" value="Y" disabled="false"/> We accept all Terms and Conditions of the Scheme. 
               <A HREF="applicationValidationNew.do?method=applicationValidationNew">
                Click Here</A> to see Terms and Conditions:
                </TD>
              </TR>
              </table>	
          		</td>
        	</tr>
           
      		<TR >
       			<TD align="center" valign="baseline">
							<DIV align="center">
								<A href="javascript:submitForm('addPCGSTermCreditApp.do?method=submitPCGSApp')"><IMG src="images/Save.gif" alt="Save" width="49" height="37" border="0"></A>	
								<A href="javascript:document.appForm.reset()">
									<IMG src="images/Reset.gif" alt="Reset" width="49" height="37" border="0"></A>
								<A href="home.do?method=getMainMenu&menuIcon=<%=session.getAttribute("menuIcon")%>&mainMenu=<%=session.getAttribute("mainMenu")%>">
								<IMG src="images/Cancel.gif" alt="Cancel" width="49" height="37" border="0"></A>
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
</body>






					