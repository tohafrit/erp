<div class="ui large modal detail_specification_info_replacement_add-component__main">
    <div class="ui small header">Добавление компонента замены</div>
    <div class="content">
        <div class="ui small form">
            <div class="field inline">
                <label>Изделие</label>
                <span class="ui text">${productInfo}</span>
            </div>
            <div class="field inline">
                <label>Компонент</label>
                <span class="ui text">${componentInfo}</span>
            </div>
            <div class="field">
                <label>Дата замены</label>
                <div class="ui calendar">
                    <div class="ui input left icon">
                        <i class="calendar icon"></i>
                        <input type="text" class="std-date detail_specification_info_replacement_add-component__replacement-date-input"/>
                    </div>
                </div>
            </div>
        </div>
        <i class="icon filter blue link detail_specification_info_replacement_add-component__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        <div class="detail_specification_info_replacement_add-component__table table-sm table-striped"></div>
    </div>
    <div class="actions">
        <div class="ui small button disabled detail_specification_info_replacement_add-component__btn-select">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.select"/>
        </div>
    </div>
</div>

<script>
    $(() => {
        const categoryId = '${categoryId}';
        const bomItemId = '${bomItemId}';
        const $modal = $('div.detail_specification_info_replacement_add-component__main');
        const $btnFilter = $('i.detail_specification_info_replacement_add-component__btn-filter');
        const $btnSelect = $('div.detail_specification_info_replacement_add-component__btn-select');
        const $replacementDatatable = $('div.detail_specification_info_replacement__table');
        const $replacementDateInput = $('input.detail_specification_info_replacement_add-component__replacement-date-input');
        let filterData = { categoryIdList: [categoryId] };

        const table = new Tabulator('div.detail_specification_info_replacement_add-component__table', {
            selectable: 1,
            ajaxSorting: true,
            pagination: 'remote',
            height: 'calc(100vh * 0.5)',
            ajaxURL: '/api/action/prod/product/detail/specification/info/replacement/add-component/list-load',
            ajaxRequesting: (url, params) => {
                params.filterData = JSON.stringify(filterData);
            },
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                { title: '', field: TABR_FIELD.MARK, hozAlign: 'center', resizable: false, headerSort: false, width: 5 },
                { title: 'Позиция', field: TABR_FIELD.POSITION },
                { title: 'Наименование', field: TABR_FIELD.NAME },
                { title: 'Заместитель', field: TABR_FIELD.SUBSTITUTE_COMPONENT },
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

        // Добавление выбранного изделия
        $btnSelect.on({
            'click': () => {
                const data = table.getSelectedData();
                if (data.length === 1) {
                    $.post({
                        url: '/api/action/prod/product/detail/specification/info/replacement/add-component/save',
                        data: {
                            bomItemId: bomItemId,
                            componentId: data[0].id,
                            replacementDate: $replacementDateInput.val()
                        },
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => {
                        $modal.modal('hide');
                        $replacementDatatable.trigger('reload');
                    });
                }
            }
        });

        // Фильтр
        $.modalFilter({
            url: '/api/view/prod/product/detail/specification/info/replacement/add-component/filter',
            button: $btnFilter,
            filterData: () => filterData,
            onApply: data => {
                filterData = data;
                table.setData();
            }
        });
    });
</script>