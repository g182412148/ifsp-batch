package util;

import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAccMchtsDtl;
import lombok.Data;

import java.util.List;

/**
 * @author: ljy
 * @create: 2018-08-25 14:47
 */
@Data
public class BthInAcc {
    private  List<BthMerInAccMchtsDtl> dtlList;
}
