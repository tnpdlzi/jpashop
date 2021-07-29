package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// 타입과 Id를 넣어준다.
// findAll, save가 유지된다. JpaRepository 인터페이스 들어가보면 뭐가 있는지 볼 수 있다. 공통적인 메서드는 다 만들어져있다. 구현체를 알아서 다 만들어서 넣어준다.
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 이것만 써도 Spring Data JPA가
    // select m from Member m where m.name = :name 을 짜줘버린다. 이 name을 보고.
    List<Member> findByName(String name);
}
