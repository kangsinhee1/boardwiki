package kr.spring.order.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.spring.cart.service.CartService;
import kr.spring.cart.vo.CartVO;
import kr.spring.item.service.ItemService;
import kr.spring.member.vo.MemberVO;
import kr.spring.order.service.OrderService;
import kr.spring.order.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class OrderController {
	@Autowired
	private CartService cartService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private OrderService orderService;

	//자바빈(VO) 초기화
	@ModelAttribute
	public OrderVO initCommand() {
		return new OrderVO();
	}

	/*=========================
	 * 장바구니에서 데이터 불러오기
	 *=========================*/
	@GetMapping("/order/order")
	public String GetToOrder(Model model,Long mem_num, HttpSession session){

		MemberVO member = (MemberVO) session.getAttribute("user");

		if (member == null) {
			return "redirect:/login"; // 세션에 user가 없으면 로그인 페이지로 리다이렉트
		}

		Map<String, Object> map = new HashMap<>();
		map.put("mem_num", member.getMem_num());

		List<CartVO> list = null;
		list = cartService.selectCartList(map);

		model.addAttribute("mem_num",mem_num);
		model.addAttribute("list",list);

		return "order";
	}

	/*=========================
	 * 주문창에 데이터 담기
	 *=========================*/
	@PostMapping("/order/order1")
	public String addToOrder(Integer item_quantity,
	        @RequestParam("order_name") String order_name,
	        @RequestParam("order_phone") String order_phone,
	        @RequestParam("order_zipcode") Long order_zipcode,
	        @RequestParam("order_address1") String order_address1,
	        @RequestParam("order_address2") String order_address2,
	        @RequestParam("order_pay") int order_pay,
	        HttpSession session, Model model) {

	    MemberVO member = (MemberVO) session.getAttribute("user");

	    log.debug("<<유저 - order_name>>" + order_name);
	    log.debug("<<유저 - mem_num>>" + member);

	    OrderVO order2 = orderService.selectagg(member.getMem_num());

	    OrderVO order = new OrderVO();
	    order.setMem_num(member.getMem_num());
	    order.setOrder_name(order_name);
	    order.setOrder_phone(order_phone);
	    order.setOrder_zipcode(order_zipcode);
	    order.setOrder_address1(order_address1);
	    order.setOrder_address2(order_address2);
	    order.setOrder_price(order2.getTotal_price());
	    order.setOrder_pay(order_pay);

	    orderService.insertOrder(order);
	    cartService.updateCartDate(member.getMem_num());

	    return "pay"; // "common/resultAlert" 대신 리다이렉트 사용
	}
//	// 새로운 GET 메서드 추가
//    @GetMapping("/order/order1")
//    public String showOrderForm(Model model, HttpSession session) {
//        MemberVO member = (MemberVO) session.getAttribute("user");
//
//        if (member == null) {
//            return "redirect:/login"; // 세션에 user가 없으면 로그인 페이지로 리다이렉트
//        }
//
//        // 주문 정보를 설정하는 로직을 추가합니다. 필요에 따라 설정하세요.
//        // 예를 들어, 세션에서 필요한 데이터를 가져와서 모델에 추가합니다.
//
//        return "pay"; // 실제 주문 폼을 보여주는 JSP 페이지 이름
//    }
	
	/*=========================
	 * 결제 완료창
	 *=========================*/
	 @GetMapping("/order/order1")
	    public String GetToPay(Model model, HttpSession session) {
	        MemberVO member = (MemberVO) session.getAttribute("user");

	        if (member == null) {
	            return "redirect:/login"; // 세션에 user가 없으면 로그인 페이지로 리다이렉트
	        }

	        log.debug("<<유저 - mem_num>>" + member.getMem_num());

	        // 주문 정보 가져오기
	        OrderVO order = orderService.selectnum(member.getMem_num());

	        if (order == null) {
	            // 주문 정보가 없을 경우 처리
	            return "redirect:/order/error"; // 예시로 에러 페이지로 리다이렉트
	        }

	        // 장바구니 정보 가져오기
	        Map<String, Object> map = new HashMap<>();
	        map.put("order_date", order.getOrder_date());
	        map.put("order_date", member.getMem_num());
	        List<CartVO> list = cartService.selectname(map);

	        // 모델에 데이터 추가
	        model.addAttribute("order", order);
	        model.addAttribute("list", list);

	        return "/order/order1";
	    }
}





















