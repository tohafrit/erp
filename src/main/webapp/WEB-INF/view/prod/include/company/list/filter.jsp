<div class="list_filter__main">
    <form:form modelAttribute="companyListFilterForm" cssClass="ui tiny form secondary segment list_filter__form">
        <div class="field">
            <div class="ui icon small buttons">
                <div class="ui button list_filter__btn-search" title="Поиск">
                    <i class="search blue icon"></i>
                </div>
                <div class="ui button list_filter__btn-clear-all" title="Очистить фильтр">
                    <i class="times blue icon"></i>
                </div>
            </div>
        </div>
        <div class="ui three column grid">
            <div class="column field">
                <label>
                    <fmt:message key="company.field.name"/>
                    <i class="times link blue icon list_filter__btn-clear"></i>
                </label>
                <form:input path="name"/>
            </div>
            <div class="column field">
                <label>
                    <fmt:message key="company.field.inn"/>
                    <i class="times link blue icon list_filter__btn-clear"></i>
                </label>
                <form:input path="inn"/>
            </div>
            <div class="column field">
                <label>
                    <fmt:message key="company.field.kpp"/>
                    <i class="times link blue icon list_filter__btn-clear"></i>
                </label>
                <form:input path="kpp"/>
            </div>
        </div>
    </form:form>

    <script>
        $(() => {
            const $main = $('div.list_filter__main');
            const $clearButtonList = $('i.list_filter__btn-clear');
            const $clearAllButton = $('div.list_filter__btn-clear-all');
            const $btnSearch = $('div.list_filter__btn-search');

            $main.find('select').dropdown({
                fullTextSearch: true,
                maxSelections: 5
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

            $clearAllButton.on({
                 'click': () => $clearButtonList.trigger('click')
            });

            $main.enter(() => $btnSearch.trigger('click'));
        })
    </script>
</div>