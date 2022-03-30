<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<% session.setAttribute("CurrentPage","ASFappropriatePaymentsnew.do?method=getPaymentsMadeForASFNew");%>
<%@ include file="/jsp/SetMenuInfo.jsp" %>

<script type="text/javascript">
	function getPaymentsMadeForGF(){
		var fromDate = document.forms[0].dateOfTheDocument24.value;
		var toDate = document.forms[0].dateOfTheDocument25.value;
		if(fromDate == ''){
			alert('Please Enter From Date.');
			return false;
		}
		if(toDate == ''){
			alert('Please Enter To Date.');
			return false;
		}
		document.forms[0].target = '_self';
		document.forms[0].method = 'POST';
		document.forms[0].action = 'ASFappropriatePaymentsnew.do?method=getPaymentsMadeForASFNew';
		document.forms[0].submit();
	}
</script>

<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form action="ASFappropriatePaymentsnew.do?method=getPaymentsMadeForASFNew" method="POST">
	
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
							<table width="661" border="0" cellspacing="1" cellpadding="0">
							 <TR>
								<TD colspan="7">
									<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
										<TR>
											<TD width="31%" class="Heading">Appropriation Detail</TD>
											<TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
										</TR>
										<TR>
											<TD colspan="6" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
										</TR>
									</TABLE>
								</TD>
							</TR>
							</table>
							
							<table width="661" border="0" cellspacing="1" cellpadding="1">
								
								  <TR>
                     <TD align="left" valign="top" class="ColumnBackground">
                       <DIV align="left"> &nbsp; BANK NAME </DIV>
                     </TD>
									  
									 
									   <TD  align="left" valign="center" class="TableData">
										  <DIV align="left">
										     <html:select property="bankName">
                             <html:option value="select">Select</html:option>                                                                                       
 <html:option value="ALLAHABAD BANK"> ALLAHABAD BANK</html:option>
 <html:option value="ALLAHABAD UP GRAMIN BANK">ALLAHABAD UP GRAMIN BANK</html:option>
 <html:option value="ANDHRA BANK">ANDHRA BANK</html:option>
 <html:option value="ANDHRA PRADESH GRAMEENA VIKAS BANK">ANDHRA PRADESH GRAMEENA VIKAS BANK</html:option>
 <html:option value="ANDHRA PRADESH STATE FINANCIAL CORPORATION">ANDHRA PRADESH STATE FINANCIAL CORPORATION</html:option>
 <html:option value="ANDHRA PRAGATHI GRAMEENA BANK">ANDHRA PRAGATHI GRAMEENA BANK</html:option>
 <html:option value="ARYAVART GRAMIN BANK">ARYAVART GRAMIN BANK</html:option>
 <html:option value="ASSAM GRAMIN VIKASH BANK">ASSAM GRAMIN VIKASH BANK</html:option>
 <html:option value="AXIS BANK LIMITED">AXIS BANK LIMITED</html:option>
 <html:option value="BAITARANI GRAMYA BANK">BAITARANI GRAMYA BANK</html:option>
 <html:option value="BALLIA ETAWAH GRAMIN BANK">BALLIA ETAWAH GRAMIN BANK</html:option>
 <html:option value="BANGIYA GRAMIN VIKASH BANK">BANGIYA GRAMIN VIKASH BANK</html:option>
 <html:option value="BANK OF BAHRAIN AND KUWAIT">BANK OF BAHRAIN AND KUWAIT</html:option>
 <html:option value="BANK OF BARODA">BANK OF BARODA</html:option>
 <html:option value="BANK OF INDIA">BANK OF INDIA</html:option>
 <html:option value="BANK OF MAHARASHTRA">BANK OF MAHARASHTRA</html:option>
 <html:option value="BARAODA RAJASTHAN KSHETRIYA GRAMIN BANK">BARAODA RAJASTHAN KSHETRIYA GRAMIN BANK</html:option>
 <html:option value="BARCLAYS BANK PLC">BARCLAYS BANK PLC</html:option>
 <html:option value="BAREILLY KSHETRIYA GRAMIN BANK">BAREILLY KSHETRIYA GRAMIN BANK</html:option>
 <html:option value="BARODA GUJARAT GRAMIN BANK">BARODA GUJARAT GRAMIN BANK</html:option>
 <html:option value="BARODA RAJASTHAN GRAMIN BANK">BARODA RAJASTHAN GRAMIN BANK</html:option>
 <html:option value="BARODA UTTAR PRADESH GRAMIN BANK">BARODA UTTAR PRADESH GRAMIN BANK</html:option>
 <html:option value="BHARATIYA MAHILA BANK">BHARATIYA MAHILA BANK</html:option>
 <html:option value="BIHAR KSHETRIYA GRAMIN BANK">BIHAR KSHETRIYA GRAMIN BANK</html:option>
 <html:option value="CANARA BANK">CANARA BANK</html:option>
 <html:option value="CAUVERY KALPATARU GRAMEENA BANK">CAUVERY KALPATARU GRAMEENA BANK</html:option>
 <html:option value="CENTRAL BANK OF INDIA">CENTRAL BANK OF INDIA</html:option>

 
 <html:option value="CHAITANYA GODAVARI GRAMMENA BANK">CHAITANYA GODAVARI GRAMMENA BANK</html:option>
 <html:option value="CHATTISGARH GRAMIN BANK">CHATTISGARH GRAMIN BANK</html:option>
 <html:option value="CHIKMAGALUR-KODAGU GRAMEENA BANK">CHIKMAGALUR-KODAGU GRAMEENA BANK</html:option>
 <html:option value="CHITRADURGA GRAMIN BANK">CHITRADURGA GRAMIN BANK</html:option>
 <html:option value="CITY UNION BANK">CITY UNION BANK</html:option>
 <html:option value="CORPORATION BANK">CORPORATION BANK</html:option>
 <html:option value="DECCAN GRAMEENA BANK">DECCAN GRAMEENA BANK</html:option>
 <html:option value="DELHI FINANCIAL CORPORATION">DELHI FINANCIAL CORPORATION</html:option>
 <html:option value="DENA BANK">DENA BANK</html:option>
 <html:option value="DENA GUJARAT GRAMIN BANK">DENA GUJARAT GRAMIN BANK</html:option>
 <html:option value="DEUTSCHE BANK">DEUTSCHE BANK</html:option>
 <html:option value="DEVELOPMENT CREDIT BANK LTD">DEVELOPMENT CREDIT BANK LTD</html:option>
 <html:option value="DURG RAJNANDGAON GRAMIN BANK">DURG RAJNANDGAON GRAMIN BANK</html:option>
 <html:option value="SBI">ETAWAH KSHETRIYA GRAMIN BANK</html:option>
 <html:option value="ETAWAH KSHETRIYA GRAMIN BANK">EXPORT IMPORT BANK OF INDIA</html:option>
 <html:option value="GIRIDIH KSHETRIYA GRAMIN BANK">GIRIDIH KSHETRIYA GRAMIN BANK</html:option>
 <html:option value="GLOBAL TRUST BANK LIMITED">GLOBAL TRUST BANK LIMITED</html:option>
 <html:option value="GRAMIN BANK OF ARAVART">GRAMIN BANK OF ARAVART</html:option>
 <html:option value="GURGAON GRAMIN BANK">GURGAON GRAMIN BANK</html:option>
<html:option value="HADOTI KSHETRIYA GRAMIN BANK">HADOTI KSHETRIYA GRAMIN BANK</html:option>
<html:option value="HARYANA GRAMIN BANK">HARYANA GRAMIN BANK</html:option>
<html:option value="HDFC BANK LIMITED">HDFC BANK LIMITED</html:option>
<html:option value="HIMACHAL GRAMIN BANK">HIMACHAL GRAMIN BANK</html:option>
<html:option value="HOWRAH GRAMIN BANK">HOWRAH GRAMIN BANK</html:option>
<html:option value="ICICI BANK">ICICI BANK</html:option>
<html:option value="IDBI BANK LTD">IDBI BANK LTD</html:option>

<html:option value="INDIAN BANK">INDIAN BANK</html:option>
<html:option value="INDIAN OVERSEAS BANK">INDIAN OVERSEAS BANK</html:option>
<html:option value="INDUSIND BANK">INDUSIND BANK</html:option>
<html:option value="ING VYSYA BANK LTD">ING VYSYA BANK LTD</html:option>
<html:option value="J & K GRAMEEN BANK">J & K GRAMEEN BANK</html:option>
<html:option value="JAIPUR THAR GRAMIN BANK">JAIPUR THAR GRAMIN BANK</html:option>
<html:option value="JAMMU & KASHMIR DEVELOPMENT FINANCE CORPORATION LIMITED">JAMMU & KASHMIR DEVELOPMENT FINANCE CORPORATION LIMITED</html:option>
<html:option value="JHARKHAND GRAMIN BANK">JHARKHAND GRAMIN BANK</html:option>
<html:option value="KAPURTHALA-FIROZPUR KSHETRIYA GRAMIN BANK">KAPURTHALA-FIROZPUR KSHETRIYA GRAMIN BANK</html:option>
<html:option value="KARNATAKA BANK LTD">KARNATAKA BANK LTD.</html:option>
<html:option value="KARNATAKA VIKAS GRAMEENA BANK">KARNATAKA VIKAS GRAMEENA BANK</html:option>
<html:option value="KASHI GOMTI SAMYUT GRAMIN BANK">KASHI GOMTI SAMYUT GRAMIN BANK</html:option>
<html:option value="KERALA FINANCIAL CORPORATION">KERALA FINANCIAL CORPORATION</html:option>
<html:option value="KERALA GRAMIN BANK">KERALA GRAMIN BANK</html:option>
<html:option value="KOTAK MAHINDRA BANK">KOTAK MAHINDRA BANK</html:option>
<html:option value="KRISHNA GRAMEENA BANK">KRISHNA GRAMEENA BANK</html:option>
<html:option value="LAKSHMI VILAS BANK">LAKSHMI VILAS BANK</html:option>
<html:option value="LANGPI DEHANGI RURAL BANK">LANGPI DEHANGI RURAL BANK</html:option>
<html:option value="MADHYA BHARAT GRAMIN BANK">MADHYA BHARAT GRAMIN BANK</html:option>

<html:option value="MAHARASHTRA GODAVARI GRAMIN BANK">MAHARASHTRA GODAVARI GRAMIN BANK</html:option>
<html:option value="MAHARASHTRA GRAMIN BANK">MAHARASHTRA GRAMIN BANK</html:option>
<html:option value="MALWA GRAMIN BANK">MALWA GRAMIN BANK</html:option>
<html:option value="MARUDHARA GRAMIN BANK">MARUDHARA GRAMIN BANK</html:option>
<html:option value="MARWAR GRAMIN BANK">MARWAR GRAMIN BANK</html:option>
<html:option value="MEGHALAYA RURAL BANK">MEGHALAYA RURAL BANK</html:option>
<html:option value="MGB GRAMIN BANK">MGB GRAMIN BANK</html:option>
<html:option value="MIZORAM RURAL BANK">MIZORAM RURAL BANK</html:option>
<html:option value="NAINITAL-ALMORA KSHETRIYA GRAMIN BANK">NAINITAL-ALMORA KSHETRIYA GRAMIN BANK</html:option>
<html:option value="NARMADA MALWA GRAMIN BANK">NARMADA MALWA GRAMIN BANK</html:option>
<html:option value="NATIONAL SMALL INDUSTRIES CORPORATION LTD">NATIONAL SMALL INDUSTRIES CORPORATION LTD.</html:option>
<html:option value="NEELACHAL GRAMYA BANK">NEELACHAL GRAMYA BANK</html:option>
<html:option value="NORTH EASTERN DEVELOPMENT FINANCE CORPORATION LTD">NORTH EASTERN DEVELOPMENT FINANCE CORPORATION LTD.</html:option>
<html:option value="NORTH MALABAR GRAMINA BANK">NORTH MALABAR GRAMINA BANK</html:option>
<html:option value="ORIENTAL BANK OF COMMERCE">ORIENTAL BANK OF COMMERCE</html:option>
<html:option value="PALLAVAN GRAMA BANK">PALLAVAN GRAMA BANK</html:option>
<html:option value="PANDYAN GRAMA BANK">PANDYAN GRAMA BANK</html:option>
<html:option value="PARVATIYA GRAMIN BANK">PARVATIYA GRAMIN BANK</html:option>
<html:option value="PRAGATHI GRAMIN BANK">PRAGATHI GRAMIN BANK</html:option>
<html:option value="PRATHAMA BANK">PRATHAMA BANK</html:option>
<html:option value="PUDUVAI BHARATHIAR GRAMA BANK">PUDUVAI BHARATHIAR GRAMA BANK</html:option>
<html:option value="PUNJAB & SIND BANK">PUNJAB & SIND BANK</html:option>
<html:option value="PUNJAB GRAMIN BANK">PUNJAB GRAMIN BANK</html:option>
<html:option value="PUNJAB NATIONAL BANK">PUNJAB NATIONAL BANK</html:option>
<html:option value="PURVANCHAL GRAMIN BANK">PURVANCHAL GRAMIN BANK</html:option>
<html:option value="RAJASTHAN GRAMIN BANK">RAJASTHAN GRAMIN BANK</html:option>
<html:option value="REWA SIDHI GRAMIN BANK">REWA SIDHI GRAMIN BANK</html:option>
<html:option value="RUSHIKULYA GRAMYA BANK">RUSHIKULYA GRAMYA BANK</html:option>
<html:option value="SABHARKANTHA GANDHINAGAR GRAMIN BANK">SABHARKANTHA GANDHINAGAR GRAMIN BANK</html:option>
<html:option value="SAMASTIPUR KSHETRIYA GRAMIN BANK">SAMASTIPUR KSHETRIYA GRAMIN BANK</html:option>
<html:option value="SAPTAGIRI GRAMEENA BANK">SAPTAGIRI GRAMEENA BANK</html:option>
<html:option value="SARVA HARYANA GRAMIN BANK">SARVA HARYANA GRAMIN BANK</html:option>
<html:option value="SARVA UP GRAMIN BANK">SARVA UP GRAMIN BANK</html:option>
<html:option value="SATPURA NARMADA KSHETRIYA GRAMIN BANK">SATPURA NARMADA KSHETRIYA GRAMIN BANK</html:option>
<html:option value="SAURASHTRA GRAMIN BANK">SAURASHTRA GRAMIN BANK</html:option>
<html:option value="SHARDA GRAMIN BANK">SHARDA GRAMIN BANK</html:option>
<html:option value="SHIVALIK KSHETRIYA GRAMIN BANK">SHIVALIK KSHETRIYA GRAMIN BANK</html:option>
<html:option value="SHREYAS GRAMIN BANK">SHREYAS GRAMIN BANK</html:option>
<html:option value="SMALL INDUSTRIES DEVELOPMENT BANK OF INDIA">SMALL INDUSTRIES DEVELOPMENT BANK OF INDIA</html:option>
<html:option value="SOUTH MALABAR GRAMIN BANK">SOUTH MALABAR GRAMIN BANK</html:option>
<html:option value="SREE ANANTHA GRAMEENA BANK">SREE ANANTHA GRAMEENA BANK</html:option>
<html:option value="SRI SARASWATHI GRAMEENA BANK">SRI SARASWATHI GRAMEENA BANK</html:option>
<html:option value="STANDARD CHARTERED BANK">STANDARD CHARTERED BANK</html:option>
<html:option value="STATE BANK OF BIKANER & JAIPUR">STATE BANK OF BIKANER & JAIPUR</html:option>
<html:option value="STATE BANK OF HYDERABAD">STATE BANK OF HYDERABAD</html:option>
<html:option value="STATE BANK OF INDIA">STATE BANK OF INDIA</html:option>
<html:option value="STATE BANK OF INDORE">STATE BANK OF INDORE</html:option>
 <html:option value="STATE BANK OF MYSORE">STATE BANK OF MYSORE</html:option>
 <html:option value="STATE BANK OF PATIALA">STATE BANK OF PATIALA</html:option>
 <html:option value="STATE BANK OF SAURASHTRA">STATE BANK OF SAURASHTRA</html:option>
 <html:option value="STATE BANK OF TRAVANCORE">STATE BANK OF TRAVANCORE</html:option>
 <html:option value="SURGUJA KSHETRIYA GRAMIN BANK">SURGUJA KSHETRIYA GRAMIN BANK</html:option>
 <html:option value="SUTLEJ GRAMIN BANK">SUTLEJ GRAMIN BANK</html:option>
 <html:option value="SYNDICATE BAN">SYNDICATE BANK</html:option>
 <html:option value="TAMILNAD MERCANTILE BANK LTD">TAMILNAD MERCANTILE BANK LTD</html:option>
 <html:option value="THE BANK OF RAJASTHAN LTD">THE BANK OF RAJASTHAN LTD.</html:option>
 <html:option value="THE CATHOLIC SYRIAN BANK LIMITED">THE CATHOLIC SYRIAN BANK LIMITED</html:option>
 <html:option value="THE DHANALAKSHMI BANK LIMITED">THE DHANALAKSHMI BANK LIMITED</html:option>
 <html:option value="THE FEDERAL BANK LTD">THE FEDERAL BANK LTD</html:option>
 <html:option value="THE JAMMU & KASHMIR BANK LTD">THE JAMMU & KASHMIR BANK LTD</html:option>
 <html:option value="THE KARUR VYSYA BANK LTD">THE KARUR VYSYA BANK LTD.</html:option>
 <html:option value="THE NAINITAL BANK LTD">THE NAINITAL BANK LTD.</html:option>
 <html:option value="THE RATNAKAR BANK LTD">THE RATNAKAR BANK LTD</html:option>
 <html:option value="THE SOUTH INDIAN BANK LIMITED">THE SOUTH INDIAN BANK LIMITED</html:option>
 <html:option value="THE TAMILNADU INDUSTRIAL INVESTMENT CORPORATION LIMITED">THE TAMILNADU INDUSTRIAL INVESTMENT CORPORATION LIMITED</html:option>
 <html:option value="TRIPURA GRAMIN BANK">TRIPURA GRAMIN BANK</html:option>
 <html:option value="TRIVENI KSHETRIYA GRAMIN BANK">TRIVENI KSHETRIYA GRAMIN BANK</html:option>
 <html:option value="UCO BANK">UCO BANK</html:option>
 <html:option value="UNION BANK OF INDIA">UNION BANK OF INDIA</html:option>
 <html:option value="UNITED BANK OF INDIA">UNITED BANK OF INDIA</html:option>
 <html:option value="UTTAR BIHAR GRAMIN BANK">UTTAR BIHAR GRAMIN BANK</html:option>
 <html:option value="UTTARANCHAL GRAMIN BANK">UTTARANCHAL GRAMIN BANK</html:option>
 <html:option value="UTTARBANGA KSHETRIYA GRAMIN BANK">UTTARBANGA KSHETRIYA GRAMIN BANK</html:option>
 <html:option value="VANANCHAL GRAMIN BANK">VANANCHAL GRAMIN BANK</html:option>
 <html:option value="VIDHARBHA KSHETRIYA GRAMIN BANK">VIDHARBHA KSHETRIYA GRAMIN BANK</html:option>
 <html:option value="VIDISHA BHOPAL KHETRIYA GRAMIN BANK">VIDISHA BHOPAL KHETRIYA GRAMIN BANK</html:option>
 <html:option value="VIJAYA BANK">VIJAYA BANK</html:option>
 <html:option value="VISVESHVARAYA GRAMEENA BANK">VISVESHVARAYA GRAMEENA BANK</html:option>
 <html:option value="WAINGANGA KRISHNA GRAMIN BANK">WAINGANGA KRISHNA GRAMIN BANK</html:option>
 <html:option value="YES BANK LTD">YES BANK LTD</html:option>
                              
                         </html:select>
										  </DIV>
									  </TD>
									  <TD class="ColumnBackground"></TD>
								 </TR>
								<tr align="left">
									<td class="ColumnBackground" width="207">
										<div align="center">
										  &nbsp;<bean:message key="fromdate"/>
										</div>
									 </td>
									 <td class="TableData" width="343">
										<div align="left">
										  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										  <html:text property="dateOfTheDocument24" size="10" maxlength="10" name="rpAllocationForm"/>
										  <img src="images/CalendarIcon.gif" alt="" width="20" onClick="showCalendar('rpAllocationForm.dateOfTheDocument24')" align="center">
										</div>
									 </td>
									 <td class="ColumnBackground" width="207">
										<div align="center">
										  &nbsp;<bean:message key="toDate"/>
										</div>
									 </td>
									 <td class="TableData" width="343">
										<div align="left">
										  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										  <html:text property="dateOfTheDocument25" size="10" maxlength="10" name="rpAllocationForm"/>
										  <img src="images/CalendarIcon.gif" alt="" width="20" onClick="showCalendar('rpAllocationForm.dateOfTheDocument25')" align="center">
										</div>
									 </td>
								</tr>
								
							 </table>									
						</td>
					</tr>

					
					<TR >
						<TD align="center" valign="baseline" >
							<DIV align="center">								
								 <A href="javascript:submitForm('ASFappropriatePaymentsnew.do?method=getPaymentsMadeForASFNew')">
								<!-- <A href="#" onclick="getPaymentsMadeForGF();"> -->
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