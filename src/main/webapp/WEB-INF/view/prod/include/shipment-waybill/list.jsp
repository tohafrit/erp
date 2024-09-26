<div class="list__header">
    <h1 class="list__header_title">Отгрузка ГП</h1>
    <div class="list__header_buttons">
        <i class="icon filter link blue list__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        <i class="icon add link blue list__btn-add" title="<fmt:message key="label.button.add"/>"></i>
    </div>
</div>
<div class="list__tables-block">
    <div class="list__table-block">
        <div class="list__table-wrap">
            <div class="list__table table-sm table-striped"></div>
        </div>
    </div>
    <div class="list__sub-block">
        <i class="close link blue icon list__btn-close-sub-block"></i>
        <div class="list__sub-block-content"></div>
    </div>
</div>

<script>
    $(() => {
        const tableSelector = 'div.list__table';
        const $btnFilter = $('i.list__btn-filter');
        const $btnAdd = $('i.list__btn-add');
        const $subBlock = $('div.list__sub-block');
        const $subContent = $('div.list__sub-block-content');
        const $btnCloseSubBlock = $('i.list__btn-close-sub-block');
        // Параметры фильтра таблицы
        const tableData = tableDataFromUrlQuery(window.location.search);
        let filterData = tableData.filterData;

        let notInitLoad = false;
        const table = new Tabulator(tableSelector, {
            pagination: 'remote',
            paginationInitialPage: tableData.page,
            paginationSize: tableData.size,
            initialSort: tableData.sort.length ? tableData.sort : [{ column: TABR_FIELD.CREATE_DATE, dir: SORT_DIR_DESC }],
            ajaxURL: ACTION_PATH.LIST_LOAD,
            ajaxRequesting: (url, params) => {
                params.filterData = JSON.stringify(filterData);
                if (notInitLoad) {
                    const query = urlQueryFromTableParams(window.location.search, params);
                    sessionStorage.setItem(S_STORAGE.LIST_QUERY, query);
                    page.show(ROUTE.list(query), undefined, false);
                }
                $subBlock.hide();
                notInitLoad = true;
            },
            ajaxSorting: true,
            height: '100%',
            layout: 'fitDataFill',
            rowFormatter: row => {
                const data = row.getData();
                const $cont = $('<div></div>');
                $cont.addClass(TABR_CLASS_ROW_TOGGLE_HOLDER);
                const $table = $('<table></table>');
                const rowCreate = (titleFirst, valueFirst, titleSecond, valueSecond) =>
                    '<tr><td><strong>' + titleFirst + '</strong></td><td>' + (valueFirst ? valueFirst : '') + '</td><td><strong>' + titleSecond + '</strong></td><td>' + (valueSecond ? valueSecond : '') + '</td></tr>';
                $table.append(rowCreate('Договор', data.contract, 'Всего по накладной', formatAsCurrency(data.totalWoVat)));
                $table.append(rowCreate('Расчетный счет', data.account, 'Всего с учетом НДС', formatAsCurrency(data.totalVat)));
                $table.append(rowCreate('Плательщик', data.payer, 'НДС', data.vat));
                $table.append(rowCreate('Грузополучатель', data.consignee, 'Отпуск произвел', data.giveUser));
                $table.append(rowCreate('Сопроводительное письмо', data.transmittalLetter, 'Отпуск разрешил', data.permitUser));
                $table.append(rowCreate('Получил', data.receiver, 'Главный бухгалтер', data.accountantUser));
                $table.append(rowCreate('Доверенность', data.letterOfAttorney, '', ''));
                $cont.css({
                    'box-sizing': 'border-box',
                    'padding': '5px',
                    'border-top': '1px solid #dee2e6',
                    'display': 'none'
                });
                $table.find('td').css({ 'padding': '0 10px 0 10px' });
                $cont.append($table);
                row.getElement().appendChild($cont.get(0));
            },
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                TABR_COL_ID,
                {
                    width: 40,
                    minWidth: 40,
                    hozAlign: 'center',
                    resizable: false,
                    headerSort: false,
                    formatter: 'rowToggle'
                },
                {
                    title: 'Накладная',
                    field: TABR_FIELD.NUMBER,
                    hozAlign: 'center',
                    width: 110,
                    resizable: false
                },
                {
                    title: 'Дата создания',
                    field: TABR_FIELD.CREATE_DATE,
                    hozAlign: 'center',
                    width: 130,
                    resizable: false,
                    formatter: 'stdDate'
                },
                {
                    title: 'Дата отгрузки',
                    field: TABR_FIELD.SHIPMENT_DATE,
                    hozAlign: 'center',
                    width: 130,
                    resizable: false,
                    formatter: 'stdDate'
                },
                {
                    title: 'Комментарий',
                    field: TABR_FIELD.COMMENT,
                    variableHeight: true,
                    minWidth: 300,
                    formatter: 'textarea',
                    headerSort: false
                }
            ],
            rowClick: (e, row) => {
                const data = row.getData();
                table.deselectRow();
                row.select();
                showMatValue(data.id);
            },
            rowContext: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowContextMenu: row => {
                const menu = [];
                const data = row.getData();
                const id = data.id;
                menu.push({
                    label: '<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>',
                    action: () => editRecord(id)
                });
                menu.push({
                    label: '<i class="file word blue icon"></i>Сформировать акт',
                    action: () => window.open('/warehouse-documentation-formed/download?type=' + 31 + '&id=' + id, '_blank')
                });
                menu.push({
                    label: '<i class="receipt blue icon"></i>Сформировать накладную для ОКР',
                    action: () => window.open('/warehouse-documentation-formed/download?type=' + 30 + '&id=' + id, '_blank')
                });
                menu.push({
                    label: '<i class="tasks blue icon"></i>Проверить список отгрузки',
                    action: () => checkShipment(id)
                });
                if (data.canShipment) {
                    menu.push({
                        label: '<i class="check icon blue"></i>Отгрузить изделия',
                        action: () => shipmentRecord(id, true)
                    });
                } else {
                    menu.push({
                        label: '<i class="times icon blue"></i>Отменить отгрузку изделий',
                        action: () => shipmentRecord(id, false)
                    });
                }
                menu.push({
                    label: '<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>',
                    action: () => deleteRecord(id)
                });
                return menu;
            }
        });

        // Скролл до строки и ее выбор по id
        function rowScrollSelect(id) {
            table.selectRow(id);
            table.scrollToRow(id, 'middle', false);
        }

        // Редактирование/добавление
        function editRecord(id) {
            $.modalWindow({
                loadURL: VIEW_PATH.LIST_EDIT,
                loadData: { id: id },
                submitURL: ACTION_PATH.LIST_EDIT_SAVE,
                submitAsJson: true,
                onSubmitSuccess: resp => {
                    if (id) {
                        table.setPage(table.getPage()).then(() => rowScrollSelect(id));
                    } else {
                        const id = resp.attributes.id;
                        if (id) {
                            filterData = {};
                            table.setSort(TABR_SORT_ID_DESC);
                            table.setPage(1).then(() => rowScrollSelect(id));
                        }
                    }
                }
            });
        }

        // Удаление
        function deleteRecord(id) {
            confirmDialog({
                title: 'Удаление накладной',
                message: 'Вы действительно хотите удалить накладную?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: ACTION_PATH.LIST_DELETE + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setPage(table.getPage()))
            });
        }

        // Принятие/снятие принятия
        function shipmentRecord(id, toShipment) {
            if (toShipment) $.modalWindow({
                loadURL: VIEW_PATH.LIST_SHIPMENT,
                loadData: { id: id },
                submitURL: ACTION_PATH.LIST_SHIPMENT_APPLY,
                submitAsJson: true,
                onSubmitSuccess: () => table.setPage(table.getPage()).then(() => rowScrollSelect(id))
            })
            else confirmDialog({
                title: 'Отмена отгрузки',
                message: 'Вы действительно хотите отменить отгрузку?',
                onAccept: () => $.post({
                    url: ACTION_PATH.LIST_UNSHIPMENT,
                    data: { id: id },
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setPage(table.getPage()).then(() => rowScrollSelect(id)))
            })
        }

        // Проверка списка отгрузки
        function checkShipment(id) {
            $.modalWindow({
                loadURL: VIEW_PATH.LIST_CHECK_SHIPMENT,
                loadData: { id: id }
            });
        }

        // Отображение данных накладной
        function showMatValue(id) {
            $subContent.html('');
            $subBlock.show();
            setTimeout(() => table.scrollToRow(id, 'middle', false), 200);
            $.get({
                url: VIEW_PATH.LIST_MAT_VALUE,
                data: { id: id }
            }).done(html => $subContent.html(html));
        }

        // Скрытие области вспомогательного контейнера
        $btnCloseSubBlock.on({
            'click': () => {
                $subContent.html('');
                $subBlock.hide();
            }
        });

        // Фильтр
        $.modalFilter({
            url: VIEW_PATH.LIST_FILTER,
            button: $btnFilter,
            filterData: () => filterData,
            onApply: data => {
                filterData = data;
                table.setData();
            }
        });

        // Добавление
        $btnAdd.on({
            'click': () => editRecord()
        });

        // Обновление таблицы
        tableTimerUpdate({
            selector: tableSelector,
            url: ACTION_PATH.LIST_LOAD,
            filterData: () => filterData
        });
    })
</script>