<div class="list__header">
    <h1 class="list__header_title">Штатное расписание</h1>
</div>
<div class="list__table-block">
    <div class="list__table"></div>
</div>

<script>
    $(() => {
        const table = new Tabulator('div.list__table', {
            pagination: 'local',
            height: '100%',
            ajaxURL: '/api/action/corp/subdivision/list/load',
            layout: 'fitColumns',
            dataTree: true,
            dataTreeStartExpanded: true,
            selectable: 1,
            columns: [
                {
                    headerSort: false,
                    title: 'Название',
                    field: 'name'
                }
            ],
            rowDblClick: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowContext: (e, row) => {
                table.deselectRow();
                row.select();
            }
        });
    })
</script>