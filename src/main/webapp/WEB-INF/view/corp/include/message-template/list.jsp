<div class="list__header">
    <h1 class="list__header_title">Шаблоны почтовых сообщений</h1>
    <div class="list__table_buttons">
        <i class="icon filter link list__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        <i class="icon add link blue list__btn-add" title="Добавить шаблон"></i>
    </div>
</div>
<div class="list__table-block">
    <form:form modelAttribute="messageTemplateListFilterForm" cssClass="ui tiny form secondary segment list_filter__form">
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
                <label>Тип сообщения</label>
                <form:input path="messageTypeName" type="search"/>
            </div>
            <div class="column field">
                <label>Активность</label>
                <form:select cssClass="ui dropdown std-select" path="active">
                    <form:option value=""><fmt:message key="text.notSpecified"/></form:option>
                    <form:option value="true"><fmt:message key="text.yes"/></form:option>
                    <form:option value="false"><fmt:message key="text.no"/></form:option>
                </form:select>
            </div>
            <div class="column field">
                <label>Тема сообщения</label>
                <form:input path="subject" type="search"/>
            </div>
        </div>
    </form:form>
    <div class="list__table table-sm table-striped"></div>
</div>

<script>
    $(() => {
        const $content = $('div.root__content');
        const $table = $('div.list__table');
        const $btnFilter = $('i.list__btn-filter');
        const $filterForm = $('form.list_filter__form');
        const $btnSearch = $('div.list_filter__btn-search');
        const $btnAdd = $('i.list__btn-add');
        const $clearAllButton = $('div.list_filter__btn-clear-all');

        $clearAllButton.on({
            'click': () => formClear($filterForm)
        });

        $filterForm.enter(() => $btnSearch.trigger('click'));

        // Кнопка фильтра
        $btnFilter.on({
            'click': function() {
                $(this).toggleClass('primary');
                $filterForm.toggle(!$filterForm.is(':visible'));
            }
        });

        const table = new Tabulator('div.list__table', {
            pagination: 'remote',
            height: '100%',
            ajaxURL: '/api/action/corp/message-template/list/load',
            ajaxRequesting: (url, params) => {
                params.filterForm = formToJson($filterForm);
            },
            layout: 'fitColumns',
            selectable: 1,
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                {
                    title: 'Тип сообщения',
                    field: 'messageTypeName',
                    variableHeight: true,
                    formatter: cell => {
                        $(cell.getElement()).css({'white-space': 'pre-wrap'});
                        return cell.getValue();
                    }
                },
                {
                    title: 'Статус',
                    field: 'active',
                    hozAlign: 'center',
                    vertAlign: 'middle',
                    formatter: cell => {
                        return booleanToLight(cell.getValue(), { onTrue: 'Активный' , onFalse: 'Архивный' });
                    }
                },
                {
                    title: 'От кого',
                    field: 'emailFrom',
                    hozAlign: 'center',
                    headerSort: false
                },
                {
                    title: 'Кому',
                    field: 'emailTo',
                    hozAlign: 'center',
                    headerSort: false
                },
                {
                    title: 'Тема сообщения',
                    field: 'subject',
                    variableHeight: true,
                    formatter: cell => {
                        $(cell.getElement()).css({'white-space': 'pre-wrap'});
                        return cell.getValue();
                    }
                },
                {
                    title: 'Сообщение',
                    field: 'message',
                    headerSort: false,
                    variableHeight: true,
                    minWidth: 200,
                    width: 300,
                    formatter: cell => {
                        $(cell.getElement()).css({'white-space': 'pre-wrap'});
                        return cell.getValue();
                    }
                },
                {
                    title: 'Копия',
                    field: 'cc',
                    headerSort: false
                },
                {
                    title: 'Скрытая копия',
                    field: 'bcc',
                    headerSort: false
                }
            ],
            rowDblClick: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowContext: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowContextMenu: row => {
                const menu = [];
                const id = row.getData().id;
                menu.push({
                    label: '<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>',
                    action: () => editMessageTemplate(id)
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => deleteMessageTemplate(id)
                });
                return menu;
            }
        });

        // Добавление стилей для переноса слов заголовка
        $table.find('.tabulator-col').css({
            'height': '',
            'word-wrap': 'break-word'
        });
        $table.find('.tabulator-col-title').css({
            'white-space': 'normal',
            'text-overflow': 'clip'
        });

        // Кнопка добавления
        $btnAdd.on({
            'click': () => editMessageTemplate()
        });

        // Функция добавления/редактирования типа сообщения
        function editMessageTemplate(id) {
            $.modalWindow({
                loadURL: '/api/view/corp/message-template/list/edit',
                loadData: { id: id },
                submitURL: '/api/action/corp/message-template/list/edit/save',
                onSubmitSuccess: () => table.setData()
            });
        }

        // Функция удаления типа сообщения
        function deleteMessageTemplate(id) {
            confirmDialog({
                title: 'Удаление шаблона почтового сообщения',
                message: 'Вы уверены, что хотите удалить шаблон почтового сообщения?',
                onAccept: () => {
                    $.ajax({
                        method: 'DELETE',
                        url: '/api/action/corp/message-template/list/delete/' + id,
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
    })
</script>