<div class="ui modal detail_invoices_edit__main">
    <div class="header">Редактирование счета</div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui small form">
            <form:hidden path="id"/>
            <form:hidden path="accountId"/>
            <form:hidden path="sectionId"/>
            <div class="field">
                <label>Номер</label>
                <form:input path="invoiceNumber"/>
            </div>
            <div class="field">
                <label>Дата</label>
                <div class="ui calendar">
                    <div class="ui input left icon">
                        <i class="calendar icon"></i>
                        <form:input cssClass="std-date" path="invoiceDate"/>
                    </div>
                </div>
            </div>
            <div class="field">
                <label>Действителен до</label>
                <div class="ui calendar">
                    <div class="ui input left icon">
                        <i class="calendar icon"></i>
                        <form:input cssClass="std-date" path="goodThroughDate"/>
                    </div>
                </div>
            </div>
            <div class="field">
                <div class="detail_invoices_edit_selected-account_table table-sm table-striped"></div>
                <div class="ui compact message error" data-field="accountId"></div>
            </div>
            <div class="field">
                <label>Сумма</label>
                <form:input path="invoiceAmount"/>
            </div>
            <div class="field">
                <label>Оплачено</label>
                <form:input path="paidAmount"/>
            </div>
            <div class="field">
                <label>Описание</label>
                <div class="ui textarea">
                    <form:textarea path="note" rows="1"/>
                </div>
            </div>
            <div class="field">
                <label>Тип счета:</label>
                <form:select cssClass="ui dropdown label std-select" path="invoiceType">
                    <c:forEach items="${invoiceTypeList}" var="invoiceType">
                        <form:option value="${invoiceType.id}">${invoiceType.value}</form:option>
                    </c:forEach>
                </form:select>
            </div>
            <div class="field">
                <label>Статус</label>
                <form:select cssClass="ui dropdown search label std-select" path="invoiceStatus">
                    <c:forEach items="${invoiceStatusList}" var="invoiceStatus">
                        <form:option value="${invoiceStatus.id}">${invoiceStatus.value}</form:option>
                    </c:forEach>
                </form:select>
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
        const $main = $('div.detail_invoices_edit__main');
        const $accountId = $main.find('input#accountId');

        $main.find('input[name="invoiceAmount"], input[name="paidAmount"]').inputmask('inputMoney');

        new Tabulator('div.detail_invoices_edit_selected-account_table', {
            ajaxURL: ACTION_PATH.DETAIL_INVOICE_EDIT_SELECTED_ACCOUNT_LOAD,
            ajaxRequesting: (url, params) => {
                params.accountId = $accountId.val();
            },
            selectable: 1,
            headerSort: false,
            layout: 'fitColumns',
            columns: [
                { title: 'Расчетный счет', field: TABR_FIELD.ACCOUNT },
                { title: 'Банк', field: TABR_FIELD.BANK_NAME },
                { title: 'Комментарий', field: TABR_FIELD.COMMENT }
            ],
            rowContextMenu: component => {
                const menu = [];
                const id = component.getData().id;
                menu.push({
                    label:`<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>`,
                    action: () => editAccount(id)
                });
                return menu;
            }
        });

        // Функция добавления/редактирования расчетного счета
        function editAccount(id) {
            $.modalWindow({
                loadURL: VIEW_PATH.DETAIL_INVOICES_EDIT_ACCOUNTS,
                loadData: { id: id }
            });
        }
    });
</script>