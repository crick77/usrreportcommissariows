package it.usr.reportcommissario.ws;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import it.usr.reportcommissario.ws.model.Report;
import it.usr.reportcommissario.ws.model.Status;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author riccardo.iovenitti
 */
@Stateless
@Path("v1/data")
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Produces(MediaType.APPLICATION_JSON)
public class ReportCommissarioV1 {    
    public final static String HEADER_FIELDS = "numeroFascicoloMUDE;ordinanza;ordinanza100;sorteggiataPerVerificaACampione;numeroProtocolloUSR;dataProtocolloUSR;numeroFascicoloUSR;CFIntestatario;nomeCognomeIntestatario;titolaritaGiuridicaRichiedente;CFProfessionistaCapogruppo;nomeCognomeProfessionistaCapogruppo;"
            + "CFoPivaImpresaAffidataria;ragioneSocialeImpresaAffidataria;codiceIstatProvincia;codiceIstatComune;indirizzo;foglio;mappaleTerreni;destinazioneUsoPrevalente;livelloOperativo;tipologiaIntervento;interventoAggregato;totUStrutturali;totUI;totUIPrincipaliOAttProdEse;istanzaRigettataArchiviata;"
            + "dataRigettoArchiviazione;numeroDecretoContributo;dataDecretoContributo;CUP;dataPresentazioneAnticipazioneSpeseTecniche;numeroDecretoAnticipazioneSpeseTecniche;dataDecretoAnticipazioneSpeseTecniche;importoAnticipazioneSpeseTecniche;dataPresentazioneSAL0;numeroDecretoSAL0;dataDecretoSAL0;"
            + "dataPresentazioneSAL20;numeroDecretoSAL20;dataDecretoSAL20;dataPresentazioneSAL40;numeroDecretoSAL40;dataDecretoSAL40;dataPresentazioneSAL50;numeroDecretoSAL50;dataDecretoSAL50;dataPresentazioneSAL70;numeroDecretoSAL70;dataDecretoSAL70;dataPresentazioneSALfinale;numeroDecretoSALfinale;"
            + "dataDecretoSALfinale;contributoConcesso;contributoLiquidato";
    @Resource(lookup = "jdbc/decreti")
    DataSource dsDecreti;
    @Resource(lookup = "jms/reportGenerator")
    Queue generator;
    @Resource
    ConnectionFactory ctx;
    
    @GET
    @Path(value = "generate")
    @Consumes(value = MediaType.WILDCARD)    
    public Response generateReportData() {
        Status s = getStatus();
                        
        if (s.isCompleted()) {                            
            if (s.getError() != null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(s).build();
            }
        } else {
            return Response.ok(s).build();
        }                        
    
        try(javax.jms.Connection con = ctx.createConnection();
            Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer md = session.createProducer(generator);) {
            
            TextMessage m = session.createTextMessage();
            m.setText("GO!");            
            md.send(m);
            
            System.out.println("Process submitted. Returning.");
            return Response.ok().build();
        }
        catch(JMSException je) {
            return Response.serverError().entity(je).build();
        }                   
    }

    /**
     *
     * @return
     */
    @GET
    public Response getReport() { 
        Status s = getStatus();
        if(!s.isCompleted()) {
            return Response.status(Response.Status.CONFLICT).entity(s).build();
        }
        else {
            if(s.getError()!=null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(s).build();
            }
        }
        
        try {
            return Response.ok(getAllReports()).build();
        }
        catch(SQLException sqle) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(sqle).build();
        }
    }

    /**
     *
     * @return
     */
    @GET
    @Path("status")
    public Status getStatus() {
        try (Connection decretiCon = dsDecreti.getConnection()) {
            
            String sql = "SELECT step, total, dt, completed, error FROM reportcommissariowork WHERE (id = 1)";
            try (PreparedStatement psCheck = decretiCon.prepareStatement(sql)) {
                try (ResultSet rsCheck = psCheck.executeQuery()) {
                    if (rsCheck.next()) {                        
                        Status s = new Status();
                        s.setCurrent(rsCheck.getInt(1));
                        s.setTotal(rsCheck.getInt(2));
                        s.setDt(rsCheck.getTimestamp(3).toLocalDateTime());
                        s.setCompleted(rsCheck.getInt(4) == 1);
                        s.setError(rsCheck.getString(5));
                                                
                        return s;
                    }
                    else {
                        throw new RuntimeException("No status");
                    }
                }
            }                                 
        } catch (SQLException sqle) {
            throw new RuntimeException(sqle);
        }
    }     
    
    @GET
    @Path("csv")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getCSV() {
        Status s = getStatus();
        if(!s.isCompleted()) {
            return Response.status(Response.Status.CONFLICT).entity(s).build();
        }
        else {
            if(s.getError()!=null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(s).build();
            }
        }
        
        List<Report> lRep;
        try {            
            lRep = getAllReports();
        }
        catch(SQLException sqle) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(sqle).build();
        }
        
        try(StringWriter sw = new StringWriter()) {
            StatefulBeanToCsv<Report> beanToCsv = new StatefulBeanToCsvBuilder<Report>(sw).withSeparator(';').withQuotechar('\u0000').build();             
            beanToCsv.write(lRep);
            String out = sw.toString();
            out = out.replaceAll("true", "SI").replaceAll("false", "NO");
            out = HEADER_FIELDS+"\n"+out;
            String fileName = "RP_ABR_"+new SimpleDateFormat("dd_MM_yyyy").format(s.getDt())+".csv";
            return Response.ok(out).header("Content-Disposition", "attachment; filename=\"" + fileName + "\"").build();
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex).build();
        }
    }
    
    private List<Report> getAllReports() throws SQLException {
        try (Connection decretiCon = dsDecreti.getConnection()) {
            String sql = "SELECT numerofascicolomude, ordinanza, ordinanza100, sorteggiatoperverificaacampione, numeroprotocollousr, dataprotocollousr, numerofascicolousr, cfintestatario, nomecognomeintestatario, titolaritagiuridicarichiedente, cfprofessionistacapogruppo, "
                    + "nomecognomeprofessionistacapogruppo, cfopivaimpresaaffidataria, ragionesocialeimpresaaffidataria, codiceistatprovincia, codiceistatcomune, indirizzo, foglio, mappaleterreni, destinazioneusoprevalente, livellooperativo, tipologiaintervento, interventoaggregato, totustrutturali, "
                    + "totui, totuiprincipalioattprodese, istanzarigettataarchiviata, datarigettoarchiviazione, numerodecretocontributo, datadecretocontributo, cup, datapresentazioneanticipazionespesetecniche, numerodecretoanticipazionespesetecniche, datadecretoanticipazionespesetecniche, "
                    + "importoanticipazionespesetecniche, datapresentazionesal0, numerodecretosal0, datadecretosal0, datapresentazionesal20, numerodecretosal20, datadecretosal20, datapresentazionesal40, numerodecretosal40, datadecretosal40, datapresentazionesal50, numerodecretosal50, datadecretosal50, "
                    + "datapresentazionesal70, numerodecretosal70, datadecretosal70, datapresentazionesalfinale, numerodecretosalfinale, datadecretosalfinale, contributoconcesso, contributoliquidato FROM reportcommissario";
            try (PreparedStatement psRep = decretiCon.prepareStatement(sql)) {
                try (ResultSet rsRep = psRep.executeQuery()) {
                    List<Report> lRep = new ArrayList<>();
                    Report r;
                    while (rsRep.next()) {
                        r = new Report();
                        int idx = 1;
                        
                        r.setNumeroFascicoloMUDE(rsRep.getString(idx++));
                        r.setOrdinanza(rsRep.getObject(idx++, Integer.class));
                        r.setOrdinanza100(rsRep.getObject(idx++, Boolean.class));
                        r.setSorteggiataPerVerificaACampione(rsRep.getObject(idx++, Boolean.class));
                        r.setNumeroProtocolloUSR(rsRep.getString(idx++));
                        r.setDataProtocolloUSR(rsRep.getDate(idx++));
                        r.setNumeroFascicoloUSR(rsRep.getString(idx++));
                        r.setCFIntestatario(rsRep.getString(idx++));
                        r.setNomeCognomeIntestatario(rsRep.getString(idx++));
                        r.setTitolaritaGiuridicaRichiedente(rsRep.getString(idx++));
                        r.setCFProfessionistaCapogruppo(rsRep.getString(idx++));
                        r.setNomeCognomeProfessionistaCapogruppo(rsRep.getString(idx++));
                        r.setCFoPivaImpresaAffidataria(rsRep.getString(idx++));
                        r.setRagioneSocialeImpresaAffidataria(rsRep.getString(idx++));
                        r.setCodiceIstatProvincia(rsRep.getString(idx++));
                        r.setCodiceIstatComune(rsRep.getString(idx++));
                        r.setIndirizzo(rsRep.getString(idx++));
                        r.setFoglio(rsRep.getObject(idx++, Integer.class));
                        r.setMappaleTerreni(rsRep.getString(idx++));
                        r.setDestinazioneUsoPrevalente(rsRep.getString(idx++));
                        r.setLivelloOperativo(rsRep.getString(idx++));
                        r.setTipologiaIntervento(rsRep.getString(idx++));
                        r.setInterventoAggregato(rsRep.getObject(idx++, Boolean.class));
                        r.setTotUStrutturali(rsRep.getObject(idx++, Integer.class));
                        r.setTotUI(rsRep.getObject(idx++, Integer.class));
                        r.setTotUIPrincipaliOAttProdEse(rsRep.getObject(idx++, Integer.class));
                        r.setIstanzaRigettataArchiviata(rsRep.getObject(idx++, Boolean.class));
                        r.setDataRigettoArchiviazione(rsRep.getDate(idx++));
                        r.setNumeroDecretoContributo(rsRep.getString(idx++));
                        r.setDataDecretoContributo(rsRep.getDate(idx++));
                        r.setCUP(rsRep.getString(idx++));
                        r.setDataPresentazioneAnticipazioneSpeseTecniche(rsRep.getDate(idx++));
                        r.setNumeroDecretoAnticipazioneSpeseTecniche(rsRep.getString(idx++));
                        r.setDataDecretoAnticipazioneSpeseTecniche(rsRep.getDate(idx++));
                        r.setImportoAnticipazioneSpeseTecniche(rsRep.getObject(idx++, BigDecimal.class));
                        
                        r.setDataPresentazioneSAL0(rsRep.getDate(idx++));
                        r.setNumeroDecretoSAL0(rsRep.getString(idx++));
                        r.setDataDecretoSAL0(rsRep.getDate(idx++));
                        
                        r.setDataPresentazioneSAL20(rsRep.getDate(idx++));
                        r.setNumeroDecretoSAL20(rsRep.getString(idx++));
                        r.setDataDecretoSAL20(rsRep.getDate(idx++));
                        
                        r.setDataPresentazioneSAL40(rsRep.getDate(idx++));
                        r.setNumeroDecretoSAL40(rsRep.getString(idx++));
                        r.setDataDecretoSAL40(rsRep.getDate(idx++));
                        
                        r.setDataPresentazioneSAL50(rsRep.getDate(idx++));
                        r.setNumeroDecretoSAL50(rsRep.getString(idx++));
                        r.setDataDecretoSAL50(rsRep.getDate(idx++));
                        
                        r.setDataPresentazioneSAL70(rsRep.getDate(idx++));
                        r.setNumeroDecretoSAL70(rsRep.getString(idx++));
                        r.setDataDecretoSAL70(rsRep.getDate(idx++));
                        
                        r.setDataPresentazioneSALfinale(rsRep.getDate(idx++));
                        r.setNumeroDecretoSALfinale(rsRep.getString(idx++));
                        r.setDataDecretoSALfinale(rsRep.getDate(idx++));
                        
                        r.setContributoConcesso(rsRep.getObject(idx++, BigDecimal.class));
                        r.setContributoLiquidato(rsRep.getObject(idx++, BigDecimal.class));
                        
                        lRep.add(r);
                    }
                    
                    return lRep;
                }
            }            
        } catch (SQLException sqle) {
            throw sqle;
        }        
    } 
    
    @Schedule(hour = "2", minute = "0", dayOfWeek = "*", persistent = false)
    public void automaticGeneration() {
        System.out.println("Automatic generation on "+new Date());
        generateReportData();
    }
}
