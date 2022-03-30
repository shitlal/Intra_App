// Decompiled Using: FrontEnd Plus v2.03 and the JAD Engine
// Available From: http://www.reflections.ath.cx
// Decompiler options: packimports(3) 
// Source File Name:   GMActionForm.java

package com.cgtsi.actionform;

import com.cgtsi.application.BorrowerDetails;
import com.cgtsi.common.Log;
import com.cgtsi.guaranteemaintenance.*;
import com.cgtsi.reports.ApplicationReport;
import java.util.*;
import javax.servlet.ServletRequest;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorActionForm;

public class GMActionForm extends ValidatorActionForm
{

    private void $init$()
    {
        cgpans = new TreeMap();
        schemes = new TreeMap();
        borrowerIds = new ArrayList();
        cgpansForWc = new TreeMap();
        cgpansForTc = new TreeMap();
        
        tcOutstandingAsOnDate = new TreeMap();
        tcPrincipalOutstandingAmount = new TreeMap();
        tcInterestOutstandingAmount = new TreeMap();       
        
        wcFBOutstandingAsOnDate = new TreeMap();
        wcFBPrincipalOutstandingAmount = new TreeMap();
        wcFBInterestOutstandingAmount = new TreeMap();        
       
        wcNFBOutstandingAsOnDate = new TreeMap();
        wcNFBInterestOutstandingAmount = new TreeMap();
        wcNFBPrincipalOutstandingAmount = new TreeMap();        
        
        termCreditId = new TreeMap();
        workingCapitalId = new TreeMap();
        disbursementDate = new TreeMap();
        disbursementAmount = new TreeMap();
        finalDisbursement = new TreeMap();
        disbursementId = new TreeMap();
        firstInstallmentDueDate = new TreeMap();
        moratorium = new TreeMap();
        noOfInstallment = new TreeMap();
        periodicity = new TreeMap();
        repaymentAmount = new TreeMap();
        repaymentDate = new TreeMap();
        repaymentId = new TreeMap();
        closureFlag = new TreeMap();
        closureCgpans = new TreeMap();
        closureDetails = new HashMap();
        closureReasons = new ArrayList();
        reasonForClosure = new TreeMap();
        remarksForClosure = new TreeMap();
        closureDetailsNotPaid = new HashMap();
        memberIdCgpans = new TreeMap();
        clFlag = new TreeMap();
        reasonForCl = new TreeMap();
        remarksForCl = new TreeMap();
        clCgpan = new TreeMap();
        clReasons = new ArrayList();
        memberIdsForShifting = new ArrayList();
        borrowerIdsForShifting = new ArrayList();
        cgpansForShifting = new ArrayList();
        memberIdsToShift = new ArrayList();
        osPeriodicInfoDetails = new ArrayList();
        disbPeriodicInfoDetails = new ArrayList();
        repayPeriodicInfoDetails = new ArrayList();
        repayPeriodicInfoDetailsTemp = new ArrayList();
        repaymentSchedules = new ArrayList();
        actionTypeDb = new TreeMap();
        actionDetailsDb = new TreeMap();
        actionDateDb = new TreeMap();
        attachmentNameDb = new TreeMap();
        radId = new TreeMap();
        recTypes = new ArrayList();
        recoveryProcedures = new ArrayList();
        recProcedures = new HashMap();
        dtOfFilingLegalSuit = null;
        npaDetails = new NPADetails();
        legalSuitDetail = new LegalSuitDetail();
        recoveryProcedure = new RecoveryProcedure();
        borrowerDetails = new BorrowerDetails();
        recoveryDetails = new HashMap();
        approveBorrowerFlag = new TreeMap();
        bidsList = new TreeMap();
        Cgpandetails = new TreeMap();
        bankId = null;
        zoneId = null;
        branchId = null;
        targetURL = null;
        clearCgpan = new TreeMap();
        closureCgpan = new TreeMap();
        commentCgpan = new TreeMap();// vinod 08-jan-16
        empComments = new TreeMap();
        
   //added by upchar@path     
        guaranteeStartDates = new HashMap();
        sanctionDates = new HashMap();
        cgpanNos = new HashMap();
        cgpansForNpaDetail = new HashMap(); 
        firstDisbursementDates = new HashMap();
        lastDisbursementDates = new HashMap();
        firstInstallmentDates = new HashMap();
        amountDisbursedValues = new HashMap();
        principalmoratoriums = new HashMap();
        Interestmoratoriums = new HashMap();
        isSubsidyValues = new HashMap();
        isSubsidyRcvdValues = new HashMap();
        isSubsidyAdjustedValues = new HashMap();
        npaDetailsMap = new HashMap();
        
        asOnDtOfSanctionDtl = new HashMap();
        asOnDtOfNPADtl = new HashMap();
        cgpansVector = new Vector();
        tccgpansVector = new Vector();
        wccgpansVector = new Vector();
        
        securityAsOnDtOfSanction = new HashMap();
        repaymentAmountPrincipal = new TreeMap();
        repaymentAmountInterest = new TreeMap();
        
         tcCounter = 0;
         wcCounter = 0;
         
         tcDisbursementAmount = new TreeMap();
         wcDisbursementAmount = new TreeMap();
         
         tcCgpanMap = new HashMap();
         wcCgpanMap = new HashMap();
         cgmap=new ArrayList();
         
    
        
    }

    public ApplicationReport getApplicationReport()
    {
        return applicationReport;
    }

    public void setApplicationReport(ApplicationReport applicationReport)
    {
        this.applicationReport = applicationReport;
    }

    public Map getClearCgpan()
    {
        return clearCgpan;
    }

    public Map getClosureCgpan()
    {
        return closureCgpan;
    }

    public void setclosureCgpan(Map map)
    {
        closureCgpan = map;
    }
    
    public Map getCommentCgpan() {
		return commentCgpan;
	}

	public void setCommentCgpan(Map map) {
		commentCgpan = map;
	}
    
	public void setClearCgpan(Map map)
    {
        clearCgpan = map;
    }

    public ArrayList getDanSummaries()
    {
        return danSummaries;
    }

    public void setDanSummaries(ArrayList list)
    {
        danSummaries = list;
    }

    public ArrayList getMemberList()
    {
        return memberList;
    }

    public void setMemberList(ArrayList list)
    {
        memberList = list;
    }

    public String getDanNo()
    {
        return danNo;
    }

    public void setDanNo(String string)
    {
        danNo = string;
    }

    public String getSelectMember()
    {
        return selectMember;
    }

    public void setSelectMember(String string)
    {
        selectMember = string;
    }

    public String getBankId()
    {
        return bankId;
    }

    public String getBranchId()
    {
        return branchId;
    }

    public String getZoneId()
    {
        return zoneId;
    }

    public void setBankId(String string)
    {
        bankId = string;
    }

    public void setBranchId(String string)
    {
        branchId = string;
    }

    public void setZoneId(String string)
    {
        zoneId = string;
    }

    public String getTargetURL()
    {
        return targetURL;
    }

    public void setTargetURL(String string)
    {
        targetURL = string;
    }

    public Object getTcPrincipalOutstandingAmount(String key)
    {
        return tcPrincipalOutstandingAmount;
    }

    public Object getTcInterestOutstandingAmount(String key)
    {
        return tcInterestOutstandingAmount;
    }

    public Object getTcOutstandingAsOnDate(String key)
    {
        return tcOutstandingAsOnDate;
    }

    public Object getWcFBPrincipalOutstandingAmount(String key)
    {
        return wcFBPrincipalOutstandingAmount.get(key);
    }

    public Object getWcFBInterestOutstandingAmount(String key)
    {
        return wcFBInterestOutstandingAmount.get(key);
    }

    public Object getWcFBOutstandingAsOnDate(String key)
    {
        return wcFBOutstandingAsOnDate.get(key);
    }

    public Object getWcNFBPrincipalOutstandingAmount(String key)
    {
        return wcNFBPrincipalOutstandingAmount.get(key);
    }

    public Object getWcNFBInterestOutstandingAmount(String key)
    {
        return wcNFBInterestOutstandingAmount.get(key);
    }

    public Object getWcNFBOutstandingAsOnDate(String key)
    {
        return wcNFBOutstandingAsOnDate.get(key);
    }

    public Object getDisbursementAmount(String key)
    {
        return disbursementAmount.get(key);
    }

    public Object getFirstInstallmentDueDate(String key)
    {
        return firstInstallmentDueDate.get(key);
    }

    public Object getMoratorium(String key)
    {
        return moratorium.get(key);
    }

    public Object getNoOfInstallment(String key)
    {
        return noOfInstallment.get(key);
    }

    public Object getPeriodicity(String key)
    {
        return periodicity.get(key);
    }

    public Object getFinalDisbursement(String key)
    {
        return finalDisbursement.get(key);
    }

    public Object getRepaymentAmount(String key)
    {
        return repaymentAmount.get(key);
    }

    public Object getRepaymentDate(String key)
    {
        return repaymentDate.get(key);
    }

    public void setTcPrincipalOutstandingAmount(String key, Object value)
    {
        tcPrincipalOutstandingAmount.put(key, value);
    }

    public void setTcInterestOutstandingAmount(String key, Object value)
    {
        tcInterestOutstandingAmount.put(key, value);
    }

    public void setTcOutstandingAsOnDate(String key, Object value)
    {
        tcOutstandingAsOnDate.put(key, value);
    }

    public void setWcFBPrincipalOutstandingAmount(String key, Object value)
    {
        wcFBPrincipalOutstandingAmount.put(key, value);
    }

    public void setWcFBInterestOutstandingAmount(String key, Object value)
    {
        wcFBInterestOutstandingAmount.put(key, value);
    }

    public void setWcFBOutstandingAsOnDate(String key, Object value)
    {
        wcFBOutstandingAsOnDate.put(key, value);
    }

    public void setWcNFBPrincipalOutstandingAmount(String key, Object value)
    {
        wcNFBPrincipalOutstandingAmount.put(key, value);
    }

    public void setWcNFBInterestOutstandingAmount(String key, Object value)
    {
        wcNFBInterestOutstandingAmount.put(key, value);
    }

    public void setWcNFBOutstandingAsOnDate(String key, Object value)
    {
        wcNFBOutstandingAsOnDate.put(key, value);
    }

    public void setDisbursementAmount(String key, Object value)
    {
        disbursementAmount.put(key, value);
    }

    public void setDisbursementDate(String key, Object value)
    {
        disbursementDate.put(key, value);
    }

    public void setFirstInstallmentDueDate(String key, Object value)
    {
        firstInstallmentDueDate.put(key, value);
    }

    public void setMoratorium(String key, Object value)
    {
        moratorium.put(key, value);
    }

    public void setNoOfInstallment(String key, Object value)
    {
        noOfInstallment.put(key, value);
    }

    public void setPeriodicity(String key, Object value)
    {
        periodicity.put(key, value);
    }

    public void setFinalDisbursement(String key, Object value)
    {
        finalDisbursement.put(key, value);
    }

    public void setRepaymentAmount(String key, Object value)
    {
        repaymentAmount.put(key, value);
    }

    public void setRepaymentDates(String key, Object value)
    {
        repaymentDate.put(key, value);
    }

    public Map getTcPrincipalOutstandingAmount()
    {
        return tcPrincipalOutstandingAmount;
    }

    public Map getTcInterestOutstandingAmount()
    {
        return tcInterestOutstandingAmount;
    }

    public Map getTcOutstandingAsOnDate()
    {
        return tcOutstandingAsOnDate;
    }

    public Map getWcFBPrincipalOutstandingAmount()
    {
        return wcFBPrincipalOutstandingAmount;
    }

    public Map getWcFBInterestOutstandingAmount()
    {
        return wcFBInterestOutstandingAmount;
    }

    public Map getWcFBOutstandingAsOnDate()
    {
        return wcFBOutstandingAsOnDate;
    }

    public Map getWcNFBInterestOutstandingAmount()
    {
        return wcNFBInterestOutstandingAmount;
    }

    public Map getWcNFBPrincipalOutstandingAmount()
    {
        return wcNFBPrincipalOutstandingAmount;
    }

    public Map getWcNFBOutstandingAsOnDate()
    {
        return wcNFBOutstandingAsOnDate;
    }

    public Map getDisbursementAmount()
    {
        return disbursementAmount;
    }

    public Map getDisbursementDate()
    {
        return disbursementDate;
    }

    public Map getFirstInstallmentDueDate()
    {
        return firstInstallmentDueDate;
    }

    public Map getMoratorium()
    {
        return moratorium;
    }

    public Map getNoOfInstallment()
    {
        return noOfInstallment;
    }

    public Map getPeriodicity()
    {
        return periodicity;
    }

    public Map getFinalDisbursement()
    {
        return finalDisbursement;
    }

    public Map getRepaymentAmount()
    {
        return repaymentAmount;
    }

    public void setTcPrincipalOutstandingAmount(Map map)
    {
        tcPrincipalOutstandingAmount = map;
    }

    public void setTcInterestOutstandingAmount(Map map)
    {
        tcInterestOutstandingAmount = map;
    }

    public void setTcOutstandingAsOnDate(Map map)
    {
        tcOutstandingAsOnDate = map;
    }

    public void setWcFBPrincipalOutstandingAmount(Map map)
    {
        wcFBPrincipalOutstandingAmount = map;
    }

    public void setWcFBInterestOutstandingAmount(Map map)
    {
        wcFBInterestOutstandingAmount = map;
    }

    public void setWcFBOutstandingAsOnDate(Map map)
    {
        wcFBOutstandingAsOnDate = map;
    }

    public void setWcNFBInterestOutstandingAmount(Map map)
    {
        wcNFBInterestOutstandingAmount = map;
    }

    public void setWcNFBPrincipalOutstandingAmount(Map map)
    {
        wcNFBPrincipalOutstandingAmount = map;
    }

    public void setWcNFBOutstandingAsOnDate(Map map)
    {
        wcNFBOutstandingAsOnDate = map;
    }

    public void setDisbursementAmount(Map map)
    {
        disbursementAmount = map;
    }

    public void setDisbursementDate(Map map)
    {
        disbursementDate = map;
    }

    public void setFirstInstallmentDueDate(Map map)
    {
        firstInstallmentDueDate = map;
    }

    public void setMoratorium(Map map)
    {
        moratorium = map;
    }

    public void setNoOfInstallment(Map map)
    {
        noOfInstallment = map;
    }

    public void setPeriodicity(Map map)
    {
        periodicity = map;
    }

    public void setFinalDisbursement(Map map)
    {
        finalDisbursement = map;
    }

    public void setRepaymentAmount(Map map)
    {
        repaymentAmount = map;
    }

    public ArrayList getOsPeriodicInfoDetails()
    {
        Log.log(5, "periodic", "info", "details ---- get");
        return osPeriodicInfoDetails;
    }

    public void setOsPeriodicInfoDetails(ArrayList list)
    {
        Log.log(5, "periodic", "info", "details ---- set");
        osPeriodicInfoDetails = list;
    }

    public ArrayList getRepaymentSchedules()
    {
        return repaymentSchedules;
    }

    public void setRepaymentSchedules(ArrayList list)
    {
        repaymentSchedules = list;
    }

    public Map getCgpans()
    {
        return cgpans;
    }

    public Map getSchemes()
    {
        return schemes;
    }

    public void setCgpans(Map map)
    {
        cgpans = map;
    }

    public void setSchemes(Map map)
    {
        schemes = map;
    }

    public Map getRepaymentDate()
    {
        return repaymentDate;
    }

    public void setRepaymentDate(Map map)
    {
        repaymentDate = map;
    }

    public ArrayList getBorrowerIds()
    {
        return borrowerIds;
    }

    public void setBorrowerIds(ArrayList list)
    {
        borrowerIds = list;
    }

    public String getBorrowerIdForClosure()
    {
        return borrowerIdForClosure;
    }

    public String getCgpanForClosure()
    {
        return cgpanForClosure;
    }

    public void setBorrowerIdForClosure(String string)
    {
        borrowerIdForClosure = string;
    }

    public void setCgpanForClosure(String string)
    {
        cgpanForClosure = string;
    }

    public Map getClosureDetails()
    {
        return closureDetails;
    }

    public void setClosureDetails(Map map)
    {
        closureDetails = map;
    }

    public String getMemberIdForClosure()
    {
        return memberIdForClosure;
    }

    public void setMemberIdForClosure(String string)
    {
        memberIdForClosure = string;
    }

    public String getMemberIdForShifting()
    {
        return memberIdForShifting;
    }

    public ArrayList getMemberIdsForShifting()
    {
        return memberIdsForShifting;
    }

    public void setMemberIdForShifting(String string)
    {
        memberIdForShifting = string;
    }

    public void setMemberIdsForShifting(ArrayList list)
    {
        memberIdsForShifting = list;
    }

    public String getBorrowerIdForShifting()
    {
        return borrowerIdForShifting;
    }

    public ArrayList getBorrowerIdsForShifting()
    {
        return borrowerIdsForShifting;
    }

    public void setBorrowerIdForShifting(String string)
    {
        borrowerIdForShifting = string;
    }

    public void setBorrowerIdsForShifting(ArrayList list)
    {
        borrowerIdsForShifting = list;
    }

    public String getCgpanForShifting()
    {
        return cgpanForShifting;
    }

    public ArrayList getCgpansForShifting()
    {
        return cgpansForShifting;
    }

    public void setCgpanForShifting(String string)
    {
        cgpanForShifting = string;
    }

    public void setCgpansForShifting(ArrayList list)
    {
        cgpansForShifting = list;
    }

    public ArrayList getMemberIdsToShift()
    {
        return memberIdsToShift;
    }

    public String getMemberIdToShift()
    {
        return memberIdToShift;
    }

    public void setMemberIdsToShift(ArrayList list)
    {
        memberIdsToShift = list;
    }

    public void setMemberIdToShift(String string)
    {
        memberIdToShift = string;
    }

    public String getBorrowerIdForSchedule()
    {
        return borrowerIdForSchedule;
    }

    public String getCgpanForSchedule()
    {
        return cgpanForSchedule;
    }

    public void setBorrowerIdForSchedule(String string)
    {
        borrowerIdForSchedule = string;
    }

    public void setCgpanForSchedule(String string)
    {
        cgpanForSchedule = string;
    }

    public double getAmountClaimed()
    {
        return amountClaimed;
    }

    public String getCourtName()
    {
        return courtName;
    }

    public String getCurrentStatus()
    {
        return currentStatus;
    }

    public Date getDtOfFilingLegalSuit()
    {
        return dtOfFilingLegalSuit;
    }

    public String getForumName()
    {
        return forumName;
    }

    public String getLegalSuitNo()
    {
        return legalSuitNo;
    }

    public String getLocation()
    {
        return location;
    }

    public String getRecoveryProceedingsConcluded()
    {
        return recoveryProceedingsConcluded;
    }

    public void setAmountClaimed(double d)
    {
        amountClaimed = d;
    }

    public void setCourtName(String string)
    {
        courtName = string;
    }

    public void setCurrentStatus(String string)
    {
        currentStatus = string;
    }

    public void setDtOfFilingLegalSuit(Date date)
    {
        dtOfFilingLegalSuit = date;
    }

    public void setForumName(String string)
    {
        forumName = string;
    }

    public void setLegalSuitNo(String string)
    {
        legalSuitNo = string;
    }

    public void setLocation(String string)
    {
        location = string;
    }

    public void setRecoveryProceedingsConcluded(String string)
    {
        recoveryProceedingsConcluded = string;
    }

    public String getBankFacilityDetail()
    {
        return bankFacilityDetail;
    }

    public String getCreditSupport()
    {
        return creditSupport;
    }

    public String getDetailsOfFinAssistance()
    {
        return detailsOfFinAssistance;
    }

    public Date getEffortsConclusionDate()
    {
        return effortsConclusionDate;
    }

    public String getEffortsTaken()
    {
        return effortsTaken;
    }

    public String getIsRecoveryInitiated()
    {
        return isRecoveryInitiated;
    }

    public String getMliCommentOnFinPosition()
    {
        return mliCommentOnFinPosition;
    }

    public Date getNpaDate()
    {
        return npaDate;
    }

    public String getNpaReason()
    {
        return npaReason;
    }

    public double getOsAmtOnNPA()
    {
        return osAmtOnNPA;
    }

    public String getPlaceUnderWatchList()
    {
        return placeUnderWatchList;
    }

    public String getRemarksOnNpa()
    {
        return remarksOnNpa;
    }

    public Date getReportingDate()
    {
        return reportingDate;
    }

    public String getWhetherNPAReported()
    {
        return whetherNPAReported;
    }

    public String getWillfulDefaulter()
    {
        return willfulDefaulter;
    }

    public void setBankFacilityDetail(String string)
    {
        bankFacilityDetail = string;
    }

    public void setCreditSupport(String string)
    {
        creditSupport = string;
    }

    public void setDetailsOfFinAssistance(String string)
    {
        detailsOfFinAssistance = string;
    }

    public void setEffortsConclusionDate(Date date)
    {
        effortsConclusionDate = date;
    }

    public void setEffortsTaken(String string)
    {
        effortsTaken = string;
    }

    public void setIsRecoveryInitiated(String string)
    {
        isRecoveryInitiated = string;
    }

    public void setMliCommentOnFinPosition(String string)
    {
        mliCommentOnFinPosition = string;
    }

    public void setNpaDate(Date date)
    {
        npaDate = date;
    }

    public void setNpaReason(String string)
    {
        npaReason = string;
    }

    public void setOsAmtOnNPA(double d)
    {
        osAmtOnNPA = d;
    }

    public void setPlaceUnderWatchList(String string)
    {
        placeUnderWatchList = string;
    }

    public void setRemarksOnNpa(String string)
    {
        remarksOnNpa = string;
    }

    public void setReportingDate(Date date)
    {
        reportingDate = date;
    }

    public void setWhetherNPAReported(String string)
    {
        whetherNPAReported = string;
    }

    public void setWillfulDefaulter(String string)
    {
        willfulDefaulter = string;
    }

    public String getInitiatedName()
    {
        return initiatedName;
    }

    public void setInitiatedName(String string)
    {
        initiatedName = string;
    }

    public LegalSuitDetail getLegalSuitDetail()
    {
        return legalSuitDetail;
    }

    public NPADetails getNpaDetails()
    {
        return npaDetails;
    }

    public RecoveryProcedure getRecoveryProcedure()
    {
        return recoveryProcedure;
    }

    public void setLegalSuitDetail(LegalSuitDetail detail)
    {
        legalSuitDetail = detail;
    }

    public void setNpaDetails(NPADetails details)
    {
        npaDetails = details;
    }

    public void setRecoveryProcedure(RecoveryProcedure procedure)
    {
        recoveryProcedure = procedure;
    }

    public Map getCgpansForTc()
    {
        return cgpansForTc;
    }

    public Map getCgpansForWc()
    {
        return cgpansForWc;
    }

    public void setCgpansForTc(Map map)
    {
        cgpansForTc = map;
    }

    public void setCgpansForWc(Map map)
    {
        cgpansForWc = map;
    }

    public ArrayList getClosureReasons()
    {
        return closureReasons;
    }

    public void setClosureReasons(ArrayList list)
    {
        closureReasons = list;
    }

    public Map getReasonForClosure()
    {
        return reasonForClosure;
    }

    public void setReasonForClosure(Map map)
    {
        reasonForClosure = map;
    }

    public Object getReasonForClosure(String key)
    {
        return reasonForClosure.get(key);
    }

    public void setReasonForClosure(String key, Object value)
    {
        reasonForClosure.put(key, value);
    }

    public Map getClosureCgpans()
    {
        return closureCgpans;
    }

    public void setClosureCgpans(Map map)
    {
        closureCgpans = map;
    }

    public Object getClosureCgpans(String key)
    {
        return closureCgpans.get(key);
    }

    public void setClosureCgpans(String key, Object value)
    {
        closureCgpans.put(key, value);
    }

    public double getAmountRecovered()
    {
        return amountRecovered;
    }

    public Date getDateOfRecovery()
    {
        return dateOfRecovery;
    }

    public String getDetailsOfAssetSold()
    {
        return detailsOfAssetSold;
    }

    public String getIsRecoveryByOTS()
    {
        return isRecoveryByOTS;
    }

    public String getIsRecoveryBySaleOfAsset()
    {
        return isRecoveryBySaleOfAsset;
    }

    public double getLegalCharges()
    {
        return legalCharges;
    }

    public String getRemarks()
    {
        return remarks;
    }

    public void setAmountRecovered(double d)
    {
        amountRecovered = d;
    }

    public void setDateOfRecovery(Date date)
    {
        dateOfRecovery = date;
    }

    public void setDetailsOfAssetSold(String string)
    {
        detailsOfAssetSold = string;
    }

    public void setIsRecoveryByOTS(String string)
    {
        isRecoveryByOTS = string;
    }

    public void setIsRecoveryBySaleOfAsset(String string)
    {
        isRecoveryBySaleOfAsset = string;
    }

    public void setLegalCharges(double d)
    {
        legalCharges = d;
    }

    public void setRemarks(String string)
    {
        remarks = string;
    }

    public ArrayList getDisbPeriodicInfoDetails()
    {
        return disbPeriodicInfoDetails;
    }

    public ArrayList getRepayPeriodicInfoDetails()
    {
        return repayPeriodicInfoDetails;
    }

    public void setDisbPeriodicInfoDetails(ArrayList list)
    {
        disbPeriodicInfoDetails = list;
    }

    public void setRepayPeriodicInfoDetails(ArrayList list)
    {
        repayPeriodicInfoDetails = list;
    }

    public String getBorrowerId()
    {
        return borrowerId;
    }

    public String getBorrowerName()
    {
        return borrowerName;
    }

    public String getCgpan()
    {
        return cgpan;
    }

    public void setBorrowerId(String string)
    {
        borrowerId = string;
    }

    public void setBorrowerName(String string)
    {
        borrowerName = string;
    }

    public void setCgpan(String string)
    {
        cgpan = string;
    }

    public int getNoOfActions()
    {
        return noOfActions;
    }

    public void setNoOfActions(int i)
    {
        noOfActions = i;
    }

    public String getBorrowerNameForSchedule()
    {
        return borrowerNameForSchedule;
    }

    public String getMemberIdForSchedule()
    {
        return memberIdForSchedule;
    }

    public void setBorrowerNameForSchedule(String string)
    {
        borrowerNameForSchedule = string;
    }

    public void setMemberIdForSchedule(String string)
    {
        memberIdForSchedule = string;
    }

    public String getBorrowerNameForClosure()
    {
        return borrowerNameForClosure;
    }

    public void setBorrowerNameForClosure(String string)
    {
        borrowerNameForClosure = string;
    }

    public String getMemberId()
    {
        return memberId;
    }

    public void setMemberId(String str)
    {
        memberId = str;
    }

    public String getPeriodicBankId()
    {
        return periodicBankId;
    }

    public void setPeriodicBankId(String string)
    {
        periodicBankId = string;
    }

    public String getBankIdForSchedule()
    {
        return bankIdForSchedule;
    }

    public void setBankIdForSchedule(String string)
    {
        bankIdForSchedule = string;
    }

    public int getRowCount()
    {
        return rowCount;
    }

    public void setRowCount(int i)
    {
        rowCount = i;
    }

    public String getBankIdForClosure()
    {
        return bankIdForClosure;
    }

    public void setBankIdForClosure(String string)
    {
        bankIdForClosure = string;
    }

    public String getMemberIdForExport()
    {
        return memberIdForExport;
    }

    public void setMemberIdForExport(String string)
    {
        memberIdForExport = string;
    }

    public void resetWhenRequired(ActionMapping arg0, ServletRequest arg1)
    {
        Log.log(4, "GMActionForm", "resetWhenRequired", "Entered");
        Log.log(5, "GMActionForm", "Resets the RecoveryDetails page", "");
        dateOfRecovery = null;
        amountRecovered = 0.0D;
        legalCharges = 0.0D;
        remarks = null;
        isRecoveryBySaleOfAsset = "N";
        detailsOfAssetSold = null;
        isRecoveryByOTS = "N";
        recoveryDetails.clear();
        Log.log(4, "GMActionForm", "resetWhenRequired", "Exited");
    }

    public void resetNpaDetailsPage(ActionMapping arg0, ServletRequest arg1)
    {
        Log.log(4, "GMActionForm", "resetNpaDetailsPage", "Entered");
        legalSuitNo = null;
        courtName = null;
        location = null;
        dtOfFilingLegalSuit = null;
        amountClaimed = 0.0D;
        forumName = null;
        recoveryProceedingsConcluded = "Y";
        currentStatus = null;
        npaDate = null;
        osAmtOnNPA = 0.0D;
        whetherNPAReported = "Y";//default value changed by upchar@path from 'N' to 'Y'
        reportingDate = null;
        npaReason = null;
        willfulDefaulter = null;
        effortsTaken = null;
        isRecoveryInitiated = "N";
        effortsConclusionDate = null;
        mliCommentOnFinPosition = null;
        detailsOfFinAssistance = null;
        creditSupport = "N";
        bankFacilityDetail = null;
        placeUnderWatchList = "Y";
        remarksOnNpa = null;
        initiatedName = null;
        // added by upchar@path on 02-04-2013
         reasonForReduction = null;
         netWorthOfGuarantor = null;
        lastInspectionDt = null;
        //ended by upchar@path 
        Log.log(4, "GMActionForm", "resetNpaDetailsPage", "Entered");
    }

    public Map getRemarksForClosure()
    {
        return remarksForClosure;
    }

    public void setRemarksForClosure(Map map)
    {
        remarksForClosure = map;
    }

    public Object getRemarksForClosure(String key)
    {
        return remarksForClosure.get(key);
    }

    public void setRemarksForClosure(String key, Object value)
    {
        remarksForClosure.put(key, value);
    }

    public String getCityForCgpanLink()
    {
        return cityForCgpanLink;
    }

    public void setCityForCgpanLink(String string)
    {
        cityForCgpanLink = string;
    }

    public double getAmountApprovedForLink()
    {
        return amountApprovedForLink;
    }

    public String getBidForCgpanLink()
    {
        return bidForCgpanLink;
    }

    public String getBorrowerNameForLink()
    {
        return borrowerNameForLink;
    }

    public Date getGuaranteeIssueDateForLink()
    {
        return guaranteeIssueDateForLink;
    }

    public String getPromoterNameForLink()
    {
        return promoterNameForLink;
    }

    public double getTcAmountSanctionedForLink()
    {
        return tcAmountSanctionedForLink;
    }

    public double getWcAmountSanctionedForLink()
    {
        return wcAmountSanctionedForLink;
    }

    public void setAmountApprovedForLink(double d)
    {
        amountApprovedForLink = d;
    }

    public void setBidForCgpanLink(String string)
    {
        bidForCgpanLink = string;
    }

    public void setBorrowerNameForLink(String string)
    {
        borrowerNameForLink = string;
    }

    public void setGuaranteeIssueDateForLink(Date date)
    {
        guaranteeIssueDateForLink = date;
    }

    public void setPromoterNameForLink(String string)
    {
        promoterNameForLink = string;
    }

    public void setTcAmountSanctionedForLink(double d)
    {
        tcAmountSanctionedForLink = d;
    }

    public void setWcAmountSanctionedForLink(double d)
    {
        wcAmountSanctionedForLink = d;
    }

    public Map getRepaymentId()
    {
        return repaymentId;
    }

    public void setRepaymentId(Map map)
    {
        repaymentId = map;
    }

    public Map getClosureFlag()
    {
        return closureFlag;
    }

    public void setClosureFlag(Map map)
    {
        closureFlag = map;
    }

    public ArrayList getRepayPeriodicInfoDetailsTemp()
    {
        return repayPeriodicInfoDetailsTemp;
    }

    public void setRepayPeriodicInfoDetailsTemp(ArrayList list)
    {
        repayPeriodicInfoDetailsTemp = list;
    }

    public Map getDisbursementId()
    {
        return disbursementId;
    }

    public void setDisbursementId(Map map)
    {
        disbursementId = map;
    }

    public Map getTermCreditId()
    {
        return termCreditId;
    }

    public Map getWorkingCapitalId()
    {
        return workingCapitalId;
    }

    public void setTermCreditId(Map map)
    {
        termCreditId = map;
    }

    public void setWorkingCapitalId(Map map)
    {
        workingCapitalId = map;
    }

    public ArrayList getRecoveryProcedures()
    {
        return recoveryProcedures;
    }

    public void setRecoveryProcedures(ArrayList list)
    {
        recoveryProcedures = list;
    }

    public ArrayList getRecTypes()
    {
        return recTypes;
    }

    public void setRecTypes(ArrayList list)
    {
        recTypes = list;
    }

    public BorrowerDetails getBorrowerDetails()
    {
        return borrowerDetails;
    }

    public void setBorrowerDetails(BorrowerDetails details)
    {
        borrowerDetails = details;
    }

    public Map getActionDateDb()
    {
        return actionDateDb;
    }

    public Map getActionDetailsDb()
    {
        return actionDetailsDb;
    }

    public Map getActionTypeDb()
    {
        return actionTypeDb;
    }

    public Map getAttachmentNameDb()
    {
        return attachmentNameDb;
    }

    public void setActionDateDb(Map map)
    {
        actionDateDb = map;
    }

    public void setActionDetailsDb(Map map)
    {
        actionDetailsDb = map;
    }

    public void setActionTypeDb(Map map)
    {
        actionTypeDb = map;
    }

    public void setAttachmentNameDb(Map map)
    {
        attachmentNameDb = map;
    }

    public Map getRadId()
    {
        return radId;
    }

    public void setRadId(Map map)
    {
        radId = map;
    }

    public Map getRecoveryDetails()
    {
        return recoveryDetails;
    }

    public void setRecoveryDetails(Map map)
    {
        recoveryDetails = map;
    }

    public Map getRecProcedures()
    {
        return recProcedures;
    }

    public void setRecProcedures(Map map)
    {
        recProcedures = map;
    }

    public Object getRecProcedure(String key)
    {
        return recProcedures.get(key);
    }

    public void setRecProcedure(String key, Object value)
    {
        recProcedures.put(key, value);
    }

    public String getNpaId()
    {
        return npaId;
    }

    public void setNpaId(String string)
    {
        npaId = string;
    }

    public String getFeeNotPaid()
    {
        return feeNotPaid;
    }

    public void setFeeNotPaid(String string)
    {
        feeNotPaid = string;
    }

    public Map getMemberIdCgpans()
    {
        return memberIdCgpans;
    }

    public void setMemberIdCgpans(Map map)
    {
        memberIdCgpans = map;
    }

    public Map getClosureDetailsNotPaid()
    {
        return closureDetailsNotPaid;
    }

    public void setClosureDetailsNotPaid(Map map)
    {
        closureDetailsNotPaid = map;
    }

    public Map getClFlag()
    {
        return clFlag;
    }

    public Map getReasonForCl()
    {
        return reasonForCl;
    }

    public Map getRemarksForCl()
    {
        return remarksForCl;
    }

    public void setClFlag(Map map)
    {
        clFlag = map;
    }

    public void setReasonForCl(Map map)
    {
        reasonForCl = map;
    }

    public void setRemarksForCl(Map map)
    {
        remarksForCl = map;
    }

    public Map getClCgpan()
    {
        return clCgpan;
    }

    public void setClCgpan(Map map)
    {
        clCgpan = map;
    }

    public ArrayList getClReasons()
    {
        return clReasons;
    }

    public void setClosureDate(Date d)
    {
        closureDate = d;
    }

    public Date getclosureDate()
    {
        return closureDate;
    }

    public void setClosureRemarks(String closureRemarks)
    {
        this.closureRemarks = closureRemarks;
    }

    public ArrayList getClosureDetailsReq()
    {
        return closureDetailsReq;
    }

    public void setClosureDetailsReq(ArrayList closureDetailsReq)
    {
        this.closureDetailsReq = closureDetailsReq;
    }

    public String getclosureRemarks()
    {
        return closureRemarks;
    }

    public void setClReasons(ArrayList list)
    {
        clReasons = list;
    }

    public void resetOutstandingDtls()
    {
        tcPrincipalOutstandingAmount.clear();
        tcOutstandingAsOnDate.clear();
    }

    public Map getApproveBorrowerFlag()
    {
        return approveBorrowerFlag;
    }

    public void setApproveBorrowerFlag(Map map)
    {
        approveBorrowerFlag = map;
    }

    public Map getBidsList()
    {
        return bidsList;
    }

    public void setBidsList(Map map)
    {
        bidsList = map;
    }
    public Map getCgpandetails() {
		return Cgpandetails;
	}

	public void setCgpandetails(Map cgpandetails) {
		Cgpandetails = cgpandetails;
	}
    public String getSelectAll()
    {
        return selectAll;
    }

    public void setSelectAll(String string)
    {
        selectAll = string;
    }

    public void setReviseOfTenure(String string)
    {
        reviseOfTenure = string;
    }

    public String getReviseOfTenure()
    {
        return reviseOfTenure;
    }

    public void setLastDateOfPayment(String string)
    {
        lastDateOfPayment = string;
    }

    public String getLastDateOfPayment()
    {
        return lastDateOfPayment;
    }

    public void setModificationOfRemarks(String string)
    {
        modificationOfRemarks = string;
    }

    public String getModificationOfRemarks()
    {
        return modificationOfRemarks;
    }

    public String getTenure()
    {
        return tenure;
    }

    public void setTenure(String string)
    {
        tenure = string;
    }

    public void setBankName(String bankName)
    {
        this.bankName = bankName;
    }

    public String getBankName()
    {
        return bankName;
    }

    public void setBranchName(String branchName)
    {
        this.branchName = branchName;
    }

    public String getBranchName()
    {
        return branchName;
    }

    public void setZoneName(String zoneName)
    {
        this.zoneName = zoneName;
    }

    public String getZoneName()
    {
        return zoneName;
    }

    public void setExpiryDate(String expiryDate)
    {
        this.expiryDate = expiryDate;
    }

    public String getExpiryDate()
    {
        return expiryDate;
    }

    public void setUnitName(String unitName)
    {
        this.unitName = unitName;
    }

    public String getUnitName()
    {
        return unitName;
    }

    public void setGuaranteeStartDate(Date guaranteeStartDate)
    {
        this.guaranteeStartDate = guaranteeStartDate;
    }

    public Date getGuaranteeStartDate()
    {
        return guaranteeStartDate;
    }

    public void setFirstDisbursementDate(Date firstDisbursementDate)
    {
        this.firstDisbursementDate = firstDisbursementDate;
    }

    public Date getFirstDisbursementDate()
    {
        return firstDisbursementDate;
    }

    public void setAmountDisbursed(double amountDisbursed)
    {
        this.amountDisbursed = amountDisbursed;
    }

    public double getAmountDisbursed()
    {
        return amountDisbursed;
    }

    /**
     * @param moratoriumperiod
     */
    public void setMoratoriumperiod(int moratoriumperiod)
    {
        this.moratoriumperiod = moratoriumperiod;
    }

    public int getMoratoriumperiod()
    {
        return moratoriumperiod;
    }

    public void setFirstInstallmentDate(String firstInstallmentDate)
    {
        this.firstInstallmentDate = firstInstallmentDate;
    }

    public String getFirstInstallmentDate()
    {
        return firstInstallmentDate;
    }

    public void setNpaConfirm(String npaConfirm)
    {
        this.npaConfirm = npaConfirm;
    }

    public String getNpaConfirm()
    {
        return npaConfirm;
    }

    public GMActionForm()
    {
        $init$();
    }

    private Map cgpans;
    private Map schemes;
    private ArrayList borrowerIds;
    private String borrowerId;
    private String cgpan;
    private String borrowerName;
    private String memberId;
    private String memberIdForExport;
    private String firstInstallmentDate;
    private String npaConfirm;
    private String periodicBankId;
    private String bankIdForClosure;
    private int moratoriumperiod;
    private Date dateOfRecovery;
    private double amountRecovered;
    private double legalCharges;
    private String remarks;
    private String isRecoveryBySaleOfAsset;
    private String detailsOfAssetSold;
    private String isRecoveryByOTS;
    private Date guaranteeStartDate;
    private Date firstDisbursementDate;
    private double amountDisbursed;
    private Map cgpansForWc;
    private Map cgpansForTc;
    private Map tcPrincipalOutstandingAmount;
    private Map tcInterestOutstandingAmount;
    private Map tcOutstandingAsOnDate;
    
    private Map wcFBPrincipalOutstandingAmount;
    private Map wcFBInterestOutstandingAmount;
    private Map wcFBOutstandingAsOnDate;
    private Map wcNFBInterestOutstandingAmount;
    private Map wcNFBPrincipalOutstandingAmount;
    private Map wcNFBOutstandingAsOnDate;
    
    private Map termCreditId;
    private Map workingCapitalId;
    private Map disbursementDate;
    private Map disbursementAmount;
    private Map finalDisbursement;
    private Map disbursementId;
    private Map firstInstallmentDueDate;
    private Map moratorium;
    private Map noOfInstallment;
    private Map periodicity;
    private int rowCount;
    private Map repaymentAmount;
    private Map repaymentDate;
    private Map repaymentId;
    private String borrowerIdForClosure;
    private String cgpanForClosure;
    private String memberIdForClosure;
    private String borrowerNameForClosure;
    private Date closureDate;
    private String closureRemarks;
    private ArrayList closureDetailsReq;
    private Map closureFlag;
    private Map closureCgpans;
    private Map closureDetails;
    private ArrayList closureReasons;
    private Map reasonForClosure;
    private Map remarksForClosure;
    private Map closureDetailsNotPaid;
    private String feeNotPaid;
    private Map memberIdCgpans;
    private Map clFlag;
    private Map reasonForCl;
    private Map remarksForCl;
    private Map clCgpan;
    private ArrayList clReasons;
    private String memberIdForShifting;
    private ArrayList memberIdsForShifting;
    private String borrowerIdForShifting;
    private ArrayList borrowerIdsForShifting;
    private String cgpanForShifting;
    private ArrayList cgpansForShifting;
    private String memberIdToShift;
    private ArrayList memberIdsToShift;
    private String cgpanForSchedule;
    private String borrowerIdForSchedule;
    private String borrowerNameForSchedule;
    private String memberIdForSchedule;
    private String bankIdForSchedule;
    private ArrayList osPeriodicInfoDetails;
    private ArrayList disbPeriodicInfoDetails;
    private ArrayList repayPeriodicInfoDetails;
    private ArrayList repayPeriodicInfoDetailsTemp;
    private ArrayList repaymentSchedules;
    private int noOfActions;
    private Map actionTypeDb;
    private Map actionDetailsDb;
    private Map actionDateDb;
    private Map attachmentNameDb;
    private Map radId;
    private String npaId;
    private ArrayList recTypes;
    private ArrayList recoveryProcedures;
    private Map recProcedures;
    private String legalSuitNo;
    private String courtName;
    private String location;
    private Date dtOfFilingLegalSuit;
    private double amountClaimed;
    private String forumName;
    private String recoveryProceedingsConcluded;
    private String currentStatus;
    private Date npaDate;
    private double osAmtOnNPA;
    private String whetherNPAReported;
    private Date reportingDate;
    private String npaReason;
    private String willfulDefaulter;
    private String effortsTaken;
    private String isRecoveryInitiated;
    private Date effortsConclusionDate;
    private String mliCommentOnFinPosition;
    private String detailsOfFinAssistance;
    private String creditSupport;
    private String bankFacilityDetail;
    private String placeUnderWatchList;
    private String remarksOnNpa;
    private String initiatedName;
    private String bidForCgpanLink;
    private String borrowerNameForLink;
    private String cityForCgpanLink;
    private String promoterNameForLink;
    private double amountApprovedForLink;
    private double tcAmountSanctionedForLink;
    private double wcAmountSanctionedForLink;
    private Date guaranteeIssueDateForLink;
    private NPADetails npaDetails;
    private LegalSuitDetail legalSuitDetail;
    private RecoveryProcedure recoveryProcedure;
    private BorrowerDetails borrowerDetails;
    private Map recoveryDetails;
    private Map approveBorrowerFlag;
    private Map bidsList;
   

	private Map  Cgpandetails;    

	private String selectAll;
    private String selectMember;
    String bankId;
    String zoneId;
    String branchId;
    String targetURL;
    private String danNo;
    private Map clearCgpan;    
    private Map closureCgpan;
    private Map commentCgpan;
    private ArrayList danSummaries;
    private ArrayList memberList;
    private String reviseOfTenure;
    private String lastDateOfPayment;
    private String modificationOfRemarks;
    private String tenure;
    private String bankName;
    private String branchName;
    private String zoneName;
    private String expiryDate;
    private String unitName;
    private ApplicationReport applicationReport;
    
// added by upchar@path on 13-03-2013     
    
    private String isAcctReconstructed;
    private String isSubsidyRcvd;
    private String isInvolveAnySubsidy;
    private String reasonForReduction;
    private Date lastInspectionDt;
    private String netWorthOfGuarantor;
    private Date sanctionDate;
    private Date lastDisbursementDate;
    private HashMap guaranteeStartDates;
    private HashMap sanctionDates;
    private String isAsPerRBI;
    private double totalGuaranteeAmount;
    
    //added by vinod@path 06-JAN-2016
    private String lstNpaDt;
    private String reasonForNpa;
    private String upGradationDt;
    private String mliRemarks;
    private String comment;    
    private ArrayList npaApprovalList;
    private String newNpaDt;
    private String actionType;
    private String textarea[];
    
    //added by vinod@path 28-jan-2016
    private ArrayList npaRegistFormList;
    private String empFName;
    private String empMName;
    private String empLName;  
    private String empId;
    private String designation;
    private String phoneNo;
    private String emailId;
    private String hintQues;
    private String hintAns;
    private String check[];
    private String checkerId;
    private Map empComments;
        
    public String getEmpFName() {
		return empFName;
	}

	public void setEmpFName(String empFName) {
		this.empFName = empFName;
	}

	public String getEmpMName() {
		return empMName;
	}

	public void setEmpMName(String empMName) {
		this.empMName = empMName;
	}

	public String getEmpLName() {
		return empLName;
	}

	public void setEmpLName(String empLName) {
		this.empLName = empLName;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getHintQues() {
		return hintQues;
	}

	public void setHintQues(String hintQues) {
		this.hintQues = hintQues;
	}

	public String getHintAns() {
		return hintAns;
	}

	public void setHintAns(String hintAns) {
		this.hintAns = hintAns;
	}

	public String[] getCheck() {
		return check;
	}

	public void setCheck(String[] check) {
		this.check = check;
	}

	public String getCheckerId() {
		return checkerId;
	}

	public void setCheckerId(String checkerId) {
		this.checkerId = checkerId;
	}

	public Map getEmpComments() {
		return empComments;
	}

	public void setEmpComments(Map empComments) {
		this.empComments = empComments;
	}

	public ArrayList getNpaRegistFormList() {
		return npaRegistFormList;
	}

	public void setNpaRegistFormList(ArrayList npaRegistFormList) {
		this.npaRegistFormList = npaRegistFormList;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String[] getTextarea() {
		return textarea;
	}

	public void setTextarea(String[] textarea) {
		this.textarea = textarea;
	}

	public String getLstNpaDt() {
		return lstNpaDt;
	}

	public void setLstNpaDt(String lstNpaDt) {
		this.lstNpaDt = lstNpaDt;
	}

	public String getReasonForNpa() {
		return reasonForNpa;
	}

	public void setReasonForNpa(String reasonForNpa) {
		this.reasonForNpa = reasonForNpa;
	}

	public String getUpGradationDt() {
		return upGradationDt;
	}

	public void setUpGradationDt(String upGradationDt) {
		this.upGradationDt = upGradationDt;
	}

	public String getMliRemarks() {
		return mliRemarks;
	}

	public void setMliRemarks(String mliRemarks) {
		this.mliRemarks = mliRemarks;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setIsAcctReconstructed(String isAcctReconstructed) {
        this.isAcctReconstructed = isAcctReconstructed;
    }
	
    public ArrayList getNpaApprovalList() {
		return npaApprovalList;
	}

	public void setNpaApprovalList(ArrayList npaApprovalList) {
		this.npaApprovalList = npaApprovalList;
	}
	
	public String getNewNpaDt() {
		return newNpaDt;
	}

	public void setNewNpaDt(String newNpaDt) {
		this.newNpaDt = newNpaDt;
	}

	public String getIsAcctReconstructed() {
        return isAcctReconstructed;
    }

    public void setIsSubsidyRcvd(String isSubsidyRcvd) {
        this.isSubsidyRcvd = isSubsidyRcvd;
    }

    public String getIsSubsidyRcvd() {
        return isSubsidyRcvd;
    }

    public void setIsInvolveAnySubsidy(String isInvolveAnySubsidy) {
        this.isInvolveAnySubsidy = isInvolveAnySubsidy;
    }

    public String getIsInvolveAnySubsidy() {
        return isInvolveAnySubsidy;
    }

    public void setReasonForReduction(String reasonForReduction) {
        this.reasonForReduction = reasonForReduction;
    }

    public String getReasonForReduction() {
        return reasonForReduction;
    }

    public void setLastInspectionDt(Date lastInspectionDt) {
        this.lastInspectionDt = lastInspectionDt;
    }

    public Date getLastInspectionDt() {
        return lastInspectionDt;
    }


    public void setSanctionDate(Date sanctionDate) {
        this.sanctionDate = sanctionDate;
    }

    public Date getSanctionDate() {
        return sanctionDate;
    }

    public void setLastDisbursementDate(Date lastDisbursementDate) {
        this.lastDisbursementDate = lastDisbursementDate;
    }

    public Date getLastDisbursementDate() {
        return lastDisbursementDate;
    }
    
//  added by upchar@path on 20-03-2013
    private List npaDetailsForCgpan;
    private HashMap cgpansForNpaDetail;
    private HashMap cgpanNos;   
    private HashMap firstDisbursementDates;
    private HashMap lastDisbursementDates;
    private HashMap firstInstallmentDates;
    private HashMap amountDisbursedValues;
    private HashMap principalmoratoriums;
    private HashMap Interestmoratoriums;
    private HashMap isSubsidyValues;
    private HashMap isSubsidyRcvdValues;
    private HashMap isSubsidyAdjustedValues;
    private int moratoriumperiodInterest;
    private String isSubsidyAdjusted;
    private HashMap tcPrincipalOsAmnts;
    private HashMap wcPrincipalOsAmnts;
    private HashMap tcInterestOsAmnts;
    private HashMap wcInterestOsAmnts;
   
    private Map asOnDtOfSanctionDtl;
    private Map asOnDtOfNPADtl;
    private Vector cgpansVector;
    private Vector tccgpansVector;
    private Vector wccgpansVector;
    
    private Map securityAsOnDtOfSanction;
    private Map repaymentAmountPrincipal;
    private Map repaymentAmountInterest;
    
    private Map tcDisbursementAmount;
    private Map wcDisbursementAmount;
    private Map tcCgpanMap;
    private Map wcCgpanMap;


    public void setCgpansForNpaDetail(HashMap cgpansForNpaDetail) {
        this.cgpansForNpaDetail = cgpansForNpaDetail;
    }

    public HashMap getCgpansForNpaDetail() {
        return cgpansForNpaDetail;
    }


    public void setGuaranteeStartDates(HashMap guaranteeStartDates) {
        this.guaranteeStartDates = guaranteeStartDates;
    }

    public HashMap getGuaranteeStartDates() {
        return guaranteeStartDates;
    }

    public void setSanctionDates(HashMap sanctionDates) {
        this.sanctionDates = sanctionDates;
    }

    public HashMap getSanctionDates() {
        return sanctionDates;
    }

    public void setCgpanNos(HashMap cgpanNos) {
        this.cgpanNos = cgpanNos;
    }

    public HashMap getCgpanNos() {
        return cgpanNos;
    }

    public void setFirstDisbursementDates(HashMap firstDisbursementDates) {
        this.firstDisbursementDates = firstDisbursementDates;
    }

    public HashMap getFirstDisbursementDates() {
        return firstDisbursementDates;
    }

    public void setLastDisbursementDates(HashMap lastDisbursementDates) {
        this.lastDisbursementDates = lastDisbursementDates;
    }

    public HashMap getLastDisbursementDates() {
        return lastDisbursementDates;
    }
    public void setLastDisbursementDates(String key,Object value) {
        lastDisbursementDates.put(key, value);
    }

    public Object getLastDisbursementDates(String key) {
        return lastDisbursementDates.get(key);
    }

    public void setFirstInstallmentDates(HashMap firstInstallmentDates) {
        this.firstInstallmentDates = firstInstallmentDates;
    }

    public HashMap getFirstInstallmentDates() {
        return firstInstallmentDates;
    }
    public void setFirstInstallmentDates(String key,Object value) {
        firstInstallmentDates.put(key, value);
    }

    public Object getFirstInstallmentDates(String key) {
        return firstInstallmentDates.get(key);
    }
    

    public void setAmountDisbursedValues(HashMap amountDisbursedValues) {
        this.amountDisbursedValues = amountDisbursedValues;
    }
    public HashMap getAmountDisbursedValues() {
        return amountDisbursedValues;
    }
    public void setAmountDisbursedValues(String key,Object value) {
        amountDisbursedValues.put(key, value);
    }

    public Object getAmountDisbursedValues(String key) {
        return amountDisbursedValues.get(key);
    }



    public void setPrincipalmoratoriums(HashMap principalmoratoriums) {
        this.principalmoratoriums = principalmoratoriums;
    }

    public HashMap getPrincipalmoratoriums() {
        return principalmoratoriums;
    }
    public void setPrincipalmoratoriums(String key,Object value) {
        principalmoratoriums.put(key, value);
    }

    public Object getPrincipalmoratoriums(String key) {
        return principalmoratoriums.get(key);
    }



    public void setInterestmoratoriums(HashMap interestmoratoriums) {
        this.Interestmoratoriums = interestmoratoriums;
    }
    public HashMap getInterestmoratoriums() {
        return Interestmoratoriums;
    }
    public void setInterestmoratoriums(String key,Object value) {
        Interestmoratoriums.put(key, value);
    }
    public Object getInterestmoratoriums(String key) {
        return Interestmoratoriums.get(key);
    }


    public void setIsSubsidyValues(HashMap isSubsidyValues) {
        this.isSubsidyValues = isSubsidyValues;
    }
    public HashMap getIsSubsidyValues() {
        return isSubsidyValues;
    }
    public void setIsSubsidyValues(String key,Object value) {
        isSubsidyValues.put(key, value);
    }
    public Object getIsSubsidyValues(String key) {
        return isSubsidyValues.get(key);
    }


    public void setIsSubsidyRcvdValues(HashMap isSubsidyRcvdValues) {
        this.isSubsidyRcvdValues = isSubsidyRcvdValues;
    }
    public HashMap getIsSubsidyRcvdValues() {
        return isSubsidyRcvdValues;
    }
    public void setIsSubsidyRcvdValues(String key,Object value) {
        isSubsidyRcvdValues.put(key, value);
    }
    public Object getIsSubsidyRcvdValues(String key) {
        return isSubsidyRcvdValues.get(key);
    }
    

    public void setIsSubsidyAdjustedValues(HashMap isSubsidyAdjustedValues) {
        this.isSubsidyAdjustedValues = isSubsidyAdjustedValues;
    }
    public HashMap getIsSubsidyAdjustedValues() {
        return isSubsidyAdjustedValues;
    }  
    public void setIsSubsidyAdjustedValues(String key,Object value) {
        isSubsidyAdjustedValues.put(key, value);
    }
    public Object getIsSubsidyAdjustedValues(String key) {
        return isSubsidyAdjustedValues.get(key);
    }


    public void setMoratoriumperiodInterest(int moratoriumperiodInterest) {
        this.moratoriumperiodInterest = moratoriumperiodInterest;
    }
    public int getMoratoriumperiodInterest() {
        return moratoriumperiodInterest;
    }

    public void setIsSubsidyAdjusted(String isSubsidyAdjusted) {
        this.isSubsidyAdjusted = isSubsidyAdjusted;
    }

    public String getIsSubsidyAdjusted() {
        return isSubsidyAdjusted;
    }


    public void setNpaDetailsForCgpan(List npaDetailsForCgpan) {
        this.npaDetailsForCgpan = npaDetailsForCgpan;
    }

    public List getNpaDetailsForCgpan() {
        return npaDetailsForCgpan;
    }

    public void setTcPrincipalOsAmnts(HashMap tcPrincipalOsAmnts) {
        this.tcPrincipalOsAmnts = tcPrincipalOsAmnts;
    }
    public HashMap getTcPrincipalOsAmnts() {
        return tcPrincipalOsAmnts;
    }
    public void setTcPrincipalOsAmnts(String key, Object value)
    {
        tcPrincipalOsAmnts.put(key, value);
    }
    public Object getTcPrincipalOsAmnts(String key)
    {
        return tcPrincipalOsAmnts.get(key);
    }
    

    public void setWcPrincipalOsAmnts(HashMap wcPrincipalOsAmnts) {
        this.wcPrincipalOsAmnts = wcPrincipalOsAmnts;
    }
    public HashMap getWcPrincipalOsAmnts() {
        return wcPrincipalOsAmnts;
    }
    public void setWcPrincipalOsAmnts(String key, Object value)
    {
        wcPrincipalOsAmnts.put(key, value);
    }
    public Object getWcPrincipalOsAmnts(String key)
    {
        return wcPrincipalOsAmnts.get(key);
    }


    public void setTcInterestOsAmnts(HashMap tcInterestOsAmnts) {
        this.tcInterestOsAmnts = tcInterestOsAmnts;
    }
    public HashMap getTcInterestOsAmnts() {
        return tcInterestOsAmnts;
    }
    public void setTcInterestOsAmnts(String key, Object value)
    {
        tcInterestOsAmnts.put(key, value);
    }
    public Object getTcInterestOsAmnts(String key)
    {
        return tcInterestOsAmnts.get(key);
    }


    public void setWcInterestOsAmnts(HashMap wcInterestOsAmnts) {
        this.wcInterestOsAmnts = wcInterestOsAmnts;
    }
    public HashMap getWcInterestOsAmnts() {
        return wcInterestOsAmnts;
    }
    public void setWcInterestOsAmnts(String key, Object value)
    {
        wcInterestOsAmnts.put(key, value);
    }
    public Object getWcInterestOsAmnts(String key)
    {
        return wcInterestOsAmnts.get(key);
    }
    
    
    /* added by upchar@path */
     public Object getFirstDisbursementDates(String key)
     {
         return firstDisbursementDates.get(key);
     }
     public void setFirstDisbursementDates(String key, Object value)
     {
         firstDisbursementDates.put(key, value);
     }


    // added by upchar@path on 12-04-2013
        private HashMap npaDetailsMap;
        
    public void setNpaDetailsMap(HashMap npaDetailsMap) {
        this.npaDetailsMap = npaDetailsMap;
    }

    public HashMap getNpaDetailsMap() {
        return npaDetailsMap;
    }

    public void setCgpansVector(Vector cgpansVector) {
        this.cgpansVector = cgpansVector;
    }

    public Vector getCgpansVector() {
        return cgpansVector;
    }

    public void setTccgpansVector(Vector tccgpansVector) {
        this.tccgpansVector = tccgpansVector;
    }

    public Vector getTccgpansVector() {
        return tccgpansVector;
    }

    public void setWccgpansVector(Vector wccgpansVector) {
        this.wccgpansVector = wccgpansVector;
    }

    public Vector getWccgpansVector() {
        return wccgpansVector;
    }


    public void setAsOnDtOfSanctionDtl(Map asOnDtOfSanctionDtl) {
        this.asOnDtOfSanctionDtl = asOnDtOfSanctionDtl;
    }

    public Map getAsOnDtOfSanctionDtl() {
        return asOnDtOfSanctionDtl;
    }

    public void setNetWorthOfGuarantor(String netWorthOfGuarantor) {
        this.netWorthOfGuarantor = netWorthOfGuarantor;
    }

    public String getNetWorthOfGuarantor() {
        return netWorthOfGuarantor;
    }

    public void setAsOnDtOfNPADtl(Map asOnDtOfNPADtl) {
        this.asOnDtOfNPADtl = asOnDtOfNPADtl;
    }

    public Map getAsOnDtOfNPADtl() {
        return asOnDtOfNPADtl;
    }
    
  

    public void setSecurityAsOnDtOfSanction(Map securityAsOnDtOfSanction) {
        this.securityAsOnDtOfSanction = securityAsOnDtOfSanction;
    }

    public Map getSecurityAsOnDtOfSanction() {
        return securityAsOnDtOfSanction;
    }


    public void setRepaymentAmountPrincipal(Map repaymentAmountPrincipal) {
        this.repaymentAmountPrincipal = repaymentAmountPrincipal;
    }
    public Map getRepaymentAmountPrincipal() {
        return repaymentAmountPrincipal;
    }
    public void setRepaymentAmountPrincipal(String key, Object value)
    {
        repaymentAmountPrincipal.put(key, value);
    }
    public Object getRepaymentAmountPrincipal(String key)
    {
        return repaymentAmountPrincipal.get(key);
    }
    

    public void setRepaymentAmountInterest(Map repaymentAmountInterest) {
        this.repaymentAmountInterest = repaymentAmountInterest;
    }
    public Map getRepaymentAmountInterest() {
        return repaymentAmountInterest;
    }
    public void setRepaymentAmountInterest(String key, Object value)
    {
        repaymentAmountInterest.put(key, value);
    }
    public Object getRepaymentAmountInterest(String key)
    {
        return repaymentAmountInterest.get(key);
    }
    
    int tcCounter;
    int wcCounter;

    public void setTcCounter(int tcCounter) {
        this.tcCounter = tcCounter;
    }

    public int getTcCounter() {
        return tcCounter;
    }

    public void setWcCounter(int wcCounter) {
        this.wcCounter = wcCounter;
    }

    public int getWcCounter() {
        return wcCounter;
    }

    public void setTcDisbursementAmount(Map tcDisbursementAmount) {
        this.tcDisbursementAmount = tcDisbursementAmount;
    }

    public Map getTcDisbursementAmount() {
        return tcDisbursementAmount;
    }

    public void setWcDisbursementAmount(Map wcDisbursementAmount) {
        this.wcDisbursementAmount = wcDisbursementAmount;
    }

    public Map getWcDisbursementAmount() {
        return wcDisbursementAmount;
    }

    public void setTcCgpanMap(Map tcCgpanMap) {
        this.tcCgpanMap = tcCgpanMap;
    }

    public Map getTcCgpanMap() {
        return tcCgpanMap;
    }

    public void setWcCgpanMap(Map wcCgpanMap) {
        this.wcCgpanMap = wcCgpanMap;
    }

    public Map getWcCgpanMap() {
        return wcCgpanMap;
    }

    public void setIsAsPerRBI(String isAsPerRBI) {
        this.isAsPerRBI = isAsPerRBI;
    }

    public String getIsAsPerRBI() {
        return isAsPerRBI;
    }

    public void setTotalGuaranteeAmount(double totalGuaranteeAmount) {
        this.totalGuaranteeAmount = totalGuaranteeAmount;
    }

    public double getTotalGuaranteeAmount() {
        return totalGuaranteeAmount;
    }
    private ArrayList cgmap;

	public ArrayList getCgmap() {
		return cgmap;
	}

	public void setCgmap(ArrayList cgmap) {
		this.cgmap = cgmap;
	}
	private String CGPAN;
	private String WCP_FB_LIMIT_SANCTIONED;
	private String WCP_NFB_LIMIT_SANCTIONED;
	private String WCP_NFB_CREDIT_TO_GUARANTEE; 
	private String WCP_FB_CREDIT_TO_GUARANTEE;
	private String[] decision;
	private String mliRequestDate;
	private String mliRequestDt;
    public String getMliRequestDt() {
		return mliRequestDt;
	}

	public String setMliRequestDt(String mliRequestDt) {
	 return 	this.mliRequestDt = mliRequestDt;
	}

		public String getMliRequestDate() {
		return mliRequestDate;
	}

	public void setMliRequestDate(String mliRequestDate) {
		this.mliRequestDate = mliRequestDate;
	}

		public String getCGPAN() {
	  		return CGPAN;
	  	}
	  	public String[] getDecision() {
			return decision;
		}
		public void setDecision(String[] decision) {
			this.decision = decision;
		}
		public void setCGPAN(String cGPAN) {
	  		CGPAN = cGPAN;
	  	}
	  	
	       public String getWCP_FB_CREDIT_TO_GUARANTEE() {
			return WCP_FB_CREDIT_TO_GUARANTEE;
		}
		public void setWCP_FB_CREDIT_TO_GUARANTEE(String wCP_FB_CREDIT_TO_GUARANTEE) {
			WCP_FB_CREDIT_TO_GUARANTEE = wCP_FB_CREDIT_TO_GUARANTEE;
		}

	       
	    public String getWCP_FB_LIMIT_SANCTIONED() {
			return WCP_FB_LIMIT_SANCTIONED;
		}
		public void setWCP_FB_LIMIT_SANCTIONED(String wCP_FB_LIMIT_SANCTIONED) {
			WCP_FB_LIMIT_SANCTIONED = wCP_FB_LIMIT_SANCTIONED;
		}
		public String getWCP_NFB_LIMIT_SANCTIONED() {
			return WCP_NFB_LIMIT_SANCTIONED;
		}
		public void setWCP_NFB_LIMIT_SANCTIONED(String wCP_NFB_LIMIT_SANCTIONED) {
			WCP_NFB_LIMIT_SANCTIONED = wCP_NFB_LIMIT_SANCTIONED;
		}
		public String getWCP_NFB_CREDIT_TO_GUARANTEE() {
			return WCP_NFB_CREDIT_TO_GUARANTEE;
		}
		public void setWCP_NFB_CREDIT_TO_GUARANTEE(String wCP_NFB_CREDIT_TO_GUARANTEE) {
			WCP_NFB_CREDIT_TO_GUARANTEE = wCP_NFB_CREDIT_TO_GUARANTEE;
		}
}
