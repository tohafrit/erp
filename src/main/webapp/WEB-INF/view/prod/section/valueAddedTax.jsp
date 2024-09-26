<link rel="stylesheet" type="text/css" href="<c:url value="/resources/style/prod/view/value-added-tax.css"/>">

<div class="root__header">
    <h1 class="root__header_title"><fmt:message key="valueAddedTax.title"/></h1>
    <div class="root__header_buttons">
        <i class="icon add link blue root__btn-add" title="<fmt:message key="label.button.add"/>"></i>
    </div>
</div>
<div class="root__table table-sm table-striped"></div>

<script>
    $(() => {
        const $addValueAddedTax = $('i.root__btn-add');
        const table = new Tabulator('div.root__table', {
            ajaxURL: '/api/action/prod/value-added-tax/load',
            headerSort: false,
            height: '100%',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: '<fmt:message key="valueAddedTax.periodName"/>', field: 'periodName' },
                {
                    title: '<fmt:message key="valueAddedTax.dateFrom"/>',
                    field: 'dateFrom',
                    hozAlign: 'center',
                    formatter: 'stdDate'
                },
                {
                    title: '<fmt:message key="valueAddedTax.dateTo"/>',
                    field: 'dateTo',
                    hozAlign: 'center',
                    formatter: 'stdDate'
                },
                { title: '<fmt:message key="valueAddedTax.amount"/>', field: 'amount' },
                {
                    title: '<fmt:message key="valueAddedTax.file"/>',
                    field: 'fileHref',
                    formatter: 'fileLink'
                }
            ],
            rowContextMenu: row => {
                if (row.getData().last) {
                    return [
                        {
                            label: '<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>',
                            action: (e, row) => editValueAddedTax(row.getData().id)
                        },
                        {
                            label: '<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>',
                            action: (e, row) => deleteValueAddedTax(row.getData().id)
                        }
                    ];
                }
            },
            rowClick: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowContext: (e, row) => {
                table.deselectRow();
                row.select();
            }
        });

        // Добавление НДС
        $addValueAddedTax.on({
            'click': () => editValueAddedTax()
        });

        // Функция добавления/редактирования НДС
        function editValueAddedTax(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/value-added-tax/edit',
                loadData: { id: id },
                submitURL: '/api/action/prod/value-added-tax/edit/save',
                onSubmitSuccess: () => table.setData()
            });
        }

        // Функция удаления НДС
        function deleteValueAddedTax(id) {
            confirmDialog({
                title: '<fmt:message key="valueAddedTax.delete.title"/>',
                message: '<fmt:message key="valueAddedTax.delete.confirm"/>',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: '/api/action/prod/value-added-tax/delete/' + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setData())
            });
        }
    });
</script>