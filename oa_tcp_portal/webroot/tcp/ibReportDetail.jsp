<%@ page contentType="text/html;charset=UTF-8" language="java"
         errorPage="../common/error.jsp"%>
<%@include file="../common/common.jsp"%>
<html>
<head>
    <p:link title="中收激励统计"/>
    <script language="JavaScript" src="../js/common.js"></script>
    <script language="JavaScript" src="../js/JCheck.js"></script>
    <script language="javascript">
        function exports()
        {
            var ibReportId = '${ibReportId}';
            console.log("ibReportId:"+ibReportId);
            if (window.confirm("确定导出当前明细?"))
                document.location.href = '../tcp/apply.do?method=exportIbDetail&ibReportId='+ibReportId;
        }

    </script>

</head>
<body class="body_class" onload="load()">
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
                                        <td align="center">
                                            <a href="../sail/out.do?method=findOut&fow=99&outId=${item.fullId}">
                                            ${item.fullId}
                                            </a></td>
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

        <tr height="10">
            <td height="10" colspan='2'>
                <div align="right">
                    <input type="button" class="button_class"
                           value="&nbsp;导出查询结果&nbsp;" onclick="exports()" />&nbsp;&nbsp;
                    <input type="button" class="button_class" value="返回" onclick="javascript:history.go(-1)" >
                </div>

            </td>
        </tr>

        <p:message2/>
    </table>

</form>

</body>
</html>
