<div class="ui modal list_edit_add-equipment__modal">
    <div class="header">Добавление оборудования</div>
    <div class="content">
        <div class="list_edit_add-equipment__table table-sm table-striped"></div>
    </div>
    <div class="actions">
        <button class="ui small button list_edit_add-equipment__btn-apply disabled">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.apply"/>
        </button>
        <button class="ui small button list_edit_add-equipment__btn-all">
            <i class="icon blue bars"></i>
            Выбрать все
        </button>
        <button class="ui small button list_edit_add-equipment__btn-clear">
            <i class="icon blue times"></i>
            Очистить выбор
        </button>
    </div>
</div>

<script>
    $(() => {
        const $modal = $('div.list_edit_add-equipment__modal');
        const $btnApply = $('button.list_edit_add-equipment__btn-apply');
        const $btnClear = $('button.list_edit_add-equipment__btn-clear');
        const $btnAll = $('button.list_edit_add-equipment__btn-all');
        const equipmentTable = Tabulator.prototype.findTable('div.list_edit__equipment-table')[0];
        const equipmentIdList = '${equipmentIdList}';

        const table = new Tabulator('div.list_edit_add-equipment__table', {
            selectable: true,
            ajaxURL: ACTION_PATH.DETAIL_EDIT_ADD_EQUIPMENT_LOAD,
            ajaxRequesting: (url, params) => {
                params.idList = equipmentIdList;
            },
            height: 'calc(100vh * 0.5)',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                TABR_COL_ID,
                { title: 'Наименование', field: TABR_FIELD.NAME, headerSort: false },
                { title: 'Модель', field: TABR_FIELD.MODEL, headerSort: false }
            ],
            rowSelected: () => $btnApply.toggleClass('disabled', !table.getSelectedRows().length),
            rowDeselected: () => $btnApply.toggleClass('disabled', !table.getSelectedRows().length)
        });

        // Применить выбор
        $btnApply.on({
            'click': () => {
                const data = table.getSelectedData();
                if (data.length) {
                    const arr = data.map(el => {
                        return {
                            id: el.id,
                            name: el.name,
                            model: el.model
                        }
                    });
                    equipmentTable.addData(arr);
                    equipmentTable.clearSort();
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