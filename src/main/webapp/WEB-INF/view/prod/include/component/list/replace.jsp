<div class="ui modal large list_replace__main">
    <div class="ui small header">Замена компонентом из справочника</div>
    <div class="content">
        <form class="ui form">
            <div class="field inline">
                <label>Компонент</label>
                <span class="ui text">${componentName}</span>
            </div>
        </form>
        <div class="list_replace__header_buttons">
            <i class="icon filter blue link list_replace__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
            <i class="icon file excel link blue list_replace__btn-excel" title="Выгрузить в excel"></i>
        </div>
        <div class="list_replace__table table-sm table-striped"></div>
    </div>
    <div class="actions">
        <div class="ui small button disabled list_replace__btn-select">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.select"/>
        </div>
    </div>
</div>

<script>
    $(() => {
        const componentId = '${componentId}';
        const categoryId = '${categoryId}';
        const $modal = $('div.list_replace__main');
        const $btnFilter = $('i.list_replace__btn-filter');
        const $btnExcel = $('i.list_replace__btn-excel');
        const $btnSelect = $('div.list_replace__btn-select');
        const $componentDatatable = $('div.list__table');
        let filterData = { categoryIdList: [categoryId] };

        const table = new Tabulator('div.list_replace__table', {
            selectable: 1,
            ajaxSorting: true,
            pagination: 'remote',
            height: 'calc(100vh * 0.5)',
            ajaxURL: '/api/action/prod/component/list/replace/list-load',
            ajaxRequesting: (url, params) => {
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
            rowDeselected: () => $btnSelect.toggleClass('disabled', !table.getSelectedRows().length),
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
                        url: '/api/action/prod/component/list/replace/save',
                        data: { componentId: componentId, replaceId: data[0].id },
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => {
                        $modal.modal('hide');
                        $componentDatatable.trigger('removeSelectRow', [componentId]);
                    });
                }
            }
        });

        // Выгрузка в excel
        $btnExcel.on({
            'click': () => {
                const usp = new URLSearchParams();
                const sorters = table.getSorters();
                if (sorters.length) {
                    usp.set(TABR_REQ_ATTR_FIELD, sorters[0].field);
                    usp.set(TABR_REQ_ATTR_DIR, sorters[0].dir);
                }
                usp.set(TABR_REQ_ATTR_FILTER_DATA, formToJson($filter));
                window.open('/api/action/prod/component/list/replace/download?' + usp.toString(), '_blank')
            }
        });

        // Фильтр
        $.modalFilter({
            url: '/api/view/prod/component/list/replace/filter',
            button: $btnFilter,
            filterData: () => filterData,
            onApply: data => {
                filterData = data;
                table.setData();
            }
        });
    })
</script>