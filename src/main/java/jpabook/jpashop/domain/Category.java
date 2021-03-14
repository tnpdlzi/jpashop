package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    // 카테고리도 리스트로 아이템을 가지고 아이템도 리스트로 카테고리를 가지는 다대 다 관계이다.

    @ManyToMany
    @JoinTable(name = "category_item",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id") // item쪽으로 들어가는 거
    ) // 중간 테이블 매핑. 정형화된 모습으로 매핑 가능. 근데 실무에서는 쓰면 안 됨. 테이블에 뭔가 더 추가가 안되기 때문
    private List<Item> items = new ArrayList<>();

    // 카테고리 구조를 어떡하지? 부모, 자식을 쭉 볼 수 있어야 하는데?
    @ManyToOne // 내 부모! 내 부모니까 매니 투 원이다. 부모 하나니까
    @JoinColumn(name = "parent_id")
    private Category parent;

    // 내 자식은?
    @OneToMany(mappedBy = "parent") // 셀프로 양방향 연관관계를 걸었다. 이름만 내꺼지 그냥 다른 애랑 맺는거랑 똑같이 하면 된다.
    private List<Category> child = new ArrayList<>();

}
