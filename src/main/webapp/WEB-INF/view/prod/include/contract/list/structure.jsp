<div class="list_structure__header">
    <h1 class="list_structure__header_title">${fullNumberWithDate}</h1>
</div>
<div class="list_structure__table-wrap">
    <div class="list_structure__table table-sm"></div>
</div>

<script>
    $(() => {
        const contractId = parseInt('${id}');
        //
        const tableData = tableDataFromUrlQuery(window.location.search);
        let filterData = tableData.filterData;

        const table = new Tabulator('div.list_structure__table', {
            ajaxURL: ACTION_PATH.LIST_STRUCTURE_LOAD,
            ajaxRequesting: (url, params) => {
                params.contractId = contractId;
                params.filterData = JSON.stringify(filterData);
            },
            selectable: 1,
            ajaxSorting: false,
            maxHeight: '100%',
            layout: 'fitDataStretch',
            columns: [
                { title: 'Структура', field: TABR_FIELD.STRUCTURE },
                {
                    title: 'Дата создания',
                    field: TABR_FIELD.CREATE_DATE,
                    hozAlign: 'center',
                    formatter: 'stdDate'
                },
                {
                    title: 'Статус',
                    field: TABR_FIELD.STATUS,
                    resizable: false,
                    headerSort: false,
                    width: 100,
                    hozAlign: 'center',
                    formatter: cell => booleanToLight(cell.getValue(), { onTrue: 'Активный', onFalse: 'Архивный' })
                },
                {
                    title: 'Дата передачи в ПЗ',
                    field: TABR_FIELD.SEND_TO_CLIENT_DATE,
                    resizable: false,
                    headerSort: false,
                    width: 160,
                    hozAlign: 'center',
                    formatter: cell => booleanToLight(
                        cell.getValue() != null,
                        { onTrue: 'Передано в ПЗ ' + dateTimeStdToString(cell.getValue()), onFalse: 'Не передано' }
                    )
                },
                { title: 'Идентификатор', field: TABR_FIELD.IDENTIFIER },
                { title: 'ОБС', field: TABR_FIELD.SEPARATE_ACCOUNT },
                { title: 'Ведущий', field: TABR_FIELD.MANAGER },
                { title: 'Комментарий', field: TABR_FIELD.COMMENT }
            ],
            rowContext: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowContextMenu: row => {
                const menu = [];
                const data = row.getData();
                const contractId = data.contractId;
                const sectionId = data.sectionId;
                const isSectionNumberZero = data.sectionNumberZero;
                menu.push({
                    label:
                        `<i class="book open icon blue link"></i>
                        <fmt:message key="label.menu.open"/>`,
                    action: () => page(ROUTE.detail('contractId=' + contractId + "&sectionId=" + sectionId))
                });
                menu.push({
                    label: '<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>',
                    action: () => deleteSection(sectionId, isSectionNumberZero)
                });
                return menu;
            }
        });

        // Удаление секции договора
        function deleteSection(sectionId, isSectionNumberZero) {
            confirmDialog({
                title: isSectionNumberZero ? 'Удаление договора' : 'Удаление доп. соглашения',
                message: isSectionNumberZero ? 'Вы действительно хотите удалить договор?' : 'ВЫ действительно хотите удалить доп. соглашение?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: ACTION_PATH.LIST_SECTION_DELETE + sectionId,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => {
                    page.redirect(ROUTE.list());
                })
            });
        }
    });
</script>