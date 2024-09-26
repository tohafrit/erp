<div class="list_product__header">
    <h1 class="list_product__header_title">Изделия</h1>
</div>
<div class="list_product__table-wrap">
    <div class="list_product__table table-sm table-striped"></div>
</div>

<script>
    $(() => {
        const pricePeriodId = parseInt('${pricePeriodId}');
        const tableData = tableDataFromUrlQuery(window.location.search);
        const filterData = tableData.filterData;

        const table = new Tabulator('div.list_product__table', {
            pagination: 'remote',
            ajaxURL: ACTION_PATH.LIST_PRODUCT_LOAD,
            ajaxRequesting: (url, params) => {
                params.filterData = JSON.stringify(filterData);
                params.pricePeriodId = pricePeriodId;
            },
            ajaxSorting: true,
            height: '100%',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                TABR_COL_ID,
                { title: 'Изделие', field: TABR_FIELD.NAME },
                { title: 'Период', field: TABR_FIELD.PERIOD, headerSort: false },
                { title: 'Цена без упаковки', field: TABR_FIELD.PRICE_WO_PACK, headerSort: false },
                { title: 'Цена с упаковкой ', field: TABR_FIELD.PRICE_PACK, headerSort: false },
                { title: 'Цена с упаковкой и СИ', field: TABR_FIELD.PRICE_PACK_RESEARCH, headerSort: false }
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
                const periodId = data.periodId;
                menu.push({
                    label: '<i class="calculator icon blue"></i>Расчет цены',
                    action: () => window.open(window.location.origin + '/prod/product/detail/' + id + '/decipherment?periodId=' + periodId)
                });
                return menu;
            }
        });
    });
</script>