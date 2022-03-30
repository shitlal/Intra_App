package com.cgtsi.util;

   import java.sql.*;  
	class OracleCon{  
	public static void main(String args[]){  
	try{  
	Class.forName("oracle.jdbc.driver.OracleDriver");  
	
	Connection con=DriverManager.getConnection("dbc:oracle:thin:@152.67.26.18:1521:CGSUAT.svcsubnetad1.svcvcn.oraclevcn.com",	"CGTSIINTRANETUSER","CGTSIINTRANETUSER");    
    
      Statement stmt=con.createStatement();          
    System.out.println("con>>>>>>>>>"+con);
    
      ResultSet rs=stmt.executeQuery("select app_ref_no,cgpan from application_detail"); 
      System.out.println("RS>>>>>>>"+rs.getFetchSize());
      while(rs.next())  
      System.out.println(rs.getString("app_ref_no")+"  "+rs.getString("cgpan"));  
        
      //step5 close the connection object  
      con.close();  
        
      }catch(Exception e){ System.out.println(e);}  
       
      }  
      }  
