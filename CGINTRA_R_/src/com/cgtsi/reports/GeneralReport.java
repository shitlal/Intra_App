// FrontEnd Plus GUI for JAD
// DeCompiled : GeneralReport.class

package com.cgtsi.reports;

import java.io.Serializable;
import java.util.Date;

public class GeneralReport
    implements Serializable
{

    private String type;
    private String name;
    private String bankName;
    private String zoneName;
    private String memberId;
    private String membnkId;
    private String appRefNo;
    private String ssiName;
    private String status;
    private String cgpan;
    private String ddNum;
    private int proposals;
    private int prevProposals;
    private int cumProposals;
    private int employees;
    private int proposal;
    private double amount;
    private double prevAmount;
    private double cumAmount;
    private double export;
    private double turnover;
    private int inwardNum;
    private String inwardSection;
    private String place;
    private String ltrRefNo;
    private String subject;
    private int instrumentNum;
    private double instrumentAmt;
    private String drawnonBank;
    private Date dateOfTheDocument;
    private Date dateOfTheDocument1;
    private Date dateOfTheDocument2;
    private Date dateOfTheDocument3;
    private Date dateOfTheDocument4;
    private Date dateOfTheDocument5;
    private Date dateOfTheDocument6;
    private Date dateOfTheDocument7;
    private Date dateOfTheDocument8;
    private Date dateOfTheDocument9;
    private Date dateOfTheDocument10;
    private Date dateOfTheDocument11;
    private Date dateOfTheDocument12;
    private Date dateOfTheDocument13;
    private Date dateOfTheDocument14;
    private Date dateOfTheDocument15;
    private Date dateOfTheDocument16;
    private Date dateOfTheDocument17;
    private Date dateOfTheDocument18;
    private Date dateOfTheDocument19;
    private Date dateOfTheDocument20;
    private Date dateOfTheDocument21;
    private Date dateOfTheDocument22;
    private Date dateOfTheDocument23;
    private Date dateOfTheDocument24;
    private Date dateOfTheDocument25;
    private Date dateOfTheDocument26;
    private Date dateOfTheDocument27;
    private Date dateOfTheDocument28;
    private Date dateOfTheDocument29;
    private Date dateOfTheDocument30;
    private Date dateOfTheDocument31;
    private Date dateOfTheDocument32;
    private Date dateOfTheDocument33;
    private Date dateOfTheDocument34;
    private Date dateOfTheDocument35;
    private Date dateOfTheDocument36;
    private Date dateOfTheDocument37;
    private Date dateOfTheDocument38;
    private Date dateOfTheDocument39;
    private Date dateOfTheDocument40;
    private Date dateOfTheDocument41;
    private Date dateOfTheDocument42;
    private Date dateOfTheDocument43;
    private Date dateOfTheDocument44;
    private int claimReceivedCases;
    private int claimSettledCases;
    private int claimForwardCases;
    private int tcCases;
    private int trCases;
    private int rejectedCases;
    private int pendingCases;
    private int claimWithDrawnCases;
    private double claimReceivedAmt;
    private double claimSettledAmt;
    private double claimForwardAmt;
    private double tcAmt;
    private double trAmt;
    private double rejectedAmt;
    private double pendingAmt;
    private double claimWithdrawnAmt;
    private int newDeclReceivedCases;
    private int newDeclNotReceivedCases;
    private double newDeclReceivedAmt;
    private double newDeclNotReceivedAmt;
    private String investmentId;
    private Date depositDate;
    private double depositAmt;
    private String compoundFrequency;
    private double rateofInterest;
    private int tenureYears;
    private int tenureMonths;
    private int tenureDays;
    private Date maturityDate;
    private double maturityAmt;
    private String workshopDt;
    private String city;
    private String topic;
    private int participants;
    private String targetGroup;
    private String organisation;
    private String designation;
    private String reasons;
    private String stateName;
    private String districtName;
    private String mliName;
    private String organisedfor;
    private String agencyName;
    private String appWeaverCreditScheme;
    private Date sanctionDate;
    private Date approvedDate;
    private String category;
    private int number;
    private int totalCount;
    private double totalAmount;
    private String industry;
    private Date closerDate;
    private String closerRemark;
    private Date requestDate;
    private String inwardNumCorr;
    private String txnType;
    private Long instrumentNumLong;
    private Date dateOfTheDocument45;
    private Date dateOfTheDocument46;
    private String unitName;
    private Date npaUpgraDt;
    private Date npaEffDt;
    private String count;
    private double toatlAmounts;
    private String claimRefNum;
    private String MliIds;
    private String payids;
    private String vaccno;
    private String ifsccode;
    private String dantype;
    private String danstatus;
    private Date paymentDate;
    private double Amounts;
    private String paymentstatus;
    private Date paymentcreditDate;
	
	private String employeeId;
    private String userId;
    private String empFName;
    private String empMName;
    private String empLName;
    private String empId;
    private String phoneNo;
    private String emailId;

  //Dipika start 12/1/2018 npa percent above 10
    private int guarNo;
    private double guarAmt;
    private int npaNo;
    private double npaAmt;
    private double npaPercent;
    private double percentAmt;
    //dipika end 12/1/2018 npa percent above 10
    

    
    private int proposals1;
    
    
    public String getProposaltotal() {
		return proposaltotal;
	}

	public void setProposaltotal(String proposaltotal) {
		this.proposaltotal = proposaltotal;
	}


	private String proposaltotal;
	
	
	
    public int getProposals1() {
		return proposals1;
	}

	public void setProposals1(int proposals1) {
		this.proposals1 = proposals1;
	}

	public int getProposals2() {
		return proposals2;
	}

	public void setProposals2(int proposals2) {
		this.proposals2 = proposals2;
	}

	public int getProposals3() {
		return proposals3;
	}

	public void setProposals3(int proposals3) {
		this.proposals3 = proposals3;
	}


    private int proposals2;
    private int proposals3;
    
   
    
    
	public Date getDateOfTheDocument46()
    {
        return dateOfTheDocument46;
    }

    public void setDateOfTheDocument46(Date dateOfTheDocument46)
    {
        this.dateOfTheDocument46 = dateOfTheDocument46;
    }

    public Date getDateOfTheDocument45()
    {
        return dateOfTheDocument45;
    }

    public void setDateOfTheDocument45(Date dateOfTheDocument45)
    {
        this.dateOfTheDocument45 = dateOfTheDocument45;
    }

    public String getCount()
    {
        return count;
    }

    public void setCount(String count)
    {
        this.count = count;
    }

    public double getToatlAmounts()
    {
        return toatlAmounts;
    }

    public void setToatlAmounts(double toatlAmounts)
    {
        this.toatlAmounts = toatlAmounts;
    }

    public String getMliIds()
    {
        return MliIds;
    }

    public void setMliIds(String mliIds)
    {
        MliIds = mliIds;
    }

    public String getPayids()
    {
        return payids;
    }

    public void setPayids(String payids)
    {
        this.payids = payids;
    }

    public String getVaccno()
    {
        return vaccno;
    }

    public void setVaccno(String vaccno)
    {
        this.vaccno = vaccno;
    }

    public Date getPaymentDate()
    {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate)
    {
        this.paymentDate = paymentDate;
    }

    public double getAmounts()
    {
        return Amounts;
    }

    public void setAmounts(double amounts)
    {
        Amounts = amounts;
    }

    public String getPaymentstatus()
    {
        return paymentstatus;
    }

    public void setPaymentstatus(String paymentstatus)
    {
        this.paymentstatus = paymentstatus;
    }

    public String getDantype()
    {
        return dantype;
    }

    public void setDantype(String dantype)
    {
        this.dantype = dantype;
    }

    public String getDanstatus()
    {
        return danstatus;
    }

    public void setDanstatus(String danstatus)
    {
        this.danstatus = danstatus;
    }

    public String getIfsccode()
    {
        return ifsccode;
    }

    public void setIfsccode(String ifsccode)
    {
        this.ifsccode = ifsccode;
    }

    public Date getPaymentcreditDate()
    {
        return paymentcreditDate;
    }

    public void setPaymentcreditDate(Date paymentcreditDate)
    {
        this.paymentcreditDate = paymentcreditDate;
    }

    public String getClaimRefNum()
    {
        return claimRefNum;
    }

    public void setClaimRefNum(String claimRefNum)
    {
        this.claimRefNum = claimRefNum;
    }

    public Long getInstrumentNumLong()
    {
        return instrumentNumLong;
    }

    public void setInstrumentNumLong(Long instrumentNumLong)
    {
        this.instrumentNumLong = instrumentNumLong;
    }

    public String getUnitName()
    {
        return unitName;
    }

    public void setUnitName(String unitName)
    {
        this.unitName = unitName;
    }

    public Date getNpaUpgraDt()
    {
        return npaUpgraDt;
    }

    public void setNpaUpgraDt(Date npaUpgraDt)
    {
        this.npaUpgraDt = npaUpgraDt;
    }

    public Date getNpaEffDt()
    {
        return npaEffDt;
    }

    public void setNpaEffDt(Date npaEffDt)
    {
        this.npaEffDt = npaEffDt;
    }
    public String getMliName() {
		return mliName;
	}

	public void setMliName(String mliName) {
		this.mliName = mliName;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

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


    public GeneralReport()
    {
    }

    public String getDdNum()
    {
        return ddNum;
    }

    public void setDdNum(String ddNum)
    {
        this.ddNum = ddNum;
    }

    public String getDrawnonBank()
    {
        return drawnonBank;
    }

    public void setDrawnonBank(String drawnonBank)
    {
        this.drawnonBank = drawnonBank;
    }

    public double getInstrumentAmt()
    {
        return instrumentAmt;
    }

    public void setInstrumentAmt(double instrumentAmt)
    {
        this.instrumentAmt = instrumentAmt;
    }

    public int getInstrumentNum()
    {
        return instrumentNum;
    }

    public void setInstrumentNum(int instrumentNum)
    {
        this.instrumentNum = instrumentNum;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getLtrRefNo()
    {
        return ltrRefNo;
    }

    public void setLtrRefNo(String ltrRefNo)
    {
        this.ltrRefNo = ltrRefNo;
    }

    public String getPlace()
    {
        return place;
    }

    public void setPlace(String place)
    {
        this.place = place;
    }

    public String getInwardSection()
    {
        return inwardSection;
    }

    public void setInwardSection(String inwardSection)
    {
        this.inwardSection = inwardSection;
    }

    public int getInwardNum()
    {
        return inwardNum;
    }

    public void setInwardNum(int inwardNum)
    {
        this.inwardNum = inwardNum;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String aType)
    {
        type = aType;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String aName)
    {
        name = aName;
    }

    public String getCgpan()
    {
        return cgpan;
    }

    public void setCgpan(String cgpan)
    {
        this.cgpan = cgpan;
    }

    public void setBankName(String bankName)
    {
        this.bankName = bankName;
    }

    public String getBankName()
    {
        return bankName;
    }

    public void setZoneName(String zoneName)
    {
        this.zoneName = zoneName;
    }

    public String getZoneName()
    {
        return zoneName;
    }

    public void setMemberId(String memberId)
    {
        this.memberId = memberId;
    }

    public String getMemberId()
    {
        return memberId;
    }

    public void setMembnkId(String membnkId)
    {
        this.membnkId = membnkId;
    }

    public String getMembnkId()
    {
        return membnkId;
    }

    public void setAppRefNo(String appRefNo)
    {
        this.appRefNo = appRefNo;
    }

    public String getAppRefNo()
    {
        return appRefNo;
    }

    public void setSsiName(String ssiName)
    {
        this.ssiName = ssiName;
    }

    public String getSsiName()
    {
        return ssiName;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    public int getProposals()
    {
        return proposals;
    }

    public void setProposals(int aProposals)
    {
        proposals = aProposals;
    }

    public int getCumProposals()
    {
        return cumProposals;
    }

    public void setCumProposals(int acumProposals)
    {
        cumProposals = acumProposals;
    }

    public int getPrevProposals()
    {
        return prevProposals;
    }

    public void setPrevProposals(int aPrevProposals)
    {
        prevProposals = aPrevProposals;
    }

    public int getProposal()
    {
        return proposal;
    }

    public void setProposal(int aProposal)
    {
        proposal = aProposal;
    }

    public int getEmployees()
    {
        return employees;
    }

    public void setEmployees(int employee)
    {
        employees = employee;
    }

    public double getCumAmount()
    {
        return cumAmount;
    }

    public void setCumAmount(double cAmount)
    {
        cumAmount = cAmount;
    }

    public double getPrevAmount()
    {
        return prevAmount;
    }

    public void setPrevAmount(double pAmount)
    {
        prevAmount = pAmount;
    }

    public double getAmount()
    {
        return amount;
    }

    public void setAmount(double dAmount)
    {
        amount = dAmount;
    }

    public double getTurnover()
    {
        return turnover;
    }

    public void setTurnover(double aturnover)
    {
        turnover = aturnover;
    }

    public double getExport()
    {
        return export;
    }

    public void setExport(double exp)
    {
        export = exp;
    }

    public Date getDateOfTheDocument6()
    {
        return dateOfTheDocument6;
    }

    public Date getDateOfTheDocument7()
    {
        return dateOfTheDocument7;
    }

    public void setDateOfTheDocument6(Date date)
    {
        dateOfTheDocument6 = date;
    }

    public void setDateOfTheDocument7(Date date)
    {
        dateOfTheDocument7 = date;
    }

    public Date getDateOfTheDocument1()
    {
        return dateOfTheDocument1;
    }

    public Date getDateOfTheDocument10()
    {
        return dateOfTheDocument10;
    }

    public Date getDateOfTheDocument11()
    {
        return dateOfTheDocument11;
    }

    public Date getDateOfTheDocument12()
    {
        return dateOfTheDocument12;
    }

    public Date getDateOfTheDocument13()
    {
        return dateOfTheDocument13;
    }

    public Date getDateOfTheDocument14()
    {
        return dateOfTheDocument14;
    }

    public Date getDateOfTheDocument15()
    {
        return dateOfTheDocument15;
    }

    public Date getDateOfTheDocument16()
    {
        return dateOfTheDocument16;
    }

    public Date getDateOfTheDocument17()
    {
        return dateOfTheDocument17;
    }

    public Date getDateOfTheDocument18()
    {
        return dateOfTheDocument18;
    }

    public Date getDateOfTheDocument19()
    {
        return dateOfTheDocument19;
    }

    public Date getDateOfTheDocument2()
    {
        return dateOfTheDocument2;
    }

    public Date getDateOfTheDocument20()
    {
        return dateOfTheDocument20;
    }

    public Date getDateOfTheDocument21()
    {
        return dateOfTheDocument21;
    }

    public Date getDateOfTheDocument22()
    {
        return dateOfTheDocument22;
    }

    public Date getDateOfTheDocument23()
    {
        return dateOfTheDocument23;
    }

    public Date getDateOfTheDocument24()
    {
        return dateOfTheDocument24;
    }

    public Date getDateOfTheDocument25()
    {
        return dateOfTheDocument25;
    }

    public Date getDateOfTheDocument26()
    {
        return dateOfTheDocument26;
    }

    public Date getDateOfTheDocument27()
    {
        return dateOfTheDocument27;
    }

    public Date getDateOfTheDocument3()
    {
        return dateOfTheDocument3;
    }

    public Date getDateOfTheDocument4()
    {
        return dateOfTheDocument4;
    }

    public Date getDateOfTheDocument5()
    {
        return dateOfTheDocument5;
    }

    public Date getDateOfTheDocument8()
    {
        return dateOfTheDocument8;
    }

    public Date getDateOfTheDocument9()
    {
        return dateOfTheDocument9;
    }

    public void setDateOfTheDocument1(Date date)
    {
        dateOfTheDocument1 = date;
    }

    public void setDateOfTheDocument10(Date date)
    {
        dateOfTheDocument10 = date;
    }

    public void setDateOfTheDocument11(Date date)
    {
        dateOfTheDocument11 = date;
    }

    public void setDateOfTheDocument12(Date date)
    {
        dateOfTheDocument12 = date;
    }

    public void setDateOfTheDocument13(Date date)
    {
        dateOfTheDocument13 = date;
    }

    public void setDateOfTheDocument14(Date date)
    {
        dateOfTheDocument14 = date;
    }

    public void setDateOfTheDocument15(Date date)
    {
        dateOfTheDocument15 = date;
    }

    public void setDateOfTheDocument16(Date date)
    {
        dateOfTheDocument16 = date;
    }

    public void setDateOfTheDocument17(Date date)
    {
        dateOfTheDocument17 = date;
    }

    public void setDateOfTheDocument18(Date date)
    {
        dateOfTheDocument18 = date;
    }

    public void setDateOfTheDocument19(Date date)
    {
        dateOfTheDocument19 = date;
    }

    public void setDateOfTheDocument2(Date date)
    {
        dateOfTheDocument2 = date;
    }

    public void setDateOfTheDocument20(Date date)
    {
        dateOfTheDocument20 = date;
    }

    public void setDateOfTheDocument21(Date date)
    {
        dateOfTheDocument21 = date;
    }

    public void setDateOfTheDocument22(Date date)
    {
        dateOfTheDocument22 = date;
    }

    public void setDateOfTheDocument23(Date date)
    {
        dateOfTheDocument23 = date;
    }

    public void setDateOfTheDocument24(Date date)
    {
        dateOfTheDocument24 = date;
    }

    public void setDateOfTheDocument25(Date date)
    {
        dateOfTheDocument25 = date;
    }

    public void setDateOfTheDocument26(Date date)
    {
        dateOfTheDocument26 = date;
    }

    public void setDateOfTheDocument27(Date date)
    {
        dateOfTheDocument27 = date;
    }

    public void setDateOfTheDocument3(Date date)
    {
        dateOfTheDocument3 = date;
    }

    public void setDateOfTheDocument4(Date date)
    {
        dateOfTheDocument4 = date;
    }

    public void setDateOfTheDocument5(Date date)
    {
        dateOfTheDocument5 = date;
    }

    public void setDateOfTheDocument8(Date date)
    {
        dateOfTheDocument8 = date;
    }

    public void setDateOfTheDocument9(Date date)
    {
        dateOfTheDocument9 = date;
    }

    public Date getDateOfTheDocument()
    {
        return dateOfTheDocument;
    }

    public void setDateOfTheDocument(Date date)
    {
        dateOfTheDocument = date;
    }

    public Date getDateOfTheDocument28()
    {
        return dateOfTheDocument28;
    }

    public Date getDateOfTheDocument29()
    {
        return dateOfTheDocument29;
    }

    public Date getDateOfTheDocument30()
    {
        return dateOfTheDocument30;
    }

    public Date getDateOfTheDocument31()
    {
        return dateOfTheDocument31;
    }

    public Date getDateOfTheDocument34()
    {
        return dateOfTheDocument34;
    }

    public Date getDateOfTheDocument35()
    {
        return dateOfTheDocument35;
    }

    public Date getDateOfTheDocument36()
    {
        return dateOfTheDocument36;
    }

    public Date getDateOfTheDocument37()
    {
        return dateOfTheDocument37;
    }

    public Date getDateOfTheDocument38()
    {
        return dateOfTheDocument38;
    }

    public Date getDateOfTheDocument39()
    {
        return dateOfTheDocument39;
    }

    public Date getDateOfTheDocument40()
    {
        return dateOfTheDocument40;
    }

    public Date getDateOfTheDocument41()
    {
        return dateOfTheDocument41;
    }

    public Date getDateOfTheDocument42()
    {
        return dateOfTheDocument42;
    }

    public Date getDateOfTheDocument43()
    {
        return dateOfTheDocument43;
    }

    public void setDateOfTheDocument28(Date date)
    {
        dateOfTheDocument28 = date;
    }

    public void setDateOfTheDocument29(Date date)
    {
        dateOfTheDocument29 = date;
    }

    public void setDateOfTheDocument30(Date date)
    {
        dateOfTheDocument30 = date;
    }

    public void setDateOfTheDocument31(Date date)
    {
        dateOfTheDocument31 = date;
    }

    public Date getDateOfTheDocument32()
    {
        return dateOfTheDocument32;
    }

    public Date getDateOfTheDocument33()
    {
        return dateOfTheDocument33;
    }

    public void setDateOfTheDocument32(Date date)
    {
        dateOfTheDocument32 = date;
    }

    public void setDateOfTheDocument33(Date date)
    {
        dateOfTheDocument33 = date;
    }

    public void setDateOfTheDocument34(Date date)
    {
        dateOfTheDocument34 = date;
    }

    public void setDateOfTheDocument35(Date date)
    {
        dateOfTheDocument35 = date;
    }

    public void setDateOfTheDocument36(Date date)
    {
        dateOfTheDocument36 = date;
    }

    public void setDateOfTheDocument37(Date date)
    {
        dateOfTheDocument37 = date;
    }

    public void setDateOfTheDocument38(Date date)
    {
        dateOfTheDocument38 = date;
    }

    public void setDateOfTheDocument39(Date date)
    {
        dateOfTheDocument39 = date;
    }

    public void setDateOfTheDocument40(Date date)
    {
        dateOfTheDocument40 = date;
    }

    public void setDateOfTheDocument41(Date date)
    {
        dateOfTheDocument41 = date;
    }

    public void setDateOfTheDocument42(Date date)
    {
        dateOfTheDocument42 = date;
    }

    public void setDateOfTheDocument43(Date date)
    {
        dateOfTheDocument43 = date;
    }

    public void setDateOfTheDocument44(Date date)
    {
        dateOfTheDocument44 = date;
    }

    public Date getDateOfTheDocument44()
    {
        return dateOfTheDocument44;
    }

    public void setClaimReceivedCases(int claimReceivedCases)
    {
        this.claimReceivedCases = claimReceivedCases;
    }

    public int getClaimReceivedCases()
    {
        return claimReceivedCases;
    }

    public void setClaimSettledCases(int claimSettledCases)
    {
        this.claimSettledCases = claimSettledCases;
    }

    public int getClaimSettledCases()
    {
        return claimSettledCases;
    }

    public void setClaimForwardCases(int claimForwardCases)
    {
        this.claimForwardCases = claimForwardCases;
    }

    public int getClaimForwardCases()
    {
        return claimForwardCases;
    }

    public void setTcCases(int tcCases)
    {
        this.tcCases = tcCases;
    }

    public int getTcCases()
    {
        return tcCases;
    }

    public void setTrCases(int trCases)
    {
        this.trCases = trCases;
    }

    public int getTrCases()
    {
        return trCases;
    }

    public void setRejectedCases(int rejectedCases)
    {
        this.rejectedCases = rejectedCases;
    }

    public int getRejectedCases()
    {
        return rejectedCases;
    }

    public void setPendingCases(int pendingCases)
    {
        this.pendingCases = pendingCases;
    }

    public int getPendingCases()
    {
        return pendingCases;
    }

    public void setClaimReceivedAmt(double claimReceivedAmt)
    {
        this.claimReceivedAmt = claimReceivedAmt;
    }

    public double getClaimReceivedAmt()
    {
        return claimReceivedAmt;
    }

    public void setClaimSettledAmt(double claimSettledAmt)
    {
        this.claimSettledAmt = claimSettledAmt;
    }

    public double getClaimSettledAmt()
    {
        return claimSettledAmt;
    }

    public void setClaimForwardAmt(double claimForwardAmt)
    {
        this.claimForwardAmt = claimForwardAmt;
    }

    public double getClaimForwardAmt()
    {
        return claimForwardAmt;
    }

    public void setTcAmt(double tcAmt)
    {
        this.tcAmt = tcAmt;
    }

    public double getTcAmt()
    {
        return tcAmt;
    }

    public void setTrAmt(double trAmt)
    {
        this.trAmt = trAmt;
    }

    public double getTrAmt()
    {
        return trAmt;
    }

    public void setRejectedAmt(double rejectedAmt)
    {
        this.rejectedAmt = rejectedAmt;
    }

    public double getRejectedAmt()
    {
        return rejectedAmt;
    }

    public void setPendingAmt(double pendingAmt)
    {
        this.pendingAmt = pendingAmt;
    }

    public double getPendingAmt()
    {
        return pendingAmt;
    }

    public void setClaimWithDrawnCases(int claimWithDrawnCases)
    {
        this.claimWithDrawnCases = claimWithDrawnCases;
    }

    public int getClaimWithDrawnCases()
    {
        return claimWithDrawnCases;
    }

    public void setClaimWithdrawnAmt(double claimWithdrawnAmt)
    {
        this.claimWithdrawnAmt = claimWithdrawnAmt;
    }

    public double getClaimWithdrawnAmt()
    {
        return claimWithdrawnAmt;
    }

    public void setNewDeclReceivedCases(int newDeclReceivedCases)
    {
        this.newDeclReceivedCases = newDeclReceivedCases;
    }

    public int getNewDeclReceivedCases()
    {
        return newDeclReceivedCases;
    }

    public void setNewDeclNotReceivedCases(int newDeclNotReceivedCases)
    {
        this.newDeclNotReceivedCases = newDeclNotReceivedCases;
    }

    public int getNewDeclNotReceivedCases()
    {
        return newDeclNotReceivedCases;
    }

    public void setNewDeclReceivedAmt(double newDeclReceivedAmt)
    {
        this.newDeclReceivedAmt = newDeclReceivedAmt;
    }

    public double getNewDeclReceivedAmt()
    {
        return newDeclReceivedAmt;
    }

    public void setNewDeclNotReceivedAmt(double newDeclNotReceivedAmt)
    {
        this.newDeclNotReceivedAmt = newDeclNotReceivedAmt;
    }

    public double getNewDeclNotReceivedAmt()
    {
        return newDeclNotReceivedAmt;
    }

    public void setInvestmentId(String investmentId)
    {
        this.investmentId = investmentId;
    }

    public String getInvestmentId()
    {
        return investmentId;
    }

    public void setDepositDate(Date depositDate)
    {
        this.depositDate = depositDate;
    }

    public Date getDepositDate()
    {
        return depositDate;
    }

    public void setDepositAmt(double depositAmt)
    {
        this.depositAmt = depositAmt;
    }

    public double getDepositAmt()
    {
        return depositAmt;
    }

    public void setCompoundFrequency(String compoundFrequency)
    {
        this.compoundFrequency = compoundFrequency;
    }

    public String getCompoundFrequency()
    {
        return compoundFrequency;
    }

    public void setRateofInterest(double rateofInterest)
    {
        this.rateofInterest = rateofInterest;
    }

    public double getRateofInterest()
    {
        return rateofInterest;
    }

    public void setTenureYears(int tenureYears)
    {
        this.tenureYears = tenureYears;
    }

    public int getTenureYears()
    {
        return tenureYears;
    }

    public void setTenureMonths(int tenureMonths)
    {
        this.tenureMonths = tenureMonths;
    }

    public int getTenureMonths()
    {
        return tenureMonths;
    }

    public void setTenureDays(int tenureDays)
    {
        this.tenureDays = tenureDays;
    }

    public int getTenureDays()
    {
        return tenureDays;
    }

    public void setMaturityDate(Date maturityDate)
    {
        this.maturityDate = maturityDate;
    }

    public Date getMaturityDate()
    {
        return maturityDate;
    }

    public void setMaturityAmt(double maturityAmt)
    {
        this.maturityAmt = maturityAmt;
    }

    public double getMaturityAmt()
    {
        return maturityAmt;
    }

    public void setOrganisedfor(String organisedfor)
    {
        this.organisedfor = organisedfor;
    }

    public String getOrganisedfor()
    {
        return organisedfor;
    }

    public void setAgencyName(String agencyName)
    {
        this.agencyName = agencyName;
    }

    public String getAgencyName()
    {
        return agencyName;
    }

    public void setTargetGroup(String targetGroup)
    {
        this.targetGroup = targetGroup;
    }

    public String getTargetGroup()
    {
        return targetGroup;
    }

    public void setTopic(String topic)
    {
        this.topic = topic;
    }

    public String getTopic()
    {
        return topic;
    }

    public void setStateName(String stateName)
    {
        this.stateName = stateName;
    }

    public String getStateName()
    {
        return stateName;
    }

    public void setDistrictName(String districtName)
    {
        this.districtName = districtName;
    }

    public String getDistrictName()
    {
        return districtName;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getCity()
    {
        return city;
    }

    public void setParticipants(int participants)
    {
        this.participants = participants;
    }

    public int getParticipants()
    {
        return participants;
    }

    public void setOrganisation(String organisation)
    {
        this.organisation = organisation;
    }

    public String getOrganisation()
    {
        return organisation;
    }

    public void setDesignation(String designation)
    {
        this.designation = designation;
    }

    public String getDesignation()
    {
        return designation;
    }

    public void setReasons(String reasons)
    {
        this.reasons = reasons;
    }

    public String getReasons()
    {
        return reasons;
    }

    public void setAppWeaverCreditScheme(String appWeaverCreditScheme)
    {
        this.appWeaverCreditScheme = appWeaverCreditScheme;
    }

    public String getAppWeaverCreditScheme()
    {
        return appWeaverCreditScheme;
    }

    public void setSanctionDate(Date sanctionDate)
    {
        this.sanctionDate = sanctionDate;
    }

    public Date getSanctionDate()
    {
        return sanctionDate;
    }

    public void setApprovedDate(Date approvedDate)
    {
        this.approvedDate = approvedDate;
    }

    public Date getApprovedDate()
    {
        return approvedDate;
    }

    public Date getCloserDate()
    {
        return closerDate;
    }

    public void setCloserDate(Date closerDate)
    {
        this.closerDate = closerDate;
    }

    public String getCloserRemark()
    {
        return closerRemark;
    }

    public void setCloserRemark(String closerRemark)
    {
        this.closerRemark = closerRemark;
    }

    public Date getRequestDate()
    {
        return requestDate;
    }

    public void setRequestDate(Date requestDate)
    {
        this.requestDate = requestDate;
    }

    public void setInwardNumCorr(String inwardNumCorr)
    {
        this.inwardNumCorr = inwardNumCorr;
    }

    public String getInwardNumCorr()
    {
        return inwardNumCorr;
    }

    public void setTxnType(String txnType)
    {
        this.txnType = txnType;
    }

    public String getTxnType()
    {
        return txnType;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getCategory()
    {
        return category;
    }

    public void setNumber(int number)
    {
        this.number = number;
    }

    public int getNumber()
    {
        return number;
    }

    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }

    public int getTotalCount()
    {
        return totalCount;
    }

    public void setTotalAmount(double totalAmount)
    {
        this.totalAmount = totalAmount;
    }

    public double getTotalAmount()
    {
        return totalAmount;
    }

    public void setIndustry(String industry)
    {
        this.industry = industry;
    }

    public String getIndustry()
    {
        return industry;
    }
    public int getGuarNo() {
		return guarNo;
	}

	public void setGuarNo(int guarNo) {
		this.guarNo = guarNo;
	}

	public double getGuarAmt() {
		return guarAmt;
	}

	public void setGuarAmt(double guarAmt) {
		this.guarAmt = guarAmt;
	}

	public int getNpaNo() {
		return npaNo;
	}

	public void setNpaNo(int npaNo) {
		this.npaNo = npaNo;
	}

	public double getNpaAmt() {
		return npaAmt;
	}

	public void setNpaAmt(double npaAmt) {
		this.npaAmt = npaAmt;
	}

	public double getNpaPercent() {
		return npaPercent;
	}

	public void setNpaPercent(double npaPercent) {
		this.npaPercent = npaPercent;
	}

	public double getPercentAmt() {
		return percentAmt;
	}

	public void setPercentAmt(double percentAmt) {
		this.percentAmt = percentAmt;
	}
	

}
