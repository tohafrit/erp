<div class="list_work-cost__header">
    <h1 class="list_work-cost__header_title">Работы</h1>
</div>
<div class="list_work-cost__table-wrap">
    <div class="list_work-cost__table table-sm table-striped"></div>
</div>

<script>
    $(() => {
        new Tabulator('div.list_work-cost__table', {
            data: JSON.parse('${std:escapeJS(workCostList)}'),
            height: '100%',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                TABR_COL_ID,
                { title: 'Наименование', field: TABR_FIELD.NAME },
                { title: 'Стоимость', field: TABR_FIELD.COST }
            ]
        });
    });
</script>