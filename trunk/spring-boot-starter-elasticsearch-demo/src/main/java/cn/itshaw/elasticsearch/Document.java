package cn.itshaw.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Document {

	@Size(max = 50)
	private String title;
	@NotNull
	private String content;

	private String url;

}
