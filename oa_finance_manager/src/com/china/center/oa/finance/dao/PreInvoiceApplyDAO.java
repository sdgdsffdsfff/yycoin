package com.china.center.oa.finance.dao;

import com.china.center.jdbc.inter.DAO;
import com.china.center.oa.finance.bean.PreInvoiceApplyBean;
import com.china.center.oa.finance.vo.PreInvoiceApplyVO;

public interface PreInvoiceApplyDAO extends DAO<PreInvoiceApplyBean, PreInvoiceApplyVO>
{
	int updateStatus(String id, int status);

    /**
     * 2015/3/5 更新发票号码
     * @param id
     * @param invoiceNumber
     * @return
     */
    int updateInvoiceNumber(String id, String invoiceNumber);
	
	int updateInvoiceMoney(String id, Long invoiceMoney);
	
	int countOverTimeBeans(String stafferId);
}
