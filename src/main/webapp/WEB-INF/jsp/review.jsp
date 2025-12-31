<%@ page contentType="text/html;charset=UTF-8" %>
    <%@ page import="jakarta.servlet.http.HttpSession" %>
        <%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

            <html>

            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>
                    <spring:message code="review.title" />
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

                <% HttpSession sess=request.getSession(false); String username=(String) sess.getAttribute("username");
                    String pan=(String) sess.getAttribute("panNumber"); String name=(String)
                    sess.getAttribute("fullName"); String videoFile=(String) sess.getAttribute("videoFileName"); %>

                    <div class="container">
                        <jsp:include page="../lang_dropdown.jsp" />

                        <h3>
                            <spring:message code="review.header" />
                        </h3>

                        <table>
                            <tr>
                                <th>
                                    <spring:message code="review.label.username" />
                                </th>
                                <td>
                                    <%= username %>
                                </td>
                            </tr>
                            <tr>
                                <th>
                                    <spring:message code="review.label.pan" />
                                </th>
                                <td>
                                    <%= pan %>
                                </td>
                            </tr>
                            <tr>
                                <th>
                                    <spring:message code="review.label.name" />
                                </th>
                                <td>
                                    <%= name %>
                                </td>
                            </tr>
                            <tr>
                                <th>
                                    <spring:message code="review.label.video" />
                                </th>
                                <td>
                                    <% if (videoFile !=null) { %>
                                        <video width="220" height="160" controls>
                                            <source src="/video/<%= videoFile %>" type="video/webm">
                                            Your browser does not support video.
                                        </video>
                                        <% } else { %>
                                            <span style="color:red;">
                                                <spring:message code="review.error.novideo" />
                                            </span>
                                            <% } %>
                                </td>
                            </tr>
                        </table>

                        <div class="edit-buttons"
                            style="margin-top:20px; display:flex; gap:10px; flex-wrap: wrap; justify-content: center;">
                            <a href="/edit-pan" class="btn btn-secondary"
                                style="text-decoration:none; padding:10px 20px; background-color:#6c757d; color:white; border-radius:4px; font-weight:bold;">
                                Edit PAN Details
                            </a>
                            <a href="/edit-video" class="btn btn-secondary"
                                style="text-decoration:none; padding:10px 20px; background-color:#6c757d; color:white; border-radius:4px; font-weight:bold;">
                                Edit Video Recording
                            </a>
                        </div>

                        <form method="post" action="/submit" style="margin-top:20px;">
                            <button type="submit"
                                style="width:100%; padding:12px; background-color:#28a745; color:white; border:none; border-radius:4px; font-weight:bold; cursor:pointer;">
                                <spring:message code="review.button.submit" />
                            </button>
                        </form>
                    </div>

            </body>

            </html>