<div class="ui modal detail_delivery-statement_distribution_edit-products-launch_main">
    <div class="header">${isAllotmentLaunch ? "Изменение запуска для изделий" : "Добавление изделий в запуск"}</div>
    <div class="content">
        <h1 class="detail_delivery-statement_distribution_edit-products-launch__header_title">
            ${conditionalName}, ${allotmentAmount} шт., поставка <javatime:format value="${deliveryDate}" pattern="dd.MM.yyyy"/>
        </h1>
        <div class="detail_delivery-statement_distribution_edit-products-launch__table table-sm"></div>
    </div>
    <div class="actions">
        <div class="ui small button disabled detail_delivery-statement_distribution_edit-products-launch__btn-add">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.add"/>
        </div>
    </div>
</div>

<script>
    $(() => {
        const allotmentId = '${allotmentId}';
        const isAllotmentLaunch = '${isAllotmentLaunch}' === "true";
        //
        const $modal = $('div.detail_delivery-statement_distribution_edit-products-launch_main');
        const $btnAdd = $('div.detail_delivery-statement_distribution_edit-products-launch__btn-add');
        const specMainTabulator = Tabulator.prototype.findTable('div.detail_delivery-statement__table')[0];
        const specSubMainTabulator = Tabulator.prototype.findTable('div.detail_delivery-statement_distribution__table')[0];

        const table = new Tabulator('div.detail_delivery-statement_distribution_edit-products-launch__table', {
            ajaxURL: ACTION_PATH.DETAIL_DELIVERY_STATEMENT_DISTRIBUTION_EDIT_PRODUCTS_LAUNCH_LOAD,
            ajaxRequesting: (url, params) => {
                params.allotmentId = allotmentId;
            },
            selectable: 1,
            ajaxSorting: true,
            maxHeight: '450px',
            layout: 'fitColumns',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: 'Запуск', field: TABR_FIELD.NUMBER_IN_YEAR, hozAlign: 'center' },
                {
                    title: 'Комментарий',
                    field: TABR_FIELD.COMMENT,
                    variableHeight: true,
                    minWidth: 300,
                    formatter: 'textarea'
                }
            ],
            rowClick: () => $btnAdd.toggleClass('disabled', !table.getSelectedRows().length),
            rowDblClick: (e, row) => {
                table.deselectRow();
                row.select();
                $btnAdd.trigger('click');
                table.deselectRow();
            }
        });

        // Кнопка выбрать
        $btnAdd.on({
            'click': () => {
                let selectedRows = table.getSelectedRows();
                let launchIdList = [];
                selectedRows.forEach(row => launchIdList.push(row.getData().id));
                let launchId = launchIdList[0];
                $.post({
                    url: ACTION_PATH.DETAIL_DELIVERY_STATEMENT_DISTRIBUTION_EDIT_PRODUCTS_LAUNCH_SAVE,
                    data: {
                        launchId: launchId,
                        allotmentId: allotmentId,
                        isAllotmentLaunch: isAllotmentLaunch
                    },
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => {
                    $modal.modal('hide');
                    specMainTabulator.setData();
                    specSubMainTabulator.setData();
                });
            }
        });
    });
</script>