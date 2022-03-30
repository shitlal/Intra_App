/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cgtsi.actionform;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;


public class ProductInvForm extends org.apache.struts.action.ActionForm {
    
    private int id;
    private String productid;
    private String productname;
    private int sequenceno;
    private String quantity;
    private String listprice;
    private String discount_percent;
    private String discount_amount;
    private String comments;
    private String description;
    private double tax1;
    private double tax2;
    private double tax3;
    private String qtyinstock;
    private String discount;
    private int deleted;
    /**
     *
     */
    public ProductInvForm() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the productid
     */
    public String getProductid() {
        return productid;
    }

    /**
     * @param productid the productid to set
     */
    public void setProductid(String productid) {
        this.productid = productid;
    }

    /**
     * @return the productname
     */
    public String getProductname() {
        return productname;
    }

    /**
     * @param productname the productname to set
     */
    public void setProductname(String productname) {
        this.productname = productname;
    }

    /**
     * @return the sequenceno
     */
    public int getSequenceno() {
        return sequenceno;
    }

    /**
     * @param sequenceno the sequenceno to set
     */
    public void setSequenceno(int sequenceno) {
        this.sequenceno = sequenceno;
    }

    /**
     * @return the quantity
     */
    public String getQuantity() {
        return quantity;
    }

    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    /**
     * @return the listprice
     */
    public String getListprice() {
        return listprice;
    }

    /**
     * @param listprice the listprice to set
     */
    public void setListprice(String listprice) {
        this.listprice = listprice;
    }

    /**
     * @return the discount_percent
     */
    public String getDiscount_percent() {
        return discount_percent;
    }

    /**
     * @param discount_percent the discount_percent to set
     */
    public void setDiscount_percent(String discount_percent) {
        this.discount_percent = discount_percent;
    }

    /**
     * @return the discount_amount
     */
    public String getDiscount_amount() {
        return discount_amount;
    }

    /**
     * @param discount_amount the discount_amount to set
     */
    public void setDiscount_amount(String discount_amount) {
        this.discount_amount = discount_amount;
    }

    /**
     * @return the comment
     */
    public String getComments() {
        return comments;
    }

    /**
     * @param comment the comment to set
     */
    public void setComments(String comments) {
        this.comments = comments;
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
     * @return the tax1
     */
    public double getTax1() {
        return tax1;
    }

    /**
     * @param tax1 the tax1 to set
     */
    public void setTax1(double tax1) {
        this.tax1 = tax1;
    }

    /**
     * @return the tax2
     */
    public double getTax2() {
        return tax2;
    }

    /**
     * @param tax2 the tax2 to set
     */
    public void setTax2(double tax2) {
        this.tax2 = tax2;
    }

    /**
     * @return the tax3
     */
    public double getTax3() {
        return tax3;
    }

    /**
     * @param tax3 the tax3 to set
     */
    public void setTax3(double tax3) {
        this.tax3 = tax3;
    }

    /**
     * @return the qtyinstock
     */
    public String getQtyinstock() {
        return qtyinstock;
    }

    /**
     * @param qtyinstock the qtyinstock to set
     */
    public void setQtyinstock(String qtyinstock) {
        this.qtyinstock = qtyinstock;
    }

    /**
     * @return the discount
     */
    public String getDiscount() {
        return discount;
    }

    /**
     * @param discount the discount to set
     */
    public void setDiscount(String discount) {
        this.discount = discount;
    }

    /**
     * @return the deleted
     */
    public int getDeleted() {
        return deleted;
    }

    /**
     * @param deleted the deleted to set
     */
    public void setDeleted(int deleted) {
        this.deleted = deleted;
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
}
