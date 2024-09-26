<div class="list_log-record-info__header">
    <h1 class="list_log-record-info__header_title">Предъявление №${number} от ${registrationDate} для ${conditionalName}</h1>
</div>
<div class="list_log-record-info__table-wrap">
    <div class="list_log-record-info__table table-sm table-striped"></div>
</div>

<script>
    $(() => {
        const logRecordId = parseInt('${id}');
        //
        const table = new Tabulator('div.list_log-record-info__table', {
            ajaxURL: ACTION_PATH.LIST_LOG_RECORD_INFO_LOAD,
            ajaxRequesting: (url, params) => {
                params.logRecordId = logRecordId;
            },
            selectable: 1,
            ajaxSorting: false,
            maxHeight: '100%',
            layout: 'fitDataStretch',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                {
                    title: 'Серийный номер ',
                    field: TABR_FIELD.SERIAL_NUMBER
                },
                {
                    title: 'Пройден ОТК',
                    field: TABR_FIELD.TECHNICAL_CONTROL_DATE,
                    hozAlign: 'center',
                    formatter: 'stdDate'
                },
                {
                    title: 'МСН',
                    field: TABR_FIELD.INTER_WAREHOUSE_WAYBILL,
                    headerSort: false
                }
            ],
            rowContextMenu: row => {
                const menu = [];
                const data = row.getData();
                const id = data.matValueId
                menu.push({
                    label: '<i class="envelope outline icon blue"></i>Открыть МСН',
                    action: () => window.open(window.location.origin + '/prod/internal-waybill/list?selectedId=' + data.internalWaybillId)
                });
                menu.push({
                    label: '<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>',
                    action: () => deleteMatValue(id)
                });
                return menu;
            }
        });

        // Удаление
        function deleteMatValue(id) {
            confirmDialog({
                title: 'Удаление изделия из предъявления',
                message: 'Вы действительно хотите удалить изделие из предъявления?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: ACTION_PATH.LIST_LOG_RECORD_INFO_DELETE + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setData())
            });
        }
    });
</script>