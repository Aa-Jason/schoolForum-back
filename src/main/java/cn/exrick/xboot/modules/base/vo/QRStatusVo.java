package cn.exrick.xboot.modules.base.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author exrick
 */
@Data
@Accessors(chain = true)
public class QRStatusVo {

    private String status;

    private String accessToken;
}
