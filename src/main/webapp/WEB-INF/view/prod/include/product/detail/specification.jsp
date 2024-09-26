<div class="detail_specification__main">
    <div class="detail_specification__version-container">
        <div class="detail_specification__version-label">
            <fmt:message key="product.detail.specification.version.label"/>
        </div>
        <div class="detail_specification__version-select">
            <select class="ui tiny fluid dropdown search">
                <c:forEach items="${versionList}" var="version">
                    <option value="${version.id}" <c:if test="${version.selected}">selected</c:if>>
                        ${version.value}
                    </option>
                </c:forEach>
            </select>
        </div>
        <div class="ui small icon buttons detail_specification__btn-grp-version">
            <div class="ui button basic top left pointing dropdown detail_specification__btn-add-version" title="Добавить">
                <i class="add icon"></i>
                <div class="menu">
                    <div class="item detail_specification__menu-major">Версия</div>
                    <div class="item detail_specification__menu-minor">Изменение</div>
                    <div class="item detail_specification__menu-modification">Модификация</div>
                </div>
            </div>
            <div class="ui button basic detail_specification__btn-remove-version" title="Удалить версию">
                <i class="times red icon"></i>
            </div>
        </div>
    </div>
    <div class="detail_specification__content"></div>

    <script>
        $(() => {
            const productId = '${productId}';
            const loadBomFromSession = '${loadBomFromSession}' === 'true'; // флаг загрузки из сессионного кэша
            //
            const $parentContent = $('div.detail__content');
            const $content = $('div.detail_specification__content');
            const $version = $('div.detail_specification__version-select > select');
            const $btnRemoveVersion = $('div.detail_specification__btn-remove-version');
            const menuData = [
                { $item: $('div.detail_specification__menu-major'), type: 'major', header: 'версии', message: 'версию' },
                { $item: $('div.detail_specification__menu-minor'), type: 'minor', header: 'изменения', message: 'изменение' },
                { $item: $('div.detail_specification__menu-modification'), type: 'modification', header: 'модификации', message: 'модификацию' }
            ];

            // Устанавливаем версию из сессионного кэша
            if (loadBomFromSession) {
                const selectedId = sessionStorage.getItem(ssProduct_bomVersionId);
                if (selectedId != null && $version.find('option[value=' + selectedId + ']').length > 0) {
                    $version.val(selectedId);
                }
            }

            // Привязка кнопок формирования версии
            $.each(menuData, (inx, item) =>
                item.$item.on({
                    'click': () => confirmDialog({
                        title: 'Добавление ' + item.header,
                        message: 'Вы действительно хотите добавить ' + item.message + '?',
                        onAccept: () => editVersion(item.type)
                    })
                })
            );

            // Функция редактирования версии изделия
            function editVersion(type) {
                const processVersion = isReplacementStatusCopy => {
                    $.get({
                        url: '/api/action/prod/product/detail/specification/edit-version',
                        data: {
                            productId: productId,
                            type: type,
                            isReplacementStatusCopy: isReplacementStatusCopy
                        },
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(bomId => {
                        $.get({
                            url: '/api/view/prod/product/detail/specification',
                            data: {
                                productId: productId,
                                selectedBomId: bomId
                            },
                            beforeSend: () => togglePreloader(true),
                            complete: () => togglePreloader(false)
                        }).done(html => $parentContent.html(html));
                    });
                };
                if ($version.has('option').length === 0) {
                    processVersion(false);
                } else {
                    confirmDialog({
                        title: 'Статусы замен',
                        message: 'Вы хотите скопировать статус замен?',
                        buttonTextReject: 'Нет',
                        onAccept: () => processVersion(true),
                        onReject: () => processVersion(false)
                    });
                }
            }

            // Загрузка страницы списка
            $content.on({
                'load-list': () => {
                    const id = $version.find(':selected').val();
                    if (!id) return;
                    $.get({
                        url: '/api/view/prod/product/detail/specification/info',
                        data: { bomId: id },
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(html => {
                        sessionStorage.setItem(ssProduct_bomVersionId, id);
                        $content.html(html);
                    });
                }
            });

            // Инициализация списка версий
            $version.dropdown({
                forceSelection: false,
                fullTextSearch: true,
                placeholder: 'Версии отсутствуют'
            });
            $version.on({
                'change': () => $content.trigger('load-list')
            }).trigger('change');

            // Кнопка добавления вариантов версии изделия
            $('div.detail_specification__btn-add-version').dropdown({
                action: 'hide'
            });

            // Кнопка удаления версии
            $btnRemoveVersion.on({
                'click': () => {
                    confirmDialog({
                        title: 'Удаление версии',
                        message: 'Вы действительно хотите удалить версию?',
                        onAccept: () => {
                            const id = $version.find(':selected').val();
                            $.ajax({
                                method: 'DELETE',
                                url: '/api/action/prod/product/detail/specification/delete-version/' + (id == null ? 0 : id),
                                beforeSend: () => togglePreloader(true),
                                complete: () => togglePreloader(false)
                            }).done(() => {
                                $.get({
                                    url: '/api/view/prod/product/detail/specification',
                                    data: { productId: productId },
                                    beforeSend: () => togglePreloader(true),
                                    complete: () => togglePreloader(false)
                                }).done(html => $parentContent.html(html));
                            });
                        }
                    });
                }
            });
        });
    </script>
</div>