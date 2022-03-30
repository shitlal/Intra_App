package com.cgtsi.reports;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.cgtsi.common.Constants;
import com.cgtsi.common.DatabaseException;
import com.cgtsi.util.DBConnection;

public class RiskPremiumDAO {

	public RiskPremiumDAO() {
	}
	
	
	public ArrayList getRiskPremiumReport(String financialYear, String id) throws DatabaseException {
		// TODO Auto-generated method stub
		
		System.out.println("getRiskPremiumReport-----------id.."+id+"...financialYear...."+financialYear);
		
		CallableStatement callableStmt = null;
		 Connection conn = null;
		ResultSet resultset = null;
		ArrayList riskPremiumData = new ArrayList();
		int status = -1;
		String errorCode = null;
		try {
			conn = DBConnection.getConnection();
			
			
			
					callableStmt = conn
							.prepareCall("{call PROC_RISKRATIOS_ASFREPORT (?,?,?)}");
					callableStmt.setString(1, financialYear);
	//				callableStmt.setDate(3, endDate);
	//				callableStmt.setString(4, userId);
	//				callableStmt.setString(5, Id);
					callableStmt.registerOutParameter(2, Constants.CURSOR);
					callableStmt.setInt(3, Integer.parseInt(id));
	//				callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);
			

				callableStmt.execute();

				resultset = (ResultSet) callableStmt.getObject(2);

				System.out.println("getRiskPremiumReport-----------resultset..."+resultset);

				while (resultset.next()) {

				
					RiskPremium riskPremium = new RiskPremium();
					
					riskPremium.setFinalcialYear(resultset.getString("FY_START_DATE"));
					riskPremium.setMemBankName(resultset.getString("MEM_BANK_NAME"));
					riskPremium.setNpaRatio(resultset.getString("NPA_RATIO"));
					riskPremium.setNpaRiskPremPercentage(resultset.getString("NPA_RISK_PREM_PERCENTAGE"));
					riskPremium.setClaimPayOutRatio(resultset.getString("CLAIM_PAY_OUT_RATIO"));
					
					if(riskPremium.getClaimPayOutRatio()!=null){
					 	if(Float.parseFloat(riskPremium.getClaimPayOutRatio())<1 && Float.parseFloat(riskPremium.getClaimPayOutRatio()) !=0 ){
					 		riskPremium.setClaimPayOutRatio(0+riskPremium.getClaimPayOutRatio());
					 	}
					}
					
//					System.out.println("riskPremium.getClaimPayOutRatio() ......"+riskPremium.getClaimPayOutRatio());
					
					riskPremium.setClaimPayoutRiskPremPercentage(resultset.getString("CLAIM_PAYOUT_RISK_PREM_PERCENT"));
					riskPremium.setTotal(resultset.getString("TOTAL"));
					
										    
				    riskPremiumData.add(riskPremium);

				}
				////System.out.println("list data " + nestData);
			resultset.close();
			resultset = null;
			callableStmt.close();
			callableStmt = null;
		} catch (SQLException sqlexception) {
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		System.out.println("riskPremiumData is "+riskPremiumData.size());
		return riskPremiumData;
	}
	
	
	
	
	
}