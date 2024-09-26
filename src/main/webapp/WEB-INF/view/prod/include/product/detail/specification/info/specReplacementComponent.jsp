<div class="ui large modal detail_specification_info_spec-replacement-component__main">
    <div class="ui small header">Замена компонентом из справочника</div>
    <div class="content">
        <form class="ui form">
            <div class="field inline">
                <label>Изделие</label>
                <span class="ui text">${productInfo}</span>
            </div>
            <div class="field inline">
                <label>Компонент</label>
                <span class="ui text">${componentInfo}</span>
            </div>
        </form>
        <i class="icon filter blue link detail_specification_info_spec-replacement-component__btn-filter" title="Фильтр"></i>
        <div class="detail_specification_info_spec-replacement-component__table table-sm table-striped"></div>
    </div>
    <div class="actions">
        <div class="ui small button disabled detail_specification_info_spec-replacement-component__btn-select">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.select"/>
        </div>
    </div>
</div>

<script>
    $(() => {
        const categoryId = '${categoryId}';
        const bomId = '${bomId}';
        const bomItemId = '${bomItemId}';
        const $modal = $('div.detail_specification_info_spec-replacement-component__main');
        const $btnFilter = $('i.detail_specification_info_spec-replacement-component__btn-filter');
        const $btnSelect = $('div.detail_specification_info_spec-replacement-component__btn-select');
        const $specificationDatatable = $('div.detail_specification_info__spec-table');
        let filterData = { categoryIdList: [categoryId] };

        const table = new Tabulator('div.detail_specification_info_spec-replacement-component__table', {
            selectable: 1,
            ajaxSorting: true,
            pagination: 'remote',
            height: 'calc(100vh * 0.6)',
            ajaxURL: '/api/action/prod/product/detail/specification/info/spec-replacement-component/list-load',
            ajaxRequesting: (url, params) => {
                params.bomItemId = bomItemId;
                params.filterData = JSON.stringify(filterData);
            },
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                { title: '', field: TABR_FIELD.MARK, hozAlign: 'center', resizable: false, headerSort: false, width: 5 },
                { title: 'Позиция', field: TABR_FIELD.POSITION },
                { title: 'Наименование', field: TABR_FIELD.NAME },
                { title: 'Заместитель', field: TABR_FIELD.SUBSTITUTE_COMPONENT },
                { title: 'Производитель', field: TABR_FIELD.PRODUCER },
                { title: 'Категория', field: TABR_FIELD.CATEGORY },
                {
                    title: 'Описание',
                    field: TABR_FIELD.DESCRIPTION,
                    variableHeight: true,
                    width: 300,
                    minWidth: 300,
                    formatter: 'textarea'
                }
            ],
            rowSelected: () => $btnSelect.toggleClass('disabled', !table.getSelectedRows().length),
            rowDblClick: (e, row) => {
                table.deselectRow();
                row.select();
                $btnSelect.trigger('click');
            }
        });

        // Замена компонента в спецификации на другой компонент из справочника
        $btnSelect.on({
            'click': () => {
                const data = table.getSelectedData();
                if (data.length === 1) {
                    $.post({
                        url: '/api/action/prod/product/detail/specification/info/spec-replacement-component/save',
                        data: {
                            bomId: bomId,
                            bomItemId: bomItemId,
                            replaceId: data[0].id
                        },
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => {
                        $modal.modal('hide');
                        $specificationDatatable.trigger('reload');
                    });
                }
            }
        });

        // Фильтр
        $.modalFilter({
            url: '/api/view/prod/product/detail/specification/info/spec-replacement-component/filter',
            button: $btnFilter,
            filterData: () => filterData,
            onApply: data => {
                filterData = data;
                table.setData();
            }
        });
    })
</script>