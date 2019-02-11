package mypackage.dto;

import com.github.manosbatsis.scrudbeans.jpa.model.AbstractSystemUuidPersistableModel;
import lombok.Data;

@Data
public class OrderUpdateEmailDTO extends AbstractSystemUuidPersistableModel {

	private String id;

	private String email;

}
