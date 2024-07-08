package kr.spring.member.controller;

import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import kr.spring.member.service.MemberService;
import kr.spring.member.vo.MemberVO;
import kr.spring.util.AuthCheckException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MemberController {
	//네이버 로그인 설정 시작//
	@Value("${spring.oauth2.client.registration.naver.client-id}")
	private String naverClientId;

	@Value("${spring.oauth2.client.registration.naver.client-secret}")
	private String naverClientSecret;
	//네이버 로그인 설정 끝//

	@Autowired
	private MemberService memberService;
	
	//자바빈(VO) 초기화
		@ModelAttribute
		public MemberVO initCommand() {
			return new MemberVO();
		}
		
	/*==============================
	 * 회원로그인
	 *==============================*/
	//로그인 폼 호출
	@GetMapping("/member/login")
	public String formLogin() {
		return "memberLogin";
	}
	//로그인 폼에서 전송된 데이터 처리
	@PostMapping("/member/login")
	public String submitLogin(@Valid MemberVO memberVO,BindingResult result,HttpSession session, HttpServletResponse response){
		log.debug("<<회원로그인>> : " + memberVO);

		//유효성 체크 결과 오류가 있으면 폼 호출
		//id와 passwd 필드만 체크
		if(result.hasFieldErrors("mem_email") ||
				result.hasFieldErrors("mem_passwd")){
			return formLogin();
		}

		//로그인 체크(id,비밀번호 일치 여부 체크)
		MemberVO member = null;
		try {
			member = memberService.selectCheckMember(
					memberVO.getMem_email());
			boolean check = false;
			if(member!=null) {
				//비밀번호 일치 여부 체크
				check = member.ischeckedPassword(memberVO.getMem_passwd());
			}
			if(check) {//인증 성공
				//==== 자동로그인 체크 시작====//
				//==== 자동로그인 체크 끝====//

				//인증 성공, 로그인 처리
				session.setAttribute("user", member);


				log.debug("<<인증성공>>");
				log.debug("<<id>> : " + member.getMem_email());
				log.debug("<<auth>> : " + member.getMem_auth());

				if(member.getMem_auth() == 9) {//관리자
					return "redirect:/main/admin";
				}else
					return "redirect:/main/main";
			}

			//인증 실패
			throw new AuthCheckException();	
		}catch(AuthCheckException e) {
			//인증 실패로 로그인 폼 호출
			if(member!=null && member.getMem_auth()==1) {//정지회원 메시지 표시
				result.reject("noAuthority");
			}else {
				result.reject("invalidIdOrPassword");
			}
			log.debug("<<인증 실패>>");

			return formLogin();
		}
	}
	
	
	
	/*==============================
	 * 로그아웃
	 *==============================*/	
	@GetMapping("/member/logout")
	public String processLogout(HttpSession session) {
		//로그아웃
		session.invalidate();
		
		//====자동로그인 시작====//
		//====자동로그인 끝====//
		
		return "redirect:/main/main";
	}
	/*==============================
	 * MY페이지
	 *==============================*/
	@GetMapping("/member/myPage")
	public String process(HttpSession session,Model model) {
		MemberVO user = (MemberVO)session.getAttribute("user");
		//회원정보
		MemberVO member = memberService.selectMember(user.getMem_num());
		log.debug("<<MY페이지>> : " + member);
		
		model.addAttribute("member",member);
				
		return "myPage";
	}
}
