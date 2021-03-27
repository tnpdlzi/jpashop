package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm()); // 컨트롤러에서 뷰로 넘어갈 때 얘를 실어서 넘긴다.
        return "members/createMemberForm"; // 이 html을 만들어야 한다.
    }

    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result) {
        // Member가 아닌 MemberForm을 쓰는 이유? 두개가 딱 맞질 않는다.게다가 validation까지 있다! 그래서 화면에 딱 맞는 폼 데이타를 새로 만들어 그걸로 받는게 낫다.
        // @Valid --> 폼 안에 있는 애노테이션을 활성화 해 준다.
        // BindingResult는 스프링에 있는거. 오류가 나면 result에 담겨서 아래 코드가 실행이 된다.
        if (result.hasErrors()) {
            return "members/createMemberForm"; // 여기까지 result를 끌고 가 줘서 여기서 에러를 다 뿌릴 수 있다.
            // memberForm에서 적어놨던 메시지가 출력된다.
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member); // 저장 된다.
        // 저장 된 다음 리다이렉트로 홈으로 보내버린다.
        return "redirect:/";
    }

    @GetMapping("/members")
    public String list(Model model) {
        List<Member> members = memberService.findMembers(); // 원래는 멤버를 그대로 뿌리는 게 아닌 DTO를 사용해 꼭 필요한 애만 뿌려야 한다.
        model.addAttribute("members", members);
        return "members/memberList";
    }
}
