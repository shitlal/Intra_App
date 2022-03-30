/*************************************************************
   *
   * Name of the class: SecuritizationAction.java
   * This is the action class which handles all the actions related to Securitization 
   * module. This class acts as an interface between web layer and the business layer.
   * 
   *
   * @author : Veldurai T
   * @version:  
   * @since: Nov 21, 2003
   **************************************************************/
package com.cgtsi.action;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import com.cgtsi.admin.Administrator;
import com.cgtsi.admin.MenuOptions_back;
import com.cgtsi.admin.User;
import com.cgtsi.common.Constants;
import com.cgtsi.common.Log;
import com.cgtsi.registration.MLIInfo;
import com.cgtsi.registration.Registration;
import com.cgtsi.securitization.Investor;
import com.cgtsi.securitization.LoanPool;
import com.cgtsi.securitization.SecuritizationProcessor;
import com.cgtsi.securitization.SelectCriteria;
import com.cgtsi.securitization.StateWise;
import com.cgtsi.util.SessionConstants;

/**
 * @author Veldurai T
 *
 * This class handles all the requests related to Securitization module.
 * 
 */
public class SecuritizationAction extends BaseAction
{

	/**
	 * This method is used to show the select query options to the user.
	 *  
	 * @param mapping	The ActionMapping object
	 * @param form		The ActionForm object
	 * @param request	The HttpServletRequest object
	 * @param response	The HttpServletResponse object
	 * @return			The ActionForward object
	 * @throws Exception	If any error occurs.
	 */
	public ActionForward showSelectQuery(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"SecuritizationAction","showSelectQuery","Entered");
			
			DynaActionForm dynaForm=(DynaActionForm)form;
			
			HttpSession session=request.getSession(false);
			
			//remove the the loan pool id from the user session.
			session.removeAttribute(SessionConstants.LOAN_POOL_ID);
			
			//set to default values.
			dynaForm.initialize(mapping);
			
			//get all the mlis
			
			Registration registration=new Registration();
			
			ArrayList mlis=registration.getAllMLIs();
			
			ArrayList allMembers=new ArrayList(mlis.size());
			
			for (int i=0;i<mlis.size();i++)
			{
				MLIInfo mliInfo=(MLIInfo)mlis.get(i);
				allMembers.add(mliInfo.getBankId()+mliInfo.getZoneId()+mliInfo.getBranchId()+"("+mliInfo.getShortName()+","+mliInfo.getBankName()+")");
				
				Log.log(Log.DEBUG,"SecuritizationAction","showSelectQuery","MLI at "+i+" is "+(mliInfo.getShortName()+","+mliInfo.getBankName()));
			}
			dynaForm.set("allMembers",allMembers);
			
			//get all the states
			Administrator admin=new Administrator();
			
			ArrayList states=admin.getAllStates();
			
			dynaForm.set("allStates",states);
			
			//get all the sectors
			
			ArrayList industrySectors=admin.getAllIndustrySectors();
			 
			dynaForm.set("allSectors",industrySectors);
			
			Log.log(Log.INFO,"SecuritizationAction","showSelectQuery","Exited");
			return mapping.findForward(Constants.SUCCESS);
		
		}
	/**
	 * This method is used to get the homogeneous loans based on the select query.
	 *  
	 * @param mapping	The ActionMapping object
	 * @param form		The ActionForm object
	 * @param request	The HttpServletRequest object
	 * @param response	The HttpServletResponse object
	 * @return			The ActionForward object
	 * @throws Exception	If any error occurs.
	 */
	
	public ActionForward getHomogenousLoans(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"SecuritizationAction","getHomogenousLoans","Entered");
			
			DynaActionForm dynaForm=(DynaActionForm)form;
			
			String[] mlis=(String[])dynaForm.get("mlis");
			
			
			Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","mlis "+mlis);
			if(mlis!=null)
			{
				Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","mlis length "+mlis.length);
				
				for(int i=0;i<mlis.length;i++)
				{
					Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","mli at "+i+" is "+mlis[i]);
				}
			}

			String[] states=(String[])dynaForm.get("states");
			
			Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","states "+states);
			
			if(states!=null)
			{
				Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","states length "+states.length);

				for(int i=0;i<states.length;i++)
				{
					Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","states at "+i+" is "+states[i]);
				}
			}
			
			String[] sectors=(String[])dynaForm.get("sectors");
			
			
			Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","sectors "+sectors);
			
			if(sectors!=null)
			{
				Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","sectors length "+sectors.length);
				
				for(int i=0;i<sectors.length;i++)
				{
					Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","sectors at "+i+" is "+sectors[i]);
				}
			}			
			
			SelectCriteria selectCriteria=new SelectCriteria();
			
			BeanUtils.populate(selectCriteria,dynaForm.getMap());
			
			boolean isMliAll=false;
			if(mlis!=null)
			{
				Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","mlis length "+mlis.length);
				
				for(int i=0;i<mlis.length;i++)
				{
					Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","mli at "+i+" is "+mlis[i]);
					
					if(mlis[i].equals(Constants.ALL))
					{
						isMliAll=true;
						break;
					}
				}
			}

			Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","states "+states);
			
			boolean isStateAll=false;
			
			if(states!=null)
			{
				Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","states length "+states.length);

				for(int i=0;i<states.length;i++)
				{
					Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","states at "+i+" is "+states[i]);
					
					if(states[i].equals(Constants.ALL))
					{	
						isStateAll=true;
						break;
					}					
				}
			}
			
			Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","sectors "+sectors);
			
			boolean isSectorAll=false;
			
			if(sectors!=null)
			{
				Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","sectors length "+sectors.length);
				
				for(int i=0;i<sectors.length;i++)
				{
					Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","sectors at "+i+" is "+sectors[i]);
					if(sectors[i].equals(Constants.ALL))
					{	
						isSectorAll=true;
						break;
					}					
				}
			}			
		
			if(isMliAll)
			{
				ArrayList allMembers=(ArrayList)dynaForm.get("allMembers");;
				String[] allMemArray=new String[allMembers.size()];
				
				for(int i=0;i<allMembers.size();i++)
				{
					allMemArray[i]=((String)allMembers.get(i)).substring(0,12);
					Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","members... at "+i+" is "+allMemArray[i]);	
				}
				
				selectCriteria.setMlis(allMemArray);
			}
			
			if(isStateAll)
			{
				ArrayList allStates=(ArrayList)dynaForm.get("allStates");
				String []allStatesArray=new String[allStates.size()];
				
				for(int i=0;i<allStates.size();i++)
				{
					allStatesArray[i]=(String)allStates.get(i);	
				}
				
				selectCriteria.setStates(allStatesArray);				
			}
			
			if(isSectorAll)
			{
				ArrayList allSectors=(ArrayList)dynaForm.get("allSectors");;
				String[] allSectorsArray=new String[allSectors.size()];
				
				for(int i=0;i<allSectors.size();i++)
				{
					allSectorsArray[i]=(String)allSectors.get(i);	
				}
				
				selectCriteria.setSectors(allSectorsArray);				
			}
			
			Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","Loan tenure "+selectCriteria.getTenure());
			Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","Effective From "+selectCriteria.getEffectiveDate());
			Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","Loan type "+selectCriteria.getLoanType());
			Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","Interest rate1 "+selectCriteria.getInterestRate());
			Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","Interest rate2 "+selectCriteria.getNextInterestRate());
			Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","Interest type "+selectCriteria.getTypeOfInterest());
			Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","Loan Size1 "+selectCriteria.getLoanSize());
			Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","Loan Size2 "+selectCriteria.getNextLoanSize());
			
			Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","Track Record "+selectCriteria.getTrackRecord());
			
			
				
			SecuritizationProcessor secProcessor=new SecuritizationProcessor();
			
			ArrayList homogeneousLoans=secProcessor.getHomogenousLoans(selectCriteria);
			/*
			MLIWise mliWise=new MLIWise();
			
			mliWise.setMliName("Name1");
			mliWise.setNoOfLoans(5);
			mliWise.setTotalGC(1000000);
			mliWise.setTotalSanctionedAmt(100000);
			
			ArrayList mliWiseData=new ArrayList();
			mliWiseData.add(mliWise);
			*/
			/*
			SectorWise sectorWise=new SectorWise();
			
			sectorWise.setState("Tamil Nadu");
			sectorWise.setNoOfLoans(10);
			sectorWise.setTotalGC(1000000);
			sectorWise.setTotalSanctionedAmt(100000);
			
			SectorWise sectorWise1=new SectorWise();
			
			sectorWise1.setState("Karnataka");
			sectorWise1.setNoOfLoans(20);
			sectorWise1.setTotalGC(2000000);
			sectorWise1.setTotalSanctionedAmt(200000);


			SectorWise sectorWise2=new SectorWise();
			
			sectorWise2.setState("Andhra Pradesh");
			sectorWise2.setNoOfLoans(30);
			sectorWise2.setTotalGC(3000000);
			sectorWise2.setTotalSanctionedAmt(300000);
			
			SectorWise sectorWise3=new SectorWise();
			
			sectorWise3.setState("Kerala");
			sectorWise3.setNoOfLoans(40);
			sectorWise3.setTotalGC(4000000);
			sectorWise3.setTotalSanctionedAmt(400000);
									
			ArrayList stateWiseData=new ArrayList();
			
			stateWiseData.add(sectorWise);
			stateWiseData.add(sectorWise1);
			
			ArrayList stateWiseData1=new ArrayList();
			
			stateWiseData1.add(sectorWise2);
			stateWiseData1.add(sectorWise3);


			HomogeneousLoan homogeneousLoan=new HomogeneousLoan();
			
			homogeneousLoan.setSector("Sector1");
			homogeneousLoan.setStates(stateWiseData);
			
			HomogeneousLoan homogeneousLoan1=new HomogeneousLoan();
			
			homogeneousLoan1.setSector("Sector2");
			homogeneousLoan1.setStates(stateWiseData1);			
			
			ArrayList homogeneousLoans=new ArrayList();
			
			homogeneousLoans.add(homogeneousLoan);
			
			homogeneousLoans.add(homogeneousLoan1);
			
			*/
			
			dynaForm.set("homogeneousLoans",homogeneousLoans);
			
			Log.log(Log.INFO,"SecuritizationAction","getHomogenousLoans","Exited");
			
			return mapping.findForward(Constants.SUCCESS);
		}

	/**
	 * This method is used to create the loan pool after viewing the homogeneous loans.
	 *  
	 * @param mapping	The ActionMapping object
	 * @param form		The ActionForm object
	 * @param request	The HttpServletRequest object
	 * @param response	The HttpServletResponse object
	 * @return			The ActionForward object
	 * @throws Exception	If any error occurs.
	 */
			
	public ActionForward createLoanPool(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"SecuritizationAction","createLoanPool","Entered");
			
			
			DynaActionForm dynaForm=(DynaActionForm)form;
			String[] mlis=(String[])dynaForm.get("mlis");
			
			boolean isMliAll=false;
			if(mlis!=null)
			{
				Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","mlis length "+mlis.length);
				
				for(int i=0;i<mlis.length;i++)
				{
					Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","mli at "+i+" is "+mlis[i]);
					
					if(mlis[i].equals(Constants.ALL))
					{
						isMliAll=true;
						break;
					}
				}
			}

			String[] states=(String[])dynaForm.get("states");
			
			Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","states "+states);
			
			boolean isStateAll=false;
			
			if(states!=null)
			{
				Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","states length "+states.length);

				for(int i=0;i<states.length;i++)
				{
					Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","states at "+i+" is "+states[i]);
					
					if(states[i].equals(Constants.ALL))
					{	
						isStateAll=true;
						break;
					}					
				}
			}
			
			String[] sectors=(String[])dynaForm.get("sectors");
			
			
			Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","sectors "+sectors);
			
			boolean isSectorAll=false;
			
			if(sectors!=null)
			{
				Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","sectors length "+sectors.length);
				
				for(int i=0;i<sectors.length;i++)
				{
					Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","sectors at "+i+" is "+sectors[i]);
					if(sectors[i].equals(Constants.ALL))
					{	
						isSectorAll=true;
						break;
					}					
				}
			}			
			
			SelectCriteria selectCriteria=new SelectCriteria();
			
			BeanUtils.populate(selectCriteria,dynaForm.getMap());
			
			if(isMliAll)
			{
				ArrayList allMembers=(ArrayList)dynaForm.get("allMembers");;
				String[] allMemArray=new String[allMembers.size()];
				
				for(int i=0;i<allMembers.size();i++)
				{
					allMemArray[i]=(String)allMembers.get(i);
					Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","members... at "+i+" is "+allMemArray[i]);	
				}
				
				selectCriteria.setMlis(allMemArray);
			}
			
			if(isStateAll)
			{
				ArrayList allStates=(ArrayList)dynaForm.get("allStates");
				String []allStatesArray=new String[allStates.size()];
				
				for(int i=0;i<allStates.size();i++)
				{
					allStatesArray[i]=(String)allStates.get(i);	
				}
				
				selectCriteria.setStates(allStatesArray);				
			}
			
			if(isSectorAll)
			{
				
				
				ArrayList allSectors=(ArrayList)dynaForm.get("allSectors");;
				String[] allSectorsArray=new String[allSectors.size()];
				
				for(int i=0;i<allSectors.size();i++)
				{
					allSectorsArray[i]=(String)allSectors.get(i);	
				}
				
				selectCriteria.setSectors(allSectorsArray);				
			}			
			LoanPool loanPool=new LoanPool();
			
			BeanUtils.populate(loanPool,dynaForm.getMap());
			
			double poolRate=((Double)dynaForm.get("poolInterestRate")).doubleValue();
			
			loanPool.setInterestRate(poolRate);
			
			if(selectCriteria.getMlis()!=null)
			{
				Log.log(Log.DEBUG,"SecuritizationAction","createLoanPool","mlis length "+selectCriteria.getMlis().length);
				
				for(int i=0;i<selectCriteria.getMlis().length;i++)
				{
					Log.log(Log.DEBUG,"SecuritizationAction","createLoanPool","mli at "+i+" is "+selectCriteria.getMlis()[i]);
				}
			}

			
			Log.log(Log.DEBUG,"SecuritizationAction","createLoanPool","states "+selectCriteria.getStates());
			
			if(selectCriteria.getStates()!=null)
			{
				Log.log(Log.DEBUG,"SecuritizationAction","createLoanPool","states length "+selectCriteria.getStates().length);
				
				for(int i=0;i<selectCriteria.getStates().length;i++)
				{
					Log.log(Log.DEBUG,"SecuritizationAction","createLoanPool","states at "+i+" is "+selectCriteria.getStates()[i]);
				}
			}
			
			
			Log.log(Log.DEBUG,"SecuritizationAction","createLoanPool","sectors "+selectCriteria.getSectors());
			
			if(selectCriteria.getSectors()!=null)
			{
				Log.log(Log.DEBUG,"SecuritizationAction","createLoanPool","sectors length "+selectCriteria.getSectors().length);
				
				for(int i=0;i<selectCriteria.getSectors().length;i++)
				{
					Log.log(Log.DEBUG,"SecuritizationAction","createLoanPool","sectors at "+i+" is "+selectCriteria.getSectors()[i]);
				}
			}			
			
			Log.log(Log.DEBUG,"SecuritizationAction","createLoanPool","interest rate is "+selectCriteria.getInterestRate());
			
			Log.log(Log.DEBUG,"SecuritizationAction","createLoanPool","interest rate is "+selectCriteria.getInterestRate());
			Log.log(Log.DEBUG,"SecuritizationAction","createLoanPool","loan size is "+selectCriteria.getLoanSize());
			Log.log(Log.DEBUG,"SecuritizationAction","createLoanPool","loan type is "+selectCriteria.getLoanType());
			Log.log(Log.DEBUG,"SecuritizationAction","createLoanPool","tenure is "+selectCriteria.getTenure());
			Log.log(Log.DEBUG,"SecuritizationAction","createLoanPool","effective date is "+selectCriteria.getEffectiveDate());
			
			Log.log(Log.DEBUG,"SecuritizationAction","createLoanPool","Track record is "+selectCriteria.getTrackRecord());
			Log.log(Log.DEBUG,"SecuritizationAction","createLoanPool","Type of interest is "+selectCriteria.getTypeOfInterest());
			
			Log.log(Log.DEBUG,"SecuritizationAction","createLoanPool","Loan pool details...");
			
			Log.log(Log.DEBUG,"SecuritizationAction","createLoanPool","Amount securitized "+loanPool.getAmountSecuritized());
			Log.log(Log.DEBUG,"SecuritizationAction","createLoanPool","Interest rate "+loanPool.getInterestRate());
			Log.log(Log.DEBUG,"SecuritizationAction","createLoanPool","Pool Name "+loanPool.getLoanPoolName());
			Log.log(Log.DEBUG,"SecuritizationAction","createLoanPool","Rating "+loanPool.getRating());
			Log.log(Log.DEBUG,"SecuritizationAction","createLoanPool","Rating Agency Name "+loanPool.getRatingAgencyName());
			Log.log(Log.DEBUG,"SecuritizationAction","createLoanPool","SPV Name "+loanPool.getSpvName());
			Log.log(Log.DEBUG,"SecuritizationAction","createLoanPool","Sec. Issue date Name "+loanPool.getSecuritizationIssueDate());
			
			User user=getUserInformation(request);
		
			SecuritizationProcessor processor=new SecuritizationProcessor();
			
			int poolId=processor.createLoanPool(loanPool,selectCriteria,user);
			
			Log.log(Log.DEBUG,"SecuritizationAction","createLoanPool","pool id "+poolId);
			
			HttpSession session=request.getSession(false);
			
			session.setAttribute(SessionConstants.LOAN_POOL_ID,String.valueOf(poolId));
			
			String message="Loan pool created succesfully. Loan pool id is "+poolId;
			
			request.setAttribute("message",message);
			Log.log(Log.INFO,"SecuritizationAction","createLoanPool","Exited");
			
			return mapping.findForward(Constants.SUCCESS);
		}
		
	/**
	 * This method is used to add the investor details for the created loan pool.
	 *  
	 * @param mapping	The ActionMapping object
	 * @param form		The ActionForm object
	 * @param request	The HttpServletRequest object
	 * @param response	The HttpServletResponse object
	 * @return			The ActionForward object
	 * @throws Exception	If any error occurs.
	 */
		
	public ActionForward showInvestor(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"SecuritizationAction","showInvestor","Entered");
			
			SecuritizationProcessor processor=new SecuritizationProcessor();
			ArrayList poolNames=processor.getLoanPoolNames();
			DynaActionForm dynaForm=(DynaActionForm)form;
			dynaForm.initialize(mapping);
			dynaForm.set("poolNames",poolNames);
			
			Log.log(Log.INFO,"SecuritizationAction","showInvestor","Exited");
			return mapping.findForward("success");
		}		
	/**
	 * This method is used to add the investor details for the created loan pool.
	 *  
	 * @param mapping	The ActionMapping object
	 * @param form		The ActionForm object
	 * @param request	The HttpServletRequest object
	 * @param response	The HttpServletResponse object
	 * @return			The ActionForward object
	 * @throws Exception	If any error occurs.
	 */
		
	public ActionForward addInvestor(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"SecuritizationAction","addInvestor","Entered");
			
			DynaActionForm dynaForm=(DynaActionForm)form;
			
			HttpSession session=request.getSession(false);
			
			//int poolId=Integer.parseInt((String)session.getAttribute(SessionConstants.LOAN_POOL_ID));
			
			Investor investor=new Investor();
			
			BeanUtils.populate(investor,dynaForm.getMap());
			
			//String investorName=(String)dynaForm.get("investorName");
			//double investedAmount=((Double)dynaForm.get("investedAmount")).doubleValue();
			
			Log.log(Log.DEBUG,"SecuritizationAction","addInvestor","Investor name "+investor.getInvestorName());
			
			Log.log(Log.DEBUG,"SecuritizationAction","addInvestor","Investor name "+investor.getInvestedAmount());
			Log.log(Log.DEBUG,"SecuritizationAction","addInvestor","Loan Pool name "+investor.getLoanPoolName());
			String loanPoolName=investor.getLoanPoolName();
			int index=loanPoolName.indexOf(")");
			
			int poolId=Integer.parseInt(loanPoolName.substring(0,index));
			
			Log.log(Log.DEBUG,"SecuritizationAction","addInvestor","pool Id "+poolId);
			
			SecuritizationProcessor processor=new SecuritizationProcessor();
			
			processor.addInvestor(poolId,investor);

			//set to default values.
			dynaForm.initialize(mapping);
			
			Log.log(Log.INFO,"SecuritizationAction","addInvestor","Exited");
			String message="Investor added successfully." +
				" <br> <a href="+MenuOptions_back.getMenuAction(MenuOptions_back.SC_ADD_INVESTOR)
				+">Add more Investors!</a>";
			request.setAttribute("message",message);
			
			return mapping.findForward("success");
		}

	/**
	 * This method is used get the state wise homogenous loans if the user selected
	 * the hyper link.
	 *  
	 * @param mapping	The ActionMapping object
	 * @param form		The ActionForm object
	 * @param request	The HttpServletRequest object
	 * @param response	The HttpServletResponse object
	 * @return			The ActionForward object
	 * @throws Exception	If any error occurs.
	 */
					
	public ActionForward getStateWiseLoans(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"SecuritizationAction","getStateWiseLoans","Entered");
			
			DynaActionForm dynaForm=(DynaActionForm)form;
			
			String sector=request.getParameter("sector");
			
			String state=request.getParameter("state");
			
			Log.log(Log.DEBUG,"SecuritizationAction","getStateWiseLoans"," sector,state "+sector+" "+state);
			
			SelectCriteria selectCriteria=new SelectCriteria();
			
			BeanUtils.populate(selectCriteria,dynaForm.getMap());

			String[] mlis=(String[])dynaForm.get("mlis");
			
			boolean isMliAll=false;
			if(mlis!=null)
			{
				Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","mlis length "+mlis.length);
				
				for(int i=0;i<mlis.length;i++)
				{
					Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","mli at "+i+" is "+mlis[i]);
					
					if(mlis[i].equals(Constants.ALL))
					{
						isMliAll=true;
						break;
					}
				}
			}
			
			if(isMliAll)
			{
				ArrayList allMembers=(ArrayList)dynaForm.get("allMembers");;
				String[] allMemArray=new String[allMembers.size()];
				
				for(int i=0;i<allMembers.size();i++)
				{
					allMemArray[i]=(String)allMembers.get(i);
					Log.log(Log.DEBUG,"SecuritizationAction","getHomogenousLoans","members... at "+i+" is "+allMemArray[i]);	
				}
				
				selectCriteria.setMlis(allMemArray);
			}
						
			Log.log(Log.DEBUG,"SecuritizationAction","getStateWiseLoans","states ... "+selectCriteria.getStates());
						
			SecuritizationProcessor securitizationProcessor=new SecuritizationProcessor();
			
			StateWise stateWise=securitizationProcessor.getStateWiseLoans(
									sector,state,selectCriteria);
			
			
			/*
			StateWise stateWise=new StateWise();
			stateWise.setState(state);
			
			MLIWise mliWise=new MLIWise();
			
			mliWise.setMliName("MLI1");
			mliWise.setNoOfLoans(1);
			mliWise.setTotalGC(100);
			mliWise.setTotalSanctionedAmt(100);

			MLIWise mliWise1=new MLIWise();
			
			mliWise1.setMliName("MLI2");
			mliWise1.setNoOfLoans(2);
			mliWise1.setTotalGC(200);
			mliWise1.setTotalSanctionedAmt(200);

			
			ArrayList mliWiseData=new ArrayList();
			mliWiseData.add(mliWise);
			mliWiseData.add(mliWise1);
			
			
			stateWise.setMliWise(mliWiseData);
			
			*/
				
			dynaForm.set("stateWise",stateWise);
			
			
			Log.log(Log.INFO,"SecuritizationAction","getStateWiseLoans","Exited");
			
			return mapping.findForward(Constants.SUCCESS); 
		}

	/**
	 * Added on 10-Sep-2004.
	 * This method is used initialise the loan pool details before prompting the user
	 * to enter loan pool details.
	 *  
	 * @param mapping	The ActionMapping object
	 * @param form		The ActionForm object
	 * @param request	The HttpServletRequest object
	 * @param response	The HttpServletResponse object
	 * @return			The ActionForward object
	 * @throws Exception	If any error occurs.
	 */
					
	public ActionForward showPoolDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"SecuritizationAction","showPoolDetails","Entered");
			
			DynaActionForm dynaForm=(DynaActionForm)form;
			LoanPool loanPool=new LoanPool();
			
			BeanUtils.copyProperties(dynaForm,loanPool);
			
			Log.log(Log.INFO,"SecuritizationAction","showPoolDetails","Exited");
			
			return mapping.findForward(Constants.SUCCESS); 
		}		
		
}

