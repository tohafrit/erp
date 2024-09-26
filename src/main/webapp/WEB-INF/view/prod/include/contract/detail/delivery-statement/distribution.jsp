<div class="detail_delivery-statement_distribution__header">
    <div class="detail_delivery-statement_distribution__header_buttons">
        <i class="icon chart pie link blue detail_delivery-statement_distribution__btn-split" title="Разделить"></i>
        <i class="icon compress arrows alternate link blue detail_delivery-statement_distribution__btn-unite" title="Объединить"></i>
    </div>
    <h1 class="detail_delivery-statement_distribution__header_title">
        ${conditionalName}, ${lotAmount} шт., поставка <javatime:format value="${deliveryDate}" pattern="dd.MM.yyyy"/>
    </h1>
</div>
<div class="detail_delivery-statement_distribution__table-wrap">
    <div class="detail_delivery-statement_distribution__table table-sm"></div>
</div>

<script>
    $(() => {
        const lotId = '${lotId}';
        const $table = $('div.detail_delivery-statement_distribution__table');
        const $btnSplit = $('i.detail_delivery-statement_distribution__btn-split');
        const $btnUnite = $('i.detail_delivery-statement_distribution__btn-unite');
        const specTabulator = Tabulator.prototype.findTable('div.detail_delivery-statement__table')[0];

        const table = new Tabulator('div.detail_delivery-statement_distribution__table', {
            ajaxURL: ACTION_PATH.DETAIL_DELIVERY_STATEMENT_DISTRIBUTION_LOAD,
            selectable: true,
            headerSort: false,
            layout: 'fitColumns',
            ajaxRequesting: (url, params) => {
                params.lotId = lotId;
            },
            height: '100%',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: 'Кол-во', field: TABR_FIELD.ALLOTMENT_AMOUNT, hozAlign: 'center' },
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
                { title: 'Запуск', field: TABR_FIELD.LAUNCH_NUMBER, hozAlign: 'center' },
                {
                    title: 'Письмо на пр-во',
                    columns: [
                        { title: 'Номер', field: TABR_FIELD.LETTER_NUMBER, hozAlign: 'center' },
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
                {
                    // TODO жду от Сани К. правильноую логику вывода "ОТК"
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
                    // TODO жду от Сани К. правильноую логику вывода "Упаковки"
                    title: 'Упаковка',
                    field: TABR_FIELD.TRANSFER_FOR_WRAPPING_DATE,
                    resizable: false,
                    hozAlign: 'center',
                    formatter: cell => {
                        const date = dateStdToString(cell.getValue());
                        return booleanToLight(cell.getValue() != null, { onTrue: 'Дата упаковки ' + date, onFalse: 'Не упаковано' });
                    }
                },
                {
                    // TODO жду от Сани К. правильноую логику вывода "На СГП"
                    title: 'На СГП',
                    field: 'shipmentDate',
                    resizable: false,
                    hozAlign: 'center',
                    formatter: () => {
                        // TODO вернуться и доделать на правильную логику после реализации СКЛАДА
                        return '';
                    }
                },
                {
                    title: 'Отгружено',
                    field: TABR_FIELD.SHIPPED,
                    hozAlign: 'center'
                }
            ],
            rowContext: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowContextMenu: (row) => {
                const menu = [];
                const data = row.getData();
                const allotmentId = data.id;
                if (data.canAdd) {
                    menu.push({
                        label: `<i class="rocket icon blue"></i>Добавить в запуск`,
                        action: () => editProductsLaunch(allotmentId)
                    });
                }
                if (data.canEdit) {
                    menu.push({
                        label: `<i class="edit icon blue"></i>Редактировать запуск`,
                        action: () => editProductsLaunch(allotmentId)
                    });
                    menu.push({
                        label: `<i class="trash alternate outline icon blue"></i>Удалить из запуска`,
                        action: () => removeFromLaunch(allotmentId)
                    });
                }
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
            'white-space': 'normal',
            'text-overflow': 'clip'
        });

        // Функция добавления изделия в запуск / изменения запуска для изделий
        function editProductsLaunch(id) {
            $.modalWindow({
                loadURL: VIEW_PATH.DETAIL_DELIVERY_STATEMENT_DISTRIBUTE_EDIT_PRODUCTS_LAUNCH,
                loadData: { id: id },
            });
        }

        // Удаление изделий из запуска
        function removeFromLaunch(id) {
            confirmDialog({
                title: 'Удаление изделий из запуска',
                message: 'Вы действительно хотите убрать изделия из запуска?',
                onAccept: () => $.post({
                    url: ACTION_PATH.DETAIL_DELIVERY_STATEMENT_DISTRIBUTION_EDIT_PRODUCTS_LAUNCH_DELETE,
                    data: { id: id },
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => {
                    specTabulator.setData();
                    table.setData();
                })
            });
        }

        // Функция разделения части поставки
        function splitPartSupply() {
            let selectedRows = table.getSelectedRows();
            let allotmentIdList = [];
            selectedRows.forEach(row => allotmentIdList.push(row.getData().id));
            $.modalWindow({
                loadURL: VIEW_PATH.DETAIL_DELIVERY_STATEMENT_DISTRIBUTE_SPLIT,
                loadData: { allotmentIdList: allotmentIdList.join() },
            });
        }

        // Функция объединения частей поставки
        function unitePartSupply() {
            let selectedRows = table.getSelectedRows();
            let allotmentIdList = [];
            selectedRows.forEach(row => allotmentIdList.push(row.getData().id));
            confirmDialog({
                title: 'Объединение частей поставки',
                message: 'Вы действительно хотите объединить выбранные части поставки?',
                onAccept: () => $.post({
                    url: ACTION_PATH.DETAIL_DELIVERY_STATEMENT__DISTRIBUTE_UNITE,
                    data: { allotmentIdList: allotmentIdList.join() },
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setData())
            });
        }

        // Кнопка разделения части поставки
        $btnSplit.on({
            'click': () => splitPartSupply()
        });

        // Кнопка объединения частей поставки
        $btnUnite.on({
            'click': () => unitePartSupply()
        });
    });
</script>