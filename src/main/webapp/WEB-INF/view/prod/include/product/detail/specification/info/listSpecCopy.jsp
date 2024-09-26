<div class="ui modal detail_specification_info_list-spec-copy__main">
    <div class="header">Копирование спецификации</div>
    <div class="scrolling content">
        <div class="ui icon small button basic detail_specification_info_list-spec-copy__btn-filter" title="Фильтр">
            <i class="filter icon"></i>
        </div>
        <form class="ui tiny form secondary segment detail_specification_info_list-spec-copy__filter-form">
            <div class="field">
                <div class="ui icon small button detail_specification_info_list-spec-copy__btn-search" title="Поиск">
                    <i class="search blue icon"></i>
                </div>
            </div>
            <div class="ui four column grid">
                <div class="column field">
                    <label>
                        Условное наименование
                        <i class="times link blue icon detail_specification_info_list-spec-copy__btn-clear"></i>
                    </label>
                    <input type="text" name="conditionalName">
                </div>
            </div>
        </form>
        <div class="detail_specification_info_list-spec-copy__table"></div>
        <div class="detail_specification_info_list-spec-copy__version">
            Версия: <span class="detail_specification_info_list-spec-copy__version-select"></span>
        </div>
    </div>
    <div class="actions">
        <div class="ui small button disabled detail_specification_info_list-spec-copy__btn-select">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.select"/>
        </div>
    </div>
</div>

<script>
    $(() => {
        const $content = $('div.detail_specification__content');

        const bomId = '${bomId}';
        const $modal = $('div.detail_specification_info_list-spec-copy__main');
        const $filter = $('form.detail_specification_info_list-spec-copy__filter-form');
        const $btnFilter = $('div.detail_specification_info_list-spec-copy__btn-filter');
        const $btnSelect = $('div.detail_specification_info_list-spec-copy__btn-select');
        const $btnSearch = $('div.detail_specification_info_list-spec-copy__btn-search');
        const $clearButtonList = $('i.detail_specification_info_list-spec-copy__btn-clear');

        // Версия
        const $version = $('div.detail_specification_info_list-spec-copy__version');
        const $versionSelect = $('span.detail_specification_info_list-spec-copy__version-select');

        // Кнопка фильтра
        $btnFilter.on({
            'click': function() {
                $(this).toggleClass('primary');
                $filter.toggle(!$filter.is(':visible'));
            }
        });

        const datatable = new Tabulator('div.detail_specification_info_list-spec-copy__table', {
            selectable: 1,
            ajaxSorting: true,
            pagination: 'remote',
            layout: 'fitDataStretch',
            maxHeight: '450px',
            ajaxURL: '/api/action/prod/product/detail/specification/info/list-spec-copy/list-load',
            ajaxRequesting: (url, params) => {
                params.bomId = bomId;
                params.filterForm = formToJson($filter);
            },
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                { title: 'Условное наименование', field: 'conditionalName' },
                {
                    title: 'Комментарий',
                    field: 'comment',
                    variableHeight: true,
                    minWidth: 200,
                    width: 300,
                    formatter: cell => {
                        $(cell.getElement()).css({'white-space': 'pre-wrap'});
                        return cell.getValue();
                    }
                }
            ],
            rowClick: (e, row) => {
                datatable.deselectRow();
                row.select();
                updateVersionList();
                if (datatable.getSelectedRows().length === 0) {
                    $version.hide();
                    $btnSelect.addClass('disabled');
                } else {
                    $version.show();
                    $btnSelect.removeClass('disabled');
                }
            },
            rowDblClick: (e, row) => {
                datatable.deselectRow();
                row.select();
                $version.show();
                $btnSelect.trigger('click');
                updateVersionList();
            }
        });

        // Кнопка поиска
        $btnSearch.on({
            'click': () => datatable.setData()
        });

        // Функция фильтра очистки поля или селекта от значений
        $clearButtonList.on({
            'click': function() {
                const $field = $(this).closest('div.field').find('input[type="text"], select');
                if ($field.is('input')) {
                    $field.val('');
                } else if ($field.is('select')) {
                    $field.dropdown('clear');
                }
            }
        });

        // Выбор изделия
        $btnSelect.on({
            'click': () => {
                const data = datatable.getSelectedData();
                if (data.length === 1) {
                    confirmDialog({
                        title: 'Статусы замен',
                        message: 'Вы хотите скопировать статус замен?',
                        buttonTextReject: 'Нет',
                        onAccept: () => processCopyVersion(true),
                        onReject: () => processCopyVersion(false)
                    });
                }
            }
        });

        // Функция копирования версии
        function processCopyVersion(isReplacementStatusCopy) {
            $.post({
                url: '/api/action/prod/product/detail/specification/info/list-spec-copy/save',
                data: {
                    copyBomId: $versionSelect.find('select').val(),
                    bomId: bomId,
                    isReplacementStatusCopy: isReplacementStatusCopy
                },
                beforeSend: () => togglePreloader(true),
                complete: () => togglePreloader(false)
            }).done(() => {
                $modal.modal('hide');
                $content.trigger('load-list');
            });
        }

        // Обновить список версия для выбранного изделия
        function updateVersionList() {
            const data = datatable.getSelectedData();
            $.get({
                url: '/api/action/prod/product/detail/specification/info/list-spec-copy/version-list',
                data: { productId: data[0].id },
                beforeSend: () => togglePreloader(true),
                complete: () => togglePreloader(false)
            }).done(data => {
                let select = '<select class="ui tiny label dropdown">';
                $.each(data, (k, v) => {
                    select += '<option value="' + v.id + '">' + v.value + '</option>';
                });
                select += '</select>';
                $versionSelect.html(select).find('.ui.dropdown').dropdown({
                    direction: 'upward'
                });
            });
        }

        $filter.enter(() => $btnSearch.trigger('click'));
    })
</script>