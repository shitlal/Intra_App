<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<% session.setAttribute("CurrentPage","mliQueryBifurcation.do?method=mliQueryBifurcation");%>
<%@ include file="/jsp/SetMenuInfo.jsp" %>

<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
<html:errors />

<script>

function ShowQueryRequest(action)
{
	//alert("inside if35"+action);


	if(document.forms[0].departments.value=="" || document.forms[0].departments.value==null )
	{
		alert("please enter departments  code ");

		//document.getElementById('departments').focus();
	
	}
	else
	{
	
	//var department = document.getElementsByName('departments').value;

		
		//alert("inside if"+department);
		document.forms[0].action=action;
		document.forms[0].target="_self";
		document.forms[0].method="POST";
		document.forms[0].submit();
		//alert("inside if3"+action);
	}
	//alert("gfgfgfgf");

//alert("gfgfgfgfend");
}

</script>



<html:form action="showChangeHintQA.do" method="POST" focus="newQuestion">
<TABLE width="680" border="0" cellspacing="0" cellpadding="0" align="center">
<TR> 
			<TD width="20" align="right" valign="bottom"><IMG src="images/TableLeftTop.gif" width="20" height="31"></TD>
			<TD background="images/TableBackground1.gif"></TD>
			<TD width="20" align="left" valign="bottom"><IMG src="images/TableRightTop.gif" width="23" height="31"></TD>
		</TR>
		<TR>
			<TD width="20" background="images/TableVerticalLeftBG.gif">&nbsp;</TD>
			<TD width="713">
			<DIV align="right">			
				<A HREF="javascript:submitForm('helpChangeHintQA.do?method=helpChangeHintQA')">
			    HELP</A>
			</DIV>
			<TABLE width="606" border="0" cellspacing="1" cellpadding="0">
			<TD  align="left" colspan="4"><font size="2"><bold>
				Fields marked as </font><font color="#FF0000" size="2">*</font><font size="2"> are mandatory</bold></font>
				</td>
          <TR> 
            <TD colspan="2" width="700"><TABLE width="604" border="0" cellspacing="0" cellpadding="0">
												<TD width="40%" class="Heading">GET INBOX</TD>
												<TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
											</TR>
											<TR>
												<TD colspan="3" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
											</TR>

										</TABLE>
									</TD> 

								</TR>

          <TR>
		    <TD align="left" valign="top" class="ColumnBackground" >
										&nbsp;<font color="#FF0000" size="2">*</font>Enter Department code
			</TD>
			<TD align="left" valign="top" class="TableData" >										
			<html:text property="departments" size="20"  alt="Reference"  name="adminActionForm" maxlength="200"/>
			</TD>            
          </TR>

		   
  
           
          </TR>
            
							<TR align="center" valign="baseline" >
						<TD colspan="2" width="700"><DIV align="center">
						
						
						<A href="javascript:ShowQueryRequest('mliQueryOrComplaints.do?method=mliQueryOrComplaints')"><img src="images/Save.gif" alt="Accept" width="49" height="37" border="0"></a>
						
								<A href="javascript:document.adminForm.reset()">
									<IMG src="images/Reset.gif" alt="Cancel" width="49" height="37" border="0"></A>
								
						<A href="subHome.do?method=getSubMenu&menuIcon=<%=session.getAttribute("menuIcon")%>&mainMenu=<%=session.getAttribute("mainMenu")%>"><IMG src="images/Cancel.gif" alt="Cancel" width="49" height="37" border="0"></A>	
							</DIV>
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

