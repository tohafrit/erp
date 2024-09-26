<div class="ui modal list_report-first_product__modal">
    <div class="header">Выбор изделия</div>
    <div class="content">
        <div class="list_report-first_product__buttons">
            <i class="icon filter link blue list_report-first_product__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        </div>
        <div class="list_report-first_product__table table-sm table-striped"></div>
    </div>
    <div class="actions">
        <div class="ui small button list_report-first_product__btn-select disabled">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.select"/>
        </div>
    </div>
</div>

<script>
    $(() => {
        const $modal = $('div.list_report-first_product__modal');
        const $btnSelect = $('div.list_report-first_product__btn-select');
        const $btnFilter = $('i.list_report-first_product__btn-filter');
        const $productId = $('input.list_report-first__product-id');
        const $productName = $('span.list_report-first__product-name');
        let filterData = {};

        const table = new Tabulator('div.list_report-first_product__table', {
            selectable: 1,
            pagination: 'remote',
            ajaxURL: ACTION_PATH.LIST_REPORT_FIRST_PRODUCT_LOAD,
            ajaxRequesting: (url, params) => {
                params.filterData = JSON.stringify(filterData);
            },
            ajaxSorting: true,
            height: 'calc(100vh * 0.6)',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                TABR_COL_ID,
                { title: 'Изделие', field: TABR_FIELD.PRODUCT, minWidth: 200 },
                { title: 'Краткая техническая характеристика', field: TABR_FIELD.TYPE, minWidth: 200 },
                {
                    title: 'Серийное',
                    field: TABR_FIELD.SERIAL,
                    resizable: false,
                    hozAlign: 'center',
                    formatter: 'lightMark',
                    headerSort: false
                }
            ],
            rowSelected: () => $btnSelect.toggleClass('disabled', !table.getSelectedRows().length),
            rowDeselected: () => $btnSelect.toggleClass('disabled', !table.getSelectedRows().length),
            rowDblClick: (e, row) => {
                table.deselectRow();
                row.select();
                $btnSelect.trigger('click');
            }
        });

        // Выбор обоснования
        $btnSelect.on({
            'click': () => {
                const data = table.getSelectedData();
                if (data.length === 1) {
                    $productId.val(data[0].id);
                    $productName.text(data[0].product);
                    $productId.trigger('change');
                    $modal.modal('hide');
                }
            }
        });

        // Фильтр
        $.modalFilter({
            url: VIEW_PATH.LIST_REPORT_FIRST_PRODUCT_FILTER,
            button: $btnFilter,
            filterData: () => filterData,
            onApply: data => {
                filterData = data;
                table.setData();
            }
        });
    });
</script>