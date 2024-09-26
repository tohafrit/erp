/*
    Плагин для создания из <ul> <li> элементов древовидного вида списка.
    Пример:

        <ul class="tree-list-view">
            <li>
                <span>Cat</span>
            </li>
            <li>
                <span>Cat</span>
                <ul>
                    <li>
                        <span>tom</span>
                    </li>
                </ul>
            </li>
        </ul>

    --- Способы инициализации:
        1) Указать у корневого <ul> класс "tree-list-view"
        2) Создать дерево через js код:
            $(".treeList").treeListView(); , где treeList - любой класс у корневого <ul> элемента.

    --- Способ инициализации настроек:
        1) Если задан класс "tree-list-view", то настроки задаются параметром "data-tree-list-view-options" у корневого <ul> элемента.
           Если не задать настройки, то произойдет инициализация со стандартными параметрами.
           Пример:
                data-tree-list-view-options= '{ "collapseAll": false, "nodeTurnDelay": 300, "nodeChoiceMode": "multiple", "inputSearchField": ".itest" ,"elementClickCollapseAll": ".hide"}'
        2) Если дерево создано через js код, то настроки указываются в скобках при инициализации.
           Если не задать настройки, то произойдет попытка чтения настроек через атрибут "data-tree-list-view-options" у корневого <ul> элемента.
           Если и у корневого элемента не оказалось настроек, то произодйет иницализация со стандартными параметрами.
           Пример:
                $(".treeList").treeListView({collapseAll: true, nodeTurnDelay: 300, nodeChoiceMode: 'single', inputSearchField: $('.itest'), elementFocusContainer: $('#focusCont')});

    --- Общие ограничения:
        1) Под тегом <li> может быть только один тег <a> или <span>
        2) Только один элемент <li> может быть отмечен классом "tlv__focused-node"
        3) Под тегом <li> могут быть два элемента <i> (один "tlv__node-expand-toggle" другой "tlv__node-collapse-toggle")
            или один "tlv__node-empty-toggle"

    --- Описание настроек:
        1) collapseAll:
            true - при загрузке страницы весь список будет свернут, false - развернут. По умолчанию используется true

        2) nodeTurnDelay:
            задержка при анимации скрытия/расскрытия пунктов списка. По умолчанию значение равно 300

        3) nodeChoiceMode:
            режим выбора пунктов списка
                "none" - выбор пунктов неиспользуется
                "single" - доступен выбор одного пункта дерева
                "multiple" - режим множественного выбора пунктов списка
                Выбранный элемент автоматически помечается классом "tlv__selected-node"

        4) nodeChoiceStyle:
            Определяет стиль выбора элемента. "fill" - заливка пункта меню, "underline" - подчеркивание линией. По умолчанию используется "fill"

        4) elementClickExpandAll:
            В эту настройку указывается jquery селектор элемента или jquery объект по клику на который будет выполняться полное расскрытия списка

        5) elementClickCollapseAll:
            В эту настройку указывается jquery селектор элемента или jquery объект по клику на который будет выполняться полное сокрытие списка

        6) elementFocusContainer:
            В эту настройку указывается jquery селектор элемента или jquery объект в котором будет производится фокусировка на элементе списка.
            Срабатывает когда элемент контейнера имеет скроллбар по X или Y.
            По умолчанию используется на первый элемент совпадения при поиске по списку(поиск включается отдельной настройкой).
            При загрузке происходит фокус для элемента <li> у которого указан класс "tlv__focused-node".
            Под фокусом понимается пролистывание скролируемого контейнера пока фокусируемый элемент не окажется в центре контейнера
            Контейнером может быть только <div> или <body> элемент

        7) inputSearchField:
            Поле ввода типа <input type="search">. Задается как jquery селектор элемента или jquery объект.
            При наборе символов в поле происходит поиск элементов по списку (элементы будут выделены рамкой).

        8) elementClickSelectAll:
            В эту настройку указывается jquery селектор элемента или jquery объект по клику на который будут выбраны все элементы списка

        9) elementClickDeselectAll:
            В эту настройку указывается jquery селектор элемента или jquery объект по клику на который выбор всех элементов списка будет отменен

    --- Классовые настройки
        1) "tlv__expand-tree-to-node" - указав у элемента <li> этот класс при загрузке будет выполнена полная развертка древа ДО этого элемента
        2) "tlv__expanded-node" - указав у элемента <li> этот класс при загрузке будет выполнена развертка подсписка указанного пункта меню
        3) "tlv__collapsed-node" - указав у элемента <li> этот класс при загрузке будет выполнена свертка подсписка указанного пункта меню
        4) "tlv__focused-node" - указав у элемента <li> этот класс при загрузке будет выполнен фокус на указанный элемент. Работает только при наличии контейнера в настройке elementFocusContainer.
        5) "tlv__selected-node" - указав у элемента <li> этот класс при загрузке этот элемент будет помечен как активный
        6) Если иконки пунктов не устраивают, то можно указать свои иконки <i> внутри тега <li> со следующими классами:
            "tlv__node-expand-toggle" - иконка для расскрытия подсписка
            "tlv__node-collapse-toggle" - иконка для сокрытия подсписка
            "tlv__node-empty-toggle" - иконка для стандартного пункта списка
        7) "tlv__disable-selection-node" - указав у элемента <li> этот класс, элемент станет недоступен для выбора (аналогично эффекту "nodeChoiceMode" = none)

    --- Изменение стилей:
            Просто указываем у ul элемента какой либо класс и переопределяем классы из jquery.tree.list.view.css
*/
;(function($) {

    'use strict';

    // Значения по умолчанию
    const defaults = {
        collapseAll : true,
        nodeTurnDelay : 300,
        nodeChoiceMode : 'single',
        nodeChoiceStyle : 'fill',
        elementClickExpandAll : null,
        elementClickCollapseAll : null,
        elementFocusContainer : null,
        inputSearchField : null,
        elementClickSelectAll : null,
        elementClickDeselectAll : null,
        sortable : false,
        changeURL : null
    },
    // Для валидации настроек
    optionPreferences = {
        collapseAll : {nullable : false, type : 'boolean'},
        nodeTurnDelay : {nullable : false, type : 'number'},
        nodeChoiceMode : {nullable : false, type : 'string'},
        nodeChoiceStyle : {nullable : false, type : 'string'},
        elementClickExpandAll : {nullable : true, type : 'jQuery'},
        elementClickCollapseAll : {nullable : true, type : 'jQuery'},
        elementFocusContainer : {nullable : true, type : 'jQuery', allowableTags : ['DIV', 'BODY']},
        inputSearchField : {nullable : true, type : 'jQuery', allowableTags : ['INPUT'], allowableTagProperties : {type : 'search'}},
        elementClickSelectAll : {nullable : true, type : 'jQuery'},
        elementClickDeselectAll : {nullable : true, type : 'jQuery'},
        sortable : {nullable : true, type : 'boolean'},
        changeURL : {nullable : true, type : 'string'}
    };

    // Конструктор
    function Plugin(element, options) {
        let $scope = {
            $rootElement : $(element),
            rootElement : element,
            options : $.extend({}, defaults, options)
        };
        this.validateOptions($scope);
        this.init($scope);
    }

    // Функция валидации настроек
    Plugin.prototype.validateOptions = function($scope) {
        let $treeObject = $scope.$rootElement,
            treeObject = $scope.rootElement,
            options = $scope.options;
        if (treeObject == null || $treeObject == null || $treeObject.prop("tagName") !== 'UL') {
            alert('[tree-list-view]: Invalid tree object. The object must be an "ul" element');
        }
        // Валидация допустимых настроек
        $(Object.keys(options)).each(function(index, value) {
            if ($.inArray(value, Object.keys(defaults)) === -1) {
                alert(`[tree-list-view]: Invalid property name "${value}"`);
            }
            __validateOption(value);
        });

        // Общая функция валидации настройки
        function __validateOption(propertyKey) {
            let optionPreference = optionPreferences[propertyKey],
                option = options[propertyKey];
            // null check
            if (!optionPreference.nullable) {
                if (option == null) {
                    alert(`[tree-list-view]: The "${propertyKey}" can not be null or undefined`);
                }
            }
            // type check
            if (option != null) {
                if (optionPreference.type === 'jQuery') {
                    if (typeof option === 'string') {
                        option = $(option);
                        if (option.size() === 0) {
                            alert(`[tree-list-view]: Can not find an object by jQuery selector "${options[propertyKey]}" for property "${propertyKey}"`);
                        }
                    } else if (option instanceof jQuery) {
                        if (option.size() === 0) {
                            alert(`[tree-list-view]: Can not find an jQuery object for property "${propertyKey}"`);
                        }
                    } else {
                        alert(`[tree-list-view]: Invalid value for property "${propertyKey}"`);
                    }
                    options[propertyKey] = option;
                } else if (typeof option !== optionPreference.type) {
                    alert(`[tree-list-view]: The "${propertyKey}" property type must be a "${optionPreference.type}" type`);
                }
            }
            // Проверки только для jQuery объектов
            // allowableTags
            if (option != null && optionPreference.allowableTags != null && optionPreference.allowableTags.length > 0) {
                if ($.inArray(option.prop("tagName"), optionPreference.allowableTags) === -1) {
                    alert(`[tree-list-view]: The "${propertyKey}" property must contain "${optionPreference.allowableTags.join(' or ')}" html element`);
                }
            }
            // allowableProperties
            if (option != null && optionPreference.allowableTagProperties != null) {
                $(Object.keys(optionPreference.allowableTagProperties)).each((index, value) => {
                    let optionProp = option.prop(value),
                        optionPropValidValue = optionPreference.allowableTagProperties[value];
                    if (optionProp !== optionPropValidValue) {
                        alert(`[tree-list-view]: The "${propertyKey}" property must contain html element with attribute "${value}" = ${optionPropValidValue}`);
                    }
                });
            }
        }
    };

    // Функция инициализации плагина
    Plugin.prototype.init = function($scope) {
        let $treeObject = $scope.$rootElement,
            options = $scope.options;

        $treeObject.addClass('tlv__root');

        // Проверка на несколько <span> или <a> элементов в одном <li> пункте
        $treeObject.find('li').each(function() {
            $(this).children('span, a').addClass('tlv__style-' + options.nodeChoiceStyle);
            if ($(this).children('span, a').size() > 1) {
                alert(`[tree-list-view]: You cannot specify more than one "span" or "a" element inside the "li" tag\n${$(this).html()}`);
            }
        });

        // Проставляем стандартные значения кнопок переключения
        $treeObject.find('li').each(function() {
            let iconCount = $(this).children('i.tlv__node-expand-toggle, i.tlv__node-collapse-toggle, i.tlv__node-empty-toggle').size(),
                iconCountE = $(this).children('i.tlv__node-expand-toggle').size(),
                iconCountC = $(this).children('i.tlv__node-collapse-toggle').size();
            if (
                iconCount > 2
                || (iconCount === 2 && iconCountE === 0 && iconCountC === 0)
                || (iconCount === 2 && iconCountE === 0 && iconCountC === 1)
                || (iconCount === 2 && iconCountE === 1 && iconCountC === 0)
            ) {
                alert(
                    '[tree-list-view]: <li> element can contain a maximum of two <i> elements (one with class "tlv__node-expand-toggle" and one with class"tlv__node-collapse-toggle") ' +
                    + 'or one <i> element with class "tlv__node-expand-toggle" or "tlv__node-collapse-toggle" or "tlv__node-empty-toggle"\n' + $(this).html()
                );
            } else if (iconCount === 1) {
                return true;
            }
            // Переключатель
            if ($(this).children('ul').size() > 0) {
                // Кнопка раскрытия
                if ($(this).children('i.tlv__node-expand-toggle').size() === 0) {
                    $(this).prepend('<i class="fas fa-angle-right fa-1x tlv__node-expand-toggle"></i>');
                }
                // Кнопка свертки
                if ($(this).children('i.tlv__node-collapse-toggle').size() === 0) {
                    $(this).prepend('<i class="fas fa-angle-down fa-1x tlv__node-collapse-toggle"></i>');
                }
            } else { // Обычная иконка пункта
                if ($(this).children('i.tlv__node-empty-toggle').size() === 0) {
                    $(this).prepend('<i class="fas fa-circle fa-xs tlv__node-empty-toggle"></i>');
                }
            }
        });

        // Сворачиваем/разворачиваем список при загрузке
        $treeObject.find('ul').toggle(!options.collapseAll);
        $treeObject.find('i.tlv__node-expand-toggle').toggle(options.collapseAll);
        $treeObject.find('i.tlv__node-collapse-toggle').toggle(!options.collapseAll);

        // Функция выполняет раскрытие всего дерева до указанного элемента
        function _expandTreeToNode($node) {
            if ($node == null) {
                return;
            }
            if (!($node instanceof jQuery) || $node.prop("tagName") !== 'LI') {
                alert('[tree-list-view]: Invalid "expand tree to node" object. The object must be an "li" element in the jQuery wrapper');
            }
            while ($node.parent('ul').parent('li').size() > 0) {
                if ($node.parent('ul.tlv__root').size() > 0) break;
                $node.parent('ul').show();
                $node.parent('ul').parent('li').children('i.tlv__node-expand-toggle:first').hide();
                $node.parent('ul').parent('li').children('i.tlv__node-collapse-toggle:first').show();
                $node = $node.parent('ul').parent('li');
            }
        }

        // Расскрытие всего древа до элемента <li class="tlv__expand-tree-to-node">
        $treeObject.find('li.tlv__expand-tree-to-node').each(function() {
            _expandTreeToNode($(this));
        });

        // Расскрытие только конкретного подсписка элемента <li class="tlv__expanded-node">
        $treeObject.find('li.tlv__expanded-node').each(function() {
            $(this).children('ul').show();
            $(this).children('i.tlv__node-expand-toggle:first').hide();
            $(this).children('i.tlv__node-collapse-toggle:first').show();
        });

        // Сокрытие только конкретного подсписка элемента <li class="tlv__collapsed-node">
        $treeObject.find('li.tlv__collapsed-node').each(function() {
            $(this).children('ul').hide();
            $(this).children('i.tlv__node-expand-toggle:first').show();
            $(this).children('i.tlv__node-collapse-toggle:first').hide();
        });

        // Развернуть весь список
        if (options.elementClickExpandAll != null) {
            options.elementClickExpandAll.on('click', () => {
                if (options.elementClickCollapseAll != null) {
                    $treeObject.find('ul').show({
                        duration: 1,
                        start: () => { options.elementClickCollapseAll.prop('disabled', true); },
                        always: () => { options.elementClickCollapseAll.prop('disabled', false); }
                    });
                } else {
                    $treeObject.find('ul').show();
                }
                $treeObject.find('i.tlv__node-collapse-toggle').show();
                $treeObject.find('i.tlv__node-expand-toggle').hide();
            });
        }

        // Свернуть весь список
        if (options.elementClickCollapseAll != null) {
            options.elementClickCollapseAll.on('click', () => {
                if (options.elementClickExpandAll != null) {
                    $treeObject.find('ul').hide({
                        duration: options.nodeTurnDelay,
                        start: () => { options.elementClickExpandAll.prop('disabled', true); },
                        always: () => { options.elementClickExpandAll.prop('disabled', false); }
                    });
                } else {
                    $treeObject.find('ul').hide(options.nodeTurnDelay);
                }
                $treeObject.find('i.tlv__node-collapse-toggle').hide();
                $treeObject.find('i.tlv__node-expand-toggle').show();
            });
        }

        // Функция для центрирования элемента в конейнере с активным скролом
        function _scrollCenterFocusNode($container, $node) {
            if ($container == null || $container === '' || $node == null || $node.size() !== 1) {
                return;
            }
            let containerTopCenterOffset = 0,
                containerLeftCenterOffset = 0,
                nodeTopOffset = $node.offset().top,
                nodeLeftOffset = $node.offset().left;
            if ($container.prop("tagName") === 'BODY') {
                containerTopCenterOffset = $container.outerHeight()/2;
                containerLeftCenterOffset = $container.outerWidth()/2;
            } else if ($container.prop("tagName") === 'DIV') {
                containerTopCenterOffset = $container.offset().top + $container.outerHeight()/2;
                containerLeftCenterOffset = $container.offset().left + $container.outerWidth()/2;
            }
            // Вертикальное центрирование
            if (nodeTopOffset > containerTopCenterOffset) {
                $container.animate({scrollTop: nodeTopOffset - containerTopCenterOffset + $container.scrollTop() + $node.outerHeight()/2}, 1);
            } else if (nodeTopOffset < containerTopCenterOffset) {
                $container.animate({scrollTop: $container.scrollTop() - (containerTopCenterOffset - nodeTopOffset) + $node.outerHeight()/2}, 1);
            }
            // Горизонтальное центрирование
            if (nodeLeftOffset > containerLeftCenterOffset) {
                $container.animate({scrollLeft: nodeLeftOffset - containerLeftCenterOffset + $container.scrollLeft() + $node.outerWidth()/2}, 1);
            } else if (nodeLeftOffset < containerLeftCenterOffset) {
                $container.animate({scrollLeft: $container.scrollLeft() - (containerLeftCenterOffset - nodeLeftOffset) + $node.outerWidth()/2}, 1);
            }
        }

        // Для заданного контейнера class="tlv__focused-node-container"
        // выполняется скролл до элемента <li class="tlv__focused-node">, если это возможно
        let $propFocusedNode = $treeObject.find('li.tlv__focused-node');
        if ($propFocusedNode.size() > 1) {
            alert('[tree-list-view]: Only one <li> element can have a class "tlv__focused-node"');
        }
        _scrollCenterFocusNode(options.elementFocusContainer, $propFocusedNode);

        // Выбор активного пункта меню при нажатии
        if (options.nodeChoiceMode !== 'none') {
            $treeObject.find('li > span, li > a').not('.tlv__disable-selection-node > span, a').on({click : function() {
                if (options.nodeChoiceMode === 'single') {
                    $treeObject.find('li > span,li > a').each(function() {
                        $(this).parent('li').removeClass('tlv__selected-node');
                    });
                }
                if (options.nodeChoiceMode === 'single' || options.nodeChoiceMode === 'multiple') {
                    $(this).parent('li').toggleClass('tlv__selected-node');
                }
            }});
        }

        // Закрытие/открытие нодов по кликам на +/-
        $treeObject.find('i.tlv__node-expand-toggle').on({click : function(event) {
            event.stopPropagation();
            $(this).parent('li').children('ul').show(options.nodeTurnDelay);
            $(this).hide();
            $(this).parent('li').children('i.tlv__node-collapse-toggle:first').show();
        }});
        $treeObject.find('i.tlv__node-collapse-toggle').on({click : function(event) {
            event.stopPropagation();
            $(this).parent('li').children('ul').hide(options.nodeTurnDelay);
            $(this).hide();
            $(this).parent('li').children('i.tlv__node-expand-toggle:first').show();
        }});

        // Выбор всех элементов списка по клику
        if (options.elementClickSelectAll != null) {
            options.elementClickSelectAll.on('click', () => {
                $treeObject.find('li').addClass('tlv__selected-node');
            });
        }

        // Отмена выбора всех элементов списка по клику
        if (options.elementClickDeselectAll != null) {
            options.elementClickDeselectAll.on('click', () => {
                $treeObject.find('li').removeClass('tlv__selected-node');
            });
        }

        // Поиск узлов
        if (options.inputSearchField != null) {
            // Основная функция поиска по дереву
            options.inputSearchField.on('input', function() {
                if ($(this).val()) {
                    let $inputThis = $(this);
                    $treeObject.find('li').each(function() {
                        let nodeValue = $(this).children('span:first, a:first').text().trim().replace(/\s\s+/g, ' ').toLowerCase();
                        try {
                            nodeValue.search($inputThis.val().toLowerCase())
                        } catch (e) {
                            return true;
                        }
                        if ($inputThis.val() != null && nodeValue.search($inputThis.val().toLowerCase()) !== -1) {
                            if ($(this).children('ul').not(':visible')) {
                                _expandTreeToNode($(this));
                            }
                            $(this).addClass('tlv__search-found-node');
                        } else {
                            $(this).removeClass('tlv__search-found-node');
                        }
                    });
                    // Выполняем фокус на первый элемент в центре контейнера
                    let $foundNode = $treeObject.find('li.tlv__search-found-node:first').children('span:first, a:first');
                    _scrollCenterFocusNode(options.elementFocusContainer, $foundNode);
                } else {
                    $treeObject.find('li').each(function() {
                        $(this).removeClass('tlv__search-found-node');
                    });
                }
            });
        }

        // Подключение сортировки из jQuery UI
        if (options.sortable) {
            $treeObject.find('li:not(:has(> ul))').each(function() {
                $(this).append('<ul></ul>');
            });
            $treeObject.parent().find('ul').disableSelection().sortable({
                connectWith: 'ul',
                tolerance: 'pointer',
                cursorAt: {
                    left: 100,
                    top: 10
                },
                cursor: 'move',
                opacity: 0.7,
                delay: 300,
                placeholder: 'highlighter',
                helper: 'clone',
                revert: true,
                sort: (event, ui) => {
                    $treeObject.find('ul').css({'min-height':'30px', 'border':'1px dashed #e1e1e1'});
                    ui.helper.find('ul').removeAttr('style');
                },
                stop: (event, ui) => {
                    $treeObject.find('ul').removeAttr('style');
                    if (options.changeURL != null) {
                        $.post(options.changeURL,
                            {
                                entityId: ui.item.data('id'),
                                parentEntityId: ui.item.closest('ul').parent('li').data('id'),
                                mainEntityId: $treeObject.data('mainId')
                            })
                            .done((data) => {
                                $('[class$="-tree-container"]').html(data);
                            }).then(() => {});
                    }
                }
            });
        }
    };

    // Функция инициализации плагина
    $.fn.treeListView = function (options) {
        return this.each(function () {
            new Plugin(this, options != null ? options : $(this).data('treeListViewOptions'));
        });
    };

}) (jQuery);

$(() => {
    $('ul.tree-list-view').treeListView();
});