<div class="list_comment__header">
    <h1 class="list_comment__header_title">Комментарии</h1>
    <div class="list_comment__table_buttons">
        <i class="icon filter link list_comment__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        <i class="icon add link blue list_comment__btn-add" title="<fmt:message key="label.button.add"/>"></i>
    </div>
</div>
<div class="list_comment__table-wrap">
    <form class="ui tiny form secondary segment list_comment__filter-form">
        <div class="field">
            <div class="ui icon small buttons">
                <div class="ui button list_comment__btn-filter-search" title="Поиск">
                    <i class="search blue icon"></i>
                </div>
                <div class="ui button list_comment__btn-filter-clear" title="Очистить фильтр">
                    <i class="times blue icon"></i>
                </div>
            </div>
        </div>
        <div class="ui three column grid">
            <div class="column field">
                <label>Комментарий</label>
                <div class="ui input std-div-input-search icon">
                    <input type="text" name="comment">
                </div>
            </div>
            <div class="column field">
                <div class="two fields">
                    <div class="field">
                        <label>Дата и время создания с</label>
                        <div class="ui calendar">
                            <div class="ui input left icon std-div-input-search">
                                <i class="calendar icon"></i>
                                <input type="text" class="std-date" name="createDateFrom">
                            </div>
                        </div>
                    </div>
                    <div class="field">
                        <label><fmt:message key="label.to"/></label>
                        <div class="ui calendar">
                            <div class="ui input left icon std-div-input-search">
                                <i class="calendar icon"></i>
                                <input type="text" class="std-date" name="createDateTo">
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="column field">
                <label>Автор</label>
                <select class="ui dropdown search std-select" name="createdBy">
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                    <c:forEach items="${userList}" var="user">
                        <option value="${user.id}">${user.userOfficialName}</option>
                    </c:forEach>
                </select>
            </div>
        </div>
    </form>
    <div class="list_comment__table table-sm table-striped"></div>
</div>

<script>
    $(() => {
        const componentId = '${componentId}';
        const $filter = $('form.list_comment__filter-form');
        const $btnFilter = $('i.list_comment__btn-filter');
        const $btnAdd = $('i.list_comment__btn-add');
        const $btnFilterSearch = $('div.list_comment__btn-filter-search');
        const $btnFilterClear = $('div.list_comment__btn-filter-clear');

        // Кнопка фильтра
        $btnFilter.on({
            'click': function() {
                $(this).toggleClass('blue');
                $filter.toggle(!$filter.is(':visible'));
            }
        });

        const table = new Tabulator('div.list_comment__table', {
            selectable: 1,
            pagination: 'remote',
            initialSort: [{ column: TABR_FIELD.CREATE_DATE, dir: SORT_DIR_DESC }],
            ajaxSorting: true,
            height: '100%',
            ajaxURL: '/api/action/prod/component/list/comment/load',
            ajaxRequesting: (url, params) => {
                params.componentId = componentId;
                params.filterData = formToJson($filter);
            },
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                TABR_COL_ID,
                {
                    title: 'Комментарий',
                    field: TABR_FIELD.COMMENT,
                    variableHeight: true,
                    minWidth: 200,
                    width: 300,
                    formatter: 'textarea'
                },
                {
                    title: 'Дата и время создания',
                    field: TABR_FIELD.CREATE_DATE,
                    formatter: 'stdDatetime'
                },
                { title: 'Автор', field: TABR_FIELD.CREATED_BY }
            ],
            rowClick: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowContext: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowContextMenu: row => {
                const menu = [];
                const data = row.getData();
                const id = data.id;
                menu.push({
                    label: `<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>`,
                    action: () => editComment(id)
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => deleteComment(id)
                });
                return menu;
            }
        });

        // Скролл до строки и ее выбор по id
        function rowScrollSelect(id) {
            table.selectRow(id);
            table.scrollToRow(id, 'middle', false);
        }

        // Функция добавления/редактирования
        function editComment(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/component/list/comment/edit',
                loadData: { componentId: componentId, id: id },
                submitAsJson: true,
                submitURL: '/api/action/prod/component/list/comment/edit/save',
                onSubmitSuccess: resp => {
                    if (id) {
                        table.setPage(table.getPage()).then(() => rowScrollSelect(id));
                    } else {
                        const id = resp.attributes.id;
                        if (id) {
                            formClear($filter);
                            table.setSort(TABR_SORT_ID_DESC);
                            table.setPage(1).then(() => rowScrollSelect(id));
                        }
                    }
                }
            });
        }

        // Функция удаления
        function deleteComment(id) {
            confirmDialog({
                title: 'Удаление комментария',
                message: 'Вы действительно хотите удалить комментарий?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: '/api/action/prod/component/list/comment/delete/' + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setPage(table.getPage()))
            });
        }

        // Очистка фильтра
        $btnFilterClear.on({
            'click': () => formClear($filter)
        });

        // Поиск по фильтру
        $btnFilterSearch.on({
            'click': () => table.setData()
        });
        $filter.enter(() => $btnFilterSearch.trigger('click'));

        // Кнопка добавления
        $btnAdd.on({
            'click': () => editComment()
        });
    });
</script>