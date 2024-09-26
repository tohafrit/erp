<div class="detail_occurrence__buttons">
    <i class="icon filter link detail_occurrence__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
</div>
<div class="detail_occurrence__table-block">
    <jsp:include page="/api/view/prod/product/detail/occurrence/filter"/>
    <div class="detail_occurrence__table-wrap">
        <div class="detail_occurrence__table table-sm table-striped"></div>
    </div>
</div>

<script>
    $(() => {
        const productId = '${productId}';
        const $filterForm = $('form.detail_occurrence_filter__form');
        const $filter = $('div.detail_occurrence_filter__main');
        const $btnSearch = $('div.detail_occurrence_filter__btn-search');

        // Кнопка фильтра
        $('i.detail_occurrence__btn-filter').on({
            'click': function() {
                $(this).toggleClass('blue');
                $filter.toggle(!$filter.is(':visible'));
            }
        });

        const datatable = new Tabulator('div.detail_occurrence__table', {
            selectable: 1,
            pagination: 'remote',
            ajaxURL: '/api/action/prod/product/detail/occurrence/load',
            ajaxRequesting: (url, params) => {
                params.productId = productId;
                params.filterForm = formToJson($filterForm);
            },
            ajaxSorting: true,
            columns: [
                {
                    title: '#',
                    resizable: false,
                    headerSort: false,
                    frozen: true,
                    width: 50,
                    formatter: cell => {
                        $(cell.getElement()).addClass('row-number-cell');
                        return (datatable.getPageSize() * (datatable.getPage() - 1)) + cell.getRow().getPosition() + 1;
                    }
                },
                { title: '<fmt:message key="product.detail.occurrence.table.field.lead"/>', field: 'lead' },
                { title: '<fmt:message key="product.detail.occurrence.table.field.conditionalName"/>', field: 'conditionalName' },
                { title: '<fmt:message key="product.detail.occurrence.table.field.version"/>', field: 'version' },
                { title: '<fmt:message key="product.detail.occurrence.table.field.identifier"/>', field: 'identifier' },
                { title: '<fmt:message key="product.detail.occurrence.table.field.approved"/>', field: 'approvedText' },
                { title: '<fmt:message key="product.detail.occurrence.table.field.accepted"/>', field: 'acceptedText' }
            ],
            rowContext: (e, row) => {
                datatable.deselectRow();
                row.select();
            },
            rowContextMenu: row => {
                const menu = [];
                const data = row.getData();
                menu.push({
                    label: `<i class="file alternate outline icon blue"></i>Открыть закупочную спецификацию`,
                    action: () => window.open(window.location.origin + '/prod/product/detail/' + data.productId + '/specification?selectedBomId=' + data.id)
                });
                return menu;
            }
        });

        // Поиск по фильтру
        $btnSearch.on({
            'click': () => datatable.setData()
        });

        $filter.enter(() => $btnSearch.trigger('click'));
    });
</script>