/**
 * File Name: TcpShareVO.java<br>
 * CopyRight: Copyright by www.center.china<br>
 * Description:<br>
 * CREATER: ZHUACHEN<br>
 * CreateTime: 2011-7-10<br>
 * Grant: open source to everybody
 */
package com.china.center.oa.tcp.vo;


import com.china.center.jdbc.annotation.Entity;
import com.china.center.oa.tcp.bean.TcpIbReportItemBean;


/**
 * TcpShareVO
 * 
 * @author ZHUZHU
 * @version 2015-04-13
 * @see com.china.center.oa.tcp.vo.TcpIbReportItemVO
 * @since 3.0
 */
@Entity(inherit = true)
public class TcpIbReportItemVO extends TcpIbReportItemBean
{

    /**
     * default constructor
     */
    public TcpIbReportItemVO()
    {
    }

}
