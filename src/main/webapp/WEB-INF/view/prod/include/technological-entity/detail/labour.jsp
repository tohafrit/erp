<div class="ui modal detail_labour__modal">
    <div class="header">Трудоемкость</div>
    <div class="content">
        <div class="detail__labour-table table-sm table-striped"></div>
    </div>
    <div class="actions">
        <div class="ui small button detail-labour__btn-cancel">
            <i class="icon blue times"></i>
            <fmt:message key="label.button.cancellation"/>
        </div>
    </div>
</div>

<script>
    $(() => {
        const entityId = '${entityId}';
        const $modal = $('div.detail_labour__modal');
        const $btnCancel = $modal.find('.detail-labour__btn-cancel');

        new Tabulator('div.detail__labour-table', {
            ajaxURL: ACTION_PATH.DETAIL_LABOUR_LOAD,
            ajaxRequesting: (url, params) => params.entityId = entityId,
            headerSort: false,
            height: '100%',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: '№ по ТП', field: TABR_FIELD.NUMBER },
                { title: 'Операция', field: TABR_FIELD.WORK_TYPE },
                { title: 'Трудоемкость', field: TABR_FIELD.LABOUR_VALUE },
            ]
        });

        $btnCancel.on({
            'click': () => $modal.modal('hide')
        });
    });
</script>