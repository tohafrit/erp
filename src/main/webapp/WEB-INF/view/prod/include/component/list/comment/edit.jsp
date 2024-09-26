<div class="ui modal large">
    <div class="ui small header">
        ${empty id ? 'Добавление комментария' : 'Редактирование комментария'}
    </div>
    <div class="content">
        <form class="ui form">
            <input type="hidden" name="id" value="${id}">
            <input type="hidden" name="componentId" value="${componentId}">
            <div class="field">
                <label>Автор</label>
                ${createdBy}
            </div>
            <div class="field">
                <label>Дата и время создания:</label>
                ${createdDate}
            </div>
            <div class="field required">
                <label>Комментарий</label>
                <div class="ui textarea">
                    <textarea name="comment" rows="10">${comment}</textarea>
                    <div class="ui compact message error" data-field="comment"></div>
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