<%@ page contentType="text/html;charset=UTF-8" language="java"
	errorPage="../common/error.jsp"%>
<%@include file="../common/common.jsp"%>

<html>
<head>
<p:link title="填写销售单(now)" />
<script language="JavaScript" src="../js/common.js"></script>
<script language="JavaScript" src="../js/math.js"></script>
<script language="JavaScript" src="../js/public.js"></script>
<script language="JavaScript" src="../js/cnchina.js"></script>
<script language="JavaScript" src="../js/JCheck.js"></script>
<script language="JavaScript" src="../js/compatible.js"></script>
<script language="JavaScript" src="../js/jquery/jquery.js"></script>
<script language="JavaScript" src="../js/json.js"></script>
<script language="JavaScript" src="../sail_js/accessoryInStorage.js"></script>
<script language="JavaScript" src="../sail_js/localforage.min.js"></script>
<script language="javascript">
<%--<%@include file="../sail_js/out501.jsp"%>--%>
    var productList = [];
    <c:forEach items="${bean.baseList}" var="item">
    productList.push("${item.productId}")
    </c:forEach>
    console.log(productList);

    function load()
    {
        loadForm();
    }

//    function accessoryInStorage(){
//        window.open('../sail/out.do?method=accessoryInStorage&productId=product1&outId=' + getRadioValue("fullId"));
//    }

    function confirmBack(){
        var result = {};
//        var len=productList.length;
//        for(var i=0; i<len; i++) {
//            var value = productList[i];
//            console.log("product:"+value);
//            localforage.getItem(value, function(err, value) {
//                // Run this code once the value has been
//                // loaded from the offline store.
//                console.log(value);
//                $O('productList').value = value;
//            });
//        }

        localforage.iterate(function(value, key) {
            // Resulting key/value pair -- this callback
            // will be executed for every item in the
            // database.
            console.log([key, value]);
            result[key] = value
        }, function() {
            console.log('Iteration has completed:'+JSON.stringify(result));
            $O('accessoryList').value = JSON.stringify(result);
            console.log('productList:'+JSON.stringify($('#backForm').serializeArray()));
            $O('productList').value = JSON.stringify($('#backForm').serializeArray());
//            $O('productList').value = JSON.stringify(result);
//            $O('accessoryList').value = JSON.stringify($('#backForm').serializeArray());
            backForm.submit();
        });

//        backForm.submit();
    }

</script>
</head>
<body class="body_class" onload="load()">
<form name="backForm" id="backForm" method="post" action="../sail/out.do?method=submitOut2">
<input type=hidden name="productList" />
<input type=hidden name="accessoryList" />
<input type=hidden name="outId" value="${bean.fullId}"/>

<p:navigation
	height="22">
	<td width="550" class="navigation">入库 &gt;&gt; 领样销售退库</td>
				<td width="85"></td>
</p:navigation> <br>

<table width="95%" border="0" cellpadding="0" cellspacing="0"	align="center">

	<tr>
		<td background="../images/dot_line.gif" colspan='2'></td>
	</tr>

	<tr>
		<td height="10" colspan='2'></td>
	</tr>

	<tr>
		<td height="10" colspan='2'></td>
        <table width="100%" border="0" cellpadding="0" cellspacing="0"
               class="border">
            <tr>
                <td>
                    <table width="100%" border="0" cellspacing='1'>
                        <tr class="content2">
                            <td width="15%" align="right">商品名：</td>
                            <td width="35%">数量：</td>
                        </tr>

                        <c:forEach items="${bean.baseList}" var="item" varStatus="vs">
                            <tr class='${vs.index % 2 == 0 ? "content1" : "content2"}'>
                                <td align="center">
                                    <input type="text" name="productName"
                                           readonly="readonly"
                                           style="width: 100%"
                                           value="${item.productName}" />
                                </td>

                                <td align="center">
                                    <input type="text" readonly="readonly" style="width: 100%"  value="${item.amount}"
                                           maxlength="6" name="amount">
                                </td>

                            </tr>
                        </c:forEach>


                    </table>
                </td>
            </tr>
        </table>
	</tr>


	<tr>
		<td background="../images/dot_line.gif" colspan='2'></td>
	</tr>

	<tr>
		<td height="10" colspan='2'></td>
	</tr>

	<tr>
		<td colspan='2' align='center'>
		<table width="100%" border="0" cellpadding="0" cellspacing="0"
			class="border">
			<tr>
				<td>
				<table width="100%" border="0" cellspacing='1' id="tables">
					<tr align="center" class="content0">
						<td width="27%" align="center">商品名</td>
						<td width="5%" align="center">数量</td>
                        <td width="13%" align="center">入库仓库</td>
                        <td width="5%" align="center">
                            <input type="button" accesskey="A" value="增加" class="button_class" onclick="addTr()">
                        </td>
					</tr>

					<tr class="content1" id="trCopy" style="display: none;">
                        <td>
                            <select name="productName" class="select_class product" style="width: 100%">
                                <option value="">--</option>
                                <c:forEach items='${bean.baseList}' var="item">
                                    <option value="${item.productId}">${item.productName}</option>
                                </c:forEach>
                            </select>
                        </td>

                        <td align="center">
                            <input type="text" style="width: 100%" maxlength="6" name="amount" required="required">
                        </td>

                        <td>
                            <select name="location" class="select_class location" style="width: 100%">
                                <option value="">--</option>
                                <c:forEach items='${locationList}' var="item">
                                    <option value="${item.id}">${item.name}</option>
                                </c:forEach>
                            </select>
                        </td>


						<td align="center"></td>
					</tr>

					<%--<tr class="content2">--%>
                        <%--<td><input type=button value="按配件入库"  class="button_class" onclick="accessoryInStorage()"></td>--%>
						<%--<td><input type=button value="清空"  class="button_class" onclick="clears()"></td>--%>
					<%--</tr>--%>
				</table>
				</td>
			</tr>
		</table>

		</td>
	</tr>

	<tr>
		<td height="10" colspan='2'></td>
	</tr>

	<tr>
		<td background="../images/dot_line.gif" colspan='2'></td>
	</tr>

	<tr>
		<td height="10" colspan='2'></td>
	</tr>

	<tr>
		<td width="100%">
		<div align="right">
			<input type="button" class="button_class"
			value="确认退库" onClick="confirmBack()" />&nbsp;&nbsp;
			</div>
		</td>
		<td width="0%"></td>
	</tr>

</table>
</form>
</body>
</html>

