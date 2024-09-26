<div class="list__header">
    <h1 class="list__header_title">Письма на производство</h1>
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

        // Таблица писем
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
                { title: 'Номер', field: TABR_FIELD.NUMBER },
                {
                    title: 'Дата подписания',
                    field: TABR_FIELD.CREATE_DATE,
                    hozAlign: 'center',
                    width: 150,
                    resizable: false,
                    formatter: 'stdDate'
                },
                {
                    title: 'Договоры',
                    headerSort: false,
                    field: TABR_FIELD.CONTRACT_NUMBER,
                    variableHeight: true,
                    minWidth: 150,
                    width: 250,
                    formatter: 'textarea'
                },
                {
                    title: 'Дата отправки в ОТК',
                    field: TABR_FIELD.SEND_TO_PRODUCTION_DATE,
                    hozAlign: 'center',
                    width: 150,
                    resizable: false,
                    formatter: 'stdDate'
                },
                {
                    title: 'Дата отгрузки',
                    field: TABR_FIELD.SEND_TO_WAREHOUSE_DATE,
                    hozAlign: 'center',
                    width: 150,
                    resizable: false,
                    formatter: 'stdDate'
                },
                {
                    title: 'Комментарий',
                    field: TABR_FIELD.COMMENT,
                    headerSort: false,
                    variableHeight: true,
                    minWidth: 200,
                    width: 300,
                    formatter: 'textarea'
                }
            ],
            rowClick: (e, row) => {
                const data = row.getData();
                table.deselectRow();
                row.select();
                showLetterInfo(data.id);
            },
            rowContext: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowContextMenu: row => {
                const menu = [];
                const data = row.getData();
                menu.push({
                    label: `<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>`,
                    action: () => editLetter(data.id)
                });
                menu.push({
                    label: `<i class="file word alternate outline icon blue"></i><fmt:message key="label.menu.download"/>`,
                    action: () => window.open('/production-shipment-letter-documentation-formed/download/' + data.id, '_blank')
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => deleteLetter(data.id)
                });
                return menu;
            }
        });

        // Скролл до строки и ее выбор по id
        function rowScrollSelect(id) {
            table.selectRow(id);
            table.scrollToRow(id, 'middle', false);
        }

        // Функция добавления/редактирования письма на производство
        function editLetter(id) {
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

        // Удаление письма на производство
        function deleteLetter(id) {
            confirmDialog({
                title: 'Удаление письма на производство',
                message: 'Вы действительно хотите удалить письмо на производство?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: ACTION_PATH.LIST_DELETE + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setPage(table.getPage()))
            });
        }

        // Отображение информации о письме
        function showLetterInfo(id) {
            $subContent.html('');
            $subBlock.show();
            setTimeout(() => table.scrollToRow(id, 'middle', false), 200);
            $.get({
                url: VIEW_PATH.LIST_LETTER_INFO,
                data: { id: id }
            }).done(html => $subContent.html(html));
        }

        // Скрытие области вспомогательного контейнера
        $btnCloseSubBlock.on({
            'click': () => $subBlock.hide()
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

        // Кнопка добавления письма на производство
        $btnAdd.on({
            'click': () => editLetter()
        });
    })
</script>