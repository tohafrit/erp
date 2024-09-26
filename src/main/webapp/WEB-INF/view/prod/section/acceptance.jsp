<form:form method="POST" action="/prod/acceptance" cssClass="ui small form js-acceptence-form" enctype="multipart/form-data" cssStyle="width:500px;">
    <h2 class="ui dividing header"><fmt:message key="acceptance.title"/></h2>
    <div class="field">
        <input type="file" name="file" id="file"/>
    </div>
    <button class="ui button" type="submit"><fmt:message key="label.button.generate"/></button>
</form:form>

<script>
    $(() => {
        $('.js-acceptance-form').on({
            'submit': e => $(e.currentTarget).find('[type="file"]').get(0).files.length !== 0
        });
    });
</script>