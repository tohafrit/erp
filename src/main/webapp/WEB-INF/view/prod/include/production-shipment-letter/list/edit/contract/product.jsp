<div class="ui modal fullscreen list_edit_contract_product__main">
    <div class="header">Выберите изделия. Договор № ${sectionFullNumber}</div>
    <div class="content">
        <div class="list_edit_contract_product_buttons">
            <i class="icon filter link blue list_edit_contract_product__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
            <i class="icon expand alternate link blue list_edit_contract_product__btn-expand" title="Развернуть"></i>
            <i class="icon compress alternate link blue list_edit_contract_product__btn-compress" title="Свернуть"></i>
        </div>
        <div class="list_edit_contract_product__table table-sm table-striped"></div>
    </div>
    <div class="actions">
        <button class="ui small button disabled list_edit_contract_product__btn-select">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.select"/>
        </button>
    </div>
</div>

<script>
    $(() => {
        const contractSectionId = '${contractSectionId}';
        //
        const $modal = $('div.list_edit_contract_product__main');
        const $btnFilter = $('i.list_edit_contract_product__btn-filter');
        const $btnSelect = $('button.list_edit_contract_product__btn-select');
        const $table = $('div.list_edit_contract_product__table');
        const $btnExpand = $('i.list_edit_contract_product__btn-expand');
        const $btnCompress = $('i.list_edit_contract_product__btn-compress');
        const $parentEditModal = $('div.list_edit__main');
        const $allotmentIdList = $parentEditModal.find('input#allotmentIdList');
        const $parentContractModal = $('div.list_edit_contract__main');
        const specTabulator = Tabulator.prototype.findTable('div.list_edit__product-table')[0]

        // Параметры фильтра таблицы
        const tableData = tableDataFromUrlQuery(window.location.search);
        let filterData = tableData.filterData;

        const table = new Tabulator('div.list_edit_contract_product__table', {
            ajaxURL: ACTION_PATH.LIST_EDIT_CONTRACT_PRODUCT_LOAD,
            ajaxRequesting: (url, params) => {
                params.filterData = JSON.stringify(filterData);
                params.contractSectionId = contractSectionId;
            },
            selectable: true,
            headerSort: false,
            ajaxSorting: true,
            height: 'calc(100vh * 0.5)',
            groupBy: [ TABR_FIELD.GROUP_MAIN, TABR_FIELD.GROUP_SUB_MAIN ],
            groupStartOpen: [ true, true ],
            groupToggleElement: 'header',
            layout: 'fitColumns',
            selectableCheck: row => row.getData().cannotAddedLetter,
            groupHeader: [
                value => '<span style="color:#315c83;">' + value + '</span>',
                value => value,
            ],
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                {
                    title: 'Кол-во',
                    field: TABR_FIELD.ALLOTMENT_AMOUNT,
                    hozAlign: 'center'
                },
                {
                    title: 'Стоимость',
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
                        },
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
                {
                    title: 'Запущено',
                    field: TABR_FIELD.LAUNCH_AMOUNT,
                    hozAlign: 'center'
                },
                {
                    title: 'Запуск',
                    field: TABR_FIELD.LAUNCH_NUMBER,
                    hozAlign: 'center'
                },
                {
                    title: 'Письмо на производство',
                    columns:[
                        {
                            title: 'Номер',
                            field: TABR_FIELD.LETTER_NUMBER,
                            hozAlign: 'center'
                        },
                        {
                            title: 'Дата',
                            field: TABR_FIELD.LETTER_CREATION_DATE,
                            hozAlign: 'center',
                            formatter: 'stdDate'
                        },
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
                {
                    title: 'ОТК',
                    field: TABR_FIELD.TRANSFER_FOR_WRAPPING_DATE,
                    resizable: false,
                    hozAlign: 'center',
                    formatter: cell => {
                        const date = dateStdToString(cell.getValue());
                        return booleanToLight(cell.getValue() != null, { onTrue: 'Дата прохождения ' + date, onFalse: 'ОТК не пройден' });
                    }
                },
                {
                    title: 'Упаковка',
                    field: TABR_FIELD.READY_FOR_SHIPMENT_DATE,
                    resizable: false,
                    hozAlign: 'center',
                    formatter: cell => {
                        const date = dateStdToString(cell.getValue());
                        return booleanToLight(cell.getValue() != null, { onTrue: 'Дата упаковки ' + date, onFalse: 'Не упаковано' });
                    }
                },
                {
                    title: 'На СГП',
                    field: TABR_FIELD.SHIPMENT_DATE,
                    resizable: false,
                    hozAlign: 'center',
                    formatter: () => {
                        // TODO вернуться и переделать на правильную логику после реализации СКЛАДА
                        // let shipmentDate = cell.getValue();
                        // const date = dateStdToString(shipmentDate);
                        // if (shipmentDate != null) {
                        //     return booleanToLight(false, {onTrue: '', onFalse: 'Отгружено ' + date})
                        // } else if (cell.getRow().getData().readyForShipmentDate == null) {
                        //     return booleanToLight(false, {onTrue: '', onFalse: 'Не поступало'})
                        // } else {
                        //     if (shipmentDate == null) {
                        //         return booleanToLight(cell.getRow().getData().isReceivedByMSN, {onTrue: 'Получено по МСН', onFalse: ''})
                        //     }
                        // }
                        return '';
                    }
                },
                {
                    title: 'Отгружено',
                    columns: [
                        {
                            title: 'Кол-во',
                            field: TABR_FIELD.ALLOTMENT_SHIPPED_AMOUNT,
                            hozAlign: 'center'
                        },
                        {
                            title: 'Дата',
                            field: TABR_FIELD.SHIPMENT_DATE,
                            hozAlign: 'center',
                            formatter: 'stdDate'
                        },
                    ]
                },
                {
                    title: 'Комментарий',
                    field: TABR_FIELD.COMMENT,
                    variableHeight: true,
                    minWidth: 80,
                    width: 180,
                    formatter: cell => {
                        $(cell.getElement()).css({'white-space': 'pre-wrap'});
                        return cell.getValue();
                    }
                },
                { title: '', field: 'editGroupMain', visible: false },
                { title: '', field: 'editGroupSubMain', visible: false }
            ],
            rowSelected: () => $btnSelect.toggleClass('disabled', !table.getSelectedRows().length),
            rowDeselected: () => $btnSelect.toggleClass('disabled', !table.getSelectedRows().length)
        });

        // Добавление стилей для переноса слов заголовка и выравнивание заголовков по центру
        $table.find('.tabulator-col').css({
            'word-wrap': 'break-word',
            'text-align': 'center'
        });
        $table.find('.tabulator-col-title').css({
            'white-space': 'pre-wrap',
            'text-overflow': 'clip'
        });

        // Кнопка свернуть группы
        $btnCompress.on({
            'click': () => table.getGroups().forEach(group => group.hide())
        });
        // Кнопка развернуть группы
        $btnExpand.on({
            'click': () => table.getGroups().forEach(group => group.show())
        });

        // Фильтр
        $.modalFilter({
            url: VIEW_PATH.LIST_EDIT_CONTRACT_PRODUCT_FILTER,
            button: $btnFilter,
            filterData: () => filterData,
            onApply: data => {
                filterData = data;
                table.setData();
            }
        });

        // Кнопка выбрать
        $btnSelect.on({
            'click': () => {
                const data = table.getSelectedData();
                if (data.length) {
                    const arr = [];
                    data.forEach(el => arr.push({
                        id: el.id,
                        productName: el.productName,
                        groupMain: el.editGroupMain,
                        groupSubMain: el.editGroupSubMain,
                        deliveryDate: el.deliveryDate,
                        allotmentAmount: el.allotmentAmount,
                        cost: el.cost,
                        paid: el.paid,
                        percentPaid: el.percentPaid,
                        finalPrice: el.finalPrice,
                        launchAmount: el.launchAmount,
                        launchNumber: el.launchNumber
                    }));
                    specTabulator.addData(arr);
                    specTabulator.clearSort();
                    $modal.modal('hide');
                    $parentContractModal.modal('hide');
                }
            }
        });
    })
</script>