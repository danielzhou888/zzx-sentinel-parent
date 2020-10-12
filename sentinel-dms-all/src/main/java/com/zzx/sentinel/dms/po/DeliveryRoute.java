package com.zzx.sentinel.dms.po;

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
public class DeliveryRoute implements Serializable {

    private static final long serialVersionUID = -3001850007216371876L;
    private Long routeId;
    /** defaultRoutes, baseRoutes */
    private String routes;
    /** defaultType, baseType */
    private String type;
    private String orderCode;
    private Long userId;

    public boolean isDmsDownGrade() {
        return "defaultDmsDeliveryRoutes".endsWith(routes) ? true : false;
    }
}
