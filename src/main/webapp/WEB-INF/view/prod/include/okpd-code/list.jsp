<div class="list__header">
    <h1 class="list__header_title" data-text="Коды ОКП/ОКПД2"></h1>
    <div class="list__header_buttons">
        <i class="icon add link blue list__btn-add" title="<fmt:message key="label.button.add"/>"></i>
        <i class="ui icon layer group blue dropdown list__btn-type" title="Тип кодов">
            <div class="menu">
                <div class="item" data-type="PRODUCT" data-text="Изделия">Изделия</div>
                <div class="item" data-type="COMPONENT" data-text="Компоненты">Компоненты</div>
            </div>
        </i>
    </div>
</div>
<div class="list__table table-sm table-striped"></div>

<script>
    $(() => {
        const $btnAdd = $('i.list__btn-add');
        const $btnType = $('i.list__btn-type');
        const $title = $('h1.list__header_title');
        // Инициализация параметра типа кодов
        let typeParam;
        {
            const usp = new URLSearchParams(window.location.search);
            const arrTypeValues = ['PRODUCT', 'COMPONENT'];
            const paramValue = usp.get(TABR_FIELD.TYPE);
            arrTypeValues.includes(paramValue) ? typeParam = paramValue : updateURLTypeParam(arrTypeValues[0]);
            updateTitle();
        }

        // Функция обновляет состояние строки браузера параметром типа кодов
        function updateURLTypeParam(value) {
            typeParam = value;
            const usp = new URLSearchParams(window.location.search);
            usp.set(TABR_FIELD.TYPE, value);
            const query = usp.toString();
            page.show(ROUTE.list(query), undefined, false);
            sessionStorage.setItem(S_STORAGE.LIST_QUERY, query);
        }

        // Функция обновления заголовка
        function updateTitle() {
            $title.html($title.data('text') + ' - ' + $btnType.find('div.item[data-type="' + typeParam + '"]').data('text'));
        }

        const table = new Tabulator('div.list__table', {
            ajaxURL: ACTION_PATH.LIST_LOAD,
            ajaxRequesting: (url, params) => {
                params.type = typeParam;
                table.updateColumnDefinition(TABR_FIELD.NAME, { title: typeParam === 'PRODUCT' ? 'Наименование типа' : 'Группа' } );
            },
            headerSort: false,
            height: 'calc(100vh - 140px)',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                TABR_COL_ID,
                { title: 'Наименование типа', field: TABR_FIELD.NAME, minWidth: 300 },
                { title: 'Код', field: TABR_FIELD.CODE, minWidth: 150 }
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
                const id = data.id;
                menu.push({
                    label: '<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>',
                    action: () => editCode(id)
                });
                menu.push({
                    label: '<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>',
                    action: () => deleteCode(id)
                });
                return menu;
            }
        });

        // Кнопка типов кодов
        $btnType.dropdown({ action: 'hide' });
        $btnType.find('div.item').on({
            'click': e => {
                updateURLTypeParam($(e.currentTarget).data('type'));
                updateTitle();
                table.setData();
            }
        });
        $btnType.find('div.item:eq(0)').trigger('click');

        // Скролл до строки и ее выбор по id
        function rowScrollSelect(id) {
            table.selectRow(id);
            table.scrollToRow(id, 'middle', false);
        }

        // Редактирование/добавление
        function editCode(id) {
            $.modalWindow({
                loadURL: VIEW_PATH.LIST_EDIT,
                loadData: { id: id, typeParam: typeParam },
                submitURL: ACTION_PATH.LIST_EDIT_SAVE,
                onSubmitSuccess: resp => {
                    if (!id) id = resp.attributes.id;
                    table.setData().then(() => rowScrollSelect(id));
                }
            });
        }

        // Удаление
        function deleteCode(id) {
            confirmDialog({
                title: 'Удаление кода',
                message: 'Вы действительно хотите удалить код?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: ACTION_PATH.LIST_DELETE + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setData())
            });
        }

        // Добавление
        $btnAdd.on({
            'click': () => editCode()
        });
    })
</script>