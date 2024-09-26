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
        </div>
        <div class="list_mat-value_add__table table-sm table-striped"></div>
        <div class="list_mat-value_add__title-table-select">Выбранные предъявления</div>
        <div class="list_mat-value_add__table-select table-sm table-striped"></div>
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
        const tableSel = 'div.list_mat-value_add__table';
        const $modal = $('div.list_mat-value_add__modal');
        const $btnApply = $('div.list_mat-value_add__btn-apply');
        const $btnFilter = $('i.list_mat-value_add__btn-filter');
        const matValueTable = Tabulator.prototype.findTable('div.list_mat-value__table')[0];
        let filterData = {};

        const tableSelect = new Tabulator('div.list_mat-value_add__table-select', {
            selectable: true,
            headerSort: false,
            height: 'calc(100vh * 0.3)',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                TABR_COL_ID,
                { title: 'Изделие', field: TABR_FIELD.PRODUCT, minWidth: 200 },
                {
                    title: 'Кол-во',
                    field: TABR_FIELD.AMOUNT,
                    width: 80,
                    resizable: false,
                    hozAlign: 'center',
                    headerSort: false
                },
                { title: 'Серийные номера', field: TABR_FIELD.SERIAL_NUMBER },
                { title: 'Извещение', field: TABR_FIELD.NOTICE },
                {
                    title: 'Дата извещения',
                    field: TABR_FIELD.NOTICE_DATE,
                    hozAlign: 'center',
                    width: 150,
                    resizable: false,
                    formatter: 'stdDate'
                },
                {
                    title: 'Пройден ОТК',
                    field: TABR_FIELD.TECH_CONTROLLED,
                    resizable: false,
                    hozAlign: 'center',
                    formatter: 'lightMark',
                    headerSort: false
                },
                {
                    title: 'Упаковано',
                    field: TABR_FIELD.PACKED,
                    resizable: false,
                    hozAlign: 'center',
                    formatter: 'lightMark',
                    headerSort: false
                }
            ],
            dataChanged: data => $btnApply.toggleClass('disabled', !data.length),
            rowClick: (e, row) => {
                if (!e.ctrlKey) table.deselectRow();
                row.select();
            },
            rowContext: (e, row) => {
                if (!row.isSelected()) {
                    table.deselectRow();
                    row.select();
                }
            },
            rowContextMenu: () => {
                const menu = [];
                menu.push({
                    label: '<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>',
                    action: () => tableSelect.deleteRow(tableSelect.getSelectedData().map(el => el.id))
                });
                return menu;
            }
        });

        const table = new Tabulator(tableSel, {
            selectable: true,
            pagination: 'remote',
            initialSort: [{ column: TABR_FIELD.NOTICE_DATE, dir: SORT_DIR_DESC }],
            ajaxURL: ACTION_PATH.LIST_MAT_VALUE_ADD_LOAD,
            ajaxRequesting: (url, params) => {
                params.filterData = JSON.stringify(filterData);
            },
            ajaxSorting: true,
            height: 'calc(100vh * 0.35)',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                TABR_COL_ID,
                { title: 'Изделие', field: TABR_FIELD.PRODUCT, minWidth: 200 },
                {
                    title: 'Кол-во',
                    field: TABR_FIELD.AMOUNT,
                    width: 80,
                    resizable: false,
                    hozAlign: 'center',
                    headerSort: false
                },
                { title: 'Серийные номера', field: TABR_FIELD.SERIAL_NUMBER, headerSort: false },
                { title: 'Извещение', field: TABR_FIELD.NOTICE },
                {
                    title: 'Дата извещения',
                    field: TABR_FIELD.NOTICE_DATE,
                    hozAlign: 'center',
                    width: 150,
                    resizable: false,
                    formatter: 'stdDate'
                },
                {
                    title: 'Пройден ОТК',
                    field: TABR_FIELD.TECH_CONTROLLED,
                    resizable: false,
                    hozAlign: 'center',
                    formatter: 'lightMark',
                    headerSort: false
                },
                {
                    title: 'Упаковано',
                    field: TABR_FIELD.PACKED,
                    resizable: false,
                    hozAlign: 'center',
                    formatter: 'lightMark',
                    headerSort: false
                }
            ],
            rowClick: (e, row) => {
                if (!e.ctrlKey) table.deselectRow();
                row.select();
            },
            rowContext: (e, row) => {
                if (!row.isSelected()) {
                    table.deselectRow();
                    row.select();
                }
            },
            rowContextMenu: () => {
                const menu = [];
                menu.push({
                    label: '<i class="check icon blue"></i>Выбрать',
                    action: () => {
                        const data = table.getSelectedData();
                        if (data.some(el => !el.packed)) alertDialog({ message: 'Выбор неупакованных изделий в предъявлении недоступен' });
                        if (data.some(el => !el.techControlled)) alertDialog({ message: 'Выбор изделий не прошедших ОТК в предъявлении недоступен' });
                        else tableSelect.updateOrAddData(data);
                    }
                });
                return menu;
            }
        });

        // Выбор записей
        $btnApply.on({
            'click': () => {
                const data = tableSelect.getData();
                if (data.length) {
                    $.post({
                        url: ACTION_PATH.LIST_MAT_VALUE_ADD_APPLY,
                        data: { idList: data.map(el => el.id).join(), waybillId: waybillId },
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => {
                        matValueTable.setSort(TABR_SORT_ID_DESC);
                        matValueTable.setPage(1);
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

        // Обновление таблицы
        tableTimerUpdate({
            selector: tableSel,
            url: ACTION_PATH.LIST_MAT_VALUE_ADD_LOAD,
            filterData: () => filterData
        });
    })
</script>