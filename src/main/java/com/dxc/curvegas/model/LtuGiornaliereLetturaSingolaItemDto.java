package com.dxc.curvegas.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class LtuGiornaliereLetturaSingolaItemDto {
    public Date datLettura;
//    public String codTipVoceLtu;
    public String numMtcAppar;
    public String codTipLtuGio;
    public Integer quaLettura;
    public Date datLtuPrecedente;
    public BigDecimal quaLtuPrecedente;
    public String codFlgValida;
    public String codFlgRettificata;
    public String codTipoFonteLtuGio;
    public String codFlgQuadrata;
    public String codAnomalia;
    public Date datAcquisizioneLtu;
    public Date datPubblicazioneLtu;
    public Integer quaLtuPublic;
    public Date datCreazioneRec;
    public Date datUltAggRec;
    public String codOperatore;
    public String numMtcApparNew;
    public String codTipLtuGioNew;
    public Integer quaLetturaNew;
    public Integer quaLtuPrdNew;
    public Integer quaLtuScsNew;
    public String codTipoFonteLtuGioNew;
    public String codAnomaliaNew;
    public String codTipoStatoFinNew;
    public String codFlgRetPbl;
    public Date datForzatura;
    public String codFlgForzata;
}
