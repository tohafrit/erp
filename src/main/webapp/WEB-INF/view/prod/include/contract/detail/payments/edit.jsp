<div class="ui modal detail_payments_edit__main">
    <div class="header">
        ${empty form.id ? 'Добавление платежа' : 'Редактирование платежа'}
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui small form">
            <form:hidden path="id"/>
            <form:hidden path="sectionId"/>
            <form:hidden path="accountPayerId"/>
            <div class="field">
                <label>Номер п/п:</label>
                <form:input path="number" data-inputmask-mask="9{1,20}"/>
                <div class="ui compact message error" data-field="number"></div>
            </div>
            <div class="field">
                <label>Дата создания</label>
                <div class="ui calendar">
                    <div class="ui input left icon">
                        <i class="calendar icon"></i>
                        <form:input cssClass="std-date" path="date"/>
                    </div>
                </div>
                <div class="ui compact message error" data-field="date"></div>
            </div>
            <div class="field">
                <label>Сумма</label>
                <form:input path="amount" cssClass="detail_payments_edit__input-amount"/>
                <div class="ui compact message error" data-field="amount"></div>
            </div>
            <div class="field">
                <label>По счету</label>
                <form:select cssClass="ui dropdown search label std-select" path="invoice.id">
                    <c:forEach items="${invoicesNumbersList}" var="invoice">
                        <form:option value="${invoice.id}">${invoice.value}</form:option>
                    </c:forEach>
                </form:select>
                <div class="ui compact message error" data-field="invoice"></div>
            </div>
            <div class="field">
                <label>Плательщик</label>
                ${form.payer}
                <form:hidden path="payer"/>
            </div>
            <div class="field required detail_payments_edit__field-account">
                <label>Расчетный счет</label>
                <div class="detail_payments_edit__button">
                    <i class="icon add link blue detail_payments_edit__btn-add-account" title="<fmt:message key="label.button.add"/>"></i>
                </div>
                <div class="detail_payments_edit_selected-account_table table-sm"></div>
                <div class="ui compact message error" data-field="accountPayerId"></div>
            </div>
            <div class="field">
                <label>Счет-фактура на аванс</label>
                <form:input path="advanceInvoice"/>
            </div>
            <div class="field">
                <label>Комментарий</label>
                <div class="ui textarea">
                    <form:textarea path="note" rows="3"/>
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
        const $main = $('div.detail_payments_edit__main');
        const $accountPayerId = $main.find('input#accountPayerId');
        const $addBtn = $main.find('i.detail_payments_edit__btn-add-account');

        $addBtn.toggle(${empty form.accountPayerId});

        $('input.detail_payments_edit__input-amount').inputmask('inputMoney');

        const table = new Tabulator('div.detail_payments_edit_selected-account_table', {
            ajaxURL: ACTION_PATH.DETAIL_PAYMENT_EDIT_SELECTED_ACCOUNT_LOAD,
            ajaxRequesting: (url, params) => {
                params.accountPayerId = $accountPayerId.val();
            },
            selectable: 1,
            headerSort: false,
            layout: 'fitColumns',
            columns: [
                { title: 'Расчетный счет', field: TABR_FIELD.ACCOUNT },
                { title: 'Банк', field: TABR_FIELD.BANK_NAME },
                {
                    title: 'Комментарий',
                    field: TABR_FIELD.COMMENT,
                    variableHeight: true,
                    minWidth: 150,
                    formatter: 'textarea',
                    headerSort: false
                }
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
                loadURL: VIEW_PATH.DETAIL_PAYMENTS_EDIT_ACCOUNTS,
                loadData: { id: id }
            });
        }

        // Редактирование/добавление расчетного счета
        $addBtn.on({
            'click': () => editAccount()
        });
    });
</script>