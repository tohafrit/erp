<div class="list__header">
    <h1 class="list__header_title">Закупочные ведомости</h1>
    <div class="list__header_buttons">
        <i class="icon filter link blue list__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        <i class="icon add link blue list__btn-add" title="<fmt:message key="label.button.add"/>"></i>
    </div>
</div>
<div class="list__tables-block">
    <div class="list__table-block">
        <div class="list__table-wrap">
            <div class="list__table table-sm table-striped"></div>
        </div>
    </div>
    <div class="list__sub-block">
        <i class="close link blue icon list__btn-close-sub-block"></i>
        <div class="list__sub-block-content"></div>
    </div>
</div>

<script>
    $(() => {
        const tableSelector = 'div.list__table';
        const $btnFilter = $('i.list__btn-filter');
        const $btnAdd = $('i.list__btn-add');
        const $subBlock = $('div.list__sub-block');
        const $subContent = $('div.list__sub-block-content');
        const $btnCloseSubBlock = $('i.list__btn-close-sub-block');
        // Параметры фильтра таблицы
        const tableData = tableDataFromUrlQuery(window.location.search);
        let filterData = tableData.filterData;

        let notInitLoad = false;
        const table = new Tabulator(tableSelector, {
            pagination: 'remote',
            paginationInitialPage: tableData.page,
            paginationSize: tableData.size,
            initialSort: tableData.sort.length ? tableData.sort : [{ column: TABR_FIELD.CREATE_DATE, dir: SORT_DIR_DESC }],
            ajaxURL: ACTION_PATH.LIST_LOAD,
            ajaxRequesting: (url, params) => {
                params.filterData = JSON.stringify(filterData);
                if (notInitLoad) {
                    const query = urlQueryFromTableParams(window.location.search, params);
                    sessionStorage.setItem(S_STORAGE.LIST_QUERY, query);
                    page.show(ROUTE.list(query), undefined, false);
                }
                $subBlock.hide();
                notInitLoad = true;
            },
            ajaxSorting: true,
            height: '100%',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                TABR_COL_ID,
                {
                    title: 'Наименование',
                    field: TABR_FIELD.NAME,
                    hozAlign: 'center'
                },
                {
                    title: 'Дата создания',
                    field: TABR_FIELD.CREATE_DATE,
                    hozAlign: 'center',
                    width: 130,
                    resizable: false,
                    formatter: 'stdDate'
                },
                {
                    title: 'Создана',
                    field: TABR_FIELD.CREATED_BY,
                    hozAlign: 'center'
                },
                {
                    title: 'Запуск',
                    field: TABR_FIELD.NUMBER_IN_YEAR,
                    hozAlign: 'center'
                },
                {
                    title: 'Предыдущий запуск',
                    field: TABR_FIELD.NUMBER_IN_YEAR,
                    hozAlign: 'center'
                },
                {
                    title: 'Версия ЗС',
                    field: TABR_FIELD.VERSION,
                    hozAlign: 'center'
                },
                {
                    title: 'Дата отсечки',
                    field: TABR_FIELD.DATE,
                    hozAlign: 'center',
                    width: 130,
                    resizable: false,
                    formatter: 'stdDate'
                },
                {
                    title: 'Учет запасов',
                    field: TABR_FIELD.RESERVE,
                    hozAlign: 'center'
                },
                {
                    title: 'Дата утверждения',
                    field: TABR_FIELD.APPROVAL_DATE,
                    hozAlign: 'center',
                    width: 130,
                    resizable: false,
                    formatter: 'stdDate'
                },
                {
                    title: 'Утверждена',
                    field: TABR_FIELD.APPROVED_BY,
                    hozAlign: 'center'
                },
                {
                    title: 'Комментарий',
                    field: TABR_FIELD.COMMENT,
                    variableHeight: true,
                    minWidth: 300,
                    formatter: 'textarea',
                    headerSort: false
                }
            ],
            rowClick: (e, row) => {
                const data = row.getData();
                table.deselectRow();
                row.select();
                showInfo(data.id);
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
                    action: () => editPurchasePlan(id)
                });
                if (data.canApprove) {
                    menu.push({
                        label: '<i class="check double icon blue"></i>Утвердить',
                        action: () => purchaseApprove(id, true)
                    });
                } else {
                    menu.push({
                        label: '<i class="check double icon blue"></i>Отменить утверждение',
                        action: () => purchaseApprove(id, false)
                    });
                }
                menu.push({
                    label: '<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>',
                    action: () => deletePurchasePlan(id)
                });
                return menu;
            }
        });

        // Скролл до строки и ее выбор по id
        function rowScrollSelect(id) {
            table.selectRow(id);
            table.scrollToRow(id, 'middle', false);
        }

        // Редактирование/добавление
        function editPurchasePlan(id) {
            $.modalWindow({
                loadURL: VIEW_PATH.LIST_EDIT,
                loadData: { id: id },
                submitURL: ACTION_PATH.LIST_EDIT_SAVE,
                submitAsJson: true,
                onSubmitSuccess: resp => {
                    if (id) {
                        table.setPage(table.getPage()).then(() => rowScrollSelect(id));
                    } else {
                        const id = resp.attributes.id;
                        if (id) {
                            filterData = {};
                            table.setSort(TABR_SORT_ID_DESC);
                            table.setPage(1).then(() => rowScrollSelect(id));
                        }
                    }
                }
            });
        }

        // Удаление
        function deletePurchasePlan(id) {
            confirmDialog({
                title: 'Удаление закупочной ведомости',
                message: 'Вы действительно хотите удалить закупочную ведомость?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: ACTION_PATH.LIST_DELETE + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setPage(table.getPage()))
            });
        }

        // Отображение информации о закупочной ведомости
        function showInfo(id) {
            $subContent.html('');
            // TODO Доделать как будет известна информация к выводу
/*            $subBlock.show();
            setTimeout(() => table.scrollToRow(id, 'middle', false), 200);
            $.get({
                url: VIEW_PATH.LIST_INFO,
                data: { id: id }
            }).done(html => $subContent.html(html));*/
        }

        // Утверждение/отмена утверждения закупочной ведомости
        function purchaseApprove(id, toApprove) {
            confirmDialog({
                title: toApprove ? 'Утверждение закупочной ведомости' : 'Отмена утверждения закупочной ведомости',
                message: toApprove ? 'Вы действительно хотите утвердить закупочную ведомость?' : 'Вы действительно хотите отменить утверждение закупочной ведомости?',
                onAccept: () => $.post({
                    url: ACTION_PATH.LIST_APPROVE,
                    data: { id: id, toApprove: toApprove },
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setPage(table.getPage()).then(() => rowScrollSelect(id)))
            })
        }

        // Скрытие области вспомогательного контейнера
        $btnCloseSubBlock.on({
            'click': () => {
                $subContent.html('');
                $subBlock.hide();
            }
        });

        // Фильтр
        $.modalFilter({
            url: VIEW_PATH.LIST_FILTER,
            button: $btnFilter,
            filterData: () => filterData,
            onApply: data => {
                filterData = data;
                table.setData();
            }
        });

        // Добавление
        $btnAdd.on({
            'click': () => editPurchasePlan()
        });
    })
</script>