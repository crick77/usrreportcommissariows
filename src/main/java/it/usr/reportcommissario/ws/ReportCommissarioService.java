/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.usr.reportcommissario.ws;

import it.usr.reportcommissario.ws.helper.ReflectiveSetterHelper;
import it.usr.reportcommissario.ws.model.Contributo;
import it.usr.reportcommissario.ws.model.Decreto;
import it.usr.reportcommissario.ws.model.Report;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.sql.DataSource;

/**
 *
 * @author riccardo.iovenitti
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup",
            propertyValue = "jms/reportGenerator"),
    @ActivationConfigProperty(propertyName = "destinationType",
            propertyValue = "javax.jms.Queue")
})
@TransactionManagement(TransactionManagementType.BEAN)
public class ReportCommissarioService implements MessageListener {
    //public final static int[] RIGETTO = { 11, 18 }; 
    public final static int[] RIGETTO = { 11, 18, 20 }; // MODIFICA 11/11/2022 - VEDI SOTTO
    public final static int[] CONTRIBUTO = { 1 };
    public final static int[] ORDINANZA_RIFERIMENTO = { 1, 3, 4, 37, 38, 39 };
    public final static int BATCH_SIZE = 100;    
    public final static double EPSILON = 0.00001;
    @Resource(lookup = "jdbc/decreti")
    DataSource dsDecreti;
    @Resource(lookup = "jdbc/pigreco")
    DataSource dsPigreco;  
    
    @Override
    public void onMessage(Message msg) {          
        try(Connection pigrecoCon = dsPigreco.getConnection();
            Connection decretiCon = dsDecreti.getConnection()) {
            
            decretiCon.setAutoCommit(false);
                                                                        
            /*String sql = "SELECT "+
                    "  COUNT(im.ID) AS total " +
                    "FROM " +
                    "	tbl_IstanzaMUDE im " +
                    "JOIN " +
                    "	(SELECT _im.IDPratica, MIN(_im.IstanzaMudeData) AS IstanzaMudeData FROM tbl_IstanzaMUDE _im GROUP BY _im.IDPratica) AS im2 ON (im.IDPratica = im2.IDPratica) AND (im.IstanzaMudeData = im2.IstanzaMudeData) " +
                    "JOIN " +
                    "	tbl_IstanzaMUDEProtocolli imp ON im.IstanzaMudeNr = imp.IDIstanzaMUDE " +
                    "JOIN " +
                    "	cbo_IstanzaTipologia it ON im.SpeciePratica = it.IdSpeciePraticaIdSpeciePratica " +
                    "JOIN " +
                    "	MDMComuni com ON UPPER(im.IstanzaComune) = UPPER(com.Nome) " +
                    "JOIN " +
                    "    tbl_Istanza ist ON im.IstanzaMudeNr = ist.IstanzaMudeNr " +
                    "LEFT JOIN " +
                    "    cbo_LivelliOperativi lo ON lo.idLivelloOperativo = ist.IdLivelloOperativo " +
                    "WHERE " +
                    "	im.IDPratica > 0 " +
                    "AND " +
                    "	(im.IstanzaMUDEDiriferimento IS NULL OR im.IstanzaMUDEDiriferimento = '') " +
                    "AND " +
                    "	it.Sequenza = 'Prima' ";
            int totalRecords = 0;
            try(PreparedStatement ps = pigrecoCon.prepareStatement(sql)) {
                try(ResultSet rs = ps.executeQuery()) {
                    if(rs.next()) {
                        totalRecords = rs.getInt(1);
                    }
                }
            }
            
            if(totalRecords==0) {
                return;
            }*/
            
            // svuota temporanea di lavori
            int tot;
            String sql = "DELETE FROM tbl_istanzaMUDECommissario";
            try(PreparedStatement ps = pigrecoCon.prepareStatement(sql)) {
                tot = ps.executeUpdate();            
            }
            // conferma
            pigrecoCon.commit();
            log("Eliminati da [tbl_istanzaMUDECommissario] "+tot+" record.");
            
            // ripopola
            sql = "insert into tbl_IstanzaMUDECommissario select * from View_IstanzeCommissario";
            try(PreparedStatement ps = pigrecoCon.prepareStatement(sql)) {
                tot = ps.executeUpdate();
            }
            // salva tutto
            pigrecoCon.commit();
            log("Inseriti in [tbl_istanzaMUDECommissario] da [View_IstanzeCommissario] "+tot+" record.");
            
            sql = "SELECT" +
"                    	im.IstanzaMudeNr AS NumeroFascicoloMUDE, " +
"                    	it.Ordinanza AS Ordinanza, " +
"                    	imp.Numeroprotocollo AS NumeroProtocolloUSR, " +
"                    	imp.dataProtocollo AS DataProtocolloUSR, " +
"                    	im.IDPratica AS NumeroFascicoloUSR, " +
"                    	ist.IntestatarioCF AS CFIntestatario, " +
"                    	im.IntestatarioNomeCognome AS NomeCognomeIntestatario, " +
"                    	ist.ProfessionistaCF AS CFProfessionistaCapogruppo, " +
"                    	im.Professionista AS NomeCognomeProfessionistaCapogruppo, " +
"                    	CASE com.Provincia " +
"                    		WHEN 'AQ' THEN '066' " +
"                    		WHEN 'CH' THEN '069' " +
"                    		WHEN 'PE' THEN '068' " +
"                    		ELSE '067' " +
"                    	END AS CodiceIstatProvincia, " +
"                    	com.Istat AS CodiceIstatComune, " +
"                    	ist.UbicazioneEdificio AS Indirizzo, " +
"                    	ist.Foglio AS Foglio, " +
"                    	ist.Mappale AS MappaleTerreni, " +
"                    	it.DestinazioneUso AS DestinazioneUsoPrevalente, " +
"                    	lo.LivelloOperativo AS LivelloOperativo, " +
"                    	NULLIF(ist.NumeroUnitaImmobiliare, '') AS TotUI, " +
"                    	NULLIF(ist.NumeroAbitazioniPrincipali, '') AS TotUIPrincipaliOAttProdEse " +
"                    FROM " +
"                    	tbl_istanzaMUDECommissario im " +
"                    LEFT JOIN " +
"                    	(SELECT _im.IDPratica, MIN(_im.IstanzaMudeData) AS IstanzaMudeData FROM tbl_IstanzaMUDE _im GROUP BY _im.IDPratica) AS im2 ON (im.IDPratica = im2.IDPratica) AND (im.IstanzaMudeData = im2.IstanzaMudeData) " +
"                    LEFT JOIN " +
"			(SELECT min(IDIstanzaMUDE) AS IDIstanzaMUDE, min(Numeroprotocollo) AS Numeroprotocollo, min(dataProtocollo) AS dataProtocollo from tbl_IstanzaMUDEProtocolli group by IDIstanzaMUDE) AS imp ON im.IstanzaMudeNr = imp.IDIstanzaMUDE " +
"                    LEFT JOIN " +
"                    	cbo_IstanzaTipologia it ON im.SpeciePratica = it.IdSpeciePraticaIdSpeciePratica " +
"                    LEFT JOIN " +
"                    	MDMComuni com ON UPPER(im.IstanzaComune) = UPPER(com.Nome) " +
"                    LEFT JOIN " +
"                        tbl_Istanza ist ON im.IstanzaMudeNr = ist.IstanzaMudeNr " +
"                    LEFT JOIN " +
"                        cbo_LivelliOperativi lo ON lo.idLivelloOperativo = ist.IdLivelloOperativo" +
"		     WHERE" +
"                    	im.IDPratica > 0" +
//"                    AND " +
//"                    	(im.IstanzaMUDEDiriferimento IS NULL OR im.IstanzaMUDEDiriferimento = '') " +
"                    AND " +
"                    	it.Sequenza = 'Prima'" +
"                    ORDER BY " +
"                    	im.IDPratica, im.IstanzaMudeData";
                         
            List<Report> lRep = new ArrayList<>();
            try(PreparedStatement ps = pigrecoCon.prepareStatement(sql)) {
                try(ResultSet rs = ps.executeQuery()) {
                    ResultSetMetaData rsMD = rs.getMetaData();
                    ReflectiveSetterHelper<Report> rse = new ReflectiveSetterHelper<>(Report.class);
                    
                    while(rs.next()) {
                        Report r = new Report();
                        
                        for(int i=1;i<=rsMD.getColumnCount();i++) {
                            rse.setValue(r, rsMD.getColumnName(i), rsMD.getColumnClassName(i), rs.getObject(i));
                        }
                        
                        lRep.add(r);
                        
                        // Decommentare per effettuare prove più rapide
                        //if(lRep.size()==500) break;
                    } 
                }
            }
            
            if(lRep.isEmpty()) {
                log("WARNING: No record to compute. Quitting.");
                return;
            }
            
            log("There are "+lRep.size()+" record to compute.");
            
            try(PreparedStatement ps = decretiCon.prepareStatement("DELETE FROM reportcommissario")) {
                ps.executeUpdate();
            }            
            try(PreparedStatement ps = decretiCon.prepareStatement("UPDATE reportcommissariowork SET step = 0, total = ?, completed = 0, error = null, dt = now() WHERE id = 1")) {
                ps.setInt(1,  lRep.size());
                ps.executeUpdate();                
            }
            decretiCon.commit();
            log("Table cleared and work reset.");
            
            sql = "SELECT idpratica FROM usrestrazioni.pratica";
            Map<Integer, Integer> estrazioni = new HashMap<>();            
            try(PreparedStatement ps = decretiCon.prepareStatement(sql)) {
                try(ResultSet rs = ps.executeQuery()) {
                    while(rs.next()) {
                        int idPratica = rs.getInt(1);
                        estrazioni.put(idPratica, idPratica);
                    }
                }
            }
            log("Estrazioni loaded: "+estrazioni.size()+" records found.");
            
            sql = "SELECT "+
                    "  id_pratica, " +
                    "  id_ordinanza_riferimento, " +
                    "  id_tipo_decreto, " +
                    "  data_ora_provvedimento, " +
                    "  numero_provvedimento, " +
                    "  ord_100," + 
                    "  cup " +
                    "FROM " +
                    "  decreti AS d " +
                    "WHERE " +
                    "  id_ordinanza_riferimento IN (1,3,4,37,38,39) " +
                    "AND " +
                    "  da_rendicontare = 1 " +
                    "AND " +
                    "  numero_provvedimento IS NOT NULL " +
                    "AND " +
                    "  id_tipo_provvedimento > 0 " +
                    "AND " +
                    //"  id_tipo_decreto in (1,11,18) " +  
                    "  id_tipo_decreto in (1,11,18,20) " +  // MODIFICA 11/11/2022 - VEDI SOTTO
                    "AND " +
                    "  id_pratica IS NOT NULL AND id_pratica > 0 "+
                    "ORDER BY " +
                    //"  id_pratica ASC, data_atto DESC";
                    "  id_pratica ASC, data_atto ASC";     // MODIFICA 11/11/2022 - VEDI SOTTO
            
            Map<Integer, List<Decreto>> decreti = new HashMap<>();
            try(PreparedStatement ps = decretiCon.prepareStatement(sql)) {
                try(ResultSet rs = ps.executeQuery()) {
                    while(rs.next()) {                        
                        int idPratica = rs.getInt("id_pratica"); 
                        if(!rs.wasNull()) {   
                            Decreto d = new Decreto();
                            
                            d.setIdPratica(idPratica);
                            d.setIdOrdinanzaRiferimento(rs.getInt("id_ordinanza_riferimento"));
                            d.setIdTipoDecreto(rs.getInt("id_tipo_decreto"));
                            d.setDataOraProvvedimento(rs.getDate("data_ora_provvedimento"));                        
                            d.setNumeroProvvedimento(rs.getInt("numero_provvedimento"));
                            if(idPratica>=2144) {
                                d.setOrd100(true);
                            }
                            else {
                                int v = rs.getInt("ord_100");
                                v = rs.wasNull() ? 0 : v;
                                d.setOrd100(v==-1);                                
                            }
                            d.setCup(rs.getString("cup"));

                            if(!decreti.containsKey(idPratica)) {
                                decreti.put(d.getIdPratica(), new ArrayList<>());
                            }
                            
                            List<Decreto> lDec = decreti.get(idPratica);                        
                            lDec.add(d);
                        }                        
                    }
                }
            }
            log("Decreti loaded: "+decreti.size()+" unique records found.");
               
            sql = "SELECT DISTINCT id_pratica, contributo_concesso, contributo_liquidato FROM gis_dettaglio";
            Map<Integer, Contributo> contributi = new HashMap<>();
            try(PreparedStatement ps = decretiCon.prepareStatement(sql)) {
                try(ResultSet rs = ps.executeQuery()) {
                    while(rs.next()) {
                        Contributo c = new Contributo();
                        c.setIdPratica(rs.getInt(1));
                        c.setContributoConcesso(rs.getBigDecimal(2));
                        c.setContributoLiquidato(rs.getBigDecimal(3));

                        contributi.put(c.getIdPratica(), c);
                    }
                }
            }
            log("Contributi loaded: "+contributi.size()+" unique records found.");
            
            String anticipazioneSal0FinaleSql = "(SELECT data_ora_provvedimento, numero_provvedimento, importo_contributo, 'ANTIC' AS tipo FROM decreti WHERE id_ordinanza_riferimento IN (15) AND id_tipo_decreto IN (2) AND da_rendicontare = 1 AND numero_provvedimento IS NOT NULL AND id_tipo_provvedimento > 0 AND id_pratica = ? LIMIT 1) "
                    + "UNION "
                    + "(SELECT data_ora_provvedimento, numero_provvedimento, importo_contributo, 'SAL0' AS tipo FROM decreti WHERE id_ordinanza_riferimento IN (1,3,4,37,38,39) AND id_tipo_decreto IN (21) AND da_rendicontare = 1 AND numero_provvedimento IS NOT NULL AND id_tipo_provvedimento > 0 AND id_pratica = ? LIMIT 1) "
                    + "UNION "
                    + "(SELECT data_ora_provvedimento, numero_provvedimento, importo_contributo, 'FINALE' AS tipo FROM decreti WHERE id_ordinanza_riferimento IN (1,3,4,37,38,39) AND id_tipo_decreto IN (19) AND da_rendicontare = 1 AND numero_provvedimento IS NOT NULL AND id_tipo_provvedimento > 0 AND id_pratica = ? LIMIT 1) " 
                    + "UNION "
                    + "(SELECT data_ora_provvedimento, numero_provvedimento, importo_contributo, 'LIEVI50' AS tipo FROM decreti WHERE id_ordinanza_riferimento IN (1,37) AND id_tipo_decreto IN (2) AND da_rendicontare = 1 AND numero_provvedimento IS NOT NULL AND id_tipo_provvedimento > 0 AND id_pratica = ? LIMIT 1)";
            String dataPresentazioneSql =   "SELECT * FROM (" +
                    "    SELECT TOP 1 im.IstanzaMudeData, 'FINALE' AS tipo FROM tbl_IstanzaMUDE im WHERE im.IDPratica = ? AND im.SpeciePratica IN ('SPE00CI129', 'SPE00CI130', 'SPE00CI155', 'SPE00CI156', 'SPE00CI171', 'SPE00CI172', 'SPE00CI180') ORDER BY im.IstanzaMudeData" +
                    "  UNION ALL " +
                    "    SELECT TOP 1 im.IstanzaMudeData, 'SAL70' AS tipo FROM tbl_IstanzaMUDE im WHERE im.IDPratica = ? AND im.SpeciePratica IN ('SPE00CI154', 'SPE00CI170') ORDER BY im.IstanzaMudeData" +
                    "  UNION ALL " +
                    "    SELECT TOP 1 im.IstanzaMudeData, 'SAL40' AS tipo FROM tbl_IstanzaMUDE im WHERE im.IDPratica = ? AND im.SpeciePratica IN ('SPE00CI153', 'SPE00CI169') ORDER BY im.IstanzaMudeData" +
                    "  UNION ALL " +
                    "    SELECT TOP 1 im.IstanzaMudeData, 'SAL20' AS tipo FROM tbl_IstanzaMUDE im WHERE im.IDPratica = ? AND im.SpeciePratica IN ('SPE00CI152', 'SPE00CI168') ORDER BY im.IstanzaMudeData" +
                    "  UNION ALL " +
                    "    SELECT TOP 1 im.IstanzaMudeData, 'SAL50' AS tipo FROM tbl_IstanzaMUDE im WHERE im.IDPratica = ? AND im.SpeciePratica IN ('SPE00CI128') ORDER BY im.IstanzaMudeData" +
                    "  UNION ALL " +
                    "    SELECT TOP 1 im.IstanzaMudeData, 'SAL0' AS tipo FROM tbl_IstanzaMUDE im WHERE im.IDPratica = ? AND im.SpeciePratica IN ('SPE00CI127', 'SPE00CI151', 'SPE00CI167') ORDER BY im.IstanzaMudeData" +
                    ") AS t";
            String dataProvvedimentiSql = "SELECT data_ora_provvedimento, numero_provvedimento FROM decreti WHERE id_ordinanza_riferimento in (3,4,38,39) AND id_tipo_decreto IN (2) AND da_rendicontare=1 AND numero_provvedimento IS NOT NULL AND id_tipo_provvedimento>0 AND id_pratica = ? ORDER BY data_ora_provvedimento DESC";
            String dataProvvedimento50Sql = "SELECT data_ora_provvedimento, numero_provvedimento from decreti WHERE id_ordinanza_riferimento IN (1,37) AND id_tipo_decreto IN (2) AND da_rendicontare=1 AND numero_provvedimento IS NOT NULL AND id_tipo_provvedimento>0 AND id_pratica = ?";
            String reportCommissarioSql = "INSERT INTO reportcommissario (numerofascicolomude, ordinanza, ordinanza100, sorteggiatoperverificaacampione, numeroprotocollousr, dataprotocollousr, numerofascicolousr, cfintestatario, nomecognomeintestatario, titolaritagiuridicarichiedente, cfprofessionistacapogruppo, "
                    + "nomecognomeprofessionistacapogruppo, cfopivaimpresaaffidataria, ragionesocialeimpresaaffidataria, codiceistatprovincia, codiceistatcomune, indirizzo, foglio, mappaleterreni, destinazioneusoprevalente, livellooperativo, tipologiaintervento, interventoaggregato, totustrutturali, "
                    + "totui, totuiprincipalioattprodese, istanzarigettataarchiviata, datarigettoarchiviazione, numerodecretocontributo, datadecretocontributo, cup, datapresentazioneanticipazionespesetecniche, numerodecretoanticipazionespesetecniche, datadecretoanticipazionespesetecniche, "
                    + "importoanticipazionespesetecniche, datapresentazionesal0, numerodecretosal0, datadecretosal0, datapresentazionesal20, numerodecretosal20, datadecretosal20, datapresentazionesal40, numerodecretosal40, datadecretosal40, datapresentazionesal50, numerodecretosal50, datadecretosal50, "
                    + "datapresentazionesal70, numerodecretosal70, datadecretosal70, datapresentazionesalfinale, numerodecretosalfinale, datadecretosalfinale, contributoconcesso, contributoliquidato) "
                    + "VALUES " 
                    + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            String reportoCommissarioWorkSql = "UPDATE reportcommissariowork SET step = ?, completed = ?, error = ? WHERE (id = 1)";
            try(PreparedStatement psAnticipazioneSal0Finale = decretiCon.prepareStatement(anticipazioneSal0FinaleSql);
                    PreparedStatement psDataPresentazione = pigrecoCon.prepareStatement(dataPresentazioneSql);
                    PreparedStatement psReportCommissario = decretiCon.prepareStatement(reportCommissarioSql);
                    PreparedStatement psReportCommissarioWork = decretiCon.prepareStatement(reportoCommissarioWorkSql);
                    PreparedStatement psDataProvvedimenti = decretiCon.prepareStatement(dataProvvedimentiSql);
                    PreparedStatement psDataProvvedimento50 = decretiCon.prepareStatement(dataProvvedimento50Sql)) {
                
                int rowCount = 0;
                for(Report r : lRep) {
                    int idPratica = Integer.parseInt(r.getNumeroFascicoloUSR());
                    List<Decreto> lD = decreti.get(idPratica);
                    if(lD!=null && !lD.isEmpty()) {
                        /**
                         * 
                         * MODIFICA DEL 11/11/2022 PER ALLINEARE I VALORI DI CONTRIBUTO/REVOCA AL REPORT COMPLESSIVO PER IL COMMISSARIO (inviato da LUCA).                         
                         * 
                         */
                        
                        /*Decreto d = lD.get(0);
                        if(isOrdinanzaRiferimento(d.getIdOrdinanzaRiferimento())) {
                            if(isContributo(d.getIdTipoDecreto())) {
                                int idx = 0;
                                while(idx<lD.size() && lD.get(idx).getIdTipoDecreto()==d.getIdTipoDecreto() ) idx++; // forse va invertita partendo dall'ultimo e arrivando al primo

                                Decreto dContrib = lD.get(idx-1);
                                r.setDataDecretoContributo(dContrib.getDataOraProvvedimento());
                                r.setNumeroDecretoContributo(String.valueOf(dContrib.getNumeroProvvedimento()));
                                r.setCUP(dContrib.getCup());
                            }
                            else { 
                                if(isRigetto(d.getIdTipoDecreto())) {
                                    r.setDataRigettoArchiviazione(d.getDataOraProvvedimento());
                                }
                            }
                        }*/
                        
                        // Cerco un contributo (il più vecchio che trovo)
                        boolean praticaAContributo = false;
                        for(Decreto d : lD) {
                            if(isOrdinanzaRiferimento(d.getIdOrdinanzaRiferimento()) && isContributo(d.getIdTipoDecreto())) {
                                r.setDataDecretoContributo(d.getDataOraProvvedimento());
                                r.setNumeroDecretoContributo(String.valueOf(d.getNumeroProvvedimento()));
                                r.setCUP(d.getCup());
                                r.setOrdinanza100(d.isOrd100());
                                
                                praticaAContributo = true;
                                break;
                            }
                        }
                        
                        // Nessun contributo? 
                        // Allora cerca un rigetto, archiviazione o revoca (il più vecchio che trovo)
                        if(!praticaAContributo) {
                            for(Decreto d : lD) {
                                if(isOrdinanzaRiferimento(d.getIdOrdinanzaRiferimento()) && isRigetto(d.getIdTipoDecreto())) {
                                    r.setDataRigettoArchiviazione(d.getDataOraProvvedimento());
                                    r.setOrdinanza100(d.isOrd100());
                                    
                                    break;
                                }
                            }
                        }

                        // Imposta verifica da estrazioni
                        r.setSorteggiataPerVerificaACampione(estrazioni.get(idPratica)!=null);                        
                        
                        psAnticipazioneSal0Finale.clearParameters();
                        for(int i=0;i<4;i++) psAnticipazioneSal0Finale.setInt(i+1, idPratica);                        
                        try(ResultSet rs = psAnticipazioneSal0Finale.executeQuery()) {
                            while(rs.next()) {
                                switch(rs.getString("tipo")) {
                                    case "ANTIC": {
                                        r.setDataDecretoAnticipazioneSpeseTecniche(rs.getDate("data_ora_provvedimento"));
                                        r.setNumeroDecretoAnticipazioneSpeseTecniche(rs.getString("numero_provvedimento"));
                                        r.setImportoAnticipazioneSpeseTecniche(rs.getBigDecimal("importo_contributo"));
                                        break;
                                    }  
                                    case "SAL0": {
                                        r.setDataDecretoSAL0(rs.getDate("data_ora_provvedimento"));
                                        r.setNumeroDecretoSAL0(rs.getString("numero_provvedimento"));
                                        break;
                                    }
                                    case "FINALE": {
                                        r.setDataDecretoSALfinale(rs.getDate("data_ora_provvedimento"));
                                        r.setNumeroDecretoSALfinale(rs.getString("numero_provvedimento"));
                                        break;
                                    }
                                    case "LIEVI50": {
                                        r.setDataDecretoSAL50(rs.getDate("data_ora_provvedimento"));
                                        r.setNumeroDecretoSAL50(rs.getString("numero_provvedimento"));
                                        break;
                                    }
                                }                                
                            }
                        }
                         
                        psDataPresentazione.clearParameters();
                        for(int i=0;i<6;i++) psDataPresentazione.setInt(i+1, idPratica);
                        try(ResultSet rs = psDataPresentazione.executeQuery()) {
                            while(rs.next()) {
                                switch(rs.getString("tipo")) {
                                    case "FINALE": {
                                        r.setDataPresentazioneSALfinale(rs.getDate("IstanzaMudeData"));
                                        break;
                                    }  
                                    case "SAL70": {
                                        r.setDataPresentazioneSAL70(rs.getDate("IstanzaMudeData"));
                                        break;
                                    }
                                    case "SAL40": {
                                        r.setDataPresentazioneSAL40(rs.getDate("IstanzaMudeData"));
                                        break;
                                    }
                                    case "SAL20": {
                                        r.setDataPresentazioneSAL20(rs.getDate("IstanzaMudeData"));
                                        break;
                                    }
                                    case "SAL50": {
                                        r.setDataPresentazioneSAL50(rs.getDate("IstanzaMudeData"));
                                        break;
                                    }
                                    case "SAL0": {
                                        r.setDataPresentazioneSAL0(rs.getDate("IstanzaMudeData"));
                                        break;
                                    }
                                }                                
                            }
                        }

                        // Carica date provvvedimenti SAL 20,40 e 70
                        psDataProvvedimenti.clearParameters();
                        psDataProvvedimenti.setInt(1, idPratica);
                        try(ResultSet rs = psDataProvvedimenti.executeQuery()) {
                            int num = 1;
                            while(rs.next()) {
                                switch(num) {
                                    case 3: {
                                        r.setDataDecretoSAL70(rs.getDate("data_ora_provvedimento"));
                                        r.setNumeroDecretoSAL70(rs.getString("numero_provvedimento"));
                                        num++;
                                        break;
                                    }
                                    case 2: {
                                        r.setDataDecretoSAL40(rs.getDate("data_ora_provvedimento"));
                                        r.setNumeroDecretoSAL40(rs.getString("numero_provvedimento"));
                                        num++;
                                        break;
                                    }
                                    case 1: {
                                        r.setDataDecretoSAL20(rs.getDate("data_ora_provvedimento"));
                                        r.setNumeroDecretoSAL20(rs.getString("numero_provvedimento"));
                                        num++;
                                        break;
                                    }
                                }
                            }
                        }
                        
                        psDataProvvedimento50.clearParameters();
                        psDataProvvedimento50.setInt(1, idPratica);
                        try(ResultSet rs = psDataProvvedimento50.executeQuery()) {
                            if(rs.next()) {
                                r.setDataDecretoSAL50(rs.getDate("data_ora_provvedimento"));
                                r.setNumeroDecretoSAL0(rs.getString("numero_provvedimento"));
                            }
                            
                        }
                    }
                    else {                        
                        log("WARNING! IdPratica: "+idPratica+" not found in decreti!");
                        if(idPratica>=2144) {
                            r.setOrdinanza100(Boolean.TRUE);
                            log("IdPratica "+idPratica+": Ordinanza 100->true!");
                        }
                    } 
                    
                    Contributo c = contributi.get(idPratica);
                    if(c!=null) {
                        if(c.getContributoConcesso()!=null) {
                            double d = c.getContributoConcesso().doubleValue();
                            if(d>=EPSILON) {
                                r.setContributoConcesso(c.getContributoConcesso());
                            }
                        }
                        if(c.getContributoLiquidato()!=null) {
                            double d = c.getContributoLiquidato().doubleValue();
                            if(d>=EPSILON){
                                r.setContributoLiquidato(c.getContributoLiquidato());
                            }
                        }
                    }
                    else {
                        log("WARNING! IdPratica: "+idPratica+" not found in contributi!");
                    }
                    
                    batchInsert(psReportCommissario, r);
                    rowCount++;
                    if((rowCount % BATCH_SIZE)==0) {
                        psReportCommissario.executeBatch();                        
                        
                        psReportCommissarioWork.clearParameters();
                        psReportCommissarioWork.setInt(1, rowCount);
                        psReportCommissarioWork.setInt(2, 0);
                        psReportCommissarioWork.setNull(3, Types.VARCHAR);
                        psReportCommissarioWork.executeUpdate();
                        
                        decretiCon.commit();
                        
                        psReportCommissario.clearBatch();
                    }
                }
                
                
                // salva l'ultimo batch
                psReportCommissario.executeBatch();

                psReportCommissarioWork.clearParameters();
                psReportCommissarioWork.setInt(1, lRep.size());
                psReportCommissarioWork.setInt(2, 1);
                psReportCommissarioWork.setNull(3, Types.VARCHAR);
                psReportCommissarioWork.executeUpdate();

                decretiCon.commit();                
            }
            
            log("Renegeration completed!");
        }
        catch(Exception e) {
            e.printStackTrace();
            
            String reportoCommissarioWorkSql = "UPDATE reportcommissariowork SET step = ?, completed = ?, error = ? WHERE (id = 1)";
            try(Connection decretiCon = dsDecreti.getConnection();
                PreparedStatement psReportCommissarioWork = decretiCon.prepareStatement(reportoCommissarioWorkSql)) {
                psReportCommissarioWork.setInt(1, 0);
                psReportCommissarioWork.setInt(2, 1);
                psReportCommissarioWork.setString(3, e.toString());
                psReportCommissarioWork.executeUpdate();
            }
            catch(Exception ex) {
                log(ex);
            }            
        }
    }
    
    private boolean isContributo(int idTipoDecreto) {
        return IntStream.of(CONTRIBUTO).anyMatch(v -> (v==idTipoDecreto));
    }
    
    private boolean isRigetto(int idTipoDecreto) {
        return IntStream.of(RIGETTO).anyMatch(v -> (v==idTipoDecreto));
    }
    
    private boolean isOrdinanzaRiferimento(int idOrdinanzaRiferimento) {        
        return IntStream.of(ORDINANZA_RIFERIMENTO).anyMatch(v -> (v==idOrdinanzaRiferimento));
    }

    private void batchInsert(PreparedStatement psReportCommissario, Report r) throws SQLException {
        int i = 1;
        psReportCommissario.setString(i++, r.getNumeroFascicoloMUDE());
        psReportCommissario.setInt(i++, r.getOrdinanza());
        psReportCommissario.setString(i++, boolToStr(r.getOrdinanza100()));
        psReportCommissario.setString(i++, boolToStr(r.getSorteggiataPerVerificaACampione()));
        psReportCommissario.setString(i++, r.getNumeroProtocolloUSR());
        psReportCommissario.setDate(i++, dateToSql(r.getDataProtocolloUSR()));
        psReportCommissario.setString(i++, r.getNumeroFascicoloUSR());
        psReportCommissario.setString(i++, r.getCFIntestatario());
        psReportCommissario.setString(i++, r.getNomeCognomeIntestatario());
        psReportCommissario.setString(i++, r.getTitolaritaGiuridicaRichiedente());
        
        psReportCommissario.setString(i++, r.getCFProfessionistaCapogruppo());
        psReportCommissario.setString(i++, r.getNomeCognomeProfessionistaCapogruppo());
        psReportCommissario.setString(i++, r.getCFoPivaImpresaAffidataria());
        psReportCommissario.setString(i++, r.getRagioneSocialeImpresaAffidataria());
        psReportCommissario.setString(i++, r.getCodiceIstatProvincia());
        psReportCommissario.setString(i++, r.getCodiceIstatComune());
        psReportCommissario.setString(i++, r.getIndirizzo());
        if(r.getFoglio()!=null) {
            psReportCommissario.setInt(i++, r.getFoglio());
        }
        else {
            psReportCommissario.setNull(i++, Types.INTEGER);
        }
        psReportCommissario.setString(i++, r.getMappaleTerreni());
        psReportCommissario.setString(i++, r.getDestinazioneUsoPrevalente());        
        psReportCommissario.setString(i++, r.getLivelloOperativo());
        psReportCommissario.setString(i++, r.getTipologiaIntervento());
        psReportCommissario.setString(i++, boolToStr(r.getInterventoAggregato()));        
        if(r.getTotUStrutturali()!=null) {
            psReportCommissario.setInt(i++, r.getTotUStrutturali());
        }
        else {
            psReportCommissario.setNull(i++, Types.INTEGER);
        }
        if(r.getTotUI()!=null) {
            psReportCommissario.setInt(i++, r.getTotUI());
        }
        else {
            psReportCommissario.setNull(i++, Types.INTEGER);
        }
        if(r.getTotUIPrincipaliOAttProdEse()!=null) {
            psReportCommissario.setInt(i++, r.getTotUIPrincipaliOAttProdEse());                
        }
        else {
            psReportCommissario.setNull(i++, Types.INTEGER);
        }
        psReportCommissario.setString(i++, boolToStr(r.getIstanzaRigettataArchiviata()));
        psReportCommissario.setDate(i++, dateToSql(r.getDataRigettoArchiviazione()));        
        psReportCommissario.setString(i++, r.getNumeroDecretoContributo());
        psReportCommissario.setDate(i++, dateToSql(r.getDataDecretoContributo()));       
        psReportCommissario.setString(i++, r.getCUP());
        
        psReportCommissario.setDate(i++, dateToSql(r.getDataPresentazioneAnticipazioneSpeseTecniche()));
        psReportCommissario.setString(i++, r.getNumeroDecretoAnticipazioneSpeseTecniche());
        psReportCommissario.setDate(i++, dateToSql(r.getDataDecretoAnticipazioneSpeseTecniche()));        
        if(r.getImportoAnticipazioneSpeseTecniche()!=null) {
            psReportCommissario.setBigDecimal(i++, r.getImportoAnticipazioneSpeseTecniche());
        }
        else {
            psReportCommissario.setNull(i++, Types.DECIMAL);
        }
        
        psReportCommissario.setDate(i++, dateToSql(r.getDataPresentazioneSAL0()));
        psReportCommissario.setString(i++, r.getNumeroDecretoSAL0());
        psReportCommissario.setDate(i++, dateToSql(r.getDataDecretoSAL0()));
        
        psReportCommissario.setDate(i++, dateToSql(r.getDataPresentazioneSAL20()));
        psReportCommissario.setString(i++, r.getNumeroDecretoSAL20());
        psReportCommissario.setDate(i++, dateToSql(r.getDataDecretoSAL20()));
        
        psReportCommissario.setDate(i++, dateToSql(r.getDataPresentazioneSAL40()));
        psReportCommissario.setString(i++, r.getNumeroDecretoSAL40());
        psReportCommissario.setDate(i++, dateToSql(r.getDataDecretoSAL40()));
        
        psReportCommissario.setDate(i++, dateToSql(r.getDataPresentazioneSAL50()));
        psReportCommissario.setString(i++, r.getNumeroDecretoSAL50());
        psReportCommissario.setDate(i++, dateToSql(r.getDataDecretoSAL50()));
        
        psReportCommissario.setDate(i++, dateToSql(r.getDataPresentazioneSAL70()));
        psReportCommissario.setString(i++, r.getNumeroDecretoSAL70());
        psReportCommissario.setDate(i++, dateToSql(r.getDataDecretoSAL70()));
        
        psReportCommissario.setDate(i++, dateToSql(r.getDataPresentazioneSALfinale()));
        psReportCommissario.setString(i++, r.getNumeroDecretoSALfinale());
        psReportCommissario.setDate(i++, dateToSql(r.getDataDecretoSALfinale()));
        
        if(r.getContributoConcesso()!=null) {
            psReportCommissario.setBigDecimal(i++, r.getContributoConcesso());
        }
        else {
            psReportCommissario.setNull(i++, Types.DECIMAL);
        }
        if(r.getContributoLiquidato()!=null) {
            psReportCommissario.setBigDecimal(i++, r.getContributoLiquidato());
        }
        else {
            psReportCommissario.setNull(i++, Types.DECIMAL);
        }
        
        psReportCommissario.addBatch();
    }
    
    private String boolToStr(Boolean b) {
        if(b!=null && b) return "1";
        return "0";
    }

    private java.sql.Date dateToSql(java.util.Date d) {
        return (d!=null ? new java.sql.Date(d.getTime()) : null);
    }    
    
    private void log(Object msg) {
        //SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        //System.out.println("["+sdf.format(new Date())+"]: "+msg);
        //System.out.flush();
        System.out.println(msg);
    }
}
