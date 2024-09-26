<div class="list__header">
    <h1 class="list__header_title">Реквизиты компании</h1>
    <div class="list__table_buttons">
        <i class="icon filter link list__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        <i class="icon add link blue list__btn-add" title="<fmt:message key="label.button.add"/>"></i>
    </div>
</div>
<div class="list__table-block">
    <form:form modelAttribute="companyDetailListFilterForm" cssClass="ui tiny form secondary segment list_filter__form">
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
        <div class="ui three column grid">
            <div class="column field">
                <label>Название позиции</label>
                <form:input path="name" type="search"/>
            </div>
            <div class="column field">
                <label>Значение позиции</label>
                <form:input path="value" type="search"/>
            </div>
            <div class="column field">
                <label>Сортировка</label>
                <form:input path="sort" type="search"/>
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
            ajaxURL: '/api/action/corp/company-detail/list/load',
            ajaxRequesting: (url, params) => {
                params.filterForm = formToJson($filterForm);
            },
            layout: 'fitColumns',
            selectable: 1,
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                {
                    title: 'Название позиции',
                    field: 'name'
                },
                {
                    title: 'Значение позиции',
                    field: 'value',
                    formatter: 'html'
                },
                {
                    title: 'Сортировка',
                    field: 'sort',
                    variableHeight: true,
                    minWidth: 150,
                    width: 250
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
                    action: () => editCompanyDetail(id)
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => deleteCompanyDetail(id)
                });
                return menu;
            }
        });

        // Кнопка добавления
        $btnAdd.on({
            'click': () => editCompanyDetail()
        });

        // Функция добавления/редактирования позиции реквизитов компании
        function editCompanyDetail(id) {
            $.modalWindow({
                loadURL: '/api/view/corp/company-detail/list/edit',
                loadData: { id: id },
                submitURL: '/api/action/corp/company-detail/list/edit/save',
                onSubmitSuccess: () => table.setData()
            });
        }

        // Функция удаления позиция из реквизитов компании
        function deleteCompanyDetail(id) {
            confirmDialog({
                title: 'Удаление позиции из реквизитов компании',
                message: 'Вы уверены, что хотите удалить позицию из реквизитов компании?',
                onAccept: () => {
                    $.ajax({
                        method: 'DELETE',
                        url: '/api/action/corp/company-detail/list/delete/' + id,
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
