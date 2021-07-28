package jpabook.jpashop.service.query;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
public class OrderDto {

    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;
    // DTO로 반환할 땐 Entity가 노출되면 안 된다. 이렇게 아래처럼 Dto를 감싸는 걸로는 안 된다. 완전히 DTO로 바꿔야 한다. 이렇게 하면 Entity가 바뀌면 다 바껴버린다.
//    private List<OrderItem> orderItems;
    // orderItem 매핑
    private List<OrderItemDto> orderItems;

    public OrderDto(Order order) {
        orderId = order.getId();
        name = order.getMember().getName();
        orderDate = order.getOrderDate();
        orderStatus = order.getStatus();
        address = order.getDelivery().getAddress();
        // 엔티티라 그냥하면 얘는 안 나옴. 그래서 스트림으로 한 번 더 돌리자. 프록시 초기화 후 돌리기.
        // DTO로 반환할 땐 Entity가 노출되면 안 된다. 이렇게 아래처럼 Dto를 감싸는 걸로는 안 된다. 완전히 DTO로 바꿔야 한다. 이렇게 하면 Entity가 바뀌면 다 바껴버린다.
//            order.getOrderItems().stream().forEach(o -> o.getItem().getName());
//            orderItems = order.getOrderItems();
        orderItems = order.getOrderItems().stream()
                .map(orderItem -> new OrderItemDto(orderItem))
                .collect(toList());
    }
}
