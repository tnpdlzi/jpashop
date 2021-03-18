package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {

        // 엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송 정보 생성
        // 원래는 deliveryRepository 필요한데 없어도 됨. order의 cascade 옵션 때문
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        // 주문 상품 생성
        // 원래는 orderItemRepository 필요한데 없어도 됨. order의 cascade 옵션 때문
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장
        // order가 persist 될 때 딜리버리, 오더 아이템도 cascade로 같이 해줌.
        // order가 딜리버리, 오더 아이템을 관리 하니까 cascade 해 준다. 얘네는 order만 쓰니까. 라이프 사이클에 대해 동일하게 관리 하니까. 다른 것이 참조할 수 없는 private인 경우
        orderRepository.save(order);

        return order.getId();
    }


    // 취소
    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        // 주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        // 주문 취소
        order.cancel(); // 엔티티 안에서 데이터만 바꿔주면 jpa가 바뀐 변경 포인트들을 감지해서 디비에 업데이트 쿼리가 날라간다.
    }

    // 검색
    /**
     * 주문 검색
     */
//    public List<Order> findOrders(OrderSearch orderSearch) {
//        return orderRepository.findAll(orderSearch);
//    }

}
