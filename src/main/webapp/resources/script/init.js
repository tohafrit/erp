///////////////////// Глобальные установки jQuery
$.ajaxPrefilter(options => {
    options.async = true; // явная установка устраняет deprecation warning о XMLHttpRequest
});
$(document).ajaxError((event, resp) => {
    // Status
    const status = resp.status;
    if (status === HTTP_STATUS.UNAUTHORIZED) document.location.href = '/index'
    // JSON
    const json = resp.responseJSON;
    if (json) {
        const title = json.title;
        const message = json.message;
        const trace = json.trace;
        if (trace.length) trace.forEach(line => console.log(line));
        if (message) alertDialog({ title: title ? title : 'Ошибка', message: message });
    }
});

///////////////////// Стандартные настройки плагинов

// Chart
Chart.defaults.global.defaultFontColor = '#363636';

// Events
$(document).on({
    'keydown': e => {
        if (e.key === 'ArrowUp' || e.key === 'ArrowDown') e.preventDefault();
        if (e.key === 'Escape') {
            // При быстром нажатии esc могут остаться неинициализированные скрытые модалки
            $('div.ui.dimmer.modals > div.ui.modal').filter('.hidden').remove();
            $('div.ui.dimmer.modals > div.ui.modal:last').modal('hide');
        }
    }
});

// Tabulator
Tabulator.prototype.extendModule('localize', 'langs', {
    'ru-ru': {
        'ajax': {
            'loading': 'Загрузка данных',
            'error': 'Ошибка загрузки данных',
        },
        'pagination': {
            'page_title': 'Страница',
            'page_size': 'Строк на странице',
            'first': 'Первая',
            'first_title': 'Перейти на первую страницу',
            'last': 'Последняя',
            'last_title': 'Перейти на последнюю страницу',
            'prev': 'Предыдущая',
            'prev_title': 'Перейти на предыдущую страницу',
            'next': 'Следующая',
            'next_title': 'Перейти на следующую страницу'
        }
    }
});
Tabulator.prototype.extendModule('format', 'formatters', {
    'remoteRowNum': cell => {
        const row = cell.getRow();
        const table = row.getTable();
        $(cell.getElement()).addClass('tabulator-row-number-cell');
        return (table.getPageSize() * (table.getPage() - 1)) + row.getPosition() + 1;
    },
    'stdDate': cell => dateStdToString(cell.getValue()),
    'stdDatetime': cell => dateTimeStdToString(cell.getValue()),
    'stdTime': cell => timeStdToString(cell.getValue()),
    'stdMoney': cell => formatAsCurrency(cell.getValue()),
    'lightMark': (cell, params) => {
        const value = params.value == null ? cell.getValue() : params.value;
        const trueText = params.trueText == null ? '' : params.trueText;
        const falseText = params.falseText == null ? '' : params.falseText;
        return booleanToLight(
            typeof value === 'function' ? value(cell) : value, {
            onTrue: typeof trueText === 'function' ? trueText(cell) : trueText,
            onFalse: typeof falseText === 'function' ? falseText(cell) : falseText
        });
    },
    'fileLink': (cell, params) => {
        const value = cell.getValue();
        const href = typeof params.href === 'string' ? params.href : '/download-file/';
        if (value) return `<a target="_blank" href="${href}${value}">Скачать файл</a>`;
    },
    'rowToggle': cell => {
        const $row = $(cell.getRow().getElement());
        $row.addClass(TABR_CLASS_ROW_TOGGLE, TABR_CLASS_ROW_TOGGLE_COLLAPSE);
        const $btn = $('<i class="plus circle blue icon small large"></i>');
        $btn.addClass(TABR_CLASS_ROW_TOGGLE_BUTTON);
        $btn.css({ 'font-size': '16px' });
        $btn.on({
            'click': e => {
                e.stopPropagation();
                $row.toggleClass(`${TABR_CLASS_ROW_TOGGLE_COLLAPSE} ${TABR_CLASS_ROW_TOGGLE_EXPAND}`);
                $btn.toggleClass('plus minus');
                $row.find(`.${TABR_CLASS_ROW_TOGGLE_HOLDER}`).toggle($row.hasClass(TABR_CLASS_ROW_TOGGLE_EXPAND));
            }
        });
        return $btn.get(0);
    }
});
Tabulator.prototype.extendModule('keybindings', 'actions', {
    'selectPrevRow': e => {
        const table = Tabulator.prototype.findTable($(e.target).closest('div.tabulator').get(0))[0];
        let rows = table.getSelectedRows();
        rows = rows.length ? rows : table.getRows();
        if (rows.length) {
            const row = rows[0];
            const prevRow = row.getPrevRow();
            if (row.isSelected() && prevRow) {
                table.deselectRow();
                prevRow.select();
                tabrScrollToRow(table, 'top');
            } else if (!row.isSelected()) {
                row.select();
                tabrScrollToRow(table, 'top');
            }
        }
    },
    'selectNextRow': e => {
        const table = Tabulator.prototype.findTable($(e.target).closest('div.tabulator').get(0))[0];
        let rows = table.getSelectedRows();
        rows = rows.length ? rows : table.getRows();
        if (rows.length) {
            const row = rows[0];
            const nextRow = row.getNextRow();
            if (row.isSelected() && nextRow) {
                table.deselectRow();
                nextRow.select();
                tabrScrollToRow(table, 'bottom');
            } else if (!row.isSelected()) {
                row.select();
                tabrScrollToRow(table, 'top');
            }
        }
    },
    'selectedRowClick': e => {
        const table = Tabulator.prototype.findTable($(e.target).closest('div.tabulator').get(0))[0];
        const rows = table.getSelectedRows();
        if (rows.length === 1) $(rows[0].getElement()).trigger('click');
    },
    'pagePrev': e => {
        const table = Tabulator.prototype.findTable($(e.target).closest('div.tabulator').get(0))[0];
        table.previousPage();
    },
    'pageNext': e => {
        const table = Tabulator.prototype.findTable($(e.target).closest('div.tabulator').get(0))[0];
        table.nextPage();
    }
});
Tabulator.prototype.extendModule('keybindings', 'bindings', {
    'navUp': false,
    'navDown': false,
    'selectPrevRow': 38, // ArrowUp
    'selectNextRow': 40, // ArrowDown
    'selectedRowClick': 13, // Enter
    'pagePrev': 37, // ArrowLeft
    'pageNext': 39 // ArrowRight
});
Tabulator.prototype.defaultOptions.locale = 'ru-ru';
Tabulator.prototype.defaultOptions.ajaxLoader = false;
Tabulator.prototype.defaultOptions.ajaxLoaderLoading =
    `<div class="ui active inverted dimmer">
        <div class="ui small text loader elastic blue"></div>
    </div>`;
Tabulator.prototype.defaultOptions.ajaxLoaderError =
    `<div class="ui active inverted dimmer">
        <div class="ui small error message">
            Ошибка получения данных
        </div>
    </div>`;
Tabulator.prototype.defaultOptions.placeholder = 'Данные отсутствуют';
Tabulator.prototype.defaultOptions.virtualDomBuffer = 5000;
Tabulator.prototype.defaultOptions.paginationSize = TABR_PAGE_SIZE;
Tabulator.prototype.defaultOptions.paginationSizeSelector = [TABR_PAGE_SIZE, TABR_PAGE_SIZE * 2, TABR_PAGE_SIZE * 3, TABR_PAGE_SIZE * 4];

// Semantic-ui calendar
$.fn.calendar.settings.firstDayOfWeek = 1;
$.fn.calendar.settings.formatInput = false;
$.fn.calendar.settings.monthFirst = false;
$.fn.calendar.settings.today = true;
$.fn.calendar.settings.onHide = input => $(input).trigger('blur');
$.fn.calendar.settings.text = {
    days: ['Вс', 'Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб'],
    months: ['Январь', 'Февраль', 'Март', 'Апрель', 'Май', 'Июнь', 'Июль', 'Август', 'Сентябрь', 'Октябрь', 'Ноябрь', 'Декабрь'],
    monthsShort: ['Янв', 'Фев', 'Мар', 'Апр', 'Май', 'Июн', 'Июл', 'Авг', 'Сен', 'Окт', 'Ноя', 'Дек'],
    today: 'Сегодня',
    now: 'Сейчас',
    am: 'AM',
    pm: 'PM'
};

// Semantic-ui dropdown
$.fn.dropdown.settings.forceSelection = false;
$.fn.dropdown.settings.fullTextSearch = true;
$.fn.dropdown.settings.message = {
    noResults: 'Ничего не найдено'
};

// Semantic-ui search
$.fn.search.settings.error = {
    noResultsHeader: 'Нет совпадений',
    noResults: 'Ничего не найдено'
};

// Semantic-ui modal
$.fn.modal.settings.duration = 0;
$.fn.modal.settings.closable = false;
$.fn.modal.settings.autofocus = false;
$.fn.modal.settings.dimmerSettings = {
    closable: false,
    template : {
        dimmer: () => $('<div>').attr('class', 'ui dimmer very light')
    }
};

// Inputmask
Inputmask.extendAliases({
    'inputMoney': {
        alias: 'currency',
        groupSeparator: ' ',
        radixPoint: '.',
        rightAlign: false,
        allowMinus: false,
        max: 99999999.99
    },
    'laboriousnessValue': {
        alias: 'numeric',
        digits: 2,
        radixPoint: '.',
        rightAlign: false,
        allowMinus: false,
        digitsOptional: false
    }
});