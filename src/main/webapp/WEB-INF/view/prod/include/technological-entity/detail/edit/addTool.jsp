<div class="ui modal list_edit_add-tool__modal">
    <div class="header">Добавление оснастки</div>
    <div class="content">
        <div class="list_edit_add-tool__table table-sm table-striped"></div>
    </div>
    <div class="actions">
        <button class="ui small button list_edit_add-tool__btn-apply disabled">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.apply"/>
        </button>
        <button class="ui small button list_edit_add-tool__btn-all">
            <i class="icon blue bars"></i>
            Выбрать все
        </button>
        <button class="ui small button list_edit_add-tool__btn-clear">
            <i class="icon blue times"></i>
            Очистить выбор
        </button>
    </div>
</div>

<script>
    $(() => {
        const $modal = $('div.list_edit_add-tool__modal');
        const $btnApply = $('button.list_edit_add-tool__btn-apply');
        const $btnClear = $('button.list_edit_add-tool__btn-clear');
        const $btnAll = $('button.list_edit_add-tool__btn-all');
        const toolTable = Tabulator.prototype.findTable('div.list_edit__tool-table')[0];
        const toolIdList = '${toolIdList}';

        const table = new Tabulator('div.list_edit_add-tool__table', {
            selectable: true,
            ajaxURL: ACTION_PATH.DETAIL_EDIT_ADD_TOOL_LOAD,
            ajaxRequesting: (url, params) => {
                params.idList = toolIdList;
            },
            height: 'calc(100vh * 0.5)',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                TABR_COL_ID,
                { title: 'Обозначение', field: TABR_FIELD.SIGN, headerSort: false },
                { title: 'Наименование', field: TABR_FIELD.NAME, headerSort: false }
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
                            name: el.name
                        }
                    });
                    toolTable.addData(arr);
                    toolTable.clearSort();
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