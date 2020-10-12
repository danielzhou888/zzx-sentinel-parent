package com.zzx.sentinel.dfc.po;

import com.sun.xml.internal.ws.developer.Serialization;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zhouzhixiang
 * @Date 2020-09-30
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BaseRoute implements Serializable {

    private static final long serialVersionUID = -729955951729153276L;
    private Long routeId;
    /** defaultRoutes, baseRoutes */
    private String routes;
    /** defaultType, baseType */
    private String type;
    private String orderCode;
    private Long userId;

    public boolean isDfcDownGrade() {
        return "defaultDfcRoutes".equals(routes) ? true : false;
    }
}
