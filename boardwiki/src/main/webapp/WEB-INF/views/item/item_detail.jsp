<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.7.1.min.js"></script>
<h2 class="big-name">${item.item_name}</h2>
<div>
	<div class="big-box1">
		<div class="midle-box1" style="display: inline-block; vertical-align: top;">
			<div class="image-box1"
				style="display: inline-block; vertical-align: top;">
				<img src="${item.item_image}" width="180" height="180">
			</div>
			<div class="small-box1" style="display: inline-block; vertical-align: top;">
				<div class="text-box1" style="display: inline-block; vertical-align: top;">
					<p>장르:${item.item_genre}</p>
					<p>최소연령:${item.minage}세</p>
					<p>참여인원:${item.minplayers}~${item.maxplayers}명</p>
					<p>플레이 타임:${mintime}~${maxtime}시간</p>
					<p>출시연도:${item.item_year}년</p>	
				</div>
				<div class="text-box1" style="display: inline-block; vertical-align: top;">
				    <p>순위:${item.item_rank}위</p>
					<p>평점:${item.item_average}점</p>
					<p>재고:${item.item_stock}개</p>
					<p>가격:${item.item_price}원</p>
				</div>
                <c:if test="${!empty member.mem_num}">
                <div class="text-box2" style="display: inline-block; vertical-align: top;">
           <!-- <button>바로구매</button> -->
                    <form id="addToCart" method="post" action="${pageContext.request.contextPath}/cart/cart">
                        <input type="hidden" name="item" value="${item.item_num}" />
                        <input type="hidden" name="user" value="${member.mem_num}" />
                        <label for="quantity">수량:</label>
                        <input type="number" id="quantity" name="item_quantity" value="1" min="1" max="${item.item_stock}"/>
                        <button type="submit">장바구니</button>
                    </form>
                </div>
                </c:if>
			</div>
		</div>
	</div>
	<div class="big-box2">
	    <div class="midle-box2">
	        <button>간단설명</button>
	        <button onclick="location.href='/rent/rent?item_num=${item.item_num}'">대여</button>
		    <button>중고거래</button>
		</div>
		<div class="midle-box3">
		    <a>${item.description}</a>
		</div>
        <button>목록</button>
     </div>
</div>