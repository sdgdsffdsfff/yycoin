<%@ page contentType="text/html;charset=UTF-8" language="java"
	errorPage="../common/error.jsp"%>
<%@include file="../common/common.jsp"%>
<html>
<head>
<p:link title="预开票" guid="true"/>
<script language="JavaScript" src="../js/common.js"></script>
<script language="JavaScript" src="../js/key.js"></script>
<script language="JavaScript" src="../tcp_js/expense.js"></script>
<script language="javascript">
function load()
{

}

</script>
</head>

<body class="body_class" onload="load()">
<form name="formEntry" action="../tcp/expense.do"  method="post">
<input type="hidden" name="oprType" value="0"> 
<input type="hidden" name="stafferId" value="${g_stafferBean.id}"> 
<input type="hidden" name="departmentId" value="${g_stafferBean.principalshipId}"> 

<p:navigation height="22">
	<td width="550" class="navigation">预开票明细</td>
	<td width="85"></td>
</p:navigation> <br>

<p:body width="100%">

	<p:title>
		<td class="caption">
		 <strong>预开票</strong>
		</td>
	</p:title>

	<p:line flag="0" />

	<p:subBody width="98%">
	
	    <p:class value="com.china.center.oa.finance.bean.PreInvoiceApplyBean" opr="2"/>
	    
		<p:table cells="2">
			<p:cell title="处理流程" width="8" end="true">
            ${bean.flowDescription}
            </p:cell>
            
            <p:pro field="id" cell="0"/>
            
            <p:cell title="申请人">
            ${bean.stafferName}
            </p:cell>
            
            <p:cell title="部门">
            ${bean.departmentName}
            </p:cell>
            
            <p:pro field="name" cell="0" innerString="size=60"/>
            
            <p:pro field="invoiceName" cell="0" innerString="size=60"/>
            
            <p:pro field="invoiceType">
                <p:option type="preInvoiceType" empty="true"></p:option>
            </p:pro>
            
            <p:pro field="dutyId" innerString="style='width=80%'">
            	<option value="">--</option>
                <p:option type="dutyList" />
            </p:pro>
            
            <p:pro field="customerName" innerString="readonly='readonly'">
            </p:pro>
            
            <p:pro field="planOutTime"/>
            
            <p:pro field="total" value="${my:formatNum(bean.total / 100.0)}"/>
            
            <p:pro field="invoiceMoney" value="${my:formatNum(bean.invoiceMoney / 100.0)}"/>

            <p:cell title="发货方式" end="true">
                <input type="radio" name="rshipping" value="0" onClick="radio_click(this)">自提&nbsp;&nbsp;
                <input type="radio" name="rshipping" value="1" onClick="radio_click(this)">公司&nbsp;&nbsp;
                <input type="radio" name="rshipping" value="2" onClick="radio_click(this)">第三方快递&nbsp;&nbsp;
                <input type="radio" name="rshipping" value="3" onClick="radio_click(this)">第三方货运&nbsp;&nbsp;
                <input type="radio" name="rshipping" value="4" onClick="radio_click(this)">第三方快递+货运&nbsp;&nbsp;
            </p:cell>

            <p:cell title="运输方式" end="true">
                <select name="transport1" quick=true class="select_class" style="width:20%" values="${pmap['transport1']}">
                </select>&nbsp;&nbsp;
                <select name="transport2" quick=true class="select_class" style="width:20%" values="${pmap['transport2']}">
                </select>
            </p:cell>

            <p:cell title="运费支付方式" end="true">
                <select name="expressPay" quick=true class="select_class" style="width:20%" values="${pmap['expressPay']}">
                    <p:option type="deliverPay" empty="true"></p:option>
                </select>&nbsp;&nbsp;
                <select name="transportPay" quick=true class="select_class" style="width:20%" values="${pmap['transportPay']}">
                    <p:option type="deliverPay" empty="true"></p:option>
                </select>
            </p:cell>

            <tr  class="content1">
                <td>送货地址：</td>
                <td>选择地址：
                    <select name="provinceId" quick=true onchange="changes(this)" values="${pmap['provinceId']}" class="select_class" ></select>&nbsp;&nbsp;
                    <select name="cityId" quick=true onchange="changeArea()" values="${pmap['cityId']}" class="select_class" ></select>&nbsp;&nbsp;
                </td>
            </tr>

            <tr  class="content2">
                <td></td>
                <td>详细地址：
                    <input type="text" name="address" value="${pmap['address']}" size=100 maxlength="300" style="width: 80%;">
                </td>
            </tr>

            <p:cell title="收 货 人" end="true">
                <input type="text" name="receiver" value="${pmap['receiver']}" size=20 maxlength="30">
            </p:cell>

            <tr  class="content2">
                <td>手&nbsp;&nbsp;&nbsp;&nbsp;机：</td>
                <td>
                    <input type="text" name="mobile" value="${pmap['mobile']}"  size=13 maxlength="13"><font color="#FF0000">*</font>
                    &nbsp;&nbsp;固定电话：&nbsp;&nbsp;
                    <input type="text" name="telephone" value="${pmap['telephone']}" size=20 maxlength="30">
                </td>
            </tr>

            <%--<p:pro field="address" cell="0" innerString="size=60"/>--%>

            <%--<p:pro field="receiver" cell="0" innerString="size=20"/>--%>

            <%--<p:pro field="mobile" cell="0" innerString="size=20"/>--%>
            
            <p:pro field="description" cell="0" innerString="rows=4 cols=55" />
            
            <p:cell title="处理人" width="8" end="true">
            ${bean.processer}
            </p:cell>
            
        </p:table>
	</p:subBody>
	
	<p:title>
        <td class="caption">
         <strong>销售单明细</strong>
        </td>
    </p:title>

    <p:line flag="0" />
    
    <tr id="pay_main_tr">
        <td colspan='2' align='center'>
        <table width="98%" border="0" cellpadding="0" cellspacing="0"
            class="border">
            <tr>
                <td>
                <table width="100%" border="0" cellspacing='1' id="tables_pay">
                    <tr align="center" class="content0">
                        <td width="25%" align="center">销售单</td>
                        <td width="15%" align="center">销售单金额</td>
                        <td width="15%" align="center">可开票金额</td>
                        <td width="15%" align="center">开票金额</td>
                    </tr>
                    <c:forEach items="${bean.vsList}" var="item">
                    <tr align="center" class="content1">
                        <td align="center">
                        <a href="../sail/out.do?method=findOut&fow=99&outId=${item.outId}">${item.outId}</a>
                        </td>
                        <td align="center">${my:formatNum(item.money)}</td>
                        <td align="center">${my:formatNum(item.mayInvoiceMoney)}</td>
                        <td align="center">${my:formatNum(item.invoiceMoney)}</td>
                    </tr>
                    </c:forEach>
                </table>
                </td>
            </tr>
        </table>

        </td>
    </tr>
    
    <p:title>
        <td class="caption">
         <strong>流程日志</strong>
        </td>
    </p:title>

    <p:line flag="0" />
    
    <tr id="flowLog">
        <td colspan='2' align='center'>
        <table width="98%" border="0" cellpadding="0" cellspacing="0"
            class="border">
            <tr>
                <td>
                <table width="100%" border="0" cellspacing='1' id="tables">
                    <tr align="center" class="content0">
                        <td width="10%" align="center">审批人</td>
                        <td width="10%" align="center">审批动作</td>
                        <td width="10%" align="center">前状态</td>
                        <td width="10%" align="center">后状态</td>
                        <td width="45%" align="center">意见</td>
                        <td width="15%" align="center">时间</td>
                    </tr>

                    <c:forEach items="${logList}" var="item" varStatus="vs">
                        <tr class='${vs.index % 2 == 0 ? "content1" : "content2"}'>
                            <td align="center">${item.actor}</td>

                            <td  align="center">${item.oprModeName}</td>

                            <td  align="center">${item.preStatusName}</td>

                            <td  align="center">${item.afterStatusName}</td>

                            <td  align="center">${item.description}</td>

                            <td  align="center">${item.logTime}</td>

                        </tr>
                    </c:forEach>
                </table>
                </td>
            </tr>
        </table>

        </td>
    </tr>
    
    <p:line flag="1" />
    
	<p:button leftWidth="98%" rightWidth="0%">
        <div align="right">
        <input type="button" name="pr"
            class="button_class" onclick="pagePrint()"
            value="&nbsp;&nbsp;打 印&nbsp;&nbsp;">&nbsp;&nbsp;
        <input type="button" class="button_class"
            id="ok_b" style="cursor: pointer" value="&nbsp;&nbsp;返 回&nbsp;&nbsp;"
            onclick="javaScript:window.history.go(-1);"></div>
    </p:button>
	
	<p:message2/>
</p:body>
</form>
</body>
</html>

