package com.dxc.curvegas.service;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class CurveGasService {
    @Autowired
    private MongoTemplate mongoTemplate;

    public Document findCurveGas(String codPdf, String anno, String mese, String codTipVoceLtu) {
        ArrayList<AggregationOperation> aggrList = new ArrayList<>();

        Criteria matchCriteria = new Criteria("codPdf").is(codPdf);
        if (StringUtils.isNotBlank(anno)) matchCriteria = matchCriteria.and("anno").is(anno);
        if (StringUtils.isNotBlank(mese)) matchCriteria = matchCriteria.and("mese").is(mese);
        if (StringUtils.isNotBlank(codTipVoceLtu)) matchCriteria = matchCriteria.and("codTipVoceLtu").is(codTipVoceLtu);
        aggrList.add(match(matchCriteria));


        aggrList.add(project().andExclude("lettureSingole.storico", "_id"));

        aggrList.add(unwind("$lettureSingole"));

//        aggrList.add(addFields()
//                .addFieldWithValue("quaLettura", "$lettureSingole.quaLettura")
//                .addFieldWithValue("datLettura", "$lettureSingole.datLettura")
//                .build()
//        );

        AggregationOperation customAddFieldsOperation =
                new AggregationOperation() {
                    @Override
                    public Document toDocument(AggregationOperationContext aoc) {
                        Document doc = new Document();
                        doc.put("quaLettura", "$lettureSingole.quaLettura");
                        doc.put("datLettura", "$lettureSingole.datLettura");
                        doc.put("consumoGiornaliero", "$lettureSingole.consumoGiornaliero");
                        doc.put("consumoGiornalieroStimato", "$lettureSingole.consumoGiornalieroStimato");
                        doc.put("codTipoFonteLtuGio", "$lettureSingole.codTipoFonteLtuGio");
                        doc.put("codTipLtuGio", "$lettureSingole.codTipLtuGio");
                        return new Document("$addFields", doc);
                    }
                };
        aggrList.add(customAddFieldsOperation);

        aggrList.add(project().andExclude("lettureSingole"));

        aggrList.add(sort(ASC, "datLettura"));

        aggrList.add(project(
                        "codPdf",
                        "consumoReale",
                        "minQuaLettura",
                        "maxQuaLettura",
                        "dtaPrimaLetturaValida",
                        "primaLetturaValida",
                        "dtaUltimaLetturaValida",
                        "ultimaLetturaValida"
                )
                        .and("$quaLettura").as("lettureSingole.quaLettura")
                        .and("$datLettura").as("lettureSingole.datLettura")
                        .and("$consumoGiornaliero").as("lettureSingole.consumoGiornaliero")
                        .and("$consumoGiornalieroStimato").as("lettureSingole.consumoGiornalieroStimato")
                        .and("$codTipoFonteLtuGio").as("lettureSingole.codTipoFonteLtuGio")
                        .and("$codTipLtuGio").as("lettureSingole.codTipLtuGio")
        );

        AggregationResults<Document> aggrRetDocument = mongoTemplate.aggregate(newAggregation(aggrList), "ltuGiornaliereAggregated", Document.class);

        ArrayList<Document> lettureSingole = new ArrayList<>();


        Document retDocument = new Document();
        if (aggrRetDocument.getMappedResults().isEmpty()) {
            retDocument.append("codPdf", codPdf);
            return retDocument;
        }
        retDocument
                .append("codPdf", aggrRetDocument.getMappedResults().get(0).get("codPdf"))
                .append("consumoReale", aggrRetDocument.getMappedResults().get(0).get("consumoReale"))
                .append("minQuaLettura", aggrRetDocument.getMappedResults().get(0).get("minQuaLettura"))
                .append("maxQuaLettura", aggrRetDocument.getMappedResults().get(0).get("maxQuaLettura"))
                .append("dtaPrimaLetturaValida", aggrRetDocument.getMappedResults().get(0).get("dtaPrimaLetturaValida"))
                .append("primaLetturaValida", aggrRetDocument.getMappedResults().get(0).get("primaLetturaValida"))
                .append("dtaUltimaLetturaValida", aggrRetDocument.getMappedResults().get(0).get("dtaUltimaLetturaValida"))
                .append("ultimaLetturaValida", aggrRetDocument.getMappedResults().get(0).get("ultimaLetturaValida"));
        aggrRetDocument.getMappedResults().forEach(
                doc -> lettureSingole.add((Document) doc.get("lettureSingole"))
        );
        retDocument.append("lettureSingole", lettureSingole);
        return retDocument;
    }

    public List<Document> findCurveGasList(
            String codPdf,
            String codPdm,
            String codTipoFornitura,
            String codTipVoceLtu,
            String mese,
            String anno
    ) {
        Document retDoc = new Document();
        ArrayList<AggregationOperation> aggrList = new ArrayList<>();
        Criteria matchCriteria = new Criteria();
        if (StringUtils.isNotBlank(codPdf))
            matchCriteria.and("codPdf").is(codPdf);
        if (StringUtils.isNotBlank(codPdm))
            matchCriteria.and("codPdm").is(codPdm);
        if (StringUtils.isNotBlank(codTipoFornitura))
            matchCriteria.and("codTipoFornitura").is(codTipoFornitura);
        if (StringUtils.isNotBlank(codTipVoceLtu))
            matchCriteria.and("codTipVoceLtu").is(codTipVoceLtu);
        if (StringUtils.isNotBlank(mese))
            matchCriteria.and("mese").is(mese);
        if (StringUtils.isNotBlank(anno))
            matchCriteria.and("anno").is(anno);
        aggrList.add(match(matchCriteria));

        aggrList.add(unwind("$lettureSingole"));

        aggrList.add(project(
                "codPdf",
                "codPdm",
                "anno",
                "mese",
                "codTipVoceLtu",
                "consumoReale",
                "minQuaLettura",
                "maxQuaLettura",
                "dtaPrimaLetturaValida",
                "primaLetturaValida",
                "dtaUltimaLetturaValida",
                "ultimaLetturaValida")
                .and("$lettureSingole.quaLettura").as("lettureSingole.quaLettura")
                .and("$lettureSingole.datLettura").as("lettureSingole.datLettura")
                .and("$lettureSingole.consumoGiornaliero").as("lettureSingole.consumoGiornaliero")
                .and("$lettureSingole.consumoGiornalieroStimato").as("lettureSingole.consumoGiornalieroStimato")
                .and("$lettureSingole.codTipoFonteLtuGio").as("lettureSingole.codTipoFonteLtuGio")
                .and("$lettureSingole.codTipLtuGio").as("lettureSingole.codTipLtuGio")
                .andExclude("_id"));

        AggregationOperation groupOperation = new AggregationOperation() {
            @Override
            public Document toDocument(AggregationOperationContext aoc) {
                Document pushDoc = new Document("$push", "$lettureSingole");

                Document idDoc = new Document("codPdf", "$codPdf")
                        .append("codPdm", "$codPdm")
                        .append("anno", "$anno")
                        .append("mese", "$mese")
                        .append("consumoReale", "$consumoReale")
                        .append("minQuaLettura", "$minQuaLettura")
                        .append("maxQuaLettura", "$maxQuaLettura")
                        .append("dtaPrimaLetturaValida", "$dtaPrimaLetturaValida")
                        .append("primaLetturaValida", "$primaLetturaValida")
                        .append("dtaUltimaLetturaValida", "$dtaUltimaLetturaValida")
                        .append("ultimaLetturaValida", "$ultimaLetturaValida");

                return new Document("$group", new Document("_id", idDoc).append("lettureSingole", pushDoc));
            }
        };
        aggrList.add(groupOperation);

        aggrList.add(
            project()
                .and("$_id.codPdf").as("codPdf")
                .and("$_id.codPdm").as( "codPdm")
                .and("$_id.anno").as( "anno")
                .and("$_id.mese").as( "mese")
                .and("$_id.consumoReale").as( "consumoReale")
                .and("$_id.minQuaLettura").as( "minQuaLettura")
                .and("$_id.maxQuaLettura").as( "maxQuaLettura")
                .and("$_id.dtaPrimaLetturaValida").as( "dtaPrimaLetturaValida")
                .and("$_id.primaLetturaValida").as( "primaLetturaValida")
                .and("$_id.dtaUltimaLetturaValida").as( "dtaUltimaLetturaValida")
                .and("$_id.ultimaLetturaValida").as( "ultimaLetturaValida")
                .and("$lettureSingole").as("lettureSingole")
                .andExclude("_id")
        );

        return mongoTemplate.aggregate(newAggregation(aggrList), "ltuGiornaliereAggregated", Document.class)
                .getMappedResults();
    }
}
