<div class="ui button basic attribute__btn-return" title="Вернуться к списку категорий">
    <i class="long arrow alternate left icon"></i>
    Категории
</div>
<h1 class="attribute__header_title">Атрибуты категории ${categoryName}</h1>
<div class="ui icon small buttons attribute__table_buttons">
    <div class="ui button basic top left pointing dropdown attribute__btn-add" title="Добавить атрибут">
        <i class="add icon"></i>
        <div class="menu">
            <div class="item attribute__menu_select">
                <fmt:message key="componentAttributeType.select"/>
            </div>
            <div class="item attribute__menu_checkbox">
                <fmt:message key="componentAttributeType.checkbox"/>
            </div>
            <div class="item attribute__menu_input">
                <fmt:message key="componentAttributeType.input"/>
            </div>
        </div>
    </div>
</div>
<div class="attribute__table table-sm table-striped"></div>

<script>
    $(() => {
        const $content = $('div.root__content');
        const categoryId = '${categoryId}';
        const $btnAdd = $('div.attribute__btn-add');
        const $btnReturn = $('div.attribute__btn-return');
        // Данные меню
        const menuData = [
            { $item: $('div.attribute__menu_select'), type: 'SELECT' },
            { $item: $('div.attribute__menu_checkbox'), type: 'CHECKBOX' },
            { $item: $('div.attribute__menu_input'), type: 'INPUT' }
        ];

        // Выпадающий список для добавления атрибута
        $btnAdd.dropdown({
            action: 'hide'
        });

        // Кнопка возврата на список категорий
        $btnReturn.on({
            'click': () => {
                $.get({
                    url: '/api/view/prod/component-category/list',
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(html => $content.html(html));
            }
        });

        const datatable = new Tabulator('div.attribute__table', {
            maxHeight: 'calc(100vh - 200px)',
            ajaxURL: '/api/action/prod/component-category/attribute/load',
            layout: 'fitDataStretch',
            selectable: 1,
            ajaxSorting: true,
            groupBy: 'type',
            groupToggleElement: false,
            groupHeader: value => value,
            ajaxRequesting: (url, params) => {
                params.categoryId = categoryId;
            },
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: 'Наименование', field: 'name' },
                { title: 'Тип атрибута', field: 'type', visible: false },
                {
                    title: 'Описание',
                    field: 'description',
                    variableHeight: true,
                    width: 300,
                    minWidth: 300,
                    formatter: 'textarea'
                }
            ],
            rowContext: (e, row) => {
                datatable.deselectRow();
                row.select();
            },
            rowContextMenu: component => {
                const menu = [];
                const id = component.getData().id;
                menu.push({
                    label: '<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>',
                    action: () => editAttribute({ id: id })
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => deleteAttribute(id)
                });
                return menu;
            }
        });

        // Привязка функция добавления атрибута
        $.each(menuData, (inx, item) =>
            item.$item.on({
                'click': () => editAttribute({ categoryId: categoryId, type: item.type })
            })
        );

        // Функция добавления/редактирования атрибута
        function editAttribute(data) {
            $.modalWindow({
                loadURL: '/api/view/prod/component-category/attribute/edit',
                loadData: data,
                submitURL: '/api/action/prod/component-category/attribute/save',
                onSubmitSuccess: () => datatable.setData()
            });
        }

        // Функция удаления атрибута
        function deleteAttribute(id) {
            confirmDialog({
                title: 'Удаление атрибута',
                message: 'Вы уверены, что хотите удалить атрибут?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: '/api/action/prod/component-category/attribute/delete/' + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => datatable.setData())
            });
        }
    })
</script>