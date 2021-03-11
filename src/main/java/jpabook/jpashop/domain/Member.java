package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded // 내장 타입을 포함했다. @Embaddable이랑 이거 둘 중 하나만 있어도 되긴 하는데 그냥 두개 다 해준다.
    private Address address;

    // 연관관계의 거울
    @OneToMany(mappedBy = "member") // order table에 있는 멤버 테이블에 의해 매핑이 된거야! 나는 매핑된 거울일 뿐이다! 여기 값을 넣어도 포린키 값이 변경되지 않는다.
    private List<Order> orders = new ArrayList<>();

}
