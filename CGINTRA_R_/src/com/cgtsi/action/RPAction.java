// Decompiled Using: FrontEnd Plus v2.03 and the JAD Engine
// Available From: http://www.reflections.ath.cx
// Decompiler options: packimports(3) 
// Source File Name:   RPAction.java

package com.cgtsi.action;

import java.io.File; 
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;
import com.cgtsi.actionform.GMActionForm;
import javax.mail.Session;
import javax.mail.Transport;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import com.cgtsi.actionform.ClaimActionForm;
//import com.cgtsi.actionform.GMActionForm; // Diksha
import com.cgtsi.actionform.RPActionForm;
import com.cgtsi.admin.AdminHelper;
import com.cgtsi.admin.MenuOptions;
import com.cgtsi.admin.User;
import com.cgtsi.claim.ClaimsProcessor;
import com.cgtsi.common.Constants;
import com.cgtsi.common.DatabaseException;
import com.cgtsi.common.Log;
import com.cgtsi.common.MessageException;
import com.cgtsi.common.NoDataException;
import com.cgtsi.investmentfund.IFProcessor;
import com.cgtsi.receiptspayments.AllocationDetail;
import com.cgtsi.receiptspayments.DANSummary;
import com.cgtsi.receiptspayments.DemandAdvice;
import com.cgtsi.receiptspayments.MissingDANDetailsException;
import com.cgtsi.receiptspayments.PaymentDetails;
import com.cgtsi.receiptspayments.RealisationDetail;
import com.cgtsi.receiptspayments.RpDAO;
import com.cgtsi.receiptspayments.RpProcessor;
import com.cgtsi.receiptspayments.ShortExceedsLimitException;
import com.cgtsi.receiptspayments.Voucher;
import com.cgtsi.receiptspayments.VoucherDetail;
import com.cgtsi.registration.CollectingBank;
import com.cgtsi.registration.MLIInfo;
import com.cgtsi.registration.NoMemberFoundException;
import com.cgtsi.registration.Registration;
import com.cgtsi.reports.GeneralReport;
import com.cgtsi.reports.ReportDAO;
import com.cgtsi.reports.ReportManager;
import com.cgtsi.util.DBConnection;

// Referenced classes of package com.cgtsi.action:
//            BaseAction

public class RPAction extends BaseAction {

	private void $init$() {
		registration = new Registration();
	}

	public ActionForward showPaymentFilter(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "showPaymentFilter", "Entered");
		Log.log(4, "RPAction", "showPaymentFilter", "Exited");
		RPActionForm rpActionForm = (RPActionForm) form;
		rpActionForm.resetWhenRequired();
		rpActionForm.setPaymentId(null);
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		rpActionForm.setBankId(user.getBankId());
		rpActionForm.setZoneId(user.getZoneId());
		rpActionForm.setBranchId(user.getBranchId());
		if (bankId.equals("0000")) {
			user = null;
			rpActionForm.setSelectMember("");
		} else {
			MLIInfo mliInfo = getMemberInfo(request);
			bankId = mliInfo.getBankId();
			String branchId = mliInfo.getBranchId();
			String zoneId = mliInfo.getZoneId();
			String memberId = (new StringBuilder()).append(bankId)
					.append(zoneId).append(branchId).toString();
			rpActionForm.setSelectMember(memberId);
		}
		return mapping.findForward("success");
	}

	public ActionForward generateClaimSFDAN(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "generateClaimSFDAN", "Entered");
		RPActionForm rpActionForm = (RPActionForm) form;
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		rpActionForm.setBankId(user.getBankId());
		rpActionForm.setZoneId(user.getZoneId());
		rpActionForm.setBranchId(user.getBranchId());
		if (bankId.equals("0000")) {
			user = null;
			rpActionForm.setSelectMember("");
		} else {
			MLIInfo mliInfo = getMemberInfo(request);
			bankId = mliInfo.getBankId();
			String branchId = mliInfo.getBranchId();
			String zoneId = mliInfo.getZoneId();
			String memberId = (new StringBuilder()).append(bankId)
					.append(zoneId).append(branchId).toString();
			rpActionForm.setSelectMember(memberId);
		}
		return mapping.findForward("generateSFDAN");
	}
	
	
 
	
	 
	public ActionForward updateDanWiseDeallocation(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "generateClaimSFDAN", "Entered");
		
		System.out.println("updateDanWiseDeallocation 161");
		RPActionForm rpActionForm = (RPActionForm) form;
		User user = getUserInformation(request);
		String pay_Id = rpActionForm.getPayId();
	     System.out.println("inputPayId 161:"+pay_Id);
	 
		
		 Statement stmt = null;
		     ResultSet rs=null;
		   Connection conn = null;
	 
		   
		   ArrayList rpArray = new ArrayList();
		   RPActionForm rpActionobj = (RPActionForm)form;
		
		try{ 
       	 conn = DBConnection.getConnection();
       	 
       	 
  
       	    int pay_id_new=0;
       	    
		   //   String payIdChk1="select count(*) from (select  danamt,payamt  from (select sum(dci_amount_raised) danamt,pay_amount payamt from dan_cgpan_info_temp da,payment_detail_temp pa  where da.pay_id=pa.pay_id and da.PAY_ID='"+pay_Id+"'  group by pay_amount) where danamt=payamt)";
		       
		      String payIdChk="select count(CGPAN) count from DAN_CGPAN_INFO_TEMP where PAY_ID='"+pay_Id+"'";
		      
		      //  select count(*) from (select  danamt,payamt  from (select sum(dci_amount_raised) danamt,pay_amount payamt from dan_cgpan_info_temp da,payment_detail_temp pa
		      //  		where da.pay_id=pa.pay_id and da.PAY_ID='RP-00277-14-05-2015'  group by pay_amount) where danamt=payamt)
		        
		   		System.out.println("payIdChk   query"+payIdChk);
		   		stmt = conn.createStatement();
				 rs = stmt.executeQuery(payIdChk);
		   		 if(rs.next())
		         {
		   			pay_id_new=rs.getInt(1);
		   			System.out.println("pay_id_new");
		   		 }
		   		 System.out.println("pay_id_new=="+pay_id_new);  
		   		 
		 
		   		if(pay_id_new==0)
     		    {
		   		 System.out.println("==162=======");
		   		 try
			     {	
                  throw new NoMemberFoundException(" Entered PAY ID not Available for Update. please enter correct cgpan: ");
     		     }
		        catch(Exception e)
		        {
		          throw new NoMemberFoundException("Entered PAY ID not Available for Update. please enter correct cgpan1: ");
		        }
		        
		        }else{
		   			System.out.println(" ELSE Amount is not  match");
		   			
		   			String getDanRecord="select da.pay_id, da.dan_id,da.cgpan,da.dci_amount_raised from dan_cgpan_info_temp da,payment_detail_temp pa where da.pay_id=pa.pay_id and da.PAY_ID='"+pay_Id+"'";
		   			System.out.println("getDanRecord : "+getDanRecord);
		   		 stmt = conn.createStatement();
				 rs = stmt.executeQuery(getDanRecord);
				
				 
				 String payId="";
				  ArrayList danId=new ArrayList();
				  ArrayList danCgpan=new ArrayList();
				  ArrayList danRaisedAmt=new ArrayList(); 
				 
				
				 while(rs.next())
				 {
					 payId=rs.getString("pay_id");
					 System.out.println("payId 218 "+ payId);
					 danId.add(rs.getString("dan_id"));
					 danCgpan.add(rs.getString("cgpan"));
					 danRaisedAmt.add(rs.getDouble("dci_amount_raised"));
					 
				 }
				   request.setAttribute("payId",payId);
				 request.setAttribute("danId", danId);
				 request.setAttribute("danCgpan", danCgpan);
				 request.setAttribute("danRaisedAmt", danRaisedAmt);
				 
				 }
		 
		   		
		 }catch(SQLException e)
         {
			 e.printStackTrace();
      	   throw new NoMemberFoundException(" SQL Exception ");
         }
		        
		        Log.log(Log.INFO, "ClaimAction", "showClmInspOption", "Exited");
		          
		        return mapping.findForward("success");
		    }
	
	
	
	public ActionForward submitDanWiseDeallocation(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "generateClaimSFDAN", "Entered");
		
		System.out.println("submitDanWiseDeallocation 249");
		
		RPActionForm rpActionForm = (RPActionForm) form;
		User user = getUserInformation(request);
		//String pay_Id = rpActionForm.getPayId();
	//	System.out.println("submitDanWiseDeallocation "+rpActionForm.getDansList());
	//	System.out.println("submitDanWiseDeallocation payid "+rpActionForm.getPayId());
		
		
		Map danDetails = rpActionForm.getDansList();
		RpDAO rpDao = new RpDAO();
		
	//	System.out.println("danDetails :"+danDetails);
		
		if (danDetails.size() == 0) {
			
			//System.out.println("Atleast ONE should be select");
			throw new MessageException("Atleast One  DAN should be Deallocation.");
		}
		
		String pay_id=rpActionForm.getPayId();
	  //   System.out.println("danDetails  256 "+danDetails);
		Set voucherSet = danDetails.keySet();
		Iterator danIterator = voucherSet.iterator();
		try
		{
		while (danIterator.hasNext()) {
			String key = (String) danIterator.next();
			
			//System.out.println("key" +key);
			
			String[]  danCgpan =key.split("-"); 
			
		//	System.out.println("danCgpan"+danCgpan);
			 
		//	System.out.println("DAN No  :"+danCgpan[0]); 
			
		//	System.out.println("CGPAN  :"+danCgpan[1]); 
			
			//String cgpanStr[]=danCgpan[1].split("@");
		//	System.out.println("cgpanStr "+cgpanStr[0]);
			 
			String userId=user.getUserId();
			//String pay_id=rpActionForm.getPayId();
			rpDao.insertDeallocationData(danCgpan[0] ,danCgpan[1],pay_id,userId);
		 }
		
	               request.setAttribute("message",(new StringBuilder()).append("RP NO -: "+ pay_id+ " Modified/Cancelled ").append(" Successfully").toString());  		    
		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
	   return mapping.findForward("success");
		 
	}
	
	
	
	

	// bhu
	public ActionForward generateClaimASFDAN(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "generateClaimASFDAN", "Entered");
		RPActionForm rpActionForm = (RPActionForm) form;
		RpDAO rpDao = new RpDAO();
		User user = getUserInformation(request);
		String cgpan = rpActionForm.getCgpan().toUpperCase();
		int serviceFee = rpActionForm.getDanAmt();
		String remarks = rpActionForm.getApplRemarks().toUpperCase();
		String danType = rpActionForm.getDanType();
		rpDao.generateASFDANforClaimSettled(cgpan, serviceFee, remarks, danType);
		rpActionForm.setCgpan(null);
		rpActionForm.setApplRemarks(null);
		rpActionForm.setDanAmt(0);
		rpActionForm.setDanType(null);
		request.setAttribute(
				"message",
				(new StringBuilder())
						.append("ASF DAN generated for entered CGPAN No - ")
						.append(cgpan).append(" Successfully").toString());
		return mapping.findForward("success");
	}

	public ActionForward getPaymentDetailsForPayInSlip(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "getPaymentDetailsForPayInSlip", "Entered");
		String paymentId = request.getParameter("payId");
		RPActionForm rpActionForm = (RPActionForm) form;
		Log.log(5, "RPAction", "getPaymentDetailsForPayInSlip",
				(new StringBuilder()).append("paymentId ").append(paymentId)
						.toString());
		RpProcessor rpProcessor = new RpProcessor();
		PaymentDetails paymentDetails = rpProcessor.displayPayInSlip(paymentId);
		paymentDetails.setPaymentId(paymentId);
		BeanUtils.copyProperties(rpActionForm, paymentDetails);
		rpActionForm.setAccountNumber(paymentDetails.getCgtsiAccNumber());
		String bankName = rpActionForm.getPayInSlipFormat();
		String retPath = "";
		if (bankName.equalsIgnoreCase("IDBI"))
			retPath = "idbi";
		else if (bankName.equalsIgnoreCase("PNB"))
			retPath = "pnb";
		else if (bankName.equalsIgnoreCase("HDFC"))
			retPath = "hdfc";
		Log.log(4, "RPAction", "getPaymentDetailsForPayInSlip", "Exited");
		return mapping.findForward(retPath);
	}

	public ActionForward showJournalVoucherDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "showJournalVoucherDetails", "Entered");
		RPActionForm actionForm = (RPActionForm) form;
		actionForm.resetWhenRequired();
		IFProcessor ifProcessor = new IFProcessor();
		ArrayList instruments = ifProcessor.getInstrumentTypes("G");
		actionForm.setInstruments(instruments);
		request.setAttribute("IsRequired", Boolean.valueOf(true));
		Log.log(4, "RPAction", "showJournalVoucherDetails", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward addMoreJournalVoucherDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "addMoreJournalVoucherDetails", "Entered");
		RPActionForm rpForm = (RPActionForm) form;
		Map voucherDetails = rpForm.getVoucherDetails();
		Set voucherDetailsSet = voucherDetails.keySet();
		Iterator voucherDetailsIterator = voucherDetailsSet.iterator();
		String count = null;
		int counter = 0;
		for (; voucherDetailsIterator.hasNext(); Log.log(5, "RPAction",
				"addMoreJournalVoucherDetails",
				(new StringBuilder()).append(" count ").append(count)
						.toString())) {
			String key = (String) voucherDetailsIterator.next();
			Log.log(5, "RPAction", "addMoreJournalVoucherDetails",
					(new StringBuilder()).append(" key ").append(key)
							.toString());
			count = key.substring(key.indexOf("-") + 1, key.length());
		}

		Log.log(5, "RPAction", "addMoreJournalVoucherDetails",
				(new StringBuilder()).append(" counter ").append(counter)
						.toString());
		request.setAttribute("IsRequired", Boolean.valueOf(true));
		Log.log(4, "RPAction", "addMoreJournalVoucherDetails", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward insertJournalVoucherDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "insertJournalVoucherDetails", "Entered");
		RPActionForm rpForm = (RPActionForm) form;
		Map voucherDetails = rpForm.getVoucherDetails();
		Set voucherSet = voucherDetails.keySet();
		Iterator voucherIterator = voucherSet.iterator();
		ArrayList vouchers = new ArrayList();
		double dbtAmt = 0.0D;
		double cdtAmt = 0.0D;
		while (voucherIterator.hasNext()) {
			String key = (String) voucherIterator.next();
			Log.log(5, "RPAction", "insertJournalVoucherDetails",
					(new StringBuilder()).append("key ").append(key).toString());
			Voucher voucher = (Voucher) voucherDetails.get(key);
			vouchers.add(voucher);
			Log.log(4,
					"RPAction",
					"insertJournalVoucherDetails",
					(new StringBuilder()).append(" Ac code ")
							.append(voucher.getAcCode()).toString());
			Log.log(4,
					"RPAction",
					"insertJournalVoucherDetails",
					(new StringBuilder()).append(" adv date ")
							.append(voucher.getAdvDate()).toString());
			Log.log(5,
					"RPAction",
					"insertJournalVoucherDetails",
					(new StringBuilder()).append("adv no ")
							.append(voucher.getAdvNo()).toString());
			Log.log(5,
					"RPAction",
					"insertJournalVoucherDetails",
					(new StringBuilder()).append("amount is rs ")
							.append(voucher.getAmountInRs()).toString());
			Log.log(5,
					"RPAction",
					"insertJournalVoucherDetails",
					(new StringBuilder()).append(" debit or credir ")
							.append(voucher.getDebitOrCredit()).toString());
			Log.log(5,
					"RPAction",
					"insertJournalVoucherDetails",
					(new StringBuilder()).append("instrument date ")
							.append(voucher.getInstrumentDate()).toString());
			Log.log(5,
					"RPAction",
					"insertJournalVoucherDetails",
					(new StringBuilder()).append(" instrument no ")
							.append(voucher.getInstrumentNo()).toString());
			if (voucher.getDebitOrCredit().equalsIgnoreCase("D"))
				dbtAmt += Double.parseDouble(voucher.getAmountInRs());
			else if (voucher.getDebitOrCredit().equalsIgnoreCase("C")) {
				cdtAmt += Double.parseDouble(voucher.getAmountInRs());
				voucher.setAmountInRs((new StringBuilder()).append("-")
						.append(voucher.getAmountInRs()).toString());
			}
		}
		VoucherDetail voucherDetail = new VoucherDetail();
		BeanUtils.copyProperties(voucherDetail, rpForm);
		Log.log(5,
				"RPAction",
				"insertJournalVoucherDetails",
				(new StringBuilder()).append(" amount ")
						.append(voucherDetail.getAmount()).toString());
		Log.log(5,
				"RPAction",
				"insertJournalVoucherDetails",
				(new StringBuilder()).append(" figure ")
						.append(voucherDetail.getAmountInFigure()).toString());
		Log.log(5,
				"RPAction",
				"insertJournalVoucherDetails",
				(new StringBuilder()).append(" GL code ")
						.append(voucherDetail.getBankGLCode()).toString());
		Log.log(5,
				"RPAction",
				"insertJournalVoucherDetails",
				(new StringBuilder()).append(" GL name")
						.append(voucherDetail.getBankGLName()).toString());
		Log.log(5,
				"RPAction",
				"insertJournalVoucherDetails",
				(new StringBuilder()).append("dept code ")
						.append(voucherDetail.getDeptCode()).toString());
		voucherDetail.setAmount(cdtAmt - dbtAmt);
		Log.log(5,
				"RPAction",
				"insertJournalVoucherDetails",
				(new StringBuilder()).append(" amount ")
						.append(voucherDetail.getAmount()).toString());
		voucherDetail.setVouchers(vouchers);
		RpProcessor rpProcessor = new RpProcessor();
		User user = getUserInformation(request);
		voucherDetail.setVoucherType("JOURNAL VOUCHER");
		String voucherId = rpProcessor.insertVoucherDetails(voucherDetail,
				user.getUserId());
		String message = (new StringBuilder())
				.append("Journal Voucher details stored successfull. Voucher number is ")
				.append(voucherId).toString();
		request.setAttribute("message", message);
		Log.log(4, "RPAction", "insertJournalVoucherDetails", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward showPaymentVoucherDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "showPaymentVoucherDetails", "Entered");
		RPActionForm actionForm = (RPActionForm) form;
		actionForm.resetWhenRequired();
		IFProcessor ifProcessor = new IFProcessor();
		ArrayList instruments = ifProcessor.getInstrumentTypes("G");
		RpProcessor rpProcessor = new RpProcessor();
		ArrayList glHeads = rpProcessor.getGLHeads();
		actionForm.setGlHeads(glHeads);
		actionForm.setInstruments(instruments);
		request.setAttribute("IsRequired", Boolean.valueOf(true));
		HttpSession session = request.getSession(false);
		session.setAttribute("VOUCHER_FLAG", "1");
		Log.log(4, "RPAction", "showPaymentVoucherDetails", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward insertPaymentVoucherDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "insertPaymentVoucherDetails", "Entered");
		RPActionForm rpForm = (RPActionForm) form;
		Map voucherDetails = rpForm.getVoucherDetails();
		Set voucherSet = voucherDetails.keySet();
		Iterator voucherIterator = voucherSet.iterator();
		ArrayList vouchers = new ArrayList();
		double dbtAmt = 0.0D;
		double cdtAmt = 0.0D;
		while (voucherIterator.hasNext()) {
			String key = (String) voucherIterator.next();
			Log.log(5, "RPAction", "insertPaymentVoucherDetails",
					(new StringBuilder()).append("key ").append(key).toString());
			Voucher voucher = (Voucher) voucherDetails.get(key);
			vouchers.add(voucher);
			Log.log(5,
					"RPAction",
					"insertPaymentVoucherDetails",
					(new StringBuilder()).append(" Ac code ")
							.append(voucher.getAcCode()).toString());
			Log.log(5,
					"RPAction",
					"insertPaymentVoucherDetails",
					(new StringBuilder()).append(" adv date ")
							.append(voucher.getAdvDate()).toString());
			Log.log(5,
					"RPAction",
					"insertPaymentVoucherDetails",
					(new StringBuilder()).append("adv no ")
							.append(voucher.getAdvNo()).toString());
			Log.log(5,
					"RPAction",
					"insertPaymentVoucherDetails",
					(new StringBuilder()).append("amount is rs ")
							.append(voucher.getAmountInRs()).toString());
			Log.log(5,
					"RPAction",
					"insertPaymentVoucherDetails",
					(new StringBuilder()).append(" debit or credir ")
							.append(voucher.getDebitOrCredit()).toString());
			Log.log(5,
					"RPAction",
					"insertPaymentVoucherDetails",
					(new StringBuilder()).append("instrument date ")
							.append(voucher.getInstrumentDate()).toString());
			Log.log(5,
					"RPAction",
					"insertPaymentVoucherDetails",
					(new StringBuilder()).append(" instrument no ")
							.append(voucher.getInstrumentNo()).toString());
			if (voucher.getDebitOrCredit().equalsIgnoreCase("D"))
				dbtAmt += Double.parseDouble(voucher.getAmountInRs());
			else if (voucher.getDebitOrCredit().equalsIgnoreCase("C")) {
				cdtAmt += Double.parseDouble(voucher.getAmountInRs());
				if (cdtAmt > 0.0D)
					voucher.setAmountInRs((new StringBuilder()).append("-")
							.append(voucher.getAmountInRs()).toString());
			}
		}
		VoucherDetail voucherDetail = new VoucherDetail();
		BeanUtils.copyProperties(voucherDetail, rpForm);
		Log.log(5,
				"RPAction",
				"insertPaymentVoucherDetails",
				(new StringBuilder()).append(" amount ")
						.append(rpForm.getAmount()).toString());
		Log.log(5,
				"RPAction",
				"insertPaymentVoucherDetails",
				(new StringBuilder()).append(" amount ")
						.append(voucherDetail.getAmount()).toString());
		Log.log(5,
				"RPAction",
				"insertPaymentVoucherDetails",
				(new StringBuilder()).append(" figure ")
						.append(voucherDetail.getAmountInFigure()).toString());
		Log.log(5,
				"RPAction",
				"insertPaymentVoucherDetails",
				(new StringBuilder()).append(" GL code ")
						.append(voucherDetail.getBankGLCode()).toString());
		Log.log(5,
				"RPAction",
				"insertPaymentVoucherDetails",
				(new StringBuilder()).append(" GL name")
						.append(voucherDetail.getBankGLName()).toString());
		Log.log(5,
				"RPAction",
				"insertPaymentVoucherDetails",
				(new StringBuilder()).append("dept code ")
						.append(voucherDetail.getDeptCode()).toString());
		voucherDetail.setAmount(cdtAmt - dbtAmt);
		Log.log(5,
				"RPAction",
				"insertPaymentVoucherDetails",
				(new StringBuilder()).append(" amount ")
						.append(voucherDetail.getAmount()).toString());
		voucherDetail.setVouchers(vouchers);
		RpProcessor rpProcessor = new RpProcessor();
		User user = getUserInformation(request);
		voucherDetail.setVoucherType("PAYMENT VOUCHER");
		String voucherId = rpProcessor.insertVoucherDetails(voucherDetail,
				user.getUserId());
		String message = (new StringBuilder())
				.append("Voucher details stored successfull. Voucher number is ")
				.append(voucherId).toString();
		request.setAttribute("message", message);
		Log.log(4, "RPAction", "insertPaymentVoucherDetails", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward addMorePaymentVoucherDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "addMorePaymentVoucherDetails", "Entered");
		RPActionForm rpForm = (RPActionForm) form;
		Map voucherDetails = rpForm.getVoucherDetails();
		Set voucherDetailsSet = voucherDetails.keySet();
		Iterator voucherDetailsIterator = voucherDetailsSet.iterator();
		String count = null;
		int counter = 0;
		for (; voucherDetailsIterator.hasNext(); Log.log(5, "RPAction",
				"addMorePaymentVoucherDetails",
				(new StringBuilder()).append(" count ").append(count)
						.toString())) {
			String key = (String) voucherDetailsIterator.next();
			Log.log(5, "RPAction", "addMorePaymentVoucherDetails",
					(new StringBuilder()).append(" key ").append(key)
							.toString());
			count = key.substring(key.indexOf("-") + 1, key.length());
		}

		Log.log(5, "RPAction", "addMorePaymentVoucherDetails",
				(new StringBuilder()).append(" counter ").append(counter)
						.toString());
		request.setAttribute("IsRequired", Boolean.valueOf(true));
		Log.log(4, "RPAction", "addMorePaymentVoucherDetails", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward showReceiptVoucherDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "showReceiptVoucherDetails", "Entered");
		RPActionForm actionForm = (RPActionForm) form;
		actionForm.resetWhenRequired();
		IFProcessor ifProcessor = new IFProcessor();
		ArrayList instruments = ifProcessor.getInstrumentTypes("G");
		actionForm.setInstruments(instruments);
		RpProcessor rpProcessor = new RpProcessor();
		ArrayList glHeads = rpProcessor.getGLHeads();
		actionForm.setGlHeads(glHeads);
		HttpSession session = request.getSession(false);
		session.setAttribute("VOUCHER_FLAG", "1");
		request.setAttribute("IsRequired", Boolean.valueOf(true));
		Log.log(4, "RPAction", "showReceiptVoucherDetails", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward insertReceiptVoucherDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "insertReceiptVoucherDetails", "Entered");
		RPActionForm rpForm = (RPActionForm) form;
		Map voucherDetails = rpForm.getVoucherDetails();
		Set voucherSet = voucherDetails.keySet();
		Iterator voucherIterator = voucherSet.iterator();
		ArrayList vouchers = new ArrayList();
		double dbtAmt = 0.0D;
		double cdtAmt = 0.0D;
		while (voucherIterator.hasNext()) {
			String key = (String) voucherIterator.next();
			Log.log(5, "RPAction", "insertReceiptVoucherDetails",
					(new StringBuilder()).append("key ").append(key).toString());
			Voucher voucher = (Voucher) voucherDetails.get(key);
			vouchers.add(voucher);
			Log.log(5,
					"RPAction",
					"insertReceiptVoucherDetails",
					(new StringBuilder()).append(" Ac code ")
							.append(voucher.getAcCode()).toString());
			Log.log(5,
					"RPAction",
					"insertReceiptVoucherDetails",
					(new StringBuilder()).append(" adv date ")
							.append(voucher.getAdvDate()).toString());
			Log.log(5,
					"RPAction",
					"insertReceiptVoucherDetails",
					(new StringBuilder()).append("adv no ")
							.append(voucher.getAdvNo()).toString());
			Log.log(5,
					"RPAction",
					"insertReceiptVoucherDetails",
					(new StringBuilder()).append("amount is rs ")
							.append(voucher.getAmountInRs()).toString());
			Log.log(5,
					"RPAction",
					"insertReceiptVoucherDetails",
					(new StringBuilder()).append(" debit or credir ")
							.append(voucher.getDebitOrCredit()).toString());
			Log.log(5,
					"RPAction",
					"insertReceiptVoucherDetails",
					(new StringBuilder()).append("instrument date ")
							.append(voucher.getInstrumentDate()).toString());
			Log.log(5,
					"RPAction",
					"insertReceiptVoucherDetails",
					(new StringBuilder()).append(" instrument no ")
							.append(voucher.getInstrumentNo()).toString());
			if (voucher.getDebitOrCredit().equalsIgnoreCase("D"))
				dbtAmt += Double.parseDouble(voucher.getAmountInRs());
			else if (voucher.getDebitOrCredit().equalsIgnoreCase("C")) {
				cdtAmt += Double.parseDouble(voucher.getAmountInRs());
				voucher.setAmountInRs((new StringBuilder()).append("-")
						.append(voucher.getAmountInRs()).toString());
			}
		}
		VoucherDetail voucherDetail = new VoucherDetail();
		BeanUtils.copyProperties(voucherDetail, rpForm);
		Log.log(5,
				"RPAction",
				"insertReceiptVoucherDetails",
				(new StringBuilder()).append(" amount ")
						.append(voucherDetail.getAmount()).toString());
		Log.log(5,
				"RPAction",
				"insertReceiptVoucherDetails",
				(new StringBuilder()).append(" figure ")
						.append(voucherDetail.getAmountInFigure()).toString());
		Log.log(5,
				"RPAction",
				"insertReceiptVoucherDetails",
				(new StringBuilder()).append(" GL code ")
						.append(voucherDetail.getBankGLCode()).toString());
		Log.log(5,
				"RPAction",
				"insertReceiptVoucherDetails",
				(new StringBuilder()).append(" GL name")
						.append(voucherDetail.getBankGLName()).toString());
		Log.log(5,
				"RPAction",
				"insertReceiptVoucherDetails",
				(new StringBuilder()).append("dept code ")
						.append(voucherDetail.getDeptCode()).toString());
		voucherDetail.setAmount(dbtAmt - cdtAmt);
		Log.log(5,
				"RPAction",
				"insertReceiptVoucherDetails",
				(new StringBuilder()).append(" amount ")
						.append(voucherDetail.getAmount()).toString());
		voucherDetail.setVouchers(vouchers);
		RpProcessor rpProcessor = new RpProcessor();
		User user = getUserInformation(request);
		voucherDetail.setVoucherType("RECEIPT VOUCHER");
		String voucherId = rpProcessor.insertVoucherDetails(voucherDetail,
				user.getUserId());
		String message = (new StringBuilder())
				.append("Voucher details stored successfull. Voucher number is ")
				.append(voucherId).toString();
		request.setAttribute("message", message);
		Log.log(4, "RPAction", "insertReceiptVoucherDetails", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward addMoreReceiptVoucherDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "addMoreReceiptVoucherDetails", "Entered");
		RPActionForm rpForm = (RPActionForm) form;
		Map voucherDetails = rpForm.getVoucherDetails();
		Set voucherDetailsSet = voucherDetails.keySet();
		Iterator voucherDetailsIterator = voucherDetailsSet.iterator();
		String count = null;
		int counter = 0;
		for (; voucherDetailsIterator.hasNext(); Log.log(5, "RPAction",
				"addMoreReceiptVoucherDetails",
				(new StringBuilder()).append(" count ").append(count)
						.toString())) {
			String key = (String) voucherDetailsIterator.next();
			Log.log(5, "RPAction", "addMoreReceiptVoucherDetails",
					(new StringBuilder()).append(" key ").append(key)
							.toString());
			count = key.substring(key.indexOf("-") + 1, key.length());
		}

		Log.log(5, "RPAction", "addMoreReceiptVoucherDetails",
				(new StringBuilder()).append(" counter ").append(counter)
						.toString());
		request.setAttribute("IsRequired", Boolean.valueOf(true));
		Log.log(4, "RPAction", "addMoreReceiptVoucherDetails", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward getPendingDANsFilter(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		RPActionForm rpActionForm = (RPActionForm) form;
		HttpSession session = request.getSession(false);
		rpActionForm.setAllocationType("F");
		if (bankId.equalsIgnoreCase("0000")) {
			rpActionForm.setSelectMember("");
			session.setAttribute("TARGET_URL",
					"selectMember1.do?method=getPendingDANs");
			return mapping.findForward("memberInfo");
		} else {
			request.setAttribute("pageValue", "1");
			getPendingDANs(mapping, form, request, response);
			return mapping.findForward("danSummary");
		}
	}

	public ActionForward getPendingASFDANsFilter(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		RPActionForm rpActionForm = (RPActionForm) form;
		HttpSession session = request.getSession(false);
		rpActionForm.setAllocationType("A");
		if (bankId.equalsIgnoreCase("0000")) {
			rpActionForm.setSelectMember("");
			session.setAttribute("TARGET_URL",
					"selectASFMember.do?method=getPendingASFDANs");
			return mapping.findForward("memberInfo");
		} else {
			request.setAttribute(
					"message",
					"<b> In terms of Circular No.59/2009-10 dated March 11,2010, it is mandatory for all MLIs <br>  to make ASF 2011 payment through a single payment from the Head Office.");
			return mapping.findForward("success");
		}
	}

	public ActionForward getPendingExpiredASFDANsFilter(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		RPActionForm rpActionForm = (RPActionForm) form;
		HttpSession session = request.getSession(false);
		if (bankId.equalsIgnoreCase("0000")) {
			rpActionForm.setSelectMember("");
			session.setAttribute("TARGET_URL",
					"selectASFMemberForExpired.do?method=getPendingExpiredASFDANs");
			return mapping.findForward("memberInfo");
		} else {
			request.setAttribute(
					"message",
					"<b> In terms of Circular No.59/2009-10 dated March 11,2010, it is mandatory for all MLIs <br>  to make ASF 2011 payment through a single payment from the Head Office.");
			return mapping.findForward("success");
		}
	}

	public ActionForward getPendingGFDANsFilter(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		RPActionForm rpActionForm = (RPActionForm) form;
		rpActionForm.setAllocationType("G");
		HttpSession session = request.getSession(false);
		if (bankId.equalsIgnoreCase("0000")) {
			rpActionForm.setSelectMember("");
			session.setAttribute("TARGET_URL",
					"selectGFMember.do?method=getPendingGFDANs");
			return mapping.findForward("memberInfo");
		} else {
			request.setAttribute("pageValue", "1");
			getPendingGFDANs(mapping, form, request, response);
			return mapping.findForward("danSummary");
		}
	}

	public ActionForward getPendingGFDANsFilterNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		RPActionForm rpActionForm = (RPActionForm) form;
		rpActionForm.setAllocationType("G");
		HttpSession session = request.getSession(false);
		if (bankId.equalsIgnoreCase("0000")) {
			rpActionForm.setSelectMember("");
			session.setAttribute("TARGET_URL",
					"selectGFMemberNew.do?method=getNEFTPendingGFDANs");
			return mapping.findForward("memberInfoNew");
		} else {
			request.setAttribute("pageValue", "1");
			getNEFTPendingGFDANs(mapping, form, request, response);
			return mapping.findForward("neftdanSummary");
		}
	}

	public ActionForward getPendingTextileGFDANsFilter(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		RPActionForm rpActionForm = (RPActionForm) form;
		HttpSession session = request.getSession(false);
		rpActionForm.setAllocationType("H");
		request.setAttribute("pageValue", "1");
		getPendingTextileGFDANs(mapping, form, request, response);
		return mapping.findForward("danSummary4");
	}

	public ActionForward getPendingTextileGFDANs(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "getPendingTextileGFDANs", "Entered");
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = "";
		RPActionForm actionForm = (RPActionForm) form;
		HttpSession session = request.getSession(false);
		if (actionForm.getDanSummaries() != null)
			actionForm.getDanSummaries().clear();
		if (actionForm.getDanPanDetails() != null)
			actionForm.getDanPanDetails().clear();
		if (actionForm.getCgpans() != null)
			actionForm.getCgpans().clear();
		if (actionForm.getAllocatedFlags() != null)
			actionForm.getAllocatedFlags().clear();
		if (actionForm.getFirstDisbursementDates() != null)
			actionForm.getFirstDisbursementDates().clear();
		if (actionForm.getNotAllocatedReasons() != null)
			actionForm.getNotAllocatedReasons().clear();
		Log.log(5, "RPAction", "getPendingTextileGFDANs", (new StringBuilder())
				.append("Bank Id : ").append(bankId).toString());
		Log.log(5, "RPAction", "getPendingTextileGFDANs", (new StringBuilder())
				.append("Zone Id : ").append(zoneId).toString());
		Log.log(5, "RPAction", "getPendingTextileGFDANs", (new StringBuilder())
				.append("Branch Id : ").append(branchId).toString());
		bankId = "0019";
		zoneId = "0001";
		Log.log(5,
				"RPAction",
				"getPendingGFDANs",
				(new StringBuilder())
						.append("Selected Bank Id,zone and branch ids : ")
						.append(bankId).append(",").append(zoneId).append(",")
						.append(branchId).toString());
		RpProcessor rpProcessor = new RpProcessor();
		ArrayList danSummaries = rpProcessor.displayTextileGFDANs();
		Log.log(5, "RPAction", "getPendingGFDANs", (new StringBuilder())
				.append("dan summary size : ").append(danSummaries.size())
				.toString());
		if (danSummaries.size() == 0) {
			actionForm.setSelectMember(null);
			throw new MissingDANDetailsException(
					"No DANs available for Allocation");
		}
		boolean isDanAvailable = false;
		for (int i = 0; i < danSummaries.size(); i++) {
			DANSummary danSummary = (DANSummary) danSummaries.get(i);
			if (danSummary.getAmountDue() == danSummary.getAmountPaid())
				continue;
			isDanAvailable = true;
			break;
		}

		if (!isDanAvailable) {
			actionForm.setSelectMember(null);
			throw new MissingDANDetailsException(
					"No DANs available for Allocation");
		}
		actionForm.setDanSummaries(danSummaries);
		actionForm.setBankId(bankId);
		actionForm.setZoneId(zoneId);
		actionForm.setBranchId(branchId);
		Log.log(4, "RPAction", "getPendingTextileGFDANs", "Exited");
		if (actionForm.getSelectMember() != null)
			actionForm.setMemberId(actionForm.getSelectMember());
		else
			actionForm.setMemberId((new StringBuilder()).append(bankId)
					.append(zoneId).append(branchId).toString());
		actionForm.setSelectMember(null);
		return mapping.findForward("danSummary4");
	}

	public ActionForward submitTextileGFDANPayments(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RPActionForm actionForm = (RPActionForm) form;
		double totalAmount = 0.0D;
		Map danIds = actionForm.getDanIds();
		Map allocatedFlags = actionForm.getAllocatedFlags();
		Map cgpans = actionForm.getCgpans();
		int allocatedcount = 0;
		int testallocatecount = 0;
		Set danIdSet = danIds.keySet();
		Log.log(5,
				"RPAction",
				"submitTextileGFDANPayments",
				(new StringBuilder()).append("Checkbox size = ")
						.append(allocatedFlags.size()).toString());
		Set cgpansSet = cgpans.keySet();
		Iterator cgpanIterator = cgpansSet.iterator();
		Log.log(5,
				"RPAction",
				"submitTextileGFDANPayments",
				(new StringBuilder()).append("Checkbox size = ")
						.append(cgpans.size()).toString());
		String value;
		for (; cgpanIterator.hasNext(); Log.log(5, "RPAction",
				"submitTextileGFDANPayments",
				(new StringBuilder()).append("cgpan value = ").append(value)
						.toString())) {
			String key = (String) cgpanIterator.next();
			value = (String) cgpans.get(key);
			Log.log(5, "RPAction", "submitTextileGFDANPayments",
					(new StringBuilder()).append("cgpan key = ").append(key)
							.toString());
		}

		cgpanIterator = cgpansSet.iterator();
		ArrayList danSummaries = actionForm.getDanSummaries();
		boolean isAllocated = false;
		for (int i = 0; i < danSummaries.size(); i++) {
			DANSummary danSummary = (DANSummary) danSummaries.get(i);
			String danId = danSummary.getDanId();
			Log.log(5, "RPAction", "submitTextileGFDANPayments",
					(new StringBuilder()).append("danId= ").append(danId)
							.toString());
			String danIdKey = danId.replace('.', '_');
			if (allocatedFlags.containsKey(danIdKey)
					&& request.getParameter((new StringBuilder())
							.append("allocatedFlag(").append(danIdKey)
							.append(")").toString()) != null) {
				allocatedcount++;
				Log.log(5,
						"RPAction",
						"submitTextileGFDANPayments",
						(new StringBuilder()).append("danSummaries= ")
								.append(danSummaries.size()).toString());
				isAllocated = true;
				totalAmount += danSummary.getAmountDue()
						- danSummary.getAmountPaid();
				Log.log(5,
						"RPAction",
						"submitTextileGFDANPayments",
						(new StringBuilder())
								.append("due amount ")
								.append(danSummary.getAmountDue()
										- danSummary.getAmountPaid())
								.toString());
			} else {
				Log.log(5, "RPAction", "submitTextileGFDANPayments",
						"CGPANS are allocated ");
				ArrayList panDetails = (ArrayList) actionForm
						.getDanPanDetail(danId);
				while (cgpanIterator.hasNext()) {
					String key = (String) cgpanIterator.next();
					value = (String) cgpans.get(key);
					String cgpanPart = value.substring(value.indexOf("-") + 1,
							value.length());
					String tempKey = value.replace('.', '_');
					Log.log(5, "RPAction", "submitTextileGFDANPayments",
							(new StringBuilder()).append("key ").append(key)
									.toString());
					Log.log(5, "RPAction", "submitTextileGFDANPayments",
							(new StringBuilder()).append("value ")
									.append(value).toString());
					Log.log(5,
							"RPAction",
							"submitTextileGFDANPayments",
							(new StringBuilder()).append("tempKey ")
									.append(tempKey).toString());
					if (value.startsWith(danId)
							&& allocatedFlags.get(tempKey) != null
							&& ((String) allocatedFlags.get(tempKey))
									.equals("Y")) {
						testallocatecount++;
						cgpanPart = value.substring(value.indexOf("-") + 1,
								value.length());
						isAllocated = true;
						for (int j = 0; j < panDetails.size(); j++) {
							AllocationDetail allocation = (AllocationDetail) panDetails
									.get(j);
							Log.log(5,
									"RPAction",
									"submitTextileGFDANPayments",
									(new StringBuilder())
											.append("amount for CGPAN ")
											.append(allocation.getCgpan())
											.append(",")
											.append(allocation.getAmountDue())
											.toString());
							if (!cgpanPart.equals(allocation.getCgpan()))
								continue;
							totalAmount += allocation.getAmountDue();
							break;
						}

					}
				}
				cgpanIterator = cgpansSet.iterator();
			}
		}

		if (!isAllocated) {
			throw new MissingDANDetailsException("No Allocation made.");
		} else {
			Registration registration = new Registration();
			Log.log(5,
					"RPAction",
					"submitTextileGFDANPayments",
					(new StringBuilder()).append("member id ")
							.append(actionForm.getMemberId()).toString());
			CollectingBank collectingBank = registration
					.getCollectingBank((new StringBuilder()).append("(")
							.append(actionForm.getMemberId()).append(")")
							.toString());
			Log.log(5,
					"RPAction",
					"submitTextileGFDANPayments",
					(new StringBuilder()).append("collectingBank ")
							.append(collectingBank).toString());
			actionForm.setModeOfPayment("");
			actionForm.setPaymentDate(null);
			actionForm.setInstrumentNo("");
			actionForm.setInstrumentDate(null);
			actionForm.setDrawnAtBank("");
			actionForm.setDrawnAtBranch("");
			actionForm.setPayableAt("");
			IFProcessor ifProcessor = new IFProcessor();
			ArrayList instruments = ifProcessor.getInstrumentTypes("G");
			actionForm.setInstruments(instruments);
			RpDAO rpDAO = new RpDAO();
			actionForm.setInstrumentNo(rpDAO.getInstrumentSeq());
			actionForm.setCollectingBank(collectingBank.getCollectingBankId());
			actionForm.setCollectingBankName(collectingBank
					.getCollectingBankName());
			actionForm.setCollectingBankBranch(collectingBank.getBranchName());
			actionForm.setAccountNumber(collectingBank.getAccNo());
			actionForm.setInstrumentAmount(totalAmount);
			return mapping.findForward("gfpaymentDetails");
		}
	}

	public ActionForward getTextileGFPANDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "getGFPANDetails", "Entered");
		RPActionForm actionForm = (RPActionForm) form;
		Map cgpans = actionForm.getCgpans();
		Set cgpanSet = cgpans.keySet();
		Iterator cgpanIterator = cgpanSet.iterator();
		Log.log(5, "RPAction", "getGFPANDetails", "CGPANS selected ");
		String key;
		for (; cgpanIterator.hasNext(); Log.log(5, "RPAction",
				"getGFPANDetails", (new StringBuilder()).append("key,value ")
						.append(key).append(",").append(cgpans.get(key))
						.toString()))
			key = (String) cgpanIterator.next();

		Log.log(5,
				"RPAction",
				"getGFPANDetails",
				(new StringBuilder()).append("Cgpan map size ")
						.append(cgpans.size()).toString());
		String danNo = actionForm.getDanNo();
		Log.log(4,
				"RPAction",
				"getGFPANDetails",
				(new StringBuilder()).append("On entering, DAN no: ")
						.append(danNo).toString());
		Log.log(4,
				"RPAction",
				"getGFPANDetails",
				(new StringBuilder()).append("No Session: DAN no : ")
						.append(danNo).toString());
		RpProcessor rpProcessor = new RpProcessor();
		ArrayList returnList = rpProcessor.displayCGPANs(danNo);
		ArrayList panDetails = (ArrayList) returnList.get(0);
		ArrayList allocatedPanDetails = (ArrayList) returnList.get(1);
		Log.log(4,
				"RPAction",
				"getGFPANDetails",
				(new StringBuilder())
						.append("No Session: No. of PAN details : ")
						.append(panDetails.size()).toString());
		String allocatedFlag = (String) actionForm.getAllocatedFlag(danNo
				.replace('.', '_'));
		Log.log(4, "RPAction", "getGFPANDetails",
				(new StringBuilder()).append("flag ").append(allocatedFlag)
						.toString());
		Map allocatedFlags = actionForm.getAllocatedFlags();
		if (allocatedFlag != null && allocatedFlag.equalsIgnoreCase(danNo)) {
			for (int i = 0; i < panDetails.size(); i++) {
				AllocationDetail allocationDetail = (AllocationDetail) panDetails
						.get(i);
				key = (new StringBuilder()).append(danNo.replace('.', '_'))
						.append("-").append(allocationDetail.getCgpan())
						.toString();
				allocatedFlags.put(key, "Y");
			}

		}
		actionForm.setAllocatedFlags(allocatedFlags);
		Log.log(4, "RPAction", "getGFPANDetails",
				(new StringBuilder()).append("After session validation : ")
						.append(panDetails.size()).toString());
		actionForm.setDanPanDetail(danNo, panDetails);
		actionForm.setPanDetails(panDetails);
		actionForm.setAllocatedPanDetails(allocatedPanDetails);
		return mapping.findForward("gfpanDetails");
	}

	public ActionForward gfallocatePaymentsforTextile(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RPActionForm actionForm = (RPActionForm) form;
		RpProcessor rpProcessor = new RpProcessor();
		RpDAO rpDAO = new RpDAO();
		String paymentId = "";
		String methodName = "gfallocatePaymentsforTextile";
		Log.log(5, "RPAction", methodName, "Entered");
		User user = getUserInformation(request);
		PaymentDetails paymentDetails = new PaymentDetails();
		String allocationType4 = actionForm.getAllocationType();
		paymentDetails.setAllocationType1(allocationType4);
		String modeOfPayment = actionForm.getModeOfPayment();
		double tempamounttobeallocated = rpDAO
				.getBalancePaymentFromOtherFacility(modeOfPayment);
		String collectingBranch = actionForm.getCollectingBankBranch();
		Date paymentDate = actionForm.getPaymentDate();
		String instrumentNumber = actionForm.getInstrumentNo();
		Date instrumentDate = actionForm.getInstrumentDate();
		String modeOfDelivery = actionForm.getModeOfDelivery();
		double instrumentAmount = actionForm.getInstrumentAmount();
		String drawnAtBank = actionForm.getDrawnAtBank();
		String drawnAtBranch = actionForm.getDrawnAtBranch();
		String payableAt = actionForm.getPayableAt();
		String accNumber = actionForm.getAccountNumber();
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("collecting bank ")
						.append(actionForm.getCollectingBankName()).toString());
		paymentDetails.setCollectingBank(actionForm.getCollectingBankName());
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("mode of payment ")
						.append(modeOfPayment).toString());
		paymentDetails.setModeOfPayment(modeOfPayment);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("collecting branch ")
						.append(collectingBranch).toString());
		paymentDetails.setCollectingBankBranch(collectingBranch);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("payment date ")
						.append(paymentDate).toString());
		paymentDetails.setPaymentDate(paymentDate);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("instrument number ")
						.append(instrumentNumber).toString());
		paymentDetails.setInstrumentNo(instrumentNumber);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("instrument date ")
						.append(instrumentDate).toString());
		paymentDetails.setInstrumentDate(instrumentDate);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("mode of delivery ")
						.append(modeOfDelivery).toString());
		paymentDetails.setModeOfDelivery(modeOfDelivery);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("instrument amount ")
						.append(instrumentAmount).toString());
		paymentDetails.setInstrumentAmount(instrumentAmount);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("drawn at bank ")
						.append(drawnAtBank).toString());
		paymentDetails.setDrawnAtBank(drawnAtBank);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("drawn at branch ")
						.append(drawnAtBranch).toString());
		paymentDetails.setDrawnAtBranch(drawnAtBranch);
		Log.log(5, "RPAction", methodName,
				(new StringBuilder()).append("payable at ").append(payableAt)
						.toString());
		paymentDetails.setPayableAt(payableAt);
		Log.log(5, "RPAction", methodName,
				(new StringBuilder()).append("acc num ").append(accNumber)
						.toString());
		paymentDetails.setCgtsiAccNumber(accNumber);
		if (tempamounttobeallocated - instrumentAmount < 0.0D)
			throw new ShortExceedsLimitException((new StringBuilder())
					.append("Sufficient fund not available. Short by - ")
					.append(tempamounttobeallocated - instrumentAmount)
					.toString());
		if (tempamounttobeallocated - instrumentAmount >= 0.0D)
			rpDAO.updateTempUtilForOtherFacility(modeOfPayment,
					instrumentAmount);
		Map allocationFlags = actionForm.getAllocatedFlags();
		ArrayList danSummaries = actionForm.getDanSummaries();
		Map cgpans = actionForm.getCgpans();
		Set cgpansSet = cgpans.keySet();
		Map danCgpanDetails = actionForm.getDanPanDetails();
		Map notAllocatedReasons = actionForm.getNotAllocatedReasons();
		for (int i = 0; i < danSummaries.size(); i++) {
			DANSummary danSummary = (DANSummary) danSummaries.get(i);
			String danId = danSummary.getDanId();
			Log.log(5, "RPAction", methodName,
					(new StringBuilder()).append("danId ").append(danId)
							.toString());
			String shiftDanId = danId.replace('.', '_');
			Log.log(5,
					"RPAction",
					methodName,
					(new StringBuilder()).append("contains ")
							.append(danCgpanDetails.containsKey(danId))
							.toString());
			if (danCgpanDetails.containsKey(danId)) {
				ArrayList panAllocationDetails = (ArrayList) danCgpanDetails
						.get(danId);
				if (panAllocationDetails == null) {
					Log.log(5, "RPAction", methodName,
							"CGPAN details are not available. get them.");
					ArrayList totalList = rpProcessor.displayCGPANs(danId);
					panAllocationDetails = (ArrayList) totalList.get(0);
				}
				for (int j = 0; j < panAllocationDetails.size(); j++) {
					AllocationDetail allocationDetail = (AllocationDetail) panAllocationDetails
							.get(j);
					Log.log(5,
							"RPActionForm",
							"validate",
							(new StringBuilder())
									.append(" cgpan from danpandetails ")
									.append(allocationDetail.getCgpan())
									.toString());
					if (allocationDetail.getAllocatedFlag().equals("N")) {
						Log.log(5, "RPActionForm", "validate",
								" not allocated ");
						String reasons = (String) notAllocatedReasons
								.get((new StringBuilder()).append(shiftDanId)
										.append("-")
										.append(allocationDetail.getCgpan())
										.toString());
						Log.log(5,
								"RPActionForm",
								"validate",
								(new StringBuilder())
										.append(" reason for not allocated ")
										.append(reasons).toString());
						allocationDetail.setNotAllocatedReason(reasons);
					} else {
						allocationDetail.setNotAllocatedReason("");
					}
					panAllocationDetails.set(j, allocationDetail);
				}

				danCgpanDetails.put(danId, panAllocationDetails);
			}
		}

		request.setAttribute(
				"message",
				(new StringBuilder())
						.append("Payment Allocated Successfully.<BR>Payment ID : ")
						.append(paymentId).toString());
		Log.log(5, "RPAction", methodName, "Exited");
		return mapping.findForward("success");
	}

	public ActionForward getPendingSFDANsFilter(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		RPActionForm rpActionForm = (RPActionForm) form;
		HttpSession session = request.getSession(false);
		if (bankId.equalsIgnoreCase("0000")) {
			rpActionForm.setSelectMember("");
			session.setAttribute("TARGET_URL",
					"selectSFMember.do?method=getPendingSFDANs");
			return mapping.findForward("memberInfo");
		} else {
			request.setAttribute("pageValue", "1");
			getPendingDANs(mapping, form, request, response);
			return mapping.findForward("danSummary");
		}
	}

	public ActionForward getPendingDANs(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Log.log(4, "RPAction", "getPendingDANs", "Entered");
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = "";
		RPActionForm actionForm = (RPActionForm) form;
		HttpSession session = request.getSession(false);
		if (actionForm.getDanSummaries() != null)
			actionForm.getDanSummaries().clear();
		if (actionForm.getDanPanDetails() != null)
			actionForm.getDanPanDetails().clear();
		if (actionForm.getCgpans() != null)
			actionForm.getCgpans().clear();
		if (actionForm.getAllocatedFlags() != null)
			actionForm.getAllocatedFlags().clear();
		if (actionForm.getFirstDisbursementDates() != null)
			actionForm.getFirstDisbursementDates().clear();
		if (actionForm.getNotAllocatedReasons() != null)
			actionForm.getNotAllocatedReasons().clear();
		if (actionForm.getAppropriatedFlags() != null)
			actionForm.getAppropriatedFlags().clear();
		Log.log(5, "RPAction", "getPendingDANs",
				(new StringBuilder()).append("Bank Id : ").append(bankId)
						.toString());
		Log.log(5, "RPAction", "getPendingDANs",
				(new StringBuilder()).append("Zone Id : ").append(zoneId)
						.toString());
		Log.log(5, "RPAction", "getPendingDANs",
				(new StringBuilder()).append("Branch Id : ").append(branchId)
						.toString());
		if (bankId.equals("0000")) {
			memberId = actionForm.getSelectMember();
			if (memberId == null || memberId.equals(""))
				memberId = actionForm.getMemberId();
			Log.log(5, "RPAction", "getPendingDANs", (new StringBuilder())
					.append("mliId = ").append(memberId).toString());
			if (memberId == null || memberId.equals("")) {
				session.setAttribute("TARGET_URL",
						"selectMember1.do?method=getPendingDANs");
				return mapping.findForward("memberInfo");
			}
			bankId = memberId.substring(0, 4);
			zoneId = memberId.substring(4, 8);
			branchId = memberId.substring(8, 12);
			ClaimsProcessor cpProcessor = new ClaimsProcessor();
			Vector memberIds = cpProcessor.getAllMemberIds();
			if (!memberIds.contains(memberId))
				throw new NoMemberFoundException("The Member ID does not exist");
		}
		Log.log(5,
				"RPAction",
				"getPendingDANs",
				(new StringBuilder())
						.append("Selected Bank Id,zone and branch ids : ")
						.append(bankId).append(",").append(zoneId).append(",")
						.append(branchId).toString());
		RpProcessor rpProcessor = new RpProcessor();
		ArrayList danSummaries = rpProcessor.displayDANs(bankId, zoneId,
				branchId);
		Log.log(5,
				"RPAction",
				"getPendingDANs",
				(new StringBuilder()).append("dan summary size : ")
						.append(danSummaries.size()).toString());
		if (danSummaries.size() == 0) {
			actionForm.setSelectMember(null);
			throw new MissingDANDetailsException(
					"No DANs available for Allocation");
		}
		boolean isDanAvailable = false;
		for (int i = 0; i < danSummaries.size(); i++) {
			DANSummary danSummary = (DANSummary) danSummaries.get(i);
			if (danSummary.getAmountDue() == danSummary.getAmountPaid())
				continue;
			isDanAvailable = true;
			break;
		}

		if (!isDanAvailable) {
			actionForm.setSelectMember(null);
			throw new MissingDANDetailsException(
					"No DANs available for Allocation");
		}
		actionForm.setDanSummaries(danSummaries);
		actionForm.setBankId(bankId);
		actionForm.setZoneId(zoneId);
		actionForm.setBranchId(branchId);
		Log.log(4, "RPAction", "getPendingDANs", "Exited");
		if (actionForm.getSelectMember() != null)
			actionForm.setMemberId(actionForm.getSelectMember());
		else
			actionForm.setMemberId((new StringBuilder()).append(bankId)
					.append(zoneId).append(branchId).toString());
		actionForm.setSelectMember(null);
		return mapping.findForward("danSummary");
	}

	public ActionForward getPendingASFDANs(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "getPendingASFDANs", "Entered");
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = "";
		RPActionForm actionForm = (RPActionForm) form;
		HttpSession session = request.getSession(false);
		if (actionForm.getDanSummaries() != null)
			actionForm.getDanSummaries().clear();
		if (actionForm.getDanPanDetails() != null)
			actionForm.getDanPanDetails().clear();
		if (actionForm.getCgpans() != null)
			actionForm.getCgpans().clear();
		if (actionForm.getAllocatedFlags() != null)
			actionForm.getAllocatedFlags().clear();
		if (actionForm.getFirstDisbursementDates() != null)
			actionForm.getFirstDisbursementDates().clear();
		if (actionForm.getNotAllocatedReasons() != null)
			actionForm.getNotAllocatedReasons().clear();
		Log.log(5, "RPAction", "getPendingASFDANs", (new StringBuilder())
				.append("Bank Id : ").append(bankId).toString());
		Log.log(5, "RPAction", "getPendingASFDANs", (new StringBuilder())
				.append("Zone Id : ").append(zoneId).toString());
		Log.log(5, "RPAction", "getPendingASFDANs", (new StringBuilder())
				.append("Branch Id : ").append(branchId).toString());
		if (bankId.equals("0000")) {
			memberId = actionForm.getSelectMember();
			if (memberId == null || memberId.equals(""))
				memberId = actionForm.getMemberId();
			Log.log(5, "RPAction", "getPendingASFDANs", (new StringBuilder())
					.append("mliId = ").append(memberId).toString());
			if (memberId == null || memberId.equals("")) {
				Log.log(5,
						"RPAction",
						"getPendingASFDANs",
						(new StringBuilder())
								.append("Menu Target = ")
								.append(MenuOptions
										.getMenuAction("RP_ALLOCATE_PAYMENTS"))
								.toString());
				session.setAttribute("TARGET_URL",
						"selectASFMember.do?method=getPendingASFDANs");
				return mapping.findForward("memberInfo");
			}
			bankId = memberId.substring(0, 4);
			zoneId = memberId.substring(4, 8);
			branchId = memberId.substring(8, 12);
			ClaimsProcessor cpProcessor = new ClaimsProcessor();
			Vector memberIds = cpProcessor.getAllMemberIds();
			if (!memberIds.contains(memberId))
				throw new NoMemberFoundException("The Member ID does not exist");
		}
		Log.log(5,
				"RPAction",
				"getPendingASFDANs",
				(new StringBuilder())
						.append("Selected Bank Id,zone and branch ids : ")
						.append(bankId).append(",").append(zoneId).append(",")
						.append(branchId).toString());
		RpProcessor rpProcessor = new RpProcessor();
		ArrayList danSummaries = rpProcessor.displayASFDANs(bankId, zoneId,
				branchId);
		Log.log(5, "RPAction", "getPendingASFDANs", (new StringBuilder())
				.append("dan summary size : ").append(danSummaries.size())
				.toString());
		if (danSummaries.size() == 0) {
			actionForm.setSelectMember(null);
			throw new MissingDANDetailsException(
					"No DANs available for Allocation");
		}
		boolean isDanAvailable = false;
		for (int i = 0; i < danSummaries.size(); i++) {
			DANSummary danSummary = (DANSummary) danSummaries.get(i);
			if (danSummary.getAmountDue() == danSummary.getAmountPaid())
				continue;
			isDanAvailable = true;
			break;
		}

		if (!isDanAvailable) {
			actionForm.setSelectMember(null);
			throw new MissingDANDetailsException(
					"No DANs available for Allocation");
		}
		actionForm.setDanSummaries(danSummaries);
		actionForm.setBankId(bankId);
		actionForm.setZoneId(zoneId);
		actionForm.setBranchId(branchId);
		Log.log(4, "RPAction", "getPendingASFDANs", "Exited");
		if (actionForm.getSelectMember() != null)
			actionForm.setMemberId(actionForm.getSelectMember());
		else
			actionForm.setMemberId((new StringBuilder()).append(bankId)
					.append(zoneId).append(branchId).toString());
		actionForm.setSelectMember(null);
		return mapping.findForward("danSummary3");
	}

	public ActionForward getPendingExpiredASFDANs(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "getPendingExpiredASFDANs", "Entered");
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = "";
		RPActionForm actionForm = (RPActionForm) form;
		HttpSession session = request.getSession(false);
		if (actionForm.getDanSummaries() != null)
			actionForm.getDanSummaries().clear();
		if (actionForm.getDanPanDetails() != null)
			actionForm.getDanPanDetails().clear();
		if (actionForm.getCgpans() != null)
			actionForm.getCgpans().clear();
		if (actionForm.getAllocatedFlags() != null)
			actionForm.getAllocatedFlags().clear();
		if (actionForm.getFirstDisbursementDates() != null)
			actionForm.getFirstDisbursementDates().clear();
		if (actionForm.getNotAllocatedReasons() != null)
			actionForm.getNotAllocatedReasons().clear();
		Log.log(5, "RPAction", "getPendingExpiredASFDANs",
				(new StringBuilder()).append("Bank Id : ").append(bankId)
						.toString());
		Log.log(5, "RPAction", "getPendingExpiredASFDANs",
				(new StringBuilder()).append("Zone Id : ").append(zoneId)
						.toString());
		Log.log(5, "RPAction", "getPendingExpiredASFDANs",
				(new StringBuilder()).append("Branch Id : ").append(branchId)
						.toString());
		if (bankId.equals("0000")) {
			memberId = actionForm.getSelectMember();
			if (memberId == null || memberId.equals(""))
				memberId = actionForm.getMemberId();
			Log.log(5, "RPAction", "getPendingExpiredASFDANs",
					(new StringBuilder()).append("mliId = ").append(memberId)
							.toString());
			if (memberId == null || memberId.equals("")) {
				Log.log(5,
						"RPAction",
						"getPendingExpiredASFDANs",
						(new StringBuilder())
								.append("Menu Target = ")
								.append(MenuOptions
										.getMenuAction("RP_ALLOCATE_PAYMENTS"))
								.toString());
				session.setAttribute("TARGET_URL",
						"selectASFMemberForExpired.do?method=getPendingExpiredASFDANs");
				return mapping.findForward("memberInfo");
			}
			bankId = memberId.substring(0, 4);
			zoneId = memberId.substring(4, 8);
			branchId = memberId.substring(8, 12);
			ClaimsProcessor cpProcessor = new ClaimsProcessor();
			Vector memberIds = cpProcessor.getAllMemberIds();
			if (!memberIds.contains(memberId))
				throw new NoMemberFoundException("The Member ID does not exist");
		}
		Log.log(5,
				"RPAction",
				"getPendingExpiredASFDANs",
				(new StringBuilder())
						.append("Selected Bank Id,zone and branch ids : ")
						.append(bankId).append(",").append(zoneId).append(",")
						.append(branchId).toString());
		RpProcessor rpProcessor = new RpProcessor();
		ArrayList danSummaries = rpProcessor.displayASFDANsforExpired(bankId,
				zoneId, branchId);
		Log.log(5,
				"RPAction",
				"getPendingExpiredASFDANs",
				(new StringBuilder()).append("dan summary size : ")
						.append(danSummaries.size()).toString());
		if (danSummaries.size() == 0) {
			actionForm.setSelectMember(null);
			throw new MissingDANDetailsException(
					"No DANs available for Allocation");
		}
		boolean isDanAvailable = false;
		for (int i = 0; i < danSummaries.size(); i++) {
			DANSummary danSummary = (DANSummary) danSummaries.get(i);
			if (danSummary.getAmountDue() == danSummary.getAmountPaid())
				continue;
			isDanAvailable = true;
			break;
		}

		if (!isDanAvailable) {
			actionForm.setSelectMember(null);
			throw new MissingDANDetailsException(
					"No DANs available for Allocation");
		}
		actionForm.setDanSummaries(danSummaries);
		actionForm.setBankId(bankId);
		actionForm.setZoneId(zoneId);
		actionForm.setBranchId(branchId);
		Log.log(4, "RPAction", "getPendingExpiredASFDANs", "Exited");
		if (actionForm.getSelectMember() != null)
			actionForm.setMemberId(actionForm.getSelectMember());
		else
			actionForm.setMemberId((new StringBuilder()).append(bankId)
					.append(zoneId).append(branchId).toString());
		actionForm.setSelectMember(null);
		return mapping.findForward("danSummary3");
	}

	public ActionForward getPendingGFDANs(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "getPendingGFDANs", "Entered");
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = "";
		RPActionForm actionForm = (RPActionForm) form;
		HttpSession session = request.getSession(false);
		if (actionForm.getDanSummaries() != null)
			actionForm.getDanSummaries().clear();
		if (actionForm.getDanPanDetails() != null)
			actionForm.getDanPanDetails().clear();
		if (actionForm.getCgpans() != null)
			actionForm.getCgpans().clear();
		if (actionForm.getAllocatedFlags() != null)
			actionForm.getAllocatedFlags().clear();
		if (actionForm.getFirstDisbursementDates() != null)
			actionForm.getFirstDisbursementDates().clear();
		if (actionForm.getNotAllocatedReasons() != null)
			actionForm.getNotAllocatedReasons().clear();
		if (actionForm.getAppropriatedFlags() != null)
			actionForm.getAppropriatedFlags().clear();
		Log.log(5, "RPAction", "getPendingGFDANs", (new StringBuilder())
				.append("Bank Id : ").append(bankId).toString());
		Log.log(5, "RPAction", "getPendingGFDANs", (new StringBuilder())
				.append("Zone Id : ").append(zoneId).toString());
		Log.log(5, "RPAction", "getPendingGFDANs", (new StringBuilder())
				.append("Branch Id : ").append(branchId).toString());
		if (bankId.equals("0000")) {
			memberId = actionForm.getSelectMember();
			if (memberId == null || memberId.equals(""))
				memberId = actionForm.getMemberId();
			Log.log(5, "RPAction", "getPendingGFDANs", (new StringBuilder())
					.append("mliId = ").append(memberId).toString());
			if (memberId == null || memberId.equals("")) {
				session.setAttribute("TARGET_URL",
						"selectGFMember.do?method=getPendingGFDANs");
				return mapping.findForward("memberInfo");
			}
			bankId = memberId.substring(0, 4);
			zoneId = memberId.substring(4, 8);
			branchId = memberId.substring(8, 12);
			ClaimsProcessor cpProcessor = new ClaimsProcessor();
			Vector memberIds = cpProcessor.getAllMemberIds();
			if (!memberIds.contains(memberId))
				throw new NoMemberFoundException("The Member ID does not exist");
		}
		Log.log(5,
				"RPAction",
				"getPendingGFDANs",
				(new StringBuilder())
						.append("Selected Bank Id,zone and branch ids : ")
						.append(bankId).append(",").append(zoneId).append(",")
						.append(branchId).toString());
		RpProcessor rpProcessor = new RpProcessor();
		ArrayList danSummaries = rpProcessor.displayGFDANs(bankId, zoneId,
				branchId);
		Log.log(5, "RPAction", "getPendingGFDANs", (new StringBuilder())
				.append("dan summary size : ").append(danSummaries.size())
				.toString());
		if (danSummaries.size() == 0) {
			actionForm.setSelectMember(null);
			throw new MissingDANDetailsException(
					"No DANs available for Allocation");
		}
		boolean isDanAvailable = false;
		for (int i = 0; i < danSummaries.size(); i++) {
			DANSummary danSummary = (DANSummary) danSummaries.get(i);
			if (danSummary.getAmountDue() == danSummary.getAmountPaid())
				continue;
			isDanAvailable = true;
			break;
		}

		if (!isDanAvailable) {
			actionForm.setSelectMember(null);
			throw new MissingDANDetailsException(
					"No DANs available for Allocation");
		}
		actionForm.setDanSummaries(danSummaries);
		actionForm.setBankId(bankId);
		actionForm.setZoneId(zoneId);
		actionForm.setBranchId(branchId);
		Log.log(4, "RPAction", "getPendingGFDANs", "Exited");
		if (actionForm.getSelectMember() != null)
			actionForm.setMemberId(actionForm.getSelectMember());
		else
			actionForm.setMemberId((new StringBuilder()).append(bankId)
					.append(zoneId).append(branchId).toString());
		actionForm.setSelectMember(null);
		return mapping.findForward("danSummary2");
	}

	public ActionForward getNEFTPendingGFDANs(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "getNEFTPendingGFDANs", "Entered");
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = "";
		RPActionForm actionForm = (RPActionForm) form;
		HttpSession session = request.getSession(false);
		if (actionForm.getDanSummaries() != null)
			actionForm.getDanSummaries().clear();
		if (actionForm.getDanPanDetails() != null)
			actionForm.getDanPanDetails().clear();
		if (actionForm.getCgpans() != null)
			actionForm.getCgpans().clear();
		if (actionForm.getAllocatedFlags() != null)
			actionForm.getAllocatedFlags().clear();
		if (actionForm.getFirstDisbursementDates() != null)
			actionForm.getFirstDisbursementDates().clear();
		if (actionForm.getNotAllocatedReasons() != null)
			actionForm.getNotAllocatedReasons().clear();
		Log.log(5, "RPAction", "getNEFTPendingGFDANs", (new StringBuilder())
				.append("Bank Id : ").append(bankId).toString());
		Log.log(5, "RPAction", "getNEFTPendingGFDANs", (new StringBuilder())
				.append("Zone Id : ").append(zoneId).toString());
		Log.log(5, "RPAction", "getNEFTPendingGFDANs", (new StringBuilder())
				.append("Branch Id : ").append(branchId).toString());
		if (bankId.equals("0000")) {
			memberId = actionForm.getSelectMember();
			if (memberId == null || memberId.equals(""))
				memberId = actionForm.getMemberId();
			Log.log(5, "RPAction", "getNEFTPendingGFDANs",
					(new StringBuilder()).append("mliId = ").append(memberId)
							.toString());
			if (memberId == null || memberId.equals("")) {
				session.setAttribute("TARGET_URL",
						"selectGFMemberNew.do?method=getNEFTPendingGFDANs");
				return mapping.findForward("memberInfoNew");
			}
			bankId = memberId.substring(0, 4);
			zoneId = memberId.substring(4, 8);
			branchId = memberId.substring(8, 12);
			ClaimsProcessor cpProcessor = new ClaimsProcessor();
			Vector memberIds = cpProcessor.getAllMemberIds();
			if (!memberIds.contains(memberId))
				throw new NoMemberFoundException("The Member ID does not exist");
		}
		Log.log(5,
				"RPAction",
				"getNEFTPendingGFDANs",
				(new StringBuilder())
						.append("Selected Bank Id,zone and branch ids : ")
						.append(bankId).append(",").append(zoneId).append(",")
						.append(branchId).toString());
		RpProcessor rpProcessor = new RpProcessor();
		ArrayList danSummaries = rpProcessor.displayGFDANs(bankId, zoneId,
				branchId);
		Log.log(5, "RPAction", "getNEFTPendingGFDANs", (new StringBuilder())
				.append("dan summary size : ").append(danSummaries.size())
				.toString());
		if (danSummaries.size() == 0) {
			actionForm.setSelectMember(null);
			throw new MissingDANDetailsException(
					"No DANs available for Allocation");
		}
		boolean isDanAvailable = false;
		for (int i = 0; i < danSummaries.size(); i++) {
			DANSummary danSummary = (DANSummary) danSummaries.get(i);
			if (danSummary.getAmountDue() == danSummary.getAmountPaid())
				continue;
			isDanAvailable = true;
			break;
		}

		if (!isDanAvailable) {
			actionForm.setSelectMember(null);
			throw new MissingDANDetailsException(
					"No DANs available for Allocation");
		}
		actionForm.setDanSummaries(danSummaries);
		actionForm.setBankId(bankId);
		actionForm.setZoneId(zoneId);
		actionForm.setBranchId(branchId);
		Log.log(4, "RPAction", "getNEFTPendingGFDANs", "Exited");
		if (actionForm.getSelectMember() != null)
			actionForm.setMemberId(actionForm.getSelectMember());
		else
			actionForm.setMemberId((new StringBuilder()).append(bankId)
					.append(zoneId).append(branchId).toString());
		actionForm.setSelectMember(null);
		return mapping.findForward("neftdanSummary");
	}

	public ActionForward getPendingSFDANs(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "getPendingDANs", "Entered");
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = "";
		RPActionForm actionForm = (RPActionForm) form;
		HttpSession session = request.getSession(false);
		if (actionForm.getDanSummaries() != null)
			actionForm.getDanSummaries().clear();
		if (actionForm.getDanPanDetails() != null)
			actionForm.getDanPanDetails().clear();
		if (actionForm.getCgpans() != null)
			actionForm.getCgpans().clear();
		if (actionForm.getAllocatedFlags() != null)
			actionForm.getAllocatedFlags().clear();
		if (actionForm.getFirstDisbursementDates() != null)
			actionForm.getFirstDisbursementDates().clear();
		if (actionForm.getNotAllocatedReasons() != null)
			actionForm.getNotAllocatedReasons().clear();
		Log.log(5, "RPAction", "getPendingDANs",
				(new StringBuilder()).append("Bank Id : ").append(bankId)
						.toString());
		Log.log(5, "RPAction", "getPendingDANs",
				(new StringBuilder()).append("Zone Id : ").append(zoneId)
						.toString());
		Log.log(5, "RPAction", "getPendingDANs",
				(new StringBuilder()).append("Branch Id : ").append(branchId)
						.toString());
		if (bankId.equals("0000")) {
			memberId = actionForm.getSelectMember();
			if (memberId == null || memberId.equals(""))
				memberId = actionForm.getMemberId();
			Log.log(5, "RPAction", "getPendingSFDANs", (new StringBuilder())
					.append("mliId = ").append(memberId).toString());
			if (memberId == null || memberId.equals("")) {
				session.setAttribute("TARGET_URL",
						"selectMember1.do?method=getPendingSFDANs");
				return mapping.findForward("memberInfo");
			}
			bankId = memberId.substring(0, 4);
			zoneId = memberId.substring(4, 8);
			branchId = memberId.substring(8, 12);
			ClaimsProcessor cpProcessor = new ClaimsProcessor();
			Vector memberIds = cpProcessor.getAllMemberIds();
			if (!memberIds.contains(memberId))
				throw new NoMemberFoundException("The Member ID does not exist");
		}
		Log.log(5,
				"RPAction",
				"getPendingDANs",
				(new StringBuilder())
						.append("Selected Bank Id,zone and branch ids : ")
						.append(bankId).append(",").append(zoneId).append(",")
						.append(branchId).toString());
		RpProcessor rpProcessor = new RpProcessor();
		ArrayList danSummaries = rpProcessor.displaySFDANs(bankId, zoneId,
				branchId);
		Log.log(5,
				"RPAction",
				"getPendingDANs",
				(new StringBuilder()).append("dan summary size : ")
						.append(danSummaries.size()).toString());
		if (danSummaries.size() == 0) {
			actionForm.setSelectMember(null);
			throw new MissingDANDetailsException(
					"No DANs available for Allocation");
		}
		boolean isDanAvailable = false;
		for (int i = 0; i < danSummaries.size(); i++) {
			DANSummary danSummary = (DANSummary) danSummaries.get(i);
			if (danSummary.getAmountDue() == danSummary.getAmountPaid())
				continue;
			isDanAvailable = true;
			break;
		}

		if (!isDanAvailable) {
			actionForm.setSelectMember(null);
			throw new MissingDANDetailsException(
					"No DANs available for Allocation");
		}
		actionForm.setDanSummaries(danSummaries);
		actionForm.setBankId(bankId);
		actionForm.setZoneId(zoneId);
		actionForm.setBranchId(branchId);
		Log.log(4, "RPAction", "getPendingDANs", "Exited");
		if (actionForm.getSelectMember() != null)
			actionForm.setMemberId(actionForm.getSelectMember());
		else
			actionForm.setMemberId((new StringBuilder()).append(bankId)
					.append(zoneId).append(branchId).toString());
		actionForm.setSelectMember(null);
		return mapping.findForward("danSummary");
	}

	public ActionForward getASFPANDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "getASFPANDetails", "Entered");
		RPActionForm actionForm = (RPActionForm) form;
		Map cgpans = actionForm.getCgpans();
		Set cgpanSet = cgpans.keySet();
		Iterator cgpanIterator = cgpanSet.iterator();
		Log.log(5, "RPAction", "getASFPANDetails", "CGPANS selected ");
		String key;
		for (; cgpanIterator.hasNext(); Log.log(5, "RPAction",
				"getASFPANDetails", (new StringBuilder()).append("key,value ")
						.append(key).append(",").append(cgpans.get(key))
						.toString()))
			key = (String) cgpanIterator.next();

		Log.log(5, "RPAction", "getASFPANDetails", (new StringBuilder())
				.append("Cgpan map size ").append(cgpans.size()).toString());
		String danNo = actionForm.getDanNo();
		Log.log(4, "RPAction", "getASFPANDetails", (new StringBuilder())
				.append("On entering, DAN no: ").append(danNo).toString());
		Log.log(4, "RPAction", "getASFPANDetails", (new StringBuilder())
				.append("No Session: DAN no : ").append(danNo).toString());
		RpProcessor rpProcessor = new RpProcessor();
		ArrayList returnList = rpProcessor.displayCGPANs(danNo);
		ArrayList panDetails = (ArrayList) returnList.get(0);
		ArrayList allocatedPanDetails = (ArrayList) returnList.get(1);
		Log.log(4,
				"RPAction",
				"getASFPANDetails",
				(new StringBuilder())
						.append("No Session: No. of PAN details : ")
						.append(panDetails.size()).toString());
		String allocatedFlag = (String) actionForm.getAllocatedFlag(danNo
				.replace('.', '_'));
		Log.log(4, "RPAction", "getASFPANDetails", (new StringBuilder())
				.append("flag ").append(allocatedFlag).toString());
		Map allocatedFlags = actionForm.getAllocatedFlags();
		if (allocatedFlag != null && allocatedFlag.equalsIgnoreCase(danNo)) {
			for (int i = 0; i < panDetails.size(); i++) {
				AllocationDetail allocationDetail = (AllocationDetail) panDetails
						.get(i);
				key = (new StringBuilder()).append(danNo.replace('.', '_'))
						.append("-").append(allocationDetail.getCgpan())
						.toString();
				allocatedFlags.put(key, "Y");
			}

		}
		actionForm.setAllocatedFlags(allocatedFlags);
		Log.log(4, "RPAction", "getASFPANDetails", (new StringBuilder())
				.append("After session validation : ")
				.append(panDetails.size()).toString());
		actionForm.setDanPanDetail(danNo, panDetails);
		actionForm.setPanDetails(panDetails);
		actionForm.setAllocatedPanDetails(allocatedPanDetails);
		return mapping.findForward("asfpanDetails");
	}

	public ActionForward getExpiredASFPANDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "getExpiredASFPANDetails", "Entered");
		RPActionForm actionForm = (RPActionForm) form;
		Map cgpans = actionForm.getCgpans();
		Set cgpanSet = cgpans.keySet();
		Iterator cgpanIterator = cgpanSet.iterator();
		Log.log(5, "RPAction", "getExpiredASFPANDetails", "CGPANS selected ");
		String key;
		for (; cgpanIterator.hasNext(); Log.log(
				5,
				"RPAction",
				"getExpiredASFPANDetails",
				(new StringBuilder()).append("key,value ").append(key)
						.append(",").append(cgpans.get(key)).toString()))
			key = (String) cgpanIterator.next();

		Log.log(5, "RPAction", "getExpiredASFPANDetails", (new StringBuilder())
				.append("Cgpan map size ").append(cgpans.size()).toString());
		String danNo = actionForm.getDanNo();
		Log.log(4, "RPAction", "getExpiredASFPANDetails", (new StringBuilder())
				.append("On entering, DAN no: ").append(danNo).toString());
		Log.log(4, "RPAction", "getExpiredASFPANDetails", (new StringBuilder())
				.append("No Session: DAN no : ").append(danNo).toString());
		RpProcessor rpProcessor = new RpProcessor();
		ArrayList returnList = rpProcessor.displayCGPANs(danNo);
		ArrayList panDetails = (ArrayList) returnList.get(0);
		ArrayList allocatedPanDetails = (ArrayList) returnList.get(1);
		Log.log(4,
				"RPAction",
				"getExpiredASFPANDetails",
				(new StringBuilder())
						.append("No Session: No. of PAN details : ")
						.append(panDetails.size()).toString());
		String allocatedFlag = (String) actionForm.getAllocatedFlag(danNo
				.replace('.', '_'));
		Log.log(4, "RPAction", "getExpiredASFPANDetails", (new StringBuilder())
				.append("flag ").append(allocatedFlag).toString());
		Map allocatedFlags = actionForm.getAllocatedFlags();
		if (allocatedFlag != null && allocatedFlag.equalsIgnoreCase(danNo)) {
			for (int i = 0; i < panDetails.size(); i++) {
				AllocationDetail allocationDetail = (AllocationDetail) panDetails
						.get(i);
				key = (new StringBuilder()).append(danNo.replace('.', '_'))
						.append("-").append(allocationDetail.getCgpan())
						.toString();
				allocatedFlags.put(key, "Y");
			}

		}
		actionForm.setAllocatedFlags(allocatedFlags);
		Log.log(4, "RPAction", "getExpiredASFPANDetails", (new StringBuilder())
				.append("After session validation : ")
				.append(panDetails.size()).toString());
		actionForm.setDanPanDetail(danNo, panDetails);
		actionForm.setPanDetails(panDetails);
		actionForm.setAllocatedPanDetails(allocatedPanDetails);
		return mapping.findForward("asfpanDetails");
	}

	public ActionForward getGFPANDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "getGFPANDetails", "Entered");
		RPActionForm actionForm = (RPActionForm) form;
		Map cgpans = actionForm.getCgpans();
		Set cgpanSet = cgpans.keySet();
		Iterator cgpanIterator = cgpanSet.iterator();
		Log.log(5, "RPAction", "getGFPANDetails", "CGPANS selected ");
		String key;
		for (; cgpanIterator.hasNext(); Log.log(5, "RPAction",
				"getGFPANDetails", (new StringBuilder()).append("key,value ")
						.append(key).append(",").append(cgpans.get(key))
						.toString()))
			key = (String) cgpanIterator.next();

		Log.log(5,
				"RPAction",
				"getGFPANDetails",
				(new StringBuilder()).append("Cgpan map size ")
						.append(cgpans.size()).toString());
		String danNo = actionForm.getDanNo();
		Log.log(4,
				"RPAction",
				"getGFPANDetails",
				(new StringBuilder()).append("On entering, DAN no: ")
						.append(danNo).toString());
		Log.log(4,
				"RPAction",
				"getGFPANDetails",
				(new StringBuilder()).append("No Session: DAN no : ")
						.append(danNo).toString());
		RpProcessor rpProcessor = new RpProcessor();
		ArrayList returnList = rpProcessor.displayCGPANs(danNo);
		ArrayList panDetails = (ArrayList) returnList.get(0);
		ArrayList allocatedPanDetails = (ArrayList) returnList.get(1);
		Log.log(4,
				"RPAction",
				"getGFPANDetails",
				(new StringBuilder())
						.append("No Session: No. of PAN details : ")
						.append(panDetails.size()).toString());
		String allocatedFlag = (String) actionForm.getAllocatedFlag(danNo
				.replace('.', '_'));
		Log.log(4, "RPAction", "getGFPANDetails",
				(new StringBuilder()).append("flag ").append(allocatedFlag)
						.toString());
		Map allocatedFlags = actionForm.getAllocatedFlags();
		if (allocatedFlag != null && allocatedFlag.equalsIgnoreCase(danNo)) {
			for (int i = 0; i < panDetails.size(); i++) {
				AllocationDetail allocationDetail = (AllocationDetail) panDetails
						.get(i);
				key = (new StringBuilder()).append(danNo.replace('.', '_'))
						.append("-").append(allocationDetail.getCgpan())
						.toString();
				allocatedFlags.put(key, "Y");
			}

		}
		actionForm.setAllocatedFlags(allocatedFlags);
		Log.log(4, "RPAction", "getGFPANDetails",
				(new StringBuilder()).append("After session validation : ")
						.append(panDetails.size()).toString());
		actionForm.setDanPanDetail(danNo, panDetails);
		actionForm.setPanDetails(panDetails);
		actionForm.setAllocatedPanDetails(allocatedPanDetails);
		return mapping.findForward("gfpanDetails");
	}

	public ActionForward getPANDetails(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Log.log(4, "RPAction", "getPANDetails", "Entered");
		RPActionForm actionForm = (RPActionForm) form;
		Map cgpans = actionForm.getCgpans();
		Set cgpanSet = cgpans.keySet();
		Iterator cgpanIterator = cgpanSet.iterator();
		Log.log(5, "RPAction", "getPANDetails", "CGPANS selected ");
		String key;
		for (; cgpanIterator.hasNext(); Log.log(
				5,
				"RPAction",
				"getPANDetails",
				(new StringBuilder()).append("key,value ").append(key)
						.append(",").append(cgpans.get(key)).toString()))
			key = (String) cgpanIterator.next();

		Log.log(5,
				"RPAction",
				"getPANDetails",
				(new StringBuilder()).append("Cgpan map size ")
						.append(cgpans.size()).toString());
		String danNo = actionForm.getDanNo();
		Log.log(4,
				"RPAction",
				"getPANDetails",
				(new StringBuilder()).append("On entering, DAN no: ")
						.append(danNo).toString());
		Log.log(4,
				"RPAction",
				"getPANDetails",
				(new StringBuilder()).append("No Session: DAN no : ")
						.append(danNo).toString());
		RpProcessor rpProcessor = new RpProcessor();
		ArrayList returnList = rpProcessor.displayCGPANs(danNo);
		ArrayList panDetails = (ArrayList) returnList.get(0);
		ArrayList allocatedPanDetails = (ArrayList) returnList.get(1);
		Log.log(4,
				"RPAction",
				"getPANDetails",
				(new StringBuilder())
						.append("No Session: No. of PAN details : ")
						.append(panDetails.size()).toString());
		String allocatedFlag = (String) actionForm.getAllocatedFlag(danNo
				.replace('.', '_'));
		Log.log(4, "RPAction", "getPANDetails",
				(new StringBuilder()).append("flag ").append(allocatedFlag)
						.toString());
		Map allocatedFlags = actionForm.getAllocatedFlags();
		if (allocatedFlag != null && allocatedFlag.equalsIgnoreCase(danNo)) {
			for (int i = 0; i < panDetails.size(); i++) {
				AllocationDetail allocationDetail = (AllocationDetail) panDetails
						.get(i);
				key = (new StringBuilder()).append(danNo.replace('.', '_'))
						.append("-").append(allocationDetail.getCgpan())
						.toString();
				allocatedFlags.put(key, "Y");
			}

		}
		actionForm.setAllocatedFlags(allocatedFlags);
		Log.log(4, "RPAction", "getPANDetails",
				(new StringBuilder()).append("After session validation : ")
						.append(panDetails.size()).toString());
		actionForm.setDanPanDetail(danNo, panDetails);
		actionForm.setPanDetails(panDetails);
		actionForm.setAllocatedPanDetails(allocatedPanDetails);
		return mapping.findForward("panDetails");
	}

	public ActionForward getSFPANDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "getSFPANDetails", "Entered");
		RPActionForm actionForm = (RPActionForm) form;
		Map cgpans = actionForm.getCgpans();
		Set cgpanSet = cgpans.keySet();
		Iterator cgpanIterator = cgpanSet.iterator();
		Log.log(5, "RPAction", "getSFPANDetails", "CGPANS selected ");
		String key;
		for (; cgpanIterator.hasNext(); Log.log(
				5,
				"RPAction",
				"getPANDetails",
				(new StringBuilder()).append("key,value ").append(key)
						.append(",").append(cgpans.get(key)).toString()))
			key = (String) cgpanIterator.next();

		Log.log(5,
				"RPAction",
				"getPANDetails",
				(new StringBuilder()).append("Cgpan map size ")
						.append(cgpans.size()).toString());
		String danNo = actionForm.getDanNo();
		Log.log(4,
				"RPAction",
				"getPANDetails",
				(new StringBuilder()).append("On entering, DAN no: ")
						.append(danNo).toString());
		Log.log(4,
				"RPAction",
				"getPANDetails",
				(new StringBuilder()).append("No Session: DAN no : ")
						.append(danNo).toString());
		RpProcessor rpProcessor = new RpProcessor();
		ArrayList returnList = rpProcessor.displaySFCGPANs(danNo);
		ArrayList panDetails = (ArrayList) returnList.get(0);
		ArrayList allocatedPanDetails = (ArrayList) returnList.get(1);
		Log.log(4,
				"RPAction",
				"getPANDetails",
				(new StringBuilder())
						.append("No Session: No. of PAN details : ")
						.append(panDetails.size()).toString());
		String allocatedFlag = (String) actionForm.getAllocatedFlag(danNo
				.replace('.', '_'));
		Log.log(4, "RPAction", "getPANDetails",
				(new StringBuilder()).append("flag ").append(allocatedFlag)
						.toString());
		Map allocatedFlags = actionForm.getAllocatedFlags();
		if (allocatedFlag != null && allocatedFlag.equalsIgnoreCase(danNo)) {
			for (int i = 0; i < panDetails.size(); i++) {
				AllocationDetail allocationDetail = (AllocationDetail) panDetails
						.get(i);
				key = (new StringBuilder()).append(danNo.replace('.', '_'))
						.append("-").append(allocationDetail.getCgpan())
						.toString();
				allocatedFlags.put(key, "Y");
			}

		}
		actionForm.setAllocatedFlags(allocatedFlags);
		Log.log(4, "RPAction", "getPANDetails",
				(new StringBuilder()).append("After session validation : ")
						.append(panDetails.size()).toString());
		actionForm.setDanPanDetail(danNo, panDetails);
		actionForm.setPanDetails(panDetails);
		actionForm.setAllocatedPanDetails(allocatedPanDetails);
		return mapping.findForward("panDetails");
	}

	public ActionForward deAllocatePayments(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "deAllocatePayments", "Entered");
		RPActionForm actionForm = (RPActionForm) form;
		RpProcessor rpProcessor = new RpProcessor();
		User user = getUserInformation(request);
		String userId = user.getUserId();
		String paymentId = null;
		paymentId = actionForm.getPaymentId();
		if (paymentId != null) {
			rpProcessor.deAllocatePayments(paymentId, userId);
			actionForm.setPaymentId(null);
		}
		request.setAttribute("message",
				(new StringBuilder()).append("Payment ID : ").append(paymentId)
						.append(" Cancelled Successfully:").toString());
		Log.log(4, "RPAction", "deAllocatePayments", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward deAllocatePaymentsInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "deAllocatePaymentsInput", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		Log.log(4, "RPAction", "deAllocatePaymentsInput", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward neftAllocatePaymentsInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "RPAction", "neftAllocatePaymentsInput", "Entered");
		// System.out.println("neftAllocatePaymentsInput Entered");
		RPActionForm dynaForm = (RPActionForm) form;
		String paymentId = null;
		dynaForm.setPaymentId("");
		dynaForm.setMemberId("");

		Calendar cal = Calendar.getInstance();
		cal.set(0, 0, 0);
		Date emptyDate = cal.getTime();
		dynaForm.setPaymentDate(null);
		dynaForm.setNeftCode("");

		Log.log(Log.INFO, "RPAction", "neftAllocatePaymentsInput", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward neftAllocatePaymentsNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "RPAction", "neftAllocatePayments", "Entered");
		RPActionForm actionForm = (RPActionForm) form;
		RpDAO rpDAO = new RpDAO();
		RpProcessor rpProcessor = new RpProcessor();
		User user = getUserInformation(request);
		String userId = user.getUserId();
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();

		String loginMemberId = bankId.concat(zoneId).concat(branchId);

		String paymentId = null;
		PaymentDetails paymentDtls = null;
		paymentId = actionForm.getPaymentId();

		// System.out.println("userId:"+user.getUserId());
		// System.out.println(" paymentId:"+ paymentId);

		if (paymentId != null || paymentId != "") {

			paymentId = paymentId.trim().toUpperCase();
			String memberId = rpDAO.getMemberId(paymentId);
			if (memberId != null) {
				if (!loginMemberId.equals(memberId)) {

					paymentDtls = rpDAO.getPayInSlipDetailsForEPAY(paymentId,
							memberId);

				} else {
					throw new DatabaseException(
							"RP Number not relevant with member id-"
									+ loginMemberId
									+ ".Please check RP number.");
				}
			} else {
				throw new DatabaseException("Please check RP number.");
			}
			double allocatedAmt = paymentDtls.getInstrumentAmount();

			actionForm.setMemberId(memberId);
			actionForm.setPaymentId(paymentId);
			actionForm.setAllocatedAmt(allocatedAmt);

			actionForm.setBankName(paymentDtls.getDrawnAtBank());
			actionForm.setZoneName(paymentDtls.getPayableAt());
			actionForm.setBranchName(paymentDtls.getDrawnAtBranch());

			actionForm.setBankId(bankId);
			actionForm.setZoneId(zoneId);
			actionForm.setBranchId(branchId);
			return mapping.findForward("insertPayId");
			// return mapping.findForward("success");

		}

		Log.log(Log.INFO, "RPAction", "neftAllocatePayments", "Exited");

		return mapping.findForward("success");
		// return mapping.findForward("insertPayId");
	}

	public ActionForward neftAllocatePayments(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "neftAllocatePayments", "Entered");
		RPActionForm actionForm = (RPActionForm) form;
		RpDAO rpDAO = new RpDAO();
		RpProcessor rpProcessor = new RpProcessor();
		User user = getUserInformation(request);
		String userId = user.getUserId();
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String paymentId = null;
		paymentId = actionForm.getPaymentId();
		if (paymentId != null) {
			PaymentDetails paymentDtls = rpDAO.getPayInSlipDetails(paymentId);
			double allocatedAmt = paymentDtls.getInstrumentAmount();
			actionForm.setAllocatedAmt(allocatedAmt);
			actionForm.setPaymentId(paymentId);
			String memberId = rpDAO.getMemberId(paymentId);
			actionForm.setMemberId(memberId);
			actionForm.setBankId(bankId);
			actionForm.setZoneId(zoneId);
			actionForm.setBranchId(branchId);
			return mapping.findForward("insertPayId");
		} else {
			Log.log(4, "RPAction", "neftAllocatePayments", "Exited");
			return mapping.findForward("success");
		}
	}

	public ActionForward SubmitMappingRPandNEFT(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RPActionForm actionForm = (RPActionForm) form;
		RpDAO rpDAO = new RpDAO();
		RpProcessor rpProcessor = new RpProcessor();
		User user = getUserInformation(request);
		String userId = user.getUserId();
		String paymentId = actionForm.getPaymentId();
		String memberId = actionForm.getMemberId();
		double allocatedAmt = actionForm.getAllocatedAmt();
		String neftCode = actionForm.getNeftCode();
		String bankName = actionForm.getBankName();
		String zoneName = actionForm.getZoneName();
		String branchName = actionForm.getBranchName();
		String ifscCode = actionForm.getIfscCode();
		Date paymentDate = actionForm.getPaymentDate();
		rpDAO.afterMapRPwithNEFTDtls(memberId, paymentId, allocatedAmt,
				neftCode, bankName, zoneName, branchName, ifscCode,
				paymentDate, userId);
		request.setAttribute(
				"message",
				(new StringBuilder()).append("<b> Mapping Payment Id ")
						.append(paymentId).append(" with NEFT transaction ")
						.append(neftCode).append(" Successful").toString());
		return mapping.findForward("success");
	}

	public ActionForward modifyAllocatePaymentDetailInput(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "modifyAllocatePaymentDetailInput", "Entered");
		RPActionForm rpAllocationForm = (RPActionForm) form;
		String paymentId = "";
		rpAllocationForm.setPaymentId(paymentId);
		Log.log(4, "RPAction", "modifyAllocatePaymentDetailInput", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward modifyAllocatePaymentDetail(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "modifyAllocatePaymentDetail", "Entered");
		RPActionForm rpActionForm = (RPActionForm) form;
		String paymentId = rpActionForm.getPaymentId().toUpperCase();
		Log.log(5, "RPAction", "getPaymentDetailsForPayInSlip",
				(new StringBuilder()).append("paymentId ").append(paymentId)
						.toString());
		RpProcessor rpProcessor = new RpProcessor();
		PaymentDetails paymentDetails = rpProcessor.displayPayInSlip(paymentId);
		paymentDetails.setPaymentId(paymentId);
		paymentDetails.setNewInstrumentNo(paymentDetails.getInstrumentNo());
		paymentDetails.setNewInstrumentDt(paymentDetails.getInstrumentDate());
		BeanUtils.copyProperties(rpActionForm, paymentDetails);
		paymentDetails = null;
		Log.log(4, "RPAction", "modifyAllocatePaymentDetail", "Exited");
		return mapping.findForward("insertPayId");
	}

	public ActionForward afterModifyAllocatePaymentDetail(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "afterModifyAllocatePaymentDetail", "Entered");
		RPActionForm rpActionForm = (RPActionForm) form;
		RpProcessor rpProcessor = new RpProcessor();
		RpDAO rpDAO = new RpDAO();
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);
		rpDAO.afterModifyAllocatePaymentDetail(rpActionForm, user.getUserId());
		request.setAttribute(
				"message",
				(new StringBuilder()).append("Allocated Payment Id - ")
						.append(rpActionForm.getPaymentId())
						.append(" Details Modified Successfully ").toString());
		Log.log(4, "RPAction", "afterModifyAllocatePaymentDetail", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward asfallocatePayments(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RPActionForm actionForm = (RPActionForm) form;
		RpProcessor rpProcessor = new RpProcessor();
		String paymentId = "";
		String methodName = "asfallocatePayments";
		Log.log(5, "RPAction", methodName, "Entered");
		User user = getUserInformation(request);
		PaymentDetails paymentDetails = new PaymentDetails();
		String allocationType2 = actionForm.getAllocationType();
		paymentDetails.setAllocationType1(allocationType2);
		String modeOfPayment = actionForm.getModeOfPayment();
		String collectingBranch = actionForm.getCollectingBankBranch();
		Date paymentDate = actionForm.getPaymentDate();
		String instrumentNumber = actionForm.getInstrumentNo();
		Date instrumentDate = actionForm.getInstrumentDate();
		String modeOfDelivery = actionForm.getModeOfDelivery();
		double instrumentAmount = actionForm.getInstrumentAmount();
		String drawnAtBank = actionForm.getDrawnAtBank();
		String drawnAtBranch = actionForm.getDrawnAtBranch();
		String payableAt = actionForm.getPayableAt();
		String accNumber = actionForm.getAccountNumber();
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("collecting bank ")
						.append(actionForm.getCollectingBankName()).toString());
		paymentDetails.setCollectingBank(actionForm.getCollectingBankName());
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("mode of payment ")
						.append(modeOfPayment).toString());
		paymentDetails.setModeOfPayment(modeOfPayment);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("collecting branch ")
						.append(collectingBranch).toString());
		paymentDetails.setCollectingBankBranch(collectingBranch);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("payment date ")
						.append(paymentDate).toString());
		paymentDetails.setPaymentDate(paymentDate);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("instrument number ")
						.append(instrumentNumber).toString());
		paymentDetails.setInstrumentNo(instrumentNumber);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("instrument date ")
						.append(instrumentDate).toString());
		paymentDetails.setInstrumentDate(instrumentDate);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("mode of delivery ")
						.append(modeOfDelivery).toString());
		paymentDetails.setModeOfDelivery(modeOfDelivery);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("instrument amount ")
						.append(instrumentAmount).toString());
		paymentDetails.setInstrumentAmount(instrumentAmount);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("drawn at bank ")
						.append(drawnAtBank).toString());
		paymentDetails.setDrawnAtBank(drawnAtBank);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("drawn at branch ")
						.append(drawnAtBranch).toString());
		paymentDetails.setDrawnAtBranch(drawnAtBranch);
		Log.log(5, "RPAction", methodName,
				(new StringBuilder()).append("payable at ").append(payableAt)
						.toString());
		paymentDetails.setPayableAt(payableAt);
		Log.log(5, "RPAction", methodName,
				(new StringBuilder()).append("acc num ").append(accNumber)
						.toString());
		paymentDetails.setCgtsiAccNumber(accNumber);
		Map allocationFlags = actionForm.getAllocatedFlags();
		ArrayList danSummaries = actionForm.getDanSummaries();
		Map cgpans = actionForm.getCgpans();
		Set cgpansSet = cgpans.keySet();
		Map danCgpanDetails = actionForm.getDanPanDetails();
		Map notAllocatedReasons = actionForm.getNotAllocatedReasons();
		for (int i = 0; i < danSummaries.size(); i++) {
			DANSummary danSummary = (DANSummary) danSummaries.get(i);
			String danId = danSummary.getDanId();
			Log.log(5, "RPAction", methodName,
					(new StringBuilder()).append("danId ").append(danId)
							.toString());
			String shiftDanId = danId.replace('.', '_');
			Log.log(5,
					"RPAction",
					methodName,
					(new StringBuilder()).append("contains ")
							.append(danCgpanDetails.containsKey(danId))
							.toString());
			if (danCgpanDetails.containsKey(danId)) {
				ArrayList panAllocationDetails = (ArrayList) danCgpanDetails
						.get(danId);
				if (panAllocationDetails == null) {
					Log.log(5, "RPAction", methodName,
							"CGPAN details are not available. get them.");
					ArrayList totalList = rpProcessor.displayCGPANs(danId);
					panAllocationDetails = (ArrayList) totalList.get(0);
				}
				for (int j = 0; j < panAllocationDetails.size(); j++) {
					AllocationDetail allocationDetail = (AllocationDetail) panAllocationDetails
							.get(j);
					Log.log(5,
							"RPActionForm",
							"validate",
							(new StringBuilder())
									.append(" cgpan from danpandetails ")
									.append(allocationDetail.getCgpan())
									.toString());
					if (allocationDetail.getAllocatedFlag().equals("N")) {
						Log.log(5, "RPActionForm", "validate",
								" not allocated ");
						String reasons = (String) notAllocatedReasons
								.get((new StringBuilder()).append(shiftDanId)
										.append("-")
										.append(allocationDetail.getCgpan())
										.toString());
						Log.log(5,
								"RPActionForm",
								"validate",
								(new StringBuilder())
										.append(" reason for not allocated ")
										.append(reasons).toString());
						allocationDetail.setNotAllocatedReason(reasons);
					} else {
						allocationDetail.setNotAllocatedReason("");
					}
					panAllocationDetails.set(j, allocationDetail);
				}

				danCgpanDetails.put(danId, panAllocationDetails);
			}
		}

		paymentId = rpProcessor.allocateASFDAN(paymentDetails, danSummaries,
				allocationFlags, cgpans, danCgpanDetails, user);
		request.setAttribute(
				"message",
				(new StringBuilder())
						.append("Payment Allocated Successfully.<BR>Payment ID : ")
						.append(paymentId).toString());
		Log.log(5, "RPAction", methodName, "Exited");
		return mapping.findForward("success");
	}

	public ActionForward expiredasfallocatePayments(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RPActionForm actionForm = (RPActionForm) form;
		RpProcessor rpProcessor = new RpProcessor();
		String paymentId = "";
		String methodName = "expiredasfallocatePayments";
		Log.log(5, "RPAction", methodName, "Entered");
		User user = getUserInformation(request);
		PaymentDetails paymentDetails = new PaymentDetails();
		String modeOfPayment = actionForm.getModeOfPayment();
		String collectingBranch = actionForm.getCollectingBankBranch();
		Date paymentDate = actionForm.getPaymentDate();
		String instrumentNumber = actionForm.getInstrumentNo();
		Date instrumentDate = actionForm.getInstrumentDate();
		String modeOfDelivery = actionForm.getModeOfDelivery();
		double instrumentAmount = actionForm.getInstrumentAmount();
		String drawnAtBank = actionForm.getDrawnAtBank();
		String drawnAtBranch = actionForm.getDrawnAtBranch();
		String payableAt = actionForm.getPayableAt();
		String accNumber = actionForm.getAccountNumber();
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("collecting bank ")
						.append(actionForm.getCollectingBankName()).toString());
		paymentDetails.setCollectingBank(actionForm.getCollectingBankName());
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("mode of payment ")
						.append(modeOfPayment).toString());
		paymentDetails.setModeOfPayment(modeOfPayment);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("collecting branch ")
						.append(collectingBranch).toString());
		paymentDetails.setCollectingBankBranch(collectingBranch);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("payment date ")
						.append(paymentDate).toString());
		paymentDetails.setPaymentDate(paymentDate);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("instrument number ")
						.append(instrumentNumber).toString());
		paymentDetails.setInstrumentNo(instrumentNumber);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("instrument date ")
						.append(instrumentDate).toString());
		paymentDetails.setInstrumentDate(instrumentDate);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("mode of delivery ")
						.append(modeOfDelivery).toString());
		paymentDetails.setModeOfDelivery(modeOfDelivery);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("instrument amount ")
						.append(instrumentAmount).toString());
		paymentDetails.setInstrumentAmount(instrumentAmount);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("drawn at bank ")
						.append(drawnAtBank).toString());
		paymentDetails.setDrawnAtBank(drawnAtBank);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("drawn at branch ")
						.append(drawnAtBranch).toString());
		paymentDetails.setDrawnAtBranch(drawnAtBranch);
		Log.log(5, "RPAction", methodName,
				(new StringBuilder()).append("payable at ").append(payableAt)
						.toString());
		paymentDetails.setPayableAt(payableAt);
		Log.log(5, "RPAction", methodName,
				(new StringBuilder()).append("acc num ").append(accNumber)
						.toString());
		paymentDetails.setCgtsiAccNumber(accNumber);
		Map allocationFlags = actionForm.getAllocatedFlags();
		ArrayList danSummaries = actionForm.getDanSummaries();
		Map cgpans = actionForm.getCgpans();
		Set cgpansSet = cgpans.keySet();
		Map danCgpanDetails = actionForm.getDanPanDetails();
		Map notAllocatedReasons = actionForm.getNotAllocatedReasons();
		for (int i = 0; i < danSummaries.size(); i++) {
			DANSummary danSummary = (DANSummary) danSummaries.get(i);
			String danId = danSummary.getDanId();
			Log.log(5, "RPAction", methodName,
					(new StringBuilder()).append("danId ").append(danId)
							.toString());
			String shiftDanId = danId.replace('.', '_');
			Log.log(5,
					"RPAction",
					methodName,
					(new StringBuilder()).append("contains ")
							.append(danCgpanDetails.containsKey(danId))
							.toString());
			if (danCgpanDetails.containsKey(danId)) {
				ArrayList panAllocationDetails = (ArrayList) danCgpanDetails
						.get(danId);
				if (panAllocationDetails == null) {
					Log.log(5, "RPAction", methodName,
							"CGPAN details are not available. get them.");
					ArrayList totalList = rpProcessor.displayCGPANs(danId);
					panAllocationDetails = (ArrayList) totalList.get(0);
				}
				for (int j = 0; j < panAllocationDetails.size(); j++) {
					AllocationDetail allocationDetail = (AllocationDetail) panAllocationDetails
							.get(j);
					Log.log(5,
							"RPActionForm",
							"validate",
							(new StringBuilder())
									.append(" cgpan from danpandetails ")
									.append(allocationDetail.getCgpan())
									.toString());
					if (allocationDetail.getAllocatedFlag().equals("N")) {
						Log.log(5, "RPActionForm", "validate",
								" not allocated ");
						String reasons = (String) notAllocatedReasons
								.get((new StringBuilder()).append(shiftDanId)
										.append("-")
										.append(allocationDetail.getCgpan())
										.toString());
						Log.log(5,
								"RPActionForm",
								"validate",
								(new StringBuilder())
										.append(" reason for not allocated ")
										.append(reasons).toString());
						allocationDetail.setNotAllocatedReason(reasons);
					} else {
						allocationDetail.setNotAllocatedReason("");
					}
					panAllocationDetails.set(j, allocationDetail);
				}

				danCgpanDetails.put(danId, panAllocationDetails);
			}
		}

		paymentId = rpProcessor.allocateASFDAN(paymentDetails, danSummaries,
				allocationFlags, cgpans, danCgpanDetails, user);
		request.setAttribute(
				"message",
				(new StringBuilder())
						.append("Payment Allocated Successfully.<BR>Payment ID : ")
						.append(paymentId).toString());
		Log.log(5, "RPAction", methodName, "Exited");
		return mapping.findForward("success");
	}

	public ActionForward gfallocatePayments(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RPActionForm actionForm = (RPActionForm) form;
		RpProcessor rpProcessor = new RpProcessor();
		String paymentId = "";
		String methodName = "gfallocatePayments";
		Log.log(5, "RPAction", methodName, "Entered");
		User user = getUserInformation(request);
		PaymentDetails paymentDetails = new PaymentDetails();
		String allocationType = actionForm.getAllocationType();
		paymentDetails.setAllocationType1(allocationType);
		String modeOfPayment = actionForm.getModeOfPayment();
		String collectingBranch = actionForm.getCollectingBankBranch();
		Date paymentDate = actionForm.getPaymentDate();
		String instrumentNumber = actionForm.getInstrumentNo();
		Date instrumentDate = actionForm.getInstrumentDate();
		String modeOfDelivery = actionForm.getModeOfDelivery();
		double instrumentAmount = actionForm.getInstrumentAmount();
		String drawnAtBank = actionForm.getDrawnAtBank();
		String drawnAtBranch = actionForm.getDrawnAtBranch();
		String payableAt = actionForm.getPayableAt();
		String accNumber = actionForm.getAccountNumber();
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("collecting bank ")
						.append(actionForm.getCollectingBankName()).toString());
		paymentDetails.setCollectingBank(actionForm.getCollectingBankName());
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("mode of payment ")
						.append(modeOfPayment).toString());
		paymentDetails.setModeOfPayment(modeOfPayment);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("collecting branch ")
						.append(collectingBranch).toString());
		paymentDetails.setCollectingBankBranch(collectingBranch);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("payment date ")
						.append(paymentDate).toString());
		paymentDetails.setPaymentDate(paymentDate);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("instrument number ")
						.append(instrumentNumber).toString());
		paymentDetails.setInstrumentNo(instrumentNumber);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("instrument date ")
						.append(instrumentDate).toString());
		paymentDetails.setInstrumentDate(instrumentDate);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("mode of delivery ")
						.append(modeOfDelivery).toString());
		paymentDetails.setModeOfDelivery(modeOfDelivery);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("instrument amount ")
						.append(instrumentAmount).toString());
		paymentDetails.setInstrumentAmount(instrumentAmount);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("drawn at bank ")
						.append(drawnAtBank).toString());
		paymentDetails.setDrawnAtBank(drawnAtBank);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("drawn at branch ")
						.append(drawnAtBranch).toString());
		paymentDetails.setDrawnAtBranch(drawnAtBranch);
		Log.log(5, "RPAction", methodName,
				(new StringBuilder()).append("payable at ").append(payableAt)
						.toString());
		paymentDetails.setPayableAt(payableAt);
		Log.log(5, "RPAction", methodName,
				(new StringBuilder()).append("acc num ").append(accNumber)
						.toString());
		paymentDetails.setCgtsiAccNumber(accNumber);
		Map appropriatedFlags = actionForm.getAppropriatedFlags();
		Map cgpans = actionForm.getCgpans();
		Set cgpansSet = cgpans.keySet();
		Map danCgpanDetails = actionForm.getDanPanDetails();
		paymentId = rpProcessor.allocateCGDAN(paymentDetails,
				appropriatedFlags, cgpans, danCgpanDetails, user);
		HttpSession session = request.getSession(false);
		request.setAttribute(
				"message",
				(new StringBuilder())
						.append("Payment Allocated Successfully.<BR>Payment ID : ")
						.append(paymentId).toString());
		Log.log(5, "RPAction", methodName, "Exited");
		return mapping.findForward("success");
	}

	public ActionForward gfallocatePaymentsold(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RPActionForm actionForm = (RPActionForm) form;
		RpProcessor rpProcessor = new RpProcessor();
		String paymentId = "";
		String methodName = "gfallocatePayments";
		Log.log(5, "RPAction", methodName, "Entered");
		User user = getUserInformation(request);
		PaymentDetails paymentDetails = new PaymentDetails();
		String allocationType = actionForm.getAllocationType();
		paymentDetails.setAllocationType1(allocationType);
		String modeOfPayment = actionForm.getModeOfPayment();
		String collectingBranch = actionForm.getCollectingBankBranch();
		Date paymentDate = actionForm.getPaymentDate();
		String instrumentNumber = actionForm.getInstrumentNo();
		Date instrumentDate = actionForm.getInstrumentDate();
		String modeOfDelivery = actionForm.getModeOfDelivery();
		double instrumentAmount = actionForm.getInstrumentAmount();
		String drawnAtBank = actionForm.getDrawnAtBank();
		String drawnAtBranch = actionForm.getDrawnAtBranch();
		String payableAt = actionForm.getPayableAt();
		String accNumber = actionForm.getAccountNumber();
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("collecting bank ")
						.append(actionForm.getCollectingBankName()).toString());
		paymentDetails.setCollectingBank(actionForm.getCollectingBankName());
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("mode of payment ")
						.append(modeOfPayment).toString());
		paymentDetails.setModeOfPayment(modeOfPayment);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("collecting branch ")
						.append(collectingBranch).toString());
		paymentDetails.setCollectingBankBranch(collectingBranch);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("payment date ")
						.append(paymentDate).toString());
		paymentDetails.setPaymentDate(paymentDate);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("instrument number ")
						.append(instrumentNumber).toString());
		paymentDetails.setInstrumentNo(instrumentNumber);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("instrument date ")
						.append(instrumentDate).toString());
		paymentDetails.setInstrumentDate(instrumentDate);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("mode of delivery ")
						.append(modeOfDelivery).toString());
		paymentDetails.setModeOfDelivery(modeOfDelivery);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("instrument amount ")
						.append(instrumentAmount).toString());
		paymentDetails.setInstrumentAmount(instrumentAmount);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("drawn at bank ")
						.append(drawnAtBank).toString());
		paymentDetails.setDrawnAtBank(drawnAtBank);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("drawn at branch ")
						.append(drawnAtBranch).toString());
		paymentDetails.setDrawnAtBranch(drawnAtBranch);
		Log.log(5, "RPAction", methodName,
				(new StringBuilder()).append("payable at ").append(payableAt)
						.toString());
		paymentDetails.setPayableAt(payableAt);
		Log.log(5, "RPAction", methodName,
				(new StringBuilder()).append("acc num ").append(accNumber)
						.toString());
		paymentDetails.setCgtsiAccNumber(accNumber);
		Map allocationFlags = actionForm.getAllocatedFlags();
		ArrayList danSummaries = actionForm.getDanSummaries();
		Map cgpans = actionForm.getCgpans();
		Set cgpansSet = cgpans.keySet();
		Map danCgpanDetails = actionForm.getDanPanDetails();
		Map notAllocatedReasons = actionForm.getNotAllocatedReasons();
		for (int i = 0; i < danSummaries.size(); i++) {
			DANSummary danSummary = (DANSummary) danSummaries.get(i);
			String danId = danSummary.getDanId();
			Log.log(5, "RPAction", methodName,
					(new StringBuilder()).append("danId ").append(danId)
							.toString());
			String shiftDanId = danId.replace('.', '_');
			Log.log(5,
					"RPAction",
					methodName,
					(new StringBuilder()).append("contains ")
							.append(danCgpanDetails.containsKey(danId))
							.toString());
			if (danCgpanDetails.containsKey(danId)) {
				ArrayList panAllocationDetails = (ArrayList) danCgpanDetails
						.get(danId);
				if (panAllocationDetails == null) {
					Log.log(5, "RPAction", methodName,
							"CGPAN details are not available. get them.");
					ArrayList totalList = rpProcessor.displayCGPANs(danId);
					panAllocationDetails = (ArrayList) totalList.get(0);
				}
				for (int j = 0; j < panAllocationDetails.size(); j++) {
					AllocationDetail allocationDetail = (AllocationDetail) panAllocationDetails
							.get(j);
					Log.log(5,
							"RPActionForm",
							"validate",
							(new StringBuilder())
									.append(" cgpan from danpandetails ")
									.append(allocationDetail.getCgpan())
									.toString());
					if (allocationDetail.getAllocatedFlag().equals("N")) {
						Log.log(5, "RPActionForm", "validate",
								" not allocated ");
						String reasons = (String) notAllocatedReasons
								.get((new StringBuilder()).append(shiftDanId)
										.append("-")
										.append(allocationDetail.getCgpan())
										.toString());
						Log.log(5,
								"RPActionForm",
								"validate",
								(new StringBuilder())
										.append(" reason for not allocated ")
										.append(reasons).toString());
						allocationDetail.setNotAllocatedReason(reasons);
					} else {
						allocationDetail.setNotAllocatedReason("");
					}
					panAllocationDetails.set(j, allocationDetail);
				}

				danCgpanDetails.put(danId, panAllocationDetails);
			}
		}

		request.setAttribute(
				"message",
				(new StringBuilder())
						.append("Payment Allocated Successfully.<BR>Payment ID : ")
						.append(paymentId).toString());
		Log.log(5, "RPAction", methodName, "Exited");
		return mapping.findForward("success");
	}

	public ActionForward gfallocatePaymentsNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RPActionForm actionForm = (RPActionForm) form;
		RpProcessor rpProcessor = new RpProcessor();
		String paymentId = "";
		String methodName = "gfallocatePaymentsNew";
		Log.log(5, "RPAction", methodName, "Entered");
		User user = getUserInformation(request);
		PaymentDetails paymentDetails = new PaymentDetails();
		String allocationType = actionForm.getAllocationType();
		paymentDetails.setAllocationType1(allocationType);
		String modeOfPayment = actionForm.getModeOfPayment();
		String collectingBank = actionForm.getCollectingBank();
		String collectingBranch = actionForm.getCollectingBankBranch();
		String accountName = actionForm.getAccountName();
		String accNumber = actionForm.getAccountNumber();
		String ifscCode = actionForm.getIfscCode();
		Date paymentDate = actionForm.getPaymentDate();
		double allocatedAmt = actionForm.getInstrumentAmount();
		Date instrumentDate = actionForm.getPaymentDate();
		String instrumentNumber = actionForm.getInstrumentNo();
		String modeOfDelivery = actionForm.getModeOfDelivery();
		String drawnAtBank = actionForm.getDrawnAtBank();
		String drawnAtBranch = actionForm.getDrawnAtBranch();
		String payableAt = actionForm.getPayableAt();
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("collecting bank ")
						.append(actionForm.getCollectingBank()).toString());
		paymentDetails.setCollectingBank(actionForm.getCollectingBank());
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("mode of payment ")
						.append(modeOfPayment).toString());
		paymentDetails.setModeOfPayment(modeOfPayment);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("collecting branch ")
						.append(collectingBranch).toString());
		paymentDetails.setCollectingBankBranch(collectingBranch);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("payment date ")
						.append(paymentDate).toString());
		paymentDetails.setPaymentDate(paymentDate);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("instrument number ")
						.append(instrumentNumber).toString());
		paymentDetails.setInstrumentNo(instrumentNumber);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("instrument date ")
						.append(instrumentDate).toString());
		paymentDetails.setInstrumentDate(instrumentDate);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("mode of delivery ")
						.append(modeOfDelivery).toString());
		paymentDetails.setModeOfDelivery(modeOfDelivery);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("instrument amount ")
						.append(allocatedAmt).toString());
		paymentDetails.setInstrumentAmount(allocatedAmt);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("drawn at bank ")
						.append(drawnAtBank).toString());
		paymentDetails.setDrawnAtBank(drawnAtBank);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("drawn at branch ")
						.append(drawnAtBranch).toString());
		paymentDetails.setDrawnAtBranch(drawnAtBranch);
		Log.log(5, "RPAction", methodName,
				(new StringBuilder()).append("payable at ").append(payableAt)
						.toString());
		paymentDetails.setPayableAt(payableAt);
		Log.log(5, "RPAction", methodName,
				(new StringBuilder()).append("acc num ").append(accNumber)
						.toString());
		paymentDetails.setCgtsiAccNumber(accNumber);
		Map allocationFlags = actionForm.getAllocatedFlags();
		ArrayList danSummaries = actionForm.getDanSummaries();
		Map cgpans = actionForm.getCgpans();
		Set cgpansSet = cgpans.keySet();
		Map danCgpanDetails = actionForm.getDanPanDetails();
		Map notAllocatedReasons = actionForm.getNotAllocatedReasons();
		for (int i = 0; i < danSummaries.size(); i++) {
			DANSummary danSummary = (DANSummary) danSummaries.get(i);
			String danId = danSummary.getDanId();
			Log.log(5, "RPAction", methodName,
					(new StringBuilder()).append("danId ").append(danId)
							.toString());
			String shiftDanId = danId.replace('.', '_');
			Log.log(5,
					"RPAction",
					methodName,
					(new StringBuilder()).append("contains ")
							.append(danCgpanDetails.containsKey(danId))
							.toString());
			if (danCgpanDetails.containsKey(danId)) {
				ArrayList panAllocationDetails = (ArrayList) danCgpanDetails
						.get(danId);
				if (panAllocationDetails == null) {
					Log.log(5, "RPAction", methodName,
							"CGPAN details are not available. get them.");
					ArrayList totalList = rpProcessor.displayCGPANs(danId);
					panAllocationDetails = (ArrayList) totalList.get(0);
				}
				for (int j = 0; j < panAllocationDetails.size(); j++) {
					AllocationDetail allocationDetail = (AllocationDetail) panAllocationDetails
							.get(j);
					Log.log(5,
							"RPActionForm",
							"validate",
							(new StringBuilder())
									.append(" cgpan from danpandetails ")
									.append(allocationDetail.getCgpan())
									.toString());
					if (allocationDetail.getAllocatedFlag().equals("N")) {
						Log.log(5, "RPActionForm", "validate",
								" not allocated ");
						String reasons = (String) notAllocatedReasons
								.get((new StringBuilder()).append(shiftDanId)
										.append("-")
										.append(allocationDetail.getCgpan())
										.toString());
						Log.log(5,
								"RPActionForm",
								"validate",
								(new StringBuilder())
										.append(" reason for not allocated ")
										.append(reasons).toString());
						allocationDetail.setNotAllocatedReason(reasons);
					} else {
						allocationDetail.setNotAllocatedReason("");
					}
					panAllocationDetails.set(j, allocationDetail);
				}

				danCgpanDetails.put(danId, panAllocationDetails);
			}
		}

		paymentId = rpProcessor.allocateNEFTCGDAN(paymentDetails, danSummaries,
				allocationFlags, cgpans, danCgpanDetails, user);
		request.setAttribute(
				"message",
				(new StringBuilder())
						.append("Payment Allocated Successfully.<BR>Payment ID : ")
						.append(paymentId).toString());
		Log.log(5, "RPAction", methodName, "Exited");
		return mapping.findForward("success");
	}

	public ActionForward allocatePayments(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RPActionForm actionForm = (RPActionForm) form;
		RpProcessor rpProcessor = new RpProcessor();
		String paymentId = "";
		String methodName = "gfallocatePayments";
		Log.log(5, "RPAction", methodName, "Entered");
		User user = getUserInformation(request);
		PaymentDetails paymentDetails = new PaymentDetails();
		String allocationType = actionForm.getAllocationType();
		paymentDetails.setAllocationType1(allocationType);
		String modeOfPayment = actionForm.getModeOfPayment();
		String collectingBranch = actionForm.getCollectingBankBranch();
		Date paymentDate = actionForm.getPaymentDate();
		String instrumentNumber = actionForm.getInstrumentNo();
		Date instrumentDate = actionForm.getInstrumentDate();
		String modeOfDelivery = actionForm.getModeOfDelivery();
		double instrumentAmount = actionForm.getInstrumentAmount();
		String drawnAtBank = actionForm.getDrawnAtBank();
		String drawnAtBranch = actionForm.getDrawnAtBranch();
		String payableAt = actionForm.getPayableAt();
		String accNumber = actionForm.getAccountNumber();
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("collecting bank ")
						.append(actionForm.getCollectingBankName()).toString());
		paymentDetails.setCollectingBank(actionForm.getCollectingBankName());
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("mode of payment ")
						.append(modeOfPayment).toString());
		paymentDetails.setModeOfPayment(modeOfPayment);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("collecting branch ")
						.append(collectingBranch).toString());
		paymentDetails.setCollectingBankBranch(collectingBranch);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("payment date ")
						.append(paymentDate).toString());
		paymentDetails.setPaymentDate(paymentDate);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("instrument number ")
						.append(instrumentNumber).toString());
		paymentDetails.setInstrumentNo(instrumentNumber);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("instrument date ")
						.append(instrumentDate).toString());
		paymentDetails.setInstrumentDate(instrumentDate);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("mode of delivery ")
						.append(modeOfDelivery).toString());
		paymentDetails.setModeOfDelivery(modeOfDelivery);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("instrument amount ")
						.append(instrumentAmount).toString());
		paymentDetails.setInstrumentAmount(instrumentAmount);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("drawn at bank ")
						.append(drawnAtBank).toString());
		paymentDetails.setDrawnAtBank(drawnAtBank);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("drawn at branch ")
						.append(drawnAtBranch).toString());
		paymentDetails.setDrawnAtBranch(drawnAtBranch);
		Log.log(5, "RPAction", methodName,
				(new StringBuilder()).append("payable at ").append(payableAt)
						.toString());
		paymentDetails.setPayableAt(payableAt);
		Log.log(5, "RPAction", methodName,
				(new StringBuilder()).append("acc num ").append(accNumber)
						.toString());
		paymentDetails.setCgtsiAccNumber(accNumber);
		Map appropriatedFlags = actionForm.getAppropriatedFlags();
		Map cgpans = actionForm.getCgpans();
		Set cgpansSet = cgpans.keySet();
		Map danCgpanDetails = actionForm.getDanPanDetails();
		paymentId = rpProcessor.allocateCGDAN(paymentDetails,
				appropriatedFlags, cgpans, danCgpanDetails, user);
		HttpSession session = request.getSession(false);
		request.setAttribute(
				"message",
				(new StringBuilder())
						.append("Payment Allocated Successfully.<BR>Payment ID : ")
						.append(paymentId).toString());
		Log.log(5, "RPAction", methodName, "Exited");
		return mapping.findForward("success");
	}

	public ActionForward allocatePaymentsold(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RPActionForm actionForm = (RPActionForm) form;
		RpProcessor rpProcessor = new RpProcessor();
		String paymentId = "";
		String methodName = "allocatePayments";
		Log.log(5, "RPAction", methodName, "Entered");
		User user = getUserInformation(request);
		PaymentDetails paymentDetails = new PaymentDetails();
		String allocationType3 = actionForm.getAllocationType();
		paymentDetails.setAllocationType1(allocationType3);
		String modeOfPayment = actionForm.getModeOfPayment();
		String collectingBranch = actionForm.getCollectingBankBranch();
		Date paymentDate = actionForm.getPaymentDate();
		String instrumentNumber = actionForm.getInstrumentNo();
		Date instrumentDate = actionForm.getInstrumentDate();
		String modeOfDelivery = actionForm.getModeOfDelivery();
		double instrumentAmount = actionForm.getInstrumentAmount();
		String drawnAtBank = actionForm.getDrawnAtBank();
		String drawnAtBranch = actionForm.getDrawnAtBranch();
		String payableAt = actionForm.getPayableAt();
		String accNumber = actionForm.getAccountNumber();
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("collecting bank ")
						.append(actionForm.getCollectingBankName()).toString());
		paymentDetails.setCollectingBank(actionForm.getCollectingBankName());
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("mode of payment ")
						.append(modeOfPayment).toString());
		paymentDetails.setModeOfPayment(modeOfPayment);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("collecting branch ")
						.append(collectingBranch).toString());
		paymentDetails.setCollectingBankBranch(collectingBranch);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("payment date ")
						.append(paymentDate).toString());
		paymentDetails.setPaymentDate(paymentDate);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("instrument number ")
						.append(instrumentNumber).toString());
		paymentDetails.setInstrumentNo(instrumentNumber);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("instrument date ")
						.append(instrumentDate).toString());
		paymentDetails.setInstrumentDate(instrumentDate);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("mode of delivery ")
						.append(modeOfDelivery).toString());
		paymentDetails.setModeOfDelivery(modeOfDelivery);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("instrument amount ")
						.append(instrumentAmount).toString());
		paymentDetails.setInstrumentAmount(instrumentAmount);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("drawn at bank ")
						.append(drawnAtBank).toString());
		paymentDetails.setDrawnAtBank(drawnAtBank);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("drawn at branch ")
						.append(drawnAtBranch).toString());
		paymentDetails.setDrawnAtBranch(drawnAtBranch);
		Log.log(5, "RPAction", methodName,
				(new StringBuilder()).append("payable at ").append(payableAt)
						.toString());
		paymentDetails.setPayableAt(payableAt);
		Log.log(5, "RPAction", methodName,
				(new StringBuilder()).append("acc num ").append(accNumber)
						.toString());
		paymentDetails.setCgtsiAccNumber(accNumber);
		Map allocationFlags = actionForm.getAllocatedFlags();
		ArrayList danSummaries = actionForm.getDanSummaries();
		Map cgpans = actionForm.getCgpans();
		Set cgpansSet = cgpans.keySet();
		Map danCgpanDetails = actionForm.getDanPanDetails();
		Map notAllocatedReasons = actionForm.getNotAllocatedReasons();
		for (int i = 0; i < danSummaries.size(); i++) {
			DANSummary danSummary = (DANSummary) danSummaries.get(i);
			String danId = danSummary.getDanId();
			Log.log(5, "RPAction", methodName,
					(new StringBuilder()).append("danId ").append(danId)
							.toString());
			String shiftDanId = danId.replace('.', '_');
			Log.log(5,
					"RPAction",
					methodName,
					(new StringBuilder()).append("contains ")
							.append(danCgpanDetails.containsKey(danId))
							.toString());
			if (danCgpanDetails.containsKey(danId)) {
				ArrayList panAllocationDetails = (ArrayList) danCgpanDetails
						.get(danId);
				if (panAllocationDetails == null) {
					Log.log(5, "RPAction", methodName,
							"CGPAN details are not available. get them.");
					ArrayList totalList = rpProcessor.displayCGPANs(danId);
					panAllocationDetails = (ArrayList) totalList.get(0);
				}
				for (int j = 0; j < panAllocationDetails.size(); j++) {
					AllocationDetail allocationDetail = (AllocationDetail) panAllocationDetails
							.get(j);
					Log.log(5,
							"RPActionForm",
							"validate",
							(new StringBuilder())
									.append(" cgpan from danpandetails ")
									.append(allocationDetail.getCgpan())
									.toString());
					if (allocationDetail.getAllocatedFlag().equals("N")) {
						Log.log(5, "RPActionForm", "validate",
								" not allocated ");
						String reasons = (String) notAllocatedReasons
								.get((new StringBuilder()).append(shiftDanId)
										.append("-")
										.append(allocationDetail.getCgpan())
										.toString());
						Log.log(5,
								"RPActionForm",
								"validate",
								(new StringBuilder())
										.append(" reason for not allocated ")
										.append(reasons).toString());
						allocationDetail.setNotAllocatedReason(reasons);
					} else {
						allocationDetail.setNotAllocatedReason("");
					}
					panAllocationDetails.set(j, allocationDetail);
				}

				danCgpanDetails.put(danId, panAllocationDetails);
			}
		}

		paymentId = rpProcessor.allocateDAN(paymentDetails, danSummaries,
				allocationFlags, cgpans, danCgpanDetails, user);
		request.setAttribute(
				"message",
				(new StringBuilder())
						.append("Payment Allocated Successfully.<BR>Payment ID : ")
						.append(paymentId).toString());
		Log.log(5, "RPAction", methodName, "Exited");
		return mapping.findForward("success");
	}

	public ActionForward appropriateallocatePayments(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RPActionForm actionForm = (RPActionForm) form;
		RpProcessor rpProcessor = new RpProcessor();
		String paymentId = "";
		String methodName = "appropriateallocatePayments";
		Log.log(5, "RPAction", methodName, "Entered");
		User user = getUserInformation(request);
		PaymentDetails paymentDetails = new PaymentDetails();
		String modeOfPayment = actionForm.getModeOfPayment();
		String collectingBranch = actionForm.getCollectingBankBranch();
		Date paymentDate = actionForm.getPaymentDate();
		String instrumentNumber = actionForm.getInstrumentNo();
		Date instrumentDate = actionForm.getInstrumentDate();
		String modeOfDelivery = actionForm.getModeOfDelivery();
		double instrumentAmount = actionForm.getInstrumentAmount();
		String drawnAtBank = actionForm.getDrawnAtBank();
		String drawnAtBranch = actionForm.getDrawnAtBranch();
		String payableAt = actionForm.getPayableAt();
		String accNumber = actionForm.getAccountNumber();
		double realisationAmount = actionForm.getReceivedAmount();
		Date realisationDate = actionForm.getDateOfRealisation();
		String remarksforAppropriation = actionForm
				.getremarksforAppropriation();
		paymentDetails.setReceivedAmount(realisationAmount);
		paymentDetails.setRealisationDate(realisationDate);
		paymentDetails.setRemarksforAppropriation(remarksforAppropriation);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("collecting bank ")
						.append(actionForm.getCollectingBankName()).toString());
		paymentDetails.setCollectingBank(actionForm.getCollectingBankName());
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("mode of payment ")
						.append(modeOfPayment).toString());
		paymentDetails.setModeOfPayment(modeOfPayment);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("collecting branch ")
						.append(collectingBranch).toString());
		paymentDetails.setCollectingBankBranch(collectingBranch);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("payment date ")
						.append(paymentDate).toString());
		paymentDetails.setPaymentDate(paymentDate);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("instrument number ")
						.append(instrumentNumber).toString());
		paymentDetails.setInstrumentNo(instrumentNumber);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("instrument date ")
						.append(instrumentDate).toString());
		paymentDetails.setInstrumentDate(instrumentDate);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("mode of delivery ")
						.append(modeOfDelivery).toString());
		paymentDetails.setModeOfDelivery(modeOfDelivery);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("instrument amount ")
						.append(instrumentAmount).toString());
		paymentDetails.setInstrumentAmount(instrumentAmount);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("drawn at bank ")
						.append(drawnAtBank).toString());
		paymentDetails.setDrawnAtBank(drawnAtBank);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("drawn at branch ")
						.append(drawnAtBranch).toString());
		paymentDetails.setDrawnAtBranch(drawnAtBranch);
		Log.log(5, "RPAction", methodName,
				(new StringBuilder()).append("payable at ").append(payableAt)
						.toString());
		paymentDetails.setPayableAt(payableAt);
		Log.log(5, "RPAction", methodName,
				(new StringBuilder()).append("acc num ").append(accNumber)
						.toString());
		paymentDetails.setCgtsiAccNumber(accNumber);
		Map allocationFlags = actionForm.getAllocatedFlags();
		ArrayList danSummaries = actionForm.getDanSummaries();
		Map cgpans = actionForm.getCgpans();
		Set cgpansSet = cgpans.keySet();
		Map danCgpanDetails = actionForm.getDanPanDetails();
		Map notAllocatedReasons = actionForm.getNotAllocatedReasons();
		for (int i = 0; i < danSummaries.size(); i++) {
			DANSummary danSummary = (DANSummary) danSummaries.get(i);
			String danId = danSummary.getDanId();
			Log.log(5, "RPAction", methodName,
					(new StringBuilder()).append("danId ").append(danId)
							.toString());
			String shiftDanId = danId.replace('.', '_');
			Log.log(5,
					"RPAction",
					methodName,
					(new StringBuilder()).append("contains ")
							.append(danCgpanDetails.containsKey(danId))
							.toString());
			if (danCgpanDetails.containsKey(danId)) {
				ArrayList panAllocationDetails = (ArrayList) danCgpanDetails
						.get(danId);
				if (panAllocationDetails == null) {
					Log.log(5, "RPAction", methodName,
							"CGPAN details are not available. get them.");
					ArrayList totalList = rpProcessor.displaySFCGPANs(danId);
					panAllocationDetails = (ArrayList) totalList.get(0);
				}
				for (int j = 0; j < panAllocationDetails.size(); j++) {
					AllocationDetail allocationDetail = (AllocationDetail) panAllocationDetails
							.get(j);
					Log.log(5,
							"RPActionForm",
							"validate",
							(new StringBuilder())
									.append(" cgpan from danpandetails ")
									.append(allocationDetail.getCgpan())
									.toString());
					if (allocationDetail.getAllocatedFlag().equals("N")) {
						Log.log(5, "RPActionForm", "validate",
								" not allocated ");
						String reasons = (String) notAllocatedReasons
								.get((new StringBuilder()).append(shiftDanId)
										.append("-")
										.append(allocationDetail.getCgpan())
										.toString());
						Log.log(5,
								"RPActionForm",
								"validate",
								(new StringBuilder())
										.append(" reason for not allocated ")
										.append(reasons).toString());
						allocationDetail.setNotAllocatedReason(reasons);
					} else {
						allocationDetail.setNotAllocatedReason("");
					}
					panAllocationDetails.set(j, allocationDetail);
				}

				danCgpanDetails.put(danId, panAllocationDetails);
			}
		}

		paymentDetails.setRealisationDate(actionForm.getDateOfRealisation());
		paymentId = rpProcessor.appropriateallocateDAN(paymentDetails,
				danSummaries, allocationFlags, cgpans, danCgpanDetails, user);
		request.setAttribute(
				"message",
				(new StringBuilder())
						.append("Payment Allocated & Appropriated Successfully.<BR>Payment ID : ")
						.append(paymentId).toString());
		Log.log(5, "RPAction", methodName, "Exited");
		return mapping.findForward("success");
	}

	public ActionForward submitASFDANPayments(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RPActionForm actionForm = (RPActionForm) form;
		double totalAmount = 0.0D;
		Map danIds = actionForm.getDanIds();
		Map allocatedFlags = actionForm.getAllocatedFlags();
		Map cgpans = actionForm.getCgpans();
		int allocatedcount = 0;
		int testallocatecount = 0;
		Set danIdSet = danIds.keySet();
		Log.log(5, "RPAction", "submitASFDANPayments", (new StringBuilder())
				.append("Checkbox size = ").append(allocatedFlags.size())
				.toString());
		Set cgpansSet = cgpans.keySet();
		Iterator cgpanIterator = cgpansSet.iterator();
		Log.log(5, "RPAction", "submitASFDANPayments", (new StringBuilder())
				.append("Checkbox size = ").append(cgpans.size()).toString());
		String value;
		for (; cgpanIterator.hasNext(); Log.log(5, "RPAction",
				"submitASFDANPayments",
				(new StringBuilder()).append("cgpan value = ").append(value)
						.toString())) {
			String key = (String) cgpanIterator.next();
			value = (String) cgpans.get(key);
			Log.log(5, "RPAction", "submitASFDANPayments",
					(new StringBuilder()).append("cgpan key = ").append(key)
							.toString());
		}

		cgpanIterator = cgpansSet.iterator();
		ArrayList danSummaries = actionForm.getDanSummaries();
		boolean isAllocated = false;
		for (int i = 0; i < danSummaries.size(); i++) {
			DANSummary danSummary = (DANSummary) danSummaries.get(i);
			String danId = danSummary.getDanId();
			Log.log(5, "RPAction", "submitASFDANPayments",
					(new StringBuilder()).append("danId= ").append(danId)
							.toString());
			String danIdKey = danId.replace('.', '_');
			if (allocatedFlags.containsKey(danIdKey)
					&& request.getParameter((new StringBuilder())
							.append("allocatedFlag(").append(danIdKey)
							.append(")").toString()) != null) {
				allocatedcount++;
				Log.log(5,
						"RPAction",
						"submitASFDANPayments",
						(new StringBuilder()).append("danSummaries= ")
								.append(danSummaries.size()).toString());
				isAllocated = true;
				totalAmount += danSummary.getAmountDue()
						- danSummary.getAmountPaid();
				Log.log(5,
						"RPAction",
						"submitASFDANPayments",
						(new StringBuilder())
								.append("due amount ")
								.append(danSummary.getAmountDue()
										- danSummary.getAmountPaid())
								.toString());
			} else {
				Log.log(5, "RPAction", "submitASFDANPayments",
						"CGPANS are allocated ");
				ArrayList panDetails = (ArrayList) actionForm
						.getDanPanDetail(danId);
				while (cgpanIterator.hasNext()) {
					String key = (String) cgpanIterator.next();
					value = (String) cgpans.get(key);
					String cgpanPart = value.substring(value.indexOf("-") + 1,
							value.length());
					String tempKey = value.replace('.', '_');
					Log.log(5, "RPAction", "submitASFDANPayments",
							(new StringBuilder()).append("key ").append(key)
									.toString());
					Log.log(5, "RPAction", "submitASFDANPayments",
							(new StringBuilder()).append("value ")
									.append(value).toString());
					Log.log(5,
							"RPAction",
							"submitASFDANPayments",
							(new StringBuilder()).append("tempKey ")
									.append(tempKey).toString());
					if (value.startsWith(danId)
							&& allocatedFlags.get(tempKey) != null
							&& ((String) allocatedFlags.get(tempKey))
									.equals("Y")) {
						testallocatecount++;
						cgpanPart = value.substring(value.indexOf("-") + 1,
								value.length());
						isAllocated = true;
						for (int j = 0; j < panDetails.size(); j++) {
							AllocationDetail allocation = (AllocationDetail) panDetails
									.get(j);
							Log.log(5,
									"RPAction",
									"submitASFDANPayments",
									(new StringBuilder())
											.append("amount for CGPAN ")
											.append(allocation.getCgpan())
											.append(",")
											.append(allocation.getAmountDue())
											.toString());
							if (!cgpanPart.equals(allocation.getCgpan()))
								continue;
							totalAmount += allocation.getAmountDue();
							break;
						}

					}
				}
				cgpanIterator = cgpansSet.iterator();
			}
		}

		if (!isAllocated) {
			throw new MissingDANDetailsException("No Allocation made.");
		} else {
			Registration registration = new Registration();
			Log.log(5,
					"RPAction",
					"submitASFDANPayments",
					(new StringBuilder()).append("member id ")
							.append(actionForm.getMemberId()).toString());
			CollectingBank collectingBank = registration
					.getCollectingBank((new StringBuilder()).append("(")
							.append(actionForm.getMemberId()).append(")")
							.toString());
			Log.log(5,
					"RPAction",
					"submitASFDANPayments",
					(new StringBuilder()).append("collectingBank ")
							.append(collectingBank).toString());
			actionForm.setModeOfPayment("");
			actionForm.setPaymentDate(null);
			actionForm.setInstrumentNo("");
			actionForm.setInstrumentDate(null);
			actionForm.setDrawnAtBank("");
			actionForm.setDrawnAtBranch("");
			actionForm.setPayableAt("");
			IFProcessor ifProcessor = new IFProcessor();
			ArrayList instruments = ifProcessor.getInstrumentTypes("G");
			actionForm.setInstruments(instruments);
			actionForm.setCollectingBank(collectingBank.getCollectingBankId());
			actionForm.setCollectingBankName(collectingBank
					.getCollectingBankName());
			actionForm.setCollectingBankBranch(collectingBank.getBranchName());
			actionForm.setAccountNumber(collectingBank.getAccNo());
			actionForm.setInstrumentAmount(totalAmount);
			return mapping.findForward("asfpaymentDetails");
		}
	}

	public ActionForward submitExpiredASFDANPayments(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RPActionForm actionForm = (RPActionForm) form;
		double totalAmount = 0.0D;
		Map danIds = actionForm.getDanIds();
		Map allocatedFlags = actionForm.getAllocatedFlags();
		Map cgpans = actionForm.getCgpans();
		int allocatedcount = 0;
		int testallocatecount = 0;
		Set danIdSet = danIds.keySet();
		Log.log(5,
				"RPAction",
				"submitExpiredASFDANPayments",
				(new StringBuilder()).append("Checkbox size = ")
						.append(allocatedFlags.size()).toString());
		Set cgpansSet = cgpans.keySet();
		Iterator cgpanIterator = cgpansSet.iterator();
		Log.log(5,
				"RPAction",
				"submitExpiredASFDANPayments",
				(new StringBuilder()).append("Checkbox size = ")
						.append(cgpans.size()).toString());
		String value;
		for (; cgpanIterator.hasNext(); Log.log(5, "RPAction",
				"submitExpiredASFDANPayments",
				(new StringBuilder()).append("cgpan value = ").append(value)
						.toString())) {
			String key = (String) cgpanIterator.next();
			value = (String) cgpans.get(key);
			Log.log(5, "RPAction", "submitExpiredASFDANPayments",
					(new StringBuilder()).append("cgpan key = ").append(key)
							.toString());
		}

		cgpanIterator = cgpansSet.iterator();
		ArrayList danSummaries = actionForm.getDanSummaries();
		boolean isAllocated = false;
		for (int i = 0; i < danSummaries.size(); i++) {
			DANSummary danSummary = (DANSummary) danSummaries.get(i);
			String danId = danSummary.getDanId();
			Log.log(5, "RPAction", "submitExpiredASFDANPayments",
					(new StringBuilder()).append("danId= ").append(danId)
							.toString());
			String danIdKey = danId.replace('.', '_');
			if (allocatedFlags.containsKey(danIdKey)
					&& request.getParameter((new StringBuilder())
							.append("allocatedFlag(").append(danIdKey)
							.append(")").toString()) != null) {
				allocatedcount++;
				Log.log(5,
						"RPAction",
						"submitExpiredASFDANPayments",
						(new StringBuilder()).append("danSummaries= ")
								.append(danSummaries.size()).toString());
				isAllocated = true;
				totalAmount += danSummary.getAmountDue()
						- danSummary.getAmountPaid();
				Log.log(5,
						"RPAction",
						"submitExpiredASFDANPayments",
						(new StringBuilder())
								.append("due amount ")
								.append(danSummary.getAmountDue()
										- danSummary.getAmountPaid())
								.toString());
			} else {
				Log.log(5, "RPAction", "submitExpiredASFDANPayments",
						"CGPANS are allocated ");
				ArrayList panDetails = (ArrayList) actionForm
						.getDanPanDetail(danId);
				while (cgpanIterator.hasNext()) {
					String key = (String) cgpanIterator.next();
					value = (String) cgpans.get(key);
					String cgpanPart = value.substring(value.indexOf("-") + 1,
							value.length());
					String tempKey = value.replace('.', '_');
					Log.log(5, "RPAction", "submitExpiredASFDANPayments",
							(new StringBuilder()).append("key ").append(key)
									.toString());
					Log.log(5, "RPAction", "submitExpiredASFDANPayments",
							(new StringBuilder()).append("value ")
									.append(value).toString());
					Log.log(5,
							"RPAction",
							"submitExpiredASFDANPayments",
							(new StringBuilder()).append("tempKey ")
									.append(tempKey).toString());
					if (value.startsWith(danId)
							&& allocatedFlags.get(tempKey) != null
							&& ((String) allocatedFlags.get(tempKey))
									.equals("Y")) {
						testallocatecount++;
						cgpanPart = value.substring(value.indexOf("-") + 1,
								value.length());
						isAllocated = true;
						for (int j = 0; j < panDetails.size(); j++) {
							AllocationDetail allocation = (AllocationDetail) panDetails
									.get(j);
							Log.log(5,
									"RPAction",
									"submitExpiredASFDANPayments",
									(new StringBuilder())
											.append("amount for CGPAN ")
											.append(allocation.getCgpan())
											.append(",")
											.append(allocation.getAmountDue())
											.toString());
							if (!cgpanPart.equals(allocation.getCgpan()))
								continue;
							totalAmount += allocation.getAmountDue();
							break;
						}

					}
				}
				cgpanIterator = cgpansSet.iterator();
			}
		}

		if (!isAllocated) {
			throw new MissingDANDetailsException("No Allocation made.");
		} else {
			Registration registration = new Registration();
			Log.log(5,
					"RPAction",
					"submitExpiredASFDANPayments",
					(new StringBuilder()).append("member id ")
							.append(actionForm.getMemberId()).toString());
			CollectingBank collectingBank = registration
					.getCollectingBank((new StringBuilder()).append("(")
							.append(actionForm.getMemberId()).append(")")
							.toString());
			Log.log(5,
					"RPAction",
					"submitExpiredASFDANPayments",
					(new StringBuilder()).append("collectingBank ")
							.append(collectingBank).toString());
			actionForm.setModeOfPayment("");
			actionForm.setPaymentDate(null);
			actionForm.setInstrumentNo("");
			actionForm.setInstrumentDate(null);
			actionForm.setDrawnAtBank("");
			actionForm.setDrawnAtBranch("");
			actionForm.setPayableAt("");
			IFProcessor ifProcessor = new IFProcessor();
			ArrayList instruments = ifProcessor.getInstrumentTypes("G");
			actionForm.setInstruments(instruments);
			actionForm.setCollectingBank(collectingBank.getCollectingBankId());
			actionForm.setCollectingBankName(collectingBank
					.getCollectingBankName());
			actionForm.setCollectingBankBranch(collectingBank.getBranchName());
			actionForm.setAccountNumber(collectingBank.getAccNo());
			actionForm.setInstrumentAmount(totalAmount);
			return mapping.findForward("asfpaymentDetails");
		}
	}

	public ActionForward submitGFDANPayments(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RPActionForm actionForm = (RPActionForm) form;
		double totalAmount = 0.0D;
		StringTokenizer tokenizer = null;
		double tot = 0.0D;
		Map appropriatedCases = actionForm.getAppropriatedFlags();
		if (appropriatedCases.size() < 1)
			throw new MissingDANDetailsException(
					"Please select atleast one dan for allocation .");
		Set appropriatedCasesSet = appropriatedCases.keySet();
		Iterator appropriatedCasesIterator = appropriatedCasesSet.iterator();
		String token = null;
		String token1 = null;
		float total = 0.0F;
		float total2 = 0.0F;
		while (appropriatedCasesIterator.hasNext()) {
			String key = (String) appropriatedCasesIterator.next();
			for (tokenizer = new StringTokenizer(key, "#"); tokenizer
					.hasMoreTokens(); System.out.println(total2)) {
				token = tokenizer.nextToken();
				token1 = tokenizer.nextToken();
				total = Integer.parseInt(token1);
				total2 += total;
			}

		}
		Registration registration = new Registration();
		Log.log(5, "RPAction", "submitGFDANPayments", (new StringBuilder())
				.append("member id ").append(actionForm.getMemberId())
				.toString());
		CollectingBank collectingBank = registration
				.getCollectingBank((new StringBuilder()).append("(")
						.append(actionForm.getMemberId()).append(")")
						.toString());
		Log.log(5, "RPAction", "submitGFDANPayments", (new StringBuilder())
				.append("collectingBank ").append(collectingBank).toString());
		actionForm.setModeOfPayment("");
		actionForm.setPaymentDate(null);
		actionForm.setInstrumentNo("");
		actionForm.setInstrumentDate(null);
		actionForm.setDrawnAtBank("");
		actionForm.setDrawnAtBranch("");
		actionForm.setPayableAt("");
		IFProcessor ifProcessor = new IFProcessor();
		ArrayList instruments = ifProcessor.getInstrumentTypes("G");
		actionForm.setInstruments(instruments);
		actionForm.setCollectingBank(collectingBank.getCollectingBankId());
		actionForm
				.setCollectingBankName(collectingBank.getCollectingBankName());
		actionForm.setCollectingBankBranch(collectingBank.getBranchName());
		actionForm.setAccountNumber(collectingBank.getAccNo());
		actionForm.setInstrumentAmount(total2);
		return mapping.findForward("gfpaymentDetails");
	}

	public ActionForward submitGFDANPaymentsold(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RPActionForm actionForm = (RPActionForm) form;
		double totalAmount = 0.0D;
		Map allocatedFlags = actionForm.getAllocatedFlags();
		Map cgpans = actionForm.getCgpans();
		int allocatedcount = 0;
		int testallocatecount = 0;
		Log.log(5, "RPAction", "submitGFDANPayments", (new StringBuilder())
				.append("Checkbox size = ").append(allocatedFlags.size())
				.toString());
		Set cgpansSet = cgpans.keySet();
		Iterator cgpanIterator = cgpansSet.iterator();
		Log.log(5, "RPAction", "submitGFDANPayments", (new StringBuilder())
				.append("Checkbox size = ").append(cgpans.size()).toString());
		String value;
		for (; cgpanIterator.hasNext(); Log.log(5, "RPAction",
				"submitGFDANPayments",
				(new StringBuilder()).append("cgpan value = ").append(value)
						.toString())) {
			String key = (String) cgpanIterator.next();
			value = (String) cgpans.get(key);
			Log.log(5, "RPAction", "submitGFDANPayments", (new StringBuilder())
					.append("cgpan key = ").append(key).toString());
		}

		cgpanIterator = cgpansSet.iterator();
		ArrayList danSummaries = actionForm.getDanSummaries();
		boolean isAllocated = false;
		for (int i = 0; i < danSummaries.size(); i++) {
			DANSummary danSummary = (DANSummary) danSummaries.get(i);
			String danId = danSummary.getDanId();
			Log.log(5, "RPAction", "submitGFDANPayments", (new StringBuilder())
					.append("danId= ").append(danId).toString());
			String danIdKey = danId.replace('.', '_');
			if (allocatedFlags.containsKey(danIdKey)
					&& request.getParameter((new StringBuilder())
							.append("allocatedFlag(").append(danIdKey)
							.append(")").toString()) != null) {
				allocatedcount++;
				Log.log(5,
						"RPAction",
						"submitGFDANPayments",
						(new StringBuilder()).append("danSummaries= ")
								.append(danSummaries.size()).toString());
				isAllocated = true;
				totalAmount += danSummary.getAmountDue()
						- danSummary.getAmountPaid();
				Log.log(5,
						"RPAction",
						"submitGFDANPayments",
						(new StringBuilder())
								.append("due amount ")
								.append(danSummary.getAmountDue()
										- danSummary.getAmountPaid())
								.toString());
			} else {
				Log.log(5, "RPAction", "submitGFDANPayments",
						"CGPANS are allocated ");
				ArrayList panDetails = (ArrayList) actionForm
						.getDanPanDetail(danId);
				while (cgpanIterator.hasNext()) {
					String key = (String) cgpanIterator.next();
					value = (String) cgpans.get(key);
					String cgpanPart = value.substring(value.indexOf("-") + 1,
							value.length());
					String tempKey = value.replace('.', '_');
					Log.log(5, "RPAction", "submitGFDANPayments",
							(new StringBuilder()).append("key ").append(key)
									.toString());
					Log.log(5, "RPAction", "submitGFDANPayments",
							(new StringBuilder()).append("value ")
									.append(value).toString());
					Log.log(5,
							"RPAction",
							"submitGFDANPayments",
							(new StringBuilder()).append("tempKey ")
									.append(tempKey).toString());
					if (value.startsWith(danId)
							&& allocatedFlags.get(tempKey) != null
							&& ((String) allocatedFlags.get(tempKey))
									.equals("Y")) {
						testallocatecount++;
						cgpanPart = value.substring(value.indexOf("-") + 1,
								value.length());
						isAllocated = true;
						for (int j = 0; j < panDetails.size(); j++) {
							AllocationDetail allocation = (AllocationDetail) panDetails
									.get(j);
							Log.log(5,
									"RPAction",
									"submitGFDANPayments",
									(new StringBuilder())
											.append("amount for CGPAN ")
											.append(allocation.getCgpan())
											.append(",")
											.append(allocation.getAmountDue())
											.toString());
							if (!cgpanPart.equals(allocation.getCgpan()))
								continue;
							totalAmount += allocation.getAmountDue();
							break;
						}

					}
				}
				cgpanIterator = cgpansSet.iterator();
			}
		}

		if (!isAllocated) {
			throw new MissingDANDetailsException("No Allocation made.");
		} else {
			Registration registration = new Registration();
			Log.log(5, "RPAction", "submitGFDANPayments", (new StringBuilder())
					.append("member id ").append(actionForm.getMemberId())
					.toString());
			CollectingBank collectingBank = registration
					.getCollectingBank((new StringBuilder()).append("(")
							.append(actionForm.getMemberId()).append(")")
							.toString());
			Log.log(5, "RPAction", "submitGFDANPayments", (new StringBuilder())
					.append("collectingBank ").append(collectingBank)
					.toString());
			actionForm.setModeOfPayment("");
			actionForm.setPaymentDate(null);
			actionForm.setInstrumentNo("");
			actionForm.setInstrumentDate(null);
			actionForm.setDrawnAtBank("");
			actionForm.setDrawnAtBranch("");
			actionForm.setPayableAt("");
			IFProcessor ifProcessor = new IFProcessor();
			ArrayList instruments = ifProcessor.getInstrumentTypes("G");
			actionForm.setInstruments(instruments);
			actionForm.setCollectingBank(collectingBank.getCollectingBankId());
			actionForm.setCollectingBankName(collectingBank
					.getCollectingBankName());
			actionForm.setCollectingBankBranch(collectingBank.getBranchName());
			actionForm.setAccountNumber(collectingBank.getAccNo());
			actionForm.setInstrumentAmount(totalAmount);
			return mapping.findForward("gfpaymentDetails");
		}
	}

	public ActionForward submitNEFTGFDANPayments(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RPActionForm actionForm = (RPActionForm) form;
		double totalAmount = 0.0D;
		Map danIds = actionForm.getDanIds();
		Map allocatedFlags = actionForm.getAllocatedFlags();
		Map cgpans = actionForm.getCgpans();
		int allocatedcount = 0;
		int testallocatecount = 0;
		Set danIdSet = danIds.keySet();
		Log.log(5, "RPAction", "submitNEFTGFDANPayments", (new StringBuilder())
				.append("Checkbox size = ").append(allocatedFlags.size())
				.toString());
		Set cgpansSet = cgpans.keySet();
		Iterator cgpanIterator = cgpansSet.iterator();
		Log.log(5, "RPAction", "submitNEFTGFDANPayments", (new StringBuilder())
				.append("Checkbox size = ").append(cgpans.size()).toString());
		String value;
		for (; cgpanIterator.hasNext(); Log.log(5, "RPAction",
				"submitNEFTGFDANPayments",
				(new StringBuilder()).append("cgpan value = ").append(value)
						.toString())) {
			String key = (String) cgpanIterator.next();
			value = (String) cgpans.get(key);
			Log.log(5, "RPAction", "submitNEFTGFDANPayments",
					(new StringBuilder()).append("cgpan key = ").append(key)
							.toString());
		}

		cgpanIterator = cgpansSet.iterator();
		ArrayList danSummaries = actionForm.getDanSummaries();
		boolean isAllocated = false;
		for (int i = 0; i < danSummaries.size(); i++) {
			DANSummary danSummary = (DANSummary) danSummaries.get(i);
			String danId = danSummary.getDanId();
			Log.log(5, "RPAction", "submitNEFTGFDANPayments",
					(new StringBuilder()).append("danId= ").append(danId)
							.toString());
			String danIdKey = danId.replace('.', '_');
			if (allocatedFlags.containsKey(danIdKey)
					&& request.getParameter((new StringBuilder())
							.append("allocatedFlag(").append(danIdKey)
							.append(")").toString()) != null) {
				allocatedcount++;
				Log.log(5,
						"RPAction",
						"submitNEFTGFDANPayments",
						(new StringBuilder()).append("danSummaries= ")
								.append(danSummaries.size()).toString());
				isAllocated = true;
				totalAmount += danSummary.getAmountDue()
						- danSummary.getAmountPaid();
				Log.log(5,
						"RPAction",
						"submitNEFTGFDANPayments",
						(new StringBuilder())
								.append("due amount ")
								.append(danSummary.getAmountDue()
										- danSummary.getAmountPaid())
								.toString());
			} else {
				Log.log(5, "RPAction", "submitNEFTGFDANPayments",
						"CGPANS are allocated ");
				ArrayList panDetails = (ArrayList) actionForm
						.getDanPanDetail(danId);
				while (cgpanIterator.hasNext()) {
					String key = (String) cgpanIterator.next();
					value = (String) cgpans.get(key);
					String cgpanPart = value.substring(value.indexOf("-") + 1,
							value.length());
					String tempKey = value.replace('.', '_');
					Log.log(5, "RPAction", "submitNEFTGFDANPayments",
							(new StringBuilder()).append("key ").append(key)
									.toString());
					Log.log(5, "RPAction", "submitNEFTGFDANPayments",
							(new StringBuilder()).append("value ")
									.append(value).toString());
					Log.log(5,
							"RPAction",
							"submitNEFTGFDANPayments",
							(new StringBuilder()).append("tempKey ")
									.append(tempKey).toString());
					if (value.startsWith(danId)
							&& allocatedFlags.get(tempKey) != null
							&& ((String) allocatedFlags.get(tempKey))
									.equals("Y")) {
						testallocatecount++;
						cgpanPart = value.substring(value.indexOf("-") + 1,
								value.length());
						isAllocated = true;
						for (int j = 0; j < panDetails.size(); j++) {
							AllocationDetail allocation = (AllocationDetail) panDetails
									.get(j);
							Log.log(5,
									"RPAction",
									"submitNEFTGFDANPayments",
									(new StringBuilder())
											.append("amount for CGPAN ")
											.append(allocation.getCgpan())
											.append(",")
											.append(allocation.getAmountDue())
											.toString());
							if (!cgpanPart.equals(allocation.getCgpan()))
								continue;
							totalAmount += allocation.getAmountDue();
							break;
						}

					}
				}
				cgpanIterator = cgpansSet.iterator();
			}
		}

		if (!isAllocated) {
			throw new MissingDANDetailsException("No Allocation made.");
		} else {
			Registration registration = new Registration();
			Log.log(5,
					"RPAction",
					"submitNEFTGFDANPayments",
					(new StringBuilder()).append("member id ")
							.append(actionForm.getMemberId()).toString());
			CollectingBank collectingBank = registration
					.getCollectingBank((new StringBuilder()).append("(")
							.append(actionForm.getMemberId()).append(")")
							.toString());
			Log.log(5,
					"RPAction",
					"submitNEFTGFDANPayments",
					(new StringBuilder()).append("collectingBank ")
							.append(collectingBank).toString());
			actionForm.setModeOfPayment("");
			actionForm.setPaymentDate(null);
			actionForm.setInstrumentNo("");
			actionForm.setInstrumentDate(null);
			actionForm.setDrawnAtBank("");
			actionForm.setDrawnAtBranch("");
			actionForm.setPayableAt("");
			IFProcessor ifProcessor = new IFProcessor();
			actionForm.setCollectingBank("IDBI BANK LTD");
			actionForm.setCollectingBankBranch("CHEMBUR");
			actionForm.setAccountNumber("018102000014951");
			actionForm
					.setAccountName("Credit Guarantee Fund Trust for Micro And Small Enterprises");
			actionForm.setIfscCode("IBKL0000018");
			actionForm.setModeOfPayment("NEFT");
			actionForm.setInstrumentAmount(totalAmount);
			return mapping.findForward("gfpaymentDetailsNew");
		}
	}

	public ActionForward submitDANPayments(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RPActionForm actionForm = (RPActionForm) form;
		double totalAmount = 0.0D;
		StringTokenizer tokenizer = null;
		double tot = 0.0D;
		Map appropriatedCases = actionForm.getAppropriatedFlags();
		if (appropriatedCases.size() < 1)
			throw new MissingDANDetailsException(
					"Please select atleast one dan for allocation .");
		Set appropriatedCasesSet = appropriatedCases.keySet();
		Iterator appropriatedCasesIterator = appropriatedCasesSet.iterator();
		String token = null;
		String token1 = null;
		float total = 0.0F;
		float total2 = 0.0F;
		while (appropriatedCasesIterator.hasNext()) {
			String key = (String) appropriatedCasesIterator.next();
			for (tokenizer = new StringTokenizer(key, "#"); tokenizer
					.hasMoreTokens(); System.out.println(total2)) {
				token = tokenizer.nextToken();
				token1 = tokenizer.nextToken();
				total = Integer.parseInt(token1);
				total2 += total;
			}

		}
		Registration registration = new Registration();
		Log.log(5, "RPAction", "submitGFDANPayments", (new StringBuilder())
				.append("member id ").append(actionForm.getMemberId())
				.toString());
		CollectingBank collectingBank = registration
				.getCollectingBank((new StringBuilder()).append("(")
						.append(actionForm.getMemberId()).append(")")
						.toString());
		Log.log(5, "RPAction", "submitGFDANPayments", (new StringBuilder())
				.append("collectingBank ").append(collectingBank).toString());
		actionForm.setModeOfPayment("");
		actionForm.setPaymentDate(null);
		actionForm.setInstrumentNo("");
		actionForm.setInstrumentDate(null);
		actionForm.setDrawnAtBank("");
		actionForm.setDrawnAtBranch("");
		actionForm.setPayableAt("");
		IFProcessor ifProcessor = new IFProcessor();
		ArrayList instruments = ifProcessor.getInstrumentTypes("G");
		actionForm.setInstruments(instruments);
		actionForm.setCollectingBank(collectingBank.getCollectingBankId());
		actionForm
				.setCollectingBankName(collectingBank.getCollectingBankName());
		actionForm.setCollectingBankBranch(collectingBank.getBranchName());
		actionForm.setAccountNumber(collectingBank.getAccNo());
		actionForm.setInstrumentAmount(total2);
		return mapping.findForward("gfpaymentDetails");
	}

	public ActionForward submitDANPaymentsold(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RPActionForm actionForm = (RPActionForm) form;
		double totalAmount = 0.0D;
		Map danIds = actionForm.getDanIds();
		Map allocatedFlags = actionForm.getAllocatedFlags();
		Map cgpans = actionForm.getCgpans();
		int allocatedcount = 0;
		int testallocatecount = 0;
		Set danIdSet = danIds.keySet();
		Log.log(5, "RPAction", "submitDANPayments", (new StringBuilder())
				.append("Checkbox size = ").append(allocatedFlags.size())
				.toString());
		Set cgpansSet = cgpans.keySet();
		Iterator cgpanIterator = cgpansSet.iterator();
		Log.log(5, "RPAction", "submitDANPayments", (new StringBuilder())
				.append("Checkbox size = ").append(cgpans.size()).toString());
		String value;
		for (; cgpanIterator.hasNext(); Log.log(5, "RPAction",
				"submitDANPayments",
				(new StringBuilder()).append("cgpan value = ").append(value)
						.toString())) {
			String key = (String) cgpanIterator.next();
			value = (String) cgpans.get(key);
			Log.log(5, "RPAction", "submitDANPayments", (new StringBuilder())
					.append("cgpan key = ").append(key).toString());
		}

		cgpanIterator = cgpansSet.iterator();
		ArrayList danSummaries = actionForm.getDanSummaries();
		boolean isAllocated = false;
		for (int i = 0; i < danSummaries.size(); i++) {
			DANSummary danSummary = (DANSummary) danSummaries.get(i);
			String danId = danSummary.getDanId();
			Log.log(5, "RPAction", "submitDANPayments", (new StringBuilder())
					.append("danId= ").append(danId).toString());
			String danIdKey = danId.replace('.', '_');
			if (allocatedFlags.containsKey(danIdKey)
					&& request.getParameter((new StringBuilder())
							.append("allocatedFlag(").append(danIdKey)
							.append(")").toString()) != null) {
				allocatedcount++;
				Log.log(5,
						"RPAction",
						"submitDANPayments",
						(new StringBuilder()).append("danSummaries= ")
								.append(danSummaries.size()).toString());
				isAllocated = true;
				totalAmount += danSummary.getAmountDue()
						- danSummary.getAmountPaid();
				Log.log(5,
						"RPAction",
						"submitDANPayments",
						(new StringBuilder())
								.append("due amount ")
								.append(danSummary.getAmountDue()
										- danSummary.getAmountPaid())
								.toString());
			} else {
				Log.log(5, "RPAction", "submitDANPayments",
						"CGPANS are allocated ");
				ArrayList panDetails = (ArrayList) actionForm
						.getDanPanDetail(danId);
				while (cgpanIterator.hasNext()) {
					String key = (String) cgpanIterator.next();
					value = (String) cgpans.get(key);
					String cgpanPart = value.substring(value.indexOf("-") + 1,
							value.length());
					String tempKey = value.replace('.', '_');
					Log.log(5, "RPAction", "submitDANPayments",
							(new StringBuilder()).append("key ").append(key)
									.toString());
					Log.log(5, "RPAction", "submitDANPayments",
							(new StringBuilder()).append("value ")
									.append(value).toString());
					Log.log(5,
							"RPAction",
							"submitDANPayments",
							(new StringBuilder()).append("tempKey ")
									.append(tempKey).toString());
					if (value.startsWith(danId)
							&& allocatedFlags.get(tempKey) != null
							&& ((String) allocatedFlags.get(tempKey))
									.equals("Y")) {
						testallocatecount++;
						cgpanPart = value.substring(value.indexOf("-") + 1,
								value.length());
						isAllocated = true;
						for (int j = 0; j < panDetails.size(); j++) {
							AllocationDetail allocation = (AllocationDetail) panDetails
									.get(j);
							Log.log(5,
									"RPAction",
									"submitDANPayments",
									(new StringBuilder())
											.append("amount for CGPAN ")
											.append(allocation.getCgpan())
											.append(",")
											.append(allocation.getAmountDue())
											.toString());
							if (!cgpanPart.equals(allocation.getCgpan()))
								continue;
							totalAmount += allocation.getAmountDue();
							break;
						}

					}
				}
				cgpanIterator = cgpansSet.iterator();
			}
		}

		if (!isAllocated) {
			throw new MissingDANDetailsException("No Allocation made.");
		} else {
			Registration registration = new Registration();
			Log.log(5, "RPAction", "submitDANPayments", (new StringBuilder())
					.append("member id ").append(actionForm.getMemberId())
					.toString());
			CollectingBank collectingBank = registration
					.getCollectingBank((new StringBuilder()).append("(")
							.append(actionForm.getMemberId()).append(")")
							.toString());
			Log.log(5, "RPAction", "submitDANPayments", (new StringBuilder())
					.append("collectingBank ").append(collectingBank)
					.toString());
			actionForm.setModeOfPayment("");
			actionForm.setPaymentDate(null);
			actionForm.setInstrumentNo("");
			actionForm.setInstrumentDate(null);
			actionForm.setDrawnAtBank("");
			actionForm.setDrawnAtBranch("");
			actionForm.setPayableAt("");
			IFProcessor ifProcessor = new IFProcessor();
			ArrayList instruments = ifProcessor.getInstrumentTypes("G");
			actionForm.setInstruments(instruments);
			actionForm.setCollectingBank(collectingBank.getCollectingBankId());
			actionForm.setCollectingBankName(collectingBank
					.getCollectingBankName());
			actionForm.setCollectingBankBranch(collectingBank.getBranchName());
			actionForm.setAccountNumber(collectingBank.getAccNo());
			actionForm.setInstrumentAmount(totalAmount);
			return mapping.findForward("paymentDetails");
		}
	}

	public ActionForward submitSFDANPayments(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RPActionForm actionForm = (RPActionForm) form;
		double totalAmount = 0.0D;
		Map danIds = actionForm.getDanIds();
		Map allocatedFlags = actionForm.getAllocatedFlags();
		Map cgpans = actionForm.getCgpans();
		Set danIdSet = danIds.keySet();
		Log.log(5, "RPAction", "submitSFDANPayments", (new StringBuilder())
				.append("Checkbox size = ").append(allocatedFlags.size())
				.toString());
		Set cgpansSet = cgpans.keySet();
		Iterator cgpanIterator = cgpansSet.iterator();
		Log.log(5, "RPAction", "submitSFDANPayments", (new StringBuilder())
				.append("Checkbox size = ").append(cgpans.size()).toString());
		String value;
		for (; cgpanIterator.hasNext(); Log.log(5, "RPAction",
				"submitSFDANPayments",
				(new StringBuilder()).append("cgpan value = ").append(value)
						.toString())) {
			String key = (String) cgpanIterator.next();
			value = (String) cgpans.get(key);
			Log.log(5, "RPAction", "submitSFDANPayments", (new StringBuilder())
					.append("cgpan key = ").append(key).toString());
		}

		cgpanIterator = cgpansSet.iterator();
		ArrayList danSummaries = actionForm.getDanSummaries();
		boolean isAllocated = false;
		for (int i = 0; i < danSummaries.size(); i++) {
			DANSummary danSummary = (DANSummary) danSummaries.get(i);
			String danId = danSummary.getDanId();
			Log.log(5, "RPAction", "submitSFDANPayments", (new StringBuilder())
					.append("danId= ").append(danId).toString());
			String danIdKey = danId.replace('.', '_');
			if (allocatedFlags.containsKey(danIdKey)
					&& request.getParameter((new StringBuilder())
							.append("allocatedFlag(").append(danIdKey)
							.append(")").toString()) != null) {
				Log.log(5,
						"RPAction",
						"submitSFDANPayments",
						(new StringBuilder()).append("danSummaries= ")
								.append(danSummaries.size()).toString());
				isAllocated = true;
				totalAmount += danSummary.getAmountDue()
						- danSummary.getAmountPaid();
				Log.log(5,
						"RPAction",
						"submitSFDANPayments",
						(new StringBuilder())
								.append("due amount ")
								.append(danSummary.getAmountDue()
										- danSummary.getAmountPaid())
								.toString());
			} else {
				Log.log(5, "RPAction", "submitSFDANPayments",
						"CGPANS are allocated ");
				ArrayList panDetails = (ArrayList) actionForm
						.getDanPanDetail(danId);
				while (cgpanIterator.hasNext()) {
					String key = (String) cgpanIterator.next();
					value = (String) cgpans.get(key);
					String cgpanPart = value.substring(value.indexOf("-") + 1,
							value.length());
					String tempKey = value.replace('.', '_');
					Log.log(5, "RPAction", "submitSFDANPayments",
							(new StringBuilder()).append("key ").append(key)
									.toString());
					Log.log(5, "RPAction", "submitSFDANPayments",
							(new StringBuilder()).append("value ")
									.append(value).toString());
					Log.log(5,
							"RPAction",
							"submitSFDANPayments",
							(new StringBuilder()).append("tempKey ")
									.append(tempKey).toString());
					if (value.startsWith(danId)
							&& allocatedFlags.get(tempKey) != null
							&& ((String) allocatedFlags.get(tempKey))
									.equals("Y")) {
						cgpanPart = value.substring(value.indexOf("-") + 1,
								value.length());
						isAllocated = true;
						for (int j = 0; j < panDetails.size(); j++) {
							AllocationDetail allocation = (AllocationDetail) panDetails
									.get(j);
							Log.log(5,
									"RPAction",
									"submitSFDANPayments",
									(new StringBuilder())
											.append("amount for CGPAN ")
											.append(allocation.getCgpan())
											.append(",")
											.append(allocation.getAmountDue())
											.toString());
							if (!cgpanPart.equals(allocation.getCgpan()))
								continue;
							totalAmount += allocation.getAmountDue();
							break;
						}

					}
				}
				cgpanIterator = cgpansSet.iterator();
			}
		}

		if (!isAllocated) {
			throw new MissingDANDetailsException("No Allocation made.");
		} else {
			Registration registration = new Registration();
			Log.log(5, "RPAction", "submitSFDANPayments", (new StringBuilder())
					.append("member id ").append(actionForm.getMemberId())
					.toString());
			CollectingBank collectingBank = registration
					.getCollectingBank((new StringBuilder()).append("(")
							.append(actionForm.getMemberId()).append(")")
							.toString());
			Log.log(5, "RPAction", "submitSFDANPayments", (new StringBuilder())
					.append("collectingBank ").append(collectingBank)
					.toString());
			actionForm.setModeOfPayment("");
			actionForm.setPaymentDate(null);
			actionForm.setInstrumentNo("");
			actionForm.setInstrumentDate(null);
			actionForm.setDrawnAtBank("");
			actionForm.setDrawnAtBranch("");
			actionForm.setPayableAt("");
			IFProcessor ifProcessor = new IFProcessor();
			ArrayList instruments = ifProcessor.getInstrumentTypes("G");
			actionForm.setInstruments(instruments);
			actionForm.setCollectingBank(collectingBank.getCollectingBankId());
			actionForm.setCollectingBankName(collectingBank
					.getCollectingBankName());
			actionForm.setCollectingBankBranch(collectingBank.getBranchName());
			actionForm.setAccountNumber(collectingBank.getAccNo());
			actionForm.setInstrumentAmount(totalAmount);
			return mapping.findForward("sfpaymentDetails");
		}
	}

	public ActionForward getPaymentMadeForGFInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RPActionForm actionForm = (RPActionForm) form;
		// actionForm.setMemberId("");
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, day);
		Date prevdate = cal.getTime();
		GeneralReport general = new GeneralReport();
		general.setDateOfTheDocument24(prevdate);
		general.setDateOfTheDocument25(date);
		BeanUtils.copyProperties(actionForm, general);
		return mapping.findForward("success");
	}

	public ActionForward getPaymentsMadeForGF(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RPActionForm actionForm = (RPActionForm) form;
		// String memId = actionForm.getMemberId();
		Date fromDate = actionForm.getDateOfTheDocument24();
		Date toDate = actionForm.getDateOfTheDocument25();
		 System.out.println("fromdate "+fromDate+"---- to date   "+toDate);
		RpProcessor rpProcessor = new RpProcessor();
		ArrayList paymentDetails = rpProcessor.displayPaymentsReceivedForGF(
				fromDate, toDate);
		actionForm.getCgpans().clear();
		actionForm.setPaymentDetails(paymentDetails);
		return mapping.findForward("paymentsSummary");
	}

	public ActionForward getPaymentMadeForSFInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RPActionForm actionForm = (RPActionForm) form;
		// actionForm.setMemberId("");
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, day);
		Date prevdate = cal.getTime();
		GeneralReport general = new GeneralReport();
		general.setDateOfTheDocument24(prevdate);
		general.setDateOfTheDocument25(date);
		BeanUtils.copyProperties(actionForm, general);
		return mapping.findForward("success");
	}

	public ActionForward getPaymentsMadeForASF(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RpProcessor rpProcessor = new RpProcessor();
		ArrayList paymentDetails = rpProcessor.displayPaymentsReceivedForASF();
		RPActionForm actionForm = (RPActionForm) form;
		actionForm.getCgpans().clear();
		actionForm.setPaymentDetails(paymentDetails);
		return mapping.findForward("paymentsSummary");
	}

	public ActionForward getPaymentsMadeForCLAIM(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RpProcessor rpProcessor = new RpProcessor();
		ArrayList paymentDetails = rpProcessor
				.displayPaymentsReceivedForCLAIM();
		RPActionForm actionForm = (RPActionForm) form;
		actionForm.getCgpans().clear();
		actionForm.setPaymentDetails(paymentDetails);
		return mapping.findForward("paymentsSummary");
	}

	public ActionForward submitClaimASFPaymentsnew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		RPActionForm actionForm = (RPActionForm) form;
		RpProcessor rpProcessor = new RpProcessor();
		ArrayList demandAdvices = new ArrayList();
		String methodName = "appropriatePayments";
		String payId = "";
		double recvAmount = 0.0;
		String allocatedFlag = "";
		String appropriatedFlag = "";
		String remark = "";
		String paymentId = "";
		String forwardPage = "";
		DemandAdvice demandAdvice = null;
		java.util.Date realizationDate = null;

		Map payIds = actionForm.getPayIds();

		Map allocatedFlags = actionForm.getAllocatedFlags();

		Map recvdAmounts = actionForm.getReceivedAmounts();
		Map remarks = actionForm.getRemarks();

		Map realizationDates = actionForm.getRealizationDates();

		User user = getUserInformation(request);
		String userId = user.getUserId();
		Set allocatedSet = allocatedFlags.keySet();

		if (allocatedFlags.size() == 0) {
			throw new NoDataException(
					"PLEASE SELECT AT LEAST ONE CHECK BOX FOR APPROPRIATION");
		}
		
		Connection connection = null;
		if (connection == null) {
			connection = DBConnection.getConnection(false);

		}

		CallableStatement multiPayIdAppr = null;

		Iterator allocatedSetIterator = allocatedSet.iterator();
		while (allocatedSetIterator.hasNext()) {

			String allocatkey = (String) allocatedSetIterator.next();
			System.out.println(allocatkey);
			String tempAmt = (String) recvdAmounts.get(allocatkey);

			if ((tempAmt == "null") || (tempAmt == "") || (tempAmt.equals(""))) {

				throw new NoDataException(
						"PLEASE ENTER RECEIVED AMOUNT. IT SHOULD NOT BE EMPTY");
			}

			recvAmount = Double.parseDouble(tempAmt);
			// System.out.println(recvAmount);
			remark = (String) remarks.get(allocatkey);
			//System.out.println(remark);
			payId = (String) payIds.get(allocatkey);
			//System.out.println(allocatedFlag);
			String relDt = (String) realizationDates.get(allocatkey);

			Double db = new Double(recvAmount);

			if ((remark == "null") || (remark == "") || (remark.equals(""))) {

				throw new NoDataException(
						"PLEASE ENTER REMARKS. IT SHOULD NOT BE EMPTY");
			}

			if ((relDt.contains("-")) || (relDt.contains("."))
					|| (relDt.contains("#")) || (relDt.contains("&"))
					|| (relDt.contains("*")) || (relDt.contains("!"))
					|| (relDt.contains("@")) || (relDt.contains("$"))
					|| (relDt.contains("%")) || !(relDt.contains("/"))) {

				throw new NoDataException(
						"PLEASE ENTER REALIZATION DATE SHOULD BE  DD/MM/YYYY   FORMAT");
			}

			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

			java.sql.Date endDate = null;

			try {

				Date eDate = formatter.parse(relDt);
				endDate = new java.sql.Date(eDate.getTime());

			} catch (ParseException e) {
				e.printStackTrace();
			}

			java.util.Date todaydate = new Date();

			if ((todaydate.compareTo(endDate)) < 0)

			{

				throw new NoDataException(
						"Please Check Your Realization Dates should not be greater than  today's date");

			}

		

			try {
				multiPayIdAppr = connection
						.prepareCall("{? = call funcapprmultiPayIds(?,?,?,?,?,?)}");
				multiPayIdAppr.registerOutParameter(1, Types.INTEGER);
				multiPayIdAppr.registerOutParameter(7, Types.VARCHAR);
				multiPayIdAppr.setString(2, payId);
				multiPayIdAppr.setDouble(3, recvAmount);
				multiPayIdAppr.setDate(4, endDate);
				multiPayIdAppr.setString(5, remark);
				multiPayIdAppr.setString(6, userId);

				multiPayIdAppr.executeQuery();
				int status = multiPayIdAppr.getInt(1);

				if (status == Constants.FUNCTION_FAILURE) {
					String errorCode = multiPayIdAppr.getString(7);
					connection.rollback();
					multiPayIdAppr.close();
					multiPayIdAppr = null;

					throw new DatabaseException(errorCode);
				}
				multiPayIdAppr.close();
				multiPayIdAppr = null;
				connection.commit();

			} catch (SQLException e) {

				throw new DatabaseException(e.getMessage());
			}

		}
	       DBConnection.freeConnection(connection);
		return mapping.findForward("success");
	}

	public ActionForward submitASFPaymentsnew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		
		//System.out.println("*****************start******submitASFPaymentsnew()****AsfAppropriation***** ");
		RPActionForm actionForm = (RPActionForm) form;
		RpProcessor rpProcessor = new RpProcessor();
		ArrayList demandAdvices = new ArrayList();
		String methodName = "appropriatePayments";
		String payId = "";
		double recvAmount = 0.0;
		String allocatedFlag = "";
		String appropriatedFlag = "";
		String remark = "";
		String paymentId = "";
		String forwardPage = "";
		DemandAdvice demandAdvice = null;
		java.util.Date realizationDate = null;

		Map payIds = actionForm.getPayIds();

		Map allocatedFlags = actionForm.getAllocatedFlags();

		Map recvdAmounts = actionForm.getReceivedAmounts();
		Map remarks = actionForm.getRemarks();

		Map realizationDates = actionForm.getRealizationDates();

		User user = getUserInformation(request);
		String userId = user.getUserId();
		Set allocatedSet = allocatedFlags.keySet();

		if (allocatedFlags.size() == 0) {
			throw new NoDataException(
					"PLEASE SELECT AT LEAST ONE CHECK BOX FOR APPROPRIATION");
		}
		Connection connection = null;
		if (connection == null) {
			connection = DBConnection.getConnection(false);//false changed to true by bhuneshwar 10062015

		}

		CallableStatement multiPayIdAppr = null;

		Iterator allocatedSetIterator = allocatedSet.iterator();
		
		while (allocatedSetIterator.hasNext()) 
		{
 			String allocatkey = (String) allocatedSetIterator.next();
			//System.out.println(allocatkey);
			String tempAmt = (String) recvdAmounts.get(allocatkey);
		//	System.out.println("tempAmt   :"+tempAmt);

			if ((tempAmt == null) || (tempAmt == "") || (tempAmt.equals(""))) {

				throw new NoDataException(
						"PLEASE ENTER RECEIVED AMOUNT. IT SHOULD NOT BE EMPTY");
			}

			recvAmount = Double.parseDouble(tempAmt);
			 //System.out.println(recvAmount);
			remark = (String) remarks.get(allocatkey);
			//System.out.println(remark);
			payId = (String) payIds.get(allocatkey);
			//System.out.println(payId);
			
//			System.out.println(allocatedFlag);
			String relDt = (String) realizationDates.get(allocatkey);
	//		System.out.println("relDt :"+relDt);

			Double db = new Double(recvAmount);

			if ((remark == null) || (remark == "") || (remark.equals(""))) {

				throw new NoDataException(
						"PLEASE ENTER REMARKS. IT SHOULD NOT BE EMPTY");
			}

			if ((relDt.contains("-")) || (relDt.contains("."))
					|| (relDt.contains("#")) || (relDt.contains("&"))
					|| (relDt.contains("*")) || (relDt.contains("!"))
					|| (relDt.contains("@")) || (relDt.contains("$"))
					|| (relDt.contains("%")) || !(relDt.contains("/"))) {

				throw new NoDataException(
						"PLEASE ENTER REALIZATION DATE SHOULD BE  DD/MM/YYYY   FORMAT");
			}

			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

			java.sql.Date endDate = null;

			try {

				Date eDate = formatter.parse(relDt);
				endDate = new java.sql.Date(eDate.getTime());

			} catch (ParseException e) {
				e.printStackTrace();
			}

			java.util.Date todaydate = new Date();

		//	System.out.println("todaydate :"+todaydate+"endDate : "+endDate);
			if ((todaydate.compareTo(endDate)) < 0)

			{

				throw new NoDataException(
						"Please Check Your Realization Dates should not be greater than  today's date");

			}

			
		

			try {
				multiPayIdAppr = connection
						.prepareCall("{? = call funcapprmultiPayIdsForASF(?,?,?,?,?,?)}");
				multiPayIdAppr.registerOutParameter(1, Types.INTEGER);
				multiPayIdAppr.registerOutParameter(7, Types.VARCHAR);
				multiPayIdAppr.setString(2, payId);
				multiPayIdAppr.setDouble(3, recvAmount);
				multiPayIdAppr.setDate(4, endDate);
				multiPayIdAppr.setString(5, remark);
				multiPayIdAppr.setString(6, userId);

				multiPayIdAppr.executeQuery();
				int status = multiPayIdAppr.getInt(1);

				if (status == Constants.FUNCTION_FAILURE) {
					String errorCode = multiPayIdAppr.getString(7);
					connection.rollback();
					multiPayIdAppr.close();
					multiPayIdAppr = null;

					throw new DatabaseException(errorCode);
				}
				multiPayIdAppr.close();
				multiPayIdAppr = null;
				connection.commit();
				//connection.rollback();

			} catch (SQLException e) {

				throw new DatabaseException(e.getMessage());
			}

		}
		   DBConnection.freeConnection(connection);
		//System.out.println("*****************end()******submitASFPaymentsnew()****AsfAppropriation***** ");
		return mapping.findForward("success");
	}

	public ActionForward getPaymentsMadeForCLAIMNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RpProcessor rpProcessor = new RpProcessor();

		RPActionForm rpActionForm = (RPActionForm) form;

		java.util.Date toDate = rpActionForm.getDateOfTheDocument24();

		java.util.Date fromDate = rpActionForm.getDateOfTheDocument25();

		// ArrayList paymentDetails =
		// rpProcessor.displayPaymentsReceivedForASF(toDate,fromDate);
		if (rpActionForm.getAllocatedFlags() != null)
			rpActionForm.getAllocatedFlags().clear();
		if (rpActionForm.getRealizationDates() != null)
			rpActionForm.getRealizationDates().clear();
		if (rpActionForm.getReceivedAmounts() != null)
			rpActionForm.getReceivedAmounts().clear();

		ArrayList paymentDetails = rpProcessor
				.displayPaymentsReceivedForCLAIM1(toDate, fromDate);
		if (rpActionForm.getCgpans() != null)
			rpActionForm.getCgpans().clear();

		rpActionForm.setPaymentDetails(paymentDetails);
		return mapping.findForward("paymentsSummary");
	}

	public ActionForward getPaymentMadeInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RPActionForm actionForm = (RPActionForm) form;
		// actionForm.setMemberId("");
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, day);
		Date prevdate = cal.getTime();
		GeneralReport general = new GeneralReport();
		general.setDateOfTheDocument24(prevdate);
		general.setDateOfTheDocument25(date);
		BeanUtils.copyProperties(actionForm, general);
		return mapping.findForward("success");
	}
	
	
	
	
	

	public ActionForward getPaymentsMade(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RPActionForm actionForm = (RPActionForm) form;
		Date fromDate = actionForm.getDateOfTheDocument24();
		Date toDate = actionForm.getDateOfTheDocument25();
		RpProcessor rpProcessor = new RpProcessor();
		ArrayList paymentDetails = rpProcessor.displayPaymentsReceived(
				fromDate, toDate);
		actionForm.getCgpans().clear();
		actionForm.setPaymentDetails(paymentDetails);
		return mapping.findForward("paymentsSummary");
	}

	public ActionForward gfbatchappropriatePayments(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RpProcessor rpProcessor = new RpProcessor();
		ArrayList paymentDetails = rpProcessor
				.displayBatchPaymentsReceivedForGF();
		RPActionForm actionForm = (RPActionForm) form;
		actionForm.getCgpans().clear();
		actionForm.setPaymentDetails(paymentDetails);
		return mapping.findForward("paymentsSummary");
	}

	public ActionForward daywisegfbatchappropriation(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RPActionForm actionForm = (RPActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument24(date);
		BeanUtils.copyProperties(actionForm, generalReport);
		return mapping.findForward("inputDate");
	}

	public ActionForward daywiseddmarkedforDeposited(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RPActionForm actionForm = (RPActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument24(date);
		BeanUtils.copyProperties(actionForm, generalReport);
		return mapping.findForward("inputDate");
	}

	public ActionForward daywisegfbatchappropriatePayments( 
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RpProcessor rpProcessor = new RpProcessor();
		RPActionForm actionForm = (RPActionForm) form;
		Date dateofRealisation = actionForm.getDateOfTheDocument24();
		ArrayList paymentDetails = rpProcessor
				.daywiseBatchPaymentsReceivedForGF(dateofRealisation);
		actionForm.getCgpans().clear();
		actionForm.setPaymentDetails(paymentDetails);
		return mapping.findForward("paymentsSummary");
	}

	public ActionForward daywiseddmarkedforDepositedCases(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RpProcessor rpProcessor = new RpProcessor();
		RPActionForm actionForm = (RPActionForm) form;
		Date inwardDate = actionForm.getDateOfTheDocument24();
		ArrayList paymentDetails = rpProcessor
				.daywiseBatchPaymentsInwardedForGF(inwardDate);
		actionForm.getCgpans().clear();
		actionForm.setPaymentDetails(paymentDetails);
		return mapping.findForward("paymentsSummary");
	}

	public ActionForward dayWiseddMarkedForDepositedDate(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "dayWiseddMarkedForDepositedDate", "Entered");
		RPActionForm actionForm = (RPActionForm) form;
		RpProcessor rpProcessor = new RpProcessor();
		Date dateofDeposit = actionForm.getDateOfTheDocument24();
		User user = getUserInformation(request);
		String userId = user.getUserId();
		Log.log(4, "RPAction", "dayWiseddMarkedForDepositedDate", "Exited");
		return mapping.findForward("deposited");
	}

	public ActionForward aftergfbatchappropriatePayments(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "aftergfbatchappropriatePayments", "Entered");
		RPActionForm actionForm = (RPActionForm) form;
		RpProcessor rpProcessor = new RpProcessor();
		User user = getUserInformation(request);
		String userId = user.getUserId();
		int appropriatedCount = 0;
		Map appropriatedCases = actionForm.getAppropriatedFlags();
		Set appropriatedCasesSet = appropriatedCases.keySet();
		Iterator appropriatedCasesIterator = appropriatedCasesSet.iterator();
		boolean appropriatedFlag = false;
		while (appropriatedCasesIterator.hasNext()) {
			String key = (String) appropriatedCasesIterator.next();
			String decision = (String) appropriatedCases.get(key);
			if (decision.equals("Y")) {
				appropriatedCount += rpProcessor
						.aftergfbatchappropriatePayments(key, userId);
				appropriatedFlag = true;
			}
		}
		if (!appropriatedFlag) {
			throw new MissingDANDetailsException("No Appropriation Made.");
		} else {
			System.out.println((new StringBuilder())
					.append("Appropriated Count:").append(appropriatedCount)
					.toString());
			actionForm.setPaymentDetails(null);
			appropriatedCases.clear();
			request.setAttribute(
					"message",
					(new StringBuilder())
							.append("No.of Appropriations done are: ")
							.append(appropriatedCount).toString());
			Log.log(4, "RPAction", "aftergfbatchappropriatePayments", "Exited");
			return mapping.findForward("success");
		}
	}

	public ActionForward aftergfdaywisebatchappropriatePayments(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "aftergfdaywisebatchappropriatePayments",
				"Entered");
		
		System.out.println("    aftergfdaywisebatchappropriatePayments ....");
		
		RPActionForm actionForm = (RPActionForm) form;
		RpProcessor rpProcessor = new RpProcessor();
		Date dateofRealisation = actionForm.getDateOfTheDocument24();
		
		System.out.println("dateofRealisation"+dateofRealisation);
		User user = getUserInformation(request);
		String userId = user.getUserId();
		int appropriatedCount = 0;
		Map appropriatedCases = actionForm.getAppropriatedFlags();
		
		System.out.println(" appropriatedCases ..."+appropriatedCases); 
		
		Set appropriatedCasesSet = appropriatedCases.keySet();
		Iterator appropriatedCasesIterator = appropriatedCasesSet.iterator();
		boolean appropriatedFlag = false;
		while (appropriatedCasesIterator.hasNext()) {
			String key = (String) appropriatedCasesIterator.next();
			String decision = (String) appropriatedCases.get(key);
			if (decision.equals("Y")) {
				
				System.out.println("under If ");
				appropriatedCount += rpProcessor
						.aftergfdaywisebatchappropriatePayments(key, userId,
								dateofRealisation);
				appropriatedFlag = true;
			}
		}
		if (!appropriatedFlag) {
			throw new MissingDANDetailsException("No Appropriation Made.");
		} else {
			System.out.println((new StringBuilder())
					.append("Appropriated Count:").append(appropriatedCount)
					.toString());
			actionForm.setPaymentDetails(null);
			appropriatedCases.clear();
			request.setAttribute(
					"message",
					(new StringBuilder())
							.append("No.of Appropriations done are: ")
							.append(appropriatedCount).toString());
			Log.log(4, "RPAction", "aftergfdaywisebatchappropriatePayments",
					"Exited");
			return mapping.findForward("success");
		}
	}

	public ActionForward dayWiseddMarkedForDepositedSummary(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "dayWiseddMarkedForDepositedSummary", "Entered");
		RPActionForm actionForm = (RPActionForm) form;
		RpProcessor rpProcessor = new RpProcessor();
		Date dateofDeposit = actionForm.getDateOfTheDocument24();
		System.out.println((new StringBuilder())
				.append("After Confirm date of Deposit:").append(dateofDeposit)
				.toString());
		User user = getUserInformation(request);
		String userId = user.getUserId();
		int depositedCount = 0;
		StringTokenizer tokenizer = null;
		String instrumentNo = null;
		String inwardId = null;
		Map appropriatedCases = actionForm.getAppropriatedFlags();
		Set appropriatedCasesSet = appropriatedCases.keySet();
		Iterator appropriatedCasesIterator = appropriatedCasesSet.iterator();
		boolean appropriatedFlag = false;
		while (appropriatedCasesIterator.hasNext()) {
			String key = (String) appropriatedCasesIterator.next();
			String decision = (String) appropriatedCases.get(key);
			if (decision.equals("Y")) {
				String token = null;
				tokenizer = new StringTokenizer(key, "#");
				boolean isInstrumentNoRead = false;
				boolean isInwardIdRead = false;
				while (tokenizer.hasMoreTokens()) {
					token = tokenizer.nextToken();
					if (!isInwardIdRead)
						if (!isInstrumentNoRead) {
							instrumentNo = token;
							isInstrumentNoRead = true;
						} else {
							inwardId = token;
							isInwardIdRead = true;
						}
				}
				depositedCount += rpProcessor
						.dayWiseddMarkedForDepositedSummary(inwardId,
								instrumentNo, userId, dateofDeposit);
				appropriatedFlag = true;
			}
		}
		if (!appropriatedFlag) {
			throw new MissingDANDetailsException("No Selection Made.");
		} else {
			System.out.println((new StringBuilder())
					.append("No.of dds Marked for Deposited:")
					.append(depositedCount).toString());
			actionForm.setPaymentDetails(null);
			appropriatedCases.clear();
			request.setAttribute(
					"message",
					(new StringBuilder())
							.append("No.of dds Marked for Deposited are: ")
							.append(depositedCount).toString());
			Log.log(4, "RPAction", "dayWiseddMarkedForDepositedSummary",
					"Exited");
			return mapping.findForward("success");
		}
	}

	public ActionForward getPaymentsForReallocation(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "getPaymentsForReallocation", "Entered");
		RpProcessor rpProcessor = new RpProcessor();
		ArrayList paymentDetails = rpProcessor.displayPaymentsForReallocation();
		Log.log(5,
				"RPAction",
				"getPaymentsForReallocation",
				(new StringBuilder()).append("paymentDetails ")
						.append(paymentDetails).toString());
		RPActionForm actionForm = (RPActionForm) form;
		Log.log(5, "RPAction", "getPaymentsForReallocation",
				(new StringBuilder()).append("actionForm ").append(actionForm)
						.toString());
		actionForm.setPaymentDetails(paymentDetails);
		Log.log(4, "RPAction", "getPaymentsForReallocation", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward submitReallocationPayments(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "submitReallocationPayments", "Entered");
		RPActionForm actionForm = (RPActionForm) form;
		String payId = actionForm.getPaymentId();
		Log.log(5,
				"RPAction",
				"submitReallocationPayments",
				(new StringBuilder()).append("Pay id from form is ")
						.append(payId).toString());
		RpProcessor rpProcessor = new RpProcessor();
		User user = getUserInformation(request);
		rpProcessor.submitReAllocationDetails(actionForm, request, user, payId);
		request.setAttribute("message", "Reallocation details are updated.");
		Log.log(4, "RPAction", "submitReallocationPayments", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward submitASFPANPayments(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "submitASFPANPayments", "Entered");
		RPActionForm actionForm = (RPActionForm) form;
		String danNo = actionForm.getDanNo();
		Log.log(5, "RPAction", "submitASFPANPayments", (new StringBuilder())
				.append("danNo ").append(danNo).toString());
		ArrayList panAllocationDetails = (ArrayList) actionForm
				.getDanPanDetail(danNo);
		String strDanNo = danNo.replace('.', '_');
		Map allocatedFlags = actionForm.getAllocatedFlags();
		Map cgpans = actionForm.getCgpans();
		Set cgpanSet = cgpans.keySet();
		Iterator cgpanIterator = cgpanSet.iterator();
		Log.log(5, "RPAction", "submitASFPANPayments", (new StringBuilder())
				.append("CGPANS selected ").append(cgpans.size()).toString());
		String key;
		for (; cgpanIterator.hasNext(); Log.log(
				5,
				"RPAction",
				"submitASFPANPayments",
				(new StringBuilder())
						.append("From request ")
						.append(request.getParameter((new StringBuilder())
								.append("cgpan(").append(key).append(")")
								.toString())).toString())) {
			key = (String) cgpanIterator.next();
			Log.log(5, "RPAction", "submitASFPANPayments",
					(new StringBuilder()).append("key,value ").append(key)
							.append(",").append(cgpans.get(key)).toString());
		}

		cgpanIterator = cgpanSet.iterator();
		String cgpanPart = null;
		Log.log(5, "RPAction", "submitASFPANPayments",
				"browsing through the pan list");
		boolean isAvl = false;
		String value = null;
		Log.log(5, "RPAction", "submitASFPANPayments", (new StringBuilder())
				.append("Cgpan map size ").append(cgpans.size()).toString());
		for (int i = 0; i < panAllocationDetails.size(); i++) {
			AllocationDetail allocationDetail = (AllocationDetail) panAllocationDetails
					.get(i);
			Log.log(5,
					"RPAction",
					"submitASFPANPayments",
					(new StringBuilder()).append("cgpan frm array ")
							.append(allocationDetail.getCgpan()).toString());
			Log.log(5,
					"RPAction",
					"submitASFPANPayments",
					(new StringBuilder()).append("flag frm array ")
							.append(allocationDetail.getAllocatedFlag())
							.toString());
			while (cgpanIterator.hasNext()) {
				key = (String) cgpanIterator.next();
				value = (String) cgpans.get(key);
				Log.log(5, "RPAction", "submitASFPANPayments",
						(new StringBuilder()).append("key ").append(key)
								.toString());
				Log.log(5, "RPAction", "submitASFPANPayments",
						(new StringBuilder()).append("value ").append(value)
								.toString());
				cgpanPart = value.substring(value.indexOf("-") + 1,
						value.length());
				Log.log(5,
						"RPAction",
						"submitASFPANPayments",
						(new StringBuilder()).append("cgpanPart ")
								.append(cgpanPart).toString());
				if (value.startsWith(danNo)
						&& cgpanPart.equals(allocationDetail.getCgpan())
						&& allocatedFlags.get(key) != null
						&& ((String) allocatedFlags.get(key)).equals("Y")) {
					Log.log(5, "RPAction", "submitASFPANPayments",
							(new StringBuilder()).append("amount due  ")
									.append(allocationDetail.getAmountDue())
									.toString());
					allocationDetail.setAllocatedFlag("Y");
					isAvl = true;
					break;
				}
			}
			if (!isAvl) {
				Object removed = cgpans.remove((new StringBuilder())
						.append(strDanNo).append("-")
						.append(allocationDetail.getCgpan()).toString());
				Log.log(5,
						"RPAction",
						"submitASFPANPayments",
						(new StringBuilder()).append("Removed element")
								.append(removed).toString());
			}
			isAvl = false;
			cgpanIterator = cgpanSet.iterator();
		}

		Log.log(5, "RPAction", "submitASFPANPayments", (new StringBuilder())
				.append("Cgpan map size ").append(cgpans.size()).toString());
		return mapping.findForward("danSummary");
	}

	public ActionForward submitExpiredASFPANPayments(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "submitExpiredASFPANPayments", "Entered");
		RPActionForm actionForm = (RPActionForm) form;
		String danNo = actionForm.getDanNo();
		Log.log(5, "RPAction", "submitExpiredASFPANPayments",
				(new StringBuilder()).append("danNo ").append(danNo).toString());
		ArrayList panAllocationDetails = (ArrayList) actionForm
				.getDanPanDetail(danNo);
		String strDanNo = danNo.replace('.', '_');
		Map allocatedFlags = actionForm.getAllocatedFlags();
		Map cgpans = actionForm.getCgpans();
		Set cgpanSet = cgpans.keySet();
		Iterator cgpanIterator = cgpanSet.iterator();
		Log.log(5,
				"RPAction",
				"submitExpiredASFPANPayments",
				(new StringBuilder()).append("CGPANS selected ")
						.append(cgpans.size()).toString());
		String key;
		for (; cgpanIterator.hasNext(); Log.log(
				5,
				"RPAction",
				"submitExpiredASFPANPayments",
				(new StringBuilder())
						.append("From request ")
						.append(request.getParameter((new StringBuilder())
								.append("cgpan(").append(key).append(")")
								.toString())).toString())) {
			key = (String) cgpanIterator.next();
			Log.log(5, "RPAction", "submitExpiredASFPANPayments",
					(new StringBuilder()).append("key,value ").append(key)
							.append(",").append(cgpans.get(key)).toString());
		}

		cgpanIterator = cgpanSet.iterator();
		String cgpanPart = null;
		Log.log(5, "RPAction", "submitExpiredASFPANPayments",
				"browsing through the pan list");
		boolean isAvl = false;
		String value = null;
		Log.log(5,
				"RPAction",
				"submitExpiredASFPANPayments",
				(new StringBuilder()).append("Cgpan map size ")
						.append(cgpans.size()).toString());
		for (int i = 0; i < panAllocationDetails.size(); i++) {
			AllocationDetail allocationDetail = (AllocationDetail) panAllocationDetails
					.get(i);
			Log.log(5,
					"RPAction",
					"submitExpiredASFPANPayments",
					(new StringBuilder()).append("cgpan frm array ")
							.append(allocationDetail.getCgpan()).toString());
			Log.log(5,
					"RPAction",
					"submitExpiredASFPANPayments",
					(new StringBuilder()).append("flag frm array ")
							.append(allocationDetail.getAllocatedFlag())
							.toString());
			while (cgpanIterator.hasNext()) {
				key = (String) cgpanIterator.next();
				value = (String) cgpans.get(key);
				Log.log(5, "RPAction", "submitExpiredASFPANPayments",
						(new StringBuilder()).append("key ").append(key)
								.toString());
				Log.log(5, "RPAction", "submitExpiredASFPANPayments",
						(new StringBuilder()).append("value ").append(value)
								.toString());
				cgpanPart = value.substring(value.indexOf("-") + 1,
						value.length());
				Log.log(5,
						"RPAction",
						"submitExpiredASFPANPayments",
						(new StringBuilder()).append("cgpanPart ")
								.append(cgpanPart).toString());
				if (value.startsWith(danNo)
						&& cgpanPart.equals(allocationDetail.getCgpan())
						&& allocatedFlags.get(key) != null
						&& ((String) allocatedFlags.get(key)).equals("Y")) {
					Log.log(5, "RPAction", "submitExpiredASFPANPayments",
							(new StringBuilder()).append("amount due  ")
									.append(allocationDetail.getAmountDue())
									.toString());
					allocationDetail.setAllocatedFlag("Y");
					isAvl = true;
					break;
				}
			}
			if (!isAvl) {
				Object removed = cgpans.remove((new StringBuilder())
						.append(strDanNo).append("-")
						.append(allocationDetail.getCgpan()).toString());
				Log.log(5,
						"RPAction",
						"submitExpiredASFPANPayments",
						(new StringBuilder()).append("Removed element")
								.append(removed).toString());
			}
			isAvl = false;
			cgpanIterator = cgpanSet.iterator();
		}

		Log.log(5,
				"RPAction",
				"submitExpiredASFPANPayments",
				(new StringBuilder()).append("Cgpan map size ")
						.append(cgpans.size()).toString());
		return mapping.findForward("danSummary");
	}

	public ActionForward submitGFPANPayments(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "submitGFPANPayments", "Entered");
		RPActionForm actionForm = (RPActionForm) form;
		String danNo = actionForm.getDanNo();
		Log.log(5, "RPAction", "submitGFPANPayments", (new StringBuilder())
				.append("danNo ").append(danNo).toString());
		ArrayList panAllocationDetails = (ArrayList) actionForm
				.getDanPanDetail(danNo);
		String strDanNo = danNo.replace('.', '_');
		Map allocatedFlags = actionForm.getAllocatedFlags();
		Map cgpans = actionForm.getCgpans();
		Set cgpanSet = cgpans.keySet();
		Iterator cgpanIterator = cgpanSet.iterator();
		Log.log(5, "RPAction", "submitGFPANPayments", (new StringBuilder())
				.append("CGPANS selected ").append(cgpans.size()).toString());
		String key;
		for (; cgpanIterator.hasNext(); Log.log(
				5,
				"RPAction",
				"submitGFPANPayments",
				(new StringBuilder())
						.append("From request ")
						.append(request.getParameter((new StringBuilder())
								.append("cgpan(").append(key).append(")")
								.toString())).toString())) {
			key = (String) cgpanIterator.next();
			Log.log(5, "RPAction", "submitGFPANPayments",
					(new StringBuilder()).append("key,value ").append(key)
							.append(",").append(cgpans.get(key)).toString());
		}

		cgpanIterator = cgpanSet.iterator();
		String cgpanPart = null;
		Log.log(5, "RPAction", "submitGFPANPayments",
				"browsing through the pan list");
		boolean isAvl = false;
		String value = null;
		Log.log(5, "RPAction", "submitGFPANPayments", (new StringBuilder())
				.append("Cgpan map size ").append(cgpans.size()).toString());
		for (int i = 0; i < panAllocationDetails.size(); i++) {
			AllocationDetail allocationDetail = (AllocationDetail) panAllocationDetails
					.get(i);
			Log.log(5,
					"RPAction",
					"submitGFPANPayments",
					(new StringBuilder()).append("cgpan frm array ")
							.append(allocationDetail.getCgpan()).toString());
			Log.log(5,
					"RPAction",
					"submitGFPANPayments",
					(new StringBuilder()).append("flag frm array ")
							.append(allocationDetail.getAllocatedFlag())
							.toString());
			while (cgpanIterator.hasNext()) {
				key = (String) cgpanIterator.next();
				value = (String) cgpans.get(key);
				Log.log(5, "RPAction", "submitGFPANPayments",
						(new StringBuilder()).append("key ").append(key)
								.toString());
				Log.log(5, "RPAction", "submitGFPANPayments",
						(new StringBuilder()).append("value ").append(value)
								.toString());
				cgpanPart = value.substring(value.indexOf("-") + 1,
						value.length());
				Log.log(5,
						"RPAction",
						"submitGFPANPayments",
						(new StringBuilder()).append("cgpanPart ")
								.append(cgpanPart).toString());
				if (value.startsWith(danNo)
						&& cgpanPart.equals(allocationDetail.getCgpan())
						&& allocatedFlags.get(key) != null
						&& ((String) allocatedFlags.get(key)).equals("Y")) {
					Log.log(5, "RPAction", "submitGFPANPayments",
							(new StringBuilder()).append("amount due  ")
									.append(allocationDetail.getAmountDue())
									.toString());
					allocationDetail.setAllocatedFlag("Y");
					isAvl = true;
					break;
				}
			}
			if (!isAvl) {
				Object removed = cgpans.remove((new StringBuilder())
						.append(strDanNo).append("-")
						.append(allocationDetail.getCgpan()).toString());
				Log.log(5,
						"RPAction",
						"submitGFPANPayments",
						(new StringBuilder()).append("Removed element")
								.append(removed).toString());
			}
			isAvl = false;
			cgpanIterator = cgpanSet.iterator();
		}

		Log.log(5, "RPAction", "submitGFPANPayments", (new StringBuilder())
				.append("Cgpan map size ").append(cgpans.size()).toString());
		return mapping.findForward("danSummary");
	}

	public ActionForward submitPANPayments(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "submitPANPayments", "Entered");
		RPActionForm actionForm = (RPActionForm) form;
		String danNo = actionForm.getDanNo();
		Log.log(5, "RPAction", "submitPANPayments", (new StringBuilder())
				.append("danNo ").append(danNo).toString());
		ArrayList panAllocationDetails = (ArrayList) actionForm
				.getDanPanDetail(danNo);
		String strDanNo = danNo.replace('.', '_');
		Map allocatedFlags = actionForm.getAllocatedFlags();
		Map cgpans = actionForm.getCgpans();
		Set cgpanSet = cgpans.keySet();
		Iterator cgpanIterator = cgpanSet.iterator();
		Log.log(5, "RPAction", "submitPANPayments", (new StringBuilder())
				.append("CGPANS selected ").append(cgpans.size()).toString());
		String key;
		for (; cgpanIterator.hasNext(); Log.log(
				5,
				"RPAction",
				"submitPANPayments",
				(new StringBuilder())
						.append("From request ")
						.append(request.getParameter((new StringBuilder())
								.append("cgpan(").append(key).append(")")
								.toString())).toString())) {
			key = (String) cgpanIterator.next();
			Log.log(5, "RPAction", "submitPANPayments",
					(new StringBuilder()).append("key,value ").append(key)
							.append(",").append(cgpans.get(key)).toString());
		}

		cgpanIterator = cgpanSet.iterator();
		String cgpanPart = null;
		Log.log(5, "RPAction", "submitPANPayments",
				"browsing through the pan list");
		boolean isAvl = false;
		String value = null;
		Log.log(5, "RPAction", "submitPANPayments", (new StringBuilder())
				.append("Cgpan map size ").append(cgpans.size()).toString());
		for (int i = 0; i < panAllocationDetails.size(); i++) {
			AllocationDetail allocationDetail = (AllocationDetail) panAllocationDetails
					.get(i);
			Log.log(5,
					"RPAction",
					"submitPANPayments",
					(new StringBuilder()).append("cgpan frm array ")
							.append(allocationDetail.getCgpan()).toString());
			Log.log(5,
					"RPAction",
					"submitPANPayments",
					(new StringBuilder()).append("flag frm array ")
							.append(allocationDetail.getAllocatedFlag())
							.toString());
			while (cgpanIterator.hasNext()) {
				key = (String) cgpanIterator.next();
				value = (String) cgpans.get(key);
				Log.log(5, "RPAction", "submitPANPayments",
						(new StringBuilder()).append("key ").append(key)
								.toString());
				Log.log(5, "RPAction", "submitPANPayments",
						(new StringBuilder()).append("value ").append(value)
								.toString());
				cgpanPart = value.substring(value.indexOf("-") + 1,
						value.length());
				Log.log(5,
						"RPAction",
						"submitPANPayments",
						(new StringBuilder()).append("cgpanPart ")
								.append(cgpanPart).toString());
				if (value.startsWith(danNo)
						&& cgpanPart.equals(allocationDetail.getCgpan())
						&& allocatedFlags.get(key) != null
						&& ((String) allocatedFlags.get(key)).equals("Y")) {
					Log.log(5, "RPAction", "submitPANPayments",
							(new StringBuilder()).append("amount due  ")
									.append(allocationDetail.getAmountDue())
									.toString());
					isAvl = true;
					break;
				}
			}
			if (!isAvl) {
				Object removed = cgpans.remove((new StringBuilder())
						.append(strDanNo).append("-")
						.append(allocationDetail.getCgpan()).toString());
				Log.log(5,
						"RPAction",
						"submitPANPayments",
						(new StringBuilder()).append("Removed element")
								.append(removed).toString());
			}
			isAvl = false;
			cgpanIterator = cgpanSet.iterator();
		}

		Log.log(5, "RPAction", "submitPANPayments", (new StringBuilder())
				.append("Cgpan map size ").append(cgpans.size()).toString());
		return mapping.findForward("danSummary");
	}

	public ActionForward submitSFPANPayments(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "submitsfPANPayments", "Entered");
		RPActionForm actionForm = (RPActionForm) form;
		String danNo = actionForm.getDanNo();
		Log.log(5, "RPAction", "submitPANPayments", (new StringBuilder())
				.append("danNo ").append(danNo).toString());
		ArrayList panAllocationDetails = (ArrayList) actionForm
				.getDanPanDetail(danNo);
		String strDanNo = danNo.replace('.', '_');
		Map allocatedFlags = actionForm.getAllocatedFlags();
		Map cgpans = actionForm.getCgpans();
		Set cgpanSet = cgpans.keySet();
		Iterator cgpanIterator = cgpanSet.iterator();
		Log.log(5, "RPAction", "submitPANPayments", (new StringBuilder())
				.append("CGPANS selected ").append(cgpans.size()).toString());
		String key;
		for (; cgpanIterator.hasNext(); Log.log(
				5,
				"RPAction",
				"submitPANPayments",
				(new StringBuilder())
						.append("From request ")
						.append(request.getParameter((new StringBuilder())
								.append("cgpan(").append(key).append(")")
								.toString())).toString())) {
			key = (String) cgpanIterator.next();
			Log.log(5, "RPAction", "submitPANPayments",
					(new StringBuilder()).append("key,value ").append(key)
							.append(",").append(cgpans.get(key)).toString());
		}

		cgpanIterator = cgpanSet.iterator();
		String cgpanPart = null;
		Log.log(5, "RPAction", "submitPANPayments",
				"browsing through the pan list");
		boolean isAvl = false;
		String value = null;
		Log.log(5, "RPAction", "submitPANPayments", (new StringBuilder())
				.append("Cgpan map size ").append(cgpans.size()).toString());
		for (int i = 0; i < panAllocationDetails.size(); i++) {
			AllocationDetail allocationDetail = (AllocationDetail) panAllocationDetails
					.get(i);
			Log.log(5,
					"RPAction",
					"submitPANPayments",
					(new StringBuilder()).append("cgpan frm array ")
							.append(allocationDetail.getCgpan()).toString());
			Log.log(5,
					"RPAction",
					"submitPANPayments",
					(new StringBuilder()).append("flag frm array ")
							.append(allocationDetail.getAllocatedFlag())
							.toString());
			while (cgpanIterator.hasNext()) {
				key = (String) cgpanIterator.next();
				value = (String) cgpans.get(key);
				Log.log(5, "RPAction", "submitPANPayments",
						(new StringBuilder()).append("key ").append(key)
								.toString());
				Log.log(5, "RPAction", "submitPANPayments",
						(new StringBuilder()).append("value ").append(value)
								.toString());
				cgpanPart = value.substring(value.indexOf("-") + 1,
						value.length());
				Log.log(5,
						"RPAction",
						"submitPANPayments",
						(new StringBuilder()).append("cgpanPart ")
								.append(cgpanPart).toString());
				if (value.startsWith(danNo)
						&& cgpanPart.equals(allocationDetail.getCgpan())
						&& allocatedFlags.get(key) != null
						&& ((String) allocatedFlags.get(key)).equals("Y")) {
					Log.log(5, "RPAction", "submitPANPayments",
							(new StringBuilder()).append("amount due  ")
									.append(allocationDetail.getAmountDue())
									.toString());
					allocationDetail.setAllocatedFlag("Y");
					isAvl = true;
					break;
				}
			}
			if (!isAvl) {
				Object removed = cgpans.remove((new StringBuilder())
						.append(strDanNo).append("-")
						.append(allocationDetail.getCgpan()).toString());
				Log.log(5,
						"RPAction",
						"submitPANPayments",
						(new StringBuilder()).append("Removed element")
								.append(removed).toString());
			}
			isAvl = false;
			cgpanIterator = cgpanSet.iterator();
		}

		Log.log(5, "RPAction", "submitPANPayments", (new StringBuilder())
				.append("Cgpan map size ").append(cgpans.size()).toString());
		return mapping.findForward("danSummary");
	}

	public ActionForward getAllocatedPaymentDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String methodName = "getAllocatedPaymentDetails";
		RPActionForm actionForm = (RPActionForm) form;
		Log.log(4, "RPAction", methodName, "Entering");
		actionForm.getCgpans().clear();
		String payId = request.getParameter("payId");
		String memberId = request.getParameter("memberId");
		actionForm.setPaymentId(payId);
		actionForm.setMemberId(memberId);
		Log.log(5, "RPAction", methodName,
				(new StringBuilder()).append("Got paymentId ").append(payId)
						.toString());
		Log.log(5, "RPAction", methodName,
				(new StringBuilder()).append("Got memberId ").append(memberId)
						.toString());
		RpProcessor rpProcessor = new RpProcessor();
		ArrayList paymentDetails = null;
		Log.log(5, "RPAction", methodName, "Before calling payment details ");
		paymentDetails = rpProcessor.getDANDetailsForReallocation(payId,
				memberId);
		Map danCgpanInfo = new HashMap();
		Map cgpans = actionForm.getCgpans();
		ArrayList panDetails = null;
		String tempDanNo = "";
		for (int i = 0; i < paymentDetails.size(); i++) {
			panDetails = null;
			AllocationDetail allocationDtl = (AllocationDetail) paymentDetails
					.get(i);
			Log.log(4, "RPAction", methodName,
					(new StringBuilder()).append("dan no ").append(tempDanNo)
							.toString());
			Log.log(4,
					"RPAction",
					methodName,
					(new StringBuilder()).append("cgpan ")
							.append(allocationDtl.getCgpan()).toString());
			if (allocationDtl.getAllocatedFlag().equals("Y"))
				cgpans.put(
						(new StringBuilder())
								.append(allocationDtl.getDanNo().replace('.',
										'_')).append("-")
								.append(allocationDtl.getCgpan()).toString(),
						allocationDtl.getCgpan());
			if (danCgpanInfo.containsKey(allocationDtl.getDanNo()))
				panDetails = (ArrayList) danCgpanInfo.get(allocationDtl
						.getDanNo());
			else
				panDetails = new ArrayList();
			panDetails.add(allocationDtl);
			danCgpanInfo.put(allocationDtl.getDanNo(), panDetails);
		}

		Log.log(5, "RPAction", methodName, "After calling payment details");
		Log.log(5, "RPAction", methodName,
				"Before dynaForm set in RPAction::getPaymentDetails");
		actionForm.setDanPanDetails(danCgpanInfo);
		actionForm.setCgpans(cgpans);
		Log.log(5, "RPAction", methodName,
				"After dynaForm set in RPAction::getPaymentDetails");
		return mapping.findForward("paymentDetails");
	}

	public ActionForward getPaymentDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String methodName = "getPaymentDetails";
		RPActionForm actionForm = (RPActionForm) form;
		Log.log(4, "RPAction", methodName, "Entering");
		String paymentId = actionForm.getPaymentId();
		Log.log(4, "RPAction", methodName, "Got paymentId");
		actionForm.getCgpans().clear();
		RpProcessor rpProcessor = new RpProcessor();
		ArrayList paymentDetails = null;
		Log.log(4, "RPAction", methodName, "Before calling payment details");
		paymentDetails = rpProcessor.getPaymentDetails(paymentId);
		HashMap bankIds = new HashMap();
		HashMap zoneIds = new HashMap();
		HashMap branchIds = new HashMap();
		PaymentDetails payDetails = (PaymentDetails) paymentDetails.get(0);
		actionForm.setInstrumentDate(payDetails.getInstrumentDate());
		for (int i = 1; i < paymentDetails.size(); i++) {
			DemandAdvice demandAdvice = (DemandAdvice) paymentDetails.get(i);
			demandAdvice.setAppropriated(demandAdvice.getAllocated());
			actionForm.setAppropriatedFlag((new StringBuilder()).append("key-")
					.append(i - 1).toString(), demandAdvice.getAllocated());
			bankIds.put((new StringBuilder()).append("key-").append(i - 1)
					.toString(), demandAdvice.getBankId());
			zoneIds.put((new StringBuilder()).append("key-").append(i - 1)
					.toString(), demandAdvice.getZoneId());
			branchIds.put((new StringBuilder()).append("key-").append(i - 1)
					.toString(), demandAdvice.getBranchId());
		}

		Log.log(4, "RPAction", methodName, "After calling payment details");
		Log.log(4, "RPAction", methodName,
				"Before dynaForm set in RPAction::getPaymentDetails");
		actionForm.setPaymentDetails(paymentDetails);
		actionForm.setDateOfRealisation(null);
		actionForm.setReceivedAmount(0.0D);
		actionForm.setPaymentId(paymentId);
		actionForm.setBankIds(bankIds);
		actionForm.setBranchIds(branchIds);
		actionForm.setZoneIds(zoneIds);
		Log.log(4, "RPAction", methodName,
				"After dynaForm set in RPAction::getPaymentDetails");
		return mapping.findForward("paymentDetails");
	}

	public ActionForward getPaymentDetailsForGF(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String methodName = "getPaymentDetails";
		RPActionForm actionForm = (RPActionForm) form;
		Log.log(4, "RPAction", methodName, "Entering");
		String paymentId = actionForm.getPaymentId();
		Log.log(4, "RPAction", methodName, "Got paymentId");
		actionForm.getCgpans().clear();
		RpProcessor rpProcessor = new RpProcessor();
		ArrayList paymentDetails = null;
		Log.log(4, "RPAction", methodName, "Before calling payment details");
		paymentDetails = rpProcessor.getPaymentDetails(paymentId);
		HashMap bankIds = new HashMap();
		HashMap zoneIds = new HashMap();
		HashMap branchIds = new HashMap();
		PaymentDetails payDetails = (PaymentDetails) paymentDetails.get(0);
		actionForm.setInstrumentDate(payDetails.getInstrumentDate());
		for (int i = 1; i < paymentDetails.size(); i++) {
			DemandAdvice demandAdvice = (DemandAdvice) paymentDetails.get(i);
			demandAdvice.setAppropriated(demandAdvice.getAllocated());
			actionForm.setAppropriatedFlag((new StringBuilder()).append("key-")
					.append(i - 1).toString(), demandAdvice.getAllocated());
			bankIds.put((new StringBuilder()).append("key-").append(i - 1)
					.toString(), demandAdvice.getBankId());
			zoneIds.put((new StringBuilder()).append("key-").append(i - 1)
					.toString(), demandAdvice.getZoneId());
			branchIds.put((new StringBuilder()).append("key-").append(i - 1)
					.toString(), demandAdvice.getBranchId());
		}

		Log.log(4, "RPAction", methodName, "After calling payment details");
		Log.log(4, "RPAction", methodName,
				"Before dynaForm set in RPAction::getPaymentDetails");
		actionForm.setPaymentDetails(paymentDetails);
		actionForm.setDateOfRealisation(null);
		actionForm.setReceivedAmount(0.0D);
		actionForm.setPaymentId(paymentId);
		actionForm.setBankIds(bankIds);
		actionForm.setBranchIds(branchIds);
		actionForm.setZoneIds(zoneIds);
		Log.log(4, "RPAction", methodName,
				"After dynaForm set in RPAction::getPaymentDetails");
		return mapping.findForward("paymentDetails");
	}

	public ActionForward getPaymentDetailsForASF(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String methodName = "getPaymentDetails";
		RPActionForm actionForm = (RPActionForm) form;
		Log.log(4, "RPAction", methodName, "Entering");
		String paymentId = actionForm.getPaymentId();
		Log.log(4, "RPAction", methodName, "Got paymentId");
		actionForm.getCgpans().clear();
		RpProcessor rpProcessor = new RpProcessor();
		ArrayList paymentDetails = null;
		Log.log(4, "RPAction", methodName, "Before calling payment details");
		paymentDetails = rpProcessor.getPaymentDetails(paymentId);
		HashMap bankIds = new HashMap();
		HashMap zoneIds = new HashMap();
		HashMap branchIds = new HashMap();
		PaymentDetails payDetails = (PaymentDetails) paymentDetails.get(0);
		actionForm.setInstrumentDate(payDetails.getInstrumentDate());
		for (int i = 1; i < paymentDetails.size(); i++) {
			DemandAdvice demandAdvice = (DemandAdvice) paymentDetails.get(i);
			demandAdvice.setAppropriated(demandAdvice.getAllocated());
			actionForm.setAppropriatedFlag((new StringBuilder()).append("key-")
					.append(i - 1).toString(), demandAdvice.getAllocated());
			bankIds.put((new StringBuilder()).append("key-").append(i - 1)
					.toString(), demandAdvice.getBankId());
			zoneIds.put((new StringBuilder()).append("key-").append(i - 1)
					.toString(), demandAdvice.getZoneId());
			branchIds.put((new StringBuilder()).append("key-").append(i - 1)
					.toString(), demandAdvice.getBranchId());
		}

		Log.log(4, "RPAction", methodName, "After calling payment details");
		Log.log(4, "RPAction", methodName,
				"Before dynaForm set in RPAction::getPaymentDetails");
		actionForm.setPaymentDetails(paymentDetails);
		actionForm.setDateOfRealisation(null);
		actionForm.setReceivedAmount(0.0D);
		actionForm.setPaymentId(paymentId);
		actionForm.setBankIds(bankIds);
		actionForm.setBranchIds(branchIds);
		actionForm.setZoneIds(zoneIds);
		Log.log(4, "RPAction", methodName,
				"After dynaForm set in RPAction::getPaymentDetails");
		return mapping.findForward("paymentDetails");
	}

	public ActionForward getPaymentDetailsForCLAIM(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String methodName = "getPaymentDetails";
		RPActionForm actionForm = (RPActionForm) form;
		Log.log(4, "RPAction", methodName, "Entering");
		String paymentId = actionForm.getPaymentId();
		Log.log(4, "RPAction", methodName, "Got paymentId");
		actionForm.getCgpans().clear();
		RpProcessor rpProcessor = new RpProcessor();
		ArrayList paymentDetails = null;
		Log.log(4, "RPAction", methodName, "Before calling payment details");
		paymentDetails = rpProcessor.getPaymentDetails(paymentId);
		HashMap bankIds = new HashMap();
		HashMap zoneIds = new HashMap();
		HashMap branchIds = new HashMap();
		PaymentDetails payDetails = (PaymentDetails) paymentDetails.get(0);
		actionForm.setInstrumentDate(payDetails.getInstrumentDate());
		for (int i = 1; i < paymentDetails.size(); i++) {
			DemandAdvice demandAdvice = (DemandAdvice) paymentDetails.get(i);
			demandAdvice.setAppropriated(demandAdvice.getAllocated());
			actionForm.setAppropriatedFlag((new StringBuilder()).append("key-")
					.append(i - 1).toString(), demandAdvice.getAllocated());
			bankIds.put((new StringBuilder()).append("key-").append(i - 1)
					.toString(), demandAdvice.getBankId());
			zoneIds.put((new StringBuilder()).append("key-").append(i - 1)
					.toString(), demandAdvice.getZoneId());
			branchIds.put((new StringBuilder()).append("key-").append(i - 1)
					.toString(), demandAdvice.getBranchId());
		}

		Log.log(4, "RPAction", methodName, "After calling payment details");
		Log.log(4, "RPAction", methodName,
				"Before dynaForm set in RPAction::getPaymentDetails");
		actionForm.setPaymentDetails(paymentDetails);
		actionForm.setDateOfRealisation(null);
		actionForm.setReceivedAmount(0.0D);
		actionForm.setPaymentId(paymentId);
		actionForm.setBankIds(bankIds);
		actionForm.setBranchIds(branchIds);
		actionForm.setZoneIds(zoneIds);
		Log.log(4, "RPAction", methodName,
				"After dynaForm set in RPAction::getPaymentDetails");
		return mapping.findForward("paymentDetails");
	}

	public ActionForward getClaimPaymentDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String methodName = "getClaimPaymentDetails";
		RPActionForm actionForm = (RPActionForm) form;
		actionForm.resetWhenRequired();
		Log.log(4, "RPAction", methodName, "Entering");
		Log.log(5, "RPAction", methodName,
				(new StringBuilder()).append("actionForm.getSelectMember() ")
						.append(actionForm.getSelectMember()).toString());
		IFProcessor ifProcessor = new IFProcessor();
		ArrayList instruments = ifProcessor.getInstrumentTypes("G");
		actionForm.setInstruments(instruments);
		Log.log(4, "RPAction", methodName, "Exited");
		return mapping.findForward("success");
	}

	public ActionForward insertPaymentDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String methodName = "insertPaymentDetails";
		RPActionForm actionForm = (RPActionForm) form;
		Log.log(4, "RPAction", methodName, "Entering");
		PaymentDetails paymentDetails = new PaymentDetails();
		Log.log(5, "RPAction", methodName,
				(new StringBuilder()).append("actionForm.getModeOfPayment() ")
						.append(actionForm.getModeOfPayment()).toString());
		BeanUtils.copyProperties(paymentDetails, actionForm);
		paymentDetails.setCollectingBank(actionForm.getCollectingBankName());
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder())
						.append("paymentDetails.getModeOfPayment() ")
						.append(paymentDetails.getModeOfPayment()).toString());
		Log.log(5, "RPAction", methodName,
				(new StringBuilder()).append("actionForm.getModeOfPayment() ")
						.append(actionForm.getModeOfPayment()).toString());
		RpProcessor rpProcessor = new RpProcessor();
		String paymentId = rpProcessor.insertPaymentByCGTSI(paymentDetails);
		Log.log(5, "RPAction", methodName,
				(new StringBuilder()).append("paymentId ").append(paymentId)
						.toString());
		String message = (new StringBuilder())
				.append("Payment details stored successfull. Payment id is ")
				.append(paymentId).toString();
		request.setAttribute("message", message);
		Log.log(4, "RPAction", methodName, "Exited");
		return mapping.findForward("success");
	}

	public ActionForward appropriatePayments(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		System.out.println(" appropriatePayments ");
		RPActionForm actionForm = (RPActionForm) form;
		RpProcessor rpProcessor = new RpProcessor();
		ArrayList demandAdvices = new ArrayList();
		String methodName = "appropriatePayments";
		String danId = "";
		String cgpan = "";
		String allocatedFlag = "";
		String appropriatedFlag = "";
		String remark = "";
		String paymentId = "";
		DemandAdvice demandAdvice = null;
		Log.log(4, "RPAction", methodName, "Entered");
		Map danIds = actionForm.getDanIds();
		Map cgpans = actionForm.getCgpans();
		Map remarks = actionForm.getRemarks();
		Map allocatedFlags = actionForm.getAllocatedFlags();
		Map appropriatedFlags = actionForm.getAppropriatedFlags();
		Map amounts = actionForm.getAmountsRaised();
		Map penalties = actionForm.getPenalties();
		Map bankIds = actionForm.getBankIds();
		Map zoneIds = actionForm.getZoneIds();
		Map branchIds = actionForm.getBranchIds();
		Log.log(4, "RPAction", methodName, "Assigned CGPAN details to hashmap");
		Set danIdSet = danIds.keySet();
		Set allocatedFlagSet = allocatedFlags.keySet();
		Set appropriatedFlagSet = appropriatedFlags.keySet();
		Set cgpanSet = cgpans.keySet();
		Set remarksSet = remarks.keySet();
		Set amountsSet = amounts.keySet();
		Set penaltySet = penalties.keySet();
		Set bankIdSet = bankIds.keySet();
		Set zoneIdSet = zoneIds.keySet();
		Set branchIdSet = branchIds.keySet();
		Log.log(4, "RPAction", methodName, "Assigned CGPAN details to Set");
		Iterator cgpanIterator = cgpanSet.iterator();
		Iterator bankIdIterator = bankIdSet.iterator();
		Iterator zoneIdIterator = zoneIdSet.iterator();
		Iterator branchIdIterator = branchIdSet.iterator();
		Log.log(4, "RPAction", methodName, "Assigned CGPAN details to Iterator");
		User user = getUserInformation(request);
		String userId = user.getUserId();
		paymentId = actionForm.getPaymentId();
		double appropriatedAmount = 0.0D;
		for (; cgpanIterator.hasNext(); Log.log(4, "RPAction", methodName,
				"DemandAdvices added to ArrayList")) {
			String cgpanKey = (String) cgpanIterator.next();
			danId = (String) danIds.get(cgpanKey);
			cgpan = (String) cgpans.get(cgpanKey);
			allocatedFlag = (String) allocatedFlags.get(cgpanKey);
			appropriatedFlag = (String) appropriatedFlags.get(cgpanKey);
			remark = (String) remarks.get(cgpanKey);
			double amount = Double.parseDouble((String) amounts.get(cgpanKey));
			double penalty = Double.parseDouble((String) penalties
					.get(cgpanKey));
			Log.log(4,
					"RPAction",
					methodName,
					(new StringBuilder())
							.append(" inside iterator - dan id - ")
							.append(danId).toString());
			Log.log(4, "RPAction", methodName,
					(new StringBuilder()).append(" inside iterator - cgpan - ")
							.append(cgpan).toString());
			Log.log(4,
					"RPAction",
					methodName,
					(new StringBuilder())
							.append(" inside iterator - allocated flag - ")
							.append(allocatedFlag).toString());
			Log.log(4,
					"RPAction",
					methodName,
					(new StringBuilder())
							.append(" inside iterator - appropriated flag - ")
							.append(appropriatedFlag).toString());
			Log.log(4,
					"RPAction",
					methodName,
					(new StringBuilder())
							.append(" inside iterator - amount - ")
							.append(amount).toString());
			Log.log(4,
					"RPAction",
					methodName,
					(new StringBuilder())
							.append(" inside iterator - penalty - ")
							.append(penalty).toString());
			demandAdvice = new DemandAdvice();
			demandAdvice.setDanNo(danId);
			demandAdvice.setCgpan(cgpan);
			demandAdvice.setReason(remark);
			demandAdvice.setAmountRaised(amount);
			demandAdvice.setPenalty(penalty);
			demandAdvice.setPaymentId(paymentId);
			demandAdvice.setAllocated(appropriatedFlag);
			demandAdvice.setAppropriated(appropriatedFlag);
			demandAdvice.setUserId(userId);
			demandAdvice.setBankId((String) bankIds.get(bankIdIterator.next()));
			demandAdvice.setZoneId((String) zoneIds.get(zoneIdIterator.next()));
			demandAdvice.setBranchId((String) branchIds.get(branchIdIterator
					.next()));
			Log.log(4, "RPAction", methodName,
					(new StringBuilder()).append(" inside iterator - cgpan - ")
							.append(demandAdvice.getCgpan()).toString());
			Log.log(4,
					"RPAction",
					methodName,
					(new StringBuilder())
							.append(" inside iterator - allocated flag - ")
							.append(demandAdvice.getAllocated()).toString());
			Log.log(4,
					"RPAction",
					methodName,
					(new StringBuilder())
							.append(" inside iterator - appropriated flag - ")
							.append(demandAdvice.getAppropriated()).toString());
			if (appropriatedFlag.equals("Y"))
				appropriatedAmount += amount;
			demandAdvices.add(demandAdvice);
			Log.log(4,
					"RPAction",
					methodName,
					(new StringBuilder())
							.append(" inside iterator - adding cgpan to demand advice list - ")
							.append(cgpan).toString());
		}
		Date realisationDate = actionForm.getDateOfRealisation();
		double receivedAmount = actionForm.getReceivedAmount();
		RealisationDetail realisationDetail = new RealisationDetail();
		realisationDetail.setPaymentId(paymentId);
		realisationDetail.setRealisationAmount(receivedAmount);
		realisationDetail.setRealisationDate(realisationDate);
		if (receivedAmount < appropriatedAmount) {
			double shortLimit = appropriatedAmount - receivedAmount;
			throw new ShortExceedsLimitException(
					(new StringBuilder())
							.append("Received Amount is less than Allocated Amount by Rs.")
							.append(shortLimit).toString());
		}
		if (receivedAmount > appropriatedAmount) {
			double excessLimit = receivedAmount - appropriatedAmount;
			throw new ShortExceedsLimitException(
					(new StringBuilder())
							.append("Received Amount is greater than Allocated Amount by Rs.")
							.append(excessLimit).toString());
		} else {
			double shortOrExcess = rpProcessor.appropriatePayment(
					demandAdvices, realisationDetail, request.getSession(false)
							.getServletContext().getRealPath(""));
			request.setAttribute(
					"message",
					(new StringBuilder())
							.append("Payment Amount Appropriated Successfully.<BR><BR>Total Received Amount : ")
							.append(receivedAmount)
							.append("<BR>Total Appropriated Amount : ")
							.append(appropriatedAmount)
							.append("<BR>Short / Excess : ")
							.append(shortOrExcess).toString());
			return mapping.findForward("success");
		}
	}

	public ActionForward appropriatePaymentsForGF(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RPActionForm actionForm = (RPActionForm) form;
		RpProcessor rpProcessor = new RpProcessor();
		ArrayList demandAdvices = new ArrayList();
		String methodName = "appropriatePayments";
		String danId = "";
		String cgpan = "";
		String allocatedFlag = "";
		String appropriatedFlag = "";
		String remark = "";
		String paymentId = "";
		DemandAdvice demandAdvice = null;
		Log.log(4, "RPAction", methodName, "Entered");
		Map danIds = actionForm.getDanIds();
		Map cgpans = actionForm.getCgpans();
		Map remarks = actionForm.getRemarks();
		Map allocatedFlags = actionForm.getAllocatedFlags();
		Map appropriatedFlags = actionForm.getAppropriatedFlags();
		Map amounts = actionForm.getAmountsRaised();
		Map penalties = actionForm.getPenalties();
		Map bankIds = actionForm.getBankIds();
		Map zoneIds = actionForm.getZoneIds();
		Map branchIds = actionForm.getBranchIds();
		Log.log(4, "RPAction", methodName, "Assigned CGPAN details to hashmap");
		Set danIdSet = danIds.keySet();
		Set allocatedFlagSet = allocatedFlags.keySet();
		Set appropriatedFlagSet = appropriatedFlags.keySet();
		Set cgpanSet = cgpans.keySet();
		Set remarksSet = remarks.keySet();
		Set amountsSet = amounts.keySet();
		Set penaltySet = penalties.keySet();
		Set bankIdSet = bankIds.keySet();
		Set zoneIdSet = zoneIds.keySet();
		Set branchIdSet = branchIds.keySet();
		Log.log(4, "RPAction", methodName, "Assigned CGPAN details to Set");
		Iterator cgpanIterator = cgpanSet.iterator();
		Iterator bankIdIterator = bankIdSet.iterator();
		Iterator zoneIdIterator = zoneIdSet.iterator();
		Iterator branchIdIterator = branchIdSet.iterator();
		Log.log(4, "RPAction", methodName, "Assigned CGPAN details to Iterator");
		User user = getUserInformation(request);
		String userId = user.getUserId();
		paymentId = actionForm.getPaymentId();
		double appropriatedAmount = 0.0D;
		for (; cgpanIterator.hasNext(); Log.log(4, "RPAction", methodName,
				"DemandAdvices added to ArrayList")) {
			String cgpanKey = (String) cgpanIterator.next();
			danId = (String) danIds.get(cgpanKey);
			cgpan = (String) cgpans.get(cgpanKey);
			allocatedFlag = (String) allocatedFlags.get(cgpanKey);
			appropriatedFlag = (String) appropriatedFlags.get(cgpanKey);
			remark = (String) remarks.get(cgpanKey);
			double amount = Double.parseDouble((String) amounts.get(cgpanKey));
			double penalty = Double.parseDouble((String) penalties
					.get(cgpanKey));
			Log.log(Log.DEBUG, "RPAction", methodName, (new StringBuilder())
					.append(" inside iterator - dan id - ").append(danId)
					.append(" inside iterator - cgpan - ").append(cgpan)
					.toString());
			Log.log(Log.DEBUG,
					"RPAction",
					methodName,
					(new StringBuilder())
							.append(" inside iterator - allocated flag - ")
							.append(allocatedFlag)
							.append(" inside iterator - appropriated flag - ")
							.append(appropriatedFlag).toString());
			Log.log(Log.DEBUG, "RPAction", methodName, (new StringBuilder())
					.append(" inside iterator - amount - ").append(amount)
					.append(" inside iterator - penalty - ").append(penalty)
					.toString());
			if (cgpan == null || cgpan.equals("") || danId == null
					|| danId.equals("") || allocatedFlag == null) {
				throw new MessageException("Problem in data. Check for "
						+ danId);
			}
			demandAdvice = new DemandAdvice();
			demandAdvice.setDanNo(danId);
			demandAdvice.setCgpan(cgpan);
			demandAdvice.setReason(remark);
			demandAdvice.setAmountRaised(amount);
			demandAdvice.setPenalty(penalty);
			demandAdvice.setPaymentId(paymentId);
			demandAdvice.setAllocated(appropriatedFlag);
			demandAdvice.setAppropriated(appropriatedFlag);
			demandAdvice.setUserId(userId);
			demandAdvice.setBankId((String) bankIds.get(bankIdIterator.next()));
			demandAdvice.setZoneId((String) zoneIds.get(zoneIdIterator.next()));
			demandAdvice.setBranchId((String) branchIds.get(branchIdIterator
					.next()));

			if (appropriatedFlag.equals("Y"))
				appropriatedAmount += amount;
			demandAdvices.add(demandAdvice);
			Log.log(4,
					"RPAction",
					methodName,
					(new StringBuilder())
							.append(" inside iterator - adding cgpan to demand advice list - ")
							.append(cgpan).toString());
		}

		Date realisationDate = actionForm.getDateOfRealisation();
		Log.log(Log.DEBUG, "RPAction", methodName, " realisationDate--"
				+ realisationDate);
		if (realisationDate == null) {
			throw new MessageException("Realisation date missing. Try again.");
		}

		double receivedAmount = actionForm.getReceivedAmount();
		RealisationDetail realisationDetail = new RealisationDetail();
		realisationDetail.setPaymentId(paymentId);
		realisationDetail.setRealisationAmount(receivedAmount);
		realisationDetail.setRealisationDate(realisationDate);
		if (receivedAmount < appropriatedAmount) {
			double shortLimit = appropriatedAmount - receivedAmount;
			throw new ShortExceedsLimitException(
					(new StringBuilder())
							.append("Received Amount is less than Allocated Amount by Rs.")
							.append(shortLimit).toString());
		}
		if (receivedAmount > appropriatedAmount) {
			double excessLimit = receivedAmount - appropriatedAmount;
			throw new ShortExceedsLimitException(
					(new StringBuilder())
							.append("Received Amount is greater than Allocated Amount by Rs.")
							.append(excessLimit).toString());
		} else {
			double shortOrExcess = rpProcessor.appropriatePayment(
					demandAdvices, realisationDetail, request.getSession(false)
							.getServletContext().getRealPath(""));
			request.setAttribute(
					"message",
					(new StringBuilder())
							.append("Payment Amount Appropriated Successfully.<BR><BR>Total Received Amount : ")
							.append(receivedAmount)
							.append("<BR>Total Appropriated Amount : ")
							.append(appropriatedAmount)
							.append("<BR>Short / Excess : ")
							.append(shortOrExcess).toString());
			return mapping.findForward("success");
		}
	}

	public ActionForward appropriatePaymentsForASF(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RPActionForm actionForm = (RPActionForm) form;
		RpProcessor rpProcessor = new RpProcessor();
		ArrayList demandAdvices = new ArrayList();
		String methodName = "appropriatePayments";
		String danId = "";
		String cgpan = "";
		String allocatedFlag = "";
		String appropriatedFlag = "";
		String remark = "";
		String paymentId = "";
		DemandAdvice demandAdvice = null;
		Log.log(4, "RPAction", methodName, "Entered");
		Map danIds = actionForm.getDanIds();
		Map cgpans = actionForm.getCgpans();
		Map remarks = actionForm.getRemarks();
		Map allocatedFlags = actionForm.getAllocatedFlags();
		Map appropriatedFlags = actionForm.getAppropriatedFlags();
		Map amounts = actionForm.getAmountsRaised();
		Map penalties = actionForm.getPenalties();
		Map bankIds = actionForm.getBankIds();
		Map zoneIds = actionForm.getZoneIds();
		Map branchIds = actionForm.getBranchIds();
		Log.log(4, "RPAction", methodName, "Assigned CGPAN details to hashmap");
		Set danIdSet = danIds.keySet();
		Set allocatedFlagSet = allocatedFlags.keySet();
		Set appropriatedFlagSet = appropriatedFlags.keySet();
		Set cgpanSet = cgpans.keySet();
		Set remarksSet = remarks.keySet();
		Set amountsSet = amounts.keySet();
		Set penaltySet = penalties.keySet();
		Set bankIdSet = bankIds.keySet();
		Set zoneIdSet = zoneIds.keySet();
		Set branchIdSet = branchIds.keySet();
		Log.log(4, "RPAction", methodName, "Assigned CGPAN details to Set");
		Iterator cgpanIterator = cgpanSet.iterator();
		Iterator bankIdIterator = bankIdSet.iterator();
		Iterator zoneIdIterator = zoneIdSet.iterator();
		Iterator branchIdIterator = branchIdSet.iterator();
		Log.log(4, "RPAction", methodName, "Assigned CGPAN details to Iterator");
		User user = getUserInformation(request);
		String userId = user.getUserId();
		paymentId = actionForm.getPaymentId();
		double appropriatedAmount = 0.0D;
		for (; cgpanIterator.hasNext(); Log.log(4, "RPAction", methodName,
				"DemandAdvices added to ArrayList")) {
			String cgpanKey = (String) cgpanIterator.next();
			danId = (String) danIds.get(cgpanKey);
			cgpan = (String) cgpans.get(cgpanKey);
			allocatedFlag = (String) allocatedFlags.get(cgpanKey);
			appropriatedFlag = (String) appropriatedFlags.get(cgpanKey);
			remark = (String) remarks.get(cgpanKey);
			double amount = Double.parseDouble((String) amounts.get(cgpanKey));
			double penalty = Double.parseDouble((String) penalties
					.get(cgpanKey));
			Log.log(4,
					"RPAction",
					methodName,
					(new StringBuilder())
							.append(" inside iterator - dan id - ")
							.append(danId).toString());
			Log.log(4, "RPAction", methodName,
					(new StringBuilder()).append(" inside iterator - cgpan - ")
							.append(cgpan).toString());
			Log.log(4,
					"RPAction",
					methodName,
					(new StringBuilder())
							.append(" inside iterator - allocated flag - ")
							.append(allocatedFlag).toString());
			Log.log(4,
					"RPAction",
					methodName,
					(new StringBuilder())
							.append(" inside iterator - appropriated flag - ")
							.append(appropriatedFlag).toString());
			Log.log(4,
					"RPAction",
					methodName,
					(new StringBuilder())
							.append(" inside iterator - amount - ")
							.append(amount).toString());
			Log.log(4,
					"RPAction",
					methodName,
					(new StringBuilder())
							.append(" inside iterator - penalty - ")
							.append(penalty).toString());
			demandAdvice = new DemandAdvice();
			demandAdvice.setDanNo(danId);
			demandAdvice.setCgpan(cgpan);
			demandAdvice.setReason(remark);
			demandAdvice.setAmountRaised(amount);
			demandAdvice.setPenalty(penalty);
			demandAdvice.setPaymentId(paymentId);
			demandAdvice.setAllocated(appropriatedFlag);
			demandAdvice.setAppropriated(appropriatedFlag);
			demandAdvice.setUserId(userId);
			demandAdvice.setBankId((String) bankIds.get(bankIdIterator.next()));
			demandAdvice.setZoneId((String) zoneIds.get(zoneIdIterator.next()));
			demandAdvice.setBranchId((String) branchIds.get(branchIdIterator
					.next()));
			Log.log(4, "RPAction", methodName,
					(new StringBuilder()).append(" inside iterator - cgpan - ")
							.append(demandAdvice.getCgpan()).toString());
			Log.log(4,
					"RPAction",
					methodName,
					(new StringBuilder())
							.append(" inside iterator - allocated flag - ")
							.append(demandAdvice.getAllocated()).toString());
			Log.log(4,
					"RPAction",
					methodName,
					(new StringBuilder())
							.append(" inside iterator - appropriated flag - ")
							.append(demandAdvice.getAppropriated()).toString());
			if (appropriatedFlag.equals("Y"))
				appropriatedAmount += amount;
			demandAdvices.add(demandAdvice);
			Log.log(4,
					"RPAction",
					methodName,
					(new StringBuilder())
							.append(" inside iterator - adding cgpan to demand advice list - ")
							.append(cgpan).toString());
		}

		Date realisationDate = actionForm.getDateOfRealisation();
		double receivedAmount = actionForm.getReceivedAmount();
		RealisationDetail realisationDetail = new RealisationDetail();
		realisationDetail.setPaymentId(paymentId);
		realisationDetail.setRealisationAmount(receivedAmount);
		realisationDetail.setRealisationDate(realisationDate);
		if (receivedAmount < appropriatedAmount) {
			double shortLimit = appropriatedAmount - receivedAmount;
			throw new ShortExceedsLimitException(
					(new StringBuilder())
							.append("Received Amount is less than Allocated Amount by Rs.")
							.append(shortLimit).toString());
		}
		if (receivedAmount > appropriatedAmount) {
			double excessLimit = receivedAmount - appropriatedAmount;
			throw new ShortExceedsLimitException(
					(new StringBuilder())
							.append("Received Amount is greater than Allocated Amount by Rs.")
							.append(excessLimit).toString());
		} else {
			double shortOrExcess = rpProcessor.appropriatePayment(
					demandAdvices, realisationDetail, request.getSession(false)
							.getServletContext().getRealPath(""));
			request.setAttribute(
					"message",
					(new StringBuilder())
							.append("Payment Amount Appropriated Successfully.<BR><BR>Total Received Amount : ")
							.append(receivedAmount)
							.append("<BR>Total Appropriated Amount : ")
							.append(appropriatedAmount)
							.append("<BR>Short / Excess : ")
							.append(shortOrExcess).toString());
			return mapping.findForward("success");
		}
	}

	public ActionForward generateCGDAN(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		DynaActionForm dynaForm = (DynaActionForm) form;
		String mliName = (String) dynaForm.get("selectMember");
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		Date sDate = (Date) dynaForm.get("fromdt");
		Date eDate = (Date) dynaForm.get("todt");
		String stDate = String.valueOf(sDate);
		String estDate = String.valueOf(eDate);
		if (stDate == null || stDate.equals(""))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());
		if (estDate == null || estDate.equals(""))
			endDate = null;
		else if (estDate != null)
			endDate = new java.sql.Date(eDate.getTime());
		Log.log(4,
				"RPAction",
				"generateCGDAN",
				(new StringBuilder()).append("Selected Member Id : ")
						.append(mliName).toString());
		String forwardPage = "";
		User user = getUserInformation(request);
		Log.log(4,
				"RPAction",
				"generateCGDAN",
				(new StringBuilder()).append("Logged in user: ")
						.append(user.getUserId()).toString());
		if (mliName.equals("")) {
			Log.log(4, "RPAction", "generateCGDAN",
					"Fetching Member Details for whom CGDAN has to be generated");
			ArrayList memberNames = registration.getAllMembers();
			ArrayList memberDetails = new ArrayList(memberNames.size());
			for (int i = 0; i < memberNames.size(); i++) {
				MLIInfo mliInfo = (MLIInfo) memberNames.get(i);
				String mli = "";
				mli = (new StringBuilder()).append("(")
						.append(mliInfo.getBankId())
						.append(mliInfo.getZoneId())
						.append(mliInfo.getBranchId()).append(")")
						.append(mliInfo.getShortName()).toString();
				if (mliInfo.getBranchName() != null
						&& !mliInfo.getBranchName().equals("")
						&& (mliInfo.getZoneName() == null || mliInfo
								.getZoneName().equals("")))
					mli = (new StringBuilder()).append(mli).append(",")
							.append(mliInfo.getBranchName()).toString();
				else if ((mliInfo.getBranchName() == null || mliInfo
						.getBranchName().equals(""))
						&& mliInfo.getZoneName() != null
						&& !mliInfo.getZoneName().equals(""))
					mli = (new StringBuilder()).append(mli).append(",")
							.append(mliInfo.getZoneName()).toString();
				else if (mliInfo.getBranchName() != null
						&& !mliInfo.getBranchName().equals("")
						&& mliInfo.getZoneName() != null
						&& !mliInfo.getZoneName().equals(""))
					mli = (new StringBuilder()).append(mli).append(",")
							.append(mliInfo.getZoneName()).append(",")
							.append(mliInfo.getBranchName()).toString();
				memberDetails.add(mli);
			}

			Log.log(4, "RPAction", "generateCGDAN",
					"Fetched Member Details for whom CGDAN has to be generated");
			dynaForm.set("mliNames", memberDetails);
			request.setAttribute("TARGET_URL",
					MenuOptions.getMenuAction("RP_GENERATE_CGDAN"));
			HttpSession session = request.getSession(false);
			session.setAttribute("DAN_TYPE", "CGDAN");
			forwardPage = "memberInfo";
		} else {
			if (!mliName.equals("All"))
				mliName = mliName.substring(1, 13);
			Log.log(4, "RPAction", "generateCGDAN", (new StringBuilder())
					.append("mli name ").append(mliName).toString());
			RpProcessor rpProcessor = new RpProcessor();
			try {
				String message = "";
				if ((startDate == null || startDate.equals(""))
						&& (endDate == null || endDate.equals("")))
					rpProcessor.generateCGDAN(user, mliName);
				else
					rpProcessor
							.generateCGDAN(user, mliName, startDate, endDate);
				message = "CGDAN generated Successfully";
				request.setAttribute("message", message);
				forwardPage = "success";
			} catch (Exception exp) {
				forwardPage = "success1";
				request.setAttribute("message",
						"No Applications Available For CGDAN Generation");
			}
		}
		return mapping.findForward(forwardPage);
	}

	public ActionForward generateSFDAN(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String methodName = "generateSFDAN";
		Log.log(4, "RPAction", methodName, "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		String mliName = (String) dynaForm.get("selectMember");
		Log.log(4,
				"RPAction",
				methodName,
				(new StringBuilder()).append("Selected Member Id : ")
						.append(mliName).toString());
		String forwardPage = "";
		User user = getUserInformation(request);
		Log.log(4,
				"RPAction",
				"generateCGDAN",
				(new StringBuilder()).append("Logged in user: ")
						.append(user.getUserId()).toString());
		if (mliName.equals("")) {
			Log.log(5, "RPAction", methodName,
					"Fetching Member Details for whom SFDAN has to be generated");
			ArrayList memberNames = registration.getAllMembers();
			ArrayList memberDetails = new ArrayList(memberNames.size());
			for (int i = 0; i < memberNames.size(); i++) {
				MLIInfo mliInfo = (MLIInfo) memberNames.get(i);
				String mli = "";
				mli = (new StringBuilder()).append("(")
						.append(mliInfo.getBankId())
						.append(mliInfo.getZoneId())
						.append(mliInfo.getBranchId()).append(")")
						.append(mliInfo.getShortName()).toString();
				if (mliInfo.getBranchName() != null
						&& !mliInfo.getBranchName().equals("")
						&& (mliInfo.getZoneName() == null || mliInfo
								.getZoneName().equals("")))
					mli = (new StringBuilder()).append(mli).append(",")
							.append(mliInfo.getBranchName()).toString();
				else if ((mliInfo.getBranchName() == null || mliInfo
						.getBranchName().equals(""))
						&& mliInfo.getZoneName() != null
						&& !mliInfo.getZoneName().equals(""))
					mli = (new StringBuilder()).append(mli).append(",")
							.append(mliInfo.getZoneName()).toString();
				else if (mliInfo.getBranchName() != null
						&& !mliInfo.getBranchName().equals("")
						&& mliInfo.getZoneName() != null
						&& !mliInfo.getZoneName().equals(""))
					mli = (new StringBuilder()).append(mli).append(",")
							.append(mliInfo.getZoneName()).append(",")
							.append(mliInfo.getBranchName()).toString();
				memberDetails.add(mli);
			}

			Log.log(5, "RPAction", methodName,
					"Fetched Member Details for whom SFDAN has to be generated");
			dynaForm.set("mliNames", memberDetails);
			request.setAttribute("TARGET_URL",
					MenuOptions.getMenuAction("RP_GENERATE_SFDAN"));
			HttpSession session = request.getSession(false);
			session.setAttribute("DAN_TYPE", "SFDAN");
			forwardPage = "memberInfo";
		} else {
			forwardPage = "";
			String message = "";
			try {
				Log.log(5, "RPAction", methodName,
						"Entering routine to generate SFDAN for all members");
				RpProcessor rpProcessor = new RpProcessor();
				if (mliName.equalsIgnoreCase("All")) {
					rpProcessor.generateSFDAN(user, null, null, null);
				} else {
					mliName = mliName.substring(1, 13);
					rpProcessor.generateSFDAN(user, mliName.substring(0, 4),
							mliName.substring(4, 8), mliName.substring(8, 12));
				}
				request.setAttribute("message", "SFDAN generated successfully");
				forwardPage = "success";
			} catch (Exception exp) {
				forwardPage = "success1";
				request.setAttribute("message",
						" No Applications Available For SFDAN Generation");
			}
		}
		Log.log(4, "RPAction", methodName, "Exited");
		return mapping.findForward(forwardPage);
	}

	public ActionForward generateSFDANEXP(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String methodName = "generateSFDANEXP";
		Log.log(4, "RPAction", methodName, "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		String mliName = (String) dynaForm.get("selectMember");
		Log.log(4,
				"RPAction",
				methodName,
				(new StringBuilder()).append("Selected Member Id : ")
						.append(mliName).toString());
		String forwardPage = "";
		User user = getUserInformation(request);
		Log.log(4,
				"RPAction",
				"generateCGDAN",
				(new StringBuilder()).append("Logged in user: ")
						.append(user.getUserId()).toString());
		if (mliName.equals("")) {
			Log.log(5, "RPAction", methodName,
					"Fetching Member Details for whom SFDANEXP has to be generated");
			ArrayList memberNames = registration.getAllMembers();
			ArrayList memberDetails = new ArrayList(memberNames.size());
			for (int i = 0; i < memberNames.size(); i++) {
				MLIInfo mliInfo = (MLIInfo) memberNames.get(i);
				String mli = "";
				mli = (new StringBuilder()).append("(")
						.append(mliInfo.getBankId())
						.append(mliInfo.getZoneId())
						.append(mliInfo.getBranchId()).append(")")
						.append(mliInfo.getShortName()).toString();
				if (mliInfo.getBranchName() != null
						&& !mliInfo.getBranchName().equals("")
						&& (mliInfo.getZoneName() == null || mliInfo
								.getZoneName().equals("")))
					mli = (new StringBuilder()).append(mli).append(",")
							.append(mliInfo.getBranchName()).toString();
				else if ((mliInfo.getBranchName() == null || mliInfo
						.getBranchName().equals(""))
						&& mliInfo.getZoneName() != null
						&& !mliInfo.getZoneName().equals(""))
					mli = (new StringBuilder()).append(mli).append(",")
							.append(mliInfo.getZoneName()).toString();
				else if (mliInfo.getBranchName() != null
						&& !mliInfo.getBranchName().equals("")
						&& mliInfo.getZoneName() != null
						&& !mliInfo.getZoneName().equals(""))
					mli = (new StringBuilder()).append(mli).append(",")
							.append(mliInfo.getZoneName()).append(",")
							.append(mliInfo.getBranchName()).toString();
				memberDetails.add(mli);
			}

			Log.log(5, "RPAction", methodName,
					"Fetched Member Details for whom SFDANEXP has to be generated");
			dynaForm.set("mliNames", memberDetails);
			request.setAttribute("TARGET_URL",
					MenuOptions.getMenuAction("RP_GENERATE_SFDAN"));
			HttpSession session = request.getSession(false);
			session.setAttribute("DAN_TYPE", "SFDAN");
			forwardPage = "memberInfo";
		} else {
			forwardPage = "";
			String message = "";
			try {
				Log.log(5, "RPAction", methodName,
						"Entering routine to generate SFDANEXP for all members");
				RpProcessor rpProcessor = new RpProcessor();
				if (mliName.equalsIgnoreCase("All")) {
					rpProcessor.generateSFDAN(user, null, null, null);
				} else {
					mliName = mliName.substring(1, 13);
					rpProcessor.generateSFDANEXP(user, mliName.substring(0, 4),
							mliName.substring(4, 8), mliName.substring(8, 12));
				}
				request.setAttribute("message",
						"SFDAN for Expired Cases generated successfully");
				forwardPage = "success";
			} catch (Exception exp) {
				forwardPage = "success1";
				request.setAttribute("message",
						" No Applications Available For SFDAN for Expired Cases Generation");
			}
		}
		Log.log(4, "RPAction", methodName, "Exited");
		return mapping.findForward(forwardPage);
	}

	public ActionForward generateSHDAN(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String methodName = "generateSHDAN";
		Log.log(4, "RPAction", methodName, "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		String mliName = (String) dynaForm.get("selectMember");
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("Selected Member Id : ")
						.append(mliName).toString());
		String forwardPage = "";
		User user = getUserInformation(request);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("Logged in user: ")
						.append(user.getUserId()).toString());
		RpProcessor rpProcessor = new RpProcessor();
		if (mliName.equals("")) {
			Log.log(5, "RPAction", methodName,
					"Fetching Member Details for whom CGDAN has to be generated");
			ArrayList memberIds = rpProcessor.getMemberIdsForSHDAN();
			if (memberIds != null && memberIds.size() != 0) {
				ArrayList memberDetails = new ArrayList(memberIds.size());
				for (int i = 0; i < memberIds.size(); i++) {
					String memberId = (String) memberIds.get(i);
					MLIInfo mliInfo = registration.getMemberDetails(
							memberId.substring(0, 4), memberId.substring(4, 8),
							memberId.substring(8, 12));
					String mli = "";
					mli = (new StringBuilder()).append("(")
							.append(memberId.substring(0, 4))
							.append(memberId.substring(4, 8))
							.append(memberId.substring(8, 12)).append(")")
							.append(mliInfo.getShortName()).toString();
					if (mliInfo.getBranchName() != null
							&& !mliInfo.getBranchName().equals("")
							&& (mliInfo.getZoneName() == null || mliInfo
									.getZoneName().equals("")))
						mli = (new StringBuilder()).append(mli).append(",")
								.append(mliInfo.getBranchName()).toString();
					else if ((mliInfo.getBranchName() == null || mliInfo
							.getBranchName().equals(""))
							&& mliInfo.getZoneName() != null
							&& !mliInfo.getZoneName().equals(""))
						mli = (new StringBuilder()).append(mli).append(",")
								.append(mliInfo.getZoneName()).toString();
					else if (mliInfo.getBranchName() != null
							&& !mliInfo.getBranchName().equals("")
							&& mliInfo.getZoneName() != null
							&& !mliInfo.getZoneName().equals(""))
						mli = (new StringBuilder()).append(mli).append(",")
								.append(mliInfo.getZoneName()).append(",")
								.append(mliInfo.getBranchName()).toString();
					memberDetails.add(mli);
				}

				dynaForm.set("mliNames", memberDetails);
				request.setAttribute("TARGET_URL",
						MenuOptions.getMenuAction("RP_GENERATE_DAN_FOR_SHORT"));
				HttpSession session = request.getSession(false);
				session.setAttribute("DAN_TYPE", "SHDAN");
				forwardPage = "memberInfo";
			} else {
				request.setAttribute("message",
						"No Members available for SHDAN Generation");
				forwardPage = "success";
			}
		} else {
			Log.log(5, "RPAction", methodName,
					"Entering routine to generate CGDAN for all members");
			rpProcessor.generateSHDAN(user, mliName);
			String message = "Short DAN generated successfully";
			request.setAttribute("message", message);
			forwardPage = "success";
		}
		Log.log(5, "RPAction", methodName, "Exited");
		return mapping.findForward(forwardPage);
	}

	public ActionForward generateCLDAN(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String methodName = "generateCLDAN";
		Log.log(4, "RPAction", methodName, "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		String mliName = (String) dynaForm.get("selectMember");
		Log.log(4,
				"RPAction",
				methodName,
				(new StringBuilder()).append("Selected Member Id : ")
						.append(mliName).toString());
		String forwardPage = "";
		User user = getUserInformation(request);
		Log.log(4,
				"RPAction",
				"generateCLDAN",
				(new StringBuilder()).append("Logged in user: ")
						.append(user.getUserId()).toString());
		if (mliName.equals("")) {
			Log.log(5, "RPAction", methodName,
					"Fetching Member Details for whom CLDAN has to be generated");
			ArrayList memberNames = registration.getAllMembers();
			ArrayList memberDetails = new ArrayList(memberNames.size());
			for (int i = 0; i < memberNames.size(); i++) {
				MLIInfo mliInfo = (MLIInfo) memberNames.get(i);
				String mli = "";
				mli = (new StringBuilder()).append("(")
						.append(mliInfo.getBankId())
						.append(mliInfo.getZoneId())
						.append(mliInfo.getBranchId()).append(")")
						.append(mliInfo.getShortName()).toString();
				if (mliInfo.getBranchName() != null
						&& !mliInfo.getBranchName().equals("")
						&& (mliInfo.getZoneName() == null || mliInfo
								.getZoneName().equals("")))
					mli = (new StringBuilder()).append(mli).append(",")
							.append(mliInfo.getBranchName()).toString();
				else if ((mliInfo.getBranchName() == null || mliInfo
						.getBranchName().equals(""))
						&& mliInfo.getZoneName() != null
						&& !mliInfo.getZoneName().equals(""))
					mli = (new StringBuilder()).append(mli).append(",")
							.append(mliInfo.getZoneName()).toString();
				else if (mliInfo.getBranchName() != null
						&& !mliInfo.getBranchName().equals("")
						&& mliInfo.getZoneName() != null
						&& !mliInfo.getZoneName().equals(""))
					mli = (new StringBuilder()).append(mli).append(",")
							.append(mliInfo.getZoneName()).append(",")
							.append(mliInfo.getBranchName()).toString();
				memberDetails.add(mli);
			}

			Log.log(5, "RPAction", methodName,
					"Fetched Member Details for whom CLDAN has to be generated");
			dynaForm.set("mliNames", memberDetails);
			request.setAttribute("TARGET_URL",
					MenuOptions.getMenuAction("RP_GENERATE_CLDAN"));
			HttpSession session = request.getSession(false);
			session.setAttribute("DAN_TYPE", "CLDAN");
			forwardPage = "memberInfo";
		} else {
			forwardPage = "";
			String message = "";
			try {
				Log.log(5, "RPAction", methodName,
						"Entering routine to generate CGDAN for all members");
				RpProcessor rpProcessor = new RpProcessor();
				if (mliName.equalsIgnoreCase("All")) {
					String bankId = null;
					String zoneId = null;
					String branchId = null;
					rpProcessor.generateCLDAN(user, bankId, zoneId, branchId);
				} else {
					mliName = mliName.substring(1, 13);
					rpProcessor.generateCLDAN(user, mliName.substring(0, 4),
							mliName.substring(4, 8), mliName.substring(8, 12));
				}
				request.setAttribute("message", "CLDAN generated successfully");
				forwardPage = "success";
			} catch (Exception exp) {
				forwardPage = "success1";
				request.setAttribute("message",
						" No Applications Available For CLDAN Generation");
			}
		}
		Log.log(4, "RPAction", methodName, "Exited");
		return mapping.findForward(forwardPage);
	}

	public ActionForward displayPPMLIWiseFilter(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		RPActionForm rpActionForm = (RPActionForm) form;
		RpProcessor processor = new RpProcessor();
		String fwdPage = "";
		if (bankId.equalsIgnoreCase("0000")) {
			rpActionForm.setMemberId("");
			fwdPage = "displayFilter";
		} else {
			rpActionForm.setMemberId((new StringBuilder())
					.append(user.getBankId()).append(user.getZoneId())
					.append(user.getBranchId()).toString());
			HashMap details = processor
					.getMLIWiseDANDetails((new StringBuilder())
							.append(user.getBankId()).append(user.getZoneId())
							.append(user.getBranchId()).toString());
			Vector mliWiseDanDetails = (Vector) details.get("pending_dtls");
			rpActionForm.setMliWiseDanDetails(mliWiseDanDetails);
			fwdPage = "getDetails";
		}
		return mapping.findForward(fwdPage);
	}

	public ActionForward displayPPDateWiseFilter(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RPActionForm rpActionForm = (RPActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(2);
		int day = calendar.get(5);
		month--;
		day++;
		calendar.set(2, month);
		calendar.set(5, day);
		Date prevDate = calendar.getTime();
		rpActionForm.setFromDate(prevDate);
		rpActionForm.setToDate(date);
		return mapping.findForward("displayFilter");
	}

	public ActionForward getPPMLIWiseDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "getPPMLIWiseDetails", "Entered");
		RpProcessor processor = new RpProcessor();
		RPActionForm rpActionForm = (RPActionForm) form;
		String memberId = rpActionForm.getMemberId().trim();
		ClaimsProcessor cpProcessor = new ClaimsProcessor();
		Vector memberIds = cpProcessor.getAllMemberIds();
		if (!memberIds.contains(memberId))
			throw new NoMemberFoundException("The Member ID does not exist");
		String bankId = memberId.substring(0, 4);
		String zoneId = memberId.substring(4, 8);
		String branchId = memberId.substring(8, 12);
		MLIInfo mliInfo = registration.getMemberDetails(bankId, zoneId,
				branchId);
		if (mliInfo != null) {
			Log.log(5, "ApplicationProcessingAction", "getApps",
					(new StringBuilder()).append("mli Info.. :")
							.append(mliInfo).toString());
			String statusFlag = mliInfo.getStatus();
			if (statusFlag.equals("I"))
				throw new NoDataException((new StringBuilder())
						.append("Member Id:").append(memberId)
						.append("  has been deactivated.").toString());
		}
		HashMap details = processor.getMLIWiseDANDetails(memberId);
		Vector mliWiseDanDetails = (Vector) details.get("pending_dtls");
		rpActionForm.setMliWiseDanDetails(mliWiseDanDetails);
		Log.log(4, "RPAction", "getPPMLIWiseDetails", "Exited");
		return mapping.findForward("getDetails");
	}

	public ActionForward getPPDateWiseDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "getPPDateWiseDetails", "Entered");
		RpProcessor processor = new RpProcessor();
		RPActionForm rpActionForm = (RPActionForm) form;
		Vector dateWiseDANDetails = null;
		Date fromDate = rpActionForm.getFromDate();
		Date toDate = rpActionForm.getToDate();
		if (toDate.toString().equals(""))
			toDate = new Date(System.currentTimeMillis());
		java.sql.Date sqlFromDate = null;
		if (fromDate.toString().equals("")) {
			dateWiseDANDetails = processor.getDateWiseDANDetails(null,
					new java.sql.Date(toDate.getTime()));
		} else {
			sqlFromDate = new java.sql.Date(fromDate.getTime());
			dateWiseDANDetails = processor.getDateWiseDANDetails(sqlFromDate,
					new java.sql.Date(toDate.getTime()));
		}
		rpActionForm.setDateWiseDANDetails(dateWiseDANDetails);
		Log.log(4, "RPAction", "getPPDateWiseDetails", "Exited");
		return mapping.findForward("getDetails");
	}

	public ActionForward afterMemberInfo(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RPActionForm rpAllocationForm = (RPActionForm) form;
		RpProcessor rpProcessor = new RpProcessor();
		ArrayList demandAdviceList = new ArrayList();
		String forward = "";
		String mliId = rpAllocationForm.getSelectMember();
		String bankId = mliId.substring(0, 4);
		String zoneId = mliId.substring(4, 8);
		String branchId = mliId.substring(8, 12);
		ClaimsProcessor cpProcessor = new ClaimsProcessor();
		Vector memberIds = cpProcessor.getAllMemberIds();
		if (!memberIds.contains(mliId))
			throw new NoMemberFoundException("The Member ID does not exist");
		demandAdviceList = rpProcessor.showShortDansForWaive(bankId, zoneId,
				branchId);
		if (demandAdviceList == null || demandAdviceList.size() == 0) {
			request.setAttribute("message",
					"No Applications For Waive Short DAN Amounts");
			forward = "success";
		} else {
			rpAllocationForm.setPaymentDetails(demandAdviceList);
			forward = "waiveShortAmntsDisplay";
		}
		return mapping.findForward(forward);
	}

	public ActionForward waiveShortDans(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Log.log(5, "RPAction", "waiveShortDANs", "entered");
		RPActionForm rpAllocationForm = (RPActionForm) form;
		RpProcessor rpProcessor = new RpProcessor();
		Map danNoMap = rpAllocationForm.getDanIds();
		Map waivedFlagMap = rpAllocationForm.getWaivedFlags();
		Set waivedFlagSet = waivedFlagMap.keySet();
		String key;
		for (Iterator waivedFlagIterator = waivedFlagSet.iterator(); waivedFlagIterator
				.hasNext(); rpProcessor.waiveShortDANs(key.replace('_', '.'))) {
			key = (String) waivedFlagIterator.next();
			Log.log(5, "RPAction", "waiveShortDANs", (new StringBuilder())
					.append("key :").append(key).toString());
			String shdan = (String) danNoMap.get(key);
			Log.log(5, "RPAction", "waiveShortDANs", (new StringBuilder())
					.append("key :").append(shdan).toString());
		}

		request.setAttribute("message", "Short DANs Waived Sucessfully.");
		return mapping.findForward("success");
	}

	public ActionForward afterShowMliForRefAdv(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RPActionForm rpAllocationForm = (RPActionForm) form;
		RpProcessor rpProcessor = new RpProcessor();
		String mliId = rpAllocationForm.getSelectMember();
		ClaimsProcessor cpProcessor = new ClaimsProcessor();
		Vector memberIds = cpProcessor.getAllMemberIds();
		if (!memberIds.contains(mliId)) {
			throw new NoMemberFoundException("The Member ID does not exist");
		} else {
			double refAmount = rpProcessor.getRefundAmountForMember(mliId);
			rpAllocationForm.setRefundAmount(refAmount);
			return mapping.findForward("success");
		}
	}

	public ActionForward generateRefAdv(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		RPActionForm rpAllocationForm = (RPActionForm) form;
		RpProcessor rpProcessor = new RpProcessor();
		String mliId = rpAllocationForm.getSelectMember();
		User user = getUserInformation(request);
		String userId = user.getUserId();
		String refAdvNumber = rpProcessor.generateRefundAdvice(mliId, userId);
		String message = (new StringBuilder())
				.append("Refund Advice Generated. Refund Advice Number: ")
				.append(refAdvNumber).toString();
		request.setAttribute("message", message);
		return mapping.findForward("success");
	}

	public ActionForward getPaymentList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		RPActionForm rpAllocationForm = (RPActionForm) form;
		RpProcessor rpProcessor = new RpProcessor();
		Date fromDate = rpAllocationForm.getFromDate();
		Date toDate = rpAllocationForm.getToDate();
		Log.log(4, "RPAction", "getPaymentList",
				(new StringBuilder()).append(" from date ").append(fromDate)
						.toString());
		Log.log(4, "RPAction", "getPaymentList",
				(new StringBuilder()).append(" to date ").append(toDate)
						.toString());
		String dateType = rpAllocationForm.getDateType();
		User user = getUserInformation(request);
		String memberId = "";
		if (user.getBankId().equals("0000")) {
			memberId = rpAllocationForm.getSelectMember();
			ClaimsProcessor cpProcessor = new ClaimsProcessor();
			Vector memberIds = cpProcessor.getAllMemberIds();
			if (!memberIds.contains(memberId))
				throw new NoMemberFoundException("Member Id does not exist");
		} else {
			memberId = rpAllocationForm.getSelectMember();
			Log.log(4, "RPAction", "getPaymentList", (new StringBuilder())
					.append(" member id ").append(memberId).toString());
		}
		ArrayList paymentIds = rpProcessor.getPaymentList(fromDate, toDate,
				dateType, memberId);
		rpAllocationForm.setPaymentList(paymentIds);
		return mapping.findForward("success");
	}

	public ActionForward showCGDANGenFilter(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		DynaActionForm rpAllocationForm = (DynaActionForm) form;
		rpAllocationForm.initialize(mapping);
		ArrayList memberNames = registration.getAllMembers();
		ArrayList memberDetails = new ArrayList(memberNames.size());
		for (int i = 0; i < memberNames.size(); i++) {
			MLIInfo mliInfo = (MLIInfo) memberNames.get(i);
			String mli = "";
			mli = (new StringBuilder()).append("(").append(mliInfo.getBankId())
					.append(mliInfo.getZoneId()).append(mliInfo.getBranchId())
					.append(")").append(mliInfo.getShortName()).toString();
			if (mliInfo.getBranchName() != null
					&& !mliInfo.getBranchName().equals("")
					&& (mliInfo.getZoneName() == null || mliInfo.getZoneName()
							.equals("")))
				mli = (new StringBuilder()).append(mli).append(",")
						.append(mliInfo.getBranchName()).toString();
			else if ((mliInfo.getBranchName() == null || mliInfo
					.getBranchName().equals(""))
					&& mliInfo.getZoneName() != null
					&& !mliInfo.getZoneName().equals(""))
				mli = (new StringBuilder()).append(mli).append(",")
						.append(mliInfo.getZoneName()).toString();
			else if (mliInfo.getBranchName() != null
					&& !mliInfo.getBranchName().equals("")
					&& mliInfo.getZoneName() != null
					&& !mliInfo.getZoneName().equals(""))
				mli = (new StringBuilder()).append(mli).append(",")
						.append(mliInfo.getZoneName()).append(",")
						.append(mliInfo.getBranchName()).toString();
			memberDetails.add(mli);
		}

		Log.log(4, "RPAction", "generateCGDAN",
				"Fetched Member Details for whom CGDAN has to be generated");
		rpAllocationForm.set("mliNames", memberDetails);
		HttpSession session = request.getSession(false);
		session.setAttribute("TARGET_URL",
				"generateCGDAN.do?method=generateCGDAN");
		session.setAttribute("DAN_TYPE", "CGDAN");
		return mapping.findForward("memberInfo");
	}

	public ActionForward showSFDANGenFilter(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		DynaActionForm rpAllocationForm = (DynaActionForm) form;
		rpAllocationForm.initialize(mapping);
		ArrayList memberNames = registration.getAllMembers();
		ArrayList memberDetails = new ArrayList(memberNames.size());
		for (int i = 0; i < memberNames.size(); i++) {
			MLIInfo mliInfo = (MLIInfo) memberNames.get(i);
			String mli = "";
			mli = (new StringBuilder()).append("(").append(mliInfo.getBankId())
					.append(mliInfo.getZoneId()).append(mliInfo.getBranchId())
					.append(")").append(mliInfo.getShortName()).toString();
			if (mliInfo.getBranchName() != null
					&& !mliInfo.getBranchName().equals("")
					&& (mliInfo.getZoneName() == null || mliInfo.getZoneName()
							.equals("")))
				mli = (new StringBuilder()).append(mli).append(",")
						.append(mliInfo.getBranchName()).toString();
			else if ((mliInfo.getBranchName() == null || mliInfo
					.getBranchName().equals(""))
					&& mliInfo.getZoneName() != null
					&& !mliInfo.getZoneName().equals(""))
				mli = (new StringBuilder()).append(mli).append(",")
						.append(mliInfo.getZoneName()).toString();
			else if (mliInfo.getBranchName() != null
					&& !mliInfo.getBranchName().equals("")
					&& mliInfo.getZoneName() != null
					&& !mliInfo.getZoneName().equals(""))
				mli = (new StringBuilder()).append(mli).append(",")
						.append(mliInfo.getZoneName()).append(",")
						.append(mliInfo.getBranchName()).toString();
			memberDetails.add(mli);
		}

		Log.log(4, "RPAction", "generateCGDAN",
				"Fetched Member Details for whom CGDAN has to be generated");
		rpAllocationForm.set("mliNames", memberDetails);
		HttpSession session = request.getSession(false);
		session.setAttribute("TARGET_URL",
				"generateSFDAN.do?method=generateSFDAN");
		session.setAttribute("DAN_TYPE", "SFDAN");
		return mapping.findForward("memberInfo");
	}

	public ActionForward showBatchSFDANGenFilter(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		DynaActionForm rpAllocationForm = (DynaActionForm) form;
		rpAllocationForm.initialize(mapping);
		ArrayList memberNames = registration.getAllHOMembers();
		ArrayList memberDetails = new ArrayList(memberNames.size());
		for (int i = 0; i < memberNames.size(); i++) {
			MLIInfo mliInfo = (MLIInfo) memberNames.get(i);
			String mli = "";
			mli = (new StringBuilder()).append("(").append(mliInfo.getBankId())
					.append(mliInfo.getZoneId()).append(mliInfo.getBranchId())
					.append(")").append(mliInfo.getBankName()).toString();
			if (mliInfo.getBranchName() != null
					&& !mliInfo.getBranchName().equals("")
					&& (mliInfo.getZoneName() == null || mliInfo.getZoneName()
							.equals("")))
				mli = (new StringBuilder()).append(mli).append(",")
						.append(mliInfo.getBranchName()).toString();
			else if ((mliInfo.getBranchName() == null || mliInfo
					.getBranchName().equals(""))
					&& mliInfo.getZoneName() != null
					&& !mliInfo.getZoneName().equals(""))
				mli = (new StringBuilder()).append(mli).append(",")
						.append(mliInfo.getZoneName()).toString();
			else if (mliInfo.getBranchName() != null
					&& !mliInfo.getBranchName().equals("")
					&& mliInfo.getZoneName() != null
					&& !mliInfo.getZoneName().equals(""))
				mli = (new StringBuilder()).append(mli).append(",")
						.append(mliInfo.getZoneName()).append(",")
						.append(mliInfo.getBranchName()).toString();
			memberDetails.add(mli);
		}

		Log.log(4, "RPAction", "showBatchSFDANGenFilter",
				"Fetched Member Details for whom CGDAN has to be generated");
		rpAllocationForm.set("mliNames", memberDetails);
		HttpSession session = request.getSession(false);
		session.setAttribute("TARGET_URL",
				"generateBatchSFDAN.do?method=generateBatchSFDAN");
		session.setAttribute("DAN_TYPE", "BATCHSFDAN");
		return mapping.findForward("memberInfo");
	}

	public ActionForward generateBatchSFDAN(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String methodName = "generateBatchSFDAN";
		Log.log(4, "RPAction", methodName, "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		String mliName = (String) dynaForm.get("selectMember");
		Log.log(4,
				"RPAction",
				methodName,
				(new StringBuilder()).append("Selected Member Id : ")
						.append(mliName).toString());
		String forwardPage = "";
		User user = getUserInformation(request);
		Log.log(4, "RPAction", "generateBatchSFDAN", (new StringBuilder())
				.append("Logged in user: ").append(user.getUserId()).toString());
		if (mliName.equals("")) {
			Log.log(5, "RPAction", methodName,
					"Fetching Member Details for whom SFDAN has to be generated");
			ArrayList memberNames = registration.getAllMembers();
			ArrayList memberDetails = new ArrayList(memberNames.size());
			for (int i = 0; i < memberNames.size(); i++) {
				MLIInfo mliInfo = (MLIInfo) memberNames.get(i);
				String mli = "";
				mli = (new StringBuilder()).append("(")
						.append(mliInfo.getBankId())
						.append(mliInfo.getZoneId())
						.append(mliInfo.getBranchId()).append(")")
						.append(mliInfo.getShortName()).toString();
				if (mliInfo.getBranchName() != null
						&& !mliInfo.getBranchName().equals("")
						&& (mliInfo.getZoneName() == null || mliInfo
								.getZoneName().equals("")))
					mli = (new StringBuilder()).append(mli).append(",")
							.append(mliInfo.getBranchName()).toString();
				else if ((mliInfo.getBranchName() == null || mliInfo
						.getBranchName().equals(""))
						&& mliInfo.getZoneName() != null
						&& !mliInfo.getZoneName().equals(""))
					mli = (new StringBuilder()).append(mli).append(",")
							.append(mliInfo.getZoneName()).toString();
				else if (mliInfo.getBranchName() != null
						&& !mliInfo.getBranchName().equals("")
						&& mliInfo.getZoneName() != null
						&& !mliInfo.getZoneName().equals(""))
					mli = (new StringBuilder()).append(mli).append(",")
							.append(mliInfo.getZoneName()).append(",")
							.append(mliInfo.getBranchName()).toString();
				memberDetails.add(mli);
			}

			Log.log(5, "RPAction", methodName,
					"Fetched Member Details for whom SFDAN has to be generated");
			dynaForm.set("mliNames", memberDetails);
			request.setAttribute("TARGET_URL",
					MenuOptions.getMenuAction("RP_GENERATE_BATCH_SFDAN"));
			HttpSession session = request.getSession(false);
			session.setAttribute("DAN_TYPE", "BATCHSFDAN");
			forwardPage = "memberInfo";
		} else {
			forwardPage = "";
			String message = "";
			try {
				Log.log(5, "RPAction", methodName,
						"Entering routine to generate Batch SFDAN for all members");
				RpProcessor rpProcessor = new RpProcessor();
				if (mliName.equalsIgnoreCase("All")) {
					request.setAttribute("message",
							" No Applications Available For SFDAN Generation");
				} else {
					mliName = mliName.substring(1, 13);
					System.out.println((new StringBuilder())
							.append("MLI Name:").append(mliName).toString());
					rpProcessor.generateBatchSFDAN(user,
							mliName.substring(0, 4));
					message = "Batch SFDAN generated successfully";
				}
				request.setAttribute("message",
						"Batch SFDAN generated successfully");
				forwardPage = "success";
			} catch (Exception exp) {
				forwardPage = "success1";
				request.setAttribute("message",
						" No Applications Available For SFDAN Generation");
			}
		}
		Log.log(4, "RPAction", methodName, "Exited");
		return mapping.findForward(forwardPage);
	}

	public ActionForward showSFDANGenFilterForExpired(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		DynaActionForm rpAllocationForm = (DynaActionForm) form;
		rpAllocationForm.initialize(mapping);
		ArrayList memberNames = registration.getAllMembers();
		ArrayList memberDetails = new ArrayList(memberNames.size());
		for (int i = 0; i < memberNames.size(); i++) {
			MLIInfo mliInfo = (MLIInfo) memberNames.get(i);
			String mli = "";
			mli = (new StringBuilder()).append("(").append(mliInfo.getBankId())
					.append(mliInfo.getZoneId()).append(mliInfo.getBranchId())
					.append(")").append(mliInfo.getShortName()).toString();
			if (mliInfo.getBranchName() != null
					&& !mliInfo.getBranchName().equals("")
					&& (mliInfo.getZoneName() == null || mliInfo.getZoneName()
							.equals("")))
				mli = (new StringBuilder()).append(mli).append(",")
						.append(mliInfo.getBranchName()).toString();
			else if ((mliInfo.getBranchName() == null || mliInfo
					.getBranchName().equals(""))
					&& mliInfo.getZoneName() != null
					&& !mliInfo.getZoneName().equals(""))
				mli = (new StringBuilder()).append(mli).append(",")
						.append(mliInfo.getZoneName()).toString();
			else if (mliInfo.getBranchName() != null
					&& !mliInfo.getBranchName().equals("")
					&& mliInfo.getZoneName() != null
					&& !mliInfo.getZoneName().equals(""))
				mli = (new StringBuilder()).append(mli).append(",")
						.append(mliInfo.getZoneName()).append(",")
						.append(mliInfo.getBranchName()).toString();
			memberDetails.add(mli);
		}

		Log.log(4,
				"RPAction",
				"showSFDANGenFilterForExpired",
				"Fetched Member Details for whom showSFDANGenFilterForExpired has to be generated");
		rpAllocationForm.set("mliNames", memberDetails);
		HttpSession session = request.getSession(false);
		session.setAttribute("TARGET_URL",
				"generateSFDANEXP.do?method=generateSFDANEXP");
		session.setAttribute("DAN_TYPE", "SFDANEXP");
		return mapping.findForward("memberInfo");
	}

	public ActionForward showSHDANGenFilter(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		DynaActionForm rpAllocationForm = (DynaActionForm) form;
		rpAllocationForm.initialize(mapping);
		RpProcessor rpProcessor = new RpProcessor();
		String forwardPage = "";
		ArrayList memberIds = rpProcessor.getMemberIdsForSHDAN();
		if (memberIds != null && memberIds.size() != 0) {
			ArrayList memberDetails = new ArrayList(memberIds.size());
			for (int i = 0; i < memberIds.size(); i++) {
				String memberId = (String) memberIds.get(i);
				MLIInfo mliInfo = registration.getMemberDetails(
						memberId.substring(0, 4), memberId.substring(4, 8),
						memberId.substring(8, 12));
				String mli = "";
				mli = (new StringBuilder()).append("(")
						.append(memberId.substring(0, 4))
						.append(memberId.substring(4, 8))
						.append(memberId.substring(8, 12)).append(")")
						.append(mliInfo.getShortName()).toString();
				if (mliInfo.getBranchName() != null
						&& !mliInfo.getBranchName().equals("")
						&& (mliInfo.getZoneName() == null || mliInfo
								.getZoneName().equals("")))
					mli = (new StringBuilder()).append(mli).append(",")
							.append(mliInfo.getBranchName()).toString();
				else if ((mliInfo.getBranchName() == null || mliInfo
						.getBranchName().equals(""))
						&& mliInfo.getZoneName() != null
						&& !mliInfo.getZoneName().equals(""))
					mli = (new StringBuilder()).append(mli).append(",")
							.append(mliInfo.getZoneName()).toString();
				else if (mliInfo.getBranchName() != null
						&& !mliInfo.getBranchName().equals("")
						&& mliInfo.getZoneName() != null
						&& !mliInfo.getZoneName().equals(""))
					mli = (new StringBuilder()).append(mli).append(",")
							.append(mliInfo.getZoneName()).append(",")
							.append(mliInfo.getBranchName()).toString();
				memberDetails.add(mli);
			}

			rpAllocationForm.set("mliNames", memberDetails);
			forwardPage = "memberInfo";
			HttpSession session = request.getSession(false);
			session.setAttribute("TARGET_URL",
					"generateSHDAN.do?method=generateSHDAN");
			session.setAttribute("DAN_TYPE", "SHDAN");
		} else {
			request.setAttribute("message",
					"No Members available for SHDAN Generation");
			forwardPage = "success";
		}
		Log.log(4, "RPAction", "generateCGDAN",
				"Fetched Member Details for whom CGDAN has to be generated");
		return mapping.findForward(forwardPage);
	}

	public ActionForward showCLDANGenFilter(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String methodName = "showCLDANGenFilter";
		Log.log(4, "RPAction", methodName, "Entered");
		String forwardPage = "";
		User user = getUserInformation(request);
		Log.log(4,
				"RPAction",
				"generateCLDAN",
				(new StringBuilder()).append("Logged in user: ")
						.append(user.getUserId()).toString());
		DynaActionForm rpAllocationForm = (DynaActionForm) form;
		rpAllocationForm.initialize(mapping);
		Log.log(5, "RPAction", methodName,
				"Fetching Member Details for whom CLDAN has to be generated");
		ArrayList memberNames = registration.getAllMembers();
		ArrayList memberDetails = new ArrayList(memberNames.size());
		for (int i = 0; i < memberNames.size(); i++) {
			MLIInfo mliInfo = (MLIInfo) memberNames.get(i);
			String mli = "";
			mli = (new StringBuilder()).append("(").append(mliInfo.getBankId())
					.append(mliInfo.getZoneId()).append(mliInfo.getBranchId())
					.append(")").append(mliInfo.getShortName()).toString();
			if (mliInfo.getBranchName() != null
					&& !mliInfo.getBranchName().equals("")
					&& (mliInfo.getZoneName() == null || mliInfo.getZoneName()
							.equals("")))
				mli = (new StringBuilder()).append(mli).append(",")
						.append(mliInfo.getBranchName()).toString();
			else if ((mliInfo.getBranchName() == null || mliInfo
					.getBranchName().equals(""))
					&& mliInfo.getZoneName() != null
					&& !mliInfo.getZoneName().equals(""))
				mli = (new StringBuilder()).append(mli).append(",")
						.append(mliInfo.getZoneName()).toString();
			else if (mliInfo.getBranchName() != null
					&& !mliInfo.getBranchName().equals("")
					&& mliInfo.getZoneName() != null
					&& !mliInfo.getZoneName().equals(""))
				mli = (new StringBuilder()).append(mli).append(",")
						.append(mliInfo.getZoneName()).append(",")
						.append(mliInfo.getBranchName()).toString();
			memberDetails.add(mli);
		}

		Log.log(5, "RPAction", methodName,
				"Fetched Member Details for whom CLDAN has to be generated");
		rpAllocationForm.set("mliNames", memberDetails);
		HttpSession session = request.getSession(false);
		session.setAttribute("TARGET_URL",
				"generateCLDAN.do?method=generateCLDAN");
		session.setAttribute("DAN_TYPE", "CLDAN");
		forwardPage = "memberInfo";
		Log.log(4, "RPAction", methodName, "Exited");
		return mapping.findForward(forwardPage);
	}

	public ActionForward getGLName(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Log.log(4, "RPAction", "getGLName", "Entered");
		RPActionForm actionForm = (RPActionForm) form;
		String glCode = actionForm.getBankGLCode();
		Log.log(4, "RPAction", "getGLName",
				(new StringBuilder()).append("code ").append(glCode).toString());
		String glName = "";
		if (!glCode.equals("")) {
			RpProcessor rpProcessor = new RpProcessor();
			glName = rpProcessor.getGLName(glCode);
		}
		request.setAttribute("IsRequired", null);
		actionForm.setBankGLName(glName);
		HttpSession session = request.getSession(false);
		session.setAttribute("VOUCHER_FLAG", "2");
		Log.log(4, "RPAction", "getGLName", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward generateExcessVoucherFilter(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String methodName = "generateExcessVoucherFilter";
		Log.log(4, "RPAction", methodName, "Entered");
		String forwardPage = "";
		User user = getUserInformation(request);
		Log.log(5,
				"RPAction",
				methodName,
				(new StringBuilder()).append("Logged in user: ")
						.append(user.getUserId()).toString());
		RpProcessor rpProcessor = new RpProcessor();
		DynaActionForm rpAllocationForm = (DynaActionForm) form;
		rpAllocationForm.initialize(mapping);
		ArrayList memberIds = rpProcessor.getMemberIdsForExcess();
		if (memberIds != null && memberIds.size() != 0) {
			ArrayList memberDetails = new ArrayList(memberIds.size());
			for (int i = 0; i < memberIds.size(); i++) {
				String memberId = (String) memberIds.get(i);
				MLIInfo mliInfo = registration.getMemberDetails(
						memberId.substring(0, 4), memberId.substring(4, 8),
						memberId.substring(8, 12));
				String mli = "";
				mli = (new StringBuilder()).append("(")
						.append(memberId.substring(0, 4))
						.append(memberId.substring(4, 8))
						.append(memberId.substring(8, 12)).append(")")
						.append(mliInfo.getShortName()).toString();
				if (mliInfo.getBranchName() != null
						&& !mliInfo.getBranchName().equals("")
						&& (mliInfo.getZoneName() == null || mliInfo
								.getZoneName().equals("")))
					mli = (new StringBuilder()).append(mli).append(",")
							.append(mliInfo.getBranchName()).toString();
				else if ((mliInfo.getBranchName() == null || mliInfo
						.getBranchName().equals(""))
						&& mliInfo.getZoneName() != null
						&& !mliInfo.getZoneName().equals(""))
					mli = (new StringBuilder()).append(mli).append(",")
							.append(mliInfo.getZoneName()).toString();
				else if (mliInfo.getBranchName() != null
						&& !mliInfo.getBranchName().equals("")
						&& mliInfo.getZoneName() != null
						&& !mliInfo.getZoneName().equals(""))
					mli = (new StringBuilder()).append(mli).append(",")
							.append(mliInfo.getZoneName()).append(",")
							.append(mliInfo.getBranchName()).toString();
				memberDetails.add(mli);
			}

			rpAllocationForm.set("mliNames", memberDetails);
			forwardPage = "memberInfo";
		} else {
			request.setAttribute("message",
					"No Members available for Voucher Generation");
			forwardPage = "success";
		}
		HttpSession session = request.getSession(false);
		session.setAttribute("TARGET_URL",
				"generateExcessVoucher.do?method=generateExcessVoucher");
		session.setAttribute("DAN_TYPE", "EXCESS");
		return mapping.findForward(forwardPage);
	}

	public ActionForward generateExcessVoucher(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RpProcessor rpProcessor = new RpProcessor();
		DynaActionForm rpGenerateDANForm = (DynaActionForm) form;
		String mliName = (String) rpGenerateDANForm.get("selectMember");
		Log.log(4,
				"RPAction",
				"generateCGDAN",
				(new StringBuilder()).append("Selected Member Id : ")
						.append(mliName).toString());
		User user = getUserInformation(request);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Properties accCodes = new Properties();
		String contextPath = request.getSession(false).getServletContext()
				.getRealPath("");
		Log.log(5, "RPAction", "getPaymentsMade",
				(new StringBuilder()).append("path ").append(contextPath)
						.toString());
		File tempFile = new File((new StringBuilder()).append(contextPath)
				.append("\\WEB-INF\\classes").toString(),
				"AccountCodes.properties");
		Log.log(5, "RPAction", "getPaymentsMade", "file opened ");
		File accCodeFile = new File(tempFile.getAbsolutePath());
		try {
			FileInputStream fin = new FileInputStream(accCodeFile);
			accCodes.load(fin);
		} catch (FileNotFoundException fe) {
			throw new MessageException("Could not load Account Codes.");
		} catch (IOException ie) {
			throw new MessageException("Could not load Account Codes.");
		}
		if (!mliName.equals("All")) {
			VoucherDetail voucherDetail = new VoucherDetail();
			ArrayList vouchers = new ArrayList();
			mliName = mliName.substring(1, 13);
			double voucherAmount = rpProcessor.getAmountForExcess(mliName);
			voucherDetail.setBankGLName("");
			voucherDetail.setAmount(voucherAmount);
			voucherDetail.setBankGLCode(accCodes.getProperty("bank_ac"));
			voucherDetail.setDeptCode("CG");
			voucherDetail.setVoucherType("PAYMENT VOUCHER");
			Voucher voucher = new Voucher();
			voucher.setAcCode(accCodes.getProperty("excess_ac"));
			voucher.setPaidTo("CGTSI");
			voucher.setDebitOrCredit("C");
			voucher.setInstrumentDate(dateFormat.format(new Date()));
			voucher.setInstrumentNo(null);
			voucher.setInstrumentType(null);
			voucher.setAmountInRs((new StringBuilder()).append("-")
					.append(voucherAmount).toString());
			vouchers.add(voucher);
			String narration = "";
			narration = (new StringBuilder()).append(narration)
					.append(" Member Id: ").append(mliName).toString();
			narration = (new StringBuilder()).append(narration)
					.append(" Voucher Amount: ").append(voucherAmount)
					.toString();
			voucherDetail.setNarration(narration);
			voucherDetail.setVouchers(vouchers);
			String voucherId = rpProcessor.insertVoucherDetails(voucherDetail,
					user.getUserId());
			rpProcessor.updateIdForExcess(mliName, voucherId);
			vouchers.clear();
			voucherDetail = null;
		} else if (mliName.equals("All")) {
			ArrayList memberIds = rpProcessor.getMemberIdsForExcess();
			for (int i = 0; i < memberIds.size(); i++) {
				VoucherDetail voucherDetail = new VoucherDetail();
				ArrayList vouchers = new ArrayList();
				mliName = (String) memberIds.get(i);
				double voucherAmount = rpProcessor.getAmountForExcess(mliName);
				voucherDetail.setBankGLName("");
				voucherDetail.setAmount(voucherAmount);
				voucherDetail.setBankGLCode(accCodes.getProperty("bank_ac"));
				voucherDetail.setDeptCode("CG");
				voucherDetail.setVoucherType("PAYMENT VOUCHER");
				Voucher voucher = new Voucher();
				voucher.setAcCode(accCodes.getProperty("excess_ac"));
				voucher.setPaidTo("CGTSI");
				voucher.setDebitOrCredit("C");
				voucher.setInstrumentDate(dateFormat.format(new Date()));
				voucher.setInstrumentNo(null);
				voucher.setInstrumentType(null);
				voucher.setAmountInRs((new StringBuilder()).append("-")
						.append(voucherAmount).toString());
				vouchers.add(voucher);
				String narration = "";
				narration = (new StringBuilder()).append(narration)
						.append(" Member Id: ").append(mliName).toString();
				narration = (new StringBuilder()).append(narration)
						.append(" Voucher Amount: ").append(voucherAmount)
						.toString();
				voucherDetail.setNarration(narration);
				voucherDetail.setVouchers(vouchers);
				String voucherId = rpProcessor.insertVoucherDetails(
						voucherDetail, user.getUserId());
				rpProcessor.updateIdForExcess(mliName, voucherId);
				vouchers.clear();
				voucherDetail = null;
			}

		}
		request.setAttribute("message",
				"Voucher details are stored successfully");
		return mapping.findForward("success");
	}

	public ActionForward showAllCardRates(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RpProcessor rpProcessor = new RpProcessor();
		RPActionForm actionForm = (RPActionForm) form;
		ArrayList cardRateList = rpProcessor.getAllCardRates();
		actionForm.setGfCardRateList(cardRateList);
		return mapping.findForward("success");
	}

	public ActionForward saveGFCardRates(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "RPAction", "saveGFCardRates", "Entered");
		RpProcessor rpProcessor = new RpProcessor();
		RPActionForm actionForm = (RPActionForm) form;
		User user = getUserInformation(request);
		Map rateId = new TreeMap();
		Map gfLowAmount = new TreeMap();
		Map gfLowHigh = new TreeMap();
		Map gfCardRate = new TreeMap();
		rateId = actionForm.getRateId();
		gfLowAmount = actionForm.getLowAmount();
		gfLowHigh = actionForm.getHighAmount();
		gfCardRate = actionForm.getGfRate();
		Set rateIdSet = rateId.keySet();
		int id;
		double cardRate;
		for (Iterator rateIdIterator = rateIdSet.iterator(); rateIdIterator
				.hasNext(); rpProcessor.updateCardRate(id, cardRate,
				user.getUserId())) {
			String key = (String) rateIdIterator.next();
			id = Integer.parseInt((String) rateId.get(key));
			cardRate = Double.parseDouble((String) gfCardRate.get(key));
			Log.log(4, "RPAction", "saveGFCardRates", (new StringBuilder())
					.append("cardRate ;").append(cardRate).toString());
			Log.log(4, "RPAction", "saveGFCardRates", (new StringBuilder())
					.append("id :").append(id).toString());
		}

		request.setAttribute("message",
				"Guarantee Fee Card Rates Updated Successfully");
		Log.log(4, "RPAction", "saveGFCardRates", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward rpCancelPayments(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "ReportsAction", "rpCancelPayments", "Entered");
		RPActionForm actionForm = (RPActionForm) form;
		Log.log(4, "ReportsAction", "rpCancelPayments", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward cancelRpAppropriation(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String userid;
		String paymentId;
		String instrumentNo;
		String remarks;
		Connection connection;
		Log.log(4, "ReportsAction", "displayClmRefNumberDtl", "Entered");
		ReportManager manager = new ReportManager();
		RPActionForm rpForm = (RPActionForm) form;
		String clmApplicationStatus = "";
		Log.log(4, "ReportsAction", "displayTCQueryDetail",
				"Claim Application Status being queried ");
		User user = getUserInformation(request);
		userid = user.getUserId().trim();
		String bankid = user.getBankId().trim();
		String zoneid = user.getZoneId().trim();
		String branchid = user.getBranchId().trim();
		String memberId = (new StringBuilder()).append(bankid).append(zoneid)
				.append(branchid).toString();
		paymentId = null;
		instrumentNo = null;
		remarks = null;
		paymentId = request.getParameter("paymentId");
		remarks = request.getParameter("remarksforAppropriation");
		instrumentNo = request.getParameter("instrumentNo");
		if (paymentId != null && instrumentNo != null) {
			connection = DBConnection.getConnection(false);
			try {
				CallableStatement callable = connection
						.prepareCall("{?=call funccancelappropriation(?,?,?,?,?)}");
				callable.registerOutParameter(1, 4);
				callable.setString(2, paymentId);
				callable.setString(3, instrumentNo);
				callable.setString(4, userid);
				callable.setString(5, remarks);
				callable.registerOutParameter(6, 12);
				callable.execute();
				int errorCode = callable.getInt(1);
				String error = callable.getString(6);
				Log.log(5,
						"RPAction",
						"cancelRpAppropriation",
						(new StringBuilder())
								.append("Error code and error are ")
								.append(errorCode).append(" ").append(error)
								.toString());
				if (errorCode == 1) {
					connection.rollback();
					callable.close();
					callable = null;
					throw new DatabaseException(error);
				}
				callable.close();
				callable = null;
				connection.commit();
			} catch (SQLException e) {
				try {
					connection.rollback();
				} catch (SQLException ignore) {
					Log.log(2, "RPAction", "cancelRpAppropriation",
							ignore.getMessage());
				}
				Log.log(2, "RPAction", "cancelRpAppropriation", e.getMessage());
				Log.logException(e);
			} finally {
				DBConnection.freeConnection(connection);
			}
		}
		return mapping.findForward("success");
	}

	public ActionForward getPaymentMadeForClaimAsfInput(ActionMapping mapping,
			
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		RPActionForm rpActionForm = (RPActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		if (rpActionForm.getAllocatedFlags() != null)
			rpActionForm.getAllocatedFlags().clear();
		if (rpActionForm.getRealizationDates() != null)
			rpActionForm.getRealizationDates().clear();
		if (rpActionForm.getReceivedAmounts() != null)
			rpActionForm.getReceivedAmounts().clear();

		GeneralReport generalReport = new GeneralReport();
		rpActionForm.setDateOfTheDocument24(prevDate);
		rpActionForm.setDateOfTheDocument25(date);
		BeanUtils.copyProperties(rpActionForm, generalReport);
		// BeanUtils.copyProperties(dynaForm, generalReport);
		return mapping.findForward("paymentDetails");

	}

	public ActionForward getPaymentsMadeForASFNewInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		RPActionForm rpActionForm = (RPActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		if (rpActionForm.getAllocatedFlags() != null)
			rpActionForm.getAllocatedFlags().clear();
		if (rpActionForm.getRealizationDates() != null)
			rpActionForm.getRealizationDates().clear();
		if (rpActionForm.getReceivedAmounts() != null)
			rpActionForm.getReceivedAmounts().clear();

		GeneralReport generalReport = new GeneralReport();
		rpActionForm.setDateOfTheDocument24(prevDate);
		rpActionForm.setDateOfTheDocument25(date);
		BeanUtils.copyProperties(rpActionForm, generalReport);
		// BeanUtils.copyProperties(dynaForm, generalReport);
		return mapping.findForward("paymentDetails");

	}

    public ActionForward getPaymentsMadeForASFNew(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
    throws Exception
{
    	 RpProcessor rpProcessor = new RpProcessor();
         RPActionForm rpActionForm = (RPActionForm)form;
         java.util.Date toDate=rpActionForm.getDateOfTheDocument24();
         java.util.Date fromDate=rpActionForm.getDateOfTheDocument25();
         String bank = rpActionForm.getBankName();
         // ArrayList paymentDetails = rpProcessor.displayPaymentsReceivedForASF(toDate,fromDate);
         if(rpActionForm.getAllocatedFlags() != null)
             rpActionForm.getAllocatedFlags().clear();
         if(rpActionForm.getRealizationDates() != null)
             rpActionForm.getRealizationDates().clear();
         if(rpActionForm.getReceivedAmounts() != null)
             rpActionForm.getReceivedAmounts().clear();
    ArrayList paymentDetails = rpProcessor.displayPaymentsReceivedForASFNew(toDate,fromDate,bank);
   /*java.util.List ar=new ArrayList();
    paymentDetails.size();
//    System.out.println(count);
    if(paymentDetails.size()>50)
    {
    	ar=paymentDetails.subList(0,50);
    	paymentDetails.retainAll(ar);
    	
    }*/
    
    
    if(rpActionForm.getCgpans() != null)
        rpActionForm.getCgpans().clear();
        
    rpActionForm.setPaymentDetails(paymentDetails);
    return mapping.findForward("paymentsSummary");
}

	/*
	 * public ActionForward openServiceFeeDansInput(ActionMapping mapping,
	 * ActionForm form, HttpServletRequest request, HttpServletResponse
	 * response) { com.cgtsi.actionform.RPActionForm rpForm =
	 * (com.cgtsi.actionform.RPActionForm) form; rpForm.setMemberId(""); return
	 * mapping.findForward(Constants.SUCCESS); }
	 */

	public ActionForward openServiceFeeDans(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		com.cgtsi.actionform.RPActionForm rpForm = (com.cgtsi.actionform.RPActionForm) form;
		String memberId = rpForm.getMemberId();
		RpDAO dao = new RpDAO();
		int dan_count = dao.openServiceFeeDans(memberId);
		request.setAttribute("DAN_COUNT", dan_count);
		request.setAttribute("message", "No. of dans re-opened : " + dan_count);
		rpForm.setMemberId("");
		return mapping.findForward(Constants.SUCCESS);
	}
	
	//Niteen singh
	
	/*public ActionForward showRecoveryPaymentDetailsforAppropriation(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		System.out.println(" showRecoveryPaymentDetailsforAppropriation ");
		Log.log(5, "GMAction", "showApprRegistrationForm", "Entered");
		HttpSession session = request.getSession(false);
		RPActionForm Recovryform = (RPActionForm) form;
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId + zoneId + branchId;
		
		//  java.util.Date toDate=Recovryform.getDateOfTheDocument24();
		  
	     //    java.util.Date fromDate=Recovryform.getDateOfTheDocument25();
		
	     //  System.out.println("toDate "+toDate+ " fromDate : "+fromDate);  
	         
		// System.out.println("GMA memberId : "+memberId);
	//	ArrayList formRecvryList = displayRecoveryList(memberId,request); //diksha 
		 ArrayList formRecvryList = displayRecoveryList(memberId); //Diksha 
	     //  ArrayList  formRecvryList = displayRecoveryList(fromDate,toDate);

		String forward = "";
		if (formRecvryList == null || formRecvryList.size() == 0) {
			request.setAttribute("message",
					"No Applications Available For Approval");
			forward = "success";
		} else {
			Recovryform.setRecvryPaymentList(formRecvryList);
			forward = "npaRegistList";
		}

		Log.log(5, "GMAction", "showApprRegistrationForm", "Exited");
		return mapping.findForward(forward);
	}*/

	// Diksha .....//public ArrayList displayRecoveryList(String memberId,HttpServletRequest request)
	/*public ArrayList displayRecoveryList(String memberId)
	throws DatabaseException {
             Log.log(5, "GMAction", "displayNpaRegistrationFormList", "Entered");
   
             ArrayList RecoveryPayList = new ArrayList();
             com.cgtsi.actionform.GMActionForm npaRegistForm = null;
            // ArrayList RAFC_ID=new ArrayList();
           
            Connection connection = DBConnection.getConnection(false);
            RPActionForm rpActionForm = null;
            ResultSet rs = null;
            Statement stmt = null;
            
        String npaRegistQuery = "SELECT distinct  CL.MEM_BNK_ID||CL.MEM_ZNE_ID||CL.MEM_BRN_ID,MEM_ZONE_NAME,RE.CGPAN,DECODE(APP_REAPPROVE_AMOUNT,NULL,APP_APPROVED_AMOUNT,APP_REAPPROVE_AMOUNT),SSI_UNIT_NAME,CTD_TC_FIRST_INST_PAY_AMT," +
	   " AMOUNT_REMITTED_TO_CGTMSE,(SELECT  RMD_DESCRIPTION  FROM  RECOVERY_MODE_MASTER RM WHERE RM.RMD_ID=RE.TYPE_OF_RECOVERY) REC_TYPE,PAY_INSTRUMENT_NUMBER" +
	   ",nvl(PAY_INSTRUMENT_DT,PAY_PAYMENT_DT) as PAY_INSTRUMENT_DT,RAFC_ID FROM MEMBER_INFO  M,CLAIM_TC_DETAIL TC,CLAIM_DETAIL CL,RECOVRY_AFTER_BEFORE_FST_CLAIM RE,PAYMENT_dETAIL_TEMP  PM,SSI_dETAIL S,APPLICATION_DETAIL AP  " +
       " WHERE M.MEM_BNK_ID||M.MEM_ZNE_ID||M.MEM_BRN_ID=CL.MEM_BNK_ID||CL.MEM_ZNE_ID||CL.MEM_BRN_ID  AND CLM_STATUS='AP' and   RECOVERY_UPDATION_FLAG='RP'  and re.cgpan like '%TC'  and RECOVERY_FLAG='AC' " +
       " AND CL.CLM_REF_NO=TC.CLM_REF_NO AND RE.CLM_REF_NO=CL.CLM_REF_NO AND PM.PAY_ID=RE.PAY_ID  AND CL.BID=S.BID  AND TC.CGPAN=AP.CGPAN UNION ALL " +
       " SELECT distinct CL.MEM_BNK_ID||CL.MEM_ZNE_ID||CL.MEM_BRN_ID,MEM_ZONE_NAME,RE.CGPAN,DECODE(APP_REAPPROVE_AMOUNT,NULL,APP_APPROVED_AMOUNT,APP_REAPPROVE_AMOUNT),SSI_UNIT_NAME,CWD_WC_FIRST_INST_PAY_AMT,AMOUNT_REMITTED_TO_CGTMSE," +
       "(SELECT  RMD_DESCRIPTION  FROM  RECOVERY_MODE_MASTER RM WHERE RM.RMD_ID=RE.TYPE_OF_RECOVERY) REC_TYPE,PAY_INSTRUMENT_NUMBER,nvl(PAY_INSTRUMENT_DT,PAY_PAYMENT_DT) as PAY_INSTRUMENT_DT,RAFC_ID " +
       "  FROM MEMBER_INFO  M,CLAIM_WC_DETAIL TC,CLAIM_DETAIL CL,RECOVRY_AFTER_BEFORE_FST_CLAIM RE,PAYMENT_dETAIL_TEMP  PM,SSI_dETAIL S,APPLICATION_DETAIL AP  " +
       " WHERE M.MEM_BNK_ID||M.MEM_ZNE_ID||M.MEM_BRN_ID=CL.MEM_BNK_ID||CL.MEM_ZNE_ID||CL.MEM_BRN_ID  AND CLM_STATUS='AP'  and RECOVERY_UPDATION_FLAG='RP'  and re.cgpan like '%WC'  and RECOVERY_FLAG='AC' " +
       " AND CL.CLM_REF_NO=TC.CLM_REF_NO AND RE.CLM_REF_NO=CL.CLM_REF_NO AND PM.PAY_ID=RE.PAY_ID  AND CL.BID=S.BID  AND TC.CGPAN=AP.CGPAN  order by 1 ";

          //System.out.println("GMA npaRegistQuery : " + npaRegistQuery);

         try {
	        stmt = connection.createStatement();
	        rs = stmt.executeQuery((npaRegistQuery));
	
	  while (rs.next()) 
	  {
		 rpActionForm = new RPActionForm();
		 rpActionForm.setMemberId(rs.getString(1));
		 rpActionForm.setZoneName(rs.getString(2));
		 rpActionForm.setCgpan(rs.getString(3));
		 rpActionForm.setGuaranteeAppAmt(rs.getDouble(4));
		 rpActionForm.setUnitName(rs.getString(5));		
		 rpActionForm.setFirstClaimSetAmt(rs.getDouble(6));
		 rpActionForm.setAmountRemitedtoCgtmse(rs.getDouble(7));
		 rpActionForm.setRecoveryType(rs.getString(8));
		 rpActionForm.setInstrumentNo(rs.getString(9));
		 rpActionForm.setInstrumentDate(rs.getDate(10));
		 rpActionForm.setCheckerId(rs.getInt(11));
		 //===============get total record count====//
//Diksha		// RAFC_ID.add(rs.getString("RAFC_ID"));
		 //System.out.println("Row count=="+rs.getRow());
		 //================till here================//
         RecoveryPayList.add(rpActionForm);
	}
//Diksha 	   //request.setAttribute("RAFC_ID",RAFC_ID);
	    connection.commit();
	    stmt.close();
	    stmt = null;
	    rs.close();
	    rs = null;
       }catch (Exception sql) {
	    sql.printStackTrace();
      } finally {
	   DBConnection.freeConnection(connection);
    }
        Log.log(5, "GMAction", "displayNpaRegistrationFormList", "Exited");
        return RecoveryPayList;
       }*/
//Diksha 30/05/2017	
/*	
 //==============Return recovery added on 6th Feb 16==================//
	public ActionForward ReturnClmRecovry(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(5, "GMAction", "showApprRegistrationFormSubmit", "Entered");
		HttpSession session = request.getSession(false);
	
		//System.out.println("===inside ReturnClmRecovry method 8447====");
		String l_str_Return=request.getParameter("return");
		//System.out.println("l_str_Return==8449===="+l_str_Return);
		
		Connection connection = DBConnection.getConnection(false);
		Statement stmt = connection.createStatement();
		ResultSet rs=null;

		RPActionForm actionForm = (RPActionForm) form;
     //====================get Remarks value==================//	
		//Map qryResponse = actionForm.getRt_remarks();
		//String l_strREMARKS_new = "";
		//String[] rmrk_no = actionForm.getRtcheck();
		 
		//Arrays.sort(rmrk_no); 
		//String a=Arrays.toString(rmrk_no); //toString the List or Vector
		//String l_strREMARKS_new[]=a.substring(1,a.length()-1).split(", ");
		
	    //String qrykey = null;		
		//String qryValue = null;
	//======================till here========================//	
		
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId + zoneId + branchId;
		
		int chSelectId = 0;
		String commVal = "";
		String query = "";
		
		String l_strCGPAN="";
		String l_strUNITNAME="";
		String l_strDD_NFT_NO="";
		String l_strAMOUNT="";
		String l_strMEMBER_ID="";
		String l_strEMAIL_ID="";
		String l_strEMAIL_IDArr[]={""};
		String l_strRAFC_ID="";
		String l_strPAY_ID="";
		String l_strCLM_REF_NO="";
		String l_strTYPE_OF_RECOVERY="";
		String l_strRECOVERY_AMOUNT="";
		String l_strLEGAL_EXPENSES="";
		String l_strAMOUNT_REMITTED_TO_CGTMSE="";
		String l_strRECOVERY_UPDATION_FLAG="";
		String l_strRECOVERY_CREATED_BY="";
		
		java.util.Date l_strRECOVERY_CREATED_DATE = new java.util.Date();
		String l_strRECOVERY_APPROVED_BY="";
		java.util.Date l_strRECOVERY_APPROVED_DATE = new java.util.Date();
		String l_strRECOVERY_FLAG="";
		String userId = user.getUserId();
		String l_strRECOVERY_APPROVED_DATE_new ="";
		int inst=0;
		
		java.util.Date date = new java.util.Date();
	    long t = date.getTime();
		java.sql.Date l_strCANC_DATE = new java.sql.Date(t);
		
	    boolean mailssend=false;
	    String qry_detail="";
	    //String[] checkId = actionForm.getRtcheck();
	    //ClaimActionForm CLForm = (ClaimActionForm)form;
	       Map resultMap=actionForm.getApprovedClaimrefnos();
	       //System.out.println("resultMap==727=="+resultMap);
	       Map clearRemarks = actionForm.getSecClmAppRemarks();//Remarks
	       //System.out.println("clearRemarks==729=="+clearRemarks);
	    
	    String[] qrykeyArr ={""};
	    
		try {
			Set SecClmApprvlStatusSet = resultMap.keySet();
			Set clearRemarksSet = clearRemarks.keySet();
			  //for (int i = 0; i < checkId.length; i++) 
			for(Iterator clearClaimRefNosIterator = SecClmApprvlStatusSet.iterator(); clearClaimRefNosIterator.hasNext();)
			  {
		//==============split CGPAN and id from here===// 		  
				    String key = (String)clearClaimRefNosIterator.next();
				    String status = (String)resultMap.get(key);
			    	System.out.println("status====status=="+status);
			    	String remarks=(String)clearRemarks.get(key);
			    	System.out.println("remarks====status=="+remarks);
				    //qrykeyArr=qrykey.split(",");
				    
				    //String rpc_id = qrykeyArr[0];   
				   // String l_strCGPAN_new = qrykeyArr[1];
		
		//==================till here=================//		    
					//qryValue = (String)qryResponse.get(qrykey);
					//System.out.println("qrykey==8571=="+qrykey);
					//System.out.println("Remark==8572=="+qryValue);
	        //===========mail code start from here================//
				
			 /*	
				 qry_detail="SELECT RE.RAFC_ID,RE.PAY_ID,CL.CLM_REF_NO,RE.CGPAN,(SELECT RMD_ID FROM RECOVERY_MODE_MASTER RM WHERE RM.RMD_ID = RE.TYPE_OF_RECOVERY)REC_TYPE," +
				 		" RE.RECOVERY_AMOUNT,RE.LEGAL_EXPENSES,AMOUNT_REMITTED_TO_CGTMSE,RE.RECOVERY_UPDATION_FLAG,RE.RECOVERY_CREATED_BY, to_char(RE.RECOVERY_CREATED_DATE,'dd/MM/yyyy')RECOVERY_CREATED_DATE,RE.RECOVERY_APPROVED_BY,to_char(RE.RECOVERY_APPROVED_DATE,'dd/MM/yyyy')RECOVERY_APPROVED_DATE,RE.RECOVERY_FLAG," +
				 		" CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID MBR_ID,MEM_ZONE_NAME,SSI_UNIT_NAME,PAY_INSTRUMENT_NUMBER,NVL (PAY_INSTRUMENT_DT, PAY_PAYMENT_DT) AS PAY_INSTRUMENT_DT " +
				 		" FROM MEMBER_INFO M,CLAIM_TC_DETAIL TC,CLAIM_DETAIL CL,RECOVRY_AFTER_BEFORE_FST_CLAIM RE,PAYMENT_DETAIL_TEMP PM,SSI_DETAIL S,APPLICATION_DETAIL AP" +
				 		" WHERE M.MEM_BNK_ID || M.MEM_ZNE_ID || M.MEM_BRN_ID =" +
				 		" CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID AND CLM_STATUS = 'AP' AND RECOVERY_UPDATION_FLAG = 'RP'" +
				 		" AND re.cgpan LIKE '%TC' AND RECOVERY_FLAG = 'AC' AND CL.CLM_REF_NO = TC.CLM_REF_NO AND RE.CLM_REF_NO = CL.CLM_REF_NO" +
				 		" AND PM.PAY_ID = RE.PAY_ID  AND CL.BID = S.BID  AND TC.CGPAN = AP.CGPAN AND RAFC_ID IN("+rpc_id+") AND RE.CGPAN IN('"+l_strCGPAN_new+"')" +
				 		" UNION ALL" +
				 		" SELECT RE.RAFC_ID,RE.PAY_ID,CL.CLM_REF_NO,RE.CGPAN,(SELECT RMD_ID FROM RECOVERY_MODE_MASTER RM WHERE RM.RMD_ID = RE.TYPE_OF_RECOVERY)REC_TYPE," +
				 		" RE.RECOVERY_AMOUNT,RE.LEGAL_EXPENSES,AMOUNT_REMITTED_TO_CGTMSE,RE.RECOVERY_UPDATION_FLAG,RE.RECOVERY_CREATED_BY,to_char(RE.RECOVERY_CREATED_DATE,'dd/MM/yyyy')RECOVERY_CREATED_DATE,RE.RECOVERY_APPROVED_BY,to_char(RE.RECOVERY_APPROVED_DATE,'dd/MM/yyyy')RECOVERY_APPROVED_DATE,RE.RECOVERY_FLAG," +
				 		" CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID MBR_ID,MEM_ZONE_NAME,SSI_UNIT_NAME,PAY_INSTRUMENT_NUMBER,NVL (PAY_INSTRUMENT_DT, PAY_PAYMENT_DT) AS PAY_INSTRUMENT_DT " +
				 		" FROM MEMBER_INFO M,CLAIM_WC_DETAIL TC,CLAIM_DETAIL CL,RECOVRY_AFTER_BEFORE_FST_CLAIM RE," +
				 		" PAYMENT_dETAIL_TEMP PM,SSI_DETAIL S,APPLICATION_DETAIL AP WHERE M.MEM_BNK_ID || M.MEM_ZNE_ID || M.MEM_BRN_ID =" +
				 		" CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID AND CLM_STATUS = 'AP'  AND RECOVERY_UPDATION_FLAG = 'RP'" +
				 		" AND re.cgpan LIKE '%WC' AND RECOVERY_FLAG = 'AC' AND CL.CLM_REF_NO = TC.CLM_REF_NO AND RE.CLM_REF_NO = CL.CLM_REF_NO" +
				 		" AND PM.PAY_ID = RE.PAY_ID AND CL.BID = S.BID AND TC.CGPAN = AP.CGPAN AND RAFC_ID IN("+rpc_id+") AND RE.CGPAN IN('"+l_strCGPAN_new+"')";	 
				 
				 //System.out.println("qry_detail===8529==="+qry_detail);
				 stmt = connection.createStatement();
			     rs = stmt.executeQuery((qry_detail));
				 while(rs.next())
				 {
				   l_strRAFC_ID	=rs.getString("RAFC_ID");
				   l_strPAY_ID=rs.getString("PAY_ID");
				   l_strCLM_REF_NO=rs.getString("CLM_REF_NO");
				   l_strTYPE_OF_RECOVERY=rs.getString("REC_TYPE");
				   l_strRECOVERY_AMOUNT=rs.getString("RECOVERY_AMOUNT");
				   l_strLEGAL_EXPENSES=rs.getString("LEGAL_EXPENSES");
				   l_strAMOUNT_REMITTED_TO_CGTMSE=rs.getString("AMOUNT_REMITTED_TO_CGTMSE");
				   l_strRECOVERY_UPDATION_FLAG=rs.getString("RECOVERY_UPDATION_FLAG");
				   l_strRECOVERY_CREATED_BY=rs.getString("RECOVERY_CREATED_BY");
				   
				   String l_strRECOVERY_CREATED_DATE1=rs.getString("RECOVERY_CREATED_DATE").replaceAll("00:00:00.0", "");
				   
				   String date_new=null;
				   
				    if(l_strRECOVERY_CREATED_DATE1!=null && l_strRECOVERY_CREATED_DATE1.length()>0)
				    {
				    	date_new="to_date('"+l_strRECOVERY_CREATED_DATE1+"','dd/MM/yyyy')";	
				    }else{
				    	date_new="null";
				    }
				   
				   l_strRECOVERY_APPROVED_BY=rs.getString("RECOVERY_APPROVED_BY");
				   
				   String date2_new="";
				   String l_strRECOVERY_APPROVED_DATE1=rs.getString("RECOVERY_APPROVED_DATE");
				   if(l_strRECOVERY_APPROVED_DATE1!=null && (l_strRECOVERY_APPROVED_DATE1.length()>0))
				   {
					   date2_new="to_date('"+l_strRECOVERY_APPROVED_DATE1+"','dd/MM/yyyy')";
				   }else{
					   date2_new="null";
				   }
				   l_strRECOVERY_FLAG=rs.getString("RECOVERY_FLAG");   
				   l_strCGPAN=rs.getString("CGPAN");
				   l_strUNITNAME=rs.getString("SSI_UNIT_NAME");
				   l_strDD_NFT_NO=rs.getString("PAY_INSTRUMENT_NUMBER");
				   l_strAMOUNT=rs.getString("AMOUNT_REMITTED_TO_CGTMSE");
				   l_strMEMBER_ID=rs.getString("MBR_ID");
				   
		//=================inserting record into canc table=====//	
     				   
		if(l_strREMARKS_new!=null && l_strREMARKS_new.length>0)	
		{
		  
			
			String insert_qry="INSERT INTO RECOVRY_AFTER_BEFR_FST_CLM_CNC VALUES('"+l_strRAFC_ID+"','"+l_strPAY_ID+"'" +
					",'"+l_strCLM_REF_NO+"','"+l_strCGPAN+"','"+l_strTYPE_OF_RECOVERY+"','"+l_strRECOVERY_AMOUNT+"','"+l_strLEGAL_EXPENSES+"','"+l_strAMOUNT_REMITTED_TO_CGTMSE+"'" +
					",'"+l_strRECOVERY_UPDATION_FLAG+"','"+l_strRECOVERY_CREATED_BY+"',"+date_new+",'"+l_strRECOVERY_APPROVED_BY+"'," +
					""+date2_new+",'"+l_strRECOVERY_FLAG+"','"+qryValue+"','"+userId+"',SYSDATE)";	  
			
			 //System.out.println("insert_qry==8552=="+insert_qry);	
			 stmt = connection.createStatement();
		     inst = stmt.executeUpdate((insert_qry));
		     
		     if(inst>0)
		     {
		      System.out.println("==Record inserted===");	 
		     }else{
		      System.out.println("==Record insertion failed===");	
		     }
		   }
	   }
	*/			 
		//==============delete after insertion==================//
		 /* 
		   String DeleteQry="delete from RECOVRY_AFTER_BEFORE_FST_CLAIM where RAFC_ID in("+rpc_id+") and CGPAN in('"+l_strCGPAN_new+"')";
		   System.out.println("DeleteQry===8647==="+DeleteQry);
		   stmt = connection.createStatement();
		   int del = stmt.executeUpdate((DeleteQry)); 
		    if(del>0)
		    {
		     System.out.println("==Record delete==");  	
		    }else{
		    	System.out.println("==Deletion failed=="); 
		    }
		   */ 
	    //=======================delete till here===============//
	/*		    	
		   String dtlQry="SELECT RE.CGPAN,AMOUNT_REMITTED_TO_CGTMSE,SSI_UNIT_NAME,PAY_INSTRUMENT_NUMBER,REMARKS,NVL (PAY_INSTRUMENT_DT, PAY_PAYMENT_DT) AS PAY_INSTRUMENT_DT" +
		   		" FROM MEMBER_INFO M,CLAIM_TC_DETAIL TC,CLAIM_DETAIL CL,RECOVRY_AFTER_BEFR_FST_CLM_CNC RE,PAYMENT_DETAIL_TEMP PM," +
		   		" SSI_DETAIL S,APPLICATION_DETAIL AP WHERE M.MEM_BNK_ID || M.MEM_ZNE_ID || M.MEM_BRN_ID =CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID" +
		   		" AND CLM_STATUS = 'AP' AND RECOVERY_UPDATION_FLAG = 'RP' AND re.cgpan LIKE '%TC' AND " +
		   		" RECOVERY_FLAG = 'AC' AND CL.CLM_REF_NO = TC.CLM_REF_NO AND RE.CLM_REF_NO = CL.CLM_REF_NO AND PM.PAY_ID = RE.PAY_ID" +
		   		" AND CL.BID = S.BID AND TC.CGPAN = AP.CGPAN AND RAFC_ID IN ("+rpc_id+") AND RE.CGPAN IN ('"+l_strCGPAN_new+"')" +
		   		" UNION ALL" +
		   		" SELECT RE.CGPAN,AMOUNT_REMITTED_TO_CGTMSE,SSI_UNIT_NAME,PAY_INSTRUMENT_NUMBER,REMARKS,NVL (PAY_INSTRUMENT_DT, PAY_PAYMENT_DT) AS PAY_INSTRUMENT_DT" +
		   		" FROM MEMBER_INFO M,CLAIM_WC_DETAIL TC,CLAIM_DETAIL CL,RECOVRY_AFTER_BEFR_FST_CLM_CNC RE," +
		   		" PAYMENT_dETAIL_TEMP PM,SSI_DETAIL S,APPLICATION_DETAIL AP WHERE M.MEM_BNK_ID || M.MEM_ZNE_ID || M.MEM_BRN_ID =" +
		   		" CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID AND CLM_STATUS = 'AP' AND RECOVERY_UPDATION_FLAG = 'RP' AND re.cgpan LIKE '%WC' AND RECOVERY_FLAG = 'AC'" +
		   		" AND CL.CLM_REF_NO = TC.CLM_REF_NO AND RE.CLM_REF_NO = CL.CLM_REF_NO AND PM.PAY_ID = RE.PAY_ID" +
		   		" AND CL.BID = S.BID AND TC.CGPAN = AP.CGPAN AND RAFC_ID IN ("+rpc_id+") AND RE.CGPAN IN ('"+l_strCGPAN_new+"')";  
		   
		 //  System.out.println("dtlQry===8658==="+dtlQry);
		   stmt = connection.createStatement();
		   rs = stmt.executeQuery((dtlQry));  
		   while(rs.next())
			 {	
		
		
		//======================till here=======================//
				 String subject = "Claim Recovery Return" ;
				 String mailBody="MLI is requested to note that appropriation of recovery payment for the CGPAN "+rs.getString("CGPAN")+""+
				 		" unit name "+l_strUNITNAME+"  having D.D. No./ NEFT No. "+l_strDD_NFT_NO+" for an amount of Rs."+rs.getString("AMOUNT_REMITTED_TO_CGTMSE")+" has been rejected due to reason " +
				 		" "+qryValue+"";
				 
				 //System.out.println("mailBody==="+mailBody); 
				 
		  //===========getEmail ID=======================//
		     
			  String qry_mailID="select distinct USR_EMAIL_ID from user_info where MEM_BNK_ID||MEM_ZNE_ID||MEM_BRN_ID in("+l_strMEMBER_ID+") and USR_STATUS='A'";
		      //System.out.println("qry_mailID===8649==="+qry_mailID);
		         stmt = connection.createStatement();
			     rs = stmt.executeQuery((qry_mailID));
			     while(rs.next())
			     {
			      l_strEMAIL_ID =rs.getString("USR_EMAIL_ID");	 
			     
			      System.out.println("l_strEMAIL_ID===8705==="+l_strEMAIL_ID);
		  //===============till here====================//		 
			 
				 String filePath = "";
				 String host = "192.168.10.118";
				 boolean sessionDebug = false;			
				 Properties props = System.getProperties();
				 props.put("mail.host", host);
				 props.put("mail.transport.protocol", "smtp");
				 props.put("mail.smtp.host", host);
				 props.put("mail.from", "administrator@cgtmse.in");
				 
				 Session session1= null;
			     session1 = Session.getDefaultInstance(props, null);
			     session1.setDebug(sessionDebug);
			     
			     javax.mail.internet.MimeMessage msg = new javax.mail.internet.MimeMessage(session1);
			     msg.setFrom(new javax.mail.internet.InternetAddress("administrator@cgtmse.in"));
			     javax.mail.internet.InternetAddress[] Toaddress = 
			     {
			      new javax.mail.internet.InternetAddress(l_strEMAIL_ID)
			     };     
			     msg.setRecipients(javax.mail.Message.RecipientType.TO, Toaddress);
			     msg.setSubject(subject);
			     msg.setSentDate(new java.util.Date());
				 msg.setText(mailBody);
				 Transport.send(msg);
				 System.out.println("==Message sent Successfully===");
			 } 
			  } //inner while close
	
			}//for loop close
					
		}
		catch (Exception sql) 
	    {
			connection.rollback();
			throw new DatabaseException(
					"A Problem Occured While Performing Updation/Deletion Action"
							+ sql.getMessage());
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception ss) {
				ss.printStackTrace();
			}
			DBConnection.freeConnection(connection);
		}
		request.setAttribute("message",
				"<b>Recovery Returned Successfully.</b>");
		Log.log(5, "GMAction", "showApprRegistrationFormSubmit", "Exited");
		return mapping.findForward("success");
	}
*/    //Diksha.....        
 //============================till here==============================//	
	/*public ActionForward getPaymentMadeForRecoveryInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RPActionForm actionForm = (RPActionForm) form;
		// actionForm.setMemberId("");
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, day);
		Date prevdate = cal.getTime();
		GeneralReport general = new GeneralReport();
		general.setDateOfTheDocument24(prevdate);
		general.setDateOfTheDocument25(date);
		BeanUtils.copyProperties(actionForm, general);
		return mapping.findForward("success");
	}*/
	
	/*public ActionForward submitClmRecovryAppropriation(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(5, "GMAction", "showApprRegistrationFormSubmit", "Entered");
		HttpSession session = request.getSession(false);
		// System.out.println("===8778===");
		Connection connection = DBConnection.getConnection(false);
		// PreparedStatement prepareStatement = null;
		Statement statement = connection.createStatement();

		RPActionForm actionForm = (RPActionForm) form;
		User user = getUserInformation(request);
		// int checkId[];
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId + zoneId + branchId;
		String actionType = request.getParameter("action");
	
		int[] checkId = actionForm.getCheck();
		int chSelectId = 0;
		String commVal = "";
		String query = "";
		AdminHelper adminHelper = new AdminHelper();
		//checkId = actionForm.getCheck(); //Diksha
		 boolean mailssend=false;
		 //Diksha 30/05/2017
		 Map resultMap=actionForm.getApprovedClaimrefnos();
	     Map clearRemarks = actionForm.getSecClmAppRemarks();//Remarks
	     Set SecClmApprvlStatusSet = resultMap.keySet();
	    
		// System.out.println("===8806===");
	     String[] qrykeyArr ={""}; //Diksha..
		try {
			//Diksha 30/05/2017
			 for(Iterator clearClaimRefNosIterator = SecClmApprvlStatusSet.iterator(); clearClaimRefNosIterator.hasNext();)
			 {
				// System.out.println("===8813===");
				 String key = (String)clearClaimRefNosIterator.next();
		    	 //System.out.println("key===8813=="+key);
		    	 String status = (String)resultMap.get(key);
		    	 //System.out.println("status===8814=="+status);
		    	 String remarks=(String)clearRemarks.get(key);
		         //System.out.println("remarks====8816==="+remarks);
		        
		         String values =  key; // 
		         
		         qrykeyArr=values.split(",");
		         String rpc_id = qrykeyArr[0]; 
		         int rpc_id_new=0; 
		         if(rpc_id!=null && rpc_id.length()>0)   //Diksha...
//Diksha...
			 for(int i = 0; i < checkId.length; i++)
		         {
				 	 chSelectId = checkId[i];
		        	rpc_id_new=Integer.
		        	 * *parseInt(rpc_id);	 
		        	 
		         }
//Diksha...... String l_strCGPAN = qrykeyArr[1];
				// System.out.println("rpc_id_new==8827=="+rpc_id_new);
				 //System.out.println("l_strCGPAN==8828=="+l_strCGPAN);
		     
		//============Save or Accept Appropriation code	from here==========//	 
//Diksha		        // if(status!=null && status.equals("AP"))
		         {
		           //System.out.println("====Approve case=====");
					CallableStatement callable = connection.prepareCall("{?=call "
							+ "FUNCCLMRECOVERYAPPROPRIATION(?,?,?)}");
					callable.registerOutParameter(1, Types.INTEGER);					
					callable.setInt(2, chSelectId);
					//callable.setInt(2, rpc_id_new);
					callable.setString(3, user.getUserId());
					callable.registerOutParameter(4, Types.VARCHAR);
					callable.execute();
					int errorCode = callable.getInt(1);
					String error = callable.getString(4);
					Log.log(Log.DEBUG, "GMDAO", "submitUpgradationDetails",
							"error code and error" + errorCode + "," + error);

					if (errorCode == Constants.FUNCTION_FAILURE) {
						Log.log(Log.ERROR, "GMDAO", "submitUpgradationDetails", error);

						callable.close();
						callable = null;
						throw new DatabaseException(error);
					}
	//==============Return code from here===================//	         
		      }else if(status!=null && status.equals("RT"))
		      {
                //System.out.println("====Return case=====");
		  		ResultSet rs=null;
		  		String l_strUNITNAME="";
		  		String l_strDD_NFT_NO="";
		  		String l_strAMOUNT="";
		  		String l_strMEMBER_ID="";
		  		String l_strEMAIL_ID="";
		  		String l_strEMAIL_IDArr[]={""};
		  		String l_strRAFC_ID="";
		  		String l_strPAY_ID="";
		  		String l_strCLM_REF_NO="";
		  		String l_strTYPE_OF_RECOVERY="";
		  		String l_strRECOVERY_AMOUNT="";
		  		String l_strLEGAL_EXPENSES="";
		  		String l_strAMOUNT_REMITTED_TO_CGTMSE="";
		  		String l_strRECOVERY_UPDATION_FLAG="";
		  		String l_strRECOVERY_CREATED_BY="";
		  		java.util.Date l_strRECOVERY_CREATED_DATE = new java.util.Date();
		  		String l_strRECOVERY_APPROVED_BY="";
		  		java.util.Date l_strRECOVERY_APPROVED_DATE = new java.util.Date();
		  		String l_strRECOVERY_FLAG="";
		  		String userId = user.getUserId();
		  		String l_strRECOVERY_APPROVED_DATE_new ="";
		  		int inst=0;
		  		java.util.Date date = new java.util.Date();
		  	    long t = date.getTime();
		  		java.sql.Date l_strCANC_DATE = new java.sql.Date(t);
		  	    String qry_detail="";
		  	  
		  	    qry_detail="SELECT RE.RAFC_ID,RE.PAY_ID,CL.CLM_REF_NO,RE.CGPAN,(SELECT RMD_ID FROM RECOVERY_MODE_MASTER RM WHERE RM.RMD_ID = RE.TYPE_OF_RECOVERY)REC_TYPE," +
		 		" RE.RECOVERY_AMOUNT,RE.LEGAL_EXPENSES,AMOUNT_REMITTED_TO_CGTMSE,RE.RECOVERY_UPDATION_FLAG,RE.RECOVERY_CREATED_BY, to_char(RE.RECOVERY_CREATED_DATE,'dd/MM/yyyy')RECOVERY_CREATED_DATE,RE.RECOVERY_APPROVED_BY,to_char(RE.RECOVERY_APPROVED_DATE,'dd/MM/yyyy')RECOVERY_APPROVED_DATE,RE.RECOVERY_FLAG," +
		 		" CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID MBR_ID,MEM_ZONE_NAME,SSI_UNIT_NAME,PAY_INSTRUMENT_NUMBER,NVL (PAY_INSTRUMENT_DT, PAY_PAYMENT_DT) AS PAY_INSTRUMENT_DT " +
		 		" FROM MEMBER_INFO M,CLAIM_TC_DETAIL TC,CLAIM_DETAIL CL,RECOVRY_AFTER_BEFORE_FST_CLAIM RE,PAYMENT_DETAIL_TEMP PM,SSI_DETAIL S,APPLICATION_DETAIL AP" +
		 		" WHERE M.MEM_BNK_ID || M.MEM_ZNE_ID || M.MEM_BRN_ID =" +
		 		" CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID AND CLM_STATUS = 'AP' AND RECOVERY_UPDATION_FLAG = 'RP'" +
		 		" AND re.cgpan LIKE '%TC' AND RECOVERY_FLAG = 'AC' AND CL.CLM_REF_NO = TC.CLM_REF_NO AND RE.CLM_REF_NO = CL.CLM_REF_NO" +
		 		" AND PM.PAY_ID = RE.PAY_ID  AND CL.BID = S.BID  AND TC.CGPAN = AP.CGPAN AND RAFC_ID IN("+rpc_id_new+") AND RE.CGPAN IN('"+l_strCGPAN+"')" +
		 		" UNION ALL" +
		 		" SELECT RE.RAFC_ID,RE.PAY_ID,CL.CLM_REF_NO,RE.CGPAN,(SELECT RMD_ID FROM RECOVERY_MODE_MASTER RM WHERE RM.RMD_ID = RE.TYPE_OF_RECOVERY)REC_TYPE," +
		 		" RE.RECOVERY_AMOUNT,RE.LEGAL_EXPENSES,AMOUNT_REMITTED_TO_CGTMSE,RE.RECOVERY_UPDATION_FLAG,RE.RECOVERY_CREATED_BY,to_char(RE.RECOVERY_CREATED_DATE,'dd/MM/yyyy')RECOVERY_CREATED_DATE,RE.RECOVERY_APPROVED_BY,to_char(RE.RECOVERY_APPROVED_DATE,'dd/MM/yyyy')RECOVERY_APPROVED_DATE,RE.RECOVERY_FLAG," +
		 		" CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID MBR_ID,MEM_ZONE_NAME,SSI_UNIT_NAME,PAY_INSTRUMENT_NUMBER,NVL (PAY_INSTRUMENT_DT, PAY_PAYMENT_DT) AS PAY_INSTRUMENT_DT " +
		 		" FROM MEMBER_INFO M,CLAIM_WC_DETAIL TC,CLAIM_DETAIL CL,RECOVRY_AFTER_BEFORE_FST_CLAIM RE," +
		 		" PAYMENT_dETAIL_TEMP PM,SSI_DETAIL S,APPLICATION_DETAIL AP WHERE M.MEM_BNK_ID || M.MEM_ZNE_ID || M.MEM_BRN_ID =" +
		 		" CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID AND CLM_STATUS = 'AP'  AND RECOVERY_UPDATION_FLAG = 'RP'" +
		 		" AND re.cgpan LIKE '%WC' AND RECOVERY_FLAG = 'AC' AND CL.CLM_REF_NO = TC.CLM_REF_NO AND RE.CLM_REF_NO = CL.CLM_REF_NO" +
		 		" AND PM.PAY_ID = RE.PAY_ID AND CL.BID = S.BID AND TC.CGPAN = AP.CGPAN AND RAFC_ID IN("+rpc_id_new+") AND RE.CGPAN IN('"+l_strCGPAN+"')";	 
		 
		        //System.out.println("qry_detail===8529==="+qry_detail);
		        statement = connection.createStatement();
		        rs = statement.executeQuery((qry_detail));
		        
		        while(rs.next())
				 {
				   l_strRAFC_ID	=rs.getString("RAFC_ID");
				   l_strPAY_ID=rs.getString("PAY_ID");
				   l_strCLM_REF_NO=rs.getString("CLM_REF_NO");
				   l_strTYPE_OF_RECOVERY=rs.getString("REC_TYPE");
				   l_strRECOVERY_AMOUNT=rs.getString("RECOVERY_AMOUNT");
				   l_strLEGAL_EXPENSES=rs.getString("LEGAL_EXPENSES");
				   l_strAMOUNT_REMITTED_TO_CGTMSE=rs.getString("AMOUNT_REMITTED_TO_CGTMSE");
				   l_strRECOVERY_UPDATION_FLAG=rs.getString("RECOVERY_UPDATION_FLAG");
				   l_strRECOVERY_CREATED_BY=rs.getString("RECOVERY_CREATED_BY");
				   
				   String l_strRECOVERY_CREATED_DATE1=rs.getString("RECOVERY_CREATED_DATE").replaceAll("00:00:00.0", "");
				   
				   String date_new=null;
				   
				    if(l_strRECOVERY_CREATED_DATE1!=null && l_strRECOVERY_CREATED_DATE1.length()>0)
				    {
				    	date_new="to_date('"+l_strRECOVERY_CREATED_DATE1+"','dd/MM/yyyy')";	
				    }else{
				    	date_new="null";
				    }
				   l_strRECOVERY_APPROVED_BY=rs.getString("RECOVERY_APPROVED_BY");
				  
				   String date2_new="";
				   String l_strRECOVERY_APPROVED_DATE1=rs.getString("RECOVERY_APPROVED_DATE");
				   if(l_strRECOVERY_APPROVED_DATE1!=null && (l_strRECOVERY_APPROVED_DATE1.length()>0))
				   {
					 date2_new="to_date('"+l_strRECOVERY_APPROVED_DATE1+"','dd/MM/yyyy')";
				   }else{
					 date2_new="null";
				   }
				   //System.out.println("date2_new===8581==="+date2_new);
				   l_strRECOVERY_FLAG=rs.getString("RECOVERY_FLAG");
				   //System.out.println("=====8556====");
  
				   l_strCGPAN=rs.getString("CGPAN");
				   l_strUNITNAME=rs.getString("SSI_UNIT_NAME");
				   l_strDD_NFT_NO=rs.getString("PAY_INSTRUMENT_NUMBER");
				   l_strAMOUNT=rs.getString("AMOUNT_REMITTED_TO_CGTMSE");
				   l_strMEMBER_ID=rs.getString("MBR_ID");
     //==============inserting code into canc table=========//

					String insert_qry="INSERT INTO RECOVRY_AFTER_BEFR_FST_CLM_CNC VALUES('"+l_strRAFC_ID+"','"+l_strPAY_ID+"'" +
							",'"+l_strCLM_REF_NO+"','"+l_strCGPAN+"','"+l_strTYPE_OF_RECOVERY+"','"+l_strRECOVERY_AMOUNT+"','"+l_strLEGAL_EXPENSES+"','"+l_strAMOUNT_REMITTED_TO_CGTMSE+"'" +
							",'"+l_strRECOVERY_UPDATION_FLAG+"','"+l_strRECOVERY_CREATED_BY+"',"+date_new+",'"+l_strRECOVERY_APPROVED_BY+"'," +
							""+date2_new+",'"+l_strRECOVERY_FLAG+"','"+remarks+"','"+userId+"',SYSDATE)";	  
					
					//System.out.println("insert_qry==8952=="+insert_qry);	
					statement = connection.createStatement();
				     inst = statement.executeUpdate((insert_qry));
				     
				     if(inst>0)
				     {
				      System.out.println("==Record inserted===");	 
				     }else{
				      System.out.println("==Record insertion failed===");	
				     }
		//==============delete after insertion==================//
				 /* 
				   String DeleteQry="delete from RECOVRY_AFTER_BEFORE_FST_CLAIM where RAFC_ID in("+rpc_id_new+") and CGPAN in('"+l_strCGPAN+"')";
				   System.out.println("DeleteQry===8647==="+DeleteQry);
				   statement = connection.createStatement();
				   int del =statement.executeUpdate((DeleteQry)); 
				    if(del>0)
				    {
				     System.out.println("==Record delete==");  	
				    }else{
				     System.out.println("==Deletion failed=="); 
				    }
			
	   //======================fetch data for mail===============//
		   String dtlQry="SELECT USR_ID,RE.CGPAN,AMOUNT_REMITTED_TO_CGTMSE,SSI_UNIT_NAME,PAY_INSTRUMENT_NUMBER,REMARKS,NVL (PAY_INSTRUMENT_DT, PAY_PAYMENT_DT) AS PAY_INSTRUMENT_DT" +
				   	    " FROM MEMBER_INFO M,CLAIM_TC_DETAIL TC,CLAIM_DETAIL CL,RECOVRY_AFTER_BEFR_FST_CLM_CNC RE,PAYMENT_DETAIL_TEMP PM," +
				   		" SSI_DETAIL S,APPLICATION_DETAIL AP WHERE M.MEM_BNK_ID || M.MEM_ZNE_ID || M.MEM_BRN_ID =CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID" +
				   		" AND CLM_STATUS = 'AP' AND RECOVERY_UPDATION_FLAG = 'RP' AND re.cgpan LIKE '%TC' AND " +
				   		" RECOVERY_FLAG = 'AC' AND CL.CLM_REF_NO = TC.CLM_REF_NO AND RE.CLM_REF_NO = CL.CLM_REF_NO AND PM.PAY_ID = RE.PAY_ID" +
				   		" AND CL.BID = S.BID AND TC.CGPAN = AP.CGPAN AND RAFC_ID IN ("+rpc_id_new+") AND RE.CGPAN IN ('"+l_strCGPAN+"')" +
				   		" UNION ALL" +
				   		" SELECT USR_ID,RE.CGPAN,AMOUNT_REMITTED_TO_CGTMSE,SSI_UNIT_NAME,PAY_INSTRUMENT_NUMBER,REMARKS,NVL (PAY_INSTRUMENT_DT, PAY_PAYMENT_DT) AS PAY_INSTRUMENT_DT" +
				   		" FROM MEMBER_INFO M,CLAIM_WC_DETAIL TC,CLAIM_DETAIL CL,RECOVRY_AFTER_BEFR_FST_CLM_CNC RE," +
				   		" PAYMENT_dETAIL_TEMP PM,SSI_DETAIL S,APPLICATION_DETAIL AP WHERE M.MEM_BNK_ID || M.MEM_ZNE_ID || M.MEM_BRN_ID =" +
				   		" CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID AND CLM_STATUS = 'AP' AND RECOVERY_UPDATION_FLAG = 'RP' AND re.cgpan LIKE '%WC' AND RECOVERY_FLAG = 'AC'" +
				   		" AND CL.CLM_REF_NO = TC.CLM_REF_NO AND RE.CLM_REF_NO = CL.CLM_REF_NO AND PM.PAY_ID = RE.PAY_ID" +
				   		" AND CL.BID = S.BID AND TC.CGPAN = AP.CGPAN AND RAFC_ID IN ("+rpc_id_new+") AND RE.CGPAN IN ('"+l_strCGPAN+"')";  
				   
				 //  System.out.println("dtlQry===8658==="+dtlQry);
				     statement = connection.createStatement();
				     rs = statement.executeQuery((dtlQry)); 
				     
				 while(rs.next())
				 {
					 String USR_ID=rs.getString("USR_ID");
				 //======================till here=======================//
						 String subject = "Claim Recovery Return" ;
						 String mailBody="MLI is requested to note that appropriation of recovery payment for the CGPAN "+rs.getString("CGPAN")+""+
						 		" unit name "+l_strUNITNAME+"  having D.D. No./ NEFT No. "+l_strDD_NFT_NO+" for an amount of Rs."+rs.getString("AMOUNT_REMITTED_TO_CGTMSE")+" has been rejected due to reason " +
						 		" "+remarks+"";
						 
						//System.out.println("mailBody==="+mailBody); 
						 
				//==================getEmail ID=========================//	 
					    String qry_mailID="select distinct USR_EMAIL_ID from user_info where MEM_BNK_ID||MEM_ZNE_ID||MEM_BRN_ID in("+l_strMEMBER_ID+") and USR_STATUS='A' and USR_ID='"+USR_ID+"'";
					   // System.out.println("qry_mailID===9004=="+qry_mailID);
					    statement = connection.createStatement();
					    rs = statement.executeQuery((qry_mailID));
					    while(rs.next())
					     {
					      l_strEMAIL_ID =rs.getString("USR_EMAIL_ID");	 
					     // System.out.println("l_strEMAIL_ID===8705==="+l_strEMAIL_ID);
				  //===============till here====================//		 
					 /*
						 String filePath = "";
						 String host = "192.168.10.118";
						 boolean sessionDebug = false;			
						 Properties props = System.getProperties();
						 props.put("mail.host", host);
						 props.put("mail.transport.protocol", "smtp");
						 props.put("mail.smtp.host", host);
						 props.put("mail.from", "administrator@cgtmse.in");
						 
						 Session session1= null;
					     session1 = Session.getDefaultInstance(props, null);
					     session1.setDebug(sessionDebug);
					     
						 //System.out.println("==Message sent Successfully===");
					     javax.mail.internet.MimeMessage msg = new javax.mail.internet.MimeMessage(session1);
					     msg.setFrom(new javax.mail.internet.InternetAddress("administrator@cgtmse.in"));
					     javax.mail.internet.InternetAddress[] Toaddress = 
					     {
					      new javax.mail.internet.InternetAddress(l_strEMAIL_ID)
					     };     
					     msg.setRecipients(javax.mail.Message.RecipientType.TO, Toaddress);
					     msg.setSubject(subject);
					     msg.setSentDate(new java.util.Date());
						 msg.setText(mailBody);
						 Transport.send(msg);
					
					  } //inner while close
				   }
				 }
	 
		      }
    //====================till here=========================//		         
			}//for loop close
			connection.commit();
			// end for
		}// end try
		catch (Exception sql) {
			connection.rollback();
			throw new DatabaseException(
					"A Problem Occured While Performing Updation/Deletion Action"
							+ sql.getMessage());
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
			} catch (Exception ss) {
				ss.printStackTrace();
			}
			DBConnection.freeConnection(connection);
		}
		request.setAttribute("message",
				"<b>Successfully Claim Recovery Appropriated.</b>");
		Log.log(5, "GMAction", "showApprRegistrationFormSubmit", "Exited");
		// System.out.println("GMA showApprRegistrationFormSubmit end");
		return mapping.findForward("success");
	}*/

	  public ActionForward verifyCredtNoteDet(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	    throws Exception
	{
	    String methodName = "showCLDANGenFilter";
	    Log.log(4, "RPAction", methodName, "Entered");
	    String forwardPage = "";
	    User user = getUserInformation(request);
	    Log.log(4, "RPAction", "generateCLDAN", (new StringBuilder()).append("Logged in user: ").append(user.getUserId()).toString());
	    DynaActionForm rpAllocationForm = (DynaActionForm)form;
	    rpAllocationForm.initialize(mapping);
	    Log.log(5, "RPAction", methodName, "Fetching Member Details for whom CLDAN has to be generated");
	       forwardPage = "memberInfo";
	    Log.log(4, "RPAction", methodName, "Exited");
	    return mapping.findForward(forwardPage);
	}

	    
	    
	    public ActionForward displayCredtitNoteDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	    throws Exception
	{
	    String methodName = "showCLDANGenFilter";
	    Log.log(4, "RPAction", methodName, "Entered");
	    String forwardPage = "";
	    User user = getUserInformation(request);
	    Log.log(4, "RPAction", "generateCLDAN", (new StringBuilder()).append("Logged in user: ").append(user.getUserId()).toString());
	    DynaActionForm rpAllocationForm = (DynaActionForm)form;
	    ArrayList creditDetailsList    = new ArrayList();
		 Statement stmt        = null;
	    ResultSet result      = null;
	    
	    Connection connection = DBConnection.getConnection();
	    HttpSession session    = request.getSession();
	    
	    java.sql.Date startDate = null;
	    java.sql.Date endDate = null;

	    java.util.Date sDate = (java.util.Date)rpAllocationForm.get("fromdt");
	    java.util.Date eDate = (java.util.Date)rpAllocationForm.get("todt");
	 //   System.out.println(sDate);
	   // System.out.println(eDate);

	    String stDate = String.valueOf(sDate);
	    String estDate = String.valueOf(eDate);
	    
	  //  System.out.println(stDate);
	   // System.out.println(stDate);
	  

	  
	    try
	    {
	       String query =                              "SELECT A.mem_bnk_id,\n" + 
	                                "             A.mem_zne_id,\n" + 
	                                "             A.mem_brn_id,\n" + 
	                                "             A.cgpan,\n" + 
	                                "             dc.dan_id,\n" + 
	                                "             DCI_NEW_DAN_ID,\n" + 
	                                "             dan_type,\n" + 
	                                "             dci_amount_raised DANAMT,\n" + 
	                                "             dci_amount_cancelled CANCAMT,\n" + 
	                                "             dci_stax_amt ST,\n" + 
	                                "             dci_ecess_amt EC,\n" + 
	                                "             dci_hecess_amt HEC,\n" + 
	                                "            to_char(dan_generated_dt) DANDT,\n" + 
	                                "             DCI_BASE_AMT,\n" + 
	                                "             mem_state_name , \n" + 
	                                " DCI_SWBHCESS_AMT, DCI_KKALYANCESS_AMT " +
	                                "        FROM dan_cgpan_info dc, demand_advice_info da, APPLICATION_DETAIL A, member_info m\n" + 
	                                "       WHERE     dc.dan_id = da.dan_id\n" + 
	                                "             AND DC.CGPAN = A.CGPAN\n" + 
	                                "             AND TRUNC (dan_generated_dt) >= '01-jul-2012'" +
	                                "             AND TRUNC (dan_generated_dt) >= to_date('" +sDate + "','dd/mm/yyyy')  " +
	                                "             AND TRUNC (dan_generated_dt) <= to_date('" +eDate + "','dd/mm/yyyy')  " +
	                                "             AND dci_amount_Raised - NVL (dci_amount_cancelled, 0) > 0\n" + 
	                                "             AND dan_type IN ('GF', 'CG')\n" + 
	                                "             AND DCI_ALLOCATION_FLAG = 'N'\n" + 
	                                "             AND TRUNC (APP_GUAR_START_DATE_TIME) IS NULL\n" + 
	                                "             and dci_new_dan_id is null\n" + 
	                                "             and a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id\n" + 
	                                "             AND replace(MEM_STATE_NAME,'&',' ') NOT IN  'JAMMU   KASHMIR'\n" + 
	                                "            /* ORDER BY 1,2,3; */\n" + 
	                                "      Union all\n" + 
	                                "      SELECT A.mem_bnk_id,\n" + 
	                                "             A.mem_zne_id,\n" + 
	                                "             A.mem_brn_id,\n" + 
	                                "             A.cgpan,\n" + 
	                                "             dc.dan_id,\n" + 
	                                "             DCI_NEW_DAN_ID,\n" + 
	                                "             dan_type,\n" + 
	                                "             dci_amount_raised DANAMT,\n" + 
	                                "             dci_amount_cancelled CANCAMT,\n" + 
	                                "             dci_stax_amt ST,\n" + 
	                                "             dci_ecess_amt EC,\n" + 
	                                "             dci_hecess_amt HEC,\n" +                                 
	                                "             to_char(dan_generated_dt) DANDT,\n" + 
	                                "             DCI_BASE_AMT,\n" + 
	                                "             mem_state_name, \n" + 
	                                " DCI_SWBHCESS_AMT, DCI_KKALYANCESS_AMT "+
	                                "        FROM dan_cgpan_info dc, demand_advice_info da, APPLICATION_DETAIL A, member_info m\n" + 
	                                "       WHERE     dc.dan_id = da.dan_id\n" + 
	                                "             AND DC.CGPAN = A.CGPAN\n" + 
	                                "             AND TRUNC (dan_generated_dt) >= '01-jul-2012'\n" + 
	                                "             AND TRUNC (dan_generated_dt) >= to_date('" +sDate + "','dd/mm/yyyy')  " +
	                                "             AND TRUNC (dan_generated_dt) <= to_date('" +eDate + "','dd/mm/yyyy')  " +
	                                "             AND dci_amount_Raised - NVL (dci_amount_cancelled, 0) > 0\n" + 
	                                "             AND dan_type IN ('GF', 'CG')\n" + 
	                                "             AND DCI_ALLOCATION_FLAG = 'N'\n" + 
	                                "             AND TRUNC (APP_GUAR_START_DATE_TIME) IS NOT NULL\n" + 
	                                "             and dci_new_dan_id is null\n" + 
	                                "             and a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id\n" + 
	                                "             AND replace(MEM_STATE_NAME,'&',' ') NOT IN  'JAMMU   KASHMIR'\n" + 
	                                "             ORDER BY 1,2,3 "; 
	       stmt   = connection.createStatement();
	       result = stmt.executeQuery(query);
	       String CreditDetails[] = null;
	       while(result.next())
	       {
	    	   CreditDetails = new String[17];
	    	   
	    	
	    	   CreditDetails[0] = result.getString(1);
	    	  // System.out.println( CreditDetails[0]);
	    	   CreditDetails[1] = result.getString(2);
	    	 //  System.out.println( CreditDetails[1]);
	    	   CreditDetails[2] = result.getString(3);
	    	  // System.out.println( CreditDetails[2]);
	    	   CreditDetails[3] = result.getString(4);
	    	  // System.out.println( CreditDetails[3]);
	    	   CreditDetails[4] = result.getString(5);
	    	  // System.out.println( CreditDetails[4]);
	    	   CreditDetails[5] = result.getString(6);
	    	  // System.out.println( CreditDetails[5]);
	    	   CreditDetails[6] = result.getString(7);
	    	  // System.out.println( CreditDetails[6]);
	    	   CreditDetails[7] = result.getString(8);
	    	 //  System.out.println( CreditDetails[7]);
	    	   CreditDetails[8] = result.getString(9);
	    	 //  System.out.println( CreditDetails[8]);
	    	   CreditDetails[9] = result.getString(10);
	    	  // System.out.println( CreditDetails[9]);
	    	   CreditDetails[10] = result.getString(11);
	    	  // System.out.println( CreditDetails[10]);
	    	   CreditDetails[11] = result.getString(12);
	    	  // System.out.println( CreditDetails[11]);
	    	   CreditDetails[12] = result.getString(13);
	    	  // System.out.println( CreditDetails[12]);
	    	   CreditDetails[13] = result.getString(14);
	    	 //  System.out.println( CreditDetails[13]);
	    	   CreditDetails[14] = result.getString(15);
	    	 //  System.out.println( CreditDetails[14]);
	    	   CreditDetails[15] = result.getString(16);
               CreditDetails[16] = result.getString(17);
	    	   creditDetailsList.add(CreditDetails);
	        }
	       session.setAttribute("fromdat",sDate);
	       session.setAttribute("toDate",eDate);
	        session.setAttribute("creditDetailsList",creditDetailsList);
	        session.setAttribute("creditDetailsListSize",creditDetailsList.size());
	        result = null;
	        stmt = null;


	    }
	    catch(Exception exception)
	    {
	      Log.logException(exception);
	      throw new DatabaseException(exception.getMessage());
	    }
	    return mapping.findForward("success");
	    }


	    
	    
	    
	    public ActionForward generateCreditNoteInput(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	    throws Exception
	{
	    String methodName = "showCLDANGenFilter";
	    Log.log(4, "RPAction", methodName, "Entered");
	    String forwardPage = "";
	    User user = getUserInformation(request);
	    Log.log(4, "RPAction", "generateCLDAN", (new StringBuilder()).append("Logged in user: ").append(user.getUserId()).toString());
	    DynaActionForm rpAllocationForm = (DynaActionForm)form;
	      forwardPage = "memberInfo";
	    Log.log(4, "RPAction", methodName, "Exited");
	    return mapping.findForward(forwardPage);
	}

	    
	    
	    public ActionForward generateCreditNote(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	    throws Exception
	{
	    	String methodName = "showCLDANGenFilter";
	        Log.log(4, "RPAction", methodName, "Entered");
	        String forwardPage = "";
	        User user = getUserInformation(request);
	        Log.log(4, "RPAction", "generateCLDAN", (new StringBuilder()).append("Logged in user: ").append(user.getUserId()).toString());
	        DynaActionForm rpAllocationForm = (DynaActionForm)form;
	        ArrayList creditDetailsList    = new ArrayList();
	    	 Statement stmt        = null;
	        ResultSet result      = null;
	        
	        Connection connection = DBConnection.getConnection();
	        HttpSession session    = request.getSession();
	        
	        java.sql.Date startDate = null;
	        java.sql.Date endDate = null;
	        java.sql.Date valDate = null;

	        java.util.Date sDate = (java.util.Date)rpAllocationForm.get("fromdt");
	        java.util.Date eDate = (java.util.Date)rpAllocationForm.get("todt");
	        java.util.Date vDate = (java.util.Date)rpAllocationForm.get("valueDt");
	    
	      //  System.out.println(sDate);
	       // System.out.println(eDate);
	       // System.out.println(vDate);
	        if(sDate == null || sDate.equals(""))
	            startDate = null;
	        else
	        if(sDate != null)
	            startDate = new java.sql.Date(sDate.getTime());
	        if(eDate == null || eDate.equals(""))
	            endDate = null;
	        else
	        if(eDate != null)
	            endDate = new java.sql.Date(eDate.getTime());
	        if(vDate == null || vDate.equals(""))
	            endDate = null;
	        else
	        if(vDate != null)
	        	valDate = new java.sql.Date(vDate.getTime());
	        String userId = user.getUserId();
	       // System.out.println(startDate);
	      //  System.out.println(endDate);
	       // System.out.println(valDate);
	        String stDate = String.valueOf(sDate);
	        String estDate = String.valueOf(eDate);
	         String vaDate = String.valueOf(vDate);              
	        Connection connection1= null;
	        if (connection1 == null) {
	            connection1 = DBConnection.getConnection(false);
	                    }
	                   CallableStatement CreditDanGen = null;

	        try {
	        	CreditDanGen = 
	                    connection.prepareCall("{? = call FUNCGENSTAXREFUNDDAN(?,?,?,?,?)}");
	            CreditDanGen.registerOutParameter(1, Types.INTEGER);
	            CreditDanGen.registerOutParameter(6, Types.VARCHAR);           
	            CreditDanGen.setDate(2, startDate);
	            CreditDanGen.setDate(3, endDate);
	            CreditDanGen.setDate(4, valDate);
	            CreditDanGen.setString(5, userId);
	             CreditDanGen.executeQuery();
	            int status = CreditDanGen.getInt(1);
	            if (status == Constants.FUNCTION_FAILURE) {
	                String errorCode = CreditDanGen.getString(6);
	                                      connection.rollback();
	                                      CreditDanGen.close();
	                                      CreditDanGen = null;
	                throw new DatabaseException(errorCode);
	            }
	            CreditDanGen.close();
	            CreditDanGen = null;
	            connection1.commit();
	              } catch (SQLException e) {
	                  throw new DatabaseException(e.getMessage());
	        }
	       
	    Log.log(5, "RPAction", methodName, "Fetched Member Details for whom CLDAN has to be generated");
	   
	    session.setAttribute("TARGET_URL", "generateCLDAN.do?method=generateCLDAN");
	    session.setAttribute("DAN_TYPE", "CLDAN");
	    forwardPage = "success";
	    Log.log(4, "RPAction", methodName, "Exited");
	    return mapping.findForward(forwardPage);
	}
	    
	    // added depak
	    
	 // display recovery approperiated rp detail CBD
	/*	public ActionForward showRecoveryPaymentDetailsforRPNo(ActionMapping mapping, ActionForm form,
				HttpServletRequest request, HttpServletResponse response)
				throws Exception {		
			System.out.println(" showRecoveryPaymentDetailsforRPNo ");
			Log.log(5, "GMAction", "showRecoveryPaymentDetailsforRPNo", "Entered");
			HttpSession session = request.getSession(false);
			RPActionForm recovryform = (RPActionForm) form;
			try{
			User user = getUserInformation(request);
			String bankId = user.getBankId();
			String zoneId = user.getZoneId();
			String branchId = user.getBranchId();
			String loginID  = bankId + zoneId + branchId;		
	        String memberBank="''";
			String payID="";	      
		    java.util.Date toDate=recovryform.getDateOfTheDocument24();
	        java.util.Date fromDate=recovryform.getDateOfTheDocument25();        
	        memberBank = recovryform.getBankName();   
	        
	        String fetchRecordType = "";
	        if(recovryform.getBankName()!=null || recovryform.getBankName()!="''"){
	        	  memberBank = recovryform.getBankName();   
	            fetchRecordType="ALLRECORD";
	        }        
	        if(request.getParameter("payID")!=null || request.getParameter("payID")!=""){
	        payID= request.getParameter("payID");
	        fetchRecordType="PAYIDRECORD";
	        }	
	        
	       
	        ArrayList formRecvryList =  displayRecoveryList(memberBank,loginID,payID,toDate,fromDate,fetchRecordType); 
	        
	        if(formRecvryList.size()>0){
			recovryform.setRecvryPaymentList(formRecvryList);
	        }else{
	        	throw new MessageException("Unable to fetch Pay ID:'"+payID+"' 's record.");
	        }
			Log.log(5, "GMAction", "showRecoveryPaymentDetailsforRPNo", "Exited");
			}catch(Exception ex){
				throw new MessageException(ex.getMessage());
			}
			return mapping.findForward("npaRegistList");
		}
*/
		// CBD
		

				// CBD	All recovery approperiation detail 	
		/*public ArrayList displayRecoveryList(String memBank,String loginId,String payId,java.util.Date tDate, java.util.Date fmDate, String valueType)
		throws DatabaseException {
	             Log.log(5, "rpACTION", "displayNpaRegistrationFormList", "Entered");   
	            ArrayList recoveryPayList = new ArrayList();
	            Connection connection = DBConnection.getConnection(false);
	            RPActionForm rpActionForm = null;
	            ResultSet rs = null;
	            Statement stmt = null;   
	            String fDate = "";
	    		String toDate = "";    		
	            String memberBank="''";
	       		DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");       		
		       		if(tDate!=null){
		       		    toDate = formatter.format(tDate.getTime());
		       		}
		       		 	if(fmDate!=null){
		       		   fDate = formatter.format(fmDate.getTime());
		       		}
	       		 	if(memBank!=null){
	       		 	memberBank = memBank;
	       		 	}                
	       		 String npaRegistQuery ="";
	       		 try {
	       		 	if(valueType.equals("ALLRECORD")){       		 	
	       		        npaRegistQuery =	" select distinct RE.PAY_ID,CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID AS MLIID,  NVL (PAY_INSTRUMENT_DT, PAY_PAYMENT_DT) AS PAY_INSTRUMENT_DT, NVL(o.VIRTUAL_ACCOUNT_NO, 0) INSTUMENT_NUMBER," +
	       		        		" AMOUNT,(SELECT MAX(V.RAFC_ID) FROM RECOVRY_AFTER_BEFORE_FST_CLAIM@REPUSER V WHERE V.PAY_ID = RE.PAY_ID GROUP BY PAY_ID) AS RAFC_ID  FROM MEMBER_INFO M,CLAIM_TC_DETAIL TC,CLAIM_DETAIL CL,RECOVRY_AFTER_BEFORE_FST_CLAIM@repuser RE,PAYMENT_dETAIL_TEMP PM, SSI_dETAIL S, APPLICATION_DETAIL AP,ONLINE_PAYMENT_DETAIL@repuser o" +
	       		        		" where re.pay_id=o.pay_id AND CLM_STATUS = 'AP'  AND RECOVERY_UPDATION_FLAG = 'RP'  AND re.cgpan LIKE '%TC' AND RECOVERY_FLAG = 'AC' AND CL.CLM_REF_NO = TC.CLM_REF_NO  AND RE.CLM_REF_NO = CL.CLM_REF_NO" +
	       		        		"  AND PM.PAY_ID = RE.PAY_ID AND CL.BID = S.BID AND TC.CGPAN = AP.CGPAN AND RE.PAY_ID = o.PAY_ID AND (M.MEM_BANK_NAME LIKE '%"+memberBank+"%' AND (PAY_INSTRUMENT_DT BETWEEN TO_DATE ('"+toDate+"','dd-MON-yyyy') AND TO_DATE ('"+fDate+"','dd-MON-yyyy'))) UNION ALL" +
	       		        		" select distinct RE.PAY_ID,CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID AS MLIID,  NVL (PAY_INSTRUMENT_DT, PAY_PAYMENT_DT) AS PAY_INSTRUMENT_DT, NVL(o.VIRTUAL_ACCOUNT_NO, 0) INSTUMENT_NUMBER," +
	       		        		"  AMOUNT,(SELECT MAX(V.RAFC_ID) FROM RECOVRY_AFTER_BEFORE_FST_CLAIM@REPUSER V WHERE V.PAY_ID = RE.PAY_ID GROUP BY PAY_ID) AS RAFC_ID  FROM MEMBER_INFO M,CLAIM_WC_DETAIL TC,CLAIM_DETAIL CL,RECOVRY_AFTER_BEFORE_FST_CLAIM@repuser RE,PAYMENT_dETAIL_TEMP PM,SSI_dETAIL S,APPLICATION_DETAIL AP, ONLINE_PAYMENT_DETAIL@repuser o" +
	       		        		"  where re.pay_id=o.pay_id AND CLM_STATUS = 'AP'  AND RECOVERY_UPDATION_FLAG = 'RP'  AND re.cgpan LIKE '%TC' AND RECOVERY_FLAG = 'AC' AND CL.CLM_REF_NO = TC.CLM_REF_NO  AND RE.CLM_REF_NO = CL.CLM_REF_NO" +
	       		        		"  AND PM.PAY_ID = RE.PAY_ID AND CL.BID = S.BID AND TC.CGPAN = AP.CGPAN AND RE.PAY_ID = o.PAY_ID AND (M.MEM_BANK_NAME LIKE '%"+memberBank+"%' AND (PAY_INSTRUMENT_DT BETWEEN TO_DATE ('"+toDate+"','dd-MON-yyyy') AND TO_DATE ('"+fDate+"','dd-MON-yyyy')))";
	       		     System.out.println(">>>>Approperiation Query>>>>ALLRECORD>>>>1>>>>>>>>>>>>>"+npaRegistQuery);       
			     	        stmt = connection.createStatement();
			     	        rs = stmt.executeQuery((npaRegistQuery));
			     		  while (rs.next()) 
			     	    {
			     		 rpActionForm = new RPActionForm();
			     		 rpActionForm.setPayId(rs.getString("PAY_ID"));//----
			     		 rpActionForm.setMemberId(rs.getString("MLIID"));
			     		 rpActionForm.setInstrumentDate(rs.getDate("PAY_INSTRUMENT_DT"));//--
			     		 rpActionForm.setInstrumentNo(rs.getString("INSTUMENT_NUMBER")); 
			     		 rpActionForm.setInstrumentAmount(rs.getDouble("AMOUNT"));
			     		 rpActionForm.setCheckerId(rs.getInt("RAFC_ID"));
			     		 rpActionForm.setGuaranteeAppAmt(rs.getDouble("AMOUNT"));		     		
			     		 rpActionForm.setAmountRemitedtoCgtmse(rs.getDouble("AMOUNT"));	     		
			     		 rpActionForm.setReceivedAmount(rpActionForm.getAmountRemitedtoCgtmse()); 
			     		 rpActionForm.setDateOfRealisation(rpActionForm.getInstrumentDate());  
			     		 //================till here================//
			              recoveryPayList.add(rpActionForm);	             
			             // PM.VIRTUAL_ACCOUNT_NO as instument number		              
			     	   } 		     		 
	       		}else if(valueType.equals("PAYIDRECORD") && (!payId.equals("") || payId!=null)){  
	       		 npaRegistQuery =	"SELECT DISTINCT CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID AS MLIID, MEM_ZONE_NAME,  RE.CGPAN, "
	    	      	 		+ " DECODE (APP_REAPPROVE_AMOUNT, NULL, APP_APPROVED_AMOUNT,  APP_REAPPROVE_AMOUNT) AS ApproveAmt, SSI_UNIT_NAME,  CTD_TC_FIRST_INST_PAY_AMT,  "
	    	      	 		+ " AMOUNT_REMITTED_TO_CGTMSE, (SELECT RMD_DESCRIPTION FROM RECOVERY_MODE_MASTER RM  WHERE RM.RMD_ID = RE.TYPE_OF_RECOVERY)  REC_TYPE,"
	    	      	 		+ " RE.PAY_ID, NVL(PAY_INSTRUMENT_DT, PAY_PAYMENT_DT) AS PAY_INSTRUMENT_DT, RAFC_ID, M.MEM_BANK_NAME, NVL(o.VIRTUAL_ACCOUNT_NO,0) INSTUMENT_NUMBER FROM MEMBER_INFO M,  CLAIM_TC_DETAIL TC, CLAIM_DETAIL CL,"
	    	      	 		+ " RECOVRY_AFTER_BEFORE_FST_CLAIM@repuser RE, PAYMENT_dETAIL_TEMP PM, SSI_dETAIL S,  APPLICATION_DETAIL AP, ONLINE_PAYMENT_DETAIL@repuser o  WHERE  M.MEM_BNK_ID || M.MEM_ZNE_ID"
	    	      	 		+ " || M.MEM_BRN_ID = CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID  AND CLM_STATUS = 'AP'  AND RECOVERY_UPDATION_FLAG = 'RP' AND re.cgpan LIKE '%TC' "
	    	      	 		+ " AND RECOVERY_FLAG = 'AC'  AND CL.CLM_REF_NO = TC.CLM_REF_NO AND RE.CLM_REF_NO = CL.CLM_REF_NO AND PM.PAY_ID = RE.PAY_ID AND CL.BID = S.BID  "
	    	      	 		+ " AND TC.CGPAN = AP.CGPAN AND RE.PAY_ID = o.PAY_ID AND RE.PAY_ID=nvl('"+payId+"',0) UNION ALL  SELECT DISTINCT CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID, MEM_ZONE_NAME, RE.CGPAN, DECODE (APP_REAPPROVE_AMOUNT, "
	    	      	 		+ " NULL, APP_APPROVED_AMOUNT, APP_REAPPROVE_AMOUNT),  SSI_UNIT_NAME, CWD_WC_FIRST_INST_PAY_AMT, AMOUNT_REMITTED_TO_CGTMSE, (SELECT RMD_DESCRIPTION FROM RECOVERY_MODE_MASTER RM"
	    	      	 		+ " WHERE RM.RMD_ID = RE.TYPE_OF_RECOVERY) REC_TYPE,  RE.PAY_ID, NVL (PAY_INSTRUMENT_DT, PAY_PAYMENT_DT) AS PAY_INSTRUMENT_DT,  RAFC_ID,M.MEM_BANK_NAME, NVL(o.VIRTUAL_ACCOUNT_NO,0) INSTUMENT_NUMBER FROM MEMBER_INFO M, CLAIM_WC_DETAIL TC, "
	    	      	 		+ " CLAIM_DETAIL CL, RECOVRY_AFTER_BEFORE_FST_CLAIM@repuser RE, PAYMENT_dETAIL_TEMP PM,  SSI_dETAIL S, APPLICATION_DETAIL AP,ONLINE_PAYMENT_DETAIL@repuser o WHERE  M.MEM_BNK_ID || M.MEM_ZNE_ID || M.MEM_BRN_ID = "
	    	      	 		+ "  CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID  AND CLM_STATUS = 'AP' AND RECOVERY_UPDATION_FLAG = 'RP'  AND re.cgpan LIKE '%WC'  AND RECOVERY_FLAG = 'AC' AND CL.CLM_REF_NO ="
	    	      	 		+ " TC.CLM_REF_NO  AND RE.CLM_REF_NO = CL.CLM_REF_NO  AND PM.PAY_ID = RE.PAY_ID  AND CL.BID = S.BID  AND TC.CGPAN = AP.CGPAN AND RE.PAY_ID = o.PAY_ID AND RE.PAY_ID=nvl('"+payId+"',0)";    	      
	     
	       		 System.out.println(payId+ ">>>>Approperiation Query>>>>PAYIDRECORD>>>>2>>>>>>>>>>>>>"+npaRegistQuery);       
		        stmt = connection.createStatement();
		        rs = stmt.executeQuery((npaRegistQuery));
				  while (rs.next()) 
			    {
				 rpActionForm = new RPActionForm();
				 rpActionForm.setMemberId(rs.getString("MLIID"));
				 rpActionForm.setZoneName(rs.getString("MEM_ZONE_NAME"));
				 rpActionForm.setCgpan(rs.getString("CGPAN"));
				 rpActionForm.setGuaranteeAppAmt(rs.getDouble("ApproveAmt"));
				 rpActionForm.setUnitName(rs.getString("SSI_UNIT_NAME"));		
				 rpActionForm.setFirstClaimSetAmt(rs.getDouble("CTD_TC_FIRST_INST_PAY_AMT"));
				 rpActionForm.setAmountRemitedtoCgtmse(rs.getDouble("AMOUNT_REMITTED_TO_CGTMSE"));
				 rpActionForm.setRecoveryType(rs.getString("REC_TYPE"));
				 rpActionForm.setPayId(rs.getString("PAY_ID"));//----
				 rpActionForm.setInstrumentDate(rs.getDate("PAY_INSTRUMENT_DT"));//--
				 rpActionForm.setCheckerId(rs.getInt("RAFC_ID"));		 
				 rpActionForm.setBankName(rs.getString("MEM_BANK_NAME"));           // Bank Name
				 rpActionForm.setInstrumentNo(rs.getString("INSTUMENT_NUMBER"));                //PM.VIRTUAL_ACCOUNT_NO
				 rpActionForm.setAmount(rpActionForm.getGuaranteeAppAmt());//rs.getDouble(4));              // allocated amount
				 rpActionForm.setInstrumentAmount(rpActionForm.getAmountRemitedtoCgtmse());  //rs.getDouble(7));   //  setAmountRemitedtoCgtmse
				 rpActionForm.setReceivedAmount(rpActionForm.getAmountRemitedtoCgtmse()); //rs.getDouble(4));      // receivedAmt				
				 rpActionForm.setDateOfRealisation(rpActionForm.getInstrumentDate());  //rs.getDate(10));    // InstrumentDate(rs.getDate(10));		 
				 rpActionForm.setReceivedAmount(0);//rpActionForm.getGuaranteeAppAmt());//rs.getDouble(4));    //   short/excess amount
				 //================till here================//
		         recoveryPayList.add(rpActionForm);
		        // PM.VIRTUAL_ACCOUNT_NO as instument number
		      }			  
		   }       
			   
	       }catch (Exception sql) {
		    sql.printStackTrace();
	      } finally {
		   DBConnection.freeConnection(connection);
		   try {
			stmt.close();
			 rs.close();	
		  } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		   }
		   
	      }
	        Log.log(5, "GMAction", "displayNpaRegistrationFormList", "Exited");
	        return recoveryPayList;
	     }*/

	
		public ActionForward generateClaimASFDANForGSTCheck(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	    {
	        Log.log(4, "RpAction", "getGSTNO", "Entered");
	        try
	        {
	            PrintWriter out = response.getWriter();
	            String cgpan = request.getParameter("cgpan");
	            boolean gstNo = getGstNoForASFDAN(cgpan);
	            out.print(gstNo);
	        }
	        catch(Exception e)
	        {
	            System.err.println((new StringBuilder("Exception in RpAction...")).append(e).toString());
	        }
	        return null;
	    }

	    private boolean getGstNoForASFDAN(String cgpan)
	    {
	        Connection connection = DBConnection.getConnection();
	        boolean gstCheckFlag = false;
	        Log.log(4, "CPDAO", "getGSTStateAndNo", "Entered");
	        PreparedStatement pStmt = null;
	        ResultSet rsSet = null;
	        try
	        {
	            String query = "select state_code,gst_no from application_detail where CGPAN=?";
	            pStmt = connection.prepareStatement(query);
	            pStmt.setString(1, cgpan);
	            rsSet = pStmt.executeQuery();
	            if(rsSet.next() && rsSet.getString(1) != null && rsSet.getString(2) != null)
	                gstCheckFlag = true;
	            rsSet.close();
	            pStmt.close();
	        }
	        catch(Exception exception)
	        {
	            Log.logException(exception);
	            try
	            {
	                throw new DatabaseException((new StringBuilder("Exception in getGSTStateAndNo()")).append(exception.getMessage()).toString());
	            }
	            catch(DatabaseException e)
	            {
	                e.printStackTrace();
	            }
	        }
	        finally
	        {
	            DBConnection.freeConnection(connection);
	            return gstCheckFlag;
	        }
	    }
	    
	    //==========================RK+++++++++++++++++++++++++++===============================================
	 // display recovery approperiated rp detail CBD
		public ActionForward showRecoveryPaymentDetailsforRPNo(ActionMapping mapping, ActionForm form,
				HttpServletRequest request, HttpServletResponse response)
				throws Exception {		
			System.out.println(" showRecoveryPaymentDetailsforRPNo ");
			Log.log(5, "GMAction", "showRecoveryPaymentDetailsforRPNo", "Entered");
			HttpSession session = request.getSession(false);
			RPActionForm recovryform = (RPActionForm) form;
			try{
			User user = getUserInformation(request);
			String bankId = user.getBankId();
			String zoneId = user.getZoneId();
			String branchId = user.getBranchId();
			String loginID  = bankId + zoneId + branchId;		
	        String memberBank="''";
			String payID="";	      
		    java.util.Date toDate=recovryform.getDateOfTheDocument24();
	        java.util.Date fromDate=recovryform.getDateOfTheDocument25();        
	        memberBank = recovryform.getBankName();   
	        
	        String fetchRecordType = "";
	        if(recovryform.getBankName()!=null || recovryform.getBankName()!="''"){
	        	  memberBank = recovryform.getBankName();   
	            fetchRecordType="ALLRECORD";
	        }  
	        
	        if(request.getParameter("payID")!=null || request.getParameter("payID")!=""){
	        payID= request.getParameter("payID");
	        fetchRecordType="PAYIDRECORD";
	        }	
	        
	       
	        ArrayList formRecvryList =  displayRecoveryList(memberBank,payID,toDate,fromDate,fetchRecordType); 
	        
	        if(formRecvryList.size()>0){
			recovryform.setRecvryPaymentList(formRecvryList);
	        }else{
	        	throw new MessageException("Unable to fetch Pay ID:'"+payID+"' 's record.");
	        }
			Log.log(5, "GMAction", "showRecoveryPaymentDetailsforRPNo", "Exited");
			}catch(Exception ex){
				throw new MessageException(ex.getMessage());
			}
			return mapping.findForward("npaRegistList");
		}

		// DKR submitClmRecovryAppropriation
		/*public ActionForward submitClmRecovryAppropriation(ActionMapping mapping,
				ActionForm form, HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			Log.log(5, "GMAction", "showApprRegistrationFormSubmit", "Entered");
			HttpSession session = request.getSession(false);
			// System.out.println("===8778===");
			Connection connection = DBConnection.getConnection(false);
			
			RPActionForm actionForm = (RPActionForm) form;
			User user = getUserInformation(request);
			// int checkId[];
			String bankId = user.getBankId();
			String zoneId = user.getZoneId();
			String branchId = user.getBranchId();
			String memberId = bankId + zoneId + branchId;
			String actionType = request.getParameter("action");
			try {
					int[] checkId = actionForm.getCheck();
					String [] checkIdRemark =  actionForm.getRemark();
					System.out.println(" submitClmRecovryAppropriation()>>>>>>appropriated>>>>>>>>>checkbox size checked>>>>>>>>>>>==="+checkId.length);
					int chSelectId = 0;
					String commVal = "";
					String query = "";
					AdminHelper adminHelper = new AdminHelper();		
					 boolean mailssend=false;
					 CallableStatement callable;		
						for(int i = 0; i < checkId.length; i++)
					  {
					 	 chSelectId = checkId[i];
					     commVal  = checkIdRemark[i];
					     System.out.println(i+" ===========checkId[i]  >>>>>>>>>>>>>>>>> "+chSelectId+"  >>>>checkIdRemark[i] "+commVal);
					 	// if(chSelectId!=0) {				
					 	  callable = connection.prepareCall("{?=call FUNCCLMRECOVERYAPPROPRIATION_d(?,?,?,?)}");
							callable.registerOutParameter(1, Types.INTEGER);					
							callable.setInt(2, chSelectId);						
							callable.setString(3, user.getUserId());
							callable.registerOutParameter(4, Types.VARCHAR);
							callable.setString(5, commVal);
							callable.execute();
							int errorCode = callable.getInt(1);
							String error = callable.getString(4);		
							if (errorCode == Constants.FUNCTION_FAILURE) {
								Log.log(Log.ERROR, "RPAction", "submitClmRecovryAppropriation", error); 
	                            System.out.println("errorCode>>>>>>>>RPAction.submitClmRecovryAppropriation()>>>>>>>>>>>>>>>"+errorCode);
								callable.close();
								//connection.rollback();
								throw new DatabaseException(error);
							}
					        connection.commit();
					 	// }
			         }
			  }// end try
			  catch (Exception sql) {
				connection.rollback();
				throw new DatabaseException("A Problem occured While performing Updation / Deletion Action "+ sql.getMessage());
			} finally {				
				DBConnection.freeConnection(connection);
			}
			request.setAttribute("message","<b>Selected Claim Recovery has been successfully Appropriated.</b>");
			Log.log(5, "rPAction", "showApprRegistrationFormSubmit", "Exited");
			return mapping.findForward("success");
		  }*/
		 public ActionForward submitClmRecovryAppropriation(ActionMapping mapping,
					ActionForm form, HttpServletRequest request,
					HttpServletResponse response) throws Exception {
				Log.log(5, "RPAction", "submitClmRecovryAppropriation", "Entered");
				HttpSession session = request.getSession(false);		
				Connection connection = DBConnection.getConnection(false);
				
				ArrayList<String> errDrList = new  ArrayList<String>();
				RPActionForm actionForm = (RPActionForm) form;
				User user = getUserInformation(request);
				CallableStatement callable=null;
				String bankId = user.getBankId();
				String zoneId = user.getZoneId();
				String branchId = user.getBranchId();
				String memberId = bankId + zoneId + branchId;
				String actionType = request.getParameter("action");
				// DEEPAK
			try {			
				int[] checkId = actionForm.getCheck();
				String [] checkIdRemark =  actionForm.getRemark();
		        if(checkId.length > 0) {
				String query = "";
				AdminHelper adminHelper = new AdminHelper();		
				 boolean mailssend=false;			 
				 for(int i = 0; i < checkId.length; i++)
				  {				
						 	 callable = connection.prepareCall("{?=call FUNCCLMRECOVERYAPPROPRIATION_d(?,?,?,?)}");
								callable.registerOutParameter(1, Types.INTEGER);					
								callable.setInt(2, checkId[i]);						
								callable.setString(3, user.getUserId());
								callable.registerOutParameter(4, Types.VARCHAR);
								callable.setString(5, checkIdRemark[i]);
								callable.execute();
								int errorCode = callable.getInt(1);
								String error = callable.getString(4);							
								if (errorCode == Constants.FUNCTION_FAILURE) {
									errDrList.add((String.valueOf(errorCode)).concat(error.toString())+"\n");								
								}
				         }
					    if(errDrList.size()== 0) {
					    	request.setAttribute("message","Selected Claim Recovery has been successfully Appropriated.");
					    }else {
					    	 request.setAttribute("message","Error in approperation <FUNCCLMRECOVERYAPPROPRIATION_d>:"+errDrList.toString());
		            	 }
		           }
		        }// end try
				  catch (Exception sql) {
					connection.rollback();
					throw new DatabaseException("A Problem occured While Performing Updation"+ sql.getMessage());
				  } finally {
					try {
						if (callable != null) {
							callable.close();
							callable = null;
						}
					} catch (Exception ss) {
						ss.printStackTrace();
					}
					DBConnection.freeConnection(connection);
				}
			Log.log(5, "rPAction", "showApprRegistrationFormSubmit", "Exited");
				return mapping.findForward("success");
			  } 
		public ActionForward getPaymentMadeForRecoveryInput(ActionMapping mapping,
				ActionForm form, HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			HttpSession session = request.getSession(false);
			ReportManager manager = new ReportManager();
			Log.log(Log.INFO,"RPAction","getPaymentMadeForRecoveryInput","Entered");     
	        ReportDAO reportDao = new ReportDAO();
	        ArrayList mli = new ArrayList();
	        DynaActionForm dynaForm = (DynaActionForm)form;
	        mli = reportDao.recoveryAppListMliWise();
	        dynaForm.set("mli",mli);
	        String forward = "";     
	        if(mli == null || mli.size()==0)
	        {
	          Log.log(Log.INFO,"ReportsAction","getPaymentMadeForRecoveryInput","Exited");
	          request.setAttribute("message","Recovery applications are not available for Approval");
	          forward = "success";         
	        }
	        else
	        {
	          mli = null;
	          Log.log(Log.INFO,"rpACTION","getPaymentMadeForRecoveryInput","Exited");
	          forward = "approvalRecoveryList";
	        }
	        System.out.println("forward........."+forward);
	        return mapping.findForward(forward);
		}
		
		/*public ActionForward showRecoveryPaymentDetailsforAppropriation(ActionMapping mapping, ActionForm form,
				HttpServletRequest request, HttpServletResponse response)
				throws Exception {
			
			System.out.println(" showRecoveryPaymentDetailsforAppropriation~!~~~~~~~~~~~~~~~~~");
			Log.log(5, "rpACTION", "showRecoveryPaymentDetailsforAppropriation", "Entered");
			HttpSession session = request.getSession(false);
			RPActionForm recovryform = (RPActionForm) form;
			User user = getUserInformation(request);
			String bankId = user.getBankId();
			String zoneId = user.getZoneId();
			String branchId = user.getBranchId();
			String loginID  = bankId + zoneId + branchId;	
			ArrayList formRecvryList=null;
			String memberBank="'0000'";
			String payIDs="";
			String forward = "";
			Date toDate=null;
		    Date fromDate=null;
		    String fetchRecordType = "";
		        
		     memberBank = recovryform.getBankName();
		     payIDs= request.getParameter("payID");
		     
		     System.out.println(recovryform.getDateOfTheDocument24() +"===<<=====getDateOfTheDocument24====111111111111====getDateOfTheDocument25>>>>>>>>>>>>>>>"+recovryform.getDateOfTheDocument25());
		     
		     if(recovryform.getDateOfTheDocument24().toString().length()>0 && recovryform.getDateOfTheDocument25().toString().length()>0 ){
		        	toDate= recovryform.getDateOfTheDocument24();
			        fromDate=recovryform.getDateOfTheDocument25();
	               fetchRecordType="ALLRECORD_DATE";		      
		        }else 
                    if(null!=memberBank || memberBank.length()>0){
		        	  memberBank = recovryform.getBankName();   
		            fetchRecordType="ALLRECORD_BANK_NAME";
		        }else		        
		          if(null!=payIDs || payIDs.length()>0){	       
		           fetchRecordType="PAYIDRECORD";
		          }	
		          		        
		        if((memberBank.length()>0) || (recovryform.getDateOfTheDocument24().toString().length()>0 && recovryform.getDateOfTheDocument25().toString().length()>0) || (payIDs.length()>0)){
		       //   System.out.println(fetchRecordType+"========fetchRecordType========recovryform.getDateOfTheDocument25().toString().length()>>>>"+recovryform.getDateOfTheDocument25().toString().length()+">>>>>>>2222222222222>>>>>>>>>>"+memberBank+"toDate>>>>>>>>>>>>>>>>>>>>>"+toDate.toString().length()+"fromDate>>>>>>>>>>>>>>>>>>>>>"+fromDate.toString().length()+"payID>>>>>>>>>>>>>>>>>>>>>"+payIDs.toString().length());
						
		        	
		           formRecvryList =  displayRecoveryList(memberBank,payIDs,toDate,fromDate,fetchRecordType); 
		        }else{
		        	request.setAttribute("message",	"Please select Bank Name or From Date and To Date For approperiation.");
					forward = "success";
		        }
			
		        if (formRecvryList == null || formRecvryList.size() == 0) {
				request.setAttribute("message",	"No recovery are available For approperiation.");
				forward = "success";
			}else {
				recovryform.setRecvryPaymentList(formRecvryList);
				forward = "npaRegistList";
			}

			Log.log(5, "rpACTION", "showRecoveryPaymentDetailsforAppropriation", "Exited");
			return mapping.findForward(forward);
		}*/
			
		public ActionForward showRecoveryPaymentDetailsforAppropriation(ActionMapping mapping, ActionForm form,
				HttpServletRequest request, HttpServletResponse response)
				throws Exception {
			
			System.out.println(" showRecoveryPaymentDetailsforAppropriation~!~~~~~~~~~~~~~~~~~");
			Log.log(5, "rpACTION", "showRecoveryPaymentDetailsforAppropriation", "Entered");
			HttpSession session = request.getSession(false);
			RPActionForm recovryform = (RPActionForm) form;			
			ArrayList formRecvryList=null;
			String memberBank="'0'";
			String payIDs="";
			String forward = "";
			Date toDate=null;
		    Date fromDate=null;
		    String fetchRecordType = "";
		        
		     memberBank = recovryform.getBankName();
		     payIDs= request.getParameter("payID");
		     
	    System.out.println(recovryform.getDateOfTheDocument24().toString() +"===<<="+recovryform.getDateOfTheDocument24().toString().length()+"====getDateOfTheDocument24====47454747457457===getDateOfTheDocument25>>>>>"+recovryform.getDateOfTheDocument25().toString().length()+"=>>>>>>>>>>"+recovryform.getDateOfTheDocument25().toString());
		  
		if ((recovryform.getDateOfTheDocument24().toString().length() > 0 && recovryform
				.getDateOfTheDocument25().toString().length() > 0)
				&& (memberBank.length() == 0 || memberBank.equals("''"))) {

			toDate = recovryform.getDateOfTheDocument24();
			fromDate = recovryform.getDateOfTheDocument25();
			fetchRecordType = "ALLRECORD_DATE";
			System.out
					.println(toDate
							+ "toDate  DKR>>>>1>>>> date wise >>>>>fetchRecordType>>>>>"
							+ fetchRecordType);
		}else if ((memberBank.length() > 0)
				&& (recovryform.getDateOfTheDocument24().toString().length() == 0 && recovryform
						.getDateOfTheDocument25().toString().length() == 0)) {

			memberBank = recovryform.getBankName();
			fetchRecordType = "ALLRECORD_BANK_NAME";
			System.out
					.println(memberBank
							+ "memberBank    DKR>>>>2>>>>> Bank wise >>>>>fetchRecordType>>>>>"
							+ fetchRecordType);
		}
		else if ((memberBank.length() > 0 && !memberBank.equals("''"))
				&& (recovryform.getDateOfTheDocument24().toString().length() > 0 && recovryform
						.getDateOfTheDocument25().toString().length() > 0)) {

			memberBank = recovryform.getBankName();
			toDate = recovryform.getDateOfTheDocument24();
			fromDate = recovryform.getDateOfTheDocument25();
			fetchRecordType = "BANK_AND_DATE_RECORD";
			System.out.println(memberBank+ "memberBank    DKR>>>>3>>>>> Bank wise >>>>>fetchRecordType>>>>>"+ fetchRecordType);
           }
		    
		    if(null!=payIDs && payIDs.length()>0 && payIDs.equals("")){	       
		           fetchRecordType="PAYIDRECORD";
		     }	
		     
		     System.out.println("DKR>>>>>>>>>>>>>>fetchRecordType>>>>>"+fetchRecordType);
		          		        
		        if((memberBank.length()>0) || (recovryform.getDateOfTheDocument24().toString().length()>0 && recovryform.getDateOfTheDocument25().toString().length()>0) || (payIDs.length()>0)){
		             formRecvryList =  displayRecoveryList(memberBank,payIDs,toDate,fromDate,fetchRecordType); 
		        }else{
		        	request.setAttribute("message",	"Please select Bank Name or From Date and To Date For approperiation.");
					forward = "success";
					
		        }
			
		        if (formRecvryList == null || formRecvryList.size() == 0) {
				request.setAttribute("message",	"No recovery are available For approperiation.");
				forward = "success";
				
			}else {
				recovryform.setRecvryPaymentList(formRecvryList);
				forward = "npaRegistList";
				fetchRecordType="''";
			}

			Log.log(5, "rpACTION", "showRecoveryPaymentDetailsforAppropriation", "Exited");
			return mapping.findForward(forward);
		}
			// CBD	All recovery approperiation detail 	
		
	/*	public ArrayList displayRecoveryList(String memBank,String payId,java.util.Date tDate, java.util.Date fmDate, String valueType)
		throws DatabaseException {
	             Log.log(5, "rpACTION", "displayNpaRegistrationFormList", "Entered");   
	            ArrayList recoveryPayList = new ArrayList();
	            Connection connection = DBConnection.getConnection(false);
	            RPActionForm rpActionForm = null;
	            ResultSet rs = null,rsd = null;
	            Statement stmt = null,stmtd = null;   
	            String fDate = "";
	    		String toDate = "";    		
	            String memberBank="''";
	            String bank_id="0000";
	            String npaRegistQuery ="";
	       		DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");         		 
	       		
	       		 try {
	       			 
	       		 	if((valueType.equals("ALLRECORD_DATE") || valueType.equals("ALLRECORD_BANK_NAME"))){ 
					     		 if(valueType.equals("ALLRECORD_DATE")){
					     			 toDate = formatter.format(tDate.getTime());
						       		 fDate = formatter.format(fmDate.getTime());			       		
					     		
						       		npaRegistQuery  = " select distinct RE.PAY_ID,CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID AS MLIID,  PAY_PAYMENT_DT AS PAY_INSTRUMENT_DT, NVL(o.VIRTUAL_ACCOUNT_NO, 0) INSTUMENT_NUMBER," +
				       		        		" AMOUNT,(SELECT MAX(V.RAFC_ID) FROM RECOVRY_AFTER_BEFORE_FST_CLAIM V WHERE V.PAY_ID = RE.PAY_ID GROUP BY PAY_ID) AS RAFC_ID  FROM MEMBER_INFO M,CLAIM_TC_DETAIL TC,CLAIM_DETAIL CL,RECOVRY_AFTER_BEFORE_FST_CLAIM RE,PAYMENT_dETAIL_TEMP PM, SSI_dETAIL S, APPLICATION_DETAIL AP,ONLINE_PAYMENT_DETAIL@repuser o" +
				       		        		" where re.pay_id=o.pay_id AND CLM_STATUS = 'AP'  AND RECOVERY_UPDATION_FLAG = 'RP'  AND re.cgpan LIKE '%TC' AND RECOVERY_FLAG = 'AC' and DAN_TYPE='RV' and PAYMENT_STATUS='R' AND CL.CLM_REF_NO = TC.CLM_REF_NO  AND RE.CLM_REF_NO = CL.CLM_REF_NO" +
				       		        		"  AND PM.PAY_ID = RE.PAY_ID AND CL.BID = S.BID AND TC.CGPAN = AP.CGPAN AND RE.PAY_ID = o.PAY_ID and (PAY_PAYMENT_DT BETWEEN TO_DATE ('"+toDate+"','dd-MON-yyyy') AND TO_DATE ('"+fDate+"','dd-MON-yyyy')) UNION ALL" +
				       		        		" select distinct RE.PAY_ID,CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID AS MLIID,  PAY_PAYMENT_DT AS PAY_INSTRUMENT_DT, NVL(o.VIRTUAL_ACCOUNT_NO, 0) INSTUMENT_NUMBER," +
				       		        		"  AMOUNT,(SELECT MAX(V.RAFC_ID) FROM RECOVRY_AFTER_BEFORE_FST_CLAIM V WHERE V.PAY_ID = RE.PAY_ID GROUP BY PAY_ID) AS RAFC_ID  FROM MEMBER_INFO M,CLAIM_WC_DETAIL TC,CLAIM_DETAIL CL,RECOVRY_AFTER_BEFORE_FST_CLAIM RE,PAYMENT_dETAIL_TEMP PM,SSI_dETAIL S,APPLICATION_DETAIL AP, ONLINE_PAYMENT_DETAIL@repuser o" +
				       		        		"  where re.pay_id=o.pay_id AND CLM_STATUS = 'AP'  AND RECOVERY_UPDATION_FLAG = 'RP'  AND re.cgpan LIKE '%TC' AND RECOVERY_FLAG = 'AC' and DAN_TYPE='RV' and PAYMENT_STATUS='R' AND CL.CLM_REF_NO = TC.CLM_REF_NO  AND RE.CLM_REF_NO = CL.CLM_REF_NO" +
				       		        		"  AND PM.PAY_ID = RE.PAY_ID AND CL.BID = S.BID AND TC.CGPAN = AP.CGPAN AND RE.PAY_ID = o.PAY_ID  AND (PAY_PAYMENT_DT BETWEEN TO_DATE ('"+toDate+"','dd-MON-yyyy') AND TO_DATE ('"+fDate+"','dd-MON-yyyy'))";
				       		     
						       		npaRegistQuery  = " select distinct RE.PAY_ID,CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID AS MLIID, NVL (PAY_INSTRUMENT_DT, PAY_PAYMENT_DT) AS PAY_INSTRUMENT_DT, NVL(o.VIRTUAL_ACCOUNT_NO, 0) INSTUMENT_NUMBER," +
				       		        		" AMOUNT,(SELECT MAX(V.RAFC_ID) FROM RECOVRY_AFTER_BEFORE_FST_CLAIM V WHERE V.PAY_ID = RE.PAY_ID GROUP BY PAY_ID) AS RAFC_ID  FROM MEMBER_INFO M,CLAIM_TC_DETAIL TC,CLAIM_DETAIL CL,RECOVRY_AFTER_BEFORE_FST_CLAIM RE,PAYMENT_dETAIL_TEMP PM, SSI_dETAIL S, APPLICATION_DETAIL AP,ONLINE_PAYMENT_DETAIL@repuser o" +
				       		        		" where re.pay_id=o.pay_id AND CLM_STATUS = 'AP'  AND RECOVERY_UPDATION_FLAG = 'RP'  AND re.cgpan LIKE '%TC' AND RECOVERY_FLAG = 'AC' and DAN_TYPE='RV' and PAYMENT_STATUS='R' AND CL.CLM_REF_NO = TC.CLM_REF_NO  AND RE.CLM_REF_NO = CL.CLM_REF_NO" +
				       		        		"  AND PM.PAY_ID = RE.PAY_ID AND CL.BID = S.BID AND TC.CGPAN = AP.CGPAN AND RE.PAY_ID = o.PAY_ID and (PAY_PAYMENT_DT BETWEEN TO_DATE ('"+toDate+"','dd-MON-yyyy') AND TO_DATE ('"+fDate+"','dd-MON-yyyy')) UNION ALL" +
				       		        		" select distinct RE.PAY_ID,CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID AS MLIID,  PAY_PAYMENT_DT AS PAY_INSTRUMENT_DT, NVL(o.VIRTUAL_ACCOUNT_NO, 0) INSTUMENT_NUMBER," +
				       		        		"  AMOUNT,(SELECT MAX(V.RAFC_ID) FROM RECOVRY_AFTER_BEFORE_FST_CLAIM V WHERE V.PAY_ID = RE.PAY_ID GROUP BY PAY_ID) AS RAFC_ID  FROM MEMBER_INFO M,CLAIM_WC_DETAIL TC,CLAIM_DETAIL CL,RECOVRY_AFTER_BEFORE_FST_CLAIM RE,PAYMENT_dETAIL_TEMP PM,SSI_dETAIL S,APPLICATION_DETAIL AP, ONLINE_PAYMENT_DETAIL@repuser o" +
				       		        		"  where re.pay_id=o.pay_id AND CLM_STATUS = 'AP'  AND RECOVERY_UPDATION_FLAG = 'RP'  AND re.cgpan LIKE '%TC' AND RECOVERY_FLAG = 'AC' and DAN_TYPE='RV' and PAYMENT_STATUS='R' AND CL.CLM_REF_NO = TC.CLM_REF_NO  AND RE.CLM_REF_NO = CL.CLM_REF_NO" +
				       		        		"  AND PM.PAY_ID = RE.PAY_ID AND CL.BID = S.BID AND TC.CGPAN = AP.CGPAN AND RE.PAY_ID = o.PAY_ID  AND (PAY_PAYMENT_DT BETWEEN TO_DATE ('"+toDate+"','dd-MON-yyyy') AND TO_DATE ('"+fDate+"','dd-MON-yyyy'))";
				       		     
					 				System.out.println("valueType================ Date wise =========1=================="+valueType);
					 				    stmt = connection.createStatement();
						     	        rs = stmt.executeQuery((npaRegistQuery));
					 			}else if(valueType.equals("ALLRECORD_BANK_NAME")){
					 				 memberBank = memBank;
					 	       		npaRegistQuery = " select distinct RE.PAY_ID,CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID AS MLIID,  NVL (PAY_INSTRUMENT_DT, PAY_PAYMENT_DT) AS PAY_INSTRUMENT_DT, NVL(o.VIRTUAL_ACCOUNT_NO, 0) INSTUMENT_NUMBER," +
				       		        		" AMOUNT,(SELECT MAX(V.RAFC_ID) FROM RECOVRY_AFTER_BEFORE_FST_CLAIM V WHERE V.PAY_ID = RE.PAY_ID GROUP BY PAY_ID) AS RAFC_ID  FROM MEMBER_INFO M,CLAIM_TC_DETAIL TC,CLAIM_DETAIL CL,RECOVRY_AFTER_BEFORE_FST_CLAIM RE,PAYMENT_dETAIL_TEMP PM, SSI_dETAIL S, APPLICATION_DETAIL AP,ONLINE_PAYMENT_DETAIL@repuser o" +
				       		        		" where re.pay_id=o.pay_id AND CLM_STATUS = 'AP'  AND RECOVERY_UPDATION_FLAG = 'RP'  AND re.cgpan LIKE '%TC' AND RECOVERY_FLAG = 'AC' and DAN_TYPE='RV' and PAYMENT_STATUS='R' AND CL.CLM_REF_NO = TC.CLM_REF_NO  AND RE.CLM_REF_NO = CL.CLM_REF_NO" +
				       		        		"  AND PM.PAY_ID = RE.PAY_ID AND CL.BID = S.BID AND TC.CGPAN = AP.CGPAN AND RE.PAY_ID = o.PAY_ID and AP.MEM_BNK_ID = (select distinct mem_bnk_id from member_info where MEM_BANK_NAME = '"+memberBank+"') UNION ALL" +
				       		        		" select distinct RE.PAY_ID,CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID AS MLIID,  NVL (PAY_INSTRUMENT_DT, PAY_PAYMENT_DT) AS PAY_INSTRUMENT_DT, NVL(o.VIRTUAL_ACCOUNT_NO, 0) INSTUMENT_NUMBER," +
				       		        		"  AMOUNT,(SELECT MAX(V.RAFC_ID) FROM RECOVRY_AFTER_BEFORE_FST_CLAIM V WHERE V.PAY_ID = RE.PAY_ID GROUP BY PAY_ID) AS RAFC_ID  FROM MEMBER_INFO M,CLAIM_WC_DETAIL TC,CLAIM_DETAIL CL,RECOVRY_AFTER_BEFORE_FST_CLAIM RE,PAYMENT_dETAIL_TEMP PM,SSI_dETAIL S,APPLICATION_DETAIL AP, ONLINE_PAYMENT_DETAIL@repuser o" +
				       		        		"  where re.pay_id=o.pay_id AND CLM_STATUS = 'AP'  AND RECOVERY_UPDATION_FLAG = 'RP'  AND re.cgpan LIKE '%TC' AND RECOVERY_FLAG = 'AC' and DAN_TYPE='RV' and PAYMENT_STATUS='R' AND CL.CLM_REF_NO = TC.CLM_REF_NO  AND RE.CLM_REF_NO = CL.CLM_REF_NO" +
				       		        		"  AND PM.PAY_ID = RE.PAY_ID AND CL.BID = S.BID AND TC.CGPAN = AP.CGPAN AND RE.PAY_ID = o.PAY_ID and AP.MEM_BNK_ID = (select distinct mem_bnk_id from member_info where MEM_BANK_NAME = '"+memberBank+"')";
					 				System.out.println("valueType============ bank Name ===========2==================="+valueType);
					 				 stmt = connection.createStatement();
						     	      rs = stmt.executeQuery((npaRegistQuery));
					 			 }
					     		  System.out.println(">>>>Approperiation Query>>>>ALLRECORD>>>> OUT >>>>>>>>>>>>"+npaRegistQuery);       
					     	      
					     		  while (rs.next()) 
					     	    {
					     		 rpActionForm = new RPActionForm();
					     		 rpActionForm.setPayId(rs.getString("PAY_ID"));//----
					     		 rpActionForm.setMemberId(rs.getString("MLIID"));
					     		 rpActionForm.setInstrumentDate(rs.getDate("PAY_INSTRUMENT_DT"));//--
					     		 rpActionForm.setInstrumentNo(rs.getString("INSTUMENT_NUMBER")); 
					     		 rpActionForm.setInstrumentAmount(rs.getDouble("AMOUNT"));
					     		 rpActionForm.setCheckerId(rs.getInt("RAFC_ID"));
					     		 rpActionForm.setGuaranteeAppAmt(rs.getDouble("AMOUNT"));		     		
					     		 rpActionForm.setAmountRemitedtoCgtmse(rs.getDouble("AMOUNT"));	     		
					     		 rpActionForm.setReceivedAmount(rpActionForm.getAmountRemitedtoCgtmse()); 
					     		 rpActionForm.setDateOfRealisation(rpActionForm.getInstrumentDate());  
					     		 //================till here================//
					              recoveryPayList.add(rpActionForm);  
					     	   }
		         }else if(valueType.equals("PAYIDRECORD")){  
	       		 npaRegistQuery =	"SELECT DISTINCT CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID AS MLIID, MEM_ZONE_NAME,  RE.CGPAN, "
	    	      	 		+ " DECODE (APP_REAPPROVE_AMOUNT, NULL, APP_APPROVED_AMOUNT,  APP_REAPPROVE_AMOUNT) AS ApproveAmt, SSI_UNIT_NAME,  CTD_TC_FIRST_INST_PAY_AMT,  "
	    	      	 		+ " AMOUNT_REMITTED_TO_CGTMSE, (SELECT RMD_DESCRIPTION FROM RECOVERY_MODE_MASTER RM  WHERE RM.RMD_ID = RE.TYPE_OF_RECOVERY)  REC_TYPE,"
	    	      	 		+ " RE.PAY_ID, NVL(PAY_INSTRUMENT_DT, PAY_PAYMENT_DT) AS PAY_INSTRUMENT_DT, RAFC_ID, M.MEM_BANK_NAME, NVL(o.VIRTUAL_ACCOUNT_NO,0) INSTUMENT_NUMBER FROM MEMBER_INFO M,  CLAIM_TC_DETAIL TC, CLAIM_DETAIL CL,"
	    	      	 		+ " RECOVRY_AFTER_BEFORE_FST_CLAIM RE, PAYMENT_dETAIL_TEMP PM, SSI_dETAIL S,  APPLICATION_DETAIL AP, ONLINE_PAYMENT_DETAIL@repuser o  WHERE  M.MEM_BNK_ID || M.MEM_ZNE_ID"
	    	      	 		+ " || M.MEM_BRN_ID = CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID  AND CLM_STATUS = 'AP'  AND RECOVERY_UPDATION_FLAG = 'RP' AND re.cgpan LIKE '%TC' "
	    	      	 		+ " AND RECOVERY_FLAG = 'AC'  AND CL.CLM_REF_NO = TC.CLM_REF_NO AND RE.CLM_REF_NO = CL.CLM_REF_NO AND PM.PAY_ID = RE.PAY_ID AND CL.BID = S.BID  "
	    	      	 		+ " AND TC.CGPAN = AP.CGPAN AND RE.PAY_ID = o.PAY_ID AND RE.PAY_ID=nvl('"+payId+"',0) UNION ALL  SELECT DISTINCT CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID, MEM_ZONE_NAME, RE.CGPAN, DECODE (APP_REAPPROVE_AMOUNT, "
	    	      	 		+ " NULL, APP_APPROVED_AMOUNT, APP_REAPPROVE_AMOUNT),  SSI_UNIT_NAME, CWD_WC_FIRST_INST_PAY_AMT, AMOUNT_REMITTED_TO_CGTMSE, (SELECT RMD_DESCRIPTION FROM RECOVERY_MODE_MASTER RM"
	    	      	 		+ " WHERE RM.RMD_ID = RE.TYPE_OF_RECOVERY) REC_TYPE,  RE.PAY_ID, NVL (PAY_INSTRUMENT_DT, PAY_PAYMENT_DT) AS PAY_INSTRUMENT_DT,  RAFC_ID,M.MEM_BANK_NAME, NVL(o.VIRTUAL_ACCOUNT_NO,0) INSTUMENT_NUMBER FROM MEMBER_INFO M, CLAIM_WC_DETAIL TC, "
	    	      	 		+ " CLAIM_DETAIL CL, RECOVRY_AFTER_BEFORE_FST_CLAIM RE, PAYMENT_dETAIL_TEMP PM,  SSI_dETAIL S, APPLICATION_DETAIL AP,ONLINE_PAYMENT_DETAIL@repuser o WHERE  M.MEM_BNK_ID || M.MEM_ZNE_ID || M.MEM_BRN_ID = "
	    	      	 		+ "  CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID  AND CLM_STATUS = 'AP' AND RECOVERY_UPDATION_FLAG = 'RP'  AND re.cgpan LIKE '%WC'  AND RECOVERY_FLAG = 'AC' AND CL.CLM_REF_NO ="
	    	      	 		+ " TC.CLM_REF_NO  AND RE.CLM_REF_NO = CL.CLM_REF_NO  AND PM.PAY_ID = RE.PAY_ID  AND CL.BID = S.BID  AND TC.CGPAN = AP.CGPAN AND RE.PAY_ID = o.PAY_ID AND RE.PAY_ID=nvl('"+payId+"',0)";    	      
	     
	       		 System.out.println(payId+ ">>>>Approperiation detail on payid>>>>>>>>>>"+npaRegistQuery);       
		        stmt = connection.createStatement();
		        rs = stmt.executeQuery((npaRegistQuery));
				  while (rs.next()) 
			    {
				 rpActionForm = new RPActionForm();
				 rpActionForm.setMemberId(rs.getString("MLIID"));
				 rpActionForm.setZoneName(rs.getString("MEM_ZONE_NAME"));
				 rpActionForm.setCgpan(rs.getString("CGPAN"));
				 rpActionForm.setGuaranteeAppAmt(rs.getDouble("ApproveAmt"));
				 rpActionForm.setUnitName(rs.getString("SSI_UNIT_NAME"));		
				 rpActionForm.setFirstClaimSetAmt(rs.getDouble("CTD_TC_FIRST_INST_PAY_AMT"));
				 rpActionForm.setAmountRemitedtoCgtmse(rs.getDouble("AMOUNT_REMITTED_TO_CGTMSE"));
				 rpActionForm.setRecoveryType(rs.getString("REC_TYPE"));
				 rpActionForm.setPayId(rs.getString("PAY_ID"));//----
				 rpActionForm.setInstrumentDate(rs.getDate("PAY_INSTRUMENT_DT"));//--
				 rpActionForm.setCheckerId(rs.getInt("RAFC_ID"));		 
				 rpActionForm.setBankName(rs.getString("MEM_BANK_NAME"));           // Bank Name
				 rpActionForm.setInstrumentNo(rs.getString("INSTUMENT_NUMBER"));                //PM.VIRTUAL_ACCOUNT_NO
				 rpActionForm.setAmount(rpActionForm.getGuaranteeAppAmt());//rs.getDouble(4));              // allocated amount
				 rpActionForm.setInstrumentAmount(rpActionForm.getAmountRemitedtoCgtmse());  //rs.getDouble(7));   //  setAmountRemitedtoCgtmse
				 rpActionForm.setReceivedAmount(rpActionForm.getAmountRemitedtoCgtmse()); //rs.getDouble(4));      // receivedAmt				
				 rpActionForm.setDateOfRealisation(rpActionForm.getInstrumentDate());  //rs.getDate(10));    // InstrumentDate(rs.getDate(10));		 
				 rpActionForm.setReceivedAmount(0);//rpActionForm.getGuaranteeAppAmt());//rs.getDouble(4));    //   short/excess amount
				 //================till here================//
		         recoveryPayList.add(rpActionForm);
		        // PM.VIRTUAL_ACCOUNT_NO as instument number
		      }			  
		   }       
		     	   
	       }catch (Exception sql) {
		    sql.printStackTrace();
	      } finally {
		   DBConnection.freeConnection(connection);
		   try {
			stmt.close();
			 rs.close();	
		  } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		   }
		   
	      }
	        Log.log(5, "GMAction", "displayNpaRegistrationFormList", "Exited");
	        return recoveryPayList;
	     }
*/
	    
		public ArrayList displayRecoveryList(String memBank,String payId,java.util.Date tDate, java.util.Date fmDate, String valueType)
		throws DatabaseException {
	             Log.log(5, "rpACTION", "displayNpaRegistrationFormList", "Entered");   
	            ArrayList recoveryPayList = new ArrayList();
	            Connection connection = DBConnection.getConnection(false);
	            RPActionForm rpActionForm = null;
	            ResultSet rs = null,rsd = null;
	            Statement stmt = null,stmtd = null;   
	            String fDate = "";
	    		String toDate = "";    		
	            String memberBank="''";
	            String bank_id="0000";
	            String npaRegistQuery ="";
	       		DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");         		 
	       		//System.out.println("memBank"+memBank+" payId"+payId+" tDate"+tDate+" fmDate"+fmDate+" valueType"+valueType);
	       		 try {
	       			 
	       		 	if((valueType.equals("ALLRECORD_DATE") || valueType.equals("ALLRECORD_BANK_NAME") || valueType.equals("BANK_AND_DATE_RECORD"))){
	       		 		
		     		 if(valueType.equals("ALLRECORD_DATE")){		     			 
		     			 toDate = formatter.format(tDate.getTime());
			       		 fDate = formatter.format(fmDate.getTime());			       		
		     			/*npaRegistQuery  = " select distinct RE.PAY_ID,CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID AS MLIID,  NVL (PAY_INSTRUMENT_DT, PAY_PAYMENT_DT) AS PAY_INSTRUMENT_DT, NVL(o.VIRTUAL_ACCOUNT_NO, 0) INSTUMENT_NUMBER," +
	       		        		" AMOUNT,(SELECT MAX(V.RAFC_ID) FROM RECOVRY_AFTER_BEFORE_FST_CLAIM V WHERE V.PAY_ID = RE.PAY_ID GROUP BY PAY_ID) AS RAFC_ID  FROM MEMBER_INFO M,CLAIM_TC_DETAIL TC,CLAIM_DETAIL CL,RECOVRY_AFTER_BEFORE_FST_CLAIM RE,PAYMENT_dETAIL_TEMP PM, SSI_dETAIL S, APPLICATION_DETAIL AP,ONLINE_PAYMENT_DETAIL@repuser o" +
	       		        		" where re.pay_id=o.pay_id AND CLM_STATUS = 'AP'  AND RECOVERY_UPDATION_FLAG = 'RP'  AND re.cgpan LIKE '%TC' AND RECOVERY_FLAG = 'AC' and DAN_TYPE='RV' and PAYMENT_STATUS='R' AND CL.CLM_REF_NO = TC.CLM_REF_NO  AND RE.CLM_REF_NO = CL.CLM_REF_NO" +
	       		        		"  AND PM.PAY_ID = RE.PAY_ID AND CL.BID = S.BID AND TC.CGPAN = AP.CGPAN AND RE.PAY_ID = o.PAY_ID and (trunc(o.PAYMENT_DATE) BETWEEN TO_DATE ('"+toDate+"','dd-MON-yyyy') AND TO_DATE ('"+fDate+"','dd-MON-yyyy')) UNION ALL" +
	       		        		" select distinct RE.PAY_ID,CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID AS MLIID,  NVL (PAY_INSTRUMENT_DT, PAY_PAYMENT_DT) AS PAY_INSTRUMENT_DT, NVL(o.VIRTUAL_ACCOUNT_NO, 0) INSTUMENT_NUMBER," +
	       		        		"  AMOUNT,(SELECT MAX(V.RAFC_ID) FROM RECOVRY_AFTER_BEFORE_FST_CLAIM V WHERE V.PAY_ID = RE.PAY_ID GROUP BY PAY_ID) AS RAFC_ID  FROM MEMBER_INFO M,CLAIM_WC_DETAIL TC,CLAIM_DETAIL CL,RECOVRY_AFTER_BEFORE_FST_CLAIM RE,PAYMENT_dETAIL_TEMP PM,SSI_dETAIL S,APPLICATION_DETAIL AP, ONLINE_PAYMENT_DETAIL@repuser o" +
	       		        		"  where re.pay_id=o.pay_id AND CLM_STATUS = 'AP'  AND RECOVERY_UPDATION_FLAG = 'RP'  AND re.cgpan LIKE '%TC' AND RECOVERY_FLAG = 'AC' and DAN_TYPE='RV' and PAYMENT_STATUS='R' AND CL.CLM_REF_NO = TC.CLM_REF_NO  AND RE.CLM_REF_NO = CL.CLM_REF_NO" +
	       		        		"  AND PM.PAY_ID = RE.PAY_ID AND CL.BID = S.BID AND TC.CGPAN = AP.CGPAN AND RE.PAY_ID = o.PAY_ID  AND (trunc(o.PAYMENT_DATE) BETWEEN TO_DATE ('"+toDate+"','dd-MON-yyyy') AND TO_DATE ('"+fDate+"','dd-MON-yyyy'))";
	       		     */
			       		 
			       	/* UAT	npaRegistQuery  = "select O.PAY_ID AS PAY_ID, O.MEM_BNK_ID || O.MEM_ZNE_ID || O.MEM_BRN_ID AS MLIID,O.PAYMENT_DATE AS PAY_INSTRUMENT_DT,O.VIRTUAL_ACCOUNT_NO AS INSTUMENT_NUMBER, O.AMOUNT AS AMOUNT," +
			       				"(SELECT MAX (V.RAFC_ID) FROM RECOVRY_AFTER_BEFORE_FST_CLAIM@repuser V  WHERE o.PAY_ID = V.PAY_ID GROUP BY PAY_ID)  AS RAFC_ID   from " +
			       				" ONLINE_PAYMENT_DETAIL@repuser o where PAYMENT_STATUS = 'R' and APPROPRIATE_STATUS='U' and " +
			       				" trunc(PAYMENT_DATE) BETWEEN TO_DATE ('"+toDate+"','dd-MON-yyyy') AND TO_DATE ('"+fDate+"','dd-MON-yyyy') and DAN_TYPE='RV' ORDER BY TO_CHAR(O.PAYMENT_DATE) ASC";  
			       		*/
			       		
			       		npaRegistQuery  = "select O.PAY_ID AS PAY_ID, O.MEM_BNK_ID || O.MEM_ZNE_ID || O.MEM_BRN_ID AS MLIID,O.PAYMENT_CREDITED_DATE AS PAY_INSTRUMENT_DT,O.VIRTUAL_ACCOUNT_NO AS INSTUMENT_NUMBER, O.AMOUNT AS AMOUNT," +
			       				"(SELECT MAX (V.RAFC_ID) FROM RECOVRY_AFTER_BEFORE_FST_CLAIM V  WHERE o.PAY_ID = V.PAY_ID GROUP BY PAY_ID)  AS RAFC_ID   from " +
			       				" ONLINE_PAYMENT_DETAIL@repuser o where PAYMENT_STATUS = 'R' and APPROPRIATE_STATUS='U' and " +
			       				" trunc(PAYMENT_CREDITED_DATE) BETWEEN TO_DATE ('"+toDate+"','dd-MON-yyyy') AND TO_DATE ('"+fDate+"','dd-MON-yyyy') and DAN_TYPE='RV' ORDER BY TO_CHAR(O.PAYMENT_CREDITED_DATE) ASC";  
			       		
		 				System.out.println("valueType================ Date wise =========1=================="+valueType);
		 				    stmt = connection.createStatement();
			     	        rs = stmt.executeQuery((npaRegistQuery));
			     	        valueType="''";
		 			}else if(valueType.equals("ALLRECORD_BANK_NAME")){
		 				 memberBank = memBank;
		 	       		/*npaRegistQuery = " select distinct RE.PAY_ID,CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID AS MLIID,  NVL (PAY_INSTRUMENT_DT, PAY_PAYMENT_DT) AS PAY_INSTRUMENT_DT, NVL(o.VIRTUAL_ACCOUNT_NO, 0) INSTUMENT_NUMBER," +
	       		        		" AMOUNT,(SELECT MAX(V.RAFC_ID) FROM RECOVRY_AFTER_BEFORE_FST_CLAIM V WHERE V.PAY_ID = RE.PAY_ID GROUP BY PAY_ID) AS RAFC_ID  FROM MEMBER_INFO M,CLAIM_TC_DETAIL TC,CLAIM_DETAIL CL,RECOVRY_AFTER_BEFORE_FST_CLAIM RE,PAYMENT_dETAIL_TEMP PM, SSI_dETAIL S, APPLICATION_DETAIL AP,ONLINE_PAYMENT_DETAIL@repuser o" +
	       		        		" where re.pay_id=o.pay_id AND CLM_STATUS = 'AP'  AND RECOVERY_UPDATION_FLAG = 'RP'  AND re.cgpan LIKE '%TC' AND RECOVERY_FLAG = 'AC' and DAN_TYPE='RV' and PAYMENT_STATUS='R' AND CL.CLM_REF_NO = TC.CLM_REF_NO  AND RE.CLM_REF_NO = CL.CLM_REF_NO" +
	       		        		"  AND PM.PAY_ID = RE.PAY_ID AND CL.BID = S.BID AND TC.CGPAN = AP.CGPAN AND RE.PAY_ID = o.PAY_ID and AP.MEM_BNK_ID = (select distinct mem_bnk_id from member_info where MEM_BANK_NAME = '"+memberBank+"') UNION ALL" +
	       		        		" select distinct RE.PAY_ID,CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID AS MLIID,  NVL (PAY_INSTRUMENT_DT, PAY_PAYMENT_DT) AS PAY_INSTRUMENT_DT, NVL(o.VIRTUAL_ACCOUNT_NO, 0) INSTUMENT_NUMBER," +
	       		        		"  AMOUNT,(SELECT MAX(V.RAFC_ID) FROM RECOVRY_AFTER_BEFORE_FST_CLAIM V WHERE V.PAY_ID = RE.PAY_ID GROUP BY PAY_ID) AS RAFC_ID  FROM MEMBER_INFO M,CLAIM_WC_DETAIL TC,CLAIM_DETAIL CL,RECOVRY_AFTER_BEFORE_FST_CLAIM RE,PAYMENT_dETAIL_TEMP PM,SSI_dETAIL S,APPLICATION_DETAIL AP, ONLINE_PAYMENT_DETAIL@repuser o" +
	       		        		"  where re.pay_id=o.pay_id AND CLM_STATUS = 'AP'  AND RECOVERY_UPDATION_FLAG = 'RP'  AND re.cgpan LIKE '%TC' AND RECOVERY_FLAG = 'AC' and DAN_TYPE='RV' and PAYMENT_STATUS='R' AND CL.CLM_REF_NO = TC.CLM_REF_NO  AND RE.CLM_REF_NO = CL.CLM_REF_NO" +
	       		        		"  AND PM.PAY_ID = RE.PAY_ID AND CL.BID = S.BID AND TC.CGPAN = AP.CGPAN AND RE.PAY_ID = o.PAY_ID and AP.MEM_BNK_ID = (select distinct mem_bnk_id from member_info where MEM_BANK_NAME = '"+memberBank+"')";
		 				*/
		 			/* UAT 	npaRegistQuery  = "select O.PAY_ID AS PAY_ID, O.MEM_BNK_ID || O.MEM_ZNE_ID || O.MEM_BRN_ID AS MLIID,O.PAYMENT_DATE AS PAY_INSTRUMENT_DT,O.VIRTUAL_ACCOUNT_NO AS INSTUMENT_NUMBER, O.AMOUNT AS AMOUNT," +
			       				"(SELECT MAX (V.RAFC_ID) FROM RECOVRY_AFTER_BEFORE_FST_CLAIM@repuser V  WHERE o.PAY_ID = V.PAY_ID GROUP BY PAY_ID)  AS RAFC_ID   from " +
			       				" ONLINE_PAYMENT_DETAIL@repuser o where PAYMENT_STATUS = 'R' and APPROPRIATE_STATUS='U' and DAN_TYPE='RV' and " +
			       				"  o.MEM_BNK_ID = (select distinct mem_bnk_id from member_info where MEM_BANK_NAME = '"+memberBank+"') ORDER BY TO_CHAR(O.PAYMENT_DATE) ASC";
		 			*/	// PROD
		 				npaRegistQuery  = "select O.PAY_ID AS PAY_ID, O.MEM_BNK_ID || O.MEM_ZNE_ID || O.MEM_BRN_ID AS MLIID,O.PAYMENT_DATE AS PAY_INSTRUMENT_DT,O.VIRTUAL_ACCOUNT_NO AS INSTUMENT_NUMBER, O.AMOUNT AS AMOUNT," +
			       				"(SELECT MAX (V.RAFC_ID) FROM RECOVRY_AFTER_BEFORE_FST_CLAIM V  WHERE o.PAY_ID = V.PAY_ID GROUP BY PAY_ID)  AS RAFC_ID   from " +
			       				" ONLINE_PAYMENT_DETAIL@repuser o where PAYMENT_STATUS = 'R' and APPROPRIATE_STATUS='U' and DAN_TYPE='RV' and " +
			       				"  o.MEM_BNK_ID = (select distinct mem_bnk_id from member_info where MEM_BANK_NAME = '"+memberBank+"') ORDER BY TO_CHAR(O.PAYMENT_DATE) ASC";
		 			 
		 				 System.out.println("valueType============ bank Name ===========2==================="+valueType);
		 				 stmt = connection.createStatement();
			     	      rs = stmt.executeQuery((npaRegistQuery));
			     	      valueType="''";
		 			 }else if(valueType.equals("BANK_AND_DATE_RECORD") ){
		 				 memberBank = memBank;
		 				 toDate = formatter.format(tDate.getTime());
			       		 fDate = formatter.format(fmDate.getTime());
			 	       		/*npaRegistQuery = " select distinct RE.PAY_ID,CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID AS MLIID,  NVL (PAY_INSTRUMENT_DT, PAY_PAYMENT_DT) AS PAY_INSTRUMENT_DT, NVL(o.VIRTUAL_ACCOUNT_NO, 0) INSTUMENT_NUMBER," +
		       		        		" AMOUNT,(SELECT MAX(V.RAFC_ID) FROM RECOVRY_AFTER_BEFORE_FST_CLAIM V WHERE V.PAY_ID = RE.PAY_ID GROUP BY PAY_ID) AS RAFC_ID  FROM MEMBER_INFO M,CLAIM_TC_DETAIL TC,CLAIM_DETAIL CL,RECOVRY_AFTER_BEFORE_FST_CLAIM RE,PAYMENT_dETAIL_TEMP PM, SSI_dETAIL S, APPLICATION_DETAIL AP,ONLINE_PAYMENT_DETAIL@repuser o" +
		       		        		" where re.pay_id=o.pay_id AND CLM_STATUS = 'AP'  AND RECOVERY_UPDATION_FLAG = 'RP'  AND re.cgpan LIKE '%TC' AND RECOVERY_FLAG = 'AC' and DAN_TYPE='RV' and PAYMENT_STATUS='R' AND CL.CLM_REF_NO = TC.CLM_REF_NO  AND RE.CLM_REF_NO = CL.CLM_REF_NO" +
		       		        		"  AND PM.PAY_ID = RE.PAY_ID AND CL.BID = S.BID AND TC.CGPAN = AP.CGPAN AND RE.PAY_ID = o.PAY_ID and AP.MEM_BNK_ID = (select distinct mem_bnk_id from member_info where MEM_BANK_NAME = '"+memberBank+"') UNION ALL" +
		       		        		" select distinct RE.PAY_ID,CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID AS MLIID,  NVL (PAY_INSTRUMENT_DT, PAY_PAYMENT_DT) AS PAY_INSTRUMENT_DT, NVL(o.VIRTUAL_ACCOUNT_NO, 0) INSTUMENT_NUMBER," +
		       		        		"  AMOUNT,(SELECT MAX(V.RAFC_ID) FROM RECOVRY_AFTER_BEFORE_FST_CLAIM V WHERE V.PAY_ID = RE.PAY_ID GROUP BY PAY_ID) AS RAFC_ID  FROM MEMBER_INFO M,CLAIM_WC_DETAIL TC,CLAIM_DETAIL CL,RECOVRY_AFTER_BEFORE_FST_CLAIM RE,PAYMENT_dETAIL_TEMP PM,SSI_dETAIL S,APPLICATION_DETAIL AP, ONLINE_PAYMENT_DETAIL@repuser o" +
		       		        		"  where re.pay_id=o.pay_id AND CLM_STATUS = 'AP'  AND RECOVERY_UPDATION_FLAG = 'RP'  AND re.cgpan LIKE '%TC' AND RECOVERY_FLAG = 'AC' and DAN_TYPE='RV' and PAYMENT_STATUS='R' AND CL.CLM_REF_NO = TC.CLM_REF_NO  AND RE.CLM_REF_NO = CL.CLM_REF_NO" +
		       		        		"  AND PM.PAY_ID = RE.PAY_ID AND CL.BID = S.BID AND TC.CGPAN = AP.CGPAN AND RE.PAY_ID = o.PAY_ID and AP.MEM_BNK_ID = (select distinct mem_bnk_id from member_info where MEM_BANK_NAME = '"+memberBank+"')";
			 				*/
			 		/*UAT		npaRegistQuery  = "select O.PAY_ID AS PAY_ID, O.MEM_BNK_ID || O.MEM_ZNE_ID || O.MEM_BRN_ID AS MLIID,O.PAYMENT_DATE AS PAY_INSTRUMENT_DT,O.VIRTUAL_ACCOUNT_NO AS INSTUMENT_NUMBER, O.AMOUNT AS AMOUNT," +
				       				"(SELECT MAX (V.RAFC_ID) FROM RECOVRY_AFTER_BEFORE_FST_CLAIM@repuser V  WHERE o.PAY_ID = V.PAY_ID GROUP BY PAY_ID)  AS RAFC_ID   from " +
				       				" ONLINE_PAYMENT_DETAIL@repuser o where PAYMENT_STATUS = 'R' and APPROPRIATE_STATUS='U' and DAN_TYPE='RV' and " +
				       				" (trunc(PAYMENT_DATE) BETWEEN TO_DATE ('"+toDate+"','dd-MON-yyyy') AND TO_DATE ('"+fDate+"','dd-MON-yyyy') AND o.MEM_BNK_ID = (select distinct mem_bnk_id from member_info where MEM_BANK_NAME = '"+memberBank+"')) ORDER BY TO_CHAR(O.PAYMENT_DATE) ASC";
			 			
			 			*/	//Prod
			       		npaRegistQuery  = "select O.PAY_ID AS PAY_ID, O.MEM_BNK_ID || O.MEM_ZNE_ID || O.MEM_BRN_ID AS MLIID,O.PAYMENT_DATE AS PAY_INSTRUMENT_DT,O.VIRTUAL_ACCOUNT_NO AS INSTUMENT_NUMBER, O.AMOUNT AS AMOUNT," +
			       				"(SELECT MAX (V.RAFC_ID) FROM RECOVRY_AFTER_BEFORE_FST_CLAIM V  WHERE o.PAY_ID = V.PAY_ID GROUP BY PAY_ID)  AS RAFC_ID   from " +
			       				" ONLINE_PAYMENT_DETAIL@repuser o where PAYMENT_STATUS = 'R' and APPROPRIATE_STATUS='U' and DAN_TYPE='RV' and " +
			       				" (trunc(PAYMENT_DATE) BETWEEN TO_DATE ('"+toDate+"','dd-MON-yyyy') AND TO_DATE ('"+fDate+"','dd-MON-yyyy') AND o.MEM_BNK_ID = (select distinct mem_bnk_id from member_info where MEM_BANK_NAME = '"+memberBank+"')) ORDER BY TO_CHAR(O.PAYMENT_DATE) ASC";
		 					
			 				
			 				
			 				
			 				System.out.println("valueType===========DATE bank Name ===========3=================="+valueType+"npaRegistQuery==="+npaRegistQuery);
			 				 stmt = connection.createStatement();
				     	      rs = stmt.executeQuery((npaRegistQuery));
				     	      valueType="''";
			 			 }
		     	      System.out.println(">>>>Approperiation Query>>>>ALLRECORD>>>> OUT >>>>>>>>>>>>"+npaRegistQuery); 
		     		  while (rs.next()) 
		     	    {
		     		 rpActionForm = new RPActionForm();
		     		 rpActionForm.setPayId(rs.getString("PAY_ID"));//----
		     		 rpActionForm.setMemberId(rs.getString("MLIID"));
		     		 rpActionForm.setInstrumentDate(rs.getDate("PAY_INSTRUMENT_DT"));//--
		     		 rpActionForm.setInstrumentNo(rs.getString("INSTUMENT_NUMBER")); 
		     		 rpActionForm.setInstrumentAmount(rs.getDouble("AMOUNT"));
		     		 rpActionForm.setCheckerId(rs.getInt("RAFC_ID"));
		     		 rpActionForm.setGuaranteeAppAmt(rs.getDouble("AMOUNT"));		     		
		     		 rpActionForm.setAmountRemitedtoCgtmse(rs.getDouble("AMOUNT"));	     		
		     		 rpActionForm.setReceivedAmount(rpActionForm.getAmountRemitedtoCgtmse()); 
		     		 rpActionForm.setDateOfRealisation(rpActionForm.getInstrumentDate());  
		     		 System.out.println("RSF ID>>>>>>>>>>>>>>"+ rpActionForm.getCheckerId());
		              recoveryPayList.add(rpActionForm);  
		     	   }
		         }else if(valueType.equals("PAYIDRECORD")){  
	       		 npaRegistQuery =	"SELECT DISTINCT CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID AS MLIID, MEM_ZONE_NAME,  RE.CGPAN, "
	    	      	 		+ " DECODE (APP_REAPPROVE_AMOUNT, NULL, APP_APPROVED_AMOUNT,  APP_REAPPROVE_AMOUNT) AS ApproveAmt, SSI_UNIT_NAME,  CTD_TC_FIRST_INST_PAY_AMT,  "
	    	      	 		+ " AMOUNT_REMITTED_TO_CGTMSE, (SELECT RMD_DESCRIPTION FROM RECOVERY_MODE_MASTER RM  WHERE RM.RMD_ID = RE.TYPE_OF_RECOVERY)  REC_TYPE,"
	    	      	 		+ " RE.PAY_ID, NVL(PAY_INSTRUMENT_DT, PAY_PAYMENT_DT) AS PAY_INSTRUMENT_DT, RAFC_ID, M.MEM_BANK_NAME, NVL(o.VIRTUAL_ACCOUNT_NO,0) INSTUMENT_NUMBER FROM MEMBER_INFO M,  CLAIM_TC_DETAIL TC, CLAIM_DETAIL CL,"
	    	      	 		+ " RECOVRY_AFTER_BEFORE_FST_CLAIM RE, PAYMENT_dETAIL_TEMP PM, SSI_dETAIL S,  APPLICATION_DETAIL AP, ONLINE_PAYMENT_DETAIL@repuser o  WHERE  M.MEM_BNK_ID || M.MEM_ZNE_ID"
	    	      	 		+ " || M.MEM_BRN_ID = CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID  AND CLM_STATUS = 'AP'  AND RECOVERY_UPDATION_FLAG = 'RP' AND re.cgpan LIKE '%TC' "
	    	      	 		+ " AND RECOVERY_FLAG = 'AC'  AND CL.CLM_REF_NO = TC.CLM_REF_NO AND RE.CLM_REF_NO = CL.CLM_REF_NO AND PM.PAY_ID = RE.PAY_ID AND CL.BID = S.BID  "
	    	      	 		+ " AND TC.CGPAN = AP.CGPAN AND RE.PAY_ID = o.PAY_ID AND RE.PAY_ID=nvl('"+payId+"',0) UNION ALL  SELECT DISTINCT CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID, MEM_ZONE_NAME, RE.CGPAN, DECODE (APP_REAPPROVE_AMOUNT, "
	    	      	 		+ " NULL, APP_APPROVED_AMOUNT, APP_REAPPROVE_AMOUNT),  SSI_UNIT_NAME, CWD_WC_FIRST_INST_PAY_AMT, AMOUNT_REMITTED_TO_CGTMSE, (SELECT RMD_DESCRIPTION FROM RECOVERY_MODE_MASTER RM"
	    	      	 		+ " WHERE RM.RMD_ID = RE.TYPE_OF_RECOVERY) REC_TYPE,  RE.PAY_ID, NVL (PAY_INSTRUMENT_DT, PAY_PAYMENT_DT) AS PAY_INSTRUMENT_DT,  RAFC_ID,M.MEM_BANK_NAME, NVL(o.VIRTUAL_ACCOUNT_NO,0) INSTUMENT_NUMBER FROM MEMBER_INFO M, CLAIM_WC_DETAIL TC, "
	    	      	 		+ " CLAIM_DETAIL CL, RECOVRY_AFTER_BEFORE_FST_CLAIM RE, PAYMENT_dETAIL_TEMP PM,  SSI_dETAIL S, APPLICATION_DETAIL AP,ONLINE_PAYMENT_DETAIL@repuser o WHERE  M.MEM_BNK_ID || M.MEM_ZNE_ID || M.MEM_BRN_ID = "
	    	      	 		+ "  CL.MEM_BNK_ID || CL.MEM_ZNE_ID || CL.MEM_BRN_ID  AND CLM_STATUS = 'AP' AND RECOVERY_UPDATION_FLAG = 'RP'  AND re.cgpan LIKE '%WC'  AND RECOVERY_FLAG = 'AC' AND CL.CLM_REF_NO ="
	    	      	 		+ " TC.CLM_REF_NO  AND RE.CLM_REF_NO = CL.CLM_REF_NO  AND PM.PAY_ID = RE.PAY_ID  AND CL.BID = S.BID  AND TC.CGPAN = AP.CGPAN AND RE.PAY_ID = o.PAY_ID AND RE.PAY_ID=nvl('"+payId+"',0)";    	      
	     
	       		 System.out.println(payId+ ">>>>Approperiation detail on payid>>>>>>>>>>"+npaRegistQuery);       
		        stmt = connection.createStatement();
		        rs = stmt.executeQuery((npaRegistQuery));
				  while (rs.next()) 
			    {
				 rpActionForm = new RPActionForm();
				 rpActionForm.setMemberId(rs.getString("MLIID"));
				 rpActionForm.setZoneName(rs.getString("MEM_ZONE_NAME"));
				 rpActionForm.setCgpan(rs.getString("CGPAN"));
				 rpActionForm.setGuaranteeAppAmt(rs.getDouble("ApproveAmt"));
				 rpActionForm.setUnitName(rs.getString("SSI_UNIT_NAME"));		
				 rpActionForm.setFirstClaimSetAmt(rs.getDouble("CTD_TC_FIRST_INST_PAY_AMT"));
				 rpActionForm.setAmountRemitedtoCgtmse(rs.getDouble("AMOUNT_REMITTED_TO_CGTMSE"));
				 rpActionForm.setRecoveryType(rs.getString("REC_TYPE"));
				 rpActionForm.setPayId(rs.getString("PAY_ID"));//----
				 rpActionForm.setInstrumentDate(rs.getDate("PAY_INSTRUMENT_DT"));//--
				 rpActionForm.setCheckerId(rs.getInt("RAFC_ID"));		 
				 rpActionForm.setBankName(rs.getString("MEM_BANK_NAME"));           // Bank Name
				 rpActionForm.setInstrumentNo(rs.getString("INSTUMENT_NUMBER"));                //PM.VIRTUAL_ACCOUNT_NO
				 rpActionForm.setAmount(rpActionForm.getGuaranteeAppAmt());//rs.getDouble(4));              // allocated amount
				 rpActionForm.setInstrumentAmount(rpActionForm.getAmountRemitedtoCgtmse());  //rs.getDouble(7));   //  setAmountRemitedtoCgtmse
				 rpActionForm.setReceivedAmount(rpActionForm.getAmountRemitedtoCgtmse()); //rs.getDouble(4));      // receivedAmt				
				 rpActionForm.setDateOfRealisation(rpActionForm.getInstrumentDate());  //rs.getDate(10));    // InstrumentDate(rs.getDate(10));		 
				 rpActionForm.setReceivedAmount(0);//rpActionForm.getGuaranteeAppAmt());//rs.getDouble(4));    //   short/excess amount
				 //================till here================//
		         recoveryPayList.add(rpActionForm);
		        // PM.VIRTUAL_ACCOUNT_NO as instument number
		      }			  
		   }       
		     	   
	       }catch (Exception sql) {
		    sql.printStackTrace();
	      } finally {
		   DBConnection.freeConnection(connection);
		   try {
			stmt.close();
			 rs.close();	
		  } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		   }
		   
	      }
	        Log.log(5, "Rpction", "displayRecoveryList", "Exited");
	        return recoveryPayList;
	     }  
		
	public ActionForward getPaymentMadeForTenureInput(ActionMapping mapping,
				ActionForm form, HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			RPActionForm actionForm = (RPActionForm) form;
			// actionForm.setMemberId("");
			Date date = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DATE);
			month = month - 1;
			day = day + 1;
			cal.set(Calendar.MONTH, month);
			cal.set(Calendar.DATE, day);
			Date prevdate = cal.getTime();
			GeneralReport general = new GeneralReport();
			general.setDateOfTheDocument24(prevdate);
			general.setDateOfTheDocument25(date);
			BeanUtils.copyProperties(actionForm, general);
			return mapping.findForward("success");
		}
	    
		public ActionForward getPaymentsMadeForTenure(ActionMapping mapping,
				ActionForm form, HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			RPActionForm actionForm = (RPActionForm) form;
			// String memId = actionForm.getMemberId();
			Date fromDate = actionForm.getDateOfTheDocument24();
			Date toDate = actionForm.getDateOfTheDocument25();
			 System.out.println("fromdate "+fromDate+"---- to date   "+toDate);
			RpProcessor rpProcessor = new RpProcessor();
			ArrayList paymentDetails = rpProcessor.displayPaymentsReceivedForTenure(
					fromDate, toDate);
			actionForm.getCgpans().clear();
			actionForm.setPaymentDetails(paymentDetails);
			return mapping.findForward("paymentsSummary");
		}

		public ActionForward getPaymentDetailsForTenure(ActionMapping mapping,
				ActionForm form, HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			String methodName = "getPaymentDetails";
			RPActionForm actionForm = (RPActionForm) form;
			Log.log(4, "RPAction", methodName, "Entering");
			String paymentId = actionForm.getPaymentId();
			Log.log(4, "RPAction", methodName, "Got paymentId");
			actionForm.getCgpans().clear();
			RpProcessor rpProcessor = new RpProcessor();
			ArrayList paymentDetails = null;
			Log.log(4, "RPAction", methodName, "Before calling payment details");
			paymentDetails = rpProcessor.getPaymentDetails(paymentId);
			HashMap bankIds = new HashMap();
			HashMap zoneIds = new HashMap();
			HashMap branchIds = new HashMap();
			PaymentDetails payDetails = (PaymentDetails) paymentDetails.get(0);
			actionForm.setInstrumentDate(payDetails.getInstrumentDate());
			for (int i = 1; i < paymentDetails.size(); i++) {
				DemandAdvice demandAdvice = (DemandAdvice) paymentDetails.get(i);
				demandAdvice.setAppropriated(demandAdvice.getAllocated());
				actionForm.setAppropriatedFlag((new StringBuilder()).append("key-")
						.append(i - 1).toString(), demandAdvice.getAllocated());
				bankIds.put((new StringBuilder()).append("key-").append(i - 1)
						.toString(), demandAdvice.getBankId());
				zoneIds.put((new StringBuilder()).append("key-").append(i - 1)
						.toString(), demandAdvice.getZoneId());
				branchIds.put((new StringBuilder()).append("key-").append(i - 1)
						.toString(), demandAdvice.getBranchId());
			}

			Log.log(4, "RPAction", methodName, "After calling payment details");
			Log.log(4, "RPAction", methodName,
					"Before dynaForm set in RPAction::getPaymentDetails");
			actionForm.setPaymentDetails(paymentDetails);
			actionForm.setDateOfRealisation(null);
			actionForm.setReceivedAmount(0.0D);
			actionForm.setPaymentId(paymentId);
			actionForm.setBankIds(bankIds);
			actionForm.setBranchIds(branchIds);
			actionForm.setZoneIds(zoneIds);
			Log.log(4, "RPAction", methodName,
					"After dynaForm set in RPAction::getPaymentDetails");
			return mapping.findForward("paymentDetails");
		}
		
		public ActionForward appropriatePaymentsForTenure(ActionMapping mapping,
				ActionForm form, HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			RPActionForm actionForm = (RPActionForm) form;
			RpProcessor rpProcessor = new RpProcessor();
			ArrayList demandAdvices = new ArrayList();
			String methodName = "appropriatePayments";
			String danId = "";
			String cgpan = "";
			String allocatedFlag = "";
			String appropriatedFlag = "";
			String remark = "";
			String paymentId = "";
			DemandAdvice demandAdvice = null;
			Log.log(4, "RPAction", methodName, "Entered");
			Map danIds = actionForm.getDanIds();
			Map cgpans = actionForm.getCgpans();
			Map remarks = actionForm.getRemarks();
			Map allocatedFlags = actionForm.getAllocatedFlags();
			Map appropriatedFlags = actionForm.getAppropriatedFlags();
			Map amounts = actionForm.getAmountsRaised();
			Map penalties = actionForm.getPenalties();
			Map bankIds = actionForm.getBankIds();
			Map zoneIds = actionForm.getZoneIds();
			Map branchIds = actionForm.getBranchIds();
			Log.log(4, "RPAction", methodName, "Assigned CGPAN details to hashmap");
			Set danIdSet = danIds.keySet();
			Set allocatedFlagSet = allocatedFlags.keySet();
			Set appropriatedFlagSet = appropriatedFlags.keySet();
			Set cgpanSet = cgpans.keySet();
			Set remarksSet = remarks.keySet();
			Set amountsSet = amounts.keySet();
			Set penaltySet = penalties.keySet();
			Set bankIdSet = bankIds.keySet();
			Set zoneIdSet = zoneIds.keySet();
			Set branchIdSet = branchIds.keySet();
			Log.log(4, "RPAction", methodName, "Assigned CGPAN details to Set");
			Iterator cgpanIterator = cgpanSet.iterator();
			Iterator bankIdIterator = bankIdSet.iterator();
			Iterator zoneIdIterator = zoneIdSet.iterator();
			Iterator branchIdIterator = branchIdSet.iterator();
			Log.log(4, "RPAction", methodName, "Assigned CGPAN details to Iterator");
			User user = getUserInformation(request);
			String userId = user.getUserId();
			paymentId = actionForm.getPaymentId();
			double appropriatedAmount = 0.0D;
			for (; cgpanIterator.hasNext(); Log.log(4, "RPAction", methodName,
					"DemandAdvices added to ArrayList")) {
				String cgpanKey = (String) cgpanIterator.next();
				danId = (String) danIds.get(cgpanKey);
				cgpan = (String) cgpans.get(cgpanKey);
				allocatedFlag = (String) allocatedFlags.get(cgpanKey);
				appropriatedFlag = (String) appropriatedFlags.get(cgpanKey);
				remark = (String) remarks.get(cgpanKey);
				double amount = Double.parseDouble((String) amounts.get(cgpanKey));
				double penalty = Double.parseDouble((String) penalties
						.get(cgpanKey));
				Log.log(Log.DEBUG, "RPAction", methodName, (new StringBuilder())
						.append(" inside iterator - dan id - ").append(danId)
						.append(" inside iterator - cgpan - ").append(cgpan)
						.toString());
				Log.log(Log.DEBUG,
						"RPAction",
						methodName,
						(new StringBuilder())
								.append(" inside iterator - allocated flag - ")
								.append(allocatedFlag)
								.append(" inside iterator - appropriated flag - ")
								.append(appropriatedFlag).toString());
				Log.log(Log.DEBUG, "RPAction", methodName, (new StringBuilder())
						.append(" inside iterator - amount - ").append(amount)
						.append(" inside iterator - penalty - ").append(penalty)
						.toString());
				if (cgpan == null || cgpan.equals("") || danId == null
						|| danId.equals("") || allocatedFlag == null) {
					throw new MessageException("Problem in data. Check for "
							+ danId);
				}
				demandAdvice = new DemandAdvice();
				demandAdvice.setDanNo(danId);
				demandAdvice.setCgpan(cgpan);
				demandAdvice.setReason(remark);
				demandAdvice.setAmountRaised(amount);
				demandAdvice.setPenalty(penalty);
				demandAdvice.setPaymentId(paymentId);
				demandAdvice.setAllocated(appropriatedFlag);
				demandAdvice.setAppropriated(appropriatedFlag);
				demandAdvice.setUserId(userId);
				demandAdvice.setBankId((String) bankIds.get(bankIdIterator.next()));
				demandAdvice.setZoneId((String) zoneIds.get(zoneIdIterator.next()));
				demandAdvice.setBranchId((String) branchIds.get(branchIdIterator
						.next()));

				if (appropriatedFlag.equals("Y"))
					appropriatedAmount += amount;
				demandAdvices.add(demandAdvice);
				Log.log(4,
						"RPAction",
						methodName,
						(new StringBuilder())
								.append(" inside iterator - adding cgpan to demand advice list - ")
								.append(cgpan).toString());
			}

			Date realisationDate = actionForm.getDateOfRealisation();
			Log.log(Log.DEBUG, "RPAction", methodName, " realisationDate--"
					+ realisationDate);
			if (realisationDate == null) {
				throw new MessageException("Realisation date missing. Try again.");
			}

			double receivedAmount = actionForm.getReceivedAmount();
			RealisationDetail realisationDetail = new RealisationDetail();
			realisationDetail.setPaymentId(paymentId);
			realisationDetail.setRealisationAmount(receivedAmount);
			realisationDetail.setRealisationDate(realisationDate);
			if (receivedAmount < appropriatedAmount) {
				double shortLimit = appropriatedAmount - receivedAmount;
				throw new ShortExceedsLimitException(
						(new StringBuilder())
								.append("Received Amount is less than Allocated Amount by Rs.")
								.append(shortLimit).toString());
			}
			if (receivedAmount > appropriatedAmount) {
				double excessLimit = receivedAmount - appropriatedAmount;
				throw new ShortExceedsLimitException(
						(new StringBuilder())
								.append("Received Amount is greater than Allocated Amount by Rs.")
								.append(excessLimit).toString());
			} else {
				double shortOrExcess = rpProcessor.appropriatePayment(
						demandAdvices, realisationDetail, request.getSession(false)
								.getServletContext().getRealPath(""));
				request.setAttribute(
						"message",
						(new StringBuilder())
								.append("Payment Amount Appropriated Successfully.<BR><BR>Total Received Amount : ")
								.append(receivedAmount)
								.append("<BR>Total Appropriated Amount : ")
								.append(appropriatedAmount)
								.append("<BR>Short / Excess : ")
								.append(shortOrExcess).toString());
				return mapping.findForward("success");
			}
		}

	    //+++++++++++++++++++++++++++++END===================================

	

	public RPAction() {
		$init$();
	}

	private static final String className = "RPAction";
	Registration registration;
}



