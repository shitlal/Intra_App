<%@ page language="java"%>
<%@ page import="com.cgtsi.actionform.AdministrationActionForm"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic" %>
<%@ include file="/jsp/SetMenuInfo.jsp" %>

<%
	session.setAttribute("CurrentPage", "processQueryStatus.do?method=processQueryStatus");
%>


<script>


function SubmitNpaApprForm(actionType)
{
	//alert("SubmitNpaApprForm S : "+actionType);	
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
		alert("inside if");
		document.gmPeriodicInfoForm.action="showApprRegistrationFormSubmit.do?method=showApprRegistrationFormSubmit&action="+actionType;
		document.gmPeriodicInfoForm.target="_self";
		document.gmPeriodicInfoForm.method="POST";
		document.gmPeriodicInfoForm.submit();
	}
	//alert("SubmitNpaApprForm E");
}



function approvedQueryRequest(action)
{
	//alert("fdfdfd");
	
	//alert("kkkkk26"+document.forms[0].qryRemarks[0].value);	

	var checkList1 = document.getElementsByName('qryRemarks');
	//alert("fdfdfd11");
	//alert(checkList1.length);

	//alert("fdfdfd22");

	var check = false;
	var comment = false;

	for(var j=0; j<checkList1.length; j++)
	{
		//alert("fdfdfd2");
		//alert("checkList1[0].value"+checkList1[0].value);
		//alert("checkList1[1].value"+checkList1[1].value);
		//alert("checkList1[2].value"+checkList1[2].value);
	//	alert("checkList1[j].value"+checkList1[j].value);
		if(checkList1[j].checked)
		{
		//	alert("document.getElementById(checkList1[j].value)"+document.getElementById(checkList1[j].value));
		//	alert("document.getElementById(checkList1[j].value11)"+document.getElementById(checkList1[j].value));
		//	alert("document.getElementById(checkList1[j].value22)"+document.getElementById(checkList1[j].value).value);
			check = true;
			if(document.getElementById(checkList1[j].value) != null)
			{
				//alert("fdfdfd3");
				if(document.getElementById(checkList1[j].value).value == "")
				{
					//alert("fdfdfd4");
					alert("Please provide comments to request.");
					comment = true;
					break;					
				}				
			}
		}			
	}

	if(check==true && comment==false)
	{		
		//alert("inside if"+action);
		document.forms[0].action=action;
		document.forms[0].target="_self";
		document.forms[0].method="POST";
		document.forms[0].submit();
		//alert("inside if3"+action);
	}//alert("gfgfgfgf");

//alert("gfgfgfgfend");
}

</script>



<html>
	<head>		
		<title></title>
		<script type="text/javascript">
			
		</script>
	</head>
	<body>
		<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
			<html:errors />
			<html:form  action="processQueryStatus.do?method=processQueryStatus" method="POST" enctype="multipart/form-data">
				<TR> 
					<TD width="20" align="right" valign="bottom"><IMG src="images/TableLeftTop.gif" width="20" height="31"></TD>
					<TD background="images/TableBackground1.gif"><IMG src="images/SystemAdministrationHeading.gif"></TD>
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
								<TD align="left" valign="top" class="ColumnBackground" width="50">
									QUERY/COMPLAINT ID
								</TD>
								<TD align="left" valign="top" class="ColumnBackground" width="98">
									BANK ID
								</TD>
								<TD align="left" valign="top" class="ColumnBackground" width="98">
									ZONE ID
								</TD>						
								<TD align="left" valign="top" class="ColumnBackground" width="98">
									BRANCH ID
								</TD>
								<TD align="left" valign="top" class="ColumnBackground" width="98">
									PHONE NO
								</TD>
								<TD align="left" valign="top" class="ColumnBackground" width="98">
									EMAIL ID
								</TD>
								<TD align="left" valign="top" class="ColumnBackground" width="98">
									QUERY DESCRIPTION
								</TD>						
								<TD align="left" valign="top" class="ColumnBackground" width="114">
									CONTACT PERSON
								</TD>
								
								<TD align="left" valign="top" class="ColumnBackground" width="114">
									QUERY DATE
								</TD>
								<TD align="left" valign="top" class="ColumnBackground" width="114">
									QUERY  DECISION
								</TD>
								<TD align="left" valign="top" class="ColumnBackground" width="114">
									QUERY RESPONSE
								</TD>
							</TR>
													
							<logic:iterate id="object" name="adminActionForm" property="mliQueryList" indexId="index">
								<%
									AdministrationActionForm QueryFrom = (AdministrationActionForm)object;
									String queryRespon = "";
								
								%>
								<TR>
														
									
									
									
									<TD align="left" valign="top" class="ColumnBackground" width="50"><%=Integer.parseInt(index+"")+1%></TD>
									<TD align="left" valign="top" class="ColumnBackground" width="98"><%=QueryFrom.getQueryId()%></TD>
									<TD align="left" valign="top" class="ColumnBackground" width="98"><%=QueryFrom.getBankId()%></TD>
									<TD align="left" valign="top" class="ColumnBackground" width="98"><%=QueryFrom.getZoneId()%></TD>
									<TD align="left" valign="top" class="ColumnBackground" width="98"><%=QueryFrom.getBranchId()%></TD>
									<TD align="left" valign="top" class="ColumnBackground" width="98"><%=QueryFrom.getPhoneNo()%></TD>
									<TD align="left" valign="top" class="ColumnBackground" width="98"><%=QueryFrom.getEmailId()%></TD>
									<TD align="left" valign="top" class="ColumnBackground" width="114"><%=QueryFrom.getQueryDescription()%></TD>
									<TD align="left" valign="top" class="ColumnBackground" width="98"><%=QueryFrom.getContPerson()%></TD>						
									<TD align="left" valign="top" class="ColumnBackground" width="114"><%=QueryFrom.getQueryRaisDt()%></TD>
									
									
								<TD align="left" valign="top" class="ColumnBackground" width="114">&nbsp;
								<%
									
								%>
								<html:checkbox property="qryRemarks" name="adminActionForm" value="<%=QueryFrom.getQueryId()%>" />
							</TD>
							<TD align="left" valign="top" class="ColumnBackground" width="114">
								<%
								queryRespon="queryRespon("+QueryFrom.getQueryId()+")";
									//System.out.println("JAP commentCgpan : "+commentCgpan);
								%>
								<html:textarea property="<%=queryRespon%>" name="adminActionForm"  styleId="<%=QueryFrom.getQueryId()%>"/>
<!--								<html:textarea property="<%=queryRespon%>" name="adminActionForm" />-->
							</TD>	
								
								
								</TR>
							</logic:iterate>																
							<TR>
								<TD align="center" valign="baseline" colspan="10">
									<DIV align="center">
									
									<A href="javascript:approvedQueryRequest('processQueryStatus.do?method=processQueryStatus')"><img src="images/Save.gif" alt="Accept" width="49" height="37" border="0"></a>
										
										
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