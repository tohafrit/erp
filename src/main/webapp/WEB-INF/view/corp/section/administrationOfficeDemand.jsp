<link rel="stylesheet" type="text/css" href="<c:url value="/resources/style/corp/view/administration-office-demand.css"/>">

<div class="root__content"></div>

<script>
    $(() => {
        let url = ${isAho} ? '/api/view/corp/administration-office-demand/admin' : '/api/view/corp/administration-office-demand/list';
        const $content = $('div.root__content');
        $.get({
            url: url,
            beforeSend: () => togglePreloader(true),
            complete: () => togglePreloader(false)
        }).done(html => $content.html(html));
    })
</script>