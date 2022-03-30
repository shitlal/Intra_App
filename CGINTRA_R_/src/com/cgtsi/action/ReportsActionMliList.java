package com.cgtsi.action;

import com.cgtsi.common.Log;
import com.cgtsi.common.NoDataException;
import com.cgtsi.reports.ReportManager;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

public class ReportsActionMliList extends BaseAction 
{
	private ReportManager reportManager = null;

	public ReportsActionMliList ()
  {
		reportManager = new ReportManager();
	}
//////Start Code Added By Path 0n 10Oct06. //
//Created new Function to show the MLIs list on fron page of the application////////
	public ActionForward listOfMLIPath1(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception 
    {
  
      Log.log(Log.INFO,"ReportsAction","listOfMLIPath","Entered");
      ArrayList mli = new ArrayList();
      DynaActionForm dynaForm = (DynaActionForm)form;
  
      //BaseAction baseAction = new BaseAction();
      //MLIInfo mliInfo= baseAction.getMemberInfo(request);
      //String bankId = mliInfo.getBankId();
      //String zoneId = mliInfo.getZoneId();
      //String branchId = mliInfo.getBranchId();
  
        //System.out.println("cgtsi user");
        mli = reportManager.getMliList();
        dynaForm.set("mli",mli);
        if(mli == null || mli.size()==0)
        {
          throw new NoDataException("No Data is available for the values entered," +
                        " Please Enter Any Other Value ");
        }
        else
        {
          mli = null;
          Log.log(Log.INFO,"ReportsAction","listOfMLIPath","Exited");
          return mapping.findForward("success1");
        }
  	}
/////Start Code Added By Path 0n 10Oct06////////
}
