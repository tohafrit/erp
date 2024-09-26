<div class="list__header">
    <h1 class="list__header_title">
        <c:set var="typeName">
            <fmt:message key="${companyType.property}"/>
        </c:set>
        <fmt:message key="company.list.title">
            <fmt:param value="${typeName}"/>
        </fmt:message>
    </h1>
    <div class="list__header_buttons">
        <i class="icon filter link list__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        <i class="icon add link blue list__btn-add-company" title="<fmt:message key="label.button.add"/>"></i>
    </div>
</div>
<div class="list__table-block">
    <jsp:include page="/api/view/prod/company/list/filter"/>
    <div class="list__table table-sm table-striped"></div>
</div>

<script>
    $(() => {
        const $btnFilter = $('i.list__btn-filter');
        const $btnAdd = $('i.list__btn-add-company');
        const $btnSearch = $('div.list_filter__btn-search');
        const $filter = $('div.list_filter__main');
        const $filterForm = $('form.list_filter__form');
        const typeId = '${companyType.id}';
        const $content = $('div.root__content');
        //
        const selectedCompanyId = '${selectedCompanyId}'; // Для загрузки определенной позиции изделия в таблице
        let initialPage = '${initialPage}';

        const columnMap = new Map();
        columnMap.set('name',                   '<fmt:message key="company.field.name"/>');
        if (typeId !== '8') {
            columnMap.set('fullName',           '<fmt:message key="company.field.fullName"/>');
            columnMap.set('chiefName',          '<fmt:message key="company.field.chiefName"/>');
            columnMap.set('chiefPosition',      '<fmt:message key="company.field.chiefPosition"/>');
        }
        columnMap.set('phoneNumber',            '<fmt:message key="company.field.phoneNumber"/>');
        columnMap.set('contactPerson',          '<fmt:message key="company.field.contactPerson"/>');
        columnMap.set('location',               '<fmt:message key="company.field.location"/>');
        columnMap.set('inn',                    '<fmt:message key="company.field.inn"/>');
        columnMap.set('kpp',                    '<fmt:message key="company.field.kpp"/>');
        if (typeId !== '8') {
            columnMap.set('ogrn',               '<fmt:message key="company.field.ogrn"/>');
            columnMap.set('inspectorName',      '<fmt:message key="company.field.inspectorName"/>');
            columnMap.set('inspectorHead',      '<fmt:message key="company.field.inspectorHead"/>');
        }
        columnMap.set('factualAddress',         '<fmt:message key="company.field.factualAddress"/>');
        columnMap.set('juridicalAddress',       '<fmt:message key="company.field.juridicalAddress"/>');
        columnMap.set('mailAddress',            '<fmt:message key="company.field.mailAddress"/>');
        columnMap.set('note',                   '<fmt:message key="company.field.note"/>');

        // Дефолтные колонки
        const numeration = [TABR_COL_REMOTE_ROW_NUM];

        const defaultColumns = {};
        defaultColumns.name = { title: columnMap.get('name'), field: 'name' };
        if (typeId !== '8') {
            defaultColumns.fullName = { title: columnMap.get('fullName'), field: 'fullName' };
            defaultColumns.chiefName = { title: columnMap.get('chiefName'), field: 'chiefName' };
            defaultColumns.chiefPosition = { title: columnMap.get('chiefPosition'), field: 'chiefPosition' };
        }
        defaultColumns.phoneNumber = { title: columnMap.get('phoneNumber'), field: 'phoneNumber' };
        defaultColumns.contactPerson = { title: columnMap.get('contactPerson'), field: 'contactPerson' };
        defaultColumns.location = { title: columnMap.get('location'), field: 'location' };
        defaultColumns.inn = { title: columnMap.get('inn'), field: 'inn' };
        defaultColumns.kpp = { title: columnMap.get('kpp'), field: 'kpp' };
        if (typeId !== '8') {
            defaultColumns.ogrn = { title: columnMap.get('ogrn'), field: 'ogrn' };
            defaultColumns.inspectorName = { title: columnMap.get('inspectorName'), field: 'inspectorName' };
            defaultColumns.inspectorHead = { title: columnMap.get('inspectorHead'), field: 'inspectorHead' };
        }
        defaultColumns.factualAddress = {
            title: columnMap.get('factualAddress'),
            field: 'factualAddress',
            headerSort: false
        };
        defaultColumns.juridicalAddress = {
            title: columnMap.get('juridicalAddress'),
            field: 'juridicalAddress',
            headerSort: false
        };
        defaultColumns.mailAddress = {
            title: columnMap.get('mailAddress'),
            field: 'mailAddress',
            headerSort: false
        };
        defaultColumns.note = { title: columnMap.get('note'), field: 'note' };

        // Таблица компаний
        let isSessionLoad = true;
        let initLoadCount = 0;
        const table = new Tabulator('div.list__table', {
            pagination: 'remote',
            paginationSize: initialPage ? TABR_PAGE_SIZE : TABR_PAGE_SIZE,
            ajaxURL: '/api/action/prod/company/list/load',
            tooltips: true,
            tooltipsHeader: true,
            ajaxRequesting: (url, params) => {
                if (initialPage && (initLoadCount === 0 || initLoadCount === 1)) {
                    params.initLoad = initLoadCount++ === 0;
                    params.filterForm = formToJson($filterForm);
                    params.typeId = '${companyType.id}';
                } else {
                    params.filterForm = formToJson($filterForm);
                    params.typeId = '${companyType.id}';
                }
            },
            dataLoaded: () => {
                if (initialPage) {
                    if (initLoadCount === 1) {
                        const maxPage = table.getPageMax();
                        table.setPage(initialPage > maxPage ? maxPage : initialPage);
                    } else if (initLoadCount === 2) {
                        initialPage = '';
                        isSessionLoad = false;
                        table.deselectRow();
                        table.selectRow(selectedCompanyId);
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
            ajaxSorting: true,
            height: '100%',
            initialSort: initialPage ? [] : false,
            columns: numeration.concat(Object.values(defaultColumns)),
            rowClick: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowContext: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowContextMenu: company => {
                const menu = [];
                const data = company.getData();
                menu.push({
                    label: `<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>`,
                    action: () => editCompany(data.id)
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => deleteCompany(data.id)
                });
                return menu;
            }
        });

        // Кнопка фильтра
        $btnFilter.on({
            'click': function () {
                $(this).toggleClass('blue');
                $filter.toggle(!$filter.is(':visible'));
            }
        });

        // Кнопка поиска
        $btnSearch.on({
            'click': () => table.setData()
        });

        // Кнопка добавление компании
        $btnAdd.on({
            'click': () => editCompany()
        });

        // Функция добавления/редактирования компании
        function editCompany(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/company/list/edit',
                loadData: {
                    id: id,
                    typeId: typeId
                },
                submitURL: '/api/action/prod/company/list/edit/save',
                onSubmitSuccess: response => {
                    if (id) {
                        table.setPage(table.getPage());
                    } else {
                        $.get({
                            url: '/api/view/prod/company/list',
                            data: {
                                selectedCompanyId: response.attributes.addedCompanyId,
                                typeId: typeId
                            }
                        }).done(html => $content.html(html));
                    }
                }
            });
        }

        // Функция удаления компании
        function deleteCompany(id) {
            confirmDialog({
                title: '<fmt:message key="company.list.delete.title"/>',
                message: '<fmt:message key="company.list.delete.confirm"/>',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: '/api/action/prod/company/list/delete/' + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setPage(table.getPage()))
            });
        }
    });
</script>