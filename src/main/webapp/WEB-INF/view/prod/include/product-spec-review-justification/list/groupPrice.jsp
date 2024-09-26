<div class="list_group-price__header">
    <h1 class="list_group-price__header_title">Классификационные группы</h1>
</div>
<div class="list_group-price__table-wrap">
    <div class="list_group-price__table table-sm table-striped"></div>
</div>

<script>
    $(() => {
        new Tabulator('div.list_group-price__table', {
            data: JSON.parse('${std:escapeJS(groupPriceList)}'),
            height: '100%',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                TABR_COL_ID,
                { title: 'Номер', field: TABR_FIELD.NUMBER },
                { title: 'Характеристика', field: TABR_FIELD.NAME },
                { title: 'Цена', field: TABR_FIELD.PRICE }
            ]
        });
    });
</script>