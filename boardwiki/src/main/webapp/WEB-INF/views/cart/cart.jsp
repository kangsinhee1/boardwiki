<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<script src="${pageContext.request.contextPath}/js/jquery-3.7.1.min.js"></script>
<div class="page-main">
    <h2>장바구니</h2>
    <div class="text-box">
        <c:forEach var="cart" items="list">
        <div class="image-box">
            <img src="${item.item_image}" width="150" height="150">
            <input type="hidden" name="item_num" value="${item.item_num}" />
            <p>${item.item_name}</p>
            <p>${item.item_price}</p>
            <p>${cart.item_quantity}</p>
        </div>
        </c:forEach>
        <div>
            <label for="quantity">수량:</label>
            <input type="number" id="quantity" name="item_quantity" value="${cart.item_quantity}" min="1" max="${item.item_stock}"/>
        </div>
        
        <form id="addToCart" method="get" action="${pageContext.request.contextPath}/cart/order">
             <button type="submit">결재하기</button>
        </form>
    </div>
</div>
