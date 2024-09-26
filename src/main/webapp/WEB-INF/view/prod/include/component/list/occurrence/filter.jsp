<form:form modelAttribute="componentListOccurrenceFilterForm" cssClass="ui tiny form secondary segment list_occurrence_filter__form">
    <div class="field">
        <div class="ui icon small buttons">
            <div class="ui button list_occurrence_filter__btn-search" title="Поиск">
                <i class="search blue icon"></i>
            </div>
            <div class="ui button list_occurrence_filter__btn-clear-all" title="Очистить фильтр">
                <i class="times blue icon"></i>
            </div>
        </div>
    </div>
    <div class="ui grid">
        <div class="two wide column field">
            <h4>Изделия</h4>
            <div class="ui divider"></div>
            <div class="field">
                <div class="ui checkbox">
                    <form:checkbox path="active"/>
                    <label>Выпускаемые</label>
                </div>
            </div>
            <div class="field">
                <div class="ui checkbox">
                    <form:checkbox path="archive"/>
                    <label>Устаревшие</label>
                </div>
            </div>
        </div>
        <div class="three wide column field">
            <h4>Спецификации</h4>
            <div class="ui divider"></div>
            <div class="field">
                <div class="ui checkbox">
                    <form:checkbox path="lastApprove"/>
                    <label>Последние утвержденные</label>
                </div>
            </div>
            <div class="field">
                <div class="ui checkbox">
                    <form:checkbox path="lastAccept"/>
                    <label>Последние принятые</label>
                </div>
            </div>
        </div>
        <div class="one wide column field">&nbsp;</div>
        <div class="six wide column field">
            <h4>Запуски</h4>
            <div class="ui divider"></div>
            <div class="ui two column grid">
                <div class="column field">
                    <label>Спецификация утверждена к</label>
                    <form:input type="search" path="approveSearchText" />
                </div>
                <div class="column field">
                    <label>Спецификация принята к</label>
                    <form:input type="search" path="acceptSearchText"/>
                </div>
            </div>
        </div>
    </div>
</form:form>

<script>
    $(() => {
        const $clearAllBtn = $('div.list_occurrence_filter__btn-clear-all');
        $clearAllBtn.on({
            'click': () => formClear('form.list_occurrence_filter__form')
        });
    })
</script>