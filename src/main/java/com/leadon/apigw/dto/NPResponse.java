package com.leadon.apigw.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(callSuper = false)
public class NPResponse extends AbstractDto {

	private static final long serialVersionUID = 5520256083157206236L;

	private String type;
	private String message;
	private String duplicated;

	@Override
	public String toString() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			NPResponse npResponse = new NPResponse(type, message, duplicated);
			return mapper.writeValueAsString(npResponse);
		} catch (JsonProcessingException e) {
		}
		return "NPResponse [type=" + type + ", message=" + message + ", duplicated=" + duplicated + "]";
	}

}
