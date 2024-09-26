/*
 * Реализация модального окна фильтра
 *
 * Настройки
 *      - url - URL загрузки страницы окна (GET запрос)
 *      - urlData - параметры загрузки страницы окна (GET запрос)
 *      - filterData - функция, генерирующая объект с данными фильтра
 *      - button - кнопка вызова фильтра (jQuery объект или селектор)
 *      - onApply(data) - колбек применения фильтра (data - обновленные данные фильтра)
 *
 * Date: 15.05.2021
 * Author: mazur_ea
 */
;(($) => {

    'use strict';

    $.modalFilter = options => new Plugin(options);

    // Стандартные настройки
    const defaults = {
        url: null,
        urlData: null,
        button: null,
        filterData: () => {},
        onApply: () => {}
    };

    function Plugin(options) {
        options = $.extend(true, {}, defaults, options);
        bind(options);
    }

    function bind(options) {
        const button = options.button;
        const $button = typeof button === 'string' ? $(button) : button;
        $button.on({
            'click': () => $.get({
                url: options.url,
                data: options.urlData,
                beforeSend: () => togglePreloader(true),
                complete: () => togglePreloader(false)
            }).done(html => initialize(html, options))
        });
    }

    function initialize(html, options) {
        const onApply = options.onApply;
        //
        const $html = $(`<div>${html}</div>`);
        const $modal = $html.find('div.ui.modal');
        const $form = $modal.find('form');
        const $modalHeader = $modal.find('div.header');
        $modalHeader.css({
            'cursor': 'move'
        });
        const $closeIcon = $(`<i class="close icon"></i>`);
        $closeIcon.css({
            'color': '#c14545 !important',
            'top': '1.0535rem',
            'right': '1rem'
        });
        $modal.prepend($closeIcon);
        const $expandIcon = $(`<i class="expand alternate icon"></i>`);
        const expandCss = {
            'cursor': 'pointer',
            'color': '#008000 !important',
            'position': 'absolute',
            'top': '1.0535rem',
            'right': '3rem',
            'z-index': 1,
            'opacity': 0.8,
            'font-size': '1em',
            'width': '2.25rem',
            'height': '2.25rem',
            'padding': '0.625rem 0px 0px'
        };
        $expandIcon.css(expandCss);
        $modal.prepend($expandIcon);
        $expandIcon.on({
            'mouseenter': e => $(e.currentTarget).css('opacity', 1),
            'mouseleave': e => $(e.currentTarget).css('opacity', 0.8),
            'click': e => {
                $(e.currentTarget).toggleClass('expand compress');
                $modal.removeAttr('style').toggleClass('fullscreen');
                $modal.modal('refresh');
            }
        });
        $modal.append($html.find('script, style'));

        let modalObserver;
        let modalObserverStop = false;
        // Закрепление диалога в DOM и его инициализация
        $('body').prepend($modal);
        $modal.modal({
            allowMultiple: true,
            centered: false,
            onHidden: () => {
                modalObserverStop = true;
                clearInterval(modalObserver);
                const $modals = $('div.ui.dimmer.modals > div.ui.modal');
                $modals.filter('.hidden').remove();
                $modals.not('.hidden').each((idx, el) => $(el).modal('refresh'));
            }
        });
        $modal.modal('show');
        formRestore($form, options.filterData());

        // Наблюдатель состояния диалога
        let refreshTimer;
        let modalHeight = $modal.height();
        modalObserver = setInterval(() => {
            const height = $modal.height();
            if (height !== modalHeight) {
                modalHeight = height;
                clearTimeout(refreshTimer);
                refreshTimer = setTimeout(() => modalObserverStop ? '' : $modal.modal('refresh'), 400);
            }
        }, 1);

        // Перетаскивание окна
        $modal.draggable({
            cursor: 'move',
            containment: 'parent',
            handle: $modalHeader
        });

        // Кнопки действий
        $modal.append(`
            <div class="actions">
                <div class="ui small button">
                    <i class="icon blue search"></i>
                    Применить
                </div>
                <div class="ui small button">
                    <i class="icon blue times"></i>
                    Очистить
                </div>
            </div>
        `);
        const $applyButton = $modal.find('div.actions > div.button:eq(0)');
        $applyButton.on({
            'click': () => {
                onApply(formToObject($form));
                $modal.modal('hide');
            }
        });
        const $clearButton = $modal.find('div.actions > div.button:eq(1)');
        $clearButton.on({
            'click': () => formClear($form)
        });
        $modal.on({
            'keyup': e => {
                if (e.key === 'Enter') {
                    e.preventDefault();
                    $applyButton.trigger('click');
                }
            }
        });
    }

})(jQuery);