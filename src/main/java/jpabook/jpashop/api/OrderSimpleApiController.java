package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    // 엔티티 그냥 반환
    // 이러면 Order에 있는 Member를 뿌려야하는데, 이 멤버를 가면 멤버는 오더스가 또 있다. 그럼 또 오더스를 가보면 멤범가 있다 --> 무한루프가 됨. 양방향 연관관계 문제가 생긴다. 한 쪽은 JsonIgnore를 해주어야한다.
    // 두번째 문제가 발생. type definition error가 생긴다. order를 가져온다. 멤버 패치가 LAZY로 되어있다. 지연로딩이기 때문에 멤버 객체를 안 가져오고 (디비에서 안 가져오고) 프록시 멤버를 넣어놓는다. (바이트버디 인터셉트가 대신 들어가있는다.)
    // 지연로딩인 경우에는 뿌리지 말라고 모듈 설치를 해 주어야 한다. 애플리케이션에서 HIBErnate5module을 빈을 만들어주어야 한다. 근데 엔티티를 외부에 노출하면 안 되기때문에 이렇게 하지 말자.
    // 게다가 성능상에도 문제가 발생한다.
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch()); // 주문을 다 들고오게 된다.
        return all;
    }
}
