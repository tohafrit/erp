<div class="ui modal">
    <div class="ui small header">Фильтр</div>
    <div class="content">
        <form class="ui small form">
            <div class="field">
                <label>Изделие</label>
                <div class="ui input std-div-input-search">
                    <input type="text" name="product"/>
                </div>
            </div>
            <div class="field">
                <label>Краткая техническая характеристика</label>
                <select class="ui dropdown search std-select" name="typeId">
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                    <c:forEach items="${typeList}" var="opt">
                        <option value="${opt.id}">${opt.value}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="field">
                <label>Серийное</label>
                <select class="ui dropdown std-select" name="serial">
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                    <option value="false"><fmt:message key="text.no"/></option>
                    <option value="true"><fmt:message key="text.yes"/></option>
                </select>
            </div>
        </form>
    </div>
</div>