<%-- @noinspection JSPTaglibDescriptor --%>
<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>


<div class="lang-dropdown">
    <div class="LangMenu" onclick="toggleLangMenu(event)">
        🌐
        <spring:message code="lang.select" />
        <span class="arrow">▾</span>

        <div id="langMenu" class="lang-menu">
            <a href="?lang=en" class="${(empty param.lang or param.lang eq 'en') ? 'active' : ''}">
                🇬🇧 <spring:message code="lang.en" />
            </a>

            <a href="?lang=te"
               class="${param.lang == 'te' ? 'active' : ''}">
                🇮🇳 <spring:message code="lang.te" />
            </a>

            <a href="?lang=hi"
               class="${param.lang == 'hi' ? 'active' : ''}">
                🇮🇳 <spring:message code="lang.hi" />
            </a>
        </div>
    </div>
</div>

<!-- JavaScript -->
<script type="text/javascript">
    function toggleLangMenu(event) {
        if (event) event.stopPropagation();

        /** @type {HTMLElement} */
        const menu = document.getElementById("langMenu");

        if (menu) {
            menu.style.display =
                (menu.style.display === "block") ? "none" : "block";
        }
    }

    document.addEventListener("click", function (event) {
        const dropdown = document.querySelector(".LangMenu");

        /** @type {HTMLElement} */
        const menu = document.getElementById("langMenu");

        if (menu && menu.style.display === "block" &&
            !dropdown.contains(event.target)) {
            menu.style.display = "none";
        }
    });
</script>
