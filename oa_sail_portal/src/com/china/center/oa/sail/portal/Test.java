package com.china.center.oa.sail.portal;

import com.china.center.common.MYException;
import com.china.center.oa.product.bean.ComposeFeeBean;
import com.china.center.oa.product.bean.ComposeItemBean;
import com.china.center.oa.product.bean.ComposeProductBean;
import com.china.center.oa.product.constant.StorageConstant;
import com.china.center.tools.CommonTools;
import com.china.center.tools.MathTools;
import com.china.center.tools.StringTools;
import com.china.center.tools.TimeTools;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Simon
 * Date: 15-1-3
 * Time: 上午11:15
 * To change this template use File | Settings | File Templates.
 */
public class Test {
    public static void main(String[] args){
        System.out.println(2==Integer.valueOf("2"));
        String str ="16524963:bomProductName=&amount=2&location=&bomProductName=16524967&amount=2&location=10201103130001000167&bomProductName=16725384&amount=2&location=10201103130001000179;16525315:bomProductName=&amount=3&location=&bomProductName=16725448&amount=3&location=99000000000000000001&bomProductName=16725452&amount=3&location=A1201204171505676333:";

        String[] arr1 = str.split(";");
        System.out.println(arr1.length);
        for (String s : arr1){
            System.out.println(s);
            String[] arr2 = s.split(":");
            String productId = arr2[0];
            String[] arr3 = arr2[1].split("&");
            System.out.println("productId:"+productId+":"+arr3.length);
            ComposeProductBean bean = new ComposeProductBean();
            bean.setProductId(productId);

            if (arr3.length>=3){
                List<ComposeItemBean> itemList = new ArrayList<ComposeItemBean>();
                for (int i=3;i<arr3.length;i+=3){
                    System.out.println(arr3[i]);
                    ComposeItemBean each = new ComposeItemBean();
                    for (int j=0;j<3;j++){
                        System.out.println(arr3[i+j]);
                        String value = arr3[i+j].split("=")[1];
                        if (j==0){
                            each.setProductId(value);
                        } else if (j==1){
                            each.setAmount(Integer.valueOf(value));
                        } else if (j==2){
                            each.setDeportId(value);
                        }
                    }
                    itemList.add(each);
                }
                System.out.println("itemList size****"+itemList.size());
                bean.setItemList(itemList);
            }

        }
    }

}
