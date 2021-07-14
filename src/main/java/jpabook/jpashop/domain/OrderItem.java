package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.jpashop.domain.item.Item;
import lombok.*;
import org.aspectj.weaver.ast.Or;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @JsonIgnore // 양방향 연관관계에서 한 쪽은 JsonIgnore를 해 주어야 무한 루프에 빠지지 않는다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; // 주문 가격
    private int count; // 주문 수량

//    // 외부에서 생성 방지 ---> 롬복으로 대체
//    protected OrderItem() {}

    // == 생성 메서드 == //
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count);
        return orderItem;
    }

    // == 비지니스 로직 == //
    public void cancel() {
        getItem().addStock(count); // 재고 수량 원복
    }

    // == 조회 로직 == //

    /**
     * 주문 상품 전체 가격 조회
     */
    // 주문 가격과 수량을 곱해야 함.
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
