package com.china.center.oa.sail.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.center.china.osgi.publics.file.read.ReadeFileFactory;
import com.center.china.osgi.publics.file.read.ReaderFile;
import com.china.center.oa.client.bean.CustomerBean;
import com.china.center.oa.client.dao.CustomerMainDAO;
import com.china.center.oa.finance.dao.InvoiceinsDAO;
import com.china.center.oa.finance.bean.InvoiceinsBean;
import com.china.center.oa.publics.bean.CityBean;
import com.china.center.oa.publics.bean.ProvinceBean;
import com.china.center.oa.publics.bean.StafferBean;
import com.china.center.oa.publics.dao.CityDAO;
import com.china.center.oa.publics.dao.ProvinceDAO;
import com.china.center.oa.publics.dao.StafferDAO;
import com.china.center.oa.sail.bean.*;
import com.china.center.oa.sail.dao.*;
import com.china.center.tools.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.center.china.osgi.publics.User;
import com.china.center.actionhelper.common.ActionTools;
import com.china.center.actionhelper.common.JSONTools;
import com.china.center.actionhelper.common.KeyConstant;
import com.china.center.actionhelper.common.OldPageSeparateTools;
import com.china.center.actionhelper.json.AjaxResult;
import com.china.center.actionhelper.query.HandleResult;
import com.china.center.common.MYException;
import com.china.center.common.taglib.DefinedCommon;
import com.china.center.jdbc.annosql.constant.AnoConstant;
import com.china.center.jdbc.util.ConditionParse;
import com.china.center.jdbc.util.PageSeparate;
import com.china.center.oa.product.bean.ComposeProductBean;
import com.china.center.oa.product.bean.DepotBean;
import com.china.center.oa.product.bean.ProductBean;
import com.china.center.oa.product.constant.ProductConstant;
import com.china.center.oa.product.dao.ComposeItemDAO;
import com.china.center.oa.product.dao.ComposeProductDAO;
import com.china.center.oa.product.dao.DepotDAO;
import com.china.center.oa.product.dao.ProductDAO;
import com.china.center.oa.product.vo.ComposeItemVO;
import com.china.center.oa.publics.Helper;
import com.china.center.oa.publics.bean.AuthBean;
import com.china.center.oa.publics.constant.AuthConstant;
import com.china.center.oa.publics.manager.UserManager;
import com.china.center.oa.sail.constanst.OutConstant;
import com.china.center.oa.sail.constanst.ShipConstant;
import com.china.center.oa.sail.manager.ShipManager;
import com.china.center.oa.sail.vo.PackageVO;
import com.china.center.oa.sail.wrap.PackageWrap;
import com.china.center.oa.sail.wrap.PickupWrap;

public class ShipAction extends DispatchAction
{
    private final Log _logger = LogFactory.getLog(getClass());

    private PackageDAO packageDAO = null;

    private PackageItemDAO packageItemDAO = null;

    private OutDAO outDAO = null;

    private BaseDAO baseDAO = null;

    private DistributionDAO distributionDAO = null;

    private ExpressDAO expressDAO = null;

    private ShipManager shipManager = null;

    private ProductDAO productDAO = null;

    private ComposeProductDAO composeProductDAO = null;

    private ComposeItemDAO composeItemDAO = null;

    private OutImportDAO outImportDAO = null;

    private PackageVSCustomerDAO packageVSCustomerDAO = null;

    private UserManager userManager = null;

    private DepotDAO depotDAO = null;

    private BranchRelationDAO branchRelationDAO = null;

    private StafferDAO stafferDAO = null;

    private InvoiceinsDAO invoiceinsDAO = null;

    private CustomerMainDAO customerMainDAO = null;

    private ProvinceDAO provinceDAO = null;

    private CityDAO cityDAO = null;

    private final static String QUERYPACKAGE = "queryPackage";

    private final static String QUERYPICKUP = "queryPickup";

    /**
     * default construct
     */
    public ShipAction()
    {
    }

    /**
     * queryPackage
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws ServletException
     */
    public ActionForward queryPackage(ActionMapping mapping, ActionForm form,
                                      HttpServletRequest request,
                                      HttpServletResponse response)
            throws ServletException
    {
        String customerName = request.getParameter("customerName");
        String productName = request.getParameter("productName");
        String stafferName = request.getParameter("stafferName");
        String insFollowOut = request.getParameter("insFollowOut");
        _logger.info("***queryPackage with parameter customer:"+customerName+"***stafferName***"+stafferName+"***productName***"+productName+"***insFollowOut**"+insFollowOut);
        User user = Helper.getUser(request);

        ConditionParse condtion = new ConditionParse();
        condtion.addWhereStr();

        // 要根据仓库的权限,计算出地点,根据地点查询
        setDepotCondotionInOut(user, condtion);

        // TEMPLATE 在action里面默认查询条件
        Map<String, String> initMap = initLogTime(request, condtion, true);

//        System.out.println("**************condition1111"+condtion);

        ActionTools.processJSONDataQueryCondition(QUERYPACKAGE, request, condtion, initMap);
//        System.out.println("**************condition222222222222222222"+condtion);

        String temp = condtion.toString();
        _logger.info("***SQL generated*************"+temp);
        if (!StringTools.isNullOrNone(productName) && temp.indexOf("PackageItemBean") !=-1){
            int index2 = temp.lastIndexOf("AND");
            String prefix = temp.substring(0,index2);
            String sql = prefix+"and exists (select PackageItemBean.id from t_center_package_item PackageItemBean where PackageItemBean.packageId=PackageBean.id and PackageItemBean.productName like '%"+productName+"%')";
            condtion.setCondition(sql);
        }

//        if (!StringTools.isNullOrNone(insFollowOut)){
//            String placeholder = "AND PackageBean.insFollowOut =";
//            int index3 = temp.indexOf(placeholder);
//            temp = temp.substring(0, index3)+temp.substring(index3+placeholder.length()+1);
//        }
//        _logger.info("***SQL converted*************"+temp);

//        if (!StringTools.isNullOrNone(productName) && temp.indexOf("PackageItemBean") !=-1){
//            int index2 = temp.lastIndexOf("AND");
//            String prefix = temp.substring(0,index2);
//            StringBuilder sb = new StringBuilder();
//            sb.append(prefix)
//               .append(" and exists ")
//               .append("(select PackageItemBean.id from t_center_package_item PackageItemBean where PackageItemBean.packageId=PackageBean.id and PackageItemBean.productName like '%")
//               .append(productName)
//               .append("%'");
//            //2015/3/19 “发票单发”则选择CK单中只有A或FP开头的单号
//            if (!StringTools.isNullOrNone(insFollowOut) && "0".equals(insFollowOut)){
//                sb.append(" and (PackageItemBean.outId like 'A%' or PackageItemBean.outId like 'FP%')");
//            }
//            sb.append(")");
//            condtion.setCondition(sb.toString());
//        } else {
//            //2015/3/19 “发票单发”则选择CK单中只有A或FP开头的单号
//            if (!StringTools.isNullOrNone(insFollowOut) && "0".equals(insFollowOut)){
//                StringBuilder sb = new StringBuilder();
//                sb.append(temp)
//                  .append(" and exists ")
//                  .append("(select PackageItemBean.id from t_center_package_item PackageItemBean where PackageItemBean.packageId=PackageBean.id")
//                  .append(" and (PackageItemBean.outId like 'A%' or PackageItemBean.outId like 'FP%')")
//                  .append(")");
//                condtion.setCondition(sb.toString());
//            }
//        }

        _logger.info("**************SQL to be executed***"+condtion.toString());

        //2015/3/22 按照单据时间排序，时间最老的最先显示
        condtion.addCondition("order by PackageBean.billsTime asc");
        String jsonstr = ActionTools.queryVOByJSONAndToString(QUERYPACKAGE, request, condtion, this.packageDAO,
                new HandleResult<PackageVO>()
                {
                    public void handle(PackageVO vo)
                    {
                        vo.setPay(DefinedCommon.getValue("deliverPay", vo.getExpressPay()) + "/" + DefinedCommon.getValue("deliverPay", vo.getTransportPay()));
                    }

                });

        _logger.info("**************jsonstr****************"+jsonstr);

        return JSONTools.writeResponse(response, jsonstr);
    }

    /**
     * 设置仓库的过滤条件
     *
     * @param user
     * @param condtion
     */
    private void setDepotCondotionInOut(User user, ConditionParse condtion)
    {
        // 只能看到自己的仓库
        List<AuthBean> depotAuthList = userManager.queryExpandAuthById(
                user.getId(), AuthConstant.EXPAND_AUTH_DEPOT);

        if (ListTools.isEmptyOrNull(depotAuthList))
        {
            // 永远也没有结果
            condtion.addFlaseCondition();
        }
        else
        {
            StringBuffer sb = new StringBuffer();

            sb.append("and (");
            for (Iterator<AuthBean> iterator = depotAuthList.iterator(); iterator
                    .hasNext();)
            {
                AuthBean authBean = (AuthBean) iterator.next();

                // 接受仓库是自己管辖的
                if (iterator.hasNext())
                {
                    // 根据仓库找出地点
                    DepotBean dept = depotDAO.find(authBean.getId());

                    if (null != dept)
                    {
                        sb.append("PackageBean.locationId = '" + dept.getIndustryId2()
                                + "' or ");
                    }
                }
                else
                {
                    // 根据仓库找出地点
                    DepotBean dept = depotDAO.find(authBean.getId());

                    if (null != dept)
                    {
                        sb.append("PackageBean.locationId = '" + dept.getIndustryId2() + "'");
                    }
                    else
                    {
                        sb.append("1=1");
                    }
                }
            }

            sb.append(") ");

            condtion.addCondition(sb.toString());
        }
    }

    /**
     * initLogTime
     *
     * @param request
     * @param condtion
     * @return
     */
    private Map<String, String> initLogTime(HttpServletRequest request,
                                            ConditionParse condtion, boolean initStatus) {
        Map<String, String> changeMap = new HashMap<String, String>();

        String alogTime = request.getParameter("alogTime");

        String blogTime = request.getParameter("blogTime");

        if (StringTools.isNullOrNone(alogTime)
                && StringTools.isNullOrNone(blogTime)) {
            changeMap.put("alogTime", TimeTools.now_short(-7));

            changeMap.put("blogTime", TimeTools.now_short());

            if (initStatus) {
                changeMap.put("status",	String.valueOf(ShipConstant.SHIP_STATUS_INIT));

                condtion.addIntCondition("PackageBean.status", "=", ShipConstant.SHIP_STATUS_INIT);
            }

            condtion.addCondition("PackageBean.logTime", ">=",
                    TimeTools.now_short(-7) + " 00:00:00");

            condtion.addCondition("PackageBean.logTime", "<=",
                    TimeTools.now_short() + " 23:59:59");
        }

        return changeMap;
    }

    /**
     * preForQueryPickup
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws ServletException
     */
    public ActionForward preForQueryPickup(ActionMapping mapping, ActionForm form,
                                           HttpServletRequest request,
                                           HttpServletResponse response)
            throws ServletException
    {
        prepare(request);

        return mapping.findForward("queryPickup");
    }

    private void prepare(HttpServletRequest request)
    {
        List<ExpressBean> expressList = expressDAO.listEntityBeansByOrder("order by id");

        request.setAttribute("expressList", expressList);
    }

    /**
     * queryPickup
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws ServletException
     */
    public ActionForward queryPickup(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request,
                                     HttpServletResponse response)
            throws ServletException
    {
        User user = (User) request.getSession().getAttribute("user");

        List<PackageVO> list = null;

        CommonTools.saveParamers(request);

        try
        {
            if (OldPageSeparateTools.isFirstLoad(request))
            {
                ConditionParse condtion = getQueryPickupCondition(request,user, true);
                _logger.info("*******************condtion**********"+condtion);

                int tatol = packageDAO.countByCon(condtion);

                PageSeparate page = new PageSeparate(tatol,	30);

                ConditionParse condtion2 = getQueryPickupCondition(request,user, false);
                _logger.info("*******************condtion22222**********"+condtion);
                OldPageSeparateTools.initPageSeparate(condtion2, page, request,QUERYPICKUP);

                list = packageDAO.queryVOsByCon(condtion2, page);
            }
            else
            {
                OldPageSeparateTools.processSeparate(request,
                        QUERYPICKUP);

                list = packageDAO.queryVOsByCon(OldPageSeparateTools.getCondition(request, QUERYPICKUP), OldPageSeparateTools
                        .getPageSeparate(request, QUERYPICKUP));
            }
        }
        catch (Exception e)
        {
            request.setAttribute(KeyConstant.ERROR_MESSAGE, "查询单据失败");

            _logger.error(e, e);

            return mapping.findForward("error");
        }

        // 对结果集包装
        List<PickupWrap> wrapList = new ArrayList<PickupWrap>();

        Set<String> set = new HashSet<String>();

        for (PackageVO each : list)
        {
            if (!set.contains(each.getPickupId()))
            {
                set.add(each.getPickupId());
            }
        }

        for (String pickupId : set)
        {
            List<PackageVO> voList = packageDAO.queryEntityVOsByFK(pickupId);

            // 从小到大
            Collections.sort(voList, new Comparator<PackageVO>() {
                public int compare(PackageVO o1, PackageVO o2) {
                    return o1.getIndex_pos() - o2.getIndex_pos();
                }
            });

            PickupWrap wrap = new PickupWrap();

            wrap.setPickupId(pickupId);
            wrap.setPackageList(voList);

            wrapList.add(wrap);
        }

        request.setAttribute("itemList", wrapList);

        List<ExpressBean> expressList = expressDAO.listEntityBeansByOrder("order by id");

        request.setAttribute("expressList", expressList);

        return mapping.findForward("queryPickup");
    }

    /**
     * getQuerySelfBalanceCondition
     *
     *
     * @param request
     * @param user
     * @param count
     * @return
     */
    private ConditionParse getQueryPickupCondition(HttpServletRequest request, User user, boolean count)
    {
        Map<String, String> queryOutCondtionMap = CommonTools.saveParamersToMap(request);

        ConditionParse condtion = new ConditionParse();

        //condtion.addWhereStr();

        String batchId = request.getParameter("batchId");

        String shipment = request.getParameter("shipment");

        if (!StringTools.isNullOrNone(batchId))
        {
            condtion.addCondition("PackageBean.pickupId", "like", batchId);

            queryOutCondtionMap.put("batchId", batchId);
        }

        int shipping = -1;

        if (!StringTools.isNullOrNone(shipment))
        {
            shipping = MathTools.parseInt(shipment);

            condtion.addIntCondition("PackageBean.shipping", "=", shipping);

            queryOutCondtionMap.put("shipment", shipment);
        }

        String transport1 = request.getParameter("transport1");

        String transport2 = request.getParameter("transport2");

        if (!StringTools.isNullOrNone(transport1))
        {
            condtion.addIntCondition("PackageBean.transport1", "=", MathTools.parseInt(transport1));

            queryOutCondtionMap.put("transport1", transport1);
        }

        if (!StringTools.isNullOrNone(transport2))
        {
            condtion.addIntCondition("PackageBean.transport2", "=", MathTools.parseInt(transport2));

            queryOutCondtionMap.put("transport2", transport2);
        }

        String packageId = request.getParameter("packageId");

        if (!StringTools.isNullOrNone(packageId))
        {
            condtion.addCondition("PackageBean.id", "like", packageId.trim());

            queryOutCondtionMap.put("packageId", packageId.trim());
        }

        String receiver = request.getParameter("receiver");

        if (!StringTools.isNullOrNone(receiver))
        {
            condtion.addCondition("PackageBean.receiver", "like", receiver.trim());

            queryOutCondtionMap.put("receiver", receiver.trim());
        }

        String mobile = request.getParameter("mobile");

        if (!StringTools.isNullOrNone(mobile))
        {
            condtion.addCondition("PackageBean.mobile", "like", mobile.trim());

            queryOutCondtionMap.put("mobile", mobile.trim());
        }

        String location = request.getParameter("location");

        if (!StringTools.isNullOrNone(location))
        {
            condtion.addCondition("PackageBean.locationId", "like", location.trim());

            queryOutCondtionMap.put("location", location.trim());
        }

        String status = request.getParameter("currentStatus");

        if (!StringTools.isNullOrNone(status))
        {
            if (status.equals("4"))
                condtion.addCondition(" and PackageBean.status in (1,3)");
            else
                condtion.addIntCondition("PackageBean.status", "=", MathTools.parseInt(status));

            queryOutCondtionMap.put("currentStatus",status);
        }else
        {
            condtion.addCondition(" and PackageBean.status in (1,3)");

            queryOutCondtionMap.put("currentStatus","4");
        }

        // 事业部
        String industryName = request.getParameter("industryName");
        if (!StringTools.isNullOrNone(industryName))
        {
            condtion.addCondition("PackageBean.industryName", "=", industryName);

            queryOutCondtionMap.put("industryName",industryName);
        }

        // 销售单
        String outId = request.getParameter("outId");
        if (!StringTools.isNullOrNone(outId))
        {
            condtion.addCondition("PackageItemBean.outid", "like", outId);

            queryOutCondtionMap.put("outId",outId);
        }

        // 销售单 紧急
        String emergency = request.getParameter("emergency");
        if (!StringTools.isNullOrNone(emergency))
        {
            condtion.addIntCondition("PackageItemBean.emergency", "=", emergency);

            queryOutCondtionMap.put("emergency", emergency);
        }

        //客户名称
        String customerName = request.getParameter("customerName");
        _logger.info("****customerName condition****"+customerName);
        if (!StringTools.isNullOrNone(customerName))
        {
            if (count){
                condtion.addCondition("and exists (select CustomerBean.id from T_CENTER_CUSTOMER_MAIN CustomerBean where PackageBean.customerId = CustomerBean.id and CustomerBean.name like '%"+customerName+ "%')");

            } else{
                condtion.addCondition("CustomerBean.name", "like", "%"+customerName+"%");
            }

            queryOutCondtionMap.put("customerName", customerName);
        }

        //产品名称
        String productName = request.getParameter("productName");
        _logger.info("****productName condition****"+productName);
        if (!StringTools.isNullOrNone(productName))
        {
            condtion.addCondition("PackageItemBean.productName", "like", "%" + productName + "%");

            queryOutCondtionMap.put("productName", productName);
        }

        setDepotCondotionInOut(user, condtion);

        condtion.addCondition("order by PackageBean.logTime desc");

        request.getSession().setAttribute("ppmap", queryOutCondtionMap);

        return condtion;
    }

    /**
     * addPickup
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws ServletException
     */
    public ActionForward addPickup(ActionMapping mapping, ActionForm form,
                                   HttpServletRequest request,
                                   HttpServletResponse response)
            throws ServletException
    {
        User user = Helper.getUser(request);

        AjaxResult ajax = new AjaxResult();

        // separate by ~
        String packageIds = request.getParameter("packageIds");

        try{
            shipManager.addPickup(user, packageIds);

            ajax.setSuccess("拣配成功");
        }catch(MYException e)
        {
            _logger.warn(e, e);

            ajax.setError("拣配出错:"+ e.getErrorContent());
        }

        return JSONTools.writeResponse(response, ajax);
    }

    /**
     * deletePackage
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws ServletException
     */
    public ActionForward deletePackage(ActionMapping mapping, ActionForm form,
                                       HttpServletRequest request,
                                       HttpServletResponse response)
            throws ServletException
    {
        User user = Helper.getUser(request);

        AjaxResult ajax = new AjaxResult();

        // separate by ~
        String packageIds = request.getParameter("packageIds");

        try{
            shipManager.deletePackage(user, packageIds);

            ajax.setSuccess("撤销成功");
        }catch(MYException e)
        {
            _logger.warn(e, e);

            ajax.setError("撤销出错:"+ e.getErrorContent());
        }

        return JSONTools.writeResponse(response, ajax);
    }

    /**
     * 2015/2/26 撤销捡配CK单
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws ServletException
     */
    public ActionForward cancelPickup(ActionMapping mapping, ActionForm form,
                                       HttpServletRequest request,
                                       HttpServletResponse response)
            throws ServletException
    {
        User user = Helper.getUser(request);

        // separate by ~
        String packageIds = request.getParameter("packageIds");
        String pickupId = request.getParameter("pickupId");
        if (StringTools.isNullOrNone(pickupId)){
            _logger.info("****cancelPickup with packageIds****"+packageIds);
        } else {
            _logger.info("****cancelPickup with pickupId****"+pickupId);
            List<PackageBean> packages = this.packageDAO.queryEntityBeansByFK(pickupId);
            StringBuilder sb = new StringBuilder();
            for (PackageBean pack : packages){
                sb.append(pack.getId()+"~");
            }
            packageIds = sb.toString();
            _logger.info("cancelPickup packages***"+packageIds);
        }

        try{
            shipManager.cancelPackage(user, packageIds);
            request.setAttribute(KeyConstant.MESSAGE, "撤销成功");
        }catch(MYException e)
        {
            _logger.error(e, e);
            request.setAttribute(KeyConstant.ERROR_MESSAGE, "撤销出错:"+e.getErrorContent());
        }

//        return mapping.findForward("queryPickup");
        return this.queryPickup(mapping,form,request,response);
    }

    /**
     * findPackage
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws ServletException
     */
    public ActionForward findPackage(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request,
                                     HttpServletResponse response)
            throws ServletException
    {
        String packageId = request.getParameter("packageId");

        PackageVO vo = packageDAO.findVO(packageId);

        if (null == vo)
        {
            request.setAttribute(KeyConstant.ERROR_MESSAGE, "出库单不存在");

            return mapping.findForward("error");
        }

        List<PackageItemBean> itemList = packageItemDAO.queryEntityBeansByFK(packageId);

        vo.setItemList(itemList);

        request.setAttribute("bean", vo);

        return mapping.findForward("detailPackage");
    }

    private boolean checkBankPackages(List<PackageVO> packages){
        for (PackageVO vo : packages){
            System.out.println("getIndustryName***************"+vo.getIndustryName());
            if (vo.getIndustryName().indexOf("银行业务部") ==-1){
                return false;
            }
        }
        return true;
    }

    /**
     * findPickup
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws ServletException
     */
    public ActionForward findPickup(ActionMapping mapping, ActionForm form,
                                    HttpServletRequest request,
                                    HttpServletResponse response)
            throws ServletException
    {
        CommonTools.saveParamers(request);

        String pickupId = request.getParameter("pickupId");

        String compose = request.getParameter("compose");

        if (StringTools.isNullOrNone(compose))
            compose = "1";

        // 根据拣配单(批次单) 生成一张批次出库单
        List<PackageVO> packageList = packageDAO.queryEntityVOsByFK(pickupId);

        StringBuilder sb = new StringBuilder();


        int pickupCount = packageList.size();

        //2015/1/25 检查是否银行业务部订单
        boolean isBankOrder = this.checkBankPackages(packageList);
        if (isBankOrder){
            //<key,value> as <银行名称，List<PackageItemBean>>
            Map<String, List<PackageItemBean>> map = new HashMap<String, List<PackageItemBean>>();
            //<key,value> as <银行名称-productId,PackageItemBean>
            Map<String, PackageItemBean> map2 = new HashMap<String, PackageItemBean>();
            for (PackageVO each : packageList)
            {
                sb.append(each.getId()).append("<br>");

                List<PackageItemBean> itemList = packageItemDAO.queryEntityBeansByFK(each.getId());

                // 根据产品分组: 1.判断是否为合成,如是,则要找出子产品;2.按照银行合并
                for (PackageItemBean eachItem : itemList)
                {
                    String customerId = eachItem.getCustomerId();
                    CustomerBean customer = customerMainDAO.find(customerId);
                    String name = customer.getName();
                    String bank = name.split("银行")[0]+"银行";

                    PackageItemBean itemBean = new PackageItemBean();

                    itemBean.setProductId(eachItem.getProductId());
                    itemBean.setProductName(eachItem.getProductName());
                    itemBean.setAmount(eachItem.getAmount());
                    //itemBean.setShowSubProductName(showSubProductName);

                    checkCompose(eachItem, itemBean, compose);

                    String key = bank+"-"+eachItem.getProductName();

                    if (map.containsKey(bank))
                    {
                        List<PackageItemBean> items = map.get(bank);

                        //2015/1/27 同一银行同一产品数量合并
                        if (map2.containsKey(key))
                        {
                            PackageItemBean item = map2.get(key);
                            item.setAmount(itemBean.getAmount() + item.getAmount());
                        } else{
                            items.add(itemBean);
                            map2.put(key,itemBean);
                        }
                    }else{
                        List<PackageItemBean> items = new ArrayList<PackageItemBean>();
                        items.add(itemBean);
                        map.put(bank,items);
                        map2.put(key,itemBean);
                    }
                }
            }

            //2015/1/27 按照名称排序
            for (List<PackageItemBean> items: map.values()){
                Collections.sort(items, new Comparator(){
                    @Override
                    public int compare(Object o1, Object o2) {
                        PackageItemBean i1 = (PackageItemBean)o1;
                        PackageItemBean i2 = (PackageItemBean)o2;
                        return i1.getProductName().compareTo(i2.getProductName());
                    }
                });
            }


            PackageVO batchVO = new PackageVO();

            batchVO.setId(sb.toString());
            batchVO.setPickupId(pickupId);
            batchVO.setRepTime(TimeTools.now_short());

//            List<PackageItemBean> lastList = new ArrayList<PackageItemBean>();

/*            for (Entry<String, PackageItemBean> entry : map.entrySet())
            {
                lastList.add(entry.getValue());
            }*/

//            batchVO.setItemList(lastList);

            // key:以批次号做为key ?
            request.setAttribute("bean", batchVO);
            request.setAttribute("map", map);

            request.setAttribute("year", TimeTools.now("yyyy"));
            request.setAttribute("month", TimeTools.now("MM"));
            request.setAttribute("day", TimeTools.now("dd"));

            request.setAttribute("index_pos", 0);

            request.setAttribute("pickupCount", pickupCount);

            request.setAttribute("compose", compose);

            return mapping.findForward("printBankPickup");
        } else{
            //<key,value> as <productId,PackageItemBean>
            Map<String, PackageItemBean> map = new HashMap<String, PackageItemBean>();
            for (PackageVO each : packageList)
            {
                sb.append(each.getId()).append("<br>");

                List<PackageItemBean> itemList = packageItemDAO.queryEntityBeansByFK(each.getId());

                // 根据产品分组: 1.判断是否为合成,如是,则要找出子产品;2.数量合并
                for (PackageItemBean eachItem : itemList)
                {
                    if (map.containsKey(eachItem.getProductId()))
                    {
                        PackageItemBean itemBean = map.get(eachItem.getProductId());

                        itemBean.setAmount(itemBean.getAmount() + eachItem.getAmount());
                    }else{
                        PackageItemBean itemBean = new PackageItemBean();

                        itemBean.setProductId(eachItem.getProductId());
                        itemBean.setProductName(eachItem.getProductName());
                        itemBean.setAmount(eachItem.getAmount());
                        //itemBean.setShowSubProductName(showSubProductName);

                        checkCompose(eachItem, itemBean, compose);

                        map.put(eachItem.getProductId(), itemBean);
                    }
                }
            }

            PackageVO batchVO = new PackageVO();

            batchVO.setId(sb.toString());
            batchVO.setPickupId(pickupId);
            batchVO.setRepTime(TimeTools.now_short());

            List<PackageItemBean> lastList = new ArrayList<PackageItemBean>();

            for (Entry<String, PackageItemBean> entry : map.entrySet())
            {
                lastList.add(entry.getValue());
            }

            //2015/1/14 按照名称排序
            Collections.sort(lastList, new Comparator(){
                @Override
                public int compare(Object o1, Object o2) {
                    PackageItemBean i1 = (PackageItemBean)o1;
                    PackageItemBean i2 = (PackageItemBean)o2;
                    return i1.getProductName().compareTo(i2.getProductName());
                }
            });
            batchVO.setItemList(lastList);

            // key:以批次号做为key ?
            request.setAttribute("bean", batchVO);

            request.setAttribute("year", TimeTools.now("yyyy"));
            request.setAttribute("month", TimeTools.now("MM"));
            request.setAttribute("day", TimeTools.now("dd"));

            request.setAttribute("index_pos", 0);

            request.setAttribute("pickupCount", pickupCount);

            request.setAttribute("compose", compose);

            return mapping.findForward("printPickup");
        }

    }

    private void checkCompose(PackageItemBean eachItem, PackageItemBean itemBean, String com)
    {
        if ("1".equals(com))
        {
            ProductBean product = productDAO.find(eachItem.getProductId());

            if (null != product){
                if (product.getCtype() == ProductConstant.CTYPE_YES)
                {
                    ComposeProductBean compose = composeProductDAO.queryLatestByProduct(eachItem.getProductId());

                    if (null != compose)
                    {
                        List<ComposeItemVO> citemList = composeItemDAO.queryEntityVOsByFK(compose.getId());

                        StringBuilder sb2 = new StringBuilder();

                        for (ComposeItemVO eachc : citemList)
                        {
                            sb2.append(eachc.getProductName()).append("<br>");
                        }

                        itemBean.setShowSubProductName(sb2.toString());
                    }
                }
            }
        }
    }

    /**
     * findNextPackage
     *
     * 根据批次号(拣配号)及index获取包package
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws ServletException
     */
    public ActionForward findNextPackage(ActionMapping mapping, ActionForm form,
                                         HttpServletRequest request,
                                         HttpServletResponse response)
            throws ServletException
    {
        CommonTools.saveParamers(request);

        String pickupId = request.getParameter("pickupId");

        String sindex_pos = request.getParameter("index_pos");

        String printMode = request.getParameter("printMode");

        // 0 连打 1 单打
        String printSmode = request.getParameter("printSmode");

        if (StringTools.isNullOrNone(printSmode))
            printSmode = "";

        String compose = request.getParameter("compose");

        if (StringTools.isNullOrNone(compose))
            compose = "1";

        int index_pos = 0;

        if (!StringTools.isNullOrNone(sindex_pos))
        {
            index_pos = MathTools.parseInt(sindex_pos);
        }

        index_pos += 1;

        ConditionParse condtion = new ConditionParse();

        condtion.addWhereStr();

        condtion.addCondition("PackageBean.pickupId", "=", pickupId);
        condtion.addIntCondition("PackageBean.index_pos", "=", index_pos);

        _logger.info("**********findNextPackage with pickupId:"+pickupId+" index_pos:"+index_pos);

        List<PackageVO> packageList = packageDAO.queryVOsByCondition(condtion);

        _logger.info("****findNextPackage packageList size***"+packageList.size());

        if (ListTools.isEmptyOrNull(packageList) || packageList.size() > 1)
        {
            // 只有连打时,才跳转到回执单打印
            if (printMode.equals("0") && printSmode.equals("0"))
            {
                CommonTools.removeParamers(request);

                request.setAttribute("pickupId2", pickupId);

                request.setAttribute("compose1", compose);

                //连打模式
                request.setAttribute("batchPrint", "0");

                //连打模式
                request.setAttribute("printMode", "0");
                request.setAttribute("printSmode", "0");
//                request.getSession().setAttribute("printMode", "0");
//                request.getSession().setAttribute("printSmode", "0");

                _logger.info("****redirect to findOutForReceipt print**********");
                return findOutForReceipt(mapping, form, request, response);
            }else
            {
                _logger.info("**** print finished*****");
                request.setAttribute(KeyConstant.ERROR_MESSAGE, "已打印完毕");

                return mapping.findForward("error");
            }
        }

        PackageVO vo = packageList.get(0);

        Map<String, PackageItemBean> map = new HashMap<String, PackageItemBean>();

        Map<String, PackageWrap> map1 = new HashMap<String, PackageWrap>();

        List<PackageItemBean> itemList = packageItemDAO.queryEntityBeansByFK(vo.getId());

        // 根据产品分组: 1.判断是否为合成,如是,则要找出子产品;2.数量合并
        for (PackageItemBean eachItem : itemList)
        {
            if (map.containsKey(eachItem.getProductId()))
            {
                PackageItemBean itemBean = map.get(eachItem.getProductId());

                itemBean.setAmount(itemBean.getAmount() + eachItem.getAmount());
            }else{
                PackageItemBean itemBean = new PackageItemBean();

                itemBean.setProductId(eachItem.getProductId());
                itemBean.setProductName(eachItem.getProductName());
                itemBean.setAmount(eachItem.getAmount());
                //itemBean.setShowSubProductName(showSubProductName);

                checkCompose(eachItem, itemBean, compose);

                map.put(eachItem.getProductId(), itemBean);
            }

            if (!map1.containsKey(eachItem.getOutId()))
            {
                PackageWrap wrap = new PackageWrap();

                wrap.setOutId(eachItem.getOutId());
                wrap.setDescription(eachItem.getDescription());

                map1.put(eachItem.getOutId(), wrap);
            }
        }

        vo.setRepTime(TimeTools.now_short());

        List<PackageItemBean> lastList = new ArrayList<PackageItemBean>();

        for (Entry<String, PackageItemBean> entry : map.entrySet())
        {
            lastList.add(entry.getValue());
        }

        vo.setItemList(lastList);

        List<PackageWrap> wrapList = new ArrayList<PackageWrap>();

        for (Entry<String, PackageWrap> entry : map1.entrySet())
        {
            wrapList.add(entry.getValue());
        }

        vo.setItemList(lastList);

        vo.setWrapList(wrapList);

        request.setAttribute("bean", vo);

        request.setAttribute("year", TimeTools.now("yyyy"));
        request.setAttribute("month", TimeTools.now("MM"));
        request.setAttribute("day", TimeTools.now("dd"));

        request.setAttribute("index_pos", index_pos);

        request.setAttribute("compose", compose);

        return mapping.findForward("printPackage");
    }

    /**
     * 显示要打印的回执单
     * 邮政、浦发、中信
     * 一个包(package)一个打印单
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws ServletException
     */
    public ActionForward findOutForReceipt(ActionMapping mapping, ActionForm form,
                                           HttpServletRequest request,
                                           HttpServletResponse response)
            throws ServletException
    {
        CommonTools.saveParamers(request);

        String compose =  RequestTools.getValueFromRequest(request, "compose");

        String compose1 =  RequestTools.getValueFromRequest(request, "compose1");

        if (!StringTools.isNullOrNone(compose1))
            compose = compose1;

        String pickupId =  RequestTools.getValueFromRequest(request, "pickupId");

        String pickupId2 =  RequestTools.getValueFromRequest(request, "pickupId2");

        // 第一次跳转
        if (!StringTools.isNullOrNone(pickupId2))
        {
            pickupId = pickupId2;
        }

        String sindex_pos =  RequestTools.getValueFromRequest(request, "index_pos");

        int index_pos = 0;

        if (!StringTools.isNullOrNone(sindex_pos))
        {
            index_pos = MathTools.parseInt(sindex_pos);
        }

        String packageId =  RequestTools.getValueFromRequest(request, "packageId");

        String printMode =  (String)request.getAttribute("printMode");
        String printSmode =  (String)request.getAttribute("printSmode");

        String batchPrint =  RequestTools.getValueFromRequest(request, "batchPrint");
        String print2 = (String)request.getSession().getAttribute("printMode");
        _logger.info("**************batchPrint******"+batchPrint+"****print2****"+print2);

        String msg1 = "**********pickupId****"+pickupId+"****packageId*****"+packageId+"***index_pos***"+index_pos+"***printMode***"+printMode+"***printSmode***"+printSmode;
        _logger.info(msg1);
        //2015/3/25 批量打印标志
        request.setAttribute("batchPrint", batchPrint);

        //可去掉？很奇怪在页面上读不到printMode参数
        request.setAttribute("printMode", printMode);
        request.setAttribute("printSmode", printSmode);

        // 第一次打印时，找出第一个出库单，一个出库单对应多个客户 1:n
        if (index_pos == 0)
        {
            ConditionParse condtion = new ConditionParse();

            condtion.addWhereStr();

            condtion.addCondition("PackageBean.pickupId", "=", pickupId);
            condtion.addIntCondition("PackageBean.index_pos", "=", 1);

            List<PackageVO> packageList = packageDAO.queryVOsByCondition(condtion);

            if (ListTools.isEmptyOrNull(packageList) || packageList.size() > 1)
            {
                request.setAttribute(KeyConstant.ERROR_MESSAGE, "已打印完毕");

                return mapping.findForward("error");
            }

            packageId = packageList.get(0).getId();

            index_pos = 1;
        }
        String subindex_pos =  request.getParameter("subindex_pos");

        int subindexpos = 0;

        if (!StringTools.isNullOrNone(subindex_pos))
        {
            subindexpos = MathTools.parseInt(subindex_pos);
        }

        subindexpos += 1;

        String customerId = "";
        String customerName = "";

        ConditionParse con = new ConditionParse();

        con.addWhereStr();

        con.addCondition("PackageVSCustomerBean.packageId", "=", packageId);
        con.addIntCondition("PackageVSCustomerBean.indexPos", "=", subindexpos);
        _logger.info("=======con========" + con.toString());
        List<PackageVSCustomerBean> vsList = packageVSCustomerDAO.queryEntityBeansByCondition(con);

        if (!ListTools.isEmptyOrNull(vsList))
        {
            customerId = vsList.get(0).getCustomerId();
            customerName = vsList.get(0).getCustomerName();

            request.setAttribute("subindex_pos", subindexpos);
            String msg2 = "**********vsList size****"+vsList.size();
            _logger.info(msg2);
        }else
        {
            // 更新状态
            try
            {
                shipManager.updatePrintStatus(pickupId, index_pos);
                _logger.info(pickupId+":"+index_pos+" print finished***");
            }
            catch (MYException e)
            {
                request.setAttribute(KeyConstant.ERROR_MESSAGE, "已打印出错." + e.getErrorContent());

                return mapping.findForward("error");
            }

            index_pos += 1;
        }

        ConditionParse condtion = new ConditionParse();

        condtion.addWhereStr();

        condtion.addCondition("PackageBean.pickupId", "=", pickupId);
        condtion.addIntCondition("PackageBean.index_pos", "=", index_pos);

        List<PackageVO> packageList = packageDAO.queryVOsByCondition(condtion);

        if (ListTools.isEmptyOrNull(packageList) || packageList.size() > 1)
        {
            request.setAttribute(KeyConstant.ERROR_MESSAGE, "已打印完毕");

            return mapping.findForward("error");
        }

        // 取出包
        PackageVO vo = packageList.get(0);

        vo.setRepTime(TimeTools.now_short());

        vo.setCustomerId(customerId);
        vo.setCustomerName(customerName);

        if (StringTools.isNullOrNone(customerId))
        {
            ConditionParse con1 = new ConditionParse();

            con1.addWhereStr();

            con1.addCondition("PackageVSCustomerBean.packageId", "=", vo.getId());
            con1.addIntCondition("PackageVSCustomerBean.indexPos", "=", 1);
            //System.out.println("=======con1========" + con1.toString());
            List<PackageVSCustomerBean> vsList1 = packageVSCustomerDAO.queryEntityBeansByCondition(con1);

            if (!ListTools.isEmptyOrNull(vsList1))
            {
                customerId = vsList1.get(0).getCustomerId();
                customerName = vsList1.get(0).getCustomerName();

                vo.setCustomerId(customerId);
                vo.setCustomerName(customerName);

                request.setAttribute("subindex_pos", 1);
                String msg3 = "**********vsList1 size****"+vsList1.size()+"***customerId***"+customerId;
                _logger.info(msg3);

            }
        }

        List<PackageItemBean> itemList = packageItemDAO.
                queryEntityBeansByCondition(" where PackageItemBean.packageId = ? order by PackageItemBean.productName", vo.getId()); //  .queryEntityBeansByFK(vo.getId());
        String msg4 = vo.getId()+"**********itemList size****"+itemList.size();
        _logger.info(msg4);
        request.setAttribute("bean", vo);

        request.setAttribute("pickupId", pickupId);

        request.setAttribute("index_pos", index_pos);

        request.setAttribute("compose", compose);

        request.setAttribute("year", TimeTools.now("yyyy"));
        request.setAttribute("month", TimeTools.now("MM"));
        request.setAttribute("day", TimeTools.now("dd"));

        int totalAmount = 0;
        double total = 0.0d;

        if (vo.getIndustryName().indexOf("邮政") != -1)
        {
            request.setAttribute("packageId", vo.getId());

            List<PackageItemBean> itemList1 = new ArrayList<PackageItemBean>();

            Map<String, PackageItemBean> map1 = new HashMap<String, PackageItemBean>();

            for (PackageItemBean each : itemList)
            {
                if (!each.getCustomerId().equals(vo.getCustomerId()))
                {
                    continue;
                }

                String key = each.getProductId()+ "~" + each.getPrice();

                if (!map1.containsKey(key))
                {
                    checkCompose(each, each, compose);

                    map1.put(each.getProductId(), each);
                }else{
                    PackageItemBean itemBean = map1.get(key);

                    itemBean.setAmount(itemBean.getAmount() + each.getAmount());
                    itemBean.setValue(itemBean.getValue() + each.getValue());

                    itemBean.setOutId(itemBean.getOutId() + "<br>" + each.getOutId());
                }

                total += each.getValue();
            }

            for(Entry<String, PackageItemBean> each : map1.entrySet())
            {
                itemList1.add(each.getValue());
            }

            vo.setItemList(itemList1);

            request.setAttribute("total", total);

            return mapping.findForward("printPostReceipt");
        }else if (vo.getCustomerName().indexOf("中信银行") != -1 || vo.getCustomerName().indexOf("招商银行") != -1)
        {
            request.setAttribute("packageId", vo.getId());

            request.setAttribute("title", "永银文化创意产业发展有限责任公司产品发货清单");

            ConditionParse con2 = new ConditionParse();
            con2.addWhereStr();
            con2.addCondition("PackageBean.pickupId", "=", pickupId);

            List<PackageVO> allPackages = packageDAO.queryVOsByCondition(con2);
            if (!ListTools.isEmptyOrNull(allPackages)){
                _logger.info("****allPackages size****"+allPackages.size());
                request.setAttribute("allPackages", allPackages.size());

                //2015/3/30 批量打印最后一张回执单时，因定向到交接单打印，需要此时把最后一张CK单状态设置为“已打印"
                if ("0".equals(batchPrint) && allPackages.size() == index_pos){
                    // 更新状态
                    try
                    {
                        shipManager.updatePrintStatus(pickupId, index_pos);
                        _logger.info(pickupId+":"+index_pos+" print finished***");
                    }
                    catch (MYException e)
                    {
                        request.setAttribute(KeyConstant.ERROR_MESSAGE, "已打印出错." + e.getErrorContent());

                        return mapping.findForward("error");
                    }
                }
            }

            try{
                String msg5 = "**********before prepareForBankPrint****";
                _logger.info(msg5);
                prepareForBankPrint(request, vo, itemList, compose);
                String msg6 = "**********after prepareForBankPrint****";
                _logger.info(msg6);
            }catch(Exception e){
                e.printStackTrace();
                _logger.error("****printBankReceipt exception***",e);
            }

            return mapping.findForward("printBankReceipt");

        }else if (vo.getCustomerName().indexOf("浦发银行") != -1)
        {
            request.setAttribute("packageId", vo.getId());

            request.setAttribute("title", "北京黄金交易中心有限公司贵金属产品发货清单");

            //for (PackageItemBean each : itemList)
            for (Iterator<PackageItemBean> iterator = itemList.iterator(); iterator.hasNext();)
            {
                PackageItemBean each = iterator.next();

                if (!each.getCustomerId().equals(vo.getCustomerId()))
                {
                    iterator.remove();

                    continue;
                }

                List<OutImportBean> outiList = outImportDAO.queryEntityBeansByFK(each.getOutId(), AnoConstant.FK_FIRST);

                if (!ListTools.isEmptyOrNull(outiList))
                {
                    each.setRefId(outiList.get(0).getCiticNo());
                    each.setOutTime(outiList.get(0).getCiticOrderDate());
                }

                if (each.getDescription().indexOf("赠品") != -1)
                {
                    each.setDescription("赠品");
                }else
                {
                    each.setDescription(each.getPrintText());
                }

                totalAmount += each.getAmount();
            }

            vo.setItemList(itemList);

            request.setAttribute("total", totalAmount);

            return mapping.findForward("printPufaReceipt");

        }else{  // 打印发货单
            //request.setAttribute("packageId", "None");
            request.setAttribute("packageId", vo.getId());

            List<PackageItemBean> itemList1 = new ArrayList<PackageItemBean>();

            Map<String, PackageItemBean> map1 = new HashMap<String, PackageItemBean>();

            for (PackageItemBean each : itemList)
            {
                String key = each.getProductId();

                if (!map1.containsKey(key))
                {
                    map1.put(each.getProductId(), each);
                }else{
                    PackageItemBean itemBean = map1.get(key);

                    itemBean.setAmount(itemBean.getAmount() + each.getAmount());
                }
            }

            for(Entry<String, PackageItemBean> each : map1.entrySet())
            {
                itemList1.add(each.getValue());
            }

            vo.setItemList(itemList1);

            return mapping.findForward("printShipment");
        }
    }

    private String[] getStafferNameAndPhone(String outId){
        _logger.info("*****getStafferNameAndPhone*****"+outId);
        String stafferName = "永银商务部";
        String phone = "4006518859";
        OutBean out = outDAO.find(outId);
        _logger.info("*****getStafferNameAndPhone out*****"+out);
        if (out!= null){
            String stafferId = out.getStafferId();
            _logger.info("*****getStafferNameAndPhone stafferId*****"+stafferId);
            StafferBean staffer = this.stafferDAO.find(stafferId);
            if (staffer!= null){
                if (!StringTools.isNullOrNone(staffer.getName())){
                    stafferName = staffer.getName();
                }
                if (!StringTools.isNullOrNone(staffer.getHandphone())){
                    phone = staffer.getHandphone();
                }
            }
        } else{
            _logger.warn("******OutBean not found*****"+outId);
        }
        return new String[]{stafferName,phone};
    }


    /**
     * prepareForBankPrint
     *
     * @param request
     * @param vo
     * @param itemList
     */
    private void prepareForBankPrint(HttpServletRequest request, PackageVO vo,
                                     List<PackageItemBean> itemList, String compose)
    {
        int totalAmount = 0 ;

        List<PackageItemBean> itemList1 = new ArrayList<PackageItemBean>();

        Map<String, PackageItemBean> map1 = new HashMap<String, PackageItemBean>();

        //2015/1/25 取商务联系人及电话
        if (!ListTools.isEmptyOrNull(itemList)){
            _logger.info("******itemList size****"+itemList.size());
            PackageItemBean first = itemList.get(0);
            String outId = first.getOutId();
            String stafferName = "永银商务部";
            String phone = "4006518859";
            _logger.info(first+"******first****"+outId);
            if (StringTools.isNullOrNone(outId)){
                _logger.warn("****Empty OutId***********"+first.getId());
            }else if (outId.startsWith("SO")){
                String[] result = this.getStafferNameAndPhone(outId);
                if (result.length>=2){
                    stafferName = result[0];
                    phone = result[1];
                }
            } else if(outId.startsWith("A")){
                InvoiceinsBean bean = this.invoiceinsDAO.find(outId);
                if (bean!= null){
                    String refIds = bean.getRefIds();
                    _logger.info(outId+"*****refIds found********"+refIds);
                    if (!StringTools.isNullOrNone(refIds)){
                        String[] temp = refIds.split(";");
                        String refOutId = null;
                        for (String out: temp){
                            if (out.startsWith("SO")){
                                refOutId = out;
                                break;
                            }
                        }
                        String[] result2 = this.getStafferNameAndPhone(refOutId);
                        if (result2.length>=2){
                            stafferName = result2[0];
                            phone = result2[1];
                        }
                    }
                }
            }
            _logger.info("*****stafferName***********"+stafferName);
            _logger.info("*******phone*************"+phone);
            request.setAttribute("stafferName", stafferName);
            request.setAttribute("phone",phone);
        }

        for (PackageItemBean each : itemList)
        {
            _logger.info(each.getId()+"****iterate package item:"+"***"+each.getOutId()+"***"+each.getDescription()+"***"+each.getRefId());
            if (!each.getCustomerId().equals(vo.getCustomerId()))
            {
                _logger.info("*************each.getCustomerId()***"+each.getCustomerId()+"****"+vo.getCustomerId());
                continue;
            }

            // 针对赠品,且有备注的订单,单独显示
            String outId = each.getOutId();

            OutBean out = outDAO.find(outId);

            if (out != null && out.getOutType() == OutConstant.OUTTYPE_OUT_PRESENT)
            {
                _logger.info("******赠品类型*****"+each.getOutId());
                List<OutImportBean> outiList = outImportDAO.queryEntityBeansByFK(each.getOutId(), AnoConstant.FK_FIRST);

                if (!ListTools.isEmptyOrNull(outiList))
                {
                    String refId = outiList.get(0).getCiticNo();
                    _logger.info("****refId:"+refId);
                    each.setRefId(refId);

                    if (!StringTools.isNullOrNone(outiList.get(0).getDescription()))
                    {
                        checkCompose(each, each, compose);

                        String description = outiList.get(0).getDescription();
                        _logger.info("****Description****"+description);
                        each.setDescription(description);

                        itemList1.add(each);

                        totalAmount += each.getAmount();

                        continue;
                    }
                }
            }

            String key = each.getProductId();

            if (!map1.containsKey(key))
            {
                checkCompose(each, each, compose);

                String refId = this.getRefId(each.getOutId());
                if (!StringTools.isNullOrNone(refId)){
                    each.setRefId(refId);
                }

                //2015/1/25 注释掉
//				each.setDescription("");

                map1.put(each.getProductId(), each);
            }else{
                PackageItemBean itemBean = map1.get(key);

                itemBean.setAmount(itemBean.getAmount() + each.getAmount());

                itemBean.setOutId(itemBean.getOutId() + "<br>" + each.getOutId());

                if (!StringTools.isNullOrNone(itemBean.getRefId()))
                {
                    String refId = this.getRefId(each.getOutId());
                    if (!StringTools.isNullOrNone(refId))
                    {
                        String refId3 = itemBean.getRefId() + "<br>" + refId;
                        _logger.info("**********refId3**********"+refId3);
                        itemBean.setRefId(refId3);
                    }
                }else{
                    if (!StringTools.isNullOrNone(each.getRefId()))
                    {
                        _logger.info("**********refId4**********"+each.getRefId());
                        itemBean.setRefId(each.getRefId());
                    }
                }

                //2015/1/29 合并Description
                if (!StringTools.isNullOrNone(itemBean.getDescription()))
                {
                    if (!StringTools.isNullOrNone(each.getDescription()))
                    {
                        String description = itemBean.getDescription() + "<br>" + each.getDescription();
                        _logger.info("**********description2**********"+description);
                        itemBean.setDescription(description);
                    }
                }else{
                    if (!StringTools.isNullOrNone(each.getDescription()))
                    {
                        itemBean.setDescription(each.getDescription());
                    }
                }
            }

            totalAmount += each.getAmount();
        }

        for(Entry<String, PackageItemBean> each : map1.entrySet())
        {
            itemList1.add(each.getValue());
            _logger.debug("**********getDescription******"+each.getValue().getDescription());
        }

        vo.setItemList(itemList1);

        request.setAttribute("total", totalAmount);
    }

    private String getRefId(String outId){
        String refId = "";
        List<OutImportBean> outiList = outImportDAO.queryEntityBeansByFK(outId, AnoConstant.FK_FIRST);

        if (!ListTools.isEmptyOrNull(outiList))
        {
            refId = outiList.get(0).getCiticNo();
            _logger.info("**************redId2*****"+refId);
        }
        return refId;
    }

    /**
     * updateStatus
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws ServletException
     */
    public ActionForward updateStatus(ActionMapping mapping, ActionForm form,
                                      HttpServletRequest request,
                                      HttpServletResponse response)
            throws ServletException
    {
        String pickupId = request.getParameter("pickupId");

        User user = Helper.getUser(request);

        AjaxResult ajax = new AjaxResult();

        try{
            shipManager.updateStatus(user, pickupId);

            ajax.setSuccess("更新成功");
        }catch(MYException e)
        {
            _logger.warn(e, e);

            ajax.setError("更新失败");
        }

        return JSONTools.writeResponse(response, ajax);
    }

    /** 确认发货
     * mUpdateStatus
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws ServletException
     */
    public ActionForward mUpdateStatus(ActionMapping mapping, ActionForm form,
                                       HttpServletRequest request,
                                       HttpServletResponse response)
            throws ServletException
    {
        String pickupId = request.getParameter("pickupId");
        String packageIds = request.getParameter("packageIds");

        User user = Helper.getUser(request);

        if (StringTools.isNullOrNone(pickupId)){
            _logger.info("****mUpdateStatus with packageIds****"+packageIds);
            try{
                shipManager.updatePackagesStatus(user, packageIds);
                request.setAttribute(KeyConstant.MESSAGE, "发货确认成功");
            }catch(MYException e)
            {
                _logger.warn(e, e);

                request.setAttribute(KeyConstant.ERROR_MESSAGE, "确认发货失败:"+e.getErrorContent());
            }
        } else {
            _logger.info("****mUpdateStatus with pickupId****"+pickupId);
            try{
                shipManager.updateStatus(user, pickupId);
                request.setAttribute(KeyConstant.MESSAGE, "发货确认成功");
            }catch(MYException e)
            {
                _logger.warn(e, e);

                request.setAttribute(KeyConstant.ERROR_MESSAGE, "确认发货失败:"+e.getErrorContent());
            }
        }



//        return mapping.findForward("queryPickup");
        return this.queryPickup(mapping, form, request, response);
    }

    /**
     * 批量导入分支行对应关系信息
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws ServletException
     */
    public ActionForward importBranchRelation(ActionMapping mapping, ActionForm form,
                                              HttpServletRequest request, HttpServletResponse response)
            throws ServletException
    {
        RequestDataStream rds = new RequestDataStream(request);

        boolean importError = false;

        List<BranchRelationBean> importItemList = new ArrayList<BranchRelationBean>();

        StringBuilder builder = new StringBuilder();
        try
        {
            rds.parser();
        }
        catch (Exception e1)
        {
            _logger.error(e1, e1);

            request.setAttribute(KeyConstant.ERROR_MESSAGE, "解析失败");

            return mapping.findForward("importBranchRelation");
        }

        if ( !rds.haveStream())
        {
            request.setAttribute(KeyConstant.ERROR_MESSAGE, "解析失败");

            return mapping.findForward("importBranchRelation");
        }

        ReaderFile reader = ReadeFileFactory.getXLSReader();
        try
        {
            reader.readFile(rds.getUniqueInputStream());

            while (reader.hasNext())
            {
                String[] obj = fillObj((String[])reader.next());

                // 第一行忽略
                if (reader.getCurrentLineNumber() == 1)
                {
                    continue;
                }

                if (StringTools.isNullOrNone(obj[0]))
                {
                    continue;
                }

                int currentNumber = reader.getCurrentLineNumber();

                if (obj.length >= 2 )
                {
                    BranchRelationBean bean = new BranchRelationBean();

                    // 客户ID
                    if ( !StringTools.isNullOrNone(obj[0]))
                    {
                        bean.setId(obj[0]);
                    }
                    else
                    {
                        builder
                                .append("第[" + currentNumber + "]错误:")
                                .append("客户ID不能为空")
                                .append("<br>");

                        importError = true;
                    }

                    //支行名称
                    if ( !StringTools.isNullOrNone(obj[1]))
                    {
                        bean.setSubBranchName(obj[1]);
                    } else
                    {
                        builder
                                .append("第[" + currentNumber + "]错误:")
                                .append("支行名称不能为空")
                                .append("<br>");

                        importError = true;
                    }

                    //分行名称
                    if ( !StringTools.isNullOrNone(obj[2]))
                    {
                        bean.setBranchName(obj[2]);
                    }  else
                    {
                        builder
                                .append("第[" + currentNumber + "]错误:")
                                .append("分行名称不能为空")
                                .append("<br>");

                        importError = true;
                    }

                    //分行邮件地址
                    if ( !StringTools.isNullOrNone(obj[3]))
                    {
                        bean.setBranchMail(obj[3]);
                    }


                    //支行邮件地址
                    if ( !StringTools.isNullOrNone(obj[4]))
                    {
                        bean.setSubBranchMail(obj[4]);
                    }else
                    {
                        builder
                                .append("第[" + currentNumber + "]错误:")
                                .append("支行邮件地址不能为空")
                                .append("<br>");

                        importError = true;
                    }

                    //发送邮件标志
                    if ( !StringTools.isNullOrNone(obj[5]))
                    {
                        bean.setSendMailFlag(Integer.valueOf(obj[5]));
                    }else
                    {
                        builder
                                .append("第[" + currentNumber + "]错误:")
                                .append("发送邮件标志不能为空，比如为0或1")
                                .append("<br>");

                        importError = true;
                    }

                    //抄送分行标志
                    if ( !StringTools.isNullOrNone(obj[6]))
                    {
                        bean.setCopyToBranchFlag(Integer.valueOf(obj[6]));
                    }
                    importItemList.add(bean);

                }
                else
                {
                    builder
                            .append("第[" + currentNumber + "]错误:")
                            .append("数据长度不足7格错误")
                            .append("<br>");

                    importError = true;
                }
            }
        }catch (Exception e)
        {
            _logger.error(e, e);

            request.setAttribute(KeyConstant.ERROR_MESSAGE, e.toString());

            return mapping.findForward("importBranchRelation");
        }
        finally
        {
            try
            {
                reader.close();
            }
            catch (IOException e)
            {
                _logger.error(e, e);
            }
        }

        rds.close();
        if (importError){

            request.setAttribute(KeyConstant.ERROR_MESSAGE, "导入出错:"+ builder.toString());

            return mapping.findForward("importBranchRelation");
        }

        try
        {
            this.shipManager.saveAllEntityBeans(importItemList);
            request.setAttribute(KeyConstant.MESSAGE, "批量更新成功");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            request.setAttribute(KeyConstant.ERROR_MESSAGE, "导入出错:"+ e.getMessage());

            return mapping.findForward("importBranchRelation");
        }
        return mapping.findForward("importBranchRelation");
    }

    /**
     *
     * @param obj
     * @return
     */
    private String[] fillObj(String[] obj)
    {
        String[] result = new String[50];

        for (int i = 0; i < result.length; i++ )
        {
            if (i < obj.length)
            {
                result[i] = obj[i];
            }
            else
            {
                result[i] = "";
            }
        }

        return result;
    }

    /**
     * 2015/3/11 打印货物交接清单
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws ServletException
     */
    public ActionForward printHandover(ActionMapping mapping, ActionForm form,
                                           HttpServletRequest request,
                                           HttpServletResponse response)
            throws ServletException
    {
        CommonTools.saveParamers(request);

        String pickupId = request.getParameter("pickupId");
        String printMode = request.getParameter("printMode");
        String sindex_pos = request.getParameter("index_pos");

        _logger.info("printHandover with pickupId***"+pickupId+"***printMode***"+printMode+"***sindex_pos***"+sindex_pos);

        int index_pos = 0;

        if (!StringTools.isNullOrNone(sindex_pos))
        {
            index_pos = MathTools.parseInt(sindex_pos);
        }

        index_pos += 1;

        // 先找出该批次下所有index大于index_pos的CK单
        ConditionParse condtion = new ConditionParse();
        condtion.addWhereStr();
        condtion.addCondition("PackageBean.pickupId", "=", pickupId);
        condtion.addIntCondition("PackageBean.index_pos", ">=", index_pos);
        condtion.addCondition("order by PackageBean.index_pos asc");

        List<PackageVO> packageList = this.packageDAO.queryVOsByCondition(condtion);

        if (ListTools.isEmptyOrNull(packageList))
        {
            request.setAttribute(KeyConstant.ERROR_MESSAGE, "已打印完毕");

            return mapping.findForward("error");
        }

        _logger.info("****printHandover packageList size***"+packageList.size());

//        List<PackageVO> packageList = packageDAO.queryEntityVOsByFK(pickupId);

        StringBuilder sb = new StringBuilder();


        int pickupCount = packageList.size();

        //<key,value> as <productId,PackageItemBean>
        Map<String, PackageItemBean> map = new HashMap<String, PackageItemBean>();
        boolean printFlag = false;
        for (PackageVO each : packageList)
        {
            //2015/3/23 判断业务员为“叶百韬”且 销售单上的客户名称为“中信银行XXXX”类型的才打印
            String stafferName = each.getStafferName();
            String customerName = each.getCustomerName();
            _logger.info("****stafferName***"+stafferName+"***customerName***"+customerName);
            if ("叶百韬".equals(stafferName) && customerName.indexOf("中信银行")!= -1){
                _logger.info(each.getId()+" print handover for CK with index:"+index_pos);

                index_pos = each.getIndex_pos();

                sb.append(each.getId()).append("<br>");

                List<PackageItemBean> itemList = packageItemDAO.queryEntityBeansByFK(each.getId());

                // 根据产品分组: 1.判断是否为合成,如是,则要找出子产品;2.数量合并
                for (PackageItemBean eachItem : itemList)
                {
                    if (map.containsKey(eachItem.getProductId()))
                    {
                        PackageItemBean itemBean = map.get(eachItem.getProductId());

                        itemBean.setAmount(itemBean.getAmount() + eachItem.getAmount());
                    }else{
                        PackageItemBean itemBean = new PackageItemBean();

                        itemBean.setProductId(eachItem.getProductId());
                        itemBean.setProductName(eachItem.getProductName());
                        itemBean.setAmount(eachItem.getAmount());
                        //itemBean.setShowSubProductName(showSubProductName);

                        map.put(eachItem.getProductId(), itemBean);
                    }
                }

                printFlag = true;
                break;
            } else{
                continue;
            }
        }

        if (!printFlag){
            request.setAttribute(KeyConstant.ERROR_MESSAGE, "已打印完毕");

            return mapping.findForward("error");
        }

        PackageVO batchVO = new PackageVO();

        batchVO.setId(sb.toString());
        batchVO.setPickupId(pickupId);
        batchVO.setRepTime(TimeTools.now_short());

        List<PackageItemBean> lastList = new ArrayList<PackageItemBean>();

        for (Entry<String, PackageItemBean> entry : map.entrySet())
        {
            lastList.add(entry.getValue());
        }

        //2015/1/14 按照名称排序
        Collections.sort(lastList, new Comparator(){
            @Override
            public int compare(Object o1, Object o2) {
                PackageItemBean i1 = (PackageItemBean)o1;
                PackageItemBean i2 = (PackageItemBean)o2;
                return i1.getProductName().compareTo(i2.getProductName());
            }
        });
        batchVO.setItemList(lastList);

        // key:以批次号做为key ?
        request.setAttribute("bean", batchVO);

        request.setAttribute("year", TimeTools.now("yyyy"));
        request.setAttribute("month", TimeTools.now("MM"));
        request.setAttribute("day", TimeTools.now("dd"));

        request.setAttribute("index_pos", index_pos);
        request.setAttribute("pickupCount", pickupCount);

        return mapping.findForward("printHandover");

    }

    public ActionForward preForMergePackages(ActionMapping mapping, ActionForm form,
                                          HttpServletRequest request, HttpServletResponse response){
        String packageIds = request.getParameter("packageIds");
        request.getSession().setAttribute("packageIds", packageIds);
        _logger.info("***************preForMergePackages**************"+packageIds);

        //运输方式
        List<ExpressBean> expressList = this.expressDAO.listEntityBeans();
        request.setAttribute("expressList", expressList);

        //省市
        List<ProvinceBean> provinceList = this.provinceDAO.listEntityBeans();
        request.setAttribute("provinceList", provinceList);
        List<CityBean> cityList = this.cityDAO.listEntityBeans();
        request.setAttribute("cityList", cityList);
        return mapping.findForward("mergePackages");
    }

    public ActionForward mergePackages(ActionMapping mapping, ActionForm form,
                                    HttpServletRequest request, HttpServletResponse response)
            throws ServletException{
        String shippingStr = request.getParameter("shipping");
        int shipping = 0;
        if (!StringTools.isNullOrNone(shippingStr)){
            shipping = Integer.valueOf(shippingStr);
        }
        String transport1Str = request.getParameter("transport1");
        int transport1 = 0;
        if (!StringTools.isNullOrNone(transport1Str)){
            try{
                transport1 = Integer.valueOf(transport1Str);
            }catch(Exception e){}
        }
        String transport2Str = request.getParameter("transport2");
        int transport2 = -1;
        if (!StringTools.isNullOrNone(transport2Str)){
            try{
                transport2 = Integer.valueOf(transport2Str);
            }catch(Exception e){}
        }

        String expressPayStr = request.getParameter("expressPay");
        int expressPay = -1;
        if (!StringTools.isNullOrNone(expressPayStr)){
            try{
                expressPay = Integer.valueOf(expressPayStr);
            }catch(Exception e){}
        }

        String transportPayStr = request.getParameter("transportPay");
        int transportPay = -1;
        if (!StringTools.isNullOrNone(transportPayStr)){
            try{
                transportPay = Integer.valueOf(transportPayStr);
            }catch(Exception e){}
        }

        String cityId = request.getParameter("cityId");
        String address = request.getParameter("address");
        String receiver = request.getParameter("receiver");
        String phone = request.getParameter("phone");
        String packageIds = (String)request.getSession().getAttribute("packageIds");
        _logger.info("***mergePickups with param***"+shipping+":"+transport1+":"+transport2+":"+expressPay+":"+transportPay+":"+address+";"+receiver+":"+phone+":"+packageIds);
        try
        {
            this.shipManager.mergePackages(null,packageIds, shipping, transport1, transport2, expressPay, transportPay,cityId, address, receiver, phone );
            request.setAttribute(KeyConstant.MESSAGE, "手动合并出库单成功");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            _logger.error("手动合并出库单失败:", e);
            request.setAttribute(KeyConstant.ERROR_MESSAGE, "手动合并出库单出错:"+ e.getMessage());

            return mapping.findForward("queryPickup");
        }

        return mapping.findForward("queryPickup");
//        return this.queryPackage(mapping,form, request, response);
    }

    public ActionForward preForAutoPickup(ActionMapping mapping, ActionForm form,
                                    HttpServletRequest request, HttpServletResponse response){
        _logger.info("***************preForAutoPickup**************");
        return mapping.findForward("addAutoPickup");
    }

    public ActionForward autoPickup(ActionMapping mapping, ActionForm form,
                                              HttpServletRequest request, HttpServletResponse response){
        String pickupCount = request.getParameter("pickupCount");
        String productName = request.getParameter("productName");
        String productId = request.getParameter("productId");
        _logger.info("*****autoPickup****************"+pickupCount+";"+productName+":"+productId);
        try
        {
            List<String> pickupList = this.shipManager.autoPickup(Integer.valueOf(pickupCount), productName);
            StringBuilder sb = new StringBuilder();
            for (String pickupId: pickupList){
                sb.append(pickupId)
                    .append(";");
            }
            request.setAttribute(KeyConstant.MESSAGE, "自动捡配成功:"+sb.toString());
        }
        catch(Exception e)
        {
            e.printStackTrace();
            _logger.error("自动捡配失败:", e);
            request.setAttribute(KeyConstant.ERROR_MESSAGE, "自动捡配出错:"+ e.getMessage());

            return mapping.findForward("queryPickup");
        }

        return mapping.findForward("queryPickup");
    }

    public PackageDAO getPackageDAO()
    {
        return packageDAO;
    }

    public void setPackageDAO(PackageDAO packageDAO)
    {
        this.packageDAO = packageDAO;
    }

    public PackageItemDAO getPackageItemDAO()
    {
        return packageItemDAO;
    }

    public void setPackageItemDAO(PackageItemDAO packageItemDAO)
    {
        this.packageItemDAO = packageItemDAO;
    }

    public OutDAO getOutDAO()
    {
        return outDAO;
    }

    public void setOutDAO(OutDAO outDAO)
    {
        this.outDAO = outDAO;
    }

    public BaseDAO getBaseDAO()
    {
        return baseDAO;
    }

    public void setBaseDAO(BaseDAO baseDAO)
    {
        this.baseDAO = baseDAO;
    }

    public DistributionDAO getDistributionDAO()
    {
        return distributionDAO;
    }

    public void setDistributionDAO(DistributionDAO distributionDAO)
    {
        this.distributionDAO = distributionDAO;
    }

    public ExpressDAO getExpressDAO()
    {
        return expressDAO;
    }

    public void setExpressDAO(ExpressDAO expressDAO)
    {
        this.expressDAO = expressDAO;
    }

    public ShipManager getShipManager()
    {
        return shipManager;
    }

    public void setShipManager(ShipManager shipManager)
    {
        this.shipManager = shipManager;
    }

    public ProductDAO getProductDAO()
    {
        return productDAO;
    }

    public void setProductDAO(ProductDAO productDAO)
    {
        this.productDAO = productDAO;
    }

    public ComposeProductDAO getComposeProductDAO()
    {
        return composeProductDAO;
    }

    public void setComposeProductDAO(ComposeProductDAO composeProductDAO)
    {
        this.composeProductDAO = composeProductDAO;
    }

    public ComposeItemDAO getComposeItemDAO()
    {
        return composeItemDAO;
    }

    public void setComposeItemDAO(ComposeItemDAO composeItemDAO)
    {
        this.composeItemDAO = composeItemDAO;
    }

    /**
     * @return the outImportDAO
     */
    public OutImportDAO getOutImportDAO()
    {
        return outImportDAO;
    }

    /**
     * @param outImportDAO the outImportDAO to set
     */
    public void setOutImportDAO(OutImportDAO outImportDAO)
    {
        this.outImportDAO = outImportDAO;
    }

    /**
     * @return the packageVSCustomerDAO
     */
    public PackageVSCustomerDAO getPackageVSCustomerDAO()
    {
        return packageVSCustomerDAO;
    }

    /**
     * @param packageVSCustomerDAO the packageVSCustomerDAO to set
     */
    public void setPackageVSCustomerDAO(PackageVSCustomerDAO packageVSCustomerDAO)
    {
        this.packageVSCustomerDAO = packageVSCustomerDAO;
    }

    /**
     * @return the userManager
     */
    public UserManager getUserManager()
    {
        return userManager;
    }

    /**
     * @param userManager the userManager to set
     */
    public void setUserManager(UserManager userManager)
    {
        this.userManager = userManager;
    }

    /**
     * @return the depotDAO
     */
    public DepotDAO getDepotDAO()
    {
        return depotDAO;
    }

    /**
     * @param depotDAO the depotDAO to set
     */
    public void setDepotDAO(DepotDAO depotDAO)
    {
        this.depotDAO = depotDAO;
    }

    public BranchRelationDAO getBranchRelationDAO() {
        return branchRelationDAO;
    }

    public void setBranchRelationDAO(BranchRelationDAO branchRelationDAO) {
        this.branchRelationDAO = branchRelationDAO;
    }

    public StafferDAO getStafferDAO() {
        return stafferDAO;
    }

    public void setStafferDAO(StafferDAO stafferDAO) {
        this.stafferDAO = stafferDAO;
    }

    public InvoiceinsDAO getInvoiceinsDAO() {
        return invoiceinsDAO;
    }

    public void setInvoiceinsDAO(InvoiceinsDAO invoiceinsDAO) {
        this.invoiceinsDAO = invoiceinsDAO;
    }

    public CustomerMainDAO getCustomerMainDAO() {
        return customerMainDAO;
    }

    public void setCustomerMainDAO(CustomerMainDAO customerMainDAO) {
        this.customerMainDAO = customerMainDAO;
    }

    public ProvinceDAO getProvinceDAO() {
        return provinceDAO;
    }

    public void setProvinceDAO(ProvinceDAO provinceDAO) {
        this.provinceDAO = provinceDAO;
    }

    public CityDAO getCityDAO() {
        return cityDAO;
    }

    public void setCityDAO(CityDAO cityDAO) {
        this.cityDAO = cityDAO;
    }
}