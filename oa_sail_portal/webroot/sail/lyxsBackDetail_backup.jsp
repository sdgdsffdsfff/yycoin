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
<script language="JavaScript" src="../sail_js/accessoryInStorage.js.js"></script>
<script language="javascript">
<%@include file="../sail_js/out501.jsp"%>

var duesMap = {};
var duesTypeMap = {};
<c:forEach items="${dutyList}" var="item">
duesMap['${item.id}'] = '${item.dues}';
duesTypeMap['${item.id}'] = '${item.mtype}';
</c:forEach>
/**
 * 查询库存
 */
function opens(obj)
{
	var ret = checkCurrentUser();

	if (!ret)
	{
		window.parent.location.reload();

		return false;
	}

	var os = obj.parentNode.parentNode;
//    alert(os.innerHTML);

//	var location = os.cells[0].childNodes[0].value;
    var location = document.getElementById("unLocationId").value;
//    alert(location);
//    alert(os.cells[0].innerHTML);
//    alert(os.cells[0].childNodes[0].innerHTML);

	if (location == '' || typeof location === 'undefined')
    {
        alert('请选择仓库');
        
        return false;
    }
    
    //if ($$('dutyId') == '')
    //{
        //alert('请选择纳税实体');
        
        //return false;
    //}
    
    var mtype = "";
    oo = obj;
    // 配件
    window.common.modal('../depot/storage.do?method=rptQueryStorageRelationInDepot&sailLocation=${g_staffer.industryId}&load=1&depotId='
	                    + location + '&code=' + obj.productcode + '&mtype=' + mtype 
	                    + '&init=1');
}

function load()
{	
    blackForbid();
    
    titleChange();
    
    loadForm();
    
     //load show
    //loadShow();
    
    //loadForm();
    
    managerChange();


}

function changePrice()
{
    var ssList = document.getElementsByName('price');
    
    for (var i = 0; i < ssList.length; i++)
    {
        if (ssList[i].value != '')
        {
           ccs(ssList[i]);
           total();
        }
    }
}

function blackForbid()
{
	
	var black = "${black}";

	if (black > '')
	{
		alert(black);

		document.location.href = '../admin/welcome.jsp';
		return false;
	}

	return true;
}

</script>
</head>
<body class="body_class" onload="load()">
<form name="outForm" method="post" action="../sail/out.do?method=addOut">
<input type=hidden name="update" value="0" />
<input type=hidden name="nameList" /> 
<input type=hidden name="idsList" /> 
<input type=hidden name="amontList" />

<input type=hidden name="totalList" /> 
<input type=hidden name="totalss" /> 
<input type=hidden name="customerId" /> 
<input type=hidden name="type" value='0' /> 
<input type=hidden name="saves" value="" />
<input type=hidden name="desList" value="" />
<input type=hidden name="showCostList" value="" />
<input type=hidden name="otherList" value="" />
<input type=hidden name="depotList" value="" />
<input type=hidden name="mtypeList" value="" />
<input type=hidden name="oldGoodsList" value="" />
<input type=hidden name="taxList" value="" />
<input type=hidden name="taxrateList" value="" />
<input type=hidden name="inputRateList" value="" />

<input type=hidden name="customercreditlevel" value="" />
<input type=hidden name="id" value="" />
<input type=hidden name="priceList"> 
<input type=hidden name="inputPriceList">
<input type=hidden name="mtype" value="" />
<input type=hidden name="hasProm" value="${hasProm}" />
<input type=hidden name="step" value="1" />

<input type=hidden name="locationShadow" value="">
<!-- 批量开单 -->
<input type=hidden name="oprType" value="0">

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
		<td colspan='2' align='center'>
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
                                                      style="width: 100%; cursor: pointer"
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
                            <select name="productName" class="select_class" style="width: 100%">
                                <option value="">--</option>
                                <c:forEach items='${bean.baseList}' var="item">
                                    <option value="${item.id}">${item.productName}</option>
                                </c:forEach>
                            </select>
                        </td>


                        <td align="center">
                            <input type="text" style="width: 100%" maxlength="6" onkeyup="cc(this)" name="amount">
                        </td>

                        <td>
                            <select name="locationIds" class="select_class" onchange="clearsItem(this)" style="width: 100%">
                                <option value="">--</option>
                                <c:forEach items='${bean.baseList}' var="item">
                                    <option value="${item.id}">${item.productName}</option>
                                </c:forEach>
                            </select>
                        </td>


						<td align="center"></td>
					</tr>

					<tr class="content2">
                        <td><input type=button value="按配件入库"  class="button_class" onclick="clears()"></td>
						<td><input type=button value="清空"  class="button_class" onclick="clears()"></td>
					</tr>
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
			value="确认退库" onClick="save()" />&nbsp;&nbsp;
			</div>
		</td>
		<td width="0%"></td>
	</tr>

</table>
</form>
</body>
</html>

