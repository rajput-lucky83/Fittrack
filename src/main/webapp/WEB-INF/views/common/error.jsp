<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Something went wrong"/>
<c:set var="bare" value="${true}"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<div class="auth-main" style="min-height:100vh;">
    <div class="auth-card" style="text-align:center;">
        <div class="card">
            <h2>Oops</h2>
            <p>
                <c:choose>
                    <c:when test="${not empty errorMessage}"><c:out value="${errorMessage}"/></c:when>
                    <c:otherwise>The page you asked for isn't available.</c:otherwise>
                </c:choose>
            </p>
            <a class="btn" href="${pageContext.request.contextPath}/login">Back to start</a>
        </div>
    </div>
</div>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
