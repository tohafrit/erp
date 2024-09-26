<div class="list__header">
    <h1 class="list__header_title"><fmt:message key="supplier.title"/></h1>
    <div class="list__header_buttons">
        <i class="icon filter link list__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        <i class="icon add link blue list__btn-add" title="<fmt:message key="label.button.add"/>"></i>
    </div>
</div>
<div class="list__table-block">
    <div class="list__table-main-block">
        <div class="list__table-wrap">
            <jsp:include page="/api/view/prod/supplier/list/filter"/>
            <div class="list__table table-sm table-striped"></div>
        </div>
    </div>
</div>

<script>
    $(() => {
        const $addSupplier = $('i.list__btn-add');
        const $btnFilter = $('i.list__btn-filter');
        const $filter = $('div.list_filter__main');
        const $filterForm = $('form.list_filter__form');
        const $btnSearch = $('div.list_filter__btn-search');
        const $content = $('div.root__content');

        let initialPage = '${initialPage}';
        const selectedSupplierId = '${selectedSupplierId}'; // Для загрузки определенного производителя в таблице

        // Кнопка фильтра
        $btnFilter.on({
            'click': function() {
                $(this).toggleClass('blue');
                $filter.toggle(!$filter.is(':visible'));
            }
        });

        // Таблица поставщиков
        let isSessionLoad = true;
        let initLoadCount = 0;
        const table = new Tabulator('div.list__table', {
            ajaxURL: '/api/action/prod/supplier/list/load',
            ajaxRequesting: (url, params) => {
                if (initialPage && (initLoadCount === 0 || initLoadCount === 1)) {
                    params.initLoad = initLoadCount++ === 0;
                    params.filterForm = formToJson($filterForm);
                } else {
                    params.filterForm = formToJson($filterForm);
                }
            },
            ajaxSorting: true,
            layout: 'fitColumns',
            pagination: 'remote',
            height: '100%',
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                { title: '<fmt:message key="supplier.field.name"/>', field: 'name' },
                { title: '<fmt:message key="supplier.field.inn"/>', field: 'inn' },
                { title: '<fmt:message key="supplier.field.kpp"/>', field: 'kpp' }
            ],
            dataLoaded: () => {
                if (initialPage) {
                    if (initLoadCount === 1) {
                        const maxPage = table.getPageMax();
                        table.setPage(initialPage > maxPage ? maxPage : initialPage);
                    } else if (initLoadCount === 2) {
                        initialPage = '';
                        isSessionLoad = false;
                        table.deselectRow();
                        table.selectRow(selectedSupplierId);
                        const rows = table.getSelectedRows();
                        if (rows.length > 1) {
                            table.deselectRow();
                        }
                        if (rows && rows.length) {
                            rows[0].scrollTo();
                        }
                    }
                }
            },
            ajaxError: () => initialPage = '',
            rowClick: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowContext: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowContextMenu: () => {
                return [
                    {
                        label: '<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>',
                        action: (e, row) => editSupplier(row.getData().id)
                    },
                    {
                        label: '<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>',
                        action: (e, row) => deleteSupplier(row.getData().id)
                    }
                ];
            }
        });

        // Добавление поставщика
        $addSupplier.on({
            'click': () => editSupplier()
        });

        // Функция добавления/редактирования поставщика
        function editSupplier(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/supplier/list/edit',
                loadData: { id: id },
                submitURL: '/api/action/prod/supplier/list/edit/save',
                onSubmitSuccess: response => {
                    if (id) {
                        table.setPage(table.getPage());
                    } else {
                        $.get({
                            url: '/api/view/prod/supplier/list',
                            data: {
                                selectedSupplierId: response.attributes.addedSupplierId
                            }
                        }).done(html => $content.html(html));
                    }
                }
            });
        }

        // Функция удаления поставщика
        function deleteSupplier(id) {
            confirmDialog({
                title: '<fmt:message key="supplier.delete.title"/>',
                message: '<fmt:message key="supplier.delete.confirm"/>',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: '/api/action/prod/supplier/list/delete/' + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setData())
            });
        }

        // Кнопка поиска
        $btnSearch.on({
            'click': () => table.setData()
        });
    });
</script>