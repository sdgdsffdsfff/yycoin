/**
 * File Name: TcpApplyBean.java<br>
 * CopyRight: Copyright by www.center.china<br>
 * Description:<br>
 * CREATER: ZHUACHEN<br>
 * CreateTime: 2011-7-10<br>
 * Grant: open source to everybody
 */
package com.china.center.oa.tcp.bean;

import com.china.center.jdbc.annotation.Entity;
import com.china.center.jdbc.annotation.FK;
import com.china.center.jdbc.annotation.Id;
import com.china.center.jdbc.annotation.Table;
import java.io.Serializable;


/**
 * 中收激励统计表
 * 
 * @author ZHUZHU
 * @version 2015-04-09
 * @see com.china.center.oa.tcp.bean.TcpIbReportBean
 * @since 3.0
 */
@Entity
@Table(name = "T_CENTER_TCPIBREPORT")
public class TcpIbReportBean implements Serializable
{
    @Id
    private String id = "";

    private String customerId = "";

    private String customerName = "";

    /**
     * 该客户的中收金额总数
     */
    private double ibMoneyTotal = 0.0d;

    /**
     * 该客户的激励金额总数
     */
    private double motivationMoneyTotal = 0.0d;



    /**
     * default constructor
     */
    public TcpIbReportBean()
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

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public double getIbMoneyTotal() {
        return ibMoneyTotal;
    }

    public void setIbMoneyTotal(double ibMoneyTotal) {
        this.ibMoneyTotal = ibMoneyTotal;
    }

    public double getMotivationMoneyTotal() {
        return motivationMoneyTotal;
    }

    public void setMotivationMoneyTotal(double motivationMoneyTotal) {
        this.motivationMoneyTotal = motivationMoneyTotal;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    @Override
    public String toString() {
        return "TcpIbReportBean{" +
                "id='" + id + '\'' +
                ", customerId='" + customerId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", ibMoneyTotal=" + ibMoneyTotal +
                ", motivationMoneyTotal=" + motivationMoneyTotal +
                '}';
    }
}
