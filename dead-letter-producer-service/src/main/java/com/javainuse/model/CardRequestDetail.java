package com.javainuse.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardRequestDetail {

    @JsonProperty("REQNBR")
    private long REQNBR;

    @JsonProperty("REQTRACEID")
    private String REQTRACEID;

    @JsonProperty("FIRSTNAME")
    private String FIRSTNAME;

    @JsonProperty("LASTNAME")
    private String LASTNAME;

    @JsonProperty("MIDDLENAME")
    private String MIDDLENAME;

    @JsonProperty("CTNBR")
    private long CTNBR;

    @JsonProperty("CARDCD")
    private String CARDCD;

    @JsonProperty("CARDDESC")
    private String CARDDESC;

    @JsonProperty("PERSNBR")
    private long PERSNBR;

    @JsonProperty("NHANXUNG")
    private String NHANXUNG;

    @JsonProperty("EMBOSSINGNAME")
    private String EMBOSSINGNAME;

    @JsonProperty("NGAYSINH")
    private String NGAYSINH;

    @JsonProperty("GIOITINH")
    private String GIOITINH;

    @JsonProperty("NHANVIEN")
    private String NHANVIEN;

    @JsonProperty("NOIGUITB")
    private String NOIGUITB;

    @JsonProperty("CNNHANTHE")
    private long CNNHANTHE;

    @JsonProperty("SONHA")
    private String SONHA;

    @JsonProperty("DUONG")
    private String DUONG;

    @JsonProperty("TP")
    private String TP;

    @JsonProperty("TELL")
    private String TELL;

    @JsonProperty("EMAIL")
    private String EMAIL;

    @JsonProperty("FEECD")
    private String FEECD;

    @JsonProperty("PTNCD")
    private String PTNCD;

    @JsonProperty("PGNCD")
    private String PGNCD;

    @JsonProperty("PEMVCD")
    private String PEMVCD;

    @JsonProperty("EFFDATE")
    private String EFFDATE;

    @JsonProperty("ORGBRANCHNBR")
    private long ORGBRANCHNBR;

    @JsonProperty("CMND")
    private String CMND;

    @JsonProperty("QUOCTICH")
    private String QUOCTICH;

    @JsonProperty("HOLDACCTNBR")
    private long HOLDACCTNBR;

    @JsonProperty("ACCHOLDCD")
    private String ACCHOLDCD;

    @JsonProperty("HOLDSEQNBR")
    private long HOLDSEQNBR;

    @JsonProperty("HOLDAMT")
    private long HOLDAMT;

    @JsonProperty("FULLNAME")
    private String FULLNAME;

}
