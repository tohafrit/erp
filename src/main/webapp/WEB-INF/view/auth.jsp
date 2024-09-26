<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title><fmt:message key="page.authorization.title"/></title>

        <link rel="icon" href="<c:url value="/resources/image/favicon.ico"/>" type="image/x-icon">
        <link rel="shortcut icon" href="<c:url value="/resources/image/favicon.ico"/>" type="image/x-icon">

        <link rel="stylesheet" type="text/css" href="<c:url value="/resources/semantic-ui/semantic.min.css"/>"/>
        <link rel="stylesheet" type="text/css" href="<c:url value="/resources/style/reset.css"/>"/>
        <link rel="stylesheet" type="text/css" href="<c:url value="/resources/style/default.css"/>"/>
    </head>
    <body>
        <div class="form-container">
            <div class="form-container__logo"></div>
            <div class="auth-form">
                <div class="auth-form__block">
                    <div class="auth-form__title"><fmt:message key="page.authorization.title"/></div>
                    <div class="auth-form__sub-title"><fmt:message key="page.authorization.message"/></div>
                    <p class="b-error">${error}</p>
                    <form action="j_security_check" method="POST" class="ui small form">
                        <div class="field">
                            <div class="ui fluid input icon big">
                                <input placeholder="<fmt:message key="page.authorization.login"/>" type="text" size="20" name="j_username" autofocus>
                                <i class="user alternate icon"></i>
                            </div>
                        </div>
                        <div class="field">
                            <div class="ui fluid input icon big">
                                <input placeholder="<fmt:message key="page.authorization.password"/>" type="password" size="20" name="j_password">
                                <i class="lock icon"></i>
                            </div>
                        </div>
                        <input class="ui button right floated blue big" type="submit" value="<fmt:message key="label.button.enter"/>">
                    </form>
                </div>
            </div>
        </div>
    </body>
</html>