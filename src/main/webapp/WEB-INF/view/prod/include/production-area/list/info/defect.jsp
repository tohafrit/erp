<div class="ui modal">
    <div class="header">
        Производственные дефекты
    </div>
    <div class="content">
        <div class="list_info_defect__table table-sm table-striped"></div>
    </div>
</div>

<script>
    $(() => {
        const table = new Tabulator('div.list_info_defect__table', {
            ajaxURL: '/api/action/prod/production-area/list/info/defect/load',
            ajaxRequesting: (url, params) => {
                params.areaId = '${productionArea.id}'
            },
            layout: 'fitDataStretch',
            pagination: 'local',
            height: 'calc(100vh * 0.7)',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: 'Код', field: 'code' },
                { title: 'Комментарий', field: 'description' }
            ]
        });
    });
</script>