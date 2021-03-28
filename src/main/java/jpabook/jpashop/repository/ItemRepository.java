package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {
        if (item.getId() == null) { // item은 저장하기 전에는 id 값이 없다.
            em.persist(item);
        } else {
            // 실무에서는 이렇게 merge 쓰는 것 보다 updateItem같이 변경 감지를 쓰는 것이 훨씬 좋다. 이건 웬만하면 쓰지 말자.
            em.merge(item); // 이게 병합 방법. 준영속성 엔티티를 영속성 엔티티로 변경시키는 방법.
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }

}
