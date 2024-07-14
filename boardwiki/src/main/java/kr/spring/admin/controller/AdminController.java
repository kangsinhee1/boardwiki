package kr.spring.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.spring.member.service.MemberService;
import kr.spring.member.vo.MemberVO;
import kr.spring.util.PagingUtil;

@Controller
public class AdminController {
	@Autowired
	private MemberService memberService;
	
	
	@GetMapping("/admin/adminPage")
	public String adminPage(HttpSession session,
							HttpServletRequest request,
							Model model) {
		MemberVO user = (MemberVO)session.getAttribute("user");
		if(user.getMem_auth()!= 9) {
			model.addAttribute("message","관리자 등급만 접속할 수 있습니다.");
			model.addAttribute("url",request.getContextPath()+"/main/main");
			return "common/resultAlert";
		}
		
		return "adminPage";
	}
	
	@GetMapping("/adminPage/memberManage")
	public String memberManagePage(@RequestParam(defaultValue="1") int pageNum,
            						@RequestParam(defaultValue="") String category,
            						String keyfield,	
            						String keyword,Model model,
            						HttpSession session,
            						HttpServletRequest request
            						) {
		MemberVO user = (MemberVO)session.getAttribute("user");
		if(user.getMem_auth()!= 9) {
			model.addAttribute("message","관리자 등급만 접속할 수 있습니다.");
			model.addAttribute("url",request.getContextPath()+"/main/login");
			return "common/resultAlert";
		}
		Map<String,Object> map = new HashMap<String,Object>();

		map.put("category", category);
		map.put("keyfield", keyfield);
		map.put("keyword", keyword);
		
		int count = memberService.countAllmember(map);
		
		PagingUtil page =
				new PagingUtil(keyfield,keyword,pageNum,count,20,10,"mainList");
		 
		List<MemberVO> list = null;
	    if(count > 0) {
	    	map.put("start", page.getStartRow());
	    	map.put("end", page.getEndRow());
	    	
	    	list = memberService.selectAllmember(map);  
	    }

	    model.addAttribute("count", count);
		model.addAttribute("list", list);
		model.addAttribute("page", page.getPage());
		
		return "memberManage";
	}
}
