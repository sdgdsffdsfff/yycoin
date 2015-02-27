<%@ page contentType="text/html;charset=UTF-8" language="java"
         errorPage="../common/error.jsp"%>
<%@include file="../common/common.jsp"%>
<html>
<head>
    <p:link title="自动捡配批次配置" />
    <script language="JavaScript" src="../js/JCheck.js"></script>
    <script language="JavaScript" src="../js/common.js"></script>
    <script language="JavaScript" src="../js/public.js"></script>
    <script language="JavaScript" src="../js/math.js"></script>
    <script language="JavaScript" src="../product_js/product.js"></script>
    <script language="javascript">

        function addBean()
        {

            submit('确定提交自动捡配批次配置?', null);
        }


        function selectPrincipalship()
        {
            window.common.modal('../admin/pop.do?method=rptQueryPrincipalship&load=1&selectMode=0');
        }

        function getPrincipalship(oos)
        {
            var ids = '';
            var names = '';
            for (var i = 0; i < oos.length; i++)
            {
                if (i == oos.length - 1)
                {
                    ids = ids + oos[i].value ;
                    names = names + oos[i].pname ;
                }
                else
                {
                    ids = ids + oos[i].value + ';';
                    names = names + oos[i].pname + ';' ;
                }
            }

            $O('industryId').value = ids;
            $O('industryName').value = names;

        }

        function load()
        {
            loadForm();

        }

        </script>

</head>
<body class="body_class" onload="load()">
<form name="formEntry" action="../sail/ship.do" method="post">
    <input type="hidden" name="method" value="autoPickup" />
    <input type="hidden" name="productId" value="" />

    <p:navigation height="22">
        <td width="550" class="navigation"><span style="cursor: pointer;"
                                                 onclick="javascript:history.go(-1)">发货管理</span> &gt;&gt; 自动捡配批次配置</td>
        <td width="85"></td>
    </p:navigation> <br>

    <p:body width="100%">

        <p:title>
            <td class="caption"><strong>自动捡配批次配置</strong></td>
        </p:title>

        <p:line flag="0" />

        <p:subBody width="98%">

            <p:table cells="2">

                <%--<p:pro field="productId" cell="0" innerString="readonly=true size=80" >--%>
                    <%--<input type="button" value="&nbsp;...&nbsp;" name="qout" id="qout"--%>
                           <%--class="button_class" onclick="selectProduct()">&nbsp;&nbsp;--%>
                <%--</p:pro>--%>

                <p:cell title="产品名称" end="true">
                    <input type="text" name='productName' id ='productName' maxlength="12" />
                </p:cell>

                <p:cell title="自动捡配批次数量" end="true">
                    <input type="number" name='pickupCount' id ='pickupCount' maxlength="12" />
                </p:cell>

            </p:table>
        </p:subBody>

        <p:line flag="1" />

        <p:button leftWidth="100%" rightWidth="0%">
            <div align="right"><input type="button"
                                      class="button_class" id="ok_b" style="cursor: pointer"
                                      value="&nbsp;&nbsp;提交&nbsp;&nbsp;" onclick="addBean()"></div>
        </p:button>

        <p:message />

    </p:body>
</form>

</body>
</html>