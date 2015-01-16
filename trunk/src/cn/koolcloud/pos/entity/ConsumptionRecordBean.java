package cn.koolcloud.pos.entity;

import java.util.Map;
import java.util.TreeMap;

public class ConsumptionRecordBean {
    private String merchName;
    private String merchNo;
    private String termId;
    private String batchNo;
    private String operator;
    private String printTime;
    private Map<String, String> summaryMap;

    public Map<String, String> getSummaryMap() {
        return summaryMap;
    }

    public void setSummaryMap(TreeMap<String, String> summaryMap) {
        this.summaryMap = summaryMap;
    }

    public String getMerchName() {
        return merchName;
    }

    public void setMerchName(String merchName) {
        this.merchName = merchName;
    }

    public String getMerchNo() {
        return merchNo;
    }

    public void setMerchNo(String merchNo) {
        this.merchNo = merchNo;
    }

    public String getTermId() {
        return termId;
    }

    public void setTerId(String termId) {
        this.termId = termId;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getPrintTime() {
        return printTime;
    }

    public void setPrintTime(String printTime) {
        this.printTime = printTime;
    }
}
