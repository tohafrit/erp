<div class="list__header">
    <h1 class="list__header_title">Служебные записки</h1>
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

        let notInitLoad = false;
        const table = new Tabulator('div.list__table', {
            pagination: 'remote',
            paginationInitialPage: tableData.page,
            paginationSize: tableData.size,
            initialSort: tableData.sort.length ? tableData.sort : [{ column: TABR_FIELD.NUMBER_IN_YEAR, dir: SORT_DIR_DESC }],
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
                { title: 'Номер/Год', field: TABR_FIELD.NUMBER_IN_YEAR, hozAlign: 'center' },
                {
                    title: 'Дата согласования',
                    field: TABR_FIELD.AGREEMENT_DATE,
                    hozAlign: 'center',
                    formatter: cell => {
                        const date = dateStdToString(cell.getValue());
                        return (date ? `<i class="icon large check circle green"></i>` : `<i class="icon large times circle red"></i>`) + date;
                    }
                },
                { title: 'Согласована', field: TABR_FIELD.AGREED_BY, hozAlign: 'center', minWidth: 200 },
                {
                    title: 'Дата создания',
                    field: TABR_FIELD.CREATE_DATE,
                    hozAlign: 'center',
                    formatter: 'stdDate'
                },
                { title: 'Создана', field: TABR_FIELD.CREATED_BY, hozAlign: 'center', minWidth: 200 },
                {
                    title: 'Комментарий',
                    field: TABR_FIELD.COMMENT,
                    variableHeight: true,
                    minWidth: 300,
                    formatter: 'textarea'
                }
            ],
            rowClick: (e, row) => {
                const data = row.getData();
                table.deselectRow();
                row.select();
                showProducts(data.id);
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
                    action: () => editNote(id)
                });
                if (data.canAgreement) {
                    menu.push({
                        label: '<i class="check double icon blue"></i>Согласовать',
                        action: () => noteAgreement(id, true)
                    });
                }
                if (data.canUnAgreement) {
                    menu.push({
                        label: '<i class="check double icon blue"></i>Снять согласование',
                        action: () => noteAgreement(id, false)
                    });
                }
                if (data.canDelete) {
                    menu.push({
                        label: '<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>',
                        action: () => deleteNote(id)
                    });
                }
                return menu;
            }
        });

        // Скролл до строки и ее выбор по id
        function rowScrollSelect(id) {
            table.selectRow(id);
            table.scrollToRow(id, 'middle', false);
        }

        // Согласование/снятие согласования записки
        function noteAgreement(id, toAgreement) {
            confirmDialog({
                title: toAgreement ? 'Согласование служебной записки' : 'Отмена согласования служебной записки',
                message: toAgreement ? 'Вы действительно хотите согласовать служебную записку?' : 'Вы действительно хотите отменить согласование служебной записки?',
                onAccept: () => $.post({
                    url: ACTION_PATH.LIST_AGREEMENT,
                    data: { id: id, toAgreement: toAgreement },
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setPage(table.getPage()).then(() => rowScrollSelect(id)))
            })
        }

        // Редактирование/добавление записки
        function editNote(id) {
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

        // Удаление записки
        function deleteNote(id) {
            confirmDialog({
                title: 'Удаление служебной записки',
                message: 'Вы действительно хотите удалить служебную записку?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: ACTION_PATH.LIST_DELETE + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setPage(table.getPage()))
            });
        }

        // Отображение изделий в доп. контейнере
        function showProducts(id) {
            $subContent.html('');
            $subBlock.show();
            setTimeout(() => table.scrollToRow(id, 'middle', false), 200);
            $.get({
                url: VIEW_PATH.LIST_PRODUCT,
                data: { id: id }
            }).done(html => $subContent.html(html));
        }

        // Скрытие области контейнера с изделиями
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

        // Добавление записки
        $btnAdd.on({
            'click': () => editNote()
        });
    })
</script>