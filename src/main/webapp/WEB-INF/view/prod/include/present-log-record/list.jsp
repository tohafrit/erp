<div class="list__header">
    <h1 class="list__header_title"><fmt:message key="presentLogRecord.title"/></h1>
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
        const $btnFilter = $('i.list__btn-filter');
        const $btnAdd = $('i.list__btn-add');
        const $subBlock = $('div.list__sub-block');
        const $subContent = $('div.list__sub-block-content');
        const $btnCloseSubBlock = $('i.list__btn-close-sub-block');

        // Параметры фильтра таблицы
        const tableData = tableDataFromUrlQuery(window.location.search);
        let filterData = tableData.filterData;

        // Таблица предъявлений
        let notInitLoad = false;
        const table = new Tabulator('div.list__table', {
            pagination: 'remote',
            paginationInitialPage: tableData.page,
            paginationSize: tableData.size,
            initialSort: tableData.sort.length ? tableData.sort : [{ column: TABR_FIELD.REGISTRATION_DATE, dir: SORT_DIR_DESC }],
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
                { title: 'Номер', field: TABR_FIELD.PRESENT_LOG_RECORD_NUMBER },
                {
                    title: 'Дата регистрации',
                    field: TABR_FIELD.REGISTRATION_DATE,
                    hozAlign: 'center',
                    formatter: 'stdDate'
                },
                { title: 'Письмо', field: TABR_FIELD.LETTER_NUMBER, headerSort: false },
                { title: 'Договор', field: TABR_FIELD.CONTRACT_NUMBER, headerSort: false },
                {
                    title: 'Пункт ведомости поставки',
                    field: TABR_FIELD.LOT_GROUP_NUMBER,
                    headerSort: false,
                    hozAlign: 'center'
                },
                {
                    title: 'Заказчик',
                    field: TABR_FIELD.CUSTOMER,
                    headerSort: false,
                    variableHeight: true,
                    minWidth: 100,
                    width: 200,
                    formatter: 'textarea'
                },
                {
                    title: 'Изделие',
                    field: TABR_FIELD.PRODUCT_NAME,
                    headerSort: false,
                    variableHeight: true,
                    minWidth: 150,
                    width: 250,
                    formatter: 'textarea'
                },
                {
                    title: 'Кол-во',
                    field: TABR_FIELD.AMOUNT,
                    hozAlign: 'center',
                    headerSort: false
                },
                {
                    title: 'Тип приемки',
                    field: TABR_FIELD.ACCEPT_TYPE,
                    hozAlign: 'center',
                    headerSort: false
                },
                {
                    title: 'Спец. проверка',
                    field: TABR_FIELD.SPECIAL_TEST_TYPE,
                    hozAlign: 'center',
                    headerSort: false
                },
                {
                    title: 'Акт ПИ',
                    field: TABR_FIELD.EXAM_ACT,
                    headerSort: false,
                    variableHeight: true,
                    minWidth: 100,
                    width: 150,
                    formatter: 'textarea'
                },
                {
                    title: 'ТУ семейства',
                    field: TABR_FIELD.FAMILY_DECIMAL_NUMBER,
                    headerSort: false,
                    variableHeight: true,
                    minWidth: 50,
                    width: 150,
                    formatter: 'textarea'
                },
                { title: 'Суффикс изделия', field: TABR_FIELD.SUFFIX, headerSort: false },
                {
                    title: 'Упаковано',
                    field: TABR_FIELD.WRAPPING_DATE,
                    hozAlign: 'center',
                    formatter: 'stdDate'
                },
                { title: 'Заявление о соответствии', field: TABR_FIELD.CONFORMITY_STATEMENT, headerSort: false },
                {
                    title: 'Комментарий',
                    field: TABR_FIELD.COMMENT,
                    headerSort: false,
                    variableHeight: true,
                    minWidth: 200,
                    width: 300,
                    formatter: 'textarea'
                }
            ],
            rowClick: (e, row) => {
                const data = row.getData();
                table.deselectRow();
                row.select();
                showLogRecordInfo(data.id);
            },
            rowContext: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowContextMenu: row => {
                const menu = [];
                const data = row.getData();
                const id = data.id
                menu.push({
                    label: `<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>`,
                    action: () => editPresentLogRecord(id)
                });
                menu.push({
                    label: '<i class="envelope outline icon blue"></i>Открыть письмо ПЭО',
                    action: () => window.open(window.location.origin + '/prod/production-shipment-letter/list?selectedId=' + data.letterId)
                });
                if (data.canPack) {
                    menu.push({
                        label: '<i class="box open icon blue"></i>Упаковать изделия',
                        action: () => packProducts(id, true)
                    });
                } else {
                    menu.push({
                        label: '<i class="box open icon blue"></i>Отменить упаковку изделий',
                        action: () => packProducts(id, false)
                    });
                }
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => deletePresentLogRecord(id)
                });
                if (data.canStatementWord) {
                    menu.push({
                        label: `<i class="file word outline icon blue"></i>Выгрузить заявление о соответствии`,
                        action: () => window.open('/api/action/prod/present-log-record/list/download-conformity-statement-word?logRecordId=' + id, '_blank')
                    });
                }
                menu.push({
                    label: `<i class="file word outline icon blue"></i>Сформировать пакет документов на изделие`,
                    action: () => createPackageDocuments(id)
                });
                menu.push({
                    label: '<i class="file word outline icon blue"></i>Выгрузить паспорта изделий',
                    action: () => window.open('/present-log-template-documentation-formed/download/' + id, '_blank')
                });
                return menu;
            }
        });

        // Скролл до строки и ее выбор по id
        function rowScrollSelect(id) {
            table.selectRow(id);
            table.scrollToRow(id, 'middle', false);
        }

        // Функция добавления/редактирования предъявлений
        function editPresentLogRecord(id) {
            $.modalWindow({
                loadURL: VIEW_PATH.LIST_EDIT,
                loadData: { id: id },
                submitURL: ACTION_PATH.LIST_EDIT_SAVE,
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

        // Удаление предъявления
        function deletePresentLogRecord(id) {
            confirmDialog({
                title: 'Удаление предъявления',
                message: 'Вы действительно хотите удалить предъявление?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: ACTION_PATH.LIST_DELETE + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setPage(table.getPage()))
            });
        }

        // Отображение информации о предъявлении
        function showLogRecordInfo(id) {
            $subContent.html('');
            $subBlock.show();
            setTimeout(() => table.scrollToRow(id, 'middle', false), 200);
            $.get({
                url: VIEW_PATH.LIST_LOG_RECORD_INFO,
                data: { id: id }
            }).done(html => $subContent.html(html));
        }

        // Функция создания пакета документов по шаблону
        function createPackageDocuments(id) {
            $.modalWindow({
                loadURL: VIEW_PATH.LIST_CREATE_PACKAGE,
                loadData: { id: id }
            });
        }

        // Упаковать изделия/ отменить упаковку изделий
        function packProducts(id, toPack) {
            confirmDialog({
                title: toPack ? 'Упаковка изделий' : 'Отмена упаковки изделий',
                message: toPack ? 'Вы действительно хотите упаковать изделия?' : 'Вы действительно хотите отменить упаковку изделий?',
                onAccept: () => $.post({
                    url: ACTION_PATH.LIST_PACK,
                    data: { id: id, toPack: toPack },
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setPage(table.getPage()).then(() => rowScrollSelect(id)))
            })
        }

        // Скрытие области вспомогательного контейнера
        $btnCloseSubBlock.on({
            'click': () => $subBlock.hide()
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

        // Кнопка добавления предъявления
        $btnAdd.on({
            'click': () => editPresentLogRecord()
        });

        // Resize вспомогательного контейнера
        $subBlock.resizable({
            autoHide: true,
            handles: 'n',
            ghost: true,
            stop: () => $subBlock.css({
                'width': '100%',
                'top': 0
            })
        });
    })
</script>