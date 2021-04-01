package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
// 읽기가 많으니까 리드온리를 트루로 하고 쓰기에만 따로 걸어준다.
@Transactional(readOnly = true) // JPA의 모든 데이터 변경이나 로직들은 트랜잭션 안에서 동작해야 한다. 퍼블릭 메서드들에 트랜잭션 쭉 걸리게 된다.
// javax거 보단 스프링 거를 쓴다. 이미 스프링 다 썼으니까.또 쓸 수 있는 옵션들이 많다.
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원 가입
     */
    @Transactional // 따로 설정한 애는 얘가 우선권을 갖는다. 디폴트 값이 readonly = false이다.
    public Long join(Member member) {
        // 중복회원 검증
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId(); // save 때 키가 들어가므로 항상 값이 있음이 보장됨.
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName()); // DB에서 Unique 제약 조건을 잡아 줄 것.
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회
//    @Transactional(readOnly = true) // 이러면 JPA가 조회하는 데서는 성능을 최적화 해 준다.
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    // 한 건만 조회
//    @Transactional(readOnly = true) // 이러면 JPA가 조회하는 데서는 성능을 최적화 해 준다.
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }


    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findOne(id);
        member.setName(name);
    }
}
