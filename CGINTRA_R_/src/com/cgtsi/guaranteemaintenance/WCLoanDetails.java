package com.cgtsi.guaranteemaintenance;

import java.util.Date;

public class WCLoanDetails {

    private String cgpan;
    private double osAsOnSancDtPrincipal;
    private double osAsOnSancDtInterest;
    private double disbursementAmount;
    
    private double osAsOnNpaDtPrincipal;
    private double osAsOnNpaDtInterest;
    private Date firstInstalmentDate;
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
     * @param osAsOnSancDtPrincipal
     */
    public void setOsAsOnSancDtPrincipal(double osAsOnSancDtPrincipal) {
        this.osAsOnSancDtPrincipal = osAsOnSancDtPrincipal;
    }

    /**
     * @return
     */
    public double getOsAsOnSancDtPrincipal() {
        return osAsOnSancDtPrincipal;
    }

    /**
     * @param osAsOnSancDtInterest
     */
    public void setOsAsOnSancDtInterest(double osAsOnSancDtInterest) {
        this.osAsOnSancDtInterest = osAsOnSancDtInterest;
    }

    /**
     * @return
     */
    public double getOsAsOnSancDtInterest() {
        return osAsOnSancDtInterest;
    }

    /**
     * @param disbursementAmount
     */
    public void setDisbursementAmount(double disbursementAmount) {
        this.disbursementAmount = disbursementAmount;
    }

    /**
     * @return
     */
    public double getDisbursementAmount() {
        return disbursementAmount;
    }
    
    public WCLoanDetails() {
    }

    public void setOsAsOnNpaDtPrincipal(double osAsOnNpaDtPrincipal) {
        this.osAsOnNpaDtPrincipal = osAsOnNpaDtPrincipal;
    }

    public double getOsAsOnNpaDtPrincipal() {
        return osAsOnNpaDtPrincipal;
    }

    public void setOsAsOnNpaDtInterest(double osAsOnNpaDtInterest) {
        this.osAsOnNpaDtInterest = osAsOnNpaDtInterest;
    }

    public double getOsAsOnNpaDtInterest() {
        return osAsOnNpaDtInterest;
    }

    public void setFirstInstalmentDate(Date firstInstalmentDate) {
        this.firstInstalmentDate = firstInstalmentDate;
    }

    public Date getFirstInstalmentDate() {
        return firstInstalmentDate;
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
}
