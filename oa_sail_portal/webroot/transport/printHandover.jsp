<%@ page contentType="text/html;charset=UTF-8" language="java"
         errorPage="../common/error.jsp"%>
<%@include file="../common/common.jsp"%>
<html>
<head>
    <p:link title="打印货物交接清单" link="false" guid="true" cal="true" dialog="true" />
    <script language="JavaScript" src="../js/common.js"></script>
    <script language="JavaScript" src="../js/JCheck.js"></script>
    <script language="JavaScript" src="../js/public.js"></script>
    <link href="../css/self.css" type=text/css rel=stylesheet>
    <script language="javascript">
        function pagePrint()
        {
            document.getElementById('ptr').style.display = 'none';
            window.print();
            document.getElementById('ptr').style.display = 'block';

            var pickupId = $O('pickupId').value;
            var index_pos = $O('index_pos').value;

            var packageId = $O('packageId').value;
            var subindex_pos = $O('subindex_pos').value;

            var compose = $O('compose').value;

            // 链到打印完毕页面
            $l("../sail/ship.do?method=printHandover&pickupId="+pickupId+"&index_pos="+index_pos +"&packageId=" + packageId + "&subindex_pos=" + subindex_pos + "&compose=" + compose+ "&printMode=0");
        }

        function callBackPrintFun()
        {
            //
        }

    </script>
</head>
<body>
<input type="hidden" name="pickupId" value="${pickupId}">
<input type="hidden" name="index_pos" value="${index_pos}">
<input type="hidden" name="packageId" value="${packageId}">
<input type="hidden" name="subindex_pos" value="${subindex_pos}">
<input type="hidden" name="compose" value="${compose}">
<table width="100%" border="0" cellpadding="0" cellspacing="0" id="na">
    <tr>
        <td height="6" >
            <table width="100%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <td align="center">
                        <font size="6"><b>
                        </b>
                        </font></td>
                </tr>
            </table>
        </td>
    </tr>
</table>

<table width="90%" border="0" cellpadding="0" cellspacing="0"
       align="center">
    <tr>
        <td colspan='2'>
            <table width="100%" border="0" cellpadding="0" cellspacing="0">
                <tr>

                </tr>
                <tr>
                    <td align="center">
                        <table width="100%" border="0" cellspacing="2">
                            <tr>
                                <td style="height: 27px" align="center">
                                    <font size=5>
                                        <b>
                                            货物交接清单
                                        </b>
                                    </font>
                                </td>
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
        <td colspan='2' align='center'>
            <table width="100%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <td>
                        <table width="100%" cellspacing='0' cellpadding="0" >
                            <tr>
                                <td align="left" colspan="4">
                                    配送任务书编号：
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>

                <tr>
                    <td>
                        <table width="100%" cellspacing='0' cellpadding="0"  class="border">
                            <tr class="content2">
                                <td colspan="4"><table class="border1"><tr><td>货物总箱数：</td></tr></table></td>
                            </tr>
                            <tr class="content2">
                                <td colspan="4">
                                    <table class="border1">
                                        <tr>
                                            <td>封条号及清单：<br>
                                                <c:forEach items="${bean.itemList}" var="item" varStatus="vs">
                                                    <p>品名：${item.productName}</p>
                                                    <p>数量：${item.amount}</p>
                                                    <br>
                                                </c:forEach>
                                        </td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>

                            <tr class="content2">
                                <td colspan="2" style="height: 120px"><table class="border1"><tr><td>出货方：<br><p align="center">盖章</p><br><br><br><p>经办员：刘俊杰</p>${year} 年 ${month} 月 ${day} 日</td></tr></table></td>
                                <td colspan="2" style="height: 120px"><table class="border1"><tr><td>承运方：<br><p align="center"></p><br><br><br><p>经办员：</p>&nbsp;&nbsp;&nbsp;&nbsp; 年 &nbsp;&nbsp;&nbsp;&nbsp; 月 &nbsp;&nbsp;&nbsp;&nbsp; 日</td></tr></table></td>
                            </tr>
                            <tr class="content2">
                                <td colspan="2" style="height: 120px"><table class="border1"><tr><td>收货方：<br><p align="center">盖章</p><br><br><p>经办员：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;复核员：</p>&nbsp;&nbsp;&nbsp;&nbsp;年&nbsp;&nbsp;&nbsp;&nbsp;月&nbsp;&nbsp;&nbsp;&nbsp;日</td></tr></table></td>
                                <td colspan="2" style="height: 120px"><table class="border1"><tr><td></td></tr></table></td>
                            </tr>
                        </table>
                    </td>
                </tr>

            </table>

        </td>
    </tr>

    <tr id="ptr">
        <td width="92%">
            <div align="right"><input type="button" name="pr"
                                      class="button_class" onclick="pagePrint()"
                                      value="&nbsp;&nbsp;打 印&nbsp;&nbsp;"></div>
        </td>
    </tr>
</table>
</body>
</html>
