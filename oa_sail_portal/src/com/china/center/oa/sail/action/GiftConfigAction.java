/**
 * File Name: SailConfigAction.java<br>
 * CopyRight: Copyright by www.center.china<br>
 * Description:<br>
 * CREATER: ZHUACHEN<br>
 * CreateTime: 2011-12-17<br>
 * Grant: open source to everybody
 */
package com.china.center.oa.sail.action;


import com.center.china.osgi.publics.User;
import com.china.center.actionhelper.common.ActionTools;
import com.china.center.actionhelper.common.JSONTools;
import com.china.center.actionhelper.common.KeyConstant;
import com.china.center.actionhelper.json.AjaxResult;
import com.china.center.actionhelper.query.HandleResult;
import com.china.center.common.MYException;
import com.china.center.jdbc.util.ConditionParse;
import com.china.center.oa.product.dao.ProductVSGiftDAO;
import com.china.center.oa.product.vo.ProductVSGiftVO;
import com.china.center.oa.publics.Helper;
import com.china.center.oa.publics.bean.PrincipalshipBean;
import com.china.center.oa.publics.dao.ShowDAO;
import com.china.center.oa.publics.manager.OrgManager;
import com.china.center.oa.sail.bean.SailConfBean;
import com.china.center.oa.sail.dao.SailConfDAO;
import com.china.center.oa.sail.dao.SailConfigDAO;
import com.china.center.oa.sail.manager.SailConfigManager;
import com.china.center.oa.sail.vo.SailConfVO;
import com.china.center.tools.BeanUtil;
import com.china.center.tools.CommonTools;
import com.china.center.tools.StringTools;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * GiftConfigAction
 * 
 * @author ZHUZHU
 * @version 2015-04-22
 * @see com.china.center.oa.sail.action.GiftConfigAction
 * @since 3.0
 */
public class GiftConfigAction extends DispatchAction
{
    private final Log _logger = LogFactory.getLog(getClass());

    private ProductVSGiftDAO productVSGiftDAO = null;

    private static final String QUERYSAILCONFIG = "queryGiftConfig";

    /**
     * default constructor
     */
    public GiftConfigAction()
    {
    }

    /**
     * queryGiftConfig
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws javax.servlet.ServletException
     */
    public ActionForward queryGiftConfig(ActionMapping mapping, ActionForm form,
                                         HttpServletRequest request, HttpServletResponse response)
        throws ServletException
    {
        _logger.info("queryGiftConfig********************");
        ConditionParse condtion = new ConditionParse();

        condtion.addWhereStr();

        ActionTools.processJSONQueryCondition(QUERYSAILCONFIG, request, condtion);

        String jsonstr = ActionTools.queryVOByJSONAndToString(QUERYSAILCONFIG, request, condtion,
            this.productVSGiftDAO, new HandleResult<ProductVSGiftVO>()
            {
                public void handle(ProductVSGiftVO obj)
                {

                }
            });
        _logger.info("queryGiftConfig********************"+jsonstr);
        return JSONTools.writeResponse(response, jsonstr);
    }

    /**
     * preForAddGiftConfig
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws javax.servlet.ServletException
     */
    public ActionForward preForAddGiftConfig(ActionMapping mapping, ActionForm form,
                                             HttpServletRequest request,
                                             HttpServletResponse response)
        throws ServletException
    {
        return mapping.findForward("addGiftConfig");
    }

    /**
     * addGiftConfig
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws javax.servlet.ServletException
     */
    public ActionForward addGiftConfig(ActionMapping mapping, ActionForm form,
                                       HttpServletRequest request, HttpServletResponse response)
        throws ServletException
    {
        //TODO
        SailConfBean bean = new SailConfBean();

        String sailType = request.getParameter("sailType");
        String productType = request.getParameter("productType");

        try
        {
            BeanUtil.getBean(bean, request);

            if (StringTools.isNullOrNone(sailType))
            {
                bean.setSailType( -1);
            }

            if (StringTools.isNullOrNone(productType))
            {
                bean.setProductType( -1);
            }

            User user = Helper.getUser(request);

//            sailConfigManager.addBean(user, bean);
            //TODO

            request.setAttribute(KeyConstant.MESSAGE, "成功操作");
        }
        catch (Exception e)
        {
            _logger.warn(e, e);

            request.setAttribute(KeyConstant.ERROR_MESSAGE, "增加失败:" + e.getMessage());
        }

        CommonTools.removeParamers(request);

        return mapping.findForward("queryGiftConfig");
    }

    /**
     * updateGiftConfig
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws javax.servlet.ServletException
     */
    public ActionForward updateGiftConfig(ActionMapping mapping, ActionForm form,
                                          HttpServletRequest request, HttpServletResponse response)
        throws ServletException
    {
        SailConfBean bean = new SailConfBean();

        String sailType = request.getParameter("sailType");
        String productType = request.getParameter("productType");

        try
        {
            BeanUtil.getBean(bean, request);

            if (StringTools.isNullOrNone(sailType))
            {
                bean.setSailType( -1);
            }

            if (StringTools.isNullOrNone(productType))
            {
                bean.setProductType( -1);
            }

            User user = Helper.getUser(request);

//            sailConfigManager.updateBean(user, bean);

            request.setAttribute(KeyConstant.MESSAGE, "成功操作");
        }
        catch (Exception e)
        {
            _logger.warn(e, e);

            request.setAttribute(KeyConstant.ERROR_MESSAGE, "增加失败:" + e.getMessage());
        }

        CommonTools.removeParamers(request);

        return mapping.findForward("queryGiftConfig");
    }

    /**
     * findGiftConfig
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws javax.servlet.ServletException
     */
    public ActionForward findGiftConfig(ActionMapping mapping, ActionForm form,
                                        HttpServletRequest request, HttpServletResponse response)
        throws ServletException
    {
        String id = request.getParameter("id");

        String update = request.getParameter("update");

//        SailConfVO vo = sailConfDAO.findVO(id);
//
//        if (vo == null)
//        {
//            request.setAttribute(KeyConstant.ERROR_MESSAGE, "不存在");
//
//            return mapping.findForward("querySailConfig");
//        }

//        request.setAttribute("bean", vo);

        if ("1".equals(update))
        {
            return mapping.findForward("updateGiftConfig");
        }

        return mapping.findForward("detailGiftConfig");
    }

    /**
     * deleteGiftConfig
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws javax.servlet.ServletException
     */
    public ActionForward deleteGiftConfig(ActionMapping mapping, ActionForm form,
                                          HttpServletRequest request, HttpServletResponse response)
        throws ServletException
    {
        AjaxResult ajax = new AjaxResult();

        try
        {
            String id = request.getParameter("id");

            User user = Helper.getUser(request);

//            sailConfigManager.deleteConf(user, id);

            ajax.setSuccess("成功删除");
        }
        catch (Exception e)
        {
            _logger.warn(e, e);

            ajax.setError("删除失败:" + e.getMessage());
        }

        return JSONTools.writeResponse(response, ajax);
    }

    public ProductVSGiftDAO getProductVSGiftDAO() {
        return productVSGiftDAO;
    }

    public void setProductVSGiftDAO(ProductVSGiftDAO productVSGiftDAO) {
        this.productVSGiftDAO = productVSGiftDAO;
    }
}
