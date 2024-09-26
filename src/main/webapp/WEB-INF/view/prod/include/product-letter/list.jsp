<div class="list__header">
    <h1 class="list__header_title">Литеры</h1>
    <div class="list__header_buttons">
        <i class="icon add link blue list__btn-add" title="<fmt:message key="label.button.add"/>"></i>
    </div>
</div>
<div class="list__table-block">
    <div class="list__table table-sm table-striped"></div>
</div>

<script>
    $(() => {
        const $btnAdd = $('i.list__btn-add');

        // Таблица с литерами
        const datatable = new Tabulator('div.list__table', {
            selectable: 1,
            ajaxURL: ACTION_PATH.LIST_LOAD,
            layout: 'fitColumns',
            resizableColumns: false,
            height: '100%',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: 'Наименование', field: TABR_FIELD.NAME },
                {
                    title: 'Описание',
                    field: TABR_FIELD.DESCRIPTION,
                    variableHeight: true,
                    minWidth: 400,
                    formatter: 'textarea'
                }
            ],
            rowContext: (e, row) => {
                datatable.deselectRow();
                row.select();
            },
            rowContextMenu: component => {
                const menu = [];
                const data = component.getData();
                menu.push({
                    label: `<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>`,
                    action: () => editProductLetter(data.id)
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => deleteProductLetter(data.id)
                });
                return menu;
            }
        });

        // Функция добавления/редактирования
        function editProductLetter(id) {
            $.modalWindow({
                loadURL: VIEW_PATH.LIST_EDIT,
                loadData: { id: id },
                submitAsJson: true,
                submitURL: ACTION_PATH.LIST_EDIT_SAVE,
                onSubmitSuccess: () => datatable.setData()
            });
        }

        // Функция удаления
        function deleteProductLetter(id) {
            confirmDialog({
                title: 'Удаление литеры',
                message: 'Вы уверены, что хотите удалить литеру?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: ACTION_PATH.LIST_DELETE + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => datatable.setData())
            });
        }

        // Кнопка добавления
        $btnAdd.on({
            'click': () => editProductLetter()
        });
    });
</script>