<div class="ui modal list_mat-value_history__modal">
    <div class="ui small header">История движения экземляра изделия</div>
    <div class="content">
        <div class="list_mat-value_history__table table-sm table-striped"></div>
    </div>
    <div class="actions">
        <div class="ui small button list_mat-value_history__btn-close">
            <i class="icon blue times"></i>
            Закрыть
        </div>
    </div>
</div>

<script>
    $(() => {
        const mvId = '${id}';
        const $modal = $('div.list_mat-value_history__modal');
        const $btnClose = $('div.list_mat-value_history__btn-close');

        new Tabulator('div.list_mat-value_history__table', {
            ajaxURL: ACTION_PATH.LIST_MAT_VALUE_HISTORY_LOAD,
            ajaxRequesting: (url, params) => {
                params.mvId = mvId;
            },
            headerSort: false,
            height: 'calc(100vh * 0.7)',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                {
                    title: 'Тип',
                    field: TABR_FIELD.TYPE,
                    width: 150,
                    resizable: false,
                    formatter: cell => {
                        let value = cell.getValue();
                        if (value === 1) value = 'МСН';
                        else if (value === 2) value = 'Накладная на отгрузку'
                        else value = '';
                        return value;
                    }
                },
                {
                    title: 'Номер',
                    hozAlign: 'center',
                    field: TABR_FIELD.NUMBER,
                    minWidth: 140
                },
                {
                    title: 'Дата подписания',
                    field: TABR_FIELD.SIGN_DATE,
                    hozAlign: 'center',
                    width: 150,
                    resizable: false,
                    formatter: 'stdDate'
                }
            ]
        });

        // Закрытие окна
        $btnClose.on({
            'click': () => $modal.modal('hide')
        });
    })
</script>