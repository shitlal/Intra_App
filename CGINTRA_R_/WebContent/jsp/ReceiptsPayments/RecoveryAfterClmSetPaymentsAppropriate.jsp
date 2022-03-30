<%@ page language="java"%>
<%@ page import="com.cgtsi.actionform.RPActionForm"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
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
		<title>DKR</title>		
		<script type="text/javascript">
		
		function Submitclaimrecoverydetail(){
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
             alert("Please click on atleast one check box.");
			}
			else
			{		
				document.rpAllocationForm.action="submitClmRecovryAppropriation.do?method=submitClmRecovryAppropriation";
				document.rpAllocationForm.target="_self";
				document.rpAllocationForm.method="POST";
				document.rpAllocationForm.submit();
			}			
		}
		</script>
	</head>
	<body >
	 
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
								<TD colspan="13"> 
									<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
										<TR>
											<TD width="35%" class="Heading">Appropriation Form Detail:</TD>
											<TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
										</TR>
										<TR><TD colspan="12" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD></TR>
									</TABLE>
								</TD>
							</TR>
							<tr><td colspan="9"><font color="Green" size="3">  </font></td></tr>
							<TR>
								<TD align="left" valign="top" class="ColumnBackground" width="50">
									<b><bean:message key="sNo" /></b>
								</TD>
								<!--<TD align="left" valign="top" class="ColumnBackground" width="98">
									<b>MLI</b>
								</TD>
								--><TD align="left" valign="top" class="ColumnBackground" width="98">
									<b><bean:message key="cpmliid" /></b>
								</TD>
								<TD align="left" valign="top" class="ColumnBackground"  width="250">
									<b>PAY ID<br>(Payment Number)</b>
								</TD>	
								<TD align="left" valign="top" class="ColumnBackground" width="98">
									<b>Allocated Amount</b>
								</TD>
								<TD align="left" valign="top" class="ColumnBackground" width="98">
									<b>Instument Number</b>
								</TD>					
								<TD align="left" valign="top" class="ColumnBackground" width="98">
									<b>Instument Amount</b>
								</TD>
								<TD align="left" valign="top" class="ColumnBackground" width="98">
									<b>Instument Date</b>
								</TD>
								<TD align="left" valign="top" class="ColumnBackground" width="98">
									<b>Received Amount</b>
								</TD>
								<TD align="left" valign="top" class="ColumnBackground" width="98">
									<b>Realization Date</b> 
								</TD>						
								<TD align="left" valign="top" class="ColumnBackground" width="114">
									<b>Short/Excess Amount</b>
								</TD>
								<TD align="left" valign="top" class="ColumnBackground" width="114">
									<b>Remarks</b>
								</TD>
								
								<TD align="left" valign="top" class="ColumnBackground" width="114">
								<b>Approriate All Checkbox</b><center><html:checkbox property="selectAll" name="rpAllocationForm" onclick="selectDeselect(this,1)"/></center>
								</TD>
							</TR>
							<html:hidden name="rpAllocationForm" property="checkerId"/>							
							<logic:iterate id="object" name="rpAllocationForm" property="recvryPaymentList" indexId="index">
								<%
								RPActionForm recovryform = null;
								String instDate="";
								String checkerId1="";
								String remarkwithId=null;
								String dateOfRealisation="";
								try
								{
								 recovryform = (RPActionForm)object;
									String empComments = "";
								    checkerId1 =String.valueOf(recovryform.getCheckerId()) ;
									remarkwithId =String.valueOf(recovryform.getRemarks()) ;
									instDate=dateFormat.format(recovryform.getInstrumentDate());
									
									dateOfRealisation=dateFormat.format(recovryform.getDateOfRealisation());
								}
								catch(Exception  e)
								{
									e.printStackTrace();
								}
									//allocatede amount = recovryform.getAmount(); 
								%>
								<TR>
									<TD align="left" valign="top" class="tableData" width="50"><%=Integer.parseInt(index+"")+1%></TD>
									<!--<TD align="left" valign="top" class="ColumnBackground" width="98"><%=recovryform.getBankName()%></TD>
									--><TD align="left" valign="top" class="tableData" width="98"><%=recovryform.getMemberId()%></TD>
									<TD align="left" valign="top" class="tableData" width="250">
									  <a href="showRecoveryPaymentDetailsforRPNo.do?method=showRecoveryPaymentDetailsforRPNo&payID=<%=recovryform.getPayId()%>"><%=recovryform.getPayId()%></a>
									</TD>
									<TD align="left" valign="top" class="tableData" width="98"><%=recovryform.getInstrumentAmount()%></TD>
									<TD align="left" valign="top" class="tableData" width="98"><%=recovryform.getInstrumentNo()%></TD>
									<TD align="left" valign="top" class="tableData" width="98"><%=recovryform.getInstrumentAmount()%></TD>
									<TD align="left" valign="top" class="tableData" width="98"><%=instDate%></TD>
									<TD align="left" valign="top" class="tableData" width="98"><%=recovryform.getInstrumentAmount()%></TD>
									<TD align="left" valign="top" class="tableData" width="98"><%=dateOfRealisation %></TD>
									<TD align="left" valign="top" class="tableData" width="98"><%=recovryform.getReceivedAmount()%></TD>
									<TD align="left" valign="top" class="v" width="98">
									<%-- <html:textarea cols="10" rows="3" property="remark" value="<%=remarkwithId%>" name="rpAllocationForm"  styleId="<%=remarkwithId%>" styleClass="txtAreaCls"/> --%>
									<html:textarea cols="10" rows="3" property="remark" name="rpAllocationForm"  styleId="<%=remarkwithId %>" styleClass="txtAreaCls"/>
									</TD>		
									 <TD align="left" valign="top" class="tableData" width="114">&nbsp;&nbsp;
										<html:checkbox property="check" value="<%=checkerId1%>" name="rpAllocationForm"/>
									</TD>									
								</TR>
							</logic:iterate>
							 																
							<TR>
								<TD align="center" valign="baseline" colspan="10">								  
									<DIV align="center">
									<A href="#" onclick="window.history.go(-1); return false;" > 
											<IMG src="images/Back.gif" alt="Back" width="49" height="37" border="0">
										</A>
										<A href="#" onclick="Submitclaimrecoverydetail();">
											<IMG src="images/Save.gif" alt="Save" width="49" height="37" border="0">
										</A>
										<A href="#" onclick="window.location.reload();"><IMG src="images/Reset.gif" alt="Reset" width="49" height="37" border="0">
										</A>							
									</DIV>
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