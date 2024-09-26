<div class="admin_filter__main">
    <form:form modelAttribute="administrationOfficeDemandListFilterForm" cssClass="ui tiny form secondary segment admin_filter__form">
        <div class="field">
            <div class="ui icon small buttons">
                <div class="ui button admin_filter__btn-search" title="Поиск">
                    <i class="search blue icon"></i>
                </div>
                <div class="ui button admin_filter__btn-clear-all" title="Очистить фильтр">
                    <i class="times blue icon"></i>
                </div>
            </div>
        </div>
        <div class="ui four column grid">
            <div class="column field">
                <label>
                    Пользователь
                    <i class="times link blue icon admin_filter__btn-clear"></i>
                </label>
                <form:select cssClass="ui search dropdown label std-select" path="userIdList" multiple="multiple">
                    <c:forEach items="${userList}" var="user">
                        <form:option value="${user.id}">${user.fullName}</form:option>
                    </c:forEach>
                </form:select>
            </div>
            <div class="column field">
                <label>
                    Номер комнаты
                    <i class="times link blue icon admin_filter__btn-clear"></i>
                </label>
                <form:input path="roomNumber"/>
            </div>
            <div class="column field">
                <label>
                    Причина
                    <i class="times link blue icon admin_filter__btn-clear"></i>
                </label>
                <form:input path="reason"/>
            </div>
            <div class="column field">
                <div class="two fields">
                    <div class="field">
                        <label>
                            <fmt:message key="label.from"/>
                            <i class="times link blue icon admin_filter__btn-clear"></i>
                        </label>
                        <div class="ui calendar">
                            <div class="ui input left icon">
                                <i class="calendar icon"></i>
                                <form:input cssClass="std-datetime" path="requestDateFrom"/>
                            </div>
                        </div>
                    </div>
                    <div class="field">
                        <label>
                            <fmt:message key="label.to"/>
                            <i class="times link blue icon admin_filter__btn-clear"></i>
                        </label>
                        <div class="ui calendar">
                            <div class="ui input left icon">
                                <i class="calendar icon"></i>
                                <form:input cssClass="std-datetime" path="requestDateTo"/>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </form:form>

    <script>
        $(() => {
            const $clearButtonList = $('i.admin_filter__btn-clear');
            const $clearAllButton = $('div.admin_filter__btn-clear-all')

            $clearAllButton.on({
                'click': () => $clearButtonList.trigger('click')
            });

            $clearButtonList.on({
                'click': function() {
                    const $field = $(this).closest('div.field').find('input[type="text"], select');
                    if ($field.is('input')) {
                        $field.val('');
                    } else if ($field.is('select')) {
                        $field.dropdown('clear');
                    }
                }
            });
        })
    </script>
</div>