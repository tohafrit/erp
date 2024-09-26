<link rel="stylesheet" type="text/css" href="<c:url value="/resources/style/prod/view/printer.css"/>">

<div class="root__content"></div>

<script>
    $(() => {
        const $content = $('div.root__content');
        $.get({
            url: '/api/view/prod/printer/list',
            beforeSend: () => togglePreloader(true),
            complete: () => togglePreloader(false)
        }).done(html => $content.html(html));
    })
</script>