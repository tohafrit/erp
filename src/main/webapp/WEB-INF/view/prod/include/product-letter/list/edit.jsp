<div class="ui modal">
    <div class="header">
        ${empty id ? 'Добавление литеры' : 'Редактирование литеры'}
    </div>
    <div class="content">
        <form class="ui small form">
            <input type="hidden" name="id" value="${id}">
            <div class="field required">
                <label>Наименование</label>
                <input type="text" name="name" value="${name}"/>
                <div class="ui compact message error" data-field="name"></div>
            </div>
            <div class="field">
                <label>Комментарий</label>
                <div class="ui textarea">
                    <textarea name="comment" rows="3">${comment}</textarea>
                </div>
            </div>
        </form>
    </div>
    <div class="actions">
        <button class="ui small button" type="submit">
            <i class="icon blue save"></i>
            <fmt:message key="label.button.save"/>
        </button>
    </div>
</div>