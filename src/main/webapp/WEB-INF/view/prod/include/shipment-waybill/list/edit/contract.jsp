<div class="ui modal list_edit_contract__modal">
    <div class="ui small header">Выбор договора</div>
    <div class="content">
        <div class="list_edit_contract__buttons">
            <i class="icon filter link blue list_edit_contract__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        </div>
        <div class="list_edit_contract__table table-sm table-striped"></div>
    </div>
    <div class="actions">
        <div class="ui small button list_edit_contract__btn-select disabled">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.select"/>
        </div>
    </div>
</div>

<script>
    $(() => {
        const $modal = $('div.list_edit_contract__modal');
        const $btnSelect = $('div.list_edit_contract__btn-select');
        const $btnFilter = $('i.list_edit_contract__btn-filter');
        const $sectionId = $('input.list_edit__section-id');
        const $sectionName = $('span.list_edit__section-name');
        const $payerNameDiv = $('div.list_edit__payer-name');
        const $consigneeSelect = $('div.list_edit__consignee-select');
        let filterData = {};

        const table = new Tabulator('div.list_edit_contract__table', {
            selectable: 1,
            pagination: 'remote',
            initialSort: [{ column: TABR_FIELD.CREATE_DATE, dir: SORT_DIR_DESC }],
            ajaxURL: ACTION_PATH.LIST_EDIT_CONTRACT_LOAD,
            ajaxRequesting: (url, params) => {
                params.filterData = JSON.stringify(filterData);
            },
            ajaxSorting: true,
            height: 'calc(100vh * 0.6)',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                TABR_COL_ID,
                { title: 'Номер', field: TABR_FIELD.NUMBER },
                {
                    title: 'Дата создания',
                    field: TABR_FIELD.CREATE_DATE,
                    hozAlign: 'center',
                    width: 130,
                    resizable: false,
                    formatter: 'stdDate'
                },
                { title: 'Внешний номер', field: TABR_FIELD.EXTERNAL_NUMBER },
                {
                    title: 'Готов к отгрузке',
                    field: TABR_FIELD.READY,
                    resizable: false,
                    hozAlign: 'center',
                    formatter: 'lightMark',
                    headerSort: false
                },
                { title: 'Заказчик', field: TABR_FIELD.CUSTOMER, headerSort: false }
            ],
            rowSelected: () => $btnSelect.toggleClass('disabled', !table.getSelectedRows().length),
            rowDeselected: () => $btnSelect.toggleClass('disabled', !table.getSelectedRows().length),
            rowDblClick: (e, row) => {
                table.deselectRow();
                row.select();
                $btnSelect.trigger('click');
            }
        });

        // Кнопка выбора
        $btnSelect.on({
            'click': () => {
                const data = table.getSelectedData();
                if (data.length === 1) {
                    const row = data[0];
                    $sectionId.val(row.id);
                    $sectionName.text(row.fullNumber);
                    $payerNameDiv.find('span').text(row.customer);
                    $payerNameDiv.find('span').data('id', row.customerId);
                    $consigneeSelect.dropdown('set selected', row.customerId);
                    $sectionId.trigger('change');
                    $modal.modal('hide');
                }
            }
        });

        // Фильтр
        $.modalFilter({
            url: VIEW_PATH.LIST_EDIT_CONTRACT_FILTER,
            button: $btnFilter,
            filterData: () => filterData,
            onApply: data => {
                filterData = data;
                table.setData();
            }
        });
    });
</script>