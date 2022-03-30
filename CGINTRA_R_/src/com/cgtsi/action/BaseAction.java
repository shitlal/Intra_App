/*************************************************************
   *
   * Name of the class: BaseAction.java
   * This is the base class for all the Action classes.
   * This class is used if any common functionality is to be
   * implemented across modules.
   *  
   * @author : Veldurai T
   * @version:  
   * @since: Sep 16, 2003
   **************************************************************/

package com.cgtsi.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.actions.DispatchAction;
import org.apache.struts.upload.FormFile;

import com.cgtsi.admin.User;
import com.cgtsi.common.Constants;
import com.cgtsi.common.Log;
import com.cgtsi.registration.MLIInfo;
import com.cgtsi.util.PropertyLoader;
import com.cgtsi.util.SessionConstants;

/**
 * @author VT8150
 *
 * This is the base class for all the Action classes.
 * This class is used if any common functionality is to be
 * implemented across modules.
 */
public class BaseAction extends DispatchAction {

	/**
	 * 
	 */
	protected BaseAction() {
		super();
	}
	
	
	
	/**
	 * This method is used to get the logged in user's information.
	 * 
	 * @param request	The current Http request
	 * @return	User 	The logged in user's object.
	 */
	
	protected User getUserInformation(HttpServletRequest request)
	{
		Log.log(Log.INFO,"BaseAction","getUserInformation","Entered");
		HttpSession session=request.getSession(false);
		
		String userId=(String)session.getAttribute(SessionConstants.USER_ID);
		
		Log.log(Log.DEBUG,"BaseAction","getUserInformation","User id obtained from session is "+userId);
		
		User user=(User)session.getAttribute(userId);
		
		Log.log(Log.DEBUG,"BaseAction","getUserInformation","User info is "+user);
		
		Log.log(Log.INFO,"BaseAction","getUserInformation","Exited");
		return user;
		
	}
	/**
	 * This method is used to get the logged in user's Member information
	 * 
	 * @param request	The current Http request
	 * @return	MLIInfo 	The logged in user's member information object.
	 */	
	
	protected MLIInfo getMemberInfo(HttpServletRequest request)
	{
		Log.log(Log.INFO,"BaseAction","getMemberInfo","Entered");
		
		HttpSession session=request.getSession(false);
		MLIInfo mliInfo=(MLIInfo)session.getAttribute(SessionConstants.MEMBER_INFO);
		
		Log.log(Log.INFO,"BaseAction","getMemberInfo","Exited");
		
		return mliInfo;
	}
	
	/**
	 * This method is used to upload files from the client machine.
	 * 
	 * @param formFile		The encapsulated uploaded file details.
	 * @param contextPath	The context path where in the application runs.
	 * @return File			The stored in File object
	 * @throws Exception	If any error occurs Exception would be thrown.
	 */
	
	protected File uploadFile(FormFile formFile,String contextPathTemp) throws Exception
	{
		try
		{
		   Log.log(Log.INFO,"CommonAction","uploadFile","Entered");

		   InputStream input=formFile.getInputStream();
		   String contextPath=PropertyLoader.changeToOSpath(contextPathTemp);
			
		   Log.log(Log.DEBUG,"CommonAction","uploadFile","contextPath: "+contextPath);
			
		   int fileSize=formFile.getFileSize();
			
		   Log.log(Log.DEBUG,"CommonAction","uploadFile","fileSize: "+fileSize+" "+formFile.getFileName());
			
		   if(fileSize==0)
		   {
			   Log.log(Log.WARNING,"CommonAction","uploadFile","File Size is Zero");
			   return null;
		   }
		   
		   String fileName=contextPath+File.separator+
		   				Constants.FILE_UPLOAD_DIRECTORY+File.separator+
		   						formFile.getFileName();
		   FileOutputStream fileOut=new FileOutputStream(fileName);
		   int readByte=0;
		   byte[] buffer = new byte[1024];
		   while((readByte=(int)input.read(buffer,0,buffer.length))!=-1)
		   {
			   fileOut.write(buffer,0,readByte);
				
			   //Log.log(Log.DEBUG,"CommonAction","uploadFile","writing ...");
		   }
		   buffer=null;
		   fileOut.flush();
		   fileOut.close();
		   input.close();
		   formFile.destroy();
		   File file=new File(fileName);
		   
		   return file;
		}
		catch(IOException io)
		{
		   Log.log(Log.ERROR,"CommonAction","uploadFile ",io.getMessage());
		   Log.logException(io);
		}
		
		return null;		
	}	

		
	protected File uploadFile(FormFile formFile,String contextPathTemp, String nameOfFile) throws Exception
	{
		try 
		{
		   Log.log(Log.INFO,"CommonAction","uploadFile","Entered");

		   InputStream input=formFile.getInputStream();
		   String contextPath=PropertyLoader.changeToOSpath(contextPathTemp);
			
		   Log.log(Log.DEBUG,"CommonAction","uploadFile","contextPath: "+contextPath);
			
		   int fileSize=formFile.getFileSize();
			
		   Log.log(Log.DEBUG,"CommonAction","uploadFile","fileSize: "+fileSize+" "+formFile.getFileName());
			
		   if(fileSize==0)
		   {
			   Log.log(Log.WARNING,"CommonAction","uploadFile","File Size is Zero");
			   return null;
		   }
		  // nameOfFile = "Testing";
		    
		  String fileName=contextPath+File.separator+ 
			Constants.FILE_UPLOAD_DIRECTORY+File.separator+nameOfFile;
		   FileOutputStream fileOut=new FileOutputStream(fileName);
		   int readByte=0;
		   byte[] buffer = new byte[1024];
		   while((readByte=(int)input.read(buffer,0,buffer.length))!=-1)
		   {
			   fileOut.write(buffer,0,readByte);
				
			   //Log.log(Log.DEBUG,"CommonAction","uploadFile","writing ...");
		   }
		   buffer=null;
		   fileOut.flush();
		   fileOut.close();
		   input.close();
		   formFile.destroy();
		   File file=new File(fileName);
		   
		   return file;
		}
		catch(IOException io)
		{
		   Log.log(Log.ERROR,"CommonAction","uploadFile ",io.getMessage());
		   Log.logException(io);
		}
		
		return null;		
	}	
}
