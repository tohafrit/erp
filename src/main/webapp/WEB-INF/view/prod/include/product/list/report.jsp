<div class="ui modal list_report__modal">
    <div class="header">
        <c:if test="${reportType eq 1}">
            Закупочные спецификации для запуска
        </c:if>
        <c:if test="${reportType eq 2}">
            Пропущенные замены
        </c:if>
        <c:if test="${reportType eq 3}">
            Статус допустимых замен
        </c:if>
    </div>
    <div class="content">
        <%-- Закупочные спецификации для запуска --%>
        <c:if test="${reportType eq 1}">
            <i class="icon filter link blue list_report__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
            <i class="icon sync alternate link blue list_report__btn-refresh" title="Обновить"></i>
            <i class="icon file excel outline link blue list_report__btn-excel" title="Выгрузить в excel"></i>
            <i class="icon print link blue list_report__btn-print" title="Печать"></i>
            <div class="list_report__table-block">
                <form class="ui tiny form secondary segment list_report__filter-form">
                    <div class="field">
                        <div class="ui icon small buttons">
                            <div class="ui button list_report__btn-search" title="Поиск">
                                <i class="search blue icon"></i>
                            </div>
                        </div>
                    </div>
                    <div class="ui three column grid">
                        <div class="column field">
                            <label>Запуск</label>
                            <select class="ui dropdown search label std-select" name="launchId">
                                <c:forEach items="${launchList}" var="launch">
                                    <option value="${launch.id}">${launch.numberInYear}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="column field">
                            <label>Показывать ЗС</label>
                            <select class="ui dropdown search label std-select" name="typeId">
                                <c:forEach items="${typeList}" var="type">
                                    <option value="${type.id}">${type.value}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                </form>
                <div class="list_report__table table-sm table-striped"></div>
            </div>
        </c:if>
        <%-- Пропущенные замены --%>
        <c:if test="${reportType eq 2}">
            <i class="icon filter link blue list_report__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
            <i class="icon sync alternate link blue list_report__btn-refresh" title="Обновить"></i>
            <i class="icon file excel outline link blue list_report__btn-excel" title="Выгрузить в excel"></i>
            <i class="icon print link blue list_report__btn-print" title="Печать"></i>
            <div class="list_report__table-block">
                <form class="ui tiny form secondary segment list_report__filter-form">
                    <div class="field">
                        <div class="ui icon small buttons">
                            <div class="ui button list_report__btn-search" title="Поиск">
                                <i class="search blue icon"></i>
                            </div>
                        </div>
                    </div>
                    <div class="ui three column grid">
                        <div class="column field">
                            <label>Начало периода</label>
                            <div class="ui calendar">
                                <div class="ui input left icon">
                                    <i class="calendar icon"></i>
                                    <input type="text" class="std-date" name="startDate" value="<javatime:format value='${startDate}' pattern='dd.MM.yyyy'/>"/>
                                </div>
                            </div>
                        </div>
                        <div class="column field">
                            <label>Окончание периода</label>
                            <div class="ui calendar">
                                <div class="ui input left icon">
                                    <i class="calendar icon"></i>
                                    <input type="text" class="std-date" name="endDate" value="<javatime:format value='${endDate}' pattern='dd.MM.yyyy'/>"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
                <div class="list_report__table table-sm table-striped"></div>
            </div>
        </c:if>
        <%-- Статус допустимых замен --%>
        <c:if test="${reportType eq 3}">
            <i class="icon filter link blue list_report__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
            <i class="icon sync alternate link blue list_report__btn-refresh" title="Обновить"></i>
            <i class="icon file excel outline link blue list_report__btn-excel" title="Выгрузить в excel"></i>
            <i class="icon print link blue list_report__btn-print" title="Печать"></i>
            <div class="list_report__table-block">
                <form class="ui tiny form secondary segment list_report__filter-form">
                    <div class="field">
                        <div class="ui icon small buttons">
                            <div class="ui button list_report__btn-search" title="Поиск">
                                <i class="search blue icon"></i>
                            </div>
                        </div>
                    </div>
                    <div class="ui three column grid">
                        <div class="column field">
                            <label>Начало периода</label>
                            <div class="ui calendar">
                                <div class="ui input left icon">
                                    <i class="calendar icon"></i>
                                    <input type="text" class="std-date" name="startDate" value="<javatime:format value='${startDate}' pattern='dd.MM.yyyy'/>"/>
                                </div>
                            </div>
                        </div>
                        <div class="column field">
                            <label>Окончание периода</label>
                            <div class="ui calendar">
                                <div class="ui input left icon">
                                    <i class="calendar icon"></i>
                                    <input type="text" class="std-date" name="endDate" value="<javatime:format value='${endDate}' pattern='dd.MM.yyyy'/>"/>
                                </div>
                            </div>
                        </div>
                        <div class="column field">
                            <label>Статус</label>
                            <select class="ui dropdown search label std-select" name="statusIdList" multiple>
                                <c:forEach items="${statusList}" var="status">
                                    <option value="${status.id}">${status.description}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                </form>
                <div class="list_report__table table-sm table-striped"></div>
            </div>
        </c:if>
    </div>
</div>

<script>
    $(() => {
        const $main = $('div.list_report__modal');
        const $filterForm = $('form.list_report__filter-form');
        //
        const $btnRefresh = $('i.list_report__btn-refresh');
        const $btnSearch = $('div.list_report__btn-search');
        const $btnFilter = $('i.list_report__btn-filter');
        const $btnPrint = $('i.list_report__btn-print');
        const $btnExcel = $('i.list_report__btn-excel');
        let table;
        //let excelTitle;

        <%-- Закупочные спецификации для запуска --%>
        <c:if test="${reportType eq 1}">
            table = new Tabulator('div.list_report__table', {
                ajaxURL: '/api/action/prod/product/list/report/load',
                ajaxRequesting: (url, params) => {
                    params.reportType = 1;
                    params.filterForm = formToJson($filterForm);
                },
                height: 'calc(100vh * 0.5)',
                printHeader: () => {
                    let launchName = $main.find('select[name=launchId] > option:selected').text();
                    launchName = launchName == null ? '' : launchName;
                    return '<h4>Закупочные спецификации для запуска ' + launchName + '</h4>';
                },
                columns: [
                    TABR_COL_LOCAL_ROW_NUM,
                    { title: 'Изделие', field: 'productName', headerSort: false },
                    { title: 'Разработчик', field: 'developer', headerSort: false },
                    { title: 'Версия', field: 'version', headerSort: false },
                    { title: 'Утверждена к запуску', field: 'approvedToLaunch', headerSort: false }
                ]
            });

            // Восстановление модалки после печати
            $(window).on({
                'afterprint': () => $main.addClass('visible active')
            });
        </c:if>

        <%-- Пропущенные замены --%>
        <c:if test="${reportType eq 2}">
            table = new Tabulator('div.list_report__table', {
                ajaxURL: '/api/action/prod/product/list/report/load',
                ajaxRequesting: (url, params) => {
                    params.reportType = 2;
                    params.filterForm = formToJson($filterForm);
                },
                height: 'calc(100vh * 0.5)',
                layout: 'fitColumns',
                columns: [
                    {
                        title: '№ пп',
                        resizable: false,
                        headerSort: false,
                        frozen: true,
                        width: 50,
                        formatter: 'rownum'
                    },
                    { title: 'Поз. комп. по КД', field: 'cell', headerSort: false },
                    { title: 'Наим. комп. по КД', field: 'name', formatter: 'textarea', headerSort: false },
                    { title: 'Описание комп. по КД', field: 'description', formatter: 'textarea', headerSort: false },
                    { title: 'Поз. комп. по замене', field: 'purCell', headerSort: false },
                    { title: 'Наим. комп. по замене', field: 'purName', formatter: 'textarea', headerSort: false },
                    { title: 'Описание комп. по замене', field: 'purDescription', formatter: 'textarea', headerSort: false },
                    { title: 'Наименование изделия', field: 'productName', formatter: 'textarea', headerSort: false },
                    { title: 'Версия спецификации', field: 'version', headerSort: false },
                    { title: 'Утверждение', field: 'launches', headerSort: false }
                ]
            });
            //excelTitle = 'Проверка допустимых замен по закупке';
            /*{
                const startDate = $main.find('input[name=startDate]').val();
                const endDate = $main.find('input[name=endDate]').val();
                if (startDate != null && endDate != null) {
                    excelTitle += ' с ' + startDate + ' по ' + endDate;
                }
            }*/
        </c:if>

        <%-- Статус допустимых замен --%>
        <c:if test="${reportType eq 3}">
            table = new Tabulator('div.list_report__table', {
                ajaxURL: '/api/action/prod/product/list/report/load',
                ajaxRequesting: (url, params) => {
                    params.reportType = 3;
                    params.filterForm = formToJson($filterForm);
                },
                height: 'calc(100vh * 0.5)',
                layout: 'fitColumns',
                columns: [
                    {
                        title: '№ пп',
                        resizable: false,
                        headerSort: false,
                        frozen: true,
                        width: 50,
                        formatter: 'rownum'
                    },
                    { title: 'Поз. комп. по КД', field: 'cell', headerSort: false },
                    { title: 'Наим. комп. по КД', field: 'name', formatter: 'textarea', headerSort: false },
                    { title: 'Описание комп. по КД', field: 'description', formatter: 'textarea', headerSort: false },
                    { title: 'Поз. комп. по замене', field: 'purCell', headerSort: false },
                    { title: 'Наим. комп. по замене', field: 'purName', formatter: 'textarea', headerSort: false },
                    { title: 'Описание комп. по замене', field: 'purDescription', formatter: 'textarea', headerSort: false },
                    { title: 'Ведущий', field: 'developer', headerSort: false },
                    { title: 'Наименование изделия', field: 'productName', formatter: 'textarea', headerSort: false },
                    { title: 'Версия спецификации', field: 'version', headerSort: false },
                    { title: 'Утверждение', field: 'approved', headerSort: false },
                    { title: 'Принятие', field: 'accepted', headerSort: false },
                    { title: 'Статус', field: 'status', headerSort: false }
                ]
            });
            //excelTitle = 'Статус введенных допустимых замен';
            /*{
                const startDate = $main.find('input[name=startDate]').val();
                const endDate = $main.find('input[name=endDate]').val();
                if (startDate != null && endDate != null) {
                    excelTitle += ' с ' + startDate + ' по ' + endDate;
                    const $statusOptions = $main.find('select[name=statusIdList] > option:selected');
                    if ($statusOptions.length) {
                        excelTitle += '(';
                        const statuses = [];
                        $statusOptions.each((ind, el) => statuses.push($(el).text()));
                        excelTitle += statuses.join(', ');
                        excelTitle += ')';
                    }
                }
            }*/
        </c:if>

        // Кнопки поиска и фильтра
        $btnRefresh.on({
            'click': () => table.setData()
        });
        $btnSearch.on({
            'click': () => {
                $btnFilter.trigger('click');
                table.setData();
            }
        });
        $btnFilter.on({
            'click': function() {
                $(this).toggleClass('primary');
                $filterForm.toggle(!$filterForm.is(':visible'));
            }
        });

        // Кнопка печати
        $btnPrint.on({
            'click': () => {
                // Скрытие модалки
                $main.removeAttr('style');
                $main.removeClass('visible active');
                table.print();
            }
        });

        // Кнопка выгрузки в excel
        $btnExcel.on({
            'click': () => table.download('xlsx', 'report.xlsx', { sheetName: 'отчет' })
        });
    })
</script>