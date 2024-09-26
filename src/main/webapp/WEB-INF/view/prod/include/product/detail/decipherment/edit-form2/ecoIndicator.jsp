<div class="ui modal decipherment_edit_form2_eco-indicator__modal">
    <div class="ui small header">Выбор базового планово-экономического показателя</div>
    <div class="content">
        <div class="decipherment_edit_form2_eco-indicator__table table-sm table-striped"></div>
    </div>
    <div class="actions">
        <div class="ui small button disabled decipherment_edit_form2_eco-indicator__btn-select">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.select"/>
        </div>
    </div>
</div>

<script>
    $(() => {
        // Текущий диалог
        const $modal = $('div.decipherment_edit_form2_eco-indicator__modal');
        const $btnSelect = $('div.decipherment_edit_form2_eco-indicator__btn-select');
        // Диалог расшифровки
        const $indicatorId = $('input.decipherment_edit_form2__indicator-id');
        const $tableIndicator = $('table.decipherment_edit_form2__table-indicator');

        // Таблица
        const table = new Tabulator('div.decipherment_edit_form2_eco-indicator__table', {
            selectable: 1,
            ajaxURL: ACTION_PATH.DETAIL_DECIPHERMENT_EDIT_FORM2_ECO_INDICATOR_LOAD,
            height: 'calc(100vh * 0.6)',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                TABR_COL_ID,
                { title: 'Наименование', field: TABR_FIELD.NAME, minWidth: 200 },
                {
                    title: 'Дата утверждения',
                    field: TABR_FIELD.APPROVE_DATE,
                    hozAlign: 'center',
                    width: 160,
                    resizable: false,
                    formatter: 'stdDate'
                }
            ],
            rowSelected: () => $btnSelect.toggleClass('disabled', !table.getSelectedRows().length),
            rowDeselected: () => $btnSelect.toggleClass('disabled', !table.getSelectedRows().length),
            dataLoaded: () => $btnSelect.toggleClass('disabled', !table.getSelectedRows().length),
            rowDblClick: (e, row) => {
                table.deselectRow();
                row.select();
                $btnSelect.trigger('click');
            }
        });

        $btnSelect.on({
            'click': () => {
                const data = table.getSelectedData();
                if (data.length === 1) {
                    $indicatorId.val(data[0].id);
                    $tableIndicator.find('tr:eq(0) > td:eq(1)').text(data[0].name);
                    $tableIndicator.find('tr:eq(1) > td:eq(1)').text(dateStdToString(data[0].approveDate));
                    $indicatorId.trigger('change');
                    $modal.modal('hide');
                }
            }
        });
    });
</script>