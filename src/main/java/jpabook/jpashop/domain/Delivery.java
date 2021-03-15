package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Delivery {

    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

    @Embedded
    private Address address;

    // 이넘 타입은 반드시 이뉴머레이트 어노테이션이 필요
    @Enumerated(EnumType.STRING) // EnumType을 오디널, 스트링 넣을 수 있다. 디폴트는 오디널. 오디널은 1, 2, 3, 4 숫자로 들어간다.
    // 그러면 중간에 다른 상태가 들어가면 망한다. 그래서 오디널 절대 쓰면 안되고 꼭 스트링으로 쓰자.
    private DeliveryStatus status; // READY, COMP

}
