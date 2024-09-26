<div class="list_additional__header">
    <h1 class="list_additional__header_title"></h1>
    <div class="list_additional__table_buttons">
        <i class="icon add link blue list_additional__btn-add" title="<fmt:message key="label.button.add"/>"></i>
    </div>
</div>
<div class="list_additional__table-wrap">
    <div class="list_additional__table table-sm table-striped"></div>
</div>

<script>
    $(() => {
        const launchId = parseInt('${id}');
        const $btnAdd = $('i.list_additional__btn-add');
        //
        const tableData = tableDataFromUrlQuery(window.location.search);
        let filterData = tableData.filterData;

        const table = new Tabulator('div.list_additional__table', {
            pagination: 'remote',
            paginationInitialPage: tableData.page,
            paginationSize: tableData.size,
            initialSort: tableData.sort.length ? tableData.sort : [{ column: TABR_FIELD.NUMBER_IN_YEAR, dir: SORT_DIR_DESC }],
            ajaxURL: ACTION_PATH.LIST_ADDITIONAL_LOAD,
            ajaxRequesting: (url, params) => {
                params.launchId = launchId;
                params.filterData = JSON.stringify(filterData);
            },
            ajaxSorting: true,
            height: '100%',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                TABR_COL_ID,
                { title: 'Номер/Год', field: TABR_FIELD.NUMBER_IN_YEAR, hozAlign: 'center' },
                {
                    title: 'Дата утверждения',
                    field: TABR_FIELD.APPROVAL_DATE,
                    hozAlign: 'center',
                    formatter: 'stdDate'
                },
                { title: 'Утвержден', field: TABR_FIELD.APPROVED_BY, hozAlign: 'center' },
                {
                    title: 'Комментарий',
                    field: TABR_FIELD.COMMENT,
                    variableHeight: true,
                    minWidth: 200,
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
                if (data.canApprove) {
                    menu.push({
                        label: '<i class="check double icon blue"></i>Утвердить',
                        action: () => launchApprove(id, true)
                    });
                }
                if (data.canUnApprove) {
                    menu.push({
                        label: '<i class="check double icon blue"></i>Отменить утверждение',
                        action: () => launchApprove(id, false)
                    });
                }
                if (data.canEdit) {
                    menu.push({
                        label: '<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>',
                        action: () => editLaunch(id)
                    });
                }
                if (data.canDelete) {
                    menu.push({
                        label: '<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>',
                        action: () => deleteLaunch(id)
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

        // Подтверждение/отмена подтверждения запуска
        function launchApprove(id, toApprove) {
            confirmDialog({
                title: toApprove ? 'Утверждение запуска' : 'Отмена утверждения запуска',
                message: toApprove ? 'Вы действительно хотите утвердить запуск?' : 'Вы действительно хотите отменить утверждение запуска?',
                onAccept: () => $.post({
                    url: ACTION_PATH.LIST_ADDITIONAL_APPROVE,
                    data: { id: id, toApprove: toApprove },
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setPage(table.getPage()).then(() => rowScrollSelect(id)))
            })
        }

        // Редактирование/добавление запуска
        function editLaunch(id) {
            $.modalWindow({
                loadURL: VIEW_PATH.LIST_ADDITIONAL_EDIT,
                loadData: { id: id, launchId: launchId },
                submitURL: ACTION_PATH.LIST_ADDITIONAL_EDIT_SAVE,
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

        // Удаление запуска
        function deleteLaunch(id) {
            confirmDialog({
                title: 'Удаление запуска',
                message: 'Вы действительно хотите удалить запуск?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: ACTION_PATH.LIST_ADDITIONAL_DELETE + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setPage(table.getPage()))
            });
        }

        // Добавление запуска
        $btnAdd.on({
            'click': () => editLaunch()
        });
    });
</script>