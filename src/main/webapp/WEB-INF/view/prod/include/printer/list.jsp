<div class="list__header">
    <h1 class="list__header_title"><fmt:message key="printer.title"/></h1>
    <div class="list__header_buttons">
        <div class="ui icon small buttons">
            <div class="ui button basic list__btn-add" title="Добавить принтер">
                <i class="add icon"></i>
            </div>
        </div>
    </div>
</div>
<div class="list__table-block">
    <div class="list__table-main-block">
        <div class="list__table-wrap">
            <div class="list__table"></div>
        </div>
    </div>
    <div class="list__table-sub-block">
        <i class="close link blue icon list__btn-close-sub-block"></i>
        <div class="list__sub-block-content"></div>
    </div>
</div>

<script>
    $(() => {
        const $btnAdd = $('div.list__btn-add');
        const $subBlock = $('div.list__table-sub-block');
        const $subContent = $('div.list__sub-block-content');
        const $btnCloseSubBlock = $('i.list__btn-close-sub-block');

        // Таблица принтеров
        const table = new Tabulator('div.list__table', {
            ajaxURL: '/api/action/prod/printer/list/load',
            layout: 'fitColumns',
            pagination: 'local',
            paginationSize: 30,
            paginationSizeSelector: [30, 40, 50],
            height: '100%',
            columns: [
                {
                    title: '#',
                    resizable: false,
                    headerSort: false,
                    frozen: true,
                    width: 70,
                    formatter: 'rownum'
                },
                { title: '<fmt:message key="printer.field.name"/>', field: 'name' },
                { title: '<fmt:message key="printer.field.ip"/>', field: 'ip' },
                { title: '<fmt:message key="printer.field.port"/>', field: 'port' },
                { title: '<fmt:message key="printer.field.users"/>', field: 'users' },
                { title: '<fmt:message key="printer.field.description"/>', field: 'description' }
            ],
            rowClick: (e, row) => {
                if (row.isSelected()) {
                    table.deselectRow();
                    sessionStorage.removeItem(ssPrinter_selectedId);
                } else {
                    table.deselectRow();
                    row.select();
                    showDetails(row.getData().id);
                    sessionStorage.setItem(ssPrinter_selectedId, row.getData().id);
                }
            },
            rowContext: (e, row) => {
                table.deselectRow();
                row.select();
                sessionStorage.setItem(ssPrinter_selectedId, row.getData().id);
            },
            rowContextMenu: printer => {
                const menu = [];
                const data = printer.getData();
                menu.push({
                    label: `<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>`,
                    action: () => editPrinter(data.id)
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => deletePrinter(data.id)
                });
                return menu;
            },
            dataLoaded: () => {
                const ssPrinterSelectedId = sessionStorage.getItem(ssPrinter_selectedId);
                let row = table.searchRows("id", "=", ssPrinterSelectedId)[0];
                if (row !== undefined) {
                    row.pageTo();
                    row.select();
                    row.scrollTo();
                }
            }
        });

        // Ресайз вспомогательного контейнера
        $subBlock.resizable({
            autoHide: true,
            handles: 'n',
            ghost: true,
            stop: () => {
                $subBlock.css({
                    'width': '100%',
                    'top': 0
                });
            }
        });

        // Скрытие области вспомогательного контейнера
        $btnCloseSubBlock.on({
            'click': () => {
                table.deselectRow();
                $subBlock.hide();
            }
        });

        // Функция раскрытия подробностей
        function showDetails(id) {
            $.get({
                url: '/api/view/prod/printer/list/users',
                data: { entityId: id },
                beforeSend: () => togglePreloader(true),
                complete: () => togglePreloader(false)
            }).done(html => {
                $subContent.html(html);
                $subBlock.show();
            }).fail(() => $subBlock.hide());
        }

        // Функция добавления/редактирования принтера
        function editPrinter(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/printer/list/edit',
                loadData: { id: id },
                submitURL: '/api/action/prod/printer/list/edit/save',
                onSubmitSuccess: response => {
                    if (!id) {
                        sessionStorage.setItem(ssPrinter_selectedId, response.attributes.addedPrinterId);
                    }
                    table.setData();
                }
            });
        }

        // Функция удаления принтера
        function deletePrinter(id) {
            confirmDialog({
                title: 'Удаление принтера',
                message: 'Вы уверены, что хотите удалить принтер?',
                onAccept: () => {
                    $.ajax({
                        method: 'DELETE',
                        url: '/api/action/prod/printer/list/delete/' + id,
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => {
                        sessionStorage.removeItem(ssPrinter_selectedId);
                        table.setData();
                    });
                }
            });
        }

        // Кнопка добавления принтера
        $btnAdd.on({
            'click': () => editPrinter()
        });
    });
</script>