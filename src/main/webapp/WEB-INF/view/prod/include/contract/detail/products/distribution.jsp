<div class="detail_products_distribution__header">
    <h1 class="detail_products_distribution__header_title">
        ${conditionalName}, ${lotAmount} шт., поставка <javatime:format value="${deliveryDate}" pattern="dd.MM.yyyy"/>
    </h1>
</div>
<div class="detail_products_distribution__table-wrap">
    <div class="detail_products_distribution__table"></div>
</div>

<script>
    $(() => {
        const lotId = '${lotId}';
        const scroll = '${scroll}' === 'true';
        const $table = $('div.detail_products_distribution__table');
        const specTabulator = Tabulator.prototype.findTable('div.detail_products__table')[0];

        const table = new Tabulator('div.detail_products_distribution__table', {
            selectable: 1,
            headerSort: false,
            layout: 'fitColumns',
            ajaxURL: '/api/action/prod/contract/detail/products/distribution/load',
            ajaxRequesting: (url, params) => {
                params.lotId = lotId;
            },
            ajaxResponse: (url, params, response) => {
                if (scroll) {
                    setTimeout(() => specTabulator.scrollToRow(lotId, 'top', false), 100);
                }
                return response;
            },
            height: '100%',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                {
                    title: 'Кол-во',
                    field: 'allotmentAmount',
                    hozAlign: 'center',
                },
                {
                    title: 'Оплачено',
                    columns: [
                        {
                            title: 'руб.',
                            field: 'paid',
                            hozAlign: 'center',
                            formatter: 'stdMoney'
                        },
                        {
                            title: '%',
                            field: 'percentPaid',
                            hozAlign: 'center',
                            formatter: cell => {
                                return cell.getValue();
                            }
                        }
                    ]
                },
                {
                    title: 'Счет на ОО',
                    field: 'finalPrice',
                    resizable: false,
                    hozAlign: 'center',
                    formatter: cell => {
                        return booleanToLight(cell.getValue() > 0);
                    }
                },
                {
                    title: 'Запуск',
                    field: 'launchNumber',
                    hozAlign: 'center',
                },
                {
                    title: 'Письмо на пр-во',
                    columns: [
                        {
                            title: 'Номер',
                            field: 'letterNumber',
                            hozAlign: 'center',
                        },
                        {
                            title: 'Дата',
                            field: 'letterCreationDate',
                            hozAlign: 'center',
                            formatter: cell => dateStdToString(cell.getValue())
                        }
                    ]
                },
                {
                    title: 'Распор-ие на отгрузку',
                    field: 'shipmentPermitDate',
                    resizable: false,
                    hozAlign: 'center',
                    formatter: cell => {
                        const date = dateStdToString(cell.getValue());
                        return booleanToLight(cell.getValue() != null, { onTrue: 'Распоряжение выдано ' + date, onFalse: 'Отгрузка не разрешена' });
                    }
                },
                {
                    title: 'ОТК',
                    field: 'transferForWrappingDate',
                    resizable: false,
                    hozAlign: 'center',
                    formatter: cell => {
                        const date = dateStdToString(cell.getValue());
                        return booleanToLight(cell.getValue() != null, { onTrue: 'Дата прохождения ' + date, onFalse: 'ОТК не пройден' });
                    }
                },
                {
                    title: 'Упаковка',
                    field: 'readyForShipmentDate',
                    resizable: false,
                    hozAlign: 'center',
                    formatter: cell => {
                        const date = dateStdToString(cell.getValue());
                        return booleanToLight(cell.getValue() != null, { onTrue: 'Дата упаковки ' + date, onFalse: 'Не упаковано' });
                    }
                },
                {
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
                    columns: [
                        {
                            title: 'Кол-во',
                            field: 'allotmentShippedAmount',
                            hozAlign: 'center'
                        },
                        {
                            title: 'Дата',
                            field: 'shipmentDate',
                            hozAlign: 'center',
                            formatter: 'stdDate'
                        }
                    ]
                },
                {
                    title: 'Цена',

                    field: 'neededPrice',
                    hozAlign: 'center',
                    formatter: 'stdMoney'
                },
                {
                    title: 'Вид цены',
                    field: 'priceKind',
                    hozAlign: 'center',
                },
                {
                    title: 'Комментарий',
                    field: 'comment',
                    variableHeight: true,
                    minWidth: 80,
                    width: 180,
                    formatter: cell => {
                        $(cell.getElement()).css({'white-space': 'pre-wrap'});
                        return cell.getValue();
                    }
                }
            ],
            rowContext: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowContextMenu: (component) => {
                const menu = [];
                const allotmentId = component.getData().id;
                menu.push({
                    label: `<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>`,
                    // TODO в работе
                    action: () => editPartDelivery(allotmentId)
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    // TODO в работе
                    action: () => deletePartDelivery(allotmentId)
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
            'white-space': 'normal',
            'text-overflow': 'clip'
        });
    });
</script>