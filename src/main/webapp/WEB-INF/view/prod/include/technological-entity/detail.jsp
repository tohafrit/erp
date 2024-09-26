<div class="detail__container">
    <div class="ui button basic detail__btn-technological-entity-list" title="Вернуться к списку">
        <i class="long arrow alternate left icon"></i>
        к списку
    </div>
    <div class="detail__title">
        <h3>Технологическая документация ${entityName}</h3>
    </div>
    <div class="detail__buttons">
        <i class="icon add link blue detail__btn-add" title="<fmt:message key="label.button.add"/>"></i>
        <i class="icon users cog link blue labour__btn-show" title="Таблица трудоемкости"></i>
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
            const entityId = '${entityId}';
            const $btnAdd = $('i.detail__btn-add');
            const $btnEntityList = $('div.detail__btn-technological-entity-list');
            const $subBlock = $('div.detail__sub-block');
            const $subContent = $('div.detail__sub-block-content');
            const $btnCloseSubBlock = $('i.detail__btn-close-sub-block');
            const $btnLabour = $('i.labour__btn-show');

            // Возврат к списку запусков
            $btnEntityList.on({
                'click': () => page(ROUTE.list())
            });

            const table = new Tabulator('div.detail__table', {
                ajaxURL: ACTION_PATH.DETAIL_LOAD,
                movableRows: true,
                ajaxRequesting: (url, params) => params.entityId = entityId,
                headerSort: false,
                height: '100%',
                layout: 'fitDataStretch',
                columns: [
                    {
                        rowHandle: true,
                        formatter: 'handle',
                        frozen: true,
                        width: 30,
                        minWidth: 30
                    },
                    TABR_COL_ID,
                    { title: 'Символ', field: TABR_FIELD.SYMBOL },
                    { title: 'Код участка', field: TABR_FIELD.AREA_CODE },
                    { title: 'Номер операции', field: TABR_FIELD.NUMBER },
                    { title: 'Операция', field: TABR_FIELD.WORK_TYPE },
                    {
                        title: 'Комментарий к операции',
                        field: TABR_FIELD.DESCRIPTION,
                        variableHeight: true,
                        minWidth: 300,
                        formatter: 'textarea'
                    },
                    { title: 'ИОТ', field: TABR_FIELD.LABOR_PROTECTION_INSTRUCTION }
                ],
                rowClick: (e, row) => {
                    const data = row.getData();
                    showSub(data.id);
                    table.deselectRow();
                    row.select();
                },
                rowContext: (e, row) => {
                    table.deselectRow();
                    row.select();
                },
                rowContextMenu: row => {
                    const data = row.getData();
                    const id = data.id;
                    const menu = [];
                    menu.push({
                        label: '<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>',
                        action: () => editRecord(id)
                    });
                    menu.push({
                        label: '<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>',
                        action: () => deleteRecord(id)
                    });
                    return menu;
                },
                rowMoved: () => $.post({
                    url: ACTION_PATH.DETAIL_UPDATE_SORT,
                    data: { idList: JSON.stringify(table.getData().map(el => el.id)) }
                })
            });

            // Скрытие области доп. контейнера
            $btnCloseSubBlock.on({
                'click': () => $subBlock.hide()
            });

            // Отображение доп. контейнера
            function showSub(id) {
                $subContent.html('');
                $subBlock.show();
                setTimeout(() => table.scrollToRow(id, 'middle', false), 200);
                $.get({
                    url: VIEW_PATH.DETAIL_CHILD,
                    data: { id: id }
                }).done(html => $subContent.html(html));
            }

            // Редактирование/добавление
            function editRecord(id) {
                $.modalWindow({
                    loadURL: VIEW_PATH.DETAIL_EDIT,
                    loadData: { entityId: entityId, id: id },
                    submitURL: ACTION_PATH.DETAIL_EDIT_SAVE,
                    submitAsJson: true,
                    onSubmitSuccess: () => table.setData()
                });
            }

            // Удаление
            function deleteRecord(id) {
                confirmDialog({
                    title: 'Удаление операции',
                    message: 'Вы действительно хотите удалить операцию?',
                    onAccept: () => $.ajax({
                        method: 'DELETE',
                        url: ACTION_PATH.DETAIL_DELETE + id,
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => table.setPage(table.getPage()))
                });
            }

            // Добавление
            $btnAdd.on({
                'click': () => editRecord()
            });

            // Показать трудоемкость
            $btnLabour.on({
                'click': () => $.modalWindow({
                    loadURL: VIEW_PATH.DETAIL_LABOUR,
                    loadData: { entityId: entityId }
                })
            });
        })
    </script>
</div>