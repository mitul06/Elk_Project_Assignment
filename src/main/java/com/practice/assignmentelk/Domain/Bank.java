package com.practice.assignmentelk.Domain;

import java.util.Date;

public class Bank {
    private int depoid;
    private String depoName;
    private String branchName;
    private int depoAmt;
    private Date depoDate;
    private String custName;
    private String custCity;


    public Bank() {
    }

    public Bank(int depoid, String depoName, String branchName, int depoAmt, Date depoDate, String custName, String custCity) {
        this.depoid = depoid;
        this.depoName = depoName;
        this.branchName = branchName;
        this.depoAmt = depoAmt;
        this.depoDate = depoDate;
        this.custName = custName;
        this.custCity = custCity;
    }

    public int getDepoid() {
        return depoid;
    }

    public void setDepoid(int depoid) {
        this.depoid = depoid;
    }

    public String getDepoName() {
        return depoName;
    }

    public void setDepoName(String depoName) {
        this.depoName = depoName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public int getDepoAmt() {
        return depoAmt;
    }

    public void setDepoAmt(int depoAmt) {
        this.depoAmt = depoAmt;
    }

    public Date getDepoDate() {
        return depoDate;
    }

    public void setDepoDate(Date depoDate) {
        this.depoDate = depoDate;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getCustCity() {
        return custCity;
    }

    public void setCustCity(String custCity) {
        this.custCity = custCity;
    }
}
