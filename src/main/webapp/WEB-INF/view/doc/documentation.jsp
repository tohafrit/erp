<div class="admin_documentation__main">
    <div class="admin_documentation__header">
        <h1 class="admin_documentation__header_title"><fmt:message key="documentation.list.title"/></h1>
        <div class="admin_documentation__header_buttons">
            <div class="ui icon small buttons">
                <div class="ui button basic admin_documentation__btn-add" title="Добавить">
                    <i class="add icon"></i>
                </div>
            </div>
        </div>
    </div>
    <div class="admin_documentation__table"></div>
</div>
<script>
    $(() => {
        const $addDocumentation = $('div.admin_documentation__btn-add');
        const table = new Tabulator('div.admin_documentation__table', {
            maxHeight: '80vh',
            ajaxURL: '/admin/documentation/list',
            layout: 'fitColumns',
            dataTree: true,
            dataTreeStartExpanded: false,
            headerFilterPlaceholder: '<fmt:message key="text.search"/>',
            resizableColumns: false,
            selectable: false,
            headerSort: false,
            columns: [
                {
                    title: '<fmt:message key="documentation.field.name"/>',
                    field: 'name',
                    headerFilter: 'input',
                    headerFilterFunc: 'like'
                },
                {
                    title: '<fmt:message key="documentation.field.menuItem"/>',
                    field: 'menuName',
                    headerFilter: 'input',
                    headerFilterFunc: 'like'
                }
            ],
            tableBuilt: function() {
                const $table = $(this.element);
                $.each($table.find('.tabulator-header-filter input'), function() {
                    $(this).wrap('<div class="ui input fluid"></div>');
                });
            },
            rowContextMenu: () => {
                return [
                    {
                        label: '<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>',
                        action: (e, row) => editDocumentation(row.getData().id)
                    },
                    {
                        label: '<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>',
                        action: (e, row) => {
                            confirmDialog({
                                title: '<fmt:message key="documentation.list.delete.title"/>',
                                message: '<fmt:message key="documentation.list.delete.confirm"/>',
                                onAccept: () => {
                                    $.ajax({
                                        method: 'DELETE',
                                        url: '/admin/documentation/list/delete/' + row.getData().id,
                                        beforeSend: () => togglePreloader(true),
                                        complete: () => togglePreloader(false)
                                    }).done(() => row.delete());
                                }
                            });
                        }
                    }
                ];
            }
        });
        // Добавление документации
        $addDocumentation.on({
            'click': () => editDocumentation()
        });
        // Функция добавления/редактирования документации
        function editDocumentation(id) {
            $.modalWindow({
                loadURL: '/admin/documentation/list/edit',
                loadData: { id: id },
                submitURL: '/admin/documentation/list/ops/save',
                onSubmitSuccess: () => table.setData()
            });
        }
    });
</script>