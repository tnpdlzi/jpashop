package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // new를 통한 생성 방지. 생성 메서드를 쓰도록.
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    // 연관관계의 주인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

//    @BatchSize(size = 1000) // 이렇게 따로 적용할 수 있다. 컬렉션일 경우. 근데 그냥 프로퍼티에서 정의해 주자.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; // 주문 시간 // java8 에서는 하이버네이트가 알아서 지원해준다.

    // 이넘 타입은 반드시 이뉴머레이트 어노테이션이 필요
    @Enumerated(EnumType.STRING) // EnumType을 오디널, 스트링 넣을 수 있다. 디폴트는 오디널. 오디널은 1, 2, 3, 4 숫자로 들어간다.
    // 그러면 중간에 다른 상태가 들어가면 망한다. 그래서 오디널 절대 쓰면 안되고 꼭 스트링으로 쓰자.
    private OrderStatus status; // 주문 상태 [ORDER, CANCEL]


    // == 연관관계 편의 메서드 ==//
    public void setMember(Member member) {
        this.member = member;

        // 오더에서 멤버 셋만 해도  오더 셋도 되게 해버린다. 원래는 양쪽에 다 입력을 해 주어야 하는데.
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        // 넘어온 orderItem에게도 자신을 set 해 준다.
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        // delivery에도 오더를 세팅해 준다.
        delivery.setOrder(this);
    }

    // == 생성 메서드 == //
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) { // 여러개 넘기기 가능
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    // == 비지니스 로직 == //
    /**
     * 주문 취소
     */
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송이 완료된 상품은 취소가 불가능합니다.");
        }

        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    // == 조회 로직 == //

    /**
     * 전체 주문 가격 조회
     */
//    public int getTotalPrice() {
//        int totalPrice = 0;
//        for (OrderItem orderItem : orderItems) {
//            totalPrice += orderItem.getTotalPrice();
//        }
//        return totalPrice;
//    }
    public int getTotalPrice() {
        return orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum();
    }

}
