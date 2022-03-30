<%@ page import="com.cgtsi.util.SessionConstants"%>
<%@ taglib uri="/WEB-INF/TLD/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/TLD/struts-bean.tld" prefix="bean"%>
<script type="text/javascript">

function calltotalAmt()
{
var sanctionedamt=document.getElementById('termCreditSanctioned');
var fundbasedamt=document.getElementById('wcFundBasedSanctioned');
var nonfundbasedamt=document.getElementById('wcNonFundBasedSanctioned');

if (!(isNaN(sanctionedamt.value)) && (sanctionedamt.value != ""))
	{
sanctionedamtValue=parseFloat(sanctionedamt.value);
	}
	else
	{
	sanctionedamtValue=0;
	}

if (!(isNaN(fundbasedamt.value)) && (fundbasedamt.value != ""))
	{
fundbasedamtValue=parseFloat(fundbasedamt.value);
	}
	else
	{
	fundbasedamtValue=0;
	}
if (!(isNaN(nonfundbasedamt.value)) && (nonfundbasedamt.value != ""))
	{
nonfundbasedamtValue=parseFloat(nonfundbasedamt.value);
	}
else
	{
	nonfundbasedamtValue=0;
	}
	//alert("sukant");
var amtVal=sanctionedamtValue+fundbasedamtValue+nonfundbasedamtValue;
//alert("sukant"+amtVal);
		if (amtVal>1000000)
		{
		document.forms[0].agree.disabled=false;
		document.forms[0].agree.checked=false;
		}
		else{
		document.forms[0].agree.disabled=true;
		document.forms[0].agree.checked=false;
		}			
		alert("sanctionedamtValue:"+sanctionedamtValue+"  fundbasedamtValue:"+fundbasedamtValue);	
var total_amount_for_rating = sanctionedamtValue + fundbasedamtValue;
					 
		if(parseFloat(total_amount_for_rating) > 5000000 || parseFloat(total_amount_for_rating) == 5000000){
		alert("internal rating value "+document.getElementById('internalRating').value);
		
			if(document.getElementById('internalRatingField').value == 'NA'){
				document.getElementById('internalRatingField').innerText = '';
			}
			/* document.getElementById('internalRating').readOnly = false; */
			document.getElementById('internalRatingField').disabled = false;
			//document.forms[0].internalRating.disabled = false;
		    document.forms[0].internalratingProposal[0].disabled = false;			
			document.forms[0].internalratingProposal[1].disabled = false;	
			document.forms[0].internalratingProposal[0].checked = false;			
			document.forms[0].internalratingProposal[1].checked = false;			
		}else{
		 	document.getElementById('internalRatingField').innerText = 'NA';
			document.getElementById('internalRatingField').disabled = true;
			//document.forms[0].internalRating.disabled = true;
		 	document.forms[0].internalratingProposal[0].disabled = true;			
			document.forms[0].internalratingProposal[1].disabled = true; 			
			document.forms[0].internalratingProposal[0].checked = false;			
			document.forms[0].internalratingProposal[1].checked = true; 		
			document.forms[0].investmentGrade[0].disabled = true;
			document.forms[0].investmentGrade[1].disabled = true;
			document.forms[0].investmentGrade[0].checked = false;	
			document.forms[0].investmentGrade[1].checked = false;								
		}
}

	function enableOnActivityConfirm(flag){
	
		if(flag == 'Y'){
			//disable all fields
			alert("disan;le");
			document.forms[0].projectedSalesTurnover.disabled = true;
			document.forms[0].projectedSalesTurnover.value = '';
			document.forms[0].projectedExports.disabled = true;
			document.forms[0].projectedExports.value = '';
			document.forms[0].cpTitle.disabled = true;
			document.forms[0].cpFirstName.disabled = true;
			document.forms[0].cpFirstName.value = '';
			document.forms[0].cpLastName.disabled = true;
			document.forms[0].cpLastName.value = '';
			document.forms[0].cpMiddleName.disabled = true;
			document.forms[0].cpMiddleName.value = '';
			document.forms[0].cpFMTitle.disabled = true;
			document.forms[0].cpFMFirstName.disabled = true;
			document.forms[0].cpFMFirstName.value = '';
			document.forms[0].cpFMLastName.disabled = true;
			document.forms[0].cpFMLastName.value = '';
			document.forms[0].cpFMMiddleName.disabled = true;
			document.forms[0].cpFMMiddleName.value = '';
			document.forms[0].cpGender[0].disabled = true;
			document.forms[0].cpGender[1].disabled = true;
			document.forms[0].cpITPAN.disabled = true;
			document.forms[0].cpITPAN.value = '';
			document.forms[0].cpDOB.disabled = true;
			document.forms[0].cpDOB.value = '';
			//document.forms[0].internalRating.disabled = true;
			//document.forms[0].internalRating.value = '';
			document.getElementById('internalRatingField').disabled = true;
			document.getElementById('internalRatingField').value = 'NA';
			document.forms[0].internalratingProposal[0].disabled = true;
			document.forms[0].internalratingProposal[1].disabled = true;
			document.forms[0].investmentGrade[0].disabled = true;
			document.forms[0].investmentGrade[1].disabled = true;
			
			document.forms[0].landParticulars.disabled = true;
			document.forms[0].landParticulars.value = '';
			document.forms[0].landValue.disabled = true;
			document.forms[0].landValue.value = '';
			document.forms[0].bldgParticulars.disabled = true;
			document.forms[0].bldgParticulars.value = '';
			document.forms[0].bldgValue.disabled = true;
			document.forms[0].bldgValue.value = '';
			document.forms[0].machineParticulars.disabled = true;
			document.forms[0].machineParticulars.value = '';
			document.forms[0].machineValue.disabled = true;
			document.forms[0].machineValue.value = '';
			document.forms[0].assetsParticulars.disabled = true;
			document.forms[0].assetsParticulars.value = '';
			document.forms[0].assetsValue.disabled = true;
			document.forms[0].assetsValue.value = '';
			document.forms[0].currentAssetsParticulars.disabled = true;
			document.forms[0].currentAssetsParticulars.value = '';
			document.forms[0].currentAssetsValue.disabled = true;
			document.forms[0].currentAssetsValue.value = '';
			document.forms[0].othersParticulars.disabled = true;
			document.forms[0].othersParticulars.value = '';
			document.forms[0].othersValue.disabled = true;
			document.forms[0].othersValue.value = '';
			document.getElementById('psTotalWorth').innerHTML = '';
		
			document.forms[0].termCreditSanctioned.disabled = true;
			document.forms[0].termCreditSanctioned.value = '';
			document.forms[0].wcFundBasedSanctioned.disabled = true;
			document.forms[0].wcFundBasedSanctioned.value = '';
			document.forms[0].wcNonFundBasedSanctioned.disabled = true;
			document.forms[0].wcNonFundBasedSanctioned.value = '';
			document.forms[0].tcPromoterContribution.disabled = true;
			document.forms[0].tcPromoterContribution.value = '';
			document.forms[0].wcPromoterContribution.disabled = true;
			document.forms[0].wcPromoterContribution.value = '';
			document.forms[0].tcSubsidyOrEquity.disabled = true;
			document.forms[0].tcSubsidyOrEquity.value = '';
			document.forms[0].wcSubsidyOrEquity.disabled = true;
			document.forms[0].wcSubsidyOrEquity.value = '';
			document.forms[0].tcOthers.disabled = true;
			document.forms[0].tcOthers.value = '';
			document.forms[0].wcOthers.disabled = true;
			document.forms[0].wcOthers.value = '';
			document.getElementById('projectcost').innerHTML = '';
			document.getElementById('wcassessed').innerHTML = '';
			document.getElementById('projectoutlay').innerHTML = '';
			document.forms[0].expiryDate.disabled = true;
			document.forms[0].expiryDate.value = '';
			document.forms[0].remarks.disabled = true;
			document.forms[0].remarks.value = '';
		
//<!-- properties from securitizationDetails -->		
			document.forms[0].spreadOverPLR.disabled = true;
			document.forms[0].spreadOverPLR.value = '';
			document.forms[0].pplRepaymentInEqual[0].disabled = true;	
			document.forms[0].pplRepaymentInEqual[1].disabled = true;
			document.forms[0].tangibleNetWorth.disabled = true;
			document.forms[0].tangibleNetWorth.value = '';
			document.forms[0].fixedACR.disabled = true;
			document.forms[0].fixedACR.value = '';
			document.forms[0].currentRatio.disabled = true;
			document.forms[0].currentRatio.value = '';
			document.forms[0].minimumDSCR.disabled = true;	
			document.forms[0].minimumDSCR.value = '';
			document.forms[0].avgDSCR.disabled = true;	
			document.forms[0].avgDSCR.value = '';
		
		}
		if(flag == 'N'){
			//enable all fields
			alert("enale");
			document.forms[0].projectedSalesTurnover.disabled = false;
			document.forms[0].projectedExports.disabled = false;
			document.forms[0].cpTitle.disabled = false;
			document.forms[0].cpFirstName.disabled = false;
			document.forms[0].cpLastName.disabled = false;
			document.forms[0].cpMiddleName.disabled = false;
			document.forms[0].cpFMTitle.disabled = false;
			document.forms[0].cpFMFirstName.disabled = false;
			document.forms[0].cpFMLastName.disabled = false;
			document.forms[0].cpFMMiddleName.disabled = false;
			document.forms[0].cpGender[0].disabled = false;
			document.forms[0].cpGender[1].disabled = false;
			document.forms[0].cpITPAN.disabled = false;
			document.forms[0].cpDOB.disabled = false;
			//document.forms[0].internalRating.disabled = false;
		
			document.forms[0].internalratingProposal[0].disabled = false;
			document.forms[0].internalratingProposal[1].disabled = false;
			document.forms[0].investmentGrade[0].disabled = false;
			document.forms[0].investmentGrade[1].disabled = false;
			
			document.forms[0].landParticulars.disabled = false;
			document.forms[0].landValue.disabled = false;
			document.forms[0].bldgParticulars.disabled = false;
			document.forms[0].bldgValue.disabled = false;
			document.forms[0].machineParticulars.disabled = false;
			document.forms[0].machineValue.disabled = false;
			document.forms[0].assetsParticulars.disabled = false;
			document.forms[0].assetsValue.disabled = false;
			document.forms[0].currentAssetsParticulars.disabled = false;
			document.forms[0].currentAssetsValue.disabled = false;
			document.forms[0].othersParticulars.disabled = false;
			document.forms[0].othersValue.disabled = false;
		
			document.forms[0].termCreditSanctioned.disabled = false;
			document.forms[0].wcFundBasedSanctioned.disabled = false;
			document.forms[0].wcNonFundBasedSanctioned.disabled = false;
			document.forms[0].tcPromoterContribution.disabled = false;
			document.forms[0].wcPromoterContribution.disabled = false;
			document.forms[0].tcSubsidyOrEquity.disabled = false;
			document.forms[0].wcSubsidyOrEquity.disabled = false;
			document.forms[0].tcOthers.disabled = false;
			document.forms[0].wcOthers.disabled = false;
			document.forms[0].expiryDate.disabled = false;
	
			
		//<!-- properties from securitizationDetails -->	
			document.forms[0].spreadOverPLR.disabled = false;
			document.forms[0].pplRepaymentInEqual[0].disabled = false;	
			document.forms[0].pplRepaymentInEqual[1].disabled = false;
			document.forms[0].tangibleNetWorth.disabled = false;
			document.forms[0].fixedACR.disabled = false;
			document.forms[0].currentRatio.disabled = false;
			document.forms[0].minimumDSCR.disabled = false;	
			document.forms[0].avgDSCR.disabled = false;	
			
		}
	}
	
	function checkssiNameAndPromoterName(){
	var unitName = document.forms[0].ssiName.value;
	alert("unit name"+unitName);
		if(document.forms[0].cpMiddleName.value !="" || document.forms[0].cpMiddleName.value != null){
			var chiefPromoterName = document.forms[0].cpFirstName.value + " "+ document.forms[0].cpMiddleName.value + " " + document.forms[0].cpLastName.value;
		}else{
			var chiefPromoterName = document.forms[0].cpFirstName.value + " " + document.forms[0].cpLastName.value;
		}
		if(unitName!="" || unitName!=null){
			unitName = unitName.replace(/\s/g,'');   
		}
		if(chiefPromoterName!="" || chiefPromoterName!=null){
			chiefPromoterName = chiefPromoterName.replace(/\s/g,'');   
		}
			
		if(unitName==chiefPromoterName){
			var flag = confirm("Is the concerns name and the promoters name same?");
				if(flag==true){					
						/* document.forms[0].ssiName.focus();						
						alert("please enter different concern name");
						return false; */
					}else{
						document.forms[0].ssiName.focus();
						alert("please enter different concern name");
						return false;
					}
		}
}

function setConstitutionDate(){
		var option = document.forms[0].constitution.value;
		if("Partnership" == option || "Limited liability Partnership" == option || "private" == option){
			document.forms[0].PartnershipDeedDate.disabled = false;
		}else{
			document.forms[0].PartnershipDeedDate.disabled = true;
			document.forms[0].PartnershipDeedDate.value = '';
		}

}

function enableForInternalRatingProposal(flag){

//var loanType = document.getElementById('loanType').innerHTML;
//alert("loan type:"+loanType);
	if(flag === 'Y'){	
		
		document.forms[0].investmentGrade[0].disabled = false;
		document.forms[0].investmentGrade[1].disabled = false;
		document.forms[0].investmentGrade[0].checked = false;
		document.forms[0].investmentGrade[1].checked = false;

			document.forms[0].landParticulars.disabled = false;
			document.forms[0].landValue.disabled = false;
			document.forms[0].bldgParticulars.disabled = false;
			document.forms[0].bldgValue.disabled = false;
			document.forms[0].machineParticulars.disabled = false;
			document.forms[0].machineValue.disabled = false;
			document.forms[0].assetsParticulars.disabled = false;
			document.forms[0].assetsValue.disabled = false;
			document.forms[0].currentAssetsParticulars.disabled = false;
			document.forms[0].currentAssetsValue.disabled = false;
			document.forms[0].othersParticulars.disabled = false;
			document.forms[0].othersValue.disabled = false;
		
			document.forms[0].termCreditSanctioned.disabled = false;
			document.forms[0].wcFundBasedSanctioned.disabled = false;
			document.forms[0].wcNonFundBasedSanctioned.disabled = false;
			document.forms[0].tcPromoterContribution.disabled = false;
			document.forms[0].wcPromoterContribution.disabled = false;
			document.forms[0].tcSubsidyOrEquity.disabled = false;
			document.forms[0].wcSubsidyOrEquity.disabled = false;
			document.forms[0].tcOthers.disabled = false;
			document.forms[0].wcOthers.disabled = false;
			document.forms[0].expiryDate.disabled = false;
			//<!-- properties from securitizationDetails -->	
		document.forms[0].spreadOverPLR.disabled = false;
		document.forms[0].pplRepaymentInEqual[0].disabled = false;	
		document.forms[0].pplRepaymentInEqual[1].disabled = false;
		document.forms[0].tangibleNetWorth.disabled = false;
		document.forms[0].fixedACR.disabled = false;
		document.forms[0].currentRatio.disabled = false;
		document.forms[0].minimumDSCR.disabled = false;	
		document.forms[0].avgDSCR.disabled = false;	
		
			
	}
	
	if(flag === 'N'){	
		document.forms[0].investmentGrade[0].disabled = true;
		document.forms[0].investmentGrade[1].disabled = true;
		document.forms[0].investmentGrade[0].checked = false;
		document.forms[0].investmentGrade[1].checked = false;

			document.forms[0].landParticulars.disabled = true;
			document.forms[0].landParticulars.value = '';
			document.forms[0].landValue.disabled = true;
			document.forms[0].landValue.value = '';
			document.forms[0].bldgParticulars.disabled = true;
			document.forms[0].bldgParticulars.value = '';
			document.forms[0].bldgValue.disabled = true;
			document.forms[0].bldgValue.value = '';
			document.forms[0].machineParticulars.disabled = true;
			document.forms[0].machineParticulars.value = '';
			document.forms[0].machineValue.disabled = true;
			document.forms[0].machineValue.value = '';
			document.forms[0].assetsParticulars.disabled = true;
			document.forms[0].assetsParticulars.value = '';
			document.forms[0].assetsValue.disabled = true;
			document.forms[0].assetsValue.value = '';
			document.forms[0].currentAssetsParticulars.disabled = true;
			document.forms[0].currentAssetsParticulars.value = '';
			document.forms[0].currentAssetsValue.disabled = true;
			document.forms[0].currentAssetsValue.value = '';
			document.forms[0].othersParticulars.disabled = true;
			document.forms[0].othersParticulars.value = '';
			document.forms[0].othersValue.disabled = true;
			document.forms[0].othersValue.value = '';
			document.getElementById('psTotalWorth').innerHTML = '';
		
			document.forms[0].termCreditSanctioned.disabled = true;
			document.forms[0].termCreditSanctioned.value = '';
			document.forms[0].wcFundBasedSanctioned.disabled = true;
			document.forms[0].wcFundBasedSanctioned.value = '';
			document.forms[0].wcNonFundBasedSanctioned.disabled = true;
			document.forms[0].wcNonFundBasedSanctioned.value = '';
			document.forms[0].tcPromoterContribution.disabled = true;
			document.forms[0].tcPromoterContribution.value = '';
			document.forms[0].wcPromoterContribution.disabled = true;
			document.forms[0].wcPromoterContribution.value = '';
			document.forms[0].tcSubsidyOrEquity.disabled = true;
			document.forms[0].tcSubsidyOrEquity.value = '';
			document.forms[0].wcSubsidyOrEquity.disabled = true;
			document.forms[0].wcSubsidyOrEquity.value = '';
			document.forms[0].tcOthers.disabled = true;
			document.forms[0].tcOthers.value = '';
			document.forms[0].wcOthers.disabled = true;
			document.forms[0].wcOthers.value = '';
			document.getElementById('projectcost').innerHTML = '';
			document.getElementById('wcassessed').innerHTML = '';
			document.getElementById('projectoutlay').innerHTML = '';
			document.forms[0].expiryDate.disabled = true;
			document.forms[0].expiryDate.value = '';
		document.forms[0].remarks.disabled = true;
		document.forms[0].remarks.value = '';
		//<!-- properties from securitizationDetails -->		
		document.forms[0].spreadOverPLR.disabled = true;
		document.forms[0].spreadOverPLR.value = '';
		document.forms[0].pplRepaymentInEqual[0].disabled = true;	
		document.forms[0].pplRepaymentInEqual[1].disabled = true;
		document.forms[0].tangibleNetWorth.disabled = true;
		document.forms[0].tangibleNetWorth.value = '';
		document.forms[0].fixedACR.disabled = true;
		document.forms[0].fixedACR.value = '';
		document.forms[0].currentRatio.disabled = true;
		document.forms[0].currentRatio.value = '';
		document.forms[0].minimumDSCR.disabled = true;	
		document.forms[0].minimumDSCR.value = '';
		document.forms[0].avgDSCR.disabled = true;	
		document.forms[0].avgDSCR.value = '';	
						
	}
}

function enableForInvestmentGrade(flag){
	
	if(flag === 'Y'){	
		
			document.forms[0].landParticulars.disabled = false;
			document.forms[0].landValue.disabled = false;
			document.forms[0].bldgParticulars.disabled = false;
			document.forms[0].bldgValue.disabled = false;
			document.forms[0].machineParticulars.disabled = false;
			document.forms[0].machineValue.disabled = false;
			document.forms[0].assetsParticulars.disabled = false;
			document.forms[0].assetsValue.disabled = false;
			document.forms[0].currentAssetsParticulars.disabled = false;
			document.forms[0].currentAssetsValue.disabled = false;
			document.forms[0].othersParticulars.disabled = false;
			document.forms[0].othersValue.disabled = false;
		
			document.forms[0].termCreditSanctioned.disabled = false;
			document.forms[0].wcFundBasedSanctioned.disabled = false;
			document.forms[0].wcNonFundBasedSanctioned.disabled = false;
			document.forms[0].tcPromoterContribution.disabled = false;
			document.forms[0].wcPromoterContribution.disabled = false;
			document.forms[0].tcSubsidyOrEquity.disabled = false;
			document.forms[0].wcSubsidyOrEquity.disabled = false;
			document.forms[0].tcOthers.disabled = false;
			document.forms[0].wcOthers.disabled = false;
			document.forms[0].expiryDate.disabled = false;
	
			
		//<!-- properties from securitizationDetails -->	
		document.forms[0].spreadOverPLR.disabled = false;
		document.forms[0].pplRepaymentInEqual[0].disabled = false;	
		document.forms[0].pplRepaymentInEqual[1].disabled = false;
		document.forms[0].tangibleNetWorth.disabled = false;
		document.forms[0].fixedACR.disabled = false;
		document.forms[0].currentRatio.disabled = false;
		document.forms[0].minimumDSCR.disabled = false;	
		document.forms[0].avgDSCR.disabled = false;	
		document.forms[0].remarks.value = '';	
	}
	
	if(flag === 'N'){	
		document.getElementById("projectCost").innerText = '';
		document.getElementById("projectOutlay").innerText = '';
		document.getElementById("wcAssessed").innerText = '';		
	//<!--	document.forms[0].investmentGrade[1].checked = true; -->
			document.forms[0].landParticulars.disabled = true;
			document.forms[0].landParticulars.value = '';
			document.forms[0].landValue.disabled = true;
			document.forms[0].landValue.value = '';
			document.forms[0].bldgParticulars.disabled = true;
			document.forms[0].bldgParticulars.value = '';
			document.forms[0].bldgValue.disabled = true;
			document.forms[0].bldgValue.value = '';
			document.forms[0].machineParticulars.disabled = true;
			document.forms[0].machineParticulars.value = '';
			document.forms[0].machineValue.disabled = true;
			document.forms[0].machineValue.value = '';
			document.forms[0].assetsParticulars.disabled = true;
			document.forms[0].assetsParticulars.value = '';
			document.forms[0].assetsValue.disabled = true;
			document.forms[0].assetsValue.value = '';
			document.forms[0].currentAssetsParticulars.disabled = true;
			document.forms[0].currentAssetsParticulars.value = '';
			document.forms[0].currentAssetsValue.disabled = true;
			document.forms[0].currentAssetsValue.value = '';
			document.forms[0].othersParticulars.disabled = true;
			document.forms[0].othersParticulars.value = '';
			document.forms[0].othersValue.disabled = true;
			document.forms[0].othersValue.value = '';
			document.getElementById('psTotalWorth').innerHTML = '';
		
			document.forms[0].termCreditSanctioned.disabled = true;
			document.forms[0].termCreditSanctioned.value = '';
			document.forms[0].wcFundBasedSanctioned.disabled = true;
			document.forms[0].wcFundBasedSanctioned.value = '';
			document.forms[0].wcNonFundBasedSanctioned.disabled = true;
			document.forms[0].wcNonFundBasedSanctioned.value = '';
			document.forms[0].tcPromoterContribution.disabled = true;
			document.forms[0].tcPromoterContribution.value = '';
			document.forms[0].wcPromoterContribution.disabled = true;
			document.forms[0].wcPromoterContribution.value = '';
			document.forms[0].tcSubsidyOrEquity.disabled = true;
			document.forms[0].tcSubsidyOrEquity.value = '';
			document.forms[0].wcSubsidyOrEquity.disabled = true;
			document.forms[0].wcSubsidyOrEquity.value = '';
			document.forms[0].tcOthers.disabled = true;
			document.forms[0].tcOthers.value = '';
			document.forms[0].wcOthers.disabled = true;
			document.forms[0].wcOthers.value = '';
			document.getElementById('projectcost').innerHTML = '';
			document.getElementById('wcassessed').innerHTML = '';
			document.getElementById('projectoutlay').innerHTML = '';
			document.forms[0].expiryDate.disabled = true;
			document.forms[0].expiryDate.value = '';
			document.forms[0].remarks.disabled = true;
			document.forms[0].remarks.value = '';
		
//<!-- properties from securitizationDetails -->		
		document.forms[0].spreadOverPLR.disabled = true;
		document.forms[0].spreadOverPLR.value = '';
		document.forms[0].pplRepaymentInEqual[0].disabled = true;	
		document.forms[0].pplRepaymentInEqual[1].disabled = true;
		document.forms[0].tangibleNetWorth.disabled = true;
		document.forms[0].tangibleNetWorth.value = '';
		document.forms[0].fixedACR.disabled = true;
		document.forms[0].fixedACR.value = '';
		document.forms[0].currentRatio.disabled = true;
		document.forms[0].currentRatio.value = '';
		document.forms[0].minimumDSCR.disabled = true;	
		document.forms[0].minimumDSCR.value = '';
		document.forms[0].avgDSCR.disabled = true;	
		document.forms[0].avgDSCR.value = '';					
	}
}

/*function YesClicked()
{
	if(appForm.pSecurityY.checked==true)// || appForm.pSecurityN.checked==true)
	{
		appForm.pSecurityN.checked=false;
		return false;
	}
	/*if(appForm.pSecurityY.checked==true)
	{
		appForm.pSecurityN.checked=false;
		return false;
	}
}*/
/*function NoClicked()
{
	if(appForm.pSecurityY.checked==true && appForm.pSecurityN.checked==true)
	{
		appForm.pSecurityY.checked=false;
		return false;
	}
}*/

</script>
<TABLE width="100%" border="0" cellspacing="1" cellpadding="0">
<tr>
			<TD  align="left" colspan="4"><font size="2"><bold>
				Fields marked as </font><font color="#FF0000" size="2">*</font><font size="2"> are mandatory</bold></font>
			</td>
		</tr>
								<TR>
									<TD colspan="4"> 
										<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
											<TR>
												<TD width="31%" class="Heading"><bean:message key="applicationHeader" /></TD>
												<TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
											</TR>
											<TR>
												<TD colspan="4" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
											</TR>
										</TABLE>
									</TD>
								</TR>
								<TR align="left">
									<TD align="left" valign="top" class="ColumnBackground">
									<font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="bankRefNo"/>
									</td>								
									<TD align="left" valign="top" class="tableData">
										<%	

											String appCommonFlag=(String)session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG);										
											if((appCommonFlag.equals("1"))||(appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")))
											{										
												
										%>
											<bean:write name="appForm" property="mliRefNo"/>

										<%	}										
											else
											{%>
												<html:text property="mliRefNo" size="20" alt="Bank Reference No" name="appForm" maxlength="12" />
											<%}%>

									</TD>
								 </TR>
								 <TR align="left">
									<TD align="left" valign="top" class="ColumnBackground"><font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="branchName"/></td>								
									<TD align="left" valign="top" class="tableData">
									<%
									if((appCommonFlag.equals("1"))||(appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")))
									{
									%>
									<bean:write name="appForm" property="mliBranchName"/>
									<%	}
										else
										{ %><html:text property="mliBranchName" size="20" alt="branch Name" name="appForm" maxlength="100" />
										<%}%>
									</TD>
								 </TR>
								<TR align="left">
									<TD align="left" valign="top" class="ColumnBackground"><bean:message key="branchCode"/></td>								
									<TD align="left" valign="top" class="tableData">
									<%
										if((appCommonFlag.equals("1"))||(appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")))
										{
									%>
										<bean:write name="appForm" property="mliBranchCode"/>
									<%	}
										else
										{ %>
										<html:text property="mliBranchCode" size="20" alt="Branch Code"  onkeypress="return numbersOnly(this, event)" onkeyup="isValidNumber(this)" name="appForm" maxlength="10" />
										<%}%>
									</TD>
								 </TR>								
							</TABLE>		<!--closing for app details-->
							

	<!--table starting --><TABLE width="100%" border="0" cellspacing="1" cellpadding="1">
								<TR>
									<TD colspan="7">
										<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
											<TR>
												<TD width="31%" class="Heading"><bean:message key="borrowerHeader" /></TD>
												<TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>
											</TR>
											<TR>
												<TD colspan="6" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
											</TR>
										</TABLE>
									</TD>
								</TR>
								
								<%--<TR align="left">
									<TD align="left" valign="top" class="ColumnBackground" colspan="3">
										<font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="bankAssistance" />
									</TD>
									<TD align="left" class="TableData" colspan="4"> 
									<%
									if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
									|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13"))  || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))
									||(appCommonFlag.equals("19")))
									{
										%>
							<bean:write property="assistedByBank" name="appForm"/>
							
											<html:radio name="appForm" value="Y" property="assistedByBank" disabled="true"></html:radio>
											
											<bean:message key="yes" />&nbsp;&nbsp;

											
											<html:radio name="appForm" value="N" property="assistedByBank" disabled="true"></html:radio>
											
											<bean:message key="no"/>

												
										<%}
											else {
											%>
											<html:radio name="appForm" value="Y" property="assistedByBank" onclick="javascript:enableAssistance()"></html:radio>
											
											<bean:message key="yes" />&nbsp;&nbsp;

											
												<html:radio name="appForm" value="N" property="assistedByBank" onclick="javascript:enableAssistance()"></html:radio>
											
											<bean:message key="no"/>
											
											<%}%>
									</TD>								
								</TR>
								<TR align="left">
									<TD align="left" valign="top" class="ColumnBackground" colspan="3"><bean:message key="osAmt"/></td>								
									<TD align="left" valign="top" class="TableData" colspan="4">
									<%
										if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
											|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))
									||(appCommonFlag.equals("19")))

											{
										%>
											<bean:write name="appForm" property="osAmt"/>

										<%	}
											else
											{ %><html:text property="osAmt" size="20" alt="Outstanding Amount" name="appForm" maxlength="16" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)"/>
											<%}%>
											<bean:message key="inRs"/>
									</TD>
								 </TR>
								 <TR align="left">
									<TD align="left" valign="top" class="ColumnBackground" colspan="3">
										<bean:message key="npa" />
									</TD>
									<TD align="left" class="TableData" colspan="4"> 
									<%
									if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
									|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))
									||(appCommonFlag.equals("19")))
									{
										%>
											<bean:write property="npa" name="appForm"/>
																							
										<%}
											else {
											%>
									
											<html:radio name="appForm" value="Y" property="npa" ></html:radio>
											
											<bean:message key="yes" />&nbsp;&nbsp;
											<html:radio name="appForm" value="N" property="npa" ></html:radio>
												
											<bean:message key="no"/>
										<%}%>
									</TD>								
								</TR>
						--%>
								
								

								<TR>
									<TD colspan="8">
										<TABLE width="100%" border="0" cellpadding="1" cellspacing="1">	
											<TR align="left">
												<TD align="left" valign="top" class="ColumnBackground">
													<font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="coveredByCGTSI" />
												</TD>
												<TD align="left" class="TableData" colspan="6">

												 <% if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
														|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))
												||(appCommonFlag.equals("19")))
												 {
												%>
												<html:radio name="appForm" value="Y" property="previouslyCovered" disabled="true"></html:radio>
														
														<bean:message key="yes" />&nbsp;&nbsp;
														
														<html:radio name="appForm" value="N" property="previouslyCovered"  disabled="true"></html:radio>
														
														<bean:message key="no"/>
												<%}
												else {
												%>
												
														<html:radio name="appForm" value="Y" property="previouslyCovered" onclick="enableNone()"></html:radio>
														
														<bean:message key="yes" />&nbsp;&nbsp;
														
														<html:radio name="appForm" value="N" property="previouslyCovered"  onclick="enableNone()"></html:radio>
														
														<bean:message key="no"/>
													<%}%>
													
												</TD>								
											</TR>
										
											 <TR align="left">
											 <%										
											 if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
											|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) ||(appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))
									||(appCommonFlag.equals("19")))
											{	
											%>	
											<script>
												booleanVal=true;
											</script>
											
												<TD align="left" valign="top" class="ColumnBackground" width="25%"><html:radio name="appForm" value="none" property="none" disabled="true"><bean:message key="none" /></html:radio>
												</TD>
												<TD align="left" valign="top" class="ColumnBackground" ><html:radio name="appForm" value="cgbid" property="none" disabled="true">
												<bean:message key="cgbid" /></html:radio>								
												</TD>
												<TD align="left" valign="top" class="ColumnBackground" width="25%"><html:radio name="appForm" value="cgpan" property="none" disabled="true">
												<bean:message key="cgpan" /></html:radio>
																								
												</TD>
											<%}else {%>

											<TD align="left" valign="top" class="ColumnBackground" width="25%"><html:radio name="appForm" value="none" property="none" disabled="false"><bean:message key="none" /></html:radio>
												</TD>
												<TD align="left" valign="top" class="ColumnBackground" ><html:radio name="appForm" value="cgbid" property="none" disabled="false">
												<bean:message key="cgbid" /></html:radio>								
												</TD>
												<TD align="left" valign="top" class="ColumnBackground" width="25%"><html:radio name="appForm" value="cgpan" property="none" disabled="false">
												<bean:message key="cgpan" /></html:radio>
												
											<%}%>
												
												<TD align="left" valign="top" class="ColumnBackground" width="15%" colspan="2">	
												

											 <%	

											 if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
											|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")))
											{													
												
											%>	
											<script>
												booleanVal=true;
											</script>

												<html:text property="unitValue" size="15" alt="Value" name="appForm"  maxlength="15" disabled="true"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											<%}
											else if((appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19"))){
											%>
											<script>
												booleanVal=true;
											</script>

												<html:text property="unitValue" size="15" alt="Value" name="appForm"  maxlength="15" disabled="false"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

											<%}else {%>

											<script>
												booleanVal=false;
											</script>

												<html:text property="unitValue" size="15" alt="Value" name="appForm"  maxlength="15" disabled="false"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											<%}%>
										
											<%
											if((appCommonFlag.equals("7"))||(appCommonFlag.equals("8"))||(appCommonFlag.equals("9")) || (appCommonFlag.equals("10")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))
											{
											%>												
												<a href="javascript:submitForm('afterTcMli.do?method=getBorrowerDetails')"><bean:message key="getBorrowerDetails"/></a>


											<%}%>
												</TD>											
												
											</TR>

											<TR align="left">
												<TD align="left" valign="top" class="ColumnBackground" width="30%">&nbsp;<bean:message key="balanceApprovedAmt"/>
												</TD>
												<TD align="left" valign="top" class="TableData" colspan="8">
												<%	if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
												|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))
												{	
												%>
												<bean:write property="balanceApprovedAmt" name="appForm"/>
												<%}%>
												</TD>
											</TR>


											 <TR align="left">
												<TD align="left" valign="top" class="ColumnBackground" width="30%"><font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="constitution"/>
												</TD>
												<TD align="left" valign="top" class="TableData" colspan="8">
												<%	if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
												|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))
												{	
												%>
														<html:select property="constitution" name="appForm" disabled="true">
														<html:option value="">Select</html:option>
														<html:option value="Proprietary/Individual">Proprietary/Individual</html:option>
														<html:option value="Partnership">Partnership</html:option>
                                                        <html:option value="Limited liability Partnership">Limited liability Partnership</html:option>
														<html:option value="private"><bean:message key="private" /></html:option>
														
														<html:option value="public"><bean:message key="public" /></html:option>
														<html:option value="HUF">HUF</html:option>
														<html:option value="Trust">Trust</html:option>
														<html:option value="Society/Co op">Society/Co op Society</html:option>
<!--	<html:option value="Others"><bean:message key="others" /></html:option> -->
													</html:select>
	<!--				<bean:write property="constitution" name="appForm"/>-->

												<%}
												else{
													%>
												
													<html:select property="constitution" name="appForm" onchange="setConstitutionDate();">
														<html:option value="">Select</html:option>
														<html:option value="Proprietary/Individual">Proprietary/Individual</html:option>
														<html:option value="Partnership">Partnership</html:option>
                                                        <html:option value="Limited liability Partnership">Limited liability Partnership</html:option>
														<html:option value="private"><bean:message key="private" /></html:option>												
														<html:option value="public"><bean:message key="public" /></html:option>
														<html:option value="HUF">HUF</html:option>
														<html:option value="Trust">Trust</html:option>
														<html:option value="Society/Co op">Society/Co op Society</html:option>

													<!--	<html:option value="Others"><bean:message key="others" /></html:option> -->
													</html:select>&nbsp;&nbsp;&nbsp;&nbsp;		
												<%}%>
												
										
													
												<% if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
												|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))
												 {
												
												%>
												<%--<bean:write property="PartnershipDeedDate" name="appForm"/> --%>
												<%}
												else {
												%>
									
													<%--<html:text property="PartnershipDeedDate" size="15" alt="constitution" name="appForm" maxlength="50" disabled="true"/>
													<IMG src="images/CalendarIcon.gif" width="20" onClick="showCalendar('appForm.PartnershipDeedDate')" align="center"> --%>
												<%}%>
											
												</TD>
											 </TR>
											
											 <TR align="left">
												<TD align="left" valign="top" class="ColumnBackground" width="30%"><font color="#FF0000" size="2">*</font>&nbsp;
												<!--<bean:message key="unitName"/> -->
												Name of the concern
												</TD>												
												<TD align="left" valign="top" class="TableData" width="30%" colspan="4">

												<%	if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
												|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))
												{	
												%>
												<bean:write property="ssiType" name="appForm"/>
												<%}
												else{
													%>
												
													<html:select property="ssiType" name="appForm" >
														<html:option value="">Select</html:option>
														<html:option value="M/s">M/s</html:option>
														<html:option value="Shri">Shri</html:option>
														<html:option value="Smt">Smt</html:option>
														<html:option value="Ku">Ku</html:option>
													</html:select>&nbsp;&nbsp;&nbsp;&nbsp;
												<%}%>
													

													<%	if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
													|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))

													{
													%>
													<bean:write name="appForm" property="ssiName"/>
													<%}
													else
													{ %>
													<html:text property="ssiName" size="30" alt="unitName" name="appForm" maxlength="100" onchange="javascript:checkssiNameAndPromoterName();"/>	
													<%}%>
												 </td>
											</tr>
                    
											 <TR align="left">											
												<TD align="left" valign="top" class="ColumnBackground">
												<font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="unitAddress"/>
												</td>
												<TD align="left" valign="top" class="TableData"  colspan="4">
												<%
													if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
													|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))

													{
												%>
													<bean:write name="appForm" property="address"/>

												<%	}
													else
													{ %>
													<html:textarea property="address" cols="90" alt="address" name="appForm" rows="2"/>	
													<%}%>
												</td>
																							
												
											</tr>

												
											 <TR align="left">
											 
													<TD align="left" valign="top" class="ColumnBackground">
													<font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="state"/>
													</TD>	
													<TD align="left" valign="top" class="TableData" >
												<%
													if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
													|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))

													{
													%>
													<bean:write name="appForm" property="state"/>

													<%	}
													else
													{ %>												
												
													<html:select property="state" name="appForm" onchange="javascript:submitForm('afterTcMli.do?method=getDistricts')">
														<html:option value="">Select</html:option>
														<html:options name="appForm" property="statesList"></html:options>

													</html:select>	
												<%}%>
													
												 </td>
												<TD align="left" valign="top" class="ColumnBackground"><font color="#FF0000" size="2">*</font><bean:message key="district"/>
												</TD>												
												<TD align="left" valign="top" class="TableData">
												<%
													if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
													|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))

													{
													%>
													<bean:write name="appForm" property="district"/>

													<%	}
													else
													{ %>
												
													<html:select property="district" name="appForm">
														<html:option value="">Select</html:option>
														<html:options name="appForm" property="districtList"/>
													</html:select>
												</TD>
												
												 <%}%>
												
											</tr>

											 <TR align="left">
												<TD align="left" valign="top" class="ColumnBackground">
												<font color="#FF0000" size="2">*</font><bean:message key="city"/>
												</td>
												<TD align="left" valign="top" class="TableData">
												<%
													if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
													|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))

													{
													%>
													<bean:write name="appForm" property="city"/>

													<%	}
													else
													{ %>
													<html:text property="city" size="20" alt="city" name="appForm" maxlength="100"/>	
													<%}%>
												</td>
												<TD align="left" valign="top" class="ColumnBackground"><font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="pinCode"/>
												</td>
												<TD align="left" valign="top" class="TableData">
												<%
													if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
													|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))

													{
													%>
													<bean:write name="appForm" property="pincode"/>

													<%	}
													else
													{ %>
													<html:text property="pincode" size="20" alt="pinCode" name="appForm" maxlength="6" onkeypress="return numbersOnly(this, event)" onkeyup="isValidNumber(this)"/>
													<%}%>
												</td>		
											</TR>

						
											 <TR align="left">											
												<TD align="left" valign="top" class="ColumnBackground" width="25%">&nbsp;<bean:message key="firmItpan"/>
												</td>
												<TD align="left" valign="top" class="TableData" width="15%" >
												<%
													if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
													|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))

													{
												%>
													<bean:write name="appForm" property="ssiITPan"/>

												<%	}
													else
													{ %><html:text property="ssiITPan" size="20" alt="ITPAN" name="appForm" maxlength="10" />
													<%}%>
												</td>
												<TD align="left" valign="top" class="ColumnBackground" width="25%">&nbsp;<bean:message key="ssiRegNo"/>
												</td>
												<TD align="left" valign="top" class="TableData" width="15%" colspan="2">
												<%
													if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
													|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))

													{
												%>
													<bean:write name="appForm" property="regNo"/>

												<%	}
													else
													{ %>
														<html:text property="regNo" size="30" alt="SSI Reg No" name="appForm" maxlength="25"/>
													<%}%>
												</td>

											</tr>

											 <TR align="left">
												<TD align="left" valign="top" class="ColumnBackground" width="30%"><font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="industryNature"/>
												</TD>												
												<TD align="left" valign="top" class="TableData">

												<%
													if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
													|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))

													{
													%>
													<bean:write name="appForm" property="industryNature"/>

													<%	}
													else
													{ %>											
													<html:select property="industryNature" name="appForm" onchange="javascript:submitForm('afterTcMli.do?method=getIndustrySector')">
														<html:option value="">Select</html:option>
														<html:options name="appForm" property="industryNatureList"></html:options>	
													</html:select>	
												<%}%>
													
												 </td>
												<TD align="left" valign="top" class="ColumnBackground" width="25%"><bean:message key="industrySector"/>
												</td>
												<TD align="left" valign="top" class="TableData" colspan="2">
												<%
													if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
													|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))

													{
													%>
													<bean:write name="appForm" property="industrySector"/>

													<%	}
													else
													{ %>										
												
													<html:select property="industrySector" name="appForm" onchange="javascript:submitForm('afterTcMli.do?method=getTypeOfActivity')">
														<html:option value="">Select</html:option>
														<html:options name="appForm" property="industrySectorList"></html:options>
														<html:option value="Others">Others</html:option>
													 </html:select>
													<%}%>
												 </td>
											</tr>

											 <TR align="left">
												<TD align="left" valign="top" class="ColumnBackground" width="30%">&nbsp;<font color="#FF0000" size="2">*</font><bean:message key="activitytype"/>
												</TD>												
												<TD align="left" valign="top" class="TableData">
												<%
													if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
													|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))

													{
												%>
													<html:text property="activityType" size="20" alt="Activity Type" name="appForm" maxlength="50"/>
												<%	}
													else
													{ %>
													<html:text property="activityType" size="20" alt="Activity Type" name="appForm" maxlength="50"/>
													<%}%>
												 </td>
												<TD align="left" valign="top" class="ColumnBackground" width="25%"><font color="#FF0000" size="2">*</font><bean:message key="noOfEmployees"/>
												</td>
												<TD align="left" valign="top" class="TableData" width="15%" colspan="2">
												<%
													if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
													|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))

													{
												%>
													
													<html:text property="employeeNos" size="10" alt="No Of employees" name="appForm" maxlength="3"/>

												<%	}
													else
													{ %><html:text property="employeeNos" size="10" alt="No Of employees" name="appForm" onkeypress="return numbersOnly(this, event)" onkeyup="isValidNumber(this)" maxlength="2"/>	
													<%}%>
												</td>
											</tr>
											<TR>
												<TD align="left" valign="top" class="ColumnBackground" colspan="2" ><font color="red"><B>Whether the Unit is engaged in Training, Retail Trading, Buying & Selling and Constitution is SHG/JLG:</B></font></TD>
												<TD align="left" valign="top" class="TableData" colspan="5" >
													<input type="radio" name="activityConfirm" value="Y" onclick="enableOnActivityConfirm('Y');">Yes
													<input type="radio" name="activityConfirm" value="N" onclick="enableOnActivityConfirm('N');">No
												</TD>
											</TR>	

											 <TR align="left">
												<TD align="left" valign="top" class="ColumnBackground" width="30%"><font color="#FF0000" size="2">*</font><bean:message key="turnover"/>
												</TD>												
												<TD align="left" valign="top" class="TableData">
												<%
													if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
													|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))
													{
												%>
													

                                                    <html:text property="projectedSalesTurnover" size="10" alt="turnover" name="appForm" maxlength="16"/>
												<%	}
													else
													{ %>
													<html:text property="projectedSalesTurnover" size="20" alt="turnover" name="appForm" maxlength="16" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)"/>
													<%}%>
													<bean:message key="inRs"/>
												 </td>
												<TD align="left" valign="top" class="ColumnBackground" width="25%"><bean:message key="exports"/>
												</td>
												<TD align="left" valign="top" class="TableData" width="15%" colspan="2">
												<%
													if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
													|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))

													{
												%>
													<bean:write name="appForm" property="projectedExports"/>

												<%	}
													else
													{ %>
													<html:text property="projectedExports" size="10" alt="exports" name="appForm" maxlength="16" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)"/>
													<%}%>
													<bean:message key="inRs"/>
												</td>
											</tr>


										</TABLE>
									</TD>
								</TR>		
								<tr>
									<td>
									</td>
								</tr>
								<TR>
									<TD class="SubHeading" colspan="6"><bean:message key="promoterHeader" />
									</TD>						
								</TR>

								<TR align="left">
									<TD align="left" valign="top" class="ColumnBackground" colspan="6">
										<bean:message key="chiefInfo"/>
									</TD>
								</TR>

								<TR align="left">
								  <TD align="left" valign="top" class="ColumnBackground" colspan="2" width="15%">
								  </TD>												 
								  <TD align="left" valign="top" class="ColumnBackground" width="10%">
										<font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="title"/>
								   </TD>
								   <TD align="left" valign="top" class="ColumnBackground" width="25%">
										<font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="firstName"/>
								   </TD>
									<TD align="left" valign="top" class="ColumnBackground" width="25%">
										<bean:message key="middleName"/>	
								   </TD>
									<TD align="left" valign="top" class="ColumnBackground" width="25%">
										<font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="lastName"/>
								   </TD>
								</TR>

								<TR align="left">
								  <TD align="left" valign="top" class="ColumnBackground" colspan="2" width="15%">
								  <font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="name"/>
								  </TD>		
								  <TD align="left" valign="top" class="TableData">

								  <%if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
									|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))

											{										

									%>
									<bean:write property="cpTitle" name="appForm"/>
									<%}
									else {
									%>
								 
									<html:select property="cpTitle" name="appForm" onchange="enableGender()">
										<html:option value="">Select</html:option>
										<html:option value="Mr.">Mr</html:option>
										<html:option value="Smt">Smt</html:option>
										<html:option value="Ku">Ku</html:option>
									</html:select>
									<%}%>
									
								  </TD>
								  <TD align="left" valign="top" class="TableData">

								  <% if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
									|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))
									||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))
									||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))
											{										

									%>
									<bean:write property="cpFirstName" name="appForm"/>
									<%}
									else {
									%>
								  
									<html:text property="cpFirstName" size="20" alt="firstName" name="appForm" maxlength="20"/>
									<%}%>
									
								  </TD>
								   <TD align="left" valign="top" class="TableData">

								   <%if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
									|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))
									||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))
									||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))

											{										

									%>
									<bean:write property="cpMiddleName" name="appForm"/>
									<%}
									else {
									%>								  
								   
									<html:text property="cpMiddleName" size="20" alt="middleName" name="appForm" maxlength="20"/>

									<%}%>
									
								  </TD>
								   <TD align="left" valign="top" class="TableData">

								   <%if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
									|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))
									||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))
									||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))
											{										

									%>
									<bean:write property="cpLastName" name="appForm"/>
									<%}
									else {
									%>	
								  
									<html:text property="cpLastName" size="20" alt="lastName" name="appForm" maxlength="20" onchange="javascript:checkssiNameAndPromoterName();"/>

									<%}%>									
								  </TD>
								</TR>
								
								<TR align="left">
								  <TD align="left" valign="top" class="ColumnBackground" colspan="2" width="15%">
								  <font color="#FF0000" size="2">*</font>&nbsp;Father/Mother Name
								  </TD>		
								  <TD align="left" valign="top" class="TableData">

								  <%if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
									|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))

											{										

									%>
									
									<%}
									else {
									%>
								 
									
									<%}%>
									
								  </TD>
								  <TD align="left" valign="top" class="TableData">

								  <% if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
									|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))
									||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))
									||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))
											{										

									%>
									
									<%}
									else {
									%>
								  
									
									<%}%>
									
								  </TD>
								   <TD align="left" valign="top" class="TableData">

								   <%if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
									|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))
									||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))
									||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))

											{										

									%>
									
									<%}
									else {
									%>								  
								   
									

									<%}%>
									
								  </TD>
								   <TD align="left" valign="top" class="TableData">

								   <%if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
									|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))
									||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))
									||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))
											{										

									%>
									
									<%}
									else {
									%>	
								  
									

									<%}%>									
								  </TD>
								</TR>

								<TR align="left">
									<TD align="left" valign="top" class="ColumnBackground" colspan="2"><font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="gender"/>
									</TD>
									<TD align="left" valign="top" class="TableData" width="20%">

									<% if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
									|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))
											{										

									%>
										<bean:write property="cpGender" name="appForm"/>

									<%}
									else {
									%>	
									
										<html:radio name="appForm" value="M" property="cpGender" >
										</html:radio>
									
										<bean:message key="male" />&nbsp;&nbsp;&nbsp;

									
										<html:radio name="appForm" value="F" property="cpGender" ></html:radio>
									
										<bean:message key="female" />
									<%}%>
									
									</TD>
									<TD align="left" valign="top" class="ColumnBackground">
										&nbsp;<bean:message key="chiefItpan" />
									</TD>
									<TD align="left" valign="top" class="TableData" colspan="2" width="20%">

									<% if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
									|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))
											{										

									%>
									<bean:write property="cpITPAN" name="appForm"/>
									<%}
									else {
									%>	
									
										<html:text property="cpITPAN" size="15" alt="chiefItpan" name="appForm" maxlength="10"/>

									<%}%>
									
									</TD>													
								</TR>
                <!--added by sukant-->
								   <TR>
									  <TD align="left" valign="top" class="ColumnBackground" colspan="3" >Whether the Chief Promoter belongs to Minority Community:</TD>
									  <TD align="left" valign="top" class="ColumnBackground" colspan="5" >
										<input type="radio" name="religion" value="Y">Yes
										<input type="radio" name="religion" value="N">No
									  </TD>	
									  
									</TR>
									<TR>
									  <TD align="left" valign="top" class="ColumnBackground" colspan="3" ><font color="#FF0000" size="2">*</font>&nbsp;Whether the Chief Promoter is Physically Handicapped:</TD>
									  <TD align="left" valign="top" class="ColumnBackground" colspan="7" >
										<html:radio name="appForm" value="Y" property="physicallyHandicapped" >Yes</html:radio>
										<html:radio name="appForm" value="N" property="physicallyHandicapped">No</html:radio>			
									</TD>
									</TR>

								<TR align="left">
									<TD align="left" valign="top" class="ColumnBackground" colspan="2">
										<font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="dob" />					
									</TD>
									<TD align="left" valign="top" class="TableData" >

									<% if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
									|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))
											{										

									%>
									<bean:write property="cpDOB" name="appForm"/>
									<%}
									else {
									%>	
									
										<html:text property="cpDOB" size="15" alt="dob" name="appForm" maxlength="10"/>
										<IMG src="images/CalendarIcon.gif" width="20" onClick="showCalendar('appForm.cpDOB')" align="center">
									<%}%>
									

									<TD align="left" valign="top" class="ColumnBackground">
									<bean:message key="socialCategory"/>
									</TD>
									<TD align="left" valign="top" class="TableData" width="20%" colspan="3">

									<% if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
									|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))
											{										

									%>
									<bean:write property="socialCategory" name="appForm"/>
									<%}
									else {
									%>	
									
										<html:select property="socialCategory" name="appForm">
											<html:option value="">Select</html:option>
											<html:options name="appForm" property="socialCategoryList"></html:options>
											
										</html:select>

									<%}%>									
										
									</TD>

										
									</TD>
								</TR>

								<%--<TR align="left">
									<TD align="left" valign="top" class="ColumnBackground" colspan="2">
										&nbsp;<bean:message key="legalid" />					
									</TD>
									<TD align="left" valign="top" class="TableData" colspan="5">

									<% if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
									|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))
											{										

									%>
									<bean:write property="cpLegalID" name="appForm"/>
									<%}
									else {
									%>	
																	
										<html:select property="cpLegalID" name="appForm" onchange="enableOtherLegalId()">
											<html:option value="">Select</html:option>
											<html:option value="VoterIdentityCard">Voter Identity Card</html:option>
											<html:option value="PASSPORT">Passport Number</html:option>
											<html:option value="Driving License">Driving License</html:option>
											<html:option value="RationCardnumber">Ration Card Number</html:option>
											<html:option value="Others">Others</html:option>
										</html:select>			
									<%}%>

									<% if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
									|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))
											{										

									%>
									<bean:write property="otherCpLegalID" name="appForm"/>
									<%}
									else {
									%>																	
										<html:text property="otherCpLegalID" size="20" alt="otherId" name="appForm"  maxlength="50" disabled="true"/>&nbsp;&nbsp;<bean:message key="otherId"/>	
									<%}%>

										&nbsp;&nbsp;&nbsp;&nbsp;<strong><bean:message key="pleaseSpecify"/></strong>

									<% if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
									|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))
											{										

									%>
									<bean:write property="cpLegalIdValue" name="appForm"/>
									<%}
									else {
									%>							
									
										<html:text property="cpLegalIdValue" size="20" alt="value" name="appForm" maxlength="20"/>&nbsp;&nbsp;<bean:message key="id" />
									<%}%>
									
									
									</TD>
								</TR>--%>

								<TR align="left">
									<TD align="left" valign="top" class="ColumnBackground" colspan="7"><bean:message key="otherPromoters" />	
									</TD>
								</TR>
								 <tr> 
									 <td class="ColumnBackground"colspan="7">
										 <table border="0" cellpadding="1" cellspacing="0" width="100%">
											<tr>
												<td class="ColumnBackground" width="17%"><b>
													<span style="font-size: 9pt">1. <bean:message key="promoterName" />	</span></b>
												</td>
												<td width="15%" class="TableData">
												
													<b><span style="font-size: 9pt" >	

													<% if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
													|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))
															{										

													%>
													<bean:write property="firstName" name="appForm"/>
													<%}
													else {
													%>							
									
													<html:text property="firstName" size="20" alt="firstName" name="appForm" maxlength="50"/>
													<%}%>
													</span></b>
													
												</td>
												<td width="17%" class="ColumnBackground" >
													<b><span style="font-size: 9pt"> <bean:message key="promoterItpan" /></span></b>
												</td>
												<td width="17%" class="TableData">												
													<b><span style="font-size: 9pt"> 

													<% if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
													|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))
													{										

													%>
													<bean:write property="firstItpan" name="appForm"/>
													<%}
													else {
													%>		
													<html:text property="firstItpan" size="20" alt="firstItpan" name="appForm" maxlength="10"/>
													<%}%>
													</span></b>
													
												</td>
												<td width="17%" class="ColumnBackground" >
													<b><span style="font-size: 9pt"><bean:message key="promoterDob" /></span></b>
												</td>
												<td width="17%" class="TableData">
												
													<b><span style="font-size: 9pt"> 
													
													<% if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
													|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))
													{										

													%>
													<bean:write property="firstDOB" name="appForm"/>
													<%}
													else {
													%>												
													 <html:text property="firstDOB" size="10" alt="firstDOB" name="appForm" maxlength="10"/>
													<IMG src="images/CalendarIcon.gif" width="20" onClick="showCalendar('appForm.firstDOB')" align="center">
													<%}%>
													</span></b>
													
												</td>
											</tr>
											 <tr>
												<td class="ColumnBackground" width="17%"><b>
													<span style="font-size: 9pt">2. <bean:message key="promoterName" />	</span></b>
												</td>
												<td width="15%" class="TableData">
												
													<b><span style="font-size: 9pt"> 

													<% if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
													|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))
													{										

													%>
													<bean:write property="secondName" name="appForm"/>
													<%}
													else {
													%>		
													<html:text property="secondName" size="20" alt="secondName" name="appForm" maxlength="50"/>
													<%}%>
													</span></b>
													
												</td>
												<td width="17%" class="ColumnBackground" >
													<b><span style="font-size: 9pt"> <bean:message key="promoterItpan" /></span></b>
												</td>
												<td width="17%" class="TableData">
												
													<b><span style="font-size: 9pt"> 

													<% if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
													|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))
													{										

													%>
													<bean:write property="secondItpan" name="appForm"/>
													<%}
													else {
													%>		
													<html:text property="secondItpan" size="20" alt="secondItpan" name="appForm" maxlength="10"/>
													<%}%>
													</span></b>
													
												</td>
												<td width="17%" class="ColumnBackground" >
													<b><span style="font-size: 9pt"><bean:message key="promoterDob" /></span></b>
												</td>
												<td width="17%" class="TableData">
												
													<b><span style="font-size: 9pt"> 

													<% if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
													|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))
													{										

													%>
													<bean:write property="secondDOB" name="appForm"/>
													<%}
													else {
													%>	
													 <html:text property="secondDOB" size="10" alt="secondDOB" name="appForm" maxlength="10"/>
													<IMG src="images/CalendarIcon.gif" width="20" onClick="showCalendar('appForm.secondDOB')" align="center">
													<%}%>
													</span></b>
													
												</td>
											</tr>

											 <tr>
												<td class="ColumnBackground" width="17%"><b>
													<span style="font-size: 9pt">3. <bean:message key="promoterName" />	</span></b>
												</td>
												<td width="15%" class="TableData">
												
													<b><span style="font-size: 9pt"> 

													<% if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
													|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))
												{										

													%>
													<bean:write property="thirdName" name="appForm"/>
													<%}
													else {
													%>	
													<html:text property="thirdName" size="20" alt="thirdName" name="appForm" maxlength="50"/>
													<%}%>
													</span></b>
													
												</td>
												<td width="17%" class="ColumnBackground" >
													<b><span style="font-size: 9pt"> <bean:message key="promoterItpan" /></span></b>
												</td>
												<td width="17%" class="TableData">
												
													<b><span style="font-size: 9pt"> 
													<% if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
													|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))
													{										

													%>
													<bean:write property="thirdItpan" name="appForm"/>
													<%}
													else {
													%>	
													<html:text property="thirdItpan" size="20" alt="thirdItpan" name="appForm" maxlength="10"/>
													<%}%>
													</span></b>
													
												</td>
												<td width="17%" class="ColumnBackground" >
													<b><span style="font-size: 9pt"><bean:message key="promoterDob" /></span></b>
												</td>
												<td width="17%" class="TableData">
												
													<b><span style="font-size: 9pt"> 

													<% if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2")) ||(appCommonFlag.equals("3"))
													|| (appCommonFlag.equals("4")) || (appCommonFlag.equals("5")) || (appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")) || (appCommonFlag.equals("14"))||(appCommonFlag.equals("17"))||(appCommonFlag.equals("18"))||(appCommonFlag.equals("19")))
													{										

													%>
													<bean:write property="thirdDOB" name="appForm"/>
													<%}
													else {
													%>	
													 <html:text property="thirdDOB" size="10" alt="thirdDOB" name="appForm" maxlength="10"/>
													<IMG src="images/CalendarIcon.gif" width="20" onClick="showCalendar('appForm.thirdDOB')" align="center">
													<%}%>
													</span></b>
													
												</td>
											</tr>
										 </table>
									</td>
								 </tr>
								<tr align="left"> 
									<td colspan="6">
										<img src="../images/clear.gif" width="5" height="15">
									</td>
								</tr>
							</TABLE>			<!--table closing for borrower details-->


<!--start for project-->	<table width="100%" cellspacing="1" cellpadding="0">
								<tr> 
									<td colspan="8">
										<table width="100%" border="0" cellspacing="0" cellpadding="0">
											 <tr> 
												<TD width="31%" class="Heading"><bean:message key="projectHeader" /></TD>
												<TD><IMG src="images/TriangleSubhead.gif" width="19" height="19"></TD>                 
												 <td>&nbsp;</td>
												 <td>&nbsp;</td>
											 </tr>											
											 <TR>
												<TD colspan="4" class="Heading"><IMG src="images/Clear.gif" width="5" height="5"></TD>
											</TR>                   
										</table>
									</td>
								</tr>
								<TR>
								  <TD align="left" valign="top" class="ColumnBackground" colspan="3" >Whether Unit assisted is a new unit:</TD>
								  <TD align="left" valign="top" class="TableData" colspan="5" >
									<input type="radio" name="unitAssisted" value="Y">Yes
									<input type="radio" name="unitAssisted" value="N" checked="checked">No
								  </TD>
								</TR>

					<TR>
						<TD align="left" valign="top" class="ColumnBackground" colspan="3" >Whether the Unit Assisted is Women Operated and/or Women Owned:</TD>
						<TD align="left" valign="top" class="TableData" colspan="5" >
						<input type="radio" name="womenOperated" value="Y">Yes
						<input type="radio" name="womenOperated" value="N" checked="checked">No
						</TD>
					</TR>	
					<TR>
						<TD align="left" valign="top" class="ColumnBackground" colspan="3">Whether the Unit Assisted is an MSE as per the MSMED Act 2006 definition of MSE:</TD>
						<TD align="left" valign="top" class="TableData" colspan="5">
						<html:radio name="appForm" value="Y" property="mSE" >Y</html:radio>
						<html:radio name="appForm" value="N" property="mSE">N</html:radio>
						</TD> 
					</TR>
					<TR>
					  <TD align="left" valign="top" class="ColumnBackground" colspan="3" >Whether the Unit Assisted is a Micro Enterprise and Guarantee cover is within Rs 5 lakh cap:</TD>
					  <TD align="left" valign="top" class="TableData" colspan="5" >
					  <input type="radio" name="enterprise" value="Y">Yes
					  <input type="radio" name="enterprise" value="N" checked="checked">No</TD>
					</TR>
					<!-- added vinod 13-Mar-2015 -->
					<TR>
						<TD align="left" valign="top" class="ColumnBackground" colspan="3"><font color="#FF0000" size="2">*</font>Is Primary Security</TD>
						<TD align="left" valign="top" class="TableData" colspan="5">														
							<html:radio property="pSecurity" name="appForm" value="Y" >Yes</html:radio>
							<html:radio property="pSecurity" name="appForm" value="N" >No</html:radio>																									
						</TD>
					</TR>
					<tr> 
									 <td class="ColumnBackground" colspan="2">
										<font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="collateralSec" />
									 </td>
									 <td align="left" class="TableData" colspan="2"> <div align="left">
									 <% if(appCommonFlag.equals("11") || appCommonFlag.equals("12") || appCommonFlag.equals("13"))
										 {
										%>
<%--				<bean:write property="collateralSecurityTaken" name="appForm"/>--%>
										<html:radio name="appForm" value="Y" property="collateralSecurityTaken" disabled="true">
										</html:radio>
										
										<bean:message key="y" />

										
										<html:radio name="appForm" value="N" property="collateralSecurityTaken" disabled="true"></html:radio>
										
										<bean:message key="n" />

										<%}
										else {
										%>	
									
										<html:radio name="appForm" value="Y" property="collateralSecurityTaken" >
										</html:radio>
										
										<bean:message key="y" />

										
										<html:radio name="appForm" value="N" property="collateralSecurityTaken" ></html:radio>
										
										<bean:message key="n" />
									<%}%>
										
									</td>
									<td class="ColumnBackground" colspan="2"> &nbsp
										<font color="#FF0000" size="1">#</font>&nbsp;<bean:message key="thirdPartyTaken" />
									</td>
									<td align="left" class="TableData" colspan="2">	

									 <% if(appCommonFlag.equals("11") || appCommonFlag.equals("12") || appCommonFlag.equals("13"))
										 {
										%>
<%--					<bean:write property="thirdPartyGuaranteeTaken" name="appForm"/>--%>
										 <html:radio name="appForm" value="Y" property="thirdPartyGuaranteeTaken" disabled="true">
										 <bean:message key="y" /></html:radio>						 
										
										<html:radio name="appForm" value="N" property="thirdPartyGuaranteeTaken" disabled="true">
										
										<bean:message key="n" /></html:radio>

										<%}
										else {
										%>	
										
										 <html:radio name="appForm" value="Y" property="thirdPartyGuaranteeTaken" >
										 <bean:message key="y" /></html:radio>						 
										
										<html:radio name="appForm" value="N" property="thirdPartyGuaranteeTaken" >
										
										<bean:message key="n" /></html:radio>
									<%}%>
									
									</td>
								</tr>
								<tr align="left">
									<td class="ColumnBackground" colspan="8">									
										<font color="#FF0000" size="2">*&nbsp;</font> <font color="#FF0000" size="1"><bean:message key="collateralSecHint"/></font><br>
										<font color="#FF0000" size="1">#&nbsp;</font><font color="#FF0000" size="1"><bean:message key="thirdPartyTakenHint"/></font>
									</td>
								</tr>
                                <tr align="left"> 
									<td align="left" class="ColumnBackground" colspan="8"><b><font color="#008600" size="4">Joint Finance</font></b></td>
								</tr>
								<TR>
									  <TD align="left" valign="top" class="ColumnBackground" colspan="3" ><font color="#FF0000" size="2">*</font>&nbsp;Whether the credit is sanctioned under Joint Finance scheme:</TD>
								     <TD align="left" valign="top" class="TableData" colspan="5" >             
										<html:radio name="appForm" value="Y" property="jointFinance" onclick="javascript:enableJointFinance()">Yes</html:radio>
											<html:radio name="appForm" value="N" property="jointFinance" onclick="javascript:enableJointFinance()">No</html:radio>
									  </TD>   
									   
								</TR>
								<TR>
									<TD align="left" valign="top" class="ColumnBackground" colspan="3">&nbsp;Joint Cgpan (Existing Cgpan of this borrower)&nbsp;</TD>
								   <TD align="left" valign="top" class="TableData" colspan="5" >&nbsp;
										<html:text property="jointcgpan" size="25" alt="jointcgpan" name="appForm" maxlength="15" disabled="true"/>
									</td>  
								
								</TR>
                                                                <!-- Handicrafts -->
                                <tr align="left"> 
									<td colspan="8" align="left" class="ColumnBackground"><b><font color="#FF0000" size="4">Handicrafts</font></b></td>
								</tr>
                                <TR>
								  <TD align="left" valign="top" class="ColumnBackground" colspan="3" >
								  <font color="#FF0000" size="2">*</font>&nbsp;Whether the credit is sanctioned under Artisan Credit Card (ACC) scheme for Handicraft Artisans operated by DC(Handicrafts), Govt.of India:</TD>
								  <TD align="left" valign="top" class="TableData" colspan="5" >
	                <%if((appCommonFlag.equals("4"))||((appCommonFlag.equals("3"))))
											{	
										%>
										<html:radio name="appForm" value="Y" property="handiCraftsStatus">Yes</html:radio>
                  <html:radio name="appForm" value="N" property="handiCraftsStatus" >No</html:radio>
										<%}
										else {
											%><html:radio name="appForm" value="Y" property="handiCrafts" onclick="javascript:enableHandiCrafts()">Yes</html:radio>
                  <html:radio name="appForm" value="N" property="handiCrafts" onclick="javascript:enableHandiCrafts()">No</html:radio>
											
				   
				<%}%>
<!--<input type="radio" name="handiCrafts" value="Y" onclick="javascript:enableHandiCrafts()">Yes<input type="radio" name="handiCrafts" value="N" checked="checked" onclick="javascript:enableHandiCrafts()">N-->
							</TD>
                  
					</TR>
                    <TR>
					  <TD align="left" valign="top" class="ColumnBackground" colspan="3">
					  <font color="#FF0000" size="2">*</font>&nbsp;Whether GF/ASF is re-imbursable from O/o DC(Handicrafts) Govt.of India:</TD>
					  <TD align="left" valign="top" class="TableData" colspan="5" >
<!--<input type="radio" name="dcHandicrafts" value="Y">Yes<input type="radio" name="dcHandicrafts" value="N" checked="checked">N-->
				                 <%if((appCommonFlag.equals("4"))||((appCommonFlag.equals("3"))))
											{										
										%><html:radio name="appForm" value="Y" property="dcHandicraftsStatus"  >Yes</html:radio>
											<html:radio name="appForm" value="N" property="dcHandicraftsStatus">No</html:radio>
										<%}
										else {
									%><html:radio name="appForm" value="Y" property="dcHandicrafts" >Yes</html:radio>
										<html:radio name="appForm" value="N" property="dcHandicrafts" >No</html:radio>
											
				   
				<%}%>																				
												</TD>
                  
					</TR>

                    
                                         <tr align="left">
												<TD align="left" valign="top" class="ColumnBackground" colspan="2">&nbsp;I Card Number:</td>
                                                <TD align="left" valign="top" class="TableData" colspan="2">&nbsp;
                                                   <html:text property="icardNo" size="25" alt="icard No" name="appForm" maxlength="10" />
                                                </td> 
                                                <TD align="left" valign="top" class="ColumnBackground" colspan="2">&nbsp;I Card Issue date:</td>
                                                <TD align="left" valign="top" class="TableData" colspan="2">&nbsp;
                                                   <html:text property="icardIssueDate" size="20" alt="icardIssue Date" name="appForm" maxlength="10"/>
													<IMG src="images/CalendarIcon.gif" width="20" onClick="showCalendar('appForm.icardIssueDate')" align="center">
												</td> 
                                                             
                                          </tr>
								<tr align="left"> 
									<td colspan="8" align="left" class="ColumnBackground"><b><font color="#FF0000" size="4">Handlooms</font></b></td>
								</tr>
					                                      <TR>
                  <TD align="left" valign="top" class="ColumnBackground" colspan="3" ><font color="#FF0000" size="2">*</font>&nbsp;Whether the credit is sanctioned under DC(Handloom) scheme for Handoom Weavers </TD>
                  <TD align="left" valign="top" class="TableData" colspan="5" >
				   <%
						   if((appCommonFlag.equals("4"))||((appCommonFlag.equals("3"))))
											{
								%>    
					<html:radio name="appForm" value="Y" property="dcHandloomsStatus" >Yes</html:radio>
					<html:radio name="appForm" value="N" property="dcHandloomsStatus" onclick="javascript:enabledcHandlooms()">No</html:radio>
										<%}
										else {
										%>    
					<html:radio name="appForm" value="Y" property="dcHandlooms" onclick="javascript:enabledcHandlooms()">Yes</html:radio>
					<html:radio name="appForm" value="N" property="dcHandlooms" onclick="javascript:enabledcHandlooms()" >No</html:radio>
			   				<%}%>					
				  		</TD> 
        			</TR>
			       	 <TR>
                  <TD align="left" valign="top" class="ColumnBackground" colspan="3" ><font color="#FF0000" size="2">*</font>&nbsp;If Yes,Please select the name of the Scheme</TD>
                 <TD align="left" valign="top" class="TableData" colspan="5" >
			  			
							<!-- <html:text property="WeaverCreditScheme" size="25"  name="appForm" /> --->
							 
							   <html:select property="WeaverCreditScheme" name="appForm" disabled="true">
														<html:option value="Select">Select</html:option>
														<html:option value="IHDS">Integreated Handloom Development Scheme(CP{IHDS}) </html:option>
														<html:option value="CHCDS">Mega Cluster Scheme( Varanasi/Murshidabad)-CHCDS(MC)</html:option>
													</html:select>	
				</TD> 
                  	</TR>
									
									 <TR>
                  <TD align="left" valign="top" class="ColumnBackground" colspan="3" ><font color="#FF0000" size="2">*</font>&nbsp;
				  <font color="#008600" size="1"><b> If Yes,Please certify by ticking the checkbox.</b></font>We certify that the credit facility being covered under CGTMSE and for which reimbursement of GF/ASF in being availed adheres to all guidelines as issued by Office of DC(Handloom) from  time to time for sanction of Credit under the respective schemes</TD>
                  <TD align="left" valign="top" class="TableData" colspan="5" >
			  			
						<html:checkbox property="handloomchk" name="appForm" value="Y"/>						
			  							</TD> 
                  					</TR>

                                          <tr align="left">
									<td class="ColumnBackground" colspan="8">									
										<font color="#FF0000" size="2">*&nbsp;</font> <font color="#008600" size="2"><b>Credit facilities above Rs. 50 lakh and upto Rs.100 lakh will have to be rated internally by the MLI and should be of investment grade.For loan facility upto 50 lakhs MLIs may indicate ‘N.A’ if rating is not available.</b></font><br>
								</td>
								</tr>
                                
											
								
								<tr align="left"> 
									<TD class="SubHeading" colspan="8">
										<bean:message key="primarySecurity" />
									</TD>
								</tr>
								<tr align="left">
									<TD align="center" valign="top" class="ColumnBackground" colspan="4">
										<div align="center">
										<bean:message key="securityName"/></div>
									</td>
									<TD align="center" valign="top" class="ColumnBackground" colspan="2"><div align="center">
										<bean:message key="particulars"/></div>
									</td>
									<TD align="center" valign="top" class="ColumnBackground" colspan="2"> 
										<div align="center">
										<bean:message key="valueInRs"/><div>
									</td>
								</tr>	
								<tr align="left">
									<TD align="center" valign="top" class="TableData"  colspan="4">
										<div align="center">
										<bean:message key="land"/></div>
									</td>
									<TD align="center" valign="top" class="TableData" colspan="2"> 
										<div align="center">

										<% if(appCommonFlag.equals("11") || appCommonFlag.equals("12") || appCommonFlag.equals("13"))
										 {
										%>
										<bean:write property="landParticulars" name="appForm"/>
										<%}
										else {
										%>		
										
										<html:text property="landParticulars" alt="landPart" name="appForm" size="10" maxlength="10"/>
										<%}%>
										</div>
										
									</td>
									<TD align="center" valign="top" class="TableData" colspan="2">
										<div align="center">

										<% if(appCommonFlag.equals("11") || appCommonFlag.equals("12") || appCommonFlag.equals("13"))
										 {
										%>
										<bean:write property="landValue" name="appForm"/>
										<%}
										else {
										%>	
										
										<html:text property="landValue" alt="landValue" name="appForm" size="10" maxlength="10" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)" onblur="javascript:calculatePsTotal()"/>
										<%}%>
										</div>
										
									</td>
								</tr>
								<tr align="left">
									<TD align="center" valign="top" class="TableData"  colspan="4">
										<div align="center">
										<bean:message key="bldg"/></div>
									</td>
									<TD align="center" valign="top" class="TableData" colspan="2"> 
										<div align="center">

										<% if(appCommonFlag.equals("11") || appCommonFlag.equals("12") || appCommonFlag.equals("13"))
										 {
										%>
										<bean:write property="bldgParticulars" name="appForm"/>
										<%}
										else {
										%>											
										<html:text property="bldgParticulars" alt="bldgPart" name="appForm" size="10" maxlength="10"/>
										<%}%>
										</div>
										
									</td>
									<TD align="center" valign="top" class="TableData" colspan="2">
										<div align="center">

										<% if(appCommonFlag.equals("11") || appCommonFlag.equals("12") || appCommonFlag.equals("13"))
										 {
										%>
										<bean:write property="bldgValue" name="appForm"/>
										<%}
										else {
										%>	
										
										<html:text property="bldgValue" alt="bldgValue" name="appForm" size="10" maxlength="10" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)" onblur="javascript:calculatePsTotal()"/>
										<%}%>
										</div>
										
									</td>
								</tr>
								<tr align="left">
									<TD align="center" valign="top" class="TableData" colspan="4">
										<div align="center">
										<bean:message key="machine"/></div>
									</td>
									<TD align="center" valign="top" class="TableData" colspan="2">
										<div align="center">

										<% if(appCommonFlag.equals("11") || appCommonFlag.equals("12") || appCommonFlag.equals("13"))
										 {
										%>
										<bean:write property="machineParticulars" name="appForm"/>
										<%}
										else {
										%>	
										
										<html:text property="machineParticulars" alt="machinePart" name="appForm" size="10" maxlength="10"/>
										<%}%>
										</div>
										
									</td>
									<TD align="center" valign="top" class="TableData" colspan="2">
										<div align="center">

										<% if(appCommonFlag.equals("11") || appCommonFlag.equals("12") || appCommonFlag.equals("13"))
										 {
										%>
										<bean:write property="machineValue" name="appForm"/>
										<%}
										else {
										%>	
										
										<html:text property="machineValue" alt="machineValue" name="appForm" size="10" maxlength="10" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)" onblur="javascript:calculatePsTotal()"/>
										<%}%>
										</div>
										
									</td>
								</tr>
								<tr align="left">
									<TD align="center" valign="top" class="TableData" colspan="4">
										<div align="center">
										<bean:message key="assets"/></div>
									</td>
									<TD align="center" valign="top" class="TableData" colspan="2"> 
										<div align="center">

										<% if(appCommonFlag.equals("11") || appCommonFlag.equals("12") || appCommonFlag.equals("13"))
										 {
										%>
										<bean:write property="assetsParticulars" name="appForm"/>
										<%}
										else {
										%>	
										
										<html:text property="assetsParticulars" alt="assetsPart" name="appForm" size="10" maxlength="10"/>
										<%}%>
										</div>

									</td>
									<TD align="center" valign="top" class="TableData" colspan="2">
										<div align="center">

										<% if(appCommonFlag.equals("11") || appCommonFlag.equals("12") || appCommonFlag.equals("13"))
										 {
										%>
										<bean:write property="assetsValue" name="appForm"/>
										<%}
										else {
										%>	
										
										<html:text property="assetsValue" alt="assetsValue" name="appForm" size="10" maxlength="10" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)" onblur="javascript:calculatePsTotal()"/>
										<%}%>
										</div>
										
									</td>
								</tr>
								<tr align="left">
									<TD align="center" valign="top" class="TableData" colspan="4">
										<div align="center">
										<bean:message key="currentAssets"/></div>
									</td>
									<TD align="center" valign="top" class="TableData" colspan="2">
										<div align="center">

										<% if(appCommonFlag.equals("11") || appCommonFlag.equals("12") || appCommonFlag.equals("13"))
										 {
										%>
										<bean:write property="currentAssetsParticulars" name="appForm"/>
										<%}
										else {
										%>	
										
										<html:text property="currentAssetsParticulars" alt="currentAssetsPart" name="appForm" size="10" maxlength="10"/>
										<%}%>
										</div>
										
									</td>
									<TD align="center" valign="top" class="TableData" colspan="2">
										<div align="center">

										<% if(appCommonFlag.equals("11") || appCommonFlag.equals("12") || appCommonFlag.equals("13"))
										 {
										%>
										<bean:write property="currentAssetsValue" name="appForm"/>
										<%}
										else {
										%>										
										
										<html:text property="currentAssetsValue"  alt="currentAssetsValue" name="appForm" size="10" maxlength="10" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)" onblur="javascript:calculatePsTotal()"/>
										<%}%>
										</div>
										
									</td>
								</tr>
								<tr align="left">
									<TD align="center" valign="top" class="TableData" colspan="4">
									<div align="center">
										<bean:message key="psOthers"/></div>
									</td>
									<TD align="center" valign="top" class="TableData" colspan="2"> 
										<div align="center">

										<% if(appCommonFlag.equals("11") || appCommonFlag.equals("12") || appCommonFlag.equals("13"))
										 {
										%>
										<bean:write property="othersParticulars" name="appForm"/>
										<%}
										else {
										%>	
										
										<html:text property="othersParticulars" alt="othersPart" name="appForm" size="10" maxlength="10"/>
										<%}%>
										</div>
										
									</td>
									<TD align="center" valign="top" class="TableData" colspan="2">
										<div align="center">

										<% if(appCommonFlag.equals("11") || appCommonFlag.equals("12") || appCommonFlag.equals("13"))
										 {
										%>
										<bean:write property="othersValue" name="appForm"/>
										<%}
										else {
										%>	
										
										<html:text property="othersValue" alt="othersValue" name="appForm" size="10" maxlength="10" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)" onblur="javascript:calculatePsTotal()"/>
										<%}%>
										</div>
										
									</td>
								</tr>	
								<tr align="left">
									<TD align="right" valign="center" class="TableData" colspan="6"><div align="center">
										<b><bean:message key="psTotalWorth"/>&nbsp;<bean:message key="inRs"/></b></div>
									</td>									
									<TD align="center" valign="top" class="TableData" id="psTotalWorth" colspan="2">
									<%													if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2"))||(appCommonFlag.equals("3"))||(appCommonFlag.equals("4"))||(appCommonFlag.equals("5"))||(appCommonFlag.equals("6")) || (appCommonFlag.equals("11"))||(appCommonFlag.equals("12"))||(appCommonFlag.equals("13")))

									{
									%>
									<bean:write name="appForm" property="psTotal"/>
									<%}
									else
									{ %>

									
									<%}%>					
									</td>
									
								</tr>
								
								
									
								
								
								
								<tr align="left"> 
									<td colspan="8" class="SubHeading" height="28" width="843"><br> &nbsp;
										<bean:message key="meansOfFinance" />
									</td>
								</tr>
								<tr valign="top"> 
									<td colspan="8">
										<table width="100%" border="0" align="right" cellpadding="0" cellspacing="1">
											<tr> 
												<td class="ColumnBackground" width="25%" align="left" valign="top" >&nbsp;<font color="#FF0000" size="2">*</font>&nbsp;
												<% if(appCommonFlag.equals("7")|| appCommonFlag.equals("8")|| appCommonFlag.equals("10")|| appCommonFlag.equals("0")|| appCommonFlag.equals("3")|| appCommonFlag.equals("5")|| appCommonFlag.equals("11")|| appCommonFlag.equals("13")||appCommonFlag.equals("14")||appCommonFlag.equals("19"))
												{
												%>
												
													<bean:message key="tcSanctioned" />
												<%
												}
												else
												{
												%>
												<bean:message key="tcSanctioned" />
												<%}%>
												</td>
												<td class="TableData" width="20%" align="left" valign="top" id="tcSanctioned">

												<% if(appCommonFlag.equals("11") || appCommonFlag.equals("12") || appCommonFlag.equals("13"))
												 {
												%>
												<bean:write property="termCreditSanctioned" name="appForm"/>
												<%}
												else {
												%>									
												
													<html:text property="termCreditSanctioned" size="20" alt="tcSanctioned" name="appForm" maxlength="16" onchange="calltotalAmt()" onblur="javascript:calProjectOutlay()" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)"/>
												<%}%>
												
													
													<bean:message key="inRs" />
												</td>
												<td class="ColumnBackground" colspan="2" align="left" valign="top" >
													<table width="100%" border="0" align="right" cellpadding="0" cellspacing="1">
														<tr align="left">
															<td class="ColumnBackground" colspan="2" align="center">&nbsp;
															<% if(appCommonFlag.equals("9")|| appCommonFlag.equals("8")|| appCommonFlag.equals("10")|| appCommonFlag.equals("2")|| appCommonFlag.equals("4")|| appCommonFlag.equals("6")|| appCommonFlag.equals("12")|| appCommonFlag.equals("13")||appCommonFlag.equals("18")||appCommonFlag.equals("19"))
															{
															%>
															
																<bean:message key="wcLimitSanctioned"/>
															<%}
															else if(appCommonFlag.equals("1"))
															{%>
															
																<bean:message key="wcEnhanceLimitSanctioned"/>
															<%}
															else
															{%>
															<bean:message key="wcLimitSanctioned"/>
															<%}%>
															</td>
														</tr>							
														<tr align="left">
															<td class="ColumnBackground" align="left" valign="top" >&nbsp;
																<font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="wcFundBased"/>
															</td>
															<td class="TableData" align="left" valign="top" id="wcFBsanctioned">

															<% if(appCommonFlag.equals("11") || appCommonFlag.equals("12") || appCommonFlag.equals("13"))
															 {
															%>
															<bean:write property="wcFundBasedSanctioned" name="appForm"/>
															<%}
															else {
															%>		
															
																<html:text property="wcFundBasedSanctioned" size="20" alt="wcFundBased" name="appForm" maxlength="16" onchange="calltotalAmt()" onblur="javascript:calProjectOutlay()" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)"/>
															<%}%>
															
														
																<bean:message key="inRs"/>
															</td>						
														</tr>
														<tr align="left">
															<td class="ColumnBackground" align="left" valign="top" >&nbsp;
																<font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="wcNonFundBased"/>
															</td>
															<td class="TableData" align="left" valign="top"  id="wcNFBsanctioned">

															<% if(appCommonFlag.equals("11") || appCommonFlag.equals("12") || appCommonFlag.equals("13"))
															 {
															%>
															<bean:write property="wcNonFundBasedSanctioned" name="appForm"/>
															<%}
															else {
															%>		
															
																<html:text property="wcNonFundBasedSanctioned" size="20" alt="wcFundBased" name="appForm" maxlength="16" onchange="calltotalAmt()" onblur="javascript:calProjectOutlay()" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)"/>
															<%}%>

															
																<bean:message key="inRs"/>
															</td>						
														</tr>	
														<tr align="left">
															<td class="ColumnBackground" align="left" valign="top" width="50%">
																<bean:message key="marginMoneyAsTL"/>
															</td>
															<td class="TableData" align="left" valign="top" >
															<% if(appCommonFlag.equals("11") || appCommonFlag.equals("12") || appCommonFlag.equals("13"))
															 {
															%>
<!--						<bean:write property="marginMoneyAsTL" name="appForm"/>-->
																<html:radio name="appForm" value="Y" property="marginMoneyAsTL" disabled="true"></html:radio>
											
																<bean:message key="yes" />&nbsp;&nbsp;
																
																<html:radio name="appForm" value="N" property="marginMoneyAsTL" disabled="true"></html:radio>
																
																<bean:message key="no"/>

															<%}
															else {
															%>		
															
																<html:radio name="appForm" value="Y" property="marginMoneyAsTL" onclick="javascript:calProjectOutlay()" ></html:radio>
											
																<bean:message key="yes" />&nbsp;&nbsp;
																
																<html:radio name="appForm" value="N" property="marginMoneyAsTL" onclick="javascript:calProjectOutlay()" ></html:radio>
																
																<bean:message key="no"/>
															<%}%>
															
															</td>
														</tr>
														
													</table>
												</td>						 
											</tr>
											<tr align="left"> 
												<td class="ColumnBackground" width="25%">&nbsp;
													<font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="tcPromoterContribution"/>
												</td>
												<td class="TableData" width="20%" align="left" valign="top" id="tcCont">

												<% if(appCommonFlag.equals("11") || appCommonFlag.equals("12") || appCommonFlag.equals("13"))
												 {
												%>
												<bean:write property="tcPromoterContribution" name="appForm"/>
												<%}
												else {
												%>	
												
													<html:text property="tcPromoterContribution" size="20" alt="promotersCont" name="appForm" maxlength="16" onblur="javascript:calProjectOutlay()" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)"/>
												<%}%>
												
												
												  <bean:message key="inRs"/>
												</td>
												<td class="ColumnBackground" width="25%">&nbsp;
													<font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="wcPromoterContribution"/>
												</td>
												<td class="TableData" width="20%" align="left" valign="top" id="wcCont">

												<% if(appCommonFlag.equals("11") || appCommonFlag.equals("12") || appCommonFlag.equals("13"))
												 {
												%>
												<bean:write property="wcPromoterContribution" name="appForm"/>
												<%}
												else {
												%>										
												
													<html:text property="wcPromoterContribution" size="20" alt="wcPromoterContribution" name="appForm" maxlength="16" onblur="javascript:calProjectOutlay()" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)"/>
												<%}%>
												
													
												  <bean:message key="inRs"/>
												</td>
											</tr>
											<tr align="left"> 
												<td class="ColumnBackground" width="25%">&nbsp;
													<font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="tcSubsidyOrEquity"/>
												</td>
												<td class="TableData" width="20%" align="left" valign="top" id="tcSubsidy">

												<% if(appCommonFlag.equals("11") || appCommonFlag.equals("12") || appCommonFlag.equals("13"))
												 {
												%>
												<bean:write property="tcSubsidyOrEquity" name="appForm"/>
												<%}
												else {
												%>											
												
													<html:text property="tcSubsidyOrEquity" size="20" alt="tcSubsidyOrEquity" name="appForm" maxlength="16" onblur="javascript:calProjectOutlay()" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)"/>
												<%}%>		
											
												  <bean:message key="inRs"/>
												</td>
												<td class="ColumnBackground" width="25%">&nbsp;
													<font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="wcSubsidyOrEquity"/>
												</td>
												<td class="TableData" width="20%" align="left" valign="top" id="wcSubsidy">
												<% if(appCommonFlag.equals("11") || appCommonFlag.equals("12") || appCommonFlag.equals("13"))
												 {
												%>
												<bean:write property="wcSubsidyOrEquity" name="appForm"/>
												<%}
												else {
												%>												
												
													<html:text property="wcSubsidyOrEquity" size="20" alt="wcSubsidyOrEquity" name="appForm" maxlength="16" onblur="javascript:calProjectOutlay()" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)"/>
												<%}%>												
													
												  <bean:message key="inRs"/>
												</td>
											</tr>
											<tr align="left"> 
												<td class="ColumnBackground" width="25%">&nbsp;
													<font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="tcOthers"/>
												</td>
												<td class="TableData" width="20%" align="left" valign="top" id="tcOther">

												<% if(appCommonFlag.equals("11") || appCommonFlag.equals("12") || appCommonFlag.equals("13"))
												 {
												%>
												<bean:write property="tcOthers" name="appForm"/>
												<%}
												else {
												%>	
												
													<html:text property="tcOthers" size="20" alt="tcOthers" name="appForm" maxlength="16" onblur="javascript:calProjectOutlay()" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)"/>
												<%}%>												
													
												  <bean:message key="inRs"/>
												</td>
												<td class="ColumnBackground" width="25%">&nbsp;
													<font color="#FF0000" size="2">*</font>&nbsp;<bean:message key="wcOthers"/>
												</td>
												<td class="TableData" width="20%" align="left" valign="top" id="wcOther">

												<% if(appCommonFlag.equals("11") || appCommonFlag.equals("12") || appCommonFlag.equals("13"))
												 {
												%>
												<bean:write property="wcOthers" name="appForm"/>
												<%}
												else {
												%>												
												
													<html:text property="wcOthers" size="20" alt="wcOthers" name="appForm" maxlength="16" onblur="javascript:calProjectOutlay()" onkeypress="return decimalOnly(this, event,13)" onkeyup="isValidDecimal(this)"/>
												<%}%>							
													
												  <bean:message key="inRs"/>
												</td>
											</tr>												
											<TR align="left">
												<td class="ColumnBackground" width="25%">&nbsp; 
													<bean:message key="projectCost"/>
												</td>
												<td class="TableData" width="20%" align="left" valign="top" id="projectCost">
												<%	
												if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2"))||(appCommonFlag.equals("3"))||(appCommonFlag.equals("4"))||(appCommonFlag.equals("5"))|| appCommonFlag.equals("6") || appCommonFlag.equals("11") || appCommonFlag.equals("12") || appCommonFlag.equals("13"))

													{										
														
													%>
													
													<bean:write name="appForm" property="projectCost"/>												
													<%}
													else
													{ 
														%>	
													<bean:write name="appForm" property="projectCost"/>

													<bean:message key="inRs"/>
													<%}%>
												</td>
												<td class="ColumnBackground" width="25%">&nbsp;
													<bean:message key="wcAssessed"/>
												</td>
												<td class="TableData" width="20%" align="left" valign="top" id="wcAssessed">
												<%
													if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2"))||(appCommonFlag.equals("3"))||(appCommonFlag.equals("4"))||(appCommonFlag.equals("5"))|| appCommonFlag.equals("6") || appCommonFlag.equals("11") || appCommonFlag.equals("12") || appCommonFlag.equals("13"))

													{
													%>
													<bean:write name="appForm" property="wcAssessed"/>
													<%}
													else
													{ %>
												
													<bean:message key="inRs"/>
													<%}%>
												</td>
											</tr>
											<tr align="left"> 
												<td class="ColumnBackground" colspan="2" style="text-align: center" >
													<bean:message key="projectOutlay"/>
												</td>
												<td class="ColumnBackground" colspan="1" id="projectOutlay">	
												<%													if((appCommonFlag.equals("0"))||(appCommonFlag.equals("1"))||(appCommonFlag.equals("2"))||(appCommonFlag.equals("3"))||(appCommonFlag.equals("4"))||(appCommonFlag.equals("5"))||(appCommonFlag.equals("6")) || appCommonFlag.equals("11") || appCommonFlag.equals("12") || appCommonFlag.equals("13"))

													{
													%>
													<bean:write name="appForm" property="projectOutlay"/>
													<%}%>
												</td>
												<td class="ColumnBackground" colspan="1">
												<bean:message key="inRs"/>
												<html:hidden property="projectOutlay" name="appForm"/>
												</td>
											</tr>
											<html:hidden property="status" name="appForm"/>											
				
											
				
											<tr align="left"> 
												<td class="ColumnBackground"  align="left" valign="top" colspan="2">
												<% if(appCommonFlag.equals("7")|| appCommonFlag.equals("8")|| appCommonFlag.equals("10")|| appCommonFlag.equals("0")|| appCommonFlag.equals("3")|| appCommonFlag.equals("5")|| appCommonFlag.equals("11")|| appCommonFlag.equals("13")||appCommonFlag.equals("14")||appCommonFlag.equals("19"))
												{
												%>
												<font color="#FF0000" size="2">*</font>
                                                                                                <bean:message key="expiryDate" /> 
												<%
												}
												else
												{
												%>
													<bean:message key="expiryDate" /> 
												<%}%>
												</td>
												<td class="TableData"  align="left" valign="top" id="expiryDate" colspan="2">

												<% 	if((appCommonFlag.equals("11"))||(appCommonFlag.equals("13")))
												 {
												%>
												<%}
												else {
												%>	
                                                  <html:text property="expiryDate" size="20" alt="expiryDate" name="appForm" maxlength="10"/>
													<IMG src="images/CalendarIcon.gif" width="20" onClick="showCalendar('appForm.expiryDate')" align="center">
							
												<%}%>
												</td>
											
										</tr>	
										<TR align="left">
												<TD align="left" valign="top" class="ColumnBackground" colspan="2"><font color="#FF0000" size="2">*</font>&nbsp;Internal Rating</td>								
												<TD align="left" valign="top" class="tableData" colspan="2">
												 <% if(appCommonFlag.equals("11") || appCommonFlag.equals("12") || appCommonFlag.equals("13"))
													 {													
													%>
													<bean:write property="internalRating" name="appForm" />
												<%}
													else
													{ 													
													%>
													<!--<html:text property="internalRating" size="20" alt="internal Rating" name="appForm" maxlength="5" disabled="true"/>-->
													<input type="text" name="internalRating" id="internalRatingField" value="NA" disabled="true"/>
													<%}%>
													<html:hidden name="appForm" property="internalRating" value="NA" />
												</TD>
											  							
									
											</TR>	
											<TR align="left">
												<TD align="left" valign="top" class="ColumnBackground" colspan="2"><font color="#FF0000" size="2">*</font>&nbsp;Whether internal rating of the proposal carried out?</td>								
												<TD align="left" valign="top" class="tableData" colspan="2">
													
												</TD>
													
											</TR>
											<TR align="left">
												<TD align="left" valign="top" class="ColumnBackground" colspan="2">&nbsp;Whether internal rating is equal or greater than investment grade?</td>								
												<TD align="left" valign="top" class="TableData" colspan="2">
												 
												</TD>
													
											</TR>									
										</table>					
									</TD>
								</TR>
									
					</table>							