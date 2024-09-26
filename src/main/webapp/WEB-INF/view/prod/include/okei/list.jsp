<div class="list__header">
    <h1 class="list__header_title"><fmt:message key="okei.list.title"/></h1>
    <div class="list__header_buttons">
        <i class="icon add link blue list__btn-add-okei" title="<fmt:message key="label.button.add"/>"></i>
    </div>
</div>
<div class="list__table-block">
    <div class="list__table table-sm table-striped"></div>
</div>

<script>
    $(() => {
        const $btnAdd = $('i.list__btn-add-okei');

        // Таблица с единицами измерения
        const datatable = new Tabulator('div.list__table', {
            ajaxURL: '/api/action/prod/okei/list/load',
            layout: 'fitColumns',
            dataTree: true,
            dataTreeStartExpanded: false,
            selectable: 1,
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: '<fmt:message key="okei.field.code"/>', field: 'code' },
                { title: '<fmt:message key="okei.field.coefficient"/>', field: 'coefficient' },
                { title: '<fmt:message key="okei.field.name"/>', field: 'name' },
                { title: '<fmt:message key="okei.field.symbol"/>',
                    columns: [
                        { title: '<fmt:message key="okei.field.national"/>', field: 'symbolNational' },
                        { title: '<fmt:message key="okei.field.international"/>', field: 'symbolInternational' }
                    ]
                },
                { title: '<fmt:message key="okei.field.codeLetter"/>',
                    columns: [
                        { title: '<fmt:message key="okei.field.national"/>', field: 'codeLetterNational' },
                        { title: '<fmt:message key="okei.field.international"/>', field: 'codeLetterInternational' }
                    ]
                }
            ],
            rowContext: (e, row) => {
                datatable.deselectRow();
                row.select();
            },
            rowContextMenu: row => {
                const menu = [];
                const data = row.getData();
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => deleteOkei(data.id)
                });
                return menu;
            }
        });

        // Функция добавления/редактирования единицы измерения
        function editOkei(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/okei/list/edit',
                loadData: { id: id },
                submitURL: '/api/action/prod/okei/list/edit/save',
                onSubmitSuccess: () => datatable.setData()
            });
        }

        // Функция удаления единицы измерения
        function deleteOkei(id) {
            confirmDialog({
                title: '<fmt:message key="okei.list.delete.title"/>',
                message: '<fmt:message key="okei.list.delete.confirm"/>',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: '/api/action/prod/okei/list/delete/' + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => datatable.setData())
            });
        }

        // Кнопка добавления единицы измерения
        $btnAdd.on({
            'click': () => editOkei()
        });
    });
</script>