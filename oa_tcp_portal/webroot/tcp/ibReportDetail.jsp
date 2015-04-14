<%@ page contentType="text/html;charset=UTF-8" language="java"
         errorPage="../../../oa_public_portal/webroot/common/error.jsp"%>
<%@include file="../../../oa_public_portal/webroot/common/common.jsp"%>
<html>
<head>
    <p:link title="中收激励统计"/>
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
        <td width="550" class="navigation">中收激励统计</td>
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
                                    <td width="15%" align="center">客户：</td>
                                    <td align="center">
                                        <input type="text" name="customerName" maxlength="14">
                                    </td>
                                </tr>


                                <tr class="content2">
                                    <td colspan="4" align="right">
                                        <input type="button" id="query_b"
                                               onclick="query()" class="button_class"
                                               value="&nbsp;&nbsp;查 询&nbsp;&nbsp;">&nbsp;&nbsp;
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
                                    <td align="center" onclick="tableSort(this)" class="td_class">客户名</td>
                                    <td align="center" onclick="tableSort(this)" class="td_class">订单号</td>
                                    <td align="center" onclick="tableSort(this)" class="td_class">商品名</td>
                                    <td align="center" onclick="tableSort(this)" class="td_class">商品数量</td>
                                    <td align="center" onclick="tableSort(this)" class="td_class">中收金额</td>
                                    <td align="center" onclick="tableSort(this)" class="td_class">激励金额</td>
                                </tr>

                                <c:forEach items="${ibReportItems}" var="item" varStatus="vs">
                                    <tr class='${vs.index % 2 == 0 ? "content1" : "content2"}'>
                                        <td align="center">${item.customerName}</td>
                                        <td align="center">${item.fullId}</td>
                                        <td align="center">${item.productName}</td>
                                        <td align="center">${item.amount}</td>
                                        <td align="center">${item.ibMoney}</td>
                                        <td align="center">${item.motivationMoney}</td>
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
