<div class="detail_comment_filter__main">
    <form:form modelAttribute="productCommentFilterForm" cssClass="ui tiny form secondary segment detail_comment_filter__form">
        <div class="field">
            <div class="ui icon small buttons">
                <div class="ui button detail_comment_filter__btn-search" title="Поиск">
                    <i class="search blue icon"></i>
                </div>
                <div class="ui button detail_comment_filter__btn-clear-all" title="Очистить фильтр">
                    <i class="times blue icon"></i>
                </div>
            </div>
        </div>
        <div class="ui three column grid">
            <div class="column field">
                <label>Комментарий</label>
                <div class="ui input std-div-input-search icon">
                    <form:input path="comment"/>
                </div>
            </div>
            <div class="column field">
                <div class="two fields">
                    <div class="field">
                        <label>Дата и время создания с</label>
                        <div class="ui calendar">
                            <div class="ui input left icon std-div-input-search">
                                <i class="calendar icon"></i>
                                <form:input cssClass="std-date" path="createDateFrom"/>
                            </div>
                        </div>
                    </div>
                    <div class="field">
                        <label><fmt:message key="label.to"/></label>
                        <div class="ui calendar">
                            <div class="ui input left icon std-div-input-search">
                                <i class="calendar icon"></i>
                                <form:input cssClass="std-date" path="createDateTo"/>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="column field">
                <label>Автор</label>
                <form:select cssClass="ui dropdown search std-select" path="createdBy">
                    <form:option value=""><fmt:message key="text.notSpecified"/></form:option>
                    <c:forEach items="${userList}" var="user">
                        <form:option value="${user.id}">${user.userOfficialName}</form:option>
                    </c:forEach>
                </form:select>
            </div>
        </div>
    </form:form>

    <script>
        $(() => {
            const $main = $('div.detail_comment_filter__main');
            const $clearAllButton = $('div.detail_comment_filter__btn-clear-all');
            const $btnSearch = $('div.detail_comment_filter__btn-search');

            $clearAllButton.on({
                'click': () => formClear($main.find('form'))
            });

            $main.enter(() => $btnSearch.trigger('click'));
        })
    </script>
</div>