/**
 * DBConnection
 * @author
 */
package com.cgtsi.util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;


/**
  * Creates the initial connection pool and gets connection from the pool.
  */

public class DBConnection
{
private static final int MIN_CONNECTIONS=1;
private static final int MAX_CONNECTIONS=2;
private static final double CONNECTION_REFRESH_DAYS=1;

private DBConnection()
{
}
	private static DbConnectionBroker dbBroker = null;
  /**
	*Creates the initial connection pool. This method is invoked in the init method of the calling jsp or servlet.<BR>
	*driver		:	The Database driver. Obtained from property file.<BR>
	*conUrl		:	The Connection String. Obtained from property file.<BR>
	*login		:	Database username.Obtained from property file.<BR>
	*password   :	Database password.Obtained from property file.<BR>
	*minConn	:	Minimum number of connections allowed.Obtained from property file.<BR>
	*maxConn 	:	Maximum number of connections allowed.Obtained from property file.<BR>
	*dbLogFile	:	path of the file where which is used to log the connection details as connections are being created or destroyed.<BR>
	*maxTimeOut :	The maximum time allowed for a connection to exist.Obtained from property file.<BR>
	*<B>Procedures Called</B><BR>
	*DbConnectionBroker	: Creates the initial connection pool.<BR>
	*/

	private static DbConnectionBroker createPool() throws Exception
	{
		int minConn = 0;
		int maxConn = 0;
		double maxTimeOut =0;
		try
		{
			String driver=PropertyLoader.getValue("databasedriver");
//			System.out.println("driver"+driver);
			String conUrl = PropertyLoader.getValue("connectionurl");
//      System.out.println("conUrl:"+conUrl);
			String login = PropertyLoader.getValue("dblogin");
//      System.out.println("login:"+login);
			String password = PropertyLoader.getValue("dbpassword");
//      System.out.println("password:"+password);

			//System.out.println(driver+","+conUrl+", "+login+","+password);

			if(driver==null || driver.trim().equals("")||
			 conUrl==null || conUrl.trim().equals("") ||
			 login==null || login.trim().equals("") ||
			 password==null || password.trim().equals(""))
			{
				throw new Exception("Enter Values for " +
					"DatabaseDriver/ConnectionURL/DBLogin/DBPassword");
			}
			try
			{
				minConn = Integer.parseInt(PropertyLoader.getValue("minconnections"));
//				System.out.println("minConn===="+minConn);
			}
			catch(NumberFormatException invalidNumber)
			{
				minConn=MIN_CONNECTIONS;
			}
			try
			{
				maxConn = Integer.parseInt(PropertyLoader.getValue("maxconnections"));
//				System.out.println("maxConn===="+maxConn);
			}
			catch(NumberFormatException invalidNumber)
			{
				maxConn =MAX_CONNECTIONS;
			}

			String dbLogFile = PropertyLoader.getValue("contextpath")+PropertyLoader.CONFIG_DIRECTORY+"/dbPool.log";
			try
			{
				maxTimeOut = Double.parseDouble(PropertyLoader.getValue("maxconnectiontime"));
				System.out.println("maxTimeOut===="+maxTimeOut);
			}
			catch(NumberFormatException invalidNumber)
			{
				maxTimeOut = CONNECTION_REFRESH_DAYS;
			}

			dbBroker = new DbConnectionBroker(driver.trim(), conUrl.trim(),
								 login.trim(), password.trim(), minConn, maxConn,
								 	dbLogFile.trim(), maxTimeOut);
		}
		catch(IOException e)
		{
			System.out.println("Erorr===="+e);
			throw new Exception (e.getMessage());
		}
		return dbBroker;
	}
    public static Connection getConnection()
    {
		return getConnection(true);
    }

//	public static Connection getConnection(boolean autoCommit)
//	{
//		if(dbBroker==null)
//		{
//			throw new IllegalStateException("Connection pool is not" +
//				"started. Please call startConnectionPool() befoer " +
//				"calling this method.");
//		}
//		Connection connection=dbBroker.getConnection();
//		if(connection!=null)
//		{
//			try {
//				connection.setAutoCommit(autoCommit);
//			} catch (SQLException e) {
//				connection=null;
//			}
//		}
//		return connection;
//
//	}
	
	
	public static Connection getConnection(boolean autoCommit)
	{
		
		String driver = PropertyLoader.getValue("databasedriver");
		String conUrl = PropertyLoader.getValue("connectionurl");
		String login = PropertyLoader.getValue("dblogin");
		String password = PropertyLoader.getValue("dbpassword");

		Connection connection = null;
		try {
			Class.forName(driver).newInstance();
			connection = DriverManager.getConnection(conUrl, login, password);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
		
		
		if(connection!=null)
		{
			try {
				connection.setAutoCommit(autoCommit);
			} catch (SQLException e) {
				connection=null;
			}
		}
		return connection;

	}
	
	
	
	
	
	
	
	
	
	
	
	public static Connection getMyConnection()
	{
		String driver = PropertyLoader.getValue("databasedriver");
		String conUrl = PropertyLoader.getValue("connectionurl");
		String login = PropertyLoader.getValue("dblogin");
		String password = PropertyLoader.getValue("dbpassword");

		Connection conn = null;
		try {
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(conUrl, login, password);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("getMyConnection conn..." + conn);
		return conn;

	}
	
	
	
	
    public static void startConnectionPool() throws Exception
    {
		dbBroker=createPool();
    }
    public static void freeConnection(Connection connection)
    {
//		if(dbBroker!=null)
//		{
			if(connection!=null)
			{
				try
				{
					connection.commit();
					connection.close();
					connection =null;
				}
				catch(SQLException exception)
				{
					exception.printStackTrace();
				}
			}

//			dbBroker.freeConnection(connection);
//		}
    }
    
    
//    public static void freeConnection(Connection connection)
//    {
//		if(dbBroker!=null)
//		{
//			if(connection!=null)
//			{
//				try
//				{
//					connection.commit();
//				}
//				catch(SQLException exception)
//				{
//					exception.printStackTrace();
//				}
//			}
//
//			dbBroker.freeConnection(connection);
//		}
//    }
    
    
    
    public static void destroy()
    {
		if(dbBroker!=null)
		{
			dbBroker.destroy();
			dbBroker=null;
		}
    }
    public static Connection getNewConnection(boolean autoCommit)
    {
		Connection connection=dbBroker.getNewConnection(autoCommit);

		return connection;
    }

}

