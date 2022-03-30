<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ include file="/jsp/SetMenuInfo.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<LINK href="<%=request.getContextPath()%>/css/StyleSheet.css" rel="stylesheet" type="text/css">
</head>
<body>
	<html:form action="/getUserLoginHistory.do?method=getUserLoginHistory" method="post"><br/><br/><br/><br/>
		<center>
		<div align="center" border="1">
			<table>
				<tr>
					<td class="TableData">Member Id</td> 	<td>:</td>
					<td><html:text name="adminForm" property="memberId" maxlength="12"/></td>
				</tr>
				
				<tr>
					<td class="TableData">User Id</td> 	<td>:</td>
					<td><html:text name="adminForm" property="userId"/></td>
				</tr>
				<tr>
					<td colspan="3" align="right"><html:submit value="Submit"/></td>
				</tr>
			</table>
			</div>					
		</center>
	</html:form>
</body>
</html>