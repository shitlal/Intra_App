// Decompiled Using: FrontEnd Plus v2.03 and the JAD Engine
// Available From: http://www.reflections.ath.cx
// Decompiler options: packimports(3) 
// Source File Name:   DANSummary.java

package com.cgtsi.receiptspayments;

import java.util.Date;
//InclSBCESSAmnt

public class DANSummary
{

    public DANSummary()
    {
    }

    public String getMemberId()
    {
        return memberId;
    }

    public void setMemberId(String memberId)
    {
        this.memberId = memberId;
    }

    public Date getClosureDate()
    {
        return closureDate;
    }

    public void setClosureDate(Date closureDate)
    {
        this.closureDate = closureDate;
    }

    public String getBranchName()
    {
        return branchName;
    }

    public void setBranchName(String abranchName)
    {
        branchName = abranchName;
    }

    public String getDanId()
    {
        return danId;
    }

    public void setDanId(String aDanId)
    {
        danId = aDanId;
    }

    public String getCgpan()
    {
        return cgpan;
    }

    public void setCgpan(String pan)
    {
        cgpan = pan;
    }

    public String getUnitname()
    {
        return unitname;
    }

    public void setUnitname(String aunit)
    {
        unitname = aunit;
    }

    public Date getDanDate()
    {
        return danDate;
    }

    public void setDanDate(Date aDanDate)
    {
        danDate = aDanDate;
    }

    public int getNoOfCGPANs()
    {
        return noOfCGPANs;
    }

    public void setNoOfCGPANs(int aNoOfCGPANs)
    {
        noOfCGPANs = aNoOfCGPANs;
    }

    public double getAmountDue()
    {
        return amountDue;
    }

    public void setAmountDue(double aAmountDue)
    {
        amountDue = aAmountDue;
    }

    public double getAmountPaid()
    {
        return amountPaid;
    }

    public void setAmountPaid(double aAmountPaid)
    {
        amountPaid = aAmountPaid;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason(String aReason)
    {
        reason = aReason;
    }

    public double getAmountBeingPaid()
    {
        return amountBeingPaid;
    }

    public void setAmountBengPaid(double aAmountBeingPaid)
    {
        amountBeingPaid = aAmountBeingPaid;
    }

    public boolean getIsAllocated()
    {
        return isAllocated;
    }

    public void setIsAllocated(boolean aIsAllocated)
    {
        isAllocated = aIsAllocated;
    }

    private String danId;
    private String cgpan;
    private String unitname;
    private Date danDate;
    private int noOfCGPANs;
    private double amountDue;
    private double amountPaid;
    private String reason;
    private boolean isAllocated;
    private double amountBeingPaid;
    private String branchName;
    private String memberId;
    private Date closureDate;
    private String appStatus;
    private int originalTenure;
     private int revisedTenure;
     private String lastDateOfRePayment;
     private String appExpiryDate;
     private String termAmountSanctionedtDate;
     private String requestCreatedUserId;
     private String requestCreatedDate;
  

    public void setAppStatus(String appStatus) {
        this.appStatus = appStatus;
    }

    public String getAppStatus() {
        return appStatus;
    }

    public void setOriginalTenure(int originalTenure) {
        this.originalTenure = originalTenure;
    }

    public int getOriginalTenure() {
        return originalTenure;
    }

    public void setLastDateOfRePayment(String lastDateOfRePayment) {
        this.lastDateOfRePayment = lastDateOfRePayment;
    }

    public String getLastDateOfRePayment() {
        return lastDateOfRePayment;
    }

    public void setAppExpiryDate(String appExpiryDate) {
        this.appExpiryDate = appExpiryDate;
    }

    public String getAppExpiryDate() {
        return appExpiryDate;
    }

    public void setTermAmountSanctionedtDate(String termAmountSanctionedtDate) {
        this.termAmountSanctionedtDate = termAmountSanctionedtDate;
    }

    public String getTermAmountSanctionedtDate() {
        return termAmountSanctionedtDate;
    }

    public void setRequestCreatedUserId(String requestCreatedUserId) {
        this.requestCreatedUserId = requestCreatedUserId;
    }

    public String getRequestCreatedUserId() {
        return requestCreatedUserId;
    }

    public void setRequestCreatedDate(String requestCreatedDate) {
        this.requestCreatedDate = requestCreatedDate;
    }

    public String getRequestCreatedDate() {
        return requestCreatedDate;
    }

    public void setRevisedTenure(int revisedTenure) {
        this.revisedTenure = revisedTenure;
    }

    public int getRevisedTenure() {
        return revisedTenure;
    }
    //added by kuldeep@path 24-may-2016
    private double krishiKalCess;
    
    public double getKrishiKalCess() {
		return krishiKalCess;
	}

	public void setKrishiKalCess(double krishiKalCess) {
		this.krishiKalCess = krishiKalCess;
	}

	//added @path on 07-09-2013
    private double inclSTaxAmnt;
    private double inclECESSAmnt;
    private double inclHECESSAmnt;
    private double swBhaCessDed;


	public double getSwBhaCessDed() {
		return swBhaCessDed;
	}

	public void setSwBhaCessDed(double swBhaCessDed) {
		this.swBhaCessDed = swBhaCessDed;
	}

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
}

