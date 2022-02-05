package com.dxc.curvegas.controller;

import com.dxc.curvegas.model.LtuGiornaliereAggregatedDto;
import com.dxc.curvegas.service.CurveGasService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class CurveGasController {

    @Autowired
    private CurveGasService service;

    @GetMapping(value = "curvagas/codPdf/{codPdf}")
    public ResponseEntity<Document> findCurveGas(
            @PathVariable("codPdf") String codPdf,
            @RequestParam(name = "anno", required = true) String anno,
            @RequestParam(name = "mese", required = true) String mese,
            @RequestParam(name = "codTipVoceLtu", required = true) String codTipVoceLtu
    ){
        return ResponseEntity.ok(service.findCurveGas(codPdf, anno, mese, codTipVoceLtu));
    }


}
