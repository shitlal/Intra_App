
<%@ page language="java"%>
<%@ page import="com.cgtsi.actionform.ClaimActionForm"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic"%>
<%@ include file="/jsp/SetMenuInfo.jsp" %>
<% session.setAttribute("CurrentPage","updateClaimSettledDetailsNew.do?method=updateClaimSettledDetailsNew");
String focusField = null;
%>
<html:errors/>
<%
ClaimActionForm claimForm = (ClaimActionForm)session.getAttribute("cpTcDetailsForm") ;
%>
<html:form action="updateClaimSettledDetailsNew.do?method=updateClaimSettledDetailsNew" method="POST" focus="<%=focusField%>" enctype="multipart/form-data">
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
    
    <tr>
      <td class="FontStyle">&nbsp;</td>
    </tr>
  </table>
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
    <td width="20" align="right" valign="bottom"><img src="images/TableLeftTop.gif" width="20" height="31"></td>
      <td width="248" background="images/TableBackground1.gif"></td>
    <td align="right" valign="top" background="images/TableBackground1.gif"> </td>
    <td width="23" align="left" valign="bottom"><img src="images/TableRightTop.gif" width="23" height="31"></td>
  </tr>
  <tr>
    <td width="20" background="images/TableVerticalLeftBG.gif">&nbsp;</td>
    <td colspan="2">
      <DIV align="right">			
      		<A HREF="javascript:submitForm('helpEnterMemIdBIdCGPAN.do')">
      	        HELP</A>
      </DIV>        
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td> <table width="100%" border="0" cellspacing="1" cellpadding="0">
                <tr>
                  <td colspan="4"><table width="100%" border="0" cellspacing="0" cellpadding="0">
		      <tr>
			 <td width="35%" class="Heading">&nbsp;Update Cliam Settled Details</td>
			 <td align="left" valign="bottom"><img src="images/TriangleSubhead.gif" width="19" height="19"></td>
			 <td>&nbsp;</td>
			 <td>&nbsp;</td>
		      </tr>
                      <tr>
                        <td colspan="4" class="Heading"><img src="images/Clear.gif" width="5" height="5"></td>
                      </tr>
                    </table></td>
                </tr>
             <!--       <logic:equal property="bankId" value="0000" name="cpTcDetailsForm">
                    <tr>                
                  <td colspan ="2" class="ColumnBackground"> <div align="left">&nbsp;<bean:message key="memberId"/></div></td>  
		    <td colspan="2" class="TableData"> <div align="left">
		    <html:text property="memberId" maxlength="12" name="cpTcDetailsForm" onkeypress="return numbersOnly(this, event)" onkeyup="isValidNumber(this)"/>
		    </div></td>
		    </tr>
		    </logic:equal>
		    <logic:notEqual property="bankId" value="0000" name="cpTcDetailsForm">
		    <tr>                
		     <td colspan ="2" class="ColumnBackground"> <div align="left">&nbsp;<bean:message key="memberId"/></div></td>                		    
		    <td colspan="2" class="TableData"> <div align="left">
		    <html:text property="memberId" maxlength="12" name="cpTcDetailsForm" onkeypress="return numbersOnly(this, event)" onkeyup="isValidNumber(this)"/>
		                 </div></td>
                     </tr>
		    </logic:notEqual>    -->             
       <tr colspan="4">
			 <td class="ColumnBackground" colspan ="2"> <div align="left"><font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="cpclaimrefnumber"/></div></td>
			  <td  class="TableData" colspan ="2"><div align="left">
			  <html:text property="clmRefNumber" maxlength="9" name="cpTcDetailsForm"/> &nbsp;&nbsp;&nbsp;
			  </div></td>		
        </tr>
        <tr colspan="4">
			  <td class="ColumnBackground" colspan ="2"> <div align="left"><font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="thiscgpan"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div></td>
			  <td class="TableData" colspan ="2"><div align="left">
			  <html:text property="cgpan" maxlength="13" name="cpTcDetailsForm"/>
			  </div></td>			  
       </tr>
      <tr>
        <td class="ColumnBackground"><font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="approvalcomments"/>&nbsp;</td>
     <td align="left" valign="top" class="TableData" colspan="7">
    <!-- <html:textarea property="userRemarks" name="cpTcDetailsForm"/>		-->
     <html:text property="userRemarks"  size="100" alt="userRemarks" name="cpTcDetailsForm"/>
	   </td>
      </tr>
               <tr>
                  <td colspan="4"><img src="images/Clear.gif" width="5" height="15"></td>
                </tr>
              </table>
      </table></td>

    <td width="23" background="images/TableVerticalRightBG.gif">&nbsp;</td>
  </tr>
  <tr>
      <td width="20" align="right" valign="bottom"><img src="images/TableLeftBottom.gif" width="20" height="51"></td>
      <td colspan="2" valign="bottom" background="images/TableBackground3.gif">
        <div>
          <div align="center">          
          <A href="javascript:submitForm('afterUpdateClaimSettledDetails.do?method=afterUpdateClaimSettledDetails')">
<img src="images/OK.gif" alt="Apply" width="49" height="37" border="0"></a>
          <a href="javascript:document.cpTcDetailsForm.reset()"><img src="images/Reset.gif" alt="Reset" width="49" height="37" border="0"></a>
          <a href="subHome.do?method=getSubMenu&menuIcon=<%=session.getAttribute("menuIcon")%>&mainMenu=<%=session.getAttribute("mainMenu")%>"><img src="images/Cancel.gif" alt="Cancel" width="49" height="37" border="0"></a>
          </div>
      </div></td>
      <td width="23" align="right" valign="bottom"><img src="images/TableRightBottom.gif" width="23" height="51"></td>
  </tr>
</table>
</html:form>
</body>
</html>
