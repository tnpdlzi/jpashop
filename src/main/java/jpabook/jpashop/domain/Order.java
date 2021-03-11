package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    // 연관관계의 주인
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; // 주문 시간 // java8 에서는 하이버네이트가 알아서 지원해준다.

    // 이넘 타입은 반드시 이뉴머레이트 어노테이션이 필요
    @Enumerated(EnumType.STRING) // EnumType을 오디널, 스트링 넣을 수 있다. 디폴트는 오디널. 오디널은 1, 2, 3, 4 숫자로 들어간다.
    // 그러면 중간에 다른 상태가 들어가면 망한다. 그래서 오디널 절대 쓰면 안되고 꼭 스트링으로 쓰자.
    private OrderStatus status; // 주문 상태 [ORDER, CANCEL]
}
