<%@ page language="java"%>
<%@ page import="com.cgtsi.actionform.RPActionForm"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic" %>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ include file="/jsp/SetMenuInfo.jsp" %>

<%
	session.setAttribute("CurrentPage", "submitClmRecovryAppropriation.do?method=submitClmRecovryAppropriation");
SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");

%>
<html>
	<head>		
		<title></title>
		<script type="text/javascript">

		function Submitclaimrecoverydetail()
		{
			//alert("SubmitNpaApprForm S : "+actionType);	
			var checkList1 = document.getElementsByName('check');
			//alert("checkList name : "+checkList1.length);
			//var check = false;
			//var comment = false;

			var cnt=0;
		
			for(var j=0; j<checkList1.length; j++)
			{
			 if(checkList1[j].checked)
				{
			     cnt++;
				}			
			}

			if(cnt==0)
			{
             alert("please select atleast one check box")
			}
			else			
		
			{		
				//alert("inside if");
				document.rpAllocationForm.action="submitClmRecovryAppropriation.do?method=submitClmRecovryAppropriation";
				document.rpAllocationForm.target="_self";
				document.rpAllocationForm.method="POST";
				document.rpAllocationForm.submit();
			}
			//alert("SubmitNpaApprForm E");
		}
		</script>
	</head>
	<body>
	 
		<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
			<html:errors />
			<html:form  action="submitClmRecovryAppropriation.do?method=submitClmRecovryAppropriation" method="POST" enctype="multipart/form-data">
				<TR> 
					<TD width="20" align="right" valign="bottom"><IMG src="images/TableLeftTop.gif" width="20" height="31"></TD>
					<TD background="images/TableBackground1.gif"><IMG src="images/ReceiptsPaymentsHeading.gif"></TD>
					<TD width="20" align="left" valign="bottom"><IMG src="images/TableRightTop.gif" width="23" height="31"></TD>
				</TR>
				<TR>
					<TD width="20" background="images/TableVerticalLeftBG.gif">&nbsp;</TD>			
					<TD>
						<TABLE width="100%" border="0" align="left" cellpadding="1" cellspacing="1">
							<TR>
								<TD colspan="12"> 
									<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
										<TR>
											<TD width="35%" class="Heading">Appropriation Form Detail:</TD>
											<TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
										</TR>
										<TR><TD colspan="12" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD></TR>
									</TABLE>
								</TD>
							</TR>
							<tr><td colspan="8"><font color="Green" size="3">  </font></td></tr>
							<TR>
								<TD align="left" valign="top" class="ColumnBackground" width="50">
									<b><bean:message key="sNo" /></b>
								</TD>
								<TD align="left" valign="top" class="ColumnBackground" width="98">
									<b><bean:message key="cpmliid" /></b>
								</TD>
								<TD align="left" valign="top" class="ColumnBackground" width="98">
									<b><bean:message key="zoneName" /></b>
								</TD>	
								<TD align="left" valign="top" class="ColumnBackground" width="98">
									<b>Cgpan</b>
								</TD>
								<TD align="left" valign="top" class="ColumnBackground" width="98">
									<b>Guaratnee Amount</b>
								</TD>					
								<TD align="left" valign="top" class="ColumnBackground" width="98">
									<b>Unit Name</b>
								</TD>
								<TD align="left" valign="top" class="ColumnBackground" width="98">
									<b>First Installement Amount</b>
								</TD>
								<TD align="left" valign="top" class="ColumnBackground" width="98">
									<b>Amount Remitted to CGTMSE</b>
								</TD>
								<TD align="left" valign="top" class="ColumnBackground" width="98">
									<b>Type of Recovery</b> 
								</TD>						
								<TD align="left" valign="top" class="ColumnBackground" width="114">
									<b>RP Number</b>
								</TD>
								<TD align="left" valign="top" class="ColumnBackground" width="114">
									<b>Payment Date</b>
								</TD>								
							</TR>
							<html:hidden name="rpAllocationForm" property="checkerId"/>							
							<logic:iterate id="object" name="rpAllocationForm" property="recvryPaymentList" indexId="index">
								<%
								RPActionForm Recovryform = null;
								String instDate="";
								String checkerId1="";
								try
								{
								 Recovryform = (RPActionForm)object;
									String empComments = "";
									System.out.println("JSP Recovryform.getCheckerId() : ="+Recovryform.getCheckerId()+"=");
									checkerId1 =String.valueOf(Recovryform.getCheckerId()) ;
									System.out.println("JSP checkerId : "+checkerId1);									
								  java.util.Date  instDate1=Recovryform.getInstrumentDate();   
									instDate=dateFormat.format(Recovryform.getInstrumentDate());
									System.out.println("JSP Recovryform.getCgpan() : "+Recovryform.getCgpan());
								}
								catch(Exception  e)
								{
									e.printStackTrace();
								}
									 
								%>
								<TR>
									<TD align="left" valign="top" class="tableData" width="50"><%=Integer.parseInt(index+"")+1%></TD>
									<TD align="left" valign="top" class="tableData" width="98"><%=Recovryform.getMemberId()%></TD>
									<TD align="left" valign="top" class="tableData" width="98"><%=Recovryform.getZoneName()%></TD>										
									<TD align="left" valign="top" class="tableData" width="98"><%=Recovryform.getCgpan()%></TD>
									<TD align="left" valign="top" class="tableData" width="98"><%=Recovryform.getGuaranteeAppAmt()%></TD>										
									<TD align="left" valign="top" class="tableData" width="98"><%=Recovryform.getUnitName()%></TD>
									<TD align="left" valign="top" class="tableData" width="98"><%=Recovryform.getFirstClaimSetAmt()%></TD>
									<TD align="left" valign="top" class="tableData" width="98"><%=Recovryform.getAmountRemitedtoCgtmse()	%></TD>
									<TD align="left" valign="top" class="tableData" width="98"><%=Recovryform.getRecoveryType()%></TD>
								<TD align="left" valign="top" class="tableData" width="114"><%=Recovryform.getPayId()%></TD>
								<TD align="left" valign="top" class="tableData" width="114"><%=instDate%></TD><!--									
									 <TD align="left" valign="top" class="ColumnBackground" width="114">&nbsp;&nbsp;
										<html:checkbox property="check" value="<%=checkerId1%>" name="rpAllocationForm"/>
									</TD>									
								--></TR>
							</logic:iterate>							 																
							<TR>
								<TD align="center" valign="baseline" colspan="10">								  
									<DIV align="center">
									<A href="#" onclick="window.history.go(-1); return false;" > 
											<IMG src="images/Back.gif" alt="Back" width="49" height="37" border="0">
										</A>
										<!--<A href="#" onclick="Submitclaimrecoverydetail()">
											<IMG src="images/Save.gif" alt="Save" width="49" height="37" border="0">
										</A>																	
									--></DIV>
								</TD>
							</TR>
						</TABLE>
					</TD>
					<TD width="20" background="images/TableVerticalRightBG.gif">&nbsp;</TD>
				</TR>
				<TR>
					<TD width="20" align="right" valign="top">
						<IMG src="images/TableLeftBottom1.gif" width="20" height="15">
					</TD>
					<TD background="images/TableBackground2.gif">&nbsp;</TD>
					<TD width="20" align="left" valign="top">
						<IMG src="images/TableRightBottom1.gif" width="23" height="15">
					</TD>
				</TR>
			</html:form>
		</TABLE>
	</body>
</html>