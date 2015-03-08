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
    var provinceMap = {};
    <c:forEach items="${cityList}" var="item">
    var cities = provinceMap['${item.parentId}'];
    //        console.log(cities);
    if (typeof cities === "undefined"){
        provinceMap['${item.parentId}'] = []
        provinceMap['${item.parentId}'].push({'id':'${item.id}','name':'${item.name}'})
    } else{
        cities.push({'id':'${item.id}','name':'${item.name}'})
    }
    </c:forEach>
    //    console.log(provinceMap);

    function change_city(obj)
    {
        removeAllItem($O('cityId'));
        setOption($O('cityId'), "", "--");

        if ($$('provinceId') == "")
        {
            return;
        }

        var cityList = provinceMap[$$('provinceId')];
//        console.log(cityList);
        for (var i = 0; i < cityList.length; i++)
        {
            setOption($O('cityId'), cityList[i].id, cityList[i].name);
        }
    }

    function radio_click(obj)
    {
        if (obj.value == '2')
        {
            $O('transport1').disabled = false;
            removeAllItem($O('transport1'));
            setOption($O('transport1'), "", "--");

            <c:forEach items="${expressList}" var="item">
            if ("${item.type}" == 0 || "${item.type}" == 99)
            {
                setOption($O('transport1'), "${item.id}", "${item.name}");
            }
            </c:forEach>

            removeAllItem($O('transport2'));
            setOption($O('transport2'), "", "--");

            $O('transport2').disabled = true;

            var expressPay = $O('expressPay');
            var transportPay = $O('transportPay');

            removeAllItem(expressPay);

            setOption(expressPay, '1', '业务员支付');
            setOption(expressPay, '2', '公司支付');
            setOption(expressPay, '3', '客户支付');

            removeAllItem(transportPay);

            setOption(transportPay, '', '--');
        }
        else if (obj.value == '3')
        {
            $O('transport2').disabled = false;
            removeAllItem($O('transport2'));
            setOption($O('transport2'), "", "--");

            <c:forEach items="${expressList}" var="item">
            if ("${item.type}" == 1 || "${item.type}" == 99)
            {
                setOption($O('transport2'), "${item.id}", "${item.name}");
            }
            </c:forEach>

            removeAllItem($O('transport1'));
            setOption($O('transport1'), "", "--");

            $O('transport1').disabled = true;

            var expressPay = $O('expressPay');
            var transportPay = $O('transportPay');

            removeAllItem(expressPay);

            setOption(expressPay, '', '--');

            removeAllItem(transportPay);

            setOption(transportPay, '1', '业务员支付');
            setOption(transportPay, '2', '公司支付');
            setOption(transportPay, '3', '客户支付');
        }
        else if (obj.value == '4')
        {
            $O('transport1').disabled = false;
            $O('transport2').disabled = false;

            removeAllItem($O('transport1'));
            setOption($O('transport1'), "", "--");

            removeAllItem($O('transport2'));
            setOption($O('transport2'), "", "--");

            <c:forEach items="${expressList}" var="item">

            if ("${item.type}" == 0 || "${item.type}" == 99)
            {
                setOption($O('transport1'), "${item.id}", "${item.name}");
            }
            </c:forEach>

            <c:forEach items="${expressList}" var="item">

            if ("${item.type}" == 1 || "${item.type}" == 99)
            {
                setOption($O('transport2'), "${item.id}", "${item.name}");
            }
            </c:forEach>

            var expressPay = $O('expressPay');
            var transportPay = $O('transportPay');

            removeAllItem(expressPay);

            setOption(expressPay, '1', '业务员支付');
            setOption(expressPay, '2', '公司支付');
            setOption(expressPay, '3', '客户支付');

            removeAllItem(transportPay);

            setOption(transportPay, '1', '业务员支付');
            setOption(transportPay, '2', '公司支付');
            setOption(transportPay, '3', '客户支付');
        }
        else
        {
            $O('transport1').disabled = true;
            $O('transport2').disabled = true;

            removeAllItem($O('transport1'));
            setOption($O('transport1'), "", "--");

            removeAllItem($O('transport2'));
            setOption($O('transport2'), "", "--");

            var expressPay = $O('expressPay');
            var transportPay = $O('transportPay');

            removeAllItem(expressPay);

            setOption(expressPay, '', '--');

            removeAllItem(transportPay);

            setOption(transportPay, '', '--');
        }

    }

//        function addBean()
//        {
//
//            submit('确定提交CK单合并?', null);
//        }


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

                <p:cell title="发货方式" end="true">
                    <input type="radio" name="shipping" value="0" onClick="radio_click(this)">自提&nbsp;&nbsp;
                    <input type="radio" name="shipping" value="1" onClick="radio_click(this)">公司&nbsp;&nbsp;
                    <input type="radio" name="shipping" value="2" onClick="radio_click(this)">第三方快递&nbsp;&nbsp;
                    <input type="radio" name="shipping" value="3" onClick="radio_click(this)">第三方货运&nbsp;&nbsp;
                    <input type="radio" name="shipping" value="4" onClick="radio_click(this)">第三方快递+货运&nbsp;&nbsp;
                </p:cell>

                <p:cell title="运输方式" end="true">
                    <select name="transport1" id="transport1" quick=true class="select_class" style="width:20%" >
                    </select>&nbsp;&nbsp;
                    <select name="transport2" id="transport2" quick=true class="select_class" style="width:20%" >
                    </select>
                </p:cell>

                <p:cell title="运费支付方式" end="true">
                    <select name="expressPay" quick=true class="select_class" style="width:20%">
                        <p:option type="deliverPay" empty="true"></p:option>
                    </select>&nbsp;&nbsp;
                    <select name="transportPay" quick=true class="select_class" style="width:20%">
                        <p:option type="deliverPay" empty="true"></p:option>
                    </select>
                </p:cell>

                <p:cell title="送货地址" end="true">
                    <select name="provinceId" quick=true onchange="change_city(this)" class="select_class" >
                        <option>-</option>
                        <c:forEach items="${provinceList}" var="province">
                            <option value="${province.id}">${province.name}</option>
                        </c:forEach>
                    </select>&nbsp;&nbsp;
                    <select name="cityId" quick=true class="select_class" >
                        <option>-</option>
                    </select>&nbsp;&nbsp;
                </p:cell>

                <p:cell title="地址" end="true">
                    <input type="text" name='address' id ='address' maxlength="12" required="required"/><b style="color:red">*</b></td>
                </p:cell>

                <p:cell title="收货人" end="true">
                    <input type="text" name='receiver' id ='receiver' maxlength="12" required="required"/><b style="color:red">*</b></td>
                </p:cell>

                <p:cell title="电话" end="true">
                    <input type="text" name='phone' id ='phone' maxlength="13" required="required"/><b style="color:red">*</b></td>
                </p:cell>

            </p:table>
        </p:subBody>

        <p:line flag="1" />

        <p:button leftWidth="100%" rightWidth="0%">
            <div align="right"><input type="submit"
                                      class="button_class" id="ok_b" style="cursor: pointer"
                                      value="&nbsp;&nbsp;提交&nbsp;&nbsp;"></div>
        </p:button>

        <p:message />

    </p:body>
</form>

</body>
</html>