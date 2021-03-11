package jpabook.jpashop;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository {

    // 부트가 entitymanager 주입해 준다.
    @PersistenceContext
    private EntityManager em;

    public Long save(Member member) {
        em.persist(member);
        // side effect가 안 생기게 member를 리턴하는 게 아닌 id 정보 정도만 return한다.
        return member.getId();
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}
