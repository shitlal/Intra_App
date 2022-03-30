package com.cgtsi.action;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import com.cgtsi.admin.User;
import com.cgtsi.common.NoDataException;
import com.cgtsi.reports.RiskPremiumDAO;


public class RiskPremiumAction extends BaseAction {
	private RiskPremiumDAO riskPremiumDao;

	private void $init$() {
		this.riskPremiumDao = null;
	}

	public RiskPremiumAction() {
		$init$(); 

		this.riskPremiumDao = new RiskPremiumDAO();
	}
	
			public ActionForward riskPremiumReportInput(ActionMapping mapping,
					ActionForm form, HttpServletRequest request,
					HttpServletResponse response) throws Exception {

				DynaActionForm dynaForm = (DynaActionForm) form;
				User user = getUserInformation(request);
				String bankId = user.getBankId();
				String zoneId = user.getZoneId();
				String branchId = user.getBranchId();
				String memberId = bankId.concat(zoneId).concat(branchId);

				dynaForm.set("bankId", bankId);
				if (bankId.equals("0000")) {
					memberId = "";
				}
				dynaForm.set("memberId", memberId);


				return mapping.findForward("success");
			}

			public ActionForward riskPremiumReport(ActionMapping mapping, ActionForm form,
					HttpServletRequest request, HttpServletResponse response)
					throws Exception {
				ArrayList riskPremium = new ArrayList();
				DynaActionForm dynaForm = (DynaActionForm) form;
				
				User user = getUserInformation(request);
				String bankId = user.getBankId();
				String zoneId = user.getZoneId();
				String branchId = user.getBranchId();
				
				String id = (String) dynaForm.get("memberId");
				
				String financialYear = (String) dynaForm.get("financialYear");
				
				System.out.println("memberId is ..."+id+"....financialYear"+financialYear);
					
				riskPremium = this.riskPremiumDao.getRiskPremiumReport(financialYear,bankId);
				dynaForm.set("riskPremium", riskPremium);
				if ((riskPremium == null) || (riskPremium.size() == 0)) {
					throw new NoDataException(
							"No Data is available for the values entered, Please Enter Any Other Value ");
				}
				return mapping.findForward("success");
			}
	
	
	
	
	
	
	
	
	
	
}