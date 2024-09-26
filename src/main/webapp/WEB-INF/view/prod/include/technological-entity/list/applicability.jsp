<div class="list_applicability__header">
  <h1 class="list_applicability__header_title">Применяемость</h1>
</div>
<div class="list_applicability__table-wrap">
  <div class="list_applicability__table table-sm table-striped"></div>
</div>

<script>
  $(() => {
    new Tabulator('div.list_applicability__table', {
      data: JSON.parse('${std:escapeJS(applicabilityList)}'),
      height: '100%',
      layout: 'fitDataFill',
      columns: [
        TABR_COL_LOCAL_ROW_NUM,
        TABR_COL_ID,
        { title: 'Наименование', field: TABR_FIELD.NAME },
        {
          title: 'Децимальный номер',
          field: TABR_FIELD.DECIMAL_NUMBER
        }
      ]
    });
  });
</script>