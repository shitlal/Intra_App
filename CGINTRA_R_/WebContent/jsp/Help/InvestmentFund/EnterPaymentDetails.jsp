<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<% session.setAttribute("CurrentPage","helpEnterPaymentDetails.do");%>
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
									<TD width="31%" class="Heading"><bean:message key="helpHeader" /></TD>
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
											&nbsp; Payments To
										</DIV>
									</TD>
           <TD width="27%" align="left" valign="top" class="TableData">&nbsp;
												Enter the source for which payments  to be made.
		  </TD>
          </tr>
		  
		   <tr>
		  <TD width="20%" align="left" valign="top" class="ColumnBackground">
										<DIV align="left">
											&nbsp; Amount
										</DIV>
									</TD>
           <TD width="27%" align="left" valign="top" class="TableData">&nbsp;
												Enter the amount to be paid.
		  </TD>
          </tr>
		  
		   <tr>
		  <TD width="20%" align="left" valign="top" class="ColumnBackground">
										<DIV align="left">
											&nbsp; Payment Date
										</DIV>
									</TD>
           <TD width="27%" align="left" valign="top" class="TableData">&nbsp;
												Enter the date on which the payment is to be made.
		  </TD>
          </tr>

		  <tr>
		  <TD width="20%" align="left" valign="top" class="ColumnBackground">
										<DIV align="left">
											&nbsp; If Chequed
										</DIV>
									</TD>
           <TD width="27%" align="left" valign="top" class="TableData">&nbsp;
												Select an option to check if payment details are chequed or not.
		  </TD>
          </tr>

		  <tr>
		  <TD width="20%" align="left" valign="top" class="ColumnBackground">
										<DIV align="left">
											&nbsp; Bank
										</DIV>
									</TD>
           <TD width="27%" align="left" valign="top" class="TableData">&nbsp;
												Select the bank from the combo box which has issued the cheque.
		  </TD>
          </tr>

		  <tr>
		  <TD width="20%" align="left" valign="top" class="ColumnBackground">
										<DIV align="left">
											&nbsp; Number
										</DIV>
									</TD>
           <TD width="27%" align="left" valign="top" class="TableData">&nbsp;
												Enter a value for the Cheque Number.
		  </TD>
          </tr>

		  <tr>
		  <TD width="20%" align="left" valign="top" class="ColumnBackground">
										<DIV align="left">
											&nbsp; Date
										</DIV>
									</TD>
           <TD width="27%" align="left" valign="top" class="TableData">&nbsp;
												Enter the date on which the cheque is issued.
		  </TD>
          </tr>

		  <tr>
		  <TD width="20%" align="left" valign="top" class="ColumnBackground">
										<DIV align="left">
											&nbsp; Amount
										</DIV>
									</TD>
           <TD width="27%" align="left" valign="top" class="TableData">&nbsp;
												Enter the cheque amount.
		  </TD>
          </tr>

		  <tr>
		  <TD width="20%" align="left" valign="top" class="ColumnBackground">
										<DIV align="left">
											&nbsp; Issued to
										</DIV>
									</TD>
           <TD width="27%" align="left" valign="top" class="TableData">&nbsp;
												Enter the source for which the cheque has been issued.
		  </TD>
          </tr>
		   
		   <tr>
		  <TD width="20%" align="left" valign="top" class="ColumnBackground">
										<DIV align="left">
											&nbsp; Remarks
										</DIV>
									</TD>
           <TD width="27%" align="left" valign="top" class="TableData">&nbsp;
												Enter the remarks
		  </TD>
          </tr>

		   

							<TR align="center" valign="baseline" >
						<td colspan="2" width="700">
						<DIV align="center">
						<A href="javascript:history.back()">
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


