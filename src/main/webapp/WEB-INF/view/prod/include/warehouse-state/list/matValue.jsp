<div class="list_mat-value__header">
    <h1 class="list_mat-value__header_title">${productName}</h1>
</div>
<div class="list_mat-value__table-wrap">
    <div class="list_mat-value__table table-sm table-striped"></div>
</div>

<script>
    $(() => {
        const tableSel = 'div.list_mat-value__table';
        const productId = '${id}';
        // Параметры фильтра таблицы
        const tableData = tableDataFromUrlQuery(window.location.search);
        let filterData = tableData.filterData;

        const table = new Tabulator(tableSel, {
            pagination: 'remote',
            ajaxURL: ACTION_PATH.LIST_MAT_VALUE_LOAD,
            ajaxRequesting: (url, params) => {
                params.productId = productId;
                params.filterData = JSON.stringify(filterData);
            },
            ajaxSorting: true,
            height: '100%',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                TABR_COL_ID,
                { title: 'Серийный номер', field: TABR_FIELD.SERIAL_NUMBER },
                { title: 'Договор', field: TABR_FIELD.CONTRACT },
                { title: 'Заказчик', field: TABR_FIELD.CUSTOMER },
                {
                    title: 'Письмо на пр-во',
                    field: TABR_FIELD.LETTER,
                    hozAlign: 'center',
                    width: 140,
                    resizable: false
                },
                { title: 'Приёмка', field: TABR_FIELD.ACCEPT_TYPE, headerSort: false },
                { title: 'Извещение/Акт ОТК', field: TABR_FIELD.NOTICE, headerSort: false },
                {
                    title: 'МСН',
                    field: TABR_FIELD.INTERNAL_WAYBILL,
                    hozAlign: 'center',
                    width: 100,
                    resizable: false
                },
                {
                    title: 'Дата поступления',
                    field: TABR_FIELD.ACCEPT_DATE,
                    hozAlign: 'center',
                    width: 150,
                    resizable: false,
                    formatter: 'stdDate'
                },
                {
                    title: 'Цена',
                    field: TABR_FIELD.PRICE,
                    headerSort: false,
                    formatter: 'stdMoney'
                },
                {
                    title: 'Дата отгрузки',
                    field: TABR_FIELD.SHIPMENT_DATE,
                    hozAlign: 'center',
                    width: 150,
                    resizable: false,
                    formatter: 'stdDate'
                },
                {
                    title: 'Ячейка',
                    field: TABR_FIELD.CELL,
                    hozAlign: 'center',
                    width: 80,
                    resizable: false,
                    headerSort: false
                },
                {
                    title: 'Местонахождение',
                    field: TABR_FIELD.LOCATION,
                    headerSort: false,
                    formatter: cell => {
                        const data = cell.getRow().getData();
                        return data.shipped ? 'Отгружено' : data.place;
                    }
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
                    label: '<i class="history icon blue"></i>Показать историю движения',
                    action: () => showHistory(id)
                });
                return menu;
            }
        });

        // Показать историю перемещения экземпляра
        function showHistory(id) {
            $.modalWindow({
                loadURL: VIEW_PATH.LIST_MAT_VALUE_HISTORY,
                loadData: { id: id }
            });
        }

        // Обновление таблицы
        tableTimerUpdate({
            selector: tableSel,
            url: ACTION_PATH.LIST_MAT_VALUE_LOAD,
            params: { productId: productId },
            filterData: () => filterData
        });
    });
</script>