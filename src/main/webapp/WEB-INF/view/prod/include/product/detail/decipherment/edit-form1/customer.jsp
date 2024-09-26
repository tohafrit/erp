<div class="ui modal decipherment_edit_form1_customer__modal">
    <div class="ui small header">Выбор заказчика</div>
    <div class="content">
        <div class="decipherment_edit_form1_customer__buttons">
            <i class="icon filter link blue decipherment_edit_form1_customer__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        </div>
        <div class="decipherment_edit_form1_customer__table table-sm table-striped"></div>
    </div>
    <div class="actions">
        <div class="ui small button disabled decipherment_edit_form1_customer__btn-select">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.select"/>
        </div>
    </div>
</div>

<script>
    $(() => {
        // Текущий диалог
        const $modal = $('div.decipherment_edit_form1_customer__modal');
        const $btnSelect = $('div.decipherment_edit_form1_customer__btn-select');
        const $btnFilter = $('i.decipherment_edit_form1_customer__btn-filter');
        // Диалог расшифровки
        const $customerId = $('input.decipherment_edit_form1__customer-id');
        const $tableCustomer = $('table.decipherment_edit_form1__table-customer');
        let filterData = {};

        // Таблица
        const table = new Tabulator('div.decipherment_edit_form1_customer__table', {
            selectable: 1,
            pagination: 'remote',
            ajaxURL: ACTION_PATH.DETAIL_DECIPHERMENT_EDIT_FORM1_CUSTOMER_LOAD,
            ajaxRequesting: (url, params) => {
                params.filterData = JSON.stringify(filterData);
            },
            ajaxSorting: true,
            height: 'calc(100vh * 0.6)',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                TABR_COL_ID,
                { title: 'Наименование', field: TABR_FIELD.NAME, minWidth: 200 },
                {
                    title: 'Местоположение',
                    field: TABR_FIELD.LOCATION,
                    variableHeight: true,
                    minWidth: 200,
                    formatter: 'textarea'
                }
            ],
            rowSelected: () => $btnSelect.toggleClass('disabled', !table.getSelectedRows().length),
            rowDeselected: () => $btnSelect.toggleClass('disabled', !table.getSelectedRows().length),
            dataLoaded: () => $btnSelect.toggleClass('disabled', !table.getSelectedRows().length),
            rowDblClick: (e, row) => {
                table.deselectRow();
                row.select();
                $btnSelect.trigger('click');
            }
        });

        $btnSelect.on({
            'click': () => {
                const data = table.getSelectedData();
                if (data.length === 1) {
                    $customerId.val(data[0].id);
                    $tableCustomer.find('tr:eq(0) > td:eq(1)').text(data[0].name);
                    $tableCustomer.find('tr:eq(1) > td:eq(1)').text(data[0].location);
                    $customerId.trigger('change');
                    $modal.modal('hide');
                }
            }
        });

        // Фильтр
        $.modalFilter({
            url: VIEW_PATH.DETAIL_DECIPHERMENT_EDIT_FORM1_CUSTOMER_FILTER,
            button: $btnFilter,
            filterData: () => filterData,
            onApply: data => {
                filterData = data;
                table.setData();
            }
        });
    });
</script>