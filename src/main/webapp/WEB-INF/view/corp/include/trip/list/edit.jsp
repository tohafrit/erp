<div class="ui modal">
    <div class="header">
        <fmt:message key="trip.${empty form.id ? 'add' : 'edit'}"/>
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui form">
            <form:hidden path="id"/>
            <div class="field">
                <label>Тип</label>
                <form:select cssClass="ui dropdown std-select list_edit__type-selector" path="type">
                    <c:forEach items="${typeList}" var="type">
                        <form:option value="${type}"><fmt:message key="${type.property}"/></form:option>
                    </c:forEach>
                </form:select>
                <div class="ui compact message error" data-field="type"></div>
            </div>
            <div class="field required">
                <label>Причина</label>
                <form:input path="name"/>
                <div class="ui compact message error" data-field="name"></div>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Сотрудник</label>
                    <form:select cssClass="ui search dropdown label std-select" path="employeeId" >
                        <c:forEach items="${userList}" var="employee">
                            <form:option value="${employee.id}">${employee.lastName} ${employee.firstName} ${employee.middleName}</form:option>
                        </c:forEach>
                    </form:select>
                    <div class="ui compact message error" data-field="employeeId"></div>
                </div>
                <div class="field">
                    <label>Кто отпустил</label>
                    <form:select cssClass="ui search dropdown label std-select" path="chiefId" >
                        <c:forEach items="${userList}" var="chief">
                            <form:option value="${chief.id}">${chief.lastName} ${chief.firstName} ${chief.middleName}</form:option>
                        </c:forEach>
                    </form:select>
                    <div class="ui compact message error" data-field="chiefId"></div>
                </div>
            </div>
            <div class="field required">
                <label>Дата</label>
                <div class="ui calendar">
                    <div class="ui input left icon">
                        <i class="calendar icon"></i>
                        <form:input cssClass="std-date" path="date"/>
                    </div>
                </div>
                <div class="ui compact message error" data-field="date"></div>
            </div>
            <div class="field list_edit__period-div">
                <form:checkbox class="list_edit__period-checkbox" path="period"/><form:label path="period" for="period">Указать период</form:label>
            </div>
            <div class="field required list_edit__date-to">
                <label>Дата окончания</label>
                <div class="ui calendar">
                    <div class="ui input left icon">
                        <i class="calendar icon"></i>
                        <form:input cssClass="std-date" path="dateTo"/>
                    </div>
                </div>
                <div class="ui compact message error" data-field="dateTo"></div>
            </div>
            <div class="two fields">
                <div class="field required">
                    <label>Время с</label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="time icon"></i>
                            <form:input cssClass="std-time" path="timeFrom"/>
                        </div>
                    </div>
                    <div class="ui compact message error" data-field="timeFrom"></div>
                </div>
                <div class="field required">
                    <label>Время по</label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="time icon"></i>
                            <form:input cssClass="std-time" path="timeTo"/>
                        </div>
                    </div>
                    <div class="ui compact message error" data-field="timeTo"></div>
                </div>
            </div>
        </form:form>
    </div>
    <div class="actions">
        <button class="ui small button" type="submit">
            <i class="icon blue save"></i>
            <fmt:message key="label.button.save"/>
        </button>
    </div>
</div>

<script>
    $(() => {
        const $dateTo = $('div.list_edit__date-to');
        const $period = $('input.list_edit__period-checkbox');
        const $typeSelector = $('div.list_edit__type-selector').find('select');
        const $periodDiv = $('div.list_edit__period-div');
        const period = ${form.period};

        $dateTo.toggle(period);
        $period.change(() => $dateTo.toggle($period.is(":checked")));
        $typeSelector.change(() => {
            let value = $typeSelector.val();
            if (value === 'BUSINESS') {
                $dateTo.show();
                $periodDiv.show();
            } else {
                $dateTo.hide();
                $periodDiv.hide();
            }
        });
    });
</script>