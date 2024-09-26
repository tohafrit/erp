<div class="list__header">
    <h1 class="list__header_title">Категории</h1>
    <div class="list__table_buttons">
        <i class="icon add link blue list__btn-add" title="<fmt:message key="label.button.add"/>"></i>
        <i class="icon expand alternate link blue list__btn-expand" title="Развернуть"></i>
        <i class="icon compress alternate link blue list__btn-compress" title="Свернуть"></i>
    </div>
</div>
<div class="list__table-block">
    <div class="list__table table-sm table-striped"></div>
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
            ajaxURL: '/api/action/prod/component-category/list/load',
            layout: 'fitDataStretch',
            dataTree: true,
            dataTreeStartExpanded: true,
            headerFilterPlaceholder: '<fmt:message key="text.search"/>',
            resizableColumns: false,
            selectable: 1,
            initialSort:[
                { column: 'name', dir: 'asc'}
            ],
            columns: [
                {
                    title: 'Наименование',
                    field: 'name',
                    headerFilter: 'input',
                    headerFilterFunc: (headerValue, rowValue, rowData) => {
                        const search = data => {
                            const result = data.name.toLowerCase().indexOf(headerValue.toLowerCase()) === -1;
                            if (result) {
                                const childArr = data._children;
                                if (childArr != null && childArr.length > 0) {
                                    for (let child of childArr) {
                                        if (search(child)) {
                                            return true;
                                        }
                                    }
                                }
                            }
                            return !result;
                        };
                        return search(rowData);
                    }
                },
                {
                    title: 'Описание',
                    field: 'description',
                    headerSort: false,
                    variableHeight: true,
                    minWidth: 200,
                    formatter: 'textarea'
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
                    label: '<i class="file alternate outline icon blue"></i>Атрибуты',
                    action: () => showAttributes(id)
                });
                menu.push({
                    label: '<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>',
                    action: () => editCategory(id)
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => deleteCategory(id)
                });
                return menu;
            },
            tableBuilt: () => $datatable.find('div.tabulator-header-filter input').each(
                (inx, elem) => $(elem).wrap('<div class="ui input fluid"></div>'))
        });

        // Кнопка добавления
        $btnAdd.on({
            'click': () => editCategory()
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

        // Функция добавления/редактирования категории
        function editCategory(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/component-category/list/edit',
                loadData: { id: id },
                submitURL: '/api/action/prod/component-category/list/edit/save',
                onSubmitSuccess: () => table.setData()
            });
        }

        // Функция удаления категории
        function deleteCategory(id) {
            confirmDialog({
                title: 'Удаление категории',
                message: 'Вы уверены, что хотите удалить категорию?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: '/api/action/prod/component-category/list/delete/' + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setData())
            });
        }

        // Функция отображения атрибутов категории
        function showAttributes(id) {
            $.get({
                url: '/api/view/prod/component-category/attribute',
                data: { categoryId: id },
                beforeSend: () => togglePreloader(true),
                complete: () => togglePreloader(false)
            }).done(html => $content.html(html));
        }
    })
</script>