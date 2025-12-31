<%@ page contentType="text/html;charset=UTF-8" %>
    <%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
        <html>

        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>
                <spring:message code="pan.title" />
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
                    <spring:message code="pan.header" />
                </h3>

                <spring:message code="pan.placeholder.number" var="phPanNumber" />
                <spring:message code="pan.placeholder.name" var="phFullName" />

                <form id="panForm">

                    <input type="text" id="panNumber" name="panNumber" placeholder="${phPanNumber}"
                        pattern="[A-Z]{5}[0-9]{4}[A-Z]" maxlength="10" required oninput="toUpper(this)">

                    <input type="text" id="fullName" name="fullName" placeholder="${phFullName}" maxlength="100"
                        required oninput="toUpper(this)" onkeypress="return /^[a-zA-Z ]$/.test(event.key)">

                    <button type="submit">
                        <spring:message code="pan.button" />
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
                    <script>
                            // 🔥 Converts ANY input (typing/paste) to UPPERCASE
                            function toUpper(input) {
                                input.value = input.value.toUpperCase();
                            }

                        document.getElementById("panForm").addEventListener("submit", function (e) {
                            e.preventDefault();

                            const pan = document.getElementById("panNumber").value.trim();
                            const name = document.getElementById("fullName").value.trim();

                            // Show verifying toast
                            showToast("<spring:message code='pan.verifying'/>", 'info', 2000);

                            fetch("/verify-pan", {
                                method: "POST",
                                headers: { "Content-Type": "application/json" },
                                body: JSON.stringify({
                                    panNumber: pan,
                                    fullName: name
                                })
                            })
                                .then(res => res.json())
                                .then(data => {
                                    console.log("VERIFY PAN RESPONSE:", data);

                                    if (data.status === "SUCCESS") {
                                        showToast(data.message, 'success', 2000);
                                        setTimeout(() => {
                                            window.location.href = "/video";
                                        }, 1000);
                                    } else if (data.status === "LOCKED") {
                                        // Account is locked - disable form and show persistent error
                                        showToast(data.message, 'error', 0);

                                        // Disable all form fields and add locked styling
                                        const form = document.getElementById('panForm');
                                        const inputs = form.querySelectorAll('input');
                                        const button = form.querySelector('button');

                                        inputs.forEach(input => {
                                            input.disabled = true;
                                            input.classList.add('locked');
                                        });
                                        button.disabled = true;
                                    } else {
                                        showToast(data.message, 'error', 5000);
                                    }
                                })
                                .catch(err => {
                                    showToast("<spring:message code='pan.failed'/>", 'error', 5000);
                                    console.error(err);
                                });
                        });
                    </script>

        </body>

        </html>