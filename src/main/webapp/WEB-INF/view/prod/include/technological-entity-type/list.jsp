<div class="list__header">
    <h1 class="list__header_title">Наименования ТД</h1>
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

        // Таблица наименований ТД
        const datatable = new Tabulator('div.list__table', {
            selectable: 1,
            ajaxURL: ACTION_PATH.LIST_LOAD,
            layout: 'fitColumns',
            resizableColumns: false,
            height: '100%',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: 'Наименование', field: TABR_FIELD.NAME },
                { title: 'Краткое наименование', field: TABR_FIELD.SHORT_NAME },
                {
                    title: 'Множественная применяемость',
                    field: TABR_FIELD.MULTI,
                    resizable: false,
                    headerSort: false,
                    hozAlign: 'center',
                    formatter: cell => booleanToLight(cell.getValue(), { onTrue: 'Да', onFalse: 'Нет' })
                },
                {
                    title: 'Комментарий',
                    field: TABR_FIELD.COMMENT,
                    variableHeight: true,
                    minWidth: 400,
                    width: 500,
                    formatter: cell => {
                        $(cell.getElement()).css({'white-space': 'pre-wrap'});
                        return cell.getValue();
                    }
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
                    action: () => editTechnologicalEntityType(data.id)
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => deleteTechnologicalEntityType(data.id)
                });
                return menu;
            }
        });

        // Функция добавления/редактирования наименования ТД
        function editTechnologicalEntityType(id) {
            $.modalWindow({
                loadURL: VIEW_PATH.LIST_EDIT,
                loadData: { id: id },
                submitAsJson: true,
                submitURL: ACTION_PATH.LIST_EDIT_SAVE,
                onSubmitSuccess: () => datatable.setData()
            });
        }

        // Функция удаления
        function deleteTechnologicalEntityType(id) {
            confirmDialog({
                title: 'Удаление наименования ТД',
                message: 'Вы уверены, что хотите удалить наименование ТД?',
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
            'click': () => editTechnologicalEntityType()
        });
    });
</script>