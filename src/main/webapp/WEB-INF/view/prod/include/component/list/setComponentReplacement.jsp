<div class="ui fullscreen modal list_set-component-replacement__main">
    <div class="ui small header">
        <c:if test="${mode eq 1}">
            Добавление заместителя
        </c:if>
        <c:if test="${mode eq 2}">
            Добавление замены к закупке
        </c:if>
        <c:if test="${mode eq 3}">
            Добавление замены по справочнику
        </c:if>
    </div>
    <div class="content">
        <form class="ui form">
            <div class="field inline">
                <label>Компонент</label>
                <span class="ui text">${componentName}</span>
            </div>
            <c:if test="${mode eq 3}">
                <div class="field">
                    <label>Дата замены</label>
                    <div class="ui calendar">
                        <div class="ui input left icon std-div-input-search">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date list_set-component-replacement__replacement-date-input" value="${replacementDate}"/>
                        </div>
                    </div>
                </div>
            </c:if>
        </form>
        <i class="icon filter blue link list_set-component-replacement__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        <div class="list_set-component-replacement__table table-sm table-striped"></div>
        <div class="list_set-component-replacement__title-table-select">Заменяемые компоненты</div>
        <i class="icon trash link blue list_set-component-replacement__btn-clear-select" title="Очистить таблицу выбора"></i>
        <div class="list_set-component-replacement__table-select table-sm table-striped"></div>
    </div>
    <div class="actions">
        <div class="ui small button disabled list_set-component-replacement__btn-select">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.select"/>
        </div>
    </div>
</div>

<script>
    $(() => {
        const componentId = '${componentId}';
        const categoryId = '${categoryId}';
        const mode = '${mode}';
        const $modal = $('div.list_set-component-replacement__main');
        const $btnFilter = $('i.list_set-component-replacement__btn-filter');
        const $btnSelect = $('div.list_set-component-replacement__btn-select');
        const $replacementDateInput = $('input.list_set-component-replacement__replacement-date-input');
        const $btnClearSelect = $('i.list_set-component-replacement__btn-clear-select');
        const listTabulator = Tabulator.prototype.findTable('div.list__table')[0];
        let filterData = { categoryIdList: [categoryId] };

        const tableSelect = new Tabulator('div.list_set-component-replacement__table-select', {
            selectable: true,
            headerSort: false,
            height: 'calc(100vh * 0.3)',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                TABR_COL_ID,
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
            dataChanged: data => $btnSelect.toggleClass('disabled', !data.length),
            rowClick: (e, row) => {
                if (!e.ctrlKey) tableSelect.deselectRow();
                row.select();
            },
            rowContext: (e, row) => {
                if (!row.isSelected()) {
                    tableSelect.deselectRow();
                    row.select();
                }
            },
            rowContextMenu: () => {
                const menu = [];
                menu.push({
                    label: '<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>',
                    action: () => tableSelect.deleteRow(tableSelect.getSelectedData().map(el => el.id))
                });
                return menu;
            }
        });

        $btnClearSelect.on({
            'click': () => {
                tableSelect.clearData();
                $btnSelect.addClass('disabled');
            }
        });

        const table = new Tabulator('div.list_set-component-replacement__table', {
            selectable: true,
            ajaxSorting: true,
            pagination: 'remote',
            height: 'calc(100vh * 0.35)',
            layout: 'fitDataStretch',
            ajaxURL: '/api/action/prod/component/list/set-component-replacement/list-load',
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
            rowClick: (e, row) => {
                if (!e.ctrlKey) table.deselectRow();
                row.select();
            },
            rowContext: (e, row) => {
                if (!row.isSelected()) {
                    table.deselectRow();
                    row.select();
                }
            },
            rowContextMenu: () => {
                const menu = [];
                menu.push({
                    label: '<i class="check icon blue"></i>Выбрать',
                    action: () => tableSelect.updateOrAddData(table.getSelectedData())
                });
                return menu;
            }
        });

        // Фильтр
        $.modalFilter({
            url: '/api/view/prod/component/list/set-component-replacement/filter',
            button: $btnFilter,
            filterData: () => filterData,
            onApply: data => {
                filterData = data;
                table.setData();
            }
        });

        // Добавление выбранного изделия
        $btnSelect.on({
            'click': () => {
                const data = tableSelect.getData();
                if (data.length) {
                    $.post({
                        url: '/api/action/prod/component/list/set-component-replacement/set-replacement',
                        data: {
                            compIdList: data.map(el => el.id).join(),
                            selectedComponentId: componentId,
                            mode: mode,
                            replacementDate: $replacementDateInput.val()
                        },
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(resp => {
                        if (mode == 3) alertDialog({ title: '', message: 'Количество произведенных замен: ' + resp.amount });
                        $modal.modal('hide');
                        listTabulator.setData();
                    });
                }
            }
        });
    });
</script>