/*
    Для того, чтобы корректно отображалось древо необходимо использовать тэг <std:treeChosen> для генерации option пунктов

    Пример:

    <select class="tree-chosen" id="departmentId" name="departmentId" data-chosen-options='{"containerWidth":"400"}'>
        <option value="1" data-chosen-parent="" data-chosen-childrens="" data-chosen-name="Cat">Cat</option>
        <option value="2" data-chosen-parent="" data-chosen-childrens="3" data-chosen-name="Cat">Cat</option>
        <option value="3" data-chosen-parent="2" data-chosen-childrens="" data-chosen-name="tom" selected>tom</option>
        <option value="4" data-chosen-parent="" data-chosen-childrens="5" data-chosen-name="Cat">Cat</option>
        <option value="5" data-chosen-parent="4" data-chosen-childrens="" data-chosen-name="tom">tom</option>
        <option value="6" data-chosen-parent="" data-chosen-childrens="7,8" data-chosen-name="Cat">Cat</option>
        <option value="7" data-chosen-parent="6" data-chosen-childrens="" data-chosen-name="tom">tom</option>
        <option value="8" data-chosen-parent="6" data-chosen-childrens="" data-chosen-name="tom2" selected>tom2</option>
    </select>

    Параметры option:
        data-chosen-parent - датасет содержащий идентификатор родителя
        data-chosen-childrens - датасет, содержащий список идентификаторов потомков
        data-chosen-name - датасет с именем сущности в древе
        value - идентификатор самой выбираемой сущности

    Параметры select:
        class="tree-chosen" - основной способ инициализации, альтернативный - вызов treeChosen() для элемента <select> ( ex. $('.tree-chosen').treeChosen() )

        data-chosen-options - датасет с настройками.
            - containerWidth - ширина контейнера
            - containerMaxWidth - максимальная ширина контейнера
            - containerHeight - высота контейнера
            - containerMinHeight - минимальная высота контейнера
            - treeContainerHeight - высота контейнера древа
            - multiple - множественный выбор опций (по умолчанию - false)
*/

;(function($, document) {

    'use strict';

    const defaults = {
        containerWidth : 'auto',
        containerMaxWidth : 700,
        containerHeight : 'auto',
        containerMinHeight : 34,
        treeContainerHeight : 300,
        multiple : false
    };

    function Plugin(element, options) {
        let $scope = {
            $rootElement : $(element),
            rootElement : element,
            options : $.extend({}, defaults, options)
        };
        this.init($scope);
    }

    Plugin.prototype.init = function($scope) {
        let $selectContainer = $scope.$rootElement,
            options = $scope.options;
        // Скрываем контейнер с опциями и заодно делаем его мультиселектом, поскольку при единичном режиме выбора(опция multiple) селект всегда будет принимать какое-то значение
        $selectContainer.hide();
        $selectContainer.attr('multiple', 'multiple');
        $selectContainer.find('option').prop('selected', false);
        $selectContainer.find('option.load-selected').prop('selected', true);
        $selectContainer.find('option.load-selected').removeClass('load-selected');
        // Контейнеры
        let $chosenContainerMain = $(`<div class="tree-chosen__container-main"></div>`).insertAfter($selectContainer),
            $treeContainer = $(`<div class="tree-chosen__tree-container"></div>`).appendTo($chosenContainerMain);
        // Настройки
        $chosenContainerMain.css({width: options.containerWidth});
        $chosenContainerMain.css({'max-width' : options.containerMaxWidth});
        $chosenContainerMain.css({height: options.containerHeight});
        $chosenContainerMain.css({'min-height': options.containerMinHeight});
        $treeContainer.css({height: options.treeContainerHeight});
        // Древо
        // Все элементы древа
        let optionElements = [];
        $selectContainer.find('option[data-chosen-parent=""]').each(function() {
            optionElements.push($(this).val());
        });
        $treeContainer.append(
            `<div class="tree-chosen_search-container ui fluid input small">
                <input class="searchField" type="search">
            </div>
            <div class="tree-chosen__scroll-container">
                <ul class="chosen-tree-list-view">
                    ${__fillTree(optionElements)}
                </ul>
            </div>`
        );
        // Если данные пусты выводим инфу их отсутствии
        if ($selectContainer.find('option').length === 0) {
            $treeContainer.find('.tree-chosen__scroll-container').css({'text-align': 'center'});
            $treeContainer.find('.tree-chosen__scroll-container').html('Данные отсутствуют');
        }
        $treeContainer.hide();
        // Открытие выпадающего списка
        $chosenContainerMain.on({
            'click' : () => {
                $treeContainer.css({top: $chosenContainerMain.offset().top > $(window).height()/2 ? - 3 - options.treeContainerHeight : 3 + $chosenContainerMain.height()});
                $treeContainer.css({width: $chosenContainerMain.width()});
                $treeContainer.show();
            }
        });
        // Формируем древо через плагин
        $treeContainer.find('.chosen-tree-list-view').treeListView({
            nodeChoiceMode: options.multiple ? 'multiple' : 'single',
            inputSearchField: $treeContainer.find('.searchField'),
            elementFocusContainer: $treeContainer.find('.tree-chosen__scroll-container')
        });
        // Кликаем по пункту древа списка
        $treeContainer.find('.chosen-tree-list-view span').on({
            'click' : function(event) {
                event.stopPropagation();
                let $selectedOptions = $selectContainer.find('option:selected');
                let $nodeList = $treeContainer.find('.tlv__selected-node');
                $selectContainer.find('option').prop('selected', false);
                $chosenContainerMain.find('.tree-chosen__container-element').remove();
                $nodeList.each(function() { __createChosenOption($selectContainer.find(`option[value="${$(this).data('chosenId')}"]`)); });
                $treeContainer.hide();

                // Проверяем изменились ли выбранные опции
                $selectContainer.find('option:selected').each(function () {
                    let isChange = true, val = $(this).val();
                    $selectedOptions.each(function () {
                        if (val === $(this).val()) {
                            isChange = false;
                            return false;
                        }
                    });
                    if (isChange) {
                        $selectContainer.trigger('change');
                        return false;
                    }
                });
            }
        });
        // Выбираем активные пункты меню
        let $loadOptionList = $selectContainer.find('option:selected');
        $loadOptionList.each(function() { __createChosenOption($(this)); });

        // Закрытие выпадающего списка при клике вне контейнера
        $(document).on({
            'mousedown' : (event) => {
                let $containers = $chosenContainerMain.add($treeContainer);
                if (!$containers.is(event.target) && $containers.has(event.target).length === 0) { $treeContainer.hide(); }
            }
        });

        // Создание элемента для опции, помеченой флагом selected
        function __createChosenOption($selectedOption) {
            $selectedOption.prop('selected', true);
            let selectedElementString =
                `<div class="tree-chosen__container-element">
                    <i class="fas fa-times fa-sm"></i>
                    <span>${$selectedOption.data('chosenName')}</span>
                 </div>`;
            // Убираем выбранный элемент по нажатие на крест
            let $optionElem = $(selectedElementString).appendTo($chosenContainerMain);
            if (!options.multiple) {
                $optionElem.width('calc(100% - 6px)');
            }
            // Убираем элемент из списка по нажатию на иконку креста
            $optionElem.find('i').on({
                'click' : function(event) {
                    event.stopPropagation();
                    $treeContainer.find(`li[data-chosen-id="${$selectedOption.val()}"]`).removeClass('tlv__selected-node');
                    $selectedOption.prop('selected', false);
                    $(this).closest('.tree-chosen__container-element').remove();
                    $treeContainer.hide();
                    $selectContainer.trigger('change');
                }
            });
        }

        // Рекурсивное заполнение древа
        function __fillTree(optionElements) {
            let result = '';
            $(optionElements).each(function() {
                let $option = $selectContainer.find(`option[value="${this}"]`);
                let liClass = '';
                if ($option.prop('selected')) {
                    liClass += 'tlv__selected-node tlv__expand-tree-to-node';
                }
                result +=
                    `<li class="${liClass}" data-chosen-id="${$option.val()}">
                     <span>${$option.data('chosenName')}</span>`;

                let chosenChildren = $option.data('chosenChildrens') + '';
                let chosenChildrenArray = [];
                if (chosenChildren != null && chosenChildren !== '') {
                    chosenChildrenArray = chosenChildren.split(',');
                }
                if (chosenChildrenArray.length > 0) {
                    result +=
                        `<ul>
                            ${__fillTree(chosenChildrenArray)}
                         </ul>`;
                }
                result += `</li>`;
            });
            return result;
        }
    };

    $.fn.treeChosen = function (options) {
        return this.each(function () {
            new Plugin(this, options != null ? options : $(this).data('chosenOptions'));
        });
    };

}) (jQuery, document);

$(() => {
    $('select.tree-chosen').treeChosen();
});