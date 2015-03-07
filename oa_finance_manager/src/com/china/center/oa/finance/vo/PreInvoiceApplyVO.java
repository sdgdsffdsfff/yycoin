package com.china.center.oa.finance.vo;

import com.china.center.jdbc.annotation.Entity;
import com.china.center.jdbc.annotation.Ignore;
import com.china.center.jdbc.annotation.Relationship;
import com.china.center.oa.finance.bean.PreInvoiceApplyBean;

@SuppressWarnings("serial")
@Entity(inherit = true)
public class PreInvoiceApplyVO extends PreInvoiceApplyBean
{
    @Relationship(relationField = "stafferId")
    private String stafferName = "";

    @Relationship(relationField = "departmentId")
    private String departmentName = "";

    @Relationship(relationField = "provinceId")
    private String provinceName = "";

    @Relationship(relationField = "cityId")
    private String cityName = "";

    /**
     * 运输方式1
     */
    @Relationship(relationField = "transport1")
    private String transportName1 = "";

    /**
     * 运输方式2
     */
    @Relationship(relationField = "transport2")
    private String transportName2 = "";

    /**
     * 当前处理人
     */
    @Ignore
    private String processer = "";

    /**
     * 流程描述
     */
    @Ignore
    private String flowDescription = "";

    @Ignore
    private String showTotal = "";
    
    @Ignore
    private String showLastMoney = "";
    
	/**
	 * 
	 */
	public PreInvoiceApplyVO()
	{
	}

	/**
	 * @return the stafferName
	 */
	public String getStafferName()
	{
		return stafferName;
	}

	/**
	 * @param stafferName the stafferName to set
	 */
	public void setStafferName(String stafferName)
	{
		this.stafferName = stafferName;
	}

	/**
	 * @return the departmentName
	 */
	public String getDepartmentName()
	{
		return departmentName;
	}

	/**
	 * @param departmentName the departmentName to set
	 */
	public void setDepartmentName(String departmentName)
	{
		this.departmentName = departmentName;
	}

	/**
	 * @return the processer
	 */
	public String getProcesser()
	{
		return processer;
	}

	/**
	 * @param processer the processer to set
	 */
	public void setProcesser(String processer)
	{
		this.processer = processer;
	}

	/**
	 * @return the flowDescription
	 */
	public String getFlowDescription()
	{
		return flowDescription;
	}

	/**
	 * @param flowDescription the flowDescription to set
	 */
	public void setFlowDescription(String flowDescription)
	{
		this.flowDescription = flowDescription;
	}

	/**
	 * @return the showTotal
	 */
	public String getShowTotal()
	{
		return showTotal;
	}

	/**
	 * @param showTotal the showTotal to set
	 */
	public void setShowTotal(String showTotal)
	{
		this.showTotal = showTotal;
	}

	/**
	 * @return the showLastMoney
	 */
	public String getShowLastMoney()
	{
		return showLastMoney;
	}

	/**
	 * @param showLastMoney the showLastMoney to set
	 */
	public void setShowLastMoney(String showLastMoney)
	{
		this.showLastMoney = showLastMoney;
	}

    /**
     * @return the provinceName
     */
    public String getProvinceName() {
        return provinceName;
    }

    /**
     * @param provinceName the provinceName to set
     */
    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    /**
     * @return the cityName
     */
    public String getCityName() {
        return cityName;
    }

    /**
     * @param cityName the cityName to set
     */
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    /**
     * @return the transportName1
     */
    public String getTransportName1() {
        return transportName1;
    }

    /**
     * @param transportName1 the transportName1 to set
     */
    public void setTransportName1(String transportName1) {
        this.transportName1 = transportName1;
    }

    /**
     * @return the transportName2
     */
    public String getTransportName2() {
        return transportName2;
    }

    /**
     * @param transportName2 the transportName2 to set
     */
    public void setTransportName2(String transportName2) {
        this.transportName2 = transportName2;
    }
}
