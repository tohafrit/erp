<div class="ui modal detail_invoices_add__main">
    <div class="header">Создание счета</div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui small form">
            <form:hidden path="id"/>
            <form:hidden path="accountId"/>
            <form:hidden path="sectionId"/>
            <form:hidden path="allotmentIdList"/>
            <form:hidden path="invoiceForAmountDialog"/>
            <div class="ui three column grid">
                <div class="column field">
                    <label>Номер счета:</label>
                    <form:input path="invoiceNumber"/>
                    <div class="ui compact message error" data-field="invoiceNumber"></div>
                </div>
                <div class="column field">
                    <label>Дата создания:</label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <form:input cssClass="std-date" path="invoiceDate" maxlength="10"/>
                        </div>
                    </div>
                    <div class="ui compact message error" data-field="invoiceDate"></div>
                </div>
                <div class="column field"></div>
            </div>
            <div class="ui three column grid">
                <div class="column field">
                    <label>Счет действителен до:</label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <form:input cssClass="std-date" path="goodThroughDate" maxlength="10"/>
                        </div>
                    </div>
                    <div class="ui compact message error" data-field="goodThroughDate"></div>
                </div>
                <div class="column field">
                    <label>Срок изготовления изделий:</label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <form:input cssClass="std-date" path="productionDate" maxlength="10"/>
                        </div>
                    </div>
                    <div class="ui compact message error" data-field="productionDate"></div>
                </div>
                <div class="column field"></div>
            </div>
            <div class="ui three column grid">
                <div class="column field">
                    <label>Тип счета:</label>
                    <form:select cssClass="ui dropdown label std-select" path="invoiceType">
                        <c:forEach items="${invoiceTypeList}" var="invoiceType">
                            <form:option value="${invoiceType.id}">${invoiceType.value}</form:option>
                        </c:forEach>
                    </form:select>
                </div>
                <div class="column field required detail_invoices_add_percent-advance-div">
                    <label>Аванс (%):</label>
                    <form:input path="percentAdvance" cssClass="detail_invoices_add__percent-advance-input"/>
                </div>
                <div class="column field required detail_invoices_add_for-amount-invoice-div">
                    <label>Сумма:</label>
                    <form:input path="forAmountInvoice" cssClass="detail_invoices_add__for-amount-invoice-input"/>
                    <div class="ui compact message error" data-field="forAmountInvoice"></div>
                </div>
            </div>
            <div class="field required detail_invoices_add__field-account">
                <label>Расчетный счет</label>
                <div class="detail_invoices_add__button">
                    <i class="icon add link blue detail_invoices_add__btn-add-account" title="<fmt:message key="label.button.add"/>"></i>
                </div>
                <div class="detail_invoices_add_selected-account_table table-sm"></div>
                <div class="ui compact message error" data-field="accountId"></div>
            </div>
        </form:form>
        <div class="field">
            <label class="detail_invoices_add_label">Оплачиваемые изделия</label>
            <div class="content detail_invoices_add_content-paid-products">
                <i class="icon filter link blue detail_invoices_add__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
                <div class="detail_delivery-statement__header_buttons-expand">
                    <i class="icon expand alternate link blue detail_invoices_add__btn-expand" title="Развернуть"></i>
                    <i class="icon compress alternate link blue detail_invoices_add__btn-compress" title="Свернуть"></i>
                </div>
                <form:form modelAttribute="contractPaidProductsFilterForm" cssClass="ui tiny form secondary segment detail_invoices_add_filter__form">
                    <div class="field">
                        <div class="ui icon small buttons">
                            <div class="ui button detail_invoices_add_filter__btn-search"
                                 title="<fmt:message key="label.button.search"/>">
                                <i class="search blue icon"></i>
                            </div>
                            <div class="ui button detail_invoices_add_filter__btn-clear-all"
                                 title="<fmt:message key="label.button.clearFilter"/>">
                                <i class="times blue icon"></i>
                            </div>
                        </div>
                    </div>
                    <div class="ui two column grid">
                        <div class="column field">
                            <label>Условное наименование изделия</label>
                            <form:input path="conditionalName" type="search"/>
                        </div>
                        <div class="column field">
                            <div class="two fields">
                                <div class="field">
                                    <label>Дата поставки с</label>
                                    <div class="ui calendar">
                                        <div class="ui input left icon">
                                            <i class="calendar icon"></i>
                                            <form:input cssClass="std-date" path="deliveryDateFrom" type="search"/>
                                        </div>
                                    </div>
                                </div>
                                <div class="field">
                                    <label>
                                        <fmt:message key="label.to"/>
                                        <i class="times link blue icon detail_invoices_add_filter__btn-clear"></i>
                                    </label>
                                    <div class="ui calendar">
                                        <div class="ui input left icon">
                                            <i class="calendar icon"></i>
                                            <form:input cssClass="std-date" path="deliveryDateTo" type="search"/>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </form:form>
                <div class="detail_invoices_add_paid-products_table table-sm"></div>
            </div>
        </div>
    </div>
    <div class="actions">
        <button class="ui small disabled button detail_invoices_add__button-save" type="submit">
            <i class="icon blue save"></i>
            <fmt:message key="label.button.save"/>
        </button>
    </div>
</div>

<script>
    $(() => {
        const sectionId = '${sectionId}';
        const invoiceAdvanceId = '${invoiceAdvanceId}';
        const invoiceForAmountId = '${invoiceForAmountId}';
        //
        const $main = $('div.detail_invoices_add__main');
        const $invoiceType = $main.find('[name="invoiceType"]');
        const $addBtn = $main.find('i.detail_invoices_add__btn-add-account');
        const $accountTable = $main.find('.detail_invoices_add_selected-account_table');
        const $accountId = $main.find('input#accountId');
        const $forAmountInvoiceInput = $('input.detail_invoices_add__for-amount-invoice-input');
        const $percentAdvanceInput = $('input.detail_invoices_add__percent-advance-input');
        const $forAmountInvoiceDiv = $('div.detail_invoices_add_for-amount-invoice-div');
        const $percentAdvanceDiv = $('div.detail_invoices_add_percent-advance-div');
        const $allotmentIdList = $main.find('input[name="allotmentIdList"]');
        const $btnSave = $('button.detail_invoices_add__button-save');
        //
        const $btnFilter = $('i.detail_invoices_add__btn-filter');
        const $filterForm = $('form.detail_invoices_add_filter__form');
        const $clearAllButton = $('div.detail_invoices_add_filter__btn-clear-all');
        const $btnSearch = $('div.detail_invoices_add_filter__btn-search');
        const $paidProductsTable = $('div.detail_invoices_add_paid-products_table');
        const $btnExpand = $('i.detail_invoices_add__btn-expand');
        const $btnCompress = $('i.detail_invoices_add__btn-compress');

        $addBtn.toggle(${empty form.accountId});

        $invoiceType.on({
            'change': e => {
                let typeValue = $(e.currentTarget).val();
                $percentAdvanceDiv.toggle(typeValue === invoiceAdvanceId);
                $forAmountInvoiceDiv.toggle(typeValue === invoiceForAmountId);
            }
        }).filter(':selected').trigger('change');
        $invoiceType.trigger('change');

        // Функция добавления/редактирования расчетного счета
        function editAccount(id) {
            $.modalWindow({
                loadURL: VIEW_PATH.DETAIL_INVOICES_ADD_ACCOUNTS,
                loadData: { id: id }
            });
        }

        // Редактирование/добавление расчетного счета
        $addBtn.on({
            'click' : () => editAccount()
        });

        const table = new Tabulator('div.detail_invoices_add_selected-account_table', {
            ajaxURL: ACTION_PATH.DETAIL_INVOICE_ADD_SELECTED_ACCOUNT_LOAD,
            ajaxRequesting: (url, params) => {
                params.accountId = $accountId.val();
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

        $forAmountInvoiceInput.inputmask('inputMoney');

        $percentAdvanceInput.inputmask('numeric', {
            min: 1,
            max: 100,
            rightAlign: false
        });

        // Очистка полей фильтра
        $clearAllButton.on({
            'click': () => formClear($filterForm)
        });

        // Поиск по кнопки enter
        $filterForm.enter(() => $btnSearch.trigger('click'));

        // Кнопка фильтра
        $btnFilter.on({
            'click': function() {
                $(this).toggleClass('primary');
                $filterForm.toggle(!$filterForm.is(':visible'));
            }
        });

        const paidProductsTable = new Tabulator('div.detail_invoices_add_paid-products_table', {
            ajaxURL: ACTION_PATH.DETAIL_INVOICE_ADD_PRODUCTS_LOAD,
            ajaxRequesting: (url, params) => {
                params.filterForm = formToJson($filterForm);
                params.sectionId = sectionId;
            },
            selectable: true,
            headerSort: false,
            ajaxSorting: true,
            maxHeight: '250px',
            groupBy: ['groupMain', 'groupSubMain'],
            groupStartOpen: [false, true],
            groupToggleElement: 'header',
            layout: 'fitColumns',
            selectableCheck: (row) => {
                return row.getData().cannotAddedInvoice;
            },
            groupHeader: [
                function(value) {
                    return '<span style="color:#315c83;">' + value + '</span>';
                },
                function(value) {
                    return value;
                }
            ],
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: 'Кол-во', field: TABR_FIELD.ALLOTMENT_AMOUNT, hozAlign: 'center', },
                {
                    title: 'Ст-ть',
                    field: TABR_FIELD.COST,
                    hozAlign: 'center',
                    formatter: 'stdMoney'
                },
                {
                    title: 'Оплачено',
                    columns: [
                        {
                            title: 'руб.',
                            field: TABR_FIELD.PAID,
                            hozAlign: 'center',
                            formatter: 'stdMoney'
                        },
                        {
                            title: '%',
                            field: TABR_FIELD.PERCENT_PAID,
                            hozAlign: 'center'
                        }
                    ]
                },
                {
                    title: 'Счет на ОО',
                    field: TABR_FIELD.FINAL_PRICE,
                    resizable: false,

                    hozAlign: 'center',
                    formatter: cell => {
                        return booleanToLight(cell.getValue() > 0);
                    }
                },
                { title: 'Запущено', field: TABR_FIELD.LAUNCH_AMOUNT, hozAlign: 'center' },
                { title: 'Запуск', field: TABR_FIELD.LAUNCH_NUMBER, hozAlign: 'center' },
                {
                    title: 'Письмо на пр-во',
                    columns:[
                        {
                            title: 'Номер',
                            field: TABR_FIELD.LETTER_NUMBER,
                            hozAlign: 'center',
                        },
                        {
                            title: 'Дата',
                            field: TABR_FIELD.LETTER_CREATION_DATE,
                            hozAlign: 'center',
                            formatter: 'stdDate'
                        }
                    ]
                },
                {
                    title: 'Распор-ие на отгрузку',
                    field: TABR_FIELD.SHIPMENT_PERMIT_DATE,
                    resizable: false,
                    hozAlign: 'center',
                    formatter: cell => {
                        const date = dateStdToString(cell.getValue());
                        return booleanToLight(cell.getValue() != null, { onTrue: 'Распоряжение выдано ' + date, onFalse: 'Отгрузка не разрешена' });
                    }
                },
            ],
            rowSelected: () => $btnSave.toggleClass('disabled', !paidProductsTable.getSelectedRows().length),
            rowDeselected: () => $btnSave.toggleClass('disabled', !paidProductsTable.getSelectedRows().length)
        });

        // Добавление стилей для переноса слов заголовка и выравнивание заголовков по центру
        $paidProductsTable.find('.tabulator-col').css({
            'height': '',
            'word-wrap': 'break-word',
            'text-align': 'center'
        });
        $paidProductsTable.find('.tabulator-col-title').css({
            'white-space': 'pre-wrap',
            'text-overflow': 'clip'
        });

        // Кнопка свернуть группы
        $btnCompress.on({
            'click': () => paidProductsTable.getGroups().forEach(group => group.hide())
        });
        // Кнопка развернуть группы
        $btnExpand.on({
            'click': () => paidProductsTable.getGroups().forEach(group => group.show())
        });

        // Кнопка поиска
        $btnSearch.on({
            'click': () => paidProductsTable.setData()
        });

        // Добавление списка идентификаторов allotment-ов в сабмит форму
        $main.on({
            'cb.onInitSubmit' : () => {
                let selectedRows = paidProductsTable.getSelectedRows();
                let allotmentIdArr = selectedRows.map(row => row.getData().id);
                $allotmentIdList.val(JSON.stringify(allotmentIdArr));
            }
        });
    });
</script>