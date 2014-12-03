package com.china.center.oa.sail.bean;


import com.china.center.jdbc.annotation.*;
import java.io.Serializable;


/**
 * 库单(销售单、入库单) CORE
 * 
 * @author Simon
 * @version 2014-12-02
 * @see com.china.center.oa.sail.bean.BranchRelationBean
 * @since 1.0
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "T_CENTER_BRANCH_RELATION")
public class BranchRelationBean implements Serializable
{
    /**
     * 同客户ID
     */
    @Id
    private String id = "";

    /**
     * 支行名称
     * 同客户名称
     */
    private String subBranchName = "";

    /**
     * 分行名称
     */
    private String branchName = "";

    /**
     * 分行邮件地址
     */
    private String subBranchMail = "";

    /**
     * 支行邮件地址
     */
    private String branchMail = "";

    /**
     * 内容为1/0，1代表发送，0代表不发送
     */
    private int sendMailFlag = 0;

    /**
     * 抄送分行
     * 内容为1/0，1代表同时抄送分行，0代表不发送
     */
    private int copyToBranchFlag = 0;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubBranchName() {
        return subBranchName;
    }

    public void setSubBranchName(String subBranchName) {
        this.subBranchName = subBranchName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getSubBranchMail() {
        return subBranchMail;
    }

    public void setSubBranchMail(String subBranchMail) {
        this.subBranchMail = subBranchMail;
    }

    public String getBranchMail() {
        return branchMail;
    }

    public void setBranchMail(String branchMail) {
        this.branchMail = branchMail;
    }

    public int getSendMailFlag() {
        return sendMailFlag;
    }

    public void setSendMailFlag(int sendMailFlag) {
        this.sendMailFlag = sendMailFlag;
    }

    public int getCopyToBranchFlag() {
        return copyToBranchFlag;
    }

    public void setCopyToBranchFlag(int copyToBranchFlag) {
        this.copyToBranchFlag = copyToBranchFlag;
    }

    @Override
    public String toString() {
        return "BranchRelationBean{" +
                "id='" + id + '\'' +
                ", subBranchName='" + subBranchName + '\'' +
                ", branchName='" + branchName + '\'' +
                ", subBranchMail='" + subBranchMail + '\'' +
                ", branchMail='" + branchMail + '\'' +
                ", sendMailFlag=" + sendMailFlag +
                ", copyToBranchFlag=" + copyToBranchFlag +
                '}';
    }
}
