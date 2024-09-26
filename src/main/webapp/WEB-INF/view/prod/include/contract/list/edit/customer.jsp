<div class="ui modal list_edit_customer__main">
    <div class="header">Выберите заказчика</div>
    <div class="scrolling content">
        <div class="list_edit_customer__header_buttons">
            <i class="icon filter link list_edit_customer__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        </div>
        <div class="list_edit_customer__table table-sm"></div>
    </div>
    <div class="actions">
        <div class="ui small button disabled list_edit_customer__btn-select">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.select"/>
        </div>
    </div>
</div>

<script>
    $(() => {
        const $modal = $('div.list_edit_customer__main');
        const $btnFilter = $('i.list_edit_customer__btn-filter');
        const $parentEditModal = $('div.list_edit__main');
        const $customerId = $parentEditModal.find('input#customerId');
        const $addBtn = $parentEditModal.find('.list_edit__btn-add');
        const $btnSelect = $('div.list_edit_customer__btn-select');
        const specTabulator = Tabulator.prototype.findTable('div.list_edit_selected-customer_table')[0];

        // Параметры фильтра таблицы
        const tableData = tableDataFromUrlQuery(window.location.search);
        let filterData = tableData.filterData;

        const table = new Tabulator('div.list_edit_customer__table', {
            pagination: 'remote',
            paginationInitialPage: tableData.page,
            paginationSize: tableData.size,
            initialSort: tableData.sort.length ? tableData.sort : [{ column: TABR_FIELD.NAME, dir: SORT_DIR_DESC }],
            ajaxURL: ACTION_PATH.LIST_EDIT_CUSTOMER_LOAD,
            ajaxRequesting: (url, params) => {
                params.filterData = JSON.stringify(filterData);
                params.customerTypeId = $parentEditModal.find('[name="customerTypeId"]').filter(':checked').val();
            },
            ajaxSorting: true,
            layout: 'fitColumns',
            selectable: 1,
            maxHeight: '450px',
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                TABR_COL_ID,
                { title: 'Название', field: TABR_FIELD.NAME },
                { title: 'Адрес', field: TABR_FIELD.ADDRESS }
            ],
            rowClick: () => $btnSelect.toggleClass('disabled', !table.getSelectedRows().length),
            rowDblClick: (e, row) => {
                table.deselectRow();
                row.select();
                $btnSelect.trigger('click');
                table.deselectRow();
            }
        });

        // Фильтр
        $.modalFilter({
            url: VIEW_PATH.LIST_EDIT_CUSTOMER_FILTER,
            button: $btnFilter,
            filterData: () => filterData,
            onApply: data => {
                filterData = data;
                table.setData();
            }
        });

        // Кнопка выбрать
        $btnSelect.on({
            'click': () => {
                let selectedRows = table.getSelectedRows();
                let customerIdList = [];
                selectedRows.forEach(row => customerIdList.push(row.getData().id));
                let customerId = customerIdList[0];
                $customerId.val(customerId);
                $addBtn.hide();
                specTabulator.setData();
                $modal.modal('hide');
            }
        });
    });
</script>