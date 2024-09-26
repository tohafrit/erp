<div class="list__header">
    <h1 class="list__header_title">Канцелярские товары</h1>
    <div class="list__table_buttons">
        <i class="icon filter link list__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        <i class="icon add link blue list__btn-add" title="Добавить канцелярский товар"></i>
    </div>
</div>
<div class="list__table-block">
    <form:form modelAttribute="officeSupplyListFilterForm" cssClass="ui tiny form secondary segment list_filter__form">
        <div class="field">
            <div class="ui icon small buttons">
                <div class="ui button list_filter__btn-search" title="<fmt:message key="label.button.search"/>">
                    <i class="search blue icon"></i>
                </div>
                <div class="ui button list_filter__btn-clear-all" title="<fmt:message key="label.button.clearFilter"/>">
                    <i class="times blue icon"></i>
                </div>
            </div>
        </div>
        <div class="ui four column grid">
            <div class="column field">
                <label>
                    Артикул
                </label>
                <input type="search" name="article"/>
            </div>
            <div class="column field">
                <label>
                    Наименование
                </label>
                <input type="search" name="name"/>
            </div>
            <div class="column field">
                <label>
                    Только для секритариата
                </label>
                <form:select cssClass="ui dropdown std-select" path="onlySecretaries">
                    <form:option value=""><fmt:message key="text.notSpecified"/></form:option>
                    <form:option value="true"><fmt:message key="text.yes"/></form:option>
                    <form:option value="false"><fmt:message key="text.no"/></form:option>
                </form:select>
            </div>
            <div class="column field">
                <label>
                    Активность
                </label>
                <form:select cssClass="ui dropdown std-select" path="active">
                    <form:option value=""><fmt:message key="text.notSpecified"/></form:option>
                    <form:option value="true"><fmt:message key="text.yes"/></form:option>
                    <form:option value="false"><fmt:message key="text.no"/></form:option>
                </form:select>
            </div>
        </div>
    </form:form>
    <div class="list__table table-sm table-striped"></div>
</div>

<script>
    $(() => {
        const $content = $('div.root__content');
        const $table = $('div.list__table');
        const $btnFilter = $('i.list__btn-filter');
        const $filterForm = $('form.list_filter__form');
        const $btnSearch = $('div.list_filter__btn-search');
        const $btnAdd = $('i.list__btn-add');
        const $clearAllButton = $('div.list_filter__btn-clear-all');

        $clearAllButton.on({
            'click': () => formClear($filterForm)
        });

        $filterForm.enter(() => $btnSearch.trigger('click'));

        // Кнопка фильтра
        $btnFilter.on({
            'click': function() {
                $(this).toggleClass('primary');
                $filterForm.toggle(!$filterForm.is(':visible'));
            }
        });

        const table = new Tabulator('div.list__table', {
            pagination: 'remote',
            height: '100%',
            ajaxURL: '/api/action/corp/office-supply/list/load',
            ajaxRequesting: (url, params) => {
                params.filterForm = formToJson($filterForm);
            },
            layout: 'fitColumns',
            selectable: 1,
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                {
                    title: 'Артикул',
                    field: 'article',
                    resizable: false,
                    width: 100,
                    formatter: cell => {
                        let article = cell.getValue();
                        let articleDash = cell.getRow().getData().articleContainDash;
                        return articleDash ? article :
                            '<a target="_blank" href="https://www.komus.ru/search?text= '+ article +'">'+ article +'</a>'
                    }
                },
                {
                    title: 'Наименование',
                    field: 'name',
                    variableHeight: true,
                    minWidth: 200,
                    formatter: 'textarea'
                },
                {
                    title: 'Только для секритариата',
                    field: 'onlySecretaries',
                    resizable: false,
                    headerSort: false,
                    width: 200,
                    hozAlign: 'center',
                    formatter: cell => booleanToLight(!cell.getValue(), { onTrue: 'Для всех', onFalse: 'Только для секритариата' })
                },
                {
                    title: 'Активность',
                    field: 'active',
                    resizable: false,
                    headerSort: false,
                    width: 100,
                    hozAlign: 'center',
                    formatter: cell => booleanToLight(cell.getValue(), { onTrue: 'Активный', onFalse: 'Архивный' })
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
            rowContextMenu: row => {
                const menu = [];
                const id = row.getData().id;
                menu.push({
                    label: '<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>',
                    action: () => editOfficeSupply(id)
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => deleteOfficeSupply(id)
                });
                return menu;
            }
        });

        // Кнопка добавления
        $btnAdd.on({
            'click': () => editOfficeSupply()
        });

        // Функция добавления/редактирования канцелярского товара
        function editOfficeSupply(id) {
            $.modalWindow({
                loadURL: '/api/view/corp/office-supply/list/edit',
                loadData: { id: id },
                submitURL: '/api/action/corp/office-supply/list/edit/save',
                onSubmitSuccess: () => table.setData()
            });
        }

        // Функция удаления канцелярского товара
        function deleteOfficeSupply(id) {
            confirmDialog({
                title: 'Удаление канцелярского товара',
                message: 'Вы уверены, что хотите удалить канцелярский товар?',
                onAccept: () => {
                    $.ajax({
                        method: 'DELETE',
                        url: '/api/action/corp/office-supply/list/delete/' + id,
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => table.setData());
                }
            });
        }

        // Кнопка поиска
        $btnSearch.on({
            'click': () => table.setData()
        });
    })
</script>