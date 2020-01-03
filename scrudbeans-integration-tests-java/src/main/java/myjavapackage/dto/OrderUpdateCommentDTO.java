package myjavapackage.dto;

import com.github.manosbatsis.scrudbeans.model.AbstractSystemUuidPersistableModel;
import lombok.Data;

@Data
public class OrderUpdateCommentDTO extends AbstractSystemUuidPersistableModel {

	private String id;

	private String comment;

}
