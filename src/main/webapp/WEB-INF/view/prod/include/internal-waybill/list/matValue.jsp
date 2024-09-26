<div class="list_mat-value__header">
    <h1 class="list_mat-value__header_title">${number}</h1>
    <div class="list_mat-value__table_buttons">
        <i class="icon add link blue list_mat-value__btn-add" title="<fmt:message key="label.button.add"/>"></i>
    </div>
</div>
<div class="list_mat-value__table-wrap">
    <div class="list_mat-value__table table-sm table-striped"></div>
</div>

<script>
    $(() => {
        const waybillId = '${waybillId}';
        const $btnAdd = $('i.list_mat-value__btn-add');

        const table = new Tabulator('div.list_mat-value__table', {
            pagination: 'remote',
            paginationSize: TABR_MAX_PAGE_SIZE,
            initialSort: [{ column: TABR_FIELD.NOTICE_DATE, dir: SORT_DIR_DESC }],
            ajaxURL: ACTION_PATH.LIST_MAT_VALUE_LOAD,
            ajaxRequesting: (url, params) => {
                params.waybillId = waybillId;
            },
            groupBy: TABR_FIELD.PRODUCT,
            groupStartOpen: true,
            groupHeader: value => 'Изделие: ' + value,
            ajaxSorting: true,
            height: '100%',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                TABR_COL_ID,
                { title: 'Изделие', field: TABR_FIELD.PRODUCT, visible: false },
                { title: 'Серийный номер', field: TABR_FIELD.SERIAL_NUMBER },
                {
                    title: 'Ячейка',
                    field: TABR_FIELD.CELL,
                    hozAlign: 'center',
                    width: 80,
                    resizable: false
                },
                { title: 'Договор', field: TABR_FIELD.CONTRACT },
                { title: 'Заказчик', field: TABR_FIELD.CUSTOMER },
                { title: 'Извещение', field: TABR_FIELD.NOTICE, hozAlign: 'center' },
                {
                    title: 'Дата извещения',
                    field: TABR_FIELD.NOTICE_DATE,
                    hozAlign: 'center',
                    width: 150,
                    resizable: false,
                    formatter: 'stdDate'
                },
                { title: 'Заявление', field: TABR_FIELD.STATEMENT },
                { title: 'Письмо ПЭО', field: TABR_FIELD.LETTER, hozAlign: 'center' },
                { title: 'Местонахождение', field: TABR_FIELD.LOCATION }
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
                    label: '<i class="file alternate outline icon blue"></i>Открыть договор',
                    action: () => window.open(window.location.origin + '/prod/contract/detail?contractId=' + data.contractId + '&sectionId=' + data.sectionId)
                });
                menu.push({
                    label: '<i class="envelope outline icon blue"></i>Открыть письмо ПЭО',
                    action: () => window.open(window.location.origin + '/prod/production-shipment-letter/list?selectedId=' + data.letterId)
                });
                menu.push({
                    label: '<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>',
                    action: () => deleteRecord(id)
                });
                return menu;
            }
        });

        // Удаление
        function deleteRecord(id) {
            confirmDialog({
                title: 'Удаление изделия',
                message: 'Вы действительно хотите удалить изделие из накладной?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: ACTION_PATH.LIST_MAT_VALUE_DELETE + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setPage(table.getPage()))
            });
        }

        // Добавление
        $btnAdd.on({
            'click': () => $.modalWindow({
                loadURL: VIEW_PATH.LIST_MAT_VALUE_ADD,
                loadData: { id: waybillId }
            })
        });

        // Обновление таблицы
        tableTimerUpdate({
            selector: 'div.list_mat-value__table',
            url: ACTION_PATH.LIST_MAT_VALUE_LOAD,
            params: { waybillId: waybillId }
        });
    });
</script>