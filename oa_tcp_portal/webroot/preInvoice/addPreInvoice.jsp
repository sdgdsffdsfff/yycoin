<%@ page contentType="text/html;charset=UTF-8" language="java"
	errorPage="../common/error.jsp"%>
<%@include file="../common/common.jsp"%>
<html>
<head>
<p:link title="预开票" guid="true"/>
<script language="JavaScript" src="../js/common.js"></script>
<script language="JavaScript" src="../js/compatible.js"></script>
<script language="JavaScript" src="../js/math.js"></script>
<script language="JavaScript" src="../js/public.js"></script>
<script language="JavaScript" src="../js/JCheck.js"></script>
<script language="JavaScript" src="../js/json.js"></script>
<script language="JavaScript" src="../tcp_js/rebate.js"></script>
<script language="javascript">

function addBean(opr)
{
    $("input[name='oprType']").val(opr);
    
    if ("0" == opr)
    {
        $O('processer').oncheck = '';
    }
    else
    {
        $O('processer').oncheck = 'notNone';
    }
    
    submit('确定预开票', null, checkPreInvoices);
}

function load()
{
	addPayTr();
}

function selectCus()
{
    window.common.modal('../client/client.do?method=rptQuerySelfClient&stafferId=${user.stafferId}&load=1');
}

function getCustomer(obj)
{
    $O('customerId').value = obj.value;
    $O('customerName').value = obj.pname;
}

</script>
</head>

<body class="body_class" onload="load()">
<form name="formEntry" action="../tcp/preinvoice.do?method=addOrUpdatePreInvoice" method="post">
<input type="hidden" name="oprType" value="0"> 
<input type="hidden" name="addOrUpdate" value="0"> 
<input type="hidden" name="processId" value=""> 
<input type="hidden" name="customerId" value="">
<input type="hidden" name="type" value="22"> 
<input type="hidden" name="stafferId" value="${g_stafferBean.id}"> 
<input type="hidden" name="departmentId" value="${g_stafferBean.principalshipId}"> 
<input type="hidden" name="stype" value="${g_stafferBean.otype}">

<p:navigation height="22">
	<td width="550" class="navigation">新增预开票</td>
	<td width="85"></td>
</p:navigation> <br>

<p:body width="100%">

	<p:title>
		<td class="caption">
		 <strong>预开票申请</strong>
		</td>
	</p:title>

	<p:line flag="0" />

	<p:subBody width="98%">
	
	    <p:class value="com.china.center.oa.finance.bean.PreInvoiceApplyBean" />
	    
		<p:table cells="2">
            <p:pro field="stafferId" value="${g_stafferBean.name}"/>
            <p:pro field="departmentId" value="${g_stafferBean.principalshipName}"/>            
            
            <p:pro field="name" cell="0" innerString="size=60"/>
            
            <p:pro field="invoiceName" cell="0" innerString="size=60"/>
            
            <p:pro field="invoiceType">
                <p:option type="preInvoiceType" empty="true"></p:option>
            </p:pro>
            
            <p:pro field="dutyId" innerString="style='width=80%'">
            	<option value="">--</option>
                <p:option type="dutyList" />
            </p:pro>
            
            <p:pro field="customerName" innerString="readonly='readonly' size=50" cell="0">
                  <input type="button" value="&nbsp;...&nbsp;" name="qout" id="qout"
                    class="button_class" onclick="selectCus()">&nbsp;
            </p:pro>
            
            <p:pro field="total" value="0"/>
            
            <p:pro field="planOutTime"/>

            <p:cell title="发货方式" end="true">
                <input type="radio" name="shipping" value="0" onClick="radio_click(this)">自提&nbsp;&nbsp;
                <input type="radio" name="shipping" value="1" onClick="radio_click(this)">公司&nbsp;&nbsp;
                <input type="radio" name="shipping" value="2" onClick="radio_click(this)">第三方快递&nbsp;&nbsp;
                <input type="radio" name="shipping" value="3" onClick="radio_click(this)">第三方货运&nbsp;&nbsp;
                <input type="radio" name="shipping" value="4" onClick="radio_click(this)">第三方快递+货运&nbsp;&nbsp;
            </p:cell>

            <p:cell title="运输方式" end="true">
                <select name="transport1" quick=true class="select_class" style="width:20%" >
                    <option>-</option>
                    <c:forEach items="${expressList}" var="item">
                        <option value="${item.id}">${item.name}</option>
                    </c:forEach>
                </select>&nbsp;&nbsp;
                <select name="transport2" quick=true class="select_class" style="width:20%" >
                    <option>-</option>
                    <c:forEach items="${freightList}" var="item">
                        <option value="${item.id}">${item.name}</option>
                    </c:forEach>
                </select>
            </p:cell>

            <p:cell title="运费支付方式" end="true">
                <select name="expressPay" quick=true class="select_class" style="width:20%">
                    <p:option type="deliverPay" empty="true"></p:option>
                </select>&nbsp;&nbsp;
                <select name="transportPay" quick=true class="select_class" style="width:20%">
                    <p:option type="deliverPay" empty="true"></p:option>
                </select>
            </p:cell>

            <tr  class="content1">
                <td>送货地址：</td>
                <td>选择地址：
                    <select name="provinceId" quick=true onchange="changes(this)" class="select_class" >
                        <option>-</option>
                        <c:forEach items="${provinceList}" var="province">
                            <option value="${province.id}">${province.name}</option>
                        </c:forEach>
                    </select>&nbsp;&nbsp;
                    <select name="cityId" quick=true class="select_class" >
                        <option>-</option>
                        <c:forEach items="${cityList}" var="city">
                            <option value="${city.id}">${city.name}</option>
                        </c:forEach>
                    </select>&nbsp;&nbsp;
                </td>
            </tr>

            <p:pro field="address" cell="0" innerString="size=60"/>

            <p:pro field="receiver" cell="0" innerString="size=60"/>

            <p:pro field="mobile" cell="0" innerString="size=60"/>
            
            <p:pro field="description" cell="0" innerString="rows=4 cols=55" />
            
        </p:table>
	</p:subBody>
	
    <p:title>
        <td class="caption">
         <strong>提交/审核</strong>
        </td>
    </p:title>

    <p:line flag="0" />
    
    <tr id="sub_main_tr">
        <td colspan='2' align='center'>
        <table width="98%" border="0" cellpadding="0" cellspacing="0"
            class="border">
            <tr>
                <td>
                <table width="100%" border="0" cellspacing='1' id="tables_pay">
                    <tr align="center" class="content0">
                        <td width="15%" align="center">提交到</td>
                        <td align="left">
                        <input type="text" name="processer" readonly="readonly" oncheck="notNone" head="下环处理人"/>&nbsp;
                        <font color=red>*</font>
                        <input type="button" value="&nbsp;...&nbsp;" name="qout" id="qout"
                            class="button_class" onclick="initSelectNext()">&nbsp;&nbsp;
                        </td>
                    </tr>
                </table>
                </td>
            </tr>
        </table>

        </td>
    </tr>
    
    <p:line flag="1" />
    
	<p:button leftWidth="98%" rightWidth="0%">
		<div align="right">
		<input type="button" class="button_class" id="sub_b1"
            value="&nbsp;&nbsp;保存为草稿&nbsp;&nbsp;" onclick="addBean(0)">
		  &nbsp;&nbsp;
		  <input type="button" class="button_class" id="sub_b2"
            value="&nbsp;&nbsp;提 交&nbsp;&nbsp;" onclick="addBean(1)">
        </div>
	</p:button>
	
	<p:message2/>
</p:body>
</form>
</body>
</html>

