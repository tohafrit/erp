<div class="ui modal list_edit_contract__main">
    <div class="header">Выберите договор или доп. соглашение</div>
    <div class="scrolling content">
        <div class="list_edit_contract__header_buttons">
            <i class="icon filter blue link list_edit_contract__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        </div>
        <div class="list_edit_contract__table table-sm"></div>
    </div>
    <div class="actions">
        <div class="ui small button disabled list_edit_contract__btn-select">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.select"/>
        </div>
    </div>
</div>

<script>
    $(() => {
        const $modal = $('div.list_edit_contract__main');
        const $btnFilter = $('i.list_edit_contract__btn-filter');
        const $btnSelect = $('div.list_edit_contract__btn-select');

        // Параметры фильтра таблицы
        let filterData = { pzCopy: true, available: true };

        const table = new Tabulator('div.list_edit_contract__table', {
            pagination: 'remote',
            ajaxURL: ACTION_PATH.LIST_EDIT_CONTRACT_LOAD,
            ajaxRequesting: (url, params) => {
                params.filterData = JSON.stringify(filterData);
            },
            ajaxSorting: true,
            layout: 'fitColumns',
            selectable: 1,
            height: 'calc(100vh * 0.5)',
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                TABR_COL_ID,
                { title: 'Номер', field: TABR_FIELD.FULL_NUMBER },
                { title: 'Заказчик', field: TABR_FIELD.CUSTOMER },
                {
                    title: 'Передано в ПЗ',
                    field: TABR_FIELD.PZ_COPY_DATE,
                    resizable: false,
                    headerSort: false,
                    hozAlign: 'center',
                    formatter: cell => {
                        const date = dateStdToString(cell.getValue());
                        return booleanToLight(cell.getValue() != null, { onTrue: 'Передан ' + date, onFalse: 'Не передан' });
                    }
                }
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
            url: VIEW_PATH.LIST_EDIT_CONTRACT_FILTER,
            button: $btnFilter,
            filterData: () => filterData,
            onApply: data => {
                filterData = data;
                table.setData();
            }
        });

        // Выбор договора
        $btnSelect.on({
            'click': () => {
                let selectedRows = table.getSelectedRows();
                let contractSectionIdList = [];
                selectedRows.forEach(row => contractSectionIdList.push(row.getData().id));
                let contractSectionId = contractSectionIdList[0];
                $.modalWindow({
                    loadURL: VIEW_PATH.LIST_EDIT_CONTRACT_PRODUCT,
                    loadData: {
                        contractSectionId: contractSectionId
                    }
                });
            }
        });
    });
</script>