package com.dxc.curvegas.controller;

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

    @GetMapping(value = "curvagas/getCurveList")
    public ResponseEntity<List<Document>> findCurveGas(
            @RequestParam(name = "codPdf", required = false) String codPdf,
            @RequestParam(name = "codPdm", required = false) String codPdm,
            @RequestParam(name = "codTipoFornitura", required = false) String codTipoFornitura,
            @RequestParam(name = "codTipVoceLtu", required = false) String codTipVoceLtu,
            @RequestParam(name = "mese", required = false) String mese,
            @RequestParam(name = "anno", required = false) String anno
    ){
        return ResponseEntity.ok(service.findCurveGasList(codPdf, codPdm, codTipoFornitura, codTipVoceLtu,mese, anno));
    }

}
