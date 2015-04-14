<%@ page contentType="text/html;charset=UTF-8" language="java"
         errorPage="../common/error.jsp"%>
<%@include file="../common/common.jsp"%>
<html>
<head>
<p:link title="中收激励统计"/>
    <script language="JavaScript" src="../js/common.js"></script>
    <script language="JavaScript" src="../js/JCheck.js"></script>
<script language="javascript">

</script>

</head>
<body class="body_class"  onload="load()">
<form action="../tcp/apply.do" name="adminForm">
<input type="hidden" value="queryIbReport" name="method">

<p:navigation
        height="22">
    <td width="550" class="navigation">中收激励统计</td>
    <td width="85"></td>
</p:navigation> <br>

<table width="98%" border="0" cellpadding="0" cellspacing="0"
       align="center">

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
                            <td align="center" onclick="tableSort(this)" class="td_class">中收金额</td>
                            <td align="center" onclick="tableSort(this)" class="td_class">激励金额</td>
                        </tr>

                        <c:forEach items="${ibReportList}" var="item" varStatus="vs">
                            <tr class='${vs.index % 2 == 0 ? "content1" : "content2"}'>
                                <td align="center">${item.customerName}</td>
                                <td align="center"><a href="../tcp/apply.do?method=ibReportDetail&ibReportId=${vs.id}">${item.ibMoneyTotal}</a></td>
                                <td align="center">${item.motivationMoneyTotal}</td>
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

<tr>
    <td width="100%">
        <div align="right">
            <input type="button" class="button_class"
                    value="&nbsp;导出查询结果&nbsp;" onclick="exports()" />&nbsp;&nbsp;

        </div>
    </td>
    <td width="0%"></td>
</tr>

<tr height="10">
    <td height="10" colspan='2'></td>
</tr>

<p:message2/>
</table>

</form>

</body>
</html>
