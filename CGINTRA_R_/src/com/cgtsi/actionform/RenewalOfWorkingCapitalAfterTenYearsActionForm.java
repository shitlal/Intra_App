package com.cgtsi.actionform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.struts.validator.ValidatorActionForm;


public class RenewalOfWorkingCapitalAfterTenYearsActionForm extends ValidatorActionForm{
		private static final long serialVersionUID = 1L;
	
		private String memberId;
		private String loginUserId;
		private ArrayList renewalOfWorkingCapitalAfterTenYears=new ArrayList();
		private String cgpan;
		private String unitName;
		private String unitPAN;
		private String lineOfActivity;
		private String classification;
		private String loanAccountNumber;
		private String promotersName;
		private String chiefPromoterPAN;
		private String address;
		private String guranteeStartDate;
		private String guranteeExpiryDate;
		private String lastRenewalDateOfWC;
		

		public static long getSerialversionuid() {
			return serialVersionUID;
		}
		private String existing;
		private String nonfundbasedexisting;
		private String totexistingnonfundbased;
		private String proposed;
		private String nonfundbasedproposed;
		private String totfundnonbasedproposed;
		
		
		private String satisfactoryCreditReports;
		private String satisfactoryCibilReports;
		private String pddefaulter;
		private String groupAssociateNPA;
		private String satisfactoryPositionStatDues;
		private String repaymentTrackSatisfactory;
						
		private String availabilityOfStockAsset;
		private String workingOfTheFactory;
		private String generalUpkeep;
		private String stockRegister;
		private String stockStatement;
		private String proposalInvstGrade;
		
		
		private String totalScoreGradePYAPAAppraial;
		private String totalScoreGradeCYAPAAppraial;

		private String commentsOnScorePYAPAAppraial;
		private String commentsOnScoreCYAPAAppraial;

		private String workingCapitalLimitExisting;
		private String workingCapitalLimitProposed;
		private String workingCapitalLimitnonfundProposed;
		private String workingCapitalLimittotFundNonFundProposed;
		private String workingCapitalLimitComments;
		private String workingCapitalLimitStatus;
		
		private String appReferNo;
		private String bankname;
		private String zonename;
		private String lodgedate;
		private String apptype;
		private String appstatus;
		
		private int firstCounter;
		private Vector firstInstallmentClaims;
		private String clmRefDtlSet;
		//Parmanand
		
		private String   statuss;
		private String  mlicomments;
		private ArrayList renewalWCAllFormData;
		private String mlidecision;
		private String cgPanValue;

		private String currrentRatioActualValue;
		private String tolOrTNWActualValue;
		private String drOrCrDaysActualValue;
		private String interestCoverageActualValue;
		private String assetCoverageRatioActualValue;
		private String fixedAssetCoverageRatioActualValue;
		
		private String currentRatio;
		private String tolOrTNW;
		private String drOrCrDays;
		private String interestCoverage;
		private String assetCoverageRatio;
		private String fixedAssetCoverageRatio;
		
		private String psMovablesValue;
		private String psMovablesRemarks;
		private String psImmovalableValue;
		private String psImmovalableRemarks;
		
		private String clMovablesValue;
		private String clMovablesRemarks;
		private String clImmovalableValue;
		private String clImmovalableRemarks;
		
		private String salesPreviousYear;
		private String salesCurrentYear;
		private String salesProjectionNextYear;

		private String pbditPreviousYear;
		private String pbditCurrentYear;
		private String pbditProjectionNextYear;

		private String equityCapitalPreviousYear;
		private String equityCapitalCurrentYear;
		private String equityCapitalProjectionNextYear;
		
		
		
		public String getPsMovablesValue() {
			return psMovablesValue;
		}
		public void setPsMovablesValue(String psMovablesValue) {
			this.psMovablesValue = psMovablesValue;
		}
		public String getPsMovablesRemarks() {
			return psMovablesRemarks;
		}
		public void setPsMovablesRemarks(String psMovablesRemarks) {
			this.psMovablesRemarks = psMovablesRemarks;
		}
		public String getPsImmovalableValue() {
			return psImmovalableValue;
		}
		public void setPsImmovalableValue(String psImmovalableValue) {
			this.psImmovalableValue = psImmovalableValue;
		}
		public String getPsImmovalableRemarks() {
			return psImmovalableRemarks;
		}
		public void setPsImmovalableRemarks(String psImmovalableRemarks) {
			this.psImmovalableRemarks = psImmovalableRemarks;
		}
		public String getClMovablesValue() {
			return clMovablesValue;
		}
		public void setClMovablesValue(String clMovablesValue) {
			this.clMovablesValue = clMovablesValue;
		}
		public String getClMovablesRemarks() {
			return clMovablesRemarks;
		}
		public void setClMovablesRemarks(String clMovablesRemarks) {
			this.clMovablesRemarks = clMovablesRemarks;
		}
		public String getClImmovalableValue() {
			return clImmovalableValue;
		}
		public void setClImmovalableValue(String clImmovalableValue) {
			this.clImmovalableValue = clImmovalableValue;
		}
		public String getClImmovalableRemarks() {
			return clImmovalableRemarks;
		}
		public void setClImmovalableRemarks(String clImmovalableRemarks) {
			this.clImmovalableRemarks = clImmovalableRemarks;
		}
		public String getSalesPreviousYear() {
			return salesPreviousYear;
		}
		public void setSalesPreviousYear(String salesPreviousYear) {
			this.salesPreviousYear = salesPreviousYear;
		}
		public String getSalesCurrentYear() {
			return salesCurrentYear;
		}
		public void setSalesCurrentYear(String salesCurrentYear) {
			this.salesCurrentYear = salesCurrentYear;
		}
		public String getSalesProjectionNextYear() {
			return salesProjectionNextYear;
		}
		public void setSalesProjectionNextYear(String salesProjectionNextYear) {
			this.salesProjectionNextYear = salesProjectionNextYear;
		}
		public String getPbditPreviousYear() {
			return pbditPreviousYear;
		}
		public void setPbditPreviousYear(String pbditPreviousYear) {
			this.pbditPreviousYear = pbditPreviousYear;
		}
		public String getPbditCurrentYear() {
			return pbditCurrentYear;
		}
		public void setPbditCurrentYear(String pbditCurrentYear) {
			this.pbditCurrentYear = pbditCurrentYear;
		}
		public String getPbditProjectionNextYear() {
			return pbditProjectionNextYear;
		}
		public void setPbditProjectionNextYear(String pbditProjectionNextYear) {
			this.pbditProjectionNextYear = pbditProjectionNextYear;
		}
		public String getEquityCapitalPreviousYear() {
			return equityCapitalPreviousYear;
		}
		public void setEquityCapitalPreviousYear(String equityCapitalPreviousYear) {
			this.equityCapitalPreviousYear = equityCapitalPreviousYear;
		}
		public String getEquityCapitalCurrentYear() {
			return equityCapitalCurrentYear;
		}
		public void setEquityCapitalCurrentYear(String equityCapitalCurrentYear) {
			this.equityCapitalCurrentYear = equityCapitalCurrentYear;
		}
		public String getEquityCapitalProjectionNextYear() {
			return equityCapitalProjectionNextYear;
		}
		public void setEquityCapitalProjectionNextYear(
				String equityCapitalProjectionNextYear) {
			this.equityCapitalProjectionNextYear = equityCapitalProjectionNextYear;
		}
		public String getCurrentRatio() {
			return currentRatio;
		}
		public void setCurrentRatio(String currentRatio) {
			this.currentRatio = currentRatio;
		}
		public String getTolOrTNW() {
			return tolOrTNW;
		}
		public void setTolOrTNW(String tolOrTNW) {
			this.tolOrTNW = tolOrTNW;
		}
		public String getDrOrCrDays() {
			return drOrCrDays;
		}
		public void setDrOrCrDays(String drOrCrDays) {
			this.drOrCrDays = drOrCrDays;
		}
		public String getInterestCoverage() {
			return interestCoverage;
		}
		public void setInterestCoverage(String interestCoverage) {
			this.interestCoverage = interestCoverage;
		}
		public String getAssetCoverageRatio() {
			return assetCoverageRatio;
		}
		public void setAssetCoverageRatio(String assetCoverageRatio) {
			this.assetCoverageRatio = assetCoverageRatio;
		}
		public String getFixedAssetCoverageRatio() {
			return fixedAssetCoverageRatio;
		}
		public void setFixedAssetCoverageRatio(String fixedAssetCoverageRatio) {
			this.fixedAssetCoverageRatio = fixedAssetCoverageRatio;
		}
		public String getCurrrentRatioActualValue() {
			return currrentRatioActualValue;
		}
		public void setCurrrentRatioActualValue(String currrentRatioActualValue) {
			this.currrentRatioActualValue = currrentRatioActualValue;
		}
		public String getTolOrTNWActualValue() {
			return tolOrTNWActualValue;
		}
		public void setTolOrTNWActualValue(String tolOrTNWActualValue) {
			this.tolOrTNWActualValue = tolOrTNWActualValue;
		}
		public String getDrOrCrDaysActualValue() {
			return drOrCrDaysActualValue;
		}
		public void setDrOrCrDaysActualValue(String drOrCrDaysActualValue) {
			this.drOrCrDaysActualValue = drOrCrDaysActualValue;
		}
		public String getInterestCoverageActualValue() {
			return interestCoverageActualValue;
		}
		public void setInterestCoverageActualValue(String interestCoverageActualValue) {
			this.interestCoverageActualValue = interestCoverageActualValue;
		}
		public String getAssetCoverageRatioActualValue() {
			return assetCoverageRatioActualValue;
		}
		public void setAssetCoverageRatioActualValue(
				String assetCoverageRatioActualValue) {
			this.assetCoverageRatioActualValue = assetCoverageRatioActualValue;
		}
		public String getFixedAssetCoverageRatioActualValue() {
			return fixedAssetCoverageRatioActualValue;
		}
		public void setFixedAssetCoverageRatioActualValue(
				String fixedAssetCoverageRatioActualValue) {
			this.fixedAssetCoverageRatioActualValue = fixedAssetCoverageRatioActualValue;
		}
		public String getCgPanValue() {
			return cgPanValue;
		}
		public void setCgPanValue(String cgPanValue) {
			this.cgPanValue = cgPanValue;
		}
		public String getAppstatus() {
			return appstatus;
		}
		public void setAppstatus(String appstatus) {
			this.appstatus = appstatus;
		}
		
		public String getMlicomments() {
			return mlicomments;
		}
		public void setMlicomments(String mlicomments) {
			this.mlicomments = mlicomments;
		}
		public String getMlidecision() {
			return mlidecision;
		}
		public void setMlidecision(String mlidecision) {
			this.mlidecision = mlidecision;
		}
		public String getStatuss() {
			return statuss;
		}
		public void setStatuss(String statuss) {
			this.statuss = statuss;
		}
		public int getFirstCounter() {
			return firstCounter;
		}
		public void setFirstCounter(int firstCounter) {
			this.firstCounter = firstCounter;
		}
		public String getClmRefDtlSet() {
			return clmRefDtlSet;
		}
		public void setClmRefDtlSet(String clmRefDtlSet) {
			this.clmRefDtlSet = clmRefDtlSet;
		}
		public Vector getFirstInstallmentClaims() {
			return firstInstallmentClaims;
		}
		public void setFirstInstallmentClaims(Vector firstInstallmentClaims) {
			this.firstInstallmentClaims = firstInstallmentClaims;
		}
		public String getLodgedate() {
			return lodgedate;
		}
		public void setLodgedate(String lodgedate) {
			this.lodgedate = lodgedate;
		}
		public String getApptype() {
			return apptype;
		}
		public void setApptype(String apptype) {
			this.apptype = apptype;
		}
		public String getZonename() {
			return zonename;
		}
		public void setZonename(String zonename) {
			this.zonename = zonename;
		}
		public String getBankname() {
			return bankname;
		}
		public void setBankname(String bankname) {
			this.bankname = bankname;
		}
		public String getAppReferNo() {
			return appReferNo;
		}
		public void setAppReferNo(String appReferNo) {
			this.appReferNo = appReferNo;
		}
		public String getProposalInvstGrade() {
			return proposalInvstGrade;
		}
		public void setProposalInvstGrade(String proposalInvstGrade) {
			this.proposalInvstGrade = proposalInvstGrade;
		}
		public String getTotalScoreGradePYAPAAppraial() {
			return totalScoreGradePYAPAAppraial;
		}
		public void setTotalScoreGradePYAPAAppraial(String totalScoreGradePYAPAAppraial) {
			this.totalScoreGradePYAPAAppraial = totalScoreGradePYAPAAppraial;
		}
		public String getTotalScoreGradeCYAPAAppraial() {
			return totalScoreGradeCYAPAAppraial;
		}
		public void setTotalScoreGradeCYAPAAppraial(String totalScoreGradeCYAPAAppraial) {
			this.totalScoreGradeCYAPAAppraial = totalScoreGradeCYAPAAppraial;
		}
		public String getCommentsOnScorePYAPAAppraial() {
			return commentsOnScorePYAPAAppraial;
		}
		public void setCommentsOnScorePYAPAAppraial(String commentsOnScorePYAPAAppraial) {
			this.commentsOnScorePYAPAAppraial = commentsOnScorePYAPAAppraial;
		}
		public String getCommentsOnScoreCYAPAAppraial() {
			return commentsOnScoreCYAPAAppraial;
		}
		public void setCommentsOnScoreCYAPAAppraial(String commentsOnScoreCYAPAAppraial) {
			this.commentsOnScoreCYAPAAppraial = commentsOnScoreCYAPAAppraial;
		}
		public String getWorkingCapitalLimitExisting() {
			return workingCapitalLimitExisting;
		}
		public void setWorkingCapitalLimitExisting(String workingCapitalLimitExisting) {
			this.workingCapitalLimitExisting = workingCapitalLimitExisting;
		}
		public String getWorkingCapitalLimitProposed() {
			return workingCapitalLimitProposed;
		}
		public void setWorkingCapitalLimitProposed(String workingCapitalLimitProposed) {
			this.workingCapitalLimitProposed = workingCapitalLimitProposed;
		}
		public String getWorkingCapitalLimitnonfundProposed() {
			return workingCapitalLimitnonfundProposed;
		}
		public void setWorkingCapitalLimitnonfundProposed(
				String workingCapitalLimitnonfundProposed) {
			this.workingCapitalLimitnonfundProposed = workingCapitalLimitnonfundProposed;
		}
		public String getWorkingCapitalLimittotFundNonFundProposed() {
			return workingCapitalLimittotFundNonFundProposed;
		}
		public void setWorkingCapitalLimittotFundNonFundProposed(
				String workingCapitalLimittotFundNonFundProposed) {
			this.workingCapitalLimittotFundNonFundProposed = workingCapitalLimittotFundNonFundProposed;
		}
		public String getWorkingCapitalLimitComments() {
			return workingCapitalLimitComments;
		}
		public void setWorkingCapitalLimitComments(String workingCapitalLimitComments) {
			this.workingCapitalLimitComments = workingCapitalLimitComments;
		}
		public String getWorkingCapitalLimitStatus() {
			return workingCapitalLimitStatus;
		}
		public void setWorkingCapitalLimitStatus(String workingCapitalLimitStatus) {
			this.workingCapitalLimitStatus = workingCapitalLimitStatus;
		}
		public String getExisting() {
			return existing;
		}
		public void setExisting(String existing) {
			this.existing = existing;
		}
		public String getProposed() {
			return proposed;
		}
		public void setProposed(String proposed) {
			this.proposed = proposed;
		}
		public String getNonfundbasedexisting() {
			return nonfundbasedexisting;
		}
		public void setNonfundbasedexisting(String nonfundbasedexisting) {
			this.nonfundbasedexisting = nonfundbasedexisting;
		}
		public void setTotexistingnonfundbased(String totexistingnonfundbased) {
			this.totexistingnonfundbased = totexistingnonfundbased;
		}
		public String getTotexistingnonfundbased() {
			return totexistingnonfundbased;
		}
		public String getNonfundbasedproposed() {
			return nonfundbasedproposed;
		}
		public void setNonfundbasedproposed(String nonfundbasedproposed) {
			this.nonfundbasedproposed = nonfundbasedproposed;
		}
		public String getTotfundnonbasedproposed() {
			return totfundnonbasedproposed;
		}
		public void setTotfundnonbasedproposed(String totfundnonbasedproposed) {
			this.totfundnonbasedproposed = totfundnonbasedproposed;
		}
			
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		
		public String getAvailabilityOfStockAsset() {
			return availabilityOfStockAsset;
		}
		public void setAvailabilityOfStockAsset(String availabilityOfStockAsset) {
			this.availabilityOfStockAsset = availabilityOfStockAsset;
		}
		public String getWorkingOfTheFactory() {
			return workingOfTheFactory;
		}
		public void setWorkingOfTheFactory(String workingOfTheFactory) {
			this.workingOfTheFactory = workingOfTheFactory;
		}
		public String getGeneralUpkeep() {
			return generalUpkeep;
		}
		public void setGeneralUpkeep(String generalUpkeep) {
			this.generalUpkeep = generalUpkeep;
		}
		public String getStockRegister() {
			return stockRegister;
		}
		public void setStockRegister(String stockRegister) {
			this.stockRegister = stockRegister;
		}
		public String getStockStatement() {
			return stockStatement;
		}
		public void setStockStatement(String stockStatement) {
			this.stockStatement = stockStatement;
		}
		public String getSatisfactoryCibilReports() {
			return satisfactoryCibilReports;
		}
		public void setSatisfactoryCibilReports(String satisfactoryCibilReports) {
			this.satisfactoryCibilReports = satisfactoryCibilReports;
		}
		public String getPddefaulter() {
			return pddefaulter;
		}
		public void setPddefaulter(String pddefaulter) {
			this.pddefaulter = pddefaulter;
		}
		public String getGroupAssociateNPA() {
			return groupAssociateNPA;
		}
		public void setGroupAssociateNPA(String groupAssociateNPA) {
			this.groupAssociateNPA = groupAssociateNPA;
		}
		public String getSatisfactoryPositionStatDues() {
			return satisfactoryPositionStatDues;
		}
		public void setSatisfactoryPositionStatDues(String satisfactoryPositionStatDues) {
			this.satisfactoryPositionStatDues = satisfactoryPositionStatDues;
		}
		public String getRepaymentTrackSatisfactory() {
			return repaymentTrackSatisfactory;
		}
		public void setRepaymentTrackSatisfactory(String repaymentTrackSatisfactory) {
			this.repaymentTrackSatisfactory = repaymentTrackSatisfactory;
		}
		public String getSatisfactoryCreditReports() {
			return satisfactoryCreditReports;
		}
		public void setSatisfactoryCreditReports(String satisfactoryCreditReports) {
			this.satisfactoryCreditReports = satisfactoryCreditReports;
		}
		public String getCgpan() {
			return cgpan;
		}
		public void setCgpan(String cgpan) {
			this.cgpan = cgpan;
		}
		public String getUnitName() {
			return unitName;
		}
		public void setUnitName(String unitName) {
			this.unitName = unitName;
		}
		public String getUnitPAN() {
			return unitPAN;
		}
		public void setUnitPAN(String unitPAN) {
			this.unitPAN = unitPAN;
		}
		public String getLineOfActivity() {
			return lineOfActivity;
		}
		public void setLineOfActivity(String lineOfActivity) {
			this.lineOfActivity = lineOfActivity;
		}
		public String getClassification() {
			return classification;
		}
		public void setClassification(String classification) {
			this.classification = classification;
		}
		public String getLoanAccountNumber() {
			return loanAccountNumber;
		}
		public void setLoanAccountNumber(String loanAccountNumber) {
			this.loanAccountNumber = loanAccountNumber;
		}
		public String getPromotersName() {
			return promotersName;
		}
		public void setPromotersName(String promotersName) {
			this.promotersName = promotersName;
		}
		public String getChiefPromoterPAN() {
			return chiefPromoterPAN;
		}
		public void setChiefPromoterPAN(String chiefPromoterPAN) {
			this.chiefPromoterPAN = chiefPromoterPAN;
		}
		public String getGuranteeStartDate() {
			return guranteeStartDate;
		}
		public void setGuranteeStartDate(String guranteeStartDate) {
			this.guranteeStartDate = guranteeStartDate;
		}
		public String getGuranteeExpiryDate() {
			return guranteeExpiryDate;
		}
		public void setGuranteeExpiryDate(String guranteeExpiryDate) {
			this.guranteeExpiryDate = guranteeExpiryDate;
		}
		public String getLastRenewalDateOfWC() {
			return lastRenewalDateOfWC;
		}
		public void setLastRenewalDateOfWC(String lastRenewalDateOfWC) {
			this.lastRenewalDateOfWC = lastRenewalDateOfWC;
		}
		public String getLoginUserId() {
			return loginUserId;
		}
		public void setLoginUserId(String loginUserId) {
			this.loginUserId = loginUserId;
		}
		public String getMemberId() {
			return memberId;
		}
		public void setMemberId(String memberId) {
			this.memberId = memberId;
		}
		public ArrayList getRenewalOfWorkingCapitalAfterTenYears() {
			return renewalOfWorkingCapitalAfterTenYears;
		}
		public void setRenewalOfWorkingCapitalAfterTenYears(
				ArrayList renewalOfWorkingCapitalAfterTenYears) {
			this.renewalOfWorkingCapitalAfterTenYears = renewalOfWorkingCapitalAfterTenYears;
		}
		/*@Override
		/*public String toString() {
			return "RenewalOfWorkingCapitalAfterTenYearsActionForm [memberId="
					+ memberId + ", loginUserId=" + loginUserId
					+ ", renewalOfWorkingCapitalAfterTenYears="
					+ renewalOfWorkingCapitalAfterTenYears + ", cgpan=" + cgpan
					+ ", unitName=" + unitName + ", unitPAN=" + unitPAN
					+ ", lineOfActivity=" + lineOfActivity
					+ ", classification=" + classification
					+ ", loanAccountNumber=" + loanAccountNumber
					+ ", promotersName=" + promotersName
					+ ", chiefPromoterPAN=" + chiefPromoterPAN + ", address="
					+ address + ", guranteeStartDate=" + guranteeStartDate
					+ ", guranteeExpiryDate=" + guranteeExpiryDate
					+ ", lastRenewalDateOfWC=" + lastRenewalDateOfWC
					+ ", existing=" + existing + ", nonfundbasedexisting="
					+ nonfundbasedexisting + ", totexistingnonfundbased="
					+ totexistingnonfundbased + ", proposed=" + proposed
					+ ", nonfundbasedproposed=" + nonfundbasedproposed
					+ ", totfundnonbasedproposed=" + totfundnonbasedproposed
					+ ", satisfactoryCreditReports="
					+ satisfactoryCreditReports + ", satisfactoryCibilReports="
					+ satisfactoryCibilReports + ", pddefaulter=" + pddefaulter
					+ ", groupAssociateNPA=" + groupAssociateNPA
					+ ", satisfactoryPositionStatDues="
					+ satisfactoryPositionStatDues
					+ ", repaymentTrackSatisfactory="
					+ repaymentTrackSatisfactory
					+ ", availabilityOfStockAsset=" + availabilityOfStockAsset
					+ ", workingOfTheFactory=" + workingOfTheFactory
					+ ", generalUpkeep=" + generalUpkeep + ", stockRegister="
					+ stockRegister + ", stockStatement=" + stockStatement
					+ ", proposalInvstGrade=" + proposalInvstGrade
					+ ", totalScoreGradePYAPAAppraial="
					+ totalScoreGradePYAPAAppraial
					+ ", totalScoreGradeCYAPAAppraial="
					+ totalScoreGradeCYAPAAppraial
					+ ", commentsOnScorePYAPAAppraial="
					+ commentsOnScorePYAPAAppraial
					+ ", commentsOnScoreCYAPAAppraial="
					+ commentsOnScoreCYAPAAppraial
					+ ", workingCapitalLimitExisting="
					+ workingCapitalLimitExisting
					+ ", workingCapitalLimitProposed="
					+ workingCapitalLimitProposed
					+ ", workingCapitalLimitnonfundProposed="
					+ workingCapitalLimitnonfundProposed
					+ ", workingCapitalLimittotFundNonFundProposed="
					+ workingCapitalLimittotFundNonFundProposed
					+ ", workingCapitalLimitComments="
					+ workingCapitalLimitComments
					+ ", workingCapitalLimitStatus="
					+ workingCapitalLimitStatus + ", appReferNo=" + appReferNo
					+ ", bankname=" + bankname + ", zonename=" + zonename
					+ ", lodgedate=" + lodgedate + ", apptype=" + apptype
					+ ", appstatus=" + appstatus + ", firstCounter="
					+ firstCounter + ", firstInstallmentClaims="
					+ firstInstallmentClaims + ", clmRefDtlSet=" + clmRefDtlSet
					+ ", mlicomments=" + mlicomments + ", mlidecision="
					+ mlidecision + ", statuss=" + statuss + ", statuslist="
					+ statuslist + "]";
		}
		*/
		public ArrayList getRenewalWCAllFormData() {
			System.out.println("GetData==="+renewalWCAllFormData);
			return renewalWCAllFormData;
		}
		public void setRenewalWCAllFormData(ArrayList renewalWCAllFormData) {
			this.renewalWCAllFormData = renewalWCAllFormData;
			System.out.println("SetData==="+renewalWCAllFormData);
		}
			
			
	}
