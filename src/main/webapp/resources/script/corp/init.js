// Personal menu
const personalMenuItems = $('.personal-menu__items');
$('.personal-menu').on({
    'mouseenter': () => personalMenuItems.show(),
    'mouseleave': () => personalMenuItems.hide()
});

// Site menu
const siteMenuItems = $('.site-menu__items');
$('.site-menu').on({
    'mouseenter': () => siteMenuItems.show(),
    'mouseleave': () => siteMenuItems.hide()
});

// Left menu
$('.sidebar__nav').treefactory({
    hint: {
        isShow: true
    },
    search: {
        inputSelector: '.menu-search'
    }
});

// Fullcalendar
document.addEventListener('DOMContentLoaded', function() {
    const domElement = document.getElementById('calendar');
    if (domElement !== null) {
        let calendar = new FullCalendar.Calendar(domElement, {
            height: 400,
            expandRows: true,
            dayMaxEvents: true,
            buttonText: {today: 'сегодня'},
            selectable: true,
            locale: 'ru',
            fixedWeekCount: false,
            // events: JSON.parse('${jsonEvents}'),
            eventClick: function (event) {
                if (event.url) {
                    window.location.href = event.url
                }
            }
        });
        calendar.render();
    }
});