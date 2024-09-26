<div class="ui modal list_edit__main">
    <div class="header">
        ${empty form.id ? 'Добавление шаблона сообщения' : 'Редактирование шаблона сообщения'}
    </div>
    <div class="scrolling content">
        <form:form method="POST" modelAttribute="form" cssClass="ui small form">
            <form:hidden path="id"/>
            <div class="field">
                <label>Активность</label>
                <std:trueOrFalse name="active" value="${form.active}"/>
            </div>
            <div class="field required">
                <label>От кого</label>
                <form:input path="emailFrom"/>
                <div class="ui compact message error" data-field="emailFrom"></div>
            </div>
            <div class="field required">
                <label>Кому</label>
                <form:input path="emailTo"/>
                <div class="ui compact message error" data-field="emailTo"></div>
            </div>
            <div class="field required">
                <label>Тема сообщения</label>
                <form:input path="subject"/>
                <div class="ui compact message error" data-field="subject"></div>
            </div>
            <div class="field required">
                <label>Сообщение</label>
                <form:textarea path="message" cssClass="std-ckeditor" rows="10"/>
                <div class="ui compact message error" data-field="message"></div>
            </div>
            <div class="field list_edit__available-fields">
                <label>Доступные поля</label>
                <div class="list_edit__description"></div>
            </div>
            <div class="field required">
                <label>Тип сообщения</label>
                <form:select cssClass="ui dropdown label std-select list_edit__type-select" path="messageType.id">
                    <form:option value=""><fmt:message key="text.notSpecified"/></form:option>
                    <c:forEach items="${messageTypeList}" var="messageType">
                        <form:option value="${messageType.id}">${messageType.name}</form:option>
                    </c:forEach>
                </form:select>
                <div class="ui compact message error" data-field="messageType"></div>
            </div>
            <div class="field">
                <label>Копия</label>
                <form:input path="cc"/>
                <div class="ui compact message error" data-field="cc"></div>
            </div>
            <div class="field">
                <label>Скрытая копия</label>
                <form:input path="bcc"/>
                <div class="ui compact message error" data-field="bcc"></div>
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
        const $main = $('div.list_edit__main');
        const $messageType = $('div.list_edit__type-select select');
        const $typeDescription = $('div.list_edit__description');
        const $availableFields = $('div.list_edit__available-fields');
        //
        let lastFocused = null;

        $main.find('input').on({
            'focus': e => lastFocused = $(e.currentTarget)
        });
        CKEDITOR.instances['message'].on('focus', () => {
            lastFocused = null;
        });
        $messageType.on({
            'change': e => {
                $.ajax({
                    type: 'POST',
                    url: '/api/action/corp/message-template/list/edit/description',
                    data: { id: $(e.currentTarget).val() },
                    success: (description) => {
                        let isNotSpecified = description !== '';
                        $availableFields.toggle(isNotSpecified);
                        $typeDescription.html('');
                        if (isNotSpecified) {
                            let arrDescription = description.split('\n');
                            $.each(arrDescription, (index, value) => {
                                let values = value.split('-');
                                $typeDescription.append('<p><a class="list_edit__href" href="' + values[0] + '">' + values[0] + '</a> -' + values[1] + '</p>');
                            });
                            $('.list_edit__href').on({
                                'click': e => {
                                    e.preventDefault();
                                    let key = $(e.currentTarget).attr('href');
                                    if (lastFocused !== null) {
                                        lastFocused.val(
                                            function (i, val) {
                                                let begin = val.slice(0, this.selectionStart);
                                                let end = val.slice(this.selectionStart, val.length);
                                                return begin + key + end;
                                            }
                                        );
                                    } else {
                                        CKEDITOR.instances['message'].insertText(key.trim());
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        $messageType.trigger('change');
    })
</script>