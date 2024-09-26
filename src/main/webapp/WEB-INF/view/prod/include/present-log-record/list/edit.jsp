<div class="ui modal list_edit__main">
    <div class="header">
        ${empty form.id ? 'Добавление предъявления' : 'Редактирование предъявления'}
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui form">
            <form:hidden path="id"/>
            <form:hidden path="groupSerialNumber"/>
            <form:hidden path="maxSerialNumberQuantity"/>
            <form:hidden path="allotmentId"/>
            <form:hidden path="productId"/>
            <form:hidden path="conformityStatement"/>
            <form:hidden path="managerId"/>
            <div class="column field">
                <div class="four fields">
                    <div class="field inline">
                        <label>Номер</label>
                        ${form.number}
                        <form:hidden path="number"/>
                    </div>
                    <div class="field">
                        <label>Дата регистрации</label>
                        <div class="ui calendar">
                            <div class="ui input left icon">
                                <i class="calendar icon"></i>
                                <form:input cssClass="std-date" path="registrationDate"/>
                            </div>
                        </div>
                        <div class="ui compact message error" data-field="registrationDate"></div>
                    </div>
                    <div class="field">
                        <label>Тип приемки</label>
                        <form:select cssClass="ui dropdown label std-select" path="acceptType">
                            <c:forEach items="${acceptTypeList}" var="acceptType">
                                <form:option value="${acceptType}">${acceptType.code}</form:option>
                            </c:forEach>
                        </form:select>
                    </div>
                    <div class="field">
                        <label>Спец. проверка</label>
                        <form:select cssClass="ui dropdown label std-select" path="specialTestType">
                            <c:forEach items="${specialTestTypeList}" var="specialTestType">
                                <form:option value="${specialTestType}"><fmt:message key="${specialTestType.property}"/></form:option>
                            </c:forEach>
                        </form:select>
                    </div>
                </div>
            </div>
            <div class="column field">
                <div class="two fields">
                    <div class="field">
                        <label>Акт ПИ</label>
                        <form:input path="examAct" cssClass="list_edit__field-examAct"/>
                        <div class="ui compact message error" data-field="examAct"></div>
                    </div>
                    <div class="field">
                        <label>Дата составления Акта ПИ</label>
                        <div class="ui calendar">
                            <div class="ui input left icon">
                                <i class="calendar icon"></i>
                                <form:input cssClass="std-date" path="examActDate"/>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="field">
                <label>ТУ семейства</label>
                <form:input path="familyDecimalNumber" cssClass="list_edit__field-familyDecimalNumber"/>
                <div class="ui compact message error" data-field="familyDecimalNumber"></div>
            </div>
            <div class="field">
                <label>Суффикс изделия</label>
                <form:input path="suffix" cssClass="list_edit__field-suffix"/>
                <div class="ui compact message error" data-field="suffix"></div>
            </div>
            <div class="field inline">
                <label>Заявление о соответствии</label>
                <i class="icon add link blue list_edit__btn-add-conformity-statement" title="<fmt:message key="label.button.add"/>"></i>
                <div class="list_edit_conformity-statement__table table-sm table-striped"></div>
            </div>
            <div class="field">
                <label>Комментарий</label>
                <div class="ui textarea">
                    <form:textarea path="comment" rows="2"/>
                </div>
            </div>
            <div class="field required inline">
                <label>Пункт письма</label>
                <i class="icon add link blue list_edit__btn-add" title="<fmt:message key="label.button.add"/>"></i>
                <i class="icon sort numeric down link blue list_edit__btn-add-select-number" title="Выбрать серийный номер"></i>
                <i class="icon add link blue list_edit__btn-add-serial-number" title="Добавить серийный номер в ручном режиме"></i>
                <i class="icon trash link blue list_edit__btn-delete" title="<fmt:message key="label.button.delete"/>"></i>
                <input class="list_edit__max-serial-number-quantity" type="hidden" name="maxSerialNumberQuantity" value="0">
                <div class="list_edit__table table-sm table-striped"></div>
                <div class="ui compact message error" data-field="groupSerialNumber"></div>
            </div>
        </form:form>
    </div>
    <div class="actions">
        <button class="ui small button" type="submit">
            <i class="icon blue save"></i>
            <fmt:message key="label.button.save"/>
        </button>
    </div>
</div>

<script>
    $(() => {
        const logRecordId = '${form.id}';
        const isFormId = '${not empty form.id}' === 'true';
        //
        const $modal = $('div.list_edit__main');
        const $btnAdd = $('i.list_edit__btn-add');
        const $btnAddConformityStatement = $('i.list_edit__btn-add-conformity-statement');
        const $btnAddSerialNumber = $('i.list_edit__btn-add-serial-number');
        const $btnSelectSerialNumber = $('i.list_edit__btn-add-select-number');
        const $maxSerialNumberQuantityInput = $modal.find('input[name="maxSerialNumberQuantity"]');
        const $groupSerialNumber = $modal.find('input[name="groupSerialNumber"]');
        const $conformityStatement = $modal.find('input[name="conformityStatement"]');
        const $btnDelete = $('i.list_edit__btn-delete');
        const $productId = $modal.find('input[name="productId"]');

        $btnAdd.toggle(${empty form.id});
        $btnAddSerialNumber.toggle(${form.maxSerialNumberQuantity ne null});
        $btnSelectSerialNumber.toggle(${form.allotmentId ne null});
        $btnAddConformityStatement.toggle(${form.conformityStatement eq "[]"});

        // Кастомный инпут редактирования серийных номеров
        const serialNumberEditor = (cell, onRendered, success) => {
            const $editor = $(document.createElement('input'));
            const editor = $editor.get(0);
            $editor.css('padding', '3px');
            $editor.attr('type', 'text');
            $editor.val(cell.getValue());
            $editor.inputmask({
                placeholder: '',
                regex: '[0-9]{0,12}'
            });
            onRendered(() => {
                $editor.trigger('focus');
                editor.setSelectionRange(0, 0);
            });
            const onSuccess = () => success($editor.val() === '' ? '' : $editor.val().toString());
            $editor.on({
                'change': () => onSuccess(),
                'blur': () => onSuccess()
            });
            return editor;
        };

        // Добавление/редактирование письма
        $btnAdd.on({
            'click': () => $.modalWindow({
                loadURL: VIEW_PATH.LIST_EDIT_LETTER
            })
        });

        // Функция добавления/редактирования заявления о соответствии
        function editConformityStatement(formJson) {
            $.modalWindow({
                loadURL: VIEW_PATH.LIST_EDIT_CONFORMITY_STATEMENT,
                loadData: {logRecordId: logRecordId, formJson: formJson }
            });
        }

        // Кнопка добавления заявления о соответствии
        $btnAddConformityStatement.on({
            'click': () => editConformityStatement('[]')
        });

        const table = new Tabulator('div.list_edit__table', {
            selectable: true,
            data: JSON.parse('${std:escapeJS(form.groupSerialNumber)}'),
            height: 'calc(100vh * 0.2)',
            layout: 'fitDataStretch',
            groupBy: [TABR_FIELD.GROUP_MAIN, TABR_FIELD.GROUP_SUB_MAIN],
            groupStartOpen: [true, true],
            groupToggleElement: 'header',
            groupHeader: [
                function(value) {
                    return '<span style="color:#315c83;">' + value + '</span>';
                },
                function(value) {
                    return '<span style="color:#315c83;">' + value + '</span>';
                }
            ],
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                {
                    title: 'Серийный номер',
                    field: TABR_FIELD.SERIAL_NUMBER,
                    cellClick: e => e.stopPropagation(),
                    editor: serialNumberEditor
                }
            ]
        });

        const conformityStatementTable = new Tabulator('div.list_edit_conformity-statement__table', {
            selectable: 1,
            data: JSON.parse('${std:escapeJS(form.conformityStatement)}'),
            height: '100%',
            layout: 'fitDataStretch',
            columns: [
                {
                    title: 'Номер',
                    field: 'conformityStatementNumber',
                    hozAlign: 'center'
                },
                {
                    title: 'Дата',
                    field: 'conformityStatementCreateDate',
                    hozAlign: 'center'
                },
                {
                    title: 'Срок действия',
                    field: 'conformityStatementValidity',
                    hozAlign: 'center'
                },
                {
                    title: 'Дата передачи',
                    field: 'conformityStatementTransferDate',
                    hozAlign: 'center'
                },
                { title: 'Подписал', field: TABR_FIELD.MANAGER }
            ],
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
                    action: () => editConformityStatement($conformityStatement.val())
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => deleteConformityStatement(id)
                });
                return menu;
            }
        });

        // Удаление заявления о соответствии
        function deleteConformityStatement(id) {
            confirmDialog({
                title: 'Удаление заявления о соответствии',
                message: 'Вы действительно хотите удалить заявление о соответствии?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: ACTION_PATH.LIST_EDIT_DELETE + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => {
                    $conformityStatement.val('[]');
                    conformityStatementTable.setData();
                })
            });
        }

        // Выбор серийных номеров
        $btnSelectSerialNumber.on({
            'click': () => $.modalWindow({
                loadURL: VIEW_PATH.LIST_EDIT_SERIAL_NUMBER,
                loadData: {
                    logRecordId: logRecordId,
                    productId: $productId.val()
                }
            })
        });

        // Удаление
        $btnDelete.on({
            'click': () => {
                table.getSelectedRows().forEach(row => {
                    let dataCount = table.getDataCount();
                    if (isFormId) {
                        if (dataCount > 1) {
                            row.delete();
                        } else {
                            alertDialog({ title: '', message: 'Предъявление должно содержать хотя бы одно изделие' });
                        }
                    } else {
                        if (dataCount > 1) {
                            row.delete();
                        } else {
                            row.delete();
                            $groupSerialNumber.val('[]');
                            $maxSerialNumberQuantityInput.val('');
                            $btnAddSerialNumber.hide();
                            $btnAdd.show();
                        }
                    }
                });
            }
        });

        $btnAddSerialNumber.on({
            'click': () => {
                const data = [];
                table.getData().forEach(el => data.push({
                    groupMain: el.groupMain,
                    groupSubMain: el.groupSubMain,
                    serialNumber: ''
                }));
                if (table.getDataCount() <=  $maxSerialNumberQuantityInput.val() - 1) {
                    table.addRow(data[0]);
                } else {
                    alertDialog({ title: '', message: 'Достигнуто максимально возможное количество изделий для добавления серийных номеров' });
                }
            }
        });

        // Добавление списка серийных номеров в сабмит форму
        $modal.on({
            'cb.onInitSubmit': () => {
                const data = [];
                table.getData().forEach(el => data.push({ serialNumber: el.serialNumber, productId: $productId.val() }));
                $groupSerialNumber.val(JSON.stringify(data));
                const  arr = [];
                conformityStatementTable.getData().forEach(el => arr.push({
                    conformityStatementNumber: el.conformityStatementNumber,
                    conformityStatementCreateDate: el.conformityStatementCreateDate,
                    conformityStatementValidity: el.conformityStatementValidity,
                    conformityStatementTransferDate: el.conformityStatementTransferDate,
                    manager: el.manager,
                    managerId: el.managerId
                }));
                $conformityStatement.val(JSON.stringify(arr));
            }
        });
    })
</script>