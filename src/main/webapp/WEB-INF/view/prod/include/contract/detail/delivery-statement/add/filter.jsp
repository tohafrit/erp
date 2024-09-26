<div class="ui modal">
    <div class="header">Фильтр Изделий</div>
    <div class="content">
        <form class="ui small form">
            <div class="field">
                <label>Изделие</label>
                <input name="productName" type="search"/>
            </div>
            <div class="field">
                <label>Краткая техническая характеристика</label>
                <select class="ui dropdown search label std-select" name="typeIdList" multiple="multiple">
                    <c:forEach items="${productTypeList}" var="productType">
                        <option value="${productType.id}">${productType.value}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="field">
                <label>ТУ изделия</label>
                <input name="decimalNumber" type="search"/>
            </div>
            <div class="field">
                <label>Протокол</label>
                <input name="protocolNumber" type="search"/>
            </div>
        </form>
    </div>
</div>