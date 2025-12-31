<%@ page contentType="text/html;charset=UTF-8" %>
    <%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
        <html>

        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>
                <spring:message code="login.title" />
            </title>
            <link rel="stylesheet" href="/css/style.css">
            <script>
                // Prevent backtracking
                history.pushState(null, null, location.href);
                window.onpopstate = function () {
                    history.go(1);
                };
            </script>
        </head>

        <body>

            <div class="container">
                <jsp:include page="../lang_dropdown.jsp" />

                <h3>
                    <spring:message code="login.header" />
                </h3>

                <spring:message code="login.username" var="phUsername" />
                <spring:message code="login.password" var="phPassword" />

                <form action="/login" method="post">
                    <input type="text" name="username" placeholder="${phUsername}" required>
                    <input type="password" name="password" placeholder="${phPassword}" required>
                    <button type="submit">
                        <spring:message code="login.button" />
                    </button>
                </form>
            </div>

            <script src="/js/toast.js"></script>
            <% String error=(String) request.getAttribute("error"); Boolean locked=(Boolean)
                request.getAttribute("locked"); if (error !=null) { %>
                <script>
                    showToast('<%= error %>', 'error', <%=(locked != null && locked) ? "0" : "5000" %>);
                    
                    <% if (locked != null && locked) { %>
                        // Disable form fields when locked
                        const form = document.querySelector('form');
                        const inputs = form.querySelectorAll('input');
                        const button = form.querySelector('button');

                        inputs.forEach(input => input.disabled = true);
                        button.disabled = true;
                    <% } %>
                </script>
                <% } %>

        </body>

        </html>