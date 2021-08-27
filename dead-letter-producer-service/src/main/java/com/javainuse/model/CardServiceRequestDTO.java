package com.javainuse.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor
@AllArgsConstructor
public class CardServiceRequestDTO {

	@JsonProperty("requestTypCd")
	private String requestTypCd;

	@JsonProperty("data")
	private CardServiceRequestDetail cardServiceRequestDetail;
	
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	static class CardServiceRequestDetail {
		
		private String persNbr;
		
		private String cardTypeCd;
		
		private String acctNbr;
		
		private String cardStyleCd;
		
		private String reqTypCd;
		
		private String email;
		
		private String phoneNbr;
		
		private String embossingName;
		
		private String cardToken;
		
	}
}
