<div class="ui modal list_edit_add-work__modal">
    <div class="header">Добавление работ</div>
    <div class="content">
        <div class="list_edit_add-work__table table-sm table-striped"></div>
    </div>
    <div class="actions">
        <button class="ui small button list_edit_add-work__btn-apply disabled">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.apply"/>
        </button>
        <button class="ui small button list_edit_add-work__btn-all">
            <i class="icon blue bars"></i>
            Выбрать все
        </button>
        <button class="ui small button list_edit_add-work__btn-clear">
            <i class="icon blue times"></i>
            Очистить выбор
        </button>
    </div>
</div>

<script>
    $(() => {
        const $modal = $('div.list_edit_add-work__modal');
        const $btnApply = $('button.list_edit_add-work__btn-apply');
        const $btnClear = $('button.list_edit_add-work__btn-clear');
        const $btnAll = $('button.list_edit_add-work__btn-all');
        const workTable = Tabulator.prototype.findTable('div.list_edit__table')[0];

        const table = new Tabulator('div.list_edit_add-work__table', {
            selectable: true,
            ajaxURL: ACTION_PATH.LIST_EDIT_ADD_WORK_LOAD,
            ajaxRequesting: (url, params) => {
                params.workTypeData = '${workTypeData}';
            },
            height: 'calc(100vh * 0.5)',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                TABR_COL_ID,
                { title: 'Наименование', field: TABR_FIELD.NAME }
            ],
            rowSelected: () => $btnApply.toggleClass('disabled', !table.getSelectedRows().length),
            rowDeselected: () => $btnApply.toggleClass('disabled', !table.getSelectedRows().length)
        });

        // Применить выбор
        $btnApply.on({
            'click': () => {
                const data = table.getSelectedData();
                if (data.length) {
                    const arr = [];
                    data.forEach(el => arr.push({ id: el.id, name: el.name, cost: '0.00' }));
                    workTable.addData(arr);
                    workTable.clearSort();
                    $modal.modal('hide');
                }
            }
        });

        // Очистить выбор строк
        $btnClear.on({
            'click': () => {
                table.deselectRow();
                $btnApply.toggleClass('disabled', !table.getSelectedRows().length);
            }
        });

        // Выбрать все
        $btnAll.on({
            'click': () => {
                table.selectRow();
                $btnApply.toggleClass('disabled', !table.getSelectedRows().length);
            }
        });
    })
</script>