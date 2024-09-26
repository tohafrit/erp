<div class="detail_work-type__header">
    <h1 class="detail_work-type__header_title">${title}</h1>
</div>
<div class="detail_work-type__table-wrap">
    <div class="detail_work-type__table table-sm table-striped"></div>
</div>

<script>
    $(() => {
        new Tabulator('div.detail_work-type__table', {
            data: JSON.parse('${std:escapeJS(data)}'),
            height: '100%',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                TABR_COL_ID,
                { title: 'Работа', field: TABR_FIELD.NAME },
                { title: 'Трудоемкость', field: TABR_FIELD.VALUE }
            ]
        });
    });
</script>