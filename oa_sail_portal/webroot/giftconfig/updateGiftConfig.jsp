<%@ page contentType="text/html;charset=UTF-8" language="java"
    errorPage="../common/error.jsp"%>
<%@include file="../common/common.jsp"%>
<html>
<head>
<p:link title="修改赠品配置" />
<script language="JavaScript" src="../js/JCheck.js"></script>
<script language="JavaScript" src="../js/common.js"></script>
<script language="JavaScript" src="../js/public.js"></script>
<script language="JavaScript" src="../js/math.js"></script>
<script language="javascript">
function addBean()
{
    submit('确定修改赠品配置?', null, null);
}

function selectProduct()
{
    window.common.modal('../product/product.do?method=rptQueryProduct&load=1&selectMode=1');
}

function getProduct(oos)
{
    var obj = oos[0];
    
    $O('productName').value = obj.pname;   
    $O('productId').value = obj.value;   
}

function clears()
{
    $O('productId').value = '';
    $O('productName').value = '公共';
}

function load()
{
}

</script>

</head>
<body class="body_class" onload="load()">
<form name="formEntry" action="../sail/giftconfig.do" method="post">
<input type="hidden" name="method" value="updateGiftConfig">
<input type="hidden" name="productId" value="${bean.productId}">
<input type="hidden" name="giftProductId" value="${bean.giftProductId}">
<input type="hidden" name="id" value="${bean.id}">

<p:navigation
    height="22">
    <td width="550" class="navigation"><span style="cursor: pointer;"
        onclick="javascript:history.go(-1)">赠品配置管理</span> &gt;&gt; 修改赠品配置</td>
    <td width="85"></td>
    
</p:navigation> <br>

<p:body width="98%">

    <p:title>
        <td class="caption"><strong>赠品配置基本信息：</strong></td>
    </p:title>

    <p:line flag="0" />

    <p:subBody width="100%">
        <p:class value="com.china.center.oa.product.bean.ProductVSGiftBean" opr="1"/>

        <p:table cells="1">

            <p:cell title="活动描述">
                <input type="text" name="activity" id="activity" value="${bean.activity}">
            </p:cell>

            <p:cell title="适用银行">
                <input type="text" name="bank" id="bank" value="${bean.bank}">
            </p:cell>

            <p:pro field="beginDate" />

            <p:pro field="endDate" />

            <p:pro field="productId" value="${bean.productName}" innerString="size=60">
            </p:pro>

            <p:pro field="sailAmount" value="${bean.sailAmount}" innerString="size=60 oncheck='isMathNumber'" />

            <p:pro field="giftProductId" value="${bean.giftProductName}" innerString="size=60">
            </p:pro>

            <p:pro field="amount" value="${bean.amount}" innerString="size=60 oncheck='isMathNumber'"/>

            <p:pro field="description" cell="0" innerString="rows=3 cols=55" />

        </p:table>
    </p:subBody>

    <p:line flag="1" />

    <p:button leftWidth="100%" rightWidth="0%">
        <div align="right"><input type="button" class="button_class" id="ok_b"
            style="cursor: pointer" value="&nbsp;&nbsp;确 定&nbsp;&nbsp;"
            onclick="addBean()"></div>
    </p:button>

    <p:message2/>
</p:body></form>
</body>
</html>

