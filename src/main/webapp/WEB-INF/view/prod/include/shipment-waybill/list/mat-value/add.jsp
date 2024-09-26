<div class="ui modal list_mat-value_add__modal">
    <div class="ui small header">Добавление изделия в накладную</div>
    <div class="content">
        <form class="ui form">
            <div class="field inline">
                <label>Накладная</label>
                <span class="ui text">${number}</span>
            </div>
        </form>
        <div class="list_mat-value_add__buttons">
            <i class="icon filter link blue list_mat-value_add__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
            <i class="icon tasks link blue list_mat-value_add__btn-select-all" title="Выбрать все изделия, готовые к отгрузке"></i>
        </div>
        <div class="list_mat-value_add__table table-sm table-striped"></div>
    </div>
    <div class="actions">
        <div class="ui small button list_mat-value_add__btn-apply disabled">
            <i class="icon blue save"></i>
            <fmt:message key="label.button.apply"/>
        </div>
    </div>
</div>

<script>
    $(() => {
        const waybillId = '${waybillId}';
        const $modal = $('div.list_mat-value_add__modal');
        const $btnApply = $('div.list_mat-value_add__btn-apply');
        const $btnFilter = $('i.list_mat-value_add__btn-filter');
        const $btnSelectAll = $('i.list_mat-value_add__btn-select-all');
        const matValueTable = Tabulator.prototype.findTable('div.list_mat-value__table')[0];
        let filterData = {};

        const table = new Tabulator('div.list_mat-value_add__table', {
            selectable: true,
            ajaxURL: ACTION_PATH.LIST_MAT_VALUE_ADD_LOAD,
            ajaxRequesting: (url, params) => {
                params.waybillId = waybillId;
                params.filterData = JSON.stringify(filterData);
            },
            ajaxSorting: true,
            height: 'calc(100vh * 0.5)',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                TABR_COL_ID,
                { title: 'Изделие', field: TABR_FIELD.PRODUCT, minWidth: 200 },
                { title: 'Извещение', field: TABR_FIELD.NOTICE, width: 140, resizable: false },
                { title: 'МСН', field: TABR_FIELD.INTERNAL_WAYBILL, width: 100, resizable: false },
                {
                    title: 'Кол-во',
                    field: TABR_FIELD.AMOUNT,
                    width: 80,
                    resizable: false,
                    hozAlign: 'center',
                    headerSort: false
                },
                {
                    title: 'Серийный номер',
                    field: TABR_FIELD.SERIAL_NUMBER,
                    minWidth: 150,
                    headerSort: false
                },
                {
                    title: 'Готовность к отгрузке',
                    field: TABR_FIELD.READY,
                    resizable: false,
                    hozAlign: 'center',
                    formatter: 'lightMark',
                    headerSort: false
                }
            ],
            rowSelected: () => $btnApply.toggleClass('disabled', !table.getSelectedRows().length),
            rowClick: (e, row) => {
                if (!e.ctrlKey) table.deselectRow();
                row.select();
            },
            rowContext: (e, row) => {
                if (!row.isSelected()) {
                    table.deselectRow();
                    row.select();
                }
            }
        });

        // Выбрать все
        $btnSelectAll.on({
            'click': () => {
                table.selectRow(table.getData().filter(el => el.ready).map(el => el.id));
                $btnApply.toggleClass('disabled', !table.getSelectedRows().length);
            }
        });

        // Выбор записей
        $btnApply.on({
            'click': () => {
                const data = table.getSelectedData();
                if (data.length) {
                    $.post({
                        url: ACTION_PATH.LIST_MAT_VALUE_ADD_APPLY,
                        data: { idList: data.map(el => el.id).join(), waybillId: waybillId },
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => {
                        matValueTable.setData();
                        $modal.modal('hide');
                    });
                }
            }
        });

        // Фильтр
        $.modalFilter({
            url: VIEW_PATH.LIST_MAT_VALUE_ADD_FILTER,
            button: $btnFilter,
            filterData: () => filterData,
            onApply: data => {
                filterData = data;
                table.setData();
            }
        });
    })
</script>