<div class="ui modal">
    <div class="header">
        ${empty form.id ? 'Добавление атрибута - ' : 'Редактирование атрибута - '}
        <fmt:message key="${form.type.nameProperty}"/>
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui small form">
            <form:hidden path="id"/>
            <form:hidden path="type"/>
            <form:hidden path="category.id"/>
            <div class="field required">
                <label>Наименование</label>
                <form:input path="name"/>
                <div class="ui compact message error" data-field="name"></div>
            </div>
            <c:if test="${fn:contains('SELECT', form.type)}">
                <div class="field inline">
                    <div class="ui checkbox">
                        <form:checkbox path="required"/>
                        <label><fmt:message key="componentAttributePreferenceType.required"/></label>
                    </div>
                </div>
            </c:if>
            <c:if test="${fn:contains('SELECT;CHECKBOX;INPUT', form.type)}">
                <div class="field inline">
                    <div class="ui checkbox">
                        <form:checkbox path="disabled"/>
                        <label><fmt:message key="componentAttributePreferenceType.disabled"/></label>
                    </div>
                </div>
            </c:if>
            <c:if test="${fn:contains('SELECT', form.type)}">
                <div class="field inline">
                    <div class="ui checkbox">
                        <form:checkbox path="multiple"/>
                        <label><fmt:message key="componentAttributePreferenceType.multiple"/></label>
                    </div>
                </div>
            </c:if>
            <c:if test="${fn:contains('SELECT;CHECKBOX;INPUT', form.type)}">
                <div class="field inline">
                    <div class="ui checkbox">
                        <form:checkbox path="techCharInclude"/>
                        <label><fmt:message key="componentAttributePreferenceType.techCharInclude"/></label>
                    </div>
                </div>
            </c:if>
            <c:if test="${fn:contains('INPUT', form.type)}">
                <div class="field required">
                    <label><fmt:message key="componentAttributePreferenceType.inputMinLength"/></label>
                    <form:input path="inputMinLength" cssClass="attribute_edit__input-length"/>
                    <div class="ui compact message error" data-field="inputMinLength"></div>
                </div>
                <div class="field required">
                    <label><fmt:message key="componentAttributePreferenceType.inputMaxLength"/></label>
                    <form:input path="inputMaxLength" cssClass="attribute_edit__input-length"/>
                    <div class="ui compact message error" data-field="inputMaxLength"></div>
                </div>
            </c:if>
            <c:if test="${fn:contains('SELECT', form.type)}">
                <div class="field">
                    <label><fmt:message key="componentAttributePreferenceType.selectPostfix"/></label>
                    <form:input path="selectPostfix"/>
                    <div class="ui compact message error" data-field="selectPostfix"></div>
                </div>
                <div class="field required">
                    <label>
                        <fmt:message key="componentAttributePreferenceType.selectOptions"/>
                        <div class="ui button tiny basic icon attribute_edit__btn-add-option" title="Добавить пункт">
                            <i class="add icon"></i>
                        </div>
                    </label>
                    <div class="ui compact message error" data-field="selectOptionList"></div>
                    <table class="attribute_edit__option-table dialog-list-form-container">
                        <tbody>
                            <c:forEach items="${form.selectOptionList}" var="option">
                                <tr>
                                    <td class="attribute_edit__cell-option">
                                        <div class="ui fluid input">
                                            <input type="hidden" data-list-form-attribute="selectOptionList.id" value="${option.id}">
                                            <input type="text" data-list-form-attribute="selectOptionList.value" value="${option.value}">
                                        </div>
                                    </td>
                                    <td class="attribute_edit__cell-remove">
                                        <div class="ui button tiny basic icon" title="Удалить пункт">
                                            <i class="times link red icon"></i>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:if>
            <div class="field">
                <label>Описание</label>
                <div class="ui textarea">
                    <form:textarea path="description" rows="3"/>
                </div>
                <div class="ui compact message error" data-field="description"></div>
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
        const $btnAddOption = $('div.attribute_edit__btn-add-option');
        const $optionTable = $('table.attribute_edit__option-table');
        const blankOption =
            `<tr>
                <td class="attribute_edit__cell-option">
                    <div class="ui fluid input">
                        <input type="hidden" data-list-form-attribute="selectOptionList.id" value="0">
                        <input type="text" data-list-form-attribute="selectOptionList.value">
                    </div>
                </td>
                <td class="attribute_edit__cell-remove">
                    <div class="ui button tiny basic icon" title="Удалить пункт">
                        <i class="times link red icon"></i>
                    </div>
                </td>
            </tr>`;

        // Инициализация сортировки
        initSortableTable($optionTable);

        // Добавление опции
        $btnAddOption.on({
            'click': () => {
                $(blankOption).appendTo($optionTable);
                $optionTable.trigger('table.recalculate');
            }
        });

        // Функция преназначения кнопок удаления строки
        $optionTable.on({
            'table.recalculate': () => {
                $optionTable.find('td.attribute_edit__cell-remove > div.button').off().on({
                    'click': function () {
                        $(this).closest('tr').remove();
                    }
                });
            }
        });
        $optionTable.trigger('table.recalculate');

        // Ограничение полей ввода
        $('input.attribute_edit__input-length').inputmask('numeric', {
            min: 0,
            max: 64,
            rightAlign: false,
            placeholder: ''
        });
    });
</script>