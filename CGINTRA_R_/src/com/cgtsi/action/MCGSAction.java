/*************************************************************
   *
   * Name of the class: MCGSAction.java
   * This class handles all the requests pertaining to MCGS.
   * 
   *
   * @author : Veldurai T
   * @version:  
   * @since: Nov 18, 2003
   **************************************************************/
package com.cgtsi.action;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import com.cgtsi.admin.Administrator;
import com.cgtsi.admin.User;
import com.cgtsi.application.BorrowerDetails;
import com.cgtsi.application.SSIDetails;
import com.cgtsi.common.Constants;
import com.cgtsi.common.Log;
import com.cgtsi.mcgs.DonorDetail;
import com.cgtsi.mcgs.MCGSProcessor;
import com.cgtsi.mcgs.ParticipatingBank;
import com.cgtsi.registration.MLIInfo;
import com.cgtsi.util.SessionConstants;

/**
 * @author Veldurai T
 *
 * This class is used to intercept the requests pertaining to MCGS.
 * 
 */
public class MCGSAction extends BaseAction {
	
	/**
	 * This method is used to show the participating bank details.
	 * 
	 * @param mapping	The ActionMapping object
	 * @param form		The ActionForm object
	 * @param request	The HttpServletRequest object
	 * @param response	The HttpServeltResponse object
	 * @return	ActionForward	The ActionForward object
	 * @throws Exception	If any error occurs, Exception would be thrown.
	 */
	public ActionForward showParticipatingBank(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"MCGSAction","showParticipatingBank","Entered");
			
			Administrator admin=new Administrator();
			
			ArrayList states=admin.getAllStates();
			
			MCGSProcessor processor=new MCGSProcessor();
			
			ArrayList tempMcgfs=processor.getAllMCGFs();
			
			ArrayList mcgfs=new ArrayList();
			
			for(int i=0;i<tempMcgfs.size();i++)
			{
				MLIInfo member=(MLIInfo)tempMcgfs.get(i);
				String text="("+member.getBankId()+member.getZoneId()+member.getBranchId()+"" +
							")"+member.getShortName()+","+member.getBankName();
				
				mcgfs.add(text);
				
			}
			DynaActionForm dynaForm=(DynaActionForm)form;
			
			dynaForm.initialize(mapping);
			
			dynaForm.set("states",states);
			dynaForm.set("mcgfs",mcgfs);
			
			request.setAttribute(SessionConstants.MCGS_ACTION_FLAG,null);
			
			processor=null;
			admin=null;
			
			Log.log(Log.INFO,"MCGSAction","showParticipatingBank","Exited");
			
			return mapping.findForward(Constants.SUCCESS);
		}
	/**
	 * This method is used to get all the districts for the selected state.
	 * 
	 * @param mapping	The ActionMapping object
	 * @param form		The ActionForm object
	 * @param request	The HttpServletRequest object
	 * @param response	The HttpServeltResponse object
	 * @return	ActionForward	The ActionForward object
	 * @throws Exception	If any error occurs, Exception would be thrown.
	 */
			
	public ActionForward getDistricts(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{	
			
			Log.log(Log.INFO,"MCGSAction","getDistricts","Entered");
			
			Administrator admin=new Administrator();
			
			DynaActionForm dynaForm=(DynaActionForm)form;
			
			//HttpSession session=request.getSession(false);
			
			String state=(String)dynaForm.get("state");
			
			Log.log(Log.DEBUG,"MCGSAction","getDistricts","state "+state);
			
			ArrayList districts=admin.getAllDistricts(state);
			
			Log.log(Log.DEBUG,"MCGSAction","getDistricts","districts "+districts);
			
			dynaForm.set("districts",districts);
			
			request.setAttribute(SessionConstants.MCGS_ACTION_FLAG,"1");		
			
			Log.log(Log.INFO,"MCGSAction","getDistricts","Exited");
			
			return mapping.findForward(Constants.SUCCESS);
		}
		
	/**
	 * This method is used to add the participating bank details into database.
	 * 
	 * @param mapping	The ActionMapping object
	 * @param form		The ActionForm object
	 * @param request	The HttpServletRequest object
	 * @param response	The HttpServeltResponse object
	 * @return	ActionForward	The ActionForward object
	 * @throws Exception	If any error occurs, Exception would be thrown.
	 */
			
	public ActionForward addParticipatingBank(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"MCGSAction","addParticipatingBank","Entered");
			
			DynaActionForm dynaForm=(DynaActionForm)form;
			
			ParticipatingBank participatingBank=new ParticipatingBank();
			
			BeanUtils.populate(participatingBank,dynaForm.getMap());
			Log.log(Log.INFO,"MCGSAction","addParticipatingBank","MCGF Name "+dynaForm.get("mcgf"));
			
			participatingBank.setMemberId((String)dynaForm.get("mcgf"));
			MCGSProcessor mcgsProcessor=new MCGSProcessor();
			
			User user=getUserInformation(request);
			
			mcgsProcessor.addParticipatingBank(participatingBank,user);
			
			/*
			Log.log(Log.DEBUG,"MCGSAction","addParticipatingBank"," Bank Name "+participatingBank.getBankName());
			Log.log(Log.DEBUG,"MCGSAction","addParticipatingBank"," Address "+participatingBank.getAddress());
			Log.log(Log.DEBUG,"MCGSAction","addParticipatingBank"," Branch Name "+participatingBank.getBranchName());
			Log.log(Log.DEBUG,"MCGSAction","addParticipatingBank"," city "+participatingBank.getCity());
			Log.log(Log.DEBUG,"MCGSAction","addParticipatingBank"," Contact Person "+participatingBank.getContactPerson());
			Log.log(Log.DEBUG,"MCGSAction","addParticipatingBank"," Distict "+participatingBank.getDistrict());
			Log.log(Log.DEBUG,"MCGSAction","addParticipatingBank"," Email "+participatingBank.getEmailId());
			Log.log(Log.DEBUG,"MCGSAction","addParticipatingBank"," fax "+participatingBank.getFaxNo());
			Log.log(Log.DEBUG,"MCGSAction","addParticipatingBank"," Ho Address "+participatingBank.getHoAddress());
			Log.log(Log.DEBUG,"MCGSAction","addParticipatingBank"," Phone No "+participatingBank.getPhoneNo());
			Log.log(Log.DEBUG,"MCGSAction","addParticipatingBank"," state Name "+participatingBank.getState());
			Log.log(Log.DEBUG,"MCGSAction","addParticipatingBank"," std code "+participatingBank.getStdCode());
			*/
			
			
			dynaForm.initialize(mapping);
			
			Log.log(Log.INFO,"MCGSAction","addParticipatingBank","Exited");
			
			request.setAttribute("message","Participating Bank Added Successfully");
			
			return mapping.findForward(Constants.SUCCESS);
		}
		
	/**
	 * This method is used to get the mcgs informations and display a screen to the 
	 * user to enter donor details.
	 * 
	 * @param mapping	The ActionMapping object
	 * @param form		The ActionForm object
	 * @param request	The HttpServletRequest object
	 * @param response	The HttpServeltResponse object
	 * @return	ActionForward	The ActionForward object
	 * @throws Exception	If any error occurs, Exception would be thrown.
	 */
			
	public ActionForward showAddDonorDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			
			Log.log(Log.INFO,"MCGSAction","showAddDonorDetails","Entered");
			
			MCGSProcessor processor=new MCGSProcessor();
			
			ArrayList tempMcgfs=processor.getAllMCGFs();
			
			ArrayList mcgfs=new ArrayList();
			
			for(int i=0;i<tempMcgfs.size();i++)
			{
				MLIInfo member=(MLIInfo)tempMcgfs.get(i);
				String text="("+member.getBankId()+member.getZoneId()+member.getBranchId()+"" +
							")"+member.getShortName()+","+member.getBankName();
				
				mcgfs.add(text);
				
			}
			DynaActionForm dynaForm=(DynaActionForm)form;
			
			dynaForm.initialize(mapping);
			
			dynaForm.set("mcgfs",mcgfs);
			
			Log.log(Log.INFO,"MCGSAction","showAddDonorDetails","Exited");
			
			return mapping.findForward("success");
		}
		
			
	/**
	 * This method is used to add the donor details into database.
	 * 
	 * @param mapping	The ActionMapping object
	 * @param form		The ActionForm object
	 * @param request	The HttpServletRequest object
	 * @param response	The HttpServeltResponse object
	 * @return	ActionForward	The ActionForward object
	 * @throws Exception	If any error occurs, Exception would be thrown.
	 */
			
	public ActionForward addDonorDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			
			Log.log(Log.INFO,"MCGSAction","addDonorDetails","Entered");
			
			MCGSProcessor mcgsProcessor=new MCGSProcessor();
			
			DynaActionForm dynaForm=(DynaActionForm)form;
			
			Log.log(Log.DEBUG,"MCGSAction","addDonorDetails"," corpus contribution "+dynaForm.get("corpusContributionAmt"));
			
			DonorDetail donorDetail=new DonorDetail();
			
			Log.log(Log.INFO,"MCGSAction","addDonorDetails","MCGF Name "+dynaForm.get("mcgf"));
			
			BeanUtils.populate(donorDetail,dynaForm.getMap()); 
			donorDetail.setMemberId((String)dynaForm.get("mcgf"));
			
			User user=getUserInformation(request);

 
			Log.log(Log.DEBUG,"MCGSAction","addDonorDetails"," Address "+donorDetail.getAddress());
			Log.log(Log.DEBUG,"MCGSAction","addDonorDetails"," Name "+donorDetail.getName());
			Log.log(Log.DEBUG,"MCGSAction","addDonorDetails"," corpus contribution "+donorDetail.getCorpusContributionAmt());
			Log.log(Log.DEBUG,"MCGSAction","addDonorDetails"," contribution date "+donorDetail.getCorpusContributionDate());
						
			mcgsProcessor.addDonorDetails(donorDetail,user);
			
			dynaForm.initialize(mapping);
			
			Log.log(Log.INFO,"MCGSAction","addDonorDetails","Exited");
			
			request.setAttribute("message","Donor Details Added Successfully");
			
			return mapping.findForward(Constants.SUCCESS);		
		}

	/**
	 * This method is used to show the SSI Member details.
	 * 
	 * @param mapping	The ActionMapping object
	 * @param form		The ActionForm object
	 * @param request	The HttpServletRequest object
	 * @param response	The HttpServeltResponse object
	 * @return	ActionForward	The ActionForward object
	 * @throws Exception	If any error occurs, Exception would be thrown.
	 */
	
	public ActionForward showSSIMemberDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			
			Log.log(Log.INFO,"MCGSAction","showSSIMemberDetails","Entered");
			
			
			Administrator admin=new Administrator();
			ArrayList states=admin.getAllStates();
			
			ArrayList industryNatures=admin.getAllIndustryNature();
			
			ArrayList socialCategories=admin.getAllSocialCategories();
			MCGSProcessor processor=new MCGSProcessor();
			
			ArrayList tempMcgfs=processor.getAllMCGFs();
			
			ArrayList mcgfs=new ArrayList();
			
			for(int i=0;i<tempMcgfs.size();i++)
			{
				MLIInfo member=(MLIInfo)tempMcgfs.get(i);
				String text="("+member.getBankId()+member.getZoneId()+member.getBranchId()+"" +
							")"+member.getShortName()+","+member.getBankName();
				
				mcgfs.add(text);
				
			}			
			DynaActionForm dynaForm=(DynaActionForm)form;
			dynaForm.initialize(mapping);
			dynaForm.set("states",states);
			dynaForm.set("industryNatures",industryNatures);
			dynaForm.set("socialCategories",socialCategories);
			dynaForm.set("mcgfs",mcgfs); 
			
			Log.log(Log.INFO,"MCGSAction","showSSIMemberDetails","Exited");
			
			return mapping.findForward(Constants.SUCCESS);
		}
		
	/**
	 * This method is used to get all the industry sectors.
	 * 
	 * @param mapping	The ActionMapping object
	 * @param form		The ActionForm object
	 * @param request	The HttpServletRequest object
	 * @param response	The HttpServeltResponse object
	 * @return	ActionForward	The ActionForward object
	 * @throws Exception	If any error occurs, Exception would be thrown.
	 */
			
	public ActionForward getIndustrySectors(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"MCGSAction","getIndustrySectors","Entered");
			
			DynaActionForm dynaForm=(DynaActionForm)form;
			
			String industryNature=(String)dynaForm.get("industryNature");
			
			Log.log(Log.DEBUG,"MCGSAction","getIndustrySectors","industryNature "+industryNature);
			
			Administrator admin=new Administrator();
			ArrayList industrySectors = new ArrayList();
			if(!industryNature.equals(""))
			{
				industrySectors=admin.getIndustrySectors(industryNature);
			}
			else{
				industrySectors.clear();
			}
			
			dynaForm.set("industrySectors",industrySectors);
			
			request.setAttribute(SessionConstants.MCGS_ACTION_FLAG,"2");
			
			Log.log(Log.INFO,"MCGSAction","getIndustrySectors","Exited");
			
			return mapping.findForward(Constants.SUCCESS);
		}
	
	/**
	 * This method is used to add the SSI Member details.
	 * 
	 * @param mapping	The ActionMapping object
	 * @param form		The ActionForm object
	 * @param request	The HttpServletRequest object
	 * @param response	The HttpServeltResponse object
	 * @return	ActionForward	The ActionForward object
	 * @throws Exception	If any error occurs, Exception would be thrown.
	 */
		
	public ActionForward addSSIMemberDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception 
		{
			Log.log(Log.INFO,"MCGSAction","addSSIMemberDetails","Entered");
			
			DynaActionForm dynaForm=(DynaActionForm)form;
			
			Log.log(Log.INFO,"MCGSAction","addSSIMemberDetails","MCGF Name "+dynaForm.get("mcgf"));
			
			BorrowerDetails borrowerDetails=new BorrowerDetails();
			
			BeanUtils.populate(borrowerDetails,dynaForm.getMap());
			
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","Ac no"+borrowerDetails.getAcNo());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","Assisted by bank "+borrowerDetails.getAssistedByBank());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","NPA "+borrowerDetails.getNpa());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","Os Amount "+borrowerDetails.getOsAmt());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","Previously covered "+borrowerDetails.getPreviouslyCovered());
			
			
			SSIDetails ssiDetails=new SSIDetails();
			
			BeanUtils.populate(ssiDetails,dynaForm.getMap());
			
		
			if(ssiDetails.getDistrict()==null || ssiDetails.getDistrict().equals("")
			|| ssiDetails.getDistrict().equals("Others"))
			{
				Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","Original Dt is  "+ssiDetails.getDistrict());
				
				//Other distict is entered.
				String district=(String)dynaForm.get("districtOthers");
				
				ssiDetails.setDistrict(district);
			}
			
			String idType=(String)dynaForm.get("idType");
			
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","idType is  "+idType);
			
			if(idType!=null && !idType.equals("none"))
			{
				Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","idType is  "+dynaForm.get("idTypeOther"));
				
				ssiDetails.setCgbid((String)dynaForm.get("idTypeOther"));
			}
			
			if(ssiDetails.getConstitution()==null || ssiDetails.getConstitution().equals("")
			|| ssiDetails.getConstitution().equals("Others"))
			{
				Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","Constituition is  "+ssiDetails.getConstitution());
				
				ssiDetails.setConstitution((String)dynaForm.get("constitutionOther"));
			}
			
			
			if(ssiDetails.getCpLegalID()==null || ssiDetails.getCpLegalID().equals("")
				|| ssiDetails.getCpLegalID().equals("Others"))
			{
				Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","Legal Id is "+ssiDetails.getCpLegalID());
				
				ssiDetails.setCpLegalID((String)dynaForm.get("otherCpLegalID"));
			}
			
			borrowerDetails.setSsiDetails(ssiDetails);
			
						
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","Activity Type "+ssiDetails.getActivityType());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","Address "+ssiDetails.getAddress());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","Borrower Ref No "+ssiDetails.getBorrowerRefNo());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","CGBID "+ssiDetails.getCgbid());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","city "+ssiDetails.getCity());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","Constituition "+ssiDetails.getConstitution());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","Constituition other "+ssiDetails.getConstitutionOther());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","First name "+ssiDetails.getCpFirstName());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","Gender "+ssiDetails.getCpGender());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","ITPAN "+ssiDetails.getCpITPAN());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","Last name "+ssiDetails.getCpLastName());
			
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","Legal Id "+ssiDetails.getCpLegalID());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","Legal Id Value "+ssiDetails.getCpLegalIdValue());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","Middle Name "+ssiDetails.getCpMiddleName());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","Title "+ssiDetails.getCpTitle());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","District "+ssiDetails.getDistrict());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","No of employees "+ssiDetails.getEmployeeNos());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","First ITPAN "+ssiDetails.getFirstItpan());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","First name "+ssiDetails.getFirstName());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","Industry Nature "+ssiDetails.getIndustryNature());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","Industry sector "+ssiDetails.getIndustrySector());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","Pin Code "+ssiDetails.getPincode());
			
			
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","Project exports "+ssiDetails.getProjectedExports());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","Turn over "+ssiDetails.getProjectedSalesTurnover());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","Reg No "+ssiDetails.getRegNo());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","Second ITPAN "+ssiDetails.getSecondItpan());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","second name "+ssiDetails.getSecondName());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","SSI ITPAN "+ssiDetails.getSsiITPan());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","SSI Name "+ssiDetails.getSsiName());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","SSI type "+ssiDetails.getSsiType());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","State "+ssiDetails.getState());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","Second DOB "+ssiDetails.getSecondDOB());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","Third ITPAN "+ssiDetails.getThirdItpan());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","Third Name "+ssiDetails.getThirdName());
			
			
			
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","Commencement Date "+ssiDetails.getCommencementDate());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","CP DOB "+ssiDetails.getCpDOB());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","First DOB "+ssiDetails.getFirstDOB());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","Third DOB "+ssiDetails.getThirdDOB());

			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","Corpus Contribution Amt "+ssiDetails.getCorpusContributionAmt());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","Corpus Contribution Date "+ssiDetails.getCorpusContributionDate());


			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","Display Default list "+ssiDetails.getDisplayDefaultersList());
			Log.log(Log.DEBUG,"MCGSAction","addSSIMemberDetails","social category "+ssiDetails.getSocialCategory());

			
			//Call the appropriate classes to insert the ssi member details.
			MCGSProcessor mcgsProcessor=new MCGSProcessor();
			
			String memberId=((String)dynaForm.get("mcgf")).substring(1,13);
			
			
			User user=getUserInformation(request);
			
			mcgsProcessor.addSSIMembers(borrowerDetails,memberId,user);
			
			dynaForm.initialize(mapping);
			Log.log(Log.INFO,"MCGSAction","addSSIMemberDetails","Exited");
			
			request.setAttribute("message","SSI Members Added Successfully");			
			
			return mapping.findForward(Constants.SUCCESS);	
		}
	/**
	 * This method is used to get all the MCGF while displaying in the 
	 * MCGF default screen.
	 * 
	 * @param mapping	The ActionMapping object
	 * @param form		The ActionForm object
	 * @param request	The HttpServletRequest object
	 * @param response	The HttpServeltResponse object
	 * @return	ActionForward	The ActionForward object
	 * @throws Exception	If any error occurs, Exception would be thrown.
	 */
			
	public ActionForward showMCGFDefault(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{		
			Log.log(Log.INFO,"MCGSAction","showMCGFDefault","Entered");
			
			//get all the active mcgs list.
			MCGSProcessor mcgsProcessor=new MCGSProcessor();
			ArrayList activeMCGFs=mcgsProcessor.getAllMCGFs();
			
			ArrayList activeMCGFList=new ArrayList();
			
			for(int i=0;i<activeMCGFs.size();i++)
			{
				MLIInfo mliInfo=(MLIInfo)activeMCGFs.get(i);
				
				String id=mliInfo.getBankId()+mliInfo.getZoneId()+mliInfo.getBranchId();
				id+=", "+mliInfo.getBankName();
				
				activeMCGFList.add(id);
			}
			
			DynaActionForm dynaForm=(DynaActionForm)form;
			
			dynaForm.initialize(mapping);
			
			dynaForm.set("mcgfs",activeMCGFList);
			
			Log.log(Log.INFO,"MCGSAction","showMCGFDefault","Exited");
			return mapping.findForward(Constants.SUCCESS);
		}
	/**
	 * This method is used to update the MCGF status.
	 * 
	 * @param mapping	The ActionMapping object
	 * @param form		The ActionForm object
	 * @param request	The HttpServletRequest object
	 * @param response	The HttpServeltResponse object
	 * @return	ActionForward	The ActionForward object
	 * @throws Exception	If any error occurs, Exception would be thrown.
	 */
			
	public ActionForward updateMCGFStatus(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"MCGSAction","updateMCGFStatus","Entered");
			
			MCGSProcessor mcgsProcessor=new MCGSProcessor();
			
			DynaActionForm dynaForm=(DynaActionForm)form;
			
			String mcgfId=(String)dynaForm.get("mcgfName");
			
			String reason=(String)dynaForm.get("reason");
			
			User user=getUserInformation(request);
			
			mcgsProcessor.updateMCGFStatus(mcgfId,reason,user);			
			
			Log.log(Log.DEBUG,"MCGSAction","updateMCGFStatus","mcgfName,reason "+mcgfId+" "+reason);
			
			//mcgfProcessor.updateMCGFStatus(mcgfName,reason,userId);
			Log.log(Log.INFO,"MCGSAction","updateMCGFStatus","Exited");
			
			request.setAttribute("message","MCGF Status Updated Successfully");
			
			return mapping.findForward(Constants.SUCCESS);
		}
}
