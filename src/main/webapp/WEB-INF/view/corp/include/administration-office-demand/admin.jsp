<div class="admin__header">
    <h1 class="admin__header_title">Управление заявками</h1>
    <div class="admin__header_buttons">
        <i class="icon filter link admin__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
    </div>
</div>
<div class="admin__table-block">
    <div class="admin__table-main-block">
        <div class="admin__table-wrap">
            <jsp:include page="/api/view/corp/administration-office-demand/admin/filter"/>
            <div class="admin__table table-sm table-striped"></div>
        </div>
    </div>
    <div class="admin__table-sub-block">
        <i class="close link blue icon admin__btn-close-sub-block"></i>
        <div class="admin__sub-block-content"></div>
    </div>
</div>

<script>
    $(() => {
        const $btnFilter = $('i.admin__btn-filter');
        const $btnSearch = $('div.admin_filter__btn-search');
        const $filter = $('div.admin_filter__main');
        const $filterForm = $('form.admin_filter__form');
        const $subBlock = $('div.admin__table-sub-block');
        const $subContent = $('div.admin__sub-block-content');
        const $btnCloseSubBlock = $('i.admin__btn-close-sub-block');

        // Кнопка фильтра
        $btnFilter.on({
            'click': function() {
                $(this).toggleClass('primary');
                $filter.toggle(!$filter.is(':visible'));
            }
        });

        // Таблица заявок
        let isSessionLoad = true;
        const table = new Tabulator('div.admin__table', {
            pagination: 'remote',
            layout: 'fitColumns',
            ajaxURL: '/api/action/corp/administration-office-demand/admin/load',
            ajaxRequesting: (url, params) => {
                params.filterForm = formToJson($filterForm);
            },
            ajaxSorting: true,
            height: '100%',
            initialSort:[
                { column: 'requestDate', dir: 'desc' }
            ],
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                {
                    title: 'Дата заявки',
                    field: 'requestDate',
                    formatter: cell => dateTimeStdToString(cell.getValue())
                },
                { title: 'Заявитель', field: 'user' },
                { title: 'Номер комнаты', field: 'roomNumber' },
                { title: 'Описание проблемы', field: 'reason' },
                {
                    title: 'Статус',
                    field: 'statusText',
                    formatter: cell => {
                        const status = cell.getRow().getData().status;
                        let color = '#faa';
                        if (status === 'IN_PROGRESS') color = '#ffa';
                        if (status === 'FINISHED') color = '#afa';
                        $(cell.getElement()).css({'background-color': color});
                        return cell.getValue();
                    }
                }
            ],
            renderComplete: () => {
                const rowIndex = sessionStorage.getItem(ssAdministrationOfficeDemand_admin_rowIndex);
                if (rowIndex != null) {
                    const pageNumber = Math.ceil((Number(rowIndex) + 1) / table.getPageSize());
                    if (isSessionLoad) {
                        table.setPage(pageNumber);
                        isSessionLoad = false;
                    }
                    if (pageNumber === table.getPage()) {
                        const rowPosition = Number(rowIndex) - (pageNumber - 1) * table.getPageSize();
                        const row = table.getRowFromPosition(rowPosition, true);
                        if (row) {
                            row.select();
                            row.scrollTo();
                        }
                    }
                }
            },
            rowClick: (e, row) => {
                if (row.isSelected()) {
                    table.deselectRow();
                    sessionStorage.removeItem(ssAdministrationOfficeDemand_admin_rowIndex);
                } else {
                    const data = row.getData();
                    table.deselectRow();
                    row.select();
                    showDetails(data.id);
                    sessionStorage.setItem(ssAdministrationOfficeDemand_admin_rowIndex, (table.getPage() - 1) * table.getPageSize() + row.getPosition());
                }
            },
            rowContext: (e, row) => {
                table.deselectRow();
                row.select();
                sessionStorage.setItem(ssAdministrationOfficeDemand_admin_rowIndex, (table.getPage() - 1) * table.getPageSize() + row.getPosition());
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

        // Функция раскрытия окна подробностей
        function showDetails(id) {
            $.get({
                url: '/api/view/corp/administration-office-demand/admin/detail',
                data: { entityId: id },
                beforeSend: () => togglePreloader(true),
                complete: () => togglePreloader(false)
            }).done(html => {
                $subContent.html(html);
                $subBlock.show();
            }).fail(() => $subBlock.hide());
        }

        // Кнопка поиска
        $btnSearch.on({
            'click': () => {
                sessionStorage.removeItem(ssAdministrationOfficeDemand_admin_rowIndex);
                table.setData();
            }
        });
    });
</script>