<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Sign in"/>
<c:set var="bare" value="${true}"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>

<div class="auth-wrap">
    <section class="auth-side">
        <h1>Log it.<br>Track it.<br><span>Beat it.</span></h1>
        <p>Your workouts, goals and challenges in one place.</p>
        <ul>
            <li>Log every session in under a minute</li>
            <li>See your weekly progress in plain charts</li>
            <li>Join challenges and compare how you did</li>
        </ul>
    </section>

    <section class="auth-main">
        <div class="auth-card">
            <h2>Welcome back</h2>
            <p class="muted">Sign in to your <c:out value="${applicationScope.siteName}"/> account.</p>

            <div class="card">
                <c:if test="${not empty error}">
                    <div class="flash error"><c:out value="${error}"/></div>
                </c:if>

                <form method="post" action="${pageContext.request.contextPath}/login">
                    <div class="field">
                        <label for="email">Email</label>
                        <input type="email" id="email" name="email" value="<c:out value='${email}'/>" required autofocus>
                    </div>
                    <div class="field">
                        <label for="password">Password</label>
                        <input type="password" id="password" name="password" required>
                    </div>
                    <button class="btn block" type="submit">Sign in</button>
                </form>
            </div>

            <p class="small" style="margin-top:1rem;">
                New here? <a href="${pageContext.request.contextPath}/register">Create an account</a>
            </p>
        </div>
    </section>
</div>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
