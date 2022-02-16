package com.dxc.curvegas.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class LtuGiornaliereStatsDto {
    Integer numLtuGte25000;
    Integer numLtuGte1000Lt25000;
    Integer numLtuLt1000;
}
