<div class="list__header">
    <h1 class="list__header_title"><fmt:message key="${lifecycle.property}"/></h1>
    <div class="list__header_buttons">
        <div class="ui icon small basic button list__btn-column-trigger" title="Видимость колонок">
            <i class="fas fa-tasks icon"></i>
            <div class="ui compact message hidden list__trigger-column-container"></div>
        </div>
        <i class="icon filter link list__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        <i class="icon file excel outline link blue list__btn-excel" title="Выгрузка в excel"></i>
        <i class="icon add link blue list__btn-add-component" title="<fmt:message key="label.button.add"/>"></i>
    </div>
</div>
<div class="list__table-block">
    <jsp:include page="/api/view/prod/component/list/filter">
        <jsp:param name="lificycleId" value="${lifecycle.id}"/>
    </jsp:include>
    <div class="list__table-main-block">
        <div class="list__table-wrap">
            <div class="list__table table-sm table-striped"></div>
        </div>
    </div>
    <div class="list__table-sub-block">
        <i class="close link blue icon list__btn-close-sub-block"></i>
        <div class="list__sub-block-content"></div>
    </div>
</div>

<script>
    $(() => {
        const lifecycleId = '${lifecycle.id}';
        const isNew = '${isNew}' === 'true';
        const isDesign = '${isDesign}' === 'true';
        //const isIndustrial = '${isIndustrial}' === 'true';

        const $btnFilter = $('i.list__btn-filter');
        const $btnAddComponent = $('i.list__btn-add-component');
        const $btnSearch = $('div.list_filter__btn-search');
        const $btnExcel = $('i.list__btn-excel');
        const $filter = $('div.list_filter__main');
        const $filterForm = $('form.list_filter__form');
        const $datatable = $('div.list__table');

        const $subBlock = $('div.list__table-sub-block');
        const $subContent = $('div.list__sub-block-content');
        const $btnCloseSubBlock = $('i.list__btn-close-sub-block');

        const $btnTrigger = $('div.list__btn-column-trigger');
        const $triggerColumnContainer = $btnTrigger.find('div.list__trigger-column-container');
        let selectedId = null;

        if (isNew) $btnAddComponent.hide();

        const columnMap = new Map();
        columnMap.set('position',               'Позиция');
        columnMap.set('name',                   'Наименование по ТС');
        columnMap.set('producer',               'Производитель');
        columnMap.set('category',               'Категория');
        columnMap.set('description',            'Описание');
        columnMap.set('product',                'Изделие');
        columnMap.set('bom',                    'Версия');
        columnMap.set('techCharacteristics',    'Технические характеристики');
        columnMap.set('okei',                   'Единица измерения');
        columnMap.set('purpose',                'Назначение');
        columnMap.set('installation',           'Тип установки');
        columnMap.set('kind',                   'Тип');
        columnMap.set('price',                  'Ориентировочная цена');
        columnMap.set('deliveryTime',           'Срок поставки');
        columnMap.set('docPath',                'Документация');
        columnMap.set('processed',              'Обработан');
        columnMap.set('modifiedDatetime',       'Последнее изменение');
        columnMap.set('substituteComponent',    'Заместитель');
        columnMap.set('purchaseComponent',      'Замена к закупке');

        // Дефолтные колонки
        const numeration = [TABR_COL_REMOTE_ROW_NUM, TABR_COL_ID];
        const defaultColumns = {
            position: { title: columnMap.get('position'), field: 'position', visible: !isNew },
            name: { title: columnMap.get('name'), field: 'name' },
            producer: { title: columnMap.get('producer'), field: 'producer' },
            category: { title: columnMap.get('category'), field: 'category', visible: false },
            description: {
                title: columnMap.get('description'),
                field: 'description',
                variableHeight: true,
                minWidth: 200,
                width: 300,
                formatter: 'textarea'
            },
            product: {
                title: columnMap.get('product'),
                field: 'product',
                visible: isNew,
                headerSort: false,
                variableHeight: true,
                width: 350,
                formatter: 'textarea'
            },
            bom: {
                title: columnMap.get('bom'),
                field: 'bom',
                visible: isNew,
                headerSort: false,
                variableHeight: true,
                width: 100,
                formatter: 'textarea'
            },
            techCharacteristics: {
                title: columnMap.get('techCharacteristics'),
                field: 'techCharacteristics',
                headerSort: false,
                variableHeight: true,
                minWidth: 300,
                width: 300,
                formatter: 'textarea'
            },
            okei: { title: columnMap.get('okei'), field: 'okei' },
            purpose: { title: columnMap.get('purpose'), field: 'purpose' },
            substituteComponent: { title: columnMap.get('substituteComponent'), field: 'substituteComponent', variableHeight: true, formatter: 'textarea' },
            purchaseComponent: { title: columnMap.get('purchaseComponent'), field: 'purchaseComponent' },
            installation: { title: columnMap.get('installation'), field: 'installation' },
            kind: { title: columnMap.get('kind'), field: 'kind' },
            price: { title: columnMap.get('price'), field: 'price' },
            deliveryTime: { title: columnMap.get('deliveryTime'), field: 'deliveryTime', visible: isNew || isDesign },
            docPath: {
                title: columnMap.get('docPath'),
                field: 'docPath',
                visible: isNew || isDesign,
                headerSort: false,
                variableHeight: true,
                minWidth: 200,
                width: 250,
                formatter: 'textarea'
            },
            processed: {
                title: columnMap.get('processed'),
                field: 'processed',
                resizable: false,
                headerSort: false,
                hozAlign: 'center',
                formatter: cell => {
                    return booleanToLight(cell.getValue());
                }
            },
            modifiedDatetime: {
                title: columnMap.get('modifiedDatetime'),
                field: 'modifiedDatetime',
                formatter: cell => dateTimeStdToString(cell.getValue())
            }
        };

        // Кнопка отображения
        $btnTrigger.on({
            'click': function () {
                $(this).toggleClass('primary');
                $triggerColumnContainer.toggleClass('hidden visible');
            }
        });

        // Кнопка фильтра
        $btnFilter.on({
            'click': function() {
                $(this).toggleClass('blue');
                $filter.toggle(!$filter.is(':visible'));
            }
        });

        // Таблица компонентов
        const table = new Tabulator('div.list__table', {
            pagination: 'remote',
            ajaxURL: '/api/action/prod/component/list/load',
            groupBy: 'category',
            initialSort: [{ column: 'category', dir: 'asc' }],
            groupHeader: value => value,
            ajaxRequesting: (url, params) => {
                params.selectedId = selectedId;
                params.filterForm = formToJson($filterForm);
                params.lifecycleId = lifecycleId;
            },
            dataLoaded: () => {
                if (selectedId) table.selectRow(selectedId);
                selectedId = null;
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
            rowContextMenu: component => {
                const menu = [];
                const data = component.getData();
                const id = data.id;
                if (data.removeApprovedCtxMenu) {
                    menu.push({
                        label: `<i class="reply all icon blue"></i>Отменить утверждение`,
                        action: () => approveComponent(false, id)
                    });
                }
                if (!data.approved) {
                    menu.push({
                        label: `<i class="tasks icon blue"></i>Утвердить`,
                        action: () => approveComponent(true, id)
                    });
                    menu.push({
                        label: `<i class="arrows alternate horizontal icon blue"></i>Заменить на компонент из справочника`,
                        action: () => replaceComponent(id)
                    });
                }
                if (data.substituteCtxMenu) {
                    menu.push({ separator: true });
                    menu.push({
                        label: `<i class="clone outline icon blue"></i>Добавить как заместитель`,
                        action: () => setComponentReplacement(id, 1)
                    });
                }
                if (data.addPurchaseCtxMenu) {
                    menu.push({ separator: true });
                    menu.push({
                        label: `<i class="dollar sign icon blue"></i>Добавить как замену к закупке`,
                        action: () => setComponentReplacement(id, 2)
                    });
                }
                if (data.removePurchaseCtxMenu) {
                    menu.push({
                        label: `<i class="reply all icon blue"></i>Удалить замену к закупке`,
                        action: () => unsetComponentReplacement(id, 2)
                    });
                }
                if (data.replacementCtxMenu) {
                    menu.push({ separator: true });
                    menu.push({
                        label: `<i class="project diagram icon blue"></i>Добавить как замену по справочнику`,
                        action: () => setComponentReplacement(id, 3)
                    });
                }
                menu.push({ separator: true });
                menu.push({
                    label: `<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>`,
                    action: () => editComponent(id, false)
                });
                menu.push({
                    label: `<i class="clipboard list blue icon"></i>Показать входимость`,
                    action: () => showOccurrence(id)
                });
                menu.push({
                    label: `<i class="comment blue icon"></i>Показать комментарии`,
                    action: () => showComment(id)
                });
                menu.push({
                    label: `<i class="copy outline icon blue"></i>Копировать`,
                    action: () => editComponent(id, true)
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => deleteComponent(id)
                });
                return menu;
            },
            tableBuilt: () => {
                const columnNames = [];
                for (key of Object.keys(defaultColumns)) {
                    const iterationColumn = defaultColumns[key];
                    const isVisible = !('visible' in iterationColumn) || ('visible' in iterationColumn && iterationColumn.visible);
                    if (isVisible) {
                        if ('columns' in iterationColumn) {
                            for (item of iterationColumn.columns) {
                                columnNames.push(item.field);
                            }
                        } else {
                            columnNames.push(key);
                        }
                    }
                }
                const data = {
                    tableId: 'component' + lifecycleId,
                    columnNameList: columnNames.join(',')
                };
                $.get('/column/list/ops/load', data).done(data => {
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
                                        tableId: 'component' + lifecycleId,
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
                        tableId: 'component' + lifecycleId,
                        columnNameList: columns.filter(col => col.getField() !== undefined).map(col => col.getField()).join(',')
                    },
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                });
            },
            dataSorting: () => $subBlock.hide()
        });

        // Утвердить/разутвердить компоненты
        function approveComponent(value, id) {
            confirmDialog({
                title: (value ? 'Утверждение' : 'Снятие утверждения с') + ' компонента',
                message: 'Вы уверены, что хотите' + (value ? ' утвердить компонент?' : ' снять утверждение с компонента?'),
                onAccept: () => $.post({
                    url: '/api/action/prod/component/list/component-approve',
                    data: {
                        approved: value,
                        id: id
                    },
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setPage(table.getPage()))
            });
        }

        // Заменить на компонент из справочника
        function replaceComponent(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/component/list/replace',
                loadData: { componentId: id },
                submitURL: '/api/action/prod/component/list/replace/save'
            });
        }

        // Функция добавления/редактирования компонента
        function editComponent(id, copy) {
            $.modalWindow({
                loadURL: '/api/view/prod/component/list/edit',
                loadData: {
                    id: id,
                    addAsDesign: !isNew,
                    copy: copy
                },
                submitURL: '/api/action/prod/component/list/edit/save',
                onSubmitSuccess: response => {
                    if (id) {
                        table.setPage(table.getPage());
                    } else {
                        selectedId = response.attributes.id;
                        table.setPage(1);
                    }
                }
            });
        }

        // Функция удаления компонента
        function deleteComponent(id) {
            confirmDialog({
                title: 'Удаление компонента',
                message: 'Вы уверены, что хотите удалить компонент?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: '/api/action/prod/component/list/delete/' + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setPage(table.getPage()))
            });
        }

        // Функция отображения вхождений
        function showOccurrence(id) {
            $subBlock.show();
            setTimeout(() => tabrScrollToRow(table, 'top'), 100);
            $.get({
                url: '/api/view/prod/component/list/occurrence',
                data: { id: id },
                beforeSend: () => togglePreloader(true),
                complete: () => togglePreloader(false)
            }).done(html => $subContent.html(html));
        }

        // Функция отображения комментарий
        function showComment(id) {
            $subBlock.show();
            setTimeout(() => tabrScrollToRow(table, 'top'), 100);
            $.get({
                url: '/api/view/prod/component/list/comment',
                data: { id: id },
                beforeSend: () => togglePreloader(true),
                complete: () => togglePreloader(false)
            }).done(html => $subContent.html(html));
        }

        // Скрытие области вспомогательного контейнера
        $btnCloseSubBlock.on({
            'click': () => $subBlock.hide()
        });

        // Кнопка поиска
        $btnSearch.on({
            'click': () => table.setData()
        });

        $filter.enter(() => $btnSearch.trigger('click'));

        // Кнопка добавления компонента
        $btnAddComponent.on({
            'click': () => editComponent()
        });

        // Функции установки/очистки компонента замен
        // 1 - компонент заместитель, 2 - компонента к закупке, 3 - компонент по замене
        function setComponentReplacement(id, mode) {
            $.modalWindow({
                loadURL: '/api/view/prod/component/list/set-component-replacement',
                loadData: {
                    componentId: id,
                    mode: mode
                }
            });
        }

        // Функция очистка компонента замены
        function unsetComponentReplacement(id, mode) {
            let title, message;
            if (mode === 1) {
                title = 'Очистка компонента заместителя';
                message = 'Вы уверены, что хотите очистить компонент заместителя?'
            } else if (mode === 2) {
                title = 'Очистка компонента закупки';
                message = 'Вы уверены, что хотите очистить компонент закупки?'
            }
            confirmDialog({
                title: title,
                message: message,
                onAccept: () => $.post({
                    url: '/api/action/prod/component/list/unset-replacement',
                    data: {
                        componentId: id,
                        mode: mode
                    },
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setData())
            });
        }

        // Ивенты для взаимодействия с табулятором
        $datatable.on({
            'removeSelectRow': (e, id) => {
                const row = table.getRow(id);
                if (row != null) {
                    row.delete();
                }
            }
        });

        // Кнопка выгрузки в excel
        $btnExcel.on({
            'click': () => table.download('xlsx', 'component.xlsx', { sheetName: 'Компоненты' })
        });
    });
</script>