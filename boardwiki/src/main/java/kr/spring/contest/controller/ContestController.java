package kr.spring.contest.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import kr.spring.contest.service.ContestService;
import kr.spring.contest.vo.ContestApplyVO;
import kr.spring.contest.vo.ContestVO;
import kr.spring.member.vo.MemberVO;
import kr.spring.util.PagingUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ContestController {
	@Autowired
	ContestService contestservice;

	@ModelAttribute
	public ContestVO initCommand() {
		return new ContestVO();
	}



	/*=====================
	 * 게시판 글쓰기
	 *=====================*/
	//등록 폼 호출
	@GetMapping("/contest/contestWrite")
	public String writeform(ContestVO contestVO, HttpServletRequest request,
			HttpSession session,
			Model model) {
		MemberVO member =(MemberVO)session.getAttribute("user");
		if(member == null && member.getMem_auth() < 9 ) {
			model.addAttribute("message", "잘못된 접근입니다.");
			model.addAttribute("url",
					request.getContextPath()+"/main/main");

			return "common/resultAlert";
		}
		return "contestWrite";
	}
	//등록 폼에서 전송된 데이터 처리
	@PostMapping("/contest/contestWrite")
	public String writesubmit(@Valid ContestVO contestVO,
			BindingResult result,
			HttpServletRequest request,
			HttpSession session,
			Model model)
					throws IllegalStateException,
					IOException{
		log.debug("<<게시판 글 저장>> : " + contestVO);
		
		//유효성 체크 결과 오류가 있으면 폼 호출
		if(result.hasErrors()) {
			return writeform(contestVO, request, session, model);
		}

		//회원번호 셋팅
		MemberVO member = (MemberVO)session.getAttribute("user");
		contestVO.setMem_num(member.getMem_num());
		contestVO.getCon_sdate();
		
		// 현재 날짜와 비교하여 con_sdate가 오늘보다 크면 con_status를 2로 설정
	    Date now = new Date();
	    
	    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
	    
	    String nowTime = sdf1.format(now);
	    
	    String sdate = contestVO.getCon_sdate();
	    
	    int compare = sdate.compareTo(nowTime);
	    
	    System.out.println("**********************"+compare+"*********************");
	    
	    if (compare > 0) {
	        contestVO.setCon_status(2);
	    }else {
	    	contestVO.setCon_status(0);
	    }
		//글쓰기
		contestservice.insertContest(contestVO);

		//View 메시지 처리
		model.addAttribute("message", "성공적으로 글이 등록되었습니다.");
		model.addAttribute("url",
				request.getContextPath()+"/contest/contestList");

		return "common/resultAlert";
	}

	/*=====================
	 * 		대회 목록
	 *=====================*/
	@GetMapping("/contest/contestList")
	public String getList(
			@RequestParam(defaultValue="1") int pageNum,
			@RequestParam(defaultValue="1") int order,
			@RequestParam(defaultValue="") String category,
			String keyfield,String keyword,Model model) {

		log.debug("<<대회 목록 진입>>");

		Map<String,Object> map =
				new HashMap<>();
		map.put("category", category);
		map.put("keyfield", keyfield);
		map.put("keyword", keyword);

		//전체,검색 레코드수
		int count = contestservice.countAllcontest(map);

		//페이지 처리
		PagingUtil page = new PagingUtil(keyfield,keyword,pageNum,count,20,10,"contestList","&order="+order);
		
		
		List<ContestVO> list = null;
		
		if(count > 0) {
			map.put("order", order);
			map.put("start", page.getStartRow());
			map.put("end", page.getEndRow());

			list = contestservice.selectContestList(map);
		}

		model.addAttribute("count", count);
		model.addAttribute("list", list);
		model.addAttribute("page", page.getPage());

		return "contestList";
	}
	/*=====================
	 * 	   대회 상세
	 *=====================*/
	@GetMapping("/contest/contestDetail")
	public ModelAndView contestDetail(long con_num, HttpSession session) {
		
		log.debug("<<대회 상세 진입 : >>   " + con_num);
	    // 조회수 증가
	    contestservice.updateContestHit(con_num);

	    // 대회 상세 정보 가져오기
	    ContestVO contest = contestservice.detailContest(con_num);

	    // 사용자가 이미 신청했는지 여부를 확인
	    MemberVO member = (MemberVO) session.getAttribute("user");
	    boolean applied = false;
	    int conManCount = contestservice.countContestMan(con_num);

	    if (member != null) {
	        ContestApplyVO contestApplyVO = new ContestApplyVO();
	        contestApplyVO.setMem_num(member.getMem_num());
	        contestApplyVO.setCon_num(con_num);
	        applied = contestservice.selectContestApplyList(contestApplyVO) > 0;	        
	    }

	    // ModelAndView 객체에 데이터 추가
	    ModelAndView mav = new ModelAndView("contestDetail");
	    mav.addObject("contest", contest);
	    mav.addObject("applied", applied);
	    mav.addObject("conManCount", conManCount);
	   	    
	    
	    return mav;
	}



	/*=====================
	 * 	  대회 신청/취소 처리
	 *=====================*/
	@GetMapping("/contest/contestApply")
	public String submitApply(@RequestParam String action, long con_num,
			HttpServletRequest request, HttpSession session, ContestApplyVO contestApplyVO, Model model) {

		MemberVO member = (MemberVO) session.getAttribute("user");
		contestApplyVO.setMem_num(member.getMem_num());
		contestApplyVO.setCon_num(con_num);

		
			
		if (action.equals("cancel")) {
			contestservice.cancelContestApply(contestApplyVO);
			model.addAttribute("message", "신청 취소 완료");
			model.addAttribute("url", request.getContextPath() + "contestDetail?con_num=" + contestApplyVO.getCon_num());
			return "common/resultAlert";
		}
		if(action.equals("apply")) {
			contestservice.applyForContest(contestApplyVO);
			model.addAttribute("message", "신청 완료");
			model.addAttribute("url", request.getContextPath() + "contestDetail?con_num=" + contestApplyVO.getCon_num());
			return "common/resultAlert";
		}
		model.addAttribute("message", "신청 오류");
		model.addAttribute("url", request.getContextPath() + "contestDetail?con_num=" + contestApplyVO.getCon_num());
		return "common/resultAlert";
	}
	
	/*=====================
	 * 	  대회 삭체 처리
	 *=====================*/
	@GetMapping("/contest/deleteContest")
	public String deleteContest(@RequestParam Long con_num,
			HttpServletRequest request, HttpSession session, ContestVO contestVO, Model model) {

		MemberVO member = (MemberVO) session.getAttribute("user");
		
		if(member.getMem_auth() < 9) {
			model.addAttribute("message", "잘못된 접근입니다.");
			model.addAttribute("url", request.getContextPath() + "/main/main");
			return "common/resultAlert";
		}

		contestservice.deleteContest(con_num);
		
		model.addAttribute("message", "삭제되었습니다.");
		model.addAttribute("url", request.getContextPath() + "/contest/contestList");
		
		return "common/resultAlert";
	}
}
