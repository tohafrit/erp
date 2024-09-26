<div class="ui modal fullscreen detail_specification_info_spec-add-component__main">
    <div class="ui small header">Добавление компонента</div>
    <div class="content">
        <form class="ui form">
            <div class="field inline">
                <label>Изделие</label>
                <span class="ui text">${productInfo}</span>
            </div>
        </form>
        <i class="icon filter link detail_specification_info_spec-add-component__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        <form:form modelAttribute="productSpecComponentFilterForm" cssClass="ui tiny form secondary segment detail_specification_info_spec-add-component__filter-form">
            <div class="field">
                <div class="ui icon small buttons">
                    <div class="ui button detail_specification_info_spec-add-component__btn-search" title="Поиск">
                        <i class="search blue icon"></i>
                    </div>
                    <div class="ui button detail_specification_info_spec-add-component__btn-clear-all" title="Очистить фильтр">
                        <i class="times blue icon"></i>
                    </div>
                </div>
            </div>
            <div class="ui three column grid">
                <div class="column field">
                    <div class="ui checkbox">
                        <form:checkbox path="newComponent"/>
                        <label><strong>Только новые</strong></label>
                    </div>
                </div>
            </div>
            <div class="ui three column grid">
                <div class="column field">
                    <label>Позиция</label>
                    <div class="ui input std-div-input-search icon">
                        <form:input path="position" cssClass="detail_specification_info_spec-add-component__field-position"/>
                    </div>
                </div>
                <div class="column field">
                    <label>Наименование</label>
                    <div class="ui input std-div-input-search icon">
                        <form:input path="name"/>
                    </div>
                </div>
                <div class="column field">
                    <label>Производитель</label>
                    <form:select cssClass="ui dropdown search std-select" path="producerIdList" multiple="multiple">
                        <c:forEach items="${producerList}" var="producer">
                            <form:option value="${producer.id}">${producer.name}</form:option>
                        </c:forEach>
                    </form:select>
                </div>
                <div class="column field">
                    <label>Категория</label>
                    <form:select cssClass="ui dropdown search std-select" path="categoryIdList" multiple="multiple">
                        <c:forEach items="${categoryList}" var="category">
                            <form:option value="${category.id}">${category.name}</form:option>
                        </c:forEach>
                    </form:select>
                </div>
                <div class="column field">
                    <label>Показать замещаемые</label>
                    <form:select cssClass="ui dropdown std-select" path="showReplaceable">
                        <form:option value="false"><fmt:message key="text.no"/></form:option>
                        <form:option value="true"><fmt:message key="text.yes"/></form:option>
                    </form:select>
                </div>
                <div class="column field">
                    <label>Описание</label>
                    <div class="ui input std-div-input-search icon">
                        <form:input path="description"/>
                    </div>
                </div>
            </div>
        </form:form>
        <div class="detail_specification_info_spec-add-component__table table-sm table-striped"></div>
    </div>
    <div class="actions">
        <div class="ui small button disabled detail_specification_info_spec-add-component__btn-select">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.select"/>
        </div>
    </div>
</div>

<script>
    $(() => {
        const bomId = '${bomId}';
        const $modal = $('div.detail_specification_info_spec-add-component__main');
        const $filter = $('form.detail_specification_info_spec-add-component__filter-form');
        const $btnFilter = $('i.detail_specification_info_spec-add-component__btn-filter');
        const $btnSelect = $('div.detail_specification_info_spec-add-component__btn-select');
        const $btnSearch = $('div.detail_specification_info_spec-add-component__btn-search');
        const $specDatatable = $('div.detail_specification_info__spec-table');

        // Кнопка фильтра
        $btnFilter.on({
            'click': function() {
                $(this).toggleClass('blue');
                $filter.toggle(!$filter.is(':visible'));
            }
        });

        // Маска позиции
        $('input.detail_specification_info_spec-add-component__field-position').inputmask({
            placeholder: '',
            regex: '[0-9]{0,6}'
        });

        const table = new Tabulator('div.detail_specification_info_spec-add-component__table', {
            selectable: 1,
            ajaxSorting: true,
            pagination: 'remote',
            height: 'calc(100vh * 0.4)',
            layout: 'fitDataStretch',
            ajaxURL: '/api/action/prod/product/detail/specification/info/spec-add-component/list-load',
            ajaxRequesting: (url, params) => {
                params.filterForm = formToJson($filter);
            },
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                { title: '', field: 'mark', hozAlign: 'center', resizable: false, headerSort: false, width: 5 },
                { title: 'Позиция', field: 'position' },
                { title: 'Наименование', field: 'name' },
                { title: 'Заместитель', field: 'substituteComponent' },
                { title: 'Производитель', field: 'producer' },
                { title: 'Категория', field: 'category' },
                {
                    title: 'Описание',
                    field: 'description',
                    variableHeight: true,
                    width: 300,
                    minWidth: 300,
                    formatter: cell => {
                        $(cell.getElement()).css({'white-space': 'pre-wrap'});
                        return cell.getValue();
                    }
                }
            ],
            rowClick: () => $btnSelect.toggleClass('disabled', !table.getSelectedRows().length),
            rowDblClick: (e, row) => {
                table.deselectRow();
                row.select();
                $btnSelect.trigger('click');
                table.deselectRow();
            }
        });

        // Кнопка поиска
        $btnSearch.on({
            'click': () => table.setData()
        });
        $filter.enter(() => $btnSearch.trigger('click'));

        // Кнопка очистки фильтров
        $('div.detail_specification_info_spec-add-component__btn-clear-all').on({
            'click': () => formClear($filter)
        });

        // Добавление выбранного изделия
        $btnSelect.on({
            'click': () => {
                const data = table.getSelectedData();
                if (data.length === 1) {
                    let id = data[0].id;
                    $.post({
                        url: '/api/action/prod/product/detail/specification/info/spec-add-component/save',
                        data: { bomId: bomId, componentId: id },
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(bomItemId => {
                        sessionStorage.setItem(ssProduct_selectedComponent + bomId, id);
                        $modal.modal('hide');
                        $specDatatable.trigger('reloadSelectRow', [bomItemId]);
                    });
                }
            }
        });
    });
</script>