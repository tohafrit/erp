<div class="list__header">
    <h1 class="list__header_title"><fmt:message key="equipmentUnitEvent.title"/></h1>
    <div class="list__header_buttons">
        <i class="icon add link blue list__btn-add" title="<fmt:message key="label.button.add"/>"></i>
    </div>
</div>
<div class="list__table-block">
    <div class="list__table-main-block">
        <div class="list__table-wrap">
            <div class="list__table table-sm table-striped"></div>
        </div>
    </div>
    <div class="list__table-sub-block">
        <i class="close link blue icon list__btn-close-sub-block"></i>
        <div class="list__sub-block-content"></div>
    </div>
</div>

<script>
    $(() => {
        const $addBtn = $('i.list__btn-add');
        const $subBlock = $('div.list__table-sub-block');
        const $subContent = $('div.list__sub-block-content');
        const $btnCloseSubBlock = $('i.list__btn-close-sub-block');
        const $content = $('div.root__content');

        let initialPage = '${initialPage}';
        const selectedEventId = '${selectedEventId}'; // Для загрузки определенного события в таблице

        let isSessionLoad = true;
        let initLoadCount = 0;
        const table = new Tabulator('div.list__table', {
            ajaxURL: '/api/action/prod/equipment-unit-event/list/load',
            ajaxRequesting: (url, params) => {
                if (initialPage && (initLoadCount === 0 || initLoadCount === 1)) {
                    params.initLoad = initLoadCount++ === 0;
                }
                $subBlock.hide();
            },
            layout: 'fitColumns',
            height: '100%',
            pagination: 'remote',
            ajaxSorting: true,
            initialSort:[
                { column: 'eventOn', dir: 'desc' }
            ],
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                { title: '<fmt:message key="equipmentUnitEvent.field.name"/>', field: 'name' },
                { title: '<fmt:message key="equipmentUnitEvent.field.eventType.name"/>', field: 'eventType' },
                { title: '<fmt:message key="equipmentUnitEvent.field.equipment.name"/>', field: 'equipmentName' },
                { title: '<fmt:message key="equipmentUnitEvent.field.equipmentUnit.serialNumber"/>', field: 'serialNumber' },
                { title: '<fmt:message key="equipmentUnitEvent.field.equipmentUnit.inventoryNumber"/>', field: 'inventoryNumber' },
                {
                    title: '<fmt:message key="equipmentUnitEvent.field.eventOn"/>',
                    field: 'eventOn',
                    hozAlign: 'center',
                    formatter: 'stdDate'
                },
                { title: '<fmt:message key="equipmentUnitEvent.field.commentary"/>', field: 'commentary' }
            ],
            dataLoaded: () => {
                if (initialPage) {
                    if (initLoadCount === 1) {
                        const maxPage = table.getPageMax();
                        table.setPage(initialPage > maxPage ? maxPage : initialPage);
                    } else if (initLoadCount === 2) {
                        initialPage = '';
                        isSessionLoad = false;
                        table.deselectRow();
                        table.selectRow(selectedEventId);
                        const rows = table.getSelectedRows();
                        if (rows.length > 1) {
                            table.deselectRow();
                        }
                        if (rows && rows.length) {
                            rows[0].scrollTo();
                        }
                    }
                }
            },
            ajaxError: () => initialPage = '',
            rowClick: (e, row) => {
                const data = row.getData();
                table.deselectRow();
                row.select();
                showDetails(data.id, data.equipmentId);
            },
            rowContext: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowContextMenu: () => {
                return [
                    {
                        label: '<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>',
                        action: (e, row) => editEvent(row.getData().id)
                    },
                    {
                        label: '<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>',
                        action: (e, row) => deleteEvent(row.getData().id)
                    }
                ];
            }
        });

        // Скрытие области вспомогательного контейнера
        $btnCloseSubBlock.on({
            'click': () => {
                table.deselectRow();
                $subBlock.hide();
            }
        });

        // Добавление события
        $addBtn.on({
            'click': () => editEvent()
        });

        // Функция раскрытия окна подробностей
        function showDetails(id, eqId) {
            $subContent.html('');
            $subBlock.show();
            setTimeout(() => table.scrollToRow(id, 'middle', false), 200);
            $.get({
                url: '/api/view/prod/equipment-unit-event/list/info',
                data: { id: eqId }
            }).done(html => $subContent.html(html));
        }

        // Функция добавления/редактирования события
        function editEvent(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/equipment-unit-event/list/edit',
                loadData: { id: id },
                submitURL: '/api/action/prod/equipment-unit-event/list/edit/save',
                onSubmitSuccess: response => {
                    if (id) {
                        table.setPage(table.getPage());
                    } else {
                        $.get({
                            url: '/api/view/prod/equipment-unit-event/list',
                            data: {
                                selectedEventId: response.attributes.addedEventId
                            }
                        }).done(html => $content.html(html));
                    }
                }
            });
        }

        // Функция удаления события
        function deleteEvent(id) {
            confirmDialog({
                title: '<fmt:message key="equipmentUnitEvent.delete.title"/>',
                message: '<fmt:message key="equipmentUnitEvent.delete.confirm"/>',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: '/api/action/prod/equipment-unit-event/list/delete/' + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setData())
            });
        }
    });
</script>