/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.usr.reportcommissario.ws.model;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvNumber;
import it.usr.reportcommissario.ws.helper.IntegerTransformer;
import it.usr.reportcommissario.ws.helper.OrdinanzaTransformer;
import it.usr.reportcommissario.ws.helper.SiNoBooleanSerializer;
import it.usr.reportcommissario.ws.helper.StringTransformer;
import it.usr.reportcommissario.ws.helper.Transform;
import java.math.BigDecimal;
import java.util.Date;
import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbTypeSerializer;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author riccardo.iovenitti
 */
@XmlRootElement
@JsonbNillable
public class Report {
    @CsvBindByPosition(position = 0)
    private String numeroFascicoloMUDE;
    
    @CsvBindByPosition(position = 1)
    @Transform(transformer = OrdinanzaTransformer.class)   
    private Integer ordinanza;
    
    @CsvBindByPosition(position = 2)
    @JsonbTypeSerializer(SiNoBooleanSerializer.class)
    private Boolean ordinanza100;
    
    @CsvBindByPosition(position = 3)
    @JsonbTypeSerializer(SiNoBooleanSerializer.class)
    private Boolean sorteggiataPerVerificaACampione;
    
    @CsvBindByPosition(position = 4)
    private String numeroProtocolloUSR;
    
    @CsvBindByPosition(position = 5)
    @JsonbDateFormat("yyyy/MM/dd")
    @CsvDate("dd/MM/yyyy")
    private Date dataProtocolloUSR;
    
    @CsvBindByPosition(position = 6)
    @Transform(transformer = StringTransformer.class)
    private String numeroFascicoloUSR;
    
    @CsvBindByPosition(position = 7)
    private String CFIntestatario;
    
    @CsvBindByPosition(position = 8)
    private String nomeCognomeIntestatario;
    
    @CsvBindByPosition(position = 9)
    private String titolaritaGiuridicaRichiedente;
    
    @CsvBindByPosition(position = 10)
    private String CFProfessionistaCapogruppo;
    
    @CsvBindByPosition(position = 11)
    private String nomeCognomeProfessionistaCapogruppo;
    
    @CsvBindByPosition(position = 12)
    private String CFoPivaImpresaAffidataria;
    
    @CsvBindByPosition(position = 13)
    private String ragioneSocialeImpresaAffidataria;
    
    @CsvBindByPosition(position = 14)
    private String codiceIstatProvincia;
    
    @CsvBindByPosition(position = 15)
    private String codiceIstatComune;
    
    @CsvBindByPosition(position = 16)
    private String indirizzo;
    
    @CsvBindByPosition(position = 17)
    //@Transform(transformer = IntegerTransformer.class) (was Integer)
    private String foglio;
    
    @CsvBindByPosition(position = 18)
    private String mappaleTerreni;
    
    @CsvBindByPosition(position = 19)
    private String destinazioneUsoPrevalente;
    
    @CsvBindByPosition(position = 20)
    private String livelloOperativo;
    
    @CsvBindByPosition(position = 21)
    private String tipologiaIntervento;
    
    @CsvBindByPosition(position = 22)
    @JsonbTypeSerializer(SiNoBooleanSerializer.class)
    private Boolean interventoAggregato;
    
    @CsvBindByPosition(position = 23)
    @Transform(transformer = IntegerTransformer.class)
    private Integer totUStrutturali;
    
    @CsvBindByPosition(position = 24)
    @Transform(transformer = IntegerTransformer.class)
    private Integer totUI;
    
    @CsvBindByPosition(position = 25)
    @Transform(transformer = IntegerTransformer.class)
    private Integer totUIPrincipaliOAttProdEse;
    
    @CsvBindByPosition(position = 26)
    @JsonbTypeSerializer(SiNoBooleanSerializer.class)
    private Boolean istanzaRigettataArchiviata;
    
    @CsvBindByPosition(position = 27)
    @JsonbDateFormat("yyyy/MM/dd")
    @CsvDate("dd/MM/yyyy")
    private Date dataRigettoArchiviazione;
    
    @CsvBindByPosition(position = 28)
    private String numeroDecretoContributo;
    
    @CsvBindByPosition(position = 29)
    @JsonbDateFormat("yyyy/MM/dd")
    @CsvDate("dd/MM/yyyy")
    private Date  dataDecretoContributo;
    
    @CsvBindByPosition(position = 30)
    private String CUP;
    
    @CsvBindByPosition(position = 31)
    @JsonbDateFormat("yyyy/MM/dd")
    @CsvDate("dd/MM/yyyy")
    private Date dataPresentazioneAnticipazioneSpeseTecniche;
    
    @CsvBindByPosition(position = 32)
    private String numeroDecretoAnticipazioneSpeseTecniche;
    
    @CsvBindByPosition(position = 33)
    @JsonbDateFormat("yyyy/MM/dd")
    @CsvDate("dd/MM/yyyy")
    private Date dataDecretoAnticipazioneSpeseTecniche;    
    
    @CsvBindByPosition(position = 34)
    @CsvNumber("#0,00")
    private BigDecimal importoAnticipazioneSpeseTecniche;
    
    @CsvBindByPosition(position = 35)
    @JsonbDateFormat("yyyy/MM/dd")
    @CsvDate("dd/MM/yyyy")
    private Date dataPresentazioneSAL0;
    
    @CsvBindByPosition(position = 36)
    private String numeroDecretoSAL0;
    
    @CsvBindByPosition(position = 37)
    @JsonbDateFormat("yyyy/MM/dd")
    @CsvDate("dd/MM/yyyy")
    private Date dataDecretoSAL0;
    
    @CsvBindByPosition(position = 38)
    @JsonbDateFormat("yyyy/MM/dd")
    @CsvDate("dd/MM/yyyy")
    private Date dataPresentazioneSAL20;
    
    @CsvBindByPosition(position = 39)
    private String numeroDecretoSAL20;
    
    @CsvBindByPosition(position = 40)
    @JsonbDateFormat("yyyy/MM/dd")    
    @CsvDate("dd/MM/yyyy")
    private Date dataDecretoSAL20;
    
    @CsvBindByPosition(position = 41)
    @JsonbDateFormat("yyyy/MM/dd")
    @CsvDate("dd/MM/yyyy")
    private Date dataPresentazioneSAL40;
    
    @CsvBindByPosition(position = 42)
    private String numeroDecretoSAL40;
    
    @CsvBindByPosition(position = 43)
    @JsonbDateFormat("yyyy/MM/dd")
    @CsvDate("dd/MM/yyyy")
    private Date dataDecretoSAL40;
    
    @CsvBindByPosition(position = 44)
    @JsonbDateFormat("yyyy/MM/dd")
    @CsvDate("dd/MM/yyyy")
    private Date dataPresentazioneSAL50;
    
    @CsvBindByPosition(position = 45)
    private String numeroDecretoSAL50;
    
    @CsvBindByPosition(position = 46)
    @JsonbDateFormat("yyyy/MM/dd")
    @CsvDate("dd/MM/yyyy")
    private Date dataDecretoSAL50;
    
    @CsvBindByPosition(position = 47)
    @JsonbDateFormat("yyyy/MM/dd")    
    @CsvDate("dd/MM/yyyy")
    private Date dataPresentazioneSAL70;
    
    @CsvBindByPosition(position = 48)
    private String numeroDecretoSAL70;
    
    @CsvBindByPosition(position = 49)
    @JsonbDateFormat("yyyy/MM/dd")
    @CsvDate("dd/MM/yyyy")
    private Date dataDecretoSAL70;
    
    @CsvBindByPosition(position = 50)
    @JsonbDateFormat("yyyy/MM/dd")
    @CsvDate("dd/MM/yyyy")
    private Date dataPresentazioneSALfinale;
    
    @CsvBindByPosition(position = 51)
    private String numeroDecretoSALfinale;
    
    @CsvBindByPosition(position = 52)
    @JsonbDateFormat("yyyy/MM/dd")
    @CsvDate("dd/MM/yyyy")
    private Date dataDecretoSALfinale;
        
    @CsvBindByPosition(position = 53)
    @CsvNumber("#0,00")
    private BigDecimal contributoConcesso;    
    
    @CsvBindByPosition(position = 54)
    @CsvNumber("#0,00")
    private BigDecimal contributoLiquidato;

    public String getNumeroFascicoloMUDE() {
        return numeroFascicoloMUDE;
    }

    public void setNumeroFascicoloMUDE(String numeroFascicoloMUDE) {
        this.numeroFascicoloMUDE = numeroFascicoloMUDE;
    }

    public Integer getOrdinanza() {
        return ordinanza;
    }

    public void setOrdinanza(Integer ordinanza) {
        this.ordinanza = ordinanza;
    }

    public Boolean getOrdinanza100() {
        return ordinanza100;
    }

    public void setOrdinanza100(Boolean ordinanza100) {
        this.ordinanza100 = ordinanza100;
    }

    public Boolean getSorteggiataPerVerificaACampione() {
        return sorteggiataPerVerificaACampione;
    }

    public void setSorteggiataPerVerificaACampione(Boolean sorteggiataPerVerificaACampione) {
        this.sorteggiataPerVerificaACampione = sorteggiataPerVerificaACampione;
    }

    public String getNumeroProtocolloUSR() {
        return numeroProtocolloUSR;
    }

    public void setNumeroProtocolloUSR(String numeroProtocolloUSR) {
        this.numeroProtocolloUSR = numeroProtocolloUSR;
    }

    public Date getDataProtocolloUSR() {
        return dataProtocolloUSR;
    }

    public void setDataProtocolloUSR(Date dataProtocolloUSR) {
        this.dataProtocolloUSR = dataProtocolloUSR;
    }

    public String getNumeroFascicoloUSR() {
        return numeroFascicoloUSR;
    }

    public void setNumeroFascicoloUSR(String numeroFascicoloUSR) {
        this.numeroFascicoloUSR = numeroFascicoloUSR;
    }

    public String getCFIntestatario() {
        return CFIntestatario;
    }

    public void setCFIntestatario(String CFIntestatario) {
        this.CFIntestatario = CFIntestatario;
    }

    public String getNomeCognomeIntestatario() {
        return nomeCognomeIntestatario;
    }

    public void setNomeCognomeIntestatario(String nomeCognomeIntestatario) {
        this.nomeCognomeIntestatario = nomeCognomeIntestatario;
    }

    public String getTitolaritaGiuridicaRichiedente() {
        return titolaritaGiuridicaRichiedente;
    }

    public void setTitolaritaGiuridicaRichiedente(String titolaritaGiuridicaRichiedente) {
        this.titolaritaGiuridicaRichiedente = titolaritaGiuridicaRichiedente;
    }

    public String getCFProfessionistaCapogruppo() {
        return CFProfessionistaCapogruppo;
    }

    public void setCFProfessionistaCapogruppo(String CFProfessionistaCapogruppo) {
        this.CFProfessionistaCapogruppo = CFProfessionistaCapogruppo;
    }

    public String getNomeCognomeProfessionistaCapogruppo() {
        return nomeCognomeProfessionistaCapogruppo;
    }

    public void setNomeCognomeProfessionistaCapogruppo(String nomeCognomeProfessionistaCapogruppo) {
        this.nomeCognomeProfessionistaCapogruppo = nomeCognomeProfessionistaCapogruppo;
    }

    public String getCFoPivaImpresaAffidataria() {
        return CFoPivaImpresaAffidataria;
    }

    public void setCFoPivaImpresaAffidataria(String CFoPivaImpresaAffidataria) {
        this.CFoPivaImpresaAffidataria = CFoPivaImpresaAffidataria;
    }

    public String getRagioneSocialeImpresaAffidataria() {
        return ragioneSocialeImpresaAffidataria;
    }

    public void setRagioneSocialeImpresaAffidataria(String ragioneSocialeImpresaAffidataria) {
        this.ragioneSocialeImpresaAffidataria = ragioneSocialeImpresaAffidataria;
    }

    public String getCodiceIstatProvincia() {
        return codiceIstatProvincia;
    }

    public void setCodiceIstatProvincia(String codiceIstatProvincia) {
        this.codiceIstatProvincia = codiceIstatProvincia;
    }

    public String getCodiceIstatComune() {
        return codiceIstatComune;
    }

    public void setCodiceIstatComune(String codiceIstatComune) {
        this.codiceIstatComune = codiceIstatComune;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getFoglio() {
        return foglio;
    }

    public void setFoglio(String foglio) {
        this.foglio = foglio;
    }

    public String getMappaleTerreni() {
        return mappaleTerreni;
    }

    public void setMappaleTerreni(String mappaleTerreni) {
        this.mappaleTerreni = mappaleTerreni;
    }

    public String getDestinazioneUsoPrevalente() {
        return destinazioneUsoPrevalente;
    }

    public void setDestinazioneUsoPrevalente(String destinazioneUsoPrevalente) {
        this.destinazioneUsoPrevalente = destinazioneUsoPrevalente;
    }

    public String getLivelloOperativo() {
        return livelloOperativo;
    }

    public void setLivelloOperativo(String livelloOperativo) {
        this.livelloOperativo = livelloOperativo;
    }

    public String getTipologiaIntervento() {
        return tipologiaIntervento;
    }

    public void setTipologiaIntervento(String tipologiaIntervento) {
        this.tipologiaIntervento = tipologiaIntervento;
    }

    public Boolean getInterventoAggregato() {
        return interventoAggregato;
    }

    public void setInterventoAggregato(Boolean interventoAggregato) {
        this.interventoAggregato = interventoAggregato;
    }

    public Integer getTotUStrutturali() {
        return totUStrutturali;
    }

    public void setTotUStrutturali(Integer totUStrutturali) {
        this.totUStrutturali = totUStrutturali;
    }

    public Integer getTotUI() {
        return totUI;
    }

    public void setTotUI(Integer totUI) {
        this.totUI = totUI;
    }

    public Integer getTotUIPrincipaliOAttProdEse() {
        return totUIPrincipaliOAttProdEse;
    }

    public void setTotUIPrincipaliOAttProdEse(Integer totUIPrincipaliOAttProdEse) {
        this.totUIPrincipaliOAttProdEse = totUIPrincipaliOAttProdEse;
    }

    public Boolean getIstanzaRigettataArchiviata() {
        return istanzaRigettataArchiviata;
    }

    public void setIstanzaRigettataArchiviata(Boolean istanzaRigettataArchiviata) {
        this.istanzaRigettataArchiviata = istanzaRigettataArchiviata;
    }

    public Date getDataRigettoArchiviazione() {
        return dataRigettoArchiviazione;
    }

    public void setDataRigettoArchiviazione(Date dataRigettoArchiviazione) {
        this.dataRigettoArchiviazione = dataRigettoArchiviazione;
        this.istanzaRigettataArchiviata = (dataRigettoArchiviazione!=null);
    }

    public String getNumeroDecretoContributo() {
        return numeroDecretoContributo;
    }

    public void setNumeroDecretoContributo(String numeroDecretoContributo) {
        this.numeroDecretoContributo = numeroDecretoContributo;
    }

    public Date getDataDecretoContributo() {
        return dataDecretoContributo;
    }

    public void setDataDecretoContributo(Date dataDecretoContributo) {
        this.dataDecretoContributo = dataDecretoContributo;
    }

    public String getCUP() {
        return CUP;
    }

    public void setCUP(String CUP) {
        this.CUP = CUP;
    }

    public Date getDataPresentazioneAnticipazioneSpeseTecniche() {
        return dataPresentazioneAnticipazioneSpeseTecniche;
    }

    public void setDataPresentazioneAnticipazioneSpeseTecniche(Date dataPresentazioneAnticipazioneSpeseTecniche) {
        this.dataPresentazioneAnticipazioneSpeseTecniche = dataPresentazioneAnticipazioneSpeseTecniche;
    }

    public String getNumeroDecretoAnticipazioneSpeseTecniche() {
        return numeroDecretoAnticipazioneSpeseTecniche;
    }

    public void setNumeroDecretoAnticipazioneSpeseTecniche(String numeroDecretoAnticipazioneSpeseTecniche) {
        this.numeroDecretoAnticipazioneSpeseTecniche = numeroDecretoAnticipazioneSpeseTecniche;
    }

    public Date getDataDecretoAnticipazioneSpeseTecniche() {
        return dataDecretoAnticipazioneSpeseTecniche;
    }

    public void setDataDecretoAnticipazioneSpeseTecniche(Date dataDecretoAnticipazioneSpeseTecniche) {
        this.dataDecretoAnticipazioneSpeseTecniche = dataDecretoAnticipazioneSpeseTecniche;
    }

    public BigDecimal getImportoAnticipazioneSpeseTecniche() {
        return importoAnticipazioneSpeseTecniche;
    }

    public void setImportoAnticipazioneSpeseTecniche(BigDecimal importoAnticipazioneSpeseTecniche) {
        this.importoAnticipazioneSpeseTecniche = importoAnticipazioneSpeseTecniche;
    }

    public Date getDataPresentazioneSAL0() {
        return dataPresentazioneSAL0;
    }

    public void setDataPresentazioneSAL0(Date dataPresentazioneSAL0) {
        this.dataPresentazioneSAL0 = dataPresentazioneSAL0;
    }

    public String getNumeroDecretoSAL0() {
        return numeroDecretoSAL0;
    }

    public void setNumeroDecretoSAL0(String numeroDecretoSAL0) {
        this.numeroDecretoSAL0 = numeroDecretoSAL0;
    }

    public Date getDataDecretoSAL0() {
        return dataDecretoSAL0;
    }

    public void setDataDecretoSAL0(Date dataDecretoSAL0) {
        this.dataDecretoSAL0 = dataDecretoSAL0;
    }

    public Date getDataPresentazioneSAL20() {
        return dataPresentazioneSAL20;
    }

    public void setDataPresentazioneSAL20(Date dataPresentazioneSAL20) {
        this.dataPresentazioneSAL20 = dataPresentazioneSAL20;
    }

    public String getNumeroDecretoSAL20() {
        return numeroDecretoSAL20;
    }

    public void setNumeroDecretoSAL20(String numeroDecretoSAL20) {
        this.numeroDecretoSAL20 = numeroDecretoSAL20;
    }

    public Date getDataDecretoSAL20() {
        return dataDecretoSAL20;
    }

    public void setDataDecretoSAL20(Date dataDecretoSAL20) {
        this.dataDecretoSAL20 = dataDecretoSAL20;
    }

    public Date getDataPresentazioneSAL40() {
        return dataPresentazioneSAL40;
    }

    public void setDataPresentazioneSAL40(Date dataPresentazioneSAL40) {
        this.dataPresentazioneSAL40 = dataPresentazioneSAL40;
    }

    public String getNumeroDecretoSAL40() {
        return numeroDecretoSAL40;
    }

    public void setNumeroDecretoSAL40(String numeroDecretoSAL40) {
        this.numeroDecretoSAL40 = numeroDecretoSAL40;
    }

    public Date getDataDecretoSAL40() {
        return dataDecretoSAL40;
    }

    public void setDataDecretoSAL40(Date dataDecretoSAL40) {
        this.dataDecretoSAL40 = dataDecretoSAL40;
    }

    public Date getDataPresentazioneSAL50() {
        return dataPresentazioneSAL50;
    }

    public void setDataPresentazioneSAL50(Date dataPresentazioneSAL50) {
        this.dataPresentazioneSAL50 = dataPresentazioneSAL50;
    }

    public String getNumeroDecretoSAL50() {
        return numeroDecretoSAL50;
    }

    public void setNumeroDecretoSAL50(String numeroDecretoSAL50) {
        this.numeroDecretoSAL50 = numeroDecretoSAL50;
    }

    public Date getDataDecretoSAL50() {
        return dataDecretoSAL50;
    }

    public void setDataDecretoSAL50(Date dataDecretoSAL50) {
        this.dataDecretoSAL50 = dataDecretoSAL50;
    }

    public Date getDataPresentazioneSAL70() {
        return dataPresentazioneSAL70;
    }

    public void setDataPresentazioneSAL70(Date dataPresentazioneSAL70) {
        this.dataPresentazioneSAL70 = dataPresentazioneSAL70;
    }

    public String getNumeroDecretoSAL70() {
        return numeroDecretoSAL70;
    }

    public void setNumeroDecretoSAL70(String numeroDecretoSAL70) {
        this.numeroDecretoSAL70 = numeroDecretoSAL70;
    }

    public Date getDataDecretoSAL70() {
        return dataDecretoSAL70;
    }

    public void setDataDecretoSAL70(Date dataDecretoSAL70) {
        this.dataDecretoSAL70 = dataDecretoSAL70;
    }

    public Date getDataPresentazioneSALfinale() {
        return dataPresentazioneSALfinale;
    }

    public void setDataPresentazioneSALfinale(Date dataPresentazioneSALfinale) {
        this.dataPresentazioneSALfinale = dataPresentazioneSALfinale;
    }

    public String getNumeroDecretoSALfinale() {
        return numeroDecretoSALfinale;
    }

    public void setNumeroDecretoSALfinale(String numeroDecretoSALfinale) {
        this.numeroDecretoSALfinale = numeroDecretoSALfinale;
    }

    public Date getDataDecretoSALfinale() {
        return dataDecretoSALfinale;
    }

    public void setDataDecretoSALfinale(Date dataDecretoSALfinale) {
        this.dataDecretoSALfinale = dataDecretoSALfinale;
    }

    public BigDecimal getContributoConcesso() {
        return contributoConcesso;
    }

    public void setContributoConcesso(BigDecimal contributoConcesso) {
        this.contributoConcesso = contributoConcesso;
    }

    public BigDecimal getContributoLiquidato() {
        return contributoLiquidato;
    }

    public void setContributoLiquidato(BigDecimal contributoLiquidato) {
        this.contributoLiquidato = contributoLiquidato;
    }        
}
