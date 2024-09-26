<div class="detail_occurrence_filter__main">
    <form:form modelAttribute="productOccurrenceFilterForm" cssClass="ui tiny form secondary segment detail_occurrence_filter__form">
        <div class="field">
            <div class="ui icon small buttons">
                <div class="ui button detail_occurrence_filter__btn-search" title="Поиск">
                    <i class="search blue icon"></i>
                </div>
                <div class="ui button detail_occurrence_filter__btn-clear-all" title="Очистить фильтр">
                    <i class="times blue icon"></i>
                </div>
            </div>
        </div>
        <div class="ui three column grid">
            <div class="column field">
                <h4><fmt:message key="product.detail.occurrence.filter.group.header.product"/></h4>
                <div class="ui divider"></div>
                <div class="field">
                    <div class="ui checkbox">
                        <form:checkbox path="active"/>
                        <label><fmt:message key="product.detail.occurrence.filter.group.product.active"/></label>
                    </div>
                </div>
                <div class="field">
                    <div class="ui checkbox">
                        <form:checkbox path="archive"/>
                        <label><fmt:message key="product.detail.occurrence.filter.group.product.archive"/></label>
                    </div>
                </div>
            </div>
            <div class="column field">
                <h4><fmt:message key="product.detail.occurrence.filter.group.header.last"/></h4>
                <div class="ui divider"></div>
                <div class="field">
                    <div class="ui checkbox">
                        <form:checkbox path="lastApprove"/>
                        <label><fmt:message key="product.detail.occurrence.filter.group.last.approve"/></label>
                    </div>
                </div>
                <div class="field">
                    <div class="ui checkbox">
                        <form:checkbox path="lastAccept"/>
                        <label><fmt:message key="product.detail.occurrence.filter.group.last.accept"/></label>
                    </div>
                </div>
                <div class="field">
                    <div class="ui checkbox">
                        <form:checkbox path="lastNumber"/>
                        <label><fmt:message key="product.detail.occurrence.filter.group.last.number"/></label>
                    </div>
                </div>
            </div>
            <div class="column field">
                <h4><fmt:message key="product.detail.occurrence.filter.group.header.launch"/></h4>
                <div class="ui divider"></div>
                <div class="field">
                    <label><fmt:message key="product.detail.occurrence.filter.group.launch.approve"/></label>
                    <div class="ui input std-div-input-search icon">
                        <form:input path="approveSearchText"/>
                    </div>
                </div>
                <div class="field">
                    <label><fmt:message key="product.detail.occurrence.filter.group.launch.accept"/></label>
                    <div class="ui input std-div-input-search icon">
                        <form:input path="acceptSearchText"/>
                    </div>
                </div>
            </div>
        </div>
    </form:form>

    <script>
        $(() => {
            $('div.detail_occurrence_filter__btn-clear-all').on({
                'click': () => formClear('.detail_occurrence_filter__form')
            });
        })
    </script>
</div>