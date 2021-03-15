package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class) // 스프링이랑 같이 실행할래!!
@SpringBootTest // 스프링 부트를 띄운 상태로 테스트 하겠다!
@Transactional
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
//    @Autowired EntityManager em;
    
    @Test
//    @Rollback(value = false) // 이러면 롤백을 안 하고 커밋을 해 버린다. 이러면 Insert를 한다.
    public void 회원가입() {
        //given
        Member member = new Member();
        member.setName("kim");

        //when
        Long savedId = memberService.join(member); // 데이타베이스 트랜잭션이 커밋을 하는 순간 플러시가 되면서 jpa 영속성 컨텍스트에 있는 멤버 객체가 인서트 문이 만들어지면서 인서트가 나가는데 스프링에서 트랜잭션은 커밋을 안 해버리고 롤백을 한다. 그래서 insert문이 없다.

        //then
//        em.flush(); // 이러면 영속성 컨텍스트가 들어가고 인서트 되는거 확인 가능
        assertEquals(member, memberRepository.findOne(savedId)); // pk가 같으면 같은 영속성 컨텍스트에서 똑같은 애가 관리가 된다.
    }


    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() {
        //given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        //when
        memberService.join(member1);
        memberService.join(member2); // 예외가 발생해야한다.

        //then
        fail("예외가 발생해야 한다."); // 코드가 돌다가 여기에 오면 안 되는거다.
    }

}