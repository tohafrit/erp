<div class="detail_specification_info__version-info">
    <div>
        <b>Идентификатор:</b>
        ${bomDescriptor}
    </div>
    <c:if test="${not empty prefix}">
        <div>
            <b>Префикс:</b>
            ${prefix}
        </div>
    </c:if>
    <c:if test="${not empty bomProductionName}">
        <div>
            <b>Производственное наименование:</b>
            ${bomProductionName}
        </div>
    </c:if>
</div>
<div class="detail_specification_info__table-button-block">
    <div class="detail_specification_info__spec-table-buttons">
        <i class="icon filter link blue detail_specification_info__btn-filter" title="Фильтр"></i>
        <i class="icon add link blue detail_specification_info__btn-spec-add-component" title="Добавить компонент в спецификацию"></i>
        <i class="icon plus square outline link blue detail_specification_info__btn-spec-add-new-component" title="Добавить новый компонент"></i>
    </div>
    <div class="detail_specification_info__spec-buttons">
        <%--<i class="icon circle link detail_specification_info__spec-buttons_stock"></i>--%>
        <i class="icon check square outline link blue detail_specification_info__spec-buttons_approve" title="Утвердить к запуску"></i>
        <i class="icon file outline link blue detail_specification_info__spec-buttons_accept" title="Принять к запуску"></i>
        <i class="icon broom link blue detail_specification_info__spec-buttons_clear" title="Очистить спецификацию"></i>
        <i class="icon copy outline link blue detail_specification_info__spec-buttons_copy" title="Заполнить на основании другой спецификации"></i>
        <i class="icon not equal link blue detail_specification_info__spec-buttons_compare" title="Сравнить спецификации"></i>
    </div>
    <div class="detail_specification_info__spec-buttons">
        <i class="icon file excel outline link blue detail_specification_info__btn-excel-export" title="Выгрузить в excel"></i>
        <c:if test="${hasImport}">
            <i class="icon angle double right link blue detail_specification_info__import-detail" title="Показать информацию по выгрузке"></i>
        </c:if>
        <i class="icon file import link blue detail_specification_info__btn-excel-import" title="Загрузить из excel"></i>
    </div>
    <input type="file" name="import" class="detail_specification_info__input-excel-import"/>
    <div class="detail_specification_info__launch-block">
        <b>Запуск</b>
        <div class="detail_specification_info__launch-select">
            <select class="ui mini fluid dropdown search">
                <c:forEach items="${launchList}" var="launch">
                    <option value="${launch.id}">${launch.numberInYear}</option>
                </c:forEach>
            </select>
        </div>
    </div>
</div>
<div class="detail_specification_info__spec-table-block">
    <div class="detail_specification_info__spec-table-main-block">
        <div class="detail_specification_info__spec-table-wrap">
            <div class="detail_specification_info__spec-table table-sm table-striped"></div>
        </div>
    </div>
    <div class="detail_specification_info__spec-table-sub-block">
        <i class="close link blue icon detail_specification_info__btn-close-sub-block"></i>
        <div class="detail_specification_info__sub-block-content"></div>
    </div>
</div>

<script>
    $(() => {
        const $content = $('div.detail_specification__content');

        const bomId = '${bomId}';
        const bomIsStock = '${bomIsStock}' === 'true';
        const $subBlock = $('div.detail_specification_info__spec-table-sub-block');
        const $subContent = $('div.detail_specification_info__sub-block-content');
        const $btnCloseSubBlock = $('i.detail_specification_info__btn-close-sub-block');
        const $btnSpecAddComponent = $('i.detail_specification_info__btn-spec-add-component');
        const $btnSpecAddNewComponent = $('i.detail_specification_info__btn-spec-add-new-component');
        const $btnStock = $('i.detail_specification_info__spec-buttons_stock');
        const $datatable = $('div.detail_specification_info__spec-table');
        const $launchSelect = $('div.detail_specification_info__launch-select > select');
        // Фильтр
        const $btnFilter = $('i.detail_specification_info__btn-filter');
        // Excel
        const $btnExcel = $('i.detail_specification_info__btn-excel-export');
        const $btnImport = $('i.detail_specification_info__btn-excel-import');
        const $inputImport = $('input.detail_specification_info__input-excel-import');
        const $btnImportDetail = $('i.detail_specification_info__import-detail');
        // Diff
        const $btnClear = $('i.detail_specification_info__spec-buttons_clear'); // Очистка
        const $btnCopy = $('i.detail_specification_info__spec-buttons_copy'); // Копирование
        const $btnCompare = $('i.detail_specification_info__spec-buttons_compare'); // Сравнение
        const $btnApprove = $('i.detail_specification_info__spec-buttons_approve'); // Утвердить/снять утверждение
        const $btnAccept = $('i.detail_specification_info__spec-buttons_accept'); // Принять
        let filterData = {};

        // Кнопка утверждения
        $btnApprove.data('state', false);
        $btnApprove.on({
            'click': e => {
                const isApproved = $(e.currentTarget).data('state');
                confirmDialog({
                    title: isApproved ? 'Отмена утверждения спецификации к запуску' : 'Утверждение спецификации',
                    message: 'Вы уверены, что хотите ' + (isApproved ? 'отменить утверждение спецификации к запуску?' : 'утвердить спецификацию?'),
                    onAccept: () => $.post({
                        url: '/api/action/prod/product/detail/specification/info/list-spec-approved',
                        data: {
                            bomId: bomId,
                            launchId: $launchSelect.find(':selected').val(),
                            isApproved: isApproved
                        },
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => $('a.detail__menu_specification').trigger('click'))
                });
            }
        });

        // Кнопка принятия
        $btnAccept.data('state', false);
        $btnAccept.on({
            'click': e => {
                const isAccepted = $(e.currentTarget).data('state');
                confirmDialog({
                    title: isAccepted ? 'Отмена принятия спецификации к запуску' : 'Принятие спецификации',
                    message: 'Вы уверены, что хотите ' + (isAccepted ? 'отменить принятие спецификации к запуску?' : 'принять спецификацию?'),
                    onAccept: () => $.post({
                        url: '/api/action/prod/product/detail/specification/info/list-spec-accept',
                        data: {
                            bomId: bomId,
                            launchId: $launchSelect.find(':selected').val(),
                            isAccepted: isAccepted
                        },
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => $('a.detail__menu_specification').trigger('click'))
                });
            }
        });

        // Список запусков
        $launchSelect.dropdown({
            forceSelection: false,
            fullTextSearch: true,
            message: { noResults: '' }
        });
        $launchSelect.on({
            'change': () => $.get({
                url: '/api/action/prod/product/detail/specification/info/check-state-by-launch',
                data: {
                    bomId: bomId,
                    launchId: $launchSelect.find(':selected').val()
                },
                beforeSend: () => togglePreloader(true),
                complete: () => togglePreloader(false)
            }).done(state => {
                if (state.bomApproved) {
                    $btnApprove.attr('title', 'Отменить утверждение к запуску');
                    $btnApprove.addClass('check');
                } else {
                    $btnApprove.attr('title', 'Утвердить к запуску');
                    $btnApprove.removeClass('check');
                }
                if (state.bomAccept) {
                    $btnAccept.attr('title', 'Отменить принятие к запуску');
                    $btnAccept.removeClass('outline');
                } else {
                    $btnAccept.attr('title', 'Принять к запуску');
                    $btnAccept.addClass('outline');
                }
                $btnApprove.data('state', state.bomApproved);
                $btnAccept.data('state', state.bomAccept);
            })
        });
        $launchSelect.trigger('change');

        // Кнопка импорта
        $btnImport.on({
            'click': () => $inputImport.trigger('click')
        });
        $inputImport.on({
            'change': e => {
                const data = new FormData();
                data.append('excel', e.target.files[0]);
                data.append('bomId', bomId);
                $.ajax({
                    url: '/api/action/prod/product/detail/specification/info/excel-import',
                    type: 'POST',
                    data: data,
                    processData: false,
                    contentType: false,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => $content.trigger('load-list')).always(() => $inputImport.val(''));
            }
        });
        $btnImportDetail.on({
            'click': () => $.modalWindow({
                loadURL: '/api/view/prod/product/detail/specification/info/excel-import-detail',
                loadData: { bomId: bomId }
            })
        });

        // Кнопка очистки
        $btnClear.on({
            'click': () => {
                confirmDialog({
                    title: 'Очистка спецификации',
                    message: 'Вы уверены, что хотите очистить спецификацию?',
                    onAccept: () => $.ajax({
                        method: 'DELETE',
                        url: '/api/action/prod/product/detail/specification/info/list-spec-clear/' + bomId,
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => $content.trigger('load-list'))
                });
            }
        });

        // Кнопка копирования
        $btnCopy.on({
            'click': () => $.modalWindow({
                loadURL: '/api/view/prod/product/detail/specification/info/list-spec-copy',
                loadData: { bomId: bomId }
            })
        });

        // Кнопка сравнения
        $btnCompare.on({
            'click': () => $.modalWindow({
                loadURL: '/api/view/prod/product/detail/specification/info/comparison',
                loadData: { bomId: bomId }
            })
        });

        // Кнопка установки задела для версии
        $btnStock.data('state', bomIsStock);
        $btnStock.addClass(bomIsStock ? 'green' : 'red');
        $btnStock.attr('title', bomIsStock ? 'Использовать задел' : 'Не использовать задел');
        $btnStock.on({
            'click': () => $.get({
                url: '/api/action/prod/product/detail/specification/info/version-stock',
                data: {
                    bomId: bomId,
                    state: $btnStock.data('state')
                },
                beforeSend: () => togglePreloader(true),
                complete: () => togglePreloader(false)
            }).done(state => {
                $btnStock.data('state', state);
                $btnStock.toggleClass('green red');
                $btnStock.attr('title', state ? 'Использовать задел' : 'Не использовать задел');
            })
        });

        const table = new Tabulator('div.detail_specification_info__spec-table', {
            ajaxURL: '/api/action/prod/product/detail/specification/info/list-spec-load',
            ajaxRequesting: (url, params) => {
                params.filterData = JSON.stringify(filterData);
                params.bomId = bomId;
            },
            ajaxSorting: true,
            height: '100%',
            groupBy: TABR_FIELD.CATEGORY,
            groupToggleElement: false,
            layout: 'fitDataFill',
            groupHeader: value => value,
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                {
                    title: 'Позиция',
                    field: TABR_FIELD.POSITION,
                    frozen: true,
                    resizable: false,
                    width: 100
                },
                {
                    title: 'Наименование',
                    field: TABR_FIELD.NAME,
                    formatter: cell => {
                        const $cell = $(cell.getElement());
                        const row = cell.getRow();
                        const data = row.getData();
                        if (!data.position && !data.approved) $cell.css({ 'background-color': '#c2d6b8' });
                        return cell.getValue();
                    }
                },
                {
                    title: 'Описание',
                    field: TABR_FIELD.DESCRIPTION,
                    variableHeight: true,
                    width: 300,
                    minWidth: 300,
                    formatter: 'textarea'
                },
                { title: 'Количество', field: TABR_FIELD.QUANTITY },
                { title: 'Категория', field: TABR_FIELD.CATEGORY, visible: false },
                {
                    title: 'Поз.обоз.',
                    field: TABR_FIELD.DESIGNATION,
                    variableHeight: true,
                    formatter: 'textarea'
                },
                { title: 'Прошивка', field: TABR_FIELD.FIRMWARE },
                {
                    title: 'Давальческое',
                    field: TABR_FIELD.GIVEN_RAW_MATERIAL,
                    resizable: false,
                    headerSort: false,
                    hozAlign: 'center',
                    vertAlign: 'middle',
                    formatter: 'lightMark',
                    download: false
                },
                {
                    title: 'Изготовители',
                    field: TABR_FIELD.PRODUCERS,
                    variableHeight: true,
                    width: 300,
                    formatter: 'textarea'
                }
            ],
            rowClick: (e, row) => {
                if (row.isSelected()) {
                    table.deselectRow();
                    sessionStorage.removeItem(ssProduct_selectedComponent + bomId);
                } else {
                    table.deselectRow();
                    row.select();
                    showReplacement(row.getData().id, $subBlock.is(':hidden'));
                    sessionStorage.setItem(ssProduct_selectedComponent + bomId, row.getData().id);
                }
            },
            rowContext: (e, row) => {
                table.deselectRow();
                row.select();
                sessionStorage.setItem(ssProduct_selectedComponent + bomId, row.getData().id);
            },
            dataLoaded: () => {
                const ssComponentSelectedId = sessionStorage.getItem(ssProduct_selectedComponent + bomId);
                let row = table.searchRows("id", "=", ssComponentSelectedId)[0];
                if (row !== undefined) {
                    row.select();
                    row.scrollTo();
                }
            },
            rowContextMenu: row => {
                const menu = [];
                const id = row.getData().id;
                table.deselectRow();
                row.select();
                showReplacement(id, $subBlock.is(':hidden'));
                menu.push({
                    label: `<i class="align justify icon blue"></i>Показать позиционные обозначения`,
                    action: () => showPosition(id)
                });
                menu.push({
                    label: `<i class="arrows alternate horizontal icon blue"></i>Заменить на компонент из справочника`,
                    action: () => replaceComponent(id)
                });
                menu.push({
                    label: `<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>`,
                    action: () => specEditPosition(id)
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => specDeletePosition(id)
                });
                return menu;
            },
            dataSorting: () => $subBlock.hide()
        });

        // Резсайз вспомогательного контейнера
        $subBlock.resizable({
            autoHide: true,
            handles: 'n',
            ghost: true,
            stop: () => $subBlock.css({
                'width': '100%',
                'top': 0
            })
        });

        // Скрытие области вспомогательного контейнера
        $btnCloseSubBlock.on({
            'click': () => {
                table.deselectRow();
                sessionStorage.removeItem(ssProduct_selectedComponent + bomId);
                $subBlock.hide();
            }
        });

        // Заменить компонент в спецификации на другой компонент из справочника
        function replaceComponent(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/product/detail/specification/info/spec-replacement-component',
                loadData: {
                    bomId: bomId,
                    bomItemId: id
                }
            });
        }

        // Функция редактирования позиции
        function specEditPosition(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/product/detail/specification/info/edit-spec-item',
                loadData: { id: id },
                submitURL: '/api/action/prod/product/detail/specification/info/edit-spec-item/save',
                onSubmitSuccess: () => $datatable.trigger('reloadSelectRow', [id])
            });
        }

        // Функция удаления позиции
        function specDeletePosition(id) {
            confirmDialog({
                title: 'Удаление позиции',
                message: 'Вы уверены, что хотите удалить позицию?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: '/api/action/prod/product/detail/specification/info/list-spec-delete/' + id + '/' + bomId,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => {
                    sessionStorage.removeItem(ssProduct_selectedComponent + bomId);
                    $subBlock.hide();
                    table.setData();
                })
            });
        }

        // Функция отображения позиций
        function showPosition(id) {
            $.get({
                url: '/api/view/prod/product/detail/specification/info/position',
                data: { bomItemId: id },
                beforeSend: () => togglePreloader(true),
                complete: () => togglePreloader(false)
            }).done(html => {
                $subContent.html(html);
                $subBlock.show();
            }).fail(() => $subBlock.hide());
        }

        // Функция отображения замен
        function showReplacement(id, scroll) {
            $.get({
                url: '/api/view/prod/product/detail/specification/info/replacement',
                data: { bomItemId: id , scroll: scroll },
                beforeSend: () => togglePreloader(true),
                complete: () => togglePreloader(false)
            }).done(html => {
                $subContent.html(html);
                $subBlock.show();
            }).fail(() => $subBlock.hide());
        }

        // Функция добавления компонента в спецификацию
        $btnSpecAddComponent.on({
            'click': () => $.modalWindow({
                loadURL: '/api/view/prod/product/detail/specification/info/spec-add-component',
                loadData: { bomId: bomId }
            })
        });

        // Добавление нового компонента
        $btnSpecAddNewComponent.on({
            'click': () => $.modalWindow({
                loadURL: '/api/view/prod/component/list/edit',
                loadData: {
                    id: null,
                    addAsDesign: false,
                    copy: false
                },
                submitURL: '/api/action/prod/component/list/edit/save',
                onSubmitSuccess: response => {
                    $.post({
                        url: '/api/action/prod/product/detail/specification/info/spec-add-component/save',
                        data: { bomId: bomId, componentId: response.attributes.id },
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(id => $datatable.trigger('reloadSelectRow', [id]));
                }
            })
        })

        // Ивенты для взаимодействия с табулятором
        $datatable.on({
            'reload': () => table.setData(),
            'reloadSelectRow': (e, id) => {
                table.setData().then(() => {
                    const row = table.getRow(id);
                    if (row != null) {
                        row.select();
                        showReplacement(id, true);
                    }
                });
            }
        });

        // Кнопка выгрузки в excel
        $btnExcel.on({
            'click': () => {
                const usp = new URLSearchParams();
                usp.set(TABR_FIELD.BOM_ID, bomId);
                usp.set(TABR_REQ_ATTR_FILTER_DATA, JSON.stringify(filterData));
                const sorters = table.getSorters();
                if (sorters.length) {
                    usp.set(TABR_REQ_ATTR_FIELD, sorters[0].field);
                    usp.set(TABR_REQ_ATTR_DIR, sorters[0].dir);
                }
                window.open(window.location.origin + '/api/action/prod/product/detail/specification/info/spec-download?' + usp.toString(), '_blank');
            }
        });

        // Фильтр
        $.modalFilter({
            url: '/api/view/prod/product/detail/specification/info/filter',
            button: $btnFilter,
            filterData: () => filterData,
            onApply: data => {
                filterData = data;
                table.setData();
            }
        });
    });
</script>