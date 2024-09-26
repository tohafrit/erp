<div class="list_occurrence__header">
    <h1 class="list_occurrence__header_title">Входимость</h1>
    <div class="list_occurrence__table_buttons">
        <i class="icon filter link list_occurrence__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        <i class="icon file excel outline link blue list_occurrence__btn-excel" title="Выгрузка в excel"></i>
    </div>
</div>
<div class="list_occurrence__table-wrap">
    <jsp:include page="/api/view/prod/component/list/occurrence/filter"/>
    <div class="list_occurrence__table table-sm table-striped"></div>
</div>

<script>
    $(() => {
        const componentId = '${componentId}';
        const $filter = $('form.list_occurrence_filter__form');
        const $btnFilter = $('i.list_occurrence__btn-filter');
        const $btnExcel = $('i.list_occurrence__btn-excel');
        const $btnSearch = $('div.list_occurrence_filter__btn-search');

        // Кнопка фильтра
        $btnFilter.on({
            'click': function() {
                $(this).toggleClass('blue');
                $filter.toggle(!$filter.is(':visible'));
            }
        });

        const datatable = new Tabulator('div.list_occurrence__table', {
            selectable: 1,
            ajaxSorting: true,
            height: '100%',
            ajaxURL: '/api/action/prod/component/list/occurrence/load',
            ajaxRequesting: (url, params) => {
                params.componentId = componentId;
                params.filterForm = formToJson($filter);
            },
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: 'Ведущий', field: 'lead' },
                { title: 'Условное наименование изделия', field: 'conditionalName' },
                { title: 'Версия', field: 'version' },
                { title: 'ИД', field: 'identifier' },
                {
                    title: 'КД',
                    field: 'kd',
                    headerSort: false,
                    hozAlign: 'center',
                    vertAlign: 'middle',
                    formatter: cell => booleanToLight(cell.getValue())
                },
                {
                    title: 'Закупка',
                    field: 'purchase',
                    resizable: false,
                    headerSort: false,
                    hozAlign: 'center',
                    vertAlign: 'middle',
                    formatter: cell => booleanToLight(cell.getValue())
                },
                {
                    title: 'Утверждена',
                    field: 'approvedText',
                    variableHeight: true,
                    width: 300,
                    minWidth: 300,
                    formatter: 'textarea'
                },
                {
                    title: 'Принята',
                    field: 'acceptedText',
                    variableHeight: true,
                    width: 300,
                    minWidth: 300,
                    formatter: 'textarea'
                }
            ],
            rowDblClick: (e, row) => {
                datatable.deselectRow();
                row.select();
            },
            rowContextMenu: row => {
                const menu = [];
                const data = row.getData();
                menu.push({
                    label: `<i class="file alternate outline icon blue"></i>Открыть закупочную спецификацию`,
                    action: () => window.open(window.location.origin + '/prod/product/detail/' + data.productId + '/specification?selectedBomId=' + data.bomId)
                });
                return menu;
            },
        });

        // Кнопка поиска
        $btnSearch.on({
            'click': () => datatable.setData()
        });
        $filter.enter(() => $btnSearch.trigger('click'));

        // Кнопка выгрузки в excel
        $btnExcel.on({
            'click': () => datatable.download('xlsx', 'occurrence.xlsx', { sheetName: 'Вхождения' })
        });
    });
</script>