package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

//@Controller + @ResponseBody
@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    // 이렇게 엔티티를 직접 보내면 엔티티의 정보가 전부 외부에 노출이 되어버린다. 새로운 DTO를 만들어 @JsonIgnore를 넣어주자.
    // 또한 이렇게 어레이를 바로 반환하면 스펙이 이렇게 굳는다. 즉 "count": 4, "data": [] <-- 이 어레이 안에 데이터가 들어가게 해 줘야 확장 가능하다.
    @GetMapping("/api/v1/members")
    public List<Member> memberV1() {
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result memberV2() {
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new Result(collect.size(), collect);
    }

    // list를 바로 내보내면 json 배열 타입으로 나가지만 이렇게 내보내면 "data": [ ........ ] 이런 식으로 나간다.
    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

    // api를 만들 때는 엔티티를 파라미터로 받아서도 안 되고, 엔티티를 외부에 노출해서도 안 된다. 이 예제는 잘못된 것.
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) { // @Valid가 붙으면 javax validation을 다 읽는다.(@NotEmpty 같은거)
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    // 이 방식을 사용하면 api스펙이 바뀌지 않는다. 뭔가 엔티티에서 변화가 있으면 컴파일 오류가 생긴다.
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id, @RequestBody @Valid UpdateMemberRequest request) {

        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    // 이걸 만들면 API 스펙을 한 눈에 확인 가능. 어떤 값이 들어오는지.
    @Data
    static class CreateMemberRequest {
        @NotEmpty // validation 스펙을 여기 넣을 수 있다.
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

}
