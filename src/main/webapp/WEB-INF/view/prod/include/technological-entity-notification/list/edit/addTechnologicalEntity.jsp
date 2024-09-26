<div class="ui modal list_edit_add-entity__modal">
    <div class="header">Добавление КД</div>
    <div class="content">
        <div class="list_edit_entity__buttons">
            <i class="icon filter link blue list_edit_entity__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        </div>
        <div class="list_edit_add-entity__table table-sm table-striped"></div>
    </div>
    <div class="actions">
        <button class="ui small button list_edit_add-entity__btn-apply disabled">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.apply"/>
        </button>
    </div>
</div>

<script>
    $(() => {
        const $modal = $('div.list_edit_add-entity__modal');
        const $btnApply = $('button.list_edit_add-entity__btn-apply');
        const entityTable = Tabulator.prototype.findTable('div.list_edit__entity-table')[0];
        const $btnFilter = $('i.list_edit_entity__btn-filter');
        const entityIdList = '${entityIdList}';
        let filterData = {};

        const table = new Tabulator('div.list_edit_add-entity__table', {
            selectable: 1,
            pagination: 'remote',
            ajaxURL: ACTION_PATH.LIST_EDIT_ADD_TECHNOLOGICAL_ENTITY_LOAD,
            ajaxSorting: true,
            ajaxRequesting: (url, params) => {
                filterData.entityIdList = entityIdList;
                params.filterData = JSON.stringify(filterData);
            },
            height: 'calc(100vh * 0.5)',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                TABR_COL_ID,
                { title: 'Номер ТД', field: TABR_FIELD.ENTITY_NUMBER },
                { title: 'Комплект', field: TABR_FIELD.SET_NUMBER },
                { title: 'Разработчик', field: TABR_FIELD.DESIGNED_BY },
                { title: 'Дата разработки', field: TABR_FIELD.DESIGNED_ON }
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
                            entityNumber: el.entityNumber,
                            setNumber: el.setNumber,
                            designedBy: el.designedBy,
                            designedOn: el.designedOn
                        }
                    });
                    entityTable.addData(arr);
                    entityTable.clearSort();
                    $modal.modal('hide');
                }
            }
        });

        // Фильтр
        $.modalFilter({
            url: VIEW_PATH.LIST_EDIT_ADD_TECHNOLOGICAL_ENTITY_FILTER,
            button: $btnFilter,
            filterData: () => filterData,
            onApply: data => {
                filterData = data;
                table.setData();
            }
        });
    })
</script>