package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.Collection;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	CommandLineRunner runner (ReservationRepository rr) {
		return strings -> {
			Arrays.asList("Josep,Pepe,Manel,Maria,Carme,Mireia".split(","))
					.forEach(n -> rr.save(new Reservation(n)));
			rr.findAll().forEach(System.out::println);

			rr.findByReservationName("Pepe").forEach(System.out::println);
		};
	}
}

/*
@RestController
class ReservationRestController {
	private final ReservationRepository reservationRepository;

	public ReservationRestController(ReservationRepository reservationRepository) {
		this.reservationRepository = reservationRepository;
	}

	@RequestMapping("/reservations")
	public Collection<Reservation> reservations() {
		return reservationRepository.findAll();
	}
}

*/

@Controller
class ReservationMvcController {
	private final ReservationRepository reservationRepository;

	public ReservationMvcController(ReservationRepository reservationRepository) {
		this.reservationRepository = reservationRepository;
	}

	@RequestMapping("reservations.web")
	String reservations(Model model) {
		model.addAttribute("reservations", reservationRepository.findAll());
		return "reservations";
	}
}

@Component
class ReservationResourceProcessor implements ResourceProcessor<Resource<Reservation>> {
	@Override
	public Resource<Reservation> process(Resource<Reservation> reservationResource) {
		reservationResource.add(new Link("http://s3.com/imgs/" +
				reservationResource.getContent().getId() + ".jpg", "profile-photo"));

		return reservationResource;
	}
}

@RepositoryRestResource
interface ReservationRepository extends JpaRepository<Reservation,Long> {
	Collection<Reservation> findByReservationName(@Param("rn") String rn);
}


@Entity
class Reservation {

	@Id
	@GeneratedValue
	private Long id;
	private String reservationName;

	public Reservation(String reservationName) {
		this.reservationName = reservationName;
	}

	public Reservation() {
	}

	public String getReservationName() {
		return reservationName;
	}

	public void setReservationName(String reservationName) {
		this.reservationName = reservationName;
	}

	public Long getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Reservation{" + "id=" + id +
				", reservationName='" + reservationName + "'}";
	}

}