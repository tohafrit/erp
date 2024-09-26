/**
 * Метод для форматированного вывода даты в виде строки
 * @param {Date, Array} date дата или массив с датой (обычно внутри JSON) вида [год, месяц, день, час, минута, секунда]
 * @param {String} pattern паттерн формата даты
 * @returns {String} строка с датой нужного формата
 */
function dateToString(date, pattern) {
    let result = '';
    if (date == null || pattern == null) return result;
    if (date instanceof Date) date = [date.getFullYear(), date.getMonth() + 1, date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds()];
    if (Array.isArray(date) && date.length) {
        result = pattern;
        const _fnFormat = value => value.length === 1 ? `0${value}` : value;
        const patternMap = new Map();
        const patternKeys = ['dd', 'MM', 'yyyy', 'yy', 'HH', 'mm', 'ss'];
        $(date.concat()).each((index, value) => {
            value = value.toString();
            switch (index) {
                case 0:
                    patternMap.set('yyyy', value);
                    patternMap.set('yy', value.substring(2));
                    break;
                case 1:
                    patternMap.set('MM', _fnFormat(value));
                    break;
                case 2:
                    patternMap.set('dd', _fnFormat(value));
                    break;
                case 3:
                    patternMap.set('HH', _fnFormat(value));
                    break;
                case 4:
                    patternMap.set('mm', _fnFormat(value));
                    break;
                case 5:
                    patternMap.set('ss', _fnFormat(value));
                    break;
            }
        });
        // Заменяем паттерн значениями
        patternMap.forEach((value, key) => result = result.replace(key, value));
        // Если какие-то значения не были заменены, то убираем их
        patternKeys.forEach(value => result = result.replace(new RegExp(`(\.|\:|\-|\_|\/|\\|\s)${value}`, 'g'), ''));
    }
    return result;
}

/**
 * Метод для стандартного формата вывода даты без времени в виде строки
 * @param {Date, Array} date дата или массив с датой (обычно внутри JSON) вида [год, месяц, день, час, минута, секунда]
 * @returns {String} строка с датой нужного формата
 */
function dateStdToString(date) {
    return dateToString(date, 'dd.MM.yyyy');
}

/**
 * Метод для стандартного формата вывода времени в виде строки
 * @param {Array} time массив с временем вида [час, минута, секунда] или [час, минута]
 * @returns {String} строка с временем нужного формата
 */
function timeStdToString(time) {
    return dateToString([0, 0, 0].concat(time), 'HH:mm:ss');
}

/**
 * Метод для стандартного формата вывода даты и времени в виде строки
 * @param {Date, Array} date дата или массив с датой (обычно внутри JSON) вида [год, месяц, день, час, минута, секунда]
 * @returns {String} строка с датой нужного формата
 */
function dateTimeStdToString(date) {
    return dateToString(date, 'dd.MM.yyyy HH:mm:ss');
}

/**
 * Метод для сортировки дат и времени, получаемых табулятором
 * @param {Array} a,
 * @param {Array} b - массивы с датой, временем или датовременем вида [год, месяц, день, час, минута, секунда] и подобных
 * @returns {int} результат сравнения
 */
function sortDate(a, b) {
    if (a == null) return -1;
    if (b == null) return 1;
    for (let i = 0; i < a.length; i++) {
        if (a[i] !== b[i]) return a[i] - b[i];
    }
    return 0;
}

/**
 * Метод для включения/выключения preloader
 * Активирует прелоадер активного модального окна. Если такового нет, то активируется прелоадер
 * рабочей области
 * @param {boolean} flag флаг переключения (true - показать, false - скрыть)
 */
let preloaderCount = 0;
function togglePreloader(flag) {
    const $modals = $('div.ui.dimmer.modals > div.ui.modal:not(.modal-confirm)');
    if (flag) {
        preloaderCount++;
        if ($modals.length > 0) {
            $modals.last().find('div.std-modal-preloader').show();
        } else {
            $('div.workarea-preloader').addClass('active');
        }
    } else {
        preloaderCount--;
        if (preloaderCount <= 0) {
            preloaderCount = 0;
            $modals.find('div.std-modal-preloader').hide();
            $('div.workarea-preloader').removeClass('active');
        }
    }
}

/**
 * Функция установки индексов для атрибутов формы множественного выбора
 * Читает атрибут data-list-form-attribute для элементов input и select по каждой строке индексируя их
 * Если указан атрибут через точку ex. - equipmentList.id, то добавляется порядковый номер в списке equipmentList[0].id
 * Если указан атрибут без точки ex. - equipmentList, то порядковый номер определяется так - equipmentList[0]
 * Индексация доступна по тегу в параметре nodeType (по умолчанию <tr>) для всех параметров
 * @example:
 *   1)
 *      из таблицы со строками
 *
 *      <tr>
 *        <input data-list-form-attribute="equipmentList.id">
 *        <input data-list-form-attribute="equipmentList.name">
 *      </tr>
 *      <tr>
 *        <input data-list-form-attribute="equipmentList.id">
 *        <input data-list-form-attribute="equipmentList.name">
 *      </tr>
 *
 *      получаем
 *
 *      <tr>
 *        <input data-list-form-attribute="equipmentList[0].id">
 *        <input data-list-form-attribute="equipmentList[0].name">
 *      </tr>
 *      <tr>
 *        <input data-list-form-attribute="equipmentList[1].id">
 *        <input data-list-form-attribute="equipmentList[1].name">
 *      </tr>
 *
 *   2)
 *      из таблицы со строками
 *
 *      <tr>
 *        <input data-list-form-attribute="equipmentList.id">
 *        <input data-list-form-attribute="equipmentList.name">
 *      </tr>
 *      <tr>
 *        <input data-list-form-attribute="equipmentList.id">
 *        <input data-list-form-attribute="equipmentList.name">
 *      </tr>
 *      <tr>
 *        <input data-list-form-attribute="anyList.id">
 *        <input data-list-form-attribute="anyList.name">
 *      </tr>
 *      <tr>
 *        <input data-list-form-attribute="anyList.id">
 *        <input data-list-form-attribute="anyList.name">
 *      </tr>
 *      <tr>
 *        <input data-list-form-attribute="moreList">
 *      </tr>
 *      <tr>
 *        <input data-list-form-attribute="moreList">
 *      </tr>
 *      <tr>
 *        <input data-list-form-attribute="anyMoreList">
 *      </tr>
 *
 *      получаем
 *
 *      <tr>
 *        <input data-list-form-attribute="equipmentList[0].id">
 *        <input data-list-form-attribute="equipmentList[0].name">
 *      </tr>
 *      <tr>
 *        <input data-list-form-attribute="equipmentList[1].id">
 *        <input data-list-form-attribute="equipmentList[1].name">
 *      </tr>
 *      <tr>
 *        <input data-list-form-attribute="anyList[0].id">
 *        <input data-list-form-attribute="anyList[0].name">
 *      </tr>
 *      <tr>
 *        <input data-list-form-attribute="anyList[1].id">
 *        <input data-list-form-attribute="anyList[1].name">
 *      </tr>
 *      <tr>
 *        <input data-list-form-attribute="moreList[0]">
 *      </tr>
 *      <tr>
 *        <input data-list-form-attribute="moreList[1]">
 *      </tr>
 *      <tr>
 *        <input data-list-form-attribute="anyMoreList[0]">
 *      </tr>
 *
 * @param {JQuery} $elementSearch  объект поиска атрибутов
 * @param {string} nodeType - тип узла для поиска индексов
 */
function listFormAttr($elementSearch, nodeType = 'tr') {
    const formSelector = (attrValue = '') => {
        let postFix = attrValue === '' ? 'attribute' : `parameter="${attrValue}"`;
        return `select[data-list-form-${postFix}], input[data-list-form-${postFix}], textarea[data-list-form-${postFix}]`;
    };
    $elementSearch.each(function () {
        // Получаем список уникальных параметров и устанавливаем имя параметра формы
        let parameters = [];
        $(this).find(formSelector()).each(function () {
            let attr = $(this).data('listFormAttribute');
            if (attr != null) {
                attr = attr.indexOf('.') > 0 ? attr.substring(0, attr.indexOf('.')) : attr;
                if (parameters.indexOf(attr) === -1) {
                    parameters.push(attr);
                }
                $(this).attr('data-list-form-parameter', attr);
            }
        });
        // По каждому параметру формы ищем соответствующие строки
        $.each(parameters, (paramIndex, paramValue) => {
            $(this).find(formSelector(paramValue)).closest(nodeType).each(function (index) {
                $(this).find(formSelector()).each(function() {
                    let attr = $(this).data('listFormAttribute');
                    if (attr != null) {
                        let firstPart = attr.indexOf('.') > 0 ? attr.substring(0, attr.indexOf('.')) : attr;
                        let secondPart = attr.indexOf('.') > 0 ? attr.substring(attr.indexOf('.')) : '';
                        $(this).attr('id', `${firstPart}[${index}]${secondPart}`);
                        $(this).attr('name', `${firstPart}[${index}]${secondPart}`);
                    }
                    $(this).removeAttr('data-list-form-parameter');
                });
            });
        });
    });
}

/**
 * Функция для инициализации таблицы файлов
 * @param {JQuery} $fileContainer контейнер файловой таблицы
 * @param {string} selectorAdd селектор кнопки добавления файла
 * @param {string} selectorRemove селектор элемента удаления файла
 * @param {string} listFormAttribute значение аттрибута списка
 */
function initFileTable($fileContainer, selectorAdd = 'js-add-file', selectorRemove = 'js-remove-file', listFormAttribute = 'fileStorage') {
    const
        $fileTable = $fileContainer.find('table'),
        rowFileBlank =
        `<tr>
            <td class="b-table-edit__td">
                <i class="${selectorRemove} b-icon-remove"></i>
            </td>
            <td class="b-table-edit__td" colspan="2">
                <input type="file" data-list-form-attribute="${listFormAttribute}">
            </td>
        </tr>`;
    // Функция пересчета таблицы
    $fileTable.on({
        'table.recalculate' : () => {
            // Привязка листенеров на кнопки удаления строк
            $fileTable.find(`.${selectorRemove}`).off();
            $fileTable.find(`.${selectorRemove}`).on({
                'click' : function() {
                    $(this).closest('tr').remove();
                }
            });
        }
    });
    // Добавление файлов
    $fileContainer.find(`.${selectorAdd}`).on({
        'click': () => {
            $(rowFileBlank).appendTo($fileTable);
            $fileTable.trigger('table.recalculate');
        }
    });
    $fileTable.trigger('table.recalculate');
}

/**
 * Функция для инициализации таблицы сортировки
 * @param {JQuery} $table контейнер таблицы
 * @param {string} mode 'drag' (двигать элементы мышью) , 'click' (сдвиг по клику на элемент)
 * @param {function} onSortableStop колбек после завершения сортировки
 */
function initSortableTable($table, mode = 'drag', onSortableStop = () => {}) {
    let sortToggleBlank = '',
        emptyBlank = '<td></td>',
        disableSortable = '.js-disable-sortable';
    if (mode === 'click') {
        sortToggleBlank =
            `<td>
                <i class="js-sortable-move-up b-sortable-move-up fas fa-caret-up fa-1x" title="переместить выше"></i>
                <i class="js-sortable-move-down fas fa-caret-down fa-1x" title="переместить ниже"></i>
            </td>`;
    } else if (mode === 'drag') {
        sortToggleBlank =
            `<td style="text-align: center;vertical-align: middle;">
                <i class="b-sortable-drag fas fa-arrows-alt fa-1x" title="перетащить"></i>
            </td>`;
    }
    let __fnInitRow = function($rows) {
        if ($table.find('thead').length > 0) {
            $table.find('thead:first > tr > th:first').attr('colspan', '2');
        }
        $rows.each(function() {
            let _this = $(this);
            if (!_this.is(`${disableSortable}`)) {
                let $sortToggle = $(sortToggleBlank).prependTo(_this);
                if (mode === 'click') {
                    $sortToggle.find('.js-sortable-move-up, .js-sortable-move-down').on({
                        'click': function () {
                            let $row = _this.closest('tr');
                            _this.is('.js-sortable-move-up') ? $row.insertBefore($row.prev()) : $row.insertAfter($row.next());
                            onSortableStop();
                        }
                    });
                }
                $(this).addClass('js-sortable-row');
            } else {
                $(emptyBlank).prependTo(_this);
            }
        });
        if (mode === 'drag') {
            $table.find('tbody:first').sortable({
                cursor: 'move',
                items: `tr:not('${disableSortable}')`,
                stop: (event, ui) => {
                    $table.find('.nfile').each(function(index) {
                        $(this).text(index + 1);
                    });
                    $table.find('textarea.std-ckeditor').each(function() {
                        CKEDITOR.instances[$(this).attr('name')].destroy();
                        $(this).ckeditor();
                    });
                    onSortableStop();
                }
            });
        }
    };
    __fnInitRow($table.find('tbody:first > tr'));
    $table.on('DOMNodeInserted.sortable', function (event) {
        let $elem = $(event.target);
        if ($elem.prop("tagName") === 'TR' && !$elem.hasClass('js-sortable-row')) {
            __fnInitRow($elem);
        }
    });
}

/**
 * Функция преобразования булева значения в текст
 * @param {boolean, string, int} value допустимые значения true, false, 'true', 'false', 1, 0, '1', '0'
 * @param {Object} textData
 * @param {string} textData.onTrue текст вывода при true значении
 * @param {string} textData.onFalse текст вывода при false значении
 * @return {string} текст
 */
function booleanToText(value, textData = { onTrue: '', onFalse: '' }) {
    if (value === true || value === '1' || value === 'true' || value === 1) {
        return textData.onTrue;
    } else if (value === false || value === '0' || value === 'false' || value === 0) {
        return textData.onFalse;
    }
    return '';
}

/**
 * Функция генерации глобального сообщения
 * @param {Object} options
 * @param options.type тип сообщения - error/success/warning, по умолчанию - error
 * @param options.size размер сообщения - mini/tiny/small/large/big/huge/massive, по умолчанию - large
 * @param options.timeout задержка окна сообщения в мс, по умолчанию 5 сек
 * @param options.title заголовок сообщения
 * @param options.message тело сообщения
 */
function globalMessage({type = 'error', size = 'large', timeout = 5000, title, message}) {
    if (message == null || message.length === 0) return;
    $('body').toast({
        title: title,
        message: message,
        displayTime: timeout,
        class: type,
        className: { toast: `ui message ${size}` }
    });
}

/**
 * Функция преобразования булева значения в цвет светофора
 * @param {boolean, string, int} value допустимые значения true, false, 'true', 'false', 1, 0, '1', '0'
 * @param {Object} titleData
 * @param {string} titleData.onTrue текст title при true значении
 * @param {string} titleData.onFalse текст title при false значении
 * @return {string} html строка иконки светофора
 */
function booleanToLight(value, titleData = { onTrue: '', onFalse: '' }) {
    if (value === true || value === '1' || value === 'true' || value === 1) {
        return `<i title="${titleData.onTrue}" class="icon large check circle green"></i>`;
    } else if (value === false || value === '0' || value === 'false' || value === 0) {
        return `<i title="${titleData.onFalse}" class="icon large times circle red"></i>`;
    }
    return '';
}

/**
 * Функция преобразования формы в JSON строку
 * @param {jQuery, string} form селектор формы или jQuery объект формы
 * @return {string} JSON строка
 */
function formToJson(form) {
    return JSON.stringify(formToObject(form));
}

/**
 * Функция сериализации формы в объект
 * @param {jQuery, string} form селектор формы или jQuery объект формы
 * @return {Object} объект с данными формы
 */
function formToObject(form) {
    const object = {};
    let $form;
    if (typeof form === 'string') {
        $form = $(form);
    } else if (form instanceof jQuery) {
        $form = form;
    }
    if ($form == null || $form.prop('tagName') !== 'FORM') {
        return object;
    }
    new FormData($form.get(0)).forEach((value, key) => {
        if (key.startsWith('_')) {
            return;
        }
        if (!object.hasOwnProperty(key)) {
            object[key] = value;
            return;
        }
        if (!Array.isArray(object[key])) {
            object[key] = [object[key]];
        }
        object[key].push(value);
    });
    $form.find(':disabled').each((key, element) => {
        const $element = $(element);
        object[$element.attr('name')] = $element.is(':checked');
    });
    return object;
}

/**
 * Функция восстановления данных формы
 * @param {jQuery, string} form селектор формы или jQuery объект формы
 * @param {Object, string} restObj объект восстановления - json строка или объект
 */
function formRestore(form, restObj) {
    let $form;
    if (typeof form === 'string') {
        $form = $(form);
    } else if (form instanceof jQuery) {
        $form = form;
    }
    let dataObject;
    if (typeof restObj === 'string') {
        try { dataObject = JSON.parse(restObj) } catch {}
    } else if (typeof restObj === 'object') {
        dataObject = restObj;
    }
    if ($form == null || $form.prop('tagName') !== 'FORM' || dataObject == null) {
        return;
    }
    for (const [key, value] of Object.entries(dataObject)) {
        const $entry = $form.find(`[name="${key}"]`);
        if (!value || value === 'null' || $entry.length === 0) {
            continue;
        }
        const tagName = $entry.prop('tagName');
        const type = $entry.prop('type');
        switch (tagName.toUpperCase()) {
            case 'INPUT': {
                switch (type.toUpperCase()) {
                    case 'SEARCH':
                    case 'TEXT':
                        $entry.val(value);
                        $entry.trigger('change');
                        break;
                    case 'CHECKBOX': $entry.prop('checked', value); break;
                }
                break;
            }
            case 'SELECT': {
                const _fnSetState = value => $entry.find(`option[value="${value}"]`).prop('selected', true);
                value instanceof Array ? value.forEach(el => _fnSetState(el)) : _fnSetState(value);
                $entry.trigger('change');
                break;
            }
            case 'TEXTAREA': $entry.val(value); break;
        }
    }
}

/**
 * Функция очистки данных формы
 * @param {jQuery, string} form селектор формы или jQuery объект формы
 */
function formClear(form) {
    let $form;
    if (typeof form === 'string') {
        $form = $(form);
    } else if (form instanceof jQuery) {
        $form = form;
    }
    $form.find('input[type="text"], input[type="search"], input[type="checkbox"], select').each((inx, elem) => {
        const $elem = $(elem);
        if ($elem.is('input:checkbox')) $elem.prop('checked', false);
        else if ($elem.is('input')) {
            $elem.val('');
            $elem.trigger('change');
        } else if ($elem.is('select:not(.std-tree-select)')) {
            $elem.dropdown('clear');
            if (!elem.hasAttribute('multiple')) {
                $elem.dropdown('set selected', $elem.find('option:eq(0)').val());
            }
        }
    });
}

/**
 * Функция создания простого диалога с подтверждением/отклонением действия
 * @param {String} title заголовок
 * @param {String} message сообщение
 * @param {String} dialogSize размер диалогового окна. Доступные значения mini/tiny/small/large/big/huge/massive
 * @param {String} buttonTextAccept текст кнопки подтверждения
 * @param {String} buttonTextReject текст кнопки отмены
 * @param {function} onAccept колбек при нажатии на кнопку подтверждения
 * @param {function} onReject колбек при нажатии на кнопку отмены
 */
function confirmDialog({
   title = '',
   message = '',
   dialogSize = 'mini',
   buttonTextAccept = 'Да',
   buttonTextReject = 'Отмена',
   onAccept = () => {},
   onReject = () => {},
}) {
    const $dialog = $(
        `<div class="ui ${dialogSize} modal modal-confirm">
            <div class="header">${title}</div>
            <div class="content">
                <p>${message}</p>
            </div>
            <div class="actions">
                <div class="ui basic button">
                    ${buttonTextReject}
                </div>
                <div class="ui basic button">
                    ${buttonTextAccept}
                </div>
            </div>
        </div>`
    ).prependTo($('body'));
    let action = () => {};
    $dialog.modal({
        allowMultiple: true,
        centered: false,
        onHidden: () => {
            $dialog.remove();
            action();
        }
    });
    $dialog.find('div.actions > div.button:eq(0)').on({
        'click': () => {
            action = onReject;
            $dialog.modal('hide');
        }
    });
    $dialog.find('div.actions > div.button:eq(1)').on({
        'click': () => {
            action = onAccept;
            $dialog.modal('hide');
        }
    });
    $dialog.modal('show');
}

/**
 * Функция создания простого пользовательского алерта
 * @param {String} title заголовок
 * @param {String} message сообщение
 */
function alertDialog({title = 'Ошибка', message = ''}) {
    const $dialog = $(
        `<div class="ui mini modal">
            <div class="header">${title}</div>
            <div class="content">
                <p>${message}</p>
            </div>
            <div class="actions">
                <div class="ui basic button">OK</div>
            </div>
        </div>`
    ).prependTo($('body'));
    $dialog.modal({
        allowMultiple: true,
        centered: false,
        onHidden: () => $dialog.remove()
    });
    $dialog.find('p').css({
        'white-space': 'pre-wrap'
    });
    $dialog.find('div.button').on({
        'click': () => $dialog.modal('hide')
    });
    $dialog.modal('show');
}

/**
 * jQuery-функция для обработки нажатия клавиши enter
 */
;(($) => {
    $.fn.enter = function(func) {
        return this.each(function() {
            $(this).on({
                'keyup': function(e) {
                    if (e.key === 'Enter') {
                        e.preventDefault();
                        func.call(this, e);
                    }
                }
            });
        });
    }
})(jQuery);

/**
 * jQuery-функция для стилизации поля выбора файла
 *
 * <div class="std-file ui action input">
 *      <form:hidden path="fileStorage.id"/>
 *      <input type="file" name="file"/>
 *      <span>
 *          ${form.fileStorage.name}
 *          <a target="_blank" href="<c:url value="/download-file/${form.fileStorage.storeHash}"/>"><fmt:message key="text.downloadFile"/></a>
 *      </span>
 * </div>
 */
;(($) => {
    $.fn.fileInput = function() {
        this.each(function() {
            const $fileDiv = $(this);
            const $hiddenInput = $fileDiv.children('input[type=hidden]');
            const $fileInput = $fileDiv.children('input[type=file]');
            const $linkBlock = $fileDiv.children('span');
            const $removeLink = $('<i class="times link red icon"></i>').appendTo($linkBlock);
            const $fileName = $('<input type="text" placeholder="Выберите файл" readonly>').appendTo($fileDiv);
            const $fileAttach = $('<div class="ui small button icon" title="Прикрепить"><i class="attach icon"></i></div>').appendTo($fileDiv);
            const $fileRemove = $('<div class="ui small button icon" title="Отмена"><i class="red times icon"></i></div>').appendTo($fileDiv);
            $fileAttach.on({
                'click': () => $fileInput.trigger('click')
            });
            $fileInput.on({
                'change': e => {
                    if (e.target.files.length) {
                        $fileName.val(e.target.files[0].name);
                        $fileRemove.show();
                    } else {
                        $fileName.val('');
                        $fileInput.val('');
                        $fileRemove.hide();
                    }
                }
            });
            $removeLink.on({
                'click': () => {
                    $hiddenInput.val('');
                    $linkBlock.remove();
                    $fileName.show();
                    $fileAttach.show();
                }
            });
            $fileRemove.on({
                'click': () => {
                    $fileInput.val('');
                    $fileInput.trigger('change');
                }
            });
            $fileInput.hide();
            if ($hiddenInput.val()) {
                $fileName.hide();
                $fileAttach.hide();
                $fileRemove.hide();
            } else {
                $linkBlock.hide();
                $fileRemove.hide();
            }
        });
    }
})(jQuery);

/**
 * Функция безопасного разбора JSON-строки. В случае неудачи возвращает пустой объект
 * @param {String} json JSON-строка
 * @return {Object} объект разбора
 */
function safetyParseJSON(json) {
    if (json == null) return {};
    try {
        return JSON.parse(json);
    } catch {
        return {};
    }
}

/**
 * Функция получения из url-зароса табличных данных
 * @param {string} query url-запрос
 * @param {string} [paramPref = ''] префикс параметра в url-запросе, для идентификации принадлежности к конкретной таблице
 * @return {Object} Объект табличных данных.
 * Если какие-либо параметры не удалось разобрать, то вернет параметры по умолчанию, либо пустые параметры
 */
function tableDataFromUrlQuery(query, paramPref = '') {
    const data = {};
    const converter = paramName => paramPref ? `${paramPref}${paramName[0].toUpperCase()}${paramName.slice(1)}` : paramName;
    // Сырые значения параметров
    const usp = new URLSearchParams(query);
    const pPage = usp.get(converter(TABR_URL_ATTR_PAGE));
    const pSize = usp.get(converter(TABR_URL_ATTR_SIZE));
    const pSorterField = usp.get(converter(TABR_URL_ATTR_SORT_FIELD));
    const pSorterDir = usp.get(converter(TABR_URL_ATTR_SORT_DIR));
    // Форматирование параметров/значения по умолчанию
    const page = parseInt(pPage);
    data.page = isNaN(page) || page <= 0 ? 1 : page;
    const size = parseInt(pSize)
    data.size = isNaN(size) || size <= 0 || size % TABR_PAGE_SIZE > 0 ? TABR_PAGE_SIZE : size;
    data.sort = pSorterField && pSorterDir ? [{ column: pSorterField, dir: pSorterDir }] : [];
    data.filterData = safetyParseJSON(usp.get(converter(TABR_URL_ATTR_FILTER_DATA)));
    return data;
}

/**
 * Функция получения url-запроса из параметров таблицы. Выполняется дополнение параметров строки
 * @param {string} query url-запрос
 * @param {Object} params параметры таблицы
 * @param {string, number} params.page страница
 * @param {string, number} params.size строк на странице
 * @param {Array} params.sorters массив сортировки
 * @param {string} params.filterData данные фильтра таблицы (JSON строка)
 * @param {string} [paramPref = ''] префикс параметра, для идентификации принадлежности к конкретной таблице
 * @return {string} url-запрос
 */
function urlQueryFromTableParams(query, params, paramPref = '') {
    const converter = paramName => paramPref ? `${paramPref}${paramName[0].toUpperCase()}${paramName.slice(1)}` : paramName;
    // Форматирование параметров
    const pPage = parseInt(params.page);
    params.page = isNaN(pPage) || pPage <= 0 ? 1 : pPage;
    const pSize = parseInt(params.size);
    params.size = isNaN(pSize) || pSize <= 0 || pSize % TABR_PAGE_SIZE > 0 ? TABR_PAGE_SIZE : pSize;
    params.sorters = params.sorters || [];
    params.filterData = params.filterData || '';
    //
    const usp = new URLSearchParams(query);
    usp.set(converter(TABR_URL_ATTR_PAGE), params.page.toString());
    usp.set(converter(TABR_URL_ATTR_SIZE), params.size.toString());
    //
    const sortFieldAttr = converter(TABR_URL_ATTR_SORT_FIELD);
    const sortDirAttr = converter(TABR_URL_ATTR_SORT_DIR);
    if (params.sorters.length) {
        const field = params.sorters[0].field;
        const dir = params.sorters[0].dir;
        usp.set(sortFieldAttr, field || '');
        usp.set(sortDirAttr, dir || '');
    } else {
        usp.delete(sortFieldAttr);
        usp.delete(sortDirAttr);
    }
    const filterDataAttr = converter(TABR_URL_ATTR_FILTER_DATA);
    params.filterData ? usp.set(filterDataAttr, params.filterData) : usp.delete(filterDataAttr);
    return usp.toString();
}

/**
 * Функция фильтрации url-запроса по допустимым параметрам
 * @param {string, URLSearchParams} query url-запрос
 * @param {string[]} params массив допустимых параметров
 * @return {string} фильтрованый url-запрос
 */
function filterUrlQuery(query, params) {
    const usp = query instanceof URLSearchParams ? query : new URLSearchParams(query);
    let removeKeys = [];
    usp.forEach((v, k) => params.includes(k) || removeKeys.push(k));
    removeKeys.forEach(k => usp.delete(k));
    return query instanceof URLSearchParams ? usp : usp.toString();
}

/**
 * Метод инициализации представления - метаданные и маршруты
 * @param {Object} options
 * @param {function} options.route функция маршрутизации
 * @param {Object} [options.pageOptions] настройки инициализации page.js
 */
function initPageView({
    route,
    pageOptions = { click: false, decodeURLComponents: false }
}) {
    ROUTE.base = `${PATH_SYSTEM_SCHEME}${PATH_SECTION_SCHEME}`;
    ROUTE.empty = '/';
    ROUTE.unknown = '*';
    Object.keys(VIEW_PATH).forEach(key => VIEW_PATH[key] = `${PATH_API_VIEW}${ROUTE.base}${VIEW_PATH[key]}`);
    Object.keys(ACTION_PATH).forEach(key => ACTION_PATH[key] = `${PATH_API_ACTION}${ROUTE.base}${ACTION_PATH[key]}`);
    if (L_STORAGE) Object.keys(L_STORAGE).forEach(key => L_STORAGE[key] = `${SYSTEM_SCHEME}.${SECTION_SCHEME}.${L_STORAGE[key]}`);
    if (S_STORAGE) Object.keys(S_STORAGE).forEach(key => S_STORAGE[key] = `${SYSTEM_SCHEME}.${SECTION_SCHEME}.${S_STORAGE[key]}`);
    page.base(ROUTE.base);
    route();
    page(ROUTE.unknown, () => document.location.href = '/not-found');
    page(pageOptions);
}

/**
 * Метод правильного скролла до первой выбранной строки в табуляторной таблице
 * @param {Object} table объект табулятора
 * @param {String} scrollType тип скролла ('top', 'middle', 'bottom')
 */
function tabrScrollToRow(table, scrollType = 'middle') {
    const data = table.getSelectedData();
    if (data.length && data[0].id) {
        table.redraw();
        table.scrollToRow(data[0].id, scrollType, false);
    }
}

/**
 * Метод форматирования числа в валютное представление
 * @param {Number} str число для форматирования
 * @return {String} строка в валютном представлении
 */
function formatAsCurrency(str) {
    return new Intl.NumberFormat('ru', { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(str);
}

/**
 * Метод установки таймера обновления таблицы <br>
 * Установка происходит единожды на одну таблицу <br>
 * Также доступен внеплановый вызов обновления таблицы по событию 'forceTimerUpdate' у JQuery объекта таблицы
 * @param {Object} options
 * @param {String} options.selector селектор табуляторной таблицы
 * @param {String} options.url url-адрес обновления
 * @param {Object} [options.params] параметры запроса
 * @param {function} [options.filterData] функция получения данных фильтра
 * @param {number} [options.interval] интервал обновления
 */
function tableTimerUpdate({
    selector,
    url,
    params = {},
    filterData = () => {},
    interval = 5000
}) {
    const $table = $(selector);
    const initClass = 'timer-update-initialized';
    if ($table.hasClass(initClass)) return;
    else $table.addClass(initClass);

    let requestCount = 0; // Счетчик запросов
    let timer = setTimeout(update, interval); // Таймер обновления таблицы
    $table.on({ // Событие внепланового мгновенного обновления таблицы
        'forceTimerUpdate': () => {
            clearTimeout(timer);
            timer = setTimeout(update, 1);
        }
    });
    function update() {
        const requestParams = $.extend(true, {}, params);
        const requestFilterData = filterData();

        // Не нашли таблицу - остановим таймер
        const table = Tabulator.prototype.findTable(selector)[0];
        if (!document.contains($table.get(0))) return;

        // Формируем параметры запроса
        const usp = new URLSearchParams();
        usp.set(TABR_REQ_ATTR_PAGE, table.getPage());
        usp.set(TABR_REQ_ATTR_SIZE, table.getPageSize());
        const sorters = table.getSorters();
        if (sorters.length) {
            usp.set(TABR_REQ_ATTR_FIELD, sorters[0].field);
            usp.set(TABR_REQ_ATTR_DIR, sorters[0].dir);
        }
        usp.set(TABR_REQ_ATTR_FILTER_DATA, requestFilterData ? JSON.stringify(requestFilterData) : "");
        Object.keys(requestParams).forEach(key => usp.set(key, requestParams[key]));

        const requestNumber = ++requestCount;
        $.get({
            url: `${url}?${usp.toString()}`,
            global: false,
            timeout: interval - 100,
            complete: () => timer = setTimeout(update, interval)
        }).done(resp => {
            const data = Array.isArray(resp) ? resp : resp.data; // постраничная загрузка или обычная
            const selectedData = table.getSelectedData();
            const tableData = table.getData();
            const holder = $table.find(TABR_SELECTOR_TABLE_HOLDER).get(0);
            const scrollTop = holder ? holder.scrollTop : 0;
            const selectIdArr = selectedData ? selectedData.map(el => el.id) : [];
            if (
                document.contains($table.get(0)) && // таблица должна существовать
                requestNumber === requestCount && // избавляемся от одновременно нескольких запросов (берем последний)
                JSON.stringify(requestParams) === JSON.stringify(params) && // параметры таблицы не должны отличаться
                JSON.stringify(requestFilterData) === JSON.stringify(filterData()) && // фильтр таблицы не должен меняться
                JSON.stringify(tableData) !== JSON.stringify(data) // пришедшие данные должны отличаться от исходных данных
            ) {
                // Получаем строки, которые были открыты, если используется переключатель открытия расскрытия строк
                const expandRowIdList = [];
                $table.find(`.${TABR_CLASS_ROW_TOGGLE_EXPAND}`).each((idx, elem) => {
                    const row = table.getRow(elem);
                    if (row) {
                        const id = row.getData().id;
                        if (id) expandRowIdList.push(id);
                    }
                });
                table.replaceData(data).then(() => {
                    selectIdArr.length ? table.selectRow(selectIdArr) : null;
                    expandRowIdList.forEach(id => {
                        const row = table.getRow(id);
                        if (row) {
                            const $row = $(row.getElement());
                            $row.find(`.${TABR_CLASS_ROW_TOGGLE_BUTTON}`).trigger('click');
                        }
                    });
                    holder ? holder.scrollTop = scrollTop : null;
                });
            }
        });
    }
}