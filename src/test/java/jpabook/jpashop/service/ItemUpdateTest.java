package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemUpdateTest {

    @Autowired
    EntityManager em;

    @Test
    public void updateTest() {
        Book book = em.find(Book.class, 1L);

        // Transaction 안에서
        book.setName("test");

        // Transaction commit
        // 하면 위의 것에 대해 변경분을 찾아 자동으로 업데이트 한다.
        // --> 이게 더티 체킹. 이것이 변경 감지.
    }
}
