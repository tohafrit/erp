<div class="ui modal decipherment_period_edit_report-period__modal">
    <div class="header">Отчетный период</div>
    <div class="content">
        <div class="decipherment_period_edit_report-period__table table-sm table-striped"></div>
    </div>
    <div class="actions">
        <div class="ui small button decipherment_period_edit_report-period__btn-select disabled">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.select"/>
        </div>
    </div>
</div>

<script>
    $(() => {
        const productId = ${productId};
        const planPeriodId = ${planPeriodId};
        const excludePeriodId = '${excludePeriodId}';
        const $modal = $('div.decipherment_period_edit_report-period__modal');
        const $btnSelect = $('div.decipherment_period_edit_report-period__btn-select');
        const $btnFilter = $('i.decipherment_period_edit_report-period__btn-filter');
        const $reportPeriodId = $('input.decipherment_period_edit__report-period-id');
        const $reportPeriodName = $('span.decipherment_period_edit__report-period-name');
        let filterData = {};

        const table = new Tabulator('div.decipherment_period_edit_report-period__table', {
            selectable: 1,
            pagination: 'remote',
            initialSort: [{ column: TABR_FIELD.START_DATE, dir: SORT_DIR_DESC }],
            ajaxURL: ACTION_PATH.DETAIL_DECIPHERMENT_PERIOD_EDIT_REPORT_PERIOD_LOAD,
            ajaxRequesting: (url, params) => {
                params.productId = productId;
                params.planPeriodId = planPeriodId;
                params.excludePeriodId = excludePeriodId;
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
                    $reportPeriodId.val(data[0].id);
                    $reportPeriodName.text(data[0].name + " от " + dateStdToString(data[0].startDate));
                    $reportPeriodId.trigger('change');
                    $modal.modal('hide');
                }
            }
        });
    });
</script>