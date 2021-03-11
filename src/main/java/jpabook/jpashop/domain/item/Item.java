package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
// 상속관계 전략을 부모 테이블에 잡아주어야 한다. 우리는 싱글 테이블 전략을 쓴다.
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // singletable, table_per_class, joined 가 있다. joined는 가장 정구화된 스타일. singletable은 한 테이블에 다 때려박는거 table_per_class는 자식걸로만 테이블을 나누는 전략
@Getter @Setter
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

}
