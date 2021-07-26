package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();

            //기존 것에서 아래 두 줄이 추가.
            List<OrderItem> orderItems = order.getOrderItems();
            // orderItem 프록시도 강제 초기화, 이름도 초기화. 객체 그래프의 초기화.
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }

    // 쿼리가 엄청 많이 나간다. DTO로 변환해서 Entity 노출은 없긴 하지만. 더 최적화 필요.
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        // orders를 OrderDto로 변환
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    // 코드는 같은데 쿼리가 한 번 나가도록 튜닝 됨.
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllwithItem();
        // orders를 OrderDto로 변환
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit
            ) {
        // order랑 member만 ToOne 관계인 애들만 페치조인. 그럼 페이징 가능.
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);
        // orderItems가 필요.
        // orders를 OrderDto로 변환 프록시 초기화 되면서 orderItems 가져온다. 그리고 각 아이템스의 아이템도 가져온다. 엄청난 쿼리량.
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }



//    @Data 이거 쓴는 것도 좋다.
    @Getter
    static class OrderDto {

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
                    .collect(Collectors.toList());
        }

    }

    // orderItem에서 노출하고 싶은 것만 나타내자.
    @Getter
    static class OrderItemDto {

        private String itemName; //상풍 명
        private int orderPrice; // 주문 가격
        private int count; // 주문 수량

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }

}
