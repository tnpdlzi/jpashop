package jpabook.jpashop.repository.order.query;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

// api 안에 있는 DTO를 쓰지 않는 이유는 그렇게 되면 Repository가 controller를 참조하는 순환 관계가 되어버린다. 그리고 같은 패키지에 있는 게 좋다.
@Data
@EqualsAndHashCode(of = "orderId") // 묶어주는 것. 이렇게 해야 중복이 안 나타나게 할 수 있따 V6에서. 4, 11을 하나로 묶어주게 된다.
public class OrderQueryDto {

    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItemQueryDto> orderItems;

    public OrderQueryDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
        // 얘는 뺀다. new operation에서 컬렉션을 바로 넣을 순 없다.
//        this.orderItems = orderItems;
    }

    public OrderQueryDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address, List<OrderItemQueryDto> orderItems) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
        this.orderItems = orderItems;
    }
}
