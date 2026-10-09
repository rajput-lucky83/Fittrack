<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="My profile"/>
<c:set var="pageSub" value="Your details and preferences."/>
<c:set var="nav" value="profile"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>

<div class="grid-2">

    <div class="card">
        <h2>Personal details</h2>
        <form method="post" action="${pageContext.request.contextPath}/user/profile">
            <input type="hidden" name="action" value="details">

            <div class="field">
                <label for="name">Name</label>
                <input type="text" id="name" name="name" maxlength="80" value="<c:out value='${profile.name}'/>" required>
            </div>
            <div class="field">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" value="<c:out value='${profile.email}'/>" required>
            </div>

            <div class="row">
                <div class="field">
                    <label for="weight">Weight (kg)</label>
                    <input type="number" id="weight" name="weight" step="0.1" min="20" max="300" value="${profile.weightKg}">
                </div>
                <div class="field">
                    <label for="height">Height (cm)</label>
                    <input type="number" id="height" name="height" step="0.1" min="80" max="250" value="${profile.heightCm}">
                </div>
            </div>

            <div class="field">
                <label for="goal">Main fitness goal</label>
                <select id="goal" name="goal">
                    <option value="GENERAL"     ${profile.fitnessGoal == 'GENERAL' ? 'selected' : ''}>General fitness</option>
                    <option value="WEIGHT_LOSS" ${profile.fitnessGoal == 'WEIGHT_LOSS' ? 'selected' : ''}>Lose weight</option>
                    <option value="MUSCLE_GAIN" ${profile.fitnessGoal == 'MUSCLE_GAIN' ? 'selected' : ''}>Build muscle</option>
                    <option value="ENDURANCE"   ${profile.fitnessGoal == 'ENDURANCE' ? 'selected' : ''}>Improve endurance</option>
                </select>
                <div class="hint">Used to tailor the suggestions on your dashboard.</div>
            </div>

            <button class="btn" type="submit">Save changes</button>
        </form>

        <c:if test="${profile.bmi > 0}">
            <p class="small" style="margin-top:1rem;">
                BMI: <strong>${profile.bmi}</strong> (${profile.bmiCategory}).
                <span class="muted">It's only a rough indicator.</span>
            </p>
        </c:if>
        <p class="small muted" style="margin:.4rem 0 0;">Member since ${profile.joinedOn}</p>
    </div>

    <div class="card">
        <h2>Change password</h2>
        <form method="post" action="${pageContext.request.contextPath}/user/profile">
            <input type="hidden" name="action" value="password">

            <div class="field">
                <label for="currentPassword">Current password</label>
                <input type="password" id="currentPassword" name="currentPassword" required>
            </div>
            <div class="field">
                <label for="newPassword">New password</label>
                <input type="password" id="newPassword" name="newPassword" minlength="6" required>
            </div>
            <div class="field">
                <label for="confirmPassword">Repeat new password</label>
                <input type="password" id="confirmPassword" name="confirmPassword" required>
            </div>
            <button class="btn clay" type="submit">Update password</button>
        </form>
    </div>

</div>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
