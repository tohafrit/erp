<div class="detail_documentation__table-wrap">
    <i class="icon upload link blue detail_documentation__btn-upload" title="<fmt:message key="label.button.upload"/>"></i>
    <i class="ui icon blue layer group dropdown detail_documentation__btn-generate" title="<fmt:message key="label.button.generate"/>">
        <div class="menu">
            <div class="item" data-type="1">Текст договора</div>
            <div class="item" data-type="2">Ведомость исполнения</div>
            <div class="item" data-type="3">Сопроводительное письмо Заказчику</div>
            <div class="item" data-type="4">Протокол согласования разногласий</div>
            <div class="item" data-type="5">Письмо Заказчику с протоколом согласования разногласий</div>
            <div class="item" data-type="6">Простое письмо</div>
            <div class="item" data-type="7">Письмо Заказчику с документами (протокол цены)</div>
            <div class="item" data-type="8">Письмо Заказчику с документами (акт)</div>
            <div class="item" data-type="9">Сопроводительное письмо в ПЗ</div>
            <div class="item" data-type="10">Письмо в ПЗ (копия)</div>
            <div class="item" data-type="11">Письмо в ПЗ с протоколом согласования разногласий</div>
            <div class="item" data-type="12">Письмо в ПЗ с протоколом цены</div>
            <div class="item" data-type="13">Служебная записка в бухгалтерию (оригинал)</div>
            <div class="item" data-type="14">Служебная записка в бухгалтерию (оригинал акта)</div>
            <div class="item" data-type="15">Служебная записка в бухгалтерию (запрос на акт)</div>
            <div class="item" data-type="16">Акт приемки-передачи продукции</div>
   <%--            <div class="item" data-type="18">Разрешение на отгрузку</div>
            <div class="item" data-type="19">Служебная записка технологам (запрос на трудоемкость)</div>
            <div class="item" data-type="20">Служебная о дополнительной гарантии</div>--%>
            <div class="item" data-type="21">Сопроводительное письмо к счету фактуре</div>
            <div class="item" data-type="22">Письмо о переносе сроков</div>
            <div class="item" data-type="23">Письмо на оплату аванса</div>
           <div class="item" data-type="24">Письмо на окончательную оплату</div>
            <div class="item" data-type="25">Письмо неправильные платежные поручения</div>
           <div class="item" data-type="28">Распоряжение</div>
<%--             <div class="item" data-type="26">Письмо на склад</div>--%>
            <div class="item" data-type="27">Служебная записка</div>
        </div>
    </i>
    <div class ='detail__header_title'>Загруженные документы</div>
    <div class="detail_documentation__table table-sm table-striped"></div>
    <div class ='detail__header_title'>Сформированные документы</div>
    <div class="detail_documentation-formed__table table-sm table-striped"></div>
</div>

<script>
    $(() => {
        const sectionId = '${sectionId}';
        const $btnUpload = $('i.detail_documentation__btn-upload');

        const table = new Tabulator('div.detail_documentation__table', {
            selectable: 1,
            pagination: 'remote',
            ajaxURL: ACTION_PATH.DETAIL_DOCUMENTATION_LOAD,
            ajaxRequesting: (url, params) => {
                params.sectionId = sectionId;
            },
            ajaxSorting: true,
            height: 'calc(50vh - 185px)',
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                TABR_COL_ID,
                { title: 'Наименование', field: TABR_FIELD.NAME },
                {
                    title: '',
                    field: TABR_FIELD.FILE_HASH,
                    headerSort: false,
                    formatter: 'fileLink'
                },
                { title: 'Комментарий', field: TABR_FIELD.COMMENT, formatter: 'textarea' }
            ],
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
                    action: () => editDoc(id)
                });
                menu.push({
                    label: '<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>',
                    action: () => deleteDoc(id)
                });
                return menu;
            }
        });

        new Tabulator('div.detail_documentation-formed__table', {
            selectable: 1,
            pagination: 'remote',
            ajaxURL: ACTION_PATH.DETAIL_DOCUMENTATION_FORMED_LOAD,
            ajaxRequesting: (url, params) => {
                params.sectionId = sectionId;
            },
            ajaxSorting: true,
            height: 'calc(50vh - 185px)',
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                TABR_COL_ID,
                { title: 'Наименование', field: TABR_FIELD.NAME },
                {
                    title: '',
                    field: TABR_FIELD.ID,
                    headerSort: false,
                    formatter: 'fileLink',
                    formatterParams: { href: '/contract-documentation-formed/download/' }
                },
                { title: 'Комментарий', field: TABR_FIELD.COMMENT, formatter: 'textarea' }
            ],
            rowContext: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowContextMenu: row => {
                const menu = [];
                const data = row.getData();
                const id = data.id;
                menu.push({
                    label: '<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>',
                    action: () => deleteFormedDoc(id)
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
        function editDoc(id) {
            $.modalWindow({
                loadURL: VIEW_PATH.DETAIL_DETAIL_DOCUMENTATION_EDIT,
                loadData: { id: id, sectionId: sectionId },
                submitURL: ACTION_PATH.DETAIL_DOCUMENTATION_EDIT_SAVE,
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
                }
            });
        }
        // Добавление
        $btnUpload.on({
            'click': () => editDoc()
        });

        const $btnGenerateDocuments = $('i.detail_documentation__btn-generate');
        const $btnGenerateDocumentItem = $btnGenerateDocuments.find('div.item');

        // Кнопка генерации документов
        $btnGenerateDocuments.dropdown({
            action: 'hide'
        });
        $btnGenerateDocumentItem.on({
            'click': e => {
                const type = ($(e.currentTarget).data("type"));
                if (type === 1 ||
                    type === 3 ||
                    type === 23 ||
                    type === 24 ||
                    type === 25 ||
                    type === 28) {
                    $.modalWindow({
                        loadURL: VIEW_PATH.DETAIL_DOCUMENTATION_FORMED_EDIT,
                        loadData: { sectionId: sectionId, documentType: $(e.currentTarget).data("type") }
                    });
                } else {
                    $.post({
                        url: ACTION_PATH.DETAIL_DOCUMENTATION_FORMED_SAVE,
                        data: {
                            sectionId: sectionId,
                            documentType: type
                        },
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => $('.detail__menu_documentation').trigger('click'));
                }
            }
        });

        // Удаление
        function deleteDoc(id) {
            confirmDialog({
                title: 'Удаление документации',
                message: 'Вы действительно хотите удалить документацию?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: ACTION_PATH.DETAIL_DOCUMENTATION_DELETE + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setPage(table.getPage()))
            });
        }

        // Удаление сгенерированного документа
        function deleteFormedDoc(id) {
            confirmDialog({
                title: 'Удаление документации',
                message: 'Вы действительно хотите удалить документацию?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: ACTION_PATH.DETAIL_DOCUMENTATION_FORMED_DELETE + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => $('.detail__menu_documentation').trigger('click'))
            });
        }
    });
</script>