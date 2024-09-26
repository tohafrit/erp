<div class="ui modal decipherment_search_invoice__modal">
    <div class="header"><fmt:message key="decipherment.form.searchInvoice.title"/></div>
    <div class="content">
        <i class="icon filter link blue js-invoice-filter" title="Фильтр"></i>
        <div class="decipherment_search_invoice__table table-sm table-striped"></div>
    </div>
    <div class="actions">
        <div class="ui small button disabled js-select-btn">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.select"/>
        </div>
    </div>
</div>

<script>
    $(() => {
        const $parentDialog = $('div.decipherment_product_invoice__modal');
        const $dialog = $('div.decipherment_search_invoice__modal');
        const $filterBtn = $dialog.find('.js-invoice-filter');
        const $selectBtn = $dialog.find('.js-select-btn');
        const componentId = '${componentId}';
        const cell = '${cell}';
        const selectedId = '${selectedInvoiceId}';
        //
        let filterData = {};
        filterData.dateFrom = '${filterDateFrom}';
        filterData.dateTo = '${filterDateTo}';
        filterData.plantId = '${filterPlantId}';

        const table = new Tabulator('div.decipherment_search_invoice__table', {
            selectable: 1,
            headerSort: false,
            ajaxURL: '/decipherment/search-invoice/load-invoices',
            ajaxRequesting: (url, params) => {
                params.filterData = JSON.stringify(filterData);
                params.cell = cell;
            },
            layout: 'fitDataFill',
            height: 'calc(100vh * 0.6)',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: 'Наименование', field: TABR_FIELD.NAME },
                {
                    title: 'Дата',
                    field: TABR_FIELD.DATE,
                    hozAlign: 'center',
                    formatter: 'stdDate'
                },
                { title: 'Договор', field: TABR_FIELD.CONTRACT_NUMBER },
                { title: 'Поставщик', field: TABR_FIELD.SUPPLIER_NAME },
                { title: 'Цена', field: TABR_FIELD.PRICE },
                {
                    title: 'Количество',
                    headerHozAlign: 'center',
                    columns: [
                        { title: 'Нач.', field: TABR_FIELD.INITIAL_QUANTITY },
                        { title: 'Тек.', field: TABR_FIELD.CURRENT_QUANTITY },
                        { title: 'Рез.', field: TABR_FIELD.RESERVED_QUANTITY },
                        { title: 'Брак.', field: TABR_FIELD.WASTED_QUANTITY },
                        { title: 'Не прин.', field: TABR_FIELD.NOT_ACCEPTED_QUANTITY }
                    ]
                }
            ],
            rowClick: () => $selectBtn.toggleClass('disabled', !table.getSelectedRows().length),
            rowDblClick: (e, row) => {
                table.deselectRow();
                row.select();
                $selectBtn.trigger('click');
            },
            dataLoaded: () => {
                if (selectedId) table.selectRow(selectedId);
                $selectBtn.toggleClass('disabled', !table.getSelectedRows().length)
            }
        });

        // Фильтр
        $.modalFilter({
            url: '/decipherment/search-invoice/filter-invoice',
            button: $filterBtn,
            filterData: () => filterData,
            onApply: data => {
                filterData = data;
                table.setData();
            }
        });

        // Выбор накладной
        $selectBtn.on({
            'click': () => {
                const data = table.getSelectedData();
                if (data.length === 1) {
                    const rowData = data[0];
                    $parentDialog.find('tr[data-id=' + componentId + ']').trigger('setInvoice', [{
                        id: rowData.id,
                        name: rowData.name,
                        price: rowData.price,
                        supplier: rowData.supplierName,
                        date: dateStdToString(rowData.date)
                    }]);
                    $dialog.modal('hide');
                }
            }
        });
    });
</script>