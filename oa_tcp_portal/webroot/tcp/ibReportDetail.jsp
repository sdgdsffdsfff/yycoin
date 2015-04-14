<%@ page contentType="text/html;charset=UTF-8" language="java"
         errorPage="../../../oa_public_portal/webroot/common/error.jsp"%>
<%@include file="../../../oa_public_portal/webroot/common/common.jsp"%>
<html>
<head>
<p:link title="中收激励统计明细"/>
<link href="../js/plugin/dialog/css/dialog.css" type="text/css" rel="stylesheet"/>
<script src="../js/title_div.js"></script>
<script src="../../../oa_public_portal/webroot/js/public.js"></script>
<script src="../../../oa_public_portal/webroot/js/JCheck.js"></script>
<script src="../../../oa_public_portal/webroot/js/common.js"></script>
<script src="../../../oa_public_portal/webroot/js/json.js"></script>
<script src="../js/tableSort.js"></script>
<script src="../js/jquery/jquery.js"></script>
<script src="../js/plugin/dialog/jquery.dialog.js"></script>
<script src="../js/plugin/highlight/jquery.highlight.js"></script>
<script src="../../../oa_public_portal/webroot/js/adapter.js"></script>
<script language="javascript">

</script>

</head>
<body class="body_class" onkeypress="tooltip.bingEsc(event)" onload="load()">
<form action="../tcp/apply.do" name="adminForm">
<input type="hidden" value="queryIbReport" name="method">
<c:set var="fg" value='销售'/>

<p:navigation
        height="22">
    <td width="550" class="navigation">库单管理 &gt;&gt; 查询销售单${queryType}</td>
    <td width="85"></td>
</p:navigation> <br>

<table width="98%" border="0" cellpadding="0" cellspacing="0"
       align="center">

<p:title>
    <td class="caption">
        <strong><span id="queryConditionText" style="cursor: pointer;" onclick="showQueryConditionTr()">隐藏查询条件</span></strong>
    </td>
</p:title>

<tr id="queryCondition">
    <td align='center' colspan='2'>
        <table width="100%" border="0" cellpadding="0" cellspacing="0"
               class="border">
            <tr>
                <td>
                    <table width="100%" border="0" cellspacing='1'>
                        <tr class="content1">
                            <td width="15%" align="center">开始时间</td>
                            <td align="center" width="35%"><p:plugin name="outTime" size="20" value="${ppmap.outTime}"/></td>
                            <td width="15%" align="center">结束时间</td>
                            <td align="center"><p:plugin name="outTime1" size="20" value="${ppmap.outTime1}"/>
                            </td>
                        </tr>

                        <tr class="content2">
                            <td width="15%" align="center">发货时间从</td>
                            <td align="center" width="35%"><p:plugin name="changeTime" type="0" size="20" value="${ppmap.changeTime}"/></td>
                            <td width="15%" align="center">到</td>
                            <td align="center"><p:plugin name="changeTime1" type="0" size="20" value="${ppmap.changeTime1}"/>
                            </td>
                        </tr>

                        <tr class="content1">
                            <td width="15%" align="center">回款时间从</td>
                            <td align="center" width="35%"><p:plugin name="redateB" type="0" size="20" value="${ppmap.redateB}"/></td>
                            <td width="15%" align="center">到</td>
                            <td align="center"><p:plugin name="redateE" type="0" size="20" value="${ppmap.redateE}"/>
                            </td>
                        </tr>

                        <tr class="content2">
                            <td width="15%" align="center">销售单状态</td>
                            <td align="center">
                                <select name="status" class="select_class" values="${ppmap.status}">
                                    <option value="">--</option>
                                    <p:option type="outStatus"/>
                                    <option value="99">发货态</option>
                                </select>
                            </td>

                            <c:if test="${queryType == '8'}">
                                <td width="15%" align="center">客户：</td>
                                <td align="center">
                                    <input type="text" name="customerName" maxlength="14" value="${ppmap.customerName}"
                                           onclick="selectCustomer()" style="cursor: pointer;"
                                           readonly="readonly">
                                </td>
                            </c:if>

                            <c:if test="${queryType != '8'}">
                                <td width="15%" align="center">客户：</td>
                                <td align="center"><input type="text" name="customerName" maxlength="14" value="${ppmap.customerName}"></td>
                            </c:if>

                        </tr>

                        <tr class="content1">
                            <td width="15%" align="center">销售类型</td>
                            <td align="center">
                                <select name="outType"
                                        class="select_class" values=${ppmap.outType}>
                                    <option value="">--</option>
                                    <p:option type="outType_out"></p:option>
                                </select>

                            </td>
                            <td width="15%" align="center">销售单号</td>
                            <td align="center"><input type="text" name="id" value="${ppmap.id}"></td>
                        </tr>

                        <tr class="content2">
                            <td width="15%" align="center">是否回款</td>
                            <td align="center" colspan="1"><select name="pay" values="${ppmap.pay}"
                                                                   class="select_class">
                                <option value="">--</option>
                                <option value="1">是</option>
                                <option value="0">否</option>
                                <option value="2">超期</option>
                            </select></td>

                            <td width="15%" align="center">仓库</td>
                            <td align="center">
                                <select name="location"
                                        class="select_class" values=${ppmap.location}>
                                    <option value="">--</option>
                                    <c:forEach items="${depotList}" var="item">
                                        <option value="${item.id}">${item.name}</option>
                                    </c:forEach>
                                </select>
                            </td>
                        </tr>

                        <tr class="content1">
                            <td width="15%" align="center">纳税实体</td>
                            <td align="center">
                                <select name="duty"
                                        class="select_class" values=${ppmap.duty}>
                                    <option value="">--</option>
                                    <c:forEach items='${dutyList}' var="item">
                                        <option value="${item.id}">${item.name}</option>
                                    </c:forEach>
                                </select>
                            </td>
                            <td width="15%" align="center">销售人员</td>
                            <td align="center"><input type="text" name="stafferName" value="${ppmap.stafferName}"></td>
                        </tr>

                        <tr class="content2">
                            <td width="15%" align="center">开票状态</td>
                            <td align="center" colspan="1"><select name="invoiceStatus" values="${ppmap.invoiceStatus}"
                                                                   class="select_class">
                                <option value="">--</option>
                                <option value="0">可开票</option>
                                <option value="1">全部开票</option>
                            </select></td>

                            <td width="15%" align="center">关注状态</td>
                            <td align="center" colspan="1"><select name="vtype" values="${ppmap.vtype}"
                                                                   class="select_class">
                                <p:option type="outVtype" empty="true"></p:option>
                            </select>
                            </td>
                        </tr>

                        <tr class="content2">
                            <td width="15%" align="center">经办人</td>
                            <td align="center"><input type="text" name="operatorName" value="${ppmap.operatorName}"></td>

                            <td width="15%" align="center">事业部</td>
                            <td align="center">
                                <input type="text" name="industryName" value="${ppmap.industryName}" readonly="readonly" onClick="selectPrincipalship()">
                                <input
                                        type="button" value="清空" name="qout" id="qout"
                                        class="button_class" onclick="clears()">&nbsp;&nbsp;
                            </td>
                        </tr>

                        <tr class="content2">
                            <td width="15%" align="center">银行导入</td>
                            <td align="center" colspan="1"><select name="isBank" values="${ppmap.isBank}"
                                                                   class="select_class">
                                <option value="">--</option>
                                <option value="0">是</option>
                                <option value="1">否</option>
                            </select></td>

                            <td width="15%" align="center">产品名称</td>
                            <td align="center"><input type="text" name="product_name"></td>
                        </tr>

                        <tr class="content2">

                            <td colspan="4" align="right"><input type="button" id="query_b"
                                                                 onclick="query()" class="button_class"
                                                                 value="&nbsp;&nbsp;查 询&nbsp;&nbsp;">&nbsp;&nbsp;
                                <input type="button" onclick="res()" class="button_class" value="&nbsp;&nbsp;重 置&nbsp;&nbsp;">
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
    </td>
</tr>

<tr>
    <td valign="top" colspan='2'>
        <table width="100%" border="0" cellpadding="0" cellspacing="0">
            <!--DWLayoutTable-->
            <tr>
                <td width="784" height="6"></td>
            </tr>
            <tr>
                <td align="center" valign="top">
                    <div align="left">
                        <table width="90%" border="0" cellspacing="2">
                            <tr>
                                <td>
                                    <table width="100%" border="0" cellpadding="0" cellspacing="10">
                                        <tr>
                                            <td width="35">&nbsp;</td>
                                            <td width="6"><img src="../images/dot_r.gif" width="6"
                                                               height="6"></td>
                                            <td class="caption"><strong>浏览${fg}单:</strong>
                                                <c:if test="${queryType == '1'}">
                                                    <font color=blue>[当前您剩余的信用:${credit}]</font>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                        </table>
                </td>
            </tr>
        </table>
    </td>
</tr>


<tr>
    <td background="../images/dot_line.gif" colspan='2'></td>
</tr>

<tr>
    <td height="10" colspan='2'></td>
</tr>

<tr>
    <td align='center' colspan='2'>
        <table width="100%" border="0" cellpadding="0" cellspacing="0"
               class="border">
            <tr>
                <td>
                    <table width="100%" border="0" cellspacing='1' id="mainTable">
                        <tr align="center" class="content0">
                            <td align="center" width="5%" align="center">选择</td>
                            <td align="center" onclick="tableSort(this)" class="td_class">单据编号</td>
                            <td align="center" onclick="tableSort(this)" class="td_class">客户</td>
                            <td align="center" onclick="tableSort(this)" class="td_class">状态</td>
                            <td align="center" onclick="tableSort(this)" class="td_class">紧急标识</td>
                            <td align="center" onclick="tableSort(this)" class="td_class">${fg}类型</td>
                            <td align="center" onclick="tableSort(this)" class="td_class">${fg}时间</td>
                            <c:if test="${queryType == '5' || queryType == '6'}">
                                <td align="center" onclick="tableSort(this)" class="td_class">库管通过</td>
                            </c:if>
                            <c:if test="${queryType != '5' && queryType != '6'}">
                                <td align="center" onclick="tableSort(this)" class="td_class">结算通过</td>
                            </c:if>
                            <td align="center" onclick="tableSort(this)" class="td_class">回款日期</td>
                            <td align="center" onclick="tableSort(this)" class="td_class">付款方式</td>
                            <td align="center" onclick="tableSort(this)" class="td_class">超期(天)</td>
                            <td align="center" onclick="tableSort(this)" class="td_class">金额</td>
                            <td align="center" onclick="tableSort(this)" class="td_class">付款</td>
                            <td align="center" onclick="tableSort(this)" class="td_class">${fg}人</td>
                            <td align="center" onclick="tableSort(this)" class="td_class">仓库</td>
                            <td align="center" onclick="tableSort(this)" class="td_class">发货单</td>
                        </tr>

                        <c:forEach items="${listOut1}" var="item" varStatus="vs">
                            <c:if test="${queryType == '6' && item.status == 3}">
                                <c:set var="pcheck" value="&check=1"></c:set>
                            </c:if>
                            <c:if test="${queryType != '6' || item.status != 3}">
                                <c:set var="pcheck" value=""></c:set>
                            </c:if>
                            <tr class='${vs.index % 2 == 0 ? "content1" : "content2"}'
                                    >
                                <td align="center"><input type="radio" name="fullId"
                                                          temptype="${item.tempType}"
                                                          hasmap="${hasMap[item.fullId]}"
                                ${vs.index == 0 ? "checked=checked" : ""}
                                                          con="${item.consign}"
                                                          pay="${item.reserve3}"
                                                          paytype="${item.pay}"
                                                          outtype="${item.outType}"
                                                          statuss='${item.status}'
                                                          reserve9='${item.reserve9}'
                                                          baddebts='${my:formatNum(item.total - item.hadPay)}'
                                                          value="${item.fullId}"/></td>
                                <td align="center"
                                    onMouseOver="showDiv('${item.fullId}')" onmousemove="tooltip.move()" onmouseout="tooltip.hide()"><a onclick="hrefAndSelect(this)" href="../sail/out.do?method=findOut&radioIndex=${vs.index}&fow=99&outId=${item.fullId}${pcheck}">
                                    ${item.fullId}</a></td>
                                <td align="center" onclick="hrefAndSelect(this)">${item.customerName}</td>
                                <td align="center" onclick="hrefAndSelect(this)">${my:get('outStatus', item.status)}</td>
                                <td align="center" onclick="hrefAndSelect(this)">
                                    <c:if test="${item.emergency == 1}">
                                        紧急订单
                                    </c:if>
                                    <c:if test="${item.emergency == 0}">
                                        非紧急订单
                                    </c:if>
                                </td>
                                <td align="center" onclick="hrefAndSelect(this)">${my:get('outType_out', item.outType)}</td>
                                <td align="center" onclick="hrefAndSelect(this)">${item.outTime}</td>

                                <c:if test="${queryType != '5' && queryType != '6'}">
                                    <td align="center" onclick="hrefAndSelect(this)">${item.managerTime}</td>
                                </c:if>
                                <c:if test="${queryType == '5' || queryType == '6'}">
                                    <td align="center" onclick="hrefAndSelect(this)">${item.changeTime}</td>
                                </c:if>

                                <c:if test="${item.pay == 0}">
                                    <td align="center" onclick="hrefAndSelect(this)"><font color=red>${item.redate}</font></td>
                                </c:if>
                                <c:if test="${item.pay == 1}">
                                    <td align="center" onclick="hrefAndSelect(this)"><font color=blue>${item.redate}</font></td>
                                </c:if>
                                <c:if test="${item.reserve3 == 1}">
                                    <td align="center" onclick="hrefAndSelect(this)">款到发货(黑名单客户/零售)</td>
                                </c:if>
                                <c:if test="${item.reserve3 == 2}">
                                    <td align="center" onclick="hrefAndSelect(this)">客户信用和业务员信用额度担保</td>
                                </c:if>
                                <c:if test="${item.reserve3 == 3}">
                                    <td align="center" onclick="hrefAndSelect(this)">信用担保</td>
                                </c:if>
                                <td align="center" onclick="hrefAndSelect(this)">${overDayMap[item.fullId]}</td>
                                <td align="center" onclick="hrefAndSelect(this)">${my:formatNum(item.total)}</td>
                                <td align="center" onclick="hrefAndSelect(this)">${my:formatNum(item.hadPay)}</td>
                                <td align="center" onclick="hrefAndSelect(this)">${item.stafferName}</td>
                                <td align="center" onclick="hrefAndSelect(this)">${item.depotName}</td>
                                <c:if test="${queryType == '3'}">
                                    <td align="center" onclick="hrefAndSelect(this)"><a
                                            href="../sail/transport.do?method=findConsign&fullId=${item.fullId}"
                                            >${my:get("consignStatus", item.consign)}</a></td>
                                </c:if>
                                <c:if test="${queryType != '3'}">
                                    <td align="center" onclick="hrefAndSelect(this)">
                                        <a
                                                href="../sail/transport.do?method=findConsign&forward=2&fullId=${item.fullId}"
                                                >
                                            ${my:get("consignStatus", item.consign)}
                                        </a>
                                    </td>
                                </c:if>
                            </tr>
                        </c:forEach>
                    </table>

                    <p:formTurning form="adminForm" method="queryOut"></p:formTurning>
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

<c:if test="${my:length(listOut1) > 0}">
    <tr>
        <td width="100%">
            <div align="right">

                <c:if test="${queryType != '5' && queryType != '6'
		  && queryType != '8' && queryType != '9' && queryType != '10' && queryType != '11'}">

                    <c:if test="${queryType == '2'}">
                        <input type="button" class="button_class" style="display: none;"
                               value="&nbsp;&nbsp;确认回款&nbsp;&nbsp;" onClick="payOut()">&nbsp;&nbsp;
                        <input type="button" class="button_class"
                               value="&nbsp;&nbsp;紧急处理&nbsp;&nbsp;" onClick="updateEmergency()">&nbsp;&nbsp;
                    </c:if>

                    <input name="bu1"
                           type="button" class="button_class" value="&nbsp;审核通过&nbsp;"
                           onclick="check()" />&nbsp;&nbsp;<input type="button" name="bu2"
                                                                  class="button_class" value="&nbsp;&nbsp;驳 回&nbsp;&nbsp;"
                                                                  onclick="reject()" />&nbsp;&nbsp;
                    <!--
	                <c:if test="${queryType == '4'}">
	                <input type="button" name="bu_pridist"
	                class="button_class" value="&nbsp;&nbsp;打印配送单&nbsp;&nbsp;"
	                onclick="printDist()" />&nbsp;&nbsp;
	                </c:if>-->
                </c:if>

                <c:if test="${queryType == '5'}">
                    <input type="button" class="button_class"
                           value="&nbsp;&nbsp;确认回款&nbsp;&nbsp;" onClick="payOut2()"/>&nbsp;&nbsp;
                    <input type="button" class="button_class"
                           value="&nbsp;&nbsp;强制回款&nbsp;&nbsp;" onClick="fourcePayOut()"/>&nbsp;&nbsp;

                    <input type="button" class="button_class"
                           value="&nbsp;&nbsp;确认坏账&nbsp;&nbsp;" onClick="payOut3()"/>&nbsp;&nbsp;
                    <input type="button" class="button_class"
                           value="&nbsp;&nbsp;坏账取消&nbsp;&nbsp;" onClick="payOut4()"/>&nbsp;&nbsp;

                    <c:if test="${my:auth(user, '1418')}">
                        <input
                                type="button" class="button_class"
                                value="&nbsp;修改发票状态&nbsp;" onclick="updateInvoiceStatus()" />&nbsp;&nbsp;
                    </c:if>

                </c:if>

                <c:if test="${queryType == '6'}">
                    <input type="button" class="button_class"
                           value="&nbsp;&nbsp;总部核对&nbsp;&nbsp;" onClick="centerCheck()"/>&nbsp;&nbsp;
                </c:if>

                <c:if test="${queryType == '9'}">
                    <input type="button" class="button_class"
                           value="&nbsp;&nbsp;领样巡展退库&nbsp;&nbsp;" onClick="outBack()"/>&nbsp;&nbsp;
                    <input type="button" class="button_class"
                           value="&nbsp;&nbsp;领样巡展转销售&nbsp;&nbsp;" onClick="swatchToSail()"/>&nbsp;&nbsp;
                </c:if>

                <c:if test="${queryType == '8'}">
                    <input type="button" class="button_class"
                           value="&nbsp;&nbsp;销售退单&nbsp;&nbsp;" onClick="outBack2()"/>&nbsp;&nbsp;
                    <input type="button" class="button_class"
                           value="&nbsp;&nbsp;申请退款&nbsp;&nbsp;" onClick="applyBackPay()"/>&nbsp;&nbsp;
                    <input type="button" class="button_class"
                           value="&nbsp;&nbsp;空开空退&nbsp;&nbsp;" onClick="outRepaire()"/>&nbsp;&nbsp;
                </c:if>
                <input
                        type="button" class="button_class"
                        value="&nbsp;导出查询结果&nbsp;" onclick="exports()" />&nbsp;&nbsp;

            </div>
        </td>
        <td width="0%"></td>
    </tr>

</c:if>

<tr height="10">
    <td height="10" colspan='2'></td>
</tr>

<p:message2/>
</table>

</form>

</body>
</html>
