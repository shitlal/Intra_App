//Source file: F:\\CGTSI\\Design\\DesignModel\\com\\cgtsi\\claim\\ClaimApplication.java

package com.cgtsi.claim;

import java.util.Vector;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * This is a Value Object holds the data related to Claim Application.
 */
public class ClaimApplication implements java.io.Serializable
{
   private String memberId;
   private String borrowerId;
   private MemberInfo memberDetails;
   private BorrowerInfo borrowerDetails;
   private Date dateOnWhichAccountClassifiedNPA;
   private Date dateOfReportingNpaToCgtsi;
   private String reasonsForAccountTurningNPA;
   private Date dateOfIssueOfRecallNotice;
   private LegalProceedingsDetail legalProceedingsDetails;
   private Date dateOfReleaseOfWC;
   private SecurityAndPersonalGuaranteeDtls securityAndPersonalGuaranteeDtls;
   private Date dateOfSeekingOTS;
   private Vector recoveryDetails;
   private String nameOfOfficial;
   private String designationOfOfficial;
   private Date claimSubmittedDate;
   private String place;
   private ArrayList claimSummaryDtls;
   // The ArrayList will contain objects of WorkingCapitalDetail
   private ArrayList workingCapitalDtls;
   // The Vector will contain objects of TermLoanCapitalLoanDetail
   private Vector termCapitalDtls;
   private String participatingBank;
   private String claimRefNumber;
   private String whetherBorrowerIsWilfulDefaulter;
   private String whetherAccntWrittenOffFromBooksOfMLI;
   private Date dtOnWhichAccntWrittenOff;
   private Date dtOfConclusionOfRecoveryProc;

   // Properties added for Claim Reports in Reports Module
   private Date claimApprovedDt;
   private String cgclan;

   //field added for second installment
   private String clpan;

   private boolean firstInstallment;
   private boolean secondInstallment;

   private boolean isVerified;

   private String dateOnWhichAccountClassifiedNPAStr;
   private String dateOfReportingNpaToCgtsiStr;
   private String dateOfIssueOfRecallNoticeStr;
   private String dateOfReleaseOfWCStr;
   private String dateOfSeekingOTSStr;
   private String claimSubmittedDateStr;
  
   private String claimApprovedDtStr;
   private String dtOnWhichAccntWrittenOffStr;

   private Date claimSettlementDate;		//added by nithya for second installment 07/08/2004


   private SimpleDateFormat sdf = null;

   private String createdModifiedy;

/*
	The following four field are added on 11/10/2004 by
	Veldurai to enable viewing uploaded attachments.
*/
   private String legalDetailsFileName=null;

   private byte[] legalDetailsFileData=null;

   private String recalNoticeFileName=null;

   private byte[] recallNoticeFileData=null;

  // added subisidy/ credit date and Amount,IFS code,NEFT code,Bank a/c name,Bank a/c number and Branch Name,unitName by sukumar@path on 13-08-2009
    private java.util.Date subsidyDate;
    private double subsidyAmt;    
    private String ifsCode;
    private String neftCode;
    private String rtgsBankName;
    private String rtgsBankNumber;
    private String rtgsBranchName;
    private String unitName;
    private String microCategory;
    private String claimProceedings;
    
    private String claimCheckerDoneDatestr;
    
    
    
    public String getClaimCheckerDoneDatestr() {
		return claimCheckerDoneDatestr;
	}
	public void setClaimCheckerDoneDatestr(String claimCheckerDoneDatestr) {
		this.claimCheckerDoneDatestr = claimCheckerDoneDatestr;
	}
	public String getClaimCheckerDoneDate() {
		return claimCheckerDoneDate;
	}
	public void setClaimCheckerDoneDate(String claimCheckerDoneDate) {
		this.claimCheckerDoneDate = claimCheckerDoneDate;
	}
	public String getClaimUpdationDateStr() {
		return claimUpdationDateStr;
	}
	public void setClaimUpdationDateStr(String claimUpdationDateStr) {
		this.claimUpdationDateStr = claimUpdationDateStr;
	}


	private String claimCheckerDoneDate;
	
	public Date getClaimUpdationDate() {
		return claimUpdationDate;
	}
	public void setClaimUpdationDate(Date claimUpdationDate) {
		this.claimUpdationDate = claimUpdationDate;
	}
	public Date getClaimCheckerDate() {
		return claimCheckerDate;
	}
	public void setClaimCheckerDate(Date claimCheckerDate) {
		this.claimCheckerDate = claimCheckerDate;
	}


	private Date  claimUpdationDate;
	
	private Date  claimCheckerDate;
	
	
	
    private String claimUpdationDateStr;
    
   


	
    
   
	
   public ClaimApplication()
   {

   }
   /**
   * 
   * @return claimProceedings
   */
public String getClaimProceedings()
{
 return this.claimProceedings;
}
/**
   * 
   * @param claimProceedings
   */
public void setClaimProceedings(String claimProceedings)
{
 this.claimProceedings = claimProceedings;
}
/**
   * 
   * @return microCategory
   */
 public String getMicroCategory()
 {
  return this.microCategory;
 }
 /**
   * 
   * @param microCategory
   */
 public void setMicroCategory(String microCategory)
 {
  this.microCategory = microCategory;
 }
   
   /**
   * 
   * @return unitName
   */
   public String getUnitName()
   {
    return this.unitName;
   }
   /**
   * 
   * @param unitName
   */
   public void setUnitName(String unitName)
    {
     this.unitName = unitName;
    }
    /**
   * 
   * @return SubsidyDate
   */
  public java.util.Date getSubsidyDate()
	{
		return this.subsidyDate;
	}
  /**
   * 
   * @param date
   */
	public void setSubsidyDate(java.util.Date date)
	{
		this.subsidyDate = date;
	}

  /**
   * 
   * @return subsidyAmt
   */
  public double getSubsidyAmt()
  {
   return subsidyAmt;
  }
  /**
   * 
   * @param subsidyAmt
   */
  public void setSubsidyAmt(double subsidyAmt)
  {
    this.subsidyAmt = subsidyAmt;
  }
  /**
   * 
   * @return ifsCode
   */
  public String getIfsCode()
  {
    return ifsCode;
  }
  /**
   * 
   * @param ifsCode
   */
  public void setIfsCode(String ifsCode)
  {
   this.ifsCode = ifsCode;
  }
  /**
   * 
   * @return neftCode
   */
  public String getNeftCode()
  {
   return neftCode;
  }
  /**
   * 
   * @param neftCode
   */
  public void setNeftCode(String neftCode)
  {
   this.neftCode = neftCode;
  }
  /**
   * 
   * @return rtgsBankName
   */
  public String getRtgsBankName()
  {
   return rtgsBankName;
  }
  /**
   * 
   * @param rtgsBankName
   */
  public void setRtgsBankName(String rtgsBankName)
  {
   this.rtgsBankName = rtgsBankName;
  }
  /**
   * 
   * @return rtgsBankNumber
   */
  public String getRtgsBankNumber()
  {
   return rtgsBankNumber;
  }
  
  /**
   * @param rtgsBankNumber
   */
  public void setRtgsBankNumber(String rtgsBankNumber)
  {
   this.rtgsBankNumber = rtgsBankNumber;
  }
  
  /**
   * 
   * @return rtgsBranchName
   */
  public String getRtgsBranchName()
  {
   return rtgsBranchName;
  }
  
  public void setRtgsBranchName(String rtgsBranchName)
  {
   this.rtgsBranchName = rtgsBranchName;
  }
   

   /**
	* Access method for the memberId property.
	*
	* @return   the current value of the memberId property
	*/
   public String getMemberId()
   {
	  return memberId;
   }

   /**
	* Sets the value of the memberId property.
	*
	* @param aMemberId the new value of the memberId property
	*/
   public void setMemberId(String aMemberId)
   {
	  memberId = aMemberId;
   }

   /**
	* Access method for the borrowerId property.
	*
	* @return   the current value of the borrowerId property
	*/
   public String getBorrowerId()
   {
	  return borrowerId;
   }

   /**
	* Sets the value of the borrowerId property.
	*
	* @param aBorrowerId the new value of the borrowerId property
	*/
   public void setBorrowerId(String aBorrowerId)
   {
	  borrowerId = aBorrowerId;
   }

   /**
	* Access method for the memberDetails property.
	*
	* @return   the current value of the memberDetails property
	*/
   public MemberInfo getMemberDetails()
   {
	  return memberDetails;
   }

   /**
	* Sets the value of the memberDetails property.
	*
	* @param aMemberDetails the new value of the memberDetails property
	*/
   public void setMemberDetails(MemberInfo aMemberDetails)
   {
	  memberDetails = aMemberDetails;
   }

   /**
	* Access method for the borrowerDetails property.
	*
	* @return   the current value of the borrowerDetails property
	*/
   public BorrowerInfo getBorrowerDetails()
   {
	  return borrowerDetails;
   }

   /**
	* Sets the value of the borrowerDetails property.
	*
	* @param aBorrowerDetails the new value of the borrowerDetails property
	*/
   public void setBorrowerDetails(BorrowerInfo aBorrowerDetails)
   {
	  borrowerDetails = aBorrowerDetails;
   }

   /**
	* Access method for the dateOnWhichAccountClassifiedNPA property.
	*
	* @return   the current value of the dateOnWhichAccountClassifiedNPA property
	*/
   public Date getDateOnWhichAccountClassifiedNPA()
   {
	  return dateOnWhichAccountClassifiedNPA;
   }

   /**
	* Sets the value of the dateOnWhichAccountClassifiedNPA property.
	*
	* @param aDateOnWhichAccountClassifiedNPA the new value of the dateOnWhichAccountClassifiedNPA property
	*/
   public void setDateOnWhichAccountClassifiedNPA(Date aDateOnWhichAccountClassifiedNPA)
   {
	  dateOnWhichAccountClassifiedNPA = aDateOnWhichAccountClassifiedNPA;
   }

   /**
	* Access method for the dateOfReportingNpaToCgtsi property.
	*
	* @return   the current value of the dateOfReportingNpaToCgtsi property
	*/
   public Date getDateOfReportingNpaToCgtsi()
   {
	  return dateOfReportingNpaToCgtsi;
   }

   /**
	* Sets the value of the dateOfReportingNpaToCgtsi property.
	*
	* @param aDateOfReportingNpaToCgtsi the new value of the dateOfReportingNpaToCgtsi property
	*/
   public void setDateOfReportingNpaToCgtsi(Date aDateOfReportingNpaToCgtsi)
   {
	  dateOfReportingNpaToCgtsi = aDateOfReportingNpaToCgtsi;
   }

   /**
	* Access method for the reasonsForAccountTurningNPA property.
	*
	* @return   the current value of the reasonsForAccountTurningNPA property
	*/
   public String getReasonsForAccountTurningNPA()
   {
	  return reasonsForAccountTurningNPA;
   }

   /**
	* Sets the value of the reasonsForAccountTurningNPA property.
	*
	* @param aReasonsForAccountTurningNPA the new value of the reasonsForAccountTurningNPA property
	*/
   public void setReasonsForAccountTurningNPA(String aReasonsForAccountTurningNPA)
   {
	  reasonsForAccountTurningNPA = aReasonsForAccountTurningNPA;
   }

   /**
	* Access method for the dateOfIssueOfRecallNotice property.
	*
	* @return   the current value of the dateOfIssueOfRecallNotice property
	*/
   public Date getDateOfIssueOfRecallNotice()
   {
	  return dateOfIssueOfRecallNotice;
   }

   /**
	* Sets the value of the dateOfIssueOfRecallNotice property.
	*
	* @param aDateOfIssueOfRecallNotice the new value of the dateOfIssueOfRecallNotice property
	*/
   public void setDateOfIssueOfRecallNotice(Date aDateOfIssueOfRecallNotice)
   {
	  dateOfIssueOfRecallNotice = aDateOfIssueOfRecallNotice;
   }

   /**
	* Access method for the legalProceedingsDetails property.
	*
	* @return   the current value of the legalProceedingsDetails property
	*/
   public LegalProceedingsDetail getLegalProceedingsDetails()
   {
	  return legalProceedingsDetails;
   }

   /**
	* Sets the value of the legalProceedingsDetails property.
	*
	* @param aLegalProceedingsDetails the new value of the legalProceedingsDetails property
	*/
   public void setLegalProceedingsDetails(LegalProceedingsDetail aLegalProceedingsDetails)
   {
	  legalProceedingsDetails = aLegalProceedingsDetails;
   }

   /**
	* Access method for the dateOfReleaseOfWC property.
	*
	* @return   the current value of the dateOfReleaseOfWC property
	*/
   public Date getDateOfReleaseOfWC()
   {
	  return dateOfReleaseOfWC;
   }

   /**
	* Sets the value of the dateOfReleaseOfWC property.
	*
	* @param aDateOfReleaseOfWC the new value of the dateOfReleaseOfWC property
	*/
   public void setDateOfReleaseOfWC(Date aDateOfReleaseOfWC)
   {
	  dateOfReleaseOfWC = aDateOfReleaseOfWC;
   }

   /**
	* Access method for the securityAndPersonalGuaranteeDtls property.
	*
	* @return   the current value of the securityAndPersonalGuaranteeDtls property
	*/
   public SecurityAndPersonalGuaranteeDtls getSecurityAndPersonalGuaranteeDtls()
   {
	  return securityAndPersonalGuaranteeDtls;
   }

   /**
	* Sets the value of the securityAndPersonalGuaranteeDtls property.
	*
	* @param aSecurityAndPersonalGuaranteeDtls the new value of the securityAndPersonalGuaranteeDtls property
	*/
   public void setSecurityAndPersonalGuaranteeDtls(SecurityAndPersonalGuaranteeDtls aSecurityAndPersonalGuaranteeDtls)
   {
	  securityAndPersonalGuaranteeDtls = aSecurityAndPersonalGuaranteeDtls;
   }

   /**
	* Access method for the dateOfSeekingOTS property.
	*
	* @return   the current value of the dateOfSeekingOTS property
	*/
   public Date getDateOfSeekingOTS()
   {
	  return dateOfSeekingOTS;
   }

   /**
	* Sets the value of the dateOfSeekingOTS property.
	*
	* @param aDateOfSeekingOTS the new value of the dateOfSeekingOTS property
	*/
   public void setDateOfSeekingOTS(Date aDateOfSeekingOTS)
   {
	  dateOfSeekingOTS = aDateOfSeekingOTS;
   }

   /**
	* Access method for the recoveryDetails property.
	*
	* @return   the current value of the recoveryDetails property
	*/
   public Vector getRecoveryDetails()
   {
	  return recoveryDetails;
   }

   /**
	* Sets the value of the recoveryDetails property.
	*
	* @param aRecoveryDetails the new value of the recoveryDetails property
	*/
   public void setRecoveryDetails(Vector aRecoveryDetails)
   {
	  recoveryDetails = aRecoveryDetails;
   }

   /**
	* Access method for the nameOfOfficial property.
	*
	* @return   the current value of the nameOfOfficial property
	*/
   public String getNameOfOfficial()
   {
	  return nameOfOfficial;
   }

   /**
	* Sets the value of the nameOfOfficial property.
	*
	* @param aNameOfOfficial the new value of the nameOfOfficial property
	*/
   public void setNameOfOfficial(String aNameOfOfficial)
   {
	  nameOfOfficial = aNameOfOfficial;
   }

   /**
	* Access method for the designationOfOfficial property.
	*
	* @return   the current value of the designationOfOfficial property
	*/
   public String getDesignationOfOfficial()
   {
	  return designationOfOfficial;
   }

   /**
	* Sets the value of the designationOfOfficial property.
	*
	* @param aDesignationOfOfficial the new value of the designationOfOfficial property
	*/
   public void setDesignationOfOfficial(String aDesignationOfOfficial)
   {
	  designationOfOfficial = aDesignationOfOfficial;
   }

   /**
	* Access method for the claimSubmittedDate property.
	*
	* @return   the current value of the claimSubmittedDate property
	*/
   public Date getClaimSubmittedDate()
   {
	  return claimSubmittedDate;
   }

   /**
	* Sets the value of the claimSubmittedDate property.
	*
	* @param aClaimSubmittedDate the new value of the claimSubmittedDate property
	*/
   public void setClaimSubmittedDate(Date aClaimSubmittedDate)
   {
	  claimSubmittedDate = aClaimSubmittedDate;
   }

   /**
	* Access method for the place property.
	*
	* @return   the current value of the place property
	*/
   public String getPlace()
   {
	  return place;
   }

   /**
	* Sets the value of the place property.
	*
	* @param aPlace the new value of the place property
	*/
   public void setPlace(String aPlace)
   {
	  place = aPlace;
   }

   /**
	* Access method for the claimSummaryDtls property.
	*
	* @return   the current value of the claimSummaryDtls property
	*/
   public ArrayList getClaimSummaryDtls()
   {
	  return claimSummaryDtls;
   }

   /**
	* Sets the value of the claimSummaryDtls property.
	*
	* @param aClaimSummaryDtls the new value of the claimSummaryDtls property
	*/
   public void setClaimSummaryDtls(ArrayList aClaimSummaryDtls)
   {
	  claimSummaryDtls = aClaimSummaryDtls;
   }

   /**
	* Access method for the workingCapitalDtls property.
	*/
   public ArrayList getWorkingCapitalDtls()
   {
	  return workingCapitalDtls;
   }

   /**
	* Sets the value of the workingCapitalDtls property.
	*
	* @param aWorkingCapitalDtls the new value of the workingCapitalDtls property
	*/
   public void setWorkingCapitalDtls(ArrayList aWorkingCapitalDtls)
   {
	  workingCapitalDtls = aWorkingCapitalDtls;
   }

   /**
	* Access method for the termCapitalDtls property.
	*
	* @return   the current value of the termCapitalDtls property
	*/
   public Vector getTermCapitalDtls()
   {
	  return termCapitalDtls;
   }

   /**
	* Sets the value of the termCapitalDtls property.
	*
	* @param aTermCapitalDtls the new value of the termCapitalDtls property
	*/
   public void setTermCapitalDtls(Vector aTermCapitalDtls)
   {
	  termCapitalDtls = aTermCapitalDtls;
   }

   public String getParticipatingBank()
   {
	   return participatingBank;
   }

   public void setParticipatingBank(String aParticipatingBank)
   {
	   participatingBank = aParticipatingBank;
   }

   public String getClaimRefNumber()
   {
	   return claimRefNumber;
   }

   public void setClaimRefNumber(String aClaimRefNumber)
   {
	   claimRefNumber = aClaimRefNumber;
   }

   public String getWhetherBorrowerIsWilfulDefaulter()
   {
	   return this.whetherBorrowerIsWilfulDefaulter;
   }

   public void setWhetherBorrowerIsWilfulDefaulter(String flag)
   {
	   this.whetherBorrowerIsWilfulDefaulter = flag;
   }

   public java.util.Date getDtOnWhichAccntWrittenOff()
   {
	   return this.dtOnWhichAccntWrittenOff;
   }

   public void setDtOnWhichAccntWrittenOff(java.util.Date date)
   {
	   this.dtOnWhichAccntWrittenOff = date;
   }

   /**
	* Access method for the clpan property.
	*
	* @return   the current value of the clpan property
	*/
   public String getClpan()
   {
	  return clpan;
   }

   /**
	* Sets the value of the clpan property.
	*
	* @param aClpan the new value of the clpan property
	*/
   public void setClpan(String aClpan)
   {
	  clpan = aClpan;
   }

   public boolean getFirstInstallment()
   {
	   return firstInstallment;
   }

   public void setFirstInstallment(boolean aFirstInstallment)
   {
	   firstInstallment = aFirstInstallment;
   }

   public boolean getSecondInstallment()
   {
	   return secondInstallment;
   }

   public void setSecondInstallment(boolean aSecondInstallment)
   {
	   secondInstallment = aSecondInstallment;
   }

   public Date getDtOfConclusionOfRecoveryProc()
   {
	   return this.dtOfConclusionOfRecoveryProc;
   }

   public void setDtOfConclusionOfRecoveryProc(Date dt)
   {
	   this.dtOfConclusionOfRecoveryProc = dt;
   }

   public String getWhetherAccntWrittenOffFromBooksOfMLI()
   {
		return this.whetherAccntWrittenOffFromBooksOfMLI;
   }

   public void setWhetherAccntWrittenOffFromBooksOfMLI(String flag)
   {
	   this.whetherAccntWrittenOffFromBooksOfMLI = flag;
   }

   public Date getClaimApprovedDt()
   {
	   return this.claimApprovedDt;
   }

   public void setClaimApprovedDt(java.util.Date dt)
   {
	   this.claimApprovedDt = dt;
   }

   public String getCgclan()
   {
	   return this.cgclan;
   }

   public void setCgclan(String aCgclan)
   {
	   this.cgclan = aCgclan;
   }
/**
 * @return
 */
public boolean getIsVerified() {
	return isVerified;
}

/**
 * @param b
 */
public void setIsVerified(boolean b) {
	isVerified = b;
}

public String getDateOnWhichAccountClassifiedNPAStr()
{
	sdf = new SimpleDateFormat("dd/MM/yyyy");
	if(dateOnWhichAccountClassifiedNPA != null)
	{
		dateOnWhichAccountClassifiedNPAStr = sdf.format(dateOnWhichAccountClassifiedNPA);
	}
	return this.dateOnWhichAccountClassifiedNPAStr;
}

public void setDateOnWhichAccountClassifiedNPAStr(String str)
{
	this.dateOnWhichAccountClassifiedNPAStr = str;
}

public String getDateOfReportingNpaToCgtsiStr()
{
	sdf = new SimpleDateFormat("dd/MM/yyyy");
	if(dateOfReportingNpaToCgtsi != null)
	{
		dateOfReportingNpaToCgtsiStr = sdf.format(dateOfReportingNpaToCgtsi);
	}
	return this.dateOfReportingNpaToCgtsiStr;
}

public void setDateOfReportingNpaToCgtsiStr(String str)
{
	this.dateOfReportingNpaToCgtsiStr = str;
}

public String getDateOfIssueOfRecallNoticeStr()
{
	sdf = new SimpleDateFormat("dd/MM/yyyy");
	if(dateOfIssueOfRecallNotice != null)
	{
		dateOfIssueOfRecallNoticeStr = sdf.format(dateOfIssueOfRecallNotice);
	}
	return this.dateOfIssueOfRecallNoticeStr;
}

public void setDateOfIssueOfRecallNoticeStr(String str)
{
	this.dateOfIssueOfRecallNoticeStr = str;
}

public String getDateOfReleaseOfWCStr()
{
	sdf = new SimpleDateFormat("dd/MM/yyyy");
	if(dateOfReleaseOfWC != null)
	{
		dateOfReleaseOfWCStr = sdf.format(dateOfReleaseOfWC);
	}
	return this.dateOfReleaseOfWCStr;
}

public void setDateOfReleaseOfWC(String str)
{
	this.dateOfReleaseOfWCStr = str;
}

public String getDateOfSeekingOTSStr()
{
	sdf = new SimpleDateFormat("dd/MM/yyyy");
	if(dateOfSeekingOTS != null)
	{
		dateOfSeekingOTSStr = sdf.format(dateOfSeekingOTS);
	}
	return this.dateOfSeekingOTSStr;
}

public void setDateOfSeekingOTS(String str)
{
	this.dateOfSeekingOTSStr = str;
}

public String getClaimSubmittedDateStr()
{
	sdf = new SimpleDateFormat("dd/MM/yyyy");
	if(claimSubmittedDate != null)
	{
		claimSubmittedDateStr = sdf.format(claimSubmittedDate);
	}
	return this.claimSubmittedDateStr;
}

public void setClaimSubmittedDateStr(String str)
{
	this.claimSubmittedDateStr = str;
}

public String getClaimApprovedDtStr()
{
	sdf = new SimpleDateFormat("dd/MM/yyyy");
	if(claimApprovedDt != null)
	{
		claimApprovedDtStr = sdf.format(claimApprovedDt);
	}
	return this.claimApprovedDtStr;
}

public void setClaimApprovedDtStr(String str)
{
	this.claimApprovedDtStr = str;
}

public String getDtOnWhichAccntWrittenOffStr()
{
	sdf = new SimpleDateFormat("dd/MM/yyyy");
	if(dtOnWhichAccntWrittenOff != null)
	{
		dtOnWhichAccntWrittenOffStr = sdf.format(dtOnWhichAccntWrittenOff);
	}
	return this.dtOnWhichAccntWrittenOffStr;
}

public void setDtOnWhichAccntWrittenOffStr(String str)
{
	this.dtOnWhichAccntWrittenOffStr = str;
}

/**
 * @return
 */
public Date getClaimSettlementDate() {
	return claimSettlementDate;
}

/**
 * @param date
 */
public void setClaimSettlementDate(Date date) {
	claimSettlementDate = date;
}

/**
 * @return
 */
public byte[] getLegalDetailsFileData() {
	return legalDetailsFileData;
}

/**
 * @return
 */
public String getLegalDetailsFileName() {
	return legalDetailsFileName;
}

/**
 * @return
 */
public byte[] getRecallNoticeFileData() {
	return recallNoticeFileData;
}

/**
 * @return
 */
public String getRecalNoticeFileName() {
	return recalNoticeFileName;
}

/**
 * @param bs
 */
public void setLegalDetailsFileData(byte[] bs) {
	legalDetailsFileData = bs;
}

/**
 * @param string
 */
public void setLegalDetailsFileName(String string) {
	legalDetailsFileName = string;
}

/**
 * @param bs
 */
public void setRecallNoticeFileData(byte[] bs) {
	recallNoticeFileData = bs;
}

/**
 * @param string
 */
public void setRecalNoticeFileName(String string) {
	recalNoticeFileName = string;
}

public String getCreatedModifiedy()
{
	return this.createdModifiedy;
}

public void setCreatedModifiedy(String id)
{
	this.createdModifiedy = id;
}


//added by upchar@path on 07-11-2013 for new fields of claim form
         private String unitAssistedMSE;
         private String wilful;
         private String fraudFlag;
         private String enquiryFlag;
         private String mliInvolvementFlag;
         private String reasonForRecall;
         private String reasonForFilingSuit;
         private Date assetPossessionDt;
         private String inclusionOfReceipt;
         private String confirmRecoveryFlag;
         private String subsidyFlag;
         private String isSubsidyAdjustedOnDues;
         private String isSubsidyRcvdAfterNpa;
         private String mliCommentOnFinPosition;
         private String detailsOfFinAssistance;
         private String creditSupport;
         private String bankFacilityDetail;
         private String placeUnderWatchList;
         private String remarksOnNpa;
         private String returnRemarks;


        public void setUnitAssistedMSE(String unitAssistedMSE) {
            this.unitAssistedMSE = unitAssistedMSE;
        }

        public String getUnitAssistedMSE() {
            return unitAssistedMSE;
        }

        public void setWilful(String wilful) {
            this.wilful = wilful;
        }

        public String getWilful() {
            return wilful;
        }

        public void setFraudFlag(String fraudFlag) {
            this.fraudFlag = fraudFlag;
        }

        public String getFraudFlag() {
            return fraudFlag;
        }

        public void setEnquiryFlag(String enquiryFlag) {
            this.enquiryFlag = enquiryFlag;
        }

        public String getEnquiryFlag() {
            return enquiryFlag;
        }

        public void setMliInvolvementFlag(String mliInvolvementFlag) {
            this.mliInvolvementFlag = mliInvolvementFlag;
        }

        public String getMliInvolvementFlag() {
            return mliInvolvementFlag;
        }

        public void setReasonForRecall(String reasonForRecall) {
            this.reasonForRecall = reasonForRecall;
        }

        public String getReasonForRecall() {
            return reasonForRecall;
        }

        public void setReasonForFilingSuit(String reasonForFilingSuit) {
            this.reasonForFilingSuit = reasonForFilingSuit;
        }

        public String getReasonForFilingSuit() {
            return reasonForFilingSuit;
        }

        public void setAssetPossessionDt(Date assetPossessionDt) {
            this.assetPossessionDt = assetPossessionDt;
        }

        public Date getAssetPossessionDt() {
            return assetPossessionDt;
        }

        public void setInclusionOfReceipt(String inclusionOfReceipt) {
            this.inclusionOfReceipt = inclusionOfReceipt;
        }

        public String getInclusionOfReceipt() {
            return inclusionOfReceipt;
        }

        public void setSubsidyFlag(String subsidyFlag) {
            this.subsidyFlag = subsidyFlag;
        }

        public String getSubsidyFlag() {
            return subsidyFlag;
        }

        public void setCreditSupport(String creditSupport) {
            this.creditSupport = creditSupport;
        }

        public String getCreditSupport() {
            return creditSupport;
        }

        public void setConfirmRecoveryFlag(String confirmRecoveryFlag) {
            this.confirmRecoveryFlag = confirmRecoveryFlag;
        }

        public String getConfirmRecoveryFlag() {
            return confirmRecoveryFlag;
        }

        public void setIsSubsidyAdjustedOnDues(String isSubsidyAdjustedOnDues) {
            this.isSubsidyAdjustedOnDues = isSubsidyAdjustedOnDues;
        }
    
        public String getIsSubsidyAdjustedOnDues() {
            return isSubsidyAdjustedOnDues;
        }
    
        public void setIsSubsidyRcvdAfterNpa(String isSubsidyRcvdAfterNpa) {
            this.isSubsidyRcvdAfterNpa = isSubsidyRcvdAfterNpa;
        }
    
        public String getIsSubsidyRcvdAfterNpa() {
            return isSubsidyRcvdAfterNpa;
        }
    
        public void setMliCommentOnFinPosition(String mliCommentOnFinPosition) {
            this.mliCommentOnFinPosition = mliCommentOnFinPosition;
        }
    
        public String getMliCommentOnFinPosition() {
            return mliCommentOnFinPosition;
        }
    
        public void setDetailsOfFinAssistance(String detailsOfFinAssistance) {
            this.detailsOfFinAssistance = detailsOfFinAssistance;
        }
    
        public String getDetailsOfFinAssistance() {
            return detailsOfFinAssistance;
        }
    
        public void setBankFacilityDetail(String bankFacilityDetail) {
            this.bankFacilityDetail = bankFacilityDetail;
        }
    
        public String getBankFacilityDetail() {
            return bankFacilityDetail;
        }
    
        public void setPlaceUnderWatchList(String placeUnderWatchList) {
            this.placeUnderWatchList = placeUnderWatchList;
        }
    
        public String getPlaceUnderWatchList() {
            return placeUnderWatchList;
        }
    
        public void setRemarksOnNpa(String remarksOnNpa) {
            this.remarksOnNpa = remarksOnNpa;
        }
    
        public String getRemarksOnNpa() {
            return remarksOnNpa;
        }
        
        private String dealingOfficerName;
    
        public void setDealingOfficerName(String dealingOfficerName) {
            this.dealingOfficerName = dealingOfficerName;
        }
    
        public String getDealingOfficerName() {
            return dealingOfficerName;
        }
        
    private Vector workingCapitalDtlsVector;

    public void setWorkingCapitalDtlsVector(Vector workingCapitalDtlsVector) {
        this.workingCapitalDtlsVector = workingCapitalDtlsVector;
    }

    public Vector getWorkingCapitalDtlsVector() {
        return workingCapitalDtlsVector;
    }

    public void setReturnRemarks(String returnRemarks) {
        this.returnRemarks = returnRemarks;
    }
    public String getReturnRemarks() {
        return returnRemarks;
    }
}
