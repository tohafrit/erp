<div class="list_filter__main">
    <form:form modelAttribute="supplierFilter" cssClass="ui tiny form secondary segment list_filter__form">
        <div class="field">
            <div class="ui icon small buttons">
                <div class="ui button list_filter__btn-search" title="<fmt:message key="label.button.search"/>">
                    <i class="search blue icon"></i>
                </div>
                <div class="ui button list_filter__btn-clear-all" title="<fmt:message key="label.button.clearFilter"/>">
                    <i class="times blue icon"></i>
                </div>
            </div>
        </div>
        <div class="ui three column grid">
            <div class="column field">
                <label>
                    <fmt:message key="supplier.field.name"/>
                    <i class="times link blue icon list_filter__btn-clear"></i>
                </label>
                <form:input path="name"/>
            </div>
            <div class="column field">
                <label>
                    <fmt:message key="supplier.field.inn"/>
                    <i class="times link blue icon list_filter__btn-clear"></i>
                </label>
                <form:input path="inn"/>
            </div>
            <div class="column field">
                <label>
                    <fmt:message key="supplier.field.kpp"/>
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

            $clearButtonList.on({
                'click': e => $(e.currentTarget).closest('div.field').find('input[type="text"], select').val('')
            });

            $clearAllButton.on({
                'click': () => $clearButtonList.trigger('click')
            });

            $main.enter(() => $btnSearch.trigger('click'));
        })
    </script>
</div>