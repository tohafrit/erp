<div class="ui modal">
    <div class="ui small header">Фильтр изделий</div>
    <div class="content">
        <form class="ui small form">
            <div class="two fields">
                <div class="field">
                    <label>Условное наименование</label>
                    <div class="ui input std-div-input-search icon">
                        <input type="text" name="conditionalName"/>
                    </div>
                </div>
                <div class="field">
                    <label>ТУ изделия</label>
                    <div class="ui input std-div-input-search icon">
                        <input type="text" name="decimalNumber"/>
                    </div>
                </div>
            </div>
            <div class="field">
                <label>Наименование по ТС</label>
                <div class="ui input std-div-input-search icon">
                    <input type="text" name="techSpecName"/>
                </div>
            </div>
            <div class="three fields">
                <div class="field">
                    <label>Серийное</label>
                    <select class="ui dropdown std-select" name="serial">
                        <option value=""><fmt:message key="text.notSpecified"/></option>
                        <option value="false"><fmt:message key="text.no"/></option>
                        <option value="true"><fmt:message key="text.yes"/></option>
                    </select>
                </div>
                <div class="field">
                    <label>Краткая техническая характеристика</label>
                    <select class="ui dropdown search std-select" name="typeIdList" multiple>
                        <c:forEach items="${productTypeList}" var="productType">
                            <option value="${productType.id}">${productType.name}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="field">
                    <label>Литера</label>
                    <select class="ui dropdown search std-select" name="letterIdList" multiple>
                        <c:forEach items="${letterList}" var="letter">
                            <option value="${letter.id}">${letter.name}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Выпускаемые</label>
                    <select class="ui dropdown std-select" name="active">
                        <option value="true"><fmt:message key="text.yes"/></option>
                        <option value="false"><fmt:message key="text.no"/></option>
                    </select>
                </div>
                <div class="field">
                    <label>Устаревшие</label>
                    <select class="ui dropdown std-select" name="archive">
                        <option value="false"><fmt:message key="text.no"/></option>
                        <option value="true"><fmt:message key="text.yes"/></option>
                    </select>
                </div>
            </div>
            <div class="three fields">
                <div class="field">
                    <label>Позиция</label>
                    <div class="ui input std-div-input-search icon">
                        <input type="text" name="position" data-inputmask-regex="[0-9]{0,6}"/>
                    </div>
                </div>
                <div class="field">
                    <label>Идентификатор</label>
                    <div class="ui input std-div-input-search icon">
                        <input type="text" name="descriptor"/>
                    </div>
                </div>
                <div class="field">
                    <label>Префикс</label>
                    <div class="ui input std-div-input-search icon">
                        <input type="text" name="prefix"/>
                    </div>
                </div>
            </div>
            <div class="field">
                <label>Ведущий</label>
                <select class="ui dropdown search std-select" name="leadIdList" multiple>
                    <c:forEach items="${leadList}" var="lead">
                        <option value="${lead.id}">${lead.userOfficialName}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="field">
                <label>Классификационная группа</label>
                <select class="ui dropdown search std-select" name="classificationGroupIdList" multiple>
                    <c:forEach items="${classificationGroupList}" var="opt">
                        <option value="${opt.id}">${opt.value}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="field">
                <label>Комментарий</label>
                <div class="ui input std-div-input-search icon">
                    <input type="text" name="comment"/>
                </div>
            </div>
        </form>
    </div>
</div>