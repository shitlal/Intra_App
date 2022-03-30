// FrontEnd Plus GUI for JAD
// DeCompiled : DemandAdvice.class

package com.cgtsi.receiptspayments;

import java.util.Date;

// Referenced classes of package com.cgtsi.receiptspayments:
//            ServiceFee, ShortAmountInfo

public class DemandAdvice
{

    private String danNo;
    private String danType;
    private String bankId;
    private String zoneId;
    private String branchId;
    private Date danGeneratedDate;
    private Date danDueDate;
    private Date danExpiryDate;
    private String cgpan;
    private String borrowerId;
    private double amountRaised;
    private String allocated;
    private String appropriated;
    private String reason;
    private String paymentId;
    private double penalty;
    private String userId;
    private double feeId;
    private double cancelledAmount;
    private Date appropriatedDate;
    private String newDanNo;
    private String ssiRef;
    private String status;
    
       
    public double getStandardRate() {
		return standardRate;
	}

	public void setStandardRate(double standardRate) {
		this.standardRate = standardRate;
	}

	public double getNpaRiskRate() {
		return npaRiskRate;
	}

	public void setNpaRiskRate(double npaRiskRate) {
		this.npaRiskRate = npaRiskRate;
	}

	public double getClaimRiskrate() {
		return claimRiskrate;
	}

	public void setClaimRiskrate(double claimRiskrate) {
		this.claimRiskrate = claimRiskrate;
	}

	public double getFinalRate() {
		return finalRate;
	}

	public void setFinalRate(double finalRate) {
		this.finalRate = finalRate;
	}

	private double standardRate;
    
    private double npaRiskRate;
    
    private double claimRiskrate;
    
    private double finalRate;

    public DemandAdvice()
    {
        appropriatedDate = null;
    }

    public DemandAdvice(double cldanAmount, String cldan)
    {
        appropriatedDate = null;
        amountRaised = cldanAmount;
        danNo = cldan;
    }

    public DemandAdvice(ServiceFee serviceFee)
    {
        appropriatedDate = null;
        danType = "SF";
        bankId = serviceFee.getBankId();
        zoneId = serviceFee.getZoneId();
        branchId = serviceFee.getBranchId();
        cgpan = serviceFee.getCgpan();
        borrowerId = serviceFee.getBorrowerId();
        amountRaised = serviceFee.getServiceAmount();
        feeId = serviceFee.getServiceFeeId();
    }

    public DemandAdvice(ShortAmountInfo shortAmountInfo)
    {
        appropriatedDate = null;
        danType = "SH";
        bankId = shortAmountInfo.getBankId();
        zoneId = shortAmountInfo.getZoneId();
        branchId = shortAmountInfo.getBranchId();
        cgpan = shortAmountInfo.getCgpan();
        borrowerId = shortAmountInfo.getBorrowerId();
        amountRaised = shortAmountInfo.getShortAmount();
        feeId = shortAmountInfo.getShortId();
    }

    public String getSsiRef()
    {
        return ssiRef;
    }

    public void setSsiRef(String ssiRef)
    {
        this.ssiRef = ssiRef;
    }

    public String getDanNo()
    {
        return danNo;
    }

    public void setDanNo(String aDanNo)
    {
        danNo = aDanNo;
    }

    public String getDanType()
    {
        return danType;
    }

    public void setDanType(String aDanType)
    {
        danType = aDanType;
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

    public Date getDanGeneratedDate()
    {
        return danGeneratedDate;
    }

    public void setDanGeneratedDate(Date aDanGeneratedDate)
    {
        danGeneratedDate = aDanGeneratedDate;
    }

    public Date getDanDueDate()
    {
        return danDueDate;
    }

    public void setDanDueDate(Date aDanDueDate)
    {
        danDueDate = aDanDueDate;
    }

    public Date getDanExpiryDate()
    {
        return danExpiryDate;
    }

    public void setDanExpiryDate(Date aDanExpiryDate)
    {
        danExpiryDate = aDanExpiryDate;
    }

    public String getCgpan()
    {
        return cgpan;
    }

    public void setCgpan(String aCgpan)
    {
        cgpan = aCgpan;
    }

    public double getAmountRaised()
    {
        return amountRaised;
    }

    public void setAmountRaised(double aAmountRaised)
    {
        amountRaised = aAmountRaised;
    }

    public String getAllocated()
    {
        return allocated;
    }

    public void setAllocated(String aAllocated)
    {
        allocated = aAllocated;
    }

    public String getAppropriated()
    {
        return appropriated;
    }

    public void setAppropriated(String aAppropriated)
    {
        appropriated = aAppropriated;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason(String aReason)
    {
        reason = aReason;
    }

    public String getPaymentId()
    {
        return paymentId;
    }

    public void setPaymentId(String aPaymentId)
    {
        paymentId = aPaymentId;
    }

    public double getPenalty()
    {
        return penalty;
    }

    public void setPenalty(double aPenalty)
    {
        penalty = aPenalty;
    }

    public Boolean getAllocatedValue()
    {
        return null;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String string)
    {
        userId = string;
    }

    public String getBorrowerId()
    {
        return borrowerId;
    }

    public void setBorrowerId(String string)
    {
        borrowerId = string;
    }

    public double getFeeId()
    {
        return feeId;
    }

    public void setFeeId(double d)
    {
        feeId = d;
    }

    public double getCancelledAmount()
    {
        return cancelledAmount;
    }

    public void setCancelledAmount(double d)
    {
        cancelledAmount = d;
    }

    public Date getAppropriatedDate()
    {
        return appropriatedDate;
    }

    public void setAppropriatedDate(Date date)
    {
        appropriatedDate = date;
    }

    public String getNewDanNo()
    {
        return newDanNo;
    }

    public void setNewDanNo(String string)
    {
        newDanNo = string;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }
}
