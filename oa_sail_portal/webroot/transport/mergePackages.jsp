<%@ page contentType="text/html;charset=UTF-8" language="java"
         errorPage="../common/error.jsp"%>
<%@include file="../common/common.jsp"%>
<html>
<head>
    <p:link title="手工合并CK单" />
    <script language="JavaScript" src="../js/JCheck.js"></script>
    <script language="JavaScript" src="../js/common.js"></script>
    <script language="JavaScript" src="../js/public.js"></script>
    <script language="JavaScript" src="../js/math.js"></script>
    <script language="JavaScript" src="../product_js/product.js"></script>
    <script language="javascript">

        function addBean()
        {

            submit('确定提交CK单合并?', null);
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
    <input type="hidden" name="method" value="mergePackages" />

    <p:navigation height="22">
        <td width="550" class="navigation"><span style="cursor: pointer;"
                                                 onclick="javascript:history.go(-1)">发货管理</span> &gt;&gt; 手动合并出库单</td>
        <td width="85"></td>
    </p:navigation> <br>

    <p:body width="100%">

        <p:title>
            <td class="caption"><strong>手动合并出库单</strong></td>
        </p:title>

        <p:line flag="0" />

        <p:subBody width="98%">
            <p:table cells="3">

                <p:cell title="地址" end="true">
                    <input type="text" name='address' id ='address' maxlength="12" />
                </p:cell>

                <p:cell title="收货人" end="true">
                    <input type="text" name='receiver' id ='receiver' maxlength="12" />
                </p:cell>

                <p:cell title="电话" end="true">
                    <input type="text" name='phone' id ='phone' maxlength="12" />
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