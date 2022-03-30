// Decompiled Using: FrontEnd Plus v2.03 and the JAD Engine
// Available From: http://www.reflections.ath.cx
// Decompiler options: packimports(3) 
// Source File Name:   Application.java

package com.cgtsi.application;

import com.cgtsi.mcgs.MCGFDetails;
import java.io.Serializable;
import java.util.Date;

// Referenced classes of package com.cgtsi.application:
//            BorrowerDetails, ProjectOutlayDetails, TermLoan, WorkingCapital, 
//            RepaymentDetail, Securitization

public class Application
    implements Serializable
{

    public void setSsiRef(String ssiRef)
    {
        this.ssiRef = ssiRef;
    }

    public String getSsiRef()
    {
        return ssiRef;
    }

    public void setActivity(String act)
    {
        activity = act;
    }

    public String getActivity()
    {
        return activity;
    }

    public void setZoneName(String name)
    {
        zoneName = name;
    }

    public String getZoneName()
    {
        return zoneName;
    }

    public void setCoFinanceTaken1(String coFinanceTaken1)
    {
        this.coFinanceTaken1 = coFinanceTaken1;
    }

    public String getCoFinanceTaken1()
    {
        return coFinanceTaken1;
    }

    public void setIcardIssueDate(Date icardIssueDate)
    {
        this.icardIssueDate = icardIssueDate;
    }

    public Date getIcardIssueDate()
    {
        return icardIssueDate;
    }

    public void setIcardNo(String icardNo)
    {
        this.icardNo = icardNo;
    }

    public String getIcardNo()
    {
        return icardNo;
    }

    public void setHandiCrafts(String handiCrafts)
    {
        this.handiCrafts = handiCrafts;
    }

    public String getHandiCrafts()
    {
        return handiCrafts;
    }

    public void setJointFinance(String jointFinance)
    {
        this.jointFinance = jointFinance;
    }

    public String getJointFinance()
    {
        return jointFinance;
    }

    public void setDcHandicrafts(String dcHandicrafts)
    {
        this.dcHandicrafts = dcHandicrafts;
    }

    public String getDcHandicrafts()
    {
        return dcHandicrafts;
    }

    public void setInternalRate(String internalRate)
    {
        this.internalRate = internalRate;
    }

    public String getInternalRate()
    {
        return internalRate;
    }

    public void setExternalRate(String externalRate)
    {
        this.externalRate = externalRate;
    }

    public String getExternalRate()
    {
        return externalRate;
    }

    public void setDistrict(String district)
    {
        this.district = district;
    }

    public String getDistrict()
    {
        return district;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getState()
    {
        return state;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }

    public String getSex()
    {
        return sex;
    }

    public void setSocialCategory(String socialCategory)
    {
        this.socialCategory = socialCategory;
    }

    public String getSocialCategory()
    {
        return socialCategory;
    }

    public Application()
    {
        borrowerDetails = null;
        projectOutlayDetails = null;
        loanType = null;
        applicationType = null;
        termLoan = null;
        wc = null;
        approvedAmount = 0.0D;
        sanctionedAmount = 0.0D;
        reapprovedAmount = 0.0D;
        enhancementAmount = 0.0D;
        docRefNo = null;
        reapprovalRemarks = null;
        cgpan = null;
        cgpanReference = null;
        userId = null;
        bankId = null;
        zoneId = null;
        branchId = null;
        appRefNo = null;
        itpan=null;		//Diksha 02/11/2017
        wcAppRefNo = null;
        regionId = null;
        collateralSecDtls = null;
        subsidyProvided = 0;
        submittedDate = null;
        sanctionedDate = null;
        approvedDate = null;
        guaranteeStartDate = null;
        appExpiryDate = null;
        remarks = null;
        prevSSI = null;
        existSSI = null;
        status = null;
        projectType = 0;
        securitization = null;
        mcgfDetails = null;
        guaranteeFee = 0.0D;
        coFinanceTaken1 = "N";
         msE="N";
         
        AppmsE="N";
        
        district = "";
        state = "";
        sex = "";
        socialCategory = "";
        internalRate = null;
        externalRate = null;
        handiCrafts = null;
        dcHandicrafts = null;
        icardNo = null;
        icardIssueDate = null;
        jointFinance = null;
        jointcgpan = null;
        activityConfirm = null;
    }

    public String getMliID()
    {
        return mliID;
    }

    public void setMliID(String aMliID)
    {
        mliID = aMliID;
    }

    public String getMliBranchName()
    {
        return mliBranchName;
    }

    public void setMliBranchName(String aMliBranchName)
    {
        mliBranchName = aMliBranchName;
    }

    public BorrowerDetails getBorrowerDetails()
    {
        return borrowerDetails;
    }

    public void setBorrowerDetails(BorrowerDetails aBorrowerDetails)
    {
        borrowerDetails = aBorrowerDetails;
    }

    public TermLoan getTermLoan()
    {
        return termLoan;
    }

    public void setTermLoan(TermLoan aTermLoan)
    {
        termLoan = aTermLoan;
    }

    public WorkingCapital getWc()
    {
        return wc;
    }

    public void setWc(WorkingCapital aWc)
    {
        wc = aWc;
    }

    public String getLoanType()
    {
        return loanType;
    }

    public void setLoanType(String aLoanType)
    {
        loanType = aLoanType;
    }

    public String getCgpan()
    {
        return cgpan;
    }

    public void setCgpan(String aCgpan)
    {
        cgpan = aCgpan;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String aUserId)
    {
        userId = aUserId;
    }

    public String getBankId()
    {
        return bankId;
    }

    public void setBankId(String aBankId)
    {
        bankId = aBankId;
    }

    public String getZoneId()
    {
        return zoneId;
    }

    public void setZoneId(String aZoneId)
    {
        zoneId = aZoneId;
    }

    public String getBranchId()
    {
        return branchId;
    }

    public void setBranchId(String aBranchId)
    {
        branchId = aBranchId;
    }

    public String getAppRefNo()
    {
        return appRefNo;
    }

    public void setAppRefNo(String aAppRefNo)
    {
        appRefNo = aAppRefNo;
    }

    public String getWcAppRefNo()
    {
        return wcAppRefNo;
    }

    public void setWcAppRefNo(String aWcAppRefNo)
    {
        wcAppRefNo = aWcAppRefNo;
    }
//Diksha 02/11/2017
    public String getItpan() {
		return itpan;
	}

	public void setItpan(String itpan) {
		this.itpan = itpan;
	}
//Diksha 02/11/2017
	
	public String getNPA()
    {
        return NPA;
    }

    public void setNPA(String aNPA)
    {
        NPA = aNPA;
    }

    public String getCompositeLoan()
    {
        return compositeLoan;
    }

    public void setCompositeLoan(String aCompositeLoan)
    {
        compositeLoan = aCompositeLoan;
    }

    public String getCollateralSecDtls()
    {
        return collateralSecDtls;
    }

    public void setCollateralSecDtls(String aCollateralSecDtls)
    {
        collateralSecDtls = aCollateralSecDtls;
    }

    public int getSubsidyProvided()
    {
        return subsidyProvided;
    }

    public void setSubsidyProvided(int aSubsidyProvided)
    {
        subsidyProvided = aSubsidyProvided;
    }

    public Date getSubmittedDate()
    {
        return submittedDate;
    }

    public void setSubmittedDate(Date aSubmittedDate)
    {
        submittedDate = aSubmittedDate;
    }

    public Date getSanctionedDate()
    {
        return sanctionedDate;
    }

    public void setSanctionedDate(Date aSanctionedDate)
    {
        sanctionedDate = aSanctionedDate;
    }

    public String getRehabilitation()
    {
        return rehabilitation;
    }

    public void setRehabilitation(String aRehabilitation)
    {
        rehabilitation = aRehabilitation;
    }

    public Date getApprovedDate()
    {
        return approvedDate;
    }

    public void setApprovedDate(Date aApprovedDate)
    {
        approvedDate = aApprovedDate;
    }

    public Date getGuaranteeStartDate()
    {
        return guaranteeStartDate;
    }

    public Date getAppExpiryDate()
    {
        return appExpiryDate;
    }

    public void setAppExpiryDate(Date bappExpiryDate)
    {
        appExpiryDate = bappExpiryDate;
    }

    public void setGuaranteeStartDate(Date aGuaranteeStartDate)
    {
        guaranteeStartDate = aGuaranteeStartDate;
    }

    public String getRemarks()
    {
        return remarks;
    }

    public void setRemarks(String aRemarks)
    {
        remarks = aRemarks;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String aStatus)
    {
        status = aStatus;
    }

    public int getProjectType()
    {
        return projectType;
    }

    public void setProjectType(int aProjectType)
    {
        projectType = aProjectType;
    }

    public double getOutstandingAmount()
    {
        return outstandingAmount;
    }

    public void setOutstandingAmount(double aOutstandingAmount)
    {
        outstandingAmount = aOutstandingAmount;
    }

    public double getApprovedAmount()
    {
        return approvedAmount;
    }

    public void setApprovedAmount(double aApprovedAmount)
    {
        approvedAmount = aApprovedAmount;
    }

    public double getSantionedAmount()
    {
        return sanctionedAmount;
    }

    public void setSanctionedAmount(double aSanctionedAmount)
    {
        sanctionedAmount = aSanctionedAmount;
    }

    public String getITPAN()
    {
        return ITPAN;
    }

    public void setITPAN(String aITPAN)
    {
        ITPAN = aITPAN;
    }

    public double getEnhancementAmount()
    {
        return enhancementAmount;
    }

    public void setEnhancementAmount(double aEnhancementAmount)
    {
        enhancementAmount = aEnhancementAmount;
    }

    public double getReapprovedAmount()
    {
        return reapprovedAmount;
    }

    public void setReapprovedAmount(double aReapprovedAmount)
    {
        reapprovedAmount = aReapprovedAmount;
    }

    public String getDocRefNo()
    {
        return docRefNo;
    }

    public void setDocRefNo(String aDocRefNo)
    {
        docRefNo = aDocRefNo;
    }

    public String getReapprovalRemarks()
    {
        return reapprovalRemarks;
    }

    public void setReapprovalRemarks(String aReapprovalRemarks)
    {
        reapprovalRemarks = aReapprovalRemarks;
    }

    public ProjectOutlayDetails getProjectOutlayDetails()
    {
        return projectOutlayDetails;
    }

    public double getSanctionedAmount()
    {
        return sanctionedAmount;
    }

    public String getScheme()
    {
        return scheme;
    }

    public RepaymentDetail getTheRepaymentDetail()
    {
        return theRepaymentDetail;
    }

    public void setProjectOutlayDetails(ProjectOutlayDetails details)
    {
        projectOutlayDetails = details;
    }

    public void setScheme(String string)
    {
        scheme = string;
    }

    public void setTheRepaymentDetail(RepaymentDetail detail)
    {
        theRepaymentDetail = detail;
    }

    public String getRegionId()
    {
        return regionId;
    }

    public void setRegionId(String aRegionId)
    {
        regionId = aRegionId;
    }

    public String getMliBranchCode()
    {
        return mliBranchCode;
    }

    public void setMliBranchCode(String aMliBranchCode)
    {
        mliBranchCode = aMliBranchCode;
    }

    public void setGuaranteeAmount(double guaranteeFee)
    {
        this.guaranteeFee = guaranteeFee;
    }

    public double getGuaranteeAmount()
    {
        return guaranteeFee;
    }

    public String getMliRefNo()
    {
        return mliRefNo;
    }

    public void setMliRefNo(String aMliRefNo)
    {
        mliRefNo = aMliRefNo;
    }

    public Securitization getSecuritization()
    {
        return securitization;
    }

    public void setSecuritization(Securitization aSecuritization)
    {
        securitization = aSecuritization;
    }

    public MCGFDetails getMCGFDetails()
    {
        return mcgfDetails;
    }

    public void setMCGFDetails(MCGFDetails aMCGFDetails)
    {
        mcgfDetails = aMCGFDetails;
    }

    public String getSubSchemeName()
    {
        return subSchemeName;
    }

    public void setSubSchemeName(String aSubSchemeName)
    {
        subSchemeName = aSubSchemeName;
    }

    public String getExistingRemarks()
    {
        return existingRemarks;
    }

    public void setExistingRemarks(String aExistingRemarks)
    {
        existingRemarks = aExistingRemarks;
    }

    public boolean getAdditionalTC()
    {
        return additionalTC;
    }

    public void setAdditionalTC(boolean aAdditionalTC)
    {
        additionalTC = aAdditionalTC;
    }

    public boolean getWcEnhancement()
    {
        return wcEnhancement;
    }

    public void setWcEnhancement(boolean aWcEnhancement)
    {
        wcEnhancement = aWcEnhancement;
    }

    public boolean getWcRenewal()
    {
        return wcRenewal;
    }

    public void setWcRenewal(boolean aWcRenewal)
    {
        wcRenewal = aWcRenewal;
    }

    public String getCgpanReference()
    {
        return cgpanReference;
    }

    public void setCgpanReference(String aCgpanReference)
    {
        cgpanReference = aCgpanReference;
    }

    public boolean getIsVerified()
    {
        return isVerified;
    }

    public void setIsVerified(boolean b)
    {
        isVerified = b;
    }

    public void setJointcgpan(String jointcgpan)
    {
        this.jointcgpan = jointcgpan;
    }

    public String getJointcgpan()
    {
        return jointcgpan;
    }

    public void setApplicationType(String applicationType)
    {
        this.applicationType = applicationType;
    }

    public String getApplicationType()
    {
        return applicationType;
    }

    public void setActivityConfirm(String activityConfirm)
    {
        this.activityConfirm = activityConfirm;
    }

    public String getActivityConfirm()
    {
        return activityConfirm;
    }

    public void setPrevSSI(String prevSSI)
    {
        this.prevSSI = prevSSI;
    }

    public String getPrevSSI()
    {
        return prevSSI;
    }

    public void setExistSSI(String existSSI)
    {
        this.existSSI = existSSI;
    }

    public String getExistSSI()
    {
        return existSSI;
    }
  
    private String mliID;
    private String mliBranchName;
    private String mliBranchCode;
    private String mliRefNo;
    private BorrowerDetails borrowerDetails;
    private ProjectOutlayDetails projectOutlayDetails;
    private String rehabilitation;
    private String compositeLoan;
    private String loanType;
    private String applicationType;
    private String scheme;
    private TermLoan termLoan;
    private WorkingCapital wc;
    private double approvedAmount;
    private double sanctionedAmount;
    private double reapprovedAmount;
    private double enhancementAmount;
    private String docRefNo;
    private String reapprovalRemarks;
    private String cgpan;
    private String cgpanReference;
    private String userId;
    private String bankId;
    private String zoneId;
    private String branchId;
    private String appRefNo;
    
    private String itpan;
    
    private String wcAppRefNo;
    private String regionId;
    private String NPA;
    private String ssiRef;
    private String collateralSecDtls;
    private double outstandingAmount;
    private String ITPAN;
    private int subsidyProvided;
    private Date submittedDate;
    private Date sanctionedDate;
    private String activity;
    private Date approvedDate;
    private Date guaranteeStartDate;
    private Date appExpiryDate;
    private String remarks;
    private String prevSSI;
    private String existSSI;
    private String status;
    private int projectType;
    private RepaymentDetail theRepaymentDetail;
    private Securitization securitization;
    private MCGFDetails mcgfDetails;
    private double guaranteeFee;
    private String subSchemeName;
    private String existingRemarks;
    private boolean additionalTC;
    private boolean wcEnhancement;
    private boolean wcRenewal;
    private boolean isVerified;
    private String zoneName;
    private String coFinanceTaken1;
    private String district;
    private String state;
    private String sex;
    private String socialCategory;
    private String internalRate;
    private String externalRate;
    private String handiCrafts;
    private String dcHandicrafts;
    private String icardNo;
    private Date icardIssueDate;
    private String jointFinance;
    private String jointcgpan;
    private String activityConfirm;
    private String handiCraftsStatus;
    private String dcHandicraftsStatus;
    private String dcHandlooms;
   
    
    private String dcHandloomsStatus;
   
   
    private String WeaverCreditScheme;
    private String handloomchk;
    
    private String msE;
    
    private String AppmsE;
    private String pSecurity; 
//  Added by DKR
    private String gstNo;
    public MCGFDetails getMcgfDetails() {
		return mcgfDetails;
	}

	public void setMcgfDetails(MCGFDetails mcgfDetails) {
		this.mcgfDetails = mcgfDetails;
	}

	public double getGuaranteeFee() {
		return guaranteeFee;
	}

	public void setGuaranteeFee(double guaranteeFee) {
		this.guaranteeFee = guaranteeFee;
	}

	public String getpSecurity() {
		return pSecurity;
	}

	public void setpSecurity(String pSecurity) {
		this.pSecurity = pSecurity;
	}

	public String getGstNo() {
		return gstNo;
	}

	public void setGstNo(String gstNo) {
		this.gstNo = gstNo;
	}

	public String getGstState() {
		return gstState;
	}

	public void setGstState(String gstState) {
		this.gstState = gstState;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public String getHybridSecurity() {
		return hybridSecurity;
	}

	public void setHybridSecurity(String hybridSecurity) {
		this.hybridSecurity = hybridSecurity;
	}

	public Double getMovCollateratlSecurityAmt() {
		return movCollateratlSecurityAmt;
	}

	public void setMovCollateratlSecurityAmt(Double movCollateratlSecurityAmt) {
		this.movCollateratlSecurityAmt = movCollateratlSecurityAmt;
	}

	public Double getImmovCollateratlSecurityAmt() {
		return immovCollateratlSecurityAmt;
	}

	public void setImmovCollateratlSecurityAmt(Double immovCollateratlSecurityAmt) {
		this.immovCollateratlSecurityAmt = immovCollateratlSecurityAmt;
	}

	public Double getTotalMIcollatSecAmt() {
		return totalMIcollatSecAmt;
	}

	public void setTotalMIcollatSecAmt(Double totalMIcollatSecAmt) {
		this.totalMIcollatSecAmt = totalMIcollatSecAmt;
	}

	public Long getProMobileNo() {
		return proMobileNo;
	}

	public void setProMobileNo(Long proMobileNo) {
		this.proMobileNo = proMobileNo;
	}

	public String getPromDirDefaltFlg() {
		return promDirDefaltFlg;
	}

	public void setPromDirDefaltFlg(String promDirDefaltFlg) {
		this.promDirDefaltFlg = promDirDefaltFlg;
	}

	public int getCredBureKeyPromScor() {
		return credBureKeyPromScor;
	}

	public void setCredBureKeyPromScor(int credBureKeyPromScor) {
		this.credBureKeyPromScor = credBureKeyPromScor;
	}

	public int getCredBurePromScor2() {
		return credBurePromScor2;
	}

	public void setCredBurePromScor2(int credBurePromScor2) {
		this.credBurePromScor2 = credBurePromScor2;
	}

	public int getCredBurePromScor3() {
		return credBurePromScor3;
	}

	public void setCredBurePromScor3(int credBurePromScor3) {
		this.credBurePromScor3 = credBurePromScor3;
	}

	public int getCredBurePromScor4() {
		return credBurePromScor4;
	}

	public void setCredBurePromScor4(int credBurePromScor4) {
		this.credBurePromScor4 = credBurePromScor4;
	}

	public int getCredBurePromScor5() {
		return credBurePromScor5;
	}

	public void setCredBurePromScor5(int credBurePromScor5) {
		this.credBurePromScor5 = credBurePromScor5;
	}

	public String getCredBureName1() {
		return credBureName1;
	}

	public void setCredBureName1(String credBureName1) {
		this.credBureName1 = credBureName1;
	}

	public String getCredBureName2() {
		return credBureName2;
	}

	public void setCredBureName2(String credBureName2) {
		this.credBureName2 = credBureName2;
	}

	public String getCredBureName3() {
		return credBureName3;
	}

	public void setCredBureName3(String credBureName3) {
		this.credBureName3 = credBureName3;
	}

	public String getCredBureName4() {
		return credBureName4;
	}

	public void setCredBureName4(String credBureName4) {
		this.credBureName4 = credBureName4;
	}

	public String getCredBureName5() {
		return credBureName5;
	}

	public void setCredBureName5(String credBureName5) {
		this.credBureName5 = credBureName5;
	}

	public int getCibilFirmMsmeRank() {
		return cibilFirmMsmeRank;
	}

	public void setCibilFirmMsmeRank(int cibilFirmMsmeRank) {
		this.cibilFirmMsmeRank = cibilFirmMsmeRank;
	}

	public int getExpCommerScor() {
		return expCommerScor;
	}

	public void setExpCommerScor(int expCommerScor) {
		this.expCommerScor = expCommerScor;
	}

	public float getPromBorrNetWorth() {
		return promBorrNetWorth;
	}

	public void setPromBorrNetWorth(float promBorrNetWorth) {
		this.promBorrNetWorth = promBorrNetWorth;
	}

	public int getPromContribution() {
		return promContribution;
	}

	public void setPromContribution(int promContribution) {
		this.promContribution = promContribution;
	}

	public String getPromGAssoNPA1YrFlg() {
		return promGAssoNPA1YrFlg;
	}

	public void setPromGAssoNPA1YrFlg(String promGAssoNPA1YrFlg) {
		this.promGAssoNPA1YrFlg = promGAssoNPA1YrFlg;
	}

	public int getPromBussExpYr() {
		return promBussExpYr;
	}

	public void setPromBussExpYr(int promBussExpYr) {
		this.promBussExpYr = promBussExpYr;
	}

	public float getSalesRevenue() {
		return salesRevenue;
	}

	public void setSalesRevenue(float salesRevenue) {
		this.salesRevenue = salesRevenue;
	}

	public float getTaxPBIT() {
		return taxPBIT;
	}

	public void setTaxPBIT(float taxPBIT) {
		this.taxPBIT = taxPBIT;
	}

	public float getInterestPayment() {
		return interestPayment;
	}

	public void setInterestPayment(float interestPayment) {
		this.interestPayment = interestPayment;
	}

	public float getTaxCurrentProvisionAmt() {
		return taxCurrentProvisionAmt;
	}

	public void setTaxCurrentProvisionAmt(float taxCurrentProvisionAmt) {
		this.taxCurrentProvisionAmt = taxCurrentProvisionAmt;
	}

	public float getTotCurrentAssets() {
		return totCurrentAssets;
	}

	public void setTotCurrentAssets(float totCurrentAssets) {
		this.totCurrentAssets = totCurrentAssets;
	}

	public float getTotCurrentLiability() {
		return totCurrentLiability;
	}

	public void setTotCurrentLiability(float totCurrentLiability) {
		this.totCurrentLiability = totCurrentLiability;
	}

	public float getTotTermLiability() {
		return totTermLiability;
	}

	public void setTotTermLiability(float totTermLiability) {
		this.totTermLiability = totTermLiability;
	}

	public float getExuityCapital() {
		return exuityCapital;
	}

	public void setExuityCapital(float exuityCapital) {
		this.exuityCapital = exuityCapital;
	}

	public float getPreferenceCapital() {
		return preferenceCapital;
	}

	public void setPreferenceCapital(float preferenceCapital) {
		this.preferenceCapital = preferenceCapital;
	}

	public float getReservesSurplus() {
		return reservesSurplus;
	}

	public void setReservesSurplus(float reservesSurplus) {
		this.reservesSurplus = reservesSurplus;
	}

	public float getRepaymentDueNyrAmt() {
		return repaymentDueNyrAmt;
	}

	public void setRepaymentDueNyrAmt(float repaymentDueNyrAmt) {
		this.repaymentDueNyrAmt = repaymentDueNyrAmt;
	}

	public String getExistGreenFldUnitType() {
		return existGreenFldUnitType;
	}

	public void setExistGreenFldUnitType(String existGreenFldUnitType) {
		this.existGreenFldUnitType = existGreenFldUnitType;
	}

	public float getOpratIncome() {
		return opratIncome;
	}

	public void setOpratIncome(float opratIncome) {
		this.opratIncome = opratIncome;
	}

	public float getProfAftTax() {
		return profAftTax;
	}

	public void setProfAftTax(float profAftTax) {
		this.profAftTax = profAftTax;
	}

	public float getNetworth() {
		return networth;
	}

	public void setNetworth(float networth) {
		this.networth = networth;
	}

	public int getDebitEqtRatioUnt() {
		return debitEqtRatioUnt;
	}

	public void setDebitEqtRatioUnt(int debitEqtRatioUnt) {
		this.debitEqtRatioUnt = debitEqtRatioUnt;
	}

	public int getDebitSrvCoverageRatioTl() {
		return debitSrvCoverageRatioTl;
	}

	public void setDebitSrvCoverageRatioTl(int debitSrvCoverageRatioTl) {
		this.debitSrvCoverageRatioTl = debitSrvCoverageRatioTl;
	}

	public int getCurrentRatioWc() {
		return currentRatioWc;
	}

	public void setCurrentRatioWc(int currentRatioWc) {
		this.currentRatioWc = currentRatioWc;
	}

	public int getDebitEqtRatio() {
		return debitEqtRatio;
	}

	public void setDebitEqtRatio(int debitEqtRatio) {
		this.debitEqtRatio = debitEqtRatio;
	}

	public int getDebitSrvCoverageRatio() {
		return debitSrvCoverageRatio;
	}

	public void setDebitSrvCoverageRatio(int debitSrvCoverageRatio) {
		this.debitSrvCoverageRatio = debitSrvCoverageRatio;
	}

	public int getCurrentRatios() {
		return currentRatios;
	}

	public void setCurrentRatios(int currentRatios) {
		this.currentRatios = currentRatios;
	}

	public int getCreditBureauChiefPromScor() {
		return creditBureauChiefPromScor;
	}

	public void setCreditBureauChiefPromScor(int creditBureauChiefPromScor) {
		this.creditBureauChiefPromScor = creditBureauChiefPromScor;
	}

	public float getTotalAssets() {
		return totalAssets;
	}

	public void setTotalAssets(float totalAssets) {
		this.totalAssets = totalAssets;
	}

	public Double getExistExpoCgt() {
		return existExpoCgt;
	}

	public void setExistExpoCgt(Double existExpoCgt) {
		this.existExpoCgt = existExpoCgt;
	}

	public Double getUnseqLoanportion() {
		return unseqLoanportion;
	}

	public void setUnseqLoanportion(Double unseqLoanportion) {
		this.unseqLoanportion = unseqLoanportion;
	}

	public Double getUnLoanPortionExcludCgtCovered() {
		return unLoanPortionExcludCgtCovered;
	}

	public void setUnLoanPortionExcludCgtCovered(
			Double unLoanPortionExcludCgtCovered) {
		this.unLoanPortionExcludCgtCovered = unLoanPortionExcludCgtCovered;
	}

	public void setVerified(boolean isVerified) {
		this.isVerified = isVerified;
	}

	private String gstState;
    private String stateCode;
//  private String gst;
/** private String exposureFbId;
    private String exposureFbIdY;
    private String exposureFbIdN;  */
  	private String hybridSecurity="";
  	private Double movCollateratlSecurityAmt = 0.0d;
  	private Double immovCollateratlSecurityAmt = 0.0d;
  	private Double totalMIcollatSecAmt = 0.0d;
    private Long proMobileNo=0l;
    private String promDirDefaltFlg="";       
      private int credBureKeyPromScor=0;       
      private int credBurePromScor2=0;         
      private int credBurePromScor3=0;         
      private int credBurePromScor4=0;         
      private int credBurePromScor5=0;         
      private String credBureName1="";                 
      private String credBureName2="";          	   
      private String credBureName3="";       		   
      private String credBureName4="";        		   
      private String credBureName5="";       		   
      private int cibilFirmMsmeRank=0;        
      private int expCommerScor=0;             
      private float promBorrNetWorth=0.0f;        
      private int promContribution=0;          
      private String promGAssoNPA1YrFlg;     
      private int promBussExpYr=0;             
      private float salesRevenue=0.0f;             
      private float taxPBIT=0.0f;                  
      private float interestPayment=0.0f;          
      private float taxCurrentProvisionAmt=0.0f;   
      private float totCurrentAssets=0.0f;         
      private float totCurrentLiability=0.0f;      
      private float totTermLiability=0.0f;         
      private float exuityCapital=0.0f;            
      private float preferenceCapital=0.0f;        
      private float reservesSurplus=0.0f;          
      private float repaymentDueNyrAmt=0.0f;       
      private String existGreenFldUnitType="";               				
      private float opratIncome=0.0f;               			
      private float profAftTax=0.0f;                           
      private float networth=0.0f;                			
      private int debitEqtRatioUnt=0;
      private int debitSrvCoverageRatioTl=0;	
      private int currentRatioWc=0;	
      private int debitEqtRatio=0;		
      private int debitSrvCoverageRatio=0;			
      private int currentRatios=0;	   					
      private int creditBureauChiefPromScor=0;			
      private float totalAssets=0.0f;	
      private Double existExpoCgt=0.0d;
      private Double unseqLoanportion=0.0d;	
      private Double unLoanPortionExcludCgtCovered=0.0d;	
    
    public void setHandiCraftsStatus(String handiCraftsStatus) {
        this.handiCraftsStatus = handiCraftsStatus;
    }

    public String getHandiCraftsStatus() {
        return handiCraftsStatus;
    }

    public void setDcHandicraftsStatus(String dcHandicraftsStatus) {
        this.dcHandicraftsStatus = dcHandicraftsStatus;
    }

    public String getDcHandicraftsStatus() {
        return dcHandicraftsStatus;
    }

    public void setDcHandlooms(String dcHandlooms) {
        this.dcHandlooms = dcHandlooms;
    }

    public String getDcHandlooms() {
        return dcHandlooms;
    }

    public void setDcHandloomsStatus(String dcHandloomsStatus) {
        this.dcHandloomsStatus = dcHandloomsStatus;
    }

    public String getDcHandloomsStatus() {
        return dcHandloomsStatus;
    }

    public void setWeaverCreditScheme(String weaverCreditScheme) {
        this.WeaverCreditScheme = weaverCreditScheme;
    }

    public String getWeaverCreditScheme() {
        return WeaverCreditScheme;
    }

    public void setHandloomchk(String handloomchk) {
        this.handloomchk = handloomchk;
    }

    public String getHandloomchk() {
        return handloomchk;
    }

    public void setMsE(String msE) {
        this.msE = msE;
    }

    public String getMsE() {
        return msE;
    }

    public void setAppmsE(String appmsE) {
        this.AppmsE = appmsE;
    }

    public String getAppmsE() {
        return AppmsE;
    }
    
    /* Braj begin  */
	private Double TRM_INTEREST_RATE;
    private Double TRM_PLR;
	private Double WCP_PLR;
    private Double WCP_INTEREST;
    private Double INTEREST_RATE;
	private Double PLR;
    
    public Double getINTEREST_RATE() {
		return INTEREST_RATE;
	}

	public void setINTEREST_RATE(Double iNTEREST_RATE) {
		INTEREST_RATE = iNTEREST_RATE;
	}

	public Double getPLR() {
		return PLR;
	}

	public void setPLR(Double pLR)
	{
		PLR = pLR;
	}


  
    public Double getWCP_INTEREST() {
		return WCP_INTEREST;
	}

	public void setWCP_INTEREST(Double wCP_INTEREST) {
		WCP_INTEREST = wCP_INTEREST;
	}

	public Double getWCP_PLR() {
		return WCP_PLR;
	}

	public void setWCP_PLR(Double wCP_PLR) {
		WCP_PLR = wCP_PLR;
	}
	
    public Double getTRM_INTEREST_RATE() {
		return TRM_INTEREST_RATE;
	}

	public void setTRM_INTEREST_RATE(Double tRM_INTEREST_RATE) {
		TRM_INTEREST_RATE = tRM_INTEREST_RATE;
	}

	public Double getTRM_PLR() {
		return TRM_PLR;
	}

	public void setTRM_PLR(Double tRM_PLR) {
		TRM_PLR = tRM_PLR;
	}
	//Diksha 03/11/2017
	private String memBankName;
	private String ssiUnitName;
	private String ssiConstitution;
	private String mliId;
	private String gurAmt;
	private String appStatus;
	private String npaDate;
	//Diksha
	public String getSsiConstitution() {
		return ssiConstitution;
	}

	public void setSsiConstitution(String ssiConstitution) {
		this.ssiConstitution = ssiConstitution;
	}
	public String getMemBankName() {
		return memBankName;
	}
	public void setMemBankName(String memBankName) {
		this.memBankName = memBankName;
	}
	public String getSsiUnitName() {
		return ssiUnitName;
	}
	public void setSsiUnitName(String ssiUnitName) {
		this.ssiUnitName = ssiUnitName;
	}
	public String getMliId() {
		return mliId;
	}
	public void setMliId(String mliId) {
		this.mliId = mliId;
	}
	public String getGurAmt() {
		return gurAmt;
	}
	public void setGurAmt(String gurAmt) {
		this.gurAmt = gurAmt;
	}
	public String getAppStatus() {
		return appStatus;
	}
	public void setAppStatus(String appStatus) {
		this.appStatus = appStatus;
	}
	public String getNpaDate() {
		return npaDate;
	}
	public void setNpaDate(String npaDate) {
		this.npaDate = npaDate;
	}
	//Diksha

	@Override
	public String toString() {
		return "Application [mliID=" + mliID + ", mliBranchName="
				+ mliBranchName + ", mliBranchCode=" + mliBranchCode
				+ ", mliRefNo=" + mliRefNo + ", borrowerDetails="
				+ borrowerDetails + ", projectOutlayDetails="
				+ projectOutlayDetails + ", rehabilitation=" + rehabilitation
				+ ", compositeLoan=" + compositeLoan + ", loanType=" + loanType
				+ ", applicationType=" + applicationType + ", scheme=" + scheme
				+ ", termLoan=" + termLoan + ", wc=" + wc + ", approvedAmount="
				+ approvedAmount + ", sanctionedAmount=" + sanctionedAmount
				+ ", reapprovedAmount=" + reapprovedAmount
				+ ", enhancementAmount=" + enhancementAmount + ", docRefNo="
				+ docRefNo + ", reapprovalRemarks=" + reapprovalRemarks
				+ ", cgpan=" + cgpan + ", cgpanReference=" + cgpanReference
				+ ", userId=" + userId + ", bankId=" + bankId + ", zoneId="
				+ zoneId + ", branchId=" + branchId + ", appRefNo=" + appRefNo
				+ ", itpan=" + itpan + ", wcAppRefNo=" + wcAppRefNo
				+ ", regionId=" + regionId + ", NPA=" + NPA + ", ssiRef="
				+ ssiRef + ", collateralSecDtls=" + collateralSecDtls
				+ ", outstandingAmount=" + outstandingAmount + ", ITPAN="
				+ ITPAN + ", subsidyProvided=" + subsidyProvided
				+ ", submittedDate=" + submittedDate + ", sanctionedDate="
				+ sanctionedDate + ", activity=" + activity + ", approvedDate="
				+ approvedDate + ", guaranteeStartDate=" + guaranteeStartDate
				+ ", appExpiryDate=" + appExpiryDate + ", remarks=" + remarks
				+ ", prevSSI=" + prevSSI + ", existSSI=" + existSSI
				+ ", status=" + status + ", projectType=" + projectType
				+ ", theRepaymentDetail=" + theRepaymentDetail
				+ ", securitization=" + securitization + ", mcgfDetails="
				+ mcgfDetails + ", guaranteeFee=" + guaranteeFee
				+ ", subSchemeName=" + subSchemeName + ", existingRemarks="
				+ existingRemarks + ", additionalTC=" + additionalTC
				+ ", wcEnhancement=" + wcEnhancement + ", wcRenewal="
				+ wcRenewal + ", isVerified=" + isVerified + ", zoneName="
				+ zoneName + ", coFinanceTaken1=" + coFinanceTaken1
				+ ", district=" + district + ", state=" + state + ", sex="
				+ sex + ", socialCategory=" + socialCategory
				+ ", internalRate=" + internalRate + ", externalRate="
				+ externalRate + ", handiCrafts=" + handiCrafts
				+ ", dcHandicrafts=" + dcHandicrafts + ", icardNo=" + icardNo
				+ ", icardIssueDate=" + icardIssueDate + ", jointFinance="
				+ jointFinance + ", jointcgpan=" + jointcgpan
				+ ", activityConfirm=" + activityConfirm
				+ ", handiCraftsStatus=" + handiCraftsStatus
				+ ", dcHandicraftsStatus=" + dcHandicraftsStatus
				+ ", dcHandlooms=" + dcHandlooms + ", dcHandloomsStatus="
				+ dcHandloomsStatus + ", WeaverCreditScheme="
				+ WeaverCreditScheme + ", handloomchk=" + handloomchk
				+ ", msE=" + msE + ", AppmsE=" + AppmsE + ", pSecurity="
				+ pSecurity + ", gstNo=" + gstNo + ", gstState=" + gstState
				+ ", stateCode=" + stateCode + ", hybridSecurity="
				+ hybridSecurity + ", movCollateratlSecurityAmt="
				+ movCollateratlSecurityAmt + ", immovCollateratlSecurityAmt="
				+ immovCollateratlSecurityAmt + ", totalMIcollatSecAmt="
				+ totalMIcollatSecAmt + ", proMobileNo=" + proMobileNo
				+ ", promDirDefaltFlg=" + promDirDefaltFlg
				+ ", credBureKeyPromScor=" + credBureKeyPromScor
				+ ", credBurePromScor2=" + credBurePromScor2
				+ ", credBurePromScor3=" + credBurePromScor3
				+ ", credBurePromScor4=" + credBurePromScor4
				+ ", credBurePromScor5=" + credBurePromScor5
				+ ", credBureName1=" + credBureName1 + ", credBureName2="
				+ credBureName2 + ", credBureName3=" + credBureName3
				+ ", credBureName4=" + credBureName4 + ", credBureName5="
				+ credBureName5 + ", cibilFirmMsmeRank=" + cibilFirmMsmeRank
				+ ", expCommerScor=" + expCommerScor + ", promBorrNetWorth="
				+ promBorrNetWorth + ", promContribution=" + promContribution
				+ ", promGAssoNPA1YrFlg=" + promGAssoNPA1YrFlg
				+ ", promBussExpYr=" + promBussExpYr + ", salesRevenue="
				+ salesRevenue + ", taxPBIT=" + taxPBIT + ", interestPayment="
				+ interestPayment + ", taxCurrentProvisionAmt="
				+ taxCurrentProvisionAmt + ", totCurrentAssets="
				+ totCurrentAssets + ", totCurrentLiability="
				+ totCurrentLiability + ", totTermLiability="
				+ totTermLiability + ", exuityCapital=" + exuityCapital
				+ ", preferenceCapital=" + preferenceCapital
				+ ", reservesSurplus=" + reservesSurplus
				+ ", repaymentDueNyrAmt=" + repaymentDueNyrAmt
				+ ", existGreenFldUnitType=" + existGreenFldUnitType
				+ ", opratIncome=" + opratIncome + ", profAftTax=" + profAftTax
				+ ", networth=" + networth + ", debitEqtRatioUnt="
				+ debitEqtRatioUnt + ", debitSrvCoverageRatioTl="
				+ debitSrvCoverageRatioTl + ", currentRatioWc="
				+ currentRatioWc + ", debitEqtRatio=" + debitEqtRatio
				+ ", debitSrvCoverageRatio=" + debitSrvCoverageRatio
				+ ", currentRatios=" + currentRatios
				+ ", creditBureauChiefPromScor=" + creditBureauChiefPromScor
				+ ", totalAssets=" + totalAssets + ", existExpoCgt="
				+ existExpoCgt + ", unseqLoanportion=" + unseqLoanportion
				+ ", unLoanPortionExcludCgtCovered="
				+ unLoanPortionExcludCgtCovered + ", TRM_INTEREST_RATE="
				+ TRM_INTEREST_RATE + ", TRM_PLR=" + TRM_PLR + ", WCP_PLR="
				+ WCP_PLR + ", WCP_INTEREST=" + WCP_INTEREST
				+ ", INTEREST_RATE=" + INTEREST_RATE + ", PLR=" + PLR
				+ ", memBankName=" + memBankName + ", ssiUnitName="
				+ ssiUnitName + ", ssiConstitution=" + ssiConstitution
				+ ", mliId=" + mliId + ", gurAmt=" + gurAmt + ", appStatus="
				+ appStatus + ", npaDate=" + npaDate + "]";
	}
	
	/* Bpraj  */
    
}
