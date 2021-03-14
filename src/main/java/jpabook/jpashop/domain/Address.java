package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

// 값 타입은 이뮤터블 해야한다. 게터만 제공하고 세터는 아예 제공을 안해야한다.
@Embeddable
@Getter
public class Address {

    private String city;
    private String street;
    private String zipcode;

    // jpa에서 기술 스택을 사용 할 때 기본 생성자가 필요하다. 그래서 기본 생성자를 만들어줘야 하는데 접근 지정자를 protected까지 허용해 준다.
    protected Address() {
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
