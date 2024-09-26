<div class="ui modal decipherment_edit_form18_review-justification__modal">
    <div class="header">Обоснование цен на СП</div>
    <div class="content">
        <div class="decipherment_edit_form18_review-justification__buttons">
            <i class="icon filter link blue decipherment_edit_form18_review-justification__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        </div>
        <div class="decipherment_edit_form18_review-justification__table table-sm table-striped"></div>
    </div>
    <div class="actions">
        <div class="ui small button decipherment_edit_form18_review-justification__btn-select disabled">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.select"/>
        </div>
    </div>
</div>

<script>
    $(() => {
        const deciphermentId = '${deciphermentId}';
        const $modal = $('div.decipherment_edit_form18_review-justification__modal');
        const $btnSelect = $('div.decipherment_edit_form18_review-justification__btn-select');
        const $btnFilter = $('i.decipherment_edit_form18_review-justification__btn-filter');
        const $justificationId = $('input.decipherment_edit_form18__review-justification-id');
        const $justificationName = $('span.decipherment_edit_form18__review-justification-name');
        let filterData = {};

        const table = new Tabulator('div.decipherment_edit_form18_review-justification__table', {
            selectable: 1,
            pagination: 'remote',
            initialSort: [{ column: TABR_FIELD.NAME, dir: SORT_DIR_DESC }],
            ajaxURL: ACTION_PATH.DETAIL_DECIPHERMENT_EDIT_FORM18_REVIEW_JUSTIFICATION_LOAD,
            ajaxRequesting: (url, params) => {
                params.deciphermentId = deciphermentId;
                params.filterData = JSON.stringify(filterData);
            },
            ajaxSorting: true,
            height: 'calc(100vh * 0.6)',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                TABR_COL_ID,
                { title: 'Документ', field: TABR_FIELD.NAME, hozAlign: 'center' },
                {
                    title: 'Дата создания',
                    field: TABR_FIELD.CREATE_DATE,
                    hozAlign: 'center',
                    width: 130,
                    resizable: false,
                    formatter: 'stdDate'
                },
                {
                    title: 'Цена',
                    headerSort: false,
                    field: TABR_FIELD.PRICE,
                    formatter: 'money',
                    formatterParams: { decimal: ',', thousand: ' ' }
                },
                {
                    title: 'Комментарий',
                    field: TABR_FIELD.COMMENT,
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

        // Выбор обоснования
        $btnSelect.on({
            'click': () => {
                const data = table.getSelectedData();
                if (data.length === 1) {
                    $justificationId.val(data[0].id);
                    $justificationName.text(formatAsCurrency(data[0].price) + " в " + data[0].name);
                    $justificationId.trigger('change');
                    $modal.modal('hide');
                }
            }
        });

        // Фильтр
        $.modalFilter({
            url: VIEW_PATH.DETAIL_DECIPHERMENT_EDIT_FORM18_REVIEW_JUSTIFICATION_FILTER,
            button: $btnFilter,
            filterData: () => filterData,
            onApply: data => {
                filterData = data;
                table.setData();
            }
        });
    });
</script>