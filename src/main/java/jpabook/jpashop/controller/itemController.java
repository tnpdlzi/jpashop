package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class itemController {

    private final ItemService itemService;

    @GetMapping("/items/new")
    private String createForm(Model model) {
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    private String create(BookForm form) {
        Book book = new Book();
        // 원래는 이렇게 set말고 createBook 이런거 book에 넣어서 setter 없이 하는 설계가 더 좋은 설계이다.
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setAuthor(form.getAuthor());
        book.setStockQuantity(form.getStockQuantity());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/";
    }

    @GetMapping("/items")
    public String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }


    @GetMapping("/items/{itemId}/edit") // itemId는 넘어오는 값
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) { // 위에 itemId에 PathVariable 넘겨주는 값
        Book item = (Book) itemService.findOne(itemId); // 예제 단순화를 위해 캐스팅. 원래는 안 하는게 좋다.

        BookForm form = new BookForm(); // 업데이트 하는데 북 엔티티를 보내는 게 아닌 북 폼을 보내는 것이다.
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());
        form.setStockQuantity(item.getStockQuantity());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    // user가 이 itemId에 대한 권힌이 있는지 없는지 체크하는 로직이 필요하다.
    @PostMapping("/items/{itemId}/edit") // itemId는 넘어오는 값
    public String updateItem(@PathVariable Long itemId, @ModelAttribute("form") BookForm form) { // 위에 itemId에 PathVariable 넘겨주는 값

        // 이런식으로 엔티티(Book)을 어설프게 만들어서 넘기면 안 된다. 이거는 merge 사용법
        // 이렇게 하지 말고 변경 감지를 사용해야 한다. 영속성 entity를 불러와서 하는 것이 좋다.

//        // 준영속 엔티티는 영속성 컨텍스트가 더 이상 관리하지 않는 애다.
//        // 얘는 id가 있다. JPA로 디비에 한 번 다녀온 애다. 그런 애를 준 영속 엔티티라고 한다. JPA가 식별할 수 있는 아이디를 가지고 있기 때문.
//        Book book = new Book();
//        // 이제 폼을 북으로 바꾼다.
//        // option * 2 한 후 복사, 그리고 shift + Command + U로 대문자 바꾸기로 쉽게 하자!
//        book.setId(form.getId());
//        book.setName(form.getName());
//        book.setPrice(form.getPrice());
//        book.setStockQuantity(form.getStockQuantity());
//        book.setAuthor(form.getAuthor());
//        book.setIsbn(form.getIsbn());
//
//        itemService.saveItem(book);

        // 그럼 어케 해야하나?
        // 훨씬 깔끔하면서도 변경 감지를 사용한다.
        // 필요한 데이터만 딱딱 받은 것이다.
        // 업데이트 할 게 많으면 서비스 계층에 DTO를 하나 만들어라.
        itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity());

        return "redirect:/items";
    }
}
