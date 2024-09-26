/*
* Селекторы-классы для стандартной автоматической инициализации элементов интерфейса
*      - std-date - поле ввода даты
*      - std-time - поле ввода времени
*      - std-datetime - поле ввода даты/времени
*      - std-year - поле ввода года
*      - std-month - поле ввода месяца
*      - std-file - поле выбора файла
*      - std-select - выпадающий список
*      - std-tree-select - иерархический выпадающий список
*      - std-tree-menu - древовидное меню
*      - std-ckeditor - поле редактирования ckeditor
*      - std-checkbox - чекбокс
*      - std-div-input-search - поле поиска
*/
{
    const initClass = 'std-ui-initialized'; // Класс для элемента, инициализация которого была завершена
    const initSelector = `.${initClass}`; // Селектор класса для элемента, инициализация которого была завершена
    const selectors = { // Селекторы стандартных классов инициализаторов
        date: `input.std-date:not(${initSelector})`,
        time: `input.std-time:not(${initSelector})`,
        datetime: `input.std-datetime:not(${initSelector})`,
        year: `input.std-year:not(${initSelector})`,
        month: `input.std-month:not(${initSelector})`,
        file: `div.std-file:not(${initSelector})`,
        select: `select.std-select:not(${initSelector})`,
        treeSelect: `select.std-tree-select:not(${initSelector})`,
        treeMenu: `ul.std-tree-menu:not(${initSelector})`,
        ckeditor: `textarea.std-ckeditor:not(${initSelector})`,
        checkbox: `input.std-checkbox:not(${initSelector})`,
        divInputSearch: `div.std-div-input-search:not(${initSelector})`
    };

    const $date = $(selectors.date);
    const $time = $(selectors.time);
    const $datetime = $(selectors.datetime);
    const $year = $(selectors.year);
    const $month = $(selectors.month);
    const $file = $(selectors.file);
    const $select = $(selectors.select);
    const $treeSelect = $(selectors.treeSelect);
    const $treeMenu = $(selectors.treeMenu);
    const $ckeditor = $(selectors.ckeditor);
    const $checkbox = $(selectors.checkbox);
    const $divInputSearch = $(selectors.divInputSearch);

    $date.addClass(initClass);
    $time.addClass(initClass);
    $datetime.addClass(initClass);
    $year.addClass(initClass);
    $month.addClass(initClass);
    $file.addClass(initClass);
    $select.addClass(initClass);
    $treeSelect.addClass(initClass);
    $treeMenu.addClass(initClass);
    $ckeditor.addClass(initClass);
    $checkbox.addClass(initClass);
    $divInputSearch.addClass(initClass);

    const dateMin = new Date(1971, 0, 0, 0, 0, 0);
    const dateMax = new Date(2101, 0, 0, 0, 0, 0);

    // Дата
    $date.attr('autocomplete', 'off');
    $date.closest('div.ui.calendar').calendar({
        type: 'date',
        formatter: {
            date: date => dateStdToString(date)
        },
        minDate: dateMin,
        maxDate: dateMax
    });
    $date.inputmask({
        alias: 'datetime',
        inputFormat: 'dd.mm.yyyy',
        placeholder: '_',
        clearIncomplete: true
    });

    // Время
    $time.attr('autocomplete', 'off');
    $time.closest('div.ui.calendar').calendar({
        type: 'time',
        ampm: false
    });
    $time.inputmask({
        alias: 'datetime',
        inputFormat: 'HH:MM',
        placeholder: '_',
        clearIncomplete: true
    });

    // Дата и время
    $datetime.attr('autocomplete', 'off');
    $datetime.closest('div.ui.calendar').calendar({
        ampm: false,
        formatter: {
            datetime: date => dateTimeStdToString(date)
        },
        minDate: dateMin,
        maxDate: dateMax
    });
    $datetime.inputmask({
        alias: 'datetime',
        inputFormat: 'dd.mm.yyyy HH:MM:ss',
        placeholder: '_',
        clearIncomplete: true
    });

    // Год
    $year.attr('autocomplete', 'off');
    $year.closest('div.ui.calendar').calendar({
        type: 'year',
        minDate: dateMin,
        maxDate: dateMax
    });
    $year.inputmask({
        alias: 'datetime',
        inputFormat: 'yyyy',
        placeholder: '_',
        min: '1970',
        max: '2100',
        clearIncomplete: true
    });

    // Месяц
    $month.attr('autocomplete', 'off');
    $month.closest('div.ui.calendar').calendar({
        type: 'month',
        formatter: {
            datetime: date => dateToString(date, 'MM.yyyy')
        },
        minDate: dateMin,
        maxDate: dateMax
    });
    $month.inputmask({
        alias: 'datetime',
        inputFormat: 'mm.yyyy',
        placeholder: '_',
        clearIncomplete: true
    });

    // Поле выбора файла
    $file.fileInput();

    // Выпадающий список
    $select.each((inx, elem) => {
        const $elem = $(elem);
        $elem.dropdown({
            clearable: $elem.find('option[value=""]').length || $elem.prop('multiple')
        });
    });

    // Выпадающий древовидный список
    $treeSelect.treeChosen();

    // Древовидное меню
    $treeMenu.treeListView();

    // ckeditor
    $ckeditor.ckeditor();

    // Чекбокс
    $checkbox.checkbox();

    // Поле поиска
    $divInputSearch.each((inx, elem) => {
        const $elem = $(elem);
        const $clearIcon = $('<i class="times link icon gray"></i>');
        const $input = $elem.find('input');
        $elem.append($clearIcon);
        $elem.addClass('icon');
        $clearIcon.css({
            'right': '4px',
            'left': 'auto'
        });
        const event = () => $clearIcon.toggle($input.val().length > 0);
        $input.on({
            'input': event,
            'change': event,
            'focusout': event,
            'clearIconStateRefresh': event
        });
        event();
        $clearIcon.on({
            'click': () => {
                $input.val('');
                $input.trigger('change');
            }
        });
    });
}

// Использование для input аттрибутов вида data-inputmask-*="" или data-inputmask=""
{
    const initClass = 'std-inputmask-initialized';
    $(`input:not(.${initClass}`).each((inx, elem) => {
        const $elem = $(elem);
        $.each(elem.attributes, (inx, attr) => {
            if (attr.name.startsWith('data-inputmask')) {
                $elem.inputmask();
                $elem.addClass(initClass);
                return false;
            }
        });
    });
}