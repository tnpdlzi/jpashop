package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("B") // 디비에 들어갈 때 구분하기 위한 이름. 안 쓰면 기본은 클래스 이름이 들어간다.
@Getter
@Setter
public class Book extends Item {

    private String author;
    private String isbn;
}
