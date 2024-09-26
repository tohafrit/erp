<div class="list__header">
    <h1 class="list__header_title">Назначения</h1>
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

        // Таблица назначений
        const datatable = new Tabulator('div.list__table', {
            selectable: 1,
            ajaxURL: '/api/action/prod/component-purpose/list/load',
            layout: 'fitColumns',
            resizableColumns: false,
            height: '100%',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: 'Наименование', field: 'name' },
                {
                    title: 'Описание',
                    field: 'description',
                    variableHeight: true,
                    minWidth: 400,
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
                    action: () => editPurpose(data.id)
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => deletePurpose(data.id)
                });
                return menu;
            }
        });

        // Функция добавления/редактирования назначение
        function editPurpose(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/component-purpose/list/edit',
                loadData: { id: id },
                submitURL: '/api/action/prod/component-purpose/list/edit/save',
                onSubmitSuccess: () => datatable.setData()
            });
        }

        // Функция удаления назначения
        function deletePurpose(id) {
            confirmDialog({
                title: 'Удаление назначения',
                message: 'Вы уверены, что хотите удалить назначение?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: '/api/action/prod/component-purpose/list/delete/' + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => datatable.setData())
            });
        }

        // Кнопка добавления назначения
        $btnAdd.on({
            'click': () => editPurpose()
        });
    });
</script>