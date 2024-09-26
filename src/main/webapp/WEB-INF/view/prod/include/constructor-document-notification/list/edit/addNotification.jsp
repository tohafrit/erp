<div class="ui modal list_edit_add-notification__modal">
    <div class="header">Добавление извещения по КД</div>
    <div class="content">
        <div class="list_edit_notification__buttons">
            <i class="icon filter link blue list_edit_notification__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        </div>
        <div class="list_edit_add-notification__table table-sm table-striped"></div>
    </div>
    <div class="actions">
        <button class="ui small button list_edit_add-notification__btn-apply disabled">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.apply"/>
        </button>
    </div>
</div>

<script>
    $(() => {
        const $modal = $('div.list_edit_add-notification__modal');
        const $btnApply = $('button.list_edit_add-notification__btn-apply');
        const notificationTable = Tabulator.prototype.findTable('div.list_edit__notification-table')[0];
        const $btnFilter = $('i.list_edit_notification__btn-filter');
        const notificationIdList = '${notificationIdList}';
        let filterData = {};

        const table = new Tabulator('div.list_edit_add-notification__table', {
            selectable: 1,
            pagination: 'remote',
            ajaxURL: ACTION_PATH.LIST_EDIT_ADD_NOTIFICATION_LOAD,
            ajaxSorting: true,
            ajaxRequesting: (url, params) => {
                filterData.notificationIdList = notificationIdList;
                filterData.includeChild = true;
                params.filterData = JSON.stringify(filterData);
            },
            height: 'calc(100vh * 0.5)',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                TABR_COL_ID,
                { title: 'Номер', field: TABR_FIELD.DOC_NUMBER },
                { title: 'Изделие применяемости', field: TABR_FIELD.PRODUCT }
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
                            docNumber: el.docNumber,
                            product: el.product
                        }
                    });
                    notificationTable.addData(arr);
                    notificationTable.clearSort();
                    $modal.modal('hide');
                }
            }
        });

        // Фильтр
        $.modalFilter({
            url: VIEW_PATH.LIST_EDIT_ADD_NOTIFICATION_FILTER,
            button: $btnFilter,
            filterData: () => filterData,
            onApply: data => {
                filterData = data;
                table.setData();
            }
        });
    })
</script>