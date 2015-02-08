package com.china.center.oa.sail.manager.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.center.china.osgi.config.ConfigLoader;
import com.china.center.oa.publics.manager.CommonMailManager;
import com.china.center.oa.sail.bean.*;
import com.china.center.oa.sail.dao.*;
import com.china.center.oa.sail.vo.BranchRelationVO;
import com.china.center.tools.StringTools;
import jxl.Workbook;
import jxl.format.*;
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
import com.china.center.oa.sail.constanst.OutConstant;
import com.china.center.oa.sail.constanst.ShipConstant;
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

    private final Log _logger = LogFactory.getLog(getClass());
	
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

    private BranchRelationDAO branchRelationDAO = null;

    private OutImportDAO outImportDAO = null;

    private ConsignDAO consignDAO = null;
	
	public ShipManagerImpl()
	{
	}
	
	private void createNewPackage(OutVO outBean,
			List<BaseBean> baseList, DistributionVO distVO, String fullAddress, String location)
	{
        System.out.println("***********ShipManager createNewPackage*************");
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
        int shipping = distVO.getShipping();
        if (shipping == 0){
            //自提：收货人，电话一致，才合并
            //2015/2/3 仓库地址也必须一致
            con.addCondition("PackageBean.locationId", "=", location);
            con.addCondition("PackageBean.receiver", "=", distVO.getReceiver());
            con.addCondition("PackageBean.mobile", "=", distVO.getMobile());
            con.addIntCondition("PackageBean.status", "=", 0);
        } else if (shipping == 2){
            //第三方快递：地址、收货人、电话完全一致，才合并.能不能判断地址后6个字符一致，电话，收货人一致，就合并
            String fullAddress = distVO.getProvinceName()+distVO.getCityName()+distVO.getAddress();
            String temp = fullAddress.trim();

            if (temp.length()>=6){
                con.addCondition("PackageBean.address", "like", "%"+temp.substring(temp.length()-6));
            }else{
                con.addCondition("PackageBean.address", "like", "%"+temp);
            }

            con.addCondition("PackageBean.locationId", "=", location);
            con.addCondition("PackageBean.receiver", "=", distVO.getReceiver());
            con.addCondition("PackageBean.mobile", "=", distVO.getMobile());

            con.addIntCondition("PackageBean.status", "=", 0);
        } else{
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

	}

	/**
	 * 
	 */
	public void createPackage(PreConsignBean pre, OutVO out) throws MYException
	{
        System.out.println("**************ShipManager createPackage************");
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
            System.out.println("**********ShipManager package already exist*******************");
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
				}else if (bean.getStatus() != ShipConstant.SHIP_STATUS_INIT)
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

    @Transactional(rollbackFor = MYException.class)
    @Override
    public int addPickup(String packageIds) throws MYException {
        String [] packages = packageIds.split("~");

        if (null != packages)
        {
            _logger.info(packageIds+"*****addPickup size*************"+packages.length);
            //TODO
            //一个批次里的商品总数量不能大于50，如一张CK单的数量超过50，单独为一个批次
            String pickupId = commonDAO.getSquenceString20("PC");

            int i = 1;


            for (String id : packages)
            {
                // 只能拣配初始态的
                PackageBean bean = packageDAO.find(id);

                if (null == bean)
                {
                    throw new MYException("出库单[%s]不存在", id);
                }else if (bean.getStatus() != ShipConstant.SHIP_STATUS_INIT)
                {
                    throw new MYException("[%s]已被拣配", id);
                }

                bean.setIndex_pos(i++);

                bean.setPickupId(pickupId);

                bean.setStatus(ShipConstant.SHIP_STATUS_PICKUP);

                packageDAO.updateEntityBean(bean);
                _logger.info(id+"*****update package pickupId****"+pickupId);
            }

        }

        return 1;
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
            if (StringTools.isNullOrNone(each.getPickupId())){
                _logger.info("****CK单pickupId不能为空****"+each.getId());
                throw new MYException("CK单[%s]pickupId不能为空", each.getId());
            }
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

    @Deprecated
    @Transactional(rollbackFor = MYException.class)
    public void sendMailForShipping2() throws MYException {
        //To change body of implemented methods use File | Settings | File Templates.
        long now = System.currentTimeMillis();
        System.out.println("**************run schedule****************"+now);

        ConditionParse con = new ConditionParse();
        con.addWhereStr();
        con.addIntCondition("PackageBean.sendMailFlag", "=", 0);
        con.addIntCondition("PackageBean.status", "=", 2);

        List<PackageVO> packageList = packageDAO.queryVOsByCondition(con);
        if (!ListTools.isEmptyOrNull(packageList))
        {
            for (PackageVO vo : packageList){
                System.out.println("************VO****"+vo);
                //First query 分支行对应关系表
                ConditionParse con2 = new ConditionParse();
                con2.addWhereStr();
                con2.addCondition("BranchRelationBean.id", "=", vo.getCustomerId());
                List<BranchRelationVO> relationList = this.branchRelationDAO.queryVOsByCondition(con2);
                if (!ListTools.isEmptyOrNull(relationList)){
                    BranchRelationVO relation = relationList.get(0);
                    System.out.println("**********relation******"+relation);

                    String fileName = getShippingAttachmentPath() + "/" + vo.getCustomerId()
                            + "_" + TimeTools.now("yyyyMMddHHmmss") + ".xls";
                    System.out.println("************fileName****"+fileName);

                    String title = String.format("永银文化%s发货信息", this.getYesterday());
                    String content = "永银文化创意产业发展有限责任公司发货信息，请查看附件，谢谢。";
                    if(relation.getSendMailFlag() == 1){
                        createMailAttachment2(vo,relation.getBranchName(), fileName);

                        // check file either exists
                        File file = new File(fileName);

                        if (!file.exists())
                        {
                            throw new MYException("邮件附件未成功生成");
                        }

                        // send mail contain attachment
                        commonMailManager.sendMail(relation.getSubBranchMail(), title,content, fileName);

                        //Update sendMailFlag to 1
                        PackageBean packBean = packageDAO.find(vo.getId());
                        packBean.setSendMailFlag(1);
                        this.packageDAO.updateEntityBean(packBean);
                    }

                    if(relation.getCopyToBranchFlag() == 1){
                        // 抄送分行
                        commonMailManager.sendMail(relation.getBranchMail(), title,content, fileName);
                    }

                }
            }
        } else {
            System.out.println("**************no Vo found***************");
        }

    }

    @Override
    @Transactional(rollbackFor = MYException.class)
    public void sendMailForShipping() throws MYException {
        //To change body of implemented methods use File | Settings | File Templates.
        String msg =  "**************run sendMailForShipping job****************";
        System.out.println(msg);
        _logger.info(msg);

        ConditionParse con = new ConditionParse();
        con.addWhereStr();
        con.addIntCondition("PackageBean.sendMailFlag", "=", 0);
        con.addIntCondition("PackageBean.status", "=", 2);

        //分支行对应关系：<分行邮件，List<支行邮件>>
        Map<String,Set<String>> branch2SubBranch = new HashMap<String,Set<String>>();
        //分行邮件与名称表:<分行邮件,分行名称>
        Map<String,String> branch2Name = new HashMap<String,String>();
        //分行邮件与合并订单表:<分行邮件,List<订单>>
        Map<String,List<PackageVO>> branch2Packages = new HashMap<String,List<PackageVO>>();
        //发送邮件标志:<分行邮件,flg>
        Map<String,Integer> branch2Flag = new HashMap<String,Integer>();
        //抄送支行标志:<分行邮件,flg>
        Map<String,Integer> branch2Copy = new HashMap<String,Integer>();

        List<PackageVO> packageList = packageDAO.queryVOsByCondition(con);
        if (!ListTools.isEmptyOrNull(packageList))
        {
            _logger.info("****packageList to be sent mail***"+packageList.size());
            for (PackageVO vo : packageList){
                //First query 分支行对应关系表
                ConditionParse con2 = new ConditionParse();
                con2.addWhereStr();
                con2.addCondition("BranchRelationBean.id", "=", vo.getCustomerId());
                List<BranchRelationVO> relationList = this.branchRelationDAO.queryVOsByCondition(con2);
                if (!ListTools.isEmptyOrNull(relationList)){
                    BranchRelationVO relation = relationList.get(0);
                    _logger.info("**********relation******"+relation);
                    String branchMail = relation.getBranchMail();
                    if (!StringTools.isNullOrNone(branchMail)){
                        String subBranchMail = relation.getSubBranchMail();
                        if (branch2SubBranch.containsKey(branchMail)){
                            branch2SubBranch.get(branchMail).add(subBranchMail);
                        } else{
                            Set<String> branchMailSet = new HashSet<String>();
                            branchMailSet.add(subBranchMail);
                            branch2SubBranch.put(branchMail,branchMailSet);
                        }

                        branch2Name.put(branchMail,relation.getBranchName());

                        if (branch2Packages.containsKey(branchMail)){
                            List<PackageVO> voList = branch2Packages.get(branchMail);
                            voList.add(vo);
                        }else{
                            List<PackageVO> voList =  new ArrayList<PackageVO>();
                            voList.add(vo);
                            branch2Packages.put(branchMail,voList);
                        }

                        branch2Flag.put(branchMail,relation.getSendMailFlag());
                        branch2Copy.put(branchMail,relation.getCopyToBranchFlag());
                    }
                }
            }

            //send mail for merged packages
            for (String key : branch2Packages.keySet()) {
                List<PackageVO> packages = branch2Packages.get(key);
                String fileName = getShippingAttachmentPath() + "/" + branch2Name.get(key)
                        + "_" + TimeTools.now("yyyyMMddHHmmss") + ".xls";
                _logger.info("************fileName****"+fileName);

                String title = String.format("永银文化%s发货信息", this.getYesterday());
                String content = "永银文化创意产业发展有限责任公司发货信息，请查看附件，谢谢。";
                if(branch2Flag.get(key) == 1){
                    String branchName = branch2Name.get(key);
                    _logger.info("分行:"+branchName+"***包裹数***:"+branch2Packages.get(key).size());
                    createMailAttachment(branch2Packages.get(key),branchName , fileName);

                    // check file either exists
                    File file = new File(fileName);

                    if (!file.exists())
                    {
                        throw new MYException("邮件附件未成功生成");
                    }

                    // 发送给分行
                    commonMailManager.sendMail(key, title,content, fileName);
                }

                if(branch2Copy.get(key) == 1){
                    // 抄送所有支行
                    for (String branchMail : branch2SubBranch.get(key)){
                        commonMailManager.sendMail(branchMail, title,content, fileName);
                    }
                }

                for (PackageBean vo:branch2Packages.get(key)){
                    //Update sendMailFlag to 1
                    PackageBean packBean = packageDAO.find(vo.getId());
                    packBean.setSendMailFlag(1);
                    this.packageDAO.updateEntityBean(packBean);
                }
            }
        } else {
//            System.out.println("**************no Vo found***************");
            _logger.info("*****no VO found to send mail****");
        }

    }

    private String getYesterday(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String specifiedDay = sdf.format(date);
        return this.getSpecifiedDayBefore(specifiedDay);
    }

    /**
     * 获得指定日期的前一天
     *
     * @param specifiedDay
     * @return
     * @throws Exception
     */
    private String getSpecifiedDayBefore(String specifiedDay) {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day - 1);

        String dayBefore = new SimpleDateFormat("MM月dd日").format(c
                .getTime());
        return dayBefore;
    }

    /**
     * @return the mailAttchmentPath
     */
    public String getShippingAttachmentPath()
    {
        return ConfigLoader.getProperty("shippingAttachmentPath");
    }

    private void createMailAttachment(List<PackageVO> beans, String branchName, String fileName)
    {
        WritableWorkbook wwb = null;

        WritableSheet ws = null;

        OutputStream out = null;

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
            String title = String.format("永银文化%s发货信息", this.getYesterday());

            // 完成标题
            ws.addCell(new Label(1, i, title, format));

            //set column width
            ws.setColumnView(0, 5);
            ws.setColumnView(1, 40);
            ws.setColumnView(2, 40);
            ws.setColumnView(3, 40);
            ws.setColumnView(4, 5);
            ws.setColumnView(5, 30);
            ws.setColumnView(6, 10);
            ws.setColumnView(7, 20);
            ws.setColumnView(8, 10);
            ws.setColumnView(9, 10);

            i++;
            // 正文表格
            ws.addCell(new Label(0, i, "序号", format3));
            ws.addCell(new Label(1, i, "分行名称", format3));
            ws.addCell(new Label(2, i, "支行名称", format3));
            ws.addCell(new Label(3, i, "产品名称", format3));
            ws.addCell(new Label(4, i, "数量", format3));
            ws.addCell(new Label(5, i, "银行订单号", format3));
            ws.addCell(new Label(6, i, "收货人", format3));
            ws.addCell(new Label(7, i, "快递单号", format3));
            ws.addCell(new Label(8, i, "快递公司", format3));
            ws.addCell(new Label(9, i, "发货时间", format3));

            for (PackageVO bean :beans){
                List<PackageItemBean> itemList = packageItemDAO.queryEntityBeansByFK(bean.getId());
                if (!ListTools.isEmptyOrNull(itemList)){
//                    i++;
                    PackageItemBean first = itemList.get(0);
                    first.getOutId();
                    ConditionParse con3 = new ConditionParse();
                    con3.addWhereStr();
                    con3.addCondition("OutImportBean.oano", "=", first.getOutId());
                    List<OutImportBean> importBeans = this.outImportDAO.queryEntityBeansByCondition(con3);
                    String citicNo = "";
                    if (!ListTools.isEmptyOrNull(importBeans)){
                        for (OutImportBean b: importBeans){
                           if (!StringTools.isNullOrNone(b.getCiticNo())){
                               citicNo = b.getCiticNo();
                           }
                        }
                    }

                    //First get transportNo for this package
                    String transportNo = "";
                    for (PackageItemBean each : itemList){
                        //快递单号
                        List<ConsignBean> consignBeans = this.consignDAO.queryConsignByFullId(each.getOutId());
                        if (!ListTools.isEmptyOrNull(consignBeans)){
                            ConsignBean b = consignBeans.get(0);
                            if (!StringTools.isNullOrNone(b.getTransportNo())){
                                transportNo = b.getTransportNo();
                                break;
                            }
                        }
                    }

                    for (PackageItemBean each : itemList)
                    {
                        i++;
                        ws.addCell(new Label(j++, i, String.valueOf(i1++), format3));
                        setWS(ws, i, 300, false);

                        //分行名称
                        ws.addCell(new Label(j++, i, branchName, format3));
                        //支行名称
                        ws.addCell(new Label(j++, i, bean.getCustomerName(), format3));
                        //产品名称
                        ws.addCell(new Label(j++, i, each.getProductName(), format3));
                        //数量
                        ws.addCell(new Label(j++, i, String.valueOf(each.getAmount()), format3));
                        //银行订单号
                        ws.addCell(new Label(j++, i, citicNo, format3));
                        //收货人
                        ws.addCell(new Label(j++, i, bean.getReceiver(), format3));
                        //快递单号
//                        String transportNo = "";
//                        List<ConsignBean> consignBeans = this.consignDAO.queryConsignByFullId(each.getOutId());
//                        if (!ListTools.isEmptyOrNull(consignBeans)){
//                            ConsignBean b = consignBeans.get(0);
//                            if (!StringTools.isNullOrNone(b.getTransportNo())){
//                                transportNo = b.getTransportNo();
//                            }
//                        }
                        ws.addCell(new Label(j++, i, transportNo, format3));

                        //快递公司
                        ws.addCell(new Label(j++, i, bean.getTransportName1(), format3));
                        //发货时间默认为前1天
                        ws.addCell(new Label(j++, i, this.getYesterday(), format3));
//                        List<DistributionVO> distList = distributionDAO.queryEntityVOsByFK(each.getOutId());
//                        if (ListTools.isEmptyOrNull(distList)){
//                            ws.addCell(new Label(j++, i, this.getYesterday(), format3));
//                        } else{
//                            String outboundDate = distList.get(0).getOutboundDate();
//                            if (StringTools.isNullOrNone(outboundDate)){
//                                ws.addCell(new Label(j++, i, this.getYesterday(), format3));
//                            } else{
//                                ws.addCell(new Label(j++, i, distList.get(0).getOutboundDate(), format3));
//                            }
//                        }

                        j = 0;
//                        i++;
                    }
                }
            }


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

    @Deprecated
    private void createMailAttachment2(PackageVO bean, String branchName, String fileName)
    {
        WritableWorkbook wwb = null;

        WritableSheet ws = null;

        OutputStream out = null;

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
            ws.addCell(new Label(0, i, "发货信息", format));

//            setWS(ws, i, 800, true);

            //set column width
            ws.setColumnView(0, 5);
            ws.setColumnView(1, 40);
            ws.setColumnView(2, 40);
            ws.setColumnView(3, 40);
            ws.setColumnView(4, 5);
            ws.setColumnView(5, 30);
            ws.setColumnView(6, 10);
            ws.setColumnView(7, 20);
            ws.setColumnView(8, 10);
            ws.setColumnView(9, 10);

            i++;
            // 正文表格
            ws.addCell(new Label(0, i, "序号", format3));
            ws.addCell(new Label(1, i, "分行名称", format3));
            ws.addCell(new Label(2, i, "支行名称", format3));
            ws.addCell(new Label(3, i, "产品名称", format3));
            ws.addCell(new Label(4, i, "数量", format3));
            ws.addCell(new Label(5, i, "银行订单号", format3));
            ws.addCell(new Label(6, i, "收货人", format3));
            ws.addCell(new Label(7, i, "快递单号", format3));
            ws.addCell(new Label(8, i, "快递公司", format3));
            ws.addCell(new Label(9, i, "发货时间", format3));


            List<PackageItemBean> itemList = packageItemDAO.queryEntityBeansByFK(bean.getId());
            if (!ListTools.isEmptyOrNull(itemList)){
                System.out.println("package itemlist size***********"+itemList.size());

                i++;
                PackageItemBean first = itemList.get(0);
                first.getOutId();
                ConditionParse con3 = new ConditionParse();
                con3.addWhereStr();
                con3.addCondition("OutImportBean.oano", "=", first.getOutId());
                List<OutImportBean> importBeans = this.outImportDAO.queryEntityBeansByCondition(con3);
                String citicNo = "";
                if (!ListTools.isEmptyOrNull(importBeans)){
                    OutImportBean b = importBeans.get(0);
                    citicNo = b.getCiticNo();
                }

                for (PackageItemBean each : itemList)
                {
                    ws.addCell(new Label(j++, i, String.valueOf(i1++), format3));
                    setWS(ws, i, 300, false);

                    //分行名称
                    ws.addCell(new Label(j++, i, branchName, format3));
                    //支行名称
                    ws.addCell(new Label(j++, i, bean.getCustomerName(), format3));
                    //产品名称
                    ws.addCell(new Label(j++, i, each.getProductName(), format3));
                    //数量
                    ws.addCell(new Label(j++, i, String.valueOf(each.getAmount()), format3));
                    //银行订单号
                    ws.addCell(new Label(j++, i, citicNo, format3));
                    //收货人
                    ws.addCell(new Label(j++, i, bean.getReceiver(), format3));
                    //快递单号
                    String transportNo = "";
                    List<ConsignBean> consignBeans = this.consignDAO.queryConsignByFullId(each.getOutId());
                    if (!ListTools.isEmptyOrNull(consignBeans)){
                        ConsignBean b = consignBeans.get(0);
                        if (!StringTools.isNullOrNone(b.getTransportNo())){
                            transportNo = b.getTransportNo();
                        }
                    }
                    ws.addCell(new Label(j++, i, transportNo, format3));
                    //快递公司
                    ws.addCell(new Label(j++, i, bean.getTransportName1(), format3));
                    //发货时间,如没有默认为前1天
                    List<DistributionVO> distList = distributionDAO.queryEntityVOsByFK(each.getOutId());
                    if (ListTools.isEmptyOrNull(distList)){
                        ws.addCell(new Label(j++, i, this.getYesterday(), format3));
                    } else{
                        String outboundDate = distList.get(0).getOutboundDate();
                        if (StringTools.isNullOrNone(outboundDate)){
                            ws.addCell(new Label(j++, i, this.getYesterday(), format3));
                        } else{
                            ws.addCell(new Label(j++, i, distList.get(0).getOutboundDate(), format3));
                        }
                    }

                    j = 0;
                    i++;
                }
            }

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

    @Override
    @Transactional(rollbackFor = MYException.class)
    public void saveAllEntityBeans(List<BranchRelationBean> branchRelationBeans) throws MYException {
        //To change body of implemented methods use File | Settings | File Templates.
        for (BranchRelationBean bean : branchRelationBeans){
            BranchRelationBean beanInDb = this.branchRelationDAO.find(bean.getId());
            if (beanInDb == null){
                this.branchRelationDAO.saveEntityBean(bean);
            } else{
                this.branchRelationDAO.updateEntityBean(bean);
            }
        }
//        this.branchRelationDAO.saveAllEntityBeans(branchRelationBeans);
    }

    @Override
    public void autoPickup(int pickupCount, String productName) throws MYException {
        //To change body of implemented methods use File | Settings | File Templates.
        _logger.info("***autoPickup****"+pickupCount+":"+productName);
        ConditionParse con1 = new ConditionParse();
        con1.addWhereStr();
        con1.addIntCondition("status","=", ShipConstant.SHIP_STATUS_INIT);
        List<PackageBean> packages = this.packageDAO.queryEntityBeansByCondition(con1);

        if (!ListTools.isEmptyOrNull(packages)){
            int realPickupCount = 0;
            _logger.info("****packages size****"+packages.size());

            //紧急的最优先
            StringBuilder sb1 = new StringBuilder();
            for (Iterator<PackageBean> it = packages.iterator();it.hasNext();){
                PackageBean current = it.next();
                if (current.getEmergency() == 1){
                    String ck = current.getId();
                    _logger.info("*****getEmergency***"+ck);
                    it.remove();
                    sb1.append(ck).append("~");
                }
            }
            String emergencyPackages = sb1.toString();
            _logger.info("****packages size remove emergency****"+packages.size());
            if (!StringTools.isNullOrNone(emergencyPackages)){
                realPickupCount += this.addPickup(emergencyPackages);
                if (realPickupCount>= pickupCount){
                    return ;
                }
            }

            //发货方式为“自提”类的CK单拣配在一个批次里
            StringBuilder sb2 = new StringBuilder();
            for (Iterator<PackageBean> it = packages.iterator();it.hasNext();){
                PackageBean current = it.next();
                if (current.getShipping() == 0){
                    String ck = current.getId();
                    _logger.info("*****selfTakePackages***"+ck);
                    it.remove();
                    sb2.append(ck).append("~");
                }
            }
            _logger.info("****packages size remove selfTakePackages****"+packages.size());
            String selfTakePackages = sb2.toString();
            if (!StringTools.isNullOrNone(selfTakePackages)){
                realPickupCount += this.addPickup(selfTakePackages);
                if (realPickupCount>= pickupCount){
                    return ;
                }
            }

            //TODO
            //仓库地点相同的在一个批次里
//            Map<String,StringBuilder> map1 = new HashMap<String,StringBuilder>();
//            for (Iterator<PackageBean> it = packages.iterator();it.hasNext();){
//                PackageBean current = it.next();
//                if (current.getShipping() == 0){
//                    String ck = current.getId();
//                    _logger.info("*****selfTakePackages***"+ck);
//                    it.remove();
//                    sb2.append(ck).append("~");
//                }
//            }
//            _logger.info("****packages size remove selfTakePackages****"+packages.size());
//            String selfTakePackages = sb2.toString();
//            if (!StringTools.isNullOrNone(selfTakePackages)){
//                this.addPickup(selfTakePackages);
//            }

            //同一事业部的CK单在同一批次里
            Map<String,StringBuilder> map2 = new HashMap<String,StringBuilder>();
            for (Iterator<PackageBean> it = packages.iterator();it.hasNext();){
                PackageBean current = it.next();
                String industryName = current.getIndustryName();
                if (map2.containsKey(industryName)){
                    map2.get(industryName).append(current.getId()+"~");
                } else{
                    StringBuilder sb = new StringBuilder();
                    sb.append(current.getId()).append("~");
                    map2.put(industryName,sb);
                }
            }
            for (StringBuilder sb :map2.values()){
                realPickupCount += this.addPickup(sb.toString());
                if (realPickupCount>= pickupCount){
                    return ;
                }
            }

            _logger.info("****autoPickup exit with pickup count:"+realPickupCount);
        }

    }

    @Override
    public void sortPackagesJob() throws MYException {
        _logger.info("**********sortPackagesJob running*************");
        //TODO
        ConditionParse con1 = new ConditionParse();
        con1.addWhereStr();
        con1.addIntCondition("status","=", ShipConstant.SHIP_STATUS_INIT);
        List<PackageBean> packages = this.packageDAO.queryEntityBeansByCondition(con1);

        if (!ListTools.isEmptyOrNull(packages)){
            _logger.info("****sortPackagesJob with packages size****"+packages.size());
        }
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

    public BranchRelationDAO getBranchRelationDAO() {
        return branchRelationDAO;
    }

    public void setBranchRelationDAO(BranchRelationDAO branchRelationDAO) {
        this.branchRelationDAO = branchRelationDAO;
    }

    public OutImportDAO getOutImportDAO() {
        return outImportDAO;
    }

    public void setOutImportDAO(OutImportDAO outImportDAO) {
        this.outImportDAO = outImportDAO;
    }

    public ConsignDAO getConsignDAO() {
        return consignDAO;
    }

    public void setConsignDAO(ConsignDAO consignDAO) {
        this.consignDAO = consignDAO;
    }
}