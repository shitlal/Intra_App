package com.cgtsi.actionform;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
//import java.util.HashMap;
import java.util.Date;
import java.util.TreeMap;
import org.apache.struts.validator.ValidatorActionForm;

public class APForm extends ValidatorActionForm
{
	
	HashMap<String,Double> colleralAmtMap = new HashMap<String,Double>();

	 
	 
	public HashMap<String, Double> getColleralAmtMap() {
		return colleralAmtMap;
	}

 
	public void setColleralAmtMap(HashMap<String, Double> itemMap) {
		// TODO Auto-generated method stub
		this.colleralAmtMap = itemMap;
		
	}
	
	private Map clearStatus = new TreeMap();
	private Map duplicateStatus = new TreeMap();
	private Map ineligibleStatus = new TreeMap();
	private Map ineligibleDupStatus = new TreeMap();
	
	private Map clearRemarks = new TreeMap();
	private Map duplicateRemarks = new TreeMap();
	private Map ineligibleRemarks = new TreeMap();
	private Map ineligibleDupRemarks = new TreeMap();
	
	private Map clearAppRefNo = new TreeMap();
	private Map duplicateAppRefNo = new TreeMap();
	private Map ineligibleAppRefNo = new TreeMap();
	private Map ineligibleDupAppRefNo = new TreeMap();
	
	private Map clearApprovedAmt = new TreeMap();
    private Map clearRsfApprovedAmt = new TreeMap();
	private Map duplicateApprovedAmt = new TreeMap();
	private Map ineligibleApprovedAmt = new TreeMap();
	private Map ineligibleDupApprovedAmt = new TreeMap();
	
	private Map clearCreditAmt = new TreeMap();
	private Map duplicateCreditAmt = new TreeMap();
	private Map ineligibleCreditAmt = new TreeMap();
	private Map ineligibleDupCreditAmt = new TreeMap();
	
	private Map reApprovedAmt = new TreeMap();
	private Map reApprovalRemarks = new TreeMap();
	private Map cgpanNo = new TreeMap();
	private Map reapprovalStatus = new TreeMap();
	private Map eligibleCreditAmt = new TreeMap();
	private Map creditAmt = new TreeMap();
	
	private Map tempMap = new TreeMap();
	private Map clearTempMap = new TreeMap();
        private Map clearRsfTempMap = new TreeMap();
	private Map dupTempMap = new TreeMap();
	private Map ineligibleTempMap = new TreeMap();
	
	private Map tempRemMap = new TreeMap();
	private Map clearRemMap = new TreeMap();
         private Map clearRsfRemMap = new TreeMap();
	private Map dupRemMap = new TreeMap();
	private Map ineligibleRemMap = new TreeMap();
	
	
	private Map ineligibleReapproveMap = new TreeMap();
	private Map eligibleReapproveMap = new TreeMap();
	
//	For Application conversion
	
	private Map tcAppRefNo = new TreeMap();
	private Map tcCgpan = new TreeMap();
	private Map tcDecision = new TreeMap();

	private Map wcAppRefNo = new TreeMap();
	private Map wcCgpan = new TreeMap();
	private Map wcDecision = new TreeMap();
	
	private Map appRefNo = new TreeMap();
//***************************************//
	private String withinMlis="";

	private ArrayList duplicateApplications = null;

	private ArrayList duplicateApplication =new ArrayList();
	
	private ArrayList holdApplications = new ArrayList();
	private ArrayList rejectedApplications = new ArrayList();
	private ArrayList approvedApplications = new ArrayList();
	private ArrayList pendingApplications = new ArrayList();

	private ArrayList tcClearApplications = null;
	private ArrayList wcClearApplications = null;

	private ArrayList specialMessagesList = new ArrayList();
	
	private ArrayList eligibleAppList=new ArrayList();
	
	private ArrayList eligibleNonDupApps=new ArrayList();
        private ArrayList eligibleNonDupRsfApps=new ArrayList();
	private ArrayList eligibleDupApps=new ArrayList();
	private ArrayList ineligibleNonDupApps=new ArrayList();
	private ArrayList ineligibleApps=new ArrayList();
	private ArrayList duplicateApps=new ArrayList();
	private ArrayList ineligibleDupApps=new ArrayList();
	
	private int intApplicationCount;
	
	//Arraylists for display 
	private ArrayList clearApprovedApplications = new ArrayList();
	private ArrayList clearHoldApplications = new ArrayList();
	private ArrayList clearRejectedApplications = new ArrayList();
	private ArrayList clearPendingApplications = new ArrayList();
	
	private ArrayList dupApprovedApplications = new ArrayList();
	private ArrayList dupHoldApplications = new ArrayList();
	private ArrayList dupRejectedApplications = new ArrayList();
	private ArrayList dupPendingApplications = new ArrayList();
	
	private ArrayList ineligibleApprovedApplications = new ArrayList();
	private ArrayList ineligibleHoldApplications = new ArrayList();
	private ArrayList ineligibleRejectedApplications = new ArrayList();
	private ArrayList ineligiblePendingApplications = new ArrayList();
	
	private ArrayList ineligibleDupApprovedApplications = new ArrayList();
	private ArrayList ineligibleDupHoldApplications = new ArrayList();
	private ArrayList ineligibleDupRejectedApplications = new ArrayList();
	private ArrayList ineligibleDupPendingApplications = new ArrayList();
	
	private ArrayList tcApplications = new ArrayList();
	private ArrayList wcApplications = new ArrayList();
	
	private ArrayList tcConvertedApplications = new ArrayList();
	private ArrayList enhanceConvertedApplications = new ArrayList();
	private ArrayList renewConvertedApplications = new ArrayList();

	private int tcEntryIndex;
	private int wcEntryIndex;
  
        private String cgpan;
  
         private String remarks;
	

	public ArrayList getDuplicateApplications()
	{
		return duplicateApplications;
	}

	public void setDuplicateApplications(ArrayList duplicateList)
	{
		duplicateApplications = duplicateList;
	}


public void setCgpan(String cgpan){

this.cgpan = cgpan;
}

public String getCgpan(){
 return cgpan;
}

public void setRemarks(String remark)
{
  remarks = remark;
}

public String getRemarks(){
 return remarks;
}

/*	public Map getStatus()
	{
		return status;
	}

	public void setStatus(Map aStatus)
	{
		status = aStatus;
	}

	public Map getRemarks()
	{
		return remarks;
	}

	public void setRemarks(Map aRemarks)
	{
		remarks = aRemarks;
	}
*/
	public String getWithinMlis()
	{
		return withinMlis;
	}

	public void setWithinMlis(String aWithinMlis)
	{
		withinMlis=aWithinMlis;
	}

	public Map getAppRefNo()
	{
		return appRefNo;
	}

	public void setAppRefNo(Map aAppRefNo)
	{
		appRefNo = aAppRefNo;
	}

/*	public Map getApprovedAmt()
	{
		return approvedAmt;
	}	

	public void setApprovedAmt(Map aApprovedAmt)
	{
		approvedAmt = aApprovedAmt;
	}
*/
	public Map getReApprovedAmt()
	{
		return reApprovedAmt;
	}

	public void setReApprovedAmt(Map aReApprovedAmt)
	{
		reApprovedAmt = aReApprovedAmt;
	}

	public void setDuplicateApplication(ArrayList aDuplicateApplication)
	{
		duplicateApplication=aDuplicateApplication;
	}

	public ArrayList getDuplicateApplication()
	{
		return duplicateApplication;
	}


	public void setHoldApplications(ArrayList aHoldApplications)
	{
		holdApplications=aHoldApplications;
	}

	public ArrayList getHoldApplications()
	{
		return holdApplications;
	}

	public void setRejectedApplications(ArrayList aRejectedApplications)
	{
		rejectedApplications=aRejectedApplications;
	}

	public ArrayList getRejectedApplications()
	{
		return rejectedApplications;
	}


	public void setApprovedApplications(ArrayList aApprovedApplications)
	{
		approvedApplications=aApprovedApplications;
	}

	public ArrayList getApprovedApplications()
	{
		return approvedApplications;
	}


	public void setTcClearApplications(ArrayList aTcClearApplications)
	{
		tcClearApplications=aTcClearApplications;
	}

	public ArrayList getTcClearApplications()
	{
		return tcClearApplications;
	}

	public void setWcClearApplications(ArrayList aWcClearApplications)
	{
		wcClearApplications=aWcClearApplications;
	}

	public ArrayList getWcClearApplications()
	{
		return wcClearApplications;
	}

	public void setSpecialMessagesList(ArrayList aSpecialMessagesList)
	{
		specialMessagesList=aSpecialMessagesList;
	}

	public ArrayList getSpecialMessagesList()
	{
		return specialMessagesList;
	}
	/**
	 * @return
	 */
	public ArrayList getEligibleAppList() {
		return eligibleAppList;
	}

	/**
	 * @param list
	 */
	public void setEligibleAppList(ArrayList aEligibleAppList) {
		eligibleAppList = aEligibleAppList;
	}

	/**
	 * @return
	 */
	public ArrayList getEligibleDupApps() {
		return eligibleDupApps;
	}

	/**
	 * @return
	 */
	public ArrayList getEligibleNonDupApps() {
		return eligibleNonDupApps;
	}

	/**
	 * @return
	 */
	public ArrayList getEligibleNonDupRsfApps() {
		return eligibleNonDupRsfApps;
	}
	/**
	 * @return
	 */
	public ArrayList getIneligibleApps() {
		return ineligibleApps;
	}

	/**
	 * @return
	 */
	public ArrayList getIneligibleNonDupApps() {
		return ineligibleNonDupApps;
	}

	/**
	 * @param list
	 */
	public void setEligibleDupApps(ArrayList list) {
		eligibleDupApps = list;
	}

	/**
	 * @param list
	 */
	public void setEligibleNonDupApps(ArrayList list) {
		eligibleNonDupApps = list;
	}

/**
	 * @param list
	 */
	public void setEligibleNonDupRsfApps(ArrayList list) {
		eligibleNonDupRsfApps = list;
	}
	/**
	 * @param list
	 */
	public void setIneligibleApps(ArrayList list) {
		ineligibleApps = list;
	}

	/**
	 * @param list
	 */
	public void setIneligibleNonDupApps(ArrayList list) {
		ineligibleNonDupApps = list;
	}

	/**
	 * @return
	 */
	public ArrayList getDuplicateApps() {
		return duplicateApps;
	}

	/**
	 * @param list
	 */
	public void setDuplicateApps(ArrayList list) {
		duplicateApps = list;
	}

	/**
	 * @return
	 */
	public ArrayList getIneligibleDupApps() {
		return ineligibleDupApps;
	}

	/**
	 * @param list
	 */
	public void setIneligibleDupApps(ArrayList list) {
		ineligibleDupApps = list;
	}

	/**
	 * @return
	 */
	public int getIntApplicationCount() {
		return intApplicationCount;
	}

	/**
	 * @param i
	 */
	public void setIntApplicationCount(int aIntApplicationCount) {
		intApplicationCount = aIntApplicationCount;
	}

	/**
	 * @return
	 */
	public Map getClearAppRefNo() {
		return clearAppRefNo;
	}

	/**
	 * @return
	 */
	public Map getClearApprovedAmt() {
		return clearApprovedAmt;
	}

/**
	 * @return
	 */
	public Map getClearRsfApprovedAmt() {
		return clearRsfApprovedAmt;
	}
	/**
	 * @return
	 */
	public Map getClearRemarks() {
		return clearRemarks;
	}

	/**
	 * @return
	 */
	public Map getClearStatus() {
		return clearStatus;
	}

	/**
	 * @return
	 */
	public Map getDuplicateAppRefNo() {
		return duplicateAppRefNo;
	}

	/**
	 * @return
	 */
	public Map getDuplicateApprovedAmt() {
		return duplicateApprovedAmt;
	}

	/**
	 * @return
	 */
	public Map getDuplicateRemarks() {
		return duplicateRemarks;
	}

	/**
	 * @return
	 */
	public Map getDuplicateStatus() {
		return duplicateStatus;
	}

	/**
	 * @return
	 */
	public Map getIneligibleAppRefNo() {
		return ineligibleAppRefNo;
	}

	/**
	 * @return
	 */
	public Map getIneligibleApprovedAmt() {
		return ineligibleApprovedAmt;
	}

	/**
	 * @return
	 */
	public Map getIneligibleDupAppRefNo() {
		return ineligibleDupAppRefNo;
	}

	/**
	 * @return
	 */
	public Map getIneligibleDupApprovedAmt() {
		return ineligibleDupApprovedAmt;
	}

	/**
	 * @return
	 */
	public Map getIneligibleDupRemarks() {
		return ineligibleDupRemarks;
	}

	/**
	 * @return
	 */
	public Map getIneligibleDupStatus() {
		return ineligibleDupStatus;
	}

	/**
	 * @return
	 */
	public Map getIneligibleRemarks() {
		return ineligibleRemarks;
	}

	/**
	 * @return
	 */
	public Map getIneligibleStatus() {
		return ineligibleStatus;
	}

	/**
	 * @param map
	 */
	public void setClearAppRefNo(Map map) {
		clearAppRefNo = map;
	}

	/**
	 * @param map
	 */
	public void setClearApprovedAmt(Map map) {
		clearApprovedAmt = map;
	}

/**
	 * @param map
	 */
	public void setClearRsfApprovedAmt(Map map) {
		clearRsfApprovedAmt = map;
	}
	/**
	 * @param map
	 */
	public void setClearRemarks(Map map) {
		clearRemarks = map;
	}

	/**
	 * @param map
	 */
	public void setClearStatus(Map map) {
		clearStatus = map;
	}

	/**
	 * @param map
	 */
	public void setDuplicateAppRefNo(Map map) {
		duplicateAppRefNo = map;
	}

	/**
	 * @param map
	 */
	public void setDuplicateApprovedAmt(Map map) {
		duplicateApprovedAmt = map;
	}

	/**
	 * @param map
	 */
	public void setDuplicateRemarks(Map map) {
		duplicateRemarks = map;
	}

	/**
	 * @param map
	 */
	public void setDuplicateStatus(Map map) {
		duplicateStatus = map;
	}

	/**
	 * @param map
	 */
	public void setIneligibleAppRefNo(Map map) {
		ineligibleAppRefNo = map;
	}

	/**
	 * @param map
	 */
	public void setIneligibleApprovedAmt(Map map) {
		ineligibleApprovedAmt = map;
	}

	/**
	 * @param map
	 */
	public void setIneligibleDupAppRefNo(Map map) {
		ineligibleDupAppRefNo = map;
	}

	/**
	 * @param map
	 */
	public void setIneligibleDupApprovedAmt(Map map) {
		ineligibleDupApprovedAmt = map;
	}

	/**
	 * @param map
	 */
	public void setIneligibleDupRemarks(Map map) {
		ineligibleDupRemarks = map;
	}

	/**
	 * @param map
	 */
	public void setIneligibleDupStatus(Map map) {
		ineligibleDupStatus = map;
	}

	/**
	 * @param map
	 */
	public void setIneligibleRemarks(Map map) {
		ineligibleRemarks = map;
	}

	/**
	 * @param map
	 */
	public void setIneligibleStatus(Map map) {
		ineligibleStatus = map;
	}

	/**
	 * @return
	 */
	public ArrayList getClearApprovedApplications() {
		return clearApprovedApplications;
	}

	/**
	 * @return
	 */
	public ArrayList getClearHoldApplications() {
		return clearHoldApplications;
	}

	/**
	 * @return
	 */
	public ArrayList getClearRejectedApplications() {
		return clearRejectedApplications;
	}

	/**
	 * @return
	 */
	public ArrayList getDupApprovedApplications() {
		return dupApprovedApplications;
	}

	/**
	 * @return
	 */
	public ArrayList getDupHoldApplications() {
		return dupHoldApplications;
	}

	/**
	 * @return
	 */
	public ArrayList getDupRejectedApplications() {
		return dupRejectedApplications;
	}

	/**
	 * @return
	 */
	public ArrayList getIneligibleApprovedApplications() {
		return ineligibleApprovedApplications;
	}

	/**
	 * @return
	 */
	public ArrayList getIneligibleDupApprovedApplications() {
		return ineligibleDupApprovedApplications;
	}

	/**
	 * @return
	 */
	public ArrayList getIneligibleDupHoldApplications() {
		return ineligibleDupHoldApplications;
	}

	/**
	 * @return
	 */
	public ArrayList getIneligibleDupRejectedApplications() {
		return ineligibleDupRejectedApplications;
	}

	/**
	 * @return
	 */
	public ArrayList getIneligibleHoldApplications() {
		return ineligibleHoldApplications;
	}

	/**
	 * @return
	 */
	public ArrayList getIneligibleRejectedApplications() {
		return ineligibleRejectedApplications;
	}

	/**
	 * @param list
	 */
	public void setClearApprovedApplications(ArrayList aClearApprovedApplications) {
		clearApprovedApplications = aClearApprovedApplications;
	}

	/**
	 * @param list
	 */
	public void setClearHoldApplications(ArrayList aClearHoldApplications) {
		clearHoldApplications = aClearHoldApplications;
	}

	/**
	 * @param list
	 */
	public void setClearRejectedApplications(ArrayList aClearRejectedApplications) {
		clearRejectedApplications = aClearRejectedApplications;
	}

	/**
	 * @param list
	 */
	public void setDupApprovedApplications(ArrayList aDupApprovedApplications) {
		dupApprovedApplications = aDupApprovedApplications;
	}

	/**
	 * @param list
	 */
	public void setDupHoldApplications(ArrayList aDupHoldApplications) {
		dupHoldApplications = aDupHoldApplications;
	}

	/**
	 * @param list
	 */
	public void setDupRejectedApplications(ArrayList aDupRejectedApplications) {
		dupRejectedApplications = aDupRejectedApplications;
	}

	/**
	 * @param list
	 */
	public void setIneligibleApprovedApplications(ArrayList aIneligibleApprovedApplications) {
		ineligibleApprovedApplications = aIneligibleApprovedApplications;
	}

	/**
	 * @param list
	 */
	public void setIneligibleDupApprovedApplications(ArrayList aIneligibleDupApprovedApplications) {
		ineligibleDupApprovedApplications = aIneligibleDupApprovedApplications;
	}

	/**
	 * @param list
	 */
	public void setIneligibleDupHoldApplications(ArrayList aIneligibleDupHoldApplications) {
		ineligibleDupHoldApplications = aIneligibleDupHoldApplications;
	}

	/**
	 * @param list
	 */
	public void setIneligibleDupRejectedApplications(ArrayList aIneligibleDupRejectedApplications) {
		ineligibleDupRejectedApplications = aIneligibleDupRejectedApplications;
	}

	/**
	 * @param list
	 */
	public void setIneligibleHoldApplications(ArrayList aIneligibleHoldApplications) {
		ineligibleHoldApplications = aIneligibleHoldApplications;
	}

	/**
	 * @param list
	 */
	public void setIneligibleRejectedApplications(ArrayList list) {
		ineligibleRejectedApplications = list;
	}

	/**
	 * @return
	 */
	public Map getReApprovalRemarks() {
		return reApprovalRemarks;
	}

	/**
	 * @param map
	 */
	public void setReApprovalRemarks(Map aReApprovalRemarks) {
		reApprovalRemarks = aReApprovalRemarks;
	}

	/**
	 * @return
	 */
	public Map getCgpanNo() {
		return cgpanNo;
	}

	/**
	 * @param map
	 */
	public void setCgpanNo(Map aCgpanNo) {
		cgpanNo = aCgpanNo;
	}

	/**
	 * @return
	 */
	public Map getReapprovalStatus() {
		return reapprovalStatus;
	}

	/**
	 * @param map
	 */
	public void setReapprovalStatus(Map aReapprovalStatus) {
		reapprovalStatus = aReapprovalStatus;
	}

	/**
	 * @return
	 */
	public Map getClearCreditAmt() {
		return clearCreditAmt;
	}

	/**
	 * @return
	 */
	public Map getDuplicateCreditAmt() {
		return duplicateCreditAmt;
	}

	/**
	 * @return
	 */
	public Map getIneligibleCreditAmt() {
		return ineligibleCreditAmt;
	}

	/**
	 * @return
	 */
	public Map getIneligibleDupCreditAmt() {
		return ineligibleDupCreditAmt;
	}

	/**
	 * @param map
	 */
	public void setClearCreditAmt(Map aClearCreditAmt) {
		clearCreditAmt = aClearCreditAmt;
	}

	/**
	 * @param map
	 */
	public void setDuplicateCreditAmt(Map aDuplicateCreditAmt) {
		duplicateCreditAmt = aDuplicateCreditAmt;
	}

	/**
	 * @param map
	 */
	public void setIneligibleCreditAmt(Map aIneligibleCreditAmt) {
		ineligibleCreditAmt = aIneligibleCreditAmt;
	}

	/**
	 * @param map
	 */
	public void setIneligibleDupCreditAmt(Map aIneligibleDupCreditAmt) {
		ineligibleDupCreditAmt = aIneligibleDupCreditAmt;
	}

	/**
	 * @return
	 */
	public ArrayList getPendingApplications() {
		return pendingApplications;
	}

	/**
	 * @param list
	 */
	public void setPendingApplications(ArrayList list) {
		pendingApplications = list;
	}

	/**
	 * @return
	 */
	public ArrayList getClearPendingApplications() {
		return clearPendingApplications;
	}

	/**
	 * @return
	 */
	public ArrayList getDupPendingApplications() {
		return dupPendingApplications;
	}

	/**
	 * @return
	 */
	public ArrayList getIneligibleDupPendingApplications() {
		return ineligibleDupPendingApplications;
	}

	/**
	 * @return
	 */
	public ArrayList getIneligiblePendingApplications() {
		return ineligiblePendingApplications;
	}

	/**
	 * @param list
	 */
	public void setClearPendingApplications(ArrayList list) {
		clearPendingApplications = list;
	}

	/**
	 * @param list
	 */
	public void setDupPendingApplications(ArrayList list) {
		dupPendingApplications = list;
	}

	/**
	 * @param list
	 */
	public void setIneligibleDupPendingApplications(ArrayList list) {
		ineligibleDupPendingApplications = list;
	}

	/**
	 * @param list
	 */
	public void setIneligiblePendingApplications(ArrayList list) {
		ineligiblePendingApplications = list;
	}
	
	public void resetMaps()
	{
		clearStatus.clear();
		duplicateStatus.clear();
		ineligibleStatus.clear();
		ineligibleDupStatus.clear();
	
		clearRemarks.clear();
		duplicateRemarks.clear();
		ineligibleRemarks.clear();
		ineligibleDupRemarks.clear();
		
	
		clearAppRefNo.clear();
		duplicateAppRefNo.clear();
		ineligibleAppRefNo.clear();
		ineligibleDupAppRefNo.clear();
	
		clearApprovedAmt.clear();
		duplicateApprovedAmt.clear();
		ineligibleApprovedAmt.clear();
		ineligibleDupApprovedAmt.clear();
	
		/*
		clearCreditAmt.clear();
		duplicateCreditAmt.clear();
		ineligibleCreditAmt.clear();
		ineligibleDupCreditAmt.clear();
		
		eligibleNonDupApps.clear();
		eligibleDupApps.clear();
		ineligibleNonDupApps.clear();
		ineligibleDupApps.clear();
		*/

	}

	public void resetReApproveMaps()
		{
			reApprovedAmt.clear();
			reApprovalRemarks.clear();
			reapprovalStatus.clear();
			
		}

	public void resetTCConvMaps()
		{
			tcCgpan.clear();
			tcDecision.clear();
			
		}

	public void resetWCConvMaps()
		{
			wcCgpan.clear();
			wcDecision.clear();
			
		}

	/**
	 * @return
	 */
	public Map getTempMap() {
		return tempMap;
	}

	/**
	 * @param map
	 */
	public void setTempMap(Map map) {
		tempMap = map;
	}

	/**
	 * @return
	 */
	public Map getClearTempMap() {
		return clearTempMap;
	}

/**
	 * @return
	 */
	public Map getClearRsfTempMap() {
		return clearRsfTempMap;
	}
	/**
	 * @return
	 */
	public Map getDupTempMap() {
		return dupTempMap;
	}

	/**
	 * @return
	 */
	public Map getIneligibleTempMap() {
		return ineligibleTempMap;
	}

	/**
	 * @param map
	 */
	public void setClearTempMap(Map map) {
		clearTempMap = map;
	}

/**
	 * @param map
	 */
	public void setClearRsfTempMap(Map map) {
		clearRsfTempMap = map;
	}
	/**
	 * @param map
	 */
	public void setDupTempMap(Map map) {
		dupTempMap = map;
	}

	/**
	 * @param map
	 */
	public void setIneligibleTempMap(Map map) {
		ineligibleTempMap = map;
	}

	/**
	 * @return
	 */
	public Map getCreditAmt() {
		return creditAmt;
	}

	/**
	 * @return
	 */
	public Map getEligibleCreditAmt() {
		return eligibleCreditAmt;
	}

	/**
	 * @param map
	 */
	public void setCreditAmt(Map map) {
		creditAmt = map;
	}

	/**
	 * @param map
	 */
	public void setEligibleCreditAmt(Map map) {
		eligibleCreditAmt = map;
	}

	/**
	 * @return
	 */
	public Map getEligibleReapproveMap() {
		return eligibleReapproveMap;
	}

	/**
	 * @return
	 */
	public Map getIneligibleReapproveMap() {
		return ineligibleReapproveMap;
	}

	/**
	 * @param map
	 */
	public void setEligibleReapproveMap(Map map) {
		eligibleReapproveMap = map;
	}

	/**
	 * @param map
	 */
	public void setIneligibleReapproveMap(Map map) {
		ineligibleReapproveMap = map;
	}

	/**
	 * @return
	 */
	public Map getClearRemMap() {
		return clearRemMap;
	}

/**
	 * @return
	 */
	public Map getClearRsfRemMap() {
		return clearRsfRemMap;
	}
	/**
	 * @return
	 */
	public Map getDupRemMap() {
		return dupRemMap;
	}

	/**
	 * @return
	 */
	public Map getIneligibleRemMap() {
		return ineligibleRemMap;
	}

	/**
	 * @return
	 */
	public Map getTempRemMap() {
		return tempRemMap;
	}

	/**
	 * @param map
	 */
	public void setClearRemMap(Map map) {
		clearRemMap = map;
	}

/**
	 * @param map
	 */
	public void setClearRsfRemMap(Map map) {
		clearRsfRemMap = map;
	}
	/**
	 * @param map
	 */
	public void setDupRemMap(Map map) {
		dupRemMap = map;
	}

	/**
	 * @param map
	 */
	public void setIneligibleRemMap(Map map) {
		ineligibleRemMap = map;
	}

	/**
	 * @param map
	 */
	public void setTempRemMap(Map map) {
		tempRemMap = map;
	}

	/**
	 * @return
	 */
	public ArrayList getTcApplications() {
		return tcApplications;
	}

	/**
	 * @return
	 */
	public ArrayList getWcApplications() {
		return wcApplications;
	}

	/**
	 * @param list
	 */
	public void setTcApplications(ArrayList list) {
		tcApplications = list;
	}

	/**
	 * @param list
	 */
	public void setWcApplications(ArrayList list) {
		wcApplications = list;
	}

/**
 * @return
 */
public Map getTcAppRefNo() {
	return tcAppRefNo;
}

	/**
	 * @return
	 */
	public Map getTcCgpan() {
		return tcCgpan;
	}

	/**
	 * @return
	 */
	public Map getTcDecision() {
		return tcDecision;
	}

/**
 * @param map
 */
public void setTcAppRefNo(Map map) {
	tcAppRefNo = map;
}

	/**
	 * @param map
	 */
	public void setTcCgpan(Map map) {
		tcCgpan = map;
	}

	/**
	 * @param map
	 */
	public void setTcDecision(Map map) {
		tcDecision = map;
	}

	/**
	 * @return
	 */
	public Map getWcAppRefNo() {
		return wcAppRefNo;
	}

	/**
	 * @return
	 */
	public Map getWcCgpan() {
		return wcCgpan;
	}

	/**
	 * @return
	 */
	public Map getWcDecision() {
		return wcDecision;
	}

	/**
	 * @param map
	 */
	public void setWcAppRefNo(Map map) {
		wcAppRefNo = map;
	}

	/**
	 * @param map
	 */
	public void setWcCgpan(Map map) {
		wcCgpan = map;
	}

	/**
	 * @param map
	 */
	public void setWcDecision(Map map) {
		wcDecision = map;
	}

	/**
	 * @return
	 */
	public int getTcEntryIndex() {
		return tcEntryIndex;
	}

	/**
	 * @return
	 */
	public int getWcEntryIndex() {
		return wcEntryIndex;
	}

	/**
	 * @param i
	 */
	public void setTcEntryIndex(int i) {
		tcEntryIndex = i;
	}

	/**
	 * @param i
	 */
	public void setWcEntryIndex(int i) {
		wcEntryIndex = i;
	}

	/**
	 * @return
	 */
	public ArrayList getTcConvertedApplications() {
		return tcConvertedApplications;
	}

	/**
	 * @param list
	 */
	public void setTcConvertedApplications(ArrayList list) {
		tcConvertedApplications = list;
	}


	/**
	 * @return
	 */
	public ArrayList getEnhanceConvertedApplications() {
		return enhanceConvertedApplications;
	}

	/**
	 * @return
	 */
	public ArrayList getRenewConvertedApplications() {
		return renewConvertedApplications;
	}

	/**
	 * @param list
	 */
	public void setEnhanceConvertedApplications(ArrayList list) {
		enhanceConvertedApplications = list;
	}

	/**
	 * @param list
	 */
	public void setRenewConvertedApplications(ArrayList list) {
		renewConvertedApplications = list;
	}
        
        //added by upchar@path 
        private Date cpDOB;

    public void setCpDOB(Date cpDOB) {
        this.cpDOB = cpDOB;
    }

    public Date getCpDOB() {
        return cpDOB;
    }
    
    private String principalMoratorium;
    private String interestMoratorium;

    public void setPrincipalMoratorium(String principalMoratorium) {
        this.principalMoratorium = principalMoratorium;
    }

    public String getPrincipalMoratorium() {
        return principalMoratorium;
    }

    public void setInterestMoratorium(String interestMoratorium) {
        this.interestMoratorium = interestMoratorium;
    }

    public String getInterestMoratorium() {
        return interestMoratorium;
    }
}
