<div class="list__header">
    <h1 class="list__header_title">Причины изменений ТП, ОК, МК</h1>
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

        // Таблица причин
        const table = new Tabulator('div.list__table', {
            selectable: 1,
            ajaxURL: '/api/action/prod/reason-change/list/load',
            resizableColumns: false,
            height: '100%',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: 'Код', field: 'code' },
                {
                    title: 'Причина',
                    field: 'reason',
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
                table.deselectRow();
                row.select();
            },
            rowContextMenu: component => {
                const menu = [];
                const data = component.getData();
                menu.push({
                    label: `<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>`,
                    action: () => editReason(data.id)
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => deleteReason(data.id)
                });
                return menu;
            }
        });

        // Функция добавления/редактирования причины
        function editReason(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/reason-change/list/edit',
                loadData: { id: id },
                submitURL: '/api/action/prod/reason-change/list/edit/save',
                onSubmitSuccess: () => table.setData()
            });
        }

        // Функция удаления причины
        function deleteReason(id) {
            confirmDialog({
                title: 'Удаление причины',
                message: 'Вы уверены, что хотите удалить причину?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: '/api/action/prod/reason-change/list/delete/' + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setData())
            });
        }

        // Кнопка добавления причины
        $btnAdd.on({
            'click': () => editReason()
        });
    });
</script>