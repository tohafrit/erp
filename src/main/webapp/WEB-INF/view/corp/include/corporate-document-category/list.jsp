<div class="list__header">
    <h1 class="list__header_title">Корпоративные категории документов</h1>
    <div class="buttons list__table_buttons">
        <i class="icon add link blue list__btn-add" title="Добавить категорию"></i>
        <div class="list__header_buttons-expand">
            <i class="icon expand alternate link blue list__btn-expand" title="Развернуть"></i>
            <i class="icon compress alternate link blue list__btn-compress" title="Свернуть"></i>
        </div>
    </div>
</div>
<div class="list__table-block">
    <div class="list__table table-sm"></div>
</div>

<script>
    $(() => {
        const $content = $('div.root__content');
        const $datatable = $('div.list__table');
        const $btnAdd = $('i.list__btn-add');
        const $btnExpand = $('i.list__btn-expand');
        const $btnCompress = $('i.list__btn-compress');

        const table = new Tabulator('div.list__table', {
            maxHeight: 'calc(100vh - 150px)',
            ajaxURL: '/api/action/corp/corporate-document-category/list/load',
            layout: 'fitColumns',
            dataTree: true,
            dataTreeStartExpanded: true,
            resizableColumns: false,
            selectable: 1,
            initialSort:[
                { column: 'sort', dir: 'asc'}
            ],
            columns: [
                {
                    title: 'Наименование',
                    field: 'name',
                    headerSort: false
                },
                {
                    title: 'Описание',
                    field: 'description',
                    headerSort: false,
                    variableHeight: true,
                    minWidth: 200,
                    width: 300,
                    formatter: cell => {
                        $(cell.getElement()).css({'white-space': 'pre-wrap'});
                        return cell.getValue();
                    }
                },
                {
                    title: 'Сортировка',
                    field: 'sort',
                    headerSort: false
                }
            ],
            rowDblClick: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowContext: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowContextMenu: component => {
                const menu = [];
                const id = component.getData().id;
                menu.push({
                    label: '<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>',
                    action: () => editCategory(id)
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => deleteCategory(id)
                });
                return menu;
            }
        });

        // Кнопка свернуть
        $btnCompress.on({
            'click': () => {
                const collapse = rows => {
                    $.each(rows, (inx, row) => {
                        row.treeCollapse();
                        collapse(row.getTreeChildren());
                    });
                };
                collapse(table.getRows());
            }
        });

        // Кнопка развернуть
        $btnExpand.on({
            'click': () => {
                const expand = rows => {
                    $.each(rows, (inx, row) => {
                        row.treeExpand();
                        expand(row.getTreeChildren());
                    });
                };
                expand(table.getRows());
            }
        });

        // Кнопка добавления
        $btnAdd.on({
            'click': () => editCategory()
        });

        // Функция добавления/редактирования категории
        function editCategory(id) {
            $.modalWindow({
                loadURL: '/api/view/corp/corporate-document-category/list/edit',
                loadData: { id: id },
                submitURL: '/api/action/corp/corporate-document-category/list/edit/save',
                onSubmitSuccess: () => table.setData()
            });
        }

        // Функция удаления категории
        function deleteCategory(id) {
            confirmDialog({
                title: 'Удаление категории',
                message: 'Вы уверены, что хотите удалить категорию?',
                onAccept: () => {
                    $.ajax({
                        method: 'DELETE',
                        url: '/api/action/corp/corporate-document-category/list/delete/' + id,
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => table.setData());
                }
            });
        }
    })
</script>