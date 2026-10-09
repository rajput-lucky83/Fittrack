<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Guidance library"/>
<c:set var="pageSub" value="Tips and plans shared by the community, reviewed by our admins."/>
<c:set var="nav" value="content"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>

<div class="split">

    <div>
        <div class="card">
            <h2>Share a tip</h2>
            <form method="post" action="${pageContext.request.contextPath}/user/content">
                <div class="field">
                    <label for="title">Title</label>
                    <input type="text" id="title" name="title" maxlength="120" required>
                </div>
                <div class="field">
                    <label for="category">Category</label>
                    <select id="category" name="category">
                        <c:forEach var="cat" items="${categories}">
                            <option value="${fn:escapeXml(cat)}"><c:out value="${cat}"/></option>
                        </c:forEach>
                    </select>
                </div>
                <div class="field">
                    <label for="body">Your content</label>
                    <textarea id="body" name="body" maxlength="3000" required></textarea>
                </div>
                <button class="btn" type="submit">Submit for review</button>
            </form>
        </div>

        <div class="card" style="margin-top:1.2rem;">
            <h2>My submissions</h2>
            <c:forEach var="m" items="${mine}">
                <div class="item">
                    <div class="item-top">
                        <strong><c:out value="${m.title}"/></strong>
                        <span class="badge ${m.status == 'APPROVED' ? 'green' : (m.status == 'REJECTED' ? 'red' : 'amber')}">${m.status}</span>
                    </div>
                    <div class="small muted">${m.createdText}</div>
                    <c:if test="${not empty m.adminNote}">
                        <div class="small">Admin note: <c:out value="${m.adminNote}"/></div>
                    </c:if>
                </div>
            </c:forEach>
            <c:if test="${empty mine}"><p class="muted" style="margin:0;">You haven't submitted anything yet.</p></c:if>
        </div>
    </div>

    <div class="card">
        <h2>Approved tips</h2>
        <c:forEach var="a" items="${approved}">
            <div class="item">
                <div class="item-top">
                    <h3 style="margin:0;"><c:out value="${a.title}"/></h3>
                    <span class="badge blue"><c:out value="${a.category}"/></span>
                </div>
                <p style="white-space:pre-line; margin:.4rem 0;"><c:out value="${a.body}"/></p>
                <div class="small muted">by <c:out value="${a.submitterName}"/> &middot; ${a.createdText}</div>
            </div>
        </c:forEach>
        <c:if test="${empty approved}"><p class="muted" style="margin:0;">Nothing has been approved yet.</p></c:if>
    </div>

</div>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
