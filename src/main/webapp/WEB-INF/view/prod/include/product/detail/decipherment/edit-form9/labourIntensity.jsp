<div class="ui modal decipherment_edit_form9_labour__modal">
    <div class="header">Расчет трудоемкости</div>
    <div class="content">
        <div class="decipherment_edit_form9_labour__buttons">
            <i class="icon filter link blue decipherment_edit_form9_labour__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        </div>
        <div class="decipherment_edit_form9_labour__table table-sm table-striped"></div>
    </div>
    <div class="actions">
        <div class="ui small button decipherment_edit_form9_labour__btn-select disabled">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.select"/>
        </div>
    </div>
</div>

<script>
    $(() => {
        const id = '${id}';
        const productId = '${productId}';
        const $modal = $('div.decipherment_edit_form9_labour__modal');
        const $btnSelect = $('div.decipherment_edit_form9_labour__btn-select');
        const $btnFilter = $('i.decipherment_edit_form9_labour__btn-filter');
        const $labourIntensityId = $('input.decipherment_edit_form9__lab-intensity-id');
        const $labourIntensityName = $('span.decipherment_edit_form9__lab-intensity-name');
        let filterData = {};

        const table = new Tabulator('div.decipherment_edit_form9_labour__table', {
            selectable: 1,
            pagination: 'remote',
            initialSort: [{ column: TABR_FIELD.CREATE_DATE, dir: SORT_DIR_DESC }],
            ajaxURL: ACTION_PATH.DETAIL_DECIPHERMENT_EDIT_FORM9_LABOUR_INTENSITY_LOAD,
            ajaxRequesting: (url, params) => {
                filterData.productId = productId;
                filterData.isProductApproved = true;
                params.filterData = JSON.stringify(filterData);
            },
            ajaxSorting: true,
            height: 'calc(100vh * 0.6)',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                TABR_COL_ID,
                { title: 'Наименование', field: TABR_FIELD.NAME },
                {
                    title: 'Дата добавления',
                    field: TABR_FIELD.CREATE_DATE,
                    hozAlign: 'center',
                    formatter: 'stdDate'
                },
                {
                    title: 'Утверждено',
                    field: TABR_FIELD.APPROVED,
                    hozAlign: 'center',
                    headerSort: false,
                    width: 100,
                    resizable: false
                },
                { title: 'Добавил', field: TABR_FIELD.CREATED_BY, hozAlign: 'center', headerSort: false },
                {
                    title: 'Комментарий',
                    headerSort: false,
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

        // Выбор трудоемкости
        $btnSelect.on({
            'click': () => {
                const data = table.getSelectedData();
                if (data.length === 1) {
                    $labourIntensityId.val(data[0].id);
                    $labourIntensityName.text(data[0].name);
                    $labourIntensityId.trigger('change');
                    $modal.modal('hide');
                }
            }
        });

        // Фильтр
        $.modalFilter({
            url: VIEW_PATH.DETAIL_DECIPHERMENT_EDIT_FORM9_LABOUR_INTENSITY_FILTER,
            button: $btnFilter,
            filterData: () => filterData,
            onApply: data => {
                filterData = data;
                table.setData();
            }
        });
    });
</script>