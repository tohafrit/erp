<div class="ui modal list_edit_add-group__modal">
    <div class="header">Добавление группы</div>
    <div class="content">
        <div class="list_edit_add-group__table table-sm table-striped"></div>
    </div>
    <div class="actions">
        <button class="ui small button list_edit_add-group__btn-apply disabled">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.apply"/>
        </button>
        <button class="ui small button list_edit_add-group__btn-all">
            <i class="icon blue bars"></i>
            Выбрать все
        </button>
        <button class="ui small button list_edit_add-group__btn-clear">
            <i class="icon blue times"></i>
            Очистить выбор
        </button>
    </div>
</div>

<script>
    $(() => {
        const $modal = $('div.list_edit_add-group__modal');
        const $btnApply = $('button.list_edit_add-group__btn-apply');
        const $btnClear = $('button.list_edit_add-group__btn-clear');
        const $btnAll = $('button.list_edit_add-group__btn-all');
        const groupTable = Tabulator.prototype.findTable('div.list_edit__table')[0];

        const table = new Tabulator('div.list_edit_add-group__table', {
            selectable: true,
            ajaxRequesting: (url, params) => {
                params.classGroupData = '${classGroupData}';
            },
            ajaxURL: ACTION_PATH.LIST_EDIT_ADD_GROUP_LOAD,
            height: 'calc(100vh * 0.5)',
            layout: 'fitDataStretch',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                TABR_COL_ID,
                { title: 'Номер', field: TABR_FIELD.NUMBER },
                { title: 'Характеристика', field: TABR_FIELD.NAME }
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
                    data.forEach(el => arr.push({
                        id: el.id,
                        number: el.number,
                        name: el.name,
                        price: '0.00'
                    }));
                    groupTable.addData(arr);
                    groupTable.clearSort();
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