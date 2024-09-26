<div class="ui modal">
    <div class="header">
        Сотрудники участка
    </div>
    <div class="content">
        <div class="list_info_employee__table table-sm table-striped"></div>
    </div>
</div>

<script>
    $(() => {
        const table = new Tabulator('div.list_info_employee__table', {
            ajaxURL: '/api/action/prod/production-area/list/info/employee/load',
            ajaxRequesting: (url, params) => {
                params.areaId = '${productionArea.id}'
            },
            layout: 'fitDataStretch',
            pagination: 'local',
            height: 'calc(100vh * 0.7)',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: 'Табельный номер', field: 'personnelNumber' },
                { title: 'ФИО', field: 'name' }
            ]
        });
    });
</script>