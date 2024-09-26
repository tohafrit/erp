<div class="list__header">
    <h1 class="list__header_title">Управление новостями</h1>
    <div class="list__header_buttons">
        <i class="icon filter link list__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        <i class="icon add link blue list__btn-add" title="<fmt:message key="label.button.add"/>"></i>
    </div>
</div>
<div class="list__table-block">
    <div class="list__table-main-block">
        <div class="list__table-wrap">
            <div class="list__filter_main">
                <form:form modelAttribute="newsListFilterForm" cssClass="ui tiny form secondary segment list__filter_form">
                    <div class="field">
                        <div class="ui icon small buttons">
                            <div class="ui button list__filter_btn-search" title="Поиск">
                                <i class="search blue icon"></i>
                            </div>
                            <div class="ui button list__filter_btn-clear-all" title="Очистить фильтр">
                                <i class="times blue icon"></i>
                            </div>
                        </div>
                    </div>
                    <div class="ui four column grid">
                        <div class="column field">
                            <label>Заголовок</label>
                            <form:input path="title" type="search"/>
                        </div>
                        <div class="column field">
                            <label>Анонс</label>
                            <form:input path="previewText" type="search"/>
                        </div>
                        <div class="column field">
                            <label>Содержимое</label>
                            <form:input path="detailText" type="search"/>
                        </div>
                        <div class="column field">
                            <label>Закрепленные</label>
                            <form:checkbox path="topStatus" />
                        </div>
                        <div class="column field">
                            <div class="two fields">
                                <div class="field">
                                    <label><fmt:message key="label.from"/></label>
                                    <div class="ui calendar">
                                        <div class="ui input left icon">
                                            <i class="calendar icon"></i>
                                            <form:input cssClass="std-datetime" path="dateCreatedFrom" type="search"/>
                                        </div>
                                    </div>
                                </div>
                                <div class="field">
                                    <label><fmt:message key="label.to"/></label>
                                    <div class="ui calendar">
                                        <div class="ui input left icon">
                                            <i class="calendar icon"></i>
                                            <form:input cssClass="std-datetime" path="dateCreatedTo" type="search"/>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </form:form>
            </div>
            <div class="list__table table-sm table-striped"></div>
        </div>
    </div>
    <div class="list__table-sub-block">
        <i class="close link blue icon list__btn-close-sub-block"></i>
        <div class="list__sub-block-content"></div>
    </div>
</div>

<script>
    $(() => {
        const $addNews = $('i.list__btn-add');
        const $btnFilter = $('i.list__btn-filter');
        const $btnSearch = $('div.list__filter_btn-search');
        const $filter = $('div.list__filter_main');
        const $filterForm = $('form.list__filter_form');
        const $clearAllButton = $('div.list__filter_btn-clear-all');
        const $subBlock = $('div.list__table-sub-block');
        const $subContent = $('div.list__sub-block-content');
        const $btnCloseSubBlock = $('i.list__btn-close-sub-block');

        // Кнопка фильтра
        $btnFilter.on({
            'click': function() {
                $(this).toggleClass('primary');
                $filter.toggle(!$filter.is(':visible'));
            }
        });

        // Таблица новостей
        const table = new Tabulator('div.list__table', {
            pagination: 'remote',
            layout: 'fitColumns',
            selectable: 1,
            ajaxURL: '/api/action/corp/news-edition/list/load',
            ajaxRequesting: (url, params) => {
                params.filterForm = formToJson($filterForm);
            },
            ajaxSorting: true,
            height: '100%',
            initialSort:[
                { column: 'dateCreated', dir: 'desc' }
            ],
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                { title: 'Заголовок', field: 'title' },
                {
                    title: 'Закреплённая',
                    field: 'topStatus',
                    headerSort: false,
                    width: 100,
                    formatter: cell => cell.getValue() ? 'Да' : ''
                },
                {
                    title: 'Дата создания',
                    field: 'dateCreated',
                    width: 150,
                    formatter: cell => dateTimeStdToString(cell.getValue())
                }
            ],
            rowClick: (e, row) => showDetails(row.getData().id),
            rowContextMenu: row => {
                const menu = [];
                const id = row.getData().id;
                menu.push({
                    label: '<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>',
                    action: () => editNews(id)
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => deleteNews(id)
                });
                return menu;
            },
            dataSorting: () => $subBlock.hide()
        });

        // Ресайз вспомогательного контейнера
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

        // Добавление новости
        $addNews.on({
            'click': () => editNews()
        });

        // Функция раскрытия окна подробностей
        function showDetails(id) {
            $.get({
                url: '/api/view/corp/news-edition/list/detail',
                data: { id: id },
                beforeSend: () => togglePreloader(true),
                complete: () => togglePreloader(false)
            }).done(html => {
                $subContent.html(html);
                $subBlock.show();
            }).fail(() => $subBlock.hide());
        }

        // Функция добавления/редактирования новости
        function editNews(id) {
            $.modalWindow({
                loadURL: '/api/view/corp/news-edition/list/edit',
                loadData: { id: id },
                submitURL: '/api/action/corp/news-edition/list/edit/save',
                onSubmitSuccess: () => table.setData()
            });
        }

        // Функция удаления новости
        function deleteNews(id) {
            confirmDialog({
                title: 'Удаление новости',
                message: 'Вы уверены, что хотите удалить новость?',
                onAccept: () => {
                    $.ajax({
                        method: 'DELETE',
                        url: '/api/action/corp/news-edition/list/delete/' + id,
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => table.setData());
                }
            });
        }

        // Кнопка поиска
        $btnSearch.on({
            'click': () => table.setData()
        });

        // Очистка полей фильтра
        $clearAllButton.on({
            'click': () => formClear($filterForm)
        });
    });
</script>