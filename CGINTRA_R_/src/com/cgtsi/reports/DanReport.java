/*
 * Created on Dec 3, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cgtsi.reports;

import java.util.Date;

/**
 * @author RT14509
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DanReport {

	private String cgpan;
	private String dan;
	private String scheme;
	private String ssi;
	private int count;				
	private String applicationNumber;
	private Date applicationDate;
	private Date danDate;
	private double totalAmount;
	private double guaranteeFee;
	private double guaranteeFeePaid;
	private String memberId;
	private String bank;
	private String zone;
	private String branch;
  private String branchName;
  //getBaseAmnt
  private double baseAmnt;
  //added by vinod@path 17-nov-15
  private double swBhaCessDed;
  
   public double getSwBhaCessDed() {
	return swBhaCessDed;
}

public void setSwBhaCessDed(double swBhaCessDed) {
	this.swBhaCessDed = swBhaCessDed;
}

public double getBaseAmnt() {
	return baseAmnt;
}

//added by kuldeep@path 19-5-2016
private double krishiKalCess;

public double getKrishiKalCess() {
	return krishiKalCess;
}

public void setKrishiKalCess(double krishiKalCess) {
	this.krishiKalCess = krishiKalCess;
}

//following Three attribute are added by sudeep.dhiman@pathinfotech.com for dispalying the 
  // corresponding column in dan report 
  private String status;
  private Date closeDate;
  private double appAmount;
  //added by sukumar for getting Reapproved amount
  private double reAppAmount;
  private String appropriationBy;
  
  // added by sukumar on 08012008 for getting guarantee start date
  private Date guaranteeStartDate;
 

	public DanReport() {
	}
  
  public void setAppropriationBy(String appropriationBy){
  
  this.appropriationBy=appropriationBy;
  }
  public String getAppropriationBy(){
   return this.appropriationBy;
  }
	//setter and getter for guaranteeStartDate added by sukumar
  
  /**
	 * @param date
	 */
	public void setGuaranteeStartDate(Date date) {
		guaranteeStartDate = date;
	}


	/**
	 * @return
	 */
	public Date getGuaranteeStartDate() {
		return guaranteeStartDate;
	}
  //setter getter for status, closeDate and appAmount added by sudeep.dhiman@pathinfotech.com
  /////////////////////////////////////////////
  /**
  * @param String
  */
  public void setStatus(String status)
  {
    this.status = status;
  }
  /**
  * @return String
  */
  public String getStatus()
  {
    return this.status;
  }
  
  /**
  * @param Date
  */
  public void setCloseDate(Date closeDate)
  {
    this.closeDate = closeDate;
  }
  /**
  * @return Date
  */
  public Date getCloseDate()
  {
    return this.closeDate;
  }
  /**
  * @param double
  */
  public void setAppAmount(double appAmount)
  {
    this.appAmount = appAmount;
  }
  /**
  * @return double
  */
  public double getAppAmount()
  {
    return this.appAmount;
  }
  /**
  * @param double
  */
  public void setReAppAmount(double reAppAmount)
  {
    this.reAppAmount = reAppAmount;
  }
  /**
  * @return double
  */
  public double getReAppAmount()
  {
    return this.reAppAmount;
  }
//////////////////////////////////////////

   /**
	 * @return
	 */
	public String getCgpan() {
		return cgpan;
	}

	/**
	 * @param string
	 */
	public void setCgpan(String string) {
		cgpan = string;
	}
	
	

	/**
	 * @return
	 */
	public String getScheme() {
		return scheme;
	}

	/**
	 * @param string
	 */
	public void setScheme(String string) {
		scheme = string;
	}

	/**
	 * @return
	 */
	public String getSsi() {
		return ssi;
	}

	/**
	 * @param string
	 */
	public void setSsi(String string) {
		ssi = string;
	}

	/**
	 * @return
	 */
	public String getApplicationNumber() {
		return applicationNumber;
	}

	/**
	 * @param i
	 */
	public void setApplicationNumber(String iApplicationNumber) {
		applicationNumber = iApplicationNumber;
	}

	/**
	 * @return
	 */
	public Date getApplicationDate() {
		return applicationDate;
	}

	/**
	 * @param date
	 */
	public void setApplicationDate(Date date) {
		applicationDate = date;
	}

	/**
	 * @return
	 */
	public double getTotalAmount() {
		return totalAmount;
	}

	/**
	 * @param d
	 */
	public void setTotalAmount(double d) {
		totalAmount = d;
	}

	/**
	 * @return
	 */
	public double getGuaranteeFee() {
		return guaranteeFee;
	}

	/**
	 * @param d
	 */
	public void setGuaranteeFee(double d) {
		guaranteeFee = d;
	}

	/**
	 * @return
	 */
	public String getMemberId() {
		return memberId;
	}

	/**
	 * @param string 
	 */
	public void setMemberId(String string) {
		memberId = string;
	}

	/**
	 * @return
	 */
	public String getDan() {
		return dan;
	}

	/**
	 * @return
	 */
	public Date getDanDate() {
		return danDate;
	}

	/**
	 * @param string
	 */
	public void setDan(String string) {
		dan = string;
	}

	/**
	 * @param date
	 */
	public void setDanDate(Date date) {
		danDate = date;
	}

	/**
	 * @return
	 */
	public int getCount() {   
		return count;
	}

	/**
	 * @param i
	 */
	public void setCount(int i) {
		count = i;
	}

	/**
	 * @return
	 */
	public double getGuaranteeFeePaid() {
		return guaranteeFeePaid; 
	}

	/**
	 * @param d
	 */
	public void setGuaranteeFeePaid(double d) {
		guaranteeFeePaid = d;
	}

	/**
	 * @return
	 */
	public String getBank() {
		return bank;
	}

	/**
	 * @return
	 */
	public String getBranch() {
		return branch;
	}
/**
	 * @return
	 */
	public String getBranchName() {
		return branchName;
	}
  /**
   * 
   * @param name
   */
  public void setBranchName(String name){
   branchName = name;
  }
	/**
	 * @return
	 */
	public String getZone() {
		return zone;
	}

	/**
	 * @param string
	 */
	public void setBank(String string) {
		bank = string;
	}

	/**
	 * @param string
	 */
	public void setBranch(String string) {
		branch = string;
	}

	/**
	 * @param string
	 */
	public void setZone(String string) {
		zone = string;
	}


//added @path on 07-09-2013
        private double inclSTaxAmnt;
        private double inclECESSAmnt;
        private double inclHECESSAmnt;


    public void setInclSTaxAmnt(double inclSTaxAmnt) {
        this.inclSTaxAmnt = inclSTaxAmnt;
    }

    public double getInclSTaxAmnt() {
        return inclSTaxAmnt;
    }

    public void setInclECESSAmnt(double inclECESSAmnt) {
        this.inclECESSAmnt = inclECESSAmnt;
    }

    public double getInclECESSAmnt() {
        return inclECESSAmnt;
    }

    public void setInclHECESSAmnt(double inclHECESSAmnt) {
        this.inclHECESSAmnt = inclHECESSAmnt;
    }

    public double getInclHECESSAmnt() {
        return inclHECESSAmnt;
    }
    
    //field added for consolidated dan report
    private String stFlag;
    public String getStFlag() {
		return stFlag;
	}

	public void setStFlag(String stFlag) {
		this.stFlag = stFlag;
	}


	private String danType;
    private Date lastDate;
    private String stclass;
    
    private long totalcnt;
    private double totalamt;
    
    private long notpaidcnt;
    private double notpaidamt;
    
    private long paidcnt;
    private double paidamt;
    
    private double totalstamt;
    private double notpaidstamt;
    private double paidstamt;
    
    private double totalecamt;
    private double notpaidecamt;
    private double paidecamt;
    
    private double totalhecamt;
    private double notpaidhecamt;
    private double paidhecamt;
    
    /*
     * added by vinod@path 2-nov-15*/
    private double totalsbamt;
    private double notpaidsbamt;
    private double paidsbamt;
    
    /*added by kuldeep 27-5-16*/
     private double totalkrishikamt;
     private double notpaidkrishikamt;
     private double paidkrishikamt;

    
	public double getTotalkrishikamt() {
		return totalkrishikamt;
	}

	public void setTotalkrishikamt(double totalkrishikamt) {
		this.totalkrishikamt = totalkrishikamt;
	}

	public double getNotpaidkrishikamt() {
		return notpaidkrishikamt;
	}

	public void setNotpaidkrishikamt(double notpaidkrishikamt) {
		this.notpaidkrishikamt = notpaidkrishikamt;
	}

	public double getPaidkrishikamt() {
		return paidkrishikamt;
	}

	public void setPaidkrishikamt(double paidkrishikamt) {
		this.paidkrishikamt = paidkrishikamt;
	}

	public double getTotalsbamt() {
		return totalsbamt;
	}

	public void setTotalsbamt(double totalsbamt) {
		this.totalsbamt = totalsbamt;
	}

	public double getNotpaidsbamt() {
		return notpaidsbamt;
	}

	public void setNotpaidsbamt(double notpaidsbamt) {
		this.notpaidsbamt = notpaidsbamt;
	}

	public double getPaidsbamt() {
		return paidsbamt;
	}

	public void setPaidsbamt(double paidsbamt) {
		this.paidsbamt = paidsbamt;
	}

	public String getDanType() {
		return danType;
	}

	public void setDanType(String danType) {
		this.danType = danType;
	}

	public Date getLastDate() {
		return lastDate;
	}

	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}

	public String getStclass() {
		return stclass;
	}

	public void setStclass(String stclass) {
		this.stclass = stclass;
	}

	public long getTotalcnt() {
		return totalcnt;
	}

	public void setTotalcnt(long totalcnt) {
		this.totalcnt = totalcnt;
	}

	public double getTotalamt() {
		return totalamt;
	}

	public void setTotalamt(double totalamt) {
		this.totalamt = totalamt;
	}

	public long getNotpaidcnt() {
		return notpaidcnt;
	}

	public void setNotpaidcnt(long notpaidcnt) {
		this.notpaidcnt = notpaidcnt;
	}

	public double getNotpaidamt() {
		return notpaidamt;
	}

	public void setNotpaidamt(double notpaidamt) {
		this.notpaidamt = notpaidamt;
	}

	public long getPaidcnt() {
		return paidcnt;
	}

	public void setPaidcnt(long paidcnt) {
		this.paidcnt = paidcnt;
	}

	public double getPaidamt() {
		return paidamt;
	}

	public void setPaidamt(double paidamt) {
		this.paidamt = paidamt;
	}

	public double getTotalstamt() {
		return totalstamt;
	}

	public void setTotalstamt(double totalstamt) {
		this.totalstamt = totalstamt;
	}

	public double getNotpaidstamt() {
		return notpaidstamt;
	}

	public void setNotpaidstamt(double notpaidstamt) {
		this.notpaidstamt = notpaidstamt;
	}

	public double getPaidstamt() {
		return paidstamt;
	}

	public void setPaidstamt(double paidstamt) {
		this.paidstamt = paidstamt;
	}

	public double getTotalecamt() {
		return totalecamt;
	}

	public void setTotalecamt(double totalecamt) {
		this.totalecamt = totalecamt;
	}

	public double getNotpaidecamt() {
		return notpaidecamt;
	}

	public void setNotpaidecamt(double notpaidecamt) {
		this.notpaidecamt = notpaidecamt;
	}

	public double getPaidecamt() {
		return paidecamt;
	}

	public void setPaidecamt(double paidecamt) {
		this.paidecamt = paidecamt;
	}

	public double getTotalhecamt() {
		return totalhecamt;
	}

	public void setTotalhecamt(double totalhecamt) {
		this.totalhecamt = totalhecamt;
	}

	public double getNotpaidhecamt() {
		return notpaidhecamt;
	}

	public void setNotpaidhecamt(double notpaidhecamt) {
		this.notpaidhecamt = notpaidhecamt;
	}

	public double getPaidhecamt() {
		return paidhecamt;
	}

	public void setPaidhecamt(double paidhecamt) {
		this.paidhecamt = paidhecamt;
	}

	public void setBaseAmnt(double baseAmnt) {		
		this.baseAmnt = baseAmnt;
	}
    
}
