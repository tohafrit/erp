<div class="ui modal">
    <div class="header">Фильтр документации</div>
    <div class="content">
        <form class="ui small form">
            <div class="two fields">
                <div class="field">
                    <label>№ документации</label>
                    <input type="text" name="entityNumber"/>
                </div>
                <div class="field">
                    <label>№ комплекта</label>
                    <input type="text" name="setNumber"/>
                </div>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Тип документации</label>
                    <select class="ui dropdown search std-select" name="entityTypeId">
                        <option value=""><fmt:message key="text.notSpecified"/></option>
                        <c:forEach items="${entityTypeList}" var="entityType">
                            <option value="${entityType.id}">${entityType.value}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="field">
                    <label>Наименование изделия</label>
                    <input type="text" name="conditionalName"/>
                </div>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Номер извещения</label>
                    <input type="text" name="notificationNumber"/>
                </div>
                <div class="field">
                    <div class="two fields">
                        <div class="field">
                            <label>Срок изменений с</label>
                            <div class="ui calendar">
                                <div class="ui input left icon">
                                    <i class="calendar icon"></i>
                                    <input type="text" class="std-date" name="termChangeOnFrom"/>
                                </div>
                            </div>
                        </div>
                        <div class="field">
                            <label>по</label>
                            <div class="ui calendar">
                                <div class="ui input left icon">
                                    <i class="calendar icon"></i>
                                    <input type="text" class="std-date" name="termChangeOnTo"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Утверждено</label>
                    <select class="ui dropdown search std-select" name="checkedById">
                        <option value=""><fmt:message key="text.notSpecified"/></option>
                        <c:forEach items="${userList}" var="approvedBy">
                            <option value="${approvedBy.id}">${approvedBy.value}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="field">
                    <div class="two fields">
                        <div class="field">
                            <label>Утверждено с</label>
                            <div class="ui calendar">
                                <div class="ui input left icon">
                                    <i class="calendar icon"></i>
                                    <input type="text" class="std-datetime" name="approvedOnFrom"/>
                                </div>
                            </div>
                        </div>
                        <div class="field">
                            <label>по</label>
                            <div class="ui calendar">
                                <div class="ui input left icon">
                                    <i class="calendar icon"></i>
                                    <input type="text" class="std-datetime" name="approvedOnTo"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Разработчик</label>
                    <select class="ui dropdown search std-select" name="designedById">
                        <option value=""><fmt:message key="text.notSpecified"/></option>
                        <c:forEach items="${userList}" var="designedBy">
                            <option value="${designedBy.id}">${designedBy.value}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="field">
                    <div class="two fields">
                        <div class="field">
                            <label>Разработан с</label>
                            <div class="ui calendar">
                                <div class="ui input left icon">
                                    <i class="calendar icon"></i>
                                    <input type="text" class="std-datetime" name="designedOnFrom"/>
                                </div>
                            </div>
                        </div>
                        <div class="field">
                            <label>по</label>
                            <div class="ui calendar">
                                <div class="ui input left icon">
                                    <i class="calendar icon"></i>
                                    <input type="text" class="std-datetime" name="designedOnTo"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Проверен</label>
                    <select class="ui dropdown search std-select" name="checkedById">
                        <option value=""><fmt:message key="text.notSpecified"/></option>
                        <c:forEach items="${userList}" var="checkedBy">
                            <option value="${checkedBy.id}">${checkedBy.value}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="field">
                    <div class="two fields">
                        <div class="field">
                            <label>Проверено с</label>
                            <div class="ui calendar">
                                <div class="ui input left icon">
                                    <i class="calendar icon"></i>
                                    <input type="text" class="std-datetime" name="checkedOnFrom"/>
                                </div>
                            </div>
                        </div>
                        <div class="field">
                            <label>по</label>
                            <div class="ui calendar">
                                <div class="ui input left icon">
                                    <i class="calendar icon"></i>
                                    <input type="text" class="std-datetime" name="checkedOnTo"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Согласовано ВП МО РФ</label>
                    <select class="ui dropdown search std-select" name="militaryById">
                        <option value=""><fmt:message key="text.notSpecified"/></option>
                        <c:forEach items="${userList}" var="approvedMilitaryBy">
                            <option value="${approvedMilitaryBy.id}">${approvedMilitaryBy.value}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="field">
                    <div class="two fields">
                        <div class="field">
                            <label>Согласовано с</label>
                            <div class="ui calendar">
                                <div class="ui input left icon">
                                    <i class="calendar icon"></i>
                                    <input type="text" class="std-datetime" name="militaryOnFrom"/>
                                </div>
                            </div>
                        </div>
                        <div class="field">
                            <label>по</label>
                            <div class="ui calendar">
                                <div class="ui input left icon">
                                    <i class="calendar icon"></i>
                                    <input type="text" class="std-datetime" name="militaryOnTo"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Метролог</label>
                    <select class="ui dropdown search std-select" name="metrologistById">
                        <option value=""><fmt:message key="text.notSpecified"/></option>
                        <c:forEach items="${userList}" var="metrologistBy">
                            <option value="${metrologistBy.id}">${metrologistBy.value}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="field">
                    <div class="two fields">
                        <div class="field">
                            <label>Дата одобрения метролога с</label>
                            <div class="ui calendar">
                                <div class="ui input left icon">
                                    <i class="calendar icon"></i>
                                    <input type="text" class="std-datetime" name="metrologistOnFrom"/>
                                </div>
                            </div>
                        </div>
                        <div class="field">
                            <label>по</label>
                            <div class="ui calendar">
                                <div class="ui input left icon">
                                    <i class="calendar icon"></i>
                                    <input type="text" class="std-datetime" name="metrologistOnTo"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Нормоконтролер</label>
                    <select class="ui dropdown search std-select" name="normocontrollerById">
                        <option value=""><fmt:message key="text.notSpecified"/></option>
                        <c:forEach items="${userList}" var="normocontrollerBy">
                            <option value="${normocontrollerBy.id}">${normocontrollerBy.value}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="field">
                    <div class="two fields">
                        <div class="field">
                            <label>Одобрено нормоконтролером с</label>
                            <div class="ui calendar">
                                <div class="ui input left icon">
                                    <i class="calendar icon"></i>
                                    <input type="text" class="std-datetime" name="normocontrollerOnFrom"/>
                                </div>
                            </div>
                        </div>
                        <div class="field">
                            <label>по</label>
                            <div class="ui calendar">
                                <div class="ui input left icon">
                                    <i class="calendar icon"></i>
                                    <input type="text" class="std-datetime" name="normocontrollerOnTo"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Начальник технологического отдела</label>
                    <select class="ui dropdown search std-select" name="technologicalChiefById">
                        <option value=""><fmt:message key="text.notSpecified"/></option>
                        <c:forEach items="${userList}" var="technologicalChiefBy">
                            <option value="${technologicalChiefBy.id}">${technologicalChiefBy.value}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="field">
                    <div class="two fields">
                        <div class="field">
                            <label>Утвердил с</label>
                            <div class="ui calendar">
                                <div class="ui input left icon">
                                    <i class="calendar icon"></i>
                                    <input type="text" class="std-datetime" name="technologicalChiefOnFrom"/>
                                </div>
                            </div>
                        </div>
                        <div class="field">
                            <label>по</label>
                            <div class="ui calendar">
                                <div class="ui input left icon">
                                    <i class="calendar icon"></i>
                                    <input type="text" class="std-datetime" name="technologicalChiefOnTo"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>