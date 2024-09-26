<div class="detail_products__main">
    <div class="detail_products__header">
        <h1 class="detail_products__header_title">
            Изделия
            <c:if test="${isAdditionalAgreement}">
                дополнительного соглашения № ${contractSectionNumber}
            </c:if>
        </h1>
        <div class="detail_products__header_buttons">
            <div class="ui icon small buttons">
                <div class="ui button basic detail_products__btn-filter" title="<fmt:message key="label.button.filter"/>">
                    <i class="icon filter"></i>
                </div>
            </div>
            <div class="ui icon small buttons">
                <div class="ui button basic detail_products__btn-expand" title="Развернуть">
                    <i class="expand alternate icon"></i>
                </div>
                <div class="ui button basic detail_products__btn-compress" title="Свернуть">
                    <i class="compress alternate icon"></i>
                </div>
            </div>
        </div>
    </div>
    <div class="detail_products__table-block">
    <form:form modelAttribute="contractProductsFilterForm" cssClass="ui tiny form secondary segment detail_products_filter__form">
        <div class="field">
            <div class="ui icon small buttons">
                <div class="ui button detail_products_filter__btn-search"
                     title="<fmt:message key="label.button.search"/>">
                    <i class="search blue icon"></i>
                </div>
                <div class="ui button detail_products_filter__btn-clear-all"
                     title="<fmt:message key="label.button.clearFilter"/>">
                    <i class="times blue icon"></i>
                </div>
            </div>
        </div>
        <div class="ui two column grid">
            <div class="column field">
                <label>Условное наименование изделия</label>
                <form:input path="conditionalName" type="search"/>
            </div>
            <div class="column field">
                <div class="two fields">
                    <div class="field">
                        <label>Дата поставки с</label>
                        <div class="ui calendar">
                            <div class="ui input left icon">
                                <i class="calendar icon"></i>
                                <form:input cssClass="std-date" path="deliveryDateFrom" type="search"/>
                            </div>
                        </div>
                    </div>
                    <div class="field">
                        <label><fmt:message key="label.to"/></label>
                        <div class="ui calendar">
                            <div class="ui input left icon">
                                <i class="calendar icon"></i>
                                <form:input cssClass="std-date" path="deliveryDateTo" type="search"/>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </form:form>
        <div class="detail_products__table-main-block">
            <div class="detail_products__table-wrap">
                <div class="detail_products__table"></div>
            </div>
        </div>
        <div class="detail_products__table-sub-block">
            <i class="close link blue icon detail_products__btn-close-sub-block"></i>
            <div class="detail_products__sub-block-content"></div>
        </div>
    </div>

    <script>
        $(() => {
            const contractSectionId = '${contractSectionId}';
            //
            const $menuTree = $('ul.detail__menu_tree');
            const $btnFilter = $('div.detail_products__btn-filter');
            const $filterForm = $('form.detail_products_filter__form');
            const $btnSearch = $('div.detail_products_filter__btn-search');
            const $btnExpand = $('div.detail_products__btn-expand');
            const $btnCompress = $('div.detail_products__btn-compress');
            const $subBlock = $('div.detail_products__table-sub-block');
            const $subContent = $('div.detail_products__sub-block-content');
            const $btnCloseSubBlock = $('i.detail_products__btn-close-sub-block');
            const $clearAllButton = $('div.detail_products_filter__btn-clear-all');
            const $productsMenu = $menuTree.find('li.detail__menu_products[data-id=${contractSectionId}]');

            // Очистка полей фильтра
            $clearAllButton.on({
                'click': () => formClear($filterForm)
            });

            // Поиск по кнопки enter
            $filterForm.enter(() => $btnSearch.trigger('click'));

            // Кнопка фильтра
            $btnFilter.on({
                'click': function() {
                    $(this).toggleClass('primary');
                    $filterForm.toggle(!$filterForm.is(':visible'));
                }
            });

            const table = new Tabulator('div.detail_products__table', {
                ajaxURL: '/api/action/prod/contract/detail/products/load',
                ajaxRequesting: (url, params) => {
                    params.filterForm = formToJson($filterForm);
                    params.contractSectionId = contractSectionId;
                },
                selectable: 1,
                headerSort: false,
                height: '100%',
                groupBy: 'groupMain',
                groupToggleElement: 'header',
                layout: 'fitColumns',
                groupHeader: [
                    value => {
                        return '<span style="color:#315c83;">' + value + '</span>';
                    }
                ],
                columns: [
                    TABR_COL_LOCAL_ROW_NUM,
                    {
                        title: 'Кол-во',
                        field: 'amount',
                        hozAlign: 'center',
                        resizable: false
                    },
                    {
                        title: 'Дата поставки',
                        field: 'deliveryDate',
                        hozAlign: 'center',
                        formatter: cell => dateStdToString(cell.getValue())
                    },
                    {
                        title: 'Спец. провека',
                        field: 'specialTestType',
                        hozAlign: 'center',
                        resizable: false
                    },
                    {
                        title: 'Запущено',
                        field: 'launchAmount',
                        hozAlign: 'center'
                    },
                    {
                        title: 'Отгружено',
                        field: 'shippedAmount',
                        hozAlign: 'center'
                    }
                ],
                rowClick: (e, row) => {
                    table.deselectRow();
                    row.select();
                    showDistribution(row.getData().id, $subBlock.is(':hidden'));
                }
            });

            // Кнопка свернуть группу
            $btnCompress.on({
                'click': () => table.getGroups().forEach(group => group.hide())
            });
            // Кнопка развернуть группу
            $btnExpand.on({
                'click': () => table.getGroups().forEach(group => group.show())
            });

            // Резсайз вспомогательного контейнера
            $subBlock.resizable({
                autoHide: true,
                handles: 'n',
                ghost: true,
                stop: () => {
                    $subBlock.css({
                        'width': '100%',
                        'top': 0
                    });
                }
            });

            // Скрытие области вспомогательного контейнера
            $btnCloseSubBlock.on({
                'click': () => {
                    table.deselectRow();
                    $subBlock.hide();
                }
            });

            // Функция отображения распределения изделий поставки
            function showDistribution(id, scroll) {
                $.get({
                    url: '/api/view/prod/contract/detail/products/distribution',
                    data: { lotId: id , scroll: scroll },
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(html => {
                    $subContent.html(html);
                    $subBlock.show();
                }).fail(() => $subBlock.hide());
            }

            // Кнопка поиска
            $btnSearch.on({
                'click': () => table.setData()
            });
        })
    </script>
</div>