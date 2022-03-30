<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page language="java"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Date"%>
<%@ page import="com.cgtsi.actionform.GMActionForm"%>
<%@ page import="com.cgtsi.action.GMActionNew" %>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic" %>
<%@ include file="/jsp/SetMenuInfo.jsp" %>



<%
	session.setAttribute("CurrentPage","afterNpaUpdationApproval.do?method=afterNpaUpdationApproval");	
%>
<% 

	String formName = (String)session.getAttribute("FORMNAME");

	String sdanDate;
	SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
	String name = "";
%>

<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form  action="afterNpaUpdationApprovalSubmit.do?method=afterNpaUpdationApprovalSubmit" method="POST" enctype="multipart/form-data">
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
						<TD colspan="11"> 
							<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
								<TR>
									<TD width="40%" class="Heading">Approve Request for NPA Upgradation by MLI </TD>
									<TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
								</TR>
								<TR>
									<TD colspan="8" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
								</TR>
							</TABLE>
						</TD>
					</TR>
					<tr><td colspan="8"><font color="Green" size="2">  </font></td></tr>
					<TR>
						<TD align="left" valign="top" class="ColumnBackground" width="98">
							<bean:message key="sNo" />
						</TD>
						<TD align="left" valign="top" class="ColumnBackground" width="98">
							<bean:message key="bankNames" />
						</TD>						
						<TD align="left" valign="top" class="ColumnBackground" width="98">
							<bean:message key="cpmliid" />
						</TD>
						<TD align="left" valign="top" class="ColumnBackground" width="98">
							<bean:message key="cgpan" />
						</TD>
						<TD align="left" valign="top" class="ColumnBackground" width="98">
							<bean:message key="unitNameExisting" />
						</TD>
						<TD align="left" valign="top" class="ColumnBackground" width="114">
							Last NPA Reported Date <!-- NPA_EFFECTIVE_DT -->
						</TD>
						<TD align="left" valign="top" class="ColumnBackground" width="83">
							Reason For Marking NPA <!-- NPA_REASONS_TURNING_NPA -->
						</TD>
						<%
							if(formName.equals("modification"))
							{
						%>
								<TD align="left" valign="top" class="ColumnBackground" width="114">
									New NPA Effective Date <!-- NEW_NPA_EFFECTIVE_DT -->
								</TD>
						<%
							}
						%>						
						
						<TD align="left" valign="top" class="ColumnBackground" width="114">
							NPA Up-gradation Date <!-- NPA_UPGRADE_DT -->
						</TD>												
						<TD align="left" valign="top" class="ColumnBackground" width="114">
							MLI Remarks <!-- NUD_USER_REMARKS -->
						</TD>
						<TD align="left" valign="top" class="ColumnBackground" width="114">
							Approve/Return<br/>&nbsp;
							<html:checkbox property="selectAll" alt="cg" name="gmApproveForm" onclick="selectDeselect(this,1)"/>
													
						</TD>
						<TD align="left" valign="top" class="ColumnBackground" width="114">
							Comments				
						</TD>						
					</TR>
					<%
						int i = 0;
						String checkboxKey = "";
						String commentCgpan = "";
						String cgpan = "";
						String npaCgpan = "";
					%>
					<html:hidden property="cgpan" name="gmApproveForm"/>
					<logic:iterate id="object" name="gmApproveForm" property="npaApprovalList" indexId="index">
						<%
							GMActionForm npaData = (GMActionForm)object;
							npaCgpan = npaData.getCgpan();
						%>
						<TR>
							<td align="left" valign="top" class="ColumnBackground" width="98">
								<div align="center">&nbsp;
									<%=Integer.parseInt(index+"")+1%>
								</div>
							</td>
							<TD align="left" valign="top" class="ColumnBackground" width="98">
								<%=npaData.getBankName()%>
							</TD>			
							<TD align="left" valign="top" class="ColumnBackground" width="98">
								<%=npaData.getMemberId()%>
							</TD>
							<TD align="left" valign="top" class="ColumnBackground" width="98">
								<%checkboxKey="closureCgpan("+cgpan+")";%>
								<%--commentCgpan="commentCgpan("+cgpan+")";--%>
								<%=npaCgpan%>								
							</TD>
							<TD align="left" valign="top" class="ColumnBackground" width="83">
								<%=npaData.getUnitName()%>
							</TD>
							<TD align="left" valign="top" class="ColumnBackground" width="114">&nbsp;
								<%=npaData.getLstNpaDt()%>
							</TD>
							<TD align="left" valign="top" class="ColumnBackground" width="114">&nbsp;
								<%=npaData.getReasonForNpa()%>
							</TD> 
							<%
								if(formName.equals("modification"))
								{
							%>
									<TD align="left" valign="top" class="ColumnBackground" width="114">&nbsp;
										<%=npaData.getNewNpaDt()%>
									</TD> 
							<%
								}
							%>       
							<TD align="left" valign="top" class="ColumnBackground" width="114">&nbsp;
								<%=npaData.getUpGradationDt()%>
							</TD>                                           																			
							<TD align="left" valign="top" class="ColumnBackground" width="114">&nbsp;
								<%=npaData.getMliRemarks()%>
							</TD>
							<TD align="left" valign="top" class="ColumnBackground" width="114">&nbsp;
								<%
									checkboxKey="closureCgpan("+npaCgpan+")";
								%>								
								<html:checkbox name="gmApproveForm" property="textarea" value="<%=npaCgpan%>" styleId="CHE"/>								
							</TD>
							<TD align="left" valign="top" class="ColumnBackground" width="114">&nbsp;
								<%
									commentCgpan="commentCgpan("+npaCgpan+")";
									System.out.println("JAP commentCgpan : "+commentCgpan);
								%>								
								<html:textarea name="gmApproveForm" property="<%=commentCgpan%>" styleId="<%=npaCgpan%>" />													
							</TD>
						</TR>
					</logic:iterate>	
					<tr><td><html:hidden name="gmApproveForm" property="actionType" /></td></tr>
					<TR>
						<TD align="center" valign="baseline" colspan="10">
							<DIV align="center">
								<%
									String cheTest = "TestCheck";
									String submitUrl = "afterNpaUpdationApprovalSubmit.do?method=afterNpaUpdationApprovalSubmit&FormType="+formName;
									session.setAttribute("FormType",formName);
									//System.out.println("JSP FVV url : "+submitUrl);
								%>
								<%-- <A href="javascript:submitForm('<%=submitUrl%>')"> --%>
								 <A href="#" onclick="TestCheck('update')">			
									<IMG src="images/Save.gif" alt="Save" width="49" height="37" border="0">
								</A>
								<%
									String deleteUrl = "afterNpaUpdationApprovalDelete.do?method=afterNpaUpdationApprovalDelete&FormType="+formName;
								%>							
								<%-- <A href="javascript:submitForm('<%=deleteUrl%>')"> --%>
								<A href="#" onclick="TestCheck('delete')">		
									<IMG src="images/Delete.gif" alt="Delete" width="49" height="37" border="0">
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

<script type="text/javascript">

	function TestCheck(actionType1)
	{
		//alert("cgpan check");
		
		document.gmApproveForm.action="afterNpaUpdationApprovalSubmit.do?method=afterNpaUpdationApprovalSubmit&actionType="+actionType1;
		//document.getElementById('actionType').value='delete';
		var checks = document.getElementsByName('textarea');
		//alert("cgpan check 1"+checks.length);
		var checkD =  false;
		var checkD1 =  false;
		//var checksss = document.getElementsById("COM");
		//alert('hi'+checks.length);
		for(var i=0; i<checks.length; i++)
		{
			
			if(checks[i].checked)
			{
				checkD1=true;
			//	alert("cgpan check : "+checks[i].value);
				//alert('HI'+document.getElementById(checks[i].value));
				if(document.getElementById(checks[i].value) != null) 
				{
					if(document.getElementById(checks[i].value).value=='')
					{
						alert('comment is required');
					//	inlineMsg(checks[i].value,'You must enter comment.',2);
						checkD=true;
					}
					
				}
			}
			else
			{
				//alert("cgpan not check : "+checks[i]);
			}
		}
		if(checkD==false && checkD1==true)
		{ 
			//alert('submit called');
			document.gmApproveForm.submit();
		}
	}
</script>