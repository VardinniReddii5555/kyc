<%@ page contentType="text/html;charset=UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

            <html>

            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>
                    <spring:message code="success.title" />
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

                    <!-- Message -->
                    <c:if test="${alreadySubmitted}">
                        <h3 style="color:red;">
                            <spring:message code="success.header.already" />
                        </h3>
                    </c:if>

                    <c:if test="${not alreadySubmitted}">
                        <h3 class="success">
                            <spring:message code="success.header.new" />
                        </h3>
                    </c:if>

                    <!-- KYC Details from DB -->
                    <c:if test="${not empty kyc}">
                        <table>
                            <tr>
                                <th>
                                    <spring:message code="success.label.username" />
                                </th>
                                <td>${kyc.username}</td>
                            </tr>

                            <tr>
                                <th>
                                    <spring:message code="success.label.pan" />
                                </th>
                                <td>${kyc.panNumber}</td>
                            </tr>

                            <tr>
                                <th>
                                    <spring:message code="success.label.name" />
                                </th>
                                <td>${kyc.fullName}</td>
                            </tr>

                            <tr>
                                <th>
                                    <spring:message code="success.label.video" />
                                </th>
                                <td>
                                    <a href="/video/${kyc.videoPath.substring(14)}" target="_blank">
                                        ${kyc.videoPath}
                                    </a>
                                </td>
                            </tr>
                        </table>
                    </c:if>

                    <br>
                    <form action="/logout" method="get">
                        <button type="submit">
                            <spring:message code="success.button.login" />
                        </button>
                    </form>

                </div>

            </body>

            </html>