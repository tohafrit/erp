<div class="ui modal detail_generate-file_data__main">
    <div class="header">Выберите данные</div>
    <div class="content">
        <div class="generate-file_table-name"></div>
        <div class="generate-file_data__table table-sm"></div>
        <div class="generate-file_table-name_second"></div>
        <div class="generate-file_data__table_second table-sm"></div>
    </div>
    <div class="actions">
        <div class="ui small button disabled generate-file_data__btn-select">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.select"/>
        </div>
    </div>
</div>

<script>
    $(() => {
        // Кнопка выбрать
        const $modal = $('div.detail_generate-file_data__main');
        const sectionId = '${sectionId}';
        const documentType = '${documentType}';
        const $btnSelect = $('div.generate-file_data__btn-select');

        if (documentType === '1') {
            $('div.generate-file_table-name').text('Расчетный счет');
            $('div.generate-file_table-name_second').text('Расчетный счет Заказчика');
            const table = new Tabulator('div.generate-file_data__table', {
                pagination: 'remote',
                ajaxURL: ACTION_PATH.DETAIL_DOCUMENTATION_FORMED_WITH_ACCOUNT_INFO_LOAD,
                ajaxRequesting: (url, params) => {
                    params.sectionId = sectionId;
                    params.documentType = documentType;
                    params.isPerformer = true;
                },
                selectable: 1,
                ajaxSorting: true,
                maxHeight: '450px',
                layout: 'fitColumns',
                columns: [
                    TABR_COL_REMOTE_ROW_NUM,
                    { title: 'Расчетный счет', field: TABR_FIELD.ACCOUNT },
                    { title: 'Банк', field: TABR_FIELD.BANK_NAME },
                    { title: 'Комментарии', field: TABR_FIELD.NOTE }
                ],
                rowClick: (e, row) => {
                    table.deselectRow();
                    row.select();
                    $btnSelect.toggleClass('disabled', !table.getSelectedRows().length);
                },
                rowDblClick: (e, row) => {
                    table.deselectRow();
                    row.select();
                    $btnSelect.trigger('click');
                    table.deselectRow();
                }
            });

            const tableCustomer = new Tabulator('div.generate-file_data__table_second', {
                pagination: 'remote',
                ajaxURL: ACTION_PATH.DETAIL_DOCUMENTATION_FORMED_WITH_ACCOUNT_INFO_LOAD,
                ajaxRequesting: (url, params) => {
                    params.sectionId = sectionId;
                    params.documentType = documentType;
                    params.isPerformer = false;
                },
                selectable: 1,
                ajaxSorting: true,
                maxHeight: '450px',
                layout: 'fitColumns',
                columns: [
                    TABR_COL_REMOTE_ROW_NUM,
                    { title: 'Расчетный счет', field: TABR_FIELD.ACCOUNT },
                    { title: 'Банк', field: TABR_FIELD.BANK_NAME },
                    { title: 'Комментарии', field: TABR_FIELD.NOTE }
                ],
                rowClick: (e, row) => {
                    tableCustomer.deselectRow();
                    row.select();
                    $btnSelect.toggleClass('disabled', !tableCustomer.getSelectedRows().length);
                },
                rowDblClick: (e, row) => {
                    tableCustomer.deselectRow();
                    row.select();
                    $btnSelect.trigger('click');
                    tableCustomer.deselectRow();
                }
            });

            $btnSelect.on({
                'click': () => {
                    const selectedRow = table.getSelectedRows();
                    const performerAccount = selectedRow[0].getData().id;
                    const selectedRowCustomer = tableCustomer.getSelectedRows();
                    const customerAccount = selectedRowCustomer[0].getData().id;
                    $.post({
                        url: ACTION_PATH.DETAIL_DOCUMENTATION_FORMED_WITH_ACCOUNT_INFO_SAVE,
                        data: {
                            performerAccount: performerAccount,
                            customerAccount: customerAccount,
                            sectionId: sectionId,
                            documentType: documentType
                        },
                    }).done(() => {
                        $modal.modal('hide');
                        $('.detail__menu_documentation').trigger('click');
                    });
                }
            });
        } else if (documentType === '25') {
            $('div.generate-file_table-name').text('Платежи')
            const table = new Tabulator('div.generate-file_data__table', {
                pagination: 'remote',
                ajaxURL: ACTION_PATH.DETAIL_DOCUMENTATION_FORMED_WITH_PAYMENT_INFO_LOAD,
                ajaxRequesting: (url, params) => {
                    params.sectionId = sectionId;
                    params.documentType = documentType;
                },
                selectable: 1,
                ajaxSorting: true,
                maxHeight: '450px',
                layout: 'fitColumns',
                columns: [
                    TABR_COL_REMOTE_ROW_NUM,
                    { title: 'Номер п/п', field: TABR_FIELD.NUMBER },
                    { title: 'Дата', field: TABR_FIELD.DATE },
                    { title: 'Сумма', field: TABR_FIELD.AMOUNT }
                ],
                rowClick: (e, row) => {
                    table.deselectRow();
                    row.select();
                    $btnSelect.toggleClass('disabled', !table.getSelectedRows().length)
                },
                rowDblClick: (e, row) => {
                    table.deselectRow();
                    row.select();
                    $btnSelect.trigger('click');
                    table.deselectRow();
                }
            });

            $btnSelect.on({
                'click': () => {
                    const selectedRow = table.getSelectedRows();
                    const paymentId = selectedRow[0].getData().id;
                    $.post({
                        url: ACTION_PATH.DETAIL_DOCUMENTATION_FORMED_WITH_PAYMENT_INFO_SAVE,
                        data: { paymentId: paymentId, sectionId: sectionId, documentType: documentType }
                    }).done(() => {
                        $modal.modal('hide');
                        $('.detail__menu_documentation').trigger('click');
                    });
                }
            });
        } else if (documentType === '28') {
            $('div.generate-file_table-name').text('Изделия');
            $('div.generate-file_table-name_second').text('Платежи');
            const tableAllotment = new Tabulator('div.generate-file_data__table', {
                pagination: 'remote',
                ajaxURL: ACTION_PATH.DETAIL_DOCUMENTATION_FORMED_WITH_ALLOTMENT_INFO_LOAD,
                ajaxRequesting: (url, params) => {
                    params.sectionId = sectionId;
                    params.documentType = documentType;
                },
                selectable: true,
                ajaxSorting: true,
                maxHeight: '650px',
                layout: 'fitColumns',
                columns: [
                    TABR_COL_LOCAL_ROW_NUM,
                    { title: 'Наименование', field: TABR_FIELD.NAME, hozAlign: 'center' },
                    { title: 'Кол-во', field: TABR_FIELD.ALLOTMENT_AMOUNT, hozAlign: 'center' },
                    {
                        title: 'Дата поставки',
                        field: 'shipmentDate',
                        hozAlign: 'center'

                    },
                    {
                        title: 'Оплачено',
                        field: TABR_FIELD.PAID,
                        hozAlign: 'center',
                        formatter: 'money',
                        formatterParams: {
                            decimal: ',',
                            thousand: ' '
                        }
                    },
                    // {
                    //     title: 'Счет на ОО',
                    //     field: TABR_FIELD.FINAL_PRICE,
                    //     resizable: false,
                    //     hozAlign: 'center',
                    //     formatter: cell => {
                    //         return booleanToLight(cell.getValue() > 0);
                    //     }
                    // },
                    // {title: 'Запуск', field: TABR_FIELD.LAUNCH_NUMBER, hozAlign: 'center'},
                    // {
                    //     title: 'Письмо на пр-во',
                    //     columns: [
                    //         {title: 'Номер', field: TABR_FIELD.LETTER_NUMBER, hozAlign: 'center'},
                    //         {
                    //             title: 'Дата',
                    //             field: TABR_FIELD.LETTER_CREATION_DATE,
                    //             hozAlign: 'center',
                    //             formatter: 'stdDate'
                    //         }
                    //     ]
                    // },
                    // {
                    //     title: 'Распор-ие на отгрузку',
                    //     field: TABR_FIELD.SHIPMENT_PERMIT_DATE,
                    //     resizable: false,
                    //     hozAlign: 'center',
                    //     formatter: cell => {
                    //         const date = dateStdToString(cell.getValue());
                    //         return booleanToLight(cell.getValue() != null, {
                    //             onTrue: 'Распоряжение выдано ' + date,
                    //             onFalse: 'Отгрузка не разрешена'
                    //         });
                    //     }
                    // },
                    // {
                    //     title: 'ОТК',
                    //     field: TABR_FIELD.TRANSFER_FOR_WRAPPING_DATE,
                    //     resizable: false,
                    //     hozAlign: 'center',
                    //     formatter: cell => {
                    //         const date = dateStdToString(cell.getValue());
                    //         return booleanToLight(cell.getValue() != null, {
                    //             onTrue: 'Дата прохождения ' + date,
                    //             onFalse: 'ОТК не пройден'
                    //         });
                    //     }
                    // },
                    // {
                    //     title: 'Упаковка',
                    //     field: TABR_FIELD.READY_FOR_SHIPMENT_DATE,
                    //     resizable: false,
                    //     hozAlign: 'center',
                    //     formatter: cell => {
                    //         const date = dateStdToString(cell.getValue());
                    //         return booleanToLight(cell.getValue() != null, {
                    //             onTrue: 'Дата упаковки ' + date,
                    //             onFalse: 'Не упаковано'
                    //         });
                    //     }
                    // },
                    // {
                    //     title: 'На СГП',
                    //     field: 'shipmentDate',
                    //     resizable: false,
                    //     hozAlign: 'center',
                    //     formatter: () => {
                    //         // TODO вернуться и доделать на правильную логику после реализации СКЛАДА
                    //         return '';
                    //     }
                    // },
                    // {
                    //     title: 'Отгружено',
                    //     field: 'shipmentDate',
                    //     hozAlign: 'center',
                    //     formatter: cell => dateStdToString(cell.getValue())
                    // },
                    // {
                    //     title: 'Комментарий',
                    //     field: 'comment',
                    //     variableHeight: true,
                    //     minWidth: 80,
                    //     width: 180,
                    //     formatter: cell => {
                    //         $(cell.getElement()).css({'white-space': 'pre-wrap'});
                    //         return cell.getValue();
                    //     }
                    // }
                ],
                rowClick: (e, row) => {
                    row.select();
                    $btnSelect.toggleClass('disabled', !table.getSelectedRows().length);
                }
            });

            const table = new Tabulator('div.generate-file_data__table_second', {
                pagination: 'remote',
                ajaxURL: ACTION_PATH.DETAIL_DOCUMENTATION_FORMED_WITH_PAYMENT_INFO_LOAD,
                ajaxRequesting: (url, params) => {
                    params.sectionId = sectionId;
                    params.documentType = documentType;
                },
                selectable: true,
                ajaxSorting: true,
                maxHeight: '250px',
                layout: 'fitColumns',
                columns: [
                    TABR_COL_REMOTE_ROW_NUM,
                    { title: 'Номер п/п', field: TABR_FIELD.NUMBER },
                    { title: 'Дата', field: TABR_FIELD.DATE },
                    { title: 'Сумма', field: TABR_FIELD.AMOUNT }
                ],
                rowClick: (e, row) => {
                    row.select();
                    $btnSelect.toggleClass('disabled', !table.getSelectedRows().length);
                }
            });

            $btnSelect.on({
                'click': () => {
                    const selectedPaymentRow = table.getSelectedRows();
                    const selectedAllotmentRow = tableAllotment.getSelectedRows();
                    const paymentIdList = [];
                    const allotmentIdList = [];
                    selectedPaymentRow.forEach(row => paymentIdList.push(row.getData().id));
                    selectedAllotmentRow.forEach(row => allotmentIdList.push(row.getData().id));
                    $.post({
                        url: ACTION_PATH.DETAIL_DOCUMENTATION_FORMED_WITH_ALLOTMENT_INFO_SAVE,
                        data: {
                            paymentIdList: paymentIdList.join(),
                            allotmentIdList: allotmentIdList.join(),
                            sectionId: sectionId,
                            documentType: documentType
                        }
                    }).done(() => {
                        $modal.modal('hide');
                        $('.detail__menu_documentation').trigger('click');
                    });
                }
            });
        } else {
            $('div.generate-file_table-name').text('Счета на оплату');
            const table = new Tabulator('div.generate-file_data__table', {
                pagination: 'remote',
                ajaxURL: ACTION_PATH.DETAIL_DOCUMENTATION_FORMED_WITH_INVOICE_INFO_LOAD,
                ajaxRequesting: (url, params) => {
                    params.sectionId = sectionId;
                    params.documentType = documentType;
                },
                selectable: 1,
                ajaxSorting: true,
                maxHeight: '450px',
                layout: 'fitColumns',
                columns: [
                    TABR_COL_REMOTE_ROW_NUM,
                    { title: 'Номер', field: TABR_FIELD.NUMBER },
                    { title: 'Дата', field: TABR_FIELD.CREATE_DATE },
                    { title: 'Действителен до', field: TABR_FIELD.DATE_VALID_BEFORE },
                    { title: 'Сумма', field: TABR_FIELD.AMOUNT },
                    { title: 'Оплачено', field: TABR_FIELD.PAID_AMOUNT }
                ],
                rowClick: (e, row) => {
                    table.deselectRow();
                    row.select();
                    $btnSelect.toggleClass('disabled', !table.getSelectedRows().length);
                },
                rowDblClick: (e, row) => {
                    table.deselectRow();
                    row.select();
                    $btnSelect.trigger('click');
                    table.deselectRow();
                }
            });

            $btnSelect.on({
                'click': () => {
                    const selectedRow = table.getSelectedRows();
                    const invoiceId = selectedRow[0].getData().id;
                    $.post({
                        url: ACTION_PATH.DETAIL_DOCUMENTATION_FORMED_WITH_INVOICE_INFO_SAVE,
                        data: { invoiceId: invoiceId, sectionId: sectionId, documentType: documentType }
                    }).done(() => {
                        $modal.modal('hide');
                        $('.detail__menu_documentation').trigger('click');
                    });
                }
            });
        }
    });
</script>