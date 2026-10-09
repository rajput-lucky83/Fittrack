<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="System settings"/>
<c:set var="pageSub" value="These apply to everyone using the site."/>
<c:set var="nav" value="settings"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>

<div class="card" style="max-width:640px;">
    <form method="post" action="${pageContext.request.contextPath}/admin/settings">

        <div class="field">
            <label for="site_name">Site name</label>
            <input type="text" id="site_name" name="site_name" maxlength="40"
                   value="<c:out value="${settings['site_name']}"/>" required>
            <div class="hint">Shown in the browser tab title.</div>
        </div>

        <div class="row">
            <div class="field">
                <label for="max_workout_minutes">Longest allowed workout (min)</label>
                <input type="number" id="max_workout_minutes" name="max_workout_minutes" min="1" max="1440"
                       value="<c:out value="${settings['max_workout_minutes']}"/>" required>
                <div class="hint">Stops typing mistakes like 9000 minutes.</div>
            </div>
            <div class="field">
                <label for="default_weight_kg">Default body weight (kg)</label>
                <input type="number" id="default_weight_kg" name="default_weight_kg" min="1" max="300"
                       value="<c:out value="${settings['default_weight_kg']}"/>" required>
                <div class="hint">Used for calorie estimates when a member hasn't entered theirs.</div>
            </div>
        </div>

        <div class="field">
            <label class="check">
                <input type="checkbox" name="allow_registration" ${settings['allow_registration'] == 'true' ? 'checked' : ''}>
                Allow new members to register
            </label>
        </div>
        <div class="field">
            <label class="check">
                <input type="checkbox" name="require_content_approval" ${settings['require_content_approval'] == 'true' ? 'checked' : ''}>
                Member posts must be approved before they appear
            </label>
        </div>

        <button class="btn" type="submit">Save settings</button>
    </form>
</div>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
