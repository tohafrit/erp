<div class="list__header">
    <h1 class="list__header_title">Инструкции по охране труда</h1>
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
        // Таблица инструкций
        const table = new Tabulator('div.list__table', {
            ajaxURL: '/api/action/prod/labor-protection-instruction/list/load',
            layout: 'fitColumns',
            resizableColumns: false,
            pagination: 'local',
            height: '100%',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: 'Наименование', field: TABR_FIELD.NAME },
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
                    action: () => editInstruction(data.id)
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => deleteInstruction(data.id)
                });
                return menu;
            }
        });

        // Функция добавления/редактирования инструкции
        function editInstruction(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/labor-protection-instruction/list/edit',
                loadData: { id: id },
                submitAsJson: true,
                submitURL: '/api/action/prod/labor-protection-instruction/list/edit/save',
                onSubmitSuccess: () => table.setData()
            });
        }

        // Функция удаления инструкции
        function deleteInstruction(id) {
            confirmDialog({
                title: 'Удаление инструкции',
                message: 'Вы уверены, что хотите удалить инструкцию?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: '/api/action/prod/labor-protection-instruction/list/delete/' + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setData())
            });
        }

        // Кнопка добавления инструкции
        $btnAdd.on({
            'click': () => editInstruction()
        });
    });
</script>