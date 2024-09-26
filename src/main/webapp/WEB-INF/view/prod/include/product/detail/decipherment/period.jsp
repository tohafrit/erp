<div class="ui modal detail_decipherment_period__modal">
    <div class="header">Выбор периода</div>
    <div class="content">
        <div class="detail_decipherment_period__buttons">
            <i class="icon add link blue detail_decipherment_period__btn-add" title="<fmt:message key="label.button.add"/>"></i>
        </div>
        <div class="detail_decipherment_period__table table-sm table-striped"></div>
    </div>
    <div class="actions">
        <div class="ui small button detail_decipherment_period__btn-select disabled">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.select"/>
        </div>
    </div>
</div>

<script>
    $(() => {
        const productId = ${productId};
        const periodId = '${periodId}';
        const $modal = $('div.detail_decipherment_period__modal');
        const $btnSelect = $('div.detail_decipherment_period__btn-select');
        const $btnAdd = $('i.detail_decipherment_period__btn-add');

        const table = new Tabulator('div.detail_decipherment_period__table', {
            selectable: 1,
            pagination: 'remote',
            initialSort: [{ column: TABR_FIELD.PLAN_START_DATE, dir: SORT_DIR_DESC }],
            ajaxURL: ACTION_PATH.DETAIL_DECIPHERMENT_PERIOD_LOAD,
            ajaxRequesting: (url, params) => {
                params.productId = productId;
            },
            ajaxSorting: true,
            height: 'calc(100vh * 0.7)',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                TABR_COL_ID,
                {
                    title: 'Планируемый',
                    headerHozAlign: 'center',
                    columns: [
                        { title: 'Период', field: TABR_FIELD.PLAN_NAME },
                        {
                            title: 'Дата начала',
                            field: TABR_FIELD.PLAN_START_DATE,
                            formatter: 'stdDate'
                        },
                        {
                            title: 'Комментарий',
                            field: TABR_FIELD.PLAN_COMMENT,
                            headerSort: false,
                            variableHeight: true,
                            minWidth: 150,
                            formatter: 'textarea'
                        }
                    ],
                },
                {
                    title: 'Отчетный',
                    headerHozAlign: 'center',
                    columns: [
                        { title: 'Период', field: TABR_FIELD.REPORT_NAME },
                        {
                            title: 'Дата начала',
                            field: TABR_FIELD.REPORT_START_DATE,
                            formatter: 'stdDate'
                        },
                        {
                            title: 'Комментарий',
                            field: TABR_FIELD.REPORT_COMMENT,
                            headerSort: false,
                            variableHeight: true,
                            minWidth: 150,
                            formatter: 'textarea'
                        }
                    ],
                }
            ],
            rowSelected: () => $btnSelect.toggleClass('disabled', !table.getSelectedRows().length),
            rowDeselected: () => $btnSelect.toggleClass('disabled', !table.getSelectedRows().length),
            rowContext: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowDblClick: (e, row) => {
                table.deselectRow();
                row.select();
                $btnSelect.trigger('click');
            },
            rowContextMenu: row => {
                const menu = [];
                const data = row.getData();
                const id = data.id;
                menu.push({
                    label: `<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>`,
                    action: () => editRecord(id)
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
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
                loadURL: VIEW_PATH.DETAIL_DECIPHERMENT_PERIOD_EDIT,
                loadData: { id: id, productId: productId },
                submitAsJson: true,
                submitURL: ACTION_PATH.DETAIL_DECIPHERMENT_PERIOD_EDIT_SAVE,
                onSubmitSuccess: resp => {
                    if (id) {
                        table.setPage(table.getPage()).then(() => rowScrollSelect(id));
                    } else {
                        const id = resp.attributes.id;
                        if (id) {
                            table.setSort(TABR_SORT_ID_DESC);
                            table.setPage(1).then(() => rowScrollSelect(id));
                        }
                    }
                    if (periodId) page.show('/detail/' + productId + '/decipherment?periodId=' + periodId, undefined, true, false);
                }
            });
        }

        // Удаление
        function deleteRecord(id) {
            confirmDialog({
                title: 'Удаление периода',
                message: 'Вы действительно хотите удалить период?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: ACTION_PATH.DETAIL_DECIPHERMENT_PERIOD_DELETE + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => {
                    if (id == periodId) page('/detail/' + productId + '/decipherment');
                    table.setPage(table.getPage());
                })
            });
        }

        // Добавление
        $btnAdd.on({
            'click': () => editRecord()
        });

        // Выбор периода
        $btnSelect.on({
            'click': () => {
                const data = table.getSelectedData();
                if (data.length === 1) {
                    $modal.modal('hide');
                    page('/detail/' + productId + '/decipherment?periodId=' + data[0].id);
                }
            }
        });
    });
</script>