/**
 * File Name: TcpApplyBean.java<br>
 * CopyRight: Copyright by www.center.china<br>
 * Description:<br>
 * CREATER: ZHUACHEN<br>
 * CreateTime: 2011-7-10<br>
 * Grant: open source to everybody
 */
package com.china.center.oa.tcp.bean;


import java.io.Serializable;

import com.china.center.jdbc.annotation.Entity;
import com.china.center.jdbc.annotation.Id;
import com.china.center.jdbc.annotation.Join;
import com.china.center.jdbc.annotation.Table;
import com.china.center.jdbc.annotation.enums.JoinType;
import com.china.center.oa.publics.bean.PrincipalshipBean;
import com.china.center.oa.publics.bean.StafferBean;
import com.china.center.oa.tcp.constanst.TcpConstanst;


/**
 * 申请总表
 * 
 * @author ZHUZHU
 * @version 2011-7-10
 * @see TcpApplyBean
 * @since 3.0
 */
@Entity
@Table(name = "T_CENTER_TCPAPPLY")
public class TcpApplyBean implements Serializable
{
    @Id
    private String id = "";

    private String name = "";

    private String flowKey = "";

    /**
     * 申请ID
     */
    private String applyId = "";

    /**
     * 申请人
     */
    @Join(tagClass = StafferBean.class, alias = "APPLYER")
    private String applyerId = "";

    @Join(tagClass = PrincipalshipBean.class, type = JoinType.LEFT)
    private String departmentId = "";

    /**
     * 差旅费申请及借款、业务招待费申请及借款、日常办公和固定资产采购申请及借款、对公业务申请及借款
     */
    private int type = TcpConstanst.TCP_APPLYTYPE_TRAVEL;

    private int payType = TcpConstanst.PAYTYPE_GPAY_OK;

    private int stype = TcpConstanst.TCP_STYPE_SAIL;

    private int status = TcpConstanst.TCP_STATUS_INIT;

    private long total = 0;

    private String logTime = "";

    private String description = "";

    /**
     * default constructor
     */
    public TcpApplyBean()
    {
    }

    /**
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the applyId
     */
    public String getApplyId()
    {
        return applyId;
    }

    /**
     * @param applyId
     *            the applyId to set
     */
    public void setApplyId(String applyId)
    {
        this.applyId = applyId;
    }

    /**
     * @return the applyerId
     */
    public String getApplyerId()
    {
        return applyerId;
    }

    /**
     * @param applyerId
     *            the applyerId to set
     */
    public void setApplyerId(String applyerId)
    {
        this.applyerId = applyerId;
    }

    /**
     * @return the type
     */
    public int getType()
    {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(int type)
    {
        this.type = type;
    }

    /**
     * @return the status
     */
    public int getStatus()
    {
        return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(int status)
    {
        this.status = status;
    }

    /**
     * @return the total
     */
    public long getTotal()
    {
        return total;
    }

    /**
     * @param total
     *            the total to set
     */
    public void setTotal(long total)
    {
        this.total = total;
    }

    /**
     * @return the logTime
     */
    public String getLogTime()
    {
        return logTime;
    }

    /**
     * @param logTime
     *            the logTime to set
     */
    public void setLogTime(String logTime)
    {
        this.logTime = logTime;
    }

    /**
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * @return the flowKey
     */
    public String getFlowKey()
    {
        return flowKey;
    }

    /**
     * @param flowKey
     *            the flowKey to set
     */
    public void setFlowKey(String flowKey)
    {
        this.flowKey = flowKey;
    }

    /**
     * @return the departmentId
     */
    public String getDepartmentId()
    {
        return departmentId;
    }

    /**
     * @param departmentId
     *            the departmentId to set
     */
    public void setDepartmentId(String departmentId)
    {
        this.departmentId = departmentId;
    }

    /**
     * @return the stype
     */
    public int getStype()
    {
        return stype;
    }

    /**
     * @param stype
     *            the stype to set
     */
    public void setStype(int stype)
    {
        this.stype = stype;
    }

    /**
     * @return the payType
     */
    public int getPayType()
    {
        return payType;
    }

    /**
     * @param payType
     *            the payType to set
     */
    public void setPayType(int payType)
    {
        this.payType = payType;
    }

    /**
     * Constructs a <code>String</code> with all attributes in name = value format.
     * 
     * @return a <code>String</code> representation of this object.
     */
    public String toString()
    {
        final String TAB = ",";

        StringBuilder retValue = new StringBuilder();

        retValue
            .append("TcpApplyBean ( ")
            .append(super.toString())
            .append(TAB)
            .append("id = ")
            .append(this.id)
            .append(TAB)
            .append("name = ")
            .append(this.name)
            .append(TAB)
            .append("flowKey = ")
            .append(this.flowKey)
            .append(TAB)
            .append("applyId = ")
            .append(this.applyId)
            .append(TAB)
            .append("applyerId = ")
            .append(this.applyerId)
            .append(TAB)
            .append("departmentId = ")
            .append(this.departmentId)
            .append(TAB)
            .append("type = ")
            .append(this.type)
            .append(TAB)
            .append("payType = ")
            .append(this.payType)
            .append(TAB)
            .append("stype = ")
            .append(this.stype)
            .append(TAB)
            .append("status = ")
            .append(this.status)
            .append(TAB)
            .append("total = ")
            .append(this.total)
            .append(TAB)
            .append("logTime = ")
            .append(this.logTime)
            .append(TAB)
            .append("description = ")
            .append(this.description)
            .append(TAB)
            .append(" )");

        return retValue.toString();
    }

}
