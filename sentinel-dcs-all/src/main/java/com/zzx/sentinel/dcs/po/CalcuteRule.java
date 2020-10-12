package com.zzx.sentinel.dcs.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author zhouzhixiang
 * @Date 2020-09-30
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalcuteRule implements Serializable {

    private static final long serialVersionUID = -94517540832088566L;
    private String rules;
    private Long id;
    private BigDecimal price;
    private BigDecimal promotionPrice;
    private String orderCode;

    public boolean isDcsDownGrade() {
        return "defaultDcsRule".equals(rules) ? true : false;
    }

}
