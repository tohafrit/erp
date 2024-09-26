<div class="list__header">
    <h1 class="list__header_title"><fmt:message key="bank.title"/></h1>
    <div class="list__header_buttons">
        <i class="icon add link blue list__btn-add" title="<fmt:message key="label.button.add"/>"></i>
    </div>
</div>
<div class="list__table-container">
    <div class="list__table table-sm table-striped"></div>
</div>

<script>
    $(() => {
        const $addBank = $('i.list__btn-add');

        const table = new Tabulator('div.list__table', {
            ajaxURL: '/api/action/prod/bank/list/load',
            headerSort: false,
            layout: 'fitColumns',
            pagination: 'local',
            height: '100%',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: '<fmt:message key="bank.name"/>', field: 'name' },
                { title: '<fmt:message key="bank.location"/>', field: 'location' },
                { title: '<fmt:message key="bank.bik"/>', field: 'bik' },
                { title: '<fmt:message key="bank.account"/>', field: 'correspondentAccount' },
                { title: '<fmt:message key="bank.address"/>', field: 'address' },
                { title: '<fmt:message key="bank.phone"/>', field: 'phone' }
            ],
            rowClick: (e, row) => {
                if (row.isSelected()) {
                    table.deselectRow();
                    sessionStorage.removeItem(ssBank_selectedId);
                } else {
                    table.deselectRow();
                    row.select();
                    sessionStorage.setItem(ssBank_selectedId, row.getData().id);
                }
            },
            rowContext: (e, row) => {
                table.deselectRow();
                row.select();
                sessionStorage.setItem(ssBank_selectedId, row.getData().id);
            },
            rowContextMenu: row => {
                let id = row.getData().id;
                return [
                    {
                        label: '<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>',
                        action: () => {
                            editBank(id);
                            sessionStorage.setItem(ssBank_selectedId, id);
                        }
                    },
                    {
                        label: '<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>',
                        action: () => deleteBank(id)
                    }
                ];
            },
            dataLoaded: () => {
                const ssBankSelectedId = sessionStorage.getItem(ssBank_selectedId);
                let row = table.searchRows("id", "=", ssBankSelectedId)[0];
                if (row !== undefined) {
                    row.pageTo();
                    row.select();
                    row.scrollTo();
                }
            }
        });

        // Добавление банка
        $addBank.on({
            'click': () => editBank()
        });

        // Функция добавления/редактирования банка
        function editBank(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/bank/list/edit',
                loadData: { id: id },
                submitURL: '/api/action/prod/bank/list/edit/save',
                onSubmitSuccess: response => {
                    if (!id) {
                        sessionStorage.setItem(ssBank_selectedId, response.attributes.addedBankId);
                    }
                    table.setData();
                }
            });
        }

        // Функция удаления банка
        function deleteBank(id) {
            confirmDialog({
                title: '<fmt:message key="bank.delete.title"/>',
                message: '<fmt:message key="bank.delete.confirm"/>',
                onAccept: () => {
                    $.ajax({
                        method: 'DELETE',
                        url: '/api/action/prod/bank/list/delete/' + id,
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => {
                        sessionStorage.removeItem(ssBank_selectedId);
                        table.setData();
                    });
                }
            });
        }
    });
</script>