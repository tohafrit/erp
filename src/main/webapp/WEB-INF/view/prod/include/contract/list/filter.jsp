<div class="ui modal">
    <div class="header">Фильтр договоров</div>
    <div class="content">
        <form class="ui small form">
            <div class="field">
                <label>Статус</label>
                <select class="ui dropdown std-select search" name="condition">
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                    <option value="true">Активные</option>
                    <option value="false">Архивные</option>
                </select>
            </div>
            <div class="field">
                <label>Тип</label>
                <select class="ui dropdown std-select search" name="typeIdList" multiple="multiple">
                    <c:forEach items="${contractTypeList}" var="contractType">
                        <option value="${contractType.id}">${contractType.value}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="field">
                <label>Номер</label>
                <input type="search" name="number"/>
            </div>
            <div class="field">
                <label>Заказчик</label>
                <input type="search" name="customer"/>
            </div>
            <div class="field">
                <label>Изделие</label>
                <input type="search" name="conditionalName"/>
            </div>
        </form>
    </div>

    <script>
        $(() => {
            $('input.list_filter__field-invoiceNumber').inputmask({
                placeholder: '',
                regex: '[0-9]{0,9}'
            });
        })
    </script>
</div>