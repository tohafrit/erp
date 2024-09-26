/*
 * Реализация общего модального окна приложения
 *
 * Настройки
 *      - loadURL - URL загрузки страницы окна (GET запрос)
 *      - loadData - параметры загрузки страницы окна (GET запрос)
 *      - submitURL - URL для отправки данных формы страницы (POST запрос)
 *          Параметр необходим для отправки данных на сервер под тегом <form>.
 *          Действие отправки автоматически цепляется к кнопке вида <button type="submit">
 *      - submitAsJson - параметр указывает на необходимость пересылки тела формы в виде JSON.
 *          JSON данные не будут включать в себя <input type="file">. JSON форму можно получить @RequestPart form: (DynamicObject или String)
 *          Файлы же можно получить по имени параметра также используя @RequestPart.
 *      - onSubmitSuccess(response) - колбек при успешном сабмите формы по параметру submitURL
 *          - response - объект с атрибутами ответа (мапы response.errors и response.attributes)
 *      - onInitComplete - колбек при завершении инициализации модального окна
 *      - onAfterClose - колбек при полном закрытии окна
 *
 *      'cb.onInitSubmit' - колбек привязки действия при сабмите (доступно только при наличии <button type="submit">)
 *
 * Date: 2020.04.06
 * Author: mazur_ea
 */
;(($) => {

    'use strict';

    $.modalWindow = options => new Plugin(options);

    // Стандартные настройки
    const defaults = {
        loadURL: null,
        loadData: null,
        submitURL: null,
        submitAsJson: false,
        onSubmitSuccess: () => {},
        onInitComplete: () => {},
        onAfterClose: () => {}
    };

    function Plugin(options) {
        options = $.extend(true, {}, defaults, options);
        initialize(options);
    }

    function initialize({
        loadURL,
        loadData,
        submitURL,
        submitAsJson,
        onSubmitSuccess,
        onInitComplete,
        onAfterClose
    }) {
        $.get({
            url: loadURL,
            data: loadData,
            beforeSend: () => togglePreloader(true),
            complete: () => togglePreloader(false)
        }).done(html => loadDone(
            html, {
                submitURL,
                submitAsJson,
                onSubmitSuccess,
                onInitComplete,
                onAfterClose
            }
        ));
    }

    function loadDone(html, { submitURL, submitAsJson, onSubmitSuccess, onInitComplete, onAfterClose }) {
        const $html = $(`<div>${html}</div>`);
        const $modal = $html.find('div.ui.modal');
        const $modalHeader = $modal.find('div.header');
        $modalHeader.css({
            'cursor': 'move',
            'margin-right': '10px'
        });
        const $preloader = $(
            `<div class="std-modal-preloader">
                <div class="ui inverted dimmer active">
                    <div class="ui loader elastic blue">
                    </div>
                </div>
            </div>`
        ).prependTo($modal);
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
                onAfterClose();
            }
        });
        $modal.modal('show');

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

        // Стилизация прелоадера
        const preloaderHeight = $modalHeader.length ? $modalHeader.outerHeight(true) : 0;
        $preloader.css({
            'margin-top': preloaderHeight,
            'height': `calc(100% - ${preloaderHeight}px)`,
            'position': 'absolute',
            'width': '100%',
            'top': 0,
            'left': 0
        });

        // Обновление индексов для списочных параметров
        listFormAttr($modal.find('.dialog-list-form-container'));
        onInitComplete();
        // Кнопка сабмита формы
        $modal.find('button[type=submit]:first').on({
            'click': event => {
                event.preventDefault();
                $modal.trigger('cb.onInitSubmit');
                // Обновление индексов для списочных параметров
                listFormAttr($modal.find('.dialog-list-form-container'));
                // CKE
                $modal.find('textarea').each(function () {
                    const instance = CKEDITOR.instances[$(this).attr('name')];
                    if (instance) $(this).html(instance.getData());
                });
                let form;
                const $form = $modal.find('form');
                if (submitAsJson) {
                    form = new FormData();
                    const formObj = formToObject($form);
                    $form.find('input[type="file"]').each((inx, elem) => {
                        const name = elem.name;
                        delete formObj[name];
                        const files = elem.files;
                        const length = files.length;
                        if (length > 1) files.forEach(file => form.append(`${name}[]`, file));
                        else if (length === 1) form.append(name, files[0]);
                    });
                    form.append('form', new Blob([JSON.stringify(formObj)], { type: 'application/json' }));
                } else {
                    form = new FormData($form.get(0));
                }
                const $divErrors = $modal.find('div.ui.message.error');
                $.post({
                    url: submitURL,
                    data: form,
                    contentType: false,
                    processData: false,
                    beforeSend: () => {
                        $divErrors.text('');
                        $divErrors.removeClass('visible');
                        $modal.find('div.field').removeClass('error');
                        $preloader.show();
                    },
                    complete: () => $preloader.hide()
                }).done(response => {
                    if (response == null) return;
                    if (response.error) {
                        $.each(response.errors, (field, error) => {
                            const $errorField = $divErrors.filter(`[data-field=${field}]`);
                            $errorField.text(error.toString()).addClass('visible');
                            $errorField.closest('div.field').addClass('error');
                        });
                    } else {
                        $modal.modal('hide');
                        onSubmitSuccess(response);
                    }
                });
            }
        });

        // Отключаем сабмит формы по кнопке Enter
        $modal.find('form').on({
            'keydown': e => {
                if (e.key === 'Enter') {
                    e.preventDefault();
                    return false;
                }
            }
        });
    }

})(jQuery);