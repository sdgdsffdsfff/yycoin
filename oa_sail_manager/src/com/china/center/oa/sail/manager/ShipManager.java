package com.china.center.oa.sail.manager;

import com.center.china.osgi.publics.User;
import com.china.center.common.MYException;
import com.china.center.oa.sail.bean.BranchRelationBean;
import com.china.center.oa.sail.bean.PreConsignBean;
import com.china.center.oa.sail.vo.OutVO;

import java.util.List;

public interface ShipManager
{
//	void createPackage();
	
	boolean addPickup(User user, String packageIds) throws MYException;
	
	boolean deletePackage(User user, String packageIds) throws MYException;

    /**  2015/2/26
     * 撤销“已拣配”或“已打印”状态的CK单
     * @param user
     * @param packageIds
     * @return
     * @throws MYException
     */
    boolean cancelPackage(User user, String packageIds) throws MYException;
	
	boolean updateStatus(User user, String pickupId) throws MYException;

    boolean updatePackagesStatus(User user, String packageIds) throws MYException;
	
	boolean updatePrintStatus(String pickupId, int index_pos) throws MYException;
	
	void createPackage(PreConsignBean pre, OutVO out) throws MYException;

    void sendMailForShipping() throws MYException;

    void saveAllEntityBeans(List<BranchRelationBean> importItemList) throws MYException;

    void autoPickup(int pickupCount, String productName) throws MYException;

    int addPickup(String packageIds) throws MYException;

    // 2015/2/8 后台Job，商品拣配的排序默认按订单日期由远到近的顺序排列
    void sortPackagesJob() throws MYException;
//	
//	void createInsPackage(PreConsignBean pre, String insId) throws MYException;
    //2015/2/25 手工合并CK单
    void mergePackages(String user, String packageIds, String address, String receiver, String phone) throws MYException;
}
