package com.dxc.curvegas.service;

import com.mongodb.BasicDBObject;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
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


        aggrList.add(project().andExclude("lettureSingole.storico","_id"));

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
                        return new Document("$addFields",doc);
                    }
                };
        aggrList.add(customAddFieldsOperation);

        aggrList.add(project().andExclude("lettureSingole"));

        aggrList.add(sort(ASC, "datLettura"));

        aggrList.add(project("mese", "anno", "codTipoFornitura", "codPdf", "codPdm", "codTipVoceLtu").and("$quaLettura").as("lettureSingole.quaLettura").and("$datLettura").as("lettureSingole.datLettura"));
//
//        aggrList.add(
//                Aggregation
//                        .group("codPdf")
//                        .addToSet("lettureSingole").as("blah")
//        );

        AggregationResults<Document> aggrRetDocument = mongoTemplate.aggregate(newAggregation(aggrList),"ltuGiornaliereAggregated",Document.class);

        ArrayList<Document> lettureSingole = new ArrayList<>();

        aggrRetDocument.getMappedResults().forEach(
                doc -> lettureSingole.add((Document) doc.get("lettureSingole"))
        );
        Document retDocument = new Document()
                .append("codPdf", codPdf);
        retDocument.append("lettureSingole", lettureSingole);
        return (Document) retDocument;

    }
}
