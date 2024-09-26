<div class="ui modal">
    <div class="ui small header">
        ${empty id ? 'Добавление периода поставки компонентов' : 'Редактирование периода поставки компонентов'}
    </div>
    <div class="content">
        <form class="ui form">
            <input type="hidden" name="id" value="${id}">
            <input type="hidden" name="version" value="${version}">
            <div class="field inline">
                <label>Номер</label>
                <span class="ui text">${number}</span>
            </div>
            <div class="field required">
                <label>Дата создания</label>
                <div class="ui calendar">
                    <div class="ui input left icon std-div-input-search">
                        <i class="calendar icon"></i>
                        <input type="text" class="std-date" name="createDate" value="${createDate}">
                    </div>
                </div>
                <div class="ui compact message small error" data-field="createDate"></div>
            </div>
            <div class="field required">
                <label>Дата начала периода</label>
                <div class="ui calendar">
                    <div class="ui input left icon std-div-input-search">
                        <i class="calendar icon"></i>
                        <input type="text" class="std-date" name="firstDate" value="${firstDate}">
                    </div>
                </div>
                <div class="ui compact message small error" data-field="firstDate"></div>
            </div>
            <div class="field required">
                <label>Дата окончания периода</label>
                <div class="ui calendar">
                    <div class="ui input left icon std-div-input-search">
                        <i class="calendar icon"></i>
                        <input type="text" class="std-date" name="lastDate" value="${lastDate}">
                    </div>
                </div>
                <div class="ui compact message small error" data-field="lastDate"></div>
            </div>
            <div class="field">
                <label>Комментарий</label>
                <div class="ui textarea">
                    <textarea name="comment" rows="3">${comment}</textarea>
                </div>
                <div class="ui compact message small error" data-field="comment"></div>
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