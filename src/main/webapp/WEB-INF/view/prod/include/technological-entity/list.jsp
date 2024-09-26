<div class="list__container">
    <div class="list__header">
        <h1 class="list__header_title">Технологическая документация</h1>
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
            const $addEntity = $('i.list__btn-add');
            const $btnFilter = $('i.list__btn-filter');
            const $typeSelector = $('select.list__filter_type-select');
            const $productApplicability = $('div.list__filter_product-applicability');
            const $stageApplicability = $('div.list__filter_stage-applicability');
            const $subBlock = $('div.list__sub-block');
            const $subContent = $('div.list__sub-block-content');
            const $btnCloseSubBlock = $('i.list__btn-close-sub-block');
            const search = window.location.search;
            // Параметры фильтра таблицы
            const tableData = tableDataFromUrlQuery(search);
            let filterData = tableData.filterData;

            let notInitLoad = false;
            const table = new Tabulator('div.list__table', {
                pagination: 'remote',
                tooltips: true,
                ajaxURL: ACTION_PATH.LIST_LOAD,
                ajaxRequesting: (url, params) => {
                    params.filterData = JSON.stringify(filterData);
                    if (notInitLoad) {
                        const query = urlQueryFromTableParams(search, params);
                        sessionStorage.setItem(S_STORAGE.LIST_QUERY, query);
                        page.show(ROUTE.list(query), undefined, false);
                    }
                    notInitLoad = true;
                },
                ajaxSorting: true,
                initialSort: tableData.sort.length ? tableData.sort : [{ column: TABR_FIELD.ENTITY_NUMBER, dir: SORT_DIR_DESC }],
                height: '100%',
                layout: 'fitDataFill',
                columns: [
                    TABR_COL_REMOTE_ROW_NUM,
                    TABR_COL_ID,
                    { title: '№ документации', field: TABR_FIELD.ENTITY_NUMBER },
                    { title: '№ комплекта', field: TABR_FIELD.SET_NUMBER },
                    {
                        title: 'Применяемость',
                        field: TABR_FIELD.DESCRIPTION,
                        headerSort: false
                    },
                    {
                        title: 'Утвержден',
                        field: TABR_FIELD.APPROVED,
                        resizable: false,
                        hozAlign: 'center',
                        formatter: 'lightMark',
                        headerSort: false,
                        formatterParams: {
                            value: cell => cell.getValue(),
                            trueText: cell => cell.getData().approvedTooltip,
                            falseText: () => ''
                        }
                    },
                    {
                        title: 'Разработан',
                        field: TABR_FIELD.DESIGNED,
                        resizable: false,
                        hozAlign: 'center',
                        formatter: 'lightMark',
                        headerSort: false,
                        formatterParams: {
                            value: cell => cell.getValue(),
                            trueText: cell => cell.getData().designedTooltip,
                            falseText: () => ''
                        }
                    },
                    {
                        title: 'Проверен',
                        field: TABR_FIELD.CHECKED,
                        resizable: false,
                        hozAlign: 'center',
                        formatter: 'lightMark',
                        headerSort: false,
                        formatterParams: {
                            value: cell => cell.getValue(),
                            trueText: cell => cell.getData().checkedTooltip,
                            falseText: () => ''
                        }
                    },
                    {
                        title: 'Метролог',
                        field: TABR_FIELD.METROLOGIST,
                        resizable: false,
                        hozAlign: 'center',
                        formatter: 'lightMark',
                        headerSort: false,
                        formatterParams: {
                            value: cell => cell.getValue(),
                            trueText: cell => cell.getData().metrologistTooltip,
                            falseText: () => ''
                        }
                    },
                    {
                        title: 'Нормоконтролер',
                        field: TABR_FIELD.NORMOCONTROLLER,
                        resizable: false,
                        hozAlign: 'center',
                        formatter: 'lightMark',
                        headerSort: false,
                        formatterParams: {
                            value: cell => cell.getValue(),
                            trueText: cell => cell.getData().normocontrollerTooltip,
                            falseText: () => ''
                        }
                    },
                    {
                        title: 'Согласовано ВП МО РФ',
                        field: TABR_FIELD.MILITARY,
                        resizable: false,
                        hozAlign: 'center',
                        formatter: 'lightMark',
                        headerSort: false,
                        formatterParams: {
                            value: cell => cell.getValue(),
                            trueText: cell => cell.getData().militaryTooltip,
                            falseText: () => ''
                        }
                    },
                    {
                        title: 'Начальник технологического отдела',
                        field: TABR_FIELD.TECHNOLOGICAL_CHIEF,
                        resizable: false,
                        hozAlign: 'center',
                        formatter: 'lightMark',
                        headerSort: false,
                        formatterParams: {
                            value: cell => cell.getValue(),
                            trueText: cell => cell.getData().technologicalChiefTooltip,
                            falseText: () => ''
                        }
                    }
                ],
                rowContextMenu: row => {
                    let id = row.getData().id;
                    return [
                        {
                            label: '<i class="book open icon blue link"></i><fmt:message key="label.menu.open"/>',
                            action: () => page(ROUTE.detail(id))
                        },
                        {
                            label: '<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>',
                            action: () => editEntity(id)
                        },
                        {
                            label: '<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>',
                            action: () => deleteEntity(id)
                        }
                    ];
                },
                rowClick: (e, row) => {
                    const data = row.getData();
                    table.deselectRow();
                    row.select();
                    showApplicability(data.id);
                },
                rowContext: (e, row) => {
                    table.deselectRow();
                    row.select();
                }
            });

            // Отображение применяемости
            function showApplicability(id) {
                $subContent.html('');
                $subBlock.show();
                setTimeout(() => table.scrollToRow(id, 'middle', false), 200);
                $.get({
                    url: VIEW_PATH.LIST_APPLICABILITY,
                    data: { id: id }
                }).done(html => $subContent.html(html));
            }

            // Скрытие области вспомогательного контейнера
            $btnCloseSubBlock.on({
                'click': () => $subBlock.hide()
            });

            // Добавление сущности
            $addEntity.on({
                'click': () => editEntity()
            });

            // Функция добавления/редактирования сущности
            function editEntity(id) {
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
                            const entityTypeId = resp.attributes.entityTypeId;
                            if (id && entityTypeId) {
                                filterData = {};
                                filterData.entityTypeId = entityTypeId
                                table.setSort(TABR_SORT_ID_DESC);
                                table.setPage(1).then(() => rowScrollSelect(id));
                            }
                        }
                    }
                });
            }

            // Скролл до строки и ее выбор по id
            function rowScrollSelect(id) {
                table.selectRow(id);
                table.scrollToRow(id, 'middle', false);
            }

            // Функция удаления сущности
            function deleteEntity(id) {
                confirmDialog({
                    title: 'Удаление сущности',
                    message: 'Вы действительно хотите удалить сущность?',
                    onAccept: () => $.ajax({
                        method: 'DELETE',
                        url: ACTION_PATH.LIST_DELETE + id,
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => table.setData())
                });
            }

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

            // Переключение поля применяемости
            $typeSelector.dropdown().on({
                'change': () => {
                    let type = Number($typeSelector.val());
                    if (type === APPLICABILITY_TYPE.PRIVATE || type === APPLICABILITY_TYPE.TYPICAL_PRODUCT_GROUP) {
                        $productApplicability.show();
                        $stageApplicability.hide();
                        formClear($stageApplicability);
                    } else if (type === APPLICABILITY_TYPE.TYPICAL_TECHNOLOGICAL_STAGE) {
                        $productApplicability.hide();
                        $stageApplicability.show();
                        formClear($productApplicability);
                    } else {
                        $productApplicability.hide();
                        $stageApplicability.hide();
                        formClear($productApplicability);
                        formClear($stageApplicability);
                    }
                }
            }).trigger('change');
        });
    </script>
</div>