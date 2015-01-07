<%@ page contentType="text/html;charset=UTF-8" language="java"
	errorPage="../common/error.jsp"%>
<%@include file="../common/common.jsp"%>

<html>
<head>
<p:link title="按配件入库(now)" />
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

    var productId = "${productId}";
    console.log(productId);
    var amount = "${amount}";
    console.log(amount);

    function load()
    {
        loadForm();
    }

    function saveAccessory(){
        //TODO
        var formData = $( "#accessoryForm" ).serialize();
        localforage.setItem(productId, formData, function(err, value) {
            // Do other things once the value has been saved.
            console.log(value);
        });

//        var formData = JSON.parse(JSON.stringify($('#accessoryForm').serializeArray()))
//        console.info(formData);
//        if (formData.length >3){
//            accessoryArray = formData.slice(3);
//            accessoryStr = JSON.stringify(accessoryArray);
//            console.log(accessoryStr);
//            localforage.setItem(productId, accessoryStr, function(err, value) {
//                // Do other things once the value has been saved.
//                console.log(value);
//            });
//        }

    }

</script>
</head>
<body class="body_class" onload="load()">
<form name="accessoryForm" id="accessoryForm" method="post">

<p:navigation
	height="22">
	<td width="550" class="navigation">入库 &gt;&gt; 领样销售退库 &gt;&gt; 按配件入库</td>
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
						<td width="27%" align="center">配件名</td>
						<td width="5%" align="center">数量</td>
                        <td width="13%" align="center">入库仓库</td>
                        <td width="5%" align="center">
                            <input type="button" accesskey="A" value="增加" class="button_class" onclick="addTr2()">
                        </td>
					</tr>

                    <!-- 用来新增时拷贝用-->
					<tr class="content1" id="trCopy" style="display: none;">
                        <td>
                            <select name="bomProductId" class="select_class"  style="width: 100%">
                                <option value="">--</option>
                                <c:forEach items='${bomList}' var="item">
                                    <option value="${item.id}">${item.name}</option>
                                </c:forEach>
                            </select>
                        </td>
						<td>
                            <input type="text" name="amount" value="${amount}">
						</td>

						<td align="center">
                            <select name="location" class="select_class"  style="width: 100%">
                                <option value="">--</option>
                                <c:forEach items='${locationList}' var="item">
                                    <option value="${item.id}">${item.name}</option>
                                </c:forEach>
                            </select>
                        </td>


						<td align="center"></td>
					</tr>
                    <!-- end of trCopy -->

                    <c:forEach items="${bomList}" var="item" varStatus="vs">
                        <tr class="content1">
                            <td>
                                <select name="bomProductId" class="select_class" style="width: 100%">
                                    <option value="">--</option>
                                    <c:forEach items='${bomList}' var="bom">
                                        <c:if test="${bom.name == item.name}">
                                            <option value="${bom.productId}" selected>${bom.name}</option>
                                        </c:if>
                                        <c:if test="${bom.name != item.name}">
                                            <option value="${bom.productId}">${bom.name}</option>
                                        </c:if>
                                    </c:forEach>
                                </select>
                            </td>

                            <td align="center">
                                <input type="text" style="width: 100%" maxlength="6" name="amount" value="${item.amount}" required="required">
                            </td>

                            <td>
                                <select name="location" class="select_class location" style="width: 100%">
                                    <option value="">--</option>
                                    <c:forEach items='${locationList}' var="location">
                                        <c:if test="${location.name == '公共库--南京物流中心'}">
                                            <option value="${location.id}" selected>${location.name}</option>
                                        </c:if>
                                        <c:if test="${location.name != '公共库--南京物流中心'}">
                                            <option value="${location.id}">${location.name}</option>
                                        </c:if>
                                    </c:forEach>
                                </select>
                            </td>

                            <td>
                                <input type="button" value="删除" name="eachDel" class="button_class" onclick="removeTr(this)"> <input type=button value="按配件入库" name=eachInStorage class=button_class onclick="accessoryInStorage(this)">
                            </td>


                            <td align="center"></td>
                        </tr>
                    </c:forEach>
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
			value="保存" onClick="saveAccessory()" />&nbsp;&nbsp;
			</div>
		</td>
		<td width="0%"></td>
	</tr>

</table>
</form>
</body>
</html>

