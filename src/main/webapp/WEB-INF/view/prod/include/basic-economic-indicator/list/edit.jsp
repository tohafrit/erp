<div class="ui modal list_edit__modal">
    <div class="header">
        ${empty form.id ? 'Добавление показателей' : 'Редактирование показателей'}
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui form">
            <form:hidden path="id"/>
            <form:hidden path="version"/>
            <div class="field required">
                <label>Наименование</label>
                <form:input type="search" path="name"/>
                <div class="ui compact message error" data-field="name"></div>
            </div>
            <div class="field required">
                <label>Документ</label>
                <form:input type="search" path="docName"/>
                <div class="ui compact message error" data-field="docName"></div>
            </div>
            <div class="field required">
                <label>Дата утверждения</label>
                <div class="ui calendar">
                    <div class="ui input left icon">
                        <i class="calendar icon"></i>
                        <form:input type="search" cssClass="std-date" path="approvalDate"/>
                    </div>
                </div>
                <div class="ui compact message error" data-field="approvalDate"></div>
            </div>
            <div class="field required">
                <label>Дополнительная заработная плата, %</label>
                <form:input type="search" path="additionalSalary"/>
                <div class="ui compact message error" data-field="additionalSalary"></div>
            </div>
            <div class="field required">
                <label>Отчисления на соц. страхование, %</label>
                <form:input type="search" path="socialInsurance"/>
                <div class="ui compact message error" data-field="socialInsurance"></div>
            </div>
            <div class="field required">
                <label>Накладные расходы, %</label>
                <form:input type="search" path="overheadCosts"/>
                <div class="ui compact message error" data-field="overheadCosts"></div>
            </div>
            <div class="field required">
                <label>Общепроизводственные расходы, %</label>
                <form:input type="search" path="productionCosts"/>
                <div class="ui compact message error" data-field="productionCosts"></div>
            </div>
            <div class="field required">
                <label>Общехозяйственные расходы, %</label>
                <form:input type="search" path="householdExpenses"/>
                <div class="ui compact message error" data-field="householdExpenses"></div>
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
        const $modal = $('div.list_edit__modal');
        $modal.find(
            'input[name="additionalSalary"], ' +
            'input[name="socialInsurance"], ' +
            'input[name="overheadCosts"], ' +
            'input[name="productionCosts"], ' +
            'input[name="householdExpenses"]'
        ).inputmask('inputMoney');
    });
</script>