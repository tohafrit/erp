<div class="ui modal detail_decipherment_add__modal">
    <div class="header">Добавление расшифровки</div>
    <div class="content">
        <div class="detail_decipherment_add__table table-sm table-striped"></div>
    </div>
    <div class="actions">
        <button class="ui small button detail_decipherment_add__btn-apply disabled">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.apply"/>
        </button>
        <button class="ui small button detail_decipherment_add__btn-all">
            <i class="icon blue bars"></i>
            Выбрать все
        </button>
        <button class="ui small button detail_decipherment_add__btn-clear">
            <i class="icon blue times"></i>
            Очистить выбор
        </button>
    </div>
</div>

<script>
    $(() => {
        const periodId = '${periodId}';
        const $modal = $('div.detail_decipherment_add__modal');
        const $btnApply = $('button.detail_decipherment_add__btn-apply');
        const $btnClear = $('button.detail_decipherment_add__btn-clear');
        const $btnAll = $('button.detail_decipherment_add__btn-all');
        const deciphermentTable = Tabulator.prototype.findTable('div.detail_decipherment__table')[0];

        const table = new Tabulator('div.detail_decipherment_add__table', {
            selectable: true,
            headerSort: false,
            data: JSON.parse('${std:escapeJS(data)}'),
            height: 'calc(100vh * 0.5)',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: 'Наименование', field: TABR_FIELD.NAME }
            ],
            rowSelected: () => $btnApply.toggleClass('disabled', !table.getSelectedRows().length),
            rowDeselected: () => $btnApply.toggleClass('disabled', !table.getSelectedRows().length),
            rowDblClick: (e, row) => {
                table.deselectRow();
                row.select();
                $btnApply.trigger('click');
            }
        });

        // Применить выбор
        $btnApply.on({
            'click': () => {
                const data = table.getSelectedData();
                if (data.length) {
                    $.post({
                        url: ACTION_PATH.DETAIL_DECIPHERMENT_ADD_SELECT,
                        data: { periodId: periodId, typeIdList: data.map(i => i.id).join(',') },
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => {
                        deciphermentTable.setData();
                        $modal.modal('hide');
                    });
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