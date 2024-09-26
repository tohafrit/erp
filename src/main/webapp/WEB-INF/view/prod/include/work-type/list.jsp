<div class="list__header">
    <h1 class="list__header_title">Типы операций</h1>
    <div class="list__header_buttons">
        <i class="icon add link blue list__btn-add-type" title="<fmt:message key="label.button.add"/>"></i>
    </div>
</div>
<div class="list__table-block">
    <div class="list__table table-sm table-striped"></div>
</div>

<script>
    $(() => {
        const $btnAdd = $('i.list__btn-add-type');

        // Таблица видов операций
        const table = new Tabulator('div.list__table', {
            selectable: 1,
            ajaxURL: '/api/action/prod/work-type/list/load',
            resizableColumns: false,
            height: '100%',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: 'Наименование', field: TABR_FIELD.NAME },
                {
                    title: 'Отдельная поставка',
                    field: TABR_FIELD.SEPARATE_DELIVERY,
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
                    formatter: 'textarea'
                }
            ],
            rowContext: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowContextMenu: component => {
                const menu = [];
                const data = component.getData();
                menu.push({
                    label: `<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>`,
                    action: () => editType(data.id)
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => deleteType(data.id)
                });
                return menu;
            }
        });

        // Функция добавления/редактирования
        function editType(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/work-type/list/edit',
                loadData: { id: id },
                submitURL: '/api/action/prod/work-type/list/edit/save',
                onSubmitSuccess: () => table.setData()
            });
        }

        // Функция удаления
        function deleteType(id) {
            confirmDialog({
                title: 'Удаление типа операции',
                message: 'Вы уверены, что хотите удалить тип операции?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: '/api/action/prod/work-type/list/delete/' + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setData())
            });
        }

        // Кнопка добавления
        $btnAdd.on({
            'click': () => editType()
        });
    });
</script>