/**
 * const data = [
        {
            id: 1,
            name: 'Первый пункт',
            href: '/page/1',
            icon: 'fas fa-bullhorn'
        },
        {
            id: 2,
            name: 'Второй пункт',
            icon: 'far fa-file-alt',
            childList: [
                {
                    id: 21,
                    name: 'Первый подпункт второго пункта',
                    href: '/page/21',
                    selected: true
                },
                {
                    id: 22,
                    name: 'Второй подпункт второго пункта',
                    href: '/page/22'
                }
            ]
        },
        {
            id: 3,
            name: 'Третий пункт',
            href: '/page/3',
            icon: 'far fa-edit'
        },
        {
            id: 4,
            name: 'Четвертый пункт',
            href: '/page/4',
            icon: 'fab fa-telegram-plane'
        }
 ];
 */
;(function ($) {

    'use strict';

    // Значения по умолчанию
    const defaults = {
        collapse: true,
        search: {
            inputSelector: null,
            placeholderText: 'Введите наименование'
        },
        ajax: {
            url: null, // ссылка для обращения
            data: {}   // передаваемые параметры
        },
        source: [],
        isMultiple: false,
        arrowStyle: {
            down: 'fas fa-angle-down',
            up: 'fas fa-angle-up'
        },
        useScroll: true,
        startLeftPadding: 60,
        visibilitySelector: null
    }

    // Конструктор
    function Plugin(element, options) {
        let $scope = {
            $rootElement : $(element),
            rootElement : element,
            options : $.extend(true, {}, defaults, options)
        };
        this.init($scope);
    }

    // Функция инициализации плагина
    Plugin.prototype.init = function ($scope) {
        const
            $root = $scope.$rootElement,
            options = $scope.options,
            arrowClass = options.collapse ? options.arrowStyle.up : options.arrowStyle.down,
            innerClass = options.collapse ? 'tf-menu__inner_hidden' : '';

        if (options.search.inputSelector !== null) {
            const $search =
                $(`<div class="tf-search">
                    <i class="fas fa-search tf-search__icon"></i>
                    <input type="text" placeholder="${options.search.placeholderText}" class="tf-search__input"/>
                </div>`);
            $(options.search.inputSelector).first().append($search);

            $search.on({
                'keyup': e => _itemFilter($(e.currentTarget).find('input').val())
            });
        }

        $root.addClass('tf-menu');

        const $menuScrollContainer = $('<div class="tf-menu__container scrollbar-macosx scrollbar-dynamic"/>');
        $root.append($menuScrollContainer);

        if (options.ajax.url !== null && options.ajax.url.length) {
            $.get(options.ajax.url, options.ajax.data).done(data => $menuScrollContainer.append(_recursionBySource(data)));
        } else if (options.source.length) {
            $menuScrollContainer.append(_recursionBySource(options.source));
        } else if ($root.data('source') !== 'undefined' && $root.data('source').length) {
            $menuScrollContainer.append(_recursionBySource($root.data('source')));
        }
        _recursionOpen($('.tf-menu__item_selected:first'));

        // Scrollbar
        if (options.useScroll) {
            $menuScrollContainer.scrollbar();
        }

        // Скрыть/раскрыть список
        $root.find('.tf-menu__parent-icon, .tf-menu__item-parent:not([href])').on({
            'click': e => {
                e.stopPropagation();
                e.preventDefault();
                let $element = $(e.currentTarget);
                if ($element.hasClass('tf-menu__item-parent')) {
                    $element = $element.find('.tf-menu__parent-icon:first');
                }
                $element.toggleClass(`${options.arrowStyle.down} ${options.arrowStyle.up}`);
                $element.closest('a, span').next().slideToggle(300, function () {
                    $(this).toggleClass('tf-menu__inner_hidden');
                });
            }
        });

        // Активируем переключение размера меню
        if (options.visibilitySelector) {
            const $visibility = $(options.visibilitySelector);
            if ($visibility.length) {
                $visibility.addClass('tf-switcher checkbox').append('<input type="checkbox" name="public"><label></label>');
                $visibility.on({
                    'click': e => {
                        const checkbox = $(e.currentTarget).find('input');
                        checkbox.prop('checked', !checkbox.is(':checked'));

                        $('.sidebar__logo').add($('.site-menu')).add($('.menu-search'))
                            .add($('.sidebar__nav')).add($('.sidebar__nav-closed')).toggle();
                        $('.sidebar').toggleClass('sidebar_close');
                        $('.body').toggleClass('body_close');
                    }
                });

                const $activeMenuItems = $menuScrollContainer.find("> a[class^='tf-menu__item']");
                let itemsHTML = '';
                $activeMenuItems.each((key, value) => {
                    itemsHTML += `<li class="tf-closed-menu__li">
                                <a class="tf-closed-menu__a" href="` + $(value).attr('href') + `" title="` + $.trim($(value).text()) + `">
                                    <i class="` + $.trim($(value).find('i').attr('class').replace('tf-menu__icon-mind')) + `"></i>
                                </a>
                            </li>`;
                });
                const closedMenuHTML = `<nav class="sidebar__nav-closed" style="display: none;">
                    <ul class="tf-closed-menu">${itemsHTML}</ul>
                </nav>`;
                $root.after($(closedMenuHTML));
            }
        }

        // Рекурсивный обход
        function _recursionBySource(source, padding = options.startLeftPadding) {
            let sourceHtml = '';
            $.each(source, (key, value) => {
                const
                    selectedClass = value.selected ? 'tf-menu__item_selected' : '',
                    $mindIcon = value.hasOwnProperty('icon') ? `<i class="${value.icon} tf-menu__icon-mind"></i>` : '',
                    hasChild = value.hasOwnProperty('childList') && value.childList.length,
                    $parentIcon = hasChild ? `<i class="${arrowClass} tf-menu__parent-icon"></i>` : '',
                    itemClass = hasChild ? 'tf-menu__item-parent' : 'tf-menu__item',
                    hasUrl = value.hasOwnProperty('href') && value.href.length;

                if (hasUrl) {
                    sourceHtml +=
                        `<a href="${value.href}" class="${itemClass} ${selectedClass}" data-id="${value.id}">
                            <span class="tf-menu__item-wrapper" style="padding-left: ${padding}px">
                                ${$mindIcon}${value.name}
                            </span>
                            ${$parentIcon}
                        </a>`;
                } else {
                    sourceHtml +=
                        `<span class="${itemClass} ${selectedClass}" data-id="${value.id}">
                            <span class="tf-menu__item-wrapper" style="padding-left: ${padding}px">
                                ${$mindIcon}${value.name}
                            </span>
                            ${$parentIcon}
                        </span>`;
                }
                if (value.hasOwnProperty('childList')) {
                    sourceHtml += `<div class="tf-menu__inner ${innerClass}">`;
                    sourceHtml += _recursionBySource(value.childList, padding + 10);
                    sourceHtml += '</div>';
                }
            });
            return sourceHtml;
        }

        // Поиск элементов
        function _itemFilter(text) {
            const
                value = text.toLowerCase(),
                $items = $menuScrollContainer.find('.tf-menu__item, .tf-menu__item-parent');
            $items.removeClass('tf-menu__item_pointer');

            if (text.length) {
                $items.each((key, element) => {
                    const $this = $(element);
                    if ($this.text().toLowerCase().indexOf(value) !== -1) {
                        $this.addClass('tf-menu__item_pointer');
                        _recursionOpen($this);
                    }
                });
            }
        }

        // Рекурсивное открытие
        function _recursionOpen($element) {
            const $inner = $element.closest('.tf-menu__inner');
            if ($inner.length) {
                $inner.removeClass('tf-menu__inner_hidden').prev().find('.tf-menu__parent-icon')
                    .removeClass(options.arrowStyle.up).addClass(options.arrowStyle.down);
                const $prevInner = $inner.prev().closest('.tf-menu__inner')
                if ($prevInner.length) {
                    _recursionOpen($inner.prev());
                }
            }
        }
    };

    // Инициализации плагина
    $.fn.treefactory = function (options) {
        return this.each(function () {
            new Plugin(this, options);
        });
    };

}) (jQuery);