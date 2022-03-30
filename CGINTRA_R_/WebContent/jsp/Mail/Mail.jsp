<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<% session.setAttribute("CurrentPage","showSendMail.do?method=showSendMail");%>
<%@ include file="/jsp/SetMenuInfo.jsp" %>
<%! String focusProperty = ""; %>
<%! String flag1 = "";%>

<%
// String focusProperty = null;
String mainMenu = (String)session.getAttribute("mainMenu");
String subMenu = (String)session.getAttribute("subMenuItem");
com.cgtsi.admin.Message message=(com.cgtsi.admin.Message)session.getAttribute("adminMail");
String flag = "SEND_FLAG";
if(message != null)
{          
     flag = "REPLY_FLAG";
}
%>
<HTML>
		
<html:errors />
<%
if(flag.equals("SEND_FLAG"))
{	   
	flag1 = (String)session.getAttribute("MemberSelected");
	if((flag1 != null) && (flag1.equals("Y")))
	{
	    focusProperty = "userId";
	}
}
if(flag.equals("REPLY_FLAG"))
{
    focusProperty = "message";
}
%>

<html:form action="Mail.do?method=Mail" method="POST" focus="<%=focusProperty%>">
<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
	<TR> 
		<TD width="20" align="right" valign="bottom"><IMG src="images/TableLeftTop.gif" width="20" height="31"></TD>
		<TD background="images/TableBackground1.gif"></TD>
		<TD width="20" align="left" valign="bottom"><IMG src="images/TableRightTop.gif" width="23" height="31"></TD>
	</TR>
	<TR>
		<TD width="20" background="images/TableVerticalLeftBG.gif">&nbsp;</TD>
		<TD>
		<DIV align="right">			
	<A HREF="javascript:submitForm('helpSendMail.do?method=helpSendMail')">
    HELP</A>
</DIV>
			<TABLE width="100%" border="0" align="left" cellpadding="0" cellspacing="0">
			<TD  align="left" colspan="4"><font size="2"><bold>
	Fields marked as </font><font color="#FF0000" size="2">*</font><font size="2"> are mandatory</bold></font>
	</td>
				<TR>
					<TD>
						<TABLE width="100%" border="0" cellspacing="1" cellpadding="1">
							<TR>
								<TD colspan="4"> 
									<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
										<TR>
											<TD width="31%" class="Heading"><bean:message key="sendMailHeader" /></TD>
											<TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
										</TR>
										<TR>
											<TD colspan="3" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
										</TR>

									</TABLE>
								</TD>
							</TR>

							<TR align="left">
									<TD align="left" valign="top" class="ColumnBackground">
										&nbsp;<font color="#FF0000" size="2">*</font>&nbsp;&nbsp;&nbsp;TO
									</TD>
									<TD align="left" class="TableData"> 
										<html:text property="bankName" size="50" alt="Bank name" maxlength="100"/>
									</TD>
								</TR>
								
								<TR align="left">
									<TD align="left" valign="top" class="ColumnBackground">
										&nbsp;<font color="#FF0000" size="2">*</font>&nbsp;&nbsp;&nbsp;CC
									</TD>
									<TD align="left" class="TableData"> 
										<html:text property="bankName" size="50" alt="Bank name" maxlength="100"/>
									</TD>
								</TR>						

							 	<TR align="left">
									<TD align="left" valign="top" class="ColumnBackground">
										&nbsp;<font color="#FF0000" size="2">*</font>&nbsp;&nbsp;&nbsp;Subject
									</TD>
									<TD align="left" class="TableData"> 
										<html:text property="bankName" size="100" alt="Bank name" maxlength="100"/>
									</TD>
								</TR>
							 <TR align="left">
									<TD align="left" valign="top" class="ColumnBackground">
										&nbsp;&nbsp;&nbsp;&nbsp;
									</TD>
									<TD align="left" class="TableData"> <b>Attachment</b>
										<html:text property="bankName" size="50" alt="Bank name" maxlength="100"/>&nbsp;&nbsp;<b>Upload File</b> &nbsp;&nbsp;&nbsp;<button> <i>Upload</i></button>
									</TD>
								</TR>
								<TR align="left">
									<TD align="left" valign="top" class="ColumnBackground">
										&nbsp;<font color="#FF0000" size="2">*</font>&nbsp;&nbsp;&nbsp;Message
									</TD>
									<TD align="left" class="TableData"> 
										<html:textarea property="message" cols="100" rows="10" alt="Message" name="adminForm" />
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
				<%
				     if((flag1 != null) && (flag1.equals("Y")))
				     {
					 session.setAttribute("MemberSelected","N");
				     }
				%> 				
				<TR >
					<TD align="center" valign="baseline" >
						<DIV align="center">
							<A href="javascript:submitForm('sendMail.do?method=sendMail')"><IMG src="images/Submit.gif" alt="Save" width="49" height="37" border="0"></A>
							
							<A href="javascript:history.back()">
								<IMG src="images/Reset.gif" alt="Cancel" width="49" height="37" border="0"></A>			
	
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
	
</HTML>		






