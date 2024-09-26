<div class="ui modal detail_specification_info_comparison_select__main">
    <div class="header">
        Выбрать изделия
    </div>
    <div class="scrolling content">
        <i class="icon filter link blue detail_specification_info_comparison_select__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        <form class="ui tiny form secondary segment detail_specification_info_comparison_select__filter-form">
            <div class="field">
                <div class="ui icon small button detail_specification_info_comparison_select__btn-search" title="Поиск">
                    <i class="search blue icon"></i>
                </div>
            </div>
            <div class="ui four column grid">
                <div class="column field">
                    <label>Условное наименование</label>
                    <input type="search" name="conditionalName">
                </div>
            </div>
        </form>
        <div class="detail_specification_info_comparison_select__table table-sm table-striped"></div>
    </div>
    <div class="actions">
        <div class="ui small button disabled detail_specification_info_comparison_select__btn-select">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.select"/>
        </div>
    </div>
</div>

<script>
    $(() => {
        const $modal = $('div.detail_specification_info_comparison_select__main');
        const $filter = $('form.detail_specification_info_comparison_select__filter-form');
        const $btnFilter = $('i.detail_specification_info_comparison_select__btn-filter');
        const $btnSelect = $('div.detail_specification_info_comparison_select__btn-select');
        const $btnSearch = $('div.detail_specification_info_comparison_select__btn-search');

        const $parentModal = $('div.detail_specification_info_comparison__main');
        const productLetter = '${productLetter}';

        const $compareContainer = $('div.detail_specification_info_comparison__comparison-form-container');

        // Кнопка фильтра
        $btnFilter.on({
            'click': function() {
                $(this).toggleClass('primary');
                $filter.toggle(!$filter.is(':visible'));
            }
        });

        const datatable = new Tabulator('div.detail_specification_info_comparison_select__table', {
            selectable: 1,
            ajaxSorting: true,
            pagination: 'remote',
            layout: 'fitDataStretch',
            maxHeight: '450px',
            ajaxURL: '/api/action/prod/product/detail/specification/info/comparison/select/list-load',
            ajaxRequesting: (url, params) => {
                params.filterForm = formToJson($filter);
            },
            columns: [
               TABR_COL_REMOTE_ROW_NUM,
                { title: 'Условное наименование', field: 'conditionalName' },
                {
                    title: 'Комментарий',
                    field: 'comment',
                    variableHeight: true,
                    minWidth: 200,
                    width: 300,
                    formatter: cell => {
                        $(cell.getElement()).css({'white-space': 'pre-wrap'});
                        return cell.getValue();
                    }
                }
            ],
            rowClick: () => $btnSelect.toggleClass('disabled', !datatable.getSelectedRows().length),
            rowDblClick: (e, row) => {
                datatable.deselectRow();
                row.select();
                $btnSelect.trigger('click')
            }
        });

        // Кнопка поиска
        $btnSearch.on({
            'click': () => datatable.setData()
        });

        // Выбор изделия
        $btnSelect.on({
            'click': () => {
                const data = datatable.getSelectedData();
                if (data.length === 1) {
                    if (productLetter === 'A') {
                        $parentModal.find('input[name="productAId"]').val(data[0].id);
                        $parentModal.find('td.product-a-name').text(data[0].conditionalName);
                    } else {
                        $parentModal.find('input[name="productBId"]').val(data[0].id);
                        $parentModal.find('td.product-b-name').text(data[0].conditionalName);
                    }
                    $compareContainer.trigger('update');
                    $modal.modal('hide');
                }
            }
        });

        $filter.enter(() => $btnSearch.trigger('click'));
    })
</script>