package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * xToOne(ManyToOne, OneToOne)
 * Order
 * Order -> Member
 * Order -> Delivery
 */
// Order랑 Member는 ManyToOne이다. ToOne 관계.
// Order랑 Delivery도 oneToOne이다. 근데 아이템스는? OneToMany이다. 이건 복잡해져서 다음장.
// 이번에는 ToOne에서 성틍 최적화이다.
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    // 엔티티 그냥 반환
    // 이러면 Order에 있는 Member를 뿌려야하는데, 이 멤버를 가면 멤버는 오더스가 또 있다. 그럼 또 오더스를 가보면 멤범가 있다 --> 무한루프가 됨. 양방향 연관관계 문제가 생긴다. 한 쪽은 JsonIgnore를 해주어야한다.
    // 두번째 문제가 발생. type definition error가 생긴다. order를 가져온다. 멤버 패치가 LAZY로 되어있다. 지연로딩이기 때문에 멤버 객체를 안 가져오고 (디비에서 안 가져오고) 프록시 멤버를 넣어놓는다. (바이트버디 인터셉트가 대신 들어가있는다.)
    // 지연로딩인 경우에는 뿌리지 말라고 모듈 설치를 해 주어야 한다. 애플리케이션에서 HIBErnate5module을 빈을 만들어주어야 한다. 근데 엔티티를 외부에 노출하면 안 되기때문에 이렇게 하지 말자.
    // 게다가 성능상에도 문제가 발생한다.
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch()); // 주문을 다 들고오게 된다.
        for (Order order : all) {
            order.getMember().getName(); // 여기까지는 프록시 객체. 여기서 getName()을 하면 실제 객체를 가져와야해서 LAZY 강제 초기화.
            order.getDelivery().getAddress(); // 마찬가지.
        }
        return all;
    }

    // 엔티티가 아닌 DTO 반환
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() { // 원래는 리스트로 반환인 아닌 Result로 한 번 감싸야 한다.
        // ORDER 2개
        // N + 1 -> 1 + N 문제.
        // 첫 번째 쿼리가 나간다. 1(오더 개수 가져오기) 1 + N(2) Order 2개 2
        // 1 + 회원 N + 배송 N --> 5번 실행됨.
        // 즉시 로딩으로 바꾸면 되지 않냐? 아니다. 이상한 쿼리가 나간다.
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    // fetch join 최적화
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    // 바로 DTO로 조회하기
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() { // 컨트롤러가 리포지토리 의존하도록 변경.
        return orderSimpleQueryRepository.findOrderDtos();
    }

    // v3는 오더를 가져와서 내가 원하는 것만 SELECT한 것. 외부를 건드리지 않고 내부의 내가 원하는 것만 패치조인으로 튜닝 가능
    // v4는 쿼리를 한 번 할 때 query를 가져와서 그냥 해버린것. 재사용성이 떨어진다. 엔티티 변경이 불가능.
    // v3를 쓰는 걸 추천. v4는 계층이 깨진다. 성능 차이도 그렇게까지 크진 않다.

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) { // DTO가 엔티티를 파라미터로 받는 건 큰 문제가 되지 않는다. 중요하지 않은게 중요한 걸 의존하는 거기 때문.
            orderId = order.getId();
            name = order.getMember().getName(); // LAZY 초기화. 멤버 아이디로 영속성 컨텍스트를 찾음. 없으면 디비에서 긁어옴.
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // LAZY 초기화. 멤버 아이디로 영속성 컨텍스트를 찾음. 없으면 디비에서 긁어옴.
        }
    }

}
