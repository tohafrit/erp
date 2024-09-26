<div class="ui modal decipherment_period_edit_plan-period__modal">
    <div class="header">Планируемый период</div>
    <div class="content">
        <div class="decipherment_period_edit_plan-period__buttons">
            <i class="icon filter link blue decipherment_period_edit_plan-period__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        </div>
        <div class="decipherment_period_edit_plan-period__table table-sm table-striped"></div>
    </div>
    <div class="actions">
        <div class="ui small button decipherment_period_edit_plan-period__btn-select disabled">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.select"/>
        </div>
    </div>
</div>

<script>
    $(() => {
        const productId = ${productId};
        const $modal = $('div.decipherment_period_edit_plan-period__modal');
        const $btnSelect = $('div.decipherment_period_edit_plan-period__btn-select');
        const $btnFilter = $('i.decipherment_period_edit_plan-period__btn-filter');
        const $planPeriodId = $('input.decipherment_period_edit__plan-period-id');
        const $planPeriodName = $('span.decipherment_period_edit__plan-period-name');
        let filterData = {};

        const table = new Tabulator('div.decipherment_period_edit_plan-period__table', {
            selectable: 1,
            pagination: 'remote',
            initialSort: [{ column: TABR_FIELD.START_DATE, dir: SORT_DIR_DESC }],
            ajaxURL: ACTION_PATH.DETAIL_DECIPHERMENT_PERIOD_EDIT_PLAN_PERIOD_LOAD,
            ajaxRequesting: (url, params) => {
                params.filterData = JSON.stringify(filterData);
                params.productId = productId;
            },
            ajaxSorting: true,
            height: 'calc(100vh * 0.6)',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                TABR_COL_ID,
                { title: 'Период', field: TABR_FIELD.NAME },
                {
                    title: 'Дата начала',
                    field: TABR_FIELD.START_DATE,
                    formatter: 'stdDate'
                },
                {
                    title: 'Комментарий',
                    field: TABR_FIELD.COMMENT,
                    headerSort: false,
                    variableHeight: true,
                    minWidth: 300,
                    formatter: 'textarea'
                }
            ],
            rowSelected: () => $btnSelect.toggleClass('disabled', !table.getSelectedRows().length),
            rowDeselected: () => $btnSelect.toggleClass('disabled', !table.getSelectedRows().length),
            rowDblClick: (e, row) => {
                table.deselectRow();
                row.select();
                $btnSelect.trigger('click');
            }
        });

        // Выбор периода
        $btnSelect.on({
            'click': () => {
                const data = table.getSelectedData();
                if (data.length === 1) {
                    $planPeriodId.val(data[0].id);
                    $planPeriodName.text(data[0].name + " от " + dateStdToString(data[0].startDate));
                    $planPeriodId.trigger('change');
                    $modal.modal('hide');
                }
            }
        });

        // Фильтр
        $.modalFilter({
            url: VIEW_PATH.DETAIL_DECIPHERMENT_PERIOD_EDIT_PLAN_PERIOD_FILTER,
            button: $btnFilter,
            filterData: () => filterData,
            onApply: data => {
                filterData = data;
                table.setData();
            }
        });
    });
</script>