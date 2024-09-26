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
        const tableSelector = 'div.list_mat-value__table';
        const $btnAdd = $('i.list_mat-value__btn-add');

        const table = new Tabulator(tableSelector, {
            ajaxURL: ACTION_PATH.LIST_MAT_VALUE_LOAD,
            ajaxRequesting: (url, params) => {
                params.waybillId = waybillId;
            },
            headerSort: false,
            height: '100%',
            layout: 'fitDataFill',
            groupBy: [TABR_FIELD.PRODUCT_NAME, TABR_FIELD.PRICE],
            groupStartOpen: [true, true],
            groupHeader: [
                value => 'Изделие: ' + value,
                (value, count, data) => 'Цена: ' + formatAsCurrency(value) + ' | Кол-во: ' + data[0].amount + ' | Стоимость: ' + data[0].cost
            ],
            groupToggleElement: false,
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                {
                    title: 'Заводской номер',
                    field: TABR_FIELD.SERIAL_NUMBER,
                    minWidth: 140
                },
                {
                    title: 'Отгружено',
                    field: TABR_FIELD.SHIPPED,
                    resizable: false,
                    hozAlign: 'center',
                    formatter: 'lightMark'
                },
                {
                    title: 'Проверено',
                    field: TABR_FIELD.CHECKED,
                    resizable: false,
                    hozAlign: 'center',
                    formatter: 'lightMark'
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
                }).done(() => table.setData())
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
            selector: tableSelector,
            url: ACTION_PATH.LIST_MAT_VALUE_LOAD,
            params: { waybillId: waybillId }
        });
    });
</script>