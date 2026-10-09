<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Create account"/>
<c:set var="bare" value="${true}"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>

<div class="auth-wrap">
    <section class="auth-side">
        <h1>Start your<br><span>first streak.</span></h1>
        <p>Registration takes less than a minute. You can add your height, weight and goal later from your profile
           so the calorie estimates and tips fit you better.</p>
    </section>

    <section class="auth-main">
        <div class="auth-card">
            <h2>Create your account</h2>

            <div class="card">
                <c:if test="${not empty error}">
                    <div class="flash error"><c:out value="${error}"/></div>
                </c:if>

                <c:choose>
                    <c:when test="${registrationOpen == false}">
                        <p>Registration is closed at the moment. Please check back later or contact an administrator.</p>
                    </c:when>
                    <c:otherwise>
                        <form method="post" action="${pageContext.request.contextPath}/register">
                            <div class="field">
                                <label for="name">Full name</label>
                                <input type="text" id="name" name="name" maxlength="80" value="<c:out value='${name}'/>" required autofocus>
                            </div>
                            <div class="field">
                                <label for="email">Email</label>
                                <input type="email" id="email" name="email" value="<c:out value='${email}'/>" required>
                            </div>
                            <div class="field">
                                <label for="password">Password</label>
                                <input type="password" id="password" name="password" minlength="6" required>
                                <div class="hint">At least 6 characters.</div>
                            </div>
                            <div class="field">
                                <label for="confirm">Confirm password</label>
                                <input type="password" id="confirm" name="confirm" required>
                            </div>
                            <button class="btn block" type="submit">Register</button>
                        </form>
                    </c:otherwise>
                </c:choose>
            </div>

            <p class="small" style="margin-top:1rem;">
                Already registered? <a href="${pageContext.request.contextPath}/login">Sign in</a>
            </p>
        </div>
    </section>
</div>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
