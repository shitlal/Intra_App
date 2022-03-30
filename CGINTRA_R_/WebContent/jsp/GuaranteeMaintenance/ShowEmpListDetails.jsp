<%@ page language="java"%>
<%@ page import="com.cgtsi.actionform.GMActionForm"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic" %>
<%@ include file="/jsp/SetMenuInfo.jsp" %>

<%
	session.setAttribute("CurrentPage", "showEmpDetails.do?method=showEmpDetails");
%>
<html>
	<head>		
		<title></title>
		<script type="text/javascript">
			function SubmitNpaApprForm(actionType)
			{
				alert("SubmitNpaApprForm S : "+actionType);	
				var checkList1 = document.getElementsByName('check');
				//alert("checkList name : "+checkList1.length);
				var check = false;
				var comment = false;
			
				for(var j=0; j<checkList1.length; j++)
				{
					if(checkList1[j].checked)
					{
						check = true;
						if(document.getElementById(checkList1[j].value) != null)
						{
							if(document.getElementById(checkList1[j].value).value == "")
							{
								alert("Emp Comments Is Required.");
								comment = true;
								break;					
							}				
						}
					}			
				}
				//alert("check val : "+check+ "\t comment : "+comment);
				if(check==true && comment==false)
				{		
					//alert("inside if");
					document.gmPeriodicInfoForm.action="showApprRegistrationFormSubmit.do?method=showApprRegistrationFormSubmit&action="+actionType;
					document.gmPeriodicInfoForm.target="_self";
					document.gmPeriodicInfoForm.method="POST";
					document.gmPeriodicInfoForm.submit();
				}
				//alert("SubmitNpaApprForm E");
			}
		</script>
	</head>
	<body>
		<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
			<html:errors />
			<html:form  action="showApprRegistrationFormSubmit.do?method=showApprRegistrationFormSubmit" method="POST" enctype="multipart/form-data">
				<TR> 
					<TD width="20" align="right" valign="bottom"><IMG src="images/TableLeftTop.gif" width="20" height="31"></TD>
					<TD background="images/TableBackground1.gif"><IMG src="images/GuaranteeMaintenanceHeading.gif"></TD>
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
											<TD width="35%" class="Heading">Submission Request for NPA Registration Form</TD>
											<TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
										</TR>
										<TR><TD colspan="12" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD></TR>
									</TABLE>
								</TD>
							</TR>
							<tr><td colspan="8"><font color="Green" size="2">  </font></td></tr>
							<TR>
								<TD align="left" valign="top" class="ColumnBackground" width="50">
									<bean:message key="sNo" />
								</TD>
								<TD align="left" valign="top" class="ColumnBackground" width="98">
									<bean:message key="cpmliid" />
								</TD>
								<TD align="left" valign="top" class="ColumnBackground" width="98">
									<bean:message key="zoneName" />
								</TD>						
								<TD align="left" valign="top" class="ColumnBackground" width="98">
									<bean:message key="empFName" />
								</TD>
								<TD align="left" valign="top" class="ColumnBackground" width="98">
									<bean:message key="empMName" />
								</TD>
								<TD align="left" valign="top" class="ColumnBackground" width="98">
									<bean:message key="empLName" />
								</TD>
								<TD align="left" valign="top" class="ColumnBackground" width="98">
									<bean:message key="empId" />
								</TD>						
								<TD align="left" valign="top" class="ColumnBackground" width="114">
									<bean:message key="designation"/>
								</TD>
								<TD align="left" valign="top" class="ColumnBackground" width="114">
									<bean:message key="phoneNo"/>
								</TD>
								<TD align="left" valign="top" class="ColumnBackground" width="114">
									<bean:message key="emailId"/>
								</TD>																													
								<TD align="left" valign="top" class="ColumnBackground" width="114">
									Approve/Return<br/>&nbsp;&nbsp;
									<html:checkbox property="selectAll" name="gmPeriodicInfoForm" onclick="selectDeselect(this,1)"/>				
								</TD>
								<TD align="left" valign="top" class="ColumnBackground" width="114">
									Emp Comments
								</TD>
							</TR>
							<html:hidden name="gmPeriodicInfoForm" property="checkerId"/>							
							<logic:iterate id="object" name="gmPeriodicInfoForm" property="npaRegistFormList" indexId="index">
								<%
									GMActionForm npaRegistForm = (GMActionForm)object;
									String empComments = "";
									String checkerId = npaRegistForm.getCheckerId();
									//System.out.println("JSP checkerId : "+checkerId);
								%>
								<TR>
									<TD align="left" valign="top" class="ColumnBackground" width="50"><%=Integer.parseInt(index+"")+1%></TD>
									<TD align="left" valign="top" class="ColumnBackground" width="98"><%=npaRegistForm.getMemberId()%></TD>
									<TD align="left" valign="top" class="ColumnBackground" width="98"><%=npaRegistForm.getZoneName()%></TD>
									<TD align="left" valign="top" class="ColumnBackground" width="98"><%=npaRegistForm.getEmpFName()%></TD>
									<TD align="left" valign="top" class="ColumnBackground" width="98"><%=npaRegistForm.getEmpMName()%></TD>
									<TD align="left" valign="top" class="ColumnBackground" width="98"><%=npaRegistForm.getEmpLName()%></TD>
									<TD align="left" valign="top" class="ColumnBackground" width="98"><%=npaRegistForm.getEmpId()%></TD>
									<TD align="left" valign="top" class="ColumnBackground" width="114"><%=npaRegistForm.getDesignation()%></TD>
									<TD align="left" valign="top" class="ColumnBackground" width="114"><%=npaRegistForm.getPhoneNo()%></TD>
									<TD align="left" valign="top" class="ColumnBackground" width="114"><%=npaRegistForm.getEmailId()%></TD>
									<TD align="left" valign="top" class="ColumnBackground" width="114">&nbsp;&nbsp;
										<html:checkbox property="check" value="<%=checkerId%>" name="gmPeriodicInfoForm"/>
									</TD>
									<TD align="left" valign="top" class="ColumnBackground" width="114">
										<%
											empComments = "empComments("+checkerId+")";
											//System.out.println("JSP empComments : "+empComments);
										%>
										<html:textarea property="<%=empComments%>" name="gmPeriodicInfoForm" styleId="<%=checkerId%>"/>
									</TD>
								</TR>
							</logic:iterate>																
							<TR>
								<TD align="center" valign="baseline" colspan="10">
									<DIV align="center">
										<A href="#" onclick="SubmitNpaApprForm('update')">
											<IMG src="images/Save.gif" alt="Save" width="49" height="37" border="0">
										</A>
										<A href="#" onclick="SubmitNpaApprForm('delete')">
											<IMG src="images/Delete.gif" alt="Delete" width="40" height="30" border="0">
										</A>
										<A href="home.do?method=getMainMenu&menuIcon=<%=session.getAttribute("menuIcon")%>">
											<IMG src="images/Cancel.gif" alt="Cancel" width="49" height="37" border="0">
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