<div class="ui modal list_edit__main">
    <div class="header">
        ${empty form.id ? 'Добавление расчетного счета' : 'Редактирование расчетного счета'}
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui small form">
            <form:hidden path="id"/>
            <form:hidden path="bankId"/>
            <form:hidden path="customerId"/>
            <div class="field">
                <label>Номер расчетного счета</label>
                <form:input path="accountNumber"/>
                <div class="ui compact message error" data-field="accountNumber"></div>
            </div>
            <div class="field">
                <label>Комментарий</label>
                <form:textarea path="comment" rows="3"/>
                <div class="ui compact message error" data-field="comment"></div>
            </div>
            <div class="inline fields required">
                <label>Банк</label>
                <div class="list_edit_bank__buttons">
                    <i class="icon add link blue list_edit_bank__btn-add" title="<fmt:message key="label.button.add"/>"></i>
                </div>
            </div>
            <div class="field">
                <div class="list_edit_selected-bank_table table-sm table-striped"></div>
                <div class="ui compact message error" data-field="bankId"></div>
            </div>
            <div class="inline fields required">
                <label>Заказчик</label>
                <div class="list_edit_customer__buttons">
                    <i class="icon add link blue list_edit_customer__btn-add" title="<fmt:message key="label.button.add"/>"></i>
                </div>
            </div>
            <div class="field">
                <div class="list_edit_selected-customer_table table-sm table-striped"></div>
                <div class="ui compact message error" data-field="customerId"></div>
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
        const $main = $('div.list_edit__main');
        const $addCustomerBtn = $main.find('i.list_edit_customer__btn-add');
        const $addBankBtn = $main.find('i.list_edit_bank__btn-add');
        const $customerId = $main.find('input#customerId');
        const $bankId = $main.find('input#bankId');
        const $accountNumber = $main.find('input#accountNumber');
        const isEditCustomer = '${isEditCustomer}' === 'true'

        $addCustomerBtn.toggle(${empty form.customerId});
        $addBankBtn.toggle(${empty form.bankId});

        $accountNumber.inputmask({
            placeholder: '',
            regex: '[0-9]{0,20}'
        });

        // Функция добавления/редактирования заказчика
        function editCustomer(id) {
            $.modalWindow({
                loadURL: VIEW_PATH.LIST_EDIT_CUSTOMER,
                loadData: { id: id }
            });
        }

        // Редактирование/добавление заказчика
        $addCustomerBtn.on({
            'click' : () => editCustomer()
        });

        new Tabulator('div.list_edit_selected-customer_table', {
            ajaxURL: ACTION_PATH.LIST_EDIT_CUSTOMER_SELECTED_LOAD,
            ajaxRequesting: (url, params) => {
                params.customerId = $customerId.val();
            },
            selectable: 1,
            headerSort: false,
            layout: 'fitDataFill',
            columns: [
                { title: 'Название', field: TABR_FIELD.NAME },
                { title: 'Адрес', field: TABR_FIELD.ADDRESS }
            ],
            rowContextMenu: row => {
                const menu = [];
                const id = row.getData().id;
                menu.push({
                    label:`<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>`,
                    action: () => editCustomer(id)
                });
                return menu;
            }
        });

        // Функция добавления/редактирования банка
        function editBank(id) {
            $.modalWindow({
                loadURL: VIEW_PATH.LIST_EDIT_BANK,
                loadData: { id: id }
            });
        }

        // Редактирование/добавление банка
        $addBankBtn.on({
            'click' : () => editBank()
        });

        new Tabulator('div.list_edit_selected-bank_table', {
            ajaxURL: ACTION_PATH.LIST_EDIT_BANK_SELECTED_LOAD,
            ajaxRequesting: (url, params) => {
                params.bankId = $bankId.val();
            },
            selectable: 1,
            headerSort: false,
            layout: 'fitDataFill',
            columns: [
                { title: 'Название', field: TABR_FIELD.NAME },
                { title: 'Местонахождение', field: TABR_FIELD.LOCATION }
            ],
            rowContextMenu: row => {
                const menu = [];
                const id = row.getData().id;
                if (isEditCustomer) {
                    menu.push({
                        label:`<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>`,
                        action: () => editCustomer(id)
                    });
                    return menu;
                }
            }
        });
    });
</script>