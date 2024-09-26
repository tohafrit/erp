<div class="detail__container">
    <div class="ui button basic detail__btn-list" title="Вернуться к списку">
        <i class="long arrow alternate left icon"></i>
        к списку
    </div>
    <div class="detail__title">
        <h3>${title}</h3>
    </div>
    <div class="detail__buttons">
        <i class="icon filter link blue detail__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        <i class="icon add link blue detail__btn-add" title="<fmt:message key="label.button.add"/>"></i>
    </div>
    <div class="detail__tables-block">
        <div class="detail__table table-sm table-striped"></div>
        <div class="detail__sub-block">
            <i class="close link blue icon detail__btn-close-sub-block"></i>
            <div class="detail__sub-block-content"></div>
        </div>
    </div>

    <script>
        $(() => {
            const id = '${id}';
            const tableSel = 'div.detail__table';
            const $table = $(tableSel);
            const $btnList = $('div.detail__btn-list');
            const $btnFilter = $('i.detail__btn-filter');
            const $btnAdd = $('i.detail__btn-add');
            const $subBlock = $('div.detail__sub-block');
            const $subContent = $('div.detail__sub-block-content');
            const $btnCloseSubBlock = $('i.detail__btn-close-sub-block');
            const $listTable = $('div.list__table');
            // Параметры фильтра таблицы
            const tableData = tableDataFromUrlQuery(window.location.search);
            let filterData = tableData.filterData;

            // Возврат к общему списку
            $btnList.on({
                'click': () => page(ROUTE.list())
            });

            let notInitLoad = false;
            const table = new Tabulator(tableSel, {
                pagination: 'remote',
                paginationInitialPage: tableData.page,
                paginationSize: tableData.size,
                initialSort: tableData.sort.length ? tableData.sort : [{ column: TABR_FIELD.CREATE_DATE, dir: SORT_DIR_DESC }],
                ajaxURL: ACTION_PATH.DETAIL_LOAD,
                ajaxRequesting: (url, params) => {
                    params.labourIntensityId = id;
                    params.filterData = JSON.stringify(filterData);
                    if (notInitLoad) {
                        const query = urlQueryFromTableParams(window.location.search, params);
                        sessionStorage.setItem(S_STORAGE.DETAIL_QUERY, query);
                        page.show(ROUTE.detail(id, query), undefined, false);
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
                    { title: 'Изделие', field: TABR_FIELD.PRODUCT_NAME },
                    { title: 'ТУ изделия', field: TABR_FIELD.DECIMAL_NUMBER },
                    {
                        title: 'Общая трудоемкость',
                        field: TABR_FIELD.TOTAL_LABOUR_INTENSITY,
                        hozAlign: 'center',
                        formatter: 'money',
                        formatterParams: { decimal: ',', thousand: ' ' }
                    },
                    {
                        title: 'Дата добавления',
                        field: TABR_FIELD.CREATE_DATE,
                        hozAlign: 'center',
                        width: 150,
                        resizable: false,
                        formatter: 'stdDate'
                    },
                    { title: 'Добавил', field: TABR_FIELD.CREATED_BY, hozAlign: 'center', headerSort: false },
                    {
                        title: 'Дата утверждения',
                        field: TABR_FIELD.APPROVAL_DATE,
                        hozAlign: 'center',
                        width: 160,
                        resizable: false,
                        formatter: 'stdDate'
                    },
                    { title: 'Утвердил', field: TABR_FIELD.APPROVED_BY, hozAlign: 'center', headerSort: false },
                    {
                        title: 'Комментарий',
                        headerSort: false,
                        field: TABR_FIELD.COMMENT,
                        variableHeight: true,
                        minWidth: 300,
                        formatter: 'textarea'
                    }
                ],
                rowClick: (e, row) => {
                    const data = row.getData();
                    table.deselectRow();
                    row.select();
                    showWorkType(data.id);
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
                        action: () => editRecord(id)
                    });
                    if (data.canApprove) {
                        menu.push({
                            label: '<i class="check double icon blue"></i>Утвердить',
                            action: () => approveRecord(id, true)
                        });
                    } else {
                        menu.push({
                            label: '<i class="check double icon blue"></i>Отменить утверждение',
                            action: () => approveRecord(id, false)
                        });
                    }
                    menu.push({
                        label: '<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>',
                        action: () => deleteRecord(id)
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
            function editRecord(id) {
                $.modalWindow({
                    loadURL: VIEW_PATH.DETAIL_EDIT,
                    loadData: { id: id },
                    submitURL: ACTION_PATH.DETAIL_EDIT_SAVE,
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
            function deleteRecord(id) {
                confirmDialog({
                    title: 'Удаление трудоемкости',
                    message: 'Вы действительно хотите удалить трудоемкость?',
                    onAccept: () => $.ajax({
                        method: 'DELETE',
                        url: ACTION_PATH.DETAIL_DELETE + id,
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => table.setPage(table.getPage()))
                });
            }

            // Утверждение/снятие утверждения
            function approveRecord(id, toApprove) {
                confirmDialog({
                    title: toApprove ? 'Утверждение трудоемкости' : 'Отмена утверждения трудоемкости',
                    message: toApprove ? 'Вы действительно хотите утвердить трудоемкость?' : 'Вы действительно хотите отменить утверждение трудоемкости?',
                    onAccept: () => $.post({
                        url: ACTION_PATH.DETAIL_APPROVE,
                        data: { id: id, toApprove: toApprove },
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => {
                        $table.trigger('forceTimerUpdate');
                        $listTable.trigger('forceTimerUpdate');
                    })
                })
            }

            // Отображение работ
            let workTypeReqId = 0;
            function showWorkType(id) {
                $subContent.html('');
                $subBlock.show();
                setTimeout(() => table.scrollToRow(id, 'middle', false), 200);
                const reqId = ++workTypeReqId;
                $.get({
                    url: VIEW_PATH.DETAIL_WORK_TYPE,
                    data: { id: id }
                }).done(html => workTypeReqId === reqId ? $subContent.html(html) : null);
            }

            // Скрытие области вспомогательного контейнера
            $btnCloseSubBlock.on({
                'click': () => $subBlock.hide()
            });

            // Фильтр
            $.modalFilter({
                url: VIEW_PATH.DETAIL_FILTER,
                button: $btnFilter,
                filterData: () => filterData,
                onApply: data => {
                    filterData = data;
                    table.setData();
                }
            });

            // Добавление
            $btnAdd.on({
                'click': () => $.modalWindow({
                    loadURL: VIEW_PATH.DETAIL_ADD,
                    loadData: { id: id }
                })
            });

            // Обновление таблицы
            tableTimerUpdate({
                selector: tableSel,
                url: ACTION_PATH.DETAIL_LOAD,
                params: { labourIntensityId: id },
                filterData: () => filterData
            });
        })
    </script>
</div>