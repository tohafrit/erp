<div class="ui modal list_edit_add-product__modal">
    <div class="header">Добавление изделий</div>
    <div class="content">
        <div class="list_edit_product__buttons">
            <i class="icon filter link blue list_edit_product__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        </div>
        <div class="list_edit_add-product__table table-sm table-striped"></div>
    </div>
    <div class="actions">
        <button class="ui small button list_edit_add-product__btn-apply disabled">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.apply"/>
        </button>
        <c:if test="${multi}">
            <button class="ui small button list_edit_add-product__btn-all">
                <i class="icon blue bars"></i>
                Выбрать все
            </button>
            <button class="ui small button list_edit_add-product__btn-clear">
                <i class="icon blue times"></i>
                Очистить выбор
            </button>
        </c:if>
    </div>
</div>

<script>
    $(() => {
        const $modal = $('div.list_edit_add-product__modal');
        const $btnApply = $('button.list_edit_add-product__btn-apply');
        const $btnClear = $('button.list_edit_add-product__btn-clear');
        const $btnAll = $('button.list_edit_add-product__btn-all');
        const productTable = Tabulator.prototype.findTable('div.list_edit__product-table')[0];
        const $btnFilter = $('i.list_edit_product__btn-filter');
        const productApplicabilityIdList = '${productApplicabilityIdList}';
        const multi = '${multi}' === 'true'
        let filterData = {};

        const table = new Tabulator('div.list_edit_add-product__table', {
            selectable: multi ? true : 1,
            pagination: 'remote',
            ajaxURL: ACTION_PATH.LIST_EDIT_ADD_PRODUCT_LOAD,
            ajaxSorting: true,
            ajaxRequesting: (url, params) => {
                filterData.productApplicabilityIdList = productApplicabilityIdList;
                params.filterData = JSON.stringify(filterData);
            },
            height: 'calc(100vh * 0.5)',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                TABR_COL_ID,
                { title: 'Наименование', field: TABR_FIELD.CONDITIONAL_NAME },
                { title: 'Децимальный номер', field: TABR_FIELD.DECIMAL_NUMBER },
                { title: 'Краткая техническая характеристика', field: TABR_FIELD.TYPE_NAME }
            ],
            rowSelected: () => $btnApply.toggleClass('disabled', !table.getSelectedRows().length),
            rowDeselected: () => $btnApply.toggleClass('disabled', !table.getSelectedRows().length)
        });

        // Применить выбор
        $btnApply.on({
            'click': () => {
                const data = table.getSelectedData();
                if (data.length) {
                    const arr = data.map(el => {
                        return {
                            id: el.id,
                            conditionalName: el.conditionalName,
                            decimalNumber: el.decimalNumber
                        }
                    });
                    productTable.addData(arr);
                    productTable.clearSort();
                    $modal.modal('hide');
                }
            }
        });

        // Очистить выбор строк
        $btnClear.on({
            'click': () => {
                table.deselectRow();
                $btnApply.toggleClass('disabled', !table.getSelectedRows().length);
            }
        });

        // Выбрать все
        $btnAll.on({
            'click': () => {
                table.selectRow();
                $btnApply.toggleClass('disabled', !table.getSelectedRows().length);
            }
        });

        // Фильтр
        $.modalFilter({
            url: VIEW_PATH.LIST_EDIT_ADD_PRODUCT_FILTER,
            button: $btnFilter,
            filterData: () => filterData,
            onApply: data => {
                filterData = data;
                table.setData();
            }
        });
    })
</script>