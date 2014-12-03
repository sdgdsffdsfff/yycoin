package com.china.center.oa.sail.manager.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.center.china.osgi.config.ConfigLoader;
import com.china.center.oa.publics.manager.CommonMailManager;
import com.china.center.tools.MathTools;
import jxl.Workbook;
import jxl.format.PageOrientation;
import jxl.format.PaperSize;
import jxl.write.*;
import jxl.write.biff.RowsExceededException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.center.china.osgi.publics.User;
import com.china.center.common.MYException;
import com.china.center.jdbc.util.ConditionParse;
import com.china.center.oa.product.bean.DepotBean;
import com.china.center.oa.product.dao.DepotDAO;
import com.china.center.oa.publics.dao.CommonDAO;
import com.china.center.oa.sail.bean.BaseBean;
import com.china.center.oa.sail.bean.OutBean;
import com.china.center.oa.sail.bean.PackageBean;
import com.china.center.oa.sail.bean.PackageItemBean;
import com.china.center.oa.sail.bean.PackageVSCustomerBean;
import com.china.center.oa.sail.bean.PreConsignBean;
import com.china.center.oa.sail.constanst.OutConstant;
import com.china.center.oa.sail.constanst.ShipConstant;
import com.china.center.oa.sail.dao.BaseDAO;
import com.china.center.oa.sail.dao.DistributionDAO;
import com.china.center.oa.sail.dao.OutDAO;
import com.china.center.oa.sail.dao.PackageDAO;
import com.china.center.oa.sail.dao.PackageItemDAO;
import com.china.center.oa.sail.dao.PackageVSCustomerDAO;
import com.china.center.oa.sail.dao.PreConsignDAO;
import com.china.center.oa.sail.manager.ShipManager;
import com.china.center.oa.sail.vo.DistributionVO;
import com.china.center.oa.sail.vo.OutVO;
import com.china.center.oa.sail.vo.PackageVO;
import com.china.center.tools.JudgeTools;
import com.china.center.tools.ListTools;
import com.china.center.tools.TimeTools;

public class ShipManagerImpl implements ShipManager
{
	private final Log operationLog = LogFactory.getLog("opr");
	
	private PreConsignDAO preConsignDAO = null;
	
	private PackageDAO packageDAO = null;
	
	private PackageItemDAO packageItemDAO = null;
	
	private OutDAO outDAO = null;
	
	private BaseDAO baseDAO = null;
	
	private DistributionDAO distributionDAO = null;
	
	private CommonDAO commonDAO = null;
	
	private DepotDAO depotDAO = null;
	
	private PackageVSCustomerDAO packageVSCustomerDAO = null;

    private CommonMailManager commonMailManager = null;
	
	public ShipManagerImpl()
	{
	}
	
	private void createNewPackage(OutVO outBean,
			List<BaseBean> baseList, DistributionVO distVO, String fullAddress, String location)
	{
		String id = commonDAO.getSquenceString20("CK");
		
		int allAmount = 0;
		
		PackageBean packBean = new PackageBean();
		
		packBean.setId(id);
		packBean.setCustomerId(outBean.getCustomerId());
		packBean.setShipping(distVO.getShipping());
		packBean.setTransport1(distVO.getTransport1());
		packBean.setExpressPay(distVO.getExpressPay());
		packBean.setTransport2(distVO.getTransport2());
		packBean.setTransportPay(distVO.getTransportPay());
		packBean.setAddress(fullAddress);
		packBean.setReceiver(distVO.getReceiver());
		packBean.setMobile(distVO.getMobile());
		packBean.setLocationId(location);
		packBean.setCityId(distVO.getCityId());
		
		packBean.setStafferName(outBean.getStafferName());
		packBean.setIndustryName(outBean.getIndustryName());
		packBean.setDepartName(outBean.getIndustryName3());
		
		packBean.setTotal(outBean.getTotal());
		packBean.setStatus(0);
		packBean.setLogTime(TimeTools.now());
		
		List<PackageItemBean> itemList = new ArrayList<PackageItemBean>();
		
		Map<String, List<BaseBean>> pmap = new HashMap<String, List<BaseBean>>();
		
		boolean isEmergency = false;
		for (BaseBean base : baseList)
		{
			PackageItemBean item = new PackageItemBean();
			
			item.setPackageId(id);
			item.setOutId(outBean.getFullId());
			item.setBaseId(base.getId());
			item.setProductId(base.getProductId());
			item.setProductName(base.getProductName());
			item.setAmount(base.getAmount());
			item.setPrice(base.getPrice());
			item.setValue(base.getValue());
			item.setOutTime(outBean.getOutTime());
			item.setDescription(outBean.getDescription());
			item.setCustomerId(outBean.getCustomerId());
			item.setEmergency(outBean.getEmergency());
			
			if (item.getEmergency() == 1) {
				isEmergency = true;
			}
			
			itemList.add(item);
			
			allAmount += item.getAmount();
			
			if (!pmap.containsKey(base.getProductId()))
			{
				List<BaseBean> blist = new ArrayList<BaseBean>();
				
				blist.add(base);
				
				pmap.put(base.getProductId(), blist);
			}else
			{
				List<BaseBean> blist = pmap.get(base.getProductId());
				
				blist.add(base);
			}
		}
		
		packBean.setAmount(allAmount);
		
		if (isEmergency) {
			packBean.setEmergency(OutConstant.OUT_EMERGENCY_YES);
		}
		
		packBean.setProductCount(pmap.values().size());
		
		PackageVSCustomerBean vsBean = new PackageVSCustomerBean();
		
		vsBean.setPackageId(id);
		vsBean.setCustomerId(outBean.getCustomerId());
		vsBean.setCustomerName(outBean.getCustomerName());
		vsBean.setIndexPos(1);
		
		packageDAO.saveEntityBean(packBean);
		
		packageItemDAO.saveAllEntityBeans(itemList);
		
		packageVSCustomerDAO.saveEntityBean(vsBean);
	}
	
	private void setInnerCondition(DistributionVO distVO, String location, ConditionParse con)
	{
		//con.addCondition("PackageBean.customerId", "=", outBean.getCustomerId());
		con.addCondition("PackageBean.cityId", "=", distVO.getCityId());  // 借用outId 用于存储城市。生成出库单增加 城市 维度
		
		con.addIntCondition("PackageBean.shipping", "=", distVO.getShipping());
		
		con.addIntCondition("PackageBean.transport1", "=", distVO.getTransport1());
		
		con.addIntCondition("PackageBean.expressPay", "=", distVO.getExpressPay());
		
		con.addIntCondition("PackageBean.transport2", "=", distVO.getTransport2());
		
		con.addIntCondition("PackageBean.transportPay", "=", distVO.getTransportPay());
		
		con.addCondition("PackageBean.locationId", "=", location);
		
		con.addCondition("PackageBean.receiver", "=", distVO.getReceiver());
		
		con.addCondition("PackageBean.mobile", "=", distVO.getMobile());
		
		con.addIntCondition("PackageBean.status", "=", 0);
	}

	/**
	 * 
	 */
	public void createPackage(PreConsignBean pre, OutVO out) throws MYException
	{
		String location = "";
		
		// 通过仓库获取 仓库地点
		DepotBean depot = depotDAO.find(out.getLocation());
		
		if (depot != null)
			location = depot.getIndustryId2();
		
		List<BaseBean> baseList = baseDAO.queryEntityBeansByFK(out.getFullId());
		
		List<DistributionVO> distList = distributionDAO.queryEntityVOsByFK(out.getFullId());
		
		if (ListTools.isEmptyOrNull(distList))
		{
			preConsignDAO.deleteEntityBean(pre.getId());
			
			return;
		}
		
		DistributionVO distVO = distList.get(0);
		
		// 如果是空发,则不处理
		if (distVO.getShipping() == OutConstant.OUT_SHIPPING_NOTSHIPPING)
		{
			preConsignDAO.deleteEntityBean(pre.getId());
			
			return;
		}
		
		// 地址不全,不发
		if (distVO.getAddress().trim().equals("0") && distVO.getReceiver().trim().equals("0") && distVO.getMobile().trim().equals("0"))
		{
			return;
		}
		
		String fullAddress = distVO.getProvinceName()+distVO.getCityName()+distVO.getAddress();
		
		// 此客户是否存在同一个发货包裹,且未拣配
		ConditionParse con = new ConditionParse();
		
		con.addWhereStr();
		
		setInnerCondition(distVO, location, con);
		
		List<PackageVO> packageList = packageDAO.queryVOsByCondition(con);
		
//		if (packageList.size() > 1){
//			throw new MYException("数据异常,生成发货单出错.");
//		}
		
		if (ListTools.isEmptyOrNull(packageList))
		{
			createNewPackage(out, baseList, distVO, fullAddress, location);
			
		}else{
			String id = packageList.get(0).getId();
			
			PackageBean packBean = packageDAO.find(id);
			
			// 不存在或已不是初始状态(可能已被拣配)
			if (null == packBean || packBean.getStatus() != 0)
			{
				createNewPackage(out, baseList, distVO, fullAddress, location);
			}else
			{
				List<PackageItemBean> itemList = new ArrayList<PackageItemBean>();
				
				int allAmount = 0;
				double total = 0;
				
				Map<String, List<BaseBean>> pmap = new HashMap<String, List<BaseBean>>();
				boolean isEmergency = false;
				for (BaseBean base : baseList)
				{
					PackageItemBean item = new PackageItemBean();
					
					item.setPackageId(id);
					item.setOutId(out.getFullId());
					item.setBaseId(base.getId());
					item.setProductId(base.getProductId());
					item.setProductName(base.getProductName());
					item.setAmount(base.getAmount());
					item.setPrice(base.getPrice());
					item.setValue(base.getValue());
					item.setOutTime(out.getOutTime());
					item.setDescription(out.getDescription());
					item.setCustomerId(out.getCustomerId());
					item.setEmergency(out.getEmergency());
					
					if (item.getEmergency() == 1) {
						isEmergency = true;
					}
					
					itemList.add(item);
					
					allAmount += item.getAmount();
					total += base.getValue();
					
					if (!pmap.containsKey(base.getProductId()))
					{
						List<BaseBean> blist = new ArrayList<BaseBean>();
						
						blist.add(base);
						
						pmap.put(base.getProductId(), blist);
					}else
					{
						List<BaseBean> blist = pmap.get(base.getProductId());
						
						blist.add(base);
					}
				}
				
				packBean.setAmount(packBean.getAmount() + allAmount);
				packBean.setTotal(packBean.getTotal() + total);
				packBean.setProductCount(packBean.getProductCount() + pmap.values().size());
				
				if (isEmergency) {
					packBean.setEmergency(OutConstant.OUT_EMERGENCY_YES);
				}
				
				packageDAO.updateEntityBean(packBean);
				
				packageItemDAO.saveAllEntityBeans(itemList);
				
				// 包与客户关系
				PackageVSCustomerBean vsBean = packageVSCustomerDAO.findByUnique(id, out.getCustomerId());
				
				if (null == vsBean)
				{
					int count = packageVSCustomerDAO.countByCondition("where packageId = ?", id);
					
					PackageVSCustomerBean newvsBean = new PackageVSCustomerBean();
					
					newvsBean.setPackageId(id);
					newvsBean.setCustomerId(out.getCustomerId());
					newvsBean.setCustomerName(out.getCustomerName());
					newvsBean.setIndexPos(count + 1);
					
					packageVSCustomerDAO.saveEntityBean(newvsBean);
				}
			}
		}
		
		preConsignDAO.deleteEntityBean(pre.getId());
	}
	
	/**
	 * 拣配包
	 */
	@Transactional(rollbackFor = MYException.class)
	public boolean addPickup(User user, String packageIds) throws MYException
	{
		JudgeTools.judgeParameterIsNull(user, packageIds);

		String [] packages = packageIds.split("~");

		if (null != packages)
		{
			String pickupId = commonDAO.getSquenceString20("PC");
			
			int i = 1;
			
			for (String id : packages)
			{
				// 只能拣配初始态的
				PackageBean bean = packageDAO.find(id);
				
				if (null == bean)
				{
					throw new MYException("出库单[%s]不存在", id);
				}
				
				if (bean.getStatus() != ShipConstant.SHIP_STATUS_INIT)
				{
					throw new MYException("[%s]已被拣配", id);
				}
				
				bean.setIndex_pos(i++);

				bean.setPickupId(pickupId);
				
				bean.setStatus(ShipConstant.SHIP_STATUS_PICKUP);
				
				packageDAO.updateEntityBean(bean);
			}
			
		}
		
		return true;
	}

	/**
	 * 撤销生成的出库单
	 */
	@Transactional(rollbackFor = MYException.class)
	public boolean deletePackage(User user, String packageIds)
			throws MYException
	{
		JudgeTools.judgeParameterIsNull(user, packageIds);
		
		String [] packages = packageIds.split("~");
		
		if (null != packages)
		{
			Set<String> set = new HashSet<String>();
			
			for (String id : packages)
			{
				// 只能拣配初始态的
				PackageBean bean = packageDAO.find(id);
				
				if (null == bean)
				{
					throw new MYException("出库单[%s]不存在", id);
				}
				
				packageDAO.deleteEntityBean(bean.getId());
				
				List<PackageItemBean> itemList = packageItemDAO.queryEntityBeansByFK(bean.getId());
				
				for (PackageItemBean each : itemList)
				{
					if (!set.contains(each.getOutId()))
					{
						set.add(each.getOutId());
					}
					
					packageItemDAO.deleteEntityBean(each.getId());
				}
				
				packageVSCustomerDAO.deleteEntityBeansByFK(id);
			}
			
			// 重新生成发货单
			for (String outId : set)
			{
				PreConsignBean pre = new PreConsignBean();
				
				pre.setOutId(outId);
				
				preConsignDAO.saveEntityBean(pre);
			}
			
			operationLog.info("重新生成发货单准备数据PreConsign:" + set);
		}
		
		return true;
	}

	/**
	 * 
	 */
	@Transactional(rollbackFor = MYException.class)
	public boolean updatePrintStatus(String pickupId, int index_pos) throws MYException
	{
		JudgeTools.judgeParameterIsNull(pickupId);
		
		ConditionParse condtion = new ConditionParse();
    	
    	condtion.addWhereStr();
    	
    	condtion.addCondition("PackageBean.pickupId", "=", pickupId);
    	condtion.addIntCondition("PackageBean.index_pos", "=", index_pos);

    	List<PackageBean> packageList = packageDAO.queryEntityBeansByCondition(condtion);
    	
    	if (!ListTools.isEmptyOrNull(packageList))
    	{
    		PackageBean packageBean = packageList.get(0);
    		
    		if (packageBean.getStatus() != ShipConstant.SHIP_STATUS_CONSIGN)
    		{
    			packageBean.setStatus(ShipConstant.SHIP_STATUS_PRINT);
        		
        		packageDAO.updateEntityBean(packageBean);
    		}
    	}
		
		return true;
	}
	
	@Transactional(rollbackFor = MYException.class)
	public boolean updateStatus(User user, String pickupId) throws MYException
	{
		JudgeTools.judgeParameterIsNull(user, pickupId);
		
		List<PackageBean> packageList = packageDAO.queryEntityBeansByFK(pickupId);
		
		Set<String> set = new HashSet<String>();
		
		for (PackageBean each : packageList)
		{
			each.setStatus(ShipConstant.SHIP_STATUS_CONSIGN);
			
			each.setShipTime(TimeTools.now());
			
			List<PackageItemBean> itemList = packageItemDAO.queryEntityBeansByFK(each.getId());
			
			for (PackageItemBean eachItem : itemList)
			{
				if (!set.contains(eachItem.getOutId()))
				{
					OutBean out = outDAO.find(eachItem.getOutId());
					
					if (null != out && out.getStatus() == OutConstant.STATUS_PASS)
					{
						outDAO.modifyOutStatus(out.getFullId(), OutConstant.STATUS_SEC_PASS);
						
						distributionDAO.updateOutboundDate(out.getFullId(), TimeTools.now_short());
					}
				}
			}
			
			packageDAO.updateEntityBean(each);
		}
		
		return true;
	}

    @Override
    public void sendMailForShipping() throws MYException {
        //To change body of implemented methods use File | Settings | File Templates.
        long now = System.currentTimeMillis();
        System.out.println("**************run schedule****************"+now);
//        this.commonMailManager.sendMail("smartman2014@qq.com","test","test message");


        ConditionParse con = new ConditionParse();

        con.addWhereStr();
        con.addIntCondition("PackageBean.sendMailFlag", "=", 0);

        con.addIntCondition("PackageBean.status", "=", 2);
        System.out.println("***********con**************"+con);

//        setInnerCondition(distVO, location, con);

        List<PackageVO> packageList = packageDAO.queryVOsByCondition(con);
        if (!ListTools.isEmptyOrNull(packageList))
        {
            for (PackageVO vo : packageList){
                System.out.println("************VO****"+vo);
                String fileName = getShippingAttachmentPath() + "/" + vo.getCustomerName()
                        + "_" + TimeTools.now("yyyyMMddHHmmss") + ".xls";
                System.out.println("************fileName****"+fileName);
                createMailAttachment(vo, fileName);

                // check file either exists
                File file = new File(fileName);

                if (!file.exists())
                {
                    throw new MYException("邮件附件未成功生成");
                }

                // send mail contain attachment
                commonMailManager.sendMail("smartman2014@qq.com", "发货信息邮件",
                        "永银文化创意产业发展有限责任公司发货信息，请查看附件，谢谢。", fileName);

                //Update sendMailFlag to 1
                PackageBean packBean = packageDAO.find(vo.getId());
                packBean.setSendMailFlag(1);
                this.packageDAO.updateEntityBean(packBean);
            }
        } else {
            System.out.println("**************no Vo found***************");
        }

    }

    /**
     * @return the mailAttchmentPath
     */
    public String getShippingAttachmentPath()
    {
        return ConfigLoader.getProperty("shippingAttachmentPath");
    }

    private void createMailAttachment(PackageVO bean, String fileName)
    {
        WritableWorkbook wwb = null;

        WritableSheet ws = null;

        OutputStream out = null;

        String content = "永银公司为正确反映双方的往来，特与贵公司核实往来账项等事项。下列信息出自本公司系统记录，如与贵公司记录相符，请在本函下端“信息证明无误”处签章证明；如有不符，请在“信息不符”处列明不符项目。如存在与本公司有关的未列入本函的其他项目，也请在“信息不符”处列出的这些项目的金额及其他详细资料。";

        String content2 = "回函地址：南京市秦淮区应天大街388号1865创意园c2栋   邮编：210006";

        String content3 = "电话：  4006518859           传真：  025-51885907      联系人：永银商务部";

        String content4 = "1．本公司至今与贵公司未结款商品的往来账项列示如下：";

        String content5 = "备注，以上均为已发货未付款，请贵公司核实，谢谢！";

        String content6 = "本函仅为复核账目之用，并非催款结算。若款项在上述日期之后已经付清，仍请及时函复为盼。";

//        List<FeedBackDetailBean> detailList = bean.getDetailList();
//
//        List<FeedBackDetailBean> mergeList = merge(detailList);

        try
        {
            out = new FileOutputStream(fileName);

            // create a excel
            wwb = Workbook.createWorkbook(out);

            ws = wwb.createSheet("发货信息", 0);

            // 横向
            ws.setPageSetup(PageOrientation.LANDSCAPE.LANDSCAPE, PaperSize.A4,0.5d,0.5d);

            // 标题字体
            WritableFont font = new WritableFont(WritableFont.ARIAL, 11,
                    WritableFont.BOLD, false,
                    jxl.format.UnderlineStyle.NO_UNDERLINE,
                    jxl.format.Colour.BLACK);

            WritableFont font2 = new WritableFont(WritableFont.ARIAL, 9,
                    WritableFont.BOLD, false,
                    jxl.format.UnderlineStyle.NO_UNDERLINE,
                    jxl.format.Colour.BLACK);

            WritableFont font3 = new WritableFont(WritableFont.ARIAL, 9,
                    WritableFont.NO_BOLD, false,
                    jxl.format.UnderlineStyle.NO_UNDERLINE,
                    jxl.format.Colour.BLACK);

            WritableFont font4 = new WritableFont(WritableFont.ARIAL, 9,
                    WritableFont.BOLD, false,
                    jxl.format.UnderlineStyle.NO_UNDERLINE,
                    jxl.format.Colour.BLUE);

            WritableCellFormat format = new WritableCellFormat(font);

            format.setAlignment(jxl.format.Alignment.CENTRE);
            format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);

            WritableCellFormat format2 = new WritableCellFormat(font2);

            format2.setAlignment(jxl.format.Alignment.LEFT);
            format2.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
            format2.setWrap(true);

            WritableCellFormat format21 = new WritableCellFormat(font2);
            format21.setAlignment(jxl.format.Alignment.RIGHT);

            WritableCellFormat format3 = new WritableCellFormat(font3);
            format3.setBorder(jxl.format.Border.ALL,
                    jxl.format.BorderLineStyle.THIN);

            WritableCellFormat format31 = new WritableCellFormat(font3);
            format31.setBorder(jxl.format.Border.ALL,
                    jxl.format.BorderLineStyle.THIN);
            format31.setAlignment(jxl.format.Alignment.RIGHT);

            WritableCellFormat format4 = new WritableCellFormat(font4);
            format4.setBorder(jxl.format.Border.ALL,
                    jxl.format.BorderLineStyle.THIN);

            WritableCellFormat format41 = new WritableCellFormat(font4);
            format41.setBorder(jxl.format.Border.ALL,
                    jxl.format.BorderLineStyle.THIN);
            format41.setAlignment(jxl.format.Alignment.CENTRE);
            format41.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);

            int i = 0, j = 0, i1 = 1;

            // 完成标题
            ws.addCell(new Label(0, i, "物流发货信息", format));

//            setWS(ws, i, 800, true);

            ws.setColumnView(0, 5);
            ws.setColumnView(1, 40);
            ws.setColumnView(2, 10);
            ws.setColumnView(3, 10);
            ws.setColumnView(4, 10);
            ws.setColumnView(5, 10);
            ws.setColumnView(6, 10);
            ws.setColumnView(7, 10);
            ws.setColumnView(8, 10);
            ws.setColumnView(9, 10);

//            第三行
            i++;
            ws.addCell(new Label(0, i, bean.getCustomerName() + " 公司", format2));
            setWS(ws, i, 300, true);
//
//            i++;
//            // 第4行
//            ws.addCell(new Label(0, i, content, format2));
//            setWS(ws, i, 1000, true);
//
//            // 第5行
//            i++;
//            ws.addCell(new Label(0, i, content2, format2));
//            setWS(ws, i, 300, true);
//            // 第6行
//            i++;
//            ws.addCell(new Label(0, i, content3, format2));
//            setWS(ws, i, 300, true);
//            // 第7行
//            i++;
//            ws.addCell(new Label(0, i, content4, format2));
//            setWS(ws, i, 300, true);
//            // 第8行
//            i++;
//            ws.addCell(new Label(0, i, "单位：元", format21));
//            setWS(ws, i, 300, true);
//
//            i++;
//            // 正文表格
//            ws.addCell(new Label(0, i, "序号", format3));
//
//            setWS(ws, i, 300, false);

            ws.addCell(new Label(1, i, "产品名称", format3));

            ws.addCell(new Label(2, i, "数量", format3));

            ws.addCell(new Label(3, i, "银行订单好", format3));

            ws.addCell(new Label(4, i, "回款数量", format3));

            ws.addCell(new Label(5, i, "回款金额", format3));

            ws.addCell(new Label(6, i, "退货数量", format3));
            ws.addCell(new Label(7, i, "退货金额", format3));

            ws.addCell(new Label(8, i, "应收数量", format3));
            ws.addCell(new Label(9, i, "应收金额", format3));

            int allAmount = 0, amount = 0, hasBack = 0, noPayAmount = 0, hadAmount = 0;

            double allMoney = 0.0d, money = 0.0d, backMoney = 0.0d, noPayMoney = 0.0d, hadMoney = 0.0d;

            i++;

//            for (FeedBackDetailBean each : mergeList)
//            {
//                ws.addCell(new Label(j++, i, String.valueOf(i1++), format3));
//                setWS(ws, i, 300, false);
//                ws.addCell(new Label(j++, i, each.getProductName(), format3));
//                ws.addCell(new Label(j++, i, String.valueOf(each.getAmount()
//                        + each.getHasBack()), format31));
//                ws.addCell(new Label(j++, i, String.valueOf(MathTools.formatNum2(each.getMoney()
//                        + each.getBackMoney())), format31));
//
//                ws.addCell(new Label(j++, i, String.valueOf(each.getAmount() - each.getNoPayAmount()),
//                        format31));
//                ws.addCell(new Label(j++, i,
//                        String.valueOf(MathTools.formatNum2(each.getMoney() - each.getNoPayMoneys())), format31));
//
//                ws.addCell(new Label(j++, i, String.valueOf(each.getHasBack()),
//                        format31));
//                ws.addCell(new Label(j++, i,
//                        String.valueOf(MathTools.formatNum2(each.getBackMoney())), format31));
//                ws.addCell(new Label(j++, i, String.valueOf(each.getNoPayAmount()),
//                        format31));
//                ws.addCell(new Label(j++, i, String.valueOf(MathTools.formatNum2(each.getNoPayMoneys())),
//                        format31));
//
//                allAmount += each.getAmount() + each.getHasBack();
//                amount += each.getAmount();
//                hasBack += each.getHasBack();
//                noPayAmount += each.getNoPayAmount();
//                hadAmount += each.getAmount() - each.getNoPayAmount();
//
//                allMoney += each.getMoney() + each.getBackMoney();
//                money += each.getMoney();
//                backMoney += each.getBackMoney();
//                noPayMoney += each.getNoPayMoneys();
//                hadMoney += each.getMoney() - each.getNoPayMoneys();
//
//                j = 0;
//                i++;
//            }

            // 第i + 1 行
            ws.addCell(new Label(0, i, "合计:", format31));
            //setWS(ws, i, 300, false);

            ws.mergeCells(0, i, 1, i);

            ws.addCell(new Label(2, i, String.valueOf(allAmount), format31));
            ws.addCell(new Label(3, i, String.valueOf(MathTools.formatNum2(allMoney)), format31));
            ws.addCell(new Label(4, i, String.valueOf(hadAmount), format31));
            ws.addCell(new Label(5, i, String.valueOf(MathTools.formatNum2(hadMoney)), format31));
            ws.addCell(new Label(6, i, String.valueOf(hasBack), format31));
            ws.addCell(new Label(7, i, String.valueOf(MathTools.formatNum2(backMoney)), format31));
            ws.addCell(new Label(8, i, String.valueOf(noPayAmount), format31));
            ws.addCell(new Label(9, i, String.valueOf(MathTools.formatNum2(noPayMoney)), format31));

            i++;

//            ws.addCell(new Label(0, i, TimeTools.changeFormat(
//                    TimeTools.changeTimeToDate(bean.getStatsStar()),
//                    "yyyy-MM-dd", "yyyy年MM月dd日") + "至"
//                    + TimeTools.changeFormat(
//                    TimeTools.changeTimeToDate(bean.getStatsEnd()),
//                    "yyyy-MM-dd", "yyyy年MM月dd日") + " 应收合计：", format3));

            ws.mergeCells(0, i, 2, i);
            //setWS(ws, i, 300, false);

            ws.addCell(new Label(3, i, String.valueOf(MathTools.formatNum2(noPayMoney)), format31));

            ws.mergeCells(3, i, 9, i);

            i++;

            ws.addCell(new Label(0, i, "预收款余额：", format3));

            ws.mergeCells(0, i, 2, i);
//            setWS(ws, i, 300, false);

//            ws.addCell(new Label(3, i, String.valueOf(MathTools.formatNum2(getPreMoney(bean
//                    .getCustomerId()))), format31));

            ws.mergeCells(3, i, 9, i);

//            i++;
//            ws.addCell(new Label(0, i, content5, format2));
//            setWS(ws, i, 300, true);
//
//            i++;
//            ws.addCell(new Label(0, i, content6, format2));
//            setWS(ws, i, 300, true);
//            i++;
//
//            ws.addCell(new Label(0, i, "永银文化创意产业发展有限责任公司", format21));
//            setWS(ws, i, 300, true);
//
//            i++;
//            ws.addCell(new Label(0, i, TimeTools.changeFormat(
//                    TimeTools.changeTimeToDate(TimeTools.now()), "yyyy-MM-dd",
//                    "yyyy年MM月dd日"), format21));
//            setWS(ws, i, 300, true);
//
//            i++;
//            ws.addCell(new Label(0, i, "结论:", format4));
//            setWS(ws, i, 300, true);
//
//            i++;
//            ws.addCell(new Label(0, i, "1. 信息证明无误。", format4));
//            setWS(ws, i, 300, false);
//            ws.mergeCells(0, i, 2, i);
//            ws.addCell(new Label(3, i, "2．信息不符，请列明不符项目及具体内容。", format4));
//
//            ws.mergeCells(3, i, 9, i);
//
//            i++;
//            ws.addCell(new Label(0, i, "", format4));
//            setWS(ws, i, 1600, false);
//            ws.mergeCells(0, i, 2, i);
//
//            ws.addCell(new Label(3, i, "", format4));
//
//            ws.mergeCells(3, i, 9, i);
//
//            i++;
//            ws.addCell(new Label(0, i, "（ 公司盖章）", format41));
//
//            ws.mergeCells(0, i, 2, i);
//            setWS(ws, i, 800, false);
//
//            ws.addCell(new Label(3, i, "（ 公司盖章）", format41));
//
//            ws.mergeCells(3, i, 9, i);
//
//            i++;
//            // 空行
//
//            i++;
//            ws.addCell(new Label(0, i, "年   月  日 ", format41));
//
//            ws.mergeCells(0, i, 2, i);
//            setWS(ws, i, 500, false);
//
//            ws.addCell(new Label(3, i, "年   月  日 ", format41));
//
//            ws.mergeCells(3, i, 9, i);
//
//            i++;
//            // 空行
//            i++;
//            ws.addCell(new Label(0, i, "经办人 ", format41));
//
//            ws.mergeCells(0, i, 2, i);
//            setWS(ws, i, 500, false);

            ws.addCell(new Label(3, i, "经办人 ", format41));

            ws.mergeCells(3, i, 9, i);
        }
        catch (Throwable e)
        {
//            _logger.error(e, e);
            e.printStackTrace();
        }
        finally
        {
            if (wwb != null)
            {
                try
                {
                    wwb.write();
                    wwb.close();
                }
                catch (Exception e1)
                {
                }
            }
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException e1)
                {
                }
            }
        }
    }

    private void setWS(WritableSheet ws, int i, int rowHeight, boolean mergeCell)
            throws WriteException, RowsExceededException
    {
        if (mergeCell) ws.mergeCells(0, i, 9, i);

        ws.setRowView(i, rowHeight);
    }

    /**
	 * @return the preConsignDAO
	 */
	public PreConsignDAO getPreConsignDAO()
	{
		return preConsignDAO;
	}

	/**
	 * @param preConsignDAO the preConsignDAO to set
	 */
	public void setPreConsignDAO(PreConsignDAO preConsignDAO)
	{
		this.preConsignDAO = preConsignDAO;
	}

	/**
	 * @return the packageDAO
	 */
	public PackageDAO getPackageDAO()
	{
		return packageDAO;
	}

	/**
	 * @param packageDAO the packageDAO to set
	 */
	public void setPackageDAO(PackageDAO packageDAO)
	{
		this.packageDAO = packageDAO;
	}

	/**
	 * @return the packageItemDAO
	 */
	public PackageItemDAO getPackageItemDAO()
	{
		return packageItemDAO;
	}

	/**
	 * @param packageItemDAO the packageItemDAO to set
	 */
	public void setPackageItemDAO(PackageItemDAO packageItemDAO)
	{
		this.packageItemDAO = packageItemDAO;
	}

	/**
	 * @return the outDAO
	 */
	public OutDAO getOutDAO()
	{
		return outDAO;
	}

	/**
	 * @param outDAO the outDAO to set
	 */
	public void setOutDAO(OutDAO outDAO)
	{
		this.outDAO = outDAO;
	}

	/**
	 * @return the baseDAO
	 */
	public BaseDAO getBaseDAO()
	{
		return baseDAO;
	}

	/**
	 * @param baseDAO the baseDAO to set
	 */
	public void setBaseDAO(BaseDAO baseDAO)
	{
		this.baseDAO = baseDAO;
	}

	/**
	 * @return the distributionDAO
	 */
	public DistributionDAO getDistributionDAO()
	{
		return distributionDAO;
	}

	/**
	 * @param distributionDAO the distributionDAO to set
	 */
	public void setDistributionDAO(DistributionDAO distributionDAO)
	{
		this.distributionDAO = distributionDAO;
	}

	/**
	 * @return the commonDAO
	 */
	public CommonDAO getCommonDAO()
	{
		return commonDAO;
	}

	/**
	 * @param commonDAO the commonDAO to set
	 */
	public void setCommonDAO(CommonDAO commonDAO)
	{
		this.commonDAO = commonDAO;
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

    public CommonMailManager getCommonMailManager() {
        return commonMailManager;
    }

    public void setCommonMailManager(CommonMailManager commonMailManager) {
        this.commonMailManager = commonMailManager;
    }
}