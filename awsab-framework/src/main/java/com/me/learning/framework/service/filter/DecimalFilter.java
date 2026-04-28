/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 14/05/2025
 * Usage    :
 * Since    : Version 1.0
 */
package com.me.learning.framework.service.filter;

import java.io.Serial;
import java.math.BigDecimal;

public class DecimalFilter extends RangeFilter<BigDecimal> {

    @Serial
    private static final long serialVersionUID = -6069489515057589533L;

    public DecimalFilter() {
    }

    public DecimalFilter(DecimalFilter filter) {
        super(filter);
    }

    @Override
    public DecimalFilter copy() {
        return new DecimalFilter(this);
    }
}
