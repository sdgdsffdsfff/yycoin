/**
 * File Name: SailConfigManagerImpl.java<br>
 * CopyRight: Copyright by www.center.china<br>
 * Description:<br>
 * CREATER: ZHUACHEN<br>
 * CreateTime: 2011-12-17<br>
 * Grant: open source to everybody
 */
package com.china.center.oa.product.manager.impl;


import com.center.china.osgi.publics.User;
import com.china.center.common.MYException;
import com.china.center.common.taglib.DefinedCommon;
import com.china.center.jdbc.expression.Expression;
import com.china.center.jdbc.util.ConditionParse;
import com.china.center.oa.product.bean.ProductBean;
import com.china.center.oa.product.bean.ProductVSGiftBean;
import com.china.center.oa.product.dao.ProductVSGiftDAO;
import com.china.center.oa.product.manager.GiftConfigManager;
import com.china.center.oa.product.vo.ProductVSGiftVO;
import com.china.center.oa.publics.dao.CommonDAO;
import com.china.center.tools.JudgeTools;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.china.center.spring.iaop.annotation.IntegrationAOP;
import org.springframework.transaction.annotation.Transactional;


/**
 * GiftConfigManagerImpl
 * 
 * @author ZHUZHU
 * @version 2015-05-1
 * @see com.china.center.oa.product.manager.impl.GiftConfigManagerImpl
 * @since 3.0
 */
@IntegrationAOP
public class GiftConfigManagerImpl implements GiftConfigManager
{
    private final Log _logger = LogFactory.getLog(getClass());

    private ProductVSGiftDAO productVSGiftDAO = null;

    private CommonDAO commonDAO = null;


    /**
     * default constructor
     */
    public GiftConfigManagerImpl()
    {
    }

    @Transactional(rollbackFor = MYException.class)
    @Override
    public boolean addBean(User user, ProductVSGiftBean bean) throws MYException {

        JudgeTools.judgeParameterIsNull(user, bean);

        bean.setId(commonDAO.getSquenceString20());

        _logger.info("***save ProductVSGiftBean bean:"+bean);
        return this.productVSGiftDAO.saveEntityBean(bean);
    }

    @Override
    @Transactional(rollbackFor = MYException.class)
    public boolean updateBean(User user, ProductVSGiftBean bean) throws MYException {
        JudgeTools.judgeParameterIsNull(user, bean);
        return this.productVSGiftDAO.updateEntityBean(bean);
    }

    @Override
    @Transactional(rollbackFor = MYException.class)
    public boolean deleteBean(User user, String id) throws MYException {
        JudgeTools.judgeParameterIsNull(user, id);

        ProductVSGiftBean conf = this.productVSGiftDAO.find(id);

        if (conf == null)
        {
            throw new MYException("数据错误,请确认操作");
        }

        return productVSGiftDAO.deleteEntityBean(id);
    }

    public ProductVSGiftVO findVO(String id)
        throws MYException
    {
        JudgeTools.judgeParameterIsNull(id);

        ProductVSGiftVO obj = this.productVSGiftDAO.findVO(id);

        if (obj == null)
        {
            throw new MYException("数据错误,请确认操作");
        }

        return obj;
    }

    /**
     * @return the commonDAO
     */
    public CommonDAO getCommonDAO()
    {
        return commonDAO;
    }

    /**
     * @param commonDAO
     *            the commonDAO to set
     */
    public void setCommonDAO(CommonDAO commonDAO)
    {
        this.commonDAO = commonDAO;
    }

    public ProductVSGiftDAO getProductVSGiftDAO() {
        return productVSGiftDAO;
    }

    public void setProductVSGiftDAO(ProductVSGiftDAO productVSGiftDAO) {
        this.productVSGiftDAO = productVSGiftDAO;
    }
}
