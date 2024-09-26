<div class="ui modal list_edit__modal">
    <div class="header">
        ${empty form.id ? 'Добавление обоснования' : 'Редактирование обоснования'}
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui form">
            <form:hidden path="id"/>
            <form:hidden path="version"/>
            <form:hidden path="groupPrice"/>
            <div class="field required">
                <label>Наименование</label>
                <form:input path="name" type="search"/>
                <div class="ui compact message small error" data-field="name"></div>
            </div>
            <c:if test="${not empty form.id}">
                <div class="field inline">
                    <label>Дата создания</label>
                    <span class="ui text">${form.createDate}</span>
                </div>
            </c:if>
            <div class="field required">
                <label>Дата утверждения</label>
                <div class="ui calendar list_edit__approval-date-block">
                    <div class="ui input left icon">
                        <i class="calendar icon"></i>
                        <form:input cssClass="std-date" path="approvalDate"/>
                    </div>
                </div>
                <div class="ui compact message small error" data-field="approvalDate"></div>
            </div>
            <div class="field">
                <label>Файл</label>
                <div class="std-file ui action input">
                    <form:hidden path="fileStorage.id"/>
                    <input type="file" name="file"/>
                    <span>
                        ${form.fileStorage.name}
                        <a target="_blank" href="<c:url value="/download-file/${form.fileStorage.urlHash}"/>">
                            <fmt:message key="text.downloadFile"/>
                        </a>
                    </span>
                </div>
                <div class="ui compact message small error" data-field="file"></div>
            </div>
            <div class="field">
                <label>Комментарий</label>
                <div class="ui textarea">
                    <form:textarea path="comment" rows="3"/>
                </div>
                <div class="ui compact message small error" data-field="comment"></div>
            </div>
            <div class="field required inline">
                <label>Классификационные группы</label>
                <i class="icon add link blue list_edit__btn-add" title="<fmt:message key="label.button.add"/>"></i>
                <i class="icon trash link blue list_edit__btn-delete" title="<fmt:message key="label.button.delete"/>"></i>
                <div class="list_edit__table table-sm table-striped"></div>
                <div class="ui compact message small error" data-field="groupPrice"></div>
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
        const isDisableEdit = '${isDisableEdit}' === 'true';
        const $modal = $('div.list_edit__modal');
        const $approvalDateBlock = $('div.list_edit__approval-date-block');
        const $btnAdd = $('i.list_edit__btn-add');
        const $btnDelete = $('i.list_edit__btn-delete');

        if (isDisableEdit) {
            $approvalDateBlock.addClass('disabled');
            $btnAdd.addClass('disabled');
            $btnDelete.addClass('disabled');
        }

        // Кастомый инпут редактирования цен
        const priceEditor = (cell, onRendered, success) => {
            const $editor = $(document.createElement('input'));
            const editor = $editor.get(0);
            $editor.css('padding', '3px');
            $editor.attr('type', 'text');
            $editor.val(cell.getValue());
            $editor.inputmask('inputMoney');
            onRendered(() => {
                $editor.trigger('focus');
                editor.setSelectionRange(0, 0);
            });
            const onSuccess = () => success($editor.val() === '' ? '0.00' : $editor.val().toString().replaceAll(' ', ''));
            $editor.on({
                'change': () => onSuccess(),
                'blur': () => onSuccess()
            });
            return editor;
        };

        const table = new Tabulator('div.list_edit__table', {
            selectable: true,
            data: JSON.parse('${std:escapeJS(form.groupPrice)}'),
            height: 'calc(100vh * 0.3)',
            layout: 'fitDataStretch',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                TABR_COL_ID,
                { title: 'Номер', field: TABR_FIELD.NUMBER },
                { title: 'Характеристика', field: TABR_FIELD.NAME },
                {
                    title: 'Цена',
                    field: TABR_FIELD.PRICE,
                    editor: priceEditor,
                    cellClick: e => e.stopPropagation(),
                    formatter: 'money',
                    formatterParams: { decimal: ',', thousand: ' ' }
                }
            ]
        });

        // Добавление группы
        $btnAdd.on({
            'click': () => {
                const data = [];
                table.getData().forEach(el => data.push(el.id));
                $.modalWindow({
                    loadURL: VIEW_PATH.LIST_EDIT_ADD_GROUP,
                    loadData: { classGroupIdList: data.join(',') }
                });
            }
        });

        // Удаление группы
        $btnDelete.on({
            'click': () => {
                const data = [];
                table.getSelectedData().forEach(el => data.push(el.id));
                table.deleteRow(data);
            }
        });

        // Добавление списка цен в сабмит форму
        $modal.on({
            'cb.onInitSubmit': () => {
                const data = [];
                table.getData().forEach(el => data.push({ id: el.id, number: el.number, name: el.name, price: el.price.toString().replaceAll(' ', '') }));
                $modal.find('input[name="groupPrice"]').val(JSON.stringify(data));
            }
        });
    })
</script>