  <%@ page language="java"%>
  <%@ page import="java.util.*"%>
  <%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html"%>
  <%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean"%>
  <%@ taglib uri="/WEB-INF/TLD/struts-logic.tld" prefix="logic"%>
  <%@ include file="/jsp/SetMenuInfo.jsp"%>
  
  <% session.setAttribute("CurrentPage","womenEntrepreneurReports.do?method=womenEntrepreneurReports");%>
  	
  <TABLE width="725" border="0" cellpadding="0" cellspacing="0">
     <html:errors/>
      <html:form action="womenEntrepreneurReports.do?method=womenEntrepreneurReports" method="POST" enctype="multipart/form-data">
      <TR>
        <TD width="20" align="right" valign="bottom">
          <IMG src="images/TableLeftTop.gif" width="20" height="31"/>
        </TD>
        <TD background="images/TableBackground1.gif">
          <IMG src="images/ReportsHeading.gif" width="121" height="25"/>
        </TD>
        <TD width="20" align="left" valign="bottom">
          <IMG src="images/TableRightTop.gif" width="23" height="31"/>
        </TD>
      </TR>
      <TR>
        <TD width="20" background="images/TableVerticalLeftBG.gif">&nbsp;</TD>
        <TD>
          <DIV align="right">
            <A HREF="womenEntrepreneurReportHelp.do?method=womenEntrepreneurReportHelp">HELP</A>
            <TABLE width="100%" border="0" align="left" cellpadding="0" cellspacing="0">
              <tr>
                <TD align="left" colspan="4">&nbsp;</TD>
              </tr>
              <TR>
                <TD>
                  <TABLE width="100%" border="0" cellspacing="1" cellpadding="1">
                    <TR>
                      <TD colspan="8">
                        <TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
                          <TR>
                            <TD width="27%" class="Heading">
                              <bean:message key="danReportHeader"/>
                            </TD>
                            <TD>
                              <IMG src="images/TriangleSubhead.gif" width="19" height="19"/>
                            </TD>
                          </TR>
                          <TR>
                            <TD colspan="3" class="Heading">
                              <IMG src="images/Clear.gif" width="5" height="4"/>
                            </TD>
                          </TR>
                        </TABLE>
                      </TD>
                    </TR>
                    <TR>
                      <TD align="left" valign="top" class="ColumnBackground" width="15%">
                        <DIV align="center">
                          <bean:message key="fromdate"/>
                        </DIV>
                      </TD>
                      <TD align="left" valign="center" class="TableData">
                        <DIV align="left">
                          <html:text property="dateOfTheDocument34" size="20" maxlength="10" alt="Reference" name="rsForm"/>
                          <IMG src="images/CalendarIcon.gif" width="20" onClick="showCalendar('rsForm.dateOfTheDocument34')" align="center"/>
                          <DIV align="left"/>
                        </DIV>
                      </TD>
                      <TD align="left" valign="top" class="ColumnBackground">&nbsp;
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="toDate"/>
                      </TD>
                      <TD align="left" valign="center" class="TableData">
                        <DIV align="left">
                          <html:text property="dateOfTheDocument35" size="20" maxlength="10" alt="Reference" name="rsForm"/>
                          <IMG src="images/CalendarIcon.gif" width="20" onClick="showCalendar('rsForm.dateOfTheDocument35')" align="center"/>
                          <DIV align="left"/>
                        </DIV>
                      </TD>
                    </TR>
                    <tr>
                    <TD  align="center" valign="center"  class="ColumnBackground" width="15%">
									  <DIV align="center"><bean:message key="memberId" /> 
									   </DIV>
									  </TD>
									   <TD align="left" valign="center" class="TableData" colspan=1>
									   <DIV align="left">
										 <html:text property="memberId" size="20"  alt="memberId" name="rsForm" onkeypress="return numbersOnly(this, event)" onkeyup="isValidNumber(this)" maxlength="12"/>  
										 </DIV>
									  </TD>
                    <TD  align="center" valign="center"  class="ColumnBackground" width="15%">
									  </TD>
									   <TD align="left" valign="center" class="TableData" colspan=1>
									  </TD>  
                    </tr>
                     <TR>
                      <TD align="left" valign="center" class="ColumnBackground" width="15%">
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="state"/>
                      </TD>
                      <TD align="left" valign="center" class="TableData">
                        <DIV align="left">
                          <% 

    ArrayList arraylist2 = (ArrayList)session.getAttribute("womenEntrepreneurReportArray2");
    Iterator iterator2 = arraylist2.iterator();
    
	%>
                          <html:select property="State" onchange="javascript:submitForm('womenEntrepreneurReports.do?method=fetchDistrict')">
    <html:option value="">Select</html:option>
    <%String s[]=null; %>
    <%
    while (iterator2.hasNext())
    { 
    s=new String[2];
    s = (String[])iterator2.next();
%>
     <html:option value="<%=s[0]%>"><%=s[1]%></html:option>
	 <%
	   }
   %>  
                          </html:select>
                        </DIV>
                      </TD>
                    <TD align="left" valign="top" class="ColumnBackground">
                      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="district"/>
                    </TD>
                  
                               <TD align="left" valign="center" class="TableData">
                        <DIV align="left">
      
    <html:select property="District">
    <html:option value="">Select</html:option>
 <%
    String size = (String)request.getAttribute("district_arraylist_data_size");
    String s[]=null;
    
    if(size!="" && size!="0" && size!=null)
    {
     ArrayList districtarraylist = (ArrayList)request.getAttribute("district_arraylist_data");
      
    for(int i=0;i<districtarraylist.size();i++)
    { 
    s=new String[2];
    s = (String[])districtarraylist.get(i);
%>
     <html:option value="<%=s[0]%>"><%=s[1]%></html:option>
	 <%
	   }
  }  
  %>
  </html:select>
      </TR>
                    <tr>
                     
                  </TABLE>
                </TD>
              </TR>
              <TR>
                <TD height="20">&nbsp;</TD>
              </TR>
              <TR>
                <TD align="center" valign="baseline">
                  <DIV align="center">
                    <A href="javascript:submitForm('womenEntrepreneurReports.do?method=womenEntrepreneurReportDetails')">
                      <IMG src="images/OK.gif" alt="OK" width="49" height="37" border="0"/>
                    </A>
                    <A href="javascript:document.rsForm.reset()">
                      <IMG src="images/Reset.gif" alt="Reset" width="49" height="37" border="0"/>
                    </A>
                    <a href="subHome.do?method=getSubMenu&menuIcon=<%=session.getAttribute("menuIcon")%>&mainMenu=<%=session.getAttribute("mainMenu")%>">
                      <IMG src="images/Cancel.gif" alt="Cancel" width="49" height="37" border="0"/>
                    </a>
                  </DIV>
                </TD>
              </TR>
            </TABLE>
          </DIV>
        </TD>
        <TD width="20" background="images/TableVerticalRightBG.gif">&nbsp;</TD>
      </TR>
      <TR>
        <TD width="20" align="right" valign="top">
          <IMG src="images/TableLeftBottom1.gif" width="20" height="15"/>
        </TD>
        <TD background="images/TableBackground2.gif">&nbsp;</TD>
        <TD width="20" align="left" valign="top">
          <IMG src="images/TableRightBottom1.gif" width="23" height="15"/>
        </TD>
      </TR>
    </html:form>
  </TABLE>
  </body>
