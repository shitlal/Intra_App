/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cgtsi.actionform;

import com.cgtsi.actionform.ProductInvForm;

import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.validator.ValidatorForm;

public class QuoteForm extends ValidatorForm {

    private int quoteid;
    private String subject;
    private String potentialid;
    private String potentialname;
    private String quotestage;
    private String validtill;
    private String team;
    private String contactid;
    private String contactname;
    private int assignedto;
    private String assignedtoname;
    private String subtotal;
    private String carrier;
    private String shipping;
    private String inventorymanager;
    private String invmanagername;
    private String adjustment;
    private String total;
    private String taxtype;
    private String s_h_amount;
    private String accountid;
    private String accountname;
    private String terms_conditions;

    private int quotebilladdressid;
    private String bill_city;
    private String bill_code;
    private String bill_country;
    private String bill_state;
    private String bill_street;
    private String bill_pobox;

    private int quoteshipaddressid;
    private String ship_city;
    private String ship_code;
    private String ship_country;
    private String ship_state;
    private String ship_street;
    private String ship_pobox;
    private int userid;
    private String description;
    private String prevquotestage;
    private int[] arrQuoteid;
    private ArrayList prodinvlist;
    
    private String orderId1;
    private String orderName1;
    private double orderRange1;
    
    private String orderId2;
    private String orderName2;
    private double orderRange2;
    
    private String orderId3;
    private String orderName3;
    private double orderRange3;

    /**
     * @return the quoteid
     */
    public int getQuoteid() {
        return quoteid;
    }

    /**
     * @param quoteid the quoteid to set
     */
    public void setQuoteid(int quoteid) {
        this.quoteid = quoteid;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return the potentialid
     */
    public String getPotentialid() {
        return potentialid;
    }

    /**
     * @param potentialid the potentialid to set
     */
    public void setPotentialid(String potentialid) {
        this.potentialid = potentialid;
    }

    /**
     * @return the quotestage
     */
    public String getQuotestage() {
        return quotestage;
    }

    /**
     * @param quotestage the quotestage to set
     */
    public void setQuotestage(String quotestage) {
        this.quotestage = quotestage;
    }

    /**
     * @return the validtill
     */
    public String getValidtill() {
        return validtill;
    }

    /**
     * @param validtill the validtill to set
     */
    public void setValidtill(String validtill) {
        this.validtill = validtill;
    }

    /**
     * @return the team
     */
    public String getTeam() {
        return team;
    }

    /**
     * @param team the team to set
     */
    public void setTeam(String team) {
        this.team = team;
    }

    /**
     * @return the contactid
     */
    public String getContactid() {
        return contactid;
    }

    /**
     * @param contactid the contactid to set
     */
    public void setContactid(String contactid) {
        this.contactid = contactid;
    }

    /**
     * @return the subtotal
     */
    public String getSubtotal() {
        return subtotal;
    }

    /**
     * @param subtotal the subtotal to set
     */
    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    /**
     * @return the carrier
     */
    public String getCarrier() {
        return carrier;
    }

    /**
     * @param carrier the carrier to set
     */
    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    /**
     * @return the inventorymanager
     */
    public String getInventorymanager() {
        return inventorymanager;
    }

    /**
     * @param inventorymanager the inventorymanager to set
     */
    public void setInventorymanager(String inventorymanager) {
        this.inventorymanager = inventorymanager;
    }

    /**
     * @return the adjustment
     */
    public String getAdjustment() {
        return adjustment;
    }

    /**
     * @param adjustment the adjustment to set
     */
    public void setAdjustment(String adjustment) {
        this.adjustment = adjustment;
    }

    /**
     * @return the total
     */
    public String getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(String total) {
        this.total = total;
    }

    /**
     * @return the taxtype
     */
    public String getTaxtype() {
        return taxtype;
    }

    /**
     * @param taxtype the taxtype to set
     */
    public void setTaxtype(String taxtype) {
        this.taxtype = taxtype;
    }

    /**
     * @return the s_h_amount
     */
    public String getS_h_amount() {
        return s_h_amount;
    }

    /**
     * @param s_h_amount the s_h_amount to set
     */
    public void setS_h_amount(String s_h_amount) {
        this.s_h_amount = s_h_amount;
    }

    /**
     * @return the accountid
     */
    public String getAccountid() {
        return accountid;
    }

    /**
     * @param accountid the accountid to set
     */
    public void setAccountid(String accountid) {
        this.accountid = accountid;
    }

    /**
     * @return the terms_conditions
     */
    public String getTerms_conditions() {
        return terms_conditions;
    }

    /**
     * @param terms_conditions the terms_conditions to set
     */
    public void setTerms_conditions(String terms_conditions) {
        this.terms_conditions = terms_conditions;
    }

    /**
     * @return the quotebilladdressid
     */
    public int getQuotebilladdressid() {
        return quotebilladdressid;
    }

    /**
     * @param quotebilladdressid the quotebilladdressid to set
     */
    public void setQuotebilladdressid(int quotebilladdressid) {
        this.quotebilladdressid = quotebilladdressid;
    }

    /**
     * @return the bill_city
     */
    public String getBill_city() {
        return bill_city;
    }

    /**
     * @param bill_city the bill_city to set
     */
    public void setBill_city(String bill_city) {
        this.bill_city = bill_city;
    }

    /**
     * @return the bill_code
     */
    public String getBill_code() {
        return bill_code;
    }

    /**
     * @param bill_code the bill_code to set
     */
    public void setBill_code(String bill_code) {
        this.bill_code = bill_code;
    }

    /**
     * @return the bill_country
     */
    public String getBill_country() {
        return bill_country;
    }

    /**
     * @param bill_country the bill_country to set
     */
    public void setBill_country(String bill_country) {
        this.bill_country = bill_country;
    }

    /**
     * @return the bill_state
     */
    public String getBill_state() {
        return bill_state;
    }

    /**
     * @param bill_state the bill_state to set
     */
    public void setBill_state(String bill_state) {
        this.bill_state = bill_state;
    }

    /**
     * @return the bill_street
     */
    public String getBill_street() {
        return bill_street;
    }

    /**
     * @param bill_street the bill_street to set
     */
    public void setBill_street(String bill_street) {
        this.bill_street = bill_street;
    }

    /**
     * @return the bill_pobox
     */
    public String getBill_pobox() {
        return bill_pobox;
    }

    /**
     * @param bill_pobox the bill_pobox to set
     */
    public void setBill_pobox(String bill_pobox) {
        this.bill_pobox = bill_pobox;
    }

    /**
     * @return the quoteshipaddressid
     */
    public int getQuoteshipaddressid() {
        return quoteshipaddressid;
    }

    /**
     * @param quoteshipaddressid the quoteshipaddressid to set
     */
    public void setQuoteshipaddressid(int quoteshipaddressid) {
        this.quoteshipaddressid = quoteshipaddressid;
    }

    /**
     * @return the ship_city
     */
    public String getShip_city() {
        return ship_city;
    }

    /**
     * @param ship_city the ship_city to set
     */
    public void setShip_city(String ship_city) {
        this.ship_city = ship_city;
    }

    /**
     * @return the ship_code
     */
    public String getShip_code() {
        return ship_code;
    }

    /**
     * @param ship_code the ship_code to set
     */
    public void setShip_code(String ship_code) {
        this.ship_code = ship_code;
    }

    /**
     * @return the ship_country
     */
    public String getShip_country() {
        return ship_country;
    }

    /**
     * @param ship_country the ship_country to set
     */
    public void setShip_country(String ship_country) {
        this.ship_country = ship_country;
    }

    /**
     * @return the ship_state
     */
    public String getShip_state() {
        return ship_state;
    }

    /**
     * @param ship_state the ship_state to set
     */
    public void setShip_state(String ship_state) {
        this.ship_state = ship_state;
    }

    /**
     * @return the ship_street
     */
    public String getShip_street() {
        return ship_street;
    }

    /**
     * @param ship_street the ship_street to set
     */
    public void setShip_street(String ship_street) {
        this.ship_street = ship_street;
    }

    /**
     * @return the ship_pobox
     */
    public String getShip_pobox() {
        return ship_pobox;
    }

    /**
     * @param ship_pobox the ship_pobox to set
     */
    public void setShip_pobox(String ship_pobox) {
        this.ship_pobox = ship_pobox;
    }

    /**
     * @return the productlist
     */
    public ArrayList getProdinvlist() {
        return prodinvlist;
    }

    /**
     * @param productlist the productlist to set
     */
    public void setProdinvlist(ArrayList prodinvlist) {
        this.prodinvlist = prodinvlist;
    }

    public ProductInvForm getProdinv(int index)
    {
         System.out.println("Inside tax getting"+index);
         if(prodinvlist==null)
             prodinvlist=new ArrayList();
         if(prodinvlist.size()<=index)
             prodinvlist.add(new ProductInvForm());
         return (ProductInvForm)prodinvlist.get(index);

    }
   
    public void setProdinv(int index,ProductInvForm prodinv)
    {
         System.out.println("Inside tax setting");
         prodinvlist.set(index,prodinv);
    }
    
    /**
     * @return the assignedto
     */
    public int getAssignedto() {
        return assignedto;
    }

    /**
     * @param assignedto the assignedto to set
     */
    public void setAssignedto(int assignedto) {
        this.assignedto = assignedto;
    }

    /**
     * @return the potentialname
     */
    public String getPotentialname() {
        return potentialname;
    }

    /**
     * @param potentialname the potentialname to set
     */
    public void setPotentialname(String potentialname) {
        this.potentialname = potentialname;
    }

    /**
     * @return the contactname
     */
    public String getContactname() {
        return contactname;
    }

    /**
     * @param contactname the contactname to set
     */
    public void setContactname(String contactname) {
        this.contactname = contactname;
    }

    /**
     * @return the assignedtoname
     */
    public String getAssignedtoname() {
        return assignedtoname;
    }

    /**
     * @param assignedtoname the assignedtoname to set
     */
    public void setAssignedtoname(String assignedtoname) {
        this.assignedtoname = assignedtoname;
    }

    /**
     * @return the shipping
     */
    public String getShipping() {
        return shipping;
    }

    /**
     * @param shipping the shipping to set
     */
    public void setShipping(String shipping) {
        this.shipping = shipping;
    }

    /**
     * @return the accountname
     */
    public String getAccountname() {
        return accountname;
    }

    /**
     * @param accountname the accountname to set
     */
    public void setAccountname(String accountname) {
        this.accountname = accountname;
    }

    /**
     * @return the userid
     */
    public int getUserid() {
        return userid;
    }

    /**
     * @param userid the userid to set
     */
    public void setUserid(int userid) {
        this.userid = userid;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the arrQuoteid
     */
    public int[] getArrQuoteid() {
        return arrQuoteid;
    }

    /**
     * @param arrQuoteid the arrQuoteid to set
     */
    public void setArrQuoteid(int[] arrQuoteid) {
        this.arrQuoteid = arrQuoteid;
    }

    /**
     * @return the invmanagername
     */
    public String getInvmanagername() {
        return invmanagername;
    }

    /**
     * @param invmanagername the invmanagername to set
     */
    public void setInvmanagername(String invmanagername) {
        this.invmanagername = invmanagername;
    }

    /**
     * @return the prevquotestage
     */
    public String getPrevquotestage() {
        return prevquotestage;
    }

    /**
     * @param prevquotestage the prevquotestage to set
     */
    public void setPrevquotestage(String prevquotestage) {
        this.prevquotestage = prevquotestage;
    }

    /*
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        if (getName() == null || getName().length() < 1) {
            errors.add("name", new ActionMessage("error.name.required"));
            // TODO: add 'error.name.required' key to your resources
        }
        return errors;
    }
     */

    public void setOrderId1(String orderId) {
        this.orderId1 = orderId;
    }

    public String getOrderId1() {
        return orderId1;
    }

    public void setOrderName1(String orderName) {
        this.orderName1 = orderName;
    }

    public String getOrderName1() {
        return orderName1;
    }

    public void setOrderRange1(double orderRange) {
        this.orderRange1 = orderRange;
    }

    public double getOrderRange1() {
        return orderRange1;
    }

    public void setOrderId2(String orderId2) {
        this.orderId2 = orderId2;
    }

    public String getOrderId2() {
        return orderId2;
    }

    public void setOrderName2(String orderName2) {
        this.orderName2 = orderName2;
    }

    public String getOrderName2() {
        return orderName2;
    }

    public void setOrderRange2(double orderRange2) {
        this.orderRange2 = orderRange2;
    }

    public double getOrderRange2() {
        return orderRange2;
    }

    public void setOrderId3(String orderId3) {
        this.orderId3 = orderId3;
    }

    public String getOrderId3() {
        return orderId3;
    }

    public void setOrderName3(String orderName3) {
        this.orderName3 = orderName3;
    }

    public String getOrderName3() {
        return orderName3;
    }

    public void setOrderRange3(double orderRange3) {
        this.orderRange3 = orderRange3;
    }

    public double getOrderRange3() {
        return orderRange3;
    }
}
