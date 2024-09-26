<div class="ui modal detail_specification_info_comparison__main">
    <div class="header">Сравнение спецификаций</div>
    <div class="content">
        <div class="ui small icon buttons detail_specification_info_comparison__data-buttons">
            <div class="ui button basic detail_specification_info_comparison__btn-excel" title="Выгрузить в excel">
                <i class="file excel outline icon"></i>
            </div>
        </div>
        <div class="detail_specification_info_comparison__comparison-form-container"></div>
        <div class="detail_specification_info_comparison__statistics">
            <div class="detail_specification_info_comparison__equal-count">Количество идентичных компонентов в составе: <span>0</span></div>
            <div class="detail_specification_info_comparison__diff-count">Количество отличающихся компонентов в составе: <span>0</span></div>
        </div>
        <div class="detail_specification_info_comparison__table table-sm table-striped"></div>
    </div>
</div>

<script>
    $(() => {
        const $datatable = $('div.detail_specification_info_comparison__table');
        const $compareContainer = $('div.detail_specification_info_comparison__comparison-form-container');

        const $btnExcel = $('div.detail_specification_info_comparison__btn-excel');

        //const $statistics = $('div.detail_specification_info_comparison__statistics');
        const $equalCount = $('div.detail_specification_info_comparison__equal-count span');
        const $diffCount = $('div.detail_specification_info_comparison__diff-count span');

        const table = new Tabulator('div.detail_specification_info_comparison__table', {
            tooltipsHeader: true,
            height: '100%',
            groupBy: 'category',
            groupToggleElement: false,
            groupHeader: value => value,
            selectable: 1,
            layout: 'fitColumns',
            ajaxRequesting: (url, params) => {
                params.compareForm = formToJson($compareContainer.find('form.detail_specification_info_comparison_filter__comparison-form'));
            },
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: 'Позиция', field: 'position' },
                { title: 'Наименование', field: 'name' },
                { title: 'Категория', field: 'category', visible: false },
                {
                    title: '',
                    field: 'leftQuantity',
                    headerSort: false,
                    resizable: false
                },
                {
                    title: '',
                    field: 'rightQuantity',
                    headerSort: false,
                    resizable: false
                }
            ],
            rowDblClick: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowFormatter: row => {
                const leftQuantityCell = row.getCell('leftQuantity');
                const rightQuantityCell = row.getCell('rightQuantity');
                const postfix = leftQuantityCell.getValue() === rightQuantityCell.getValue() ? 'positive' : 'negative';
                $(rightQuantityCell.getElement()).addClass('detail_specification_info_comparison__' + postfix);
            }
        });

        // Кнопка выгрузки в excel
        $btnExcel.on({
            'click': () => table.download('xlsx', 'compare.xlsx', { sheetName: 'Сравнение спецификаций' })
        });

        $compareContainer.on({
            'update': () => {
                const $compare = $compareContainer.find('form.detail_specification_info_comparison_filter__comparison-form');
                const $productAId = $compare.find('input[name="productAId"]');
                const $productBId = $compare.find('input[name="productBId"]');
                const productId = '${productId}';

                $.get({
                    url: '/api/view/prod/product/detail/specification/info/comparison/filter',
                    data: {
                        productAId: $productAId.length > 0 ? $productAId.val() : productId,
                        productBId: $productBId.length > 0 ? $productBId.val() : productId
                    },
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(html => {
                    $compareContainer.html(html);

                    const $btnCompare = $compareContainer.find('div.detail_specification_info_comparison_filter__btn-compare');

                    const $selectProduct = $compareContainer.find('div.detail_specification_info_comparison_filter__select-product');
                    const $selectVersionA = $compareContainer.find('.detail_specification_info_comparison_filter__select-version-a');
                    const $selectVersionB = $compareContainer.find('.detail_specification_info_comparison_filter__select-version-b');
                    const $productA = $compareContainer.find('.product-a-name');
                    const $productB = $compareContainer.find('.product-b-name');

                    $compareContainer.find('select').dropdown();

                    $selectProduct.on({
                        'click': e => {
                            $.modalWindow({
                                loadURL: '/api/view/prod/product/detail/specification/info/comparison/select',
                                loadData: { productLetter: $(e.currentTarget).data('letter') }
                            });
                        }
                    });

                    // Кнопка сравнения
                    $btnCompare.on({
                        'click': () => {
                            table.setData('/api/action/prod/product/detail/specification/info/comparison/compare')
                                .then(() => {
                                    $equalCount.text($datatable.find('.detail_specification_info_comparison__positive').length);
                                    const diffCount = $datatable.find('.detail_specification_info_comparison__negative').length;
                                    $diffCount.text(diffCount);
                                    //$statistics.show();
                                    if (!diffCount) alertDialog({ title: '', message: 'Различия не найдены' });
                                    table.updateColumnDefinition('leftQuantity', { title: $productA.text() + ': ' + $selectVersionA.find('option:selected').text() });
                                    table.updateColumnDefinition('rightQuantity', { title: $productB.text() + ': ' + $selectVersionB.find('option:selected').text() });
                                });
                        }
                    });
                });
            }
        }).trigger('update');
    });
</script>