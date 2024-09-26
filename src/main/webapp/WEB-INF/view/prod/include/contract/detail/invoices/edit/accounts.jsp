<div class="ui modal detail_invoices_edit_accounts__main">
    <div class="header">Выберете расчетный счет</div>
    <div class="content">
        <i class="icon filter link blue detail_invoices_edit_accounts__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        <div class="detail_invoices_edit_accounts__table table-sm"></div>
    </div>
    <div class="actions">
        <div class="ui small button disabled detail_invoices_edit_accounts__btn-select">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.select"/>
        </div>
    </div>
</div>

<script>
    $(() => {
        const $modal = $('div.detail_invoices_edit_accounts__main');
        const $btnFilter = $('i.detail_invoices_edit_accounts__btn-filter');
        const $parentAddModal = $('div.detail_invoices_edit__main');
        const $accountId = $parentAddModal.find('input#accountId');
        const $btnSelect = $('div.detail_invoices_edit_accounts__btn-select');
        const specTabulator = Tabulator.prototype.findTable('div.detail_invoices_edit_selected-account_table')[0];

        // Параметры фильтра таблицы
        const tableData = tableDataFromUrlQuery(window.location.search);
        let filterData = tableData.filterData;

        const table = new Tabulator('div.detail_invoices_edit_accounts__table', {
            pagination: 'remote',
            paginationInitialPage: tableData.page,
            paginationSize: tableData.size,
            ajaxURL: ACTION_PATH.DETAIL_INVOICE_EDIT_ACCOUNTS_LOAD,
            ajaxRequesting: (url, params) => {
                params.filterData = JSON.stringify(filterData);
            },
            selectable: 1,
            ajaxSorting: true,
            maxHeight: '450px',
            layout: 'fitColumns',
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                { title: 'Расчетный счет', field: TABR_FIELD.ACCOUNT },
                { title: 'Банк', field: TABR_FIELD.BANK_NAME },
                { title: 'Комментарий', field: TABR_FIELD.COMMENT }
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
            url: VIEW_PATH.DETAIL_INVOICES_EDIT_FILTER,
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
                let accountIdList = [];
                selectedRows.forEach(row => accountIdList.push(row.getData().id));
                let accountId = accountIdList[0];
                $accountId.val(accountId);
                specTabulator.setData();
                $modal.modal('hide');
            }
        });
    });
</script>