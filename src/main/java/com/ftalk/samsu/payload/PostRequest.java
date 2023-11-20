package com.ftalk.samsu.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {
	@NotBlank
	private String title;
	@NotBlank
	private String body;
	private Integer kudos ;
	@NotNull
	private Integer eventId;
	@NotBlank
	private String userRollnumber;
	private String image_urls ;
	private String file_urls ;
	private Short status ;

}
