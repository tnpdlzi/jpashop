package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

//    @NotEmpty // 근데 이러면 이건 엔티티에서 검증 로직이 들어가게 되는 거라 안 좋은 거다.
    private String name; // 이 name이 바뀌면 api 스펙이 바껴버리는 문제도 생긴다. 그래서 DTO가 필요하다.

    @Embedded // 내장 타입을 포함했다. @Embaddable이랑 이거 둘 중 하나만 있어도 되긴 하는데 그냥 두개 다 해준다.
    private Address address;

    // 연관관계의 거울
    @JsonIgnore //양방향 연관관계에서는 한 쪽을 JsonIgnore해 주어야 한다.
    @OneToMany(mappedBy = "member") // order table에 있는 멤버 테이블에 의해 매핑이 된거야! 나는 매핑된 거울일 뿐이다! 여기 값을 넣어도 포린키 값이 변경되지 않는다.
    private List<Order> orders = new ArrayList<>();

}
