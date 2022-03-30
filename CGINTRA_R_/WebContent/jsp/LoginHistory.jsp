<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean" %>
<%@ include file="/jsp/SetMenuInfo.jsp" %>
<html>
<head><title>Login History Page...</title></head>
<LINK href="<%=request.getContextPath()%>/css/StyleSheet.css" rel="stylesheet" type="text/css"><br/><br/><br/><br/>
<body>
	<center>
	<table>
		<tr>
			<td class="TableData">Member ID</td> 	<td>:</td>
			<td><bean:write name="adminForm" property="memberId" /></td>
		</tr>
		<tr>
			<td class="TableData">User Id </td> 	<td>:</td>
			<td><bean:write name="adminForm" property="userId" /></td>
		</tr>
		<tr>
			<td class="TableData">Designation</td> 	<td>:</td>
			<td><bean:write name="adminForm" property="designation" /></td>
		</tr>
		<tr>
			<td class="TableData">Password Changed Date</td> 	<td>:</td>
			<td><bean:write name="adminForm" property="passwordChangedDt" /></td>
		</tr>
		
		<tr>
			<td class="TableData">Password Expiry Date</td> 	<td>:</td>
			<td><bean:write name="adminForm" property="passwordExpiryDt" /></td>
		</tr>
		<tr>
			<td class="TableData">Hint Ques</td> 	<td>:</td>
			<td><bean:write name="adminForm" property="hintQuestion" /></td>
		</tr>
		<tr>
			<td class="TableData">UnSuccessful Attempts</td> 	<td>:</td>
			<td><bean:write name="adminForm" property="unsuccessfulAttempt" /></td>
		</tr>
		<tr>
			<td class="TableData">Password</td> 	<td>:</td>
			<td><html:text name="adminForm" property="password"/></td>
		</tr>
		<tr>
			<td colspan="3" align="right"><html:submit value="Login"/></td>
		</tr>		
	</table>
	<br/>
	
	</center>
</body>
</html>
