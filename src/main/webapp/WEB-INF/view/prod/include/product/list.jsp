<div class="list__container">
    <div class="list__header">
        <h1 class="list__header_title"><fmt:message key="product.list.title"/></h1>
        <div class="list__header_buttons">
            <i class="ui fas fa-tasks icon list__btn-column-trigger" title="Видимость колонок">
                <div class="ui compact message hidden list__trigger-column-container"></div>
            </i>
            <i class="icon filter link blue list__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
            <i class="icon add link blue list__btn-add-product" title="Добавить изделие"></i>
            <i class="ui icon blue layer group dropdown list__btn-reports" title="Отчеты">
                <div class="menu">
                    <div class="item">
                        ЗС для запуска
                    </div>
                    <div class="item">
                        Проверка ввода допустимых замен по закупке
                    </div>
                    <div class="item">
                        Статус введенных допустимых замен
                    </div>
                </div>
            </i>
        </div>
    </div>
    <div class="list__table-block">
        <div class="list__table table-sm table-striped"></div>
    </div>

    <script>
        $(() => {
            const $btnFilter = $('i.list__btn-filter');
            const $btnAddProduct = $('i.list__btn-add-product');
            const $btnReports = $('i.list__btn-reports');
            const $btnReportItems = $btnReports.find('div.item');
            const $btnTrigger = $('i.list__btn-column-trigger');
            const $triggerColumnContainer = $btnTrigger.find('div.list__trigger-column-container');
            // Параметры фильтра таблицы
            const tableData = tableDataFromUrlQuery(window.location.search);
            let filterData = tableData.filterData;

            const columnMap = new Map();
            columnMap.set('conditionalName',        '<fmt:message key="product.field.conditionalName"/>');
            columnMap.set('techSpecName',           '<fmt:message key="product.field.techSpecName"/>');
            columnMap.set('type',                   '<fmt:message key="product.field.type"/>');
            columnMap.set('decimalNumber',          '<fmt:message key="product.field.decimalNumber"/>');
            columnMap.set('letter',                 '<fmt:message key="product.field.letter"/>');
            columnMap.set('archiveDate',            '<fmt:message key="product.field.status"/>');
            columnMap.set('position',               '<fmt:message key="product.field.position"/>');
            columnMap.set('lead',                   '<fmt:message key="product.field.lead"/>');
            columnMap.set('classificationGroup',    '<fmt:message key="product.field.classificationGroup"/>');
            columnMap.set('comment',                '<fmt:message key="product.field.comment"/>');

            // Дефолтные колонки
            const numeration = [TABR_COL_REMOTE_ROW_NUM, TABR_COL_ID];

            const defaultColumns = {
                conditionalName: {
                    title: columnMap.get('conditionalName'),
                    field: 'conditionalName',
                    variableHeight: true,
                    minWidth: 100,
                    width: 200,
                    formatter: 'textarea'
                },
                techSpecName: {
                    title: columnMap.get('techSpecName'),
                    field: 'techSpecName',
                    variableHeight: true,
                    minWidth: 100,
                    width: 200,
                    formatter: 'textarea'
                },
                type: { title: columnMap.get('type'), field: 'type' },
                decimalNumber: { title: columnMap.get('decimalNumber'), field: 'decimalNumber' },
                letter: { title: columnMap.get('letter'), field: 'letter', hozAlign: 'center' },
                archiveDate: {
                    title: columnMap.get('archiveDate'),
                    field: 'archiveDate',
                    resizable: false,
                    headerSort: false,
                    hozAlign: 'center',
                    formatter: cell => {
                        const date = dateTimeStdToString(cell.getValue());
                        return booleanToLight(cell.getValue() == null, { onTrue: 'Активен', onFalse: 'Устаревший с ' + date });
                    }
                },
                position: { title: columnMap.get('position'), field: 'position' },
                lead: { title: columnMap.get('lead'), field: 'lead' },
                classificationGroup: { title: columnMap.get('classificationGroup'), field: 'classificationGroup' },
                comment: {
                    title: columnMap.get('comment'),
                    field: 'comment',
                    variableHeight: true,
                    minWidth: 200,
                    width: 300,
                    formatter: 'textarea'
                }
            };

            // Таблица изделий
            let notInitLoad = false;
            const table = new Tabulator('div.list__table', {
                pagination: 'remote',
                paginationInitialPage: tableData.page,
                paginationSize: tableData.size,
                initialSort: tableData.sort.length ? tableData.sort : [],
                ajaxURL: ACTION_PATH.LIST_LOAD,
                ajaxRequesting: (url, params) => {
                    params.filterData = JSON.stringify(filterData);
                    if (notInitLoad) {
                        const query = urlQueryFromTableParams(window.location.search, params);
                        sessionStorage.setItem(S_STORAGE.LIST_QUERY, query);
                        page.show(ROUTE.list(query), undefined, false);
                    }
                    notInitLoad = true;
                },
                ajaxSorting: true,
                height: '100%',
                columns: numeration.concat(Object.values(defaultColumns)),
                rowClick: (e, row) => {
                    table.deselectRow();
                    row.select();
                },
                rowContext: (e, row) => {
                    table.deselectRow();
                    row.select();
                },
                rowContextMenu: row => {
                    const menu = [];
                    const data = row.getData();
                    menu.push({
                        label: `<i class="book open icon blue link"></i><fmt:message key="label.menu.open"/>`,
                        action: () => page('/detail/' + data.id + '/general')
                    });
                    menu.push({
                        label: `<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>`,
                        action: () => editProduct(data.id)
                    });
                    menu.push({
                        label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                        action: () => deleteProduct(data.id)
                    });
                    return menu;
                },
                tableBuilt: () => {
                    const columnNames = [];
                    for (key of Object.keys(defaultColumns)) {
                        if ('columns' in defaultColumns[key]) {
                            for (item of defaultColumns[key].columns) {
                                columnNames.push(item.field);
                            }
                        } else {
                            columnNames.push(key);
                        }
                    }
                    const data = {
                        tableId: 'component',
                        columnNameList: columnNames.join(',')
                    };
                    $.get('/column/list/ops/load', data)
                        .done(data => {
                            const orderColumns = numeration;
                            let toggleColumns = '<div class="ui relaxed divided list link">';
                            let groupColumn = {};
                            $.each(data, function (k,v) {
                                const clazz = 'item' + (v.toggle ? ' active' : '');
                                toggleColumns += '<a data-name="' + v.name + '" class="' + clazz + '">' + columnMap.get(v.name) + '</a>';
                                let column = defaultColumns[v.name];
                                if (column === undefined) {
                                    if (Object.keys(groupColumn).length === 0) {
                                        for (key of Object.keys(defaultColumns)) {
                                            if ('columns' in defaultColumns[key]) {
                                                for (itemKey in defaultColumns[key].columns) {
                                                    if (v.name === defaultColumns[key].columns[itemKey].field) {
                                                        defaultColumns[key].columns[itemKey].visible = v.toggle;
                                                        groupColumn = defaultColumns[key];
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        for (itemKey in groupColumn.columns) {
                                            if (v.name === groupColumn.columns[itemKey].field) {
                                                groupColumn.columns[itemKey].visible = v.toggle;
                                            }
                                        }
                                    }
                                } else {
                                    if (Object.keys(groupColumn).length !== 0) {
                                        orderColumns.push(groupColumn);
                                        groupColumn = {};
                                    }
                                    column.visible = v.toggle;
                                    orderColumns.push(column);
                                }
                            });
                            toggleColumns += '</div>';
                            $triggerColumnContainer.html(toggleColumns);
                            table.setColumns(orderColumns);

                            $triggerColumnContainer.find('.item').on({
                                'click': function (event) {
                                    event.stopPropagation();
                                    const $name = $(this).data('name');
                                    $.post({
                                        url: '/column/list/ops/toggle',
                                        data: {
                                            tableId: 'component',
                                            name: $name
                                        },
                                        beforeSend: () => togglePreloader(true),
                                        complete: () => togglePreloader(false)
                                    }).done(() => {
                                        $(this).toggleClass('active');
                                        table.toggleColumn($name);
                                        table.redraw();
                                    });
                                }
                            });
                        });
                },
                columnMoved: (column, columns) => {
                    $.post({
                        url: '/column/list/ops/order',
                        data: {
                            tableId: 'component',
                            columnNameList: columns.filter(col => col.getField() !== undefined).map(col => col.getField()).join(',')
                        },
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    });
                }
            });

            // Скролл до строки и ее выбор по id
            function rowScrollSelect(id) {
                table.selectRow(id);
                table.scrollToRow(id, 'middle', false);
            }

            // Функция добавления/редактирования изделия
            function editProduct(id) {
                $.modalWindow({
                    loadURL: VIEW_PATH.LIST_EDIT,
                    loadData: { id: id },
                    submitURL: ACTION_PATH.LIST_EDIT_SAVE,
                    onSubmitSuccess: resp => {
                        if (id) {
                            table.setPage(table.getPage()).then(() => rowScrollSelect(id));
                        } else {
                            const id = resp.attributes.id;
                            if (id) {
                                filterData = {};
                                table.setSort(TABR_SORT_ID_DESC);
                                table.setPage(1).then(() => rowScrollSelect(id));
                                page('/detail/' + id + '/general');
                            }
                        }
                    }
                });
            }

            // Функция удаления изделия
            function deleteProduct(id) {
                confirmDialog({
                    title: '<fmt:message key="product.list.delete.title"/>',
                    message: '<fmt:message key="product.list.delete.confirm"/>',
                    onAccept: () => $.ajax({
                        method: 'DELETE',
                        url: ACTION_PATH.LIST_DELETE + id,
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => table.setPage(table.getPage()))
                });
            }

            // Кнопка отображения
            $btnTrigger.on({
                'click': function() {
                    $(this).toggleClass('primary');
                    $triggerColumnContainer.toggleClass('hidden visible');
                }
            });

            // Кнопка добавление изделия
            $btnAddProduct.on({
                'click': () => editProduct()
            });

            // Кнопка отчетов
            $btnReports.dropdown({ action: 'hide' });
            $btnReportItems.on({
                'click': e => $.modalWindow({
                    loadURL: VIEW_PATH.LIST_REPORT,
                    loadData: { reportType: $btnReportItems.index(e.target) + 1 }
                })
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
        });
    </script>
</div>