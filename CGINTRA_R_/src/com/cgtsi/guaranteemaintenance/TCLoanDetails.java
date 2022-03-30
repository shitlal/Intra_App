package com.cgtsi.guaranteemaintenance;

import java.util.Date;

public class TCLoanDetails {


    private String cgpan;
    private Date firstDisbursementDate;
    private Date lastDisbursementDate;
    private double disbursementAmount;
    private double principalRepayment;
    private double interestAndOtherCharges;
    private double osAsOnSancDtPrinciple;
    private double osAsOnSancDtInterest;
    
    private double osAsOnNpaDtPrinciple;
    private double osAsOnNpaDtInterest;
    private Date firstInstalmentDate;
    private String moratoriumprincipal;
    private String moratoriuminterest;
    private String subsidyflag;
    private String subsidyrcvdflag;
    private String subsidyadjustedflag;


    /**
     * @param cgpan
     */
    public void setCgpan(String cgpan) {
        this.cgpan = cgpan;
    }

    /**
     * @return
     */
    public String getCgpan() {
        return cgpan;
    }

    /**
     * @param firstDisbursementDate
     */
    public void setFirstDisbursementDate(Date firstDisbursementDate) {
        this.firstDisbursementDate = firstDisbursementDate;
    }

    /**
     * @return
     */
    public Date getFirstDisbursementDate() {
        return firstDisbursementDate;
    }

    /**
     * @param lastDisbursementDate
     */
    public void setLastDisbursementDate(Date lastDisbursementDate) {
        this.lastDisbursementDate = lastDisbursementDate;
    }

    /**
     * @return
     */
    public Date getLastDisbursementDate() {
        return lastDisbursementDate;
    }

    public void setDisbursementAmount(double disbursementAmount) {
        this.disbursementAmount = disbursementAmount;
    }

    public double getDisbursementAmount() {
        return disbursementAmount;
    }

    public void setPrincipalRepayment(double principalRepayment) {
        this.principalRepayment = principalRepayment;
    }

    public double getPrincipalRepayment() {
        return principalRepayment;
    }

    public void setInterestAndOtherCharges(double interestAndOtherCharges) {
        this.interestAndOtherCharges = interestAndOtherCharges;
    }

    public double getInterestAndOtherCharges() {
        return interestAndOtherCharges;
    }

    public void setOsAsOnSancDtPrinciple(double osAsOnSancDtPrinciple) {
        this.osAsOnSancDtPrinciple = osAsOnSancDtPrinciple;
    }

    public double getOsAsOnSancDtPrinciple() {
        return osAsOnSancDtPrinciple;
    }

    public void setOsAsOnSancDtInterest(double osAsOnSancDtInterest) {
        this.osAsOnSancDtInterest = osAsOnSancDtInterest;
    }

    public double getOsAsOnSancDtInterest() {
        return osAsOnSancDtInterest;
    }
    
    public TCLoanDetails() {
    }

    public void setFirstInstalmentDate(Date firstInstalmentDate) {
        this.firstInstalmentDate = firstInstalmentDate;
    }

    public Date getFirstInstalmentDate() {
        return firstInstalmentDate;
    }

    public void setMoratoriumprincipal(String moratoriumprincipal) {
        this.moratoriumprincipal = moratoriumprincipal;
    }

    public String getMoratoriumprincipal() {
        return moratoriumprincipal;
    }

    public void setMoratoriuminterest(String moratoriuminterest) {
        this.moratoriuminterest = moratoriuminterest;
    }

    public String getMoratoriuminterest() {
        return moratoriuminterest;
    }

    public void setSubsidyflag(String subsidyflag) {
        this.subsidyflag = subsidyflag;
    }

    public String getSubsidyflag() {
        return subsidyflag;
    }

    public void setSubsidyrcvdflag(String subsidyrcvdflag) {
        this.subsidyrcvdflag = subsidyrcvdflag;
    }

    public String getSubsidyrcvdflag() {
        return subsidyrcvdflag;
    }

    public void setSubsidyadjustedflag(String subsidyadjustedflag) {
        this.subsidyadjustedflag = subsidyadjustedflag;
    }

    public String getSubsidyadjustedflag() {
        return subsidyadjustedflag;
    }

    public void setOsAsOnNpaDtPrinciple(double osAsOnNpaDtPrinciple) {
        this.osAsOnNpaDtPrinciple = osAsOnNpaDtPrinciple;
    }

    public double getOsAsOnNpaDtPrinciple() {
        return osAsOnNpaDtPrinciple;
    }

    public void setOsAsOnNpaDtInterest(double osAsOnNpaDtInterest) {
        this.osAsOnNpaDtInterest = osAsOnNpaDtInterest;
    }

    public double getOsAsOnNpaDtInterest() {
        return osAsOnNpaDtInterest;
    }
}
