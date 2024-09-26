<div class="ui modal list_edit_add-material__modal">
    <div class="header">Добавление материала</div>
    <div class="content">
        <div class="list_edit_add-material__table table-sm table-striped"></div>
    </div>
    <div class="actions">
        <button class="ui small button list_edit_add-material__btn-apply disabled">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.apply"/>
        </button>
        <button class="ui small button list_edit_add-material__btn-all">
            <i class="icon blue bars"></i>
            Выбрать все
        </button>
        <button class="ui small button list_edit_add-material__btn-clear">
            <i class="icon blue times"></i>
            Очистить выбор
        </button>
    </div>
</div>

<script>
    $(() => {
        const $modal = $('div.list_edit_add-material__modal');
        const $btnApply = $('button.list_edit_add-material__btn-apply');
        const $btnClear = $('button.list_edit_add-material__btn-clear');
        const $btnAll = $('button.list_edit_add-material__btn-all');
        const materialTable = Tabulator.prototype.findTable('div.list_edit__material-table')[0];
        const materialIdList = '${materialIdList}';
        const parentId = '${parentId}';

        const table = new Tabulator('div.list_edit_add-material__table', {
            selectable: true,
            ajaxURL: ACTION_PATH.DETAIL_EDIT_ADD_MATERIAL_LOAD,
            ajaxRequesting: (url, params) => {
                params.idList = materialIdList;
                params.parentId = parentId;
            },
            height: 'calc(100vh * 0.5)',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                TABR_COL_ID,
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
                    materialTable.addData(arr);
                    materialTable.clearSort();
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