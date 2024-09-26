<div class="list__header">
    <h1 class="list__header_title">
        <fmt:message key="equipment.list.title">
            <fmt:param value="${equipmentType.description}"/>
        </fmt:message>
    </h1>
    <div class="list__header_buttons">
        <i class="icon filter link list__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        <i class="icon add link blue list__btn-add-equipment" title="<fmt:message key="label.button.add"/>"></i>
    </div>
</div>
<div class="list__table-block">
    <div class="list__table-main-block">
        <div class="list__table-wrap">
            <jsp:include page="/api/view/prod/equipment/list/filter"/>
            <div class="list__table equipment_type_${equipmentType.id} table-sm table-striped"></div>
        </div>
    </div>
</div>

<script>
    $(() => {
        const $content = $('div.root__content');
        const $btnFilter = $('i.list__btn-filter');
        const $btnAddEquipment = $('i.list__btn-add-equipment');
        const $btnSearch = $('div.list_filter__btn-search');
        const $filter = $('div.list_filter__main');
        const $filterForm = $('form.list_filter__form');
        const equipmentTypeId = '${equipmentType.id}';

        let initialPage = '${initialPage}';
        const selectedNoteId = '${selectedEquipmentId}'; // для выделения определенной единицы оборудования в таблице

        let table = {};
        let isSessionLoad = true;
        let initLoadCount = 0;

        if (equipmentTypeId === '3') {
            // Таблица рабочих мест
            table = new Tabulator('div.list__table.equipment_type_3', {
                pagination: 'remote',
                ajaxURL: '/api/action/prod/equipment/list/load',
                ajaxRequesting: (url, params) => {
                    if (initialPage && (initLoadCount === 0 || initLoadCount === 1)) {
                        params.initLoad = initLoadCount++ === 0;
                        params.filterForm = formToJson($filterForm);
                    } else {
                        params.filterForm = formToJson($filterForm);
                    }
                },
                ajaxSorting: true,
                height: '100%',
                columns: [
                    TABR_COL_REMOTE_ROW_NUM,
                    { title: '<fmt:message key="equipment.field.name"/>', field: 'name' },
                    { title: '<fmt:message key="equipment.field.areaName"/>', field: 'areaName' },
                    { title: '<fmt:message key="equipment.field.code"/>', field: 'code' },
                    { title: '<fmt:message key="equipment.field.user"/>', field: 'user' },
                    { title: '<fmt:message key="equipment.field.shift"/>', field: 'shift' },
                    {
                        title: '<fmt:message key="equipment.field.archive"/>',
                        field: 'archive',
                        resizable: false,
                        headerSort: false,
                        hozAlign: 'center',
                        formatter: cell => booleanToLight(cell.getValue(), { onTrue: 'Есть', onFalse: 'Нет' })
                    }
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
                            table.selectRow(selectedNoteId);
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
                    table.deselectRow();
                    row.select();
                },
                rowContext: (e, row) => {
                    table.deselectRow();
                    row.select();
                },
                rowContextMenu: component => {
                    const menu = [];
                    const data = component.getData();
                    menu.push({
                        label: `<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>`,
                        action: () => editEquipment(data.id)
                    });
                    menu.push({
                        label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                        action: () => deleteEquipment(data.id)
                    });
                    return menu;
                }
            });
        } else {
            const messageMap = new Map();
            const columnMap = new Map();

            messageMap.set('trunk', '<fmt:message key="waterType.trunk"/>');
            messageMap.set('di',    '<fmt:message key="waterType.di"/>');
            messageMap.set('look',  '<fmt:message key="text.look"/>');

            columnMap.set('name',                      '<fmt:message key="equipment.field.name"/>');
            columnMap.set('producerName',              '<fmt:message key="equipment.field.producerName"/>');
            columnMap.set('model',                     '<fmt:message key="equipment.field.model"/>');
            columnMap.set('weight',                    '<fmt:message key="equipment.field.weight"/>');
            columnMap.set('voltage',                   '<fmt:message key="equipment.field.voltage"/>');
            columnMap.set('power',                     '<fmt:message key="equipment.field.power"/>');
            columnMap.set('dimensions',                '<fmt:message key="equipment.field.dimensions"/>');
            columnMap.set('compressedAir',             '<fmt:message key="equipment.field.compressedAir"/>');
            columnMap.set('compressedAirPressure',     '<fmt:message key="equipment.field.compressedAirPressure"/>');
            columnMap.set('compressedAirConsumption',  '<fmt:message key="equipment.field.compressedAirConsumption"/>');
            columnMap.set('nitrogenPressure',          '<fmt:message key="equipment.field.nitrogen"/>');
            columnMap.set('water',                     '<fmt:message key="equipment.field.water"/>');
            columnMap.set('sewerage',                  '<fmt:message key="equipment.field.sewerage"/>');
            columnMap.set('extractor',                 '<fmt:message key="equipment.field.extractor"/>');
            columnMap.set('extractorVolume',           '<fmt:message key="equipment.field.extractorVolume"/>');
            columnMap.set('extractorDiameter',         '<fmt:message key="equipment.field.extractorDiameter"/>');
            columnMap.set('link',                      '<fmt:message key="equipment.field.link"/>');
            columnMap.set('archive',                   '<fmt:message key="equipment.field.archive"/>');

            // Дефолтные колонки
            const numeration = [TABR_COL_REMOTE_ROW_NUM];
            const defaultColumns = {
                name: { title: columnMap.get('name'), field: 'name', frozen: true },
                producerName: { title: columnMap.get('producerName'), field: 'producerName' },
                model: { title: columnMap.get('model'), field: 'model' },
                weight: { title: columnMap.get('weight'), field: 'weight' },
                voltage: { title: columnMap.get('voltage'), field: 'voltage' },
                power: { title: columnMap.get('power'), field: 'power' },
                dimensions: { title: columnMap.get('dimensions'), field: 'dimensions' },
                compressedAir: {
                    title: columnMap.get('compressedAir'),
                    hozAlign: 'center',
                    columns: [
                        { title: columnMap.get('compressedAirPressure'), field: 'compressedAirPressure' },
                        { title: columnMap.get('compressedAirConsumption'), field: 'compressedAirConsumption' }
                    ]
                },
                nitrogenPressure: { title: columnMap.get('nitrogenPressure'), field: 'nitrogenPressure' },
                water: { title: columnMap.get('water'), field: 'water', formatter: cell => columnMap.get(cell.getValue()) },
                sewerage: {
                    title: columnMap.get('sewerage'),
                    field: 'sewerage',
                    resizable: false,
                    headerSort: false,
                    hozAlign: 'center',
                    formatter: cell => booleanToLight(cell.getValue(), { onTrue: 'Есть', onFalse: 'Нет' })
                },
                extractor: {
                    title: columnMap.get('extractor'),
                    columns: [
                        { title: columnMap.get('extractorVolume'), field: 'extractorVolume' },
                        { title: columnMap.get('extractorDiameter'), field: 'extractorDiameter' }
                    ]
                },
                archive: {
                    title: columnMap.get('archive'),
                    field: 'archive',
                    resizable: false,
                    headerSort: false,
                    hozAlign: 'center',
                    formatter: cell => booleanToLight(cell.getValue(), { onTrue: 'Есть', onFalse: 'Нет' })
                },
                link: {
                    title: columnMap.get('link'),
                    field: 'link',
                    resizable: false,
                    headerSort: false,
                    hozAlign: 'center',
                    formatter: cell => cell.getValue() != null && cell.getValue().length > 0 ? '<a class="b-link" target="_blank" href="' + cell.getValue() + '">' + messageMap.get('look') + '</a>' : ''
                }
            };

            // Таблица оборудования
            table = new Tabulator('div.list__table.equipment_type_1, div.list__table.equipment_type_2', {
                pagination: 'remote',
                ajaxURL: '/api/action/prod/equipment/list/load',
                ajaxRequesting: (url, params) => {
                    if (initialPage && (initLoadCount === 0 || initLoadCount === 1)) {
                        params.initLoad = initLoadCount++ === 0;
                        params.filterForm = formToJson($filterForm);
                    } else {
                        params.filterForm = formToJson($filterForm);
                    }
                },
                ajaxSorting: true,
                height: '100%',
                columns: numeration.concat(Object.values(defaultColumns)),
                dataLoaded: () => {
                    if (initialPage) {
                        if (initLoadCount === 1) {
                            const maxPage = table.getPageMax();
                            table.setPage(initialPage > maxPage ? maxPage : initialPage);
                        } else if (initLoadCount === 2) {
                            initialPage = '';
                            isSessionLoad = false;
                            table.deselectRow();
                            table.selectRow(selectedNoteId);
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
                    table.deselectRow();
                    row.select();
                },
                rowContext: (e, row) => {
                    table.deselectRow();
                    row.select();
                },
                rowContextMenu: row => {
                    const menu = [];
                    const data = row.getData();
                    menu.push({
                        label: `<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>`,
                        action: () => editEquipment(data.id)
                    });
                    menu.push({
                        label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                        action: () => deleteEquipment(data.id)
                    });
                    return menu;
                }
            });
        }

        // Кнопка фильтра
        $btnFilter.on({
            'click': function () {
                $(this).toggleClass('blue');
                $filter.toggle(!$filter.is(':visible'));
            }
        });

        // Кнопка поиска
        $btnSearch.on({
            'click': () => $.get({
                url: '/api/view/prod/equipment/list',
                data: { filterForm: formToJson($filterForm) },
                beforeSend: () => togglePreloader(true),
                complete: () => togglePreloader(false)
            }).done(html => $content.html(html))
        });

        // Кнопка добавление оборудования
        $btnAddEquipment.on({
            'click': () => editEquipment()
        });

        // Функция добавления/редактирования оборудования
        function editEquipment(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/equipment/list/edit',
                loadData: { id: id },
                submitURL: '/api/action/prod/equipment/list/edit/save',
                onSubmitSuccess: response => {
                    if (id) {
                        table.setPage(table.getPage());
                    } else {
                        $.get({
                            url: '/api/view/prod/equipment/list',
                            data: {
                                selectedEquipmentId: response.attributes.addedEquipmentId,
                                filterForm: '{"equipmentTypeId":"' + response.attributes.addedEquipmentType + '"}'
                            }
                        }).done(html => $content.html(html));
                    }
                }
            });
        }

        // Функция удаления оборудования
        function deleteEquipment(id) {
            confirmDialog({
                title: '<fmt:message key="equipment.list.delete.title"/>',
                message: '<fmt:message key="equipment.list.delete.confirm"/>',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: '/api/action/prod/equipment/list/delete/' + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setPage(table.getPage()))
            });
        }
    });
</script>