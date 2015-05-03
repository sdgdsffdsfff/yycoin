/**
 * File Name: SailConfigManager.java<br>
 * CopyRight: Copyright by www.center.china<br>
 * Description:<br>
 * CREATER: ZHUACHEN<br>
 * CreateTime: 2011-12-17<br>
 * Grant: open source to everybody
 */
package com.china.center.oa.product.manager;


import com.center.china.osgi.publics.User;
import com.china.center.common.MYException;
import com.china.center.oa.product.bean.ProductVSGiftBean;
import com.china.center.oa.product.vo.ProductVSGiftVO;
import java.util.List;


/**
 * GiftConfigManager
 * 
 * @author ZHUZHU
 * @version 2015-05-01
 * @see com.china.center.oa.product.manager.GiftConfigManager
 * @since 3.0
 */
public interface GiftConfigManager
{
    boolean deleteBean(User user, String id)
        throws MYException;

    ProductVSGiftVO findVO(String id)
        throws MYException;

    boolean addBean(User user, ProductVSGiftBean bean)
        throws MYException;

    boolean updateBean(User user, ProductVSGiftBean bean)
        throws MYException;

}
