<div class="ui modal list_edit_serial-number__main">
    <div class="header">Выбор доступных серийных номеров для изделия ${conditionalName}</div>
    <div class="content">
        <div class="list_edit_serial-number-table table-sm table-striped"></div>
    </div>
    <div class="actions">
        <button class="ui small disabled button list_edit_serial-number__btn-select">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.select"/>
        </button>
    </div>
</div>

<script>
    $(() => {
        const logRecordId = '${logRecordId}';
        const $modal = $('div.list_edit_serial-number__main');
        const $selectBtn = $('button.list_edit_serial-number__btn-select');
        const $editModal = $('div.list_edit__main');
        const $allotmentIdInput = $editModal.find('input[name="allotmentId"]');
        const listEditTable = Tabulator.prototype.findTable('div.list_edit__table')[0];
        const $idInput = $editModal.find('input[name="id"]');
        const $maxSerialNumberQuantityInput = $editModal.find('input[name="maxSerialNumberQuantity"]');
        //

        // Таблица доступных серийных номеров
        const table = new Tabulator('div.list_edit_serial-number-table', {
            ajaxURL: ACTION_PATH.LIST_EDIT_SERIAL_NUMBER_LOAD,
            ajaxRequesting: (url, params) => {
                params.id = $allotmentIdInput.val();
                params.logRecordId = logRecordId;
            },
            ajaxSorting: false,
            maxHeight: '100%',
            layout: 'fitDataStretch',
            selectable: true,
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: 'Серийный номер', field: TABR_FIELD.SERIAL_NUMBER }
            ],
            rowClick: () => $selectBtn.toggleClass('disabled', !table.getSelectedRows().length)
        });

        // Выбор серийных номеров
        $selectBtn.on({
            'click': () => {
                const data = table.getSelectedData();
                if (data.length) {
                    const arr = [];
                    data.forEach(el => arr.push({
                        groupMain: 'Письмо № ' +  el.letterFullNumber + ", " + "Пункт: " +
                            el.orderIndex + ", " + "Договор: " + el.contractNumber,
                        groupSubMain: 'Изделие: ' + el.productName + ', ' + 'Тип приемки: ' + el.acceptType,
                        serialNumber: el.serialNumber
                    }));

                    let rows = listEditTable.getRows();
                    rows.forEach(row => {
                        const rowData = row.getData();
                        if (rowData.serialNumber === '') row.delete();
                    });
                    if ($maxSerialNumberQuantityInput.val() >= arr.length + listEditTable.getDataCount()) {
                        arr.forEach(el => listEditTable.addRow(el));
                    } else {
                        alertDialog({ title: '', message: 'Достигнуто максимально возможное количество изделий для добавления серийных номеров' });
                    }
                    $modal.modal('hide');
                }
            }
        });
    });
</script>