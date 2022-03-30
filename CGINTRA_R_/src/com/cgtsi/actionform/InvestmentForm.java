package com.cgtsi.actionform;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

//import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;


import org.apache.struts.validator.ValidatorActionForm;

import com.cgtsi.common.Log;
import com.cgtsi.investmentfund.FundTransferDetail;
import com.cgtsi.investmentfund.InvestmentMaturityDetails;
import com.cgtsi.investmentfund.MiscReceipts;
import com.cgtsi.util.DateHelper;




/**
 * @author NP13031
 *
 * Form for Budget Details. Displays the budget heads and sub heads dynamically.
 */
public class InvestmentForm extends ValidatorActionForm {
	private Map heads=new HashMap();
	private Map subHeads=new HashMap();

	private Map shortHeads=new HashMap();
	private Map shortSubHeads=new HashMap();
	
	private Map headsToRender=new HashMap();
	private Map subHeadsToRender=new HashMap();

	private String annualOrShortTerm="";
	private String inflowOrOutflow="";
	private String month="";
	private String year="";
	private String annualFromDate="";
	private String annualToDate="";
	private String startDate	= "";
	private String endDate		= "";
	private String dateOfFlow	= "";
	private Date miscReceiptsDate;
	private int counter;
	private Date statementDate;
	
	private Map fundTransfers = new TreeMap();
	
	private Map miscReceipts = new HashMap();

	private final Map values = new HashMap();
	
	private ArrayList bnkChqDetails = new ArrayList();
	
	private ArrayList unutilizedChqDetails = new ArrayList();
	
	private String test="";
	
	private String bankBranchName ;
	
	private Map chequeId  = new TreeMap();  
	private Map startNo  = new TreeMap();
	private Map endNo  = new TreeMap();
	private Map bankId  = new TreeMap();
	
	private Map cancelledChq  = new TreeMap();

	private Map tempStartNo  = new TreeMap();
	private Map tempEndNo  = new TreeMap();
	
	private Date valueDate;
	private Map invstMaturingDetails = new TreeMap();
	
	private Map planInvMainDetails = new TreeMap();
	private ArrayList planInvMatDetails = new ArrayList();
	private ArrayList planInvFTDetails = new ArrayList();
	private ArrayList planInvProvisionDetails = new ArrayList();
	
	private Map provisionRemarks = new TreeMap();
	
	private Date dateOfTheDocument2;
	private Date dateOfTheDocument3;
	private ArrayList chequeArray = new ArrayList();
	private String chqId = "";
  
  private Date depositDate;
  

	public void setValue(String key, Object value) 
	{
		//System.out.println("inside INVESTMENT FORM - setValue");
		values.put(key, value); 
	}

	public Object getValue(String key){
		//System.out.println("inside INVESTMENT FORM - getValue");
		return values.get(key); 
	}
	
	public Map getValues() {
		//System.out.println("inside INVESTMENT FORM - getValues");
		return values; 
	}

	public void setHead(String key, Object value)
	{
		//System.out.println("inside INVESTMENT FORM - setHead");
		heads.put(key,value);
	}

	public Object getHead(String key)
	{
		//System.out.println("Key "+key+" , "+heads.get(key));
		return heads.get(key); 
	}

	public Map getHeads()
	{
		//System.out.println("inside Investment form-getHeads"+heads);
		return heads;
	}

	public void setSubHead(String key, Object value)
	{
		//System.out.println("inside Investment form-setSubHead");
		//System.out.println("Key = "+key);
		//System.out.println("value = "+(String)value);
		subHeads.put(key,value);
	}

	public Object getSubHead(String key)
	{
		//System.out.println("inside Investment form-getSubHead(key)");
		return subHeads.get(key); 
	}

	public Map getSubHeads()
	{
		//System.out.println("inside Investment form-getSubHeads()");
		return subHeads;
	}

//herh 

	public void setHeadsToRender(String key, Object value)
	{
		headsToRender.put(key,value);
	}

	public Object getHeadsToRender(String key)
	{
		return headsToRender.get(key); 
	}

	public Map getHeadsToRender()
	{
		return headsToRender;
	}

	public void setSubHeadsToRender(String key, Object value)
	{
		subHeadsToRender.put(key,value);
	}

	public Object getSubHeadsToRender(String key)
	{
		return subHeadsToRender.get(key); 
	}

	public Map getSubHeadsToRender()
	{
		return subHeadsToRender;
	}

//here end
	public void setAnnualOrShortTerm(String aAnnualOrShortTerm)
	{
		annualOrShortTerm=aAnnualOrShortTerm;
	}

	public String getAnnualOrShortTerm()
	{
		return annualOrShortTerm;
	}

	public void setInflowOrOutflow(String aInflowOrOutflow)
	{
		inflowOrOutflow=aInflowOrOutflow;
	}

	public String getInflowOrOutflow()
	{
		return inflowOrOutflow;
	}

	public void setMonth(String aMonth)
	{
		month=aMonth;
	}

	public String getMonth()
	{
		return month;
	}

	public void setYear(String aYear)
	{
		year=aYear;
	}

	public String getYear()
	{
		return year;
	}

	public void setAnnualFromDate(String aAnnualFromDate)
	{
		//System.out.println("inside Investment form#");
		annualFromDate=aAnnualFromDate;
	}

	public String getAnnualFromDate()
	{
		//System.out.println("Getting annual From date "+annualFromDate);
		return annualFromDate;
	}

	public void setAnnualToDate(String aAnnualToDate)
	{
		annualToDate=aAnnualToDate;
	}

	public String getAnnualToDate()
	{
		return annualToDate;
	}


	public void setStartDate(String aStartDate)
	{
		startDate=aStartDate;
	}

	public String getStartDate()
	{
		return startDate;
	}

	public void setEndDate(String aEndDate)
	{
		endDate=aEndDate;
	}

	public String getEndDate()
	{
		return endDate;
	}

	public void setDateOfFlow(String aDateOfFlow)
	{
		dateOfFlow=aDateOfFlow;
	}

	public String getDateOfFlow()
	{
		return dateOfFlow;
	}
	public Object getHeadsMapped(String key) { 
        return getHead(key);
    }
    
    public void setHeadsMapped(String key, Object value) { 
        setHead(key, value); 
    }

	public Object getSubHeadsMapped(String key) { 
        return getSubHead(key);
    }
    
    public void setSubHeadsMapped(String key, Object value) { 
        setSubHead(key, value); 
    }

	public String getHeadsRam()
	{
		StringBuffer sb = new StringBuffer();
		if ((heads!=null && !heads.isEmpty())) {
			Iterator it = heads.keySet().iterator();
			while (it.hasNext()) {
				String paramName = (String)it.next();
				//String paramValue = (String)heads.get(paramName);
				sb.append(paramName);
				//sb.append(paramValue);
			}
		}
		//System.out.println("heads "+heads); 
		return sb.toString();
	}

	public String getSubHeadsRam()
	{
		StringBuffer sb = new StringBuffer();
		if ((subHeads!=null && !subHeads.isEmpty())) {
			Iterator it = subHeads.keySet().iterator();
			while (it.hasNext()) {
				String paramName = (String)it.next();
				String paramValue = (String)subHeads.get(paramName);
				sb.append(paramName);
				sb.append(paramValue);
			}
		} 
		//System.out.println("subHeads "+subHeads);
		return sb.toString();
	}

	/*public String getQuestions()
	{
		StringBuffer sb = new StringBuffer();
		if ((values!=null && !values.isEmpty())) {
			Iterator it = values.keySet().iterator();
			while (it.hasNext()) {
				String paramName = (String)it.next();
				String paramValue = (String)values.get(paramName);
				sb.append(paramName);
				sb.append(paramValue);
			}
		} 
		return sb.toString();
	}*/


		/* (non-Javadoc)
		 * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
		 */
		public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)
		{
			//System.out.println("Validation...");
			Log.log(Log.INFO,"InvestmentForm","validate","Entered");
			
			//HttpSession session=request.getSession(false);
			//InvestmentForm investmentForm=(InvestmentForm)session.getAttribute("investmentForm");
			
			//System.out.println("Annual from date "+investmentForm.getAnnualFromDate());
			//System.out.println("Annual from date from request "+request.getParameter("annualFromDate"));
			
			ActionErrors errors=super.validate(mapping, request);
			

			if(mapping.getPath().equals("/setBudgetInflowDetails") 
			|| mapping.getPath().equals("/setBudgetOutflowDetails") 
			|| mapping.getPath().equals("/setAnnualFundsInflowDetails")
			|| mapping.getPath().equals("/setAnnualFundsOutflowDetails") )
			{
				Log.log(Log.DEBUG,"InvestmentForm","validate","Validating set budget inflow details ");
				
				if(errors.isEmpty())
				{
					Log.log(Log.DEBUG,"InvestmentForm","validate","No Errors from super class");
					
					boolean returnValue=false;
					boolean returnValue1=false;
					
					if(mapping.getPath().equals("/setAnnualFundsInflowDetails")
					|| mapping.getPath().equals("/setAnnualFundsOutflowDetails") )
					{
						returnValue=checkValidDate(getDateOfFlow());
						returnValue1=true;
					}
					else
					{
						returnValue=checkValidDate(getAnnualFromDate());
						
						returnValue1=checkValidDate(getAnnualToDate());
					}
					
					if(!returnValue || !returnValue1 )
					{
						errors.add(ActionErrors.GLOBAL_ERROR,new ActionError("InvalidYear"));
					}
				}
				
				Set headSet=heads.keySet();
				
				Iterator iterator=headSet.iterator();
				int counter=0;				
				while(iterator.hasNext())
				{
					String key=(String)iterator.next();
					
					String value=(String)heads.get(key);
					Log.log(Log.DEBUG,"InvestmentForm","validate","Head key and value "+key+","+value);
					
					if(value==null || value.equals(""))
					{
						Log.log(Log.DEBUG,"InvestmentForm","validate","value is null or empty ");
							
					}
					else
					{
						counter++;
					}
				}
				
				Set subHeadSet=subHeads.keySet();
				Iterator subHeadIterator=subHeadSet.iterator();
				
				while(subHeadIterator.hasNext())
				{
					String key=(String)subHeadIterator.next();
					
					String value=(String)subHeads.get(key);
					
					Log.log(Log.DEBUG,"InvestmentForm","validate","Sub Head key and value "+key+","+value);
					
					if(value==null || value.equals(""))
					{
						Log.log(Log.DEBUG,"InvestmentForm","validate","value is null or empty ");
							
					}
					else
					{
						Log.log(Log.DEBUG,"InvestmentForm","validate","value are available. increment the counter");
						counter++;
					}										
				}
				
				Log.log(Log.DEBUG,"InvestmentForm","validate","Counter value "+counter);
				
				Log.log(Log.DEBUG,"InvestmentForm","validate","size of heads and sub heads "+heads.size()+", "+subHeads.size());
				
				if(counter==0)
				{
					//No values entered.
					ActionError error=new ActionError("oneValueRequired");
					errors.add(ActionErrors.GLOBAL_ERROR,error);	
				}
			}
			else if(mapping.getPath().equals("/setShortTermBudgetInflowDetails") || mapping.getPath().equals("/setShortTermBudgetOutflowDetails") )
			{
				Log.log(Log.DEBUG,"InvestmentForm","validate","Validating set short term budget inflow details ");
				
				Set headSet=shortHeads.keySet();
				
				Iterator iterator=headSet.iterator();
				int counter=0;				
				while(iterator.hasNext())
				{
					String key=(String)iterator.next();
					
					String value=(String)shortHeads.get(key);
					Log.log(Log.DEBUG,"InvestmentForm","validate","Head key and value "+key+","+value);
					
					if(value==null || value.equals(""))
					{
						Log.log(Log.DEBUG,"InvestmentForm","validate","value is null or empty ");
							
					}
					else
					{
						counter++;
					}
				}
				
				Set subHeadSet=shortSubHeads.keySet();
				Iterator subHeadIterator=subHeadSet.iterator();
				
				while(subHeadIterator.hasNext())
				{
					String key=(String)subHeadIterator.next();
					
					String value=(String)shortSubHeads.get(key);
					
					Log.log(Log.DEBUG,"InvestmentForm","validate","Sub Head key and value "+key+","+value);
					
					if(value==null || value.equals(""))
					{
						Log.log(Log.DEBUG,"InvestmentForm","validate","value is null or empty ");
							
					}
					else
					{
						Log.log(Log.DEBUG,"InvestmentForm","validate","value are available. increment the counter");
						counter++;
					}										
				}
				
				Log.log(Log.DEBUG,"InvestmentForm","validate","Counter value "+counter);
				
				Log.log(Log.DEBUG,"InvestmentForm","validate","size of heads and sub heads "+heads.size()+", "+subHeads.size());
				
				if(counter==0)
				{
					//No values entered.
					ActionError error=new ActionError("oneValueRequired");
					errors.add(ActionErrors.GLOBAL_ERROR,error);	
				}
			}
			else if(mapping.getPath().equals("/addMoreMiscReceipts") ||
			mapping.getPath().equals("/insertMiscReceipts") )
			{
				Log.log(Log.DEBUG,"InvestmentForm","validate"," Misc Receipts");
				Set miscReceiptsSet = miscReceipts.keySet();
				Iterator miscReceiptsIterator = miscReceiptsSet.iterator();
				boolean source = false;
				boolean instDate = false;
				boolean instNo = false;
				boolean amt = false;
				boolean receiptDate = false;
				
				while(miscReceiptsIterator.hasNext())
				{
					String key=(String)miscReceiptsIterator.next();
					Log.log(Log.DEBUG,"InvestmentForm","validate"," key "+key);

					MiscReceipts receipts=(MiscReceipts)miscReceipts.get(key);
					
					boolean sourceVal=true;
					boolean instDateVal=true;
					boolean instNoVal=true;
					boolean amtVal=true;
					boolean rectDateVal=true;
					
					if (receipts.getSourceOfFund()==null || receipts.getSourceOfFund().equals(""))
					{
						sourceVal=false;
					}
					if (receipts.getInstrumentDate()==null || receipts.getInstrumentDate().equals(""))
					{
						instDateVal=false;
					}
					if (receipts.getInstrumentNo()==null || receipts.getInstrumentNo().equals(""))
					{
						instNoVal=false;
					}
					if (receipts.getAmount()==null || receipts.getAmount().equals("") || Double.parseDouble(receipts.getAmount())==0)
					{
						amtVal=false;
					}
					if (receipts.getDateOfReceipt()==null || receipts.getDateOfReceipt().equals(""))
					{
						rectDateVal=false;
					}

/*					if(!source && (receipts.getId()!=0 || (receipts.getId()==0 && (instDateVal || instNoVal || amtVal || rectDateVal))) && !sourceVal)
					{
						ActionError error=new ActionError("errors.required","Source of Fund");
						errors.add(ActionErrors.GLOBAL_ERROR,error);
						source=true;
						Log.log(Log.DEBUG,"InvestmentForm","validate"," source is null ");						
					}
					
					if(!instDate && (receipts.getId()!=0 || (receipts.getId()==0 && (sourceVal || instNoVal || amtVal || rectDateVal))) && !instDateVal)
					{
						ActionError error=new ActionError("errors.required","Instrument Date ");
						errors.add(ActionErrors.GLOBAL_ERROR,error);
						Log.log(Log.DEBUG,"InvestmentForm","validate"," Instrument date is null ");
						instDate=true;
					}
					else if (instDateVal)
					{
						String instrumentDate=receipts.getInstrumentDate();

						if(instrumentDate.trim().length()<10)
						{
							String [] errorStrs=new String[2];
							errorStrs[0]="Instrument Date ";
							errorStrs[1]="10";

							ActionError error=new ActionError("errors.minlength",errorStrs);

							errors.add(ActionErrors.GLOBAL_ERROR,error);
							instDate=true;

							Log.log(Log.DEBUG,"InvestmentForm","validate"," length is less than zero");
						}
						else
						{
							SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");

							java.util.Date date=dateFormat.parse(instrumentDate, new ParsePosition(0));
							Log.log(Log.DEBUG,"InvestmentForm","validate"," date "+date);

							if(date==null)
							{
								ActionError error=new ActionError("errors.date","Instrument Date");

								errors.add(ActionErrors.GLOBAL_ERROR,error);
								instDate=true;
							}
							else if (date.after(miscReceiptsDate))
							{
								ActionError error=new ActionError("instDtGTReceiptDt");
								errors.add(ActionErrors.GLOBAL_ERROR,error);
								instDate=true; 
							}
						}
					}
					
					if(!instNo && (receipts.getId()!=0 || (receipts.getId()==0 && (instDateVal || sourceVal || amtVal || rectDateVal))) && !instNoVal)
					{
						ActionError error=new ActionError("errors.required","Instrument Number");
						errors.add(ActionErrors.GLOBAL_ERROR,error);
						instNo=true;
						Log.log(Log.DEBUG,"InvestmentForm","validate"," inst no is null ");
					}
					
					if(!amt && (receipts.getId()!=0 || (receipts.getId()==0 && (instDateVal || instNoVal || sourceVal || rectDateVal))) && !amtVal)
					{
						ActionError error=new ActionError("errors.required","Amount");
						errors.add(ActionErrors.GLOBAL_ERROR,error);
						Log.log(Log.DEBUG,"InvestmentForm","validate"," Amount in Rs ");

						amt=true;
					}
					
					if(!receiptDate && (receipts.getId()!=0 || (receipts.getId()==0 && (instDateVal || instNoVal || amtVal || sourceVal))) && !rectDateVal)
					{
						ActionError error=new ActionError("errors.required","Date of Receipt ");
						errors.add(ActionErrors.GLOBAL_ERROR,error);
						Log.log(Log.DEBUG,"InvestmentForm","validate"," Instrument date is null ");
						receiptDate=true;
					}
					else if (rectDateVal)
					{
						String instrumentDate=receipts.getDateOfReceipt();

						if(instrumentDate.trim().length()<10)
						{
							String [] errorStrs=new String[2];
							errorStrs[0]="Date of Receipt";
							errorStrs[1]="10";

							ActionError error=new ActionError("errors.minlength",errorStrs);

							errors.add(ActionErrors.GLOBAL_ERROR,error);
							receiptDate=true;

							Log.log(Log.DEBUG,"InvestmentForm","validate"," length is less than zero");
						}
						else
						{
							SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");

							java.util.Date date=dateFormat.parse(instrumentDate, new ParsePosition(0));
							Log.log(Log.DEBUG,"InvestmentForm","validate"," date "+date);

							if(date==null)
							{
								ActionError error=new ActionError("errors.date","Date of Receipt");

								errors.add(ActionErrors.GLOBAL_ERROR,error);
								receiptDate=true;
							}
							else if (date.after(miscReceiptsDate))
							{
								ActionError error=new ActionError("miscDtGTReceiptDt");
								errors.add(ActionErrors.GLOBAL_ERROR,error);
								receiptDate=true; 
							}

						}
					}

					if (!instDate && ! receiptDate)
					{
						SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
						
						String receiptDateStr=receipts.getDateOfReceipt();
						String instrumentDateStr=receipts.getInstrumentDate();
						
						if (receiptDateStr!=null && instrumentDateStr!=null)
						{
							java.util.Date receiptDateDt=dateFormat.parse(receiptDateStr, new ParsePosition(0));
							java.util.Date instDateDt=dateFormat.parse(instrumentDateStr, new ParsePosition(0));
							
							Log.log(Log.DEBUG,"InvestmentForm","validate"," instdate " + instDateDt);
							Log.log(Log.DEBUG,"InvestmentForm","validate"," rectdate " + receiptDateDt);
							
							if ((instDateDt!=null && receiptDateDt!=null) && instDateDt.after(receiptDateDt))
							{
								ActionError actionMessage=new ActionError("receiptDtGTInstDt");
								errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
								instDate=true;
								receiptDate=true;
							}
						}
					}*/
					if(!source && !sourceVal)
					{
						ActionError error=new ActionError("errors.required","Source of Fund");
						errors.add(ActionErrors.GLOBAL_ERROR,error);
						source=true;
						Log.log(Log.DEBUG,"InvestmentForm","validate"," source is null ");						
					}

					if(!instDate && !instDateVal)
					{
						ActionError error=new ActionError("errors.required","Instrument Date ");
						errors.add(ActionErrors.GLOBAL_ERROR,error);
						Log.log(Log.DEBUG,"InvestmentForm","validate"," Instrument date is null ");
						instDate=true;
					}
					else if (instDateVal && !instDate)
					{
						String instrumentDate=receipts.getInstrumentDate();

						if(instrumentDate.trim().length()<10)
						{
							String [] errorStrs=new String[2];
							errorStrs[0]="Instrument Date ";
							errorStrs[1]="10";

							ActionError error=new ActionError("errors.minlength",errorStrs);

							errors.add(ActionErrors.GLOBAL_ERROR,error);
							instDate=true;

							Log.log(Log.DEBUG,"InvestmentForm","validate"," length is less than zero");
						}
						else
						{
							SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");

							java.util.Date date=dateFormat.parse(instrumentDate, new ParsePosition(0));
							Log.log(Log.DEBUG,"InvestmentForm","validate"," date "+date);

							if(date==null)
							{
								ActionError error=new ActionError("errors.date","Instrument Date");

								errors.add(ActionErrors.GLOBAL_ERROR,error);
								instDate=true;
							}
							else if (date.after(miscReceiptsDate))
							{
								ActionError error=new ActionError("instDtGTReceiptDt");
								errors.add(ActionErrors.GLOBAL_ERROR,error);
								instDate=true; 
							}
						}
					}

					if(!instNo && !instNoVal)
					{
						ActionError error=new ActionError("errors.required","Instrument Number");
						errors.add(ActionErrors.GLOBAL_ERROR,error);
						instNo=true;
						Log.log(Log.DEBUG,"InvestmentForm","validate"," inst no is null ");
					}

					if(!amt && !amtVal)
					{
						ActionError error=new ActionError("errors.required","Amount");
						errors.add(ActionErrors.GLOBAL_ERROR,error);
						Log.log(Log.DEBUG,"InvestmentForm","validate"," Amount in Rs ");

						amt=true;
					}

					if(!receiptDate && !rectDateVal)
					{
						ActionError error=new ActionError("errors.required","Date of Receipt ");
						errors.add(ActionErrors.GLOBAL_ERROR,error);
						Log.log(Log.DEBUG,"InvestmentForm","validate"," Instrument date is null ");
						receiptDate=true;
					}
					else if (rectDateVal)
					{
						String instrumentDate=receipts.getDateOfReceipt();

						if(instrumentDate.trim().length()<10)
						{
							String [] errorStrs=new String[2];
							errorStrs[0]="Date of Receipt";
							errorStrs[1]="10";

							ActionError error=new ActionError("errors.minlength",errorStrs);

							errors.add(ActionErrors.GLOBAL_ERROR,error);
							receiptDate=true;

							Log.log(Log.DEBUG,"InvestmentForm","validate"," length is less than zero");
						}
						else
						{
							SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");

							java.util.Date date=dateFormat.parse(instrumentDate, new ParsePosition(0));
							Log.log(Log.DEBUG,"InvestmentForm","validate"," date "+date);

							if(date==null)
							{
								ActionError error=new ActionError("errors.date","Date of Receipt");

								errors.add(ActionErrors.GLOBAL_ERROR,error);
								receiptDate=true;
							}
							else if (date.after(miscReceiptsDate))
							{
								ActionError error=new ActionError("miscDtGTReceiptDt");
								errors.add(ActionErrors.GLOBAL_ERROR,error);
								receiptDate=true; 
							}

						}
					}

					if (!instDate && ! receiptDate)
					{
						SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
	
						String receiptDateStr=receipts.getDateOfReceipt();
						String instrumentDateStr=receipts.getInstrumentDate();
	
						if (receiptDateStr!=null && instrumentDateStr!=null)
						{
							java.util.Date receiptDateDt=dateFormat.parse(receiptDateStr, new ParsePosition(0));
							java.util.Date instDateDt=dateFormat.parse(instrumentDateStr, new ParsePosition(0));
		
							Log.log(Log.DEBUG,"InvestmentForm","validate"," instdate " + instDateDt);
							Log.log(Log.DEBUG,"InvestmentForm","validate"," rectdate " + receiptDateDt);
		
							if ((instDateDt!=null && receiptDateDt!=null) && instDateDt.after(receiptDateDt))
							{
								ActionError actionMessage=new ActionError("receiptDtGTInstDt");
								errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
								instDate=true;
								receiptDate=true;
							}
						}
					}
					
					if((!source && !instDate && !instNo && !amt && !receiptDate) || ((source && instDate && instNo && amt && receiptDate)  &&  receipts.getId()==0))
					{
						continue;
					}
				}
				if (mapping.getPath().equals("/insertMiscReceipts") && miscReceipts.size()==1)
				{
					MiscReceipts misc = (MiscReceipts)miscReceipts.get("key-0");
					if (misc.getId()==0 && (misc.getSourceOfFund()==null || misc.getSourceOfFund().equals("")) &&
					(misc.getInstrumentNo()==null || misc.getInstrumentNo().equals("")) &&
					(misc.getInstrumentDate()==null || misc.getInstrumentDate().toString().equals("")) &&
					(misc.getDateOfReceipt()==null || misc.getDateOfReceipt().toString().equals("")))
					{
						ActionError actionMessage=new ActionError("atleastOneReceipt");
						errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
					}
				}
			}
			else if(mapping.getPath().equals("/updateFundTransfer"))
			{
				Log.log(Log.DEBUG,"InvestmentForm","validate"," Fund Transfer");
				Set ftSet = fundTransfers.keySet();
				Iterator ftIterator = ftSet.iterator();
				boolean closeBalDate = false;
				boolean balanceStmt = false;
				boolean balanceUncleared = false;
				boolean amtCA = false;
				boolean remarks = false;
				
				while(ftIterator.hasNext())
				{
					String key=(String)ftIterator.next();
					Log.log(Log.DEBUG,"InvestmentForm","validate"," key "+key);

					FundTransferDetail ftDetail=(FundTransferDetail)fundTransfers.get(key);
					
					boolean closeBalDateVal=true;
					boolean balanceStmtVal=true;
					boolean balanceUnclearedVal=true;
					boolean amtCAVal=true;
					boolean remarksVal=true;
					
					if (ftDetail.getClosingBalanceDate()==null || ftDetail.getClosingBalanceDate().equals(""))
					{
						closeBalDateVal=false;
					}
					if (ftDetail.getBalanceAsPerStmt()==null || ftDetail.getBalanceAsPerStmt().equals("") || Double.parseDouble(ftDetail.getBalanceAsPerStmt())==0)
					{
						balanceStmtVal=false;
					}
					if (ftDetail.getUnclearedBalance()==null || ftDetail.getUnclearedBalance().equals("")
						// || Double.parseDouble(ftDetail.getUnclearedBalance())==0
						)
					{
						balanceUnclearedVal=false;
					}
					if (ftDetail.getAmtCANotReflected()==null || ftDetail.getAmtCANotReflected().equals("")
						// || Double.parseDouble(ftDetail.getAmtCANotReflected())==0
						)
					{
						amtCAVal=false;
					}
					if (ftDetail.getRemarks()==null || ftDetail.getRemarks().equals(""))
					{
						remarksVal=false;
					}

					if(!closeBalDate && (ftDetail.getId()!=0 || (ftDetail.getId()==0 && (balanceStmtVal || balanceUnclearedVal || amtCAVal || remarksVal))) && !closeBalDateVal)
					{
						ActionError error=new ActionError("errors.required","Closing Balance Date");
						errors.add(ActionErrors.GLOBAL_ERROR,error);
						closeBalDate=true;
						Log.log(Log.DEBUG,"InvestmentForm","validate"," Closing Balance Date is null ");						
					}
					else if (closeBalDateVal && !closeBalDate)
					{
						String clDate=ftDetail.getClosingBalanceDate();

						if(clDate.trim().length()<10)
						{
							String [] errorStrs=new String[2];
							errorStrs[0]="Closing Balance Date ";
							errorStrs[1]="10";

							ActionError error=new ActionError("errors.minlength",errorStrs);

							errors.add(ActionErrors.GLOBAL_ERROR,error);
							closeBalDate=true;

							Log.log(Log.DEBUG,"InvestmentForm","validate"," length is less than zero");
						}
						else
						{
							SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");

							java.util.Date date=dateFormat.parse(clDate, new ParsePosition(0));
							Log.log(Log.DEBUG,"InvestmentForm","validate"," date "+date);

							if(date==null)
							{
								ActionError error=new ActionError("errors.date","Closing Balance Date");

								errors.add(ActionErrors.GLOBAL_ERROR,error);
								closeBalDate=true;
							}
							else if (date.after(statementDate))
							{
								ActionError error=new ActionError("closingBalDtGTStmtDt");
								errors.add(ActionErrors.GLOBAL_ERROR,error);
								closeBalDate=true; 
							}
						}
					}
										
					if(!balanceStmt && (ftDetail.getId()!=0 || (ftDetail.getId()==0 && (closeBalDateVal || balanceUnclearedVal || amtCAVal || remarksVal))) && !balanceStmtVal)
					{
						ActionError error=new ActionError("errors.required","Balance as per Statement ");
						errors.add(ActionErrors.GLOBAL_ERROR,error);
						Log.log(Log.DEBUG,"InvestmentForm","validate"," Balance as per Statement is null ");
						balanceStmt=true;
					}
					
					if(!balanceUncleared && (ftDetail.getId()!=0 || (ftDetail.getId()==0 && (closeBalDateVal || balanceStmtVal || amtCAVal || remarksVal))) && !balanceUnclearedVal)
					{
						ActionError error=new ActionError("errors.required","Credit with Future Value Date");
						errors.add(ActionErrors.GLOBAL_ERROR,error);
						balanceUncleared=true;
						Log.log(Log.DEBUG,"InvestmentForm","validate"," Credit with Future Value Date is null ");
					}
					
					if(!amtCA && (ftDetail.getId()!=0 || (ftDetail.getId()==0 && (closeBalDateVal || balanceStmtVal || balanceUnclearedVal || remarksVal))) && !amtCAVal)
					{
						ActionError error=new ActionError("errors.required","Amount transferred to CA not reflected");
						errors.add(ActionErrors.GLOBAL_ERROR,error);
						Log.log(Log.DEBUG,"InvestmentForm","validate"," Amount transferred to CA not reflected is null");

						amtCA=true;
					}

/*					if (!balanceStmt && !balanceUncleared && !amtCA)
					{
						double stmtBal = Double.parseDouble(ftDetail.getBalanceAsPerStmt());
						double unclBal = Double.parseDouble(ftDetail.getUnclearedBalance());
						double amtCaVal = Double.parseDouble(ftDetail.getAmtCANotReflected());
						double minBalVal = Double.parseDouble(ftDetail.getMinBalance());
						
						if (unclBal > stmtBal)
						{
							ActionError error=new ActionError("stmtBalGTunclBal");
							errors.add(ActionErrors.GLOBAL_ERROR,error);
							Log.log(Log.DEBUG,"RPActionForm","validate","uncleared balance > statement balance");
							
							balanceStmt=true;
							balanceUncleared=true;
						}
						
						double utilBal = stmtBal - unclBal;
						
						if (minBalVal > utilBal)
						{
							ActionError error=new ActionError("utilBalGTminBal");
							errors.add(ActionErrors.GLOBAL_ERROR,error);
							Log.log(Log.DEBUG,"RPActionForm","validate","minimum balance > balance for utilization");
							
							balanceStmt=true;
							balanceUncleared=true;
						}
						
						double amtIdbi = utilBal - minBalVal - amtCaVal;
						if (amtIdbi < 0)
						{
							ActionError error=new ActionError("utilBalGTminBal");
							errors.add(ActionErrors.GLOBAL_ERROR,error);
							Log.log(Log.DEBUG,"RPActionForm","validate","minimum balance > balance for utilization");
							
							amtCA=true;
						}
					}*/
					
					if(!remarks && (ftDetail.getId()!=0 || (ftDetail.getId()==0 && (closeBalDateVal || balanceStmtVal || balanceUnclearedVal || amtCAVal))) && !remarksVal)
					{
						ActionError error=new ActionError("errors.required","Remarks ");
						errors.add(ActionErrors.GLOBAL_ERROR,error);
						Log.log(Log.DEBUG,"InvestmentForm","validate"," Remarks is null ");
						remarks=true;
					}

					if((!closeBalDate && !balanceStmt && !balanceUncleared && !amtCA && !remarks) || ((closeBalDate && balanceStmt && balanceUncleared && amtCA && remarks)  &&  ftDetail.getId()==0))
					{
						continue;
					}
				}
				ftIterator=ftSet.iterator();
				int notPresentCount=0;
				while(ftIterator.hasNext())
				{
					String key = (String)ftIterator.next();
					FundTransferDetail fundTransfer = (FundTransferDetail)fundTransfers.get(key);
					if (fundTransfer.getId()==0 && (fundTransfer.getClosingBalanceDate()==null || fundTransfer.getClosingBalanceDate().equals("")) &&
						(fundTransfer.getBalanceAsPerStmt()==null || fundTransfer.getBalanceAsPerStmt().equals("") || Double.parseDouble(fundTransfer.getBalanceAsPerStmt())==0) &&
						(fundTransfer.getUnclearedBalance()==null || fundTransfer.getUnclearedBalance().equals("") || Double.parseDouble(fundTransfer.getUnclearedBalance())==0) &&
						(fundTransfer.getAmtCANotReflected()==null || fundTransfer.getAmtCANotReflected().equals("") || Double.parseDouble(fundTransfer.getAmtCANotReflected())==0))
					{
						notPresentCount++;
					}
				}
				if (notPresentCount==fundTransfers.size())
				{
					ActionError actionMessage=new ActionError("atleastOneFundTransfer");
					errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
				}
			}
			else if(mapping.getPath().equals("/showInflowOutflowReport"))
			{
				Log.log(Log.DEBUG,"InvestmentForm","validate"," Plan Investment ");
				Set imSet = invstMaturingDetails.keySet();
				Iterator imIterator = imSet.iterator();
				boolean matuirtyAmt = false;
				boolean otherDesc = false;
				
				while(imIterator.hasNext())
				{
					String key=(String)imIterator.next();
					Log.log(Log.DEBUG,"InvestmentForm","validate"," key "+key);

					InvestmentMaturityDetails imDetail=(InvestmentMaturityDetails)invstMaturingDetails.get(key);
					
					boolean matAmtVal=true;
					boolean otherDescVal=true;
					boolean invNameVal = true;
					
					if (imDetail.getInvName()==null || imDetail.getInvName().equals(""))
					{
						invNameVal = false;
					}
					if (imDetail.getOtherDesc()==null || imDetail.getOtherDesc().equals(""))
					{
						otherDescVal=false;
					}
					
					if (imDetail.getMaturityAmt()==null || imDetail.getMaturityAmt().equals("") || Double.parseDouble(imDetail.getMaturityAmt())==0)
					{
						matAmtVal=false;
					}

					if(!otherDesc && (imDetail.getPliId()!=0 || (imDetail.getPliId()==0 && matAmtVal)) && !otherDescVal && matAmtVal && !invNameVal)
					{
						ActionError error=new ActionError("errors.required","Description ");
						errors.add(ActionErrors.GLOBAL_ERROR,error);
						otherDesc=true;
						Log.log(Log.DEBUG,"InvestmentForm","validate"," Description is null ");
					}

					if(!matuirtyAmt && !matAmtVal && (invNameVal || otherDescVal))
					{
						ActionError error=new ActionError("errors.required","Maturity Amount ");
						errors.add(ActionErrors.GLOBAL_ERROR,error);
						Log.log(Log.DEBUG,"InvestmentForm","validate"," Maturity Amount is null ");
						matuirtyAmt=true;
					}
				}
/*				imIterator=imSet.iterator();
				int notPresentCount=0;
				while(imIterator.hasNext())
				{
					String key = (String)imIterator.next();
					InvestmentMaturityDetails imDetail=(InvestmentMaturityDetails)invstMaturingDetails.get(key);
					if (imDetail.getPliId()==0 && (imDetail.getInvName()==null || imDetail.getInvName().equals("")) &&
						(imDetail.getMaturityAmt()==null || imDetail.getMaturityAmt().equals("") || Double.parseDouble(imDetail.getMaturityAmt())==0) &&
						(imDetail.getOtherDesc()==null || imDetail.getOtherDesc().equals("")))
					{
						notPresentCount++;
					}
				}
				if (notPresentCount==invstMaturingDetails.size())
				{
					ActionError actionMessage=new ActionError("atleastOneMatDetails");
					errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
				}*/
			}
			

			Log.log(Log.INFO,"InvestmentForm","validate","Exited");			
			return errors;
			//ActionErrors errors=new ActionErrors();
			
			//System.out.println("***************Validation in ActionForm ***************");
			/*
			if(errors==null || errors.isEmpty())
			{
				//No Errors found. validate the properties here.
			}*/
			
			/*
			if(dates.isEmpty())
			{
				errors.add(ActionErrors.GLOBAL_ERROR,new ActionError("All dates are required"));
			}
			if(cgpans.isEmpty())
			{
				errors.add(ActionErrors.GLOBAL_ERROR,new ActionError("All CGPANs are required"));
			}
			if(cgbids.isEmpty())
			{
				errors.add(ActionErrors.GLOBAL_ERROR,new ActionError("All CGBIDs are required"));
			}

			if(headerFours.isEmpty())
			{
				errors.add(ActionErrors.GLOBAL_ERROR,new ActionError("All Header Four details are required"));
			}
			*/
			//Map datesMap=getDates();
			//Set datesSet=datesMap.keySet();

			//Iterator datesIterator=datesSet.iterator();
	/*		ActionError error=generateError(dates,"dates");
			if(error !=null)
			{
				errors.add(ActionErrors.GLOBAL_ERROR,error);
			}
			error=generateError(cgpans,"CGPANS");
			if(error !=null)
			{
				errors.add(ActionErrors.GLOBAL_ERROR,error);
			}
			
			error=generateError(cgbids,"CGBIDS");
			if(error !=null)
			{
				errors.add(ActionErrors.GLOBAL_ERROR,error);
			}
			error=generateError(headerFours,"Header Fours");
			if(error !=null)
			{
				errors.add(ActionErrors.GLOBAL_ERROR,error);
			}
			/*					
			while(datesIterator.hasNext())
			{
				String data=(String)datesMap.get(datesIterator.next());
				if(data==null || data.trim().equals(""))
				{
					errors.add(ActionErrors.GLOBAL_ERROR,new ActionError("errors.all.required","dates"));
					break;	
				}
			}
			
			Iterator cgpanIterator=getCgpans().keySet().iterator();
			while(cgpanIterator.hasNext())
			{
				String data=(String)cgpans.get(cgpanIterator.next());
				if(data==null || data.trim().equals(""))
				{
					errors.add(ActionErrors.GLOBAL_ERROR,new ActionError("errors.all.required","CGPANs"));
					break;	
				}
			}
					
			Iterator cgbidIterator=getCgbids().keySet().iterator();
			
			while(cgbidIterator.hasNext())
			{
				String data=(String)cgbids.get(cgbidIterator.next());
				if(data==null || data.trim().equals(""))
				{
					errors.add(ActionErrors.GLOBAL_ERROR,new ActionError("errors.all.required","CGBIDs"));
					break;	
				}
			}				
			Iterator headerFourIterator=getHeaderFours().keySet().iterator();
			
			while(headerFourIterator.hasNext())
			{
				String data=(String)headerFours.get(headerFourIterator.next());
				if(data==null || data.trim().equals(""))
				{
					errors.add(ActionErrors.GLOBAL_ERROR,new ActionError("errors.all.required","Header Four"));
					break;	
				}
			}*/		
			//System.out.println("***************Validation in ActionForm ***************");
	//		return errors;

		}
		
	/*	private ActionError generateError(Map map,String message)
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
	*/
		/* (non-Javadoc)
		 * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.ServletRequest)
		 */
	public void resetWhenRequired(ActionMapping arg0, HttpServletRequest arg1)
	{
		//System.out.println("IF Reset is called");
		heads.clear();
		subHeads.clear();

		headsToRender.clear();
		subHeadsToRender.clear();
		shortHeads.clear();
		shortSubHeads.clear();

		annualOrShortTerm="";
		//inflowOrOutflow="";
		month="";
		year="";
		annualFromDate="";
		annualToDate="";
		startDate	= "";
		endDate		= "";
		dateOfFlow	= "";
		valueDate = new Date();
		values.clear();
		miscReceipts.clear();
		fundTransfers.clear();
	}
	
	private boolean checkValidDate(String date)
	{
		boolean returnValue=true;
		Log.log(Log.INFO,"InvestmentForm","checkValidDate","Entered");
		
		int index=date.lastIndexOf("/");
		
		Log.log(Log.DEBUG,"InvestmentForm","index","index"+index);
					
		String year=date.substring(index,date.length());
		
		Log.log(Log.DEBUG,"InvestmentForm","year","year"+year);
					
		if(year.length()<4)
		{
			returnValue=false;	
		}
		
		Log.log(Log.INFO,"InvestmentForm","checkValidDate","Exited");
		
		return returnValue;
	}
	/**
	 * @return
	 */
	public Map getShortHeads() {
		return shortHeads;
	}
	
	/**
	 * @return
	 */
	public Map getShortSubHeads() {
		return shortSubHeads;
	}
	
	public void setShortSubHead(String key, Object value)
	{
		//System.out.println("inside INVESTMENT FORM - setHead");
		shortSubHeads.put(key,value);
	}

	public Object getShortSubHead(String key)
	{
		//System.out.println("Key "+key+" , "+heads.get(key));
		return shortSubHeads.get(key); 
	}
	
	public void setShortHead(String key, Object value)
	{
		//System.out.println("inside INVESTMENT FORM - setHead");
		shortHeads.put(key,value);
	}

	public Object getShortHead(String key)
	{
		//System.out.println("Key "+key+" , "+heads.get(key));
		return shortHeads.get(key); 
	}

	/**
	 * @return
	 */
	public Map getMiscReceipts() {
		return miscReceipts;
	}

	/**
	 * @param map
	 */
	public void setMiscReceipts(Map map) {
		miscReceipts = map;
	}

	/**
	 * @param string
	 */
	public void setMiscReceiptsDate(Date date) {
		miscReceiptsDate = date;
	}

	/**
	 * @return
	 */
	public Date getMiscReceiptsDate() {
		return miscReceiptsDate;
	}
	/**
	 * @return
	 */
	public int getCounter() {
		return counter;
	}

	/**
	 * @param i
	 */
	public void setCounter(int i) {
		counter = i;
	}

	/**
	 * @return
	 */
	public Map getFundTransfers() {
		return fundTransfers;
	}

	/**
	 * @return
	 */
	public Date getStatementDate() {
		return statementDate;
	}

	/**
	 * @param map
	 */
	public void setFundTransfers(Map map) {
		fundTransfers = map;
	}

	/**
	 * @param date
	 */
	public void setStatementDate(Date date) {
		statementDate = date;
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
	public ArrayList getBnkChqDetails() {
		return bnkChqDetails;
	}

	/**
	 * @param list
	 */
	public void setBnkChqDetails(ArrayList list) {
		bnkChqDetails = list;
	}

	/**
	 * @return
	 */
	public String getBankBranchName() {
		return bankBranchName;
	}

	/**
	 * @param string
	 */
	public void setBankBranchName(String string) {
		bankBranchName = string;
	}

	/**
	 * @return
	 */
	public Map getEndNo() {
		return endNo;
	}

	/**
	 * @return
	 */
	public Map getStartNo() {
		return startNo;
	}


	/**
	 * @param map
	 */
	public void setEndNo(Map map) {
		endNo = map;
	}

	/**
	 * @param map
	 */
	public void setStartNo(Map map) {
		startNo = map;
	}

	/**
	 * @return
	 */
	public Map getBankId() {
		return bankId;
	}

	/**
	 * @param map
	 */
	public void setBankId(Map map) {
		bankId = map;
	}

	/**
	 * @return
	 */
	public ArrayList getUnutilizedChqDetails() {
		return unutilizedChqDetails;
	}

	/**
	 * @param list
	 */
	public void setUnutilizedChqDetails(ArrayList list) {
		unutilizedChqDetails = list;
	}

	/**
	 * @return
	 */
	public Map getCancelledChq() {
		return cancelledChq;
	}

	/**
	 * @param map
	 */
	public void setCancelledChq(Map map) {
		cancelledChq = map;
	}
	
	public void resetMaps()
	{
		cancelledChq.clear();
		bnkChqDetails.clear();
	}
	
	

	/**
	 * @return
	 */
	public Map getTempEndNo() {
		return tempEndNo;
	}

	/**
	 * @param map
	 */
	public void setTempEndNo(Map map) {
		tempEndNo = map;
	}

	/**
	 * @return
	 */
	public Map getTempStartNo() {
		return tempStartNo;
	}

	/**
	 * @param map
	 */
	public void setTempStartNo(Map map) {
		tempStartNo = map;
	}

	/**
	 * @return
	 */
	public Map getInvstMaturingDetails() {
		return invstMaturingDetails;
	}

	/**
	 * @return
	 */
	public Date getValueDate() {
		return valueDate;
	}

	/**
	 * @param map
	 */
	public void setInvstMaturingDetails(Map map) {
		invstMaturingDetails = map;
	}

	/**
	 * @param date
	 */
	public void setValueDate(Date date) {
		valueDate = date;
	}

	/**
	 * @return
	 */
	public ArrayList getPlanInvFTDetails() {
		return planInvFTDetails;
	}

	/**
	 * @return
	 */
	public Map getPlanInvMainDetails() {
		return planInvMainDetails;
	}

	/**
	 * @return
	 */
	public ArrayList getPlanInvMatDetails() {
		return planInvMatDetails;
	}

	/**
	 * @return
	 */
	public ArrayList getPlanInvProvisionDetails() {
		return planInvProvisionDetails;
	}

	/**
	 * @param list
	 */
	public void setPlanInvFTDetails(ArrayList list) {
		planInvFTDetails = list;
	}

	/**
	 * @param list
	 */
	public void setPlanInvMainDetails(Map map) {
		planInvMainDetails = map;
	}

	/**
	 * @param list
	 */
	public void setPlanInvMatDetails(ArrayList list) {
		planInvMatDetails = list;
	}

	/**
	 * @param list
	 */
	public void setPlanInvProvisionDetails(ArrayList list) {
		planInvProvisionDetails = list;
	}

	/**
	 * @return
	 */
	public Map getProvisionRemarks() {
		return provisionRemarks;
	}

	/**
	 * @param map
	 */
	public void setProvisionRemarks(Map map) {
		provisionRemarks = map;
	}

	/**
	 * @return
	 */
	public Map getChequeId() {
		return chequeId;
	}

	/**
	 * @param map
	 */
	public void setChequeId(Map map) {
		chequeId = map;
	}

	/**
	 * @return
	 */
	public Date getDateOfTheDocument2() {
		return dateOfTheDocument2;
	}

	/**
	 * @return
	 */
	public Date getDateOfTheDocument3() {
		return dateOfTheDocument3;
	}

	/**
	 * @param date
	 */
	public void setDateOfTheDocument2(Date date) {
		dateOfTheDocument2 = date;
	}

	/**
	 * @param date
	 */
	public void setDateOfTheDocument3(Date date) {
		dateOfTheDocument3 = date;
	}

	/**
	 * @return
	 */
	public ArrayList getChequeArray() {
		return chequeArray;
	}

	/**
	 * @param list
	 */
	public void setChequeArray(ArrayList list) {
		chequeArray = list;
	}

	/**
	 * @return
	 */
	public String getChqId() {
		return chqId;
	}

	/**
	 * @param string
	 */
	public void setChqId(String string) {
		chqId = string;
	}


  public void setDepositDate(Date depositDate)
  {
    this.depositDate = depositDate;
  }


  public Date getDepositDate()
  {
    return depositDate;
  }

}
