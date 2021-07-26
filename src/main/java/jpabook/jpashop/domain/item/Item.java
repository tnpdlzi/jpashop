package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//@BatchSize(size = 100) // 컬렉션이 아닐 경우 여기에 이렇게 적어준다. 특정하게 값을 정하고 싶을 때.
@Entity
// 상속관계 전략을 부모 테이블에 잡아주어야 한다. 우리는 싱글 테이블 전략을 쓴다.
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // singletable, table_per_class, joined 가 있다. joined는 가장 정구화된 스타일. singletable은 한 테이블에 다 때려박는거 table_per_class는 자식걸로만 테이블을 나누는 전략
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    // 단방향 해도 되는데 최대한 복잡한 예제를 위해 양방향 다대다로

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    // == 비즈니스 로직 == //
    // 도메인 주도 설계 할 때 엔티티 내에서 처리할 수 있는건 엔티티 안에 비즈니스 로직을 넣는 것이 좋다.
    // 데이터를 가지고 있는 데서 비즈니스 로직이 나가는 것이 가장 응집도가 있다.

    /**
     * stock 증가
     */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    /**
     * stock 감소
     */
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}
