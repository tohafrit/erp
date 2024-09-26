<div class="list__container">
    <div class="list__header">
        <h1 class="list__header_title">Трудоемкость изготовления и проверки изделий</h1>
        <div class="list__header_buttons">
            <i class="icon filter link blue list__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
            <i class="icon add link blue list__btn-add" title="<fmt:message key="label.button.add"/>"></i>
        </div>
    </div>
    <div class="list__table table-sm table-striped"></div>

    <script>
        $(() => {
            const $table = $('div.list__table');
            const $btnFilter = $('i.list__btn-filter');
            const $btnAdd = $('i.list__btn-add');
            // Параметры фильтра таблицы
            const tableData = tableDataFromUrlQuery(window.location.search);
            let filterData = tableData.filterData;

            let notInitLoad = false;
            const table = new Tabulator('div.list__table', {
                virtualDom: false,
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
                    notInitLoad = true;
                },
                ajaxSorting: true,
                height: 'calc(100vh - 140px)',
                layout: 'fitDataFill',
                columns: [
                    TABR_COL_REMOTE_ROW_NUM,
                    TABR_COL_ID,
                    { title: 'Наименование', field: TABR_FIELD.NAME },
                    {
                        title: 'Дата добавления',
                        field: TABR_FIELD.CREATE_DATE,
                        hozAlign: 'center',
                        formatter: 'stdDate',
                        width: 150,
                        resizable: false
                    },
                    {
                        title: 'Утверждено',
                        field: TABR_FIELD.APPROVED,
                        hozAlign: 'center',
                        headerSort: false,
                        width: 100,
                        resizable: false
                    },
                    { title: 'Добавил', field: TABR_FIELD.CREATED_BY, hozAlign: 'center', headerSort: false },
                    {
                        title: 'Комментарий',
                        headerSort: false,
                        field: TABR_FIELD.COMMENT,
                        variableHeight: true,
                        minWidth: 300,
                        formatter: 'textarea'
                    }
                ],
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
                    const id = data.id;
                    menu.push({
                        label: '<i class="book open icon blue link"></i><fmt:message key="label.menu.open"/>',
                        action: () => page(ROUTE.detail(id))
                    });
                    menu.push({
                        label: '<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>',
                        action: () => editRecord(id)
                    });
                    menu.push({
                        label: '<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>',
                        action: () => deleteRecord(id)
                    });
                    return menu;
                }
            });
            $table.find(TABR_SELECTOR_TABLE_HOLDER).css({ height: 'calc(100vh - 210px)' });

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
                    title: 'Удаление расчета трудоемкости',
                    message: 'Вы действительно хотите удалить расчет трудоемкости?',
                    onAccept: () => $.ajax({
                        method: 'DELETE',
                        url: ACTION_PATH.LIST_DELETE + id,
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => table.setPage(table.getPage()))
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

            // Добавление
            $btnAdd.on({
                'click': () => editRecord()
            });

            // Обновление таблицы
            tableTimerUpdate({
                selector: 'div.list__table',
                url: ACTION_PATH.LIST_LOAD,
                filterData: () => filterData
            });
        })
    </script>
</div>