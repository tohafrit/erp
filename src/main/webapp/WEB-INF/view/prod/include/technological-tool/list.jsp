<div class="list__header">
    <h1 class="list__header_title">Технологическая оснастка и инструмент</h1>
    <div class="list__header_buttons">
        <i class="icon filter link list__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        <i class="icon add link blue list__btn-add" title="<fmt:message key="label.button.add"/>"></i>
    </div>
</div>
<div class="list__table-block">
    <div class="list__table-main-block">
        <div class="list__table-wrap">
            <jsp:include page="/api/view/prod/technological-tool/list/filter"/>
            <div class="list__table table-sm table-striped"></div>
        </div>
    </div>
</div>

<script>
    $(() => {
        const $btnFilter = $('i.list__btn-filter');
        const $btnAdd = $('i.list__btn-add');
        const $btnSearch = $('div.list_filter__btn-search');
        const $filter = $('div.list_filter__main');
        const $filterForm = $('form.list_filter__form');

        // Кнопка фильтра
        $btnFilter.on({
            'click': function() {
                $(this).toggleClass('blue');
                $filter.toggle(!$filter.is(':visible'));
            }
        });

        // Таблица оснастки и инструментов
        const table = new Tabulator('div.list__table', {
            resizableColumns: false,
            ajaxURL: '/api/action/prod/technological-tool/list/load',
            ajaxRequesting: (url, params) => {
                params.filterForm = formToJson($filterForm);
            },
            pagination: 'local',
            height: '100%',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: 'Обозначение', field: 'sign' },
                { title: 'Наименование', field: 'name' },
                { title: 'Тип', field: 'type' },
                { title: 'Назначение', field: 'appointment' },
                {
                    title: 'Ссылка на файл',
                    field: 'link',
                    formatter: cell => cell.getValue().length > 0 ? '<a href="">Посмотреть</a>' : '',
                    cellClick: (e, cell) => window.location.href = cell.getValue()
                },
                { title: 'Состояние', field: 'state' },
                {
                    title: 'Участок',
                    field: 'productionArea',
                    variableHeight: true,
                    minWidth: 400,
                    width: 500,
                    formatter: cell => {
                        $(cell.getElement()).css({'white-space': 'pre-wrap'});
                        return cell.getValue();
                    }
                },
                {
                    title: 'Дата выпуска',
                    field: 'issueDate',
                    formatter: 'stdDate'
                },
                { title: 'Кем выпущен', field: 'user' }
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
                menu.push({
                    label: `<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>`,
                    action: () => editTechnologicalTool(data.id)
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => deleteTechnologicalTool(data.id)
                });
                return menu;
            }
        });

        // Функция добавления/редактирования оснастки и инструмента
        function editTechnologicalTool(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/technological-tool/list/edit',
                loadData: { id: id },
                submitURL: '/api/action/prod/technological-tool/list/edit/save',
                onSubmitSuccess: () => table.setData()
            });
        }

        // Функция удаления оснастки и инструмента
        function deleteTechnologicalTool(id) {
            confirmDialog({
                title: 'Удаление оснастки и инструмента',
                message: 'Вы уверены, что хотите удалить оснастку и инструмент?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: '/api/action/prod/technological-tool/list/delete/' + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setData())
            });
        }

        // Кнопка поиска
        $btnSearch.on({
            'click': () => table.setData()
        });

        // Кнопка добавления оснастки и инструмента
        $btnAdd.on({
            'click': () => editTechnologicalTool()
        });

        $filterForm.enter(() => $btnSearch.trigger('click'))
    });
</script>