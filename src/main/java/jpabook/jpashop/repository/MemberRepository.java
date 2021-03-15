package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

//    @PersistenceContext // JPA 표준 애노테이션 --> @Autowired로 바꿀 수 있음. 스프링 데이타 JPA가 지원해줌. 근데 그럼 얘는 생성자 하나 있으면 생략 가능 그럼 롬복으로 처리 가능. 개꿀
    private final EntityManager em; // 스프링이 엔티티 매니저를 만들어 주입해 준다.

//    // EntityManagerFactory를 주입받고 싶을 때. 거의 쓸 일이 없다.
//    @PersistenceUnit
//    private EntityManagerFactory emf;

    // 영속성 컨텍스트로 올린다.
    // 영속성 컨텍스트의 key와 value가 있는데, key가 id 값이 된다. db pk랑 매핑한 게 키가 된다.
    public void save(Member member) {
        em.persist(member); // JPA가 얘를 저장해 준다.
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id); // JPA가 제공하는 찾는 기능
    }

    public List<Member> findAll() {
        // JPA 쿼리를 작성한다.
        // JPQL과 SQL 쿼리는 거의 비슷하다. JPQL이 SQL로 번역이 된다.
        // 하지만 차이가 존재. SQL은 테이블 대상으로 쿼리를 하는데 JPQL은 객체를 대상으로 쿼리를 한다. Member entity 객체에 대한 alias를 m으로, 그리고 엔티티 멤버를 조회해라!
        return em.createQuery("select m from Member m", Member.class) // 첫번째 JPQL, 두 번째 반환 타입
                .getResultList(); // 조회된 결과를 result로 반환
    }

    public List<Member> findByName(String name) {
        // name 파라미터를 바인딩
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }

}
