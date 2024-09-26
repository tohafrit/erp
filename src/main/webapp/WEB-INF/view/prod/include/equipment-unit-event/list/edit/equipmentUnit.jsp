<div class="ui modal list_edit_equipment-unit__main">
    <div class="header">
        Выбор единицы оборудования
    </div>
    <div class="content">
        <div class="list_edit_equipment-unit__table table-sm table-striped"></div>
    </div>
    <div class="actions">
        <button class="ui small button list_edit_equipment-unit__btn-select">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.select"/>
        </button>
    </div>
</div>

<script>
    $(() => {
        const $modal = $('div.list_edit_equipment-unit__main');
        const $selectBtn = $('button.list_edit_equipment-unit__btn-select');
        const $unitTable = $('table.list_edit__unit-table');
        const $buttonAdd = $('div.list_edit__unit-table_div-add');

        const table = new Tabulator('div.list_edit_equipment-unit__table', {
            ajaxURL: '/api/action/prod/equipment-unit-event/list/edit/equipment-unit/load',
            pagination: 'remote',
            ajaxSorting: true,
            height: 'calc(100vh * 0.7)',
            layout: 'fitData',
            initialSort:[
                { column: 'name', dir: 'asc' }
            ],
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                { title: '<fmt:message key="equipmentUnitEvent.field.equipment.name"/>', field: 'name' },
                { title: '<fmt:message key="equipmentUnitEvent.field.producer.name"/>', field: 'producer' },
                { title: '<fmt:message key="equipmentUnitEvent.field.equipment.model"/>', field: 'model' },
                { title: '<fmt:message key="equipmentUnitEvent.field.productionArea.name"/>', field: 'productionArea' },
                { title: '<fmt:message key="equipmentUnitEvent.field.equipmentUnit.serialNumber"/>', field: 'serialNumber' },
                { title: '<fmt:message key="equipmentUnitEvent.field.equipmentUnit.inventoryNumber"/>', field: 'inventoryNumber' }
            ],
            rowClick: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowDblClick: (e, row) => {
                table.deselectRow();
                row.select();
                $selectBtn.trigger('click');
            }
        });

        $selectBtn.on({
            'click' : () => {
                const rows = table.getSelectedRows();
                if (rows.length === 1) {
                    const rowData = rows[0].getData();
                    $('input.list_edit__unit-table_input').val(rowData.id);
                    const data = [
                        rowData.name,
                        rowData.producer,
                        rowData.model,
                        rowData.productionArea,
                        rowData.serialNumber,
                        rowData.inventoryNumber,
                    ];
                    $.each(data, (index, value) => {
                        $unitTable.find('tr').eq(index + 1).find('td').html(value);
                    });
                    $unitTable.show();
                    $buttonAdd.hide();
                    $modal.modal('hide');
                }
            }
        });
    });
</script>