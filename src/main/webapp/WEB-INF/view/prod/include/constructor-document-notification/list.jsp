<div class="list__header">
    <h1 class="list__header_title">Извещения об изменении КД</h1>
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
        const $subBlock = $('div.list__sub-block');
        const $subContent = $('div.list__sub-block-content');
        const $btnCloseSubBlock = $('i.list__btn-close-sub-block');
        // Параметры фильтра таблицы
        const tableData = tableDataFromUrlQuery(window.location.search);
        let filterData = tableData.filterData;

        let notInitLoad = false;
        const table = new Tabulator('div.list__table', {
            pagination: 'remote',
            tooltips: true,
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
            initialSort: tableData.sort.length ? tableData.sort : [{ column: TABR_FIELD.ENTITY_NUMBER, dir: SORT_DIR_DESC }],
            height: '100%',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                TABR_COL_ID,
                { title: 'Номер', field: TABR_FIELD.DOC_NUMBER },
                {
                    title: 'Дата выпуска',
                    field: TABR_FIELD.RELEASE_ON,
                    hozAlign: 'center',
                    width: 130,
                    resizable: false,
                    formatter: 'stdDate'
                },
                {
                    title: 'Срок изменения',
                    field: TABR_FIELD.TERM_CHANGE_ON,
                    hozAlign: 'center',
                    width: 130,
                    resizable: false,
                    formatter: 'stdDate'
                },
                { title: 'Причина', field: TABR_FIELD.REASON },
                {
                    title: 'Указание о заделе',
                    field: TABR_FIELD.RESERVE_INDICATION,
                    resizable: false,
                    hozAlign: 'center',
                    formatter: 'lightMark'
                },
                { title: 'Указание о внедрении', field: TABR_FIELD.INTRODUCTION_INDICATION },
                { title: 'Изделие применяемости', field: TABR_FIELD.PRODUCT },
                { title: 'Ведущий изделия', field: TABR_FIELD.USER }
            ],
            rowContextMenu: row => {
                let id = row.getData().id;
                return [
                    {
                        label: '<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>',
                        action: () => editRecord(id)
                    },
                    {
                        label: '<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>',
                        action: () => deleteNotification(id)
                    }
                ];
            },
            rowClick: (e, row) => {
                const data = row.getData();
                table.deselectRow();
                row.select();
                showParentNotification(data.id);
            },
            rowContext: (e, row) => {
                table.deselectRow();
                row.select();
            }
        });

        // Отображение применяемости
        function showParentNotification(id) {
            $subContent.html('');
            $subBlock.show();
            setTimeout(() => table.scrollToRow(id, 'middle', false), 200);
            $.get({
                url: VIEW_PATH.LIST_CHILD_NOTIFICATION,
                data: { id: id }
            }).done(html => $subContent.html(html));
        }

        // Скрытие области вспомогательного контейнера
        $btnCloseSubBlock.on({
            'click': () => $subBlock.hide()
        });

        // Добавление сущности
        $addEntity.on({
            'click': () => editRecord()
        });

        // Функция добавления/редактирования сущности
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

        // Скролл до строки и ее выбор по id
        function rowScrollSelect(id) {
            table.selectRow(id);
            table.scrollToRow(id, 'middle', false);
        }

        // Функция удаления сущности
        function deleteNotification(id) {
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
    });
</script>