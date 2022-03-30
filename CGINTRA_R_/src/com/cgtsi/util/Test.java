package com.cgtsi.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Test {
   /* public static void main(String[] args) {
        String input="";
        System.out.println("Enter the input string");
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            input = br.readLine();
            char[] try1= input.toCharArray();
            for (int i=try1.length-1;i>=0;i--)
            System.out.print(try1[i]);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
}*/
	
	 public static void main(String[] args) throws ClassNotFoundException {
		 Test exporter = new Test();
	        exporter.export("Review");
	        exporter.export("Product");
	    }
	 
	    private String getFileName(String baseName) {
	        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	        String dateTimeInfo = dateFormat.format(new Date());
	        return baseName.concat(String.format("_%s.xlsx", dateTimeInfo));
	    }
	 
	    public void export(String table) throws ClassNotFoundException {
	       /* String jdbcURL = "jdbc:mysql://localhost:3306/sales";
	        String username = "root";
	        String password = "password";
	 */
	        String excelFilePath = getFileName(table.concat("_Export"));
	 
	        try {
	              Class.forName("oracle.jdbc.driver.OracleDriver");  
	        		
	        		Connection connection=DriverManager.getConnection("dbc:oracle:thin:@152.67.26.18:1521:CGSUAT.svcsubnetad1.svcvcn.oraclevcn.com","CGTSITEMPUSER","CGTSITEMPUSER");    
	        	    
	        	      Statement statement=connection.createStatement();          
	        	    
	        	      ResultSet result=statement.executeQuery("select app_ref_no,cgpan from application_detail");  
	        	       		
	       /*Connection connection = DriverManager.getConnection(jdbcURL, username, password)) //{
	            String sql = "SELECT * FROM ".concat(table);
	 
	            Statement statement = connection.createStatement();
	 
	            ResultSet result = statement.executeQuery(sql);*/
	 
	            XSSFWorkbook workbook = new XSSFWorkbook();
	            XSSFSheet sheet = workbook.createSheet(table);
	 
	            writeHeaderLine(result, sheet);
	 
	            writeDataLines(result, workbook, sheet);
	 
	            FileOutputStream outputStream = new FileOutputStream(excelFilePath);
	            workbook.write(outputStream);
	            ((Connection) workbook).close();
	 
	            statement.close();
	 
	        } catch (SQLException e) {
	            System.out.println("Datababse error:");
	            e.printStackTrace();
	        } catch (IOException e) {
	            System.out.println("File IO error:");
	            e.printStackTrace();
	        }
	    }
	 
	    private void writeHeaderLine(ResultSet result, XSSFSheet sheet) throws SQLException {
	        // write header line containing column names
	        ResultSetMetaData metaData = result.getMetaData();
	        int numberOfColumns = metaData.getColumnCount();
	 
	        Row headerRow = sheet.createRow(0);
	 
	        // exclude the first column which is the ID field
	        for (int i = 2; i <= numberOfColumns; i++) {
	            String columnName = metaData.getColumnName(i);
	            Cell headerCell = headerRow.createCell(i - 2);
	            headerCell.setCellValue(columnName);
	        }
	    }
	 
	    private void writeDataLines(ResultSet result, XSSFWorkbook workbook, XSSFSheet sheet) 
	            throws SQLException {
	        ResultSetMetaData metaData = result.getMetaData();
	        int numberOfColumns = metaData.getColumnCount();
	 
	        int rowCount = 1;
	 
	        while (result.next()) {
	            Row row = sheet.createRow(rowCount++);
	 
	            for (int i = 2; i <= numberOfColumns; i++) {
	                Object valueObject = result.getObject(i);
	 
	                Cell cell = row.createCell(i - 2);
	 
	                if (valueObject instanceof Boolean) 
	                    cell.setCellValue((Boolean) valueObject);
	                else if (valueObject instanceof Double)
	                    cell.setCellValue((Double) valueObject);
	                else if (valueObject instanceof Float)
	                    cell.setCellValue((Float) valueObject);
	                else if (valueObject instanceof Date) {
	                    cell.setCellValue((Date) valueObject);
	                    formatDateCell(workbook, cell);
	                } else cell.setCellValue((String) valueObject); 
	 
	            }
	 
	        }
	    }
	 
	    private void formatDateCell(XSSFWorkbook workbook, Cell cell) {
	        CellStyle cellStyle = workbook.createCellStyle();
	        CreationHelper creationHelper = workbook.getCreationHelper();
	        cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));
	        cell.setCellStyle(cellStyle);
	    }
	}
