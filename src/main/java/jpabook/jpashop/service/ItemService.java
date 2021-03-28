package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity) {
        Item findItem = itemRepository.findOne(itemId);// item을 일단 찾는다. 이러면 아이디를 기반으로 실제 영속상태에 있는 아이템을 찾아 왔다.
//        findItem.change(name, price, stockQuantity);
        // 이런 식으로 의미있는 함수를 만들어서 넣어야지 아래처럼 set을 막 깔면 안 된다. 이렇게 해야 변경 지점이 entity로 다 간다.
        findItem.setName(name); // 영속상태에 있는 애를 가져왔으니 param에서 가져와서 넣으면 된다.
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantity);
//        itemRepository.save(findItem); // 이제 얘가 필요가 없다. merge를 호출할 필요가 없다.
        // 찾아온 findItem이 영속상태이기 때문.
        // 스프링의 @Transactional에 의해 트랜잭션이 commit이 된다. 그럼 JPA는 flush를 날린다. 그럼 영속성 엔티티 중에 변경된 애를 다 찾는다. 그래서 바뀐 애를 찾아서 바뀐걸 디비에 날려서 변경해 버린다.
        // 이게 변경 감지에 의해 데이터를 변경하는 방법.
//        return findItem; // <-- 이게 들어가면 이제 merge 방식이 되는거.
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}
