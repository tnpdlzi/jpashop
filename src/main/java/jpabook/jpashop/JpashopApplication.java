package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpashopApplication.class, args);
	}

//	// 사실 이건 할 필요 없다. 엔티티를 외부에 노출하면 안 되기 때문.
//	@Bean
//	Hibernate5Module hibernate5Module() {
//		return new Hibernate5Module();
//	}
}
