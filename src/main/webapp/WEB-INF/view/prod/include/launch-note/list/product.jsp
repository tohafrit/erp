<div class="list_product__header">
    <h1 class="list_product__header_title"></h1>
    <div class="list_product__table_buttons">
        <i class="icon filter link blue list_product__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
    </div>
</div>
<div class="list_product__table-wrap">
    <div class="list_product__table table-sm table-striped"></div>
</div>

<script>
    $(() => {
        const noteId = parseInt('${id}');
        const $btnFilter = $('i.list_product__btn-filter');
        let filterData = {};

        const table = new Tabulator('div.list_product__table', {
            pagination: 'remote',
            ajaxURL: ACTION_PATH.LIST_PRODUCT_LOAD,
            ajaxRequesting: (url, params) => {
                params.filterData = JSON.stringify(filterData);
                params.noteId = noteId;
            },
            ajaxSorting: true,
            height: '100%',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                TABR_COL_ID,
                { title: 'Запуск', field: TABR_FIELD.NUMBER_IN_YEAR, headerSort: false },
                { title: 'Наименование изделия', field: TABR_FIELD.PRODUCT_NAME, headerSort: false },
                { title: 'По договору', field: TABR_FIELD.CONTRACT_AMOUNT, headerSort: false },
                { title: 'Задел по договору', field: TABR_FIELD.RF_CONTRACT_AMOUNT, headerSort: false },
                { title: 'Задел для сборки', field: TABR_FIELD.RF_ASSEMBLED_AMOUNT, headerSort: false },
                { title: 'Исп-но заделов по договору', field: TABR_FIELD.UFRF_CONTRACT_AMOUNT, headerSort: false },
                { title: 'Исп-но заделов для сборки', field: TABR_FIELD.UFRF_ASSEMBLED_AMOUNT, headerSort: false },
                { title: 'В составе исп-х заделов', field: TABR_FIELD.UFRF_CONTRACT_IN_OTHER_PRODUCT_AMOUNT, headerSort: false }
            ],
            rowClick: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowContext: (e, row) => {
                table.deselectRow();
                row.select();
            }
        });

        // Фильтр
        $.modalFilter({
            url: VIEW_PATH.LIST_PRODUCT_FILTER,
            button: $btnFilter,
            filterData: () => filterData,
            onApply: data => {
                filterData = data;
                table.setData();
            }
        });
    });
</script>