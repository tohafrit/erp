<div class="list__header">
    <h1 class="list__header_title">Поступление со склада производства</h1>
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
        const $btnFilter = $('i.list__btn-filter');
        const $btnAdd = $('i.list__btn-add');
        const $subBlock = $('div.list__sub-block');
        const $subContent = $('div.list__sub-block-content');
        const $btnCloseSubBlock = $('i.list__btn-close-sub-block');
        // Параметры url запроса
        const usp = new URLSearchParams(window.location.search);
        let selectedId = usp.get(TABR_FIELD.SELECTED_ID);
        // Параметры фильтра таблицы
        const tableData = tableDataFromUrlQuery(window.location.search);
        let filterData = tableData.filterData;
        if (selectedId) tableData.page = 1;

        let notInitLoad = false;
        const table = new Tabulator('div.list__table', {
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
                } else {
                    if (selectedId) {
                        params.selectedId = selectedId;
                        sessionStorage.setItem(S_STORAGE.LIST_QUERY, '');
                        page.show(ROUTE.list(), undefined, false);
                    }
                }
                $subBlock.hide();
                notInitLoad = true;
            },
            dataLoaded: () => {
                if (selectedId) table.selectRow(selectedId);
                selectedId = null;
            },
            ajaxSorting: true,
            height: '100%',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                TABR_COL_ID,
                {
                    title: 'МСН',
                    field: TABR_FIELD.FULL_NUMBER,
                    hozAlign: 'center',
                    width: 100,
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
                    title: 'Дата принятия',
                    field: TABR_FIELD.ACCEPT_DATE,
                    hozAlign: 'center',
                    width: 130,
                    resizable: false,
                    formatter: 'stdDate'
                },
                {
                    title: 'Отпустил',
                    field: TABR_FIELD.GIVE_USER,
                    hozAlign: 'center',
                    minWidth: 130
                },
                {
                    title: 'Получил',
                    field: TABR_FIELD.ACCEPT_USER,
                    hozAlign: 'center',
                    minWidth: 130
                },
                { title: 'Место назначения', field: TABR_FIELD.STORAGE_PLACE },
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
                    label: '<i class="file word blue icon"></i>Сформировать МСН',
                    action: () => window.open('/warehouse-documentation-formed/download?type=' + 29 + '&id=' + id, '_blank')
                });
                if (data.canAccept) {
                    menu.push({
                        label: '<i class="check icon blue"></i>Принять изделия',
                        action: () => acceptRecord(id, true)
                    });
                } else {
                    menu.push({
                        label: '<i class="times icon blue"></i>Отменить принятие изделий',
                        action: () => acceptRecord(id, false)
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
        function acceptRecord(id, toAccept) {
            if (toAccept) $.modalWindow({
                loadURL: VIEW_PATH.LIST_ACCEPT,
                loadData: { id: id },
                submitURL: ACTION_PATH.LIST_ACCEPT_APPLY,
                submitAsJson: true,
                onSubmitSuccess: () => table.setPage(table.getPage()).then(() => rowScrollSelect(id))
            })
            else confirmDialog({
                title: 'Отмена принятия',
                message: 'Вы действительно хотите отменить принятие?',
                onAccept: () => $.post({
                    url: ACTION_PATH.LIST_UNACCEPT,
                    data: { id: id },
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setPage(table.getPage()).then(() => rowScrollSelect(id)))
            })
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
            selector: 'div.list__table',
            url: ACTION_PATH.LIST_LOAD,
            filterData: () => filterData
        });
    })
</script>