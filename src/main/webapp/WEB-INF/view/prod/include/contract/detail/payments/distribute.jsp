<div class="ui modal detail_payments_distribute__main">
    <div class="header">
        ${paymentId eq null ? 'Распределение платежей' : 'Распределение платежа'}
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui small form">
            <form:hidden path="sectionId"/>
            <form:hidden path="paidProductsTableData"/>
            <div class="ui three column grid">
                <div class="column field">
                    <label>Алгоритм распределения:</label>
                    <form:select cssClass="ui dropdown label std-select" path="distributionAlgorithmType">
                        <c:forEach items="${distributionAlgorithmTypeList}" var="distributionAlgorithmType">
                            <form:option value="${distributionAlgorithmType.id}"><fmt:message key="${distributionAlgorithmType.value}"/></form:option>
                        </c:forEach>
                    </form:select>
                </div>
                <div class="column field">
                    <div class="column field">
                        <label>Нераспределенная сумма:</label>
                        <form:input path="unallocatedAmount" readonly="true"/>
                    </div>
                </div>
                <div class="column field">
                    <div class="column field">
                        <label>Распределяемая сумма:</label>
                        <form:input path="allocatedAmount" cssClass="detail_payments_distribute__allocatedAmount-input"/>
                    </div>
                </div>
            </div>
        </form:form>
        <div class="field">
            <label class="detail_payments_distribute_label">Изделия</label>
            <div class="detail_payments_distribute_content-paid-products">
                <i class="icon filter link detail_payments_distribute__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
                <div class="detail_payments_distribute__header_buttons-expand">
                    <i class="icon expand alternate link blue detail_payments_distribute__btn-expand" title="Развернуть"></i>
                    <i class="icon compress alternate link blue detail_payments_distribute__btn-compress" title="Свернуть"></i>
                </div>
                <div class="detail_payments_distribute__header_buttons-action">
                    <button class="ui small disabled button detail_payments_distribute__btn-calculate">
                        <i class="icon calculator link blue"></i>
                        Рассчитать
                    </button>
                    <button class="ui small disabled button detail_payments_distribute__btn-nullify">
                        <i class="icon creative commons zero percent link blue"></i>
                        Обнулить
                    </button>
                    <button class="ui small button detail_payments_distribute__btn-cancel">
                        <i class="icon reply all link blue"></i>
                        Отменить
                    </button>
                </div>
                <form:form modelAttribute="contractPaidProductsFilterForm" cssClass="ui tiny form secondary segment detail_payments_distribute_filter__form">
                    <div class="field">
                        <div class="ui icon small buttons">
                            <div class="ui button detail_payments_distribute_filter__btn-search"
                                 title="<fmt:message key="label.button.search"/>">
                                <i class="search blue icon"></i>
                            </div>
                            <div class="ui button detail_payments_distribute_filter__btn-clear-all"
                                 title="<fmt:message key="label.button.clearFilter"/>">
                                <i class="times blue icon"></i>
                            </div>
                        </div>
                    </div>
                    <div class="ui two column grid">
                        <div class="column field">
                            <label>Изделие</label>
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
                                        <i class="times link blue icon detail_payments_distribute_filter__btn-clear"></i>
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
                <div class="detail_payments_distribute_paid-products_table table-sm"></div>
            </div>
        </div>
    </div>
    <div class="actions">
        <button class="ui small disabled button detail_payments_distribute__btn-save" type="submit">
            <i class="icon blue save"></i><fmt:message key="label.button.save"/>
        </button>
    </div>
</div>

<script>
    $(() => {
        const sectionId = '${sectionId}';
        const paymentId = '${paymentId}';
        //
        const $main = $('div.detail_payments_distribute__main');
        const $buttonCalculate = $main.find('button.detail_payments_distribute__btn-calculate');
        const $buttonNullify = $main.find('button.detail_payments_distribute__btn-nullify');
        const $buttonCancel = $main.find('button.detail_payments_distribute__btn-cancel');
        const $allocatedAmountInput = $main.find('input[name="allocatedAmount"]');
        const $unallocatedAmount = $main.find('input[name="unallocatedAmount"]');
        const $paidProductsTableData = $main.find('input[name="paidProductsTableData"]');
        //
        const $btnFilter = $('i.detail_payments_distribute__btn-filter');
        const $filterForm = $('form.detail_payments_distribute_filter__form');
        const $clearAllButton = $('div.detail_payments_distribute_filter__btn-clear-all');
        const $btnSearch = $('div.detail_payments_distribute_filter__btn-search');
        const $paidProductsTable = $('div.detail_payments_distribute_paid-products_table');
        const $btnExpand = $('i.detail_payments_distribute__btn-expand');
        const $btnCompress = $('i.detail_payments_distribute__btn-compress');
        const $btnSave = $('button.detail_payments_distribute__btn-save');

        $allocatedAmountInput.inputmask('inputMoney');
        $unallocatedAmount.inputmask('inputMoney');

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

        const paidProductsTable = new Tabulator('div.detail_payments_distribute_paid-products_table', {
            ajaxURL: ACTION_PATH.DETAIL_PAYMENT_EDIT_PRODUCTS_LOAD,
            ajaxRequesting: (url, params) => {
                params.filterForm = formToJson($filterForm);
                params.sectionId = sectionId;
            },
            selectable: true,
            headerSort: false,
            ajaxSorting: true,
            maxHeight: '350px',
            groupBy: ['groupMain', 'groupSubMain'],
            groupStartOpen: [true, true],
            groupToggleElement: 'header',
            layout: 'fitColumns',
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
                            hozAlign: 'center',
                            formatter: cell => {
                                return cell.getValue();
                            }
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
            rowSelected: () => {
                $buttonCalculate.toggleClass('disabled', !paidProductsTable.getSelectedRows().length);
                $buttonNullify.toggleClass('disabled', !paidProductsTable.getSelectedRows().length);
            },
            rowDeselected: () => {
                $buttonCalculate.toggleClass('disabled', !paidProductsTable.getSelectedRows().length);
                $buttonNullify.toggleClass('disabled', !paidProductsTable.getSelectedRows().length);
            }
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

        $buttonCalculate.on({
            'click': () => {
                const unallocatedAmount = $unallocatedAmount.val();
                const allocatedAmount = $main.find('input[name="allocatedAmount"]').val();
                const algorithmTypeId = $main.find('select[name=distributionAlgorithmType] > option:selected').val();
                //
                const data = [];
                paidProductsTable.getSelectedRows().forEach(row => data.push({
                    id: row.getData().id,
                    cost: row.getData().cost,
                    paid: row.getData().paid,
                    percentPaid: row.getData().percentPaid,
                    unallocatedAmount: row.getData().unallocatedAmount
                }));
                $.get({
                    url: ACTION_PATH.DETAIL_PAYMENTS_DISTRIBUTE_CALCULATE,
                    data: {
                        sectionId: sectionId,
                        unallocatedAmount: unallocatedAmount,
                        allocatedAmount: allocatedAmount,
                        algorithmTypeId: algorithmTypeId,
                        paymentId: paymentId,
                        paidProductsDataJson: JSON.stringify(data)
                    },
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(dataTable => {
                    paidProductsTable.updateOrAddData(dataTable);
                    $unallocatedAmount.val(dataTable[0].unallocatedAmount);
                    $allocatedAmountInput.val(dataTable[0].allocatedAmount);
                    $btnSave.removeClass('disabled');
                });
            }
        });

        $buttonCancel.on({
            'click': () => {
                $.get({
                    url: ACTION_PATH.DETAIL_PAYMENT_EDIT_PRODUCTS_LOAD,
                    data: {
                        filterForm: formToJson($filterForm),
                        sectionId: sectionId,
                        paymentId: paymentId
                    }
                }).done(data => {
                    paidProductsTable.setData(data);
                    $unallocatedAmount.val(data[0].unallocatedAmount);
                    $allocatedAmountInput.val(data[0].allocatedAmount);
                    $btnSave.addClass('disabled');
                });
            }
        });

        $buttonNullify.on({
            'click': () => {
                const unallocatedAmount = $unallocatedAmount.val();
                const allocatedAmount = $main.find('input[name="allocatedAmount"]').val();
                const data = [];
                paidProductsTable.getSelectedRows().forEach(row => data.push({
                    id: row.getData().id,
                    cost: row.getData().cost,
                    paid: row.getData().paid,
                    percentPaid: row.getData().percentPaid,
                    unallocatedAmount: row.getData().unallocatedAmount
                }));
                $.get({
                    url: ACTION_PATH.DETAIL_PAYMENTS_DISTRIBUTE_NULLIFY,
                    data: {
                        sectionId: sectionId,
                        unallocatedAmount: unallocatedAmount,
                        allocatedAmount: allocatedAmount,
                        paymentId: paymentId,
                        paidProductsDataJson: JSON.stringify(data)
                    }
                }).done(dataTable => {
                    paidProductsTable.updateOrAddData(dataTable);
                    $unallocatedAmount.val(dataTable[0].unallocatedAmount);
                    $allocatedAmountInput.val(dataTable[0].allocatedAmount);
                    $btnSave.removeClass('disabled');
                });
            }
        });

        // Добавление данных таблицы после распределения в сабмит форму
        $main.on({
            'cb.onInitSubmit': () => {
                let selectedRows = paidProductsTable.getSelectedRows();
                let data = [];
                selectedRows.forEach(row => data.push({ id: row.getData().id, paid: row.getData().paid }));
                $paidProductsTableData.val(JSON.stringify(data));
            }
        });
    });
</script>