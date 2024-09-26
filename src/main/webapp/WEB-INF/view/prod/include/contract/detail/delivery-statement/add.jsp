<div class="ui modal detail_delivery-statement_products__main">
    <div class="header">Выберите изделие</div>
    <div class="content">
        <div class="detail_delivery-statement_products__header_buttons">
            <i class="icon filter link detail_delivery-statement_products__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        </div>
        <div class="field detail_delivery-statement_products__kind">
            <label>Тип услуги</label>
            <select class="ui small dropdown std-select label detail_delivery-statement_products__type-dropdown">
                <c:forEach items="${serviceTypeList}" var="serviceType">
                    <option value="${serviceType.id}">${serviceType.value}</option>
                </c:forEach>
            </select>
        </div>
        <div class="field detail_delivery-statement_products__prefix-container">
            <label>Префикс в ведомости поставки: </label>
            <span class="detail_delivery-statement_products__prefix"></span>
        </div>
        <div class="detail_delivery-statement_products__table table-sm"></div>
    </div>
    <div class="actions">
        <div class="ui small button disabled detail_delivery-statement_products__btn-select">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.select"/>
        </div>
    </div>
</div>

<script>
    $(() => {
        const sectionId = '${sectionId}';
        //
        const $menuTree = $('ul.detail__menu_tree');
        const $modal = $('div.detail_delivery-statement_products__main');
        const $btnFilter = $('i.detail_delivery-statement_products__btn-filter');
        const $btnSelect = $('div.detail_delivery-statement_products__btn-select');
        const $typeDropdown = $('div.detail_delivery-statement_products__type-dropdown');
        const $prefix = $('span.detail_delivery-statement_products__prefix');
        const $deliveryStatement = $menuTree.find('li.detail__menu_delivery-statement[data-id=${sectionId}]');

        // Ивент для загрузки префикса при определенном типе услуг
        $typeDropdown.dropdown().on({
            'change': e => $.post({
                url:  ACTION_PATH.DETAIL_DELIVERY_STATEMENT_ADD_NEEDED_PREFIX,
                data: { typeId: $(e.currentTarget).find('option:selected').val() }
            }).done(data => $prefix.text(data))
        }).trigger('change');

        // Параметры фильтра таблицы
        const tableData = tableDataFromUrlQuery(window.location.search);
        let filterData = tableData.filterData;

        const table = new Tabulator('div.detail_delivery-statement_products__table', {
            pagination: 'remote',
            paginationInitialPage: tableData.page,
            paginationSize: tableData.size,
            initialSort: tableData.sort.length ? tableData.sort : [{ column: TABR_FIELD.PRODUCT_NAME, dir: SORT_DIR_DESC }],
            ajaxURL: ACTION_PATH.DETAIL_DELIVERY_STATEMENT_PRODUCTS_LOAD,
            ajaxRequesting: (url, params) => {
                params.filterData = JSON.stringify(filterData);
            },
            selectable: 1,
            ajaxSorting: true,
            maxHeight: '450px',
            layout: 'fitColumns',
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                { title: 'Изделие', field: TABR_FIELD.PRODUCT_NAME },
                { title: 'Технические условия', field: TABR_FIELD.DECIMAL_NUMBER },
                { title: 'Краткая техническая характеристика', field: TABR_FIELD.TYPE_NAME },
                { title: 'Протокол', field: TABR_FIELD.PROTOCOL_NUMBER, headerSort: false },
                { title: 'Комментарий', field: TABR_FIELD.COMMENT }
            ],
            rowClick: () => $btnSelect.toggleClass('disabled', !table.getSelectedRows().length),
            rowDblClick: (e, row) => {
                table.deselectRow();
                row.select();
                $btnSelect.trigger('click');
                table.deselectRow();
            }
        });

        // Фильтр
        $.modalFilter({
            url: VIEW_PATH.DETAIL_DELIVERY_STATEMENT_ADD_FILTER,
            button: $btnFilter,
            filterData: () => filterData,
            onApply: data => {
                filterData = data;
                table.setData();
            }
        });

        // Кнопка выбрать
        $btnSelect.on({
            'click': () => {
                let selectedRows = table.getSelectedRows();
                let productIdList = [];
                selectedRows.forEach(row => productIdList.push(row.getData().id));
                let productId = productIdList[0];
                $.modalWindow({
                    loadURL: VIEW_PATH.DETAIL_DELIVERY_STATEMENT_EDIT,
                    loadData: {
                        productId: productId,
                        serviceTypeId: $modal.find('option:selected').val(),
                        sectionId: sectionId
                    },
                    submitURL: ACTION_PATH.DETAIL_DELIVERY_STATEMENT_EDIT_SAVE,
                    onSubmitSuccess: response => {
                        $deliveryStatement.trigger('click');
                    }
                });
            }
        });
    });
</script>