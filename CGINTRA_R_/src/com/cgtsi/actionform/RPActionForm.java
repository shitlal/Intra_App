/*
 * Created on Nov 4, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cgtsi.actionform;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
 
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.GenericValidator;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorActionForm;

import com.cgtsi.common.Log;
import com.cgtsi.receiptspayments.AllocationDetail;
import com.cgtsi.receiptspayments.PaymentDetails;
import com.cgtsi.receiptspayments.Voucher;
import com.cgtsi.util.DateHelper;




//import org.apache.struts.validator.ValidatorActionForm;

/**
 * @author NS30571
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RPActionForm extends ValidatorActionForm
{
	private ArrayList glHeads=new ArrayList();
  
  
  private int bankAccountNo;
  private String bankName;
  private String zoneName;
  private String branchName;
  private String comments;
  private double allocatedAmt;
 
//private ArrayList recvryPaymentList;
  private int check[];
//==========added new for Return=======//
 /* 
  private String qryRemarks[];
  public String[] getQryRemarks() {
		return qryRemarks;
	}

	public void setQryRemarks(String[] qryRemarks) {
		this.qryRemarks = qryRemarks;
	}
*/
	

//Diksah
 /* private Map approvedClaimrefnos=new HashMap();
  private Map secClmAppRemarks=new HashMap();*/
  //Diskha end
  
  //private Map approvedClaimrefnos;
  //public Map getApprovedClaimrefnos() {
	//return approvedClaimrefnos;
//}
  
  //Diksha 
  /*public Map getApprovedClaimrefnos() {
		return approvedClaimrefnos;
	}
public void setApprovedClaimrefnos(Map approvedClaimrefnos) {
	this.approvedClaimrefnos = approvedClaimrefnos;
}


public Map getSecClmAppRemarks() {
	return secClmAppRemarks;
}

public void setSecClmAppRemarks(Map secClmAppRemarks) {
	this.secClmAppRemarks = secClmAppRemarks;
}
*/
  
  //Diksha end
  
//private Map secClmAppRemarks;
 
  /* 
  public Map getRt_remarks() {
		return rt_remarks;
	}


	public void setRt_remarks(Map rt_remarks) {
		this.rt_remarks = rt_remarks;
	}
	
  
	public String[] getRtcheck() {
		return rtcheck;
	}

	public void setRtcheck(String[] rtcheck) {
		this.rtcheck = rtcheck;
	}

	private String rtcheck[];
 */	
//==========till here==================//
public int[] getCheck() {
	return check;
}

public void setCheck(int[] check) {
	this.check = check;
}

private ArrayList recvryPaymentList;
  
public ArrayList getRecvryPaymentList() {
	return recvryPaymentList;
}

public void setRecvryPaymentList(ArrayList recvryPaymentList) {
	this.recvryPaymentList = recvryPaymentList;
}

public String getUnitName() {
	return unitName;
}

public void setUnitName(String unitName) {
	this.unitName = unitName;
}

private String unitName;

private String recoveryType;
  

public int getCheckerId() {
	return checkerId;
}

public void setCheckerId(int checkerId) {
	this.checkerId = checkerId;
}

private int checkerId;

public String getRecoveryType() {
	return recoveryType;
}

public void setRecoveryType(String recoveryType) {
	this.recoveryType = recoveryType;
}

private double  firstClaimSetAmt;

private double  guaranteeAppAmt;

public double getGuaranteeAppAmt() {
	return guaranteeAppAmt;
}

public void setGuaranteeAppAmt(double guaranteeAppAmt) {
	this.guaranteeAppAmt = guaranteeAppAmt;
}

private double  amountRemitedtoCgtmse;
	
	public double getAmountRemitedtoCgtmse() {
	return amountRemitedtoCgtmse;
}

public void setAmountRemitedtoCgtmse(double amountRemitedtoCgtmse) {
	this.amountRemitedtoCgtmse = amountRemitedtoCgtmse;
}

	public double getFirstClaimSetAmt() {
	return firstClaimSetAmt;
}

public void setFirstClaimSetAmt(double firstClaimSetAmt) {
	this.firstClaimSetAmt = firstClaimSetAmt;
}

	private String test="";
	private String payInSlipFormat="";

	private String bankGLCode;
	private String bankGLName;
	private String deptCode;
	private double amount;
	private String amountInFigure;
	private String narration;
	private String manager;
	private String asstManager;
	private Map voucherDetails=new HashMap();

	private Map danIds=new HashMap();
	private Map cgpans=new HashMap();
	private Map unitNames=new HashMap();
	private Map facilitiesCovered=new HashMap();
	private Map dueAmounts=new HashMap();
	private Map amountBeingPaid=new HashMap();
	private Map allocatedFlags=new HashMap();
        
        
        //added by upchar temprary
         private Map allocatedFlagKey=new HashMap();
        
	private Map appropriatedFlags=new HashMap();
  // added by sukumar@path on 15-May-2010
  private Map depositedFlags = new HashMap();
  
	private Map remarks=new HashMap();
	private Map notAllocatedReasons=new HashMap();
	private Map danPanDetails=new HashMap();
	private Map newDanIds=new HashMap();

	private ArrayList danSummaries;// = new ArrayList();
	private ArrayList panDetails; // = new ArrayList();
	private ArrayList selectedDANs; // = new ArrayList();
	private ArrayList danRemarks;
	private ArrayList paymentDetails;
	private ArrayList allocatedPanDetails;

	private double instrumentAmount = 0.0;
	private Map firstDisbursementDates=new HashMap();

	private String danNo ;
  //added cgpan,danAmt,applRemarks and danType by sukumar@path on 30-Jan-2010
  private String cgpan;
  private int danAmt;
  private String applRemarks;
  private String danType;
	private String selectMember;
	private String collectingBankName;
	private String accountNumber;
	private String memberId;

	private String paymentId;
  
  
  private String accountName;
  private String ifscCode;
  private String neftCode;
  
  private String inwardId;
  
  
	private Date dateOfRealisation;
  
        private Date dateOfTheDocument24;
  
	private double receivedAmount;

	private Map bankIds=new HashMap();
	private Map zoneIds=new HashMap();
	private Map branchIds=new HashMap();
	private Map waivedFlags=new HashMap();

	private Map amountsRaised=new HashMap();
	private Map penalties=new HashMap();

	String bankId = null;
	String zoneId  = null;
	String branchId  = null;
	String targetURL = null;


	private String modeOfPayment;
	private String modeOfDelivery;
	private String instrumentNo;
	private String instrumentType;
	private Date instrumentDate;
	private String payableAt;
	private String collectingBank;
	private String collectingBankBranch;
	private String cgtsiAccountHoldingBranch;
	private Date paymentDate;
	private String drawnAtBranch;
	private String officerName;
	private String drawnAtBank;
	private String userId;
  //added by sukumar@path on 18/06/2009
  private String selectAll;
	private ArrayList instruments=new ArrayList();

	private java.util.Date fromDate;
	private java.util.Date toDate;

    private Vector mliWiseDanDetails;
    private Vector dateWiseDANDetails;
    
    private double refundAmount;		//Refund Advice
    
    private String dateType;
    private ArrayList paymentList;
    
	private ArrayList gfCardRateList;
	private Map rateId = new TreeMap();
	private Map lowAmount = new TreeMap();
	private Map highAmount = new TreeMap();
	private Map gfRate = new TreeMap();
  //added by sukumar@path for separate Appropriation to GF,ASF and HandiCrafts
  private String allocationType;
  
  //added by sukumar@path on 14-jun-2010 for capturing allocate & appropriate Remarks
  private String remarksforAppropriation;
  
  
  private String newInstrumentNo;
  private Date newInstrumentDt;
  
  
//niteen
 private String payId;
 
 
 private String danId;
 private String danCgpan;
 private double danRaisedAmt;
 

 public String[] getRemark() {
	return remark;
}

public void setRemark(String[] remark) {
	this.remark = remark;
}

private String remark[];

/* private ArrayList danDallocation;
 public ArrayList getDanDallocation() 
 {
	return danDallocation;
}

public void setDanDallocation(ArrayList danDallocation) {
	this.danDallocation = danDallocation;
}*/

/*public String getDanTotalAmount() {
	return danTotalAmount;
}

public void setDanTotalAmount(String danTotalAmount) {
	this.danTotalAmount = danTotalAmount;
}




private String danTotalAmount;*/
 
 
 
 

public TreeMap getDansList() {
	return dansList;
}

public void setDansList(TreeMap dansList) {
	this.dansList = dansList;
}

private TreeMap dansList=new TreeMap();
 
 


public String getDanId() {
	return danId;
}

public void setDanId(String danId) {
	this.danId = danId;
}

public String getDanCgpan() {
	return danCgpan;
}

public void setDanCgpan(String danCgpan) {
	this.danCgpan = danCgpan;
}

public double getDanRaisedAmt() {
	return danRaisedAmt;
}

public void setDanRaisedAmt(double danRaisedAmt) {
	this.danRaisedAmt = danRaisedAmt;
}

public String getPayId() {
	return payId;
}

public void setPayId(String payId) {
	this.payId = payId;
}

private Map receivedAmounts=new HashMap();
 
  
  private Map realizationDates=new HashMap();
  
  private Map payIds=new HashMap();
  
  /**
   * 
   * @param remarksforAppropriation
   */
  public void setRemarksforAppropriation(String remarksforAppropriation)
  {
   this.remarksforAppropriation = remarksforAppropriation;
  }
  
  /**
   * 
   * @return remarksforAppropriation
   */
  public String getremarksforAppropriation()
  {
   return this.remarksforAppropriation;
  }
  /**
   * 
   * @param inwardId
   */
  public void setInwardId(String inwardId)
  {
   this.inwardId = inwardId;
  }
  /**
   * 
   * @return inwardId
   */
  public String getInwardId()
  {
   return this.inwardId;
  }
  
  /**
   * 
   * @param danType
   */
  public void setDanType(String danType)
  {
   this.danType=danType;
  }
  /**
   * 
   * @return danType
   */
  public String getDanType()
  {
   return this.danType;
  }
  /**
   * 
   * @param applRemarks
   */
  public void setApplRemarks(String applRemarks)
  {
   this.applRemarks=applRemarks;
  }
  /**
   * 
   * @return applRemarks
   */
  public String getApplRemarks()
  {
   return this.applRemarks;
  }
  
  /**
   * 
   * @param danAmt
   */
  public void setDanAmt(int danAmt)
  {
  this.danAmt=danAmt;
  }
  /**
   * 
   * @return danAmt
   */
  public int getDanAmt()
  {
   return this.danAmt;
  }
  /**
   * 
   * @param cgpan
   */
  public void setCgpan(String cgpan){
   this.cgpan=cgpan;
  } 
  /**
   * 
   * @return cgpan
   */
  public String getCgpan()
  {
    return this.cgpan;  
  }

	public void setDanId(String key, Object value)
	{
		danIds.put(key,value);
	}

	public Object getDanId(String key)
	{
		return danIds.get(key);
	}

	public Map getAmountBeingPaid()
	{
		return amountBeingPaid;
	}

	public void setAmountBeingPaid(String key, Object value)
	{
		amountBeingPaid.put(key,value);
	}

	public Object getpaidAmounts(String key)
	{
		return amountBeingPaid.get(key);
	}


	public Object getRemarks(String key)
	{
		return remarks.get(key);
	}

	public Map setRemarks()
	{
		return remarks;
	}


/**
 * @return
 */
public String getSelectAll() {
	return selectAll;
}

/**
 * @param string
 */
public void setSelectAll(String string) {
	selectAll = string;
}
		/* (non-Javadoc)
		 * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
		 */
		public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)
		{
			ActionErrors errors=super.validate(mapping, request);

			if(errors.isEmpty())
			{
				Log.log(Log.DEBUG,"RPActionForm","validate","size of cgpans selected"+cgpans.size());

				Log.log(Log.DEBUG,"RPActionForm","validate","param method value is "+request.getParameter("method"));

				if(mapping.getPath().equals("/submitPANPayments")
				&& request.getParameter("method").equals("submitPANPayments"))
				{
					String danId=getDanNo();
					Log.log(Log.DEBUG,"RPActionForm","validate"," danId "+danId);
					if (danId.indexOf(".")>0)
					{
						danId=danId.replace('.','_');
					}
					Log.log(Log.DEBUG,"RPActionForm","validate"," danId "+danId);
					Set cgpansSet=getCgpans().keySet();
					ArrayList panAllocationDetails=(ArrayList)getDanPanDetail(danNo);				
					int allocationSize = panAllocationDetails.size();

					Iterator cgpansIterator=cgpansSet.iterator();
					
					Set notAllocatedReasonsSet = getNotAllocatedReasons().keySet();
					Iterator notAllocatedReasonsIterator = notAllocatedReasonsSet.iterator();
					boolean isAvl=false;
					boolean isDate=true;
					boolean isReasonsGiven=false;
					boolean disDateAfterCurrDate=true;
					boolean validDisDate=true;
					boolean disDateMinLength=true;
					Log.log(Log.DEBUG,"RPActionForm","validate"," cgpan size "+cgpans.size());
						while(cgpansIterator.hasNext())
						{
							String cgpanKey=(String)cgpansIterator.next();
							Log.log(Log.DEBUG,"RPActionForm","validate"," cgpanKey "+cgpanKey);
							String cgpanValue = (String)cgpans.get(cgpanKey);
	
							Log.log(Log.DEBUG,"RPActionForm","validate"," cgpanKey "+cgpanValue.startsWith(danId));
							
							String cgpanPart=cgpanValue.substring(cgpanValue.indexOf("-")+1,cgpanValue.length());
							String tempIdKey = danId+"-"+cgpanPart;
							
							Log.log(Log.DEBUG,"RPActionForm","validate"," cgpan part " + cgpanPart);
							Log.log(Log.DEBUG,"RPActionForm","validate"," frm request " + request.getParameter("allocatedFlags("+tempIdKey+")"));
	
							if(cgpanValue.startsWith(danId.replace('_','.'))
													&& request.getParameter("allocatedFlags("+tempIdKey+")")!=null)
							{
								Log.log(Log.DEBUG,"RPActionForm","validate"," allocated " + cgpanValue);
								
								for (int j=0;j<allocationSize;j++)
								{
									AllocationDetail allocationDetail = (AllocationDetail) panAllocationDetails.get(j);
									if (allocationDetail.getCgpan().equals(cgpanPart))
									{
										allocationDetail.setAllocatedFlag("Y");
										panAllocationDetails.set(j,allocationDetail);
										break;
									}
								}
								
								isAvl=true;
								
								if(cgpanValue.substring(cgpanValue.length()-2,cgpanValue.length()).equals("TC"))
								{
									Log.log(Log.DEBUG,"RPActionForm","validate"," checking dis date " + cgpanKey);
									Log.log(Log.DEBUG,"RPActionForm","validate"," dis date " + ((String)firstDisbursementDates.get(cgpanKey)));
									if(((String)firstDisbursementDates.get(cgpanKey))==null || ((String)firstDisbursementDates.get(cgpanKey)).equals(""))
									{
										Log.log(Log.DEBUG,"RPActionForm","validate"," dis date " + ((String)firstDisbursementDates.get(cgpanKey)));
								
										isDate=false;
//										break;
									
									}else
									{
										java.util.Date currentDate=new java.util.Date();
										SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
										
										String disDate = (String)firstDisbursementDates.get(cgpanKey);

										try{

											String stringDate=dateFormat.format(currentDate);
											if(!disDate.equals(""))
											{
												if(disDate.trim().length()<10)
												{
													disDateMinLength=false;

													Log.log(Log.DEBUG,"RPActionForm","validate"," length is less than zero");
												}
												else
												{
													dateFormat=new SimpleDateFormat("dd/MM/yyyy");

													java.util.Date date=dateFormat.parse(disDate, new ParsePosition(0));
													Log.log(Log.DEBUG,"RPActionForm","validate"," date "+disDate);

													if(date==null)
													{
														validDisDate=false;
													}

												}
												if (disDateMinLength && validDisDate)
												{
													if(DateHelper.compareDates(disDate,stringDate)!=0 && DateHelper.compareDates(disDate,stringDate)!=1)
														{
															disDateAfterCurrDate=false;
														}
												}
											}
										}catch(Exception exp){
											validDisDate=false;
												}
									}
								}
//								break;							
							}
							else
							{
								Log.log(Log.DEBUG,"RPActionForm","validate","not allocated " + cgpanValue);
								Log.log(Log.DEBUG,"RPActionForm","validate","not allocated reason " + notAllocatedReasons.get(cgpanKey));
								
								if (notAllocatedReasons.get(cgpanKey)!=null && !((String)notAllocatedReasons.get(cgpanKey)).equals(""))
								{
									isReasonsGiven=true;
//									break;
								}
								for (int j=0;j<allocationSize;j++)
								{
									AllocationDetail allocationDetail = (AllocationDetail) panAllocationDetails.get(j);
									if (allocationDetail.getCgpan().equals(cgpanPart))
									{
										Log.log(Log.DEBUG,"RPActionForm","validate","setting allocated N " + cgpanKey);
										allocationDetail.setAllocatedFlag("N");
										panAllocationDetails.set(j,allocationDetail);
										break;
									}
								}								
							}
							
							if (request.getParameter("allocatedFlags("+tempIdKey+")")==null)
							{
								Log.log(Log.DEBUG,"RPActionForm","validate","not allocated " + tempIdKey);
								allocatedFlags.put(tempIdKey, "N");
							}
						}


					if(!isAvl)
					{
						Log.log(Log.DEBUG,"RPActionForm","validate"," No CGPANS selected ");
	
						ActionError error=new ActionError("oneCGPANSelected");
	
						errors.add(ActionErrors.GLOBAL_ERROR,error);
					}
					if(!isDate)
					{
						Log.log(Log.DEBUG,"RPActionForm","validate"," No Dates entered");
	
						ActionError error=new ActionError("disbursementDateRequired");
	
						errors.add(ActionErrors.GLOBAL_ERROR,error);
					}
					if (!disDateMinLength)
					{
						String [] errorStrs=new String[2];
						errorStrs[0]="Date of First Disbursement";
						errorStrs[1]="10";
						ActionError error=new ActionError("errors.minlength",errorStrs);
						errors.add(ActionErrors.GLOBAL_ERROR,error);
					}
					if (!disDateAfterCurrDate)
					{
						ActionError actionError=new ActionError("futureDate","Date of First Disbursement");
						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
					}
					if (!validDisDate)
					{
						ActionError actionError=new ActionError("errors.date", "Date of First Disbursement");
						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
					}					
/*					if(!isReasonsGiven)
					{
						Log.log(Log.DEBUG,"RPActionForm","validate"," No Reasons entered");
	
						ActionError error=new ActionError("notAllocatedReasonRequired");
	
						errors.add(ActionErrors.GLOBAL_ERROR,error);
					}*/
					
					
					for (int j=0;j<panAllocationDetails.size();j++)
					{
						
						AllocationDetail allocationDetail = (AllocationDetail) panAllocationDetails.get(j);
						Log.log(Log.DEBUG,"RPActionForm","validate"," cgpan from danpandetails " + allocationDetail.getCgpan());
						if (allocationDetail.getAllocatedFlag().equals("N") && allocationDetail.getAmountDue()>0)
						{
							Log.log(Log.DEBUG,"RPActionForm","validate"," not allocated ");
							danId = danNo;
							danId=danId.replace('.', '_');
							String reasons = (String)notAllocatedReasons.get(danId+"-"+allocationDetail.getCgpan());
							Log.log(Log.DEBUG,"RPActionForm","validate"," reason for not allocated " + reasons);
							
							if ((reasons==null || reasons.equals("")) && ((String)newDanIds.get(danId+"-"+allocationDetail.getCgpan())).equals(""))
							{
								ActionError error=new ActionError("notAllocatedReasonRequired");
								errors.add(ActionErrors.GLOBAL_ERROR,error);
								break;
							}
						}
					}
				}
				/**
				 * To fix bug 39 and 40 - 3/9/2004
				 */
				else if(mapping.getPath().equals("/allocatePayments")
				&& request.getParameter("method").equals("allocatePayments"))
				{
					Log.log(Log.DEBUG,"RPActionForm","validate"," allocations ");

					if(modeOfPayment==null || modeOfPayment.equals(""))
					{
						ActionError error=new ActionError("errors.required","Mode Of Payment");

						errors.add(ActionErrors.GLOBAL_ERROR,error);

					}
					Log.log(Log.DEBUG,"RPActionForm","validate"," paymentDate " + paymentDate);
					if(paymentDate==null  || paymentDate.toString().equals(""))
					{
						ActionError error=new ActionError("errors.required","Payment Date");

						errors.add(ActionErrors.GLOBAL_ERROR,error);

					}
					else
					{
						try
						{
							
							SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
	
							String payDate = paymentDate.toString();	
							
							Log.log(Log.DEBUG,"RPActionForm","validate"," payDate " + payDate.length());
							if(payDate.length()<10)
							{
								ActionError actionError=new ActionError("errors.minlength","Payment Date","10");
								errors.add(ActionErrors.GLOBAL_ERROR,actionError);								
								
														
							}
							else{
								Date date = dateFormat.parse(payDate,new ParsePosition(0));
							
								Log.log(Log.DEBUG,"RPActionForm","validate"," date " + date); 
								if (date==null)
								{
									ActionError actionError=new ActionError("errors.date","Payment Date");
									errors.add(ActionErrors.GLOBAL_ERROR,actionError);								
								}
								else
								{
									Log.log(Log.DEBUG,"RPActionForm","validate"," date not null" ); 
									java.util.Date stringDate=new java.util.Date();
								
									try{
										if(date.compareTo(stringDate)==1)
										{
											ActionError actionError=new ActionError("currentDate" + "paymentDate");
											errors.add(ActionErrors.GLOBAL_ERROR,actionError);
										}

									}catch(NumberFormatException numberFormatException){
									
										Log.log(Log.DEBUG,"RPActionForm","validate"," numberFormatException :" + numberFormatException.getMessage() ); 

									ActionError actionError=new ActionError("errors.date","Payment Date");
									errors.add(ActionErrors.GLOBAL_ERROR,actionError);

									}
								}

							}
							
						} catch (Exception e) {
							
							Log.log(Log.DEBUG,"RPActionForm","validate"," error message" );
							
							ActionError actionError=new ActionError("errors.date","Payment Date");
							errors.add(ActionErrors.GLOBAL_ERROR,actionError);
						}
					}

					if(instrumentNo==null || instrumentNo.equals(""))
					{
						ActionError error=new ActionError("errors.required","Instrument Number");

						errors.add(ActionErrors.GLOBAL_ERROR,error);

					}

					if(instrumentDate==null || instrumentDate.toString().equals(""))
					{
						ActionError error=new ActionError("errors.required","Instrument Date");

						errors.add(ActionErrors.GLOBAL_ERROR,error);

					}
					else
					{

						try
						{
							SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
							String instDate = instrumentDate.toString();						
							
							if(instDate.length()<10)
							{
								ActionError actionError=new ActionError("errors.minlength","Instrument Date","10");
								errors.add(ActionErrors.GLOBAL_ERROR,actionError);								
								
														
							}
							else{
								Date date = dateFormat.parse(instDate,new ParsePosition(0));

								if (date==null)
								{
									ActionError actionError=new ActionError("errors.date","Instrument Date");
									errors.add(ActionErrors.GLOBAL_ERROR,actionError);								
								}

							}
						} catch (Exception e) {
							ActionError actionError=new ActionError("errors.date","Instrument Date");
							errors.add(ActionErrors.GLOBAL_ERROR,actionError);
						}
					}
					if(payableAt==null || payableAt.equals(""))
					{
						ActionError error=new ActionError("errors.required","Payable At");

						errors.add(ActionErrors.GLOBAL_ERROR,error);

					}

					if(drawnAtBank==null || drawnAtBank.equals(""))
					{
						ActionError error=new ActionError("errors.required","Drawn At Bank");

						errors.add(ActionErrors.GLOBAL_ERROR,error);

					}

					if(drawnAtBranch==null || drawnAtBranch.equals(""))
					{
						ActionError error=new ActionError("errors.required","Drawn At Branch");

						errors.add(ActionErrors.GLOBAL_ERROR,error);

					}
				}/*
				* Fix completed
				**/
				else if(mapping.getPath().equals("/addMoreReceiptVoucherDetails")
				||mapping.getPath().equals("/insertReceiptVoucherDetails")
				||mapping.getPath().equals("/addMorePaymentVoucherDetails")
				|| mapping.getPath().equals("/insertPaymentVoucherDetails")
				|| mapping.getPath().equals("/insertJournalVoucherDetails")
				|| mapping.getPath().equals("/addMoreJournalVoucherDetails"))
				{
					Log.log(Log.DEBUG,"RPActionForm","validate"," Voucher");

					Set voucherSet=voucherDetails.keySet();
					Iterator voucherIterator=voucherSet.iterator();
					boolean acCode=false;
					boolean paidTo=false;
					boolean amountRs=false;
					boolean debitCredit=false;
					boolean instrumentTypeBoolean=false;
					boolean instrumentDateBoolean=false;
					boolean instrumentNumBoolean=false;
					boolean advDateBoolean=false;

					while(voucherIterator.hasNext())
					{
						String key=(String)voucherIterator.next();
						Log.log(Log.DEBUG,"RPActionForm","validate"," key "+key);

						Voucher voucher=(Voucher)voucherDetails.get(key);

						if(!acCode && (voucher.getAcCode()==null || voucher.getAcCode().equals("")))
						{
							ActionError error=new ActionError("errors.required","Account Code");

							errors.add(ActionErrors.GLOBAL_ERROR,error);

							acCode=true;
							Log.log(Log.DEBUG,"RPActionForm","validate"," Ac is null ");
						}

						if(!paidTo && (voucher.getPaidTo()==null || voucher.getPaidTo().equals("")))
						{
							ActionError error=null;

							if(mapping.getPath().equals("/addMorePaymentVoucherDetails")
							|| mapping.getPath().equals("/insertPaymentVoucherDetails"))
							{
								error=new ActionError("errors.required","Paid To ");
							}
							else
							{
								error=new ActionError("errors.required","Received From");
							}


							errors.add(ActionErrors.GLOBAL_ERROR,error);
							Log.log(Log.DEBUG,"RPActionForm","validate"," Paid to is null ");
							paidTo=true;
						}

						if(!amountRs && (voucher.getAmountInRs()==null || voucher.getAmountInRs().equals("")))
						{
							ActionError error=new ActionError("errors.required","Amount in Rs");

							errors.add(ActionErrors.GLOBAL_ERROR,error);
							Log.log(Log.DEBUG,"RPActionForm","validate"," Amount in Rs ");

							amountRs=true;
						}

//						if(mapping.getPath().equals("/addMoreJournalVoucherDetails")
//						|| mapping.getPath().equals("/insertJournalVoucherDetails"))
//						{
							if(!debitCredit && (voucher.getDebitOrCredit()==null || voucher.getDebitOrCredit().equals("")))
							{
								ActionError error=new ActionError("errors.required","Debit or Credit ");
	
								errors.add(ActionErrors.GLOBAL_ERROR,error);
	
								Log.log(Log.DEBUG,"RPActionForm","validate"," Debit or Credit ");
								debitCredit=true;
							}
//						}

						if(!instrumentTypeBoolean && (voucher.getInstrumentType()==null || voucher.getInstrumentType().equals("")))
						{
							ActionError error=new ActionError("errors.required","Instrument Type ");

							errors.add(ActionErrors.GLOBAL_ERROR,error);

							Log.log(Log.DEBUG,"RPActionForm","validate"," Instrument type");

							instrumentTypeBoolean=true;
						}

						if(!instrumentDateBoolean)
						{
							if(voucher.getInstrumentDate()==null || voucher.getInstrumentDate().equals(""))
							{
								ActionError error=new ActionError("errors.required","Instrument Date ");

								errors.add(ActionErrors.GLOBAL_ERROR,error);

								Log.log(Log.DEBUG,"RPActionForm","validate"," Instrument date is null ");
								instrumentDateBoolean=true;
							}
							else
							{
								String instrumentDate=voucher.getInstrumentDate();

								if(instrumentDate.trim().length()<10)
								{
									String [] errorStrs=new String[2];
									errorStrs[0]="Instrument Date ";
									errorStrs[1]="10";

									ActionError error=new ActionError("errors.minlength",errorStrs);

									errors.add(ActionErrors.GLOBAL_ERROR,error);
									instrumentDateBoolean=true;

									Log.log(Log.DEBUG,"RPActionForm","validate"," length is less than zero");
								}
								else
								{
									SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");

									java.util.Date date=dateFormat.parse(instrumentDate, new ParsePosition(0));
									Log.log(Log.DEBUG,"RPActionForm","validate"," date "+date);

									if(date==null)
									{
										ActionError error=new ActionError("errors.date","Instrument Date");

										errors.add(ActionErrors.GLOBAL_ERROR,error);
										instrumentDateBoolean=true;
									}

								}
							}
						}
						if(!instrumentNumBoolean && (voucher.getInstrumentNo()==null || voucher.getInstrumentNo().equals("")))
						{
							ActionError error=new ActionError("errors.required","Instrument Number ");

							errors.add(ActionErrors.GLOBAL_ERROR,error);

							Log.log(Log.DEBUG,"RPActionForm","validate"," Instrument Num is null ");

							instrumentNumBoolean=true;
						}

						Log.log(Log.DEBUG,"RPActionForm","validate","advDateBoolean "+advDateBoolean+","+voucher.getAdvDate());
						if(!advDateBoolean && voucher.getAdvDate()!=null && !voucher.getAdvDate().equals(""))
						{
							String advDate=voucher.getAdvDate();

							if(advDate.trim().length()<10)
							{
								String [] errorStrs=new String[2];
								errorStrs[0]="Adv. Date ";
								errorStrs[1]="10";

								ActionError error=new ActionError("errors.minlength",errorStrs);

								errors.add(ActionErrors.GLOBAL_ERROR,error);

								Log.log(Log.DEBUG,"RPActionForm","validate"," length is less than zero");
							}
							else
							{
								SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");

								java.util.Date date=dateFormat.parse(advDate, new ParsePosition(0));
								Log.log(Log.DEBUG,"RPActionForm","validate"," date "+date);

								if(date==null)
								{
									ActionError error=new ActionError("errors.date","Adv Date");

									errors.add(ActionErrors.GLOBAL_ERROR,error);
									instrumentDateBoolean=true;
								}

							}
							advDateBoolean=true;
						}
						Log.log(Log.DEBUG,"RPActionForm","validate","instrumentDateBoolean," +
							"instrumentTypeBoolean,instrumentNumBoolean,acCode,paidTo,amountRs,debitCredit "+
						instrumentDateBoolean+","+instrumentTypeBoolean+","+instrumentNumBoolean+","+acCode+","+paidTo+","+amountRs+","+debitCredit);

						if(instrumentDateBoolean && instrumentTypeBoolean
						&& instrumentNumBoolean && acCode && paidTo&& amountRs && debitCredit)
						{
							break;
						}
					}

				}
				
			}
			
			if(mapping.getPath().equals("/afterAppropriatePayments")
			&& request.getParameter("method").equals("appropriatePayments"))
			{
      //System.out.println("ritesh in rpaction form ");
				Map appMap=getAppropriatedFlags();
				Set appSet=appMap.keySet();
				Iterator appIterator=appSet.iterator();
				//System.out.println(" appMap "+appMap);
				boolean atleastOneAppropriated=false;
				while(appIterator.hasNext())
				{
					Object key=appIterator.next();
					String value=(String)appMap.get(key);
					Log.log(Log.DEBUG,"RPActionForm","validate",key+","+value);
					
					Log.log(Log.DEBUG,"RPActionForm","validate",(request.getParameter("appropriatedFlags("+(String)key+")")));
					
					if(request.getParameter("appropriatedFlags("+(String)key+")")!=null)
					{
						Log.log(Log.DEBUG,"RPActionForm","validate","request has values ");	
						atleastOneAppropriated=true;
					}
					else
					{
						appMap.put(key,"N");
					}
				}
				if (!atleastOneAppropriated)
				{
					ActionError error=new ActionError("appropriateOne");
					errors.add(ActionErrors.GLOBAL_ERROR,error);
				}
				ArrayList payments = getPaymentDetails();
				PaymentDetails payment = (PaymentDetails) payments.get(0);
				String instNo = payment.getInstrumentNo();
				/*if(GenericValidator.isBlankOrNull(instNo)){
					ActionError error = new ActionError("instrumentNumberRequired");
					errors.add(ActionErrors.GLOBAL_ERROR, error);
				}*/
				
				Date realisationDate = getDateOfRealisation();
				Date instDate = getInstrumentDate();
				if(instDate == null || instDate.equals("")){
					ActionError error = new ActionError("instrumentDateRequired");
					errors.add(ActionErrors.GLOBAL_ERROR, error);
				}
				if (realisationDate!=null && !realisationDate.toString().equals("") && instDate!=null)
				{
					if (realisationDate.compareTo(instDate)<0)
					{
						ActionError error=new ActionError("dtRealisationGTEqualInstDate");
						errors.add(ActionErrors.GLOBAL_ERROR,error);					
					}
				}
			}
			
			if(mapping.getPath().equals("/afterAppropriatePaymentsForGF")
					&& request.getParameter("method").equals("appropriatePaymentsForGF"))
					{
		      //System.out.println("ritesh in rpaction form ");
						
						
						Map appMap=getAppropriatedFlags();
						Set appSet=appMap.keySet();
						Iterator appIterator=appSet.iterator();
						//System.out.println(" appMap "+appMap);
						boolean atleastOneAppropriated=false;
						while(appIterator.hasNext())
						{
							Object key=appIterator.next();
							String value=(String)appMap.get(key);
							Log.log(Log.DEBUG,"RPActionForm","validate",key+","+value);
							
							Log.log(Log.DEBUG,"RPActionForm","validate",(request.getParameter("appropriatedFlags("+(String)key+")")));
							
							if(request.getParameter("appropriatedFlags("+(String)key+")")!=null)
							{
								Log.log(Log.DEBUG,"RPActionForm","validate","request has values ");	
								atleastOneAppropriated=true;
							}
							else
							{
								appMap.put(key,"N");
							}
						}
						if (!atleastOneAppropriated)
						{
							ActionError error=new ActionError("appropriateOne");
							errors.add(ActionErrors.GLOBAL_ERROR,error);
						}
						ArrayList payments = getPaymentDetails();
						PaymentDetails payment = (PaymentDetails) payments.get(0);
						String instNo = payment.getInstrumentNo();
						if(GenericValidator.isBlankOrNull(instNo)){
							ActionError error = new ActionError("instrumentNumberRequired");
							errors.add(ActionErrors.GLOBAL_ERROR, error);
						}
						
						Date realisationDate = getDateOfRealisation();
						Date instDate = getInstrumentDate();
						if(instDate == null || instDate.equals("")){
							ActionError error = new ActionError("instrumentDateRequired");
							errors.add(ActionErrors.GLOBAL_ERROR, error);
						}
						if (realisationDate!=null && !realisationDate.toString().equals("") && instDate!=null)
						{
							if (realisationDate.compareTo(instDate)<0)
							{
								ActionError error=new ActionError("dtRealisationGTEqualInstDate");
								errors.add(ActionErrors.GLOBAL_ERROR,error);					
							}
						}
					}
			if(mapping.getPath().equals("/afterAppropriatePaymentsForASF")
					&& request.getParameter("method").equals("appropriatePaymentsForASF"))
					{
		      //System.out.println("ritesh in rpaction form ");
						Map appMap=getAppropriatedFlags();
						Set appSet=appMap.keySet();
						Iterator appIterator=appSet.iterator();
						//System.out.println(" appMap "+appMap);
						boolean atleastOneAppropriated=false;
						while(appIterator.hasNext())
						{
							Object key=appIterator.next();
							String value=(String)appMap.get(key);
							Log.log(Log.DEBUG,"RPActionForm","validate",key+","+value);
							
							Log.log(Log.DEBUG,"RPActionForm","validate",(request.getParameter("appropriatedFlags("+(String)key+")")));
							
							if(request.getParameter("appropriatedFlags("+(String)key+")")!=null)
							{
								Log.log(Log.DEBUG,"RPActionForm","validate","request has values ");	
								atleastOneAppropriated=true;
							}
							else
							{
								appMap.put(key,"N");
							}
						}
						if (!atleastOneAppropriated)
						{
							ActionError error=new ActionError("appropriateOne");
							errors.add(ActionErrors.GLOBAL_ERROR,error);
						}
						ArrayList payments = getPaymentDetails();
						PaymentDetails payment = (PaymentDetails) payments.get(0);
						String instNo = payment.getInstrumentNo();
						/*if(GenericValidator.isBlankOrNull(instNo)){
							ActionError error = new ActionError("instrumentNumberRequired");
							errors.add(ActionErrors.GLOBAL_ERROR, error);
						}*/
						
						Date realisationDate = getDateOfRealisation();
						Date instDate = getInstrumentDate();
						if(instDate == null || instDate.equals("")){
							ActionError error = new ActionError("instrumentDateRequired");
							errors.add(ActionErrors.GLOBAL_ERROR, error);
						}
						if (realisationDate!=null && !realisationDate.toString().equals("") && instDate!=null)
						{
							if (realisationDate.compareTo(instDate)<0)
							{
								ActionError error=new ActionError("dtRealisationGTEqualInstDate");
								errors.add(ActionErrors.GLOBAL_ERROR,error);					
							}
						}
			}
			if(mapping.getPath().equals("/afterAppropriatePaymentsForCLAIM")
					&& request.getParameter("method").equals("appropriatePaymentsForASF"))
					{
						Map appMap=getAppropriatedFlags();
						Set appSet=appMap.keySet();
						Iterator appIterator=appSet.iterator();
						//System.out.println(" appMap "+appMap);
						boolean atleastOneAppropriated=false;
						while(appIterator.hasNext())
						{
							Object key=appIterator.next();
							String value=(String)appMap.get(key);
							Log.log(Log.DEBUG,"RPActionForm","validate",key+","+value);
							
							Log.log(Log.DEBUG,"RPActionForm","validate",(request.getParameter("appropriatedFlags("+(String)key+")")));
							
							if(request.getParameter("appropriatedFlags("+(String)key+")")!=null)
							{
								Log.log(Log.DEBUG,"RPActionForm","validate","request has values ");	
								atleastOneAppropriated=true;
							}
							else
							{
								appMap.put(key,"N");
							}
						}
						if (!atleastOneAppropriated)
						{
							ActionError error=new ActionError("appropriateOne");
							errors.add(ActionErrors.GLOBAL_ERROR,error);
						}
						ArrayList payments = getPaymentDetails();
						PaymentDetails payment = (PaymentDetails) payments.get(0);
						String instNo = payment.getInstrumentNo();
						/*if(GenericValidator.isBlankOrNull(instNo)){
							ActionError error = new ActionError("instrumentNumberRequired");
							errors.add(ActionErrors.GLOBAL_ERROR, error);
						}*/
						
						Date realisationDate = getDateOfRealisation();
						Date instDate = getInstrumentDate();
						if(instDate == null || instDate.equals("")){
							ActionError error = new ActionError("instrumentDateRequired");
							errors.add(ActionErrors.GLOBAL_ERROR, error);
						}
						if (realisationDate!=null && !realisationDate.toString().equals("") && instDate!=null)
						{
							if (realisationDate.compareTo(instDate)<0)
							{
								ActionError error=new ActionError("dtRealisationGTEqualInstDate");
								errors.add(ActionErrors.GLOBAL_ERROR,error);					
							}
						}
				}
			
			
			
			
			
			if(mapping.getPath().equals("/submitReallocationPayments")
			&& request.getParameter("method").equals("submitReallocationPayments"))
			{
				Map appMap=getCgpans();
				Set appSet=appMap.keySet();
				Iterator appIterator=appSet.iterator();
//				System.out.println(" appMap "+appMap);
				boolean atleastOneReallocated=false;
				boolean isDate=true;
				boolean disDateMinLength=true;
				boolean validDisDate=true;
				boolean disDateAfterCurrDate=true;
				while(appIterator.hasNext())
				{
					Object key=appIterator.next();
					String value=(String)appMap.get(key);
					Log.log(Log.DEBUG,"RPActionForm","validate",key+","+value);
					String cgpanValue = ((String)key).substring(((String)key).indexOf('-')+1, ((String)key).length());
					
					Log.log(Log.DEBUG,"RPActionForm","validate",(request.getParameter("cgpan("+(String)key+")")));
					
					if(request.getParameter("cgpan("+(String)key+")")!=null)
					{
						Log.log(Log.DEBUG,"RPActionForm","validate","request has values ");	
						atleastOneReallocated=true;
						
						if (cgpanValue.substring(cgpanValue.length()-2,cgpanValue.length()).equals("TC"))
						{
							if(((String)firstDisbursementDates.get(key))==null || ((String)firstDisbursementDates.get(key)).equals(""))
							{
								Log.log(Log.DEBUG,"RPActionForm","validate"," dis date " + ((String)firstDisbursementDates.get(key)));

								isDate=false;
							}else
							{
								java.util.Date currentDate=new java.util.Date();
								SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
		
								String disDate = (String)firstDisbursementDates.get(key);

								try{

									String stringDate=dateFormat.format(currentDate);
									if(!disDate.equals(""))
									{
										if(disDate.trim().length()<10)
										{
											disDateMinLength=false;

											Log.log(Log.DEBUG,"RPActionForm","validate"," length is less than zero");
										}
										else
										{
											dateFormat=new SimpleDateFormat("dd/MM/yyyy");

											java.util.Date date=dateFormat.parse(disDate, new ParsePosition(0));
											Log.log(Log.DEBUG,"RPActionForm","validate"," date "+disDate);

											if(date==null)
											{
												validDisDate=false;
											}

										}
										if (disDateMinLength && validDisDate)
										{
											if(DateHelper.compareDates(disDate,stringDate)!=0 && DateHelper.compareDates(disDate,stringDate)!=1)
												{
													disDateAfterCurrDate=false;
												}
										}
									}
								}catch(Exception exp){
									validDisDate=false;
										}
							}
						}
					}
					else
					{
						appMap.put(key,"N");
					}
				}
				if (!atleastOneReallocated)
				{
					ActionError error=new ActionError("reallocateOne");
					errors.add(ActionErrors.GLOBAL_ERROR,error);
				}
				if(!isDate)
				{
					Log.log(Log.DEBUG,"RPActionForm","validate"," No Dates entered");
	
					ActionError error=new ActionError("disbursementDateRequired");
	
					errors.add(ActionErrors.GLOBAL_ERROR,error);
				}
				Log.log(Log.DEBUG,"RPActionForm","validate"," disDateMinLength :" + disDateMinLength);
				if (!disDateMinLength)
				{
/*					String [] errorStrs=new String[2];
					errorStrs[0]="Date of First Disbursement";
					errorStrs[1]="10";
*/					ActionError error=new ActionError("errors.minlength","Date of First Disbursement","10");
					errors.add(ActionErrors.GLOBAL_ERROR,error);
				}
				if (!disDateAfterCurrDate)
				{
					ActionError actionError=new ActionError("futureDate","Date of First Disbursement");
					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}
				if (!validDisDate)
				{
					ActionError actionError=new ActionError("errors.date", "Date of First Disbursement");
					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}
			}
			
			
			return errors;
		}

		private ActionError generateError(Map map,String message)
		{
			ActionError error=null;
			Iterator iterator=map.keySet().iterator();

			while(iterator.hasNext())
			{
				String data=(String)map.get(iterator.next());
				if(data==null || data.trim().equals(""))
				{
					return new ActionError("errors.all.required",message);
				}
			}
			return error;
		}

		/* (non-Javadoc)
		 * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.ServletRequest)
		 */
		public void reset(ActionMapping arg0, ServletRequest arg1) {
			super.reset(arg0, arg1);
			/*dates.clear();
			cgpans.clear();
			cgbids.clear();
			headerFours.clear();*/
		}

	public void reset(ActionMapping arg0, HttpServletRequest arg1) {
			super.reset(arg0, arg1);
			/*dates.clear();
			cgpans.clear();
			cgbids.clear();
			headerFours.clear();*/
		}

	/**
	 * @return
	 */
	public Map getDanIds() {
		return danIds;
	}

	/**
	 * @return
	 */
	public ArrayList getDanSummaries() {
		return danSummaries;
	}

	/**
	 * @return
	 */
	public Map getRemarks() {
		return remarks;
	}


	/**
	 * @param map
	 */
	public void setAmountBeingPaid(Map map) {
		amountBeingPaid = map;
	}

	/**
	 * @param map
	 */
	public void setDanIds(Map map) {
		danIds = map;
	}

	/**
	 * @param list
	 */
	public void setDanSummaries(ArrayList list) {
		danSummaries = list;
	}

	/**
	 * @param map
	 */
	public void setRemarks(Map map) {
		remarks = map;
	}

	/**
	 * @return
	 */
	public Map getAllocatedFlags() {
		return allocatedFlags;
	}

	/**
	 * @param map
	 */
	public void setAllocatedFlag(String key,Object value)
	{
		allocatedFlags.put(key,value);
	}

	/**
	 * @return
	 */
	public Object getAllocatedFlag(String key)
	{
		return allocatedFlags.get(key);
	}

	/**
	 * @param map
	 */
	public void setAllocatedFlags(Map map) {
		allocatedFlags = map;
	}

	/**
	 * @return
	 */
	public double getInstrumentAmount() {
		return instrumentAmount;
	}

	/**
	 * @param d
	 */
	public void setInstrumentAmount(double d) {
		instrumentAmount = d;
	}

	/**
	 * @return
	 */
	public Map getCgpans() {
		return cgpans;
	}

	/**
	 * @return
	 */
	public Object getCgpan(String key)
	{
		return cgpans.get(key);
	}

	/**
	 * @param map
	 */
	public void setCgpan(String key,Object value) {
		cgpans.put(key,value);
	}


	/**
	 * @return
	 */
	public String getDanNo() {
		return danNo;
	}

	/**
	 * @param map
	 */
	public void setCgpans(Map map) {
		cgpans = map;
	}

	/**
	 * @param string
	 */
	public void setDanNo(String string) {
		danNo = string;
	}

	/**
	 * @return
	 */
	public Map getFirstDisbursementDates() {
		return firstDisbursementDates;
	}

	/**
	 * @param map
	 */
	public void setFirstDisbursementDates(Map map) {
		firstDisbursementDates = map;
	}

	/**
	 * @return
	 */
	public Date getDateOfRealisation() {
		return dateOfRealisation;
	}
  
  /**
   * 
   * @return dateOfTheDocument24
   */
  public Date getDateOfTheDocument24()
  {
   return this.dateOfTheDocument24;
  }
  
  /**
   * 
   * @param dateOfTheDocument24
   */
  public void setDateOfTheDocument24(Date dateOfTheDocument24)
  {
   this.dateOfTheDocument24 = dateOfTheDocument24;
  }

	/**
	 * @return
	 */
	public String getPaymentId() {
		return paymentId;
	}

	/**
	 * @return
	 */
	public double getReceivedAmount() {
		return receivedAmount;
	}

	/**
	 * @param date
	 */
	public void setDateOfRealisation(Date date) {
		dateOfRealisation = date;
	}

	/**
	 * @param string
	 */
	public void setPaymentId(String string) {
		paymentId = string;
	}

	/**
	 * @param d
	 */
	public void setReceivedAmount(double d) {
		receivedAmount = d;
	}

	/**
	 * @return
	 */
	public Map getBankIds() {
		return bankIds;
	}

	/**
	 * @return
	 */
	public Map getBranchIds() {
		return branchIds;
	}

	/**
	 * @return
	 */
	public Map getZoneIds() {
		return zoneIds;
	}

	/**
	 * @param map
	 */
	public void setBankIds(Map map) {
		bankIds = map;
	}

	/**
	 * @param map
	 */
	public void setBranchIds(Map map) {
		branchIds = map;
	}

	/**
	 * @param map
	 */
	public void setZoneIds(Map map) {
		zoneIds = map;
	}

	/**
	 * @return
	 */
	public Map getAmountsRaised() {
		return amountsRaised;
	}

	/**
	 * @return
	 */
	public Map getPenalties() {
		return penalties;
	}

	/**
	 * @param map
	 */
	public void setAmountsRaised(Map map) {
		amountsRaised = map;
	}

	/**
	 * @param map
	 */
	public void setPenalties(Map map) {
		penalties = map;
	}

	/**
	 * @return
	 */
/*	public ArrayList getMliNames() {
		return mliNames;
	}

	/**
	 * @param list
	 *
	public void setMliNames(ArrayList list) {
		mliNames = list;
	}
*/
	/**
	 * @return
	 */
	public String getSelectMember() {
		return selectMember;
	}

	/**
	 * @param string
	 */
	public void setSelectMember(String string) {
		selectMember = string;
	}

	/**
	 * @return
	 */
	public String getBankId() {
		return bankId;
	}

	/**
	 * @return
	 */
	public String getBranchId() {
		return branchId;
	}

	/**
	 * @return
	 */
	public String getZoneId() {
		return zoneId;
	}

	/**
	 * @param string
	 */
	public void setBankId(String string) {
		bankId = string;
	}

	/**
	 * @param string
	 */
	public void setBranchId(String string) {
		branchId = string;
	}

	/**
	 * @param string
	 */
	public void setZoneId(String string) {
		zoneId = string;
	}

	/**
	 * @return
	 */
	public String getTargetURL() {
		return targetURL;
	}

	/**
	 * @param string
	 */
	public void setTargetURL(String string) {
		targetURL = string;
	}

	/**
	 * @return
	 */
	public Map getDueAmounts() {
		return dueAmounts;
	}

	/**
	 * @return
	 */
	public Map getFacilitiesCovered() {
		return facilitiesCovered;
	}

	/**
	 * @return
	 */
	public Map getUnitNames() {
		return unitNames;
	}

	/**
	 * @param map
	 */
	public void setDueAmounts(Map map) {
		dueAmounts = map;
	}

	/**
	 * @param map
	 */
	public void setFacilitiesCovered(Map map) {
		facilitiesCovered = map;
	}

	/**
	 * @param map
	 */
	public void setUnitNames(Map map) {
		unitNames = map;
	}

	/**
	 * @return
	 */
	public ArrayList getPanDetails() {
		return panDetails;
	}

	/**
	 * @param list
	 */
	public void setPanDetails(ArrayList list) {
		panDetails = list;
	}

	/**
	 * @return
	 */
	public Map getNotAllocatedReasons() {
		return notAllocatedReasons;
	}

	/**
	 * @param map
	 */
	public void setNotAllocatedReasons(Map map) {
		notAllocatedReasons = map;
	}


	/**
	 * @return
	 */
	public Object getNotAllocatedReason(String key) {
		return notAllocatedReasons.get(key);
	}

	/**
	 * @param map
	 */
	public void setNotAllocatedReason(String key,Object value)
	{
		notAllocatedReasons.put(key,value);
	}
	/**
	 * @return
	 */
	public ArrayList getSelectedDANs() {
		return selectedDANs;
	}

	/**
	 * @param list
	 */
	public void setSelectedDANs(ArrayList list) {
		selectedDANs = list;
	}

	/**
	 * @return
	 */
	public String getAccountNumber() {
		return accountNumber;
	}

	/**
	 * @return
	 */
	public String getCollectingBankName() {
		return collectingBankName;
	}

	/**
	 * @param string
	 */
	public void setAccountNumber(String string) {
		accountNumber = string;
	}

	/**
	 * @param string
	 */
	public void setCollectingBankName(String string) {
		collectingBankName = string;
	}

	/**
	 * @return
	 */
	public String getCgtsiAccountHoldingBranch() {
		return cgtsiAccountHoldingBranch;
	}

	/**
	 * @return
	 */
	public String getCollectingBank() {
		return collectingBank;
	}

	/**
	 * @return
	 */
	public String getCollectingBankBranch() {
		return collectingBankBranch;
	}

	/**
	 * @return
	 */
	public String getDrawnAtBank() {
		return drawnAtBank;
	}

	/**
	 * @return
	 */
	public String getDrawnAtBranch() {
		return drawnAtBranch;
	}

	/**
	 * @return
	 */
	public Date getInstrumentDate() {
		return instrumentDate;
	}

	/**
	 * @return
	 */
	public String getInstrumentNo() {
		return instrumentNo;
	}

	/**
	 * @return
	 */
	public String getInstrumentType() {
		return instrumentType;
	}

	/**
	 * @return
	 */
	public String getModeOfDelivery() {
		return modeOfDelivery;
	}

	/**
	 * @return
	 */
	public String getModeOfPayment() {
		return modeOfPayment;
	}

	/**
	 * @return
	 */
	public String getOfficerName() {
		return officerName;
	}

	/**
	 * @return
	 */
	public String getPayableAt() {
		return payableAt;
	}

	/**
	 * @return
	 */
	public Date getPaymentDate() {
		return paymentDate;
	}

	/**
	 * @return
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param string
	 */
	public void setCgtsiAccountHoldingBranch(String string) {
		cgtsiAccountHoldingBranch = string;
	}

	/**
	 * @param string
	 */
	public void setCollectingBank(String string) {
		collectingBank = string;
	}

	/**
	 * @param string
	 */
	public void setCollectingBankBranch(String string) {
		collectingBankBranch = string;
	}

	/**
	 * @param string
	 */
	public void setDrawnAtBank(String string) {
		drawnAtBank = string;
	}

	/**
	 * @param string
	 */
	public void setDrawnAtBranch(String string) {
		drawnAtBranch = string;
	}

	/**
	 * @param date
	 */
	public void setInstrumentDate(Date date) {
		instrumentDate = date;
	}

	/**
	 * @param string
	 */
	public void setInstrumentNo(String string) {
		instrumentNo = string;
	}

	/**
	 * @param string
	 */
	public void setInstrumentType(String string) {
		instrumentType = string;
	}

	/**
	 * @param string
	 */
	public void setModeOfDelivery(String string) {
		modeOfDelivery = string;
	}

	/**
	 * @param string
	 */
	public void setModeOfPayment(String string) {
		modeOfPayment = string;
	}

	/**
	 * @param string
	 */
	public void setOfficerName(String string) {
		officerName = string;
	}

	/**
	 * @param string
	 */
	public void setPayableAt(String string) {
		payableAt = string;
	}

	/**
	 * @param date
	 */
	public void setPaymentDate(Date date) {
		paymentDate = date;
	}

	/**
	 * @param string
	 */
	public void setUserId(String string) {
		userId = string;
	}

	/**
	 * @return
	 */
	public ArrayList getDanRemarks() {
		return danRemarks;
	}

	/**
	 * @param list
	 */
	public void setDanRemarks(ArrayList list) {
		danRemarks = list;
	}

	/**
	 * @return
	 */
	public ArrayList getPaymentDetails() {
		return paymentDetails;
	}

	/**
	 * @param list
	 */
	public void setPaymentDetails(ArrayList list) {
		paymentDetails = list;
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
	public Map getAppropriatedFlags() {
		return appropriatedFlags;
	}
  
 /**
   * 
   * @return depositedFlags
   */
  public Map getDepositedFlags() 
  {
   return this.depositedFlags;
  }
  /**
   * 
   * @param depositedFlags
   */
  public void setDepositedFlags(Map depositedFlags)
  {
   this.depositedFlags = depositedFlags;
  }
/**
   * 
   * @param key
   * @param value
   */
  public void setDepositedFlags(String key,Object value) 
  {
	depositedFlags.put(key,value);
  }
	/**
	 * @param map
	 */
	public void setAppropriatedFlags(Map map) {
		appropriatedFlags = map;
	}

	/**
	 * @return
	 */
	public Map getDanPanDetails() {
		return danPanDetails;
	}

	/**
	 * @param map
	 */
	public void setDanPanDetails(Map map) {
		danPanDetails = map;
	}
	/**
	 * @return
	 */
	public Object getDanPanDetail(String key) {
		return danPanDetails.get(key);
	}

	/**
	 * @param map
	 */
	public void setDanPanDetail(String key,Object value) {
		danPanDetails.put(key,value);
	}
	/**
	 * @return
	 */
	public ArrayList getInstruments() {
		return instruments;
	}

	/**
	 * @param list
	 */
	public void setInstruments(ArrayList list) {
		instruments = list;
	}

	public void resetWhenRequired()
	{
		modeOfPayment="";
		modeOfDelivery="";
		instrumentNo="";
		instrumentType="";
		instrumentDate=null;
		payableAt="";
		collectingBank="";
		collectingBankBranch="";
		cgtsiAccountHoldingBranch="";
		paymentDate=null;
		drawnAtBranch="";
		officerName="";
		drawnAtBank="";
		voucherDetails.clear();

		bankGLCode="";
		bankGLName="";
		deptCode="";
		amount=0;
		amountInFigure="";
		narration="";
		manager="";
		asstManager="";
		
		fromDate=null;
		toDate=null;
		dateType="N";
		
	}
	/**
	 * @return
	 */
	public Map getVoucherDetails() {
		return voucherDetails;
	}

	/**
	 * @param map
	 */
	public void setVoucherDetails(Map map) {
		voucherDetails = map;
	}

	public void setVoucherDetail(String key,Object value) {
		voucherDetails.put(key,value);
	}

	public Object getVoucherDetail(String key) {
		return voucherDetails.get(key);
	}

	/**
	 * @return
	 */
	public double getAmount() {
		return amount;
	}

	/**
	 * @return
	 */
	public String getAmountInFigure() {
		return amountInFigure;
	}

	/**
	 * @return
	 */
	public String getBankGLCode() {
		return bankGLCode;
	}

	/**
	 * @return
	 */
	public String getBankGLName() {
		return bankGLName;
	}

	/**
	 * @return
	 */
	public String getDeptCode() {
		return deptCode;
	}

	/**
	 * @return
	 */
	public String getNarration() {
		return narration;
	}

	/**
	 * @param d
	 */
	public void setAmount(double d) {
		amount = d;
	}

	/**
	 * @param string
	 */
	public void setAmountInFigure(String string) {
		amountInFigure = string;
	}

	/**
	 * @param string
	 */
	public void setBankGLCode(String string) {
		bankGLCode = string;
	}

	/**
	 * @param string
	 */
	public void setBankGLName(String string) {
		bankGLName = string;
	}

	/**
	 * @param string
	 */
	public void setDeptCode(String string) {
		deptCode = string;
	}

	/**
	 * @param string
	 */
	public void setNarration(String string) {
		narration = string;
	}

	/**
	 * @return
	 */
	public String getAsstManager() {
		return asstManager;
	}

	/**
	 * @return
	 */
	public String getManager() {
		return manager;
	}

	/**
	 * @param string
	 */
	public void setAsstManager(String string) {
		asstManager = string;
	}

	/**
	 * @param string
	 */
	public void setManager(String string) {
		manager = string;
	}

	public java.util.Date getFromDate()
	{
		return this.fromDate;
	}

	public void setFromDate(java.util.Date aDate)
	{
		this.fromDate = aDate;
	}

	public java.util.Date getToDate()
	{
		return this.toDate;
	}

	public void setToDate(java.util.Date aDate)
	{
		this.toDate = aDate;
	}

    public Vector getMliWiseDanDetails()
    {
		return this.mliWiseDanDetails;
	}

	public void setMliWiseDanDetails(Vector dtls)
	{
		this.mliWiseDanDetails = dtls;
	}

	public Vector getDateWiseDANDetails()
	{
		return this.dateWiseDANDetails;
	}

	public void setDateWiseDANDetails(Vector dtls)
	{
		this.dateWiseDANDetails = dtls;
	}
	/**
	 * @return
	 */
	public ArrayList getGlHeads() {
		return glHeads;
	}

	/**
	 * @param list
	 */
	public void setGlHeads(ArrayList list) {
		glHeads = list;
	}

	/**
	 * @return
	 */
	public Map getWaivedFlags() {
		return waivedFlags;
	}

	/**
	 * @param map
	 */
	public void setWaivedFlags(Map map) {
		waivedFlags = map;
	}

	/**
	 * @return
	 */
	public double getRefundAmount() {
		return refundAmount;
	}

	/**
	 * @param d
	 */
	public void setRefundAmount(double d) {
		refundAmount = d;
	}

	public ArrayList getAllocatedPanDetails() {
		return allocatedPanDetails;
	}
	
	/**
	 * @param list
	 */
	public void setAllocatedPanDetails(ArrayList list) {
		allocatedPanDetails = list;
	}

	/**
	 * @return
	 */
	public String getDateType() {
		return dateType;
	}

	/**
	 * @param string
	 */
	public void setDateType(String string) {
		dateType = string;
	}

	/**
	 * @return
	 */
	public ArrayList getPaymentList() {
		return paymentList;
	}

	/**
	 * @param list
	 */
	public void setPaymentList(ArrayList list) {
		paymentList = list;
	}
/**
 * @param map
 */
public void setAppropriatedFlag(String key,Object value) {
	appropriatedFlags.put(key,value);
}
	/**
	 * @return
	 */
	public String getTest() {
		return test;
	}

	/**
	 * @param string
	 */
	public void setTest(String string) {
		test = string;
	}

	/**
	 * @return
	 */
	public String getPayInSlipFormat() {
		return payInSlipFormat;
	}

	/**
	 * @param string
	 */
	public void setPayInSlipFormat(String string) {
		payInSlipFormat = string;
	}

	/**
	 * @return
	 */
	public ArrayList getGfCardRateList() {
		return gfCardRateList;
	}

	/**
	 * @param list
	 */
	public void setGfCardRateList(ArrayList list) {
		gfCardRateList = list;
	}

	/**
	 * @return
	 */
	public Map getGfRate() {
		return gfRate;
	}

	/**
	 * @return
	 */
	public Map getHighAmount() {
		return highAmount;
	}

	/**
	 * @return
	 */
	public Map getLowAmount() {
		return lowAmount;
	}

	/**
	 * @return
	 */
	public Map getRateId() {
		return rateId;
	}

	/**
	 * @param map
	 */
	public void setGfRate(Map map) {
		gfRate = map;
	}

	/**
	 * @param map
	 */
	public void setHighAmount(Map map) {
		highAmount = map;
	}

	/**
	 * @param map
	 */
	public void setLowAmount(Map map) {
		lowAmount = map;
	}

	/**
	 * @param map
	 */
	public void setRateId(Map map) {
		rateId = map;
	}

	/**
	 * @return
	 */
	public Map getNewDanIds() {
		return newDanIds;
	}

	/**
	 * @param map
	 */
	public void setNewDanIds(Map map) {
		newDanIds = map;
	}
  
  public String getAllocationType(){
    return this.allocationType;
  }
  
  public void setAllocationType(String allocationType){
   this.allocationType = allocationType;
  }


  public void setNewInstrumentNo(String newInstrumentNo)
  {
    this.newInstrumentNo = newInstrumentNo;
  }


  public String getNewInstrumentNo()
  {
    return newInstrumentNo;
  }


  public void setNewInstrumentDt(Date newInstrumentDt)
  {
    this.newInstrumentDt = newInstrumentDt;
  }


  public Date getNewInstrumentDt()
  {
    return newInstrumentDt;
  }


  public void setAccountName(String accountName)
  {
    this.accountName = accountName;
  }


  public String getAccountName()
  {
    return accountName;
  }


  public void setIfscCode(String ifscCode)
  {
    this.ifscCode = ifscCode;
  }


  public String getIfscCode()
  {
    return ifscCode;
  }


  public void setBankAccountNo(int bankAccountNo)
  {
    this.bankAccountNo = bankAccountNo;
  }


  public int getBankAccountNo()
  {
    return bankAccountNo;
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


  public void setBranchName(String branchName)
  {
    this.branchName = branchName;
  }


  public String getBranchName()
  {
    return branchName;
  }


  public void setComments(String comments)
  {
    this.comments = comments;
  }


  public String getComments()
  {
    return comments;
  }


  public void setAllocatedAmt(double allocatedAmt)
  {
    this.allocatedAmt = allocatedAmt;
  }


  public double getAllocatedAmt()
  {
    return allocatedAmt;
  }


  public void setNeftCode(String neftCode)
  {
    this.neftCode = neftCode;
  }


  public String getNeftCode()
  {
    return neftCode;
  }


    public void setAllocatedFlagKey(Map allocatedFlagKey) {
        this.allocatedFlagKey = allocatedFlagKey;
    }

    public Map getAllocatedFlagKey() {
        return allocatedFlagKey;
    }
    
    private Date dateOfTheDocument25;

    public void setDateOfTheDocument25(Date dateOfTheDocument25) {
        this.dateOfTheDocument25 = dateOfTheDocument25;
    }

    public Date getDateOfTheDocument25() {
        return dateOfTheDocument25;
    }
    
    public void setRealizationDates(Map realizationDates) {
        this.realizationDates = realizationDates;
    }

    public Map getRealizationDates() {
        return realizationDates;
    }
    public void setReceivedAmounts(Map receivedAmounts) {
        this.receivedAmounts = receivedAmounts;
    }

    public Map getReceivedAmounts() {
        return receivedAmounts;
    }
    public void setPayIds(Map payIds) {
        this.payIds = payIds;
    }

    public Map getPayIds() {
        return payIds;
    }
    
}


