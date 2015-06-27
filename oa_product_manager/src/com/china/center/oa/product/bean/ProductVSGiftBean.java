package com.china.center.oa.product.bean;

import java.io.Serializable;

import com.china.center.jdbc.annotation.*;
import com.china.center.jdbc.annotation.enums.Element;
import com.china.center.jdbc.annotation.enums.JoinType;

@SuppressWarnings("serial")
@Entity(name = "中信产品对应赠品")
@Table(name = "T_CENTER_VS_GIFT")
public class ProductVSGiftBean implements Serializable
{
	@Id(autoIncrement = true)
	private String id = "";
	
	@FK
    @Html(title = "销售商品品名", type = Element.INPUT, name = "productName", readonly = true)
	@Join(tagClass = ProductBean.class, type = JoinType.LEFT, alias = "P1")
	private String productId = "";

    @Html(title = "赠送商品品名", type = Element.INPUT, name = "giftProductName", readonly = true)
	@Join(tagClass = ProductBean.class, type = JoinType.LEFT, alias = "P2")
	private String giftProductId = "";

    @Html(title = "赠送商品数量", must = true, maxLength = 100)
	private int amount = 0;

    @Html(title = "销售商品数量", must = true, maxLength = 100)
    private int sailAmount = 0;

    /**
     * 活动描述
     */
    @Html(title = "活动描述", must = true, maxLength = 100)
    private String activity = "";

    /**
     * 适用银行
     */
    @Html(title = "适用银行", must = true, maxLength = 100)
    private String bank = "";

    /**
     * 开始日期
     */
    @Html(title = "开始日期", must = true, type= Element.DATE)
    private String beginDate = "";

    /**
     * 结束日期
     */
    @Html(title = "结束日期", must = true,type=Element.DATE)
    private String endDate = "";

    /**
     * 备注
     */
    @Html(title = "备注", type = Element.TEXTAREA, maxLength = 255)
    private String description = "";

	public ProductVSGiftBean()
	{
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getProductId()
	{
		return productId;
	}

	public void setProductId(String productId)
	{
		this.productId = productId;
	}

	public String getGiftProductId()
	{
		return giftProductId;
	}

	public void setGiftProductId(String giftProductId)
	{
		this.giftProductId = giftProductId;
	}

	public int getAmount()
	{
		return amount;
	}

	public void setAmount(int amount)
	{
		this.amount = amount;
	}

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public int getSailAmount() {
        return sailAmount;
    }

    public void setSailAmount(int sailAmount) {
        this.sailAmount = sailAmount;
    }

    @Override
    public String toString() {
        return "ProductVSGiftBean{" +
                "id='" + id + '\'' +
                ", productId='" + productId + '\'' +
                ", giftProductId='" + giftProductId + '\'' +
                ", amount=" + amount +
                ", sailAmount=" + sailAmount +
                ", activity='" + activity + '\'' +
                ", bank='" + bank + '\'' +
                ", beginDate='" + beginDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
