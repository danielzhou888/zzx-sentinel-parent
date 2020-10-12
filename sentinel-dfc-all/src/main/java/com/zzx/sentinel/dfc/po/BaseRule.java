package com.zzx.sentinel.dfc.po;

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
public class BaseRule implements Serializable {

    private static final long serialVersionUID = -6072863206403048752L;
    private Long id;
    private BigDecimal price;
    private String rules;

    public boolean isDfcDownGrade() {
        return "defaultDfcRules".equals(rules) ? true : false;
    }
}
