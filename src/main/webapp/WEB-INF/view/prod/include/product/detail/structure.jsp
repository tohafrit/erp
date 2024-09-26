<div class="detail_structure__main">
    <div class="detail_structure__version-container">
        <div class="detail_structure__version-label">
            <fmt:message key="product.detail.structure.version.label"/>
        </div>
        <div class="detail_structure__version-select">
            <select class="ui tiny fluid dropdown search">
                <c:forEach items="${versionList}" var="version">
                    <option value="${version.id}" <c:if test="${version.selected}">selected</c:if>>
                        ${version.value}
                    </option>
                </c:forEach>
            </select>
        </div>
        <div class="ui small icon buttons detail_structure__btn-grp-version">
            <div class="ui button basic top left pointing dropdown detail_structure__btn-add-version" title="Добавить">
                <i class="add icon"></i>
                <div class="menu">
                    <div class="item detail_structure__menu-major">Версия</div>
                    <div class="item detail_structure__menu-minor">Изменение</div>
                    <div class="item detail_structure__menu-modification">Модификация</div>
                </div>
            </div>
            <div class="ui button basic detail_structure__btn-remove-version" title="Удалить версию">
                <i class="times red icon"></i>
            </div>
        </div>
    </div>
    <div class="detail_structure__content"></div>

    <script>
        $(() => {
            const loadBomFromSession = '${loadBomFromSession}' === 'true'; // флаг загрузки из сессионного кэша
            const productId = '${productId}';
            const $parentContent = $('div.detail__content');
            const $content = $('div.detail_structure__content');
            const $version = $('div.detail_structure__version-select > select');
            const $btnRemoveVersion = $('div.detail_structure__btn-remove-version');
            const menuData = [
                { $item: $('div.detail_structure__menu-major'), type: 'major', message: 'версию' },
                { $item: $('div.detail_structure__menu-minor'), type: 'minor', message: 'изменение' },
                { $item: $('div.detail_structure__menu-modification'), type: 'modification', message: 'модификацию' }
            ];

            // Устанавливаем версию
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
                        title: 'Добавить ' + item.message,
                        message: 'Вы действительно хотите добавить ' + item.message + '?',
                        onAccept: () => editVersion(item.type)
                    })
                })
            );

            // Функция редактирования версии изделия
            function editVersion(type) {
                const processVersion = isReplacementStatusCopy => {
                    $.get({
                        url: '/api/action/prod/product/detail/structure/edit-version',
                        data: {
                            productId: productId,
                            type: type,
                            isReplacementStatusCopy: isReplacementStatusCopy
                        },
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(bomId => {
                        $.get({
                            url: '/api/view/prod/product/detail/structure',
                            data: {
                                productId: productId,
                                selectedBomId: bomId
                            },
                            beforeSend: () => togglePreloader(true),
                            complete: () => togglePreloader(false)
                        }).done(html => $parentContent.html(html));
                    });
                };
                confirmDialog({
                    title: 'Статусы замен',
                    message: 'Вы хотите скопировать статус замен?',
                    buttonTextReject: 'Нет',
                    onAccept: () => processVersion(true),
                    onReject: () => processVersion(false)
                });
            }

            // Инициализация списка версий
            $version.dropdown({
                forceSelection: false,
                fullTextSearch: true,
                placeholder: 'Версии отсутствуют'
            });
            $version.on({
                'change': () => loadList($version.find(':selected').val())
            });
            $version.trigger('change');

            // Загрузка страницы списка
            function loadList(id) {
                if (!id) return;
                $.get({
                    url: '/api/view/prod/product/detail/structure/info',
                    data: { bomId: id },
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(html => {
                    sessionStorage.setItem(ssProduct_bomVersionId, id);
                    $content.html(html);
                });
            }

            // Кнопка добавления вариантов версии изделия
            $('div.detail_structure__btn-add-version').dropdown({
                action: 'hide'
            });

            // Кнопка удаления версии
            $btnRemoveVersion.on({
                'click': () => confirmDialog({
                    title: 'Удаление версии',
                    message: 'Вы действительно хотите удалить версию?',
                    onAccept: () => {
                        const id = $version.find(':selected').val();
                        $.ajax({
                            method: 'DELETE',
                            url: '/api/action/prod/product/detail/structure/delete-version/' + (id == null ? 0 : id),
                            beforeSend: () => togglePreloader(true),
                            complete: () => togglePreloader(false)
                        }).done(() => {
                            $.get({
                                url: '/api/view/prod/product/detail/structure',
                                data: { productId: productId },
                                beforeSend: () => togglePreloader(true),
                                complete: () => togglePreloader(false)
                            }).done(html => $parentContent.html(html));
                        });
                    }
                })
            });
        });
    </script>
</div>