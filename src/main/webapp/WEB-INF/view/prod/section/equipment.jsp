<link rel="stylesheet" type="text/css" href="<c:url value="/resources/style/prod/view/equipment.css"/>">

<div class="root__content"></div>

<script>
    $(() => {
        const $content = $('div.root__content');
        $.get({
            url: '/api/view/prod/equipment/list',
            beforeSend: () => togglePreloader(true),
            complete: () => togglePreloader(false)
        }).done(html => $content.html(html));
    })
</script>