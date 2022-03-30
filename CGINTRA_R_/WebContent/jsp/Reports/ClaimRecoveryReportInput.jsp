<%@ page language="java"%>
<%@ page import="java.util.ArrayList"%>

<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic" %>
<%@ include file="/jsp/SetMenuInfo.jsp" %>
<% session.setAttribute("CurrentPage","claimRecoveryReportInput.do?method=claimRecoveryReportInput");%>

<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form action="claimRecoveryReport.do?method=claimRecoveryReport" method="POST" enctype="multipart/form-data" >
		<TR> 
			<TD width="20" align="right" valign="bottom"><IMG src="images/TableLeftTop.gif" width="20" height="31"></TD>
			<TD background="images/TableBackground1.gif"><IMG src="images/ReportsHeading.gif" width="121" height="25"></TD>
			<TD width="20" align="left" valign="bottom"><IMG src="images/TableRightTop.gif" width="23" height="31"></TD>
		</TR>
		<TR>
			<TD width="20" background="images/TableVerticalLeftBG.gif">&nbsp;</TD>
			<TD>
			<DIV align="right">			
			<A HREF="disbursementReportHelp.do?method=disbursementReportHelp">
			HELP</A>
				<TABLE width="100%" border="0" align="left" cellpadding="0" cellspacing="0">
				<tr>
				<TD  align="left" colspan="4"><font size="2"><bold>
				Fields marked as </font><font color="#FF0000" size="2">*</font><font size="2"> are mandatory</bold></font>
				</td>
				</tr>
					<TR>
						<TD>
							<TABLE width="100%" border="0" cellspacing="1" cellpadding="1">
								<TR>
									<TD colspan="10"> 
										<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
											<TR>
												<TD width="27%" class="Heading"><bean:message key="recoveryReportHeader" /></TD>
												<TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
											</TR>
											<TR>
												<TD colspan="3" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
											</TR>

										</TABLE>
									</TD>

									<TR>
									<TD align="left" valign="top" class="ColumnBackground">
									<DIV align="center">
										  &nbsp;<bean:message key="fromdate" /> 
										  </DIV>
									</TD>
									   <TD  align="left" valign="center" class="TableData">
										  <DIV align="left">
										  <html:text property="dateOfTheDocument18" maxlength="10" size="40" name="rsForm" />
										  <IMG src="images/CalendarIcon.gif" width="20" onClick="showCalendar('rsForm.dateOfTheDocument18')" align="center">
										  <DIV align="left">
									  </TD>
									
									  <TD align="left" valign="top" class="ColumnBackground">
									
										  &nbsp;<bean:message key="toDate"/> 
										  
									</TD>

									    <TD  align="left" valign="center" class="TableData">
										  <DIV align="left">
										  <html:text property="dateOfTheDocument19" size="20" maxlength="10" name="rsForm"/>
										  <IMG src="images/CalendarIcon.gif" width="20" onClick="showCalendar('rsForm.dateOfTheDocument19')" align="center">
										  <DIV align="left">
									  </TD>
									  </TR>				
									<TR> 
									<TD align="left" class="ColumnBackground" width="207">
							<DIV align="left">BANK NAME</DIV>
							</TD>
							<TD class="TableData"  width="343">
							<html:select property="bankName"  style="width:84%;">
								<html:option value="''">----------------Select----------------</html:option>
								<html:option value="001700000000"> ALLAHABAD BANK</html:option>
							<html:option value="008300000000">ALLAHABAD UP GRAMIN BANK</html:option>
							<html:option value="001000000000">ANDHRA BANK</html:option>
							<html:option value="010400000000">ANDHRA PRADESH GRAMEENA VIKAS BANK</html:option>
							<html:option value="012900000000">ANDHRA PRADESH STATE FINANCIAL CORPORATION</html:option>
							<html:option value="006500000000">ANDHRA PRAGATHI GRAMEENA BANK</html:option>
							<html:option value="008200000000">ARYAVART GRAMIN BANK</html:option>
							<html:option value="00910000000">ASSAM GRAMIN VIKASH BANK</html:option>
							<html:option value="004300000000">AXIS BANK LIMITED</html:option>
							<html:option value="010000000000">BAITARANI GRAMYA BANK</html:option>
							<html:option value="012200000000">BALLIA ETAWAH GRAMIN BANK</html:option>
							<html:option value="013300000000">BANGIYA GRAMIN VIKASH BANK</html:option>
							<html:option value="013800000000">BANK OF BAHRAIN AND KUWAIT</html:option>
							<html:option value="000200000000">BANK OF BARODA</html:option>
							<html:option value="001500000000">BANK OF INDIA</html:option>
							<html:option value="001600000000">BANK OF MAHARASHTRA</html:option>
							<html:option value="015300000000">BARAODA RAJASTHAN KSHETRIYA GRAMIN BANK</html:option>
							<html:option value="013700000000">BARCLAYS BANK PLC</html:option>
							<html:option value="005100000000">BAREILLY KSHETRIYA GRAMIN BANK</html:option>
							<html:option value="009400000000">BARODA GUJARAT GRAMIN BANK</html:option>
							<html:option value="014500000000">BARODA RAJASTHAN GRAMIN BANK</html:option>
							<html:option value="006200000000">BARODA UTTAR PRADESH GRAMIN BANK</html:option>
							<html:option value="015500000000">BHARATIYA MAHILA BANK</html:option>
							<html:option value="012300000000">BIHAR KSHETRIYA GRAMIN BANK</html:option>
							<html:option value="001900000000">CANARA BANK</html:option>
						<%-- 	<html:option value="CAUVERY KALPATARU GRAMEENA BANK">CAUVERY KALPATARU GRAMEENA BANK</html:option> --%>
							<html:option value="001100000000">CENTRAL BANK OF INDIA</html:option>
							<html:option value="004100000000">CHAITANYA GODAVARI GRAMEENA BANK</html:option>
							<html:option value="011600000000">CHATTISGARH RAJYA GRAMIN BANK</html:option>
							<html:option value="005200000000">CHIKMAGALUR-KODAGU GRAMEENA BANK</html:option>
							<html:option value="005400000000">CHITRADURGA GRAMIN BANK</html:option>
							<html:option value="010200000000">CITY UNION BANK</html:option>
							<html:option value="000900000000">CORPORATION BANK</html:option>
							<%-- <html:option value="DECCAN GRAMEENA BANK">DECCAN GRAMEENA BANK</html:option> --%>
							<html:option value="010300000000">DELHI FINANCIAL CORPORATION</html:option>
							<html:option value="000600000000">DENA BANK</html:option>
							<html:option value="010100000000">DENA GUJARAT GRAMIN BANK</html:option>
							<html:option value="009600000000">DEUTSCHE BANK</html:option>
							<html:option value="013600000000">DEVELOPMENT CREDIT BANK LTD</html:option>
							<html:option value="006900000000">DURG RAJNANDGAON GRAMIN BANK</html:option>
							<html:option value="010800000000">ETAWAH KSHETRIYA GRAMIN BANK</html:option>
							<html:option value="013900000000">EXPORT IMPORT BANK OF INDIA</html:option>
							<html:option value="005300000000">GIRIDIH KSHETRIYA GRAMIN BANK</html:option>
							<html:option value="003900000000">GLOBAL TRUST BANK LIMITED</html:option>
							<html:option value="015100000000">GRAMIN BANK OF ARAVART</html:option>
							<html:option value="007700000000">GURGAON GRAMIN BANK</html:option>
							<html:option value="011700000000">HADOTI KSHETRIYA GRAMIN BANK</html:option>
							<html:option value="006600000000">HARYANA GRAMIN BANK</html:option>
							<html:option value="000800000000">HDFC BANK LIMITED</html:option>
							<html:option value="009700000000">HIMACHAL GRAMIN BANK</html:option>
							<html:option value="004000000000">HOWRAH GRAMIN BANK</html:option>
							<html:option value="002700000000">ICICI BANK</html:option>
							<html:option value="001300000000">IDBI BANK LTD</html:option>
							<html:option value="003500000000">IDBI BANK LTD</html:option> 
							<html:option value="002300000000">INDIAN BANK</html:option>
							<html:option value="000400000000">INDIAN OVERSEAS BANK</html:option>
							<html:option value="002800000000">INDUSIND BANK</html:option>
							<html:option value="004700000000">ING VYSYA BANK LTD</html:option>
							<html:option value="014300000000">J & K GRAMEEN BANK</html:option>
							<html:option value="008100000000">JAIPUR THAR GRAMIN BANK</html:option>
							<html:option value="012700000000">JAMMU & KASHMIR DEVELOPMENTFINANCE CORPORATION LIMITED</html:option>
							<html:option value="013500000000">JHARKHAND GRAMIN BANK</html:option>
							<html:option value="004800000000">KAPURTHALA-FIROZPUR KSHETRIYA GRAMIN BANK</html:option>
							<html:option value="008700000000">KAVERI GRAMEENA BANK</html:option>
							<html:option value="006300000000">KARNATAKA BANK LTD</html:option>
							<html:option value="006100000000">KARNATAKA VIKAS GRAMEENA BANK</html:option>
							<html:option value="007300000000">KASHI GOMTI SAMYUT GRAMIN BANK</html:option>
							<html:option value="011400000000">KERALA FINANCIAL CORPORATION</html:option>
							<html:option value="015600000000">KERALA GRAMIN BANK</html:option>
							<html:option value="005500000000">KOTAK MAHINDRA BANK</html:option>
							<html:option value="009900000000">KRISHNA GRAMEENA BANK</html:option>
							<html:option value="013400000000">LAKSHMI VILAS BANK</html:option>
							<html:option value="012500000000">LANGPI DEHANGI RURAL BANK</html:option>
							<html:option value="011000000000">MADHYA BHARAT GRAMIN BANK</html:option>
							<html:option value="008600000000">MADHYA BIHAR GRAMIN BANK</html:option>
							<html:option value="010900000000">MAHARASHTRA GODAVARI GRAMIN BANK</html:option>
							<html:option value="04400000000">MAHARASHTRA GRAMIN BANK</html:option>
							<html:option value="012000000000">MALWA GRAMIN BANK</html:option>
							<html:option value="015200000000">MARUDHARA GRAMIN BANK</html:option>
							<html:option value="005700000000">MARWAR GRAMIN BANK</html:option>
							<html:option value="014800000000">MEGHALAYA RURAL BANK</html:option>
							<html:option value="007200000000">MGB GRAMIN BANK</html:option>
							<html:option value="011500000000">MIZORAM RURAL BANK</html:option>
							<html:option value="006400000000">NAINITAL-ALMORA KSHETRIYA GRAMIN BANK</html:option>
							<html:option value="008900000000">NARMADA MALWA GRAMIN BANK</html:option>
							<html:option value="000300000000">NATIONAL SMALL INDUSTRIES CORPORATION LTD.</html:option>
							<html:option value="011800000000">NEELACHAL GRAMYA BANK</html:option>
							<html:option value="002000000000">NORTH EASTERN DEVELOPMENT FINANCECORPORATION LTD.</html:option>
							<html:option value="009500000000">NORTH MALABAR GRAMINA BANK</html:option>
							<html:option value="001400000000">ORIENTAL BANK OF COMMERCE</html:option>
							<html:option value="009000000000">PALLAVAN GRAMA BANK</html:option>
							<html:option value="012100000000">PANDYAN GRAMA BANK</html:option>
							<html:option value="007800000000">PARVATIYA GRAMIN BANK</html:option>
							<html:option value="006800000000">PRAGATHI GRAMIN BANK</html:option>
							<html:option value="06800000000">PRAGATHI KRISHNA GRAMEENA BANK</html:option>
							<html:option value="002400000000">PRATHAMA BANK</html:option>
							<html:option value="015000000000">PUDUVAI BHARATHIAR GRAMA BANK</html:option>
							<html:option value="000700000000">PUNJAB & SIND BANK</html:option>
							<html:option value="007100000000">PUNJAB GRAMIN BANK</html:option>
							<html:option value="000100000000">PUNJAB NATIONAL BANK</html:option>
							<html:option value="007100000000">PURVANCHAL GRAMIN BANK</html:option>
							<html:option value="007600000000">RAJASTHAN GRAMIN BANK</html:option>
							<html:option value="011900000000">REWA SIDHI GRAMIN BANK</html:option>
							<html:option value="008400000000">RUSHIKULYA GRAMYA BANK</html:option>
							<html:option value="002600000000">SABHARKANTHA GANDHINAGAR GRAMIN BANK</html:option>
							<html:option value="014000000000">SAMASTIPUR KSHETRIYA GRAMIN BANK</html:option>
							<html:option value="006700000000">SAPTAGIRI GRAMEENA BANK</html:option>
							<html:option value="015700000000">SARVA HARYANA GRAMIN BANK</html:option>
							<html:option value="011300000000">SARVA UP GRAMIN BANK</html:option>
							<html:option value="013100000000">SATPURA NARMADA KSHETRIYA GRAMIN BANK</html:option>
							<html:option value="008800000000">SAURASHTRA GRAMIN BANK</html:option>
							<html:option value="014200000000">SHARDA GRAMIN BANK</html:option>
							<html:option value="004400000000">SHIVALIK KSHETRIYA GRAMIN BANK</html:option>
							<html:option value="004900000000">SHREYAS GRAMIN BANK</html:option>
							<html:option value="003600000000">SMALL INDUSTRIES DEVELOPMENT BANK OF INDIA</html:option>
							<html:option value="005900000000">SOUTH MALABAR GRAMIN BANK</html:option>
							<html:option value="005800000000">SREE ANANTHA GRAMEENA BANK</html:option>
							<html:option value="001800000000">SRI SARASWATHI GRAMEENA BANK</html:option>
							<html:option value="011100000000">STANDARD CHARTERED BANK</html:option>
							<html:option value="003000000000">STATE BANK OF BIKANER & JAIPUR</html:option>
							<html:option value="003100000000">STATE BANK OF HYDERABAD</html:option>
							<html:option value="002500000000">STATE BANK OF INDIA</html:option>
							<html:option value="003300000000">STATE BANK OF INDORE</html:option>
							<html:option value="003200000000">STATE BANK OF MYSORE</html:option>
							<html:option value="003400000000">STATE BANK OF PATIALA</html:option>
							<html:option value="005000000000">STATE BANK OF SAURASHTRA</html:option>
							<html:option value="003800000000">STATE BANK OF TRAVANCORE</html:option>
							<html:option value="014100000000">SURGUJA KSHETRIYA GRAMIN BANK</html:option>
							<html:option value="012600000000">SUTLEJ GRAMIN BANK</html:option>
							<html:option value="002100000000">SYNDICATE BANK</html:option>
							<html:option value="006000000000">TAMILNAD MERCANTILE BANK LTD</html:option>
							<html:option value="011200000000">TELANGANA GRAMIN BANK</html:option>
							<html:option value="004200000000">THE BANK OF RAJASTHAN LTD</html:option>
							<html:option value="015400000000">THE CATHOLIC SYRIAN BANK LIMITED</html:option>
							<html:option value="009200000000">THE DHANALAKSHMI BANK LIMITED</html:option>
							<html:option value="005600000000">THE FEDERAL BANK LTD</html:option>
							<html:option value="004500000000">THE JAMMU & KASHMIR BANK LTD</html:option>
							<html:option value="012800000000">THE KARUR VYSYA BANK LTD</html:option>
							<html:option value="004600000000">THE NAINITAL BANK LTD</html:option>
							<html:option value="014700000000">THE RATNAKAR BANK LTD</html:option>
							<html:option value="003700000000">THE SOUTH INDIAN BANK LIMITED</html:option>
							<html:option value="009300000000">THE TAMILNADU INDUSTRIALINVESTMENT CORPORATION LIMITED</html:option>
							<html:option value="010600000000">TRIPURA GRAMIN BANK</html:option>
							<html:option value="008500000000">TRIVENI KSHETRIYA GRAMIN BANK</html:option>
							<html:option value="002200000000">UCO BANK</html:option>
							<html:option value="000500000000">UNION BANK OF INDIA</html:option>
							<html:option value="002900000000">UNITED BANK OF INDIA</html:option>
							<html:option value="013000000000">UTTAR BIHAR GRAMIN BANK</html:option>
							<html:option value="008000000000">UTTARANCHAL GRAMIN BANK</html:option>
							<html:option value="010500000000">UTTARBANGA KSHETRIYA GRAMIN BANK</html:option>
							<html:option value="014600000000">VANANCHAL GRAMIN BANK</html:option>
							<html:option value="010700000000">VIDHARBHA KSHETRIYA GRAMIN BANK</html:option>
							<html:option value="012400000000">VIDISHA BHOPAL KHETRIYA GRAMIN BANK</html:option>
							<html:option value="001200000000">VIJAYA BANK</html:option>
							<html:option value="009800000000">VISVESHVARAYA GRAMEENA BANK</html:option>
							<html:option value="013200000000">WAINGANGA KRISHNA GRAMIN BANK</html:option>
							<html:option value="007500000000">YES BANK LTD</html:option>
							</html:select>
							</TD>	
							<td class="ColumnBackground" width="207">
							   <DIV align="left">&nbsp; Member Id</DIV>
							</TD>
							<td class="TableData" width="343">
							<html:text property="bank_id" name="rsForm" size="25" maxlength="12"></html:text>
							</TD>								
						</TR>
							</TABLE>
						</TD>
					</TR>
					<TR >
						<TD height="20" >
							&nbsp;
						</TD>
					</TR>
					<TR >
						<TD align="center" valign="baseline" >
							<DIV align="center">
							       <A href="javascript:submitForm('recoverySettledExcelReportNew.do?method=recoverySettledExcelReportNew&amp;&amp;fileType=XLSType')">
									<IMG  src="images/deepakXls.jpg" alt="Download XLS" width="30"
										height="29" border="0">
									</A> &nbsp;&nbsp;
							        <A href="javascript:submitForm('claimRecoveryReport.do?method=claimRecoveryReport')">
									<IMG src="images/OK.gif" alt="OK" width="49" height="37" border="0"></A>
								    <!-- <A href="javascript:document.getElementById('rsForm').reset();">
									<IMG src="images/Reset.gif" alt="Reset" width="49" height="37" border="0"></A>
									 -->
									
									<a href="subHome.do?method=getSubMenu&menuIcon=<%=session.getAttribute("menuIcon")%>&mainMenu=<%=session.getAttribute("mainMenu")%>">
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
