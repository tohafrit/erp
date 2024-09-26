<div class="ui modal list_check-shipment__modal">
    <div class="ui small header">Проверка списка отгрузки</div>
    <div class="content">
        <form class="ui form">
            <input type="hidden" name="id" value="${id}">
            <div class="field inline">
                <label>Накладная</label>
                <span class="ui text">${number}</span>
            </div>
            <div class="list_check-shipment__buttons">
                <i class="icon print link blue list_check-shipment__btn-print" title="Печать"></i>
            </div>
            <div class="list_check-shipment__table table-sm table-striped"></div>
        </form>
    </div>
    <div class="actions">
        <div class="ui small button list_check-shipment__btn-cancel disabled">
            <i class="icon blue times"></i>
            <fmt:message key="label.button.cancel"/>
        </div>
        <div class="ui small button list_check-shipment__btn-save disabled">
            <i class="icon blue save"></i>
            <fmt:message key="label.button.save"/>
        </div>
    </div>
</div>

<script>
    $(() => {
        const waybillId = '${id}';
        const $modal = $('div.list_check-shipment__modal');
        const $btnPrint = $('i.list_check-shipment__btn-print');
        const $btnSave = $('div.list_check-shipment__btn-save');
        const $btnCancel = $('div.list_check-shipment__btn-cancel');
        const dataMap = new Map();

        const table = new Tabulator('div.list_check-shipment__table', {
            selectable: true,
            ajaxURL: ACTION_PATH.LIST_CHECK_SHIPMENT_LOAD,
            ajaxRequesting: (url, params) => {
                params.waybillId = waybillId;
            },
            printRowRange: 'all',
            headerSort: false,
            height: 'calc(100vh * 0.7)',
            layout: 'fitDataFill',
            groupBy: TABR_FIELD.PRODUCT_ID,
            groupStartOpen: true,
            groupHeader: (value, count, data) => 'Изделие: ' + data[0].productName,
            groupToggleElement: false,
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                {
                    title: 'Изделие',
                    field: TABR_FIELD.PRODUCT_NAME,
                    minWidth: 140,
                    visible: false,
                    print: true
                },
                {
                    title: 'Заводской номер',
                    field: TABR_FIELD.SERIAL_NUMBER,
                    minWidth: 140
                },
                {
                    title: 'Ячейка',
                    field: TABR_FIELD.CELL,
                    minWidth: 140
                },
                {
                    title: 'Проверено',
                    field: TABR_FIELD.CHECKED,
                    resizable: false,
                    hozAlign: 'center',
                    formatter: 'lightMark'
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
                    label: '<i class="check blue icon"></i>Проверить',
                    action: () => checkShipment(true)
                });
                menu.push({
                    label: '<i class="times blue icon"></i>Отменить проверку',
                    action: () => checkShipment(false)
                });
                return menu;
            }
        });

        // Проверка/отмена проверки
        function checkShipment(checked) {
            table.getSelectedData().forEach(el => {
                table.updateRow(el.id, { checked: checked });
                dataMap.set(el.id, checked);
            });
            if (dataMap.size) {
                $btnSave.removeClass('disabled');
                $btnCancel.removeClass('disabled');
            }
        }

        // Кнопка печати
        $btnPrint.on({
            'click': () => {
                $('.ui.dimmer.modals').removeClass('active visible');
                table.print();
            }
        });
        $(window).on({
            'afterprint': () => $('.ui.dimmer.modals').addClass('active visible')
        });

        // Сохранения изменений
        $btnSave.on({
            'click': () => dataMap.size ? $.post({
                url: ACTION_PATH.LIST_CHECK_SHIPMENT_SAVE,
                data: { data: JSON.stringify(Object.fromEntries(dataMap)) },
                beforeSend: () => togglePreloader(true),
                complete: () => togglePreloader(false)
            }).done(() => {
                $('div.list_mat-value__table').trigger('forceTimerUpdate');
                $modal.modal('hide');
            }) : null
        });

        // Отмена изменений
        $btnCancel.on({
            'click': () => confirmDialog({
                title: 'Отмена изменений',
                message: 'Вы действительно хотите отменить изменения?',
                onAccept: () => {
                    dataMap.clear();
                    $btnSave.addClass('disabled');
                    $btnCancel.addClass('disabled');
                    table.setData();
                }
            })
        });
    })
</script>