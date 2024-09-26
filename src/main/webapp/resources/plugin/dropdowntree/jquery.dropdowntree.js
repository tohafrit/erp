/**
 * Плагин для формирование вложенного выпадающего списка
 * Пример:
 *      Если информация должна уходить при отправке формы, то необходимо указывать data-name
 *      <div class="dropdown-test" data-name="test"></div>
 *
 *      <script>
 *          const data = [
 *              {
                    id: 'AK',
                    text: 'Alaska'
                },
                {
                    id: 'AL',
                    text: 'Alabama',
                    child: [
                        {
                            id: 'CA',
                            text: 'California'
                        }
                    ]
                }
 *          ]
 *          $(() => $('.dropdown-test').dropdowntree({
 *              source: data
 *          }));
*       </script>
 */
;(function ($) {

    'use strict';

    // Значения по умолчанию
    const defaults = {
        collapse: true, // первоначальное состояние меню (в раскрытом/скрытом виде)
        duration: 300,  // время анимации раскрытия/скрытия
        defaultText: 'Введите наименование',
        ajax: {
            url: null, // ссылка для обращения
            data: {}   // передаваемые параметры
        },
        source: [], // данные для вывода
        selected: [], // выбранные элементы
        isMultiple: false, // множественный выбор
        arrowStyle: {
            down: 'fas fa-angle-down',
            right: 'fas fa-angle-right'
        }
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
            arrowClass = options.collapse ? options.arrowStyle.right : options.arrowStyle.down,
            innerClass = options.collapse ? 'dt-dropdown__inner_hidden' : '',
            itemSelector = '.dt-dropdown__item, .dt-dropdown__item-parent';

        // Собираем dropdown
        $root.addClass('dt-dropdown');
        const
            $upDownIcon = $('<i class="fas fa-caret-down dt-dropdown__icon"></i>'),
            $name = $root.data('name'),
            $hidden = $(`<input name="${$name.length ? $name : ''}" type="hidden" value="${options.selected}"/>`);
        $root.append($upDownIcon).append($hidden);

        const
            $search = $(`<input class="dt-dropdown__search" autocomplete="off"/>`),
            $text = $(`<div class="dt-dropdown__text dt-dropdown__text_default dt-dropdown__text_visible">${options.defaultText}</div>`);
        $root.append($search).append($text);

        const
            $menu = $('<div class="dt-dropdown__menu"/>'),
            $menuScrollContainer = $('<div class="dt-dropdown__container scrollbar-macosx scrollbar-dynamic"/>');
        $root.append($menu);
        $menu.append($menuScrollContainer);

        if (options.ajax.url !== null && options.ajax.url.length) {
            $.get(options.ajax.url, options.ajax.data).done(data => $menuScrollContainer.append(_recursionBySource(data)));
        } else if (options.source.length) {
            $menuScrollContainer.append(_recursionBySource(options.source));
        }
        $menuScrollContainer.scrollbar();

        // События
        // Раскрытие/скрытие меню
        $upDownIcon.on({
            'click': () => {
                if ($menu.is(':hidden')) {
                    $root.addClass('dt-dropdown_active');
                    const $selected = $menu.find('.dt-dropdown__item_selected');
                    $selected.each((key, value) => _recursionOpen($(value)));
                    _recalculationMenuHeight();
                    $menu.slideDown(options.duration, () => {
                        $search.trigger('focus');
                        if ($selected.length) {
                            $menuScrollContainer.scrollTop($selected.first().position().top);
                        }
                    });
                } else {
                    _closeDropdownMenu();
                }
            }
        });
        $search.on({
            'mousedown': e => {
                // Проверка на клик мыши
                if (e.which === 1 && $menu.is(':hidden')) { // левая кнопка
                    $(e.currentTarget).trigger('focus');
                    $root.addClass('dt-dropdown_active');
                    const $selected = $menu.find('.dt-dropdown__item_selected');
                    $selected.each((key, value) => _recursionOpen($(value)));
                    _recalculationMenuHeight();
                    $menu.slideDown(options.duration, () => {
                        if ($selected.length) {
                            $menuScrollContainer.scrollTop($selected.first().position().top);
                        }
                    });
                }
            },
            'keyup': e => {
                e.stopPropagation();
                switch (e.keyCode) {
                    case 27:
                        _closeDropdownMenu();
                        break;
                    default:
                        $text.toggleClass('dt-dropdown__text_visible', $(e.currentTarget).val().length <= 0);
                        _itemFilter();
                        break;
                }
            }
        });

        // Скрыть/раскрыть список
        $('.dt-dropdown__item-icon').on({
            'click': e => {
                e.stopPropagation();
                const $element = $(e.currentTarget);
                $element.toggleClass(`${options.arrowStyle.down} ${options.arrowStyle.right}`);
                $element.closest('div').next().slideToggle(options.duration, function () {
                    $(this).toggleClass('dt-dropdown__inner_hidden');
                    _recalculationMenuHeight();
                });
            }
        });

        // Выбор пункта меню
        $menu.find(itemSelector).on({
            'click': e => {
                const $this = $(e.currentTarget);
                if (!$this.hasClass('dt-dropdown__item_selected')) {
                    if (!options.isMultiple) {
                        $menu.find('.dt-dropdown__item_selected').removeClass('dt-dropdown__item_selected');
                        $this.addClass('dt-dropdown__item_selected');
                        $text.removeClass('dt-dropdown__text_default').text($this.text());
                        $hidden.val($this.data('value'));
                        _closeDropdownMenu();
                    } else {
                        $this.addClass('dt-dropdown__item_selected');
                        $root.prepend(
                            `<div class="dt-dropdown__multiple" data-value="${$this.data('value')}">
                                ${$this.text()}<i class="fas fa-times dt-dropdown__multiple-close"></i>
                            </div>`
                        );
                        _recalculationMultipleValue();
                        _bindClose();
                    }
                }
            }
        });

        $(document).on({
            'mouseup': e => {
                const element = e.target;
                if (!$root.is(element) && $root.has(element).length === 0 && $menu.is(':visible')) {
                    _closeDropdownMenu();
                }
            }
        });

        // Значение в hidden
        function _recalculationMultipleValue() {
            let array = [];
            $('.dt-dropdown__item_selected').each((key, element) => array.push($(element).data('value')));
            $hidden.val(array.join());
        }

        // Пересчитываем высоту меню
        function _recalculationMenuHeight() {
            const
                defaultContainerHeight = 260, // дефолтное значение из стилей (требуется для scrollbar)
                itemHiddenSelector = '.dt-dropdown__item_hidden',
                itemHeight = $menu.find('.dt-dropdown__item:first').innerHeight();

            let visibleItemCount = $menuScrollContainer.children(itemSelector).not(itemHiddenSelector).length;
            $menuScrollContainer.find('.dt-dropdown__inner:not(.dt-dropdown__inner_hidden)')
                .each((key, element) => {
                    visibleItemCount += $(element).children(itemSelector).not(itemHiddenSelector).length
                });
            const containerHeight = itemHeight * visibleItemCount + visibleItemCount;
            $menu.css({'height': `${containerHeight < defaultContainerHeight ? containerHeight : defaultContainerHeight}px`});
        }

        // Закрыть меню
        function _closeDropdownMenu() {
            $menu.slideUp(options.duration, () => {
                $search.val('').trigger('keyup');
                $menu.find('.dt-dropdown__item_hidden').removeClass('dt-dropdown__item_hidden');
                $root.removeClass('dt-dropdown_active');
            });
        }

        // Рекурсивный обход
        function _recursionBySource(source, padding = 15, defaultPadding = 15) {
            let sourceHtml = '',
                paddingLeft = defaultPadding;

            const hasChildren = source.some(obj => obj.hasOwnProperty('child'));
            if (hasChildren && paddingLeft === defaultPadding) {
                paddingLeft += padding;
            }
            $.each(source, (key, value) => {
                let selectedClass = '';
                if (options.selected.includes(value.id)) {
                    selectedClass = 'dt-dropdown__item_selected';
                    $root.prepend(
                        `<div class="dt-dropdown__multiple" data-value="${value.id}">
                            ${value.text}<i class="fas fa-times dt-dropdown__multiple-close"></i>
                        </div>`
                    );
                    _bindClose();
                }
                if (value.hasOwnProperty('child')) {
                    sourceHtml +=
                        `<div class="dt-dropdown__item-parent ${selectedClass}" data-value="${value.id}" style="padding-left: ${paddingLeft}px">
                            <i class="${arrowClass} dt-dropdown__item-icon" style="left: ${paddingLeft - defaultPadding}px"></i>
                            ${value.text}
                        </div>`;
                    sourceHtml += `<div class="dt-dropdown__inner ${innerClass}">`;
                    paddingLeft += defaultPadding;
                    sourceHtml += _recursionBySource(value.child, paddingLeft);
                    sourceHtml += '</div>';
                } else {
                    paddingLeft = padding;
                    if (hasChildren && paddingLeft === defaultPadding) {
                        paddingLeft += defaultPadding;
                    }
                    sourceHtml +=
                        `<div class="dt-dropdown__item ${selectedClass}" data-value="${value.id}" style="padding-left: ${paddingLeft}px">
                            ${value.text}
                        </div>`;
                }
            });
            return sourceHtml;
        }

        // Поиск элементов
        function _itemFilter() {
            const
                value = $search.val().toLowerCase(),
                $items = $menu.find('.dt-dropdown__item, .dt-dropdown__item-parent');
                $items.removeClass('dt-dropdown__item_hidden');

            $items.each((key, element) => {
                const $this = $(element);
                if ($this.text().toLowerCase().indexOf(value) === -1) {
                    $this.addClass('dt-dropdown__item_hidden');
                    _recursionOpen($this);
                }
            });
            _recalculationMenuHeight();
        }

        // Рекурсивное открытие
        function _recursionOpen($element) {
            const $inner = $element.closest('.dt-dropdown__inner');
            if ($inner.length) {
                $inner.removeClass('dt-dropdown__inner_hidden').prev().find('.dt-dropdown__item-icon')
                    .removeClass(options.arrowStyle.right).addClass(options.arrowStyle.down);
                const $prevInner = $inner.prev().closest('.dt-dropdown__inner')
                if ($prevInner.length) {
                    _recursionOpen($inner.prev());
                }
            }
        }

        // Перепривязка событий
        function _bindClose() {
            $('.dt-dropdown__multiple-close').off().on({
                'click': e => {
                    const $div = $(e.currentTarget).closest('div');
                    $(`.dt-dropdown__item_selected[data-value="${$div.data('value')}"]`).removeClass('dt-dropdown__item_selected');
                    $div.remove();
                    _recalculationMultipleValue();
                    $search.trigger('focus');
                    _toggleDefaultText();
                }
            });
            _toggleDefaultText();
        }

        // Отображение текста по умолчанию
        function _toggleDefaultText() {
            if ($.trim($search.val()).length <= 0 && $('.dt-dropdown__multiple').length <= 0) {
                $search.removeClass('dt-dropdown__search_relative');
                $text.removeClass('dt-dropdown__text_hidden').addClass('dt-dropdown__text_visible');
            } else {
                $text.addClass('dt-dropdown__text_hidden').removeClass('dt-dropdown__text_visible');
                $search.addClass('dt-dropdown__search_relative');
            }
        }
    };

    // Инициализации плагина
    $.fn.dropdowntree = function (options) {
        return this.each(function () {
            new Plugin(this, options);
        });
    };

}) (jQuery);