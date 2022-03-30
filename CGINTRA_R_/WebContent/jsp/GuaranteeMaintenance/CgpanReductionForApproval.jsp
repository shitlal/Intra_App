<%@page import="java.util.Calendar"%>
<%@ page language="java"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.cgtsi.actionform.GMActionForm"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DecimalFormat"%>

<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic"%>
<%@ include file="/jsp/SetMenuInfo.jsp"%>
<%
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
String name="";
%>
<%
	session.setAttribute("CurrentPage","cgpanReductionApproval.do?method=cgpanReductionApproval");
%>
<script type="text/javascript"><!--
			function SubmitReductionApprForm(actionType)
			{

				//alert("SubmitReductionApprForm S : "+actionType);	
				var checkboxes = document.getElementsByName('decision');
				var values="";
				var vals = "";
				for (var i=0, n=checkboxes.length;i<n;i++) 
				{
				    if (checkboxes[i].checked) 
				    {
				        vals += ","+checkboxes[i].value;
				    }
				}
			
				if (vals!=null)
				{
					values=vals.replace(",", "");
			 	}
				
         
                urls="showApprReductionFormSubmit.do?method=showApprReductionFormSubmit&values="+values+ "&Actiontype="+actionType; 

                document.forms[0].action=urls;
				document.forms[0].target="_self";
				document.forms[0].method="POST";
				document.forms[0].submit();
                
                //document.forms[0].action = "/showApprReductionFormSubmit.do?action=showApprReductionFormSubmit&values="+values;
                //document.forms[0].submit();
          
				
			}


		</script>
<TABLE width="725" border="0" cellpadding="0" cellspacing="0">
	<html:errors />
	<html:form
		action="cgpanReductionApproval.do?method=cgpanReductionApproval"
		method="POST" enctype="multipart/form-data">
		<TR>
			<TD width="20" align="right" valign="bottom"><IMG
				src="images/TableLeftTop.gif" width="20" height="31"></TD>
			<TD background="images/TableBackground1.gif"><IMG
				src="images/ReportsHeading.gif" width="121" height="25"></TD>
			<TD width="20" align="left" valign="bottom"><IMG
				src="images/TableRightTop.gif" width="23" height="31"></TD>
		</TR>
		<TR>
			<TD width="20" background="images/TableVerticalLeftBG.gif">&nbsp;</TD>
			<TD>
			<TABLE width="100%" border="0" align="left" cellpadding="0"
				cellspacing="0">
				<TR>
					<TD>
					<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td colspan="6" class="Heading1" align="center"><u>Credit
							Guarantee Fund Trust for Micro and Small Enterprises </u></td>
						</tr>
						<TR>
							<TD height="20">&nbsp;</TD>
						</TR>
					</table>
					<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
						<TR>
							<TD width="30%" class="Heading">CGPAN Reduction for Approval</TD>
							<TD><IMG src="images/TriangleSubhead.gif" width="25"
								height="19"></TD>
						</TR>
						<TR>
							<TD width="5%" colspan="12" class="Heading"><IMG
								src="images/Clear.gif" width="5" height="5"></TD>
						</TR>
					</TABLE>
					<TABLE width="100%" border="0" cellpadding="0" cellspacing="1">
						<tr>
		
                  <TD valign="top" class="HeadingBg"><div align="center">&nbsp;&nbsp;<strong>Sr.No</strong><br></div></TD>
                  
                  <td valign="top"  class="HeadingBg"><div align="center">&nbsp;&nbsp;<strong>CGPAN</strong></div></td>
                  <td valign="top" class="HeadingBg"><div align="center">&nbsp;&nbsp;<strong>WCP FB LIMIT SANCTIONED</strong></div></td>
                      
                       <td valign="top" class="HeadingBg"><div align="center">&nbsp;&nbsp;
                      <strong>WCP NFB LIMIT SANCTIONED</strong><br></div></td>
                   
                     <td valign="top" class="HeadingBg"><div align="center">&nbsp;&nbsp;<strong>WCP FB CREDIT TO GUARANTEE<br>
                      </strong><br></div></td>
                      
                      <td valign="top" class="HeadingBg"><div align="center">&nbsp;&nbsp;<strong>WCP NFB CREDIT TO GUARANTEE
                      </strong><br></div></td>
                      
                 	<TD valign="top" class="HeadingBg"><div align="center">&nbsp;&nbsp;<strong>DECISION</strong><br></div></TD>
                  
                  	<!--<TD valign="top" class="HeadingBg"><div align="center">&nbsp;&nbsp;<strong>COMMENTS</strong><br></div></TD>
                  
		        --></tr>
		       
		        		   
	
			
			 <logic:iterate id="object" name="gmForm" property="cgmap" indexId="index">
								<%
									GMActionForm cgpanMap = (GMActionForm)object;
									//String comments = "";
									String cgpan = cgpanMap.getCGPAN();
									System.out.println("JSP cgpan : "+cgpan);
								%>
			
								<TR>
									<TD  align="left" valign="top" class="ColumnBackground1">
                   &nbsp;<%=Integer.parseInt(index+"")+1%></TD>
                                    <TD  align="left" valign="top" class="ColumnBackground1">
                   &nbsp;<%=cgpanMap.getCGPAN()%></TD>                 
									<TD  align="left" valign="top" class="ColumnBackground1">
                   &nbsp;<%=cgpanMap.getWCP_FB_LIMIT_SANCTIONED()%></TD>
									<TD  align="left" valign="top" class="ColumnBackground1">
                   &nbsp;<%=cgpanMap.getWCP_NFB_LIMIT_SANCTIONED()%></TD>
									<TD  align="left" valign="top" class="ColumnBackground1">
                   &nbsp;<%=cgpanMap.getWCP_FB_CREDIT_TO_GUARANTEE()%></TD>
									<TD  align="left" valign="top" class="ColumnBackground1">
                   &nbsp;<%=cgpanMap.getWCP_NFB_CREDIT_TO_GUARANTEE()%></TD>
									
									<TD  align="left" valign="top" class="ColumnBackground1">
                   &nbsp;&nbsp;&nbsp;
                   
										<html:checkbox property="decision" value="<%=cgpan%>" name="gmForm"/>
									</TD>
									
									
								</TR>
							</logic:iterate>		
                   	
						
						
					</TABLE>
			
				<tr align="center" valign="baseline">
           		 <td colspan="4"> 
           		 <div align="center"> 
		         <A href="#" onclick="SubmitReductionApprForm('update');">
              <img src="images/Approve.gif" alt="Accept" width="49" height="37" border="0"></a>
              <A href="#" onclick="SubmitReductionApprForm('reject');">
              <img src="images/Reject.gif" alt="Reject" width="49" height="37" border="0"></a>
              <A href="subHome.do?method=getSubMenu&menuIcon=<%=session.getAttribute("menuIcon")%>&mainMenu=<%=session.getAttribute("mainMenu")%>">
			  <IMG src="images/Cancel.gif" alt="Cancel" width="49" height="37" border="0"></A>
		      </div>
		      </td>
		     </tr>
		     
		     
			</TABLE>
			</TD>
			<TD width="20" background="images/TableVerticalRightBG.gif">
			&nbsp;</TD>
		</TR>
		<TR>
			<TD width="20" align="right" valign="top"><IMG
				src="images/TableLeftBottom1.gif" width="20" height="15"></TD>
			<TD background="images/TableBackground2.gif">&nbsp;</TD>
			<TD width="20" align="left" valign="top"><IMG
				src="images/TableRightBottom1.gif" width="23" height="15"></TD>
		</TR>
	</html:form>
</TABLE>
