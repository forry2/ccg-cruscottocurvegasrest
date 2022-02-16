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
                new AggregationOperation(){
                    @Override
                    public Document toDocument(AggregationOperationContext aoc) {
                        Document doc = new Document();
                        doc.put("quaLettura", "$lettureSingole.quaLettura");
                        doc.put("datLettura", "$lettureSingole.datLettura");
                        doc.put("consumoGiornaliero", "$lettureSingole.consumoGiornaliero");
                        doc.put("consumoGiornalieroStimato", "$lettureSingole.consumoGiornalieroStimato");
                        doc.put("codTipoFonteLtuGio", "$lettureSingole.codTipoFonteLtuGio");
                        doc.put("codTipLtuGio", "$lettureSingole.codTipLtuGio");
                        return new Document("$addFields",doc);
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

        AggregationResults<Document> aggrRetDocument = mongoTemplate.aggregate(newAggregation(aggrList),"ltuGiornaliereAggregated",Document.class);

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
}
