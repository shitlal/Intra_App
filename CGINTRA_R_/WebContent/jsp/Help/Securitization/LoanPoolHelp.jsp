<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<% session.setAttribute("CurrentPage","showInvestor.do");%>
<%@ include file="/jsp/SetMenuInfo.jsp" %>


<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
<html:errors />

<form>
<table width="680" border="0" cellspacing="0" cellpadding="0" align="center">
<TR> 
			<TD width="20" align="right" valign="bottom"><IMG src="images/TableLeftTop.gif" width="20" height="31"></TD>
			<TD background="images/TableBackground1.gif"></TD>
			<TD width="20" align="left" valign="bottom"><IMG src="images/TableRightTop.gif" width="23" height="31"></TD>
		</TR>
		<TR>
			<TD width="20" background="images/TableVerticalLeftBG.gif">&nbsp;</TD>
			<td width="713"><table width="606" border="0" cellspacing="1" cellpadding="0">
          <tr> 
            <td colspan="2" width="700"><table width="604" border="0" cellspacing="0" cellpadding="0">
												<TD width="31%" class="Heading"><bean:message key="addInvestor"/></TD>
												<TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
											</TR>
											<TR>
												<TD colspan="3" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
											</TR>

										</TABLE>
									</TD> 

								</TR>   
          </tr>
		  <tr>
		  <TD width="20%" align="left" valign="top" class="ColumnBackground">
										<DIV align="left">
											&nbsp;MLI Name
										</DIV>
									</TD>
           <TD width="27%" align="left" valign="top" class="TableData">&nbsp;
						Select one or more MLI Name(s) from the multiselect box.		
		  </TD>
          </tr>
          <tr>
		  <TD width="20%" align="left" valign="top" class="ColumnBackground">
										<DIV align="left">
											&nbsp;Loan Tenure
										</DIV>
									</TD>
           <TD width="27%" align="left" valign="top" class="TableData">&nbsp;
			Enter the tenure of the loan in months.
			</TD>
          </tr>
          <tr>
		  <TD width="20%" align="left" valign="top" class="ColumnBackground">
										<DIV align="left">
											&nbsp;Effective Date From
										</DIV>
									</TD>
           <TD width="27%" align="left" valign="top" class="TableData">&nbsp;
			Enter the date from which it is valid or effective.
			</TD>
          </tr>
          <tr>
		  <TD width="20%" align="left" valign="top" class="ColumnBackground">
										<DIV align="left">
											&nbsp;Loan Type
										</DIV>
									</TD>
           <TD width="27%" align="left" valign="top" class="TableData">&nbsp;
			Select the type of Loan by checking any of the radio buttons.
			</TD>
          </tr>
          <tr>
		  <TD width="20%" align="left" valign="top" class="ColumnBackground">
										<DIV align="left">
											&nbsp;State
										</DIV>
									</TD>
           <TD width="27%" align="left" valign="top" class="TableData">&nbsp;
			Select one or more State Name(s) from the multiselect box.
			</TD>
          </tr>
          <tr>
		  <TD width="20%" align="left" valign="top" class="ColumnBackground">
										<DIV align="left">
											&nbsp;Industry Sector
										</DIV>
									</TD>
           <TD width="27%" align="left" valign="top" class="TableData">&nbsp;
			Select one or more Industry Sector(s) from the multiselect box.
			</TD>
          </tr>
          <tr>
		  <TD width="20%" align="left" valign="top" class="ColumnBackground">
										<DIV align="left">
											&nbsp;Interest Rate 
										</DIV>
									</TD>
           <TD width="27%" align="left" valign="top" class="TableData">&nbsp;
			Enter the range of Interest Rate.
			</TD>
          </tr>
          <tr>
		  <TD width="20%" align="left" valign="top" class="ColumnBackground">
										<DIV align="left">
											&nbsp;Interest Type
										</DIV>
									</TD>
           <TD width="27%" align="left" valign="top" class="TableData">&nbsp;
			Select the type of Interest(Fixed or Floating) by checking the any one of the radio buttons.
			</TD>
          </tr>
          <tr>
		  <TD width="20%" align="left" valign="top" class="ColumnBackground">
										<DIV align="left">
											&nbsp;Interest Type
										</DIV>
									</TD>
           <TD width="27%" align="left" valign="top" class="TableData">&nbsp;
			Select the type of Interest by checking the any one of the radio buttons.
			</TD>
          </tr>
          <tr>
		  <TD width="20%" align="left" valign="top" class="ColumnBackground">
										<DIV align="left">
											&nbsp;Loan Size
										</DIV>
									</TD>
           <TD width="27%" align="left" valign="top" class="TableData">&nbsp;
			Enter the range of Loan Size.
			</TD>
          </tr>
		<tr>
		  <TD width="20%" align="left" valign="top" class="ColumnBackground">
										<DIV align="left">
											&nbsp;Track Record
										</DIV>
									</TD>
           <TD width="27%" align="left" valign="top" class="TableData">&nbsp;
			Enter the track record.
			</TD>
          </tr>

		  
							<TR align="center" valign="baseline" >
						<td colspan="2" width="700">
						<DIV align="center">
						<A  HREF="javascript:history.back()">	
						<IMG src="images/Back.gif" alt="Save" width="49" height="37" border="0">
						</A>								
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
	</form>
</TABLE>
