<div class="detail_structure_info__version-info">
    <div>
        <b>Идентификатор:</b>
        ${bomDescriptor}
    </div>
    <c:if test="${not empty bomProductionName}">
        <div>
            <b>Производственное наименование:</b>
            ${bomProductionName}
        </div>
    </c:if>
</div>
<div class="detail_structure_info__tree-table-buttons">
    <i class="icon expand alternate link blue detail_structure_info__btn-expand" title="Развернуть"></i>
    <i class="icon compress alternate link blue detail_structure_info__btn-compress" title="Свернуть"></i>
    <i class="icon add link blue detail_structure_info__btn-tree-table-add" title="Добавить изделие"></i>
</div>
<div class="detail_structure_info__launch-block">
    <b>Запуск</b>
    <div class="detail_structure_info__launch-select">
        <select class="ui mini fluid dropdown search">
            <c:forEach items="${launchList}" var="launch">
                <option value="${launch.id}">${launch.numberInYear}</option>
            </c:forEach>
        </select>
    </div>
</div>
<div class="detail_structure_info__producer-block">
    <b>Изготовитель</b>
    <div class="detail_structure_info__producer-select">
        <select class="ui mini fluid dropdown search">
            <option value=""><fmt:message key="text.notSpecified"/></option>
            <c:forEach items="${producerList}" var="producer">
                <option value="${producer.id}">${producer.name}</option>
            </c:forEach>
        </select>
    </div>
    <div class="ui small icon button basic detail_structure_info__btn-producer-approve">
        <i class="square outline icon"></i>
    </div>
</div>
<div class="detail_structure_info__tree-table-block">
    <div class="detail_structure_info__tree-table-wrap">
        <div class="detail_structure_info__tree-table table-sm table-striped"></div>
    </div>
</div>

<script>
    $(() => {
        const bomId = '${bomId}';
        const $btnAddProduct = $('i.detail_structure_info__btn-tree-table-add');
        const $btnExpand = $('i.detail_structure_info__btn-expand');
        const $btnCompress = $('i.detail_structure_info__btn-compress');
        const $table = $('div.detail_structure_info__tree-table');
        const $launchSelect = $('div.detail_structure_info__launch-select > select');
        const $producerSelect = $('div.detail_structure_info__producer-select > select');
        const $btnApproveProducer = $('div.detail_structure_info__btn-producer-approve');

        // Список запусков
        $launchSelect.dropdown({
            forceSelection: false,
            fullTextSearch: true,
            message: { noResults: '' }
        });
        $launchSelect.on({
            'change': () => {
                $.get({
                    url: '/api/action/prod/product/detail/structure/info/check-producer-by-launch',
                    data: {
                        bomId: bomId,
                        launchId: $launchSelect.find(':selected').val()
                    },
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(producerId => {
                    if (producerId) {
                        $producerSelect.find('option[value=' + producerId + ']').prop('selected', true);
                    }
                    $producerSelect.trigger('change');
                });
            }
        });
        $launchSelect.trigger('change');

        // Список производителей
        $producerSelect.dropdown({
            forceSelection: false,
            fullTextSearch: true,
            message: { noResults: '' }
        });
        $producerSelect.on({
            'change': () => {
                $.get({
                    url: '/api/action/prod/product/detail/structure/info/check-version-producer',
                    data: {
                        bomId: bomId,
                        producerId: $producerSelect.find(':selected').val(),
                        launchId: $launchSelect.find(':selected').val()
                    },
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(state => {
                    if (state) {
                        $btnApproveProducer.attr('title', 'Снять утверждение изготовителя');
                        $btnApproveProducer.find('i').addClass('check');
                    } else {
                        $btnApproveProducer.attr('title', 'Утвердить изготовителя');
                        $btnApproveProducer.find('i').removeClass('check');
                    }
                    $btnApproveProducer.data('state', state);
                });
            }
        });

        // Кнопка утверждения изготовителя
        $btnApproveProducer.data('state', false);
        $btnApproveProducer.on({
            'click': () => {
                const state = $btnApproveProducer.data('state');
                confirmDialog({
                    title: (state ? 'Снять утверждение' : 'Утвердить') + ' изготовителя',
                    message: 'Вы уверены, что хотите ' + (state ? 'снять утверждение изготовителя?' : 'утвердить изготовителя?'),
                    onAccept: () => {
                        $.post({
                            url: '/api/action/prod/product/detail/structure/info/approve-producer',
                            data: {
                                bomId: bomId,
                                producerId: $producerSelect.find(':selected').val(),
                                launchId: $launchSelect.find(':selected').val(),
                                state: state
                            },
                            beforeSend: () => togglePreloader(true),
                            complete: () => togglePreloader(false)
                        }).done(() => {
                            if (state) {
                                $btnApproveProducer.attr('title', 'Утвердить изготовителя');
                                $btnApproveProducer.find('i').removeClass('check');
                            } else {
                                $btnApproveProducer.attr('title', 'Снять утверждение изготовителя');
                                $btnApproveProducer.find('i').addClass('check');
                            }
                            $btnApproveProducer.data('state', !state)
                        });
                    }
                });
            }
        });

        const table = new Tabulator('div.detail_structure_info__tree-table', {
            ajaxURL: '/api/action/prod/product/detail/structure/info/list-load',
            ajaxRequesting: (url, params) => {
                params.bomId = bomId;
            },
            height: '100%',
            dataTree: true,
            layout: 'fitDataStretch',
            headerFilterPlaceholder: '<fmt:message key="text.search"/>',
            columns: [
                {
                    title: '<fmt:message key="product.detail.structure.info.list.field.conditionalName"/>',
                    field: 'conditionalName',
                    headerFilter: 'input',
                    headerFilterFunc: (headerValue, rowValue, rowData) => {
                        const search = data => {
                            if (data.conditionalName.toLowerCase().indexOf(headerValue.toLowerCase()) === -1) {
                                const childArr = data._children;
                                if (childArr != null && childArr.length > 0) {
                                    for (let child of childArr) {
                                        if (search(child)) {
                                            return true;
                                        }
                                    }
                                }
                            } else {
                                return true;
                            }
                            return false;
                        };
                        return search(rowData);
                    }
                },
                { title: '<fmt:message key="product.detail.structure.info.list.field.quantity"/>', field: 'quantity' },
                { title: '<fmt:message key="product.detail.structure.info.list.field.producer"/>', field: 'producer' }
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
                if (data.main) {
                    menu.push({
                        label: `<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>`,
                        action: () => editProduct(data.id)
                    });
                    menu.push({
                        label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                        action: () => deleteProduct(data.id)
                    });
                }
                menu.push({
                    label: `<i class="file alternate outline icon blue"></i>Открыть закупочную спецификацию`,
                    action: () => window.open(window.location.origin + '/prod/product/detail/' + data.productId + '/specification?loadLastAcceptOrApproved=true')
                });
                return menu;
            },
            tableBuilt: () => $table.find('div.tabulator-header-filter input').each(
                (inx, elem) => $(elem).wrap('<div class="ui input fluid"></div>'))
        });

        // Функция добавления изделия в состав
        function addProduct() {
            $.modalWindow({
                loadURL: '/api/view/prod/product/detail/structure/info/list-add',
                loadData: { bomId: bomId }
            });
        }

        // Функция редактирования изделия в составе
        function editProduct(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/product/detail/structure/info/list-edit',
                loadData: { bomSpecItemId: id },
                submitURL: '/api/action/prod/product/detail/structure/info/list-edit/save',
                onSubmitSuccess: () => table.setData()
            });
        }

        // Функция удаления изделия
        function deleteProduct(id) {
            confirmDialog({
                title: 'Удаление изделия',
                message: 'Вы уверены, что хотите удалить изделие из состава?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: '/api/action/prod/product/detail/structure/info/list-delete/' + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setData())
            });
        }

        // Кнопка добавления изделия в состав
        $btnAddProduct.on({
            'click': () => addProduct()
        });

        // Ивенты для взаимодействия с табулятором
        $table.on({
            'reload': () => table.setData()
        });

        // Кнопка свернуть
        $btnCompress.on({
            'click': () => {
                const collapse = rows => {
                    $.each(rows, (inx, row) => {
                        row.treeCollapse();
                        collapse(row.getTreeChildren());
                    });
                };
                collapse(table.getRows());
            }
        });

        // Кнопка развернуть
        $btnExpand.on({
            'click': () => {
                const expand = rows => {
                    $.each(rows, (inx, row) => {
                        row.treeExpand();
                        expand(row.getTreeChildren());
                    });
                };
                expand(table.getRows());
            }
        });
    });
</script>