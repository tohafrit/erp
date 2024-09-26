<div class="ui modal">
    <div class="ui small header">Фильтр</div>
    <div class="content">
        <form class="ui small form">
            <div class="field">
                <label>Наименование</label>
                <div class="ui input std-div-input-search">
                    <input type="text" name="name"/>
                </div>
            </div>
            <div class="field">
                <label>Позиция</label>
                <div class="ui input std-div-input-search">
                    <input type="text" name="position" data-inputmask-regex="[0-9]{0,6}"/>
                </div>
            </div>
            <div class="field">
                <label>Категория</label>
                <select name="categoryIdList" class="ui dropdown search std-select" multiple>
                    <c:forEach items="${categoryList}" var="opt">
                        <option value="${opt.id}">${opt.value}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="field">
                <label>Показать замещаемые</label>
                <select name="showReplaceable" class="ui dropdown search std-select">
                    <option value="false"><fmt:message key="text.no"/></option>
                    <option value="true"><fmt:message key="text.yes"/></option>
                </select>
            </div>
            <div class="field">
                <label>Описание</label>
                <div class="ui input std-div-input-search">
                    <input type="text" name="description"/>
                </div>
            </div>
        </form>
    </div>
</div>