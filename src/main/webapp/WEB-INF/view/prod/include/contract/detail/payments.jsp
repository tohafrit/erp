<div class="detail_payments__main">
    <div class="detail_payments__header_buttons">
        <i class="icon filter link blue detail_payments__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        <i class="icon add link blue detail_payments__btn-add" title="Добавить платеж"></i>
        <i class="icon chart pie link blue detail_payments__btn-distribute" title="Распределить платежи"></i>
        <div class="detail_payments__div_header_title">
            <h1 class="detail_payments__header_title">
                Платежи
                <c:if test="${isAdditionalAgreement}">
                    дополнительного соглашения № ${contractSectionNumber}
                </c:if>
            </h1>
        </div>
    </div>
    <div class="detail_payments__table-result table-sm"></div>
    <div class="detail_payments__table-block">
        <div class="detail_payments__table-main-block">
            <div class="detail_payments__table-wrap">
                <div class="detail_payments__table table-sm"></div>
            </div>
        </div>
    </div>

    <script>
        $(() => {
            const sectionId = '${sectionId}';
            //
            const $menuTree = $('ul.detail__menu_tree');
            const $btnFilter = $('i.detail_payments__btn-filter');
            const $btnAdd = $('i.detail_payments__btn-add');
            const $btnDistribute = $('i.detail_payments__btn-distribute');
            const $table = $('div.detail_payments__table');
            const $paymentsMenu = $menuTree.find('li.detail__menu_payments[data-id=${sectionId}]');

            // Параметры фильтра таблицы
            const tableData = tableDataFromUrlQuery(window.location.search);
            let filterData = tableData.filterData;

            const table = new Tabulator('div.detail_payments__table', {
                ajaxURL: ACTION_PATH.DETAIL_PAYMENTS_LOAD,
                ajaxRequesting: (url, params) => {
                    params.filterData = JSON.stringify(filterData);
                    params.sectionId = sectionId;
                },
                selectable: 1,
                headerSort: false,
                height: '100%',
                layout: 'fitColumns',
                columns: [
                    TABR_COL_LOCAL_ROW_NUM,
                    {
                        title: 'Номер п/п',
                        field: TABR_FIELD.PAYMENT_NUMBER,
                        width: 100,
                        resizable: false
                    },
                    {
                        title: 'Дата платежа',
                        field: TABR_FIELD.PAYMENT_DATE,
                        hozAlign: 'center',
                        formatter: 'stdDate'
                    },
                    {
                        title: 'Сумма',
                        field: TABR_FIELD.PAYMENT_AMOUNT,
                        hozAlign: 'center',
                        formatter: 'stdMoney'
                    },
                    {
                        title: 'По счету',
                        field: TABR_FIELD.INVOICE_NUMBER,
                        hozAlign: 'center'
                    },
                    { title: 'Плательщик', field: TABR_FIELD.PAYER_NAME },
                    {
                        title: 'Расчетный счет',
                        field: TABR_FIELD.ACCOUNT_NUMBER,
                        hozAlign: 'center'
                    },
                    {
                        title: 'Счет-фактура на аванс',
                        columns: [
                            {
                                title: 'Номер',
                                field: TABR_FIELD.ADVANCE_INVOICE_NUMBER,
                                hozAlign: 'center'
                            },
                            {
                                title: 'Дата',
                                field: TABR_FIELD.ADVANCE_INVOICE_DATE,
                                hozAlign: 'center',
                                formatter: 'stdDate'
                            }
                        ]
                    },
                    {
                        title: 'Комментарий',
                        field: TABR_FIELD.COMMENT,
                        variableHeight: true,
                        minWidth: 150,
                        formatter: 'textarea',
                        headerSort: false
                    }
                ],
                rowContext: (e, row) => {
                    table.deselectRow();
                    row.select();
                },
                rowContextMenu: row => {
                    const menu = [];
                    const id = row.getData().id;
                    menu.push({
                        label: `<i class="chart pie icon blue"></i>Распределить платёж`,
                        action: () => distributePayment(id)
                    });
                    menu.push({
                        label: `<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>`,
                        action: () => editPayment(id)
                    });
                    menu.push({
                        label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                        action: () => deletePayment(id)
                    });
                    return menu;
                }
            });

            // Добавление стилей для переноса слов заголовка и выравнивание заголовков по центру
            $table.find('.tabulator-col').css({
                'height': '',
                'word-wrap': 'break-word',
                'text-align': 'center'
            });
            $table.find('.tabulator-col-title').css({
                'white-space': 'pre-wrap',
                'text-overflow': 'clip'
            });

            // Фильтр
            $.modalFilter({
                url: VIEW_PATH.DETAIL_PAYMENTS_FILTER,
                button: $btnFilter,
                filterData: () => filterData,
                onApply: data => {
                    filterData = data;
                    table.setData();
                }
            });

            const resultTable = new Tabulator('div.detail_payments__table-result', {
                ajaxURL: ACTION_PATH.DETAIL_PAYMENTS_RESULT_LOAD,
                ajaxRequesting: (url, params) => {
                    params.sectionId = sectionId;
                },
                selectable: false,
                headerSort: false,
                layout: 'fitDataTable',
                columns: [
                    {
                        title: 'Стоимость запущенных изделий',
                        field: 'allCostWithVAT',
                        hozAlign: 'center',
                        formatter: 'stdMoney'
                    },
                    {
                        title: 'Оплачено',
                        field: 'sumPaymentAmount',
                        hozAlign: 'center',
                        formatter: 'stdMoney'
                    },
                    {
                        title: 'Задолженность по запущенным изделиям',
                        field: 'outAllCostWithVAT',
                        hozAlign: 'center',
                        formatter: 'stdMoney'
                    },
                    {
                        title: 'Не распределено по изделиям',
                        field: 'outNotDistributed',
                        hozAlign: 'center',
                        formatter: 'stdMoney'
                    }
                ]
            });

            // Функция добавления/редактирования платежа
            function editPayment(id) {
                $.modalWindow({
                    loadURL: VIEW_PATH.DETAIL_PAYMENTS_EDIT,
                    loadData: { id: id, sectionId: sectionId },
                    submitURL: ACTION_PATH.DETAIL_PAYMENT_EDIT_SAVE,
                    onSubmitSuccess: () => {
                        table.setData();
                        resultTable.setData();
                    }
                });
            }

            // Функция распределения платежей
            function distributePayment(paymentId) {
                $.modalWindow({
                    loadURL: VIEW_PATH.DETAIL_PAYMENTS_DISTRIBUTE,
                    loadData: {
                        paymentId: paymentId,
                        sectionId: sectionId
                    },
                    submitURL: ACTION_PATH.DETAIL_PAYMENTS_DISTRIBUTE_SAVE,
                    onSubmitSuccess: () => resultTable.setData()
                });
            }

            // Функция удаления платежа
            function deletePayment(id) {
                confirmDialog({
                    title: 'Удаление платежа',
                    message: 'Вы уверены, что хотите удалить платеж?',
                    onAccept: () => $.ajax({
                        method: 'DELETE',
                        url: ACTION_PATH.DETAIL_PAYMENT_DELETE + id,
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => {
                        table.setData();
                        resultTable.setData();
                    })
                });
            }

            // Кнопка добавления платежа
            $btnAdd.on({
                'click': () => editPayment()
            });

            // Кнопка распределения платежей
            $btnDistribute.on({
                'click': () => distributePayment()
            });
        })
    </script>
</div>