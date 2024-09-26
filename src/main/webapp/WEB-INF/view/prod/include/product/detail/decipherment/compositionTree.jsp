<div class="b-composition-tree__container">
    <div class="ui tiny basic icon buttons">
        <button title="<fmt:message key="label.button.refresh"/>" class="ui button js-refresh-tree" type="button"><i class="blue sync alternate icon"></i></button>
    </div>
    <c:choose>
        <c:when test="${fn:length(bom.bomSpecItemList) gt 0}">
            <div class="ui tiny basic icon buttons">
                <button title="<fmt:message key="label.button.expand"/>" data-mode="plus" class="ui button js-expand-tree" type="button"><i class="expand icon"></i></button>
                <button title="<fmt:message key="label.button.collapse"/>" data-mode="minus" class="ui button js-compress-tree" type="button"><i class="compress icon"></i></button>
            </div>
            <div class="b-composition-tree__header-container">
                <div style="width: 70px;"></div>
                <div style="width: 340px;"><fmt:message key="decipherment.productComposition.header.name"/></div>
                <div style="width: 60px;"><fmt:message key="decipherment.productComposition.header.quantity"/></div>
                <div style="width: 70px;"><fmt:message key="decipherment.productComposition.header.version"/></div>
                <div style="width: 30px;">
                    <div class="ui checkbox">
                        <input type="checkbox" class="js-mark-all"/>
                    </div>
                </div>
            </div>
            <ul>
                <jsp:useBean id="bomMap" class="java.util.HashMap" scope="request"/> <%-- карта bom для построения древа --%>
                <c:set target="${bomMap}" property="1" value="${bom}"/> <%-- кладем в карту корневую версию --%>
                    <%-- bomCount - счетчик bom для получения и хранения параметров в bomMap --%>
                    <%-- nameFieldWidth - ширина поля наименования - используется для выравнивания полей в лестницу --%>
                <jsp:include page="include/product/detail/decipherment/additional/loadComposition.jsp">
                    <jsp:param name="bomCount" value="1"/>
                    <jsp:param name="nameFieldWidth" value="340"/>
                </jsp:include>
            </ul>
        </c:when>
        <c:otherwise>
            <div class="b-align-center"><fmt:message key="text.emptyData"/></div>
        </c:otherwise>
    </c:choose>
</div>

<style>
    .b-composition-tree__container {
        border: #dddddd solid 1px;
        border-radius: 4px;
        padding: 5px;
        width: auto;
    }
    .b-composition-tree__container ul {
        list-style-type: none;
    }
    .b-composition-tree__header-container {
        display: inline-flex;
        width: 100%;
        font-weight: bold;
        margin: 5px 0 5px 3px;
    }
    .b-composition-tree__header-container > div {
        padding: 5px;
        margin: 0.14em;
    }
    .b-composition-tree__product {
        border: #dddddd solid 1px;
        border-radius: 2px;
        font-weight: bold;
        color: #315c83;
        padding: 3px 4px 3px 4px;
        background: #f9f9f9;
        margin: 2px 0 2px 0;
    }
    .b-composition-tree__product.toggled {
        background: lightsteelblue;
    }
    .b-composition-tree__product_item {
        padding: 0 5px 0 5px;
        vertical-align: middle;
        word-break: break-all;
        display: inline-block;
    }
    .b-composition-tree__product_toggle-item {
        padding: 0 5px 0 5px;
        vertical-align: middle;
        word-break: break-all;
        display: inline-block;
        width: 30px;
    }
    .b-composition-tree__product_toggle-item > div.icon {
        font-size: 7px !important;
    }
    .b-composition-tree__block {
        padding-left: 20px;
    }
    .b-align-center {
        text-align: center;
    }
    a.b-link, a.b-link:visited,
    a.b-link:focus, a.b-link:active {
        font-weight: bold;
        color: #287fc3;
        text-decoration: none;
        outline: none;
    }
    a.b-link:hover {
        cursor: pointer;
        text-decoration: underline;
    }
</style>

<script>
    $(() => {
        // Общие константы
        const
            $dialog = $('.decipherment_edit__modal'),
            $compositionTree = $dialog.find('.js-composition-tree'),
            $versionSelectionList = $compositionTree.find('.js-version-select'),
            $itemToggleList = $compositionTree.find('.js-item-toggle'),
            $compositionInput = $compositionTree.next('input[type=hidden]'),
            deciphermentId = '${deciphermentId}',
            deciphermentTypeName = '${deciphermentTypeName}';

        // Карта сохраненных изделий
        const compositionProductMap = new Map();
        <c:forEach items="${compositionProductMap}" var="compositionProduct">
            compositionProductMap.set('${compositionProduct.key}', JSON.parse('${compositionProduct.value}'));
        </c:forEach>

        // Набор запрещенных к выбору изделий
        const prohibitedProductNumberSet = new Set();
        <c:forEach items="${prohibitedProductNumberSet}" var="prohibitedProductNumber">
            prohibitedProductNumberSet.add('${prohibitedProductNumber}');
        </c:forEach>

        // Устанавливаем уникальные номера для пунктов древа и выполняем прочие обработки
        $compositionTree.find('ul:first li').each(function () {
            // Выставляем уникальный номер для изделия спецификации
            let $parentProduct = $(this).closest('ul').prev('li');
            while ($parentProduct.length > 0) {
                $(this).attr('data-full-hierarchy-number',
                    [$parentProduct.data('specificationId'), $parentProduct.data('productId'), $parentProduct.data('versionId')].join('-')
                    + '_' + $(this).attr('data-full-hierarchy-number')
                );
                $parentProduct = $parentProduct.closest('ul').prev('li');
            }
            const productNumber = $(this).data('fullHierarchyNumber');
            // Убираем чекбоксы для запрещенных изделий
            if (prohibitedProductNumberSet.has(productNumber.toString())) {
                $(this).find('.js-mark-product').parent().remove();
            }
            // Обработка сохраненных изделий - выставление выбранных версий и чекбоксов
            const compositionProduct = compositionProductMap.get(productNumber.toString());
            if (compositionProduct != null) {
                $(this).find('.js-mark-product').prop('checked', true);
                let selectedVersionId = compositionProduct.selectedVersionId,
                    selectedVersion = compositionProduct.selectedVersion;
                if (selectedVersionId != null && selectedVersion != null) {
                    const $versionSelect = $(this).find('.js-version-select');
                    $versionSelect.data('value', selectedVersionId);
                    $versionSelect.html(selectedVersion);
                }
            }
        });
        // Если имеем выбранные изделия (помеченые чекбоксами в предыдущей итерации),
        // то необходимо выставить версии изделий для вышестоящих изделий для корректного отображения в древе
        $compositionTree.find('.js-mark-product:checked').closest('li').each(function () {
            let $parentProduct = $(this).closest('ul').prev('li'),
                versionId = $(this).data('versionId'),
                versionName = $(this).data('versionName');
            while ($parentProduct.length > 0) {
                let $versionSelect = $parentProduct.find('.js-version-select');
                $versionSelect.data('value', versionId);
                $versionSelect.html(versionName);

                versionId = $parentProduct.data('versionId');
                versionName = $parentProduct.data('versionName');
                $parentProduct = $parentProduct.closest('ul').prev('li');
            }
        });
        // Стартовая загрузка древа изделий
        $compositionTree.find('ul:first > li').show().addClass('displayed-node'); // корневые пункты изделий - всегда отображаются
        _fnLoadTree($compositionTree.find('ul:first li')); // загрузка древа

        //// Функции
        // Функция обновления состояния общего чекбокса в зависимости от состояния чекбоксов изделия
        function _fnMarkAllState () {
            if ($compositionTree.find('li.displayed-node .js-mark-product').length > 0) {
                $compositionTree.find('.js-mark-all').prop('checked', $compositionTree.find('li.displayed-node .js-mark-product:not(:checked)').length === 0);
            }
        }
        // Функция загрузки древа в закрытом состоянии с учетом версий. Автоматически проставляется класс displayed-node
        // Это означает, что пункт изделия отображается и является актуальным узлом в древе в соответствии версии
        // При этом узел может быть в свернутом состоянии
        // В качестве параметра берется список изделий (li пункты дерева)
        // Алгоритм работает верно если все изделия сортированы в порядке следования в DOM
        function _fnLoadTree ($productList) {
            $productList.each(function () {
                const $ulSpec = $(this).next('ul'),
                    selectVersionId = $(this).find('.js-version-select').data('value'),
                    $itemToggle = $(this).find('.js-item-toggle'),
                    $itemToggleIcon = $itemToggle.children('i');
                if (selectVersionId && $(this).hasClass('displayed-node')) {
                    $ulSpec.children('li[data-version-id=' + selectVersionId + ']').addClass('displayed-node');
                }
                // Состояние кнопки открыть/развернуть
                if ($ulSpec.children('li.displayed-node').length > 0) {
                    $itemToggle.show();
                    $itemToggleIcon.removeClass('minus');
                    $itemToggleIcon.addClass('plus');
                } else {
                    $itemToggle.hide();
                }
            });
            _fnMarkAllState();
        }

        // Стилизация чекбоксов
        $compositionTree.find('.js-mark-product, .js-mark-all').parent().checkbox();

        // При нажатии на чекбокс у изделия
        $compositionTree.find('.js-mark-product').on({
            'change' : function () {
                // Суть алгоритма в том, что дочерний элемент не может быть помечен без пометки предка
                if (deciphermentTypeName === 'FORM_6_1' || deciphermentTypeName === 'FORM_4') {
                    if ($(this).prop('checked')) {
                        let $parentProduct = $(this).closest('ul').prev('li');
                        while ($parentProduct.length === 1) {
                            $parentProduct.find('.js-mark-product').prop('checked', true);
                            $parentProduct = $parentProduct.closest('ul').prev('li');
                        }
                    } else {
                        $(this).closest('li').next('ul').find('li .js-mark-product').prop('checked', false);
                    }
                }
                _fnMarkAllState();
            }
        });
        // Выбрать/Снять выбор всех
        $compositionTree.find('.js-mark-all').on({
            'change' : function () {
                $compositionTree.find('li.displayed-node .js-mark-product').prop('checked', $(this).is(':checked'));
            }
        });
        // Развернуть/Свернуть древо
        $compositionTree.find('.js-expand-tree, .js-compress-tree').on({
            'click' : function () {
                const mode = $(this).data('mode');
                $compositionTree.find('li.displayed-node').each(function () {
                    $(this).find('.js-item-toggle:has(i.' + mode + ')').trigger('click');
                });
            }
        });
        // Обновить древо
        $compositionTree.find('.js-refresh-tree').on({
            'click' : () => {
                $dialog.find('.b-error').text('');
                const versionId = $dialog.find('.js-version').val();
                if (versionId) {
                    $compositionTree.html('');
                    $.get({
                        url: '/decipherment/edit/composition-tree',
                        data: {
                            deciphermentId: deciphermentId,
                            versionId: versionId
                        },
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(html => {
                        $compositionTree.html(html);
                    }).fail(() => {
                        globalMessage({message: 'Ошибка загрузки состава'});
                    });
                }
            }
        });

        // Переключатели открыть/скрыть подизделия для каждого изделия (+/-)
        $itemToggleList.on({
            'click' : function () {
                const $icon = $(this).children('i'), $ul = $(this).closest('li').next('ul');
                $ul.toggle($icon.hasClass('plus'));
                $ul.children('li.displayed-node').toggle($icon.hasClass('plus'));
                $icon.toggleClass('plus minus');
            }
        });

        // Выбор версии и ее смена
        $versionSelectionList.on({
            'click' : function () {
                const $row = $(this).closest('li');
                $.modalWindow({
                    loadURL: '/decipherment/edit/search-version',
                    loadData: {
                        mode: 'compositionVersion',
                        productId: $row.data('productId'),
                        productNumber: $row.data('fullHierarchyNumber'),
                        selectedVersionId: $(this).data('value')
                    }
                });
            },
            'change' : function () {
                const $li = $(this).closest('li'),
                    $ul = $li.next('ul'),
                    selectVersionId = $(this).data('value'),
                    $itemToggle = $li.find('.js-item-toggle'),
                    $itemToggleIcon = $itemToggle.children('i');
                // Обнуляем состояние видимости и все вложенные статусы отображения
                $ul.find('li, ul').hide();
                $ul.find('li.displayed-node').removeClass('displayed-node');
                // Сброс чекбоксов изделий
                $ul.find('.js-mark-product').prop('checked', false);
                if (selectVersionId && $li.hasClass('displayed-node')) {
                    $ul.children('li[data-version-id=' + selectVersionId + ']').addClass('displayed-node');
                }
                // Отображение анимации изменения версии изделия
                $li.addClass('toggled');
                setTimeout(() => $li.removeClass('toggled'), 200);
                // Состояние кнопки открыть/развернуть
                if ($ul.children('li.displayed-node').length > 0) {
                    $itemToggle.show();
                    $itemToggleIcon.removeClass('minus');
                    $itemToggleIcon.addClass('plus');
                    $itemToggle.trigger('click'); // открываем подсписок изделий
                } else {
                    $itemToggle.hide();
                }
                // В этой точке мы изменили состояние древа того изделия в котором выбирали версию
                // Теперь необходимо загрузить состояние подизделий -> подизделий подизделий... то есть фактически рекурсия
                // Ниже функция выполняет именно такую загрузку
                _fnLoadTree($ul.find('li'));
            }
        });

        // Добавление аттрибутов перед сохранением
        $dialog.on({
            'cb.onInitSubmit' : () => {
                const composition = [];
                $compositionInput.val('');
                $compositionTree.find('.js-mark-product:checked').closest('li.displayed-node').each((inx, elem) => {
                    let product = {};
                    product.fullHierarchyNumber = $(elem).data('fullHierarchyNumber');
                    product.specificationId = $(elem).data('specificationId');
                    product.productId = $(elem).data('productId');
                    product.versionId = $(elem).data('versionId');
                    product.selectedVersionId = $(elem).find('.js-version-select').data('value');
                    composition.push(product);
                });
                $compositionInput.val(JSON.stringify(composition));
            }
        });
    });
</script>