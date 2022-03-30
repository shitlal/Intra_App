package com.cgtsi.action;

   import java.sql.*;  
	class OracleCon{  
	public static void main(String args[]){  
	try{  
	//step1 load the driver class  
	Class.forName("oracle.jdbc.driver.OracleDriver");  
	
	//step2 create  the connection object  
	Connection con=DriverManager.getConnection("jdbc:oracle:thin:@152.67.26.18:1521:CGSUAT","CGTSIINTRANETUSER","CGTSIINTRANETUSER");    
     // "jdbc:oracle:thin:@localhost:1521:xe","system","oracle");  
        
      //step3 create the statement object  
      Statement stmt=con.createStatement();  
        
      //step4 execute query  
      ResultSet rs=stmt.executeQuery("select app_ref_no,cgpan from application_detail");  
      while(rs.next())  
      System.out.println(rs.getString("DKR>>>>>>>>>>>>>>>>>>>.app_ref_no")+"  "+rs.getString("cgpan"));  
        
      //step5 close the connection object  
      con.close();  
        
      }catch(Exception e){ System.out.println(e);}  
        
      }  
      }  
