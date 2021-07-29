package jpabook.jpashop.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

import static jpabook.jpashop.domain.QMember.*;
import static jpabook.jpashop.domain.QOrder.*;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;
//    private final JPAQueryFactory query;
//
//    // query dsl 줄이기.
//    public OrderRepository(EntityManager em) {
//        this.em = em;
//        this.query = new JPAQueryFactory(em);
//    }

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    // findAll
    //        // 얘를 동적 쿼리로 바꿔야한다. Name이 없으면 다가져와!! 이런 식으로
//        return em.createQuery("select o from Order o join o.member m" +
//                        " where o.status = :status " +
//                        "and m.name like :name", Order.class)
//                .setParameter("status", orderSearch.getOrderStatus())
//                .setParameter("name", orderSearch.getMemberName())
////                .setFirstResult(100) // 페이징 하는 법. 100번째부터 검색
//                .setMaxResults(1000) // 결과를 1000개로 제한
//                .getResultList();

    public List<Order> findAllByString(OrderSearch orderSearch) {

        // 첫 번째 무식한 방법
        //language=JPAQL
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;
        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class) .setMaxResults(1000); //최대 1000건
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();
    }

    // JPA 표준 제공

    /**
     * JPA Criteria
     */
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        // 두 번째 방법. 근데 얘도 실무에서 안 쓴다.
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인
        List<Predicate> criteria = new ArrayList<>();
        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"),
                    orderSearch.getOrderStatus());
            criteria.add(status);
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" +
                            orderSearch.getMemberName() + "%");
            criteria.add(name);

        }
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대 1000건
        return query.getResultList();
    }

    // query dsl
    // 자바 코드라 오타 다 잡히고 코드 어시스턴스 다 된다.
    public List<Order> findAll(OrderSearch orderSearch) {

        // static import로 줄일 수 있따.
//        QOrder order = QOrder.order;
//        QMember member = QMember.member;

        // 얘도 위에 생성자 만들어서 줄일 수 있따.
        JPAQueryFactory query = new JPAQueryFactory(em);

        return query
                .select(order)
                .from(order)
                .join(order.member, member)
                .where(statusEq(orderSearch.getOrderStatus()), nameLike(orderSearch.getMemberName()))
//                .where(statusEq(orderSearch.getOrderStatus()), member.name.like(orderSearch.getMemberName())) 정적 쿼리
//                .where(order.status.eq(orderSearch.getOrderStatus())) 정적 쿼리.
                .limit(1000)
                .fetch();

    }

    // 동적 쿼리.
    private BooleanExpression nameLike(String memberName) {
        if (!StringUtils.hasText(memberName)) {
            return null;
        }
        return member.name.like(memberName);
    }

    // condition이 없으면 NULl 반환으로 where문 동작 안함.
    private BooleanExpression statusEq(OrderStatus statusCond) {
        if (statusCond == null) {
            return null;
        }
        return order.status.eq(statusCond);
    }


    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                // join fetch로 한 방에 가져오고 싶다!
                // select 절에서 한 방에 다 가져온다! 레이지 무시하고 값을 다 채워서 가져온다!
                // 이게 패치조인!
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class
        ).getResultList();
    }

    // 하나의 아이디에 같은게 두번 나가버린다. join이라서. 하지만 distinct로 중복 제거되고 그로 인해 쿼리가 한 번만 나간다.
    // 근데 이렇게 하면 페이징이 불가능. 일대다를 패치조인 하는 순간 페이징 불가능.
    public List<Order> findAllwithItem() {
        return em.createQuery(
                "select distinct o from Order o" + // distinct로 중복 제거. 근데 디비의 distinct는 한 줄이 전부 똑같아야 제거가 됨. 근데 그걸 JPA에서 자체적으로 오더를 가져올 때 오더가 같은 id값이면 중복을 제거해 준다.
                        " join fetch o.member m" +
                        " join fetch o.delivery d" +
                        // order와 items를 조인한다. 2개와 4개의 조인. order가 4개가 된 것. 조인하면 orderitem 개수만큼 맞춰서 늘어버린다. 중복된 데이터가 나와버림. 그래서 distinct를 넣으면 됨.
                        " join fetch o.orderItems oi" +
                        " join fetch oi.item", Order.class)
//                .setFirstResult(1)
//                .setMaxResults(100)
                // 이것들이 불가능하다. 페이징 불가능. 메모리에서 페이징처리 해버림. 잘못하면 out of memory 떠버린다. 그리고 우리가 원한 건 중복처리 된 걸 가져오고 싶은데 이게 디비 쿼리 입장에서는 조인된 거가 기준이라 원하는 데로 페이징이 안 된다. 일대다 에서는 패치조인 했을 때 패치조인 하면 안 된다.
                .getResultList();
        // 컬렉션 패치조인을 1개만 써야한다. 둘 이상에 페치조인 해 버리면 데이터가 부정확하게 나올 수 있음.
    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                // join fetch로 한 방에 가져오고 싶다!
                // select 절에서 한 방에 다 가져온다! 레이지 무시하고 값을 다 채워서 가져온다!
                // 이게 패치조인!
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }
}
