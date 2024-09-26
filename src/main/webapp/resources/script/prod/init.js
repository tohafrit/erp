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
    },
    visibilitySelector: '.sidebar__switcher'
});