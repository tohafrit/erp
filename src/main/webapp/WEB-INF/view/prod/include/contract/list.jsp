<div class="list__container">
    <div class="list__header">
        <h1 class="list__header_title">Договоры</h1>
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

            // Параметры фильтра таблицы
            const tableData = tableDataFromUrlQuery(window.location.search);
            let filterData = tableData.filterData;

            // Таблица договоров
            let notInitLoad = false;
            const table = new Tabulator('div.list__table', {
                pagination: 'remote',
                paginationInitialPage: tableData.page,
                paginationSize: tableData.size,
                initialSort: tableData.sort.length ? tableData.sort : [{ column: TABR_FIELD.FULL_NUMBER, dir: SORT_DIR_DESC }],
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
                columns: [
                    TABR_COL_REMOTE_ROW_NUM,
                    TABR_COL_ID,
                    { title: 'Номер/Год', field: TABR_FIELD.FULL_NUMBER, hozAlign: 'center' },
                    { title: 'Заказчик', field: TABR_FIELD.CUSTOMER },
                    { title: 'Город', field: TABR_FIELD.CITY }
                ],
                rowClick: (e, row) => {
                    const data = row.getData();
                    table.deselectRow();
                    row.select();
                    showStructure(data.id);
                },
                rowContext: (e, row) => {
                    table.deselectRow();
                    row.select();
                },
                rowContextMenu: row => {
                    const menu = [];
                    const data = row.getData();
                    const contractId = data.id;
                    const sectionId = data.sectionId;
                    menu.push({
                        label: '<i class="book open icon blue link"></i><fmt:message key="label.menu.open"/>',
                        action: () => page(ROUTE.detail('contractId=' + contractId + "&sectionId=" + sectionId))
                    });
                    menu.push({
                        label: '<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>',
                        action: () => editContract(contractId)
                    });
                    menu.push({
                        label: '<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>',
                        action: () => deleteContract(contractId)
                    });
                    return menu;
                }
            });

            // Скролл до строки и ее выбор по id
            function rowScrollSelect(id) {
                table.selectRow(id);
                table.scrollToRow(id, 'middle', false);
                showStructure(id);
            }

            // Редактирование/добавление договора
            function editContract(id) {
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

            // Удаление договора
            function deleteContract(id) {
                confirmDialog({
                    title: 'Удаление договора',
                    message: 'Вы действительно хотите удалить договор?',
                    onAccept: () => $.ajax({
                        method: 'DELETE',
                        url: ACTION_PATH.LIST_DELETE + id,
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => table.setPage(table.getPage()))
                });
            }

            // Отображение структуры договора в доп. контейнере
            function showStructure(id) {
                $subContent.html('');
                $subBlock.show();
                setTimeout(() => table.scrollToRow(id, 'middle', false), 200);
                $.get({
                    url: VIEW_PATH.LIST_STRUCTURE,
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

            // Добавление договора
            $btnAdd.on({
                'click': () => editContract()
            });

            // Ресайз вспомогательного контейнера
            $subBlock.resizable({
                autoHide: true,
                handles: 'n',
                ghost: true,
                stop: () => $subBlock.css({
                    'width': '100%',
                    'top': 0
                })
            });
        })
    </script>
</div>