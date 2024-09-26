<div class="ui modal list_edit_add-area__modal">
    <div class="header">Добавление участка</div>
    <div class="content">
        <div class="list_edit_add-area__table table-sm table-striped"></div>
    </div>
    <div class="actions">
        <button class="ui small button list_edit_add-area__btn-apply disabled">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.apply"/>
        </button>
        <button class="ui small button list_edit_add-area__btn-all">
            <i class="icon blue bars"></i>
            Выбрать все
        </button>
        <button class="ui small button list_edit_add-area__btn-clear">
            <i class="icon blue times"></i>
            Очистить выбор
        </button>
    </div>
</div>

<script>
    $(() => {
        const $modal = $('div.list_edit_add-area__modal');
        const $btnApply = $('button.list_edit_add-area__btn-apply');
        const $btnClear = $('button.list_edit_add-area__btn-clear');
        const $btnAll = $('button.list_edit_add-area__btn-all');
        const areaTable = Tabulator.prototype.findTable('div.list_edit__area-table')[0];
        const areaIdList = '${areaIdList}';

        const table = new Tabulator('div.list_edit_add-area__table', {
            selectable: true,
            ajaxURL: ACTION_PATH.DETAIL_EDIT_ADD_AREA_LOAD,
            ajaxRequesting: (url, params) => {
                params.idList = areaIdList;
            },
            height: 'calc(100vh * 0.5)',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                TABR_COL_ID,
                { title: 'Код', field: TABR_FIELD.CODE, headerSort: false },
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
                            code: el.code,
                            name: el.name
                        }
                    });
                    areaTable.addData(arr);
                    areaTable.clearSort();
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